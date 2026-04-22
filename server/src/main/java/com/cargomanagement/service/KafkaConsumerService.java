package com.cargomanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public KafkaConsumerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @KafkaListener(topics = "shipment-events", groupId = "supply-chain-group")
    public void consumeShipmentEvents(String message) {
        System.out.println("Consumed shipment event: " + message);
        // Send the message to WebSocket clients subscribed to /topic/shipments
        messagingTemplate.convertAndSend("/topic/shipments", message);
    }

    @KafkaListener(topics = "delivery-events", groupId = "supply-chain-group")
    public void consumeDeliveryEvents(String message) {
        System.out.println("Consumed delivery event: " + message);
        messagingTemplate.convertAndSend("/topic/deliveries", message);
    }

    @KafkaListener(topics = "route-events", groupId = "supply-chain-group")
    public void consumeRouteEvents(String message) {
        System.out.println("Consumed route event: " + message);
        messagingTemplate.convertAndSend("/topic/routes", message);
    }

    @KafkaListener(topics = "cargo-events", groupId = "supply-chain-group")
    public void consumeCargoEvents(String message) {
        System.out.println("Consumed cargo event: " + message);
        messagingTemplate.convertAndSend("/topic/cargo", message);
    }

    @KafkaListener(topics = "vendor-events", groupId = "supply-chain-group")
    public void consumeVendorEvents(String message) {
        System.out.println("Consumed vendor event: " + message);
        messagingTemplate.convertAndSend("/topic/vendors", message);
    }
}
