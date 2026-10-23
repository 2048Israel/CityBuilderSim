# XR5 - the regimes' rest: a peg pays the pressure, the tied path prices the curve, a symmetric overshoot, a thin vault's warning

Jerus, 2026-10-23, at the review desk, on XR2's calls (`XR2-notes.md` §4) and
the orchestrator's two from its review (`FX8.md`, "For Jerus"). His eye check
of Policy › Money › The currency, the rate's card under a peg and Trade › The
currency's hold is done ("Looks right"). Settled as built: ★1 a band gives way
for the month and does not break ("Gives way for the month"); ★2 the currency
aim stays inert under Float and Lean ("Keep it inert"); ★3
`PEG_BREAK_DENT` stays .5 ("Keep half"). Under Lean, the default, nothing here
runs: the playtest's trace should not move; say so if it does.

1. **A peg pays the pressure, not the month's balance** ("Pay the
   pressure"). Today the peg settles the month's balance, so in a surplus
   month with a weak shadow it buys dollars (ForeignAccounts.java about 941),
   and a band's weak edge holds for free in a surplus month (about 902). The
   vault pays what holding the rate takes, from the shadow's gap, so a weak
   D$ drains it even in a surplus month. CurrencyRegimeCheck: a fixture with
   a surplus and a weak shadow, the vault falling under a peg and at a band's
   weak edge.
2. **The tied path prices the curve** ("Price on the tied path"). While the
   dial is tied under a peg, `DebtManager.pathFor` (about L1799) still prices
   the inflation rule; under a peg the curve's expected path is the tied
   rate's. A check that the long yields under a peg follow the US-tied path.
3. **The overshoot both ways** (★5, "Overshoot both ways"). A break with the
   shadow stronger than the peg lands past it on the strong side and eases
   back, as a weak break does (the franc, 2015), by the same rule.
4. **A thin vault's warning** (★4, "Warn on a thin vault"). Choosing Peg (or
   Band) when the vault covers under about two years of the month's pace
   (the page's months-left figure) shows a plain warning on the card; the
   choice still goes ahead. The default town pegged at founding breaks at
   month 24 (CurrencyRegimeCheck 11), which is the case it names.
