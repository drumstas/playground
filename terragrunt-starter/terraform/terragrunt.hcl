# ---------------------------------------------------------------------------------------------------------------------
# TERRAGRUNT CONFIGURATION
# Terragrunt is a thin wrapper for Terraform that provides extra tools for working with multiple Terraform modules,
# remote state, and locking: https://github.com/gruntwork-io/terragrunt
# ---------------------------------------------------------------------------------------------------------------------

# Configure Terragrunt to automatically store tfstate files in an S3 bucket
remote_state {
  backend = "s3"

  config = {
    encrypt = true
    bucket  = format("%s-tf-remote-state", local.account_id)
    # the replace function strips the first path element
    key            = "${replace(path_relative_to_include(), "/^[^/]+//", "")}/terraform.tfstate"
    region         = "eu-west-2"
    dynamodb_table = format("%s-%s-tf-state-locking",local.application, local.account_id)
  }

  disable_init = tobool(get_env("TERRAGRUNT_DISABLE_INIT", "false"))
}
terraform {
  before_hook "validate_hook_sec" {
    commands = ["validate"]
    execute  = ["tfsec"]
  }
  before_hook "validate_hook_lint" {
    commands = ["validate"]
    execute  = ["tflint"]
  }
}

# ---------------------------------------------------------------------------------------------------------------------
# GLOBAL PARAMETERS
# These variables apply to all configurations in this subfolder. These are automatically merged into the child
# `terragrunt.hcl` config via the include block.
# ---------------------------------------------------------------------------------------------------------------------

locals {
  secret_vars  = yamldecode(sops_decrypt_file(find_in_parent_folders("secrets.yaml")))
  global_vars  = yamldecode(file("${find_in_parent_folders("global.yaml")}"))
  env_vars     = yamldecode(file("${find_in_parent_folders("env.yaml")}"))

  # calculate some local variables to remove the need to use the complex "envs" map directly in terraform inputs
  environment           = local.env_vars["environment"]
  application           = local.global_vars["application_name"]
  account_id            = local.env_vars["aws_account_id"]
  state_variables = {
    state_s3_bucket       = format("%s-tf-remote-state", local.account_id)
    state_dynamodb_table  = format("%s-%s-tf-state-locking",local.application, local.account_id)
  }

}

# Configure root level variables that all resources can inherit. This is especially helpful with multi-account configs
# where terraform_remote_state data sources are placed directly into the modules.
inputs = merge(
  # Configure Terragrunt to use common vars encoded as yaml to help you keep often-repeated variables (e.g., account ID)
  # DRY. We use yamldecode to merge the maps into the inputs, as opposed to using varfiles due to a restriction in
  # Terraform >=0.12 that all vars must be defined as variable blocks in modules. Terragrunt inputs are not affected by
  # this restriction.
        local.secret_vars,
        local.global_vars,
        local.env_vars,
        local.state_variables
)