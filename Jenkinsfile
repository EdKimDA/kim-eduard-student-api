pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timeout(time: 20, unit: 'MINUTES')
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        STUDENT_NAME = 'Kim Eduard'
        STUDENT_ID = '37324'
        IMAGE_NAME = 'kim-eduard-student-api'
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                checkout scm
                sh '''
                    echo "Student: $STUDENT_NAME | IT2-2313 | ID: $STUDENT_ID"
                    git rev-parse HEAD
                    java -version
                    chmod +x gradlew
                '''
            }
        }

        stage('Build') {
            steps {
                sh './gradlew --no-daemon clean assemble'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew --no-daemon test'
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    def appVersion = sh(
                        script: "sed -n 's/^appVersion=//p' gradle.properties",
                        returnStdout: true
                    ).trim()
                    if (!(appVersion ==~ /\d+\.\d+\.\d+/)) {
                        error('appVersion must have the form major.minor.patch')
                    }
                    env.IMAGE_REFERENCE = "${env.IMAGE_NAME}:v${appVersion}-kim-eduard-37324"
                }
                sh 'docker build --tag "$IMAGE_REFERENCE" .'
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    docker image inspect "$IMAGE_REFERENCE" --format 'Image={{.Id}} Created={{.Created}} Size={{.Size}}'
                    echo "VERIFIED: $IMAGE_REFERENCE | $STUDENT_NAME | ID $STUDENT_ID"
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'build/libs/*.jar'
        }
    }
}
