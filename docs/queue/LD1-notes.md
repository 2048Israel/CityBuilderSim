# LD1 - a business short of workers bids the wage up (0.8.91, 2026-10-31)

Jerus, 2026-10-31, from his own city: "i had available mines but the mining
industry wasnt building cause it wouldnt have enough workers... and new people
didnt want to come cause the wage was trash, perhaps a fix would be that if the
reason is not enough workers and the business can... then it raises its wage?"
The brief is `docs/queue/LD1.md`. A model batch: Opus, with Sonnet slices.

## 1. The loop, measured on main

**The probe** (`docs/queue/LD1/`, on the ensemble's driver, as XS1's): after
each month, every sector the staffing gate held, classified three ways.
- **The crews'**: the building would pass with the builders' laid-off crews
  counted spare. That is XS1's trap, which IC1 frees.
- **A shortage that pays**: it fails even with the crews spare, and it would
  carry the interest on its whole cost at prime at today's wage. That is
  `consider()`'s own test, `carriesAtPrime()`.
- **A shortage that does not pay**: it would be refused on money anyway.

**The sixteen AUTO2 seeds on main** (0.8.89; seed 0 is the default
playtest's founding): 43,775 holds in 38,400 months.

| class | holds | share |
|---|---|---|
| the crews' (IC1's) | 22,133 | 50% |
| a shortage that pays | 12,466 | 28% |
| a shortage that does not pay | 9,176 | 21% |

Seed 11 is the outlier: 11,810 holds, 84% the crews'. In the young cities
(m0-300) there are 16,136 holds: 64% the crews', 28% a shortage that pays.

**The shortage that pays is a skilled band's.** 10,275 of its 12,466 holds
are short of COLLEGE and UNIVERSITY workers alone, and 1,678 more of NONE
beside a skilled band. NONE alone is short in 509; DIPLOMA never alone. Two
buildings make most of it:
- **The Machine Works** (34 college and 4 university posts of 173): 4,801 hold
  months. Without a spare college worker it is 135/173 = 78% staffable, under
  the 80%.
- **The bank's branch** (13 college and 5 university of 29): 5,353, all after
  m1,450. In a city of 170,000-360,000, against 8,000 college workers.

No mine is held in any seed. The autopilot's cities rarely get an iron field
(BuildAdvice: "found none listed" every time), so Jerus's mine is his own
city's case of the same loop.

**The wage sits low and the band's migrants stay away.** At the months of these
holds:
- the college premium is 1.02-1.2x base in 87% of them, and under 1.02x in 4%;
- the university premium is under 1.02x in 38%;
- 2.9% of arrivals are skilled.

Migrants still arrive in nearly all of these months (the migration gap is
positive in 12,037 of the 12,466), but as DIPLOMA: that band's pull is 1
whatever it pays, while every other band is bought by its premium
(`Migration.reach()`).

**The young city, where it binds.** The Machine Works' longest hold in the
first 800 months of each seed:

| seed | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | 14 | 15 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| months | 255 | 137 | 108 | 60 | 117 | 112 | 60 | 128 | 304 | 192 | 210 | 305 | 252 | 52 | 91 | 300 |
| from | m116 | m83 | m111 | m153 | m154 | m91 | m159 | m143 | m72 | m97 | m72 | m71 | m119 | m84 | m87 | m76 |

Seed 4 at m100-200, for example:
- the city has 48-51 college workers against 58-65 college posts;
- the college premium is 1.06-1.07x;
- 0.2-0.3 skilled migrants arrive a month, out of 10-23;
- the affordability pull is 0.75-0.89.

Here the building's 34 college posts are two thirds of the band, and the wage
never sees them.

**Where it does not bind.** The bank's branch in a big city asks 13 college
posts of a band of 8,000 workers. That band stands 30% oversubscribed
(posts/workers about 1.3) at 1.15x, for 900 months. The bid adds a rounding
error there. That band's own shortage is the curve's (`ELASTICITY` .5) and the
world's ceiling on graduates, not this loop (§6).

## 2. The rule

