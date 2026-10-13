  - I14 (UI), 0.8.0: the map (pits by sites, quarries, camps, plantations as
    forest, the works as O13's campus; zones tinted by district) and the
    release; a pan on the PC. **From W1 (`runs/fixW1-notes.md` §7):** over an
    iron cluster `DistrictPlan` can leave a street piece walled in by buildings
    and mines. MapCheck's dry place one cell south (359424, 371712), the x
    10,000 copy, district (1, -1): 15,260 mined plots (23%) and a 50-plot piece
    the ground does not part (`join()` never walks over a building), so
    PlanCheck's one network fails there. No fixture stands there now; W1's
    clusters make such districts possible in a dense city. Probe:
    `scratch-w1/probe2 W1Plan`. **From I6 (`runs/fixI6-notes.md` §7):**
    `BuildingVisual` draws the coal mines on coal sites and the Coal Preparation
    Plant as a plain MINING building. **From I7 (`runs/fixI7-notes.md` §7):**
    the Dimension Stone Quarry draws as plain MINING. **From I9
    (`runs/fixI9-notes.md` §7, ★10):** `CityMap` places the first type of a
    resource that is not SITED nowhere (coal's mines, the quarries, the leach
    plant), a latent "districts do not sum" finding the day one is built; the
    forestry crews avoid it because `BuildingVisual` gives a site only to a
    resource in fields.
    **From I13 (`runs/fixI13-notes.md` §7):** the pits by sites can read
    `BuildCard.stands()` and `Game.claimsOn()`; `CityLand.fieldSitesOn()` gives an offer's fields.
    Still open from I5's note (in the archive with the I13 line): the map draws a 20-site works as one mine.
