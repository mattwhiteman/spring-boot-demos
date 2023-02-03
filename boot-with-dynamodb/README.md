# boot-with-dynamodb-v1

This application demonstrates how to utilize dynamodb in a spring boot application. V1 of the dynamo SDK is used, V2 will
be presented in a future separate demo application. V1 utilizes the DynamoDBMapper class for all transactions.

This documentation will not attempt to rehash all the basics of Dynamo such as keys, indexes, etc. The AWS documentation
is much more thorough and can be found at `https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/welcome.html`

This application requires a local-running instance of dynamoDB with an already-created table called "my_db". Connection
information, including the endpoint url, AWS secret key, and AWS client key should be provided in the application.properties
file under the following properties. If these values are not present or the local database is not running, the application
will fail to start or run its tests:
- amazon.dynamodb.endpoint
- amazon.aws.accesskey
- amazon.aws.secretkey

Dynamo supports a lot of different filters and parameters when performing operations. This application will not attempt to
demonstrate every single type of operation, but instead focus on some of the basics. The AWS SDK should be consulted for
more complex and indepth examples and documentation about specific query/scan parameters.

To reduce the amount of distracting code, this application purposely does not have any controllers or services that one
would find in a normal Spring Boot REST application and instead focuses on tests for the DAOs. It is assumed the reader
is familiar with Spring Boot and the typical MVC structure of REST apis.

The DAOs and tests have been split into 3 main groups for demonstration:

## CRUD (Create, Read, Update, Delete)
The SimpleCrudRepository demonstrates the 4 basic functions of a crud repository as they relate to dynamo.

## Advanced CRUD operations 
The ExtendedCrudRepository demonstrates functionality that is found in typical Spring JPA repositories, including
multi-delete, multi-save, checking if a record exists, and querying by a list of ids. Some operations have been
purposely left out, such as count() and findAll(). These operations are typically considered bad practice on a dynamo
DB as they require a scan of the full database and loading all results in memory.

The multi-save and multi-delete are transactional, meaning if an error occurs during the operation, any changes to
the database are rolled back (all-or-nothing operation). Note that more complex and mixed-type transactions are
possible, such as save+delete. The AWS documentation and SDK javadocs should be consulted for more information.

## Query operations
The QueryScanRepository has simple examples of using queries and scans. Queries always involve one or both of the
partition and range keys in addition to any other parameters or filters, and are usually the most effective way
to search for documents. Scans can be expensive and must check the entire database, but are the only way of doing
a search using a field that is not a key or part of an index. Queries and scans support numerous different parameters,
filters, and options and are too numerous to list here.  The AWS documentation and SDK javadocs should be consulted 
for more information.

### Important:
Standard Hibernate ORM libraries are intentionally not used here. There is currently no good and maintained solution that
provides the necessary driver implementation to allow standard hibernate classes and annotations to work with dynamo. Some older
libraries can be found in maven central, but they are not maintained and do not have the full functionality of the SDK
provided by Amazon.

To build this project and run the unit/integration tests, use `mvn clean package`.