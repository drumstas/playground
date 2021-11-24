locals {
  oidc_providers = {
    github = {
      oidc_provider_url              = "https://token.actions.githubusercontent.com"
      oidc_audition                  = ["sts.amazonaws.com"]
      oidc_iam_deploy_condition_tpl  = "repo:%s:ref:refs/heads/%s"
      oidc_iam_plan_condition_tpl    = "repo:%s:pull_request"
      oidc_iam_deploy_condition_test = "StringLike"
      oidc_iam_plan_condition_test   = "StringLike"
      oidc_iam_condition_var         = "token.actions.githubusercontent.com:sub"
    }
    gitlab = {
      oidc_provider_url              = "https://gitlab.com"
      oidc_audition                  = ["https://gitlab.com"]
      oidc_iam_deploy_condition_tpl  = "project_path:%s:ref_type:branch:ref:%s"
      oidc_iam_plan_condition_tpl    = "project_path:%s:*"
      oidc_iam_deploy_condition_test = "StringEquals"
      oidc_iam_plan_condition_test   = "StringLike"
      oidc_iam_condition_var         = "gitlab.com:sub"
    }
  }
}

module "remote_state_setup" {
  source = "./modules/remote"

  aws_region                     = var.region
  s3_bucket_name                 = "${var.aws_account_id}-tf-remote-state"
  dynamodb_table_name            = "${var.aws_account_id}-tf-state-locking"
  application_name               = var.application_name
  repo_path                      = var.repo_path
  repo_branch                    = var.repo_branch
  oidc_provider_url              = local.oidc_providers[var.repo_host].oidc_provider_url
  oidc_audition                  = local.oidc_providers[var.repo_host].oidc_audition
  oidc_iam_plan_condition_tpl    = local.oidc_providers[var.repo_host].oidc_iam_plan_condition_tpl
  oidc_iam_plan_condition_test   = local.oidc_providers[var.repo_host].oidc_iam_plan_condition_test
  oidc_iam_deploy_condition_tpl  = local.oidc_providers[var.repo_host].oidc_iam_deploy_condition_tpl
  oidc_iam_deploy_condition_test = local.oidc_providers[var.repo_host].oidc_iam_deploy_condition_test
  oidc_iam_condition_var         = local.oidc_providers[var.repo_host].oidc_iam_condition_var
}
