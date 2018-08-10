package com.common.apm.test;

import com.common.apm.ApmContext;
import com.common.apm.collects.JdbcCommonCollects;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;

public class JdbcCollectTest {
	private ApmContext context;
	private MockInstrumentation instrumentation;
	private JdbcCommonCollects jdbcCollect;

	@Ignore
	@Before
	public void init() {
		instrumentation = new MockInstrumentation();
		Properties pro=new Properties();
		pro.put("service.include","com.tuling.apm1.*&com.common.apm.test.*");
		pro.put("service.exclude","com.tuling.apm.test1.*");
		pro.put("logPath","/export/Instances/item.vcpserv/server1/logs/");
		pro.put("appKey","testApp");

		context = new ApmContext(instrumentation,pro);
		jdbcCollect = new JdbcCommonCollects(instrumentation,context);
	}

	@Ignore
	@Test
	public void sqlTest() throws Exception {
		String name = "com.mysql.jdbc.NonRegisteringDriver";
		byte[] classBytes = jdbcCollect.transform(
				ServiceCollectTest.class.getClassLoader(), name);
		ClassPool pool = new ClassPool();
		pool.insertClassPath(new ByteArrayClassPath(name, classBytes));
		pool.get(name).toClass();
		Class.forName(name);
		Connection conn = DriverManager
				.getConnection(
						"jdbc:mysql://192.168.166.17:3306/vc_item?useUnicode=true&;characterEncoding=UTF8",
						"test_write", "test_write123");


		PreparedStatement statment2 = conn
				.prepareStatement("update free_lunch set creator = ? where bus_id = ?");
		statment2.setString(1,"zoudezhu");
		statment2.setString(2,"2144ac70-d4c0-4ff2-95b1-96680a7b5c33");
		statment2.executeUpdate();
		statment2.close();

		PreparedStatement statment = conn
				.prepareStatement("select * from free_lunch where bus_id = ?");
		statment.setString(1,"2144ac70-d4c0-4ff2-95b1-96680a7b5c33");
		statment.executeQuery();
		statment.close();

		PreparedStatement statment3 = conn
				.prepareStatement("select * from free_lunch where bus_id = ?");
		statment3.setString(1,"2144ac70-d4c0-4ff2-95b1-96680a7b5c33");
		statment3.executeQuery();
		statment3.close();

		conn.close();

	}
}
