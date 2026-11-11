# MoneyCheck.java - 1,056 lines · 13 methods · 1 constants · harnesses

`ham/citybuildersim/MoneyCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

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

**Uses:** [MoneyAudit](MoneyAudit.md) (83), [Game](Game.md) (33), [GameFiles](GameFiles.md) (14), [PriceLevel](PriceLevel.md) (14), [Sector](Sector.md) (12), [Sectors](Sectors.md) (12), [OldMetal](OldMetal.md) (12), [Founding](Founding.md) (9), [BusinessServices](BusinessServices.md) (7), [Debt](Debt.md) (6), [TaxPolicy](TaxPolicy.md) (5), [Household](Household.md) (4), [CorporateBond](CorporateBond.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Statement](Statement.md) (3), [PolicyDates](PolicyDates.md) (3), [LandParcel](LandParcel.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [DebtManager](DebtManager.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [BuildingManager](BuildingManager.md) (1), [LandMarket](LandMarket.md) (1), [Mining](Mining.md) (1), [Good](Good.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [CapitalFlows](CapitalFlows.md) (1), [CopperRefining](CopperRefining.md) (1), [NationalAccounts](NationalAccounts.md) (1), [MediumTermBond](MediumTermBond.md) (1), [BondMarket](BondMarket.md) (1)... and 1 more

**Used by (1):** [GdpCheck](GdpCheck.md)

## Sections

| line | section |
|---:|---|
| 214 | · 1. a city that prospers |
| 255 | · AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16). |
| 306 | · AND LAND PAID FOR OUT OF THE VAULT (0.7.6). |
| 335 | · 2. a city under stress |
| 398 | · AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK. |
| 447 | · 4. AND A WORKS THAT MAKES ITS OWN POWER AND BUYS ITS CONSUMABLES |
| 498 | · OLD STEEL (0.7.123, batch I4; OldMetal): a Scrap Yard buys the |
| 574 | · OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9): |
| 637 | · THE CITY'S PAPER ON ITS BOOKS (CD3, spec-city-debt.md section 5; |
| 752 | · 12. the services' upkeep is bought of Business Services |
| 846 | · 13. the households' books close |
| 870 | · 14. a cent that vanishes from a household is seen |
| 897 | · 15. a bond's face that no cell holds is named |
| 989 | · 16. what comes off the payslips is in transit a press |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `MoneyCheck.OWN_LINES` | `MoneyAudit.OWN_LINES` | The households' own lines (HL1), by the label MoneyAudit.households() notes them under. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |
| 46 | `static double worstDrift` | The worst post-audit drift any month of the last play() saw. |
| 47 | `static String worstDriftPool` |  |
| 54 | `int months` |  |
| 56 | `double seized` | What the bankrupt's savings repaid the bank with over the run (HouseholdBalance.getSeizedAtBankruptcy()). |
| 57 | `MoneyAudit.Households worst` |  |
| 58 | `final java.util.Map<String, Integer> fired` |  |
| 60 | `static HouseholdsRun lastRun` |  |
| 80 | `private final Game city` |  |
| 81 | `private double previous` |  |
| 82 | `int months` |  |
| 83 | `double worstResidual` |  |
| 85 | `double movedMostBy` | The press the amount in transit moved most in: by how much (signed) and the audit struck that month. |
| 86 | `MoneyAudit.Households movedMostIn` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 1034 | **type** `public class MoneyCheck` | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 5 | `static void quietly(Runnable work)` |  |
| 38 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 53 | 7 | **type** `static final class HouseholdsRun` | The households' identity (HL1) over one play(): the months struck, the worst, the ones that did not close, and how often each own line fired. |
| 63 | 3 | `static boolean householdsClose(MoneyAudit.Households h)` | The households' identity closes by the test the pools' does (LongPlaytest.audit()): a cent, or a part in ten million of what moved. |
| 68 | 3 | `static boolean closes(double residual, MoneyAudit.Households h)` | The same test for a residual that is not the households' own: `residual` read against what the month moved, a cent or a part in ten million. |
| 79 | 34 | **type** `static final class OldMetalTransit` | HL1's old-metal fixtures, press by press: what a yard's pending ledger holds of the households' old metal at the press's end (MoneyAudit.oldMetalInTransit(): in their savings, not yet out of the pools) and how the hou... |
| 89 | 4 | `OldMetalTransit(Game city)` _(in MoneyCheck.OldMetalTransit)_ | Read once the yard is built and before the first press: the amount in transit then is the first press's opening figure. |
| 95 | 12 | `void pressed()` _(in MoneyCheck.OldMetalTransit)_ | Read at the end of every press. |
| 109 | 3 | `double residualWithoutCarrier()` _(in MoneyCheck.OldMetalTransit)_ | The residual of the press the amount moved most in, had the carrier not been counted. |
| 115 | 32 | `static MoneyAudit.Result play(String label, Game g, int months, boolean verbose)` | Runs `months` and returns the worst relative residual seen, printing the worst month. |
| 149 | 27 | `static Game upkeepCity(GameFiles files, boolean office)` | HL1's fixture: a played city with care, schools, police, a bus network and three bank branches (the bank's charter is exempt from its operating cost) put up for the last month and held, and Contact Centres if `office`. |
| 183 | 20 | `static Game foundingCity(GameFiles files)` | HL1's bond fixture: section 1's founding city rebuilt (the build list the first play() of this file starts from) and played 120 months, the town whose sectors have issued bonds the households hold, and whose household... |
| 205 | 4 | `static double upkeepBills(Game c)` | The five upkeeps the treasury paid and the bank's operating cost, each off the getter the audit reads (Game.buyUpkeep()). |
| 210 | 846 | `public static void main(String[] args)` |  |

