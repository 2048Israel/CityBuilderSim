# The luxury budget (LQ1, 2026-11-17)

A study, no model change. On main 0.9.139 the households' luxury counter
takes 13-19% of GDP and Luxury Retail's margin is 8-11% of it, the median
year of three cities, and 36% and 22% in seed 0's old city, where BEA's
other goods are 9.5% of GDP and other retail's value added 3.3%. Since LX1
put the shops' posts at BEA's payroll share of that margin, they staff 7-10%
of the city's posts, and 19% in the old city (a quarter of the businesses'
workers). This measures the budget by tier and by the city's age, what it
buys, and the shops' staff and margin, on the default playtest and an Earth
city; sets them against BEA, the Census, BLS and the Fed; finds where the
budget comes from in the code and why it runs high; prototypes two changes
on three seeds; and answers REV-LX1-1 on the Department Store. It ends in
three calls (section 8) and two batches (section 9).

WHY: LX1 (0.9.138) fixed the shops' sales per post, so their payroll is
BEA's 41.7% of their margin, and that turned a margin which had been a few
workers' into a quarter of the old city's. It left the question this
answers: is the margin the posts are struck on the right size? The counter
is not free to cut. `LuxuryRetail`'s header says it exists as the drain on
the households' hoard, and spec-wealth (its 3.6) keeps
`WEALTH_SPENT_A_MONTH` as the bound that turns the pile back, so a smaller
budget is a bigger pile unless the money goes somewhere else.

## 0. In short

- **The budget is the wealth term.** 99.7-99.9% of it is a third of a
  per cent of net worth a month (`HouseholdBalance.WEALTH_SPENT_A_MONTH`,
  .0033). The surplus term (`LUXURY_SHARE_OF_SURPLUS`, half of what income
  leaves) is nil for any household with a fortune, because the grocer's
  `want` carries the same wealth term (`Household.plan()`, "AND THE FORTUNE
  ALSO ASKS FOR WATCHES").
- **It runs high for two reasons that multiply.** The rate is the
  literature's (4% a year; the Fed's staff 3.5 cents a dollar), but it is
  the whole of the wealth effect, which in the US moves all of consumption
  (3.5 cents on $182 trillion is 31% of spending), and here it buys one
  imported good. And the pile it reads is 10-17 years of disposable income
  where the Fed's Z.1 has 7.9, and 32 in the old city. The budget is 60% of
  disposable income at the median against BEA's other goods' 12.8%.
