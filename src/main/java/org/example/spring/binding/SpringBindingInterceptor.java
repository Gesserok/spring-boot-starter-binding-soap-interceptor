package org.example.spring.binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import javax.enterprise.context.RequestScoped;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestScoped
public class SpringBindingInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SpringBindingInterceptor.class);

    @AroundInvoke
    public Object invoke(InvocationContext context) throws Exception {

        final Object target = context.getTarget();
        Class<?> targetClass = target.getClass();

        while (targetClass != null) {
            for (Field field : targetClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {

                    try {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(
                                field,
                                target,
                                SpringBinding.getBean(field.getName(), field.getType())
                        );

                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }

        return context.proceed();
    }

    @Configuration
    static class SpringBinding {

        private static ApplicationContext context;

        private static List<String> existingBeanNames = new ArrayList<>();

        @Autowired
        public SpringBinding(ApplicationContext applicationContext) {
            context = applicationContext;
            if (context != null) {
                existingBeanNames = Arrays.asList(context.getBeanDefinitionNames());
            }
        }

        private static Object getBean(String beanName, Class<?> beanType) {
            if (context == null) {
                throw new IllegalStateException("ApplicationContext context could not be null");
            }

            if (existingBeanNames.contains(beanName)) {
                return context.getBean(beanName);
            }


            return context.getBean(beanType);
        }
    }
}
