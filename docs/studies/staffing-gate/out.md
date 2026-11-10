# The staffing-gate study's runs

The measurements behind [the staffing-gate study](../spec-staffing-gate.md), each table printed by the kit's scripts
([kit.md](kit.md)) from the runs' CSVs and logs; the runs themselves stay outside the repository. Every city is the
AUTO2 founding (the ensemble's `foundAndPlay`) on seeds 0-15, played by `GateProbe` in a `TempDirs` folder, on main
at 0.9.72 (0e235b0f) with the wages study's `migration-target.patch` and the study's `gate.patch` applied, each
neutral until a property asks for it.

**The variants** (`run.sh`'s names; the properties each passes):

| variant | what it is | properties |
|---|---|---|
| today | main's gate: 80% of the posts from the spare workers | none |
| g60, g40 | a lower share, no draw | `gate.min=.6`, `gate.min=.4` |
| g0 | no share test; the band nobody can fill still refuses | `gate.min=0` |
| draw6, draw12, draw24 | 80% from the spare workers and those migration would bring for the posts within 6, 12 or 24 months | `gate.draw=6`, `12`, `24` |
| aim | today's gate with WG2's aim: arrivals slow past 6% out of work and stop at 12%; the jobless past 6% leave | `wages.brake=.06/.12,wages.leave=true` |
| slack05 | today's gate, migration aiming 5% over the jobs against 12.5% | `wages.slack=.05` |
| d12aim, d12slack05 | the 12-month draw with the aim, or with the 5% slack | both sets |
| g0aim | no share test with the aim | `gate.min=0` and the aim's |

## Table 1: the 16 seeds to month 2,400

`summarize.py`. Medians over the seeds. Out of work is the mean of the months named; workers a post, posts
unfilled, holds, unhoused and sectors losing are means from month 240; inflation is the price index's yearly rate
from month 240 to 2,400. People and output a head (a year's GDP over the price index, per head) are each seed's
ratio to `today` on the same seed: the median, the seeds above their twin, and the lowest and highest.

| variant | out of work, m240+ | m1200+ | young: m24-240 | workers a post | posts unfilled | holds a month | unhoused | people m240 | m480 | m1200 | m2400 | output a head m2400 | sectors losing | inflation |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| today | 11.5% | 8.5% | 12.0% | 1.11 | 2.3% | 0.6 | 0.09% | 3.5k | 11.3k | 65.0k | 557.7k | 0.148 | 2.6 | 2.23% |
| aim | 8.2% | 6.5% | 12.5% | 1.07 | 2.3% | 0.6 | 0.10% | -42% (2 of 16 up; -68 to +11) | -21% (4 of 16 up; -57 to +112) | +9% (9 of 16 up; -33 to +138) | +9% (11 of 16 up; -19 to +67) | -16% (3 of 16 up; -42 to +16) | 2.8 | 2.47% |
| d12aim | 8.3% | 6.1% | 5.2% | 1.06 | 3.2% | 0.6 | 0.09% | +54% (12 of 16 up; -60 to +289) | +22% (12 of 16 up; -55 to +273) | +148% (14 of 16 up; -38 to +380) | +22% (12 of 16 up; -19 to +68) | -0% (7 of 16 up; -27 to +40) | 2.4 | 1.98% |
| d12slack05 | 8.2% | 4.8% | 4.4% | 1.05 | 3.5% | 0.6 | 0.08% | +62% (16 of 16 up; +3 to +168) | +53% (13 of 16 up; -15 to +195) | +131% (15 of 16 up; -9 to +366) | +9% (9 of 16 up; -31 to +59) | +3% (9 of 16 up; -16 to +48) | 2.7 | 1.99% |
| draw12 | 11.7% | 8.6% | 6.3% | 1.10 | 3.1% | 0.5 | 0.08% | +58% (14 of 16 up; -11 to +229) | +20% (13 of 16 up; -23 to +195) | +112% (14 of 16 up; -31 to +402) | +12% (11 of 16 up; -29 to +67) | -6% (7 of 16 up; -29 to +23) | 2.6 | 2.00% |
| draw24 | 6.8% | 3.9% | 6.8% | 1.04 | 3.7% | 0.7 | 0.08% | +173% (16 of 16 up; +25 to +340) | +119% (16 of 16 up; +32 to +506) | +169% (14 of 16 up; -85 to +747) | -4% (8 of 16 up; -47 to +55) | +9% (12 of 16 up; -19 to +42) | 2.2 | 1.88% |
| draw6 | 12.9% | 10.1% | 6.6% | 1.11 | 3.3% | 0.5 | 0.09% | +37% (12 of 16 up; -60 to +126) | +19% (11 of 16 up; -31 to +111) | +26% (10 of 16 up; -52 to +243) | +1% (8 of 16 up; -30 to +43) | -2% (7 of 16 up; -24 to +40) | 2.6 | 2.08% |
| g0 | 10.3% | 7.0% | 11.4% | 1.09 | 3.3% | 0.0 | 0.08% | +167% (16 of 16 up; +1 to +347) | +104% (16 of 16 up; +21 to +484) | +167% (16 of 16 up; +22 to +518) | +27% (12 of 16 up; -36 to +52) | +15% (12 of 16 up; -23 to +39) | 2.5 | 1.91% |
| g0aim | 8.4% | 6.9% | 8.8% | 1.05 | 3.5% | 0.0 | 0.08% | +106% (15 of 16 up; -10 to +278) | +98% (14 of 16 up; -4 to +552) | +247% (16 of 16 up; +23 to +409) | +29% (12 of 16 up; -8 to +49) | +4% (9 of 16 up; -22 to +36) | 2.6 | 2.03% |
| g40 | 13.1% | 9.7% | 7.9% | 1.11 | 3.7% | 0.5 | 0.10% | +68% (14 of 16 up; -22 to +138) | +33% (11 of 16 up; -29 to +174) | +38% (10 of 16 up; -71 to +292) | -4% (6 of 16 up; -45 to +63) | +6% (9 of 16 up; -26 to +16) | 2.8 | 2.13% |
| g60 | 12.0% | 8.5% | 8.4% | 1.10 | 3.6% | 0.5 | 0.09% | +95% (14 of 16 up; -13 to +259) | +22% (12 of 16 up; -28 to +235) | +40% (10 of 16 up; -71 to +229) | +8% (9 of 16 up; -76 to +53) | -7% (6 of 16 up; -44 to +50) | 2.5 | 2.11% |
| slack05 | 8.5% | 6.2% | 14.3% | 1.07 | 2.5% | 0.8 | 0.09% | -8% (4 of 16 up; -61 to +52) | -19% (5 of 16 up; -44 to +70) | -3% (7 of 16 up; -61 to +215) | -14% (4 of 16 up; -25 to +28) | -6% (6 of 16 up; -21 to +43) | 2.5 | 2.31% |

## Table 2: out of work by stretch, and the falls

`busts.py`. Each stretch's out of work, the median over the seeds (the worst seed in brackets); the months a seed
spends above 25% out of work from month 24; the seeds whose people fell to 70% of their high or below after month
120 (the month it happened); and the falls of the posts, each from a high of 500 posts or more to 70% of it or
below, counted once until a new high (the seeds with one, and the falls in all).

| variant | m24-240 | m240-480 | m480-720 | m720-1200 | m1200-2400 | months above 25%, median (worst) | seeds whose people fell 30% from their high | posts fell 30% from their high, seeds (falls) |
|---|---|---|---|---|---|---|---|---|
| today | 12.1% (19) | 11.3% (16) | 18.9% (28) | 14.8% (20) | 8.5% (11) | 111 (232) | s0 m1018, s6 m586, s15 m598 | 16 (25) |
| aim | 12.5% (15) | 8.2% (10) | 13.9% (18) | 9.9% (11) | 6.5% (7) | 45 (71) | s3 m685 | 11 (21) |
| d12aim | 5.1% (11) | 10.2% (14) | 12.9% (16) | 9.2% (10) | 6.1% (10) | 14 (48) | none | 9 (15) |
| d12slack05 | 4.4% (8) | 12.2% (19) | 15.9% (22) | 9.6% (12) | 4.9% (11) | 14 (77) | none | 9 (11) |
| draw12 | 6.3% (11) | 13.9% (21) | 20.1% (26) | 12.9% (18) | 8.6% (16) | 42 (146) | none | 8 (10) |
| draw24 | 6.8% (11) | 13.4% (18) | 14.8% (30) | 7.4% (25) | 3.9% (16) | 16 (469) | s5 m584, s7 m231 | 11 (12) |
| draw6 | 6.7% (11) | 11.2% (22) | 21.9% (25) | 14.4% (18) | 10.1% (13) | 104 (214) | none | 13 (30) |
| g0 | 11.4% (14) | 15.8% (23) | 16.5% (22) | 13.7% (16) | 7.0% (17) | 13 (133) | none | 12 (14) |
| g0aim | 8.8% (11) | 10.9% (13) | 10.9% (14) | 9.2% (10) | 6.9% (11) | 6 (31) | none | 12 (15) |
| g40 | 7.9% (12) | 16.3% (23) | 20.9% (29) | 14.7% (23) | 9.7% (15) | 94 (284) | s7 m570, s8 m611, s11 m1039 | 13 (18) |
| g60 | 8.4% (13) | 17.8% (27) | 19.8% (27) | 14.3% (28) | 8.5% (20) | 61 (1032) | none | 10 (11) |
| slack05 | 14.4% (18) | 8.4% (15) | 16.3% (20) | 10.2% (15) | 6.2% (10) | 70 (144) | s0 m258 | 16 (35) |

## Table 3: who the gate refuses (seeds 0-3, months 1-480)

`instrument.patch` and `heldsum.py`. Every order for one building that fails on the spare workers alone, with
the band test passed: the asks summed over the four seeds (a sector asks again every month it still wants the
building), the share of its posts the spare could fill, how many of those asks a 12-month draw would pass, and the
first and last month it was refused. These eight runs were played earlier the same day, on main before R6 (0.9.71's
tree, without R6's research rows); every other table is on 0.9.72. The city as it is (`today`):

4 seeds, 65 buildings refused at least once

| sector, building | asks | seeds | share the spare fills | asks a 12-month draw passes | first-last month |
|---|---|---|---|---|---|
| Manufacturing, Machine Works | 2087 | 4 | 0.00-0.79 | 1269 (61%) | 2-480 |
| Business Services, Shared Services Centre | 1916 | 4 | 0.00-0.32 | 0 (0%) | 2-480 |
| Wood Products, Pulp Mill | 1907 | 4 | 0.00-0.80 | 1801 (94%) | 2-480 |
| Heavy Industry, Oxygen Steel Shop | 1874 | 4 | 0.00-0.78 | 1464 (78%) | 2-480 |
| Heavy Industry, Blast Furnace | 1861 | 4 | 0.00-0.78 | 1694 (91%) | 2-480 |
| Wood Products, Paper Mill | 1788 | 4 | 0.00-0.80 | 1736 (97%) | 2-480 |
| Copper Refining, Copper Smelter | 1695 | 4 | 0.00-0.80 | 1584 (93%) | 2-480 |
| Business Services, Contact Centre | 1458 | 4 | 0.00-0.80 | 1207 (83%) | 2-480 |
| Heavy Industry, Large Mini-Mill | 1296 | 4 | 0.00-0.80 | 1222 (94%) | 2-479 |
| Manufacturing, Wire & Cable Works | 865 | 4 | 0.00-0.80 | 792 (92%) | 2-479 |
| Manufacturing, Fabrication Works | 528 | 4 | 0.00-0.80 | 472 (89%) | 2-479 |
| Copper Refining, Electrolytic Refinery | 458 | 4 | 0.00-0.80 | 404 (88%) | 2-479 |
| Copper Refining, Scrap Copper Refinery | 458 | 4 | 0.00-0.80 | 404 (88%) | 2-479 |
| Heavy Industry, Coke Ovens | 458 | 4 | 0.00-0.80 | 404 (88%) | 2-479 |
| Manufacturing, Fabrication Shop | 458 | 4 | 0.00-0.80 | 380 (83%) | 2-479 |
| Refining, Crude Unit | 427 | 4 | 0.00-0.80 | 363 (85%) | 2-479 |
| Cement & Concrete, Large Cement Plant | 382 | 4 | 0.00-0.80 | 336 (88%) | 2-479 |
| Heavy Industry, Mini Blast Furnace | 382 | 4 | 0.00-0.80 | 336 (88%) | 2-479 |
| Heavy Industry, Pelletising Plant | 382 | 4 | 0.00-0.80 | 336 (88%) | 2-479 |
| Heavy Industry, Steel Mini-Mill | 358 | 4 | 0.00-0.79 | 310 (87%) | 2-479 |
| Wood Products, Large Sawmill | 358 | 4 | 0.00-0.79 | 310 (87%) | 2-479 |
| Refining, Oil Refinery | 329 | 4 | 0.00-0.80 | 269 (82%) | 2-479 |
| Wood Products, Panel Mill | 316 | 4 | 0.00-0.80 | 272 (86%) | 2-479 |
| Retail, Filling Station | 303 | 4 | 0.33-0.61 | 249 (82%) | 33-473 |
| Agriculture, Mixed Farm | 292 | 4 | 0.33-0.61 | 238 (82%) | 33-473 |
| Copper Refining, Rod Mill | 291 | 4 | 0.00-0.80 | 252 (87%) | 2-478 |
| Cement & Concrete, Cement Plant | 269 | 4 | 0.00-0.80 | 233 (87%) | 2-477 |
| Heavy Industry, Electric Arc Furnace | 248 | 4 | 0.00-0.79 | 205 (83%) | 2-477 |
| Cement & Concrete, Lime Kiln | 246 | 4 | 0.00-0.80 | 216 (88%) | 2-477 |
| Refining, Coker | 246 | 4 | 0.00-0.79 | 202 (82%) | 2-477 |
| all | 28585 | | | 22763 (80%) | |

The same count in the 12-month draw's city (`draw12`), where the asks the draw passes are built and the rest are
refused, as they are today:

4 seeds, 66 buildings refused at least once

| sector, building | asks | seeds | share the spare fills | asks a 12-month draw passes | first-last month |
|---|---|---|---|---|---|
| Manufacturing, Machine Works | 2035 | 4 | 0.00-0.80 | 1402 (69%) | 2-480 |
| Business Services, Shared Services Centre | 1916 | 4 | 0.00-0.32 | 0 (0%) | 2-480 |
| Wood Products, Pulp Mill | 1853 | 4 | 0.00-0.80 | 1621 (87%) | 2-480 |
| Wood Products, Paper Mill | 1815 | 4 | 0.00-0.80 | 1659 (91%) | 2-480 |
| Construction, Construction Depot | 1416 | 4 | 0.00-0.78 | 1269 (90%) | 3-425 |
| Heavy Industry, Oxygen Steel Shop | 1401 | 4 | 0.00-0.80 | 1074 (77%) | 2-440 |
| Heavy Industry, Blast Furnace | 1026 | 4 | 0.00-0.80 | 778 (76%) | 2-440 |
| Construction, Builders' Yard | 851 | 4 | 0.00-0.57 | 617 (73%) | 3-425 |
| Copper Refining, Copper Smelter | 807 | 4 | 0.00-0.80 | 607 (75%) | 2-431 |
| Business Services, Contact Centre | 761 | 4 | 0.00-0.80 | 329 (43%) | 2-437 |
| Heavy Industry, Large Mini-Mill | 668 | 4 | 0.00-0.80 | 492 (74%) | 2-426 |
| Manufacturing, Wire & Cable Works | 659 | 4 | 0.00-0.80 | 409 (62%) | 2-429 |
| all | 32154 | | | 20744 (65%) | |

## Table 4: when the first of each large building stands (seeds 0-7, to month 1,200)

`probe-instrument.patch` and `firsts.py`. For each building of 100 posts or more, the month it first stands and
the city's people then (medians over the seeds it stood in by month 1,200, and how many). `first` is the city as
it is, `first12` the 12-month draw, `first0` no share test. Rows in the order the city as it is builds them.

| building (posts) | first: month, people (seeds) | first12: month, people (seeds) | first0: month, people (seeds) |
|---|---|---|---|
| Machine Works (173) | m39, 0.6k (7 of 8) | m74, 2.0k (8 of 8) | m88, 3.5k (8 of 8) |
| Fabrication Shop (135) | m89, 1.5k (8 of 8) | m730, 22.6k (4 of 8) | m70, 3.3k (5 of 8) |
| Bus Network (250) | m134, 2.9k (8 of 8) | m132, 3.5k (8 of 8) | m129, 3.8k (8 of 8) |
| Contact Centre (300) | m135, 1.5k (8 of 8) | m56, 1.5k (8 of 8) | m312, 8.7k (8 of 8) |
| Police Station (172) | m318, 4.9k (8 of 8) | m150, 4.8k (8 of 8) | m148, 6.3k (8 of 8) |
| Fabrication Works (276) | m402, 9.2k (8 of 8) | m31, 0.8k (8 of 8) | m32, 0.8k (8 of 8) |
| Construction Materials Plant (250) | m402, 8.7k (8 of 8) | m156, 6.1k (8 of 8) | m72, 3.3k (8 of 8) |
| Vehicle Works (170) | m423, 10.2k (8 of 8) | m56, 1.4k (8 of 8) | m56, 1.9k (8 of 8) |
| Steel Mini-Mill (130) | m624, 14.9k (3 of 8) | m430, 18.1k (3 of 8) | m285, 10.3k (3 of 8) |
| Assembly Plant (700) | m626, 15.6k (8 of 8) | m428, 13.6k (8 of 8) | m404, 15.2k (8 of 8) |
| Rail Depot (148) | m751, 17.0k (7 of 8) | m570, 20.9k (8 of 8) | m504, 24.7k (8 of 8) |
| Truck Depot (125) | m817, 21.0k (6 of 8) | m786, 39.2k (8 of 8) | m748, 36.1k (8 of 8) |
| Rail Spur (173) | m888, 34.9k (6 of 8) | m788, 42.2k (8 of 8) | m750, 47.2k (8 of 8) |
| Pelletising Plant (150) | m985, 20.5k (5 of 8) | m807, 43.8k (4 of 8) | m596, 34.0k (4 of 8) |
| Large Mini-Mill (400) | m1005, 50.4k (3 of 8) | m1004, 72.1k (7 of 8) | m816, 67.0k (7 of 8) |
| Scrap Copper Refinery (200) | m1099, 32.8k (5 of 8) | m854, 68.5k (7 of 8) | m770, 56.1k (6 of 8) |
| Locomotive Works (310) | m1125, 29.6k (1 of 8) | - (0 of 8) | m769, 86.6k (1 of 8) |
| Jail (264) | m1199, 102.6k (1 of 8) | m1144, 135.3k (2 of 8) | m968, 121.7k (3 of 8) |
| Pulp Mill (200) | - (0 of 8) | m624, 18.7k (6 of 8) | m527, 30.8k (5 of 8) |
| Shared Services Centre (300) | - (0 of 8) | - (0 of 8) | m54, 1.8k (8 of 8) |
| Bulk Terminal (426) | - (0 of 8) | - (0 of 8) | m1184, 180.1k (1 of 8) |
| Police Headquarters (750) | - (0 of 8) | m1133, 126.5k (1 of 8) | m1068, 138.3k (2 of 8) |
| Blast Furnace (1200) | - (0 of 8) | m1171, 99.1k (1 of 8) | m645, 28.1k (1 of 8) |

## Table 5: seed 0's young city

Seed 0 of table 1's runs, every 12 months to 120, then every 24: people, out of work and posts; for the city as
it is, the planners' holds that bid the wage that month (`asked`, LD1's); for the draw's city, the workers the
month's last order counted on coming (`drawn`).

```
seed 0, the young city: today | draw12 | g0 (no share test)
month |  people   out  posts asked |  people   out  posts  drawn |  people   out  posts
   12 |     158 36.8%     62     1 |     166  0.0%    107     21 |     158  0.0%    107
   24 |     470 29.9%    189     0 |     555  5.0%    287     36 |     600 11.0%    286
   36 |     593 48.2%    179     0 |    1002  0.0%    647     12 |     990  0.2%    715
   48 |     840 30.0%    356     0 |    1202  0.1%    677      7 |    1460  0.0%    937
   60 |     935  0.2%    535     1 |    1880  0.0%   1123     33 |    2175 21.5%   1347
   72 |    1164 24.3%    503     0 |    2715  4.3%   1481     14 |    3616  9.5%   2062
   84 |    1143  0.0%    690     0 |    3269  0.0%   1934      9 |    3948 18.1%   2067
   96 |    1143 21.0%    505     0 |    3345  0.8%   1934      8 |    3765 14.5%   2067
  108 |    1186 23.6%    505     0 |    3422 13.7%   1718     13 |    3865 15.6%   2067
  120 |    1222 25.3%    505     0 |    3306  9.4%   1732     16 |    3789 14.9%   2067
  144 |    1912 17.5%    860     0 |    5276  0.0%   3136     16 |    6759  6.7%   4120
  168 |    1942 19.1%    868     0 |    7549 13.5%   3789     18 |   11280 13.8%   6204
  192 |    2548 14.8%   1180     0 |    7614 14.5%   3801     17 |   11082 20.7%   5380
  216 |    2350  8.7%   1184     0 |    7271 11.7%   3801     16 |   11180 19.5%   5540
  240 |    2627  8.5%   1337     0 |    7675 16.5%   3801     17 |   10013 34.2%   3871
```
