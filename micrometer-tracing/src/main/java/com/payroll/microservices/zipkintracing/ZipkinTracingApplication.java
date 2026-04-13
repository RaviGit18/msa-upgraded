package com.payroll.microservices.zipkintracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZipkinTracingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinTracingApplication.class, args);
	}
}
