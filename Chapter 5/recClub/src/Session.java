import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.NamedQuery;

@Entity                            
@Table(name = "sessions")        
@NamedQuery(name = "Session.getAll", query = "SELECT r FROM Session r") 
public class Session {  
    @Id  
    @Column(name = "sess_id")           
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // autoincrement by 1
    private Integer id;  

    // If a field rather than a property-defining method (get/set) is annotated,
    // then the field name--even if the field is private--can be used in JPQL queries.
    @Column
    private String fromD;   // in the existing DB, dates are stored as strings

    @Column     
    private String toD;     // ditto

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "activity_id")
    private Integer activityId;

    public Integer getId() { return id; }  

    public String getFromD() { return this.fromD; }
    public void setFromD(String fromD) { this.fromD = fromD; }

    public String getToD() { return this.toD; }
    public void setToD(String toD) { this.toD = toD; }

    @Override
    public String toString() {
	return "Session " + getId() + ": " + getFromD() + " to " + getToD() + "\n";
    }
}  
 