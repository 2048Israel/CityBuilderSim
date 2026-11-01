# EP1 - the game starts in 1900: the calendar, the stand-in century and the price level ({next}, 2026-11-01)

Jerus, 2026-10-30: "move the start date and history to 1900 ... like a pre
research batch, along with the history ... 1900 start date with todays tech",
and on the price level, "1900 price level". The brief is `docs/queue/EP1.md`,
the study EP0's `docs/studies/spec-1900-start.md`, the licences LC1's
`docs/studies/licences.md`. A model batch: Opus, with three Sonnet slices (the
calendar, the price level, the harnesses the moved paths broke), each reviewed
and committed by Opus.

**This is the first cut the brief allows: the calendar and the price level.**
The world file is not rebuilt from 1900. Until it is (EP1b, queued, one
re-bake with LC3's and WD1's), its reader supplies the century before 2000 by
a rule (section 2). Nothing a player meets refers to a year the file lacks.

## 1. The calendar

- `CityCalendar.EPOCH_YEAR` = 1900: month 1 is January 1900, month 121 January
  1910. 1900 is not a leap year; the calendar was already Gregorian.
- Every reader was rescanned after WN4, XR4, CD8, XS1, LB1-LB3 and KP1 landed
  (the study's call 12): `Nations.FIRST_YEAR` stays the calendar's, and
  `WorldData.FIRST_YEAR` is it too, with `WorldData.FILE_FIRST_YEAR` = 2000 for
  the file format's own month 0. `PolicyRates` keeps its table's own first year
  (2000, `TABLE_FIRST_YEAR`), since EP1b rebuilds the world file but not that
  table. `WorldMoneys.parityConstant` averages the file's own years, not the
  stand-in's flat century. The founding screen's path words are built from the
  constants. `Game.getNations()` says plainly when a world file will not open
  and the city plays on without nations.
- **The save floor rises to 50** (call 10; `GameVersion` SAVE_FORMAT and
  OLDEST_LOADABLE_FORMAT together): four saved fields hold a calendar year, a
  Historical city's world belongs to another real year, and every saved dollar
  is 38 times its new world's. Old saves are refused, naming the build that
  opens them. No conversion.
- `Nations`' constructor now builds 201 years, not 101: on the full file 237 to
  250 ms cold (271 before), 11 to 18 ms warm; Random 17 to 19 ms (11 to 13).

## 2. The stand-in century (WorldData, THE STAND-IN CENTURY)

The world file starts in 2000. Its reader prepends the years before it, once,
at read, so every reader downstream sees a world that begins in 1900:
- a path series before its first figure holds that figure (people, real output,
  PPP output, the money's rate), except the two nominal ones, `cpi` and
  `gdp_usd`, which follow the US CPI (`PriceLevel.usCpi(y)/usCpi(y0)`): every
  nation's prices follow the US's and its money holds its rate, so purchasing
  power parity holds;
- each good's real price holds its first figure (call 4 (a): each good at its
  real ratio of 2000);
- each money's monthly rate holds its first month's, and each nation's money in
  2000-01 covers the century before;
- every figure made is marked an estimate; the file's own figures read as
  before at the same calendar year (WorldDataCheck section 9 proves each).

So on **Historical** a 1900 city meets today's real world (2000's) at 1900's
prices, its US$ inflating on the US's real path (call 3: the war doublings,
1921's fall, the Depression's, the 1940s and the 1970s), and from 2000 the
file's real years as before. The eight central banks run the game's rule before
their table begins (2000-01), reading their anchors' inflation, which is the US
CPI's in the century; a flat January 2000 rate (5.5% for the Fed) against a
century of -11% to +17% inflation gave real rates of -12% to +16% and killed
the carry. On **Random** the path starts from the same world in January 1900
and draws its own; the world market's base year (the trade table's 2019) sits
as far into Random's path as it did before (1919), so a Random world is
founded at the size it always was (`WorldMarket.baseYearRead`).

Tried and reverted (ffa31140, cab783f6): Random's people on the UN's path from
1900, as they ran from 2000, rather than held through the stand-in. It did not
move the ensemble's young cities (SHOCK's m720 median 15,970 against 19,154
without it) and its path met a map finding in the playtest and three more
harnesses' fixtures, so both paths hold people through the stand-in.

This is the study's hold-quantities way of call 5, for now: the real century
(people and output replayed, the moneys before the euro, the nations under
their 1900 ruler, the banks' real rates) is EP1b's.

## 3. The price level

`PriceLevel` holds the US CPI-U a year, 1900-2025 (BLS CUUR0000SA0 from 1913;
1900-1912 Rees's index as the BLS compiled it, HSUS series E 135, joined at
1913), and `FACTOR` = its 1900 over its 2025 = 8.333/321.943 = **0.02588,
1/38.6** (call 1), computed from the series. Every nominal anchor is today's
figure times it (call 2 (a)):
- **the funnels**: `PayTier`'s six rungs (the unskilled wage D$3,460 a month
  becomes D$89.56), the `Good` constructor (68 goods' import, export and base
  freight), `consumption.json`'s food prices, the building templates (cash
  cost, upkeep, consumables, the onshore cash a km; `BuildingsTemplate.
  pricedAtFoundingLevel`), tuition; `BuildingManager.MATERIALS_WORLD_PRICE` is
  now read from `Good.MATERIALS`, not a second literal;
