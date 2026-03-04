# Security

## OWASP Mobile Top 10 Compliance

### M1: Improper Credential Usage
- API keys stored in BuildConfig, sourced from local.properties (not committed to VCS)
- No credentials hardcoded in source code

### M2: Inadequate Supply Chain Security
- Gradle dependency verification via checksums
- Regular dependency updates via Dependabot

### M3: Insecure Authentication/Authorization
- JWT Bearer tokens for all API requests
- Tokens stored in EncryptedSharedPreferences

### M4: Insufficient Input/Output Validation
- VIN validated with regex before processing
- DTC codes validated before lookup
- All API responses validated via Moshi type adapters

### M5: Insecure Communication
- TLS enforced, cleartext blocked via network_security_config.xml
- Certificate pinning for api.aa-carrepair.com
- Certificate pin expiry: 2027-01-01

### M6: Inadequate Privacy Controls
- Privacy mode: disables all telemetry
- GDPR: data export and deletion APIs
- PII redacted from logs via TelemetryRedactor

### M7: Insufficient Binary Protections
- ProGuard enabled in release builds
- Root detection recommended for production

### M8: Security Misconfiguration
- android:allowBackup="false" in manifest
- Network security config blocks cleartext
- Debug builds have separate applicationId

### M9: Insecure Data Storage
- SQLCipher encrypts entire Room database
- Tink AES-256-GCM for VIN and PII fields
- Keys managed via Android Keystore

### M10: Insufficient Cryptography
- AES-256-GCM (authenticated encryption)
- Keys generated and stored in hardware-backed Android Keystore

## Encryption Strategy

| Data Type | Encryption | Key Storage |
|-----------|-----------|-------------|
| Database  | SQLCipher AES-256 | Android Keystore |
| VIN       | Tink AES-256-GCM | Android Keystore |
| PII fields | Tink AES-256-GCM | Android Keystore |
| Network   | TLS 1.3 | Certificate pins |
| API tokens | EncryptedSharedPreferences | Android Keystore |

## ISO 26262 Functional Safety (Automotive Software)

- Safety classifier (SafetyClassifier.kt) provides CRITICAL/HIGH/MEDIUM/LOW levels
- CRITICAL level prevents drive recommendations and escalates to professional
- All safety decisions are non-binding estimates, never authoritative
- Disclaimer shown on all estimates and DTC analyses
