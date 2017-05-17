package ru.lanwen.verbalregextest;


import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import ru.lanwen.verbalregex.VerbalExpression;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static ru.lanwen.verbalregex.VerbalExpression.regex;
import static ru.lanwen.verbalregex.matchers.TestMatchMatcher.matchesTo;

public class BDDSteps {
    VerbalExpression testRegex ;
    String cause;
    List<String> sampleStrings = new ArrayList<String>();

    @Given("regular expression that finds $toFind with one of: $samples")
    public void createSampleString(@Named("toFind") String toFind, @Named("samples") List<SampleParam> samples){
        for (int i = 0; i < samples.size(); i++) {
            sampleStrings.add(samples.get(i).getSample());
        }
        String [] findWith = sampleStrings.toArray(new String[sampleStrings.size()]);
        
        testRegex = VerbalExpression.regex()
                .find(toFind)
                .oneOf(findWith)
                .build();
    }

    @Given("a check that: $string")
    public void createCheckString(@Named("string") String toCheck){
        cause = toCheck;
    }

    @Given("a regular expression that matches telephone numbers")
    public void createTelephoneNumRegex(){
        testRegex = regex()
                .startOfLine()
                .then("+")
                .capture().range("0", "9").count(3).maybe("-").maybe(" ").endCapture()
                .count(3)
                .endOfLine().build();
    }

    @Given("a regular expression that matches anything")
    public void createRegexForAnything(){
        testRegex = new VerbalExpression.Builder()
                .startOfLine()
                .anything()
                .build();
    }

    @When("create a a regex that matches something")
    public void createRegexForSomething(){
        testRegex = new VerbalExpression.Builder().something().build();
    }

    @When ("a regular expression is created that finds: $string")
    public void createRegexForFinding(@Named("string") String toFind){
        VerbalExpression regex = VerbalExpression.regex()
                .find(toFind)
                .build();

    }


    @Then("the check is false for the regex expression")
    public void checkTrue(){
        assertThat(cause, testRegex, not(matchesTo(null)));

    }

    @Then("the regular expression matches: $stringToMatch")
    public void regexpMatches(@Named("stringToMatch") String stringToMatch){
        assertThat(cause, testRegex, matchesTo(stringToMatch));

    }

    @Then("the regular expression does match: $stringToMatch")
    public void simpleMatch(@Named("stringToMatch") String stringToMatch){
        if (stringToMatch.equals("\" \"")) stringToMatch = " ";
        assertThat(testRegex, matchesTo(stringToMatch));

    }

    @Then("the regular expression does not match: $stringToCheck")
    public void simpleDismatch(@Named("stringToCheck") String stringToCheck){
        if(stringToCheck.equals("\"\"")) stringToCheck = "";

        assertThat(testRegex, not(matchesTo(stringToCheck)));

    }

}
