<!-- readiness: provisional -->
## changelog
### KR1 — the ship file's known-reds template has no header row — 2026-11-17, ready, see docs/queue/KR1-notes.md
`docs/batch.md` step 5's ship-file template wrote the `## known-reds` section with a header row, `Harness | part of the
red line | queued fix | date | cause`. A builder copied it as a row, and `tools/known-reds.sh` rejected it (its third
field has a space), so `batch-check.sh` stopped with exit 1 and "malformed known-reds row" (SQ1). The template now
shows the line as a placeholder row, `<Harness> | <part of the red line> | <queued fix id> | <date> | <cause>`, and says
that no section takes a header row and a known-reds row is one red. The script is not changed: it is the gate's judge,
and a malformed row stays an error. Checked on scratch copies: a header row and the unfilled placeholder are still
rejected, a filled row passes. No code, no tag; one in, one out: batch.md 5 lines added, 5 removed.
