package com.lagou.edu.service;

import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyService;

@MyService
public class ServiceA {

    @MyAutowired
    private ServiceB serviceB;
}
