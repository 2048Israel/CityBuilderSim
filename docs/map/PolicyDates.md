# PolicyDates.java - 206 lines · 21 methods · 3 constants · model

`ham/citybuildersim/PolicyDates.java` - generated 2026-11-04 by CodeMap; line numbers are as of that run.

> When Canada first had each of the Policy tab's levers, as pure data: the game month each one opens in a city founded in January 1900, the
> study's entry for it, what Canada first did and on what terms, and how far a law discovery may bring it forward (R0, research-1900.md 7).
> 
> WHY. Until R0 every lever was open from month 1, so a city founded in January 1900 could tax incomes 17 years before Canada did, set a
> minimum wage 18 years early and promise old-age pensions 27 years before the country had any: the Policy screen was open in full on the
> first day, and there was nothing in it to learn. The game's institutions are Canada's (a law, unlike a technology, does not cross borders
> under licence), so each lever now opens at zero on the date Canada first had it, the inbox says when and on what terms, and the player
> decides: history is the tutorial. This class is only the dates. Nothing reads it yet (R0's first slice): the gate that holds a lever shut
> is the second slice's (TaxPolicy's effective rates and setters, the minimum wage, zoning, the save and the inbox) and the Policy screen's
> drawing of a shut one the third's. Money's two dates are here as data; the gate on them, and the gold years between, are batch R0f's.
> 
> Pure: no state, no instances, no randomness. A lever the study dates by the year alone opens in January of that year, because a month would
> be a claim the study does not make; one it dates to a month opens in that month.

**Uses:** [CityCalendar](CityCalendar.md) (3)

**Used by (4):** [Game](Game.md), [Inbox](Inbox.md), [PolicyDatesCheck](PolicyDatesCheck.md), [TaxPolicy](TaxPolicy.md)

## Sections

| line | section |
|---:|---|
| 22 | THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1) |
| 156 | · the dates as game months |
| 188 | · what the player is told |

## Enum constants

| line | constant | says |
|---:|---|---|
| 41 | `PolicyDates.Lever.POLICY_RATE` | Money, August 1914: the Finance Act let the government lend to the banks. |
| 44 | `PolicyDates.Lever.PROFIT_TAX` | Taxes, 1916: the first of the game's three income taxes. |
| 46 | `PolicyDates.Lever.WAGE_TAX` | Taxes, 1917: the game's wage tax, banded, is the personal income tax, so the Income War Tax Act is its date. |
| 48 | `PolicyDates.Lever.MINIMUM_WAGE` | Wages, 1918: Manitoba and British Columbia, for women (1917 by one source); men mostly in the 1930s (unverified). |
| 50 | `PolicyDates.Lever.SALES_TAX` | Taxes, 1920. |
| 52 | `PolicyDates.Lever.ZONING` | Zoning, 1924 (unverified; New York had it in 1916): until then everything is free, the decided default. |
| 54 | `PolicyDates.Lever.PENSION_REPLACEMENT` | Promises, 1927: the pension's replacement, the promise; its contribution is PENSION_CONTRIBUTION's. |
| 57 | `PolicyDates.Lever.CENTRAL_BANK` | Money, 11 March 1935: the Bank of Canada opens the rest of Money (the policy rate having opened in 1914). |
| 59 | `PolicyDates.Lever.STUDENT_AID` | Taxes and Promises, 1939: the grant and the loan rate together. |
| 61 | `PolicyDates.Lever.EI` | Taxes and Promises, 7 August 1940: the premium and the benefit together; contributions began on 1 July 1941. |
| 65 | `PolicyDates.Lever.HEALTH_PREMIUM` | Taxes, 1 January 1947: Saskatchewan's public hospital insurance (national cost-sharing followed in 1958). |
| 67 | `PolicyDates.Lever.PENSION_CONTRIBUTION` | Taxes, 1 January 1966: the contribution, the half of the pension the worker and the employer pay. |
| 70 | `PolicyDates.Lever.PROPERTY_TAX` | Open from founding: a council's mill rate, municipal before 1900. |
| 73 | `PolicyDates.Lever.FARMLAND_RELIEF` | Open from founding: relief on the farmland the property tax assesses. |
| 75 | `PolicyDates.Lever.FOOD_ASSISTANCE` | Open from founding: poor relief predates 1900. |
| 77 | `PolicyDates.Lever.TRANSIT_FARE` | Open from founding: a fare is a service's own price, and a city set its services. |
| 79 | `PolicyDates.Lever.TUITION_SCALES` | Open from founding: schools were the school boards', on a school rate. |
| 81 | `PolicyDates.Lever.HEALTH_FEE_SCALE` | Open from founding: the fee scale on the three care types. |
| 83 | `PolicyDates.Lever.FUEL_DUTY` | Open from founding: no source dates a fuel duty. |
| 85 | `PolicyDates.Lever.PORT_DUES` | Open from founding: harbour dues. |
| 87 | `PolicyDates.Lever.SECTOR_SUBSIDIES` | Open from founding: municipal "bonuses" to attract factories. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 146 | `PolicyDates.MAX_EARLY_MONTHS` | `48` | The most months a law discovery may bring a lever forward: four years, the law field's reach (research-1900.md 7.2). |
| 149 | `PolicyDates.SOURCES` | `"[P4] 3.1: [BURNS], [CYB25], [H1], [H113], [LOP], [SC-UI], [ILO63], [BOC-W], ...` | The sources the table's rows rest on, as the study lists them under it: P4 section 3.1 and the keys in its brackets. |
| 153 | `PolicyDates.OPEN_SOURCES` | `"research-1900.md 7.1 and [P4] 3.2: in 1900 a Canadian city set only its prop...` | The source for the levers no table row dates: the study's rule for 1900, that a city set its property tax, schools, services and bylaws. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 89 | `private final String label` |  |
| 90 | `private final int year` |  |
| 91 | `private final int monthOfYear` |  |
| 92 | `private final boolean monthKnown` |  |
| 93 | `private final boolean open` |  |
| 94 | `private final String entry` |  |
| 95 | `private final String act` |  |
| 96 | `private final String terms` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 189 | **type** `public final class PolicyDates` | When Canada first had each of the Policy tab's levers, as pure data: the game month each one opens in a city founded in January 1900, the study's entry for it, what Canada first did and on what terms, and how far a la... |
| 20 | 1 | `private PolicyDates()` |  |

