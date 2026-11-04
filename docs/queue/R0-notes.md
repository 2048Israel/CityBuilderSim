# R0 - the Policy levers on history's dates ({next}, 2026-11-04)

The first implementation batch of research from 1900: its row is "the 1900
economy and the calendar from 1900" (`docs/studies/research-1900.md` §5.4,
§7, §12; `research-design.md` §9). Jerus took every call of the study as
recommended (2026-10-18, "rec all"). Built on CM1 (lane 1's predecessor,
unmerged at the start).

## WHERE IT STANDS

(Kept at the top; a session resuming from GitHub starts here.)

- 2026-11-04 10:50: these notes written; the split and the calls below taken.
- **Slice 1 done** (8edef28f): `PolicyDates` (the 21 levers: 12 dated, 9 open
  from founding; care subsidies, deposit insurance and the 1915 levy have no
  lever in the game) and `PolicyDatesCheck` (219 ok).
- **Slice 2a done, reviewed, committed** (the gate for the eight levers
  `TaxPolicy` holds, `TaxPolicy.GATED`): a closed lever's dial and offsets are
  held at 0 by `TaxPolicy.setMonth()`, its setters refuse, `copy()` keeps the
  gate; `Game.nextMonth()` pushes the month right after `month++` (the one
  place the month moves; `simulateMonth()` runs inside it), `buildWorld`
  after `reset()`, and the load path after the month is read; the openings
  saved as `DataSave.leverOpenings` by `Lever.name()` (null: every lever
  open); `Inbox.takeLevers` raises "An income tax: 1917" once, on the month;
  `PolicyDatesCheck` 7-9 (303 ok). `restorePolicyState` puts a save back as
  saved (a `restoring` flag past the refusals).
- **Slice 2b done, reviewed, committed** (03588cd6): `Game.policyHearsTheMonth()`
  (founding, the top of each month, a load) pushes the month to the policy,
  `LabourMarket.setFloorBinding(isOpen(MINIMUM_WAGE))` (call 12: the floor's
  `Math.max` and `isPinned()`'s floor skipped while shut; the anchor
  untouched) and the policy's pension to the households (call 15: month 1
  pays none); the player's minimum-wage path is `Game.setMinimumWage` /
  `setMinimumWageAdjustment`, refused while shut (PolicyScreen calls it;
  `LabourMarket`'s own setter stays open for fixtures); student loans shut to
  1939 (call 14: `HouseholdBalance.setStudentLoansOpen`, pushed with the loan
  rate each month; `StudentHousehold` lends nothing and plans with no room);
  `Founding.foundingBuildersRate()` quotes the month-1 gate (0); the inbox's
  `NOTICED` adds the minimum wage. `PolicyDatesCheck` 10-11 (324 ok).
- **Fixtures opened** (call 7; `getTaxPolicy().reset()` then `openAll()` after
  founding, since founding has already held the rates at 0): ReadPathCheck,
  SaveFileCheck (its lever fixtures, its `college` fixture for the loans, and
  its `city` and `early` fixtures: call 17), LabourCheck, ChartCheck,
  ManufacturingCheck, PolicyCheck, EducationCheck, OutsideCheck, HealthCheck,
  PolicyPreviewCheck, FoodProcessingCheck, TreasuryCheck, ZoningCheck (a tax
  offset fixture). Each green named; LongPlaytest green (every month audited,
  every reload matched).
- **ReplayCheck green again** (its premise is "a dial the player moved off
  the recording": the dial is now the property tax, open from founding, as
  the income tax is shut in its 1904 city; the city stays on history's
  dates).
- **Red, known, next:** HomeRailCheck 1, "a played city whose boundary fills
  the railway ...: home share 0" (on `HaulageCheck.played`): not a lever's
  fixture; a 1900 city with no income taxes grows differently - read it at
  its cause (call 7). **The full suite has
  not run**; other reds of the same shape may remain.
- **The finding that needs Jerus's eye: call 16** (below): a lever opening at
  zero leaves the playtest's city untaxed for 49 years.
- **Next:** the full suite (`./batch-check.sh --pick`, then `AllChecks -f`),
  each red read by call 7; call 16 answered; slice 3 (the screen: a shut
  lever drawn shut, "opens 1917 - an income tax, 4% above $1,500"; zoning's
  gate, call 13, not built yet); the ensemble. No ship file until CM1 is on
  main and merged in.

## 1. What R0's row holds now, and where it splits

The study's R0 list (§5.4, §12 item 1) was written at 0.7.108. Since then
EP1 (0.8.95) and the gold study GD0 have taken half of it:

| R0 item (study) | where it is now |
|---|---|
| the calendar's epoch to 1900 | **done**, EP1 (`CityCalendar.EPOCH_YEAR` = 1900, month 1 is January 1900) |
| the world's prices, wages, interest and migration by year (§6.1) | **EP1b** (world-1.bin rebuilt from 1900; `PriceLevel` already carries the US CPI from 1900) |
| Money's gold years and the Bank of Canada (§7.2) | **GD1-GD4, FM1-FM3** build gold and the city's money; the dated states wait for them: **R0f** |
| the Policy levers, closed until their date (§7) | **R0, this batch** |
| founding wages at 1901's real level, the 1901 census's ratios (§5.2) | **R0d** |
| the arrivals' skill mix by year (§5.4 item 6) | **R0d** |
| the household's basket by year (§5.4 item 5) | **R0e**, after R0c (lamp oil, coal and heat are R0c's goods) |
| the build list by year (`buildings.json`'s `since`, unread since EP0) | **R2** (the catalogue: what opens each building; Q12's no-discovery buildings open at founding) |
| the gap the catalogue leaves, sector by sector (§5.4 item 7) | **R1/R2** (it needs the recipes) |

