package com.common.apm.collects;

import com.common.apm.ApmContext;
import com.common.apm.ICollect;
import com.common.apm.common.HostUtil;
import com.common.apm.model.JdbcStatistics;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-7
 * Time: 下午2:45
 * To change this template use File | Settings | File Templates.
 * jdbc 数据采集
 */
public class JdbcCommonCollects extends AbstractByteTransformCollect implements ICollect{

    public static JdbcCommonCollects INSTANCE;
    private ApmContext context;

    public JdbcCommonCollects(Instrumentation instrumentation, ApmContext context) {
        super(instrumentation);
        this.context = context;
        INSTANCE = this;
    }

    private final static String[] connection_agent_methods = new String[]{"prepareStatement"};
    private final static String[] prepared_statement_methods = new String[]{"execute","executeUpdate","executeQuery","getResultSet"};
    private final static String beginSrc;
    private final static String endSrc;
    private final static String errorSrc;

    static{
        beginSrc = "com.common.apm.collects.JdbcCommonCollects inst = com.common.apm.collects.JdbcCommonCollects.INSTANCE;";
        errorSrc = "inst.error(null,e);";
        //将 java.sql.Connection 动态代理
        endSrc   = "result=inst.proxyConnection((java.sql.Connection)result);";
    }

    public JdbcStatistics begin(String className, String method){
        JdbcStatistics jdbcStatistics = new JdbcStatistics();
        jdbcStatistics.begin=System.currentTimeMillis();
        jdbcStatistics.setRecordTime(System.currentTimeMillis());
        jdbcStatistics.setHostIp(HostUtil.getHostIp());
        jdbcStatistics.setHostName(HostUtil.getHostName());
        jdbcStatistics.setModelType("jdbc");
        jdbcStatistics.setAppKey(context.getConfig("appKey"));
        return jdbcStatistics;
    }

    public void end(JdbcStatistics stat,Object result){
        JdbcStatistics jdbcStat = (JdbcStatistics) stat;
        jdbcStat.end=System.currentTimeMillis();
        jdbcStat.useTime=jdbcStat.end-jdbcStat.begin;
        if(jdbcStat.jdbcUrl != null){
            jdbcStat.databaseName = getDbName(jdbcStat.jdbcUrl);
        }
        this.buildSqlRes(jdbcStat,result);
        this.context.submitCollectResult(stat);
    }

    /**
     * 设置preparedment执行sql返回的sql结果
     * */
    public void buildSqlRes(JdbcStatistics jdbcStatistics,Object result){
        /**
         * 监控该3个方法
         * boolean execute()
         * ResultSet executeQuery()
         * int executeUpdate()
         * */
        if(null != result){
            if(result instanceof ResultSet){
                jdbcStatistics.sqlRes = result.toString();
                /**
                 * 有需求可以扩展对ResultSet的设置
                 *
                 * */
            }else {
                /**boolean int 类型直接返回*/
                jdbcStatistics.sqlRes = result.toString();
            }
        }


    }

    public void error(JdbcStatistics stat, Throwable throwable){
        if(stat != null){
            stat.error = throwable.getMessage();
            stat.errorType = throwable.getClass().getName();
            if(throwable instanceof InvocationTargetException){
                stat.error = ((InvocationTargetException) throwable).getTargetException().getMessage();
                stat.errorType = ((InvocationTargetException) throwable).getTargetException().getClass().getName();
            }
        }
    }

    public Connection proxyConnection(final Connection connection){
        Object o = Proxy.newProxyInstance(JdbcCommonCollects.class.getClassLoader(),
                new Class[]{Connection.class},new ConnectionHandler(connection));
        return (Connection) o;
    }

    public PreparedStatement proxyPreparedStatement(final PreparedStatement statement, JdbcStatistics jdbcStatistics){
        Object o = Proxy.newProxyInstance(JdbcCommonCollects.class.getClassLoader(),
                new Class[]{PreparedStatement.class},new PreparedStatementHandler(statement,jdbcStatistics));
        return (PreparedStatement)o;
    }

