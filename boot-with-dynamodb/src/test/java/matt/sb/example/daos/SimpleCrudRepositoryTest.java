package matt.sb.example.daos;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import matt.sb.example.Application;
import matt.sb.example.models.UserRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class SimpleCrudRepositoryTest {

    @Autowired
    private SimpleCrudRepository crudRepository;

    private UserRecord userRecord;

    @BeforeEach
    public void setup() {
        userRecord = new UserRecord();
        userRecord.setPartitionKey("p1");
        userRecord.setRangeKey(1);
        userRecord.setFirstName("saul");
        userRecord.setLastName("goodman");
        userRecord.setAge(40);

        crudRepository.delete(userRecord);
    }

    @Test
    public void testCrud_CreateNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> crudRepository.create(null));
    }

    @Test
    public void testCrud_CreateInvalidPKey() {
        userRecord.setPartitionKey(null);
        assertThrowsExactly(DynamoDBMappingException.class, () -> crudRepository.create(userRecord));
    }

    @Test
    public void testCrud_CreateInvalidRKey() {
        userRecord.setRangeKey(null);
        assertThrowsExactly(DynamoDBMappingException.class, () -> crudRepository.create(userRecord));
    }

    @Test
    public void testCrud_Create() {
        crudRepository.create(userRecord);

        UserRecord result = crudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey());
        assertNotNull(result);
        assertEquals(userRecord.getPartitionKey(), result.getPartitionKey());
        assertEquals(userRecord.getRangeKey(), result.getRangeKey());
        assertEquals(userRecord.getFirstName(), result.getFirstName());
        assertEquals(userRecord.getLastName(), result.getLastName());
        assertEquals(userRecord.getAge(), result.getAge());
    }

    @Test
    public void testCrud_ReadNotExists() {
        UserRecord result = crudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey());
        assertNull(result);
    }

    @Test
    public void testCrud_UpdateNull() {
        crudRepository.create(userRecord);
        assertThrowsExactly(IllegalArgumentException.class, () -> crudRepository.update(null));
    }

    @Test
    public void testCrud_UpdateInvalidPKey() {
        crudRepository.create(userRecord);
        userRecord.setPartitionKey(null);
        assertThrowsExactly(DynamoDBMappingException.class, () -> crudRepository.update(userRecord));
    }

    @Test
    public void testCrud_UpdateInvalidRKey() {
        crudRepository.create(userRecord);
        userRecord.setRangeKey(null);
        assertThrowsExactly(DynamoDBMappingException.class, () -> crudRepository.update(userRecord));
    }

    @Test
    public void testCrud_UpdateRecordNotExists() {
        userRecord.setFirstName("paul");
        userRecord.setAge(null);
        userRecord.setLastName(null);

        crudRepository.update(userRecord);
        UserRecord result = crudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey());
        assertNotNull(result);
        assertEquals(userRecord.getPartitionKey(), result.getPartitionKey());
        assertEquals(userRecord.getRangeKey(), result.getRangeKey());
        assertEquals("paul", result.getFirstName());
        assertNull(result.getLastName());
        assertNull(result.getAge());
    }

    @Test
    public void testCrud_Update() {
        crudRepository.create(userRecord);

        userRecord.setFirstName("paul");
        userRecord.setAge(null);
        userRecord.setLastName(null);

        crudRepository.update(userRecord);
        UserRecord result = crudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey());
        assertNotNull(result);
        assertEquals(userRecord.getPartitionKey(), result.getPartitionKey());
        assertEquals(userRecord.getRangeKey(), result.getRangeKey());
        assertEquals("paul", result.getFirstName());
        assertEquals("goodman", result.getLastName());
        assertEquals(40, result.getAge());
    }

    @Test
    public void testCrud_DeleteNull() {
        assertThrowsExactly(IllegalArgumentException.class, () -> crudRepository.delete(null));
    }

    @Test
    public void testCrud_DeleteInvalidPKey() {
        crudRepository.create(userRecord);
        userRecord.setPartitionKey(null);
        assertThrowsExactly(DynamoDBMappingException.class, () -> crudRepository.delete(userRecord));
    }

    @Test
    public void testCrud_DeleteInvalidRKey() {
        crudRepository.create(userRecord);
        userRecord.setRangeKey(null);
        assertThrowsExactly(DynamoDBMappingException.class, () -> crudRepository.delete(userRecord));
    }

    @Test
    public void testCrud_Delete() {
        crudRepository.create(userRecord);

        assertNotNull(crudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));

        crudRepository.delete(userRecord);

        assertNull(crudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));
    }
}
