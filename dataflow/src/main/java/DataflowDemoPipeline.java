import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;

import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PDone;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.bigquery.*;

import java.util.UUID;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataflowDemoPipeline {
    private static final Logger Log = LoggerFactory.getLogger(DataflowDemoPipeline.class);

    public static void main(String[] args){
     
        // Register Options class for our pipeline with the factory
        PipelineOptionsFactory.register(DemoPipelineOptions.class);

        DemoPipelineOptions options = PipelineOptionsFactory.fromArgs(args)
                .withValidation()
                .as(DemoPipelineOptions.class);

        Pipeline p = Pipeline.create(options);

        String time = ZonedDateTime          // Represent a moment as perceived in the wall-clock time used by the people of a particular region ( a time zone).
            .now(                            // Capture the current moment.
                ZoneId.of("UTC")             // Specify the time zone using proper Continent/Region name. Never use 3-4 character pseudo-zones such as PDT, EST, IST. 
            )                                // Returns a `ZonedDateTime` object. 
            .format(                         // Generate a `String` object containing text representing the value of our date-time object. 
                DateTimeFormatter.ofPattern( "uuuu.MM.dd.HH.mm.ss" )
            );                               // Returns a `String`. 

        List<String> input = Arrays.asList(new String[]{time});

        p.apply("Dummy Input", Create.of(input)).
            apply("Invoke Export", ParDo.of(
                new DoFn<String, String>() {
                    @ProcessElement
                    public void processElement(ProcessContext context) {
                        String elem = context.element();

                        Log.info("Time: " + elem);

                        DemoPipelineOptions options = context.getPipelineOptions()
                            .as(DemoPipelineOptions.class);

                        // BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
                        BigQuery bigquery = BigQueryOptions.newBuilder()
                            .setProjectId(options.getProjectId().get()) 
                            .build()
                            .getService();

                        for (Table table : bigquery.listTables(options.getDatasetId().get(), BigQuery.TableListOption.pageSize(100)).iterateAll()) {
                            ExtractJobConfiguration extractJobConfiguration = ExtractJobConfiguration.newBuilder(table.getTableId(), options.getGCSUrl().get())
                                .setCompression("SNAPPY")
                                .setFormat("AVRO")
                                .setUseAvroLogicalTypes(true)
                                .build();

                            // Create a job ID so that we can safely retry.
                            JobId jobId = JobId.of(UUID.randomUUID().toString());
                            JobInfo jobInfo = JobInfo.newBuilder(extractJobConfiguration).setJobId(jobId).build();
                            Job job = bigquery.create(jobInfo);
    
                            Log.info("Export job " + jobInfo.getJobId() + " created");
                        };

                        context.output(elem);
                    }
                }
            ));

        PDone.in(p);

        p.run();
    }
}

