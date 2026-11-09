# PartnerCallsCheck.java - 1,320 lines · 40 methods · 9 constants · harnesses

`ham/citybuildersim/PartnerCallsCheck.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by
> kind of cargo, CallPartners' draw of a nation for a call, the boats' exits (an offing per bearing) and the trains' land partners, and (slice U)
> what the map draws of them: a boat's or a train's words, and the forks of a lane at the offing. The map (ui/MapView) draws them from those;
> nothing in the model's month reads any of it, so no trace moves.
> 
> <p>WHY. The map drew every boat out of the offing the way its route went and every train in from the one gateway, whoever the cargo was
> for; the month's trade by partner (WN3) says exactly who bought what. A call now carries the nation it is bound for, drawn by the tonnes
> of its kind of cargo, and a boat leaves on that nation's bearing. A drawing that is wrong is a ship sailing the wrong way for good, so this
> holds the draw to the tonnes and the bearing to the sea.
> 
> <p>What this has to prove:
> <ol>
>   <li>PARTNERTRADE BY CARGO: the month's tonnes by kind of cargo add, per direction and nation, to the month's tonnes of the goods that
>       have a cargo; a good with none is in the month's tonnes by nation and in none of these; the table is cleared at the month's turn;
>       through State (and Gson) and back it is equal, and a save without it restores none.</li>
>   <li>PICK() DRAWS BY TONNES: two nations 3 to 1 in dry bulk exports are drawn 3 to 1; a nation with none of that cargo is never drawn;
>       the boxes draw on both ways; a kind with no tonnes falls back on the direction's tonnes of every kind; NONE, an empty month and a
>       hash at the ends of its range answer as they should; a nation's bearing, reach by land and name.</li>
>   <li>WITH NO PARTNERS NOTHING MOVES: with CallPartners.NONE every call, arrival, route and position, and a frame on the legs, equals what
>       the schedule made without them (the routes with exits and without), and the same inputs twice make the same partners and exits;
>       partners change no call's kind, route or arrival; the calls draw by tonnes.</li>
>   <li>EXITS ON A MASK OF THE SEA: the compass in the plane; open water has all 72 exits open at ABYSS_M; a joined route has its lane's
>       exits, found once, a straight lane has none, and a route extended onto kept ones has those it would whole; on a coast with an island
>       beside the offing a bearing into the island is closed, every open exit's leg (and either side of it by SPREAD_DEG) keeps to the sea;
>       a call whose partner's bearing is closed leaves on the nearest open one, one whose bearing is open on that; a boat rides the route
>       to the offing unturned and heads on its exit within the spread, fading from the offing to the exit's end; and frame() finds the
>       boats on an exit, those and no others in a rectangle only the exit crosses.</li>
>   <li>TRAINS DRAW LAND PARTNERS ONLY: a far nation with tonnes of the kind never boards a train, the host always may, a boat may draw the
>       far one; partners change no train's kind, route or arrival; a frame's trains carry them.</li>
>   <li>THE COMPASS IS THE EARTH'S: from a spot a step along the plane's (sin b, -cos b) has the bearing b by EarthProj and
>       TradeLanes.bearingDeg, within a degree, for b every 45 degrees; from a founded city's site too when the Earth file is named.</li>
>   <li>AT TEN BILLION a frame's boats, on MapCheck's 20,000 lanes with exits and partners over 196 nations, take no more than
>       BOAT_FRAME_MS on its three views.</li>
>   <li>A BOAT AND A TRAIN NAME THEIR PARTNER: the words are the class's or kind's, then " to " the nation for a boat or train going out laden with
>       exports, " from " it for one coming in with imports, the nation alone for the boxes, which go both ways; the class alone with no partner,
>       none named, or NONE; and in a month's calls and trains each says what pick() drew it by.</li>
>   <li>THE LANE FORKS AT THE OFFING: seaPartners(kind) is exactly the nations pick() draws for the kind, biggest first, and seaShares(kind) their
>       shares of its tonnes; a route's forks are exactly the exits (by a scan of all 72) whose nations' shares add to at least FORK_SHARE - an
>       exit under it has none, two nations on one exit add, more than MAX_FORKS over it keep the largest, the smaller bearing on a tie - none for a
>       route without exits, for NONE, or for partners with no bearing, and a small partner's boat still leaves on its own exit; forks() on a
>       rectangle gives each offing's once whatever routes share it, the union of their kinds' (each capped already); and a frame of them takes
>       no more than BOAT_FRAME_MS.</li>
> </ol>

**Uses:** [BoatSchedule](BoatSchedule.md) (174), [Ports](Ports.md) (141), [CallPartners](CallPartners.md) (60), [SeaRoutes](SeaRoutes.md) (57), [PartnerTrade](PartnerTrade.md) (43), [MapCheck](MapCheck.md) (42), [World](World.md) (39), [TrainSchedule](TrainSchedule.md) (39), [Good](Good.md) (18), [EarthProj](EarthProj.md) (10), [MapFrame](MapFrame.md) (8), [Timing](Timing.md) (7), [WorldMarket](WorldMarket.md) (4), [EarthData](EarthData.md) (4), [WorldData](WorldData.md) (3), [NationsMap](NationsMap.md) (2), [TradeLanes](TradeLanes.md) (1), [Founding](Founding.md) (1)

## Sections

| line | section |
|---:|---|
| 97 | FIXTURES |
| 161 | 1. PARTNERTRADE BY CARGO |
| 259 | 2. PICK() DRAWS BY TONNES |
| 329 | THE MASK OF THE SEA (sections 3 and 4) |
| 415 | 3. WITH NO PARTNERS NOTHING MOVES |
| 477 | 4. EXITS ON A MASK OF THE SEA |
| 669 | 5. TRAINS DRAW LAND PARTNERS ONLY |
| 741 | 6. THE COMPASS IS THE EARTH'S |
| 790 | 7. TIMING |
| 863 | 8. A BOAT AND A TRAIN NAME THEIR PARTNER |
| 945 | 9. THE FORKS AT THE OFFING |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 63 | `PartnerCallsCheck.out` | `System.out` |  |
| 71 | `PartnerCallsCheck.NODE_LAT` | `{ 0, 15, - 15, 0, 0 }, NODE_LON = { 15, 0, 0, - 15, 150 }` | The compass, as the section's fixtures stand on it: the city at the equator on the prime meridian, nodes 15 degrees off it north, east, south and west (1,668 km: by land). |
| 74 | `PartnerCallsCheck.DRAWS` | `20_000` | The draws a share is measured over: fixed hashes, so a figure that passes once passes for ever. |
| 77 | `PartnerCallsCheck.SIGMAS` | `5` | How far a measured share may be from its probability, in standard deviations of a binomial of DRAWS draws: 5, which 20,000 hashes keep to under 0.016 at a share of three quarters. |
| 80 | `PartnerCallsCheck.SAMPLE_SLACK` | `SeaRoutes.CELL / 3.0 + 1` | A model's length measured by another road may differ by what the model's own sampling lets by: a sample's spacing, a third of a cell, and a plot. |
| 335 | `PartnerCallsCheck.Coast.COAST` | `20_000` |  |
| 413 | `PartnerCallsCheck.DRY_TONNES` | `2e7` | A month's trade of dry bulk, in tonnes: about 130 Capesize calls. |
| 746 | `PartnerCallsCheck.STEP_PLOTS` | `2_000` | The longest step the compass is held over, in plots: 2,000, 60 km. |
| 868 | `PartnerCallsCheck.DOT` | `" · "` | The middle dot the words carry between a class and its partner. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `static int fails` |  |
| 336 | `final SeaRoutes.Sea plain` |  |
| 337 | `SeaRoutes.Sea island` |  |
| 338 | `final List<BoatSchedule.Berth> berths` |  |
| 339 | `long siteX, siteY` |  |
| 340 | `double radius` |  |
| 341 | `SeaRoutes.Grid plainGrid, islandGrid` |  |
| 342 | `SeaRoutes.Found plainFound, found` |  |
| 343 | `List<BoatSchedule.Route> routes` |  |
| 345 | `double ox, oy` | The first route's offing, in plots, and its cell. |
| 346 | `long oc, oj` |  |
| 349 | `private static Coast coastMemo` |  |
| 749 | `static double worst` | The largest error found, in degrees. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 60 | 1261 | **type** `public class PartnerCallsCheck` | The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by kind of cargo, CallPartners' draw of a nation for a call, the boats' e... |
| 65 | 4 | `static void check(String label, boolean ok)` |  |
| 82 | 14 | `public static void main(String[] args) throws Exception` |  |

### FIXTURES (lines 97-160)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 1 | **type** `record Row(Good good, double[] out, double[] in)` | One good's month: its units (tonnes: one to the tonne) by nation, sold and bought. |
| 105 | 4 | `static Good goodOf(Ports.Cargo c, int skip)` | The first good that ships in a kind of cargo, the next after it, or null. |
| 111 | 4 | `static Good noCargo()` | A good that ships in no kind of cargo (Good.cargo() null: check). |
| 116 | 5 | `static double sum(double[] a)` |  |
| 123 | 3 | `static double[] nat(int n, double...first)` | n nations' figures, the first ones given. |
| 127 | 4 | `static void tally(PartnerTrade pt, int month, Good g, double[] out, double[] in, double perTonne)` |  |
| 133 | 6 | `static PartnerTrade trade(int n, int month, Row...rows)` | A record of n nations with a month tallied (closed) from the rows. |
| 140 | 3 | `static long hash(int i)` |  |
| 145 | 6 | `static double[] shares(CallPartners p, Ports.Cargo c, boolean inbound, boolean byLand, int nations)` | The share of DRAWS hashes each nation is drawn for (index 0: none). |
| 153 | 3 | `static double tolerance(double p, int draws)` | How far a share of `draws` draws may be from probability p: SIGMAS standard deviations of a binomial. |
| 157 | 3 | `static boolean near(double a, double b)` |  |

### 1. PARTNERTRADE BY CARGO (lines 161-258)

| line | len | member | says |
|---:|---:|---|---|
| 165 | 93 | `static void tonnesByCargo()` |  |

### 2. PICK() DRAWS BY TONNES (lines 259-328)

| line | len | member | says |
|---:|---:|---|---|
| 263 | 65 | `static void pickByTonnes()` |  |

### THE MASK OF THE SEA (sections 3 and 4) (lines 329-414)

| line | len | member | says |
|---:|---:|---|---|
| 334 | 14 | **type** `static final class Coast` | A coast and its routes: the sea north of cell row COAST; an island of land beside the first route's offing in a second mask. |
| 356 | 22 | `static Coast coast()` | Four dry bulk berths on a straight coast: the first two a few cells apart (the second joins the first's lane), the third far along the shore (a lane of its own), the fourth inland (a straight lane). |
| 380 | 7 | `static double firstLand(SeaRoutes.Sea sea, double x, double y, double bearing, double max)` | The first land on a straight leg from (x, y) along a compass bearing, in plots, looked at every quarter plot; infinity if none within max. |
| 389 | 10 | `static int oracleExit(double[] exits, double bearing)` | The exit nearest a bearing by a plain scan of all of them: the least angle, the smaller bearing on a tie; -1 if none is open. |
| 401 | 3 | `static Ports tradeOf(double dryBulk)` | A month's trade at sea: tonnes of dry bulk shipped out (and a quarter of that in liquid, general cargo and boxes, for which the coast has no berth: calls with none). |
| 406 | 5 | `static List<BoatSchedule.Route> withoutExits(List<BoatSchedule.Route> routes)` | The routes with their exits taken off: what they were before WN7. |

### 3. WITH NO PARTNERS NOTHING MOVES (lines 415-476)

| line | len | member | says |
|---:|---:|---|---|
| 419 | 57 | `static void nothingMovesWithoutPartners(Coast c)` |  |

### 4. EXITS ON A MASK OF THE SEA (lines 477-668)

| line | len | member | says |
|---:|---:|---|---|
| 481 | 86 | `static void exitsOnAMask(Coast c)` |  |
| 569 | 39 | `static void positions(Coast c, List<BoatSchedule.Route> routes, List<BoatSchedule.Call> calls)` | The boats of calls that have exits: the way to the offing unturned, the exit within the spread, the fade, the sea. |
| 610 | 13 | `static double[] walk(BoatSchedule.Route rt, double d)` | A distance d along a route's points (the harness's own walk). |
| 624 | 3 | `static boolean inRect(BoatSchedule.Boat b, long x0, long y0, long x1, long y1)` |  |
| 633 | 35 | `static void framesOnExits()` | frame() on a rectangle only an exit crosses: exactly the boats position() puts there. |

### 5. TRAINS DRAW LAND PARTNERS ONLY (lines 669-740)

| line | len | member | says |
|---:|---:|---|---|
| 673 | 67 | `static void trainsByLand()` |  |

### 6. THE COMPASS IS THE EARTH'S (lines 741-789)

| line | len | member | says |
|---:|---:|---|---|
| 751 | 12 | `static boolean compassFrom(EarthProj proj, double px, double py)` |  |
| 764 | 25 | `static void compassOnTheEarth()` |  |

### 7. TIMING (lines 790-862)

| line | len | member | says |
|---:|---:|---|---|
| 794 | 68 | `static void frameAtTenBillion()` |  |

### 8. A BOAT AND A TRAIN NAME THEIR PARTNER (lines 863-944)

| line | len | member | says |
|---:|---:|---|---|
| 871 | 7 | `static Ports tradeBothWays(double dryOut, double dryIn, double boxes)` | A month's trade at sea both ways: dry bulk out and in, and boxes (which count one way and carry both). |
| 879 | 65 | `static void wordsOfACall(Coast c)` |  |

### 9. THE FORKS AT THE OFFING (lines 945-1320)

| line | len | member | says |
|---:|---:|---|---|
| 953 | 12 | `static double[] weights(PartnerTrade.Snapshot s, Ports.Cargo kind)` | A kind's tonnes by nation as pick() draws a boat of it, read off the snapshot with no help from CallPartners: in each direction the kind's own tonnes if it has any there, else every kind's (the boxes both ways added);... |
| 971 | 14 | `static int[] expectedForks(BoatSchedule.Route rt, PartnerTrade.Snapshot s, CallPartners p)` | The forks of a route held to a plain read of the snapshot: each nation's share of the kind's tonnes (weights() over their sum, the nations with no bearing in the sum) added to the exit (oracleExit(), a scan of all 72)... |
| 987 | 20 | `static Set<List<Double>> expectedLegs(List<BoatSchedule.Route> routes, PartnerTrade.Snapshot s, CallPartners p)` | The legs forks() should give for some routes: per distinct offing the union of their routes' expected forks, each as {offing x, y, end x, y}. |
| 1009 | 10 | `static double[][] ringNodes(double...bearings)` | Nations standing 0.1 radian from the city at (0, 0) on the given compass bearings, one each: {latitudes, longitudes}. |
| 1021 | 4 | `static CallPartners ringPartners(double[] bearings, double[] weights, Good good)` | A schedule of the given routes whose dry bulk goes to nations standing on `bearings`, `weights` tonnes each, and the partners it draws by. |
| 1026 | 5 | `static Set<List<Double>> legsOf(double[] forks)` |  |
| 1032 | 177 | `static void forksAtTheOffing(Coast c)` |  |
| 1215 | 45 | `static void forksOnTheEarth()` | Printed, not asserted: how many forks a route draws on the open sea (all 72 exits open, the most there can be) from thirteen ports, were the Earth's nations trading dry bulk with the city in proportion to their GDP in... |
| 1262 | 58 | `static void forksAtTenBillion()` | The forks of an offing a frame draws, on the 10-billion lanes sharing 200 offings (a late city's lanes share, SL1): built once, then a frame's forks in no more than BOAT_FRAME_MS. |