- **the named literals**, each `figure * PriceLevel.FACTOR` at its declaration:
  Game's founding treasury and vault, issue cost, issue floor and the note and
  bond granules; Founding's presets and bounds; Bank's paid-in capital, capital
  scale and account fee; Healthcare's five fees; the two land bases and the
  land's price step; Retail's opening price; TaxPolicy's transit fare, its cap
  and the fuel duty's cap; Equity's founding price; Exchange's dealer equity;
  ForeignAccounts' least trade; CapitalFlows' least stock; the rental rebate's
  three amounts; Motoring's fuel; the utilities' water and power; site
  preparation's earth and walls; run works' deck and tunnel; the bridge's
  deck; Freight's world fuel; FundView's least recorded price;
- **not scaled**: rates, shares, ratios to a wage (they follow their rung: the
  minimum wage, the pension base, the fixed grant, the EI cap), the numeric
  floors and guards, display thresholds, the opening rate (D$1.00 a US$), and
  never an index (WorldEconomy, PriceIndex, Expectations, Denomination).
  `LabourMarket` (LD1's, beside this batch): no line changed, its minimum wage
  and bounds derive from the unskilled rung, and PriceLevelCheck asserts they
  are not scaled twice;
- **the world market** needed nothing: its calibration (sigma and phi to the
  literals at Halifax in 2025) absorbs the scale, as the study said; the twin
  confirms it;
- **the rankings' bridge** (call 12): a founding game dollar is a 1900 US$,
  `RealDollar.BRIDGE` = CPI 2015 over CPI 1900 = 28.4 dollars of 2015.

**PriceLevelCheck (new)** is the finding. Its twin runs three cities - the
default founding (60 months), a built city and MapCheck's played city (120
each) - in a child JVM at the factor and in one at today's prices
(`-Dcbs.priceLevel=1`, PriceLevel's one door), and requires every real series
equal and every nominal one equal over the factor within 1e-9 (a balance
against its own largest size: a treasury spent down to 34 of 100,000 carries
the rounding of the flows that spent it). Worst gaps: real 2e-11, nominal
1e-10. It found six anchors and roundings the factor had missed, each mended:
the land's US$5k price step (parcels priced at 0), the bond issue's floor of
50, the proceeds' rounding to a fixed hundredth (38 times coarser at the
factor), the granule bisection (it looped on a non-integral granule), six
history series kept to fixed decimals (now six significant figures), and
ranking ties an ulp apart at the factor (`LandMarket.RANK_TIE`: the farther
parcel won). The world's US level from a Historical founding to 2025 times the
factor is 1 within 0.3%: the world file's 2025 US CPI is an estimate 0.17%
over the BLS's published average (LC3's re-bake takes the BLS's own).

`ui/Money.unitPrice` (and LandClassView's copy) print six decimals under a
tenth of a cent: the smallest world price read $0.0003 (call 13).

## 4. The calls (spec-1900-start section 7), as taken

| call | taken |
|---|---|
| ★1 the factor | the US CPI's, 0.02588, computed from the series; the harness asserts it against the Historical path within 0.3% (the file's 2025 is an estimate), not exactly 1 |
| ★2 how it reaches | (a), one constant through the funnels and the named literals, with the twin |
| ★3 the world's dollar | yes: Historical's US$ follows the US CPI from 1900 (the stand-in's US cpi is PriceLevel's series chained to the file's 2000) |
| ★4 goods before 2000 | (a): each good at its real ratio of 2000 (the stand-in) |
| ★5 people and output | NOT the recommendation yet: held at 2000's (the study's alternative) until EP1b replays them; the world file is not rebuilt in this cut |
| ★6 moneys before 1999 | EP1b; the stand-in holds each nation's 2000 money and rate |
| ★7 the nations in 1900 | EP1b |
| ★8 the rates before their banks | EP1b; until then the game's rule before each table (section 2), not a flat 2000 rate |
| ★9 the world file from 1900 | EP1b, with LC3's and WD1's re-bake (one, not three); the release is Jerus's |
| ★10 old saves refused | yes, 50 |
| ★11 what may ship | per LC1: this cut ships one series, PriceLevel's: the BLS (US public domain) and HSUS E 135 (US public domain). The figures 1900-1912 are the study's, which came through the Minneapolis Fed's reprint (refused as a source by LC1); their reading against a copy of HSUS is pending (EP1-H) |
| ★12 the readers | rescanned (section 1); the bridge mapped to the series |
| ★13 the screens | the unit price fixed, the founding words from the constants; the screens by eye and the manual's "Month 1 is January 2000" pending (EP1-C, EP1-M) |

## 5. The harnesses

