def fetchCode() {
    echo "Pull Source code from Git"
    git branch: 'docker', url: 'https://github.com/seunayolu/maven-jenkins-project.git'
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

def sonarAnalysis() {
    withSonarQubeEnv('sonar') {
        sh ''' ${ScannerHome}/bin/sonar-scanner -Dsonar.projectKey=devopsacad-docker \
            -Dsonar.projectName=devopsacad-docker \
            -Dsonar.projectVersion=1.1 \
            -Dsonar.sources=src/ \
            -Dsonar.java.binaries=target/test-classes/com/visualpathit/account/controllerTest/ \
            -Dsonar.junit.reportsPath=target/surefire-reports/ \
            -Dsonar.jacoco.reportsPath=target/jacaco.exec \
            -Dsonar.java.checkstyle.reportPaths=target/checkstyle-result.xml'''
    }
}

def qualityGate() {
    timeout(time: 1, unit: 'HOURS') {
        // Parameter indicates whether to set pipeline to UNSTABLE if Quality Gate fails
        // true = set pipeline to UNSTABLE, false = don't
        waitForQualityGate abortPipeline: true
    }
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

def pushNexus() {
    nexusArtifactUploader(
        nexusVersion: 'nexus3',
        protocol: 'http',
        nexusUrl: '172.31.11.131:8081',
        groupId: 'com.devopsacad',
        version: "${env.BUILD_ID}-${env.BUILD_TIMESTAMP}",
        repository:'devopsacad',
        credentialsId: 'JenkinsNexus',
        artifacts: [
            [artifactId:'devopsacad',
            classifer: '',
            file: 'target/devopsacad-v2.war',
            type: 'war']
            ]
    )
}

return this
