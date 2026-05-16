def call(Map config = [:]){

    def workspace = config.workspace ?: env.WORKSPACE
    def serviceName = config.serviceName ?: env.SERVICE_NAME
    def sonarQubeCred = config.sonarQubeCred

    if(!sonarQubeCred){
        error("SonarQube Cred were not provided")
    }

    withSonarQubeEnv('SonarQubeInstance'){
        def sast = sh (
        script: """
            cd ${workspace}/${serviceName} && \
            mvn sonar:sonar \
            -Dsonar.projectName=${serviceName}
        """,
        returnStatus: true
        )
        return sast
    }    
}
