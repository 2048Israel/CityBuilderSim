# FB8 - DM2's three follow-ups, the map's sidecar

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** DM2-A, DM2-B, DM2-C, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `CityMap.java` (the sidecar, the rectangle codec), `MapCheck.java`.
- **Moves the playtest:** no: the map only.
- **Not beside:** FB5, FB9 (`MapCheck`, `CityMap`).
- **Known reds:** none filed under these rows.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### DM2-A

An actual-map watched/unseen twin that causes a wall and verifies dated discovery after first view

Brief: [DM2-A](DM2-notes.md#dm2-a)

### DM2-B

A causal site-mask mismatch recovery study and fixture before relaxing whole-sidecar refusal

Brief: [DM2-B](DM2-notes.md#dm2-b)

### DM2-C

Guard and document the rectangle codec's 256-plot district assumption beside DISTRICT

Brief: [DM2-C](DM2-notes.md#dm2-c)
