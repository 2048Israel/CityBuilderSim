# FS4 notes: KeptCheck I causes what its pages read

Batch FS4, built 2026-11-20 by Claude (Opus, headless) on `batch/FS4`, cut from
origin/main 1d8a1f243 (0.9.172) and merged with ac224abf3 (FS17) and 1557daaaf (FS14,
0.9.173). The brief is `docs/studies/flaky-suite.md`,
section 6.3. It takes KS1-rest's KeptCheck 49, KT1, KA1, KP2-N, KP3-N and
KP4-N, and KRT1's Wood Products (50) and Construction (49) lines.

The batch is harness-only. The diff touches only harness code and records:
- `KeptCheck.java` and the new `KeptTimingCheck.java`;
- `ui/KeptBench.java` (the harness's bench, not a screen);
- `Timing.HARNESSES` and `AllChecks`'s three lists;
- `docs/notes/known-reds.txt`, `docs/tree.md` and the generated maps.

No model class and no screen is touched, and the trace does not move
(`moves_playtest=no`); no ensemble is needed. Sections 55 and up are FS5's.
The only change there is that the cost sections 68, 81 and 90 move out
whole (see ★2).

## The rows

The FS4 rows of `docs/notes/known-reds.txt`, by their row numbers at 1d8a1f243:

| row | KeptCheck line | cause | change |
|---|---|---|---|
| 22 | the whole kept chrome's month is within KEPT_ALL_CHROME_MS (13) | a clock's verdict in the harness that proves the pages right, on a loaded PC | the line moves, its label and budget unchanged, to KeptTimingCheck (★2). That harness is in Timing.HARNESSES, skips when soft and is judged hard nightly. |
| 26 | a materials industry's picture (Wood Products): the page equals a page built new at every month (50) | KRT1: a Residues `Text` 15.0 high kept against 16.0 built new | the comparison is looked at again held still (★3) |
| 46 | Construction, Cash & debt as a statement: the redraw and the next equal a page built new (49) | KRT1: a statement row 836x24 kept against 836x16 new | the same (★3) |
| 62-64 | fixture: a run of orders the market lends for, both offers covering 4 times its gap at the page's cap (44), its exit and count | the search stepped the count by tens and gave up at the first decade with a gap. A city whose cash buys thousands of the building has a gap of thousands of them, more than the market covers four times. | `KeptBench.fundingRunCovered` halves the decade, for the largest count whose gap the market still covers four times (★4) |
| 76 | the construction page: each month hands back nine in ten of the nodes the first draw made, or more (40) | the share was taken against the first draw. A page that shrank along the city's path (499 of 559) failed with nothing lost. | the share against what each redraw draws again (★1); the label is new with the law |
| 94 | a count running when a month lands past COUNT_UP_TOP is stopped on a business's KEPT and the Operations page's ring (51) | the swing that moves the business was sized once, at the section's start. A business page also waited on the list's KEPT, which no figure on it reads. | the swing is sized again each month, and a business page waits on its own figure (★5) |

## What changed in KeptCheck

- **The share (★1).** `pageKeptCaused` and `pageReshapedCaused` take the share
  each month against what that redraw draws again (`Redraws`). That is the
  nodes handed back over the smaller of last month's page and this month's,
  so a page that grows or shrinks along the city's path is not a loss. Their
  callers in FS4's range are 21, 23, 25, 27, 29, 31, 33, 36, 38, 40, 42, 44,
  47 and 49. Sections 1-2, 6, 11 and 14 hold the same rule against
  HANDED_BACK.
  - Each section's Observation is now a check.
  - 1-2 adds the negative fixture: People drawn from a pool let go
    (`forgetPeople`) fails the rule.
  - 6 pulls each lever the chrome answers to with no month landing: stop,
    speed, two screens, the menu, a notice in and out, and back.
  - 11 causes the panels' rows: a building opened, and a notice. Each
    is shown, its words on the panels after and not before, and is not
    required to make a node: a build log at its forty rows drops its oldest
    as it gains one, and its kept scopes rightly hand every node back.
  - Old helpers: the 5-argument `pageKept` and the 8-argument
    `pageReshaped` keep the old law and labels for FS5's sections.
- **KRT1 held still (★3).** `againstNewHeld` is used by `picturesKept` (50)
  and by every `pageReshapedCaused` comparison (49's statements among them).
  - A difference of words, style, values, handlers or tooltip fails at once.
  - A difference only of place or size is looked at again HELD_STILL (2)
    times. Each look draws the kept page and a new one with no month and no
    kept draw between.
  - Repeated, it fails as a kept fault ("repeated held still 2 times").
  - Gone, it is printed as a reading of the moment and counted. The harness's
    last lines say how many.
- **The hovers (35, 46, 54).** Each `Observation.reached` is a check.
  - 35 causes its History row: a decision is recorded
    (`DecisionLog.TAX`) and its month played, so the hard times' right
    column has a row that moves the chart.
  - The other kinds are drawn on every city: the Infrastructure cell and
    door, the chart card's head, the ore card, the offers, the Services door
    card, the Build tiles and ring cards, the Sectors card, door and crumb.
- **44's covered run (★4).** `KeptBench.fundingRunCovered` still steps the
  count by tens. Where the decade's gap is more than the market covers four
  times over, it now halves back to the last count with no gap. It returns
  the largest count whose gap both offers would still cover four times:
  furthest from both edges, the month's cash and the market's bids.
- **51's swing (★5).**
  - `businessMoving` sizes the property-tax swing again each month it goes
    up (`swungOffset`), from the figure as printed then.
  - `figuresMoved` on a business page waits on the business's KEPT, the one
    figure there that counts. The list's waits on its KEPT and a card, as
    before.
- **The cost (★2).** Sections 5, 10, 13, 20, 24, 28, 32, 39, 43, 52, 68, 81
  and 90 leave KeptCheck for `KeptTimingCheck`, with every `KEPT_*_MS` dial,
  its sentence and its line's label.
  - KeptCheck no longer calls Timing: it leaves Timing.HARNESSES and
    KeptTimingCheck joins.
  - In AllChecks, KeptTimingCheck goes in HARNESSES after KeptCheck, in
    LONGEST_FIRST after it, and in the FX harnesses a missing toolkit skips.
  - The header's items say where each cost went.
  - KeptCheck's city is built by `KeptCheck.playedCity()`, which both
    harnesses call.
  - Each timed page is first drawn kept for `WARM_MONTHS` months, untimed
    (★6).

## The calls (★)

- ★1 **The share against what is drawn again**, not against the first draw.
  The brief: "the share is taken against HANDED_BACK, not a figure seen".
  - The kept rule is that a redraw hands back what it draws again.
  - A page whose city lost a row has nothing of that row to hand back.
    HS1's construction page handed back 499 of 559 after shrinking, with
    nothing lost.
  - R1c's Construction statement read 89.8% after growing, its new rows
    counted as losses.
  - The new label says the new law, so the old rows retire rather than move.
- ★2 **The cost moves whole, 68, 81 and 90 with it**, though they are in
  FS5's range.
  - The brief splits "5, 10, 13, 20, pageCost". Those three are pageCost's
    only callers there, and are all cost.
  - Their premises came from FS5's sections. KeptTimingCheck causes its own:
    the fund filled as section 61 fills it, its code copied rather than
    FS5's section refactored.
  - It plays the city on, without drawing, to the month each budget's own
    sentence was measured at: CITY_MONTHS, then MONEY_MONTH (2,100), then
    SCREENS_MONTH (3,500). The budgets do not move (KT1).
  - When soft it does nothing at all. The gate's soft suite pays nothing,
    and the nightly's hard timing step runs it alone.
  - KT1's own run time stays nightly. KeptCheck has been in
    AllChecks.NIGHTLY since HA1 (the study's table: "nightly, its timed
    sections split into their own harness").
  - The one check that moves with 68 ("the fund's heaviest page is Search or
    Activity, the two costed") is about what is costed, so it lives with the
    costing.
- ★3 **KRT1: a difference of place repeated held still is a fault; one that
  goes was a reading.** The brief's two outcomes, in one comparison. If the
  bounds differ the same way each time with nothing moving, the kept page
  is wrong, and the line fails. If not, the content is compared, and was
  the same.
- ★4 **The largest covered count**, not the smallest. The smallest count
  with a gap has a gap of under one building. A month's cash would close it,
  and the page would be another page.
- ★5 **51's swing sized again each month.** On a moved city a business can
  grow past a unit ("$950.0M" to "$1.0B"); sized once, the swing becomes a
  thousandth of its new printed step. The business page's wait is narrowed
  to what the page counts: no figure on it reads the list's KEPT.
- ★6 **A warm-up before each cost: `WARM_MONTHS` = `KeptCheck.MONTHS`
  (25).** In KeptCheck each cost was taken after the page's correctness
  section had drawn it for MONTHS months, so the budgets were measured on
  pages the JIT had compiled.
  - Split off and timed one draw after the page first appeared, the first
    hard run missed 15 of 29 budgets. The first pages missed by several times
    (People 60.6 ms against 12, the Sectors list 43.1 against 18); the pages
    after them, whose code was compiled by then, did not.
  - With the warm-up, the page is drawn as KeptCheck drew it before the
    clock. The budgets and labels do not move.
  - The warm-up's months move only KeptTimingCheck's own city. It still
    reaches MONEY_MONTH and SCREENS_MONTH from below (1,208 and 2,299
    there), and playTo plays it the rest of the way.

## The checks

Every JVM was one of `./batch-check.sh --fast` or a named harness, one name
a call, on the suite's default pool, with the earth and world paths set.

- `./batch-check.sh --fast`: compile OK. Stale has 0 firm findings (193
  soft before the FS14 merge, 194 after; 194 at 1d8a1f243).
  The maps were regenerated; only files that changed beyond their date stamp
  are committed.
- `./batch-check.sh TimingCheck`: exit 0. Timing.HARNESSES holds
  KeptTimingCheck and not KeptCheck, AllChecks.HARNESSES holds both, and
  every timed line prints through `Timing.verdict(ok, label)`.
- `./batch-check.sh KeptCheck` at b66b91e0e plus the split (f72701b0a):
  exit 4, with two NEW reds.
  - Section 11's build-log lever ("makes its row (0 made)"): the premise was
    wrong, not the page. It is fixed in df1a9e78d (see 11 above).
  - Section 89's fixture, FS5's range (below).
  - Every FS4 row's line was green: 26, 46, 62-64, 76 and 94.
- `./batch-check.sh KeptCheck` at df1a9e78d: exit 4, with one NEW red,
  section 89's fixture ("the Rankings tab with every measure open: ... (1
  closed; the month alone would have closed 1)").
  - The month itself closes the cell, so the kept page equals the page
    built new. Only the premise failed.
  - The city moved under it. The timed sections that played months left
    KeptCheck (10's running clock, pageCost's months, 68, 81 and 90), and
    35 plays one more.
  - By the batch rules a red caused only by a moved played city is named in
    the ship file's known-reds with FS1 as its queued fix. The line still
    asserts, and FS5 owns the section.
  - Every line below 55 is green, and every FS4 row's line with it.
- origin/main moved to ac224abf3 (FS17) and was merged at 603fa7137. The
  only conflict was known-reds: FS17 retired its SaveFileCheck row and FS4
  its eight, so neither side's lines stay. Against main the file differs by
  exactly the eight FS4 rows. The generated indexes took main's version and
  were regenerated.
- origin/main moved again to 1557daaaf (FS14) and was merged at d09e2e5ad,
  the same way: FS14 retired its HoldersCheck rows. FS14's source is
  harnesses and Observation.HARNESSES' list, which KeptCheck is still on
  (FS5's sections call it), so KeptCheck was not run again.
- `./batch-check.sh KeptCheck` after the merge (303830e24; KeptCheck has
  not changed since df1a9e78d): exit 3, known red only. The one red is 89's
  fixture, matched by the ship file's row.
- KRT1 held still: `KRT1, held still: 0` in all three KeptCheck runs.
  - The Wood Products picture (50) and the Construction statement (49) were
    equal to a page built new at every month, with nothing to look at again.
  - The 15.0-against-16.0 Text did not repeat: it was a font read under
    load. The line now compares content wherever a difference of place does
    not repeat held still, and fails if one does.
- `./batch-check.sh --timing KeptTimingCheck`, hard, on this PC, four runs.
  Other batches' suites were running beside them (nine Java processes at
  the third).

  | run | at | warm-up | budgets missed (of 29) |
  |---|---|---|---|
  | 1 | df1a9e78d | none | 15: People 60.6/12, chrome 12.4/4, whole chrome 16.0/8, History 42.6/13, Sectors list 43.1/18 among them |
  | 2 | 303830e24 | kept, 25 months | 3: People 14.7/12, Build Overview 12.1/12, Build Healthcare 15.3/12 |
  | 3 | 303830e24 | the same | 2: People 14.3/12, Build Overview 15.3/12 |
  | 4 | 2f72615e4 | kept and new, 25 months (★6) | 4: Build Overview 13.7/12, Sectors list 18.4/18, Rankings 8.3/7 and 9.8/7 |

  - The misses move between runs. Rankings was 3.0 and 5.0 in run 2 and
    8.3 and 9.8 in run 4; People went from 14.3 to 11.1. That is load.
  - Build's Overview missed in all three warmed runs. Its kept-to-new ratio
    is main's: 0.36 here, 0.32 to 0.40 in four KeptCheck runs on this PC
    (kept 6.5 to 10.9 ms, built new 18.5 to 33.6). Built new it took 37.8,
    so it is the same page under more load, not a heavier page.
  - Row 22's line (the whole kept chrome) was within KEPT_ALL_CHROME_MS in
    every warmed run: 6.0, 5.2 and 3.8 against 8.
  - In the gate's soft suite the harness skips at once. Its hard verdict is
    the nightly's, on the gate box, as KeptCheck's was.
- `./batch-check.sh TimingCheck` after the merge and the warm-up: OK, and
  KeptTimingCheck soft prints its one SKIPPED line.
- The pick: `sh tools/pick.sh origin/main HEAD` is ALL (233 harnesses),
  since AllChecks.java changed. The batch has a dry run.
- Review: not needed. There is no money, no save and no threading in the
  diff.
