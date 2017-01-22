/**
   Requirements: core Java and SQLite are installed; the Java 'driver' for SQLite is available.

   Overview:

   This program connects to an SQLite3 databased named 'test.db'.
   If this database does not exist already, it is created (in the working directory). 
   Here's a simple depiction:

                               packaged in a JAR file
                                         /               installed on local system
                               +--------------------+            /
        BasicJDBC code<------->| SQLite DB 'driver' |<------->SQLite<------->a persistent SQLite database
             /                 +--------------------+                               \
        core Java code                                                   exists as a file on local system
                                                                
   Dependencies: 

   Running the code requires that the SQLite3 libraries, which are packaged in a JAR file, be on the classpath. 
   These libraries are called, for short, the "SQLite JDBC driver", and they allow a Java program to interact 
   with an SQLite3 database. The driver is available at: 

   https://bitbucket.org/xerial/sqlite-jdbc/downloads

   The name of the JAR file is arbitrary. In this case, I've simplified the name to
   
   sqlite-jdbc.jar

   The downloaded JAR has version information in the name (e.g., sqlite-jdbc-3.8.11.2.jar),
   but the JAR file under any name will do.

   Compilation and execution:

   For convenience, the working files include the executable JAR named BasicJDBC.jar. This
   can be executed at the command-line as follows:

      java -jar BasicJDBC.jar

   This JAR file can be created using the Ant script (more details later) 'build.xml':

      ant -Djar.name=BasicJDBC   ## JAR's name is fully qualified name of class that encapsulates main

   For execution (but not compilation), the SQLite JAR file must be on the classpath: the executable JAR file 
   BasicJDBC.jar handles that automatically.
 */

import java.sql.Connection;
import java.sql.DriverManager;

public class BasicJDBC {
    public static void main(String[ ] args) {
	Connection conn = null;                                         //## Connection between program and DB system
	try {
	    Class.forName("org.sqlite.JDBC");                           //## Name of the driver to load
	    conn = DriverManager.getConnection("jdbc:sqlite:test.db");  //## URL for test.db
	} 
	catch(Exception e) {
	    System.err.println(e.getClass().getName() + ": " + e.getMessage());
	    System.exit(-1);
	}

	try {
	    System.out.println("\nConnection info:\n\t" + 
			       conn.getMetaData().getDatabaseProductName() + "\n\t" +
			       conn.getMetaData().getDatabaseProductVersion() + "\n\t" +
			       conn.getMetaData().getDriverName());
	}
	catch(Exception e) {
	    System.err.println(e);
	}
    }
}
