# ResearchCatalog.java - 339 lines · 22 methods · 10 constants · model

`ham/citybuildersim/ResearchCatalog.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The research catalogue (R2): every discovery the city's schools can make, read out of research.json: its code, the field that researches it,
> what it unlocks (a building, a piece of equipment, a recipe, a drug), history's date, the discoveries it needs first and the work it takes.
> 
> WHY. Before R2 a "since" code in buildings.json had nothing to resolve it: the tractor (PE14) and the oxygen converter's recipe (E1) were
> data that never came, and Recipe and Equipment treated every code as never known. The dates and the needs are data, not code, so that the
> next entry is a line in the file and not a change to Java. The date is history's month and not the year, because R3's harness wants "every
> discovery arrives on history's date" to be a statement about months. The date rule: a month only where the study names one, else the month
> the study's model put it in (monthKnown false); and the law rows carry exactly PolicyDates' dates, so the Policy gate and the catalogue can
> never disagree about when a lever opens.
> 
> Like BuildingCatalog, nothing here can stop the game starting: a missing file, bad JSON or a bad entry is reported with println and costs
> that entry (or the file), never the game. This class is only the catalogue. Recipe, Equipment and BuildingsTemplate read their codes through
> knownFrom() in the next slice; the finding of a discovery (the schools, the grants, the work done) is later batches'.

**Uses:** [PolicyDates](PolicyDates.md) (2), [CityCalendar](CityCalendar.md) (2)

**Used by (25):** [BuildCardCheck](BuildCardCheck.md), [BuildingCatalog](BuildingCatalog.md), [BuildingManager](BuildingManager.md), [BuildingsTemplate](BuildingsTemplate.md), [DiscoveryCheck](DiscoveryCheck.md), [Equipment](Equipment.md), [EquipmentCheck](EquipmentCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [Inbox](Inbox.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PrizeCheck](PrizeCheck.md), [Recipe](Recipe.md), [RecipeCheck](RecipeCheck.md), [Research](Research.md), [ResearchCheck](ResearchCheck.md), [ResearchPreview](ResearchPreview.md), [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchView](ResearchView.md), [ResearchViewCheck](ResearchViewCheck.md), [YearBook](YearBook.md)

## Sections

| line | section |
|---:|---|
| 38 | THE WORK RULE (research-design.md 1.8; docs/studies/research-model/research_model.py: E1950, GROWTH) |
| 126 | · the catalogue |
| 183 | READING THE FILE |
| 311 | · the readers |

## Enum constants

| line | constant | says |
|---:|---|---|
| 68 | `ResearchCatalog.Size.BUILDING` |  |
| 68 | `ResearchCatalog.Size.EQUIPMENT` |  |
| 68 | `ResearchCatalog.Size.RECIPE` |  |
| 68 | `ResearchCatalog.Size.DRUG` |  |
| 72 | `ResearchCatalog.Field.ENGINEERING` |  |
| 73 | `ResearchCatalog.Field.MEDICINE` |  |
| 74 | `ResearchCatalog.Field.BUSINESS` |  |
| 75 | `ResearchCatalog.Field.LAW` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 36 | `ResearchCatalog.FILE_NAME` | `"research.json"` |  |
| 47 | `ResearchCatalog.WORK_RULE_YEAR` | `1950` | The year the work rule is stated for: 1950, where the first study's catalogue begins. |
| 50 | `ResearchCatalog.WORK_BUILDING` | `400` | Researcher-years a building took at WORK_RULE_YEAR (est.; [BJVW]). |
| 53 | `ResearchCatalog.WORK_EQUIPMENT` | `400` | Researcher-years a piece of equipment took at WORK_RULE_YEAR (est.; [BJVW]). |
| 56 | `ResearchCatalog.WORK_RECIPE` | `200` | Researcher-years a recipe took at WORK_RULE_YEAR (est.; [BJVW]). |
| 59 | `ResearchCatalog.WORK_DRUG` | `500` | Researcher-years a drug took at WORK_RULE_YEAR, clinical trials included (est.; [BJVW]). |
| 62 | `ResearchCatalog.WORK_GROWTH` | `0.053` | How much more research costs each year, as a fraction, in every field but medicine: 5.3% (the US aggregate, [BJVW]). |
| 65 | `ResearchCatalog.MEDICINE_WORK_GROWTH` | `0.035` | The same in medicine, where a drug's trials make the cost grow more slowly: 3.5% a year ([BJVW]). |
| 91 | `ResearchCatalog.KINDS` | `List.of("building", "equipment", "recipe", "drug")` | What a discovery can unlock, as the file spells it; an entry names one or two. |
| 129 | `ResearchCatalog.Holder.INSTANCE` | `load()` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `private final String school` |  |
| 118 | `private final List<Entry> entries` |  |
| 119 | `private final Map<String, Entry> byCode` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 306 | **type** `public final class ResearchCatalog` | The research catalogue (R2): every discovery the city's schools can make, read out of research.json: its code, the field that researches it, what it unlocks (a building, a piece of equipment, a recipe, a drug), histor... |

### THE WORK RULE (research-design.md 1.8; docs/studies/research-model/research_model.py: E1950, GROWTH) (lines 38-125)

| line | len | member | says |
|---:|---:|---|---|
| 68 | 1 | **type** `public enum Size` | What the four kinds of work are like; the kind of a discovery's work is its size, which is not always what it unlocks (a Walk-in Clinic is a recipe's work). |
| 71 | 18 | **type** `public enum Field` | The four fields a school researches, each with the school that does it. |
| 79 | 3 | `Field(String school)` _(in ResearchCatalog.Field)_ |  |
| 84 | 1 | `public String school()` _(in ResearchCatalog.Field)_ | The school that researches this field, as the player reads it: "the Medical school". |
| 87 | 1 | `public double workGrowth()` _(in ResearchCatalog.Field)_ | How much more a discovery of this field costs each year. |
| 97 | 20 | **type** `public record Entry(String code, String name, Field field, List<String> kinds, String kindNote, Size size, ...` | One discovery. |
| 101 | 5 | `{ ... }` _(in ResearchCatalog.Entry)_ |  |
| 108 | 3 | `public int worldMonth()` _(in ResearchCatalog.Entry)_ | The game month history has it in: PolicyDates.monthOf(year, monthOfYear), the game's one formula (month 1 = January 1900). |
| 113 | 3 | `public boolean is(String kind)` _(in ResearchCatalog.Entry)_ | Whether it unlocks that kind ("building", "equipment", "recipe" or "drug"). |
| 121 | 4 | `private ResearchCatalog(List<Entry> entries)` |  |

### the catalogue (lines 126-182)

| line | len | member | says |
|---:|---:|---|---|
| 128 | 3 | **type** `private static final class Holder` |  |
| 133 | 3 | `public static ResearchCatalog get()` | The packaged file's catalogue, read once, on first use, whichever thread asks first. |
| 138 | 3 | `public List<Entry> entries()` | Every discovery, in the file's order (the studies' order). |
| 143 | 3 | `public Entry entry(String code)` | The discovery with that code, or null for none. |
| 148 | 4 | `public int worldMonth(String code)` | The game month history has a discovery in; Integer.MAX_VALUE for a code not in the catalogue, which is never known. |
| 158 | 9 | `public static int knownFrom(String since)` | The game month a "since" is known from: a whole number is a year, known from its January (PolicyDates.monthOf(year, 1)); a code is known from its entry's worldMonth(); a blank "since" or a code not in the catalogue is... |
| 169 | 8 | `public static int workAtRuleYear(Size size)` | The researcher-years a kind of discovery took in WORK_RULE_YEAR. |
| 179 | 3 | `public static double workByRule(Size size, Field field, int year)` | The work rule: what a kind of discovery took in WORK_RULE_YEAR, grown at its field's rate to the year, in researcher-years. |

### READING THE FILE (lines 183-310)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 12 | `private static ResearchCatalog load()` |  |
| 204 | 31 | `static ResearchCatalog parse(String json)` | The catalogue a text of research.json makes. |
| 237 | 51 | `private static Entry readEntry(JsonObject o, Set<String> seen)` | The entry an object makes, or null (reported) for one that is not usable. |
| 290 | 15 | `private void reportNeeds()` | Reports each need that is not a code in the catalogue and each that history dates after its entry; the entry stays. |
| 307 | 3 | `private static void report(String message)` | Every report is a println, like the rest of the game's log. |

### the readers (lines 311-339)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 4 | `private static String string(JsonObject o, String key)` |  |
| 318 | 4 | `private static int integer(JsonObject o, String key)` |  |
| 323 | 4 | `private static boolean flag(JsonObject o, String key)` |  |
| 329 | 10 | `private static List<String> strings(JsonObject o, String key)` | A list of strings, blanks left out; an absent key or a value that is not an array is the empty list. |

