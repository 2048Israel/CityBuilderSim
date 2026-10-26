# ET5 - Landmasses and the island rule ({next}, 2026-10-26)

The brief: `QUEUE.md`'s ET5 row and `docs/studies/spec-earth2-terrain.md` §10's ET5 row, with §2.6, §6.5 and §9.1, and the
roads study's island rule (`spec-roads-and-ports.md` §3 and §5, I1-I7, decided 2026-10-09; D16 widened it to earth-2's
layer). Every phase ran as a Workflow (Jerus, 2026-10-25: two sessions may use ultracode that day, ET5 one of them): seven
readers, a design panel, four slices each written by a Sonnet 5.5 agent and read by two Opus reviewers, and an adversarial
review of the whole. The design as built is [ET5/design.md](ET5/design.md).

## The rule, as built

- **A plot's landmass** is `World.landmassAt`/`tileLandmass`: earth-2's 15″ landmass layer at the plot's blend point, as
  cover and relief are drawn. A dry plot on a cell the layer calls no land (the coast's fray, lake shores, river cuts) takes
  the nearest land cell's id within `EarthTerrain.LANDMASS_REACH` (8 cells, about 3.7 km; a tie to the lowest id), the bake's
  own naming rule; past it, home's. A generated world has no layer, so it is one landmass (D19: its trace stays byte for byte).
- **Home** is the snapped founding site's landmass, and the founding card now reads the same call (`EarthSite.siteLandmass`;
  `landmassNear` and its rings are gone), so the card and the model cannot disagree.
- **The books by landmass** (`Landmasses`, new, owned by LandManager): each landmass the city owns dry ground on, by relief
  and forest, its shore plots (owned flat or foothill dry ground beside owned salt) and its first holding; recounted at every
  land change and at load, never saved, so a reload is exact by construction. A 1° gate and a per-tile near-ids gate skip
  the plot reads where only home can lie (Medellín reads none; `EarthTerrainCheck` 18's `LANDMASS_TILE_MS`).
- **Linked:** home; then, while some linked landmass has a standing terminal, every landmass with one. Built as a closure
  with an empty `Joins` slot that ET6's bridges fill. A port links once it **stands**: one on site or a stopped shell does not.
- **Unlinked ground** is owned, counted and priced as before. It is no room: LandManager's free ground, the share used, the
  zones, the forest law and the split by relief read four linked accessors, which return the old field to the bit while
  nothing is unlinked. The pickers count linked buildable ground (DEPOSIT and `richest` skip island offers); no works stands
  on a field centred across the water (`Landmasses.fieldLandmass`, one reader for the model and the map).
- **Ports on a chosen landmass:** the order carries its landmass through `buildStack`, `processBuildOrder`, `quoteBuildOn`
  and `runAhead` (the card's one-shot pick, `CHOSEN`; the AutoBuilder and BuildAdvice by `RULE`: home with shore, else the
  linked landmass with shore and the most ground, never an unlinked one). A terminal on an unlinked island takes that
  island's own room. Where each terminal stands is saved (`DataSave.landmasses`, absent while every terminal is at home).
  A demolition that would unlink a landmass is refused.
- **The save:** `SAVE_FORMAT` 47 with an Earth-only floor (`EarthSite.LANDMASS_FORMAT`, `BEFORE_LANDMASS_WORDS`): an older
  Earth save would load ground across the water as room and every terminal at home. The general floor stays 45.
- **The screens:** the Land tab's LANDMASSES table (at two rows or more), the strip's island segment, the ISLAND tag and line
  on the office and the map's card, the map's amber hatch with its chip, hover and legend, the port card's chooser and link
  sentence, the refusals' words, a standing `island:<id>` notice with "Build a port there ›" (or the Land office where the
  island has no shore), the purchase receipt's clause, the investors' "…; 2.25 km² is free on Island 2, which no port links".
  None was looked at by eye: the game was not opened (no slot written).

## CL5's pieces, squared with it

