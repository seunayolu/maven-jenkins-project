def fetchCode() {
    echo "Pull Source code from Git"
    git branch: 'feature/jenkins-terra-eks', url: 'https://github.com/seunayolu/maven-jenkins-project.git'
} 

def buildWar() {
    echo "Building WAR with Maven"
    sh 'mvn clean install -DskipTests'
} 

/*def unitTest() {
    echo "Code Testing"
    sh 'mvn test'
} 

def checkstyle() {
    echo "Performing Checkstyle Analysis on the Code"
    sh 'mvn checkstyle:checkstyle'
}*/

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
            EKS_CLUSTER_ENDPOINT = sh(
                script: "terraform output cluster_endpoint",
                returnStdout: true
            ).trim()
        }
    }
}

def connectK8s() {
    echo "${EKS_CLUSTER_ENDPOINT}"
    
    withAWS(credentials: 'JenkinsAWSCLI', region: "${awsRegion}") {
        sh "aws eks update-kubeconfig --name ${EKS_CLUSTER_ENDPOINT} --region ${awsRegion}"
        sh 'kubectl get nodes'
    }
}

return this