- **Re-premised to the constants** (slice A): CalendarCheck, WorldDataCheck
  (+ section 9, the stand-in), NationsCheck (+ a stand-in section),
  WorldMoneysCheck, InterestAbroadCheck (section 2 causes each of the policy
  rates' three stretches), WorldMarketCheck (the base year on both paths),
  SaveFileCheck, ForeignDebtCheck, RealDollarCheck.
- **Today's figures times the factor** (slice B, about 75 files): the study
  tables of the eight chain harnesses, the pinned building costs, the wage
  3.46, the Good prices, and the fixtures' dollar thresholds written for
  today's prices.
- **Fixtures caused again on the moved paths** (slices A and D; every Random
  city meets other draws, by decision; no draw re-keyed, no tolerance widened,
  no assertion removed): the copper and steel chains (CopperRefiningCheck,
  IndustryViewCheck, HaulageCheck, HomeRailCheck, PortCheck) play on the
  literals, since Random's 1900 draws put each works's export a hair under its
  inputs and FL1's order buys nothing it loses on; the money harnesses
  (FloatLinkCheck, RestructureCheck, BondCheck, CapitalFlowCheck, ForeignDebtCheck,
  BuybackCheck, HoldersCheck, HouseholdCheck, DeskCheck, MoneyCheck,
  CurrencyRegimeCheck, NewGameCheck) set the dial from the world's rate, quiet
  the world, hold the sector that borrowed their savers away, or found on
  Historical where the path is not their subject; TreasuryCheck's hands-off
  cities leave the bank's button unpressed (a default city's bank fails by
  itself in 4 of 12 Random seeds of 120 months here, 3 of 12 on main since LD1,
  0 of 4 before); MapCheck's played city is topped up to 12 months of its GDP.
- **One layout fault** the moved path exposed: `RefineryView`'s "who took it"
  heading fell 4 px above the picture when the takers' stack reached the top.

## 6. The checks

