# Terraform Module: terraform-aws-remote-state

This Terraform module provisions a S3 bucket for remote state storage and a DynamoDB table for state locking.

The S3 bucket is created with versioning, server-side encryption, and logging enabled. A custom KMS encryption key is created for use with the S3 bucket. Bucket access logs are sent to a seperate S3 bucket.

2 IAM groups are created allowing varying levels of access to the S3 bucket:

| Group               | Description                              |
|---------------------|------------------------------------------|
| terraform_rw_access | allows full access to the S3 bucket      |
| terraform_ro_access | allows read-only access to the S3 bucket |

A GitHub OIDC provider is created together with a Deploy role. The Deploy role's Trust Policy is limited to a specific branch within a specific repository.

## Prerequisites

Terraform version 1.0 or newer is required.


### Usage

1. Navigate to the `init` repository:  <br>
    `cd init/`
2. Initiate terraform working directory: <br>
    `terraform init`
3. Create Terraform Plan: <br>
    `aws-vault exec devaccount --no-session -- terraform plan --out=plan.tfplan`
   1. If you are using aws-vault, remember to pass in`--no-session` flag since you are calling IAM API
   2. You will be promted to fill in required variables defined in `/variables.tf`
4. Check that the plan looks ok. If you are happy, apply it: <br>
   `aws-vault exec devaccount --no-session -- terraform apply plan.tfplan`
5. Repeat steps 1-5 for any new AWS account. If you are running the steps on the same machine, remove the statefile and plan files from previous runs: <br>
   `rm  terraform.tfstate plan.tfplan`
