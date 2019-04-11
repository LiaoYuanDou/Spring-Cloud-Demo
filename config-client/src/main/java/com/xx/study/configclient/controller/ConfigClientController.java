package com.xx.study.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: ConfigClientController
 * @author: XX
 * @date: 2019/4/11 15:57
 * @description:
 */
@RestController
@RequestMapping("/config-client")
public class ConfigClientController {

    @Value("${foo}")
    String foo;

    @RequestMapping(value = "/hello")
    public String hello(){
        return foo;
    }
}
