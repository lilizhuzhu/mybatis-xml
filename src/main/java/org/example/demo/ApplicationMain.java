package org.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/12
 */
@SpringBootApplication
@ComponentScan(basePackages = {"cn.hutool.extra.spring","org.example.demo"})
public class ApplicationMain {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class,args);
    }


    /*@Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver(MultipartProperties multipartProperties){
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setResolveLazily(multipartProperties.isResolveLazily());
        resolver.setMaxInMemorySize(-1);
        return resolver;
    }*/

}
