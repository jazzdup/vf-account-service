#!/usr/bin/env groovy
pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    environment {
        APP_VERSION = updatePomVersion()
        GIT_GROUP_ID = 'charging-platform'
        GIT_PROJECT_ID = 'vf-account-service'
        GIT_USER = 'jenkins'
        GIT_ACC_TOKEN = 'xbT-JNXwCr_de2_ESWLk'
        GIT_PROJECT_URL = "https://$GIT_USER:$GIT_ACC_TOKEN@" +
                "ci2.vfpartnerservices.com/" + "$GIT_GROUP_ID/$GIT_PROJECT_ID" + ".git"

        JENKINS_BUILD_BRANCH_NAME = buildBranchName()
    }

    stages {
        stage('Prepare Build') {
            steps {
                echo "GIT_PROJECT_URL=$GIT_PROJECT_URL"
                echo "GIT_PROJECT_URL=$JENKINS_BUILD_BRANCH_NAME"
                echo "NEW APP VERSION=$APP_VERSION"
                echo "Jenkins BUILD_TAG= $BUILD_TAG"
                echo "Jenkins BUILD_TAG= $currentBuild.number"

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
        //Relies on Nexus being configured on Jenkins correctly
        stage('Publish') {
            steps {
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
        stage('Deploy to Dev environment') {
            steps {
                echo "deploy to development"
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

def checkInCodeToGit(String url, String branchName) {

    println "running a sh command to check into git"

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