# XR1 - notes

*0.8.19, 2026-10-21. The brief is the exchange-rate study,
[spec-exchange-rates.md](../studies/spec-exchange-rates.md) §3.1-3.4, §4's
board 1 and §5's XR1 row, with Jerus's answers of 2026-10-21 (★1, ★2, ★7,
★8). Jerus on today's rule: "the current one is wierd".*

## 1. What changed

The D$ is now a level, struck every month:

    ln rate = parity's trend + gate x (the real gap's level + the balance's + the cover's + the market) + interventions still felt

- **Parity** is the city's basket with rent's weight held to a third
  (`PriceIndex.RENT_CAP`, ★2), chained beside the index on the same bases
  (`getParityLevel()`), against the world's prices.
- **Its trend** (Holt: a sixth of a jump at once, the slope over two years)
  is followed one for one: no lag (`TREND_LEVEL`, `TREND_SLOPE`).
- **The real gap's level**, `-GAP_CAP x tanh(PERSISTENCE x gap / GAP_CAP)`,
  60% of a change at once and the rest with a nine-month half-life (★8).
- **The balance** (a year's overall balance against a year's GDP, x0.5,
  at most 20%) and **the vault's cover** (an empty vault 10% cheaper, none
  at `THIN_COVER` months), each reached half in a month.
- **The market**: AR(1), 2.2% a month, half-life 42 months, seeded from the
  world seed at founding (`Game.marketSeedFor()`), one seed one path (★1).
- **The gate**: the market and the fundamentals reach the rate in proportion
  to openness until the city trades a tenth of its output; nothing below
  `MIN_TRADE`.
- **Interventions move the rate** at 2% per 1% of a year's GDP, half gone in
  six months (★7): the treasury's purchases and sales, felt at the next
  reprice whenever they were made (`dealtSinceReprice`, saved), and the
  defence's sales.
- **The defence leans**: in a month the market and the fundamentals would
  cheapen the D$, the central bank sells what takes back `absorption()` of
  that fall, at most `absorption()` x the month's deficit and the vault, and
  the support fades. It leans against neither parity's trend nor its own
  support fading (§4 below).
- **Retired**: the pull at REVERSION, the anchored drift (and
  `Game.anchoredDrift()`), RATE_PULL's push and its UIP level,
  SETTLING_MONTHS, DEAD_BAND, DRIFT_SPEED, the previews.
- **The save**: `SAVE_FORMAT` 43, the floor raised to 43 (an older save's
  currency would load wrongly: its trend would open at the founding's
  parity). `HistorySave` gains `fxFair` and `fxMarket`.

## 2. The slices and their review

| slice | by | what | the review changed |
|---|---|---|---|
| 1 the model | Sonnet 5.5 xhigh | PriceIndex's capped parity, the float, Game's wiring and seed, every caller restated | (to fill) |

## 3. Found on the way: the float spirals

**XR1 as designed makes the default playtest's prices run away**, the 0.7.2
ring the study's §3.2 warned about, and no dial inside XR1 stops it. The
default playtest, 4,000 months, each variant built from 58f7d2b with one
dial changed (prices at the checkpoints near m600 / m1060 / the end, against
founding; people at the end):

| run | what | prices | people |
|---|---|---|---|
| main d2869a5 | today's rule | 4.7 / 15 / 1,036 | 573,349 |
| XR1 as built | Holt trend, level 1/6, slope 1/24 | 14 / 12,225 / 1.7e9 (the D$ at its 1e9 guard from m2160) | 10,877 |
| A | level 1/6, no slope | 8.6 / 1,502 / 2.3e9 | 167,882 |
| B | level 1/24, no slope | 63 / 346 / 2.0e6 | 325,960 |
| C | level 1/60, no slope | 7.1 / 33 / 26,790 | 494,579 |
| D | as built, the market quiet | 199 / 1,357 / 2.9e9 | 67,692 |
| E | as built, the real-rate gap off | 108 / 47,017 / 1.5e9 | 12,988 |
| F | as built, the balance off | 62 / 86,520 / 3.4e9 | 37,245 |
| G | the slope the credible target (the retired anchored drift), the level pulled to parity at a 42-month half-life | 12 / 72 / 1.6e6 | 225,194 |
| H | the same, a 120-month half-life | 7.3 / 23 / 13,638 | 101,633 |
| K | today's own anchor (a pull of 0.004 and the anchored drift) under XR1's other parts | 7.8 / 32 / 11,905 | 4,795 (stalls from m1052) |
| L | as built, rent uncapped | 13 / 6,401 / 2.7e9 | 283,547 |

No single new force drives it (D, E, F): following realised parity one for
one does. A price rise moves parity, the rate follows it, imports get dearer
and prices rise again. The shelf is cost-plus and ratchets
(`Retail.repriceShelf()`: under its floor a price catches up at
FLOOR_CATCH_UP, over it it moves at CLEAR_SPEED and grows at expected
inflation), so every swing in the rate leaves prices higher. Today's rule
never followed parity (0.70 of it in the playtest), and that lag was the
price level's anchor. The study's replay of slot 3 was offline ("the rate
does not feed back into the city's prices or trade here"), so it could not
see this; §3.2 asked XR1 to measure it.

**Asked of Jerus, 2026-10-21 (XR1-C ★1):** (a) hold XR1 and restudy the
anchor - the study's §3.4 pass-through dial on import prices, or a shelf
that falls as readily as it rises - with main on today's rule meanwhile
(recommended); (b) ship XR1 on today's pull and drift (K: the city stalls);
(c) ship only the intervention fix on today's rule.

**The defence's first cut** (sized "as today", absorption() x the month's
deficit) held the D$ 26% over its fair level in MonetaryCheck's town by
month 26 and emptied the founders' vault by month 42; the fall after it fed
a held 3% dial 45% a year. Leaning against its own fading support as well,
25% over by month 36 and the vault at 1.3 months by 60. The lean as built
takes back absorption() of each month's new fall and lets the support fade.

**The player's own purchases never reached the rate** in slice 1: they are
made between months, and the next month's startMonth() cleared them before
the reprice. They are now kept since the last reprice (slot 70).

## 4. The defence, redesigned in review

(to fill)

## 5. The checks

(to fill)
