# Spring AI + MCP — Product Catalogue Demo (Ollama Edition)

> 100% local · No API key · No internet required after model download

This is the companion project for the YouTube video
**"Spring AI + MCP: Build Your First AI Tool in 20 Minutes"**.

It uses **Ollama** to run an LLM on your own machine — so you can follow along
the live demo and experiment freely without needing an Anthropic or OpenAI account.

---

## How it works

```
You (browser / curl)
        │  POST /chat  "What is the price of SKU-001?"
        ▼
┌──────────────────────────────────────────┐
│  mcp-client  (port 8081)                 │
│  ChatClient + ToolCallbackProvider       │
│  → Ollama  qwen2.5:7b  (localhost:11434) │
└──────────────────┬───────────────────────┘
                   │  MCP Streamable HTTP
                   │  POST/GET /mcp
                   ▼
┌──────────────────────────────────────────┐
│  mcp-server  (port 8080)                 │
│  4 @Tool methods — product catalogue     │
│  8 products, 3 categories, in-memory     │
└──────────────────────────────────────────┘
```

**Tools available:**

| Tool | What it does |
|------|-------------|
| `getProductBySku` | Look up a product by SKU code |
| `searchProducts` | Search by keyword |
| `getLowStockProducts` | Find products below a stock threshold |
| `getProductsByCategory` | Browse Electronics / Furniture / Books |

---

## Prerequisites

| Requirement | Version | How to check |
|-------------|---------|--------------|
| Java | 17+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Ollama | latest | `ollama --version` |

That's it. No cloud account, no API key.

---

## Step 1 — Install Ollama

