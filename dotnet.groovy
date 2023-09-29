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
        stage('Snyk SCA Test') 
        {
                 steps {
                    echo 'Testing...'
                    snykSecurity(
                        severity: 'low', snykInstallation: 'snyk@latest', snykTokenId: 'SNYK_TOKEN',
                        )

                      } 
        }
        stage('Snyk SAST Test') 
        {
                 steps {
                    //echo 'Testing...'
                    //snykSecurity(
                        //snykInstallation: 'snyk@latest', snykTokenId: 'SNYK_TOKEN',
                        //)
                     bat 'snyk auth 77b2b668-400e-4e4e-80f2-eafbfea28a1b'
                     bat 'snyk code test --report --project-name="devsecopswebapp"'
                      }
        }
                stage('Snyk DockerFile Test') 
        {
                 steps {
                    echo 'Testing...'
                    snykSecurity(
                        snykInstallation: 'snyk@latest', snykTokenId: 'SNYK_TOKEN',
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
