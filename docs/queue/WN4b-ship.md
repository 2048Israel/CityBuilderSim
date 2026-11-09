## changelog
### {next} — WN4b, the D$ pegged to the euro or another of the eight — 2026-11-09, ready, see docs/queue/WN4b-notes.md
The peg (XR2) can now be kept in any of the eight moneys with a rate this month, not only the US$ (WN4's XR3 row).
The peg is kept in its anchor's money (`ForeignAccounts` slot 148, the anchor; no format bump, an older save reads
the US$), and the rate it holds this month is the peg over the anchor's US$ price, so under a euro peg the D$ moves
against the US$ as the euro does. The shadow's gap, the hazard, the hold's lift (XR5) and the break's overshoot are
all against that held rate; the shadow stays the D$'s own rule, so a peg to a money the city does not trade in
also pays that money's swing. Choosing the anchor under a peg in force re-pegs at the day's cross, as the krone
moved from the mark to the euro; a devaluation is in the anchor's money. The tied dial follows the anchor's
central bank rate (XR4's PolicyRates) plus the spread. Policy › The currency's THE PEG has the picker of the eight,
the peg in the anchor's money with today's D$ per US$ under it; Trade's peg lines name the anchor. A US$ peg is as
it was to the bit (`PegAnchorCheck`, 115 assertions); the default trace is byte-identical, so no ensemble.
