<!-- readiness: provisional -->
## changelog
### {next} — SQ1, a building stack past 2^31 — 2026-11-16, ready, see docs/queue/SQ1-notes.md
A building type's units merge into one stack, and the stack kept its quantity, its on-site count and its unfitted
counts in an int: a ten-billion city housed in Houses holds about 2e9 of them, past an int's 2,147,483,647, and
ScaleCheck's copy did not load where a stack times K passed it (found by ST3). The counts are a long now: the stack,
BuildingManager's sums, orders and retirements, the advisor's plan maps of buildings, and the saved arrays
(DataSave's buildings and underConstructionById, the unfitted counts); an old save's int reads into a long, so
there is no format bump. A stack driven past 2^31 by orders and by ScaleCheck.scale() reads back whole through the
save and the load, and its homes, posts and places are its quantity times the template's (BuildingStackCheck, red on
main's int). Below the ceiling nothing moves: the default playtest's eight trace files are main's, byte for byte.

## known-reds
KeptCheck | no month makes more than a tenth of the nodes the first draw made (the most made in one: 46 of 400; | FB2 | 2026-11-16 | Main 08c8727d's own classes, run from this worktree before any SQ1 change reaches them (AllChecks -f KeptCheck), reproduce 46 of 400 with 6 of 25 months made none in section 69, the Government Overview's transient rows; the recorded 47 of 378 and 40 of 384 are the same fault at the counts the trace had before LF1b moved it. Nothing of SQ1 draws a node