- **The shops keep 2.66 times the landed cost**, a gross margin of 62%,
  where the specialty stores that sell such goods keep 1.92 (48%, the
  Census's 2022 retail survey).
- **Cutting the counter's share alone grows the pile** (prototype 1: net
  worth over GDP 1.3-2.4 times main's at m2400). **Cutting the margin alone**
  (prototype 2: the ceiling at 2.5, settling at 1.89) takes the shops'
  workers down 23-45% and their value added 13-35%, with the spend and the
  pile where they were; the workers go to the other sectors and the public
  posts, and the share of the people at work stays in main's own range, 38-47%.
- **The Department Store is never chosen** because it covers 62% of a
  Boutique's pieces a dollar. By the Census a department store has no edge
  to give it: it sells less per worker than a jeweller.
- **Recommended:** LQ2, the margin at the specialty stores'; then LQ3, a
  study of where a fortune spends besides the counter, the second drain a
  smaller budget needs. Three calls (section 8).

## 1. How it was measured

- **The runs:** the default playtest, `LongPlaytest`, seed 0 to m3996 and
  seed 1 to m2400, and the Earth city Rotterdam (`--earth`) to m2400, on main
  0.9.139 (`c471e961f`), each a `TempDirs` city. `luxury-budget/run.sh` is
  LS1's: a copy of `LongPlaytest.java` with `LuxuryProbe.month(g)` after
  `g.simulateMonths(1)`. Given `PATCH`, it patches copies of the files a
  prototype touches and puts them first on the classpath; the tree is not
  touched.
- **The probe** (`luxury-budget/LuxuryProbe.java`) sums a year's flows
  before dividing, since a share is a ratio of two flows and one month is
  noise. It reads the national accounts, the households' wages and
  disposable income, the budget and its two terms (the wealth term struck
  again from the fields the plan reads), what the gates pass, the pieces
  wanted and sold, the meals, net worth and its parts, Luxury Retail's books,
  workers and posts, the shops by type, and every cell's budget. Value added
  is revenue less inputs and utilities, as in LS1.
- **The tables** are in `luxury-budget/out/`:
  - `main-report.txt`: each run at m240, 600, 1,200, 2,400 and its last
    year, the median year from m240, and the budget by kind of household;
  - `cmp-share40.txt` and `cmp-margin.txt`: main against each prototype;
  - `cmp-long.txt`: seed 0's old city on all three.

  The CSVs were not kept; `run.sh`, then `report.py` and `compare.py`,
  remake them.
- **The real figures** are in `luxury-budget/real/` (their rows in `licences.md`): each CSV with its
  table, URL and licence in `sources.md`, and the scripts that made it from
  the downloads (the downloads, 15 MB, not kept).

## 2. The budget on main

The median year from m240, seed 0's last year in brackets. Shares in per
cent, stocks in years of the flow.

| | seed 0 | seed 1 | Rotterdam | real (section 4) |
|---|---|---|---|---|
| the budget over disposable income | 59.8 (132) | 40.6 | 68.6 | other goods: 12.8 of DPI |
| ...of it the wealth term | 99.7 (99.2) | 99.8 | 99.9 | |
| ...of it the gates pass | 99.5 (98.2) | 99.8 | 99.6 | |
| pieces sold over pieces wanted | 88.0 (92.8) | 65.4 | 65.1 | |
| the spend over GDP | 18.4 (36.0) | 13.0 | 19.1 | other goods: 9.5 |
| the spend over consumption | 46.8 (64.2) | 31.0 | 39.0 | other goods: 14.1 of PCE |
| consumption over GDP | 42.8 (56.1) | 44.5 | 45.9 | PCE: 67.6 |
| disposable income over GDP | 36.3 (29.9) | 48.6 | 41.0 | DPI: 74.5 |
| net worth over disposable income | 14.7 (32.2) | 10.2 | 16.7 | 7.9 |
| net worth over GDP | 5.7 (9.6) | 4.9 | 6.6 | 5.9 |
| meals over GDP | 7.8 (9.8) | 6.6 | 8.9 | food services: 4.2 |

**By the city's age** (seed 0, the year to each month):

| | m240 | m600 | m1200 | m2400 | m3996 |
|---|---|---|---|---|---|
| the budget over disposable income | 42 | 34 | 39 | 46 | 132 |
| net worth over disposable income | 10.9 | 8.2 | 9.6 | 11.7 | 32.2 |
| the spend over GDP | 19.7 | 14.6 | 10.4 | 16.5 | 36.0 |

The young cities hold their pile at 8-12 years and the budget at a third to
a half of income. The old one's pile triples, and the budget follows it.

**By kind of household** (seed 0). Each cell gives the kind's share of the
budget, then the budget over the kind's own disposable income:

| kind | m240 | m2400 | m3996 |
|---|---|---|---|
| unskilled | 68 / 45 | 18 / 29 | 18 / 77 |
| skilled | 25 / 42 | 36 / 52 | 25 / 97 |
| college | 6 / 37 | 25 / 42 | 17 / 107 |
| professional and above | 0 | 4 / 33-39 | 3 / 83-103 |
| retired | 0 | 16 / 179 | 34 / 746 |

Seed 1 and Rotterdam have the same shape (`main-report.txt`).

The budget is a flat share of wealth, so it does not rise with income the
way a luxury does:
- in the young city the unskilled spend 29-45% of their income at the
  counter, about what the college-trained do;
- the retired, living on a pension and a lifetime's savings, spend seven
  times their income in the old city.

BLS's consumer survey has discretionary goods and services (apparel,
furnishings, entertainment, personal care) at 10.5% of spending in the
lowest fifth by income, 12.8% in the highest and 13.5% in the top tenth
(section 4).

## 3. What it buys, and the shops

**The good.** The counter sells one good, `Good.LUXURIES`, imported (by
design, the header says), landed at $9,000 a piece at 2025's prices. The
landed cost goes abroad as imports; the margin is the shops' value added.

**The margin.** The shops charge what `LuxuryRetail.strikeMargin()`
reaches, between `MARGIN_FLOOR` (1.25) and `MARGIN_CEILING` (4.0), by the
queue's share of queue and counters, q/(q+cover). The planner builds until
the queue is within `BusinessInvestment.TARGET_HEADROOM` (5%) of the
counters. So the margin settles where q = 1.05 cover:
1.25 + 2.75 x 1.05/2.05 = 2.66. It is 2.5-2.9 in every year of every run.

