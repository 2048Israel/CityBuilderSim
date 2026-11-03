# Households through life: the study, 2026-11-02

Read-only study of 0.9.2 (`5602c6d0`), for four of Jerus's items: business owners, money following people, adult age bands, and taxing gains and dividends. Prototypes and figures in `households/`:
- `src/ham/citybuildersim/HouseholdsProbe.java` plays the playtest's own city (`LongPlaytest.probeCity` and `probePlay`, on `GameFiles.scratch`) to month 4,000 and reads every cell's books: `out/probe.txt`.
- `HhAgent` and `HhTimer` time the household methods inside the month; the exact timings and the JFR samples are sections (b) and (c) of `out/probe.txt`. The city is deterministic (seven runs gave the same tables), so the probe's javadoc recipe rebuilds every figure.
- `flows.py` is a toy of the ledger, today's rule against flows: `out/flows.txt`.
- `sources.md` holds every real-world figure with its page and licence.

Each figure is one of four kinds:
- **sourced:** [S§n] points to `sources.md`'s section n. All of them are Statistics Canada (Open Licence), the Federal Reserve Board (public domain), OECD data, or the Income Tax Act through the Reproduction of Federal Law Order. Canada.ca and CRA pages forbid commercial redistribution, so no figure is taken from them.
- **derived:** the sum is shown.
- **measured:** [M], from `out/`.
- **★est.:** a named constant with its reason, listed in §8.

The game's money is in its own thousands at its own price level (the 1900 start), so only ratios are compared with the world's.

## 1. Summary for Jerus

1. **The elderly who are broke are the seniors living alone.** They are half of the retired households.
   - At month 4,000 of the playtest city, such a household holds 9% of a working household's net worth and 8% of its savings [M].
   - Elder couples hold 9 times a working household's, and the retired row as a whole holds 1.6 times.
   - Where the money is decided by which cells happened to grow, not by who saved it.
2. **Why.** The census sees only each cell's net change.
   - A retiring couple's savings stay in their pay row and go to whoever joined it.
   - At month 2,400 a household coming into the retired row got 29% of a working household's average [M]. 94% of that came from strangers leaving other rows, and 43% of the newcomers arrived with only the 1.5 months' opening buffer.
   - When one of a senior couple dies, the survivor keeps half and the other half leaves the row.
3. **The fix (HS1): the month's own flows carry their money.** Retiring, a partner dying, dying alone and leaving each carry the average of every stock (savings, shares, money abroad, bonds, loans) from the cell they left. The census's pool stays only for the reshuffles nobody can trace. In the toy the retired go from about a ninth of the working's wealth per adult to about the same (sum in §2.6). In Canada the 65+ hold 1.04 times what the 35-64 hold per household (derived from [S§1]).
4. **The retired keep their former tier** (24 retired cells instead of 4). An Elite couple retires rich and an unskilled widow modest.
   - **The survivor keeps everything**, as Canada's spousal rollover does.
   - **Someone dying alone leaves an estate.** It settles their debts, then goes to the established households of their former tier.
