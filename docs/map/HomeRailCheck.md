# HomeRailCheck.java - 886 lines · 27 methods · 5 constants · harnesses

`ham/citybuildersim/HomeRailCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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
>      home share is above zero and the months a port yard stands, counted.
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. The fixtures are HaulageCheck's played city (three iron
> mines selling ore at home to a pelletising plant that lands its coal and
> ships its pellets) with a railway handed to it, because the tonnes a
> mechanic deals are the tonnes a city makes: each fixture says what it lays
> and the section asserts the condition it needed actually came about.

**Uses:** [Good](Good.md) (59), [Freight](Freight.md) (43), [Game](Game.md) (36), [Haulage](Haulage.md) (33), [LongPlaytest](LongPlaytest.md) (28), [HaulageCheck](HaulageCheck.md) (16), [Sector](Sector.md) (15), [GameFiles](GameFiles.md) (10), [Traffic](Traffic.md) (4), [RailDepotCheck](RailDepotCheck.md) (3), [BuildingManager](BuildingManager.md) (2), [TreasuryFund](TreasuryFund.md) (2), [InfrastructureManager](InfrastructureManager.md) (1), [SectorState](SectorState.md) (1), [Markets](Markets.md) (1), [Ports](Ports.md) (1), [Sectors](Sectors.md) (1), [Mining](Mining.md) (1), [TaxPolicy](TaxPolicy.md) (1)

**Used by (2):** [FreightViewCheck](FreightViewCheck.md), [TradeBandCheck](TradeBandCheck.md)

## Sections

| line | section |
|---:|---|
| 102 | THE FIXTURES |
| 166 | · the next haul's bill, worked out before it runs |
| 257 | THE RUN |
| 294 | 1. NO RAILWAY, NO CHANGE |
| 355 | 2. DEALT AFTER THE BOUNDARY |
| 449 | 3. EACH LEG ONCE |
| 512 | 4. THE PORT YARD |
| 597 | 5. THE BILL |
| 655 | 6. THE DIESEL |
| 684 | 7. THE WEIGHING |
| 739 | 8. THE SAVE |
| 784 | 9. A REAL RUN |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `HomeRailCheck.RELIEF` | `InfrastructureManager.RAIL_ROAD_RELIEF` | The siding's part of a home tonne, and of a railed boundary tonne (InfrastructureManager). |
| 91 | `HomeRailCheck.YARD` | `Freight.PORT_YARD_SHARE` | The most of the tonnes that crossed a berth the port yard takes to rail (FR6, D7). |
| 94 | `HomeRailCheck.CITY_LITRES` | `Freight.CITY_LITRES_A_TONNE` | The diesel a railed city tonne burns, in litres. |
| 97 | `HomeRailCheck.BASE_MONTHS` | `14` | Months a fixture city is played before a railway is handed to it: HaulageCheck's, long enough for the trucks to be seeded and the books settled. |
| 100 | `HomeRailCheck.REAL_RUN_MONTHS` | `1250` | Months of a real run section 9 plays: the advisor lays its first station at about month 990 in this city (the run's own count prints it) and the run takes about half a minute. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 65 | `static int fails` |  |
| 149 | `final BuildingManager bm` |  |
| 150 | `final Freight fr` |  |
| 170 | `double h, q, rq, hq` |  |
| 171 | `final Map<Sector, Double> boundary` |  |
| 172 | `double homeTonnes, quayTonnes` |  |
| 212 | `Plan plan` |  |
| 213 | `double lines, haulageBilled, railRevenue, homeRailed, quayRailed, homeBill, cityLitres, fuelLitres, hauled` |  |
| 214 | `double worstMiss` |  |
| 215 | `String worstWhere` |  |
| 216 | `double constructionExcess` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 824 | **type** `public class HomeRailCheck` | The railway's carriage of the city's own bulk, and the port yard, held to FR6's brief: a bulk tonne bought at home rides a siding at the city leg's rate on the room the boundary's tonnes leave, a tonne that crossed a ... |
| 67 | 1 | `static void quietly(Runnable r)` |  |
| 69 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 74 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 80 | 1 | `static boolean same(double a, double b)` | Bitwise, not near (TradeCostCheck explains why this family compares so). |
| 82 | 1 | `static boolean near(double a, double b, double tol)` |  |
| 85 | 1 | `static boolean isZero(double x)` | A positive zero, bit for bit: what "exactly nothing" means for a deal. |

### THE FIXTURES (lines 102-165)

| line | len | member | says |
|---:|---:|---|---|
| 110 | 11 | `static Game railed(GameFiles files, int depots, int spurs, int lines, String berth, int months)` | HaulageCheck's played city with a railway handed to it and played on: `depots` Rail Depots, `spurs` Rail Spurs, `lines` Freight Lines and one building from the ports (`berth`, or none) laid on their ground (HaulageChe... |
| 123 | 5 | `static void untilFractional(Game g)` | ...played on, up to a year, until the railway's room after the boundary's tonnes is short of the city's: the share in force strictly between nothing and all. |
| 130 | 6 | `static void setShares(Freight fr, double home, double quay)` | The home and quay shares put where a fixture wants them, through the sector's own save and restore (the shares are struck at a haul, so a fixture cannot reach them by moving the room). |
| 138 | 1 | `static double railLine(Sector s)` | What a sector pays the railway, in the statement's input line (Freight bills it "Rail freight"). |
| 141 | 5 | `static double beforeFr6(Sector s, Good g, double railShare)` | The pre-FR6 city leg of a good: the old three-argument form, written out from FR2's brief. |
| 148 | 17 | **type** `static final class Bare` | A bare Freight on a catalogue with these buildings standing (RailDepotCheck's fixture): no city, so only the buildings decide a port yard. |
| 152 | 5 | `Bare(String...standing)` _(in HomeRailCheck.Bare)_ |  |
| 159 | 5 | `double berthTonnes()` _(in HomeRailCheck.Bare)_ | The tonnes a month of berth standing, over every kind of cargo. |

### the next haul's bill, worked out before it runs (lines 166-256)

| line | len | member | says |
|---:|---:|---|---|
| 169 | 10 | **type** `static final class Plan` | What the next haul will bill each sector, from the rows the last month left and the state in force: the brief's rule, not the code's loop. |
| 175 | 1 | `double city(Sector s)` _(in HomeRailCheck.Plan)_ | The city part of a sector's line: its railed city tonnes x the railway's quote x Haulage's. |
| 177 | 1 | `double expected(Sector s)` _(in HomeRailCheck.Plan)_ |  |
| 180 | 29 | `static Plan plan(Game game)` |  |
| 211 | 7 | **type** `static final class Month` | One month run with its bill worked out first: the plan, then what the haul in the month billed and burned. |
| 220 | 36 | `static Month runMonth(Game city, double setHome, double setQuay)` | NaN leaves the shares as they stand. |

### THE RUN (lines 257-293)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 32 | `public static void main(String[] args)` |  |

### 1. NO RAILWAY, NO CHANGE (lines 294-354)

| line | len | member | says |
|---:|---:|---|---|
| 298 | 56 | `static void noRailway(Game plain, Game trackOnly)` |  |

### 2. DEALT AFTER THE BOUNDARY (lines 355-448)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 89 | `static void dealt(Game tight, Game rail)` |  |

### 3. EACH LEG ONCE (lines 449-511)

| line | len | member | says |
|---:|---:|---|---|
| 453 | 58 | `static void eachLegOnce(Game rail, Game yard)` |  |

### 4. THE PORT YARD (lines 512-596)

| line | len | member | says |
|---:|---:|---|---|
| 516 | 80 | `static void portYard(Game rail, Game yard, Game spurBerth)` |  |

### 5. THE BILL (lines 597-654)

| line | len | member | says |
|---:|---:|---|---|
| 601 | 53 | `static Month[] bill(Game rail, Game yard)` |  |

### 6. THE DIESEL (lines 655-683)

| line | len | member | says |
|---:|---:|---|---|
| 659 | 24 | `static void diesel(Game plain, Month[] months)` |  |

### 7. THE WEIGHING (lines 684-738)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 50 | `static void weighing(Game rail, Game yard)` |  |

### 8. THE SAVE (lines 739-783)

| line | len | member | says |
|---:|---:|---|---|
| 743 | 40 | `static void save(Game yard, GameFiles files)` |  |

### 9. A REAL RUN (lines 784-886)

| line | len | member | says |
|---:|---:|---|---|
| 788 | 98 | `static void realRun(Game rail)` |  |

