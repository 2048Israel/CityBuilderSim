# FreightViewCheck.java - 716 lines · 26 methods · 2 constants · harnesses

`ham/citybuildersim/FreightViewCheck.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> FreightView, the model behind the Freight screens, held to the study's
> board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's
> freight this month in legs, and the Haulage, Rail and Ports pages with each
> division's fleet, quote and bill.
> 
> WHY THIS HARNESS EXISTS. The screens only paint FreightView's figures, so
> the figures are the finding. They must be the model's own: a bar the model
> did not count, a fleet worked out in the screen or a share divided by zero
> would show on a player's page and in no other check. It is written from the
> model's getters and constants, and where the view says a figure it must
> equal the getter that keeps it. Seven claims:
> 
>   1. NO FREIGHT, NO NUMBER. A city that has never hauled shows rows of zero,
>      and no figure of any record is NaN or infinite.
>   2. THE LEGS. The long haul's three bars sum to the tonnes that crossed the
>      boundary as the railway counts them (the rail bar the tonnes it hauled,
>      the sea bar the ships', the world's lorries the rest, never below
>      zero); the city leg's two are the trucks' tonnes (which the map's
>      trucks sum to) and the sidings' (FR6's home and quay tonnes); every bar
>      names the door its mode opens.
>   3. HAULAGE. The trucks against the need, the depots counted from the
>      catalogue, the month's carriage, the quote with the rule's target and
>      the bill, each the getter's; the shippers' Haulage lines are the bill.
>   4. RAIL. The sets against the track's need, the track, stations and
>      sidings, FR6's home and quay tonnes and bill, the quote as a share of
>      the lorry rate with the rule's floor, each the getter's; the shippers'
>      Rail freight lines are the railway's bill.
>   5. PORTS. The berths by kind counted from the catalogue, their tonnes
>      against their room, the handling quote with the due as its floor and
>      the rule's target, the dues and the handling by kind summing to the
>      division's, the calls the schedule counts.
>   6. THE SHIPPERS. Each sector's three lines are its statement's, the list
>      runs largest first, and the divisions' bills together are the
>      shippers' to the cent.
>   7. THE WORKS' STRIP ("How its tonnes move", board 3). Each sector's
>      tonnes by good, way and mode are Haulage's tally of the month billed:
>      the trucks' tonnes over the sectors are Haulage.getTonnes() (and by
>      way the map's trucks' three loads), the railway's the tonnes it hauled,
>      the ships' the ports', the siding's home tonnes and the yard's FR6's;
>      each strip's bills are its statement's lines; a siding's saving is
>      RAIL_ROAD_RELIEF x the truck tonnes x Haulage's quote; the pellet
>      plant before and after is Freight.bulkTonnesOf() all by truck and with
>      a siding; a sector with no freight, and a city that never hauled, read
>      zeros and no NaN; and the tally survives the save.
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. Every fixture is a city of its own in GameFiles.scratch().

**Uses:** [Game](Game.md) (21), [Ports](Ports.md) (20), [FreightView](FreightView.md) (16), [Freight](Freight.md) (16), [Haulage](Haulage.md) (16), [Sector](Sector.md) (7), [PortDivision](PortDivision.md) (6), [HaulageCheck](HaulageCheck.md) (4), [InfrastructureManager](InfrastructureManager.md) (4), [GameFiles](GameFiles.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (3), [HomeRailCheck](HomeRailCheck.md) (2), [BuildingManager](BuildingManager.md) (2), [SectorState](SectorState.md) (2), [Sectors](Sectors.md) (2), [Good](Good.md) (2), [Founding](Founding.md) (1), [BoatSchedule](BoatSchedule.md) (1), [Traffic](Traffic.md) (1), [HeavyIndustry](HeavyIndustry.md) (1)

## Sections

| line | section |
|---:|---|
| 119 | THE FIXTURES |
| 187 | 1. NO FREIGHT |
| 228 | 2. THE LEGS |
| 291 | 3. HAULAGE |
| 334 | 4. RAIL |
| 376 | 5. PORTS |
| 461 | 6. THE SHIPPERS |
| 492 | 7. THE WORKS' STRIP |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `FreightViewCheck.CENT` | `0.01 / 1000` | A cent, in the game's money: D$0.01, and the money is in D$ thousands. |
| 117 | `FreightViewCheck.K` | `Ports.Cargo.values().length` | The kinds of cargo a berth handles: Ports.Cargo's count. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 81 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 79 | 638 | **type** `public class FreightViewCheck` | FreightView, the model behind the Freight screens, held to the study's board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this month in legs, and the Haulage, Rail and Ports pages with eac... |
| 83 | 5 | `static void quietly(Runnable r)` |  |
| 89 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 94 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 100 | 3 | `static boolean same(double a, double b)` | Bitwise, not near: the view hands a getter's figure on unchanged. |
| 105 | 3 | `static boolean near(double a, double b)` | Within a rounding: sums the model makes in another order. |
| 112 | 3 | `static boolean toTheCent(double a, double b)` |  |

### THE FIXTURES (lines 119-186)

| line | len | member | says |
|---:|---:|---|---|
| 124 | 8 | `static Game unplayed(String label)` | A founded city that has never been played: no haul has run. |
| 139 | 11 | `static Game full(String label)` | HomeRailCheck's railed city (HaulageCheck's three iron mines and a pelletising plant, played fourteen months) with two Rail Depots, two Rail Spurs and a Bulk Terminal laid, then a General Cargo Terminal, two Tanker Te... |
| 152 | 24 | `static String nonFinite(Object o, String path)` | Every double in a record (lists, optionals and nested records included) is finite; the first that is not, named. |
| 178 | 8 | `static int standing(Game g, Ports.Cargo kind)` | The terminals of a kind standing, counted from the catalogue by the building manager's own quantities. |

### 1. NO FREIGHT (lines 187-227)

| line | len | member | says |
|---:|---:|---|---|
| 191 | 36 | `static void noFreight()` |  |

### 2. THE LEGS (lines 228-290)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 40 | `static void legs(Game g, View v)` |  |
| 274 | 16 | `static void lorries()` | ...and a played city with a railway and no berth: the lorries are what the railway left, the sea bar and the ports page are zero. |

### 3. HAULAGE (lines 291-333)

| line | len | member | says |
|---:|---:|---|---|
| 295 | 38 | `static void haulage(Game g, View v)` |  |

### 4. RAIL (lines 334-375)

| line | len | member | says |
|---:|---:|---|---|
| 338 | 37 | `static void rail(Game g, View v)` |  |

### 5. PORTS (lines 376-460)

| line | len | member | says |
|---:|---:|---|---|
| 380 | 74 | `static void ports(Game g, View v)` |  |
| 455 | 5 | `static double sum(double[] a)` |  |

### 6. THE SHIPPERS (lines 461-491)

| line | len | member | says |
|---:|---:|---|---|
| 465 | 26 | `static void shippers(Game g, View v)` |  |

### 7. THE WORKS' STRIP (lines 492-716)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 5 | `static List<Tonnes> strips(Game g)` | Every Tonnes a city's sectors give, in the sector order. |
| 504 | 65 | `static void stripSums(Game g, String when)` | The city's strips held to the model's own counts: `when` names the month in the labels. |
| 571 | 40 | `static void stripSiding(Game g)` | The siding's saving, the rate and the empty strips, in a played city. |
| 613 | 44 | `static void stripPlant(Game g)` | The pellet plant before and after (spec 3.6), from the model's own tonnes and constants. |
| 659 | 13 | `static void stripSaved(Game g)` | The strip is saved: reset, restored from the sector's state, the same lines come back. |
| 674 | 11 | `static void stripNone()` | A city that never hauled: every strip is zero and finite. |
| 686 | 10 | `static void strip(Game g)` |  |
| 697 | 19 | `public static void main(String[] args) throws Exception` |  |

