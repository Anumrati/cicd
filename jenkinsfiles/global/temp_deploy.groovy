def stages(){
    stage ("Generate temp deployment file"){
        script{
            env.name = sh(returnStdout: true, script: "cat /dev/urandom | tr -dc 'a-z' | fold -w 50 | head -n 1").trim()
            env.HELM_CHART = "helm_charts/microservice/java/values/values.yaml"
        }
        container ("python"){
            sh '''
                pip install ruamel.yaml
                PWD=`pwd`
                python $PWD/help_files/jenkinsfiles/scripts/setter.py $PWD/help_files/$HELM_CHART image.repository $DOCKER_REPOSITORY
                python $PWD/help_files/jenkinsfiles/scripts/setter.py $PWD/help_files/$HELM_CHART image.tag $DOCKER_TAG
                python $PWD/help_files/jenkinsfiles/scripts/setter.py $PWD/help_files/$HELM_CHART microservice.name $name
                python $PWD/help_files/jenkinsfiles/scripts/setter.py $PWD/help_files/$HELM_CHART replicaCount 1
            '''
        }
    }
    stage ("Deploy temp file"){
        container("helm"){
            sh '''
                cd help_files/helm_charts/microservice
                helm package java
                helm install $name -n temp -f java/values/values.yaml ./java-*.tgz
            '''
        }
    }
}

return this