By `docs/batch.md` 3 on size, what is left is still several batches: the
levers alone reach `TaxPolicy` (58 readers), the minimum wage that anchors the
ladder, zoning, every harness city founded in January 1900 with a tax set,
the save, the inbox and the Policy screen. **R0 builds the levers on their
dates and nothing else.** R0d, R0e and R0f go in the ship file's `## queued`.

## 2. The calls (★), each taken as recommended

1. **★ The split** (section 1). Recommended and taken: R0 = the levers; the
   wages and skill mix R0d; the basket R0e after R0c; Money's dates R0f after
   GD2 and FM1. Why: EP1 settled "today's tech" until R0-R5 bring 1900's;
   the levers are law, not tech, so they are the one piece that stands alone,
   and the study calls them "the strongest single piece of the tutorial".
2. **★ The 1901 wage ladder waits for 1900's tech.** A quarter of today's real
   wage beside today's recipes is four times the profit per worker, and LD1's
   bidding would undo it within months. Recommended and taken: R0d records the
   ladder (each `PayTier` rung's 1901 comparator, sourced or flagged est.,
   §12.1's table) and switches it on with R1, when the recipes are 1900's.
3. **★ The game's three income taxes against the study's table.** The game has
   profit, sales and wage taxes (since 0.7.4); the wage tax, banded by
   `WageBand`, is the personal income tax. Recommended and taken: profit tax
   1916 (PL2), **wage tax 1917 (PL3, the Income War Tax Act)**, sales tax 1920
   (PL5). The 1915 workers' compensation levy (PL1) has no lever in the game
   and is left out, as the study leaves out family allowances.
4. **★ The pension's two dials.** Recommended and taken: the replacement (the
   promise) opens 1927 (PL7, the Old Age Pensions Act, paid from the
   treasury); the contribution opens 1966 (L5, the Canada Pension Plan).
5. **★ The levers the study's table does not date.** Food assistance, the
   transit fare, the tuition scales, the health fee scale, the farmland
   relief, the fuel duty, port dues and the sector subsidies. Recommended and
   taken: open from founding. Fees, fares, school rates, harbour dues and
   municipal "bonuses" were a city's own in 1900 (§7.1's last line); poor
   relief predates 1900; a fuel duty with no cars taxes nothing. Each is
   listed in `PolicyDates` as open, with that reason, so a later source moves
   one line.
6. **★ Student aid.** The grant and the loan rate open together in 1939 (PL9,
   Dominion-Provincial Student Aid). Taken as recommended.
