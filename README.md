This is a template project to create a micro service in a standardized manner. 
================================================================================

Basic ideas for Maven dependency management, Spring framework (DI, Spring Boot, RestTemplate etc.), Netflix microservice concepts (Eureka, Ribbon, Hysterix) are kind of pre-requisites for delving into world of micro-services. Final deliverable of the command 'mvn clean install' on a template project (or any microservice based on it) would be a single jar file. This jar file would have all its dependencies embedded inside and when ran with a simple java command, it will start an embedded tomcat to expose all defined REST endpoints. This jar would go inside a docker image and that docker image can be installed on any OS. Please lookup Dockerization for more.


Typically, a usual microservice has an architecture like this:



This project demoes the following moving parts to be used for the development of any new micro service
* Self-Registration to a Eureka server
* Circuit breaker / Hystrix (/demoHysterix endpoint exhibit this)
* Spring Data with Hibernate (service makes DB calls for its CRUD operations)
* Redis client (fmsTemplateAccount caches account entity, endpoint /cacheable/owner/{name} uses spring annotations, /cachingWithoutAnnotation/owner/{name} does it without using any annotation to display both modes of communication with cache)
* Kafka client and consumer (service sends a message to kafka and also receives it using a listener. See /demoKafka endpoint)
* Audit using AOP - client IP, Method arguments, Response value, response time , trackId  (exhibits all these using LogDocTrackConfig class)
* Unit test case sample class AccountsControllerTest
