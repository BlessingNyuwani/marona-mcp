# Python MCP server

This example uses `marona-sdk==0.1.14`, which wraps the official Python MCP
SDK and exposes Streamable HTTP plus Marona health, manifest, and registration
contracts.

Marona Runtime registration, publishing, discovery, and tool calls require a
Marona Developer Key. The server-only commands below are deterministic and
offline from Marona Runtime; before a Runtime operation, configure the required
key with a local value:

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
```

```bash
python -m venv .venv
source .venv/bin/activate
python -m pip install -e '.[dev]'
uvicorn marona_mcp_example.server:app --host 127.0.0.1 --port 62900
```

Inspect `http://127.0.0.1:62900/health`, `/manifest`,
`/hub-registration`, and `/mcp/`.

```bash
ruff check .
mypy
pytest
```
