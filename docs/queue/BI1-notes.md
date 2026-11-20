# BI1 - the Bank page's integrity cards on float dust

Built by a Claude (Sonnet) session on `batch/BI1`, cut from main 01d93949b (0.9.165), merged with main 0.9.166,
0.9.167 and GB3's 0.9.167 tools. Versioned: `Bank` gains public getters, so it has a `0.9.168`.

## Found

HL1's notes (*The checks*, `docs/queue/HL1-notes.md`:252) saw, on main as on HL1, the Bank page's two integrity
cards light on KeptCheck's city of 4,820 months. The test lived in `ui/BankScreen.java`, an absolute $5:

- `actionCards()`: `Math.abs(bank.equitySplitResidual()) > .005` (the card "Its two parts do not add up to its equity")
  and `Math.abs(off) > .005 || (bank.knowsYearAgo() && Math.abs(offThen) > .005)` ("Its lines do not add up").
- The Balance sheet page's "Not accounted for" line (`sheetColumn`, the same two figures) held a copy of the second.

`.005` is $5 (money is in thousands). A double's own step is .002 at 1e13 and .008 at 4e13, and the sheet's
residual is a signed sum of a dozen figures (assets by line, liabilities by line, equity): a few steps of dust
passes $5 on a book of that size.

**Which of the audit's rules** (REV-BI1's finding 1). The row says "as the audit's is (a cent and a part in ten
million)". That is `LongPlaytest.audit()`'s rule for a month's FLOW (`Math.abs(residual) <= MoneyAudit.CENT ||
relative() <= 1e-7`, read against what moved). For comparing a money STOCK the suite's rule is
`MoneyAudit.tolerance(size)`: a cent, or a part in a trillion (`RELATIVE_TOLERANCE` 1e-12) of the figures' size,
never under `ULP_STEPS` (64) steps of a double there. The first push took the flow's; on KeptCheck's 5.8e13 sheet
a part in ten million is $5.8 billion, so a line left off the page, or counted twice, stays dark. The page takes
the stock's rule: the bound there is $57,900, 7,411 times the measured dust and a trillionth of the sheet.

## Built

In the model, `Bank` (after `yearAgoResidual()` and `equitySplitResidual()`), all public so the screen reads and
recomputes nothing. No dial of its own: the rule's dials are `MoneyAudit.CENT`, `RELATIVE_TOLERANCE` and
`ULP_STEPS`, in one place.

| member | what |
|---|---|
| `addsUp(residual, size)` | finite residual and size, and `|residual| <= MoneyAudit.tolerance(size)`; NaN or an infinity does not add up |
| `sheetSize()`, `yearAgoSheetSize()`, `equitySplitSize()` | the figures' size: every line's magnitude (the sum each carries its rounding into) |
| `sheetAddsUp()`, `yearAgoSheetAddsUp()`, `equitySplitAddsUp()` | the verdicts; a year not on file, or no split kept (an old save), adds up |

In the screen, `ui/BankScreen.java`: the two cards read `!bank.equitySplitAddsUp()` and
`!bank.sheetAddsUp() || !bank.yearAgoSheetAddsUp()`; the Balance sheet page's "Not accounted for" line reads the
same. The cards' text still prints `m(bank.equitySplitResidual())`, `mFull(off)`, `mFull(offThen)`: the figures
are unchanged, only whether the card shows. Only `BankScreen` calls the getters (grep of `src/main`): nothing in
the month, the audit or the save reads them.

In the harness, `BankCheck` section "(13) the integrity cards read the book's size", `theIntegrityCardsReadTheBooksSize()`,
banner above it, by hand-built banks (`new Bank()`, `refresh`, `setMortgageBook`, `setSectorLoans`, `setCash`; the
year ago through `restoreSheetYear`, the split through `restoreEquitySplit`: the save's own paths; no `Game`, no save):

1. The rule is `MoneyAudit.tolerance()` at sizes 1 to 1e15, either sign, a cent at a size where a part in a trillion
   is under one and a part in a trillion past it; NaN and infinities (residual or size, `addsUp(Inf, Inf)` too) do
   not add up; a bank with no split or no year (an old save) reads both as adding up.
2. A small bank (a sheet of 1,930, its bound a cent, $0.26): its equity's parts $6 out and its year-ago sheet $6 out:
   both cards show; its sheet now adds up. Twice the small book's bound (under the old $5) shows, half of it adds up.
3. A book of 1.5e13 (assets 2.9e13) whose own sums leave dust past the old $5 (found among 400 fractions: the
   model's arithmetic, a sheet .0078 out, one step of a double at 5.8e13): `sheetAddsUp()` is true; the same dust
   filed as the year ago and one step in the split: neither card shows. The fixture asserts the old absolute test
   would have lit it, and that the bound is over a thousand times the dust (7,411).
4. The same book truly out, twice its bound ($115,800): the year-ago sheet and the split show; half the bound
   does not; out the other way shows.
5. (the review) A line of $1 billion left off, or counted twice, on that 5.8e13 sheet: both cards show. The fixture
   asserts it is past the bound and under a part in ten million of the sheet.

Mutations, each run once and put back:
- `addsUp` as the first push had it (`max(CENT, 1e-7 * size)`, no finite guard): 8 FAIL: the $1 billion pair, the
  three bound-sized cases, the grid, the trillionth line and the infinity line. The rule at the review's finding 1.
- The money rule without the finite guard: 1 FAIL, the infinity line (`addsUp(Inf, Inf)` was true).
- The old absolute `.005` (before the review): 8 FAIL, the dust cases among them.
- The rule as built: 0 FAIL.

## The trace

The cards are read only by the screen: no model number moves. The default playtest, traced (`-Dplaytest.trace`,
both data files named, one JVM each) on main 2677b1580 (0.9.166) built apart and on this branch merged with it:

- All eight CSVs are byte-identical (`month`, `borrow`, `house`, `pop`, `bank`, `bonds`, `build`, `labour`:
  2,405 lines each, 34 for `borrow`; 965,996 bytes `month`, 5,275,413 `bonds`).
- The log differs in ten lines and nothing else: the run time (1,643 s against 1,645 s, in two lines), one
  object's identity hash (`LandParcel@792b749c` against `@57bc27f5`, month 1320's works decision) and two
  wall-clock stamps (`[14:22:10]`) on a currency-purchase line. Both runs ended `All checks passed.`, exit 0.
- So the ensemble does not run (docs/contract.md, section 4: an unchanged trace is said in the notes).
- Main then moved (0.9.167 is HS2, which moves the playtest) and the review's two mends changed `addsUp`'s body
  after the run. Neither is traced again: HS2 touches no `Bank*` file, and the getters stay screen-only (above), so
  what BI1 does to the trace cannot depend on the path under it or on the rule inside them.

## The checks

- `./batch-check.sh --fast`: green on the final tree: compile, maps regenerated, Stale 0 firm, tree only the
  generated docs and the three sources.
- `./batch-check.sh BankCheck` on 0.9.166's merge, before the mends (the name selects three: `BankCheck`,
  `CentralBankCheck`, `YoungBankCheck`): `BankCheck` passed, 0 FAIL, the new section included; the other two known
  red only, as main's (`docs/notes/known-reds.txt`). The call ran 1,455 s, almost all `CentralBankCheck`, its start
  held by the memory gate under the other sessions' load.
- `BankCheck` alone (`java ... ham.citybuildersim.BankCheck`, one JVM, under a minute), on the final tree after the
  mends: the new section 0 FAIL, its 31 lines OK; the run's one FAIL is `it pays its savers something` (the played
  bank's savers' rate), a row of main's own, FS9 (`known-reds.txt`: "The played bank's own book: on HS2's path it
  loses on its trading in m121-144"). Not BI1's.
- The pick (`sh tools/pick.sh origin/main HEAD`): 40 of 230 harnesses, not ALL, so no dry run. Review line: money
  (`Bank.java`, `ui/BankScreen.java`). No saves, no threading.
- REV-BI1 on add76c170: SHIP, no P1; findings 2 to 5 none; two notes mended above (the rule, the infinity).

## Calls

- **The rule is `MoneyAudit.tolerance(size)`** (REV-BI1, the orchestrator's: "as the audit's is" means the stock's
  rule, one rule in one place). My first push used a part in ten million and added a dial,
  `Bank.INTEGRITY_RELATIVE`; both are gone. Nothing of its own is left in `Bank` but the verdict and the size.
- **The floor is `MoneyAudit.CENT`, not the old $5** (the orchestrator confirmed it stands as recommended). CENT is
  $0.26 at the default price level ($10 at today's, a twin). Consequence, said plainly: on every book under about
  2.6e8 units (a sheet of $260 billion, where a trillionth of it and 64 steps of a double are both under a cent, so
  the bound is the cent exactly) a miss between a cent and $5 now shows, where the old test let it by. A played bank's own residual is
  under 1e-5 (BankCheck, section 13), far under a cent, so no real city lights. Keeping `.005` as the floor would
  differ only there; one constant.
- ★ **The size is the sum of the figures' magnitudes**, not `totalAssets()`: float dust is relative to what the sum
  adds, and a split of a large retained and a negative part, or an allowance (negative on the page), would otherwise
  read small. At most twice the book; the bound is a part in a trillion of it either way.
- ★ **NaN and infinities show** (finite guard, REV-BI1's finding 2): `<=` against a bound that is Inf is true, so
  `addsUp(Inf, Inf)` passed; and a residual or size that is not a number made the old `>` hide it. Both light the
  card now: "That should be impossible - worth reporting" is what it says.
- **The Balance sheet page's "Not accounted for" line moved with the card** (`sheetColumn`): the same test on the
  same two figures; left alone it would show a red line under no card. Not in the row's words, in its meaning ("one
  rule, one place").
- **Not touched, same kind, seen:** `BankScreen` tests the equity movement's residual (`mv.residual()`, `equitySteps()`,
  the "Something moved its equity without telling it" alert and its statement line) against the same absolute `.005`.
  It is a different identity (the month's change in equity against its causes), so it is queued as BI2, not mended
  here: a card the row did not name, and its figures (`Bank.EquityMovement`) need their own size.
- **Hand-built banks, not KeptCheck's city.** The finding's city is 4,820 months of a long suite harness; the section
  causes the same condition (a few steps of a double on a book of 1e13) in milliseconds. The screen's lines are
  one-line reads of the getters: KeptCheck reads the page for equality, not for these cards.

## Eye check (not pending work)

Open the Bank tab on a big city (KeptCheck's, or any past about 1e13 of assets): no red "Its lines do not add up"
or "Its two parts do not add up to its equity" card, and no "Not accounted for" line on the Balance sheet page. On a
small young city the cards stay absent while its figures foot.

## Where the kept runs are

No dry run (the pick is not ALL). The trace logs and CSVs of both builds are in `target/trace-main/` (main
2677b1580) and `target/trace/` (this branch) of this worktree, untracked: `main.log`, `mine.log` and the eight CSVs
each. The two commits compared: main 2677b1580 (0.9.166) and this branch at f7d52f503 (merged with 0.9.166 at
d90e6cef4), before the mends.
