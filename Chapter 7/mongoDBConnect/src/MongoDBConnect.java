import java.util.List;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.ListCollectionsIterable;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.MongoClient;
import org.bson.Document;
 
public class MongoDBConnect {
     public static void main(String[ ] args) {
        try {
	    MongoClient mongoClient = new MongoClient("localhost"); // default port is 27017

	    MongoIterable<String> databases = mongoClient.listDatabaseNames();
	    MongoCursor<String> it = databases.iterator();
            
	    while (it.hasNext()) {
		String dbName = (String) it.next();
		System.out.println(" Database: " + dbName);
                
		MongoDatabase db = mongoClient.getDatabase(dbName);
                
		ListCollectionsIterable<Document> collections = db.listCollections();
		for (Document doc : collections) {
		    System.out.println("\t + Collection: " + doc.toString());
		}
	    }	
	    mongoClient.close();
	}
	catch (Exception e) { e.printStackTrace(); }
     }
}

