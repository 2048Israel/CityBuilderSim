# FS10 notes: the currency, the gold and the central bank cause their conditions

Batch FS10, built 2026-11-20 by Claude (Opus, headless; Sonnet slices one at
a time) on `batch/FS10` from origin/main 865a7171c (0.9.168), merged with
origin/main b2f27a7bd (MC1, RN1-H-M, CPB2, FS7, BI2, FS15, HL1-M, BI1). Brief:
`docs/studies/flaky-suite.md` and its table (`flaky-suite/by-section.md`, the
FS10 rows), HP1 item 1, MQ1, and PO1's Observations in these harnesses.
Harness-only: the diff is the ten harnesses (CurrencyRegime, GoldStandard,
Intervention, PassThrough, ExchangeRate, Foreign, Currency, InterestAbroad,
CentralBank, Monetary, each `...Check`), `Observation.java`'s list,
`docs/notes/known-reds.txt` and the generated maps. No model class is touched
and the trace does not move (`moves_playtest=no`); no ensemble is needed.

## The rows

All twenty FS10 rows of `docs/notes/known-reds.txt` go green and leave the
file. None is a model fault, so nothing is queued. Row numbers are the file's
at 865a7171c.

| row | harness, line | cause | change |
|---|---|---|---|
| 28 | GoldStandard 9: the policy rate is goldRate(...) as Game struck it | the cover read after the month, where Game struck the dial at its top before the gold's due and the edge settled (HP1 item 1) | the month's top made to move no M0 (fixture: nothing redeemed, no edge owed, the gold's due within the reserves), the dial asserted to 1e-12 at the cover read before the month |
| 66-71, 79, 97 | ExchangeRate 8b: the dated gold peg survives to the eve; the released hold; the return restores the peg and holds par | the played 1900 city's vault ran out before August 1914 (FLOAT, no reserves, one VAULT break) | the peg held solvent by dollars bought by name (`Tally.topUpTo`), the shadow gap set at the eve through the saved array, the vault bought for the return's sale |
| 72-75, 131 | PassThrough 3 and 4: the shelf's pass-through, the one-for-one horizon | the shelf sat on its opening-price floor on the played path, so a weaker D$ moved nothing; the horizon read over too few months | the shelf traced (`Retail.getStoreSellPrice()`: floor max(opening, basket cost x RETAIL_MARKUP)), its binding input cost caused by a dear goods' D$ (`DEAR = 2`, 12 of 12 months binding); the horizon read over months 0-119 |
| 109, 110 | CentralBank 10: the city carries a note and runs it off | the town of nine adults bought no paper | the town stood whole (`standTheTown`), its cells shopping (`assertTheCellsShop`); its note sells |
| 142-145 | CentralBank 7 and 22: a year of prices; the households and the bank each hold the line | one House standing to m97; the households held none | 7: the index restored with a year's rise (`YEAR_RISE = .10`); 22: the stood town's households hold $201k of the line |

## The harnesses

- **GoldStandardCheck** (7, 9; 10 kept). 9 as row 28. On the run the rate was
  0.109784050696, goldRate at the top's cover 0.600864. The month-end cover
  0.600626 would give 0.109843546642, which the law rejects. 7: the cover held
  at `HELD_COVER = 1` or over at every month's end to March 1933, by gold bought
  by name. *Fault lines:* `HELD_COVER = 6` tops up in 42 months, still green.
  With `GOLD_LOCAL = 400` the town comes off gold at world month 74 under both
  1 and 0, and "still on Gold" reads it.
- **ExchangeRateCheck** (6, 8, 8b; 7 and 9 kept).
  - 6: the market's draws checked against `MARKET_SIGMA` on a bare seeded float
    (`playMarket`, `SAMPLING_SES = 5`); another seed differs at its first draw
    and in every month.
  - 8: the volatility and run bounds read on a bare `still()` over 360 months
    (`SWING_LIMIT`, `RUN_CHANCE`); the played 360-month city is kept for the
    playtest-counts identity.
  - 8b: as rows 66-97. At the eve (m175): 0 breaks, topped up in 80 months for
    US$3,916k. August 1914 moves 1.0000 to 1.1524 (hold +0.176). July 1926: the
    float at 1.3360 returns to 1.0000; the lift sells US$311k against a year's
    GDP of US$1,027k.
  - *Fault lines:* the hold off turns three 8b lines red (return 4.3858 != 1);
    `RELEASED_GAP` at 0 reddens the hold line; a doubled sigma reads a
    volatility of 15.2%, outside the band.
- **PassThroughCheck** (2-6; 7 kept).
  - 2: the base city's gap set by `setStruckRate` (5%), a loaded copy pinned
    `PUSH_OFF` (8%) either side by turns.
  - 3: as rows 72-75. Groceries pass 14.4% of the step against the goods'
    28.7%.
  - 4: each twin given cash and a vault and floated; the sell-back equals what
    was bought plus its interest.
  - 6: pinned 5% off the goods' D$, the gap reads 4.86%.
- **CentralBankCheck** (7, 10, 20, 21, 22; 1 kept). The shared city has 62.1
  of 70.2 households in cells at or over `EMPTY_CELL`; 22's town has 67.0 of
  76.8.
  - 20: the serial sized from the surplus (`SERIAL_OF_SURPLUS = .10`), its
    three Observations hard.
  - 21: its five Observations go (★ below). The rule's halves are caused on a
    bare DebtManager (`strictRuleHalf`, `noSpiralHalf`) and a bare Expectations
    fed a 3.5% basket (`looseCostsTrust`: trust 0.652 against 0.849). The 24
    cities' gaps are printed as a finding: 24 of 24 negative, median -0.351.
- **CurrencyRegimeCheck** (0, 3, 5, 7, 9, 10, 12, 13; 11 kept). Its six
  Observations go.
  - 0: the defence caused on a bare Lean book in a deficit month.
  - 3: two `creditTown` cities. One pegged at `breakingGap` (0.807) breaks by
    VAULT while its twin at the shadow holds, and the dent law
    (`PEG_BREAK_DENT`) reads it.
  - 5, 12: hot money placed (`placeHotMoney`) and doubt set (`causedDoubtTown`).
  - 7: a year of prices restored (`aYearOfPrices`).
  - 9: the shadow struck under the peg (`pressedTown`, `billGap`).
  - 10: owed 238.5k at a doubt of 0.65%; it no longer borrows
    `FloatLinkCheck.worldWantsPaper`.
  - The seed, depot and climb searches (`doubtTown`, `heldTwin`,
    `climbMonth`, `surplusTown`, `surplusDepots`) are removed; nothing outside
    used them. `pegTown`'s climbing branch went with them.
- **ForeignCheck** (2, 5, 8 by the file's count, the table's 9; 3 and 4 kept).
  - 2: every month in which a cause stands is read. A bakery stands and the
    city sold abroad (140/140); a shop stands and it bought (170/170); a fuel
    bill struck and the forecourts bought PETROL abroad (137/137). The swamp is
    read in every year once the 500 houses stand (11/11), not the last one.
  - 5: the jam is the roadless build's (road ratio under 1 in 97 of 97 selling
    months). The restock bound is `Retail.STORE_COVER_MONTHS` x the last
    month's kilograms plus a basket, where it was a factor of 1.15, and a lower
    side is added.
  - 8: both Observations hard. The programme the model quoted is bought:
    28,944k and 28,340k against 22,609k. The first month a bakery stands, food
    is sold abroad. Beside them, `borderPrices(rate)` reads every shelf good's
    export and import price at the two pinned rates, one for one to 1e-9.
  - *Fault lines:* section 2's counts from month 1 fail (140/180, 178/180,
    137/180, 14/15). With 40 roads the jam binds in 0 of 56 months. Without
    the weak city's top-up it reads 18,022k against 22,609k.
