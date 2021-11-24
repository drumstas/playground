variable "application_name" {
  description = "Name of the application the infrastructure belongs to"
  type        = string
}
variable "environment" {
  description = "Enviroment eg. Staging or Production"
  type        = string
}
variable "region" {
  description = "Region that the instances will be created"
  type        = string
  default     = "eu-west-2"
}
variable "requestor" {
  type        = string
  description = "Who requested the environment"
}
variable "project_code" {
  type        = string
  description = "Project code, used to cross-charge resources against project's budget"
}
variable "ami" {
  type        = string
  description = "AMI to use for the instance."
}
variable "instance_type" {
  type        = string
  description = "The instance type to use for the instance."
  default     = "t3.nano"
}
variable "api_key_parameter_arn" {
  type        = string
  description = "ARN for the SSM Param Store Parameter that contains the value for our secret API Key"
}
variable "api_url_parameter_arn" {
  type        = string
  description = "ARN for the SSM Param Store Parameter that contains the value for the API Url"
}