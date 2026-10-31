# WN4 - the moneys: each nation's money and rate, the vault in eight, and the real US$ ({next}, 2026-10-31)

*The batch's record. The briefs are `docs/studies/spec-nations.md` §5 and §9's WN4 row with D6, D20 and D29, and
`docs/studies/spec-exchange-rates.md` §3.5, §3.7 and §3.9, XR3 folded in (Jerus, 2026-10-21, ★10 and ★11). The first
batch of lane A on the night of 2026-10-30, built on main 0.8.78. Opus wrote the briefs, reviewed each diff and
committed; four Sonnet 5.5 slices at xhigh wrote the code (A-D, one at a time); Opus found and mended the D$'s terms
of trade (5).*

## 1. What it is

- **`WorldMoneys`** (new, model): every money of the world against the US$, a month at a time, on the study's rule
  (§3.7): `ln rate = its PPP path + b x the dollar cycle + r x the risk-off + its terms of trade + its own swing`.
  - **The PPP path**: the anchor nation's monthly price level against the US's (WN1's yearly levels, log-linear
    between Januaries), anchored on the money's mean real rate over 2000-2025, so the strong dollar of 2000-2001 is
    a gap the swings close, not parity.
  - **The dollar cycle**: one AR(1) for the world, 1.6% a month, half-life five years. **The risk-off**: about once
    in eight years, a size drawn in [0.5, 1.5], fading by half in three months; the yen -8%, the franc -5%, the euro
    +3%, the pound +6%, the Canadian dollar +7%, the Australian +14%, an emerging float +20%.
  - **The terms of trade** from each January's solve (`WorldMarket.lnTermsOfTrade()`: the export price index over
    the import price index against the base year, 2024), passed at half the nation's commodity share of exports
    (`Base.commodityShare`: the bulk class's share of its exports), a third in each of January to March.
  - **Its own swing**: AR(1), half-life 3.5 years, by kind (the euro 2.0% a month, the yen 2.3%, the pound 1.9%,
    the franc 2.1%, the Canadian dollar 2.1% with a cycle loading of 0.6, the Australian 2.8%, the yuan 1.0% with
    0.3, another advanced float 2.2%, an emerging one 4.0%).
  - **Pegs** held where the file says so (41 moneys from the start); a soft one breaks by the D$'s own rule
    (`ForeignAccounts.hazard()` on its shadow, the overshoot into its swing) and floats from then; the four boards
    (HKD, BND, DJF, XCD) never break, the game keeping no nation's vault.
  - **Historical** replays the file's monthly rates to December 2025 and opens the rule from that month's gaps;
    **Random** runs it from February 2000. Every draw is a pure function of (seed, money, month): one seed is one
    path, and a reload continues it. A month of 154-158 moneys costs 16 microseconds (median).
- **Wired** (`Game`, THE WORLD'S MONEYS IN THE MONTH): built beside the market, stepped where the market is struck
  (`strikeTheWorldMarket()`), the January's terms of trade handed; saved (`DataSave.worldMoneys`); the seven majors'
  rates, the cycle and the risk-off kept in the history (`fxEUR` ... `fxCHF`, `worldCycle`, `worldRiskOff`) for
  WN6's chart; the log notes a risk-off abroad and a world peg's break; the nation's card shows the month's rate.
- **The D$ feels the world** (`ForeignAccounts`, THE WORLD REACHES THE D$; §3.5): the dollar cycle at a loading of
  1, a risk-off by its traits (`riskOffLoading()`: +10% weaker for a small open money, less HAVEN_PULL for a vault
  of up to six months of imports and SURPLUS_PULL for a year in surplus, so a haven rises as the franc does, more
  by EXPOSURE_WEIGHT for hot money and debt abroad against a year's GDP), and its own terms of trade, all gated
  with the fundamentals and in the fair level, with a move line of its own (Trade › The currency: "The world").
  No world file: main's rate to the bit (ExchangeRateCheck 10).
- **The vault in eight** (`ForeignAccounts`, THE VAULT IN EIGHT; D6, D20): a balance in each of the US$, euro, yen,
  pound, yuan, Canadian and Australian dollars and Swiss franc; `getReservesUsd()` their sum at the day's cross
  rates, which every reader goes on reading. Money in goes by the **reserve mix** - all US$ (the default), match our
  trade (each partner's money mapped to the eight through its peg, else the US$), the world's COFER mix, or chosen
  shares - and the founders' dollars as dollars; money out comes from the largest holding unless the player names
  one. The eight moving against each other **revalue the vault** (`getLastVaultCrossUsd()`, the line "the moneys
  moved it by ..."). Trade › The reserves has the money picker, the mix and the line. All US$ is the old vault to
  the bit: the 1,200-month playtest with and without the slice differs in its run-time line alone.
- **The in-game US$ on the US's path; the dollar's chips gone** (D29): where the city has the nations, the world's
  price level is the US's, `P_US(t)/P_US(0)`, its headline this year's pace, nothing pulling it back (the walk's
  TREND_PULL kept a flat world). The founding screen's THE DOLLAR'S OWN INFLATION is gone: each nation inflates
  on its own path. With no world file the walk runs as before at the default mean. The ensemble's import shock
  goes through `WorldEconomy.shockLevel()` and is permanent on the path.
- **The real US$ of 2015** (`RealDollar`, D29, §3.9): D$ per 2015 US$, real GDP in 2015 US$ and the real effective
  rate (100 at founding, the city's trade by partner its weights), Game's three getters. Nothing reads them: WN6
  shows them.
- **Harnesses**: `WorldMoneysCheck` (new, 11 sections), `VaultMoneysCheck` (new, 7), `RealDollarCheck` (new, 6);
  ExchangeRateCheck 10-14; SaveFileCheck and ReadPathCheck for the new keys and slots (ForeignAccounts 95-104 the
  world, 105-141 the vault; `DataSave.worldMoneys`; WorldEconomy's slot after the mean).
- **`SAVE_FORMAT` 49 and `OLDEST_LOADABLE_FORMAT` 49** (the gate's review, 2026-10-31; CLAUDE.md's rule): a save of
  format 48 or older is refused, naming the build that still opens it. It would load wrongly, not merely incompletely:
  its dollar walked to a flat trend (about 1.15), and loaded with a world file it is handed the US's path, so its first
  month jumps to the path's level (about 6.65 at m1,200), every import price with it and `realisedInflation()` reading
  the jump for a year; and its D$ has no world reached (slots 95-104 absent), so its first reprice moves the rate by the
  gate times the world's whole level. No conversion is written. (The batch first shipped the new keys as additive,
  missing ones reading as no world; that read rightly only for the vault.)

## 2. The calls (★; each takes the study's recommendation where it has one, BP1)

1. ★ **The vault's revaluation across the moneys is not an audit flow.** The study says it is "booked in MoneyAudit's
   VALUATION scope, as the D$ value of today's dollar vault already is"; the D$'s revaluation of the vault never was
   an audit flow (`revalueVault()`: MoneyAudit leaves revaluations out on purpose, and VALUATION holds write-offs).
   The cross move is booked where the D$'s move is, in the same line. **Recommended: keep.**
2. ★ **The world's soft pegs break often**: by the D$'s rule on their own shadow (the study's §3.7), 82% of the 37
   soft pegs break within 26 years on Random (30,397 breaks over 1,000 paths against the rule's expected 30,295). A
   peg's shadow swings as its kind's float would (the CFA franc and the Gulf pegs at an emerging float's 4% a month).
   Real long-lived pegs (the Gulf, the CFA franc, the krone) have held for decades. **Recommended: keep the study's
   rule**; a calmer shadow for a pegged money (its sigma halved, say) is one dial if Jerus finds the Gulf too fragile.
3. ★ **The commodity share is over the game's 60 goods**: Canada's is 0.444 (so its pass-through 0.22, where the
   study's "about a quarter" meant 0.13), because the file's goods leave out most manufactures. But its terms of
   trade move less over the 60 goods than oil does (0.125 in 2008 against 2024), so the CAD moves about 2.8% where
   the measured 0.13 to the log of oil gives about 2.9%. **Recommended: keep.**
4. ★ **The D$'s own terms of trade are a level** (5): minus TOT_PASS times the export-weighted ln of what the city
   sells against its normal price, reached over three months. **Taken** (a sum of changes ran away).
5. ★ **The world reaches the D$ gated with the fundamentals and in its fair level** (a city that trades nothing has
   no market rate, today's rule). **Taken.**
6. ★ **Historical after 2025: the moneys swing at full size**, not WN1's half-size shocks (the dials are real
   currencies' volatility). **Taken.**
7. ★ **Every sale from the vault comes from the largest holding** - the defence, a band's or a peg's, the land
   office's - and every purchase goes by the mix, the strong edge's included; only the player names a money.
   **Taken** (the study names the treasury's; one rule for all is the simplest that holds the default).
8. ★ **The import shock is permanent on the US's path** (the walk pulled it back over decades). **Taken**; the
   ensemble's K1 reads it within 36 months.
9. ★ **The D$'s risk-off dials** (RISK_OFF_BASE .10, HAVEN_COVER 6 months, HAVEN_PULL .10, SURPLUS_PULL .05, so six
   months and a surplus read -0.05, the franc's; EXPOSURE_WEIGHT .5; clamped to [-0.08, 0.30]) are the game's
   estimates of the study's words. **Taken.**

## 3. Not done: WN4b, queued

**The D$ pegged to the euro or another of the eight** (XR2 left it for "XR3 in WN4, once their moneys exist"; the
study's XR3 row: "the euro and the eight as anchors for XR2's peg"). The moneys and their rates exist now; the
peg's anchor, the held rate as the peg times the anchor's rate, the shadow's gap against it and the page's picker
are a batch of their own (`ForeignAccounts`, THE REGIMES; Policy › The currency). Queued as WN4b after CD8.

## 4. The checks

- **The suite** (all 157 harnesses, both data files named, timing soft): after the mends of 5, main's recorded reds
  only, word for word: PassThroughCheck's price index (0.68% against 0.51%) and ScaleCheck's seven (HD2-S).
- **The playtest's trace moves** (the D$ feels the world; the dollar inflates on the US's path), so the ensemble ran:
  see 8. Slice C alone moves nothing: the 1,200-month playtest with and without it differs in its run-time line.
- **What a 1,200-month playtest says** (the default city, its report's lines; main 0.8.78 beside it): the world's
  prices 6.65x founding (main 1.150), the D$ 11.94 a US$ (main 27.59), the city's prices +4.57% a year (main +3.68%).

## 5. Found on the way

- **The D$'s terms of trade ran away** (slice B as briefed): each January's export-weighted change in ln world
  prices, weighted by the year before's export values, was summed. The values hold that year's prices, and the
  prices revert, so a good that was dear had a big weight and then fell: the sum only climbed. On the playtest's
  city it went +0.09 to +0.69 in a century, the D$ 5,919 per US$ at m1200 against main's 27.6, prices +8.7% a year
  against 3.7%. With the term off the run was main's in kind (24.3 at m1153). As a level it stays within -0.14 and
  +0.07 over the same run. ExchangeRateCheck 13 and WorldMoneysCheck 10 hold the level.
- **MapCheck's played city grows differently once the D$ feels the world** (its land-layer premise failed): it is
  given a quiet world (`Game.quietTheWorldForTheD()`, package-private), its threshold unmoved.
- **A reloaded city repriced its first month on no debt** (ReloadCheck): the D$'s risk-off loading read
  `getForeignDebt()`, struck after the reprice and not saved (main's screens show no debt until a month turns after a
  load, harmlessly). Game now hands the exposure, the hot money and the debt abroad at the rate, in `setWorld()`.
- **ScaleCheck's copy kept its city's vault**: it scaled slot 22's dollars and the restore now reads the eight
  balances (105-112); it scales those and the cross move too, and its reds are main's again.
- **ForeignCheck's devaluation fixture** assumed a world whose prices hold still while its two cities buy one
  programme; with the dollar inflating, the slower city's later imports cost 14% more dollars. Its world is pinned.
- **WoodProductsCheck's "imports none"** read 2.3e-13 m3 of dust from a subtraction; judged by its own `same()`.
- **A city saved with the world file and loaded without it** (the gate's review, PLAUSIBLE; checked from the code, it is
  real): `WorldEconomy.restore()` keeps the path's level and drops the path, so the dollar walks again from there, and
  the walk pulls the level to its flat trend by TREND_PULL (0.6%) of the gap a month: from m1,200's 6.65 about 0.5% a
  month, 6% a year at first, half the gap gone in about ten years. Its band goes back to the literals as well (WN3's
  rule for a missing file). Not mended here: pending as WN4-W, in the ship file.
- **The world's price level now inflates** about 1.9% a year on the default Random path (6.6x by m1200), where the
  walk held it near 1.15; the city's prices averaged +4.6% a year over 1,200 months against main's +3.7%.

## 6. For the next batches

- **CD7** (the city's foreign debt revalued) reads: `ForeignAccounts.revalueVault()` and `getLastVaultRevaluation()`
  for the shape of a revaluation that is not an audit flow (the D$'s move at the rate last valued, plus the cross
  move); the debt's own existing line, `takeForeignDebt()` / `getLastRevaluation()` after `DebtManager.setExchangeRate()`
  (`docs/month-order.md` items 138-140, right after the reprice); and, for paper in another money (CD8),
  `ForeignAccounts.getMoneyUsd(k)` (the eight's US$ per unit, handed at the top of the month by `setMoneyRates()`,
  `WorldMoneys.EIGHT`'s order, `ForeignAccounts.moneyIndex(code)`) or `WorldMoneys.rate(code)` / `cross(a, b)`.
- **XR4** (the eight's policy rates): the vault's holdings by money are `getHolding(k)` (native units) and
  `getHoldingUsd(k)`; the real gap still reads `DebtManager.WORLD_BASE_RATE - world.realisedInflation()`, now the US
  path's inflation, about 2% (Game's `realRateDifferential()`).
- **CD8** (borrowing in eight moneys): the same money order and rates; `WorldMoneys.ruled/pegHeld/pegAnchor` for a
  money's state.
- **WN6** (the screens): `Game.getWorldMoneys()`, the history's `fx*` series, `ForeignAccounts.getMoveWorld()`,
  `getWorldLoading()`, the vault's reads, and `Game.dollarsPer2015Usd()`, `realGdp2015Usd()`, `realEffectiveRate()`.

## 7. One in, one out

Not a process batch: no script, check rule or session rule changed. `tools/ensemble/Ensemble.java`'s shock door
(+17 -6) calls `WorldEconomy.shockLevel()` by reflection where a tree has it, the old door kept for older trees: the
model's door, not the process's.

## 8. The ensemble

`docs/ensemble/WN4.md`, 80 of 80 runs on db8e4838 (both data files named, 2 at once, 17 minutes), against
`docs/ensemble/0.8.78.md`: **24 of 26** (main 23; H1 retired, not counted).

| | WN4 | main 0.8.78 |
|---|---|---|
| A1 AUTO2's inflation m240-end | 2.33% [1.87 to 2.96], 16/16 in band | 1.88% [1.72 to 2.26] |
| A2 longest spell of yoy over 10% from m240 | 15 months [10 to 23], FAIL | 12 [3 to 23], FAIL |
| S1 at 0% minus its AUTO2 twin, m1800-2040 | 1.98% [0.41 to 4.70], longest spell 55, FAIL | 1.09% [0.25 to 4.24], spell 35, FAIL |
| R3 at 15% minus its twin, m660-720 | -1.41%, 1 seed over +1 pt, PASS | -1.50%, 6 over, FAIL |
| T2 AUTO2's final population | 320,072 [241,062 to 381,103] | 315,900 |
| T1 seeds past 40,000 | 16/16, m1,056 | 16/16, m1,082 |

The dollar now inflates about 2% a year where it held near flat, and the AUTO2 city's inflation sits about half a
point higher, inside A1's band; the D$ swings with the world (the cycle alone is about 10% either way, one sd), and
A2 and S1, red on main, are redder. Every run is identical to its AUTO2 twin before the switch (64/64); no money
audit failed, no LongPlaytest finding.
