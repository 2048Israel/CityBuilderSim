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
January GovernmentPlayer policy as the standard run. This is the next
twelve-month skip: there is no advisor look inside it, and the endpoint is
before the next look. It tests whether private investment needs the plot;
it does not claim a cash-rich advisor would never buy at a later look.
The untouched loaded
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

## Recommendation

★ Recommend a separate batch for the automated player's **growth-land funding**:
when a land-held, positive-profit private candidate and an empty building queue
show a growth hold, let the territory-buying move request the existing land
office's bounded funding flow, using the existing debt-service test and term,
instead of repeatedly trying a cash-only whole-offer purchase. Preserve the
land prices, crowding curve, private financing tests, builder premise and
cash-share rule for unfinanced purchases. Measure the funded refusal reasons
and compare both sites with the same player. ER1's one-plot counterfactual is
evidence for testing this path, not evidence to lower land prices or force a
private lender to approve every plant.

The other way is to retain the conservative cash-only automated land policy
and leave the player to use the land office's existing funding page. That
preserves the long hold as a consequence of the player's purchasing rule;
the run eventually recovers, but for centuries it lags the control. This is
a player/land-office decision, not a justification to add free territory or
tune farms in this diagnostic batch. One pending row carries the recommendation.


The orchestrator read fixture commit ee0d02e2 and accepted it directly on
2026-11-15; no further Codex review was requested for this harness-only batch.
The hold lives at **both sides of a boundary**: the model's `LandMarket`
prices and sells whole listed offers, while the automated player's
`LongPlaytest.keepGroundAhead()` and room move in `advise()` buy territory
from cash. The recommended repair is to the **advisor funding route**, with
market prices and whole-offer rules retained. The other way keeps that
cash-only advisor policy and leaves funding to the player. The pending row
names the owner and both ways explicitly.

While the read-only replay was running, R2d shipped main 32ec5a5d (0.9.118).
The orchestrator instructed ER1 to finish the replay on its original model,
then merge main and request the gate slot after KP2c. The long-city tables
therefore describe 8083aaf33 / 0.9.117, not a claimed replay of R2d's recipes.


## The control beside it

Both standard runs have exit 0 and no LongPlaytest findings. The generated
control takes 1,227 seconds and ends at the same m4005. The same player,
founding, seed, national history and policy settings are used; only the world
site differs. In m618–2025 the control grows 43.9 times, Rotterdam 2.87 times.
This is the current comparison, not AN2's sevenfold figure.

| month | Rotterdam people | Rotterdam offered jobs | default people | default offered jobs |
|---:|---:|---:|---:|---:|
| 618 | 11,556 | 6,362 | 6,349 | 3,399 |
| 769 | 30,890 | 12,575 | 13,241 | 5,755 |
| 1074 | 32,228 | 13,684 | 14,579 | 6,165 |
| 1321 | 36,655 | 15,744 | 32,151 | 13,591 |
| 1537 | 45,499 | 19,411 | 32,405 | 13,266 |
| 1778 | 33,211 | 13,575 | 73,610 | 32,513 |
| 2025 | 33,170 | 13,575 | 278,833 | 119,372 |
| 2252 | 33,518 | 13,575 | 503,912 | 215,663 |
| 2400 | 33,157 | 13,575 | 647,319 | 267,597 |
| 3000 | 64,289 | 29,408 | 1,627,412 | 661,677 |
| 4005 | 741,753 | 308,114 | 4,661,263 | 1,924,592 |

## The monthly decisions and the release

The read-only replay records model getters at every month-end, investor words
from `getLastInvestments()`, every listed offer, and the existing test player's
purchase/refusal counters. It adds no actions. Outputs are
`target/ER1/diagnose-rotterdam-{ER1,decisions,offers}.csv`; the temporary
observer and a LongPlaytest source copy with its one observer call are in
`target/ER1/probe-src/`. The ordinary eight CSVs are also written for a byte
comparison with the original run. A counter changed by an advisor look is
seen at the next month-end; the look itself occurs between months.

| recorded investor decision | m618–1778 (1,161 months) | m1746–2529 (784 months) |
|---|---:|---:|
| Manufacturing: no land | 1,106 | 784 |
| Agriculture: no land | 1,090 | 784 |
| Copper Refining: no land | 643 | 784 |
| Construction: 0.0 months of work queued | 921 | 784 |
| Real Estate: housing ahead of jobs | 238 | 784 |

