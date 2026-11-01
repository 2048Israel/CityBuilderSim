# EP2 - the figures a screen hides or folds under a threshold, at 1900's price level

Queued 2026-11-01 from the stranger's review at EP1's ship gate (`docs/queue/EP1-notes.md`,
"The gate's review"). EP1 moved every nominal anchor to 1900's price level
(`PriceLevel.FACTOR`, 1/38.6) and scaled the dials it found by the named-literal
sweep; these are the ones it did not, because they are not anchors but cut-offs
on what the screens draw, and a cut-off written in today's thousands hides a 1900
city's figures.

## What EP1 left

- `TradeLanes.MIN_LANE` = 300 (`TradeLanes.java:37`, "D$0.3M a month in the city's
  money"): a partner needs that much real trade for a lane or an arrow, so at the
  factor a 1900 city's partner needs 38.6 times the trade it would have at today's
  prices, and a young city draws none for decades.
- `FundView.FOLD_UNDER` = 1 (`FundView.java:42`, "a thousand dollars"): a holding
  under it folds into "and N more" on the Portfolio page; at the factor that is
  $38,600 of today.
- The `>= .5`-thousand cut-offs in `GovernmentScreen` (lines 799, 1165-1177,
  1922-1930, 2170, 2273) and `FinancesScreen` (1223, 1284-1289, 1387-1390, 1572,
  1689, 2071-2076), and `ui/Money.java:248` (`signedTight`) printing $0 under .5:
  a line that says nothing, or "$0", while the city is moving real money.
- `MoneyAudit.Result.detail` prints each line in thousands to two places
  (`MoneyAudit.java:505`, `%,14.2f`): a step of $10 of 1900, so a young city's
  small lines read 0.06 for 0.0575, and HoldersCheck reads them back by text
  (its `CENT`, .0051, is that rounding). More places, or the check reading the
  model's figures.
- `NewGameCheck` (c) (`NewGameCheck.java:842`): on Random the water-plant bond
  overshot the gap by $1,109k against a $112k bound (the calendar moved Random's
  draw), so EP1 pinned that section to Historical; the bound itself rests on one
  gap, not on the auction's coupon step.

## What it is

Each cut-off at the factor (`x * PriceLevel.FACTOR`, the literal's sentence saying
so, as `FundView.MIN_RECORDED_PRICE` already does), or the money format's places
moved so a figure under a thousand shows its dollars (`ui/Money.java`), whichever
reads better on the screen: an eye-check on a new city decides, and EP1-C's list
carries it. `NewGameCheck` (c): the single-gap bound dropped, or rested on the
smallest-face promise the auction makes, so it holds on both paths.

## Checks

A harness that founds a city at the factor and at today's prices and asserts the
lanes drawn, the holdings folded and the lines printed equal by ratio (the pattern
of `PriceLevelCheck`'s twin); `NewGameCheck` (c) green on Random and Historical.
Everything else in the suite unmoved: these change what is drawn, not the model.
