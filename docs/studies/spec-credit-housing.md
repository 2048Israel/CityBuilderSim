# Household credit and owning a home: the study, 2026-11-02

Read-only study of `main` at 0.9.2 (5602c6d0), for four of Jerus's items (2026-11-02): "credit cards should be a thing, an actual thing, and a thing population really does and struggles partially with, also along with this, the game should have stats such as median and avg debt per adult and stuff like that, same way the real government has, also a very very big change... allowing population to acquire mortgages and houses... idk how to implement that one tho. and let banks sell loans?"

Prototypes in `credit-housing/`, each a plain Python 3 file with no libraries (the PC has no numpy), and one Java timing compiled against `target/classes`:
- `spread.py`: the shape inside a cell and its closed forms, checked against draws (`out/spread.txt`).
- `cards.py`: credit cards on the cells, calibrated, with the levers and one of Jerus's saves read (`out/cards.txt`).
- `homes.py`: owning, the home market, the rate (`out/homes.txt`).
- `packages.py`: a package priced like a bond, and the catch (`out/packages.txt`).
- `CreditHousingTiming.java`: what it costs a month (`out/timing.txt`).

Each figure is one of four kinds: sourced, as [S*n*] (section 3, every source's licence allows a sold game to use it, or the figure is a rule of law cited as a fact); derived, with the sum shown; measured, by a prototype; or ★est., a named estimate with its reason (all listed in section 6). Saves were read with `tools.SaveDump` and `json`, never written.

## 1. Summary for Jerus

1. **Today a household borrows only when it cannot cover the shop, and every household in a cell owes the same.** So a cell is wholly in debt or not at all. Measured in your saves: in slot 3, 36 of 78 cells owe, and whole cells sit at the ceiling (an unskilled large family owes 186 against 2.6 saved) beside cells holding 100 to 290 months of savings; slot 6 owes nothing. Nobody "partially struggles", because there is no inside to a cell.
2. **The fix under all four items is a spread inside each cell.** The cell keeps its averages; the design assumes a shape around them: a share of the households holds the thing, and the amounts among them are lognormal, the way survey data are. Everything the month needs (who borrows, who falls behind, the median) has a closed form, so no household is ever drawn. Measured: the closed forms match 200,000 drawn households within 1%.
3. **Credit cards.** Each cell holds the share carrying a balance, the balance, the balance behind on its payments, and the share shut out. Unexpected bills put households with thin savings on the card; it costs 21.35% a year [S2] with a 3% minimum; whoever cannot meet the minimum falls behind; after about six months the bank writes it off, and your rule applies (wiped, shut out a year, a quarter leave). Calibrated on a survey-shaped city: 43% revolving (US 45% [S1], Canada 46% of cardholders [S4]), 0.58 months of take-home owed, 2.9% written off a year (US banks 3.8% [S3]). The poorest tiers revolve most and fall behind; the rich barely use it.
4. **Your levers on cards:** a rate cap (default Canada's 35% [S29]) and the minimum payment (2% to 5%; Quebec moved to 5% in 2025 [S6]). Measured, a trade-off: a 5% minimum cuts the share revolving from 43% to 38% but doubles the write-offs (2.9% to 5.8%), because more households cannot meet it.
5. **Debt statistics as StatCan and the Fed print them:** the share holding debt, the median and mean among holders by type (card, student, car, mortgage), per household and per adult, by tier and age band; debt to disposable income (Canada 176% [S10]); the debt service ratio (Canada 14.5% [S10]); the share paying over 40% of income (US 8.6% [S1]); late shares, write-offs, bankruptcies and forced sales. A median comes from the cells' shapes added together; struck once a year (surveys are yearly at best) and whenever the page is open.
6. **Owning a home.** Each cell holds the share owning, the mortgage per owner, its rate, its months left, and the share behind. Owners pay the mortgage, property tax and upkeep instead of rent. A buyer needs the down payment (5%, insured by the city under 20%, CMHC's premiums) and must pass the bank's test: housing costs under 39% of gross income at the contract rate plus 2 points [S18]. The home price, per person of capacity like rent, clears the month's buyers against the homes for sale: owners who leave or die, forced sales, the landlord (it sells a let home when owners pay more than the home is worth let, and buys when they pay less), and builders (they build for sale past the 33% margin).
7. **The rate hits housing.** Measured: 1 point on the rate lowers prices 6.6% (a Federal Reserve paper finds 6.9% [S16]); a 4-point shock takes 14% off in a year and 22% in the long run. Ownership barely moves, because prices fall as payments rise. The prototype city settles at 59% owning (Canada 66.5% [S11]) with 0.13% of owners behind (Canada about 0.2% [S13]).
8. **Banks selling loans.** A month's loans of one kind become a package: the buyer gets the interest and principal, bears the defaults, and the bank keeps a share and a servicing fee. It is priced like a bond, with defaults and early repayment. Buyers: the city's fund, investors abroad, and (a call) households through the bond market. The catch you named, measured: in the same rate shock, a bank that keeps its loans sees prices fall 10% and no crunch; one that sells 95% and keeps 5% sees losses 3.6 times larger, prices down 41%, and its lending cut to 13% of normal for six years, because it screened less, buyers stop buying when defaults jump, and its thin capital cannot hold what it can no longer sell. Making it keep 20% softens it: prices down 26% and lending to 30%.
9. **What this needs from the households study** (left in its notes): its flows carry the new stocks with the people like any other stock; ageing trades an owner up or down a door at the month's price; a death or a departure sells the home, the mortgage paid first. And from the wealth study: the spread of savings inside a cell. In your cities most cells hold 30 to 50 months of take-home (slot 7), so until savings are realistic few households would ever revolve.
10. **Cost, measured.** As designed, cards and homes take 0.2 ms a month at today's 78 cells and 0.9 ms at 390 (five age bands), on a PC whose other sessions held the CPU at 100%. A real month of the playtest city at month 1,260 takes 28.7 ms, so that is under 1% of it today. The medians take 0.2 to 0.8 ms more, struck once a year. Done the plain way, every month, the medians would cost 6.6 ms at 390 cells.
11. **Seven batches:** HC1 cards, HC2 the debt statistics, HO1 owning and the home market, HO2 owners through life (on the households study's flows), HO3 the screens, HO4 the levers on Policy, LP1 banks selling loans. HC1 bumps the save format; HO1 and LP1 need no bump. Section 7.

**Your calls** (section 5, each with a recommendation; the question boxes carry the facts): C1 the card's rate and minimum, C2 one down-payment share or Canada's tiers, C3 the landlord sells and buys homes (no new sector), C4 the rate's pass-through to the landlord, C5 who buys packages, C6 the retention rule, C7 the order against the households study.

## 2. The design, on the code

### 2.1 The spread inside a cell (new `CellSpread`, pure)
Every money figure in a `Household` is per household of its cell, and every household of the cell is the same ([Household.java](../../src/main/java/ham/citybuildersim/Household.java) header). The design keeps that and reads a shape around it:

- **A holding share and a lognormal.** For a debt of kind *d*, the cell holds *p* (the share of its households that owe any) and *D* (owed per household of the cell). Among holders the amount is lognormal with mean *D/p* and a fixed σ, its log-mean μ = ln(*D/p*) − σ²/2.
- **The closed forms** (`spread.py`, `CellSpread`), with Φ as `BusinessDebtManager.normalCdf`, "the one place the game computes it":
  - share at or below *x*: Φ((ln *x* − μ)/σ);
  - what those whose buffer *S* is under a gap *g* must borrow, E[(*g* − *S*)⁺] = *g*Φ(−*d*₂) − *S̄*Φ(−*d*₁), the put's formula;
  - the part of the mean held above *x*: *m*Φ((μ + σ² − ln *x*)/σ).
- **Checked:** against 200,000 draws on six shapes, the worst error is 0.9% (`out/spread.txt`, section 1).
- **The σ's, derived from the SCF's mean over median [S1]:** σ = √(2 ln(mean/median)).
  - Cards: √(2 ln(6.1/2.7)) = 1.28 (2022); 2025's 8.0/3.1 gives 1.38.
  - Home-secured debt: √(2 ln(239.7/183.6)) = 0.73 (2025).
  - All debt: √(2 ln(179.1/88.1)) = 1.19 (2025).
  - Savings inside a cell: ★est. 1.5 until the wealth study gives it (section 2.7).
- **Why it matters, measured (`out/spread.txt` section 3):** a cell owing two months of income on average has nobody over three months today. With 45% holding and σ 1.28, 16.6% of it owes over three months and 8.6% over six.

### 2.2 Credit cards (`Household`, `HouseholdBalance`, `Bank`)
**The stocks,** per household of a cell. `debt` stays, now the card balance. New: `revolving` (the share carrying a balance), `lateDebt` and `lateShare` (behind on payments), and `shutOut`, a share that replaces the cell-wide `int lockout`.

**The month,** inside `Household.settle()`, which keeps its waterfall: payslip, then fixed bills, then food, then savings, the city's paper, the paper abroad, bonds, shares, then credit, then going without.
1. **Interest.** The card rate is `Bank.householdRate(policy)` + `CARD_SPREAD`, never over the policy's cap, plus today's `RISK_SLOPE` on the revolvers' own months owed. `CARD_SPREAD` is set at HC1 so the rate is 21.35% at a neutral policy rate [S2] (US: 20.94% on all accounts [S7]).
2. **The minimum** is a fixed bill, after rent and before food: max(`MIN_PAYMENT` × balance, interest + 1% × balance). `MIN_PAYMENT` is 3%, the common issuer practice [S5]; the US norm is 1% plus interest [S8].
3. **Shocks** (★est., calibrated): each month a sixth of the households not revolving meet an unexpected bill of three quarters of a month's take-home. Those whose buffer is under it put the difference on the card (the put's formula on the savings spread); the rest pay from savings.
4. **The shortfall** of today's waterfall, spending over income, goes on the card the same way: by the savings spread rather than by the cell's average.
5. **Falling behind.** A revolver whose minimum exceeds what is left after its fixed bills and food is at risk. The balance-weighted share over that line is a year's chance of falling behind, a twelfth of it moving to `lateDebt` each month. The shape is put back every month, so moving the whole tail at once would move it again next month; `cards.py`'s first draft did, and wrote off 53% a year.
6. **Paydown.** A current revolver pays the minimum and `PAYDOWN_SHARE`, 12% (★est., calibrated), of what is spare beyond it. One whose whole balance is under that clears it and stops revolving: the share under the payment, a closed form.
7. **The late stock** cures at 15% a month (★est.) or is charged off at a ninth a month. The ninth is derived: one stock standing for the six monthly buckets to the 180-day rule [S28], with the hazard that gives the Fed's ratio of charge-offs (3.82%) to delinquency (2.85%) [S3], 12 × 2.85 / 3.82 = 9 months.
8. **A charge-off is your discharge, per share rather than per cell.** The bank writes it off against `householdAllowance`. That share of the cell loses its savings (it has little), is shut out for `LOCKOUT_MONTHS`, and `LEAVE_ON_BANKRUPTCY` of it leave. `BANKRUPT_AT_MONTHS` and the cell-wide trigger go.

**Calibration** (`out/cards.txt`, the survey-shaped city of six tiers with buffers of 0.5 to 8 months, 20 years from no debt):

| | measured | real |
|---|---|---|
| households revolving | 43.2% | 45.2% (SCF 2022), 44.7% (2025) [S1]; 46% of Canadian cardholders [S4] |
| balance owed by revolvers | 0.58 months of take-home | ★est. 0.6: the SCF's mean balance among holders, $6.1k, against a family's take-home |
| behind | 1.6% of balances | 2.85% delinquent [S3]; 2.59% of Canadians without a mortgage 60+ days late on cards [S7a] |
| written off | 2.9% a year | 3.82% [S3] |
| by tier | unskilled 71%, elite 9% | the SCF's peaks in the middle: 30% in the bottom fifth of income, 57% in the middle, 27% at the top [S1] |

**The bottom tier's access.** The SCF's bottom fifth revolves less than its middle because lenders refuse it [S1]; the model has no refusal yet. HC1 adds the bank's card test: a card for a household whose income passes a floor, the shut-out excepted. Its share is ★est. 60% in the unskilled tier and 95% above, to be fitted to the SCF's shape at HC1.

**The levers** (measured, `out/cards.txt` section 2):
- A rate cap at 12% leaves 41.6% revolving and 2.5% written off: the rate matters less than the shocks.
- A 5% minimum: 37.6% revolving, 5.8% written off.
- A 2% minimum: 43.9% revolving, 2.6% written off.
- A bad decade, every take-home 15% lower: 48.6% revolving, 5.6% written off.

**In a real save** (slot 7, its savings as they are): 8.2% revolve. The students and seniors living alone, who have no buffer, revolve at 61% and 12% of them fall behind. The families, holding 30 to 45 months of savings, revolve at 2 to 4%.

### 2.3 Owning a home (`Household`, `HouseholdBalance`, new `HomeMarket`, `RealEstate`, `Bank`)
**The stocks,** per household of a cell:
- `owners`: the share owning;
- `ownedCapacity`: persons of capacity owned per owner;
- `mortgage`: per household, so that it carries like money;
- `mortgageRateDollars` and `monthsLeftDollars`: the rate and the months left, each times the balance, so they carry by balance (read as the sum over `mortgage`);
- `arrears`: the share of owners behind.

The city holds one `HomeMarket`: `price` per person of capacity, family doors and studios apart as the two rents are; the owned capacity of each segment; and the builders' for-sale pipeline. A home is never a building object. Doors stay the landlord's buildings, and an owned door is a share of a segment's capacity, as rent is per person of capacity.

**Where in the month:** after `motoring.month(this)` (`Game.nextMonth`, step 34 in [month-order.md](../month-order.md), L13420) and before the luxury counter. Like a car, a home is bought out of what the month left. It goes in `SimulationEngine.simulateMonth()` at the same place.

**Owners' bills replace rent.** `HouseholdBalance` hands each cell its rent as `rentShare` × (1 − `owners`). The owners pay three bills:
- the level annuity on their mortgage at its rate over its months left;
- property tax: `TaxPolicy.getPropertyTaxRate()` × the home's value / 12, to the treasury, as the landlord pays it on its stock;
- upkeep: `RealEstate.MAINTENANCE_PER_YEAR` × value / 12, placed as a repair order with the builders as the landlord's is.

`RealEstate` bills rent on the renters' weight alone, and its property tax and upkeep fall with the capacity it still owns. Its planning still counts every door, owned or let: a home is a home.

**The rules** (Canadian, as the landlords' mortgages are [S18]; constants on a new `HomeLoan` beside `Mortgage`):
- **Down payment.** A 5% minimum. Under 20% down the loan must be insured, by the city as CMHC is by Canada. The premium is added to the loan and paid to the treasury: CMHC's schedule by loan-to-value, 2.40% to 80%, 2.80% to 85%, 3.10% to 90%, 4.00% to 95% [S19]. This reuses `Mortgage`'s insurance path (`Game.runPrivateInvestment()` pays the bank an insured loss).
- **The test.** Housing costs (the payment at the stress rate, the property tax and heat) under 39% of gross income (GDS); all debt payments under 44% (TDS).
- **The stress rate** is the greater of the contract rate + 2 points and a floor [S18]. Canada's floor is 5.25%, a nominal level this game's moneys need not share: call C2.
- **Amortization and term.** 25 years insured, 30 for a first buyer since December 2024 [S18]. The rate is fixed for five years: a sixtieth of each cell's book renews at the day's rate each month.

**Buyers.** In each cell they are `BUY_PACE` (★est., a third of the gap a year) × (wanted share − share owning) × the share passing both tests at the price. Both shares come from the spreads:
- the savings over the down payment and closing (σ 1.5);
- the gross income over what the test needs (σ 0.35, ★est., pay inside one tier and stage).
- They are taken as independent (★est.; they are positively related, so this understates a little).

**The wanted share** is by life stage: young 36%, established 68%, retired 80% [S14][S15]. Until the households study's stages exist (HO1 before HO2), it is by shape: singles and flatshares at the young figure, the couples and families at the established one, the two retired shapes at the retired one.

**The price** (`HomeMarket.clear()`). P* sets the month's buyers' capacity equal to everything for sale:
- the owners who leave (EXIT, ★est. by stage, from the households study at HO2);
- the forced sales;
- the landlord's net sales;
- the builders' finished homes.

It is found by false position from last month's price, a dozen steps at most (measured, section 4), and the price walks `PRICE_LAG_MONTHS` (12, ★est., the lease's lag) toward it. Three things bound it:
- **The landlord's value of a let home:** V_L = net rent × 12 / cap rate, with cap rate = neutral cap + `PASS` × (the insured mortgage rate's move from neutral), PASS = 0.4 (call C4). Over V_L the landlord sells let homes, up to `SELL_PACE` (★est. 0.4%) of its stock a month at a 10% gap; under it, it buys from sellers.
- **The builders:** past `RealEstate.BUILD_MARGIN` over the cheapest home's cost, they start homes for sale. They finish after 18 months (★est.).
- **The buyers' tests**, which tighten with the rate.

**Arrears.** An owner at risk is one whose take-home is under its bills and a basket, and whose savings cannot carry the gap for six months (★est.). That share is a year's chance of falling behind, as cards' late line is.
- Arrears cure at 8% a month (★est.); nine months behind (★est.), the home is sold into the market.
- What the sale cannot repay is the insurer's on an insured loan (the treasury, as for the landlords) and the bank's otherwise.
- The household rents from then on, and is shut out of credit as a card charge-off shuts it out.

**Measured** (`out/homes.txt`; a survey-shaped city of six tiers × four stages with two earners in a family, opened to owning from all-renting, 40 years):

| | measured | real |
|---|---|---|
| owning, year 5 / 20 / 40 | 27% / 56% / 59% | 66.5% Canada 2021 [S11]; 65.5% US 2026 [S14] |
| young, family, middle-aged, retired (college tier; wanted 40, 66, 76, 80%) | 25%, 56%, 76%, 80% | 36%, 61-70%, 74%, 80-82% by age [S15] |
| owners behind | 0.13% | about 0.2% before the pandemic, 0.5% at the 2008 peak [S13] |
| a home for four over the median family's gross | 2.1 years | not a target: the game's own rent anchor (section 6, risks) |
| 1 point on the policy rate, 10 years on | price −6.6% | −6.9% a point (metro average) [S16] |
| 4 points at once, after 1 / 2 / 10 years | price −14% / −19% / −22% | |

The young own least, 15 to 29% across the tiers against the 36% the surveys find: what their savings put down against the price. The households study's gifts at purchase (an estate's cash reaching the young) would change that. Section 6 names it.

### 2.4 Owners through life (on the households study's flows, HO2)
The households study's note (shared folder, 2026-11-02) says its flows (ageing, retiring, dying, leaving) carry the per-grown-up average of every stock in `HouseholdBalance.stocks()`. Each new stock above is one line there. The rate and the months left carry as balance-weighted sums, and the shares as counts. Three rules are this study's:
- **Trading up and down.** A household that moves into a stage needing another door (young to established: 2 to 4 persons of capacity; established to retired: 4 to 2) trades at the month's price. Up, it borrows the difference if it passes the test, and sells and rents if not. Down, it sells the spare capacity, pays the mortgage first and banks the rest. `homes.py` runs these trades on its four stages. Even with them the owners' book ages: mortgages over take-home peak at 116% in year 10 and settle at 34% by year 40, because the prototype has no inflation and no moves inside a stage (section 6).
- **An estate sells the home.** It sells into the month's market, the mortgage is paid first (the households study's rule), and the net equity is the estate's cash for heirs. A home is never inherited as a home: the heirs have one of their own, or rent.
- **Leaving the city** sells the same way, and the net goes with the leavers, as the households study's migrants take their money.

