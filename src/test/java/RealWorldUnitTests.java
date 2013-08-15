import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class RealWorldUnitTests {
   
   @Test
   public void testUrl() {
      VerbalExpression testRegex = new VerbalExpression ()
                                       .startOfLine()
                                       .then("http")
                                       .maybe("s")
                                       .then("://")
                                       .maybe("www.")
                                       .anythingBut(" ")
                                       .endOfLine();

      // Create an example URL
      String testUrl = "https://www.google.com";
      assertTrue("Matches Google's url",testRegex.test(testUrl)); //True

      assertEquals("Regex doesn't match same regex as in example", testRegex.toString(), "^(http)(s)?(\\:\\/\\/)(www\\.)?([^\\ ]*)$");
   }
}