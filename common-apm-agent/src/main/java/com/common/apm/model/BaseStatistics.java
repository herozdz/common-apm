package com.common.apm.model;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-6
 * Time: 上午10:01
 * To change this template use File | Settings | File Templates.
 */
public class BaseStatistics implements Serializable {
    private long recordTime;
    private String modelType;
    private String hostIp;
    private String hostName;
    private String traceId;
    private String appKey;

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BaseStatistics{");
        sb.append("recordTime=").append(recordTime);
        sb.append(", modelType='").append(modelType).append('\'');
        sb.append(", hostIp='").append(hostIp).append('\'');
        sb.append(", hostName='").append(hostName).append('\'');
        sb.append(", traceId='").append(traceId).append('\'');
        sb.append(", appKey='").append(appKey).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
