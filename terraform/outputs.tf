output "cluster_name" {
  description = "Amazon Web Service EKS Cluster Name"
  value = module.eks.cluster_name
}

output "cluster_endpoint" {
  description = "Endpoint for Amazon Web Service EKS "
  value = module.eks.cluster_endpoint
}

output "region" {
  description = "Amazon Web Service EKS Cluster region"
  value = var.region
}
