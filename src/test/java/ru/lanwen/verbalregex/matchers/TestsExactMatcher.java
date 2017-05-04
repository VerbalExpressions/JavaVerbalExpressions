package ru.lanwen.verbalregex.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;
import ru.lanwen.verbalregex.VerbalExpression;

/**
 * User: lanwen
 * Date: 29.05.14
 * Time: 20:06
 */
public class TestsExactMatcher extends TypeSafeMatcher<VerbalExpression> {

    private String toTest;

    private TestsExactMatcher(String toTest) {
        this.toTest = toTest;
    }

    @Override
    protected boolean matchesSafely(VerbalExpression verbalExpression) {
        return verbalExpression.testExact(toTest);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("regex should match exactly to ").appendValue(toTest);
    }

    @Override
    protected void describeMismatchSafely(VerbalExpression item, Description mismatchDescription) {
        mismatchDescription.appendText(item.toString()).appendText(" don't matches this string");
    }

    @Factory
    public static TestsExactMatcher matchesExactly(String test) {
        return new TestsExactMatcher(test);
    }
}
