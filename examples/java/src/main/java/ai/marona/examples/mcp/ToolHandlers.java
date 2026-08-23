package ai.marona.examples.mcp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ToolHandlers {
    private ToolHandlers() {
    }

    public static Map<String, Object> estimateModelCost(Map<String, Object> input) {
        long inputTokens = number(input, "inputTokens").longValue();
        long outputTokens = number(input, "outputTokens").longValue();
        double inputPrice = number(input, "inputPricePerMillion").doubleValue();
        double outputPrice = number(input, "outputPricePerMillion").doubleValue();
        double inputCost = inputTokens * inputPrice / 1_000_000;
        double outputCost = outputTokens * outputPrice / 1_000_000;
        double totalCost = inputCost + outputCost;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("currency", "USD");
        data.put("input_cost", rounded(inputCost));
        data.put("output_cost", rounded(outputCost));
        data.put("total_cost", rounded(totalCost));
        return success(
                "Estimated request cost: USD %.6f.".formatted(totalCost),
                "metric",
                Map.of("data", data)
        );
    }

    public static Map<String, Object> formatReleaseNotes(Map<String, Object> input) {
        String version = String.valueOf(input.get("version")).trim();
        @SuppressWarnings("unchecked")
        List<Object> rawChanges = (List<Object>) input.get("changes");
        List<String> changes = rawChanges.stream()
                .map(String::valueOf)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
        String content = "## " + version + "\n\n- " + String.join("\n- ", changes);
        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("count", changes.size());
        return success(
                "Formatted %d release-note items.".formatted(changes.size()),
                "markdown",
                extra,
                content
        );
    }

    private static Map<String, Object> success(
            String message,
            String contentType,
            Map<String, Object> extra
    ) {
        return success(message, contentType, extra, message);
    }

    private static Map<String, Object> success(
            String message,
            String contentType,
            Map<String, Object> extra,
            String content
    ) {
        Map<String, Object> result = new LinkedHashMap<>(extra);
        result.put("status", "completed");
        result.put("success", true);
        result.put("message", message);
        result.put("content", content);
        result.put("content_type", contentType);
        result.put("presentation_hint", "display_as_provided");
        result.put("context", "Use content as the primary user-facing result.");
        return result;
    }

    private static Number number(Map<String, Object> input, String name) {
        Object value = input.get(name);
        if (!(value instanceof Number number)) {
            throw new IllegalArgumentException(name + " must be a number");
        }
        return number;
    }

    private static double rounded(double value) {
        return Math.round(value * 100_000_000d) / 100_000_000d;
    }
}
