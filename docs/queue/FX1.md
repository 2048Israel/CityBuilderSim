# FX1 - three small faults copper found (2026-10-12)

The brief for batch FX1. `QUEUE.md` links here. Written by the orchestrator from the notes of batch I8 (copper, 0.7.133;
`runs/fixI8-notes.md` §6 ★11 and §7). Each is a fault, not a design choice ("ship fixes without asking").

## 1. Stock written off: food's add-back is dead, and materials and luxuries have none
I8 found that a works sold back with stock in its yard wrote the stock off through `Sector.produceStock()`, and the
inventory term in `NationalAccounts` booked it as negative investment: GDP went negative in four ensemble seeds (seed 5,
month 899: -305,164). I8 fixed it for the goods in `NationalAccounts.HELD`: `Sector.bank()` keeps the month's write-offs
(`writtenOffStruck()`) past the rows it clears, `SectorState.writtenOff` carries them across a save, and
`EconomyManager` hands them to `NationalAccounts.update(..., heldWrittenOff, ...)`. GdpCheck holds it.

Found on the way and left: **food's own add-back** reads the row after it has been cleared, so it has always been zero;
**materials' and luxuries' terms** have no add-back at all. Give all three the rule HELD's goods now follow, with a
fixture that causes each (a shop's or mill's warehouse written off), in GdpCheck. Expect the traces to move only where a
write-off happens; say where.

## 2. A sector raises shares before its own credit test
On the Earth playtest at month 1,293 Copper Refining raised D$3.8B of shares, then the bank's capital rule refused the
loan, and no order was placed: the sector was left holding the equity (`BusinessInvestment.consider()`). Test the credit
first, or return the raise when the order falls through (★ which, with the reason). A fixture that causes it.

## 3. LongPlaytest's bank trace and report
Its bank trace takes `key.substring(0, 4)` and crashes on a sector key under four letters ("Oil"); its report
abbreviates Coal Mining and Copper Mining both "CM". Make both safe and distinct. The trace's columns must not change for
the keys that work today (the reference traces stay byte for byte, apart from what item 1 moves).

## Files
`NationalAccounts`, `Sector`, `EconomyManager` (item 1); `BusinessInvestment` (item 2); `LongPlaytest` (item 3);
GdpCheck and the fixtures' harnesses; `GameVersion` (it moves the books, so it takes a number).
