package run.lanwen.verbalregex;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.util.Stack;

import static org.junit.Assert.assertFalse;

/**
 * Created by soos on 2017.05.03..
 */
public class RegexStep {
    RegexStep(){}
    //private VerbalExpression verbalexpression;
    public Stack stack;

    @Given("a verbalexpression")
    public void aVerbalExpression(){
        //verbalexpression = new VerbalExpression();
        stack = new Stack();
    }

    @When("I try to getText $item")
    public void iTryToGetText(@Named("item") String item){
        /*verbalexpression.getText("abc");*/
        stack.push(item);
    }

    @Then("it prints out $expected")
    public void PrintOutSomething(@Named("expected") boolean expected){
        boolean actual = stack.empty();
        assertFalse(actual);
        if(actual != expected){
            System.out.println("expected:"+expected+";actual:"+actual);
        }
        else{
            System.out.println("good");
        }
    }
}
