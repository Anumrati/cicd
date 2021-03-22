def stages(){
    if (env.BRANCH_NAME ==~ "release/.*" || env.BRANCH_NAME =~ "^pr.*") {
        stage("Build docker images") {
            script {
                env.DOCKER_REPOSITORY = "anumrati/spring-petclinic"
                env.DOCKER_TAG = env.DOCKER_TAG + env.BUILD_TIMESTAMP
                env.DOCKER_TAG = env.DOCKER_TAG =~ "release/*" ? env.DOCKER_TAG + env.BUILD_TIMESTAMP : env.DOCKER_TAG + env.BUILD_TIMESTAMP + "_temp"
                env.DOCKER_TAG = env.DOCKER_TAG.replaceAll(" ", "")
            }
            container("docker") {
                sh '''
                    cp $DOCKER_FILES .
                    sed -i "s/DOCKER_BUILDER/\$DOCKER_BUILDER/gI" Dockerfile
                    docker build --pull --build-arg app_src=$APP_SRC . -t \$DOCKER_REPOSITORY:\$DOCKER_TAG
                    docker push \$DOCKER_REPOSITORY:\$DOCKER_TAG
                '''
            }
        }
    }
    if (env.BRANCH_NAME =~ "^pr.*") {
        load("help_files/jenkinsfiles/global/temp_deploy.groovy").stages()
    } else if (env.BRANCH_NAME ==~ "release/.*") {
        load("help_files/jenkinsfiles/global/push_to_repo.groovy").stages()
        stage ("Create promotion build"){
            build job: 'promotion',
            parameters: [
                    string(name: 'HELM_CHART', value: HELM_CHART),
                    string(name: 'DOCKER_REPOSITORY', value: DOCKER_REPOSITORY),
                    string(name: 'DOCKER_TAG', value: DOCKER_TAG),
                    string(name: 'MICROSERVICE', value: jobconsolename)
            ]
        }
    }
}

return this