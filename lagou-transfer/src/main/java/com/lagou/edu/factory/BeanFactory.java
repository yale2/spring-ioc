package com.lagou.edu.factory;

import com.google.common.base.Strings;
import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyQualifier;
import com.lagou.edu.annotation.MyService;
import com.lagou.edu.annotation.MyTransactional;
import com.lagou.edu.config.MyBeanDefinition;
import com.lagou.edu.config.MyMethodInterceptor;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 应癫
 * <p>
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    /**
     * 存储对象
     */

    private static Map<String, Object> map = new ConcurrentHashMap<>();

    private static Map<String, String> classNameMap = new HashMap();

    private List<String> proxyClasses = new ArrayList<>();


    public void loadBeans() {
        Reflections reflections = new Reflections();
        Set<Class<?>> serviceAnnotationClasses = reflections.getTypesAnnotatedWith(MyService.class);
        for (Class<?> serviceAnnotationClass : serviceAnnotationClasses) {
            String beanName = serviceAnnotationClass.getAnnotation(MyService.class).value();
            if (Strings.isNullOrEmpty(beanName)) {
                beanName = serviceAnnotationClass.getName().toLowerCase();
            }

            //类或者方法被@MyTransaction注解标记，加入集合，方便初始化完成后生成代理对象
            if (serviceAnnotationClass.isAnnotationPresent(MyTransactional.class)) {
                proxyClasses.add(beanName);
            } else {
                for (Method declaredMethod : serviceAnnotationClass.getDeclaredMethods()) {
                    if (declaredMethod.getAnnotationsByType(MyTransactional.class).length > 0) {
                        proxyClasses.add(beanName);
                    }
                }
            }

            try {
                createBean(beanName, serviceAnnotationClass);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }


    private void createBean(String beanName, Class<?> serviceAnnotationClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, ClassNotFoundException {
        //反射生成bean实例
        Object o = serviceAnnotationClass.newInstance();
        String value = serviceAnnotationClass.getAnnotation(MyService.class).value();
        if(!Strings.isNullOrEmpty(value)){
            map.put(value, o);
        }else{
            //保存类名与实例映射关系
            map.put(beanName, o);
        }
        //给bean注入属性，主要是检查依赖并注入
        injectBeanProperty(beanName, serviceAnnotationClass);
    }


    private void injectBeanProperty(String beanName, Class<?> serviceAnnotationClass) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException, ClassNotFoundException {
        Field[] declaredFields = serviceAnnotationClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            MyAutowired annotation = declaredField.getAnnotation(MyAutowired.class);
            if (null != annotation) {
                if (declaredField.getClass().getAnnotationsByType(MyService.class) == null) {
                    throw new RuntimeException("被依赖的对象不是一个bean");
                }
                String dependencyBeanName;
                MyQualifier[] qualifiers = declaredField.getAnnotationsByType(MyQualifier.class);
                if (qualifiers.length > 0) {
                    //此处因为初始化顺序的问题 可能为空
                    dependencyBeanName = qualifiers[0].value();
                } else {
                    dependencyBeanName = declaredField.getType().getName().toLowerCase();
                }
                if (!map.containsKey(dependencyBeanName)) {
                    //如果依赖的bean不存在，将依赖的bean对象放入ioc容器
                    createBean(dependencyBeanName, Class.forName(declaredField.getType().getName()));
                }
                declaredField.setAccessible(true);
                //给当前bean注入依赖bean
                declaredField.set(map.get(beanName), map.get(dependencyBeanName));
            }
        }
    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static Object getBean(String id) {
        return map.get(id);
    }

    /**
     * bean初始化之后，被@MyTransactional注解标记的对象全部换成代理对象
     */
    public void afterBeansCreated() {
        for (String proxyClass : proxyClasses) {
            ProxyFactory proxyFactory = new ProxyFactory(new TransactionManager());
            Object o = map.get(proxyClass);
            if (o.getClass().getInterfaces().length > 0 || o.getClass().isInterface()) {
                map.put(proxyClass, proxyFactory.getJdkProxy(o));
            } else {
                map.put(proxyClass, proxyFactory.getCglibProxy(o));
            }
        }
    }
}
