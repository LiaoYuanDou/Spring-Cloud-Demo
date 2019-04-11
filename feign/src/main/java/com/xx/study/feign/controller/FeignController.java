package com.xx.study.feign.controller;

import com.xx.study.feign.service.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: FeignController
 * @author: XX
 * @date: 2019/4/11 10:50
 * @description:
 */
@RestController
@RequestMapping("/feign")
public class FeignController {

    //编译器报错，无视。 因为这个Bean是在程序启动的时注入的，编译器感知不到，所以报错。
    @Autowired
    FeignService feignService;

    @RequestMapping(value = "/helloFeign", method = RequestMethod.GET)
    public String helloFeign(@RequestParam(value = "name", defaultValue = "XX") String name) {
        return feignService.helloFeign(name);
    }
}
