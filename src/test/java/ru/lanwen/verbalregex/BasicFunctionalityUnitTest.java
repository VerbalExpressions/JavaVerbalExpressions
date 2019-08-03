package ru.lanwen.verbalregex;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static ru.lanwen.verbalregex.VerbalExpression.regex;
import static ru.lanwen.verbalregex.matchers.EqualToRegexMatcher.equalToRegex;
import static ru.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;
import static ru.lanwen.verbalregex.matchers.TestsExactMatcher.matchesExactly;

public class BasicFunctionalityUnitTest {
    @Test
    public void testSomething() {
        VerbalExpression testRegex = new VerbalExpression.Builder().something().build();

        assertThat("Null object doesn't have something", testRegex, not(matchesTo(null)));
        assertThat("empty string doesn't have something", testRegex, not(matchesTo("")));
        assertThat("a", testRegex, matchesTo("a"));
    }

    @Test
    public void testAnything() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .anything()
                .build();

        assertThat(testRegex, matchesTo("what"));
        assertThat(testRegex, not(matchesTo("")));
        assertThat(testRegex, matchesTo(" "));
    }

    @Test
    public void testAnythingBut() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .anythingBut("w")
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
        assertThat("Starts with a", testRegex, matchesTo("a"));
        assertThat("Starts with a", testRegex, matchesTo("ab"));
        assertThat("Doesn't start with a", testRegex, not(matchesTo("ba")));
    }

    @Test
    public void testStartOfLineFalse() {
        VerbalExpression testRegex = regex()
                .startOfLine(false)
                .then("a")
                .build();
        assertThat(testRegex, matchesTo("ba"));
        assertThat(testRegex, matchesTo("ab"));
    }

    @Test
    public void testRangeWithMultiplyRanges() throws Exception {
        VerbalExpression regex = regex().range("a", "z", "A", "Z").build();

        assertThat("Regex with multi-range differs from expected", regex.toString(), equalTo("[a-zA-Z]"));
        assertThat("Regex don't matches letter", regex, matchesTo("b"));
        assertThat("Regex matches digit, but should match only letter", regex, not(matchesTo("1")));
    }

    @Test
    public void testEndOfLine() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .find("a")
                .endOfLine()
                .build();

        assertThat("Ends with a", testRegex, matchesTo("bba"));
        assertThat("Ends with a", testRegex, matchesTo("a"));
        assertThat("Ends with a", testRegex, not(matchesTo(null)));
        assertThat("Doesn't end with a", testRegex, not(matchesTo("ab")));
    }


    @Test
    public void testEndOfLineIsFalse() {
        VerbalExpression testRegex = regex()
                .find("a")
                .endOfLine(false)
                .build();
        assertThat(testRegex, matchesTo("ba"));
        assertThat(testRegex, matchesTo("ab"));
    }


    @Test
    public void testMaybe() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .maybe("b")
                .build();

        assertThat("Regex isn't correct", testRegex.toString(), equalTo("^(?:a)(?:b)?"));

        assertThat("Maybe has a 'b' after an 'a'", testRegex, matchesTo("acb"));
        assertThat("Maybe has a 'b' after an 'a'", testRegex, matchesTo("abc"));
        assertThat("Maybe has a 'b' after an 'a'", testRegex, not(matchesTo("cab")));
    }

    @Test
    public void testAnyOf() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .anyOf("xyz")
                .build();

        assertThat("Has an x, y, or z after a", testRegex, matchesTo("ay"));
        assertThat("Doesn't have an x, y, or z after a", testRegex, not(matchesTo("abc")));
    }


    @Test
    public void testAnySameAsAnyOf() {
        VerbalExpression any = regex().any("abc").build();
        VerbalExpression anyOf = regex().anyOf("abc").build();

        assertThat("any differs from anyOf", any.toString(), equalTo(anyOf.toString()));
    }

    @Test
    public void testOr() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .or("def")
                .build();

        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Doesn't start with abc or def", testRegex, not(matchesTo("xyzabc")));
    }

    @Test
    public void testLineBreak() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .lineBreak()
                .then("def")
                .build();

        assertThat("abc then line break then def", testRegex, matchesTo("abc\r\ndef"));
        assertThat("abc then line break then def", testRegex, matchesTo("abc\ndef"));
        assertThat("abc then line break then space then def", testRegex, not(matchesTo("abc\r\n def")));
    }

    @Test
    public void testMacintoshLineBreak() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("abc")
                .lineBreak()
                .then("def")
                .build();

        assertThat("abc then line break then def", testRegex, matchesTo("abc\r\rdef"));
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

        assertThat("tab then abc", testRegex, matchesTo("\tabc"));
        assertThat("no tab then abc", testRegex, not(matchesTo("abc")));
    }

    @Test
    public void testWord() {
       VerbalExpression testRegex = new VerbalExpression.Builder()
                    .startOfLine()
                    .word()
                    .build();

       assertThat("word", testRegex, matchesTo("abc123"));
       assertThat("non-word", testRegex, not(matchesTo("@#")));
    }

    @Test
    public void testMultipleNoRange() {
       VerbalExpression testRegexStringOnly = new VerbalExpression.Builder()
                    .startOfLine()
                    .multiple("abc")
                    .build();
       VerbalExpression testRegexStringAndNull = new VerbalExpression.Builder()
                           .startOfLine()
                           .multiple("abc", null)
                           .build();
       VerbalExpression testRegexMoreThan2Ints = new VerbalExpression.Builder()
                           .startOfLine()
                           .multiple("abc", 2, 4, 8)
                           .build();
       VerbalExpression[] testRegexesSameBehavior = {
                           testRegexStringOnly,
                           testRegexStringAndNull,
                           testRegexMoreThan2Ints
                    };
       for (VerbalExpression testRegex : testRegexesSameBehavior) {
             assertThat("abc once", testRegex,
                           matchesTo("abc"));
             assertThat("abc more than once", testRegex,
                           matchesTo("abcabcabc"));
             assertThat("no abc", testRegex,
                           not(matchesTo("xyz")));
       }
    }

    @Test
    public void testMultipleFrom() {
       VerbalExpression testRegexFrom = new VerbalExpression.Builder()
                           .startOfLine()
                           .multiple("abc", 2)
                           .build();
       assertThat("no abc", testRegexFrom,
                    not(matchesTo("xyz")));
       assertThat("abc less than 2 times", testRegexFrom,
                    not(matchesTo("abc")));
       assertThat("abc exactly 2 times", testRegexFrom,
                    matchesTo("abcabc"));
       assertThat("abc more than 2 times", testRegexFrom,
                    matchesTo("abcabcabc"));
    }

    @Test
    public void testMultipleFromTo() {
       VerbalExpression testRegexFromTo = new VerbalExpression.Builder()
                           .startOfLine()
                           .multiple("abc", 2, 4)
                           .build();
       assertThat("no abc", testRegexFromTo, not(matchesTo("xyz")));
       assertThat("abc less than 2 times", testRegexFromTo,
                    not(matchesTo("abc")));
       assertThat("abc exactly 2 times", testRegexFromTo, matchesTo("abcabc"));
       assertThat("abc between 2 and 4 times", testRegexFromTo,
                    matchesTo("abcabcabc"));
       assertThat("abc exactly 4 times", testRegexFromTo,
                    matchesTo("abcabcabcabc"));
       assertThat("abc more than 4 times", testRegexFromTo,
                    not(matchesExactly("abcabcabcabcabc")));
    }

    @Test
    public void testWithAnyCase() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .build();

        assertThat("not case insensitive", testRegex, not(matchesTo("A")));
        testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("a")
                .withAnyCase()
                .build();

        assertThat("case insensitive", testRegex, matchesTo("A"));
        assertThat("case insensitive", testRegex, matchesTo("a"));
    }

    @Test
    public void testWithAnyCaseTurnOnThenTurnOff() {
        VerbalExpression testRegex = regex()
                .withAnyCase()
                .startOfLine()
                .then("a")
                .withAnyCase(false)
                .build();

        assertThat(testRegex, not(matchesTo("A")));
    }

    @Test
    public void testWithAnyCaseIsFalse() {
        VerbalExpression testRegex = regex()
                .startOfLine()
                .then("a")
                .withAnyCase(false)
                .build();

        assertThat(testRegex, not(matchesTo("A")));
    }

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
                .searchOneLine(true)
                .build();

        assertThat("b is on the second line but we are only searching the first", testRegex, matchesTo("a\nb"));
    }

    @Test
    public void testGetText() {
        String testString = "123 https://www.google.com 456";
        VerbalExpression testRegex = new VerbalExpression.Builder().add("http")
                .maybe("s")
                .then("://")
                .then("www.")
                .anythingBut(" ")
                .add("com").build();
        assertEquals(testRegex.getText(testString), "https://www.google.com");

    }

    @Test
    public void testStartCapture() {
        String text = "aaabcd";
        VerbalExpression regex = regex()
                .find("a").count(3)
                .capture().find("b").anything().build();

        assertThat("regex don't match string", regex.getText(text), equalTo(text));
        assertThat("can't get first captured group", regex.getText(text, 1), equalTo("bcd"));
    }

    @Test
    public void testStartNamedCapture() {
        String text = "test@example.com";
        String captureName = "domain";
        VerbalExpression regex = regex()
                .find("@")
                .capture(captureName).anything().build();

        assertThat("can't get captured group named " + captureName,
                regex.getText(text, captureName),
                equalTo("example.com"));
    }

    @Test
    public void captIsSameAsCapture() {
        assertThat("Capt produce different than capture regex", regex().capt().build().toString(),
                equalTo(regex().capture().build().toString()));
    }

    @Test
    public void namedCaptIsSameAsNamedCapture() {
        String name = "test";
        assertThat("Named-capt produce different than named-capture regex",
                regex().capt(name).build().toString(),
                equalTo(regex().capture(name).build().toString()));
    }

    @Test
    public void shouldReturnEmptyStringWhenNoGroupFound() {
        String text = "abc";
        VerbalExpression regex = regex().find("d").capture().find("e").build();

        assertThat("regex don't match string", regex.getText(text), equalTo(""));
        assertThat("first captured group not empty string", regex.getText(text, 1), equalTo(""));
        assertThat("second captured group not empty string", regex.getText(text, 2), equalTo(""));
    }

    @Test
    public void testCountWithRange() {
        String text4c = "abcccce";
        String text2c = "abcce";
        String text1c = "abce";

        VerbalExpression regex = regex().find("c").count(2, 3).build();

        assertThat("regex don't match string", regex.getText(text4c), equalTo("ccc"));
        assertThat("regex don't match string", regex.getText(text2c), equalTo("cc"));
        assertThat("regex don't match string", regex, not(matchesTo(text1c)));
    }

    @Test
    public void testEndCapture() {
        String text = "aaabcd";
        VerbalExpression regex = regex()
                .find("a")
                .capture().find("b").anything().endCapture().then("cd").build();

        assertThat(regex.getText(text), equalTo("abcd"));
        assertThat("can't get first captured group", regex.getText(text, 1), equalTo("b"));
    }

    @Test
    public void testEndNamedCapture() {
        String text = "aaabcd";
        String captureName = "str";
        VerbalExpression regex = regex()
                .find("a")
                .capture(captureName).find("b").anything().endCapture()
                .then("cd").build();

        assertThat(regex.getText(text), equalTo("abcd"));
        assertThat("can't get captured group named " + captureName,
                regex.getText(text, captureName), equalTo("b"));
    }

    @Test
    public void testMultiplyCapture() {
        String text = "aaabcd";
        VerbalExpression regex = regex()
                .find("a").count(1)
                .capture().find("b").endCapture().anything().capture().find("d").build();

        assertThat("can't get first captured group", regex.getText(text, 1), equalTo("b"));
        assertThat("can't get second captured group", regex.getText(text, 2), equalTo("d"));
    }

    @Test
    public void testMultiplyNamedCapture() {
        String text = "aaabcd";
        String captureName1 = "str1";
        String captureName2 = "str2";
        VerbalExpression regex = regex()
                .find("a").count(1)
                .capture(captureName1).find("b").endCapture()
                .anything().capture(captureName2).find("d").build();

        assertThat("can't get captured group named " + captureName1,
                regex.getText(text, captureName1), equalTo("b"));
        assertThat("can't get captured group named " + captureName2,
                regex.getText(text, captureName2), equalTo("d"));
    }

    @Test
    public void testOrWithCapture() {
        VerbalExpression testRegex = regex()
                .capture()
                .find("abc")
                .or("def")
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def", testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcnull"));
        assertThat(testRegex.getText("xxxdefzzz", 1), equalTo("null"));
        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcnull"));
    }

    @Test
    public void testOrWithNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = regex()
                .capture(captureName)
                .find("abc")
                .or("def")
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def",
                testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", captureName),
                equalTo("abcnull"));
        assertThat(testRegex.getText("xxxdefzzz", captureName),
                equalTo("null"));
        assertThat(testRegex.getText("xxxabcdefzzz", captureName),
                equalTo("abcnull"));
    }

    @Test
    public void testOrWithClosedCapture() {
        VerbalExpression testRegex = regex()
                .capture()
                .find("abc")
                .endCapt()
                .or("def")
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def", testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcnull"));
        assertThat(testRegex.getText("xxxdefzzz", 1), equalTo("null"));
        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcnull"));
    }

    @Test
    public void testOrWithClosedNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = regex()
                .capture(captureName)
                .find("abc")
                .endCapt()
                .or("def")
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def",
                testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", captureName),
                equalTo("abcnull"));
        assertThat(testRegex.getText("xxxdefzzz", captureName),
                equalTo("null"));
        assertThat(testRegex.getText("xxxabcdefzzz", captureName),
                equalTo("abcnull"));
    }

    @Test
    public void addRegexBuilderWrapsItWithUnsavedGroup() throws Exception {
        VerbalExpression regex = regex()
                .add(regex().capt().find("string").count(2).endCapt().count(1).digit()).count(2).build();

        assertThat("Added regex builder don't wrapped with unsaved group",
                regex.toString(), startsWith("(?:((?:string"));

        String example = "stringstring1";
        String example2digit = "stringstring11";

        assertThat(regex, matchesExactly(example + example));
        assertThat(regex, not(matchesExactly(example2digit)));
    }

    @Test
    public void multiplyWith1NumProduceSameAsCountResult() throws Exception {
        VerbalExpression regex = regex().multiple("a", 1).build();

        assertThat(regex, equalToRegex(regex().find("a").count(1)));
    }

    @Test
    public void multiplyWith2NumProduceSameAsCountRangeResult() throws Exception {
        VerbalExpression regex = regex().multiple("a", 1, 2).build();

        assertThat(regex, equalToRegex(regex().find("a").count(1, 2)));
    }

    @Test
    public void atLeast1HaveSameEffectAsOneOrMore() throws Exception {
        VerbalExpression regex = regex().find("a").atLeast(1).build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        assertThat(regex, matchesExactly(matched));
        assertThat(regex, matchesExactly(oneMatchedExactly));
        assertThat(regex, not(matchesExactly(oneMatched)));
        assertThat(regex, matchesTo(oneMatched));
        assertThat(regex, not(matchesTo(empty)));
    }

    @Test
    public void oneOreMoreSameAsAtLeast1() throws Exception {
        VerbalExpression regexWithOneOrMore = regex().find("a").oneOrMore().build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        assertThat(regexWithOneOrMore, matchesExactly(matched));
        assertThat(regexWithOneOrMore, matchesExactly(oneMatchedExactly));
        assertThat(regexWithOneOrMore, not(matchesExactly(oneMatched)));
        assertThat(regexWithOneOrMore, matchesTo(oneMatched));
        assertThat(regexWithOneOrMore, not(matchesTo(empty)));
    }

    @Test
    public void atLeast0HaveSameEffectAsZeroOrMore() throws Exception {
        VerbalExpression regex = regex().find("a").atLeast(0).build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        assertThat(regex, matchesExactly(matched));
        assertThat(regex, matchesExactly(oneMatchedExactly));
        assertThat(regex, not(matchesExactly(oneMatched)));
        assertThat(regex, matchesTo(empty));
        assertThat(regex, matchesExactly(empty));
    }

    @Test
    public void zeroOreMoreSameAsAtLeast0() throws Exception {
        VerbalExpression regexWithOneOrMore = regex().find("a").zeroOrMore().build();

        String matched = "aaaaaa";
        String oneMatchedExactly = "a";
        String oneMatched = "ab";
        String empty = "";

        assertThat(regexWithOneOrMore, matchesExactly(matched));
        assertThat(regexWithOneOrMore, matchesExactly(oneMatchedExactly));
        assertThat(regexWithOneOrMore, not(matchesExactly(oneMatched)));
        assertThat(regexWithOneOrMore, matchesTo(oneMatched));
        assertThat(regexWithOneOrMore, matchesTo(empty));
        assertThat(regexWithOneOrMore, matchesExactly(empty));
    }

    @Test
    public void testOneOf() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .oneOf("abc", "def")
                .build();

        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc nor def", testRegex, not(matchesTo("xyzabc")));
    }

    @Test
    public void testOneOfWithCapture() {
        VerbalExpression testRegex = regex()
                .capture()
                .oneOf("abc", "def")
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def", testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcdef"));
        assertThat(testRegex.getText("xxxdefzzz", 1), equalTo("def"));
    }

    @Test
    public void testOneOfWithNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = regex()
                .capture(captureName)
                .oneOf("abc", "def")
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def",
                testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", captureName),
                equalTo("abcdef"));
        assertThat(testRegex.getText("xxxdefzzz", captureName),
                equalTo("def"));
    }

    @Test
    public void testOneOfWithClosedCapture() {
        VerbalExpression testRegex = regex()
                .capture()
                .oneOf("abc", "def")
                .endCapt()
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def", testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", 1), equalTo("abcdef"));
        assertThat(testRegex.getText("xxxdefzzz", 1), equalTo("def"));
    }

    @Test
    public void testOneOfWithClosedNamedCapture() {
        String captureName = "test";
        VerbalExpression testRegex = regex()
                .capture(captureName)
                .oneOf("abc", "def")
                .endCapt()
                .build();
        assertThat("Starts with abc or def", testRegex, matchesTo("defzzz"));
        assertThat("Starts with abc or def", testRegex, matchesTo("abczzz"));
        assertThat("Doesn't start with abc or def",
                testRegex, not(matchesExactly("xyzabcefg")));

        assertThat(testRegex.getText("xxxabcdefzzz", captureName),
                equalTo("abcdef"));
        assertThat(testRegex.getText("xxxdefzzz", captureName),
                equalTo("def"));
    }

    @Test
    public void shouldAddMaybeWithOneOfFromAnotherBuilder() {
	VerbalExpression.Builder namePrefix = regex().oneOf("Mr.", "Ms.");
	VerbalExpression name = regex()
		.maybe(namePrefix)
		.space()
		.zeroOrMore()
		.word()
		.oneOrMore()
		.build();

	assertThat("Is a name with prefix", name, matchesTo("Mr. Bond"));
	assertThat("Is a name without prefix", name, matchesTo("James"));

    }

    @Test
    public void testListOfTextGroups() {
        String text = "SampleHelloWorldString";
        VerbalExpression regex = regex()
                .capt()
                .oneOf("Hello", "World")
                .endCapt()
                .maybe("String")
                .build();

        List<String> groups0 = regex.getTextGroups(text, 0);

        assertThat(groups0.get(0), equalTo("Hello"));
        assertThat(groups0.get(1), equalTo("WorldString"));

        List<String> groups1 = regex.getTextGroups(text, 1);

        assertThat(groups1.get(0), equalTo("Hello"));
        assertThat(groups1.get(1), equalTo("World"));
    }

    @Test
    public void testWordBoundary() {
        VerbalExpression regex = regex()
                .capture()
                .wordBoundary().then("o").word().oneOrMore().wordBoundary()
                .endCapture()
                .build();

        assertThat(regex.getText("apple orange grape", 1), is("orange"));
        assertThat(regex.test("appleorange grape"), is(false));
        assertThat(regex.test("apple3orange grape"), is(false));
    }
}
