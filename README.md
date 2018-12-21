# Transaction-statistic

A Java RESTful API for calculating realtime statistics for the last 60 seconds of transactions

### Technologies
- Made use of Java 8
- Spring boot
- Maven
- lombok
- swagger for documenting rest services
- net.jodah.expiringmap (A high performance thread-safe map that expires entries) for holding and removing transactions


### How to install
```mvn clean install```

### How to run
```mvn spring-boot:run```


### Design Notes
* Valid transactions ( Transactions that has a timestamp value within the last minute for the time zone UTC ) are put into a
thread-safe net.jodah.expiringmap. Their expiring times are given individually by calculating how much time has left for the transaction to expire by looking at their timestamp values. Statistic is also updated. Saving a transaction takes O(1) time. The transaction whose expiring time has passed is automatically removed from the map. After removal, an event is triggered and the removed transaction's values are extracted from the Statistic. If the removed transaction's amount is not the maximum or the minimum amount among other transactions in the map, then this update operation also takes O(1) time without traversing the map.

* When getting a statistic for the last minute, the already calculated Statistic is returned, thus taking O(1) time.

* By default, statistics are calculated for the last minute. But this can be changed through changing the 'cache.time.in.milliseconds' property in application.properties file.  


### Available Services

* Swagger is used to document the rest services. You can find the documentation at the link: http://localhost:8080/swagger-ui.html
