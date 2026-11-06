# EquipmentFit.java - 280 lines · 15 methods · 1 constants · model

`ham/citybuildersim/EquipmentFit.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> What a piece of equipment would earn a building and what it would cost, and whether that pays: research-design 2.2's rule, steps 1 to 4, and
> each piece's verdict, read on demand and never in the month.
> 
> WHY (R1, 2026-11-05; docs/queue/R1-notes.md calls 10 and 11; Jerus: "equipment requires investment, thus sectors would not, and could not, even
> invest until it's actually worth it"). Nothing priced a refit before: a building had one way to run, and R0c's 1900 forms were shares nobody
> set. R1 slice A made the pieces data and counted the buildings still without each; this is the question R1b's order and R5's screen ask of a
> count, in the planner's own terms, for ONE building of a stack and one piece that fits it:
> 
>   1. g, what it adds a month: the building's spread with the piece less without it, at the city's prices (gain);
>   2. C, what it costs: what fitting buys at the city's prices, plus its work (cost);
>   3. it pays over its life: g x the annuity of its life at the sector's REAL rate is at least C;
>   4. it passes the money gate as a building does: g is at least the interest hurdle on its WHOLE cost.
> 
> Both of 3 and 4 must hold, and a gain of nothing never pays. The verdict (Verdict, Kind) is a record the screens print and R1b ranks;
> nothing in a played city's month calls any of this (since R1b only a city founded the 1900 way reads verdictForNew(), when a building opens:
> Game.fitWhatFinished()), and it books, buys and fits nothing (call 10). T is the piece's own life, not the lesser of that
> and the building's remaining life, because a stack keeps no building's age (call 11).
> 
> Two layers: a pure core (annuityFactor(), steamGain(), cartGain(), cost(), judge()) that takes its prices as arguments, so a harness can move
> one across the break-even, and the game layer (verdict(), rank()) that reads the city's.

**Uses:** [BuildingsTemplate](BuildingsTemplate.md) (9), [Equipment](Equipment.md) (9), [Game](Game.md) (7), [SpreadPlanner](SpreadPlanner.md) (6), [BusinessInvestment](BusinessInvestment.md) (5), [Sector](Sector.md) (4), [Good](Good.md) (4), [BuildingsStacks](BuildingsStacks.md) (3), [Haulage](Haulage.md) (2), [HydroSites](HydroSites.md) (1), [Markets](Markets.md) (1), [Recipes](Recipes.md) (1), [CityCalendar](CityCalendar.md) (1), [BusinessDebtManager](BusinessDebtManager.md) (1), [Fittings](Fittings.md) (1)

**Used by (5):** [EquipmentCheck](EquipmentCheck.md), [FittingCheck](FittingCheck.md), [Game](Game.md), [RecipeCheck](RecipeCheck.md), [SectorScreen](SectorScreen.md)

## Sections

| line | section |
|---:|---|
| 68 | THE PURE CORE: figures in, figures out |
| 142 | THE GAME LAYER: the city's prices, read on demand |

## Enum constants

| line | constant | says |
|---:|---|---|
| 40 | `EquipmentFit.Kind.FITTED` | No building of the stack lacks the piece. |
| 42 | `EquipmentFit.Kind.NOT_KNOWN` | Its "since" is not known in the city's year: a year not yet come, or a catalogue code (the tractor's PE14, always, in this build). |
| 44 | `EquipmentFit.Kind.PAYS` | Steps 3 and 4 both hold: over its life it earns back its cost, and a month's gain carries the interest hurdle on its whole cost. |
| 46 | `EquipmentFit.Kind.NOT_YET` | Either step fails (or its gain or cost cannot be had): earnsBack, over the cost, says how far short. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 211 | `EquipmentFit.BEST_FIRST` | `Comparator.comparingDouble(Verdict : : earningsOnCost).reversed().thenCompari...` | How R1b orders what pays: earnings on cost, the best first; the same earnings by template id and then piece id, so that no list order biases it. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 248 | **type** `public final class EquipmentFit` | What a piece of equipment would earn a building and what it would cost, and whether that pays: research-design 2.2's rule, steps 1 to 4, and each piece's verdict, read on demand and never in the month. |
| 35 | 1 | `private EquipmentFit()` |  |
| 38 | 10 | **type** `public enum Kind` | Where a piece stands for a stack: the screens' four verdicts (research-design 2.2). |
| 65 | 2 | **type** `public record Verdict(Kind kind, int templateId, String pieceId, double gain, double cost, int lifeMonths, ...` | One piece on one building of a stack, judged. |

### THE PURE CORE: figures in, figures out (lines 68-141)

| line | len | member | says |
|---:|---:|---|---|
| 75 | 5 | `public static double annuityFactor(int months, double annualRate)` | The level annuity's factor: the sum over t = 1..months of 1 / (1 + r / 12)^t, the present value of a unit a month for `months`. |
| 82 | 4 | `public static double steamCoalTonnes(BuildingsTemplate t)` | The tonnes of steam coal a building's engine would burn a month at nameplate: its grid draw (the units Sector bills, kW) in kWh at the engine's kilogrammes a kWh, in tonnes. |
| 92 | 4 | `public static double steamGain(BuildingsTemplate t, double coalPrice, double powerPrice)` | The electric drive's gain a month on one building (step 1): the steam coal its engine would burn at nameplate (steamCoalTonnes()) at the price of a tonne, less the grid power it would draw instead (the same units) at ... |
| 102 | 3 | `public static double cartGain(double truckRoom, double teamsPerTonne, double trucksPerTonne)` | The motor truck's gain a month on one depot (step 1): the tonnes its truck room carries a month at Haulage's own tonnes a truck, at what a tonne's city leg costs to run on teams (Haulage.horseEstimate()) less on truck... |
| 107 | 5 | `public static double cost(Equipment piece, ToDoubleFunction<Good> price, double pointPrice)` | What fitting one building costs (step 2): each good the piece buys at its price, plus the piece's work at the price of a point. |
| 114 | 4 | `private static Kind settled(Equipment piece, int year, int unfitted)` | FITTED when no building of the stack lacks the piece, NOT_KNOWN when it is not known in the year, else null: the two verdicts that price nothing. |
| 128 | 13 | `public static Verdict judge(Equipment piece, BuildingsTemplate t, int year, int unfitted, double gain, double cost, double annu...` | The verdict from the four steps, given the figures (a harness's, or the game layer's). |

### THE GAME LAYER: the city's prices, read on demand (lines 142-280)

| line | len | member | says |
|---:|---:|---|---|
| 145 | 4 | `private static double cityPrice(Game game, Good good)` | The city's price of a good: what the planners weigh it at (SpreadPlanner.cityValue() of its market); 0 for none. |
| 158 | 13 | `private static double gainOf(Game game, BuildingsTemplate t, Equipment piece)` | One building's gain a month from a piece, at the city's prices: the electric drive's from the coal's and the grid's price (the unit price the sectors are billed, EconomyManager.getPricePerWatt()), the motor truck's fr... |
| 180 | 3 | `public static Verdict verdict(Game game, BuildingsStacks stack, Equipment piece)` | The verdict on ONE building of a stack for a piece: FITTED when no building of the stack lacks it, NOT_KNOWN when it is not known in the city's year, else its gain and cost at the city's prices (the cost's work at wha... |
| 190 | 3 | `public static Verdict verdictForNew(Game game, BuildingsTemplate t, Equipment piece)` | The verdict on a NEW building for a piece (R1b; research-design 2.2 step 6, "New buildings come with every piece that pays"): the same figures and the same judgement as verdict() for one building that lacks the piece ... |
| 195 | 14 | `private static Verdict priced(Game game, BuildingsTemplate t, Equipment piece, int unfitted)` | verdict()'s and verdictForNew()'s one pricing: the template, the piece and how many of the buildings asked about lack it (0 for none: FITTED). |
| 219 | 14 | `public static List<Verdict> rank(Game game, Sector sector)` | Every piece that pays on the sector's stacks, best first: for each stack of the sector with a building unfitted that is not already being fitted (the unfitted count less the fittings on site, R1b), each such piece who... |
| 249 | 31 | `public static BusinessInvestment.Decision choose(Game game, Sector sector, BusinessInvestment.Decision plan)` | THE ORDER, ONE AT A TIME (R1b; research-design 2.2 step 5: "It competes with new buildings. |

