package com.common.apm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-7
 * Time: 下午2:39
 * To change this template use File | Settings | File Templates.
 */
public class JdbcStatistics extends BaseStatistics {
    public Long begin;//
    public Long end;
    public Long useTime;
    public String jdbcUrl;
    public String databaseName;
    //数据库用户名
    public String dbUsername;
    public String sql;
    //sql参数
    public ArrayList<ParamValues> params = new ArrayList<ParamValues>();
    //sql结果
    public String sqlRes;
    public String error;
    public String errorType;
    //sql执行计划
    public String sqlExplant;
    //是否经过预处理
    public String preman;
    //扩展字段
    public List<String> exts;

    public JdbcStatistics() {
    }

    public static class ParamValues{
        public int index;
        public Object value;

        public ParamValues(int index, Object value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ParamValues{");
            sb.append("index=").append(index);
            sb.append(", value=").append(value);
            sb.append('}');
            return sb.toString();
        }
    }
}
