pipeline {
    agent any

    tools {
        maven "MAVEN3"
        jdk "oracleJDK8"
    }

    environment {
        awsEcrCreds = 'ecr:eu-west-2:JenkinsAWSCLI'
        awsEcrRegistry =  "392102158411.dkr.ecr.eu-west-2.amazonaws.com/devopsacad-d-image"
        devopsacadEcrImgReg = "https://392102158411.dkr.ecr.eu-west-2.amazonaws.com"
        awsRegion = 'eu-west-2'
        cluster = 'webapp'
        service = 'webapp-svc'
    }

    stages {
        stage ('fetch code') {
            steps {
                script {
                    echo "Pull Source code from Git"
                    git branch: 'docker', url: 'https://github.com/seunayolu/maven-jenkins-project.git'
                }
            }
        }

        stage ('Build App') {
            steps {
                script {
                    echo "Building WAR with Maven"
                    sh 'mvn install -DskipTests'
                }
            }
        }

        stage ('Build Docker Image') {
            steps{
                script {
                    dockerImage = docker.build(awsEcrRegistry + ":$BUILD_NUMBER", "./Docker-files/app/multistage/")
                }
            }
        }

        stage ('Push Image to ECR') {
            steps{
                script {
                    docker.withRegistry (devopsacadEcrImgReg, awsEcrCreds) {
                        dockerImage.push ("$BUILD_NUMBER")
                        dockerImage.push ('latest')
                    }
                }
            }
        }

        stage ('Deploy to ECS') {
            steps {
                script {
                    withAWS(credentials: 'JenkinsAWSCLI', region: "${awsRegion}") {
                        sh 'aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment'
                    }
                }
            }
        }
    }
}
