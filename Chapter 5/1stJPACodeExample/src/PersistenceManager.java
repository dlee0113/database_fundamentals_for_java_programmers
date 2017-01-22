import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;  
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.List;

public class PersistenceManager {  
    EntityManagerFactory emf;
    
    public static void main(String[ ] args) {
	new PersistenceManager().demo();
    }

    public PersistenceManager() { 
	emf = Persistence.createEntityManagerFactory("ProductPU"); // from persistence.xml
    }

    private void demo() {
	EntityManager em = getEntityManager();

	// Create and persist a new product.
	Product prod = new Product();
	prod.setProductName("Super stuff");

	em.getTransaction().begin();
	if (!em.contains(prod)) {      // EntityManager maintains a list of things to persist
	    try {
		em.persist(prod);      // add the new Product to the list
		em.flush();            // flush the list -- save to the DB
	    }
	    catch(PersistenceException e) {
		System.err.println("New product not saved to DB.");
	    }
	}
	em.getTransaction().commit();

	// Now query the DB
	Query query = em.createQuery("Select p FROM Product p");  // QL, not SQL
	List<Product> products = query.getResultList();
	for (Product product : products)
	    System.out.println(product);

	em.close();
    }

    private EntityManager getEntityManager() {  
        if (emf == null) 
            emf = Persistence.createEntityManagerFactory("ProductPU");
	return emf.createEntityManager();
    }  
}  