CL5 grows a highway net on every piece of owned dry ground the water parts. ET5 leaves the pieces CL5's own and hides
unlinked ground from them: a plot on an unlinked landmass is not owned to the pieces, the runs, the district plans or the
placement (`CityMap`'s banner THE ISLAND RULE ON THE MAP carries the table). So an island gets no piece, weight or net
while no port links it, and a net never walks onto it by a dry neck the layer splits (case I); once linked, its ground
re-floods into a piece and grows its own net as CL5 built. Ground one landmass but two pieces (unowned land between,
fresh water, a short shallow crossing) is unchanged; two linked landmasses in one piece leave a stuck net that ET6's
bridges take. An unlinked island's terminal stands on the island's shore, and its count sits in a linked district.

## Checks and traces

- **LandmassCheck** (new, in the suite): 0 a generated world has none; 1 the reader against brute force, the near-ids gate;
  2 the books plot by plot; 3 free ground linked only (relief and forest too); 4 only a port there, no works on a field
  across the water; 5 two standing ports link, a shell never; 6 demolition refused when it unlinks, saved mid-way; 7 the
  card's pick; 8 the pickers; 9 the save; 10 a real map draws nothing on unlinked ground and re-measures at the link;
  11 the words; 12 a city grown 120 months across two landmasses, audited every month (the island linked in month 101,
  1,949 buildings drawn on it once home was filled, none on unlinked ground); 13 the full file. Each slice's mutations
  turned it red (A 3, B 4, C 3, D 2, the review's 6).
- **The default playtest, `--earth rotterdam` and `--earth medellin`**, before (main 0.8.47, then 0.8.50 after its merge)
  and after: byte-identical but the run-time lines (765,819, 669,031 and 245,314 people at the end). **No ensemble** (EN1).
- **Measured** (the spec's "measured in ET5"): Rotterdam's and Medellín's kept cities own 0.000 km² and are offered 0 of 24
  offers off home at month 4,005, on main and on the branch (`LandmassCheck --probe`). Goeree-Overflakkee is 23.5 km from
  the site; the delta's Voorne-Putten, Rozenburg, IJsselmonde and the Hoeksche Waard are Rotterdam's own landmass.
- **`./batch-check.sh --pick`**: see the ship file's paragraph; main's recorded red MapCheck 8 the only red.

## Calls for Jerus (ET5-C), each built as recommended

1. Ground used stays **pooled**: the gate is exact without a ledger by landmass, and the map deals linked ground by district
   distance (I5's "home while it has room, then the linked landmass with most free" not drawn exactly). Recommend yes.
2. The **pools stay city-wide** (remaining tonnes, fresh rights, Forestry's wood), though no works stands on an unlinked field.
3. A port **links once it stands**, any of the five terminals (no ferry until I6). Recommend yes.
4. Once the city owns ground off home, **home's coast test is the shore count** (dry shore beside owned sea), stricter than
   "owns any sea". Recommend yes.
5. The **AutoBuilder and the test player never build a port to link** an island. Recommend yes (the traces stay still).
6. **Unlinking is refused**: under pooled ground nobody knows what stands on a re-unlinked island. Recommend yes.
7. **Zones and the forest law on linked ground.** Recommend yes.
8. An unnamed landmass is **"Island N"** (N its place among the city's landmasses, home 1), not ET1's "unnamed".
9. A dry plot **more than 3.7 km from any land cell is home's**. Recommend yes.

## Not done (ET5-C)

Pools by landmass (call 2); I5 drawn exactly and ground used by landmass (call 1); an unlinked island port's count in a
linked district; a linked island's hub at its port and the railway on islands (CL5-C's call 1, still his); the sea route
between the two ports (I2's words; routes are the map's); ferries and the crossing (I6); two linked landmasses in one piece
(ET6); the NO_COAST shortcut buying sea before the chosen landmass's shore; an island port's foothill pad not credited when it
links, and the card's pick not saved; linking by the automatic builders (call 5); the demolition card's chooser and allowing
unlinking (call 6); the model's quay (the map may still count a port short); the hatch on the globe and the minimap, and the
Ports page by landmass; his generated cities' other-island districts stay buildable (D19); LandmassCheck has no unlinked
steep ground (no fixture world holds one); the screens by eye.

## The ultracode run

Every phase ran as a Workflow, each agent at xhigh; coding agents on Sonnet 5.5, reading, judging and reviewing on Opus 5.5.
Figures from each run's own usage line.

| phase | agents | subagent tokens | tool calls | wall time |
|---|---|---|---|---|
| understand: seven readers by subsystem, one synthesis | 8 | 2,018,720 | 710 | 25 min |
| design: three designers by angle, three lens judges, one synthesis | 7 | 2,095,378 | 430 | 61 min |
| slice A (reader, books, linked pool): implement, two reviewers, mend | 4 | 1,345,109 | 372 | 101 min |
| slice B (ports, linking, save) | 4 | 1,387,026 | 459 | 127 min |
| slice C (the map) | 4 | 1,127,126 | 363 | 76 min |
| slice D (screens, words, probe) | 4 | 1,075,697 | 378 | 56 min |
| review: three lenses, a skeptic a finding, one mend | 26 | 3,457,129 | 681 | 70 min |
| **in all** | **57** | **12,506,185** | **3,393** | about 8.6 h |

**What they caught:** the readers found that D19 already settles generated worlds and that CL5's pieces would lay highways
on an unlinked island (cases I and J); the judges, before any code, that a stopped shell could keep a link (F1, F20), that
masking the runs' ground leaves the pieces' own read (F4), that works would stand on island deposits (F5), and that an old
Earth save would load wrongly without a floor (F8). The slice reviewers found 21 to mend, among them a site index counted
twice in the month of a link (blocking), a restatement that left the books stale, the quote priced on another landmass
than its order, the shore missed at tile edges, and a port chooser hidden where the only shore is an island's. The final
review found 22, upheld 15: a NOT ENOUGH LAND page selling linked ground to an island port's order, the Zoning screen
sizing zones on the whole ground, a save between a demolition and the settle that did not resave the same.

**What it cost:** 12.5M subagent tokens, against BP1's figures 16 to 31 times an Opus batch session (400k-770k), and 1.7
times BP1's own 7.5M. The slices were 39% of it, the final review 28%. No agent stalled. **Verdict:** right for this batch,
which touches the save, every free-ground reader and the map in one change; the per-slice reviews caught one blocking and
several silent wrong-figure bugs that one pass would have shipped. For an ordinary batch the slice reviews alone (two
lenses, no understand fan-out) would carry most of it at about half the cost.
