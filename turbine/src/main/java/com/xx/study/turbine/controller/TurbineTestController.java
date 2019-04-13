package com.xx.study.turbine.controller;

import com.xx.study.turbine.controller.service.TurbineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/turbine")
public class TurbineTestController {
    @Autowired
    private TurbineService turbineService;

    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public String testTurbine(@RequestParam(value = "name",defaultValue = "XX")String name){
        return turbineService.testTurbine(name);
    }
}