| | seed 0 | seed 1 | Rotterdam | real |
|---|---|---|---|---|
| Luxury Retail's value added over GDP | 10.9 (22.4) | 7.7 | 11.4 | other retail 3.3; with general merchandise 4.0 |
| its payroll over its value added | 35.1 (26.2) | 41.6 | 38.2 | other retail 41.7 |
| its workers over the posts staffed | 9.5 (19.1) | 7.0 | 10.4 | other retail 4.4 of jobs; with general merchandise 6.4 |
| ...over the businesses' workers | 12.1 (24.2) | 8.9 | 13.6 | |
| the margin over the landed cost | 2.6 | 2.6 | 2.6 | specialty stores 1.92; all retail 1.46 |
| Boutiques (Department Stores) | 1,218 (61,024), 0 | 109, 0 | 161, 0 | |

LX1 holds: payroll is BEA's share of the margin. What is out of line is
the margin's size:
- the spend is 1.9 times BEA's other goods against GDP;
- the shops add 59 cents on a dollar of it, where BEA's other retail and
  general merchandise add 4.0% of GDP on other goods of 9.5%, about 42
  cents.

The workers follow the value added. LX1 struck the posts on it, so 9.5% of
the staffed posts is BEA's payroll share of a margin 2.7 times BEA's.

## 4. The real figures

| figure | value | source | licence |
|---|---|---|---|
| PCE, disposable income, GDP, 2025 | $20,870bn, $22,986bn, $30,861bn: PCE 67.6% of GDP, DPI 74.5% | BEA NIPA 2.1, 1.1.5 (`income.csv`) | public domain |
| other goods, 2025: goods less food at home, motor vehicles, gasoline and other energy, pharmaceuticals and tobacco | $2,940.6bn: 14.1% of PCE, 12.8% of DPI, 9.5% of GDP | BEA NIPA 2.4.5U (`pce.csv`) | public domain |
| jewelry and watches, 2025 | $106.2bn: 0.51% of PCE, 0.34% of GDP | 2.4.5U, line 63 | public domain |
| food services, 2025 | 6.2% of PCE, 4.2% of GDP | 2.4.5U, line 235 | public domain |
| value added, 2023-25 | other retail (4A0) 3.29% of GDP, general merchandise 0.73%, food services 2.33%; compensation over value added 41.7%, 59.5%, 66.5% | BEA GDP by industry (LS1's extracts, `labour-share/`) | public domain |
| jobs, 2025, of nonfarm | retail 9.75%, other retail 4.35%, general merchandise 2.04%, department stores 0.59%, jewelry, luggage and leather 0.081%, food services 7.74% | BLS CES (`employment.csv`) | public domain |
| gross margin over sales, 2022 | all retail 31.3% (1.46 times cost); the specialty stores (furniture 51.0, electronics 29.5, clothing 49.8, sporting goods, hobby and books 45.6, miscellaneous 54.0) by their sales 47.9% (1.92); with general merchandise (26.8) 36.8% (1.58); department stores 41.8% (1.72) | Census ARTS 2022 (`arts-margins.csv`) | US public domain (17 U.S.C. 105), as MA1 read it; no endorsement implied |
| stores, 2023 | 19,897 jewellers with 106.9k employees, 5.4 a store, $2.29m a store and $427k an employee of 2022's sales; 2,434 department stores with 195.7k, 80.4 a store, $17.9m and $223k | Census CBP 2023 with ARTS 2022 (`cbp-establishments.csv`) | as ARTS |
| household net worth, end 2025 | $182.3tn: 5.91 years of GDP, 7.93 of DPI | Fed Z.1 (`networth.csv`) | public domain |
| discretionary goods and services by income, 2025 | 10.5% of spending in the lowest fifth, 12.8% in the highest, 13.5% in the top tenth | BLS CE (`cex-quintiles.csv`) | public domain |
| spending out of wealth | 1-6 cents a dollar a year; Fed staff 3.5; the rule of thumb 3-5 | `wealth-mpc.md` | evidence only, not shipped |

Two cautions from `sources.md`:
- The department store counts are two populations. CBP's NAICS 2017 has
  195.7k employees; CES's NAICS 2022, with the discount department stores,
  has 934k.
- ARTS publishes jewellers' sales but no margin for them.

## 5. Where it comes from, and why it runs high

**The code.**

- `Household.plan()`, Household.java:1100-1136 and 1170:
  - `fromWealth = WEALTH_SPENT_A_MONTH x netWorth`, where net worth counts
    savings, the money abroad, the paper and the bonds at the month's ratios,
    less debt;
  - `want = subsistence + spendFactor x (fromIncome + fromWealth)`;
  - `surplus = spendFactor x max(0, afterFixed + investment income - wantAtNoReturn)`;
  - `luxuryWant = spendFactor x fromWealth + LUXURY_SHARE_OF_SURPLUS x surplus`;
  - `mealWant = MEAL_SHARE_OF_SURPLUS x surplus + MEAL_SHARE_OF_WEALTH x` the
    same wealth term.
- `HouseholdBalance.luxuryBudgetOf` (HouseholdBalance.java:2670) gives
  nothing to:
  - an empty cell;
  - one that cannot invest;
  - one locked out;
  - one going short.

  It gives at most savings less `SHARE_CUSHION_MONTHS` (3) of disposable
  income.
- `luxuriesWanted` (2648): the city's budget over the price struck, one
  queue.
- `LuxuryRetail.strikeMargin()` (303) and `plan()` (434); `MARGIN_FLOOR`
  (92) and `MARGIN_CEILING` (106).
- `buildings.json`, ids 69 and 70, the same in `BuildingManager`'s
  built-ins (1726-1760):
  - id 69, the Boutique: 7 pieces a month, $1,180, 8 posts, 6,000 of ground;
  - id 70, the Department Store: 80 pieces, $21,600, 91 posts, 90,000 of
    ground.
- `consumption.json` has no luxury line: the basket is the grocer's, and
  the counter stands outside it.

**Why it runs high**, in seed 0's median year:

    budget / disposable = 12 x .0033 x spendFactor x net worth / disposable
                        = 3.96% x 14.7 = 58%        (measured 59.8%)

Two factors multiply:
- **The counter takes the whole wealth effect.** The US at the same rate on
  its own pile would spend 3.96% x 7.93 = 31% of disposable income out of
  wealth. It spends 12.8% on other goods. So other goods are about two
  fifths of a wealth effect (12.8/31 = 0.41), and the counter's whole term
  is 2.4 times that.
- **The pile is 1.9 times the US's** (14.7 years of disposable income
  against 7.93).

