# FW3 - a river's far bank surfaced after the hub's side (0.8.39)

- **The note against today's code.** fixN3 §7 (0.7.72) found it in the tile deal: a road tile cut by a river
  counted its whole grid, so a gravel-only tile was dealt road its hub's bank could not hold. That deal went in
  0.7.88 (RD2): the model deals a district road plots by its room (`CityMap.placeRoad()`), and the district's plan
  surfaces them on its streets (`DistrictPlan.Builder.surface()`). What remained is the same fault one level up:
  `surface()` ran before `markParted()` and spread the road by cell rank over every street, so the far bank's
  parted streets (not on the city's network) took road while streets on the hub's side stayed tracks.
- **The fix** (`DistrictPlan`): `markParted()` runs before `surface()` and reads the streets themselves
  (`onStreet()`: a street, or beneath a highway - the plots that get a code), not their codes. `surface()` buckets a
  parted plot in a second block of the same buckets (`PARTED_BUCKETS`), and gives widths network first (half, then
  full), then the parted streets, then the one-sided seams (`widths()`). With none parted, or all (a district no
  anchor reaches), the order and every width are main's. Total road, surplus and the ladder are unchanged.
- **The check:** PlanCheck section 16, a 16-plot river (ARTERIAL_BRIDGE + 2) across a root district at row 192,
  gravel only: (a) 0.75 x the network's full width all on the network, none beyond (main: 883 of 5,293.5 beyond);
  (b) the network at full width plus half the far bank's: the network full, the far bank dealt the rest exactly.
  Red on main's `DistrictPlan` (2 FAIL), green on the fix; PlanCheck's digests of the older fixtures unchanged.
- **The trace:** nothing in the model reads the plan; the default playtest (4,005 months) is byte-identical to main's in all eight CSVs, its report but for its run-time lines. No ensemble (EN1).
- **Beside ET2/ET3:** the change is in `surface()`, `surfaceRow()` and `markParted()` only, and the build's last
  two calls swapped; an Earth batch that touches street laying merges around it.
