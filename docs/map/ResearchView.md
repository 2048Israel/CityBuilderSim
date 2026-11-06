# ResearchView.java - 789 lines · 65 methods · 5 constants · model

`ham/citybuildersim/ResearchView.java` - generated 2026-11-06 by CodeMap; line numbers are as of that run.

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

**Uses:** [Research](Research.md) (34), [Game](Game.md) (17), [ResearchCatalog](ResearchCatalog.md) (14), [BuildingsTemplate](BuildingsTemplate.md) (14), [ResearchPreview](ResearchPreview.md) (10), [Pieces](Pieces.md) (8), [Equipment](Equipment.md) (4), [BuildingsStacks](BuildingsStacks.md) (4), [Dials](Dials.md) (4), [DecisionLog](DecisionLog.md) (3), [EquipmentFit](EquipmentFit.md) (2), [BuildingManager](BuildingManager.md) (2), [Recipe](Recipe.md) (2), [PolicyDates](PolicyDates.md) (1), [EducationType](EducationType.md) (1)

**Used by (9):** [BuildScreen](BuildScreen.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [KeptCheck](KeptCheck.md), [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchViewCheck](ResearchViewCheck.md), [SectorScreen](SectorScreen.md), [TimeChart](TimeChart.md)

## Sections

| line | section |
|---:|---|
| 54 | THE TIMELINE |
| 164 | THE FLAGS ON CITY HISTORY |
| 184 | THE EQUIPMENT |
| 269 | THE RECIPES |
| 318 | WHERE A BUILDING CAME FROM |
| 384 | THE LAID-ON AXIS AND WHAT IS STILL TO COME (R3b, from R5's gate review: the Found page worked these out itself) |
| 417 | THE OVERVIEW (R3b; docs/studies/research-design.md 5.5, 6.2) |
| 560 | THE FUNDING PAGE (R3b; docs/studies/research-design.md 5.1, 5.4) |
| 693 | · the preview |
| 767 | · the preview's words |

## Enum constants

| line | constant | says |
|---:|---|---|
| 39 | `ResearchView.Source.HERE` |  |
| 39 | `ResearchView.Source.ABROAD` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 42 | `ResearchView.FIRST_MONTH` | `1` | The month every city is founded in. |
| 49 | `ResearchView.NEW_MONTHS` | `12` | How many months a building is NEW on its card after it became known in the city: it is new while its age (the city's month less the month it became known) is under this. |
| 52 | `ResearchView.FLAG_KIND` | `"discovery"` | The kind of the flags a discovery puts on City History's decision lane (flagEntries()); a discovery is not a decision, so it is no DecisionLog kind. |
| 720 | `ResearchView.CACHE_SIZE` | `32` | The preview's answers kept, the least recently read dropped first: a card's dials are read again at every redraw of the screen, and the answer does not change until a dial or the month does. |
| 721 | `ResearchView.CACHE` | `new java.util.LinkedHashMap<>(16, 0.75f, true) { @ Override protected boolean...` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 34 | 756 | **type** `public final class ResearchView` | What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each sector's stacks carry with the verdict on it, the recipes in use, how a bu... |
| 36 | 1 | `private ResearchView()` |  |
| 39 | 1 | **type** `public enum Source` | How a discovery reached the city: found here by its own researchers before history's date, or from abroad on history's date. |

### THE TIMELINE (lines 54-163)

| line | len | member | says |
|---:|---:|---|---|
| 59 | 17 | **type** `public record Discovery(Entry entry, int month, Source source)` | One discovery of the city's life: its catalogue entry, the month the city came to know it and how. |
| 62 | 3 | `public int worldMonth()` _(in ResearchView.Discovery)_ | The month history has it in. |
| 67 | 3 | `public double yearsAhead()` _(in ResearchView.Discovery)_ | How many years before history's date the city found it: (worldMonth - month) / 12 for one found here, 0 for one from abroad. |
| 72 | 3 | `public ResearchCatalog.Field field()` _(in ResearchView.Discovery)_ | The field that researches it. |
| 78 | 3 | `private static int monthHere(Research research, Entry e)` | The month the city knows an entry from: the month it found it, where that is before history's date, else history's. |
| 83 | 3 | `private static Source sourceOf(Research research, Entry e)` | How the city came to know an entry: HERE where it found it before history's date, else ABROAD on history's date. |
| 91 | 11 | `static List<Discovery> timeline(ResearchCatalog catalogue, Research research, int month)` | The timeline of a research at a month, against a catalogue (the core): every entry the city came to know after FIRST_MONTH and by `month`, with the month it came to know it and how, ordered by month and, inside a mont... |
| 108 | 3 | `public static List<Discovery> timeline(Game game)` | The city's discoveries so far, oldest first: in a city founded the 1900 way, each entry known by the city's month whose month in this city is after FIRST_MONTH - the month it was found here (Research.foundMonth()) whe... |
| 113 | 5 | `static int knownAtFounding(ResearchCatalog catalogue, Research research)` | How many entries the city knew at the founding (the core): those whose month in this city is FIRST_MONTH or before. |
| 123 | 4 | `public static int knownAtFounding(Game game)` | How many discoveries the city knew at the founding: the entries history dates at FIRST_MONTH or before in a city founded the 1900 way, and the whole catalogue in a city founded the default way, which "founded knowing ... |
| 129 | 5 | `public static int count(List<Discovery> timeline, Source source)` | How many of the discoveries came by a source. |
| 136 | 6 | `public static EnumMap<ResearchCatalog.Field, List<Discovery>> byField(List<Discovery> timeline)` | The timeline split by field: every field present, in the enum's order, possibly with an empty list; each list keeps the timeline's order. |
| 148 | 15 | `public static int[][] cumulative(List<Discovery> timeline, int firstMonth, int lastMonth)` | The count of discoveries at or before each month of City History's axis, for the graph of found here against from abroad: {here[], abroad[]}, entry i the count at or before month firstMonth + i, so entry 0 holds every... |

### THE FLAGS ON CITY HISTORY (lines 164-183)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 10 | `public static List<DecisionLog.Entry> flagEntries(Game game)` | One entry for each discovery of the timeline, oldest first, of kind FLAG_KIND at the discovery's month: "Found here: Electric arc steel furnace, 0.8 years before the world" (one decimal, and always "years") or "From a... |

### THE EQUIPMENT (lines 184-268)

| line | len | member | says |
|---:|---:|---|---|
| 193 | 13 | **type** `public record PieceRow(BuildingsTemplate template, Equipment piece, int buildings, int unfitted, int onSite...` | One piece of equipment on one stack, as the Investors page and the Found page list it: how many of the stack's buildings there are, how many lack the piece, how many are being fitted now, the verdict on the piece (the... |
| 197 | 3 | `public int fitted()` _(in ResearchView.PieceRow)_ | The buildings that carry the piece: the buildings less those that lack it. |
| 202 | 3 | `public double share()` _(in ResearchView.PieceRow)_ | The share of the stack that carries the piece, 0 to 1 (the bar on the Found page): fitted() over the buildings, 0 where there are none. |
| 208 | 1 | **type** `private record Standing(BuildingsStacks stack, Equipment piece, int buildings, int unfitted)` | One stack's piece, before it is judged: what both equipment() and fittedShare() read. |
| 211 | 18 | `private static List<Standing> standing(Game game, String sectorKey)` | The pieces standing in the city's stacks, known at the city's month, in the templates' order and each template's pieces' order; `sectorKey` null is the whole city. |
| 235 | 11 | `public static List<PieceRow> equipment(Game game, String sectorKey)` | The equipment standing in the city, in a city founded the 1900 way: for each template with a stack of buildings (the sector's own when `sectorKey` is not null, the whole city's when it is), in the templates' order, a ... |
| 252 | 9 | `public static double fittedShare(Game game)` | The share of the equipment standing that is fitted, 0 to 1: over every row of equipment(game, null), the buildings that carry the piece over the buildings. |
| 263 | 5 | `private static Map<String, Discovery> discoveriesByCode(List<Discovery> timeline)` | The discoveries of a timeline by catalogue code. |

### THE RECIPES (lines 269-317)

| line | len | member | says |
|---:|---:|---|---|
| 278 | 7 | **type** `public record RecipeUse(BuildingsTemplate template, Recipe recipe, int running, int buildings, Discovery from)` | A way of running a template other than its default, known in the city: how many of the template's `buildings` run it now (the fitted part and the unfitted part that chose it, as Recipes.apply() weighs them; 0 for one ... |
| 281 | 3 | `public double share()` _(in ResearchView.RecipeUse)_ | The share of the stack that runs the recipe, 0 to 1 (the bar on the Found page): running over the buildings, 0 where there are none. |
| 292 | 25 | `public static List<RecipeUse> recipesInUse(Game game)` | The recipes in use, in a city founded the 1900 way: for each template with a choice of recipes and a stack of buildings, each of its recipes other than the default that is known at the city's month. |

### WHERE A BUILDING CAME FROM (lines 318-383)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 7 | **type** `public record Origin(BuildingsTemplate template, int month, Source source, Entry entry)` | How a template became known to the city after the founding: the month, whether the city found it or the world did, and the discovery when its "since" is a catalogue code (null when it is a year). |
| 329 | 3 | `public double yearsAhead()` _(in ResearchView.Origin)_ | How many years before history's date the city found it: as Discovery's, 0 for one from abroad and for one with no entry. |
| 339 | 13 | `static Origin origin(ResearchCatalog catalogue, Research research, int month, BuildingsTemplate t)` | The origin of a template for a research at a month, against a catalogue (the core): a since that is a catalogue code - the month and source the timeline reads, and the entry; a since that is a year after 1900's Januar... |
| 357 | 3 | `public static Origin origin(Game game, BuildingsTemplate t)` | How a template became known after the founding, in a city founded the 1900 way (see the core). |
| 362 | 3 | `static boolean isNew(Origin origin, int month)` | Whether an origin is NEW at a month (the core): there is one, and it is fewer than NEW_MONTHS months old. |
| 367 | 3 | `public static boolean isNew(Game game, BuildingsTemplate t)` | Whether a template is NEW on its card: it has an origin, and became known fewer than NEW_MONTHS months before the city's month. |
| 372 | 6 | `public static List<BuildingsTemplate> newTemplates(Game game)` | The templates that are NEW, in the templates' order. |
| 380 | 3 | `public static boolean isLab(BuildingsTemplate t)` | Whether a template is a Research Lab (R4's four): it researches a field. |

### THE LAID-ON AXIS AND WHAT IS STILL TO COME (R3b, from R5's gate review: the Found page worked these out itself) (lines 384-416)

| line | len | member | says |
|---:|---:|---|---|
| 393 | 12 | `public static double[][] cumulativeOn(List<Discovery> timeline, List<Integer> axis)` | The counts of found here and from abroad laid on History's months: {here[], abroad[]}, entry i the count at or before the axis' month i, each as long as the axis (cumulative() over the axis' first to last month, then ... |
| 407 | 3 | `public static int stillToCome(int all, int knownAtFounding, int here, int abroad)` | How many discoveries are still to come (the core): the catalogue's `all` less those known at the founding, found here and come from abroad, never below none. |
| 412 | 4 | `public static int stillToCome(Game game)` | How many of the catalogue's discoveries are still to come in the city: those history has not yet dated and the city has not found, in a city founded the 1900 way; none in any other. |

### THE OVERVIEW (R3b; docs/studies/research-design.md 5.5, 6.2) (lines 417-559)

| line | len | member | says |
|---:|---:|---|---|
| 422 | 1 | **type** `public record SchoolCount(String name, int count)` | A school standing that hosts a field's research, by its template's name and how many of it stand. |
| 428 | 23 | **type** `public record FieldReach(Field field, double researchers, double labResearchers, double idle, int inReach, ...` | One field on the Overview page: its researchers (the city's own and the labs'), the idle among them, the problems open to them and what those touch, what the field has found here and had come from abroad, and the scho... |
| 432 | 3 | `public double total()` _(in ResearchView.FieldReach)_ | Everyone at the field's research this month: the city's researchers and the labs'. |
| 437 | 3 | `public double working()` _(in ResearchView.FieldReach)_ | The people at work on a problem: everyone less the idle. |
| 442 | 3 | `public String reachWords()` _(in ResearchView.FieldReach)_ | "1 in reach: rail", or "nothing in reach" (reachWords()). |
| 447 | 3 | `public String schoolsWords()` _(in ResearchView.FieldReach)_ | "2 Institutes of Technology, 2 Universities" (schoolsWords()). |
| 456 | 16 | **type** `public record Overview(List<FieldReach> fields, double researchBill, double prizesPaid, int foundHere, int ...` | The Overview page: a row for each field in the enum's order, the month's research bill and the prizes the treasury paid the labs, the discoveries found here and come from abroad since the founding, and the researchers... |
| 459 | 5 | `public double researchers()` _(in ResearchView.Overview)_ | The people at research in all the fields, the labs' too. |
| 466 | 5 | `public double idle()` _(in ResearchView.Overview)_ | The idle among them. |
| 474 | 4 | `public static String reachWords(int inReach, List<String> touches)` | The words for what is in reach: "nothing in reach" for none, else "2 in reach: Wind Farm; Coal Power Plant", the distinct things the problems touch, in the catalogue's order. |
| 480 | 8 | `static List<String> touchesOf(List<Entry> problems)` | What a list of problems touches, as the catalogue words it (Entry.touches()): each distinct text once, in the order of the problems. |
| 490 | 6 | `public static String schoolsWords(List<SchoolCount> schools)` | The words for the schools that host a field: "2 Institutes of Technology, 2 Universities", or "no school" where none stands. |
| 498 | 6 | `static String plural(String name)` | A school's name in the plural: "University" to "Universities", "Institute of Technology" to "Institutes of Technology", "Law School" to "Law Schools". |
| 506 | 10 | `static List<SchoolCount> schoolsOf(Game game, Field f)` | The standing schools that host a field, the field's own kind first and the Universities after, each by its template's name and how many stand. |
| 522 | 11 | `static FieldReach reach(Research research, int month, Field f, List<Discovery> timeline, List<SchoolCount> schools, boolean res...` | One field's row of the Overview (the core): the research as the last month left it, the problems open to the field at `month` and what they touch, and the field's part of the timeline. |
| 535 | 3 | `public static double perThousandWorkers(double researchers, long workers)` | Researchers to each 1,000 people at work (the core): NaN where nobody works. |
| 545 | 14 | `public static Overview overview(Game game)` | The Overview page of the city: each field's researchers (the city's own and the labs'), the idle, the problems in reach and what they touch, the discoveries here and from abroad, and the schools that host it; the mont... |

### THE FUNDING PAGE (R3b; docs/studies/research-design.md 5.1, 5.4) (lines 560-692)

| line | len | member | says |
|---:|---:|---|---|
| 569 | 13 | **type** `public record Funding(Field field, double grant, double prize, int posts, int grantPosts, double room, doub...` | One field's dial card as it stands: the grant and the prize, the posts struck this month (the grant's own and the pot's), the room the schools have for posts and the grant that would fill it, the researchers the facul... |
| 573 | 3 | `public int potPosts()` _(in ResearchView.Funding)_ | The posts the field's pot bought beyond the grant (Research.strikePosts()). |
| 578 | 3 | `public double researchers()` _(in ResearchView.Funding)_ | Everyone at the field's own research at these dials: the faculty and the posts' researchers. |
| 584 | 1 | **type** `public record AtGrant(double grant, int posts, int grantPosts, double postResearchers, double researchers, ...` | What a grant would do, staged and not applied: the posts it buys, the researchers they and the faculty give and what the posts cost a month. |
| 590 | 26 | **type** `public record Staged(Map<Field, Double> grants, Map<Field, Double> prizes)` | The dials a player has staged and not applied: a grant and a prize for the fields the player has moved. |
| 592 | 4 | `{ ... }` _(in ResearchView.Staged)_ |  |
| 598 | 3 | `public static Staged none()` _(in ResearchView.Staged)_ | Nothing staged: every dial as it is. |
| 603 | 5 | `public Staged withGrant(Field f, double grant)` _(in ResearchView.Staged)_ | The same with a field's grant staged. |
| 610 | 5 | `public Staged withPrize(Field f, double prize)` _(in ResearchView.Staged)_ | The same with a field's prize staged. |
| 621 | 2 | **type** `record Pieces(Research research, int month, boolean researching, double[] wages, double[] fill, Map<Field, ...` | Everything the funding reads of a city, taken once: the research, the month, the wages and the fill the posts were struck at (null before any month: every post then counts as filled), whether the city researches at al... |
| 624 | 4 | `static Pieces piecesOf(Game game)` |  |
| 630 | 3 | `static double roomOf(Pieces p, Field f)` | The posts a field's schools have room for (Research.room() of their university-band staff), and nothing where none stands. |
| 635 | 3 | `static double facultyOf(Pieces p, Field f)` | The researcher-FTE a field's faculty give this month (Research.faculty() of the staffed staff). |
| 644 | 11 | `static double labThreshold(Research research, int month, Field f, double[] wages)` | The least prize that would bring a lab to a field (the core): the least remaining work of an open problem that a team at its limit could finish before the world, times twelve researcher-years and a post's cost (the pu... |
| 657 | 10 | `static Funding funding(Pieces p, Field f, int labsStanding, int labsOnSite)` | One field's dial card as it stands (the core), the labs being the ones standing and on site. |
| 669 | 3 | `public static Funding funding(Game game, Field f)` | One field's dial card as it stands in the city. |
| 679 | 8 | `static AtGrant atGrant(Pieces p, Field f, double grant)` | What a grant in a field would do (the core): the posts Research.posts() says it and the field's pot buy at the wages now within the room, those the grant alone buys, the researchers the filled ones give at the fill th... |
| 689 | 3 | `public static AtGrant atGrant(Game game, Field f, double grant)` | What a staged grant in a field would do in the city. |

### the preview (lines 693-766)

| line | len | member | says |
|---:|---:|---|---|
| 700 | 1 | **type** `record Dials(double[] researchers, double[] prize, double[] labFill, double[] wages)` | The dials a preview is made at, field by field (by Field.ordinal()): the researchers held fixed (the faculty and the filled posts of the grant), the prize and the share of a lab's posts filled; and the wages. |
| 702 | 16 | `static Dials dialsOf(Pieces p, Staged staged)` |  |
| 728 | 5 | `private static List<Double> boxed(double[]...arrays)` |  |
| 739 | 17 | `static ResearchPreview.Result preview(Research research, int month, Dials dials, int draws, long seed)` | The preview at some dials (the core): ResearchPreview.preview() of the research at the month, CACHED. |
| 762 | 4 | `public static ResearchPreview.Result preview(Game game, Staged staged)` | What the city's research would find here over the next ResearchPreview.YEARS years at the dials as they are, or with some staged (null or none: as they are): ResearchPreview.DRAWS draws from ResearchPreview.SEED, cached. |

### the preview's words (lines 767-789)

| line | len | member | says |
|---:|---:|---|---|
| 773 | 3 | `public static boolean asChance(double before, double after)` | Whether a card shows its preview as a chance and not a count: when the larger of the expected finds before and after the staged dials is under 1, a chance of at least one is what is worth saying ("0.2% to 25%"); from ... |
| 781 | 8 | `public static String previewWords(double finds, double chance, boolean asChance)` | The preview in words: the chance of at least one find as a whole percent when `asChance`, else the expected finds to two places. |

