import { createMcpHandler, McpServer } from "@modelcontextprotocol/server";
import * as z from "zod/v4";

import { estimateModelCost, formatReleaseNotes } from "./contracts.js";

export function createServer(): McpServer {
  const server = new McpServer({ name: "developer-utilities-example", version: "0.1.0" });
  server.registerTool(
    "estimate_model_cost",
    {
      title: "Estimate model cost",
      description: "Estimate a request cost from token counts and per-million-token prices.",
      inputSchema: z.object({
        inputTokens: z.number().int().nonnegative(),
        outputTokens: z.number().int().nonnegative(),
        inputPricePerMillion: z.number().nonnegative(),
        outputPricePerMillion: z.number().nonnegative(),
      }),
    },
    async (input) => {
      const result = estimateModelCost(input);
      return {
        content: [{ type: "text", text: result.content }],
        structuredContent: result,
      };
    },
  );
  server.registerTool(
    "format_release_notes",
    {
      title: "Format release notes",
      description: "Format a release version and change list into Markdown release notes.",
      inputSchema: z.object({
        version: z.string().min(1),
        changes: z.array(z.string().min(1)).min(1).max(50),
      }),
    },
    async ({ version, changes }) => {
      const result = formatReleaseNotes(version, changes);
      return {
        content: [{ type: "text", text: result.content }],
        structuredContent: result,
      };
    },
  );
  return server;
}

export const mcpHandler = createMcpHandler(createServer);
