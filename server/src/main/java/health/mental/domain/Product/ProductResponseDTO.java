package health.mental.domain.Product;

public class ProductResponseDTO {


private String name;

    private String price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ProductResponseDTO(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public ProductResponseDTO(Product product) {
        this.name = product.getName();
        this.price = product.getPrice().toString();
    }

}