2.4 x 1.9 = 4.6, against the 59.8/12.8 = 4.7 measured. Against GDP the gap
is smaller, 1.9 times (18.4% against 9.5%). The city's households hold half
the US's share of GDP as disposable income (36% against 74.5%), as LS1's
labour share has it.

**The rate is not the fault.** 4% a year is inside the literature's 1-6
cents and near the Fed staff's 3.5. Spec-wealth keeps it as the bound that
turns the pile back; spending out of wealth belongs to the households
study.

**The surplus term is dead weight here.** `want` carries the wealth term,
so for any household with net worth the surplus is nil: 0.1-0.3% of the
budget. The comment beside it says why, and that it waits on a divergence
in the share path. This study changes nothing there.

**Restaurants get half the wealth term too** (`MEAL_SHARE_OF_WEALTH`), on
top of the counter's: meals are 7-9% of GDP against food services' 4.2%.

## 6. The prototypes

Two prototypes, each on the three runs to m2400 and on seed 0 to m4000. The
tables give the median year m240-m2400, main then prototype. Seed 0's
median here is 4.5 years of GDP, where section 2's is 5.7, because that one
runs to m3996.

### 6.1 The counter's share

`proto-share.patch`: the wealth term's part of `luxuryWant` at 0.4
(`-Dlq1.share=0.4`), other goods' share of a US-sized wealth effect
(12.8/31). The rest of the term is left in the bank.

| | seed 0 | seed 1 | Rotterdam |
|---|---|---|---|
| the spend over GDP | 13.6 to 5.8 | 13.0 to 6.6 | 19.1 to 10.5 |
| Luxury Retail's value added over GDP | 8.3 to 3.5 | 7.7 to 4.0 | 11.4 to 6.2 |
| its workers over the posts staffed | 7.3 to 3.0 | 7.0 to 3.0 | 10.4 to 5.6 |
| net worth over GDP | 4.5 to 4.7 | 4.9 to 5.8 | 6.6 to 11.7 |
| ...at m2400 | 5.2 to 12.3 | 4.1 to 5.3 | 9.9 to 15.8 |

