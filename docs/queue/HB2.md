# HB2 - the harbour's detail: its stock, its rail and a crane

Queued 2026-10-31 from Jerus's ideas for the ports (his words are in
[HB1](HB1.md)), "just some ideas, if its too much no worries", so each is put
to him with renders before it is built. It runs after HB1, which bundles the
terminals and docks the ships.

**The renders are made and he has picked (2026-11-09): 1a, 2a and 3a.** "His
picks", at the end, is what the batch builds.

The harbour's look is HB1's render (b), the open cargo yard by what each
terminal handles (`docs/queue/HB1/hb1-b-yard.png`, `hb1-d-kinds.png`),
settled 2026-11-08 on its recommendation (HB1 notes, HB1-S); this batch
builds it first, and item 1's stock is that yard's heaps and rows.

1. **The quay shows its stock**: container stacks or bulk piles that grow
   and shrink with what the port holds (read through the port's public
   getters; the screen never recomputes a model figure).
2. **Rail leans toward the ports**: a quay with rail sidings, as its own
   picture, and the placement preferring rail beside a port.
3. **A small crane at the near view**: a pixel line moving an orange box
   between ship, quay and train.

All of it is near-view only. It holds `MapView.FRAME_MS` (8 ms of tile
painting a frame) and the boats' bound `MapCheck.BOAT_FRAME_MS` (0.5 ms a
frame); the timing harnesses say so. Put the look of each to Jerus first, as
two or three renders, and build what he picks.

## Where the detail starts

Jerus, 2026-10-31: "at out of zoom, no need for all that detail, 1km what you
see now, at 500m is where the detail would be".
- Below the zoom where the map's scale bar reads 1 km, drawing stays as it
  is.
- The detail draws from the zoom where the bar reads 500 m, and closer.
- Measured from his screenshots (`World.PLOT_M` 30):
  - the 1 km bar was about 105 px, about 3.2 px a plot, which is
    `MapFrame.L0_FROM`, where buildings first draw one by one;
  - the 500 m bar was about 85 px, about 5 px a plot.
- So a new detail level starts at about 5 px a plot: a dial in `MapFrame`
  beside `L0_FROM`. Set it by the scale bar's own rule, where the bar first
  reads 500 m, not by the screenshot's estimate; its one sentence says so.
- The same threshold holds for any building detail added later.

## The renders (2026-11-09)

In `docs/queue/HB2/`, written by `RenderHB2.java` there; its `README.md`
says what each picture shows and what each look cost to draw. Three looks an
idea, over HB1's yard (b), as specimen terminals at 16 px a plot and in a
played Bergen city's own harbour at 8 and at 4 px a plot. Put to Jerus
2026-11-09, the recommended look first.

| idea | look | picture | what it is |
|---|---|---|---|
| 1 stock | **1a**, recommended | `hb2-1a-fills.png` | the yard fills and empties: boxes, heaps, crates and full tanks in the number the level gives, an empty slot as its painted marking |
| | 1b | `hb2-1b-rises.png` | the yard always laid out in full, the level as its height: low stacks dim and flat, high ones bright with a shadow |
| | 1c | `hb2-1c-gauge.png` | HB1's yard unchanged, and a bar along the land side filled to the level |
| 2 rail | **2a**, recommended | `hb2-2a-quay-tracks.png` | two tracks on the apron behind the wall (one at a small berth), the wagons under the ship's side |
| | 2b | `hb2-2b-back-sidings.png` | a fan of three sidings behind the yard, the apron clear |
| | 2c | `hb2-2c-side-spur.png` | one spur down the side of the box: the siding a mine or a works has today |
| 3 crane | **3a**, recommended | `hb2-3a-line.gif` | the pixel line and its orange box, his words |
| | 3b | `hb2-3b-gantry.gif` | HB1's gantry standing on the wall, the box riding its boom |
| | 3c | `hb2-3c-travelling.gif` | the same gantry walking a plot along the wall between moves |

`hb2-3-crane.png` has the three cranes as stills.

What the renders found, which the build starts from:

- **The ports hold no stock.** `Ports` is tonnes a month by cargo kind
  (`seaIn(k)`, `seaOut(k)`) and a berth's month (`berthMonth(t)`); nothing in
  it is a level. His words were conditional: "if the ports have inventory or
  whatever... perhaps showing it?". So item 1's level is how busy a kind's
  terminals are, the month's sea tonnes over their berths' capacity, read
  through those getters with nothing new saved. A stock in the model would be
  a batch of its own, and it would move the trace.
- **No port has rail today.** The railway's hub is the mine nearest the
  founding site and its sidings go into mines and works (FR7). The map is
  read by nothing in the model, so leaning the rail toward a port is the
  map's alone: no trace moves and no ensemble is owed. The renders draw the
  tracks inside the terminal's box; the line that reaches the box is the
  build's.
- **Stock and rail cost no frame time.** They are still, so they are painted
  into the tile, once, when a level steps or the rail changes; the city's 28
  shore works took 0.5 to 1.3 ms in all (HB1's yard as it is, 0.6 to 0.7),
  and a frame's tile painting (`MapView.FRAME_MS`) is the same whatever a
  tile holds.
- **The crane is the one part drawn every frame**, and only while a boat
  lies at its quay (`BoatSchedule.BERTH_SECONDS`, 2 s of a 60 s month: one or
  two moves a call). With a boat at every one of the 28 berths at once, over
  two runs, the line was 0.06 to 0.09 ms a frame and a gantry 0.13 to 0.2,
  where drawing those boats was 0.5 to 0.6 in the same measure. Its place is
  a function of the boat's time at the quay, so the schedule's own frame
  (`MapCheck.BOAT_FRAME_MS`) gains next to nothing.
- A dry bulk berth's crane carries a grab of ore, not a box; a tanker's is a
  loading arm that does not move.

## His picks (Jerus, 2026-11-09)

Asked in one box, a question an idea, each with its three looks and "Leave
it out", the facts and the costs above inside it. He ticked the recommended
look of each and added no words:

1. Stock: **"1a Yard fills"**. The question said the ports hold no stock, so
   what he took is the level as how busy a cargo kind's terminals are this
   month, its sea tonnes over its berths' capacity, with nothing new saved.
2. Rail: **"2a Tracks on quay"**, with the railway leaning toward a port so a
   line reaches those tracks.
3. Crane: **"3a Pixel line"**.

So the batch builds 1a, 2a and 3a. Nothing is left out and nothing waits on
him. The other six looks stay in `docs/queue/HB2/` as the record.
