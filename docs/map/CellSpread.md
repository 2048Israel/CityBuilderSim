# CellSpread.java - 64 lines · 7 methods · 4 constants · model

`ham/citybuildersim/CellSpread.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> Closed forms for the amounts among a cell's holders (HC1).
> WHY: a cell average cannot say who has a thin buffer or a large balance.
> The mean is conditional on holding; callers apply their holding share once.

**Uses:** [BusinessDebtManager](BusinessDebtManager.md) (4)

**Used by (4):** [CardCheck](CardCheck.md), [DebtStatistics](DebtStatistics.md), [DebtStatisticsCheck](DebtStatisticsCheck.md), [Household](Household.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 12 | `CellSpread.CARD_SIGMA` | `Math.sqrt(2 * Math.log(6.1 / 2.7))` | SCF 2022 card mean/median: sqrt(2 log(6.1/2.7)). |
| 14 | `CellSpread.TOTAL_SIGMA` | `Math.sqrt(2 * Math.log(179.1 / 88.1))` | SCF 2025 total debt mean/median: sqrt(2 log(179.1/88.1)). |
| 16 | `CellSpread.SAVINGS_SIGMA` | `1.5` | Estimated dispersion of liquid savings pending the wealth study. |
| 18 | `CellSpread.HOME_SIGMA` | `Math.sqrt(2 * Math.log(239.7 / 183.6))` | SCF home-secured debt mean/median. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 8 | 57 | **type** `public final class CellSpread` | Closed forms for the amounts among a cell's holders (HC1). |
| 9 | 1 | `private CellSpread()` |  |
| 21 | 7 | `public static double below(double x, double mean, double sigma)` | Probability an amount is at or below x, including the point mass at zero. |
| 30 | 6 | `public static double above(double x, double mean, double sigma)` | Probability above x, evaluated directly so a small holding tail is not rounded away by 1 - CDF. |
| 38 | 6 | `public static double aboveMean(double x, double mean, double sigma)` | E[X; X > x], a part of the unconditional mean, never a conditional mean. |
| 46 | 4 | `public static double shortfall(double gap, double mean, double sigma)` | E[(gap - savings)+], the credit needed by a spread of liquid buffers. |
| 52 | 5 | `public static double belowMean(double x, double mean, double sigma)` | E[X; X <= x], evaluated directly to retain thin tails beside large buffers. |
| 59 | 5 | `public static double cappedMean(double payment, double mean, double sigma)` | E[min(X, payment)], used for repayments without inventing negative debt. |

