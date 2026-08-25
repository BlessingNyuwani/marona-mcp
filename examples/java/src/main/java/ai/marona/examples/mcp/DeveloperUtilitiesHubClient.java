package ai.marona.examples.mcp;

import ai.marona.ConnectOptions;
import ai.marona.MCPConnection;
import ai.marona.mcp.MaronaMcp;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Discovers the published Developer Utilities MCP App through the Marona Hub.
 */
public final class DeveloperUtilitiesHubClient {
    private static final String DEFAULT_APP_SLUG = "developer-utilities-java-example";

    private DeveloperUtilitiesHubClient() {
    }

    public static List<JsonNode> discoverTools(String apiKey, String appSlug) {
        try (MaronaMcp mcp = new MaronaMcp(apiKey)) {
            MCPConnection connection = mcp.connect(
                    ConnectOptions.builder()
                            .apps(appSlug)
                            .build()
            );
            return connection.listTools();
        }
    }

    public static void main(String[] args) {
        String apiKey = requiredEnvironment("MARONA_API_KEY");
        String appSlug = environment("MARONA_APP_SLUG", DEFAULT_APP_SLUG);
        List<JsonNode> tools = discoverTools(apiKey, appSlug);
        System.out.println("Discovered " + tools.size() + " tools for " + appSlug + ":");
        tools.forEach(tool -> System.out.println("- " + tool.path("name").asText()));
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " is required");
        }
        return value.trim();
    }

    private static String environment(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
