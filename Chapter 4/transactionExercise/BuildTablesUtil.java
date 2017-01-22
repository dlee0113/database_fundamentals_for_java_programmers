/**
   Usage (with the executable JAR): java -jar introU.BuildTablesUtil.jar

   The program creates the activities database with the following tables:

   Customers(Name,                      ## for now, a single name for simplicity
             Id)                        ## Id is unique per customer ('primary key')

   Activities(Name,                     ## e.g., 'squash', 'bowling', '3-on-3 basketball'
	      Cost,                     ## cost to customer
              Id)                       ## unique per Activity ('primary key')

   Sessions(FromD,                      ## timestamp
            ToD,                        ## timestamp
            Activity_Id,                ## link to Activities ('foreign key')
            Customer_Id,                ## link to Customer ('foreign key')
            Id)                         ## unique per Session ('primary key')
 */

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

public class BuildTablesUtil {
    private static final Random rand = new Random();

    private static final String[ ] fakeNames = {
	"Bob", "Carol", "Ted", "Alice",
	"Moe", "Curly", "Larry",
	"Fred", "Wilma", "Pebbles", "Barney", "Betty",
	"Lou", "Bud", "Gracie", "Lucy",
	"Homer", "Marge", "Selma", "Patty", "Lisa",
	"Piggly", "Wiggly"
    };

    private static final String[ ] fakeActivities = {
	"squash", "3-on-3 basketball", "tiddly winks", "tennis",
	"basic spinning", "intermediate spinning", "insane spinning",
	"swimming", "water polo", "diving", 
	"rock climbing", "skate boarding",
	"chess", "go", "checkers", 
	"judo", "akido", "boxing", "wrestling", "extreme fighting"
    };

    private static final int CustomerCount = 32;
    private static final int ActivityCount = fakeActivities.length;

    public static void main(String[ ] args) {
	new BuildTablesUtil().demo();
    }
    
    private void demo() {
	try {
	    Connection conn = openConnection();
	    Statement stmt = conn.createStatement();
	    createTables(conn, stmt);

	    // clean up
	    stmt.close();
	    conn.close();
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
    }

    private void report(String msg) {
	System.err.println(msg);
    }

    private void reportAndExit(Exception e) {
	System.err.println(e);
	System.exit(-1); // abnormal termination
    }

    private Connection openConnection() {
	Connection conn = null;                                           //## Connection between program and DB system
	try {
	    Class.forName("org.sqlite.JDBC");                             //## Name of the driver to load
	    conn = DriverManager.getConnection("jdbc:sqlite:recClub.db"); //## creates DB if it doesn't exist
	} 
	catch(Exception e) {
	    reportAndExit(e);
	}
	return conn;
    }

    /**
       The SQL statements below follow the convention of capitalizing SQL-specific terms such as CREATE TABLE, but
       because this convention promotes readability: it's easier to distinguish SQL terms from programmer-selected
       terms such as table names. 

       However, all of the SQL statements shown could be completely in lower case, and work just the same.
     */
    private void createTables(Connection conn, Statement stmt) {
	try {
	    // Customers table
	    String sql = "DROP TABLE IF EXISTS customers";  // "if exists" is not standard, but SQLite supports it
	    stmt.executeUpdate(sql);
	    // in SQLite, varchar is an 'affinity type' for text
	    sql = "CREATE TABLE customers (cust_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(24) NOT NULL)";
	    
	    stmt.executeUpdate(sql);
	    addCustomers(conn);
	    report("\nCustomers table built and populated.");

	    // Activities table
	    sql = "DROP TABLE IF EXISTS activities";
	    stmt.executeUpdate(sql);
	    sql = "CREATE TABLE activities (act_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(32) NOT NULL, " + 
		"cost DECIMAL NOT NULL)";
	    stmt.executeUpdate(sql);
	    addActivities(conn);
	    report("Activities table built and populated.");

	    // Sessions table
	    sql = "DROP TABLE IF EXISTS sessions";
	    stmt.executeUpdate(sql);
	    // DATE is another SQLite 'affinity' type
	    sql = "CREATE TABLE sessions (sess_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		"fromD DATE NOT NULL, toD DATE NOT NULL, customer_id INTEGER, activity_id INTEGER, " +
		"FOREIGN KEY(customer_id) REFERENCES customers(cust_id) " +
		"FOREIGN KEY(activity_id) REFERENCES activities(act_id))";
	    stmt.executeUpdate(sql);
	    addSessions(conn);
	    report("Sessions table built and populated.");
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
    }

    private void addCustomers(Connection conn) {
	String template = "INSERT INTO customers (name) VALUES (?)";

	try {
	    PreparedStatement pstmt = conn.prepareStatement(template);

	    for (int i = 0; i < CustomerCount; i++) {
		String name = fakeNames[rand.nextInt(fakeNames.length)] + (i + 1);
		pstmt.setString(1, name);  // 1 is the first (and only) ? in the template
		pstmt.executeUpdate();
	    }
	    pstmt.close();
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
    }

    private void addActivities(Connection conn) {
	String template = "insert into activities (name, cost) values (?,?)";
	float[ ] fakeWeights = {6.2f, 12.4f, 24.8f, 31.0f}; // to get some variety in the pricing
	int len = fakeWeights.length;

	try {
	    PreparedStatement pstmt = conn.prepareStatement(template);

	    for (int i = 0; i < ActivityCount; i++) {
		pstmt.setString(1, fakeActivities[i]);
		float costF = (rand.nextFloat() + 1.1f) * fakeWeights[rand.nextInt(len)];
		pstmt.setBigDecimal(2, new BigDecimal(costF));
		pstmt.executeUpdate();
	    }
	    pstmt.close();
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
    }

    private void addSessions(Connection conn) {
	String template = "insert into sessions (fromD, toD, customer_id, activity_id) values (?,?,?,?)";
	int[ ] fakeDayDurs = {3,4,5,6,7,9,11,14,17,21,23,25};  // durations in days

	try {
	    PreparedStatement pstmt = conn.prepareStatement(template);
	    long from = new Date().getTime(); // milliseconds from the epoch
	    long to;
	    TimeUnit tunit = TimeUnit.MILLISECONDS;

	    for (int i = 0; i < 64; i++) {
		int plusDays = fakeDayDurs[rand.nextInt(fakeDayDurs.length)];
		long plusMillis = tunit.convert(plusDays, TimeUnit.DAYS);
		to = from + plusMillis;

		String fromS = new Date(from).toString().substring(0, 10);  // simplified string rep
		String toS = new Date(to).toString().substring(0, 10);      // ditto

		int customer_id = rand.nextInt(CustomerCount) + 1;  // IDs are 1,2,...,CustomerCount - 1
		int activity_id = rand.nextInt(ActivityCount) + 1;  // IDs are 1,2,...,ActivityCount - 1

		pstmt.setString(1, fromS);       // 1st ? in template
		pstmt.setString(2, toS);         // 2nd ? in template
		pstmt.setInt(3, customer_id);    // 3rd ? in template
		pstmt.setInt(4, activity_id);    // 4th ? in template
		pstmt.executeUpdate();           // Execute the insertion operation
	    }
	    pstmt.close();                       // clean up
	}
	catch(Exception e) {
	    reportAndExit(e);
	}
    }
}
