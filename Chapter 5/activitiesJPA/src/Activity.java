import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.NamedQuery;
import java.math.BigDecimal;

@Entity                            
@Table(name = "activities")        
@NamedQuery(name = "Activity.getAll", query = "SELECT r FROM Activity r") // there's also @NamedQueries
public class Activity {  
    @Id  
    @Column(name = "act_id")           
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // autoincrement by 1
    private Integer id;  

    @Column     
    private String name;

    @Column
    private BigDecimal cost;

    public Integer getId() {  
	return id;  
    }  

    public String getName() {  
	return this.name;  
    }  
    public void setName(String name) {  
	this.name = name;
    }  

    public BigDecimal getCost() {
	return this.cost;
    }
    public void setCost(BigDecimal cost) {
	this.cost = cost;
    }

    @Override
    public String toString() {
	return "Activity " + getId() + ": " + getName() + " @ $" + getCost() + "\n";
    }
}  
 