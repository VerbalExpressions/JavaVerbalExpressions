package verbal.expressions;

import static org.junit.Assert.*;

import org.junit.Test;

import verbal.expressions.VerbalExpression;

public class RealWorldUnitTests {

    @Test
    public void testUrl() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("http")
                .maybe("s")
                .then("://")
                .maybe("www.")
                .anythingButNot(" ")
                .endOfLine()
                .build();

        // Create an example URL
        String testUrl = "https://www.google.com";
        assertTrue("Matches Google's url", testRegex.test(testUrl)); //True

        assertEquals("Regex doesn't match same regex as in example", testRegex.toString(), "^(http)(s)?(\\:\\/\\/)(www\\.)?([^\\ ]*)$");
    }
}
