# PolicyDates.java - 188 lines · 17 methods · 5 constants · model

`ham/citybuildersim/PolicyDates.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> When Canada first had each of the Policy tab's levers, as history: the game month Canada first had each one in a city founded in January
> 1900, the study's entry for it, and what Canada first did and on what terms (R0, research-1900.md 7); and the dollar's gold years, the one
> dated regime the game still plays (R0f).
> 
> WHY. R0 (0.9.10x, 2026-11-04) opened each lever on its date, the inbox saying when and on what terms, history as the tutorial; R3 let a
> law discovery bring one forward by up to four years. Jerus, 2026-11-19 (OP1, which replaced PP1's political points): "everything policy
> related is available right away, and later ill think of an actual tutorial". So since OP1 nothing in the game opens on these dates: every
> lever is open from a city's founding, in every city, each as a lever opened under R0 (opensAtFoundingRate(), call 16: the three income
> taxes at the city's founding rate, every promise at zero; TaxPolicy.openAtFounding()), and only the player moves them. The table stays as
> the study's record: research.json's law rows carry its dates (ResearchCheck holds the two equal), and a tutorial may read it. The gold
> years stay history's (OP1 call 2): a city founded the 1900 way is founded on gold at par, which August 1914 suspends, July 1926 restores
> and September 1931 ends (goldPeriod(), ForeignAccounts.hearMoneyMonth()), unless the player has taken the dollar off it first.
> 
> Pure: no state, no instances, no randomness. A lever the study dates by the year alone is Canada's in January of that year, because a
> month would be a claim the study does not make; one it dates to a month is Canada's in that month.

**Uses:** [CityCalendar](CityCalendar.md) (2)

**Used by (19):** [DiscoveryCheck](DiscoveryCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [EquipmentCheck](EquipmentCheck.md), [ExchangeRateCheck](ExchangeRateCheck.md), [ForeignAccounts](ForeignAccounts.md), [GovernmentPlayerCheck](GovernmentPlayerCheck.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [MedicineCheck](MedicineCheck.md), [MoneyDatesCheck](MoneyDatesCheck.md), [NewUnitCheck](NewUnitCheck.md), [PolicyOpenCheck](PolicyOpenCheck.md), [PostwarRecipeCheck](PostwarRecipeCheck.md), [RecipeCheck](RecipeCheck.md), [ResearchCatalog](ResearchCatalog.md), [ResearchCheck](ResearchCheck.md), [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [TaxPolicy](TaxPolicy.md)

## Sections

| line | section |
|---:|---|
| 24 | THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1) |
| 152 | · the dates as game months |

## Enum constants

| line | constant | says |
|---:|---|---|
| 43 | `PolicyDates.Lever.POLICY_RATE` | Money, August 1914: the Finance Act let the government lend to the banks. |
| 46 | `PolicyDates.Lever.PROFIT_TAX` | Taxes, 1916: the first of the game's three income taxes. |
| 48 | `PolicyDates.Lever.WAGE_TAX` | Taxes, 1917: the game's wage tax, banded, is the personal income tax, so the Income War Tax Act is its date. |
| 50 | `PolicyDates.Lever.MINIMUM_WAGE` | Wages, 1918: Manitoba and British Columbia, for women (1917 by one source); men mostly in the 1930s (unverified). |
| 52 | `PolicyDates.Lever.SALES_TAX` | Taxes, 1920. |
| 54 | `PolicyDates.Lever.ZONING` | Zoning, 1924 (unverified; New York had it in 1916): until then everything is free, the decided default. |
| 56 | `PolicyDates.Lever.PENSION_REPLACEMENT` | Promises, 1927: the pension's replacement, the promise; its contribution is PENSION_CONTRIBUTION's. |
| 59 | `PolicyDates.Lever.CENTRAL_BANK` | Money, 11 March 1935: the Bank of Canada opens the rest of Money (the policy rate having opened in 1914). |
| 61 | `PolicyDates.Lever.STUDENT_AID` | Taxes and Promises, 1939: the grant and the loan rate together. |
| 63 | `PolicyDates.Lever.EI` | Taxes and Promises, 7 August 1940: the premium and the benefit together; contributions began on 1 July 1941. |
| 67 | `PolicyDates.Lever.HEALTH_PREMIUM` | Taxes, 1 January 1947: Saskatchewan's public hospital insurance (national cost-sharing followed in 1958). |
| 69 | `PolicyDates.Lever.PENSION_CONTRIBUTION` | Taxes, 1 January 1966: the contribution, the half of the pension the worker and the employer pay. |
| 72 | `PolicyDates.Lever.PROPERTY_TAX` | Open from founding: a council's mill rate, municipal before 1900. |
| 75 | `PolicyDates.Lever.FARMLAND_RELIEF` | Open from founding: relief on the farmland the property tax assesses. |
| 77 | `PolicyDates.Lever.FOOD_ASSISTANCE` | Open from founding: poor relief predates 1900. |
| 79 | `PolicyDates.Lever.TRANSIT_FARE` | Open from founding: a fare is a service's own price, and a city set its services. |
| 81 | `PolicyDates.Lever.TUITION_SCALES` | Open from founding: schools were the school boards', on a school rate. |
| 83 | `PolicyDates.Lever.HEALTH_FEE_SCALE` | Open from founding: the fee scale on the three care types. |
| 85 | `PolicyDates.Lever.FUEL_DUTY` | Open from founding: no source dates a fuel duty. |
| 87 | `PolicyDates.Lever.PORT_DUES` | Open from founding: harbour dues. |
| 89 | `PolicyDates.Lever.SECTOR_SUBSIDIES` | Open from founding: municipal "bonuses" to attract factories. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 145 | `PolicyDates.SOURCES` | `"[P4] 3.1: [BURNS], [CYB25], [H1], [H113], [LOP], [SC-UI], [ILO63], [BOC-W], ...` | The sources the table's rows rest on, as the study lists them under it: P4 section 3.1 and the keys in its brackets. |
| 149 | `PolicyDates.OPEN_SOURCES` | `"research-1900.md 7.1 and [P4] 3.2: in 1900 a Canadian city set only its prop...` | The source for the levers no table row dates: the study's rule for 1900, that a city set its property tax, schools, services and bylaws. |
| 165 | `PolicyDates.GOLD_SUSPENDED` | `monthOf(1914, 8)` | Canada's suspension of gold, August 1914, when the Finance Act let the government lend to the banks (research-1900.md 7.2). |
| 168 | `PolicyDates.GOLD_RETURN` | `monthOf(1926, 7)` | Canada's return to gold, July 1926 (research-1900.md 7.2). |
| 171 | `PolicyDates.GOLD_END` | `monthOf(1931, 9)` | Canada's final departure, September 1931, following Britain; the study's 1929-31 interval ends here. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 91 | `private final String label` |  |
| 92 | `private final int year` |  |
| 93 | `private final int monthOfYear` |  |
| 94 | `private final boolean monthKnown` |  |
| 95 | `private final boolean open` |  |
| 96 | `private final String entry` |  |
| 97 | `private final String act` |  |
| 98 | `private final String terms` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 20 | 169 | **type** `public final class PolicyDates` | When Canada first had each of the Policy tab's levers, as history: the game month Canada first had each one in a city founded in January 1900, the study's entry for it, and what Canada first did and on what terms (R0,... |
| 22 | 1 | `private PolicyDates()` |  |

### THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1) (lines 24-151)

| line | len | member | says |
|---:|---:|---|---|
| 40 | 103 | **type** `public enum Lever` | The Policy levers, the ones history dates in the order Canada had them, then the ones a city set before 1900. |
| 101 | 3 | `Lever(String label, int year, int monthOfYear, String entry, String act, String terms)` _(in PolicyDates.Lever)_ | A lever the study dates to a month. |
| 106 | 3 | `Lever(String label, int year, String entry, String act, String terms)` _(in PolicyDates.Lever)_ | A lever the study dates by the year alone: Canada's in January. |
| 111 | 3 | `Lever(String label, String reason)` _(in PolicyDates.Lever)_ | A lever a city had before 1900, with the reason. |
| 115 | 10 | `private Lever(String label, int year, int monthOfYear, boolean monthKnown, boolean open, String entry, String act, String terms)` _(in PolicyDates.Lever)_ |  |
| 127 | 1 | `public String label()` _(in PolicyDates.Lever)_ | The lever as a player reads it: "income tax". |
| 129 | 1 | `public int year()` _(in PolicyDates.Lever)_ | The calendar year Canada first had it in. |
| 131 | 1 | `public int monthOfYear()` _(in PolicyDates.Lever)_ | 1-12; 1 where the study dates it by the year alone. |
| 133 | 1 | `public boolean monthKnown()` _(in PolicyDates.Lever)_ | Whether the study dates it to a month (else January of its year). |
| 135 | 1 | `public String entry()` _(in PolicyDates.Lever)_ | The study's entry key ("PL3", "L5"), or "" for an undated lever and for the policy rate. |
| 137 | 1 | `public String act()` _(in PolicyDates.Lever)_ | The law or body that first had it ("the Income War Tax Act"), or "". |
| 139 | 1 | `public String terms()` _(in PolicyDates.Lever)_ | Its first terms in a phrase with no full stop; for an undated lever, the reason a city had it in 1900. |
| 141 | 1 | `public String sources()` _(in PolicyDates.Lever)_ | The sources the terms rest on: SOURCES for a dated lever, OPEN_SOURCES for an undated one. |

### the dates as game months (lines 152-188)

| line | len | member | says |
|---:|---:|---|---|
| 155 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month: the epoch's January is month 1, and a date before the epoch is month 1. |
| 160 | 3 | `public static int canadaMonth(Lever lever)` | The game month Canada first had a lever in; 1 for a lever a city had before 1900. |
| 174 | 4 | `public static int goldPeriod(int month)` | The dated gold episode (1 from the epoch to GOLD_SUSPENDED, 2 from GOLD_RETURN to GOLD_END), or 0 between and after. |
| 185 | 3 | `public static boolean opensAtFoundingRate(Lever lever)` | Whether a lever opens at the city's founding rate (TaxPolicy.DEFAULT_INCOME_TAX) and not at zero: the three income taxes, which fund the city, where a promise (a pension and its contribution, EI, the health premium, s... |

