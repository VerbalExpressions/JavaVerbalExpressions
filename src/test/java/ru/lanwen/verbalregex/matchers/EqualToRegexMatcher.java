package ru.lanwen.verbalregex.matchers;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import ru.lanwen.verbalregex.VerbalExpression;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * User: lanwen
 * Date: 29.05.14
 * Time: 22:59
 */
public final class EqualToRegexMatcher {
    private EqualToRegexMatcher() {
    }

    public static Matcher<VerbalExpression> equalToRegex(final VerbalExpression.Builder builder) {
        return new FeatureMatcher<VerbalExpression, String>(equalTo(builder.build().toString()), "regex", "") {
            @Override
            protected String featureValueOf(VerbalExpression verbalExpression) {
                return verbalExpression.toString();
            }
        };
    }
}
