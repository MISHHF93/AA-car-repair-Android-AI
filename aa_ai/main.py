from datetime import datetime, timezone
from uuid import uuid4

from fastapi import FastAPI
from pydantic import BaseModel, Field


class VehicleContext(BaseModel):
    vin: str | None = None
    make: str | None = None
    model: str | None = None
    year: int | None = None
    mileage_km: int | None = None


class ObdContext(BaseModel):
    dtc_codes: list[str] = Field(default_factory=list)
    pending_codes: list[str] = Field(default_factory=list)
    freeze_frame: dict[str, str] = Field(default_factory=dict)
    live_pids: dict[str, str] = Field(default_factory=dict)


class ChatRequest(BaseModel):
    request_id: str
    timestamp_utc: str
    surface: str
    user_role: str
    locale: str
    query_text: str
    policy_profile: str
    privacy_mode: str
    vehicle_context: VehicleContext | None = None
    obd_context: ObdContext | None = None


class ChatResponse(BaseModel):
    response_id: str
    request_id: str
    surface: str
    answer_text: str
    answer_format: str
    confidence: int
    safety_level: str
    citations: list[str]
    next_actions: list[str]
    audit_trace_id: str
    diagnosis_candidates: list[str]
    recommended_tests: list[str]
    parts_and_tools: list[str]
    estimated_time: str | None = None
    escalation: str | None = None
    risk_flags: list[str]


app = FastAPI(title="AA AI Core")


@app.post('/v1/chat', response_model=ChatResponse)
def chat(request: ChatRequest) -> ChatResponse:
    now = datetime.now(timezone.utc).isoformat()
    query = request.query_text.lower()
    obd_codes = [code.upper() for code in (request.obd_context.dtc_codes if request.obd_context else [])]
    text_codes = [code for code in ("P0171", "P0300", "P0420") if code.lower() in query]
    codes = sorted(set(obd_codes + text_codes))
    high_risk_terms = [
        "brake",
        "brakes",
        "steering",
        "airbag",
        "airbags",
        "srs",
        "high voltage",
        "ev battery",
        "fuel system",
        "jacking",
        "lifting",
    ]
    risk_flags = [term for term in high_risk_terms if term in query]

    answer = "I can help triage this repair question. Share symptoms, codes, and vehicle details for a tighter diagnosis."
    diagnosis_candidates = ["General inspection needed"]
    recommended_tests = ["Confirm symptoms", "Scan current and pending DTCs"]
    parts_and_tools = ["OBD-II scanner", "Basic inspection tools"]
    estimated_time = "15-30 minutes"
    safety_level = "medium"
    confidence = 78
    escalation = None

    if "P0171" in codes:
        answer = (
            "P0171 means the engine is running too lean on Bank 1. "
            "Common causes are vacuum leaks, MAF sensor contamination, or low fuel pressure. "
            "Start by checking intake hoses and fuel trims."
        )
        diagnosis_candidates = ["Vacuum leak", "MAF sensor contamination", "Low fuel pressure"]
        recommended_tests = ["Check live fuel trims", "Inspect intake for leaks", "Clean/test MAF sensor"]
        parts_and_tools = ["OBD-II scanner", "Smoke tester", "MAF cleaner", "Fuel pressure gauge"]
        confidence = 86
    elif "P0300" in codes:
        answer = (
            "P0300 indicates random or multiple-cylinder misfires. "
            "Check ignition, air/fuel delivery, vacuum leaks, and compression before replacing parts."
        )
        diagnosis_candidates = ["Ignition misfire", "Vacuum leak", "Fuel delivery issue", "Low compression"]
        recommended_tests = ["Inspect plugs/coils", "Check fuel trims", "Perform compression test"]
        parts_and_tools = ["OBD-II scanner", "Spark tester", "Compression gauge"]
        confidence = 82
    elif "P0420" in codes:
        answer = (
            "P0420 indicates catalyst efficiency below threshold. "
            "Confirm oxygen sensor behavior and exhaust leaks before replacing the catalytic converter."
        )
        diagnosis_candidates = ["Catalyst degradation", "Oxygen sensor issue", "Exhaust leak"]
        recommended_tests = ["Graph upstream/downstream O2 sensors", "Inspect exhaust leaks", "Check fuel trims"]
        parts_and_tools = ["OBD-II scanner", "Exhaust leak tester", "Infrared thermometer"]
        confidence = 80
        estimated_time = "30-60 minutes"

    if risk_flags:
        safety_level = "restricted"
        escalation = "Do not continue risky operation. Arrange inspection by a qualified technician."
        answer = (
            f"{answer} This includes a high-risk safety topic: {', '.join(risk_flags)}. "
            "Do not drive or perform unsafe work if control, braking, airbag/SRS, high-voltage, fuel, or lifting safety is uncertain."
        )

    return ChatResponse(
        response_id=str(uuid4()),
        request_id=request.request_id,
        surface=request.surface,
        answer_text=answer,
        answer_format="plain_text",
        confidence=confidence,
        safety_level=safety_level,
        citations=[
            "AA internal diagnostic playbook v1",
            f"generated_at:{now}",
        ],
        next_actions=recommended_tests,
        audit_trace_id=str(uuid4()),
        diagnosis_candidates=diagnosis_candidates,
        recommended_tests=recommended_tests,
        parts_and_tools=parts_and_tools,
        estimated_time=estimated_time,
        escalation=escalation,
        risk_flags=risk_flags,
    )
