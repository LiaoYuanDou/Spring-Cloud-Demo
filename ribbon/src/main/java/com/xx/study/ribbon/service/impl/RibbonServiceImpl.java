package com.xx.study.ribbon.service.impl;

import com.xx.study.ribbon.service.RibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @className: RibbonServiceImpl
 * @author: XX
 * @date: 2019/4/11 10:30
 * @description:
 */
@Service
public class RibbonServiceImpl implements RibbonService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过之前注入ioc容器的restTemplate来消费spring.application.name服务的“/XX”接口，
     * 在这里我们直接用的程序名替代了具体的url地址，在ribbon中它会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的url替换掉服务名，
     *
     * @param
     * @return
     * @author XX
     * @date 2019/4/11 13:50
     */
    @Override
    public String helloServiceRibbon(String name) {
        return restTemplate.getForObject("http://EUREKA-CLIENT/eurekaClient/test?name=" + name, String.class);
    }
}
