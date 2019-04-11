package com.xx.study.eurekaclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: EurekaTestController
 * @author: XX
 * @date: 2019/4/11 09:59
 * @description:
 */
@RestController
@RequestMapping("/eurekaClient")
public class EurekaTestController {

    @Value("${server.port}")
    private String port;

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String test(@RequestParam(value = "name", defaultValue = "XX") String name) {
        return "Hello " + name + " ,I am from port: " + port;
    }

}
