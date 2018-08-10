package com.common.apm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-3
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 * agent入口
 */
public class ApmAgent {
    public static void premain(String arg, Instrumentation instrumentation){
        System.out.println("------->agent begin!!!");
        Properties properties = new Properties();
        //装载配置文件（监控的类）
        if(arg != null && !arg.trim().equals("")){
            try {
                properties.load(new ByteArrayInputStream(arg.replaceAll(",","\n").getBytes()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ApmContext context = new ApmContext(instrumentation,properties);
        System.out.println("------->agent end!!!");
    }
}
