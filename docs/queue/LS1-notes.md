# LS1 notes (0.9.60, 2026-11-09)

The labour share by sector, a study (docs/queue/LS1.md). No model change.

## Session

- Restarted 01:10 11-09 after the first run's compaction loop at 00:43;
  `CLAUDE_CODE_AUTO_COMPACT_WINDOW` = 180000 (the orchestrator expected 150000).
- The first run's files committed as WIP (ae5ace4b): `docs/studies/labour-share/`
  (LabourProbe.java, bea.py, tables.py, report.py, run.sh, two BEA CSVs, out/)
  and two BEA rows in `docs/studies/licences.md`.

## Decisions
- The runs are done (first run): default-s0 to m3996, default-s1 to m2400,
  earth-rotterdam to m2400; the tables in `docs/studies/labour-share/out/`
  (report, decompose, costs, size, series, city). Not rerun. Left: the note
  (`docs/studies/spec-labour-share.md`, the name run.sh and the probe cite),
  its README row, the gap's source (wage path, markup, posts), the batch it
  recommends.
- The note is `docs/studies/spec-labour-share.md` (the name the prototypes
  cite), with its row in `docs/studies/README.md`.
- HD1-L's reading (wages trailing prices) is refuted: Luxury's share is 4.7%
  at m240, and the mean wage over the price index holds or rises in all runs.
  The markup is not it either (inputs 41% of sales, a 2.4x margin). The gap is
  sales per post: $9,000 landed x ~2.4 x 320 pieces for 7 posts.
- Copper Refining's posts per tonne look plausible; its margin (input at 52%
  of sales, net 35%) is 72% of seed 0's value added at m3996, which is why the
  old city's share is 10%.
- Recommended, in `## queued`: CU1 (Copper Refining's margin, a study) first,
  then LX1 (Luxury Retail's sales per post). Restaurants, Construction,
  Freight: gaps 0.3-0.5, both halves under 2x, no batch.
- No trace moves (no model change): no ensemble.
- Checks: `--pick` exit 0 (no harness reads the diff; the gate runs the
  suite). The first pass exited 1 on line endings: seven of the first run's
  files were written CRLF; stripped to LF, no content change.