- **CurrencyCheck** (4). The 500-house town plays `WARM_MONTHS = 12`. In month
  13 the shadow is put `FALL_PLACED` (8 x `MARKET_SIGMA`) under the rate
  through the saved array, and hot money is placed over its target of 0, so it
  leaves. The month sells US$62.18k of a US$681.61k vault on a US$82.57k
  deficit; the sale, equity and M0 laws read that month, and the 120 months on
  stay played.
  - *Fault lines:* without the hot money there is no deficit and no sale (2
    fixtures fail). With the shadow over the rate there is no sale. The
    shadow alone sizes the sale (US$10.17k without it).
- **InterventionCheck** (5). On bare books (`forcedDeficit()` through
  `CurrencyCheck.defended`), deficits at (1 -/+ 0.1) x `LINEAR /
  MAX_ABSORPTION` of a year's output sell 9.00% and 11.00% of it, each
  `MAX_ABSORPTION` x the deficit, one under the line and one over. "The lean
  sold in some month" reads the forced deficit's sale; the 240 played months
  stay the audit (dealt 0, none past the line), plus a new identity that no
  month's sale passes `MAX_ABSORPTION` x its deficit (0 of 240).
- **InterestAbroadCheck** (9). After the 24 played months (kept), the hot stock
  is set to `HOT_STOCK_SHARE = .5` of its target (D$59k of D$119k; the played
  D$77k). The world is calmed through its saved risk-off slot (`RISK_OFF_SLOT =
  7`, asserted to be `riskOff()`).
  - *Fault lines:* stock 0 fails the fixture. Stock 0 with the dial at
    `WORLD_BASE_RATE` fails four lines. The slot left at .6 reads a level of
    1.476 against 1.00.
