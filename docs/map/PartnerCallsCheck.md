# PartnerCallsCheck.java - 850 lines · 29 methods · 8 constants · harnesses

`ham/citybuildersim/PartnerCallsCheck.java` - generated 2026-11-01 by CodeMap; line numbers are as of that run.

> The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by
> kind of cargo, CallPartners' draw of a nation for a call, the boats' exits (an offing per bearing) and the trains' land partners. The map
> (ui/MapView) draws them in a later slice; nothing in the model's month reads any of it, so no trace moves.
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
> </ol>

**Uses:** [BoatSchedule](BoatSchedule.md) (96), [Ports](Ports.md) (72), [SeaRoutes](SeaRoutes.md) (44), [CallPartners](CallPartners.md) (35), [PartnerTrade](PartnerTrade.md) (31), [MapCheck](MapCheck.md) (25), [World](World.md) (23), [Good](Good.md) (13), [TrainSchedule](TrainSchedule.md) (12), [EarthProj](EarthProj.md) (9), [Timing](Timing.md) (4), [WorldMarket](WorldMarket.md) (4), [EarthData](EarthData.md) (4), [MapFrame](MapFrame.md) (4), [TradeLanes](TradeLanes.md) (1), [Founding](Founding.md) (1)

## Sections

| line | section |
|---:|---|
| 85 | FIXTURES |
| 149 | 1. PARTNERTRADE BY CARGO |
| 247 | 2. PICK() DRAWS BY TONNES |
| 317 | THE MASK OF THE SEA (sections 3 and 4) |
| 403 | 3. WITH NO PARTNERS NOTHING MOVES |
| 465 | 4. EXITS ON A MASK OF THE SEA |
| 657 | 5. TRAINS DRAW LAND PARTNERS ONLY |
| 729 | 6. THE COMPASS IS THE EARTH'S |
| 778 | 7. TIMING |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 53 | `PartnerCallsCheck.out` | `System.out` |  |
| 61 | `PartnerCallsCheck.NODE_LAT` | `{ 0, 15, - 15, 0, 0 }, NODE_LON = { 15, 0, 0, - 15, 150 }` | The compass, as the section's fixtures stand on it: the city at the equator on the prime meridian, nodes 15 degrees off it north, east, south and west (1,668 km: by land). |
| 64 | `PartnerCallsCheck.DRAWS` | `20_000` | The draws a share is measured over: fixed hashes, so a figure that passes once passes for ever. |
| 67 | `PartnerCallsCheck.SIGMAS` | `5` | How far a measured share may be from its probability, in standard deviations of a binomial of DRAWS draws: 5, which 20,000 hashes keep to under 0.016 at a share of three quarters. |
| 70 | `PartnerCallsCheck.SAMPLE_SLACK` | `SeaRoutes.CELL / 3.0 + 1` | A model's length measured by another road may differ by what the model's own sampling lets by: a sample's spacing, a third of a cell, and a plot. |
| 323 | `PartnerCallsCheck.Coast.COAST` | `20_000` |  |
| 401 | `PartnerCallsCheck.DRY_TONNES` | `2e7` | A month's trade of dry bulk, in tonnes: about 130 Capesize calls. |
| 734 | `PartnerCallsCheck.STEP_PLOTS` | `2_000` | The longest step the compass is held over, in plots: 2,000, 60 km. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 52 | `static int fails` |  |
| 324 | `final SeaRoutes.Sea plain` |  |
| 325 | `SeaRoutes.Sea island` |  |
| 326 | `final List<BoatSchedule.Berth> berths` |  |
| 327 | `long siteX, siteY` |  |
| 328 | `double radius` |  |
| 329 | `SeaRoutes.Grid plainGrid, islandGrid` |  |
| 330 | `SeaRoutes.Found plainFound, found` |  |
| 331 | `List<BoatSchedule.Route> routes` |  |
| 333 | `double ox, oy` | The first route's offing, in plots, and its cell. |
| 334 | `long oc, oj` |  |
| 337 | `private static Coast coastMemo` |  |
| 737 | `static double worst` | The largest error found, in degrees. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 50 | 801 | **type** `public class PartnerCallsCheck` | The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by kind of cargo, CallPartners' draw of a nation for a call, the boats' e... |
| 55 | 4 | `static void check(String label, boolean ok)` |  |
| 72 | 12 | `public static void main(String[] args) throws Exception` |  |

### FIXTURES (lines 85-148)

