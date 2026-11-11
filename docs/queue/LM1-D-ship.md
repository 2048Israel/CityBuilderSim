## changelog
### {next} — LM1-D, the founding's water widened to six blocks — 2026-11-11, ready, see docs/queue/LM1-notes.md
A new city whose water within `CityLand.FRESH_PATCH` (3 blocks) of its first fresh block holds less than one plant's (A24)
now widens the square a block at a time to `FRESH_PATCH_MOST` (6) and takes the first that holds it (`freshWater()`): a
river 1 to 5 plots wide held 0.14 to 0.18 km2 in 3 blocks, one plant about 30% of its 60,000 units. A river alone still
falls short at 6 and buys the rest (Paris 0.33 km2 of 0.49, Rome 0.25; they would hold it at 10 and 14 blocks), and 41 of
200 generated worlds start short where 75 did. The default world and Stockholm hold it in 3: no change, the trace unmoved.
New: LandCheck (e), the widening on painted rivers; EarthTerrainCheck's Paris restated. Red by name: MapCheck 14's short
card, the played city's 20-year auction selling nothing on the new founding (BQ1).
## queued
| BQ1 | MapCheck 14's short card on LM1's path: the played city (world 35) is set short of three offers by D$966.1k and its 20-year auction sells nothing ("face D$0.0k for D$0.0k", `bondQuote()`'s auction sold 0), so "short, the card's bond is the funding page's first ..." and "...and issued, the card's bond buys them all" fail. The fixture looks for a term market that clears in the city instead of causing one (as KS2's three on RB1's path); LM1 founds that city on a different patch of water and touches no debt code. Done means the fixture causes a market that clears (or asserts the card's bond against one it sets up) and both are green on LM1's path | [LM1 notes §5](docs/queue/LM1-notes.md) |
