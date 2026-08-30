use marona_sdk::{AgentApp, ToolBuilder, success};
use serde_json::{Value, json};

fn build_app() -> marona_sdk::Result<AgentApp> {
    AgentApp::builder("Developer Utilities", "developer-utilities-rust-example")
        .description("Calculate model budgets and format release summaries.")
        .category("developer-tools")
        .tool(
            ToolBuilder::new(
                "estimate_model_cost",
                "Estimate a request cost from token counts and per-million-token prices.",
                json!({
                    "type": "object",
                    "properties": {
                        "input_tokens": {"type": "integer", "minimum": 0},
                        "output_tokens": {"type": "integer", "minimum": 0},
                        "input_price_per_million": {"type": "number", "minimum": 0},
                        "output_price_per_million": {"type": "number", "minimum": 0}
                    },
                    "required": [
                        "input_tokens",
                        "output_tokens",
                        "input_price_per_million",
                        "output_price_per_million"
                    ],
                    "additionalProperties": false
                }),
            ),
            |arguments: Value, _context| async move {
                let input_cost = arguments["input_tokens"].as_u64().unwrap_or_default() as f64
                    * arguments["input_price_per_million"]
                        .as_f64()
                        .unwrap_or_default()
                    / 1_000_000.0;
                let output_cost = arguments["output_tokens"].as_u64().unwrap_or_default() as f64
                    * arguments["output_price_per_million"]
                        .as_f64()
                        .unwrap_or_default()
                    / 1_000_000.0;
                let total_cost = input_cost + output_cost;
                let mut result = success(format!("Estimated request cost: USD {total_cost:.6}."))
                    .with_data(json!({
                        "currency": "USD",
                        "input_cost": input_cost,
                        "output_cost": output_cost,
                        "total_cost": total_cost
                    }));
                result.content_type = "metric".into();
                Ok(result)
            },
        )?
        .tool(
            ToolBuilder::new(
                "format_release_notes",
                "Format a release version and change list into Markdown release notes.",
                json!({
                    "type": "object",
                    "properties": {
                        "version": {"type": "string", "minLength": 1},
                        "changes": {
                            "type": "array",
                            "items": {"type": "string", "minLength": 1},
                            "minItems": 1,
                            "maxItems": 50
                        }
                    },
                    "required": ["version", "changes"],
                    "additionalProperties": false
                }),
            ),
            |arguments: Value, context| async move {
                let version = arguments["version"].as_str().unwrap_or_default();
                let changes = arguments["changes"]
                    .as_array()
                    .cloned()
                    .unwrap_or_default()
                    .into_iter()
                    .filter_map(|value| value.as_str().map(str::trim).map(str::to_owned))
                    .filter(|value| !value.is_empty())
                    .collect::<Vec<_>>();
                let markdown = format!(
                    "## {version}\n\n{}",
                    changes
                        .iter()
                        .map(|change| format!("- {change}"))
                        .collect::<Vec<_>>()
                        .join("\n")
                );
                let mut result =
                    success(format!("Formatted {} release-note items.", changes.len()))
                        .with_data(json!({"session_id": context.session_id}));
                result.content = markdown;
                result.content_type = "markdown".into();
                result.count = changes.len();
                Ok(result)
            },
        )?
        .build()
}

#[tokio::main]
async fn main() -> marona_sdk::Result<()> {
    build_app()?
        .serve("127.0.0.1:62900".parse().expect("valid address"))
        .await
}

#[cfg(test)]
mod tests {
    use super::build_app;

    #[test]
    fn manifest_exposes_both_developer_tools() {
        let manifest = build_app().expect("app builds").manifest();
        let tools = manifest["tools"].as_array().expect("tools array");
        let mut names = tools
            .iter()
            .filter_map(|tool| tool["name"].as_str())
            .collect::<Vec<_>>();
        names.sort_unstable();
        assert_eq!(names, vec!["estimate_model_cost", "format_release_notes"]);
    }
}
