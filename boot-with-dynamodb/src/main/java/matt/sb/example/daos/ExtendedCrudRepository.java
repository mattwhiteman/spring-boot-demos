package matt.sb.example.daos;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.TransactionWriteRequest;
import lombok.extern.slf4j.Slf4j;
import matt.sb.example.models.UserRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExtendedCrudRepository {
    protected final DynamoDBMapper dbMapper;
    public ExtendedCrudRepository(DynamoDBMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    /**
     * Deletes all records from the database that match the list of specified ids. If the list is null,
     * or any record within the list is null, this method will throw an IllegalArgumentException.
     * This operation is transactional, if any error occur while deleting multiple records, it will
     * rollback all the deletes.
     * @param records
     */
    public void deleteAll(Iterable<UserRecord> records) {
        if (records == null) {
            log.error("Attempted to delete a null list of records");
            throw new IllegalArgumentException("Collection of records to delete cannot be null");
        }
        else {
            TransactionWriteRequest transactionWriteRequest = new TransactionWriteRequest();
            records.forEach(userRecord -> {
                if (userRecord == null) {
                    log.error("Attempted to delete null user record during a deleteAll");
                    throw new IllegalArgumentException("Attempted to delete a null record during a deleteAll");
                }
                else {
                    transactionWriteRequest.addDelete(userRecord);
                }
            });
            dbMapper.transactionWrite(transactionWriteRequest);
        }
    }

    /**
     * Checks if the record with the specified keys exists in the database and returns true/false. If any
     * of the parameter keys are null, this method will throw a DynamoDBMappingException
     */
    public boolean exists(String pKey, Integer rKey) {
        return dbMapper.load(UserRecord.class, pKey, rKey) != null;
    }

    /**
     * Returns a list of all records that match the ids specified in the parameter list.
     */
    public List<UserRecord> findAllByIds(Iterable<UserRecord> recordIds) {
        return dbMapper.batchLoad(recordIds).get("my_db")
                .stream().map(e -> (UserRecord)e).collect(Collectors.toList());
    }

    /**
     * Saves all records in the specified parameter list. This method will throw an IllegalArgumentException if
     * this list is null or contains a null record. This method is transactional, so if any errors occur while
     * saving any of the records, all the saves will be rolled back.
     */
    public Iterable<UserRecord> saveAll(Iterable<UserRecord> records) {
        if (records == null) {
            log.error("Attempted to save null record list");
            throw new IllegalArgumentException("Record list to save cannot be null");
        }
        else {
            TransactionWriteRequest transactionWriteRequest = new TransactionWriteRequest();
            records.forEach(userRecord -> {
                if (userRecord == null) {
                    log.error("Attempted to save null user record");
                    throw new IllegalArgumentException("Record to save cannot be nul");
                }
                else {
                    transactionWriteRequest.addPut(userRecord);
                }
            });
            dbMapper.transactionWrite(transactionWriteRequest);
        }

        return records;
    }
}
