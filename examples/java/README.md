# Java MCP server

This example uses the official MCP Java SDK 2.0 Streamable HTTP servlet and an
embedded Tomcat 11 runtime to host the protocol endpoint. It also resolves
`ai.marona:marona:1.0.0` from Maven Central so Java applications can
discover the published App and call its tools through the Marona Hub. No local
SDK installation is required.

```bash
mvn test
mvn package
java -jar target/marona-mcp-java-0.1.0.jar
```

The default port is `62900`. Inspect `/health`, `/manifest`,
`/hub-registration`, and the Streamable HTTP endpoint at `/mcp`.

After registering and publishing the App in Marona, discover its tools through
the Hub-facing Java SDK:

```bash
export MARONA_API_KEY=mr_live_xxxxx # placeholder; use your real key locally
export MARONA_APP_SLUG=developer-utilities-java-example
mvn compile exec:java \
  -Dexec.mainClass=ai.marona.examples.mcp.DeveloperUtilitiesHubClient
```

`MARONA_API_KEY` is mandatory for all Marona Runtime discovery and tool calls,
including managed, BYOK, registered, private, self-hosted, and local models. It
authenticates the developer/project and is separate from provider credentials.
