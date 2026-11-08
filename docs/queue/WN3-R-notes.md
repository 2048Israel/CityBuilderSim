# WN3-R - the band's monetary study ({next}, 2026-11-08)

The brief is WN3-R's row (WN3-notes section 3b, HD2). Lane C of the night line of 2026-11-08, after RE2, started on
lane A's slot once HB1 shipped (0.9.55). Measured on main as it stood (0.9.55, RE2 and HB1 in), both data files named.

## 0. In short

The premise holds on the band today, and no mechanism needs building. MonetaryCheck, PassThroughCheck and CurveCheck
come off the literals and pass on the band. R3 passes on the band, 2 seeds of 16 over (+1.21 and +1.88 points), median
-2.87. The R3 red in the record (`docs/ensemble/0.9.54.md`, 3 seeds over) was the literals' run: a worktree's ensemble
names no world file (RE2's ★4, queued as EN2). In every seed where the 15% rate runs hotter than its twin, on either
path, the cause is the same: the landlords' carry lift, as their mortgages renew at the held rate. The study keeps it (★1).

## 1. R3 on main today, both paths

RE2's two runs of the ensemble (`quizzical-dewdney`'s `target/ensemble/RE2` and `RE2-named`) and this batch's own 32
runs (HIGH and AUTO2 to m720, named, with the rent's parts as columns: `docs/queue/WN3-R/rent.py` has the line). The
named runs match RE2-named's figures seed for seed, so HB1 did not move them.

| path | R3 median | seeds over +1 point | R2 (unemployment over the twin) |
|---|---:|---|---:|
| the band (both files named), 0.9.55 | **-2.87** | **2: seeds 10 (+1.21), 14 (+1.88)** - PASS | +1.73, PASS |
| the literals (no world file, as 0.9.54.md), 0.9.54 | -0.97 | 3: seeds 6 (+1.13), 12 (+2.42), 15 (+2.13) - FAIL | +1.68, PASS |
| WN3's band, 0.8.75 (the row) | -1.12 | 3: seeds 3, 4, 10 | - |

## 2. The channel: the carry lift as the mortgages renew

The rent's target is `rentRequired()` (a new home's cost x `LANDLORD_YIELD`, 5.8%) x the scarcity multiple x the
carry lift (`max(1, rentBreakEven() / the blended market rent)`). The break-even carries the mortgages' interest
(0.7.11, Jerus: "Keep it"), which reprices at the day's rate as each ten-year term renews. At m720, the 15% city
over its twin (the band):

| seed | R3 gap | rent | a new home's cost | scarcity | carry lift | interest / carry, 15% / twin |
|---:|---:|---:|---:|---:|---:|---|
| 6 | +0.51 | 1.48 | 0.79 | 1.10 | 1.48 | 0.53 / 0.10 |
| 10 | **+1.21** | 1.21 | 0.72 | 0.94 | **1.80** | 0.65 / 0.31 |
| 14 | **+1.88** | 1.63 | 0.82 | 1.00 | **2.38** | 0.74 / 0.36 |
| 15 | +0.82 | 1.21 | 0.80 | 1.02 | 1.49 | 0.63 / 0.18 |
| the other 12 | -6.24 to -0.68 | 0.54 to 1.36 | 0.59 to 0.88 | 0.76 to 1.23 | 0.98 to 2.43 | |

The D$'s pull works. A new home costs 0.59-0.88 of the twin's (the stronger D$, the weaker wages), and scarcity
barely moves. Only the lift carries rent up. Its pull is a level, spent by about m660, while R3's window (m660-m720)
is where renewals at 15% land. On the literals the three red seeds show the same: interest 0.66-0.67 of the
landlords' rent at 15% against 0.12-0.24 in the twin, rent 1.18-1.78x the twin's.

**The lift is the price, not an edge case.** It binds in 99% of AUTO2's months m240-m720 (median 1.07) and in all of
HIGH's (median 1.21). Interest is 32% of the carry at the 2% target, 42% (median) at 15%. Taken out of the floor, the
floor would bind in 1% of months, and the 2% city's landlords would lose about 7% of their rent every month: RE1's
trap (RE2, seed 3), everywhere. So that way is out. Counting interest net of expected inflation (the user cost)
would lower every rent in the game. A cap at what tenants can pay (`TARGET_RENT_BURDEN`) would bind only in the hot
seeds. Both are candidates should R3 tip on the band; neither is needed today.

