# Copper Refining's margin (CU1, 2026-11-09)

A study, no model change. LS1 found Copper Refining buying its input at 52% of
its sales and keeping 35% as net income, 72% of seed 0's value added at m3996,
and asked for the cathode's price over its concentrate's on the seeds that
build refineries. No seed builds a smelter, and the refinery or two that
stands for a few years on imported anode loses. The sector is Rod Mills,
hundreds of them, rolling imported cathode into rod for export, and the margin
is the world's price of rod leaving the cathode's: 2.6 times the literals'
ratio by 2100 on the default path. A real smelter, refinery or rod mill buys
67-86% of its sales as materials and keeps a few per cent, and the game's
literals already say so. The study ends in one batch (CU2), a study (WP1) and
a look (DS1), with seven calls for Jerus (section 7).

## 1. How it was measured

- **The city runs:** `LongPlaytest` itself, with one line added to a copy
  after `g.simulateMonths(1)` calling `CopperProbe.month(g)`; the tree is not
  touched and every city lives in a `TempDirs` folder. LS1's own city first
  (0.9.60 at 092130f4, seed 0, 4,000 months), because today's seed 0 no longer
  builds the mills (RN1 moved the trace); then main at 0.9.69 (cce06c9c;
  `WorldMarket.java` is the same file on 0.9.70): seeds 0 to 3, the Earth city
  Rotterdam, and seed 0 on the Historical path (`-Dplaytest.world=HISTORICAL`).
