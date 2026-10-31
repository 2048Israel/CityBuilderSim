# LD1 - a business short of workers bids the wage up ({next}, 2026-10-31)

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
- **Never more posts than the band has workers** (`MOST_OF_SUPPLY` = 1). The
  multiple goes as the root of the tightness, so this adds at most one to it. From
  the floor's tightness (.49) that is at most 1.74 times the multiple, and less
  above it: the refused posts can never double a band's wage.
  `WantedPostsCheck` 4 prints the ratio at six tightnesses.
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
| LD1 | 2,990 | 11,511 | 34,536 | 64,793 | 337,851 [296,807-413,295] | m998 [858-1,137] | 35 [21-103] |

Medians, with [least-most].

- **The loop opens.** The Machine Works' longest wait in the first 800 months
  falls from a median of 132 months to 35. The gate's shortage holds that pay
  fall from 12,466 to 8,231, and all holds from 43,775 to 33,998. At those holds
  the college premium is 1.2x or more in 29% of months (10% on main), and 4.1%
  of arrivals are skilled (2.9%).
- **The middle city is earlier.** At m900 the median is 8% larger. It passes
  40,000 about 70 months sooner, and no seed takes longer than m1,137.
- **Seed 11** passes 40,000 at m917 and ends at 365,728 (main: m2,256 and
  68,654). That is a changed path, not a cure: its trap is XS1's (§5).
- **Seed 4** (RE1's): 42,846 at m900 against 36,856, past 40,000 at m876
  against m979. But it ends at 316,037 against 381,539.
- **The young city is smaller at m300**: 2,990 against 3,520 at the median. The
  m300 figures have two humps: a few seeds boom early to about 6,000 and most sit
  near 3,000. LD1 has nine seeds under 3,000 to main's four. By m600 the median
  is level.
- **What drives the young city's dip**, read in seed 1:
  - a refused Blast Furnace's 648 unskilled posts are priced into a town of
    2,350;
  - the unskilled premium goes to 1.16x while the town keeps taking migrants and
    the jobs do not follow;
  - its boom comes at m390 against main's m270.
- **In the first 300 months the bid is mostly unskilled**: 107 refused NONE
  posts on average, and in 29% of months. COLLEGE averages 13, in 38% of months.
- **The end of the run is 3% lower at the median.** The least seed ends at
  296,807 where main's ended at 68,654.

## 4. Two readings tried, and not taken

The same sixteen seeds, each with a diagnostic switch (`docs/queue/LD1/variants.patch`):

| reading | m300 | m600 | m900 | past 40,000 | Machine Works' wait | least end |
|---|---|---|---|---|---|---|
| as built: all the posts of a short band | 2,990 | 11,511 | 34,536 | m998 | 35 | 296,807 |
| only the posts the city could not staff (the shortfall) | 3,092 | 12,066 | 28,950 | m1,090 | 35 | 271,494 |
| only the skilled bands | 4,027 | 12,732 | 31,446 | m1,066 | 48 | 276,126 |

- **The shortfall** prices less than the building would set if it stood. It
  buys back nothing: m900 and the 40,000 come later than main's.
- **The skilled bands alone** spare the young city its dip, and gain nothing by
  m900. They would also leave Jerus's own case out: an Iron Mine is 45 unskilled,
  12 diploma and 3 college posts.

So the brief's reading stands: the posts as if offered, in every band they are
short in.

## 5. XS1's trap: not counted (as recommended)

A hold the gate puts on only because the builders' laid-off crews are counted
as spoken for asks nothing. The test is the hold's own building, asked again
with the crews counted spare.
- On main this is half of all holds (22,133); on LD1, 15,915.
- The fall is mostly seed 11's path. The trap itself is unchanged: by XS1's
  test, without its "idle at the core crew", 7,369 trap months on main and
  7,726 on LD1, seed 11 aside.
- Counting those holds would raise wages in a town with thousands jobless and
  leave the gate shut (XS1's notes §6).

IC1 changes the gate itself. LD1 leaves `Sector.staffing()`'s answer exactly as
it was, so the two stay separable. Once IC1 frees an idle builder's crews,
the holds they cause leave the gate. This test then sorts only a busy
builder's crews, who are coming back to work.

## 6. What it does not reach

- **A big city's graduate shortage.** The bank's branch asks 13 college posts of a band of
  8,000 that stands 30% short at 1.15x for 900 months. That band is held by
  the curve (`ELASTICITY` .5 makes 1.3x tightness a 1.14x wage) and by the
  world's ceiling on graduates (`WageBand.arrivalCeiling()`). A building with
  a fifth of its posts skilled cannot open there. Recorded, not queued.
- **How many come.** Migration's target is posts and homes, with an
  affordability pull: a wage decides who arrives, and how many only through
  affordability. So the bid works where the city is already taking migrants
  (the gap was positive in 12,037 of main's 12,466 shortage holds), by making
  the short band a larger share of them.
