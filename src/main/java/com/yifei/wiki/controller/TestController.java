package com.yifei.wiki.controller;

import com.yifei.wiki.domain.Test;
import com.yifei.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController // @Conroller + @ResponseBody
public class TestController {


    @Value("${hello.prop: defaultVal}")
    private String prop;
    @Autowired
    private TestService testService;

    /*
    @RequestMapper(path = "/hello", method = RequestMethod.GET)
    @GetMapping("/hello")
     */
    @RequestMapping("/hello")
    public String hello(){
        return "Hello, Spring!" + prop;
    }

    @RequestMapping("/test")
    public List<Test> test(){
        return testService.findAll();
    }
}
