# Variables
# https://www.terraform.io/intro/getting-started/variables.html
# https://www.terraform.io/docs/configuration/variables.html

variable "aws_region" {
  description = "The AWS region"
}

variable "s3_bucket_name" {
  description = "The name of the S3 bucket which will be used to terraform store state files"
}

variable "dynamodb_table_name" {
  description = "The name of the DynamoDB table which will be used for state locking"
}

variable "dynamodb_read_capacity_units" {
  description = "The amount of read capacity units for the DynamoDB table"
  default     = 5
}

variable "dynamodb_write_capacity_units" {
  description = "The amount of write capacity units for the DynamoDB table"
  default     = 1
}

variable "iam_group_name_rw_access" {
  description = "The name of the IAM group that will have read-write access"
  default     = "terraform_rw_access"
}

variable "iam_group_name_ro_access" {
  description = "The name of the IAM group that will have read-only access"
  default     = "terraform_ro_access"
}

variable "application_name" {
  description = "Application name"
}

variable "repo_path" {
  type        = string
  description = <<-EOT
  Full repository path where this code will be hosted and where CI/CD will run.
  Github: elderstudios/your-repository
  Gitlab: akj-dev/your-group/your-repository
  EOT
}

variable "repo_branch" {
  type        = string
  description = "Git branch corresponding to the environment that will be deployed to this account (live/prod/main/...)"
}

variable "oidc_provider_url" {
  type        = string
  description = <<-EOT
  URL for the OIDC provider which will be used to deploy terraform changes.
  Github: https://token.actions.githubusercontent.com
  Gitlab: https://gitlab.com or custom domain
  EOT
  default     = "https://token.actions.githubusercontent.com"
}

variable "oidc_iam_deploy_condition_test" {
  type        = string
  description = "Name of the IAM condition type to evaluate OIDC for Deploy actions"
  default     = "StringLike"
}

variable "oidc_iam_deploy_condition_tpl" {
  type        = string
  description = "String template to format assume role policy condition for Deploy actions"
  default     = "repo:%s:ref:refs/heads/%s"
}

variable "oidc_iam_plan_condition_test" {
  type        = string
  description = "Name of the IAM condition type to evaluate OIDC for Plan-only actions"
  default     = "StringLike"
}

variable "oidc_iam_plan_condition_tpl" {
  type        = string
  description = "String template to format assume role policy condition for Plan-only actions"
  default     = "repo:%s:ref:refs/heads/%s"
}

variable "oidc_iam_condition_var" {
  type        = string
  description = "Variable name of the IAM condition type to evaluate OIDC"
  default     = "token.actions.githubusercontent.com:sub"
}

variable "oidc_audition" {
  type        = list(string)
  description = <<-EOT
  Allowed client ID
  Github: sts.amazonaws.com
  Gitlab: https://gitlab.com
  EOT
  default     = ["sts.amazonaws.com"]
}
