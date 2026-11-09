# RI1 - rail on islands ({next}, 2026-11-09)

The brief is [RI1.md](RI1.md): CL5-C's call 1 and ET5-C's "a linked island's hub at its port and the railway on
islands" (Jerus ticked "Rail on islands", 2026-10-31). Lane A, after AL1 (0.9.69).

## The design (decided before the code)

- **Slice A, the railway on each piece** (`CityRuns`, THE RAILWAY'S WAYS and the pieces). The rail net opens a
  piece on each map piece but home that has weight and a rail hub (a railway line crossing, RAIL_AT, on the piece's
  own dry ground nearest `Pieces.anchor(p)`), once the home railway has started. Its corridors come after the
  BRANCH and SIDING placeholders (2, 3), so `dropWays()` keeps them. Track is dealt by built weight as CL5 deals
  the highways (home at least 1 and the rest; nothing taken back unless the model's track falls; a net a purchase
  joined to an older one's piece frozen). An island's line is its four spine arms grown evenly from its hub; no
  corridor, no link (the railway grows none). Yards: yard 0 at home as before; a new yard goes first to an island
  rail piece with none (piece order), on its own track nearest its hub, then beside works as before. An island with
  a yard grows its line to its edges, as a station does at home (FX5). Sidings and branches leave from an island's
  line as from the home main line. The freight station is a home yard; no train is routed to an island yard
  (`wayTo()` null): what reaches the island from home stays as the link is, by sea or over ET6's bridge.
- **Slice B, the hub at the port** (`CityMap.MapPieces`): `anchor(p)` is the place of a sea terminal standing on
  piece p's shore, else its hub as before; `hub(p)` the highway crossing nearest the anchor. The home piece keeps
  the founding site.
- No sidecar format bump: the rail net's pieces are written by the code that writes the highways' (FORMAT 12); an
  old sidecar reads as a railway of one piece and the islands open afresh.

## What was built

1. **Slice A** (`CityRuns`; Sonnet, reviewed): `islandRail`, `Pieces.anchor()` (default the hub), `crossingNear()` (start()'s
   search, lifted, with a piece filter), `openRailPieces()`, `layPieces()`, `yardedPieces()`/`islandYardSpot()`, and `deal()`
   lifted out of `layHighways()` so both nets are dealt by one rule (IslandNetCheck green on it). `growMainLine()` counts the
   home piece's plots; `growPiece()` opens no corridor for a railway piece; `dropWays()` keeps the pieces' spines and recounts
   by piece; `linePlots()`, the yards' spots and `distanceToNet()` read the islands' spines; the stamp hashes the piece count,
   which pieces have weight and the rail hubs, not the weights. Taken back only while the lines the model's track pays for
   (those no yard holds to the edge) are more than the model's track: the same as "only when the model's total falls", read
   from the net itself instead of last run's figure. Review fix: the freight station is a yard whose track chains back to the
   home hub (`chain()`), so a yard on a branch off an island's line is never it.
2. **Slice B** (`CityMap.MapPieces`; Sonnet, reviewed): `anchor(p)`, the middle of the first berthed sea terminal's box on
   the piece's shore (`portOf()`; a work's tiles are blocked from the pieces, so its piece is the nearest labelled tile in
   rings out to `PIECE_PORT_REACH`, 2 tiles), else `hub(p)`; `findHub()` keys each piece's tiles from its own reference
   tile, the port's or the founding site's. Worked out once per flood (a shore that moves floods again). A net already
   opened keeps its hub (saved); only a piece opened from now on starts at its port.
3. **Slice C** (Sonnet, reviewed): `IslandRailCheck.portIsland()` (section 8's city for a tool) and `tools/IslandRailRenders`, which
   draws it: `docs/queue/RI1/ri1-a-island-port.png` (the terminal on the island's shore, the rail hub beside it, the island's line
   and its yard with a siding fan, 160 of the railway's 301 plots), `ri1-b-home.png` (the home hub, boxed in by the fixture's houses:
   4 plots), `ri1-renders.txt`.
4. **Two review fixes** (mine, on the review of all three):
   - **The first yard is home's.** The islands' spines are yard lines since slice A (`spotsOnTrack()`), so the first yard, nearest
     a mine, could go to an island's line beside a mine there, and the freight station with it. `Ctx.homeFirstSpot()`: with the
     railway on more than one piece, the first box that fits on home's ground before any island's; an island's only where home's
     track has room for none (section 8's city, whose home line is 4 plots). IslandRailCheck 3: a mine on A beside its line puts
     A's box first in the order (the premise); yard 0 is home's and the freight station. Without the fix both fail.
   - **A piece with no rail hub is not searched every month.** MapCheck's month of +0.01% at 5B took 100 to 119 ms against
     main's 1 to 2 (budget 5 ms; soft at a pick, hard at the gate). The rail stamp moves whenever the model's track does, every
     month in a growing city, and `openRailPieces()` searched afresh, each run, every piece with weight and no railway (MapCheck's
     square city: 550 pieces, 334 with one) for a dry crossing near its anchor. `railMiss` keeps each failed search's ground
     version and anchor, and the piece is not searched again till one moves (not saved: a loaded city searches once). Found
     with a probe of the 5B month, `islandRail` on against off (100 ms against 2); the stamp's weights cost 0.03 ms.

## The checks

- **IslandRailCheck** (new, nine sections): 1-7 on IslandNetCheck's synthetic ground (home, islands A and B, an unowned
  square C): the premise with `islandRail` off; a rail hub and track on each island, C none; dealt by weight against the
  rule's own formula, weights drifting move nothing, more grows, less trims newest end first; three yards, home's at the
  mine and one on each island's track nearest its hub, an island's line with a yard grown till every arm waits, the
  freight station home though an island yard is nearer the main line's end, `wayTo()` of an island yard null; an island
  works's siding from the island's line; a purchase joining A to home freezes A's railway; one piece is main's railway to
  the signature; the sidecar round trip. 8-9 on LandmassCheck's Rotterdam fixture, a real CityMap: island 648 linked by
  its General Cargo Terminal (a second at home), Freight Lines, Rail Terminals and Elevated Highways handed, buildings on
  the island: its anchor the terminal's box middle, its highway and rail hubs the crossings nearest the terminal and
  nearer it than the old rule's (the premise, computed), track on its own ground, a yard on it; before the link no piece
  and no track on it; a piece with no terminal keeps the old rule. The fixture buys a dry square beside the founding
  site (Rotterdam's founding parcels have no dry rail crossing) and the island as 3 x 3 squares (one square gave the
  same hub by both rules); the city has no mine, so its first yard is not beside one. Section 3 also holds the first yard
  home's with a mine on A beside its line (review, item 4).
- **Not done: a real-map island linked by a bridge** (the brief's "one by a bridge"; RI1-F in Pending). The runs read the
  map's pieces, which do not say how a piece is linked, so the synthetic islands A and B stand for any linked ground; a
  bridge-linked island's hub keeps the founding site's rule (call 3), which a fixture on BridgeMapCheck's ground would hold.
- **MapCheck against main** (main's classes, cce06c9c, MapCheck alone; RI1's in the pick beside 19 others): the same four
  known reds word for word (gate.txt). The lines that moved are the islands' track drawn: the 10,000x city's widest view
  323 pixels of highway and track against 318, its dithered ground 12,638 pixels against 12,644, the runs key 3,983 nodes
  against 2,717 (1,724 drawn against 1,194). The month at 5B and 10B, the bounded months' worst: 1.93 and 4.42 ms against
  main's 2.52 and 3.89 (budget 5); before the review's `railMiss`, 119 and 183. The one SOFT line left, the FX-side map work
  at 5B (median 23.1 ms, budget 19), is main's too (21.1 on main alone).
- **The checks:** `./batch-check.sh --pick` (20 of 191 harnesses, both data files named) exit 3: MapCheck's four known reds
  only; IslandRailCheck 88 ok, no FAIL.

## Calls for Jerus (RI1-C)

1. ★ **An island's yard comes before home's further yards.** A new yard goes first to an island railway with none, then
   beside home's works as before; a city whose model has only one yard keeps it at home, where home's track has room for it (an island's line
   only where it has none; main laid none then). Recommend yes: each piece its
   own railway, as CL5 gave each its highways.
2. ★ **No train runs to an island's yard, over a Rail Bridge or not.** ET6-C's "trains and sidings do not use a rail
   bridge" stays in its row: this batch did not take it. The island's railway is drawn and dated, and its works have
   their sidings; the freight station and the trains' ways stay home. Recommend: as is, until a rail bridge carries
   freight (AL1-R's row).
3. ★ **A bridge-linked island starts from the founding site's side**, its hub the crossing nearest the founding site as
   before CL5 had it - the near shore, where a bridge usually lands. Only a port moves a hub. Recommend: as is.

## The trace

The default playtest (`-Dplaytest.trace`, both data files named) on main's classes (cce06c9c) and on RI1's final model
(slices A and B): the eight files byte-identical, so no ensemble (EN1); nothing in the model reads the map. The report
differs in the run time (58 s against 59) and the layout watch alone, the map's: at m2388 the playtest's other island
holds a railway of its own, so 7,074 homes' boxes are drawn against 7,468 (190,223 compared against 194,951), 0 moved
outside a spill's districts on either, and one more box (56 against 55) counted off a run, as MF1 counts them apart.
The review's two fixes (item 4) are in the map's runs, which the model never reads, so the trace was not run again.
