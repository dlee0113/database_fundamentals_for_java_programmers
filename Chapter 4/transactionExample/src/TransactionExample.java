/**
   Suppose that we've two tables to track users on our system:

      Users(id, login, password)
      PwdChanges(id, user_id, timestamp)
      
   If a user changes a login or a password, this change occurs within a
   session; hence, both tables would need to be updated together, which
   should occur within a _single_ transaction. This JDBC example illustrates.

   Using the SQLite client, I've already created the two tables:

   CREATE TABLE users (uid INTEGER PRIMARY KEY AUTOINCREMENT, 
                       login VARCHAR(24) UNIQUE, 
                       password VARCHAR(24));

   CREATE TABLE pwdchanges (sess_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                            user_id INTEGER, 
                            timestamp DATE, 
                            FOREIGN KEY(user_id) REFERENCES users(uid));   
 */

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;  
import java.sql.SQLException;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.Arrays;

public class TransactionExample {
    public static void main(String[ ] args) {
	new TransactionExample().demo();
    }

    private void demo() {
	try {
	    Connection conn = openConnection();
	    addSomeUsers(conn);
	    changePassword(conn);
	    conn.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

    private void addSomeUsers(Connection conn) {
	// Check if we've already added these users.
	// This isn't necessary, as the table has been created to accept only unique names. But
	// it's a chance to show a quick and easy way to get the count of records in a table.
	try {
	    String sql = "SELECT COUNT(*) FROM users";
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);
	    rs.next();  // move cursor to 1st record in ResultSet
	    int n = rs.getInt(1);
	    rs.close();
	    stmt.close();
	    if (n > 0) return;
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}

	String[ ] names = {"curly", "larry", "moe"};
	String sql = "INSERT INTO users(login, password) VALUES(?, ?)";

	try {
	    MessageDigest sha1 = MessageDigest.getInstance("SHA-1"); // Secure Hash Algorithm
	    PreparedStatement psmt = conn.prepareStatement(sql);
	    for (String name : names) {
		psmt.setString(1, name);
		String pass = name + "123";
		sha1.update(pass.getBytes());
		String digest = Arrays.toString(sha1.digest());
		psmt.setString(2, digest);
		psmt.executeUpdate();
	    }
	    psmt.close();
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
	/**
	   1 | curly | [-9, 4, -112, -25, 102, -39, -65, 35, -70, 4, 0, -80, -19, -46, 90, -57, 124, 42, 103, -35]
	   2 | larry | [-74, 113, -39, -104, 120, -16, 99, 47, 17, 32, 81, -36, 83, -91, 118, -15, 39, -122, 105, 46]
	   3 | moe   | [12, 58, 2, 75, 116, -85, 49, 18, -71, 8, -41, 29, 83, 71, -23, 92, 60, -100, -105, -60]
	 */
    }

    private void reportAndExit(Exception e) {
	System.err.println(e);
	System.exit(-1);
    }

    private void changePassword(Connection conn) {
	String sql1 = "INSERT INTO pwdchanges (user_id, timestamp) VALUES(?, CURRENT_TIMESTAMP)";
	String sql2 = "UPDATE users SET password = ? WHERE uid = ?";

	int id = getUserId(conn);
	if (id < 0) {  // signals failure
	    System.err.println("Error in username or password. Try again later.");
	    return;    // only 1 chance in current implementation
	}

	String newPass = getNewPass();
	if (newPass.length() < 1) { // signals failure
	    System.err.println("Problem with new password. Try again later.");
	    return;
	}

	try {
	    // Prepare pwdchanges statement.
	    PreparedStatement pstmt1 = conn.prepareStatement(sql1);
	    pstmt1.setInt(1, id);

	    // Prepare users statment
	    MessageDigest sha1 = MessageDigest.getInstance("SHA-1"); // 20-byte digest
	    sha1.update(newPass.getBytes());
	    String digest = Arrays.toString(sha1.digest());
	    PreparedStatement pstmt2 = conn.prepareStatement(sql2);
	    pstmt2.setString(1, digest);
	    pstmt2.setInt(2, id);

	    // Set up the explicit transaction.
	    conn.setAutoCommit(false);   // we'll commit explicitly: we want 2 statements in transaction
	    pstmt1.executeUpdate();      // 1st statement in this transaction
	    pstmt2.executeUpdate();      // 2nd statement in this transaction
	    conn.commit();               // explicit commit

	    pstmt1.close();
	    pstmt2.close();
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
    }

    private int getUserId(Connection conn) {
	int userId = -1;    // signals failure
	Scanner stdin = new Scanner(System.in);

	// Prompt for and read login name and password.
	System.out.print("Login name: ");
	String login = stdin.next();

	System.out.print("Password: " );
	String passwd = stdin.next();

	// Do they seem obviously bad?
	if (login.length() < 1 || passwd.length() < 1) return userId;

	// A parameterized query, turned into a PreparedStatement, protects against
	// so-called SQL-injection attacks.
	String sql = "SELECT * from users WHERE login = ? and password = ?";

	// Now check against the database.
	try {
	    // Get the digest of the entered password.
	    MessageDigest sha1 = MessageDigest.getInstance("SHA-1"); // 20-byte digest
	    sha1.update(passwd.getBytes());
	    String digest = Arrays.toString(sha1.digest());

	    // Finish up the query.
	    PreparedStatement pstmt = conn.prepareStatement(sql);
	    pstmt.setString(1, login);
	    pstmt.setString(2, digest);

	    // Execute query, extract results
	    ResultSet rs = pstmt.executeQuery();
	    if (rs.next()) {
		String slogin = rs.getString("login");
		String spassw = rs.getString("password");
	    
		// Are the login and password both ok?
		if (slogin.equals(login) && spassw.equals(digest)) {
		    userId = rs.getInt("uid");
		}
	    }
	    rs.close();
	    pstmt.close();
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
	return userId;
    }

    private String getNewPass() {
	String newPass = "";  // empty string signals failure
	Scanner stdin = new Scanner(System.in);
	System.out.print("New password: ");
	newPass = stdin.next();
	return newPass;
    }

    private Connection openConnection() {
	Connection conn = null;                                  
	try {
	    Class.forName("org.sqlite.JDBC");                    
	    conn = DriverManager.getConnection("jdbc:sqlite:users.db");
	} 
	catch(ClassNotFoundException cnfe) {
	    reportAndExit(cnfe);
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	return conn;
    }
}
