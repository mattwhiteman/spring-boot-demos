package matt.sb.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import matt.sb.example.Application;
import matt.sb.example.models.AccumulatedWork;
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

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class WorkCalculatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testWork() throws Exception {
        List<Integer> inputVals = new LinkedList<>();
        for(int i = 0; i < 5; i++) {
            inputVals.add(i);
        }

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/work", 1)
                        .content(objectMapper.writeValueAsString(inputVals))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        AccumulatedWork workResult = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), AccumulatedWork.class);

        assertEquals(5, workResult.getResults().size());
        assertEquals(0, workResult.getResults().get(0).getWorkResult());
        assertEquals(1, workResult.getResults().get(1).getWorkResult());
        assertEquals(4, workResult.getResults().get(2).getWorkResult());
        assertEquals(9, workResult.getResults().get(3).getWorkResult());
        assertEquals(16, workResult.getResults().get(4).getWorkResult());
    }
}
