import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;  
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;       // introduced in JPA 2 to specify the type of the returned result
import java.util.List;
import java.util.Arrays;
import java.math.BigDecimal;

public class PersistenceManagerCAS {  
    EntityManagerFactory emf;

    public static void main(String[ ] args) {
	new PersistenceManagerCAS().demo();
    }

    public PersistenceManagerCAS() { 
	emf = Persistence.createEntityManagerFactory("recClubPU");  // mataches name in persistence.xml
    }

    private void demo() {
	EntityManager em = getEntityManager();

	// Let's start with a 'native query'
	String sql = "SELECT * FROM activities WHERE cost > 45 AND cost < 60 AND act_id >= 10";
	Query query = em.createNativeQuery(sql);       //### Note well: 'native query'
	List<Object[ ]> list = query.getResultList();  // each row is an Object[ ], with field values as elements
	dump(list);

	// Use the NamedQuery from the Session class
	TypedQuery<Session> tquery = em.createNamedQuery("Session.getAll", Session.class);
	List<Session> slist = tquery.getResultList();
	for (Session sess : slist) System.out.println(sess);

	// A sample (implicit) JOIN
	sql = "SELECT DISTINCT c.name FROM Customer AS c, Session AS s WHERE c.id = s.customerId AND c.id > 20";
	List<String> names = em.createQuery(sql, String.class).getResultList();
	for (String name : names) System.out.println(name);

	// A parameterized query with IN
	sql = "SELECT a FROM Activity AS a WHERE a.name IN :array";
	TypedQuery<Activity> taq = em.createQuery(sql, Activity.class);
	taq.setParameter("array", Arrays.asList("go", "judo", "akido"));
	List<Activity> alist = taq.getResultList();
	for (Activity act : alist) System.out.println(act); // prints all three: go, judo, and akido records

	// Do it again, but with an explicit JOIN
	sql = "SELECT a FROM Session AS s JOIN s.name AS a WHERE a.name IN :array";
	taq.setParameter("array", Arrays.asList("go", "judo", "akido"));
	alist = taq.getResultList();
	for (Activity act : alist) System.out.println(act); // prints all three: go, judo, and akido records

	//#### Illustrate the UD operations in CRUD.
	em.getTransaction().begin(); 
	Customer firstCustomer = em.find(Customer.class, 1);  // 1 is the id
	if (firstCustomer != null)
	    firstCustomer.setName("Priscilla1");              // name was 'Piggly1'
	em.getTransaction().commit();                         // implies 'flush': name changed to Priscilla1

	// Delete the last customer, currently Barney32
	em.getTransaction().begin(); 
	Customer lastCustomer = em.find(Customer.class, 32); 
	if (lastCustomer != null)
	    em.remove(lastCustomer); 
	em.getTransaction().commit();   // implies 'flush'
    }

    private void dump(List<Object[ ]> list) {
	System.out.println("\n");
	for (Object[ ] array : list) 
	    for (Object obj : array)
		System.out.println(obj);
    }

    private EntityManager getEntityManager() {  
        if (emf == null) 
            emf = Persistence.createEntityManagerFactory("ProductPU");
	return emf.createEntityManager();
    } 
}  