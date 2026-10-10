# OilViewCheck.java - 706 lines · 20 methods · 3 constants · harnesses

`ham/citybuildersim/OilViewCheck.java` - generated 2026-10-10 by CodeMap; line numbers are as of that run.

> The oil industry screen (0.7.96, batch O12; runs/spec-oil.md 2.13):
> OilView's figures, words, chart and levers, held to the model's own reads
> in played towns.
> 
> WHY. Oil's Operations page draws the whole chain since 0.7.96 - the wells
> by kind and what they would lift over ten years, the refinery's units with
> their spreads and gates, every product's month, the city's reserve and its
> two levers. A forecast that did not decline as the wells do, a pool lifted
> past its oil, a unit's gate that was not the planner's, a product row that
> disagreed with Refining's picture, or a Fill that ordered what the game
> would not, would be a confident wrong page. The screen is checked by eye on
> the PC; this holds what it shows.
> 
> What this has to prove:
>   1. THE WELLS AHEAD, PURE: the platform wells are dealt to the platforms
>      oldest to oldest, each its wells in slots; a vintage asks its wells x
>      the nameplate x its profile, nothing once worn out; a land well's year
>      is nine tenths of the year before and a platform's holds three years
>      then keeps PLATFORM_KEEPS_A_YEAR; each pool's lift stops at its oil,
>      to the tonne, and the offshore pool is shared by the platforms' asks.
>   2. THE WELLS IN A TOWN: the counts, the pools and the fields are Oil's
>      and the land office's; the month's lift by series is the month's by
>      pool; the vintages dealt to the platforms ask the nameplate the month
>      was struck on, and month by month the wells lift that at the month's
>      rate - the forecast's own arithmetic - with nothing new built; and the
>      years ahead never lift a pool past its oil.
>   3. THE UNITS: the crude units, then every kind, standing first; a
>      standing kind's run, share and gate are the refinery picture's; every
>      kind's spread is RefineryFlow.spread() at the city's values; a kind
>      not standing has the planner's appraisal of one more, its spare feed
>      the outlook's; the crude units' fuel short and crude spare the
>      outlook's; and each gate is worded by its own pill.
>   4. THE PRODUCTS: crude made is the wells' month, used the refiners', its
>      imports the clearing's; each product's month and takers are the
>      refinery picture's, to the bit; the prices are the markets'; and each
>      product's world price over crude's is the research's ladder.
>   5. THE FIGURES: the lift in barrels a day, the two pools' tonnes, the
>      crude bought abroad, and the products across the edge as the Trade tab
>      reads them (Game.getTradeByGood()), to the bit.
>   6. THE RESERVE AND ITS LEVERS: the reserve is StrategicReserve's; Fill's
>      reach is what Game.fillReserve() orders - the room left, or what the
>      treasury can pay at crude's import price - and an order past it is cut
>      to it; what a lever would do is the arithmetic of the order; a month
>      on, the crude held is what the clearing bought; with no reserve none
>      is shown.
>   7. THE CHART TO SCALE: each year's bar foots to its series' barrels a
>      day times the scale; the month solid and the years ahead pale; inside
>      the chart; the years under every fifth bar; with nothing to lift, the
>      chart says so.
>   8. NOT COUNTED, AND PURE: a city just loaded counts no month - the lift,
>      the crude and the products say so - while its wells, pools, units and
>      reserve read; a month on it counts; read twice the same page; reading
>      it every month leaves a twin's month unchanged.
> 
> Every fixture causes its condition: the sea town buys its sea field and
> is handed dry oil by fiat (WellCheck's), the campus town stands its units
> (RefineryViewCheck's), the reserve is stood and filled.

**Uses:** [OilView](OilView.md) (156), [Good](Good.md) (30), [RefineryView](RefineryView.md) (25), [Game](Game.md) (19), [Oil](Oil.md) (15), [SpreadPlanner](SpreadPlanner.md) (11), [RefineryViewCheck](RefineryViewCheck.md) (8), [Refining](Refining.md) (5), [Sectors](Sectors.md) (5), [WellCheck](WellCheck.md) (4), [GameFiles](GameFiles.md) (4), [RefineryFlow](RefineryFlow.md) (4), [BuildingsTemplate](BuildingsTemplate.md) (4), [LandManager](LandManager.md) (3), [Resource](Resource.md) (3), [Sector](Sector.md) (3), [GoodsMarket](GoodsMarket.md) (3), [StrategicReserve](StrategicReserve.md) (3), [LongPlaytest](LongPlaytest.md) (2), [BuildingManager](BuildingManager.md) (1), [Founding](Founding.md) (1), [Trade](Trade.md) (1), [CityCalendar](CityCalendar.md) (1)

## Sections

| line | section |
|---:|---|
| 194 | 1. THE WELLS AHEAD |
| 264 | 2. THE WELLS IN A TOWN |
| 348 | 3. THE UNITS |
| 434 | 4. THE PRODUCTS |
| 489 | 5. THE FIGURES |
| 522 | 6. THE RESERVE AND ITS LEVERS |
| 596 | 7. THE CHART |
| 643 | 8. NOT COUNTED, AND PURE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 105 | `OilViewCheck.GROUND` | `2_000_000` | The oil handed to the sea town's two dry sites by fiat: the ground pool, plenty for the sections' months. |
| 108 | `OilViewCheck.FILL` | `40_000` | The fill the sea town orders for its reserve. |
| 111 | `OilViewCheck.SEA_MONTHS` | `14` | Months the sea town plays before it is read: past a year, so its platform's wells are in their plateau's second year. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 79 | `static int fails` |  |
| 80 | `static PrintStream out` |  |
| 81 | `static PrintStream quiet` |  |
| 581 | `static Game bare` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 77 | 630 | **type** `public class OilViewCheck` | The oil industry screen (0.7.96, batch O12; runs/spec-oil.md 2.13): OilView's figures, words, chart and levers, held to the model's own reads in played towns. |
| 83 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 88 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 93 | 5 | `static void quietly(Runnable r)` |  |
| 100 | 3 | `static boolean near(double a, double b)` | Within a billionth of the larger, or of one. |
| 113 | 35 | `public static void main(String[] args) throws Exception` |  |
| 149 | 7 | `static void printTown(String name, Game g, OilView.View v)` |  |
| 163 | 30 | `static Game seaTown()` | WellCheck's sea town (its sea field bought, every sector held), two dry sites handed GROUND by fiat beside it, a jacket and a well in each of its slots, two land wells, a Strategic Reserve with FILL ordered; SEA_MONTH... |

### 1. THE WELLS AHEAD (lines 194-263)

| line | len | member | says |
|---:|---:|---|---|
| 196 | 67 | `static void theWellsAhead()` |  |

### 2. THE WELLS IN A TOWN (lines 264-347)

| line | len | member | says |
|---:|---:|---|---|
| 266 | 75 | `static void theWellsInATown(Game g, OilView.View v)` |  |
| 342 | 5 | `static List<Vintage> landOf(List<Vintage> vs)` |  |

### 3. THE UNITS (lines 348-433)

| line | len | member | says |
|---:|---:|---|---|
| 350 | 74 | `static void theUnits(Game g, OilView.View v)` |  |
| 426 | 7 | `static String[] state(BuildingsTemplate t, SpreadPlanner.Gate gate, boolean crude, int standing, int onSite, double spread)` | The pill a fixture row gets: `standing` and `onSite` buildings, one more failing `gate` (null: passes), the crude units' if `crude`, at a spread. |

### 4. THE PRODUCTS (lines 434-488)

| line | len | member | says |
|---:|---:|---|---|
| 436 | 52 | `static void theProducts(Game g, OilView.View v)` |  |

### 5. THE FIGURES (lines 489-521)

| line | len | member | says |
|---:|---:|---|---|
| 491 | 30 | `static void theFigures(Game g, OilView.View v)` |  |

### 6. THE RESERVE AND ITS LEVERS (lines 522-595)

| line | len | member | says |
|---:|---:|---|---|
| 524 | 49 | `static void theReserve(Game g)` |  |
| 574 | 6 | `static<T> T quietlyGet(java.util.function.Supplier<T> s)` |  |
| 584 | 11 | `static Game bareTown()` | A city founded and played a month: no well, no refinery, no reserve. |

### 7. THE CHART (lines 596-642)

| line | len | member | says |
|---:|---:|---|---|
| 598 | 44 | `static void theChart(OilView.View v)` |  |

### 8. NOT COUNTED, AND PURE (lines 643-706)

| line | len | member | says |
|---:|---:|---|---|
| 645 | 46 | `static void notCountedAndPure(Game g) throws Exception` |  |
| 693 | 13 | `static String words(OilView.View v)` | A page as text: every figure and every word it writes. |

