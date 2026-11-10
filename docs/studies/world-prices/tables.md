# The world prices study's tables (WP1)

Every table behind [spec-world-prices.md](../spec-world-prices.md), as the probes in [probes.md](probes.md) printed them. Nothing here is edited.

**The world probe's variants** (the column heads), each a set of `chain.py`'s properties:

| name | what is on |
|---|---|
| `main` | nothing: the copy plays as main |
| `guard` | the solve's guard alone |
| `series` | the price file's table (27 followers wholly on their series' good, wire 70% rod), inputs at the cheapest maker's landed price |
| `seriesces`, `seriesmean` | the same table, an input read as the nation's own price index, and as the world's mean mill price |
| `recipe` | the works' table, a made good's own part on its own series |
| `recipemuv` | the works' table, the own part on the manufactures index |
| `path` | no chain; every good anchored on its path at the reference city |
| `seriespath` | `series` with the roots anchored |
| `recipemuvpath` | `recipemuv` with the roots anchored: what the study recommends |
| `cycle` | `recipemuvpath` with the refining charge on its cycle |
| `world`, `seriesworld` | a maker's supply grown with the world's use of the good (no anchor), alone and under `series` |

**The cities' runs** are named `s<seed><set>`: `m` is main, `series` and `seriespath` as above, `recipe` is `recipemuv`, `recipepath` is `recipemuvpath`, `cycle` as above. Sites: `standin` is the game's stand-in site for a city with no place, `halifax` the calibration's reference city, `rotterdam` a third. Random's median is over twelve drawn paths (seeds 1-11 and 48213); H is the Historical path.

## 1. A follower over its parent

Each good's floor over its literal, over its parent's same ratio: 1 is the literals' ratio.

