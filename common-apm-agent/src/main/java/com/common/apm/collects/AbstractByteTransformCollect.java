package com.common.apm.collects;

import com.common.apm.output.JulOutput;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-3
 * Time: 下午5:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractByteTransformCollect {
    private static Map<ClassLoader, ClassPool> classPoolMap = new ConcurrentHashMap<ClassLoader, ClassPool>();
    private static Logger logger = Logger.getLogger(JulOutput.class.getName());

    public AbstractByteTransformCollect(Instrumentation instrumentation) {
        //插装核心方法
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if(loader == null){
                    return null;
                }
                if(className == null){
                    return null;
                }
                className = className.replaceAll("/",".");

                try {
                    return AbstractByteTransformCollect.this.transform(loader,className);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.log(Level.SEVERE,"class transform error:",e);
                }
                return null;
            }
        });
    }

    //插装
    public abstract byte[] transform(ClassLoader loader,String className) throws Exception;

    protected static CtClass toCtClass(ClassLoader loader,String className) throws NotFoundException {
        if(!classPoolMap.containsKey(loader)){
            ClassPool classPool = new ClassPool();
            classPool.insertClassPath(new LoaderClassPath(loader));
            classPoolMap.put(loader,classPool);
        }
        ClassPool cp = classPoolMap.get(loader);
        className = className.replaceAll("/",".");
        return cp.get(className);
    }


}
