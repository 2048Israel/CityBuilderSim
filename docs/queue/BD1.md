# BD1 - the bank's equity parts part from its equity after it buys the city's paper (found by FC1)

Found by FC1 (2026-11-02, `docs/queue/FC1-notes.md` section 1). With FC1's builders' price on, SectorStatementCheck's
city takes another path. In month 134 the bank buys D$1,113M of the city's paper (unearned discount +9.93). In month
135 its UNEARNED_DISCOUNT falls by 7.0635 and its CITY_PAPER by about 18, with no named cause, so its equity moves
+25.41 and RETAINED only +18.36. "Its equity's parts are its equity" is then off by 7.05143 in 226 of 479
bank-months, to the end; "the loaded city's month holds every identity" is off once, likely the same. It reads
like BK1's class: a discount released or re-struck outside `Bank.restrikeDiscount()` and the buyback-gain path.
The money audit holds (it is the statement's decomposition, not the money).

To do: delete SectorStatementCheck's `BuildingManager.buildersShortOn = false` line, reproduce month 135, name the
release and route it through the statement as the other discount moves are. Done when SectorStatementCheck is
green with FC1 on.
