# SX1 — The exchange's prices readable

## Built and calls

- ★ The wage band is `Exchange.CONSOLIDATE_BELOW=.0025` through
  `Exchange.SPLIT_AT=.25` of PopulationManager's average monthly wage:
  roughly a fraction of an hour through five working days of pay. Adjustments
  use powers of ten against the band's geometric midpoint. Exact boundaries
  stand; no paid wage means wait. The gap prevents monthly oscillation.
- The exchange keeps its existing adjustment path: register, households,
  resting orders, fair prices, emigrants, shorts, lending, volume and the fund.
  Founding-share chart prices retain the cumulative factor.
- ★ `Formats.LARGE_PERCENT=10` (1,000%) controls the shared formatter.
  Price changes with an actual span of at least twelve months use compound
  annual growth, computed with log1p/expm1. Large ratios and cost-basis returns
  use compact multiples. A fund's purchases have different dates: inventing
  a CAGR from the lot's first purchase would misrepresent its return.
  A gain multiple includes the original cost; a ratio multiple does not.
- Shared P&L, fund chart changes, City History changes, model percentages and
  common screen percentage/rate helpers delegate to Formats. Ordinary figures
  keep their existing precision, grouping and signs.
- No save schema changes: the existing split factor, holdings and ledger
  already save every state changed here. No migration or format bump needed.
- ★ The only HW1 overlap is two one-line helper bodies in ui/BuildScreen:
  pct and pct1 delegate to Formats. Infrastructure and Services delegate to
  that helper too, so leaving it would leave three screens outside the common
  rule. No builder planning, advice, cards or fixtures changed; the orchestrator
  was notified over cbs-bus before the hunk. Game has only the three-line wage
  getter in its exchange adapter; no month-order changes.
- The same formatter also covers the debt closing gain, fiscal ratios, sector
  margins, people ratios, policy/FX changes and GDP growth. The screen helpers
  keep their old precision; none owns a separate size threshold.

## Checks so far

- `batch-check.sh --fast`: compile, generated maps, Stale green.
- Named ExchangeCheck, FundLedgerCheck, FormatsCheck, MoneyWordsCheck and
  HistoryCheck runs: green (the Exchange filter also selects DebtExchangeCheck).
- ExchangeCheck and FundLedgerCheck section 4 retain exact factor, count,
  inverse-price, cost and value assertions, now derived from the wage dials.
- ExchangeCheck's century fixture crosses both triggers, keeps every quote in
  band and preserves household value and every founding-share chart point.
  Exact boundaries and missing/invalid wages are checked.
- The played 240-month fixture split in all 240 months, had zero live quotes
  outside the wage band, and its worst split-month relative MoneyAudit residual
  was 6.00e-14. Its save/reload retains factors, founding-share prices, register,
  fund and household holdings.
- FormatsCheck checks unchanged percentages through the dial, the closed-form
  yearly rate, short spans, ratios versus gains, named trillions, extreme
  values, missing values and losses beyond cost.

- Visual QA: rendered the actual FundScreen.windowMove and Pieces.pnl outputs
  into a 612-pixel JavaFX card and inspected target/sx1-preview/readable-percent.png.
  The long chart reads about 5.0% a year; both large position returns fit as
  multiples; the ordinary +12.3% gain keeps its former form. The target-only
  preview also asserts the chart uses a yearly rate and the positions use no %.
