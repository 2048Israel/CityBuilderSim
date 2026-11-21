# KR1 notes: the ship file's known-reds template has no header row

Builder: Claude Sonnet 5.5 session, in `.claude/worktrees/cl-KR1`, branch `batch/KR1` cut from origin/main c471e961f
(0.9.139). Process batch, docs only, no tag, no dry run (the pick is NONE).

## What I read

CLAUDE.md, `docs/batch.md` (all of it), KR1's row in `QUEUE.md`, SQ1's notes (lines 141-143: the fault as SQ1 found it),
`tools/known-reds.sh` (line 14: a row must split into five fields on ` | `, the first a harness name, the third
`[A-Za-z0-9_-]+`, the fourth a date), `docs/orchestrator.md` step 6, `docs/queue/GR2.md`, and the shipped ship files'
shape in git (MED1's).

## What I found

- `docs/batch.md` step 5's template (line 52-53) had the line `Harness | part of the red line | queued fix | date |
  cause` under `## known-reds`. `known-reds.sh` reads every non-comment, non-blank line of that section as a row and
  sets `bad` on the header (`NF == 5` holds, but `$3` is "queued fix", with a space), so `batch-check.sh` stopped
  with "malformed known-reds row" and exit 1. SQ1 hit it and left the ship file with the rows only.
- The only other copies of the header row: `docs/notes/known-reds.txt` line 3 (a `#` comment in the registry, read
  as a comment; its own header, "queued mending batch", is Jerus's standing word and the registry's, not a
  template to copy) and `docs/queue/GR2.md` line 33 (a study brief describing the line in prose, not a template a
  session copies). `CLAUDE.md`, `docs/orchestrator.md` (step 6 names the section and the rows, no header),
  `docs/deepseek.md` and `README.md` have none. Neither of the two is changed: neither reaches a ship file.
- `tools/ship-selftest.sh` writes its own known-reds fixtures as rows (line 401), so no self-test reads the template.

## What I decided

1. **Mend the template, not the script** (the orchestrator's call): the script is the gate's judge, changing it needs
   the slot and a `--branch-gate` dry run, and a malformed row should stay an error.
2. **The line is a placeholder row**, `<Harness> | <part of the red line> | <queued fix id> | <date> | <cause>`, the
   same way the template's `## queued` row is one (`| <new id: tools/batch-id.sh> | what was decided | brief |`). It
   reads as one red. Copied unfilled it is still rejected (its first field has `<>`), which is right: a placeholder
   left in a ship file is an error to see.
3. **The sentence after the block** now says it: "sections hold rows, never a header row, and a known-reds row is
   one red." I folded it into the paragraph that was already there (it replaced "Pending/queued sections hold needed
   rows."), and cut "replacement" and "and name" so the paragraph kept its four lines. No WHY added: this is a
   correction of the template, not a new rule, and its incident is SQ1's.

## Checks

- `tools/known-reds.sh KR1 <scratch ship file>` on four files under `/tmp/kr1-*` (removed after; the tree's
  `known-reds.txt` only read): a header row (`Harness | part of the red line | queued fix | date | cause`) exit 1,
  "malformed known-reds row"; the unfilled placeholder row exit 1, the same; a filled row
  (`ZzCheck | a part of the line | ZFIX | 2026-11-17 | caused by the fixture`) exit 0, printed as the last line of
  the merged registry; an empty section exit 0.
- `./batch-check.sh --fast`: exit 0, compile OK, maps regenerated, Stale 0 firm findings (187 soft), tree 0 changed.
- `sh tools/pick.sh origin/main HEAD`: NONE of 218 harnesses (2 files changed since c471e961), review not needed.
  Docs/batch.md step 4 asks a dry run only for ALL, so none was run.

## One in, one out

`git diff --numstat origin/main` over the rules, the scripts and the checks, KR1's own notes and ship file left out:
`docs/batch.md` 5 added, 5 removed. Nothing in `CLAUDE.md`, `docs/orchestrator.md`, `docs/deepseek.md` or `tools/`
changed. Net 0.

## For the orchestrator

Gate it `--no-tag` (an untagged process batch: the changelog heading has no version). No queued rows, no Pending
rows, no known reds filed. KR1's row in `QUEUE.md` goes in the merge.
