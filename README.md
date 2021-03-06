# Spring-Cloud-Demo

	在微服务架构中，需要几个基础的服务治理组件，包括服务注册与发现、服务消费、负载均衡、断路器、智能路由、配置管理等，由这几个基础组件相互协作，共同组建了一个简单的微服务系统
	
	
	
### eureka Eureka 服务注册与发现

	eureka-server 服务注册中心 默认端口：8761
	  启动一个服务注册中心，只需要一个注解@EnableEurekaServer，这个注解需要在springboot工程的启动application类上加
	  eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），
	  在默认情况下erureka server也是一个eureka client ,必须要指定一个 server。
	  通过eureka.client.registerWithEureka：false和fetchRegistry：false来表明自己是一个eureka server.
	  eureka server 是有界面的，启动工程,打开浏览器访问：http://ip:port
	  
	  Eureka通过运行多个实例，使其更具有高可用性。
	  要实现高可用的服务注册中心，可以让多台服务注册中心互相注册，
		  比如有三个服务注册中心Server1、Server2、Server3，让Server1注册Server2和Server3，
		  同理Server2、Server3分别注册另外两台服务注册中心，
		  然后让Client注册到每个服务注册中心上，例如：
		
		  Server1：
		    spring:
  				profiles: Server1
				eureka:
				  instance:
				    hostname: localhost
				  client:
				    registerWithEureka: false
				    fetchRegistry: false
				    serviceUrl:
				      defaultZone: http://${Server2IP}:${server2.port}/eureka/,http://${Server3IP}:${server3.port}/eureka/
		 
				Client：
				eureka:
				  client:
				    serviceUrl:
				      defaultZone: http://Server1.ip:server1.port/eureka/,http://Server2.ip:server2.port/eureka/,http://Server3.ip:server3.port/eureka/
				      
				需要改变etc/hosts，
				linux系统通过vim /etc/hosts,windows电脑，在c:/windows/systems/drivers/etc/hosts 修改。
					127.0.0.1 server1
					127.0.0.1 server2
					127.0.0.1 server3
		    java -jar eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=server1
  			或者
  			一个application.yml,不需要手动执行java -jar,可以这样
				spring:
				    profiles:
				        active: server1
		      
  eureka.instance.preferIpAddress=true是通过设置ip让eureka让其他服务注册它。也许能通过去改变去通过改变host的方式。
  
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
	Zuul有以下功能：
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
		
	Zuul不仅只是路由，并且还能过滤，做一些安全验证
		filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
			pre：路由之前
			routing：路由之时
			post： 路由之后
			error：发送错误调用
		filterOrder：过滤的顺序
		shouldFilter：这里可以写逻辑判断，是否要过滤，true,永远过滤。
		run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。

		
### Spring Cloud Config 分布式配置中心
    在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。在Spring Cloud中，有分布式配置中心组件spring cloud config ，它支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。在spring cloud config 组件中，分两个角色，一是config server，二是config client。
	
		Config Server
			在程序的入口Application类加上@EnableConfigServer注解开启配置服务器的功能
			在程序的配置文件application.properties文件配置
				spring.cloud.config.server.git.uri：配置git仓库地址
				spring.cloud.config.server.git.searchPaths：配置仓库路径
				spring.cloud.config.label：配置仓库的分支
				spring.cloud.config.server.git.username：访问git仓库的用户名
				spring.cloud.config.server.git.password：访问git仓库的用户密码
				如果Git仓库为公开仓库，可以不填写用户名和密码，如果是私有仓库需要填写
			启动程序：访问http://ip:port/foo/dev
				{"name":"foo","profiles":["dev"],"label":null,"version":"c4e740dbe9cfd3512f44e4bf250c7f7d9b5c187d","state":null,"propertySources":[]}
				证明配置服务中心可以从远程程序获取配置信息。
			http请求地址和资源文件映射如下:
				/{application}/{profile}[/{label}]
				/{application}-{profile}.yml
				/{label}/{application}-{profile}.yml
				/{application}-{profile}.properties
				/{label}/{application}-{profile}.properties
				
		Config Client		
			客户端的spring.application.name配置config-clent是和Git服务器上面的文件名相对应的，如果你的客户端是其他名字就报错找不到参数。
			客户端加载到的配置文件的配置项会覆盖本项目已有配置，
			比如客户端你自己配置的端口是8881，但是如果读取到config-clent-dev这个配置文件中也有配置端口为8882，那么此时客户端访问的地址应该是8882
			配置文件名将Application.properties改成bootstrap.properties,详情可见SpringCloud官方文档（配置文件优先级问题）

		
