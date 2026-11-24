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
- **The idle shed over its year.** `noteIdleWorks()` hands the last one standing's earnings and the kind's
  holding to a new `noteMonth(kind, holding, month, lastEarns)`: a kind counts idle in a month when the
  lesser of that month's figure and its year's mean is below nothing. The old rule started the count again
  on any working month. It is sold (`mayShed()`) only in a month its last one loses in AND over its year, so a
  paying month sells none; a holding smaller than at its last note (one sold) starts the last one's year
  again. `BusinessInvestment.planRetirement()` no longer waits on the sector's six losing months for a
  processing sector with a kind idle over its year (`ProcessingSector.idleBuildings() > 0`). That streak
  also starts again on a paying month. The sale's words name the kind: "Rod Mill idle 7 months over its
  year, ...".
- **Copper Refining's feed gate** (`CopperRefining.feedRefusal()` → `ProcessingSector.underRunRefusal()`,
  measured by `standingRun()`). It refuses one more of a kind while the works that make its goods, standing
  and on site, would run under `FEED_GATE` (0.60) of nameplate on last month's make. The make is Sector's
  Output row as the month ended (`noteRun()`): produced, export-bound and captive over that plus idled, times
  last month's capacity over the capacity standing and on site now. The note is saved; the rows are not.
- **Saved.** Both years ride in the sector's extras (`margin.<id>.<age>`, `lastMargin.<id>.<age>`,
  newest first), with the last one's holding (`lastHolding.<id>`) and the month's run
  (`run.<GOOD>.made/.full/.capacity`), through `ProcessingSector.saveExtras()`/`restoreExtras()`, the path
  DataSave already carries. SaveFileCheck and MaterialsCheck 21 assert the round trips. No format bump (★5).

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
  **The sale is the other way round** (REV-CY1): a kind is sold only in a month its last one loses in and
  over its year. Selling on the lesser sold a quarter of the kind a month while its year still lagged a
  recovery, down to none. A sale starts the last one's year again, since its figures were the larger
  holding's; the count is kept, so a kind still losing goes on selling a month at a time.
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

The harnesses' new sections (MaterialsCheck 18 to 22, written by a slice, reviewed and amended here) cause each case through
the copper town's prices (`CopperRefiningCheck.copperWorks()`, `rodPays()`/`rodUnpaid()`, a `rodAt()` scale), month by
month in real months, each beside a twin on main's rule (its year emptied before each month):

- **18, the year itself:** the same month replaces, at most `TRAILING_MONTHS` kept, `lesser()`, and the idle count on FC2's
  Rod Mill figures (+54, -83, -33). CY1 idles from month 2; the count reaches six at month 7, a paying month, so it may
  shed from month 8, the first losing one; main's count reaches 2. `mayShed()`: true when the month and the year lose,
  false when the month pays (count 7, year D$-82.9k) or the year pays (D$134.3k). A smaller holding empties the year to
  the new figure; an equal, larger or unknown one keeps it. The extras round trip holds, the holding included, and bad
  keys are ignored.
- **19, a one-month spike:** eleven unpaid months, then one paying month. The month alone earns D$839.6k against a
  hurdle of D$12.9k, and the twin orders a Rod Mill. The year's mean is D$-615.5k, so the city orders none. Its words:
  "..., its mean over its last 12 months (...this month)". With rod paying on, the first Rod Mill comes in paying month 6.
- **20, a three-month cycle (a trough that strands no works):** 24 months, 8 peaks, 5 passing alone (the first: D$655.9k a
  month, its year D$-87.5k, hurdle D$8.5k). The city orders 0 Rod Mills and owes D$0k. The twin orders 2 and owes
  D$6,695k, its last mill losing D$75.5k a month in the last trough.
- **21, a feed-short works:** three mills with rod unpaid make 665 t and idle 12,328 t, a run of 0.0512, which equals the
  Output row's figure. The Rod Mill's first gate is FEED, worded "would run at 5% of nameplate on last month's make". With
  rod paying they run at 1.0 and the gate is open. A town with no month run reads NaN and nothing is refused. Saved to a
  scratch slot and loaded, before any month runs, with no Output row: the run reads 0.051217, the saved game's, and the
  Rod Mill is refused on FEED.
- **22, an idle works shed:** the last Rod Mill earns D$+336.4k at a peak and D$-629.5k in a trough. CY1 counts all six
  months, peaks included; main's count reaches 2. The sector's own losing streak stands at 0, under
  `RETIREMENT_LOSS_MONTHS`, and the game's own month sells a Rod Mill (2 to 1), its reason "Rod Mill idle 7 months over
  its year, 3 capacity against 0 used". After the sale the year is the month's figure alone (D$-748.3k, against D$-193.3k
  before). **The paying month (REV-CY1)**, in a fresh game: at the cycle's first peak after six idle months the last one
  earns D$+336.4k, its year D$-133.8k, 3 mills. `mayShed()` is false and the next month sells none (3 before, 3 after).
  The month after, losing (7 months, D$-193.3k), sells one (3 to 2). On the count alone (`mayShed()` returning true,
  run and restored) that line fails: "3 Rod Mills before the month, 2 after". **The first review's change:** the slice's
  sale first set six sector losses by hand (`recordSectorResult`). That exposed the second wait, and ★7 mends it. The sale
  is now the game's own, with the streak asserted as the premise.
