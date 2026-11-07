# XR5 - the regimes' rest: the hold pays the pressure, the tied path, the overshoot both ways, a thin vault's warning ({next}, 2026-11-06)

## WHERE IT STANDS

**Ready.**
- **The branch.** `batch/XR5` was built on UP7's ship file (RL1, 0ec6b4fa). UP7 came back
  from its gate (a KeptCheck conflict with main 0.9.33) and was ready again at 698360bd,
  merged here at 733156db with R5 (0.9.33) in it.
- **The commits.** Slice A (the model, the save, the harness) is e053f099; slice B (the
  warning, the screens) is f951d5ca. Both were drafted by Sonnet 5.5 at xhigh and reviewed
  by Opus.
- **The trace.** A city on the default regime (Lean) moves by nothing, byte for byte
  (section 2), so no ensemble.
- **The checks.** `--pick` on the merge exits 4. Besides main's recorded reds there are
  two KeptCheck lines that come from no code this batch wrote (section 6). CurrencyRegimeCheck
  is green.
- **Money changes**, so the gate gives it a stranger's review. Section 3 names the
  decisions this batch took that the brief did not settle.
- Two ★ calls (section 7).

## 1. What it builds

Jerus, 2026-10-23, on XR2's ★4 and ★5 and FX8's two: "Pay the pressure", "Price on the
tied path", "Overshoot both ways", "Warn on a thin vault".

- **The hold pays the pressure** (`ForeignAccounts`, `repriceCurrency()` step 7, THE
  REGIMES). Under a peg or a band the vault no longer meets the month's balance. Instead it
  runs the interventions' rule backwards (`interventionLocalFor()`, XI1's bounded inverse):
  - `free` = the shadow's log plus `holdFelt`, what the hold's own dealing still puts on
    the rate. `holdFelt` is new, saved in slot 147, and fades at `INTERVENTION_HALF_LIFE`.
  - The target is the peg, or the band's free rate clamped to its edges.
  - The vault sells (or buys) `interventionLocalFor(target - free, a year's GDP) / rate`
    dollars, and `holdFelt` takes the lift. So while held, the rate is the shadow plus
    `holdFelt`.
  - A weak D$ drains the vault in a surplus month, and a strong one fills it in a deficit
    month (CurrencyRegimeCheck 14, 15).
  - A peg breaks by the vault when the sale it needs would take the vault under
    `VAULT_FLOOR`. A purchase never breaks it, and a band gives way for the month, as
    Jerus settled.
- **The tied path prices the curve** (`DebtManager.pathFor()`, `expectedPolicyPath()`,
  `ruleNow()`, `settledDial()`).
  - Under a peg, `isTied()` (a source Game hands in: the regime is Peg) makes the curve's
    expected dial `expectedPath(floor, x -> the dial, ...)`. The floor's premium over the
    tied dial closes as it does toward the rule, and the long end follows the US's rate
    plus the spread.
  - The tie is in both caches' keys (NaN untied), so an untied manager builds every path
    exactly as before (CurrencyRegimeCheck 17, to the bit).
- **The overshoot both ways.** A broken peg lands at the shadow plus `OVERSHOOT` of the
  SIGNED gap. A strong shadow lands past it on the strong side and eases back at
  `MARKET_HALF_LIFE`, as a weak one does (CurrencyRegimeCheck 16). `breakLanding()` uses
  the same rule.
- **A thin vault's warning** (Policy › Money › The currency, HOW THE D$ IS SET). It shows
  in amber, kept (`Kept.scope("thinVault")`), while a peg or a band is staged and not in
  force. The choice still goes ahead. Two cases:
  - Before the city has traded a month: nothing yet says what holding the rate will cost.
  - When `monthsHoldingGap(BREAK_GAP)` < `THIN_HOLD_MONTHS` (24, the median peg's life,
    R29): held against the D$ 10% weaker, where doubt starts to break pegs, "the vault's
    US$X would reach its floor ... in N months at the most".
  - `monthsHoldingGap()` is pure. The first month pays the whole gap's lift, and each
    month after it what the support's fading takes, which is what step 7 then deals
    (CurrencyRegimeCheck 18, to a millionth of a dollar).
  - It leaves out the thin cover's premium, so it is an upper bound: a vault projected at
    26.8 months held 20.
