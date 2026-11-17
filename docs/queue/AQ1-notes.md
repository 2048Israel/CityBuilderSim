# AQ1 - A3's one seed on GW1: the bust (2026-11-17)

The brief is AQ1's row in `QUEUE.md` (RN1-A folded in) and [GW1's notes](GW1-notes.md) section 5, "A3 in one seed".
Cut from main 48eb9b42 (0.9.133). Built by an Opus session (Codex out of plan to 2026-11-17 19:35). The probes are
kept beside the runs in `target/aq1/` (never committed): `head/` is the one run of the seed on this tree, `gw1r/` GW1's
run replayed, `headprobe/` and `seeds/` the month-by-month sector books read below.

## 1. Measured first: the bust is off main's path; its cause is on it

Written before anything was changed.

**How.** One run of AUTO2 seed 2 on 0.9.133 (`Ensemble AUTO2 2`, the data files named as main's ensemble names them,
with `-Densemble.extra` columns for every sector's posts and workers). Its first 40 columns equal, byte for byte, the
same seed in TQ1's merged ensemble (d0c9d575, `docs/ensemble/0.9.132.md`). GW1's measured run (GW1r, 49c2d522,
0.9.86, which ran with no world file) was replayed from its own class snapshot and driver: equal to its CSV in people,
index, yoy, rate, out of work, the treasury's cash, the bank's capital and deposits through month 700. A copy of each
driver that also writes the game's own log and every sector's books for a window of months read the cause.

**On main the bust does not happen where GW1 had it.** Seed 2 is another town: 14,660 people at month 600 and none
out of work (GW1: 20,534, 17%), and no spell of yoy under -10% from month 240 (A3 0; main's 16 seeds 0 [0 to 4]).

**Its cause does.** The same seed on 0.9.133, months 1091-1103: Business Services sells every one of its offices,
6,000 posts of the town's 17,420, and the town goes from 8.2% to 38.2% out of work in twelve months (people 41,300 at
1091, 39,900 at 1103, 31,700 at 1152). The policy rate was already 0 (from 1056), so prices fell only to -8.1% a year
(1104-1105) and A3 does not see it. Across main's ensemble (0.9.132, the same model), 8 of 16 AUTO2 seeds have their
unemployment rise 20 points or more within ten months in a town of 32,000-41,000:

| seed | month | out of work, ten months before -> then | people | the sector (replayed) |
|---|---|---|---|---|
| 0 | 952 | 9.6% -> 31.6% | 36,417 | Business Services, 5,100 posts -> 0 |
| 1 | 944 | 0.5% -> 20.8% | 32,799 | |
| 2 | 1,098 | 9.0% -> 30.2% | 40,786 | Business Services, 6,000 -> 0 |
| 10 | 551 | 3.2% -> 24.5% | 38,624 | Business Services, 5,100 -> 300 (and Materials 750 -> 250) |
| 12 | 1,132 | 8.9% -> 29.7% | 38,340 | |
| 13 | 1,030 | 10.0% -> 30.7% | 31,997 | Business Services, 4,500 -> 0 |
| 14 | 941 | 8.3% -> 31.2% | 33,921 | |
| 15 | 796 | 1.4% -> 21.6% | 32,893 | |

(Seeds 1, 3, 12 and 14 also have such a rise at months 276-280 in towns of about 2,000: seed 3's, replayed, is
Construction shedding 190 of its 270 posts with no work queued, the spare-capacity rule on a builder; not this.)

So the cause is not mended on main; GW1's town is only off main's path, and the same rule lays off a quarter to a
third of the workforce in a year in half of main's AUTO2 seeds, A3 blind to it because the rate is already at zero.

## 2. What laid off 30 points in ten months on GW1's build

GW1's seed 2, from the game's own log and the sectors' books (`target/aq1/gw1r/probe.log`):

| month | yoy | rate | out of work | posts in the town | the bank's capital | what happened |
|---|---|---|---|---|---|---|
| 600 | 1.9% | 2.8% | 17.3% | 10,760 | 19.4% | Automotive in its 80th month of losses, Manufacturing its 84th |
| 609 | 4.6% | 4.8% | 18.6% | 10,630 | 19.1% | yoy up from 1.4% at 607; the autopilot follows |
| 612 | 7.2% | 9.7% | 18.7% | 10,640 | 19.1% | Automotive's till empty: $9,504k of bills unpaid, lent as interim financing |
| 613 | 6.9% | 10.8% | 17.3% | 10,760 | 9.1% | the bank under its minimum, asks the city for D$6,701k |
| 614 | 6.2% | 10.4% | 22.2% | 10,120 | 2.8% | Manufacturing's till empty; the bank fails (the city puts in D$53,347k, D$51,075k of it the central bank's) |
| 615 | 5.7% | 9.3% | 28.6% | 9,271 | 12.0% | the city repudiates $102,146k of foreign debt; Retail bankrupt |
| 617 | 3.0% | 7.0% | 38.4% | 7,935 | 12.0% | the bank fails again (and at 618); Manufacturing bankrupt |
| 619 | -2.9% | 0 | 46.0% | 6,824 | 13.0% | |
| 621 | -10.1% | 0 | 50.1% | 6,228 | 11.6% | Materials bankrupt; A3's spell begins |

