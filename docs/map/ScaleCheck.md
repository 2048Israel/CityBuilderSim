# ScaleCheck.java - 1,839 lines · 53 methods · 26 constants · harnesses

`ham/citybuildersim/ScaleCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

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
> treasury, the bank, the foreign accounts and the central bank - and, since
> FW9 (2026-10-25), the rest of the money and the quantities: the bond
> market, the exchange's books, the register of shares, the fund, the bank's
> records, the national accounts, the old metal, the care and the police
> (registers()), and the founding endowments (the hundred household places,
> the founding care and police, the grid and the wells), of which the copy
> holds K. What is per household stays - its money, its shares, its paper and
> its bonds - and the registers are K times over, so they still add up to
> their holders (they were left at one city's size, and the households'
> shares and bonds divided by K to match, until FW9). K is a whole number, so
> every building count, and so every post and door, is exactly K times the
> founded city's. Its land's books - each holding's five areas and its
> forest's timber - are K times the founded city's too, as its square feet
> are (0.7.67): the books are drawn plots, counted exactly, and a copy's
> ground of millions of square
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
> ... (21 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (26), [Game](Game.md) (25), [LongPlaytest](LongPlaytest.md) (19), [GameFiles](GameFiles.md) (12), [CityLand](CityLand.md) (10), [TreasuryCheck](TreasuryCheck.md) (7), [JobType](JobType.md) (6), [PriceIndex](PriceIndex.md) (6), [PopulationManager](PopulationManager.md) (5), [Sector](Sector.md) (4), [HistorySave](HistorySave.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [LandManager](LandManager.md) (3), [Formats](Formats.md) (3), [Equity](Equity.md) (3), [Bank](Bank.md) (3), [Crime](Crime.md) (3), [EducationType](EducationType.md) (3), [Timing](Timing.md) (2), [SaveHeader](SaveHeader.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingType](BuildingType.md) (2), [BusinessInvestment](BusinessInvestment.md) (2), [TempDirs](TempDirs.md) (1), [LandConversion](LandConversion.md) (1), [OrderSearchCheck](OrderSearchCheck.md) (1), [CentralBank](CentralBank.md) (1), [SectorBooks](SectorBooks.md) (1), [NationalAccounts](NationalAccounts.md) (1), [YearBook](YearBook.md) (1)... and 11 more

**Used by (1):** [OrderSearchCheck](OrderSearchCheck.md)

## Sections

| line | section |
|---:|---|
| 375 | · 1. the fixture, and its copy K times over |
| 420 | · 2. the counts read back whole |
| 463 | · 3. posts, doors and household places |
| 473 | · 4. three months at that size |
| 494 | · 5. the save round-trips them |
| 528 | · 6. the sectors free, at 5 and 10 billion |
| 555 | · 7. money at that size reads |
| 558 | · 8. the audit's floor at size |
| 561 | · 9. a copy tracks its city month by month |
| 1203 | THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON |
| 1375 | · THE MONEY AND THE QUANTITIES THE STUDY'S LIST LEFT AT ONE CITY'S SIZE (FW9, 2026-10-25) |
| 1728 | · the helpers the blocks above are written with |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 108 | `ScaleCheck.TARGET_PEOPLE` | `5e9` | Jerus's smaller plan, in people: what the copy is scaled to reach. |
| 111 | `ScaleCheck.MONTHS_AT_SIZE` | `3` | Months the copy plays before it is saved. |
| 114 | `ScaleCheck.FIXTURE_MONTHS` | `120` | The founded city's age, in months, when it is saved and copied. |
| 117 | `ScaleCheck.FREE_PEOPLE` | `{ 5e9, 1e10 }` | Jerus's two plans, in people: the copies whose sectors are free (section 6). |
| 262 | `ScaleCheck.FREE_FIXTURE_MONTHS` | `807` | The month the playtest's city is copied at for section 6. |
| 265 | `ScaleCheck.FREE_MONTHS` | `6` | Months each free copy plays, every one timed. |
| 268 | `ScaleCheck.RELATIVE_RESIDUAL` | `1e-10` | The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. |
| 278 | `ScaleCheck.MONTH_MEDIAN_MS` | `1500` | The longest a free copy's median month may take, in milliseconds. |
| 281 | `ScaleCheck.TRACK_TIMES` | `1000` | The copy section 9 reads against its city: its factor, K (FW9, 2026-10-25). |
| 289 | `ScaleCheck.TRACK_PEOPLE` | `.015` | How far the copy's people may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 292 | `ScaleCheck.TRACK_CASH` | `.0125` | How far the copy's treasury cash may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 295 | `ScaleCheck.TRACK_SAVINGS` | `.016` | How far the copy's household savings may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 303 | `ScaleCheck.TRACK_GDP` | `.14` | How far the copy's month GDP may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 306 | `ScaleCheck.TRACK_DEPOSITS` | `.026` | How far the copy's bank deposits may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 309 | `ScaleCheck.TRACK_PRICES` | `.004` | How far the copy's price level (PriceIndex.getIndex()) may miss the city's own, as a share of it, in any of FREE_MONTHS. |
| 316 | `ScaleCheck.TRACK_UNEMPLOYMENT` | `.033` | How far the copy's unemployment rate may stand from the city's, in rate points (.01 is a point), in any of FREE_MONTHS. |
| 319 | `ScaleCheck.TRACK_POLICY` | `.0005` | How far the copy's policy rate may stand from the city's, in rate points, in any of FREE_MONTHS. |
| 322 | `ScaleCheck.REGISTER_WITHIN` | `1e-9` | The most a register may stand from what its holders hold of it, as a share of the larger: the shares, the corporate bonds and the city's paper against the households' own (section 9). |
| 854 | `ScaleCheck.Figures.NAMES` | `{ "people", "cash", "savings", "gdp", "deposits", "prices", "unemployment", "...` |  |
| 856 | `ScaleCheck.Figures.EXTENSIVE` | `{ true, true, true, true, true, false, false, false }` | Whether each figure is a quantity (K times the city's) or a level (the city's own). |
| 857 | `ScaleCheck.Figures.WITHIN` | `{ TRACK_PEOPLE, TRACK_CASH, TRACK_SAVINGS, TRACK_GDP, TRACK_DEPOSITS, TRACK_P...` |  |
| 918 | `ScaleCheck.AFFORDABILITY_WITHIN` | `.01` | How far a free copy's affordability pull may stand from the city's in any month (0.7.55): the rents' multiplier on the migrants' target (Migration.affordabilityPull()). |
| 1229 | `ScaleCheck.INTENSIVE` | `Pattern.compile("(price\|Price\|rate\|Rate\|month\|Month\|Months\|share\|Share\|ratio\|...` | A name the scaler leaves alone wherever it meets one, unless EXTENSIVE names it: a price, a rate, a month, an id, a share, a ratio or a target, as scale_save.py's SKIP read them, and since 0.7.54 the seven ratios at i... |
| 1253 | `ScaleCheck.EXTENSIVE` | `java.util.Set.of("recognisedThisMonth", "escalationThisMonth", "repairsThisMo...` | THE NAMES THE PATTERN READ WRONG (0.7.54). |
| 1260 | `ScaleCheck.TOP` | `{ "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxC...` | The save's top-level keys scaled: scale_save.py's list, less the works yard. |
| 1275 | `ScaleCheck.NAMED` | `java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks", "secto...` | The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `static int fails` |  |
| 104 | `static PrintStream out` |  |
| 105 | `static PrintStream quiet` |  |
| 859 | `final long people` |  |
| 860 | `final double[] v` |  |
| 927 | `final double crowding, premium, ground, sale, homesBuilt` |  |
| 928 | `final long peopleBefore, peopleAfter` |  |
| 929 | `final double[] affordability` |  |
| 968 | `final java.util.Map<String, double[]> worst` | {worst miss, the largest figure, months the old tolerance missed, misses of the new} for each identity, by name. |
| 969 | `final java.util.Map<String, Double> old` |  |
| 970 | `final java.util.Set<String> missedThisMonth` |  |
| 971 | `double previousClosing` |  |
| 972 | `int arrearsMonths` |  |
| 1115 | `int sized, invested, mortgaged, largestNeeded, largestInvested, largestMortgage, mostWaits, mostDesk` |  |
| 1116 | `int overDesk, overWaits` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 101 | 1739 | **type** `public class ScaleCheck` | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them all back. |
| 324 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 329 | 5 | `static void same(String label, long actual, long expected)` |  |
| 335 | 5 | `static void quietly(Runnable r)` |  |
| 342 | 16 | `static void lookAt(Game g)` | One look by the playtest's player, as at each stop of its rhythm (OrderSearchCheck.playtestRhythm()): the schools seen to, its moves one a month, and two months after (0.7.58; see FREE_FIXTURE_MONTHS). |
| 359 | 13 | `public static void main(String[] args) throws Exception` |  |
| 373 | 191 | `static void run(Path root) throws Exception` |  |
| 572 | 26 | `static void auditFloor()` | Section 8 (0.7.64, batch L; the floor itself since 0.7.63, at 8 steps; fixK-notes 7 d): MoneyAudit.tolerance()'s floor at the size of the figures. |
| 600 | 14 | `static void reads()` | Section 7: Formats at ten billion - the quadrillion step, and cash() past the whole dollars a double holds. |
| 615 | 5 | `static void sameText(String label, String actual, String expected)` |  |
| 692 | 73 | `static void free(Path root, GameFiles small, long pop1, double target, Original original) throws Exception` | Section 6: a city's save (`small`, of `pop1` people) copied to `target` people and played with every sector free to build. |
| 797 | 40 | `static void tracks(Path root, GameFiles grown) throws Exception` | Section 9 (FW9, 2026-10-25): a copy K = TRACK_TIMES times over plays its city's months. |
| 839 | 8 | `static double registersMiss(Game g)` | The worst a register stands from what the households hold of it: the shares of each company, the corporate bonds' face and the city's paper, as a share of the larger. |
| 848 | 3 | `static double apart(double a, double b)` |  |
| 853 | 32 | **type** `static final class Figures` | The figures section 9 reads each month, by the getters a screen reads them by. |
| 862 | 11 | `Figures(Game g)` _(in ScaleCheck.Figures)_ |  |
| 875 | 9 | `double[] missAgainst(Figures city, long k)` _(in ScaleCheck.Figures)_ | Each figure's miss: a quantity's as a share of K times the city's, the price level's as a share of the city's, a rate's in rate points. |
| 887 | 23 | `static boolean sumsAgree(Game g)` | addsUp()'s sums, as a verdict rather than a list of lines: section 6 asks it every month. |
| 926 | 31 | **type** `static final class Original` | THE CITY THE COPIES ARE READ AGAINST (0.7.55): the playtest's town at FREE_FIXTURE_MONTHS, played its own FREE_MONTHS after it was saved - its ground as its first month struck it, its affordability pull each month, th... |
| 931 | 19 | `Original(Game city)` _(in ScaleCheck.Original)_ |  |
| 952 | 4 | `boolean calm()` _(in ScaleCheck.Original)_ | Whether the city's own rents priced nobody out in any of its months (0.7.62; FREE_FIXTURE_MONTHS). |
| 966 | 146 | **type** `static final class Books` | THE CITY'S BOOKS AT TEN BILLION (0.7.54): the identities the harnesses hold a played city to every month, read here at this size, each against the absolute tolerance its harness held it to before 0.7.54 and against Mo... |
| 983 | 3 | `static double roundedToTheCent(double half, double size)` _(in ScaleCheck.Books)_ | A figure the history rounds to the cent (HistorySave's round2()) misses its own by up to `half` and the steps of the double it was rounded in, which past 2^27 units are no longer under the floor's old 1e-9 (0.7.105, b... |
| 993 | 9 | `void miss(String name, double oldTolerance, double newFloor, double miss, double size)` _(in ScaleCheck.Books)_ | One reading of an identity: what it missed by, the size of the figures it is made of, the tolerance its harness held it to before 0.7.54 (oldTolerance; for one already relative, that at this size) and the floor MoneyA... |
| 1003 | 5 | `void month(Game g, double opening)` _(in ScaleCheck.Books)_ |  |
| 1009 | 89 | `void readings(Game g)` _(in ScaleCheck.Books)_ |  |
| 1099 | 12 | `void verdicts()` _(in ScaleCheck.Books)_ |  |
| 1114 | 35 | **type** `static final class Orders implements BusinessInvestment.OrderWatch` | Every order the free copy decides: how big, and how many times it asked the bond desk or read a wait. |
| 1119 | 6 | `public void sized(BuildingsTemplate t, int needed, double siteOutput, int deliverable, int waitsRead)` _(in ScaleCheck.Orders)_ |  |
| 1127 | 6 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in ScaleCheck.Orders)_ |  |
| 1135 | 5 | `public void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRa...` _(in ScaleCheck.Orders)_ |  |
| 1141 | 7 | `void verdicts()` _(in ScaleCheck.Orders)_ |  |
| 1155 | 30 | `static void addsUp(Game g, String when)` | Every way of adding up the posts, the doors and the household places agrees, to the last one - a long sum of K-times counts is exact where an int sum would have wrapped. |
| 1187 | 5 | `static long everyPost(Game g)` | Every post the buildings have, offered or not. |
| 1193 | 3 | `static long last(List<Long> series)` |  |
| 1197 | 5 | `static boolean quietlyGet(java.util.function.BooleanSupplier s)` |  |

### THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON (lines 1203-1374)

| line | len | member | says |
|---:|---:|---|---|
| 1278 | 96 | `static void scale(Path in, Path to, long k) throws Exception` |  |

### THE MONEY AND THE QUANTITIES THE STUDY'S LIST LEFT AT ONE CITY'S SIZE (FW9, 2026-10-25) (lines 1375-1727)

| line | len | member | says |
|---:|---:|---|---|
| 1406 | 123 | `static void registers(JsonObject d, long k)` |  |
| 1537 | 37 | `static void bank(JsonObject d, long k)` | The bank: one bank for the city, so every figure it carries is K times, but for its rates, its months and flags and the per-branch figures (the upkeep a later branch carries and the pricing's months). |
| 1576 | 16 | `static void registerOfShares(JsonObject d, long k)` | The register of shares (Equity.SLOTS a company, in toSaveArray()'s order): the shares and the money, K times; the price, the target share and the counts stay. |
| 1594 | 11 | `static void book(JsonObject b, long k)` | One order book: the orders' quantities and the month's, K times; every price, the last trade's month and the counts stay. |
| 1607 | 12 | `static void exchange(JsonObject d, long k)` | The exchange: every book, the leavers' shares and the buyback budgets, and the record's money and volumes; the fair values and split factors are per share. |
| 1621 | 12 | `static void bonds(JsonObject d, long k)` | The corporate bond market: each bond's issue, face and holders, its books, and the month's and life's money; the coupons, months and ids stay. |
| 1635 | 17 | `static void fund(JsonObject d, long k)` | The city's fund: its cash and what it paid in and drew, its resolutions, orders and ledger; its dial, its months and its limits and prices stay. |
| 1654 | 7 | `static void oldMetal(JsonObject m, long k)` | The old metal's tonnes and the money paid for them; the price and the months stay. |
| 1666 | 61 | `static void people(JsonObject d, long k)` | The people and what is done with them: the care and the burials, the police and the prisoners, the schools' pipeline, the licensed heads and last month's migration. |

### the helpers the blocks above are written with (lines 1728-1839)

| line | len | member | says |
|---:|---:|---|---|
| 1731 | 3 | `static void scalar(JsonObject d, String key, long k)` | One number at a key, K times; a key the save does not have is left. |
| 1736 | 3 | `static void whole(JsonObject d, String key, long k)` | Every number under a key, K times: a number, an array of them or a map of them. |
| 1741 | 12 | `static JsonElement everyTimes(JsonElement v, long k)` | Every number in a value K times (a flag or a name is not a number and stays). |
| 1755 | 5 | `static void slots(JsonObject d, String key, long k, int...at)` | The slots of an array at a key that scale; the others stay. |
| 1762 | 7 | `static void allBut(JsonObject d, String key, long k, int...stay)` | Every slot of an array at a key but the ones named. |
| 1771 | 5 | `static void fromTo(JsonObject d, String key, long k, int first, int end)` | The slots of an array at a key from one to before another. |
| 1778 | 5 | `static void fromSlot(JsonObject d, String key, long k, int first)` | Every slot of an array at a key from one on. |
| 1785 | 13 | `static void named(JsonElement v, long k, String...names)` | Each number under one of the names, wherever an object holds one, K times (a name's array or map of numbers too); the rest of the object stays. |
| 1800 | 4 | `static void expect(JsonArray a, int length, String key)` | A layout read by position has the length it was read at; a slot added to the model must be classed here, so the scaler stops and says so. |
| 1805 | 17 | `static JsonElement sc(JsonElement v, String name, long k)` |  |
| 1824 | 6 | `static JsonElement times(JsonElement v, long k)` | A number times K: a whole number stays whole and exact, a fraction is a double. |
| 1832 | 7 | `static void cleanUp(Path root)` |  |

