
def fileSystemScan(Map config = [:]){
    def workspace = config.workspace ?: env.WORKSPACE
    def tpl_path = config.tplPath ?: env.TRIVY_TPL_PATH
    def report_name = config.reportName

    return sh (
        script: """
            cd ${workspace} && \
                trivy fs \
                --scanners vuln,secret,misconfig,license \
                --severity HIGH,CRITICAL \
                --format template \
                --template "@${tpl_path}/html.tpl" \
                -o ${report_name} \
                .
        """,
        returnStatus: true
    )
}

def imageScan(Map config = [:]){
    def repo = config.repo ?: env.REPO
    def tag = config.tag ?: env.IMAGE_TAG
    def reportName = config.reportName  //trivy-image-report.html
    def tpl_path = config.tplPath ?: env.TRIVY_TPL_PATH

    return sh(
        script: """
            trivy image \
            --scanners vuln,secret,license,misconfig \
            --severity HIGH,CRITICAL \
            --format template \
            --template "@${tpl_path}/html.tpl" \
            --output ${reportName} \
            ${repo}:${tag}
        """,
        returnStatus: true
    )
}

return this
