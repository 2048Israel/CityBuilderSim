# GR2 — known reds in the repository, and what runs where (2026-11-12)

## Built

`KnownReds` supplies the runner's line tags and the saved-log verdict used by
batch-check and ship. Exact gate records and five-column, harness-scoped
approval parts share it. Counts require a covered assertion and complete
coverage of that harness's assertions. All failing assertions reach the judge,
including assertions past the former forty-line display limit. Old tagged and
untagged logs remain readable. Rejudging retained evidence uses today's entries.

The gate files `## known-reds` alongside the other ship records. A mending
batch's entries are removed before the check, with scratch exclusions removing
their exact-record cover too. The merge deletes those entries. One-time named
approvals retain their existing command-line entry point. The merge body records
the parts actually used.

The picker selects ALL for a class over its fan-out and for suite-runner changes.
Changed harnesses select their borrowers and TimingCheck. The publisher runs a
full suite for game classes, resources, the POM and suite-runner changes. Other
changes run the selected harnesses and changed scripts' self-tests with the
compile, maps and Stale. Those partial gates preserve the whole-suite gate record.

The launcher retains commit identities, the origin, the selection, completed
checks, generated indexes and hashes. Reuse requires an ancestor branch commit followed only by that batch's own
notes and ship commits, along with the same check selection.
Main may advance through Markdown outside the ensemble and through the approval
file. The requested version is part of the check selection. Fast gates with zero
harnesses also reuse their completed evidence. Restored indexes carry the checked
source line numbers and version into the merge. A changed source tree, unlaunched evidence or an incomplete/cancelled run
gets fresh checks. Completed exit-4 evidence can pass once its unrelated reds
are entered. The retained-run directory can be named in advance through
`CBS_GATE_DIRECTORY`, and the ship file names it in a `gate-run` comment.

## Historical approvals and follow-ups

Read the LM1 and PW1 launchers and their bodies in the existing cbs-gates
folder, and the queued repairs. Seeded 68 scoped parts after removing count-only
arguments and duplicate entries. Unqualified historical strings were traced to
harness labels, including dynamic labels in KeptCheck, PlanCheck and MapCheck.
The broad historical "at today's prices" part belongs to PriceLevelCheck.
The historical "asks -1, unpainted frames 0" part belongs to ReplayCheck's
`movedChecks`, whose comparison requires zero worker asks.

The entries link to HX1, MQ1, KL1, DM2, DM3, PX1, BQ1, CB2, CL6, KS1,
KS2, RT3, BS1 and HS1. Exact gate records continue covering their other findings,
including OX1. These historical approvals predate GR2's process changes.

Two seeded findings needed queued rows, supplied by the ship file.

- SS1 follows SaveSlotCheck's held-open fixture, lines 457 and 465. It reads
  the process-wide `GameFiles.swapsThatWaited()` counter against its saved value
  plus one after the refused swap. Both approved counter assertions need a
  fixture that isolates and accounts for the refused write and any concurrent
  successful writes. Preserve the successful write, refused write and intact
  file assertions. Done means the two counter assertions hold on Windows under
  concurrent load. The storage code and fixture match main in GR2.
- GR2-R follows ReplayCheck's `movedChecks`, line 922. The historically approved
  negative worker-ask delta fails its zero-asks comparison while unpainted
  frames stay zero. Trace ReplayMoveProbe's counter sampling across a loaded
  glide and cause that case in a fixture. Done means worker-ask deltas stay
  valid and the loaded-glide assertion holds. The replay code matches main.

## Checks

- `./batch-check.sh --fast` passed the compile, maps, Stale and tree checks.
- `./batch-check.sh KnownRedsCheck` passed, including scoped matching,
  exact-record retirement, count guards, a late assertion past line forty,
  one-time qualified crash parts, literal pipes, saved-log agreement and GF1's
  owned-child timeout checks. The final source recheck exited 0.
- `tools/pick-selftest.sh` passed 17/17 cases, including the common judge's
  ALL selection, high fan-out classes and borrowed harness fixtures.
