# WN5c - a picked block priced off the screen's thread

Queued 2026-11-03 by WN5b (its notes §3, ★2, the recommended way taken).

WN5b moved the map's hover quote off the screen's thread: since WN5 `LandMarket.quote()` splits a block's ground by seller
(`LandSellers.split`), and one cold quote near a foreign coast takes 45-76 ms at level 8 and seconds at levels 11-12
(WN5b notes §2). Two callers still run that split on the screen's thread:

1. **A click that picks a block**: `ui/MapView`'s picks (`toggleBlock`, `onlyBlock`) call `LandMarket.draft()`, which
   measures the block with its sellers.
2. **The month**: `LandMarket.remeasureDrafts()` measures every picked block again each month, inside `Game.nextMonth()`,
   which runs on the screen's thread.

First measure both near a border and a coast (Windsor, the Strait; a copy of a played city in a scratch folder): a click's
cost by level, and a month's cost with 1, 10 and 50 picks. Only if either can take more than a frame (16 ms, as
`LandMarket.QUOTER_MS`), move it as WN5b moved the hover: a copy of the office (`LandMarket.quoter()`) priced on its own
thread, with the card saying the price is coming. A check holds the measurement either way (LandNationsCheck, beside its
section 12).
