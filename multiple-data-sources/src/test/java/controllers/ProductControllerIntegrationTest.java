package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import matt.sb.example.Application;
import matt.sb.example.entities.secondary.ProductRecord;
import matt.sb.example.repositories.secondary.ProductRecordRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRecordRepository productRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        productRecordRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        productRecordRepository.deleteAll();
    }

    @Test
    public void testGetProduct_Exists() throws Exception {
        ProductRecord record = new ProductRecord(1, "product",
                new BigDecimal(2), new BigDecimal(3));

        productRecordRepository.save(record);

        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/products?id=1", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        ProductRecord recordResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                ProductRecord.class);

        assertEquals("product", recordResult.getProductName());
        assertEquals(1, recordResult.getProductId());
        assertEquals(2, recordResult.getCost().intValue());
        assertEquals(3, recordResult.getPrice().intValue());
    }

    @Test
    public void testGetProduct_NotExists() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/products?id=1", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }
}
