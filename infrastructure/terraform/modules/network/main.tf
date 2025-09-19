terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

variable "vpc_cidr" {
  type    = string
  default = "10.20.0.0/16"
}

resource "aws_vpc" "gigcred" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "gigcred-vpc"
  }
}

output "vpc_id" {
  value = aws_vpc.gigcred.id
}
