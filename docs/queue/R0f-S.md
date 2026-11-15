# R0f-S — the early city's loaded spending and rent

Cause the original R0f exhausted-vault save fixture, without changing its premise: `Founding.custom` with `WEALTHY_CASH`, zero reserves, `withUnfitted(true)`, run the first month, endow the vault with `WEALTHY_RESERVE_USD`, forget the fixture's dealing, prepare a snapshot, write JSON only in `GameFiles.scratch`, reload, then advance both twins once.

At the month-2 (February 1900) save, on unchanged main c0120d77, the first saved-state difference immediately after loading is Retail's `wantedSpend`: 0 in the uninterrupted city, 0.02587509525508486 in the loaded city. The next month differs in rents, household cash and the foreign account. R0f reproduces that same difference after fixing its inherited world-rate handoff. Its gold episode, exit flag and policy rate round-trip correctly. See R0f-notes.md, Save investigation.

Keep MoneyDatesCheck's hard assertion "the exhausted-gold city's whole foreign-account twin matches after the next month". Find the earliest changed derived input, fix the load path that causes it, and retain the same zero-vault/endowment fixture and exact equality. SaveFileCheck's separate R0f assertion must stay green too.
