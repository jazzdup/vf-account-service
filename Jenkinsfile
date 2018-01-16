#!/usr/bin/env groovy
/**
 *
 */
pipeline {
    agent {
        docker {
            //image 'raghera/java8-maven3-git-versioned'
            image 'paasmule/java-maven-git-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    options {
        skipDefaultCheckout true
    }

    environment {
        APP_VERSION = ''
        DEVELOPMENT_BRANCH_NAME = 'develop'

        GIT_GROUP_ID = 'charging-platform'
        GIT_PROJECT_ID = 'vf-account-service'
        GIT_URL = "ci2.vfpartnerservices.com/"
        JENKINS_BUILD_BRANCH_NAME = buildBranchName()
    }

    stages {
        stage('Prepare Workspace') {
            steps {

                println('Clean workspace')
                deleteDir()
                incrementApplicationVersion()
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
        stage('Update Version') {
            steps {
                gitCodecheckIn()
            }
        }
        //Relies on Nexus being configured on Jenkins correctly
        stage('Publish') {
            steps {
                publishToNexus()
            }
        }
        stage('Deploy to Dev') {
            steps {
                echo "deploy to development ..."
            }
        }
    }
}

String getPomAppVersion() {
    pom = readMavenPom file: 'pom.xml'
    def version = pom.version
    return version

}

Map populatePomValuesMap() {

    pom = readMavenPom file: 'pom.xml'
    def POM_VALUES_MAP = [:]
    POM_VALUES_MAP.put('name', pom.name)
    POM_VALUES_MAP.put('version', pom.version)
    POM_VALUES_MAP.put('artifactId', pom.artifactId)
    POM_VALUES_MAP.put('groupId', pom.groupId)

    println "The name is: ${POM_VALUES_MAP.get('name')}"
    println "The version is: ${POM_VALUES_MAP.get('version')}"
    println "The artifactId is: ${POM_VALUES_MAP.get('artifactId')}"
    println "The groupId is: ${POM_VALUES_MAP.get('groupId')}"

    return POM_VALUES_MAP

}


String updatePomVersion() {

    println 'OLD pom version ' + getPomAppVersion()

    def command = 'mvn build-helper:parse-version versions:set ' +
            '-DnewVersion=' +
            '\\${parsedVersion.majorVersion}' +
            '.\\${parsedVersion.minorVersion}' +
            '.\\${parsedVersion.nextIncrementalVersion} versions:commit'

    println "Running shell command: $command"

    sh command

    println 'NEW pom version ' + getPomAppVersion()

    return getPomAppVersion()
}

def gitCodecheckIn() {
    sh "git push -u origin $DEVELOPMENT_BRANCH_NAME"
}

def incrementApplicationVersion() {

    println "incrementing application version"

    withCredentials([[$class          : 'UsernamePasswordMultiBinding',
                      credentialsId   : 'jenkins',
                      usernameVariable: "GIT_USER",
                      passwordVariable: "GIT_ACC_TOKEN"]]) {

        //These credentials need to be bound in the Jenkins credentials configuration
        //Otherwise the full string would have to be hardcoded here.
        sh "git clone https://$GIT_USER:$GIT_ACC_TOKEN" + "@$GIT_URL$GIT_GROUP_ID/$GIT_PROJECT_ID" + ".git $env.WORKSPACE"

        sh "git config user.name \"jenkins\" && git config user.email \"jenkins@example.com\""
        sh "git checkout $DEVELOPMENT_BRANCH_NAME"

        APP_VERSION = updatePomVersion()

        sh "git commit -am 'Jenkins commit of new version ' "

    }

}

def publishToNexus() {
    println "Publishing artifact to Nexus version: $APP_VERSION"
    Map pomInfo = populatePomValuesMap()
    nexusPublisher nexusInstanceId: 'localNexus',
            nexusRepositoryId: 'releases',
            packages: [[$class         : 'MavenPackage',
                        mavenAssetList : [[classifier: '',
                                           extension : '',
                                           filePath  : "target/vf-account-service-${APP_VERSION}.jar"]],
                        mavenCoordinate: [artifactId: pomInfo.get('artifactId'),
                                          groupId   : pomInfo.get('groupId'),
                                          packaging : 'jar',
                                          version   : "${APP_VERSION}"]]]
}

String buildBranchName() {
    def now = new Date()
    def timestamp = now.format("yyyyMMdd-HH:mm:ss.SSS", TimeZone.getTimeZone('UTC'))
    return "-$timestamp"
}