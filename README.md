# Marona MCP

Runnable, tested examples for building standards-compliant MCP servers that can
be published to [Marona Platform](https://platform.marona.ai).

Created and maintained by **Blessing Nyuwani**, Applied AI Engineer and creator
of Marona AI Runtime.

## Marona Developer Key is required

Set `MARONA_API_KEY` before registering, publishing, discovering, or calling an
MCP App through Marona Runtime:

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
```

The key authenticates the developer and project with Marona. It is mandatory
for every Marona Runtime operation and remains separate from model-provider or
MCP-server credentials. Running the deterministic protocol server and its local
tests does not contact Marona Runtime, but using that server through Marona does
require the developer key. Create and manage keys at
<https://platform.marona.ai>; never commit a real key.

## Implementations

| Contract | Python | TypeScript | Java | Rust |
| --- | :---: | :---: | :---: | :---: |
| Streamable HTTP MCP | ✓ | ✓ | ✓ | ✓ |
| Marona health | ✓ | ✓ | ✓ | ✓ |
| Marona manifest | ✓ | ✓ | ✓ | ✓ |
| Hub registration | ✓ | ✓ | ✓ | ✓ |
| Standard structured results | ✓ | ✓ | ✓ | ✓ |
| Automated tests | ✓ | ✓ | ✓ | ✓ |
| Non-root Docker image | ✓ | ✓ | ✓ | — |

- [Python](examples/python/README.md) uses `marona-sdk==0.1.14`.
- [TypeScript](examples/typescript/README.md) uses the official MCP SDK 2.0 and `marona-sdk` descriptors.
- [Java](examples/java/README.md) uses the official MCP SDK 2.0 for the server,
  `ai.marona:marona:1.0.0` for Marona Hub access, and embedded Tomcat.
- [Rust](examples/rust/README.md) uses the official `marona-sdk = "0.1.0"` crates.io package.

All implementations expose the same two deterministic, read-only tools:
`estimate_model_cost` and `format_release_notes`.

## Run

Start Python on `http://127.0.0.1:62900`:

```bash
docker compose up --build python
```

Use `--profile typescript` or `--profile java` for the other implementations.
Inspect `/health`, `/manifest`, `/hub-registration`, and `/mcp` (Python uses
`/mcp/`).

See [architecture](docs/architecture.md) for the common contract. Publishing
requires a public HTTPS endpoint, a Marona developer account, and
`MARONA_API_KEY`; never put developer or provider credentials in tool
descriptors or registration metadata.

## License

MIT © 2026 Blessing Nyuwani. See [LICENSE](LICENSE).
