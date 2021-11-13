pipeline {
    agent any

    stages {
        stage('Pre-setup') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    sh 'pip3 install poetry'
                }
            }
        }
        stage('Installing dependencies') {
            steps {
                timeout(time: 2, unit: 'MINUTES') {
                    sh 'python3 -m poetry install -vvv'
                }
            }
        }
        stage('Linting') {
            steps {
                sh 'python3 -m poetry run pylint apollo'
            }
        }
        stage('Build') {
            steps {
                sh 'python3 -m poetry build'
            }
        }
    }
}
