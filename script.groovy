def fetchCode() {
    echo "Pull Source code from Git"
    git branch: 'feature/jenkins-terra-eks', url: 'https://github.com/seunayolu/maven-jenkins-project.git'
} 

def buildWar() {
    echo "Building WAR with Maven"
    sh 'mvn install -DskipTests'
} 

def unitTest() {
    echo "Code Testing"
    sh 'mvn test'
} 

def checkstyle() {
    echo "Performing Checkstyle Analysis on the Code"
    sh 'mvn checkstyle:checkstyle'
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

return this
