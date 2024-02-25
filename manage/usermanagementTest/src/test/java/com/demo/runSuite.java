package com.demo;

import com.demo.seleniumTest.ListTest;
import com.demo.seleniumTest.LoginTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Created with IntelliJ IDEA.
 * Description
 * User: Administrator
 * Date: 2024-02-23
 * Time: 18:27
 */
@Suite
@SelectClasses({ListTest.class, LoginTest.class})
public class runSuite {
}
