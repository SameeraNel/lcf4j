package com.sdnelson.msc.research.lcf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App {

    public static void main( String[] args ) throws Exception {

        final Properties properties = loadConfigFile();
        new UptimeServer().startServer(8060);

    }

    private static Properties loadConfigFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            String filename = "lcf4j.properties";
            input = App.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                System.out.println("Sorry, unable to find " + filename);
                return null;
            }

            prop.load(input);
            System.out.println(prop.getProperty("lcf4j.nodes.list"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally{
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
