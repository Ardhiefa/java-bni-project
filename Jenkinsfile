pipeline{
    agent any

    environment {
        PROJECT_NAME = "ardhiefa-dev"
        BUILD_NAME = "java-bni-project-git"
    }

    stages {
        stage('Trigger Build in Openshift') {
            steps {
                sh "oc start-build ${BUILD_NAME} --from-dir=. --follow -n ${PROJECT_NAME}"
            }
        }
        stage('Deploy to Openshift') {
            steps {
                sh "oc rollout restart deployment/${BUILD_NAME} -n ${PROJECT_NAME}"
            }
        }

    }

    post {
        success {
            echo 'builddan deploy berhasil via openshift buildconfig.' 
        }
        failure {
            echo 'gagal menjalankan pipeline.'
        }
    }
}