package com.xx.study.turbine.controller.service.Impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.xx.study.turbine.controller.service.TurbineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TurbineServiceImpl implements TurbineService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @HystrixCommand(fallbackMethod = "helloError")
    public String testTurbine(String name) {
        return restTemplate.getForObject("http://EUREKA-CLIENT/eurekaClient/test?name=" + name, String.class);
    }

    public String helloError(String name) {
        return "hello," + name + ",sorry,TurbineError!";
    }
}
