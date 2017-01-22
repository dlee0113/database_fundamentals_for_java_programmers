import javax.persistence.Entity;
import javax.persistence.OneToMany;  // Many is on the target side
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "departments")
public class Department {
    @Id  
    @Column(name = "did")   
    @GeneratedValue(strategy = GenerationType.AUTO) 
    private Long id;
    
    @Column
    private String name;

    private List<Employee> employees;

    public Department() { employees = new ArrayList<Employee>(); }

    @OneToMany(orphanRemoval = true,    // at ORM level, remove Emps if Department is removed
	       mappedBy = "department") // annotated field in the Employee class
    public List<Employee> getEmployees() { return this.employees; }

    public void addEmployee(Employee emp) { employees.add(emp); }
    
    public Long getId() { return this.id; }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
}
