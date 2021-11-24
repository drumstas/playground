provider "aws" {
  region = var.region
}

module "params_key" {
  source  = "app.terraform.io/Aurora/kms-key/aws"
  version = "0.1.0"

  application_name = var.application_name
  key_name         = "params-key"
  environment      = var.environment
  requestor        = var.requestor
}

module "secrets" {
  source  = "app.terraform.io/Aurora/secrets/aws"
  version = "0.1.2"

  application_name = var.application_name
  environment      = var.environment
  kms_arn          = module.params_key.key_arn
  requestor        = var.requestor
  project_code     = var.project_code

  secrets = {
    api_key = var.api_key
    api_url = var.api_url
  }
}