package health.mental.controller;

import health.mental.domain.Product.Product;
import health.mental.domain.Product.ProductMapper;
import health.mental.domain.Product.ProductRequestDTO;
import health.mental.domain.Product.ProductResponseDTO;
import health.mental.repositories.ProductRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {


        @Autowired
        private ProductRepository productRepository;

        private final ProductMapper productMapper = new ProductMapper();

        @PostMapping
        public ResponseEntity postProduct(@RequestBody  ProductRequestDTO productRequestDto) {

             Product p =  productRepository.save(productMapper.mapToProduct(productRequestDto));
            return ResponseEntity.ok(productMapper.mapProductToDTO(p));
        }

        @GetMapping
        public ResponseEntity getAllProducts() {

                List<ProductResponseDTO> productList = productMapper.mapToProductResponseDTO(productRepository.findAll());

            return ResponseEntity.ok(productList);
        }


        @GetMapping("/{id}")
        public ResponseEntity getProductById(@PathVariable Long id) {
            Product product = productRepository.findById(id).orElse(null);
            if(product == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productMapper.mapProductToDTO(product));
        }

}
