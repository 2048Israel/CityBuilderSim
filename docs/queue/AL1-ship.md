## changelog
### {next} — AL1, automatic building links the city's islands — 2026-11-09, ready, see docs/queue/AL1-notes.md
Automatic building now links ground the city owns across the water (Jerus, 2026-10-31: "autobuild will build bridges
and ports if sees that land is low and there is something unliked"). When a month's order lacks linked ground and an
island the city owns ground on is unlinked, the AutoBuilder orders, after every other order of the month: a Highway
Bridge where an Elevated Highway stands, else a Road Bridge, where the survey finds a crossing; else the cheapest sea
terminal on the island's shore, and one at home where no linked ground has one (a port already there is shared). One
rule (`BuildAdvice.linkFor()`) serves it and the test player; money, ground and refusals as its other orders. No Rail
Bridge (nothing runs a train over one). A survey that finds no crossing stands a year. The map's highway over water
links nothing: the model never reads the map. IslandLinkCheck (new) causes each case on the Rotterdam fixture;
LandmassCheck 12's "stays unlinked" is held to the cue. Overturns ET5's call 5 and ET6-C's "never orders a bridge".
One landmass reads nothing: the default playtest's trace is byte-identical, no ensemble, no format change.
## pending
| AL1-R | the automatic builders order no Rail Bridge beside a road or highway link ({next}) | a rail bridge links as a road bridge does and nothing in the model or on the map runs a train over one (ET6-C), and the ground used is pooled (ET5 call 1), so no works stands on the island for a railway to serve; built when a rail bridge carries something | [AL1](docs/queue/AL1-notes.md) |
| AL1-W | the island's words (the Land tab's "not linked: needs a port here", the map's chip, the `island:<id>` notice and its "Build a port there ›") still name a port where a bridge is allowed, and the Build tab draws no card for the advice's link ({next}) | AL1 changed the model and the two builders only, the screens left as they were | [AL1](docs/queue/AL1-notes.md) |
