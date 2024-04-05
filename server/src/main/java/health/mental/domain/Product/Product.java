package health.mental.domain.Product;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Entity(name = "product")
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Product(ProductResponseDTO productResponseDTO) {
        this.name = productResponseDTO.getName();
        this.price = new BigDecimal(productResponseDTO.getPrice());
    }

    public Product() {
    }


}
