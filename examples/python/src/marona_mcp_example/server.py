"""A deterministic MCP server ready for Marona Platform registration."""

from __future__ import annotations

from typing import Any

from marona_sdk import AgentApp, success

agent_app = AgentApp(
    name="Developer Utilities",
    slug="developer-utilities-example",
    tagline="Small, deterministic tools for developer workflows.",
    description="Calculate model budgets and format release summaries.",
    category="Developer Tools",
    version="0.1.0",
    website_url="https://platform.marona.ai",
    support_url="https://platform.marona.ai",
    visibility="public",
    execution_modes=["online"],
)


@agent_app.tool(  # type: ignore[untyped-decorator]
    title="Estimate model cost",
    description="Estimate a model request cost from token counts and per-million-token prices.",
    input_schema={
        "type": "object",
        "properties": {
            "input_tokens": {"type": "integer", "minimum": 0},
            "output_tokens": {"type": "integer", "minimum": 0},
            "input_price_per_million": {"type": "number", "minimum": 0},
            "output_price_per_million": {"type": "number", "minimum": 0},
        },
        "required": [
            "input_tokens",
            "output_tokens",
            "input_price_per_million",
            "output_price_per_million",
        ],
        "additionalProperties": False,
    },
)
def estimate_model_cost(
    input_tokens: int,
    output_tokens: int,
    input_price_per_million: float,
    output_price_per_million: float,
) -> dict[str, Any]:
    """Return a transparent, provider-neutral cost estimate."""

    input_cost = input_tokens * input_price_per_million / 1_000_000
    output_cost = output_tokens * output_price_per_million / 1_000_000
    total = input_cost + output_cost
    return success(
        f"Estimated request cost: USD {total:.6f}.",
        content=f"Estimated request cost: USD {total:.6f}.",
        content_type="metric",
        data={
            "currency": "USD",
            "input_cost": round(input_cost, 8),
            "output_cost": round(output_cost, 8),
            "total_cost": round(total, 8),
        },
    )


@agent_app.tool(  # type: ignore[untyped-decorator]
    title="Format release notes",
    description="Format a release version and change list into concise Markdown release notes.",
    input_schema={
        "type": "object",
        "properties": {
            "version": {"type": "string", "minLength": 1},
            "changes": {
                "type": "array",
                "items": {"type": "string", "minLength": 1},
                "minItems": 1,
                "maxItems": 50,
            },
        },
        "required": ["version", "changes"],
        "additionalProperties": False,
    },
)
def format_release_notes(version: str, changes: list[str]) -> dict[str, Any]:
    """Return Markdown without executing or dereferencing user content."""

    normalized = [change.strip() for change in changes if change.strip()]
    markdown = "\n".join([f"## {version}", "", *[f"- {change}" for change in normalized]])
    return success(
        f"Formatted {len(normalized)} release-note items.",
        content=markdown,
        content_type="markdown",
        count=len(normalized),
    )


app = agent_app.create_fastapi_app()
