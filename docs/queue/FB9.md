# FB9 - the map's renderer and MapCheck's fixtures

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** M6-F, BF1-M, DM3-R, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `MapCheck.java`, `CityMap.java`, `DistrictPlan.java`, `TileRaster.java`, the map's drawing in `ui/`.
- **Moves the playtest:** no: the map only.
- **Not beside:** FB5, FB8 (`MapCheck`, `CityMap`, `DistrictPlan`).
- **Known reds:** the 5 entries filed under these rows are filed under FB9 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### M6-F

MapCheck 14's scattered boxes do not guarantee every offer is hit and missed: cause those conditions explicitly, retaining all 200 scatter/oracle checks and the original premise

Brief: [brief](GDP1-notes.md#mapcheck-offer-fixture-follow-up-m6-f)

### BF1-M

A connected district plan leaves two joined-by-owned-ground street components in the drawn raster; cause the covered crossing/curve and mend MapCheck 8 without moving buildings or relaxing network rules

Brief: [BF1 map finding](BF1-map.md)

### DM3-R

Repair the unchanged map renderer's road-surface loss, six ordinary buildings beyond REACH on HW1's played-city path, the m2389 standing-box movement and the post-verdict JavaFX shutdown diagnostic while preserving the hard drawing laws

Brief: [cause and done condition](HW1-notes.md#map-renderer-follow-up-dm3-r)
