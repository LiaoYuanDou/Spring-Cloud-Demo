# Spring-Cloud-Demo

	在微服务架构中，需要几个基础的服务治理组件，包括服务注册与发现、服务消费、负载均衡、断路器、智能路由、配置管理等，由这几个基础组件相互协作，共同组建了一个简单的微服务系统
	
	
	
### eureka Eureka 服务注册与发现

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
  
  
  
### ribbon 服务消费者

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

	
	
### Feign 服务消费者

    Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，
	可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。
	Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
		Feign 采用的是基于接口的注解
		Feign 整合了ribbon，具有负载均衡的能力
		整合了Hystrix，具有熔断的能力
		
	在程序的启动类ServiceFeignApplication ，加上@EnableFeignClients注解开启Feign的功能：
	定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。
	
	
	
### Hystrix 断路器

	在微服务架构中，根据业务来拆分成一个个的服务，服务与服务之间可以相互调用（RPC），在SpringCloud可以用RestTemplate+Ribbon和Feign来调用。为了保证其高可用，单个服务通常会集群部署。
	由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。
	服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，这就是服务故障的“雪崩”效应。
	为了解决这个问题，业界提出了断路器模型。
	Netflix开源了Hystrix组件，实现了断路器模式，SpringCloud对这一组件进行了整合。 在微服务架构中，一个请求需要调用多个服务是非常常见的。
	较底层的服务如果出现故障，会导致连锁故障。当对特定的服务的调用的不可用达到一个阀值（Hystric 是5秒20次） 断路器将会被打开。
	断路打开后，可用避免连锁故障，fallback方法可以直接返回一个固定值。
	
	在ribbon使用断路器：
		在pox.xml文件中加入spring-cloud-starter-netflix-hystrix的起步依赖：
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
			</dependency>
		在程序的启动类加@EnableHystrix注解开启Hystrix：
		实现方法上加上@HystrixCommand注解。该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法，熔断方法直接返回了一个字符串。
		
	Feign中使用断路器
		Feign是自带断路器的，在D版本的Spring Cloud之后，它没有默认打开。需要在配置文件中配置打开它，在配置文件加以下代码：
			feign.hystrix.enabled=true
		只需要在FeignClient的接口的注解中加上fallback的指定类就行了：
		首先在servic下面新建一个包，命名为fallback；
		在fallback里面新建一个类 纳入spring容器，并实现接口 进行方法重写，返回当某个服务断开时你希望看到的提示内容

### Zuul 路由网关

	Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。
	zuul有以下功能：
		Authentication
		Insights
		Stress Testing
		Canary Testing
		Dynamic Routing
		Service Migration
		Load Shedding
		Security
		Static Response handling
		Active/Active traffic management
		
	zuul不仅只是路由，并且还能过滤，做一些安全验证
		filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
			pre：路由之前
			routing：路由之时
			post： 路由之后
			error：发送错误调用
		filterOrder：过滤的顺序
		shouldFilter：这里可以写逻辑判断，是否要过滤，true,永远过滤。
		run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。

		
### Spring Cloud Config 分布式配置中心