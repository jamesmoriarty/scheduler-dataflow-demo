terraform {
  backend "gcs" {
    bucket = "examples-249001"
    prefix = "terraform/state"
  }
}

provider "google" {
  version = "~> 2.20"
  project = "examples-249001"
}

# Use this data source to get project details. For more information see API.
# https://www.terraform.io/docs/providers/google/d/google_project.html

data "google_project" "project" {}

resource "google_cloud_scheduler_job" "scheduler" {
  name = "scheduler-demo"
  schedule = "0 0 * * *"
  # This needs to be us-central1 even if the app engine is in us-central.
  # You will get a resource not found error if just using us-central.
  region = "us-central1"

  http_target {
    http_method = "POST"
    uri = "https://dataflow.googleapis.com/v1b3/projects/${var.project_id}/locations/${var.region}/templates:launch?gcsPath=gs://examples-249001/templates/dataflow-demo-template"
    oauth_token {
      service_account_email = google_service_account.cloud-scheduler-demo.email
    }

    # need to encode the string
    body = base64encode(<<-EOT
    {
      "jobName": "test-cloud-scheduler",
      "parameters": {
        "region": "${var.region}",
        "autoscalingAlgorithm": "THROUGHPUT_BASED",
      },
      "environment": {
        "maxWorkers": "10",
        "tempLocation": "gs://examples-249001/temp",
        "zone": "us-west1-a"
      }
    }
EOT
    )
  }
}

resource "google_service_account" "cloud-scheduler-demo" {
  account_id = "scheduler-dataflow-demo"
  display_name = "A service account for running dataflow from cloud scheduler"
}

# MISSING_ACTAS_PERMISSION: While creating this resource, the principal did not have the 
# iam.serviceAccounts.actAs permission on the service account that is attached to the resource.
# See https://cloud.google.com/iam/docs/service-accounts-actas for details and remediation 
# steps.
resource "google_project_iam_member" "cloud-scheduler-acts-as" {
  project = var.project_id
  role = "roles/iam.serviceAccountUser"
  member = "serviceAccount:${google_service_account.cloud-scheduler-demo.email}"
}

resource "google_project_iam_member" "cloud-scheduler-dataflow" {
  project = var.project_id
  role = "roles/dataflow.admin"
  member = "serviceAccount:${google_service_account.cloud-scheduler-demo.email}"
}

resource "google_project_iam_member" "cloud-scheduler-bigquery" {
  project = var.project_id
  role = "roles/owner"
  member = "serviceAccount:${google_service_account.cloud-scheduler-demo.email}"
}

resource "google_project_iam_member" "cloud-scheduler-gcs" {
  project = var.project_id
  role = "roles/compute.storageAdmin"
  member = "serviceAccount:${google_service_account.cloud-scheduler-demo.email}"
}

