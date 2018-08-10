package com.common.apm.filter;

import com.common.apm.IFilter;
import com.common.apm.common.JsonUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-6
 * Time: 下午2:18
 * To change this template use File | Settings | File Templates.
 */
public class JSONFormat implements IFilter {
    @Override
    public Object doFilter(Object value) {
        if(value == null){
            return null;
        }else if(!(value instanceof Serializable)){
            return null;
        }
        return JsonUtil.toJson(value);
    }
}
