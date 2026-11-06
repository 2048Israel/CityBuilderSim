# PolicyDatesCheck.java - 905 lines · 10 methods · 4 constants · harnesses

`ham/citybuildersim/PolicyDatesCheck.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

> Every Policy lever opens on the date Canada first had it, and no lever may open more than the law field's four years early (R0, slice 1:
> PolicyDates), and the levers TaxPolicy holds are held shut at zero until then and open on their date: the three income taxes at the city's
> founding rate, the promises at zero (R0, slice 2a: TaxPolicy.setMonth() and the setters' refusal; call 16: the founding rate).
> 
> WHY. PolicyDates is pure data that the gate in the model and the Policy screen will read, and a wrong date is no error anywhere: a tax that
> opens a year late is a city that goes untaxed for a year, and nobody sees it until the ensemble moves. So the dates are held to the study's
> table here, row for row, with the table typed out below as the specification and not read back from the class under test; research-1900.md
> 7.1 is the source; the gate's half (sections 7 to 9) takes its dates from PolicyDates and never as a bare month number. Rows the study dates that the game has no lever for (PL1 the 1915 workers' compensation levy, PL11 care subsidies, L1
> deposit insurance) are not here; the day the game grows one, its row is added to TABLE and to PolicyDates together.
> 
> What it has to prove:
> 
>   1. THE TABLE'S DATES. Every dated lever opens in the study's year and month, a year-only date in January, and CityCalendar gives that year
>      and month back from its opening month; the table and the open list between them name every lever once.
>   2. FOUNDING. In month 1 (January of the epoch) the open levers are open and every dated lever is closed.
>   3. THE DAY. Each dated lever is closed every month before its opening month and open on it and after (a sweep of the first months, then
>      +1, +12 and +600).
>   4. THE EARLY-OPENING BOUND. A lever's earliest opening is MAX_EARLY_MONTHS before its date, never before month 1; the floor holds on
>      fixtures; an open lever's earliest is month 1.
>   5. WHAT THE PLAYER IS TOLD. A dated lever has its entry key (but the policy rate: the table gives it none), its terms and a notice naming its
>      year (and its month where the study gives one) and where it opens (an income tax at the founding rate, built from the model's constant;
>      the minimum wage's floor at the going unskilled wage; the rest at zero); an open lever has no key, its reason, and no notice.
>   6. HISTORY'S ORDER. The dates run in the table's order, which is the declared order, and no two dated levers share an entry key.
>   7. THE GATE ON A BARE POLICY. A policy told it is month 1 holds every gated lever's dials at zero from their defaults and refuses their
>      setters (writing nothing to the decision log); the property tax is settable; for each gated lever the dial reads zero the month before
>      it opens; on its month an income tax reads the founding rate (DEFAULT_INCOME_TAX, its offsets zero) and a promise reads zero; the
>      setter takes a setting that differs from what it opened at and the months after neither hold nor reset it; "every tax" sets only the
>      open ones; the effective rates, the payslip and the pension read zero while shut; openAll() opens everything and a later month leaves
>      the rates a fixture set; a copy of a shut policy is shut and opens as the original would, an income tax at the founding rate.
>   8. A CITY. A new city founds with profit, sales and wage tax at zero and the property tax at its default, every gated lever shut in
>      month 1; the month is pushed to the policy each month (a lever moved to month 3 is shut in month 2 and open in month 3, where an
>      income tax reads the founding rate and the inbox raises its notice once, titled and worded from PolicyDates).
>   9. THE SAVE. The openings round-trip through a restore, keyed by the lever's name; a null or empty map opens every lever; an unknown key
>      is skipped; a lever the map does not name keeps history's month; a saved policy state comes back as saved into a policy that has only
>      heard month 1, and is held again only when the month says so; a restore and a reset empty what the policy held, so a rate they put
>      back is never overwritten by a later opening; a reset policy is back on history's dates.
>  10. THE MINIMUM WAGE'S FLOOR (R0, call 12). The dial is also the wage ladder's anchor, so it is not zeroed: before the law the floor binds
>      no wage (a glut takes the unskilled wage under the figure, to the band's own bottom) while the anchor and every base wage stand; the
>      player's dial and its nudge, through Game, are refused in month 1 and taken from the opening month; a load pushes the month to the
>      labour market; the inbox raises the opening notice once; a city saved the month before the wage tax opens, read back and told the
>      next month, opens it at the founding rate (the load tells the month after the restore, not before).
>  11. THE TWO READERS AND THE LOANS (R0, calls 14 and 15). The founding quote of the builders' sales tax is a month-1 city's, not a bare
>      policy's; a city's households start on the policy's pension, not the 45% default; before student aid opens a student is lent nothing
>      and the loan rate is refused, and the household book is told so each month.
>  12. ZONING'S GATE (R0, call 13). Until zoning opens (PL6, 1924 on history's dates) Game.setZone() draws no zone and writes nothing to the
>      decision log, while the forest law takes a setting (it is not the study's zoning); a city whose zoning is moved is still refused the month
>      before and takes a zone in its month, where the inbox raises one notice, titled by Inbox.leverTitle() (no article, history's date);
>      TaxPolicy.openNow() opens one lever and no other, and an income tax so opened still opens at the founding rate when the month is told.
>  13. THE SHUT LEVER'S WORDS (R0, slice 3). The Policy screen's card for a shut lever is composed here and not in the screen: its first line
>      names the date, the lever with the article Inbox.leverTitle() uses and the terms, its second where the lever opens, and a city whose
>      opening a find moved is told history's date and the find.

**Uses:** [PolicyDates](PolicyDates.md) (128), [TaxPolicy](TaxPolicy.md) (116), [CityCalendar](CityCalendar.md) (14), [Game](Game.md) (13), [Notice](Notice.md) (9), [WageBand](WageBand.md) (8), [Sectors](Sectors.md) (8), [DecisionLog](DecisionLog.md) (6), [LabourMarket](LabourMarket.md) (6), [JobType](JobType.md) (6), [GameFiles](GameFiles.md) (5), [Zoning](Zoning.md) (5), [StudentHousehold](StudentHousehold.md) (2), [Inbox](Inbox.md) (2), [Founding](Founding.md) (1), [HouseholdAccounts](HouseholdAccounts.md) (1), [HouseholdBalance](HouseholdBalance.md) (1)

## Sections

| line | section |
|---:|---|
| 104 | · the gate's fixtures |
| 183 | · 1. the table's dates |
| 204 | · 2. founding |
| 217 | · 3. the day |
| 231 | · 4. the early-opening bound |
| 254 | · 5. what the player is told |
| 292 | · 6. history's order |
| 327 | · 7. the gate on a bare policy |
| 500 | · 8. a city |
| 564 | · 9. the save |
| 671 | · 10. the minimum wage's floor |
| 749 | · 11. two readers that quoted a shut lever, and the student loans |
| 789 | · 12. zoning's gate |
| 866 | · 13. the shut lever's words |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 81 | `PolicyDatesCheck.TABLE` | `List.of(new Row(Lever.POLICY_RATE, 1914, 8, true, ""), new Row(Lever.PROFIT_T...` | research-1900.md 7.1 and 7.2, in history's order: the specification the class is held to. |
| 96 | `PolicyDatesCheck.OPEN` | `List.of(Lever.PROPERTY_TAX, Lever.FARMLAND_RELIEF, Lever.FOOD_ASSISTANCE, Lev...` | The levers the study leaves open from the first day (7.1: "open"; the others by its 1900 rule, R0's notes, call 5). |
| 147 | `PolicyDatesCheck.INCOME_TAXES` | `List.of(Lever.PROFIT_TAX, Lever.WAGE_TAX, Lever.SALES_TAX)` | The three income taxes, typed here as the specification of PolicyDates.opensAtFoundingRate() (call 16): the levers that fund the city. |
| 173 | `PolicyDatesCheck.SETTING` | `TaxPolicy.DEFAULT_INCOME_TAX +.10` | A wage tax rate that is neither zero nor the rate an income tax opens at: a setting that took is told from an opening. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `private static int fails` |  |
| 70 | `private static int passes` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 839 | **type** `public class PolicyDatesCheck` | Every Policy lever opens on the date Canada first had it, and no lever may open more than the law field's four years early (R0, slice 1: PolicyDates), and the levers TaxPolicy holds are held shut at zero until then an... |
| 72 | 4 | `static void check(String label, boolean ok)` |  |
| 78 | 1 | **type** `record Row(Lever lever, int year, int month, boolean monthKnown, String entry)` | One row of research-1900.md 7.1: the year and month Canada first had the lever (monthKnown false: the study gives the year only). |
| 100 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month, worked out here and not by PolicyDates. |

### the gate's fixtures (lines 104-905)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 15 | `static void setEveryGatedDial(TaxPolicy p)` | Every gated dial set to a non-zero setting through the setters: what stays zero is what the gate refused. |
| 124 | 14 | `static double dialOf(TaxPolicy p, Lever lever)` | What one lever owns, as one number: the sum of the dials and offsets the gate holds for it (zero while it is held). |
| 140 | 5 | `static int zoningLines(Game g)` | How many lines the city's decision log holds under the zoning kind: what a zone, or the forest law, writes when it is taken. |
| 150 | 8 | `static double baseRateOf(TaxPolicy p, Lever tax)` | An income tax's base rate alone, which is what opens at the founding rate; an income tax's offsets open at zero. |
| 160 | 3 | `static double offsetOf(TaxPolicy p, Lever tax)` | An income tax's offsets summed over the fixture's band and sector: dialOf() less the base rate. |
| 165 | 6 | `static double dialsAfterSetting(TaxPolicy p)` | Sets every gated dial on a policy and returns what the gated levers own after it, summed. |
| 176 | 4 | `static double wageRateAfterSetting(TaxPolicy p)` | Tries the wage tax base at SETTING and returns what it reads. |
| 181 | 724 | `public static void main(String[] args)` |  |

