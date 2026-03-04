# Changelog

## [1.0.0] - 2024-01-01

### Added

#### Core Application
- Multi-module Clean Architecture project setup
- Hilt dependency injection throughout
- Room + SQLCipher encrypted database
- Retrofit + OkHttp networking with JWT auth
- WorkManager background sync
- Timber logging with PII redaction

#### AI Chat
- Multi-agent orchestration system
- ConversationAgent for general automotive questions
- DiagnosisAgent for symptom and DTC analysis
- EstimatorAgent for repair cost guidance
- SafetyAgent with 4-level classification (CRITICAL/HIGH/MEDIUM/LOW)
- Chat history with offline caching

#### Repair Estimator
- 4-step estimation flow
- VIN decode from barcode (CameraX + ML Kit)
- 14 service category selection
- AI-powered diagnostic chat
- Itemized estimate with OEM/aftermarket toggle
- Non-binding disclaimer on all estimates

#### DTC Code Lookup
- Full-text search with Room FTS4
- 5-section accordion detail view
- Probability bars for causes
- Safety level classification
- Related codes and repair history

#### 14 Calculators
- Labor Time Calculator
- Parts Markup Calculator
- Repair vs. Replace Analyzer
- Maintenance Schedule Calculator
- Fleet Cost Analyzer
- Diagnostic Confidence Scorer
- CO₂ Impact Calculator
- Break-Even Analyzer
- Warranty ROI Calculator
- OBD Data Analyzer
- Tire Wear Estimator
- Battery Health Checker
- Coolant Pressure Analyzer
- Shop Efficiency KPI Dashboard

#### Fleet Management
- Fleet vehicle roster
- Cost tracking (YTD)
- Maintenance due alerts
- Dashboard with key metrics

#### Voice Assistant
- Text-to-Speech responses
- Push-to-talk interface
- Offline command processing
- Foreground service with notification

#### Visual Inspection
- CameraX integration
- Gallery image selection
- ML Kit object detection
- Damage assessment mode
- Parts identification mode
- Wear analysis mode

#### Settings
- Privacy Mode (GDPR/CCPA)
- Data export
- Data deletion
- Theme settings

#### Security & Privacy
- SQLCipher database encryption
- Tink AES-256-GCM for VIN/PII
- Certificate pinning
- TelemetryRedactor
- PrivacyManager

#### Analytics
- Session analytics engine
- Conversation summarizer
- TF-IDF topic discovery
- Predictive maintenance insights
