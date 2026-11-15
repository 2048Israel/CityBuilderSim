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

The monthly traces put the longest unchanged job-capacity spell at
**m1746–2529 inclusive: 784 months at 13,575 jobs**. In m1778–2400 inclusive,
all 623 months have land utilisation at least 99% and zero construction
points remaining on site. In m618–1778, 953 of 1,161 months have land use at
least 99%, and 951 have an empty queue. These are counts of recorded states;
the individual investor refusal counts are measured separately.

The standing building stock at m2383 reconciles exactly to the saved standing
footprint, 52,188,512 ft². Only 600,000 ft² of that is Agriculture (1.15%);
Real Estate is 14,630,000 ft² (28.0%), and Manufacturing 6,500,000 ft² (12.5%).
The balance is mainly city works and the other businesses. These are saved
building quantities times the catalog's own footprints, not crop output
converted to area. The allocated ledger is slightly greater than the standing
footprint (the manual house retirements leave a small difference; AW2 already
owns allocation reconciliation). Neither figure makes the fields most of the
city's land. The recorded 264 m² free remains smaller than the named farms'
and factories' requested plots.

The price feedback is explicit in `LandMarket.update()`:
`peoplePerKm2()` drives `crowdingPremium()` (`CROWDING_MIDPOINT`,
`CROWDING_STEEPNESS`, `CROWDING_CEILING`), and inside land adds
`scarcityMultiplier()`. Offers already listed retain their listed dollar
price; `update()` fills missing places, it does not replace an unaffordable
standing offer with a smaller one. The buyer pays that dollar quote at the
current exchange rate. The shelves are not refusing to list; the buyer cannot
pay the whole offers it is looking at.

**Limit on the causal claim:** a `planMaker()` land refusal follows its
forecast, staffing and positive estimated-profit tests, but precedes the
whole financing test in `Game.consider()`. It proves a desired, positive-profit
candidate has no plot; it does not prove a loan would pass after annexation.
Other sectors independently say output ahead, insufficient licences, no
deposit or an unprofitable project. `Construction.plan()` declines another
depot when queue months are below
`BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING`; that is a response to
the empty queue, not proof that adding builders would make a plot available.

## The cause tested in the played state

A short scratch-only counterfactual uses the saved m2383 state, copied into
three independent `GameFiles.scratch()` folders and loaded through the real
loader. It advances twelve months with the automatic works and the same
January GovernmentPlayer policy as the standard run. The untouched loaded
city's m2383 and m2395 population and job counts match the original trace
(33,042 / 13,575 and 32,936 / 13,575). Log:
`target/ER1/counterfact.log`; temporary driver:
`target/ER1/probe-src/ER1Counterfact.java`.

| m2395, after twelve months | no intervention | cash only | same cash, and one ground-ahead purchase |
|---|---:|---:|---:|
| population | 32,936 | 32,936 | 33,833 |
| offered jobs | 13,575 | 13,575 | 13,853 |
| owned ft² | 52,351,354.703 | 52,351,354.703 | 53,591,357.183 |
| free ft² | 2,842.703 | 2,842.703 | 92,845.183 |

Both treated cities receive the same treasury cash, sized from the selected
offer's local price / `GROUND_AHEAD_CASH_SHARE`, with `BuildAdvice.SLACK`.
This is an explicit experimental endowment, not a claim that the played city
already held it. Only the third calls `keepGroundAhead()` at m2383. It buys
offer 90, 0.1152 km², for US$85,336.874 thousand. At m2384 Manufacturing says
`Built 1 Fabrication Works - steel worth more shaped than shipped`, and the
queue holds 276 future jobs. The factory is finished by m2385. The cash-only
twin annexes nothing and creates no job order. Agriculture remains land-held
in the land-treated city: one small annexation is not a universal cure.

**High confidence at this checkpoint:** the absence of a plot is an active
cause, not merely correlated with weak growth or a missing builder. Restoring
cash alone does not place an order because the automatic works buys land for
its own needed works; it has no independent remit to buy growth land. The
standalone advisor must perform that purchase, and its territory-buying moves
use cash. The experiment proves a real private candidate passes finance once
that one plot is present. It does not prove the entire 784-month counterfactual
path or attribute all hunger to the land hold.

## The short suite fixture

`ER1Check`, registered in one line of `AllChecks.HARNESSES`, consumes a fresh
scratch city's free ground before its first economic month, sets the treasury
cash to zero, and funds a private Convenience Store buyer. Over the model's
`PLANNING_HORIZON` months, every real `buildFor()` call is refused, every
`keepGroundAhead()` look is counted cash-short, and neither physical job
capacity nor jobs on site can increase. Then treasury cash is restored from
the actual offer quote, `GROUND_AHEAD_CASH_SHARE` and `BuildAdvice.SLACK`.
The same land rule buys ground, and the same private order is accepted with
exactly the template's own future jobs. No rate, capacity or footprint is
copied as a literal into its assertions.

★ Keep this fixture in the ordinary gate tier: measured 2.059 seconds in the
normal named harness run (`target/checks/ER1Check.log`, copied in the batch
records under `target/ER1/`). The first draft's two failures were measurement
errors, not a relaxed hold premise: a builder recalled 14 existing posts for
one month (35 offered became 49, while physical capacity stayed 49), and
`BusinessInvestment`'s copied land snapshot is refreshed for its next planning
round, not immediately on annexation. The final test uses
`getTotalJobsAtEveryPost()` for physical job capacity and the actual
`LandManager.canAllocate()` gate for the immediate build action. The forced
full ground, cash shortage, funded private order and all-month requirement
are retained.

The final named `./batch-check.sh ER1Check` passes (exit 0): compile, one
harness, maps and zero firm stale findings. The direct diagnostic run passes
in 2.094 seconds; the normal runner measures 2.059 seconds. The 1,271 compiled
model classes hashed before and after the rebuild are byte-identical. The
only Java changes are the new check and its registry entry.
