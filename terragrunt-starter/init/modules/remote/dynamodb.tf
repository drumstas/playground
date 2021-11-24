# DynamoDB
# https://www.terraform.io/docs/providers/aws/r/dynamodb_table.html

resource "aws_dynamodb_table" "state_locking_table" {
  name           = "${var.application_name}-${var.dynamodb_table_name}"
  read_capacity  = var.dynamodb_read_capacity_units
  write_capacity = var.dynamodb_write_capacity_units
  hash_key       = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    Name      = "${var.application_name}-terraform-state-locking"
    Comment   = "Used for Terraform state locking and consistency"
    Terraform = "True"
  }
}
