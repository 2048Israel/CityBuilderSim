# For any AI agent

Read `CLAUDE.md`. It is written for every assistant, not only Claude: where
the indexes are (`docs/map`, `docs/dials.md`, `docs/month-order.md`,
`docs/harnesses.md`) and the standing rules. How a batch is done and
verified here is `docs/batch.md` (a batch session) and `docs/orchestrator.md`
(the orchestrator); `CLAUDE.md` says who reads which.

Codex (OpenAI's coding agent, in its app or through `codex exec`) builds,
checks and pushes `batch/<id>` branches as `docs/batch.md` says. The old
`openai-dontread.md` binds the ChatGPT connector alone. Gemini reads
`gemini-notes.md`. WHY (GF1): the connector's brief told Codex it could not build.
