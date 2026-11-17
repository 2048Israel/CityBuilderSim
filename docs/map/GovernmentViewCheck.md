# GovernmentViewCheck.java - 403 lines · 9 methods · 0 constants · harnesses

`ham/citybuildersim/GovernmentViewCheck.java` - generated 2026-11-17 by CodeMap; line numbers are as of that run.

> The government's wealth bands and advice, measured without moving its books (GV1).
> 
> WHY. A ratio that omits the vault, counts the fund's cash twice, annualises
> an unfinished history or changes the market by being read misleads a player.
> 1. Boundaries and missing revenue. 2. Every asset and the debt counted once.
> 3. The last twelve months, including folded history. 4. Existing policy
> previews and buyback quotes, without changing cash, dials or debt.
> 5. A save restores the same reading before another month passes.

**Uses:** [GovernmentView](GovernmentView.md) (158), [TaxPolicy](TaxPolicy.md) (11), [Game](Game.md) (7), [PolicyPreviewCheck](PolicyPreviewCheck.md) (7), [LongTermBond](LongTermBond.md) (5), [Debt](Debt.md) (5), [Buyback](Buyback.md) (4), [HistorySave](HistorySave.md) (3), [PolicyPreview](PolicyPreview.md) (3), [PayTier](PayTier.md) (3), [GameFiles](GameFiles.md) (2), [InflationLinkedBond](InflationLinkedBond.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [Equity](Equity.md) (1), [DebtManager](DebtManager.md) (1), [TempDirs](TempDirs.md) (1), [Unemployment](Unemployment.md) (1), [EconomyManager](EconomyManager.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 387 | **type** `public final class GovernmentViewCheck` | The government's wealth bands and advice, measured without moving its books (GV1). |
| 19 | 4 | `static void check(String label, boolean ok)` |  |
| 23 | 4 | `static void near(String label, double a, double b)` |  |
| 27 | 7 | `static void quiet(Runnable body)` |  |
| 36 | 52 | `static void bandMargin()` | GV1-R: a notice leaves a band only BAND_MARGIN of the bound past it; the ends are bandFor()'s, open and closed. |
| 89 | 153 | `public static void main(String[] args) throws Exception` |  |
| 244 | 71 | `static void buybackCache(Game g)` | GV1-R: the buyback quote is kept while nothing it was struck on has changed, and quoted afresh when any of it has. |
| 317 | 57 | `static void policyAdvice() throws Exception` | A played policy fixture supplies positive taxable books, followed by explicit pension and EI populations. |
| 375 | 9 | `static void setTax(TaxPolicy policy, int family, double rate)` |  |
| 386 | 17 | `static void checkTransfer(Game city, String forced)` | Both alternatives are positive in the initial fixture; caps then force each choice independently. |

