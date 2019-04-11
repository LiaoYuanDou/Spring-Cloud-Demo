package com.xx.study.feign.service.fallback;

import com.xx.study.feign.service.FeignService;
import org.springframework.stereotype.Component;

/**
 * 实现FeignService 接口，并注入到Ioc容器中
 * @className: FeignServiceHystricFallback
 * @author: XX
 * @date: 2019/4/11 14:33
 * @description:
 */
@Component
public class FeignServiceHystricFallback implements FeignService {
    @Override
    public String helloFeign(String name) {
        return "sorry " + name;
    }
}
