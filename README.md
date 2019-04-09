# Core Service Framework

## Overview

Based on latest Spring Cloud version, `Core Service Framework` is a collection of tools and code snippets to quickly implement micro-service architecture.
Feel free to use it as the start of your project or copy/paste what you like from this repo.

License: Apache License 2.0

## Development

### Code Standard

* Using Unix LF (\n)
* Java code format by Google Code Format (no "import ...*")

### Runtime Environment

* Java SDK 10 or 11
* Preferred charset: `UTF-8`

### IDE

* IntelliJ IDEA (Lombok, Google-Code-format, GenSerializeID)

## Related Framework, Technology and Tools

* Spring Framework Core (Context, Bean)
* Spring Web MVC
* Spring Reactor (Mono, Flux)
* Spring WebFlux
* Spring Boot (Config, BootPackage)
* Spring Security (on MVC and WebFlux)
* Spring Cloud (Feign, Config, Eureka, Sleuth)
* Spring Boot Test and JUnit
* JPA 2.2 and Spring Data JPA
* Swagger, Jackson JSON Mapper
* Gradle
* git, curl
* MySQL, Redis, RabbitMQ
* Maven repository

## Modules Reference

### Generate dependency graph for core-services (via DOT)

![project-dependencies](./project-dependencies.png)

Refer to `geenerate-dep.sh` under `tools`.

### Config server

Run `core-service-config-server` with overriding following properties:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: file://${user.home}/projects/config-repo
          refreshRate: 10
```

### Config client

Add `core-service-config-support` as dependency with overriding following properties:

```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888
```

### Eureka Discovery Server

#### Standalone Mode

java -jar build/libs/core-service-discovery-server-1.0.0.RELEASE-boot.jar

#### Peer Mode

Examples:

Peer #1:

```sh
SPRING_PROFILES_ACTIVE=peer SERVER_PORT=8761 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8762/eureka/ java -jar build/libs/core-service-discovery-server-1.0.0.RELEASE-boot.jar
```

Peer #2:

```sh
SPRING_PROFILES_ACTIVE=peer SERVER_PORT=8762 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ java -jar build/libs/core-service-discovery-server-1.0.0.RELEASE-boot.jar
```

### Service Discovery Support (Eureka Client)

Using `core-service-discovery-support` as dependency with overriding the following properties:

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

Environment variable can be set to disable service discovery:

```bash
EUREKA_CLIENT_ENABLED=false java -jar debug-service.jar
```

### Cache

`Redis` is default cache provider with dependency `core-service-cache-support`:

```yaml
spring.cache.type: redis
```

and overriding following properties:

```yaml
spring:
  redis:
    url: redis://redis:alice-secret@192.168.99.100:6379
