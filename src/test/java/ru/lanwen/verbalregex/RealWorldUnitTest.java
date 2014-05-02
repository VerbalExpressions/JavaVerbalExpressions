package ru.lanwen.verbalregex;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class RealWorldUnitTest {

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

        assertThat("Regex doesn't match same regex as in example",
                testRegex.toString(),
                equalTo("^(http)(s)?(\\:\\/\\/)(www\\.)?([^\\ ]*)$"));
    }
}
