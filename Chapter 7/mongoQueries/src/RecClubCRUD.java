import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;      // com.mongodb.client contains interfaces for the client API
import com.mongodb.client.MongoCollection;
import java.util.regex.Pattern;

public class RecClubCRUD {
    private final static String database = "recClub";
    
    private MongoClient mongoClient;

    public RecClubCRUD() {
       mongoClient = new MongoClient("localhost");
    }

    public void createCustomer(Integer id, String name) {
	try {
	    mongoClient
		.getDatabase(database)
		.getCollection("customers")
		.insertOne(new Document()              // new JSON document
			   .append("id", id)
			   .append("name", name));
	}
	catch(Exception e) { System.err.println(e); }
    }
    
    public void getAll(String name) {
	try {
	    FindIterable<Document> iter = mongoClient
		.getDatabase(database)
		.getCollection(name)
		.find();
	    
	    iter.forEach(new Block<Document>() {      // Block is an interface construct for accessing a Document
		    @Override
		    public void apply(Document doc) { // one and only method declared in Block
			System.out.println(doc);
		    }
		});	  
	}
	catch (Exception e) { System.err.println(e); }
    }

    public void getOne(int id) {
	MongoCollection<Document> collection = 
	    mongoClient.getDatabase(database).getCollection("sessions"); // hardwired for illlustration

	Document query = new Document("id", id);
	FindIterable<Document> search = collection.find(query);

	for (Document doc : search)
	    System.out.printf("Session %d is from %s to %s.\n",
			      id, doc.getString("fromD"), doc.getString("toD"));
    }

    public void getOneRegex(String pattern) {
	MongoCollection<Document> collection = 
	    mongoClient.getDatabase(database).getCollection("activities"); // hardwired for illlustration

	// Document is now: key for field ('name' in this case), and pattern the value must satisfy
	Document regexQuery = new Document("name", Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
	FindIterable<Document> search = collection.find(regexQuery);
	for (Document doc : search)
	    System.out.printf("Activity %d is named %s.\n", doc.getInteger("id"), doc.getString("name"));
    }
    
    public void updateActivity(String name, float cost) {
	try {
	    mongoClient
		.getDatabase(database)
		.getCollection("activities")
		.updateOne(new Document("name", name),
			   new Document("$set", new Document("cost", cost)));
	    
	}
	catch (Exception e) { System.err.println(e); }
    }

    public void delete(String name) {
	try {
	    mongoClient
		.getDatabase(database)
		.getCollection("customers")
		.deleteOne(new Document("name", name));
	}
	catch (Exception e) { System.err.println(e); }
    }
}


