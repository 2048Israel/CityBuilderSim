# ExchangeCheck.java - 712 lines · 15 methods · 2 constants · harnesses

`ham/citybuildersim/ExchangeCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

> Verifies the exchange: what the desk quotes, who trades with it and why,
> what a company does with its surplus, and that a city with a market in it
> still adds up and survives a save.
> 
> Every claim is CAUSED. A register is handed shares and a book and the
> desk is asked what it quotes; a family leaves and its shares are bought;
> the world is shown a yield and sells or buys; households are given money
> and a choice of two companies; a household is left short and sells before
> it borrows; a company is handed a surplus and a market that is cheap, then
> one that is dear. The live city at the end shows the mechanism runs inside
> the audited month and comes back from a save.

**Uses:** [Exchange](Exchange.md) (73), [Equity](Equity.md) (41), [HouseholdBalance](HouseholdBalance.md) (35), [Bank](Bank.md) (15), [PayTier](PayTier.md) (12), [FamilyStructure](FamilyStructure.md) (8), [OutwardInvestment](OutwardInvestment.md) (4), [Game](Game.md) (4), [Sectors](Sectors.md) (3), [GameFiles](GameFiles.md) (2), [DebtManager](DebtManager.md) (1), [Household](Household.md) (1)

**Used by (1):** [BankCheck](BankCheck.md)

## Sections

| line | section |
|---:|---|
| 104 | · 1. the quote |
| 137 | · 2. the emigrants |
| 190 | · 3. the world |
| 242 | · 4. the households |
| 293 | · 4b. and when the model prices two of them the same |
| 386 | · 5. the distress sale |
| 426 | · 5b. the world's paper |
| 471 | · 6. the buyback, and the special dividend |
| 587 | · 7. the split |
| 622 | · 8. the save |
| 644 | · 9. a live city |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `ExchangeCheck.W` | `DebtManager.WORLD_BASE_RATE` |  |
| 43 | `ExchangeCheck.N` | `Equity.COMPANIES.length` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 23 | `static int fails` |  |
| 24 | `static PrintStream out` |  |
| 25 | `static PrintStream quiet` |  |
| 80 | `final double[] cash` |  |
| 82 | `final double[] debt` | Interest and principal a month (0.7.11, round 2): nothing unless a fixture says so. |
| 83 | `final double[] boughtBack` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 21 | 692 | **type** `public class ExchangeCheck` | Verifies the exchange: what the desk quotes, who trades with it and why, what a company does with its surplus, and that a city with a market in it still adds up and survives a save. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 9 | `static void close(String label, double actual, double expected, double tol)` |  |
| 46 | 5 | `static Equity withRecord(int company, double...monthlyIncome)` | A register with twelve months of this income on one company's record. |
| 52 | 1 | `static double[] months(double v)` |  |
| 55 | 12 | `static HouseholdBalance savers(double savedEach, double takeHome)` | Households: a hundred unskilled couples with this much saved each, struck once so they have a take-home. |
| 68 | 1 | `static Household couple(HouseholdBalance hb)` |  |
| 71 | 6 | `static Bank bankWith(double capital)` | A bank with this much capital, its month open. |
| 79 | 15 | **type** `static class Firms implements Exchange.Companies` | The companies, as the exchange sees them: a till, a balance sheet, a payroll. |
| 84 | 1 | `public double cashAvailable(int c, double wanted)` _(in ExchangeCheck.Firms)_ |  |
| 85 | 1 | `public void payBuyback(int c, double x)` _(in ExchangeCheck.Firms)_ |  |
| 86 | 1 | `public void paySpecialDividend(int c, double x)` _(in ExchangeCheck.Firms)_ |  |
| 87 | 1 | `public double assets(int c)` _(in ExchangeCheck.Firms)_ |  |
| 88 | 1 | `public double equity(int c)` _(in ExchangeCheck.Firms)_ |  |
| 89 | 1 | `public double monthlyOperatingCost(int c)` _(in ExchangeCheck.Firms)_ |  |
| 90 | 1 | `public double monthlyDebtService(int c)` _(in ExchangeCheck.Firms)_ |  |
| 95 | 617 | `public static void main(String[] args) throws Exception` |  |

