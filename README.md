# Micro-Services

M1 -> M2 -> M3 -> M4 -> M5

	Challenges with Microservices:
	
 	Bounded Context: Establing right boundaries on which terms?
	Configuration Managements: tons of configuration (5 services and 10 instances = 50 services currently running)
	Dynamic Scale up and Scale Down: Establing technology to do that, distributing the load
	Visibilty: To check where the bug is? Monitoring to check which services are down?
	Pack of cards: Building one on top of the other, one can take down the entire pack, fault tolerrance is needed
	
Spring Cloud (Netflix) solution for these challenges:
	
 	Configuration: Spring Cloud Config Server, stores the configuration of different environments in the GIT repository and expose that configuration to different services.
	
 	Dynamic Scale up and Scale Down:
		Naming Server: Eureka
		Ribbon: Client Side Load Balancing
		Feign: Easier REST Client
	
	Visibilty & Monitoring:
		Zipkin Distributed Tracing: Use spring cloud cloud to assign an id to the request across multi components and use zipkin to trace across multi components
		Netflix API Gateway: Zuul
	
 	Fault Tolerrance:
		Hystrix

Advantages of Microservices Architecture:
	
 	New Technology and Process Adaptation: Can be built in different languages
	Dynamic Scaling: Scale up and Scale down based on the load
	Faster Release Cycles: New feature faster in market than monolithic application
	
Standarding ports and urls as many application will be running

Microservices update post Spring 2.5:
	
 	Spring Cloud LoadBalancer instead of Ribbon
	Spring Cloud Gateway instead of Zuul
	Resilience4j instead of Hystrix
	
If we've this dependency added in our project: spring-cloud-starter-config
	
 	We need to add this property in the application.properties: (Earlier there used to be a bootstarp.properties for this configserver, which was deprecated in the later releases)
		spring.config.import=optional:configserver:http://localhost:8888
		(This is a proper way to tell you Spring Boot app that you want to load properties from the Spring Cloud Config service that is running on localhost:8888)

spring-cloud-config-server:
	
 	cloud-config-server application:
		Create a git repo for the properties file.
		Create a property file in the git repo, for example: limits-service.properities
		Provide the below property with the path of git repo in the application.properties:
			spring.cloud.config.server.git.uri=file:///F:/microservices/git-localconfig-repo
	 
	How to get these properities from this cloud-config-server application in a different application:
		The application name and the properties name should be same, in our case application name should be: limits-service
		Spring will hit the url provided in the property "spring.config.import" with the application name and a profile (default)
		Example: http://localhost:8888/limits-service/default.
		The properities fetched from the cloud-config-server will have higher priority.
	
	How can I have different profiles for dev and qa in the cloud-config-server:
		We can create different properities files with -<profile-name> 
		And we can use the "spring.cloud.config.profile" to provide the profile name that will be used while getting properities from the config-server
		If we provide the specific <profile> the properities of both <profile> and default will be fetched, with the <profile> having higher priority
		Similarly if we don't want to provide the application name or want to give some other name while fetching the properities we can use "spring.cloud.config.name"

To run a same application on different port:
	
 	We can edit the configurations and provide the -Dserver.port=8001 in the VM Options.
	
data.sql file in the resources is executed by the jpa to load the data:
	
 	By default this is executed before tables are created, to prevent that we can use below property:
		spring.jpa.defer-datasource-initialization=true
		
Feign Client:
	
 	Create an proxy interface to consume the REST service
		@FeignClient is used for the interface:
			name: is used for the service discovery
			url: will the be hosted url of that service
	 
	Provides a implementation of the interface at runtime
		Uses similar methods like HTTP methods. (@PathVariable to provide the path variables)
	
	@EnableFeignClient is used to enable it
	
Naming Server:
	
 	The naming server is used to provide the service instances that are up by using the service discovery
	It load balances the multiple instances of a service that are running
	The services are registered by some name that is used by the Feign client
 
 	Eureka Server:
		spring-cloud-starter-netflix-eureka-server : This dependency will look for the eureka clients
		@EnableEurekaServer:
			Properties need to be set so that eureka don't register itself:
				eureka.client.register-with-eureka=false (blocks the registery)
				eureka.client.fetch-registry=false	(blocks the fetching of the information about other services registered on eureka server)
			
	Eureka Client:
		spring-cloud-starter-netflix-eureka-client : This dependency will automatically register the application url to the eureka server


