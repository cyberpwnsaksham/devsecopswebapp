pipeline {
    agent any

    environment {
        // Define environment variables as needed
        // DOTNET_VERSION = '6.0' // Change to your desired .NET version
        DOCKER_IMAGE_NAME = 'my-dotnet-app'
        DOCKER_REGISTRY_URL = 'bsaksham/dotnetwebapp:tagname'
    }

    stages {
        stage('Build') {
            steps {
                script {
                    // Install and restore .NET dependencies
                    bat "dotnet restore"
                    // Build the .NET project
                    bat "dotnet build"
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    // Run your tests here
                    bat "dotnet test"
                }
            }
        }
        stage('Snyk Test') {
      steps {
        echo 'Testing...'
        snykSecurity(
          snykInstallation: 'snyk1',
          //snykTokenId: '77b2b668-400e-4e4e-80f2-eafbfea28a1b',
        )
      }
    }
        stage('Containerize') {
            steps {
script {
    // Authenticate with Docker Hub (if not already authenticated)
    bat "docker login -u bsaksham --password-stdin Docker@123"

    // Build a Docker image for your .NET application
    bat "docker build -t bsaksham/dotnetwebapp:1.0 ."

    // Push the Docker image to Docker Hub
    bat "docker push bsaksham/dotnetwebapp:1.0"
}

            }
        }

    }
}
