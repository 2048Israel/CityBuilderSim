# Where the wealth piles up: the study, 2026-11-02

Read-only study of 0.9.2 (main at 5602c6d0), from Jerus's brief of 2026-11-02: "in the long run everyone inventivly ends up super cash rich... the government, the businesses, the people... thing is its a thin line, the line between broke and super rich". His evidence: Rotterdam (slot 3) at month 7,871, the treasury at F$63.8T against a GDP of F$3.6T a year, gaining F$34.3B a month, while owing F$956B at 4.41%.

The measuring code and the prototypes are in [`wealth/`](wealth/): `WealthRun.java`, the driver (the ensemble's AUTO2 city, LongPlaytest's founding, advisor and rhythm, in `GameFiles.scratch()`, writing every sector's balance sheet and the year's flows a year); `report.py` and `table.py`, the tables; `probe/`, a probe on a scratch copy of `Game.java` that says which statement moved the households' savings; `fx-prototype/`, a scratch `ForeignAccounts` whose balance term builds pressure; `out/`, every table below. No game code changed, and no save slot was written; slot 3 was read with `tools.SaveDump`.

Each figure is one of four kinds. **Measured** figures come from three seeds (0, 1, 2), 8,000 months each, with earth-2 and world-1 named. **Derived** figures show their sum. **Sourced** figures give their licence. **★est.** figures are named estimates with a reason. Stocks and flows are in **years of GDP** (the year's GDP summed off `NationalAccounts`), so the price level cancels.

## 1. Summary for Jerus

1. **The money comes from abroad.** Every year the city sells the world 39-83% of its GDP more than it buys, in every century of every seed. From about month 1,800, net exports are 62-80% of GDP. Spending, investment and government together come to the other 20-38%. Your item 1, then. Item 2 is not it: the central bank adds 0.1-0.5% of GDP a year, matched by its loans. Item 3 is not it either: every figure here is over GDP, so prices cancel.
2. **It lands with the government.** The government takes 32-78% of GDP: the profit tax up to 20%, and the transfer from the city's fund up to 45%. It spends 3-32%. By month 7,200 the treasury holds 21-50 years of GDP in cash. Your city holds 18 years in the treasury, and **97 more years of GDP in the fund's cash** (F$349T in slot 3).
3. **The government ends up owning the companies.** The city's fund holds 6-22% of their value at month 1,200 and 81-97% at month 6,000 (seeds 0-2). Its rule buys no more than 10% of any company (`OWNERSHIP_LIMIT`, Norway's mandate), but the cap is on buying, not on holding. EM1's buybacks retire the households' and the world's shares, and the fund's 10% grows into a majority. So EM1's payout works, and pays the government.
4. **The businesses are not hoarding.** Their tills hold 0.4-0.6 years of GDP, inside EM1's six-month cushion (yours: 0.5 years). **Households** own 3-11 years of GDP, three quarters of it abroad late in a game. Their 4% a year spent out of wealth already holds them to that, so the households study keeps its rate.
5. **A dearer currency would not cure it.** The D$'s pull from the balance is capped at 20% (`ForeignAccounts.BALANCE_CAP`). I prototyped a pull that builds while the surplus lasts. By month 2,400 the D$ was 7-9 times dearer than parity and the town was a quarter the size or less, yet it still exported 62-74% of GDP net. The surplus is the mirror of what the city does not spend. Payroll falls from about half of GDP in the young town (50-55%, months 360-900) to 13-26% from month 1,200 on, household take-home to 12-21%, and the government banks most of the rest.
6. **A bug, in the other direction.** Money vanishes from the households' books, 0.4-4.4% of GDP a year. Three causes, found with a probe:
   - the upkeep of care, schools, safety and the bank's branches is booked as paid to households and reaches nobody;
   - transit's non-wage bill is booked the same way;
   - the emigrants' shares are bought out of savings and paid abroad on no line.

   Households sit outside the money audit by design, so no harness has seen it. **HL1** fixes it first.
