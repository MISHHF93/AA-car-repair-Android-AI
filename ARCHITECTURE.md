# Architecture

## Clean Architecture

The project follows Clean Architecture with these layers:

```
┌─────────────────────────────────────────┐
│  Presentation (feature modules + app)   │
│  ViewModels, Compose UI, Navigation     │
├─────────────────────────────────────────┤
│  Domain (business logic)                │
│  Use Cases, Repository Interfaces       │
├─────────────────────────────────────────┤
│  Data (implementation details)          │
│  Room DB, Retrofit, Repository Impls    │
├─────────────────────────────────────────┤
│  Core (shared utilities)                │
│  Network, Security, Privacy, Extensions │
└─────────────────────────────────────────┘
```

## Module Dependency Graph

```
app ──────────────────────────────────────────────────────┐
 │                                                         │
 ├── feature:chat ──────────────┐                          │
 ├── feature:estimator ─────────┤                          │
 ├── feature:dtc ───────────────┤                          │
 ├── feature:calculators ───────┼──── domain ──── core     │
 ├── feature:fleet ─────────────┤       │                  │
 ├── feature:voice ─────────────┤       │                  │
 ├── feature:inspection ────────┘       │                  │
 ├── data ──────────────────────────────┤                  │
 │    ├── contracts                     │                  │
 │    └── domain                        │                  │
 ├── analytics ──────────────── domain  │                  │
 └── sync ──────────────────────────────┘                  │
                                                            │
core ◄──────────────────────────────────────────────────────┘
```

## Data Flow

```
User Action
    │
    ▼
Composable (UI)
    │
    ▼
ViewModel (StateFlow<UiState>)
    │
    ▼
Use Case
    │
    ▼
Repository Interface (domain)
    │
    ├──► Repository Impl (data)
    │         ├──► Local: Room DB (offline-first cache)
    │         └──► Remote: Retrofit API
    │
    ▼
DataResult<T> (Success | Error | Loading)
```

## Offline-First Strategy

1. Read always returns from local cache (Room) immediately
2. Background sync fetches fresh data from API
3. API results overwrite cache on success
4. Errors bubble up as `DataResult.Error` but local data is still shown

## Agent Orchestration

```
User Message
    │
    ▼
ChatViewModel
    │
    ▼
AgentOrchestrator (routes based on intent)
    │
    ├──► ConversationAgent  (general chat)
    ├──► DiagnosisAgent    (symptoms, DTCs)
    ├──► EstimatorAgent    (cost estimates)
    └──► SafetyAgent       (safety classification)
```

## Security Architecture

- **Database**: SQLCipher AES-256 encryption
- **VIN/PII**: Tink AES-256-GCM encryption via AndroidKeystore
- **Network**: Certificate pinning + TLS 1.3
- **Auth**: JWT Bearer tokens via AuthInterceptor
- **Code**: ProGuard obfuscation in release builds
- **Logging**: PII redacted via TelemetryRedactor before any log output
