# HB2 - the harbour's detail: its stock, its rail and a crane

Queued 2026-10-31 from Jerus's ideas for the ports (his words are in
[HB1](HB1.md)), "just some ideas, if its too much no worries", so each is put
to him with renders before it is built. It runs after HB1, which bundles the
terminals and docks the ships.

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
