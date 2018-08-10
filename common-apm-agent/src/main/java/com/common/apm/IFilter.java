package com.common.apm;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-3
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 * 过滤器
 */
public interface IFilter {
    public Object doFilter(Object value);
}
