package com.liamlang.fyp.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {

    public static InetAddress toIp(String str) throws UnknownHostException {
        return InetAddress.getByName(str);
    }
}
