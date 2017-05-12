package com.rinat.marketdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;

import java.applet.AppletContext;

@SpringBootApplication
public class MarketDataApplication {

	@Autowired
	OandaStreamingService oandaStreamingService;

//	@Autowired
//	private KafkaTemplate<String, String> template;


	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(MarketDataApplication.class, args);

		context.getBean(OandaStreamingService.class).startMarketDataStreaming();
	}
}
