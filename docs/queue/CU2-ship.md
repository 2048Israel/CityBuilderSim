<!-- readiness: provisional -->
## changelog
### {next} — CU2, World prices chained — 2026-11-16, ready, see docs/queue/CU2-notes.md
The world market's January is chained (WorldChain, forty rows from buildings.json): a made good is the cheapest
maker's landed cost of its inputs plus its own part on the manufactures index, so rod follows its cathode and wire
its rod; a root is anchored on its reference band; the copper charge is money on Nations' own swing, peaking on
the Historical path in 2024 (the record, ★4, waits on Jerus as WP1-C); a guard puts back the last finite step
(0 fires to 2170 on drawn world 9). Seed 1 stands no Rod Mill (main's 363), seed 0 four Wire & Cable Works
(main's 61); at 2100 on the Historical path DR Congo's terms of trade are +0.07 (main's -0.44), Zambia's +0.28,
Chile's +0.28. Checks: WorldMarketCheck's chain, recipe, guard and charge; CopperRefiningCheck 10-11 (a mine and
a smelter that pay, the cycle seen; seed 1's Rod Mills played); SaveFileCheck's and WorldMoneysCheck's fixtures
restated for the chain. Six rows' recipe shares are the file's, not the study's. Saves: format 54, and the floor
with it, so older cities no longer open.
## pending
| CU2-W | Recommended (a), accept: a 1900 village earns .56-.60 of today's unskilled wage after two years on CU2's prices (main's .34), where R0d's 1901 ladder starts it at about a quarter (.238) - the curve's scarcity premium on an untouched 1901 base (WageLadderCheck 6 asserts the cause month by month), falling as arrivals fill the posts (.60 at month 13, .556 at 24); (b) cap a 1900 city's multiple below MAX_MULTIPLE, or slow its hiring, if a 1900 village should stay near a quarter ({next}) | Jerus's call (REV-CU2-1): the chained prices grow the village to 2,501 people in two years (main's 1,358), its unskilled band at 4.84 posts a head at month 24 and at the curve's ceiling at its tightest | [CU2's notes](docs/queue/CU2-notes.md) |
## queued
| CY1 | The planner appraises a processing works over its cycle: SpreadPlanner reads one month's margin as lasting, so a sector builds at the copper charge's peak and is left at the trough owing more than it holds, its own borrowing lifting the hurdle past every later build (DS1's answer, CU2: seed 2's five smelters from m1598, 30.44% and an 82-month loss streak at m2400; seed 1's Scrap Copper Refinery on a one-month spike). Read the margin over the charge's swing or a trailing year, and look at a sector whose rate has outrun its works' earnings; any processing sector can be left this way | [CU2's notes, DS1](docs/queue/CU2-notes.md) |
| MC1 | A credit crunch leaves households with no home: while the bank's leverage ratio binds (round 2), BusinessDebtManager.canFundMortgage refuses every landlords' mortgage for want of capital (MORTGAGE_CAPITAL), so the ensemble's SHOCK seed 13 on CU2 builds one block of Low-Rise Apartments in 70 months (m703-772) while the landlords' plan asks for 20-33 a month, and up to 174 households have neither a home nor one to double up in (m738-776, F1's one finding). Diagnose which rule should give (candidates: the bank's capital rebuilt in a crunch, the insured mortgage under the leverage rule, the landlords' own equity while the bank is short), mend the model, keep LongPlaytest's >1 unplaced-household watch; the regression causes the crunch from the SHOCK-13 city | [CU2's notes, The ensemble](docs/queue/CU2-notes.md) |
## known-reds
MapCheck | at the widest view every pixel a highway or the track crosses is drawn as it, and no other | FB9 | 2026-11-16 | CU2's chained prices grow a different played city (256 plots across, main's 296); at 1,024 plots a pixel MapCheck's own run pixels and the unchanged MapTiles.lastRun() disagree on it, in map code CU2 does not touch
KeptCheck | no month makes more than a tenth of the nodes the first draw made (the most made in one: 45 of 400; | FB2 | 2026-11-16 | The Government Overview on CU2's played city makes 45 nodes in its busiest month of 25 (main records the same page's class at 47 of 378 and 40 of 384: the unchanged Government code recreating transient fiscal rows), the kept screen's retention in UI code CU2 does not touch
