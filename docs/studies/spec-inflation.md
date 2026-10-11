# Spec: inflation that answers to money (P1a, 2026-10-04, for 0.7.42 and 0.7.43)

Prototype: `/home/claude/cbs-proto` (0.7.41 + switches `-Dproto.*`), probes and ensemble copies in `/home/claude/scratch-spec/` (`ens/out/<variant>/`, `out/<variant>/`). Every number below was measured there unless it says "estimate". The final variants are **p2g** (batch P2 as specified) and **p3h** (P2 + P3).

## 1. Summary for Jerus
1. **The anchor works.** Expected inflation is the target weighted by credibility plus recent inflation; money constants (shelf floor, land, building costs, fees) drift at it. Under the autopilot, 2 seeds × 2,400 months: inflation **2.13%** a year (P2), **2.64%** (P2+P3), against ~0% today.
2. **Wages** take half their indexing from expected inflation and half from chasing the index (never both in full): total pass-through 1, no overshoot.
3. **The currency had to drift too.** Without it a 2% city's currency ended at 0.43–0.46 of parity (0.7.41: 0.62) and populations fell to 26–54k (from 97–106k). With a drift at the *credible* part of expected inflation it ends at 0.78–0.85 and growth came back (105–134k, 40k reached by month 1,339–1,480).
4. **Groceries clear on demand at a price**: a basket a head at most, inelastic (ε 0.4), budget-capped, so the 14–17× "want" is gone. The poorest are priced out of the allocation; the charged price is capped at 1.5× the cost floor.
5. **Coverage is the supply side**: what the shops can hand over is coverage × operating rate. In your city2400 that is 43% of the people (roads 0.71, power 0.80, health 0.77), so groceries clear at 5–20× the shelf, and no price can fix it. The shops' planner must answer the shortage. That is P3's biggest untested risk.
6. **The index covers meals, luxury and services** with chained weights; luxury's weight is capped at 15% (in city2400 it would be 63%, and fx-driven).
7. **Food assistance**: a dial, default 0, paid by the treasury to poor households' grocery bills. At 50% in the founding, hunger 4.1→2.9% (year 10) and the clearing price +3–4%; the food sold is the same, it goes to the poor.
8. **Two things failed and were changed**: a drift at *full* expected inflation made the currency explode at 0% (41–66% a year, fx 400,000), and putting wages into the shelf floor bankrupted the shops (81% hungry) and ran 4% under the autopilot.
9. Policies, prototype: autopilot ✓; slack city at 0% (city2400): 2.6% ✓; growing city at 0%: 4–14% a year, and it stops under the autopilot ✓; import shock: a +9–13% step that settles ✓ (tails 1.5–4.7%); 15% held: −1 to −5 points, unemployment up, no savers' trap ✓.
10. Open for P4/P5: the founding fixture under the autopilot sits at ~5% for 20 years in P3h; young cities run ~5% at months 600–840; harness premise notes in §8.

## 2. The model, part by part

### 2.1 The anchor (new class `Expectations`, called from `Game.nextMonth` straight after `priceIndex.takeMonth`, L8070)
State: credibility κ, smoothed inflation π̃, expected inflation πe (annual), expected price level Pe (in founding money, 1.0 at the base), and this month's lean λ (record only).
- Before the basket is based (`!priceIndex.isBased()`): Pe = 1, πe = π*, π̃ = π*, and κ is untouched.
- Each month after: π12 = `priceIndex.inflation()` if `hasRate()`, else π*.
  - π̃ ← π̃ + (π12 − π̃) / `ADAPT_MONTHS` (12).
  - miss m = |π̃ − π*|.
  - **lean** λ ∈ [0,1], whether the bank is leaning against the miss. With iN = `ruleRate(π*, π*)` (the neutral nominal) and iR = `advisedPolicyRate(π12)`: if π̃ > π* and iR > iN, λ = (i − iN)/(iR − iN); if π̃ < π* and iR < iN, λ = (iN − i)/(iN − iR); otherwise 0; clamped.
  - if m ≤ `TOLERANCE` (.01): κ ← κ + (`KMAX` − κ)/`GAIN_MONTHS` (60)
  - else: κ ← κ − (κ − `KMIN`) × (1/`LOSS_MONTHS` (24)) × min(1, (m − TOL)/`MISS_SCALE` (.04)) × (1 − λ)
  - πe = max(`EXPECTED_FLOOR` (−.01), κπ* + (1 − κ)π̃); Pe ← Pe × (1 + πe)^(1/12).
