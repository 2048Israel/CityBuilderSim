# XR4 - interest abroad: the eight central banks' rates, the US's as the world's price of money, the vault's interest and the carry's unwind ({next}, 2026-10-31)

*The batch's record. The brief is `docs/studies/spec-exchange-rates.md` §3.8 and §5's XR4 row (Jerus, 2026-10-21, ★9
and ★11). Lane A's third batch on the night of 2026-10-30, built on main after WN4 (0.8.81) and CD7 (0.8.83), both
shipped. Opus transcribed the rates' table, wrote the briefs, reviewed each diff and committed; three Sonnet 5.5
slices at xhigh wrote the code (A the rates and the world's rate, B the vault and the unwind, C the screens), one at
a time; Opus recalibrated the hot money's unwind (2, call 6).*

## 1. What it is

- **`PolicyRates`** (new, model): the policy rate of each of the eight central banks of the vault's moneys
  (`WorldMoneys.EIGHT`), a month at a time, a fraction a year.
  - **Historical** replays the real monthly rates of 2000-2025 (★11), from `src/main/resources/policy-rates.tsv`: one
    line per decision, the month it took effect and the rate after it, so a month reads its last decision (its
    month's end). The readings are in the file's header (the Fed's range at its middle; the euro's main refinancing
    rate to 2008 and its deposit rate from 2009, where the euro's overnight rate sat; China's one-year lending rate,
    then its loan prime rate; the SNB's Libor target, then its policy rate).
  - **Random, and Historical from January 2026**: a Taylor rule on each bank's anchor nation's inflation over twelve
    months (`WorldMoneys.priceLevel`): target = max(0, neutral real + π + ½(π − the bank's target)); a shadow closes
    on it with a nine-month half-life, and the rate follows the shadow in whole quarter points, never past it. Random
    opens from January 2000's real rates, as the moneys do.
  - Saved (`DataSave.policyRates`; a save without it rebuilds them from January 2000, a line saying so) and kept in
    the history (`policyUSD` ... `policyCHF`).
- **The US's rate is the world's price of money** (★9): `DebtManager.worldRate()`, handed each month right after the
  moneys step and at a load (`Game.handTheUsRate()`), read where `WORLD_BASE_RATE` stood for what the world pays: the
  real rate gap (the dial less expected inflation against the US's rate less the US's inflation), the hot money, the
  carry, the foreign curve and its quotes (so dollar paper is priced and valued on the Fed's rate), the peg's tied
  dial, the currency aim and the spread's bounds, the auction's and the corporate bonds' world bidder, and the
  city's savers abroad (`OutwardInvestment`, `HouseholdBalance.investAbroad`). With no world file, or a world
  quieted for the D$, it is `WORLD_BASE_RATE` and nothing moves, to the bit.
- **The vault earns each money's rate** (`ForeignAccounts.earnVaultInterest()`, THE VAULT EARNS): at the top of the
  month, after the cross rates, each balance grows by its rate over twelve in its own units (a negative rate takes
  from it). It stays in the vault (★, 2): the central bank's equity reads it with the vault, the reserves' identities
  book it, and the month's and the lifetime's US$ are saved (slots 142-146).
- **A risk-off unwinds the hot money and the carry by their stocks** (`CapitalFlows`, THE RISK-OFF UNWINDS THEM): while
  `WorldMoneys.riskOff()` is at least RISK_OFF_FELT, the hot money leaves RISK_OFF_HOT_UNWIND times the level of its
  stock a month, departed like the doubt's money (out of the bank's deposits, across the audit's edge), and the carry
  is repaid RISK_OFF_CARRY_UNWIND times the level of its book. A city that drew more hot money with a higher dial
  loses more. The log says "Money ran home"; the playtest counts the months and the money (slots 19-23).
- **Screens**: Policy reads `worldRate()` wherever it read the constant, and names it the US's rate (the Federal
  Reserve's, this month's figure) where the city has the world file: the dial's tick, the rate line's mark, the
  over-the-world cell, the real gap's caption, the peg's tied dial and its spread's bounds, the aim's words. Trade ›
  The reserves: a chip for the month's interest ("the vault earned US$x in interest this month: the euro earned most,
  at 2.50%"), its (i) with the yield and the lifetime, and the vault card's yield line. The hot money's card: what
  risk-offs sent home, in how many months. The map's nation card: a policy rate row for a nation on one of the eight
  (GlobeCheck 12). Not looked at in the running game: the screen-check session's.
- **Harnesses**: `InterestAbroadCheck` (new, 9 sections: the table, Historical's replay and the rule, Random's rule,
  the city's reading, no world, the save, the vault's interest to the cent, the unwind by the stock, and a city
  through a forced risk-off whose audit closes); CarryTradeCheck 6 (the carry's unwind); SaveFileCheck and
  ReadPathCheck for every new key, slot and series.
- **No format bump**: every new key and slot is additive and reads rightly when missing (the rates rebuild from
  January 2000, the vault has earned nothing this month, nothing has run home since founding).

## 2. The calls (★; each takes the recommended way, BP1)

1. ★ **Every reader of "what the world pays" reads the US's rate**, the foreign curve among them (CD8 builds the
   other seven moneys' curves the same way, and a dollar curve on a flat 2% beside a euro curve on the ECB's rate
   would not hold together). **The equity's required return stays on the constant** (`Bank`, Game's two offers and
   the exchange): a share's required return is a long-run figure, and the policy rate's swings would whipsaw share
   prices. **Recommended: keep.**
2. ★ **The vault's interest stays in the vault**, compounding in each money, as a reserve manager reinvests its
   coupons: no D$ is made for it, no audit pool moves, it is not on the current account (it never meets the D$
   market), and it reaches the city only when the vault sells. The other way is the treasury's revenue each month,
   as Canada's Exchange Fund Account's net income goes to its government (the study's words), paid in money made.
   **Recommended: keep.**
3. ★ **The carry and the hot money both on the US's rate**, as §5's row says (§3.8's prose has the carry "borrow in
   the cheapest of the eight"): the carry's borrowers hold dollars (CapitalFlows' own account of it), and the US$ is
   on one side of 89% of trades. **Recommended: keep.**
4. ★ **The rule's dials are the game's estimates**: each bank's inflation target (2%; the RBA's 2.5%, the SNB's 1%) and
   neutral real rate (its own published long-run neutral rate less its target: the Fed 1%, the ECB 0, the BoJ
   −0.5%, the BoE 1%, China 1%, the BoC 0.75%, the RBA 1%, the SNB 0), Taylor's ½, a nine-month half-life, and a
   floor of zero: the rule runs no negative rates (Historical replays 2014-2024's). On five Random seeds the US's
   mean real rate is 0.94-1.28% against its 1%. **Recommended: keep.**
5. ★ **The table is transcribed from the banks' announced decisions**, as the BIS's series carries them, not fetched
   (the study's [R38] was named and not fetched). **Recommended: keep;** comparing it with the BIS's file is a
   check of an afternoon if Jerus wants one.
6. ★ **The unwind's dials**: RISK_OFF_HOT_UNWIND .05 (a size-one risk-off takes about a fifth of the stock over its
   year), RISK_OFF_CARRY_UNWIND .50 (the yen carry of July 2024 was mostly gone in six weeks), RISK_OFF_FELT .05 (the
   level under which nothing runs; `riskOff()` never reaches 0, and a size-one risk-off is under it after about
   thirteen months). The hot money's was briefed at .20; CentralBankCheck's 240-month probe city, whose dial reaches
   26%, then held about three quarters of a year's output in hot money and lost three fifths of it in a risk-off,
   and its very strict twin ended with dearer prices than Standard's (3.71% a year against 3.04%). A sudden stop
   reverses a fraction of a country's foreign money, not the most of it. At .05 the probe's order is the rule's
   again (4.14%, 5.11%, 7.04%). **Recommended: keep.**
7. ★ **The city is founded under the Fed's 5.5%** (January 2000, both paths): hot money needs a dial over 5.5% and the
   premium in the first years, and dollar paper costs that much more; on Random the rule takes the US toward about
   3% within two years. **Recommended: keep** (it is the real January 2000).

## 3. Found on the way

- **BondCheck's played city** (7c): under the US's 3.25% at month 25 the sectors' idle cash goes abroad and
  Construction borrows, so its premise, a sector owing nothing, was gone. Given a quiet world, as WN4 gave MapCheck's
  played city; the premise unmoved.
- **The peg spread was clamped before the US's rate was handed** at a load (its bounds stand over the US's rate):
  the load now applies it again after the rate, out of the decision log.
- **CurrencyCheck, CurrencyRegimeCheck and CapitalFlowCheck** asserted the world's rate by the constant in cities
  that now read the US's; ForeignCheck, CurrencyCheck and VaultMoneysCheck's vault identities take the interest as a
  booked term.
- **A purchase budget of an ulp** (DenominationCheck's decade, main's knife edge that the Fed's path reaches): a
  defaulted Freight sector whose only bill is its interest has it met exactly by the reserve its lender allows, so its
  budget is two equal figures of opposite sign - 0 in the reformed twin, 7e-15 D$ in the plain one. That dust ordered
  3e-17 trucks, a market with demand and no supply struck at its ceiling, the price of trucks doubled in one twin and
  the two parted from game month 207. `EconomyManager.BUDGET_NOISE` (1e-12 of the budget's terms) reads such a budget
  as nothing; it fired once in 10,560 budgets in that run, and it moves the ensemble's HIGH seed 3 from month 460.
- **MortgageCheck's loss identity** left out the bank's bond loss: under the Fed's rate the bank holds a little of the
  landlords' bonds and writes them down with the mortgage (`BondMarket.getBankLossThisMonth()`); it is in the identity.
- **Played cities given a quiet world** where the world is not their subject and the new path crossed a margin:
  BondCheck's (a sector owing nothing), PolicyPreviewCheck's (a bank profit to tax, claimants on the pool),
  HoldersCheck's whole-book city (the households' share of an issue 49.0% against 51.5%, under its step) and
  HealthCheck's twins (the fee's 3% in deaths under the noise of twins ending at 10,258 and 13,908 people). Each
  premise and threshold is unmoved; HoldersCheck's world section keeps the world. Jerus may prefer the last two left
  in the world with their margins argued again.

## 4. The checks

- **The suite** (all 161 harnesses, both data files named, timing soft, on 2aa7144d): main's recorded reds
  (PassThroughCheck, ScaleCheck's seven) and **one of its own: LongPlaytest, exit 1, no FAIL line**, two map findings
  at the run's end - "a building on the map moved, 2 time(s), months 3998-3999" (7 houses of a district's 1,675
  boxes) and "an old works moved, 1 time(s), months 4000-4000" (6 of 17 Rod Mills). The map's defect class that
  FX7 dated the runs for and that ET1's runs met on main; XR4's path reaches it. Pending as XR4-M.
- **The default playtest moved**, so the ensemble ran (5). Its report's XR4 line over 4,000 months: the vault earned
  US$13,362,613,906k as its report counts (a 2.75% yield in the last month), and risk-offs unwound money in 506 months.
- **A first suite** on 95504317 had DenominationCheck, MortgageCheck, HoldersCheck and HealthCheck red too: mended in
  slice D (3). CentralBankCheck's probe went red with the unwind at .20 and green at .05 (2, call 6).

## 5. The ensemble

`docs/ensemble/XR4.md`, 80 of 80 runs on 2aa7144d, the final model (both data files named, 2 at once, 21 minutes),
against `docs/ensemble/0.8.83.md`: **22 of 26** (main 24; H1 retired, not counted). A first run on 95504317, before
slice D's BUDGET_NOISE moved some paths, scored 21 (R2 red at 0.42, A3 at 15 months).

| | XR4 | main 0.8.83 |
|---|---|---|
| A1 AUTO2's inflation m240-end | 2.41% [1.97 to 2.87], 16/16 in band | 2.33% [1.87 to 2.96], 16/16 |
| A2 longest spell of yoy over 10% from m240 | 16 months [11 to 31], FAIL | 15 [10 to 23], FAIL |
| A3 deflation spell from m240 | 6 months [0 to **13**], FAIL | 6 [0 to 11], PASS |
| S1 at 0% minus its AUTO2 twin, m1800-2040 | 2.38% [0.83 to 4.92], spell 47, FAIL | 1.98% [0.41 to 4.70], spell 55, FAIL |
| R2 unemployment at 15% minus twin | +1.50 points, PASS | +1.03, PASS |
| R3 at 15% minus its twin, m660-720 | -0.95%, **3 seeds** over +1 pt, FAIL | -1.41%, 1 over, PASS |
| T2 AUTO2's final population | 347,140 [**68,654** to 383,997] | 320,072 [241,062 to 381,103] |
| T1 seeds past 40,000 | 16/16, m1,066 [881 to **2,256**] | 16/16, m1,056 [970 to 1,191] |

**AUTO2 seed 11 is held near 20,000 people from about m900 to m2,200**, unemployment 20-25%, the dial swinging
between 0 and 16% and inflation between -13% and +11% a year, its hot money between nothing and 2 months of output
within 50 months; it takes off at m2,200 and ends at 68,654. It is not RE1's trap: Real Estate earns throughout
(its net income 9-19 D$k a month over m700-1,300). Its cause is not found here: XR4-S, pending. The other fifteen
end between 248,000 and 384,000. Every run is identical to its AUTO2 twin before the switch (64/64); no money audit
failed; no LongPlaytest finding in the ensemble's runs.

## 6. For the next batches

- **CD8** (the city borrows in eight moneys): each money's rate is `Game.getPolicyRates().rate(code)` (or `rate(k)`
  in `WorldMoneys.EIGHT`'s order; null policy rates with no world file); its curve is that rate plus the country
  premium plus the term premium, as `DebtManager.foreignRate()` / `foreignCurveRate(months)` now build the US$'s on
  `worldRate()`. `PolicyRates.ruling()`, `shadow(code)` and `target(code)` say where a rate is going.
- **WN6** (the screens): Trade › Partners' policy rates column reads `PolicyRates.rate(code)`; the history keeps
  `policyUSD` ... `policyCHF` for a chart; the vault's yield is `ForeignAccounts.getVaultYield()`.
- **WN4b** (the D$ pegged to the euro or another of the eight): a peg's tied dial should then read the anchor's
  policy rate, not the US's (`DebtManager.tiedRate()` reads `worldRate()`).

## 7. One in, one out

Not a process batch: no script, check rule or session rule changed.
