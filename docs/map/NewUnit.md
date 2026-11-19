# NewUnit.java - 185 lines · 29 methods · 1 constants · model

`ham/citybuildersim/NewUnit.java` - generated 2026-11-19 by CodeMap; line numbers are as of that run.

> One new building of a template, as it would run (QN1): the recipe, posts, utility draw, output and input of a building
> ordered now - the pieces it would be fitted with (EquipmentFit.fittedForNew()) on the process the month's production
> applies (RecipeChanges.newChoices(): the saved choice once a recipe list has run, a fresh one before) - held as a value
> beside the template it is of. The template stays the one every stack, site, order, map key and kept action holds.
> 
> WHY (QN1, 2026-11-19; docs/queue/QN1.md, R2e's fifth review). The investors, the Build card, the advice, the
> auto-builder, the staffing gate, the licences, the future posts and power ahead all valued a new building on the live
> template, whose figures are the standing fleet's mix (Recipes.apply()); EquipmentFit.verdictForNew() could meanwhile
> reject a piece the fleet has, so a robot-fitted Vehicle Works's 100 unskilled posts were quoted for a new plant that
> would open without the robot and need 300. R2e's first repair handed every reader a COPIED template with the new
> building's figures; the copies leaked into what is keyed by identity (BuildingManager.getStack(), WorksValue's pending
> loads, the Build card's kept order callbacks, BuildAdvice's custom clinics looked up by id) and it was taken out. A
> value cannot be ordered, stacked or used as a key: a reader resolves one where it reads a figure (of()) and keeps
> passing the template.
> 
> A city founded the default way fits every piece to every building and runs no recipe choice, so there the unit is the
> template's own figures, read live (standing()): the same numbers, in the same arithmetic, as before.

**Uses:** [Good](Good.md) (8), [BuildingsTemplate](BuildingsTemplate.md) (7), [Recipe](Recipe.md) (5), [JobType](JobType.md) (4), [Game](Game.md) (1), [LicenceFees](LicenceFees.md) (1), [RecipeChanges](RecipeChanges.md) (1), [EquipmentFit](EquipmentFit.md) (1)

