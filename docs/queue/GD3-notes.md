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
