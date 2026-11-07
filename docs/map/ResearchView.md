# ResearchView.java - 371 lines · 27 methods · 3 constants · model

`ham/citybuildersim/ResearchView.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each
> sector's stacks carry with the verdict on it, the recipes in use, how a building became known to the city (the NEW mark and "where it came from"
> on its card), and the flags a discovery puts on City History's charts. Static reads of the city and nothing more: no JavaFX, nothing kept, nothing
> booked.
> 
> WHY (R5; docs/studies/research-design.md 5.5, 6.2, 6.3). The screens' rule is "every figure through a public model getter, never recomputed in the
> screen", and four screens (Policy > Research > Found, a sector's Investors page, a building's card, City History) need the same facts, so they are
> read once, here, where a harness can hold them to the model's own numbers. The timeline is DERIVED and not saved: a find's month is already saved
> (Research.foundMonth()), and history's month is data (research.json), so what a screen shows can always be had again from the two; a saved copy
> could only part from them. What cannot be had again - the researchers and the money over time, the share of the equipment fitted - is a flow, and
> HistorySave keeps it (researchersKey(), RESEARCH_SPEND, EQUIPMENT_FITTED).
> 
> ONLY A CITY FOUNDED THE 1900 WAY HAS ANYTHING TO SHOW (Game.foundedUnfitted(); Research's and Recipes' headers say the same). In a city founded the
> default way every discovery is known from the start, nothing arrives, nothing is chosen and nothing is unfitted, so every list here is empty, every
> origin null and knownAtFounding() the whole catalogue: the screens then draw nothing new, and every city a player has played keeps its screens.
> 
> TWO LAYERS, as EquipmentFit and Recipes are: the cores that take a catalogue, a research and a month (package-private) let a harness put the city at
> any month and the catalogue at any shape; the public reads take the game and pass its own.

**Uses:** [ResearchCatalog](ResearchCatalog.md) (13), [BuildingsTemplate](BuildingsTemplate.md) (13), [Game](Game.md) (10), [Research](Research.md) (7), [Equipment](Equipment.md) (4), [DecisionLog](DecisionLog.md) (3), [BuildingsStacks](BuildingsStacks.md) (3), [EquipmentFit](EquipmentFit.md) (2), [BuildingManager](BuildingManager.md) (2), [Recipe](Recipe.md) (2), [PolicyDates](PolicyDates.md) (1)

**Used by (8):** [BuildScreen](BuildScreen.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [KeptCheck](KeptCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchViewCheck](ResearchViewCheck.md), [SectorScreen](SectorScreen.md), [TimeChart](TimeChart.md)

## Sections

| line | section |
|---:|---|
| 53 | THE TIMELINE |
| 163 | THE FLAGS ON CITY HISTORY |
| 183 | THE EQUIPMENT |
| 263 | THE RECIPES |
| 306 | WHERE A BUILDING CAME FROM |

## Enum constants

| line | constant | says |
|---:|---|---|
| 38 | `ResearchView.Source.HERE` |  |
| 38 | `ResearchView.Source.ABROAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 41 | `ResearchView.FIRST_MONTH` | `1` | The month every city is founded in. |
| 48 | `ResearchView.NEW_MONTHS` | `12` | How many months a building is NEW on its card after it became known in the city: it is new while its age (the city's month less the month it became known) is under this. |
| 51 | `ResearchView.FLAG_KIND` | `"discovery"` | The kind of the flags a discovery puts on City History's decision lane (flagEntries()); a discovery is not a decision, so it is no DecisionLog kind. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 33 | 339 | **type** `public final class ResearchView` | What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each sector's stacks carry with the verdict on it, the recipes in use, how a bu... |
| 35 | 1 | `private ResearchView()` |  |
| 38 | 1 | **type** `public enum Source` | How a discovery reached the city: found here by its own researchers before history's date, or from abroad on history's date. |

### THE TIMELINE (lines 53-162)

| line | len | member | says |
|---:|---:|---|---|
| 58 | 17 | **type** `public record Discovery(Entry entry, int month, Source source)` | One discovery of the city's life: its catalogue entry, the month the city came to know it and how. |
| 61 | 3 | `public int worldMonth()` _(in ResearchView.Discovery)_ | The month history has it in. |
| 66 | 3 | `public double yearsAhead()` _(in ResearchView.Discovery)_ | How many years before history's date the city found it: (worldMonth - month) / 12 for one found here, 0 for one from abroad. |
| 71 | 3 | `public ResearchCatalog.Field field()` _(in ResearchView.Discovery)_ | The field that researches it. |
| 77 | 3 | `private static int monthHere(Research research, Entry e)` | The month the city knows an entry from: the month it found it, where that is before history's date, else history's. |
| 82 | 3 | `private static Source sourceOf(Research research, Entry e)` | How the city came to know an entry: HERE where it found it before history's date, else ABROAD on history's date. |
| 90 | 11 | `static List<Discovery> timeline(ResearchCatalog catalogue, Research research, int month)` | The timeline of a research at a month, against a catalogue (the core): every entry the city came to know after FIRST_MONTH and by `month`, with the month it came to know it and how, ordered by month and, inside a mont... |
| 107 | 3 | `public static List<Discovery> timeline(Game game)` | The city's discoveries so far, oldest first: in a city founded the 1900 way, each entry known by the city's month whose month in this city is after FIRST_MONTH - the month it was found here (Research.foundMonth()) whe... |
| 112 | 5 | `static int knownAtFounding(ResearchCatalog catalogue, Research research)` | How many entries the city knew at the founding (the core): those whose month in this city is FIRST_MONTH or before. |
| 122 | 4 | `public static int knownAtFounding(Game game)` | How many discoveries the city knew at the founding: the entries history dates at FIRST_MONTH or before in a city founded the 1900 way, and the whole catalogue in a city founded the default way, which "founded knowing ... |
| 128 | 5 | `public static int count(List<Discovery> timeline, Source source)` | How many of the discoveries came by a source. |
| 135 | 6 | `public static EnumMap<ResearchCatalog.Field, List<Discovery>> byField(List<Discovery> timeline)` | The timeline split by field: every field present, in the enum's order, possibly with an empty list; each list keeps the timeline's order. |
| 147 | 15 | `public static int[][] cumulative(List<Discovery> timeline, int firstMonth, int lastMonth)` | The count of discoveries at or before each month of City History's axis, for the graph of found here against from abroad: {here[], abroad[]}, entry i the count at or before month firstMonth + i, so entry 0 holds every... |

### THE FLAGS ON CITY HISTORY (lines 163-182)

| line | len | member | says |
|---:|---:|---|---|
| 172 | 10 | `public static List<DecisionLog.Entry> flagEntries(Game game)` | One entry for each discovery of the timeline, oldest first, of kind FLAG_KIND at the discovery's month: "Found here: Electric arc steel furnace, 0.8 years before the world" (one decimal, and always "years") or "From a... |

### THE EQUIPMENT (lines 183-262)

| line | len | member | says |
|---:|---:|---|---|
| 192 | 8 | **type** `public record PieceRow(BuildingsTemplate template, Equipment piece, int buildings, int unfitted, int onSite...` | One piece of equipment on one stack, as the Investors page and the Found page list it: how many of the stack's buildings there are, how many lack the piece, how many are being fitted now, the verdict on the piece (the... |
| 196 | 3 | `public int fitted()` _(in ResearchView.PieceRow)_ | The buildings that carry the piece: the buildings less those that lack it. |
| 202 | 1 | **type** `private record Standing(BuildingsStacks stack, Equipment piece, int buildings, int unfitted)` | One stack's piece, before it is judged: what both equipment() and fittedShare() read. |
| 205 | 18 | `private static List<Standing> standing(Game game, String sectorKey)` | The pieces standing in the city's stacks, known at the city's month, in the templates' order and each template's pieces' order; `sectorKey` null is the whole city. |
| 229 | 11 | `public static List<PieceRow> equipment(Game game, String sectorKey)` | The equipment standing in the city, in a city founded the 1900 way: for each template with a stack of buildings (the sector's own when `sectorKey` is not null, the whole city's when it is), in the templates' order, a ... |
| 246 | 9 | `public static double fittedShare(Game game)` | The share of the equipment standing that is fitted, 0 to 1: over every row of equipment(game, null), the buildings that carry the piece over the buildings. |
| 257 | 5 | `private static Map<String, Discovery> discoveriesByCode(List<Discovery> timeline)` | The discoveries of a timeline by catalogue code. |

### THE RECIPES (lines 263-305)

| line | len | member | says |
|---:|---:|---|---|
| 272 | 1 | **type** `public record RecipeUse(BuildingsTemplate template, Recipe recipe, int running, int buildings, Discovery from)` | A way of running a template other than its default, known in the city: how many of the template's `buildings` run it now (the fitted part and the unfitted part that chose it, as Recipes.apply() weighs them; 0 for one ... |
| 280 | 25 | `public static List<RecipeUse> recipesInUse(Game game)` | The recipes in use, in a city founded the 1900 way: for each template with a choice of recipes and a stack of buildings, each of its recipes other than the default that is known at the city's month. |

### WHERE A BUILDING CAME FROM (lines 306-371)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 7 | **type** `public record Origin(BuildingsTemplate template, int month, Source source, Entry entry)` | How a template became known to the city after the founding: the month, whether the city found it or the world did, and the discovery when its "since" is a catalogue code (null when it is a year). |
| 317 | 3 | `public double yearsAhead()` _(in ResearchView.Origin)_ | How many years before history's date the city found it: as Discovery's, 0 for one from abroad and for one with no entry. |
| 327 | 13 | `static Origin origin(ResearchCatalog catalogue, Research research, int month, BuildingsTemplate t)` | The origin of a template for a research at a month, against a catalogue (the core): a since that is a catalogue code - the month and source the timeline reads, and the entry; a since that is a year after 1900's Januar... |
| 345 | 3 | `public static Origin origin(Game game, BuildingsTemplate t)` | How a template became known after the founding, in a city founded the 1900 way (see the core). |
| 350 | 3 | `static boolean isNew(Origin origin, int month)` | Whether an origin is NEW at a month (the core): there is one, and it is fewer than NEW_MONTHS months old. |
| 355 | 3 | `public static boolean isNew(Game game, BuildingsTemplate t)` | Whether a template is NEW on its card: it has an origin, and became known fewer than NEW_MONTHS months before the city's month. |
| 360 | 6 | `public static List<BuildingsTemplate> newTemplates(Game game)` | The templates that are NEW, in the templates' order. |
| 368 | 3 | `public static boolean isLab(BuildingsTemplate t)` | Whether a template is a Research Lab (R4's four): it researches a field. |

