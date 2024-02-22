package com.example.demo;

import com.example.demo.seleniumTest.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-01-02
 * Time: 20:32
 */
//测试套件
@Suite
@SelectClasses({ListTest.class, AddTest.class, EditTest.class, DetailTest.class,CenterTest.class,UnLoginTest.class,LoginTest.class,RegTest.class})
//@SelectClasses({ListTest.class})
public class runSuite {
}
