package com.sdnelson.msc.research.lcf4j.nodemgmt.websocksts.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketsApp {

    public static void main(String[] args) throws Exception {


        ExecutorService executorService = Executors.newFixedThreadPool(100);

                for(int i = 0 ; i < 99; ++i) {
                    try {
                        executorService.execute(new Runnable() {
                            public void run() {
                                try {
                                    WebSocketClient webSocketClient = new WebSocketClient();
                                    webSocketClient.startClient();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
    }
}
