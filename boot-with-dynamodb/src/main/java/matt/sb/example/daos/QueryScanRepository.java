package matt.sb.example.daos;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.extern.slf4j.Slf4j;
import matt.sb.example.models.UserRecord;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class QueryScanRepository {

    private final DynamoDBMapper dbMapper;

    public QueryScanRepository(DynamoDBMapper dbMapper) {
        this.dbMapper = dbMapper;
    }

    public List<UserRecord> queryByPartitionKey(String pKey) {
        return queryByPartitionKey(pKey, false);
    }

    /**
     * This method demonstrates query functionality based only on a single primary key.
     *
     * Queries and returns a list of records that match the specified partition key. The result list is lazily loaded
     * and will have only the first page of results in memory. Subsequent pages can be loaded by iterating through
     * the list.
     *
     * If the firstPageOnly parameter is set to true, additional results beyond the first page will not be loaded
     * even when iterating.
     */
    public List<UserRecord> queryByPartitionKey(String pKey, boolean firstPageOnly) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":p1", new AttributeValue().withS(pKey));

        DynamoDBQueryExpression<UserRecord> queryExpression = new DynamoDBQueryExpression<UserRecord>()
                .withKeyConditionExpression("pKey = :p1")
                .withExpressionAttributeValues(eav);

        if (firstPageOnly) {
            return dbMapper.queryPage(UserRecord.class, queryExpression).getResults();
        }
        else {
            return dbMapper.query(UserRecord.class, queryExpression);
        }
    }

    public List<UserRecord> queryRange(String pKey, Integer rKey) {
        return queryRange(pKey, rKey, false);
    }

    /**
     * This method demonstrates query functionality based on the partition key and a range of partition keys.
     *
     * Queries and returns a list of records that match the specified partition key and have a range key >=
     * the specified range key. The result list is lazily loaded and will have only the first page of results in memory.
     * Subsequent pages can be loaded by iterating through the list.
     *
     * If the firstPageOnly parameter is set to true, additional results beyond the first page will not be loaded
     * even when iterating.
     */
    public List<UserRecord> queryRange(String pKey, Integer rKey, boolean firstPageOnly) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":p1", new AttributeValue().withS(pKey));
        eav.put(":r1", new AttributeValue().withN(rKey.toString()));

        DynamoDBQueryExpression<UserRecord> queryExpression = new DynamoDBQueryExpression<UserRecord>()
                .withKeyConditionExpression("pKey = :p1 and rKey >= :r1")
                .withExpressionAttributeValues(eav);

        if (firstPageOnly) {
            return dbMapper.queryPage(UserRecord.class, queryExpression).getResults();
        }
        else {
            return dbMapper.query(UserRecord.class, queryExpression);
        }
    }

    public List<UserRecord> queryWithFilter(String pKey, Integer age) {
        return queryWithFilter(pKey, age, false);
    }
    /**
     * This method demonstrates query functionality based on the partition key and a non-key field.
     *
     * Queries and returns a list of records that match the specified partition key and have an "age" value in
     * the document >=. Any documents that do not have an 'age' field will automatically not be part of the result list.
     * The result list is lazily loaded and will have only the first page of results in memory. Subsequent pages can
     * be loaded by iterating through the list.
     *
     * If the firstPageOnly parameter is set to true, additional results beyond the first page will not be loaded
     * even when iterating.
     */
    public List<UserRecord> queryWithFilter(String pKey, Integer age, boolean firstPageOnly) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":p1", new AttributeValue().withS(pKey));
        eav.put(":f1", new AttributeValue().withN(age.toString()));

        DynamoDBQueryExpression<UserRecord> queryExpression = new DynamoDBQueryExpression<UserRecord>()
                .withKeyConditionExpression("pKey = :p1")
                .withFilterExpression("age >= :f1")
                .withExpressionAttributeValues(eav);

        if (firstPageOnly) {
            return dbMapper.queryPage(UserRecord.class, queryExpression).getResults();
        }
        else {
            return dbMapper.query(UserRecord.class, queryExpression);
        }
    }

    /**
     * This method demonstrates scan functionality based on a non-key field. Note that scans check the entire database
     * and can be quite expensive if the field being queried is not part of an index, or if the database contains a lot
     * of rows.
     *
     * Queries and returns a list of records that have an "age" value in the document >= the specified age. Any
     * documents that do not have an 'age' field will automatically not be part of the result list. The result list is
     * lazily loaded and will have only the first page of results in memory. Subsequent pages can be loaded by iterating
     * through the list.
     *
     * If the firstPageOnly parameter is set to true, additional results beyond the first page will not be loaded
     * even when iterating.
     */
    public List<UserRecord> scanWithFilter(Integer age) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":f1", new AttributeValue().withN(age.toString()));

        DynamoDBScanExpression queryExpression = new DynamoDBScanExpression()
                .withFilterExpression("age >= :f1")
                .withExpressionAttributeValues(eav);

        return dbMapper.scan(UserRecord.class, queryExpression);
    }
}