- `tools/ship-selftest.sh` passed 135/135 cases. Its new cases file and retire
  entries, preserve gate.txt for a harness-only merge, record complete used
  parts, run a full suite for game/runner edits, reuse the same commits, allow
  Markdown-only advancement, reject changed commits and working-folder
  evidence, rejudge a stopped run after its unrelated red is named, restore
  generated indexes and reuse both zero-harness fast paths.
- A separate script-only fixture passed both paths. Its companion ran once,
  and a failing companion stopped publication and restored the fixture tree.
  Evidence is `target/gr2-script-gate.log` and its ignored fixture driver.
- `./batch-check.sh --pick` selected ALL and exited 3. All 201 harnesses ran
  in 2,195 seconds, with 195 green and six known-red harnesses. These were
  MonetaryCheck, HealthCheck, PlanCheck, KeptCheck, DeathRecordCheck and
  LongPlaytest. Thirteen timing assertions were soft. Maps regenerated and
  Stale reported zero firm findings. The whole output is in
  `target/gr2-pick.log`, with the suite and every harness log preserved in
  `target/gr2-pick-checks/`. Every red was covered by the seeded file or the
  exact gate record. This run needed no additional approval entries.

The fresh-clone full dry run passed with exit 0. Its underlying suite exited 3,
with 201 harnesses run in 2,129 seconds, 195 green and the same six known-red
harnesses. Fourteen timing assertions were soft. The compile, regenerated maps,
Stale and tree checks passed. The clone returned to its original HEAD and clean
tree. No red-line overrides were supplied, and no additional entries were needed.
The kept checks and generated indexes passed their SHA-256 manifest verification.

The simulation and resources match main. The full suite includes LongPlaytest.
The ensemble is skipped under EN1 because this process batch leaves that model
and its trace unchanged.

## Retained launcher runs