The posts that went: Automotive 4,500 -> 2,610, Manufacturing 2,754 -> 411, Materials 250 -> 0, Retail 709 -> 374.
Automotive had lost money every month since about 520 ("the parts cost more than the vehicle fetches"; $3,000k a
month), its debt $379M at 599 and $412M at 611, every loss lent; Manufacturing alike since about 516, $163M -> $190M.
When the rate rose and the bank failed, nobody would lend either of them, and the distress rule sold a quarter of
each one's biggest holding a month.

**WG2's brake is not the cause.** It slows arrivals past 6% out of work and stops them at 12%, sends the jobless past
6% away, and the staffing gate's draw reads the same brake (0.9.92, after GW1's 0.9.86). Nothing in it lays anyone
off. It would have kept the town from carrying 17% out of work into the bust, and it does not stop the bust on main
(section 1: seed 2's town sits at 8-10% out of work when its Business Services goes). The layoffs are the distress
rule's. The yoy rise at 607-612 that moved the rate was not read further: main's busts happen at a rate of zero.

## 3. The rule, and what it was meant to do

`Game.runRetirement()` gives every sector the spare-capacity rule (`BusinessInvestment.planRetirement()`: capacity
against a demand reading) and, when that sheds nothing, the distress rule (`planDistressRetirement()`): after
`DISTRESS_LOSS_MONTHS` (24) months of losses, and only once the sector is overdrawn (cash below zero, no lender), its
biggest holding times `MAX_RETIREMENT_FRACTION` (25%) a month. Automotive, Business Services and Manufacturing have
no demand reading (`retirementDemandAndCapacity()` returns null: "a price taker with no stock: it shrinks on
distress"), so the distress rule is the only shrink they have. And the bank's shortfall desk lends a sector its
losses until its debt passes `INSOLVENCY_TRIGGER` (1.5) times its assets (Jerus, 2026-09-25, "Stop at the default
point"; `BusinessDebtManager`, NOTHING PAST THE DEFAULT POINT). Twenty offices are a lot of collateral.

Main's seed 2 shows the two together. Business Services' payroll rose from $21.1M a month at 900 to $33.3M at 1090
(the average wage +52%, the index +45%) against revenue of $23.4M to $25.5M; it lost money from about 990, spent its
$42M of cash by 1020, and the desk lent it the rest: debt 0 at 1020, $104.5M at 1056, $409.3M at 1090. At 1091 the
desk refused, the till was empty, and the distress rule sold 1,200, 900, 600, 600, 600, 300, ... posts a month to none
at 1102, the town's out of work 8.2% to 38.2%.

Business Services' own design (its class javadoc, 2026-09-12) is otherwise: "the expansion test fails, and
twenty-four months of losses sheds plant"; "Boom and bleed, documented" (Nova Scotia's centres shed 2,000 of 15,693
jobs inside a decade); its screen says "past one hundred the offices start closing". The code closes nothing while a
lender covers the loss, for up to a hundred months, and then everything in a year.
