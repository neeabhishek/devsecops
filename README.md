# DevSecOps CI/CD Pipeline with Shift-Left Security

<div align="center">

![DevSecOps](https://img.shields.io/badge/DevSecOps-Secure%20CI%2FCD-blue?style=for-the-badge)
![Jenkins](https://img.shields.io/badge/Jenkins-Pipeline-red?style=for-the-badge&logo=jenkins)
![SonarQube](https://img.shields.io/badge/SonarQube-Quality%20Gate-brightgreen?style=for-the-badge&logo=sonarqube)
![Trivy](https://img.shields.io/badge/Trivy-Vulnerability%20Scanning-orange?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Orchestration-326ce5?style=for-the-badge&logo=kubernetes)

### Secure • Automated • Reusable • Enterprise-Ready

</div>

---

# Overview

This repository demonstrates an enterprise-grade **DevSecOps CI/CD Pipeline** designed with a strong focus on:

- Shift-Left Security
- Secure Software Supply Chain
- CI/CD Automation
- Reusable Jenkins Shared Libraries
- Pull Request / Merge Request Validation
- Continuous Security & Compliance
- Container Security
- Audit & Governance

The project integrates multiple security and quality validation stages directly into the CI/CD workflow to ensure vulnerabilities are identified early before reaching production environments.

---

# Key Highlights

## Shift-Left Security Implementation

Security is integrated from the earliest stages of the SDLC.

### Security Controls Included

- SAST (Static Application Security Testing)
- Secret Scanning
- Filesystem Vulnerability Scanning
- Container Image Scanning
- IaC & Configuration Scanning
- Dependency/FOSS Scanning
- SBOM Generation
- License Compliance Validation
- Quality Gate Enforcement

---

# DevSecOps Pipeline Architecture

```text
Developer Commit / PR
        │
        ▼
GitHub / GitLab Webhook Trigger
        │
        ▼
Jenkins Pipeline Execution
        │
 ┌────────────────────────────┐
 │      Shared Libraries      │
 └────────────────────────────┘
        │
        ▼
Code Checkout & Validation
        │
        ▼
SAST Analysis (SonarQube)
        │
        ▼
Quality Gate Validation
        │
        ▼
Dependency & FOSS Scanning
        │
        ▼
Trivy Filesystem Scan
        │
        ▼
SBOM Generation (SPDX/CycloneDX)
        │
        ▼
Container Build & Scan
        │
        ▼
Policy & Compliance Validation
        │
        ▼
Deployment Approval / Delivery
