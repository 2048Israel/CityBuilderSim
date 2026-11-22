# ScaleCheck.java - 2,285 lines · 68 methods · 31 constants · harnesses

`ham/citybuildersim/ScaleCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

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
>   6. a growing city - this check's own town (ownTown(), ST3), struck at
>      the first month from FREE_FIXTURE_MONTHS that it is growing, has
>      ground free for a home and its rents price no migrant out in the
>      months after (FS2: not a picked month; none within
>      FREE_FIXTURE_SEARCH fails as a fixture) - copied to 5
>      and to 10 billion with its sectors free, so they order: every month
>      whole and adding up, money conserved to RELATIVE_RESIDUAL of what
> ... (36 more lines in the source)

**Uses:** [Game](Game.md) (46), [LongPlaytest](LongPlaytest.md) (38), [GameFiles](GameFiles.md) (29), [MoneyAudit](MoneyAudit.md) (26), [CityLand](CityLand.md) (10), [JobType](JobType.md) (9), [Sector](Sector.md) (7), [TreasuryCheck](TreasuryCheck.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [PriceIndex](PriceIndex.md) (6), [PopulationManager](PopulationManager.md) (5), [Equity](Equity.md) (5), [BuildingManager](BuildingManager.md) (4), [HistorySave](HistorySave.md) (4), [WageLadder](WageLadder.md) (4), [Haulage](Haulage.md) (4), [BusinessInvestment](BusinessInvestment.md) (4), [HouseholdBalance](HouseholdBalance.md) (3), [LandManager](LandManager.md) (3), [Formats](Formats.md) (3), [Bank](Bank.md) (3), [Crime](Crime.md) (3), [EducationType](EducationType.md) (3), [Timing](Timing.md) (2), [TreasuryFund](TreasuryFund.md) (2), [Sectors](Sectors.md) (2), [Debt](Debt.md) (2), [SaveHeader](SaveHeader.md) (2), [Fittings](Fittings.md) (2), [Observation](Observation.md) (2)... and 20 more

**Used by (2):** [BuildingStackCheck](BuildingStackCheck.md), [OrderSearchCheck](OrderSearchCheck.md)

## Sections

| line | section |
|---:|---|
| 372 | · 1. the fixture, and its copy K times over |
| 417 | · 2. the counts read back whole |
| 460 | · 3. posts, doors and household places |
| 470 | · 4. three months at that size |
| 491 | · 5. the save round-trips them |
| 525 | · 6. the sectors free, at 5 and 10 billion |
| 586 | · 7. money at that size reads |
| 589 | · 8. the audit's floor at size |
| 592 | · 9. a copy tracks its city month by month |
| 1589 | THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON |
| 1781 | · THE MONEY AND THE QUANTITIES THE STUDY'S LIST LEFT AT ONE CITY'S SIZE (FW9, 2026-10-25) |
| 2174 | · the helpers the blocks above are written with |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 123 | `ScaleCheck.TARGET_PEOPLE` | `5e9` | Jerus's smaller plan, in people: what the copy is scaled to reach. |
| 126 | `ScaleCheck.MONTHS_AT_SIZE` | `3` | Months the copy plays before it is saved. |
| 129 | `ScaleCheck.FIXTURE_MONTHS` | `120` | The founded city's age, in months, when it is saved and copied. |
| 132 | `ScaleCheck.FREE_PEOPLE` | `{ 5e9, 1e10 }` | Jerus's two plans, in people: the copies whose sectors are free (section 6). |
| 135 | `ScaleCheck.FREE_FIXTURE_MONTHS` | `225` | The least age section 6's town is copied at: QB1's quoted-borrowing town, grown by then to million-building interest decisions; since FS2 the copy is struck at the first month from it with the premises, not at it. |
| 138 | `ScaleCheck.FREE_FIXTURE_SEARCH` | `60` | The most months past FREE_FIXTURE_MONTHS section 6's town plays on for a month that has its premises (FS2; HL1-R's scan found them in 7 of 36). |
| 141 | `ScaleCheck.FREE_MONTHS` | `6` | Months each free copy plays, every one timed. |
| 144 | `ScaleCheck.RELATIVE_RESIDUAL` | `1e-10` | The most the audit's residual may be of what moved, at any size: five orders of magnitude inside MoneyCheck's 1e-4. |
| 154 | `ScaleCheck.MONTH_MEDIAN_MS` | `1500` | The longest a free copy's median month may take, in milliseconds. |
| 157 | `ScaleCheck.TRACK_TIMES` | `1000` | The copy section 9 reads against its city: its factor, K (FW9, 2026-10-25). |
| 169 | `ScaleCheck.TRACK_CITY_TIMES` | `10` | The copies of the grown city section 9's city side is, read against TRACK_TIMES times as many (FS2, 2026-11-20): 10. |
| 172 | `ScaleCheck.TRACK_SITE_MONTHS` | `60` | The most months section 9 plays the grown city, its sectors held, to see its building sites empty before it is copied (BA1, 2026-11-04). |
| 180 | `ScaleCheck.TRACK_PEOPLE` | `.015` | How far the copy's people may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 183 | `ScaleCheck.TRACK_CASH` | `.0125` | How far the copy's treasury cash may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 186 | `ScaleCheck.TRACK_SAVINGS` | `.016` | How far the copy's household savings may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 194 | `ScaleCheck.TRACK_GDP` | `.14` | How far the copy's month GDP may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 197 | `ScaleCheck.TRACK_DEPOSITS` | `.026` | How far the copy's bank deposits may miss K times the city's, as a share of it, in any of FREE_MONTHS. |
| 200 | `ScaleCheck.TRACK_PRICES` | `.004` | How far the copy's price level (PriceIndex.getIndex()) may miss the city's own, as a share of it, in any of FREE_MONTHS. |
| 207 | `ScaleCheck.TRACK_UNEMPLOYMENT` | `.033` | How far the copy's unemployment rate may stand from the city's, in rate points (.01 is a point), in any of FREE_MONTHS. |
| 210 | `ScaleCheck.TRACK_POLICY` | `.0005` | How far the copy's policy rate may stand from the city's, in rate points, in any of FREE_MONTHS. |
| 213 | `ScaleCheck.REGISTER_WITHIN` | `1e-9` | The most a register may stand from what its holders hold of it, as a share of the larger: the shares, the corporate bonds and the city's paper against the households' own (section 9). |
| 246 | `ScaleCheck.OWN_PROPERTY_TAX` | `.04` | The property tax rate, a year, that section 6's town sets in place of the founding's .015 (ST3). |
| 249 | `ScaleCheck.OWN_PRESSURE` | `1.0` | The landlords' family pressure past which section 6's town builds Low-Rise Apartments (ST3). |
| 1179 | `ScaleCheck.Figures.NAMES` | `{ "people", "cash", "savings", "gdp", "deposits", "prices", "unemployment", "...` |  |
| 1181 | `ScaleCheck.Figures.EXTENSIVE` | `{ true, true, true, true, true, false, false, false }` | Whether each figure is a quantity (K times the city's) or a level (the city's own). |
| 1182 | `ScaleCheck.Figures.WITHIN` | `{ TRACK_PEOPLE, TRACK_CASH, TRACK_SAVINGS, TRACK_GDP, TRACK_DEPOSITS, TRACK_P...` |  |
| 1243 | `ScaleCheck.AFFORDABILITY_WITHIN` | `.01` | How far a free copy's affordability pull may stand from the city's in any month (0.7.55): the rents' multiplier on the migrants' target (Migration.affordabilityPull()). |
| 1615 | `ScaleCheck.INTENSIVE` | `Pattern.compile("(price\|Price\|rate\|Rate\|month\|Month\|Months\|share\|Share\|ratio\|...` | A name the scaler leaves alone wherever it meets one, unless EXTENSIVE names it: a price, a rate, a month, an id, a share, a ratio or a target, as scale_save.py's SKIP read them, and since 0.7.54 the seven ratios at i... |
| 1644 | `ScaleCheck.EXTENSIVE` | `java.util.Set.of("recognisedThisMonth", "escalationThisMonth", "repairsThisMo...` | THE NAMES THE PATTERN READ WRONG (0.7.54). |
| 1651 | `ScaleCheck.TOP` | `{ "cash", "householdSavings", "landOwned", "insurancePremiums", "propertyTaxC...` | The save's top-level keys scaled: scale_save.py's list, less the works yard. |
| 1675 | `ScaleCheck.NAMED` | `java.util.Set.of("sectors", "markets", "businessDebts", "sectorBooks", "secto...` | The five TOP keys scale_save.py handed sc() under a name of their own: the sectors, the markets, the business debts and the books, this month's and last's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 118 | `static int fails` |  |
| 119 | `static PrintStream out` |  |
| 120 | `static PrintStream quiet` |  |
| 1184 | `final long people` |  |
| 1185 | `final double[] v` |  |
| 1252 | `final double crowding, premium, ground, sale, homesBuilt` |  |
| 1253 | `final long peopleBefore, peopleAfter` |  |
| 1254 | `final double[] affordability` |  |
| 1298 | `final java.util.Map<String, double[]> worst` | {worst miss, the largest figure, months the old tolerance missed, misses of the new} for each identity, by name. |
| 1299 | `final java.util.Map<String, Double> old` |  |
| 1300 | `final java.util.Set<String> missedThisMonth` |  |
| 1301 | `double previousClosing` |  |
| 1302 | `int arrearsMonths` |  |
| 1449 | `int orders, passed, heldForDownPayment, declinedOnRent` |  |
| 1468 | `int invested, mortgaged, mostDesk` |  |
| 1468 | `long largestInvested, largestMortgage` |  |
| 1469 | `int overDesk` |  |
| 1470 | `final Lender lender` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 116 | 2170 | **type** `public class ScaleCheck` | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them all back. |
| 215 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 220 | 5 | `static void same(String label, long actual, long expected)` |  |
| 226 | 5 | `static void quietly(Runnable r)` |  |
| 240 | 4 | `static Game lineOff(Game g)` | Every game here plays with the owner line off (BO1-S), set at its construction, before a founding is played or a save loaded. |
| 252 | 21 | `static void ownTown(Game g, int months)` | Section 6's town to its month (ST3): founded as the playtest's, then played a month at a time by keepAhead()'s plan. |
| 275 | 16 | `static void playTown(Game g, int months)` | Section 6's town played on by keepAhead()'s plan to its month, a month at a time and quietly (ST3; FS2 split it from ownTown() for the strike). |
| 293 | 12 | `static void keepAhead(Game g)` | One month of the plan (ST3): the ground, then each service, and the apartments, whose own need is on and none on site. |
| 306 | 3 | `static void ahead(Game g, boolean need, String name)` |  |
| 310 | 13 | `public static void main(String[] args) throws Exception` |  |
| 325 | 43 | `static void paperDeskRemainder(Path root) throws Exception` | The desk drains a book through its native spread rule, including its last small holdings. |
| 369 | 227 | `static void run(Path root) throws Exception` |  |
| 598 | 93 | `static void historicalScale(Path root) throws Exception` | R1f: keep this historical fixture separate from the all-fitted scale laws above. |
| 693 | 53 | `static void historicalTracks(Path root, GameFiles saved) throws Exception` | Whole posts have an absolute rounding bound, not today's town's empirical relative tolerances. |
| 754 | 26 | `static void auditFloor()` | Section 8 (0.7.64, batch L; the floor itself since 0.7.63, at 8 steps; fixK-notes 7 d): MoneyAudit.tolerance()'s floor at the size of the figures. |
| 782 | 14 | `static void reads()` | Section 7: Formats at ten billion - the quadrillion step, and cash() past the whole dollars a double holds. |
| 797 | 5 | `static void sameText(String label, String actual, String expected)` |  |
| 874 | 77 | `static void free(Path root, GameFiles small, long pop1, double target, Original original) throws Exception` | Section 6: a city's save (`small`, of `pop1` people) copied to `target` people and played with every sector free to build. |
| 1037 | 50 | `static void tracks(Path root, GameFiles grown) throws Exception` | Section 9 (FW9, 2026-10-25): a copy K = TRACK_TIMES times over plays its city's months. |
| 1094 | 17 | `static Game trackCopy(Path root, GameFiles sites, long k, String name, java.util.function.Consumer<JsonObject> fault) throws Ex...` | A copy section 9 reads (FW9; FS2 made it one method for the city side, its copy and the fault): the sites-empty save through scale() at k, the save then changed by fault where one is given, loaded on the literals with... |
| 1117 | 24 | `static GameFiles emptySites(Path root, GameFiles grown) throws Exception` | The grown city saved again with nothing under construction (BA1, 2026-11-04; tracks() says why): its save is loaded at K = 1, every sector held, and played until its building sites are empty, at most TRACK_SITE_MONTHS... |
| 1153 | 19 | `static double registersMiss(Game g)` | The worst a register stands from what the households hold of it: the shares of each company, the corporate bonds' face and the city's paper, as a share of the larger - a company's shares as a share of its issue. |
| 1173 | 3 | `static double apart(double a, double b)` |  |
| 1178 | 32 | **type** `static final class Figures` | The figures section 9 reads each month, by the getters a screen reads them by. |
| 1187 | 11 | `Figures(Game g)` _(in ScaleCheck.Figures)_ |  |
| 1200 | 9 | `double[] missAgainst(Figures city, long k)` _(in ScaleCheck.Figures)_ | Each figure's miss: a quantity's as a share of K times the city's, the price level's as a share of the city's, a rate's in rate points. |
| 1212 | 23 | `static boolean sumsAgree(Game g)` | addsUp()'s sums, as a verdict rather than a list of lines: section 6 asks it every month. |
| 1251 | 36 | **type** `static final class Original` | THE CITY THE COPIES ARE READ AGAINST (0.7.55): the town at FREE_FIXTURE_MONTHS, played its own FREE_MONTHS after it was saved - its ground as its first month struck it, its affordability pull each month, the homes it ... |
| 1256 | 24 | `Original(Game city)` _(in ScaleCheck.Original)_ |  |
| 1282 | 4 | `boolean calm()` _(in ScaleCheck.Original)_ | Whether the city's own rents priced nobody out in any of its months (0.7.62; FREE_FIXTURE_MONTHS). |
| 1296 | 146 | **type** `static final class Books` | THE CITY'S BOOKS AT TEN BILLION (0.7.54): the identities the harnesses hold a played city to every month, read here at this size, each against the absolute tolerance its harness held it to before 0.7.54 and against Mo... |
| 1313 | 3 | `static double roundedToTheCent(double half, double size)` _(in ScaleCheck.Books)_ | A figure the history rounds to the cent (HistorySave's round2()) misses its own by up to `half` and the steps of the double it was rounded in, which past 2^27 units are no longer under the floor's old 1e-9 (0.7.105, b... |
| 1323 | 9 | `void miss(String name, double oldTolerance, double newFloor, double miss, double size)` _(in ScaleCheck.Books)_ | One reading of an identity: what it missed by, the size of the figures it is made of, the tolerance its harness held it to before 0.7.54 (oldTolerance; for one already relative, that at this size) and the floor MoneyA... |
| 1333 | 5 | `void month(Game g, double opening)` _(in ScaleCheck.Books)_ |  |
| 1339 | 89 | `void readings(Game g)` _(in ScaleCheck.Books)_ |  |
| 1429 | 12 | `void verdicts()` _(in ScaleCheck.Books)_ |  |
| 1448 | 17 | **type** `static final class Lender implements BusinessInvestment.OrderWatch` | The landlords' orders as the mortgage lender meets them (SC1): how many were decided, how many the lender passed, how many it held for the down payment and how many it declined on the rent. |
| 1452 | 7 | `public void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash, double noiPerUnit, double annual...` _(in ScaleCheck.Lender)_ |  |
| 1460 | 4 | `void verdicts(String whose)` _(in ScaleCheck.Lender)_ |  |
| 1467 | 32 | **type** `static final class Orders implements BusinessInvestment.OrderWatch` | Every order the free copy decides: how big, and how many times it asked the bond desk (the order's size reads no wait since BA1). |
| 1473 | 1 | `long largestNeeded()` _(in ScaleCheck.Orders)_ | The largest order decided, whichever way it is judged: what a sector's shortfall asked for and the plots allowed (BusinessInvestment.orderSize()), the size the old "needed" counted. |
| 1476 | 6 | `public void invested(BusinessInvestment.Decision d, double cash, double perUnitProfit, Game.Afford found)` _(in ScaleCheck.Orders)_ |  |
| 1484 | 6 | `public void mortgaged(long asked, java.util.function.LongToDoubleFunction costOf, double cash, double noiPerUnit, double annual...` _(in ScaleCheck.Orders)_ |  |
| 1491 | 7 | `void verdicts()` _(in ScaleCheck.Orders)_ |  |
| 1507 | 22 | `static void orderSizeAtScale(Game g)` | ORDER SIZE AT SCALE (BA1). |
| 1535 | 30 | `static void addsUp(Game g, String when)` | Every way of adding up the posts, the doors and the household places agrees, to the last one - a long sum of K-times counts is exact where an int sum would have wrapped. |
| 1567 | 5 | `static long everyPost(Game g)` | Every post the buildings have, offered or not. |
| 1573 | 5 | `static long postsPast(BuildingManager bm)` |  |
| 1579 | 3 | `static long last(List<Long> series)` |  |
| 1583 | 5 | `static boolean quietlyGet(java.util.function.BooleanSupplier s)` |  |

### THE STUDY'S SCALING (scale_save.py, 2026-10-06), on a save's JSON (lines 1589-1780)

| line | len | member | says |
|---:|---:|---|---|
| 1678 | 102 | `static void scale(Path in, Path to, long k) throws Exception` |  |

### THE MONEY AND THE QUANTITIES THE STUDY'S LIST LEFT AT ONE CITY'S SIZE (FW9, 2026-10-25) (lines 1781-2173)

| line | len | member | says |
|---:|---:|---|---|
| 1812 | 126 | `static void registers(JsonObject d, long k)` |  |
| 1946 | 37 | `static void bank(JsonObject d, long k)` | The bank: one bank for the city, so every figure it carries is K times, but for its rates, its months and flags and the per-branch figures (the upkeep a later branch carries and the pricing's months). |
| 1985 | 17 | `static void registerOfShares(JsonObject d, long k)` | The register of shares (Equity.SLOTS a company, in toSaveArray()'s order): the shares and the money, K times; the price, the target share and the counts stay. |
| 2004 | 11 | `static void book(JsonObject b, long k)` | One order book: the orders' quantities and the month's, K times; every price, the last trade's month and the counts stay. |
| 2017 | 20 | `static void exchange(JsonObject d, long k)` | The exchange: every book, the leavers' shares and the buyback budgets, the money fair value was struck on (EM1), and the record's money and volumes; the fair values and split factors are per share. |
| 2039 | 14 | `static void bonds(JsonObject d, long k)` | The corporate bond market: each bond's issue, face and holders, its books, and the month's and life's money; the coupons, months and ids stay. |
| 2055 | 13 | `static void fund(JsonObject d, long k)` | The city's fund: its cash and what it paid in and drew, its resolutions, orders and ledger; its dial, its months and its limits and prices stay. |
| 2070 | 6 | `static void ledger(JsonElement value, long k)` | The same average-cost ledger on either owner: book amounts and acquired units grow; months, IDs and prices stay. |
| 2078 | 20 | `static void investmentTaxesAndCustody(JsonObject d, long k)` | HT1's aggregate stocks/receipts grow with the city; per-household cell basis/claims stay on the cells above. |
| 2100 | 7 | `static void oldMetal(JsonObject m, long k)` | The old metal's tonnes and the money paid for them; the price and the months stay. |
| 2112 | 61 | `static void people(JsonObject d, long k)` | The people and what is done with them: the care and the burials, the police and the prisoners, the schools' pipeline, the licensed heads and last month's migration. |

### the helpers the blocks above are written with (lines 2174-2285)

| line | len | member | says |
|---:|---:|---|---|
| 2177 | 3 | `static void scalar(JsonObject d, String key, long k)` | One number at a key, K times; a key the save does not have is left. |
| 2182 | 3 | `static void whole(JsonObject d, String key, long k)` | Every number under a key, K times: a number, an array of them or a map of them. |
| 2187 | 12 | `static JsonElement everyTimes(JsonElement v, long k)` | Every number in a value K times (a flag or a name is not a number and stays). |
| 2201 | 5 | `static void slots(JsonObject d, String key, long k, int...at)` | The slots of an array at a key that scale; the others stay. |
| 2208 | 7 | `static void allBut(JsonObject d, String key, long k, int...stay)` | Every slot of an array at a key but the ones named. |
| 2217 | 5 | `static void fromTo(JsonObject d, String key, long k, int first, int end)` | The slots of an array at a key from one to before another. |
| 2224 | 5 | `static void fromSlot(JsonObject d, String key, long k, int first)` | Every slot of an array at a key from one on. |
| 2231 | 13 | `static void named(JsonElement v, long k, String...names)` | Each number under one of the names, wherever an object holds one, K times (a name's array or map of numbers too); the rest of the object stays. |
| 2246 | 4 | `static void expect(JsonArray a, int length, String key)` | A layout read by position has the length it was read at; a slot added to the model must be classed here, so the scaler stops and says so. |
| 2251 | 17 | `static JsonElement sc(JsonElement v, String name, long k)` |  |
| 2270 | 6 | `static JsonElement times(JsonElement v, long k)` | A number times K: a whole number stays whole and exact, a fraction is a double. |
| 2278 | 7 | `static void cleanUp(Path root)` |  |

