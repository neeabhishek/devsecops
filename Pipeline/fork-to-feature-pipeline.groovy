/*

Author: Abhishek Neelakandan
Desc: CI workflow for untrusted branch (fork to feature)

*/

pipeline{
    agent any 
    environment{
        GITHUB_OWNER = "neeabhishek"
        GITHUB_REPO = "devsecops"
        IMAGE_REPO  = "neeabhishek/devsecops"
    }
    stages{
        stage('PR Validation and Fork details gathering'){
            steps{
                script{
                    //CHECK IF ANY PR HAS COME TO FEATURE, IF SO FETCH THE FORK URL AND BRANCH DETAILS
                }
            }
        }
        stage('Fork check-out'){
            steps{
                script{

                }
            }
        }
        stage('Scanning'){
            parallel{
                stage('Trivy - Filesystem scan'){
                    steps{
                        script{

                        }
                    }
                }
                stage('SAST - SonarQube'){
                    steps{
                        script{

                        }
                    }
                }
            }
        }
        stage('Quality Gate'){
            steps{
                script{

                }
            }
        }
        stage('Unit Testing and SBOM'){
            parallel{
                stage('Unit Testing'){
                    steps{
                        script{

                        }
                    }
                }
                stage('SBOM Generation for source-code'){
                    steps{
                        script{

                        }
                    }
                }
            }
        }
        stage('Build'){
            stages{
                stage('Build raw artifacts'){
                    steps{
                        script{

                        }
                    }
                }
                stage('Build Image'){
                    steps{
                        script{

                        }
                    }
                }
            }
        }
        stage('Runtime Image Scanning'){
            steps{
                script{

                }
            }
        }
        stage('SBOM of Image'){
            steps{
                script{

                }
            }
        }
    }
    post{
        success {
            mailNotification(
                buildStatus: 'SUCCESS'
                recipient: 'neeabhishek@gmail.com'
            )    
        }
        failure {
            mailNotification(
                buildStatus: 'FAILURE'
                recipient: 'neeabhishek@gmail.com'
            )

        }
        always {
            cleanWs()
        }
    }
}