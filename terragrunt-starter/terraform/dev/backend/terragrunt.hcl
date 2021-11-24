# Terragrunt will copy the Terraform configurations specified by the source parameter, along with any files in the
# working directory, into a temporary folder, and execute your Terraform commands in that folder.
terraform {
  source = "../../..//stacks/backend"
}

# Include all settings from the root terragrunt.hcl file
include {
  path = find_in_parent_folders()
}

dependency "secrets" {
  config_path = "../secrets"

  mock_outputs = {
    api_key_parameter_arn     = "arn:aws:ssm:eu-west-2:123456789012:parameter/mock-api_key_parameter_arn"
    api_url_parameter_arn     = "arn:aws:ssm:eu-west-2:123456789012:parameter/mock-api_url_parameter_arn"
  }
}


inputs = {
  api_key_parameter_arn        = dependency.secrets.outputs.api_key_parameter_arn
  api_url_parameter_arn        = dependency.secrets.outputs.api_url_parameter_arn
}
