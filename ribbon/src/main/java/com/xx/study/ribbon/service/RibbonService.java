package com.xx.study.ribbon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @className: RibbonService
 * @author: XX
 * @date: 2019/4/11 10:29
 * @description:
 */
public interface RibbonService {

    public String helloServiceRibbon(String name);
}
