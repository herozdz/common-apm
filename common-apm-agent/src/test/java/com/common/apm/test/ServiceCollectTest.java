package com.common.apm.test;

import com.common.apm.ApmContext;
import com.common.apm.collects.ServiceCollect;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import org.junit.Before;
import org.junit.Test;

import java.lang.instrument.Instrumentation;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-6
 * Time: 上午10:33
 * To change this template use File | Settings | File Templates.
 */
public class ServiceCollectTest {
    ApmContext context ;
    private ServiceCollect serviceCollect;

    private Instrumentation instrumentation;
    @Before
    public void init() {
        instrumentation = new MockInstrumentation();
        Properties pro=new Properties();
        pro.put("service.include","com.tuling.apm1.*&com.common.apm.test.*");
        pro.put("service.exclude","com.tuling.apm.test1.*");
        context = new ApmContext(instrumentation,pro);
        serviceCollect = new ServiceCollect(instrumentation,context);
    }

    @Test
    public void collectTest() throws Exception {
        String name = "com.common.apm.test.TestServiceImpl";
        byte[] classBytes = serviceCollect.transform(ServiceCollectTest.class.getClassLoader(),name);
        ClassPool pool = new ClassPool();
        pool.insertClassPath(new ByteArrayClassPath(name,classBytes));
        pool.get(name).toClass();
        new TestServiceImpl().getUser("zdz:id","zoudezhu");
    }

}
