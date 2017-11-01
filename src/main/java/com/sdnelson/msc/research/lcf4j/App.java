package com.sdnelson.msc.research.lcf4j;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class App {

    final static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws Exception {

        final Properties properties = loadConfigFile();
        final String property = properties.getProperty("lcf4j.nodes.list");
        final List<String> nodeList = Arrays.asList(property.split(","));
        new UptimeServer().startServer(Integer.valueOf(properties.getProperty("lcf4j.server.port")));

        logger.info("Nodes list found - " + nodeList);
        for (String node : nodeList) {
            node = node.trim();
            logger.info("Node " + node);
            new Client(
                    node.split(":")[0].trim(), Integer.valueOf(node.split(":")[1].trim()).intValue());
        }
        logger.info("ok.");

    }

    private static Properties loadConfigFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "lcf4j.properties";
            input = App.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                logger.error("Sorry, unable to find " + filename);
                return null;
            }

            prop.load(input);
            logger.info("Property file loaded successfully.");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return prop;
    }
}
