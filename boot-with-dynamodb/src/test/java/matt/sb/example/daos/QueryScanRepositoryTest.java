package matt.sb.example.daos;

import matt.sb.example.Application;
import matt.sb.example.models.UserRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
public class QueryScanRepositoryTest {

    @Autowired
    private SimpleCrudRepository simpleCrudRepository;

    @Autowired
    private QueryScanRepository queryScanRepository;

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
    public void testQueryByPKey() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryByPartitionKey(userRecord.getPartitionKey());
        assertEquals(2, result.size());
        assertEquals(userRecord.getFirstName(), result.get(0).getFirstName());
        assertEquals(secondRecord.getFirstName(), result.get(1).getFirstName());
    }

    @Test
    public void testQueryByPKey_NoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryByPartitionKey("p2");
        assertEquals(0, result.size());
    }

    @Test
    public void testQueryRange_OneResult() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryRange(secondRecord.getPartitionKey(), secondRecord.getRangeKey());
        assertEquals(1, result.size());
        assertEquals(secondRecord.getFirstName(), result.get(0).getFirstName());
    }

    @Test
    public void testQueryRange_TwoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryRange(secondRecord.getPartitionKey(), 0);
        assertEquals(2, result.size());
        assertEquals(userRecord.getFirstName(), result.get(0).getFirstName());
        assertEquals(secondRecord.getFirstName(), result.get(1).getFirstName());
    }

    @Test
    public void testQueryRange_NoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryRange(secondRecord.getPartitionKey(), 5);
        assertEquals(0, result.size());
    }

    @Test
    public void testQueryWithFilter_OneResult() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryWithFilter(userRecord.getPartitionKey(), secondRecord.getAge()+5);
        assertEquals(1, result.size());
        assertEquals(userRecord.getFirstName(), result.get(0).getFirstName());
    }

    @Test
    public void testQueryWithFilter_TwoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryWithFilter(secondRecord.getPartitionKey(), secondRecord.getAge());
        assertEquals(2, result.size());
        assertEquals(userRecord.getFirstName(), result.get(0).getFirstName());
        assertEquals(secondRecord.getFirstName(), result.get(1).getFirstName());
    }

    @Test
    public void testQueryWithFilter_NoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.queryWithFilter(userRecord.getPartitionKey(), userRecord.getAge()+5);
        assertEquals(0, result.size());
    }

    @Test
    public void testScanWithFilter_OneResult() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.scanWithFilter(secondRecord.getAge()+5);
        assertEquals(1, result.size());
        assertEquals(userRecord.getFirstName(), result.get(0).getFirstName());
    }

    @Test
    public void testScanWithFilter_TwoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.scanWithFilter(secondRecord.getAge());
        assertEquals(2, result.size());
        assertEquals(userRecord.getFirstName(), result.get(0).getFirstName());
        assertEquals(secondRecord.getFirstName(), result.get(1).getFirstName());
    }

    @Test
    public void testScanWithFilter_NoResults() {
        simpleCrudRepository.create(userRecord);
        simpleCrudRepository.create(secondRecord);

        List<UserRecord> result = queryScanRepository.scanWithFilter(userRecord.getAge()+5);
        assertEquals(0, result.size());
    }
}
