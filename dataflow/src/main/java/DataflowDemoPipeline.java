import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PDone;

import java.util.Arrays;
import java.util.List;

public class DataflowDemoPipeline {
    private static final Logger Log = LoggerFactory.getLogger(DataflowDemoPipeline.class);

    public static void main(String[] args){

        // Register Options class for our pipeline with the factory
        PipelineOptionsFactory.register(DemoPipelineOptions.class);

        DemoPipelineOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(DemoPipelineOptions.class);

        Pipeline p = Pipeline.create(options);

        List<String> input = Arrays.asList(new String[]{"Job"});
        p.apply("Dummy Input", Create.of(input)).
            apply("Invoke Export", ParDo.of(
                new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext context) {
                        String elem = context.element();
                        Log.info("Step");
                        context.output(elem);
                    }
                }
            ));

        PDone.in(p);

        p.run();
    }
}

