#!/usr/bin/env groovy
pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    environment {
        APP_VERSION='0.0.2'
    }

    stages {
        stage('Prepare Build') {
            steps {
                echo "Creating new artifact.  APPLICATION_VERSION= $APP_VERSION"
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
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }

    }

}