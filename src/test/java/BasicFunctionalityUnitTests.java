
import static org.junit.Assert.*;
import org.junit.Test;

public class BasicFunctionalityUnitTests {

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
    public void testMaybe() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .maybe("b")
                .build();

        assertEquals("Regex isn't correct", testRegex.toString(), "^(a)(b)?");

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
    public void testSearchOneLine() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
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
}
