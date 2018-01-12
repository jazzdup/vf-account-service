#!/usr/bin/env groovy
pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    environment {
        POM_APP_VERSION = getAppPomVersion()
        APP_VERSION = updatePomVersion("$POM_APP_VERSION")
    }

    stages {
        stage('Prepare Build') {
            steps {
                echo "CURRENT APP VERSION=$APP_VERSION"

                echo "Jenkins BUILD_TAG= $BUILD_TAG"
                echo "Call: mvn versions:set POM VERSION=$POM_APP_VERSION"
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

String updatePomVersion(String versionStr) {

//    String[] versions = versionStr.split('\\.')
//    assert versions.length == 3
//
//    int major = Integer.parseInt(versions[0])
//    int minor = Integer.parseInt(versions[1])
//
//    println "Previous inc number: " + versions[2]
//    int inc = Integer.parseInt(versions[2]) + 1
//    println "New inc number: $inc"
//
//    for (int i = 0; i < versions.length; i++) {
//        println "CURRENT APP MAJOR VERSION=" + versions[i]
//    }
//
//    println "New version to be updated: $major.$minor.$inc"
//
//    sh 'mvn build-helper:parse-version versions:set ' +
//                        "-DnewVersion=$major.$minor.$inc"

    'mvn build-helper:parse-version versions:set ' +
            '-DnewVersion=\${parsedVersion.majorVersion}\\' +
            '.\\${parsedVersion.nextMinorVersion}\\' +
            '.\\${parsedVersion.incrementalVersion}'

//    return "$major.$minor.$inc"
}