5. **Two adult stages (HS2):** young 18-34 and established 35-69. The young form singles, couples and young families; the established hold the teens and the large families (your "large family would be older adults").
   - Ageing into established carries its money.
   - The split at 35 is where Canadian home-ownership jumps (44% to 63%) and median net worth more than doubles ($159,100 to $409,300) [S§1].
   - Retirement stays at 70 (★ call 5; Canada's average is 65.4 [S§7]).
6. **Business owners (BO1): one founding family per listed company** (your call 1).
   - The families take the founders' shares alone; everyone else still buys shares at offerings and on the exchange.
   - They have no job: they live on dividends and on what they sell, slowly (★ 5% of each stake a year). They never retire.
   - A family whose company goes broke joins the ordinary population, taking its share of the line's money.
   - Today's founders' shares are 98% of what households own at month 21 [M].
7. **A finding for the wealth study.** The month-21 "rich skilled couple" is mostly one company. Construction is 86-96% of the households' shares in five foundings, priced 50-400 times its founding price after 21 months [M]. BO1 moves that fortune to the owners; it does not explain the price.
8. **Taxes (HT1).**
   - Each cell carries a cost basis per company, carried with people like any stock.
   - Realised gains are taxed on half the gain at the holder's wage-tax rate, as Canada's inclusion rule does [S§3]: at a sale, at a death (the deemed disposition) and on leaving (the departure tax).
   - Dividends are taxed by integration (★ call 2): profit tax plus dividend tax equal the holder's wage tax. At the defaults (15% and 15%) that is zero, so the default city's dividends are unchanged; a band whose wage tax you raise pays on its dividends too.
9. **Spending from wealth is already in the game** (`WEALTH_SPENT_A_MONTH`, 4% of net worth a year). Its rate is the wealth study's to set. Two measured facts for it:
   - Working households hold 13-32 years of take-home in this city from month 240 on (18 at month 4,000) [M]; Canada's 35-64 hold 9.9 years of disposable income (derived from [S§1]).
   - The Bank of Canada finds under half a cent a year spent per dollar of stock wealth [S§4], which matters for the owners' fortune.
10. **Cost.**
    - Cells go from 78 to 141 (derived §2.7).
    - The work that grows with them, the households' books and their bond and share orders, is 24-39% of a model month today [M].
    - So expect a model month 21-33% slower: 21.4 ms becomes about 28.5 at month 4,000 on an idle machine.
    - The bond orders per cell are the largest part; §6's risk 1 has the fallback.
11. **Five batches:** BO1, HS1, HS2, HT1 (models) and HS3 (the screen, after KP7).
    - HS1, HS2 and HT1 bump the save format; BO1 does not.
    - Every model batch moves the trace and needs the ensemble.

**Your answers** (§7, 2026-11-02): one owner family per listed company, with no job; dividends by integration; a taxable share of a gain, default 50%; estates to the same former tier; retirement stays at 70; no three-adult shape for now; the order BO1, HS1, HS2, HT1, HS3, queued at the end. The flows batch is HS1, not HL1: the wealth study named its households'-books fix HL1 first.

## 2. What the code does today, checked

### 2.1 The cells
- `HouseholdBalance` holds 78 cells [M]:
  - 11 working shapes × 6 pay tiers = 66;
  - 4 retired shapes with no tier (`SENIOR_ALONE`, `SENIOR_COUPLE`, `ELDER_ALONE`, `ELDER_COUPLE`);
  - 8 outside the families: the out of work by situation, the students, the orphans by band, the prisoners.
- Every money figure is per household; the weight money follows is grown-ups.
- A household carries 86 stocks at month 2,400 [M]: savings, debt, a share count for each of 25 companies, money abroad, student debt, cars, the city's paper, and 55 bonds by id (175 stocks with 144 bonds at month 4,000). `CELL_SLOTS` is 47.
- The pyramid (`PopulationCohorts`) has one adult band, 18-70 (`AgeBand.ADULT`), drained at 1/624 a month. An 18-year-old is as likely to retire this month as a 69-year-old: the compartment model's known cost (`AgeBand.monthlyOutflowRate()`'s note).

### 2.2 The census pool (`HouseholdBalance.moveStock`, `followThePeople`)
- `FamilyModel` rebuilds the households each month: last month's are placed again where they still fit, less the 1% that re-form ("the households remember").
- `followThePeople` then moves every stock by each cell's net change:
  - cells that shrank release their average into a pool;
  - cells that grew draw on it, within the stock group (the row) first, then across the city;
  - a gain nobody released arrives with `OPENING_BUFFER_MONTHS` (1.5) of take-home;
  - a release nobody claimed leaves the city.
