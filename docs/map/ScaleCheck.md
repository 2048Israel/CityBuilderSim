# ScaleCheck.java - 1,071 lines · 30 methods · 13 constants · harnesses

`ham/citybuildersim/ScaleCheck.java` - generated 2026-10-07 by CodeMap; line numbers are as of that run.

> A city past 2^31 people (0.7.53): its counts read back whole, its posts,
> doors and household places add up to the last one, and a save gives them
> all back.
> 
> WHY. Jerus plans cities of five to ten billion people, and until 0.7.53
> every count of them was an int, which wraps silently at 2,147,483,647. The
> scale study (the project's spec-scale.md, section 2) loaded a 5.09B copy of
> his city: its population read back as 938,901,759, its workforce stuck at
> 2,147,483,647, and unclamped the save would not parse at all, because the
> slot header's population was an int. The population, the workforce, the
> posts, the homes and the household capacity are longs now, through the
> model, the save, the history and the screens.
> 
> THE METHOD IS THE STUDY'S (its scale_save.py, ported as scale()). A founded
> city is saved and its save multiplied by K: the population, the workforce,
> the pyramid, the families, the households in each cell, the buildings and
> their sites, every sector's and market's stocks and flows, the loans, the
> treasury, the bank, the foreign accounts and the central bank. What is per
> household stays; a household's shares and bonds are divided by K so the
> registers still add up. K is a whole number, so every building count, and
> so every post and door, is exactly K times the founded city's. Its land's
> books - each holding's five areas and its forest's timber - are K times the
> founded city's too, as its square feet are (0.7.67): the books are drawn
> plots, counted exactly, and a copy's ground of millions of square
> kilometres is not drawn and counted in a harness's time (until 0.7.66 the
> load drew a centre to hold it from samples); its blocks, its fields and its
> iron are the founded city's.
> 
> EVERY SECTOR IS HELD in sections 2 to 5 (BusinessInvestment.holdSector()),
> as 0.7.53 wrote them: what they count does not depend on what the sectors
> build. Section 6 lets them build, at 5 and at 10 billion, now that the
> three order loops search instead of counting (0.7.54; Game, THE LARGEST
> SLICE, WITHOUT COUNTING TO IT): counting, a 10 billion copy of city2400
> took up to 143 s a month.
> 
> What this has to prove:
>   1. the founded city, and its copy K times over past 2^31 people;
>   2. the copy's counts read back whole - not wrapped, not stuck at the int
>      ceiling - and the slot header's population with them;
>   3. its posts, doors and household places are exactly K times the founded
>      city's, and every way of adding them up agrees;
>   4. three months at that size: still whole, still adding up, and the
>      history records what the screens show;
>   5. a save gives every one of those figures back, and the city it loads
>      plays its next month as the one that was saved;
>   6. a growing city - the playtest's at FREE_FIXTURE_MONTHS, after its
>      player's look then (0.7.58), with ground free for a home - copied to 5
>      and to 10 billion with its sectors free, so they order: every month
>      whole and adding up, money conserved to RELATIVE_RESIDUAL of what
>      moved from the first month, the city's books agreeing within
>      MoneyAudit.tolerance() where a harness's absolute cent would not, no
>      order reading more waits or asking the bond desk more often than its
>      search allows, and the median month under MONTH_MEDIAN_MS; and since
>      0.7.55, its ground priced as the city it was copied from is (the same
>      crowding, the same premium), its rents in reach as that city's are,
>      and homes built - where at 0.7.54 its land cost about two million
>      times the city's and it built nothing;
>   7. money at that size reads in its own unit: quadrillions, and never a
>      whole-dollar figure past what a double holds (Formats);
>   8. the audit's floor at size (0.7.63; 64 steps since 0.7.64): a floor of
> ... (5 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (25), [Game](Game.md) (21), [LongPlaytest](LongPlaytest.md) (19), [CityLand](CityLand.md) (10), [GameFiles](GameFiles.md) (9), [TreasuryCheck](TreasuryCheck.md) (7), [JobType](JobType.md) (6), [PopulationManager](PopulationManager.md) (5), [Sector](Sector.md) (4), [HistorySave](HistorySave.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [LandManager](LandManager.md) (3), [Formats](Formats.md) (3), [SaveHeader](SaveHeader.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingType](BuildingType.md) (2), [Bank](Bank.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [LandConversion](LandConversion.md) (1), [OrderSearchCheck](OrderSearchCheck.md) (1), [CentralBank](CentralBank.md) (1), [SectorBooks](SectorBooks.md) (1), [NationalAccounts](NationalAccounts.md) (1), [YearBook](YearBook.md) (1), [Mortgage](Mortgage.md) (1), [Resource](Resource.md) (1), [LandParcel](LandParcel.md) (1)

**Used by (1):** [OrderSearchCheck](OrderSearchCheck.md)

## Sections

| line | section |
|---:|---|
| 244 | · 1. the fixture, and its copy K times over |
| 289 | · 2. the counts read back whole |
| 332 | · 3. posts, doors and household places |
| 340 | · 4. three months at that size |
| 361 | · 5. the save round-trips them |
| 393 | · 6. the sectors free, at 5 and 10 billion |
| 415 | · 7. money at that size reads |
| 418 | · 8. the audit's floor at size |
| 895 | THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `ScaleCheck.TARGET_PEOPLE` | `5e9` | Jerus's smaller plan, in people: what the copy is scaled to reach. |
| 95 | `ScaleCheck.MONTHS_AT_SIZE` | `3` | Months the copy plays before it is saved. |
| 98 | `ScaleCheck.FIXTURE_MONTHS` | `120` | The founded city's age, in months, when it is saved and copied. |
| 101 | `ScaleCheck.FREE_PEOPLE` | `{ 5e9, 1e10 }` | Jerus's two plans, in people: the copies whose sectors are free (section 6). |
| 175 | `ScaleCheck.FREE_FIXTURE_MONTHS` | `545` | The month the playtest's city is copied at for section 6. |
| 178 | `ScaleCheck.FREE_MONTHS` | `6` | Months each free copy plays, every one timed. |
| 181 | `ScaleCheck.RELATIVE_RESIDUAL` | `1e-10` | The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. |
| 191 | `ScaleCheck.MONTH_MEDIAN_MS` | `1500` | The longest a free copy's median month may take, in milliseconds. |
| 625 | `ScaleCheck.AFFORDABILITY_WITHIN` | `.01` | How far a free copy's affordability pull may stand from the city's in any month (0.7.55): the rents' multiplier on the migrants' target (Migration.affordabilityPull()). |
| 921 | `ScaleCheck.INTENSIVE` | `Pattern.compile("(price\|Price\|rate\|Rate\|month\|Month\|Months\|share\|Share\|ratio\|...` | A name the scaler leaves alone wherever it meets one, unless EXTENSIVE names it: a price, a rate, a month, an id, a share, a ratio or a target, as scale_save.py's SKIP read them, and since 0.7.54 the seven ratios at i... |
| 939 | `ScaleCheck.EXTENSIVE` | `java.util.Set.of("recognisedThisMonth", "escalationThisMonth", "repairsThisMo...` | THE NAMES THE PATTERN READ WRONG (0.7.54). |
| 943 | `ScaleCheck.TOP` | `{ "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxC...` | The save's top-level keys scaled: scale_save.py's list, less the works yard. |
| 958 | `ScaleCheck.NAMED` | `java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks", "secto...` | The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 87 | `static int fails` |  |
| 88 | `static PrintStream out` |  |
| 89 | `static PrintStream quiet` |  |
| 634 | `final double crowding, premium, ground, sale, homesBuilt` |  |
| 635 | `final long peopleBefore, peopleAfter` |  |
| 636 | `final double[] affordability` |  |
| 675 | `final java.util.Map<String, double[]> worst` | {worst miss, the largest figure, months the old tolerance missed, misses of the new} for each identity, by name. |
| 676 | `final java.util.Map<String, Double> old` |  |
| 677 | `final java.util.Set<String> missedThisMonth` |  |
| 678 | `double previousClosing` |  |
| 679 | `int arrearsMonths` |  |
| 807 | `int sized, invested, mortgaged, largestNeeded, largestInvested, largestMortgage, mostWaits, mostDesk` |  |
| 808 | `int overDesk, overWaits` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 85 | 987 | **type** `public class ScaleCheck` | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them all back. |
| 193 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 198 | 5 | `static void same(String label, long actual, long expected)` |  |
| 204 | 5 | `static void quietly(Runnable r)` |  |
| 211 | 16 | `static void lookAt(Game g)` | One look by the playtest's player, as at each stop of its rhythm (OrderSearchCheck.playtestRhythm()): the schools seen to, its moves one a month, and two months after (0.7.58; see FREE_FIXTURE_MONTHS). |
| 228 | 13 | `public static void main(String[] args) throws Exception` |  |
| 242 | 179 | `static void run(Path root) throws Exception` |  |
| 429 | 26 | `static void auditFloor()` | Section 8 (0.7.64, batch L; the floor itself since 0.7.63, at 8 steps; fixK-notes 7 d): MoneyAudit.tolerance()'s floor at the size of the figures. |
| 457 | 14 | `static void reads()` | Section 7: Formats at ten billion - the quadrillion step, and cash() past the whole dollars a double holds. |
| 472 | 5 | `static void sameText(String label, String actual, String expected)` |  |
| 519 | 73 | `static void free(Path root, GameFiles small, long pop1, double target, Original original) throws Exception` | Section 6: a city's save (`small`, of `pop1` people) copied to `target` people and played with every sector free to build. |
| 594 | 23 | `static boolean sumsAgree(Game g)` | addsUp()'s sums, as a verdict rather than a list of lines: section 6 asks it every month. |
| 633 | 31 | **type** `static final class Original` | THE CITY THE COPIES ARE READ AGAINST (0.7.55): the playtest's town at FREE_FIXTURE_MONTHS, played its own FREE_MONTHS after it was saved - its ground as its first month struck it, its affordability pull each month, th... |
| 638 | 19 | `Original(Game city)` _(in ScaleCheck.Original)_ |  |
| 659 | 4 | `boolean calm()` _(in ScaleCheck.Original)_ | Whether the city's own rents priced nobody out in any of its months (0.7.62; FREE_FIXTURE_MONTHS). |
| 673 | 131 | **type** `static final class Books` | THE CITY'S BOOKS AT TEN BILLION (0.7.54): the identities the harnesses hold a played city to every month, read here at this size, each against the absolute tolerance its harness held it to before 0.7.54 and against Mo... |
| 687 | 9 | `void miss(String name, double oldTolerance, double newFloor, double miss, double size)` _(in ScaleCheck.Books)_ | One reading of an identity: what it missed by, the size of the figures it is made of, the tolerance its harness held it to before 0.7.54 (oldTolerance; for one already relative, that at this size) and the floor MoneyA... |
| 697 | 5 | `void month(Game g, double opening)` _(in ScaleCheck.Books)_ |  |
| 703 | 87 | `void readings(Game g)` _(in ScaleCheck.Books)_ |  |
| 791 | 12 | `void verdicts()` _(in ScaleCheck.Books)_ |  |
| 806 | 35 | **type** `static final class Orders implements BusinessInvestment.OrderWatch` | Every order the free copy decides: how big, and how many times it asked the bond desk or read a wait. |
| 811 | 6 | `public void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead)` _(in ScaleCheck.Orders)_ |  |
| 819 | 6 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in ScaleCheck.Orders)_ |  |
| 827 | 5 | `public void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRa...` _(in ScaleCheck.Orders)_ |  |
| 833 | 7 | `void verdicts()` _(in ScaleCheck.Orders)_ |  |
| 847 | 30 | `static void addsUp(Game g, String when)` | Every way of adding up the posts, the doors and the household places agrees, to the last one - a long sum of K-times counts is exact where an int sum would have wrapped. |
| 879 | 5 | `static long everyPost(Game g)` | Every post the buildings have, offered or not. |
| 885 | 3 | `static long last(List<Long> series)` |  |
| 889 | 5 | `static boolean quietlyGet(java.util.function.BooleanSupplier s)` |  |

### THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON (lines 895-1071)

| line | len | member | says |
|---:|---:|---|---|
| 961 | 71 | `static void scale(Path in, Path to, long k) throws Exception` |  |
| 1033 | 17 | `static JsonElement sc(JsonElement v, String name, long k)` |  |
| 1052 | 6 | `static JsonElement times(JsonElement v, long k)` | A number times K: a whole number stays whole and exact, a fraction is a double. |
| 1059 | 4 | `static JsonElement over(JsonElement v, long k)` |  |
| 1064 | 7 | `static void cleanUp(Path root)` |  |

