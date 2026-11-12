## changelog

### GF1 — the gate fails safe — 2026-11-12, ready, no tag (process), see docs/queue/GF1-notes.md
Fresh stage results and completion proofs prevent interrupted checks from publishing.
Main's publisher judges each merge. `--branch-gate` exercises the branch publisher
only with `--dry-run`. Real publication requires a live guardian and checks
cancellation before tagging and every push attempt, rolling back stopped commits
and tags. The launcher keeps timestamped logs, returns the publisher's status,
stops its process tree and reports actual ref changes and the stop cause.
AllChecks ends over-limit harnesses and descendants after 1,740 seconds, completing
KD1's child-JVM cleanup item. AGENTS routes Codex as a builder. Verified 141/141
publisher tests, 13/13 picker tests, `--pick` 0, accepted full dry run 0 and final development stop 130.
