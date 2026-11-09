# LB5 - LB4's review follow-ups (the rankings' slots)

Queued 2026-11-09 by the orchestrator, from the review at LB4's gate (verdict SHIP + FOLLOW-UP). Neither is a wrong
figure today; each is a row that can read the wrong city for a moment. A small fix batch: Sonnet whole
(`docs/batch.md`, 3).

1. **Same name, same seed, another city.** `SavedCities.Played.matches` (SavedCities.java:67) takes a slot as the
   city in play when the name and the world seed agree. Two cities founded with the same name on the same seed (a
   re-found, a second try) then both read as "this city, as saved". Compare one founding detail too (the first
   history month or the founding cash, whichever the header already holds; add nothing to the save unless neither
   is there). The rankings' check gets a fixture: two slots, same name and seed, different founding; one marked.
2. **The stamp misses the header.** `SavedCities.stampOf` (line 141) stamps the save and the history files by size
   and time, not the header the row reads first. While a save lands over a slot (LB3's write order), the row can
   keep the old city's figures until the next file moves. Stamp the header's size and time as well.
3. **Optional:** `RankingsScreen.placeSaved` calls `snap.now(played)` per row; once per redraw is enough. Take it
   only if the timing harness shows it.