The Fed finds that owners locked into a low rate move less: 44% of the 2021-22 fall in moves came from the gap between their rate and the market's [S25]. A later batch may slow the trades by that gap; it is not proposed here.

### 2.5 Banks selling loans (new `LoanPackage`, `Bank`, `TreasuryFund`, `ForeignAccounts`)
**The instrument.** A package is one month's new loans of one kind: insured mortgages, uninsured mortgages, or card balances. It is a pass-through:
- the buyer is paid the loans' interest less the bank's servicing fee (★est. 0.25% a year on mortgages, 2% on cards), and their principal as it is paid and prepaid;
- the buyer bears the defaults less recoveries, which on an insured package is the city's guarantee (the CMHC-backed NHA MBS shape);
- the bank keeps `RETAIN` of each package.

**The price** (`packages.py` part 1) is the cash flows at the buyer's yield, the city's curve (`YieldCurve`) at the package's average life plus a spread for its kind:

| buyer's yield | CDR 0% | 0.3% | 1% | 3% |
|---|---|---|---|---|
| 3.50% | 102.87 | 102.58 | 101.91 | 100.10 |
| 4.05% | 100.89 | 100.61 | 99.98 | 98.27 |
| 4.50% | 99.30 | 99.04 | 98.44 | 96.80 |

