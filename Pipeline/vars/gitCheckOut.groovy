def call(Map config = [:]) {

    def url           = config.url
    def branch        = config.branch
    def credentialsId = config.credentialsId
    if (!url || !branch || !credentialsId) {
        error("""
        Missing required checkout configuration.

        url           : ${url}
        branch        : ${branch}
        credentialsId : ${credentialsId}
        """)
    }

    try {
        echo("""
        ========================================
        GIT CHECKOUT
        ========================================

        Repository : ${url}
        Branch     : ${branch}
        Workspace  : ${env.WORKSPACE}

        ========================================
        """)
        checkout scmGit(
            branches: [[name: branch]],
            extensions: [],
            userRemoteConfigs: [[
                credentialsId: credentialsId,
                url: url
            ]]
        )
        echo("Checkout completed successfully")

    } catch (e) {
        //env.FAILED_STAGE = env.STAGE_NAME
        error("""
        Git checkout failed.

        Repository : ${url}
        Branch     : ${branch}

        Error:
        ${e.getMessage()}
        """)
    }
}