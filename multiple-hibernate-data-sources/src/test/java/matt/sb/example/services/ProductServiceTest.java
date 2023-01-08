package matt.sb.example.services;

import matt.sb.example.entities.secondary.ProductRecord;
import matt.sb.example.repositories.secondary.ProductRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRecordRepository mockProductRepository;

    @Test
    public void testGetProductByIdExisting() {
        ProductService underTest = new ProductService(mockProductRepository);

        when(mockProductRepository.findById(anyInt())).thenReturn(
                Optional.of(new ProductRecord(1, "product",
                        new BigDecimal(2), new BigDecimal(3))));

        ProductRecord result = underTest.getProductById(1);

        assertEquals("product", result.getProductName());
        assertEquals(1, result.getProductId());
        assertEquals(2, result.getCost().intValue());
        assertEquals(3, result.getPrice().intValue());
    }

    @Test
    public void testGetProductByIdNotExisting() {
        ProductService underTest = new ProductService(mockProductRepository);

        when(mockProductRepository.findById(anyInt())).thenReturn(
                Optional.empty());

        try {
            underTest.getProductById(1);
            fail();
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        }
    }
}
