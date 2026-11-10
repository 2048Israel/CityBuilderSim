# WN4c - WN4b's review follow-ups, the peg's anchor (0.9.70, 2026-11-09)

The brief: `docs/queue/WN4c.md` (the stranger's review at WN4b's gate: SHIP + FOLLOW-UP). Branch `batch/WN4c` from main
679abe59. A small fix batch, Sonnet 5.5 the whole session (`docs/batch.md`, 3); nothing else touched.

## 1. The fallback kept the old units (fixed)

`ForeignAccounts.restore()`, slot 148: a saved anchor whose US$ price is gone (`!(usdPer[anchor] > 0)`) fell back to
the US$ (0) with `pegRate` still the saved D$ per EURO. The peg then held that figure as D$ per US$: in
`PegAnchorCheck` 8's fixture, a euro-anchored peg saved at a rate of 1.0523 with the euro's price removed held
1.1000 (the euro figure) as if it were dollars, 4.5% off what the city held the month before.

The fix, in the same fallback: `pegRate = regime == PEG ? rate : 0`. The brief said to zero it so the peg's month
re-derives it from the rate (`if (!(pegRate > 0)) pegRate = rate`, the US$ case); that is the same value, but zero
leaves `getPegRate()` 0 from the load to the first month, a screen reading a peg of nothing and `regimeHoldsDeal()` /
`rateAfterDeal()` treating the peg as not in force until then. So the fallback sets the loaded rate itself, the very
number the peg's own line would strike, now. Outside a peg `pegRate` is zero, as the brief had it (`setRegime(PEG)`
overwrites it). The slot is restored before, so `regime` and `rate` are already the saved ones.

`PegAnchorCheck` 8, on the `unrated` fixture already there (the euro's saved rate zeroed): the saved peg is D$ per
euro and differs from the loaded rate by more than 2% (the fixture causes the condition: 4.5%); restored, the regime
is still a peg on the US$; `getPegRate()` and `getPegPerAnchor()` are the loaded rate, not the euro figure; after a
month it still holds the loaded rate. Written first: it failed three assertions (1.1000 against 1.0523) on the
unfixed code, passes on the fix.

## 2. The review's two low notes (looked at; neither wrong, so neither fixed)

- **The hold's support unclamped in the held branch of the peg's month** (`holdFelt += lift`). In a held month
  `lift = ln(held) - (x + holdFelt)`, so the support after it is `ln(held) - x`: exactly the log of the held rate over
  the shadow, which is the gap the hold pays for, and a clamp could only leave the rate short of the peg. The gap is
  what the hazard and the vault's floor already bound (a doubt or the vault breaks the peg, and a break zeroes the
  support). `PegAnchorCheck` 10 runs a quiet euro peg on a deep vault for 36 months of a euro swinging 6% either
  side (held every month, the support 13 months up and 23 down): in every held month it is the log of the held rate
  over the shadow to 1e-9 (worst 1.5e-16), and never wider than the euro's whole swing, ln(1.06/.94). Settled as
  right.
- **When a non-US anchor's `currencyGap()` is read in the month.** `Game.nextMonth` hands the moneys' rates early
  (`handTheMoneysToTheVault`, statement before anything buys), reads `currencyGap()` for the dial at statement 55, and
  reprices at 140. So the dial reads the LAST reprice's shadow over the held rate at THIS month's anchor price, which
  is the held rate the reprice then measures its gap against. A euro that rose a tenth since the last reprice moves
  the dial's gap ratio by that tenth the same month, not a month later; the alternative (the gap against last month's
  held rate) would have the dial answer to a rate the peg no longer holds. Settled as right, and pinned: in 10 the
  rates handed leave the shadow the last reprice's, `currencyGap()` is that shadow over the peg per euro over the new
  euro, its ratio moves by the euro's tenth, and the reprice's own `getLastPegGap()` is against the same held rate.

## Checks

No Game change, no model change but the one load line: nothing in a played city reaches the fallback (every
anchor a city saves has a rate the month it saves), so the default playtest's trace cannot move: no ensemble. No
`SAVE_FORMAT` bump: a save that read wrongly here is one the old code could not have written (it needs the anchor's
price removed by hand or lost with the world file). `PegAnchorCheck` 10 sections, 4 new assertions in 8, 9 in 10.
Process lines: none changed.
