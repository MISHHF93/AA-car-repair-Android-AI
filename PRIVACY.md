# Privacy

## Data Collected

| Data Type | Purpose | Retention |
|-----------|---------|-----------|
| Vehicle VIN | Lookup & estimates | Until deleted by user |
| Chat messages | AI conversation history | 90 days or user deletion |
| Repair estimates | History & comparison | Until deleted by user |
| Calculator results | Saved calculations | Until deleted by user |
| Fleet data | Fleet management | Until deleted by user |

## On-Device Encryption

All sensitive data is encrypted before storage:
- VIN numbers: Tink AES-256-GCM
- Database: SQLCipher full-database encryption
- Preferences: EncryptedSharedPreferences

## Telemetry Redaction

The `TelemetryRedactor` strips the following from all logs:
- VIN numbers (17-character patterns)
- Email addresses
- Phone numbers
- License plate numbers

## Privacy Mode

When Privacy Mode is enabled:
- All analytics disabled
- No telemetry sent
- Crash reports anonymized
- Usage data not collected

## GDPR/CCPA Rights

- **Right to Access**: Data export available in Settings
- **Right to Deletion**: "Delete All Data" in Settings → Privacy
- **Right to Portability**: JSON export of all stored data
- **Right to Object**: Privacy Mode disables all data processing beyond core functionality

## Data Minimization

- No GPS tracking
- No microphone recording stored (voice is processed in real-time only)
- Camera images are processed on-device; not stored without explicit user action
