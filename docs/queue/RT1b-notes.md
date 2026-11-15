# RT1b - RT1 money review follow-ups

Started from main e8ac5a53, on batch/RT1b. No known-reds entries currently
name RT1b or RT1. The scope is the three holder-ledger subtractions and
HoldersCheck 14's measured split; no concurrent batch's source file is edited.

## Implementation and calls

The sectors' buyback/principal ledger and both household principal and
buyback/purchase ledgers use Debt.moved(), the same relative rounding rule
as the paper's holder fields. Cash, income and investing flows retain their
original formulas. No saved field or format changes.

The fixtures cause a full take to leave positive rounding under main's
subtraction, exercise each entry point at today's and founding scales,
retain partial holdings, and bound each cash credit to its price share.
The desk fixture recalls paper through the live Game wiring across two
pieces and compares the transferred face with Bank.getCityBook()'s actual
increase. It also checks the individual pieces, unrelated holders, sector
ledger, cash and pooled money. Verification and mutation evidence follow.

★ Use the existing relative rule, without a new floor or a full-take
shortcut: legitimate remainders stay at both price levels.
★ Invoke the real desk through OutwardInvestment.recall(), rather than
adding a test seam in Game, which R0f is changing concurrently.
