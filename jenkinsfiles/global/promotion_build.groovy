node("jenkins-java") {
    properties([parameters([string(name: 'HELM_CHART', trim: true), string(name: 'DOCKER_REPOSITORY', trim: true), string(name: 'DOCKER_TAG', trim: true), string(name: 'MICROSERVICE', trim: true), booleanParam('prod')])])
    try {
        stage("Checkout pipelines") {
            checkout(scm: [$class           : 'GitSCM',
                           branches         : [[name: '*/main']],
                           extensions       : [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'help_files']],
                           userRemoteConfigs: [[credentialsId: 'Github', url: 'https://github.com/Anumrati/cicd.git']]])

        }
        stage("Tag image") {
            container("docker") {
                script {
                    env.DOCKER_TAG = params.DOCKER_TAG
                    env.HELM_CHART = params.HELM_CHART
                    env.jobconsolename = params.MICROSERVICE
                    if (params.prod) {
                        env.DOCKER_TAG = env.DOCKER_TAG.replace("_test_", "_preprod_")
                        env.DOCKER_TAG_NEW = params.DOCKER_TAG.replace("_preprod_", "_prd_")
                        env.HELM_CHART = params.HELM_CHART.replace("/test/", "/prd/")
                    } else {
                        env.DOCKER_TAG_NEW = params.DOCKER_TAG.replace("_test_", "_preprod_")
                        env.HELM_CHART = params.HELM_CHART.replace("/test/", "/preprod/")
                    }
                }
                sh '''
                    docker pull \$DOCKER_REPOSITORY:\$DOCKER_TAG
                    docker tag \$DOCKER_REPOSITORY:\$DOCKER_TAG \$DOCKER_REPOSITORY:\$DOCKER_TAG_NEW
                    docker push \$DOCKER_REPOSITORY:\$DOCKER_TAG_NEW
                '''
            }
            script {
                env.DOCKER_TAG = env.DOCKER_TAG_NEW
            }
        }
        load("help_files/jenkinsfiles/global/push_to_repo.groovy").stages()
    } catch (err) {

        load("help_files/jenkinsfiles/global/exception_handling.groovy").stages(err)

    }
}