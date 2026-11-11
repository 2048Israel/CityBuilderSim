# GovernmentViewCheck.java - 252 lines · 7 methods · 0 constants · harnesses

`ham/citybuildersim/GovernmentViewCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> The government's wealth bands and advice, measured without moving its books (GV1).
> 
> WHY. A ratio that omits the vault, counts the fund's cash twice, annualises
> an unfinished history or changes the market by being read misleads a player.
> 1. Boundaries and missing revenue. 2. Every asset and the debt counted once.
> 3. The last twelve months, including folded history. 4. Existing policy
> previews and buyback quotes, without changing cash, dials or debt.
> 5. A save restores the same reading before another month passes.

**Uses:** [GovernmentView](GovernmentView.md) (59), [TaxPolicy](TaxPolicy.md) (11), [PolicyPreviewCheck](PolicyPreviewCheck.md) (7), [Game](Game.md) (6), [Buyback](Buyback.md) (4), [HistorySave](HistorySave.md) (3), [LongTermBond](LongTermBond.md) (3), [PolicyPreview](PolicyPreview.md) (3), [PayTier](PayTier.md) (3), [GameFiles](GameFiles.md) (2), [ShortTermTBill](ShortTermTBill.md) (2), [Equity](Equity.md) (1), [Debt](Debt.md) (1), [TempDirs](TempDirs.md) (1), [Unemployment](Unemployment.md) (1), [EconomyManager](EconomyManager.md) (1)

## Fields (state)

| line | field | says |
|---:|---|---|
| 18 | `static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 17 | 236 | **type** `public final class GovernmentViewCheck` | The government's wealth bands and advice, measured without moving its books (GV1). |
| 19 | 4 | `static void check(String label, boolean ok)` |  |
| 23 | 4 | `static void near(String label, double a, double b)` |  |
| 27 | 7 | `static void quiet(Runnable body)` |  |
| 35 | 129 | `public static void main(String[] args) throws Exception` |  |
| 166 | 57 | `static void policyAdvice() throws Exception` | A played policy fixture supplies positive taxable books, followed by explicit pension and EI populations. |
| 224 | 9 | `static void setTax(TaxPolicy policy, int family, double rate)` |  |
| 235 | 17 | `static void checkTransfer(Game city, String forced)` | Both alternatives are positive in the initial fixture; caps then force each choice independently. |