7. **★ A lever on history's dates in every city, harness cities too.**
   Recommended and taken: yes, it is what a city founded in 1900 is. A
   harness whose premise is a lever's mechanics (a wage tax collected, an EI
   benefit paid) opens that lever in its fixture, explicitly - the fixture
   causes the condition (README, The checks) - and says so in its label; the
   playtest runs history's dates. The alternative, a per-city switch off for
   harnesses, would leave the real game's path unchecked.
8. **★ Early opening by a law find** (§7.2: up to four years). Taken: the
   rule and its bound are data now (`PolicyDates.MAX_EARLY_MONTHS`, 48), and
   the saved opening month is what a find moves; the find itself is R3's
   (discoveries do not exist yet). R0 asserts nothing opens early without one.
9. **★ What "opens at zero" means in the model.** Recommended and taken: a
   closed lever's dial reads and charges 0 whatever was set (the effective
   rates in `TaxPolicy` are the one seam everything is charged at), its
   setter refuses a change, and on its month it opens at 0 with an inbox
   notice of Canada's first terms ("an income tax: 4% above $1,500, 1917").
   The player then sets it.
10. **★ The save.** Each lever's opening month is saved per city (§12's
    "Save"), so a law find in R3 can move one. An old save has no such field:
    Gson leaves it missing, which would read as "every lever open since month
    1" - incomplete, not wrong, for a city founded before R0 (its taxes were
    set). No format bump. Taken as recommended.
11. **★ A date the study gives by the year alone** opens in January of that
    year (profit tax 1916, income tax 1917, ...); one it gives to the month
    opens in that month (the Advance Rate August 1914, the Bank of Canada
    March 1935, EI August 1940, Saskatchewan January 1947, the CPP January
    1966). Why: a month would be a claim the study does not make. Taken.
12. **★ The minimum wage is also the ladder's anchor** (found in slice 2a's
    reading): `LabourMarket.baseWage()` is `minimumWage x ratio x
    costOfLiving` for every job, and the floor is the same figure
    (`cashMinimumWage()`, applied as `Math.max(wage, floor)`). Zero would zero
    every wage. Recommended and taken for slice 2b: before 1918 the floor does
    not bind (the `Math.max` step skipped: an unskilled surplus can price
    itself below the base, as 1900's could) and the dial and its nudge are
    shut; from 1918 the floor binds at the dial, which stands at the ladder's
    anchor, so this one lever opens at the going unskilled wage, not at zero.
    The alternative, a legislated floor apart from the anchor, opening at 0,
    is larger and moves LabourCheck's premises.
13. **★ Zoning before 1924** is "everything free, the decided default" (§7.1):
    no zone drawn and none charged until PL6's month (slice 2b reads
    `Zoning` first).
14. **★ Student loans before 1939:** the loans themselves are shut (no new
    loan is made), not merely a 0% rate, which would be free money; slice 2a
    left the loan rate alone for this reason.
15. **★ Two readers that quote a shut lever** (found in slice 2a): 
    `Founding.foundingBuildersRate()` quotes the 15% sales tax from a bare
    policy while the city charges 0 in 1900, and `HouseholdAccounts` starts
    from the 45% pension default until Game (~L3547) overwrites it each
    month. Slice 2b points both at the city's gated policy.