- **MonetaryCheck** (7, 8: MQ1).
  - 7: the 120+12 months and the reload identity are kept. The premises are
    set before the save: a based basket put in by `PriceIndex.restore`, the
    world level by `WorldEconomy.shockLevel` (1.5), the rate by `pinRate`
    (1.0).
  - 8: see the MQ1 call below; its two Observations are hard. The D$ is held
    at the founding's rate (0.6949) in every run, and the rows are read over
    months 49-84. At 3/10/20/40% they are 11.50 / 10.30 / 9.11 / 9.10% (main's
    window: 20.27 / 17.76 / 15.42 / 14.80). The late rows differ by 0.000 /
    0.138 / 0.015 / 0.017 over the window, and by at most 0.203 / 0.105 /
    0.105 in its three years, against the unchanged allowance of 0.650. The
    spread is 2.40 points against the floor of 1.
  - *Fault lines:* with the pin off, the held fixture fails and so does the
    delay in each year (0.227 / 0.127 / **1.292**). A founding without the
    General Hospital has fewest crews 0.250, and the crews fixture fails. In
    the first build, one year's window, a window opened at month 45 straddled
    the solve and its repricing fixture failed.
- **Observation.HARNESSES** loses CurrencyRegimeCheck, MonetaryCheck,
  CentralBankCheck and ForeignCheck, which have no calls left. The merge with
  FS15 and FS7 took their AuctionCheck and HomeRailCheck out of the same list
  (one conflict, both sides kept). ObservationCheck passes.

Labels: kept wherever the law is the same, so every row's fragment still matches.
Changed: CurrencyRegime 3's "fixture: the doubt city reached its peg break" is now
"fixture: the city reached its peg break, by the vault its gap was struck to
empty" (no row names it). CentralBank 21's "strict policy median across cities"
and "strict policy share across cities" are replaced by "very strict holds
prices lower than Standard, the rule's half: ..." and its two follow-ons.
GoldStandard 9's label is extended after the row's fragment. InterventionCheck
5's fixture label has a new parenthesis.

