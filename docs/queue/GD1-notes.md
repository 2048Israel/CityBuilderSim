# GD1 - gold's price ({next}, 2026-11-10)

The brief is `docs/studies/spec-gold-money.md` §4.1 (GD0, 2026-11-02; Jerus
took its four calls the recommended way the same day, "All four, mines last
(Recommended)"). `GoldPrice` gives US$ a fine troy ounce for any month of
either path, a pure function of the path, the seed and the month: nothing is
saved and a reload continues the path. Nothing in the game reads it yet; GD2
hands it to the vault as its ninth reserve.

## 1. What changed

- **`GoldPrice`** (new): the official path, the table, the rule.
  - January 1900 to March 1933: `PAR_1900` = 480 / 23.22 = US$20.6718, the
    Gold Standard Act's dollar of 23.22 grains fine.
  - April 1933 to January 1934: a ten-month geometric path to `PAR_1934`
    (★est., the study's: the dollar floated from April and the RFC bought at
    rising prices from October). 1933's twelve months average US$25.53, the
    USGS's recorded official price US$25.56.
  - February 1934 to July 1971: US$35, Proclamation 2072's.
  - On Historical, August 1971 to December 2025: the USGS's annual price
    placed at July of each year, months log-linear between Julys; July 1971 is
    the statute's $35 (1971's own figure, US$41.37, the free market's, is the
    table's record, not the path's); August to December 2025 hold 2025's
    US$3,300 (★est.: no July 2026).
  - After it (Historical from January 2026, Random from August 1971): an
    AR(1) in ln of gold's real price, in dollars of January 1900 on the
    path's own US prices, toward `targetRealPrice()`, the mean real price of
    2006-2025 by the data's own CPI (US$48.3 of 1900, US$1,868 of 2025).
    `SIGMA` 4.749% a month, `HALF_LIFE_MONTHS` 120 (★est.), no drift (★est.).
    Draws are `new Random(Nations.scramble(seed, 9_998, t, 300))`, beside
    WorldMoneys' tags.
- **`gold-usd-annual.csv`** (new, in the jar): 55 lines, each source's figure
  as printed: DS140's unit value a metric ton 1971-2022 (three significant
  figures; `GoldPrice` divides by 1e6 / 31.1034768 ounces), MCS2026's
  dollars an ounce 2023-2025.
- **`GoldPriceCheck`** (new, in `AllChecks` after InterestAbroadCheck), on
  the jar's twelve-nation fixture, so it needs no world file named.
- **The credits**: a `gold-usd-annual` section at the end of
  `tools/world/ATTRIBUTION.txt` (the Credits page shows it in full), and a
  table for it in `docs/studies/licences.md` §1.

## 2. The sources and their licences

| source | licence | ships |
|---|---|---|
| Gold Standard Act, 14 March 1900, 31 Stat. 45 | US federal law, public domain | `PAR_1900` |
| Gold Reserve Act, 30 January 1934, and Proclamation 2072, 31 January 1934 | US federal law, public domain | `PAR_1934` |
| USGS, Data Series 140, Gold statistics (ds140-gold-2022.xlsx, modified 2023-11-20) | public domain (its page's Sources/Usage) | 1971-2022 in the table |
| USGS, Mineral Commodity Summaries 2026 data release (doi 10.5066/P1WKQ63T) | CC0 1.0 (its rights statement) | 2023-2025 in the table |
| World Bank, monthly gold (the Pink Sheet), 1973-2024 | CC BY 4.0 on its face; the series is the LBMA's fix | **nothing but `SIGMA`**, a figure measured from it |

All allow commercial use. The figures were read on 2026-11-10: DS140 from its
workbook (whose embedded notes confirm the unit value is the world market
price to 1967 and Engelhard's average quotation from 1968), MCS2026 from the
data release's `MCS2026_Commodities_Data.csv` (its PDF refuses a fetch).
MCS2026's own 2022 figure, US$1,802, and DS140's, 57.9M a ton = US$1,800.9,
agree to 0.06%, so the two sources join.

## 3. The check

`GoldPriceCheck`, seven sections, 68 lines, all OK in 0.5 s. Sections 1-3,
6 and 7 run on a fixture US line (3% a year), 4 and 5 on the jar's
twelve-nation world. `./batch-check.sh --pick` exits 0 (GoldPriceCheck and
TimingCheck; maps regenerated, Stale 0 firm). `CreditsCheck`, named because
the attribution file changed, is OK.

- **The pars:** every month January 1900 to March 1933 is `PAR_1900` to
  the bit, February 1934 to July 1971 `PAR_1934`, on both paths. The ten
  1933 steps are each ×1.05407. 1933's mean is 25.5286.
- **The table:** each July 1972-2025 is its year's figure. March 1980 is
  US$486.99, between July 1979's 307.61 and July 1980's 612.74. July 1971 is
  35, against the table's 41.37.
- **The rule**, 8 seeds × 4,800 months on Random:
  - the slope of x(t) on x(t-1) is 0.99405 (`PHI` 0.99424);
  - the residual sd is 0.04754 (`SIGMA` 0.04749);
  - the pooled mean of the last 2,400 months is 0.048, 0.83 of its expected
    sd from 0.
- **Prices, seed 48,213, July of each year:**

  | path | 2025 | 2100 | 2200 | 2400 |
  |---|---|---|---|---|
  | Random | US$413 | 1,558 | 21,708 | 307,588 |
  | Historical | US$3,300 (the table) | 6,629 | | 2,176,225 |

  Both paths are US$59.99 of 1900 in 2400. The draws are a function of the
  seed and the month, not the path (as `WorldMoneys`' are), so by 2400 the
  two have forgotten their different starts.
- **A reload:** asking January 2400 first gives every month 0-6000 to the bit
  what asking in order gives. Another seed is the same through the official
  and table months and differs in every ruled one.
- **December 2025** on the fixture's US line is US$84.07 of 1900, x = +0.553
  over the target. So Historical's gold eases down in real terms from 2026,
  half the gap in ten years.

The `synchronized` that `--pick` flags for review is `Nations.year`'s
pattern: `usdPerOunce` and `realPrice` lock the walk, which is the
instance's only mutable state, and nothing calls `GoldPrice` from two
threads yet.

## 4. The trace, the save, the calls

- **No ensemble**: no existing class changed and nothing reads `GoldPrice`
  (only its check), so the default playtest's trace is main's by
  construction.
- **No save change**, no format bump: `GoldPrice` is never saved.
- **Calls**: none left. The brief's ★estimates (the 1933 path, the ten-year
  half-life, the zero drift, 2025's months held) are the study's, taken as
  written; the one this batch added is holding August-December 2025 at
  2025's figure.
