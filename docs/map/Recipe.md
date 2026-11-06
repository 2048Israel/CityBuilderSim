# Recipe.java - 175 lines · 20 methods · 2 constants · model

`ham/citybuildersim/Recipe.java` - generated 2026-11-05 by CodeMap; line numbers are as of that run.

> A way a building runs: what it uses, what it makes, who it needs and what it draws, and when it is known and what piece of equipment it needs.
> Immutable; read from buildings.json's "recipes" (BuildingCatalog.readRecipes()) and held by the template (BuildingsTemplate.getRecipes()).
> 
> WHY (R1c, 2026-11-05; docs/studies/research-design.md 2.1). Before R1c a building had one way to run, its template's top-level figures, and a
> second way of making the same thing (coke in by-product ovens instead of beehive ovens, a farm on a tractor instead of on horses) had nowhere to
> live but a second template, which is a different building with a different id and a different price, and a save keyed by it. A recipe is a
> way the SAME building runs: "a building can know several; each month it runs the best one it knows at the city's own prices (Recipes), and
> switching costs nothing". The top-level makes, uses and jobs stay as the default recipe, so that "a building with everything known runs exactly
> today's buildings.json figures" (research-design 9) and no template without a list is touched.
> 
> A Recipe always holds all seven figures. The ones the file does not state are the template's top level, filled in when the recipe is read
> (the catalogue), so that nothing downstream asks where a figure comes from. The figures are the template's own, in the template's own units:
> makes and uses in units of a good a month at nameplate, jobs in posts by JobType, electricity and gen in kW, water in units, consumables in
> thousands of the world's money (at the founding's price level once the template is priced, as the top level's are).
> 
> "since" is a year (a whole number, as text or digits) or a catalogue code; a year is known from its January, a code never in this build
> (Equipment's reading, shared: Equipment.yearOf()). needsEquipment is the id of a piece of equipment on the SAME template, or null for none;
> which part of a stack of buildings may run the recipe (the fitted or the unfitted) follows from it (Recipes).

**Uses:** [Good](Good.md) (11), [JobType](JobType.md) (5), [BuildingsTemplate](BuildingsTemplate.md) (2), [Equipment](Equipment.md) (1)

**Used by (6):** [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingsTemplate](BuildingsTemplate.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [Sector](Sector.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 30 | `Recipe.DEFAULT_ID` | `"default"` | The id of the recipe a template with no list has: its top level. |
| 33 | `Recipe.DEFAULT_SINCE` | `"1900"` | The year a template's own top level is known from: the start (Equipment's "1900"). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 35 | `private final String id` |  |
| 36 | `private final String name` |  |
| 37 | `private final String since` |  |
| 38 | `private final boolean year` |  |
| 39 | `private final int sinceYear` |  |
| 40 | `private final String needsEquipment` |  |
| 41 | `private final boolean isDefault` |  |
| 42 | `private final Map<Good, Double> makes` |  |
| 43 | `private final Map<Good, Double> uses` |  |
| 44 | `private final int[] jobs` |  |
| 45 | `private final int electricity` |  |
| 46 | `private final double gen` |  |
| 47 | `private final double water` |  |
| 48 | `private final double consumables` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 27 | 149 | **type** `public final class Recipe` | A way a building runs: what it uses, what it makes, who it needs and what it draws, and when it is known and what piece of equipment it needs. |
| 65 | 26 | `public Recipe(String id, String name, String since, String needsEquipment, boolean isDefault, Map<Good, Double> makes, Map<Good...` |  |
| 92 | 13 | `private static Map<Good, Double> copyOf(String id, String what, Map<Good, Double> units)` |  |
| 110 | 3 | `public static Recipe topLevelOf(BuildingsTemplate t)` | A template's top level as a recipe: id {@link #DEFAULT_ID}, since {@link #DEFAULT_SINCE}, needing nothing, every figure the template's own now. |
| 115 | 6 | `public static Recipe topLevelOf(BuildingsTemplate t, String id, String name, String since, String needsEquipment)` | A template's top level as a recipe of this id, name, since and needsEquipment, flagged as the default. |
| 123 | 3 | `Recipe withConsumablesTimes(double factor)` | The same recipe with its consumables times a factor: BuildingsTemplate.pricedAtFoundingLevel() prices a recipe's as it prices the top level's. |
| 128 | 1 | `public String getId()` | The recipe's permanent key on its template. |
| 131 | 1 | `public String getName()` | What the screens call it. |
| 134 | 1 | `public String getSince()` | The year it is known from, as text, or the catalogue code that will say. |
| 137 | 1 | `public boolean isYear()` | Whether "since" is a year (it parses as a whole number) and not a catalogue code. |
| 140 | 3 | `public boolean knownIn(int calendarYear)` | Whether it is known in a calendar year: a year since from that year on, a code never (R2's catalogue resolves codes; until then none is known). |
| 145 | 1 | `public String getNeedsEquipment()` | The id of the piece of equipment on the template it needs, or null for a recipe that needs none. |
| 148 | 1 | `public boolean isDefault()` | Whether this is the template's top level: today's way, which the figures of the template's buildings are when nothing else is chosen. |
| 151 | 1 | `public Map<Good, Double> makes()` | Units of each good it makes a month at nameplate; unmodifiable, only the goods it makes. |
| 154 | 1 | `public Map<Good, Double> uses()` | Units of each good it uses a month at nameplate; unmodifiable, only the goods it uses. |
| 157 | 1 | `public int getJobs(JobType type)` | Posts of a job type. |
| 160 | 1 | `public int getElectricity()` | kW it draws, its whole draw (the template's electricity), of which what it makes itself (getGen()) it does not buy. |
| 163 | 1 | `public double getGen()` | kW it makes itself. |
| 166 | 1 | `public double getWater()` | Water units it draws. |
| 169 | 1 | `public double getConsumables()` | Its consumables a month at nameplate, thousands of the world's money. |
| 172 | 3 | `public String toString()` |  |

