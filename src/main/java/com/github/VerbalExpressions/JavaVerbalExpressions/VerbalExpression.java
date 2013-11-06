package com.github.VerbalExpressions.JavaVerbalExpressions;

import java.util.regex.Matcher;

public class VerbalExpression {
	
	PatternBuilder builder;

    VerbalExpression(final PatternBuilder pBuilder) {
    	builder = pBuilder;
    }    
    
    public boolean testExact(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = builder.getPattern().matcher(pToTest).matches();
        }
        return ret;
    }

    public boolean test(final String pToTest) {
        boolean ret = false;
        if (pToTest != null) {
            ret = builder.getPattern().matcher(pToTest).find();
        }
        return ret;
    }

    public String getText(String toTest) {
        Matcher m = builder.getPattern().matcher(toTest);
        StringBuilder result = new StringBuilder();
        while (m.find()){
            result.append(m.group());
        }
        return result.toString();
    }    

    @Override
    public String toString() {
        return builder.getPattern().pattern();
    }
}