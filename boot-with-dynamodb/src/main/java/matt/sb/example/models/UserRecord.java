package matt.sb.example.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@DynamoDBTable(tableName="my_db")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRecord {
    @DynamoDBHashKey(attributeName = "pKey")
    private String partitionKey;

    @DynamoDBRangeKey(attributeName = "rKey")
    private Integer rangeKey;

    @DynamoDBAttribute(attributeName = "firstName")
    private String firstName;

    @DynamoDBAttribute(attributeName = "lastName")
    private String lastName;

    @DynamoDBAttribute(attributeName = "age")
    private Integer age;
}