### Spring Cloud Bus 消息总线
		Spring Cloud Bus 将分布式的节点用轻量的消息代理连接起来。它可以用于广播配置文件的更改或者服务之间的通讯，也可以用于监控。
		config-client pom文件加上起步依赖spring-cloud-starter-bus-amqp
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-bus-amqp</artifactId>
			</dependency>

			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-starter-actuator</artifactId>
			</dependency>

		在配置文件bootstrap.properties中加上RabbitMq的配置，包括RabbitMq的地址、端口，用户名、密码。并需要加上spring.cloud.bus的三个配置
			spring.rabbitmq.host=localhost
			spring.rabbitmq.port=5672
			spring.rabbitmq.username=guest
			spring.rabbitmq.password=guest

			spring.cloud.bus.enabled=true
			spring.cloud.bus.trace.enabled=true
			management.endpoints.web.exposure.include=bus-refresh

		读取配置文件的类上加上 @RefreshScope 
		改变配置文件的值。如果是传统的做法，需要重启服务，才能达到配置文件的更新。此时，我们只需要发送post请求：http://ip:port/actuator/bus-refresh，你会发现config-client会重新读取配置文件
		另外，/actuator/bus-refresh接口可以指定服务，即使用"destination"参数，比如 “/actuator/bus-refresh?destination=customers:**” 即刷新服务名为customers的所有服务。
		当git文件更改的时候，通过pc端用post 向端口的config-client发送请求/bus/refresh／；此时端口会发送一个消息，由消息总线向其他服务传递，从而使整个微服务集群都达到更新配置文件。

		
		
### Zipkin 服务链路追踪(Spring Cloud Sleuth)
		Spring Cloud Sleuth 主要功能就是在分布式系统中提供追踪解决方案，并且兼容支持了 zipkin，只需要在pom文件中引入相应的依赖即可。
		微服务架构上通过业务来划分服务的，通过REST调用，对外暴露的一个接口，可能需要很多个服务协同才能完成这个接口功能，
		如果链路上任何一个服务出现问题或者网络超时，都会形成导致接口调用失败。随着业务的不断扩张，服务之间互相调用会越来越复杂。
			Span：基本工作单元，例如，在一个新建的span中发送一个RPC等同于发送一个回应请求给RPC，span通过一个64位ID唯一标识，trace以另一个64位ID表示，span还有其他数据信息，比如摘要、时间戳事件、关键值注释(tags)、span的ID、以及进度ID(通常是IP地址)
						span在不断的启动和停止，同时记录了时间信息，当你创建了一个span，你必须在未来的某个时刻停止它。
			Trace：一系列spans组成的一个树状结构，例如，如果你正在跑一个分布式大数据工程，你可能需要创建一个trace。
			Annotation：用来及时记录一个事件的存在，一些核心annotations用来定义一个请求的开始和结束
				cs - Client Sent -客户端发起一个请求，这个annotion描述了这个span的开始
				sr - Server Received -服务端获得请求并准备开始处理它，如果将其sr减去cs时间戳便可得到网络延迟
				ss - Server Sent -注解表明请求处理的完成(当请求返回客户端)，如果ss减去sr时间戳便可得到服务端需要的处理请求时间
				cr - Client Received -表明span的结束，客户端成功接收到服务端的回复，如果cr减去cs时间戳便可得到客户端从服务端获取回复的所有所需时间
		
		在spring Cloud为Finchley版本的时候，已经不需要自己构建Zipkin Server了，只需要下载jar即可，下载地址：
				https://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/
				java -jar zipkin-server-2.10.1-exec.jar 
				默认端口是9411
				
		服务暴露端和调用段引入
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-zipkin</artifactId>
			</dependency>
		在其配置文件application.yml指定zipkin server的地址，头通过配置“spring.zipkin.base-url”指定：
			spring.zipkin.base-url=http://localhost:9411
			在启动类里面并加上
						@Bean
						public Sampler defaultSampler() {
							return Sampler.ALWAYS_SAMPLE;
						}
		访问http://localhost:9411/



