package ru.lanwen.verbalregex;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ru.lanwen.verbalregex.VerbalExpression.regex;
import static ru.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;
import static ru.lanwen.verbalregex.matchers.TestsExactMatcher.matchesExactly;


public class RealWorldUnitTest {

    @Test
    public void testUrl() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("http")
                .maybe("s")
                .then("://")
                .maybe("www.")
                .anythingBut(" ")
                .endOfLine()
                .build();

        // Create an example URL
        String testUrl = "https://www.google.com";
        assertThat("Matches Google's url", testRegex, matchesTo(testUrl)); //True

        assertThat("Regex doesn't match same regex as in example",
                testRegex.toString(),
                equalTo("^(?:http)(?:s)?(?:\\:\\/\\/)(?:www\\.)?(?:[^\\ ]*)$"));
    }

    @Test
    public void testTelephoneNumber() {
        VerbalExpression regex = regex()
                .startOfLine()
                .then("+")
                .capture().range("0", "9").count(3).maybe("-").maybe(" ").endCapture()
                .count(3)
                .endOfLine().build();

        String phoneWithSpace = "+097 234 243";
        String phoneWithoutSpace = "+097234243";
        String phoneWithDash = "+097-234-243";

        assertThat(regex, matchesExactly(phoneWithSpace));
        assertThat(regex, matchesExactly(phoneWithoutSpace));
        assertThat(regex, matchesExactly(phoneWithDash));

    }

    @Test
    public void complexPatternWithMultiplyCaptures() throws Exception {
        String logLine = "3\t4\t1\thttp://localhost:20001\t1\t63528800\t0\t63528800\t1000000000\t0\t63528800\tSTR1";

        VerbalExpression regex = regex()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().find("http://localhost:20").digit().count(3).endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().range("0", "1").count(1).endCapture().tab()
                .capt().digit().oneOrMore().endCapture().tab()
                .capt().find("STR").range("0", "2").count(1).endCapture().build();

        assertThat(regex, matchesExactly(logLine));

        VerbalExpression.Builder digits = regex().capt().digit().oneOrMore().endCapt().tab();
        VerbalExpression.Builder range = regex().capt().range("0", "1").count(1).endCapt().tab();
        VerbalExpression.Builder host = regex().capt().find("http://localhost:20").digit().count(3).endCapt().tab();
        VerbalExpression.Builder fake = regex().capt().find("STR").range("0", "2").count(1);

        VerbalExpression regex2 = regex()
                .add(digits).add(digits)
                .add(range).add(host).add(range).add(digits).add(range)
                .add(digits).add(digits)
                .add(range).add(digits).add(fake).build();

        assertThat(regex2, matchesExactly(logLine));

        //(\\d+)\\t(\\d+)\\t([0-1]{1})\\t(http://localhost:20\\d{3})\\t([0-1]{1})
        // \\t(\\d+)\\t([0-1]{1})\\t(\\d+)\\t(\\d+)\\t([0-1]{1})\\t(\\d+)\\t(FAKE[1-2]{1})
        /*
        3    4    1    http://localhost:20001    1    28800    0    528800    1000000000    0    528800    STR1
        3    5    1    http://localhost:20002    1    28800    0    528800    1000020002    0    528800    STR2
        4    6    0    http://localhost:20002    1    48800    0    528800    1000000000    0    528800    STR1
        4    7    0    http://localhost:20003    1    48800    0    528800    1000020003    0    528800    STR2
        5    8    1    http://localhost:20003    1    68800    0    528800    1000000000    0    528800    STR1
        5    9    1    http://localhost:20004    1    28800    0    528800    1000020004    0    528800    STR2
         */
    }

    @Test
    public void unusualRegex() throws Exception {
        assertThat(regex().add("[A-Z0-1!-|]").build().toString(), equalTo("[A-Z0-1!-|]"));

    }

    @Test
    @Ignore("Planned in 1.3")
    public void captureWithName() throws Exception {
    }

    @Test
    public void oneOfShouldFindEpisodeTitleOfStarWarsMovies() {
        VerbalExpression regex = VerbalExpression.regex()
                .find("Star Wars: ")
                .oneOf("The Phantom Menace", "Attack of the Clones", "Revenge of the Sith",
                        "The Force Awakens", "A New Hope", "The Empire Strikes Back", "Return of the Jedi")
                .build();
        assertThat(regex, matchesTo("Star Wars: The Empire Strikes Back"));
        assertThat(regex, matchesTo("Star Wars: Return of the Jedi"));
    }

    @Test
    public void captureAfterNewLineHasGroupNumberOne() throws Exception {

        final String lineBreak = "\n";
        final String some = "some";
        final String text = " text";
        final VerbalExpression expression = VerbalExpression.regex().
                        lineBreak()
                        .capture().find(some).endCapture().then(text)
                        .build();

        assertThat(some, equalTo(expression.getText(lineBreak + some + text, 1)));
    }

    @Test
    public void captureAfterNewLineHasANamedGroup() {

        final String lineBreak = "\n";
        final String some = "some";
        final String text = " text";
        final String captureName = "name";
        final VerbalExpression expression = VerbalExpression.regex().
                lineBreak()
                .capture(captureName).find(some).endCapture().then(text)
                .build();

        assertThat(some,
                equalTo(expression.getText(lineBreak + some + text, captureName)));
    }

    @Test
    public void missingOptionalCaptureGroupReturnsEmptyStringNotStringContainingNullLiteral() {
        final VerbalExpression expression = VerbalExpression.regex().
                startOfLine()
                .capture("optionalCapture")
                .oneOf("a", "b")
                .endCapture()
                .count(0, 1)
                .then("c")
                .endOfLine()
                .build();
        final String testString = "c";
        assertThat(expression, matchesExactly(testString));
        assertThat(expression.getText("c", "optionalCapture"), equalTo(""));
    }
}
