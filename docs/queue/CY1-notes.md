# CY1 — the planner appraises a processing works over its cycle — notes

The brief is QUEUE.md's CY1 row (DS1's answer in docs/queue/CU2-notes.md, and FC2's box trace in
docs/queue/FC2-notes.md section 6). Built by Claude (Opus) on batch/CY1 from 77c77ab8c (0.9.197).

## 1. What was built

- **The margin over its year** (`SpreadPlanner`, section 7, OVER ITS YEAR). Each month a processing
  sector's `plan()` notes what one more of each kind would earn (`noteMargin()`, keyed by `Game.getMonth()`;
  a second call in the same month replaces the first) and tests each candidate on the lesser of the month's
  earnings and the mean of its last `TRAILING_MONTHS` (12) (`lesser()`, `ProcessingSector.overYear()`).
  A candidate the year holds lower is asked its gates again on the year's figure, and the plan's words say
  so: "..., its mean over its last 12 months ($54.0k this month)". `appraise()`, the build card's
  estimate and `Game.consider()`'s test are the month's, as before.
- **The idle shed over its year.** `noteIdleWorks()` hands the last one standing's earnings to a new
  `noteMonth(kind, standing, month, lastEarns)`: a kind counts idle in a month when the lesser of that
  month's figure and its year's mean is below nothing. The old rule started the count again on any working
  month. `BusinessInvestment.planRetirement()` no longer waits on the sector's six losing months for a
  processing sector with a kind idle over its year (`ProcessingSector.idleBuildings() > 0`). That streak
  also starts again on a paying month.
- **Copper Refining's feed gate** (`CopperRefining.feedRefusal()` → `ProcessingSector.underRunRefusal()`,
  measured by `standingRun()`). It refuses one more of a kind while the works that make its goods, standing
  and on site, would run under `FEED_GATE` (0.60) of nameplate on last month's make. The make is Sector's
  Output row: produced, export-bound and captive over that plus idled, times last month's capacity over the
  capacity standing and on site now.
- **Saved.** Both years ride in the sector's extras (`margin.<id>.<age>`, `lastMargin.<id>.<age>`,
  newest first) through `ProcessingSector.saveExtras()`/`restoreExtras()`, the path DataSave already
  carries. SaveFileCheck asserts the round trip. No format bump (★5).

## 2. The causes, as found

- **The month read as lasting.** `plan()` judged each candidate on one month's values: output at the import
  price up to the month's imports, feed at the export price up to its exports. A chain whose tallies swing
  month to month (FC2's seed 1: cathode exported, then imported, then idle) was judged on its best month.
- **No feed gate on a feed the market sells.** A Helper's trace (Markets.java:397-407, Sector.java:3177-3206,
  3465-3500, 3552-3598) showed a works' feed is always importable without limit, and nothing in the rate
  reads it. What idles the mills is the order. `forecastFlow()` makes the home market's demand, plus the
  exports that sell at no loss. Cathode past the sector's own is costed at the market price, so rod made
  from it does not pay to export. That is "for want of cathode" in this model, and the Output row's `idled`
  counts it.
- **The shed waited on two counts that a paying month starts again.** One is the kind's own idle count
  (`noteMonth()`). The other is the sector's losing streak (`planRetirement()`, `getLossMonths()`).
  Besides them, "building, not shrinking": no sale while any of the sector's works is on site, which a
  planner ordering one works a month kept true. The margin and the feed gate end those orders.

## 3. The calls

- ★1 **A trailing year, not the charge's own cycle.** The swings that built the works in the evidence are
  the planner's own tallies: the three-month cycle (FC2) and a one-month spike (CU2's seed 1). A year sees
  both whole: 12 is a multiple of 3, and a spike counts a twelfth. The copper charge's swing is about nine
  years (`Nations.copperChargeSwing`: "large peaks nine years apart, a slower turn beside them"). A mean
  over 108 months would hold every works a city grows into for years. MaterialsCheck 19's step from trough
  to paying took six months to pass on a year, and the same arithmetic gives four years or more on nine.
  The mean is also every processing sector's, whose roots swing on cycles of their own; only copper's is
  known. A peak several years wide, seed 2's, is not seen whole by a year. What CY1 gives there is the shed
  of a kind that loses over its year. A mid-cycle charge in copper's values would be the step past this, and
  it is not taken here.
- ★2 **The lesser of the month and the year, not the year alone.** The year alone would order at a trough
  on the strength of a past peak. The lesser only ever holds an order the month would place, and never adds
  one. A CY1 city orders no works in a month where main's rule would not; MaterialsCheck 19 and 20 play a
  twin on main's rule beside each city.
- ★3 **Noted where the planner judges** (`plan()`, once a month), not at the month's end. The year holds
  exactly the figures the plan weighed. `Game.runPrivateInvestment()` is `plan()`'s only caller in the game
  (grep), and every check that calls `plan()` without playing months reads the month alone, as before.
- ★4 **The idle test is ★2's mirror**: idle when the lesser of the month's figure and the year's is below
  nothing. A kind idle for good counts from its first month, as before (six months to a sale). A cycle that
  loses over its year counts every month. One bad month in a paying year counts one and starts again.
- ★5 **Saved, not rebuilt from history.** An appraisal reads the month's tallies, prices, rates, costs and
  gates, and no saved series holds them. No format bump: a save without the keys reads an empty year, so
  its planner reads the month alone until the year fills. That is incomplete, not wrong (the old-saves rule).
- ★6 **The feed gate reads the run, not tonnes of cathode.** HeavyIndustry's gate counts a stream no market
  sells (old steel). Read the same way for cathode, it would forbid a mill on imported cathode outright. The
  run is what the model's "want of cathode" is (section 2). The threshold is `FEED_GATE`, the railway's
  "nothing built to stand idle", reused. Copper only, as the brief has it; the measure sits on
  ProcessingSector for any client.
- ★7 **The retirement's loss wait** is one condition in `BusinessInvestment`, outside the brief's files. The
  brief's "the idle shed fires" is not met without it. MaterialsCheck 22's city counts its idle Rod Mills to
  six while the sector's streak stands at 0. Checking reality: a firm closes a plant on that plant's own
  avoidable costs (the shutdown condition is a plant's), not on whether its other plants earn. The kind's
  six months idle over its year are those losses.
- ★8 **Refining (oil) is left on its own rule.** It is not a ProcessingSector: it has its own plan (the
  crude unit's package), its own idle book by RefineryFlow kind, and no swing in the evidence. The four
  ProcessingSectors (Iron & Steel, Copper Refining, Wood Products, Cement & Concrete) all take the year and
  the shed.

## 4. The checks