### Hystrix Dashboard 断路器监控
	 		在微服务架构中为例保证程序的可用性，防止程序出错导致网络阻塞，出现了断路器模型。
	 		断路器的状况反应了一个程序的可用性和健壮性，它是一个重要指标。
	 		Hystrix Dashboard是作为断路器状态的一个组件，提供了数据监控和友好的图形化界面。
		 		pom文件中引入
		 			<dependency>
		            <groupId>org.springframework.boot</groupId>
		            <artifactId>spring-boot-starter-actuator</artifactId>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
		        </dependency>
			程序的入口类，加上@EnableHystrix注解开启断路器，这个是必须的，并且需要在程序中声明断路点HystrixCommand；
			加上@EnableHystrixDashboard注解，开启HystrixDashboard
			
			打开http://localhost:port/actuator/hystrix.stream，可以看到一些具体的数据：
			打开localhost:port/hystrix
			在界面依次输入：http://localhost:port/actuator/hystrix.stream 、2000 、miya；点确定。
			重新刷新hystrix.stream网页，会看到良好的图形化界面
			
			
			
### Hystrix Turbine 断路器聚合监控 
		当我们有很多个服务的时候，这就需要聚合所以服务的Hystrix Dashboard的数据了。这就需要用到Spring Cloud的另一个组件了，即Hystrix Turbine。
		看单个的Hystrix Dashboard的数据并没有什么多大的价值，要想看这个系统的Hystrix Dashboard数据就需要用到Hystrix Turbine。
		Hystrix Turbine将每个服务Hystrix Dashboard数据进行了整合。Hystrix Turbine的使用非常简单，只需要引入相应的依赖和加上注解和配置就可以了。
				pom文件中引入
						<dependency>
		            <groupId>org.springframework.boot</groupId>
		            <artifactId>spring-boot-starter-actuator</artifactId>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
		        </dependency>
		        <dependency>
		            <groupId>org.springframework.cloud</groupId>
		            <artifactId>spring-cloud-starter-netflix-turbine</artifactId>
		        </dependency>
		
		在其入口类加上注解@EnableTurbine，开启turbine，@EnableTurbine注解包含了@EnableDiscoveryClient注解，即开启了注册服务。
		
		application.yml文件增加配置
				spring:
						application:
								name: service-turbine

				eureka:
				  client:
				    serviceUrl:
				      defaultZone: http://localhost:8761/eureka/
				management:
				  endpoints:
				    web:
				      exposure:
				        include: "*"
				      cors:
				        allowed-origins: "*"
				        allowed-methods: "*"

				turbine:
				  app-config: service-hi,service-lucy
				  aggregator:
				    clusterConfig: default
				  clusterNameExpression: new String("default")
				  combine-host: true
				  instanceUrlSuffix:
				    default: actuator/hystrix.stream  
			
			http://localhost:port/turbine.stream
			http://localhost:port/hystrix,输入监控流http://localhost:port/turbine.stream
			
			
			
### consul 服务注册
			spring cloud consul 组件，它是一个提供服务发现和配置的工具。consul具有分布式、高可用、高扩展性。
				使用 Raft 算法来保证一致性, 比复杂的 Paxos 算法更直接. 相比较而言, zookeeper 采用的是 Paxos, 
				etcd 使用的则是 Raft。	
				支持多数据中心，内外网的服务采用不同的端口进行监听。 多数据中心集群可以避免单数据中心的单点故障,而其部署则需要考虑网络延迟, 分片等情况等。 zookeeper 和 etcd 均不提供多数据中心功能的支持。
				支持健康检查。 etcd 不提供此功能。
				支持 http 和 dns 协议接口。 zookeeper 的集成较为复杂, etcd 只支持 http 协议。
				官方提供 web 管理界面, etcd 无此功能。
				综合比较, Consul 作为服务注册和配置管理的新星, 比较值得关注和研究。
			consul 具有以下性质：
					服务发现：consul通过http 方式注册服务，并且服务与服务之间相互感应。
					服务健康监测
					key/value 存储
					多数据中心
			去官网下载：https://www.consul.io/downloads.html 对应系统的版本
		  cmd启动： consul agent -dev
		  打开网址：http://localhost:8500 ，可以看到界面，相关服务发现的界面。
		  
		  引入pom文件
			  	<!--consul中健康检查需要用到actuator，不添加会check failing-->
	        <dependency>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-starter-actuator</artifactId>
	        </dependency>
	         <!--服务注册(consul)监听依赖-->
	        <dependency>
	            <groupId>org.springframework.cloud</groupId>
	            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
	        </dependency>
	        
	     application.yml配置
	     			spring:
	     			  cloud:
						    consul:
						      host: localhost
						      port: 8500
						      discovery:
						        service-name: service-consul #注册在consul上面的名字，在consul的调用中，是通过此名字调用的
						        register-health-check: true #健康检查，保证服务处于启动状态，建议开启