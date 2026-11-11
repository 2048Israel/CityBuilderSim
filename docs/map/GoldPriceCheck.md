# GoldPriceCheck.java - 491 lines · 23 methods · 6 constants · harnesses

`ham/citybuildersim/GoldPriceCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> Gold's price a month (batch GD1; spec-gold-money 4.1): GoldPrice's statutes, its table, its refusal of a table that is not whole, and the rule
> after the table, each held to the figures the study publishes and to the constants the class publishes, never a formula copied from it (the one
> exception is the draw's tags, restated once to prove them).
> 
> WHY. A par that is a digit off, a table read in the wrong unit, a July that is not the year's price, a rule whose pull or sigma is not the dial's,
> and a walk that depends on the order it is asked in would each leave GD2's vault holding a price that is not the one the study says. Nothing in the
> economy reads GoldPrice yet, so nothing else would notice.
> 
> THE DATA. No save, no city and no world file: sections 1 to 3, 6 and 7 read GoldPrice on a fixture US price level (3% a year), sections 4 and 5 on the
> Nations of the jar's twelve-nation fixture (WorldData.FIXTURE), which is always beside the classes.
> 
> What this has to prove:
>   1. THE PARS TO THE DIGIT: PAR_1900 rounds to 20.6718; January 1900 to March 1933 is PAR_1900 exactly and February 1934 to July 1971 is PAR_1934
>      exactly, on both paths; April 1933 to January 1934 is a geometric path, each step the same ratio, ending on PAR_1934; 1933's mean is the
>      study's 25.53 (USGS recorded 25.56) and 1934's is 35.00.
>   2. THE TABLE'S MONTHS: 55 years read, 1971 to 2025; 1980's figure is the source's 19,700,000 a ton in ounces; every July from 1972 is its year's
>      price on Historical, the months between are geometric, July 1971 is 35 though the table's 1971 is above 41, August 1971 lies between, and
>      August to December 2025 hold 2025's price.
>   3. A BROKEN TABLE IS REFUSED, NAMING ITS LINE: a gap, a year out of order or repeated, a value that is zero, negative or no number, an unknown
>      unit, a line of three fields or of five, a table that ends at 2024, begins at 1972 or runs past 2025, a missing header; the shipped text reads.
>   4. THE RULE'S SIGMA AND PULL: over eight seeds on Random, 4,800 months each, the slope of x(t) on x(t - 1) is PHI and the residual's sd is SIGMA, the
>      pooled mean of the last 2,400 months is within four of its expected sd of 0; the walk is the rule's (three months restated through the draw's
>      tags); on Historical the first month follows from December 2025's US$3,300, and a run to 2400 is finite and above zero.
>   5. A RELOAD CONTINUES THE PATH: a GoldPrice asked month 6000 first and then month 1000 gives every month 0 to 6000 to the bit what one asked in
>      order gives, on both paths; another seed is the same to July 1971 (Random) or December 2025 (Historical) and different in every ruled month.
>   6. THE PATHS AGREE IN THE OFFICIAL YEARS: Random and Historical are identical every month to July 1971, and differ in every month after.
>   7. GOLD IS CHEAP AT THE START: PAR_1900 over PriceLevel.FACTOR is about US$798 of 2025, and 2025's US$3,300 is about 4.13 times that.

**Uses:** [GoldPrice](GoldPrice.md) (142), [Nations](Nations.md) (34), [PriceLevel](PriceLevel.md) (6), [WorldData](WorldData.md) (5), [WorldDataCheck](WorldDataCheck.md) (1)

## Sections

| line | section |
|---:|---|
| 122 | 1. THE PARS TO THE DIGIT |
| 181 | 2. THE TABLE'S MONTHS |
| 245 | 3. A BROKEN TABLE IS REFUSED, NAMING ITS LINE |
| 329 | 4. THE RULE'S SIGMA AND PULL |
| 420 | 5. A RELOAD CONTINUES THE PATH |
| 459 | 6. THE PATHS AGREE IN THE OFFICIAL YEARS |
| 479 | 7. GOLD IS CHEAP AT THE START |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `GoldPriceCheck.out` | `System.out` | Where the harness prints: the console. |
| 46 | `GoldPriceCheck.SEED` | `Nations.DEFAULT_SEED` | The seed the single-path sections use, and the eight (it and seven more) the rule is regressed over. |
| 47 | `GoldPriceCheck.SEEDS` | `{ SEED, 7_001, 7_002, 7_003, 7_004, 7_005, 7_006, 7_007 }` |  |
| 50 | `GoldPriceCheck.RUN` | `4_800, TAIL = 2_400` | The months of a regression run, and the last of them the stationary mean is read over. |
| 53 | `GoldPriceCheck.LAST` | `at(2400, 1)` | The last month a reload is compared to: January 2400. |
| 95 | `GoldPriceCheck.US_LINE` | `year -> 8.333 * Math.pow(1.03, year - Nations.FIRST_YEAR)` | A US price level a year to stand where a world file is not wanted: 3% a year from 8.333 (any base would do). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 41 | `static int fails` |  |
| 56 | `static WorldData fixture` | The jar's twelve-nation fixture, open for sections 4 and 5. |
| 70 | `int n, bad` |  |
| 71 | `String first` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 39 | 453 | **type** `public class GoldPriceCheck` | Gold's price a month (batch GD1; spec-gold-money 4.1): GoldPrice's statutes, its table, its refusal of a table that is not whole, and the rule after the table, each held to the figures the study publishes and to the c... |
| 58 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 64 | 3 | `static void close(String label, double got, double wanted, double tol)` | Two figures that should be one, to a tolerance. |
| 69 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 73 | 7 | `void see(boolean ok, Supplier<String> what)` _(in GoldPriceCheck.Tally)_ |  |
| 82 | 6 | `static void tally(String label, Tally t)` |  |
| 89 | 1 | `static boolean same(double a, double b)` |  |
| 92 | 1 | `static int at(int year, int month)` | The month index of a calendar month: months since January of the calendar's first year (Nations.FIRST_YEAR). |
| 97 | 1 | `static GoldPrice gold(Nations.Path path, long seed)` |  |
| 99 | 20 | `public static void main(String[] args) throws Exception` |  |
| 120 | 1 | `static Nations nations(Nations.Path path, long seed)` |  |

### 1. THE PARS TO THE DIGIT (lines 122-180)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 54 | `static void thePars()` |  |

### 2. THE TABLE'S MONTHS (lines 181-244)

| line | len | member | says |
|---:|---:|---|---|
| 185 | 1 | `static int july(int year)` |  |
| 187 | 57 | `static void theTable()` |  |

### 3. A BROKEN TABLE IS REFUSED, NAMING ITS LINE (lines 245-328)

| line | len | member | says |
|---:|---:|---|---|
| 250 | 5 | `static List<String> rows()` | A good table's rows: a year each, 1971 to 2025, the value 100 plus the year after 1971, so row i is line i + 3 in the text below. |
| 257 | 1 | `static String text(List<String> rows)` | A table's text: a comment line, the header, the rows. |
| 259 | 9 | `static void refused(String label, String text, int line)` |  |
| 269 | 59 | `static void aBrokenTable() throws Exception` |  |

### 4. THE RULE'S SIGMA AND PULL (lines 329-419)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 5 | `static double lnUs(Nations nations, int t)` | ln of the nations' US price level in month t, restated: log-linear between Januaries. |
| 341 | 1 | `static double draw(long seed, int t)` | The draw the rule uses in month t, restated through Nations.scramble with the tags GoldPrice keeps (9_998, 300). |
| 344 | 1 | `static double x(GoldPrice g, int t)` | x of month t: ln of the real price over the target. |
| 346 | 73 | `static void theRule()` |  |

### 5. A RELOAD CONTINUES THE PATH (lines 420-458)

| line | len | member | says |
|---:|---:|---|---|
| 424 | 34 | `static void aReloadContinues()` |  |

### 6. THE PATHS AGREE IN THE OFFICIAL YEARS (lines 459-478)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 15 | `static void thePathsAgree()` |  |

### 7. GOLD IS CHEAP AT THE START (lines 479-491)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 8 | `static void cheapAtTheStart()` |  |

