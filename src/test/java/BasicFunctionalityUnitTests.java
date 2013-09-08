import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

import static org.junit.Assert.*;

public class BasicFunctionalityUnitTests {
   
   @Test
   public void testSomething() {
      VerbalExpression testRegex = new VerbalExpression ().something();
      String testString = "";

      assertFalse("empty string doesn't have something", testRegex.test(testString));
      testString = "a";
      assertTrue("a", testRegex.test(testString));
   }

   @Test
   public void testAnything() {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .anything();
      String testString = "what";
      assertTrue(testRegex.test(testString));
   }

   @Test
   public void testAnythingBut() {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .anythingBut("w");
      String testString = "what";
      assertTrue("starts with w",testRegex.test(testString));
   }

   @Test
   public void testSomethingBut() {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .somethingBut("a");
      String testString = "";
      assertFalse("empty string doesn't have something", testRegex.test(testString));

      testString = "b";
      assertTrue("doesn't start with a", testRegex.test(testString));
      
      testString = "a";
      assertFalse("starts with a", testRegex.test(testString));
   }

   @Test
   public void testStartOfLine() {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("a");
      String testString = "a";
      assertTrue("Starts with a", testRegex.test(testString));
      testString = "ba";
      assertFalse("Doesn't start with a", testRegex.test(testString));
   }

   @Test
   public void testEndOfLine () {
       VerbalExpression testRegex = new VerbalExpression ()
                                        .find("a")
                                        .endOfLine();
       String testString = "a";

       
       assertTrue("Ends with a", testRegex.test(testString));

       testString = "ab";
       assertFalse("Doesn't end with a", testRegex.test(testString));
   }

   @Test
   public void testMaybe () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("a")
                                       .maybe("b");
      
      assertEquals("Regex isn't correct", testRegex.toString(), "^(a)(b)?");
      String testString = "acb";

      assertTrue("Maybe has a 'b' after an 'a'", testRegex.test(testString));
      testString = "abc";
      assertTrue("Maybe has a 'b' after an 'a'", testRegex.test(testString));
   }

   @Test
   public void testAnyOf () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("a")
                                       .anyOf("xyz");
      String testString = "ay";       
      assertTrue("Has an x, y, or z after a", testRegex.test(testString));
      
      testString = "abc";
      assertFalse("Doesn't have an x, y, or z after a", testRegex.test(testString));
   }

   @Test
   public void testOr () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("abc")
                                       .or("def");
      String testString = "defzzz";
      assertTrue("Starts with abc or def", testRegex.test(testString));

      testString = "xyzabc";
      assertFalse("Doesn't start with abc or def", testRegex.test(testString));
   }

   @Test
   public void testLineBreak () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("abc")
                                       .lineBreak()
                                       .then("def");
      String testString = "abc\r\ndef";
      assertTrue("abc then line break then def", testRegex.test(testString));

      testString = "abc\ndef";
      assertTrue("abc then line break then def", testRegex.test(testString));

      testString = "abc\r\n def";
      assertFalse("abc then line break then space then def", testRegex.test(testString));
   }

   @Test
   public void testBr () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("abc")
                                       .lineBreak()
                                       .then("def");
      String testString = "abc\r\ndef";
      assertTrue("abc then line break then def", testRegex.test(testString));

      testString = "abc\ndef";
      assertTrue("abc then line break then def", testRegex.test(testString));

      testString = "abc\r\n def";
      assertFalse("abc then line break then space then def", testRegex.test(testString));
   }

   @Test
   public void testTab () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .tab()
                                       .then("abc");
      String testString = "\tabc";
      assertTrue("tab then abc", testRegex.test(testString));

      testString = "abc";
      assertFalse("no tab then abc", testRegex.test(testString));
   }

   @Test
   public void testWithAnyCase () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("a");
      String testString = "A";
      assertFalse("not case insensitive", testRegex.test(testString));
      testRegex = new VerbalExpression ()
                      .startOfLine()
                      .then("a")
                      .withAnyCase();
      testString = "A";
      assertTrue("case insensitive", testRegex.test(testString));

      testString = "a";
      assertTrue("case insensitive", testRegex.test(testString));
   }

   @Test
   public void testSearchOneLine () {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("a")
                                       .br()
                                       .then("b")
                                       .endOfLine();
      String testString = "a\nb";
      assertTrue("b is on the second line", testRegex.test(testString));

      testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("a")
                                       .br()
                                       .then("b")
                                       .endOfLine()
                                       .searchOneLine(true);
      testString = "a\nb";
      assertTrue("b is on the second line but we are only searching the first", testRegex.test(testString));
   }
   
   
   @Test
   public void testGetText () {
       String testString = "123 https://www.google.com 456";
       VerbalExpression testRegex = new VerbalExpression().add("http")
               .maybe("s").then("://").then("www.").anythingBut(" ")
               .add("com");
       assertEquals(testRegex.getText(testString), "https://www.google.com");
       
   }

}