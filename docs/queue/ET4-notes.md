# ET4 - Highways and rail priced by slope (0.8.47, 2026-10-25)

The brief: `QUEUE.md`'s ET4 row and `docs/studies/spec-earth2-terrain.md` §10's ET4 row, with §4.3; Jerus's decisions of
2026-10-12 ("all rec": D10). Three slices, each written by a Sonnet subagent at xhigh and reviewed and committed here: A the
land, B the works' price, C the map. The rates (`RunWorks.java`) were researched and written here.

## The land (slice A, §4.3: "their land comes from all ground")

- A highway or a railway line is a **run** (`Game.isRun()`: the Elevated Highway, the Rail Spur, the Freight Line; the Rail
  Terminal and Depot are yards and stay buildings; `Game.groundUse()` names the three uses). `LandManager.Use` {BUILDING,
  WORKS, RUN} beside the old `boolean works` overloads, which delegate.
- `runsSqFt`, a third part of the one pool (saved as `landRunsSqFt`; an older save reads the standing runs' land). In
  `split()` the runs take each class's share of the city's dry ground first - flat, foothill and steep in the city's
  proportions - and the works (steep first) and buildings (flat first) keep their rules on what is left.
- `runRoom()`: the largest run for which the buildings still fit on the flat and foothill the runs leave, `min(owned less
  allocated, (flat and foothill less buildings) / their share less the runs)`; the banner derives it. Where a run's flat
  share pushes buildings onto foothill, the foothill prepared rises by that much, so the next building is not charged the
  pads the run displaced (the run invoice carries it too).
- A run's plot is priced by its own pro-rata parts; the books attribute a sector's runs at the runs' mix (the Freight
  sector's track, the city's highways); `getUtilisation()`/`getUsedShare()` leave out the runs' steep part with the works'.
- **A flat city reads a run as a building, to the bit** (`flatOnly()`), as RunWorksCheck 5 proves.

## The works (slice B, §4.3: "cuts and fills on foothill, walls and viaducts on steep, tunnels on very steep")

`RunWorks`, US$ a m² of the run's land at the founding, nothing on the flat (the run's own price is the flat one):

| class | a highway | why |
|---|---|---|
| foothill | $110 | a bench across the 19% grade: the foothill pad's arithmetic (`SitePreparation.USD_PER_M2`) |
| steep | $2,080 | a viaduct: deck at $250 a sq ft (WisDOT 2025 $220, CDOT 2025 to about $305) over 23.2 m of the 30 m run (four 3.66 m lanes and their shoulders) |
| very steep | $2,330 | a tunnel: two bores at US$35M a km (the EC's rail unit-cost study: 25-30 million euros a km on average) over the 30 m run |

A railway's works are **2.7 times** a highway's on every class above flat (`RAIL_FACTOR`, 6% / 2.2%: a line gains a height
in no less than height over grade of length; AASHTO's 6% for the Interstate in mountains, 2.2% the main lines' ruling
grade). For scale: a highway costs about $1,640 a m² on the flat, so 2.3 times that on steep and 2.4 in tunnel (the spec's
sources: 1.5 to 3); a Freight Line in tunnel about $8,700 a m², $261M a km, where the Gotthard Base Tunnel cost about CHF
214M a km.

- An order of a run on a city that is not all flat pays the works at the **city's mix of ground** (`usdPerM2OfMix()` of
  CityLand's four areas), as points at the run's own founding price a point and money at today's builders' price for its
  work; in the sticker (taxed with the work) and the wait; owed on the site and billed to Construction as a foothill pad is,
  on the city's order and a business's (`BuildQuote.worksSqFt/worksPoints/worksCost/worksMix`).
- `BusinessInvestment.totalCostOf()` adds them, so the Freight sector's investor sees what a rail line costs on the hills.
- Build's card, a line under the quote (RunWorksCheck 12's fixture): "Cuts, viaducts and tunnels for its 0.0293 km² over
  the city's ground (foothill 40%, steep 25%, very steep 5%): 20,745.9 points of builders' work, $19.9M". The playtest's
  line, only when a run carried works.

## The map (slice C, §4.3: "drawn as a tunnel (portals, a dashed line), on steep ground as a viaduct")

