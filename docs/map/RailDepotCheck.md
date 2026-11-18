# RailDepotCheck.java - 697 lines · 16 methods · 1 constants · harnesses

`ham/citybuildersim/RailDepotCheck.java` - generated 2026-11-18 by CodeMap; line numbers are as of that run.

> The railway's stations: the Rail Depot, and the rules that put a station
> first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.md
> 1.1; sectors.Freight, THE STATIONS).
> 
> WHY THIS HARNESS EXISTS. Before FR7 the Rail Spur, the Freight Line and the
> Rail Terminal were one ladder of tonnes, capacity was the least of the
> track and the wagon sets, and plan() bought the best profit per cost whose
> tonnes the unserved freight filled to MIN_LINE_UTILISATION: a young city
> bought spurs and lines, a terminal late, and track ran out into the
> country to no yard. Jerus: "needs one terminal and then spurs". RailCheck
> holds the railway's money; this holds who may be ordered, in what order, and
> what the stations let through. What it has to prove:
> 
>   1. THE RAIL DEPOT IS IN THE CATALOGUE on the ladder's per-tonne rule, a
>      tenth of a terminal in everything else, and the new template fields
>      (stationTonnes, siding) are on the buildings they belong to and no
>      others.
>   2. A YOUNG CITY'S FIRST RAIL ORDER IS A RAIL DEPOT, at its threshold and
>      not below it, however many tonnes there are: never a terminal first.
>   3. NO SPUR OR LINE BEFORE A STATION, whatever track the city already has.
>   4. THE STATIONS CAP WHAT IS RAILED: goods only through a station, bulk
>      past the stations on the spurs' sidings, and nothing at all with
>      neither - first as the dealing's arithmetic, then in a played city's
>      months.
>   5. A SPUR ONLY FOR A WORKS WITHOUT A SIDING: the bulk-works predicate
>      (counted and named), a spur ordered while a bulk works has none and
>      the track and the sidings are short, none when every works has one or
>      the city has no works at all.
>   6. THE OVERBUILD: while the wagon sets limit the capacity, no track is
>      ordered against track that already stands. The old plan() ordered a
>      Rail Spur in this very fixture (measured before the rule changed; the
>      old measure is recomputed here from the model's own figures).
>   7. A TERMINAL FOLLOWS when the tonnes outgrow the depots, at its own
>      threshold.
>   8. THE LAST STATION IS NOT RETIRED while a spur or a line stands.
>   9. THE LAST STATION IS KEPT WHILE IT HAS A STATION'S FREIGHT (FD1): a sale
>      the planner would buy back (the freight left unserved is a first
>      station's rung) is refused, by the distress rule as by any other, and
>      allowed a tonne under it or with a second station standing.
>   10. SPARE TRACK IS COUNTED AS TRACK (RT2), even while wagon sets or
>      stations limit operating capacity; losses sell the most-held line
>      by the headroom and gradual-retirement constants, without cutting
>      into the track the offered freight needs. Goods retain station room:
>      spare sidings cannot replace a goods yard, even when sets bind.
> 
> The fixtures are bare cities (a catalogue, a planner, a Freight with nothing
> else), because plan() asks for nothing a city adds except the staffing,
> which a bare city passes, and because the month's tonnes must be what the
> fixture says (Freight.setMonthForTest()). An assertion whose subject is a
> played city's trade is an assertion about the trade; section 4 plays one
> only for what the stations let through. Every threshold is caused from both
> sides, and every figure comes from the templates and the model's constants.

**Uses:** [BusinessInvestment](BusinessInvestment.md) (45), [Freight](Freight.md) (43), [Traffic](Traffic.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (12), [HaulageCheck](HaulageCheck.md) (4), [BuildingManager](BuildingManager.md) (2), [Mining](Mining.md) (2), [Good](Good.md) (2), [JobType](JobType.md) (2), [PriceLevel](PriceLevel.md) (1), [EconomyManager](EconomyManager.md) (1), [Sector](Sector.md) (1), [Markets](Markets.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1), [Game](Game.md) (1), [GameFiles](GameFiles.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [HomeRailCheck](HomeRailCheck.md)

## Sections

| line | section |
|---:|---|
| 106 | THE FIXTURE: a bare city |
| 167 | · 1. THE RAIL DEPOT IN THE CATALOGUE |
| 228 | · 2. A YOUNG CITY'S FIRST RAIL ORDER IS A RAIL DEPOT |
| 251 | · 3. NO SPUR OR LINE BEFORE A STATION |
| 276 | · 4. THE STATIONS CAP WHAT IS RAILED |
| 370 | · 5. A SPUR ONLY FOR A WORKS WITHOUT A SIDING |
| 449 | · 6. THE OVERBUILD |
| 497 | · 7. A TERMINAL FOLLOWS WHEN THE TONNES OUTGROW THE DEPOTS |
| 516 | · 8. THE LAST STATION IS NOT RETIRED WHILE TRACK STANDS |
| 537 | · 9. THE LAST STATION IS KEPT WHILE IT HAS A STATION'S FREIGHT (FD1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `RailDepotCheck.LORRY_A_TONNE` | `.319 * PriceLevel.FACTOR` | What a lorry charges for a tonne in the model's money: BuildingManager's RAIL banner measures $319 a tonne in the played city, and the fixtures' freight is the planner's to value, not this harness's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `static int fails` |  |
| 112 | `final BuildingManager bm` |  |
| 113 | `final BusinessInvestment plans` |  |
| 114 | `final Freight fr` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 633 | **type** `public class RailDepotCheck` | The railway's stations: the Rail Depot, and the rules that put a station first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.md 1.1; sectors.Freight, THE STATIONS). |
| 72 | 7 | `static void quietly(Runnable r)` |  |
| 80 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 85 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 90 | 3 | `static boolean near(double a, double b, double tol)` |  |
| 95 | 5 | `static double dealtNow(Freight fr)` | The tonnes the last haul dealt the railway: this month's bulk and goods offered at the shares it set for the month to come. |
| 102 | 3 | `static String say(BusinessInvestment.Decision d)` | What a decision says, in a few words. |

### THE FIXTURE: a bare city (lines 106-697)

| line | len | member | says |
|---:|---:|---|---|
| 111 | 40 | **type** `static final class Fx` | A catalogue, a planner with all the ground it wants, and a Freight attached to them: no city, so the staffing test passes. |
| 116 | 6 | `Fx()` _(in RailDepotCheck.Fx)_ |  |
| 123 | 1 | `BuildingsTemplate t(String name)` _(in RailDepotCheck.Fx)_ |  |
| 126 | 4 | `Fx stand(String name, int n)` _(in RailDepotCheck.Fx)_ | n of the named building standing. |
| 132 | 4 | `Fx works(int n)` _(in RailDepotCheck.Fx)_ | n bulk works standing: iron mines. |
| 138 | 3 | `Fx offer(double bulk, double goods)` _(in RailDepotCheck.Fx)_ | A month's offer to the railway, the wagon sets enough for the track standing. |
| 143 | 5 | `Fx offer(double bulk, double goods, double sets)` _(in RailDepotCheck.Fx)_ | ...with this many wagon sets, however many the track needs. |
| 149 | 1 | `BusinessInvestment.Decision plan()` _(in RailDepotCheck.Fx)_ |  |
| 152 | 449 | `public static void main(String[] args)` |  |
| 603 | 58 | `static void retirement()` | The operating bottleneck must not hide spare standing track (RT2). |
| 663 | 34 | `static void goodsRetirement()` | A siding sorts bulk only: the aggregate operating minimum alone can hide a goods shortfall. |