- **The words.** Policy's regime lines, THE PEG's and THE BAND's (i)s, the vault's month
  cell, the payer's (i), Trade's hold sentence and HOLD_INFO, and Finances' WHAT THE
  MARKET EXPECTS THE DIAL TO DO (under a tie: "Tied: the US's rate and the spread", and
  a tied sentence) now state the new rule. The "of which X was hot money leaving on
  doubt" clause no longer claims the payment includes it. Trade's interventions' texts
  are XI2's and are untouched.

## 2. What moves for a city on the default regime: nothing, byte for byte

- **The code.**
  - The LEAN and FLOAT branches of step 7 are unchanged.
  - `holdFelt` is 0 under them: it is multiplied by its fade (0 stays 0) and is never read
    there.
  - The one local removed, `balanceUsd`, was read only by the band and the peg.
  - `DebtManager` untied takes the old expressions, with the tie's key NaN.
  - `monthsHoldingGap()` is pure and read only by the screen.
- **The trace.** The default playtest ran traced (`-Dplaytest.trace`, both data files
  named) twice:
  - On UP7's first tip (0ec6b4fa) against slice A.
  - On UP7's mended tip (698360bd, built apart) against this branch's merge (733156db).

  Both times all eight CSVs are byte-identical, and the logs differ only in their run
  times. So there is no ensemble (EN1).
- **The save.** `ForeignAccounts` grows to 148 slots, and slot 147 is `holdFelt`. There is
  no `SAVE_FORMAT` bump: an older save loads with no support, as a peg just entered would.
  Under Lean that is 0, which is what it was.

## 3. Decisions named for the review

