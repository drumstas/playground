output "secret_key_arn" {
  description = "key used to encrypt secrets"
  value       = module.params_key.key_arn
}

output "api_key_parameter_arn" {
  value = module.secrets.secrets["api_key"]
}

output "api_url_parameter_arn" {
  value = module.secrets.secrets["api_url"]
}