Across the 784-month job hold, ground-ahead purchases stay at 17 and scored
room purchases at 15. Cash-share refusals rise from 44 to 59; owned territory
does not grow. These are observed calls and model decisions, not guessed
refusals from an empty queue. The private planners do not buy territory for
the city. The monthly AutoBuilder can annex for a needed works order, but
has no standalone growth-territory order. Without an advisor purchase, a
profitable private factory can remain a land refusal indefinitely.

The current crop readings underline the changed timeline:

| month | crops / the model's baked-food need | Agriculture footprint, ft² |
|---:|---:|---:|
| 618 | 46.92% | 600,000 |
| 769 | 14.59% | 600,000 |
| 1074 | 13.11% | 600,000 |
| 1321 | 10.09% | 600,000 |
| 1537 | 6.54% | 600,000 |
| 1778 | 11.15% | 600,000 |
| 2025 | 11.21% | 600,000 |
| 2252 | 10.93% | 600,000 |
| 2715 | 12.65% | 600,000 |
| 2737 | 10.57% | 600,000 |
| 3000 | 10.47% | 600,000 |

**What changes, with high confidence:** not a m1778–2025 farming release.
Nothing like AN2's 100% fields and fourfold jobs occurs then: both endpoints
have 13,575 offered jobs, roughly 11% crop self-sufficiency and the same Mixed Farm's
600,000 ft². The actual territory release is the look at m2715. Cash
has risen from D$3.850 billion at m2383 to D$1.359 trillion; the exchange rate
has strengthened from D$1,541.2025 to D$870.47834 per US$. Offer 90's old
US$85,336.874-thousand quote is unchanged, but now costs D$74.284 billion,
inside the D$135.941-billion ground-ahead cash budget. One offer is bought
by `keepGroundAhead()`, a second by the scored room move. The m2716 record
shows both counters advance, 0.2304 km² more owned ground and a renewed
private build. Further looks and needed automatic works buy more territory.
By m3000 jobs have reached 29,408; farms still occupy the same area. This is
capital-and-territory access followed by orders, not fields suddenly filling
all of the city's spare land.

**Why the population follows, high confidence in the rule and measured draw:**
`Migration.monthlyNet()` weights jobs by `JOB_WEIGHT`, using
`residentsPerJob(adultShare)`; housing is the smaller `HOME_WEIGHT` component.
After `BRAKE_FROM_MONTH`, joblessness above `HEALTHY_OUT_OF_WORK` also brakes
arrivals and sends surplus workers out. At m1778 the recorded target is
37,041.1, unemployment 11.31%, arrivals 66.900 and departures 129.009. At
m2025 it is 36,553.3, 11.04%, 81.840 and 138.156. No new jobs means no large
new jobs draw; births, deaths, age composition and migration still move the
population around the hold. The causal experiment opens jobs and raises the
population in the land-treated twin. A claim that *all* price, hunger and
population variation is caused by this single hold remains unproved.


## Pinned replay closed

The read-only replay exits 0 in 1,485 seconds at m4005. **All eight complete
ordinary CSVs are byte-identical** to the first Rotterdam run: month, borrow,
house, population, bank, bonds, construction and labour. The comparison,
byte counts and SHA-256 values are in
`target/ER1/trace-neutrality-final.json`. The borrow writer only flushes its
small file when the run closes; its earlier empty buffer was not a changed
trajectory. The observer adds no action and changes no model class.

No game code or balance data is changed by ER1. The default control remains
its original 8083aaf33 trace, and the 1,271 compiled model classes before and
after the ER1 harness rebuild are identical. There is no ER1 ensemble: the
batch has no model change, and the orchestrator instructed no ensemble.
The next check is the fresh-clone launcher gate on the merge with current
main. `tools/pick.sh` selects ALL because the harness registry changed;
no local full suite or local ALL pick has been run.


## Main merge and gate preparation

Main 32ec5a5d (R2d, 0.9.118) is merged at
fd429f976f357ee83e22bc0820ac899744c4c367. Conflicts were generated indexes
only; main's versions were taken and then regenerated on the combined source.
The merged `./batch-check.sh ER1Check` exits 0 (compile 52 seconds, harness,
maps and zero firm stale findings), log `target/ER1/merge-check.log`.
`tools/pick.sh` selects ALL 208 registered harnesses. Per the starter's
instruction, this is delegated to the launcher dry run, not a local ALL pick.

Against merged main, the only Java/resource differences are ER1Check.java and
AllChecks.java's single registry line; all game and balance files match main.
R2d changed recipes separately, so the pinned 0.9.117 long traces are not
claimed identical to 0.9.118. ER1 introduces no game-trace change on either
base. The ship file remains provisional until the coordinated dry run passes.
