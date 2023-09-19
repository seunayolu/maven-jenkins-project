provider "kubernetes" {
  host = module.eks.cluster_endpoint
  cluster_ca_certificate = base64decode(module.eks.cluster_certificate_authority_data)
}

provider "aws" {
  region = var.region
}

terraform {
  backend "s3" {
    bucket         	   = "workspace-terrabackend"
    key                = "state/terraform.tfstate"
    region         	   = "eu-central-1"
  }
}

data "aws_availability_zones" "azs" {}

locals {
  cluster_name = var.clusterName
}

