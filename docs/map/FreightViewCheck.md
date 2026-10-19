# FreightViewCheck.java - 494 lines · 19 methods · 2 constants · harnesses

`ham/citybuildersim/FreightViewCheck.java` - generated 2026-10-19 by CodeMap; line numbers are as of that run.

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
> equal the getter that keeps it. Six claims:
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
> 
> It tests the model through its public getters and constants and changes
> nothing in it; where the code and the brief disagree it says FAIL and the
> line says where. Every fixture is a city of its own in GameFiles.scratch().

**Uses:** [Ports](Ports.md) (19), [Game](Game.md) (14), [Freight](Freight.md) (11), [FreightView](FreightView.md) (7), [Haulage](Haulage.md) (5), [PortDivision](PortDivision.md) (5), [HaulageCheck](HaulageCheck.md) (4), [Sector](Sector.md) (4), [GameFiles](GameFiles.md) (3), [HomeRailCheck](HomeRailCheck.md) (2), [BuildingManager](BuildingManager.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [Founding](Founding.md) (1), [SectorState](SectorState.md) (1), [Sectors](Sectors.md) (1), [BoatSchedule](BoatSchedule.md) (1)

## Sections

| line | section |
|---:|---|
| 103 | THE FIXTURES |
| 171 | 1. NO FREIGHT |
| 212 | 2. THE LEGS |
| 275 | 3. HAULAGE |
| 318 | 4. RAIL |
| 360 | 5. PORTS |
| 445 | 6. THE SHIPPERS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 95 | `FreightViewCheck.CENT` | `0.01 / 1000` | A cent, in the game's money: D$0.01, and the money is in D$ thousands. |
| 101 | `FreightViewCheck.K` | `Ports.Cargo.values().length` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 66 | `static int fails` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 64 | 431 | **type** `public class FreightViewCheck` | FreightView, the model behind the Freight screens, held to the study's board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this month in legs, and the Haulage, Rail and Ports pages with eac... |
| 68 | 5 | `static void quietly(Runnable r)` |  |
| 74 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 79 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 85 | 3 | `static boolean same(double a, double b)` | Bitwise, not near: the view hands a getter's figure on unchanged. |
| 90 | 3 | `static boolean near(double a, double b)` | Within a rounding: sums the model makes in another order. |
| 97 | 3 | `static boolean toTheCent(double a, double b)` |  |

### THE FIXTURES (lines 103-170)

| line | len | member | says |
|---:|---:|---|---|
| 108 | 8 | `static Game unplayed(String label)` | A founded city that has never been played: no haul has run. |
| 123 | 11 | `static Game full(String label)` | HomeRailCheck's railed city (HaulageCheck's three iron mines and a pelletising plant, played fourteen months) with two Rail Depots, two Rail Spurs and a Bulk Terminal laid, then a General Cargo Terminal, two Tanker Te... |
| 136 | 24 | `static String nonFinite(Object o, String path)` | Every double in a record (lists, optionals and nested records included) is finite; the first that is not, named. |
| 162 | 8 | `static int standing(Game g, Ports.Cargo kind)` | The terminals of a kind standing, counted from the catalogue by the building manager's own quantities. |

### 1. NO FREIGHT (lines 171-211)

| line | len | member | says |
|---:|---:|---|---|
| 175 | 36 | `static void noFreight()` |  |

### 2. THE LEGS (lines 212-274)

| line | len | member | says |
|---:|---:|---|---|
| 216 | 40 | `static void legs(Game g, View v)` |  |
| 258 | 16 | `static void lorries()` | ...and a played city with a railway and no berth: the lorries are what the railway left, the sea bar and the ports page are zero. |

### 3. HAULAGE (lines 275-317)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 38 | `static void haulage(Game g, View v)` |  |

### 4. RAIL (lines 318-359)

| line | len | member | says |
|---:|---:|---|---|
| 322 | 37 | `static void rail(Game g, View v)` |  |

### 5. PORTS (lines 360-444)

| line | len | member | says |
|---:|---:|---|---|
| 364 | 74 | `static void ports(Game g, View v)` |  |
| 439 | 5 | `static double sum(double[] a)` |  |

### 6. THE SHIPPERS (lines 445-494)

| line | len | member | says |
|---:|---:|---|---|
| 449 | 26 | `static void shippers(Game g, View v)` |  |
| 476 | 18 | `public static void main(String[] args) throws Exception` |  |

