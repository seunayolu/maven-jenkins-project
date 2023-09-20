def gv

pipeline {
    agent any
    environment {
        awsEcrCreds = 'ecr:eu-central-1:JenkinsAWSCLI'
        awsEcrRegistry =  "392102158411.dkr.ecr.eu-central-1.amazonaws.com/devopsacad-d-image"
        devopsacadEcrImgReg = "https://392102158411.dkr.ecr.eu-central-1.amazonaws.com"
        awsRegion = "eu-central-1"
    }
    tools {
        maven "MAVEN3"
        jdk "OracleJDK8"
    }
    stages {
        stage ("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage ('Fetch Code') {
            steps {
                script {
                    echo "Fetching Code from GIT"
                    gv.fetchCode()
                }
            }
        }
        stage ('Build') {
            steps{
                script {
                    echo "Building the WAR file"
                    gv.buildWar()
                }
            }
            post {
                success {
                    echo 'Archiving artifacts now.'
                    archiveArtifacts artifacts: '**/*.war'
                }
            }
        }
        stage ('Build App Image') {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }
        stage ('Push Image to ECR') {
            steps {
                script {
                    gv.pushImage()
                }
            }
        }

        stage ('Provision EKS') {
            steps {
                script {
                    gv.provisionServer()
                }
            }
        }

        stage ('Connect to AWS EKS') {
            steps {
                script {
                    gv.connectK8s()
                }
            }
        }
        
    }

}