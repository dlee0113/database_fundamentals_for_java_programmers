import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.NamedQuery;
import java.math.BigDecimal;

@Entity                            
@Table(name = "customers")        
@NamedQuery(name = "Customer.getAll", query = "SELECT r FROM Customer r") 
public class Customer {  
    @Id  
    @Column(name = "cust_id")           
    @GeneratedValue(strategy = GenerationType.IDENTITY)  
    private Integer id;  

    @Column     
    private String name;

    public Integer getId() { return id; }  

    public String getName() { return this.name; }  
    public void setName(String name) { this.name = name; }  

    @Override
    public String toString() {
	return "Customer " + getId() + ": " + getName() + "\n";
    }
}  
 