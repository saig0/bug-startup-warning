package com.example.demo;

import io.camunda.client.annotation.Deployment;
import io.camunda.client.annotation.JobWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Deployment(resources = "classpath*:**/*.bpmn")
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@JobWorker
	public void helloWorld() {

	}

}