| line | len | member | says |
|---:|---:|---|---|
| 90 | 1 | **type** `record Row(Good good, double[] out, double[] in)` | One good's month: its units (tonnes: one to the tonne) by nation, sold and bought. |
| 93 | 4 | `static Good goodOf(Ports.Cargo c, int skip)` | The first good that ships in a kind of cargo, the next after it, or null. |
| 99 | 4 | `static Good noCargo()` | A good that ships in no kind of cargo (Good.cargo() null: check). |
| 104 | 5 | `static double sum(double[] a)` |  |
| 111 | 3 | `static double[] nat(int n, double...first)` | n nations' figures, the first ones given. |
| 115 | 4 | `static void tally(PartnerTrade pt, int month, Good g, double[] out, double[] in, double perTonne)` |  |
| 121 | 6 | `static PartnerTrade trade(int n, int month, Row...rows)` | A record of n nations with a month tallied (closed) from the rows. |
| 128 | 3 | `static long hash(int i)` |  |
| 133 | 6 | `static double[] shares(CallPartners p, Ports.Cargo c, boolean inbound, boolean byLand, int nations)` | The share of DRAWS hashes each nation is drawn for (index 0: none). |
| 141 | 3 | `static double tolerance(double p, int draws)` | How far a share of `draws` draws may be from probability p: SIGMAS standard deviations of a binomial. |
| 145 | 3 | `static boolean near(double a, double b)` |  |

### 1. PARTNERTRADE BY CARGO (lines 149-246)

| line | len | member | says |
|---:|---:|---|---|
| 153 | 93 | `static void tonnesByCargo()` |  |

### 2. PICK() DRAWS BY TONNES (lines 247-316)

| line | len | member | says |
|---:|---:|---|---|
| 251 | 65 | `static void pickByTonnes()` |  |

### THE MASK OF THE SEA (sections 3 and 4) (lines 317-402)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 14 | **type** `static final class Coast` | A coast and its routes: the sea north of cell row COAST; an island of land beside the first route's offing in a second mask. |
| 344 | 22 | `static Coast coast()` | Four dry bulk berths on a straight coast: the first two a few cells apart (the second joins the first's lane), the third far along the shore (a lane of its own), the fourth inland (a straight lane). |
| 368 | 7 | `static double firstLand(SeaRoutes.Sea sea, double x, double y, double bearing, double max)` | The first land on a straight leg from (x, y) along a compass bearing, in plots, looked at every quarter plot; infinity if none within max. |
| 377 | 10 | `static int oracleExit(double[] exits, double bearing)` | The exit nearest a bearing by a plain scan of all of them: the least angle, the smaller bearing on a tie; -1 if none is open. |
| 389 | 3 | `static Ports tradeOf(double dryBulk)` | A month's trade at sea: tonnes of dry bulk shipped out (and a quarter of that in liquid, general cargo and boxes, for which the coast has no berth: calls with none). |
| 394 | 5 | `static List<BoatSchedule.Route> withoutExits(List<BoatSchedule.Route> routes)` | The routes with their exits taken off: what they were before WN7. |

### 3. WITH NO PARTNERS NOTHING MOVES (lines 403-464)

| line | len | member | says |
|---:|---:|---|---|
| 407 | 57 | `static void nothingMovesWithoutPartners(Coast c)` |  |

### 4. EXITS ON A MASK OF THE SEA (lines 465-656)

| line | len | member | says |
|---:|---:|---|---|
| 469 | 86 | `static void exitsOnAMask(Coast c)` |  |
| 557 | 39 | `static void positions(Coast c, List<BoatSchedule.Route> routes, List<BoatSchedule.Call> calls)` | The boats of calls that have exits: the way to the offing unturned, the exit within the spread, the fade, the sea. |
| 598 | 13 | `static double[] walk(BoatSchedule.Route rt, double d)` | A distance d along a route's points (the harness's own walk). |
| 612 | 3 | `static boolean inRect(BoatSchedule.Boat b, long x0, long y0, long x1, long y1)` |  |
| 621 | 35 | `static void framesOnExits()` | frame() on a rectangle only an exit crosses: exactly the boats position() puts there. |

### 5. TRAINS DRAW LAND PARTNERS ONLY (lines 657-728)

| line | len | member | says |
|---:|---:|---|---|
| 661 | 67 | `static void trainsByLand()` |  |

### 6. THE COMPASS IS THE EARTH'S (lines 729-777)

| line | len | member | says |
|---:|---:|---|---|
| 739 | 12 | `static boolean compassFrom(EarthProj proj, double px, double py)` |  |
| 752 | 25 | `static void compassOnTheEarth()` |  |

### 7. TIMING (lines 778-850)

| line | len | member | says |
|---:|---:|---|---|
| 782 | 68 | `static void frameAtTenBillion()` |  |

