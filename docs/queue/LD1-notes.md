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
