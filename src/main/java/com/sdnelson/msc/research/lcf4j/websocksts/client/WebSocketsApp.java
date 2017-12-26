package com.sdnelson.msc.research.lcf4j.websocksts.client;

import com.sdnelson.msc.research.lcf4j.websocksts.client.WebSocketClient;
import io.netty.util.concurrent.ThreadPerTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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
