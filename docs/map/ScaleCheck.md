# ScaleCheck.java - 1,938 lines · 59 methods · 29 constants · harnesses

`ham/citybuildersim/ScaleCheck.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

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
>   6. a growing city - this check's own town (ownTown(), ST3) at
>      FREE_FIXTURE_MONTHS, with ground free for a home - copied to 5
>      and to 10 billion with its sectors free, so they order: every month
>      whole and adding up, money conserved to RELATIVE_RESIDUAL of what
>      moved from the first month, the city's books agreeing within
>      MoneyAudit.tolerance() where a harness's absolute cent would not, no
>      order asking the bond desk more often than its search allows, an order
> ... (26 more lines in the source)

**Uses:** [Game](Game.md) (30), [MoneyAudit](MoneyAudit.md) (26), [LongPlaytest](LongPlaytest.md) (23), [GameFiles](GameFiles.md) (17), [CityLand](CityLand.md) (10), [Observation](Observation.md) (7), [TreasuryCheck](TreasuryCheck.md) (7), [Sector](Sector.md) (6), [JobType](JobType.md) (6), [PriceIndex](PriceIndex.md) (6), [PopulationManager](PopulationManager.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (5), [HistorySave](HistorySave.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [BuildingManager](BuildingManager.md) (3), [LandManager](LandManager.md) (3), [Formats](Formats.md) (3), [Equity](Equity.md) (3), [Bank](Bank.md) (3), [Crime](Crime.md) (3), [EducationType](EducationType.md) (3), [Timing](Timing.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (2), [SaveHeader](SaveHeader.md) (2), [BuildingType](BuildingType.md) (2), [Mortgage](Mortgage.md) (2), [Health](Health.md) (1), [TempDirs](TempDirs.md) (1), [LandDrawing](LandDrawing.md) (1)... and 13 more

**Used by (1):** [OrderSearchCheck](OrderSearchCheck.md)

## Sections

| line | section |
|---:|---|
| 275 | · 1. the fixture, and its copy K times over |
| 320 | · 2. the counts read back whole |
| 363 | · 3. posts, doors and household places |
| 373 | · 4. three months at that size |
| 394 | · 5. the save round-trips them |
| 428 | · 6. the sectors free, at 5 and 10 billion |
| 468 | · 7. money at that size reads |
| 471 | · 8. the audit's floor at size |
| 474 | · 9. a copy tracks its city month by month |
| 1280 | THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON |
| 1465 | · THE MONEY AND THE QUANTITIES THE STUDY'S LIST LEFT AT ONE CITY'S SIZE (FW9, 2026-10-25) |
| 1827 | · the helpers the blocks above are written with |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 113 | `ScaleCheck.TARGET_PEOPLE` | `5e9` | Jerus's smaller plan, in people: what the copy is scaled to reach. |
| 116 | `ScaleCheck.MONTHS_AT_SIZE` | `3` | Months the copy plays before it is saved. |
| 119 | `ScaleCheck.FIXTURE_MONTHS` | `120` | The founded city's age, in months, when it is saved and copied. |
| 122 | `ScaleCheck.FREE_PEOPLE` | `{ 5e9, 1e10 }` | Jerus's two plans, in people: the copies whose sectors are free (section 6). |
| 125 | `ScaleCheck.FREE_FIXTURE_MONTHS` | `172` | The month ownTown() is copied at; its growth, ground, rents and orders are observed (PO1). |
| 128 | `ScaleCheck.FREE_MONTHS` | `6` | Months each free copy plays, every one timed. |
| 131 | `ScaleCheck.RELATIVE_RESIDUAL` | `1e-10` | The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. |
| 141 | `ScaleCheck.MONTH_MEDIAN_MS` | `1500` | The longest a free copy's median month may take, in milliseconds. |
| 144 | `ScaleCheck.TRACK_TIMES` | `1000` | The copy section 9 reads against its city: its factor, K (FW9, 2026-10-25). |
| 147 | `ScaleCheck.TRACK_SITE_MONTHS` | `60` | The most months section 9 plays the grown city, its sectors held, to see its building sites empty before it is copied (BA1, 2026-11-04). |
| 155 | `ScaleCheck.TRACK_PEOPLE` | `.015` | How far the copy's people may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 158 | `ScaleCheck.TRACK_CASH` | `.0125` | How far the copy's treasury cash may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 161 | `ScaleCheck.TRACK_SAVINGS` | `.016` | How far the copy's household savings may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 169 | `ScaleCheck.TRACK_GDP` | `.14` | How far the copy's month GDP may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 172 | `ScaleCheck.TRACK_DEPOSITS` | `.026` | How far the copy's bank deposits may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 175 | `ScaleCheck.TRACK_PRICES` | `.004` | How far the copy's price level (PriceIndex.getIndex()) may miss the city's own, as a share of it, in any of FREE_MONTHS. |
| 182 | `ScaleCheck.TRACK_UNEMPLOYMENT` | `.033` | How far the copy's unemployment rate may stand from the city's, in rate points (.01 is a point), in any of FREE_MONTHS. |
| 185 | `ScaleCheck.TRACK_POLICY` | `.0005` | How far the copy's policy rate may stand from the city's, in rate points, in any of FREE_MONTHS. |
| 188 | `ScaleCheck.REGISTER_WITHIN` | `1e-9` | The most a register may stand from what its holders hold of it, as a share of the larger: the shares, the corporate bonds and the city's paper against the households' own (section 9). |
| 208 | `ScaleCheck.OWN_PROPERTY_TAX` | `.04` | The property tax rate, a year, that section 6's town sets in place of the founding's .015 (ST3). |
| 211 | `ScaleCheck.OWN_PRESSURE` | `1.0` | The landlords' family pressure past which section 6's town builds Low-Rise Apartments (ST3). |
| 875 | `ScaleCheck.Figures.NAMES` | `{ "people", "cash", "savings", "gdp", "deposits", "prices", "unemployment", "...` |  |
| 877 | `ScaleCheck.Figures.EXTENSIVE` | `{ true, true, true, true, true, false, false, false }` | Whether each figure is a quantity (K times the city's) or a level (the city's own). |
| 878 | `ScaleCheck.Figures.WITHIN` | `{ TRACK_PEOPLE, TRACK_CASH, TRACK_SAVINGS, TRACK_GDP, TRACK_DEPOSITS, TRACK_P...` |  |
| 939 | `ScaleCheck.AFFORDABILITY_WITHIN` | `.01` | How far a free copy's affordability pull may stand from the city's in any month (0.7.55): the rents' multiplier on the migrants' target (Migration.affordabilityPull()). |
| 1306 | `ScaleCheck.INTENSIVE` | `Pattern.compile("(price\|Price\|rate\|Rate\|month\|Month\|Months\|share\|Share\|ratio\|...` | A name the scaler leaves alone wherever it meets one, unless EXTENSIVE names it: a price, a rate, a month, an id, a share, a ratio or a target, as scale_save.py's SKIP read them, and since 0.7.54 the seven ratios at i... |
| 1335 | `ScaleCheck.EXTENSIVE` | `java.util.Set.of("recognisedThisMonth", "escalationThisMonth", "repairsThisMo...` | THE NAMES THE PATTERN READ WRONG (0.7.54). |
| 1342 | `ScaleCheck.TOP` | `{ "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxC...` | The save's top-level keys scaled: scale_save.py's list, less the works yard. |
| 1359 | `ScaleCheck.NAMED` | `java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks", "secto...` | The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 108 | `static int fails` |  |
| 109 | `static PrintStream out` |  |
| 110 | `static PrintStream quiet` |  |
| 880 | `final long people` |  |
| 881 | `final double[] v` |  |
| 948 | `final double crowding, premium, ground, sale, homesBuilt` |  |
| 949 | `final long peopleBefore, peopleAfter` |  |
| 950 | `final double[] affordability` |  |
| 994 | `final java.util.Map<String, double[]> worst` | {worst miss, the largest figure, months the old tolerance missed, misses of the new} for each identity, by name. |
| 995 | `final java.util.Map<String, Double> old` |  |
| 996 | `final java.util.Set<String> missedThisMonth` |  |
| 997 | `double previousClosing` |  |
| 998 | `int arrearsMonths` |  |
| 1145 | `int orders, passed, heldForDownPayment, declinedOnRent` |  |
| 1164 | `int invested, mortgaged, largestInvested, largestMortgage, mostDesk` |  |
| 1165 | `int overDesk` |  |
| 1166 | `final Lender lender` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 106 | 1833 | **type** `public class ScaleCheck` | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them all back. |
| 190 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 195 | 5 | `static void same(String label, long actual, long expected)` |  |
| 201 | 5 | `static void quietly(Runnable r)` |  |
| 214 | 26 | `static void ownTown(Game g, int months)` | Section 6's town to its month (ST3): founded as the playtest's, then played a month at a time by keepAhead()'s plan. |
| 242 | 12 | `static void keepAhead(Game g)` | One month of the plan (ST3): the ground, then each service, and the apartments, whose own need is on and none on site. |
| 255 | 3 | `static void ahead(Game g, boolean need, String name)` |  |
| 259 | 13 | `public static void main(String[] args) throws Exception` |  |
| 273 | 204 | `static void run(Path root) throws Exception` |  |
| 485 | 26 | `static void auditFloor()` | Section 8 (0.7.64, batch L; the floor itself since 0.7.63, at 8 steps; fixK-notes 7 d): MoneyAudit.tolerance()'s floor at the size of the figures. |
| 513 | 14 | `static void reads()` | Section 7: Formats at ten billion - the quadrillion step, and cash() past the whole dollars a double holds. |
| 528 | 5 | `static void sameText(String label, String actual, String expected)` |  |
| 605 | 77 | `static void free(Path root, GameFiles small, long pop1, double target, Original original) throws Exception` | Section 6: a city's save (`small`, of `pop1` people) copied to `target` people and played with every sector free to build. |
| 766 | 48 | `static void tracks(Path root, GameFiles grown) throws Exception` | Section 9 (FW9, 2026-10-25): a copy K = TRACK_TIMES times over plays its city's months. |
| 820 | 26 | `static GameFiles emptySites(Path root, GameFiles grown) throws Exception` | The grown city saved again with nothing under construction (BA1, 2026-11-04; tracks() says why): its save is loaded at K = 1, every sector held, and played until its building sites are empty, at most TRACK_SITE_MONTHS... |
| 858 | 10 | `static double registersMiss(Game g)` | The worst a register stands from what the households hold of it: the shares of each company, the corporate bonds' face and the city's paper, as a share of the larger - a company's shares as a share of its issue. |
| 869 | 3 | `static double apart(double a, double b)` |  |
| 874 | 32 | **type** `static final class Figures` | The figures section 9 reads each month, by the getters a screen reads them by. |
| 883 | 11 | `Figures(Game g)` _(in ScaleCheck.Figures)_ |  |
| 896 | 9 | `double[] missAgainst(Figures city, long k)` _(in ScaleCheck.Figures)_ | Each figure's miss: a quantity's as a share of K times the city's, the price level's as a share of the city's, a rate's in rate points. |
| 908 | 23 | `static boolean sumsAgree(Game g)` | addsUp()'s sums, as a verdict rather than a list of lines: section 6 asks it every month. |
| 947 | 36 | **type** `static final class Original` | THE CITY THE COPIES ARE READ AGAINST (0.7.55): the town at FREE_FIXTURE_MONTHS, played its own FREE_MONTHS after it was saved - its ground as its first month struck it, its affordability pull each month, the homes it ... |
| 952 | 24 | `Original(Game city)` _(in ScaleCheck.Original)_ |  |
| 978 | 4 | `boolean calm()` _(in ScaleCheck.Original)_ | Whether the city's own rents priced nobody out in any of its months (0.7.62; FREE_FIXTURE_MONTHS). |
| 992 | 146 | **type** `static final class Books` | THE CITY'S BOOKS AT TEN BILLION (0.7.54): the identities the harnesses hold a played city to every month, read here at this size, each against the absolute tolerance its harness held it to before 0.7.54 and against Mo... |
| 1009 | 3 | `static double roundedToTheCent(double half, double size)` _(in ScaleCheck.Books)_ | A figure the history rounds to the cent (HistorySave's round2()) misses its own by up to `half` and the steps of the double it was rounded in, which past 2^27 units are no longer under the floor's old 1e-9 (0.7.105, b... |
| 1019 | 9 | `void miss(String name, double oldTolerance, double newFloor, double miss, double size)` _(in ScaleCheck.Books)_ | One reading of an identity: what it missed by, the size of the figures it is made of, the tolerance its harness held it to before 0.7.54 (oldTolerance; for one already relative, that at this size) and the floor MoneyA... |
| 1029 | 5 | `void month(Game g, double opening)` _(in ScaleCheck.Books)_ |  |
| 1035 | 89 | `void readings(Game g)` _(in ScaleCheck.Books)_ |  |
| 1125 | 12 | `void verdicts()` _(in ScaleCheck.Books)_ |  |
| 1144 | 17 | **type** `static final class Lender implements BusinessInvestment.OrderWatch` | The landlords' orders as the mortgage lender meets them (SC1): how many were decided, how many the lender passed, how many it held for the down payment and how many it declined on the rent. |
| 1148 | 7 | `public void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRa...` _(in ScaleCheck.Lender)_ |  |
| 1156 | 4 | `void verdicts(String whose)` _(in ScaleCheck.Lender)_ |  |
| 1163 | 32 | **type** `static final class Orders implements BusinessInvestment.OrderWatch` | Every order the free copy decides: how big, and how many times it asked the bond desk (the order's size reads no wait since BA1). |
| 1169 | 1 | `int largestNeeded()` _(in ScaleCheck.Orders)_ | The largest order decided, whichever way it is judged: what a sector's shortfall asked for and the plots allowed (BusinessInvestment.orderSize()), the size the old "needed" counted. |
| 1172 | 6 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in ScaleCheck.Orders)_ |  |
| 1180 | 6 | `public void mortgaged(int asked, java.util.function.IntToDoubleFunction costOf, double cash, double noiPerUnit, double annualRa...` _(in ScaleCheck.Orders)_ |  |
| 1187 | 7 | `void verdicts()` _(in ScaleCheck.Orders)_ |  |
| 1203 | 23 | `static void orderSizeAtScale(Game g)` | ORDER SIZE AT SCALE (BA1). |
| 1232 | 30 | `static void addsUp(Game g, String when)` | Every way of adding up the posts, the doors and the household places agrees, to the last one - a long sum of K-times counts is exact where an int sum would have wrapped. |
| 1264 | 5 | `static long everyPost(Game g)` | Every post the buildings have, offered or not. |
| 1270 | 3 | `static long last(List<Long> series)` |  |
| 1274 | 5 | `static boolean quietlyGet(java.util.function.BooleanSupplier s)` |  |

### THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON (lines 1280-1464)

| line | len | member | says |
|---:|---:|---|---|
| 1362 | 102 | `static void scale(Path in, Path to, long k) throws Exception` |  |

### THE MONEY AND THE QUANTITIES THE STUDY'S LIST LEFT AT ONE CITY'S SIZE (FW9, 2026-10-25) (lines 1465-1826)

| line | len | member | says |
|---:|---:|---|---|
| 1496 | 125 | `static void registers(JsonObject d, long k)` |  |
| 1629 | 37 | `static void bank(JsonObject d, long k)` | The bank: one bank for the city, so every figure it carries is K times, but for its rates, its months and flags and the per-branch figures (the upkeep a later branch carries and the pricing's months). |
| 1668 | 16 | `static void registerOfShares(JsonObject d, long k)` | The register of shares (Equity.SLOTS a company, in toSaveArray()'s order): the shares and the money, K times; the price, the target share and the counts stay. |
| 1686 | 11 | `static void book(JsonObject b, long k)` | One order book: the orders' quantities and the month's, K times; every price, the last trade's month and the counts stay. |
| 1699 | 19 | `static void exchange(JsonObject d, long k)` | The exchange: every book, the leavers' shares and the buyback budgets, the money fair value was struck on (EM1), and the record's money and volumes; the fair values and split factors are per share. |
| 1720 | 12 | `static void bonds(JsonObject d, long k)` | The corporate bond market: each bond's issue, face and holders, its books, and the month's and life's money; the coupons, months and ids stay. |
| 1734 | 17 | `static void fund(JsonObject d, long k)` | The city's fund: its cash and what it paid in and drew, its resolutions, orders and ledger; its dial, its months and its limits and prices stay. |
| 1753 | 7 | `static void oldMetal(JsonObject m, long k)` | The old metal's tonnes and the money paid for them; the price and the months stay. |
| 1765 | 61 | `static void people(JsonObject d, long k)` | The people and what is done with them: the care and the burials, the police and the prisoners, the schools' pipeline, the licensed heads and last month's migration. |

### the helpers the blocks above are written with (lines 1827-1938)

| line | len | member | says |
|---:|---:|---|---|
| 1830 | 3 | `static void scalar(JsonObject d, String key, long k)` | One number at a key, K times; a key the save does not have is left. |
| 1835 | 3 | `static void whole(JsonObject d, String key, long k)` | Every number under a key, K times: a number, an array of them or a map of them. |
| 1840 | 12 | `static JsonElement everyTimes(JsonElement v, long k)` | Every number in a value K times (a flag or a name is not a number and stays). |
| 1854 | 5 | `static void slots(JsonObject d, String key, long k, int...at)` | The slots of an array at a key that scale; the others stay. |
| 1861 | 7 | `static void allBut(JsonObject d, String key, long k, int...stay)` | Every slot of an array at a key but the ones named. |
| 1870 | 5 | `static void fromTo(JsonObject d, String key, long k, int first, int end)` | The slots of an array at a key from one to before another. |
| 1877 | 5 | `static void fromSlot(JsonObject d, String key, long k, int first)` | Every slot of an array at a key from one on. |
| 1884 | 13 | `static void named(JsonElement v, long k, String...names)` | Each number under one of the names, wherever an object holds one, K times (a name's array or map of numbers too); the rest of the object stays. |
| 1899 | 4 | `static void expect(JsonArray a, int length, String key)` | A layout read by position has the length it was read at; a slot added to the model must be classed here, so the scaler stops and says so. |
| 1904 | 17 | `static JsonElement sc(JsonElement v, String name, long k)` |  |
| 1923 | 6 | `static JsonElement times(JsonElement v, long k)` | A number times K: a whole number stays whole and exact, a fraction is a double. |
| 1931 | 7 | `static void cleanUp(Path root)` |  |

