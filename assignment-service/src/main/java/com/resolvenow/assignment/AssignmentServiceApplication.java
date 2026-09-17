package com.resolvenow.assignment;
import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication @EnableFeignClients public class AssignmentServiceApplication { public static void main(String[] args){SpringApplication.run(AssignmentServiceApplication.class,args);} }
