# XI1 - the intervention bounded (0.9.26, 2026-11-06)

## WHERE IT STANDS

**Ready.**
- **The branch.** `batch/XI1` was built on R2's ship file (RL1). Main with
  R2 in (f33540a0, 0.9.24) was merged at 205f1eb4; past R2's tip, main
  changed nothing but `VERSION`.
- **The commits.** The slice is 3acf3186, drafted by Sonnet 5.5 at xhigh and
  reviewed by Opus. The ensemble is 87d61c7b. The mend of InterestAbroadCheck
  9 is 4ec6f65e.
- **The trace.** Nothing below the bound moves. The trace moved at month
  2,119 of 2,400, where the playtest's war chest bought past the bound
  (section 2), so the ensemble ran: 23 of 26, as 0.9.23 (section 5).
- **The checks.** `--pick` on the merge exited 4: one NEW red,
  InterestAbroadCheck 9, which this batch caused and mended (section 6). The
  full suite on the mend: 173 of 179 green. Its six reds are main's recorded
  ones, none NEW, and `--judge` exits 3.
- **Money changes**, so the gate gives it a stranger's review. Sections 1-2
  say what moves and what does not.
- No ★ calls.

## 1. What it builds

The brief is GD0 §4.5 (`docs/studies/spec-gold-money.md`), decided by Jerus
on 2026-11-02 ("Rate running away (Recommended)"): his Rotterdam (slot 3)
bought dollars worth 4.5 years of its output in one month, step 5 of the
float added `INTERVENTION_IMPACT` (2.0) x 4.5 = 9.0 in logs, and the D$ went
x11,600 in the month.

- **`ForeignAccounts`**: `INTERVENTION_LINEAR` = 0.10 of a year's GDP (the
  study's ★est). `interventionMove(local, annual)` is the old
  `INTERVENTION_IMPACT x local / annual` up to it and
  `I x s0 x (1 + ln(s / s0))` past it, a sale the mirror: continuous in
  value and slope, and past the bound each further dollar moves the rate
  `1/k` as much at `k` times it. `interventionLocalFor(lift, annual)` is its
  inverse. The three places that used the linear rule now call them: step 5
  (what the treasury dealt since the last reprice), step 7's Lean defence
  (how much it sells, and the lift of what it sold), and `dealEffect()`
  (Policy's 1% preview and Trade's warning). `yearsOfOutput()` is the
  screen's figure.
- **The bound is per reprice.** The treasury's net dealing since the last
  reprice and the month's defence are each bounded on their own. A purchase
  split over months is linear in each month and fades at
  `INTERVENTION_HALF_LIFE`; the bound is on what the market is asked to take
  in a month, which is what Rotterdam did.
