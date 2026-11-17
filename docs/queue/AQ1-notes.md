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

## 4. The mend: a price taker bleeds while it is lent (★)

`BusinessInvestment.planDistressRetirement()` now lets a sector whose `Sector.bleedsWhileLent()` is true (Business
Services, Automotive, Manufacturing: the three whose `retirementDemandAndCapacity()` always returns null) sell plant
while the desk still lends it, once it has lost money `DISTRESS_LOSS_MONTHS` running and only while its plant is
short of its own running costs (`statement().operatingIncome < 0`; a loss that is only interest holds, as before).
The pace is the new dial `BLEED_MONTHS` (= `DISTRESS_LOSS_MONTHS`, the losses' runway again): a twenty-fourth of its
biggest holding a month in whole buildings; below twenty-four held, one building on each count of the losses that is
a multiple of ceil(24 / held) past the dial, the step read off the holding that month. Eight held go at counts 24,
28, 32, 34, 36, 40, 48 and 72. No saved field: the step is read off the saved loss counter and the holding, so a
reload keeps it; no format bump (nothing an old save holds reads differently). Overdrawn with no lender, every
sector sells `MAX_RETIREMENT_FRACTION` a month as before.

Measured on main's four Business Services seeds to month 1300 (the ensemble driver, one run each; main, then the
first prototype at one building a month while lent, then this):

| seed | largest 10-month rise from m400 | peak out of work | months over 20% | people at 1300 |
|---|---|---|---|---|
| 0 | 27.4 / 18.8 / 12.0 pts | 36.6 / 22.3 / 17.0% | 37 / 6 / 0 | 38,849 / 38,720 / 40,589 |
| 2 | 26.9 / 15.0 / 9.6 pts | 38.2 / 29.1 / 13.9% | 35 / 12 / 0 | 38,369 / 36,178 / 39,851 |
| 10 | 27.3 / 12.3 / 14.2 pts | 32.0 / 18.7 / 19.7% | 28 / 0 / 0 | 116,060 / 670,667 / 458,213 |
| 13 | 25.1 / 20.5 / 9.0 pts | 33.6 / 27.2 / 19.9% | 26 / 24 / 0 | 40,517 / 84,396 / 42,394 |

A3 is 0 in every run. Seed 10's 14.2 points (m612-622) were read month by month: its Business Services held nine
offices, sold one at losses 24, 30, 36 and 40 while lent (cash $62k-$996k a month, principal about $21M), and at 41
the desk refused: each office sold took its book value off the assets and paid little off the debt, so the debt
passed the default point sooner. The quarter rule then sold the last five, one a month (1,500 posts), alongside
Construction's own shed of 948 posts at m616. The refusal still comes; it comes to a smaller sector.

The other ways, not taken:
- Keep main's rule: the cliff stays (8 of 16 seeds on 0.9.132's AUTO2 lose 25-27 points in ten months).
- One building a month while lent (the first prototype): seed 13 still spent 24 months over 20%, seed 2 twelve.
- Slow the overdrawn rule instead: a firm refused credit with no money is the case the rule was written for ("a firm
  with no money and no lender lays people off"), and it leaves the hundred months of lent losses as they were.
- Stop the desk lending operating losses earlier: the bank's rule, every sector's, a larger change than this batch.
- A strict step counted from the last sale: the same average pace, and a saved field per sector to carry it.

## 5. RN1-A: A3's one-month red, accepted

RN1 (0.9.63, 2026-11-09) shipped with A3 red by one month in one seed of sixteen (AUTO2 seed 4, a 12-month spell of
yoy under -10% at m970-981 against main's 11), as its notes recommended; its changelog says so, and QUEUE folded
RN1-A into this batch "as recommended". Confirmed on 0.9.132's ensemble (TQ1's baseline, `docs/ensemble/0.9.132.md`):
A3 PASS, 0 [0 to 4]; seed 4's lowest yoy is -8.6% at m740 with the policy rate at zero, no month of it under -10%;
the longest spell in any seed is seed 15's 4 months (m1385-1388). The zero bound itself is common (189 to 481
months at a 0% policy rate per seed), so RN1-A's question, the deflation at the zero bound, is not a red on main; this
batch records it and builds nothing for it.

## 6. The harness: DistressBleedCheck

A town of eight Contact Centres (the fixture of BusinessServicesCheck's "what closes it") and the minimum wage opened
and lifted to 1.8 times its founding floor: Business Services' operations lose $48k-$72k a month from month 2, the
desk lends it (cash never below $6k, debt $597k to $2,152k) and the count reaches 24 at month 25. Three times the
floor loses $250k a month and the desk refuses at about the dial, which is the quarter rule's case, not this one.
Seven sections: the fixture; nothing sold under the dial; a centre sold the month the count reaches it while lent,
half gone by the dial plus BLEED_MONTHS; never faster than max(1, held / BLEED_MONTHS) and only on the counts the
dial names; a loss that is only interest holds (a second town, the floor lifted back at a count of 12); overdrawn,
the quarter (the rule asked with an overdrawn balance as its argument); only a sector with no demand reading bleeds.
On this branch: exit 0 in 7 s, sales at counts 24, 28, 32, 34 and 36 with 8, 7, 6, 5 and 4 held.

Red on main, run once by hand (main's classes, this harness's class first on the path):

    the month the count reached 24: 0 centre(s) sold, cash $130k, operating income $-49k, debt $2,221k
    a centre is sold in the month the count reaches the dial       FAIL
    0 of the 8 centres sold in months the lender still covered; half gone at a count of never (the dial's end: 48)
    by the dial plus BLEED_MONTHS months of losses at least half the centres are gone, sold while lent FAIL
    0 sales over 25 months of lent losses, at counts none with none held before them
    a lent sale falls on a count the dial names, and a count it names with the operations losing money passes none FAIL
    the first two sales are at least the dial's step apart for the holding at the first FAIL
    Exception in thread "main" java.lang.NoSuchMethodError: 'boolean ham.citybuildersim.Sector.bleedsWhileLent()'

(section 7 calls the method main lacks, so it is last). Sections 1, 2, 5 and 6 pass on main.

## 7. The trace: it moves, and the default town is worse off

The default playtest (2,405 months, the data files named) with `-Dplaytest.trace` on e17ee67f9 against
`cbs-trace-main-ec481b4a`: all eight files are identical to the byte through month 874 and differ from 875 to the end
(1,531 of 2,404 rows each; the borrow file 37 rows against main's 45). The playtest itself passes, every month audited.
At 875 a 300-post office goes (jobs 15,916 to 15,616, out of work 3.6% to 5.2%): the bleed firing. Then the town
loses 3,900 posts in 24 months (out of work 2.7% at 870, 28.8% at 900), where main's own bust comes at 924-936 (11%
to 26%). Over the run, from m400:

| | people at 2400 | mean out of work | peak | months over 10% | largest 10-month rise |
|---|---|---|---|---|---|
| main | 646,914 | 5.6% | 26.3% (m933) | 578 | 15.3 pts (m933) |
| AQ1 e17ee67f9 | 127,424 | 7.9% | 29.7% (m1820) | 1,057 | 18.9 pts (m1825) |

One run 1,500 months past a fork is one path, not a verdict, but this one is worse, and the 3,900 posts at 876-900
are faster than a twenty-fourth of one sector's plant a month. Being read before the ensemble (what sold, by which
rule, in which month).
