package com.sdnelson.msc.research.lcf4j.Bootstrap;

import com.sdnelson.msc.research.lcf4j.nodemgmt.multicast.MulticastServer;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        InetSocketAddress groupAddress = new InetSocketAddress("239.255.27.2", 11101);
        new MulticastServer(groupAddress).run();
    }
}
