package com.lagou.edu.service;


import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyService;

@MyService
public class ServiceB {

    @MyAutowired
    private ServiceA serviceA;

    public void sayHello(){
        System.out.println("hello circleDependencyBean");
    }
}
