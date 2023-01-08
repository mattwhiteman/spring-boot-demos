package matt.sb.example.services;

import matt.sb.example.entities.secondary.ProductRecord;
import matt.sb.example.repositories.secondary.ProductRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductService {
    private final ProductRecordRepository productRepository;
    public ProductService(ProductRecordRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductRecord getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found"));
    }
}
