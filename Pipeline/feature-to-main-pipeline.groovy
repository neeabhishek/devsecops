@Library('devsecops-lib@main') _

/*
Author: Abhishek Neelakandan
Desc: CICD + Supply chain security Pipline for trusted branch (feature to main) with Shared libraries
*/

def FAILED_STAGE = "NA"

pipeline{
    agent any
    parameters {
        string(name: 'GIT_URL' , defaultValue: '' , description: 'GIT URL')
    }
    environment{
        NAMESPACE = ""
        SERVICE_NAME = ""
        TARGET_BRANCH = ""
        SOURCE_BRANCH = ""
        REPO = ""
        TRIVY_TPL_PATH = ""
        SONAR_TOKEN = credentials('')
        GITHUB_TOKEN = credentials('')
    }
    stages{
        stage('PR Validation'){
            steps{
                script {
                    FAILED_STAGE = env.STAGE_NAME
                    echo("Starting PR Validation/Check")
                    def validation = sh (
                        script: """
                            curl -s \
                            -H "Authorization: Bearer ${GITHUB_TOKEN}" \
                            "https://api.github.com/repos/neeabhishek/devsecops/pulls?state=open" \
                            | jq -r '.[] |
                            select(.head.ref=="feature" and .base.ref=="main")
                            | .number'
                        """,
                        returnStdout: true
                    ).trim()
                    if (validation?.trim()){
                        env.PR_NUMBER = validation
                        echo("PR ${env.PR_NUMBER} has been detected against main branch, starting security, build, and supply chain stages")
                    } else {
                        error("There are no open PR against main branch, hence skipping security, build, and supply chain stages")
                    }
                }
            }
        }
        stage('Git check-out'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    gitCheckOut(
                        url: "${params.GIT_URL}",
                        branch: "${env.TARGET_BRANCH}",
                        credentialsId: "GIT-CRED",
                    )
                }
            }
        }
        stage('Local Merge'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    echo("Creating a local merge for further stage workflows")
                    def local_merge = sh(
                        script: """
                            cd ${env.WORKSPACE} && \
                            git fetch origin ${env.TARGET_BRANCH}
                            git fetch origin ${env.SOURCE_BRANCH}
                            git checkout ${env.TARGET_BRANCH} 
                            git merge origin/${env.SOURCE_BRANCH}
                        """,
                        returnStatus: true
                    )
                    errorLogging(
                        statusCode: local_merge,
                        stageName: "${env.STAGE_NAME}",
                        operation: "Local Merge"
                    )
                }
            }
        }
        stage('Scanning'){
            parallel{
                stage('Trivy - Filesystem scan'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            def fs_scan = trivyScan.fileSystemScan(
                                reportName: "fs-trivy-report.html"
                            )
                            errorLogging(
                                statusCode: fs_scan,
                                stageName: "${env.STAGE_NAME}",
                                operation: "Shift-Left Scanning of repo"
                            )
                        }
                    }
                }
                stage('SAST-SonarQube'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            def sast = sastAnalysis(
                                serviceName: "${env.SERVICE_NAME}",
                                sonarQubeCred: "${env.SONAR_TOKEN}"
                            )
                            errorLogging(
                                statusCode: sast,
                                stageName: "${env.STAGE_NAME}",
                                operation: "SAST"
                            )
                            
                        }
                    }
                }
            }
        }
        stage('Quality Gate'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    timeout(time: 5, unit: 'MINUTES') {
                        waitForQualityGate abortPipeline: true
                    }

                }
            }
        }
        stage('Unit testing and SBOM'){
            parallel{
                stage('Unit Testing'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            def unit_test = buildAndTest.unitTesting()
                            errorLogging(
                                statusCode: unit_test,
                                stageName: "${env.STAGE_NAME}",
                                operation: "Unit Testing"
                            )
                        }
                    }
                }
                stage('SBOM Generation for source-code'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            def sbom = sbomGeneration.sbomFileSystem(
                                outputFileName: 'sbom-sourceCode.json'
                            )
                            errorLogging(
                                statusCode: sbom,
                                stageName: "${env.STAGE_NAME}",
                                operation: "SBOM generation of source-code"
                            )
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
                            FAILED_STAGE = env.STAGE_NAME
                            def build_rawArtifacts = buildAndTest.buildArtifacts()
                            errorLogging(
                                statusCode: build_rawArtifacts,
                                stageName: "${env.STAGE_NAME}",
                                operation: "Building raw artifacts"
                            )
                        }
                    }
                }
                stage('Build Image'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            def build_image = buildAndTest.buildImage()
                            errorLogging(
                                statusCode: build_image,
                                stageName: "${env.STAGE_NAME}",
                                operation: "Building Image"
                            )
                        }
                    }
                }
            }
        }
        stage('Runtime Image Scanning'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    def image_scan = trivyScan.imageScan(
                        reportName: 'trivy-image-report.html'
                    )
                    errorLogging(
                        statusCode: image_scan,
                        stageName: "${env.STAGE_NAME}",
                        operation: "Trivy Sacnning of Image"
                    )
                }
            }
        }
        stage('SBOM of Image'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    def image_sbom = sbomGeneration.sbomImage(
                        outPutFileName: 'image-sbom-spdx.json'
                    )
                    errorLogging(
                        statusCode: image_sbom,
                        stageName: "${env.STAGE_NAME}",
                        operation: "SBOM generation of Image"
                    )
                }
            }
        }
        stage('Push Artifacts'){
            parallel{
                stage('Push raw artifacts'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            // PUSH RAW ARTIFACTS - JAR/WAR/EAR to Artifactory/JFrog/Nexus
                            echo("PUSHED RAW ARTIFACTS - JAR/WAR/EAR to Artifactory/JFrog/Nexus")
                        }
                    }
                }
                stage('Push Image'){
                    steps{
                        script{
                            FAILED_STAGE = env.STAGE_NAME
                            echo("Establising a session with Image Repositry")
                            withCredentials([usernamePassword(credentialsId: 'DOCKER-CRED', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                                sh """
                                    echo "${DOCKER_PASS}" | docker login -u "${DOCKER_USER}" --password-stdin
                                    ##helm registry login registry-1.docker.io -u "${DOCKER_USER}" -p "${DOCKER_PASS}"
                                """
                            }
                            def image_push = sh(
                                script: """
                                    docker push ${env.REPO}:${env.IMAGE_TAG}
                                """,
                                returnStatus: true
                            )
                            errorLogging(
                                statusCode: image_push,
                                stageName: "${env.STAGE_NAME}",
                                operation: "Pushing Image to Repo"
                            )
                        }
                    }
                }
            }
        }
        stage('Sign Image and SBOM'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    def signing = sh(
                        script: """
                            COSIGN_PASSWORD="" cosign generate-key-pair
                            COSIGN_PASSWORD="" cosign sign --key cosign.key ${env.REPO}:${env.IMAGE_TAG}
                            cosign attach sbom \
                            --sbom image-sbom-spdx.json \
                            ${env.REPO}:${env.IMAGE_TAG}
                        """,
                        returnStatus: true
                    )
                    errorLogging(
                        statusCode: signing,
                        stageName: "${env.STAGE_NAME}",
                        operation: "Image signing and SBOM attachment to Repo"
                    )
                }
            }
        }
        stage('Deployment'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    def validation = sh (
                        script: """
                            cosign verify --key cosign.pub ${env.REPO}:${env.IMAGE_TAG}
                        """,
                        returnStatus: true
                    )
                    if (validation == 0){
                        def deployment = sh(
                            script: """
                                cd ${env.WORKSPACE} && \
                                kubectl apply -f K8s/app.yaml
                            """,
                            returnStatus: true
                        )
                        errorLogging(
                            statusCode: deployment,
                            stageName: "${env.STAGE_NAME}",
                            operation: "Deployment to QA post Image signing verification stage"
                        )
                        sleep(time: '1', unit: 'MINUTES')
                    } else {
                        error("Image is not SIGNED! hence stopping the deployment")
                    }
                }
            }
        }
        stage('Health Check'){
            steps{
                script{
                    FAILED_STAGE = env.STAGE_NAME
                    def url_fetcher = sh (
                        script: """
                            minikube service devsecops-svc \
                            -n ${env.NAMESPACE} \
                            --url
                        """,
                        returnStdout: true
                    ).trim()
                    def health_check = sh(
                        script: """
                            curl -s -o /dev/null -w "%{http_code}" ${url_fetcher}/health
                        """,
                        returnStdout: true
                    )
                    if (health_check == "200"){
                        echo("Service is healthy")
                    } else {
                        echo("Initiated rollback as service is not avaiabile ")
                        def rollback = sh (
                            script:"""
                                kubectl rollout undo deployment -n ${env.NAMESPACE}
                            """,
                            returnStatus: true
                        )
                        errorLogging(
                            statusCode: rollback,
                            stageName: "${env.STAGE_NAME}",
                            operation: "Deployment Rollback"
                        )
                    }
                }
            }
        }
    }
    post {
        success {

            archiveArtifacts(
                artifacts: '*.html,*.json',
                fingerprint: true
            )

            mailNotification(
                buildStatus: 'SUCCESS',
                recipient: '',
                failedStage: FAILED_STAGE,
                attachmentsPattern: '*.html,*.json'
            )

            cleanWs()
        }

        failure {

            archiveArtifacts(
                artifacts: '*.html,*.json',
                fingerprint: true
            )

            mailNotification(
                buildStatus: 'FAILURE',
                recipient: '',
                failedStage: FAILED_STAGE,
                attachmentsPattern: '*.html,*.json'
            )

            cleanWs()
        }
    }
}
