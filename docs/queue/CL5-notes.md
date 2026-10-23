# CL5 notes - highways that reach the whole city (0.8.29, 2026-10-23)

The brief is [CL5.md](CL5.md); the study [spec-city-layout §5.6](../studies/spec-city-layout.md#56-highways-on-one-island-only).

## What was found

Main's default playtest city (month 4,005, 23,159 buildings) shows the fault the study measured on his
Bergen: the highways' spine ran out at the sea and the strait before one district, no corridor line ever
crossed it, and **301 of the model's 1,687 highway plots were laid, 1,386 never** (82%); the one built
district on another island had no highway.

His Bergen and Danzik (slots 1 and 2) could not be measured: they are save format 40, and this build reads
43 alone (the floor raised since). Only the autosave, a fresh city, loads. So the playtest's city stands in
for them, and the brief's "on his two saves" is not done (CL5-C).

## What was built (three slices, each written by a Sonnet subagent and reviewed)

1. **The link** (CityRuns, `openLink`, `linkWay`, `stepLink`). When a piece's net has no arm that grows and
   no line (k x APART from its hub) crosses it, the link searches the piece's dry ground, breadth first from
   every plot of the net, to the nearest plot on a line not yet a corridor; the way is straightened into legs
   (straight and 45 degrees, at most two to each reach) and laid plot by plot like any arm's step: drawn,
   counted, told to the witness, taken back newest first. Where it arrives, the line's two arms open. One
   link at a time per piece; a link opens only where a way was found, so it leaves no stub. The search is
   capped at `LINK_SEARCH` plots (2^21) and runs only when a net is stuck, once per ground version. The first
   try, slice A's straight perpendicular link, failed on the playtest's coast: 44 links of 1 to 130 plots
   left the hub for lines across the water, and 299 plots stayed unlaid. Hence slice C.
2. **A net on every piece** (CityRuns `Pieces`, `layHighways`; CityMap `MapPieces`). The city's owned ground
   in pieces at 32-plot tiles (a tile is land when any of four samples is owned dry ground; eight ways),
   union-find over chunks, a purchase joining only its new tiles (0.3 ms on the 5-billion copy; the first
   flood about 1 s). Each piece with built ground opens a net from its hub (its tile nearest the founding
   site with a dry highway crossing): its own spine corridors in the same highways net, so every reader
   (fill, the far view's key, the witness) draws it unchanged. The model's plots are dealt by built ground,
   the home piece first; a share steers where new plots go and nothing is taken back unless the model's
   total falls (slice B first dealt every month, and moved plots between pieces as the weights drifted:
   PlanCheck's crescents went red, and 5B paid for it). A purchase that joins two pieces moves nothing: the
   younger net is frozen as it stands.
3. **The sidecar** is FORMAT 12: each corridor's piece and role (SPINE, CROSSING, LINK) and the pieces' hubs;
   one of FORMAT 11 reads as one piece. The link's plan is never saved: read back, it plans again.

The railway is unchanged: since FR7 it grows its main line alone, no corridors, so the link has nothing to
do for it; and the railway on other pieces is not built (CL5-C).

## Checks

- **IslandNetCheck** (new, in the suite): 1 a stuck net (premise: short 719 with links off; 0 with them);
  1b a link through a 6-plot neck 300 long (short 619 off, 0 on; the link two legs); 2 two islands get a net
  each, none across the sea (premise: with one piece the far island has none); 3 newest-end growth and trim
  per piece; 4 a purchase joining pieces moves nothing; 5 the sidecar, a part-grown link read back and grown
  on; 6 a ground where today's rule never sticks lays the same with links on and off; 7 a real CityMap on
  three islands, its pieces checked by brute force, each with highway, and the pieces' timing at 5B.
- **The playtest's trace** is main's but for its run time and the map's CL3 layout watch (323,399 boxes
  compared on main, 323,231 now, highways taking ground): 0 boxes moved, the watch green. The model's figures
  are byte-identical, so **no ensemble** (EN1).
- **On the playtest's city** at month 4,005: highway plots laid 1,687 of 1,687 (never laid 0), 10 corridors;
  built districts with a highway 9 of 13 (3 of 13 on main); the other island's district has one.
- The suite and the timing: see the session's report (the suite's reds on main were timing only).

## Renders

`CL5/playtest-before.png` (main 0.8.28) and `CL5/playtest-after.png`: the default playtest's city, 1,024 plots
a side, 2 px a plot, by `docs/studies/city-layout/RenderCL1.java` on a kept copy (`-Dplaytest.keep`), never
his slots. MapCheck writes no PNGs, so the brief's three-island render is IslandNetCheck section 7's city,
checked by assertion, not drawn.

## Calls for Jerus (CL5-C)

1. **The railway on other islands**: its own track and yards from each piece's hub now, or wait for ET5's
   ports, which make an unlinked island hold a port alone?
2. **A link never bridges**: its search stops at fresh water. Should it bridge as the arms do (420 m)?
3. **By eye**: the map at month 4,000 - the north half's new highways, and a 45-degree link that crosses
   built blocks (laid through districts already built, dated as FX7 lays runs; the buildings stay).
4. **The cost of a stuck big net**: a search that finds nothing on a huge stuck piece runs to its cap (about
   1.3 s at 5B), once per purchase while it stays stuck. Never seen in a run; a cheaper retry if it is.
