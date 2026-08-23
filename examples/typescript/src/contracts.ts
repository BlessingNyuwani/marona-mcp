import { AgentApp, success, type MaronaResult } from "marona-sdk";

export interface CostInput {
  inputTokens: number;
  outputTokens: number;
  inputPricePerMillion: number;
  outputPricePerMillion: number;
}

export function estimateModelCost(input: CostInput): MaronaResult {
  const inputCost = (input.inputTokens * input.inputPricePerMillion) / 1_000_000;
  const outputCost = (input.outputTokens * input.outputPricePerMillion) / 1_000_000;
  const totalCost = inputCost + outputCost;
  return success(`Estimated request cost: USD ${totalCost.toFixed(6)}.`, {
    content: `Estimated request cost: USD ${totalCost.toFixed(6)}.`,
    contentType: "metric",
    data: {
      currency: "USD",
      input_cost: Number(inputCost.toFixed(8)),
      output_cost: Number(outputCost.toFixed(8)),
      total_cost: Number(totalCost.toFixed(8)),
    },
  });
}

export function formatReleaseNotes(version: string, changes: string[]): MaronaResult {
  const normalized = changes.map((change) => change.trim()).filter(Boolean);
  const content = [`## ${version}`, "", ...normalized.map((change) => `- ${change}`)].join("\n");
  return success(`Formatted ${normalized.length} release-note items.`, {
    content,
    contentType: "markdown",
    count: normalized.length,
  });
}

export const agentApp = new AgentApp({
  name: "Developer Utilities",
  slug: "developer-utilities-typescript-example",
  tagline: "Small, deterministic tools for developer workflows.",
  description: "Calculate model budgets and format release summaries.",
  category: "Developer Tools",
  version: "0.1.0",
  websiteUrl: "https://platform.marona.ai",
  supportUrl: "https://platform.marona.ai",
  executionModes: ["online"],
  tools: [
    {
      name: "estimate_model_cost",
      title: "Estimate model cost",
      description: "Estimate a request cost from token counts and per-million-token prices.",
      inputSchema: {
        type: "object",
        properties: {
          inputTokens: { type: "integer", minimum: 0 },
          outputTokens: { type: "integer", minimum: 0 },
          inputPricePerMillion: { type: "number", minimum: 0 },
          outputPricePerMillion: { type: "number", minimum: 0 },
        },
        required: [
          "inputTokens",
          "outputTokens",
          "inputPricePerMillion",
          "outputPricePerMillion",
        ],
        additionalProperties: false,
      },
    },
    {
      name: "format_release_notes",
      title: "Format release notes",
      description: "Format a release version and change list into Markdown release notes.",
      inputSchema: {
        type: "object",
        properties: {
          version: { type: "string", minLength: 1 },
          changes: {
            type: "array",
            items: { type: "string", minLength: 1 },
            minItems: 1,
            maxItems: 50,
          },
        },
        required: ["version", "changes"],
        additionalProperties: false,
      },
    },
  ],
});
