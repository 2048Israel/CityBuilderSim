# Recipe.java - 192 lines · 23 methods · 2 constants · model

`ham/citybuildersim/Recipe.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

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
> "since" is a year (a whole number, as text or digits) or a catalogue code; a year is known from its January, a code from its entry's month in
> research.json (R2: ResearchCatalog.knownFrom(), the one reading Equipment and BuildingsTemplate share), and a code that is not in the
> catalogue never. needsEquipment is the id of a piece of equipment on the SAME template, or null for none;
> which part of a stack of buildings may run the recipe (the fitted or the unfitted) follows from it (Recipes).

**Uses:** [Good](Good.md) (11), [JobType](JobType.md) (5), [Equipment](Equipment.md) (2), [BuildingsTemplate](BuildingsTemplate.md) (2), [ResearchCatalog](ResearchCatalog.md) (1), [Research](Research.md) (1)

**Used by (10):** [BuildingCatalog](BuildingCatalog.md), [BuildingDataCheck](BuildingDataCheck.md), [BuildingsTemplate](BuildingsTemplate.md), [DiscoveryCheck](DiscoveryCheck.md), [RecipeCheck](RecipeCheck.md), [Recipes](Recipes.md), [ResearchCheck](ResearchCheck.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [Sector](Sector.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 31 | `Recipe.DEFAULT_ID` | `"default"` | The id of the recipe a template with no list has: its top level. |
| 34 | `Recipe.DEFAULT_SINCE` | `"1900"` | The year a template's own top level is known from: the start (Equipment's "1900"). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final String id` |  |
| 37 | `private final String name` |  |
| 38 | `private final String since` |  |
| 39 | `private final boolean year` |  |
| 40 | `private final int knownFrom` |  |
| 41 | `private final String needsEquipment` |  |
| 42 | `private final boolean isDefault` |  |
| 43 | `private final Map<Good, Double> makes` |  |
| 44 | `private final Map<Good, Double> uses` |  |
| 45 | `private final int[] jobs` |  |
| 46 | `private final int electricity` |  |
| 47 | `private final double gen` |  |
| 48 | `private final double water` |  |
| 49 | `private final double consumables` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 28 | 165 | **type** `public final class Recipe` | A way a building runs: what it uses, what it makes, who it needs and what it draws, and when it is known and what piece of equipment it needs. |
| 66 | 25 | `public Recipe(String id, String name, String since, String needsEquipment, boolean isDefault, Map<Good, Double> makes, Map<Good...` |  |
| 92 | 13 | `private static Map<Good, Double> copyOf(String id, String what, Map<Good, Double> units)` |  |
| 110 | 3 | `public static Recipe topLevelOf(BuildingsTemplate t)` | A template's top level as a recipe: id {@link #DEFAULT_ID}, since {@link #DEFAULT_SINCE}, needing nothing, every figure the template's own now. |
| 115 | 6 | `public static Recipe topLevelOf(BuildingsTemplate t, String id, String name, String since, String needsEquipment)` | A template's top level as a recipe of this id, name, since and needsEquipment, flagged as the default. |
| 123 | 3 | `Recipe withConsumablesTimes(double factor)` | The same recipe with its consumables times a factor: BuildingsTemplate.pricedAtFoundingLevel() prices a recipe's as it prices the top level's. |
| 128 | 1 | `public String getId()` | The recipe's permanent key on its template. |
| 131 | 1 | `public String getName()` | What the screens call it. |
| 134 | 1 | `public String getSince()` | The year it is known from, as text, or the catalogue code that says (research.json). |
| 137 | 1 | `public boolean isYear()` | Whether "since" is a year (it parses as a whole number) and not a catalogue code. |
| 140 | 1 | `public int knownFrom()` | The game month it is known from: a year's January, a code's entry's month (ResearchCatalog.knownFrom()); Integer.MAX_VALUE for a code not in the catalogue. |
| 143 | 3 | `public boolean knownAt(int gameMonth)` | Whether it is known at a game month (month 1 = January 1900): from the month it is known from on. |
| 152 | 3 | `public boolean knownAt(int gameMonth, Research found)` | Whether it is known at a game month in a city whose research may have found it first (R3): from history's month, or from the month the city found its code, whichever is first (Research.foundMonth()). |
| 157 | 3 | `public boolean knownIn(int calendarYear)` | Whether it is known by a calendar year's January: knownAt() of that month (Equipment.januaryOf()), so that a screen that asks by the year agrees with the model that asks by the month. |
| 162 | 1 | `public String getNeedsEquipment()` | The id of the piece of equipment on the template it needs, or null for a recipe that needs none. |
| 165 | 1 | `public boolean isDefault()` | Whether this is the template's top level: today's way, which the figures of the template's buildings are when nothing else is chosen. |
| 168 | 1 | `public Map<Good, Double> makes()` | Units of each good it makes a month at nameplate; unmodifiable, only the goods it makes. |
| 171 | 1 | `public Map<Good, Double> uses()` | Units of each good it uses a month at nameplate; unmodifiable, only the goods it uses. |
| 174 | 1 | `public int getJobs(JobType type)` | Posts of a job type. |
| 177 | 1 | `public int getElectricity()` | kW it draws, its whole draw (the template's electricity), of which what it makes itself (getGen()) it does not buy. |
| 180 | 1 | `public double getGen()` | kW it makes itself. |
| 183 | 1 | `public double getWater()` | Water units it draws. |
| 186 | 1 | `public double getConsumables()` | Its consumables a month at nameplate, thousands of the world's money. |
| 189 | 3 | `public String toString()` |  |

