# S3
# https://www.terraform.io/docs/providers/aws/r/s3_bucket.html

resource "aws_s3_bucket" "logs_bucket" {
  bucket        = format("%s-logs", var.s3_bucket_name)
  force_destroy = true
}

resource "aws_s3_bucket_acl" "logs_bucket_acl" {
  bucket = aws_s3_bucket.logs_bucket.id
  acl    = "log-delivery-write"
}

resource "aws_s3_bucket_server_side_encryption_configuration" "state_bucket_sse" {
  bucket = aws_s3_bucket.remote_state_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      kms_master_key_id = aws_kms_key.encryption_key.key_id
      sse_algorithm     = "aws:kms"
    }
  }
}

resource "aws_s3_bucket_logging" "state_bucket_versioning_logging" {
  bucket        = aws_s3_bucket.remote_state_bucket.id
  target_bucket = aws_s3_bucket.logs_bucket.id
  target_prefix = "logs/"
}

resource "aws_s3_bucket_versioning" "remote_state_bucket_versioning" {
  bucket = aws_s3_bucket.remote_state_bucket.id
  versioning_configuration {
    status = "Enabled"
  }
}


resource "aws_s3_bucket" "remote_state_bucket" {
  bucket = var.s3_bucket_name

  /*
    Note:
    Currently Terraform will not force destroy a bucket if it contains versioned files.
    All versions will have to be deleted manually first (ie. empty the bucket).
  */
  force_destroy = false

  tags = {
    Application = var.application_name
    Name        = "Terraform Remote State bucket"
    Comment     = "Used to store Terraform state files"
    Terraform   = "True"
  }
}

resource "aws_s3_bucket_acl" "remote_state_bucket_acl" {
  bucket = aws_s3_bucket.remote_state_bucket.id
  acl    = "private"
}