package ai.marona.examples.mcp;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ServerCapabilities;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DeveloperUtilitiesServer {
    private static final String COST_SCHEMA = """
            {"type":"object","properties":{
              "inputTokens":{"type":"integer","minimum":0},
              "outputTokens":{"type":"integer","minimum":0},
              "inputPricePerMillion":{"type":"number","minimum":0},
              "outputPricePerMillion":{"type":"number","minimum":0}},
             "required":["inputTokens","outputTokens","inputPricePerMillion",
                         "outputPricePerMillion"],"additionalProperties":false}
            """;
    private static final String RELEASE_SCHEMA = """
            {"type":"object","properties":{
              "version":{"type":"string","minLength":1},
              "changes":{"type":"array","items":{"type":"string","minLength":1},
                         "minItems":1,"maxItems":50}},
             "required":["version","changes"],"additionalProperties":false}
            """;

    private DeveloperUtilitiesServer() {
    }

    public static McpSyncServer createMcpServer(
            HttpServletStreamableServerTransportProvider transport
    ) {
        return McpServer.sync(transport)
                .serverInfo("developer-utilities-java-example", "0.1.0")
                .capabilities(ServerCapabilities.builder().tools(false).build())
                .toolCall(
                        Tool.builder(
                                        "estimate_model_cost",
                                        McpJsonDefaults.getMapper(),
                                        COST_SCHEMA
                                )
                                .description(
                                        "Estimate a request cost from token counts and prices."
                                )
                                .build(),
                        (exchange, request) -> result(
                                ToolHandlers.estimateModelCost(request.arguments())
                        )
                )
                .toolCall(
                        Tool.builder(
                                        "format_release_notes",
                                        McpJsonDefaults.getMapper(),
                                        RELEASE_SCHEMA
                                )
                                .description("Format a release version and changes into Markdown.")
                                .build(),
                        (exchange, request) -> result(
                                ToolHandlers.formatReleaseNotes(request.arguments())
                        )
                )
                .build();
    }

    private static CallToolResult result(Map<String, Object> value) {
        return CallToolResult.builder()
                .content(List.of(
                        TextContent.builder(String.valueOf(value.get("content"))).build()
                ))
                .structuredContent(value)
                .build();
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "62900"));
        HttpServletStreamableServerTransportProvider transport =
                HttpServletStreamableServerTransportProvider.builder()
                        .jsonMapper(McpJsonDefaults.getMapper())
                        .mcpEndpoint("/mcp")
                        .build();
        McpSyncServer mcpServer = createMcpServer(transport);

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();
        Context context = tomcat.addContext("", null);
        Tomcat.addServlet(context, "mcp", transport);
        Tomcat.addServlet(context, "metadata", new MetadataServlet());
        context.addServletMappingDecoded("/health", "metadata");
        context.addServletMappingDecoded("/manifest", "metadata");
        context.addServletMappingDecoded("/hub-registration", "metadata");
        context.addServletMappingDecoded("/*", "mcp");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            mcpServer.close();
            try {
                tomcat.stop();
            } catch (Exception ignored) {
                // The process is already stopping.
            }
        }));
        tomcat.start();
        System.out.println("Marona MCP Java example listening on port " + port);
        tomcat.getServer().await();
    }

    private static final class MetadataServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                throws java.io.IOException {
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort();
            Map<String, Object> payload = switch (request.getRequestURI()) {
                case "/health" -> Map.of(
                        "status", "ok",
                        "service", "developer-utilities-java-example",
                        "version", "0.1.0",
                        "protocol_version", "2026-07-28",
                        "mcp_sdk", "io.modelcontextprotocol.sdk"
                );
                case "/manifest" -> manifest(baseUrl);
                case "/hub-registration" -> registration(baseUrl);
                default -> Map.of("error", "not_found");
            };
            response.setStatus(payload.containsKey("error") ? 404 : 200);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(McpJsonDefaults.getMapper().writeValueAsString(payload));
        }

        private static Map<String, Object> manifest(String baseUrl) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("name", "developer-utilities-java-example");
            result.put("display_name", "Developer Utilities");
            result.put("version", "0.1.0");
            result.put("protocol_version", "2026-07-28");
            result.put("transport", "streamable_http");
            result.put("server_url", baseUrl + "/mcp");
            result.put("health_check_url", baseUrl + "/health");
            result.put("visibility", "public");
            result.put("execution_modes", List.of("online"));
            result.put("tools", List.of(
                    toolDescriptor(
                            "estimate_model_cost",
                            "Estimate a request cost from token counts and prices.",
                            COST_SCHEMA
                    ),
                    toolDescriptor(
                            "format_release_notes",
                            "Format a release version and changes into Markdown.",
                            RELEASE_SCHEMA
                    )
            ));
            return result;
        }

        private static Map<String, Object> toolDescriptor(
                String name,
                String description,
                String schema
        ) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> inputSchema = McpJsonDefaults.getMapper()
                        .readValue(schema, Map.class);
                return Map.of(
                        "name", name,
                        "description", description,
                        "input_schema", inputSchema,
                        "is_destructive", false,
                        "requires_user_confirmation", false
                );
            } catch (java.io.IOException error) {
                throw new IllegalStateException("Invalid embedded tool schema", error);
            }
        }

        private static Map<String, Object> registration(String baseUrl) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("name", "Developer Utilities");
            result.put("slug", "developer-utilities-java-example");
            result.put("description", "Calculate model budgets and format release summaries.");
            result.put("category", "Developer Tools");
            result.put("version", "0.1.0");
            result.put("website_url", "https://platform.marona.ai");
            result.put("support_url", "https://platform.marona.ai");
            result.put("visibility", "public");
            result.put("server_url", baseUrl + "/mcp");
            result.put("health_check_url", baseUrl + "/health");
            return result;
        }
    }
}
