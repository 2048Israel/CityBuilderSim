# MoneyCheck.java - 570 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/MoneyCheck.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> Money is conserved: every dollar that leaves a pool arrives in another, or
> crosses the city's boundary in a way the audit can name.
> 
> The 29th harness, and the one the other twenty-eight were missing. Every
> money bug this codebase has had was a flow with one side - a charge with no
> payee, a tax with no payer - and each was found by hand, months after it
> started. MoneyAudit strikes the identity every month; this plays a city with
> all six sectors trading, borrowing, building and being taxed, and demands
> the residual stay at rounding.
> 
> Two cities, because a fixture that only ever grows can hide a leak that
> only opens under stress: one is left to prosper, the other is bankrupted
> and restructured, taxed hard, and made to import everything.

**Uses:** [MoneyAudit](MoneyAudit.md) (21), [Game](Game.md) (18), [OldMetal](OldMetal.md) (12), [Sectors](Sectors.md) (10), [GameFiles](GameFiles.md) (7), [Founding](Founding.md) (7), [Debt](Debt.md) (6), [Sector](Sector.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (3), [LandParcel](LandParcel.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [DebtManager](DebtManager.md) (2), [LandMarket](LandMarket.md) (1), [Mining](Mining.md) (1), [Good](Good.md) (1), [TaxPolicy](TaxPolicy.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [CapitalFlows](CapitalFlows.md) (1), [CopperRefining](CopperRefining.md) (1), [NationalAccounts](NationalAccounts.md) (1), [MediumTermBond](MediumTermBond.md) (1)

## Sections

| line | section |
|---:|---|
| 75 | · 1. a city that prospers |
| 116 | · AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16). |
| 165 | · AND LAND PAID FOR OUT OF THE VAULT (0.7.6). |
| 194 | · 2. a city under stress |
| 250 | · AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK. |
| 299 | · 4. AND A WORKS THAT MAKES ITS OWN POWER AND BUYS ITS CONSUMABLES |
| 350 | · OLD STEEL (0.7.123, batch I4; OldMetal): a Scrap Yard buys the |
| 403 | · OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9): |
| 452 | · THE CITY'S PAPER ON ITS BOOKS (CD3, spec-city-debt.md section 5; |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |
| 46 | `static double worstDrift` | The worst post-audit drift any month of the last play() saw. |
| 47 | `static String worstDriftPool` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 548 | **type** `public class MoneyCheck` | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 5 | `static void quietly(Runnable work)` |  |
| 38 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 50 | 20 | `static MoneyAudit.Result play(String label, Game g, int months, boolean verbose)` | Runs `months` and returns the worst relative residual seen, printing the worst month. |
| 71 | 499 | `public static void main(String[] args)` |  |

