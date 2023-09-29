pipeline {
    agent any
    environment {
        dockerImage = 'my-dotnet-app'
        registry = 'bsaksham/dotnetwebapp:third'
        registryCredential = 'dockerhub'
    }
    stages {
        stage('Build dotnet') {
            steps {
                script {
                    bat "dotnet restore"
                    bat "dotnet build"
                }
            }
        }
        stage('Test dotnet') {
            steps {
                script {
                    bat "dotnet test"
                }
            }
        }
        stage('Snyk SAST Test') 
        {
                 steps {
                     script {
                    //echo 'Testing...'
                    //snykSecurity(
                        //severity: 'low', snykInstallation: 'snyk1', snykTokenId: 'SNYK_TOKEN',
                        //)
                     snyk test
                      } }
        }
        stage('Snyk SCA Test') 
        {
                 steps {
                    echo 'Testing...'
                    snykSecurity(
                        snykInstallation: 'snyk1', snykTokenId: 'SNYK_TOKEN', targetFile: 'dotnetwebapp.csproj'
                        )
                      }
        }
                stage('Snyk DockerFile Test') 
        {
                 steps {
                    echo 'Testing...'
                    snykSecurity(
                        snykInstallation: 'snyk1', snykTokenId: 'SNYK_TOKEN', targetFile: 'Dockerfile'
                        )
                      }
        }
        stage('Building a Docker Image')
        {
            steps {
                script {
                    dockerImage = docker.build registry
                        }
                  }
        }
        stage('Pushing the image to HUB')
            {
                steps {
                    script {
                    docker.withRegistry('', registryCredential) 
                            {
                            dockerImage.push()
                            }
                        }
                    }

        }
}
}
