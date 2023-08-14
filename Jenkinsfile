def gv

def COLOR_MAP = [
    'SUCCESS': 'good', 
    'FAILURE': 'danger',
]

pipeline {
    agent any
    environment {
        awsEcrCreds = 'ecr:us-east-2:JenkinsAWSCLI'
        awsEcrRegistry =  "392102158411.dkr.ecr.us-east-2.amazonaws.com/devopsacad-d-image"
        devopsacadEcrImgReg = "https://392102158411.dkr.ecr.us-east-2.amazonaws.com"
        cluster = "classecstest"
        service = "classtasksvc"
        awsRegion = "us-east-2"
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
        stage ('UNIT TEST') {
            steps {
                script {
                    echo "Code Testing"
                    gv.unitTest()
                }
            }
        }
        stage ('Checkstyle Analysis') {
            steps {
                script {
                    gv.checkstyle()
                }
            }
            post {
                success {
                    echo 'Generated Analysis Result'
                }
            }
        }
        stage('Sonar Analysis') {
            environment {
                ScannerHome = tool 'sonar4.7'
            }
            steps{
                script {
                    gv.sonarAnalysis()
                }
            }
        }
        stage('Quality Gate') {
            steps {
                script {
                    gv.qualityGate()
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
        /*stage ('Deploy Image to ECS') {
            steps {
                withAWS(credentials: 'JenkinsAWSCLI', region: "${awsRegion}") {
                    sh 'aws ecs update-service --cluster ${cluster} --service ${service} --force-new-deployment'
                }
            }
        }*/
        stage('ArtifactUpload') {
            steps {
                script {
                    gv.pushNexus()
                }
            }
        }
        
    }
    post {
        always {
            echo 'Slack Notifications'
            slackSend channel: 'general',
                color: COLOR_MAP [currentBuild.currentResult],
                message: "*${currentBuild.currentResult}: * Job ${env.JOB_NAME} build ${env.BUILD_NUMBER}-${env.BUILD_TIMESTAMP} \n More info at: ${env.BUILD_URL}"
        }
    }
}