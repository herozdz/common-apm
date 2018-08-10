package com.common.apm.collects;

import javassist.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-6
 * Time: 下午3:17
 * To change this template use File | Settings | File Templates.
 */
public class AgentByteBuild {
    private final String className;
    private final ClassLoader loader;
    private final CtClass ctClass;

    public AgentByteBuild(String className, ClassLoader loader, CtClass ctClass) {
        this.className = className;
        this.loader = loader;
        this.ctClass = ctClass;
    }

    /**
     * 插入监听方法
     * */
    public void updateMethod(CtMethod method,MethodSrcBuild srcBuild) throws CannotCompileException {
        CtMethod ctMethod = method;
        String methodName = method.getName();
        /**
         * 基于原来方法复制生成代理方法
         * */
        CtMethod agentMethod = CtNewMethod.copy(ctMethod,methodName,ctClass,null);
        agentMethod.setName(methodName + "$agent");
        ctClass.addMethod(agentMethod);

        /**
         * 原方法重置为代理执行
         * */
        ctMethod.setBody(srcBuild.buildSrc(ctMethod));
    }

    /**
     * 生成新的class 字节码 ，
     *
     * @return
     * @throws NotFoundException
     * @throws Exception
     */
    public byte[] toBytecote() throws IOException, CannotCompileException {
        return ctClass.toBytecode();
    }

    public static class MethodSrcBuild{
        private String beginSrc;
        private String endSrc;
        private String errorSrc;

        public MethodSrcBuild setBeginSrc(String beginSrc){
            this.beginSrc = beginSrc;
            return this;
        }
        public MethodSrcBuild setEndSrc(String endSrc){
            this.endSrc = endSrc;
            return this;
        }

        public MethodSrcBuild setErrorSrc(String errorSrc){
            this.errorSrc = errorSrc;
            return this;
        }

        public String buildSrc(CtMethod method){
            String result;
            try {
                String template = method.getReturnType().getName().equals("void") ? voidSource : source;
                String bSrc = beginSrc == null ? "" : beginSrc;
                String eSrc = errorSrc == null ? "" : errorSrc;
                String enSrc = endSrc ==null ? "" : endSrc;
                return String.format(template,bSrc,method.getName(),eSrc,enSrc);
            } catch (NotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        final static String source = "{\n"
                + "%s"
                + "        Object result=null;\n"
                + "       try {\n"
                + "            result=($w)%s$agent($$);\n"
                + "        } catch (Throwable e) {\n"
                + "%s"
                + "            throw e;\n"
                + "        }finally{\n"
                + "%s"
                + "        }\n"
                + "        return ($r) result;\n"
                + "}\n";

        final static String voidSource = "{\n"
                + "%s"
                + "        try {\n"
                + "            %s$agent($$);\n"
                + "        } catch (Throwable e) {\n"
                + "%s"
                + "            throw e;\n"
                + "        }finally{\n"
                + "%s"
                + "        }\n"
                + "}\n";
    }
}