- `TilePainter`: a run's plot on steep relief is a bridge (its deck, a straight plot); on very steep dry ground a tunnel
  (`Painted.tunnel`). `TileRaster`: a tunnel is its ground's grey with a dashed line in the run's colour and a
  `TUNNEL_PORTAL` band at each end (4 and 8 px a plot; below that, the ground with the run's colour on every other plot); a
  viaduct highway's casing is the deck's colour, since a highway fills its plot. The hover says "a viaduct", "a tunnel".
- Renders in `docs/queue/ET4/` (`tools.RunRenders`): `et4-a-medellin-viaduct.png`, Medellín at month 2,000, its highway
  across the steep grey on its deck; the city's runs touch no very steep ground (182 run plots: 123 foothill, 37 steep), so
  `et4-b-fixture-tunnel.png` is the harness's fixture tile. Checked by eye in the renders only, not in a played game.

## Checks and traces

- RunWorksCheck (new, `runworks`), fourteen sections, 183 OK: which templates are runs; their ground pro rata; the room and
  the displaced pads; the price and the books; a flat city to the bit (the land); saved; the rates against their
  constants; a run on each class of ground; the mix; builders' work in the books (the city's highway and Freight's line,
  owed, billed, audited); a flat city to the bit (the quote); the card's words; the map's viaduct and tunnel; the street's
  rule unmoved. SlopeCheck 9: the steep identity counts the runs' steep part beside the works'; its bound stays faithful,
  everything off the steep (the buildings and the runs' flat and foothill parts) within the flat and foothill, only the
  runs' steep part excused (the gate's review caught a first form that excused all of the runs).
- `./batch-check.sh --pick` with earth-2 named: see the report; MapCheck 8 is main's recorded red (FW11).
- The default playtest: byte-identical but the run-time lines, so no ensemble. `--earth rotterdam`: the same (flat).
- `--earth medellin`, before (main, 0.8.44) and after: **432,191** people at month 4,005 and **245,314**; slice A alone (the
  land, no works) 454,499. The works make a highway 1.46 times and a Rail Spur about twice as dear at Medellín's mix; the
  Freight sector lays 8 sidings (12 on main, 18 with the land alone), the city stops growing at about 220,000-245,000 from
  month 2,250 and orders 47 highways (141): their count follows the through traffic, not their price. Rerun after the
  merge of main (0.8.45, FW5): main's Medellín the same 432,191, the batch's the same 245,314, and the default trace
  byte-identical to main's. Works on 20 orders, 279,188 points (2.73%
  of the points billed). The ground at the end flat 20.9 / foothill 45.5 / steep 33.1 km² (very steep 3.9), against
  35.2 / 67.1 / 37.7 (0.8); the runs' 0.17 / 0.36 / 0.27. Hunger 26% in the last month only (100% of the groceries a
  checkpoint before). The audit and every reload clean in all three runs. Its trace is a baseline of its own.

## Calls for Jerus (ET4-C, under Pending)

1. **The rates**: foothill $110 a m² (the pad's bench), steep a viaduct $2,080, very steep a tunnel $2,330, from cost
   guides, DOT unit costs and the EC's study. Keep (rec.), or name better sources.
2. **Rail: its factor, and the Rail Spur** (no recommendation; the gate's review). The factor 2.7 (grade limits 6% and
   2.2%) multiplies the per-m² rates while the land is not made longer, and the tunnel's bore already comes from the EC's
   rail study, so a rail tunnel costs about 2 x 2.7 EC bores, about $191M a km (Gotthard's class), and a rail viaduct on a
   highway's deck about $170M a km. Medellín at month 4,005, each run on this branch but the one change:

   | variant | people | highways | spurs |
   |---|---|---|---|
   | main (0.8.45), no ET4 | 432,191 | 141 | 12 |
   | the land alone (slice A, no works) | 454,499 | 159 | 18 |
   | **as built**: factor 2.7, a spur a run | **245,314** | 47 | 8 |
   | factor 2.2 | 216,886 | 36 | 7 |
   | factor 1.0 | 268,137 | 60 | 9 |
   | factor 2.7, a spur priced as yard ground (no works, a building's land) | 382,036 | 115 | 14 |

   The factor does not order the result (2.2 ends lowest); what moves Medellín is the Rail Spur, a siding into one works,
   priced as a run: its works at the city's mix (+95% of its price at 2.7, +35% at 1.0) and its land pro rata. The live
   options: a factor of 2.2 or less, or a spur priced as yard ground (the main lines, the Freight Line, still runs).
3. **The points at the run's own founding price a point**, not the House's as a pad's: a highway's works at Medellín's mix
   are 46% of its points, so they take the builders as long as they cost. Keep (rec.).
4. **Tunnel portals at a tile's edge and on a 45-degree stretch** are not drawn (the tile's input holds no neighbour's
   relief). A small follow-up with an eye check (rec.), or as is.
5. **The pads a run displaces are paid by nobody.** Where a run's flat share pushes buildings onto foothill, `allocate()`
   counts their pads as cut, so the next building is quoted its own pad only. The model has no positions, so the push is
   the split's arithmetic, not a building moved; charging it would bill a pad no crew cuts. Keep (rec.), or charge the run.

## Not done

- No screen looked at in a running game: the card's line was read from the quote in the harness, the map in renders.
- The build advice still prices an order's ground at the best offer's price a dry square foot (ET3-C).

## Found on the way

- `BusinessInvestment.totalCostOf()` mirrors Game's charge by hand and still leaves out a building's foothill pad (ET2):
  the investor now sees a run's works but not a pad's preparation.
- For ET3-C: `BusinessInvestment.groundCostOf()` estimates every plot, a run's included, at the flat price, while
  `buildFor()` charges it class by class.
