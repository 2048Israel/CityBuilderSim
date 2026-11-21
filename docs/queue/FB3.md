# FB3 - reloads that read as they were saved

Bundled on 2026-11-16 from 3 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** HZ1, R0f-S, RT2-L, below as QUEUE.md had them, and FW6-C, folded in from Pending on 2026-11-16; their briefs stand.
- **Files it expects to change:** `SaveFileCheck.java`, `CapitalFlows.java` (a named setter), `MoneyDatesCheck.java`, `sectors/Retail.java` and `Game`'s load path, `InvestCheck.java` and the invest contracts' loader; for FW6-C, `ReloadCheck.java`, the restores it names (the sudden stop's words, Quarrying's carry, `Markets.State`) and `GoodsMarket.java`'s draws list.
- **Moves the playtest:** no for HZ1, R0f-S and RT2-L (the load path only); FW6-C's seller-priced markets, if they are made to clear, would, and the ensemble's rules apply to that part.
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

### FW6-C

Run counters a load restarts with no reader (the bond book's sums, an ulp in `creditMoved`); the seller-priced markets never clear their trades or fills (0.8.46); from the orchestrator's review (ship): three restores have no harness that causes their case - the sudden stop's words, Quarrying's carry (produceFlow zeroes it before the month-on comparison, Quarrying about 276) and Markets.State's drawn - so deleting any of those restore lines leaves the suite green, and a SaveFileCheck round trip each would close it; ReloadCheck 3f asserts only skips > 0 (about 469), not that an outbreak or sick month occurred; FW5's draws list (GoodsMarket about 106) is still unsaved, so a hand order made between the presses loses its seller row after a load (screens only)

It waited: the map is CC1-M's area; the rest is harmless

Brief: [FW6-notes](FW6-notes.md) §5-6
