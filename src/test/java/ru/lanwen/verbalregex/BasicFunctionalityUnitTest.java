package ru.lanwen.verbalregex;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class BasicFunctionalityUnitTest {
    @Test
    public void testSomething() {
        VerbalExpression testRegex = new VerbalExpression.Builder().something().build();

        assertFalse("Null object doesn't have something", testRegex.test(null));
        assertFalse("empty string doesn't have something", testRegex.test(""));
        assertTrue("a", testRegex.test("a"));
    }

    @Test
    public void testAnything() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .anything()
                .build();

        assertTrue(testRegex.test("what"));
        assertFalse(testRegex.test(""));
        assertTrue(testRegex.test(" "));
    }

    @Test
    public void testAnythingBut() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .anythingButNot("w")
                .build();

        assertFalse("starts with w", testRegex.testExact("what"));
        assertTrue("Not contain w", testRegex.testExact("that"));
        assertTrue("Not contain w", testRegex.testExact(" "));
        assertFalse("Null object", testRegex.testExact(null));
    }

    @Test
    public void testSomethingBut() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .somethingButNot("a")
                .build();

        assertFalse("Null string", testRegex.testExact(null));
        assertFalse("empty string doesn't have something", testRegex.testExact(""));
        assertTrue("doesn't contain a", testRegex.testExact("b"));
        assertFalse("Contain a", testRegex.testExact("a"));
    }

    @Test
    public void testStartOfLine() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .build();

        assertFalse("Null string", testRegex.testExact(null));
        assertFalse("empty string doesn't have something", testRegex.testExact(""));
        assertTrue("Starts with a", testRegex.test("a"));
        assertTrue("Starts with a", testRegex.test("ab"));
        assertFalse("Doesn't start with a", testRegex.test("ba"));
    }

    @Test
    public void testStartOfLineFalse() {
        VerbalExpression testRegex = VerbalExpression.regex()
                .startOfLine(false)
                .then("a")
                .build();
        assertThat(testRegex.test("ba"), is(true));
        assertThat(testRegex.test("ab"), is(true));
    }

    @Test
    public void testRangeWithMultiplyRanges() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().range("a", "z", "A", "Z").build();

        assertThat("Regex with multi-range differs from expected", regex.toString(), equalTo("[a-zA-Z]"));
        assertThat("Regex don't matches letter", regex.test("b"), is(true));
        assertThat("Regex matches digit, but should match only letter", regex.test("1"), is(false));
    }

    @Test
    public void testEndOfLine() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .find("a")
                .endOfLine()
                .build();

        assertTrue("Ends with a", testRegex.test("bba"));
        assertTrue("Ends with a", testRegex.test("a"));
        assertFalse("Ends with a", testRegex.test(null));
        assertFalse("Doesn't end with a", testRegex.test("ab"));
    }


    @Test
    public void testEndOfLineIsFalse() {
        VerbalExpression testRegex = VerbalExpression.regex()
                .find("a")
                .endOfLine(false)
                .build();
        assertThat(testRegex.test("ba"), is(true));
        assertThat(testRegex.test("ab"), is(true));
    }


    @Test
    public void testMaybe() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .maybe("b")
                .build();

        assertThat("Regex isn't correct", testRegex.toString(), equalTo("^(?:a)(?:b)?"));

        assertTrue("Maybe has a 'b' after an 'a'", testRegex.test("acb"));
        assertTrue("Maybe has a 'b' after an 'a'", testRegex.test("abc"));
        assertFalse("Maybe has a 'b' after an 'a'", testRegex.test("cab"));
    }

    @Test
    public void testAnyOf() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .anyOf("xyz")
                .build();

        assertTrue("Has an x, y, or z after a", testRegex.test("ay"));
        assertFalse("Doesn't have an x, y, or z after a", testRegex.test("abc"));
    }


    @Test
    public void testAnySameAsAnyOf() {
        VerbalExpression any = VerbalExpression.regex().any("abc").build();
        VerbalExpression anyOf = VerbalExpression.regex().anyOf("abc").build();

        assertThat("any differs from anyOf", any.toString(), equalTo(anyOf.toString()));
    }

    @Test
    public void testOr() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .or("def")
                .build();

        assertTrue("Starts with abc or def", testRegex.test("defzzz"));
        assertFalse("Doesn't start with abc or def", testRegex.test("xyzabc"));
    }

    @Test
    public void testLineBreak() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .lineBreak()
                .then("def")
                .build();

        assertTrue("abc then line break then def", testRegex.test("abc\r\ndef"));
        assertTrue("abc then line break then def", testRegex.test("abc\ndef"));
        assertFalse("abc then line break then space then def", testRegex.test("abc\r\n def"));
    }

    @Test
    public void testBr() {
        VerbalExpression testRegexBr = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .br()
                .then("def")
                .build();

        VerbalExpression testRegexLineBr = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .lineBreak()
                .then("def")
                .build();

       assertThat(".br() differs from .lineBreak()", testRegexBr.toString(), equalTo(testRegexLineBr.toString()));
    }

    @Test
    public void testTab() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .tab()
                .then("abc")
                .build();

        assertTrue("tab then abc", testRegex.test("\tabc"));
        assertFalse("no tab then abc", testRegex.test("abc"));
    }

    @Test
    public void testWithAnyCase() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .build();

        assertFalse("not case insensitive", testRegex.test("A"));
        testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .withAnyCase()
                .build();

        assertTrue("case insensitive", testRegex.test("A"));
        assertTrue("case insensitive", testRegex.test("a"));
    }

    @Test
    public void testWithAnyCaseIsFalse() {
        VerbalExpression testRegex = VerbalExpression.regex()
                .withAnyCase()
                .startOfLine()
                .then("a")
                .withAnyCase(false)
                .build();

        assertThat(testRegex.test("A"), is(false));
    }

    @Test
    public void testSearchOneLine() {
        VerbalExpression testRegex = VerbalExpression.regex()
                .startOfLine()
                .then("a")
                .br()
                .then("b")
                .endOfLine()
                .build();

        assertTrue("b is on the second line", testRegex.test("a\nb"));

        testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .br()
                .then("b")
                .endOfLine()
                .searchOneLine(true)
                .build();

        assertTrue("b is on the second line but we are only searching the first", testRegex.test("a\nb"));
    }

    @Test
    public void testGetText() {
        String testString = "123 https://www.google.com 456";
        VerbalExpression testRegex = new VerbalExpression.Builder().add("http")
                .maybe("s")
                .then("://")
                .then("www.")
                .anythingButNot(" ")
                .add("com").build();
        assertEquals(testRegex.getText(testString), "https://www.google.com");

    }

    @Test
    public void testStartCapture() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex()
                .find("a").count(3)
                .capture().find("b").anything().build();

        assertThat("regex don't match string", regex.getText(text), equalTo(text));
        assertThat("can't get first captured group", regex.getText(text, 1), equalTo("bcd"));
    }

    @Test
    public void shouldReturnEmptyStringWhenNoGroupFound() {
        String text = "abc";
        VerbalExpression regex = VerbalExpression.regex().find("d").capture().find("e").build();

        assertThat("regex don't match string", regex.getText(text), equalTo(""));
        assertThat("first captured group not empty string", regex.getText(text, 1), equalTo(""));
        assertThat("second captured group not empty string", regex.getText(text, 2), equalTo(""));
    }

    @Test
    public void testCountWithRange() {
        String text4c = "abcccce";
        String text2c = "abcce";
        String text1c = "abce";

        VerbalExpression regex = VerbalExpression.regex().find("c").count(2, 3).build();

        assertThat("regex don't match string", regex.getText(text4c), equalTo("ccc"));
        assertThat("regex don't match string", regex.getText(text2c), equalTo("cc"));
        assertThat("regex don't match string", regex.test(text1c), is(false));
    }

   @Test
    public void testEndCapture() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex()
                .find("a")
                .capture().find("b").anything().endCapture().then("cd").build();

        assertThat(regex.getText(text), equalTo("abcd"));
        assertThat("can't get first captured group", regex.getText(text, 1), equalTo("b"));
    }


    @Test
    public void testMultiplyCapture() {
        String text = "aaabcd";
        VerbalExpression regex = VerbalExpression.regex()
                .find("a").count(1)
                .capture().find("b").endCapture().anything().capture().find("d").build();

        assertThat("can't get first captured group", regex.getText(text, 1), equalTo("b"));
        assertThat("can't get second captured group", regex.getText(text, 2), equalTo("d"));
    }
    @Test
    public void testOrWithCapture() {
        VerbalExpression testRegex = VerbalExpression.regex()
                .capture()
                .find("abc")
                .or("def")
                .build();
        assertTrue("Starts with abc or def", testRegex.test("defzzz"));
        assertTrue("Starts with abc or def", testRegex.test("abczzz"));
        assertFalse("Doesn't start with abc or def", testRegex.testExact("xyzabcefg"));

        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcdef"));
        assertThat(testRegex.getText("xxxdefzzz", 2), equalTo("null"));
        assertThat(testRegex.getText("xxxabcdefzzz", 2), equalTo("abcnull"));
    }

}