package com.sdnelson.msc.research.lcf4j.bootstarp;

import com.sdnelson.msc.research.lcf4j.nodemgmt.multicast.MulticastServer;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;


public class Bootstrap {

    final static Logger logger = Logger.getLogger(Bootstrap.class);

    public static void main(String[] args) throws Exception {
        InetSocketAddress groupAddress = new InetSocketAddress("239.255.27.1", 11209);
        final MulticastServer multicastServer = new MulticastServer(groupAddress);
        multicastServer.start();
    }

}
