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
  never an index (WorldEconomy, PriceIndex, Expectations, Denomination);
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

{suite}

## 7. The ensemble

{ensemble}

## 8. Found, for later

- LD1 raised a hands-off default city's chance of its bank failing within 120
  months (0 of 4 seeds before it, 3 of 12 after; 4 of 12 on EP1): a balance
  matter of LD1's, measured by slice D in TreasuryCheck's setting.
- Random's 1900 draws leave the default seed's copper and steel works a hair
  under water, so a young city's chains make nothing until prices move: the
  ensemble's cities read it in their own seeds.
- One in, one out: no process line changed (no script, check machinery or
  rule a session reads).
