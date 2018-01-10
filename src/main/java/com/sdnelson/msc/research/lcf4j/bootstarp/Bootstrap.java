package com.sdnelson.msc.research.lcf4j.bootstarp;

import com.sdnelson.msc.research.lcf4j.nodemgmt.multicast.MulticastServer;

import java.net.InetSocketAddress;

/**
 * Created by SDN on 1/4/2018.
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        InetSocketAddress groupAddress = new InetSocketAddress("239.255.27.1", 1234);
        new MulticastServer(groupAddress).run();
    }

}
