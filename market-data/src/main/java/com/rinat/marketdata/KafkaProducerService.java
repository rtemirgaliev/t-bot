package com.rinat.marketdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    public static Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendString(String str) {
        this.kafkaTemplate.send("oandaMarketData", str);
    }

    public void sendStringToTopic(String topic, String str) {
        this.kafkaTemplate.send(topic, str);
    }


}
