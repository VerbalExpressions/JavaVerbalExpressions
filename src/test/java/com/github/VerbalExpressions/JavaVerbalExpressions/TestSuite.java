package com.github.VerbalExpressions.JavaVerbalExpressions;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

//JUnit Suite Test
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
   BasicFunctionalityUnitTests.class,
   RealWorldUnitTests.class
})
public class TestSuite {

}