- **The support fades, and the vault re-pays it.** The study (3.6) says a band's sale moves
  the rate by the interventions' rule and fades at `INTERVENTION_HALF_LIFE`. So the hold is
  a stock that fades, and the vault pays each month what holding takes beyond what is
  left of it.
  - A steady 10% gap costs the whole lift the first month (5% of a year's output, linear),
    then about 0.55% of a year's output a month.
  - The two other readings were refused:
    - Paying the whole gap's lift every month would cost 5% of a year's output a month at
      a 10% gap.
    - Paying only the gap's change would let a steady weak gap cost nothing, against
      "drains it".
  - Step 7 of the Lean defence says it in its own words: a defence that leaned against
    its own fading support "would sell again every month to hold where it had got to,
    which is a peg".
- **Inside a band** the fading support still shows in the rate, and the hold line (Trade)
  shows it. Before, a band's inside was the float's rate to the bit.
- **Leaving a peg or a band** for Lean or Float drops the support: the rate goes to the
  shadow, as before. Moving between a band and a peg keeps it. `repeg()` keeps it, so a
  devaluation buys dollars and a revaluation sells them.
- **A devaluation buys** what holding the D$ that much weaker than the shadow takes, on
  XI1's bounded rule run backwards. The central bank, the default payer, pays it the next
  month in new money:
  - A peg 20% weaker: 9% of a year's output (linear).
  - 50% weaker: 28% of it.
  - Twice the shadow: 1.2 years of it.

  Holding a currency far under its market takes printing that much. No cap was added: the
  stepper lets a player stage any size, and the bill is the rule's.
- **With no GDP** (`annual` 0) the rule gives 0, and the hold holds the rate and deals
  nothing.
- **The warning's test** is a steady gap of `BREAK_GAP` held under `THIN_HOLD_MONTHS`. It is
  not the brief's "two years of the month's pace":
  - Before a regime is entered there is no pace under the new rule.
  - The default town pegged at founding, the case the brief names, no longer runs its
    vault down (section 4).

## 4. The default town pegged at founding (the brief's case)

The brief says it breaks at month 24 by the vault (CurrencyRegimeCheck 11, XR2). Measured
on UP7's tip, the old rule broke it at month **54** by the vault: other batches moved it
since XR2.

Under the new rule:
- The town's shadow sits **20-30% stronger** than the peg (its hot money arrives), so the
  hold BUYS dollars every month.
- The vault goes from US$647k at founding to US$1,062k by month 24.
- It breaks at month **56 by doubt**, on the strong side, at the base 0.1% a month.

So the warning names that town only by its first case: at founding it has traded no
month.

## 5. The checks

- **CurrencyRegimeCheck** (sections 0-18, 16 s) is green:
  - Sections 2, 3, 6, 9 and 11 are restated to the new rule. Their premise was the old
    rule, which Jerus's call moved.
  - Section 10 asserts slot 147.
  - Sections 14-18 are new.
- **Fixtures moved under the new rule, the assertions' meaning kept:**
  - VaultMoneysCheck: the peg is revalued to make the sale and devalued to make the
    purchase. A peg on balanced trade no longer sells or buys by the month's balance.
  - InterestAbroadCheck and SaveFileCheck: 148 slots.
- **`--pick` on the merge** (733156db, 48 of 182 harnesses) exits 4. The reds are:
  - main's recorded ones;
  - two KeptCheck lines that are not this batch's code (section 6).

  Every other picked harness is green or main's recorded red. Stale has no firm finding.
- **No `--timing`**: the gate times hard.

## 6. The two KeptCheck reds, and their cause

The two lines are "the kept header's signature equals a header built new, at pace and
short, at every rung (18 differ)" and "the city has a plain business and a materials
industry with works to draw (Construction, null)". Both appear on this branch and not on
UP7's tip, and neither is in code this batch wrote.

- **The path.** KeptCheck's city plays real months on Policy › Money › The currency under
  each regime. Its peg breaks in its first month on both builds: its vault is US$1.3M
  against a year's output of about US$47M. The landing differs by the strong-side
  overshoot, 6.1592 against 6.1588. From there the long-played city's path drifts.
- **The city's state.** Measured with a probe on both builds, the city's D$ is at the
  rate's ceiling (`MAX_RATE`, 1e9) from month 4,182 on. It stays there at section 98
  (month 4,789).
- **The header line.** Its first difference is the header's long rate label, "D$1,000,000,000.00
  per US$". It is hidden, yet the kept header holds the layout it had when it was shown
  (145x14), where a header built new has none. On this path the label flips between shown
  and hidden across the section's draws, as the header's other tiles' widths move. That is
  a kept-header fault: a hidden node keeps a stale layout, and KeptCheck 98's signature
  counts it.
- **The works line.** By month 4,789 the sawmill `ensureWorks()` put up early is gone. No
  works can be funded: Sawmill, Scrap Yard, Wood Pellet Mill and Panel Mill each say
  NEEDS_FUNDING, even with Build's bond. On UP7's path the early sawmill happened to stand
  to then. The fixture checks that works stand, not that they are drawn, and on a city
  this broken it cannot cause its condition.
- **So** both are latent faults in KeptCheck's late fixtures, on a city whose D$ sits at
  the clamp. Any change to that city's path can trip them. They are named here and not
  mended (Jerus, 2026-11-04), and KF1 is queued for them.

## 7. ★ For Jerus

- **★1 The world's pegs: overshoot both ways too?** Yes (Recommended), as XR7.
  `WorldMoneys.resolve()` still lands a world peg's break at the shadow plus `OVERSHOOT`
  of `max(0, x)`, so a world money stronger than its peg breaks with no overshoot. The
  Swiss franc of 2015 was exactly such a world money. It is a batch of its own because it
  moves the default playtest's trace (the world's pegs break in it), so it needs the
  ensemble. The alternative: keep it one way.
- **★2 Inside a band, after its edge dealt: the fading support leans on the rate** (Keep
  it, Recommended). The vault's sale at an edge moves the rate by the interventions' rule
  and fades at `INTERVENTION_HALF_LIFE`, as the study says. So once the shadow is back
  inside the band, the rate sits between the shadow and the edge for some months, where
  before it was the float's rate to the bit. The alternative: drop the support when the
  free rate re-enters the band. The rate would then jump back to the shadow, and the
  next push to the edge would pay the whole gap again.

## 8. Left as it is

- **The world's pegs** (`WorldMoneys.resolve()`) still overshoot one way, `max(0, x)`.
  ★1 asks.
- **The manual** (the published page) still says the old rule for a peg and a band. It is
  for the docs pass: change the page, publish, pull it, and run `ManualToMarkdown`'s two
  lines.
- **WN4b** (a peg to the euro or another of the eight, queued) builds on this hold. It is
  the same rule against another money's rate.
- **Not looked at by eye.** These were never opened in a window; KeptCheck draws them kept:
  - The warning on Policy › Money › The currency, with Peg or Band staged at founding and
    with a thin vault.
  - Finances' expected-dial card under a peg.
  - Trade's hold sentence inside a band after an edge.
