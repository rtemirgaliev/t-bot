package com.rinat.marketdata;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedReader;

public class OandaStreamingService {

    protected BufferedReader setUpStreamIfPossible(CloseableHttpClient httpClient) {
        HttpUriRequest httpGet = new HttpGet("https://stream-fxpractice.oanda.com/v3/accounts/101-004-5956477-001/pricing/stream&instruments=EUR_USD ");
        httpGet.setHeader("Authorization", "Bearer 0b401ca1950dc6078b8805d903a3b7f7-228a67e3d766c8faf7ebaae633a96637");






    }

}
