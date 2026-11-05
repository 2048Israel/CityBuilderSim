# R0 - the Policy levers on history's dates (0.9.12, 2026-11-04)

The first implementation batch of research from 1900: its row is "the 1900
economy and the calendar from 1900" (`docs/studies/research-1900.md` §5.4,
§7, §12; `research-design.md` §9). Jerus took every call of the study as
recommended (2026-10-18, "rec all"). Built on CM1 (lane 1's predecessor,
unmerged at the start).

## WHERE IT STANDS

(Kept at the top; a session resuming from GitHub starts here.)

- **2026-11-04 evening: built, the suite read.** CM1 is on main (0.9.11) and merged in. What is built:
  - **Slices 1, 2a, 2b** (the morning): `PolicyDates` (21 levers, 12 dated); the gate on the eight levers `TaxPolicy.GATED` holds
    (`setMonth()` holds a shut lever's dials at 0, its setters refuse, the openings saved as `DataSave.leverOpenings`); the minimum
    wage's floor unbound to 1918 (call 12), student loans shut to 1939 (14), the two readers of call 15; the inbox's notice on the month.
  - **Call 16** (a685d5d3): the three income taxes open at `DEFAULT_INCOME_TAX`, the promises at zero (`TaxPolicy.held`, the levers
    held at the last month told; emptied by a restore, `openAll()` and `reset()`; the load now tells the month after the policy's
    restore, so a city saved the month before a date keeps its opening). The notice says where each lever opens.
  - **Slice 3** (198a6cbc): the Policy screen draws a shut lever shut - a card "Opens 1917 - an income tax: 4% above $1,500 ..." and
    where it opens (`PolicyDates.shutLine` / `shutWhy`, the city's own opening month), its dial greyed - on Taxes (profit, sales, wage;
    "every tax" names the shut ones), Wages, Pensions (two dials, two dates), Out of work, Health (the premium), Schools (student aid)
    and Zoning; **zoning's gate** (call 13): `Game.setZone` refused to 1924, the forest law open (call 19), "Zoning: 1924" in the
    inbox; `TaxPolicy.openNow(lever)` for a one-lever fixture. Checked by eye only, not yet looked at.
  - **The suite's reds, read by call 7** (c5a2f25b; section 5): 27 red on call 16's model. 23 were fixtures, opened (a lever's
    premise, call 7; a condition the untaxed 1900 city does not give, every lever opened, call 18); ReplayCheck was load (green
    alone); IndustryViewCheck was main's old-metal line running past its slot, mended (call 22). **Two stay red, both main's latent
    faults that R0's city reaches:** SlopeCheck 9 (DM2's map fault, m612: call 20) and PriceLevelCheck 8 (the farm retirement's unit
    mix, a missed price at month 47: call 21; a mend was measured, moved six other harnesses' cities, and was reverted and queued
    as RT1).
- **`--pick` (80161740):** 85 of 171 harnesses, LongPlaytest among them, exit 4 on those two alone; the maps regenerated, Stale 0
  firm, line endings clean.
