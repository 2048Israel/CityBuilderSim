# GovernmentView.java - 154 lines · 25 methods · 7 constants · model

`ham/citybuildersim/GovernmentView.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> What the government owns net of its paper, in years of its revenue, and
> the existing levers that can give an excess back (GV1, spec-wealth 3.2).
> 
> WHY. A surplus is a flow; it never says how large a stock has accumulated.
> The treasury, both fund books and the vault belong in that stock, valued
> in the city's money. This read changes no policy and reprices no market.

**Uses:** [Debt](Debt.md) (5), [Game](Game.md) (4), [TaxPolicy](TaxPolicy.md) (3), [PolicyPreview](PolicyPreview.md) (3), [HistorySave](HistorySave.md) (1), [EconomyManager](EconomyManager.md) (1), [Buyback](Buyback.md) (1)

**Used by (6):** [GovernmentPlayer](GovernmentPlayer.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentViewCheck](GovernmentViewCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 28 | `GovernmentView.Band.BROKE` |  |
| 28 | `GovernmentView.Band.THIN` |  |
| 28 | `GovernmentView.Band.SOUND` |  |
| 28 | `GovernmentView.Band.RICH` |  |
| 28 | `GovernmentView.Band.HOARDING` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 13 | `GovernmentView.REVENUE_MONTHS` | `12` | A year of actual revenue is required; an unfinished year is not forecast as a full one. |
| 15 | `GovernmentView.THIN_MIN` | `.1` | Under a tenth of revenue is broke (spec-wealth's estimated treasury-buffer bands). |
| 17 | `GovernmentView.SOUND_MIN` | `.5` | Half a year's revenue is the sound band's bottom (spec-wealth 3.2, estimated). |
| 19 | `GovernmentView.RICH_MIN` | `2` | Two years is its top, beyond which the test player gives money back (spec-wealth 3.2). |
| 21 | `GovernmentView.HOARDING_MIN` | `5` | Over five years is hoarding (spec-wealth 3.2, estimated). |
| 23 | `GovernmentView.TAX_CUT_STEP` | `.01` | An illustrative tax cut on the advice card: one percentage point, not a policy change. |
| 25 | `GovernmentView.TRANSFER_RAISE_STEP` | `.05` | An illustrative transfer rise: the five-point step the brief gives its test player. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `private final String label` |  |
| 34 | `private final Game game` |  |
| 35 | `private final double treasury, fundCash, fundHoldings, vault, debt, annualRevenue, netWealth, wealthYears` |  |
| 36 | `private Advice advice` |  |
| 91 | `Debt paper` |  |
| 92 | `double coupon` |  |
| 93 | `String taxName` |  |
| 94 | `double tax, cut, transfer, raise` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 11 | 144 | **type** `public final class GovernmentView` | What the government owns net of its paper, in years of its revenue, and the existing levers that can give an excess back (GV1, spec-wealth 3.2). |
| 27 | 6 | **type** `public enum Band` |  |
| 30 | 1 | `Band(String label)` _(in GovernmentView.Band)_ |  |
| 31 | 1 | `public String label()` _(in GovernmentView.Band)_ |  |
| 38 | 14 | `private GovernmentView(Game game)` |  |
| 53 | 1 | `public static GovernmentView of(Game game)` |  |
| 54 | 1 | `public double treasury()` |  |
| 55 | 1 | `public double fundCash()` |  |
| 56 | 1 | `public double fundHoldings()` |  |
| 57 | 1 | `public double vault()` |  |
| 58 | 1 | `public double debt()` |  |
| 59 | 1 | `public double annualRevenue()` |  |
| 60 | 1 | `public double netWealth()` |  |
| 61 | 1 | `public double wealthYears()` |  |
| 62 | 1 | `public Band band()` |  |
| 65 | 8 | `public static Band bandFor(double years)` | Exact endpoints: 0.1 thin, 0.5 sound, 2 sound, 5 rich; unavailable is no band. |
| 74 | 1 | `public boolean buybackAvailable()` |  |
| 75 | 1 | `public Debt buybackDebt()` |  |
| 76 | 1 | `public double buybackMonthlySaving()` |  |
| 77 | 1 | `public String largestTaxName()` |  |
| 78 | 1 | `public double largestTaxMonthly()` |  |
| 79 | 1 | `public double taxCutMonthly()` |  |
| 80 | 1 | `public String transferName()` |  |
| 81 | 1 | `public double transferMonthly()` |  |
| 82 | 1 | `public double transferRaiseMonthly()` |  |
| 85 | 4 | `private Advice advice()` | Advice is only needed on screen, never paid for by the monthly notice or the test player. |
| 90 | 64 | **type** `private static final class Advice` |  |
| 96 | 57 | `Advice(Game g, double cash, double owed)` _(in GovernmentView.Advice)_ |  |

