# OL1 - one long-lived orchestrator: notes

Built 2026-11-16 by a Claude session (Sonnet 5.5) on `batch/OL1`, cut from
`origin/main` 437e05a2 (0.9.124). No code, no harness; `docs/` only.

## What changed

- **Item 1, `docs/orchestrator.md`, "The day"**: the first paragraph, "A fresh
  orchestrator each day", now says "One orchestrator, compacting in place": one
  session from the app environment "CBS orchestrator" (main checkout, no
  worktree, `CBS_ORCHESTRATOR=1`, `CLAUDE_CODE_AUTO_COMPACT_WINDOW=300000`);
  the local hooks (`.claude/settings.local.json`, not in git, act only under
  that variable) print the summary's instructions before a compaction
  (PreCompact) and put back the PC note `orchestrator-handoff.md`
  (SessionStart, compact), kept current at every decision; it is replaced only
  if a turn fails beyond carrying on, a compaction loses something that
  mattered, it starts above ~115k after one, or Jerus says so; the old
  hand-over steps are one sentence. Its one-line WHY names OL1, 2026-11-08 and
  the incident (BP1's fresh orchestrator each day did not happen on 11-06 or
  11-07; CX2's notes).
- **The same file, two lines that said "each orchestrator" and "each day"**:
  the hourly check-in "lives in the session that made it, so a replaced
  orchestrator deletes it" (was: "each orchestrator arms its own and deletes
  yesterday's"), and the ready-branch wait is armed "once" (was "each day,
  once"; it is re-armed after each gate as before).
- **Item 2, CLAUDE.md's Sessions bullet: a no-op.** It already says "Opus
  compacts at 300k" (CX2), which is every Opus session, the orchestrator
  included; 300k is the orchestrator's window since 11-09. Unchanged.
- **Item 3, `docs/batch.md`: a no-op.** No line names a daily orchestrator or
  its hand-over (grep for hand-over, handoff, fresh orchestrator, each day,
  daily, yesterday: none). Unchanged.
- **Left alone on purpose**: the screen-check session's "its id is in the
  hand-off note" (the note still exists and still holds it); `day.sh`'s
  "the morning" (a replacement orchestrator starts from it the same way);
  `docs/notes/batch-history.md` (its one BP1 line on `day.sh` is history, and
  no longer story was left over: the measurement is in the PC note and below).

## The process count (One in, one out)

`git diff --numstat origin/main` over the files changed, this notes file and
the ship file left out: **15 added, 14 removed, `docs/orchestrator.md` only**.
No batch-history line was added, to keep the count even.

## Figures that moved since the brief (2026-11-08), and their source

| brief said | true today | source |
|---|---|---|
| orchestrator compacts at `CLAUDE_CODE_AUTO_COMPACT_WINDOW=150000` | 300000 since 11-09 08:2x; compaction fires about 33k under it | PC note `orchestrator-handoff.md`, "The rule", line 13 |
| replaced if it starts above ~100k after a compaction | above ~115k (~95k after a compaction is normal) | same note, line 16 |
| CLAUDE.md: the orchestrator compacts at 150k | CLAUDE.md already says Opus compacts at 300k; nothing to write | CLAUDE.md, "Sessions" |
| WHY: 150k averages ~110k a wake against ~185k for a hand-over | first 11 h (11-08, 158 calls) at 150000 cost ~13% more than the daily hand-over; from 180000 about even; the reason is continuity, not cost | same note, line 12 |

The brief's cost WHY is not written into the docs, since it no longer holds;
the paragraph's WHY is the incident (the daily hand-over did not happen, CX2).

## Calls and reasons

- **The hand-over steps are one sentence, not a section**, as the brief says;
  they are the PC note's steps, shortened (the old one CronDeletes its
  check-in and brings the note current, the new one reads it and
  `sh tools/day.sh`, archives the old one once its gate has tagged, arms its
  own check-in). "Detaches its side sessions" and "the changelog's top" are in
  the note, not repeated in the docs; the sentence stays one sentence.
- **No batch-history entry.** The longer story (the 11-08 cost measurement) is
  the PC note's and the table above; adding it would have put the count over.
- **Heading** `### OL1 — one long-lived orchestrator — 2026-11-16, ready, no
  tag (process), see docs/queue/OL1-notes.md`: the brief's heading plus the
  ", see ..." tail `docs/batch.md` item 5 requires.

## Checks

`./batch-check.sh --fast`: exit 0; compile OK, maps regenerated (tree: 0
changed), Stale 0 firm findings. Dry run: see below once it has run.
