# AA Android AI — Car Repair Application

An AI-powered chatbot and calculator system for the car repair industry, built for Android using Clean Architecture and Jetpack Compose.

## Architecture

```
AA-car-repair-Android-AI/
├── app/                        # Application entry point, navigation, UI components
├── core/                       # Core utilities, network, security, privacy
├── contracts/                  # API request/response contracts (DTOs)
├── domain/                     # Business logic: models, repositories interfaces, use cases
├── data/                       # Repository implementations, Room DB, Retrofit
├── analytics/                  # Chat analytics, topic discovery, predictive insights
├── sync/                       # WorkManager background sync
└── feature/
    ├── chat/                   # AI Chat with agent orchestration
    ├── estimator/              # 4-step repair estimate flow
    ├── dtc/                    # DTC code lookup and analysis
    ├── calculators/            # 14 automotive calculators
    ├── fleet/                  # Fleet management dashboard
    ├── voice/                  # Voice assistant with TTS
    └── inspection/             # Visual inspection with CameraX + ML Kit
```

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Minimum device: Android 8.0 (API 26)

## Setup

1. Clone the repository
2. Copy `local.properties.template` to `local.properties`
3. Fill in your API keys in `local.properties`
4. Open in Android Studio and sync Gradle
5. Run on a device or emulator (API 26+)

## Build

```bash
# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease

# Run all tests
./gradlew test

# Run lint
./gradlew lintDebug
```

## Environment Variables (local.properties)

```properties
AA_API_BASE_URL=https://api.aa-carrepair.com/
AA_API_KEY=your_api_key_here
MAPS_API_KEY=your_maps_api_key_here
```

## Key Features

- **AI Chat**: Multi-agent system with specialized agents for diagnosis, estimation, and safety
- **Repair Estimator**: 4-step flow with VIN decode, service category, AI diagnostic, and itemized estimate
- **DTC Lookup**: Full-text search with probability-based cause analysis
- **14 Calculators**: Labor, markup, repair/replace, fleet cost, battery health, and more
- **Fleet Management**: Vehicle roster, cost tracking, and maintenance scheduling
- **Voice Assistant**: TTS/STT with offline fallback
- **Visual Inspection**: Camera-based damage detection with ML Kit

## Target Business Metrics

- 75% reduction in information search time
- 59% estimate completion rate
- 40% increase in qualified leads
- $20,000+ monthly revenue lift per medium-sized shop

## Security

- All data encrypted at rest with SQLCipher + Tink AES-GCM
- VIN and PII encrypted before storage
- Certificate pinning for API endpoints
- JWT Bearer token authentication
- ProGuard obfuscation in release builds

## Privacy

- GDPR/CCPA compliant: privacy mode, data export, data deletion
- PII redaction from logs via TelemetryRedactor
- No VIN or personal data in analytics or error logs

## License

Copyright © 2024 AA Car Repair. All rights reserved.
