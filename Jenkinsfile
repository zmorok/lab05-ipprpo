pipeline {
    agent any

    parameters {
        string(
            name: 'BANK_NAME',
            defaultValue: 'DemoBank',
            description: 'Название банка для текущего запуска Pipeline'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo "Building banking system for: ${params.BANK_NAME}"
                sh 'mvn compile'
            }
        }

        stage('Static Analysis') {
            steps {
                sh 'mvn checkstyle:check'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
}