

## 核心组件
### Eureka
#### Eureka-server (服务端)
    Eureka 也称服务注册中心，同其他服务注册中心一样，支持高可用。可以集群部署
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
```
```yaml
# 指定运行端口
server:
  port: 8002

# 指定服务名称
spring:
  application:
    name: eureka-server

# 指定主机地址
eureka:
  instance:
    hostname: replica1
  client:
#    如果不使用集群，不需要注册到另一个Eureka注册中心
    # 指定是否从注册中心获取服务(不集群注册中心不需要开启)
    fetch-registry: true
    # 指定是否将服务注册到注册中心(不集群注册中心不需要开启)
    register-with-eureka: true
    service-url:
      # 注册到另一个Eureka注册中心
      defaultZone: http://localhost:8003/eureka/

```

Eureka Server的 **高可用** 实际上就是将自己作为服务向其他注册中心注册自己，这样就可以形成 **一组互相注册的服务注册中心** ，以实现服务清单的互相同步，达到高可用效果
#### Eureka 客户端
    主要处理服务的注册与发现。
    Eureka客户端向注册中心注册自身提供的服务并周期性地发送心跳来更新它的服务租约。
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
```
使用`@EnableDiscoveryClient`开启服务
```yaml
# 指定运行端口
server:
  port: 8101

# 指定服务名称
spring:
  application:
    name: eureka-client

eureka:
  client:
    # 注册到Eureka的注册中心
    register-with-eureka: true
    # 获取注册实例列表
    fetch-registry: true
    service-url:
      # 配置注册中心地址(可以注册到多个注册中心)
      defaultZone: http://localhost:8001/eureka
#,http://localhost:8002/eureka/,http://localhost:8003/eureka/

```

### Ribbon
    Ribbon是一个基于HTTP和TCP的客户端负载均衡器

pom.xml
```xml
    <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
```

将RestTemplate注入
```java
@Configuration
public class RibbonConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}

```
```java

    @Autowired
    private RestTemplate restTemplate;
// 使用 RestTemplate
    @GetMapping("/{id}")
    public Result getUser(@PathVariable Long id) {
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", Result.class, id);
    }
```

#### Hystrix


#### Fegin
    Feign具备可插拔的注解支持，同时支持Feign注解、JAX-RS注解及SpringMvc注解。
    当使用Feign时，Spring Cloud集成了Ribbon和Eureka以提供负载均衡的服务调用及基于Hystrix的服务容错保护功能。

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```
使用`@EnableFeignClients`启用Feign的客户端功能