### macOS
```bash
brew install ollama
```
Or download from [ollama.com](https://ollama.com/download) and run the installer.

### Windows
Download the installer from [ollama.com/download](https://ollama.com/download).
Run `OllamaSetup.exe`. Ollama runs as a background service automatically.

### Linux
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

---

## Step 2 — Pull the model

Open a terminal and run:

```bash
ollama pull qwen2.5:7b
```

**Why `qwen2.5:7b`?**
It has excellent tool-calling support — it correctly decides when to call your
`@Tool` methods and formats the arguments properly. This is the most important
thing for the MCP demo to work well.

**Download size:** ~4.7 GB. This only happens once; it's cached locally after that.

**Lower RAM? Use this instead (smaller, faster, ~2 GB):**
```bash
ollama pull llama3.2:3b
```
Then change `model: qwen2.5:7b` to `model: llama3.2:3b` in
`mcp-client/src/main/resources/application.yml`.

**Model comparison for this demo:**

| Model | RAM needed | Tool-calling | Speed |
|-------|-----------|--------------|-------|
| `qwen2.5:7b` | ~8 GB | ⭐⭐⭐ Excellent | Medium |
| `llama3.2:3b` | ~4 GB | ⭐⭐ Good | Fast |
| `llama3.2:1b` | ~2 GB | ⭐ Basic | Very fast |
| `qwen2.5:14b` | ~16 GB | ⭐⭐⭐ Excellent | Slow |

---

## Step 3 — Start Ollama

### macOS / Linux
```bash
ollama serve
```
Leave this running in its own terminal. You'll see:
```
Ollama is running on http://localhost:11434
```

### Windows
Ollama runs as a background service automatically after install.
No extra step needed.

**Verify it's working:**
```bash
curl http://localhost:11434
# Expected: "Ollama is running"
```

---

## Step 4 — Start the MCP Server (Terminal 1)

```bash
cd mcp-server
mvn spring-boot:run
```

Wait for:
```
Started McpServerApplication on port 8080
Tool registered: getProductBySku
Tool registered: searchProducts
Tool registered: getLowStockProducts
Tool registered: getProductsByCategory
```

**Quick test — verify the tools are exposed:**
```bash
curl -s -X POST http://localhost:8080/mcp \
     -H "Content-Type: application/json" \
     -d '{"jsonrpc":"2.0","method":"tools/list","id":1}' | python3 -m json.tool
```
You should see all 4 tools listed.

---

## Step 5 — Start the MCP Client (Terminal 2)

Open a **new terminal** (keep the server running).

```bash
cd mcp-client
mvn spring-boot:run
```

Wait for:
```
Started McpClientApplication on port 8081
Connected to MCP server: product-catalogue (4 tools discovered)
```

---

## Step 6 — Try it out

### Option A — Browser (easiest)

Open **http://localhost:8081** in your browser.

Click any of the suggestion buttons or type your own question.

### Option B — curl (best for YouTube demo)

```bash
# Tool 1: Look up by SKU
curl -s -X POST http://localhost:8081/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "What is the price of SKU-001?"}' \
     | python3 -m json.tool

# Tool 2: Keyword search
curl -s -X POST http://localhost:8081/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "Do you sell any mechanical keyboards?"}' \
     | python3 -m json.tool

# Tool 3: Stock alert
curl -s -X POST http://localhost:8081/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "Which products are running low on stock?"}' \
     | python3 -m json.tool

# Tool 4: Browse category
curl -s -X POST http://localhost:8081/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "Show me everything in the Furniture category"}' \
     | python3 -m json.tool

# Multi-tool question (LLM picks tools on its own)
curl -s -X POST http://localhost:8081/chat \
     -H "Content-Type: application/json" \
     -d '{"message": "I need a home office setup. What do you recommend and what is out of stock?"}' \
     | python3 -m json.tool
```

---

## Product catalogue (seed data)

| SKU | Name | Category | Price | Stock |
|-----|------|----------|-------|-------|
| SKU-001 | Wireless Noise-Cancelling Headphones | Electronics | $89.99 | 42 |
| SKU-002 | Mechanical Keyboard TKL | Electronics | $129.99 | 15 |
| SKU-003 | 4K USB-C Monitor 27" | Electronics | $349.99 | **0 — out of stock** |
| SKU-004 | Ergonomic Office Chair | Furniture | $299.99 | 8 |
| SKU-005 | Standing Desk 140cm | Furniture | $449.99 | 3 |
| SKU-006 | Java Programming — Complete Guide | Books | $49.99 | 200 |
| SKU-007 | Spring Boot in Action | Books | $44.99 | 150 |
| SKU-008 | USB-C Hub 7-in-1 | Electronics | $34.99 | 5 |

---

## Demo script for the YouTube video

Run these in order — each shows a different tool being called:

```
1. "What is the price and stock level of SKU-001?"
   → calls: getProductBySku

2. "Do you have any monitors?"
   → calls: searchProducts

3. "Tell me about SKU-003 — is it in stock?"
   → calls: getProductBySku  (shows out-of-stock handling)

4. "What Java books do you sell?"
   → calls: searchProducts

5. "Show me products with fewer than 10 in stock"
   → calls: getLowStockProducts

6. "Show me all furniture"
   → calls: getProductsByCategory

7. "I want to set up a home office. What do you recommend and what's the total?"
   → calls: multiple tools in sequence (the money-shot question)
```

---

## Run the unit tests (no Ollama needed)

The server-side tests run against the in-memory catalogue only — no LLM, no network:

```bash
cd mcp-server
mvn test
```

Expected: **9 tests, all green**.

---

## Project structure

```
mcp-demo/
├── README.md
├── mcp-server/                              ← Start first (port 8080)
│   ├── pom.xml
│   └── src/main/java/com/example/mcpserver/
│       ├── McpServerApplication.java
│       ├── config/McpServerConfig.java      ← registers tools with MCP
│       ├── model/Product.java               ← Java record
│       └── service/
│           ├── ProductCatalogueService.java  ← in-memory data + seed
│           └── ProductCatalogueTools.java    ← @Tool annotated methods
│
└── mcp-client/                              ← Start second (port 8081)
    ├── pom.xml                              ← uses spring-ai-starter-model-ollama
    └── src/main/
        ├── java/com/example/mcpclient/
        │   ├── McpClientApplication.java
        │   ├── controller/ChatController.java  ← POST /chat endpoint
        │   └── service/ChatService.java        ← wires Ollama + MCP tools
        └── resources/
            ├── application.yml               ← Ollama config (no API key)
            └── static/index.html             ← browser chat UI
```

---

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `Connection refused 11434` | Ollama is not running — run `ollama serve` |
| `model not found` | Run `ollama pull qwen2.5:7b` first |
| `Connection refused 8080` | Start the MCP server first |
| Client starts but tool is never called | The model didn't decide to use the tool — try rephrasing with a SKU like "price of SKU-001" |
| First request is very slow | Normal — Ollama loads the model into RAM on first use (~10–30s). Subsequent calls are fast. |
| Out of memory | Switch to `llama3.2:3b` in `application.yml` and re-run `mvn spring-boot:run` |
| Port 8080 or 8081 already in use | `lsof -i:8080` (mac/linux) to find and kill the process |

---

## Want to use a cloud API instead?

Swap the Ollama dependency and config once you have a key:

### Switch to Anthropic Claude

**pom.xml** — replace `spring-ai-starter-model-ollama` with:
```xml
<artifactId>spring-ai-starter-model-anthropic</artifactId>
```

**application.yml** — replace the `ollama` block with:
```yaml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat.options.model: claude-opus-4-5
```

### Switch to OpenAI

**pom.xml:**
```xml
<artifactId>spring-ai-starter-model-openai</artifactId>
```

**application.yml:**
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat.options.model: gpt-4o
```

The server and all business logic stays identical — that's the power of Spring AI's
provider-agnostic design.

---

## Resources

- [Ollama model library](https://ollama.com/library) — all available models
- [Spring AI Ollama docs](https://docs.spring.io/spring-ai/reference/api/chat/ollama-chat.html)
- [Spring AI MCP docs](https://docs.spring.io/spring-ai/reference/guides/getting-started-mcp.html)
- [MCP Java SDK](https://github.com/modelcontextprotocol/java-sdk)
