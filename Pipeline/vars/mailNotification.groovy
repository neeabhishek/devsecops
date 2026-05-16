def call(Map config = [:]) {

    def color = config.buildStatus == 'SUCCESS' ? '#28a745' : '#dc3545'
    def attachments = config.attachmentsPattern ?: '*.html,*.json'
    emailext(
        subject: "${config.buildStatus}: DevSecOps Pipeline - Build (${env.BUILD_NUMBER})",
        mimeType: "text/html",
        to: config.recipient,
        attachmentsPattern: attachments,
        body: """
        <html>

        <body>

        <div style="font-family: Arial;">

        <h2 style="color:${color};">
        DevSecOps Pipeline - ${config.buildStatus}
        </h2>

        <table border="1" cellpadding="8" cellspacing="0">

            <tr>
                <td><b>Build Number</b></td>
                <td>${env.BUILD_NUMBER}</td>
            </tr>

            <tr>
                <td><b>Repository</b></td>
                <td>${env.REPO}</td>
            </tr>

            <tr>
                <td><b>PR Number</b></td>
                <td>${env.PR_NUMBER}</td>
            </tr>

            <tr>
                <td><b>Image</b></td>
                <td>${env.REPO}:${env.IMAGE_TAG}</td>
            </tr>

            <tr>
                <td><b>Failed Stage</b></td>
                <td>${env.FAILED_STAGE ?: 'N/A'}</td>
            </tr>

            <tr>
                <td><b>Build URL</b></td>
                <td>
                    <a href="${env.BUILD_URL}">
                        Open Jenkins Build
                    </a>
                </td>
            </tr>

        </table>

        <br>

        <h3>Generated Reports</h3>

        <ul>
            <li>Filesystem Scan Report</li>
            <li>Runtime Image Scan Report</li>
            <li>Source SBOM</li>
            <li>Image SBOM</li>
        </ul>

        <br>

        <p>
        Reports are attached with this email.
        </p>

        </div>

        </body>

        </html>
        """
    )
}