# The manufacturing margin study's runs (MA1, docs/studies/manufacturing-margin.md)

Every run is `LongPlaytest` on main 0.9.138 (886bd92d), 2,400 months, through `run.sh`, its city in a
`TempDirs` folder, `-Dcbs.earth`/`-Dcbs.world` naming the PC's `data/earth-2.bin` and `data/world-1.bin`. Each
table in `out/` is `table.py` on the run's CSV (`out/NAME.md`, the years to m600, m1200, m1800 and m2400) and
`table.py ratios` (`out/NAME-ratios.md`, each tenth January). The CSVs are not kept (1-2 MB each); a run
remakes its own.

| run | what it is | how | tables |
|---|---|---|---|
| main s0, s1, s2 | main, seeds 0-2 (one world path) | `SEED=n sh run.sh target/ma1/main sN` | `out/main-sN.md`, `-ratios` |
| main h0 | main, seed 0, the Historical path | `SEED=0 EXTRA=-Dplaytest.world=HISTORICAL sh run.sh target/ma1/main h0` | `out/main-h0.md`, `-ratios` |
| full s0, s1, s2 | way (a): each works buys the rest at its industry's share (`proto.py 1`), the planner weighing it | `python -I proto.py 1 P; SEED=n PATCH=P sh run.sh target/ma1/full sN` | `out/full-sN.md`, `-ratios` |
| rule s0, s1, s2 | way (b), its purchases at its industry's (`proto.py rule`), with fabricated steel, machinery and the vehicles held to their chain (`hold.py`) | `python -I proto.py rule P; python -I hold.py P; SEED=n PATCH=P sh run.sh target/ma1/rule sN` | `out/rule-sN.md`, `-ratios` |
| hold s0, s1, s2 | the hold alone: fabricated steel, machinery and the vehicles held to their chain (`hold.py`), main's books | `python -I hold.py P; SEED=n PATCH=P sh run.sh target/ma1/hold sN` | `out/hold-sN.md`, `-ratios` |
| books s0, s1, s2 | way (b), the eight works' build cost at the census's assets a sale and the Machine Works' crew at a machine shop's (`proto.py rule P census crew`); no hold | `python -I proto.py rule P census crew; SEED=n PATCH=P sh run.sh target/ma1/books sN` | `out/books-sN.md`, `-ratios` |
| cap s0, s1, s2 | books and the hold | `python -I proto.py rule P census crew; python -I hold.py P; SEED=n PATCH=P sh run.sh target/ma1/cap sN` | `out/cap-sN.md`, `-ratios` |

Each run's log ended "All checks passed." (`LongPlaytest`'s audits; every month's money audited and every
reload matched). Its findings were none but on four runs, each a prototype's small city: "industry stands
farther out than the homes" at m2400 on rule seeds 0 and 1, books seed 1 and cap seed 1, and on cap seed 1
"households with no home" in 105 months of m526-701 (a village of 3-5 thousand people outgrowing its homes);
those four exited 1, as did each one-month warm-up. No money or reload finding on any run. P is a
folder for the patched files; `run.sh` compiles them over a snapshot of `target/classes` in each run's folder.

**A first hold, discarded.** `hold.py`'s first version chained a held good off its inputs' targets (each
root's path level) instead of their bands at the city's site. Steel is not held, and from m1200 it stood well
below its chain's target, so fabricated steel was held high: 1.31-1.70 of the literals' ratio over steel at
m1800-2400, as unheld. Its runs (way (a) with that hold) are not reported; `hold.py` reads each input's band.

**The census and BEA.** `census-2022-basic.csv` and `census-2022-materials.csv` are `census.py`'s output for
the industries in its CODES (US$ millions; each ratio over shipments); `bea-use-summary.csv` is `bea_use.py`'s
output for 332, 333 and 3361MV in 1997, 2007, 2017, 2022 and 2023 (nonzero rows), and `bea-use.md` is
`report.py` on it. `literal.md` is `literal.py`.
