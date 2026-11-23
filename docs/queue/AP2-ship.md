## changelog
### {next} — AP2, slot 9's slow months at 17 million people — 2026-11-22, ready, see docs/queue/AP2-notes.md
On Jerus's slot 9 (17.3 million people, 321,345 buildings, 151 districts) the months the ground moved took 8-15 s. A highway piece's failed link search reruns at every purchase, and its 2-million-plot ring drew the same Earth tiles again, about 90,000 draws for 4,000 tiles, through a 256-tile memory.
The runs' ground keeps every tile a link's search reads (`CityMap.RUN_SEARCH_TILES_KEPT`, 8,192, for the search alone; `CityRuns.Ground.searching()`), and gives back its usual 256 after.
`CityLand.centreStamp()`, asked at every plot the search reads, is kept between the centre's changes. `EarthTerrain.idsNear()`, asked by every bridge survey, is kept a tile. The auction's bidders read what each sector owes from one pass over the bank's book (`BusinessDebtManager.getPrincipalsByKey()`).
Every figure is the same to the bit. GatewayCheck, LandCheck, LandmassCheck and HoldersCheck each gain a section proving their kept read equals the read afresh and moves with what it reads, and the playtest's trace is the same byte for byte over 2,405 months.
On slot 9 the month went from a mean of 5.9 s, a median of 3.1 s and a worst of 12.9 s to 2.5 s, 2.4 s and 4.8 s, with every month's population and the whole log the same. The paused Build Overview's model went from about 590 ms to 540 ms.
A waiting link's search (about 1.5 s a land month), the re-measure when an island is linked, and the Overview's auction quotes are left for Jerus.
