package matt.sb.example.controllers;

import matt.sb.example.entities.secondary.ProductRecord;
import matt.sb.example.services.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ProductRecord getProductRecord(@RequestParam Integer id) {
        return productService.getProductById(id);
    }
}
