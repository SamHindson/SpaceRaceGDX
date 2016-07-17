package com.semdog.spacerace.misc;

import com.esotericsoftware.kryonet.Client;

import java.net.InetAddress;

/**
 * Created by Sam on 2016/07/17.
 */
public class DiscoverTest {
    public static void main(String[] args) {
        Client client = new Client();
        InetAddress address = client.discoverHost(24488, 5000);
        System.out.println(address);
    }
}
