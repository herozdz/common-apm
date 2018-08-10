package com.common.apm.test;

/**
 * Created by Tommy on 2018/3/8.
 */
public class TestServiceImpl {
    // 1、拷拷贝目标方法重命名为方法后面加$agent
    // 2、拼装监控语句，并放至原方法

    public void getUser(String userid,String name) {
        // 1
        // 2
        System.out.println("获取用户信息:" + userid);
    }

    /**   插装后的代码表现为
         1, public void getUser$agent(String userid,String name) {
                System.out.println("获取用户信息:" + userid);
            }

         2,1, public void getUser(String userid,String name) {
            com.common.apm.collects.ServiceCollect instance =
            com.common.apm.collects.ServiceCollect.INSTANCE;
            com.common.apm.model.ServiceStatistics statistic = instance.begin("com.*.TestServiceImpl","getUser");
               try {
                    getUser$agent(userid,name);
               } catch (Throwable e) {
                    instance.error(statistic,e);
                    throw e;
               }finally{
                    instance.end(statistic);
               }
             }
     *
     * */


}
