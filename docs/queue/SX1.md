# SX1 - the exchange's prices readable: splits on the wage, percentages a person can read

Queued 2026-11-02 from Jerus's play: Finances › The city's fund › Cement &
Concrete at month 7,871 (November 2555). He remembered shares "4million per
share lol" and asked for "share splits and merges".

## What he saw

1. **A share costs months of income.** Cement's quote is F$730,018 a share,
   and its chart peaks at about F$1.6M. Splits already exist
   (`Exchange.splitWhatNeedsIt`): a share splits by a power of ten at
   `SPLIT_AT` (100) times `Equity.foundingPrice()`, and consolidates at a
   hundredth of it. The founding price follows only the price level, so
   over centuries of real growth a share grows with the economy and never
   reaches the trigger. One Cement share is about seven months of output
   per person (GDP F$3.6T a year over 2,913,384 people); a real share costs
   a few days' pay at most.

   Tie the trigger to the average wage instead: split when a share costs
   more than a dial's share of the average monthly wage, consolidate below
   a dial's fraction of it. A share then costs about the same hours of work
   in 1900 and in 2555. Every count and price moves by the factor as now
   (the register, `HouseholdBalance.splitShares`, the books, the fund's
   `noteSplit`), and value is untouched. The charts stay per founding share
   (`HistorySave`, the share price series), so a split never looks like a
   crash.
2. **Percentages nobody can read.** "+4,548,682,160,882,199.0% from Dec
   1905 to Nov 2555" is about 5% a year (a multiple of about 4.5e13 over
   650 years). The position panel's unrealized "+969,121,794.8%" and total
   return "+11,631,451,027.8%" are alike. Past a dial (say +1,000%), write
   the yearly rate over the span ("about 5.0% a year since Dec 1905") or the
   multiple ("x45 trillion"). Use one formatter for every such figure, not a
   fix per screen.

## Checks

- `FundLedgerCheck` (section 4) and `ExchangeCheck` assert the split
  against the founding price. The trigger is what this batch changes, on
  purpose, so re-derive those assertions against the new constant (a share
  splits when it passes the wage dial); never loosen them.
- On a long fixture, the dearest share over the run stays inside the band
  in wage terms.
- The formatter: a figure under the dial is unchanged; over it, the yearly
  rate matches a closed form.
- Expect the trace to move where splits now fall in other months; prices
  and values at fair should match to rounding.
