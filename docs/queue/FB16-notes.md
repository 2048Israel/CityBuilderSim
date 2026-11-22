# FB16 - OE1's four follow-ups in one batch (2026-11-22)

The batch's record beside its brief, `FB16.md`: the four rows OE1 queued on 2026-11-20 (AS1, CI1, PT2, RN2), one
commit each, each harness line causing its condition. Built by one Claude session (Opus 5.5) on `batch/FB16`, cut
from origin/main 827bc03f7 (0.9.181); main had not moved at the mends. Commits: AS1 4aa294690, CI1 ccad302ff, PT2
92fba055b, RN2 ca4f45c49, the maps 08017b4c4.

## 1. AS1: AuctionCheck §11 causes the auction sale it counts

- **Found.** §11's "the mechanic fires: the treasury booked sales at auction" read its sale off LongPlaytest's
  1,200 months on automatic building. That run booked 0 sales on OE1's first mend, 9 ($68.8M) on the old estimate
  and 7 ($50.1M) on OE1's last; on 0.9.181 it books 89 ($5,062,570k). The line held only where the path sold.
- **Changed** (harness only). The run's own counts (sales, tails over a point, short sales) are read and printed
  first; then, with the probe still on, a bill is sold at the run's end: the households handed twice its face past
  their cushion (`handSpare`), the ask `max(10 x BUILD_NOTE_GRANULE, 2 x minimumIssueSize())`, quoted, and booked by
  `handleTBillLogic`. A fixture line asserts the quote is a sale; the mechanic line asserts the tally is the run's
  count plus exactly one and the probe saw that one as it was booked. The later tally lines judge the run plus it.
- **Checks.** AuctionCheck green, 363 assertions.

## 2. CI1: Motoring strikes the households' car imports from what they paid

- **Found.** `HouseholdBalance.takeCars` returns the spend as the cells' sum at cost per unit (got x price x
  households, cell by cell); `householdCarImports` was the draw's `importCost()`, the ship's one product of units
  and price. Two roundings of one sum: OE1's city, all 11 cars off a ship, read 13.07876547080566 paid against
  13.078765470805662 imported, and CarCheck's "a month of car sales is a month of real money" (`imported <= paid`)
  went red by a bit.
- **Changed** (model, `Motoring.java`). After the take, `householdCarImports = householdCarSpend x importShare`,
  the share `took.importCost() / took.cost()` (0 when nothing was drawn). A draw all off a ship has share 1.0
  exactly (x / x), so imports are the spend to the bit; a mixed draw's import half is the spend's share of it, never
  over the spend. What the households pay is untouched.
- **The fixture** (CarCheck, new section "and the imports declared are what the households paid, to the bit
  (CI1)"). With no cars on the makers' shelves, every cell is reset (no cars, debt or lockout, savings a cushion
  plus two cars) and the import price walked up a bit at a time (`importAt(nextUp(p))`, at most
  `IMPORT_BITS_TRIED` = 256) until a month's cells' sum and the ship's row (the households' import trades in
  `monthsDraws()`) part. Found in 3 steps: 11 cars, paid 10.890000000000000, the ship's product
  10.890000000000002. On main's Motoring the line fails (declared 10.890000000000002); on the branch it is
  green.
- **A line it moved.** OE1's `drawnIsPaid` asserted `imports == edgePrice` (one car off a ship, the import the
  ship's product); since CI1 the import is the households' sum, a rounding apart ($0.9900 either way, red to the bit).
  It now reads the edge's price off the ship's own row: one unit, at `edgePrice`.
- **Checks.** CarCheck green; its known-reds row leaves.

## 3. PT2: PassThroughCheck §3's fixture causes the shops' hand-over in its twelve months

- **Found.** The twins are struck from OE1's saved city. At the year's turn (m253) Retail's operating rate fell
  0.2907 to 0.2660, the shops handed over less than the households wanted, and the clearing price (0.0201) passed
  the cost floor (0.0190, 0.0193). The premise "the imported cost sets the shelf" held by the played city's staffing.
  On main's fixture a probe of the hand-over read 0 of 12 months (supply 1,158 against need 2,327, the clearing price
  at the band's top).
