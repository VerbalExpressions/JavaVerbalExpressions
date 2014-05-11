package ru.lanwen.verbalregex;

import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
                equalTo("^(?:http)(?:s)?(?:\\:\\/\\/)(?:www\\.)?(?:[^\\ ]*)$"));
    }

    @Test
    public void testTelephoneNumber() {
        VerbalExpression regex = VerbalExpression.regex()
                .startOfLine()
                .then("+")
                .capture().range("0", "9").count(3).maybe("-").maybe(" ").endCapture()
                .count(3)
                .endOfLine().build();

        String phoneWithSpace = "+097 234 243";
        String phoneWithoutSpace = "+097234243";
        String phoneWithDash = "+097-234-243";

        assertThat(regex.testExact(phoneWithSpace), is(true));
        assertThat(regex.testExact(phoneWithoutSpace), is(true));
        assertThat(regex.testExact(phoneWithDash), is(true));

    }
}
