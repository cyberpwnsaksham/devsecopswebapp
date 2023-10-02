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
                     //bat 'snyk code test --report --project-name="devsecopswebapp"'
                    catchError 
                     {
                     bat 'snyk code test'
                     //bat 'call exit(0)'
                     //bat 'snyk code test --json | snyk-to-html -o resultsSAST.html'
                     }
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
                        stage('Snyk Container Image Test') 
        {
                 steps {
                     catchError {
                bat 'snyk container test bsaksham/dotnetwebapp:third'
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
        stage('Deploying to LocalHost')
            {
                steps {
                    bat 'docker run -d -p 9020:5000 bsaksham/dotnetwebapp:third'
                        }
        }
}
}
