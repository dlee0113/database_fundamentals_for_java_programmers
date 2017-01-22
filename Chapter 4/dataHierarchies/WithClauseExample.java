
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;    //## set of records returned from a query, easy-to-use getter methods
import java.sql.SQLException; //## 10 subtypes for specialized trouble-shooting

public class WithClauseExample {
    public static void main(String[ ] args) {
	new WithClauseExample().demo();
    }

    private void demo() {
	try {
	    Connection conn = openConnection();
	    demoWithClause(conn);
	    conn.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

    private void demoWithClause(Connection conn) {
	String bsf = "ORDER BY 2 ASC) ";     // breadth-first
	String dsf = "ORDER BY 2 DESC) ";    // depth-first

	/* The SQL SUBSTR function takes 3 args:
	   -- 1st is the string, in this case a string of dots
	   -- 2nd is where to start (e.g., 0 means 'start at the beginning')
	   -- 3rd is how many characters to take (e.g., 3)
	 */
	String sql1 = 
	    "WITH RECURSIVE " +                                            // recursively walk the tree structure
	    "under_carol(name, level) AS " +                               // track name and tree level recursively
	    "(VALUES('Carol', 0) " +                                       // to begin, name 'Carol' and level 0
	    "UNION ALL " +                                                 // take the union of the following:
	    "SELECT orgchart.name, under_carol.level + 1 FROM orgchart " + // get the names/levels under Carol 
	    "JOIN under_carol ON orgchart.reportsTo = under_carol.name " + // joined direct reports under a boss
	    bsf +                                                          // order by 2nd column in recursive views
	    "SELECT SUBSTR('.........', 1, level * 3) || name FROM under_carol"; //** || is string concatenation
	
	String sql2 = 
	    "WITH RECURSIVE " +                                            // recursively walk the tree structure
	    "under_carol(name, level) AS " +                               // track name and tree level recursively
	    "(VALUES('Carol', 0) " +                                       // to begin, name 'Carol' and level 0
	    "UNION ALL " +                                                 // take the union of the following:
	    "SELECT orgchart.name, under_carol.level + 1 FROM orgchart " + // get the names/levels under Carol 
	    "JOIN under_carol ON orgchart.reportsTo = under_carol.name " + // joined direct reports under a boss
	    dsf +                                                          // order by 2nd column in recursive views
	    "SELECT SUBSTR('.........', 1, level * 3) || name FROM under_carol"; //** || is string concatenation

	try {
	    Statement stmt = conn.createStatement();

	    System.out.println("\nBreadth-first search:\n");
	    ResultSet rs = stmt.executeQuery(sql1);
	    dumpResultSet(rs);

	    System.out.println("\nDepth-first search:\n");
	    rs = stmt.executeQuery(sql2);
	    dumpResultSet(rs);

	    stmt.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }

    private void dumpResultSet(ResultSet rs) {
	try {
	    while (rs.next()) {
		String str = rs.getString(1);
		System.out.println(str);
	    }
	    rs.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/** Output from the breadth-first search:

	    Carol
	    ...Alice
	    ...Bob
	    ...Ted
	    ......Edgar
	    ......Edna
	    ......Joyce
	    ......Al
	    ......Maria
	    ......Nick
	    ......Ky
	    ......Nina
	    ......Ruth
	    .........Terry
	    .........Joe
	    .........Petra

	    Output from depth-first search:

	    Carol
	    ...Alice
	    ......Edgar
	    .........Terry
	    ......Edna
	    .........Joe
	    ......Joyce
	    ...Bob
	    ......Al
	    ......Maria
	    ......Nick
	    ...Ted
	    ......Ky
	    .........Petra
	    ......Nina
	    ......Ruth
	*/
    }

    private void reportAndExit(Exception e) {
	System.err.println(e);
	System.exit(-1);
    }

    private Connection openConnection() {
	Connection conn = null;                                          
	try {
	    Class.forName("org.sqlite.JDBC");                            
	    conn = DriverManager.getConnection("jdbc:sqlite:hierarchy.db"); 
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
