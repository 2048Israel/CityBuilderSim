# WN4c - WN4b's review follow-ups (the peg's anchor)

Queued 2026-11-09 by the orchestrator, from the stranger's review at WN4b's gate (money and saves; verdict SHIP +
FOLLOW-UP). Only a save that holds an anchored peg but has lost the anchor's US$ price reaches it. A small fix
batch: Sonnet whole (`docs/batch.md`, 3).

1. **The fallback keeps the old units.** `ForeignAccounts`' load, slot 148: when the saved anchor has no US$ price
   (`!(usdPer[pegAnchor] > 0)`), the anchor falls back to the US$ (0), but `pegRate` keeps its saved value, which
   is D$ per unit of the old anchor. The peg's own line (`if (!(pegRate > 0)) pegRate = ...`, near line 1092)
   then reads it as D$ per US$. Zero `pegRate` in that fallback, so the peg re-derives it from the rate.
   `PegAnchorCheck` section 8 (the save) gets the case: a euro-anchored peg saved, the euro's price removed from
   the save, loaded; the peg reads the US$ and its held rate is the loaded rate, not the euro figure.
2. **The review's two low notes, to look at and settle in the notes:** the hold's support is not clamped in the
   held branch of the peg's month (`holdFelt += lift`, near line 1124); and when a non-US anchor's
   `currencyGap()` is read in the month, before or after the anchor's price moves. Fix either only if a fixture
   shows it wrong.
