package com.common.apm.collects;

import com.common.apm.ApmContext;
import com.common.apm.ICollect;
import com.common.apm.common.WildcardMatcher;
import com.common.apm.model.ServiceStatistics;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-3
 * Time: 下午5:18
 * To change this template use File | Settings | File Templates.
 */
public class ServiceCollect extends AbstractByteTransformCollect implements ICollect {
    public static ServiceCollect INSTANCE;
    private final ApmContext context;
    private WildcardMatcher excludeMatcher = null;//排除非哪些类
    private WildcardMatcher includeMatcher = null;//包含哪些类
    private String include;
    private String exclude;

    private static final String beginSrc;
    private static final String endSrc;
    private static final String errorSrc;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("com.common.apm.collects.ServiceCollect instance = ");
        sb.append("com.common.apm.collects.ServiceCollect.INSTANCE;\r\n");
        sb.append("com.common.apm.model.ServiceStatistics statistic = instance.begin(\"%s\",\"%s\");");
        beginSrc = sb.toString();
        sb = new StringBuilder();
        sb.append("instance.end(statistic);");
        endSrc = sb.toString();
        sb = new StringBuilder();
        sb.append("instance.error(statistic,e);");
        errorSrc = sb.toString();
    }

    public ServiceCollect(Instrumentation instrumentation, ApmContext context) {
        super(instrumentation);
        this.context = context;
        if(context.getConfig("service.include") != null){
            includeMatcher = new WildcardMatcher(context.getConfig("service.include"));
        }else {
            System.err.println("[error]未配置 'service.include'参数无法监控service服务方法");
        }

        if(context.getConfig("service.exclude") !=null){
            excludeMatcher = new WildcardMatcher(context.getConfig("service.exclude"));
        }
        INSTANCE = this;
    }

    /**
     * 构建代理方法开头
     * */
    public ServiceStatistics begin(String className, String methodName){
        ServiceStatistics bean = new ServiceStatistics();
        bean.setRecordTime(System.currentTimeMillis());
        bean.setHostIp("");
        bean.setHostName("");
        bean.setBegin(System.currentTimeMillis());
        bean.setServiceName(className);
        bean.setMethodName(methodName);
        bean.setSimpleName(className.substring(className.lastIndexOf(".")));
        bean.setModelType("service");
        bean.setAppKey(context.getConfig("appKey"));
        return bean;
    }

    /**
     * 构建代理方法异常信息
     * */
    public void error(ServiceStatistics bean, Throwable e){
        bean.setErrorType(e.getClass().getSimpleName());
        bean.setErrorMsg(e.getMessage());
    }

    public void end(ServiceStatistics bean){
        bean.setEnd(System.currentTimeMillis());
        bean.setUseTime(bean.end - bean.begin);
        context.submitCollectResult(bean);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className) throws Exception {

        if(className.contains("CGLIB$$")){//排除代理类，com.×.ServiceImpl$$FastClassByCGLIB$$46b90760
            return null;
        }

        if(includeMatcher == null){
            return null;
        }else if(!includeMatcher.matches(className)){//包涵指定类
            return null;
        }else if(excludeMatcher != null && excludeMatcher.matches(className)){//排除指定类
            return null;
        }

        CtClass ctClass = toCtClass(loader,className);
        AgentByteBuild byteLoade = new AgentByteBuild(className,loader,ctClass);
        CtMethod[] methods =  ctClass.getDeclaredMethods();
        for (CtMethod m : methods){
            // 屏蔽非公共方法
            if (!Modifier.isPublic(m.getModifiers())){
                continue;
            }

            //屏蔽静态方法
            if (Modifier.isStatic(m.getModifiers())){
                continue;
            }

            // 屏蔽本地方法
            if (Modifier.isNative(m.getModifiers())){
                continue;
            }
            AgentByteBuild.MethodSrcBuild build = new AgentByteBuild.MethodSrcBuild();
            build.setBeginSrc(String.format(beginSrc,className ,m.getName()));
            build.setEndSrc(endSrc);
            build.setErrorSrc(errorSrc);
            byteLoade.updateMethod(m,build);
        }
        return byteLoade.toBytecote();
    }
}
