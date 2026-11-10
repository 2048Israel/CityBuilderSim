# World prices chained (WP1, 2026-11-09)

A study, no model change. CU1 found the old city's wealth is a pricing
artifact: the world solves each traded good alone, so rod leaves the cathode
it is rolled from, and a city that buys from the cheapest maker and sells to
the best buyer grows rich rolling imported cathode. Jerus took chain-linked
prices over the recommended re-anchor (CU1's ★1, 2026-11-09), for every good
the price file prices off another (★2), wire as 70% rod (★3), the refining
charge on a cycle (★4), and left the path anchor to this study (★5). This is
the design of that January, prototyped and measured, with eight calls
(section 8) and CU2's brief (section 9).

**In plain words, for Jerus.**

- **It works.** A made good now costs what its inputs cost in that country
  plus a conversion. Over thirteen worlds and 330 years rod stays on its
  cathode (1.00 of the literals' ratio; main: 2.4 by 2100, 2.7 by 2200). All
  60 goods still match the game's prices at the calibration to a thousandth of
  a per cent. The chain itself costs no time; the path anchor below takes a
  January from 17 ms to about 30 (the bound is 60), which CU2 can cut.
- **"The world's price" has to be the cheapest maker's, delivered.** Read as
  the country's own average or the world's average the spread stays open.
- **The price file's table mends copper and nothing else.** One parent a good
  cannot hold a works that buys two things: a Blast Furnace ends up paying
  more for its ore and coke than its iron sells for. The works' own recipes,
  with the conversion priced as manufactures are, keep every works near
  the margin its literals give it.
- **The path anchor is the larger half of the mend.** Today a city's copper
  falls to a third of its price path in three centuries, because the path
  holds the world's average and the city buys the cheapest. Holding the
  reference city's price on the path instead keeps every raw good within a
  few per cent of it at the game's stand-in site and within a tenth at
  Rotterdam (grains the one exception there, 0.84).
- **Once copper is mended no seed has a copper industry.** No smelter, no
  refinery, at most one Rod Mill (at a loss), three to five Wire & Cable
  Works. At the game's own prices a Rod Mill pays 1.03 for the cathode in a
  unit of rod it sells: the works was only ever built on the artifact. The
  city lives on fabricated steel and cars instead, half its exports each,
  with 86-98% of GDP exported. It is as large and as well off as main's until
  about 2050; after that main's three copper cities pull away to twice its
  income a head, and seed 0, which never had the mills, is where it was.
- **No yearly record of the refining charge may be shipped as a data
  source.** The yearly benchmark is published only by firms that sell it.
  What the note recommends is to ship the benchmark as what it is, a price two
  named companies agreed and announced each year (a fact, as LC1's rule has
  it), and, if that reading is not his, the game's own swing with the real
  record's range and pace and no real year in it.
- **The charge is money, not a share.** It has stayed between 12 and 30 cents
  a pound for thirty years while copper's price rose fourfold. Priced as a
  fee, one swing gives both the 13-33% of the 1990s and the 4-12% since.
- **Main has a fault the study ran into.** The solve can leave the numbers and
  never come back: on two of the twelve drawn worlds crude oil's price is not
  a number from 2165 and from 2191 on. Ten lines mend it; CU2 carries them.

## 1. How it was measured

Everything ran outside the tree, on copies in a git-ignored folder, and is
kept as text in `world-prices/probes.md`; every table is whole in
`world-prices/tables.md`.

- **The prototype** is `chain.py`, which writes a copy of `WorldMarket.java`
  with the chained January in it behind switches (`-Dwp1.chain`, `.see`,
  `.own`, `.root`, `.supply`, `.guard`, `.charge`). With none set the copy
  plays as main: the four seeds' yearly rows are byte-identical to main's.
- **The world probe** (`WP1Drift`) solves every January from 1900 to 2230 on
  the Historical path and on twelve drawn ones, as the game does (each from
  the year before), and reads every good's floor and ceiling with nothing
  traded at three sites (the game's stand-in site, Halifax, Rotterdam), every
  fifth year, beside its literals and its path. Twelve variants.
- **The city** is `LongPlaytest` itself on seeds 0-3 to month 2400, as CU1
  ran it, with CU1's `CopperProbe` (and the goods a city trades added to it):
  main, and five variants. Every city was founded in a `TempDirs` folder; no
  save was read and no slot written.
- **The January's cost** was timed with each variant run alone in turn, twice
  (other sessions' work shared the machine, so the figures are soft).
- **The charge's record** was researched from the publishers' own pages
  (`world-prices/tcrc.md`): every figure with its document, and every source
  with its terms.

Names used below: **series** is the price file's table (each follower wholly
on its series' good, wire 70% rod); **recipe** the works' table (3.3) with the
conversion on the manufactures index; **anchor** the roots held on their path
at the reference city (3.5). A **root** is a good that follows nothing.

## 2. Why the spread opens today

`WorldMarket.solve` solves one good at a time. Its makers' prices move until
each sells what it supplies, and one scale (`lambda`) holds the mean of the
makers' mill prices, weighted as in the base year, on the good's price path.
Nothing in a rod maker's price knows what cathode costs it.

Two things then pull a city's prices off the literals' ratios:

- **Each good's makers spread, and the city buys the cheapest.** A maker's
  supply grows with its own nation, so a fast-growing maker's price falls
  below the mean the path holds. By 2100 the cheapest copper landed at a city
  stands at 0.38 of copper's path, by 2200 at 0.33 (the drawn worlds' median;
  0.60 on the Historical path at 2100). Iron stands at 0.90 and 0.86, crude
  at 0.85 and 0.88, lumber at 0.90 and 0.87; coal, sawlogs, grains and meat
  stay within a few per cent.
- **Each good spreads by itself.** Rod's cheapest maker has not fallen as
  cathode's has, so rod stands 2.41 over its literal ratio to cathode by 2100
  and 2.68 by 2200 (1.61 on the Historical path), concentrate 1.49 and 1.63,
  copper scrap 2.27 and 2.57.

A uniform move of one good's makers' prices is exactly another `lambda`: the
same equilibrium at another level. The design leans on that twice (3.5, 9).

## 3. The chained January

### 3.1 The rule

The goods are solved in the chain's order, inputs first.

- **A root** is solved as today, then anchored (3.5).
- **A made good**'s price in a nation, as a ratio to the base year, is

      sum over its inputs (share x what the input costs that nation)
        + own x (its own solved price)

  where the shares and `own` add to 1 (3.3), and its own solved price is
  today's solve of the good with its mean held on the conversion's path
  (3.4). A good with no own part (concentrate, rod under the price file's
  table) is not solved at all.
- **Its makers' supply is what they sell at that price.** A made good's
  makers supply at cost, so its market clears by construction, and the
  month's step, the fills, the save and the reload read it as they read any
  solved good. Nothing new is saved.

### 3.2 What an input costs a nation

Three readings of "its inputs at the world's price" were run with the price
file's table and no anchor. A follower over its parent, 1 being the literals'
ratio, the drawn worlds' median at 2100 and 2200 at the stand-in site:

| | main | cheapest maker, landed | the nation's own index | the world's mean |
|---|---|---|---|---|
| rod over cathode | 2.41 2.68 | 1.00 1.00 | 1.00 1.00 | 2.75 3.19 |
| concentrate over cathode | 1.49 1.63 | 0.97 0.96 | 1.11 1.13 | 2.75 3.19 |
| copper scrap over cathode | 2.27 2.57 | 0.90 0.85 | 1.40 1.50 | 2.77 3.15 |
| wire over rod | 0.88 0.82 | 1.32 1.46 | 2.01 2.00 | 1.02 1.03 |
| steel over iron ore | 1.01 1.03 | 1.00 1.00 | 1.01 1.01 | 1.13 1.18 |

**The cheapest maker's mill price landed in that nation** (over the same at
base-year prices) is the only reading that closes the spread, because it is
what the city itself pays: the good as one commodity with one price and a
freight. The world's mean is main's fault moved one good up the chain. The
wire's 1.32 is not the reading's: it is the root's drift under it (cathode at
0.38 of its path while wire's own 30% holds its own), and the anchor closes
it (1.01, 1.03).

### 3.3 What a made good is made of

Two tables were run.

- **series**, Jerus's ★2 and ★3 as worded: the 27 goods the price file marks
  as priced off their series' own good, each wholly on it, and wire as 70%
  rod and 30% its own price. 28 made goods, 32 roots.
- **recipe**: each good a works makes, on that works' inputs at the shares
  `buildings.json` gives at the literals' mid-band prices, the rest of its
  price its conversion; a good the file prices off another with no works
  between them stays wholly on it. 40 made goods, 20 roots:

      concentrate, anode, copper scrap = cathode        rod = .98 cathode
      wire = .70 rod                                    pellets = .70 ore + .01 coal
      coking coal = thermal coal                        met coke = .74 coking coal
      pig iron = .36 ore + .33 met coke                 scrap = pig iron
      steel = .66 pig iron + .16 scrap                  fabricated steel = .38 steel
      machinery = .08 steel                             cars = .15 fabricated steel + .11 machinery
      vans = .11 fabricated steel + .09 machinery       trucks = .10 machinery + .07 fabricated steel
      rolling stock = .09 machinery + .08 fabricated steel
      cement = .34 limestone + .12 coal + .04 aggregate lime = .21 limestone + .07 coal
      the nine refined oils = .85 crude                 pulpwood, residues = sawlogs
      lumber = .52 sawlogs                              panels = .31 pulpwood
      pulp = .21 pulpwood + .08 residues                paper = .74 pulp + .07 fuel oil
      wood pellets = .32 residues                       processed meat, ready meals = .40 meat
      bread, bakery = .24 crops                         snacks = .19 crops

What a works pays for its feed over what it sells (feed at the ceiling, sales
at the floor), the drawn worlds' median at 2100 and 2200:

| works | literals | main | series | recipe | series + anchor | recipe + anchor |
|---|---|---|---|---|---|---|
| Rod Mill | 1.03 | 0.46 0.43 | 1.10 1.13 | 1.07 1.09 | 1.03 1.03 | 1.03 1.03 |
| Copper Smelter | 0.96 | 1.63 1.60 | 1.02 1.05 | 1.02 1.05 | 0.96 0.96 | 0.96 0.96 |
| Scrap Copper Refinery | 0.96 | 2.27 2.48 | 1.10 1.17 | 1.10 1.17 | 0.96 0.97 | 0.96 0.97 |
| Wire & Cable Works | 0.83 | 0.95 1.04 | 0.67 0.63 | 0.67 0.64 | 0.82 0.81 | 0.82 0.81 |
| Blast Furnace | 0.86 | 0.94 1.10 | 1.15 1.33 | 0.88 0.88 | 1.05 1.27 | 0.89 0.88 |
| Oxygen Steel Shop | 1.09 | 1.08 1.08 | 0.97 0.97 | 1.09 1.10 | 0.94 0.95 | 1.08 1.09 |
| Coke Ovens | 0.95 | 0.82 0.79 | 0.84 0.84 | 0.98 0.97 | 0.84 0.83 | 0.98 0.97 |
| Fabrication Works | 0.44 | 0.42 0.37 | 0.44 0.35 | 0.41 0.43 | 0.41 0.35 | 0.40 0.42 |
| Sawmill | 0.81 | 0.94 0.99 | 0.95 1.01 | 0.86 0.92 | 0.82 0.88 | 0.86 0.92 |
| Panel Mill | 0.64 | 0.80 0.89 | 0.81 0.86 | 0.67 0.72 | 0.64 0.71 | 0.66 0.72 |

The price file's table mends copper, which has one input. It cannot hold a
works with two: pig iron wholly on ore leaves the Blast Furnace buying its ore
and coke at 1.15 of its sales by 2100 and 1.33 by 2200, worse than main, and
the anchor does not help it (1.05, 1.27). The recipe table holds every works
near its literal for three centuries: most within a few points, the widest
the Sawmill's 0.92 for 0.81 and the Pulp Mill's 0.62 for 0.53 at 2200. The
nine works not shown are in `tables.md`; none contradicts this.

### 3.4 The conversion's price

Under the recipe table the rest of a made good's price is its conversion:
labour, energy and plant. Two paths for it were run: the good's own series in
the price file, and the manufactures index (machinery's series, the World
Bank's MUV). On the good's own series a works' margin still drifts where the
series is its main input's again (Coke Ovens 0.84 and 0.87 against 0.95;
Bakery 0.31 and 0.40 against 0.38); on the manufactures index it holds (0.98
and 0.97; 0.35 and 0.36). **The conversion follows the manufactures index.**

### 3.5 The roots' anchor (CU1's ★5)

Chaining ties a made good to its inputs; it does nothing for the raw good the
chain hangs from. Three anchors were run. A root's band at a city over its
path, the drawn worlds' median at 1900, 2000, 2100 and 2200, stand-in site:

| | as now (the mean on the path) | the reference city's band on the path | supply grown with the world's use |
|---|---|---|---|
| copper | 1.00 0.91 0.38 0.33 | 1.00 1.01 1.01 1.01 | 0.62 0.35 0.13 0.10 |
| iron ore | 1.04 0.74 0.90 0.86 | 1.10 0.96 1.02 1.02 | 0.86 0.51 0.28 0.20 |
| crude | 0.99 0.93 0.85 0.88 | 1.00 1.00 1.02 1.03 | 0.69 0.47 0.21 0.18 |
| thermal coal | 0.98 1.03 1.00 1.02 | 1.02 0.99 1.01 1.00 | 0.82 0.62 0.28 0.18 |
| lumber | 1.00 1.03 0.90 0.87 | 1.00 1.03 1.04 1.03 | 0.70 0.73 0.44 0.38 |

**The anchor:** after a root's solve its makers' prices are moved as one (and
its supply scale with them, so it is the same equilibrium) until the
reference city's band with nothing traded, over the band the calibration left
it, equals the path's level over the level the literals stand on. The
reference city is the calibration's (Halifax, no berth), so the world stays
one world whatever site a city is founded on; Rotterdam reads the same within
a few per cent (grains 0.87 and 0.84 the widest). The band does not move one
for one with the prices (the freight does not move), so the move is found by
a bracketed root-find; the first prototype's fixed-point step ran away on one
good and is the reason the brief says so.

Two limits, measured:

- **The makers still spread.** The anchor moves where the city stands in the
  spread, not the spread. Copper's mean mill price then stands 1.8 over its
  path by 2100 on the Historical path and 3.0 on drawn world 1 (iron 1.07 and
  1.12, crude 1.01 and 1.32). No city sees the mean; `lnTermsOfTrade` does
  (section 9, the terms of trade).
- **A price below its freight.** On a path low so deep that the reference
  city's floor is not positive (thermal coal on drawn world 1 at 2200, its
  path at 0.17 of the literals' level; 6 of 52,260 good-years, all coal), the
  prototype anchors by the ceiling alone and pushes the mill prices to 0.04
  of the path. CU2 leaves such a year as the solve had it.

With the recipe table and the anchor every works' margin above stays on its
literal, and the goods' bands stand closer together than in main at every
date (the spread across goods of the floor over its literal: 0.31 against
0.42 at 2100, 0.30 against 0.49 at 2200).

### 3.6 The refining charge (★4)

Concentrate carries the treatment and the refining charge, anode the refining
charge (36% of the two together at 2024's benchmark). Section 7 has the
record and what may ship. In the prototype the charge is **money off the
copper in the good**, not a share of it: on the Historical path's record
years (2000-2026) the record's share of that year's copper price; off the
record a swing, a pure function of the path's seed and the year (nothing
saved), priced as every conversion is (on the manufactures index): its
middle 5.5% of the reference year's copper price, between about 3% and 8%,
large peaks nine years apart, with a slower turn beside it so no two swings
are the same height. The Historical path's peaks stand on the record's (1997, 2006,
2015, 2024). The calibration reads the charge the literals were set on
(2024's benchmark, 5.4%), not the reference year's own 1.3% (call ★6).

### 3.7 The solve's guard

`solve` has no guard against an iterate that is not a number. The accelerator
(`Accel`) can throw one, and every pass after it is not a number either: the
solve runs to its 2,000 sweeps, says so in the log, and the next January
starts from the same prices. In main, on drawn worlds 9 and 10, crude's solve
leaves the numbers in 2165 and 2191 and never returns (66 and 40 Januaries of
330, "clearing error NaN"). The guard: a pass whose error is not finite puts
back the plain step from the last pass that was, and resets the accelerator.
With it no variant on any world stops once. It is main's fault, found here;
CU2 rewrites this method and carries the guard (decided, not a call).

### 3.8 The calibration

Halifax, 2025, Historical, as today, in two passes: a plain one first, which
fixes the freight scale of the goods that do not calibrate (a made good reads
its input's freight scale, and a class median is only known at the end), then
the chained one in the chain's order. Under every variant all 60 goods stand
within 0.1% of their literals (the worst 0.0017%), and the fits are main's:
55 calibrated, 5 one-sided, none on its class's median. With the charge's
cycle concentrate stands 4.5% over its literal there and anode 1.6%, by
design (★6).

## 4. What it does to the world's prices

Section 3's tables are the measurement; the rest is in `tables.md`. In short,
with the recipe table, the manufactures index and the anchor, against main,
at the stand-in site, the drawn worlds' median at 2100 and 2200:

| | main | chained |
|---|---|---|
| rod over cathode | 2.41 2.68 | 1.00 1.00 |
| concentrate over cathode | 1.49 1.63 | 1.00 1.00 |
| copper scrap over cathode | 2.27 2.57 | 1.00 0.99 |
| wire over rod | 0.88 0.82 | 1.01 1.03 |
| copper over its path | 0.38 0.33 | 1.01 1.01 |
| fuel oil over crude | 0.86 0.77 | 0.94 0.99 |
| naphtha over crude | 0.81 0.72 | 0.94 0.98 |
| a works' feed over sales, the widest miss of its literal (Rod Mill; Sawmill) | 0.46 for 1.03 | 0.92 for 0.81 |
| Januaries that stop at 2,000 sweeps, of 4,290 | 106 | 0 |

A good with two inputs is no longer near 1 against one of them, and should
not be: pig iron over ore runs from 0.62 to 2.14 at 2200 (the drawn worlds'
10th to 90th) because coke is a third of it. The works' table (3.3) is its
measure.

## 5. The January's cost

Each variant alone in turn, twice; seven paths; milliseconds:

| | calibration | a January after the first: median | 95th | sweeps a path |
|---|---|---|---|---|
| main | 645, 684 | 17.0, 17.6 | 23, 24 | 215,954 |
| series | 1,097, 887 | 12.0, 12.9 | 15, 18 | 109,832 |
| recipe | 869, 878 | 16.7, 23.7 | 24, 33 | 189,971 |
| series + anchor | 847, 1,212 | 29.2, 35.5 | 38, 52 | 90,766 |
| recipe + anchor | 895, 1,075 | 28.0, 35.4 | 40, 55 | 175,083 |

- **The chain is free.** A made good's own solve starts from last year's
  chained price moved onto the year's level and takes fewer sweeps; a good
  with no own part is not solved.
- **The anchor costs 11 to 23 ms a January** in the prototype, over the same
  table without it: its root-find reads `referenceBand`, which is makers
  times users, about fifteen times a root. Started from last year's move it
  needs three or four reads; CU2 does that and measures again. The bound
  (`SOLVE_WARM_MS`, 60) holds at the median and at the 95th as it is.
- **The calibration** takes 0.85 to 1.2 s for main's 0.65-0.68 (`BASE_MS`,
  2,000). The second pass need not solve at all (section 9).
- The founding January is 65-108 ms for main's 64-78 (`SOLVE_COLD_MS`, 600).

## 6. The city once the prices are chained

Seeds 0-3 at month 2400 (2100). "Real" is CU1's column, GDP a head over the
price index.

| | people (thousands), seeds 0-3 | real GDP a head | copper's share of value added | exports over GDP | imports over GDP |
|---|---|---|---|---|---|
| main | 562, 428, 613, 521 | 1.5, 4.0, 3.4, 4.6 | 0, 63, 55, 60% | 109, 159, 156, 169% | 53, 85, 86, 99% |
| series | 685, 466, 656, 516 | 1.7, 1.7, 2.1, 1.6 | 0% | 86, 93, 89, 85% | 34, 33, 36, 34% |
| recipe | 720, 533, 585, 544 | 1.8, 1.9, 2.2, 2.0 | 0% | 99, 93, 93, 89% | 35, 34, 38, 34% |
| series + anchor | 430, 331, 508, 657 | 1.3, 1.7, 1.9, 1.8 | 0% | 86, 95, 98, 95% | 30, 31, 34, 32% |
| recipe + anchor | 307, 566, 555, 707 | 1.5, 2.2, 1.9, 1.6 | 0% | 86, 92, 98, 92% | 38, 37, 40, 35% |

- **Copper is gone, on every seed.** Main's seeds 1-3 have 363, 397 and 446
  Rod Mills and 53-81 Wire & Cable Works; seed 0 has no mill and 61 Wire &
  Cable Works on imported rod (a fifth of its exports). Chained, no seed has a
  smelter or a refinery; a Rod Mill is built once on seed 1 and loses (feed
  99% of its sales, net -32%); three to five Wire & Cable Works stand, 1-3% of
  exports. This is DS1's dead sector, and it is the prices: at the literals a
  Rod Mill's cathode at the import price is 1.03 of its rod at the export
  price, and wire's export price is 1.21 of rod's import price. No seed has a
  copper mine, so nothing here tests the chain a mine feeds.
- **What carries the city is fabricated steel and cars**: 39-56% and 29-57%
  of exports, made from imported steel (40-45% of imports) and machinery
  (9-20%), with luxuries the next import (23-31%). Manufacturing is 40-44% of
  value added and Automotive 15-29%. Their margins are the literals' own, not
  a drift: a Fabrication Works' steel is 0.44 of its sales in the literals and
  0.40-0.42 chained, a car's fabricated steel and machinery 26% of its price.
- **The city is not poorer until main's mills come.** At month 1800 the
  chained city has 145-304 thousand people at 1.8-2.5 real for main's 169-298
  thousand at 1.3-2.4. Main's copper is 4-14% of value added then and 55-63%
  at month 2400, where its three copper seeds reach 3.4-4.6 and the chained
  city 1.6-2.2. Seed 0 is 1.5 either way.
- **The charge's cycle reaches no city here.** Nobody trades concentrate or
  anode: seeds 0, 1 and 3 play byte for byte with the cycle as without it,
  and seed 2 differs by one late works. Only a mine or a smelter would see it
  (the fixture CU2's row already asks for).
- Seeds differ run to run by more than the tables differ (seed 0: 685, 720,
  430 and 307 thousand under the four chained variants); four seeds rank
  nothing. The ensemble is CU2's. Three of the twenty-four runs ended on a
  layout finding (a district's boxes moved on main's seed 2 and on the price
  file's table's seed 3; industry farther out than the homes on seed 2 under
  the cycle); none is a price's, and every other run passed every month's
  audit and every reload.

## 7. The refining charge: the record, and what may ship

**There is no continuous yearly record under terms that allow a sold game to
use it.** `world-prices/tcrc.md` has every year from 1989 to 2026 with its
document and page, and each source's terms as read. In short:

| source | what it has | its terms | may ship |
|---|---|---|---|
| World Bank, the Pink Sheet (copper's price) | every year | CC BY 4.0 (already in `licences.md`) | yes |
| USGS Minerals Yearbook, copper chapters | a contract average for 1994 and 1998-2004; part-year levels 2009-2011; the treatment charge alone 2013-2014; spot levels otherwise; nothing 2016-2022 | US public domain, but the figures are credited to CRU, and by LC1's rule a third party's figure carries its own terms | unclear: no |
| Bureau of Mines Minerals Yearbook 1989 | one spot figure | US public domain | yes (spot only) |
| Canadian Minerals Yearbook (NRCan) | the benchmark 1994-2004 | non-commercial without permission | no |
| Cochilco's concentrate reports | 2021-2022 | reproduction with citation; silent on commercial use; the figures are Wood Mackenzie's and SMM's | unclear: no |
| Teck's investor decks (source line: CRU) | the benchmark 2006-2020 in one table | company copyright | no |
| CRU, Fastmarkets, SMM, Reuters | every year's benchmark as announced | publisher copyright | no |

**What the record is, as evidence** (the benchmark as announced, both charges
in cents a pound of payable copper at a 26.5% concentrate, over the Pink
Sheet's price):

| years | the two charges, c/lb | copper, US$/t | charge over price |
|---|---|---|---|
| 1994-2004 | 12.0-29.1 | 1,559-2,936 | 13-33%, median 24% |
| 2006-2024 | 12.5-29.7 | 4,868-9,317 | 3.8-12.2%, median 6.1% |
| 2025 | 5.9 | 9,947 | 1.3% |
| 2026 | 0 | 13,539 (Jan-Oct) | 0 |

- **It is a fee in money.** The two charges stayed between 12 and 30 cents a
  pound from 1994 to 2024, almost without trend, while copper's price rose
  about fourfold; the share fell because the price rose. Over the manufactures
  index, as a share of 2025's copper price, the record runs from 3.2% to 7.8%
  (2000-2024).
- **Its pace:** large peaks in 1997, 2006 and 2015, eight to nine years
  apart, a fourth in 2023-2024; a fall from peak to trough takes two to six
  years, a rise one to five.
- **2025 and 2026 are outside every earlier year** (US$21.25 a tonne and 2.125
  cents, then nothing, between one miner and one smelter; Japanese smelters
  settled higher). Whether that is a trough or a new regime no source settles.
- **The game's own copper record starts in 2000** (the price file's first
  year; before it the path is flat at 2000's level), so only 2000-2026 could
  be a record in the game. 2000 and 2005 have no benchmark anywhere found.

**In the prototype** (the money form, 3.6), concentrate over cathode against
the literals' ratio: 0.79 on the Historical path in the 1990s, 0.82 at 2000,
1.02 at 2010, 0.93 at 2015, 1.04 at 2025; on the drawn worlds 0.93 to 1.02
(10th to 90th), 0.74 the lowest. So the one swing in money gives the 1990s'
high share from the path's own cheap copper, as the record has it. A smelter
on imported concentrate then pays 0.93 to 0.98 of its sales for its feed (5th
to 95th of every path and fifth year; the literals' 0.96; 0.85 the lowest):
its margin over its feed moves between 2% and 7%, which is the cycle a city
with a smelter would play through.

## 8. The calls

Each with what the study recommends. The orchestrator may take the
recommended way (BP1); ★4 is the one that rests on a reading of a rule.

- **★1 What a made good is made of.** (a) The price file's table as ★2 and ★3
  word it: 27 followers wholly on their series' good, wire 70% rod. (b) The
  works' recipes (3.3), the conversion on the manufactures index: 40 goods,
  wire still 70% rod. Measured: (a) mends copper and leaves a Blast Furnace
  paying 1.05-1.27 of its sales for its feed (literal 0.86), an Oxygen Steel
  Shop 0.94 (1.09), Coke Ovens 0.84 (0.95); (b) holds each within a few
  points. **Recommended: (b).** It widens ★2's "every good the price file
  prices off another" to every good a works makes.
- **★2 What "the world's price" of an input is to a nation.** (a) The
  cheapest maker's price landed there; (b) the nation's own price index of the
  good; (c) the world's mean. Rod over cathode by 2100: 1.00, 1.00, 2.75;
  scrap over cathode 0.90, 1.40, 2.77. **Recommended: (a).** It is what the
  city itself pays, and the only one that closes every pair.
- **★3 The path anchor (CU1's ★5).** (a) As now, the makers' mean on the
  path: a city's copper at 0.38 of its path by 2100, iron 0.90, crude 0.85.
  (b) The reference city's band on the path: every root within 0.96-1.10 at
  the stand-in site, 0.84-1.09 at Rotterdam. (c) Supply grown with the
  world's use: 0.10-0.47 by 2200, worse.
  **Recommended: (b), in CU2.** Without it the chain holds rod on a cathode
  that is itself a third of its price. Its cost: the makers' mean leaves the
  path (copper's 1.8 to 3 times it), and 11-23 ms a January until its
  root-find is started from last year's.
- **★4 The charge's record: what ships.** No source that publishes the yearly
  benchmark allows a sold product to use it (section 7). (a) Ship the
  benchmark as facts: each year's settlement is a price two named companies
  agreed and announced, which LC1's rule calls nobody's ("a gold par fixed in
  law"); 2001-2004 and 2006-2026 from the announcements, 2000 and 2005 the
  game's own estimate and marked so; no report's text or table copied, the
  share computed on the Pink Sheet's price; a row in `licences.md` saying so.
  (b) Ship no real year: the game's own swing on every path, its range and
  pace from the evidence, its phase set so the Historical path peaks in 1997,
  2006, 2015 and 2024 and troughs after 2025. (c) Ask CRU or Fastmarkets for
  terms. (The research note's own fallback, the years the USGS chapters
  print, is set aside: those are CRU's averages, a compiler's estimates by
  the same rule, and in the game's window they are 2000-2004 alone.)
  **Recommended: (a) if Jerus reads the benchmark as a fact; (b) if
  not.** The code is the same but for a table of 27 numbers, and (b) loses
  only the real years' heights. The orchestrator should not settle this one.
- **★5 The charge's form.** (a) A share of copper's price, swinging 4-12%
  as since 2006. (b) Money, a smelter's fee on the manufactures index,
  swinging between about 3% and 8% of the reference year's copper price: the
  share then follows from the path, a tenth to three tenths while copper is
  as cheap as the Historical path's before 2000 (the record: 13-33%), 3-8%
  at 2025's price (the record 2006-2024: 4-12%). **Recommended: (b).** It is what the
  record shows, one swing gives both eras, and a smelter's margin stops
  rising and falling with the price of a metal it does not own.
- **★6 Which year's charge the literals stand for.** The calibration is 2025,
  whose benchmark is the lowest on record (1.3%); the literals were set on
  2024's (5.4%). (a) They stand for 2024's: the calibration reads 5.4%, and in
  2025 itself on the Historical path concentrate stands 4.5% over its literal
  and anode 1.6%, the check asserting the literal times the year's charge.
  (b) They stand for 2025's: nothing moves at the calibration and in every
  other year concentrate is about four points cheaper against cathode than
  the literals say. **Recommended: (a).**
- **★7 Copper Refining with no works that pays.** Chained, no seed builds a
  smelter, a refinery or a Rod Mill that lives (section 6). (a) Accept it: the
  literals say a Rod Mill does not pay on imported cathode, which is true of
  one (CU1, section 5), and the sector is for a city with a mine; CU2's
  fixture proves the chain pays there, and CU2 finds why the planner still
  builds one mill at a loss (DS1, folded into CU2). (b) Widen the works'
  literal margins until they pay on imports. **Recommended: (a).**
- **★8 What carries the city next.** The chained city exports 86-98% of GDP
  as fabricated steel and cars, whose literals give a Fabrication Works its
  steel at 0.44 of its sales and a car its metal and machinery at 26% of its
  price. A real fabricator's materials are roughly half its sales and a car
  plant's well over half (from memory, not sourced here). (a) A study of
  those two margins against the Economic Census, as CU1 did for copper, after
  CU2. (b) Leave them. **Recommended: (a)**, queued when CU2 has shipped and
  its ensemble shows the same two sectors; LX1 already waits for CU2.

## 9. CU2's brief

**CU2: world prices chained.** The January of section 3, with the recipe
table, the manufactures index, the anchor, the guard and the charge, as the
calls above are taken. For the session that reads the source; the prototype
(`world-prices/probes.md`, `chain.py`) is a working copy of every part, with
its switches where a call is open.

- **The table.** A chain of dials beside `WorldMarket` (a small class of its
  own): for each made `Good` its inputs and shares, the rest its conversion;
  the order inputs-first. Section 3.3's forty rows. A check recomputes each
  share from `buildings.json` at the literals' mid-band prices and fails,
  naming the row, when a recipe has moved more than a few points from its
  dial.
- **`solveYear`.** The chain's order. A root: its start moved onto the year's
  level (below), `solve` as today, the anchor. A made good: its start moved
  onto the level, `solve` with the level of the manufactures index's series
  (machinery's) where it has an own part, then the rule of 3.1, an input's
  cost being the cheapest maker's mill price landed in the nation
  (`phi` times the freight, as `referenceBand` reads a landed price) over the
  same at base prices. A price is floored above zero.
- **The start.** Last year's saved prices leave a root off its mean (the
  anchor) and a made good on its chained price. Moving the saved prices as
  one onto the year's level before the solve (a root's `lambda` with them, by
  `k^-(ES+ED)`; a made good's stays, it is its own part's) is what makes the
  chain free (section 5). Nothing is kept beside the saved prices and scales.
- **The anchor** (if ★3 is taken). After a root's solve, the one move `k` of
  its makers' prices, `lambda` by `k^-(ES+ED)`, that puts the reference
  city's band over the calibration's (`refFloor`, `refCeiling`) on the path's
  level over the Historical path's 2025 level. Read a two-sided good by the
  geometric middle of its sides and a one-sided good by its calibrated side.
  Bracket the root and keep inside the bracket (the band is not linear in
  `k`); start from last year's move. Where the reference floor is not
  positive leave the year as the solve had it (3.5).
- **`deriveSolution`.** A made good's supply is its makers' sales at the
  chained price, in the units `supply` is kept in; `restore` derives the same,
  so a reload matches without a new saved field.
- **`solve`: the guard** (3.7). A pass whose error is not finite puts back
  the plain step from the last finite pass (or the start), resets `Accel`,
  and counts as a sweep. A check plays drawn world 9 to 2170 and asserts no
  good stops; today crude does.
- **`Base.calibrate`.** Two passes (3.8): the plain pass gives every good
  that does not calibrate its freight scale, the chained pass fits `sigma`
  and `phi` as today. The second pass need not solve: a root's solve is the
  plain pass's, and a made good's own solve at another level is the plain
  pass's prices times the two levels' ratio (section 2). Re-measure `BASE_MS`.
- **`resolve`** (the check's re-solve of the month's step) needs a made
  good's supply per maker, not one `lambda`: a scale a maker.
- **The terms of trade.** `lnTermsOfTrade` reads the anchored prices. On the
  Historical path at 2100 DR Congo's terms of trade stand at +0.08 for main's
  -0.44 and Zambia's at +0.29 for -0.41 (in main their copper is the cheap
  maker's, a third of the path), Chile's at +0.29 for +0.03; Germany, the
  United States, Japan and China move by 0.05 or less. WN4's rates read
  them: run its checks, and say in the notes what moved.
- **The charge** (as ★4-★6 are taken). A pure function of the path's seed and
  the year beside the price paths in `Nations`, with a mark for a record year
  and an estimate; the record's table if (a); concentrate both charges, anode
  the refining charge (36% of the two). In the money form it is taken off the
  copper's cost before the floor, and the calibration reads the literals'
  charge. Its dials: the middle, the half-height, the years between large
  peaks, the literals' charge, the refining part. A `licences.md` row and the
  attribution file if a record ships.
- **Saves.** No new field. The saved prices and scales mean something else
  (a root's carry the anchor, a made good's are chained), and an old save's
  would be read wrongly: bump `SAVE_FORMAT` and raise
  `OLDEST_LOADABLE_FORMAT` to it, and say so in the notes (the standing rule).
- **`WorldMarketCheck`.** "The January solve clears every maker and meets its
  price path" is restated: a root's reference band meets its path, a made
  good is its inputs' cost plus its own part, every maker clears. "The
  reference city sees the game's literals" stands (concentrate and anode
  times the year's charge under ★6a). "The month's step against a re-solve"
  uses the per-maker scale. New: rod and wire against cathode plus their
  conversion, and every works' feed over sales within a stated distance of
  its literal, each fifth January on the Historical path and several drawn
  ones, against the chain's own dials; the guard's check; the timings
  re-measured (`SOLVE_WARM_MS`, `SOLVE_COLD_MS`, `BASE_MS`).
- **The played checks** its row already names: a count of Rod Mills on seed 1
  (363 by month 2400 today; none that lives, chained), and a fixture city with
  a copper mine and a smelter, where the chain the literals mean to pay does
  pay and the charge's cycle is seen. `CopperRefiningCheck` (FL1's section 9),
  the reload in `ReadPathCheck` and `SaveFileCheck`. DS1, folded into CU2: the
  planner builds a Rod Mill that loses from its first month (seed 1; seed 2
  under the cycle).
- **Its weight.** The trace moves on every seed, so the ensemble is due;
  money moves, so a stranger's review at the gate. The default playtest is
  seed 0, which loses its 61 Wire & Cable Works: look at what `LongPlaytest`
  no longer exercises. Three slices: the chain, the anchor, the guard and the
  calibration with `WorldMarketCheck`; the charge and its check; the played
  checks and the fixture.

**CU2's row** in `QUEUE.md` stands; its brief is this section.
