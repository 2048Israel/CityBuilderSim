# FX8 - notes: XR2's loose ends off the default path

*0.8.23, 2026-10-22. [The brief](FX8.md), from the orchestrator's stranger review of XR2 (0.8.22); Jerus put it at the top with FX7 the same day ("FX7 + FX8 now"). Built from main 74197f0 (0.8.22), beside FX7 (the map's files), sharing LongPlaytest with it: FX8 changed only the identity's lines there.*

## 1. What shipped

1. **The central bank keeps nothing, restated.** The identity was "equity less the vault is the remittance not yet paid less the loss carried". XR2 made it false: when the central bank pays the strong edge's dollars in new money (`CentralBank.buyDollarsForVault()`, the default payer), M0 and the vault rise together, so equity is unmoved and equity less the vault falls by exactly what it paid. The identity is now `equity - vault + getDollarsBoughtLifetime() = remittance due - loss carried`. **This restates an identity the model changed; it does not loosen one.** The added term is exact: it is the sum of the local money issued for the dollars, and the old statement misses by minus that term to the cent (CurrencyRegimeCheck 9). Every statement of it moved together: CentralBankCheck's month, LongPlaytest's month (the identity's two lines and their comment; its layout watch untouched), CurrencyCheck's defence months and HoldersCheck's buyback. All four run under Lean, where the term is 0. CentralBank's banner said "the treasury bought the vault with its own cash, not with money made here". It now says what the treasury bought and what this bank bought, and why the term is added back.
2. **Leaving a peg clears its doubt.** `ForeignAccounts.setRegime()` sets doubtShare to 0 when the regime left is PEG. Game hands that share to the hot money before the next reprice. So a peg left between two presses used to send the hot money home at the dead peg's hazard for one more month, against Game's own "0 under every other regime". lastHazard and lastPegGap are the last reprice's record, not an input, and are kept.
3. **The hot money's month is saved.** `CapitalFlows.toSaveArray()` appends arrived, departed and doubtDeparted at slots 16-18, making the array 19 long. `restore()` reads them past 18. A shorter array leaves them at 0, which is true of a new CapitalFlows. Policy shows the doubt's departure the month after ("of which ... was hot money leaving on doubt"), and a load dropped that clause. Nothing in the next month reads the three before `takeMonth()` resets them, so a loaded city plays on exactly as before. That is asserted to the bit (CurrencyRegimeCheck 12). DataSave carries the array whole, and the load path already calls `restore()`, so neither changed.
4. **HoldersCheck's array length is exact.** `full.size() >= 15` became `== 19`, with a comment: whoever appends next is stopped there to ask whether a cut at 12 is still a save from before the world held any paper.
5. **The vault's record counts the central bank's edge purchases** (the brief's first choice). `ForeignAccounts.noteEdgePaidByCentralBank()` is called from `Game.settleTheEdge()` with what `buyDollarsForVault()` paid. lifetimeIntervention already counted the central bank's defence sales; now it counts the central bank's purchases at the edge too, so Trade's chip "net bought into the vault" no longer leaves out every dollar a peg or a band bought on the central bank's money. Its sentence and the chip's tooltip say so. This is not the month's purchases (`getBoughtThisMonth()`, MoneyAudit's treasury line), which stay the treasury's.

Under Lean, the default, all five are off: no edge is ever bought, the doubt is always 0, and the three flows were already recomputed each month.

## 2. The checks that cause each

All of these are in CurrencyRegimeCheck. Each went red on 0.8.22's model: the four fixes were reverted on a scratch build, and the run gave 9 FAILs plus an index exception in 5, where the array is 16 long.

- **5:** the array is read by its slots.
  - The old "the last slot is the lifetime" passed only by coincidence once the array grew.
  - Slots 16-18 restore to the bit.
  - A second bare month, with no arrival, an organic departure and a doubt (0.0k, 5,492.4k, 1,201.5k), tells slots 17 and 18 apart.
  - An array cut at 16 restores with the three flows at 0.
- **9, in both payers' cities, 5 months each:** the restated identity holds in all 10 city-months.
  - The central bank's city paid US$28,110k in new money, and the old statement misses by exactly that.
  - In the settlement month the vault's record rose by the whole 6,573.5k bill in both cities. The fixtures assert that nothing else dealt in the vault that month.
- **11, the city pegged at founding:** the payer is the default (the central bank). The identity holds in all 240 months, and the central bank paid US$41,144k for dollars in 2 months before the vault broke the peg in month 24.
- **12, new:** a pegged city in the month its hot money left on doubt (arrived 6,573.5k, departed 2,751.3k, all on doubt), saved and loaded.
  - The three flows, the net flow, the lifetime, the stock and Policy's US$ figure are equal to the bit.
  - A month on, so are both cities' figures.
- **13, new:**
  - A bare peg with a 6.10% doubt, left for Float, Lean or Band, has no doubt. Re-pressing Peg keeps the doubt, and leaving and re-entering the peg does not revive it.
  - Two loads of 12's save are twins. The one that stayed pegged lost 1,988.8k on doubt the month after; the one moved to Lean lost 0. The audit closes in both.

SaveFileCheck reads the four flows across its shared city's save. That city's month has 628.6k arriving and nothing departing, so its departure and doubt read 0 == 0; 12 is what causes them. ReadPathCheck's sweep reads the new and moved getters.

## 3. The suite, the trace, the timing

The suite (`./batch-check.sh`, soft timing beside FX7): 128 of 129 harnesses green.

- **The one red is main's own:** LongPlaytest's map watch, "an old works moved" at m4000, which is FX7's to fix. The baseline on 74197f0 showed the same red.
- **Stale:** 0 firm findings.
- **The default playtest's trace:** byte-identical to main's but for its two run-time figures (the run's seconds and the layout watch's), so there is no ensemble (EN1).
- **The timing harnesses, run hard alone (`--timing`):** all 8 green in 150 s, once FX7's JVM had finished. No earth-1.bin here, so the Earth harnesses' full-file sections did not run.

## 4. For Jerus

Nothing new. The brief's two questions are his and already in XR2-C:
- a peg pays the month's balance, not the pressure;
- the curve's expected path under a tied dial.

## 5. The slices

- **Claude:** the model's five fixes and the identity's restatement in the four harnesses, which are small enough to be cheaper made than briefed.
- **Sonnet 5.5 at xhigh effort:** one slice, the harness sections (CurrencyRegimeCheck 5, 9, 11, 12 and 13; SaveFileCheck; ReadPathCheck). The review changed only a counter's name in 11 (`kept` read as its opposite).
  - The slice added the second bare month in 5, which the brief had not asked for: the first fixture's departed equalled its doubtDeparted, so it could not tell slots 17 and 18 apart. That was right.
  - The mutation run (section 2) is Claude's, and it is the review's evidence that the new sections test the fixes.
