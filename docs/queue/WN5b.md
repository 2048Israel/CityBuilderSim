# WN5b - WN5's review follow-ups: the hover quote's cost, two words on the land card

Queued 2026-11-01 from the stranger's review at WN5's gate (SHIP + FOLLOW-UP;
nothing blocking).

1. **The hover quote on the FX thread.** `ui/MapView.java` ~2855 now runs
   `LandSellers.split` on every hover; near a foreign coast that is
   plot-by-plot reads and 12-nautical-mile searches (cached). Measure a
   hover's cost near a border and a coast on a big map; if a hover can take
   more than a frame, compute the split off the FX thread (or from the cache
   only) and show the quote when it is ready. MapCheck or a timing line holds
   it.
2. **The island clause glued on** (Game.java ~2591-2595): the ET5 island words
   now follow the "Sold by" sentence ("...Canada (38%, C$1.1M) - on Island 2,
   which no port links"); give each its own sentence.
3. **Two roundings of one split:** the receipt rounds each seller's share on
   its own (`soldByWords`), the card by largest remainder, so a three-way split
   reads 33/33/33 on one and 34/33/33 on the other; both by largest remainder.

The checks: a split whose shares round differently reads the same on both; the
hover's cost measured and held.
