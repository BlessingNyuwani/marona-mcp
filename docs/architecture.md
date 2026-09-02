# Architecture

Each language exposes the same bounded Developer Utilities capability set:

```text
MCP client ── Streamable HTTP ── /mcp ── validated tool handler
                                      ├── estimate_model_cost
                                      └── format_release_notes

Marona Platform ── /health
                ├─ /manifest
                └─ /hub-registration
```

Python uses `marona-sdk`, which wraps the official MCP Python SDK. TypeScript
and Java use the official MCP SDKs to host the protocol endpoint. The Java
consumer uses `ai.marona:marona-sdk:0.14.2` to discover and invoke the published
App through the Marona Hub. Tool implementations are deterministic, side-effect
free, and return the same Marona standard result fields.
