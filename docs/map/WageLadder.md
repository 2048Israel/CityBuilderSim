# WageLadder.java - 145 lines · 10 methods · 7 constants · model

`ham/citybuildersim/WageLadder.java` - generated 2026-11-08 by CodeMap; line numbers are as of that run.

> The wage ladder a city is founded on: today's (PayTier's) or 1901's (the census's).
> 
> WHY A SECOND LADDER, AND NOT PayTier EDITED (R0d, 2026-11-06; docs/studies/research-1900.md 5.2 and 12.1, "Wage ladder: make the
> normalization explicit"; R0-notes call 2). A city founded in 1900 pays 1901's wages: about a quarter of today's real wage, with the 1901
> census's ratios between the skills (an engineer 3.2 times a factory man, against 2.8 between today's high-tech engineer and a labourer).
> PayTier is every default city's ladder - the harnesses, the playtest, every save a player holds measure it - so editing its figures would
> move every city by one bit. The ladder is therefore a choice made once, at the founding, and read through here: TODAY answers with PayTier's
> own figures (to the bit), Y1901 with the census's. It is derived from the saved founding (Founding.isUnfitted(), R1c call 1 and R2 call 3),
> so nothing about it is saved.
> 
> ONE NORMALIZATION, STATED ONCE. The census gives dollars a year; the game pays thousands a month, in founding money, and founding money is
> 1900's price level (PriceLevel.FACTOR). The US CPI is the same in 1900 and 1901 (8.333), and the Canadian dollar stood at par with the US
> dollar on gold, so a 1901 census dollar IS a founding dollar: monthlyFromAnnual() converts it and nothing else touches it - no PriceLevel.FACTOR
> on it, and no cost-of-living multiplier on top, because LabourMarket's own costOfLiving starts at 1 in the founding month (research-1900 12.1,
> "Do not apply both a historical multiplier and LabourMarket's cost-of-living multiplier to the same wage").
> 
> WHAT THE CENSUS DOES NOT SAY IS FLAGGED. One rung, the elite (doctors and lawyers, self-employed in 1901 and so outside a census of
> wage-earners), is not in the 1901 tables: the table keeps an estimate flag and says how it was made, and the harness reads it
> (WageLadderCheck 1), so a figure that is an estimate never reads as an observation.
> 
> Sources (Statistics Canada, under the Statistics Canada Open Licence, commercial use allowed; B1 is also out of Crown copyright, Copyright
> Act s.12, fifty years after publication):
>   [B1]     Census and Statistics Office, Bulletin I, "Wage-earners by occupations" (the 1901 census, 1907), Table II,
>            https://publications.gc.ca/collections/collection_2017/statcan/CS98-1901P-1.pdf
>   [CYB07a] Canada Year Book 1907, "Earnings at regular work, by class of occupation, 1901",
>            https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015901-eng.htm
>   [CYB07b] Canada Year Book 1907, "Employees and salaries, by principal occupation in the professional class, 1901",
>            https://www65.statcan.gc.ca/acyb02/1907/acyb02_1907015903-eng.htm

**Uses:** [PayTier](PayTier.md) (17), [Founding](Founding.md) (1), [JobType](JobType.md) (1)

**Used by (6):** [Game](Game.md), [LabourMarket](LabourMarket.md), [PopulationManager](PopulationManager.md), [PrizeCheck](PrizeCheck.md), [TaxPolicy](TaxPolicy.md), [WageLadderCheck](WageLadderCheck.md)

## Sections

| line | section |
|---:|---|
| 40 | THE CONVERSION, ONCE |
| 60 | THE 1901 TABLE: one rung per PayTier, in PayTier's order; each figure is one line to change |
| 96 | THE TWO LADDERS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 47 | `WageLadder.FOUNDING_PER_CENSUS_DOLLAR` | `1.0` | What one 1901 census dollar is in founding money: one. |
| 50 | `WageLadder.MONTHS_A_YEAR` | `12` | The months in the census's year of earnings. |
| 53 | `WageLadder.THOUSAND` | `1000` | The game's wages are in thousands: .0227 reads $22.67. |
| 63 | `WageLadder.CIVIL_ENGINEERS_1901` | `1316.31` | Civil engineers' earnings in 1901 (B1, Table II: $852,968 over 648 men), the professional rung and the base the elite rung's estimate is struck on. |
| 70 | `WageLadder.RUNGS_1901` | `List.of(new Rung(PayTier.UNSKILLED, "Labourers, men", 256.29, "B1", false, "$...` | The 1901 ladder, a rung for each PayTier in PayTier's order, each the comparator nearest the rung's own today (PayTier's comments), men's earnings at regular work for a year (R0d's research, 2026-11-06: the averages B... |
| 99 | `WageLadder.TODAY` | `new WageLadder("today's", false)` | Today's ladder: PayTier's own figures, the default of every city. |
| 102 | `WageLadder.Y1901` | `new WageLadder("1901's", true)` | 1901's ladder: the census's earnings in founding money, for a city founded the 1900 way. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 109 | `private final String name` |  |
| 110 | `private final boolean historical` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 111 | **type** `public final class WageLadder` | The wage ladder a city is founded on: today's (PayTier's) or 1901's (the census's). |
| 38 | 1 | **type** `public record Rung(PayTier tier, String comparator, double annual1901, String source, boolean estimate, Str...` | One rung of the 1901 table: the PayTier it prices, the census occupation it stands on, the year's earnings, where it was read, whether it is an estimate, and a note. |

### THE CONVERSION, ONCE (lines 40-59)

| line | len | member | says |
|---:|---:|---|---|
| 56 | 3 | `public static double monthlyFromAnnual(double annualCensusDollars)` | A year's earnings in census dollars as a month's wage in thousands of founding money: the one conversion. |

### THE 1901 TABLE: one rung per PayTier, in PayTier's order; each figure is one line to change (lines 60-95)

| line | len | member | says |
|---:|---:|---|---|
| 85 | 7 | `static { ... }` |  |
| 94 | 1 | `public static Rung rung1901(PayTier tier)` | The 1901 rung for a tier (the table is in PayTier's order). |

### THE TWO LADDERS (lines 96-145)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 3 | `public static WageLadder of(Founding founding)` | The ladder a city is founded on: 1901's where it is founded the 1900 way (Founding.isUnfitted()), today's otherwise. |
| 112 | 4 | `private WageLadder(String name, boolean historical)` |  |
| 118 | 1 | `public String name()` | What the ladder is called: today's or 1901's. |
| 121 | 1 | `public boolean isHistorical()` | Whether this is the census's ladder rather than PayTier's. |
| 124 | 3 | `public double monthlyWage(PayTier tier)` | What a month of a rung pays, in thousands of founding money: PayTier's own on today's ladder, the 1901 rung's year over twelve on 1901's. |
| 129 | 1 | `public double anchor()` | The bottom rung's wage: what the minimum wage starts at, the dial's bounds are multiples of, and the rent and the pension are struck on. |
| 135 | 8 | `public double ratio(JobType job)` | What this job pays relative to the unskilled floor on this ladder. |
| 144 | 1 | `public String toString()` |  |