```text
site standin; each cell: Random's median at 2100, at 2200 [10th-90th], and (H) the Historical path's 2100
good over parent            | main                              | series                            | seriesces                         | seriesmean                        | recipemuv                         | seriespath                        | recipemuvpath
copper_conc/copper          | 1.49 1.63 [1.49-1.77] H 1.11      | 0.97 0.96 [0.94-0.97] H 0.99      | 1.11 1.13 [1.11-1.18] H 1.08      | 2.75 3.19 [2.23-3.82] H 1.69      | 0.97 0.96 [0.94-0.97] H 0.99      | 1.00 1.00 [0.99-1.01] H 1.00      | 1.00 1.00 [0.99-1.01] H 1.00
anode/copper                | 0.95 1.03 [0.85-1.11] H 0.93      | 1.00 1.00 [1.00-1.00] H 1.00      | 1.01 1.01 [1.01-1.01] H 1.00      | 2.75 3.19 [2.25-3.84] H 1.69      | 1.00 1.00 [1.00-1.00] H 1.00      | 1.00 1.00 [1.00-1.00] H 1.00      | 1.00 1.00 [1.00-1.00] H 1.00
copper_scrap/copper         | 2.27 2.57 [1.80-3.06] H 1.63      | 0.90 0.85 [0.75-0.90] H 0.96      | 1.40 1.50 [1.40-1.56] H 1.29      | 2.77 3.15 [2.19-3.76] H 1.70      | 0.90 0.85 [0.75-0.90] H 0.96      | 1.00 0.99 [0.97-1.02] H 1.00      | 1.00 0.99 [0.97-1.02] H 1.00
copper_rod/copper           | 2.41 2.68 [1.96-3.23] H 1.61      | 1.00 1.00 [0.99-1.00] H 1.00      | 1.00 1.00 [0.99-1.00] H 1.00      | 2.75 3.19 [2.25-3.84] H 1.69      | 1.03 1.04 [1.03-1.06] H 1.01      | 1.00 1.00 [1.00-1.00] H 1.00      | 1.00 1.00 [0.99-1.01] H 1.00
wire_cable/copper_rod       | 0.88 0.82 [0.52-1.08] H 0.83      | 1.32 1.46 [1.30-1.73] H 1.09      | 2.01 2.00 [1.93-2.18] H 1.63      | 1.02 1.03 [0.95-1.10] H 0.97      | 1.30 1.43 [1.28-1.66] H 1.09      | 1.01 1.03 [0.95-1.09] H 0.97      | 1.01 1.03 [0.95-1.09] H 0.97
pellets/iron                | 0.98 1.02 [0.94-1.20] H 0.94      | 0.95 0.96 [0.93-1.04] H 0.95      | 0.95 0.96 [0.93-1.04] H 0.95      | 1.13 1.20 [1.10-1.27] H 0.93      | 0.98 1.01 [0.82-1.54] H 1.01      | 0.95 0.95 [0.93-1.04] H 0.94      | 0.95 0.97 [0.79-1.48] H 0.98
pig_iron/iron               | 1.04 1.07 [0.94-1.18] H 0.90      | 0.85 0.86 [0.80-1.09] H 0.83      | 0.85 0.86 [0.80-1.09] H 0.83      | 1.11 1.21 [1.09-1.30] H 0.94      | 1.08 1.22 [0.71-2.11] H 1.12      | 0.86 0.87 [0.81-1.07] H 0.82      | 0.99 1.14 [0.62-2.14] H 1.07
scrap/iron                  | 0.95 0.98 [0.84-1.03] H 0.95      | 1.00 1.00 [0.99-1.00] H 1.00      | 1.01 1.01 [0.99-1.01] H 1.01      | 1.13 1.18 [1.10-1.30] H 0.93      | 1.10 1.23 [0.72-2.06] H 1.12      | 1.00 1.00 [0.99-1.00] H 1.00      | 1.01 1.16 [0.62-2.16] H 1.07
steel/iron                  | 1.01 1.03 [0.93-1.10] H 0.86      | 1.00 1.00 [0.98-1.00] H 1.00      | 1.01 1.01 [0.98-1.02] H 1.01      | 1.13 1.18 [1.10-1.30] H 0.93      | 1.06 1.20 [0.68-2.10] H 1.09      | 1.00 1.00 [0.98-1.01] H 1.00      | 0.97 1.11 [0.59-2.10] H 1.04
fabricated_steel/steel      | 1.08 1.20 [0.40-2.26] H 1.00      | 1.03 1.29 [0.44-2.14] H 0.86      | 1.01 1.28 [0.44-2.14] H 0.85      | 0.93 1.08 [0.33-1.84] H 0.93      | 1.05 1.05 [0.82-1.22] H 0.87      | 1.03 1.28 [0.48-3.09] H 1.08      | 1.06 1.06 [0.83-1.22] H 0.88
machinery/steel             | 1.04 1.07 [0.50-2.40] H 1.16      | 0.98 1.15 [0.56-2.29] H 1.01      | 0.97 1.14 [0.55-2.29] H 0.99      | 0.89 1.01 [0.41-1.90] H 1.08      | 0.96 0.99 [0.78-1.09] H 0.93      | 0.96 1.07 [0.46-2.36] H 1.03      | 0.93 0.98 [0.70-1.05] H 0.91
cars/steel                  | 0.84 0.88 [0.46-1.99] H 1.15      | 0.83 0.93 [0.49-1.89] H 0.99      | 0.81 0.92 [0.48-1.89] H 0.98      | 0.76 0.80 [0.38-1.57] H 1.07      | 0.85 0.83 [0.73-0.97] H 0.94      | 0.94 1.05 [0.45-2.39] H 0.99      | 0.83 0.81 [0.65-0.94] H 0.92
coking_coal/thermal_coal    | 0.81 0.79 [0.67-0.94] H 0.89      | 0.89 0.87 [0.80-0.90] H 0.93      | 0.89 0.88 [0.83-0.90] H 0.93      | 1.00 0.94 [0.90-1.12] H 0.89      | 0.89 0.87 [0.80-0.90] H 0.93      | 0.89 0.87 [0.82-0.90] H 0.93      | 0.89 0.87 [0.82-0.90] H 0.93
met_coke/thermal_coal       | 0.91 0.86 [0.79-1.00] H 0.90      | 0.92 0.92 [0.85-0.93] H 0.96      | 0.92 0.92 [0.88-0.93] H 0.96      | 1.00 0.96 [0.90-1.15] H 0.90      | 0.77 0.77 [0.70-0.89] H 0.83      | 0.92 0.92 [0.86-0.93] H 0.96      | 0.78 0.77 [0.69-0.91] H 0.83
cement/thermal_coal         | 0.61 0.60 [0.43-0.80] H 0.58      | 0.61 0.60 [0.43-0.80] H 0.58      | 0.61 0.60 [0.43-0.80] H 0.58      | 0.61 0.60 [0.43-0.80] H 0.58      | 0.68 0.67 [0.51-0.85] H 0.64      | 0.61 0.63 [0.39-0.98] H 0.59      | 0.63 0.65 [0.46-0.91] H 0.63
petrol/crude                | 1.01 0.98 [0.93-1.02] H 0.99      | 0.97 0.97 [0.96-0.98] H 0.96      | 0.99 0.98 [0.97-1.01] H 0.96      | 1.18 1.11 [1.02-1.28] H 0.98      | 0.98 1.03 [0.90-1.10] H 1.00      | 0.98 0.97 [0.97-0.97] H 0.96      | 0.95 1.00 [0.88-1.07] H 1.00
diesel/crude                | 0.96 0.92 [0.87-0.96] H 0.96      | 0.97 0.97 [0.96-0.98] H 0.96      | 0.99 0.97 [0.97-1.01] H 0.96      | 1.18 1.11 [1.02-1.28] H 0.98      | 0.98 1.02 [0.89-1.09] H 1.00      | 0.98 0.97 [0.97-0.97] H 0.96      | 0.95 1.00 [0.88-1.06] H 1.00
fuel_oil/crude              | 0.86 0.77 [0.71-0.88] H 0.93      | 0.97 0.97 [0.96-0.98] H 0.96      | 0.99 0.98 [0.97-1.01] H 0.97      | 1.18 1.11 [1.02-1.28] H 0.98      | 0.97 1.01 [0.89-1.07] H 0.99      | 0.98 0.97 [0.97-0.97] H 0.97      | 0.94 0.99 [0.88-1.05] H 0.99
naphtha/crude               | 0.81 0.72 [0.66-0.83] H 0.91      | 0.97 0.97 [0.96-0.98] H 0.96      | 0.99 0.98 [0.97-1.01] H 0.97      | 1.18 1.11 [1.02-1.28] H 0.98      | 0.97 1.01 [0.89-1.06] H 0.99      | 0.98 0.97 [0.97-0.97] H 0.97      | 0.94 0.98 [0.87-1.04] H 0.99
pulpwood/sawlogs            | 1.01 1.15 [0.97-1.24] H 0.99      | 1.03 1.06 [0.99-1.12] H 1.00      | 1.03 1.17 [0.99-1.27] H 1.00      | 0.97 1.06 [0.89-1.16] H 0.95      | 1.03 1.06 [0.99-1.12] H 1.00      | 1.02 1.05 [0.99-1.13] H 1.00      | 1.02 1.05 [0.99-1.13] H 1.00
pulp/sawlogs                | 0.83 0.80 [0.78-0.83] H 0.91      | 0.84 0.83 [0.75-0.85] H 0.95      | 0.85 0.84 [0.76-0.86] H 0.95      | 0.96 0.97 [0.94-0.99] H 0.97      | 0.93 0.77 [0.68-0.99] H 0.97      | 0.84 0.83 [0.75-0.86] H 0.94      | 0.94 0.75 [0.66-1.00] H 0.96
lumber/sawlogs              | 0.85 0.68 [0.52-0.97] H 1.07      | 0.85 0.68 [0.52-0.97] H 1.07      | 0.85 0.68 [0.52-0.97] H 1.07      | 0.85 0.68 [0.52-0.97] H 1.07      | 0.92 0.79 [0.73-0.94] H 0.99      | 0.93 0.80 [0.61-1.20] H 1.11      | 0.93 0.79 [0.73-0.94] H 0.98
paper/pulp                  | 1.00 0.85 [0.66-1.23] H 0.99      | 0.99 0.86 [0.66-1.15] H 0.96      | 0.98 0.86 [0.66-1.14] H 0.95      | 0.88 0.69 [0.54-1.02] H 0.94      | 0.98 0.96 [0.93-1.06] H 0.99      | 1.17 1.01 [0.78-1.42] H 0.99      | 0.99 0.97 [0.94-1.12] H 0.99
processed_meat/meat         | 1.00 1.03 [0.96-1.13] H 1.00      | 1.00 1.00 [1.00-1.00] H 1.00      | 1.02 1.06 [1.02-1.08] H 1.00      | 1.01 1.00 [0.93-1.10] H 0.98      | 1.19 1.21 [0.98-1.38] H 0.94      | 1.00 1.00 [1.00-1.00] H 1.00      | 1.16 1.18 [0.99-1.33] H 0.93
bread/grains                | 0.99 0.93 [0.81-1.11] H 0.98      | 0.98 0.98 [0.98-1.01] H 1.00      | 0.99 0.99 [0.99-1.01] H 1.00      | 1.03 1.00 [0.92-1.15] H 0.99      | 0.84 1.10 [0.66-1.66] H 0.86      | 0.98 0.99 [0.98-1.01] H 1.00      | 0.83 1.14 [0.60-1.90] H 0.91
```

## 2. A works' feed over its sales

Feed at the ceiling, sales at the floor, at the recipe `buildings.json` gives.

