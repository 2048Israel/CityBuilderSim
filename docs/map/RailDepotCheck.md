# RailDepotCheck.java - 519 lines · 14 methods · 1 constants · harnesses

`ham/citybuildersim/RailDepotCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

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
> 
> The fixtures are bare cities (a catalogue, a planner, a Freight with nothing
> else), because plan() asks for nothing a city adds except the staffing,
> which a bare city passes, and because the month's tonnes must be what the
> fixture says (Freight.setMonthForTest()). An assertion whose subject is a
> played city's trade is an assertion about the trade; section 4 plays one
> only for what the stations let through. Every threshold is caused from both
> sides, and every figure comes from the templates and the model's constants.

**Uses:** [Freight](Freight.md) (36), [BusinessInvestment](BusinessInvestment.md) (24), [Traffic](Traffic.md) (12), [BuildingsTemplate](BuildingsTemplate.md) (8), [HaulageCheck](HaulageCheck.md) (4), [BuildingManager](BuildingManager.md) (2), [Mining](Mining.md) (2), [Good](Good.md) (2), [JobType](JobType.md) (2), [EconomyManager](EconomyManager.md) (1), [Sector](Sector.md) (1), [Markets](Markets.md) (1), [Sectors](Sectors.md) (1), [BuildingType](BuildingType.md) (1), [Game](Game.md) (1), [GoodsMarket](GoodsMarket.md) (1)

**Used by (1):** [HomeRailCheck](HomeRailCheck.md)

## Sections

| line | section |
|---:|---|
| 97 | THE FIXTURE: a bare city |
| 158 | · 1. THE RAIL DEPOT IN THE CATALOGUE |
| 219 | · 2. A YOUNG CITY'S FIRST RAIL ORDER IS A RAIL DEPOT |
| 242 | · 3. NO SPUR OR LINE BEFORE A STATION |
| 267 | · 4. THE STATIONS CAP WHAT IS RAILED |
| 346 | · 5. A SPUR ONLY FOR A WORKS WITHOUT A SIDING |
| 425 | · 6. THE OVERBUILD |
| 473 | · 7. A TERMINAL FOLLOWS WHEN THE TONNES OUTGROW THE DEPOTS |
| 492 | · 8. THE LAST STATION IS NOT RETIRED WHILE TRACK STANDS |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `RailDepotCheck.LORRY_A_TONNE` | `.319` | What a lorry charges for a tonne in the model's money: BuildingManager's RAIL banner measures $319 a tonne in the played city, and the fixtures' freight is the planner's to value, not this harness's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 58 | `static int fails` |  |
| 103 | `final BuildingManager bm` |  |
| 104 | `final BusinessInvestment plans` |  |
| 105 | `final Freight fr` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 464 | **type** `public class RailDepotCheck` | The railway's stations: the Rail Depot, and the rules that put a station first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.md 1.1; sectors.Freight, THE STATIONS). |
| 63 | 7 | `static void quietly(Runnable r)` |  |
| 71 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 76 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 81 | 3 | `static boolean near(double a, double b, double tol)` |  |
| 86 | 5 | `static double dealtNow(Freight fr)` | The tonnes the last haul dealt the railway: this month's bulk and goods offered at the shares it set for the month to come. |
| 93 | 3 | `static String say(BusinessInvestment.Decision d)` | What a decision says, in a few words. |

### THE FIXTURE: a bare city (lines 97-519)

| line | len | member | says |
|---:|---:|---|---|
| 102 | 40 | **type** `static final class Fx` | A catalogue, a planner with all the ground it wants, and a Freight attached to them: no city, so the staffing test passes. |
| 107 | 6 | `Fx()` _(in RailDepotCheck.Fx)_ |  |
| 114 | 1 | `BuildingsTemplate t(String name)` _(in RailDepotCheck.Fx)_ |  |
| 117 | 4 | `Fx stand(String name, int n)` _(in RailDepotCheck.Fx)_ | n of the named building standing. |
| 123 | 4 | `Fx works(int n)` _(in RailDepotCheck.Fx)_ | n bulk works standing: iron mines. |
| 129 | 3 | `Fx offer(double bulk, double goods)` _(in RailDepotCheck.Fx)_ | A month's offer to the railway, the wagon sets enough for the track standing. |
| 134 | 5 | `Fx offer(double bulk, double goods, double sets)` _(in RailDepotCheck.Fx)_ | ...with this many wagon sets, however many the track needs. |
| 140 | 1 | `BusinessInvestment.Decision plan()` _(in RailDepotCheck.Fx)_ |  |
| 143 | 376 | `public static void main(String[] args)` |  |

