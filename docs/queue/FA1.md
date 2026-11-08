# FA1 - a frontage avenue along a diagonal highway (left by MF2)

Jerus's Bergen city, item 4 of MF2's brief (picture 3, `docs/queue/MF2/3-highway-cuts-blocks.png`, 2026-10-31): a highway arm
off the grid (CityRuns turns an arm 45 degrees at the sea; a link arm's legs) runs diagonally through built districts and
"cuts the blocks": the plan's blocks are a grid, grid streets meet the highway's verge with nothing there, and wedges of
owned ground beside it stay empty or wooded. MF2 caused it and did not mend it (MF2's notes, Bergen 4 and star 5).

**The fixture is ready:** `docs/queue/MF2/MF2DiagonalProbe.java`, red on main (0.9.49): a full district with a 45-degree
highway through it; 52 of 52 grid-street pieces that reach the verge end there with no street along it, and the strips
beside the verge are used 73.3% against 81.9% for the same plots with no highway (`STRIP_MARGIN` 0.02). It becomes
PlanCheck's next section the day the plan passes it. Its header holds the design and what it costs; in short:

- **The fix:** a frontage avenue along each side of a diagonal highway, just outside its verge, made with CL4's avenue
  machinery (a staircase street of ROLE_ARTERIAL that blocks are built along, meeting the grid at junctions kept by the
  + floor). Streets keep passing beneath the highway (UNDER), never across it at grade.
- **What MF2 found in the way:** CL4 lays an avenue when its cell opens, from inputs that never change, while a highway
  arm is a run laid month by month (THE RUNS DATED); the avenue tables key on four arms from one origin (`avKept` keys
  arm << 28 | step). The probe's design: the frontage line is the lattice-aligned diagonal nearest outside the verge, laid
  as a further arm of the tables with its own origin when the cell opens after the arm's plots are in `fx`; cells already
  open keep their grid and the frontage avenue takes the strip's free plots only (no box moves).

Map only: the model's trace must not move. PlanCheck's ms a plan against main's, no budget moved. Opus with slices
(`DistrictPlan`'s avenues, PlanCheck, the painter's avenue marks for a heading off the four arms).
