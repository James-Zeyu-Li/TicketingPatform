# TicketingPatform

The project implements a simpler version of the ticket purchasing program using Springboot, Redis, and Kafka. The test will be mainly concurrent testing and push the limit of concurrent ticket purchasing with intentionally designed seat conflicts. 

The most important part will be the procedure of purchasing consumer tickets, but a simple and probably fixed Venue and Event will be implemented to simulate the ideal functionality that when users click different events and check different venues, the zones, tows, and columns will be changed accordingly. 

Two databases will be utilized as a CQRS format, a noSQL database which quickly saves the information to enable quick writing to potentially increase ticket purchasing speed. And a relational SQL database that provides query availabilities for backend or user query. DynamoDB will be used for the noSQL database, and MySQL is being used for SQL database.

The usage of Redis, in my current implementation, I am implementing a bitmap inside Redis to keep track of all seats according to event, venue, and zone. The bitmap is the smallest unit and the most detailed tracking, using a bit map to make sure it uses the smallest possible memory.  The redis memory will also hold a counter for zone capacity and a counter of available seats in a row to quickly reflect if a purchase and quickly reject requests if the zone is full.

The usage of Kafka is ideally providing 2 functions, the first one is to quickly and asynchronously save the ticket information and return the confirmation information to the consumer to avoid blocked waiting. The second function will be providing a message queue, which could be requesting information from the mySQL database to sync the information into the SQL database to provide more complex query requests.

Please see below the current Structure of the project:

```
.
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── org
│   │   │       └── java
│   │   │           └── ticketingplatform
│   │   │               ├── TicketingPlatformApplication.java
│   │   │               ├── component
│   │   │               │   └── DTO
│   │   │               │       ├── TicketInfoDTO.java
│   │   │               │       └── TicketRespondDTO.java
│   │   │               ├── config
│   │   │               │   ├── DynamoDbConfig.java
│   │   │               │   ├── KafkaConfig.java
│   │   │               │   └── RedisConfig.java
│   │   │               ├── controller
│   │   │               │   └── TicketPurchaseController.java
│   │   │               ├── exception
│   │   │               │   └── GeneralExceptionHandler.java
│   │   │               ├── mapper
│   │   │               │   └── TicketMapper.java
│   │   │               ├── model
│   │   │               │   ├── ErrorMessage.java
│   │   │               │   ├── Event.java
│   │   │               │   ├── TicketCreation.java
│   │   │               │   ├── TicketInfo.java
│   │   │               │   ├── TicketResponse.java
│   │   │               │   ├── Venue.java
│   │   │               │   └── Zone.java
│   │   │               ├── repository
│   │   │               │   ├── TicketDAO.java
│   │   │               │   ├── TicketDAOInterface.java
│   │   │               │   └── TicketSync.java
│   │   │               ├── service
│   │   │               │   ├── SeatOccupiedService.java
│   │   │               │   ├── TicketService.java
│   │   │               │   └── VenueConfigService.java
│   │   │               └── serviceInterface
│   │   │                   └── TicketServiceInterface.java
│   │   └── resources
│   │       ├── application.yml
│   │       ├── schema.sql
│   │       ├── static
│   │       └── templates
```
