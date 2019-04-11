package com.xx.study.feign.service;

import com.xx.study.feign.service.fallback.FeignServiceHystricFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。
 * 熔断器 注解中加上fallback的指定类
 *
 * @className: FdignServie
 * @author: XX
 * @date: 2019/4/11 10:47
 * @description:
 */
@FeignClient(value = "EUREKA-CLIENT",fallback = FeignServiceHystricFallback.class)
public interface FeignService {
    @RequestMapping(value = "/eurekaClient/test", method = RequestMethod.GET)
    String helloFeign(@RequestParam(value = "name") String name);
}
