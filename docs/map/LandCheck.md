# LandCheck.java - 3,023 lines · 43 methods · 3 constants · harnesses

`ham/citybuildersim/LandCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Verifies the land ledger: what the city owns, what it can allocate, what it
> charges, and that the three numbers never drift apart.
> 
> The one thing worth being paranoid about here is that allocated land can only
> ever go up by exactly what was built. A leak in either direction is invisible
> for a hundred months and then the city is either mysteriously full or
> mysteriously infinite.
> 
> Sections 1-11 are the ledger and the market. The land office has its own
> at the end: land priced in dollars and the two ways to pay for it (0.7.6,
> sections 12-13); and, since 0.7.13, the office in square kilometres (14),
> the funding page a city short of the price is offered, converting and
> from the vault, with the window abroad open and shut (15), and the next N
> plots bought at once ending exactly as N bought one by one (16); and,
> since 0.7.26, the three figures the redrawn office takes from the model:
> the going rate, which it used to work out itself, the GROUND row's
> verdict and the receipt in the screens' money (17); and, since 0.7.55,
> the ground priced by how crowded the city is rather than how big, at the
> world's price level and not the city's (18); and, since 0.7.57, the land
> on the world (spec-land.md, batch J1b): offers priced from batch I's ground
> and holding the world's fields (5 to 5f) - forty, ten a side, each the next
> band of its lane, until 0.7.66; since 0.7.67 (batch M3, spec-grid.md) six
> places a side, each a rectangle of whole blocks against the city or none
> while its side has no room, listed once there is, the city's level setting
> the blocks and the books its plots counted - the world's totals kept to the tonne, the ground worked
> out in the order it was bought, ground set by hand, forest's regrowth and
> the best offer for each need (19), a saved city's land field for field
> (20), and an older save's land converted - the three research cities' (21);
> and the playtest's player keeping its ground ahead, as the build advice
> keeps slack (22, batch J1d; since 0.7.67, batch M3b, its room to grow
> weighed from the same line). From 0.7.58 to 0.7.63 (batch J1c) a field was
> shared site by site among the ground its sites lie under; since 0.7.64
> (batch L) a field goes whole again to the one piece of ground holding its
> centre, an offer priced by its fields' tonnes, a new city's iron a
> significant investment the funding page sizes a bond to (5e, 19); and the
> playtest's player buys its iron a whole field at a time, the cheapest
> standing, with cash or that bond - since 0.7.67 (batch M3b) only once the
> mines it would carry pay it back (23); and, since 0.7.68 (batch M4), the
> units the player reads land in - square metres under a hundredth of a
> square kilometre, square kilometres from it, ground prices a square
> metre (24); and, since 0.7.107 (batch Z1), the ledger with zones and the
> forest law - a sector's allocation on its zone's ground first, the three
> numbers still closing, forest dry ground a building can take until a law
> keeps it (25; ZoningCheck plays the rest); and, since 0.7.110 (batch I1),
> the ground by type - sites and tonnes by type adding up to the whole, E
> by type the walk's reading of E until a works digs by type, a typed dig
> moving its own type's E and nothing else, W by type to the tonne, the
> state through a save, and where a works fits (fits()), pure (26); and,
> since 0.7.138 (batch I9), where Forestry's cut and the clearing meet - the
> clearing taking the crews' cut-over ground first, the salvage the standing
> wood alone and taken once, cleared ground never grown back by the planted
> stands, W exact (27); and, since ET1, the books by relief - a holding's,
> an offer's and the centre's dry ground split flat, foothill, steep and very
> steep, and its shallow sea, counted in the same pass as the five areas and
> adding up to them plot for plot, all flat on a generated world, on Perth's
> window of the Earth's fixture and, where the file is named, on Medellin and
> Halifax (28).

**Uses:** [LandManager](LandManager.md) (196), [CityLand](CityLand.md) (125), [World](World.md) (88), [Game](Game.md) (83), [Resource](Resource.md) (78), [LandParcel](LandParcel.md) (77), [LandMarket](LandMarket.md) (43), [LongPlaytest](LongPlaytest.md) (35), [Deposit](Deposit.md) (26), [LandGrid](LandGrid.md) (14), [MiningCheck](MiningCheck.md) (12), [Sectors](Sectors.md) (12), [CityNeeds](CityNeeds.md) (10), [GameFiles](GameFiles.md) (8), [Mining](Mining.md) (8), [ForeignAccounts](ForeignAccounts.md) (7), [BuildingsTemplate](BuildingsTemplate.md) (6), [LandMap](LandMap.md) (6), [LegacyLand](LegacyLand.md) (5), [Founding](Founding.md) (5), [EarthData](EarthData.md) (5), [MoneyAudit](MoneyAudit.md) (4), [DebtQuote](DebtQuote.md) (4), [EarthCheck](EarthCheck.md) (4), [BuildingManager](BuildingManager.md) (3), [LandConversion](LandConversion.md) (3), [Formats](Formats.md) (3), [GridConversion](GridConversion.md) (3), [BuildAdvice](BuildAdvice.md) (3), [Good](Good.md) (2)... and 8 more

**Used by (5):** [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [MaterialsCheck](MaterialsCheck.md), [QuarryingCheck](QuarryingCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 95 | · 1. what the city starts with |
| 170 | · 2. allocating |
| 192 | · 3. filling up |
| 204 | · 4. releasing |
| 213 | · 5. the listing |
| 302 | · 5b. buying one |
| 370 | · 5c. a more crowded city pays more |
| 398 | · 5d. supply and demand inside the city |
| 474 | · 5e. iron in the ground |
| 550 | · 5f. the level rule |
| 648 | · 6. not affording it |
| 666 | · 7. selling |
| 690 | · 8. the player's price |
| 710 | · 9. reset |
| 725 | · 10. every building fits on a starting city |
| 786 | · 11. the price is a density policy |
| 839 | 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) |
| 962 | 12. LAND IS PRICED IN DOLLARS (0.7.6) |
| 1022 | 13. THE TWO WAYS TO PAY (0.7.6) |
| 1220 | 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) |
| 1250 | 24. THE UNITS THE PLAYER READS (0.7.68, batch M4) |
| 1313 | 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) |
| 1442 | 16. THE NEXT N PLOTS, AT ONCE (0.7.13) |
| 1562 | 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) |
| 1661 | · the grid's own tests (0.7.67) |
| 1709 | 19. THE LAND ON THE WORLD (0.7.57) |
| 2074 | 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) |
| 2138 | 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) |
| 2269 | 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) |
| 2352 | 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) |
| 2501 | 25. THE LEDGER WITH ZONES AND THE FOREST LAW (0.7.107, batch Z1) |
| 2547 | 26. THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) |
| 2779 | 27. WHERE THE CUT AND THE CLEARING MEET (0.7.138, batch I9; |
| 2824 | 28. THE BOOKS BY RELIEF (ET1, spec-earth2-terrain 3.3) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 1720 | `LandCheck.IRON_IN_THE_GROUND` | `.0004` | Iron ore in the ground, a tonne in thousands: US$0.40, the land office's charge since 0.7.57, kept when ore fell to its world price (Jerus's D, 2026-10-08; 0.7.120). |
| 2165 | `LandCheck.RESEARCH` | `{ new Research("city600 m612", 906013741141069877L, 32_635_000, 10, 36200450....` | The three research saves' land (the autosaves at months 612, 2412 and 1851), and a city whose mines outnumber its sites. |
| 2267 | `LandCheck.DEAL` | `7` | Read by nothing since 0.7.67: the width of the bands section 19 tiled the plane with until then, 7 plots, which no offer was - the pieces are the holdings and the offers standing now. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 2960 | **type** `public class LandCheck` | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. |
| 68 | 6 | `static void check(String label, double actual, double expected)` |  |
| 75 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 81 | 5 | `static void near(String label, double actual, double expected, double relative)` | ...and to a part in `relative` of the expected figure (0.7.55): prices a ten-thousandth of a unit, where check()'s 1e-6 is no test. |
| 87 | 5 | `static void quietly(Runnable work)` |  |
| 93 | 745 | `public static void main(String[] args) throws Exception` |  |

### 18. CROWDED, NOT BIG, AND THE DOLLAR'S OWN INFLATION (0.7.55) (lines 839-961)

| line | len | member | says |
|---:|---:|---|---|
| 855 | 80 | `static void crowdedNotBig()` |  |
| 942 | 19 | `static LandManager timesOver(LandManager town, long k)` | A land office holding a city's land k times over, by its records (0.7.67): the town's blocks, holdings and offers, each holding's five areas and its forest's timber times k, and the figure times k - a copy K times ove... |

### 12. LAND IS PRICED IN DOLLARS (0.7.6) (lines 962-1021)

| line | len | member | says |
|---:|---:|---|---|
| 972 | 49 | `static void inDollars()` |  |

### 13. THE TWO WAYS TO PAY (0.7.6) (lines 1022-1219)

| line | len | member | says |
|---:|---:|---|---|
| 1033 | 6 | `static Game dollarCity(String label)` |  |
| 1040 | 5 | `static double[] moved(double[] before, double[] after)` |  |
| 1046 | 173 | `static void bothWays() throws Exception` |  |

### 14. THE OFFICE IN SQUARE KILOMETRES (0.7.13) (lines 1220-1249)

| line | len | member | says |
|---:|---:|---|---|
| 1228 | 21 | `static void inSquareKilometres()` |  |

### 24. THE UNITS THE PLAYER READS (0.7.68, batch M4) (lines 1250-1312)

| line | len | member | says |
|---:|---:|---|---|
| 1261 | 43 | `static void inThePlayersUnits()` |  |
| 1306 | 6 | `static boolean readsAs(String words, double squareMetres)` | Whether words like "7,430 m2" or "0.0301 km2" read `squareMetres` to half a percent, never as nothing. |

### 15. SHORT OF THE PRICE: THE FUNDING PAGE (0.7.13) (lines 1313-1441)

| line | len | member | says |
|---:|---:|---|---|
| 1328 | 113 | `static void whenShort() throws Exception` |  |

### 16. THE NEXT N PLOTS, AT ONCE (0.7.13) (lines 1442-1561)

| line | len | member | says |
|---:|---:|---|---|
| 1457 | 104 | `static void severalAtOnce() throws Exception` |  |

### 17. THE OFFICE'S OWN FIGURES, IN THE MODEL (0.7.26) (lines 1562-1660)

| line | len | member | says |
|---:|---:|---|---|
| 1577 | 71 | `static void theOfficesFigures()` |  |
| 1650 | 10 | `static java.util.List<LandParcel> handMade(double[] perSqFt, double[] sizes, int n)` | A listing of `n` offers by hand: dry ground of each size at its dollars a square foot, ids from 1, each in its own place on no ground of the grid, no ore. |

### the grid's own tests (0.7.67) (lines 1661-1708)

| line | len | member | says |
|---:|---:|---|---|
| 1664 | 3 | `static boolean touchesOwned(LandGrid g, LandParcel p)` | Whether an offer's rectangle holds or borders ground the city owns (GridCheck.touches()). |
| 1669 | 17 | `static double[] recountOn(CityLand land, Resource kind, java.util.function.BiPredicate<Long, Long> on)` | Every field of a resource whose centre plot passes `on`, within the nine cells round a city's site and its owned box's: its sites and amount, whole - read off the world's cells. |
| 1688 | 3 | `static double[] offerFields(CityLand land, LandParcel offer, Resource kind)` | An offer's fields of one resource, recounted from the world: every field whose centre plot is one of its rectangle's free plots, whole (spec-land star 12 on the grid). |
| 1693 | 15 | `static double[] freePlotsCounted(CityLand land, LandParcel p)` | An offer's five areas counted again, plot by plot from the world's tiles: the plots of its rectangle the city does not own (0.7.67, the books to the plot). |

### 19. THE LAND ON THE WORLD (0.7.57) (lines 1709-2073)

| line | len | member | says |
|---:|---:|---|---|
| 1722 | 351 | `static void onTheWorld()` |  |

### 20. A CITY SAVED AND LOADED IS THE SAME LAND (0.7.57) (lines 2074-2137)

| line | len | member | says |
|---:|---:|---|---|
| 2084 | 53 | `static void savedAndLoaded() throws Exception` |  |

### 21. AN OLDER SAVE'S LAND IS CONVERTED (0.7.57) (lines 2138-2268)

| line | len | member | says |
|---:|---:|---|---|
| 2162 | 1 | **type** `record Research(String name, long seed, double landOwned, int ironSites, double ironTonnes, int mines)` | One research city's land as its save carries it: its name, its world's seed, its square feet, iron sites and tonnes, and its Iron Mines. |
| 2172 | 35 | `static void converted() throws Exception` |  |
| 2209 | 18 | `static Deposit nearestUnowned(CityLand land, Resource kind)` | The iron field nearest a city's site, in the nine cells round it, whose centre the city does not own; null when none. |
| 2229 | 19 | `static Deposit nearestOnDryGround(CityLand land, Resource kind)` | The field of a resource nearest a city's site, in the nine cells round it, whose centre plot is dry ground and not the city's (0.7.99): where foundedOn() puts a fixture's city. |
| 2258 | 7 | `static LandManager foundedOn(long x, long y)` | A land office whose city is founded on the default world at plot (x, y) rather than its founding site (0.7.99, batch W1): a centre of whole blocks round it holding a new city's dry ground (CityLand.found()), nothing t... |

### 22. THE TEST PLAYER KEEPS ITS GROUND AHEAD (0.7.58, batch J1d) (lines 2269-2351)

| line | len | member | says |
|---:|---:|---|---|
| 2283 | 68 | `static void groundKeptAhead()` |  |

### 23. THE TEST PLAYER BUYS ITS IRON A WHOLE FIELD AT A TIME (0.7.64, batch L) (lines 2352-2500)

| line | len | member | says |
|---:|---:|---|---|
| 2382 | 91 | `static void ironAWholeFieldAtATime()` |  |
| 2475 | 10 | `static Game ironListed(String label)` | A new city at 1.6 to the dollar grown, by hand, toward the iron field nearest its site (nearestUnowned(); its founding field until 0.7.98) - the offer nearest it each time - until an offer holds iron. |
| 2487 | 13 | `static double[] recount(CityLand land, Resource kind, double out, java.util.function.BiPredicate<Double, Double> in)` | Every field of a resource centred within `out` plots of a city's site (L-infinity) whose centre, seen from the site, passes `in`: its sites and its amount, whole - read off the world's cells, with none of CityLand's s... |

### 25. THE LEDGER WITH ZONES AND THE FOREST LAW (0.7.107, batch Z1) (lines 2501-2546)

| line | len | member | says |
|---:|---:|---|---|
| 2512 | 34 | `static void zonesOnTheLedger()` |  |

### 26. THE GROUND BY TYPE (0.7.110, batch I1; docs/studies/spec-materials.md 2.4) (lines 2547-2778)

| line | len | member | says |
|---:|---:|---|---|
| 2569 | 196 | `static void byType()` |  |
| 2767 | 10 | `static boolean wByType(LandManager land)` | Unowned + the remaining by type + E by type is the world's total, every resource, to the tonne. |

### 27. WHERE THE CUT AND THE CLEARING MEET (0.7.138, batch I9; (lines 2779-2823)

| line | len | member | says |
|---:|---:|---|---|
| 2791 | 31 | `static void cutAndCleared()` |  |

### 28. THE BOOKS BY RELIEF (ET1, spec-earth2-terrain 3.3) (lines 2824-3023)

| line | len | member | says |
|---:|---:|---|---|
| 2848 | 17 | `static long[] reliefCounted(CityLand land, long x0, long y0, long x1, long y1, java.util.function.BiPredicate<Long, Long> counted)` | Plots by relief counted by another road: {flat, foothill, steep, very steep, shallow sea, dry, sea} over the plots of [x0, x1) x [y0, y1) that `counted` takes - World.terrainAt() and reliefAt() a plot at a time. |
| 2867 | 8 | `static boolean booked(double[] relief, double dryKm2, double seaKm2, long[] counted)` | Whether a record's relief figures (flat to shallow, km2) are the plots counted again, to the bit, add up to its dry area, and its shallow sea lies in its sea. |
| 2877 | 3 | `static int hilly(double[] relief)` | 1 where a relief record holds foothill or steeper ground. |
| 2886 | 32 | `static int[] reliefBooks(String name, CityLand land, java.util.List<LandParcel> offers)` | One city's books by relief held to the plots: the centre's, each purchase's (over the plots the grid gives that holding), the total's, and each of `offers` (over the plots of its rectangle no holding owns). |
| 2924 | 33 | `static void handBuy(CityLand land, int hills, int shallow)` | Holdings bought by hand on the Earth: windows of 16 x 16 plots round the founding site that hold the most foothill or steeper ground (`hills`) or the most shallow sea (`shallow`), the best that leave the ground they t... |
| 2958 | 44 | `static void byRelief() throws Exception` |  |
| 3008 | 14 | `static void earthRelief(String name, World.Key key, boolean coast, boolean steep)` | An Earth city's books by relief held to the plots: a land office's centre and its twenty-four offers as it lists them, then a city founded the same way with holdings bought by hand where the ground has hills (and shal... |

