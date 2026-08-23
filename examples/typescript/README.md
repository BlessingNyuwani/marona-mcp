# TypeScript MCP server

This example combines the Marona App descriptor from `marona-sdk` with the
official MCP TypeScript SDK 2.0 HTTP handler.

```bash
corepack enable
pnpm install --frozen-lockfile
pnpm test
pnpm start
```

Inspect `http://127.0.0.1:62900/health`, `/manifest`,
`/hub-registration`, and `/mcp`.