- **The ensemble** (80161740, 80 of 80 runs, 21 min at 2 at once; `docs/ensemble/R0.md`, against 0.9.6.md): 24 of 26 criteria
  pass, the same two failing as on 0.9.6 (A2, the longest spell of inflation over 10% from m240: 27 months [12 to 36] against
  0.9.6's 14 [7 to 29]; S1, ZERO_SLACK's spell 107 months against 102), and HIGH's R2 and R3, red on 0.9.6, now pass. People
  (T1): every AUTO2 seed reaches 40,000, at m1,038 [856 to 1,134] against 0.9.6's m970 [849 to 1,459]; the final population
  323,765 [265,797 to 423,013] against 335,674 (T2, 96%). No run has a playtest finding (F1), no money-audit month failed (M1).
  Call 16 holds: the untaxed decades cost the city about six years to 40,000, where opening at zero left the playtest a quarter
  of its people at m1,050 (call 16's table). A2's longer
  spell is the one criterion that moved the wrong way: the 1916-20 openings, a 15% tax arriving on an economy that grew without
  it, are a candidate cause, not traced.
- **Next:** calls 20 and 21 (pass the two reds by name at R0's gate, or hold R0 for DM2 and RT1). Once they are passed, the ship
  file goes up as section 4 drafts it (its `## pending` R0-S and R0-P, its `## queued` RT1 and R0d-f), and the screen is for the
  eye: a 1900 city's Policy tab, each shut page's card and greyed dial, and the card gone the month its lever opens.

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
    opening notice, and the ensemble's people criteria re-read. Taken as
    recommended by the orchestrator (2026-11-04) and built (a685d5d3); Jerus
    may overturn it.
17. **★ SaveFileCheck's `city` and `early` fixtures** set no lever, but their
    condition (something mid-build at the save; the index based in its six
    months) failed because a 1900 city with no taxes and no floor builds
    faster (the 20 depots finished inside a month). Slice 2b opened every
    lever in them. Recommended: keep - their premise is the save of a
    mid-build city, and the open levers put back the city that causes it; the
    alternative is a fixture that causes mid-build directly.
18. **★ Fixtures whose condition the untaxed 1900 city does not give** (the evening's suite; call 17's class, extended).
    Twenty of the suite's reds were a fixture's condition - a "fixture:" line, or what a fixture sets up for its real assertions (a
    town whose automatic builder orders, twins that stand alike, a holding steady through the measured month) - that a 1900 city with
    no income taxes to 1916-20 does not give, in harnesses that are not about the levers. Each fixture opens every lever
    (`reset()` then `openAll()`, before a month), its comment saying what it needs; what the 1900 city does instead is in section 5.
    Recommended and taken: keep - the fixture causes its condition (README, The checks), the playtest, the ensemble and the ~140
    harnesses left on history's dates keep the real path checked. The alternative is a fixture per harness that causes its condition
    in a 1900 city directly: larger, harness by harness.
19. **★ Zoning's gate is the zones, not the forest law.** Canada's Dominion Forest Reserves date from 1906 and the study gives the law
    no date; a zone's tax row is the sector's own offset, already held by its tax's lever. Taken as recommended (slice 3).
20. **★ SlopeCheck 9's red is DM2's.** Its layout watcher (`LongPlaytest.layoutCompare`) finds "a building on the map moved" once, at
    m612: district (-1,0), 58 of its 145 boxes moved with the district's carry log unchanged (2 Studio Apartments arrived). DM2, queued,
    is this fault (the wall ways; it names "SlopeCheck 9's m608-609 jump after a land purchase" and the playtest's m3010-3013, LD2-D);
    R0 touches no map code, and its 1900 city reaches the fault inside the watcher's window (months 601-613), which main's city does not.
    **Recommended:** pass it by name at R0's gate, as LD2-D was passed, DM2 mending it; the alternative is R0 waiting for DM2.
    Needs the orchestrator's or Jerus's word: `--pick` is red on it (exit 4), so no ship file is up until then.
21. **★ PriceLevelCheck 8's red is main's farm retirement, which R0's city first reaches.** `BusinessInvestment.planRetirement()`
    divides what is spare by what one building is (`Sector.unitsOf()`); Agriculture measures what is spare in money (each good at its
    export floor, `retirementDemandAndCapacity()`) but a farm in tonnes, so at the founding's price level (the factor, 0.0259) the
    quotient is 0 and no farm has retired in any city since EP1, while at today's prices one does. R0's untaxed 1900 city is the first
    played city with six loss months and a spare farm: its twins part at month 47 on a farm sold at today's prices and kept at the
    factor, which the 1e-12 nudge does not move (both sides run equal to their nudged twins past month 79) - by the harness's own rule,
    a price the factor missed. **Measured and taken out again:** a farm counted in the same money (`Agriculture.unitsOf()`, 519f8e41)
    parts the twins only at month 78 by a tie, but the farms it retires at the factor move six other harnesses' cities (MapCheck's yard
    and reserve fixtures, FoundingYardCheck's village, ShortSellingCheck, MonetaryCheck, ScaleCheck, and the playtest's map watcher at
    m1204-1209): a change to how every city plays, larger than R0. Reverted (824ee04b) and queued as **RT1** (`FoodProcessing` has the
    same unit mix: money against kilograms). **Recommended:** pass PriceLevelCheck 8 by name at R0's gate until RT1 mends it; the
    alternative is RT1 inside R0, with its ripple read first.
22. **★ IndustryPicture's old-metal line** said its name twice ("Old steel / the city's old steel · 1,338 t") and ran 2 px past its
    178 px slot once a yard took 1,000 t (IndustryViewCheck's frame bound, which R0's busier yard first reached); it now reads "the
    city's own". A screen's words, mended here; taken.

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

- Ship file: not up. CM1 is on main (0.9.11) and merged in, but `--pick` is red on two lines that are not main's recorded reds
  (calls 20 and 21). It goes up as below once they are passed by name, as LD2-D was. Its `## pending` (no eye-check row):

  | R0-S | SlopeCheck 9's layout watcher on R0's city (0.9.12): "a building on the map moved" once, m612, district (-1,0), 58 of its 145 boxes with the carry log unchanged - DM2's fault (the wall ways), which the 1900 city reaches inside the watcher's window; passed by name at R0's gate | DM2 mends it | [R0 notes](docs/queue/R0-notes.md) ★20 |
  | R0-P | PriceLevelCheck 8 on R0's city (0.9.12): the played twins part at month 47 on a farm retired at today's prices and never at the founding's (the 1e-12 nudge does not move it: a missed price); passed by name at R0's gate | RT1 mends it | [R0 notes](docs/queue/R0-notes.md) ★21 |

  Its `## changelog` paragraph:

  > A city founded in January 1900 now has each Policy lever on the date Canada first had it (`PolicyDates`): the profit tax
  > 1916, the income tax 1917, the minimum wage's floor 1918, the sales tax 1920, zoning 1924, old-age pensions 1927, student aid
  > 1939, EI in August 1940, the health premium 1947, the pension's contribution 1966; the property tax, fees, fares, food
  > assistance and the rest are open from founding. Until its date a lever is shut and refuses a setting, and the Policy screen
  > draws it shut with when it opens; on its month the inbox gives Canada's first terms and the lever opens - the three income
  > taxes at the founding rate (15%), the promises at zero, the wage floor at the going unskilled wage. Each city's openings are
  > saved (R3's law finds will move them); a save from before R0 loads with every lever open. Harness fixtures whose premise is a
  > lever, or whose condition the untaxed 1900 city does not give, open them; the playtest runs on history's dates.

  Its `## queued` rows: RT1, decided here (call 21), and R0d-f (call 1):

  | RT1 | A building counted in its sector's retirement measure: Agriculture's and FoodProcessing's retirement measure what is spare in money and divide it by a building's tonnes or kilograms (`Sector.unitsOf()`), so at the founding's price level no farm or food plant retires; count one in the same money (R0's trial, 519f8e41, then the ripple read: MapCheck's yard and reserve fixtures, FoundingYardCheck, ShortSellingCheck, MonetaryCheck, ScaleCheck, the playtest's map watcher at m1204-1209), mending PriceLevelCheck 8 (R0-P) | [R0 notes ★21](docs/queue/R0-notes.md) |

  | R0d | Research: the 1901 wage ladder and the arrivals' skill mix by year - each `PayTier` rung's 1901 comparator, sourced or flagged est. (research-1900 §5.2, §12.1), switched on with R1's 1900 recipes (R0-notes call 2); arrivals' diplomas from the year's mix (1.2 university students per 1,000 in 1901, 4.7 in 1950; nurses 0.05 to 3) | [R0-notes §1-2](docs/queue/R0-notes.md), [research-1900 §5.2, §5.4](docs/studies/research-1900.md#52-wages) |
  | R0e | Research: the household's basket by year - 1900's raw staples, lamp oil and coal, no car (after R0c's goods, and on the live demand path, not the shadow basket) | [research-1900 §4, §5.4](docs/studies/research-1900.md#54-what-batch-r0-needs-the-first-studys-q1-now-for-1900) |
  | R0f | Research: Money on Canada's dates - the D$ on gold to Aug 1914 and Jul 1926-1931, the Advance Rate opening the policy rate in Aug 1914, the Bank of Canada the rest of Money in Mar 1935 (`PolicyDates.POLICY_RATE`, `CENTRAL_BANK`); after GD2 and FM1 | [research-1900 §7.2](docs/studies/research-1900.md#72-the-recommendation), [GD0](docs/studies/spec-gold-money.md) |

## 5. The suite's reds, read by call 7 (2026-11-04 evening)

The full suite on call 16's model (CM1's tip merged): 27 red harnesses, 190 NEW lines. Each read at its cause:

| class | harnesses | what the fixture now does |
|---|---|---|
| a lever's premise (call 7) | InvestCheck (the builders' sales-tax quote, the landlords' rebate), CapitalFlowCheck's coupon city (the profit tax), ForeignDebtCheck 10 (its sales sized by the city's tax: `tradingCityTaxed`, as `tradingCity` is three other harnesses'), ZoningViewCheck (zones, and their profit and sales rows), ForestryCheck's `forestTown` and LandmassCheck (zones) | that lever, or every lever, opened |
| a condition the 1900 city does not give (call 18) | EquityCheck, FundCheck, HoldersCheck (4 fixtures), CapitalFlowCheck's hot-money city, MonetaryCheck, BondCheck's `city()`, FloatLinkCheck's `playedCity` (DeskCheck's too), BuildAdviceCheck's `shortCity`, RoadCheck's `town()`, ScaleCheck, AutoBuildCheck's `youngCity`, the shared `ChildcareCheck.town` (AutoBuildCheck, AuctionCheck, HydroCheck) and `DeathCareFirstCheck.city` (BuildersFirstCheck, ConstructionTrapCheck), TradeCostCheck, RailDepotCheck, HomeRailCheck's `railed()`, CopperRefiningCheck 4 | every lever opened, the defaults put back |
| main's latent fault, mended (call 22) | IndustryViewCheck (the old-metal line) | the words mended |
| main's latent fault, left (calls 20, 21) | SlopeCheck 9 (DM2), PriceLevelCheck 8 (the farm retirement: RT1) | nothing |
| load, not R0 | ReplayCheck (CM1's run under the suite's load: green alone) | nothing |

What the 1900 city does instead, from the logs (the findings; the ensemble measures the playtest):
- a young town's automatic builder ordered nothing in 24-36 months (`ChildcareCheck.town`, HydroCheck's 0 orders);
  AutoBuildCheck's young city had 1,154 people at month 60 where its note gives 2,618 before R0;
- ScaleCheck's playtest town at month 722 (1960): 4,500 people in 6,042 places, no more than a year before, no home built;
- the builders' site output climbs 44 to 90 over the first six months, where DeathCareFirstCheck's city was sized on 76 by month 6
  (the first depot opens in month 8 against a bound of 7);
- a month's tax is the property tax's alone (D$10k in ForeignDebtCheck's trading city), so a sale sized by it quotes cheaper abroad;
- a 90-month town's freight is 83% bulk and 17% commuters (TradeCostCheck: "mostly people" before R0);
- the households sell paper to the desk inside a month, the businesses buy none of a two-year bond (HoldersCheck), the fund's rule
  buys bank shares in two months (FundCheck), a sliver of Retail's shares sits on the desk (EquityCheck);
- RoadCheck's busy street carries 328 vehicles a day on a gravel road, which stays the least over its life (paved before R0).
