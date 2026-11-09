# HB2 renders: the harbour's stock, its rail and a crane

Written by `docs/queue/HB2/RenderHB2.java` (a single-file program; its header says how it is run): a Bergen city on the real Earth played 2400 months in 112 s in a scratch folder, and specimen terminals on a plain shore, each at its real box size with its quay wall and the ship its schedule brings. Everything is drawn at 8 px a plot, the tile raster's near width; a sheet shows the specimens doubled (the closest zoom, 16.0 px a plot) and the city's own harbour at 8 and at 4 px a plot (the 8 px picture halved: the detail starts at 3.6 px a plot, where the scale bar first reads 500 m). All of it is drawn over HB1's settled yard (b). Not a harness; nothing in it asserts.

## 1. The quay shows its stock

- `hb2-1a-fills.png` - the yard fills and empties. Boxes, heaps, crates and full tanks are there in the number the level gives; an empty slot shows its painted marking, an empty heap its pad.
- `hb2-1b-rises.png` - the stacks rise. The yard is always laid out in full; the level is its height: a low stack dim and flat, a high one bright with a shadow, a heap more rings, a tank a lighter roof.
- `hb2-1c-gauge.png` - a gauge on the box. HB1's yard as settled, never changing, and a bar along the land side of the box filled to the level in the cargo's colour.

## 2. Rail sidings on the quay

Each sheet draws the tracks inside the terminal's box alone. The line from the railway to the box is the placement's work (rail leaning toward a port) and is not drawn: the railway has no port on it today, so the city's band shows sidings no line reaches yet.

- `hb2-2a-quay-tracks.png` - tracks on the quay. Two tracks along the apron behind the wall (one at a small berth), the wagons under the ship's side; the yard gives up its two plots nearest the water.
- `hb2-2b-back-sidings.png` - sidings behind the yard. A fan of three sidings along the land side closing to one line, the rail yard's ladder in small (one track at a small berth); the apron stays clear.
- `hb2-2c-side-spur.png` - a spur down the side. One track in from the land side down the edge of the box to a buffer short of the wall: the siding a mine or a works has today.

## 3. A crane at the near view

Drawn with 2a's tracks under it, so the box goes between ship and wagon. A dry bulk berth's crane carries a grab of ore, not a box; a tanker's has a loading arm that does not move. A loop is two moves in 2.8 s; the game's boat lies at its quay 2.0 s of a 60 s month (BoatSchedule.BERTH_SECONDS), so a call is one or two moves.

- `hb2-3a-line.gif` - the pixel line. His words: one light line a berth from the ship across the wall, and an orange box sliding along it to the wagon and the hook coming back bare.
- `hb2-3b-gantry.gif` - a gantry on the wall. HB1's quay crane (four legs and a boom out over the ship), standing still; the box rides its boom.
- `hb2-3c-travelling.gif` - a gantry that travels. The same crane walking a plot along the wall between moves, to work the ship bay by bay.
- `hb2-3-crane.png` - the three as stills: four moments of a move a row (out, half way, set down, the hook coming back), and each in the city.

## What each costs to draw

Measured here, in Java2D on the PC with other work running, the least of 40 rounds: an estimate to set the looks beside one another, not the game's canvas. `MapView.FRAME_MS` is 8 ms of tile painting a frame; `MapCheck.BOAT_FRAME_MS` is 0.5 ms a frame for the boats.

- the game's own tile, painted and rastered (TilePainter.paint, TileRaster.raster): 2.45 ms a tile at 8 px a plot, 1.45 at 4, over the 8 tiles of the band's window.
- the city's 28 shore works drawn once (a stock or a rail look is still: it is painted when the level or the rail changes, with the tile, not every frame):
  - stock as HB1 settled it: 521 shapes, 0.714 ms
  - stock 1a: 527 shapes, 0.754 ms
  - stock 1b: 657 shapes, 0.798 ms
  - stock 1c: 577 shapes, 0.878 ms
  - rail 2a, a train in at each: 1,733 shapes, 1.080 ms
  - rail 2b, a train in at each: 1,596 shapes, 1.099 ms
  - rail 2c, a train in at each: 1,500 shapes, 1.308 ms
- a frame of the city's 28 berths, every one with a boat in (the crowded case):
  - the boats alone, for scale: 170 shapes, 0.500 ms
  - crane 3a, with nothing else: 117 shapes, 0.064 ms (13% of the boats')
  - crane 3b, with nothing else: 262 shapes, 0.198 ms (40% of the boats')
  - crane 3c, with nothing else: 262 shapes, 0.197 ms (39% of the boats')

