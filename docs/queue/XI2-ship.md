## changelog
### {next} — XI2, Trade's warning and rule text true of the bounded intervention, a peg and a band — 2026-11-07, ready, see docs/queue/XI2-notes.md
XI1's review follow-ups, on texts only (the model's steps 5 and 7 are untouched, the trace is byte-identical, no ensemble). Trade's
THE RULE and the Interventions row's (i) stated the unbounded linear rule; they now say up to 10% of a year's output dealt between
two month ends and less for each dollar past that, and that a peg or a band holds the rate while dealing moves the rate behind the
hold. The warning on the exchange read the deal alone: two pieces of 9% each crossed the bound unwarned, and the second's figure was
its move alone (×1.2) when the model gives it on top of the first (×1.1). It now reads the month's dealing as the sum step 5 reads
(a sale on top of a big purchase counts net) and says what is dealt already. Under a peg it said "moves nothing": the rate is held
but the float's own rate behind it moves and the vault deals to hold it (XR5); under a band it said "would move the rate ×N" where
the band stops it at its edge. New pure getters on `ForeignAccounts` (`dealtYears`, `yearsOfOutputWith`, `dealEffectOnTop`,
`dealFactor`, `rateAfterDeal`, `regimeHoldsDeal`); `TradeScreen.pastBound()` has the words. InterventionCheck 6 proves the model
(peg, band, the sum); KeptCheck 74b the texts, each scenario drawn kept.
