provider "aws" {
  region = var.region
}

module "label" {
  source       = "app.terraform.io/Aurora/tags-v2/aws"
  version      = "1.0.6"
  application  = var.application_name
  name         = "starter-ec2"
  environment  = var.environment
  requestor    = var.requestor
  project_code = var.project_code
}

resource "aws_instance" "web" {
  ami           = var.ami
  instance_type = var.instance_type

  tags      = module.label.tags
  user_data = <<EOT
    #!/bin/bash
    echo export API_KEY_PARAM_ARN="${var.api_key_parameter_arn}" >> /etc/profile
    echo export API_URL_PARAM_ARN="${var.api_url_parameter_arn}" >> /etc/profile
  EOT
}