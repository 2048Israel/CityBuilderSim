# GD3 - the gold standard ({next}, 2026-11-10)

The brief is `docs/studies/spec-gold-money.md` §4.3 (GD0, 2026-11-02; Jerus
took every call the recommended way). Built on XR5 (the hold pays the
pressure) and GD2 (gold, XAU, the vault's ninth). Lean stays the default.

## 0. The design, as the slices were briefed

**The regime.** `ForeignAccounts.Regime.GOLD("Gold")`, at the END of the enum
(its ordinal is saved, slot 71). Every other regime's numbers are as before.

**The mint price** `M`, D$ a troy ounce, and the **mint rate**, the D$ per US$
it was struck at (`M` over gold's US$ price that month). Entering Gold strikes
`M` = the day's rate x gold's price, so the D$ keeps its value (1900 at 1.00:
D$20.6718, the US$'s own 1.504631 g). The player may type a par instead
(Britain's 1925 "old par"). Gold cannot be entered in a month gold has no
price (`hasRate(GOLD)` false: no world).

**The rate** is held inside the gold points around the centre `M / gold`,
D$ per US$: `GOLD_POINTS` .005 either side (est.; Officer's ±0.5%). It is a
band whose centre moves with gold, run as XR5 runs a band: free = the shadow's
log plus `holdFelt`; past the weak edge the vault sells what lifts the rate
back to it, **gold first** (`vaultRemove` under Gold empties gold before the
largest-first rule takes the rest); past the strong edge it buys, **into
gold** (the mint's free coinage), paid by the edge's payer (the central bank
in new money by default, as today). Until 1933 the centre is 1.00 for a city
that entered at 1.00; in February 1934, if it kept its gold, 0.5906.

**The cover** = the vault's gold at `M` over M0 (`CentralBank.m0()`, handed to
`ForeignAccounts.setMoneyBase()` right before the reprice; not saved, handed
every month). The legal floor is `COVER_RATIO` .40 (the Federal Reserve Act's
40% against notes). Free gold = gold x M - .40 x M0.

**The break.**
- By the cover: a month whose hold would SELL and leave the cover under
  `COVER_RATIO` (or sell more than the vault's gold) suspends convertibility:
  `BreakCause.COVER` (new, at the END of its enum, ordinal saved slot 90), the
  rate lands at the shadow plus `OVERSHOOT` of the gap, the regime is Float,
  credibility takes its dent. Under Gold it replaces the vault's one month of
  imports (`VAULT_FLOOR`). A purchase never breaks it.
- By doubt: the peg's chance, `hazard(gap)`, times clamp((1 - cover) / (1 -
  COVER_RATIO), 0, 1), from the peg's own stream of draws: the peg's chance at
  the legal cover, none at a full cover (the boards' rule given to the city).
  `goldHazard(gap, cover)` is that, pure.

**Defend the gold.** `DebtManager.Aim.GOLD("Defend the gold")`, at the END
(saved by name). Under Gold the dial is tied to the US's rate (`tieRate()`,
tie money "USD" whatever the peg's anchor), plus a spread:
- the autopilot aiming at the gold: `goldSpread(cover)` = `GOLD_DEFENCE` .10
  (est., inside the probe's K 10 and K 25) x clamp((`GOLD_DEFENCE_FROM` .80 -
  cover) / (.80 - .40), 0, 1): nothing at twice the floor, the whole at it;
- the autopilot aiming at the currency or a mix: the peg's `CURRENCY_WEIGHT` x
  the gap;
- the autopilot off: the player's spread (`pegSpread`), as under a peg;
- the autopilot aiming at inflation: the inflation rule, untied ("aim at
  inflation instead and watch the gold go"); `isTied()` is false then.
Entering Gold sets the aim to Defend the gold (recorded); leaving Gold by
choice with that aim sets Inflation (recorded); a break drops it to Inflation
silently (the city's fate, not a decision).

**The US$'s three dates** (GoldPrice's months, the same on Historical and
Random): `GoldEvent` LEFT_1933 at `GoldPrice.FLOAT_START` (April 1933), PAR_1934
at `FLOAT_START + FLOAT_STEPS` (February 1934), WINDOW_1971 at
`LAST_PAR_MONTH + 1` (August 1971). In that month, a city on Gold has the
decision opened (1933 and 1934 only if it is not already following the
dollar), saved, and a NEEDS YOU notice keyed `gold:<event>` stands while it is
open. The three answers (`GoldChoice`):
- STAY, on gold at `M`: the D$ dearer as the dollar falls (the gold bloc);
- FOLLOW, the mint price moves with the dollar: `M` re-struck at once at the
  mint rate times gold's price (the D$ back at the US$ rate it was struck at),
  and kept there every month while following (the reprice re-strikes it as
  gold moves): a devaluation with the dollar;
- LEAVE gold: the regime goes to Lean, the city's default.
Until it is answered the city stays at `M`. Choosing another regime leaves
gold; the gold stays in the vault at gold's price. Choosing Gold again asks
the par.

**The save** (`foreignAccounts`, no format bump): slots 152-155, the mint
price, the mint rate, whether it follows the dollar (1/0) and the open event's
ordinal; read only when the array is that long. An older save has no slot 152
and no Gold (ordinal 4 did not exist), so it loads as it did.

**GD2's two review findings, mended here:**
1. `Game.goldUsdPerOunce` said NaN but `GoldPrice.level()` threw on a US price
   level not positive or finite, and that reached `nextMonth`: caught to NaN.
2. `Game.eightOf` read `moneyIndex`, which knows "XAU" since GD2: a money
   anchored to gold would have landed on gold and been dropped from the
   eight's trade. It reads `WorldMoneys.EIGHT.indexOf` now; every other
   `moneyIndex` caller Gold reaches is checked (the tie money is "USD" under
   Gold, never "XAU").

**The trace:** byte-identical expected. Nothing runs unless the regime is
Gold; the two findings' fixes change no number a default city computes.

## 1. What changed

- **The model** (`ForeignAccounts`): Gold's step 7 of `repriceCurrency()` is
  XR5's band against the gold centre, with the cover check where the band asks
  the vault floor; the peg's break is now `breakTheHold()`, shared, its
  arithmetic unchanged. Under Gold `vaultRemove` empties gold first (so a
  sale of the vault on Trade under Gold ships gold first too) and `edgeBuy`
  buys into gold. A reprice under Gold with no gold price ever handed lets the
  float's rate stand and breaks nothing. Readers: `goldCentre`, the two
  points, `goldAtMint`, `goldCover(m0)`, `goldCoverAt(price, m0)` (the cover
  at a staged par), `freeGoldUsd(m0)` (negative under the law's cover),
  `goldHazard`; `currencyGap`, `breakLanding`, `monthsToFloor` (free gold
  over the month's sale), `rateAfterDeal` and `regimeHoldsDeal` know Gold.
- **The dial** (`DebtManager`, `Game`): as section 0. `isTied()` is true under
  Gold unless the autopilot aims at inflation, so the curve is priced on the
  tied path as under a peg.
- **The US$'s dates**: `Game.takeTheDollarsGoldDates()` right after the
  moneys reach the vault; `Inbox.takeGold` raises "gold:<date>" while one is
  open, its body what the US$ did, the mint price, the centre and the three
  answers. `answerGoldEvent` writes a decision line for each answer (LEAVE
  writes "Left gold" and then the regime's own line).
- **The screens**: Policy › The currency has Gold as its fifth chip (refused,
  saying why, in a month gold has no price), THE GOLD STANDARD card (the mint
  price with ±1% steps, the centre and points, gold's price; the cover against
  40%, the vault's gold, free gold, the pace; following; the shadow; doubt;
  the US$'s decision with its three buttons) and its note when staged (the par,
  the old par as a chip, the cover it would make, "buy gold first" with a door
  to Trade). Defend the gold is offered only under or entering Gold; THE DIAL
  UNDER GOLD is the peg's card in gold's words; the payer card and the aim's
  notes say Gold where they said "a band or a peg". Trade: Gold read as a band
  at the gold points (the hold line, the deal card, the exchange card's "gold
  first"), and a break by the cover is "the gold gave way".

## 2. The save

Slots 152-155 of `foreignAccounts` (the mint price, the mint rate, following
1/0, the open date's ordinal), read only when the array is that long; M0 is
handed every month and not saved. `Regime.GOLD` (ordinal 4, slot 71) and
`BreakCause.COVER` (ordinal 3, slot 90) are at their enums' ends, and
`Aim.GOLD` is saved by name. An older save has no Gold and no mint, and loads
as it did: no format bump. The five length checks read 156; VaultMoneysCheck's
"as it was before gold" array is still 149 slots (now `SLOTS - 7`).

## 3. The checks

- **GoldStandardCheck** (new, 12 sections, about 10 s on its own), the
  figures it prints:
  - 2: twelve deficit months sell US$3,355.3k out of the gold, the US$
    untouched, a band of GOLD_POINTS dealing the same; a surplus buys
    US$3,379.8k into it.
  - 4: the sale leaving the cover 1% under COVER_RATIO breaks (COVER, Float,
    at `breakLanding()`, nothing sold), its twin 1% over holds; a purchase at a
    tenth of the floor holds; a vault under VAULT_FLOOR on Gold holds where the
    same vault pegged breaks by VAULT.
  - 5: doubt over 100 seeds x 12 months at a gap of about +.25: 0 breaks at a
    full cover, 23 at the midpoint (24.2 expected), 39 at the legal cover
    (42.8 expected, sd 6.4).
  - 7-8: the town (40,000k of the founders' D$ into gold, month 1) on gold to
    March 1933, its cover from 37 in the 1910s to 6.045, never under the
    floor; STAY: February 1934's centre 0.589231 = March 1933's 0.997642 x
    PAR_1900/PAR_1934 (the D$ 69.3% dearer), the rate at the weak point and the
    PAR_1934 date open; FOLLOW: the centre at the mint rate all the way, no
    1934 date; LEAVE: Lean and Inflation. The notice is raised in the date's
    month and resolved the month after the answer.
  - 9: at a cover of .600 the dial is 0.110000, `goldRate` at the cover the
    month read; aiming at inflation it is untied; a forced break drops the
    aim with no decision line.
  - 10: saved with the 1933 date open and again after FOLLOW, the next twelve
    months the same to the bit (0 of 12 differ, both rounds).
  - 11: GD2's findings caused: a `GoldPrice` on a US price level NaN from
    1980 throws at 1990 on its own, `Game.priceOrNaN` gives NaN; `eightOf("XAU")`
    is not gold, and `tradeSharesByMoney` gives gold no share.
  - 12: a new town is Lean, aims at inflation, has no mint, no date and no
    gold 24 months on.
- **SaveFileCheck**: slots 152-155 against the getters off gold, and a bare
  currency on Gold following with a date open restored to the bit, its next
  six reprices equal. ReadPathCheck lists none of FA's slots.
- **The moneyIndex trap** (finding 2): under Gold the tie money is "USD", so
  `setPegSpread` and the world rate never see "XAU"; `setPegAnchor("XAU")` is
  refused; `sellReserves`, `spendReservesOnLand`, `vaultAddInto` and the
  borrow and trade money paths take codes from the eight. Only `eightOf` was
  exposed, and it reads `WorldMoneys.EIGHT` now.

## 4. The trace, the review, the calls

- **The trace is main's**: the default playtest traced (`-Dplaytest.trace`,
  the Earth and world files named) on main's classes (0895f70e) and on GD3's
  model (0089e0bd). The eight files are byte-identical, and the report differs
  only in its run-time lines (49 s and 54 s). The default city is on Lean and
  never enters Gold. No ensemble (EN1). What came after the model adds only
  pure getters: `goldCoverAt` (the cover at a par not yet struck, for the
  staged note) and `mintAtToday` (the par entering strikes, which
  `setRegime(GOLD)` now reads: the same product).
- **`--pick` exits 4 on one line, KeptCheck's count**: 8 FAILED against main's
  recorded 9. The 8 are recorded labels word for word, and main's "the kept
  header's signature equals a header built new ... (18 differ)" passes here.
  It is not mended, only not reached. KeptCheck's currency loop runs once per
  `Regime.values()`, so Gold adds a pass, and its months on the shared city
  change what section 98 finds. On main (run today, 9 FAILED) the difference
  is a hidden label of the RATE tile, "D$1,000,000,000.00 per US$" (the rate
  at `ForeignAccounts.MAX_RATE`): it keeps its old size in the kept header and
  has none in one built new. That fault is still main's. The gate's KeptCheck
  record then wants 8, without that label.
- **KeptCheck stages all five regimes**: its loop staged `(ordinal + 1) % 4`,
  written for four, so Gold was never staged and the staged gold note never
  met the kept redraw. Now `% Regime.values().length`, as its comment
  ("every regime the city can reach") meant.
- **The review** (`--pick`'s line: money, `DebtManager.java`). In DebtManager:
  - `Aim.GOLD` is at the end of the enum (the other three keep their ordinals);
  - `GOLD_DEFENCE`, `GOLD_DEFENCE_FROM`, `goldSpread`, `goldRate` and
    `dropGoldAim` are new;
  - `tiedRate`'s one change reads aim GOLD as the player's spread.
  No aim saved before this batch reads differently. The line is a floor: add
  saves (`ForeignAccounts` slots 152-155 in `DataSave`'s existing array) and
  the vault's money under Gold (`repriceCurrency`'s GOLD branch, gold first in
  `vaultRemove`, `edgeBuy` into gold).
- **What to look at**: the five renders in `docs/queue/GD3/`:
  - `currency-gold.png`: in force, cover 53%, free gold US$9,748;
  - `currency-gold-thin.png`: cover 25% in red, with the law's warning;
  - `currency-gold-1933.png`: the April 1933 decision and its three buttons;
  - `currency-gold-staged.png`: the par staged 2% devalued, the buy-first
    warning, the door to Trade;
  - `currency-gold-narrow.png`: at 520 wide the page clips on the right. The
    equal-column rows clipped the payer and aim row before GD3; Gold's fifth
    chip and the cover's cells clip with them.
- **No write-up in the project**: as GD2's, the study (`spec-gold-money.md`
  4.3) is the design and the changelog the record.
- **Calls** (★, each taken as recommended):
  - LEAVE goes to Lean, the city's default, not Float.
  - The founding screen's "start on gold" box waits for FM1, which rewrites
    the founding money line (Pending GD3-F). Recommended: enough of the
    founders' vault into gold for the 40% cover, not all of it.
  - The 1933 notice states the game's fixed path for gold (US$20.67 to US$35
    over ten months, `GoldPrice.official`), since that is what the city meets.

## 5. The gate's timing red, run 1: the machine's

- **What stopped it**: "the kept month on the construction page is within
  KEPT_CONSTRUCTION_MS (4.0 ms)", main 2.06, 1.94 and 1.89 ms against the
  batch's 1.99, 6.00 and 7.44.
- **Every figure moved with it.** In the batch's second round every KeptCheck
  timing rose two to four times: the fund's Activity read 685 ms where the
  other five rounds read 147-231, and the Sectors list 23.7 ms against
  6.4-12.5. Main's third round swung too (the Sectors list 11.9 ms, Retail's
  Operations 10.5, the policy rate page 6.1). The batch's first round matched
  main's to the tenth.
- **GD3 cannot reach it.** Section 43 runs before section 77, the currency
  loop this batch changed, on a city still on Lean, and no GD3 code is on the
  construction page.
- **Eight alternating warm runs of KeptCheck alone**: main f9d92fdd against
  the branch merged with it, in the order main, batch, batch, main, twice,
  each stopped after section 43.
  - main: 3.6, 3.6, 3.8, 3.5 ms
  - the batch: 3.6, 3.5, 3.5, 3.7 ms
  - built new: 14.3-19.7 ms on both sides
  Both sides read above the gate's 2 ms here, outside its timing run; side
  by side they are the same. Noise, so nothing was mended.
