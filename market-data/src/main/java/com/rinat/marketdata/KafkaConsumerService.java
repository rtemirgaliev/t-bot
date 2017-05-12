package com.rinat.marketdata;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    public static Logger LOG = LoggerFactory.getLogger(KafkaConsumerService.class);


    @KafkaListener(topics = "test")
    public void listen(ConsumerRecord<?, ?> cr) {
        LOG.info(cr.value().toString());
    }



}
