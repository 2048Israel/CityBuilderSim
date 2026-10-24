# FW2 - the bank's sheet and the books' sheet read after a load what they read before (0.8.38)

- **F-S1-1, the bank's sheet: fixed.** The load ends with `Game.refreshBank()`, which re-read the families' savings
  and the businesses' tills as they stood after the month, not as the month last set the deposit book. The book is
  saved now (`DataSave.bankHouseholdDeposits`, `bankSectorDeposits`) and put back over the re-read
  (`Bank.restoreDeposits()`), before anything reads the bank's funding. Most of the $131.9M the finding measured was
  the harness, not the game: SectorStatementCheck's section 6 loaded with `loadGame()` into a game that was never
  built, so no branch stood and the bank "gathered" only foreign money. It loads through `loadGameSave()` now, as
  the game does, and asserts the loaded city has its buildings.
- **F-S1-2, the books' sheet: its live half was already fixed** in 0.7.102 (A16): `Game.recordMonth()` pushes the
  sheet again at the month's close (`economyManager.pushBalanceSheetInputs()`, Game.java:13093), so cash and debt
  are one instant. **The after-a-load half is fixed here:** R1 and R2 (the interest by instrument, the debt by kind
  and when it falls due) were not saved, so a loaded sheet showed its debt as one line. Both months are saved now
  (`SectorBooks.debtToSave()`, `restoreDebt()`), with R7's running totals (`BusinessDebtManager.movedToSave()`,
  `restoreMoved()`), so the debt schedule is counted the first month after a load and foots.
- **The check:** SectorStatementCheck section 6 compares every line of every sector's sheet and the bank's, both
  columns, to the cent (no exceptions left), the R1/R2 records field by field, the deposit book, and R7's
  roll-forward a month on. 7 FAIL with main's model under it, all OK on the fix. The old assertions that pinned the
  gap ("R1, R2 and the shares are not saved", the bank's four re-read lines) are replaced by these.
- **No format bump:** the fields are additive; an older save reads as before (the re-read, R1/R2 not counted).
- **Not saved still:** the shares (`SectorBooks.Shares`, the investor report), not a sheet.
- **The trace:** the default playtest (4,005 months) is byte-identical to main's but for its two run-time lines; no ensemble (EN1).
