# PolicyOpenCheck.java - 424 lines · 15 methods · 7 constants · harnesses

`ham/citybuildersim/PolicyOpenCheck.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> Every Policy lever is open from the city's first month, in every city, and nothing opens one later (OP1, 2026-11-19).
> 
> WHY. From R0 (2026-11-04) to OP1 each lever opened on the date Canada first had it: the income taxes 1916-1920, the minimum wage 1918,
> zoning 1924, the promises 1927-1966, and in a city founded the 1900 way the Advance Rate in 1914 and the Bank of Canada in 1935; a law find
> brought one up to four years forward, and the Policy screen drew a shut lever with its date. Jerus, 2026-11-19: "remove it, instead
> everything policy related is available right away, and later ill think of an actual tutorial". PolicyDatesCheck held the gate; this
> holds its absence, and it is written so that a gate put back fails it: a dated lever refuses its setter in month 1, or holds a setting at
> zero the month after, or opens a lever on a find, or reappears as a method by its old name. Canada's dates stay as the record (the
> research catalogue's law rows carry them) and the gold years stay history's (call 2): MoneyDatesCheck holds those.
> 
> What it has to prove:
> 
>   1. THE RECORD. PolicyDates keeps Canada's dates row for row (research-1900.md 7.1, the table typed out below), each as a game month
>      CityCalendar gives back, and the levers a city had before 1900 at month 1; the table and the open list name every lever once, the
>      dates run in the declared order, and no two levers share an entry key.
>   2. THE FOUNDING. In month 1 of a city founded the 1900 way and of one founded the default way, the three income taxes read the founding
>      rate (DEFAULT_INCOME_TAX, their offsets zero), exactly the levers PolicyDates.opensAtFoundingRate() names; every promise reads zero
>      (the pension, its contribution, EI's premium and benefit, the health premium, the grant and the loan rate); the minimum wage's floor
>      binds; the rule has the dial; and the builders' founding quote is the city's month-1 sales tax.
>   3. EVERY SETTER TAKES IN MONTH 1, in both cities: every dial of the twelve dated levers, the minimum wage through Game, a zone through
>      Game (and its line in the decision log); and a month on, every setting stands as set and the floor still binds.
>   4. MONEY IS THE PLAYER'S FROM THE FOUNDING. The 1900 city is founded on gold at par, its dial tied to the US$ (history's regime,
>      call 2); in month 1 the target and the holdings take a setting, the dollar can be floated, and the dial then takes a hand's rate, and
>      a month on gold is not forced back; the default city's dial, target, holdings and regime take settings in month 1 too.
>   5. A LAW FIND OPENS NOTHING (★1a). Every law row of a lever says so in its touches; in a 1900 city carried to the month before PL3's
>      window, the faculty find PL3 (the wage tax's law) and no dial of the policy moves, that month or the next, the wage tax having read
>      the founding rate all along; the find is told as a find and no lever's opening is told.
>   6. NOTHING TELLS A DATE. No Policy screen, Zoning or Trade card, Policy model or the inbox reads PolicyDates (its dates are history's
>      record only), and a 1900 city's first year raises no "lever:" notice.
>   7. THE GATE CANNOT COME BACK BY ITS OLD NAMES: TaxPolicy, PolicyDates, Game, DebtManager, CentralBank and DataSave declare none of the
>      gate's members (the month the policy heard, the openings, the shut words, the early-opening bound, the saved openings).

**Uses:** [TaxPolicy](TaxPolicy.md) (17), [Game](Game.md) (14), [ForeignAccounts](ForeignAccounts.md) (9), [DebtManager](DebtManager.md) (9), [PolicyDates](PolicyDates.md) (8), [DiscoveryCheck](DiscoveryCheck.md) (7), [WageBand](WageBand.md) (6), [CentralBank](CentralBank.md) (6), [CityCalendar](CityCalendar.md) (4), [Founding](Founding.md) (4), [Sectors](Sectors.md) (3), [ResearchCatalog](ResearchCatalog.md) (3), [DecisionLog](DecisionLog.md) (2), [DataSave](DataSave.md) (2), [Research](Research.md) (2), [GameFiles](GameFiles.md) (1), [Zoning](Zoning.md) (1), [HouseholdBalance](HouseholdBalance.md) (1), [MaterialsCheck](MaterialsCheck.md) (1)

**Used by (1):** [DiscoveryCheck](DiscoveryCheck.md)

## Sections

| line | section |
|---:|---|
| 113 | · the dials |
| 213 | · 1. the record |
| 241 | · 2. the founding |
| 266 | · 3. every setter takes in month 1 |
| 295 | · 4. money is the player's from the founding |
| 329 | · 5. a law find opens nothing |
| 345 | · 6. nothing tells a date |
| 365 | · 7. the gate cannot come back by its old names |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `PolicyOpenCheck.OUT` | `System.out` |  |
| 59 | `PolicyOpenCheck.QUIET` | `new PrintStream(OutputStream.nullOutputStream())` |  |
| 80 | `PolicyOpenCheck.TABLE` | `List.of(new Row(Lever.POLICY_RATE, 1914, 8, true, ""), new Row(Lever.PROFIT_T...` | research-1900.md 7.1 and 7.2, in history's order: the specification the record is held to (PolicyDatesCheck's, kept). |
| 95 | `PolicyOpenCheck.OPEN` | `List.of(Lever.PROPERTY_TAX, Lever.FARMLAND_RELIEF, Lever.FOOD_ASSISTANCE, Lev...` | The levers the study leaves open from the first day (7.1: "open"). |
| 99 | `PolicyOpenCheck.INCOME_TAXES` | `EnumSet.of(Lever.PROFIT_TAX, Lever.WAGE_TAX, Lever.SALES_TAX)` | The three income taxes, typed here as the specification of PolicyDates.opensAtFoundingRate() (call 16): the levers that fund the city. |
| 102 | `PolicyOpenCheck.PROMISES` | `EnumSet.of(Lever.PENSION_REPLACEMENT, Lever.PENSION_CONTRIBUTION, Lever.EI, L...` | The promises: each costs the city, and opens at zero. |
| 106 | `PolicyOpenCheck.OPENS_NOTHING` | `"; the find opens nothing(every lever is open from the founding)"` | What research.json's law row of a lever says since OP1, at the end of its touches. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 57 | `private static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 370 | **type** `public class PolicyOpenCheck` | Every Policy lever is open from the city's first month, in every city, and nothing opens one later (OP1, 2026-11-19). |
| 61 | 4 | `static void check(String label, boolean ok)` |  |
| 66 | 3 | `static void check(String label, boolean ok, String detail)` |  |
| 71 | 4 | `static void quietly(Runnable r)` | Runs a city's work with its printing silenced. |
| 77 | 1 | **type** `record Row(Lever lever, int year, int month, boolean monthKnown, String entry)` | One row of research-1900.md 7.1: the year and month Canada first had the lever (monthKnown false: the study gives the year only). |
| 109 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month, worked out here and not by PolicyDates. |

### the dials (lines 113-424)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 16 | `static void setEveryDial(TaxPolicy p)` | Every dial of the policy's dated levers set to a non-zero setting through the setters, none of them its opening rate. |
| 134 | 13 | `static double setting(Lever lever)` | What setEveryDial() set, lever by lever, as dialOf() reads it. |
| 149 | 14 | `static double dialOf(TaxPolicy p, Lever lever)` | What one of the policy's dated levers owns, as one number: its dials and offsets summed (the grant and the loan rate for student aid). |
| 165 | 8 | `static double baseRateOf(TaxPolicy p, Lever tax)` | An income tax's base rate alone, which is what opens at the founding rate. |
| 175 | 8 | `static double[] dials(TaxPolicy p)` | Every dial of the policy, the property tax's too, as one array: what a find must leave as it was. |
| 185 | 5 | `static int zoningLines(Game g)` | How many lines the city's decision log holds under the zoning kind. |
| 192 | 3 | `static long leverNotices(Game g)` | How many of a city's notices are a lever's opening (the R0 notice's key). |
| 197 | 5 | `static Game city(String label, boolean the1900Way)` | A city founded on Founding.defaults(), the 1900 way if asked, on scratch files. |
| 204 | 6 | `static Set<String> declared(Class<?> c)` | The names a class declares, methods and fields. |
| 211 | 174 | `public static void main(String[] args) throws Exception` |  |
| 390 | 34 | `static void lawFind() throws Exception` | Section 5's city: DiscoveryCheck's 1900 village with a University and a Law School, saved in its second month and carried by its file to the month before the wage tax's law (PL3) is open to the law faculty, then a pil... |

