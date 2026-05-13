# Enterprise DevSecOps CI/CD Pipeline for Java Microservice ////

## Objective

Design and implement an enterprise-grade DevSecOps CI/CD pipeline for a Java microservice with integrated security, compliance, software supply-chain validation, automated testing, artifact governance, image signing, and deployment health validation.

---

# Pipeline Flow

```text
Developer Commit/Push
        ↓
Auto Trigger Pipeline
        ↓
Filesystem Security Scan
        ↓
SAST + Quality Gate Validation
        ↓
Unit Testing
        ↓
Filesystem SBOM Generation
        ↓
Build Raw Artifact (JAR/WAR)
        ↓
Container Image Build
        ↓
Container Image Security Scan
        ↓
Container Image SBOM Generation
        ↓
Push Artifact + Image
        ↓
Sign Image + Attach SBOM
        ↓
Verify Signature
        ↓
Deploy Application
        ↓
Health Check Validation
        ↓
Rollback on Failure
