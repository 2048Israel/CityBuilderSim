# FB7 - counters read under load

Bundled on 2026-11-16 from 2 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** SS1, GR2-R, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `SaveSlotCheck.java`, `ReplayCheck.java`, `ui/ReplayMoveProbe.java`.
- **Moves the playtest:** no: harnesses and a probe.
- **Not beside:** none; it shares no file with another bundle.
- **Known reds:** the 3 entries filed under these rows are filed under FB7 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### SS1

SaveSlotCheck's held-open swap counter assertions on Windows. Trace and isolate the successful waited writes counted between its snapshots. Preserve the successful, refused and intact-file assertions. Done means both approved counter assertions hold under concurrent load

Brief: [GR2 notes](GR2-notes.md#historical-approvals-and-follow-ups)

### GR2-R

ReplayCheck's negative worker-ask delta during a loaded glide, with zero unpainted frames. Trace the counter sampling in ReplayMoveProbe and cause the case in a fixture. Done means valid nonnegative deltas and the loaded-glide zero-asks assertion holds

Brief: [GR2 notes](GR2-notes.md#historical-approvals-and-follow-ups)
