from datetime import datetime, timezone
from uuid import uuid4

from fastapi import FastAPI
from pydantic import BaseModel


class ChatRequest(BaseModel):
    request_id: str
    timestamp_utc: str
    surface: str
    user_role: str
    locale: str
    query_text: str
    policy_profile: str
    privacy_mode: str


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


app = FastAPI(title="AA AI Core")


@app.post('/v1/chat', response_model=ChatResponse)
def chat(request: ChatRequest) -> ChatResponse:
    now = datetime.now(timezone.utc).isoformat()
    answer = (
        "P0171 means the engine is running too lean on Bank 1. "
        "Common causes are vacuum leaks, MAF sensor contamination, or low fuel pressure. "
        "Start by checking intake hoses and fuel trims."
    )

    return ChatResponse(
        response_id=str(uuid4()),
        request_id=request.request_id,
        surface=request.surface,
        answer_text=answer,
        answer_format="plain_text",
        confidence=86,
        safety_level="medium",
        citations=[
            "AA internal diagnostic playbook v1",
            f"generated_at:{now}",
        ],
        next_actions=["Check live fuel trims", "Inspect intake for leaks", "Clean/test MAF sensor"],
        audit_trace_id=str(uuid4()),
    )
