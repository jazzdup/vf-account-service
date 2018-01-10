#!/usr/bin/env groovy
pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }

    stages {
        stage('Prepare Build') {
            node {
                env.APP_VERSION = '0.0.2'

                steps {
                    echo "Creating new artifact.  APPLICATION_VERSION= $APP_VERSION"
                }
            }
        }
        stage('Build..') {
            node {
                steps {
                    echo 'Building..'
                    sh 'mvn -B -DskipTests clean package'
                }
            }
        }
        stage('Test') {
            node {
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
        }
        stage('Integration Test') {
            node {
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
        }
        stage('Deploy') {
            node {
                steps {
                    echo 'Deploying....'
                }
            }
        }
    }

}
