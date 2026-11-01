# IC1 - an idle builder's crews are spare for the staffing gate ({next}, 2026-11-01)

The brief is IC1's row in `QUEUE.md` and `docs/queue/XS1-notes.md` §5 (XS1's call ★, (a) recommended). A model
batch: Opus, with one Sonnet slice. Built on LD1 (0.8.91), which changed the same gate and left the idle crews'
holds to this batch (LD1's notes §5).

## 1. The rule

- **While Construction stands idle at its core crew, its laid-off posts are spare for the gate.** "Idle" is
  `Construction.isIdleAtCoreCrew()`: the month's strike found work for no more than the core crew, so the builders
  offer `IDLE_PAYROLL_FLOOR` of their posts and lay the rest off. `Sector.staffing()` then leaves Construction's
  own withheld posts out (`BuildingManager.getPostsWithheld(sector, type)`, rounded as the city's count is).
- **A working builder's laid-off posts stay spoken for.** Those are a builder offering more than the core crew and
  less than all. 0.7.17's rule is kept where it still holds.
- **Other sectors' withheld posts stay spoken for.** That is Freight's undriven depot places (FR2).
  - XS1's switch freed every withheld post while the builders were idle. Its runs and the narrower rule's are
    byte-identical on all sixteen seeds, because Freight withholds nothing in the builders' idle months.
  - The narrower rule ships. Freight's places are a driver per truck kept, not crews laid off for want of work.
- **What does not change:**
  - No new state. The idle reading is the struck share, saved since 0.7.17, so there is no format bump.
  - The landlords' count of every post (`RealEstate.plan()`) stays, as XS1 recommended: alone it freed nothing.
- **Who reads it.** Every planner, the builders' own depot orders, the build card and the advisor all ask
  `Sector.staffing()`, so they follow. LD1's sorting of holds (`staffingWithCrewsSpare()`, `noteStaffingHold()`)
  now sorts only a working builder's crews and other sectors' withheld posts.

## 2. The 0.7.17 rule: what it was for, and why it no longer holds

**What it was for.** Jerus's 0.7.17 answer: builders "keep a core crew (a quarter) and lay the rest off; those
workers become unemployed, draw EI and can take other jobs, and are hired back when work returns". The posts were
made to follow the work. Every planner kept counting the laid-off posts, because "they come back with the work".
For a builder in a lull with work coming, that holds: a plant ordered on those workers would lose them when the
builders hire back.

**Why it no longer holds for an idle builder.** A builder at its core crew has no work coming. Its crews come back
only when something is ordered, and the gate held every order on their account. XS1's finding:
- seed 11 was held 1,300 months at 20,000 people by 3,187 such posts;
- 8-11 sectors stood at "the city could staff 33%";
- all sixteen AUTO2 seeds passed through the trap.

Jerus's own words, "can take other jobs", now hold at the gate too.

## 3. What it does: the sixteen AUTO2 seeds, main (LD1, 0.8.91) against IC1

Main's runs reproduce LD1's table to the person. IC1's runs are the shipped model's: they are byte-identical to the
study switch's (a) on seeds 5 and 11, so the tables below are the model as committed.

| | m300 | m600 | m900 | m1,200 | end | past 40,000 |
|---|---|---|---|---|---|---|
| main | 3,965 | 11,764 | 34,039 | 64,506 | 335,916 [290,726-398,232] | m1,033 [857-1,141] |
| IC1 | 7,026 | 13,996 | 36,174 | 73,018 | 335,718 [260,177-423,911] | m976 [770-1,133] |

Medians over the sixteen seeds, with [least-most]. A plainer view:
- **The young town is earlier:** 77% larger at m300, 13% larger at m1,200. It passes 40,000 57 months sooner at the
  median, and the latest seed is earlier than main's latest.
- **The end is the same at the median**, against XS1's -5% on 0.8.88. The least seed ends lower: seed 15 at 260,177,
  against main's least, 290,726.
- **Seed 11** passes 40,000 at m770 (main: m973) and ends at 350,235 (main: 293,380).
- **RE1's seed 4** passes 40,000 at m848 (main: m995) and ends at 286,243 (main: 346,167).

| seed | main m300 | IC1 m300 | main m900 | IC1 m900 | main end | IC1 end | main 40k | IC1 40k |
|---|---|---|---|---|---|---|---|---|
| 0 | 2,773 | 5,190 | 33,084 | 22,139 | 349,562 | 321,200 | m1,072 | m1,133 |
| 1 | 5,398 | 4,686 | 31,744 | 33,926 | 322,939 | 366,752 | m1,079 | m975 |
| 2 | 3,917 | 6,309 | 21,292 | 39,782 | 366,014 | 361,073 | m1,141 | m956 |
| 3 | 3,081 | 8,550 | 30,666 | 40,166 | 323,826 | 298,305 | m1,081 | m872 |
| 4 | 5,059 | 6,475 | 34,635 | 44,807 | 346,167 | 286,243 | m995 | m848 |
| 5 | 4,013 | 6,659 | 42,109 | 28,006 | 326,540 | 368,785 | m860 | m1,071 |
| 6 | 6,543 | 8,094 | 36,104 | 32,851 | 315,869 | 287,918 | m984 | m1,090 |
| 7 | 5,977 | 8,420 | 38,182 | 36,092 | 371,308 | 305,410 | m980 | m980 |
| 8 | 6,190 | 6,895 | 40,130 | 45,025 | 321,169 | 401,453 | m884 | m876 |
| 9 | 6,807 | 7,158 | 35,547 | 56,540 | 345,292 | 357,961 | m980 | m844 |
| 10 | 3,244 | 8,672 | 43,544 | 53,816 | 398,232 | 423,911 | m857 | m838 |
| 11 | 5,354 | 8,891 | 38,542 | 52,682 | 293,380 | 350,235 | m973 | m770 |
| 12 | 3,051 | 7,487 | 33,443 | 31,495 | 307,777 | 312,129 | m1,071 | m1,096 |
| 13 | 2,906 | 7,290 | 26,947 | 34,313 | 373,482 | 350,462 | m1,105 | m977 |
| 14 | 2,673 | 5,080 | 31,222 | 32,926 | 372,789 | 320,916 | m1,104 | m1,117 |
| 15 | 3,093 | 6,451 | 31,459 | 36,257 | 290,726 | 260,177 | m1,134 | m986 |

**`trap.sh` still counts its months** (3,484 on main, 3,185 on IC1). It reads the builders idle with many crews
laid off, not the gate. In those months the gate now counts the crews spare.

## 4. The slow seeds, and (b): not taken

Under IC1 ten seeds pass 40,000 sooner, one the same, and five later: 0 (+61 months), 5 (+211), 6 (+106), 12 (+25)
and 14 (+13). XS1's four slow seeds on 0.8.88 were 4, 5, 6 and 13. On LD1's model, 4 and 13 are now sooner (m848 and
m977).

**The time limit, measured.** In the variants below, the crews are spoken for until the builders have stood idle
N months, then they are spare. The switch is `docs/queue/IC1/variants.patch` on 0.8.91, with `-Dic1.grace=N`.

| run | m300 | m600 | m900 | m1,200 | end | past 40,000 | its four latest |
|---|---|---|---|---|---|---|---|
| main | 3,965 | 11,764 | 34,039 | 64,506 | 335,916 | m1,033 | m1,104-1,141 |
| IC1, (a) | 7,026 | 13,996 | 36,174 | 73,018 | 335,718 | m976 | m1,090-1,133 |
| a grace of 12 months | 5,950 | 13,144 | 33,907 | 71,410 | 336,976 | m986 | m1,096-1,189 |
| a grace of 24 months | 6,018 | 12,699 | 34,169 | 67,832 | 344,134 | m988 | m1,113-1,129 |

**The slow seeds are the tail, not a mechanism.**
- Every run has about the same late tail: its four latest seeds pass 40,000 at m1,090-1,190.
- Which seeds fill it reshuffles with every change. The seeds that are slow under (a) are fast under a grace: seed 5
  passes at m883 with 12 months, seed 12 at m857 with 24.
- The seeds that are fast under (a) are slow under a grace: seed 2 at m1,189 and seed 11 at m1,076 with 12 months,
  seed 8 at m1,113 with 24.
- A grace only costs the young town some of (a)'s gain (m300: 5,950 or 6,018, against 7,026).

**What slows seed 5: a bust, not the crews.** A probe (the ensemble driver with XS1's `probe.patch` and a line of
each sector's workers) at m480-1,100 shows:
- under IC1 the young town is ahead by m570, at 15,226 against 11,264;
- Business Services already has 2,678 workers at m480 (Contact Centres, ordered as "work the world will pay more for
  than it costs to do here"), where main's has 298;
- it loses money every month from m520, while the policy rate swings between 3.3% and 14.3%, and sheds its whole
  holding by m600;
- jobs fall from 7,941 to 5,165, and unemployment reaches 38%;
- main's Business Services reaches the same size later (2,681 by m700) and stands.

Seeds 0 and 6 show the same shape: a faster young boom, then a bust at m520-560. In seed 0, IC1 is at 12,904 at m480
against main's 5,397. Its builders then lay off 1,459 posts in a town of 12,000.

**So (b) is not taken.** Neither crews let go too soon nor crews held too long slow these seeds. A grace before the
crews are spare moves the same tail onto other seeds.

## 5. A village's founding (FoundingYardCheck 6)

**Why the founding building is a yard.** PT1-M4 chose the Builders' Yard because a depot's idle crews held its
village: 37 laid-off posts in a village of 70 workers, and no shop in 240 months.

**Under IC1 the depot village opens its shop first.** Over 36 months:

| village | first shop | people at m36 |
|---|---|---|
| founded on a depot | month 9 | 191 |
| founded on the yard | month 13 | 125 |

**The section.** Its last line asserted that the depot's twin opened no shop, which is the rule IC1 reverses. That
line now asserts that the twin opens its first shop within FIRST_SHOP_MONTHS too. The section's other lines, the
yard's, are unchanged. `Game.FOUNDING_YARD`'s javadoc now says the depot reason was PT1-M4's, not one that still
holds.

**The yard stays.** Changing the founding would move every trace, and nothing here asks for it. Recorded, not
queued.

## 6. The checks

- **The harnesses that read the change** (WantedPostsCheck, FoundingYardCheck, InvestCheck, BuildersFirstCheck,
  ManufacturingCheck) exit 0 on the slice's tree.
- **WantedPostsCheck:**
  - section 6 now causes a working builder's lay-off: struck at half, under the game's own hook, where it used a
    fake 0 share. Its every assertion's meaning stands: a working builder's crews alone ask nothing on the wage.
  - Section 1's mirror of the gate's arithmetic leaves an idle builder's posts out, as the gate does.
- **The new section, WantedPostsCheck 11, causes the trap the model's own way:**
  - 55 depots and 1,500 unemployed;
  - the builders idle at 25% withhold 2,077 posts, and the gate passes the Machine Works at 100%, the crews-spare
    share exactly;
  - the same builders working at 30% withhold fewer, 1,939, and yet the gate refuses at 70.5%;
  - Manufacturing's staffable count is 1 idle and 0 working.
- {suite}
- **The ensemble** (`docs/ensemble/IC1.md`) ran 80 runs on IC1's model (72b7f553), two at once, in 21 minutes. It
  scores 22/26, against LD1's 23/26:
  - **A3 turns red again.** The longest deflation spell from m240 is 5 months at the median [0-12]. One seed reaches
    12 against "< 12 in every seed": LD1 had 4 [0-11], 0.8.88 6 [0-13]. Pending IC1-R.
  - **A2, S1 and R2 fail as on LD1.** A2 is 15 months over 10% at the median [11-34], against 16 [12-32]. S1's
    longest spell is 79 months.
  - **R2 reads +0.19 points at the median [-3.65 to +2.94]**, against LD1's +0.38 and under R2's +0.50. That is
    LD1-R's reading on IC1, asked for in its row: still red, and smaller.
  - **T1 and T2 pass.** 16/16 seeds pass 40,000 at m976 [770-1,133], and the final population is 335,718. These are
    the sixteen-seed figures of §3, run for run.

## 7. What ST2 should know about ScaleCheck's town

**ST1 found XS1's trap at the town's founding** (ST1's notes §5):
- the builders sit at the core crew from m30, with 203 posts laid off against 196 unemployed at m60;
- ST1's `idle` switch from m0, which is IC1's rule, grew the town to 12,173 by m1,200;
- then it busts: 12,083 at m1,500, and 6,018 at the end.

IC1 ships that rule, so section 6's town moves. On IC1's full suite:
- **Section 6.** The playtest town at `FREE_FIXTURE_MONTHS` (1,124) is growing: 23,102 people at m1,129 in 29,844
  household places, its rents pricing no migrant out.
  - Its 5-billion copy still builds nothing: 0 homes per K, the line in main's record.
  - Its 10-billion copy builds 33 homes per K, and that line passes.
- **Section 9, new on IC1.** The copy 1,000 times over no longer tracks its city. It is off from the first month:
  cash by 1.46 of the city's, GDP by 2.73, deposits by 0.81. By the sixth month unemployment is off by 0.12 and
  people by 0.045.
  - That is 8 new red lines (people, cash, savings, GDP, deposits, prices, unemployment and the registers).
  - IC1's test is a share (`Construction.isIdleAtCoreCrew()`), and the withheld posts round as main's always did,
    so it should scale.
  - Check first whether the copy's builders read idle in the same months as the city's. A struck share on the core
    crew's edge, or an order count that rounds differently at K = 1,000, would part them.

**What to watch on the rescan.** Rescan on IC1's tip once it is on main. Watch for an overbuild bust after the
faster young boom: seeds 0, 5 and 6 here rise and fall at m480-600, and ST1's freed town fell by half after m1,500. A
month that holds every assertion before such a bust may not hold after the next batch moves the trace.

## 8. The calls (★, each taken the recommended way)

- ★ **(a) alone, no time limit (Recommended, taken).** §4: a grace of 12 or 24 months moves the late tail onto other
  seeds and costs the young town. The alternative is a 24-month grace: the median end is 344,134 against 335,718,
  within the seeds' spread, with m300 at 6,018 against 7,026.
- ★ **Construction's own crews, not every withheld post (Recommended, taken).** It is byte-identical on the sixteen
  seeds. Freight's undriven places are a driver per truck kept, not a lull's lay-off. The alternative is XS1's
  switch as it was.
- ★ **FoundingYardCheck 6's last line follows the new rule (Recommended, taken).** It asserted the effect IC1
  reverses. The alternative is to drop the line.
- ★ **A3's red is accepted (Recommended).** It is one seed at the criterion's boundary: 12 months against "< 12",
  as 0.8.88's 13. The median spell is 5 months. Pending IC1-R.
- ★ **LD1-R reads +0.19 on IC1 (Recommended: accept, as LD1-R recommends).** R2's red is smaller, not gone. Whether
  R2's premise still reads the rate stays LD1-R's question.

## 9. One in, one out

Not a process batch: no script under `tools/`, no check rule and no session rule changed. The study's switch
(`variants.patch`) and its medians script (`med.sh`) live in `docs/queue/IC1/`. They run with XS1's scripts in
`docs/queue/XS1/`.
