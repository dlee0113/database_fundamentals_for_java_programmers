/** Dependencies:

    Before this code can be run against PostgreSQL, the
    database named 'empdept' must be created:

    psql
      #> create database empdept;
      #> \q
 */


import javax.persistence.EntityManager;  
import javax.persistence.EntityManagerFactory;  
import javax.persistence.Persistence;  
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Random;

public class EmpDept {
    private EntityManagerFactory emf;
    private EntityManager em;
    
    public EmpDept() {
	emf = Persistence.createEntityManagerFactory("empsPU");
	em = emf.createEntityManager();
    }
    
    public static void main(String[ ] args) {
	new EmpDept().demo();
    }

    private void demo() {
	//### Uncomment the following line on the 1st run to populate the tables.
	// Department dept = createInstances(); // uncomment this on the 1st run to populate tables
	Department dept = null;
	try {
	    dept = em.getReference(Department.class, 9L); // 9 is the primary key for the Department
	}
	catch(Exception e) { }
	
	if (dept != null) {
	    List<Employee> list = dept.getEmployees();
	    for (Employee emp : list) System.out.println(emp.getName() + ": " + emp.getId());
	}
    }

    private Department createInstances() {
	String[ ] someEmps = {"Bob", "Carol", "Ted", "Alice",
			      "Fred", "Wilma", "Barney", "Betty"};
	Employee[ ] createdEmps = new Employee[someEmps.length];
	
	Department dept = new Department();
	dept.setName("Computing");

	for (int i = 0; i < someEmps.length; i++) {
	    createdEmps[i] = new Employee();
	    createdEmps[i].setName(someEmps[i]);
	    createdEmps[i].setDepartment(dept);
	    dept.addEmployee(createdEmps[i]);
	}

	try {
	    em.getTransaction().begin();
	    for (int i = 0; i < createdEmps.length; i++)
		em.persist(createdEmps[i]);
	    em.persist(dept);
	    em.getTransaction().commit();
	}
	catch(Exception e) { e.printStackTrace(); }

	return dept;
    }
}
