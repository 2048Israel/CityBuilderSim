# MT1 - the map's words: the island label, the landmasses' names, "Snow and ice", and the Earth's cache

Queued 2026-10-31, settling BK1-L and ET1-C's three calls the recommended way,
on Jerus's "Take all (Recommended)" (BP1 item 9). One small batch: each is
how the Earth map reads.

1. **The island label (BK1-L, issue #5).** "Island 2 · 5.37 km² · not
   linked: needs a port here (none has one)" is some 70 characters of amber
   mono whose only ground is a 3-pixel dark stroke, drawn over the landmass's
   own amber hatch and tint (`MapView.islandChips()`), so over busy ground it
   is hard to read. A dark rounded plate the width of the words under the
   chip, and two lines at the near view: the name and area, then why it is
   not linked.
2. **The landmasses named by hand (ET1-C ★2).** The data names a landmass by
   its most populous place, so the Americas read "New York's, 36.8 million
   km²" and Afro-Eurasia "Mumbai's". Name the six continents by hand (the
   Americas and Afro-Eurasia are one landmass each in earth-2's layer, so the
   batch chooses the names a joined landmass reads by). ET6's bridge card's
   "To New York's landmass" (ET6-C) reads the same name.
3. **"Snow and ice" (ET1-C ★3).** The legend's "Ice" takes the study's words.
4. **The cache (ET1-C ★1).** earth-2's tile with every layer inflated is 296
   KB, so `EarthData.CACHE_BYTES` (10 MB) keeps 33 tiles where earth-1 kept
   83, about five cities. Raise it to 24 MB. MP1-C named the same dial for
   Rotterdam's far node (54 of 80 ms): measure that node before and after.

Checked by renders (`docs/queue/MT1/`) and the harnesses that read the
names and the legend; no model change, the trace does not move.