**The hold carries its test.** A planner the gate refuses for want of workers
returns `Decision.unstaffed(sector, staffing, reason)`. The reason is the same
text as before. `Sector.Staffing` now knows its sector, building, count, and
posts and spare workers per band. Every planner that asks the gate does this,
the materials chains through `SpreadPlanner` included.

**Game sorts each hold as the month plans** (`consider()` -> `noteStaffingHold()`):
- **The builders' laid-off crews.** It would pass with them counted spare
  (`Sector.staffingWithCrewsSpare()`). It asks nothing: no shortage a wage
  answers (§5).
- **It does not pay.** It would not carry the interest on its whole cost at
  prime, and its record's surcharge, even at today's wage. That is consider()'s
  own test, at the best rate the sector could borrow at. It asks nothing.
- **Otherwise it asks** for its posts in the bands it is short in, with how far
  their wage could rise and it still pay: `1 + margin / (the bill of those
  posts)`.

**The market prices the asks next month** (`WantedPosts.settle()`, then
`repriceLabour()`). A band's refused posts are added to its posts, and the
fill's supply is taken against them, as if offered. The wage then moves at the
market's own pace (`ADJUST_RATE`) and cap (`MAX_MULTIPLE`). The gate's own test
and Migration's opportunity still read only the posts that exist.

**The bounds:**
- **As far as the business can pay** (his "and the business can"). A band's
  refused posts stop at the tightness where the best-paying building that wants
  them would no longer carry its interest: `tightnessAt(premium x payFactor)`.
