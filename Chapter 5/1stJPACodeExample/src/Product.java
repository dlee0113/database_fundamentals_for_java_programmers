import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;

@Entity                            // a JPA-managed Entity
@Table(name = "products")          // optional: if omitted, the table name would be "Product"
public class Product {  
    @Id  
    @Column(name = "id")           // optional: if omitted, this would be the name
    @GeneratedValue                
    private Integer id;  

    @Column(name = "prodName")     // optional: sets name to 'prodName' instead of 'productName'
    private String productName;  

    public Integer getId() {  
	return id;  
    }  

    public String getProductName() {  
	return this.productName;  
    }  
    public void setProductName(String productName) {  
	this.productName = productName;
    }  

    @Override
    public String toString() {
	return "Superstuff " + getId() + ": " + getProductName() + "\n";
    }
}  
 