package com.ishyiga.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
	static Logger logger = LoggerFactory.getLogger(App.class);

	 public static void main(String[] args) {
	        SpringApplication.run(App.class, args);
		 	logger.info("Server started");
	    }
}
