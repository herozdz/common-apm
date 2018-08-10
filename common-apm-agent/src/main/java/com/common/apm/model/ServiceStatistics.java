package com.common.apm.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-6
 * Time: 上午10:01
 * To change this template use File | Settings | File Templates.
 */
public class ServiceStatistics extends BaseStatistics implements Serializable {
    public Long begin;
    public Long end;
    public Long useTime;
    public String errorMsg;
    public String errorType;
    public String serviceName;//服务名称
    public String simpleName;//服务简称
    public String methodName;//方法名称
    public List<String> exts;//扩展字段

    public Long getBegin() {
        return begin;
    }

    public void setBegin(Long begin) {
        this.begin = begin;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getUseTime() {
        return useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ServiceStatistics{");
        sb.append("begin=").append(begin);
        sb.append(", end=").append(end);
        sb.append(", useTime=").append(useTime);
        sb.append(", errorMsg='").append(errorMsg).append('\'');
        sb.append(", errorType='").append(errorType).append('\'');
        sb.append(", serviceName='").append(serviceName).append('\'');
        sb.append(", simpleName='").append(simpleName).append('\'');
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
