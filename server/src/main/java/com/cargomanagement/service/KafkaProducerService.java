package com.cargomanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        try {
            System.out.printf("Producing message -> %s to topic -> %s%n", message, topic);
            this.kafkaTemplate.send(topic, message);
            System.out.println("Message sent successfully");
        } catch (Exception e) {
            System.err.println("Failed to send Kafka message: " + e.getMessage());
            // Doesn't throw the exception - just logs it
        }
    }
}
