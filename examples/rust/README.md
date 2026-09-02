# Rust Marona MCP Server

This server uses the official
[`marona-sdk`](https://crates.io/crates/marona-sdk) package from crates.io.

Marona Runtime registration, publishing, discovery, and tool calls require a
Marona Developer Key. This standalone server does not contact Marona Runtime;
before a Runtime operation, configure the required key locally:

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
```

```bash
cargo run
```

Inspect:

- `http://127.0.0.1:62900/health`
- `http://127.0.0.1:62900/manifest`
- `http://127.0.0.1:62900/hub-registration`
- `http://127.0.0.1:62900/mcp/`

No local SDK path or Git dependency is required.