- **The Lean defence past the bound** (a decision, named for the review). It
  still takes back `absorption()` of the fall, as its rule says ("sells only
  the dollars that takes"). So past the bound it sells
  `interventionLocalFor(absorption() x fall)`, which is more dollars than the
  linear rule for the same lift. The cap is as before: `absorption()` x the
  month's deficit, and the vault. The other reading would sell the linear
  amount and take back less. The bound would then save the vault dollars for
  free, which the defence's rule does not say. InterventionCheck 3 causes it:
  a fall of 0.317 logs with 0.270 to take back sold US$1,701k, against the
  linear rule's US$1,619k.
- **Trade**: the buy-or-sell card warns past the bound (section 4).

## 2. What moves below the bound: nothing, byte for byte

- **The code.** Every linear branch is the old expression, with its
  operands in the old order:
  - step 5: `INTERVENTION_IMPACT * dealtSinceReprice / annual`.
  - The defence's sizing: `absorption() * fall * annual / INTERVENTION_IMPACT / rate`.
    It now passes `absorption() * fall` as the lift, computes
    `lift * annual / INTERVENTION_IMPACT`, then divides by `rate`. That is
    the same left-to-right evaluation.
  - The defence's lift: `INTERVENTION_IMPACT * defenceLocal / annual`.
  - `dealEffect()`: `INTERVENTION_IMPACT * local / (12 * gdpTrailing)`.

  The functions' `annual > 0` guard was already at every call site.
- **InterventionCheck 1** compares to the bit (`Double.compare == 0`): 48
  sizes of the move, 48 lifts of the inverse and 12 `dealEffect()` sizes, up
  to and at the bound, both signs. A purchase of 9% of a year's output moves
  the rate by exactly the old figure, at tolerance 0.
- **The trace.** The default playtest ran traced (`-Dplaytest.trace`, both
  data files named) on R2's tip (4573c868, built apart) and on this branch
  (3acf3186).
  - **Months 1-2,118 are identical to the byte in all eight CSVs.**
  - The traces part at month 2,119. There the playtest's war chest
    (`LongPlaytest`, THE WAR CHEST: its player's rule, which tops the vault
    up to 1.5 x `PANIC_BACKING` of the hot money, at most 10% of the cash a
    month) bought dollars worth 11.0% of a year's output. Derived: that
    month's rate is 157.133 against 156.986, and ln of the ratio, 0.00094,
    is what the bound takes off, 2s - 0.2 x (1 + ln 10s), at s = 0.110.
  - Both runs passed the audit in every month and matched on every reload.
- **The game never deals by itself.** The founders' dollars are forgotten
  (`forgetDealing()`). Only the player buys: Trade's card, a loan abroad held
  as reserves, or the playtest's rule. A default town's own defence stays
  far under the bound: InterventionCheck 5, 240 months, 86 sales, the
  largest 5.37% of a year's output.
- So the trace moved, at its 2,119th month, by the bound and by nothing
  below it. The ensemble ran (EN1, section 5).

## 3. Rotterdam's case

InterventionCheck 4 repeats it twice, from the ratio (4.5 years of output in
a month):
- On a bare currency: x2.62, where the linear rule gave x8,103.
- On a played scratch town under Float: the buyer's felt interventions less
  the control's equal `interventionMove()` of what it spent, within 1e-12.

Slot 3 was not read. The case's figures are the ones the study measured on
it.

## 4. The screen, and the `ui/` lines touched (for KP6)

- `TradeScreen.java` 2778-2784, `INTERVENTION_INFO`: one clause ("up to 10%
  of it, and less for each dollar past that") and its argument.
- `TradeScreen.java` 3572-3593, `exchangeCard()`: the warning, past
  `INTERVENTION_LINEAR` of a year's output, for a purchase or a sale.
  - Its title: "This purchase is 4.5 years of the city's output", or "35% of
    a year of" under a year.
  - Behind the (i): the rule, then what this deal alone would do to the
    rate, "about x2.6 (D$ per US$) in its month". Under a peg it says the
    rate is held instead.
  - It is wrapped in `Kept.scope("past the bound", ...)`, as KP6 wraps the
    `THIN_COVER` alert above it. It sits between that alert's closing brace
    and `c.getChildren().add(button)`.
  - KP6 changes the alert's last line, two lines above. One unchanged line
    lies between the two changes, so git merges them either way round.
- **Not looked at by eye:** the warning on Trade, Reserves, with an ask past
  a tenth of a year's output: a purchase, a sale, and under a peg.

## 5. The ensemble

The run used the classes of 3acf3186 (`docs/ensemble/XI1.md`): 80 of 80
runs, 11.9 minutes, against `docs/ensemble/0.9.23.md`.
- **The score is 23 of 26, as 0.9.23's.** The three reds are 0.9.23's own:
  AUTO2 A2 and A3, and ZERO_SLACK S1.
- The medians barely move, because only the war chest's largest purchases
  cross the bound. Against 0.9.23:
  - AUTO2's final population is the same (602,188).
  - ZERO_SLACK's final population is 397,895 against 391,923, and S1's
    median is 2.00% against 2.14%.
  - SHOCK's final population is 29,413 against 29,852.
  - HIGH's R3 has no seed above +1 point, against one.
  - AUTO2's A1 is 2.36% against 2.39%.

## 6. The checks

- **While building.** InterventionCheck (new, 5.8 s) and ExchangeRateCheck,
  CurrencyCheck, ForeignCheck, PassThroughCheck, ForeignDebtCheck and
  CurrencyRegimeCheck: all green. Two existing assertions had fixtures past
  the bound and now read `interventionMove()`; the fixtures are unchanged:
  - ExchangeRateCheck 5's twins buy a year's imports, 50% of a year's GDP.
  - CurrencyCheck's `lift()`: its section 3 defence sells 10.4% of a year's
    output. Before and after: -0.208333 against -0.208164, and 0.088105
    against 0.088274.
- **`--pick` on the merge** (31 of 179 harnesses) exited 4 with one NEW red:
  InterestAbroadCheck 9, "...the audit saw it cross the edge (financial out
  D$2.1k)".
  - The cause is this batch. The section's town buys dollars worth **11.45
    years of its output** to fill the eight moneys. The linear rule put 22.9
    logs on that town's D$ (the bound puts 1.15), and the collapse that
    followed sent so much abroad that the audit's financial out was
    D$432,860k. Measured on this branch with `INTERVENTION_LINEAR` set to
    1e300: green there, so the old rule passes.
  - The assertion asked for the gross risk-off departure (D$4.3k) in the
    audit's financial out. But Game books the hot money's net move (the
    stock's change, `bank.returnHotMoney`). Under the bound the stock (D$81k)
    is under its target (D$120k), so hot money still arrives that month: net
    out is D$1.0k, against financial out of D$2.1k.
  - Mended in the harness (4ec6f65e). The assertion now reads the net move,
    out or in, as the audit declares it. The fixture is unchanged, and it now
    prints its purchase's years of output.
- **The full suite** (`AllChecks -f`, after the mend): 179 harnesses, 173
  green. Its six reds are main's recorded ones, word for word: SlopeCheck,
  MonetaryCheck, DenominationCheck, ScaleCheck, MapCheck and KeptCheck.
  `--judge` on the log exits 3.
- **No `--timing`**: the gate times hard.

## 7. Left as it is

- `CurrencyCheck.need()` and `ForeignCheck.lift()` keep the linear formula.
  Their fixtures are under the bound; a fixture grown past it would need the
  model's functions, as `CurrencyCheck.lift()` and ExchangeRateCheck 5's
  twins now use. Those two crossed it: a sale of 10.4% of a year's output,
  and a purchase of 50%.
- Policy's deal preview is 1% of a year's output (`DEAL_SHARE`): under the
  bound, so unchanged.