**Used by (34):** [Agriculture](Agriculture.md), [AutoBuilder](AutoBuilder.md), [Automotive](Automotive.md), [BuildAdvice](BuildAdvice.md), [BuildCard](BuildCard.md), [BuildScreen](BuildScreen.md), [BuildingManager](BuildingManager.md), [BusinessInvestment](BusinessInvestment.md), [CityNeeds](CityNeeds.md), [CoalMining](CoalMining.md), [Construction](Construction.md), [EquipmentFit](EquipmentFit.md), [ExtractionSector](ExtractionSector.md), [Fitout](Fitout.md), [FoodProcessing](FoodProcessing.md), [Forestry](Forestry.md), [Freight](Freight.md), [Game](Game.md), [GoldMining](GoldMining.md), [LicenceFees](LicenceFees.md), [LuxuryRetail](LuxuryRetail.md), [Manufacturing](Manufacturing.md), [NewUnitCheck](NewUnitCheck.md), [Oil](Oil.md), [ProcessingSector](ProcessingSector.md), [Quarrying](Quarrying.md), [RealEstate](RealEstate.md), [RecipeChanges](RecipeChanges.md), [Refining](Refining.md), [Restaurants](Restaurants.md), [Retail](Retail.md), [Sector](Sector.md), [SpreadPlanner](SpreadPlanner.md), [WorksValue](WorksValue.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 78 | `NewUnit.RESOLVING` | `ThreadLocal.withInitial(() -> false)` | Set while a proposal is being resolved: the pricing inside fittedForNew() reads the templates as they stand. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 29 | `private final BuildingsTemplate template` |  |
| 31 | `private final Recipe recipe` | The new building's recipe; null when the unit is the template's own figures as they stand. |
| 33 | `private final Set<String> fitted` | The pieces it would be fitted with; null when the unit is the template as it stands. |
| 34 | `private final Map<Good, Double> made, used` |  |
| 36 | `private final boolean extras` | RecipeChanges' extras (apply()'s runRecipeExtras()), only for a template whose changes carry them. |
| 37 | `private final double production, steamRail, petrol, dig` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 159 | **type** `public final class NewUnit` | One new building of a template, as it would run (QN1): the recipe, posts, utility draw, output and input of a building ordered now - the pieces it would be fitted with (EquipmentFit.fittedForNew()) on the process the ... |
| 39 | 21 | `private NewUnit(BuildingsTemplate template, Recipe recipe, Set<String> fitted, boolean extras, double production, double steamR...` |  |
| 62 | 3 | `public static NewUnit standing(BuildingsTemplate t)` | The template's own figures, read live: an installed building's, and every new one's where nothing is chosen. |
| 67 | 3 | `static NewUnit proposed(BuildingsTemplate t, Recipe recipe, Set<String> fitted)` | A proposal on a recipe list with no changes of its own: the chosen recipe's figures, the template's extras. |
| 72 | 4 | `static NewUnit proposed(BuildingsTemplate t, Recipe recipe, Set<String> fitted, double production, double steamRail, double pet...` | A proposal composed from RecipeChanges, with the extras its changes move. |
| 86 | 6 | `public static NewUnit of(Game game, BuildingsTemplate t)` | One new building of `t` in this city now, evaluated afresh from this call's inputs: the template as it stands in a city founded the default way, for a building with one way to run, for no game, and inside another pric... |
| 97 | 9 | `static<T> T standingWhile(java.util.function.Supplier<T> pricing)` | Runs a pricing with every proposal it asks for read as the template stands: fittedForNew()'s verdicts and the process choice price the states they are given, exactly as before QN1, wherever they are asked from. |
| 108 | 1 | `static boolean resolving()` | Whether a proposal is being resolved on this thread (inside fittedForNew()'s pricing): Fitout.of() reads the whole template there, as of() reads it standing (R1e). |
| 111 | 1 | `public BuildingsTemplate template()` | The template it is of: what is ordered, stacked and looked up. |
| 114 | 1 | `public boolean proposed()` | Whether it carries a chosen recipe of its own (a city founded the 1900 way), rather than the template as it stands. |
| 117 | 1 | `public Recipe recipe()` | Its recipe; null for the template as it stands. |
| 120 | 1 | `public Set<String> fitted()` | The pieces it would be fitted with; null for the template as it stands (every piece, in a city founded the default way). |
| 122 | 1 | `public int getJobs(JobType type)` |  |
| 125 | 6 | `public int getTotalJobs()` | The sum of its posts, as BuildingsTemplate.getTotalJobs() sums them. |
| 133 | 4 | `public int getLicensedPosts()` | Its posts that need the template's licence (BuildingsTemplate.getLicensedPosts()). |
| 138 | 1 | `public int getElectricityConsumption()` |  |
| 140 | 1 | `public double getWaterConsumption()` |  |
| 142 | 1 | `public double getGen()` |  |
| 144 | 1 | `public double getConsumables()` |  |
| 147 | 6 | `public double powerBought()` | kW it buys from the grid, as BuildingsTemplate.powerBought() reads it. |
| 155 | 5 | `public double powerSold()` | kW it sells the grid, as BuildingsTemplate.powerSold() reads it. |
| 161 | 1 | `public Map<Good, Double> goodsMade()` |  |
| 163 | 1 | `public Map<Good, Double> goodsUsed()` |  |
| 165 | 1 | `public double makes(Good good)` |  |
| 167 | 1 | `public double uses(Good good)` |  |
| 169 | 1 | `public double getProduction1()` |  |
| 171 | 1 | `public double steamRailShare()` |  |
| 173 | 1 | `public double crackerPetrol()` |  |
| 176 | 4 | `public double dig()` | The dig of the site it stands on (BuildingsTemplate.Site.dig()); 0 for a building on no resource's sites. |
| 182 | 3 | `public String toString()` |  |