## The calls (★)

- ★ HP1 item 1 by holding the cover still, not a getter for the cover Game
  struck with: a getter is a model change, and the brief is harness-only.
- ★ ExchangeRate 8b and GoldStandard 7: the dated pegs are held solvent by
  dollars or gold bought by name. The law is what the calendar does to a
  solvent peg, and the played vault running dry is the town's, not the law's.
- ★ CentralBank 21, design C: each half of the rule is caused on bare objects,
  and the 24 cities' sign is printed as a finding. Design A, both twins pinned,
  gave 11 of 24, median +0.001: the pin removes the channel. Design B, a shock
  given, gave a +10% rise 24/24, a -20% fall 23/24 and no shock 24/24, so the
  sign is the float's answer, not a premise.
- ★ CurrencyRegime 3: the break is caused by the vault, on a peg struck at a gap
  that empties it, where it waited 40 seeds for a break by doubt. Doubt's break
  is the market's chance, and the dent law is the same for either cause.
- ★ MQ1: the currency is held. Every run pins the D$ at the founding's rate in
  month 25, on time and a month late alike. The rows are read over three whole
  calendar years (`WINDOW_YEARS`, months 49-84), the 36 months the section
  read before FS10.
  - **First build:** one year between two solves (months 49-60). The
    orchestrator asked for the pin, because the delay premise still rested
    on the window: 73-84 read 1.292.
  - **The yearly step:** the shelf reprices every month at `CLEAR_SPEED`.
    The yearly step is `Game.strikeTheWorldMarket()` each January, fixed by
    the calendar, so a month's delay cannot move it. What it moved was the
    floating D$: by month 84 it is 2.57 on the 3% row and 1.78 on the 10%.
  - **Fixtures:** the D$ at the pinned rate in every month of every run;
    the window whole calendar years, with the market solved in each month's
    own year; the basket based before the window; the crews kept on; the
    delay inside the allowance over the window and in each year.
  - **The settle search is gone.**
  - **What the pin gives up:** the currency's channel, which carried most
    of the point while the D$ floated. That channel is CurrencyCheck 1's
    and PassThroughCheck's. Pinned, the domestic channels alone carry 2.40
    points over the three years, against the floor of 1.
  - **The first year alone does not fall:** 10.93 / 11.01 / 12.13 / 12.13.
    The second year (12.09 / 9.83 / 8.95 / 8.94) and the third (12.42 /
    10.19 / 6.30 / 6.29) do. The laws read the three years together.
  - **Not like CentralBank 21's design A,** where a pin destroyed the sign
    in 11 of 24 cities: here, over three years, the sign and the floor
    hold.
- ★ Some setters work by a slot index in a saved array:
  - CurrencyCheck 4 sets slots 78 and 79, the reprice's shadow. A fixture
    checks that slot 78 reads the log of the rate.
  - InterestAbroad 9 sets slot 7, the risk-off. A fixture checks that it reads
    `riskOff()`.
  - ExchangeRate 8b's `MARKET_SLOT`, `SHADOW_SLOT` and `HELD_SLOT` are checked
    only through what they move, the hold released at the gap set.
  - No model setter exists for any of these; adding one would be a model
    change.
- ★ ForeignCheck 5's restock bound is the model's (`STORE_COVER_MONTHS`), not
  the tuned 1.15.
- Kept played (KEEP): GoldStandard 10, ExchangeRate 7 and 9, ForeignCheck 3 and
  4, CurrencyRegime 11, PassThrough 7, CentralBank 1, the ten-year halves of
  CurrencyCheck 4 and InterventionCheck 5's 240 months.

## Findings (not faults, not queued)

- **The 1926 return onto a drifted float:** at par it needs years of GDP. Main's
  unheld float at 2.98 would make the return sell US$15,807k, about 16.6 years
  of the town's GDP.
