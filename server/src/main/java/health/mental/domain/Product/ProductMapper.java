package health.mental.domain.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static List<ProductResponseDTO>  mapToProductResponseDTO(List<Product> products) {
        return products.stream().map(ProductResponseDTO::new).collect(Collectors.toList());
    }

    public Product mapToProduct(ProductRequestDTO productRequestDto) {

        return new Product(productRequestDto.getName(), productRequestDto.getPrice());


    }

    public ProductResponseDTO mapProductToDTO(Product p) {

        return new ProductResponseDTO(p);
    }
}
