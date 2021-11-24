data "tls_certificate" "oidc_certificate" {
  url = var.oidc_provider_url
}

resource "aws_iam_openid_connect_provider" "oidc_provider" {
  url             = var.oidc_provider_url
  client_id_list  = var.oidc_audition
  thumbprint_list = [data.tls_certificate.oidc_certificate.certificates[0].sha1_fingerprint]
}

data "aws_iam_policy_document" "assume_role_policy" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]
    principals {
      identifiers = [aws_iam_openid_connect_provider.oidc_provider.arn]
      type        = "Federated"
    }
    condition {
      test     = var.oidc_iam_deploy_condition_test
      values   = [format(var.oidc_iam_deploy_condition_tpl, var.repo_path, var.repo_branch)]
      variable = var.oidc_iam_condition_var
    }
  }
}

resource "aws_iam_role" "deploy_role" {
  name                = "TerraformDeployRole"
  assume_role_policy  = data.aws_iam_policy_document.assume_role_policy.json
  managed_policy_arns = ["arn:aws:iam::aws:policy/AdministratorAccess"]
}


data "aws_iam_policy_document" "assume_plan_role_policy" {
  statement {
    effect  = "Allow"
    actions = ["sts:AssumeRoleWithWebIdentity"]
    principals {
      identifiers = [aws_iam_openid_connect_provider.oidc_provider.arn]
      type        = "Federated"
    }
    condition {
      test     = var.oidc_iam_plan_condition_test
      values   = [format(var.oidc_iam_plan_condition_tpl, var.repo_path)]
      variable = var.oidc_iam_condition_var
    }
  }
}

resource "aws_iam_role" "plan_role" {
  name                = "TerraformPlanRole"
  assume_role_policy  = data.aws_iam_policy_document.assume_plan_role_policy.json
  managed_policy_arns = ["arn:aws:iam::aws:policy/ReadOnlyAccess"]
}

resource "aws_iam_role_policy_attachment" "plan_role_lock_write_policy_attachment" {
  policy_arn = aws_iam_policy.dynamodb_table_access.arn
  role       = aws_iam_role.plan_role.name
}

resource "aws_iam_role_policy_attachment" "plan_role_sops_decrypt_policy_attachment" {
  policy_arn = aws_iam_policy.kms_sops_access.arn
  role       = aws_iam_role.plan_role.name
}