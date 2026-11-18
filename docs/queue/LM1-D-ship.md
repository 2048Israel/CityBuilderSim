<!-- gate-run: /tmp/cbs-gate-LM1-D-XbqCj7nU -->
<!-- readiness: provisional -->
## changelog
### {next} — LM1-D, the founding's water widened to six blocks — 2026-11-18, ready, see docs/queue/LM1-notes.md
A new city whose water within `CityLand.FRESH_PATCH` (3 blocks) of its first fresh block holds less than one plant's (A24)
now widens the square a block at a time to `FRESH_PATCH_MOST` (6) and takes the first that holds it (`freshWater()`): a
river 1 to 5 plots wide held 0.14 to 0.18 km2 in 3 blocks, one plant about 30% of its 60,000 units. A river alone still
falls short at 6 and buys the rest (Paris 0.33 km2 of 0.49, Rome 0.25; they would hold it at 10 and 14 blocks), and 41 of
200 generated worlds start short where 75 did. The default world and Stockholm hold it in 3: no change, the trace
byte-identical to 0.9.150's. New: LandCheck (e), the widening on painted rivers; EarthTerrainCheck's Paris restated;
MapCheck 2h-c's first view held to the watched city's sidecar (a first view appends the walls its plans find, DM2-A).
Stopped by Jerus on 2026-11-11, merged with 0.9.150.
