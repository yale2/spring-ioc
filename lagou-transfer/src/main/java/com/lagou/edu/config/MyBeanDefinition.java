package com.lagou.edu.config;

import java.util.List;
import java.util.Map;

public class MyBeanDefinition {

    private Class<?> beanClass;

    private List<Map<String,Object>> properyList;

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public List<Map<String, Object>> getProperyList() {
        return properyList;
    }

    public void setProperyList(List<Map<String, Object>> properyList) {
        this.properyList = properyList;
    }
}
