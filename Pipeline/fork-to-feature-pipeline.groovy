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
                    //FORK CHECKOUT
                }
            }
        }
        stage('Scanning'){
            parallel{
                stage('Trivy - Filesystem scan'){
                    steps{
                        script{
                            //SHIFT_LEFT SCANNING
                        }
                    }
                }
                stage('SAST - SonarQube'){
                    steps{
                        script{
                            //SAST
                        }
                    }
                }
            }
        }
        stage('Quality Gate'){
            steps{
                script{
                    //QUALITY GATE VALIDATIOn
                }
            }
        }
        stage('Unit Testing and SBOM'){
            parallel{
                stage('Unit Testing'){
                    steps{
                        script{
                            //UNIT TEST
                        }
                    }
                }
                stage('SBOM Generation for source-code'){
                    steps{
                        script{
                            //SBOM OF SOURCE CODE
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
                            //BUILDING RAW ARTIFACTS - JAR/WAR/EAR
                        }
                    }
                }
                stage('Build Image'){
                    steps{
                        script{
                            //IMAGE BUILD
                        }
                    }
                }
            }
        }
        stage('Runtime Image Scanning'){
            steps{
                script{
                    //TRIVY SCANNING FOR VULN
                }
            }
        }
        stage('SBOM of Image'){
            steps{
                script{
                    //SBOM OF IMAGE
                }
            }
        }
    }
    post{
        success {
            mailNotification(
                buildStatus: 'SUCCESS'
                recipient: ''
            )    
            cleanWs()
        }
        failure {
            mailNotification(
                buildStatus: 'FAILURE'
                recipient: ''
            )
            cleanWs()

        }
    }
}