## 3. MonetaryCheck's dial, beside HD2's

The held runs (founding, months 25-60), reproduced to the digit by a scratch driver beside the harness:

| policy | 3% | 10% | 20% | 40% |
|---|---|---|---|---|
| the literals, 0.9.55 (as the harness ran on main) | 10.44% | 9.33% | 7.77% | 7.50% |
| **the band, 0.9.55 (the harness now)** | **7.22%** | **5.92%** | **5.25%** | **4.96%** |
| the band, 0.8.75 (WN3) | 4.23% | 6.55% | 5.96% | 5.75% |
| HD2's base (the literals, 0.8.76) | 10.75% | 9.71% | 9.04% | 8.72% |
| D$ a US$, the band 0.9.55 | 1.07 | 0.87 | 0.73 | 0.68 |

The rows held a month late differ by 0.000 / 0.052 / 0.027 / 0.003 points on the band (allowance 0.100). Main's
recorded MonetaryCheck red (the same line on the literals) does not occur here. The young city's floor does not bind
(break-even 0.39-0.40 of the market rent), so the 0.8.75 inversion, rent outrunning the D$ between 3% and 10%, is
gone. The batches between 0.8.75 and 0.9.55 removed it; which one was not isolated. HD2's coupon cannot move this
dial on the band either: the businesses hold 0.00-0.11 of a month's GDP abroad at m60, and the coupon on it is under
0.05% of a month's GDP.

PassThroughCheck on the band: at PASS_THROUGH the twins' D$ gap is 10.22% at month 1 and -0.14% ten years on, the
vaults equal after the sale-back, the price index's mean gap 0.25% against 0.81% (**HD2-P's line passes**).
CurveCheck's steep city on the band at month 39: 1-month 1.31%, 1-year 2.21%, 10-year 3.41%.

## 4. The window's long draws (WN3-B)

RE2-named's AUTO2 runs (2,400 months, the band), "on the window" as WN3 counted it (advances over 1,000): 9 of 16
seeds draw at some point. The longest are seed 12 (217 months, m97-m313) and seed 4 (121 months, m101-m221), the two
slowest towns at m300 (1,826 and 2,431 people against a median of 6,922). Both catch up: 82,233 and 35,876 at m1200,
643,627 and 488,902 at m2400 (median 633,768). Peak advances stay under 7,700 everywhere (0.8.75's seed 13: 444,694).
No young city is trapped, and nothing is built.

## 5. ★ Calls taken, on the recommendation

- **★1 No new mechanism: the rent's pass-through of financing is kept.** It is the user cost of a rented home
  (interest, upkeep and tax on what the home costs), the floor Jerus kept at 0.7.11, and the D$'s pull and the
  weaker demand outweigh it in the median by 2.87 points and in 14 seeds of 16. A mechanism built only to move two
  seeds would reprice every rent in the game, since the lift binds 99% of the time. Recommended: keep it.
- **★2 R3 is read on the band.** The game plays with the world file. The literals' R3 red is the ensemble's missing
  data files, which EN2 fixes with a new baseline. R3 sits at the limit there (2 of 2 allowed), so a later batch can
  tip it. If one does, section 2's candidates are where to start.
- **★3 FL1-E and HD2-P are settled by these measurements** (for the orchestrator to take out of Pending): R2 passes
  on both paths (+1.73 and +1.68 points against +0.50), and PassThroughCheck's "...and so does the price index" passes
  on the band (0.25% against 0.81%).

## 6. The checks

- The diff is the three harnesses' fixtures and comments (18 lines in, 30 out) and the study's script. No model,
  save or screen change, so the default playtest's trace is main's and **the ensemble is not rerun** (EN1). The 32
  study runs are information, not a baseline.
- `./batch-check.sh MonetaryCheck PassThroughCheck CurveCheck`, both files named: all three green.
- `--pick` on the merge with main aa56f24b, both files named (CurveCheck, MonetaryCheck, PassThroughCheck, TimingCheck): **exit 0**.

## 7. How it was done

Opus alone; there was nothing to slice. The study read RE2's CSVs, ran 32 ensemble runs with
`-Densemble.extra` columns for the rent's parts (7.6 minutes, three at a time beside the other lanes), and a scratch
driver calling `MonetaryCheck.founding()` and `step()` on both paths with `BuildingManager.buildersShortOn = false`,
as the harness's `main` sets it (without it the rows were not the harness's).
