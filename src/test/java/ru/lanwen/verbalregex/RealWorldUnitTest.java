package ru.lanwen.verbalregex;

import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class RealWorldUnitTest {

    @Test
    public void testUrl() {
        VerbalExpression testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .then("http")
                .maybe("s")
                .then("://")
                .maybe("www.")
                .anythingButNot(" ")
                .endOfLine()
                .build();

        // Create an example URL
        String testUrl = "https://www.google.com";
        assertTrue("Matches Google's url", testRegex.test(testUrl)); //True

        assertThat("Regex doesn't match same regex as in example",
                testRegex.toString(),
                equalTo("^(?:http)(?:s)?(?:\\:\\/\\/)(?:www\\.)?(?:[^\\ ]*)$"));
    }

    @Test
    public void staticFabricsRetunSameAsConstructorExpressions() {
        VerbalExpression regexViaFactory = VerbalExpression.regex().anything().build();
        VerbalExpression regexViaConstructor = new VerbalExpression.Builder().anything().build();

        assertThat("Factory builder method produce not same as constructor regex",
                regexViaFactory.toString(), equalTo(regexViaConstructor.toString()));
    }

    @Test
    public void clonedBuilderEqualsOriginal() {
        VerbalExpression.Builder builder = VerbalExpression.regex().anything().addModifier('i');
        VerbalExpression.Builder clonedBuilder = VerbalExpression.regex(builder);

        assertThat("Cloned builder changed after creating new one",
                builder.build().toString(), equalTo(clonedBuilder.build().toString()));
    }

    @Test
    public void clonedBuilderCantChangeOriginal() {
        VerbalExpression.Builder builder = VerbalExpression.regex().anything().addModifier('i');
        VerbalExpression.Builder clonedBuilder = VerbalExpression.regex(builder).endOfLine();

        assertThat("Cloned builder changed after creating new one",
                builder.build().toString(), not(clonedBuilder.build().toString()));
    }
}