```text
site standin; a works' feed over its sales; each cell: Random's median at 2100, at 2200, and (H) the Historical path's 2100
works                  literal | main               | series             | recipe             | recipemuv          | seriespath         | recipemuvpath      | cycle
Copper Smelter           0.96  | 1.63 1.60 H 1.18   | 1.02 1.05 H 0.99   | 1.02 1.05 H 0.99   | 1.02 1.05 H 0.99   | 0.96 0.96 H 0.96   | 0.96 0.96 H 0.96   | 0.95 0.95 H 0.97
Electrolytic Refinery    1.02  | 1.04 1.15 H 0.98   | 1.09 1.12 H 1.05   | 1.09 1.12 H 1.05   | 1.09 1.12 H 1.05   | 1.02 1.03 H 1.02   | 1.02 1.03 H 1.02   | 1.01 1.02 H 1.03
Scrap Copper Refinery    0.96  | 2.27 2.48 H 1.55   | 1.10 1.17 H 1.01   | 1.10 1.17 H 1.01   | 1.10 1.17 H 1.01   | 0.96 0.97 H 0.95   | 0.96 0.97 H 0.95   | 0.96 0.97 H 0.95
Rod Mill                 1.03  | 0.46 0.43 H 0.66   | 1.10 1.13 H 1.06   | 1.07 1.10 H 1.04   | 1.07 1.09 H 1.05   | 1.03 1.03 H 1.02   | 1.03 1.03 H 1.03   | 1.03 1.03 H 1.03
Wire & Cable Works       0.83  | 0.95 1.04 H 0.99   | 0.67 0.63 H 0.78   | 0.68 0.64 H 0.78   | 0.67 0.64 H 0.78   | 0.82 0.81 H 0.85   | 0.82 0.81 H 0.85   | 0.82 0.81 H 0.85
Pelletising Plant        0.94  | 0.95 0.94 H 1.02   | 0.98 1.02 H 1.02   | 0.95 0.96 H 0.99   | 0.96 0.97 H 0.96   | 0.95 0.99 H 1.01   | 0.97 0.99 H 0.97   | 0.97 0.99 H 0.97
Coke Ovens               0.95  | 0.82 0.79 H 0.89   | 0.84 0.84 H 0.86   | 0.84 0.87 H 0.89   | 0.98 0.97 H 0.99   | 0.84 0.83 H 0.85   | 0.98 0.97 H 0.99   | 0.98 0.97 H 0.99
Blast Furnace            0.86  | 0.94 1.10 H 1.16   | 1.15 1.33 H 1.30   | 0.84 0.88 H 0.94   | 0.88 0.88 H 0.91   | 1.05 1.27 H 1.25   | 0.89 0.88 H 0.91   | 0.89 0.88 H 0.91
Oxygen Steel Shop        1.09  | 1.08 1.08 H 1.18   | 0.97 0.97 H 0.98   | 1.06 1.08 H 1.12   | 1.09 1.10 H 1.10   | 0.94 0.95 H 0.96   | 1.08 1.09 H 1.10   | 1.08 1.09 H 1.10
Electric Arc Furnace     0.96  | 0.91 0.88 H 1.10   | 0.95 0.99 H 0.98   | 0.93 0.95 H 0.99   | 0.96 0.97 H 0.97   | 0.92 0.96 H 0.96   | 0.95 0.96 H 0.97   | 0.95 0.96 H 0.97
Fabrication Works        0.44  | 0.42 0.37 H 0.47   | 0.44 0.35 H 0.52   | 0.39 0.39 H 0.52   | 0.41 0.43 H 0.51   | 0.41 0.35 H 0.41   | 0.40 0.42 H 0.50   | 0.40 0.42 H 0.50
Machine Works            0.12  | 0.11 0.11 H 0.11   | 0.12 0.11 H 0.12   | 0.13 0.12 H 0.13   | 0.12 0.12 H 0.13   | 0.12 0.12 H 0.11   | 0.12 0.12 H 0.13   | 0.12 0.12 H 0.13
Cement Plant             1.42  | 1.51 1.54 H 1.59   | 1.51 1.54 H 1.59   | 1.35 1.37 H 1.44   | 1.35 1.37 H 1.44   | 1.41 1.42 H 1.53   | 1.35 1.36 H 1.44   | 1.35 1.36 H 1.44
Sawmill                  0.81  | 0.94 0.99 H 0.76   | 0.95 1.01 H 0.76   | 0.88 0.91 H 0.78   | 0.86 0.92 H 0.82   | 0.82 0.88 H 0.73   | 0.86 0.92 H 0.82   | 0.86 0.92 H 0.82
Pulp Mill                0.53  | 0.61 0.62 H 0.58   | 0.61 0.59 H 0.56   | 0.58 0.58 H 0.54   | 0.55 0.62 H 0.55   | 0.61 0.59 H 0.56   | 0.55 0.62 H 0.55   | 0.55 0.62 H 0.55
Paper Mill               0.99  | 1.04 1.15 H 1.02   | 1.05 1.19 H 1.05   | 1.04 1.05 H 1.01   | 1.04 1.05 H 1.01   | 0.93 1.05 H 1.01   | 1.04 1.05 H 1.01   | 1.04 1.05 H 1.01
Panel Mill               0.64  | 0.80 0.89 H 0.68   | 0.81 0.86 H 0.68   | 0.74 0.73 H 0.67   | 0.67 0.72 H 0.69   | 0.64 0.71 H 0.61   | 0.66 0.72 H 0.69   | 0.66 0.72 H 0.69
Meat Works               0.73  | 0.58 0.75 H 0.70   | 0.63 0.75 H 0.69   | 0.68 0.76 H 0.70   | 0.70 0.70 H 0.75   | 0.62 0.76 H 0.72   | 0.69 0.70 H 0.75   | 0.69 0.70 H 0.75
Bakery                   0.38  | 0.29 0.40 H 0.34   | 0.31 0.38 H 0.33   | 0.31 0.40 H 0.35   | 0.35 0.36 H 0.39   | 0.30 0.41 H 0.35   | 0.36 0.36 H 0.39   | 0.36 0.36 H 0.39
```

## 3. A root's band over its path

The geometric middle of floor and ceiling over their literals, over the path's level over the level the literals stand on.

