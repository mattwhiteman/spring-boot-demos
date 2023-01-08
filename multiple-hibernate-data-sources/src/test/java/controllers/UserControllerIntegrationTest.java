package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import matt.sb.example.Application;
import matt.sb.example.entities.primary.UserRecord;
import matt.sb.example.repositories.primary.UserRecordRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRecordRepository userRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        userRecordRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRecordRepository.deleteAll();
    }

    @Test
    public void testGetUser_Exists() throws Exception {
        UserRecord record = new UserRecord(1, "first", "last");

        userRecordRepository.save(record);

        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users?id=1", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();

        UserRecord recordResult = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserRecord.class);

        assertEquals(1, recordResult.getUserId());
        assertEquals("first", recordResult.getFirstName());
        assertEquals("last", recordResult.getLastName());
    }

    @Test
    public void testGetUser_NotExists() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/users?id=1", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound());
    }
}
