/** 
    Usage with executable JAR:
    
    java -jar SelectQueries.jar
*/

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;    //## set of records returned from a query, easy-to-use getter methods
import java.sql.SQLException; //## 10 subtypes for specialized trouble-shooting
import java.math.BigDecimal;

public class SelectQueries {
    public static void main(String[ ] args) {
	new SelectQueries().demo();
    }

    private void demo() {
	try {
	    Connection conn = openConnection();
	    runQueries(conn);
	    conn.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }
    
    private void runQueries(Connection conn) {
	try {
	    Statement stmt = conn.createStatement();

	    simpleSelect(stmt);                // all rows, all columns, 1 table
	    filteredSelect(stmt);              // some rows, all columns, 1 table
	    projectSelect(stmt);               // all rows, some columns, 1 table
	    miscFunctionsAndModifiers(stmt);   // sum, count, distinct, group by
	    miscJoins(stmt);                   // rows/columns, > 1 table
	    subquerySelect(stmt);              // subquery example

	    stmt.close();
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
    }
    
    //## all rows, all columns, 1 table
    private void simpleSelect(Statement stmt) {
	try {
	    String sql = "SELECT * FROM activities"; 
	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);   //## set of records returned from query
	    while (rs.next()) {                      //## Iterate over the result set, printing each record.
		int id = rs.getInt("act_id");
		String name = rs.getString("name");
		BigDecimal cost = rs.getBigDecimal("cost");

		String output = String.format("%2d  %s  $%.2f", id, name, cost);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/** 
	     SELECT * FROM activities

	     1  squash  $10.77
	     2  3-on-3 basketball  $42.48
	     3  tiddly winks  $14.86
	     4  tennis  $51.43
	     5  basic spinning  $53.86
	     6  intermediate spinning  $38.94
	     7  insane spinning  $58.94
	     8  swimming  $61.79
	     9  water polo  $12.98
	    10  diving  $20.04
	    11  rock climbing  $54.05
	    12  skate boarding  $9.36
	    13  chess  $10.93
	    14  go  $59.33
	    15  checkers  $36.00
	    16  judo  $10.03
	    17  akido  $22.08
	    18  boxing  $46.00
	    19  wrestling  $7.11
	    20  extreme fighting  $9.10
	 */
    }

    //## some rows, all columns, 1 table
    private void filteredSelect(Statement stmt) {
	try {
	    String sql = "SELECT * FROM customers WHERE cust_id > 20"; 
	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);   //## set of records returned from query
	    while (rs.next()) {                      //## Iterate over the result set, printing each record.
		int id = rs.getInt("cust_id");
		String name = rs.getString("name");
		String output = String.format("%2d  %s", id, name);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT * FROM customers WHERE cust_id > 20

	   21  Gracie21
	   22  Piggly22
	   23  Gracie23
	   24  Lisa24
	   25  Curly25
	   26  Moe26
	   27  Marge27
	   28  Moe28
	   29  Pebbles29
	   30  Marge30
	   31  Larry31
	   32  Homer32
	*/

	try {
	    String sql = "SELECT * FROM activities WHERE cost > 45 AND cost < 60 AND act_id >= 10"; 

	    //### Equivalent query using the 'between' condition
	    String sql2 = "SELECT * FROM activities WHERE cost BETWEEN 45 AND 60 AND act_id >= 10"; 

	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);   //## set of records returned from query
	    while (rs.next()) {                      //## Iterate over the result set, printing each record.
		int id = rs.getInt("act_id");
		String name = rs.getString("name");
		BigDecimal cost = rs.getBigDecimal("cost");
		String output = String.format("%2d  %s %.2f", id, name, cost);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT * FROM activities WHERE cost > 45 cost < 60 AND act_id >= 10

	   11  rock climbing 54.05
	   14  go 59.33
	   18  boxing 46.00
	*/
    }

    //## all rows, selected columns, 1 table
    private void projectSelect(Statement stmt) {
	try {
	    String sql = "SELECT cost FROM activities"; 
	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);   //## set of records returned from query
	    while (rs.next()) {                      //## Iterate over the result set, printing each record.
		BigDecimal cost = rs.getBigDecimal("cost");
		String output = String.format("%5.2f", cost);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT cost FROM activities

	   10.77
	   42.48
	   14.86
	   51.43
	   53.86
	   38.94
	   58.94
	   61.79
	   12.98
	   20.04
	   54.05
 	    9.36
	   10.93
	   59.33
	   36.00
	   10.03
	   22.08
	   46.00
	    7.11
	    9.10
	 */

	try {
	    String sql = "SELECT sess_id, fromD, toD FROM sessions"; 
	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {                      
		int id = rs.getInt(1);          //## by index position, starting at 1
		String from = rs.getString(2);  //## position 2
		String to = rs.getString(3);    //## position 3
		String output = String.format("%2d %s %s", id, from, to);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/** 
	    SELECT sess_id, fromD, toD FROM sessions

	    1 Fri Aug 26 Mon Aug 29
	    2 Fri Aug 26 Fri Sep 02
	    3 Fri Aug 26 Fri Sep 02
	    4 Fri Aug 26 Fri Sep 16
	    5 Fri Aug 26 Fri Sep 09
           ...
	   10 Fri Aug 26 Fri Sep 02
	   11 Fri Aug 26 Sun Sep 18
	   12 Fri Aug 26 Sun Sep 18
	   ...
	   62 Fri Aug 26 Fri Sep 02
	   63 Fri Aug 26 Tue Aug 30
	   64 Fri Aug 26 Fri Sep 16
	*/
    }

    //## various functions and modifiers, 1 table
    private void miscFunctionsAndModifiers(Statement stmt) {
	try {
	    String sql = "SELECT DISTINCT toD FROM sessions";
	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {                      
		String toD = rs.getString("toD"); 
		String output = String.format("%s", toD);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT DISTINCT toD FROM sessions

	   Mon Aug 29
	   Fri Sep 02
	   Fri Sep 16
	   Fri Sep 09
	   Tue Sep 13
	   Tue Aug 30
	   Tue Sep 06
	   Sun Sep 18
	   Wed Aug 31
	   Thu Sep 01
	*/

	try {
	    String sql = "SELECT DISTINCT toD FROM sessions GROUP BY activity_id";

	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {      
		String toD = rs.getString("toD"); 
		String output = String.format("%s", toD);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT DISTINCT toD FROM sessions GROUP BY activity_id

	   Wed Aug 31
	   Fri Sep 16
	   Tue Sep 13
	   Tue Aug 30
	   Fri Sep 09
	   Fri Sep 02
	   Sun Sep 18
	   Tue Sep 06
	   Thu Sep 01
	 */
	
	try {
	    String sql = "SELECT COUNT(cust_id) AS total FROM customers";

	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {      
		int n = rs.getInt("total");
		String output = String.format("%d", n);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/** 
	    32
	*/

	try {
	    String sql = "SELECT SUM(cost) AS total FROM activities";

	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {      
		float total = rs.getFloat("total");
		String output = String.format("%f", total);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   630.083374
	 */
    }

    //## selected rows, selected columns, > 1 tables
    private void miscJoins(Statement stmt) {
	try {
	    String sql = 
		"SELECT activities.name, sessions.fromD, sessions.toD FROM activities, " +
		"sessions WHERE sessions.activity_id = activities.act_id";
	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {      
		String name = rs.getString("name");
		String fromD = rs.getString("fromD");
		String toD = rs.getString("toD");
		String output = String.format("%s | %s | %s", name, fromD, toD);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT activities.name, sessions.fromD, sessions.toD from activities, sessions 
	   WHERE sessions.activity_id = activities.act_id

	   insane spinning | Fri Aug 26 | Mon Aug 29
	   diving | Fri Aug 26 | Fri Sep 02
	   3-on-3 basketball | Fri Aug 26 | Fri Sep 02
	   checkers | Fri Aug 26 | Fri Sep 16
	   boxing | Fri Aug 26 | Fri Sep 09
	   chess | Fri Aug 26 | Tue Sep 13
	   water polo | Fri Aug 26 | Tue Aug 30
	   skate boarding | Fri Aug 26 | Tue Aug 30
	   go | Fri Aug 26 | Tue Sep 06
	   skate boarding | Fri Aug 26 | Fri Sep 02
	   3-on-3 basketball | Fri Aug 26 | Sun Sep 18
	   squash | Fri Aug 26 | Sun Sep 18
	   tiddly winks | Fri Aug 26 | Tue Sep 06
	   wrestling | Fri Aug 26 | Tue Sep 13
	   rock climbing | Fri Aug 26 | Tue Sep 06
	   skate boarding | Fri Aug 26 | Fri Sep 09
	   akido | Fri Aug 26 | Fri Sep 02
	   skate boarding | Fri Aug 26 | Fri Sep 09
	   water polo | Fri Aug 26 | Sun Sep 18
	   basic spinning | Fri Aug 26 | Sun Sep 18
	   ...
	*/

	try {
	    String sql = 
		"SELECT customers.name as cname, activities.name AS aname FROM customers, activities, sessions " +
		"WHERE sessions.activity_id = activities.act_id AND sessions.customer_id = customers.cust_id";

	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {      
		String cname = rs.getString("cname");
		String aname = rs.getString("aname");
		String output = String.format("%s | %s", cname, aname);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT customers.name as cname, activities.name AS aname FROM customers, activities, sessions 
	   WHERE sessions.activity_id = activities.act_id AND sessions.customer_id = customers.cust_id

	   Larry31 | insane spinning
	   Marge1 | diving
	   Bob7 | 3-on-3 basketball
	   Lisa24 | checkers                  ***
	   Lisa13 | boxing
	   Wiggly6 | chess
	   Lisa24 | water polo                ***
	   Barney12 | skate boarding
	   Marge27 | go
	   Selma8 | skate boarding
	   Marge5 | 3-on-3 basketball
	   Piggly17 | squash
	   Lisa13 | tiddly winks
	   Selma8 | wrestling
	   Larry19 | rock climbing
	   Wiggly14 | skate boarding
	   Curly25 | akido
	   Patty16 | skate boarding
	   Marge27 | water polo
	   Lisa13 | basic spinning
	   Piggly22 | go
	   Selma8 | akido
	   Homer32 | 3-on-3 basketball
	   Bob11 | chess
	   Marge1 | boxing
	   Piggly17 | diving
	   Barney12 | rock climbing
	   Lisa24 | basic spinning            ***
	   Gracie2 | skate boarding
	   Moe28 | tiddly winks
	   Piggly22 | squash
	   Marge30 | basic spinning
	   Selma8 | judo
	   Marge1 | tiddly winks
	   Bob11 | judo
	   ...
	*/
    }

    private void subquerySelect(Statement stmt) {
	try {
	    /** Conceptual view: only the sessions table combines customer and activity info,
		and this through foreign keys (customer_id and activity_id) in that table.
		The goal is to get the names of customers who play Go. The approach is to
		use three 'select' statements. Let's start from the last of these and work
		forward to the first:

		-- Get the Id(s) for the game named 'Go'. There's only one.        (activities table)
		-- Get the customer(s) who signed up for an activity with that Id. (sessions table)
		-- Get the name(s) of such customers.                              (customers table)
	     */
	    String sql = "SELECT name FROM customers WHERE cust_id IN " +    
		"(SELECT customer_id FROM sessions WHERE activity_id IN " +  // nested select in parens
		"(SELECT act_id FROM activities WHERE name = 'go'))";        // ditto

	    System.out.println("\n" + sql + "\n");
	    ResultSet rs = stmt.executeQuery(sql);  
	    while (rs.next()) {      
		String name = rs.getString("name");
		String output = String.format("%s plays Go.", name);
		System.out.println(output);
	    }
	}
	catch(SQLException e) {
	    reportAndExit(e);
	}
	/**
	   SELECT name FROM customers WHERE cust_id IN
	   (SELECT customer_id FROM sessions WHERE activity_id IN
	   (SELECT act_id FROM activities WHERE name = 'go'))

	   Piggly22 plays Go.
	   Marge27 plays Go.
	*/
    }

    private void reportAndExit(Exception e) {
	System.err.println(e);
	System.exit(-1);
    }

    private Connection openConnection() {
	Connection conn = null;                                           //## Connection between program and DB system
	try {
	    Class.forName("org.sqlite.JDBC");                             //## Name of the driver to load
	    conn = DriverManager.getConnection("jdbc:sqlite:recClub.db");
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
