# PolicyDates.java - 295 lines · 31 methods · 3 constants · model

`ham/citybuildersim/PolicyDates.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> When Canada first had each of the Policy tab's levers, as pure data: the game month each one opens in a city founded in January 1900, the
> study's entry for it, what Canada first did and on what terms, and how far a law discovery may bring it forward (R0, research-1900.md 7).
> 
> WHY. Until R0 every lever was open from month 1, so a city founded in January 1900 could tax incomes 17 years before Canada did, set a
> minimum wage 18 years early and promise old-age pensions 27 years before the country had any: the Policy screen was open in full on the
> first day, and there was nothing in it to learn. The game's institutions are Canada's (a law, unlike a technology, does not cross borders
> under licence), so each lever now opens on the date Canada first had it, the inbox says when and on what terms, and the player decides:
> history is the tutorial. The three income taxes open at the city's founding rate and the promises (the pensions, EI, the health premium,
> student aid) at zero (opensAtFoundingRate(), call 16). This class is only the dates. Nothing reads it yet (R0's first slice): the gate that holds a lever shut
> is the second slice's (TaxPolicy's effective rates and setters, the minimum wage, zoning, the save and the inbox) and the Policy screen's
> drawing of a shut one the third's. Money's two dates are here as data; the gate on them, and the gold years between, are batch R0f's.
> 
> Pure: no state, no instances, no randomness. A lever the study dates by the year alone opens in January of that year, because a month would
> be a claim the study does not make; one it dates to a month opens in that month.

**Uses:** [CityCalendar](CityCalendar.md) (4), [DecisionLog](DecisionLog.md) (1), [TaxPolicy](TaxPolicy.md) (1)

**Used by (18):** [DiscoveryCheck](DiscoveryCheck.md), [EquipmentCheck](EquipmentCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [Inbox](Inbox.md), [KeptCheck](KeptCheck.md), [LandmassCheck](LandmassCheck.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyScreen](PolicyScreen.md), [RecipeCheck](RecipeCheck.md), [ResearchCatalog](ResearchCatalog.md), [ResearchCheck](ResearchCheck.md), [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [TaxPolicy](TaxPolicy.md), [ZoningScreen](ZoningScreen.md), [ZoningViewCheck](ZoningViewCheck.md)

## Sections

| line | section |
|---:|---|
| 23 | THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1) |
| 157 | · the dates as game months |
| 209 | · what the player is told |

## Enum constants

| line | constant | says |
|---:|---|---|
| 42 | `PolicyDates.Lever.POLICY_RATE` | Money, August 1914: the Finance Act let the government lend to the banks. |
| 45 | `PolicyDates.Lever.PROFIT_TAX` | Taxes, 1916: the first of the game's three income taxes. |
| 47 | `PolicyDates.Lever.WAGE_TAX` | Taxes, 1917: the game's wage tax, banded, is the personal income tax, so the Income War Tax Act is its date. |
| 49 | `PolicyDates.Lever.MINIMUM_WAGE` | Wages, 1918: Manitoba and British Columbia, for women (1917 by one source); men mostly in the 1930s (unverified). |
| 51 | `PolicyDates.Lever.SALES_TAX` | Taxes, 1920. |
| 53 | `PolicyDates.Lever.ZONING` | Zoning, 1924 (unverified; New York had it in 1916): until then everything is free, the decided default. |
| 55 | `PolicyDates.Lever.PENSION_REPLACEMENT` | Promises, 1927: the pension's replacement, the promise; its contribution is PENSION_CONTRIBUTION's. |
| 58 | `PolicyDates.Lever.CENTRAL_BANK` | Money, 11 March 1935: the Bank of Canada opens the rest of Money (the policy rate having opened in 1914). |
| 60 | `PolicyDates.Lever.STUDENT_AID` | Taxes and Promises, 1939: the grant and the loan rate together. |
| 62 | `PolicyDates.Lever.EI` | Taxes and Promises, 7 August 1940: the premium and the benefit together; contributions began on 1 July 1941. |
| 66 | `PolicyDates.Lever.HEALTH_PREMIUM` | Taxes, 1 January 1947: Saskatchewan's public hospital insurance (national cost-sharing followed in 1958). |
| 68 | `PolicyDates.Lever.PENSION_CONTRIBUTION` | Taxes, 1 January 1966: the contribution, the half of the pension the worker and the employer pay. |
| 71 | `PolicyDates.Lever.PROPERTY_TAX` | Open from founding: a council's mill rate, municipal before 1900. |
| 74 | `PolicyDates.Lever.FARMLAND_RELIEF` | Open from founding: relief on the farmland the property tax assesses. |
| 76 | `PolicyDates.Lever.FOOD_ASSISTANCE` | Open from founding: poor relief predates 1900. |
| 78 | `PolicyDates.Lever.TRANSIT_FARE` | Open from founding: a fare is a service's own price, and a city set its services. |
| 80 | `PolicyDates.Lever.TUITION_SCALES` | Open from founding: schools were the school boards', on a school rate. |
| 82 | `PolicyDates.Lever.HEALTH_FEE_SCALE` | Open from founding: the fee scale on the three care types. |
| 84 | `PolicyDates.Lever.FUEL_DUTY` | Open from founding: no source dates a fuel duty. |
| 86 | `PolicyDates.Lever.PORT_DUES` | Open from founding: harbour dues. |
| 88 | `PolicyDates.Lever.SECTOR_SUBSIDIES` | Open from founding: municipal "bonuses" to attract factories. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 147 | `PolicyDates.MAX_EARLY_MONTHS` | `48` | The most months a law discovery may bring a lever forward: four years, the law field's reach (research-1900.md 7.2). |
| 150 | `PolicyDates.SOURCES` | `"[P4] 3.1: [BURNS], [CYB25], [H1], [H113], [LOP], [SC-UI], [ILO63], [BOC-W], ...` | The sources the table's rows rest on, as the study lists them under it: P4 section 3.1 and the keys in its brackets. |
| 154 | `PolicyDates.OPEN_SOURCES` | `"research-1900.md 7.1 and [P4] 3.2: in 1900 a Canadian city set only its prop...` | The source for the levers no table row dates: the study's rule for 1900, that a city set its property tax, schools, services and bylaws. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 90 | `private final String label` |  |
| 91 | `private final int year` |  |
| 92 | `private final int monthOfYear` |  |
| 93 | `private final boolean monthKnown` |  |
| 94 | `private final boolean open` |  |
| 95 | `private final String entry` |  |
| 96 | `private final String act` |  |
| 97 | `private final String terms` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 19 | 277 | **type** `public final class PolicyDates` | When Canada first had each of the Policy tab's levers, as pure data: the game month each one opens in a city founded in January 1900, the study's entry for it, what Canada first did and on what terms, and how far a la... |
| 21 | 1 | `private PolicyDates()` |  |

### THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1) (lines 23-156)

| line | len | member | says |
|---:|---:|---|---|
| 39 | 103 | **type** `public enum Lever` | The Policy levers that history dates, in the order they open, then the ones open from founding. |
| 100 | 3 | `Lever(String label, int year, int monthOfYear, String entry, String act, String terms)` _(in PolicyDates.Lever)_ | A lever the study dates to a month. |
| 105 | 3 | `Lever(String label, int year, String entry, String act, String terms)` _(in PolicyDates.Lever)_ | A lever the study dates by the year alone: it opens in January. |
| 110 | 3 | `Lever(String label, String reason)` _(in PolicyDates.Lever)_ | A lever open from founding, with the reason. |
| 114 | 10 | `private Lever(String label, int year, int monthOfYear, boolean monthKnown, boolean open, String entry, String act, String terms)` _(in PolicyDates.Lever)_ |  |
| 126 | 1 | `public String label()` _(in PolicyDates.Lever)_ | The lever as a player reads it: "income tax". |
| 128 | 1 | `public int year()` _(in PolicyDates.Lever)_ | The calendar year it opens in. |
| 130 | 1 | `public int monthOfYear()` _(in PolicyDates.Lever)_ | 1-12; 1 where the study dates it by the year alone. |
| 132 | 1 | `public boolean monthKnown()` _(in PolicyDates.Lever)_ | Whether the study dates it to a month (else it opens in January of its year). |
| 134 | 1 | `public String entry()` _(in PolicyDates.Lever)_ | The study's entry key ("PL3", "L5"), or "" for an open lever and for the policy rate. |
| 136 | 1 | `public String act()` _(in PolicyDates.Lever)_ | The law or body that first had it ("the Income War Tax Act"), or "". |
| 138 | 1 | `public String terms()` _(in PolicyDates.Lever)_ | Its first terms in a phrase with no full stop; for an open lever, the reason it is open. |
| 140 | 1 | `public String sources()` _(in PolicyDates.Lever)_ | The sources the terms rest on: SOURCES for a dated lever, OPEN_SOURCES for an open one. |

