# FW6 - a reloaded city plays its next month as the one that kept running ({next}, 2026-10-25)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md` (FOUND ON THE
WAY IN 0.7.46 TO 0.7.49, the ★2 residual; 0.7.55 TO 0.7.64, "left by the
batches"). At 0.7.49, 12 of 229 series differed a month after a reload, by at
most 8.3e-5; at 0.7.64 city2400 was 0.23k off and his old city -7,447k. Much
has changed since, and FW2 (0.8.38) had just fixed two sheets' reads after a
load. So today's drift was measured first.

## 1. Today's drift, measured (main, 0.8.45)

The method: the probe city (`LongPlaytest.probeCity()`, Danzik on the
generated world, played by `probePlay()`) saved at a month, loaded into a
second `Game`, both run one month (then two, then 24), both saved to scratch
slots, and the two saves' and histories' JSON compared leaf by leaf; and a
reflection walk of the two `Game` object graphs, for what no save holds.
Rotterdam on the real Earth (earth-2.bin) the same way. His saves could not
stand in: the newest is format 44 and this build loads 45 and up.

**The history: no drift.** Every HistorySave series was identical, to the
bit, a month on, two months on and 24 months on: 241 to 257 series, up to
608,481 numbers (Danzik saved at m29, 123, 364, 605, 1205 and 2405, 362,000
people at the last; Rotterdam at m123 and 1205). The 0.7.49 residual (the
sector net incomes, `bankDeposits`, the energy ratio, Retail's budget) is
gone.

**The save: 7 to 14 numbers and 24 to 27 other leaves off, a month on**, of
24,000 to 83,000:

| what | live against reloaded | why |
|---|---|---|
| each sector book's `stockRevalued`, `stockCounted` | up to -63,497 against 0 (m2405), every sector `true` against `false` | the stock the books revalue is in memory only: the month after a load counts no revaluation |
| Construction's `drawnImported`, `drawnLocal` | 3,583 against 0, 612 against 268 (m1205) | its first strike after a load read the raw row, which a load empties |
| `lastInvestment` (Heavy Industry, Refining) | "a Large Mini-Mill would earn $537,908k" against "a Scrap Yard would earn $16,225k" (m2405) | the markets' month tally is not saved, so the first investment pass after a load prices every works' feed and output on an empty one |
| `priceIndex[76]`, the trailing year's luxury spend | 12.64 against 0 (m123) | a live-city bug: see 2.9 |
| `creditMoved`, `sectorDebt` | 1 ulp (1.5e-8 on 1e8) | the bond book's running sums restart at a load, so a sum is grouped differently |
| `mapStamp` | a content hash of the map sidecar | the first month after a load re-lays the railway (left, 4) |

**In memory, never saved:** the shares of both months (the Sectors screen's
share report read "not counted", then a blank last month, for two months
after every load), the price index's rent weight (0.436 against 0.771 at
m605: `restore()` rebuilt it by the two-part basket's rule), every market's
tally (all zero after a load), the sudden stop's words (a blank reason on
the Trade screen's card for up to 18 months), and the skip report (months
sick 1,210 against 6: a live-city bug, 2.8). The rest of the walk's
differences are run counters nothing reads, the markets' rotated 36-month
rings, caches rebuilt on demand and interface state (a triage of all 22
groups of fields, by who reads each, found none that moves a month).

## 2. The causes, and what was done

1. **The markets' month tally** (the only one that changes what the city
   does): `Markets.State` carries each market's imported, exported, filled at
   home, offered, bid and the draws not yet struck; `GoodsMarket.restoreTally()`
   puts them back. The trades list stays unsaved (its screens say "not counted"
   until a month runs).
2. **Quarrying's carry**, through its extras: the first pass after a load
   weighs limestone by it.
3. **Construction's materials drawn**: `beforeBank()` reads
   `unitsBoughtLocal()`/`unitsImported()` (the row plus the carried month),
   equal to the row in a city never loaded.
4. **The books' stock at close**: `SectorBooks.stockToSave()`/`restoreStock()`,
   `DataSave.sectorStock`.
5. **The shares, both months**: `sharesToSave()`/`restoreShares()`, as FW2 saved
   the debt; `DataSave.sectorShares`, `sectorSharesBefore`.
6. **The stop's words**: `DataSave.capitalFlowsStopReason`,
   `CapitalFlows.restoreStopReason()`.
7. **The rent weight**: a based index with no link pending takes
   `weight[RENT]` after `restoreChain()`.
8. **The skip report** (live city): `TimeSkipReport.beginSkip()` reset every
   episode counter but the six health ones, so a long-lived city's report
   added every earlier skip's outbreaks, sick months and worst ratios.
9. **The meals and luxuries of an older month** (live city):
   `LuxuryCounter.dine()`/`shop()` cleared their own figures and returned
   before `takeMeals()`/`takeLuxuries()` when nothing was priced, wanted or
   sold, so the households' `lastMealSpend`/`lastLuxurySpend` kept an older
   month's spend and the price index booked it as this month's. Both are
   cleared at the top now; the index is their only reader.

No `SAVE_FORMAT` bump: an older save lacks the new keys and loads as before.

**After:** the save and the history equal the running city's to the bit a
month and two months on at m606, 1206 and 2406 (only the slot's name and the
time saved differ); ReloadCheck green, Rotterdam included.

## 3. The harness

`ReloadCheck` (new, in the suite after SaveFileCheck, 32 s with the Earth
file named): the probe city saved at about m30, 120, 360 and 1200 (four,
because the investment notes differed only at 123 and 1205) and Rotterdam at
m120, each reloaded, both played a month and two; (1) every history figure,
(2) every figure in the save, to the cent and a billionth, the slot's name,
the time and `mapStamp` exempt; (3) each cause on its own with a fixture that
the live city exercised it; (4) the real Earth. **Red on main: 32 FAILED**
(section 1 green; 2, 3a-f red); green here. SectorStatementCheck 6 asserted
"the shares are not saved, and read not counted", which this batch reverses
on purpose: it now asserts they read the saved city's (★2).

## 4. The checks

{checks}

**The trace:** the default playtest's is byte-identical to main's but for its
run-time lines (and the layout watch's seconds), so no ensemble was run
(EN1). The live-city fixes 8 and 9 do not reach it.

## 5. Calls

- ★1 `mapStamp` is exempt in ReloadCheck: the first month after a load
  re-lays the railway (`CityRuns.railStampSet` false in a map just read), so
  the sidecar's bytes can differ. Nothing in the model reads the map; whether
  the drawn railway moves is CC1-M's question (the map across a save).
  Recommended: leave it to CC1-M, under Pending as FW6-C.
- ★2 SectorStatementCheck 6's premise reversed (shares not saved -> saved).
  Recommended: keep; it held a documented limitation this batch removes.

## 6. Left, and found on the way (FW6-C)

- The map after a load (★1).
- Run counters a load restarts, with no reader in the model: the bond book's
  issued and repaid so far (the ulp in `creditMoved`), `renewedLifetime`,
  `monthsPayoutHeld`, the hot money's peaks; the `carried*` load figures are
  never cleared after their one use (harmless: read only on the load path).
- Found: the seller-priced markets (groceries, housing, luxury, meals) never
  clear their trades or fills, so at m606 housing held 1,161 trades and
  `localFilled` was a since-founding total; nothing reads them, but the lists
  grow for the city's life. Unmeasured past m606.
