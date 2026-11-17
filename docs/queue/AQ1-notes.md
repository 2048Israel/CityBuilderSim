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

## 7. The trace

**First, on e17ee67f9 (before the review), against `cbs-trace-main-ec481b4a`:** identical through m874, every row
after; the default town ended at 127,424 people against 646,914. A counted run (a copy of `Game` in target/ that
logs every sale, its trace identical to the branch's) showed why: from m875 two bleeds overlapped, Business
Services (six centres, 1,800 posts, on $26M of its OWN cash, no debt) and Manufacturing (one Machine Works a month,
m889-901, 2,249 posts, lent $228M-$419M, its count six interest-only months ahead of its operating losses): 4,049
posts, exactly the jobs lost, out of work 2.7% at m870 to 28.8% at m900. The same count on main's classes: main's
bust at m925-932 was Business Services alone (lent $849M over 74 months, losing $22M a month on its operations,
sold by the overdrawn rule in eight months); main's Manufacturing, losing up to $25M a month on $916M lent at m905,
sold nothing and recovered. So the bleed sold plant a wage cycle would have saved, and a sector's own cash was
taken for a lender. Both mended (section 8).

**Then, on the merge with 0.9.134 (7c607323c plus d00b70592's mends), against `cbs-trace-main-69a29638`:** all eight
files identical through m1199 and different from m1200 to the end (1,205 of 2,404 rows; the borrow file 47 rows
against 45). The playtest passes. The bleed fired 11 times in 2,405 months, all Business Services: two centres at
m1201-1204, then ten over m1298-1330 (4,500 posts to 1,500, lent $430M-$758M, operating losses $4M-$17M a month),
until the desk refused at m1331 and the quarter rule sold the last four (m1331-1334). The other distress sales are
the old rule's (49 overdrawn) and the spare-capacity rule's (75). Main's bust at m1343 loses 3,999 posts in ten
months. From the fork (m1200):

| | largest 10-month rise in out of work | most posts lost in 10 months | peak | months over 10% | people at the end |
|---|---|---|---|---|---|
| main 69a29638 | 18.7 pts (m1343) | 3,999 (m1343) | 30.6% (m1344) | 317 | 840,819 |
| AQ1 | 8.1 pts (m1392) | 1,530 (m1310) | 26.4% (m1334) | 411 | 423,905 |

On the brief's measure the bust is under half (the same holds on the four AUTO2 seeds, section 4); it is spread
over more months, so more months sit over 10%. The two towns stay level for 900 months (m1400 27,858 against
26,361; m2000 125,870 against 113,548; m2100 173,267 against 175,395); the gap opens over m2100-2400, 800 months after
the bleed's last sale, in a growth phase (main 175k to 824k, AQ1 173k to 415k) that includes an old-rule cliff on
AQ1's path at m2151-2155 (Heavy Industry, losses of interest only, overdrawn: seven Steel Mini-Mills in five months).
One run's end 1,200 months past a fork is a path, not a measure; the ensemble is.

## 8. The review (REV-AQ1-1, on e17ee67f9) and the counted playtest

SHIP on the money (the retire path, `settleRetirement`, booked once), on the branch's exclusivity, the three overrides
and the cause. Its findings and what was done:

1. **The streak was the net one.** The 24 months were `recordSectorResult`'s net-loss months (interest, property and
   sales tax in them) and only this month's operating income was read, so a sector losing only interest for two years
   sold on its first month of operating dip. The default playtest showed it: Manufacturing's count began at m866,
   its operating income turned negative only at m872, and it sold at m889 with 18 operating-loss months. Mended: a
   second streak, `BusinessInvestment.operatingLossMonths` (`recordOperatingResult(Sector)`, called beside
   `recordSectorResult` in `Game.runRetirement()`), saved as `DataSave.sectorOperatingLossMonths` and restored in the
   load path; SaveFileCheck asserts a reload carries it. The bleed reads it, and its step is counted on it. No format
   bump: a save without the field restarts the streak at 0, later and not wrong.
2. **The harness.** Section 5 lifted the floor before the dial, so no month mixed the two kinds; it now raises the
   floor, lifts it while interest keeps the net count climbing, and raises it again: 23 months with the net count
   at 28-50, the operating streak at 1-23 and the plant losing, nothing sold (e17ee67f9's rule sells in 7 of them, red
   when put back). Section 7 could not fail (the three overrides return null literally); it now also asks the rule of
   a sector that does not bleed, past the dial and not overdrawn (Freight, net count 24, cash $90k, owing $5,766k):
   nothing sold.
3. **Old metal (predates AQ1).** A scrapped plant's steel and copper sold to the Scrap Yards come back as other
   revenue (`Sector.sellOldMetal`, `CopperRefining`'s scrap purchases), inside operating income, which could reset a
   streak in the month the metal lands. The operating streak leaves it out (`OldMetal.SOLD` in the statement's other
   revenue); the net streak keeps it, as before.

The counted playtest (a copy of `Game` in target/ logging each sale, its trace identical to the branch's) also found
that "lent" was not tested: Business Services bled six centres from m875 on $26M of its own cash with no debt to the
desk. The bleed now needs the desk's principal above zero; a sector living on its own cash holds, as on main.

## 9. The ensemble (docs/ensemble/AQ1.md)

On 017f2ef19 (the merge with 0.9.134 and the mends; the classes the ensemble snapshots), against
`docs/ensemble/0.9.134.md`: 80 of 80 runs in 45.8 min, 23/26 criteria, hard 10/10 (M1 0 in every policy; F1 one
run with "households with no home" in AUTO2, ZERO_SLACK and SHOCK, as the baseline). Changed verdict: SHOCK K2 FAIL
to PASS (the twin's excess 0.16% against 0.48%). A1, A2 and R2 are red as on main. A3 PASS, 0 [0 to 4]. T2's AUTO2
median 1,886,738 against 1,949,996, its range 1,163,473-3,326,664 against 1,074,066-2,915,385.

The brief's measure on AUTO2's sixteen seeds (each run's CSV from m400; 0.9.134's are OX1's runs):

| | largest 10-month rise in out of work | seeds at 20 pts or more | months over 20%, all seeds | highest peak | mean people at the end |
|---|---|---|---|---|---|
| 0.9.134 | 9.7-26.1 pts | 9 | 264 | 38.7% | 1,984,804 |
| AQ1 | 6.3-15.3 pts | 0 | 41 | 27.1% | 2,007,726 |

Seed 7 is the same run to the person (the bleed never fired). The cliff the brief asked about is gone from the
ensemble; what is left over 20% is four seeds' 7-15 months (4, 6, 14, 15).

What remains of the old cliff is the quarter rule's own case, a sector overdrawn with no lender, and twice in the
default playtests that sector's plant was paying its running costs and its loss was interest alone (Manufacturing at
m1818 on e17ee67f9's path, Heavy Industry at m2151-2155 on the merge's, operating income +$571M a month on $283B
of principal). Whether such a sector should be sold at a quarter a month rather than restructured is the
distress rule's question for the debt it carries, not the bleed's; queued as QR1 (the ship file's ## queued row, its
recommended way and the other ways there).

## 10. The gate's dry run

`/tmp/cbs-gate-AQ1-Z3yUUeW5`, versioned (0.9.135), on d9bf1b2ea (the branch) and 69a29638 (main): 205 harnesses in
2,724 s, 194 passed, 11 failed, gate exit 1 on five lines in four harnesses no record named. The gate picked
CopperRefiningCheck and LandmassCheck from AQ1's diff and ran them in full; OX1's gate ran them short, so main's
record had never seen those sections. Each line traced on 69a29638's own classes, one JVM at a time:

| line | main 69a29638 | cause | named under |
|---|---|---|---|
| LandmassCheck 12, "home filled by a fixture" | the same, word for word | the month after the fill frees ground (12,178,110 sq ft against the half's 12,094,868); fill at m184, the bleed's first sale there m335 | FF1 (new) |
| CopperRefiningCheck (f), "the rule cut an order" | the same (0 months; []) | the Rotterdam run reaches no cut; the bleed never sells in it | FF1 |
| CopperRefiningCheck, "the child ran seed 1" | exit 0 | seed 1 on AQ1's path ends with "industry stands farther out than the homes" at m2400 (0.95 km against 0.71) | FB5 |
| PriceLevelCheck 10, the town "shown or left out alike" | 0 of 28,798 differ | both twins bleed the same Fabrication Shop at m73, then part at m99/m100 on main's capacity rule (Houses), 79 months past the town's nudge horizon (20, main's too), which section 10 does not trim to | HP1 |
| PopulationCheck, "...but it does fill, given a decade" | 1,108 of 1,285 (86%) | AQ1's path: 1,141 of 1,524 (75%), below | MR1 (new) |

PopulationCheck's played city, month by month (a probe in `target/`, never committed): the bleed sells one of two
Fabrication Shops at m73, 24 months of operating losses its lender covered, principal 1,000 (main's town keeps both,
its net streak at 72 by m121). The lone shop pays from m86; `Manufacturing.plan()` then orders a 276-post Fabrication
Works on credit (principal 538 to 3,341), which stands by m105 and loses again from m110: operating income -19 a month
and principal 4,126 at m121, against 1,489 in main's town. The target jumps 225 in the city's last 24 months, and the
city fills behind it at about 3.6 people a month. A gate in `Game.consider()` on the bleed's own predicate (lent, 24
operating-loss months) holds the order at m85 and lets it go at m86, the streak broken by the lone shop's profit
(measured: the same 1,141 of 1,524). An AQ1 gate only delays the order; the cause is a planner that weighs one works'
estimated profit and not a market whose plant lost money for 24 months.

The call (the orchestrator's, 2026-11-17; Jerus may overturn it): ★ pass the line by name and queue MR1
(Manufacturing's re-order after a bleed sale; done when PopulationCheck is green on AQ1's path and main's). The other
way: back to AQ1 for a planner mend before it ships, then a new trace, ensemble and dry run.

The five rows are under ## known-reds in the ship file; FF1 and MR1 are in its ## queued. Judged again on a copy of the
run's `suite.txt` (`target/checks/AQ1-dry-suite.txt`, `CBS_REJUDGE=--rejudge ./batch-check.sh --judge`): "known red
only - 11 harnesses", exit 3.
