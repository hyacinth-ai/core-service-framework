# Core Service Framework


<!-- vim-markdown-toc GitLab -->

* [Overview](#overview)
* [Development](#development)
  * [Code Standard](#code-standard)
  * [IDE](#ide)
* [Related Framework, Technology and Tools](#related-framework-technology-and-tools)
* [Runtime Environment](#runtime-environment)
* [Build from Source](#build-from-source)
* [Modules Reference](#modules-reference)
  * [Dependency Graph of Subprojects](#dependency-graph-of-subprojects)
  * [Typical Configuration](#typical-configuration)
  * [Async-Job](#async-job)
  * [Config Server](#config-server)
  * [Config Client](#config-client)
  * [Service Discovery Server (Eureka Server)](#service-discovery-server-eureka-server)
    * [Standalone Mode](#standalone-mode)
    * [Peer Mode](#peer-mode)
  * [Service Discovery Support (Eureka Client)](#service-discovery-support-eureka-client)
  * [Dynamic Configuration Reloading](#dynamic-configuration-reloading)
  * [Service Endpoint](#service-endpoint)
  * [Error Response](#error-response)
  * [Distributed Tracing (Sleuth)](#distributed-tracing-sleuth)
  * [Cache](#cache)
  * [Web Request Validation](#web-request-validation)
  * [File Uploading](#file-uploading)
  * [SSL Server](#ssl-server)
  * [HTTP/2](#http2)
  * [JWT on Gateway](#jwt-on-gateway)
  * [API Rate Limiter on Gateway](#api-rate-limiter-on-gateway)
  * [H2 In-Memory Database](#h2-in-memory-database)
  * [MySQL Database](#mysql-database)
  * [Using `JsonNode` as domain property](#using-jsonnode-as-domain-property)
  * [Generate JPA SQL Script](#generate-jpa-sql-script)
  * [Flyway Database Migration on Startup](#flyway-database-migration-on-startup)
  * [Flyway Database Migration by Gradle](#flyway-database-migration-by-gradle)
  * [Spring Cloud Bus and Bus-Event](#spring-cloud-bus-and-bus-event)
  * [Service Admin Server](#service-admin-server)
* [Test URL Examples](#test-url-examples)
  * [Using *httpie*](#using-httpie)
* [Todo for First Release](#todo-for-first-release)
* [Roadmap Points](#roadmap-points)

<!-- vim-markdown-toc -->

## Overview

Based on latest Spring Cloud version, *Core Service Framework* is a collection of tools and code snippets as several modules to help quickly implement micro-service architecture.
Feel free to use it as the start of your project or copy/paste what you like from this repo.

License: Apache License 2.0

## Development

### Code Standard

* Using Unix LF (\n)
* Java code format by Google Code Format (no "import ...*")

### IDE

* IntelliJ IDEA (Lombok, Google-Code-format, GenSerializeID)

## Related Framework, Technology and Tools

* Spring Framework Core (Context, Bean)
* Spring Web MVC
* Spring Reactor (Mono, Flux)
* Spring WebFlux
* Spring Boot (Config, BootPackage)
* Spring Security (on both MVC and WebFlux)
* Spring Cloud (Feign, Config, Eureka, Sleuth)
* Zipkin
* JUnit and Spring Boot Test
* JPA v2.2 and Spring Data JPA
* Jackson JSON Mapper
* Swagger
* Gradle
* git & git-flow
* curl, *httpie*, openssl
* MySQL, Redis, RabbitMQ
* JWT
* Maven repository

## Runtime Environment

* Java SDK 10 / 11
* Preferred charset: `UTF-8`

## Build from Source

With Gradle 5.x,

```bash
gradle buildAll
```

## Modules Reference

### Dependency Graph of Subprojects

![project-dependencies](./project-dependencies.svg)

Refer to `geenerate-dep.sh` under `tools`.

### Typical Configuration

The property `spring.application.name` should be set on `bootstrap.yml` or `application.yml` like this:

```yaml
spring:
  profiles:
    active: development
  application:
    name: food-service
    version: 1.0
```

Suggest that `spring.profiles.active` be set to a concrete execution environment value for easy development:

* `development`
* `production`
* `testing`

and override it on deployment.

> If there's no spring-cloud related modules (discovery, config) loaded, for example, in a pure spring-boot MVC service, the `bootstrap.yml` won't be loaded at all.
> Therefore, it's better to always set the application name in `application.yml` as well.

### Async-Job

Enable async job scheduling by adding `@EnableAsync` and `@EnableScheduling` to the application configuration class.

Schedule a job with `@Scheduled` annotation.

```java
  @Scheduled(fixedRateString = "PT2M", initialDelayString = "PT30S")
  public void syncDeploymentStatus() {
    // ...
  }
```

### Config Server

Run `core-service-config-server` with overriding following properties:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: http://docker.hyacinth.services:3000/user/config-repo.git
          # uri: file://${user.home}/projects/config-repo # use local directory
```

Default service port is 8888.

Testing urls are like:

```shell
http :8888/user-service/development/master
# match properties files: user-service-development.yml, user-service.yml, application-development.yml, application.yml ...

http :8888/resources/development/master/ssh_config
# match plain resource files: ssh_config-development, ssh_config ...
```

### Config Client

Add `core-service-config-support` as dependency with overriding following properties:

```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888
```

### Service Discovery Server (Eureka Server)

#### Standalone Mode

```bash
java -jar build/libs/core-service-discovery-server-1.0.0.RELEASE-boot.jar
```

Default service port is `8761`.

#### Peer Mode

Examples:

Peer #1:

```sh
SPRING_PROFILES_ACTIVE=peer SERVER_PORT=8761 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8762/eureka/ \
java -jar build/libs/core-service-discovery-server-1.0.0.RELEASE-boot.jar
```

Peer #2:

```sh
SPRING_PROFILES_ACTIVE=peer SERVER_PORT=8762 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ \
java -jar build/libs/core-service-discovery-server-1.0.0.RELEASE-boot.jar
```

### Service Discovery Support (Eureka Client)

Using `core-service-discovery-support` as dependency with overriding the following properties:

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

To enable health check:

```yaml
eureka:
  client:
    healthcheck:
      enabled: true
```

To disable service discovery and instance registration, set `eureka.client.enabled` property or using environment variable as follow:

```bash
EUREKA_CLIENT_ENABLED=false java -jar debug-service.jar
```

> "Config First Bootstrap" is used instead of "Discovery First Bootstrap".
> That means that the application could pull discovery configuration from the configuration server **before** starting the discovery client.

### Dynamic Configuration Reloading

Triggering endpoint `/actuator/refresh` causes configuration (yaml, properties) reloading and `@ConfigurationProperties` rebinding.

`@RefreshScope` can also be applied on the beans which need re-initialization after rebinding of configuration properties.

> `core-service-gateway-server` module is a working example that shows the ability of dynamic re-building api routes from the configurations pulled from config server via `core-service-config-support`.

### Service Endpoint

Implement a concrete service by adding dependency module `core-service-endpoint-support` which includes following basic features:

* Basic transit dependency to implement a SpringMVC-based service on Spring Boot platform.
* Most `actuator` endpoints including `Prometheus` are enabled.
* Exception and error code conversion. Refer to class `ServiceApiException` and `ServiceControllerExceptionHandler`.
* Swagger support. Access swagger console by `http://<service-address>:<port>/swagger-ui.html`.
* Logging support. Use Slf4j to log on console and to a json-format file. The default file name is `${spring.application.name}.log.jsonl` which could be overridden by property `logging.file`.

### Error Response

If a `ServiceApiException` is raised, it is finally converted into a json format like

```json
{
    "message": "UNKNOWN_ERROR",
    "code": "E80000",
    "data": "No converter found",
    "status": "error",
    "path": "/api/dataset",
    "service": "main-service",
    "timestamp": "2019-09-18T01:19:50.466+0000"
}
```

The fields `status`, `path`, `service`, `timestamp` are auto-generated. `message`, `code`, `data` are based on the exception thrown.

This format could be parsed by `core-support-api-support` module and converted back into a local corresponding exception during remote call in the downstream service if possible.

### Distributed Tracing (Sleuth)

Use module `core-service-tracing-support`.

As a result, a tracing information section like `[<Service>,<TraceId>,<SpanId>,<Exportable>]` could be found for every log line.

```text
[2019-04-15 20:40:38,960] [INFO ] ... [gateway-server,80adb541045a0103,80adb541045a0103,true] ...
```

Additionally, if `zipkin` is used, try the following config:

```yaml
spring:
  zipkin:
    enabled: true
    base-url: http://zipkin-host:9411/
  sleuth:
    sampler:
      # either "rate" or "probability" is effective
      # probability: 0.1 # default
      rate: 3 # limit to N requests per second
```

> Zipkin server (in-memory storage) can be started by
>
> `docker run -d -p 9411:9411 openzipkin/zipkin`

### Cache

Add `core-service-cache-support` as dependency and import `CacheConfig`.

`Caffeine` is a in-memory cache provider so `Serializable` is not mandatory on the cached object.

Here's the default configuration.

```yaml
spring:
  cache:
    caffeine:
      spec: maximumSize=100,expireAfterAccess=10m
```

Create cache by setting cache names:

```yaml
spring.cache.cache-name=cache-name-1,cache-name-2,piDecimals
```

Code example of using cache:

```java
@Cacheable("piDecimals")
public String getPi(int decimals) {
  // ...
}
```

> Generally, only one `CacheManager` instance is configured in application context.
> Remove dependency of this module if other type of cache is configured.

<!--

`Redis` cache can be activated by using SpringBoot profile `redis-cache` and overriding redis properties like:

```yaml
spring:
  redis:
    url: redis://redis:alice-secret@docker.hyacinth.services:6379
```

> NOTE:
>
> According to `org.springframework.boot.autoconfigure.cache.RedisCacheManager`, the default configuration is using JDK serializer.
> That means the returning object should implement `java.io.Serializable`.

-->

### Web Request Validation

`@Validated` / `@Valid` on request body method argument with constraints on fields like `@Size(min = 8, max = 10)` or `@NotNull`.

Validation error processing is implemented in web support module. Error response could be like:

```json
{
    "code": "E80100",
    "data": {
        "field": [
            "username"
        ]
    },
    "message": "REQUEST_VALIDATION_FAILED",
    "path": "/api/users",
    "service": "user-service",
    "status": "error",
    "timestamp": "2019-04-10T14:24:34.033+0000"
}
```

### File Uploading

Default multipart configuration in `core-service-web-support` module:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 10MB
      file-size-threshold: 1MB
```

Code example:

```java
@PostMapping("/users/{userId}/portrait")
public Map<String, Object> uploadUserPortrait(
  @PathVariable String userId,
  @RequestParam("portrait") MultipartFile file) {
  // ...
}
```

> NOTE:
>
> Http header `Expect: 100-continue` is not supported when a multipart request passes through the gateway server due to a bug in Spring Cloud Gateway. `curl` use that header but most browsers don't.
>
> However, sending request directly to a service works properly.

### SSL Server

Enable SSL configuration:

```yaml
server:
  ssl:
    enabled: true
    key-alias: tomcat
    key-store-password: password
    key-store: ./tomcat.ks # or "classpath:keystore.p12"
    # key-store-type: PKCS12
```

> If certificate files are generated by `certbot` (by `Let's Encrypt`), try `./tools/certs_to_ks.sh` which converts these files into java keystore format.

### HTTP/2

Enable HTTP/2 by following settings (SSL is mandatory):

```yaml
server:
  http2:
    enabled: true
```

> If a HTTP/2 connection goes through Spring Cloud Gateway, the routed service should be able to serve this HTTP/2 request.

### JWT on Gateway

Configure the following properties on `core-service-gateway-server` by:

```yaml
ai.hyacinth.core.service.gateway.server:
  jwt:
    enabled: true
    signing-key-file: file:keys/sym_keyfile.key
    # or set base64 string of the key:
    # signing-key: utWVSlUPfb3be0npL0JN41vuKJpFehpVZZKzJz5...
    expiration: 2h
```

Signing key file is a >64-bytes file which represents a secret key. Try generating like this:

```bash
openssl rand 128 > sym_keyfile.key

# print base64 string if "signing-key" is used.
base64 -i ./keys/sym_keyfile.key
```

A JWT token is auto-generated each time an `Authentication` payload returned from configured backend service like this:

```yaml
  - path: /auth-service/api/login
    method: POST
    authority: any
    service: user-service
    uri: /api/authentication/login
    post-processing:
      - authentication-jwt
      - api
```

The final response wrapping JWT token returns as:

```json
{
    "data": {
        "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNTU1MTc0NzQ5LCJhdXRob3JpdHkiOlsiVVNFUiIsIkFQSSJdLCJwcmluY2lwYWwiOjF9.nXrJIh4GRYkFDe-i4RrOpZXENn_-hfIYRa3QYBbQ1FaJVGOcwVqn-IDqBHbytW8GaOgrGt2CUFm6-LB-TW1bgg"
    },
    "status": "success"
}
```

### API Rate Limiter on Gateway

API rate limiter could be configured below:

```yaml
ai.hyacinth.core.service.gateway.server:
  rate-limiter:
    replenish-period: 1m
    replenish-rate: 20
```

Only *authenticated* user is restricted. It has no effect on public API (*anonymous* access).

### H2 In-Memory Database

With `core-service-jpa-support`, if no specific JDBC datasource is configured, `h2` is used as default database. H2 database console can be accessed via `http://host:port/h2-console`.

Try using in-memory h2 jdbc-url `jdbc:h2:mem:testdb` (user: `sa`, password: *empty (no password)*) when log into the console.

### MySQL Database

A typical modern MySQL datasource configuration is like this:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db?characterEncoding=UTF-8&useSSL=false
    username: root
    password: password

  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate.dialect: org.hibernate.dialect.MySQL57Dialect
      hibernate.dialect.storage_engine: innodb
```

The recommended table creation DDL suffix is: `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci`. It can also achieved in  database level.

```sql
CREATE SCHEMA `hyacinth_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci ;
```

### Using `JsonNode` as domain property

Example:

```java
  @Type(type = "json-node")
  @Column(columnDefinition = "text")
  @Basic(fetch = FetchType.LAZY)
  private JsonNode taskDoc;
```

> If the database supports "JSON" type, for example, MySQL or PostgreSQL, use "json" as `columnDefinition`, "jsonb" as argument of `@Type`.

### Generate JPA SQL Script

Enable SQL script generation by the following configuration. Refer to `jpa-support` module.

```yaml
spring:
  jpa:
    properties:
      javax:
        persistence:
          schema-generation:
            scripts:
              action: drop-and-create # default is "none" which means no generation
```

### Flyway Database Migration on Startup

Put SQL scripts (naming like `V1__init_schema.sql`) under `classpath:db/migration` or `classpath:db/migration/mysql`.

Enable flyway by:

```yaml
spring.flyway.enabled: true
```

If database is not empty, baseline operation is required:

```yaml
spring.flyway.baseline-on-migrate: true
spring.flyway.baseline-version: 1
```

> Startup migration is not recommended in production environment due to different user/pass, priviledges
> used between `admin` who executes DDL and `user` who execute DML.
>
> Database migration could be an separate job before starting a service. Read below.

### Flyway Database Migration by Gradle

Use `gradle`. The simplest way:

```bash
export FLYWAY_URL="jdbc:mysql://user:pass@db-host:3306/database?characterEncoding=UTF-8&useSSL=false"

gradle flywayinfo

# gradle -Pflyway.user=user -Pflyway.password=password -Pflyway.url=... flywayvalidate

gradle flywaymigrate

# for non-empty database
gralde -Pflyway.baselineOnMigrate=true -Pflyway.baselineVersion=0 flywaymigrate
```

Dangerous `flywayclean` task is disabled in `build-script.gradle`.

> Refer to [Flyway via gradle](https://flywaydb.org/documentation/gradle/) for advanced usage.

### Spring Cloud Bus and Bus-Event

With module `core-service-bus-support` and importing of `BusConfig`,
`Spring Cloud Bus` can be enabled by configuring `RabbitMQ` properties:

```yaml
spring:
  rabbitmq:
    # addresses: host1:port1,host2:port2
    host: docker.hyacinth.services
    port: 5672
    username: alice
    password: alice-secret
```

Send user-defined event by `BusEvent`:

```java
  @Autowired private BusService busService;

  public void broadcast() {
    busService.publish(BusService.ALL_SERVICES, "MyEventType", eventPayload);
  }

  @EventListener
  public void handleBusEvent(BusEvent<?> busEvent) {
    // busEvent.getEventType();
    // ...
  }
```

> Refer to `org.springframework.cloud.bus.BusAutoconfiguration` for internal implementation of `Spring Cloud Bus`.

### Service Admin Server

Based on `Spring Boot Admin Server`, the admin server uses discovery mechanism to find all services registered.

Default port is 8080.

Configuration properties:

* Setting `spring.boot.admin.notify.slack.webhook-url` enables Slack notification
* Setting `spring.boot.admin.notify.mail.enabled=true` and `spring.mail.host (port, username, password, ...)` enables Email notification (tested on Gmail using Gmail app-password)

## Test URL Examples

### Using *httpie*

user-service:

```bash
http -v ':8080/api/users' username=ziyangx password=12345678 birthDate=1981-10-01
http -v ':8080/api/users?username=ziyangx'
http -v ':8080/api/users/5'
http -v --form ':8080/api/users/5/portrait' 'portrait@./project-dependencies.png'

# for user-pass auth:
http -v ':8080/api/authentication/login' username=ziyang password=12345678

# curl examples for uploading
curl -v -F "portrait=@./project-dependencies.png" 'http://localhost:8080/api/users/5/portrait'
```

order-service:

```bash
http -v ':7001/api/orders' userId:=5 productId:=1000 quantity:=2
http -v ':7001/api/orders?userId=5'
```

debug-service:

```bash
http -v ':8080/api/call'
```

gateway server:

```bash
http -v ':9090/auth-service/api/login' username=ziyangx password=12345678
http -v ':9090/user-service/api/users/me' 'Cookie:SESSION=788161d1-4d1e-445e-9823-a1a0e7037b44'
http -v ':9090/user-service/api/users/current' 'Cookie:SESSION=788161d1-4d1e-445e-9823-a1a0e7037b44'
http -v ':9090/order-service/api/orders?userId=5' 'Cookie:SESSION=90dd2087-278a-4d9a-a512-dfb9dce78e17'
http -v ':9090/order-service/api/orders' 'Cookie:SESSION=9bf96fb0-752e-4659-9511-bcdeeaa925be' userId:=4 productId:=1000 quantity:=2

# jwt
http -v ':9090/user-service/api/users/whoami' 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNTU1ODU5MTQyLCJpc3MiOiJnYXRld2F5LWlzc3VlciIsImV4cCI6MTU1NTg2NjM0MiwiYXV0aG9yaXR5IjpbIlVTRVIiLCJBUEkiXSwicHJpbmNpcGFsIjoxLCJ2ZXJzaW9uIjoxfQ.RJag7ZRn0P-ohz3k6xYah5unr4AmecO4EpayrJ6dAqAH4LAg2kp_DIgU-8Zk6n6Hc4Cu7Pzzb1pbrlJQ9OOX2Q'

```

## Todo for First Release

* Documentation for modules introduced (Config class)

* Document for Job trigger server

## Roadmap Points

TL;DR (commented, only shown in source file)

<!--

1. Bug: Profile loading for fallback "default"

  docProfile=null
  activeProfiles=[]
  defaultProfiles=["default"]

  should load the doc but observed not.

1. Promethues + Grafana

30. Online chart App

2. AVRO schema

11. Login challenge (reCaptcha)

17. Saga

spring state machine

State (name, entering, do, getDoingStatus, undo, getUndoingStatus, exiting)
configuration-items: async query interval

(DONE, ASYNC_STATUS, ABORT, REVERT, REVIEW_REQUIRED)

13. Elastic Search

    compile 'org.elasticsearch.client:elasticsearch-rest-high-level-client:7.0.1'

https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.0/java-rest-high-document-index.html

1. Spring Cloud Bug

RabbitExchangeQueueProvisioner:172

1. Trigger service support EventBus

2. Logging Mark

console: "[%date{ISO8601}] [%r] [%clr(%-5level)] [%thread] [%logger] [%F:%L] [${PID:- }] [${spring.zipkin.service.name:${spring.application.name:-}}] [${spring.application.instance:-}] [%X] [%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}] --- %msg MULTIEXCEPTION %replace(%rEx){'\n','\u2028'}%nopex%n"

${PID}: The current process ID.
${LOG_FILE}: Whether logging.file was set in Boot’s external configuration.
${LOG_PATH}: Whether logging.path (representing a directory for log files to live in) was set in Boot’s external configuration.
${LOG_EXCEPTION_CONVERSION_WORD}: Whether logging.exception-conversion-word was set in Boot’s external configuration.

[%X{X-B3-TraceId:-},%X{X-B3-SpanId:-},%X{X-Span-Export:-}]

32. Single Sign On

33. SSO/OAuth2

29. OAuth2 for WX

1. Gateway features:

  request limit (redis) on anonymous accessible API (public API)

0. Spring Cloud Stream

1. RabbitMQ integration (AmqpTemplate / AmqpAdmin)

@RabbitListener(queues = "someQueue") // containerFactory="myFactory"
public void processMessage(String content) {

1. Gateway Feature

  Response-to-Session Mapping

  Rewrite URL by Session Key

  Dangerous header removing: X-Forwarded-For

  Difference between access denied payload / login required payload

15. Nginx default configuration (Replaced by Traefik)

42. @KafkaListener(topics = "someTopic") @EnableKafkaStreams

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

```
my:
  servers:
  - dev.example.com
  - another.example.com
```

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

private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(UserServiceAutoConfiguration.class));

55. ZK
  cloud:
    zookeeper:
      discovery:
        preferIpAddress: true

79.3 Customize the Jackson ObjectMapper

80. Webflux Security

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

27. Hibernate Property Converter

Inject object automatically via org.springframework.orm.hibernate5.SpringBeanContainer

31. Spring Cloud Contract

-->

<!--
Using *curl*

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

-->
