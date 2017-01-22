import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;  
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;       // introduced in JPA 2 to specify the type of the returned result
import java.util.List;
import java.math.BigDecimal;

public class PersistenceManagerA {  
    EntityManagerFactory emf;

    public static void main(String[ ] args) {
	new PersistenceManagerA().demo();
    }

    public PersistenceManagerA() { 
	emf = Persistence.createEntityManagerFactory("ActivityPU");  // mataches name in persistence.xml
    }

    private void demo() {
	EntityManager em = getEntityManager();

	//### Some sample queries using QL.
	// simple SELECT
	Query query = em.createQuery("SELECT r FROM Activity AS r");  // the 'AS' could be dropped
	List<Activity> list = query.getResultList(); 
	dump(list);

	// select just one
	query = em.createQuery("SELECT r FROM Activity r WHERE r.id = 3");
	Object activity = query.getSingleResult();
	System.out.println("Singleton: " + activity);  // tiddly-winks has id 3

	// try for one that doesn't exit
	query = em.createQuery("SELECT r FROM Activity r WHERE r.id = 999");
	try {
	    activity = query.getSingleResult();
	    System.out.println("Singleton: " + activity);       
	}
	catch (Exception e) {
	    System.out.println("There's no id with value 999"); // printed
	}

	// type-safe querie and one approach to parameterized queries
	BigDecimal costOfInterest = new BigDecimal("20.50");
	TypedQuery tquery = em.createQuery("SELECT r FROM Activity r WHERE r.cost > ?1", Activity.class); // 2nd arg is the returned type
	list = tquery.setParameter(1, costOfInterest).getResultList();
	dump(list);
	
	// variation on setting the parameter
	costOfInterest = new BigDecimal("30.50");
	tquery = em.createQuery("SELECT r FROM Activity r WHERE r.cost > :cost", Activity.class);
	list = tquery.setParameter("cost", costOfInterest).getResultList();
	dump(list);

	// Of course, a query can be used without being referenced
	list = em
	    .createQuery("SELECT r FROM Activity r WHERE r.cost > :cost", Activity.class)
	    .setParameter("cost", costOfInterest)
	    .getResultList();

	// use the NamedQuery from the Activity class
	tquery = em.createNamedQuery("Activity.getAll", Activity.class);
	list = tquery.getResultList();
	dump(list);

	// projections to the field type
	tquery = em.createQuery("SELECT r.name FROM Activity AS r", String.class); // not Activity.class
	List<String> names = tquery.getResultList();
	for (String name : names) System.out.println(name);

	// projection with mixed types: this is one way, using Criteria queries with Tuples is another
	query = em.createQuery("SELECT r.name, r.cost FROM Activity AS r"); 
	List<Object[ ]> objArray = query.getResultList();
	for (Object[ ] objs : objArray) 
	    for (Object obj : objs)
		System.out.println(obj); // the polymorphic toString() methods take over here

    }

    private void dump(List<Activity> list) {
	System.out.println("\n");
	for (Activity act : list)
	    System.out.println(act);
    }

    private EntityManager getEntityManager() {  
        if (emf == null) 
            emf = Persistence.createEntityManagerFactory("ProductPU");
	return emf.createEntityManager();
    } 
}  