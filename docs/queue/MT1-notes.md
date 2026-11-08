# MT1 - the map's words ({next}, 2026-11-08)

Brief: [MT1](MT1.md). A map-only batch on Sonnet as the whole session; the model, the saves and the trace do not
move (the names are rebuilt from the data at every load, `Landmasses.nameThem()`; no ensemble).

## What was done

1. **The island chip (BK1-L).** `LandmassView.chipLines()` gives the chip in two lines, "Island 2 · 5.37 km²" and
   "not linked: needs a port here (none has one)" (`chipWords()` is the two joined by " · ", as before). The plate is
   `ui/IslandChip.draw()`: a dark rounded plate (the stage's dark, 0.9 opaque, a 7 px corner, a hairline amber edge)
   the width of the longer line plus 6 px, 12 px a line; `MapView.islandChips()` places it as it placed the chip (centred
   on the owned tiles, kept inside the view). Render: `docs/queue/MT1/render-chip.png`, made by `tools/ChipRender` (the
   old one-line chip above, the plates below, one clamped at the view's edge).
2. **The landmasses named by hand (ET1-C 2).** `EarthData.Landmass.words()`: ids 1 to 6 read "Afro-Eurasia", "North and
   South America", "Antarctica", "Australia", "Greenland" and "New Guinea"; any other is the data's place and "'s"
   ("Charlottetown's"). The guard is the id **and** the data's place together (1 Mumbai, 2 New York, 3 McMurdo Station,
   4 Sydney, 5 Nuuk, 6 Port Moresby), so a file baked with other ids keeps the data's words rather than a wrong name.
   Read by `Landmasses.nameThem()` (the bridge card's "To North and South America", the port card, the chip, the hover)
   and `EarthSite.landmassWords()` (the founding card's "Its landmass: ..."). No hand name ends in 's or opens with an
   article, so `BridgeView.landmassWords()` lets it stand as "Island 2" stands, in any sentence position.
3. **"Snow and ice" (ET1-C 3).** `LandMap.groundWords(World.ICE)`, which the legend and the hover share (the study's
   board 2 words the entry and the hover alike); `EarthTerrainCheck` 15's assertion of the word changed with it.
4. **The cache (ET1-C 1).** `EarthData.CACHE_BYTES` 10 to 24 MB (83 tiles of earth-2's 296 KB). Rotterdam's far node on
   the full file, `MapCheck` 10, ms at 512 and 1024 plots a pixel, warm (first time in brackets): before 30.9 (65) and
   127.3 (178), after 8.3 (34) and 68.5 (148); every other stride within noise. "Every stride's far node over Rotterdam
   is drawn in no more than SCREEN_MS" was FAIL on main here (its stride-1024 node 127 ms, other sessions running
   beside it) and is OK with the 24 MB; `MapCheck` went from 4 reds to 3, main's two yard reds and its 5B timing red.

## Calls (★, the recommended way taken)

- **"Six continents" is six landmasses.** The earth-2 layer has four continents (the Americas and Afro-Eurasia are one
  each, then Antarctica and Australia); the six biggest landmasses add Greenland (id 5, "Nuuk's") and New Guinea (id 6,
  "Port Moresby's"), which would have read as a city's name the same way. Recommended and taken: name ids 1 to 6.
- **"North and South America"**, not "the Americas": a name with no article reads in "Island 2 becomes the city's 2nd
  linked landmass", "On X, which no port links" and the chip alike; "the Americas" would need a special case.
- **Two lines at every view, not only the near one.** The one line was as wide at the far views, where the island is a
  few pixels and the line a quarter of the screen.

## The harnesses

`EarthFoundingCheck` (the Medellín card reads "North and South America"; the six names; the guard; the bridge card's
sentence), `LandmassCheck` (the chip in two lines), `EarthTerrainCheck` 15 ("Snow and ice"), `BridgeCheck`, `EarthCheck`:
green on the full file (`-Dcbs.earth` named). The new assertions are in the first two.

## Process count (one in, one out)

None: no script and no rule changed. `CLAUDE.md`'s "forty-four files" became "forty-five" (`ui/IslandChip.java` is the
45th; `Stale` has it as a firm finding), and `docs/tree.md` has the two new files' line.
