def buildArtifacts(Map config = [:]) {

    def workspace = config.workspace ?: env.WORKSPACE
    def serviceSourcePath = config.serviceSourcePath ?: env.SERVICE_NAME
    return sh(
        script: """
            cd ${workspace}/${serviceSourcePath} && \\
            mvn -B clean install -DskipTests
        """,
        returnStatus: true
    )
}

def unitTesting(Map config = [:]) {

    def workspace = config.workspace ?: env.WORKSPACE
    def serviceSourcePath = config.serviceSourcePath ?: env.SERVICE_NAME
    return sh(
        script: """
            cd ${workspace}/${serviceSourcePath} && \\
            mvn test
        """,
        returnStatus: true
    )
}

def buildImage(Map config = [:]){
    def workspace = config.workspace ?: env.WORKSPACE
    def repo = config.repo ?: env.REPO

    def tag = sh (
        script: """
            git rev-parse --short HEAD
        """,
        returnStdout: true
    ).trim()

    env.IMAGE_TAG = tag

    return sh(
        script: """
            cd ${workspace} && \
            docker build -t ${repo}:${tag} -f Dockerfile/Dockerfile --target runtime .
        """,
        returnStatus: true
    )

}

return this