# CD9b - CD9's review follow-ups (the foreign debt's) - notes

Brief: `docs/queue/CD9b.md`. Built by a Claude session (Sonnet 5.5, whole batch; the night's Codex builders had run out of
plan). A javadoc and a harness change: no game code, no save format, no trace to compare, so the batch is untagged
("ready, no tag (harness)").

## What was built

1. **`Debt.effectiveRateOf`'s javadoc** (`Debt.java`, the paragraph above `effectiveRateOf`) said `getYieldToMaturity()` "is
   left as it was (its bracket stops at nothing)". It now says what the method does since CD9: the same bisection with its
   bracket split at a price equal to the payments' total - 80 steps on [0, 5] for a price up to the total, on
   [`EFFECTIVE_RATE_FLOOR`, 0] over it, exactly 0 at it - against `effectiveRateOf`'s whole
   [`EFFECTIVE_RATE_FLOOR`, `EFFECTIVE_RATE_CEILING`] in 100 steps.
2. **`CurrencyCheck` section 6's two extreme-rate revaluation lines** (the old `highBook`/`dearBook` bare ledgers, which
   called `revalueForeign` themselves and asserted its own formula back) are replaced by a real city:
   - `dollarCity()`: a city built as sections 4 and 5 build theirs (400 houses, 6 convenience stores, 24 months), then one
     US$ bond sold through `Game.handleForeignLogic("Term", ...)`, the call the player's Borrow abroad makes (the books
     opened on what the sale raised). Fixture lines: the city owes dollars, on one foreign piece.
   - `extremeMonth(city, rate)`: the city's `ForeignAccounts` put at the extreme the way a save puts it
     (`toSaveArray()` / `restore()`), its `DebtManager` told the same rate as the load path does, one whole
     `simulateMonths(1)` played, then read: the line `NationalAccounts.getDebtExchangeGain()` holds (CD7's booked line),
     the pieces' own `carryingInCurrency()` times (rate it began on less rate it ended on), and whether every piece was told
     the rate the month ended on.
   - High extreme: 150 (past `OLD_MAX_RATE`); low extreme: .005 (under `OLD_MIN_RATE`), the same city one after the other.
     Lines: the fixture (start is past the old guard; the month moved the rate), the booked line equals the carrying times
     the whole move, its sign is the gain/loss the move makes, the pieces were told the month's end rate, the books' local
     principal is the dollars times that rate, and the history's `debtExchange` series (`debtExchangeSinceFounding()`, to
     the cent it keeps) holds the line(s).
   - The bare `ForeignAccounts` loops above them are kept: they prove the rate goes past the guards on parity's trend.

## What the checks showed

- `CurrencyCheck` section 6 on the new code: 0 fails (run through a scratch launcher on section 6 alone; the pick
  runs the whole harness, below).
- **The skipped revaluation, tried by hand three ways** (each restored with `git checkout`, `git diff origin/main` then
  shows only `CurrencyCheck.java` and `Debt.java`):
  - *A, the month's call skipped*: `Game.java:14046`'s `setDebtExchangeGain(debtManager.revalueForeign(...))` commented out.
    7 FAIL lines: high month `...the month's revaluation the whole of the move: the booked line is the dollars carried
    times the rate it left` (0 != 77,007.3969), `...and it is a gain, the D$ having risen from the extreme`, `...the pieces
    were told the rate the month ended on` (149.0978 != 0), `...and the dollar debt is worth the dollars at it, in the city's
    books` (77,650.39 != 467.03); low month the mirror's booked line (0 != -550.8944), `...and it is a loss, the D$ having
    fallen from the extreme`, `...the pieces were told the rate the month ended on` (1.0666 != 0).
  - *B, `DebtManager.revalueForeign` returns 0 at once*: the same 7 FAIL lines.
  - *C, `revalueForeign` books the line but does not tell the pieces (`setExchangeRate(rate)` skipped)*: 3 FAIL lines (the
    pieces were told, in both months, and the books' principal at the rate).
  - For comparison (by reading the old lines, not run): they would be red on B only; A passes them, since the check
    called `revalueForeign` itself - the fault this batch mends.

## Calls

- **The real city's month does not hold the extreme.** A real, seeded currency reprices to where the city's prices put it
  inside the month (150 -> 0.9022; .005 -> 1.0716), unlike the quiet bare `ForeignAccounts` above, which follows parity's
  trend for months. So the extreme is where the debt was last valued (as a save leaves it) and the revaluation books the
  whole move across it, and the fixture lines say so (`starts past the old guard`, not `ends past it`). The direction
  therefore is a gain at the high start and a loss at the low start - the old bare lines had the mirror (loss, gain). A
  fixture that held a real city's rate at 150 would need prices 150 times the world's, a hyperinflation another harness's
  premise, not this one's. Recommended and taken: keep the bare-loop lines for the rate past the guard, the real city for
  the booking.
- Assert against the pieces' own `carryingInCurrency()`, not the face: a sold piece is carried at what it raised
  stepped by its effective interest, and the carrying is what `revalueForeign` is defined on (CD7). The constants the
  lines are read against are the section's own and the model's: the old guards (`OLD_MAX_RATE`, `OLD_MIN_RATE`), the
  rates the city's own `ForeignAccounts` reports, and the history's cent (`HISTORY_CENT`).
- Process lines: none changed (`git diff --numstat` over scripts/rules is nothing).
- Trace: unchanged by construction (a javadoc and a harness); no ensemble run.

## The pick

`./batch-check.sh --pick` on `16df28dff` (the code), 2026-11-16: exit 3. 34 of 209 harnesses selected (`sh tools/pick.sh
origin/main HEAD`: "review needed - money: Debt.java"), 35 run in 1,319 s with TimingCheck, 34 passed, `CurrencyCheck` whole
among them (0 FAIL). The one red is `KeptCheck`, its two lines main already records (`the kept page and the new page are the
same picture at 3 months, to SHADE_LEVELS (2)`, BF1-K; `the kept header's signature equals a header built new, at pace and
short, at every rung`, KS1-UI): `known red only - KeptCheck`, nothing of this batch's. Maps regenerated and committed
(`docs/map/CurrencyCheck.md`, `Debt.md`, `PriceLevel.md` - CurrencyCheck now uses `PriceLevel.FACTOR` - `README.md`, the
harness and dial indexes); Stale 0 firm findings.
