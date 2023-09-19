module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "19.16.0"

  cluster_name = var.clusterName
  cluster_version = "1.27"

  subnet_ids = module.vpc.private_subnets
  vpc_id = module.vpc.vpc_id
  cluster_endpoint_public_access = true

  tags = {
    environment = "development"
    application = "class-eks"
  }

  eks_managed_node_groups = {
    class_one = {
      min_size = 1
      max_size = 3
      desired_size = 2

      instance_types = ["t2.small"]
    }

  }

}