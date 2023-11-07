import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ValueProvider;

public interface DemoPipelineOptions extends DataflowPipelineOptions {
    @Description("ProjectId")
    @Default.String("examples-249001")
    ValueProvider<String> getProjectId();
    void setProjectId(ValueProvider<String> projectId);

    @Description("Kind")
    @Default.String("BigQuery")
    ValueProvider<String> getKind();
    void setKind(ValueProvider<String> kind);

    @Description("DatasetId")
    @Default.String("Test")
    ValueProvider<String> getDatasetId();
    void setDatasetId(ValueProvider<String> datasetId);

    @Description("GCS URL")
    @Default.String("gs://examples-249001/tmp/backup-example3/")
    ValueProvider<String> getGCSUrl();
    void setGCSUrl(ValueProvider<String> gcsUrl);
}
