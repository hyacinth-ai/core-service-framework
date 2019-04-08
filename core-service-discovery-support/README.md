# Eureka Server Application

# Runtime Modes

## Standalone Mode

java -jar build/libs/core-service-eureka-server-1.0.0-boot.jar

## Peer Mode

Peer #1:
```
SPRING_PROFILES_ACTIVE=peer SERVER_PORT=8761 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8762/eureka/ java -jar build/libs/core-service-eureka-server-1.0.0-boot.jar
```

Peer #2:
```
SPRING_PROFILES_ACTIVE=peer SERVER_PORT=8762 EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ java -jar build/libs/core-service-eureka-server-1.0.0-boot.jar
```
