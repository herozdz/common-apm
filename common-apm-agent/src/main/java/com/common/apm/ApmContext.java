package com.common.apm;

import com.common.apm.collects.JdbcCommonCollects;
import com.common.apm.collects.ServiceCollect;
import com.common.apm.filter.JSONFormat;
import com.common.apm.output.JulOutput;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-3
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 * apm 上下文
 */
public class ApmContext {
    private Instrumentation instrumentation;
    private Properties properties;
    List<ICollect> iCollects = new ArrayList<ICollect>();
    IFilter iFilter;
    IOutput iOutput;

    public ApmContext(Instrumentation instrumentation, Properties properties) {

        this.checkProperties(properties);
        this.instrumentation = instrumentation;
        this.properties = properties;
        //注册采集器
        iCollects.add(new ServiceCollect(instrumentation,this));
        iCollects.add(new JdbcCommonCollects(instrumentation,this));
        //filter 注册
        iFilter = new JSONFormat();

        iOutput = new JulOutput();
    }

    //提交采集结果
    public void submitCollectResult(Object value){
        //基于线程后台执行
        value = iFilter.doFilter(value);
        System.out.println("common-apm-log:"+value);
        String logPath =  properties.getProperty("logPath");
        iOutput.out(value,logPath);
    }

    public String getConfig(String key){
        return properties.getProperty(key);
    }

    public List<ICollect> getiCollects() {
        return iCollects;
    }

    public void checkProperties(Properties properties){
        if(properties == null){
            throw new RuntimeException("properties is null,common-apm app Can't monitor the app！");
        }

        if(null == properties.getProperty("appKey")){
            throw new RuntimeException("the Key-->'appKey' in properties are not allowed to be empty！");
        }

        if(null == properties.getProperty("logPath")){
            throw new RuntimeException("the Key-->'logPath' in properties are not allowed to be empty！");
        }

    }
}
