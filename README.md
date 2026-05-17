<div align="center">

<br/>

```
██████╗ ███████╗██╗   ██╗███████╗███████╗ ██████╗ ██████╗ ██████╗ ███████╗
██╔══██╗██╔════╝██║   ██║██╔════╝██╔════╝██╔════╝██╔═══██╗██╔══██╗██╔════╝
██║  ██║█████╗  ██║   ██║███████╗█████╗  ██║     ██║   ██║██████╔╝███████╗
██║  ██║██╔══╝  ╚██╗ ██╔╝╚════██║██╔══╝  ██║     ██║   ██║██╔═══╝ ╚════██║
██████╔╝███████╗ ╚████╔╝ ███████║███████╗╚██████╗╚██████╔╝██║     ███████║
╚═════╝ ╚══════╝  ╚═══╝  ╚══════╝╚══════╝ ╚═════╝ ╚═════╝ ╚═╝     ╚══════╝
```

### **Enterprise-Grade Security-First CI/CD Pipeline**
#### Shift-Left · SBOM · SAST · FOSS Scanning · Shared Libraries · Audit Compliance

<br/>

[![Pipeline Status](https://img.shields.io/badge/Pipeline-Passing-brightgreen?style=for-the-badge&logo=jenkins&logoColor=white)](https://github.com/neeabhishek/devsecops)
[![SonarQube](https://img.shields.io/badge/SonarQube-Quality%20Gate%20Passed-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)](https://github.com/neeabhishek/devsecops)
[![SBOM](https://img.shields.io/badge/SBOM-CycloneDX%20v1.6-orange?style=for-the-badge&logo=dependabot&logoColor=white)](https://github.com/neeabhishek/devsecops)
[![Trivy](https://img.shields.io/badge/Trivy-FOSS%20%2B%20Container%20Scan-1904DA?style=for-the-badge&logo=aquasecurity&logoColor=white)](https://github.com/neeabhishek/devsecops)
[![Supply Chain](https://img.shields.io/badge/Supply%20Chain-SBOM%20Signed-critical?style=for-the-badge&logo=dependabot&logoColor=white)](https://github.com/neeabhishek/devsecops)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

<br/>

</div>

---

## What This Repository Solves

Most CI/CD pipelines treat security as an afterthought — a final gate before deployment that slows teams down and delivers feedback too late. This repository demonstrates a fundamentally different approach: **security woven into every stage of the software delivery lifecycle**, enforced automatically, and built for scale.

This is not a demo pipeline. This is a production-pattern reference implementation that a security-conscious engineering team would actually run.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        DEVSECOPS PIPELINE ARCHITECTURE                       │
└─────────────────────────────────────────────────────────────────────────────┘

  PR / MR Detected on Main Branch
          │
          ▼
  ┌────────────────────────────────────────────────────────────────────────┐
  │               PLATFORM API — WEBHOOK TRIGGER                            │
  │                                                                          │
  │  GitHub : pull_request event  →  Checks API (check-runs)                │
  │           POST /repos/{owner}/{repo}/statuses  (commit status)          │
  │           POST /repos/{owner}/{repo}/pulls/{n}/reviews  (gate result)   │
  │                                                                          │
  │  GitLab : merge_request webhook  →  Pipeline trigger token              │
  │           PUT  /projects/{id}/merge_requests/{iid}  (MR state)         │
  │           POST /projects/{id}/statuses  (commit status API)             │
  │           POST /projects/{id}/merge_requests/{iid}/notes  (comments)   │
  │                                                                          │
  │  Status checks posted back to PR/MR in real time per stage              │
  │  Branch protection rules block merge until all checks pass              │
  └────────┬───────────────────────────────────────────────────────────────┘
           │
           ▼
  ┌─────────────────────────────────────────────────────────────────────┐
  │                     SHARED LIBRARY INVOCATION                        │
  │         Reusable Groovy / YAML modules called per pipeline stage     │
  └──────────────────────────┬──────────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
  ┌──────────┐       ┌──────────────┐      ┌──────────────┐
  │  Checkout │       │  Credential  │      │  Environment │
  │  & Clone  │       │  Validation  │      │  Config Prep │
  └─────┬─────┘       └──────┬───────┘      └──────┬───────┘
        └────────────────────┴────────────────────┘
                             │
                             ▼
  ┌──────────────────────────────────────────────────────────────────────┐
  │                     STAGE 1 — SHIFT-LEFT SCANNING                    │
  │                                                                        │
  │   Secret Detection (Gitleaks / TruffleHog)                            │
  │   Pre-commit hook enforcement                                          │
  │   IaC scanning (Checkov / tfsec) — Terraform, Helm, Dockerfile        │
  │   Hardcoded credential checks                                          │
  └──────────────────────────────┬───────────────────────────────────────┘
                                 │
                                 ▼
  ┌──────────────────────────────────────────────────────────────────────┐
  │              STAGE 2 — SAST + QUALITY GATE (SonarQube)               │
  │                                                                        │
  │   Static Application Security Testing via SonarScanner                │
  │   Code coverage thresholds enforced                                    │
  │   Cognitive complexity, duplication, maintainability ratings           │
  │   Quality Gate → PASS required to proceed · FAIL = pipeline abort     │
  └──────────────────────────────┬───────────────────────────────────────┘
                                 │
                                 ▼
  ┌──────────────────────────────────────────────────────────────────────┐
  │             STAGE 3 — FOSS / SCA SCANNING (Trivy)                   │
  │                                                                        │
  │   trivy fs . — filesystem scan of all language dependencies            │
  │   CVE cross-reference: NVD · OSV · GitHub Advisory DB                 │
  │   License policy validation (allowlist / denylist enforced)            │
  │   CVSS threshold: HIGH / CRITICAL findings → pipeline abort           │
  │   Output: JSON + SARIF reports archived as build artifacts            │
  └──────────────────────────────┬───────────────────────────────────────┘
                                 │
                                 ▼
  ┌──────────────────────────────────────────────────────────────────────┐
  │        STAGE 4 — SBOM GENERATION + SUPPLY CHAIN MANAGEMENT           │
  │                                                                        │
  │   Syft generates component inventory — every package, every layer     │
  │   Output: CycloneDX JSON (v1.6) + SPDX JSON (2.3) — dual format      │
  │   Artifact signed (cosign / sigstore) + SHA256 digest recorded        │
  │   SBOM attached to build record — versioned, immutable, archived      │
  │   Grype vulnerability scan run against generated SBOM                 │
  │   Enables: audit readiness · CVE retro-scanning · license governance  │
  └──────────────────────────────┬───────────────────────────────────────┘
                                 │
                                 ▼
  ┌──────────────────────────────────────────────────────────────────────┐
  │                  STAGE 5 — CONTAINER SECURITY SCAN                   │
  │                                                                        │
  │   Trivy image scan — OS packages + app dependencies                   │
  │   Dockerfile best-practice linting (Hadolint)                         │
  │   Base image vulnerability report                                      │
  │   HIGH / CRITICAL findings → build failure policy                     │
  └──────────────────────────────┬───────────────────────────────────────┘
                                 │
                                 ▼
  ┌──────────────────────────────────────────────────────────────────────┐
  │               STAGE 6 — AUDIT & COMPLIANCE REPORTING                 │
  │                                                                        │
  │   Immutable pipeline execution log (timestamped per stage)            │
  │   Consolidated compliance report: HTML + JSON + SARIF                 │
  │   Every security artifact archived with build ID + Git SHA            │
  │   SBOM + scan reports satisfy: SOC2 · PCI-DSS · ISO 27001 · EO 14028 │
  │   Grype retro-scan on archived SBOM — catch new CVEs post-ship        │
  │   Notification: Slack / email with per-gate pass/fail summary         │
  │   PR/MR status updated via platform API — no manual reporting         │
  └──────────────────────────────┬───────────────────────────────────────┘
                                 │
                                 ▼
                     ✅  Merge Approved / 🚫 Blocked
```

---

## Core Security Capabilities

### Shift-Left Security — Catch Defects at the Source

Security scanning begins before a single line reaches the build server. The pipeline enforces checks at the earliest possible stage — eliminating the cost and risk of finding vulnerabilities late in the SDLC.

| Control | Tool | Enforcement Point |
|---|---|---|
| Secret / credential leak detection | Gitleaks, TruffleHog | Pre-commit + CI trigger |
| Infrastructure-as-Code misconfig | Checkov, tfsec | Source scan stage |
| Dockerfile hardening | Hadolint | Build preparation stage |
| Branch protection | GitHub / GitLab API | PR/MR event level |

---

### SAST + SonarQube Quality Gate

Every pull request triggers a full static analysis pass via SonarQube. The pipeline enforces a **hard Quality Gate**: pipelines do not advance unless the project meets defined thresholds for security rating, reliability, maintainability, and test coverage.

```groovy
// Shared Library invocation — reusable across all repos
sonarAnalysis(
  projectKey: env.PROJECT_KEY,
  qualityGateWaitTimeout: 300,
  abortOnFailure: true
)
```

Quality Gate conditions enforced:

- Security Rating: **A** (zero open vulnerabilities)
- Coverage on new code: **≥ 80%**
- Duplicated lines on new code: **< 3%**
- Reliability Rating: **A**

---

### FOSS Scanning — Open Source Risk Management via Trivy

Every third-party dependency is treated as untrusted until validated. Trivy performs a full filesystem scan against the project source tree — covering language-specific packages (pip, npm, Maven, Go modules, RubyGems) and cross-referencing every component against multiple advisory databases simultaneously.

```bash
# Trivy filesystem scan — run as shared library step
trivy fs . \
  --format json \
  --output trivy-foss-report.json \
  --severity HIGH,CRITICAL \
  --exit-code 1 \
  --ignore-unfixed \
  --scanners vuln,license

# SARIF output for SonarQube / GitHub Code Scanning ingestion
trivy fs . \
  --format sarif \
  --output trivy-foss-results.sarif
```

**Advisory databases consulted per scan:**

| Database | Scope |
|---|---|
| NVD (National Vulnerability Database) | CVE cross-reference, CVSS scores |
| OSV (Open Source Vulnerabilities) | Language ecosystem advisories |
| GitHub Advisory Database | GitHub-native security advisories |
| Trivy built-in DB | OS packages + language libs |

**Policy enforcement:**

- Severity threshold: `HIGH` and `CRITICAL` findings abort the pipeline
- `--ignore-unfixed` flag ensures only actionable, patched CVEs block builds
- License policy: GPL, AGPL, SSPL flagged and validated against project allowlist
- SARIF report ingested by SonarQube for unified findings view and GitHub Code Scanning

---

### SBOM Generation — Software Supply Chain Management

A Software Bill of Materials is generated for every build as a first-class pipeline output — not an optional add-on. The SBOM is a machine-readable, cryptographically signed, versioned inventory of every component, dependency, transitive library, and OS package that makes up the shipped artifact.

```bash
# Syft — full component inventory, dual SBOM format
syft packages dir:. \
  -o cyclonedx-json=sbom-cyclonedx.json \
  -o spdx-json=sbom-spdx.json \
  --name "${PROJECT_NAME}" \
  --version "${BUILD_VERSION}"

# Sign the SBOM with cosign (keyless, Sigstore-backed)
cosign attest --predicate sbom-cyclonedx.json \
  --type cyclonedx "${IMAGE_REF}"

# Grype — vulnerability scan run directly against the generated SBOM
grype sbom:sbom-cyclonedx.json \
  --fail-on high \
  -o json > grype-sbom-report.json
```

**What the SBOM enables in this pipeline:**

| Capability | Mechanism |
|---|---|
| **Instant CVE response** | Archived SBOMs are re-scanned with Grype when new CVEs drop — affected builds identified without rebuilding |
| **License governance** | Complete license inventory for every transitive dependency — GPL/AGPL/SSPL flagged automatically |
| **Regulatory compliance** | CycloneDX v1.6 satisfies US EO 14028, EU Cyber Resilience Act, NIST SSDF SP 800-218 |
| **Supplier risk visibility** | Full transitive dependency tree exposed — no hidden third-party components |
| **Immutable audit evidence** | SBOM signed with Git SHA + build ID, archived permanently alongside the artifact |

**SBOM metadata recorded per build:**

```json
{
  "bomFormat": "CycloneDX",
  "specVersion": "1.6",
  "metadata": {
    "timestamp": "2025-04-01T10:00:00Z",
    "component": { "name": "my-service", "version": "1.4.2" },
    "properties": [
      { "name": "git:commit",        "value": "a3f9d21" },
      { "name": "pipeline:buildId",  "value": "jenkins-build-482" },
      { "name": "pipeline:trigger",  "value": "PR#114 → main" }
    ]
  }
}
```

---

### PR/MR Pipeline Trigger — Native GitHub & GitLab API Integration

Pipelines are not manually triggered. They respond to native platform events. The integration uses native webhook payloads and API callbacks — not polling, not a third-party bridge.

**GitHub — Pull Request Event Flow**

```yaml
# .github/workflows/devsecops-pipeline.yml
on:
  pull_request:
    branches: [main, 'release/**']
    types: [opened, synchronize, reopened]

jobs:
  security-pipeline:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      pull-requests: write    # Post review comments
      checks: write           # Create check runs
      security-events: write  # Upload SARIF to Code Scanning
```

```bash
# Post commit status back to the PR — updated per pipeline stage
curl -X POST \
  -H "Authorization: Bearer ${GITHUB_TOKEN}" \
  -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/${OWNER}/${REPO}/statuses/${SHA} \
  -d '{
    "state": "success",
    "target_url": "'"${BUILD_URL}"'",
    "description": "SBOM generated · Trivy FOSS clean · SonarQube gate passed",
    "context": "devsecops/security-pipeline"
  }'

# Create a PR review comment with the security summary
curl -X POST \
  -H "Authorization: Bearer ${GITHUB_TOKEN}" \
  https://api.github.com/repos/${OWNER}/${REPO}/pulls/${PR_NUMBER}/reviews \
  -d '{
    "event": "COMMENT",
    "body": "### DevSecOps Pipeline Report\n| Stage | Status |\n|---|---|\n| Shift-Left | Passed |\n| SAST / SonarQube | Gate Passed |\n| FOSS (Trivy) | 0 HIGH/CRITICAL |\n| SBOM | Generated + Signed |\n| Container Scan | Clean |"
  }'
```

**GitLab — Merge Request Event Flow**

```yaml
# .gitlab-ci.yml — trigger on MR to protected branch
workflow:
  rules:
    - if: '$CI_PIPELINE_SOURCE == "merge_request_event"
           && $CI_MERGE_REQUEST_TARGET_BRANCH_NAME == "main"'

variables:
  GITLAB_API: "${CI_API_V4_URL}/projects/${CI_PROJECT_ID}"
```

```bash
# Post pipeline result as commit status to the MR
curl -X POST \
  -H "PRIVATE-TOKEN: ${GITLAB_TOKEN}" \
  "${GITLAB_API}/statuses/${CI_COMMIT_SHA}" \
  -d "state=success&name=devsecops/security-pipeline&target_url=${CI_PIPELINE_URL}"

# Add a MR note (comment) with scan summary
curl -X POST \
  -H "PRIVATE-TOKEN: ${GITLAB_TOKEN}" \
  "${GITLAB_API}/merge_requests/${CI_MERGE_REQUEST_IID}/notes" \
  -d "body=**DevSecOps Gate Result**: FOSS clean · SBOM signed · SonarQube passed"

# Block merge via MR approval API if a gate fails
curl -X POST \
  -H "PRIVATE-TOKEN: ${GITLAB_TOKEN}" \
  "${GITLAB_API}/merge_requests/${CI_MERGE_REQUEST_IID}/unapprove"
```

**What this means in practice:** A developer opening a PR sees a live, stage-by-stage security status in the PR diff view. The pipeline posts its own pass/fail — no human needs to check a separate Jenkins dashboard or Slack message. A failing gate is a blocked merge, enforced by the platform itself.

---

### Shared Library — Reusable Pipeline Modules

The entire security pipeline is packaged as a **Jenkins Shared Library** (or reusable GitHub Actions composite actions), enabling any team to adopt enterprise-grade DevSecOps with a single `@Library` import. No copy-paste pipelines.

```
shared-library/
├── vars/
│   ├── shiftLeftScan.groovy          # Secret + IaC scanning (Gitleaks, Checkov)
│   ├── sonarAnalysis.groovy          # SAST + SonarQube Quality Gate
│   ├── fossAudit.groovy              # Trivy FS scan — deps + license policy
│   ├── sbomGenerate.groovy           # Syft SBOM generation (CycloneDX + SPDX)
│   ├── sbomSign.groovy               # cosign keyless SBOM attestation
│   ├── sbomVulnScan.groovy           # Grype scan against SBOM artifact
│   ├── containerScan.groovy          # Trivy image scan
│   ├── complianceReport.groovy       # Audit artifact consolidation
│   ├── postGitHubStatus.groovy       # GitHub Checks API / Statuses API
│   └── postGitLabStatus.groovy       # GitLab Commit Status + MR Notes API
├── src/
│   └── com/devsecops/
│       ├── SecurityGate.groovy
│       ├── SonarQubeClient.groovy
│       ├── PlatformApiClient.groovy  # Unified GitHub + GitLab API wrapper
│       └── NotificationService.groovy
└── resources/
    ├── templates/audit-report.html
    └── policies/
        ├── severity-threshold.json
        └── license-allowlist.txt
```

**Fork-to-Feature validation:** The shared library is structured so that forked repositories or feature branches can invoke the same security modules — ensuring security parity between trunk development and experimental branches. A fork that skips the Quality Gate is not a mergeable fork.

---

### Audit Compliance — Automated Evidence Collection

Security compliance requires evidence, not just tooling. This pipeline generates structured, timestamped, build-linked artifacts at every stage — so when an audit happens, the evidence already exists.

**Artifacts produced per build:**

| Artifact | Format | Linked To | Standard |
|---|---|---|---|
| SBOM | CycloneDX JSON + SPDX JSON | Git SHA + Build ID | EO 14028, NIST SSDF |
| SBOM signature | Sigstore / cosign bundle | Image digest | Supply chain integrity |
| FOSS scan report | JSON + SARIF (Trivy) | PR number | PCI-DSS 6.3.2 |
| SAST findings | SARIF (SonarQube export) | Commit SHA | SOC 2 Type II |
| Container scan | JSON (Trivy image) | Image tag + digest | ISO 27001 A.12.6 |
| IaC scan report | JSON (Checkov) | Terraform plan hash | CIS Benchmarks |
| Pipeline execution log | Immutable timestamped log | Jenkins build / GitLab job ID | General compliance |
| Quality Gate result | SonarQube API JSON snapshot | Project + analysis ID | Internal SLA |

**Grype retro-scanning — compliance that works after shipment:**

```bash
# Re-scan a previously archived SBOM when a new CVE is published
# No rebuild required — the SBOM already exists
grype sbom:./archive/sbom-cyclonedx-build-482.json \
  --fail-on critical \
  -o table
```

This means an organization can answer "are any of our shipped builds affected by CVE-2025-XXXX?" in seconds — by scanning archived SBOMs rather than rebuilding every artifact.

**Compliance framework coverage:**

| Framework | Controls Met by This Pipeline |
|---|---|
| **NIST SSDF SP 800-218** | PW.4 (reusable secure components), PW.7 (SAST), PW.8 (vulnerability disclosure), RV.1 (vuln identification) |
| **US Executive Order 14028** | SBOM mandate (§4(e)), supply chain risk management, artifact signing |
| **EU Cyber Resilience Act** | SBOM for CE-marked products, vulnerability handling process, security documentation |
| **OWASP Top 10** | A03 Injection (SAST), A06 Vulnerable Components (Trivy + Grype), A09 Logging |
| **PCI-DSS v4.0** | 6.3.2 (software inventory / SBOM), 6.2.4 (SAST), 6.3.1 (dependency scanning) |
| **SOC 2 Type II** | CC7.1 (change management), CC8.1 (system monitoring), availability controls |
| **ISO 27001:2022** | A.8.8 (vulnerability management), A.8.25 (secure development), A.8.29 (security testing) |

---

## Technology Stack

| Category | Tools |
|---|---|
| **CI/CD Orchestration** | Jenkins (Shared Library), GitHub Actions, GitLab CI |
| **SAST** | SonarQube, SonarScanner |
| **FOSS / SCA** | Trivy (`trivy fs`) — NVD, OSV, GitHub Advisory DB |
| **Container Security** | Trivy (`trivy image`), Hadolint |
| **SBOM Generation** | Syft (CycloneDX v1.6 + SPDX 2.3) |
| **SBOM Vulnerability Scan** | Grype |
| **SBOM Signing** | cosign (Sigstore keyless) |
| **Secret Detection** | Gitleaks, TruffleHog |
| **IaC Security** | Checkov, tfsec |
| **Platform API** | GitHub Checks API, GitHub Statuses API, GitHub Reviews API |
| | GitLab Merge Request API, GitLab Commit Status API, GitLab Notes API |
| **Notifications** | Slack API, SMTP |
| **Reporting** | SARIF, CycloneDX JSON, SPDX JSON, HTML/JSON audit templates |

---

## Repository Structure

```
devsecops/
├── .github/
│   └── workflows/
│       ├── devsecops-pipeline.yml      # GitHub Actions full security pipeline
│       └── pr-security-check.yml       # PR-triggered lightweight gate
├── jenkins/
│   ├── Jenkinsfile                     # Declarative pipeline (shared lib)
│   └── shared-library/                 # Reusable Groovy security modules
├── sonar/
│   └── sonar-project.properties        # SonarQube project config
├── security/
│   ├── trivy/
│   │   ├── trivy-foss-config.yaml      # FS scan policy (severity, licenses)
│   │   └── trivy-image-config.yaml     # Container scan policy
│   ├── sbom/
│   │   ├── sbom-cyclonedx.json         # Generated CycloneDX SBOM
│   │   ├── sbom-spdx.json              # Generated SPDX SBOM
│   │   └── sbom.sig                    # cosign signature bundle
│   ├── grype/
│   │   └── grype-sbom-report.json      # Vulnerability scan against SBOM
│   └── policies/
│       ├── severity-threshold.json
│       └── license-allowlist.txt
├── compliance/
│   ├── audit-trail/                    # Immutable per-build logs
│   └── reports/                        # Consolidated compliance reports (HTML/JSON/SARIF)
├── docs/
│   ├── architecture.md
│   ├── shift-left-strategy.md
│   ├── sbom-supply-chain-guide.md
│   └── github-gitlab-api-integration.md
└── README.md
```

---

## Getting Started

### Prerequisites

- Jenkins 2.375+ with Shared Library plugin, **or** GitHub Actions / GitLab CI access
- SonarQube server (Community Edition or above)
- Docker (for container scanning stages)
- Trivy 0.50+ (`brew install trivy` / `apt install trivy`)
- Syft 0.100+ and cosign (for SBOM generation and signing)
- Grype 0.74+ (for SBOM vulnerability scanning)

### Run the Full Pipeline

```bash
# 1. Clone the repository
git clone https://github.com/neeabhishek/devsecops.git
cd devsecops

# 2. Configure environment
cp .env.example .env
# Edit .env — set SONAR_HOST_URL, SONAR_TOKEN, SLACK_WEBHOOK_URL

# 3. Register the Shared Library in Jenkins
# Jenkins → Manage Jenkins → Configure System → Global Pipeline Libraries
# Name: devsecops-shared-lib | Default branch: main

# 4. Create a Webhook on your repo
# GitHub: Settings → Webhooks → Add webhook
# Payload URL: https://<your-jenkins>/github-webhook/
# Content type: application/json | Events: Pull requests

# 5. Open a PR targeting main — the pipeline runs automatically
```

### Using the Shared Library in Your Own Repo

```groovy
@Library('devsecops-shared-lib') _

pipeline {
    agent any
    stages {
        stage('Shift-Left Scan')    { steps { shiftLeftScan() } }
        stage('SAST Analysis')      { steps { sonarAnalysis(projectKey: 'my-app') } }
        stage('FOSS Audit')         { steps { fossAudit(severity: 'HIGH,CRITICAL') } }
        stage('SBOM Generate')      { steps { sbomGenerate(format: 'cyclonedx,spdx') } }
        stage('SBOM Sign')          { steps { sbomSign(imageRef: "${IMAGE}:${TAG}") } }
        stage('SBOM Vuln Scan')     { steps { sbomVulnScan(failOn: 'high') } }
        stage('Container Scan')     { steps { containerScan(image: 'my-app:latest') } }
        stage('Compliance Report')  { steps { complianceReport() } }
        stage('Post PR Status')     { steps { postGitHubStatus(context: 'devsecops/gate') } }
    }
}
```

---

## Security Philosophy

> **"Security is not a checkpoint. It is a continuous, automated, and evidenced property of every artifact we ship."**

This pipeline is built on four principles:

**1. Prevention over detection** — Block insecure code from merging, rather than discovering vulnerabilities in production.

**2. Evidence by default** — Every build produces audit artifacts without manual intervention. Compliance readiness is a side effect of shipping software.

**3. Reusability as force multiplication** — One shared library raises the security baseline across an entire engineering organization, not just one repo.

**4. Developer experience matters** — Security gates are fast, informative, and actionable. Developers receive feedback in the PR view, not in a separate tool no one checks.

---

## Compliance Coverage

| Framework | Controls Addressed |
|---|---|
| **NIST SSDF (SP 800-218)** | PW.4 (reusable secure components), PW.7 (SAST), PW.8 (vuln disclosure), RV.1 (vuln identification) |
| **US EO 14028** | SBOM mandate §4(e), artifact signing, supply chain risk management |
| **EU Cyber Resilience Act** | SBOM for CE-marked software, vulnerability handling process |
| **OWASP Top 10** | A03 (Injection via SAST), A06 (Vulnerable Components via Trivy + Grype), A09 (Logging) |
| **PCI-DSS v4.0** | 6.2.4 (SAST), 6.3.1 (dependency scanning), 6.3.2 (software inventory / SBOM) |
| **SOC 2 Type II** | CC7.1 (change management), CC8.1 (system monitoring), availability controls |
| **ISO 27001:2022** | A.8.8 (vulnerability management), A.8.25 (secure development), A.8.29 (security testing) |

---

## Author

**Abhishek** · DevOps / DevSecOps Engineer  
Building pipelines where security is the default, not the exception.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/abhishek)
[![GitHub](https://img.shields.io/badge/GitHub-Follow-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/neeabhishek)

---

<div align="center">

*If this pipeline architecture is relevant to a role you're hiring for — let's talk.*

</div>