The budget comes in line with BEA's, and the shops with it. The pile
grows: what the counter does not spend is banked, so net worth climbs until
0.4 of a larger wealth term drains what the whole term did. Seed 0's spend
over GDP is back to 13.6% at m2400 (main 16.5%), on a pile 2.4 times
main's. Cutting the share alone gives a bigger pile, not a smaller budget,
unless the rest of the term goes somewhere.

### 6.2 The margin

`proto-margin.patch`:
- `MARGIN_CEILING` 2.5, so the margin settles at 1.25 + 1.25 x 1.05/2.05 =
  1.89, the specialty stores' 1.92;
- the Boutique's coverage from 7 to 13 (stock 21 to 39) and the Department
  Store's from 80 to 149 (240 to 447), the posts unchanged.

The margin a piece is 0.89/1.66 = 0.537 of main's, so payroll over the
margin stays at BEA's share.

| | seed 0 | seed 1 | Rotterdam |
|---|---|---|---|
| Luxury Retail's value added over GDP | 8.3 to 5.4 | 7.7 to 6.7 | 11.4 to 8.1 |
| its workers over the posts staffed | 7.3 to 4.1 | 7.0 to 5.4 | 10.4 to 5.7 |
| the spend over GDP | 13.6 to 12.1 | 13.0 to 15.0 | 19.1 to 17.6 |
| net worth over GDP | 4.5 to 3.8 | 4.9 to 5.1 | 6.6 to 7.1 |
| people at m2400 | 897k to 915k | 289k to 499k | 136k to 40k |

The shops' workers fall 23-45% and their value added 13-35%. The spend and
the pile stay within the seeds' noise: the households spend the same wealth
term on cheaper pieces, and more of them are sold.

**What the workers do instead.** The share of the people at work stays at
38-47% in every variant at every mark, main's own range. The posts the shops give up are
filled by the other sectors and the public posts (`cmp-margin.txt`); each
of those moves 5-10 points between years with what is being built.

**Read the ratios, not the head counts.** Town sizes at m2400 differ by up
to 3.4 times between main and a prototype, in both directions and under
both prototypes; Rotterdam has 136k on main and 40k under each. A played
city's path branches on any change.

### 6.3 The old city

<!-- OLD CITY -->

## 7. The Department Store (REV-LX1-1)

**Why the planner never chooses it.** `LuxuryRetail.plan()` scores each
luxury building by the margin its whole coverage would earn over the build's
price. That price, `BusinessInvestment.getCostOf`, is the work at the
builders' scarcity, the materials short and the ground. The score reads no
payroll and no city size.

