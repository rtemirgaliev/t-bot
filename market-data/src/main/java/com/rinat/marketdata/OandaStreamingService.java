package com.rinat.marketdata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class OandaStreamingService {

    protected static final Logger LOG = LoggerFactory.getLogger(OandaStreamingService.class);

    protected static final BasicHeader UNIX_DATETIME_HEADER = new BasicHeader("X-Accept-Datetime-Format", "UNIX");
    protected volatile boolean serviceUp = true;
    protected Thread streamThread;

    @Autowired
    private Environment env;

    @Autowired
    private KafkaProducerService kafkaProducerService;



    protected BufferedReader setUpStreamIfPossible(CloseableHttpClient httpClient) throws IOException {
        String uri = env.getProperty("provider.oanda.uri_stream") + "?"
                + env.getProperty("provider.oanda.instruments");
        uri = uri.replace("<account_id>", env.getProperty("provider.oanda.account_id"));
        String header = "Bearer " +
                env.getProperty("provider.oanda.token");

        HttpUriRequest httpGet = new HttpGet(uri);
        httpGet.setHeader("Authorization", header);
        httpGet.setHeader(UNIX_DATETIME_HEADER);

        LOG.info("Executing request" + httpGet);

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK && entity != null) {
            InputStream stream = entity.getContent();
            serviceUp = true;
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            return br;
        } else {
            String responseString = EntityUtils.toString(entity, "UTF8");
            LOG.warn(responseString);
            return null;
        }
    }

    public void startMarketDataStreaming() {

        ObjectMapper mapper = new ObjectMapper();

        LOG.info("Logging - Starting new thread for stream processing...");
        streamThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                try {
                    BufferedReader br = setUpStreamIfPossible(httpClient);
                    if (br != null) {
                        String line;
                        while ( (line = br.readLine())!=null && serviceUp ) {

                            kafkaProducerService.sendStringToTopic("test", line);

//                            JsonNode node = mapper.readValue(line, JsonNode.class);
//                            String messageType = node.get("type").asText();
//                            System.out.println("Type: " + messageType);
//
//                            if ("PRICE".equals(messageType)) {
//                                JsonNode bidsNode = node.get("bids");
//                                JsonNode asksNode = node.get("asks");
//                                String bid = bidsNode.get(0).get("price").asText();
//                                String asc = asksNode.get(0).get("price").asText();
//                                System.out.println(asc + " " + bid);
//                            }

                        }
                        br.close();
                    }


//                    if(br != null) {
//                        String line;
//                        while ( (line = br.readLine())!=null && serviceUp ) {
//                            Object obj = JSONValue.parse(line);
//                            JSONObject instrumentTick = (JSONObject) obj;
//                            // unwrap if necessary
//                            if (instrumentTick.containsKey("tick")) {
//                                instrumentTick = (JSONObject) instrumentTick.get("tick");
//                            }
//
//                            if (instrumentTick.containsKey("instrument")) {
//                                final String instrument = instrumentTick.get("instrument").toString();
//                                final String timeAsString = instrumentTick.get("time").toString();
////                                final Long eventTime = Long.parseLong(timeAsString);
////                                final double bidPrice = ((Number)instrumentTick.get("bid")).doubleValue();
////                                final double ascPrice = ((Number)instrumentTick.get("asc")).doubleValue();
//
//                                System.out.println("Tick Data: " + instrument + " " + timeAsString);
//                            } else if (instrumentTick.containsKey("heartbeat")) {
//                                System.out.println("Heartbeat");
//                            } else if (instrumentTick.containsKey("disconnect")) {
//                                System.out.println("Disconnect");
//                            }
//                        }
//                        br.close();
//                    }

                } catch (Exception e) {
                    LOG.error("error encountered inside market data streaming thread", e);
                } finally {

                    serviceUp = false;
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, "O_Stream_Thread");
        streamThread.start();
    }

}
