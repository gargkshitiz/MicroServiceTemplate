package com.org.fms.account.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

/**
 * @author Kshitiz Garg
 * For Hysterix, class needs to be a @Service or @Component. Refer https://spring.io/guides/gs/circuit-breaker/
 */
@Service
public class HysterixDemoService {
	
	private RestTemplate restTemplate;
	
	@Autowired
	public HysterixDemoService(RestTemplateBuilder restTemplate){
		this.restTemplate = restTemplate.build();
	}
	
	@HystrixCommand(fallbackMethod = "hysterixFallBackMethod")
	public String demoHysterixFallBack() {
		URI uri = URI.create("http://localhost:7897/nonExistentServer");
	    String response = this.restTemplate.getForObject(uri, String.class);
		return response;
	}

	public String hysterixFallBackMethod() {
		return "hysterixFallBackMethod called";
	}

}