### the dates as game months (lines 157-208)

| line | len | member | says |
|---:|---:|---|---|
| 160 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month: the epoch's January is month 1, and a date before the epoch is month 1 (open at founding). |
| 165 | 3 | `public static int openingMonth(Lever lever)` | The game month a lever opens in on Canada's date; 1 for a lever open from founding. |
| 170 | 3 | `public static boolean isOpen(Lever lever, int gameMonth)` | Whether a lever is open in a game month: from its opening month on. |
| 175 | 3 | `public static boolean openAtFounding(Lever lever)` | Whether a lever is open in the founding month, the first month of the game. |
| 184 | 3 | `public static boolean opensAtFoundingRate(Lever lever)` | Whether a lever opens at the city's founding rate (TaxPolicy.DEFAULT_INCOME_TAX) and not at zero: the three income taxes, which fund the city, where a promise (a pension, EI, the health premium, student aid) costs it ... |
| 189 | 3 | `public static int earliestOpening(Lever lever)` | The earliest month a law discovery may bring a lever to: MAX_EARLY_MONTHS before its opening month, never before month 1. |
| 194 | 3 | `static int earliestFor(int openingMonth)` | MAX_EARLY_MONTHS before an opening month, floored at month 1 (separate from the lever so the floor can be held on fixtures). |
| 203 | 5 | `public static Lever leverOf(String code)` | The lever a discovery's code brings forward ("PL3" -> the income tax, "L5" -> the pension contribution), or null for a code that is no lever's key (a blank, a code the table does not date, and every lever that has no ... |

### what the player is told (lines 209-295)

| line | len | member | says |
|---:|---:|---|---|
| 212 | 3 | `public static String openingDate(Lever lever)` | The date a lever opens as the Policy screen prints it: "1917", "August 1940", or "founding" for a lever open from the start. |
| 217 | 4 | `private static String dateOf(Lever lever, int openingMonth)` | A lever's opening month as a date: "founding" for month 1 or earlier, the month and year where the study dates it to a month, else the year alone. |
| 226 | 6 | `public static String named(Lever lever)` | The lever as a player reads it, with its article, lower case: "an income tax", "a profit tax", "student aid", "zoning" (the three that take none: EI, student aid and zoning). |
| 237 | 3 | `public static String shutLine(Lever lever)` | What a shut lever's card says first, on Canada's date: "Opens 1917 - an income tax: 4% above $1,500 (single) or $3,000; a supertax of 2% to 25%": when it opens, what it is, and Canada's first terms for it. |
| 242 | 3 | `public static String shutLine(Lever lever, int cityOpeningMonth)` | The same on the city's own opening month (R3's finds move it earlier): the month and year where the study dates it to a month, else the year. |
| 247 | 3 | `public static String shutWhy(Lever lever)` | What a shut lever's card says second, on Canada's date: "Canada had none until then; the tax opens at the city's founding rate, 15%." |
| 252 | 6 | `public static String shutWhy(Lever lever, int cityOpeningMonth)` | The same for a city whose opening month a find has moved earlier: then "then" would name the wrong date, so it says history's and the find. |
| 265 | 6 | `public static String notice(Lever lever)` | The inbox sentence for the month a lever opens: when Canada first had it, under what, on what terms, where it opens (an income tax at the founding rate, the minimum wage's floor at the going unskilled wage, zoning wit... |
| 277 | 8 | `public static String stillShut(java.util.List<Lever> shut, java.util.function.ToIntFunction<Lever> cityOpeningMonth)` | The one line under a dial that sets several levers at once ("every tax") naming the ones still shut, which it leaves alone: "Still shut, so this dial leaves them alone: profit tax (opens 1916) and sales tax (opens 192... |
| 287 | 8 | `static String opensAt(Lever lever)` | Where a dated lever opens, as a sentence the notice finishes: the founding rate, the going unskilled wage, or zero. |

