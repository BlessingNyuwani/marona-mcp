from fastapi.testclient import TestClient

from marona_mcp_example.server import (
    agent_app,
    app,
    estimate_model_cost,
    format_release_notes,
)


def test_health_and_registration_contracts() -> None:
    with TestClient(app) as client:
        health = client.get("/health")
        manifest = client.get("/manifest")
        registration = client.get("/hub-registration")

    assert health.status_code == 200
    assert health.json()["status"] == "ok"
    assert manifest.status_code == 200
    assert manifest.json()["transport"] == "streamable_http"
    assert len(manifest.json()["tools"]) == 2
    assert registration.status_code == 200
    assert registration.json()["slug"] == "developer-utilities-example"
    assert len(agent_app.tools) == 2


def test_estimate_model_cost_returns_standard_result() -> None:
    result = estimate_model_cost(1_000, 500, 2.0, 8.0)

    assert result["success"] is True
    assert result["status"] == "completed"
    assert result["data"]["total_cost"] == 0.006


def test_release_notes_are_deterministic_markdown() -> None:
    result = format_release_notes("v1.2.0", ["Add MCP discovery", "Improve validation"])

    assert result["content_type"] == "markdown"
    assert result["content"] == (
        "## v1.2.0\n\n- Add MCP discovery\n- Improve validation"
    )
    assert result["count"] == 2
