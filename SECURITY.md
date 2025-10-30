# Security Policy

## Supported Versions

The opinion-common library follows semantic versioning. We actively support the
latest minor series with critical fixes and security patches.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :hourglass:        |

## Security Scanning

Every commit and pull request is scanned by the Opinion platform security
tooling.

### 🔍 Automated Security Scans

- **GitHub CodeQL**: Static analysis for code-level vulnerabilities.
- **GitHub Dependabot**: Automated dependency monitoring and updates.
- **Snyk**: Dependency vulnerability scanning for published artifacts.

### 📊 Security Badges

- [![Known Vulnerabilities](https://snyk.io/test/github/inqwise-opinion/opinion-common/badge.svg)](https://snyk.io/test/github/inqwise-opinion/opinion-common)
- CI pipeline security gates run on every change set.

## Reporting a Vulnerability

Email security issues directly to **security@inqwise.com** and include:

1. Description of the vulnerability.
2. Steps to reproduce the issue.
3. Potential impact assessment.
4. Suggested remediation or workaround (optional).

Please do not open public GitHub issues for sensitive findings.

### Response Timeline

- **Acknowledgment**: within 48 hours.
- **Initial Assessment**: within 5 business days.
- **Status Updates**: provided weekly until resolution.

## Security Guidance

When integrating opinion-common:

### ✅ Recommended Practices

1. Keep dependencies updated and monitor release notes.
2. Validate input to APIs that wrap opinion-common utilities.
3. Apply least-privilege access when exposing configuration values.

### ⚠️ Considerations

1. Avoid logging sensitive payloads processed by helper utilities.
2. Enforce request rate limits if utilities back public endpoints.
3. Configure observability to detect misuse or anomalies.

## Dependency Security

Production and test dependencies (Log4j, Vert.x testing tooling, JUnit) are
monitored through the scans listed above.

## Security Contact

- **Email**: security@inqwise.com
- **Response Time**: typically within 48 hours
- **PGP Key**: available on request

---

*Last updated: October 2024*