| | Department Store | Boutique |
|---|---|---|
| pieces covered | 80 | 7 |
| cash cost | $21,600 | $1,180 |
| pieces a dollar | 0.0037 (62% of the Boutique's) | 0.0059 |
| ground a piece | 1,125 | 857 |
| posts a piece | 1.14 | 1.14 |

No city's prices turn that around. Under LQ2's coverage both rows scale by
the same 1.86, and the ratios stay. None of the runs built one: zero in
every year of every run, on main and both prototypes.

**Whether it should be chosen.** By the Census a department store has no
edge in costs over a specialty store:
- it makes 7.8 jewellers' sales with 15 jewellers' staff: $223,000 a worker
  against $427,000 (CBP 2023, ARTS 2022);
- its gross margin is lower: 41.8% against the specialty stores' 48%.

Real department stores hold 0.59% of US jobs (BLS) for reasons the game
does not model: one stop for many goods, and cheaper ones.

In the game the accounts cannot tell the two apart. The posts per piece are
the same, so a city of department stores and a city of boutiques have the
same payroll, value added and imports. What differs is the map and the
building count: seed 0's old city has 61,024 Boutiques.

Recommended: leave it unchosen (★3).

## 8. The calls for Jerus

**★1 What the counter stands for.**
- (a) Recommended: BEA's other goods. That is everything a household buys
  except food at home, vehicles, fuel, medicine and tobacco: 14% of what
  households spend. These are the goods whose share rises with income (the
  dial's own definition), and the shops' figures are then the specialty
  stores': posts at other retail's payroll share, a margin of 1.92.
- (b) Jewelry and watches only, what "luxury" says: 0.5% of spending. The
  budget would fall to a fortieth, and nearly all of the wealth term would
  need another home.

**★2 How much of the wealth term goes over the counter.**
- (a) Recommended: all of it for now. Then LQ3, a study of where the rest of
  a fortune's spending should go. One candidate is services the city would
  make (recreation, accommodation, personal care), which would also lift
  consumption toward BEA's 68% of GDP. The cut to about 0.4 comes after
  that, with the second drain beside it. Measured: a cut alone grows the
  pile.
- (b) Cut to 0.4 now, and spend the rest abroad (as travel). The drain stays,
  and imports rise by the cut; the city's GDP loses that spend.
- (c) Cut to 0.4 now with nothing beside it. This is prototype 1: the pile
  goes to 1.3-2.4 times main's net worth over GDP by m2400.
- (d) Keep all of it for good. After LQ2 the counter is still a third or more
  of the city's consumption and 1.3-1.9 times BEA's against GDP, on a pile
  that is spec-wealth's business.

**★3 The Department Store.**
- (a) Recommended: leave it as it is. A player may put one down; the planner
  never chooses it. By the Census it has no edge in costs, and the accounts
  would not change.
- (b) Give it a scale edge, so a big city builds them:
  - the planner scores the pieces a store would sell, the queue it would
    fill, not its whole coverage;
  - a full store costs a tenth less a piece than the boutiques it replaces.

  The map then has stores: the old city's 61,024 Boutiques become about
  5,300 stores, with the same accounts. It would be a batch after LQ2,
  touching `LuxuryRetail.plan()`, `buildings.json` id 70 and the built-ins,
  with a `LuxuryRetailCheck` section: a queue that fills a store builds one,
  and a smaller one builds boutiques.
- (c) Take it off the planner's list and the build card.

## 9. The batches

### 9.1 LQ2

**Luxury Retail's margin at the specialty stores' (★1 (a)).** One
builder. A model change, no save field.

**The change:**
- `LuxuryRetail.MARGIN_CEILING` from 4.0 to 2.5, so the margin settles at
  1.89 against the specialty stores' 1.92. Its comment's "four times the
  landed cost is roughly where real luxury retail sits" is corrected to the
  Census's figures (section 4).
- The Boutique's coverage from 7 to 13 and its stock from 21 to 39, and the
  Department Store's from 80 to 149 and 240 to 447, in `buildings.json`
  (ids 69, 70) and in `BuildingManager`'s built-ins. Their comments
  (1714-1760) are re-derived: the margin a piece is 0.537 of main's, so the
  coverage a post rises by 1.86 and payroll over the margin stays at BEA's
  41.7%.
- `ui/SectorScreen.java`:6790 prints the ceiling with `%.0f`, which would
  show 2.5 as "3x"; it takes one decimal.

**The check:**
- `LuxuryRetailCheck`: it reads the settled margin from the dials (its
  line 87), so its sections should hold. Its fixtures name the Boutique's
  coverage, so run it and `BuildingDataCheck` first.
- This study's probe rerun on its three runs (`run.sh` with no `PATCH`).
  Done means Luxury Retail's value added over GDP and its workers over the
  posts staffed are at prototype 2's (the median year 5-8% and 4-6%), and
  the spend and net worth over GDP within main's seed-to-seed spread.

**The gate:** `buildings.json` is read by most harnesses, so expect the
pick to be ALL and a dry run. Prototype 2's seed 1 and Rotterdam had spells of
"households with no home" (HN1's class; main's own runs had none). If LQ2's
playtest shows them, trace them to HN1 or to the change before the gate.

**Not beside:** QN1 (`BuildingManager`, `ui/SectorScreen.java`), and any
batch on `sectors/LuxuryRetail.java`, `buildings.json` ids 69-70 or
`LuxuryRetailCheck`.

### 9.2 LQ3

**The fortune's other spending, a study (★2 (a)), after LQ2.** One
builder, docs only.

**The questions:**
- Where should the rest of a household's spending out of wealth go, so that
  the counter's share can fall to other goods' (about 0.4 of the term)
  without growing the pile?
- What would the city's other consumption have to be to stand at BEA's
  share of GDP? It is 39-46% against 67.6%.

**The candidates:**
- services the city makes, with BEA's industries as the marks: arts,
  entertainment and recreation (71), accommodation (721), other services
  (81);
- the existing tables, through `MEAL_SHARE_OF_WEALTH`; meals already run
  1.9 times food services against GDP;
- travel abroad.

**The method:** measured as here, on LQ2's main, with prototypes behind a
switch. It ends in the batches that build the drain and then cut the
share.

**Not beside:** runs beside any.
