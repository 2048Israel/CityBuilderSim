# ER1 — What holds Rotterdam's Earth city at twelve thousand for a century

## Contract and reproduction

Diagnosis only, from [AN2's finding and ★1](AN2-notes.md). No model code,
save slot or balance file is changed. The branch starts at main 8083aaf33
(0.9.117). `docs/notes/known-reds.txt` has no entry naming ER1 at that commit.
The first `./batch-check.sh --fast` passed: compile, maps, zero firm stale
findings and no tree changes.

★ Player comparison (orchestrator's instruction, 2026-11-15): run today's
standalone defaults first, since that is the game a fix would change. AW1
changed the standalone player's default to automatic works with cash building
past the debt limit. AN2's then-default player was the scored advisor. If the
current default no longer stalls, repeat with `-Dplaytest.autobuild=false`,
and name that player when describing the result. Each Rotterdam city has a
generated-world default beside it using the same player. This avoids calling
a player-policy difference a demonstrated model repair.

The standard commands use `LongPlaytest --earth rotterdam` and `LongPlaytest`,
`-Dplaytest.months=4000`, the standard founding, world path and seed, with each
of the eight standard trace files enabled. Both data paths are explicit:
`C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin`
and the neighbouring `world-1.bin`. The cities run sequentially in a detached
Git Bash process (launcher PID 3516). They use LongPlaytest's own TempDirs;
none opens Jerus's saved Rotterdam. Raw logs and CSVs are in `target/ER1/`.

The report's `fields` figure is crop self-sufficiency, capped at 100%, from
`Agriculture.getSelfSufficiency(Game)`, not the fraction of land occupied by
farms. Its `land` figure is `LandManager.getUtilisation()`: the clamped used
share of linked, buildable ground. Keeping the two denominators distinct is
necessary before treating a third of the crops as a third of the ground.

## Today's run: the plateau moved

The current-default Rotterdam run finishes at m4005, exit 0, in 1,525 seconds;
LongPlaytest's final hands-on moves account for the overshoot past its 4,000
month target. It has 741,753 people and 308,114 jobs. The standard log is
`target/ER1/rotterdam.log`; the eight ordinary CSVs have the prefix
`target/ER1/rotterdam-`. The run was of model commit 8083aaf33 (the notes-only
commit 2de76b16 was made while it ran; no class was rebuilt during it).

The exact old timeline does **not** reproduce: Rotterdam is already 30,890
people at m769, its land stays full instead of falling to 54%, and it does
not reach 100% crop self-sufficiency in the m1778–2025 interval. A growth
hold still occurs, at a higher population and later. Jobs are exactly 13,575
through m1778, m2025, m2252 and m2400; recovery follows m2700. Per the
orchestrator's call, this is the current-default hold to diagnose. The
waiting scored-advisor launcher was stopped before it started a city; its
process tree alone was ended. No scored-advisor city has been run.

The run's own scratch reload checkpoints were copied read-only before their
JVM's TempDirs cleanup, under `target/ER1/snapshots/`. These are this run's
scratch cities, not the user's slots. Their HistorySave records each month:
land utilisation, jobs, population, hunger, hunger priced out, baskets asked
and handed, construction capacity, exchange rate and price index. This is
recorded history, not a flow reconstructed from closing stocks. Eight standard
trace CSVs independently give the population, jobs and construction queue.

| month | people | jobs | land used | hunger | hunger priced out | baskets delivered / asked |
|---:|---:|---:|---:|---:|---:|---:|
| 618 | 11,556 | 6,362 | 98.68% | 9.92% | 9.91% | 99.99% |
| 769 | 30,890 | 12,575 | 99.91% | 20.10% | 20.10% | 100.00% |
| 1074 | 32,228 | 13,684 | 100.00% | 18.20% | 18.20% | 100.00% |
| 1321 | 36,655 | 15,744 | 99.86% | 23.83% | 23.82% | 100.00% |
| 1537 | 45,499 | 19,411 | 99.99% | 26.98% | 26.98% | 100.00% |
| 1778 | 33,211 | 13,575 | 99.99% | 29.46% | 29.46% | 99.99% |
| 2025 | 33,170 | 13,575 | 99.99% | 34.61% | 34.61% | 100.00% |
| 2252 | 33,518 | 13,575 | 99.99% | 38.52% | 38.52% | 99.99% |
| 2400 | 33,157 | 13,575 | 99.99% | 33.18% | 33.18% | 100.00% |
| 2700 | 31,315 | 12,883 | 99.91% | 22.80% | 22.79% | 100.00% |
| 2800 | 40,459 | 16,555 | 99.97% | 23.26% | 23.25% | 100.00% |
| 3000 | 64,289 | 29,408 | 99.61% | 13.34% | 13.34% | 100.00% |

**High confidence, directly recorded:** at m1947 and m2383 the city owns
4.8636 km² and has only 264 m² free. Agriculture and Manufacturing record
`Holding: no land`; Construction records `Holding: 0.0 months of work
queued`; Real Estate records `Holding: housing ahead of jobs (14,340 now,
0 coming)`. The ordinary construction trace independently shows 16 depots,
7,200 points of nameplate capacity and zero points remaining on site at
m1778, m2025 and m2400. Builders have capacity; they have no new sites to
work on. They have not disappeared.

**High confidence, the buyer's constraint:** at m2383 the cheapest standing
whole offer is US$81,366.998 thousand. At the recorded D$1,541.2025 per US$
that is about D$125.4 billion, against D$3.850 billion of treasury cash.
The vault is only US$340.37 thousand, from the *named* `reservesUsd` history
series; it cannot cover that offer either. During investigation a raw saved
array's `forgiven` field was briefly misread as reserves; that inference is
withdrawn. All vault conclusions here use the named history series.

The land mechanism's code is `LongPlaytest.keepGroundAhead()` (cash budget
`GROUND_AHEAD_CASH_SHARE`, 0.10), its scored room-to-grow move in `advise()`
(`Game.buyLandParcel()` directly, no funding request), and
`Game.bestOffer(LandNeed.room())` (affordable offers first, then the best
standing offer even when none is affordable). `Game.canAffordParcel()` and
`landPayable()` require the converted part to fit treasury cash.
`BusinessInvestment.plotsAvailableFor()` / `orderSize()` allow zero when
there is no plot, and `Game.buildFor()` refuses land before it raises private
credit. A rich private till is not permission to annex city territory.

Hunger in these checkpoints is overwhelmingly inability to pay for food,
not the shops failing to import the baskets asked for. That is a distinction
the model records itself (`hungerPricedOut`, `basketsAsked`, `basketsHanded`).
It does not by itself establish which price or income shock caused each
household's loss of purchasing power. Nor does a crop self-sufficiency reading
prove that the rest of the land is unused.
