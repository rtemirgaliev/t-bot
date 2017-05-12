package com.rinat.marketdata;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class KafkaProducerServiceTests {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Test
    public void sendStringTest() {
        kafkaProducerService.sendStringToTopic("test", "Message 1");
    }
}