How to handle the cross-cutting concerns like logging, monitoring, authentication, authorization in the microservices?
	
 	API Gateway: (Zuul older one, provided by Netflix)
	To enable the service discovery from API gateway using the eureka client:
		spring.cloud.gateway.discovery.locator.enabled=true
		http://localhost:8765/CURRENCY-EXCHANGE/currency-exchange/from/USD/to/INR
	
	To make the use of the URI in lower case that corresponds to the eureka registered name:
		spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true
		http://localhost:8765/currency-exchange/currency-exchange/from/USD/to/INR
	
	To create custom routes:
		We can create some routes to route the request via the loadbalancer
			builder.routes()
                .route(p -> p.path("/currency-exchange/**")
                        .uri("lb://currency-exchange"))
			This will re-route all the requests with "currency-exchange" to the loadbalancer
	 
			So our end result URI will be:
				http://localhost:8765/currency-exchange/from/USD/to/INR
		
			This helps us get rid of the double names in the URI.
			Also there is no need to use the service discovery in this case. So we can disable both the properities that we defined above
			
  Advantages of Spring API Gateway:
		
		Simple, yet effective way to route to APIs
		Provide cross cutting concerns:
			Securitey, Monitoring, Logging
		Build on top of Spring WebFlux (Reactive approach (Scalable & Responsive))
		Features:
			Match routes based on any request attribute
			Define predicates & filters
			Integrates with spring cloud discovery client (load balancing)
			Path rewriting
			Provide proxy to the services (provides gateway handler mapping along with filters for both request and response)
			
Remember the Spring DI is aggregation.

Circuit Breaker:
	
 	M1 --> M2 --> M3 -x-> M4 --> M5
	
	What if one of the services is down or is slow (in this case M4)
		That impacts the entire chain.
	Questions:
		Can we return a fallback response if a service is down?
		Can we implement a circuit breaker pattern to reduce load?
		Can we retry requests in case of temporary failures?
		Can we implemet rate limiting?
	Solution:
		Circuit Breaker Framework - Resilience4j
		Resilience4j is lightweight, easy-to-use fault tolerrance library inspired by Netflix Hystrix, but designed for Java 8 and functional programming
	
	Resilience4J:
	  
	dependencies required:
		io.github.resilience4j
			resilience4j-spring-boot2
		org.springframework.boot
			spring-boot-starter-aop
    
	@Retry
		This will retry the function 3 times by default before returning any error
		To provide custom retry counter:
			@Retry(name = "sample-api")
			resilience4j.retry.instances.sample-api.maxAttempts=5
	  
	Fallback:
		@Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
		hardcodedResponse is a method which should have a parameter of type Throwable and it will be called after the retries
		To configure a duration in between retries:
			resilience4j.retry.instances.sample-api.waitDuration=1s
		To configure the duration exponentially:
			resilience4j.retry.instances.sample-api.enableExponentialBackOff=true
		
	@CircuitBreaker:
		Similiar to retry in terms of annotation
			@CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
		CircuitBreaker states:
				Open
				Closed
				Half-open
		Transition between states:
			Open --threshold--> Closed
			Closed <--threshold is checked--> Half-open
			Half-open --when threshold is crossed--> Open
		
		When Open: all the request goes to the individual service
		When Closed: all the request will get the deafult fallback method response
		At a certain configurable point of time it checks if the service is up again
		If certain threshold is achieved, then the state becomes Half-open and it'll let some requests to go the service, if not it goes back to Closed state.
		When Half-open: it'll check if the threshold is crossed then the state becomes Open again.
	 
		failureRateThreshold: to configure the threshold.
		
	@RateLimiter:
		Rate Limitting is to configure a time duration and in that time duration only specific number of calls can be made to a service.
			resilience4j.ratelimiter.instances.default.limitForPeriod=2
			resilience4j.ratelimiter.instances.default.limitRefreshPeriod=10s
		
	@Bulkhead:
		This is annotation is used to configure the concurrent calls to a service.
			resilience4j.bulkhead.instances.default.maxConcurrentCalls=10
