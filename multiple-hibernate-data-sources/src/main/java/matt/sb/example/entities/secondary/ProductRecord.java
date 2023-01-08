package matt.sb.example.entities.secondary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="products")
public class ProductRecord implements Serializable {
    private static final long serialVersionUID = 5937620045212743932L;
    @Id
    @Column(name="id")
    private Integer productId;

    @Column(name="name")
    private String productName;

    @Column(name="cost")
    private BigDecimal cost;

    @Column(name="price")
    private BigDecimal price;
}
