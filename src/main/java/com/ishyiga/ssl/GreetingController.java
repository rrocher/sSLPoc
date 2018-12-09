package com.ishyiga.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Just says hello
 */
@RestController
public class GreetingController {
 
	Logger logger = LoggerFactory.getLogger(GreetingController.class);
	 
    private static final String template = "Hello, %s!";

    @RequestMapping("/greeting")
    public Greeting greet(
            @RequestParam(value = "name", required = false, defaultValue = "World!") String name) {
        return new Greeting(String.format(template, name));
    }   
    
    @RequestMapping(value="/greeting2",
            produces=MediaType.APPLICATION_XML_VALUE)
    public Greeting greetxml(
            @RequestParam(value = "name", required = false, defaultValue = "World!") String name) {
    	logger.info("Api called with param [{}}", name);
        return new Greeting(String.format(template, name));
    }

}
