package com.cargomanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class CargoManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CargoManagementApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println(
            "\n=============================================\n" +
            "  Cargo Management System server is running. \n" +
            "  Visit: http://localhost:8080\n" +
            "=============================================\n"
        );
    }
}