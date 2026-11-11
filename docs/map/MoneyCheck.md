# MoneyCheck.java - 1,031 lines · 13 methods · 1 constants · harnesses

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

**Uses:** [MoneyAudit](MoneyAudit.md) (75), [Game](Game.md) (33), [GameFiles](GameFiles.md) (14), [PriceLevel](PriceLevel.md) (14), [Sector](Sector.md) (12), [Sectors](Sectors.md) (12), [OldMetal](OldMetal.md) (12), [Founding](Founding.md) (9), [Debt](Debt.md) (6), [TaxPolicy](TaxPolicy.md) (5), [BusinessServices](BusinessServices.md) (4), [Household](Household.md) (4), [CorporateBond](CorporateBond.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (3), [Statement](Statement.md) (3), [PolicyDates](PolicyDates.md) (3), [LandParcel](LandParcel.md) (2), [HeavyIndustry](HeavyIndustry.md) (2), [DebtManager](DebtManager.md) (2), [HouseholdBalance](HouseholdBalance.md) (2), [BuildingManager](BuildingManager.md) (1), [LandMarket](LandMarket.md) (1), [Mining](Mining.md) (1), [Good](Good.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [CapitalFlows](CapitalFlows.md) (1), [CopperRefining](CopperRefining.md) (1), [NationalAccounts](NationalAccounts.md) (1), [MediumTermBond](MediumTermBond.md) (1), [BondMarket](BondMarket.md) (1)... and 1 more

## Sections

| line | section |
|---:|---|
| 217 | · 1. a city that prospers |
| 258 | · AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16). |
| 309 | · AND LAND PAID FOR OUT OF THE VAULT (0.7.6). |
| 338 | · 2. a city under stress |
| 401 | · AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK. |
| 450 | · 4. AND A WORKS THAT MAKES ITS OWN POWER AND BUYS ITS CONSUMABLES |
| 501 | · OLD STEEL (0.7.123, batch I4; OldMetal): a Scrap Yard buys the |
| 577 | · OLD COPPER (0.7.133, batch I8; OldMetal.copper(), the spec's 2.9): |
| 640 | · THE CITY'S PAPER ON ITS BOOKS (CD3, spec-city-debt.md section 5; |
| 755 | · 12. the services' upkeep is bought of Business Services |
| 821 | · 13. the households' books close |
| 845 | · 14. a cent that vanishes from a household is seen |
| 872 | · 15. a bond's face that no cell holds is named |
| 964 | · 16. what comes off the payslips is in transit a press |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 50 | `MoneyCheck.OWN_LINES` | `{ "+ households Unfunded", "- households UnsettledIncome", "+ households Unse...` | The households' own lines (HL1), by the label MoneyAudit.households() notes them under. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 25 | `static int fails` |  |
| 46 | `static double worstDrift` | The worst post-audit drift any month of the last play() saw. |
| 47 | `static String worstDriftPool` |  |
| 57 | `int months` |  |
| 59 | `double seized` | What the bankrupt's savings repaid the bank with over the run (HouseholdBalance.getSeizedAtBankruptcy()). |
| 60 | `MoneyAudit.Households worst` |  |
| 61 | `final java.util.Map<String, Integer> fired` |  |
| 63 | `static HouseholdsRun lastRun` |  |
| 83 | `private final Game city` |  |
| 84 | `private double previous` |  |
| 85 | `int months` |  |
| 86 | `double worstResidual` |  |
| 88 | `double movedMostBy` | The press the amount in transit moved most in: by how much (signed) and the audit struck that month. |
| 89 | `MoneyAudit.Households movedMostIn` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 23 | 1009 | **type** `public class MoneyCheck` | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. |
| 27 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 32 | 5 | `static void quietly(Runnable work)` |  |
| 38 | 6 | `static BuildingsTemplate t(Game g, String name)` |  |
| 56 | 7 | **type** `static final class HouseholdsRun` | The households' identity (HL1) over one play(): the months struck, the worst, the ones that did not close, and how often each own line fired. |
| 66 | 3 | `static boolean householdsClose(MoneyAudit.Households h)` | The households' identity closes by the test the pools' does (LongPlaytest.audit()): a cent, or a part in ten million of what moved. |
| 71 | 3 | `static boolean closes(double residual, MoneyAudit.Households h)` | The same test for a residual that is not the households' own: `residual` read against what the month moved, a cent or a part in ten million. |
| 82 | 34 | **type** `static final class OldMetalTransit` | HL1's old-metal fixtures, press by press: what a yard's pending ledger holds of the households' old metal at the press's end (MoneyAudit.oldMetalInTransit(): in their savings, not yet out of the pools) and how the hou... |
| 92 | 4 | `OldMetalTransit(Game city)` _(in MoneyCheck.OldMetalTransit)_ | Read once the yard is built and before the first press: the amount in transit then is the first press's opening figure. |
| 98 | 12 | `void pressed()` _(in MoneyCheck.OldMetalTransit)_ | Read at the end of every press. |
| 112 | 3 | `double residualWithoutCarrier()` _(in MoneyCheck.OldMetalTransit)_ | The residual of the press the amount moved most in, had the carrier not been counted. |
| 118 | 32 | `static MoneyAudit.Result play(String label, Game g, int months, boolean verbose)` | Runs `months` and returns the worst relative residual seen, printing the worst month. |
| 152 | 27 | `static Game upkeepCity(GameFiles files, boolean office)` | HL1's fixture: a played city with care, schools, police, a bus network and three bank branches (the bank's charter is exempt from its operating cost) put up for the last month and held, and Contact Centres if `office`. |
| 186 | 20 | `static Game foundingCity(GameFiles files)` | HL1's bond fixture: section 1's founding city rebuilt (the build list the first play() of this file starts from) and played 120 months, the town whose sectors have issued bonds the households hold, and whose household... |
| 208 | 4 | `static double upkeepBills(Game c)` | The five upkeeps the treasury paid and the bank's operating cost, each off the getter the audit reads (Game.buyUpkeep()). |
| 213 | 818 | `public static void main(String[] args)` |  |