    /**
     * 代理connection
     * */
    public class ConnectionHandler implements InvocationHandler{
        private final Connection connection;

        public ConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isTargetMethod = false;
            for(String agentm: connection_agent_methods){
                if(agentm.equals(method.getName())){
                    isTargetMethod = true;
                    break;
                }
            }
            Object result = null;
            JdbcStatistics jdbcStatistics = null;
            try{
                if(isTargetMethod){
                    jdbcStatistics = (JdbcStatistics)JdbcCommonCollects.this.begin(null,null);
                    jdbcStatistics.jdbcUrl = connection.getMetaData().getURL();
                    jdbcStatistics.dbUsername = connection.getMetaData().getUserName();
                    jdbcStatistics.sql = (String)args[0];
                }
                result = method.invoke(connection,args);
                //代理 PreparedStatement
                if(isTargetMethod && result instanceof PreparedStatement){
                    PreparedStatement ps = (PreparedStatement) result;
                    result = proxyPreparedStatement(ps,jdbcStatistics);
                }
            }catch (Throwable e){
                JdbcCommonCollects.this.error(jdbcStatistics,e);
                JdbcCommonCollects.this.end(jdbcStatistics,null);
                throw e;
            }
            return result;
        }
    }

    /**
     * 代理 PreparedStatement
     * */
    public class PreparedStatementHandler implements InvocationHandler{
        private final PreparedStatement statement;
        private final JdbcStatistics jdbcStatistics;

        public PreparedStatementHandler(PreparedStatement statement, JdbcStatistics jdbcStatistics) {
            this.statement = statement;
            this.jdbcStatistics = jdbcStatistics;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            boolean isTargetMethod = false;
            for(String agentm : prepared_statement_methods){
                if(agentm.equals(method.getName())){
                    isTargetMethod = true;
                    break;
                }
            }
            Object result = null;
            try{
                /**
                 * 扩展设置参数
                 * */
                if("set".equals(method.getName().substring(0,3))){
                    this.jdbcStatistics.params.add(new JdbcStatistics.ParamValues(args));
                    /**... ...*/
                }
                //如果参数进行了预处理设置设置字段1
                jdbcStatistics.preman = "1";

                result = method.invoke(statement,args);

            }catch (Throwable e){
                if(isTargetMethod){
                    JdbcCommonCollects.this.error(jdbcStatistics,e);
                }
                throw e;
            }finally {
                if(isTargetMethod){
                    JdbcCommonCollects.this.end(jdbcStatistics,result);
                }
            }
            return result;
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className) throws Exception {
        if(!className.equals("com.mysql.jdbc.NonRegisteringDriver")){
            return null;
        }
        CtClass ctClass = super.toCtClass(loader,className);
        AgentByteBuild byteLoade = new AgentByteBuild(className,loader,ctClass);
        /**
         * 获取connect方法，并向connect方法插装，
         * com.mysql.jdbc.NonRegisteringDriver.connect  --->
         * public Connection connect(String url, Properties info) throws SQLException
        */
        CtMethod ctMethod = ctClass.getMethod("connect","(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
        AgentByteBuild.MethodSrcBuild build = new AgentByteBuild.MethodSrcBuild();
        build.setBeginSrc(beginSrc);
        build.setErrorSrc(errorSrc);
        build.setEndSrc(endSrc);
        byteLoade.updateMethod(ctMethod,build);
        return byteLoade.toBytecote();
    }



    private static String getDbName(String url) {
        int index = url.indexOf("?"); //$NON-NLS-1$
        if (index != -1) {
            String paramString = url.substring(index + 1, url.length());
            url = url.substring(0, index);
        }
        String dbName = url.substring(url.lastIndexOf("/") + 1);
        return dbName;
    }
}
