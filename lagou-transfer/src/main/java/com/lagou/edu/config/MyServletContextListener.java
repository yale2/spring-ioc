package com.lagou.edu.config;

import com.lagou.edu.factory.BeanFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yale
 */
public class MyServletContextListener implements ServletContextListener {

    private static Map<String,Object> ioc=new ConcurrentHashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        BeanFactory beanFactory=new BeanFactory();
        beanFactory.loadBeans();
        beanFactory.afterBeansCreated();
        System.out.println("servlet容器启动完毕，开始ioc容器初始化");

    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }


}
