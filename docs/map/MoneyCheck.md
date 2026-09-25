# MoneyCheck.java - 277 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/MoneyCheck.java` - generated 2026-09-24 by CodeMap; line numbers are as of that run.

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

**Uses:** [Game](Game.md) (9), [Sectors](Sectors.md) (7), [MoneyAudit](MoneyAudit.md) (5), [GameFiles](GameFiles.md) (3), [Founding](Founding.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [LandParcel](LandParcel.md) (2), [LandMarket](LandMarket.md) (1), [TaxPolicy](TaxPolicy.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [DebtManager](DebtManager.md) (1), [CapitalFlows](CapitalFlows.md) (1)

## Sections

| line | section |
|---:|---|
| 72 | · 1. a city that prospers |
| 113 | · AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16). |
| 140 | · AND LAND PAID FOR OUT OF THE VAULT (0.7.6). |
| 169 | · 2. a city under stress |
| 225 | · AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 22 | `static int fails` |  |
| 43 | `static double worstDrift` | The worst post-audit drift any month of the last play() saw. |
| 44 | `static String worstDriftPool` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 20 | 258 | **type** `public class MoneyCheck` | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. |
| 24 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 29 | 5 | `static void quietly(Runnable work)` |  |
| 35 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 47 | 20 | `static MoneyAudit.Result play(String label, Game g, int months, boolean verbose)` | Runs `months` and returns the worst relative residual seen, printing the worst month. |
| 68 | 209 | `public static void main(String[] args)` |  |

