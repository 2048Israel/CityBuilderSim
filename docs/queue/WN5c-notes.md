# WN5c - A picked block priced off the screen’s thread

## Measurements before editing (2026-11-14)

Scratch probe target/WN5cProbe.java, target/wn5c-before.txt. Real Earth and world files named in JDK_JAVA_OPTIONS. Windsor/Detroit River and Gibraltar/Strait, one-month played fixtures in GameFiles.scratch; also a read-only copy of the current autosave into scratch slot 10 (month 1121, 378,368 owned plots, level 6). No real slot or autosave was written.

Click milliseconds by level 4..12: Windsor 0.52, 0.51, 4.91, 12.11, 57.66, 115.81, 488.93, 1137.37, 2783.50; Strait 1.70, 0.93, 3.59, 14.69, 57.46, 165.88, 518.18, 1459.58, 4977.86. Monthly remeasureDrafts alone with 1/10/50 level-8 picks: Windsor 10.11/17.28/30.04 ms; Strait 13.35/110.43/1111.50 ms. Played autosave copy at its own level: 0.25/0.78/2.51 ms (no seller plots walked). Both paths can exceed LandMarket.QUOTER_MS, so both expensive seller walks must leave the screen thread.

## Design and thread ownership

★ Move measurement, retain geometry, price in the current month. LandMarket separates immutable measured ground and seller parts from pricing. MapView supplies Picks a dedicated low-priority daemon executor and Platform.runLater. An owner-thread request copies the office through quoter(), reserves draft ids, and clears the old shelf; the card says Pricing the block... and offers no purchase until completion. The worker measures only its copied office/CityLand and its private draft cache. Shared LandSellers and world assets retain WN5b’s existing thread-safe read contract. Worker results publish through the FX dispatcher; only FX accepts them, changes live drafts, computes current-month prices, refreshes landmass linkage, or changes Picks and UI nodes. The request’s cancellation flag alone is volatile across threads.

★ The month stays synchronous and in its existing order: it prices retained unowned ground with this month’s ground price, US price level and seller factors. Equal unowned plot counts prove unchanged geometry because ownership only grows; unowned resource fields cannot be extracted. A purchase that changes a screen draft drops it for worker remeasurement; disjoint purchases retain it. A result whose land, grid, ownership count or sellers changed is rejected. A month passing during pricing is allowed: FX prices the returned geometry at acceptance using the current month, so no stale price lands a month late. Game.nextMonth still delegates to SimulationEngine.simulateMonth; neither month implementation or HW1 file was edited.

Headless model/harness Picks remain synchronous. Their draft geometry is retained too, so the monthly seller walk is removed there without changing prices or month order.

## Verification in progress

Initial batch-check --fast passed before edits. First implementation compiled; Stale found an adjacent javadoc introduced in MapView, since fixed. LandNationsCheck section 13 holds click measurements by level, 1/10/50 monthly picks at both sites, bit-identical current prices/sellers, reserved ids, no seller plots walked during monthly repricing, worker isolation, month passing in flight, cancellation, and ownership changes.
