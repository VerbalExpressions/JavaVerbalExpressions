package ru.lanwen.verbalregex;

import org.junit.Test;

import java.util.regex.PatternSyntaxException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.lanwen.verbalregex.VerbalExpression.regex;
import static ru.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;

/**
 * User: lanwen
 * Date: 11.05.14
 * Time: 3:37
 */
public class NegativeCasesTest {

    @Test(expected = IllegalStateException.class)
    public void testEndCaptureOnEmptyRegex() {
        regex().endCapture().build();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldExceptionWhenTryGetMoreThanCapturedGroup() {
        String text = "abc";
        VerbalExpression regex = regex().find("b").capture().find("c").build();

        regex.getText(text, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionWhenTryGetByNonExistentCaptureName() {
        String text = "abc";
        VerbalExpression regex = regex().find("b")
                .capture("test1").find("c").build();

        regex.getText(text, "test2");
    }

    @Test(expected = PatternSyntaxException.class)
    public void testRangeWithoutArgs() throws Exception {
        regex().startOfLine().range().build();
    }

    @Test(expected = PatternSyntaxException.class)
    public void testRangeWithOneArg() throws Exception {
        regex().startOfLine().range("a").build();
    }

    @Test
    public void rangeWithThreeArgsUsesOnlyFirstTwo() throws Exception {
        VerbalExpression regex = regex().startOfLine().range("a", "z", "A").build();

        assertThat("Range with three args differs from expected", regex.toString(), equalTo("^[a-z]"));
    }

    @Test
    public void orWithNullMatchesAny() throws Exception {
        VerbalExpression regex = regex().startOfLine().then("a").or(null).build();
        assertThat("regex don't matches writed letter", regex, matchesTo("a"));
        assertThat("or(null) should match any", regex, matchesTo("bcd"));

        assertThat("or(null) extract only first", regex.getText("abcd"), equalTo("a"));
    }

    @Test
    public void orAfterCaptureProduceEmptyGroup() throws Exception {
        VerbalExpression regex = regex().startOfLine().then("a").capture().or("b").build();

        assertThat(regex.toString(), containsString("()|"));

        assertThat("regex dont matches string abcd", regex.getText("abcd", 0), equalTo("a"));
        assertThat("regex dont extract a by first group", regex.getText("abcd", 1), equalTo(""));
    }

    @Test
    public void orAfterNamedCaptureProduceEmptyGroup() {
        String captureName = "test";
        VerbalExpression regex = regex().startOfLine().then("a")
                .capture(captureName).or("b").build();

        assertThat(regex.toString(), containsString("(?<test>)|"));

        assertThat("regex don't matches string abcd",
                regex.getText("abcd", 0), equalTo("a"));
        assertThat("regex don't extract a by group named " + captureName,
                regex.getText("abcd", captureName), equalTo(""));
    }

    @Test
    public void multiplyWithNullOnCountEqualToWithOneAndMore() throws Exception {
        VerbalExpression regex = regex().multiple("some", null).build();

        assertThat("Multiply with null should be equal to oneOrMore",
                regex.toString(), equalTo(regex().oneOrMore("some").build().toString()));
    }

    @Test
    public void multiplyWithMoreThan3ParamsOnCountEqualToWithOneAndMore() throws Exception {
        VerbalExpression regex = regex().multiple("some", 1, 2, 3).build();

        assertThat("Multiply with 3 args should be equal to oneOrMore",
                regex.toString(), equalTo(regex().oneOrMore("some").build().toString()));
    }

    @Test(expected = java.util.regex.PatternSyntaxException.class)
    public void twoOpenCaptsWithOrThrowSyntaxException() throws Exception {
        VerbalExpression regex = regex().capt().capt().or("0").build();
        String ignored = regex.toString();
    }
}
