# common-apm （该项目设计思路来源于图灵学院-鲁班老师）
通过字节码插装技术，来监控类中的方法调用情况。
目前该工程已实现功能：
1、采集指定路径类方法调用结果。
{"begin":1533783358148,"end":1533783358148,"useTime":0,"serviceName":"com.common.apm.test.TestServiceImpl","simpleName":".TestServiceImpl","methodName":"getUser","recordTime":1533783358148,"modelType":"service","hostIp":"","hostName":"","appKey":"zdzApp"}
2、采集数据库sql执行情况：
{"begin":1533800300511,"end":1533800300605,"useTime":94,"jdbcUrl":"jdbc:mysql://192.168.*.17:3306/daabase?useUnicode=true&;characterEncoding=UTF8","databaseName":"database","dbUsername":"test_write@192.168.*.16","sql":"update free_lunch set creator = ? where bus_id = ?","params":[{"index":1,"value":"zoudezhu"},{"index":2,"value":"2144ac70-d4c0-4ff2-95b1-96680a7b5c33"}],"sqlRes":"1","preman":"1","recordTime":0,"modelType":"jdbc","appKey":"testApp"}
将来要实现功能：
采集各厂家的数据库连接池。

使用：
1、将项目打包：apm-agent-1.0-SNAPSHOT.jar
2、将jar上传至git私服
3、配置项目pom.xml
<dependency>
   <groupId>com.apm</groupId>
   <artifactId>apm-agent</artifactId>
   <version>1.0-SNAPSHOT</version>
  </dependency>

  <dependency>
   <groupId>org.javassist</groupId>
   <artifactId>javassist</artifactId>
   <version>3.18.1-GA</version>
  </dependency>
 4、 启动tomcat服务
 vm 参数添加：
 -javaagent:**/WEB-INF/lib/apm-agent-1.0-SNAPSHOT.jar=service.include=com.impl.*&com.service.impl.*,appKey=zdzApp,logPath=/path/server1/logs/
 
 -javaagent参数说明：
 核心jar全路径：是maven打包在/WEB-INF/lib/下的apm-agent-1.0-SNAPSHOT.jar  例如 :**/WEB-INF/lib/apm-agent-1.0-SNAPSHOT.jar  
 agent接受参数：各个参数使用逗号隔开，参数类别如下：
 service.include（非必填） ：监控哪些包下面的类，包名可以使用通配符，多个包路径使用&字符隔开。
 service.exclude（非必填） ：排除监控哪些包下面的类，包名可以使用通配符，多个包路径使用&字符隔开。
 appKey（必填）:被监控项目代码。
 logPath（必填）：日志的输出路径。
 
 