- **SaveFileCheck:** Copper Refining's planner's year (two kinds over 16 months, the last Rod Mill over 5, its holding 7)
  survives a save, month by month, newest first; the smelter carries no holding.

The runs here, on the PC, one at a time:

- `--fast` was green on 47cb0c5e4 and again after the merge of FC2b (9ff3d6a78).
- MaterialsCheck and SaveFileCheck were green, and InvestCheck red only on its known line (`docs/notes/gate.txt`), on
  0c610bc2b and again after the merge.
- The first pick's readers ran on 0c610bc2b: SaveFileCheck, InvestCheck, BuildCardCheck, BuildingDataCheck,
  CementConcreteCheck, CoalMiningCheck, CopperBufferCheck, CopperMiningCheck, EquipmentCheck, FittingCheck, ForestryCheck,
  GdpCheck, HaulageCheck, HistoricalRecipeCheck, HydrogenSteelCheck, IndustryViewCheck, LicenceFeesCheck,
  ModernEngineeringCheck, OilCheck, OilViewCheck, OrderSearchCheck, PortCheck, QuarryingCheck, ReadPathCheck, RecipeCheck,
  RefineryCheck, RefineryViewCheck, ResearchCheck, SulphideLeachCheck, UndemandedFeedCheck and WoodProductsCheck. They
  exited 3, InvestCheck's known red the only red.
- CopperRefiningCheck ran on 47cb0c5e4, the model before ★7. It exited 3, red only on FS8's known "the rule cut an order"
  line. Seed 1 stands 0 Rod Mills at m2400 in 23.1 minutes, as main does on the PC; the 181 are box-only (OpenJDK
  21.0.13). The box's dry run is the first judge of that assertion. The row's known-reds line names CY1 as its fix, so
  `ship.sh` drops it (`tools/known-reds.sh CY1` prints it under "!").
- After REV-CY1's mends (a9ed670c8): `--fast`, MaterialsCheck and SaveFileCheck green; the 35 harnesses that read the
  planner, the processing sectors or the retirement ran one at a time: InvestCheck red on its known line, and HomeRailCheck
  red, new (section 6), green on two spurs.
- The pick is ALL (`BusinessInvestment.java`, 66 readers), so the dry run judges LongPlaytest and the rest.
- **The ensemble** (`docs/ensemble/CY1.md`), on 41d77afb0 after REV-CY1b's SHIP, 80 of 80 runs on the gate box, scored
  against 0.9.198 (FC2b's): batch verdict PASS, hard criteria 10 of 10 (M1 0 months, F1 0 of 16 in every policy), no
  verdict changed. Beside the baseline: AUTO2 reaches 40,000 people at m331 (m322), its final population 9,223,026
  (9,078,825), inflation m240 on 2.63% (2.66%); A2, the longest spell over 10%, is 13 months, FAIL as the baseline's
  (13); HIGH's unemployment over its twin 2.94 points (3.37); the pairing 64 of 64.

## 5. Left as found

- One more works is weighed at the sector's operating rate, and so is the last one standing (`SpreadPlanner.City.rate()`,
  `lastEarns()`), while the works may run cut by their order. The feed gate bounds this for copper under 60%. Between 60%
  and 100%, one more is weighed at more than it will run. Not changed: nothing in the evidence turns on it.
- Seed 2's smelters built at a peak several years wide (★1): a year does not see a nine-year charge whole.

## 6. The review (REV-CY1)

Not SHIP, one P1, two minors; all three mended.

- **P1, a paying month sold the kind.** The idle count runs on the lesser of the month and the year (★4), and
  `mayShed()` read the count alone, so in a recovery's first paying months, the year still lagging, the game sold
  ceil 25% of the kind a month down to none. Mended as the reviewer picked: sold only in a month the last one loses in
  and over its year; a sale starts the last one's year again (the holding, saved). The order side keeps the lesser.
  MaterialsCheck 22's paying month sells none, and fails on the old rule (section 4).
- **The sale's words** said "0 months of losses" for a sale on the kind's own months; they name the kind's months now.
- **The feed gate after a load.** The Output rows are not saved, so the first month after a load read the run as NaN
  and the gate open. Holding it shut until a month ran would refuse a mill a city on paying rod should get. Instead the
  month's run is noted at its end and saved, so the load reads the month before, as the game would have.
- **Found on the way:** the slice's checks showed `noteMonth()`'s new test `!(holding > 0)` true for an unknown
  holding, which the boolean overload passes, so it cleared the year every month. Now `holding <= 0`.
- **HomeRailCheck, new red after the mends:** its `tight` city's home bill was D$0.022k at m23 (a share of 0.034 set in
  m22, when the boundary dealt 197,541 t against 199,167 t of track). Main is green. The cities part at m4: main holds a
  Pulp Mill for want of staff, CY1 on money (its mean so far D$274.0k against a one-month D$770.8k; the next month it loses
  D$11.8k), so its wanted posts are not priced into wages and the city drifts. CY1's call is the right one. The fixture
  hovered within 4% of its track (main dipped under in m18 and m19), so it now lays two spurs, not three: 149,375 t of
  track under at least 202,120 t of bulk in every month m17-m23. The premise, a boundary that fills the track, is kept.
