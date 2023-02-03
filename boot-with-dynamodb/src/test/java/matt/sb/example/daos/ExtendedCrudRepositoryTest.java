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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class ExtendedCrudRepositoryTest {
    @Autowired
    private ExtendedCrudRepository extendedCrudRepository;

    @Autowired
    private SimpleCrudRepository simpleCrudRepository;

    private UserRecord userRecord;
    private UserRecord secondRecord;

    @BeforeEach
    public void setup() {
        userRecord = new UserRecord();
        userRecord.setPartitionKey("p1");
        userRecord.setRangeKey(1);
        userRecord.setFirstName("saul");
        userRecord.setLastName("goodman");
        userRecord.setAge(40);

        secondRecord = new UserRecord();
        secondRecord.setPartitionKey(userRecord.getPartitionKey());
        secondRecord.setRangeKey(2);
        secondRecord.setAge(30);
        secondRecord.setFirstName("paul");
        secondRecord.setLastName("badman");

        simpleCrudRepository.delete(userRecord);
        simpleCrudRepository.delete(secondRecord);
    }

    @Test
    public void testDeleteAll_NullCollection() {
        assertThrowsExactly(IllegalArgumentException.class, () -> extendedCrudRepository.deleteAll(null));
    }

    @Test
    public void testDeleteAll_NullRecord() {
        assertThrowsExactly(IllegalArgumentException.class, () -> extendedCrudRepository.deleteAll(Collections.singletonList(null)));
    }

    @Test
    public void testDeleteAll_RollbackOnFailure() {
        simpleCrudRepository.create(userRecord);

        assertThrowsExactly(DynamoDBMappingException.class, () -> extendedCrudRepository.deleteAll(Arrays.asList(userRecord, new UserRecord())));

        assertNotNull(simpleCrudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));
    }

    @Test
    public void testDeleteAll() {
        simpleCrudRepository.create(userRecord);

        assertNotNull(simpleCrudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));

        extendedCrudRepository.deleteAll(Collections.singletonList(userRecord));

        assertNull(simpleCrudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));
    }

    @Test
    public void testExists_InvalidPKey() {
        assertThrowsExactly(DynamoDBMappingException.class, () -> extendedCrudRepository.exists(null, userRecord.getRangeKey()));
    }

    @Test
    public void testExists_InvalidRKey() {
        assertThrowsExactly(DynamoDBMappingException.class, () -> extendedCrudRepository.exists(userRecord.getPartitionKey(), null));
    }

    @Test
    public void testExistsAfterCreate() {
        simpleCrudRepository.create(userRecord);
        assertTrue(extendedCrudRepository.exists(userRecord.getPartitionKey(), userRecord.getRangeKey()));
    }

    @Test
    public void testNotExistsAfterDelete() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.delete(userRecord);
        assertFalse(extendedCrudRepository.exists(userRecord.getPartitionKey(), userRecord.getRangeKey()));
    }

    @Test
    public void testFindAllByIds() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        UserRecord keyFind = new UserRecord();
        keyFind.setPartitionKey(userRecord.getPartitionKey());
        keyFind.setRangeKey(userRecord.getRangeKey());

        List<UserRecord> result = extendedCrudRepository.findAllByIds(Collections.singletonList(keyFind));
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testSaveAll() {
        extendedCrudRepository.saveAll(Arrays.asList(userRecord, secondRecord));

        assertNotNull(simpleCrudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));
        assertNotNull(simpleCrudRepository.read(secondRecord.getPartitionKey(), userRecord.getRangeKey()));
    }

    @Test
    public void testSaveAll_RollbackOnFailure() {
        assertThrowsExactly(DynamoDBMappingException.class, () -> extendedCrudRepository.saveAll(Arrays.asList(userRecord, secondRecord, new UserRecord())));

        assertNull(simpleCrudRepository.read(userRecord.getPartitionKey(), userRecord.getRangeKey()));
        assertNull(simpleCrudRepository.read(secondRecord.getPartitionKey(), userRecord.getRangeKey()));
    }
}