- Range: `KMIN` .25, `KMAX` .95, `KSEED` .80. A full miss (5+ points, not leaned against) halves the distance to KMIN in ~17 months; credibility rebuilds over ~5 years.
- **Seeding:** new game κ = .80, π̃ = π*, Pe = 1. An old save (no key): κ = .80, π̃ = π12 if `hasRate()` else π*, **Pe = 1.0** (the constants are still at founding money, so nothing jumps). A reform does not touch Pe (it is a ratio; constants are struck as founding / unit × Pe).

### 2.2 Wages (`LabourMarket.updateCostOfLiving`, L358, called at `Game.startOfMonthUpdate` L8200)
Once the basket is based: C ← C × (1 + πe_m)^ω × (L/C)^((1−ω)/24), with L = 1 + (P − 1) × `COST_OF_LIVING_PASS_THROUGH` (1.0), πe_m = (1+πe)^(1/12) − 1 and ω = `EXPECTED_SHARE` .5. Before it is based the line stays as today.
- **No double counting.** In steady inflation π = πe, wage growth is ωπ + (1−ω)/24 × gap = π. Wages lag the index by 2 years of inflation (as today). A one-off level shock with πe unmoved passes 1/48 a month, not 1/24; long-run level pass-through is exactly 1. Overshoot needs πe > π, which happens only in a disinflation, where it is the cost of disinflating.
- The tightness curve (`multipleAt`, ADJUST_RATE .12) is untouched. It is the Phillips part: a level wage curve, which turns into persistent inflation only when expectations un-anchor (★1).

### 2.3 Money constants: the inventory, struck monthly at Pe (`Game.restrikeMoneyConstants()`, new)
The rule: a money constant that prices something follows **Pe**, never the index (so no constant reads a price it sets). Guards and grains follow the unit only. Implemented as `seedConstants(unit / Pe)` on the classes that already have it, called after `Expectations.takeMonth` each month and on the load path after the denomination restore (Game L12764). A reform needs nothing new.

