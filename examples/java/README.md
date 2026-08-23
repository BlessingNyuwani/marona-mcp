# Java MCP server

This example uses the official MCP Java SDK 2.0 Streamable HTTP servlet and an
embedded Tomcat 11 runtime. Marona registration metadata is kept explicit until
a Marona Java MCP builder package is published.

```bash
mvn test
mvn package
java -jar target/marona-mcp-java-0.1.0.jar
```

The default port is `62900`. Inspect `/health`, `/manifest`,
`/hub-registration`, and the Streamable HTTP endpoint at `/mcp`.
