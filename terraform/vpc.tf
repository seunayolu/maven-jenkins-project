module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.1.2"

  name = "class-vpc"
  cidr = var.vpc_cidr_block
  private_subnets = var.private_subnet_cidr_blocks
  public_subnets = var.public_subnet_cidr_blocks
  azs = data.aws_availability_zones.azs.zone_ids

  enable_nat_gateway = true
  single_nat_gateway = true
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    "kubernetes.io/cluster/class-eks-cluster" = "shared"
  }

  public_subnet_tags = {
    "kubernetes.io/cluster/class-eks-cluster" = "shared"
    "kubernetes.io/role/elb" = 1
  }

  private_subnet_tags = {
    "kubernetes.io/cluster/class-eks-cluster" = "shared"
    "kubernetes.io/role/internal-elb" = 1
  }


}