# EP2 - the figures a screen hides or folds under a threshold, at 1900's price level (0.9.80, 2026-11-10)

The brief: `docs/queue/EP2.md` (EP1's gate review). Branch `batch/EP2` from main 3bdffb1b (0.9.77). Two slices on
Sonnet 5.5 at xhigh, reviewed and committed here: EP2-A (the screens) and EP2-B (the checks). The model is untouched:
no line of the month moved.

## 1. The screens' cut-offs (EP2-A)

The screens wrote a money line, or a word in its place ("nothing owed", "none", "no change on last month", "no
cash"), only from a literal half-thousand up (`.5` in the model's thousands, $500), written at today's prices; at
1900's (`PriceLevel.FACTOR`, 1/38.6) that is $19,300 of today, so a young city's real lines said nothing. One dial now,
`Money.NOTHING = .5 * PriceLevel.FACTOR` ($12.95 of 1900), and `Money.nothing(x)` / `Money.something(x)` either side of
it (`something()` is false for NaN, as the literal `>= .5` was). 66 lines: GovernmentScreen 28, FinancesScreen 28,
TradeScreen 3, UserInterface 3, BankScreen 1, FundScreen 1, each test's operator and side kept.

Left as they were: every `.5` that is a head count (half a person rounds to a person: most of PeopleScreen's 69), a
pixel, a share, a goods quantity or money already in dollars; and the smaller money guards the sweep found
(PeopleScreen's borrowed, repaid, tax and fees and TradeScreen's balance and vault revaluation at `.005`, BankScreen's
`.0005`, SectorScreen's `.0000005`), each under `NOTHING` already, so hiding less than the new cut-off at either level.

`Money.signed()` and `signedTight()` printed "$0" for anything under `.5`: "$0" on a ledger line moving $499. They
print "$0" now only for a figure that rounds to no dollars, the format's own places.

`FundView.FOLD_UNDER`, a holding folded into "and N more" on the Portfolio page: 1 thousand, now `1 *
PriceLevel.FACTOR` ($25.88 of 1900).

The call the brief left ("each cut-off at the factor, or the money format's places moved, whichever reads better"):
the factor for the cut-offs that hide, the places for the "$0". At the factor a line shows at 1900 exactly when its
today's twin does (PriceLevelCheck 10, below), and a cut-off fixed at the founding's level errs only toward showing as
prices rise (by 2025's level it is $12.95 against figures 38.6 times larger). Not on the price index and the unit, as
TP1's lane floor is: a reform divides figures that inflation first multiplied, the two meeting near the founding's
scale again, and the floor needed them because the floor left alone after a reform hid every lane; a screen's cut-off
only errs toward showing. The format's places everywhere would print a today-founded city's $2 lines and part the twin.

The trade lanes: nothing to change. TP1 had put the floor in real terms at the factor (`TradeLanes.minLane()`:
`MIN_LANE * FACTOR * price index / unit`, DenominationCheck asserting it), and the map builds its lanes and arrows on the
feed's `minLane()`. BoatSchedule names `MIN_LANE` only in prose.

## 2. The audit's figures, read as the model struck them (EP2-B)

`MoneyAudit.Result.detail` prints each line in thousands to two places ($10 at the factor), and HoldersCheck,
BondCheck and ConstructionTrapCheck parsed numbers back out of the text, each with a tolerance for that rounding
(.0051, .005 + 1e-9, 0.01). `Result` now keeps `figures`, label to amount as struck, beside the text (the same
condition; a label struck twice keeps its first amount, as the readers took the first line), with `figure(label)`.
The three readers call it, and the tolerances that only covered the printed places are the model's cent,
`MoneyAudit.CENT` (.01 times the factor); none went red. The printed detail, the log, is unchanged.

## 3. NewGameCheck (c) on both paths

BA1 (2026-11-04) had already rested (c) on the auction's smallest-face promise (the face one granule down does not
cover the gap; the overshoot printed, not asserted); what was left was EP1's pin to Historical. Section 11's village
and (c) are now one method run on Random (the default) and on Historical, each label ending with its path. Both green;
on Random the bond overshoots its gap of about $3.0M by $1.22k.

## 4. PriceLevelCheck 5 and 10

5 reads `FundView.FOLD_UNDER` and `ui.Money.NOTHING` among its named literals: today's figure times the factor in the
two children. 10 is new and starts no JVM of its own (the harness takes about 40 s): the children of 6, 7 and 9 count
each month the partners given a lane (`TradeLanes.of` over the feed at `minLane()`, as MapView builds them, and a
generated world's arrows), the holdings folded and shown (`FundView.positions` under `FOLD_UNDER`), and, for each of
the 234 live nominal series the digest samples, whether `Money.something()` shows it. Every count and flag is the same
at both levels every month (0 of 14,274 flags differ in the default founding, 0 of 28,314 in the built city, 0 of
7,956 in the town). The lines shown that the old half-thousand would have hidden: 2,989 series-months at the factor in
the default founding, 6,454 in the built city, 1,607 in the town, none at today's prices. Section 8 is left out (main records it red, its twin parting at months 91-103); 9 is red on main by one
series nine billionths apart, and its counts agree.

Two things it found:

- **No lanes.** The default founding draws none in 61 months, the built city (400 houses) none in 121, the town one
  partner in one month of 34, at either level. The floor is in real terms, so this is the cities' trade with any one
  nation, not the price level: the board's D$0.3M a month at today's prices is more than these cities sell to or buy
  from any partner. Call 1.
- **No holdings.** The fund holds nothing in any of the three, so the fold is counted over no holding; `FOLD_UNDER`'s
  own figure is section 5's.

## 5. The checks

`./batch-check.sh --pick` with the Earth and world named: exit 3, 22 of 191 harnesses (MoneyAudit's 60 readers left
to the gate, LongPlaytest in their place), red only as main's record is (PriceLevelCheck, MapCheck, KeptCheck); Stale
0 firm. The five screens changed are read by no harness. In a worktree without the data named, KeptCheck reds 12
lines; main's tip built and run the same way reds the same 12, so they are the missing data's.

The ensemble: skipped. No file the month runs changed (Money and the screens are the interface, `FOLD_UNDER` is read by
FundScreen alone, MoneyAudit records a map beside the text it already printed), so the default playtest's trace is
main's byte for byte.

What to look at on a new city, if anything looks off: the Government and Finances pages of a young 1900 city, lines of
tens of dollars (interest not in cash, the uplift, a coupon, a holder's share of a bond) now drawn where they said
nothing; a signed ledger line "+$57" where it said "$0"; the Portfolio's "and N more under $26".

## 6. The calls

1. **★ A young city's trade map is empty** (his): no lane in the 216 months of the three cities the twin founds, at
   either price level, because the floor (TP1, the board's D$0.3M a month at today's prices in real terms) is more than
   these cities trade with any one nation. Recommended, and taken: **leave it**. It is the board's figure, and a
   young city's trade with one nation is thin. If he wants a lane from the founding, the floor as a share of the city's
   own trade would be a batch of its own.
2. **The cut-offs at the factor, the "$0" at the format's places** (the brief's "whichever reads better"): taken here
   for the reasons in 1. An eye on a new city can overturn it.
