package com.xx.study.feign.controller;

import com.xx.study.feign.service.FeignServie;
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

    @Autowired
    FeignServie feignServie;

    @RequestMapping(value = "/helloFeign", method = RequestMethod.GET)
    public String helloFeign(@RequestParam(value = "name", defaultValue = "XX") String name) {
        return feignServie.helloFeign(name);
    }
}
