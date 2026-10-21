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

## 3. Found on the way

(to fill)

## 4. The defence, redesigned in review

(to fill)

## 5. The checks

(to fill)
