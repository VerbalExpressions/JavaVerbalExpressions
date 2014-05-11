package ru.lanwen.verbalregex;

import org.junit.Test;

import java.util.regex.PatternSyntaxException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: lanwen
 * Date: 11.05.14
 * Time: 3:37
 */
public class NegativeCasesTest {

    @Test(expected = IllegalStateException.class)
    public void testEndCaptureOnEmptyRegex() {
        VerbalExpression.regex().endCapture().build();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldExceptionWhenTryGetMoreThanCapturedGroup() {
        String text = "abc";
        VerbalExpression regex = VerbalExpression.regex().find("b").capture().find("c").build();

        regex.getText(text, 2);
    }

    @Test(expected = PatternSyntaxException.class)
    public void testRangeWithoutArgs() throws Exception {
        VerbalExpression.regex().startOfLine().range().build();
    }

    @Test(expected = PatternSyntaxException.class)
    public void testRangeWithOneArg() throws Exception {
        VerbalExpression.regex().startOfLine().range("a").build();
    }

    @Test
    public void rangeWithThreeArgsUsesOnlyFirstTwo() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().startOfLine().range("a", "z", "A").build();

        assertThat("Range with three args differs from expected", regex.toString(), equalTo("^[a-z]"));
    }

    @Test
    public void orWithNullMatchesAny() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().startOfLine().then("a").or(null).build();
        assertThat("regex don't matches writed letter", regex.test("a"), is(true));
        assertThat("or(null) should match any", regex.test("bcd"), is(true));

        assertThat("or(null) extract only first", regex.getText("abcd"), equalTo("a"));
    }

    @Test
    public void orAfterCaptureProduceEmptyGroup() throws Exception {
        VerbalExpression regex = VerbalExpression.regex().startOfLine().then("a").capture().or("b").build();

        assertThat(regex.toString(), containsString("()|"));

        assertThat("regex dont matches string abcd", regex.getText("abcd", 0), equalTo("a"));
        assertThat("regex dont extract a by first group", regex.getText("abcd", 1), equalTo("a"));
        assertThat("second group should produce empty string", regex.getText("abcd", 2), equalTo(""));

    }
}