```text
site standin; a root's band over its literals, over its path's level (1 = on the path): Random's median at 1900, 2000, 2100, 2200 [10th-90th at 2200]; (H) 2025 and 2100
good          | main                                        | path                                        | recipemuvpath                               | world
copper        | 1.00 0.91 0.38 0.33 [0.28-0.46] H 1.00 0.60 | 1.00 1.01 1.01 1.01 [1.01-1.01] H 1.00 1.01 | 1.00 1.01 1.01 1.01 [1.01-1.01] H 1.00 1.01 | 0.62 0.35 0.13 0.10 [0.07-0.12] H 1.01 0.28
iron          | 1.04 0.74 0.90 0.86 [0.80-0.92] H 0.99 1.06 | 1.10 0.96 1.02 1.02 [0.89-1.04] H 0.99 1.13 | 1.10 0.96 1.02 1.02 [0.89-1.04] H 0.99 1.13 | 0.86 0.51 0.28 0.20 [0.17-0.25] H 0.99 0.67
thermal_coal  | 0.98 1.03 1.00 1.02 [0.85-1.12] H 1.00 1.08 | 1.02 0.99 1.01 1.00 [0.97-1.07] H 1.00 1.10 | 1.02 0.99 1.01 1.00 [0.97-1.07] H 1.00 1.10 | 0.82 0.62 0.28 0.18 [0.16-0.26] H 1.00 0.64
crude         | 0.99 0.93 0.85 0.88 [0.78-0.98] H 1.00 1.02 | 1.00 1.00 1.02 1.03 [1.01-1.04] H 1.00 1.03 | 1.00 1.00 1.02 1.03 [1.01-1.04] H 1.00 1.03 | 0.69 0.47 0.21 0.18 [0.10-0.25] H 1.00 0.49
sawlogs       | 1.02 1.04 1.04 1.01 [0.99-1.05] H 1.00 1.03 | 1.00 1.00 1.04 1.04 [0.99-1.05] H 1.00 1.04 | 1.00 1.00 1.04 1.04 [0.99-1.05] H 1.00 1.04 | 0.76 0.85 0.53 0.39 [0.33-0.47] H 1.00 0.82
grains        | 0.98 1.06 0.98 0.98 [0.90-1.04] H 1.00 1.01 | 1.00 0.99 0.98 0.98 [0.91-1.02] H 1.00 0.97 | 1.00 0.99 0.98 0.98 [0.91-1.02] H 1.00 0.97 | 0.98 0.69 0.48 0.47 [0.29-0.59] H 1.00 0.86
meat          | 0.87 1.02 0.99 0.98 [0.92-1.04] H 1.00 1.02 | 1.00 1.01 1.00 0.99 [0.95-1.07] H 1.00 1.04 | 1.00 1.01 1.00 0.99 [0.95-1.07] H 1.00 1.04 | 1.02 0.75 0.60 0.47 [0.42-0.56] H 1.00 0.84
lumber        | 1.00 1.03 0.90 0.87 [0.85-0.89] H 1.00 1.01 | 1.00 1.03 1.04 1.03 [0.96-1.05] H 1.00 1.05 | 1.01 0.98 0.97 0.94 [0.78-1.14] H 1.00 0.95 | 0.70 0.73 0.44 0.38 [0.30-0.46] H 1.00 0.65
```

```text
site rotterdam; a root's band over its literals, over its path's level (1 = on the path): Random's median at 1900, 2000, 2100, 2200 [10th-90th at 2200]; (H) 2025 and 2100
good          | main                                        | path                                        | recipemuvpath
copper        | 1.00 0.88 0.37 0.32 [0.27-0.45] H 1.00 0.59 | 1.00 0.99 1.00 1.00 [1.00-1.00] H 1.00 1.00 | 1.00 0.99 1.00 1.00 [1.00-1.00] H 1.00 1.00
iron          | 1.03 0.69 0.88 0.83 [0.76-0.90] H 1.00 0.99 | 1.09 0.92 0.98 0.98 [0.86-0.99] H 1.00 1.05 | 1.09 0.92 0.98 0.98 [0.86-0.99] H 1.00 1.05
thermal_coal  | 0.99 0.97 0.94 0.93 [0.83-1.01] H 1.01 1.07 | 1.06 0.93 0.98 0.96 [0.85-1.03] H 1.01 1.08 | 1.06 0.93 0.98 0.96 [0.85-1.03] H 1.01 1.08
crude         | 1.00 0.90 0.83 0.84 [0.77-0.92] H 1.00 0.98 | 1.00 0.98 0.99 0.99 [0.98-1.00] H 1.00 0.99 | 1.00 0.98 0.99 0.99 [0.98-1.00] H 1.00 0.99
sawlogs       | 1.04 1.02 1.04 1.03 [0.95-1.06] H 1.02 1.03 | 1.02 0.98 1.05 1.03 [0.99-1.07] H 1.02 1.04 | 1.02 0.98 1.05 1.03 [0.99-1.07] H 1.02 1.04
grains        | 1.01 0.97 0.88 0.88 [0.79-0.89] H 1.01 0.98 | 1.04 0.93 0.87 0.84 [0.81-0.88] H 1.01 0.94 | 1.04 0.93 0.87 0.84 [0.81-0.88] H 1.01 0.94
meat          | 0.95 1.01 1.04 1.05 [1.02-1.08] H 1.01 1.03 | 1.08 0.99 1.06 1.07 [1.01-1.17] H 1.01 1.05 | 1.08 0.99 1.06 1.07 [1.01-1.17] H 1.01 1.05
lumber        | 1.00 1.02 0.89 0.87 [0.81-0.92] H 1.01 1.06 | 1.00 1.01 0.99 0.99 [0.99-1.05] H 1.01 1.11 | 1.01 0.97 1.01 0.98 [0.84-1.22] H 1.01 0.97
```

## 4. A good's two sides

```text
site standin; floor over its literal over the path's level, then ceiling the same: Random's median at 2000, 2100, 2200
good              | main                            | recipemuvpath
copper            | 0.89 0.37 0.31 / 0.92 0.39 0.36 | 1.00 1.01 1.01 / 1.01 1.01 1.01
copper_rod        | 0.94 0.87 0.87 / 0.96 0.88 0.87 | 1.00 1.01 1.01 / 1.01 1.01 1.01
wire_cable        | 0.94 0.68 0.60 / 0.93 0.74 0.69 | 0.89 0.91 0.87 / 0.89 0.92 0.89
steel             | 1.04 0.91 0.89 / 1.00 0.90 0.94 | 1.00 0.99 1.12 / 0.99 0.95 1.05
fabricated_steel  | 1.26 1.03 1.03 / nan nan nan    | 1.26 1.16 1.23 / nan nan nan
cars              | 1.01 0.83 0.79 / 0.99 0.85 0.82 | 1.02 0.90 0.87 / 1.02 0.91 0.88
```

## 5. How far the goods' bands stand apart

```text
site standin; the sd across goods of log(floor over literal export), Random's median of the paths; and how many goods' bands are not numbers on any path
year  | main        | series      | recipemuv   | path        | seriespath  | recipemuvpath
1900  | 0.77        | 0.77        | 0.68        | 0.71        | 0.70        | 0.64
1950  | 0.45        | 0.45        | 0.36        | 0.44        | 0.44        | 0.35
2000  | 0.45        | 0.46        | 0.38        | 0.40        | 0.41        | 0.36
2050  | 0.42        | 0.43        | 0.37        | 0.38        | 0.40        | 0.33
2100  | 0.42        | 0.47        | 0.44        | 0.38        | 0.37        | 0.31
2150  | 0.50        | 0.55        | 0.50        | 0.41        | 0.41        | 0.33
2200  | 0.49 (3)    | 0.51 (1)    | 0.46 (1)    | 0.40 (1)    | 0.38 (3)    | 0.30 (2)
2230  | 0.50 (2)    | 0.54        | 0.51        | 0.42        | 0.41        | 0.36
```

## 6. The calibration

The reference city in 2025 on the Historical path, against the game's literals.

