def fetchCode() {
    echo "Pull Source code from Git"
    git branch: 'feature/jenkins-terra-eks', url: 'https://github.com/seunayolu/maven-jenkins-project.git'
} 

def buildWar() {
    echo "Building WAR with Maven"
    sh 'mvn clean install -DskipTests'
} 

def buildImage() {
    dockerImage = docker.build(awsEcrRegistry + ":$BUILD_NUMBER", "./Docker-files/app/multistage/")
}

def pushImage() {
    docker.withRegistry (devopsacadEcrImgReg, awsEcrCreds) {
        dockerImage.push ("$BUILD_NUMBER")
        dockerImage.push ('latest')
    }
}

def provisionServer() {
    withAWS(credentials: 'JenkinsAWSCLI', region: "${awsRegion}") {
        dir('terraform') {
            sh 'terraform init'
            sh 'terraform apply --auto-approve'
            EKS_CLUSTER_NAME = sh(
                script: "terraform output cluster_name",
                returnStdout: true
            ).trim()
        }
    }
}

def connectK8s() {
    echo "${EKS_CLUSTER_NAME}"

    withAWS(credentials: 'JenkinsAWSCLI', region: "${awsRegion}") {
        sh "aws eks update-kubeconfig --name ${EKS_CLUSTER_NAME} --region ${awsRegion}"
        sh 'kubectl get nodes'
        sh 'kubectl apply -f app.yaml'
    }
}

return this
