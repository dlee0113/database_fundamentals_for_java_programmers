import java.util.Random;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;  // for IDs
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;

// Only two imports needed on the MongoDB side.
import org.bson.Document;
import com.mongodb.MongoClient;

public class RecClubMongo {
    private final static String database = "recClub";  // mongo will create
    
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
    private static final int SessionCount = CustomerCount * 2;
    
    private static final Integer[ ] custIds = new Integer[CustomerCount];
    private static final Integer[ ] actIds = new Integer[ActivityCount];

    private MongoClient mongoClient;
    private AtomicInteger idGenerator;
    private Random rand;
    
    public RecClubMongo() {
	mongoClient = new MongoClient("localhost");
	idGenerator = new AtomicInteger();           // initialized to zero
	rand = new Random();
    }

    public static void main(String[ ] args) {
	new RecClubMongo().demo();
    }

    private void demo() {
	addCustomers();
	addActivities();
	addSessions();
    }

    private void addCustomers() {
	for (int i = 0; i < CustomerCount; i++) {
	    String name = fakeNames[rand.nextInt(fakeNames.length)] + (i + 1);
	    int id = idGenerator.incrementAndGet();
	    custIds[i] = id;
	    addCustomer(id, name);
	}
    }

    // Write to DB. Below is what the 1st record looks like, and the others, of course,
    // have the same pattern:
    // { "_id" : ObjectId("5810f82379e6a6564f06472f"), "id" : 1, "name" : "Homer1" }
    private void addCustomer(Integer id, String name) {
	try {
	    mongoClient
		.getDatabase(database)
		.getCollection("customers")
		.insertOne(new Document()
			   .append("id", id)
			   .append("name", name));
	}
	catch(Exception e) { System.err.println(e); }
    }

    private void addActivities() {
	float[ ] fakeWeights = {6.2f, 12.4f, 24.8f, 31.0f}; // to get some variety in the pricing
	int len = fakeWeights.length;
	
	for (int i = 0; i < ActivityCount; i++) {
	    String name = fakeActivities[i];
	    float costF = (rand.nextFloat() + 1.1f) * fakeWeights[rand.nextInt(len)];
	    BigDecimal cost = new BigDecimal(costF);
	    int id = idGenerator.incrementAndGet();
	    actIds[i] = id;
	    addActivity(id, name, cost);
	}
    }

    // Write to DB.
    // { "_id" : ObjectId("5810f82379e6a6564f06474f"), "id" : 33, "name" : "squash", "cost" : 63.32413864135742 }
    private void addActivity(Integer id, String name, BigDecimal cost) {
	try {
	    mongoClient
		.getDatabase(database)
		.getCollection("activities")
		.insertOne(new Document()
			   .append("id", id)
			   .append("name", name)
			   .append("cost", cost.floatValue()));
	}
	catch(Exception e) { System.err.println(e); }
    }

    private void addSessions() {
	int[ ] fakeDayDurs = {3,4,5,6,7,9,11,14,17,21,23,25};  // durations in days
	long from = new Date().getTime(); // milliseconds from the epoch
	long to;
	TimeUnit tunit = TimeUnit.MILLISECONDS;

	for (int i = 0; i < SessionCount; i++ ) {
	    int plusDays = fakeDayDurs[rand.nextInt(fakeDayDurs.length)];
	    long plusMillis = tunit.convert(plusDays, TimeUnit.DAYS);
	    to = from + plusMillis;
		
	    String fromS = new Date(from).toString().substring(0, 10);  // simplified string rep
	    String toS = new Date(to).toString().substring(0, 10);      // ditto
	    
	    int custId = custIds[rand.nextInt(CustomerCount)]; // pick a random Customer
	    int actId = actIds[rand.nextInt(ActivityCount)];   // pick a random Activity

	    int id = idGenerator.incrementAndGet();
	    addSession(id, fromS, toS, custId, actId);
	}
    }

    // Write to DB.
    //  { "_id" : ObjectId("5810f96279e6a65695a73ef6"), "id" : 53, "fromD" : "Wed Oct 26", 
    //    "toD" : "Sun Nov 20", "custId" : 24, "actId" : 40 }
    private void addSession(Integer id, String fromD, String toD, Integer custId, Integer actId) {
	try {
	    mongoClient
		.getDatabase(database)
		.getCollection("sessions")
		.insertOne(new Document()
			   .append("id", id)
			   .append("fromD", fromD)
			   .append("toD", toD)
			   .append("custId", custId)
			   .append("actId", actId));
	}
	catch(Exception e) { System.err.println(e); }
    }
}


