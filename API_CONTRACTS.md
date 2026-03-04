# API Contracts

## Base URL

```
https://api.aa-carrepair.com/
```

## Authentication

All endpoints require JWT Bearer token:
```
Authorization: Bearer <token>
```

## Endpoints

### POST /v1/agent/chat

Chat with the AI agent.

**Request:**
```json
{
  "session_id": "string",
  "message": "string",
  "agent_type": "general|diagnosis|estimator|safety",
  "persona": "diy_owner|professional_technician|fleet_manager",
  "context": [{"role": "user|assistant", "content": "string"}],
  "vehicle_vin": "string?"
}
```

**Response:**
```json
{
  "session_id": "string",
  "message_id": "string",
  "content": "string",
  "agent_type": "string",
  "confidence": 0,
  "safety_level": "CRITICAL|HIGH|MEDIUM|LOW|null",
  "suggested_actions": ["string"]
}
```

### POST /v1/agent/estimate

Generate a repair estimate.

**Request:**
```json
{
  "vehicle_vin": "string",
  "service_category": "string",
  "description": "string",
  "mileage": 0,
  "prefer_oem": true,
  "zip_code": "string?"
}
```

### GET /v1/vehicle/vin/{vin}

Decode a VIN.

**Response:**
```json
{
  "vin": "string",
  "vehicle": {
    "year": 0, "make": "string", "model": "string",
    "engine": "string?", "trim": "string?"
  },
  "is_valid": true
}
```

### GET /v1/dtc/{code}

Analyze a DTC code.

**Query params:** `vin` (optional)

### POST /v1/inspection/analyze

Analyze an inspection image (multipart/form-data).

**Parts:** `image` (file), `mode` (string), `vehicle_vin` (string, optional)
