variable "region" {
  description = "Region that the backend configuration will be created"
}

variable "application_name" {
  description = "Name of the application the infrastructure belongs to"
}

variable "aws_account_id" {
  description = "Will be used in a number of resource names"
}

variable "repo_host" {
  type        = string
  description = "OIDC provider: either 'gitlab' or 'github'"
  validation {
    condition     = can(regex("^(gitlab|github)$", var.repo_host))
    error_message = "The repo host can only be set to [gitlab|github]."
  }
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
