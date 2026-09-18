# FoodProcessing.java - 539 lines · 12 methods · 1 constants · sectors

`ham/citybuildersim/sectors/FoodProcessing.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The plants between the farm and the shelf.
> 
> THE ELEVENTH SECTOR (2026-09-16, Jerus's call): "a new sector which makes the
> ready meals, processed meals, snacks, drinks, and cooking fats".
> 
> Those five are a THIRD OF THE SHELF BY WEIGHT and a third of the food bill by
> value - 16.3kg of the reference basket's 50, and $51.40 of its $161.75 - and
> until today every gram of them was imported. The city could grow the meat and
> bake the bread and still buy its sausages, its crisps, its cooking oil and
> its drinks from abroad, because nothing here turned one food into another.
> 
> WHAT IT CHANGES, and it is bigger than five goods: THE LIVESTOCK FARM FINALLY
> HAS A CUSTOMER AT HOME. Agriculture's meat went to the shelf or abroad and
> nowhere else, which made the dearest building in the catalogue a pure export
> play. The Meat Works buys it. That is the same sentence as a mine next door
> to a mill and a mill next door to a fabricator, one chain over, and it is the
> fourth reason in this game to put two things near each other.
> 
> EACH GOOD BUYS ITS OWN INPUT, which was Jerus's call over the simpler "crops
> only", and it is what gives the three plants three different cost stories:
> 
>   Meat Works        meat, vegetables and grains -> processed meats, ready meals
>   Snack & Oils      crops                       -> snacks, cooking fats
>   Bottling Plant    crops                       -> drinks
> 
> TWO BRAKES, NOT ONE, and that is the design - the same shape Manufacturing
> took. The Meat Works is 40% meat by revenue at mid-band and 46% at the floor,
> so THE MEAT PRICE decides it: measured at month 98 of a real city, the same
> building is worth $16,559 a month on meat at a farm's export floor and $1,678
> on meat off a ship. TEN TIMES, on one price. The Bottling Plant is 4% crops
> and 21% wages, so THE WAGE BILL AND THE WATER decide it - a drink is mostly
> water, and the plant is the first building in the game whose utility bill is
> a real line rather than a rounding error. The Snack & Oils Plant sits between
> them, 19% crops, and it is the widest mark-up in the catalogue: crisps really
> are $10 a kilo pressed out of $0.44 potatoes. A city priced out of one rung
> can still build another, which is the whole reason there are three.
> 
> IT DOES NOT USE WHAT IT MAKES, deliberately. Crisps are fried in oil and this
> sector presses oil, so FATS is the obvious input for SNACKS - and it is left
> out for the reason Agriculture leaves the cows' feed out: a sector that makes
> a good and uses it hands its other outputs a share of that line's bill
> through the joint-cost split, and the market would clear it without ever
> saying so. See Sector.costShareOf().
> 
> Everything else is the generic template: it plans output against the shops'
> demand rather than flooding its own shed, withholds below marginal cost,
> dumps above the shed's line, and ships spare nameplate abroad when the
> world's price clears the power it costs.

**Uses:** [Good](Good.md) (22), [BusinessInvestment](BusinessInvestment.md) (12), [Sector](Sector.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (4), [Game](Game.md) (3), [Formats](Formats.md) (3), [GoodsMarket](GoodsMarket.md) (2), [BuildingType](BuildingType.md) (1), [TaxPolicy](TaxPolicy.md) (1)

**Used by (2):** [FoodProcessingCheck](FoodProcessingCheck.md), [Sectors](Sectors.md)

## Sections

| line | section |
|---:|---|
| 101 | THREE PLANTS, FIVE GOODS, AND A PLANNER THAT COULD ONLY SEE ONE |
| 150 | · NOBODY BUILDS A FOOD PLANT ON EXPORTS |
| 264 | WHAT A PLANT WOULD EARN, AT THE PRICE IT WILL LEAVE BEHIND |
| 377 | · the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `FoodProcessing.FILLED` | `.85` | How full a new plant has to be before anybody finances one. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 63 | 477 | **type** `public final class FoodProcessing extends Sector` | The plants between the farm and the shelf. |
| 65 | 27 | `public FoodProcessing()` |  |

### THREE PLANTS, FIVE GOODS, AND A PLANNER THAT COULD ONLY SEE ONE (lines 101-149)

### NOBODY BUILDS A FOOD PLANT ON EXPORTS (lines 150-263)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 12 | `public double fillFor(BuildingsTemplate t)` | How much of one of these the city would eat today, as a share of what it makes. |
| 198 | 65 | `public BusinessInvestment.Decision plan(BusinessInvestment plans, Game game)` |  |

### WHAT A PLANT WOULD EARN, AT THE PRICE IT WILL LEAVE BEHIND (lines 264-376)

| line | len | member | says |
|---:|---:|---|---|
| 315 | 7 | `private Sector makerOf(Good g)` | Whoever else in the city makes this, or null when only the world does. |
| 324 | 52 | `public double estimatedMonthlyProfit(BuildingsTemplate t, BusinessInvestment plans)` |  |

### the screen (lines 377-539)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 93 | `public java.util.List<Sector.Line> operations(Game game)` | What the generic page cannot say: WHICH PRICE IS DECIDING THIS SECTOR. |
| 488 | 3 | `public double getMeatDemand()` | Kilograms of meat the plants want this month, at the rate they are running. |
| 493 | 3 | `public double getMeatPrice()` | What a kilogram is costing them - the number that decides whether a Meat Works pays here. |
| 502 | 3 | `public double getMeatPosition()` | Where the meat price sits between a farm's export floor and the world's delivered ceiling: 0 is a city with herds and nobody else to sell to, 1 is a city with no farms at all. |
| 507 | 4 | `public double inputShare()` | The input bill as a share of what the plants sold. |
| 513 | 4 | `public double payrollShare()` | ...and the wage bill, the same way. |
| 528 | 11 | `public double[] retirementDemandAndCapacity(Game game)` | Spare capacity, measured in money rather than in kilograms. |