- `carryGraduatesLoans` is the one place a flow carries its own stock. It moves the graduates' loans before the pool and takes the graduates out of `before[]`.
- What that does at each event:
  - **Retiring:** the working cell shrinks and its row keeps the money if anything in the row grew (a teen turning 18, a migrant). The retired cell's gain is matched first with the retired row's own losses (its dead), then the city's, then arrivals.
  - **A partner dying:** by grown-ups, the couple cell releases two shares and the alone cell takes one. The survivor keeps half and the other half goes to the city pool (`RetiredHousehold`'s javadoc says so).
  - **Leaving:** a leaver's money stays in the city whenever any cell grew that month. Only the unmatched rest leaves: at month 2,400 about 1,250 people left a month, yet savings left the city in only 2 of 12 months [M].

### 2.3 The founders' grant (`HouseholdBalance.grantFounders`, `Equity.offer`, `Equity.listIfUnlisted`)
- A company that lists with book equity issues `bookEquity / foundingPrice` shares to every non-empty cell by grown-ups, the retired included.
- At month 21 of the probe city (328 people, 9 of 25 companies listed) [M]:
  - founders' shares are 91% of all shares in issue, and at most 98% of what the households hold;
  - shares at market are 49 times the households' savings.
- The skilled couple cell holds $419k of shares against $160 a month of take-home and gets $1,576 a month in dividends, 9.9 times its take-home. The brief's figures ($563k, $281, $1,344, at 55 people) came from another founding; the shape is confirmed.
- **The size is Construction's price, not the grant.** The founders' book over all companies was 588 $k [M]. Construction's 1,010 founding units were issued against a book of 26.2 $k and were worth 395 times the founding price at month 21: 96% of the households' shares.
- Four smaller foundings agree [M]:

| houses | people at month 21 | Construction's share of households' shares | its price / founding price |
|---|---|---|---|
| 6 | 98 | 86% | 50× |
| 10 | 111 | 91% | 89× |
| 15 | 127 | 96% | 179× |
| 25 | 144 | 95% | 215× |

### 2.4 Dividends and the taxes households pay
- `creditDividend` adds `shares × perShare` to savings and to `investmentIncome`, untaxed.
- The taxes in force:
  - the wage tax: `TaxPolicy`'s wage base, 15%, with a band's offset;
  - the pension contribution: `SocialSecurity.DEFAULT_CONTRIBUTION_RATE`, 5.95%;
  - the profit tax every company pays before it pays a dividend: the profit base, 15%.
- No gain is taxed. `FundLedger` keeps an adjusted cost base by Canada's average-cost rules, for the city's fund only.
- Households realise gains in three places today:
  - `Exchange.Settle.settle` when the seller is a cell;
  - the waterfall's sales (`rebuildLiquidity`);
  - the leavers' shares, sold as `EMIGRANTS` with no cell's books.

### 2.5 Spending from wealth
- `WEALTH_SPENT_A_MONTH` = .0033: every cell spends a third of a percent of its net worth a month on top of what it spends from income. Its 2026-09-17 banner: a stock that compounds can only be bounded by a drain that reads the stock.
- With the plan's other terms (`MARGINAL_PROPENSITY` .80 above subsistence, a 2% real return ★), a cell's resting wealth is 0.2 × (take-home − subsistence) ÷ (.0033 − .02/12): 5.7 years of take-home for the unskilled, 9.2 for the Elite (`out/flows.txt`, ★ subsistence $1,200 a month an adult). Canada's 35-64 hold 9.9 years [S§1, derived in 2.6].

### 2.6 Measured, and against Canada

Per household, retired against working, at the checkpoints [M] (`out/probe.txt`):

| month | people | retired ÷ working, net worth | the same, savings less debt | seniors alone ÷ working | elder couples ÷ working |
|---|---|---|---|---|---|
| 120 | 2,150 | 0.36 | 0.13 | 0.23 | 0.38 |
| 600 | 9,458 | 1.19 | 0.36 | 0.61 | 0.88 |
| 1,200 | 37,834 | 1.74 | 0.94 | 0.15 | 2.27 |
| 2,400 | 300,037 | 2.62 | 2.48 | 0.32 | 12.5 |
| 4,000 | 800,102 | 1.63 | 1.01 | 0.09 | 9.34 |

- Seniors living alone are 46-50% of retired households from month 600 on (38,692 of 78,164 at month 4,000).
- `Household.netWorth()` counts savings less debt only. On that measure a senior living alone held 2% of a working household's at month 1,200 (5.1 against 230.7) and 8% at month 4,000 [M].
- At month 4,000 a working household holds 18.4 years of its take-home; the retired 79 years of their pension; seniors alone 6.0 [M].
- **Canada's** figures [S§1] (national accounts, 2025, per household), with the stages derived from them:

| stage | net worth ÷ disposable income |
|---|---|
| under 35 | 4.8 years |
| 35-64 | 9.9 years (10,866M ÷ 1,101.5M, the three groups weighted by SFS's family counts 3,109k, 2,733k, 3,079k) |
| 65+ | 16.1 years |

- Retired per household: $1,269,283 against $1,218,014 for the 35-64, a ratio of 1.04 (derived).
- The 65+ dissave 14% of their income, 0.9% of their net worth a year [S§5].

**The toy** (`flows.py`, six tiers and the retired, AgeBand's rates, the game's MPC, drain, buffer and pension, ★ subsistence and return; `out/flows.txt`):

| rule | retired ÷ established, per adult, years 50 / 100 / 200 |
|---|---|
| A, today's net-change pool | 0.11 / 0.11 / 0.11 |
| B, flows carry their money (HS1) | 0.86 / 0.97 / 1.30 |
| B with two stages (HS1 + HS2) | 0.83 / 0.95 / 1.28 |

The toy has no founders' grant and one cell a row, so rule A's 0.11 is the mechanism alone: the retired live on what the pension lets them save, plus the money of the retired who died. The game's row average is higher only where old founders' shares and the pool's shuffles happened to land, and its largest retired cell sits near the toy's figure.

### 2.7 Compute, measured
Exact inclusive wall time per method (HhAgent, one run on an idle machine, 100-month windows; share of the model month, `Game.simulateMonths`) [M]:

| window | people | model month ms | households' books (`HouseholdBalance.advanceMonth`) | their bond orders (`BondMarket.postHouseholds`) | their share orders (`Exchange.postHouseholds`) | together |
|---|---|---|---|---|---|---|
| 300-400 | 7,583 | 8.5 | 14.4% | 18.8% | 1.4% | 34.6% |
| 600-700 | 10,004 | 7.5 | 12.6% | 21.8% | 1.2% | 35.6% |
| 1,200-1,300 | 42,925 | 8.1 | 6.5% | 21.4% | 2.1% | 30.0% |
| 2,300-2,400 | 300,037 | 7.8 | 6.3% | 14.2% | 3.5% | 24.0% |
| 3,900-4,000 | 800,102 | 21.4 | 11.4% | 26.8% | 1.0% | 39.2% |

- Each of the three scales with the cells (the bond orders with cells × bonds). JFR's samples agree within their noise: over months 2,300-3,300 (530 samples), 8.9% and 15.5% [M].
- Inside the books: `followThePeople` is almost all of it; `moveStock` 2.8-5.1%, `foldEmptyCells` 2.1-9.7% (the cells the census leaves under half a household, emptied into the rest). `FamilyModel.rebuild` is under 1.1%.
- **Today's counts:** the cells are fixed at 78; the stocks grow with the bonds, 86 at month 2,400 and 175 at month 4,000 (55 and 144 bonds) [M].
- **After the batches (derived):**
  - cells: 7 young + 11 established working shapes × 6 tiers = 108, plus 24 retired, 8 outside and 1 owner = 141, against 78 (× 1.81);
  - stocks: + 25 bases + 1 loss carried, × 1.30 at month 2,400 (112 ÷ 86) and × 1.15 at month 4,000 (201 ÷ 175), which only `moveStock` reads.
  - The per-cell work grows by 81%: 0.81 × 24.0% = +19.4% at month 2,400, 0.81 × 39.2% = +31.8% at month 4,000.
  - `moveStock` grows by (1.81 × 1.30 − 1.81) × 2.8% = +1.5% at month 2,400, and (1.81 × 1.15 − 1.81) × 5.1% = +1.4% at month 4,000.
  - So **+21% to +33% on a model month**: 21.4 ms becomes about 28.5 at month 4,000 (× 1.33), and 7.8 about 9.4 at month 2,400 (× 1.21).

## 3. The design, on the code

### 3.1 Flows that carry their money (HS1, `followThePeople`)
The census keeps its pool, for what it is good at: the reshuffles nobody can trace. A child turns six and a `COUPLE_BABY` becomes a `COUPLE_CHILD`; the job mix moves a point of every shape up a tier; five singles take a flatshare.

Before the pool runs, the month's own transfers move first. Each carries the per-grown-up average of every stock in `stocks()` from the cell it left, following `carryGraduatesLoans`' pattern: move the flow's stocks, take its households out of `before[]`, add them to the destination's. All the counts are known before the households' month (`docs/month-order.md`: `cohorts.advanceMonth` at 31, `cohorts.leave` at 41, `cohorts.migrate` at 48; the households after).

| flow | people (read from) | from | to | what moves |
|---|---|---|---|---|
| retiring | `cohorts.getPromoted(ADULT)` (HS2: the established bucket's) | working cells by grown-ups (HS2: established) | `retired[t]`, the tier left (3.2) | the retiree's grown-up share of every stock |
| a partner dies | the band's deaths × its coupled share | a couple cell | the alone cell of the same row | the whole household's stocks: the survivor keeps them |
| dying alone | the band's deaths, the rest | the cell | the estate (3.3) | the grown-up share of every stock |
| leaving | `migrate(net)` with net < 0, and `leave(band, n)` | the band's cells, pro rata | out of the city | the grown-up share; shares and bonds go as today (sold as `EMIGRANTS`, the bonds held from abroad) |
| arriving | `migrate(net)` with net > 0 | none | the cells that grew | the opening buffer, as today |
| ageing (HS2) | the young bucket's promoted | young cells of tier t | established of tier t | the grown-up share of every stock |

- **Inside a group**, a destination shape gets the flow by the census's own growth there, and by grown-ups where nothing grew. Whatever the flows put in a cell becomes part of its `before[]`, so the pool nets only what the flows did not explain.
- **Conservation is exact by construction**: each flow subtracts what it adds. Positions clamp at zero as `moveStock` does (its 2026-09-16 header).
- **A stock added later is carried for free.** The credit-and-housing study's mortgage, BK2's per-holder discount and HT1's basis each need only their line in `stocks()`.

### 3.2 The retired keep their former tier (HS1)
- `RetiredHousehold` takes a tier: 4 shapes × 6 tiers = 24 cells. `row()` stays `RETIRED_ROW`, so every sum and screen by row is unchanged.
- `HouseholdBalance` carries the retired's tier mix per shape (24 shares, saved), moved by the flows: retirements add to their tier, and deaths and leavers take pro rata. `FamilyModel` still decides how many retired households of each shape there are, and the balance splits each shape across tiers by the mix.
- `cell(FamilyStructure)` has three callers in `PeopleScreen` and eleven harnesses. It becomes `cells(shape)` and a sum.
- The tier also gives an earnings-related pension (the CPP is one; the game's is flat at 45% of an unskilled wage) the cells it would need. That pension is the wage study's to propose.

### 3.3 Estates (HS1)
- **The survivor keeps everything:** the couple's whole position moves to the alone cell (Canada's spousal rollover [S§3]).
- **Someone dying alone leaves an estate.** It settles their revolving debt from their savings, and writes off any shortfall and the student loan as today's leavers' are. The rest passes in kind (no market moves):
  - to the established working households of the dead's former tier, by grown-ups (★ call 3);
  - an established adult's estate to the young of the tier (HS2; before HS2, the tier's own cells);
  - with no heir in the tier, to the city's established cells;
  - with none at all, to the treasury as unclaimed property: a revenue line, `estatesEscheated`.
- **Reference:** about 26% of Canadian families have received an inheritance; the median is $85,100 for homeowners and $29,800 for renters [S§6].

### 3.4 Two adult stages (HS2)
- **The pyramid:** `PopulationCohorts` stores the adults in two buckets, 18-34 and 35-69, and every existing caller reads `ADULT` as their sum (`AgeBand`'s own note: "nothing outside this package should ever assume the five buckets ARE the storage"). The young move up at 1/204 a month and the established retire at 1/420, both at the adult mortality.
  - Two compartments in a row give the adult years an Erlang spread instead of an exponential one. A city of young migrants then retires fewer people in its first decades: the pyramid's retirement flow changes, and with it the trace.
- **The households:**
  - `FamilyModel` forms each stage from its own adults;
  - the young take `SINGLE_ADULT`, `COUPLE`, `SINGLE_PARENT`, `COUPLE_BABY`, `COUPLE_CHILD`, `COUPLE_BABY_CHILD` and `SHARED_ADULTS`; the established take all eleven;
  - the children are split between the stages by ★ shares: babies ½ young, children ¼, teens none. The reason: Canada's mean age at a birth is 31.9 [S§7], so a baby's parents straddle 35, a child's (6-12) mostly do not, and a teen's never.
- **The pool's group** becomes (tier, stage): a baby moves a young couple's money inside the young of its tier.
- **Cells:** 108 working, against 66. The full cross would be 132: teens and large families are not formed young.

### 3.5 Business owners (BO1)
- **A row of their own**, `OWNER_ROW`, appended after `PRISON_ROW` (`padRows` pads arrays by row). One cell, `OwnerHousehold`, of two-adult households: no tier, no job, no wage (Jerus, 2026-11-02: "No job, lives of dividends or sells or whatever"). Its income is dividends, coupons and what it sells.
- **The founders' shares go to it alone.** `grantFounders` credits the owner cell, at a basis of the founding price (so founding is no gain, HT1). With no owner household (a bare fixture), today's rule.
- **Everyone else still holds shares**, as Jerus asked ("the others still have shares, they just dont get the initial ownership"): `subscribe` and the exchange are unchanged.
- **One family per listed company** (★ call 1, Jerus's C): the line holds one household for each company that has listed and not gone broke. 9 families in the 328-person city of month 21, at most 25 [M].
  - A company's listing (`Equity.offer`'s founders' branch, `listIfUnlisted`) adds a family: the census places two established adults in the line, after the seniors and before every other shape, and they bring what the pool gives them.
  - The line's count is saved.
  - **One cell, not one per company.** Each family holds an equal part of every founding stake; the screen can still name the families by company. A cell per company would show the Construction family apart, for 24 more cells: about +7-12% more on a model month by §2.7's rule (24 ÷ 78 × 24-39%), mostly the bond orders.
- **A company that goes broke sends its family back** (Jerus: "if sector goes broke he just joins the regular pop pool"). Broke means one of two things:
  - its equity is taken (`Equity.takeAllForCity`, a failed bank's resolution: today's only failure);
  - or its fair value sits at the exchange's floor (`Exchange.MIN_FAIR`) for `OWNER_BROKE_MONTHS` (★ 12) running, since a sector that sheds its plant never fails on its own.
  - The family leaves the cell for the established working cells by grown-ups (HS1's flow; before HS1, the pool). It takes its 1/N of the line's savings, money abroad and bonds, and none of its shares: the broke company's are worthless, and the other stakes stay with the families whose companies live.
  - The census then gives the two adults a tier by the job mix.
- **Never retires:** the line is a family, not a person. Its cell is outside the retiring and dying flows; its people age in the pyramid like anyone's.
- **Sells slowly:** each month it offers `OWNER_SELL_A_YEAR` (★ 5% a year) of each holding on the exchange, as a new seller class `BY_OWNERS`. Its stakes also dilute at every offering.
- **For scale:** in the US the top 1% hold 50.9% of corporate equity and 53.0% of private business equity [S§2]; founders and families ("strategic individuals") hold 8% of the world's listed market capitalisation [S§8].
- **Spends** by the plan's own rule. At a fortune that is the luxury counter's whale (see 3.7).

### 3.6 A cost basis, and the tax on gains and dividends (HT1)
- **The basis is a stock:** `Household.basis[company]`, the adjusted cost base per household by the average-cost method (`FundLedger`'s rules, which are Canada's). It is one more entry in `stocks()`, so the census and the flows carry it with the shares, and mixing two averages is what the average cost is.
  - **Buying** adds the price paid: `subscribe`, and the exchange's fills to a cell in `Exchange.Settle.settle`. Founders' shares come in at the founding price.
  - **Selling** realises the proceeds less basis × sold ÷ held: in `settle` when a cell sells, and on the waterfall's sales.
  - **Splits** leave the basis standing.
- **Capital gains:** gain × the inclusion rate (★ call 4; ½ in Canada, ITA 38(a), the 2/3 proposal cancelled [S§3]) × the holder's wage-tax rate (base plus band offset; the retired, the out of work and the owners at the base).
  - **When it is due:** at the sale; at a death (the deemed disposition, before the heirs get the rest); and on leaving (the departure tax, ITA 128.1, struck in HS1's leaving flow out of the savings the leaver takes, since the exchange sells a leaver's shares as `EMIGRANTS`, no cell's sale).
  - **A survivor** takes the basis over untaxed.
  - **Losses:** a month's net loss carries forward in one more stock, `lossCarried`, and offsets later gains.
- **Dividends, by integration** (★ call 2): the dividend tax is max(0, wage − profit) ÷ (1 − profit).
  - At 15% and 15% that is zero, which is also what Canada's credit gives at the lowest bracket: 1.38 × 15% − 1.38 × 15.0198% ≈ 0 [S§3].
  - A player who raises the Elite band's wage tax to 30% taxes its dividends at 17.6%. Today's 15/15 dividends already bear the same 15% a wage does, through the profit tax.
- **Where the money goes:**
  - two treasury revenue lines, the journal and `MoneyAudit`'s names;
  - `investmentIncome` is net of the tax;
  - one dial on the Policy tab's tax pages: the inclusion rate, 0 to 100%.

### 3.7 Spending from wealth (the wealth study's), and the retired
- This study proposes no change to `WEALTH_SPENT_A_MONTH`. With HS1 the flat drain already gives the retired about the established's wealth (the toy: 0.97 at year 100), as Canada's 1.04 does.
- Three figures for the wealth study, which owns the rate:
  - the Fed's 2.7-3.3 cents and the Bank of Canada's under 0.5 for stocks [S§4];
  - RRIF minimums of 5.28% at 71 to 8.51% at 85 (withdrawals, not spending) [S§5];
  - the owners' fortune, which is all stock.

### 3.8 What the other studies need
- **Household credit and housing:** a mortgage or home equity is a line in `stocks()`, and HS1 carries it. A mortgage at death is settled by the estate before the heirs. First-time buyers are HS2's young (Canadian ownership 44% under 35 [S§1]).
- **Where the wealth piles up:** §2.3's Construction prices, §2.6's working households at 13-32 years of take-home against Canada's 9.9, and the owners (BO1) as the top.
- **Wages:** the retired's former tier (3.2).

## 4. The save
- **HS1: `SAVE_FORMAT` + 1, and `OLDEST_LOADABLE_FORMAT` raised to it.** The retired cells are renamed by tier, so `restoreCells` (by name) would leave an old save's retired money in no cell: loaded wrongly, not incompletely. New: the retired's tier mix (24 figures) in `DataSave`.
- **HS2: + 1 again.** `PopulationCohorts.restore` finds bands by name and would put every old adult in one bucket, and the working cells are renamed by stage.
- **BO1: no bump.** An old save loads with an empty owner line and everyone keeping the founders' shares they had: incomplete, not wrong. New: the line's count.
- **HT1: + 1.** An old save's basis would read zero and its first sale would be taxed on the whole price. New: the basis and `lossCarried` slots appended (`CELL_SLOTS_BEFORE_BASIS`).
- No conversion code anywhere.

## 5. Premises that change, and the new assertions

| check | before | after |
|---|---|---|
| HouseholdCheck, OutsideCheck | the retired as 4 cells; the pool moves every stock | 24 retired cells; flows first, the pool for the rest |
| EquityCheck, InvestCheck | founders' shares to every cell by grown-ups | to the owner line (BO1) |
| HouseholdMemoryCheck, PopulationCheck | one adult bucket | two (HS2); `ADULT` their sum |
| FundLedgerCheck | the only cost basis | the households' too (HT1), the same rules |
| SaveFileCheck, ReadPathCheck | | each new saved field, both ways |

- **HouseholdFlowCheck** (HS1):
  - fixtures that cause each flow: a retirement carries the cell's average to the cent per stock; a partner's death leaves the survivor everything; an estate reaches the tier's established; a leaver takes the average out;
  - every stock conserved to the cent;
  - and, counted in a real run, how much moved by flow and how much by the pool.
- **OwnerCheck** (BO1): the grant reaches the line only; the others buy at offerings; the line sells `OWNER_SELL_A_YEAR`; its count moves only by its births; the money audit is exact.
- **StageCheck** (HS2): residence 204 and 420 months; ageing carries the money; no teen shape among the young; the pool's group is (tier, stage).
- **CapitalGainsCheck** (HT1): the basis to the cent as FundLedgerCheck; a sale's gain; the deemed disposition and the departure tax; dividends untaxed at 15/15 and at (wage − profit) ÷ (1 − profit) when a band is raised; the audit holds.

## 6. Risks, and what to measure first
1. **Time.** +21-33% on a model month (§2.7), most of it the households' bond orders per cell. HS2 measures it at its gate. Over budget (GT1's), `BondMarket.postHouseholds` posts one order per (row, stage) and splits the fills by holdings.
2. **BO1 moves demand.** Today early dividends are 10 times a worker's take-home (§2.3). With them gone to a few owner families, early spending falls; the owners' own spending is the drain on a fortune. The ensemble says how far.
3. **HS1 moves wealth to the retired.** That is the point, but the working rows lose the retirees' money they hold today. Offerings, which the working rows fund, may fill more slowly.
4. **The owners' fortune** comes from Construction's price (§2.3). If the wealth study finds that price wrong and mends it, BO1's line is less rich, not differently built.
5. **The owners take no posts** (Jerus's call): at month 21, 9 families are 18 of the city's ~190 adults [M], so a young city's workforce is about a tenth smaller than today's. In a city of thousands it is nothing. BO1's ensemble says what it does to a founding.
6. **HS1 and the wealth study's HL1.** HL1 puts the households in the money audit; HS1's leavers take their money through that identity. If HL1 is queued, HS1 goes after it.

## 7. The calls, each with a recommendation, and Jerus's answers (2026-11-02)

| ★ | call | options | recommended, and why | answer |
|---|---|---|---|---|
| 1 | How many owner families | A: one line that grows only by its own births; B: 1% of households; C: one per listed company | A: "few in nature and very rich" stays true at every size. Under B a 10M city has about 45,000 owner households (2.23 people a household [M]) and an ordinary fortune; under C the 328-person city of month 21 has 9 | **C**, with no job; a broke company's family rejoins the population |
| 2 | How dividends are taxed | A: by integration (zero at the defaults); B: a flat dial at 15% (taxed twice: 27.75% against a wage's 15%); C: untaxed, as today | A: the default city is unchanged, it is Canada's own principle, and a raised band reaches its dividends | A |
| 3 | Who inherits | A: the established of the dead's former tier; B: every established adult alike; C: by the heirs' net worth | A: old money stays in the tier it came from. B flattens wealth each generation; C compounds the rich | A |
| 4 | How much of a gain is taxed | A: half, at the holder's wage rate (Canada), as a dial; B: all of it; C: none, as today | A, the dial 0-100% at 50% | "A taxable rate, default 50%": A |
| 5 | Retirement age | A: keep 70 (the pyramid's senior band); B: 65, Canada's average (65.4 [S§7]) | A for these batches. B moves the workforce by a tenth of the adults and every trace; it is its own batch, with the pension (the wage study's) | A |
| 6 | Adults living with their parents ("perhaps even three adults") | A: not now; the young stage covers them; B: a couple and a grown child (6 more cells; the child earns at the parents' tier) | A: B breaks "one tier per household", which `FamilyModel` rests on. In Canada 28.1% of 25-29s live with a parent [S§7] | A |
| 7 | The order | A: BO1, HS1, HS2, HT1, then HS3; B: HS1 first | A: BO1 is the smallest and fixes the most visible figure. HT1 needs HS1's estates; HS3 needs KP7's kept page | A, at the end of the queue |

## 8. The batches, and the ★ estimates

Every batch is one batch session with Sonnet slices (`docs/batch.md`); a ★ in the first column marks a trace that moves, so the batch needs the ensemble.

| batch | what | files | saved · checks | size |
|---|---|---|---|---|
| **BO1 ★** Business owners | one family per listed company, no job; the founders' grant to them; the slow sale; a broke company's family back into the population | `Household.ROWS`, new `OwnerHousehold`, `HouseholdBalance` (cells, `grantFounders`, the sale), `FamilyModel` (owners after seniors), `Exchange` (`BY_OWNERS`), `HouseholdAccounts`, a People row | the line's count · OwnerCheck; EquityCheck, InvestCheck | one session, 1-2 slices, ~600 lines |
| **HS1 ★** Money follows people | flows before the pool; the survivor; estates; leavers take theirs; the retired by former tier | after the wealth study's HL1 if queued; `HouseholdBalance` (`followThePeople`, flows, estates), `RetiredHousehold`, `cells(shape)`, `DataSave`, the three `PeopleScreen` callers | SAVE_FORMAT + 1 · HouseholdFlowCheck; HouseholdCheck, OutsideCheck | one session, 2 slices, ~1,200 lines |
| **HS2 ★** Two adult stages | two adult buckets; each stage's shapes; children by stage; the ageing flow; the pool by (tier, stage) | `PopulationCohorts`, `FamilyModel`, `WorkingHousehold`, `HouseholdBalance`, `HistorySave`, the pyramid on People | SAVE_FORMAT + 1 · StageCheck; PopulationCheck, HouseholdMemoryCheck; the timing of §6 risk 1 | one session, 2 slices, ~1,200 lines |
| **HT1 ★** Gains and dividends taxed | the basis; realised gains at sale, death and leaving; the integration rule; the inclusion dial; two revenue lines | `Household`, `HouseholdBalance`, `Exchange.Settle`, `Equity`, `TaxPolicy`, the treasury's lines, `MoneyAudit`, `PolicyScreen` | SAVE_FORMAT + 1 · CapitalGainsCheck; FundLedgerCheck | one session, 2 slices, ~900 lines |
| **HS3** The household money page | the page by stage, the retired by former tier, the owner line, estates and the investment taxes in the month's lines; after KP7 keeps that page | `PeopleScreen` | none · words probe | one session, Sonnet whole, ~400 lines |

The ★ estimates, each a named constant:
- `OWNER_BROKE_MONTHS` = 12: a year at the floor, the window `Migration.DECLINE_MONTHS` gives a tier before its people give up.
- `OWNER_SELL_A_YEAR` = .05: a founding stake falls to about a fifth in 30 years (.95³⁰ = .21) before offerings dilute it further, towards the 8% of listed market capitalisation founders and families hold [S§8]. No open-licence study of founder sell-downs was found.
- The children's split by stage (babies ½, children ¼, teens 0 young): from the mean age at birth, 31.9 [S§7].
- Only in the toy: a subsistence of $1,200 an adult a month and a 2% real return. Neither goes in the game.