The first launcher run was stopped by the builder to complete generated-index
restoration and zero-harness replay. Its origin refs were unchanged. Its log is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GR2-20261112-01/ship.log`.
It was branch `b1dfaa9d4f24562f569e898e1e0e088c08465cbf` merged with main
`420c65dcebd0a0371d4d785145ef904af87a7883`, exit 130. Its stopped clone is retained.

Targeted fresh-clone fixtures then passed replay for full, docs-only and
script-only gates, including their generated indexes. Changing the requested
version refused the cached indexes and ran fresh checks. Evidence is
`target/gr2-reuse-paths.log`. The additional permanent self-test cases cover
restoring regenerated indexes and both zero-harness replay paths.

The completed run is
`C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GR2-20261112-02/ship.log`, with
branch `7170d8472dd17f4eafca8880de18f6451ae27cb7` merged with main
`420c65dcebd0a0371d4d785145ef904af87a7883`. Its `checks/` directory retains every
harness log, suite, timing figures and verdict. Its `generated/` directory retains
the checked indexes, with both trees sealed by `checks.sha256`. The launcher exited
0 and deleted its successful clone. `branch.commit`, `main.commit`, `selection`
and `exit` retain the identities and result alongside the log.

The initial notes and readiness commits followed that tested source commit.
The gatekeeper accepted that run and requested the review repairs recorded
below, with focused checks. GR2 reaches main by the brief's fresh review and
untagged merge.

## Process line count

The count covers all changed scripts, check machinery, the approval file and
hand-authored session/tree guidance. Generated indexes and this batch's notes
and ship file are excluded. It is `git diff --numstat origin/main` over the
files below.

716 lines added, 630 removed, +86 net after the review repairs.

```text
2	1	CLAUDE.md
15	152	batch-check.sh
40	49	docs/batch.md
86	0	docs/notes/known-reds.txt
34	35	docs/orchestrator.md
2	0	docs/tree.md
151	92	ship.sh
18	49	src/main/java/ham/citybuildersim/AllChecks.java
129	0	src/main/java/ham/citybuildersim/KnownReds.java
63	155	src/main/java/ham/citybuildersim/KnownRedsCheck.java
43	0	tools/gate-reuse.sh
14	3	tools/gate.sh
17	19	tools/pick-selftest.sh
7	65	tools/pick.sh
95	10	tools/ship-selftest.sh
```

## Gatekeeper review repairs

Readiness was withdrawn and pushed at `85c18175` before the repairs.

The two generic Denomination phrases are replaced by eight complete recorded
red lines from the canonical R1c gate record, `56f49e22:docs/notes/gate.txt`.
The generic PriceLevel phrase is replaced by the specific Retail cash and
Manufacturing cash precision failures recorded at
`30acee8d:docs/notes/gate.txt` and `97413aec:docs/notes/gate.txt`.
Rows, approval dates and causes retain their seeded values.

The available LM1 wrapper ends on a ship-file conflict. The available PW1
wrapper retains its final CallCheck/PlanCheck tail. Their full temporary clones
have been removed. The committed LM1 gate record has both nominal harnesses
green, and the retained LM1 builder transcript also reports PriceLevelCheck
green. The replacement evidence therefore uses the committed gate records of
the earlier failures whose generic approvals those launchers carried forward.
This source substitution was reported to the gatekeeper.

The same audit found three repeated KeptCheck prefixes and the loop-generated
PriceLevel person-series label. They now retain complete recorded red texts,
including the observed values and page name where present. Their evidence is
`4092b661`, `1751f426`, `5467eeda`, `1b80cc5c` and `b52be80a`, each commit's
`docs/notes/gate.txt`. The shorter duplicate yard prefix was removed, leaving
the complete assertion label. The file now has 81 scoped entries.

Reuse now accepts an ancestor branch commit when every intervening commit
changes only that batch's own notes and ship file. The history walk includes
merge changes. Any other branch path refuses reuse. The main-side rule is
preserved. The self-test now pushes notes and a ship marker after the dry run,
then publishes from the ship marker's kept-log comment with zero check reruns.
Its rejection case changes a different Markdown file after the run.

The accepted full-suite log was copied to `target/gr2-review-judge/suite.txt`
and judged against the narrowed file. It still exits 3 on the same six known-red
harnesses. The original retained artifacts and their manifest were preserved.
No additional full dry run was needed under the review's instruction.

Review checks passed on 2026-11-12. The publisher self-test passed 136/136,
including the full Ready flow and refusal after another branch file changes.
The picker self-test passed 17/17. `./batch-check.sh KnownRedsCheck` exited 0,
with regenerated maps and zero firm Stale findings. Logs are
`target/gr2-review-ship-selftest.log`, `target/gr2-review-pick-selftest.log`,
`target/gr2-review-knownreds.log` and `target/gr2-review-rejudge.log`.

### Permanent-part source audit

The table counts literal-containing physical source lines in each named
harness, then the count after Java comments are excluded. The FX-side map part
has one section title and one assertion, with its third occurrence in a comment.
The moved-building phrase has one flag emission and one comment.

Zero literal matches occur for assembled labels, runtime values or runner
protocol text. The newly pinned nominal and repeated-label entries retain the
complete recorded failure text, including values and padding, so they cover
that observed text. Existing assembled labels name a particular metric or
widget. LongPlaytest's no-FAIL marker is the existing runner diagnostic already
present verbatim in main's gate record.

| harness | part | literal source lines | code lines excluding comments |
|---|---|---:|---:|
| CentralBankCheck | `...and in at least STRICT_SHARE of them` | 1 | 1 |
| CentralBankCheck | `very loose costs trust, through the lean measured against holding the target` | 1 | 1 |
| DenominationCheck | `the same output                                                FAIL  16.3560078665 != 16.3560078869  (1.24e-09 apart)` | 0 | 0 |
| DenominationCheck | `...and the same year of it                                     FAIL  177.7918172170 != 177.7918177172  (2.81e-09 apart)` | 0 | 0 |
| DenominationCheck | `the same rent                                                  FAIL  0.0003825397 != 0.0003825397  (1.33e-09 apart)` | 0 | 0 |
| DenominationCheck | `the same price level                                           FAIL  6.3136808832 != 6.3136808652  (2.85e-09 apart)` | 0 | 0 |
| DenominationCheck | `the same currency                                              FAIL  0.0386046483 != 0.0386046475  (2.16e-08 apart)` | 0 | 0 |
| DenominationCheck | `the same treasury                                              FAIL  817.9392960580 != 817.9392936034  (3.00e-09 apart)` | 0 | 0 |
| DenominationCheck | `the same shops' till                                           FAIL  1.3182336111 != 1.3182335334  (5.90e-08 apart)` | 0 | 0 |
| DenominationCheck | `the same bank                                                  FAIL  289.1913668310 != 289.1913693393  (8.67e-09 apart)` | 0 | 0 |
| HealthCheck | `...and it buries more of its people over the run` | 1 | 1 |
| HealthCheck | `...its old first, whom a fee on senior care turns away` | 1 | 1 |
| HealthCheck | `fixture: so the beds are not what differs: at the dial's top they take no larger a share by more than the fee turns away` | 1 | 1 |
| KeptCheck | `Spending: fold debt opened adds to the page` | 0 | 0 |
| KeptCheck | `a bookRow was found on a money page of this city` | 0 | 0 |
| KeptCheck | `a month landing starts a business's KEPT and the Income page's waterfall counting exactly at the rungs countsUp() allows` | 0 | 0 |
| KeptCheck | `a month landing starts the fund's worth and a payment exactly at the rungs countsUp() allows` | 0 | 0 |
| KeptCheck | `a month's redraw after each choice hands back nine in ten of the page's nodes` | 0 | 0 |
| KeptCheck | `an animation running when a month lands past COUNT_UP_TOP is stopped on the fund's worth` | 0 | 0 |
| KeptCheck | `each month hands back nine in ten of the nodes the first draw made, or more (207 of 233) FAIL` | 0 | 0 |
| KeptCheck | `each month hands back nine in ten of the nodes the first draw made, or more (209 of 233) FAIL` | 0 | 0 |
| KeptCheck | `each month hands back nine in ten of the nodes the first draw made, or more (238 of 302) FAIL` | 0 | 0 |
| KeptCheck | `Household money: every tooltip a node had and still has is the same Tooltip after a month's kept redraw (79 of 79), and none is lost from a node the page keeps (1) FAIL` | 0 | 0 |
| KeptCheck | `most months make no node at all (10 of 25)                               FAIL` | 0 | 0 |
| KeptCheck | `most months make no node at all (12 of 25)                               FAIL` | 0 | 0 |
| KeptCheck | `most months make no node at all (11 of 25)                               FAIL` | 0 | 0 |
| KeptCheck | `no month makes more than a tenth of the nodes the first draw made` | 1 | 1 |
| KeptCheck | `note 1 opened on it adds to the page` | 0 | 0 |
| KeptCheck | `the BuybackLadder on Close it now: no month draws it again with nothing changed in it` | 0 | 0 |
| KeptCheck | `the city has a piece of paper to close` | 1 | 1 |
| KeptCheck | `the city has a plain business and a materials industry with works to draw` | 1 | 1 |
| KeptCheck | `the kept header's signature equals a header built new, at pace and short, at every rung` | 1 | 1 |
| KeptCheck | `the whole kept chrome's month is within KEPT_ALL_CHROME_MS` | 1 | 1 |
| LongPlaytest | `(no FAIL line)` | 0 | 0 |
| LongPlaytest | `a building on the map moved` | 2 | 1 |
| LongPlaytest | `industry stands farther out than the homes` | 1 | 1 |
| MapCheck | `...and issued, the card's bond buys them all` | 1 | 1 |
| MapCheck | `...and the arrivals after it go on` | 1 | 1 |
| MapCheck | `...and the layout watch` | 1 | 1 |
| MapCheck | `a block of the box bought in a month the runs do not change` | 1 | 1 |
| MapCheck | `a building on the map moved` | 1 | 0 |
| MapCheck | `a month's FX-side map work at 5B` | 3 | 2 |
| MapCheck | `a yard laid across a district's buildings moves off it the boxes under it and no others` | 1 | 1 |
| MapCheck | `a yard laid again across the buildings that stand moves off it the boxes under it and no others` | 1 | 1 |
| MapCheck | `a yard that goes moves none of the buildings` | 1 | 1 |
| MapCheck | `at 10 billion a month's change takes no more than 5 ms of the wall clock` | 0 | 0 |
| MapCheck | `at 5 billion a month's change takes no more than 5 ms of the wall clock` | 0 | 0 |
| MapCheck | `at every 30th month the map draws exactly what the city has` | 0 | 0 |
| MapCheck | `counts them apart and flags none` | 1 | 1 |
| MapCheck | `every box stands four months on` | 1 | 1 |
| MapCheck | `none of the boxes of the month the reserve came` | 1 | 1 |
| MapCheck | `short, the card's bond is the funding page's first: the 20-year term for the gap` | 0 | 0 |
| MapCheck | `the journal worked back from today's reserve` | 1 | 1 |
| MapCheck | `where the reserve is undated` | 1 | 1 |
| MonetaryCheck | `a month's delay in the hand moves them less than the allowance` | 1 | 1 |
| PlanCheck | `...the avenues were laid in the played city` | 1 | 1 |
| PlanCheck | `at every 30th month every district's plan keeps every check` | 0 | 0 |
| PriceLevelCheck | `FAIL sector.Retail.cash (nominal) month 29: 99.30704620 at the factor over the factor, 99.30704375 at today's prices (9.62e-09 apart)` | 0 | 0 |
| PriceLevelCheck | `FAIL sector.Manufacturing.cash (nominal) month 86: 1409.319315 at the factor over the factor, 1409.319335 at today's prices (5.77e-09 apart)` | 0 | 0 |
| PriceLevelCheck | `every live nominal series equal over the factor` | 1 | 1 |
| PriceLevelCheck | `every real series equal within` | 1 | 1 |
| PriceLevelCheck | `...the pop is the same to the person in every month                                    FAIL` | 0 | 0 |
| PriceLevelCheck | `...the workforce is the same to the person in every month                              FAIL` | 0 | 0 |
| PriceLevelCheck | `...the homes is the same to the person in every month                                  FAIL` | 0 | 0 |
| PriceLevelCheck | `...the stores is the same to the person in every month                                 FAIL` | 0 | 0 |
| PriceLevelCheck | `money series equal over the factor` | 1 | 1 |
| SaveSlotCheck | `...after waiting for it: its swap was refused and landed on a later try` | 1 | 1 |
| SaveSlotCheck | `...leaving the file whole as it was, and no .tmp, and it is no swap that waited and landed` | 1 | 1 |
| ScaleCheck | `...and it builds: more homes at the end than at the start` | 1 | 1 |
| ScaleCheck | `...cash: the copy is K times the city's within` | 0 | 0 |
| ScaleCheck | `...deposits: the copy is K times the city's within` | 0 | 0 |
| ScaleCheck | `...people: the copy is K times the city's within 0.0150` | 0 | 0 |
| ScaleCheck | `...prices: the copy is equal to the city's within 0.00400` | 0 | 0 |
| ScaleCheck | `...savings: the copy is K times the city's within` | 0 | 0 |
| ScaleCheck | `fixture: ...and its own rents price no migrant out` | 1 | 1 |
| ScaleCheck | `fixture: the city is growing - more people than a year before` | 1 | 1 |
| ScaleCheck | `fixture: the free copy ordered - an order of more than a million buildings decided` | 1 | 1 |
| ScaleCheck | `its rents pull migrants as the city's do, every month` | 1 | 1 |
| ShortSellingCheck | `holdings were the shares in issue plus what the world was short` | 1 | 1 |
| ShortSellingCheck | `that city passed the audit and kept its register whole too` | 1 | 1 |
| ReplayCheck | `(asks -1, unpainted frames 0` | 0 | 0 |
