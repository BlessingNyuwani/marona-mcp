# syntax=docker/dockerfile:1
FROM node:22-alpine AS builder

WORKDIR /build
RUN corepack enable
COPY examples/typescript/package.json examples/typescript/pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile
COPY examples/typescript/tsconfig.json ./
COPY examples/typescript/src ./src
RUN pnpm build

FROM node:22-alpine AS runtime

ENV NODE_ENV=production \
    PORT=62900
WORKDIR /app
RUN corepack enable
COPY examples/typescript/package.json examples/typescript/pnpm-lock.yaml ./
RUN pnpm install --prod --frozen-lockfile
COPY --from=builder /build/dist ./dist
RUN chown -R node:node /app

USER node
EXPOSE 62900
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --quiet --spider http://127.0.0.1:62900/health || exit 1
ENTRYPOINT ["node", "dist/src/server.js"]
