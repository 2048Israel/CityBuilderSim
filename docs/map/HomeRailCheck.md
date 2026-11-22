# HomeRailCheck.java - 927 lines · 28 methods · 6 constants · harnesses

`ham/citybuildersim/HomeRailCheck.java` - generated 2026-11-22 by CodeMap; line numbers are as of that run.

> The railway's carriage of the city's own bulk, and the port yard, held to FR6's
> brief: a bulk tonne bought at home rides a siding at the city leg's rate on
> the room the boundary's tonnes leave, a tonne that crossed a berth rides the
> port yard's rails to the same rule, and every leg is billed once.
> 
> WHY THIS HARNESS EXISTS. Until FR6 the railway was offered only what crossed
> the boundary: the ore from a city's mine to its own mill was a whole truck leg
> at Haulage's quote though FR7 lays the mine a siding, and a tonne landed at a
> berth was a whole truck leg from the quay to the door. FR6 (docs/queue/FR6.md)
> deals those tonnes to the railway last, after the boundary's, bills them at the
> railway's quote on Haulage's, and takes their railed part off the trucks' leg.
> It was written by one agent from the brief and is held here by another, from
> the brief's rules and the model's own getters and constants: no figure below
> is typed in that a getter or a constant could give. Nine claims, numbered as
> FR6.md section 6 numbers them:
> 
>   1. NO RAILWAY, NO CHANGE. With no station and no spur nothing of the city's
>      own bulk is dealt, and the trucks' legs with zero shares are the old
>      legs to the bit.
>   2. DEALT AFTER THE BOUNDARY. Never more than the offer, than what the track
>      or the sets leave once the boundary's bulk and goods are dealt, or than
>      what the stations and the sidings leave of the same; nothing when either
>      is used up; never below zero.
>   3. EACH LEG ONCE. A home tonne's truck leg is (1 - RAIL_ROAD_RELIEF x h) of
>      it and the railway's tonnes are RAIL_ROAD_RELIEF x h: together the tonne.
>   4. THE PORT YARD. A station and a sea terminal both standing, and then a
>      tonne that crossed a berth has (1 - PORT_YARD_SHARE x the quay share) of
>      its truck leg; with either missing, no yard and no quay share.
>   5. THE BILL. The shippers' "Rail freight" lines are Freight's rail revenue
>      to the cent; the city part is the railed city tonnes x the railway's
>      quote x Haulage's, both read before the haul; a mine that sold at home
>      pays no city bill for it.
>   6. THE DIESEL. The city tonnes burn CITY_LITRES_A_TONNE each.
>   7. THE WEIGHING. A planner weighs a home-bought tonne at the steady quote
>      x (its truck weight + the railway's quote x its rail weight): below the
>      all-truck leg while the railway's quote is below 1.
>   8. THE SAVE. The shares in force cross a save, and the reload bills the
>      next month to the bit.
>   9. A REAL RUN. LongPlaytest's city, played with its advisor: the months the
>      home share is above zero and the months a port yard stands, counted;
>      never a home share without a station or a spur, nor a quay share
>      without a yard, over every month. Whether the advisor lays a railway
>      is the run's path, counted, not asserted: sections 2 and 3 cause it
>      (FS7).
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. The fixtures are HaulageCheck's played city (three iron
> mines selling ore at home to a pelletising plant that lands its coal and
> ships its pellets) with a railway handed to it, because the tonnes a
> mechanic deals are the tonnes a city makes: each fixture says what it lays
> and the section asserts the condition it needed actually came about.

