# Marona MCP

Runnable, tested examples for building standards-compliant MCP servers that can
be published to [Marona Platform](https://platform.marona.ai).

Created and maintained by **Blessing Nyuwani**, Applied AI Engineer and creator
of Marona AI Runtime.

## Implementations

| Contract | Python | TypeScript | Java |
| --- | :---: | :---: | :---: |
| Streamable HTTP MCP | ✓ | ✓ | ✓ |
| Marona health | ✓ | ✓ | ✓ |
| Marona manifest | ✓ | ✓ | ✓ |
| Hub registration | ✓ | ✓ | ✓ |
| Standard structured results | ✓ | ✓ | ✓ |
| Automated tests | ✓ | ✓ | ✓ |
| Non-root Docker image | ✓ | ✓ | ✓ |

- [Python](examples/python/README.md) uses `marona-sdk==0.1.14`.
- [TypeScript](examples/typescript/README.md) uses the official MCP SDK 2.0 and `marona-sdk` descriptors.
- [Java](examples/java/README.md) uses the official MCP SDK 2.0 for the server,
  `ai.marona:marona-sdk:0.14.2` for Marona Hub access, and embedded Tomcat.

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
requires a public HTTPS endpoint and a Marona developer account; never put
provider credentials in tool descriptors or registration metadata.

## License

MIT © 2026 Blessing Nyuwani. See [LICENSE](LICENSE).
