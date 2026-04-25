terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.28.0"
    }
  }
}

provider "aws" {
  region                      = "us-east-1"
  access_key                  = "test"
  secret_key                  = "test"
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    dynamodb = "http://localhost:4566"
  }

  default_tags {
    tags = {
      Project   = "order-service"
      ManagedBy = "Terraform"
    }
  }
}

resource "aws_dynamodb_table" "order_table" {
  name         = "orders"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "order_id"
  range_key    = "order_customer"

  attribute {
    name = "order_id"
    type = "S"
  }

  attribute {
    name = "order_customer"
    type = "S"
  }
}
