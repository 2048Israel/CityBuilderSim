# WN5b - WN5's review follow-ups ({next}, 2026-11-03)

The five items of [WN5b](WN5b.md), from the stranger's reviews at WN5's and WN6's gates. Built on DM1 (lane 2, RL1). Slices by Sonnet 5.5
at xhigh, reviewed here; paused overnight 2026-11-02 on the orchestrator's word (ten sessions on one PC) and resumed when DM1's ship file was up.

## 1. What was built

1. **The hover's quote off the screen's thread.** `LandMarket.quote()` of a block splits its ground by seller since WN5, and one cold quote
   near a foreign coast takes from 45 ms to 5.3 s (section 2). The month runs on the screen's thread, so a worker cannot read the live land:
   `LandMarket.quoter()` copies the office on the screen's thread (`CityLand.copy()` plus the sellers, month, ground price and US price level;
   not the landmasses, which a hover never reads), and `MapView.QUOTES` (its own low-priority thread, not WORKER's, so a slow quote holds no
   map job) quotes the block on the copy. The card says `LandMap.QUOTING`, "Pricing the block…", until the price is in, then the hover is
   asked again; a block the pointer has left is dropped unread, the last 16 quotes are kept, and the copy is made once per month and ground
   (`shapesFor`), not once per block. LandNationsCheck 12.
2. **The island sentence.** `LandmassView.receiptWords` is a sentence of its own, "It lies on Island 2, which no port links." ("4.00 km² of
   it lies on ..."), appended after the sellers' sentence instead of cutting its full stop. LandmassCheck's three lines updated.
3. **One rounding.** The receipt's sentence is `LandSellersView.receiptLine()`, by `percents()` (largest remainder) as the card is;
   `Game.soldByWords` gives it each part's price in the seller's money. LandNationsCheck 11 causes splits whose own roundings add to 99 and
   101 (three thirds; 50.5 and 49.5 km²) and reads the same percents on the card and the receipt.
4. **The partners named.** `Game.realEffectivePartners()` is the one builder of the rate's partners (`RealDollar.Weighed`: nation, share,
   Partner) and `realEffectiveRate()` is made from it, to the bit. Trade's rate card has a muted line under the rate: "Weighs Germany 50% ·
   Japan 25% · United States 25%", the four largest and "· N more"; "Weighs the US$ alone: no nation's trade to weigh yet" (or "this world
   has no nations' moneys"); `REAL_EFFECTIVE_INFO` says the line names them. RealDollarCheck 5.
5. **`refill()`'s flag** is set after `curves = made`. No harness: a `Curve` constructor that throws cannot be caused without an injection
   seam the model does not have; WorldMarketCheck reads as before.

## 2. The measurements (scratch probes, 2026-11-02)

One cold `LandMarket.quote()` of a hover block (2^level plots a side): across the Detroit River (Windsor) level 7 up to 17 ms, 8 45 ms, 9
152 ms, 10 419 ms, 12 752 ms; off Gibraltar's coast level 8 76 ms, 9 159 ms, 10 449 ms, 11 1.3 s, 12 5.3 s. On a copy of Jerus's autosave
(level 8, 3.76M plots, 90 purchases) up to 76 ms over 75 blocks along its edge. Without sellers the same quotes are under 20 ms.
`CityLand.copy()` on that city: 0.2-1.1 ms. LandNationsCheck 12 measured again in the suite (Windsor 8: 6.8 ms warm, 10: 135 ms, 12: 1.31 s;
the Strait 9: 39 ms, 10: 313 ms, 11: 1.06 s; 7 of 17 quotes over a frame); a quoter's making at worst 0.27 ms, held under
`LandMarket.QUOTER_MS` (16 ms) by `Timing.within`, so LandNationsCheck is in `Timing.HARNESSES`.

Item 4: on world-1.bin (LC3's), all 196 nations' first-January money has a rate in WorldMoneys' opening month (Historical and Random), so
`RealDollar.moneyAtFounding`'s fallback to the file's first-year rate is taken by none. RealDollarCheck 5 counts it ("0 of 196").

## 3. The ★ calls

1. ★ **Item 4's fallback fires for no nation on today's file.** Kept as WN6 wrote it (a later file may lack a rate), held and counted in
   RealDollarCheck 5. Recommended, taken: keep it.
2. ★ **A click that picks a block, and the month, still split on the screen's thread.** `picks.toggleBlock` → `LandMarket.draft()` measures the
   picked block with its sellers, and `remeasureDrafts()` measures every picked block each month: the same 45-76 ms a block at level 8 near
   a coast, seconds at 11-12. Recommended, taken: queued as WN5c, which measures a click and a month with picks first and moves them only if
   either can take more than a frame (the alternative, leave it, costs a hitch per click near a border).

## 4. The checks

`./batch-check.sh --pick`, the data files named (`JDK_JAVA_OPTIONS`, -Dcbs.earth and -Dcbs.world from the main checkout), again after
main was merged in (DM1 as 0.9.8, FC1b): exit 0, 51 harnesses green in 1,003 s, two timing lines soft (SaveSlotCheck 1, MapCheck 1). On
DM1's tip first: exit 0: 51 of 170 harnesses (14 files changed; Game.java left to the gate, LongPlaytest in its place) green in 1,164 s, maps
regenerated, Stale 0 firm. Three timing lines soft and not counted on a PC running other batches (50 starts held for memory):
MapCheck's month of FX-side map work at 5B (22.1 ms against 19) and its median due month (33.7 against 25), SaveSlotCheck's change
at 10B (6.0 against 5); none of them makes a quoter, which only a hover does. `--pick`'s threading review was this one (§1, 1:
the copy's land is its own, `split` reads the land it is handed, one QUOTES thread and a synchronized Quoter). The new lines:
LandNationsCheck 11 "[34, 33, 33] / [34, 33, 33] | [51, 49] / [51, 49]", 12 13 blocks to the bit; RealDollarCheck 5 "0 of 196
nations took the fallback (194 of 194 agree)".

Not a process batch: no script, check machinery or rule a session reads changed (one in, one out does not apply).
