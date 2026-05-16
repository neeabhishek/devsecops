def call(Map config = [:]) {

    def status    = config.statusCode
    def stage     = config.stageName ?: env.STAGE_NAME
    def operation = config.operation ?: "Unknown Operation"

    if (status == null) {
        error("""
        =======
        FAILURE
        =======

        Stage      : ${stage}
        Operation  : ${operation}

        statusCode was not provided.
        Ensure returnStatus: true is used.
        """)
    }

    if (status == 0) {
        echo("""
        =======
        SUCCESS
        =======

        Stage      : ${stage}
        Operation  : ${operation}
        """)

    } else {
        error("""
        =======
        FAILURE
        =======


        Stage      : ${stage}
        Operation  : ${operation}
        Exit Code  : ${status}
        """)
    }
}