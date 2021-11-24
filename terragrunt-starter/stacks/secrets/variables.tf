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
variable "api_key" {
  type        = string
  description = "A secret value which I want to pass put into Parameter Store"
}
variable "api_url" {
  type        = string
  description = "Non-secret value, but I still want to put it into Parameter Store so that other services can read from it"
}