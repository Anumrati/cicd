def stages(){
    stage ("Validate"){
        container ("adoptopenjdk11"){
            sh '''
                ./mvnw validate
            '''
        }
    }
    stage ("Compile"){
        container ("adoptopenjdk11"){
            sh '''
                ./mvnw compile
            '''
        }
    }
    stage ("Test"){
        container ("adoptopenjdk11"){
            try {
                sh '''
                    ./mvnw test -B
                '''
            } catch (err) {
                if (currentBuild.result == 'UNSTABLE')
                    currentBuild.result = 'FAILURE'
                throw err
            } finally {
                junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            }
        }
    }
    stage ("Package"){
        container ("adoptopenjdk11"){
            sh '''
                ./mvnw package
            '''
        }
    }
}

return this