def stages(){
    stage ("Prepare env"){
        container ("adoptopenjdk11"){
            script {
                jobNameParts = env.JOB_NAME.tokenize('/') as String[];
                jobconsolename = jobNameParts.length <2 ? env.JOB_NAME : jobNameParts[jobNameParts.length - 2]
                env.jobconsolename = jobconsolename.replaceAll("\\.","-")
                env.BRANCH_NAME = env.BRANCH_NAME.toLowerCase()
                env.DOCKER_FILES = "help_files/jenkinsfiles/jdk/scripts/spring/*"
                env.CONFIG_PATH = ""
                DOCKER_BUILDER = sh(returnStdout: true, script: "echo \$DOCKER_BUILDER").trim()
                env.DOCKER_BUILDER = DOCKER_BUILDER.replaceAll(/(!|"|@|#|]$|%|&|\\/|\(|\)|=|\?)/, /\\$0/)
                env.DOCKER_TAG = env.jobconsolename+"_test_"+BUILD_NUMBER+"_"
                env.APP_SRC = "./target/*.jar"
                env.HELM_CHART = "helm_charts/microservice/java/values/test/"+env.jobconsolename+".yaml"
            }
            sh '''
                chmod +x mvnw
            '''
        }
    }
}

return this