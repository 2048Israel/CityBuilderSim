# RV1 - cheap reviews of ChatGPT's and Gemini's work

Jerus, 2026-10-13. ChatGPT (the GitHub connector, runs nothing) and Gemini
(AI Studio, runs Java) work on `chatgpt/<topic>` and `gemini/<topic>`
branches, by `openai-dontread.md` and `gemini-notes.md`; a Claude session
reviews and ships them with `./ship.sh --no-tag <branch>`. Most of a review
was mechanical - does every method or constant the text names exist where it
says? - and a review in a long session was costly, because every call re-sends
the whole conversation.

## 1. A name check, in ship.sh

- `ham.citybuildersim.tools.Names`: given a branch, read the lines it adds
  against main (Markdown, and comments in Java) and list every name the tree
  does not declare - `foo()`, `Class.foo()`, `Class.CONSTANT`, a backticked
  identifier - and every `Class.member` where that class does not declare the
  member. Declarations from `tools.Stale`'s Index and `JavaScan`, never
  regexes on source (they miss `final`, `double[]` and generic return types).
- Output: one line per unresolved name, file:line and the name, and a count;
  the exit code is the count.
- `ship.sh` runs it on `chatgpt/*` and `gemini/*` branches and prints
  "names: N unresolved" and the names. A warning, not a gate: a passage about
  the past may name a method that existed then.
- A harness section whose fixture causes both cases: a real name and a
  made-up one, and a member put on the wrong class.
- Regression: on `chatgpt/cd1-handoff` (0a516a5, shipped) it finds only names
  that exist.

## 2. Reviews off the main conversation

- `.claude/agents/assistant-review.md`, a subagent on the session's model:
  given a branch it reads the diff and the brief its QUEUE.md row or issue
  links, runs `tools.Names` and `./batch-check.sh --fast`, checks the text's
  claims against the source with Where and grep, and returns at most 15 lines:
  a verdict (ship / fix in the merge / redo), each finding at file:line, and a
  rating out of 10. It never pushes, merges or comments.
- CLAUDE.md, a few lines: "Other assistants" sends reviews to
  assistant-review or a fresh session, never a long conversation, and says
  ship.sh runs the name check on their branches; "Every line you read is paid
  for" says a long session costs more per call, so start a fresh one per task.

Checks: `./batch-check.sh`, then `./ship.sh --no-tag`.
