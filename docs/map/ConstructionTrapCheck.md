# ConstructionTrapCheck.java - 1,237 lines · 36 methods · 12 constants · harnesses

`ham/citybuildersim/ConstructionTrapCheck.java` - generated 2026-11-10 by CodeMap; line numbers are as of that run.

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
>   9. A CANCEL IN THE TRAP (FC1b): the city's site ordered while he takes orders, cancelled in one of two twins, leaves
>      his book at the month's end by the points the order had left at the stop and nothing else of his month moves;
>      a restart while he still takes orders puts them on once, never twice; and a depot of the builders' that goes
>      first, which he never had on his book, takes none off it when it is cancelled.
>  10. A RUSH BESIDE THE CONTRACTOR (FC1b): in a month he builds beside the city's own crews, a rushed site's
>      overtime, its premium and the wages the city pays for it are struck on the part of its share the city's own
>      crews worked, not on his; he is paid for the points the sites took past the city's own month, the overtime
>      among it; and in a city with no contractor that part is the whole share, to the bit.
>  11. SAVED MID-TRAP, THE NEXT MONTH THE SAME: a trapped city saved and loaded plays the month after as the twin that
>      was never saved does - his book, points and bill, the builders' cash and order book, the points built, the
>      price of an order and whether he takes them, to the bit.
>  12. AN ORDER'S OWN BUILDINGS (BA1): with no cap on orders an order of six hundred homes is not priced at the first's queue; each
>      building stands in the queue the ones before it leave, and the order pays the mean premium over them
>      (BuildersShort.meanPremium()). One building is FC1's price to the bit (meanPremium(from, step, 1) == premium(from),
>      orderPriceMultiplier(x) == orderPriceMultiplier(x, pointsEach, 1)); up to EXACT_SUM_MOST it is the explicit mean and past it
>      the integral within the javadoc's stated figures of the exact sum, on the grid it was measured on; never above CEILING, under
>      nothing or NaN; never falling as the order grows; and a quote of 600 homes on a queue near the knee costs more a home than
>      a quote of one (and the contractor's price is the same for any size of order).
> 
> Every fixture causes its condition. How often the trap is met in a real run is the ensemble's count (FC1.md).

**Uses:** [BuildersShort](BuildersShort.md) (108), [Game](Game.md) (50), [BuildingManager](BuildingManager.md) (20), [ConstructionControl](ConstructionControl.md) (20), [Construction](Construction.md) (15), [BuildingsTemplate](BuildingsTemplate.md) (11), [MoneyAudit](MoneyAudit.md) (7), [DeathCareFirstCheck](DeathCareFirstCheck.md) (5), [GameFiles](GameFiles.md) (4), [Founding](Founding.md) (4), [Sectors](Sectors.md) (2), [BuildersFirstCheck](BuildersFirstCheck.md) (1), [TempDirs](TempDirs.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Sector](Sector.md) (1)

## Sections

| line | section |
|---:|---|
| 205 | 1. THE PRICE'S SHAPE |
| 267 | 2. THE TRAP, CAUSED |
| 356 | 3. THE PRICE RISES AS THE FREE POINTS FALL |
| 481 | 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST |
| 558 | 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS |
| 614 | 6. THE MONEY |
| 656 | 7. A CITY THAT NEVER MEETS THE TRAP |
| 686 | 8. SAVED AND LOADED |
| 721 | 9. A CANCEL IN THE TRAP |
| 853 | 10. A RUSH BESIDE THE CONTRACTOR |
| 958 | 11. SAVED MID-TRAP, THE NEXT MONTH THE SAME |
| 1008 | 12. AN ORDER'S OWN BUILDINGS (BA1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 111 | `ConstructionTrapCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key, the landlords' (whose Houses are the queue), and the templates the fixture orders. |
| 112 | `ConstructionTrapCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 113 | `ConstructionTrapCheck.DEPOT` | `BuildersFirstCheck.DEPOT` |  |
| 114 | `ConstructionTrapCheck.ROAD` | `DeathCareFirstCheck.ROAD` |  |
| 115 | `ConstructionTrapCheck.HOUSE` | `"House"` |  |
| 118 | `ConstructionTrapCheck.LANDLORDS_HOUSES` | `40` | The landlords' Houses the trapped city has on site beside the city's own sites. |
| 121 | `ConstructionTrapCheck.CALM_SHARE_OF_KNEE` | `0.8` | The queue of the twins that never met the trap, as a share of the knee in months of the free points they start with: well under it. |
| 124 | `ConstructionTrapCheck.RUN_MONTHS` | `60` | The most months the climb out is played, and the months of quiet (no book, no points) that end it: the last bill is struck the month after. |
| 125 | `ConstructionTrapCheck.QUIET_MONTHS` | `3` |  |
| 128 | `ConstructionTrapCheck.NEVER_MONTHS` | `6` | The months the twin that never met the trap is played. |
| 131 | `ConstructionTrapCheck.SCRATCH_SLOT` | `10` | The slot the saved city is written to, in its own scratch folder (never the player's slots). |
| 724 | `ConstructionTrapCheck.CITY_DEPOTS` | `2` | The Construction Depots the city orders in the trap: more points than the contractor is offered, so one is still on site when the month ends and the cancel has something to stop. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `static int fails` |  |
| 78 | `static PrintStream out` |  |
| 79 | `static PrintStream quiet` |  |
| 134 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 135 | `static int monthsAudited` |  |
| 270 | `static int roadsAdded` | What the road loop left: the roads added, and the sites at every post before and after the one that tipped it. |
| 271 | `static double sitesBeforeTheLastRoad, sitesAfterTheLastRoad` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 1163 | **type** `public class ConstructionTrapCheck` | The builders short (FC1): what an order pays a point of work as the repairs leave the builders fewer points for the sites, and the foreign contractor that takes the orders, builds the builders' own depots first and is... |
| 81 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 86 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 92 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 98 | 5 | `static void quietly(Runnable r)` |  |
| 104 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 138 | 7 | `static void month(Game g)` | One month, audited. |
| 147 | 2 | **type** `record Row(int month, boolean trapped, double sites, double output, double points, double bill, double stmt...` | What one month played left: the trap as the month met it, the sites and the month at every post, the contractor's month, the statement's lines and the audit's. |
| 151 | 13 | `static Row play(Game g, List<Row> into)` | One month played, audited, and what it left, added to `into`. |
| 167 | 3 | `static double auditLine(MoneyAudit.Result r, String label)` | A line of the audit by its label, as the model recorded it (EP2: Result.figure(), not the detail's two printed decimals); 0 when it struck none. |
| 172 | 3 | `static GameFiles filesOf(Path root, String name)` | The files DeathCareFirstCheck.city() gives a city of that name, so a second Game can be pointed at its folder. |
| 176 | 28 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PRICE'S SHAPE (lines 205-266)

| line | len | member | says |
|---:|---:|---|---|
| 207 | 59 | `static void shape()` |  |

### 2. THE TRAP, CAUSED (lines 267-355)

| line | len | member | says |
|---:|---:|---|---|
| 274 | 4 | `static double perRoad(Game g)` | The repairs one standing Paved Road adds to the builders' month (as DeathCareFirstCheck.sites() has it). |
| 280 | 3 | `static void roads(Game g, int n)` | n standing Paved Roads, as DeathCareFirstCheck.sites() builds them. |
| 289 | 16 | `static Game trapped(Path root, String name)` | DeathCareFirstCheck's city, then standing Paved Roads: two short of the model's own figure for the roads whose repairs leave the sites the floor of the builders' month, and then one at a time until the contractor take... |
| 307 | 11 | `static void sites(Game g)` | ...and the sites DeathCareFirstCheck.sites() puts on it, less its cemetery: the city's University and Middle Schools, the landlords' Houses; nobody lies unburied. |
| 319 | 36 | `static Game trap(Path root)` |  |

### 3. THE PRICE RISES AS THE FREE POINTS FALL (lines 356-480)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 17 | `static Game calm(Path root, String name)` | A twin that never met the trap: the trap's city (DeathCareFirstCheck.city()) with its Penitentiary cancelled - its queue was the only thing in the way of a premium of none - and, in its place, the landlords' Houses on... |
| 382 | 2 | **type** `record State(String name, double sites, double months, double premium, double multiple, double sticker, dou...` | A House's quote at a state of the city: the free points, the months queued, the premium, the multiple, the sticker and the planners' cost. |
| 385 | 13 | `static State state(Game g, String name)` |  |
| 400 | 5 | `static int roadsFor(Game g, double months)` | The standing roads that bring the months queued to `months` at the points the builders have now. |
| 406 | 74 | `static void priceRises(Path root)` |  |

### 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST (lines 481-557)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 74 | `static void depotFirst(Game g, List<Row> rows)` |  |

### 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS (lines 558-613)

| line | len | member | says |
|---:|---:|---|---|
| 560 | 53 | `static void climbsOut(Game g, List<Row> rows)` |  |

### 6. THE MONEY (lines 614-655)

| line | len | member | says |
|---:|---:|---|---|
| 616 | 6 | `static void audited(String where)` |  |
| 623 | 32 | `static void money(List<Row> rows)` |  |

### 7. A CITY THAT NEVER MEETS THE TRAP (lines 656-685)

| line | len | member | says |
|---:|---:|---|---|
| 658 | 27 | `static Game never(Path root)` |  |

### 8. SAVED AND LOADED (lines 686-720)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 32 | `static void saved(Path root, Game never) throws Exception` |  |

### 9. A CANCEL IN THE TRAP (lines 721-852)

| line | len | member | says |
|---:|---:|---|---|
| 727 | 4 | `static ConstructionControl.Refund refundOf(Game g, String building)` | The refund the month's stop made for the building of that name (ConstructionControl.Refund), or null when it stopped none. |
| 733 | 11 | `static void sameStart(String where, Game a, Game b)` | Two trapped cities that stand alike to the bit: the free points, the repairs, the queue, his book and the treasury's cash, and he takes orders in both. |
| 745 | 61 | `static void cancelInTheTrap(Path root)` |  |
| 808 | 44 | `static void cancelADepotFirst(Path root)` | A depot of the builders' ordered by the city and served first: he never had it on his book, so cancelling it takes nothing off. |

### 10. A RUSH BESIDE THE CONTRACTOR (lines 853-957)

| line | len | member | says |
|---:|---:|---|---|
| 856 | 4 | `static ConstructionControl.Overtime overtimeOf(Game g, String key)` | The overtime of the rushed site the month struck, with the plan it was struck in. |
| 861 | 96 | `static void rushBesideTheContractor(Path root)` |  |

### 11. SAVED MID-TRAP, THE NEXT MONTH THE SAME (lines 958-1007)

| line | len | member | says |
|---:|---:|---|---|
| 960 | 47 | `static void nextMonthTheSame(Path root) throws Exception` |  |

### 12. AN ORDER'S OWN BUILDINGS (BA1) (lines 1008-1237)

| line | len | member | says |
|---:|---:|---|---|
| 1011 | 5 | `static double explicitMean(double from, double step, long n)` | The sums an order of n buildings is priced by, in the order the model sums them (BuildersShort.meanPremium() up to EXACT_SUM_MOST). |
| 1018 | 6 | `static double integralTolerance(double stepMonths)` | The tolerance meanPremium()'s javadoc states for the integral past EXACT_SUM_MOST, by the step in months (its MEASURED figures). |
| 1034 | 203 | `static void ordersOwnBuildings(Path root)` | AN ORDER'S OWN BUILDINGS (BA1). |

