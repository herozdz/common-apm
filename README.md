# common-apm （通用APM系统）  

通过字节码插装技术，来监控类中的方法调用情况，按照配置使用即可，零代码侵入。

### 目前该工程已实现功能:  

#### 1、采集指定路径类方法调用情况（不包含入参，返回结果）  

{"begin":1533783358148,"end":1533783358148,"useTime":0,"serviceName":"com.common.apm.test.TestServiceImpl","simpleName":".TestServiceImpl","methodName":"getUser","recordTime":1533783358148,"modelType":"service","hostIp":"","hostName":"","appKey":"zdzApp"}  

#### 2、采集数据库sql执行情况（目前支持mysql数据库驱动的监控）：
{"begin":1533800300511,"end":1533800300605,"useTime":94,"jdbcUrl":"jdbc:mysql://192.168.*.17:3306/daabase?<br>useUnicode=true&;characterEncoding=UTF8","databaseName":"database","dbUsername":"test_write@192.168.*.16","sql":"update free_lunch set creator = ? where bus_id = ?","params":[{"index":1,"value":"zoudezhu"},{"index":2,"value":"2144ac70-d4c0-4ff2-95b1-96680a7b5c33"}],"sqlRes":"1","preman":"1","recordTime":0,"modelType":"jdbc","appKey":"testApp"}
#### 将来要实现功能：
采集各厂家的数据库连接池。

### 使用：
#### 1、将项目打包：apm-agent-1.0-SNAPSHOT.jar
   *切记打包的时候将common-apm-agent/pom.xml 中测试类使用的jar注释掉，以防与调用应用中jar冲突，引起类污染。
   之所以提代码的时候没有注释掉，是应为测试类中需要用到，本地测试的时候需要使用。
#### 2、安装jar包
    1）如果你们公司有maven私服，将jar上传至maven私服。
    2）如果在本地测试，可以使用maven命令讲jar安装到本地。
    mvn install:install-file -DgroupId=com.apm -DartifactId=apm-agent -Dversion=1.0-SNAPSHOT -Dpackaging=jar -Dfile=/data/zoudezhu/tmp/apm-agent-1.0-SNAPSHOT.jar
#### 3、配置项目pom.xml
``` gradle
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
  ```
#### 4、 启动tomcat服务
 vm 参数添加：
 -javaagent:**/WEB-INF/lib/apm-agent-1.0-SNAPSHOT.jar=service.include=com.impl.*&com.service.impl.*,appKey=zdzApp,logPath=/path/server1/logs/
 
 #### -javaagent参数说明：  
 
 核心jar全路径：是maven打包在/WEB-INF/lib/下的apm-agent-1.0-SNAPSHOT.jar  例如 :**/WEB-INF/lib/apm-agent-1.0-SNAPSHOT.jar    
 
 agent接受参数：各个参数使用逗号隔开，参数类别如下:  
 
 service.include（非必填：如果不填写就不会监听） ：监控哪些包下面的类，包名可以使用通配符，多个包路径使用&字符隔开。  
 
 service.exclude（非必填） ：排除监控哪些包下面的类，包名可以使用通配符，多个包路径使用&字符隔开。  
 
 appKey（必填）:被监控项目代码。  
 
 logPath（必填）：日志的输出路径。  
 
 
 #### 注释：
 sql的监控是监控java.sql.Driver接口中的java.sql.Connection 接口 和 java.sql.PreparedStatement 接口，mysql驱动对应：com.mysql.jdbc.NonRegisteringDriver。  
 common-agent默认会监听com.mysql.jdbc.NonRegisteringDriver这个类，如果启动应用用到mysql驱动，在运行相关sql的时候便会把日志写到对应Log路径里面。
 
 
