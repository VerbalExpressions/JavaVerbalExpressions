package ru.lanwen.verbalregex;

import net.serenitybdd.jbehave.SerenityStories;
import net.serenitybdd.jbehave.SerenityStory;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.steps.ParameterConverters;

public class RegexMap extends SerenityStories {

    public RegexMap() {
        String storyPattern = "/resources/*.story";
        if (storyPattern != null) {
            findStoriesCalled("regexmap.story");
        }
    }

    @Override
    public Configuration configuration() {
        return super.configuration()
//                .useStoryParser(new GherkinStoryParser())
                .useParameterConverters(
                        new ParameterConverters().addConverters(
                                new ParameterConverters.NumberConverter(),
                                new ParameterConverters.EnumConverter(),
                                new ParameterConverters.NumberListConverter(),
                                new ParameterConverters.EnumListConverter()
                        )
                );
    }
}
