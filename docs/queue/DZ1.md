# DZ1 - DenominationCheck's reformed twin parts in a hot town: find the absolute tolerance

Found by FC1 (2026-11-02, `docs/queue/FC1-notes.md` section 1). With FC1's builders' price on, DenominationCheck's
town takes a hot path (a price index of about 6 by month 170, 3,990 people against 1,547 without FC1), and its
reformed twin parts from it at month 170: every sector's cash and the treasury at once, by about 1e-9 relative, then
growing chaotically to 3.08e-6 on "the same shops' till" a decade on (the harness allows 1e-6). FC1's own figures
(the order multiple, the queue) are equal in both twins at that month, so the parting is not FC1's code: it reads
like a threshold or a solver tolerance in absolute money, which a reform by 100 makes a hundred times looser in
relative terms, and which only a town this hot reaches.

To do: turn FC1 back on in DenominationCheck (delete its `BuildingManager.buildersShortOn = false` line), run it,
find the first figure that differs at month 170 between the twins and the tolerance behind it, and make it scale
with the unit (Denomination) as every other money threshold does. Done when DenominationCheck is green with FC1 on.
