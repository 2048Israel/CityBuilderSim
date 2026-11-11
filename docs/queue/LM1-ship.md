## changelog
### {next} — LM1, the land office map: a compact start, one number an offer, the deposits zoomed out — 2026-11-11, ready, see docs/queue/LM1-notes.md
A new city starts compact (Jerus, 2026-11-02, his city 7.8 km west-northwest of Stockholm on a river strip 5 km long). Its
water, one plant's (A24), is one patch within `CityLand.FRESH_PATCH` (3 blocks) of the block it starts from, never through
dry ground, widened a block at a time to `FRESH_PATCH_MOST` (6) where that holds too little: Stockholm 61 blocks in 30 x 14
where A24's strip was 113 in 37 x 32. A river alone still falls short at 6 and the city buys the rest (Paris 0.33 km2 of
0.49, Rome 0.25; 41 of 200 generated worlds, 5 on main); the default world founds block for block as before and the trace
did not move. The map writes one number an offer across the city, North 1-6 to West 19-24, as its row and its name do. Past
the zoom where the deposits' shading stops (a 2 km bar under 70 px) the map draws one icon a deposit, its resource's colour
and mark, thinned to one a 12 px square, with a Resources chip beside Nations. New: LandCheck's patch section,
EarthTerrainCheck 19 (Stockholm), MapCheck LM1. Moved fixtures: LandNationsCheck's Windsor and MapCheck 2c's share. Red by
name: MapCheck 14's short card, the played city's 20-year auction selling nothing on the new founding (BQ1).
## queued
| BQ1 | MapCheck 14's short card on LM1's path: the played city (world 35) is set short of three offers by D$966.1k and its 20-year auction sells nothing ("face D$0.0k for D$0.0k", `bondQuote()`'s auction sold 0), so "short, the card's bond is the funding page's first ..." and "...and issued, the card's bond buys them all" fail. The fixture looks for a term market that clears in the city instead of causing one (as KS2's three on RB1's path); LM1 founds that city on a different patch of water and touches no debt code. Done means the fixture causes a market that clears (or asserts the card's bond against one it sets up) and both are green on LM1's path | [LM1 notes §5](docs/queue/LM1-notes.md) |
| PX1 | A + floor breach in `DistrictPlan` that LM1's first cut of the founding reached: in MapCheck 1's city (world 35) at months 91 and 121, district (0, 0)'s plan laid two + junctions 4 plots apart, (48, 64) on the cell line and (48, 68), in homes cell 17 (the comb) where the city's ground ends at a cut and the streets along it are drawn as curves; the floor is `LATTICE`, 8. Planned again with `Input.latticeOnly` no + stood within 8 of another, with `combOnly` the pair stood, so CL4's avenues or curves along a cut lay it. LM1's final founding no longer reaches it (PlanCheck green). Done means a PlanCheck fixture that causes the case (that ground: `CityLand.FRESH_PATCH_MOST` set to 3 reproduces it) and the plan keeping the + floor there | [LM1 notes §5](docs/queue/LM1-notes.md) |
