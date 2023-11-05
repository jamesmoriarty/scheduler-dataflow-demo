# Cloud Scheduler & Dataflow Demo

The folder contains an example how to set up a cloud scheduler to trigger a Dataflow batch job.

## Links

- https://cloud.google.com/dataflow/docs/guides/templates/provided-templates#batch-templates
- https://github.com/GoogleCloudPlatform/DataflowTemplates/blob/71f00068baacb658b7f18e6945dd87b715ec8205/v1/src/main/java/com/google/cloud/teleport/spanner/ExportPipeline.java
- https://github.com/GoogleCloudPlatform/DataflowTemplates/blob/71f00068baacb658b7f18e6945dd87b715ec8205/v1/src/main/java/com/google/cloud/teleport/spanner/ExportTransform.java

```json
POST /v1b3/projects/examples-249001/locations/us-central1/templates:launch?gcsPath=gs://dataflow-templates-us-central1/latest/Cloud_Spanner_to_GCS_Avro
{
    "jobName": "",
    "environment": {
        "bypassTempDirValidation": false,
        "additionalExperiments": [],
        "additionalUserLabels": {}
    },
    "parameters": {}
}

```