Federated micro service template
==================================

This is a template project to create a micro service in a standardized manner. Basic ideas for Maven dependency management, Spring framework (DI, Spring Boot, RestTemplate etc.), Netflix microservice concepts (Eureka, Ribbon, Hysterix) are kind of pre-requisites for delving into world of micro-services. Final deliverable of the command 'mvn clean install' on a template project (or any microservice based on it) would be a single jar file. This jar file would have all its dependencies embedded inside and when ran with a simple java command, it will start an embedded tomcat to expose all defined REST endpoints. This jar would go inside a docker image and that docker image can be installed on any OS. Please lookup Dockerization for more.


Typically, a usual microservice has an architecture like this:


Project features
-----------------
This project demoes the following moving parts to be used for the development of any new micro service
* Self-Registration to a Eureka server
* Circuit breaker / Hystrix (/demoHysterix endpoint exhibit this)
* Spring Data with Hibernate (service makes DB calls for its CRUD operations)
* Redis client (fmsTemplateAccount caches account entity, endpoint /cacheable/owner/{name} uses spring annotations, /cachingWithoutAnnotation/owner/{name} does it without using any annotation to display both modes of communication with cache)
* Kafka client and consumer (service sends a message to kafka and also receives it using a listener. See /demoKafka endpoint)
* Audit using AOP - client IP, Method arguments, Response value, response time , trackId  (exhibits all these using LogDocTrackConfig class)
* Unit test case sample class AccountsControllerTest

Project details
----------------
* *AccountsServer*, the starting point 
  * It starts the spring boot app.
  *	It starts an embedded tomcat server.
  *	Looks for accounts-server.properties file, in which we define app name, port number, eureka URL etc. 
  *	It enables
    * EurekaClient
    * CircuitBreaker (Hysterix)
    * Redis Caching
    * Database connectivity:
			  * Searches for Property file "classpath:database.properties"
			  * Looks for all the table entities through @EntityScan("com.org.fms.account.model")
			  * Looks for all the JPA repositories and queries etc. through @EnableJpaRepositories("com.org.fms.account.model")
	* @RestController, @Services, @Components etc. by scanning packages under "com.org.fms.account.*"
	* A REST template for making REST calls

* *AccountsController* 
  * Has a normal GET
  * Has a GET, PUT and DELETE to demo caching techniques
  * Has a GET to demo hysterix fallback mechanism
	
* *CacheDemoService* has service methods to demo caching techniques

* *HysterixDemoService* has service methods to demo hysterix fallback mechanism

* *Account* is the DB table equivalent

* *AccountRepository* class has all the DB queries embedded inside it, in the form of methods OR queries

* *CacheConfig* is used to configure Redis connection etc. It looks for redis.properties in the classpath

* *DBConfig* is used to configure Redis connection etc. It looks for sql-server.properties in the classpath

* *KafkaConfig* is used to configure Kafka 0.9.0.1 connection etc. It looks for kafka.properties in the classpath

* *LogDocTrackConfig* configures swagger documentation, enables incoming request tracking, creates performance logs and adds request track id in logs. It looks for swagger.properties in the classpath. 
		
* logback.xml defines how service logs should be created.

What to do when referencing this project
-----------------------------------------
* Copy project structure, pom.xml, LogDocTrackConfig, swagger.properties and logback.xml. Tweak swagger.properties and logback.xml as per your documentation and logging requirements. If Performance logs are required, change the value of PERF_LOG_SCOPE variable in LogDocTrackConfig class as per your performance logging needs.
* For Caching needs, copy CacheConfig and redis.properties. Tweak cache.properties for connection configuration. If Caching is not required, you may also remove dependency spring-boot-starter-redis from pom file
* For Database needs, copy DBConfig and sql-server.properties. Tweak sql-server.properties for connection configuration. Also change the package names in DBConfig class for @EntityScan and @EnableJpaRepositories annotations. If Database is not required, you may also remove dependencies (spring-data-commons,spring-boot-starter-data-jpa,jtds,c3p0) from pom file
* For Kafka needs, copy KafkaConfig and kafka.properties. If Kafka is not required, you may also remove dependencies (spring-kafka) from pom file
* Create Server , Controller, Service, Model, JPA classes and server bootup properties file on the same lines, naming conventions, annotations etc.
