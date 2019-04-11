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

    @Override
    public String helloServiceRibbon(String name){
        return restTemplate.getForObject("http://EUREKA-CLIENT/eurekaClient/test?name="+name,String.class);
    }
}
