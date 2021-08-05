<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [核心组件](#%E6%A0%B8%E5%BF%83%E7%BB%84%E4%BB%B6)
  - [Eureka](#eureka)
    - [Eureka-server (服务端)](#eureka-server-%E6%9C%8D%E5%8A%A1%E7%AB%AF)
    - [Eureka 客户端](#eureka-%E5%AE%A2%E6%88%B7%E7%AB%AF)
  - [Ribbon](#ribbon)
  - [Hystrix](#hystrix)
  - [Fegin](#fegin)
  - [Zuul网关](#zuul%E7%BD%91%E5%85%B3)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->



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

### Hystrix
     Hystrix具备服务降级、服务熔断、线程和信号隔离、请求缓存、请求合并以及服务监控等强大功能
`@HystrixCommand` 来开启服务降级

`@CacheResult` 开启缓存

`@CacheKey`指定缓存的key，可以指定参数或指定参数中的属性值为缓存key，cacheKeyMethod还可以通过返回String类型的方法指定

`@CacheRemove`移除缓存，需要指定commandKey。

- 式例
```java
//    开启缓存、指定缓存Key
    @CacheResult(cacheKeyMethod = "getCacheKey")
//    开启服务降级
    @HystrixCommand(fallbackMethod = "fallbackMethod1", commandKey = "getUserCache")
    public Result getUserCache(Long id) {
        LOGGER.info("getUserCache id:{}", id);
        return restTemplate.getForObject(userServiceUrl + "/user/{1}", Result.class, id);
    }
```


### Fegin
    Feign具备可插拔的注解支持，同时支持Feign注解、JAX-RS注解及SpringMvc注解。
    当使用Feign时，Spring Cloud集成了Ribbon和Eureka以提供负载均衡的服务调用及基于Hystrix的服务容错保护功能。

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```
使用`@EnableFeignClients`启用Feign的客户端功能

### Zuul网关
    Spring Cloud Zuul通过与Spring Cloud Eureka进行整合，
    将自身注册为Eureka服务治理下的应用，同时从Eureka中获得了所有其他微服务的实例信息

使用`@EnableZuulProxy`来启动

```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
<!--健康检查 、 监控-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```
```yml
server:
  port: 8801

spring:
  application:
    name: zuul-proxy

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8001/eureka/

zuul:
  routes:
    # 给服务配置路由
    user-service:
      path: /userService/**
    feign-service:
      path: /feignService/**
# 给路由地址添加前缀
  prefix: /proxy
  #配置过滤敏感的请求头信息，设置为空就不会过滤
  sensitive-headers: Cookie,Set-Cookie,Authorization
  #设置为true重定向是会添加host请求头
  add-host-header: true

management:
  endpoints:
    web:
      exposure:
        include: 'routes'

ribbon:
  # 服务请求连接超时时间（毫秒）
  ConnectTimeout: 1000
  # 服务请求处理超时时间（毫秒）
  ReadTimeout: 3000
```
**_可添加过滤器和认证！_**

- Eureka：各个服务启动时，Eureka Client都会将服务注册到Eureka Server，并且Eureka Client还可以反过来从Eureka Server拉取注册表，从而知道其他服务在哪里
- Ribbon：服务间发起请求的时候，基于Ribbon做负载均衡，从一个服务的多台机器中选择一台
- Feign：基于Feign的动态代理机制，根据注解和选择的机器，拼接请求URL地址，发起请求
- Hystrix：发起请求是通过Hystrix的线程池来走的，不同的服务走不同的线程池，实现了不同服务调用的隔离，避免了服务雪崩的问题
- Zuul：如果前端、移动端要调用后端系统，统一从Zuul网关进入，由Zuul网关转发请求给对应的服务