| constant | where | follows | note |
|---|---|---|---|
| `OPENING_SELL_PRICE` .30, the shelf floor | Retail.seedConstants L651 | Pe | stays a floor; P3 reads it too (§2.6) |
| `LandMarket.BASE_PRICE_PER_SQ_FT`, `LandManager.DEFAULT_PRICE_PER_SQ_FT` | LandManager L525, LandMarket L838 | Pe | the USD ground price and `IRON_PRICE_PER_TONNE` stay world money |
| buildings.json `cashCost`, `upkeep` (founding dollars) | BuildingsTemplate.seedConstants L708 via BuildingManager L5389 | Pe | measured in city2400: non-labour cash is 57–63% of a home's build cost, labour 1–7%, materials 35–36% |
| `BuildingManager.nonMaterialCost`/`labourCost` L145/L155 | fix | — | labourThen = min(cash/Pe, points × perPointAtFounding); cost = cash − labourThen × Pe + labourThen × buildersWageIndex |
| Healthcare `GENERAL/CHILDCARE/SENIOR/BURIAL/CREMATION_FEE` | Healthcare L924 | Pe | × the player's fee scale as now |
| Education tuition (founding table) | Education L134 | Pe | × the tuition scales |
| `TaxPolicy.pensionWageBase` (pensions, the FIXED grant's ceiling) | TaxPolicy L882 | Pe | pensions were nominal: inflation ate them |
| `Equity.FOUNDING_PRICE` | Equity L156 | Pe | a new company's par |
| `Bank.PAID_IN_PER_BRANCH`, `DOMESTIC_CAPITAL_SCALE`, `ACCOUNT_FEE` | Bank L5672 | Pe | the fee read `accountFee(index)` (Game L1739): becomes `accountFee(1)` |
| FIXED student grant (`TaxPolicy.grantBill(... index ...)`, Game L9738/L9761/L12664) | Game | Pe | was the index |
| new-home rebates (`setFoundingToToday`, Game L256) | Game | Pe/unit | was index/unit |
| `Game.FIXED_ISSUE_COST` 12 | Game L8483 | Pe | |
| `TaxPolicy.DEFAULT_TRANSIT_FARE` and the fare dial | TaxPolicy | Pe ★9 | the dial becomes real (founding money), like the minimum wage and the grant |
| dust and guards: `MIN_MOVE`×2, `MIN_FAIR`, `MIN_DEALER_EQUITY`, `MIN_STOCK`, `OrderBook.DUST`, `ForeignAccounts.minTrade/minRate/maxRate` | — | unit only | numerical grains, not prices |
| `ForeignAccounts.parityBase` | — | unit only | parity already moves with P/Pw |
| `LabourMarket` settable bounds, every multiple and share | — | stay | already real |
| `MATERIALS_WORLD_PRICE`, `WORLD_FUEL_PER_TONNE` | — | stay | world money × fx × world level |
| founding presets, `FOUNDING_CASH`, granules, YearBook zero thresholds | — | stay | one-off or display |

### 2.4 The rule and the real rates
- **Taylor intercept** (`DebtManager.ruleRate` L240): `NEUTRAL_RATE + (target − DEFAULT_INFLATION_TARGET) + TAYLOR_WEIGHT × (π − target)`. Identical at 2% to the bit, and on target it now advises a 1% real rate at any target (today a 20% target sits at 0% until π > 18%).
- **Ex-ante real rates:** `Game.realDepositRate` (L6738) = deposit − πe; `Game.realRateDifferential` (L6726) local side = policy − πe (the world side is unchanged). An import spike under a credible bank no longer cuts the real rate.
- **Investment hurdle** (`BusinessInvestment.servicesItsOwnDebt` L914): the rate tested is max(.25 × rate, rate − πe); πe is handed in monthly (`setExpectedInflation`, default 0 so InvestCheck's fixtures are unchanged) ★7.

### 2.5 The currency (`ForeignAccounts.repriceCurrency` L746, `ratePressure` L513)
- **Anchored drift:** after the push, `rate *= (1 + d)^(1/12)` with d = (1 + κπ*)/(1 + πw) − 1, πw = `world.realisedInflation()`, set by Game before L8025 (`setExpectedDrift`). It is 0 before the basket is based and while pinned.
- **Expected reversion (UIP):** `raw = −RATE_PULL × (realRateDifferential + EXPECTED_REVERSION × ln(rate/parity))`, with `EXPECTED_REVERSION` .15 a year. A sustained real gap g now moves the currency to a level, fx/parity = e^(−g/.15) (a 3-point gap gives 22%), not a perpetual drift.
- Why both (measured): with no drift, a city at 2% ends at 0.43–0.46 of parity and growth halves (with both: 0.78–0.85; 0.7.41 0.62). A drift at the *full* πe compounds with the real-rate flow (gain ≈ 1 + 0.96 per unit of πe) and exploded at 0% held (41–66% a year). The credible part κπ* has no loop in it; the rest of πe reaches the currency only through the real rate, bounded by UIP.

### 2.6 Demand at a price, per spending kind (household side: `Household.plan`, `HouseholdBalance`)
- **Groceries (new).** Per household of a cell: q(p) = min(n × min(1, (p_sat/p)^ε), F/p), in baskets.
  - n = `baskets()`.
  - F = `spendable` + assistance (the money it could put to food: take-home after fixed bills, savings, abroad, paper, credit room).
  - p_sat = `SATIATION_MULTIPLE` 1.5 × `OPENING_SELL_PRICE` / unit × Pe (★2): a full basket is wanted up to 1.5× the anchor price, which covers today's shelves (1.33–1.41× the floor).
  - ε = `GROCERY_ELASTICITY` .4 (★3; USDA ERS food-at-home −0.3 to −0.6; Andreyeva et al. 2010, AJPH, mean of food categories ≈ −0.5; cereals and dairy lower).
  - Engel's law holds by construction: quantity saturates at a basket a head, and the spend share falls with income. Extra money goes to the meal and luxury budgets or to savings. Income elasticity of quantity is 0; the richer shelf is Consumption's composition, as now.
  - City demand D(p) = Σ households × q. It is monotone in p.
- **Meals and luxury:** unchanged budgets (`mealWant`, `luxuryWant`, the wealth term, the spend factor). Unit-elastic in price (`mealsWanted(p)`, `luxuriesWanted(p)`), with the meal appetite cap (1/3 of meals).
- **Housing:** unchanged. Money reaches doors through affordability: singles double up when take-home cannot carry rent + shopping + fees (`livingAlonePressure` → `shareByAffordability`), so seekers/doors is a demand at a rent. No wealth term (★4: it would let a held high rate's interest bid up rent).
- **Services:** fixed bills priced by dials × Pe; quantity is not price-elastic.
- **`want`/`planned` stay**, unchanged in arithmetic, as the *discretionary plan* the luxury and meal surplus and `isGoingShort()` read (★5: changing `isGoingShort()` reopens DenominationCheck's share-path divergence). They no longer reach the grocer.
- **"Coverage" is the supply side:** S = min(coverage × `getOperatingRate()`, `basketsOnShelf()`), in baskets.

### 2.7 Sticky clearing
One form for every seller: ln p' = (1−θ)(ln p + ln(1+πe_m)) + θ ln T, with T = max(floor, min(p*, CAP × floor)). If p < floor, p ← p + (floor × (1+πe_m) − p) × .5 (sellers do not stay under a floor).

| market | clearing p* | floor | cap | θ | while below clearing |
|---|---|---|---|---|---|
| shelf (`Retail`) | D(p*) = S, log-bisection on [p/50, 50p], 50 steps | max(`OPENING_SELL_PRICE`/unit × Pe, blendedCost × `RETAIL_MARKUP` 1.5) | `CLEARING_CAP` 1.5 × floor | 1/6 | sold = ⌊min(D(p), S)⌋; allocated at p_alloc = max(p, p*): cell gets q(p_alloc) × sold / D(p_alloc), paid at p. The poorest are priced out first |
| rent (`RealEstate.repriceRent` L387) | today's `targetFor` (cost × pressure × carryLift) | none (as today) | none | 1/12 (`LEASE_MONTHS`); the πe drift is new and untested | doors rationed by `families.house` as now |
| meals (`Restaurants.strikeMargin` L232) | today's position margin (3–8× food) | 3× food | 8× | 1/6 on the margin | `serve()` caps (seats × operating rate, larder) |
| luxury (`LuxuryRetail.strikeMargin` L244) | today's bisected margin (1.25–4×) | 1.25× landed | 4× | 1/6 on the margin | `serve()` caps |

Costs pass through at once (landed price, food cost); only the margin is sticky. A charged margin restored as NaN (old save) starts at the target. Reported scarcity = T/floor; delivered = sold/D(p).

### 2.8 The index (`PriceIndex`)
- I = L × Σ_k w_k × p_k / b_k, for k ∈ {groceries (shelf), rent (average paid), meals (`Restaurants.getSellPrice`), luxury (`LuxuryRetail.getSellPrice`), services}.
  - Services: a fee index = Σ (base fee spend share) × (unit fee / base unit fee) over the health fee per general visit × scale, tuition per student, fare per journey and account fee (untested; ~1–2% of spending).
- Weights come from the **trailing 12 months** of household spending (groceries `salesToHouseholds`, rent, `getMealSpend`, `getLuxurySpend`, the fee lines). Luxury is capped at `LUXURY_WEIGHT_CAP` .15 (★6), the excess spread pro rata.
- **Chain-link:** at the first month of 0.7.43 on an old save, set b_k = this month's prices, w_k from the trailing year (this month ×12 if there is no year yet), L = the index then. The level is continuous, and the 13-month ring keeps the year-on-year rate across the link.
- **Rebase every `REBASE_MONTHS` 120** the same way. A component with no price this month holds its last one.
- A new city bases all components at the 24th shopping month as now, with a zero weight for any not yet sold.
- `redenominate` scales every b_k.

### 2.9 Food assistance (`TaxPolicy.foodAssistance`, 0–1, default 0)
- **Who:** a household whose basket bill n × p exceeds half of `afterFixed` + investment income (★8), and everyone with nothing after fixed bills (the unemployed off EI, orphans, students without grant cover).
- **How much:** a voucher of share × n × p_shelf, added to F (so it bids for food) and paid only on the baskets actually got: aid = min(voucher, got × p).
- **Funded:** by the treasury, as `TreasuryLine.FOOD_ASSISTANCE` (appended last), in the month of the sale, inside the audit window. It is a declared domestic transfer, city → household pool, like EI.
- **Books:** a government-statement transfer line "Food assistance"; HouseholdAccounts outside money beside EI and grants (row split by the cells paid); `MoneyAudit` named flow; `DecisionLog.PROMISE` "Food assistance to N%"; year book column `foodAssistance`; History series `foodAssistance` and `fedByAssistance` (baskets). PolicyPreview line.

### 2.10 Transmission, the rest
- **QE:** unchanged ★10. It swaps paper for reserves/deposits and still often has no paper to buy; it now reaches prices only through net worth and the currency, which is the honest answer. The Money page should say so.
- **`investAbroad`:** unchanged ★11 (nominal comparison, 80% abroad at a 0% deposit). The leak is now bounded by UIP. Measured: doubling deposits at 3% held moves the index +8.2% at year 10 (0.7.41: +2.1%), wages +7.4%, fx +16%.
- **Savers' interest trap:** bounded, not fixed ★12. city2400 at 15% held: household net worth +2% in 20 years, and the index falls 2% a year for 3 years then holds. Guards: deposit pass-through ~.35, wealth spent 4% a year × spend factor, margin ceilings, Engel satiation, and the UIP appreciation.

## 3. Batch P2 (0.7.42, the anchor): exactly
1. **`Expectations.java`** (new, model): constants of §2.1, each a `static final` with its sentence.
   - Methods: `takeMonth(PriceIndex, double target, double policy, double neutral, double rule)`, `seed(PriceIndex, double target)`, `monthlyExpected()`, getters `getCredibility()`, `getSmoothedInflation()`, `getExpectedInflation()`, `getExpectedLevel()`, `getLean()`, `toSaveArray()`/`restore(double[])` (5 slots + lean), `reset()`.
   - Game field and `getExpectations()`. Called at L8070+ with `debtManager.ruleRate(t, t)` and `debtManager.advisedPolicyRate(...)`. Reset in the new-game path.
2. `LabourMarket.updateCostOfLiving(double index, double expectedMonthly)`, the §2.2 recurrence plus `EXPECTED_SHARE`. Game L8200 calls it once based.
3. `Game.restrikeMoneyConstants()` per §2.3. Called after (1) and on load. `Bank.accountFee(1)`; the grant and rebates read Pe; `nonMaterialCost`/`labourCost` fix; the fare dial real (TaxPolicy state slot appended, the 0.7.19 grant pattern).
4. `DebtManager.ruleRate` intercept (§2.4).
5. `Game.realDepositRate`, `Game.realRateDifferential` on πe.
6. `ForeignAccounts`: `setExpectedDrift(double)`, the drift line and `EXPECTED_REVERSION` (§2.5). The field `expectedDrift` is derived each month: not saved, re-set on load before the first reprice.
7. `BusinessInvestment.setExpectedInflation`; `servicesItsOwnDebt` real.
8. **Save:** `DataSave.expectations` (double[]), out and in. `HistorySave` series `expectedInflation`, `credibility` (round4). **No `SAVE_FORMAT` bump:** a missing key seeds (§2.1), which is correct, not wrong.
9. **Year book:** columns expected inflation, credibility. DecisionLog: nothing new.
10. **Harness:** `ExpectationsCheck` (new; in AllChecks). Sections:
    1. before basing: Pe 1, πe = target;
    2. on target κ rises to KMAX at GAIN speed;
    3. a miss not leaned against loses at LOSS × miss share; leaned against it does not;
    4. πe floor;
    5. Pe drifts at πe;
    6. the wage recurrence: total pass-through 1, the steady lag;
    7. constants at unit/Pe, and a reform twin agrees;
    8. the drift and UIP equilibrium fx/parity = e^(−g/.15);
    9. save/load round trip and old-save seeding.

**Harness premises the design makes wrong (list for Jerus; no tolerance moves):**
- LabourCheck `wagesAgainstTheIndex()`: the 1/24 recurrence becomes §2.2.
- MonetaryCheck §4 L244 ("0% and 5% differ by TAYLOR_WEIGHT × 5") becomes (TAYLOR_WEIGHT − 1) × 5. L246/L262 ("on target, neutral") becomes "the neutral real rate plus the target".
- MonetaryCheck §4 L286–288 holds only at parity: the fixture sets rate = parity.
- BankCheck: the account fee is "× Pe", no longer "× the cost of living".
- EducationCheck/TreasuryCheck: the FIXED grant is real against Pe.
- Every city fixture's numbers move: the reference traces are re-baselined.
- Expected to hold: InvestCheck (πe 0), DenominationCheck, LandCheck/HousingCheck (Pe = 1 unbased).

## 4. Batch P3 (0.7.43, demand, clearing, index, assistance): exactly
1. **Household:** fields `need`, `foodMoney`, `groceriesGot`, `assistance` (per household); `plan()` sets need = `baskets()`, foodMoney = spendable + assistance voucher. `want`/`planned`/surplus untouched (§2.6).
2. **HouseholdBalance:**
   - `groceriesWanted(double price)`, `groceryDemandOf(Household, p)` (§2.6, reading `Retail`'s p_sat and ε), `allocateGroceries(sold, pAlloc, pCharged)`.
   - Assistance: `foodAssistanceFor(Household, price)` and `getFoodAssistancePaid()`.
   - Hunger (advanceMonth L~860) counts baskets: got + meals/90 against need. "Hungry at full shelves" uses q(p)/need.
   - The shop split (L~926) and `plannedShare()` (L3459) weight by got × households.
3. **Retail:**
   - `sellOwnPriced` per §2.7 (supply S, D, p*, allocation, trade at p).
   - `endOfMonth` → `repriceShelf(blendedCost)` with the §2.7 rule. The old scarcity rule is deleted; `MAX_SCARCITY_MULTIPLE`/`COMFORTABLE_DELIVERY` give way to `CLEARING_CAP`, `CLEAR_SPEED` θ 1/6, `FLOOR_CATCH_UP` .5, `SATIATION_MULTIPLE`, `GROCERY_ELASTICITY`.
   - New getters: `getClearingPrice()`, `getDemandAtPrice()`, `getSupplyBaskets()`, `getFloorPrice()`.
   - `setExpected(Pe, πe_m)` from Game before `strikeSectors` (L8305).
   - **`Retail.plan` (L457):** forecast baskets = D(floor) grown over the lead time, against S × (1 + TARGET_HEADROOM). A shop adds `t.getCoverage()` × operating rate baskets and earns that × (p − food price). This is the supply answer to a shortage (untested; P4 watches it). `wantedSpend`/`spendingCapacity` setters are removed or kept for the old screen lines, never read by the sale.
4. **Restaurants/LuxuryRetail:** `chargedMargin` (saved in extras, NaN on old saves), θ 1/6 on the margin; `getTargetMargin()`.
5. **RealEstate.repriceRent:** the πe drift term (§2.7) on both segments.
6. **PriceIndex:**
   - Components array, weights, bases, link L, `linkedAt`, and a trailing 12-month spend ring per component. `takeMonth(prices[], spends[], month)`, plus a services price from Game.
   - Getters `getWeight(k)`, `getRelative(k)`, `getLink()`, `getLinkedMonth()`.
   - Save array appended behind a marker (an old array restores the two-component index, then links on its first month). No `SAVE_FORMAT` bump.
7. **Food assistance:** TaxPolicy dial and state slot (appended); `TreasuryLine.FOOD_ASSISTANCE` (last); Game pays after the sale (`treasuryPays`); HouseholdAccounts outside money; `MoneyAudit` flow; DecisionLog; YearBook; HistorySave; PolicyPreview.
8. **Harnesses:**
   - `GroceryCheck` (new): demand bounded by heads; satiation and ε; budget cap; clearing bisection; allocation prices out the poorest; cap; θ and drift; floor catch-up; assistance raises the poor's share and is paid by the treasury to the cent; audit clean.
   - MonetaryCheck §1 (four-plus weights, chain-link continuity: the index and its year rate do not jump at the link).

**Premises expected to change in P3:**
- MonetaryCheck §2 ("a total shortage charges the ceiling"): ceiling = CLEARING_CAP × floor, reached at θ, not MAX_SCARCITY × cost-plus at once.
- §1 "the basket is fixed for good": now chained every 120 months.
- RestaurantsCheck/LuxuryRetail: the margin is "the fixed point" → "chases it".
- HouseholdCheck: hunger in baskets; the want is the discretionary plan.
- InvestCheck: Retail's planner.
- HealthCheck stays the known red.

## 5. Stability, and what the prototype measured
**Loop gains** (elasticity of the index to itself around each loop; "measured" where a run gives it):

| loop | gain | why it closes |
|---|---|---|
| wage → price → wage | P→W 1 (long run); W→P ≈ rent weight .3–.45 × labour share of build cost .01–.07 (measured) ≈ .03 | rent follows costs, not the unskilled wage (the old +737% attractor needed a wage-determined basket). Shelf and meal floors carry no wage (★13) |
| rent → build cost → wage | ≈ .02 | as above; land and non-labour cash follow Pe |
| currency → import → shelf | levels: import share of the index ≈ .4 (shelf food .2, luxury .15, materials .05) through parity reversion; rates: 0 from the drift (κπ* has no π in it); real-rate flow bounded by UIP to a level, 6.7% of fx per point of real gap | the Taylor rule raises the real gap 0.5–1.5 per point of π: appreciation |
| demand (money → margins) | levels only: luxury ≤ .15 × (4/2.6 − 1) = +8%, meals ≤ .2 × (8/5.5 − 1) = +9%, groceries ≤ wF × 1.5 | margin ceilings, appetite, satiation, capped weights |
| expectations | 1 − κ: .05 at KMAX, .75 at KMIN (a held rate drives κ there), a round being ~18 months | leaning holds κ; the drift pulls inflation to π* at κ a round |

- The **two attractors**:
  - Deflation is floored: πe ≥ −1%, and constants (60% of build cost, the shelf floor, land) drift at Pe, so measured runs had no deflation spell ≥ 12 months (A3), and 15% held gave −2.3% at worst.
  - Runaway needs the currency loop at a held rate. It is now bounded (below).
- **Exporters' thin margins:** costs (Pe) rising faster than the currency was the takeoff killer. Measured: P2 without the drift, takeoffs 2/4 and populations 26–54k; with it 105–134k and fx/parity 0.78–0.85.

**Measured, research cities, 240 months** (index start → year 20; inflation a year):

| run | 0.7.41 | P2 anchor (p2b) | P3 (p3h) |
|---|---|---|---|
| city2400 autopilot | 1.112→1.153 (0.2%) | 1.629 (1.9%), κ .80→.95 | 1.689 (2.1%) |
| city2400 0% held (slack, 20% unemployed) | 1.156 (0.2%) | 1.66 (2.0%) | 1.851 (2.6%) |
| city2400 15% held | — | 1–2%, unemployment 61% at yr 20 | −2.3%/yr for 3 yrs then 0; κ .26 |
| city600 autopilot / 0% | 0.906 / — | 1.35 (1.1%) / 1.445 | — / 1.948 (3.0%) |
| founding fixture, autopilot | peak 20.8%, 2.12, 1.5% at yr 20 | peak 17.8%, 1.98, 1.8% | peak 16.7%, then ~6% for 15 yrs, 2.84, 4.8% ✗ |
| founding fixture, 0% | peak 74.2%, 14.49 | peak 40.8%, 5.13 | peak 47.4%, 12.44 |

**Measured, the ensemble driver** (P1b's `Ensemble.java`, my copy; seeds 0–1 unless shown; 0.7.41 = P1b's seeds 0–3):

| variant | AUTO2 π from m240 | longest yoy>10% | final pop | 40k at | 0% m600–840 (twin) | 0% m42–282, 2nd half | after stop | shock step / tail | 15%: Δπ, Δu |
|---|---|---|---|---|---|---|---|---|---|
| 0.7.41 | ≈0.0% | — | 97–106k | 1220–1503 | 0.1–0.4% | — | — | — | — |
| P2 anchor only (4 seeds) | 2.01% | 10 m | 26–54k | 2/4 | 4.0% (2.0) | 0.1% | 4.8% | 4.5% / 1.6% | −0.9, +1.9 |
| P2g = batch P2 | 2.13% | 3 m | 105–107k | 1473–80 | 4.7% (1.9) | 1.5% | 6.3% | 9.0% / 1.6% | −1.1, +4.2 |
| P3d: full-πe drift, wage floor (4 seeds) | 2.86% | 14–68 m | 89–121k | — | **57.6%** | 18% | 5.6% | 9.9% / 4.7% | −4.4, +0.9 |
| P3h = batches P2+P3 | 2.64% | 14 m | 122–134k | 1339–1431 | 14.4% (4.4) | 5.6% (1st half 9.9%, the founding blip) | 2.8% | 12.6% / 3.6% | −5.2, +2.0 |
| P3h, 0% from m120 | 5-year blocks at 0%: 8.2/5.4/10.0/8.8% (twin 5.0/1.7/6.9/−0.9); after stop 2.0–2.2% | | | | | | | | |

- **Failures that changed the design** (all measured):
  1. Pe seeded at the index on old saves: constants +11% on load, then a unit-root chase. Now Pe = 1 on load, drift only, no level catch-up.
  2. KMIN .1 with credibility lost to a supply shock: the founding stuck at 10%. Now lean-aware loss, KMIN .25.
  3. No currency drift: the currency at 0.43–0.46 of parity, growth halved.
  4. Full-πe drift: hyperinflation at 0%.
  5. Unit labour cost in the shelf floor at a 5% margin: empty shelves, 81% hungry, 3.8–4.1% under the autopilot.
  6. Luxury at 63% weight.
  7. A one-month rebase gave a zero food weight in a crash month. Now a trailing 12 months.
- Other measurements:
  - Cap 2.0 → 1.5 took AUTO2 from 3.2% to 2.6%.
  - The clearing price in starved cities runs 5–20× the shelf (city2400: 3.3–12.6 against 0.45–0.67).
  - city2400 retail per basket: food .158 + payroll .138 + other .018 = .314 at a price of .399.

## 6. What the UI pass (P6) will need
- **Header/summary:** `getExpectedInflation()`, `getCredibility()` with words: "people expect 2.1% a year; they believe the bank (82%)", "trust is falling: inflation is 4 points over target and the rate is below the rule".
- **Policy/Money page:**
  - The rule's advice at this target, the neutral (1% real + target), and the real rate ex ante (`realDepositRate`, `realRateDifferential`).
  - The lean (`getLean()`): "leaning against it"/"not".
  - The currency's drift and its UIP level (`getExpectedDrift()`, `uipLevel()` = e^(−g/.15)).
  - QE's words: "buys term paper only; reaches prices through wealth and the currency".
  - The food assistance dial with preview cost and eligible households.
- **Shops (Retail lines):**
  - Price, floor, clearing price ("the price at which what people want equals what the shops can hand over"), demand at price, supply = coverage × operating rate with the binding ratio named (roads/power/health/staff), and the share priced out.
  - Scarcity = price/floor; "a price cannot fix this" when S ≪ D.
- **Restaurants/Luxury:** charged vs target margin.
- **People:** hungry by cause (priced out vs no stock), households on food assistance, baskets got per head by tier.
- **History:** series index (chained, with a flag at each link/rebase), components' relatives, weights, expected inflation, credibility, Pe, food assistance; named crises read the new index.
- **Year book:** the new columns of §3.9 and §4.7, and the weights per rebase.
- **Finances/Treasury:** the "Food assistance" line; Pe-struck fees and pensions shown "in today's money (founding × Pe)".
- **Words for the manual:**
  - "expected inflation", "credibility", "the anchor", "clearing price", "priced out", "supply the shops can hand over".
  - "Money constants follow what people expect prices to be, not what they were".

## 7. ★ decisions
1. Phillips = the existing level wage curve plus expectations; no new rate term. It already gives persistence once expectations go, and a second tightness channel would double it.
2. p_sat = 1.5 × the anchor price: keeps today's shelves inside the satiated band, so old saves do not jump from demand alone.
3. ε .4: the middle of the measured food-at-home range; the budget cap adds the poor's elasticity.
4. No wealth term in housing: a held high rate's interest must not bid up rent.
5. Keep `want`/`planned`/`isGoingShort()` as the discretionary plan: smallest change, and it avoids DenominationCheck's known share-path fault.
6. Luxury weight cap .15: honest weights made city2400's index 63% an import at fx × margin.
7. Real investment hurdle, floored at a quarter of the rate: the debt is nominal and the profits grow at πe. Measured neutral-to-positive (70–86k vs 75–77k).
8. Assistance eligibility at basket > half of after-fixed income: targets those priced out first.
9. The fare follows Pe: a dial keeps the worth the player gave it, like the minimum wage and the grant.
10. QE unchanged: do not make it matter artificially.
11. `investAbroad` unchanged: UIP bounds the leak; changing it to real returns raised the abroad share in the prototype's arithmetic.
12. Savers' trap bounded, not fixed: measured absent; P4 guards it.
13. No wages in the shelf or meal floors: measured to bankrupt shops and break the autopilot. Wages reach the index through rent and services.
14. Speeds: ADAPT 12 months, GAIN 60, LOSS 24, KMIN .25, KMAX .95, KSEED .80, TOL 1 point, MISS_SCALE 4 points, lean-aware loss. Credibility is hard to win and slow to lose while the bank fights. KMIN .1 was measured to lock the founding at 10%.
15. Pe is drift-only, seeded 1.0 on old saves: a level catch-up was measured to be a unit root with the floor.
16. Currency drift at κπ*, and UIP reversion .15 a year: the first keeps a 2% city at parity, the second stops a held 0% compounding (both measured).
17. CLEARING_CAP 1.5, θ 1/6, floor catch-up .5, rent θ 1/12: the 2.0 cap ran the autopilot at 3.2%; θ 1/6 is ~6 months to clear, slower than today's ¼.
18. Rebase every 120 months on trailing-year weights; chain-link at P3's first month.

## 8. Risks, and how P4's ensemble catches them
- **Grocery supply.** Shortages are endemic (operating rates .4–.8). Without the planner fix (§4.3, untested) hunger in big P3h cities reached .6 at month 2,400 against ~.25 today. P4: hunger median per policy against 0.7.41; S/D at the end.
- **Persistent ~5% in young cities and the founding** under the autopilot (PPP-reversion loop, gain ≈ .4 in levels). P4: A1/A2 per window (m240–600 separately). P5 lever: Pe-anchored parity, or a stronger saving response.
- **ZERO_SLACK at month 600 is not slack** (unemployment 0–56%, population ×3 in the window): 4.7% (P2) and 14% (P3) are partly a hot city. P4 should read S1 beside unemployment, and use city2400-like mature seeds or month 1,800 for "slack". ZERO_HOT from m42 overlaps the founding blip; start at m120 (the blocks above).
- **Old-save step:** shelves move from ~1.35× to ≤1.5× the floor over ~a year (city2400: index +2% in the first year against P2 alone); luxury/meals enter at the link. P4: the year rate across the link month.
- **Untested parts:** rent's πe drift, the services component, `Retail.plan`, the TreasuryLine and audit wiring of assistance (the prototype debited the treasury directly). P4 and the gate test them first.
- **DenominationCheck:** the clearing bisection and allocation are money-over-money ratios; whole baskets must cross at `floor` as now. P4: the gate.
- **K2-type tails** (shock tail 1.5–4.7%) and **takeoffs** (T1 against 0.7.41): two seeds only here; the 16 decide.
- **The savers' trap at 15%**: household net worth/GDP at the end of HIGH within 1.2× its twin.
