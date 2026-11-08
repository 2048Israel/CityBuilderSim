# BK2b - BK2's review follow-ups (each holder's own discount)

Queued 2026-11-08 by the orchestrator, from the stranger's review at BK2's gate (money and saves; verdict
SHIP + FOLLOW-UP). None of the three can reach a player's money or save today. A small fix batch: Sonnet
whole (`docs/batch.md`, 3).

1. **A stale comment.** `Game.java:11732` (BK2's tip): "BD1: a premium eats the line's discount left, the
   settled part's too" is no longer true; under BK2 a premium reopening moves nobody's discount at the sale.
   Make the comment say what the code does.
2. **Dust on no face.** `Debt.java:569`, used by `retirePart` (lines 1403-1404), does not hand over a
   seller's whole discount when what the seller keeps is within `HOLDING_ROUNDING`, as `pass()` (line 437)
   does. A partial buyback that takes a seller's face all but an ulp short zeroes the face (`moved()`) and
   leaves about 1e-16 of the seller's discount on no face; for the bank it later accretes into income.
   Give `retirePart` `pass()`'s rule, with a fixture that causes the ulp-short case.
3. **A path no harness drives.** The one place a re-strike is still booked: the bank paying a premium larger
   than its own discount on the line (★1's clamp, booked to retained as `discountRestruck`). HoldersCheck 13a's
   premium ($0.0715k) is covered by the bank's own discount ($0.4084k). Add a HoldersCheck case whose fixture
   makes the premium exceed it and assert the re-strike and the equity footing.

The trace should not move (2 is sub-cent, 3 is a check). Harness: HoldersCheck (`--pick`).
