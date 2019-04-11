package com.xx.study.configclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 客户端的spring.application.name配置config-clent是和Git服务器上面的文件名相对应的，如果你的客户端是其他名字就报错找不到参数。
 * 客户端加载到的配置文件的配置项会覆盖本项目已有配置，
 * 比如客户端你自己配置的端口是8881，但是如果读取到config-clent-dev这个配置文件中也有配置端口为8882，那么此时客户端访问的地址应该是8882
 * 配置文件名将Application.properties改成bootstrap.properties,详情可见SpringCloud官方文档（配置文件优先级问题）
 *
 * @param
 * @author XX
 * @date 2019/4/11 16:07
 * @return
 */
@SpringBootApplication
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

}