16. **★ A lever that opens at zero stays there in an untended city**
    (measured, LongPlaytest, one run each, so noisy; the ensemble decides).
    The playtest's player checks in every few decades and first sets the
    income tax at m593-616 (1949-51). With R0 the city has no profit, wage or
    sales tax until then, and only the property tax:

    | | m~260 people, cash | m~600 people | m~1,050 people | m2405 people |
    |---|---|---|---|---|
    | CM1 (base: 15% from month 1) | 3,964, $42.5M | 11,133 | 41,074 | 372,861 |
    | R0 as built (opens at 0) | 2,742, -$150k | 4,297 | 10,357 | 142,712 |
    | trial: opens at the founding default (15%) | 4,064, $2.2M | 16,344 | 17,349 | 239,908 |

    The R0 city's treasury is below zero from about 1921 to 1967. The
    ensemble's people criteria (40,000 by m987 in EP1-E) will not hold.
    **Recommended:** keep the dates and the notice, and open the three income
    taxes at the founding default (`TaxPolicy.DEFAULT_INCOME_TAX`) instead of
    zero, the notice saying Canada's first terms and that the rate is the
    player's; the promises (pensions, EI, student aid, the health premium)
    still open at zero, as they cost the city rather than fund it. Why: the
    tutorial is the date and the notice; zero only punishes a player who has
    not read the inbox yet, for decades. The alternative is zero, as the study
    and "rec all" decided, with the playtest's player taught to answer an
    opening notice, and the ensemble's people criteria re-read. Not built
    either way: R0 still opens at zero.
17. **★ SaveFileCheck's `city` and `early` fixtures** set no lever, but their
    condition (something mid-build at the save; the index based in its six
    months) failed because a 1900 city with no taxes and no floor builds
    faster (the 20 depots finished inside a month). Slice 2b opened every
    lever in them. Recommended: keep - their premise is the save of a
    mid-build city, and the open levers put back the city that causes it; the
    alternative is a fixture that causes mid-build directly.

## 3. The slices

1. **`PolicyDates`, pure data, and `PolicyDatesCheck`** (no model change). An
   enum of the game's levers, each with Canada's first month (§7.1), the
   study's entry key (PL2...), its first terms in a sentence and its source
   keys; `opensAt(lever)`, `isOpen(lever, month)`, the early-opening bound.
   The harness: every dated lever's month is the table's; property tax and
   the open ones are open in month 1; each closed lever is closed the month
   before its date and open on it; nothing opens more than 48 months early.
2. **The gate in the model.** 2a (done): the eight levers in `TaxPolicy`,
   the month push, the save, the inbox. 2b (next): the minimum wage's floor
   (call 12), zoning (13), the student loans (14), the two readers of call
   15; then the harness ripple. A harness red because its fixture sets a
   shut lever in month 1 opens that lever in its fixture
   (`getTaxPolicy().openAll()`, or one lever where the premise is one), its
   label saying so (call 7); a red that is the model's (a city with no
   income taxes to 1916-20 grows differently) is read at its cause and never
   re-premised. The full suite once 2b's own harnesses are green.
3. **The screen**: a closed lever on the Policy tab drawn shut with "opens
   1917 - an income tax, 4% above $1,500" (checked by eye).
4. **The ensemble**: the trace moves (a 1900 city without income tax for 17
   years), so `tools/ensemble/ensemble.sh R0` once on the final model.

## 4. Records

- Ship file: not yet (CM1 is not on main; the ship file is pushed last, once
  it is and is merged in). Its `## queued` rows, decided here (call 1):

  | R0d | Research: the 1901 wage ladder and the arrivals' skill mix by year - each `PayTier` rung's 1901 comparator, sourced or flagged est. (research-1900 §5.2, §12.1), switched on with R1's 1900 recipes (R0-notes call 2); arrivals' diplomas from the year's mix (1.2 university students per 1,000 in 1901, 4.7 in 1950; nurses 0.05 to 3) | [R0-notes §1-2](docs/queue/R0-notes.md), [research-1900 §5.2, §5.4](docs/studies/research-1900.md#52-wages) |
  | R0e | Research: the household's basket by year - 1900's raw staples, lamp oil and coal, no car (after R0c's goods, and on the live demand path, not the shadow basket) | [research-1900 §4, §5.4](docs/studies/research-1900.md#54-what-batch-r0-needs-the-first-studys-q1-now-for-1900) |
  | R0f | Research: Money on Canada's dates - the D$ on gold to Aug 1914 and Jul 1926-1931, the Advance Rate opening the policy rate in Aug 1914, the Bank of Canada the rest of Money in Mar 1935 (`PolicyDates.POLICY_RATE`, `CENTRAL_BANK`); after GD2 and FM1 | [research-1900 §7.2](docs/studies/research-1900.md#72-the-recommendation), [GD0](docs/studies/spec-gold-money.md) |
