package com.resolvenow.complaint;

import org.springframework.boot.*; import org.springframework.boot.autoconfigure.SpringBootApplication; import org.springframework.cloud.openfeign.EnableFeignClients;
@SpringBootApplication @EnableFeignClients
public class ComplaintServiceApplication { public static void main(String[] args) { SpringApplication.run(ComplaintServiceApplication.class, args); } }