- **Never more than the band's own posts again** (`MOST_OF_POSTS` = 1). The
  posts the wage is priced on are at most doubled. The multiple goes as the
  root of the tightness, so the bid lifts a band's multiple at most sqrt(2) =
  1.41 times: one business's wish never doubles a wage. `WantedPostsCheck` 4
  prints 1.414 at every tightness from the floor up.
  - It is the band's posts, not its workers, because the gate keeps a plant out
    of a village on purpose ("it puts the first one in a city of three or four
    thousand rather than a village", `Sector.MIN_STAFFABLE_TO_ORDER`). A
    village has few posts, so it bids little.
  - Capped at the band's workers, as first built, it bid the founding village's
    unskilled wage to 1.87x by m10 (main: 0.92x), and the young city was 15%
    smaller at m300 (§4).
- **It decays by being rebuilt.** Each month's asks replace the last. A building
  the gate lets through, or a sector that stops asking, bids nothing next month,
  and the wage walks back at `ADJUST_RATE`.

**Saved.** `DataSave.wantedPosts`: the asks are made as a month plans and
priced in the next. A save without it reads as none, so there is no format bump.

**On the screen.** The People page's skill ladder: its caption names the posts
priced beyond the open ones when there are any, and its legend says what
"tight" is priced on.

## 3. What it does: the sixteen AUTO2 seeds, main against LD1

| | m300 | m600 | m900 | m1,200 | end | past 40,000 | Machine Works' longest wait |
|---|---|---|---|---|---|---|---|
| main | 3,520 | 11,550 | 32,015 | 68,998 | 347,140 [68,654-383,997] | m1,066 [881-2,256] | 132 [52-305] |
| LD1 | 3,965 | 11,764 | 34,039 | 64,506 | 335,916 [290,726-398,232] | m1,033 [857-1,141] | 40 [24-126] |

Medians, with [least-most]. LD1's figures are its committed model, byte for
byte (seed 1's run checked against the variant's). With the bid switched off,
LD1's tree is main's to the byte over 2,400 months (seeds 0 and 4): every
change below is the bid's.

**The loop opens.**
- The Machine Works' longest wait in the first 800 months falls from a median
  of 132 months to 40. Twelve seeds wait under 60; seed 11 waits 82, and seeds 2 and 5
  120-126.
- The gate's shortage holds that pay fall from 12,466 to 8,949, and all holds
  from 43,775 to 31,643.
- At those holds the college premium is 1.2x or more in 26% of the months
  (main: 10%), and 4.1% of arrivals are skilled (2.9%).
- In m0-600, 4.4% of all arrivals are skilled against 3.6%.

**The city is earlier.**
- The median city is 13% larger at m300 and 6% larger at m900.
- It passes 40,000 33 months sooner at the median, and no seed is later than
  m1,141.

**The end of the run is 3% lower at the median** (335,916 against 347,140).
Its least seed ends at 290,726, where main's ended at 68,654.

**Seed 11** passes 40,000 at m973 and ends at 293,380 (main: m2,256 and
68,654). That is a changed path, not a cure: its trap is XS1's (§5).

**Seed 4** (RE1's) runs a little behind main's:

| | m900 | m1,200 | past 40,000 | end |
|---|---|---|---|---|
| main | 36,856 | 78,331 | m979 | 381,539 |
| LD1 | 34,635 | 72,884 | m995 | 346,167 |

**Seed by seed** (population; past 40,000; the Machine Works' longest wait):

| seed | main m300 | LD1 m300 | main m900 | LD1 m900 | main end | LD1 end | main 40k | LD1 40k | main MW | LD1 MW |
|---|---|---|---|---|---|---|---|---|---|---|
| 0 | 3,042 | 2,773 | 32,901 | 33,084 | 358,645 | 349,562 | m1,083 | m1,072 | 255 | 24 |
| 1 | 5,971 | 5,398 | 22,570 | 31,744 | 340,223 | 322,939 | m1,087 | m1,079 | 137 | 26 |
| 2 | 3,900 | 3,917 | 40,870 | 21,292 | 368,877 | 366,014 | m881 | m1,141 | 108 | 120 |
| 3 | 5,589 | 3,081 | 22,719 | 30,666 | 340,072 | 323,826 | m1,116 | m1,081 | 60 | 35 |
| 4 | 3,823 | 5,059 | 36,856 | 34,635 | 381,539 | 346,167 | m979 | m995 | 117 | 51 |
| 5 | 3,935 | 4,013 | 34,832 | 42,109 | 306,964 | 326,540 | m975 | m860 | 112 | 126 |
| 6 | 3,677 | 6,543 | 36,306 | 36,104 | 368,509 | 315,869 | m979 | m984 | 60 | 51 |
| 7 | 4,736 | 5,977 | 23,854 | 38,182 | 247,992 | 371,308 | m1,104 | m980 | 128 | 60 |
| 8 | 2,949 | 6,190 | 31,129 | 40,130 | 331,020 | 321,169 | m1,059 | m884 | 304 | 38 |
| 9 | 3,271 | 6,807 | 33,571 | 35,547 | 383,997 | 345,292 | m986 | m980 | 192 | 25 |
| 10 | 2,957 | 3,244 | 29,813 | 43,544 | 362,443 | 398,232 | m1,085 | m857 | 210 | 32 |
| 11 | 2,936 | 5,354 | 21,976 | 38,542 | 68,654 | 293,380 | m2,256 | m973 | 305 | 82 |
| 12 | 2,775 | 3,051 | 27,071 | 33,443 | 376,833 | 307,777 | m1,114 | m1,071 | 252 | 26 |
| 13 | 6,126 | 2,906 | 33,368 | 26,947 | 350,381 | 373,482 | m975 | m1,105 | 52 | 47 |
| 14 | 3,363 | 2,673 | 30,696 | 31,222 | 343,900 | 372,789 | m1,073 | m1,104 | 91 | 42 |
| 15 | 3,194 | 3,093 | 35,113 | 31,459 | 342,337 | 290,726 | m975 | m1,134 | 300 | 31 |

**Where the bid lands.**
- In the first 300 months it is mostly unskilled: 99 refused NONE posts on
  average, in 27% of months, against 14 COLLEGE posts in 39% of months.
- After m900 it is college and university alone, in about a fifth of the
  months.
- The unskilled bids come from a young town's big plants. Seed 1 at m110: a
  refused Blast Furnace's 648 unskilled posts, in a town of 2,350.

## 4. Three other readings tried, and not taken

The same sixteen seeds, each with a diagnostic switch
(`docs/queue/LD1/variants.patch`, on the first build).

| reading | m300 | m600 | m900 | past 40,000 | Machine Works' wait | least end |
|---|---|---|---|---|---|---|
| **as built**: the posts of a short band, at most the band's posts again | 3,965 | 11,764 | 34,039 | m1,033 | 40 | 290,726 |
| at most the band's workers (the first build) | 2,990 | 11,511 | 34,536 | m998 | 35 | 296,807 |
| only the posts the city could not staff (the shortfall), at most its workers | 3,092 | 12,066 | 28,950 | m1,090 | 35 | 271,494 |
| only the skilled bands, at most its workers | 4,027 | 12,732 | 31,446 | m1,066 | 48 | 276,126 |

- **Capped at the band's workers**, it bid a village for plants no wage could
  staff there. Seed 1's unskilled multiple was 1.87x at m10 (main 0.92x), and
  the young city was 15% smaller at m300.
- **The shortfall** prices less than the building would set if it stood, and
  buys back nothing: its m900 and its 40,000 are later than main's.
- **The skilled bands alone** spare the young city, gain nothing by m900, and
  would leave Jerus's own case out: an Iron Mine is 45 unskilled, 12 diploma
  and 3 college posts.

## 5. XS1's trap: not counted (as recommended)

A hold the gate puts on only because the builders' laid-off crews are counted
as spoken for asks nothing. The test asks for the hold's own building again,
with the crews counted spare.
- On main this is half of all holds (22,133); on LD1, 13,354.
- The fall is mostly seed 11's changed path. The trap itself is barely moved:
  by XS1's test without its "idle at the core crew", 7,369 trap months on main
  and 6,540 on LD1, seed 11 aside.
- Counting those holds would raise wages in a town with thousands jobless and
  leave the gate shut (XS1's notes §6).

IC1 changes the gate itself. LD1 leaves `Sector.staffing()`'s answer exactly as
it was, so the two stay separable. Once IC1 frees an idle builder's crews,
the holds those crews cause leave the gate. This test then sorts only a busy
builder's crews, who are coming back to work.

## 6. What it does not reach

- **A big city's graduate shortage.** The bank's branch asks 13 college posts of a band of 8,000
  that stands 30% short at 1.15x for 900 months. That band is held by the curve
  (`ELASTICITY` .5 makes 1.3x tightness a 1.14x wage) and by the world's
  ceiling on graduates (`WageBand.arrivalCeiling()`). A building with a fifth
  of its posts skilled cannot open there. Recorded, not queued.
- **How many come.** Migration's target is posts and homes, with an
  affordability pull: a wage decides who arrives, and how many only through
  affordability. So the bid works where the city is already taking migrants
  (the gap was positive in 12,037 of main's 12,466 shortage holds), by making
  the short band a larger share of them.

## 7. The checks

**The ensemble** (`docs/ensemble/LD1.md`): 80 runs on LD1's model (9e590801), two at once, 38 minutes. 23/26
against 0.8.88's 22/26:
- **A3 and R3 now pass.** A3: the longest deflation spell is 4 months at the median [0-11], against 6 [0-13].
- **A2 and S1 fail as on 0.8.88.** A2: inflation over 10% for 16 months at the median [12-32], against 16 [11-31].
- **R2 turns red.** HIGH's unemployment over its twin is +0.38 points at the median [-2.74 to +6.05], against
  +1.50 [-4.32 to +9.74], under R2's +0.50. Pending LD1-R.
- The young city's inflation (m240-600) is 4.65% at the median, against 4.40%.
- T1 and T2: 16/16 seeds pass 40,000, at m1,033 [857-1,141]. The final population is 335,916, against 347,140.

**The full suite**, on the first tip (004b2d8f), exited 4 with 25 NEW red lines in 12 harnesses. Every one was a
played city's path. With the bid off, LD1 is main's to the byte (§3).
- **Mended**, each fixture now causing its condition again. No assertion changed; the fixture lines name their new
  causes. Each is green on the branch and on main's model.
  - CreditCheck: the ban had 3 months left and lapsed in the third pass. It was not a fault: `Slices.passes()`
    refuses any borrowing under a ban.
  - BondCheck 5c, BankCheck (four mends), DeskCheck, CallCheck and BuildAdviceCheck.
  - HealthCheck: every sector is held from the drain on, so the fee is all the twins differ by.
  - ForeignCheck: Heavy Industry is held, the same hinge as EM1's Wood Products. The parity twin's bid let a Blast
    Furnace through and the weaker twin's did not, since what a building can pay is in local money.
  - PolicyPreviewCheck: Freight is held, as WN3 held Cement & Concrete.
  - HoldersCheck 9: the savers' rate is set at .005, as AuctionCheck sets it, so the households' purse binds and the
    bank holds under half the issue.
  - CopperRefiningCheck (d): read to one part in a billion at the bisection's boundary, which LD1's path read one ulp
    under.
- **WellCheck 4 is a fault of main's**, which LD1's path meets, so it is not mended:
  - when a well opens in the month two wear out, `Oil.view()` nets the retirement against the opening and drops only
    the oldest one;
  - the fresh well carries the old vintage and is retired again next month;
  - the slice reproduced it on main's own classes, by landing a second retirement in the month an order completed.
  The one-line fix, `if (gone > 0) wells.strikeVintages();` in `Game.retireWornOutWells()`, passes WellCheck,
  OilCheck, OilViewCheck, ReadPathCheck and SaveFileCheck. It is queued as OV1, not made here: it is not LD1's
  mechanic, and it may move the trace the ensemble measured.
- **What the moved paths showed of Freight.** Twice, a depot the gate had held on main was let through once its
  band's workers came: a Rail Depot that borrowed $185M and lost $1.3M a month (PolicyPreviewCheck's town), and a
  fleet and depot on credit whose default failed the bank (BankCheck's books city). The estimate it was ordered on
  is Freight's own, and the gate had been hiding it. Recorded for IC1's ensemble, which lets more held buildings
  through again.
- The first build's run (capped at the band's workers) had other path reds. They are gone with that build:
  FoundingYardCheck, AutoBuildCheck, MonetaryCheck, CentralBankCheck, FundCheck, CurrencyRegimeCheck and MapCheck.

**The final suite**, on the tip with every mend (6705f230), exits 4 with 6 NEW lines in two harnesses. Every other
harness is green but for main's recorded reds. 5 timing lines are soft (SaveSlotCheck 1, MapCheck 4), and Stale
has no firm finding.
- **WellCheck 4**: its three lines, above (OV1).
- **ScaleCheck**: 2 lines fail where main's record has 7. Both are "...and it builds: more homes at the end than
  at the start", one in each copy of the growing city it copies with its sectors free (5 and 10 billion people).
  - That line is in main's record. Of the record's other lines, the city now grows, its rents price no migrant
    out, the free copy orders, and the cash tracks.
  - The judge reads the line's second time as NEW.
  - The section's town is ST1's subject, which ST2 rescans after LD1 and IC1.

## 8. The calls (★, each taken the recommended way)

- ★ **The bid is held to the band's own posts, not its workers (Recommended, taken).** A village has few posts, so
  the gate's reason for being stands. The bound is exact (1.41x). The alternative, the first build, bid a founding
  village's unskilled wage to 1.87x (§4).
- ★ **"Can pay" is consider()'s own interest test at prime and the record's surcharge (Recommended, taken).** That
  is the most the business could borrow at. The brief's "BuildAdvice's estimate" is the same figure
  (`BusinessInvestment.estimatedMonthlyProfit`, which BuildAdvice reads). The alternative is the lender's quoted
  rate for this sector this month: a wage bid that moves with the bank's mood.
- ★ **The builders' laid-off crews ask nothing (Recommended by XS1, taken).** §5.
- ★ **R2's red is accepted for now, and read again on IC1's ensemble (Recommended).** Pending LD1-R.
- ★ **WellCheck 4's three lines ship red on Jerus's word, with OV1 queued to mend them (Recommended).** It is main's
  fault, met on LD1's path, as WN3-F's was met on WN3's and shipped with FL1 queued. The alternative is OV1's line
  made here: an Oil book change in a wage batch, and maybe a moved trace after the ensemble.

## 9. One in, one out

Not a process batch. No script under `tools/`, no check rule and no session rule changed. The probe and its scripts
live in `docs/queue/LD1/`, the study's own folder.
