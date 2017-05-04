package run.lanwen.verbalregex;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InstanceStepsFactory;


/**
 * Created by soos on 2017.05.03..
 */
public class RegexMap  extends JUnitStory{
    @Override
    public Configuration configuration(){
        return new MostUsefulConfiguration()
                .useStoryLoader(new LoadFromClasspath(this.getClass()))
                .useStoryReporterBuilder(new StoryReporterBuilder().withDefaultFormats().withFormats(Format.CONSOLE, Format.TXT));
    }


    @Override
    public InstanceStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), new RegexStep());
    }
}