### THE LEVERS AND THEIR DATES (R0, 2026-11-04; docs/studies/research-1900.md 7.1) (lines 22-155)

| line | len | member | says |
|---:|---:|---|---|
| 38 | 103 | **type** `public enum Lever` | The Policy levers that history dates, in the order they open, then the ones open from founding. |
| 99 | 3 | `Lever(String label, int year, int monthOfYear, String entry, String act, String terms)` _(in PolicyDates.Lever)_ | A lever the study dates to a month. |
| 104 | 3 | `Lever(String label, int year, String entry, String act, String terms)` _(in PolicyDates.Lever)_ | A lever the study dates by the year alone: it opens in January. |
| 109 | 3 | `Lever(String label, String reason)` _(in PolicyDates.Lever)_ | A lever open from founding, with the reason. |
| 113 | 10 | `private Lever(String label, int year, int monthOfYear, boolean monthKnown, boolean open, String entry, String act, String terms)` _(in PolicyDates.Lever)_ |  |
| 125 | 1 | `public String label()` _(in PolicyDates.Lever)_ | The lever as a player reads it: "income tax". |
| 127 | 1 | `public int year()` _(in PolicyDates.Lever)_ | The calendar year it opens in. |
| 129 | 1 | `public int monthOfYear()` _(in PolicyDates.Lever)_ | 1-12; 1 where the study dates it by the year alone. |
| 131 | 1 | `public boolean monthKnown()` _(in PolicyDates.Lever)_ | Whether the study dates it to a month (else it opens in January of its year). |
| 133 | 1 | `public String entry()` _(in PolicyDates.Lever)_ | The study's entry key ("PL3", "L5"), or "" for an open lever and for the policy rate. |
| 135 | 1 | `public String act()` _(in PolicyDates.Lever)_ | The law or body that first had it ("the Income War Tax Act"), or "". |
| 137 | 1 | `public String terms()` _(in PolicyDates.Lever)_ | Its first terms in a phrase with no full stop; for an open lever, the reason it is open. |
| 139 | 1 | `public String sources()` _(in PolicyDates.Lever)_ | The sources the terms rest on: SOURCES for a dated lever, OPEN_SOURCES for an open one. |

### the dates as game months (lines 156-187)

| line | len | member | says |
|---:|---:|---|---|
| 159 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month: the epoch's January is month 1, and a date before the epoch is month 1 (open at founding). |
| 164 | 3 | `public static int openingMonth(Lever lever)` | The game month a lever opens in on Canada's date; 1 for a lever open from founding. |
| 169 | 3 | `public static boolean isOpen(Lever lever, int gameMonth)` | Whether a lever is open in a game month: from its opening month on. |
| 174 | 3 | `public static boolean openAtFounding(Lever lever)` | Whether a lever is open in the founding month, the first month of the game. |
| 179 | 3 | `public static int earliestOpening(Lever lever)` | The earliest month a law discovery may bring a lever to: MAX_EARLY_MONTHS before its opening month, never before month 1. |
| 184 | 3 | `static int earliestFor(int openingMonth)` | MAX_EARLY_MONTHS before an opening month, floored at month 1 (separate from the lever so the floor can be held on fixtures). |

### what the player is told (lines 188-206)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 4 | `public static String openingDate(Lever lever)` | The date a lever opens as the Policy screen prints it: "1917", "August 1940", or "founding" for a lever open from the start. |
| 200 | 6 | `public static String notice(Lever lever)` | The inbox sentence for the month a lever opens: when Canada first had it, under what, on what terms, and that the setting is the player's. |

