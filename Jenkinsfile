#!/usr/bin/env groovy
pipeline {
    agent {
        docker {
//            image 'maven:3-alpine'
//            image 'raghera/java8-maven3-git-versioned'
            image 'paasmule/java-maven-git-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    options {
        skipDefaultCheckout true
    }

    environment {
        APP_VERSION = '0.0.0'
        GIT_GROUP_ID = 'charging-platform'
        GIT_PROJECT_ID = 'vf-account-service'
        GIT_USER = 'jenkins'
        GIT_ACC_TOKEN = 'xbT-JNXwCr_de2_ESWLk'
        GIT_URL = "ci2.vfpartnerservices.com/"
        GIT_PROJECT_URL = "https://$GIT_USER:$GIT_ACC_TOKEN@$GIT_URL$GIT_GROUP_ID/$GIT_PROJECT_ID" + ".git"
        GIT_PROJECT_URL_WITHOUT_USER_PASS = "https://$GIT_URL$GIT_GROUP_ID/$GIT_PROJECT_ID" + ".git"

        JENKINS_BUILD_BRANCH_NAME = buildBranchName()
    }

    stages {
        stage('Prepare Build') {
            steps {
                incrementApplicationVersion('develop')
                echo "GIT_PROJECT_URL=$GIT_PROJECT_URL"
                echo "JENKINS BRANCH NAME=$JENKINS_BUILD_BRANCH_NAME"
                echo "CURRENT APP VERSION=$APP_VERSION"
                echo "Jenkins BUILD_TAG=$BUILD_TAG"
                echo "Jenkins BUILD_NUMBER=$currentBuild.number"
            }
        }
        stage('Build..') {
            steps {
                echo 'Building..'
                sh 'mvn -B -DskipTests clean package'
            }
        }

        stage('Test') {
            steps {
                echo 'Testing..'
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Integration Test') {
            steps {
                echo 'Integration Test..'
                sh 'mvn failsafe:integration-test'
            }
            post {
                always {
                    junit 'target/failsafe-reports/*.xml'
                }
            }
        }
        stage('Git push') {
            steps {
                gitCodecheckIn()
            }
        }
        //Relies on Nexus being configured on Jenkins correctly
        stage('Publish') {
            steps {
                println "Publishing artifact to Nexus version: $APP_VERSION"
                nexusPublisher nexusInstanceId: 'localNexus',
                        nexusRepositoryId: 'releases',
                        packages: [[$class         : 'MavenPackage',
                                    mavenAssetList : [[classifier: '',
                                                       extension : '',
                                                       filePath  : "target/vf-account-service-${APP_VERSION}.jar"]],
                                    mavenCoordinate: [artifactId: 'vf-account-service',
                                                      groupId   : 'com.vodafone.charging',
                                                      packaging : 'jar',
                                                      version   : "${APP_VERSION}"]]]
            }
        }
        stage('Deploy to Dev') {
            steps {
                echo "deploy to development ..."
            }
        }
    }
}

String getAppPomVersion() {
    pom = readMavenPom file: 'pom.xml'
    def version = pom.version
    return version

}


String updatePomVersion() {

    println 'OLD pom version ' + getAppPomVersion()

    def command = 'mvn build-helper:parse-version versions:set ' +
            '-DnewVersion=' +
            '\\${parsedVersion.majorVersion}' +
            '.\\${parsedVersion.minorVersion}' +
            '.\\${parsedVersion.nextIncrementalVersion} versions:commit'

    println "Running shell command: $command"

    sh command

    println 'NEW pom version ' + getAppPomVersion()

//    checkInCodeToGit()

    return getAppPomVersion()
}

def gitCodecheckIn() {
    sh "git push -u origin develop"
}

def incrementApplicationVersion(String localBranchName) {

    println "incrementing application version"

    if (fileExists('/var/jenkins_home/workspace/example-pipeline')) {
        sh 'rm -r /var/jenkins_home/workspace/example-pipeline && mkdir /var/jenkins_home/workspace/example-pipeline'
    } else {
        sh 'mkdir /var/jenkins_home/workspace/example-pipeline'
    }

    dir('/var/jenkins_home/workspace/example-pipeline') {

        withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                          credentialsId   : 'jenkins',
                          usernameVariable: "GIT_USER",
                          passwordVariable: "GIT_ACC_TOKEN"]]) {

//            sh "git clone $GIT_PROJECT_URL /var/jenkins_home/workspace/example-pipeline"
            sh "git clone $GIT_PROJECT_URL_WITHOUT_USER_PASS /var/jenkins_home/workspace/example-pipeline"
            sh "git config user.name \"jenkins\" && git config user.email \"jenkins@example.com\""
            sh 'git checkout develop'

            APP_VERSION = updatePomVersion()

            sh "git commit -am 'Jenkins commit of new version ' "
        }

    }

}

def executShellCommand(String command) {
    def cmd = command
    def sout = new StringBuffer(), serr = new StringBuffer()
    def proc = cmd.execute()
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000)
    println sout
}

String buildBranchName() {
    int buildNumber = env.BUILD_NUMBER
    def now = new Date()
    def timestamp = now.format("yyyyMMdd-HH:mm:ss.SSS", TimeZone.getTimeZone('UTC'))
    return "build-$buildNumber-$timestamp"
}