# BI2 - the Bank page's equity-movement alert on float dust

Built by a Claude (Sonnet) session on `batch/BI2`, cut from main 865a7171c (0.9.168, BI1's merge). Versioned:
`Bank.EquityMovement` gains a public getter, so it has a `0.9.169`.

## Found

BI1's notes (*Calls*, "Not touched, same kind, seen") named it: `ui/BankScreen.java` tests the month's equity movement
against the same absolute $5 BI1 took off the two cards. Three sites, one test:

- `equitySteps()` (the waterfall, `BankScreen.java:4056`): the "Not accounted for" step is red when
  `Math.abs(residual) > .005`.
- `equityMovedCard()` (:4071): the alert "Something moved its equity without telling it" under the waterfall,
  `Math.abs(mv.residual()) > .005`.
- the Statement page (:4225): the "Not accounted for" line, red, and the same alert, on `Math.abs(residual) > .005`.

`.005` is $5 (money is in thousands). The residual is a signed sum of seventeen figures (`Bank.EquityMovement.residual()`:
the closing less the opening and every cause), each carrying its own rounding, and a double's step is .002 to .008 at 1e13:
a few steps of dust on a bank of 1e13 and more passes $5 (the bank's equity movement was measured at 57.7 steps in
ScaleCheck's copies of the playtest's city, `MoneyAudit.ULP_STEPS`'s own comment). The same absolute $5 also let a small
bank's real miss of a few dollars by.

`EquityMovement.scale()` (0.7.54: "the size of the figures residual() is made of, each as a magnitude: what
`MoneyAudit.tolerance()` reads it against") was already there, and `BankCheck`'s played city already read the residual
against `MoneyAudit.tolerance(1e-6, scale)`: the screen alone had never used it.

## Built

In the model, `Bank.EquityMovement` (the record, after `scale()`), one getter, no dial:

| member | what |
|---|---|
| `addsUp()` | `Bank.addsUp(residual(), scale())`: BI1's rule, `MoneyAudit.tolerance(size)` (a cent, or a part in a trillion of the size, never under 64 steps of a double there), with its finite guard: a residual or a size that is not a finite number does not add up |

In the screen, `ui/BankScreen.java`, the three sites read `mv.addsUp()` (the waterfall's red step on `!mv.addsUp()`, the
card's alert, the Statement's red line and alert on `!m.addsUp()`); the figures they print are unchanged (`m(...)`,
`mv(...)`, `money(Math.abs(residual))`): only whether the alert shows, and the step's colour. Only `BankScreen` calls the
getter (grep of `src/main`): nothing in the month, the audit or the save reads it.

In the harness, `BankCheck` section "(13) the equity-movement alert reads the movement's size",
`theEquityMovementAlertReadsTheMovementsSize()`, banner above it, by records built by hand (`new Bank.EquityMovement(...)`
from `movementFigures(scale, k, out)`: every cause a fraction `k` gives so their sums round, the closing what they make
of the opening plus `out`; no `Game`, no save):

1. The rule is `MoneyAudit.tolerance(scale())` at sizes 1e3 to 1e15, either sign, a cent at the small sizes and a part in a
   trillion at the big: half a bound adds up, twice it does not.
2. A small movement (scale 749, bound a cent, $0.26) out by twice its bound ($0.52, under the old $5): the alert shows,
   either sign; half the bound adds up; $6 out shows. The fixture asserts the old absolute test would have let the $0.52 by.
3. A movement of 3e13 (scale 4.5e13, one step of a double there .0078) whose own sums leave dust past the old $5 (found at
   the first fraction, k = 1: .0064, a few steps of a double at its closing's size; at 1.5e13 the same sums leave at
   most .0039 in 20,000 fractions, under $5, so the fixture is at 3e13): `addsUp()` is true. The fixture asserts the old
   test would have lit it, the dust is within 64 steps, and the bound ($44,940) is a thousand times the dust and under a
   thousandth of the scale.
4. The same movement truly out, twice its bound either sign: shows; half the bound adds up; a line of $1 billion left out
   of the causes or counted twice: shows (a part in ten million of this scale, the flow's rule, would be $4.5 billion and
   leave it dark).
5. An infinite residual (closing plus or minus infinity): shows. A missing opening (NaN): shows, which `>` against the old
   $5 hid.

And one line in the played city's loop (the existing section "(13) on a played city"): each of its 72 months' movements
adds up, so the alert never shows on a played bank.

Mutations, each run once and put back (BankCheck alone, the known `it pays its savers something` red aside):
- `addsUp()` as the old absolute `!(Math.abs(residual()) > .005)`: 7 FAIL, the two grids (every size, either sign), the
  $0.52 small miss and its mirror, the dust line, the big movement's half bound and the NaN line; the infinity lines pass
  (`Inf > .005` was true).
- `addsUp()` as the flow's rule, `|residual| <= max(CENT, 1e-7 * scale())`: 8 FAIL, the two grids, the big movement twice
  its bound, both ways, the $1 billion pair and both infinity lines.
- The rule as built: 0 FAIL in the section.

## The trace

The alert is read only by the screen: no model number moves. The default playtest, traced (`LongPlaytest`,
`-Dplaytest.trace`, both data files named, one JVM each, timing soft) on main 865a7171c (0.9.168), built apart in a
detached worktree, and on this branch at dd75a68dd (the same model code as the final tree):

- All eight CSVs are byte-identical (`month`, `borrow`, `house`, `pop`, `bank`, `bonds`, `build`, `labour`: 2,405 lines
  each, 41 for `borrow`; 924,572 bytes `month`, 5,225,176 `bonds`).
- The log differs in five lines and nothing else: the run time (1,157 s against 1,122 s, in two lines), one object's
  identity hash (`LandParcel@30f842ca` against `@4d15107f`, month 1800's works decision) and two wall-clock stamps
  (`[16:19:07]` against `[16:19:17]`) on a currency-purchase line. Both runs ended `All checks passed.`, exit 0.
- So the ensemble does not run (docs/contract.md, section 4: an unchanged trace is said in the notes).

## The checks

- `./batch-check.sh --fast`: green: compile, maps regenerated, Stale 0 firm, tree only the generated docs and the three sources.
- `BankCheck` alone (`java -cp "target/classes;$(cat target/classpath.txt)" ham.citybuildersim.BankCheck`, one JVM, 58 s),
  final tree: the new section 27 lines OK, 0 FAIL, and the played-city line OK; the run's one FAIL is `it pays its savers
  something` (FS9, `docs/notes/known-reds.txt`: main's own).
- The pick (`sh tools/pick.sh origin/main HEAD`): 40 of 230 harnesses, not ALL, so no dry run. Review line: **review: needed - money** (`Bank.java`,
  `ui/BankScreen.java`). No saves, no threading.

## Calls

- **The rule is `Bank.addsUp(residual(), scale())`**, BI1's: one rule in one place (`MoneyAudit.tolerance`), with BI1's
  finite guard, so the record adds nothing of its own to the rule and no dial.
- ★ **The getter is on the record**, `EquityMovement.addsUp()`, not on `Bank`: all three sites already hold the movement
  (`mv`, `m`) and the verdict is a property of its seventeen figures; `Bank.equityMovement()` builds a fresh record each
  call, so a `Bank` wrapper would build it twice. The screen reads the verdict and recomputes nothing.
- ★ **The size is the existing `scale()`** (the sum of the figures' magnitudes), the same size BankCheck's played city
  already read the residual against. Not `Math.abs(closing)`: the movement is a signed sum, and its dust is relative to what
  the sum adds.
- **The floor is `MoneyAudit.CENT`, not the old $5** (BI1's call, which the orchestrator confirmed). CENT is $0.26 at the
  default price level ($10 at today's, a twin). On a small bank a miss between a cent and $5 now shows where it was let by.
  A played bank's movement leaves under 1e-5 (BI1's measurement of the same books; BankCheck holds it to
  `MoneyAudit.tolerance(1e-6, scale)`), far under a cent, and BankCheck's played city now asserts the alert never shows
  on 72 months of it; so no real city lights.
- **The waterfall's red step moved with the alert**: the same figure and the same verdict; left alone it would show a red
  "Not accounted for" step under no alert.
- **Not touched, seen:** `BankScreen` drops a cause under `.0005` ($0.50) from the waterfall (:4052) and from the Statement
  (:4285, `moved(...)`): that is how a cause is drawn (a step too small to see), not an integrity verdict, and `:677`'s `.005`
  is a rate-points display. Left as they are.
- **Hand-built records, not a played city.** A played city would need 1e13 of equity to cause the dust, which only KeptCheck's
  4,820 months reach; the section causes the same condition (a few steps of a double on a movement of 4.5e13) in milliseconds.
  The screen's lines are one-line reads of the getter: KeptCheck reads the page for equality, not for these cards.

## Eye check (not pending work)

Open the Bank tab, Statement page and the "How its equity moved" card on a big city (KeptCheck's, or any past about 1e13):
no red "Not accounted for" step and no "Something moved its equity without telling it" alert. On a small young city
the step stays grey and the alert absent while its movement foots.

## Where the kept runs are

No dry run (the pick is not ALL). The two trace runs: main 865a7171c in `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-BI2-main-865a7171c/`
(`main.log` and the eight `main-*.csv`), this branch in `C:/Users/Jerus/AppData/Local/Temp/cbs-trace-BI2-mine/` (`mine.log`,
`mine-*.csv`), commit dd75a68dd. Main's classes were built apart in a detached worktree at `C:/Users/Jerus/AppData/Local/Temp/cbs-bi2-main`.
