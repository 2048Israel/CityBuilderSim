# MA1 - what carries the city next: notes

The batch is docs only: `docs/studies/manufacturing-margin.md`, its probe,
prototypes, sources and the runs' tables in `docs/studies/manufacturing-margin/`,
the study's row in `docs/studies/README.md` and two rows in
`docs/studies/licences.md`. No code, no harness, no version (no {next}; shipped
`--no-tag`). The brief is the queue's MA1 row: spec-world-prices' ★8 (a), on
CU2's notes (the default city at m2400 exporting $1.64B a month of cars and
$0.75B of Manufacturing, 70% of its exports).

## What was read

**The game's side** (Opus):

- `Good`'s literals for steel, fabricated steel, machinery, the four vehicles
  and lubricants; `WorldChain.RECIPE`; `WorldMarket.solveYear` (L1813-1870)
  and `Base`'s `price`, `moveOntoLevel`, `anchor`, `ownLevelGood`,
  `refLevel` and `off`.
- `buildings.json` rows 49-51, 65-68, 128 and 140, and the file's note on
  consumables.
- `Sector`'s `buyConsumables`, `consumablesBill` and
  `estimatedMonthlyProfit`; `Manufacturing`'s `plan`, `estimatedMonthlyProfit`
  and the cost shares (L170-182); `Automotive`'s `plan`;
  `SpreadPlanner.ownCosts`.
- `BusinessInvestment`'s hurdle (L1295-1345) and `totalCostOf`;
  `BuildingManager.nonMaterialCost`.
- `ManufacturingCheck`'s "the three plants" and "the ground is the brake";
  CU2's notes; CU1's `run.sh`, the shape of this one; `licences.md`.

**The sources**, fetched 2026-11-17: the 2022 Economic Census's manufacturing
tables (`EC2231BASIC`, `EC2231MATFUEL`, through the Census API) and BEA's
summary use tables before redefinitions, 1997-2023 (the workbook). Both are US
public domain; `licences.md` has their two rows. The extracts are kept beside
the study.

**The runs:** nineteen `LongPlaytest` runs of 2,400 months in `TempDirs`
folders, never a save slot: main on seeds 0-2 and on the Historical path, and
five prototypes on seeds 0-2. At most three ran side by side, one probe at a
time. Three runs of a first hold, found wrong, are not reported.

## What was found and decided

- **CU2's 41% of GDP holds; its split does not.** On 0.9.138 the two sectors
  carry 52-76% of the exports and 42-51% of GDP on four cities, and
  Manufacturing leads on three of them.
- **The goods and the wages are real.** A Fabrication Works' steel is 38% of
  its sales at the literals, a car's metal and machinery 26%; wages are
  $49-62 thousand a year against the census's $62-77 thousand.
- **The missing purchases are BEA's "rest".** A real works buys 50-69% of its
  output from other firms. The game's buy only their recipe, 8-43%, so they
  keep 21-41% against the census's 13-23%.
- **Fabricated steel's band floats 1.1-1.8 times its chain**, and a car sits
  at 0.67-1.03 of its metal: only roots are anchored at the city's site.
- **A works costs 0.63-1.23 years of its sales to build.** The census's
  assets at cost are 0.29-0.41.
- **The Machine Works sells $134 thousand a post a year.** A machine shop's
  worker sells $197 thousand, a machinery maker's $417 thousand.
- **Power is all but free** (0.0% of sales against 0.2-0.8%). It is left to
  the PW batches.
- **The prototypes** (study §6), people at m2400 on seeds 0-2:

  | run | seeds 0, 1, 2 |
  |---|---|
  | main | 917k, 293k, 867k |
  | hold | 1.91M, 1.71M, 1.47M |
  | way (a) | 968k, 1.46M, 638k |
  | way (b) and the hold | 40k, 39k, 1.57M |
  | books | 386k, 38k, 2.36M |
  | books and the hold | 156k, 42k, 1.09M |

- **Way (b) over way (a).** Way (a) ignores what the recipe already buys. Its
  bill, fixed in 2025 money, closes the Machine Works and the car plants and
  leaves the fabricator at 33%.
- **The four book changes go in one batch.** Purchases without the build cost
  stall two cities of three, and the build cost without the purchases is the
  old margin on a cheaper plant.
- **The hold goes first.** Alone it lets every seed take off sooner, and with
  the band held, MA3's rows, set against the literals, mean what they say.
- **The prototype's hold lets fabricated steel go once the world tips.** That
  happens only in cities already taking off. Every tipped run then reads the
  same band. Steel, machinery and cars stay held; fabricated steel, the one
  held good with no import side, does not. MA2 starts there.
- **MA4 waits in Pending, not the queue.** It is needed only if MA3's
  ensemble finds more cities that never take off than main's.
- **The first hold was wrong and was discarded.** It chained each input's
  target instead of its band. `hold.py` reads the band.

## The calls

All five are in one Pending row, MA1-K. Each is recommended (a), with its
reason:

- **★1, the purchases: its industry's less its recipe's (a).** A works then
  buys what its industry buys in all.
- **★2, the Machine Works' crew: a machine shop's, 117 posts (a).** It is the
  least cut that keeps the works paying with its purchases (10-51 standing at
  m2400).
- **★3, the build cost: the census's assets a sale (a).** At today's build
  cost, real purchases stall two cities of three.
- **★4, the six manufactures held (a).** They are the goods measured and
  found floating; the other made goods are unmeasured.
- **★5, a city that never takes off: MA3 ships, and its ensemble counts them
  (a).** MA4 asks why only if there are more than main's.

## Batches queued (the ship file's `## queued`)

Ids were checked free with `tools/batch-id.sh` on origin/main c471e961f. In
order:

1. **MA2:** the hold. Not beside `WorldMarket.java`, `WorldChain.java`,
   `WorldMarketCheck.java`, or a batch that moves the world's prices.
2. **MA3:** the books, after MA2. Not beside `buildings.json` rows 49-51,
   65-68 and 140, `sectors/Manufacturing.java`, `sectors/Automotive.java`,
   `ManufacturingCheck.java` or `CarCheck.java`.

Pending: MA1-K (the calls) and MA4 (waits on MA3's ensemble).

## Process

- **No process files changed**, so there is no line count to give.
- **Processes ended:** the first rule trio, started on the wrong hold, was
  stopped by its task; its six java processes were then ended by their ids.
- **Scratch:** `target/ma1` and the downloads' folder are this session's own,
  removed at the end.

## The dry run

Not yet run.