7. **The fix is a drain that reads the stock, in every sector.** The households already have one (4% of net worth a year), and so do the businesses (EM1's cushion). The government has none. So:
   - **GV1:** your screens say what the government holds, in years of its revenue, and the three ways to give it back;
   - **GV2:** the playtest's player (the city LongPlaytest and the ensemble play, and Claude when it plays) steps its three levers back toward a band each January: taxes, pensions and benefits, and the fund's withdrawal (your call, 2026-11-02);
   - **RB1:** the fund holds to its own 10% of a company, selling what a buyback has pushed past it.
8. **A prototype player who reads the government's wealth bounds it, and the pile moves to the households.** It used only your own levers: past two years of revenue, it cut the profit and sales taxes, raised pensions and drew the fund's cash. By month 3,600 the government held 0.6-0.7 years of GDP instead of 19-26, and spending rose from 33% of GDP to 51-54%. Net exports fell from 64% of GDP to 43-46%. But households went from 10-11 years of GDP to 19.5, still rising. That is your thin line, measured: bound one sector and the money arriving from abroad piles in the next. Each sector needs its drain, and the households' rate (the households study's) and the labour share (the wages study's) decide where the city settles.
9. **Your four calls, 2026-11-02** (section 4): GV2 for the playtest city only, with all three levers and no city dividend; the fund sells past its 10%; the currency's cap and the businesses left as they are, the cap to be solved later (XC1); all four batches queued.
10. **A different blow-up past month 7,200,** found on the way and not this study's. In two seeds of three (seed 1 near month 7,390, seed 0 near 7,530), the central bank lends the bank tens of thousands of years of GDP within a year. The bank's cash goes to -1.5e17, and the treasury, the fund and the bank's equity jump a thousandfold. Every dollar is booked, so the audit closes. Nothing checks past month 4,000, and your city is at 7,871. It needs its own task (section 6).
11. **Five batches, queued** (section 5): HL1 (medium, the trace moves), GV1 (small, screens only), RB1 (small, moves), GV2 (small, the playtest's player, moves) and XC1 (the cap without the spiral, a study first, last).

## 2. What was measured

### 2.1 The money position of each sector
Financial assets less debts, with shares left out (they are a claim on the businesses, so they cancel inside the city). Years of GDP, seed 0, at each century mark; the range over seeds 0-2 is in brackets at month 7,200 ([`out/final.txt`](wealth/out/final.txt)).

| month | population | households | businesses | government | bank | central bank | treasury cash | household savings | business tills |
|---|---|---|---|---|---|---|---|---|---|
| 1,200 | 32,613 | 1.43 | 0.04 | 0.69 | 0.04 | 0.00 | 1.18 | 0.74 | 0.27 |
| 2,400 | 251,933 | 11.07 | 0.39 | 8.57 | 0.00 | -0.08 | 8.31 | 9.46 | 0.46 |
| 3,600 | 559,975 | 9.69 | 0.37 | 25.87 | 0.01 | -0.08 | 23.72 | 8.21 | 0.46 |
| 4,800 | 641,587 | 9.25 | 0.35 | 32.02 | 0.27 | -0.17 | 29.00 | 1.63 | 0.54 |
| 6,000 | 858,995 | 4.53 | 0.44 | 34.45 | 0.01 | -0.13 | 30.64 | 1.28 | 0.39 |
| 7,200 | 893,812 | 4.82 [3.6-7.4] | 0.51 [0.1-0.5] | 36.20 [22.7-54.9] | 0.00 | -0.27 | 32.50 [21.2-49.5] | 1.06 | 0.48 [0.4-0.5] |

Every sector is positive at once, so the world must be the negative side. Nothing in the city is: the bank's equity and the central bank's are near nought. For scale, the largest sovereign wealth in the record is about 3-5 years of GDP (Norway's fund, Kuwait's), ★est. from their public reports; their terms were not read, so a batch that needs the figure reads OECD's general government net financial worth instead (OECD terms allow commercial use, [licences.md](licences.md) row 35). The game's government reaches 5-18 times that (derived: 22.7-54.9 ÷ 3-5).

### 2.2 Where the money comes from
Flows over each period, years of that period's GDP, seed 0 (seeds 1 and 2 in `out/final.txt`).

| period | net exports | income balance | financial account | central bank made | audit gap | households' gap | C | I | G | revenue | spending | treasury's gain |
|---|---|---|---|---|---|---|---|---|---|---|---|---|
| 0-1,200 | 0.39 | -0.03 | -0.13 | 0.000 | 0 | -0.044 | 0.25 | 0.25 | 0.11 | 0.40 | 0.32 | 0.13 |
| 1,200-2,400 | 0.62 | 0.16 | -0.15 | 0.003 | 0 | -0.034 | 0.33 | 0.03 | 0.02 | 0.32 | 0.06 | 0.27 |
| 2,400-3,600 | 0.64 | 0.14 | -0.03 | 0.002 | 0 | -0.041 | 0.33 | 0.02 | 0.02 | 0.54 | 0.04 | 0.50 |
| 3,600-4,800 | 0.73 | 0.14 | -0.32 | 0.004 | 0 | -0.038 | 0.25 | 0.01 | 0.02 | 0.78 | 0.05 | 0.69 |
| 4,800-6,000 | 0.69 | 0.12 | -0.08 | 0.002 | 0 | -0.039 | 0.28 | 0.01 | 0.03 | 0.63 | 0.06 | 0.58 |
| 6,000-7,200 | 0.76 | 0.06 | -0.09 | 0.005 | 0 | -0.019 | 0.21 | 0.00 | 0.03 | 0.67 | 0.05 | 0.62 |

The tables stop at month 7,200 because of summary item 10: past it, seeds 0 and 1 break down.

The three candidates of the brief:
1. **A lasting surplus with abroad: yes.** The current account is +0.36 to +0.89 of GDP a year (derived: net exports plus the income balance), and the financial account takes back 0.03-0.32. So 0.3-0.8 of a year's GDP arrives each year as the city's own money that nobody in the city sold dollars for. Exporters are paid in local money, and nothing makes the world buy it. The GDP is honest: C + I + G + NX = 1.00 in every period, and the national accounts' NX equals the audit's trade balance to the digit (measured).
2. **Money created without a debt: no.** `MoneyAudit` closes its pools (the treasury, every sector, the order book, the bank, the fund) to the cent every month, with a residual of nought in all 24,000 months. The central bank made 0.1-0.5% of GDP a year, against its advances. One gap is real but runs the other way (2.4).
3. **Prices only: no.** Every figure is over GDP. The price index rose 1.4 million times by month 7,800 (2.35% a year, derived: ln 1.37e6 ÷ 650 years), and the ratios still grow.

### 2.3 Where it lands: the government, and the companies it comes to own
Revenue by line, years of GDP, seed 0: the profit tax 0.09 → 0.17, the fund's transfer 0.01 → 0.22 → 0.45 → 0.31, the wage tax 0.10 → 0.03, the sales tax 0.04-0.07, property 0.02-0.07. Spending falls from 0.32 to 0.04-0.06, and its capital spending from 0.04 to under 0.01: the playtest's advisor builds less for the city as it matures.

Who owns the companies, by fair value (seed 0):

| month | city's fund, seed 0 | of it, the rescue book | seeds 1, 2 | fund cash (years of GDP) | fund transfer (of GDP) |
|---|---|---|---|---|---|
| 1,200 | 6% | 2% | 9%, 22% | 0.00 | 0.01 |
| 2,400 | 31% | 0% | 30%, 57% | 0.23 | 0.08 |
| 3,600 | 84% | 0% | 69%, 39% | 2.09 | 0.22 |
| 4,800 | 71% | 45% (a resolution) | 80%, 39% | 2.99 | 0.45 |
| 6,000 | 97% | 0% | 97%, 81% | 3.75 | 0.31 |

The rescue book is a passing event (45% at month 4,800 after a resolution, nought at the next mark). The ratchet is the market book:
- The rule buys by its index weights, at most `OWNERSHIP_LIMIT` (10%) of any company (`TreasuryFund`; the GPFG mandate's "may not be invested in more than 10 per cent of the voting shares", 14 May 2018). The limit is read when it buys, not when it holds.
- EM1's buybacks bid fair value plus 10% (`BUYBACK_TOLERANCE`), and the households and the world sell into them. The shares in issue shrink, and the fund's 10% becomes 30%, then 84%.
- The dividends and buybacks follow the shares. The fund pays the budget 3% of its value a year (`TRANSFER_RATE`, Norway's rule), and its cash earns nothing, so the rest stays: F$349T in your slot 3.

**The labour share.** Payroll, every sector's statement plus the bank's tellers, is 50-55% of GDP in the young town (months 360-900, seed 0, from the audit's payroll lines), as in a real economy. Over months 0-1,200 it is 27-38% (seeds 0, 1), and from month 1,200 on 13-26%. Household take-home is 12-21%. A city whose people get a sixth of what it makes cannot buy what it makes, so it exports the rest.

### 2.4 The leak: the households' books
The households are outside `MoneyAudit` by design (`HouseholdBalance`: "Not part of the money audit"). So the driver strikes their identity each month. The change in savings should equal what the audit says crossed to them, net of the central bank's lines, less what left with the leavers. The audit already declares the households' own foreign moves as pairs. The gap is what is left.

**It is negative in every month:** about 0.1% of savings a month, or 0.4-4.4% of GDP a year (seeds 0-2, every century). Money vanishes. The probe ([`probe/`](wealth/probe/)) put `WealthProbe.mark()` after each of nextMonth()'s 184 statements on a scratch copy. Its sum equals the month's change in savings to the cent. Over months 240-900 of seed 0, the gap was -422,459 (thousands, measured):

| cause | amount | share |
|---|---|---|
| emigrants' shares bought out of savings, paid abroad (`Exchange.getEmigrantsPaid()`, no audit line) | 165,751 | 39% |
| care's and safety's upkeep, "- care Upkeep" and "- safety Upkeep", DOMESTIC out to nobody | 73,135 | 17% |
| the rest: transit's bill beside its wages ("- transit Bill", 459,260 in all) and the bank's branches ("- bank Operating"), the same shape | 183,573 | 44% (derived) |

The audit's own comment names the shape: "an outflow to a household and an outflow to nowhere look identical from in here" (it found the tuition subsidy that way). The households' share trades with the world (`getHouseholdsBoughtAbroad`, `getHouseholdsSoldAbroad`) are on no line either. They are small here (2.2M bought over 2,400 months), but the balance of payments misses them. The newcomers' opening buffer (`OPENING_BUFFER_MONTHS`, 1.5) is the one gap the other way: about 0.03-0.3% of GDP a year (★est. from the households' growth × 1.5 months of disposable income).

The shops do match the households: luxury 508,353 against 507,983, meals 263,092 against 262,897 (measured, months 240-900), and dividends and deposit interest to the cent.

### 2.5 Why the currency does not stop it, and why a dearer one would not
The float (`ForeignAccounts`, THE FLOAT, XR1) sets ln rate = parity's trend + the gate × (gap + balance + cover + market). The balance part is `balanceTarget()`: -0.5 × (current + financial account) ÷ GDP, **capped at ±0.20**, and it is a level, reached at `MARKET_SPEED`. So a surplus of 60% of GDP for four centuries makes the D$ 20% dearer and no more. In the runs, fx/parity sits at 0.66-1.13 for 7,800 months.

**The prototype** ([`fx-prototype/`](wealth/fx-prototype/)) replaced that level with a pull that builds by 0.3% a year per point of GDP of balance, fading over a ten-year half-life. The rate's ★est. is set between the Swiss franc's and the yuan's appreciation under their surpluses. Seeds 0 and 1, to month 2,400:

| at month 2,400 | fx/parity | population | net exports, the year to it | treasury, years of GDP |
|---|---|---|---|---|
| main, seed 0 | 0.77 | 251,933 | 0.59 | 8.31 |
| prototype, seed 0 | 0.11 | 64,556 | 0.62 | 4.80 |
| main, seed 1 | 0.87 | 353,653 | 0.61 | 7.01 |
| prototype, seed 1 | 0.15 | 42,449 | 0.74 | 5.19 |

([`out/fx-prototype.txt`](wealth/out/fx-prototype.txt).)

A dearer D$ makes every export earn less at home, so fewer businesses pay and the town stays small. The town that is left still sells abroad what its people cannot afford. The trade here follows quantities: what is made against what the city's people and government buy. Prices move it little. So the cure is on the spending side, and the cap stays (call ★3).

### 2.6 Your city, slot 3 (read with SaveDump, never written)
Rotterdam, 0.8.95, month 7,871, Historical, 2,913,384 people. Units are F$ thousands as saved:
- treasury cash 6.38e10 (F$63.8T);
- the fund's cash 3.49e11 (F$349T);
- household savings 4.08e10 (F$40.8T);
- the 24 sectors' tills 1.88e9 (F$1.9T: Copper Refining 1.18e9, Automotive 2.5e8, Manufacturing 1.7e8);
- the bank's cash -8.5e8 (borrowed at the window);
- GDP 2.73e8 a month (F$3.3T a year).

In years of your screen's GDP (F$3.6T a year): fund 97, treasury 18, households 11, businesses 0.5 (derived: each ÷ 3.6e9). On the save's last month × 12 (F$3.3T) they are 106, 19, 12 and 0.6. The gold study read the same save: net exports positive at every century, and the vault at US$6.5e15 after the month-4,716 intervention (its XI1).

### 2.7 The prototype player who reads the government's wealth
`WealthRun -Dwealth.rule=on` ([`out/rule.txt`](wealth/out/rule.txt)) is the driver with a player who reads the government's wealth.
- **What it reads:** each January, the treasury's and the fund's cash over the last twelve months' revenue.
- **Past 2 years of revenue:** it cuts the profit tax 5 points and the sales tax 2 (to nought at most), raises the pension's replacement 5 points (to 100% at most), and sets the fund's withdrawal to its most.
- **Under half a year:** it moves each lever back one step.
- **Left out:** the playtest's scripted tax steps, which would fight it.

Every lever is a setter the Policy and Finances screens already call. Seeds 0 and 1, to month 4,000:

| | government | treasury | households | net exports, m2,400-3,600 | C | revenue | population at m3,600 |
|---|---|---|---|---|---|---|---|
| main, seed 0 | 25.87 | 23.72 | 9.69 | 0.64 | 0.33 | 0.54 | 559,975 |
| the rule, seed 0 | 0.64 | 0.63 | 19.49 | 0.43 | 0.54 | 0.06 | 543,986 |
| main, seed 1 | 19.11 | 18.51 | 11.17 | 0.65 | 0.32 | 0.49 | 644,632 |
| the rule, seed 1 | 0.71 | 0.71 | 19.50 | 0.46 | 0.51 | 0.07 | 551,464 |

(Years of GDP at month 3,600; flows over months 2,400-3,600.) By month 2,400 the rule had taken the profit tax to nought and pensions to 100%, and the government sat in its band. Spending rose by 60% (derived: 0.54 ÷ 0.33), and net exports fell by a third. The town grew to about the same size. Unemployment was 7-12% against main's 6-23%.

The households' net worth rose from 13.2 to 19.5 years of GDP between months 2,400 and 3,600 and was still rising. Their take-home stayed at 12% of GDP, because a profit-tax cut reaches owners, not earners. So the rule moves the pile; on its own it does not end it. The money keeps arriving while net exports are 43% of GDP, and it settles wherever the weakest drain is. So:
- **GV2 moves the playtest's levers** (Jerus's answer to ★1: all three, no city dividend), so the pile it moves goes to the owners and the retired first;
- **the households study's drain** sets their level;
- **the wages study's labour share** (payroll 12-13% of GDP, where compensation is about half of value added in the US, from the BEA's industry accounts, a federal work in the public domain, as HD1's notes cite it) sets how much of what the city makes its people can buy.

## 3. The design, on the code

### 3.1 The rule, one shape for every sector
A stock that compounds is bounded only by a drain that reads the stock (`HouseholdBalance`, AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, 2026-09-17). Nothing that reads a flow can bound it. So each sector reads its own wealth in months of its own income or costs, and pulls back toward a band from both sides. The drain is a share of the excess per month, never a jump, so the knife edge becomes a slope:

| sector | its stock over its flow | too rich | too broke | today |
|---|---|---|---|---|
| households | net worth over income | spends 4% a year of net worth (`WEALTH_SPENT_A_MONTH`) | the waterfall: savings, then credit, then hunger | built; rate kept (households study) |
| businesses | spare cash over 6 months of costs and debt service | EM1's buyback pays it out | the bank and the bond market | built (EM1) |
| the fund | its share of each company; idle cash | RB1: holds to its 10%, and idle cash reaches the budget | the treasury pays in | **missing** |
| the government | cash, fund and vault less debt, over a year's revenue | GV1 tells you; GV2's playtest player steps its levers back | advances, bonds (built) | **missing** |
| the city against the world | the overall balance over GDP | the D$ dearer, up to 20% | cheaper, up to 20% | kept (★3) |

Per month the cost is one comparison per sector, plus GV2's levers moved once a year: nothing a month can feel.

### 3.2 The government: the advice on your screens (GV1) and the playtest's player (GV2)
**GV1, the screens.**
- **`GovernmentView`** (a new pure model, or the Government screen's own model if it has one) gets `wealthYears()`: (treasury + fund cash + the fund's holdings at mark + the vault − the city's debt) ÷ the last twelve months' revenue. Every part is a getter that exists (`getCash`, `TreasuryFund.getCash`, the fund's marked holdings, `ForeignAccounts.getReserves`, `DebtManager.getAllPrincipal`). It also gets a band:

  | band | years of revenue |
  |---|---|
  | broke | under 0.1 |
  | thin | 0.1-0.5 |
  | sound | 0.5-2 |
  | rich | 2-5 |
  | hoarding | over 5 |

  The bounds are ★est., wide around a real treasury's buffer of weeks of outlays (the US Treasury's cash balance is about six weeks of outlays, ★est. from its refunding statements, a federal work in the public domain) and a sovereign fund's few years.
- **The Government screen's overview** gets one line: "The government holds N years of its revenue", in the band's colour. Below it are the three ways back, each with what it moves a month, from getters that exist:
  - buy back the paper you owe (one click, CD4's buyback) when cash exceeds the debt and its yield is above what cash earns, which is nothing (`TreasuryFund`: "It earns nothing");
  - cut the tax that raises most;
  - raise a transfer.
- **A notice** when the band changes, through the notices the save already keeps.

No model change, so the trace is byte-identical.

**GV2, the playtest's player reads the government's wealth** (Jerus, 2026-11-02: "only for the auto city, aka the city the playtests do, not the actual player, and that would be all three. like for when claude needs to play the game, not the city dividend tho").
- **Where:** the player's moves in `LongPlaytest.main` and its copies (`tools/ensemble/Ensemble.foundAndPlay`, `WealthRun`). Nothing in the game's own model or screens: you play your city by hand.
- **The rule, each January:** read GV1's `wealthYears()`. Past the band's top (2 years of revenue), move all three lever families one step toward giving the money back:
  - taxes: the profit tax 5 points and the sales tax 2 points down, to nought at most;
  - transfers: the pension's replacement and the EI benefit 5 points up, to their caps;
  - the fund: its withdrawal to its most, so its idle cash reaches the budget.

  Under the band's bottom (half a year), move each back one step. Inside the band, do nothing.
- **It replaces the playtest's scripted tax steps** (the income tax every 11th stop, the property tax every 17th), which would fight it.
- **Measured as the prototype** (2.7): it holds the government to 0.6-0.7 years of GDP. Spending rises from 33% to 51-54% of GDP, net exports fall by a third, and the households' net worth goes to 19.5 years of GDP. So the batch reads its ensemble beside the households study's drain.
- **The trace moves** (the playtest's player changes), so the batch runs the ensemble. Its AUTO2 policy is this player.

### 3.3 The fund (RB1)
- **The fund holds to its 10%.** Today `OWNERSHIP_LIMIT` caps what the rule buys. RB1 reads it on the holding too: the part of a market-book holding past 10% of a company's shares in issue is offered into the book, a tenth of the excess a month. That uses the market book's own sale path (EM1-U's sale into a bid over fair × (1 + BAND)), so a buyback's bid takes it first. The mandate the limit cites is a limit on what the fund may hold, so this is the dial's own source read whole. A tenth a month is ★est.: fast enough that a buyback's ratchet cannot outrun it, slow enough to stay inside the book's band.
- **The rescue book** keeps its own rule (the hand sells it). It held 0-2% at every mark but one, so RB1 leaves it alone.
- **The transfer counts the fund's idle cash.** Cash past what its rule will invest in a year is transferred to the budget at 1/24 a month (★est.: two years, to keep it from being a jolt). The 3% of value (Norway's rule) stays for what is invested. This only moves money from the fund to the treasury, which the screens then count once (GV1).
- **What changes the city is the ownership.** With the fund at 10% of each company, the dividends and buybacks reach households and the world again. The world's share leaves the city as an income outflow, which narrows the current account directly.

### 3.4 The households' books (HL1, the bug)
1. **The identity, as a harness.** `MoneyAudit` gets a second strike, `households(g, before)`: savings after = savings before + every DOMESTIC line that is a household's, + the newcomers' buffer as a line of its own, − what left with the leavers. The residual is asserted at nought by `MoneyCheck` and `LongPlaytest.audit()`, with the same tolerance as the pools'. `HouseholdBalance` keeps `lastArrivedWith` (the sum of `brought` in `moveStock`, which today is a local) beside `lastTakenAway`.
2. **The upkeep goes somewhere.** "- care Upkeep", "- schools Upkeep", "- safety Upkeep", "- bank Operating" and the non-wage part of "- transit Bill" leave the pools to nobody. Each becomes a purchase from a sector that supplies it (call ★5's batch detail; recommended: Business Services at home, and the share a sector of its kind imports, at the audit's TRADE out). Or, for the part that is wages, it reaches the households' income, if FamilyModel does not already pay it as a post.
3. **The emigrants' shares and the households' trades with the world reach the balance of payments.** They are FINANCIAL lines in pairs, as the households' bonds abroad already are (`BondMarket.getHouseholdsBoughtAbroad`, "(from their savings)"): "+ equity HouseholdsBoughtAbroad (from their savings)" DOMESTIC and "- equity HouseholdsBoughtAbroad" FINANCIAL, and the same for sales and emigrants.
4. **Migration on the balance of payments.** The newcomers' buffer is money from abroad (FINANCIAL in, "+ households ArrivedWith"), and the leavers' savings are FINANCIAL out ("- households LeftWith"). Real balances of payments carry migrants' transfers. Then the currency sees them.

No saved field: every line is the month's own. The trace moves (money stops vanishing), so the batch runs the ensemble.

### 3.5 Businesses: nothing new
Measured: 0.4-0.6 years of GDP in tills, in every seed and century, and 0.6 in your city. That is about 5-7 months of GDP, inside EM1's cushion of six months of costs and debt service (`BUYBACK_CUSHION_MONTHS`), since costs exceed value added. EM1 already pays the rest out, and its payout is what feeds the fund (3.3). A special dividend was your 0.7.12 call ("no special dividend"), kept in EM1. Call ★4 asks whether to keep it so.

### 3.6 Households: the households study's
Handed over in the shared notes (`wealth.md`), and repeated here:
- net worth is 3-11 years of GDP, and 18-88 years of their own disposable income (disposable is 12-21% of GDP from month 1,200 on);
- savings alone are 45-900 months of disposable;
- 55-82% of net worth is abroad after month 4,800, and debt is under 2% of a year's disposable;
- the foreign coupon is 0.5-34% of GDP a year.

`WEALTH_SPENT_A_MONTH` (4% a year) bounds them: net worth peaks at about 11 years of GDP and falls back to 3-7. So this study proposes no change to its rate. The pile without a bound is the government's.

### 3.7 The currency: kept (call ★3)
The cap stays, by section 2.5's measurement. One related bug is the gold study's (XI1: an intervention's impact unbounded, the x11,600 move in your slot 3).

## 4. The calls, and Jerus's answers (2026-11-02)
- **★1 GV2's lever.** Asked: a city dividend (recommended), the taxes, pensions and benefits, or all three in the player's order. **Answer:** "only for the auto city, aka the city the playtests do, not the actual player, and that would be all three. like for when claude needs to play the game, not the city dividend tho". So GV2 is the playtest player's rule, moving all three, with no new lever (3.2).
- **★2 The fund's 10%.** **Answer: sell the excess** (as recommended): RB1.
- **★3 The currency's cap and ★4 businesses.** **Answer:** "leave both, but we do need to later solve the cap issue, aka be able to remove cap and not have it spiral always". So the cap and EM1 stay. XC1 is queued last: a balance pull with no cap that does not spiral. It is studied after the spending-side batches, because the prototype in 2.5 spiralled on today's city, where trade follows quantities.
- **★5 (HL1's detail, not his).** The upkeep becomes a purchase from Business Services, a sector's usual import share going abroad; the batch measures the shares.
- **Queue:** all four, plus XC1 for the cap.

## 5. The batches, their size and their order

| batch | what | size | the trace | waits for |
|---|---|---|---|---|
| **HL1** | The households' books closed: the identity in the audit (3.4.1), the upkeep to a seller (3.4.2), the households' share trades and the emigrants on the balance of payments (3.4.3), migration's money as lines (3.4.4) | medium (Sonnet slice + Opus review; MoneyAudit, HouseholdBalance, Exchange, the services' upkeep) | moves: ensemble | nothing; first |
| **RB1** | The fund holds to its 10% of a company, selling what a buyback has pushed past it; the fund's idle cash to the budget (3.3) | small | moves: ensemble | ★2 |
| **GV1** | The government's wealth on its screens: `wealthYears()`, the band, the line, the three ways back, the notice (3.2) | small (screens) | byte-identical | nothing |
| **GV2** | The playtest's player reads the government's wealth: all three levers by the band, in place of the scripted tax steps (3.2) | small (LongPlaytest, Ensemble's copy) | moves: ensemble | GV1 |
| **XC1** | The currency's balance pull without the 20% cap, and without the spiral (★3's answer): a study first, rerunning `fx-prototype/` uncapped on the city after the spending-side batches, then the batch | study, then medium | moves | HL1, RB1, GV2, the households and wages batches |

Order, as queued (Jerus, 2026-11-02): HL1 just before the households study's HS1, whose row (Jerus's) says "after the wealth study's HL1": HS1's leavers' money then goes through HL1's identity. GV1, RB1, GV2 and XC1 go at the end of the queue. HL1 also comes first because a leak is a bug. GV1 moves no trace and tells you what you hold. RB1 and GV2 each move the city. XC1 comes last, because a cap can only come off once the surplus has a cure.

The other studies:
- the households study owns spending out of wealth and the stages' targets (WEALTH_SPENT_A_MONTH's rate is kept, as agreed in the shared notes), and its HS1 waits for HL1, so its migrants' money goes through HL1's identity;
- the wages study owns the labour share; `LS1` is already queued, and it is the largest single reason the city cannot buy what it makes;
- the gold study owns the currency regimes and XI1.

## 6. Risks, and what to measure first
- **The prototype player is not you.** It plays the playtest's advisor, which builds less as the city matures (spending 0.32 → 0.05 of GDP). You spend more: your treasury gains 11% of GDP a year, where the advisor's gains 50-70%. The shape is the same and the speed is not. GV2 is the playtest's player by your answer, so its runs are the advisor's, not your save.
- **HL1 stops money vanishing**, so households end richer by up to 4% of GDP a year. The ensemble's inflation criteria may move.
- **RB1 hands the companies' dividends back to households and the world.** That is a real change in who earns, so it waits on ★2.
- **GV2 moves the pile to the households** (2.7). Its ensemble should be read beside the households study's drain.
- **The blow-up past month 7,200** (summary 10) is not this study's. Its fixture is `WealthRun` seed 1, which reaches month 7,344 in about 6 minutes: a credit run between months 7,344 and 7,392, the central bank lending the bank 8.2e16 in the year (35,000 years of GDP), the bank's cash at -1.5e17, valuation +2.9e16 and the profit tax 2.6e16. Its own task should find the month, then the line.
- **First, in HL1:** strike the identity on main before any fix. It must show the measured -0.1% of savings a month, as the harness's own premise: "a harness is the finding".
