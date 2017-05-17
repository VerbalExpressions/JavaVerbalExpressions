package ru.lanwen.verbalregextest;

import static org.junit.Assert.assertThat;
import static ru.lanwen.verbalregex.VerbalExpression.regex;
import static ru.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;

import org.junit.Test;

import ru.lanwen.verbalregex.VerbalExpression;

public class ForMoreCovarege {

	@Test
	public void testSearchOneLine() {
        VerbalExpression testRegex = regex()
                .startOfLine()
                .then("a")
                .br()
                .then("b")
                .endOfLine()
                .build();

        assertThat("b is on the second line", testRegex, matchesTo("a\nb"));

        testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .br()
                .then("b")
                .endOfLine()
                .searchOneLine(false)
                .build();

        assertThat("b is on the second line but we are only searching the first", testRegex, matchesTo("a\nb"));
    }
	
	
	
}
