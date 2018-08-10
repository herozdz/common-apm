package com.common.apm.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Tommy on 2018/3/11.
 */
public class HostUtil {
    public  static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getHostName(){
        try {
            return InetAddress.getLocalHost().getHostName().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }
}