```

Java code for spring-cache:

```java
@Cacheable("piDecimals")
String getPi(int i) {
  // ...
}
```

> NOTE:
>
> According to `org.springframework.boot.autoconfigure.cache.RedisCacheManager`, the default configuration is using JDK serializer.
That means the returning object that is thrown into cache should implement `java.io.Serializable`.

### Web Request Validation

`@Validated` / `@Valid` on RequestDocument class with constraints like `@Size(min = 8, max = 10)` or `@NotNull`

Validation error processing is implemented in web support module. Error response could be like:

```json
{
  "status":"error",
  "code":"900001",
  "message":"REQUEST_VALIDATION_ERROR",
  "data":"Validation error on field: password",
  "timestamp":"2019-03-29T15:33:44.462+0000",
  "path":"/api/users"
}
```

### File uploading

```java
@PostMapping("/users/{userId}/portrait")
public Map<String, Object> uploadUserPortrait(@PathVariable String userId, @RequestParam("portrait") MultipartFile file) {
  // ...
}
```

> NOTE:
>
> Http header `Expect: 100-continue` is not supported when a multipart request passes through the gateway server due to a bug in Spring Cloud Gateway.
> However, sending request directly to a service works properly.
> Most browsers don't use that header.

## Todo for first release

1. User service as authentication service. Change repo name.

17. Eureka register testing

eureka.instance.leaseRenewalIntervalInSeconds

BUG: slow to clean old instance. still querying old instances even it is down

2. Order service call user-service

2. Gateway @RefreshScope with Gateway server configuration dynamically

1. Spring Cloud Sleuth + Zipkin dashboard integration

0. Spring Cloud Stream

0. Spring Cloud Bus

0. Job trigger server

4. Publish to maven or jcenter

18. Documentation for modules introduced (Discovery client support & Disable, /h2-console)

## Roadmap Points

TL;DR (commented, only shown in source file)

<!--

30. JWT authentication

31. Flyway

1. Gateway Feature

  Response-to-Session Mapping

  Rewrite URL by Session Key

  Request Parameters rewrite by UID

  Request Body rewrite by UID

  JWT token

  Dangerous header removing: X-Forwarded-For
  
  Gateway request limit (redis)

29. OAuth2 for WX

17. Saga

spring state machine

State (name, entering, do, getDoingStatus, undo, getUndoingStatus, exiting)
configuration-items: async query interval 

(DONE, ASYNC_STATUS, ABORT, REVERT, REVIEW_REQUIRED)

15. Nginx default configuration

3. spring-boot-starter-data-mongodb with MongoTemplate

spring.data.mongodb.uri=mongodb://user:secret@mongo1.example.com:12345,mongo2.example.com:23456/test

1. Polyglot support with Sidecar

Zuul sidecar, Discovery Service proxy

47. @EnableAsync @Async

spring.task.execution.pool.max-threads=16
spring.task.execution.pool.queue-capacity=100
spring.task.execution.pool.keep-alive=10s

0. Configserver Encrypt

```
# curl localhost:8888/encrypt -d mysecret
# curl localhost:8888/decrypt -d 682bc583f4641835fa2db009355293665d2647dade3375c0ee201de2a49f7bda
```

{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ

1. Java Key management

keytool -genkeypair -alias mytestkey -keyalg RSA \
  -dname "CN=Web Server,OU=Unit,O=Organization,L=City,S=State,C=US" \
  -keypass changeme -keystore server.jks -storepass letmein
  
encrypt:
  keyStore:
    location: classpath:/server.jks
    password: letmein
    alias: mytestkey
    secret: changeme
    
2. Config server - plain resource

	@RequestMapping("/{name}/{profile}/{label}/**")
	public String retrieve(@PathVariable String name, @PathVariable String profile,
			@PathVariable String label, ServletWebRequest request,
			@RequestParam(defaultValue = "true") boolean resolvePlaceholders)
			throws IOException {
		String path = getFilePath(request, name, profile, label);
		return retrieve(request, name, profile, label, path, resolvePlaceholders);
	}

1. RabbitMQ integration (AmqpTemplate / AmqpAdmin)

spring-boot-starter-amqp

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=secret
spring.rabbitmq.template.retry.enabled=true
spring.rabbitmq.template.retry.initial-interval=2s

@RabbitListener(queues = "someQueue") // containerFactory="myFactory"
public void processMessage(String content) {

4. WebSocket: spring-boot-starter-websocket

5. 20.5.1 Running the Remote Client Application

6. META-INF/spring.factories
org.springframework.context.ApplicationListener=com.example.project.MyListener

7. SpringApplication.setWebApplicationType(WebApplicationType.NONE)

8. @Autowired public MyBean(ApplicationArguments args)
implements CommandLineRunner

9. ExitCodeGenerator

10. MBean: spring.application.admin.enabled=true

11. Property placeholder
```
my.secret=${random.value}
my.number=${random.int}
my.bignumber=${random.long}
my.uuid=${random.uuid}
my.number.less.than.ten=${random.int(10)}
my.number.in.range=${random.int[1024,65536]}
```

12. Properties

SpringApplication loads properties from application.properties files in the following locations and adds them to the Spring Environment:

A /config subdirectory of the current directory
The current directory
A classpath /config package
The classpath root

$ java -jar myproject.jar --spring.config.location=classpath:/default.properties,classpath:/override.properties

13. @ConfigurationProperties(prefix="my")

my:
  servers:
	- dev.example.com
	- another.example.com

@Configuration
@EnableConfigurationProperties(AcmeProperties.class)

vs

@Component
@ConfigurationProperties(prefix="acme")
on Class or on Bean()

15. YAML
acme:
  map:
    "[/key1]": value1
    "[/key2]": value2
    /key3: value3

16. Duration in configurationProperties (@DurationUnit(ChronoUnit.SECONDS))

You can also use any of the supported units. These are:

ns for nanoseconds
us for microseconds
ms for milliseconds
s for seconds
m for minutes
h for hours
d for days

"10s"

17. Size in configurationProperties (@DataSizeUnit(DataUnit.MEGABYTES))

@DataSizeUnit

18.  /actuator/configprops

19. @Profile("production") @Configuration

20. spring.profiles.active && spring.profiles.include: ...

21. Logback

    logback-spring.xml, logback-spring.groovy, logback.xml, or logback.groovy

22. HttpMessageConverters / @JsonComponent

23. META-INF/resources/index.html,favicon.ico

24. src/main/resources/templates Thymeleaf

25. @CrossOrigin

27. server.port server.address server.servlet.session.persistence server.servlet.session.timeout server.servlet.session.store-dir server.servlet.session.cookie.*

28. 29.4.4 Customizing Embedded Servlet Containers

32. spring.datasource.jndi-name=java:jboss/datasources/customers

35. Apache Solr with spring-boot-starter-data-solr @SolrDocument SolrClient

36. spring-boot-starter-data-elasticsearch

37. Cassandra

38. Spring Boot supports auto-configuration of an in-memory LDAP server from UnboundID.

42. @KafkaListener(topics = "someTopic") @EnableKafkaStreams

43. RestTemplateBuilder restTemplateBuilder => .build() to create RestTemplate

45. Hazelcast？

48. Spring Integration

48. @RunWith(SpringRunner.class)
@DataJpaTest / @DataMongoTest / @DataRedisTest

49. @RestClientTest ?

50. Spring REST Docs

51. TestPropertyValues/OutputCapture

52. WebTestClient

53. WebSockets or Server Push Event

54. org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.mycorp.libx.autoconfigure.LibXAutoConfiguration,\
com.mycorp.libx.autoconfigure.LibXWebAutoConfiguration

@ConditionalOnMissingBean
@ConditionalOnMissingClass
@ConditionalOnProperty

private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(UserServiceAutoConfiguration.class));

55. ZK
  cloud:
    zookeeper:
      discovery:
        preferIpAddress: true

79.3 Customize the Jackson ObjectMapper

11. Login challenge

ServerHttpSecurity
ServerWebExchangeMatchers
AuthenticationWebFilter
SpringSessionWebSessionStore

26. @WebServlet, @WebFilter, and @WebListener on @ServletComponentScan

@WebServlet("/swagger/*")
public class SwaggerWebFilterServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpServletRequestWrapper reqWrapper = new HttpServletRequestWrapper(req);
        super.service(reqWrapper, resp);
    }
}

@ServletComponentScan(basePackageClasses = {SwaggerWebFilterServlet.class})

27. Converter

org.springframework.orm.hibernate5.SpringBeanContainer

28. Disable discovery

-->

## Curl examples

### Using *httpie*

user-service:

```bash
http -v ':8080/api/users' username=ziyang password=12345678 birthDate=1981-10-01
http -v ':8080/api/users?username=ziyang'
http -v ':8080/api/users/5'
http -v --form ':8080/api/users/5/portrait' 'portrait@./project-dependencies.png'
```

order-service:

```bash
http -v ':7001/api/orders' userId:=5 productId:=1000 quantity:=2
http -v ':7001/api/orders?userId=5'
```

### Using *curl*

> `curl` file uploading examples:
>
> `curl -v -F "portrait=@./project-dependencies.png" 'http://localhost:8080/api/users/5/portrait'`

Auth:

```bash
curl -v -H'Content-Type:application/json' -d '{"username":"amanda","password":"123445678"}' 'localhost:9090/auth-service/api/login'
curl -v -H'Cookie: SESSION=f0368d01-4fbf-4625-b31d-d1649dba8115' 'localhost:9090/user-service/api/users/current'
curl -v -XPOST -H'Cookie: SESSION=c941581e-d01b-490f-a2bc-72c91dc13aa1' 'localhost:9090/auth-service/api/logout'
```

User service:

```bash
curl -v -H'Content-Type:application/json' 'localhost:7777/api/authentication/login' -d'{"username":"amanda","password":"123445678"}'
```

via gateway:

```bash
curl -v 'localhost:9090/user-service/api/users/current'
```

Order service:

```bash
curl -v -H'Content-Type:application/json' -d'{"userId":100,"productId":1000,"quantity":2}' 'http://localhost:8080/api/orders'
curl -v -H'Content-Type:application/json' 'http://localhost:8080/api/orders?userId=100'
```

via gateway:

```bash
curl -v 'localhost:9090/order-service/api/orders?userId=100'
curl -v -H'Content-Type:application/json' -d '{"userId":200,"productId":300,"quantity":10}' 'localhost:9090/order-service/api/orders'
```

Gateway:

```bash
curl -v -H 'Content-Type:application/json' 'localhost:9090/user-service/users?name=tommyxx'
curl -v -H 'Expect:' -F "portrait=@settings.gradle" http://localhost:9090/api/user-service/users/1000/portrait
```

