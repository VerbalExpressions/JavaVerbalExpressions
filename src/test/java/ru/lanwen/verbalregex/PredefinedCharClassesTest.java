package run.lanwen.verbalregex;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static run.lanwen.verbalregex.VerbalExpression.regex;
import static run.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;

/**
 * User: lanwen
 * Date: 13.05.14
 * Time: 16:26
 */
public class PredefinedCharClassesTest {

    public static final String LETTERS_NO_DIGITS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_";
    public static final String DIGITS = "0123456789";
    public static final String NON_LETTERS = ";'[]{}|?/";
    public static final String SPACE = " \t\n\f\r";

    @Test
    public void testWordChar() throws Exception {
        VerbalExpression regex = regex().wordChar().build();

        assertThat("Not matches on letters", regex, matchesTo(LETTERS_NO_DIGITS + DIGITS));
        assertThat("matches on non letters", regex, not(matchesTo((NON_LETTERS + SPACE))));
        assertThat("Extracts wrong word chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), equalTo(LETTERS_NO_DIGITS + DIGITS));

    }

    @Test
    public void testNonWordChar() throws Exception {
        VerbalExpression regex = regex().nonWordChar().build();

        assertThat("matches on letters", regex, not(matchesTo((LETTERS_NO_DIGITS + DIGITS))));
        assertThat("Not matches on non letters", regex, matchesTo(NON_LETTERS + SPACE));
        assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), equalTo(NON_LETTERS + SPACE));

    }

    @Test
    public void testSpace() throws Exception {
        VerbalExpression regex = regex().space().build();

        assertThat("matches on letters", regex, not(matchesTo((LETTERS_NO_DIGITS + DIGITS + NON_LETTERS))));
        assertThat("Not matches on space", regex, matchesTo(SPACE));
        assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), equalTo(SPACE));

    }

    @Test
    public void testNonSpace() throws Exception {
        VerbalExpression regex = regex().nonSpace().build();

        assertThat("Not matches on non space", regex, matchesTo(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS));
        assertThat("matches on space", regex, not(matchesTo((SPACE))));
        assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), not(SPACE));

    }

    @Test
    public void testDigit() throws Exception {
        VerbalExpression regex = regex().digit().build();

        assertThat("matches on letters", regex, not(matchesTo((LETTERS_NO_DIGITS + SPACE + NON_LETTERS))));
        assertThat("Not matches on digits", regex, matchesTo(DIGITS));
        assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), is(DIGITS));

    }

    @Test
    public void testNonDigit() throws Exception {
        VerbalExpression regex = regex().nonDigit().build();

        assertThat("Not matches on letters", regex, matchesTo(LETTERS_NO_DIGITS + SPACE + NON_LETTERS));
        assertThat("matches on digits", regex, not(matchesTo((DIGITS))));
        assertThat("Extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), not(DIGITS));

    }

    @Test
    public void testWord() throws Exception {
        VerbalExpression regex = regex().word().build();

        assertThat("not matches on word", regex, matchesTo(LETTERS_NO_DIGITS + DIGITS));
        assertThat("matches on space and non letters", regex, not(matchesTo(SPACE + NON_LETTERS)));
        assertThat("extracts wrong chars",
                regex.getText(LETTERS_NO_DIGITS + DIGITS + NON_LETTERS + SPACE), is(LETTERS_NO_DIGITS + DIGITS));

    }
}