(Per 100 of balance: a 4.55% coupon, 25-year amortization, five-year term, 8% a year prepaid, 75% recovered; prepayment moves the price by 0.3 at most here.)

**Buyers.**
- The city's fund, by a rule (it buys insured packages within a share of its assets) and by the player's hand, as it buys bonds.
- Investors abroad, at the world rate plus a spread: a financial inflow when they buy, an income outflow as they are paid.
- Households through `BondMarket`, call C5.
- The CMB spread Canada's purchases show, 12 to 29 points over its government curve in 2026 [S23], is the insured package's spread at HO's start (★ to confirm in play).

**When the bank sells.** It sells when the price is at par or better, or when its capital rule binds (`Bank.lendingGrowthLimit()`). A sold loan leaves its weighted book, and the share it keeps stays.

**The catch, as a rule.**
- The bank's laxity is the share of a new loan's risk it will not bear: λ = (share it has been selling) × (1 − `RETAIN`).
- Its card and uninsured mortgage approvals widen by `LAX_VOLUME` × λ: 30% at full laxity (★est.).
- The loans it makes default `LAX_PD` × λ more: 25% (★est., the top of Keys et al.'s 10-25% [S20]; disputed, so to confirm).
- They default harder when prices fall, because they were lent on less equity: a fall's effect × (1 + 4λ) (★est.).
- Buyers price on the last two years' defaults against the last ten. A quarter over what they are used to and they pay less; half over and they pay under par, where the bank stops selling.
- The bank's capital is a quarter over the book it keeps (★est.), so a bank that sells holds little.

**Measured** (`packages.py` part 2: 30 years, a rate shock in year 15 knocking prices down a tenth, the fall `homes.py` measured for 4 points):

| bank | losses | bank's share | deepest price fall | its lending at its least | months buyers stayed away |
|---|---|---|---|---|---|
| keeps everything | 1.84 | 100% | 10.0% | 100% | 0 |
| sells half, keeps 5% of each | 2.80 | 57% | 10.0% | 88% | 30 |
| sells 95%, keeps 5% | 6.61 | 15% | 41.5% | 13% | 77 |
| sells 95%, must keep 20% | 4.70 | 34% | 25.9% | 30% | 73 |
| sells 95%, keeps nothing | 7.40 | 8% | 46.0% | 6% | 75 |

Without the shock, a selling bank looks well: losses 55% higher but borne by buyers, and no visible trouble (`out/packages.txt` 2b). That is the trap.

**Real anchors.**
- In the US, 64% of mortgages on 1-4 family homes are in agency and GSE pools and 3.8% in private-label pools (21.6% at the 2007 peak) [S22].
- The US rule makes a securitizer keep 5% [S21].
- Canada's insured pools (NHA MBS) are about 12% of its mortgages, derived from figures not verified on a primary page; so not a target.

### 2.6 The statistics (new `DebtStatistics`, pure; the Household money page; `HistorySave`)
**What the offices print** [S1][S9][S10][S27]:
- the share holding debt, overall and by kind;
- the median and mean among holders, by kind, age band and income group;
- debt to disposable income (StatCan's headline, 176.4%) and debt to assets;
- the debt service ratio (Canada 14.52%, US 11.11% [S17]);
- the share paying over 40% of income (US 8.6% of families, 11.2% of debtors [S1]);
- the share late 60+ days, delinquency and charge-off rates by kind [S3];
- bankruptcies, and the homes sold in arrears.

**What the game prints, in the same words:** each of those, per household and per adult (a household's debt over its grown-ups, ★est. an even split, as the SCF and SFS count by family and the credit bureaus by person), by tier and, after the households study, by stage.

**The medians.** The city's distribution of a kind is the cells' shapes added together (`spread.py` section 2). Bisection on it matches a draw of 200,000 households to 0.05 to 1%. The design reads the CDF on a 32-point log grid in one pass and every quantile off it (section 4). Struck once a year into `HistorySave`, as the surveys are yearly at best (the SCF every three years), and whenever the page is open.

**Total debt.** A household's total is a sum of kinds, and a sum of lognormals is not one. The holder shares are taken as independent and each holder's total as one lognormal of the sum's mean and σ 1.19 [S1] (★est.). HC2's harness holds it to a draw.

### 2.7 What this asks of the other studies
Asked of the households study (also in the shared notes, `credit-housing.md`):
1. carry the new stocks (section 2.4);
2. the owners trade up and down on its ageing flow, by this study's rule;
3. an estate sells the home, the mortgage paid first (its rule, agreed);
4. leavers sell and take the net;
5. the wanted ownership by stage (sourced above).

Asked of the wealth study:

6. the spread of savings inside a cell. Cards, down payments and arrears all read it. In Jerus's slot 7 most cells hold 30 to 50 months of take-home and slot 3 has cells at 100 to 290, so with today's savings cards would barely happen; that is a finding about savings, not about cards.

### 2.8 Money, the audit, the bank's book
| flow | from | to | note |
|---|---|---|---|
| card interest, card fees | households | bank | interest income; `Bank.LOAN_FEE` on new card credit as today |
| card charge-off | bank's allowance | - | a write-off, as today's discharge |
| down payment, closing | buyer's savings | seller (households, landlord or builder) | closing costs to the bank and the treasury (★est. 1.5%) |
| mortgage advanced | bank | seller | a new household loan on the bank's book |
| insurance premium | added to the loan | treasury | as `Mortgage.PREMIUM_RATE` is today |
| mortgage payment | owners | bank | interest is the bank's income; principal lowers the book |
| property tax, upkeep | owners | treasury; builders' repair order | as the landlord pays them |
| forced-sale shortfall | treasury (insured) or bank | - | `receiveInsuranceClaim()` as for the landlords |
| a home sold by the landlord | buyer | Real Estate's till | a gain or loss on the capacity's book value |
| package sold | buyer | bank | the loans leave its book; RETAIN stays |
| package payments | bank (passing through) | holders | abroad: the income account; the fund: its income |

Every new line has its `MoneyAudit` counter, as the paper desk's and the bond market's do.

### 2.9 Saving and loading
- **HC1:** the cells gain `revolving`, `lateDebt`, `lateShare` and `shutOut`, appended to `toCellSaveArray()` with a `CELL_SLOTS_BEFORE_CARDS` constant, as every stock before them was.
  - An old save has a balance (`debt`) and no share revolving. That reads wrongly (a balance with no holders), not merely incompletely.
  - So HC1 bumps `SAVE_FORMAT` and raises `OLDEST_LOADABLE_FORMAT` to it, and says so in its notes, by the standing rule. No conversion code.
- **HO1:** owners at zero, the old save's state, is everyone renting, which is today's game. No bump.
- **LP1:** no packages in an old save. No bump.
- `ReadPathCheck` and `SaveFileCheck` take each new field, and `HistorySave` the yearly statistics.

## 3. The figures and their sources
Licences per `licences.md`: US federal works are public domain; Statistics Canada's Open Licence allows sale with "Adapted from Statistics Canada"; the Bank of Canada allows commercial use with credit, and a product for sale must say the data is free on the Bank's site. A rule of law is a fact, cited for its value whatever its page's licence (CMHC's, OSFI's and Canada.ca's pages are non-commercial, so their values are cited, never their pages or tables). A paper cited as evidence is a fact about the world; its data does not ship.

| | figure | source | licence |
|---|---|---|---|
| S1 | SCF: 77.0% hold debt, median $88.1k, mean $179.1k; cards 45.2% / 44.7%, median $2.7k / $3.1k, mean $6.1k / $8.0k; home-secured 42.0%, median $183.6k, mean $239.7k; ownership 65.6%; payments over 40%: 8.6% of families, 11.2% of debtors; cards by income fifth 29.8, 46.4, 57.0, 56.0, 41.1 (80-90th), 27.4% (top tenth) | Federal Reserve Board, SCF 2022 and 2025 (scf23.pdf, scf26.pdf, Tables 4, 5; scf2025_tables_public_nominal_historical.xlsx, Tables 9, 13, 18) | public domain |
| S2 | card rate 21.35%, chartered banks, Aug 2026 (series V122667812) | Bank of Canada, interest rates for new and existing lending | BoC terms: yes, with credit |
| S3 | cards: delinquency 2.85%, charge-off 3.82% (2026 Q2, SA); residential real estate delinquency 1.86% | Federal Reserve Board, charge-off and delinquency rates | public domain |
| S4 | 46% of cardholders carry a balance 2+ months; median carried $1,150 | Bank of Canada, Staff Analytical Note 2024-18 | BoC terms |
| S5 | no federal minimum; issuers ~3% or $10 | FCAC, paying off a credit card | a fact |
| S6 | Quebec's minimum 5% since 2025-08-01 | Quebec Consumer Protection Act (FCAC; BoC SWP 2024-26) | a rule |
| S7 | G.19: 20.94% all accounts, 22.15% assessed interest (2026 Q2) | Federal Reserve Board, G.19 | public domain |
| S7a | card arrears 60+ days: 2.59% without a mortgage, 1.18% with (Apr 2025) | Bank of Canada, FSR 2025 chart data (Valet) | BoC terms |
| S8 | revolve rate 49-50% of active accounts; ~15% pay only the minimum; minimum usually 1% + interest | CFPB, Consumer Credit Card Market Report 2025 | public domain |
| S9 | SFS 2023: median debt of holders $100,000; mortgage $205,000; card and instalment $4,500; student $14,000; vehicle $20,000; 39% hold a mortgage | Statistics Canada, table 11-10-0016-01 (The Daily, 2024-10-29) | Open Licence |
| S10 | debt to disposable income 176.4%, debt service 14.52% (2026 Q2) | Statistics Canada, tables 38-10-0238-01, 11-10-0065-01 | Open Licence |
| S11 | ownership 66.5%; 25-29: 36.5%, 30-34: 52.3%, 70-74: 74.8% | Statistics Canada, Census 2021 (The Daily 2022-09-21; table 98-10-0232-01) | Open Licence |
| S12 | 58.3% of owners have a mortgage (2018) | Statistics Canada, Canadian Housing Survey | Open Licence |
| S13 | mortgages 90+ days in arrears: 0.2% before the pandemic, 0.5% at the 2008 peak | Bank of Canada, staff analytical paper 2026-12 | BoC terms |
| S14 | ownership by age: under 35 36.6%, 35-44 60.1%, 45-54 70.4%, 55-64 75.4%, 65+ 79.1% | US Census Bureau, HVS 2026 Q3 | public domain |
| S15 | SCF 2025 ownership by age: 35.6, 61.2, 69.8, 73.6, 79.8, 82.3% | Federal Reserve Board, SCF 2025 tables | public domain |
| S16 | house prices −6.9% per point of the real mortgage rate (metro average) | Rappoport, FEDS 2016-081 | a Board working paper; evidence |
| S17 | household debt service 11.11% (2026 Q2) | Federal Reserve Board, DSR | public domain |
| S18 | down payment 5% / 10% / 20% to $500k / $1.5M; insurance under 20%; 25 years insured (30 for first buyers since Dec 2024); GDS 39%, TDS 44%; stress test: the greater of contract + 2 or 5.25% | FCAC, Department of Finance (2024-09), CMHC, OSFI B-20 | rules |
| S19 | CMHC premiums: 0.60% to 65%, 1.70% to 75%, 2.40% to 80%, 2.80% to 85%, 3.10% to 90%, 4.00% to 95% | CMHC, premium information | a published price, cited as a fact (as `Mortgage.PREMIUM_RATE` already is) |
| S20 | loans easier to securitize defaulted 10-25% more | Keys, Mukherjee, Seru, Vig (2010), QJE 125(1) | evidence; disputed (Bubb and Kaufman) |
| S21 | a securitizer keeps 5%; qualified residential mortgages exempt | Regulation RR, 12 CFR 244 (Fed press release 2014-10-22) | law |
| S22 | 1-4 family mortgages: 64.3% agency and GSE, 3.8% private-label (21.6% in 2007 Q2) | Federal Reserve Board, Z.1 F.4.5a | public domain |
| S23 | CMB purchase spreads 12-29 bp (2026) | Bank of Canada, CMB purchases | BoC terms |
| S24 | 59.7% of owner-occupied homes have a mortgage (2024) | US Census Bureau, ACS | public domain |
| S25 | 44% of the 2021-22 fall in moves came from the rate gap | FEDS 2024-088 | a Board working paper; evidence |
| S27 | StatCan's DTI and DTA, defined | Statistics Canada, 36-28-0001 (2026) | Open Licence |
| S28 | an open-end retail loan is charged off at 180 days past due | FFIEC, Uniform Retail Credit Classification and Account Management Policy (2000) | a rule |
| S29 | the criminal interest rate, 35% APR since 2025-01-01 | Criminal Code of Canada, s. 347 | a rule |

**Not used,** for their licences: the NY Fed's Household Debt and Credit Report (Equifax data; business use only with its credit, and the data is a bureau's); the Canadian Bankers Association's arrears (commercial use for its members only); Equifax and TransUnion; CMHC's dashboard (non-commercial); FRED as a conduit.

## 4. What it costs a month
Measured by `CreditHousingTiming.java` (`out/timing.txt`): the kernel of sections 2.2, 2.3 and 2.6 on cells shaped like a city's, a median of 7 rounds of 1,000 months after 2,000 to warm, on Jerus's PC with other sessions running.

| cells | cards + homes, price by bisection (40 steps) | + three medians by bisection | as designed: price by false position | + medians off a 32-point grid |
|---|---|---|---|---|
| 78 (today) | 0.60 ms | 1.34 ms | 0.20 ms | 0.43 ms |
| 390 (five age bands) | 2.89 ms | 6.65 ms | 0.88 ms | 1.64 ms |
| 1,560 | 12.0 ms | 26.6 ms | 3.25 ms | 8.25 ms |

The runs shared the PC with five other sessions, which held its 20 threads at 100%. So the absolute times are high: the same file's first run, on a quieter machine, gave 0.41 and 1.22 ms at 78 cells. Each row's columns ran under the same load.

- **A real month** of LongPlaytest's city on `GameFiles.scratch` (automatic building, month 1,260, 334,245 people, 78 cells) takes 28.7 ms, a median of 60 months.
- **Cards and homes as designed** are 0.7% of that month at today's 78 cells (0.20 / 28.7). At 390 cells the month itself grows, and they would be about 3% of today's.
- **The price** by false position from last month's price takes a dozen passes over the cells at most, against bisection's 40. That is the 3x between the first and third columns.
- **The medians** off a 32-point grid match bisection within 0.23% (card −0.03 to +0.02%, mortgage −0.23 to +0.08%). Struck once a year, they cost 0.02 ms a month at 78 cells, 0.06 at 390 and 0.42 at 1,560, averaged over the year. When the page is open they cost 0.2 to 5 ms once a month.
- **Packages** are a list of at most 300 live months (25 years) of a few figures each, a few thousand operations a month: not measured, and not worth it.

## 5. The calls

**C1. The card's rate and its minimum.**
- Recommended: 21.35% at a neutral policy rate (Canada's banks [S2]), moving with the bank's household rate, capped at Canada's 35%.
- The minimum: 3%, issuers' practice in Canada [S5]. Quebec's 5% is the lever's other end; 2% (or the US's 1% plus interest) the near end.
- Measured: a higher minimum means fewer revolve and more fall behind.

**C2. One down-payment share, or Canada's tiers.**
- Canada's tiers are in dollars (5% to $500k, 10% to $1.5M, 20% above [S18]), and the game has its own moneys and price level.
- Recommended: one 5% minimum, insured under 20%, with CMHC's premiums by loan-to-value.
- The stress floor (5.25%) is in the same position: only the + 2 points, no floor.

**C3. Who sells homes.**
- Recommended: Real Estate, as landlord and builder. It sells let homes when owners pay more than the home is worth let, buys when they pay less, and builds for sale past its margin. No new sector, and no change to `Sectors.KEYS`.
- The alternative is a Developers sector apart from the landlords: more to build, and a second planner.

**C4. How much of a rate move reaches the landlord's value of a home.**
- Recommended: 0.4. Measured −6.6% a point, against the Fed paper's −6.9 [S16].
- 0 leaves prices to the buyers alone (+0.1% for a point); 1.0 gives −15% a point and −26% for four points.

**C5. Who buys the packages.**
- Recommended: the city's fund and investors abroad at LP1.
- Households through `BondMarket` later: an amortizing pass-through is not the fixed-coupon bullet the bond market and the households' bond holdings are built for.

**C6. The retention rule.**
- Recommended: the player sets it on Policy, from 0 to 100%, default 5% (the US rule [S21]).
- Measured: 5% leaves the catch in full (prices down 41%, lending to 13%); 20% softens it (26%, 30%).
- Insured packages need none, since the city bears their losses.

**C7. The order against the households study.**
- Recommended: HC1 and HC2 now; they need nothing from it but read better once savings are realistic.
- HO1 on today's cells, with the wanted share by shape. HO2 after the households study's flows batch. LP1 last.

## 6. Still to decide, risks, and what to measure first
**★est. to confirm** (none blocks a batch):
- Cards: the shocks (a sixth a month, 0.75 months); the paydown share (12%); the cure (15%); the bottom tier's access (60%).
- The savings spread (1.5) and the income spread (0.35).
- Homes: the buy pace (a third of the gap a year); the exits; the landlord's pace (0.4%) and band (10%); the builders' pace and 18 months; the price lag (12); arrears' six months of savings, 8% cure and nine months to a sale; closing costs (1.5%); heat (1% of gross).
- Packages: servicing (0.25%, 2%); LAX_PD (25%), LAX_VOLUME (30%), the fall's ×(1 + 4λ); the buyers' quarter and half; the bank's capital a quarter over its kept book; recovery (75%).
- Statistics: the per-adult even split; the total-debt lognormal.

**Risks:**
- **Savings decide cards.** With the saves' savings few revolve (slot 7: 8%). HC1 is right, and quiet, until the wealth study's spread lands.
- **The game's homes are cheap against income:** a home for four is 2.1 years of the median family's gross in the prototype, against roughly 4.6 in the US (SCF 2025's owners' median home, $375k, over its median income). That is rent's anchor (`TARGET_RENT_BURDEN`, `LANDLORD_YIELD`), not this design. Mortgages over take-home land low with it (34%, against Canada's mortgages of about three quarters of 176%). Measure at HO1; any change is the landlords', not this study's.
- **Ownership lands under the real figure** (59% against 66.5%), most of the gap in the young (15 to 29% against about 36%). The households study's gifts at purchase and the 30-year first-buyer amortization (+0.3 points, measured) close part of it.
- **The rate's force** rests on C4's pass-through and one paper's metro average [S16]. Watch the ensemble's house price path at HO1.
- **Scale.** The kernel is O(cells). The price is a dozen passes over the cells; the medians, once a year, 32. At 1,560 cells it stays under 3.3 ms a month as designed, under load.
- **The month's order:** HO1's line goes in `Game.nextMonth()` and `SimulationEngine.simulateMonth()` at the same place, the classic bug here.

