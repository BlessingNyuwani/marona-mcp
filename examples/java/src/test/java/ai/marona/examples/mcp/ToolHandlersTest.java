package ai.marona.examples.mcp;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToolHandlersTest {
    @Test
    void costResultUsesMaronaStandardContract() {
        Map<String, Object> result = ToolHandlers.estimateModelCost(Map.of(
                "inputTokens", 1_000,
                "outputTokens", 500,
                "inputPricePerMillion", 2.0,
                "outputPricePerMillion", 8.0
        ));

        assertTrue((Boolean) result.get("success"));
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) result.get("data");
        assertEquals(0.006, data.get("total_cost"));
    }

    @Test
    void releaseNotesAreDeterministicMarkdown() {
        Map<String, Object> result = ToolHandlers.formatReleaseNotes(Map.of(
                "version", "v1.2.0",
                "changes", List.of("Add MCP discovery", "Improve validation")
        ));

        assertEquals(
                "## v1.2.0\n\n- Add MCP discovery\n- Improve validation",
                result.get("content")
        );
        assertEquals("markdown", result.get("content_type"));
    }
}
