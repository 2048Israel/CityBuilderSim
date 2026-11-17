# GovernmentView.java - 242 lines · 32 methods · 10 constants · model

`ham/citybuildersim/GovernmentView.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> What the government owns net of its paper, in years of its revenue, and
> the existing levers that can give an excess back (GV1, spec-wealth 3.2).
> 
> WHY. A surplus is a flow; it never says how large a stock has accumulated.
> The treasury, both fund books and the vault belong in that stock, valued
> in the city's money. This read changes no policy and reprices no market.

**Uses:** [Debt](Debt.md) (9), [Game](Game.md) (8), [TaxPolicy](TaxPolicy.md) (3), [PolicyPreview](PolicyPreview.md) (3), [HistorySave](HistorySave.md) (1), [EconomyManager](EconomyManager.md) (1), [Buyback](Buyback.md) (1)

**Used by (6):** [GovernmentPlayer](GovernmentPlayer.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [GovernmentScreen](GovernmentScreen.md), [GovernmentViewCheck](GovernmentViewCheck.md), [Inbox](Inbox.md), [InboxCheck](InboxCheck.md)

## Enum constants

| line | constant | says |
|---:|---|---|
| 36 | `GovernmentView.Band.BROKE` |  |
| 36 | `GovernmentView.Band.THIN` |  |
| 36 | `GovernmentView.Band.SOUND` |  |
| 36 | `GovernmentView.Band.RICH` |  |
| 36 | `GovernmentView.Band.HOARDING` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 19 | `GovernmentView.REVENUE_MONTHS` | `12` | A year of actual revenue is required; an unfinished year is not forecast as a full one. |
| 21 | `GovernmentView.THIN_MIN` | `.1` | Under a tenth of revenue is broke (spec-wealth's estimated treasury-buffer bands). |
| 23 | `GovernmentView.SOUND_MIN` | `.5` | Half a year's revenue is the sound band's bottom (spec-wealth 3.2, estimated). |
| 25 | `GovernmentView.RICH_MIN` | `2` | Two years is its top, beyond which the test player gives money back (spec-wealth 3.2). |
| 27 | `GovernmentView.HOARDING_MIN` | `5` | Over five years is hoarding (spec-wealth 3.2, estimated). |
| 29 | `GovernmentView.BAND_MARGIN` | `.1` | How far past a band's bound, as a share of that bound, the wealth must go before the band notice leaves the band: a tenth (GV1-R, estimated), so wealth that sits on a bound posts one notice, not one a month. |
| 31 | `GovernmentView.TAX_CUT_STEP` | `.01` | An illustrative tax cut on the advice card: one percentage point, not a policy change. |
| 33 | `GovernmentView.TRANSFER_RAISE_STEP` | `.05` | An illustrative transfer rise: the five-point step the brief gives its test player. |
| 204 | `GovernmentView.QUOTED` | `Collections.synchronizedMap(new WeakHashMap<>())` | The last buyback quote of each city and what it was struck on, so a redraw of the Government overview with nothing changed quotes nothing (GV1-R). |
| 205 | `GovernmentView.QUOTING_PASSES` | `new AtomicInteger()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 37 | `private final String label` |  |
| 42 | `private final Game game` |  |
| 43 | `private final double treasury, fundCash, fundHoldings, vault, paper, debt, annualRevenue, netWealth, wealth...` |  |
| 44 | `private Advice advice` |  |
| 125 | `Debt paper` |  |
| 126 | `double coupon` |  |
| 127 | `String taxName` |  |
| 128 | `double tax, cut, transfer, raise` |  |
| 180 | `final int month` |  |
| 181 | `final double cash, owed` |  |
| 182 | `final Debt[] pieces` |  |
| 183 | `final double[] principals, market` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 226 | **type** `public final class GovernmentView` | What the government owns net of its paper, in years of its revenue, and the existing levers that can give an excess back (GV1, spec-wealth 3.2). |
| 35 | 6 | **type** `public enum Band` |  |
| 38 | 1 | `Band(String label)` _(in GovernmentView.Band)_ |  |
| 39 | 1 | `public String label()` _(in GovernmentView.Band)_ |  |
| 46 | 15 | `private GovernmentView(Game game)` |  |
| 62 | 1 | `public static GovernmentView of(Game game)` |  |
| 63 | 1 | `public double treasury()` |  |
| 64 | 1 | `public double fundCash()` |  |
| 65 | 1 | `public double fundHoldings()` |  |
| 66 | 1 | `public double vault()` |  |
| 72 | 1 | `public double debt()` | What the government owes: its paper at face, the central bank's advances and the linked bonds' indexation past their face (GV1-R). |
| 74 | 1 | `public double paper()` | The government's paper alone, at face: what a buyback can buy (debt() adds the advances and the indexation, which no buyback retires; FB4's review). |
| 75 | 1 | `public double annualRevenue()` |  |
| 76 | 1 | `public double netWealth()` |  |
| 77 | 1 | `public double wealthYears()` |  |
| 78 | 1 | `public Band band()` |  |
| 81 | 8 | `public static Band bandFor(double years)` | Exact endpoints: 0.1 thin, 0.5 sound, 2 sound, 5 rich; unavailable is no band. |
| 96 | 11 | `public static Band bandFrom(Band held, double years)` | The band a notice holding {@code held} reports for {@code years}: the held band while the wealth lies within its range widened by BAND_MARGIN of each finite bound (a lower bound L to L x (1 - margin), an upper bound U... |
| 108 | 1 | `public boolean buybackAvailable()` |  |
| 109 | 1 | `public Debt buybackDebt()` |  |
| 110 | 1 | `public double buybackMonthlySaving()` |  |
| 111 | 1 | `public String largestTaxName()` |  |
| 112 | 1 | `public double largestTaxMonthly()` |  |
| 113 | 1 | `public double taxCutMonthly()` |  |
| 114 | 1 | `public String transferName()` |  |
| 115 | 1 | `public double transferMonthly()` |  |
| 116 | 1 | `public double transferRaiseMonthly()` |  |
| 119 | 4 | `private Advice advice()` | Advice is only needed on screen, never paid for by the monthly notice or the test player. |
| 124 | 50 | **type** `private static final class Advice` |  |
| 130 | 43 | `Advice(Game g, double cash, double owed)` _(in GovernmentView.Advice)_ |  |
| 176 | 1 | **type** `private record Quote(Debt paper, double coupon)` | The piece worth buying back and the coupon that stops, as one quoting pass found them. |
| 179 | 23 | **type** `private static final class Stamp` | What a quote was struck on: the month, the cash, the debt, each piece and its principal, and the market's saved inputs. |
| 185 | 9 | `Stamp(Game g, double cash, double owed)` _(in GovernmentView.Stamp)_ |  |
| 195 | 6 | `boolean same(Stamp other)` _(in GovernmentView.Stamp)_ |  |
| 208 | 1 | `static int buybackQuotes()` | How many times a buyback quote has been struck over every piece of debt, for the check that a view with nothing changed reuses the last. |
| 210 | 8 | `private static Quote buyback(Game g, double cash, double owed)` |  |
| 219 | 23 | `private static Quote quoteEvery(Game g, double cash)` |  |

