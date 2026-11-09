# R6 notes ({next}, 2026-11-09)

The near-future research catalogue, 2025-2060: the study half
(`docs/studies/research-near-future.md`, with the gatherers' fact sheets in
`docs/studies/research-near-future/`). No model change, no `research.json`
row: every changed path ends in `.md`. The trace is main's; no ensemble.

## Session

- Lane D, item 3; branch `batch/R6` from main at cce06c9c (0.9.69). Started
  on Opus 5.5, moved to Fable 5.2 at the orchestrator's word (Jerus) before
  any file was written.
- The web research went to four Sonnet agents at xhigh, one per area
  (industry and food; energy and transport; medicine; business and law): 60
  subjects, each to a fact sheet with its sources and whether a figure was
  seen on the page or only in a search result. Those four sheets are
  committed beside the study as gathered, one line added under each title.
- The dates the table rests on were then looked up again from this session
  ("Checked here").

## What the study found

1. Eight rows stand on a builder's date and a measured effect, all
   engineering or medicine: four already history (driverless truck May 2025,
   sulphide heap leaching Nov 2025, enhanced geothermal Sep 2026, mRNA flu
   vaccine Aug 2026), four projected (small modular reactor, hundred-hour
   battery and personalised cancer vaccine 2027; hydrogen-reduced iron 2028).
2. The evidence ends about 2030, not 2060: after 2028 there are targets and
   nothing measured.
3. The catalogue has no row for 1996-2024. Nobody decided that; about 45
   things belong there and three of the eight rows need one of them.
4. Seven of the fourteen industrial subjects exist because carbon is priced,
   and the game prices none: carbon capture at a cement works, the design's
   own example, would be a pure cost.
5. Every plant date traced had slipped by one to four years; none came early.
6. The work rule holds: a 2027 building is 21,333 researcher-years, D$4.7 bn.
7. A near-future row has no building behind it, so the data half is not a
   data batch.

## Decisions

- The study is `docs/studies/research-near-future.md`, beside
  `research-design.md` and `research-1900.md`, with its row in the README.
- **A row's date is the first commercial-scale operation**, as the old
  catalogue's dates are (Linz 1952, Crawfordsville 1989); a drug's is its
  first approval. Where that has happened (2025-2026) the row is history and
  carries no projection mark.
- **A row needs a builder's date and an effect measured at pilot scale or
  above.** A subject whose deciding test is unread is on the watch list (the
  TB vaccine among them, its phase 3 unread), not in the table.
- **A projected date is the builder's date plus an allowance** read off the
  slips on record (the study's section 4), with a window beside it.
- **Effects are physical recipes**, as the old rows are. The carbon rows are
  a table of their own (3.3) and take no code: an earlier line of these notes
  called them a reserve, and they are better kept in sight, dated and
  sourced, waiting on ★4.
- **Codes begin F** (FE, FM), leaving E19 and M11 onward to the gap.
- The gathered sheets are committed (154 KB, four files): R6b and the gap's
  study start from them and do not pay for the search again.

## Checked here

Looked up again from this session on 2026-11-09, after the gatherers:

| what | found | so |
|---|---|---|
| mRNA flu vaccine | FDA approval 5 Aug 2026, adults 50 and over (full for 50-64, accelerated for 65 and over); 26.6% (95% CI 16.7-35.4) against the standard shot, 40,805 adults; 27.4% at 65 and over. Several press reports agree | FM1 as gathered |
| Stegra, Boden | EUR 1.4 bn closed 24 Jun 2026; the timetable "under review"; 2027 the last target, no new date | FE4 projected 2028, window 2027-2030 |
| Nuton | page opened: release of 4 Dec 2025; first cathode Nov 2025; about 30,000 t over four years; up to 85%; 71 L a kg against about 130 | FE2 as gathered |
| Form Energy, Cambridge | the utility's May 2026 plan says end of 2026; a report of Oct 2026 says 2027; "40%" in one press report, no builder's figure | FE5 projected 2027 |
| Linglong One | the IAEA's record still "under construction" at 21 May 2026; no fuel loaded by 8 Sep 2026; the first-half-2026 target missed; early 2027 in the trade press | FE6 projected 2027, window to 2030 |
| The cancer vaccine's phase 3 | endpoint met, 19 Aug 2026; one report says the announcement gave no figures, two give a hazard ratio of 0.58 (0.45-0.75) from the detailed data | FM2 quotes phase 2b first and phase 3 "as reported" |
| Vogl 2018 | 3.48 MWh a tonne of liquid steel is in the abstract; kg of hydrogen a tonne is not | FE4 gives the chemistry's 54 kg a tonne of iron, computed (3 x 2.016 over 2 x 55.845) |
| M72/AS01E | 49.7% (95% CI 2.1-74.2), 3,575 randomised, NEJM 2019; the phase 3's date is the registry's | watch list |
| The work figures | by the rule in awk; the same line returns E18's 2,998, M10's 1,786, L4's 2,567 and B8's 4,086 | section 8 |

Not checked again: everything else marked snippet in the study's source table,
and the gap's figures (section 6), which its own study sources afresh. R6b
opens a figure's page before the figure goes into `research.json`.

## The calls (★)

Each with a recommendation; the study's section 9 has the reasons.

1. **★ A projected date in the game:** fixed at the central date, the row
   marked "projected" wherever its date shows, the window kept in the data.
   Not drawn by seed, not left out of the world.
2. **★ The date rule for a projection:** the first commercial-scale
   operation, at the builder's date plus the slip allowance. Yes.
3. **★ Where the catalogue ends:** at the evidence (eight rows, to 2028), the
   watch list read again when one of its named events happens. Not a
   speculative tier to 2060 with targets for effects.
4. **★ Carbon:** the carbon rows wait; whether the game prices carbon is a
   study of its own, asked for by nobody yet.
5. **★ The gap, 1996-2024:** a study of its own, before R6b's rows that need
   it (FE5, FM1, FM2).
6. **★ A row with nothing behind it:** a row enters `research.json` in the
   batch that builds what it gives. R6b is then FE1, FE2 and FE4; the power
   rows and the medical rows go to their owners' batches.
7. **★ Codes:** FE, FM, FB, FL from 2025. Yes.
8. **★ The work rule to 2060:** kept.

## How R6's row is settled

`ship.sh` deletes R6's row at the merge. Its second half, the rows, is R6b in
the ship file's `## queued` (a name is never reused), on the recommended
calls. Two things wait on Jerus and are in `## pending`: R6-G, the study of
1996-2024 (★5), and R6-C, the carbon rows (★4).

## The checks

`./batch-check.sh --fast` on the branch: exit 0 (compile OK, maps regenerated
with none changed, Stale 0 firm findings).
`git diff --name-only origin/main` lists `.md` files only. No harness reads
what changed, so no `--pick`.

## Follow-ups, not this batch's

- After this session's compaction the `SessionStart` hook printed the
  orchestrator's hand-off note here, in a worktree, where it is meant to
  print nothing. Told to the orchestrator; nothing was done on it.
- Research-design's reserves for 1950-1995 still have no code: nuclear power
  (1957, [T48]), which FE6 and PW4 both need, reverse osmosis (1977) and
  supercritical coal (1957).
