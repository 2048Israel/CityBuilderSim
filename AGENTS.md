# For any AI agent

Read `docs/contract.md` first: Jerus's standing rules and how a batch is
built, checked and shipped, the one contract Claude and Codex builders share.
Then `CLAUDE.md`, the map and the tools (`docs/map`, `docs/dials.md`,
`docs/month-order.md`, `docs/harnesses.md`); it is written for every
assistant, not only Claude.

Codex (OpenAI's coding agent, in its app or through `codex exec`) builds,
checks and pushes `batch/<id>` branches as the contract says.
`openai-dontread.md` binds the ChatGPT connector alone, and Gemini reads
`gemini-notes.md`. WHY (GF1): the connector's brief told Codex it could not build.
