import { createServer } from "node:http";

import { toNodeHandler, type NodeIncomingMessageLike } from "@modelcontextprotocol/node";

import { agentApp } from "./contracts.js";
import { mcpHandler } from "./mcp.js";

const port = Number.parseInt(process.env.PORT || "62900", 10);
const host = process.env.HOST || "0.0.0.0";
const mcp = toNodeHandler(mcpHandler, {
  onerror: (error) => console.error("MCP transport error", error),
});

const server = createServer(async (request, response) => {
  if (request.url?.startsWith("/mcp")) {
    await mcp(request as NodeIncomingMessageLike, response);
    return;
  }
  const publicHost = request.headers.host || `127.0.0.1:${port}`;
  const baseUrl = `http://${publicHost}`;
  let body: unknown;
  if (request.method === "GET" && request.url === "/health") {
    body = {
      status: "ok",
      service: "developer-utilities-example",
      version: "0.1.0",
      protocol_version: "2026-07-28",
      mcp_sdk: "@modelcontextprotocol/server",
    };
  } else if (request.method === "GET" && request.url === "/manifest") {
    body = agentApp.manifest({
      serverUrl: `${baseUrl}/mcp`,
      healthCheckUrl: `${baseUrl}/health`,
    });
  } else if (request.method === "GET" && request.url === "/hub-registration") {
    body = agentApp.hubRegistration({
      serverUrl: `${baseUrl}/mcp`,
      healthCheckUrl: `${baseUrl}/health`,
    });
  } else {
    response.writeHead(404, { "content-type": "application/json; charset=utf-8" });
    response.end(JSON.stringify({ error: "not_found" }));
    return;
  }
  response.writeHead(200, { "content-type": "application/json; charset=utf-8" });
  response.end(JSON.stringify(body));
});

server.listen(port, host, () => {
  console.log(`Marona MCP example listening at http://127.0.0.1:${port}`);
});

async function shutdown(): Promise<void> {
  server.close();
  await mcpHandler.close();
}

process.on("SIGINT", () => void shutdown());
process.on("SIGTERM", () => void shutdown());