```text
main         60 goods; 0 stand more than 0.1% off a literal; the worst RESIDUES, 0.000859% off
series       60 goods; 0 stand more than 0.1% off a literal; the worst FUEL_OIL, 0.000773% off
seriesces    60 goods; 0 stand more than 0.1% off a literal; the worst FUEL_OIL, 0.000773% off
seriesmean   60 goods; 0 stand more than 0.1% off a literal; the worst FUEL_OIL, 0.000773% off
recipe       60 goods; 0 stand more than 0.1% off a literal; the worst BREAD, 0.00169% off
recipemuv    60 goods; 0 stand more than 0.1% off a literal; the worst MACHINERY, 0.00143% off
path         60 goods; 0 stand more than 0.1% off a literal; the worst WOOD_PELLETS, 0% off
seriespath   60 goods; 0 stand more than 0.1% off a literal; the worst WIRE_CABLE, 0.000297% off
recipemuvpath 60 goods; 0 stand more than 0.1% off a literal; the worst MACHINERY, 0.00143% off
cycle        60 goods; 2 stand more than 0.1% off a literal; the worst COPPER_CONC, 4.53% off
world        60 goods; 0 stand more than 0.1% off a literal; the worst WOOD_PELLETS, 0% off
```

## 7. The solve's stops

A good whose solve ran to its 2,000 sweeps, by path, with how many Januaries of the 330.

```text
cycle.log | none
guard.log | none
main.log | R9 CRUDE/NaNx66; R10 CRUDE/NaNx40
path.log | none
recipe.log | none
recipemuv.log | none
recipemuvpath.log | none
series.log | none
seriesces.log | none
seriesmean.log | none
seriespath.log | none
seriesworld.log | none
world.log | none
```

## 8. The January's cost

