terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

module "network" {
  source   = "../../modules/network"
  vpc_cidr = var.vpc_cidr
}

resource "aws_kms_key" "gigcred" {
  description             = "GigCred encryption key"
  deletion_window_in_days = 30
}

output "vpc_id" {
  value = module.network.vpc_id
}
