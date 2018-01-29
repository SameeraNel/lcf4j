package com.sdnelson.msc.research.lcf4j.reference.http;

import com.sdnelson.msc.research.lcf4j.core.ClusterNode;
import com.sdnelson.msc.research.lcf4j.util.ClusterConfig;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {

    final static Logger logger = Logger.getLogger(App.class);
    private static ExecutorService serverListener = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        new App().initConfigProperties();
        logger.info(ClusterConfig.getNodeServerPortSsl() + " Started, ok.");
        new UptimeServer().startServer(ClusterConfig.getNodeServerPortSsl());

        logger.info("Nodes list found - " + ClusterConfig.getClusterNodeList().toString());

            for (ClusterNode nodeServer : ClusterConfig.getClusterNodeList()) {
                    Runnable runnableTask = () -> {
                        try {
                            new Client(nodeServer.getHost(), nodeServer.getPort());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                serverListener.execute(runnableTask);
            }

        logger.info("ok.");


    }

    private void initConfigProperties() {
        logger.debug("Loading Properties to Config ...");
        try{
            ClusterConfig.initClusterConfig();
        } catch (Exception ex){
            logger.error("Invalid Properties Found, Error Loading the Property File ...");
            System.exit(0);
        }
        logger.info("Successfully Loaded the Properties to Config ...");
    }
}
