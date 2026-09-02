# TypeScript MCP server

This example combines the Marona App descriptor from `marona-sdk` with the
official MCP TypeScript SDK 2.0 HTTP handler.

Marona Runtime registration, publishing, discovery, and tool calls require a
Marona Developer Key. The server-only commands below do not contact Marona
Runtime; before a Runtime operation, configure the required key locally:

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
```

```bash
corepack enable
pnpm install --frozen-lockfile
pnpm test
pnpm start
```

Inspect `http://127.0.0.1:62900/health`, `/manifest`,
`/hub-registration`, and `/mcp`.
