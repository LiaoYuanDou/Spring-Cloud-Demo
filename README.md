# Spring-Cloud-Demo

### eureka Eureka作为服务注册与发现的组件

  eureka-server 服务注册中心 默认端口：8761
  启动一个服务注册中心，只需要一个注解@EnableEurekaServer，这个注解需要在springboot工程的启动application类上加
  eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），
  在默认情况下erureka server也是一个eureka client ,必须要指定一个 server。
  通过eureka.client.registerWithEureka：false和fetchRegistry：false来表明自己是一个eureka server.
  eureka server 是有界面的，启动工程,打开浏览器访问：http://ip:port
  
  
  eureka-client 服务注册与监听 服务发布者
  当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。
  Eureka server 从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。
  通过注解@EnableEurekaClient 表明自己是一个eurekaclient.
  还需要在配置文件中注明自己的服务注册中心的地址，
  需要指明spring.application.name,这个很重要，这在以后的服务与服务之间相互调用一般都是根据这个name 。
  
### ribbon
    ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。
	ribbon 已经默认实现了这些配置bean：

		IClientConfig ribbonClientConfig: DefaultClientConfigImpl

		IRule ribbonRule: ZoneAvoidanceRule

		IPing ribbonPing: NoOpPing

		ServerList ribbonServerList: ConfigurationBasedServerList

		ServerListFilter ribbonServerListFilter: ZonePreferenceServerListFilter

		ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer
		
	在工程的启动类中,通过@EnableDiscoveryClient向服务中心注册；并且向程序的ioc注入一个bean: restTemplate;并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
	
	通过之前注入ioc容器的restTemplate来消费spring.application.name服务的“/XX”接口，
	直接用的程序名替代了具体的url地址，在ribbon中它会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的url替换掉服务名，

### Feign
    Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，
	可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。
	Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
		Feign 采用的是基于接口的注解
		Feign 整合了ribbon，具有负载均衡的能力
		整合了Hystrix，具有熔断的能力
		
	在程序的启动类ServiceFeignApplication ，加上@EnableFeignClients注解开启Feign的功能：
	定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。