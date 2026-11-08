# CD9b - CD9's review follow-ups (the foreign debt's)

Queued 2026-11-08 by the orchestrator, from the stranger's review at CD9's gate (money and saves; verdict SHIP +
FOLLOW-UP). Neither can reach a player's money or save: one is a comment, one a check that no longer reads the city.
A small fix batch: Sonnet whole (`docs/batch.md`, 3).

1. **A javadoc CD9 made false.** `Debt.java:673`: `effectiveRateOf`'s javadoc says `getYieldToMaturity()` "is left
   as it was (its bracket stops at nothing)"; CD9 split its bracket at a price equal to the payments' total (an
   80-step search on [-0.5, 0] or [0, 5]). Say what it does now.
2. **A revaluation check that repeats the formula.** `CurrencyCheck.java:886` and `:914`: the lines CD9 put in for
   the retired D$-only revaluation build a bare ledger and assert `revalueForeign`'s own formula back, so they no
   longer test the fixture city's revaluation. Drive the fixture city's own ledger (or a real one built as the city
   builds it) through the extreme rate, and assert the booked line CD7 keeps against the model's constants; the
   fixture must cause the condition (`README.md`, *The checks*).

Done when: CurrencyCheck's two sections read a ledger the city built and are red on a revaluation the code skips
(try it once by hand, then put it back), the javadoc is true, and `--pick` shows only main's recorded reds.
