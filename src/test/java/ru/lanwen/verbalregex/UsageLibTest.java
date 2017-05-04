package run.lanwen.verbalregex;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * User: lanwen
 * Date: 11.05.14
 * Time: 3:30
 */
public class UsageLibTest {


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