- **A gold town at a full cover:** it rules out doubt, not a large single sale.
  At `GOLD_LOCAL = 400` the lowest cover was 1.48 and the town still came off
  gold at world month 74.
- **MQ1, floating, the delay premise was a window's:** in the years 49-60 and
  61-72 the late rows are at most 0.227 and 0.127, but in 73-84 they read
  1.292 on the 10% row (the town's growth from 469 to 566). Held, the three
  years read 0.203 / 0.105 / 0.105. FB1's account (a delay moves the
  repricings) is corrected: the solves are calendar-fixed, and the delay
  moves the currency.
- **The domestic channels lag a year:** with the D$ held, the first year
  read shows no fall with the dial (10.93 / 11.01 / 12.13 / 12.13%).
- **InterventionCheck 5:** `MAX_ABSORPTION` x the deficit is a necessary bound
  only. The played run's worst month's deficit is 41% of a year's output,
  against the line's 11.76%, and its largest sale is 3.79%, because the need
  (the fall) binds first.
- **ForeignCheck 8 never asserted "the balance better in the weaker city"**
  (only printed, since 2026-09-11). The goods markets carry no volume
  elasticity. The world's `SUPPLY_ELASTICITY + DEMAND_ELASTICITY` is .90,
  under 1; it is printed, not asserted.
- **CentralBank's stood town:** retail sells groceries to its households to m55
  and none from m58. This was not traced; no law reads it.

## The pick, the review and the checks

- **The pick:** `sh tools/pick.sh origin/main HEAD` picks 46 of 232. These are
  the ten harnesses and the harnesses that borrow them, plus every
  `Observation.HARNESSES` user (ObservationCheck and LongPlaytest among them),
  because `Observation.java` changed. Review: not needed (no money, saves or
  threading in the diff). No dry run asked for: the pick is not ALL.
- **Before the merges:** each harness was run directly on its own commit.
  - GoldStandard: green, 78 s.
  - ExchangeRate: green, 48 s.
  - PassThrough: green, 31.6 s.
  - CentralBank: green, 662 s and 8 min 55 s.
  - CurrencyRegime: green, 18 s.
  - Foreign: green, 14.9 s.
  - InterestAbroad: green, 8.6 s.
  - Currency: green, 8.6 s.
  - Intervention: green, 11.7 s.
  - Monetary: green, 12-21 s.
  - All on the shared laptop.
- **On 8be33ed99** (main b2f27a7bd merged):
  - `./batch-check.sh --fast`: exit 0, 0 firm Stale findings.
  - One name a call, each exit 0, suite OK. Times include each call's compile:
    - GoldStandard 70 s, CurrencyRegime 59 s, Intervention 54 s.
    - PassThrough 65 s, ExchangeRate 75 s, Foreign 61 s, Currency 46 s.
    - InterestAbroad 50 s, Monetary 54 s, ObservationCheck 32 s.
    - CentralBank 1,247 s, with the laptop shared.
- **On 09f31e10b** (main 3294dde04 merged: FS16 and FS13, harness-only):
  - `Observation.java` was the only conflict, resolved to main's list less
    FS10's four.
  - `./batch-check.sh --fast`: exit 0. ObservationCheck: exit 0.
  - The ten were not rerun: the merge moved no model class and none of their
    files.
- **The maps** were regenerated and committed with the merge (12526fe48).
- **The pin (406f99d74, the orchestrator's ask after the mends):**
  - The probe first ran outside the tree (`target/`): unpinned it
    reproduced the harness's rows to the digit.
  - MonetaryCheck: exit 0 directly. With the pin taken off it fails 2 lines,
    the held fixture and the delay in each year (0.227 / 0.127 / 1.292).
  - `./batch-check.sh MonetaryCheck`: exit 0, suite OK.
  - `./batch-check.sh --fast`: exit 0, 0 firm Stale findings.
  - The maps were regenerated and committed.
  - Main had not moved (3294dde04).
