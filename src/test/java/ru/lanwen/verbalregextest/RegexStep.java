package ru.lanwen.verbalregextest;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import ru.lanwen.verbalregex.VerbalExpression;

import java.util.Stack;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static ru.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;

/**
 * Created by soos on 2017.05.03..
 */
public class RegexStep {
    public VerbalExpression testRegex = new VerbalExpression.Builder()
            .startOfLine()
            .anything()
            .build();
    @Given("a String $string")
    public void aVerbalExpression(@Named("string") String string){
        System.out.println("String: "+string);
    }

    @When("I examine if it matches to the testRegex")
    public void startOfLineAnythingButW(){
        System.out.println("Verbalexpression: "+testRegex);
    }

    @Then("it matches to the String $string")
    public void testAnythingBut(@Named("string") String string){
        assertThat(testRegex, matchesTo(string));
    }
}
