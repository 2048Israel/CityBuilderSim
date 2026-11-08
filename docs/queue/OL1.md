# OL1 - one long-lived orchestrator

Queued 2026-11-08. Jerus, that day, on one orchestrator that compacts in
place instead of a fresh one each day: "seems like it is worth it, so set up
a new orchstrator with the required settings then". It runs since 12:46.

WHY: each wake re-sends the whole context, about 80 wakes a day. A fresh
orchestrator starts near 70k and a two-day one reached 600k (10-22/24).
Compacting at 150k averages about 110k a wake, against about 185k for a
hand-over at 300k, and the daily hand-over's steps go.

1. `docs/orchestrator.md`, "The day": its first paragraph ("A fresh
   orchestrator each day") says instead that the orchestrator is one session
   from the app environment "CBS orchestrator" (main checkout, no worktree,
   `CLAUDE_CODE_AUTO_COMPACT_WINDOW=150000`, `CBS_ORCHESTRATOR=1`). The hooks
   in `.claude/settings.local.json` (not in git) act only under that
   variable: PreCompact prints the summary's instructions, SessionStart
   (compact) puts back the PC memory note `orchestrator-handoff.md`, which is
   kept current at every decision. It is replaced only when a turn fails
   beyond carrying on, a compaction loses something, or it starts above
   ~100k after one; then the old hand-over steps, kept as one sentence.
2. CLAUDE.md's Sessions bullet: batch sessions compact at 300k, the
   orchestrator at 150k.
3. `docs/batch.md`: any line naming the daily orchestrator or its hand-over.

Process batch: it removes more lines than it adds (One in, one out), with
the count in its notes. No code, no harness; `batch-check.sh --fast`.
