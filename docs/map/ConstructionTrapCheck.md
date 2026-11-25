# ConstructionTrapCheck.java - 1,653 lines · 44 methods · 13 constants · harnesses

`ham/citybuildersim/ConstructionTrapCheck.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The builders short (FC1) and the foreign crews always on offer beside them (FC2): what an order pays a point of work
> as the repairs leave the builders fewer points for the sites, the queue at home past the threshold that a foreign
> contractor takes, building it with his own crews and paid abroad, and the city that climbs out - held on a city whose
> repairs take all but a sliver of the builders' month.
> 
> WHY. Jerus's city, 2026-11-01 (docs/queue/FC1.md): "in a sufficiently big city, on a specific sequence of events, a
> city could end up, where there are no crews to build, they are all repairing. and it gets stuck, no way to fix it".
> The repairs take the builders' points before the sites, the builders' own depot waits in the order book for points
> the repairs never leave (BuildersFirstCheck's stall, but now to the last point), and the price of a point did not read
> how short they were. The model is BuildersShort (the arithmetic), BuildingManager (the multiple every order's work
> pays, and the foreign crews' terms, foreignTerms()), Game.foreignCrewsOffer() and settleForeignCrews() (the
> contractor's month) and sectors.Construction (his book, his bill and the builders' sales abroad). This is the
> evidence that the trap is met, that the price answers it and that the contractor lets the city out:
> DeathCareFirstCheck's city with enough standing roads that the repairs take all but SLIVER of the month. FC2's own
> bounds - the exchange rate, the weakest D$, the world's money under domestic inflation, the export and the long
> backlog - are ForeignCrewsCheck's.
> 
> What this has to prove:
>   1. THE PRICE'S SHAPE (BuildersShort alone): no premium to the knee, strictly rising past it, half the ceiling at
>      the knee and HALF_MONTHS past it, under the ceiling for the month counts a city can have and exactly it
>      with no points for the sites at all; the foreign crews' first share is FOREIGN_EXCESS of the world's price a
>      point over the city's, capped at the premium SURE_MONTHS past the knee; the threshold is where the premium
>      reaches it, never under the knee; the work past it is the overflow, none at it; their month is that work in
>      the threshold's months, never under a depot's points nor over the work; their excess rises with their month
>      from the first share, never to CEILING; and an order's multiple is the premium for the buildings within the
>      threshold and their excess for the rest.
>   2. THE TRAP, CAUSED: standing roads, one at a time past the model's own figure, until the repairs take more than
>      all but SLIVER of the builders' month at every post (one road fewer did not); the city's own sites and the
>      landlords' Houses on site; the queue at home past the threshold, every order's work at the contractor's price,
>      and nothing on his book until the month's offer hands him the overflow.
>   3. THE PRICE RISES AS THE FREE POINTS FALL, in a twin that never met the trap: the quote of a House at five
>      states - the free points ample (no premium, exactly the template's price), then the roads taking them, the
>      months queued passing the knee and rising within the threshold, and past it the contractor's price, the road
>      before it not: the premium passed his first share between them; every quote the multiple the model says, the
>      planners' cost rising with it, and an order placed raising the next one's price.
>   4. THE FOREIGN CREWS BUILD THE DEPOT FIRST: the builders' own depot ordered in the trap is priced at the
>      contractor's price and served first as their own; the month's offer hands him the queue at home past the
>      threshold, his month on it and his price at that month; the depot is built first out of the month with his
>      points, he is paid for the points the sites took past the city's own month at his price, his book comes down
>      by them, and the builders' statement carries the bill as an import.
>   5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS: the builders' own plan keeps opening depots, the sites at
>      every post climb over the sliver, no order goes abroad and the trap does not return, the contractor works off
>      his book and no new order adds to it.
>   6. THE MONEY: the sum of the contractor's bills is the sum of what the builders' statements carried as imports
>      from him, each in the audit's TRADE scope, and nothing was paid abroad in a month he built nothing; what the
>      builders sold abroad is their statement's exports the month after, in the same scope, and never in a month he
>      built.
>   7. A CITY THAT NEVER MEETS THE TRAP PAYS NOTHING ABROAD: free points and a queue under the knee for months
>      played: no book, no points, no bill, the multiple exactly one.
>   8. SAVED AND LOADED: the contractor's book and month and FC2's month's figures come back from a save to the bit,
>      and a city that never met the trap saves none of it.
>   9. A CANCEL IN THE TRAP (FC1b; FC2): the city's site ordered in the trap, cancelled in one of two twins, leaves his
>      book at the month's end by the points the order had left at the stop and nothing else of his month moves; a
>      restart puts them back in the queue at home, not on his book, and the next month's offer hands them on to him
>      once, never twice; and a depot of the builders' that goes first, cancelled, takes its points off his book too:
>      the builders cut the work they pay a stranger's excess for first, whatever the order.
>  10. A RUSH BESIDE THE CONTRACTOR (FC1b): in a month he builds beside the city's own crews, a rushed site's
>      overtime, its premium and the wages the city pays for it are struck on the part of its share the city's own
>      crews worked, not on his; he is paid for the points the sites took past the city's own month, the overtime
>      among it; and in a city with no contractor that part is the whole share, to the bit.
> ... (12 more lines in the source)

**Uses:** [BuildersShort](BuildersShort.md) (117), [Game](Game.md) (60), [BuildingManager](BuildingManager.md) (31), [ConstructionControl](ConstructionControl.md) (20), [BuildingsTemplate](BuildingsTemplate.md) (17), [Construction](Construction.md) (17), [MoneyAudit](MoneyAudit.md) (8), [DeathCareFirstCheck](DeathCareFirstCheck.md) (5), [GameFiles](GameFiles.md) (4), [Founding](Founding.md) (3), [Sectors](Sectors.md) (2), [BuildersFirstCheck](BuildersFirstCheck.md) (1), [TempDirs](TempDirs.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [TaxPolicy](TaxPolicy.md) (1), [Sector](Sector.md) (1)

**Used by (3):** [ForeignChoiceCheck](ForeignChoiceCheck.md), [ForeignCrewsCheck](ForeignCrewsCheck.md), [SectorStatementCheck](SectorStatementCheck.md)

## Sections

| line | section |
|---:|---|
| 233 | 1. THE PRICE'S SHAPE |
| 352 | 2. THE TRAP, CAUSED |
| 476 | 3. THE PRICE RISES AS THE FREE POINTS FALL |
| 693 | 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST |
| 804 | 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS |
| 865 | 6. THE MONEY |
| 931 | 7. A CITY THAT NEVER MEETS THE TRAP |
| 961 | 8. SAVED AND LOADED |
| 1003 | 9. A CANCEL IN THE TRAP |
| 1239 | 10. A RUSH BESIDE THE CONTRACTOR |
| 1352 | 11. SAVED MID-TRAP, THE NEXT MONTH THE SAME |
| 1407 | 12. AN ORDER'S OWN BUILDINGS (BA1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 123 | `ConstructionTrapCheck.BUILDERS` | `Sectors.CONSTRUCTION` | The builders' sector's key, the landlords' (whose Houses are the queue), and the templates the fixture orders. |
| 124 | `ConstructionTrapCheck.RE` | `Sectors.REAL_ESTATE` |  |
| 125 | `ConstructionTrapCheck.DEPOT` | `BuildersFirstCheck.DEPOT` |  |
| 126 | `ConstructionTrapCheck.ROAD` | `DeathCareFirstCheck.ROAD` |  |
| 127 | `ConstructionTrapCheck.HOUSE` | `"House"` |  |
| 130 | `ConstructionTrapCheck.LANDLORDS_HOUSES` | `40` | The landlords' Houses the trapped city has on site beside the city's own sites. |
| 137 | `ConstructionTrapCheck.SLIVER` | `0.05` | The sliver of the builders' month, every post offered, the trap leaves the sites: 5%, FC1's FOREIGN_FLOOR, under which his contractor came. |
| 140 | `ConstructionTrapCheck.CALM_SHARE_OF_KNEE` | `0.8` | The queue of the twins that never met the trap, as a share of the knee in months of the free points they start with: well under it. |
| 143 | `ConstructionTrapCheck.RUN_MONTHS` | `60` | The most months the climb out is played, and the months of quiet (no book, no points) that end it: the last bill is struck the month after. |
| 144 | `ConstructionTrapCheck.QUIET_MONTHS` | `3` |  |
| 147 | `ConstructionTrapCheck.NEVER_MONTHS` | `6` | The months the twin that never met the trap is played. |
| 150 | `ConstructionTrapCheck.SCRATCH_SLOT` | `10` | The slot the saved city is written to, in its own scratch folder (never the player's slots). |
| 1165 | `ConstructionTrapCheck.ROOM_LEAST` | `0.25, ROOM_MOST = 0.75` | The share of the Houses' points under which the cancel section stands a depot for the room, and the share it takes it down to by roads: a quarter and three quarters, a road's step (T x its repairs) far smaller than th... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 89 | `static int fails` |  |
| 90 | `static PrintStream out` |  |
| 91 | `static PrintStream quiet` |  |
| 153 | `static double worstResidual` | The worst month of the audit any month here saw, and the worst drift after it. |
| 154 | `static int monthsAudited` |  |
| 355 | `static int roadsAdded` | What the road loop left: the roads added, and the sites at every post before and after the one that tipped it. |
| 356 | `static double sitesBeforeTheLastRoad, sitesAfterTheLastRoad` |  |
| 802 | `static double billBeforeTheRows` | His bill in the month before the rows (FC2: the founding's, the Penitentiary's), which the first row's statement carries. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 87 | 1567 | **type** `public class ConstructionTrapCheck` | The builders short (FC1) and the foreign crews always on offer beside them (FC2): what an order pays a point of work as the repairs leave the builders fewer points for the sites, the queue at home past the threshold t... |
| 93 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 98 | 5 | `static void close(String label, double actual, double expected, double tol)` |  |
| 104 | 5 | `static void bits(String label, double actual, double expected)` |  |
| 110 | 5 | `static void quietly(Runnable r)` |  |
| 116 | 5 | `static BuildingsTemplate template(Game g, String name)` |  |
| 157 | 7 | `static void month(Game g)` | One month, audited. |
| 172 | 3 | **type** `record Row(int month, boolean trapped, double sites, double output, double points, double bill, double stmt...` | What one month played left: the trap as the month met it (the sites under SLIVER of the builders' month before it: FC1's floor, the fixture's depth since FC2, whose month's offer hands the overflow on and leaves the q... |
| 177 | 15 | `static Row play(Game g, List<Row> into)` | One month played, audited, and what it left, added to `into`. |
| 195 | 3 | `static double auditLine(MoneyAudit.Result r, String label)` | A line of the audit by its label, as the model recorded it (EP2: Result.figure(), not the detail's two printed decimals); 0 when it struck none. |
| 200 | 3 | `static GameFiles filesOf(Path root, String name)` | The files DeathCareFirstCheck.city() gives a city of that name, so a second Game can be pointed at its folder. |
| 204 | 28 | `public static void main(String[] args) throws Exception` |  |

### 1. THE PRICE'S SHAPE (lines 233-351)

| line | len | member | says |
|---:|---:|---|---|
| 235 | 116 | `static void shape()` |  |

### 2. THE TRAP, CAUSED (lines 352-475)

| line | len | member | says |
|---:|---:|---|---|
| 359 | 4 | `static double perRoad(Game g)` | The repairs one standing Paved Road adds to the builders' month (as DeathCareFirstCheck.sites() has it). |
| 365 | 3 | `static void roads(Game g, int n)` | n standing Paved Roads, as DeathCareFirstCheck.sites() builds them. |
| 374 | 16 | `static Game trapped(Path root, String name)` | DeathCareFirstCheck's city, then standing Paved Roads: two short of the model's own figure for the roads whose repairs leave the sites SLIVER of the builders' month, and then one at a time until they leave less - so t... |
| 392 | 11 | `static void sites(Game g)` | ...and the sites DeathCareFirstCheck.sites() puts on it, less its cemetery: the city's University and Middle Schools, the landlords' Houses; nobody lies unburied. |
| 404 | 71 | `static Game trap(Path root)` |  |

### 3. THE PRICE RISES AS THE FREE POINTS FALL (lines 476-692)

| line | len | member | says |
|---:|---:|---|---|
| 483 | 25 | `static Game calm(Path root, String name)` | A twin that never met the trap: the trap's city (DeathCareFirstCheck.city()) with its Penitentiary cancelled - its queue was the only thing in the way of a premium of none - and, in its place, the landlords' Houses on... |
| 513 | 2 | **type** `record State(String name, double sites, double months, double premium, double multiple, double sticker, dou...` | A House's quote at a state of the city: the free points, the months queued, the premium, the multiple, the sticker and the planners' cost; whether an order goes abroad, and (FC2) the foreign crews' first share and the... |
| 516 | 1 | `static State state(Game g, String name)` |  |
| 519 | 10 | `static State state(Game g, String name, boolean print)` | ...printed or not: the walk to the threshold reads a state at every road and prints the last. |
| 536 | 25 | `static boolean identity(Game g, double[] aheads, double[] eachs, int[] abroad)` | orderPriceMultiplier(x, each, 1) against FC1's one-building form for every pair (REV-FC2): to the bit orderPriceMultiplier(x) where the building stands within the threshold, and past it one and the share with its own ... |
| 567 | 4 | `static double abroadShare(BuildingManager bm, BuildingManager.ForeignTerms now, double points)` | The supply curve's share for {@code points} more on his book, from the terms as the city stands (REV-FC2): his month the book and them in the threshold's months, never under the largest depot - what an order wholly pa... |
| 573 | 4 | `static double ownShare(Game g, BuildingsTemplate t, long n)` | The foreign crews' share for an order of n of t placed now (REV-FC2, Game.abroadOfOrder()); NaN with no trade on. |
| 578 | 8 | `static void printState(Game g, State s)` |  |
| 588 | 5 | `static int roadsFor(Game g, double months)` | The standing roads that bring the months queued to `months` at the points the builders have now. |
| 594 | 98 | `static void priceRises(Path root)` |  |

### 4. THE FOREIGN CREWS TAKE THE DEPOT FIRST (lines 693-803)

| line | len | member | says |
|---:|---:|---|---|
| 695 | 105 | `static void depotFirst(Game g, List<Row> rows)` |  |

### 5. THE CITY CLIMBS OUT AND THE CONTRACTOR STOPS (lines 804-864)

| line | len | member | says |
|---:|---:|---|---|
| 806 | 58 | `static void climbsOut(Game g, List<Row> rows)` |  |

### 6. THE MONEY (lines 865-930)

| line | len | member | says |
|---:|---:|---|---|
| 867 | 6 | `static void audited(String where)` |  |
| 874 | 56 | `static void money(List<Row> rows)` |  |

### 7. A CITY THAT NEVER MEETS THE TRAP (lines 931-960)

| line | len | member | says |
|---:|---:|---|---|
| 933 | 27 | `static Game never(Path root)` |  |

### 8. SAVED AND LOADED (lines 961-1002)

| line | len | member | says |
|---:|---:|---|---|
| 963 | 39 | `static void saved(Path root, Game never) throws Exception` |  |

### 9. A CANCEL IN THE TRAP (lines 1003-1238)

| line | len | member | says |
|---:|---:|---|---|
| 1011 | 5 | `static int cityDepots(Game g, BuildingsTemplate depot)` | The Construction Depots the city orders in the trap: more points than the month has, the city's and the contractor's, so one is still on site when the month ends and the cancel has something to stop. |
| 1018 | 4 | `static ConstructionControl.Refund refundOf(Game g, String building)` | The refund the month's stop made for the building of that name (ConstructionControl.Refund), or null when it stopped none. |
| 1024 | 15 | `static void sameStart(String where, Game a, Game b)` | Two trapped cities that stand alike to the bit: the free points, the repairs, the queue, his book, his threshold and the overflow at home, and the treasury's cash. |
| 1040 | 123 | `static void cancelInTheTrap(Path root)` |  |
| 1168 | 4 | `static double roomOf(Game g)` | The room under the threshold the queue at home leaves in points: its months over the sites' month at every post, less the queue at home (BuildingManager.foreignTerms()). |
| 1174 | 7 | `static void moreHouses(Game g)` | The landlords' Houses ordered again, LANDLORDS_HOUSES of them, out of cash the quote sizes: the same in twins that stand alike. |
| 1187 | 51 | `static void cancelADepotFirst(Path root)` | A depot of the builders' ordered by the city and served first, and cancelled: FC1 never had it on his book, so the cancel took nothing off; since REV-FC2 an order's buildings past the threshold go on it as it is bille... |

### 10. A RUSH BESIDE THE CONTRACTOR (lines 1239-1351)

| line | len | member | says |
|---:|---:|---|---|
| 1242 | 4 | `static ConstructionControl.Overtime overtimeOf(Game g, String key)` | The overtime of the rushed site the month struck, with the plan it was struck in. |
| 1247 | 104 | `static void rushBesideTheContractor(Path root)` |  |

### 11. SAVED MID-TRAP, THE NEXT MONTH THE SAME (lines 1352-1406)

| line | len | member | says |
|---:|---:|---|---|
| 1354 | 52 | `static void nextMonthTheSame(Path root) throws Exception` |  |

### 12. AN ORDER'S OWN BUILDINGS (BA1) (lines 1407-1653)

| line | len | member | says |
|---:|---:|---|---|
| 1410 | 5 | `static double explicitMean(double from, double step, long n)` | The sums an order of n buildings is priced by, in the order the model sums them (BuildersShort.meanPremium() up to EXACT_SUM_MOST). |
| 1417 | 6 | `static double integralTolerance(double stepMonths)` | The tolerance meanPremium()'s javadoc states for the integral past EXACT_SUM_MOST, by the step in months (its MEASURED figures). |
| 1433 | 220 | `static void ordersOwnBuildings(Path root)` | AN ORDER'S OWN BUILDINGS (BA1). |