- **Changed** (harness only). Both twins are first played at the DEAR pin (`setStruckRate` and `pinRate`, one city
  still). Small grocery stores then stand in both twins alike, enough that the shops' places at the rate they ran at
  hand over `HAND_OVER_MARGIN` (1.5) of the households' need: the year's turn takes 7-9% off. The twins are played on at
  DEAR until the shelf holds what the places hand over in both, within `STOCKING_MONTHS` (12; two on 0.9.181), and
  only then does the weak twin step. A fixture line asserts the stores stood; a second, the shelf stocked; a third,
  that the twins are one city at the step to the bit. A new line in the twelve months counts the months both shops hand
  over at least the need with the shelf too: 12 of 12 (0 of 12 on main's fixture), the year's turn at n=10 handing
  over 3,042 against 2,332.
- **Checks.** PassThroughCheck green: groceries 14.0% of the step against the cap's 28.7%; its known-reds row leaves.

## 4. RN2: RealEstate saves its owned housing capacity

- **Found.** `RealEstate.ownedCapacity`, the count the closed homes' share (`strandedDebtShare`, RN1) moves
  against, was in neither `saveExtras()` nor `restoreExtras()`. The load's rebuild then counted from nothing
  (`Game.rebuildSimulationState()` runs the economy's `updateEcon()`, which reaches `repriceHousingCosts()` and
  `setOwnedHousingCapacity()`), so it kept the saved share and set the count to the portfolio as loaded. The live
  city still held its last month's count, and its next count moved the share from there. On OE1's path the 5 Houses the
  caused point orders between the presses raised the portfolio about 0.1%: Danzik's share read 0.12079056423500815
  live against 0.12091110568025631 reloaded, a month on and two on (ReloadCheck 2, main's red in `gate.txt`).
- **Changed** (model). `saveExtras` writes `ownedCapacity`. `restoreExtras` leaves the count at 0 and holds the
  saved one in `loadedCapacity`. The rebuild's count runs from nothing as every load's did, keeping the share as saved.
  `RealEstate.restoreLoadedCapacity()`, called in `Game.rebuildSimulationState()` right after `updateEcon()`, then puts
  the saved count back, as PW1's `restorePendingPower()` does for the power after the same rebuild. Right after the load,
  count and share are the live city's; the next month's count moves them as the live city's does. `resetExtras` clears
  both fields. Nothing reads the count between the rebuild's count and the put-back (`rentBreakEven()` is read by the
  rent's walk and the screens only).
- **The old-saves rule: no format bump owed.** The field is a sector extras key, not DataSave, so no merge after HT1
  was needed. A save from before RN2 has no key: `getOrDefault` reads 0, nothing waits, and the rebuild's count from
  nothing stands. The share is kept as saved and the count is the portfolio's, exactly as every load before RN2. Such
  a save loads as it did, not wrongly.
- **The fixtures.** HousingCheck's landlord section loads a saved landlord. The rebuild's count (1250) leaves the share
  at .2, the put-back restores 1200, and the first month's count to 1500 dilutes it to .2 x 1200 / 1500. A save with the
  key taken off loads as before. ReloadCheck gains cause 3k: on the caused point, Real Estate's count is lowered by hand
  by `CAUSED_CLOSED` (.05) through the setter the month uses, so a share of its debt is a closed home's and the month
  on's own count (the portfolio's, the Houses just ordered among them) moves the count back up and dilutes the share,
  whatever the path did. It is compared right after the load and a month on, with a by-hand fixture line.
- **Red on main.** ReloadCheck on main's RealEstate: 3k FAIL (right after the load the places it owns 18217.2 live
  against 19206.0 reloaded; a month on the share 0.07969190437569225 against 0.08401745138877244), and the two
  Danzik §2 lines. HousingCheck's first version (the count read straight after the restore) failed on main twice;
  the final one calls `restoreLoadedCapacity()`, which main lacks.
- **Checks.** HousingCheck, ReloadCheck (all checks passed, Danzik's §2 among them) and SaveFileCheck green;
  ReloadCheck's two known-reds rows leave.

## 5. The calls

- ★ **AS1: a bill caused at the run's end**, not a city of its own: the section stays the real run's, its own counts
  printed, and the line asks for exactly one sale more than the run booked, so it neither reads the path nor
  passes on a sale it did not cause.
- ★ **CI1: the import half struck as the spend's share**, `spend x importCost / cost`. Recommended over striking the
  spend from the draw (`cost()`). That would change what each cell pays, and the playtest with it; this changes only
  the declared half, and the trace is byte-identical.
- ★ **CI1's fixture walks the import price a bit at a time** until the two roundings part, so the line causes the
  disagreement it judges on any city, not only where OE1's path met one.
- **CI1: `drawnIsPaid` reads the ship's row.** The edge's price is the world's, so it is read where the world is
  paid.
- ★ **PT2: stores stood and the shelf stocked under DEAR before the step.** Rejected: stocking before the pin. The
  pin reshapes the basket (grains 0.909 to 1.499 kg) and the purchases are cash-limited (a budget near 28 against
  orders of 40-55), so a shelf stocked first never held the hand-over within 12 months. The stores alone left month
  1 shelf-bound.
- ★ **RN2: the count in sector extras, put back after the load's rebuild.** Rejected: restoring the count straight
  into the field. The rebuild's count then moves the share at the load, a month before the live city's does, so right
  after the load count and share differ. A drop between the count and the save followed by a rise in the month then
  diverges for good (share 0, count 100, loaded portfolio 90, next month 100: live 0, reloaded .09). Also rejected:
  swallowing the first count after a restore inside the setter, a coupling nothing names. The cost: `Game.java` is in
  the diff (three lines), so the pick is ALL and a dry run is owed.
- **RN2: `CAUSED_CLOSED` = .05**, a twentieth of the count: 959 of Danzik's 19,176 places at the caused point,
  so the month on's count moves the share far past ReloadCheck's cent and billionth.

## 6. The checks

- `./batch-check.sh --fast`: compile OK, maps regenerated (committed 08017b4c4), stale 0 firm, tree clean.
- `./batch-check.sh AuctionCheck CarCheck PassThroughCheck HousingCheck ReloadCheck SaveFileCheck`: suite OK, 6
  harnesses.
- `sh tools/pick.sh origin/main HEAD`: ALL, 238 harnesses (`Game.java`, 200 readers). Review: "not needed - no money,
  saves or threading in the diff". The brief names a money and saves review for RN2's saved field, which the pick's
  detector does not see (a sector extras key).
- **The playtest trace.** LongPlaytest `-Dplaytest.trace` on main's classes and on the branch's final model: all 8
  CSVs byte-identical (month, borrow, house, pop, bank, bonds, build, labour; 2,405 months); the log differs only in
  an object's hash and the runtime line (222s, 231s). No ensemble owed; `moves_playtest=no`.

## 7. For the orchestrator

- `docs/notes/gate.txt` lines 34-37 (ReloadCheck, exit 1 and Danzik's two §2 lines) are main's red that RN2 makes
  green; they are the publisher's to remove.
- The four known-reds rows (CarCheck CI1, PassThroughCheck PT2, ReloadCheck RN2 x2) leave in their rows' commits.
