# SR1 - a city stocked complete in bulk reloads with its cash drifting

Queued 2026-11-07 by R0g (`docs/queue/R0g-notes.md`, section 2). R0b's notes saw it first ("a reloaded twin's cash already
drifts a little from the live one", not traced further).

**The finding.** `CausesOfDeathCheck.founded()` is SicknessCheck's fixture city: `new Game(GameFiles.scratch(..))`,
`run()`, `setCashForTest(Founding.WEALTHY_CASH)`, then `stock()`, which puts 2,000 houses, 48 convenience stores, 16
industrial bakeries, 10 construction depots and a power and a water plant up complete (`addStack(t, n, true)`). Saved
at month M, loaded into a new Game and both played a month, its cash parts:

| reloaded at | the city's cash a month on, reloaded less unbroken |
|---|---|
| m3 | +0.0143 |
| m12 | +0.0036 |
| m60 | +1.28 |
| m228 (December 1918) | +0.16 |

It is the same with or without the influenza and its hold, and the sectors' pools part with it (retail, real estate,
industry, construction, the bank...). The cash is equal right after the load, so a flow the month reads is not carried
across the save. Three variants of the same city each reload to the bit, at m3 and m12, on cash and every
`MoneyAudit.pools` part: a bare city (no `stock()`, no cash set), one with only the cash set, and the playtest's founding
(`LongPlaytest.founding()`, which also did at m60 and m228). So it is `stock()`: the stacks put up complete, in bulk.

**What to do.** Find the flow. R0g's slice A ran a reflective diff of the two cities after the load, and it named
unsaved fields that part: the buildings' dues, the markets' trades and ring offsets, `worldBandExports`, the sectors'
supplier credit and fleet diesel, the business debt manager. Some are re-struck on purpose; one is the leak. Then
either save it (`DataSave` out and in, the load path, `SaveFileCheck`), if a player's city can carry it (the game itself
calls `addStack(.., true)` for the founding's bank branch and yard, `Game` ~888 and ~975, and in the load path), or
show it is the fixture's alone and say so in `founded()`'s comment. **Done:** `CausesOfDeathCheck`'s reload twin
(section 10, on the playtest's founding since R0g) also played on `founded()`'s city, to the bit.
