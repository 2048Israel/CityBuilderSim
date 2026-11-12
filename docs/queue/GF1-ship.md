## changelog

### GF1 — the gate fails safe — 2026-11-11, ready, no tag (process), see docs/queue/GF1-notes.md
Fresh stage results and completion proofs prevent an interrupted check from
publishing. The branch tip and ship file are revalidated before commit.
`tools/gate.sh` runs a fresh clone, retains timestamped logs and every unnamed
red, returns the publisher's status and stops its tree using recorded process
IDs across Windows exec gaps. AllChecks ends over-limit harnesses and their
descendants after 1,740 seconds, completing KD1's first child-JVM cleanup item.
AGENTS recognizes Codex as a batch builder. Verified 128/128 publisher and 13/13
picker self-tests, `--pick` 0, full dry run 0 and timing-stop 130 with HEAD,
origin refs and the gate record preserved. Commit pairs and kept logs are in the notes.
