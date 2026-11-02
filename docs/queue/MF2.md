# MF2 - four latent map faults the bigger cities exposed

Queued 2026-10-31, settling XR4-M, WN3-M, EM1-M and HD1-M the recommended way,
"a map batch" each, on Jerus's "Take all (Recommended)" (BP1 item 9). One
batch: all four are MF1's class, the district plan and the map's placement
moving or blocking what stands, met by larger or longer cities than the
default playtest's, and latent on main's default path. Only the map reads
them: the model's trace does not move.

## The four

1. **XR4-M: a house district and old works moved at the run's end**
   ([XR4 notes §4](XR4-notes.md#4-the-checks)). On XR4's path LongPlaytest
   flagged "a building on the map moved, 2 time(s), months 3998-3999" (7
   houses of a district's 1,675 boxes) and "an old works moved, 1 time(s),
   months 4000-4000" (6 of 17 Rod Mills not where they stood at m1,001): the
   class FX7 dated the runs for and ET1's runs met on main.
2. **WN3-M: three faults on Historical's larger city**
   ([WN3-notes §5](WN3-notes.md), item 1; still there after MF1):
   (a) the one rail yard dealt by district order (`CityMap.yardsOf`), so a
   depot arriving earlier in the order moves the yard (m3995, district
   (-2,-3), 10 boxes moved); (b) ground bought inside a built district moves
   old works (m2078, district (0,0), 112 plots bought, a Rod Mill, a Meat
   Works and a Bottling Plant each lost a box; again m2323); (c) the undated
   interchange reserve (`FIXED_RESERVE`, 335 plots) laid across standing works
   (m2782, district (-1,0), 41 of 65 Construction Depots moved).
3. **EM1-M: a farm on its own field blocks a street**
   ([EM1-notes](EM1-notes.md)). `DistrictPlan.wallJoinable` refuses to cross
   any building on a site (`in.site != 0`, a farm's SITE_FIELD among them),
   where its doc names "a mine, a well". PlanCheck section 6's city on EM1's
   path walled a street piece in behind a farm's building on its field
   (district (0, 0), months 91 and 121). Decide whether a farm's building on
   its field is the way's to cross, as a mine and a well are not; PlanCheck
   now parts the ground there as the planner does (the orchestrator's call,
   2026-10-29), and goes back to the planner's rule if it changes.
   **Settled by LC3 ({next}, 2026-11-02):** it is. The re-baked nations file's
   played city walled two pieces in behind a belt of houses on its iron
   field (MapCheck 8 red); `wallJoinable()` now bars only SITE_MINED, a
   field's buildings are boxes like any other, PlanCheck's `groundParts`
   is back to the planner's rule, and PlanCheck 18 is the fixture
   (`docs/queue/LC3-notes.md` §5). Three faults are left here.
4. **HD1-M: three Convenience Stores moved when one of their type left**
   ([HD1-notes](HD1-notes.md)): LongPlaytest's layout watch at m3,998,
   district (-1,-2), drawn 623 to 622, outside the spill; on HD1's
   coupon-and-posts run (3dea900f with fec704a7, the default playtest).

## How

A harness is the finding: each fault caused first by a fixture of its own
(a MapCheck or PlanCheck section, MF1's way), since the paths that met them
may not reproduce on today's main; then fixed at its cause. LongPlaytest's
layout watch is not weakened, and flags nothing on the paths that met them
where they still run. Time: MapCheck's month lines and PlanCheck's ms a plan
against main beside the same load; no budget moved. QB1 (CC1-S-B) waits for
this batch, since PlanCheck is the district plans'.

## Jerus's Bergen city, 2026-10-31 (month ~2,418, 0.8.90)

He sent pictures of his own city and ticked four faults to fix
(`docs/queue/MF2/`). The autosave they came from (month 2,413, 0.8.90:
`autosave.json`, `-history.json`, `-map.bin`) is a read-only fixture at
`C:\Users\Jerus\Documents\cbs-fixtures\bergen-pictures-2026-10-31\save\`,
outside the repository. Copy it into a `GameFiles.scratch` folder to load it;
never into a slot. Each fault gets a fixture that causes it; his city shows
it is real.

1. **Bridges as staircases** (pictures 2, 4): a highway and a railway cross
   the sea on a brown stair-stepped strip, not a straight deck.
2. **The broken cloverleaf** (picture 4): the loops are loose arcs not joined
   to the highway's lanes, and a diagonal street runs through the middle.
   This overturns MF1-C star5 ("the cloverleaf stylised") as far as the joins
   go: the loops meet the lanes they serve, and no street crosses the
   interchange.
3. **A rail V-turn to a dead end** (picture 1): the railway comes diagonally
   through the blocks, turns back sharply at a small green square, crosses
   the street and stops in empty ground.
4. **The highway cuts the blocks** (picture 3): the diagonal highway crosses
   the street grid with no junctions, leaving thin strips of empty and wooded
   ground.

Not a fault, his words: the city owning open sea, "i bought that open sea...
it had tasty oil lol."