The full suite on the final tree (a15127f7, main merged in at 0.8.93 with TR1
and OV1), the world file named, 165 harnesses: **ScaleCheck alone red** (and
StaleCheck on README's count, which TR1 left at 164: mended, 165).
- WellCheck green: OV1 mended main's fault.
- ReplayCheck (TR1's): its trade tables' years were 2001-2010 literals; now the
  calendar's first year plus one, two, five and ten.
- **After the gate's review's mends (section 9), the full suite on the final
  tree is red on three harnesses**, the gate's own lines word for word. Main's
  record has only ScaleCheck's "exit 1" and "...and it builds: more homes at the
  end than at the start" (once, matched twice); every other line is new:

      CentralBankCheck | exit 1
      CentralBankCheck | very strict holds prices lower than Standard over the horizon      FAIL
      CentralBankCheck | 1 FAILED
      ScaleCheck | exit 1
      ScaleCheck | fixture: the city is growing - more people than a year before                        FAIL
      ScaleCheck | fixture: ...and its own rents price no migrant out in the months its copies are read against (the affordability pull 1 in all 6) FAIL
      ScaleCheck | ...and it builds: more homes at the end than at the start                            FAIL
      ScaleCheck | fixture: the free copy ordered - an order sized from more than a million needed, and one judged on its interest FAIL
      ScaleCheck | ...and it builds: more homes at the end than at the start                            FAIL
      ScaleCheck | fixture: the free copy ordered - an order sized from more than a million needed, and one judged on its interest FAIL
      ScaleCheck | ...cash: the copy is K times the city's within 0.0125 in all 6 months (worst 1.62e+00) FAIL
      ScaleCheck | ...savings: the copy is K times the city's within 0.0160 in all 6 months (worst 2.38e-02) FAIL
      ScaleCheck | ...deposits: the copy is K times the city's within 0.0260 in all 6 months (worst 9.73e-02) FAIL
      ScaleCheck | ...prices: the copy is equal to the city's within 0.00400 in all 6 months (worst 1.08e-02) FAIL
      ScaleCheck | 10 FAILED
      OrderSearchCheck | exit 1
      OrderSearchCheck | every trim and refusal in the long run is the countdown's, with its rate and its "at prime" FAIL  1 against 0
      OrderSearchCheck | 1 FAILED

  ScaleCheck's ten were red before the mend too (the fixture lines are the
  town's premise, not main's record: main's gate shows only the builds line);
  CentralBankCheck's and OrderSearchCheck's are new with the mend, and are the
  same draw: section 9.
- LongPlaytest green (no finding), the money audit clean every month.
- Timing soft beside other runs: SaveSlotCheck 1, MapCheck 2 (the gate times
  hard). Stale 0 firm.

## 7. The ensemble

80 runs, 2 at once, on EP1's model before main's TR1 and OV1 were merged in
(classes 48dc072e; both left main's playtest trace as it was and ran no
ensemble), against 0.8.91: **21/26** (0.8.91 23/26). `docs/ensemble/EP1.md`.

| id | EP1 | 0.8.91 |
|---|---|---|
| A2 longest spell of yoy > 10% from m240 | 20 [11 to 40] FAIL | 16 [12 to 32] FAIL |
| A3 deflation spell from m240 | 4 [0 to 12] FAIL (one seed 12) | 4 [0 to 11] PASS |
| S1 ZERO_SLACK over its twin | 2.46% FAIL | 2.59% FAIL |
| R2 HIGH's unemployment over its twin | +3.21 pts PASS | +0.38 FAIL |
| T1 seeds to 40,000 | 16/16, m1,148 [968 to 1,873] | 16/16, m1,033 [857 to 1,141] |
| T2 AUTO2 final | 292,358 PASS | 335,916 |
| T2 SHOCK at m720 | 19,154 FAIL (72% of 0.8.91's) | 26,598 |
| T2 HIGH at m720 | 9,374 FAIL (73%) | 12,758 |
| M1 money audit, F1 findings | 0, 0/16 | 0, 0/16 |

The young city starts slower: 40,000 people about 115 months later at the
median, and SHOCK's and HIGH's 720-month cities a quarter smaller. **The factor
is not the cause**: the same model run at today's prices (`JAVA_OPTS=
-Dcbs.priceLevel=1`, on ffa31140's classes beside its own run at the factor)
scores alike, 20/26 against 20/26, SHOCK 18,184 against 15,970, HIGH 9,734
against 8,904, T1 m1,124 against m1,139; seed by seed the two part within
decades by chaos (the twin holds them equal to 1e-9 for 120 months), the
distributions agree. What is left is the calendar's: Random's draws are keyed
by the calendar year, so the 16 seeds meet 16 other worlds (all three EP1 runs
share them), and the stand-in holds Random's people a century. Recommended
(EP1-E): accept, and read it again on EP1b, which moves every path again.

## 8. Found, for later

- LD1 raised a hands-off default city's chance of its bank failing within 120
  months (0 of 4 seeds before it, 3 of 12 after; 4 of 12 on EP1): a balance
  matter of LD1's, measured by slice D in TreasuryCheck's setting.
- Random's 1900 draws leave the default seed's copper and steel works a hair
  under water, so a young city's chains make nothing until prices move: the
  ensemble's cities read it in their own seeds.
- One in, one out: no process line changed (no script, check machinery or
  rule a session reads).

## 9. The gate's review

A stranger's review at the ship gate, on the tree of 6348862c (the ship file
included). Found, mended here, and left.

**Mended here.**
- **The audit's cent was still a cent of today's thousands.** `MoneyAudit.CENT`
  was .01 (and LongPlaytest's gate a literal .01): at the factor $10 of 1900,
  $386 of today, so every conservation check through `tolerance()` let by a leak
  38.6 times main's. Now `CENT = .01 * PriceLevel.FACTOR` (a twin at
  `-Dcbs.priceLevel=1` has the old cent exactly), and the relative part takes
  over from 260 million units, not ten billion (the banner says so). The
  harness cents of the same size were scaled the same way, each `.01` in an
  audit residual, a post-audit drift or a conserved amount now `MoneyAudit.CENT`
  (BankCheck, BondCheck, BuybackCheck, CentralBankCheck (and its two `.005`,
  now CENT / 2), CurrencyCheck, CurrencyRegimeCheck (two), ExchangeCheck,
  ExchangeRateCheck, ForeignDebtCheck (two), FundCheck (four), HoldersCheck,
  InvestCheck (two), LongPlaytest (two), MoneyCheck (three), MortgageCheck,
  NewGameCheck (two), ShortSellingCheck (two), SkipReportCheck (two),
  SlopeCheck). Every one of them is tighter or equal, none looser. The full
  suite on that tree: no audit, drift or conserved amount missed by more than
  the new cent. One scaled cent was wrong and went back: `HoldersCheck.CENT`
  (.0051) reads a figure from the audit's printed detail, which prints thousands
  to two places (`%,14.2f`), so it is the print's rounding and not a tolerance:
  scaled, four assertions failed on the printed 0.06 for 0.0575 (a step of $10 of
  1900 is the print's, not a leak). It stays .0051 with its sentence saying why.
  Not scaled, being no audit or conservation: ReloadCheck.CENT (.005, a reload
  equals the live city), ConservationCheck:375 (a reload), the 1e-5 cents of
  FreightViewCheck, LandlordPortCheck, PolicyPreviewCheck and SectorFlowCheck
  (already under the scaled cent).
- **PriceLevelCheck's hung child.** `Child.run` read the child's output to the
  end before it waited, so the ten-minute guard could never fire on a child that
  hangs. The output goes to a file under the check's `TempDirs` folder now; the
  wait has the timeout (`CHILD_MINUTES`), the child is killed on it and the line
  says so.

**The town that parted from its twin, traced** (the coordinator's brief after the
review: ScaleCheck's town at today's prices is main's two lines, at the factor
ten). PriceLevelCheck gained a fourth city, section 9: ScaleCheck's founded town
(the playtest's founding, forty houses, three stores, two farms, twenty houses
more after three months, a depot after seven, saved and loaded as ScaleCheck
loads it), month by month in a child at the factor and one at today's prices.
- **First parting: month 24, the D$.** Every figure equal to the bit-level of
  rounding to month 23; in month 24 the rate is 0.70774 over the factor against
  0.70764, 1.4e-4 apart, and nothing else has moved. The cause is
  `ForeignAccounts.totStep`, the second January's terms of trade
  (`Game.cityExportPriceLevel()`): it weights the world's prices by the year's
  exports by good, read from `PartnerTrade.history()`, whose tables are rounded
  when they go to the history to `HISTORY_ROUND` = 100 places - hundredths of a
  thousand, $10 at today's prices and $386 of today at the factor. A young city
  sells a good to a nation for a thousandth of a thousand, so those cells were
  zero or a step apart, the weights another's, the term 1.2% off. Mended:
  `PartnerTrade.HISTORY_ROUND_MONEY` = `HISTORY_ROUND / PriceLevel.FACTOR`, the
  money cells only (units keep 100); at factor 1 it is 100, main's. This is the
  class the twin found twice before (`Game.cents`, `LandMarket.RANK_TIE`) and
  the one the history's money series are tolerated for: here the model READS
  the rounded figure. The twin's fourth city fails the unmended tree
  (516 failing comparisons, all in section 9: cash, gdp.month, bank.deposits,
  the rate, the price index, the CROPS market, months 35 to 55) and passes the
  mended one (1,449 series over 56 months, 972 identical to the bit, every real
  series within 3.5e-13, every live nominal within 4.8e-12).
- **Second parting: month 56, a hair, not a literal.** With the first mended the
  town runs equal to month 55. In month 56 a company's shares (Freight, 2.5
  shares, the bank desk's excess) sell to households at the factor and do not at
  today's prices. The household cell 66 is the difference: its debt is 5e-18
  at today's prices (a hair there since month 3, 1.1e-16 then, most
  likely the tail of the shortfall waterfall in `Household.fundShortfall()`,
  positive by a rounding; not traced further) and exactly 0 at the factor, and `Exchange.mayBuy()` reads
  `!(c.debt > 0)`. It is not a price-level fault: the same town parts from
  today's at the same month at a price level of 0.5, 0.25, 0.1, 0.0259 and at
  0.999999 and 1.000001 (a millionth off 1 is enough), and at 1.000001 the
  founded town is today's again. A debt clamp of 1e-12 in `fundShortfall`
  (tried, not kept) moves the parting to month 72, an ordinary rounding tie
  again; the town cannot be held equal to its twin past a rounding, which is
  why the twin's fourth city stops at month 55 (TOWN_MONTHS, its sentence says so).
- **So ScaleCheck's section 6 is a draw.** The playtest's town at month 1124
  (FREE_FIXTURE_MONTHS; its own sentence records that no month holds every
  assertion, HD2 and EM2): at today's prices it fails main's two lines; at
  1.000001 the same two; at 0.999999 nine others (the affordability, GDP, cash,
  savings, deposits and the registers); at the factor the ten above. A price
  level a millionth off 1 is enough to change which lines fail. EP1 cannot
  mend it; ST2 (queued, "move FREE_FIXTURE_MONTHS to a month that holds every
  assertion") is the batch, and it must scan at the factor, since a month that
  holds at today's prices need not at the factor.
- **What the mend moved.** Mending the first parting changed the factor's
  trajectory, and two long-run premises that were green on the factor's draw
  without it fail on the draw with it: CentralBankCheck's "very strict holds
  prices lower than Standard over the horizon" (inflation over 240 months
  3.071% against Standard's 3.046%: a 0.025-point ordering) and OrderSearchCheck's
  "every trim and refusal in the long run is the countdown's" (1 against 0 of
  2,196: month 1226, Retail 94 Convenience Store, the search says 0 where the
  countdown finds 10, 22 desk calls against 85, the first rate the same). Both
  are green at today's prices and at 0.999999, 1.000001, 0.5, 0.25, 0.1, 0.0259,
  0.026, 0.0255, 0.03 and 0.02, and red only at 0.02588 and at the factor
  (0.0258834): the draw, again. OrderSearchCheck's is more than ordering: it is
  `Game.largestSlice()` assuming that if a slice passes then every smaller one
  does, and the order at 1226 passes at 10 but at none of the sizes the search
  tried; that window is in the model and not in the price level, and any
  trajectory can draw it (not traced to its cause). Recommended: ship, queue OS1 for the
  window (the bond plan or the yard's cost is where `passes(n)` stops being
  monotonic), and read CentralBankCheck's ordering as the draw it is.

**Left for follow-ups.**
- EP2 (queued): the thresholds that hide or fold a young city's figures at the
  factor (`TradeLanes.MIN_LANE`, `FundView.FOLD_UNDER`, the `>= .5` cut-offs on
  the Government and Finances screens, `ui/Money.java`'s $0 under .5) and
  NewGameCheck (c)'s single-gap bound. The audit's printed detail
  (`MoneyAudit`, `%,14.2f`) is in the same family: a 1900 city's lines print in
  steps of $10 of 1900.
- EP1b: three traps in the stand-in that the new file walks into
  (`WorldMarket.baseYearRead()` on Random, `pad()` and `usCpi()`, `PolicyRates`
  dropping the rule's state at month 1200), in `docs/queue/EP1b.md`.
- EP1-C carries the eye-check of the lanes, the folds and the $0 cut-offs.
- A grep of every model read of a rounded history (`HistorySave.round2` and
  `roundSig`, `PartnerTrade.HISTORY_ROUND`): `PartnerTrade` was found because
  `Game.cityExportPriceLevel()` reads it; whether another reader sits behind a
  display rounding is not known beyond what the twin's four cities reach.

## 10. The ensemble, rerun at the gate

**The final run, on EP1 merged with IC1 (895b5ff1 and BankCheck 19's hold, a harness; label EP1-final, 3 at once,
26.6 minutes): 23/26**, against 0.8.91's 23/26 and IC1's own 22/26. The fails are A2 (the longest spell of
yoy over 10%: 20 months [12 to 44], under 12 in every seed), S1 (ZERO_SLACK's longest spell 107 months) and R3
(HIGH, inflation m660-720 minus its twin: median -2.23%, 3 seeds above +1 point where 2 are allowed); main
fails A2, S1 and R2, and R2 (HIGH's unemployment against its twin) passes here. A3 and the two T2s that failed
before the merge pass. 40,000 people at m987 [860 to 1,475] against m1,033, 16/16 seeds. `docs/ensemble/EP1.md`
is this run's baseline.md. What follows is the run before the merge, kept for the record.

The 80 runs were made again on fbf4d323's classes (after the cent, the trade
history's rounding and the fourth city), 3 at once, 12 minutes, against
0.8.91: **21/26** again. HIGH's 720-month city passes now (it failed before the
mend) and ZERO_SLACK's final population fails (162,106 against 75% of
236,141); SHOCK's is 16,811 against 26,598. 40,000 people at m1,276
[872-2,012] at the median, 16/16 seeds (m1,148 before the mend, m1,033 on
0.8.91). Its file, docs/ensemble/EP1.md, is replaced by the final run's.

## 11. Fixed after Jerus's word

Jerus: "fix then ship". The two long-run reds that were not ST2's: OrderSearchCheck
is mended, CentralBankCheck is a draw and is left as it was. The full
`./batch-check.sh` after both (exit 4): red on CentralBankCheck (1 line) and
ScaleCheck (10 lines, ST2's town); OrderSearchCheck passes. The lookup was made lazy
after that run (an IntSupplier, behaviour the same); OrderSearchCheck, InvestCheck,
BuildCardCheck and PriceLevelCheck pass on the final code and ScaleCheck has the same ten
lines.

**OrderSearchCheck, OS1 (mended).** `Game.largestSlice()` returned 0 where the
countdown found 10, month 1226, Retail's 94 Convenience Stores. Replayed
(1,226 months, 20 s) and both searches printed for that order, till 18,817, a
building earning 31.7 a month, cost 4,395.6 a building:
- slices 1 to 4 are paid by the till and fail, tested on their whole cost at the
  sector's own rate (17.83%; 4 stores: earnings 126.8 against a hurdle of 168.5);
- slices 5 to 10 borrow what the bank and the bond desk can raise whole
  (`Plan.covers()`), a bond is part of it, and the slice is tested at the plan's
  blended rate, 14.0% against the loan's 17.9%: they pass (10: 317.1 against 245.9);
- slices 11 to 94 borrow past that room, `covers()` is false, the slice is tested
  at the loan's rate again and fails.
The doubling asked 79 (counted), then 78, 76, 72, 64, 48, 16 and 1: all failed, so
`pass` stayed 0 and `fail` reached 1, and the halving had nothing to halve. It is
the case Game's banner already named ("a slice the till pays for is judged at the
sector's own rate, while a bigger one that borrows may be judged at a bond's
cheaper one"), not an unscaled literal: the search holds only quantities, and the
pieces under its test are scaled (`FIXED_ISSUE_COST` = 12 x FACTOR, divided by the
unit; the room and the rates are ratios). At today's prices the same window needs
a bond cheaper than the loan by that much, which main's paths did not meet.
- *The mend* (Game.java:6023, `largestSlice(quantity, passes, borrowsFrom)`; the two-argument
  form stays, for the abstract tests): a fourth step, the first slice the till does
  not pay for (`Slices.firstThatBorrows()`, Game.java:6083, found by a
  run-from-one search on the cost against the till, no bond desk; 0 under a ban, and
  looked up only when the counted slices all failed), asked unless a probe already did, and if it passes, halving above it up to the
  lowest slice known to fail over it. A window that opens at the till's edge and runs
  to the bank's room is found whatever its width; one that opens later (a bond
  dearer than the loan for the first few slices that borrow) is not asked, as before:
  the banner says so. `deskCallsMost()` = COUNTDOWN_SLICES + 3 x bits + 2 (was
  + 2 x bits + 1: the one probe and the halving above it).
- *The proof* (OrderSearchCheck 5, `window()`, OrderSearchCheck.java:533): the
  playtest's city at month 500 asked a rule of `windowGate()` (the seam
  `Game.setOrderGate()`: the slices the room raises whole tested on what they borrow,
  which a rate that much cheaper makes of them; the city's own bidders price a bond
  at the loan's rate there, 12.96% against 12.88%, so a real bond window cannot be
  caused in it), a till of 2 or 4 slices, a room of 3, 6 or 9 past it, the profit of
  a building swept in 4% steps: 816 orders of 94, 207 of them windows (the countdown
  trims into a slice past the till's while the till's slices fail, the widest by
  9). Then 6,867 windows on tests of their own (six orders to 65,537, a till of 0 to
  40, a width of 1 to 40), and 68,850 runs from one with six first slices named.
  Before (the same check against the tree's Game.java with the new overload ignoring
  its third argument): `every order of the sweep is the countdown's ... FAIL  207
  against 0` and `the search finds the window's last slice, the countdown's, in every
  one FAIL  5,176 against 0` (the first that differs: till 2, room 3, a building
  earning 0.8059: quantity 0 and 22 desk calls against the count's 3 and 92). After:
  both `OK  0 against 0`. The whole OrderSearchCheck passes ("All checks passed."): the
  long run judges 2,193 orders (2,196 before: the path moves from 1226, where the
  order built 10 instead of being refused), 19 trimmed against 18, 0 differing.
- *Left*: the mend moves the playtest wherever an order was refused that the
  countdown would have trimmed, and so the ensemble's path from there; the 21/26 of
  section 10 was run before it.

**CentralBankCheck (a draw; the check is as it was).** "Very strict holds prices
lower than Standard over the horizon" fails by 0.025 of a point (3.071% a year
against 3.046%) on the factor's path. Read first: the section plays the probe city
(the playtest's founding in seed 7's shape, to month 600) and from one save the
three ends for 240 months each; the claim is the order of two twenty-year
inflations. Looked for, and not found: a threshold or an unscaled literal in the
rule or its policy (the twins' rule is the city's own DebtManager autopilot, in rates and
ratios; PolicyRates feeds the US's rate to worldRate() by a rule of the same kind - a
neutral real rate, TAYLOR_WEIGHT, LOWER_BOUND, STEP, no money literal - and both ends
read the one world) or a feedback through output that is the factor's.
- *The gap at each price level* (very strict less Standard, points a year, 240 months, the full
  section re-run in a scratch driver, `-Dcbs.priceLevel`): 1 -0.265; 0.5 -0.268;
  0.25 -0.205; 0.1 -0.337; 0.03 -0.165; 0.028 -0.233; 0.027 -0.393; 0.026 -0.502;
  0.0259 -0.167; 0.0255 -0.262; 0.025 -0.404; 0.02 -0.220; 0.999999 -0.155;
  1.000001 -0.301; 0.0258 -0.250; 0.02589 -0.229; 0.025884 -0.424; 0.0258835
  -0.310; 0.02588347 -0.334; 0.0258834 -0.870; 0.025883464 (6e-9 over the factor)
  -0.499; 0.02588346385 (3e-10 over) -0.350. Positive: 0.02588 +0.149 and the
  factor 0.0258834638 +0.025. 22 of 24 hold it, by 0.15 to 0.87 of a point; the
  sign changes between two levels 3e-10 apart.
- *The same at the factor, other founding shapes* (houses 40 to 46 before the next
  twenty, 4 or 5 months): 11 of 12 hold it (-0.13 to -1.97); 46 houses and 4 months is
  +0.29.
- *The path is bimodal.* At 1, 0.5, 0.1, 0.02 and 0.0255 the twins are near-equal
  (9,654 to 9,670 people, the dial at most 13.4% to 13.9%, inflation's sd 2.6 to 2.7
  points): a model that does not know its price level, over a 50-fold range. The
  other levels fall onto other paths by a rounding tie (0.0259, 0.026 and 0.03 share
  one: sd 3.38, min -8.4%), as ScaleCheck's town does (section 9). The factor's is a
  wilder one: very strict's inflation sd 3.34 against Standard's 2.54, the dial 24.98%
  against 15.38%, and a late burst, the last year 3.39% against 0.65%.
- *Where the two part*: the rule differs from month 1 (the dial 6.31% against 4.73%);
  the indexes part at month 2 (0.99999) and very strict's is first above Standard's
  at month 25, then 0.944 at month 72 (Standard at 8-15% dial), 1.003 at 96, 0.962
  at 144, 1.063 at 168, 0.971 at 216 and 1.005 at 240 (the last two years the dial
  12-15% against 10%). At 0.03 it first passes at 131, at 1.000001 never.
- *So*: a draw, in the sense of ScaleCheck's town: the premise holds in the general
  case and the factor's trajectory is one that does not. Not changed: the assertion,
  the horizon, the fixture. If Jerus would have it green, the probe city's shape is
  re-picked (the banner of `probeCity()` has done it several times for this reason);
  that is a batch's work at the factor, and is not done here.

### Round two: EP1 merged with IC1 (895b5ff1)

IC1 was at its gate (34e1f1f3, waiting on a timing line), so EP1's last checks were run on EP1 merged with it,
unmerged into main.
- *The merge.* Twelve conflicts: eleven generated files (docs/dials.md, docs/harnesses.md, docs/month-order.md,
  docs/map/{AutoBuildCheck,BankCheck,BuildingManager,CapitalFlowCheck,Game,LabourCheck,MapCheck,README}.md) took EP1's side
  and batch-check regenerated them; the twelfth was LabourCheck's wagesAgainstTheIndex, where EP1 scaled the treasury line
  (`spend(-2_000_000 * PriceLevel.FACTOR)`) and IC1 held every sector (`holdSector`) with its comment and left the line
  at `-2_000_000`: both kept, IC1's hold and comment and EP1's scaled line. No model code conflicted.
- *CentralBankCheck 21* is green on the merged tree without a change (very strict 2.139% a year against Standard's 2.635%,
  the probe city at 13,531 people at month 600): IC1 moves the probe city's path, and the order is the one the
  22-of-24 price levels and 11-of-12 shapes said it was. Nothing was re-picked, so the header's list is as it was.
- *The full `./batch-check.sh` (exit 4)* freed the ScaleCheck lines IC1 mended: the copies' comparisons (cash, savings,
  deposits, prices) and the free copy's conservation are green now. Left of ScaleCheck: the growth fixture (the town at
  month 1126 has 14,576 people and is not more than a year before), the rents fixture, "it builds" twice and "the free copy
  ordered", which is ST2 (FREE_FIXTURE_MONTHS must be a month that holds every assertion, scanned at the factor on IC1's
  rule): IC1's rule frees the stall at today's prices, not at this draw. The two new reds the merge brought:
  - *BankCheck (19), the branches by their customers: mended in the fixture.* In that town (400 houses, a dozen shops, a
    bakery, a depot, a coal plant) the bank is in resolution from month 34 at every price level, today's included: the desk's
    shares of Manufacturing go from 0.184 to 0.005 in the month, the desk marks 3,431 off 3,898, equity 2,500 to -1,269
    (96,587 to -49,028 at factor 1), and `Bank.branchesToClose()` is 0 in resolution, so the five branches the fixture
    stands were not closed. EP1's 1900 world and IC1's freed planners together; either alone passed its gate's suite. The fixture holds
    Manufacturing out (`holdSector`, with its sentence) as IC1 held Freight and Copper Refining in the fixture above: the
    bank stands (equity 733 at month 34) and the whole of BankCheck passes. This is the one change beyond the brief and
    is a premise (the bank standing is the fixture's, not the claim's); it is dropped by deleting the block added after `town.run()` in `theBranchesByTheirCustomers()`.
  - *PriceLevelCheck 8, the twin on the played city (MapCheck's, 120 months): not mended.* The two children part at month
    106 (80 series at once; the people, workforce, homes and stores equal to the person; the demand for drinks 52,603 against
    52,655 with the prices before it equal to 1e-15): a discrete event, the class section 9
    found in the town (a rounding tie, `Exchange.mayBuy()`'s `!(c.debt > 0)` and the like). The same city against today's at
    eight other levels: 0.25, 0.1, 0.999999 and 0.0259 equal to month 120, 0.5, 1.000001, 0.03 and 0.02588 part at month 60
    (Construction's cash, 28,537.44 against 28,537.52), so which month a level parts is the draw's, as with ScaleCheck's
    town. IC1 changed MapCheck's city and the path. Sections 6, 7 and 9 and the history's constants pass. The mend the town
    got is a horizon (TOWN_MONTHS): PLAYED_MONTHS to 105 with its sentence, which would pass; not done here, because it takes
    coverage off a check.
- *The suite as the gate will see it* (target/checks/reds.txt after BankCheck's hold; BankCheck passes alone, its lines gone):

  | line | main's or new |
  |---|---|
  | `ScaleCheck \| exit 1` | main's |
  | `ScaleCheck \| ...and it builds: more homes at the end than at the start  FAIL` (twice: the copies at 5 and 10 billion; main's record has it once) | one main's, one new |
  | `ScaleCheck \| fixture: the city is growing - more people than a year before  FAIL` | new |
  | `ScaleCheck \| fixture: ...and its own rents price no migrant out in the months its copies are read against (the affordability pull 1 in all 6)  FAIL` | new |
  | `ScaleCheck \| fixture: the free copy ordered - an order sized from more than a million needed, and one judged on its interest  FAIL` | new |
  | `ScaleCheck \| 5 FAILED` (main's: 2 FAILED) | new |
  | `PriceLevelCheck \| exit 1` | new |
  | `PriceLevelCheck \| every real series equal within 1.0E-9 (worst 1.0 in h.fxRate)  FAIL` | new |
  | `PriceLevelCheck \| the history's 103 money series equal over the factor to the rounding they were recorded at  FAIL` | new |
  | `PriceLevelCheck \| every live nominal series equal over the factor within 1.0E-9 (worst 0.0022 in sector.Luxury Retail.cash)  FAIL` | new |
  | `PriceLevelCheck \| 358 FAILED` | new |
  | `PriceLevelCheck \| FAIL <series> month N: ...` (24 lines, months 106 to 120 of the played city) | new |

  Stale: 0 firm findings. The ensemble is section 10's final run.
