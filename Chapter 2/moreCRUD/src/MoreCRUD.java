/**
   Usage with executable JAR:

   java -jar MoreCRUD.jar
 */

/** Overview:

    In previous examples, We've seen three of the four CRUD operations in SQL/JDBC:

    -- Create: sqlite3 recClub.db (CREATE DATABASE), CREATE TABLE, INSERT INTO

    -- Read: SELECT (in many different forms, with more to come)

    -- Delete: DROP TABLE (used with the IF EXITS constraint)
               DROP also can be used to delete an entire database: DROP DATABASE 
               But there's also DELETE a row from a table, which we've not seen.

    In addition to DELETE, we still need the 'U' in CRUD for 'update' ('edit'):

    -- Update: UPDATE 

    In summary, DELETE and UPDATE on highlighted in this app.
 */

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;    
import java.sql.SQLException; 
import java.math.BigDecimal;

public class MoreCRUD {
    public static void main(String[ ] args) {
	new MoreCRUD().demo();
    }

    private void demo() {
	try {
	    Connection conn = openConnection();
	    runExamples(conn);
	    conn.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

   private void runExamples(Connection conn) {
       updateCost(conn);
       System.out.println("\n-------------------------------------");
       deleteRow(conn);
    }
    
    private void updateCost(Connection conn) {
	/** General form of UPDATE:
	    UPDATE <table> SET col1 = val1, col2 = val2,... [WHERE <condition>]
	 */
	String template = "UPDATE activities SET cost = (cost * ?) WHERE cost < ?";
	BigDecimal minCost = new BigDecimal(12.99);     // we're giving it away...
	BigDecimal increasePerc = new BigDecimal(1.12); // 112% 

	try {
	    dumpActivities(conn, "Before update with 12.99 as threshold cost:", minCost);
	    PreparedStatement pstmt = conn.prepareStatement(template);
	    pstmt.setBigDecimal(1, increasePerc);
	    pstmt.setBigDecimal(2, minCost);
	    pstmt.executeUpdate();
	    dumpActivities(conn, "After update with 12.99 as threshold cost:", minCost);
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

    private void deleteRow(Connection conn) {
	// If the 'where' condition were omitted, then all of the rows in the activities 
	// table would be deleted.
	String sql = "DELETE FROM activities WHERE act_id = 1"; // delete the record for 'squash'

	try {
	    dumpActivities(conn, "Before delete:");
	    Statement stmt = conn.createStatement();
	    stmt.execute(sql);
	    dumpActivities(conn, "After delete:");
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

    private void dumpActivities(Connection conn, String msg, BigDecimal minCost) {
	System.out.println("\n" + msg);
	String template = "SELECT * FROM activities WHERE cost < ? ORDER BY cost DESC"; // desc or asc (default)
	try {
	    PreparedStatement pstmt = conn.prepareStatement(template);
	    pstmt.setBigDecimal(1, minCost);
	    ResultSet rs = pstmt.executeQuery();
	    while (rs.next()) {
		String output = String.format("%s %.2f", rs.getString("name"), rs.getBigDecimal("cost"));
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

   private void dumpActivities(Connection conn, String msg) {
	System.out.println("\n" + msg);
	String sql = "SELECT * FROM activities ORDER BY act_id";
	try {
	    Statement stmt = conn.createStatement();
	    ResultSet rs = stmt.executeQuery(sql);
	    while (rs.next()) {
		String output = String.format("%s %.2f", rs.getString("name"), rs.getBigDecimal("cost"));
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }
    
    private void reportAndExit(Exception e) {
	System.err.println(e);
	System.exit(-1);
    }

    private Connection openConnection() {
	Connection conn = null;                                           //## Connection between program and DB system
	try {
	    Class.forName("org.sqlite.JDBC");                             //## Name of the driver to load
	    conn = DriverManager.getConnection("jdbc:sqlite:recClub.db"); //## URI for test.db: creates DB if it doesn't exist
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