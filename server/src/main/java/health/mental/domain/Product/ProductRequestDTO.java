package health.mental.domain.Product;

import java.math.BigDecimal;

public class ProductRequestDTO {

    private String name;

    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = new BigDecimal(price.doubleValue());
    }

    public ProductRequestDTO(String name, BigDecimal price) {
        this.name = name;
        this.price = new BigDecimal(price.doubleValue());
    }
}
