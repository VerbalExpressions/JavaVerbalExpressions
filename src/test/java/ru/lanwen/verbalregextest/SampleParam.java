package ru.lanwen.verbalregextest;

import org.jbehave.core.annotations.AsParameters;
import org.jbehave.core.annotations.Parameter;

@AsParameters
public class SampleParam {
    @Parameter(name = "sample")
    String sample;

    public SampleParam(){}

    public SampleParam(String sampleString){
        sample = sampleString;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getSample() {

        return sample;
    }
}
