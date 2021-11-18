package org.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author nmy
 * @version 1.0
 * @since 2021/11/12
 */
@SpringBootApplication
@ComponentScan(basePackages = {"cn.hutool.extra.spring","org.example.demo"})
public class MainTest {
    public static void main(String[] args) {
        SpringApplication.run(MainTest.class,args);
    }
}
