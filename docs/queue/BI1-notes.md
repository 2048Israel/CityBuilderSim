# BI1 - the Bank page's integrity cards on float dust

Built by a Claude (Sonnet) session on `batch/BI1`, cut from main 01d93949b (0.9.165), merged with main
2677b1580 (0.9.166) before the checks below. Versioned: `Bank` gains public getters, so it has a `{next}`.

## Found

HL1's notes (*The checks*, `docs/queue/HL1-notes.md`:252) saw, on main as on HL1, the Bank page's two integrity
cards light on KeptCheck's city of 4,820 months. The test lived in `ui/BankScreen.java`, an absolute $5:

- `actionCards()`: `Math.abs(bank.equitySplitResidual()) > .005` (the card "Its two parts do not add up to its equity")
  and `Math.abs(off) > .005 || (bank.knowsYearAgo() && Math.abs(offThen) > .005)` ("Its lines do not add up").
- The Balance sheet page's "Not accounted for" line (`sheetColumn`, the same two figures) held a copy of the second.

`.005` is $5 (money is in thousands). A double's own step is .002 at 1e13 and .008 at 4e13, and the sheet's
residual is a signed sum of a dozen figures (assets by line, liabilities by line, equity): a few steps of dust
passes $5 on a book of that size. The audit never judged a residual so: `LongPlaytest.audit()` and a dozen
harnesses read `Math.abs(residual) <= MoneyAudit.CENT || relative() <= 1e-7`, a cent or a part in ten million.

## Built

In the model, `Bank` (after `yearAgoResidual()` and `equitySplitResidual()`), all public so the screen reads and
recomputes nothing:

| member | what |
|---|---|
| `INTEGRITY_RELATIVE = 1e-7` | the dial, one sentence above it: a part in ten million of the book's size, the audit's own |
| `integrityTolerance(size)` | `max(MoneyAudit.CENT, INTEGRITY_RELATIVE * size)` |
| `addsUp(residual, size)` | `|residual| <= integrityTolerance(size)`; a residual or size that is not a number does not add up |
| `sheetSize()`, `yearAgoSheetSize()`, `equitySplitSize()` | the figures' size: every line's magnitude (the sum each carries its rounding into) |
| `sheetAddsUp()`, `yearAgoSheetAddsUp()`, `equitySplitAddsUp()` | the verdicts; a year not on file, or no split kept (an old save), adds up |

In the screen, `ui/BankScreen.java`: the two cards read `!bank.equitySplitAddsUp()` and
`!bank.sheetAddsUp() || !bank.yearAgoSheetAddsUp()`; the Balance sheet page's "Not accounted for" line reads the
same. The cards' text still prints `m(bank.equitySplitResidual())`, `mFull(off)`, `mFull(offThen)`: the figures
are unchanged, only whether the card shows.

In the harness, `BankCheck` section "(13) the integrity cards read the book's size", `theIntegrityCardsReadTheBooksSize()`,
banner above it, by hand-built banks (`new Bank()`, `refresh`, `setMortgageBook`, `setSectorLoans`, `setCash`; the
year ago through `restoreSheetYear`, the split through `restoreEquitySplit`: the save's own paths; no `Game`, no save):

1. The rule is the audit's at sizes 1 to 1e15, either sign, against the audit's own expression; NaN does not add up;
   a bank with no split or no year (an old save) reads both as adding up.
2. A small bank (a sheet of 1,930): its equity's parts $6 out and its year-ago sheet $6 out: both cards show; its
   sheet now adds up. Twice the small book's bound (under the old $5) shows, half of it adds up.
3. A book of 1.5e13 (assets 2.9e13) whose own sums leave dust past the old $5 (found among 400 fractions: the
   model's arithmetic, a sheet .0078 out, one step of a double at 5.8e13): `sheetAddsUp()` is true; the same dust
   filed as the year ago and one step in the split: neither card shows. The fixture asserts the old absolute test
   would have lit it.
4. The same book truly out, twice its bound ($5.8 billion on a 5.8e13 book): the year-ago sheet and the split show;
   half the bound does not; out the other way shows.

Mutation: with `integrityTolerance` put back to the old `.005` the section fails 8 assertions: the three dust ones
(sheet now, sheet a year ago, the split), "the page's test is the audit's", and the book-size premises and the
bound-sized cases that follow from them. With the new rule: 0 FAIL.

## The trace

The cards are read only by the screen: no model number moves. The default playtest, traced (`-Dplaytest.trace`,
both data files named, one JVM each) on main 2677b1580 (0.9.166) built apart and on this branch merged with it:

- All eight CSVs are byte-identical (`month`, `borrow`, `house`, `pop`, `bank`, `bonds`, `build`, `labour`:
  2,405 lines each, 34 for `borrow`; 965,996 bytes `month`, 5,275,413 `bonds`).
- The log differs in ten lines and nothing else: the run time (1,643 s against 1,645 s, in two lines), one
  object's identity hash (`LandParcel@792b749c` against `@57bc27f5`, month 1320's works decision) and two
  wall-clock stamps (`[14:22:10]`) on a currency-purchase line. Both runs ended `All checks passed.`, exit 0.
- So the ensemble does not run (docs/contract.md, section 4: an unchanged trace is said in the notes).
- Main then moved twice more: 0.9.167 is HS2, which moves the playtest. It was merged without a third trace:
  HS2 touches no `Bank*` file (`git diff --name-only HEAD...origin/main`), and BI1's diff adds getters only the
  screen calls, so what BI1 does to the trace cannot depend on the path under it. The logs and CSVs are kept
  (where, below).

## The checks

- `./batch-check.sh --fast`: green on the final tree (main 0.9.167 merged): compile, maps regenerated, Stale 0
  firm, tree only the generated docs.
- `./batch-check.sh BankCheck` on 0.9.166's merge (the name selects three: `BankCheck`, `CentralBankCheck`,
  `YoungBankCheck`): `BankCheck` passed, 0 FAIL, the new section included; the other two known red only, as main's
  (`docs/notes/known-reds.txt`). The call ran 1,455 s, almost all `CentralBankCheck`, its start held by the memory
  gate under the other sessions' load.