- **The probe** sums a year of the sector's `SectorBooks` months, of each
  copper good's prices and tonnes, and of what the planner weighs
  (`ProcessingSector.appraise`), and each January reads every traded good's
  band twice: with nothing traded (`WorldMarket.band(good, 0, 0)`, the world's
  own price at the city's site) and as set, with the city's own tonnes on it.
- **Parity** is how the tables measure a price: a good's band over its literal
  band (`Good`'s world prices, the 2025 calibration), over the cathode's same
  ratio. 1 is the literals' own ratio; rod at 2 is rod twice as dear against
  cathode as the literals have it.
- **The world alone** (`WorldDrift`): no city; every traded good's band with
  nothing traded at three sites (the stand-in, Halifax, Rotterdam), each fifth
  January 1900-2230, on the Historical path and on twelve Random paths.
  `Sellers` prints who sets a band: every maker's mill price, the cheapest
  landed sellers and the best buyers.
- **The prototype** (`anchor.py`): a copy of `WorldMarket.java` whose curves
  take a re-anchored price scale at `refill()`, five ways, played to m2,400
  on seeds 1 and 2 (the recommended one on seeds 0 and 3 too).
- **The real figures:** USGS's Minerals Yearbooks and Data Series 140, the
  Census Bureau's Economic Census and its 1899 bulletin, BEA, and the Office
  of Technology Assessment's 1988 copper report are US public domain; Teck's,
  Boliden's, Aurubis's and Encore Wire's reports and the trade press are
  evidence only (`licences.md`'s rule: a study may cite any source, the game
  takes only what allows commercial use). No figure of either kind reaches the
  game in this study or in CU2, so `licences.md` gets no row; a batch that
  takes one adds its row first. Three figures were read a second time in the
  files themselves (the 2002 charge, the 2022 census line, the 1899 products);
  the rest are as the search read them.

The sources are in [copper-margin/probes.md](copper-margin/probes.md) as text
(CU1's merge is documents only), what they printed in
[copper-margin/tables.md](copper-margin/tables.md). Month 1,200 is the year
2000, 2,400 the year 2100.

## 2. What the refinery is

Rod Mills. No run, LS1's or today's, has a mine, a smelter or a scrap refinery
standing at any yearly reading. An Electrolytic Refinery or two stands for a
few years on imported anode while anode is 0.8 of parity (LS1's city: one in
m888-948, two by m1320; seed 3 and Rotterdam one each) and loses: feed 92.6%
of sales and net -12.8% in the year to m1320. The rest is this:

| year to | Rod Mills | Wire & Cable Works | feed / sales | payroll / sales | net / sales | workers | sales / GDP | value added / GDP |
|---|---|---|---|---|---|---|---|---|
| m1440 | 2 | 4 | 95.5% | 1.1% | -21.8% | 155 | 36.1% | 1.6% |
| m1800 | 2 | 15 | 61.3% | 0.8% | 2.6% | 122 | 7.3% | 2.8% |
| m1920 | 90 | 15 | 61.3% | 0.8% | 32.5% | 6,598 | 139.8% | 54.0% |
| m2400 | 276 | 34 | 57.3% | 1.2% | 36.0% | 20,811 | 143.5% | 61.3% |
| m2880 | 730 | 52 | 46.2% | 2.3% | 41.1% | 55,338 | 122.1% | 65.7% |
| m3996 | 1,679 | 313 | 46.9% | 1.8% | 34.7% | 128,267 | 136.3% | 72.4% |

LS1's 52% and 35% are these books across its read months (0.47-0.57). In the
year to m2400 a city of 280,754 people imported 18.2 Mt of cathode, rolled
18.1 Mt of rod and exported 17.5 Mt (0.6 Mt went to its own wire works, which
exported the wire); in the year to m3996, 50.5 Mt in and 47.9 Mt out. The
whole United States had 13 wire-rod mills in 2002 (USGS). The planner is right
to build them: one more Rod Mill earns 0.28 of its cost a month at m2400 and
0.19 at m3996, while a smelter and a scrap refinery lose at every reading and
an Electrolytic Refinery earns 1-2% of its cost a month at best, short of 1.25
times its interest. The city is not short of copper, it is trading a spread.

## 3. Where the margin comes from

**The world's rod leaves its cathode.** LS1's city, the default path, the band
with nothing traded (its floor; the ceilings read the same within 0.2):

| month (year) | cathode's floor over its literal | concentrate | anode | scrap | rod | rod, as the city's exports set it | wire and cable |
|---|---|---|---|---|---|---|---|
| m1200 (2000) | 0.76 | 1.09 | 0.77 | 1.01 | 1.01 | 1.01 | 1.18 |
| m1440 (2020) | 0.53 | 1.18 | 0.80 | 1.11 | 1.09 | 1.09 | 1.40 |
| m1800 (2050) | 0.53 | 1.38 | 0.93 | 1.69 | 1.66 | 1.66 | 1.43 |
| m1920 (2060) | 0.33 | 1.46 | 1.01 | 2.01 | 1.96 | 1.85 | 2.22 |
| m2400 (2100) | 0.33 | 1.46 | 0.86 | 2.58 | 2.58 | 1.93 | 2.13 |
| m2880 (2140) | 0.21 | 1.51 | 0.92 | 3.05 | 2.86 | 2.42 | 3.16 |
| m3996 (2233) | 0.28 | 1.45 | 0.88 | 2.82 | 2.66 | 2.33 | 2.77 |

The books are this table and nothing else. A Rod Mill turns 8,016 t of cathode
into 8,000 t of rod; at the literals (cathode in at 10.50 thousand a tonne,
rod out at 10.25) its feed is 1.03 of its sales and it loses, as the two
standing at m1440 do; the hundreds come after 2050. In the year to m2400 the
city paid 1.09 of parity for cathode and was paid 1.93 for rod: 1.03 x 1.09 /
1.93 = 0.58, the books' 57.3%. At m3996, 1.03 x 1.09 / 2.33 = 0.48, the books'
46.9%. The city's own 17.5 Mt takes a quarter off rod's price at m2400 (2.58
to 1.93) and its 47.9 Mt an eighth at m3996: the world takes all of it.

**It is the cathode that falls, not the rod that rises.** The path's own
copper level stays between 0.82 and 1.31 of 2025's through these years (1.26
in 2100), yet the cathode's floor is 0.33 of its literal by 2100. Across all 56 traded goods at m2400 the median floor
over literal is 0.98; cathode stands at 0.33 of that median, anode at 0.29,
concentrate at 0.49, rod at 0.84.

**Why: the world's mean is held to the path, and the city buys from the
cheapest maker.** `WorldMarket` (WN3) solves each traded good alone each
January, 196 nations, each nation's output its own variety; one scale holds
the good's mean mill price to its real price path, and each maker's price is
its own (a nation that outgrows the world sells cheaper). The city treats the
good as one commodity: it buys from the cheapest landed sellers first and
sells where the best buyers net most. Prices over the literal floor, the
default path, the stand-in site:

| year | cathode: cheapest maker | median maker | the site's floor | rod: cheapest landed seller | median maker | the site's floor |
|---|---|---|---|---|---|---|
| 2025 | DR Congo 0.47 | 0.75 | 0.51 | Canada 0.56 | 0.61 | 0.56 |
| 2060 | DR Congo 0.38 | 1.76 | 0.43 | Canada 0.88 | 0.95 | 0.88 |
| 2100 | DR Congo 0.25 | 1.70 | 0.30 | Canada 0.75 | 0.82 | 0.75 |
| 2200 | DR Congo 0.17 | 1.42 | 0.22 | Canada 0.58 | 0.67 | 0.58 |

Cathode's makers fan out (0.38 to 1.86 in 2060) around one that grows very
fast: DR Congo's room on the city's curve is 13 times 2025's by 2100 and 80
times by 2200, its price a seventh of the median maker's. Rod's makers stay
within a factor of two and a half and the cheapest is next door. So the city's
cathode is DR Congo's and its rod is North America's (the best buyer of both
is the United States), and it carries one to the other. The nations themselves
never do: a nation's supply of rod does not read the cathode's price, because
each good is solved alone.

**This is the third time.** WN3 gave the goods on one Pink Sheet series one
shock (`Nations.shockOf`) because "a refinery bought anode dearer than its
cathode (the playtest's GDP went below zero, months 422-637)"; FL1 stopped a
works running at a loss when SlopeCheck's Rod Mill took cathode at 347 and
sold rod at 314 (GDP negative m834-m1837). Both mended the losing side of the
same fault. The winning side was left: the path ties a chain's goods
together, the solve at the city's site does not.

## 4. How wide it is

**Every seed whose sector is alive, and the Historical path too.** Main at
0.9.69, the year to m2400:

| run | Rod Mills | feed / sales | net / sales | workers | sales / GDP | value added / GDP | cathode in | one more mill, a month |
|---|---|---|---|---|---|---|---|---|
| seed 1 | 363 (13 at m1800) | 54.6% | 37.5% | 27,103 | 139.1% | 63.1% | 14.9 Mt | 0.20 of its cost |
| seed 2 | 397 | 56.2% | 36.5% | 30,252 | 125.7% | 55.1% | 16.3 Mt | 0.25 |
| seed 3 | 446 | 59.5% | 33.8% | 34,417 | 148.0% | 59.9% | 22.7 Mt | 0.18 |
| seed 0, Historical | 251 (1,724 at m3996) | 71.4% | 22.9% | 19,046 | 150.1% | 42.9% | | 0.18 |
| seed 0 | 0 | | | | | | | 0.42, and none built |
| Rotterdam (Earth) | 0 | | | | | | | 0.97, and none built |

At m2040 all three default seeds read feed 49-50% of sales and net 43%. The
default path is one Random path, the same world for every seed, so the three
agree. Seed 0 builds none because its sector is dead, not because the spread
is absent: one Rod Mill stood at a loss from m648 to m768, and from then to
m3996 the sector has no cash, a 31.30% rate and rating 4, the shortfall desk
having refused it $2,009k, while its Rod Mill candidate clears every gate
(DS1, section 7). Rotterdam is a city stuck at 14,481 people that builds
nothing; its bands show the same drift (rod 2.68, scrap 2.59, wire 2.62 at
m2400). The default playtest the gate runs is seed 0, so no check there sees
the mills.

**The world alone**, the stand-in site: each good's floor over parity with the
cathode, the median of twelve Random paths [10th-90th], then the Historical
path (H). Rod, concentrate and anode read the same at Halifax and Rotterdam;
scrap and wire stand higher at Rotterdam (2.81 and 2.83 in 2200).

| year | concentrate | anode | scrap | rod | wire and cable |
|---|---|---|---|---|---|
| 2000 | 1.01 [0.80-1.10] | 0.79 [0.74-0.88] | 1.00 [0.96-1.12] | 1.07 [1.02-1.19] | 1.26 [0.79-1.83] |
| 2025 | 1.11 [0.89-1.20], H 1.00 | 0.77 [0.72-0.88], H 1.00 | 1.06 [0.90-1.11], H 1.00 | 1.09 [1.06-1.17], H 1.00 | 1.51 [0.77-1.91], H 1.00 |
| 2050 | 1.29 [0.99-1.49], H 1.08 | 0.82 [0.74-0.97], H 1.01 | 1.25 [0.99-1.53], H 1.11 | 1.41 [1.14-1.58], H 1.11 | 1.49 [0.96-2.14], H 1.29 |
| 2100 | 1.49 [1.20-1.79], H 1.11 | 0.95 [0.76-1.08], H 0.93 | 2.27 [1.38-2.53], H 1.63 | 2.41 [1.40-2.63], H 1.61 | 1.90 [1.21-2.88], H 1.34 |
| 2200 | 1.63 [1.49-1.77], H 1.03 | 1.03 [0.85-1.11], H 0.86 | 2.57 [1.80-3.06], H 1.58 | 2.68 [1.96-3.23], H 1.60 | 2.02 [1.52-2.79], H 1.47 |

Rod's tenth percentile is 1.40 by 2100: it is not one path's luck, the makers'
growth is in every path. The Historical path is exact in 2025, where it is
calibrated, and 1.6 by 2100.

**Every works' feed over its sales**, feed imported at the ceiling and output
exported at the floor (a works buying and selling abroad), at the literals and
then drifted; the Random median [10th-90th], the stand-in site:

| works | literals | 2000 | 2100 | 2200 | Historical 2050 / 2100 / 2200 |
|---|---|---|---|---|---|
| Rod Mill | 1.03 | 0.97 [0.91-1.01] | 0.46 [0.44-0.76] | 0.43 [0.34-0.58] | 0.94 / 0.66 / 0.67 |
| Copper Smelter | 0.96 | 1.28 [0.92-1.37] | 1.63 [1.38-1.80] | 1.60 [1.51-1.70] | 1.04 / 1.18 / 1.20 |
| Electrolytic Refinery | 1.02 | 0.83 [0.76-0.91] | 1.04 [0.83-1.22] | 1.15 [0.98-1.28] | 1.05 / 0.98 / 0.92 |
| Scrap Copper Refinery | 0.96 | 1.02 [0.94-1.14] | 2.27 [1.36-2.72] | 2.48 [1.93-3.23] | 1.09 / 1.55 / 1.54 |
| Wire & Cable Works | 0.83 | 0.69 [0.51-1.08] | 0.95 [0.78-1.53] | 1.04 [0.79-1.58] | 0.72 / 0.99 / 0.90 |
| Mini Blast Furnace | 0.96 | 0.95 [0.66-1.20] | 1.07 [0.73-1.75] | 1.26 [0.62-1.97] | 0.81 / 1.32 / 1.28 |
| Electric Arc Furnace | 0.96 | 0.92 [0.79-1.07] | 0.91 [0.76-1.21] | 0.88 [0.76-1.43] | 0.94 / 1.10 / 1.17 |
| Pelletising Plant | 0.94 | 0.75 [0.64-0.84] | 0.95 [0.88-1.14] | 0.94 [0.83-1.17] | 0.87 / 1.02 / 1.13 |
| Coke Ovens | 0.95 | 0.91 [0.73-1.17] | 0.82 [0.77-1.24] | 0.79 [0.70-1.03] | 1.03 / 0.89 / 0.98 |
| Meat Works | 0.73 | 0.67 [0.52-1.07] | 0.58 [0.46-0.93] | 0.75 [0.53-0.99] | 0.79 / 0.70 / 0.76 |
| Bakery | 0.38 | 0.40 [0.17-1.09] | 0.29 [0.12-0.65] | 0.40 [0.22-0.79] | 0.49 / 0.34 / 0.37 |
| Sawmill | 0.81 | 0.82 [0.72-0.92] | 0.94 [0.69-1.12] | 0.99 [0.83-1.25] | 0.82 / 0.76 / 0.91 |

The Rod Mill is the one works whose feed halves on every path. The same
drift shuts the rest of copper's chain: a smelter's concentrate costs 1.6
times its anode and acid by 2100, a scrap refinery's scrap 2.3 times its
cathode, so the chain a mine was meant to feed never pays. Steel's and coke's
works swing either way by path (a Mini Blast Furnace 0.73 to 1.75 in 2100):
the same fault with no settled sign. Across all traded goods the floors stand
apart from the median good's by a factor of about 1.6 either way on a Random
path (sd of the log 0.42-0.50 from 1950) and about 1.2 on the Historical one
after 2025 (0.14-0.23).

## 5. The real margin

**The charge.** A custom smelter is paid a treatment charge a tonne of
concentrate and a refining charge a pound of copper, and pays for 96.5-96.75%
of the copper (Teck, Boliden).

| year | the annual charge, both together | over the copper price | source |
|---|---|---|---|
| 2002 | 17.9 cents a pound | 25% (copper at US$1,559 a tonne) | USGS Minerals Yearbook (public domain) |
| 2003, 2004 | about 12, about 22 cents | 15%, 17% | USGS |
| 2012-2020 | 15.9-27.4 cents | 4.4-11.3% | Teck (evidence only) |
| 2024 | US$80 a dry tonne and 8.0 cents: about 22 cents | about 5% (copper near US$9,100) | the benchmark the literals were set on (Q11) |
| 2025, 2026 | US$21.25 and 2.125 cents; zero | about 1%; nothing | trade press (evidence only) |

In 1986 a US producer's smelting and refining cost 18 cents of a 63-cent
pound (OTA, 1988): a cost, not a margin.

**The books.** Materials over shipments in the Census Bureau's counts:

| industry | materials / shipments |
|---|---|
| copper rolling, drawing, extruding and alloying (331420) | 67.2% (2022), 76.4% (2017), 82.4% (2012) |
| copper rolling and drawing; copper wire drawing (2007) | 86.4%; 77.0% |
| primary copper smelting and refining (331411) | 67.8% (2002), 81.8% (1997), 69-84% (1998-2001) |
| nonferrous smelting and refining (331410; with zinc, lead, nickel) | 75.5% (2022), 61.2% (2017), 49.7% (2012) |
| copper smelting and refining, 1899, 47 plants | 74% |

In 2022 the rolling and drawing mills shipped US$35.9 billion on US$24.1
billion of materials with a payroll of US$1.8 billion: payroll 5% of
shipments and 15% of value added. BEA's primary metals buy 70-76% of gross
output (1997-2025). A US producer's cathode sold 0.9-3.3% over the LME price
(USGS, 2007-23); Aurubis, Europe's largest refiner, kept 0.8-1.6% of revenue
as net income (evidence only).

**So:** a real smelter, refinery or rod mill buys 67-86% of its sales as
materials (the copper lines above) and keeps a few per cent. It never buys 52%
and keeps 35%. The charge has been a quarter of the price (2002) and nothing
(2026), and the literals stand inside that: a smelter and refinery on imported
concentrate buys 93% of its sales (the materials research: a smelter "is never
built without a mine"), a Rod Mill on imported cathode 103% (it wants a
refinery beside it or a home market). The literals are right. What is wrong is
that the played world leaves them.

## 6. The ways, and the prototype

- **(a) Re-anchor a made good on its own good, at the city's site** (the
  recommended way). Each January, between the fills and the curves, a
  follower's price scale is multiplied so that its band with nothing traded
  stands as far from its literals as its own good's does. The solve is not
  touched; nothing new is saved; at the calibration the factor is 1.
- **(b) A corridor:** clamp a follower within some per cent of parity. A dial
  for each chain, a kink in the curve, and a spread inside it that still pays
  at these volumes.
- **(c) A chain-linked solve:** a nation's rod costs its cathode plus a
  conversion, so the nations close the spread themselves. The principled one,
  and a rewrite of WN3's one-good-alone solve, its calibration and its
  January cost. WP1's question, not a batch.
- **(d) Stop the planner:** a cap on exporting works, or FL1's headroom
  tightened. It treats the symptom a third time and leaves the prices wrong
  for a player, who can build the mills by hand.

The prototype runs (a) five ways on main at 0.9.69, to m2400. `copper` anchors
copper's four followers on the cathode; `all` every series' followers on its
own good (`prices.tsv`'s "priced off" lines); `copperwire` and `allwire` add
wire and cable as 70% rod and 30% its own price (the rod's share of the
wire's literal); `path` instead puts every good's band on its own price path.
"A head" is the year's GDP over the price index over the people; the current
account is the last month's over a twelfth of the year's GDP.

| run | Rod Mills / Wire & Cable Works | people | GDP a head | revenue less inputs, over GDP (workers) | current account |
|---|---|---|---|---|---|
| seed 1, main | 363 / 81 | 427,917 | 4.0 | Copper Refining 63% (27k), Luxury Retail 10%, Manufacturing 9% (104k) | 65% |
| seed 1, copper | 4 / 276 | 409,099 | 2.4 | Manufacturing 54% (135k), Luxury 13%, Automotive 11% (43k) | 58% |
| seed 1, copperwire | 0 / 1 | 444,545 | 1.7 | Manufacturing 37% (110k), Automotive 28% (95k) | 52% |
| seed 1, all | 0 / 293 | 564,774 | 1.8 | Manufacturing 48% (170k), Automotive 16% (88k) | 56% |
| seed 1, allwire | 0 / 0 | 619,483 | 2.1 | Manufacturing 35% (151k), Automotive 29% (139k), Luxury 15% | 43% |
| seed 1, path | 0 / 18 | 368,011 | 1.5 | Manufacturing 35% (67k), Automotive 15%, Heavy Industry 12% | 50% |
| seed 2, main | 397 / 62 | 612,856 | 3.4 | Copper Refining 55% (30k), Luxury 13%, Manufacturing 13% (149k) | 64% |
| seed 2, copper | 0 / 314 | 562,580 | 2.4 | Manufacturing 51% (177k), Automotive 15% | 65% |
| seed 2, copperwire | 0 / 1 | 582,397 | 1.7 | Manufacturing 44% (173k), Automotive 23% (99k) | 61% |
| seed 2, all | 0 / 305 | 595,738 | 2.0 | Manufacturing 46% (174k), Automotive 16% (96k) | 55% |
| seed 2, allwire | 0 / 0 | 556,788 | 2.0 | Manufacturing 36% (137k), Automotive 26% (112k), Luxury 16% | 47% |
| seed 2, path | 1 / 12 | 358,903 | 1.7 | Manufacturing 33% (60k), Automotive 15%, Heavy Industry 12% | 49% |
| seed 3, main | 446 / 53 | 521,214 | 4.6 | Copper Refining 60% (34k) | 59% |
| seed 3, allwire | 0 / 1 | 688,798 | 1.7 | Manufacturing 35% (169k), Automotive 31% (171k), Luxury 15% | 47% |
| seed 0, main | 0 / 61 | 562,463 | 1.5 | (no mills: its sector is dead) | |
| seed 0, allwire | 0 / 1 | 467,904 | 2.3 | Manufacturing 36% (124k), Automotive 25% (95k), Luxury 17% | 47% |

What it shows:

- **The anchor holds.** Under it concentrate, anode, scrap and rod read
  1.01, 0.99, 1.03 and 1.05 of parity at m2400 (floors; each keeps its own
  width), where main reads 1.46, 0.86, 2.58 and 2.58.
- **Copper alone moves the machine one step down the chain.** With rod cheap
  again and wire and cable still 2.1 over parity, the Wire & Cable Works go
  from 81 to 276 and from 62 to 314, and Manufacturing takes Copper Refining's
  place at half the city's income.
- **With wire and cable tied to its rod the machine is gone**: no Rod Mills,
  and a Wire & Cable Works for the city's own use at most. Main builds 14-24
  of those by m1200, before the drift, on wire already 1.18 over parity; held
  to its literal (rod 83% of its sales, 400 posts) it does not export.
- **The city keeps its people and loses the windfall.** GDP a head falls from
  3.4-4.6 to 1.7-2.1 on the three seeds with mills, which is where seed 0
  stands without them (1.5); with less coming in from abroad the price index
  ends lower (under `allwire` 0.4 of main's on seed 1, 0.06 on seed 2, 0.8 on
  seed 3). Seed 0, with no mills to lose, goes from 1.5 to 2.3.
- **It is not the whole of the old city's surplus.** In every variant exports
  stay near 0.8-1.4 times GDP and the current account 43-65% of it, carried
  by Manufacturing (33-54%) and Automotive (11-31%): machinery and cars sit on
  the manufactures index with no good of their own to follow. WP1.
- **The path anchor is not ready.** It holds copper's chain within 0.93-1.08
  and squeezes wire (0.61-0.75), but it lets the paths' own swings through
  (thermal coal 0.10 of the median good in 2000, crude 3.08 in 1950) and opens
  coal to steel: Heavy Industry 30-36% of GDP at m1800. The paths are not
  cost-consistent across series. WP1's starting point, not CU2.
- **The runs are clean.** Of the sixteen, thirteen end "every month passed the
  audit and every reload matched"; three print the one finding "a building on
  the map moved", once each, and one of the three is main's own seed 2
  (FD1-D's and BA1-D's known class, DM2's fault).

What it does not show: the suite under the anchor (no harness was run on the
patched class); anything past m2400; an Earth city played under it; a city
with a mine, which no run has.

## 7. What it recommends

**CU2 - the copper chain priced off the cathode at the city's site** (the
batch CU1's row asks for). A brief, for the session that reads the source:

- **Where:** `WorldMarket.refill()`, each January, after the fills and before
  the curves. For each traded good a factor `anchor[gi]`, and the `Curve`
  built on `t.sigma * anchor[gi]` instead of `t.sigma`, so the band, the
  sinks, the sellers and the points all carry it; `millPrice` and `buyerPrice`
  the same. The solve, `resolve`, `lnTermsOfTrade` and the saved `phat` and
  `lambda` stay in the model's units.
- **The factor:** `mid(g)` is the geometric mean of the floor over the literal
  export price and the ceiling over the literal import price, with nothing
  traded (`fills[gi].model(0, 0)` times sigma); a good traded one way takes
  that side. A follower's factor is `mid(its own good) / mid(itself)`.
  The follower table comes from the baked file as `shockOf` does
  (`priceSeries`, and the marks: the series' published good is the own good,
  an estimate on the same series a follower), a `Nations.anchorOf` beside it.
  Wire and cable is rod plus fabrication:
  `(s * mid(COPPER) + (1 - s) * mid(WIRE_CABLE)) / mid(WIRE_CABLE)`, `s` a
  dial taken from the literals (rod's over wire's, 0.70). A series with no
  good of its own (maize, sugar, the manufactures index) is left alone.
- **Saves:** nothing new is saved and `SAVE_FORMAT` does not move; the factor
  is recomputed from the fills, so a reload matches (the prototype's reloads
  did). At Halifax, 2025, on the Historical path the factor is exactly 1: the
  calibration's assertions should stand as they are.
- **Checks:** a new section in `WorldMarketCheck`: the factor is 1 at the
  calibration; every follower stands at its own good's distance from the
  literals, each fifth January, on the Historical path and several Random
  ones, against the model's own table; the wire rule against its dial; and a
  played run that counts Rod Mills on a seed that builds hundreds today (seed
  1: 363 by m2400). `CopperRefiningCheck` (FL1's section 9 among them), the
  reload in `ReadPathCheck`, and a fixture city with a mine and a smelter, so
  the chain the literals mean to pay still pays. The default playtest is seed
  0, which has no mills: look at what `LongPlaytest` no longer exercises on
  the seeds that had them.
- **Its weight:** the trace moves on every seed (every follower's band does),
  so the ensemble is due; money moves, so a stranger's review at the gate.
  One slice (two classes and the harness).

**The calls for Jerus**, each with what the study recommends:

- **★1 The way.** (a) re-anchor at the site, (b) a corridor, (c) a
  chain-linked solve, (d) stop the planner. **Recommended: (a).** It mends the
  price, not the builder; it costs no save and no January time; (c) is the
  better answer and a study away.
- **★2 Its scope.** Copper's four followers, or every series' followers in the
  one batch. **Recommended: every series.** It is one rule, in `prices.tsv`'s
  own words ("priced off the copper price"), the `all` runs are clean, and
  with copper alone the planner's best trade moves to the next chain.
- **★3 Wire and cable as rod plus fabrication, in CU2.** **Recommended: yes.**
  Without it the Wire & Cable Works take the Rod Mills' place (81 to 276).
  With it the city builds one for itself and exports none, which is what its
  literal says.
- **★4 The charge's level.** Keep the literals on the 2024 benchmark, or move
  them, or give the charge a cycle. **Recommended: keep them, no cycle.** The
  real charge has ranged from a quarter of the price to nothing in 24 years;
  2024's 5% is inside it, and a cycle is a mechanic no player has asked for.
- **★5 The path anchor for every good.** **Recommended: not in CU2; WP1.**
- **★6 LX1 waits for CU2, not CU1.** **Recommended: yes.** LX1 measures the
  old city's luxury demand, and CU2 halves the old city's income a head on
  three seeds of four.
- **★7 The dead sector on seed 0.** **Recommended: a look, DS1, queued.**

**WP1 - the world's prices at the city's site, a study.** Why a good's band
leaves its price path (the cheapest maker against the mean, section 3), what
that does to a city's exports once copper is mended (Manufacturing and
Automotive at 61-67% of GDP together and exports near GDP under every site
anchor here), and what a made good's price should be made of (steel from ore
and coal, a car from steel, wire from rod), with the `path` prototype as its
start. It is where spec-wealth's inflow is likely to be found.

**DS1 - seed 0's Copper Refining builds nothing with a candidate that clears
every gate, a look.** On main's default playtest one Rod Mill stands at a loss
from m648 to m768; from then to m3996 the sector has no cash, a 31.30% rate,
rating 4 and a refused shortfall of $2,009k, and builds nothing while its Rod
Mill scores 0.07 of its cost a month at m1200 and 0.42 at m2400. Seeds 1-3
build 363-446; seed 0 on the Historical path loses a mill at m1200 and builds
251 by m2400. Find the gate that holds it and whether any processing sector
can be left this way. CU2 removes this candidate, not the state.

**CU1's row** is settled by this study and CU2 together: the study is done,
the batch that sets the margin is CU2, queued in its place.
