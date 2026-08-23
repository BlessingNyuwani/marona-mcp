import assert from "node:assert/strict";
import test from "node:test";

import { Client, StreamableHTTPClientTransport } from "@modelcontextprotocol/client";

import { agentApp, estimateModelCost, formatReleaseNotes } from "../src/contracts.js";
import { mcpHandler } from "../src/mcp.js";

test("Marona descriptor contains both MCP tools", () => {
  assert.equal(agentApp.validate().length, 0);
  assert.equal(agentApp.tools.length, 2);
});

test("standard tool results are deterministic", () => {
  const cost = estimateModelCost({
    inputTokens: 1_000,
    outputTokens: 500,
    inputPricePerMillion: 2,
    outputPricePerMillion: 8,
  });
  assert.equal(cost.success, true);
  assert.equal((cost.data as Record<string, unknown>).total_cost, 0.006);
  assert.equal(
    formatReleaseNotes("v1.2.0", ["Add MCP discovery"]).content,
    "## v1.2.0\n\n- Add MCP discovery",
  );
});

test("official MCP client can discover and call the server", async () => {
  const client = new Client({ name: "example-test", version: "0.1.0" });
  const transport = new StreamableHTTPClientTransport(new URL("http://test.local/mcp"), {
    fetch: (url, init) => mcpHandler.fetch(new Request(url, init)),
  });
  await client.connect(transport);
  try {
    const tools = await client.listTools();
    assert.deepEqual(
      tools.tools.map((tool) => tool.name).sort(),
      ["estimate_model_cost", "format_release_notes"],
    );
    const result = await client.callTool({
      name: "estimate_model_cost",
      arguments: {
        inputTokens: 1_000,
        outputTokens: 500,
        inputPricePerMillion: 2,
        outputPricePerMillion: 8,
      },
    });
    assert.equal(result.isError, undefined);
    assert.equal(
      (result.structuredContent as Record<string, unknown>).success,
      true,
    );
  } finally {
    await client.close();
  }
});
