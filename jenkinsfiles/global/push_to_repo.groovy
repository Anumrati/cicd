def stages(){
    stage ("Update helm chart"){
        container ("python"){
            sh '''
                    pip install ruamel.yaml
                    
                    PWD=`pwd`
                    python $PWD/help_files/jenkinsfiles/scripts/setter.py $PWD/help_files/$HELM_CHART image.repository $DOCKER_REPOSITORY
                    python $PWD/help_files/jenkinsfiles/scripts/setter.py $PWD/help_files/$HELM_CHART image.tag $DOCKER_TAG
                    
                '''
        }
        sh ('''
                cd help_files
                git config --global user.email "jenkins@anum.com"
                git config --global user.name "Jenkins"
                git add $HELM_CHART
                git status
                git commit -m "$jobconsolename new version $DOCKER_TAG"
            ''')
        withCredentials([usernamePassword(credentialsId: 'Github', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            sh '''
                    cd help_files
                    git pull https://$USERNAME:$PASSWORD@github.com/Anumrati/cicd.git HEAD:main --rebase
                    git push https://$USERNAME:$PASSWORD@github.com/Anumrati/cicd.git HEAD:main
                '''
        }
    }
}

return this