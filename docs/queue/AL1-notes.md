# AL1 - automatic building links the city's islands ({next}, 2026-11-09)

The brief is `docs/queue/AL1.md`: Jerus, 2026-10-31, "autobuild will build bridges and ports if sees
that land is low and there is something unliked (basically, if link is allowed with bridge, then it
builds a highway bridge or road bridge (and rail soomehow) if not it build a link via port)". Lane one,
after MB1, built on main at 1d86e0a0. It overturns ET5's call 5 ("the AutoBuilder and the test player
never build a port to link an island") and ET6-C's "the automatic builders never order a bridge".

## 1. What it does

- **The rule, once** (`BuildAdvice.linkFor()`, THE ISLAND LINK): what the city should order to link
  the island it most needs, or null. Nothing unlinked: null at once, nothing read (every generated
  world). The island is the unlinked landmass with the most buildable ground that no link is on its
  way to (a bridge on site to it, or a terminal on it while linked ground has one). Its link, in order:
  a terminal at home where one already stands on the island and none on linked ground (a port shared
  where one serves, the brief's Bergen case 2); else a bridge where `BridgeSurvey` finds a crossing,
  the **Highway Bridge** where an Elevated Highway stands, else the **Road Bridge**, quoted with its
  span as the card quotes it; else the **cheapest terminal** on the island's own shore and room and,
  where no linked landmass has one, the cheapest at home (an island's port links only beside one on
  linked ground, `Landmasses.closure`). None possible: NONE, with the survey's refusal and why no
  port. The card's pick of a bridge's far landmass is left as the rule found it. The candidate's unit
  is the ground it links (`Link.perSqFt()`), not trips: a bridge carries nothing.
- **The AutoBuilder** (`AutoBuilder.link()`, THE ISLAND LINKS): "land is low" is the measure the pass
  already reads before it buys ground - an order that lacked linked ground (`lacks() > 0`,
  `groundShort()`). At the end of such a pass, every other order placed, it places the rule's link:
  one unit, the ground under a bridge's landings or a home terminal bought as the others' is, the
  money within the debt limit on the same quote and bond; a refusal is a held line in the inbox's
  "Automatic building is held back", as the others are. An entry in its log ("Island links"),
  `linkSteps()` for the harness.
- **The test player** (`LongPlaytest.linkIslands()`): when its own ground-ahead line says the ground is
  short (`keepGroundAhead`), the same rule, each order through its own `build()` path.
- **A survey that found no crossing stands 12 months** (`BuildAdvice.NO_CROSSING_MONTHS`): such a
  survey searches out to 45 km, up to 1.7 s (ET6), and an island held for money or with no link is
  asked every month the ground is short. Kept per city's `Landmasses`, never saved.
- Small reads: `Landmasses.portsAt(id)`, `Bridges.buildingTo(id)`. A template counts only where the
  city knows it (`Game.knows()`, R3's: the Road and Rail Bridge from 1900, the Container Terminal from
  E5); the Build card has no such check, so before 1900 the auto-builder links by port where the card
  would offer a bridge.

## 2. Jerus's Bergen city (the brief's two cases)

1. **"Not linked: needs a port here (home has one)" beside a highway over the water** (picture 2).
   The model never reads the map (`CityRuns`, `Bridges`: "the map reads the model"), and the map lays
   no run onto ground no port or bridge links (`CityMap`, THE ISLAND RULE ON THE MAP): the highway in
   the picture is a short crossing between pieces of linked ground (ET6 slice A), drawn only, and it
   does not land on the island. What links the island now is a real bridge, which the auto-builder
   orders where the survey allows one (★2). The save (0.8.90, format 49) no longer loads on main
   (HB1-notes 1), so the picture was read from the code, not from the city.
2. **Too many small ports** went to HB1. AL1's port links share a port where one serves: a terminal
   already on linked ground means the island's alone, one on the island means the home one alone.

## 3. Calls (AL1-C), each built as recommended

1. ★ **No rail bridge from the automatic builders** ("and rail somehow"). A Rail Bridge links as a
   Road Bridge does; nothing in the model or on the map runs a train over one (ET6-C: trains and
   sidings do not use it), and the ground used is pooled (ET5's call 1), so no works stands "on the
   island" for a railway to serve. A second bridge beside the first would be money for nothing.
   Recommend: none until a rail bridge carries something; Pending AL1-R.
2. ★ **The map's highway over water links nothing**; the island links by the bridge the auto-builder
   now orders. The other ways (a second `Joins` reading short crossings from the ground, a free link
   within 34 plots of a highway; or the map reporting back) either give a link for nothing or break
   the one-way rule. Recommend yes.
3. ★ **A bridge wherever a crossing is allowed**, at any length to 36 km and any price, as his words
   say; a port only where none is. A long span holds the builders for years (ET6's points). The other
   way: a port where it is the cheaper link. Recommend his words; the played Earth run (IslandLinkCheck
   9) linked by port, as the fixture's strait has no crossing, so how often a long bridge fires in a
   real city is not yet counted.
4. ★ **The cue is the pass's own lack of ground**, no new dial: the AutoBuilder keeps no ground ahead
   (the test player does, and reads its own line). The link comes after every other order of the pass,
   so the month's other orders are as before; no payback cut (its worth is ground, not output); the
   builders' cut as a first of a kind (one unit, held only where the site has no output). Recommend yes.
5. **One link a pass**, the island with the most buildable ground first, the lower id on a tie.

## 4. Not done

- **AL1-R** the Rail Bridge (★1).
- **AL1-W** the words: the Land tab's "not linked: needs a port here", the map's chip and the
  `island:<id>` notice ("Build a port there ›") still name a port where a bridge is allowed; the
  notice opens the port card. The Build tab draws no card for the advice's link (the rule is the
  advice's, read by the two builders). Screens by eye: none was changed.

## 5. The trace, the checks

- **The trace**: the default playtest (`-Dplaytest.trace`) on main's classes (1d86e0a0) and on AL1's
  final model: the eight files byte-identical; the report differs only in the layout windows' run time
  (3.7 s against 3.8). No ensemble (EN1).
- **IslandLinkCheck** (new, nine sections, on LandmassCheck's and BridgeCheck's Rotterdam fixture with
  Goeree-Overflakkee's square; the crossing handed to the survey by `BridgeCheck.seam`, since the
  fixture's strait is open water past 45 km): the Road Bridge over a 450 m shallow channel $695k, the
  Highway Bridge with a highway standing $1,335k; deep sea, a Bulk Berth on the island and one at home
  $4,004k, the island's alone with one at home ($2,002k); NONE in words; the no-crossing memory once
  in 12 months; the auto-builder with its ground cut: one Road Bridge, never a second, standing 367
  months on (the village's two depots), linked; ports linked at month 311; the debt limit 0 holds it,
  nothing bought or borrowed; the test player orders the same; a played run of 240 months (the cue in
  12, one port link of two terminals, linked at month 175) and a generated world (the cue met, no link).
- **LandmassCheck 12**: ET5's call 5 restated by his words - the grown city has room, so its island
  stays unlinked unless a pass met an order short of ground (a line holds it to the cue), and a twin
  (`grownShort`) with its ground cut has the auto-builder order the port link, the island unlinked
  and audited until both terminals stand, linked at month 293.
- `./batch-check.sh --pick`, the data named (`JDK_JAVA_OPTIONS`): 26 harnesses, **exit 3**, the only red main's recorded KeptCheck (`docs/notes/gate.txt`); two timing lines SOFT, not counted (SaveSlotCheck 1, KeptCheck 1).

No process change (no script, check machinery or rule touched): one in, one out does not apply.
