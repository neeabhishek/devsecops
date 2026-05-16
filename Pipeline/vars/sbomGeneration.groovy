def sbomFileSystem(Map config = [:]){
    def workspace = config.workspace ?: env.WORKSPACE
    def outputFileName = config.outputFileName 

    return sh (
        script: """
            cd ${workspace} && \
            trivy fs . \
            --scanners license,vuln \
            --format spdx-json \
            --output ${outputFileName}
        """,
        returnStatus: true
    )
}

def sbomImage(Map config = [:]){
    def repo = config.repo ?: env.REPO
    def tag = config.tag ?: env.IMAGE_TAG
    def outPutFileName = config.outPutFileName //

    return sh(
        script:"""
            trivy image ${repo}:${tag} \
            --format spdx-json \
            --output ${outPutFileName}
        """,
        returnStatus: true
    )
}

return this