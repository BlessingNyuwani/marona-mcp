# Java MCP server

This example uses the official MCP Java SDK 2.0 Streamable HTTP servlet and an
embedded Tomcat 11 runtime to host the protocol endpoint. It also resolves
`ai.marona:marona-sdk:0.14.2` from Maven Central so Java applications can
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
export MARONA_API_KEY=replace_with_your_marona_api_key
export MARONA_APP_SLUG=developer-utilities-java-example
mvn compile exec:java \
  -Dexec.mainClass=ai.marona.examples.mcp.DeveloperUtilitiesHubClient
```
