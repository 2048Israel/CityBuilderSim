# FB3 - reloads that read as they were saved

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** HZ1, R0f-S, RT2-L, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `SaveFileCheck.java`, `CapitalFlows.java` (a named setter), `MoneyDatesCheck.java`, `sectors/Retail.java` and `Game`'s load path, `InvestCheck.java` and the invest contracts' loader.
- **Moves the playtest:** no: the load path only, an unbroken run is unchanged.
- **Not beside:** FB4 (`SaveFileCheck`).
- **Known reds:** the 2 entries filed under these rows are filed under FB3 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### HZ1

Low: SaveFileCheck places the hot-money arrival through raw save-array indices (`CapitalFlows.toSaveArray()` [0], [3], [16], SaveFileCheck.java:1542). A setter named for the field, so a reordering of the array cannot move the fixture silently

Brief: [GW1 notes §4](GW1-notes.md)

### R0f-S

Keep the month-2 early city's Retail.wantedSpend, rent and whole foreign-account twin identical after loading; unchanged main reproduces the first Retail.wantedSpend difference

Brief: [R0f-S.md](R0f-S.md)

### RT2-L

Infer legacy contracts using the loaded wage ladder and rebate rule; preserve payer, value and material laws, with a threshold-crossing save fixture

Brief: [RT2 notes, main-code proofs](RT2-notes.md)