- `BankCheck` alone, on the final tree (0.9.167 merged; one JVM, 34 s): the new section 0 FAIL, its 27 lines OK; the run's one
  FAIL is `it pays its savers something` (the played bank's savers' rate), a row of main's own, FS9 (`known-reds.txt`:
  "The played bank's own book: on HS2's path it loses on its trading in m121-144"). Not BI1's: the line is HS2's path.
- The new section against the old test: `Bank.integrityTolerance` put back to `.005` for one run, 8 FAIL (above).
- The pick (`sh tools/pick.sh origin/main HEAD`): 40 of 229 harnesses, not ALL, so no dry run. Review line: money
  (`Bank.java`, `ui/BankScreen.java`). No saves, no threading.

## Calls

- **The floor is `MoneyAudit.CENT`, not the old $5.** The row says "as the audit's is (a cent and a part in ten
  million)", so the test is the audit's, and CENT is $0.26 at the default price level ($10 at today's, a twin).
  Consequence, said plainly: on a SMALL book (a sheet under about 2.6e3 units, $2.6M) a miss between a cent and $5
  now shows, where the old test let it by; on a book over about 5e4 (a sheet of $50M) the bound is over $5 and grows
  with it. A played bank's own residual is under 1e-5 (BankCheck sections 13), far under a cent, so no real city
  lights. The alternative, keeping `.005` as the floor (`max(.005, 1e-7 * size)`), differs only on small books;
  Jerus overturns it by one constant if he wants it.
- ★ **The size is the sum of the figures' magnitudes**, not `totalAssets()`: float dust is relative to what the sum
  adds, and a split of a large retained and a negative part, or an allowance (negative on the page), would otherwise
  read small. At most twice the book: the bound is a part in ten million either way.
- ★ **NaN shows.** `<=` against a bound that is not a number is false, so a residual or size that is not a number
  lights the card; the old `>` hid it. "That should be impossible - worth reporting" is what the card says.
- **The Balance sheet page's "Not accounted for" line moved with the card** (`sheetColumn`): the same test on the
  same two figures, left alone it would show a red line under no card. Not in the row's words, in its meaning ("one
  rule, one place").
- **Not touched, same kind, seen:** `BankScreen` tests the equity movement's residual (`mv.residual()`, `equitySteps()`,
  the "Something moved its equity without telling it" alert and its statement line) against the same absolute `.005`.
  It is a different identity (the month's change in equity against its causes), so it is queued as BI2, not mended
  here: a card the row did not name, and its figures (`Bank.EquityMovement`) need their own size.
- **Hand-built banks, not KeptCheck's city.** The finding's city is 4,820 months of a long suite harness; the section
  causes the same condition (a few steps of a double on a book of 1e13) in milliseconds. The screen's two lines are
  one-line reads of the getters: KeptCheck reads the page for equality, not for these cards.

## Eye check (not pending work)

Open the Bank tab on a big city (KeptCheck's, or any past about 1e13 of assets): no red "Its lines do not add up"
or "Its two parts do not add up to its equity" card, and no "Not accounted for" line on the Balance sheet page. On a
small young city the cards stay absent while its figures foot.

## Where the kept runs are

No dry run (the pick is not ALL). The trace logs and CSVs of both builds are in
`target/trace-main/` (main 2677b1580) and `target/trace/` (this branch) of this worktree, untracked: `main.log`,
`mine.log` and the eight CSVs each. The two commits compared: main 2677b1580 (0.9.166) and this branch at f7d52f503 (its code, merged with
0.9.166 at d90e6cef4). No dry run was kept.
