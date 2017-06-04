package com.org.fms.account.service;

import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Service
/**
 * @author Kshitiz Garg
 * For Kafka demo
 */
public class KafkaDemoService {
 
	protected Logger logger = LoggerFactory.getLogger(KafkaDemoService.class);
	
	public final CountDownLatch countDownLatch1 = new CountDownLatch(1);
 
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	public void demoKafka(){
		 kafkaTemplate.send("topic1", "ABC");
		 logger.info("Sent message to kafka");
	}
	
	@KafkaListener(id = "foo", topics = "topic1", group = "group1")
	public void listen(ConsumerRecord<?, ?> record) {
		logger.info("Received message from kafka");
		countDownLatch1.countDown();
	}
 
}