package com.iot_edge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class IotEdgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotEdgeApplication.class, args);
		log.info("Iot-Edge Application has started...");
	}

}