**Measure first:**
1. HC1's share revolving, behind and written off on LongPlaytest's city and on copies of Jerus's saves (slots 3 and 7, copied to `GameFiles.scratch`), against section 2.2's table.
2. HO1's ownership, price over income and arrears against section 2.3's table.
3. HO1's price after a 4-point move.
4. LP1's losses and crunch against section 2.5's table.

## 7. Proposed batches
Names checked against `QUEUE.md`, `docs/notes/`, `docs/queue/`, the studies and the git log on 2026-11-02: HC, HO and LP are unused (HD1, HD2, HB1, HB2, LS1, MB1 and CD1-9 are taken).

**Every batch:** a batch session by `docs/batch.md`, slices to Sonnet where the brief can pin them down, the harness before the code it proves, the playtest re-baselined only where stated.

| batch | what | files and methods | saved · checks · the trace | size |
|---|---|---|---|---|
| **HC1 Credit cards** | the spread's closed forms; the card's stocks and month; the bank's card rate, test and allowance; the charge-off per share replacing the cell's discharge; levers' constants (the Policy controls are HO4's) | new `CellSpread`; `Household` (stocks, `settle()`, `fundShortfall()`, `discharge()` → `chargeOff()`); `HouseholdBalance` (`stocks()`, `advanceMonth()`, save); `Bank` (`cardRate()`, card book, `householdAllowance` on late balances); `Game`'s load | 4 cell slots, `SAVE_FORMAT` + `OLDEST_LOADABLE_FORMAT` · new `CardCheck` (each closed form against a draw; a shock puts the thin-buffered on the card; the minimum's line; the charge-off hazard gives 3.82 : 2.85; the shut-out share decays by `LOCKOUT_MONTHS`); the discharge's premises in `BankCheck` (7 assertions) and `HouseholdCheck` (4); `CarCheck` (cars still borrow on the card) · moves from the first shock | large: about 900 lines with its harness; two Sonnet slices (`CellSpread` with `CardCheck` §1; the rest) |
| **HC2 Debt statistics** | the offices' figures, per household and per adult, by tier; yearly into history; the page | new `DebtStatistics` (grid quantiles, ratios); `HouseholdBalance` getters; `PeopleScreen`'s Household money page, a banner "Debt, as the statistics office prints it"; `HistorySave` | yearly series · new `DebtStatisticsCheck` (grid against bisection under 1%, against a draw; the ratios equal their sums; per adult = per household / grown-ups); `SaveFileCheck`, `ReadPathCheck` · byte-identical (reads only) | medium: about 600 lines; one Sonnet slice |
| **HO1 Owning a home** | the owners' stocks; `HomeMarket`; the Canadian test; owners' bills for rent; the landlord's sales and purchases; builders for sale; arrears and forced sales; the city's insurance | new `HomeMarket`, `HomeLoan`; `Household`, `HouseholdBalance` (rent × (1 − owners)); `RealEstate` (billed weight, owned capacity, `landlordValue()`, for-sale starts); `Bank` (mortgage book, insured at `RISK_INSURED_MORTGAGE`, uninsured at a new weight); `TaxPolicy` (owners' property tax); `Game.nextMonth()` and `SimulationEngine` after the car market; `MoneyAudit` | 7 cell slots, `HomeMarket` in `DataSave` (no bump) · new `HomeMarketCheck` (the clearing price balances; each test refuses on a fixture that fails it; the landlord sells over V_L and buys under it; a forced sale's shortfall lands on the insurer or the bank; money exact over 24 months); the premises of `RealEstate`'s readers: `HousingCheck`, `HouseholdCheck`, `MortgageCheck`, `InvestCheck`, `LabourCheck` · moves from the first buyer | the largest: about 1,800 lines; three slices (`HomeMarket` with its check; the households' side; the landlord's side) |
| **HO2 Owners through life** | trading up and down on ageing; estates and leavers sell | `HouseholdBalance` on the households study's flows; `HomeMarket` (their sales) | none · `HomeMarketCheck` gains the trades, the estate's order (mortgage first) and the leavers' money · moves | medium: about 500 lines; waits for the households study's flows batch |
| **HO3 The screens** | the Household money page's tenure and the market (price, price over income, sales, months for sale); the owners' book and arrears; the Bank tab's card and mortgage books | `PeopleScreen`, `BankScreen`; pure `HomeMarketView` | none · `HomeMarketViewCheck`, words · byte-identical; Jerus's PC check | medium |
| **HO4 The levers** | on Policy: the card rate cap and minimum; the stress test's add-on; GDS and TDS; the minimum down payment; the amortization; the retention rule (LP1's) | new `CreditRules` (saved), `PolicyScreen`, `PolicyPreview` | `CreditRules` in `DataSave` · `PolicyPreviewCheck` · byte-identical at the defaults | small to medium |
| **LP1 Banks selling loans** | packages, their price, the fund's and abroad's purchases, servicing, retention, the laxity rule; the Bank tab's packages | new `LoanPackage`; `Bank` (sale decision, servicing income, laxity); `TreasuryFund` (rule and hand); `ForeignAccounts`; `MoneyAudit` | packages in `DataSave` · new `PackageCheck` (part 1's prices to the cent; a pass-through's cash sums to its loans'; insured losses land on the treasury; laxity raises defaults by `LAX_PD` × λ; buyers stop past half over their reference) · moves when a bank sells | large: about 1,200 lines |

Order: HC1, HC2, HO1, HO4 (the levers, once their mechanics exist), HO2 (after the households study's flows), HO3, LP1. The traces move at HC1, HO1, HO2 and LP1, each needing the ensemble against `main`.
