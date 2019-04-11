package com.xx.study.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。
 *
 * @className: FdignServie
 * @author: XX
 * @date: 2019/4/11 10:47
 * @description:
 */
@FeignClient(value = "EUREKA-CLIENT")
public interface FeignServie {
    @RequestMapping(value = "/eurekaClient/test", method = RequestMethod.GET)
    public String helloFeign(@RequestParam(value = "name") String name);
}
