# ConstructionTrapCheck.java - 701 lines · 26 methods · 11 constants · harnesses

`ham/citybuildersim/ConstructionTrapCheck.java` - generated 2026-11-03 by CodeMap; line numbers are as of that run.

> The builders short (FC1): what an order pays a point of work as the repairs leave the builders fewer points for the
> sites, and the foreign contractor that takes the orders, builds the builders' own depots first and is paid abroad
> when they leave none - held on a city whose repairs take the whole of the builders' month.
> 
> WHY. Jerus's city, 2026-11-01 (docs/queue/FC1.md): "in a sufficiently big city, on a specific sequence of events, a
> city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
> The repairs take the builders' points before the sites, the builders' own depot waits in the order book for points
> the repairs never leave (BuildersFirstCheck's stall, but now to the last point), and the price of a point did not read
> how short they were. The model is BuildersShort (the arithmetic), BuildingManager (the multiple every order's work
> pays), Game.foreignCrewsOffer() and settleForeignCrews() (the contractor's month) and sectors.Construction (its book
> and its bill). This is the evidence that the trap is met, that the price answers it and that the contractor lets the
> city out: DeathCareFirstCheck's city with enough standing roads that the repairs take all but a sliver of the month.
> 
> What this has to prove:
>   1. THE PRICE'S SHAPE (BuildersShort alone): no premium to the knee, strictly rising past it, half the ceiling at
>      the knee and HALF_MONTHS past it, under the ceiling for the month counts a city can have and exactly it
>      with no points for the sites at all; the contractor takes orders under the floor of the month and not at it,
>      never with no month; his price is above anything the city's own premium reaches; his capacity never under
>      a depot's points.
>   2. THE TRAP, CAUSED: standing roads, one at a time past the model's own figure, until the repairs take more than
>      all but FOREIGN_FLOOR of the builders' month at every post (one road fewer did not); the city's own sites and
>      the landlords' Houses on site; the contractor takes orders and has the sites' orders on his book.
>   3. THE PRICE RISES AS THE FREE POINTS FALL, in a twin that never met the trap: the quote of a House at five
>      states - the free points ample (no premium, exactly the template's price), then the roads taking them, the
>      months queued passing the knee and rising, and under the floor the contractor's price; every quote the
>      multiple the model says, the planners' cost rising with it, and an order placed raising the next one's price.
>   4. THE FOREIGN CREWS TAKE THE DEPOT FIRST: the builders' own depot ordered in the trap is priced at the
>      contractor's price and served as their own depot, not put on his book a second time; it is built first out
>      of the month by his points, he is paid for the points the sites took
>      past the city's own month at FOREIGN_PRICE the base price of a point, and the builders' statement carries the
>      bill as an import.
>   5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS: the builders' own plan keeps opening depots, the sites at
>      every post climb over the floor and the trap does not return, the contractor works off his book and no new
>      order adds to it.
>   6. THE MONEY: the sum of the contractor's bills is the sum of what the builders' statements carried as imports
>      from him, each in the audit's TRADE scope, and nothing was paid abroad in a month he built nothing.
>   7. A CITY THAT NEVER MEETS THE TRAP PAYS NOTHING ABROAD: free points and a queue under the knee for months
>      played: no book, no points, no bill, the multiple exactly one.
>   8. SAVED AND LOADED: the contractor's book and month come back from a save to the bit, and a city that never met
>      the trap saves none of it.
> 
> Every fixture causes its condition. How often the trap is met in a real run is the ensemble's count (FC1.md).

**Uses:** [BuildersShort](BuildersShort.md) (66), [Game](Game.md) (28), [BuildingManager](BuildingManager.md) (9), [Construction](Construction.md) (9), [BuildingsTemplate](BuildingsTemplate.md) (8), [DeathCareFirstCheck](DeathCareFirstCheck.md) (5), [GameFiles](GameFiles.md) (4), [ConstructionControl](ConstructionControl.md) (4), [MoneyAudit](MoneyAudit.md) (3), [Sectors](Sectors.md) (2), [BuildersFirstCheck](BuildersFirstCheck.md) (1), [TempDirs](TempDirs.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [Founding](Founding.md) (1)

## Sections

| line | section |
|---:|---|
| 186 | 1. THE PRICE'S SHAPE |
| 248 | 2. THE TRAP, CAUSED |
| 337 | 3. THE PRICE RISES AS THE FREE POINTS FALL |
| 462 | 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST |
| 539 | 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS |
| 595 | 6. THE MONEY |
| 637 | 7. A CITY THAT NEVER MEETS THE TRAP |
| 667 | 8. SAVED AND LOADED |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 95 | `ConstructionTrapCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key, the landlords' (whose Houses are the queue), and the templates the fixture orders. |
| 96 | `ConstructionTrapCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 97 | `ConstructionTrapCheck.DEPOT` | `BuildersFirstCheck.DEPOT` |  |
| 98 | `ConstructionTrapCheck.ROAD` | `DeathCareFirstCheck.ROAD` |  |
| 99 | `ConstructionTrapCheck.HOUSE` | `"House"` |  |
| 102 | `ConstructionTrapCheck.LANDLORDS_HOUSES` | `40` | The landlords' Houses the trapped city has on site beside the city's own sites. |
| 105 | `ConstructionTrapCheck.CALM_SHARE_OF_KNEE` | `0.8` | The queue of the twins that never met the trap, as a share of the knee in months of the free points they start with: well under it. |
| 108 | `ConstructionTrapCheck.RUN_MONTHS` | `60` | The most months the climb out is played, and the months of quiet (no book, no points) that end it: the last bill is struck the month after. |
| 109 | `ConstructionTrapCheck.QUIET_MONTHS` | `3` |  |
| 112 | `ConstructionTrapCheck.NEVER_MONTHS` | `6` | The months the twin that never met the trap is played. |
| 115 | `ConstructionTrapCheck.SCRATCH_SLOT` | `10` | The slot the saved city is written to, in its own scratch folder (never the player's slots). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 61 | `static int fails` |  |
| 62 | `static PrintStream out` |  |
| 63 | `static PrintStream quiet` |  |
| 118 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 119 | `static int monthsAudited` |  |
| 251 | `static int roadsAdded` | What the road loop left: the roads added, and the sites at every post before and after the one that tipped it. |
| 252 | `static double sitesBeforeTheLastRoad, sitesAfterTheLastRoad` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 59 | 643 | **type** `public class ConstructionTrapCheck` | The builders short (FC1): what an order pays a point of work as the repairs leave the builders fewer points for the sites, and the foreign contractor that takes the orders, builds the builders' own depots first and is... |
| 65 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 70 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 76 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 82 | 5 | `static void quietly(Runnable r)` |  |
| 88 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 122 | 7 | `static void month(Game g)` | One month, audited. |
| 131 | 2 | **type** `record Row(int month, boolean trapped, double sites, double output, double points, double bill, double stmt...` | What one month played left: the trap as the month met it, the sites and the month at every post, the contractor's month, the statement's lines and the audit's. |
| 135 | 13 | `static Row play(Game g, List<Row> into)` | One month played, audited, and what it left, added to `into`. |
| 150 | 5 | `static double auditLine(MoneyAudit.Result r, String label)` | A line of the audit's detail by its label, as the audit printed it (two decimals); 0 when it printed none. |
| 157 | 3 | `static GameFiles filesOf(Path root, String name)` | The files DeathCareFirstCheck.city() gives a city of that name, so a second Game can be pointed at its folder. |
| 161 | 24 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PRICE'S SHAPE (lines 186-247)

| line | len | member | says |
|---:|---:|---|---|
| 188 | 59 | `static void shape()` |  |

### 2. THE TRAP, CAUSED (lines 248-336)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 4 | `static double perRoad(Game g)` | The repairs one standing Paved Road adds to the builders' month (as DeathCareFirstCheck.sites() has it). |
| 261 | 3 | `static void roads(Game g, int n)` | n standing Paved Roads, as DeathCareFirstCheck.sites() builds them. |
| 270 | 16 | `static Game trapped(Path root, String name)` | DeathCareFirstCheck's city, then standing Paved Roads: two short of the model's own figure for the roads whose repairs leave the sites the floor of the builders' month, and then one at a time until the contractor take... |
| 288 | 11 | `static void sites(Game g)` | ...and the sites DeathCareFirstCheck.sites() puts on it, less its cemetery: the city's University and Middle Schools, the landlords' Houses; nobody lies unburied. |
| 300 | 36 | `static Game trap(Path root)` |  |

### 3. THE PRICE RISES AS THE FREE POINTS FALL (lines 337-461)

| line | len | member | says |
|---:|---:|---|---|
| 344 | 17 | `static Game calm(Path root, String name)` | A twin that never met the trap: the trap's city (DeathCareFirstCheck.city()) with its Penitentiary cancelled - its queue was the only thing in the way of a premium of none - and, in its place, the landlords' Houses on... |
| 363 | 2 | **type** `record State(String name, double sites, double months, double premium, double multiple, double sticker, dou...` | A House's quote at a state of the city: the free points, the months queued, the premium, the multiple, the sticker and the planners' cost. |
| 366 | 13 | `static State state(Game g, String name)` |  |
| 381 | 5 | `static int roadsFor(Game g, double months)` | The standing roads that bring the months queued to `months` at the points the builders have now. |
| 387 | 74 | `static void priceRises(Path root)` |  |

### 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST (lines 462-538)

| line | len | member | says |
|---:|---:|---|---|
| 464 | 74 | `static void depotFirst(Game g, List<Row> rows)` |  |

### 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS (lines 539-594)

| line | len | member | says |
|---:|---:|---|---|
| 541 | 53 | `static void climbsOut(Game g, List<Row> rows)` |  |

### 6. THE MONEY (lines 595-636)

| line | len | member | says |
|---:|---:|---|---|
| 597 | 6 | `static void audited(String where)` |  |
| 604 | 32 | `static void money(List<Row> rows)` |  |

### 7. A CITY THAT NEVER MEETS THE TRAP (lines 637-666)

| line | len | member | says |
|---:|---:|---|---|
| 639 | 27 | `static Game never(Path root)` |  |

### 8. SAVED AND LOADED (lines 667-701)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 32 | `static void saved(Path root, Game never) throws Exception` |  |

