## changelog
### {next} — OV1, a well that opens the month two wear out keeps its own month — 2026-11-01, ready, see docs/queue/OV1-notes.md
Latent on main, met by LD1's path (WellCheck 4, the three red lines Jerus passed on "Ship, OV1 next"): `Oil.view()` nets the
wells standing against the stored vintages as one figure, so when a well opened in a month two wore out it dropped one of the
old vintages and never recorded the fresh well; the old well went on standing at its worn-out profile and was retired the month
after. `Game.retireWornOutWells()` now strikes the vintages before the month's retirements (a well opened so far is a vintage
of its month) and after each kind's (a well opening later is not netted against them). The line LD1's slice found, the strike
after alone, mends WellCheck 4 and not the other order; section 16 (new) causes that one, five wells with two a month short of
their life, and neither strike mends both alone. The default playtest's trace is main's but for its run time, so no ensemble.
Nothing new is saved; no format bump. WellCheck is green.
