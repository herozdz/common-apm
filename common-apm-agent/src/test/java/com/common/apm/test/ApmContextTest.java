package com.common.apm.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created with IntelliJ IDEA.
 * User: zoudezhu
 * Date: 18-8-6
 * Time: 下午5:36
 * To change this template use File | Settings | File Templates.
 */
public class ApmContextTest {

    public static void main(String[] args) throws Exception {
        ApmContextTest test = new ApmContextTest();
        test.serviceTest();
        test.sqlTest();
    }

    public void serviceTest() {
        TestServiceImpl service = new TestServiceImpl();
        service.getUser("111", "hanmei");
    }

    public void sqlTest() throws Exception {
        String name = "com.mysql.jdbc.Driver";
        Class.forName(name);
        Connection conn = DriverManager
                .getConnection(
                        "jdbc:mysql://192.168.166.17:3306/vc_item?useUnicode=true&amp;characterEncoding=UTF8 ",
                        "test_write", "test_write123");
        PreparedStatement statment = conn
                .prepareStatement("select * from free_lunch");
        statment.executeQuery();
        statment.close();
        conn.close();
    }
}