**Uses:** [Good](Good.md) (62), [Freight](Freight.md) (42), [Game](Game.md) (36), [Haulage](Haulage.md) (33), [LongPlaytest](LongPlaytest.md) (28), [HaulageCheck](HaulageCheck.md) (17), [Sector](Sector.md) (15), [GameFiles](GameFiles.md) (11), [Traffic](Traffic.md) (4), [RailDepotCheck](RailDepotCheck.md) (3), [BuildingManager](BuildingManager.md) (2), [SubstationCheck](SubstationCheck.md) (2), [TreasuryFund](TreasuryFund.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [SectorState](SectorState.md) (1), [Markets](Markets.md) (1), [Ports](Ports.md) (1), [Sectors](Sectors.md) (1), [Mining](Mining.md) (1), [TaxPolicy](TaxPolicy.md) (1)

**Used by (2):** [FreightViewCheck](FreightViewCheck.md), [TradeBandCheck](TradeBandCheck.md)

## Sections

| line | section |
|---:|---|
| 106 | THE FIXTURES |
| 198 | · the next haul's bill, worked out before it runs |
| 289 | THE RUN |
| 329 | 1. NO RAILWAY, NO CHANGE |
| 390 | 2. DEALT AFTER THE BOUNDARY |
| 486 | 3. EACH LEG ONCE |
| 549 | 4. THE PORT YARD |
| 634 | 5. THE BILL |
| 692 | 6. THE DIESEL |
| 721 | 7. THE WEIGHING |
| 776 | 8. THE SAVE |
| 821 | 9. A REAL RUN |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 92 | `HomeRailCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The siding's part of a home tonne, and of a railed boundary tonne (InfrastructureManager). |
| 95 | `HomeRailCheck.YARD` | `Freight.PORT_YARD_SHARE` | The most of the tonnes that crossed a berth the port yard takes to rail (FR6, D7). |
| 98 | `HomeRailCheck.CITY_LITRES` | `Freight.CITY_LITRES_A_TONNE` | The diesel a railed city tonne burns, in litres. |
| 101 | `HomeRailCheck.BASE_MONTHS` | `14` | Months a fixture city is played before a railway is handed to it: HaulageCheck's, long enough for the trucks to be seeded and the books settled. |
| 104 | `HomeRailCheck.REAL_RUN_MONTHS` | `1250` | Months of a real run section 9 plays, for the breadth of its identity: about half a minute; the advisor lays its first station at about month 990 in this city on one trace (the run's own count prints it), and nothing ... |
| 150 | `HomeRailCheck.FRACTIONAL_MONTHS` | `60` | (GW1) The most months untilFractional() plays on: the staffing gate's city fills a depot and four spurs with the boundary's tonnes only after some two years. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |
| 181 | `final BuildingManager bm` |  |
| 182 | `final Freight fr` |  |
| 202 | `double h, q, rq, hq` |  |
| 203 | `final Map<Sector, Double> boundary` |  |
| 204 | `double homeTonnes, quayTonnes` |  |
| 244 | `Plan plan` |  |
| 245 | `double lines, haulageBilled, railRevenue, homeRailed, quayRailed, homeBill, cityLitres, fuelLitres, hauled` |  |
| 246 | `double worstMiss` |  |
| 247 | `String worstWhere` |  |
| 248 | `double constructionExcess` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 861 | **type** `public class HomeRailCheck` | The railway's carriage of the city's own bulk, and the port yard, held to FR6's brief: a bulk tonne bought at home rides a siding at the city leg's rate on the room the boundary's tonnes leave, a tonne that crossed a ... |
| 71 | 1 | `static void quietly(Runnable r)` |  |
| 73 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 84 | 1 | `static boolean same(double a, double b)` | Bitwise, not near (TradeCostCheck explains why this family compares so). |
| 86 | 1 | `static boolean near(double a, double b, double tol)` |  |
| 89 | 1 | `static boolean isZero(double x)` | A positive zero, bit for bit: what "exactly nothing" means for a deal. |

### THE FIXTURES (lines 106-197)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 3 | `static Game railed(GameFiles files, int depots, int spurs, int lines, String berth, int months)` | HaulageCheck's played city with a railway handed to it and played on: `depots` Rail Depots, `spurs` Rail Spurs, `lines` Freight Lines and one building from the ports (`berth`, or none) laid on their ground (HaulageChe... |
| 124 | 24 | `static Game railed(GameFiles files, int depots, int spurs, int lines, String berth, int months, boolean pelletsPay)` | ...and, with `pelletsPay`, on the literals with pellets' world prices doubled (FL1): the Pelletising Plant's pellets net under its ore and coal abroad, and THE ORDER no longer buys ore for an export at a loss, so the ... |
| 153 | 7 | `static void untilFractional(Game g)` | ...played on, up to FRACTIONAL_MONTHS, until the railway's room after the boundary's tonnes is short of the city's: the share in force strictly between nothing and all. |
| 162 | 6 | `static void setShares(Freight fr, double home, double quay)` | The home and quay shares put where a fixture wants them, through the sector's own save and restore (the shares are struck at a haul, so a fixture cannot reach them by moving the room). |
| 170 | 1 | `static double railLine(Sector s)` | What a sector pays the railway, in the statement's input line (Freight bills it "Rail freight"). |
| 173 | 5 | `static double beforeFr6(Sector s, Good g, double railShare)` | The pre-FR6 city leg of a good: the old three-argument form, written out from FR2's brief. |
| 180 | 17 | **type** `static final class Bare` | A bare Freight on a catalogue with these buildings standing (RailDepotCheck's fixture): no city, so only the buildings decide a port yard. |
| 184 | 5 | `Bare(String...standing)` _(in HomeRailCheck.Bare)_ |  |
| 191 | 5 | `double berthTonnes()` _(in HomeRailCheck.Bare)_ | The tonnes a month of berth standing, over every kind of cargo. |

### the next haul's bill, worked out before it runs (lines 198-288)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 10 | **type** `static final class Plan` | What the next haul will bill each sector, from the rows the last month left and the state in force: the brief's rule, not the code's loop. |
| 207 | 1 | `double city(Sector s)` _(in HomeRailCheck.Plan)_ | The city part of a sector's line: its railed city tonnes x the railway's quote x Haulage's. |
| 209 | 1 | `double expected(Sector s)` _(in HomeRailCheck.Plan)_ |  |
| 212 | 29 | `static Plan plan(Game game)` |  |
| 243 | 7 | **type** `static final class Month` | One month run with its bill worked out first: the plan, then what the haul in the month billed and burned. |
| 252 | 36 | `static Month runMonth(Game city, double setHome, double setQuay)` | NaN leaves the shares as they stand. |

### THE RUN (lines 289-328)

| line | len | member | says |
|---:|---:|---|---|
| 293 | 35 | `public static void main(String[] args)` |  |

### 1. NO RAILWAY, NO CHANGE (lines 329-389)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 56 | `static void noRailway(Game plain, Game trackOnly)` |  |

### 2. DEALT AFTER THE BOUNDARY (lines 390-485)

| line | len | member | says |
|---:|---:|---|---|
| 394 | 91 | `static void dealt(Game tight, Game rail)` |  |

### 3. EACH LEG ONCE (lines 486-548)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 58 | `static void eachLegOnce(Game rail, Game yard)` |  |

### 4. THE PORT YARD (lines 549-633)

| line | len | member | says |
|---:|---:|---|---|
| 553 | 80 | `static void portYard(Game rail, Game yard, Game spurBerth)` |  |

### 5. THE BILL (lines 634-691)

| line | len | member | says |
|---:|---:|---|---|
| 638 | 53 | `static Month[] bill(Game rail, Game yard)` |  |

### 6. THE DIESEL (lines 692-720)

| line | len | member | says |
|---:|---:|---|---|
| 696 | 24 | `static void diesel(Game plain, Month[] months)` |  |

### 7. THE WEIGHING (lines 721-775)

| line | len | member | says |
|---:|---:|---|---|
| 725 | 50 | `static void weighing(Game rail, Game yard)` |  |

### 8. THE SAVE (lines 776-820)

| line | len | member | says |
|---:|---:|---|---|
| 780 | 40 | `static void save(Game yard, GameFiles files)` |  |

### 9. A REAL RUN (lines 821-927)

| line | len | member | says |
|---:|---:|---|---|
| 825 | 102 | `static void realRun()` |  |

