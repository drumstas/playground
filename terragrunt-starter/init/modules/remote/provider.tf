# Terraform - AWS
# https://www.terraform.io/docs/providers/aws/index.html
# https://www.terraform.io/docs/configuration/interpolation.html
# https://www.terraform.io/docs/configuration/resources.html

terraform {
  required_version = "~> 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 4.0.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}
