# FB6 - KeptCheck's fixtures cause their own conditions

Bundled on 2026-11-16 from 5 Queued rows (Jerus: "Yes, bundle them"), so fixes
that share a harness or a file go through one build, one review and one dry run,
not one each. WHY: over 11-14 to 11-16 a fix batch held the dry-run slot 34
minutes on average, most of them for the whole suite.

- **Rows:** KS2, KS1-Saved, KBF1, KHT1, KF63, below as QUEUE.md had them; their briefs stand.
- **Files it expects to change:** `KeptCheck.java` (sections 44, 51, 63, 66, 67, 87, 89), `SavedCities.java` if 87's trace leads there.
- **Moves the playtest:** no: the harness only.
- **Not beside:** FB2 (`KeptCheck`).
- **Known reds:** the 2 entries filed under these rows are filed under FB6 now, so its ship must turn them green.
- **Done** means each row's own done condition. A row that proves bigger than a fix
  leaves the bundle before the gate: the orchestrator files it back under its own
  id, with its known-red entries, in a queue commit.

## The rows

### KS2

KeptCheck 51, 66 and 67 on RB1's path: three fixtures look for their condition in KeptCheck's city instead of causing it - section 51 a business whose figures move within twelve months ("no month in twelve moved the figures"), 66 a debt to close (`longestPiece()` finds none), 67 a bookRow on the Finances hub ("has no bookRow this month"). RB1 moves the city's path (the fund sells its excess and pays its idle cash to the budget) and none of the three is there when KeptCheck looks. Done means each fixture causes its condition (borrow a piece of paper, open a book row, a business whose month moves) and the three are green on RB1's path

Brief: [RB1 notes §7](RB1-notes.md)

### KS1-Saved

Trace KeptCheck 87's successful scratch save followed by zero saved cities on the board, including worker completion and SavedCities omission reasons. The unchanged main classes reproduced the assertion.

Brief: [KS1 saved-city finding](KS1-notes.md#saved-city-baseline-finding)

### KBF1

Cause stable auction coverage in KeptCheck 44's funding-page monthly reuse fixture and explicitly cause the first shortfall controls, preserving the 90% floor.

Brief: [BS1 notes](BS1-notes.md#kbf1)

### KHT1

Cause populated cash-grid cells to persist across KeptCheck 89's all-households tooltip month, preserving the tooltip identity and zero-loss assertions.

Brief: [BS1 notes](BS1-notes.md#kht1)

### KF63

Cause stable holding/review coverage in KeptCheck 63 while preserving its hard 90% monthly retention floor and kept/fresh equality.

Brief: [PO1 notes](PO1-notes.md#kf63)