Each variant alone in turn, twice (the name's last digit is the round); seven paths; other sessions' work shared the machine.

```text
main1          calibration   645 ms; founding January: the first 64 ms, the six after it median 20.8; a January after: median 17.0, 95th 22.7, worst 52.0 ms; 215954 sweeps a path
main2          calibration   684 ms; founding January: the first 78 ms, the six after it median 20.0; a January after: median 17.6, 95th 23.5, worst 73.6 ms; 215954 sweeps a path
recipe1        calibration   869 ms; founding January: the first 68 ms, the six after it median 17.9; a January after: median 16.7, 95th 23.9, worst 71.3 ms; 189971 sweeps a path
recipe2        calibration   878 ms; founding January: the first 65 ms, the six after it median 25.2; a January after: median 23.7, 95th 32.8, worst 86.9 ms; 189971 sweeps a path
recipepath1    calibration   895 ms; founding January: the first 77 ms, the six after it median 31.1; a January after: median 28.0, 95th 40.1, worst 122.1 ms; 175083 sweeps a path
recipepath2    calibration  1075 ms; founding January: the first 94 ms, the six after it median 38.3; a January after: median 35.4, 95th 54.7, worst 175.8 ms; 175083 sweeps a path
series1        calibration  1097 ms; founding January: the first 89 ms, the six after it median 15.0; a January after: median 12.0, 95th 14.7, worst 45.7 ms; 109832 sweeps a path
series2        calibration   887 ms; founding January: the first 68 ms, the six after it median 14.9; a January after: median 12.9, 95th 17.9, worst 76.2 ms; 109832 sweeps a path
seriespath1    calibration   847 ms; founding January: the first 71 ms, the six after it median 31.5; a January after: median 29.2, 95th 38.0, worst 67.6 ms; 90766 sweeps a path
seriespath2    calibration  1212 ms; founding January: the first 108 ms, the six after it median 41.9; a January after: median 35.5, 95th 51.9, worst 126.3 ms; 90766 sweeps a path
```

## 9. The anchor beside the city's prices

A root's mean mill price over its path's level, and the nations' terms of trade as `lnTermsOfTrade` reads them.

```text
== path H, main
H 2025 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL +0.08 COD +0.07 ZMB +0.11 PER +0.07 AUS -0.05 SAU -0.14 CHN +0.04 DEU +0.02 USA -0.02 JPN +0.05
H 2100 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL +0.03 COD -0.44 ZMB -0.41 PER -0.10 AUS -0.03 SAU -0.25 CHN +0.12 DEU +0.03 USA +0.01 JPN +0.12
H 2200 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL -0.05 COD -0.57 ZMB -0.50 PER -0.22 AUS -0.10 SAU -0.45 CHN +0.22 DEU +0.06 USA -0.02 JPN +0.24
== path H, the recipe table with the anchor
WP1: the plain calibration: 55 calibrated, 5 one-sided, 0 on their class's median
H 2025 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL +0.07 COD +0.08 ZMB +0.11 PER +0.07 AUS -0.05 SAU -0.13 CHN +0.04 DEU +0.01 USA -0.02 JPN +0.05
H 2100 mean over path: copper 1.76 iron 1.07 crude 1.01 thermal_coal 1.02; terms of trade (log): CHL +0.29 COD +0.08 ZMB +0.29 PER +0.13 AUS +0.02 SAU -0.26 CHN +0.07 DEU +0.02 USA +0.00 JPN +0.10
H 2200 mean over path: copper 1.79 iron 1.06 crude 0.98 thermal_coal 1.01; terms of trade (log): CHL +0.23 COD -0.02 ZMB +0.22 PER +0.07 AUS -0.06 SAU -0.49 CHN +0.18 DEU +0.05 USA -0.04 JPN +0.21
== path 1, main
1 2025 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL +0.34 COD +0.38 ZMB +0.01 PER +0.39 AUS +0.28 SAU -0.13 CHN -0.19 DEU +0.06 USA +0.02 JPN +0.20
1 2100 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL -0.10 COD -0.78 ZMB -1.10 PER -0.21 AUS -0.09 SAU +0.22 CHN -0.16 DEU +0.10 USA +0.00 JPN +0.15
1 2200 mean over path: copper 1.00 iron 1.00 crude 1.00 thermal_coal 1.00; terms of trade (log): CHL -0.04 COD -0.99 ZMB -0.87 PER -0.23 AUS -0.05 SAU -0.09 CHN -0.01 DEU +0.15 USA -0.08 JPN +0.29
== path 1, the recipe table with the anchor
WP1: the plain calibration: 55 calibrated, 5 one-sided, 0 on their class's median
1 2025 mean over path: copper 1.29 iron 1.60 crude 1.06 thermal_coal 1.04; terms of trade (log): CHL +0.38 COD +0.60 ZMB +0.36 PER +0.42 AUS +0.59 SAU -0.09 CHN -0.30 DEU +0.03 USA +0.01 JPN +0.10
1 2100 mean over path: copper 2.99 iron 1.12 crude 1.32 thermal_coal 1.00; terms of trade (log): CHL +0.28 COD +0.17 ZMB +0.18 PER +0.02 AUS +0.05 SAU +0.42 CHN -0.31 DEU +0.04 USA -0.02 JPN +0.03
1 2200 mean over path: copper 3.58 iron 1.20 crude 1.25 thermal_coal 0.04; terms of trade (log): CHL +0.50 COD +0.17 ZMB +0.34 PER +0.14 AUS +0.12 SAU +0.06 CHN -0.17 DEU +0.09 USA -0.10 JPN +0.20
```

## 10. The refining charge

```text
site standin; over cathode, 1 = the literals' ratio
the Historical path   1990  1995  2000  2005  2010  2015  2020  2025  2030  2035  2040  2045  2050  2055  2060
copper_conc           0.79  0.79  0.82  0.92  1.02  0.93  0.99  1.04  1.02  1.00  1.00  1.00  0.98  1.03  0.99
anode                 0.93  0.93  0.94  0.97  1.01  0.98  1.00  1.02  1.01  1.00  1.00  1.00  0.99  1.01  1.00
the Random paths, every path and fifth year: lowest, 10th, median, 90th, highest
copper_conc           0.74  0.93  0.99  1.02  1.04
anode                 0.91  0.98  1.00  1.01  1.01
a Copper Smelter's feed over its sales, every path and fifth year (871): lowest, 5th, 25th, median, 75th, 95th, highest; the literals' 0.96
                      0.85  0.93  0.95  0.96  0.96  0.98  1.02
```

## 11. The cities at month 2400

People, GDP a head, CU1's real GDP a head, the two copper sectors' share of value added, exports and imports over GDP.

```text
run              people  GDP/head    real copper    exp    imp | the sectors that carry it (value added over GDP)
s0m              562463      2281     1.5     0%   109%    53% | Manufacturing 33%; Automotive 27%; Luxury Retail 14%; Real Estate 9%; Construction 5%
s1m              427917     11351     4.0    63%   159%    85% | Copper Refining 63%; Luxury Retail 10%; Manufacturing 9%; Automotive 5%; Construction 4%
s2m              612856     21617     3.4    55%   156%    86% | Copper Refining 55%; Luxury Retail 13%; Manufacturing 13%; Automotive 6%; Freight 3%
s3m              521214      2710     4.6    60%   169%    99% | Copper Refining 60%; Luxury Retail 13%; Manufacturing 10%; Construction 4%; Automotive 4%
s0series         684975      2418     1.7     0%    86%    34% | Manufacturing 34%; Automotive 31%; Luxury Retail 15%; Real Estate 8%; Construction 5%
s1series         466182      1300     1.7     0%    93%    33% | Manufacturing 41%; Automotive 27%; Luxury Retail 10%; Real Estate 7%; Construction 6%
s2series         656376      1337     2.1     0%    89%    36% | Manufacturing 36%; Automotive 28%; Luxury Retail 16%; Real Estate 6%; Construction 5%
s3series         516154      1974     1.6     0%    85%    34% | Manufacturing 38%; Automotive 26%; Luxury Retail 14%; Real Estate 8%; Construction 5%
s0recipe         720196      1082     1.8     0%    99%    35% | Manufacturing 41%; Automotive 30%; Luxury Retail 9%; Real Estate 7%; Construction 6%
s1recipe         533403       994     1.9     0%    93%    34% | Manufacturing 44%; Automotive 27%; Luxury Retail 11%; Real Estate 7%; Construction 5%
s2recipe         584523      1040     2.2     0%    93%    38% | Manufacturing 42%; Automotive 22%; Luxury Retail 14%; Real Estate 6%; Construction 6%
s3recipe         544377       727     2.0     0%    89%    34% | Manufacturing 40%; Automotive 28%; Luxury Retail 14%; Real Estate 7%; Construction 5%
s0seriespath     430094     10249     1.3     0%    86%    30% | Manufacturing 39%; Automotive 30%; Luxury Retail 12%; Real Estate 7%; Restaurants 4%
s1seriespath     331272      7607     1.7     0%    95%    31% | Manufacturing 45%; Automotive 28%; Luxury Retail 9%; Real Estate 6%; Construction 5%
s2seriespath     507663     27737     1.9     0%    98%    34% | Manufacturing 41%; Automotive 27%; Luxury Retail 11%; Construction 5%; Real Estate 4%
s3seriespath     657124      6366     1.8     0%    95%    32% | Manufacturing 42%; Automotive 28%; Luxury Retail 11%; Construction 5%; Real Estate 5%
s0recipepath     307242     10870     1.5     0%    86%    38% | Manufacturing 41%; Luxury Retail 17%; Automotive 15%; Business Service 7%; Real Estate 7%
s1recipepath     566062      3737     2.2     0%    92%    37% | Manufacturing 44%; Automotive 21%; Luxury Retail 14%; Construction 6%; Real Estate 5%
s2recipepath     555019      5417     1.9     0%    98%    40% | Manufacturing 44%; Automotive 23%; Luxury Retail 13%; Real Estate 5%; Construction 4%
s3recipepath     707310      3858     1.6     0%    92%    35% | Manufacturing 40%; Automotive 29%; Luxury Retail 12%; Real Estate 6%; Construction 3%
s0cycle          307242     10870     1.5     0%    86%    38% | Manufacturing 41%; Luxury Retail 17%; Automotive 15%; Business Service 7%; Real Estate 7%
s1cycle          566062      3737     2.2     0%    92%    37% | Manufacturing 44%; Automotive 21%; Luxury Retail 14%; Construction 6%; Real Estate 5%
s2cycle          543388      4815     2.1     0%    96%    38% | Manufacturing 44%; Automotive 22%; Luxury Retail 13%; Construction 6%; Real Estate 5%
s3cycle          707310      3858     1.6     0%    92%    35% | Manufacturing 40%; Automotive 29%; Luxury Retail 12%; Real Estate 6%; Construction 3%
```

## 12. The cities at months 600, 1200 and 1800

```text
month 600
run              people  GDP/head    real copper    exp    imp | the sectors that carry it (value added over GDP)
s0m               14785        22     0.7     0%   107%    40% | Manufacturing 39%; Business Service 20%; Real Estate 11%; Automotive 5%; Retail 5%
s1m                9508         9     0.9     0%    83%    25% | Automotive 31%; Manufacturing 14%; Business Service 12%; Real Estate 11%; Construction 5%
s2m               21925        16     0.5     0%   109%    44% | Manufacturing 55%; Real Estate 20%; Construction 8%; Freight 7%; Retail 5%
s3m               18535        20     0.8     0%    71%    14% | Business Service 31%; Automotive 23%; Real Estate 12%; Manufacturing 11%; Luxury Retail 5%
s0recipepath      15477        15     0.8     0%    67%    22% | Business Service 32%; Manufacturing 16%; Real Estate 12%; Automotive 9%; Luxury Retail 8%
s1recipepath      12195         7     1.1     0%    85%    31% | Automotive 28%; Manufacturing 25%; Real Estate 10%; Business Service 7%; Luxury Retail 7%
s2recipepath      12775        10     1.2     0%    84%    32% | Automotive 25%; Manufacturing 24%; Real Estate 11%; Business Service 10%; Luxury Retail 7%
s3recipepath      15013        18     1.6     0%    93%    30% | Automotive 45%; Manufacturing 24%; Luxury Retail 6%; Real Estate 6%; Restaurants 5%
month 1200
run              people  GDP/head    real copper    exp    imp | the sectors that carry it (value added over GDP)
s0m               97199       184     1.7     0%   138%    72% | Manufacturing 36%; Construction 19%; Automotive 18%; Materials 8%; Real Estate 5%
s1m               48320       134     1.6     0%   141%    86% | Construction 29%; Manufacturing 24%; Automotive 20%; Real Estate 6%; Luxury Retail 5%
s2m               86228       187     1.7     0%   170%    96% | Manufacturing 44%; Construction 15%; Automotive 13%; Luxury Retail 6%; Real Estate 4%
s3m               60377       110     2.1     0%   151%    86% | Manufacturing 34%; Construction 23%; Automotive 17%; Luxury Retail 7%; Real Estate 4%
s0recipepath      23920       350     1.8     0%    79%    27% | Manufacturing 35%; Construction 35%; Automotive 14%; Real Estate 4%; Freight 3%
s1recipepath     102000        63     2.0     0%    79%    21% | Manufacturing 31%; Automotive 27%; Construction 16%; Business Service 6%; Real Estate 6%
s2recipepath      81250        86     2.1     0%    87%    27% | Manufacturing 46%; Construction 16%; Automotive 16%; Luxury Retail 6%; Real Estate 4%
s3recipepath     105423       120     2.2     0%    93%    28% | Manufacturing 44%; Automotive 23%; Construction 11%; Luxury Retail 8%; Real Estate 4%
month 1800
run              people  GDP/head    real copper    exp    imp | the sectors that carry it (value added over GDP)
s0m              298259       441     1.7     0%   125%    61% | Manufacturing 37%; Automotive 26%; Construction 13%; Luxury Retail 8%; Real Estate 7%
s1m              169374       752     2.0    14%   118%    49% | Manufacturing 30%; Automotive 20%; Construction 14%; Copper Refining 14%; Luxury Retail 7%
s2m              256771      1189     1.3     4%   110%    50% | Manufacturing 41%; Construction 14%; Automotive 13%; Luxury Retail 9%; Real Estate 7%
s3m              179159       383     2.4     8%   104%    45% | Manufacturing 32%; Automotive 18%; Construction 16%; Luxury Retail 11%; Copper Refining 8%
s0recipepath     145242      1526     1.8     0%    76%    26% | Manufacturing 37%; Automotive 19%; Construction 18%; Luxury Retail 9%; Real Estate 5%
s1recipepath     241415       329     1.8     0%    81%    24% | Manufacturing 39%; Automotive 23%; Construction 14%; Luxury Retail 9%; Real Estate 6%
s2recipepath     244182      1055     2.5     0%    90%    30% | Manufacturing 44%; Automotive 22%; Luxury Retail 11%; Construction 10%; Real Estate 4%
s3recipepath     303709       790     2.3     0%    90%    30% | Manufacturing 43%; Automotive 26%; Luxury Retail 11%; Construction 9%; Real Estate 4%
```

## 13. Copper's works at month 2400

Buildings standing (a copper mine, an Electrolytic Refinery, a Rod Mill, a Wire & Cable Works) and Copper Refining's books where it has any.

```text
s0m          copper 0 electrolytic 0 rod 0 wire 61
s1m          copper 0 electrolytic 0 rod 363 wire 81 | refini feed 55% net 38% VA 63.1%
s2m          copper 0 electrolytic 0 rod 397 wire 62 | refini feed 56% net 37% VA 55.1%
s3m          copper 0 electrolytic 0 rod 446 wire 53 | refini feed 60% net 34% VA 59.9%
s0series     copper 0 electrolytic 0 rod 0 wire 0
s1series     copper 0 electrolytic 0 rod 0 wire 0
s2series     copper 0 electrolytic 0 rod 0 wire 1
s3series     copper 0 electrolytic 0 rod 0 wire 1
s0recipe     copper 0 electrolytic 0 rod 0 wire 1
s1recipe     copper 0 electrolytic 0 rod 0 wire 1
s2recipe     copper 0 electrolytic 0 rod 0 wire 2
s3recipe     copper 0 electrolytic 0 rod 0 wire 2
s0seriespath copper 0 electrolytic 0 rod 0 wire 1
s1seriespath copper 0 electrolytic 0 rod 0 wire 3
s2seriespath copper 0 electrolytic 0 rod 0 wire 0
s3seriespath copper 0 electrolytic 0 rod 0 wire 3
s0recipepath copper 0 electrolytic 0 rod 0 wire 4
s1recipepath copper 0 electrolytic 0 rod 1 wire 3 | refini feed 99% net -32% VA 0.0%
s2recipepath copper 0 electrolytic 0 rod 0 wire 4
s3recipepath copper 0 electrolytic 0 rod 0 wire 5
s0cycle      copper 0 electrolytic 0 rod 0 wire 4
s1cycle      copper 0 electrolytic 0 rod 1 wire 3 | refini feed 99% net -32% VA 0.0%
s2cycle      copper 0 electrolytic 0 rod 0 wire 4 | refini feed 99% net -14% VA 0.0%
s3cycle      copper 0 electrolytic 0 rod 0 wire 5
```

## 14. What the cities sell and buy at month 2400

Shares of the year's exports and of its imports, by value.

```text
s0m          sells 109% of GDP: cars 40%; fabricated_steel 32%; wire_cable 20%; lime 5%; iron 2%; fats 0%; crude 0%; bread 0%
             buys   53% of GDP: copper_rod 36%; steel 23%; luxuries 17%; machinery 12%; limestone 4%; fish 2%; fruit 2%; thermal_coal 2%
s1m          sells 159% of GDP: copper_rod 81%; wire_cable 7%; fabricated_steel 6%; cars 5%; lime 0%; fats 0%; bread 0%
             buys   85% of GDP: copper 85%; luxuries 8%; steel 4%; machinery 1%; fish 0%; fruit 0%; vans 0%; meat 0%
s2m          sells 156% of GDP: copper_rod 76%; fabricated_steel 9%; cars 6%; wire_cable 5%; pig_iron 3%; lime 1%; scrap 0%; fats 0%
             buys   86% of GDP: copper 77%; luxuries 10%; steel 5%; iron 2%; machinery 2%; coking_coal 1%; limestone 1%; met_coke 1%
s3m          sells 169% of GDP: copper_rod 84%; fabricated_steel 7%; wire_cable 4%; cars 4%; pig_iron 1%; lime 0%; fats 0%; bread 0%
             buys   99% of GDP: copper 84%; luxuries 8%; steel 4%; met_coke 1%; machinery 1%; iron 1%; fish 0%; fruit 0%
s0series     sells  86% of GDP: cars 58%; fabricated_steel 40%; lime 1%; wood_pellets 0%; iron 0%; fats 0%; crude 0%; bread 0%
             buys   34% of GDP: steel 38%; luxuries 30%; machinery 20%; fish 3%; fruit 3%; meat 2%; limestone 1%; bakery 1%
s1series     sells  93% of GDP: fabricated_steel 50%; cars 47%; lime 3%; wood_pellets 0%; fats 0%; processed_meat 0%; bread 0%
             buys   33% of GDP: steel 48%; luxuries 20%; machinery 18%; fish 3%; fruit 3%; limestone 2%; meat 2%; thermal_coal 1%
s2series     sells  89% of GDP: cars 51%; fabricated_steel 44%; lime 3%; wood_pellets 1%; wire_cable 0%; fats 0%; bread 0%; sawlogs 0%
             buys   36% of GDP: steel 39%; luxuries 31%; machinery 17%; limestone 3%; fish 2%; fruit 2%; meat 1%; thermal_coal 1%
s3series     sells  85% of GDP: fabricated_steel 49%; cars 49%; lime 1%; support_work 1%; wire_cable 0%; fats 0%; bread 0%
             buys   34% of GDP: steel 42%; luxuries 28%; machinery 17%; fish 3%; fruit 3%; meat 2%; limestone 1%; bakery 1%
s0recipe     sells  99% of GDP: cars 52%; fabricated_steel 42%; lime 6%; wire_cable 0%; fats 0%; bread 0%
             buys   35% of GDP: steel 44%; machinery 20%; luxuries 16%; limestone 5%; fish 3%; fruit 3%; thermal_coal 2%; meat 2%
s1recipe     sells  93% of GDP: fabricated_steel 51%; cars 49%; wire_cable 0%; wood_pellets 0%; fats 0%; bread 0%; sawlogs 0%; pulpwood 0%
             buys   34% of GDP: steel 48%; luxuries 24%; machinery 18%; fish 3%; fruit 3%; meat 2%; vegetables 1%; bakery 1%
s2recipe     sells  93% of GDP: fabricated_steel 53%; cars 40%; lime 6%; wire_cable 0%; fats 0%; bread 0%; crude 0%
             buys   38% of GDP: steel 44%; luxuries 25%; machinery 14%; limestone 5%; fish 2%; thermal_coal 2%; fruit 2%; meat 2%
s3recipe     sells  89% of GDP: cars 53%; fabricated_steel 45%; lime 1%; wire_cable 0%; fats 0%; bread 0%
             buys   34% of GDP: steel 43%; luxuries 26%; machinery 19%; fish 3%; fruit 3%; meat 2%; limestone 1%; copper_rod 1%
s0seriespath sells  86% of GDP: cars 61%; fabricated_steel 39%; wire_cable 0%; fats 0%; bread 0%
             buys   30% of GDP: steel 36%; luxuries 28%; machinery 23%; fish 4%; fruit 3%; meat 2%; copper_rod 1%; vegetables 1%
s1seriespath sells  95% of GDP: cars 51%; fabricated_steel 46%; wire_cable 2%; wood_pellets 1%; lime 0%; fats 0%; bread 0%; processed_meat 0%
             buys   31% of GDP: steel 43%; luxuries 21%; machinery 20%; copper_rod 5%; fish 3%; fruit 3%; meat 2%; residues 1%
s2seriespath sells  98% of GDP: cars 49%; fabricated_steel 41%; lime 6%; wood_pellets 3%; lumber 2%; fats 0%; bread 0%; pulpwood 0%
             buys   34% of GDP: steel 37%; luxuries 23%; machinery 19%; limestone 5%; sawlogs 3%; residues 3%; thermal_coal 3%; fish 2%
s3seriespath sells  95% of GDP: cars 50%; fabricated_steel 42%; lime 4%; iron 1%; wood_pellets 1%; wire_cable 1%; lumber 0%; crude 0%
             buys   32% of GDP: steel 39%; luxuries 22%; machinery 20%; limestone 4%; fish 3%; fruit 3%; copper_rod 2%; thermal_coal 2%
s0recipepath sells  86% of GDP: fabricated_steel 56%; cars 29%; support_work 8%; wire_cable 3%; lime 3%; wood_pellets 0%; bread 0%; fats 0%
             buys   38% of GDP: steel 40%; luxuries 31%; machinery 9%; copper_rod 6%; fish 3%; fruit 3%; limestone 2%; meat 2%
s1recipepath sells  92% of GDP: fabricated_steel 53%; cars 40%; lime 5%; wire_cable 1%; support_work 0%; wood_pellets 0%; fats 0%; bread 0%
             buys   37% of GDP: steel 45%; luxuries 26%; machinery 13%; limestone 4%; fish 2%; fruit 2%; copper_rod 2%; thermal_coal 2%
s2recipepath sells  98% of GDP: fabricated_steel 48%; cars 41%; lime 7%; wire_cable 2%; wood_pellets 1%; fats 0%; bread 0%
             buys   40% of GDP: steel 41%; luxuries 23%; machinery 14%; limestone 5%; copper_rod 3%; thermal_coal 3%; fruit 2%; fish 2%
s3recipepath sells  92% of GDP: cars 57%; fabricated_steel 39%; wire_cable 2%; iron 2%; lime 1%; fats 0%; bread 0%; crude 0%
             buys   35% of GDP: steel 40%; luxuries 25%; machinery 20%; copper_rod 4%; fish 3%; fruit 3%; meat 2%; vegetables 1%
s0cycle      sells  86% of GDP: fabricated_steel 56%; cars 29%; support_work 8%; wire_cable 3%; lime 3%; wood_pellets 0%; bread 0%; fats 0%
             buys   38% of GDP: steel 40%; luxuries 31%; machinery 9%; copper_rod 6%; fish 3%; fruit 3%; limestone 2%; meat 2%
s1cycle      sells  92% of GDP: fabricated_steel 53%; cars 40%; lime 5%; wire_cable 1%; support_work 0%; wood_pellets 0%; fats 0%; bread 0%
             buys   37% of GDP: steel 45%; luxuries 26%; machinery 13%; limestone 4%; fish 2%; fruit 2%; copper_rod 2%; thermal_coal 2%
s2cycle      sells  96% of GDP: fabricated_steel 50%; cars 40%; lime 8%; wire_cable 2%; wood_pellets 1%; fats 0%; bread 0%; sawlogs 0%
             buys   38% of GDP: steel 34%; luxuries 25%; machinery 14%; scrap 6%; limestone 6%; thermal_coal 3%; copper_rod 2%; fruit 2%
s3cycle      sells  92% of GDP: cars 57%; fabricated_steel 39%; wire_cable 2%; iron 2%; lime 1%; fats 0%; bread 0%; crude 0%
             buys   35% of GDP: steel 40%; luxuries 25%; machinery 20%; copper_rod 4%; fish 3%; fruit 3%; meat 2%; vegetables 1%
```
