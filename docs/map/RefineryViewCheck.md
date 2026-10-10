# RefineryViewCheck.java - 626 lines · 23 methods · 6 constants · harnesses

`ham/citybuildersim/RefineryViewCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The refinery's pictogram (0.7.95, batch O11; runs/spec-oil.md 2.12):
> RefineryView's figures and its picture, held to the model's own reads in
> played towns.
> 
> WHY. Refining's Operations page draws the refinery as a picture since
> 0.7.95 - crude in, the column's cuts, the units, the tank by product, who
> took each - every ribbon to scale. A ribbon that did not foot to the flow,
> a product band that was not what the month made, a taker the market never
> sold to, or a ribbon drawn out of scale would be a confident wrong picture.
> The screen is checked by eye on the PC; this holds what it draws.
> 
> What this has to prove:
>   1. THE FLOW, TRACED, FOOTS: on every grade and on mixes, with no unit and
>      with a whole campus, the links into each unit are its run, into each
>      product the flow's make (bitumen and coke at the residue's litres a
>      tonne), out of each cut the cut, and to the furnaces the residue
>      burned - each within a billionth; and a unit feeds another (cracked
>      gas to alkylation, naphtha to the reformers) only where the flow's
>      pools carry it.
>   2. THE MONTH IS THE ONE THE PRODUCTS WERE MADE ON: the view's flow is the
>      one the production rows read (their capacity, to the bit), and each
>      product's run less what idled is its row's made.
>   3. THE CRUDE: the run is the crude units' nameplate at the month's rate;
>      its sources' tonnes are the run; what each was bought is crude's
>      clearing's, and they come to the refiners' row; the wells standing are
>      counted; the column's cuts are the run's litres.
>   4. THE TAKERS ARE THE BUYERS' OWN ROWS: what the refinery sold at home is
>      every buyer's purchase from it, the railway's haul apart; the cars are
>      the forecourts' month; the railway's diesel is its haul's tonnes at
>      FUEL_LITRES_PER_TONNE; and the tanks take what was made and not taken,
>      which is what the refiners' stock moved by over the month.
>   5. THE PICTURE IS TO SCALE: every ribbon leaves one node's right edge and
>      reaches another's left, inside each; every node's ribbons foot to its
>      litres times the scale - a unit's in its run, a product's band its
>      make, a taker's bar what it took; everything lies in the picture, and
>      its tallest column fills it; the takers' labels stand LABEL_SPACING
>      apart where there is room.
>   6. THE FURNACES: on heavy crude with no unit, the residue the diesel
>      cannot cut is burned and drawn to the furnaces; on medium none is.
>   7. NOTHING TO DRAW: a town with no crude unit, and a city just loaded,
>      say why and draw nothing; a month on, the loaded city's view is its
>      twin's.
>   8. PURE: read twice, the same view, and reading it makes no production
>      row.
> 
> Every fixture causes its condition.

**Uses:** [RefineryView](RefineryView.md) (126), [Good](Good.md) (25), [Game](Game.md) (16), [Refining](Refining.md) (11), [RefineryFlow](RefineryFlow.md) (10), [GameFiles](GameFiles.md) (6), [Sector](Sector.md) (6), [BuildingManager](BuildingManager.md) (4), [Deposit](Deposit.md) (4), [Sectors](Sectors.md) (3), [BuildingsTemplate](BuildingsTemplate.md) (2), [SpreadPlanner](SpreadPlanner.md) (2), [Rail](Rail.md) (2), [Founding](Founding.md) (1), [LandManager](LandManager.md) (1), [Resource](Resource.md) (1), [LongPlaytest](LongPlaytest.md) (1), [Oil](Oil.md) (1), [Trade](Trade.md) (1), [Retail](Retail.md) (1)

**Used by (1):** [OilViewCheck](OilViewCheck.md)

## Sections

| line | section |
|---:|---|
| 184 | 1. THE TRACE |
| 277 | 2. THE MONTH |
| 325 | 3. THE CRUDE |
| 361 | 4. THE TAKERS |
| 427 | 5. THE SCALE |
| 538 | 6. THE FURNACES |
| 563 | 7. NOTHING TO DRAW |
| 600 | 8. PURE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 95 | `RefineryViewCheck.PX` | `1e-6` | ...and a picture's pixels: within a millionth of one. |
| 97 | `RefineryViewCheck.FILES` | `new java.util.IdentityHashMap<>()` |  |
| 100 | `RefineryViewCheck.CAMPUS` | `{ "Small Reformer", "Small Cracking Unit", "Small Hydrocracker", "Small Alkyl...` | The units of the campus town: one small of each kind but the asphalt unit (heavy crude's), so the cracking unit and the coker feed alkylation and the hydrocracker and the coker the reformer. |
| 104 | `RefineryViewCheck.WELLS` | `2` | Land wells on fiat sites in the campus town - fewer than its refinery runs on, so it imports the rest - and the oil each site holds. |
| 105 | `RefineryViewCheck.SITE_TONNES` | `5_000_000` |  |
| 108 | `RefineryViewCheck.MONTHS` | `24` | Months the towns play before they are read: two years, so the households own cars and drive (OilCheck 4's). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 69 | `static int fails` |  |
| 70 | `static PrintStream out` |  |
| 71 | `static PrintStream quiet` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 67 | 560 | **type** `public class RefineryViewCheck` | The refinery's pictogram (0.7.95, batch O11; runs/spec-oil.md 2.12): RefineryView's figures and its picture, held to the model's own reads in played towns. |
| 73 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 78 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 83 | 5 | `static void quietly(Runnable r)` |  |
| 90 | 3 | `static boolean near(double a, double b)` | Within a billionth of the larger, or of one. |
| 116 | 28 | `static Game town(String label, int wells, String...units)` | OilCheck's town (houses, shops, bakeries, power, water, roads and builders), with a rail spur and two filling stations, an Oil Refinery and `units`, and `wells` land wells on fiat sites; the refiners held, so what sta... |
| 145 | 31 | `public static void main(String[] args) throws Exception` |  |
| 177 | 6 | `static void printTown(Game g, RefineryView.View v)` |  |

### 1. THE TRACE (lines 184-276)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 5 | `static double[] campus(BuildingManager b, int n)` | Each kind's feed: its small and its large unit, `n` of each, from the catalogue (RefineryCheck's campus). |
| 194 | 23 | `static String foots(RefineryFlow.Flow f, double rate)` | Whether the links of `f` traced at `rate` foot to it: into each unit its run, into each product its make, out of each cut the cut, to the furnaces the burned. |
| 218 | 46 | `static void theTrace()` |  |
| 265 | 5 | `static double between(List<RefineryView.Link> links, Kind from, Kind to)` |  |
| 271 | 5 | `static double[] only(Kind k, double litres)` |  |

### 2. THE MONTH (lines 277-324)

| line | len | member | says |
|---:|---:|---|---|
| 279 | 39 | `static void theMonth(Game g, RefineryView.View v)` |  |
| 320 | 4 | `static boolean same(ham.citybuildersim.sectors.SpreadPlanner.Candidate a, ham.citybuildersim.sectors.SpreadPlanner.Candidate b)` | Whether two appraisals of a building are the same: its template, earnings, cost and first refusal. |

### 3. THE CRUDE (lines 325-360)

| line | len | member | says |
|---:|---:|---|---|
| 327 | 33 | `static void theCrude(Game g, RefineryView.View v)` |  |

### 4. THE TAKERS (lines 361-426)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 58 | `static void theTakers(Game g, RefineryView.View v, double[] stockBefore)` |  |
| 422 | 4 | `static RefineryView.Buyer buyer(RefineryView.View v, RefineryView.BuyerKind k)` |  |

### 5. THE SCALE (lines 427-537)

| line | len | member | says |
|---:|---:|---|---|
| 429 | 102 | `static void theScale(RefineryView.View v, RefineryView.Picture p, String which)` |  |
| 532 | 5 | `static void check(List<String> wrong, Map<String, double[]> sides, String key, int side, double want)` |  |

### 6. THE FURNACES (lines 538-562)

| line | len | member | says |
|---:|---:|---|---|
| 540 | 22 | `static void theFurnaces(RefineryView.View medium)` |  |

### 7. NOTHING TO DRAW (lines 563-599)

| line | len | member | says |
|---:|---:|---|---|
| 565 | 34 | `static void nothingToDraw(Game g, RefineryView.View v)` |  |

### 8. PURE (lines 600-626)

| line | len | member | says |
|---:|---:|---|---|
| 602 | 10 | `static void pure(Game g)` |  |
| 614 | 12 | `static String words(RefineryView.View v)` | A view and its picture as text: every figure and every word. |

