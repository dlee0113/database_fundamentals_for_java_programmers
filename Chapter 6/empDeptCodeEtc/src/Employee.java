/**
   This @Enity class (Employee) together with another @Entity class (Department)
   illustrate the most common DB relation, OneToMany and its inverse ManyToOne.
   The JPA annotations are meant to capture these commonsensical points:

   -- An Employee belongs to exactly one Department.

   -- A Department may have many Employees.

   The ManyToOne mapping is from Employee (Many) to Department (One). 
   Because Department is on the 'One' side, Department is the 'target', 'owner',
   or 'parent' of the relationship.

   This example implements a bidirectional OneToMany using generics, in this
   case a generic List parameterized with Employee in the Department class.
 */

import javax.persistence.Entity;
import javax.persistence.ManyToOne;       // One is on the target side
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.JoinColumn;      // There's also PrimaryKeyJoinColumn
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.FetchType;
import javax.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {
    @Id  
    @Column(name = "eid")   
    @GeneratedValue(strategy = GenerationType.AUTO) 
    private Long id;

    @Column
    private String name;

    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)           // to prevent automatic fetches of all Employees
    @JoinColumn(name = "did", nullable = false)  // "did" is primary key in Department
    public Department getDepartment() { return this.department; }
    
    public void setDepartment(Department department) {
	this.department = department;
    }
    
    public Long getId() { return this.id; }
    
    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }
}
