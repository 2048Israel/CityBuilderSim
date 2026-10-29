# BK2 - each holder's own discount on a line of the city's paper

Queued 2026-10-29 by the orchestrator, from BK1 ([notes](BK1-notes.md), 1 and 4): it takes away
the artifact BK1 could only name. Jerus runs only BK1, HD1 and EM1 on 2026-10-29, so BK2 waits.

## What BK1 found

A line of the city's paper keeps one pool of unaccreted discount for all its face
(`Debt.discountLeft`), and every holder's share of it is pro rata by face (`Debt.unaccretedOnPaid()`,
`unaccretedOn()`). A reopening (`Debt.reopen()`, settled in `Game.householdsTakeTheirShare()`) and
the central bank's add-on (`reopenForCentralBank()`, `addOnForCentralBank()`, in
`Game.rollCentralBankAtIssue()`) add their tranche's discount to the pool at that tranche's price.
While anyone but the bank holds part of the line, the pool's discount per face moves for every
holder, so the bank's unearned discount moves with no cash and no income. BK1 measures that move
(`Game.restrikeBankDiscount()`) and routes it to retained as a named cause
(`Bank.restrikeDiscount()`, `EquityMovement.discountRestruck()`), so the split articulates and no
figure of the city changes. The move itself is still there, and it is large in a city that borrows:
AutoBuildCheck's cities re-strike up to -$1,641k in a month on about $30,000k of equity. It is
re-timing (the bank's later accretion makes up for it), never money.

## What BK2 does

1. **Each holder's discount, at its own price.** The households', the firms', the world's and the
   central bank's unaccreted discount carried on the line beside the bank's, each set from what that
   holder paid when face reached it (at the settle, at the add-on), accreting with the pool's
   straight-line rule; face that changes hands carries the seller's discount per face. The bank's is
   then its own (`unaccretedOnPaid()`), and the pool is their sum.
2. **Every place face changes hands** (about twenty in `Game`: the settle and the three takings, the
   holdings dial, the households', firms' and world's sales to the desk and to the central bank, the
   buybacks and the deals at `Game` ~16591 and ~16789): `unaccretedOn(face)` becomes the seller's.
3. **Then the re-strike is nothing** at every reopening: `HoldersCheck` 12 says so on its fixture,
   and BK1's measurement stays as the check that it is (or goes, with its cause row).
4. **The save**: the new fields are the line's. An older save would read them missing, and the
   bank's discount wrongly: bump `SAVE_FORMAT` and raise `OLDEST_LOADABLE_FORMAT` (CLAUDE.md).

## Scope and checks

`Debt`, `DebtManager`, `Game`'s paper trades, `Bank` only where the cause row goes. The bank's
accretion income changes, so the trace moves: run the full suite and the ensemble, and commit the
baseline as `docs/ensemble/BK2.md`.
