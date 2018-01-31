package com.sdnelson.msc.research.lcf4j.util;


import com.sdnelson.msc.research.lcf4j.core.ClusterNode;
import com.sdnelson.msc.research.lcf4j.exception.PropertyException;
import io.netty.util.internal.StringUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class ClusterConfig {

    final static org.apache.log4j.Logger logger = Logger.getLogger(ClusterConfig.class);

    private static final String LCF4J_PROPERTIES = "lcf4j.properties";
    private static final String CLUSTER_NODE_LIST = "cluster.node.list";
    private static final String NODE_SERVER_NAME = "node.server.name";
    private static final String NODE_SERVER_PORT = "node.server.port";
    private static final String SERVER_MASTER_THREADPOOL_SIZE = "server.master.threadpool.size";
    private static final String SERVER_SLAVE_THREADPOOL_SIZE = "server.slave.threadpool.size";
    private static final String CLIENT_SLAVE_THREADPOOL_SIZE = "client.slave.threadpool.size";
    private static final String NODE_SERVER_PORT_SSL = "node.server.port.ssl";
    private static final String CLIENT_DATA_SYNC_INTERVAL = "client.data.sync.interval";
    private static final String SERVER_REFRESH_INTERVAL = "server.refresh.interval";


    private static Properties properties;
    private static List<ClusterNode> clusterNodeList;
    private static String nodeServerName;
    private static int nodeServerPort;
    private static int nodeServerPortSsl;
    private static int serverMasterThreadpoolSize;
    private static int serverSlaveThreadpoolSize;
    private static int clientSlaveThreadpoolSize;
    private static int clientDataSyncInterval;
    private static int serverRefreshInterval;

    public static void initClusterConfig() throws Exception {
        properties = loadConfigFile();
        loadClusterNodeList();
        loadNodeServerName();
        loadNodeServerPortSsl();
        loadServerMasterThreadpoolSize();
        loadServerSlaveThreadpoolSize();
        loadClientSlaveThreadpoolSize();
        loadClientDataSyncInterval();
        loadServerRefreshInterval();
    }

    private static void loadServerRefreshInterval() {
        serverRefreshInterval = Integer.valueOf(properties.getProperty(SERVER_REFRESH_INTERVAL));
    }

    private static void loadClientDataSyncInterval() {
        clientDataSyncInterval = Integer.valueOf(properties.getProperty(CLIENT_DATA_SYNC_INTERVAL));
    }

    private static void loadNodeServerPortSsl() {
        nodeServerPortSsl = Integer.valueOf(properties.getProperty(NODE_SERVER_PORT_SSL));
    }

    private static void loadClusterNodeList() {
        final String[] nodeListString = properties.getProperty(CLUSTER_NODE_LIST).split(",");
        clusterNodeList = new ArrayList<>();
        for(String nodeString : nodeListString){
            final String[] splitString = nodeString.split(":");
            ClusterNode clusterNode = new ClusterNode(
                    splitString[0], Integer.valueOf(splitString[1]));
            clusterNodeList.add(clusterNode);
        }
    }

    private static void loadNodeServerName() throws PropertyException{
        nodeServerName = properties.getProperty(NODE_SERVER_NAME);
        if(StringUtil.isNullOrEmpty(nodeServerName)){
            throw new PropertyException("Null or empty value found for [ " + NODE_SERVER_NAME + " ]." );
        }
    }

    private static void loadNodeServerPort() throws Exception{
        nodeServerPort = Integer.valueOf(properties.getProperty(NODE_SERVER_PORT));
    }

    private static void loadServerMasterThreadpoolSize() throws Exception{
        serverMasterThreadpoolSize = Integer.valueOf(properties.getProperty(SERVER_MASTER_THREADPOOL_SIZE));
    }

    private static void loadServerSlaveThreadpoolSize() throws Exception{
        serverSlaveThreadpoolSize = Integer.valueOf(properties.getProperty(SERVER_SLAVE_THREADPOOL_SIZE));
    }

    private static void loadClientSlaveThreadpoolSize()  throws Exception{
        clientSlaveThreadpoolSize = Integer.valueOf(properties.getProperty(CLIENT_SLAVE_THREADPOOL_SIZE));
    }

    public static List<ClusterNode> getClusterNodeList() {
        return clusterNodeList;
    }

    public static String getNodeServerName() {
        return nodeServerName;
    }

    public static int getNodeServerPort() {
        return nodeServerPort;
    }

    public static int getNodeServerPortSsl() {
        return nodeServerPortSsl;
    }

    public static int getServerMasterThreadpoolSize() {
        return serverMasterThreadpoolSize;
    }

    public static int getServerSlaveThreadpoolSize() {
        return serverSlaveThreadpoolSize;
    }

    public static int getClientSlaveThreadpoolSize() {
        return clientSlaveThreadpoolSize;
    }

    public static int getClientDataSyncInterval() {
        return clientDataSyncInterval;
    }

    public static int getServerRefreshInterval() {
        return serverRefreshInterval;
    }

    private static Properties loadConfigFile() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            String filename = LCF4J_PROPERTIES;
            input = ClusterConfig.class.getClassLoader().getResourceAsStream(filename);
            if(input==null){
                logger.error("Sorry, unable to find " + filename);
                return null;
            }
            prop.load(input);
            logger.debug("Property file loaded successfully.");

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
