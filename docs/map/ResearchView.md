# ResearchView.java - 1,154 lines · 94 methods · 10 constants · model

`ham/citybuildersim/ResearchView.java` - generated 2026-11-24 by CodeMap; line numbers are as of that run.

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
> default way every discovery but the near future's (R6b's call 1) is known from the start, nothing arrives, nothing is chosen and nothing is unfitted, so
> every list here is empty, every origin null and knownAtFounding() the catalogue less the near future's: the screens then draw nothing new, and every city
> a player has played keeps its screens.
> 
> TWO LAYERS, as EquipmentFit and Recipes are: the cores that take a catalogue, a research and a month (package-private) let a harness put the city at
> any month and the catalogue at any shape; the public reads take the game and pass its own.

**Uses:** [Research](Research.md) (43), [Game](Game.md) (24), [ResearchCatalog](ResearchCatalog.md) (16), [BuildingsTemplate](BuildingsTemplate.md) (16), [ResearchPreview](ResearchPreview.md) (12), [Pieces](Pieces.md) (11), [LicenceFees](LicenceFees.md) (8), [EducationType](EducationType.md) (7), [BuildingsStacks](BuildingsStacks.md) (6), [CityCalendar](CityCalendar.md) (5), [Recipe](Recipe.md) (5), [Equipment](Equipment.md) (4), [Dials](Dials.md) (4), [DecisionLog](DecisionLog.md) (3), [JobType](JobType.md) (3), [EquipmentFit](EquipmentFit.md) (2), [BuildingManager](BuildingManager.md) (2), [PolicyDates](PolicyDates.md) (1)

**Used by (15):** [BuildScreen](BuildScreen.md), [FB13Check](FB13Check.md), [HistoricalRecipeCheck](HistoricalRecipeCheck.md), [HistorySave](HistorySave.md), [HistoryScreen](HistoryScreen.md), [Inbox](Inbox.md), [KeptBench](KeptBench.md), [KeptCheck](KeptCheck.md), [ResearchCheck](ResearchCheck.md), [ResearchPreviewCheck](ResearchPreviewCheck.md), [ResearchScreen](ResearchScreen.md), [ResearchViewCheck](ResearchViewCheck.md), [SectorScreen](SectorScreen.md), [ServicesScreen](ServicesScreen.md), [TimeChart](TimeChart.md)

## Sections

| line | section |
|---:|---|
| 55 | THE TIMELINE |
| 189 | THE FLAGS ON CITY HISTORY |
| 209 | THE EQUIPMENT |
| 294 | THE RECIPES |
| 358 | WHERE A BUILDING CAME FROM |
| 424 | · the licence line (FB13, R5-L; research-design 2.4, mockup 5) |
| 487 | THE LAID-ON AXIS AND WHAT IS STILL TO COME (R3b, from R5's gate review: the Found page worked these out itself) |
| 521 | THE OVERVIEW (R3b; docs/studies/research-design.md 5.5, 6.2) |
| 682 | THE FUNDING PAGE (R3b; docs/studies/research-design.md 5.1, 5.4) |
| 820 | · the preview |
| 894 | · the preview's words |
| 917 | WHAT THE SCREENS' CARDS ARE DRAWN FROM (R3b, slice B: "no arithmetic on model figures in ui/") |
| 1088 | · the schools |

## Enum constants

| line | constant | says |
|---:|---|---|
| 40 | `ResearchView.Source.HERE` |  |
| 40 | `ResearchView.Source.ABROAD` |  |
| 427 | `ResearchView.LicenceKind.HERE` |  |
| 427 | `ResearchView.LicenceKind.OWN` |  |
| 427 | `ResearchView.LicenceKind.FREE` |  |
| 427 | `ResearchView.LicenceKind.UNDER` |  |
| 427 | `ResearchView.LicenceKind.ENDED` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `ResearchView.FIRST_MONTH` | `1` | The month every city is founded in. |
| 50 | `ResearchView.NEW_MONTHS` | `12` | How many months a building is NEW on its card after it became known in the city: it is new while its age (the city's month less the month it became known) is under this. |
| 53 | `ResearchView.FLAG_KIND` | `"discovery"` | The kind of the flags a discovery puts on City History's decision lane (flagEntries()); a discovery is not a decision, so it is no DecisionLog kind. |
| 847 | `ResearchView.CACHE_SIZE` | `32` | The preview's answers kept, the least recently read dropped first: a card's dials are read again at every redraw of the screen, and the answer does not change until a dial or the month does. |
| 848 | `ResearchView.CACHE` | `new java.util.LinkedHashMap<>(16, 0.75f, true) { @ Override protected boolean...` |  |
| 942 | `ResearchView.GRANT_REACH` | `1.2` | How far past the room the grant dial reaches, as a share of the room, and the least number of posts it reaches past it (a grant past the room buys nothing, and the player may try one). |
| 943 | `ResearchView.GRANT_REACH_POSTS` | `3` |  |
| 969 | `ResearchView.PRIZE_DIAL_YEARS` | `1000` | The researcher-years of work a prize dial reaches for, at the least: a problem of that size, paid at a post's cost a month, is its top. |
| 972 | `ResearchView.PRIZE_REACH` | `2` | How many times the least prize that brings a lab the prize dial reaches, at the least. |
| 975 | `ResearchView.PRIZE_STEPS` | `100` | The steps a prize dial is divided into. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 35 | 1120 | **type** `public final class ResearchView` | What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each sector's stacks carry with the verdict on it, the recipes in use, how a bu... |
| 37 | 1 | `private ResearchView()` |  |
| 40 | 1 | **type** `public enum Source` | How a discovery reached the city: found here by its own researchers before history's date, or from abroad on history's date. |

### THE TIMELINE (lines 55-188)

| line | len | member | says |
|---:|---:|---|---|
| 60 | 17 | **type** `public record Discovery(Entry entry, int month, Source source)` | One discovery of the city's life: its catalogue entry, the month the city came to know it and how. |
| 63 | 3 | `public int worldMonth()` _(in ResearchView.Discovery)_ | The month history has it in. |
| 68 | 3 | `public double yearsAhead()` _(in ResearchView.Discovery)_ | How many years before history's date the city found it: (worldMonth - month) / 12 for one found here, 0 for one from abroad. |
| 73 | 3 | `public ResearchCatalog.Field field()` _(in ResearchView.Discovery)_ | The field that researches it. |
| 79 | 3 | `private static int monthHere(Research research, Entry e)` | The month the city knows an entry from: the month it found it, where that is before history's date, else history's. |
| 84 | 3 | `private static Source sourceOf(Research research, Entry e)` | How the city came to know an entry: HERE where it found it before history's date, else ABROAD on history's date. |
| 92 | 11 | `static List<Discovery> timeline(ResearchCatalog catalogue, Research research, int month)` | The timeline of a research at a month, against a catalogue (the core): every entry the city came to know after FIRST_MONTH and by `month`, with the month it came to know it and how, ordered by month and, inside a mont... |
| 109 | 3 | `public static List<Discovery> timeline(Game game)` | The city's discoveries so far, oldest first: in a city founded the 1900 way, each entry known by the city's month whose month in this city is after FIRST_MONTH - the month it was found here (Research.foundMonth()) whe... |
| 114 | 5 | `static int knownAtFounding(ResearchCatalog catalogue, Research research)` | How many entries the city knew at the founding (the core): those whose month in this city is FIRST_MONTH or before. |
| 124 | 4 | `public static int knownAtFounding(Game game)` | How many discoveries the city knew at the founding: the entries history dates at FIRST_MONTH or before in a city founded the 1900 way, and in a city founded the default way every entry that is not the near future's (E... |
| 130 | 5 | `public static int nearFutureCount()` | How many of the catalogue's discoveries are the near future's (Entry.isNearFuture()): the ones a city founded the default way never knows. |
| 140 | 4 | `public static String foundedKnowingWords(Game game)` | What the Research screens say of a city founded the default way, for each to finish: "founded knowing all 91 of the catalogue's discoveries dated before January 2025; the near future's 3 come only to a city founded th... |
| 149 | 3 | `public static String marked(Entry entry, String words)` | A date's words with the entry's mark after them in brackets ("from abroad, June 2028 (projected, 2027-2030)"), and the words alone for a history row or no entry, so that no screen prints a projection as history (the s... |
| 154 | 5 | `public static int count(List<Discovery> timeline, Source source)` | How many of the discoveries came by a source. |
| 161 | 6 | `public static EnumMap<ResearchCatalog.Field, List<Discovery>> byField(List<Discovery> timeline)` | The timeline split by field: every field present, in the enum's order, possibly with an empty list; each list keeps the timeline's order. |
| 173 | 15 | `public static int[][] cumulative(List<Discovery> timeline, int firstMonth, int lastMonth)` | The count of discoveries at or before each month of City History's axis, for the graph of found here against from abroad: {here[], abroad[]}, entry i the count at or before month firstMonth + i, so entry 0 holds every... |

### THE FLAGS ON CITY HISTORY (lines 189-208)

| line | len | member | says |
|---:|---:|---|---|
| 198 | 10 | `public static List<DecisionLog.Entry> flagEntries(Game game)` | One entry for each discovery of the timeline, oldest first, of kind FLAG_KIND at the discovery's month: "Found here: Electric arc steel furnace, 0.8 years before the world" (one decimal, and always "years") or "From a... |

### THE EQUIPMENT (lines 209-293)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 13 | **type** `public record PieceRow(BuildingsTemplate template, Equipment piece, long buildings, long unfitted, long onS...` | One piece of equipment on one stack, as the Investors page and the Found page list it: how many of the stack's buildings there are, how many lack the piece, how many are being fitted now, the verdict on the piece (the... |
| 222 | 3 | `public long fitted()` _(in ResearchView.PieceRow)_ | The buildings that carry the piece: the buildings less those that lack it. |
| 227 | 3 | `public double share()` _(in ResearchView.PieceRow)_ | The share of the stack that carries the piece, 0 to 1 (the bar on the Found page): fitted() over the buildings, 0 where there are none. |
| 233 | 1 | **type** `private record Standing(BuildingsStacks stack, Equipment piece, long buildings, long unfitted)` | One stack's piece, before it is judged: what both equipment() and fittedShare() read. |
| 236 | 18 | `private static List<Standing> standing(Game game, String sectorKey)` | The pieces standing in the city's stacks, known at the city's month, in the templates' order and each template's pieces' order; `sectorKey` null is the whole city. |
| 260 | 11 | `public static List<PieceRow> equipment(Game game, String sectorKey)` | The equipment standing in the city, in a city founded the 1900 way: for each template with a stack of buildings (the sector's own when `sectorKey` is not null, the whole city's when it is), in the templates' order, a ... |
| 277 | 9 | `public static double fittedShare(Game game)` | The share of the equipment standing that is fitted, 0 to 1: over every row of equipment(game, null), the buildings that carry the piece over the buildings. |
| 288 | 5 | `private static Map<String, Discovery> discoveriesByCode(List<Discovery> timeline)` | The discoveries of a timeline by catalogue code. |

### THE RECIPES (lines 294-357)

| line | len | member | says |
|---:|---:|---|---|
| 303 | 7 | **type** `public record RecipeUse(BuildingsTemplate template, Recipe recipe, long running, long buildings, Discovery ...` | A way of running a template other than its default, known in the city: how many of the template's `buildings` run it now (the fitted part and the unfitted part that chose it, as Recipes.apply() weighs them; 0 for one ... |
| 306 | 3 | `public double share()` _(in ResearchView.RecipeUse)_ | The share of the stack that runs the recipe, 0 to 1 (the bar on the Found page): running over the buildings, 0 where there are none. |
| 318 | 39 | `public static List<RecipeUse> recipesInUse(Game game)` | The recipes in use, in a city founded the 1900 way: for each template with a choice of recipes and a stack of buildings, each of its recipes other than the default that is known at the city's month, resolving selected... |

### WHERE A BUILDING CAME FROM (lines 358-423)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 7 | **type** `public record Origin(BuildingsTemplate template, int month, Source source, Entry entry)` | How a template became known to the city after the founding: the month, whether the city found it or the world did, and the discovery when its "since" is a catalogue code (null when it is a year). |
| 369 | 3 | `public double yearsAhead()` _(in ResearchView.Origin)_ | How many years before history's date the city found it: as Discovery's, 0 for one from abroad and for one with no entry. |
| 379 | 13 | `static Origin origin(ResearchCatalog catalogue, Research research, int month, BuildingsTemplate t)` | The origin of a template for a research at a month, against a catalogue (the core): a since that is a catalogue code - the month and source the timeline reads, and the entry; a since that is a year after 1900's Januar... |
| 397 | 3 | `public static Origin origin(Game game, BuildingsTemplate t)` | How a template became known after the founding, in a city founded the 1900 way (see the core). |
| 402 | 3 | `static boolean isNew(Origin origin, int month)` | Whether an origin is NEW at a month (the core): there is one, and it is fewer than NEW_MONTHS months old. |
| 407 | 3 | `public static boolean isNew(Game game, BuildingsTemplate t)` | Whether a template is NEW on its card: it has an origin, and became known fewer than NEW_MONTHS months before the city's month. |
| 412 | 6 | `public static List<BuildingsTemplate> newTemplates(Game game)` | The templates that are NEW, in the templates' order. |
| 420 | 3 | `public static boolean isLab(BuildingsTemplate t)` | Whether a template is a Research Lab (R4's four): it researches a field. |

### the licence line (FB13, R5-L; research-design 2.4, mockup 5) (lines 424-486)

| line | len | member | says |
|---:|---:|---|---|
| 427 | 1 | **type** `public enum LicenceKind` | What a building's licence is: patents running on one more of it, or - with none running - found here, found here since, not patented, or its patent ended. |
| 435 | 5 | **type** `public record Licence(LicenceKind kind, java.util.List<LicenceFees.Patent> patents, double perMonth, Entry ...` | What one more of a building owes for its patents (R2c, R2c-P), read from LicenceFees.rentOnNew(), the reckoning the planners' running cost sums: its kind, the patents that are running among those the bill is made of (... |
| 438 | 1 | `public int ownEnds()` _(in ResearchView.Licence)_ | The month the building's own patent ends (history's date and LicenceFees.TERM_MONTHS); 0 where it has no entry or none to end. |
| 448 | 11 | `public static Licence licence(Game game, BuildingsTemplate t)` | The licence of a template in a city founded the 1900 way, or null where its card has no line (a city founded the default way, and a building with no patent running and no discovery of its own to say it was found here). |
| 461 | 3 | `public static String shareWords()` | The world's share as the line says it: "a quarter" for LicenceFees.SHARE's .25, else a percentage. |
| 471 | 15 | `public static String licenceWords(Licence l, java.util.function.DoubleFunction<String> money)` | The card's line on its licence (mockup 5): "Licence: a quarter of what it adds goes abroad until June 1975, about D$1.17M a month." while one patent runs, "... |

### THE LAID-ON AXIS AND WHAT IS STILL TO COME (R3b, from R5's gate review: the Found page worked these out itself) (lines 487-520)

| line | len | member | says |
|---:|---:|---|---|
| 496 | 12 | `public static double[][] cumulativeOn(List<Discovery> timeline, List<Integer> axis)` | The counts of found here and from abroad laid on History's months: {here[], abroad[]}, entry i the count at or before the axis' month i, each as long as the axis (cumulative() over the axis' first to last month, then ... |
| 510 | 3 | `public static int stillToCome(int all, int knownAtFounding, int here, int abroad)` | How many discoveries are still to come (the core): the catalogue's `all` less those known at the founding, found here and come from abroad, never below none. |
| 515 | 5 | `public static int stillToCome(Game game)` | How many of the catalogue's discoveries are still to come in the city: those history has not yet dated and the city has not found, in a city founded the 1900 way; none in any other (the near future's come to no city f... |

### THE OVERVIEW (R3b; docs/studies/research-design.md 5.5, 6.2) (lines 521-681)

| line | len | member | says |
|---:|---:|---|---|
| 526 | 1 | **type** `public record SchoolCount(String name, long count)` | A school standing that hosts a field's research, by its template's name and how many of it stand. |
| 532 | 23 | **type** `public record FieldReach(Field field, double researchers, double labResearchers, double idle, int inReach, ...` | One field on the Overview page: its researchers (the city's own and the labs'), the idle among them, the problems open to them and what those touch, what the field has found here and had come from abroad, and the scho... |
| 536 | 3 | `public double total()` _(in ResearchView.FieldReach)_ | Everyone at the field's research this month: the city's researchers and the labs'. |
| 541 | 3 | `public double working()` _(in ResearchView.FieldReach)_ | The people at work on a problem: everyone less the idle. |
| 546 | 3 | `public String reachWords()` _(in ResearchView.FieldReach)_ | "1 in reach: rail", or "nothing in reach" (reachWords()). |
| 551 | 3 | `public String schoolsWords()` _(in ResearchView.FieldReach)_ | "2 Institutes of Technology, 2 Universities" (schoolsWords()). |
| 561 | 31 | **type** `public record Overview(List<FieldReach> fields, double researchBill, double prizesPaid, int foundHere, int ...` | The Overview page: a row for each field in the enum's order, the month's research bill and the prizes the treasury paid the labs, the discoveries found here and come from abroad since the founding, the researchers the... |
| 565 | 5 | `public double researchers()` _(in ResearchView.Overview)_ | The people at research in all the fields, the labs' too. |
| 572 | 5 | `public double idle()` _(in ResearchView.Overview)_ | The idle among them. |
| 579 | 5 | `public double working()` _(in ResearchView.Overview)_ | The people at work on a problem in all the fields: everyone less the idle (R3b's Overview shows it, so the screen adds nothing up). |
| 586 | 5 | `public int inReach()` _(in ResearchView.Overview)_ | The problems open to the city in all the fields. |
| 594 | 4 | `public static String reachWords(int inReach, List<String> touches)` | The words for what is in reach: "nothing in reach" for none, else "2 in reach: Wind Farm; Coal Power Plant", the distinct things the problems touch, in the catalogue's order. |
| 600 | 8 | `static List<String> touchesOf(List<Entry> problems)` | What a list of problems touches, as the catalogue words it (Entry.touches()): each distinct text once, in the order of the problems. |
| 610 | 6 | `public static String schoolsWords(List<SchoolCount> schools)` | The words for the schools that host a field: "2 Institutes of Technology, 2 Universities", or "no school" where none stands. |
| 618 | 6 | `static String plural(String name)` | A school's name in the plural: "University" to "Universities", "Institute of Technology" to "Institutes of Technology", "Law School" to "Law Schools". |
| 626 | 10 | `static List<SchoolCount> schoolsOf(Game game, Field f)` | The standing schools that host a field, the field's own kind first and the Universities after, each by its template's name and how many stand. |
| 642 | 11 | `static FieldReach reach(Research research, int month, Field f, List<Discovery> timeline, List<SchoolCount> schools, boolean res...` | One field's row of the Overview (the core): the research as the last month left it, the problems open to the field at `month` and what they touch, and the field's part of the timeline. |
| 655 | 3 | `public static double perThousandWorkers(double researchers, long workers)` | Researchers to each 1,000 people at work (the core): NaN where nobody works. |
| 665 | 16 | `public static Overview overview(Game game)` | The Overview page of the city: each field's researchers (the city's own and the labs'), the idle, the problems in reach and what they touch, the discoveries here and from abroad, and the schools that host it; the mont... |

### THE FUNDING PAGE (R3b; docs/studies/research-design.md 5.1, 5.4) (lines 682-819)

| line | len | member | says |
|---:|---:|---|---|
| 691 | 18 | **type** `public record Funding(Field field, double grant, double prize, int posts, int grantPosts, double room, doub...` | One field's dial card as it stands: the grant and the prize, the posts struck this month (the grant's own and the pot's), the room the schools have for posts and the grant that would fill it, the researchers the facul... |
| 695 | 3 | `public int potPosts()` _(in ResearchView.Funding)_ | The posts the field's pot bought beyond the grant (Research.strikePosts()). |
| 700 | 3 | `public double researchers()` _(in ResearchView.Funding)_ | Everyone at the field's own research at these dials: the faculty and the posts' researchers. |
| 705 | 3 | `public int roomPosts()` _(in ResearchView.Funding)_ | The whole posts the schools have room for (Research.posts() buys whole posts and never past the room): "room for 59 posts". |
| 711 | 1 | **type** `public record AtGrant(double grant, int posts, int grantPosts, double postResearchers, double researchers, ...` | What a grant would do, staged and not applied: the posts it buys, the researchers they and the faculty give and what the posts cost a month. |
| 717 | 26 | **type** `public record Staged(Map<Field, Double> grants, Map<Field, Double> prizes)` | The dials a player has staged and not applied: a grant and a prize for the fields the player has moved. |
| 719 | 4 | `{ ... }` _(in ResearchView.Staged)_ |  |
| 725 | 3 | `public static Staged none()` _(in ResearchView.Staged)_ | Nothing staged: every dial as it is. |
| 730 | 5 | `public Staged withGrant(Field f, double grant)` _(in ResearchView.Staged)_ | The same with a field's grant staged. |
| 737 | 5 | `public Staged withPrize(Field f, double prize)` _(in ResearchView.Staged)_ | The same with a field's prize staged. |
| 748 | 2 | **type** `record Pieces(Research research, int month, boolean researching, double[] wages, double[] fill, Map<Field, ...` | Everything the funding reads of a city, taken once: the research, the month, the wages and the fill the posts were struck at (null before any month: every post then counts as filled), whether the city researches at al... |
| 751 | 4 | `static Pieces piecesOf(Game game)` |  |
| 757 | 3 | `static double roomOf(Pieces p, Field f)` | The posts a field's schools have room for (Research.room() of their university-band staff), and nothing where none stands. |
| 762 | 3 | `static double facultyOf(Pieces p, Field f)` | The researcher-FTE a field's faculty give this month (Research.faculty() of the staffed staff). |
| 771 | 11 | `static double labThreshold(Research research, int month, Field f, double[] wages)` | The least prize that would bring a lab to a field (the core): the least remaining work of an open problem that a team at its limit could finish before the world, times twelve researcher-years and a post's cost (the pu... |
| 784 | 10 | `static Funding funding(Pieces p, Field f, long labsStanding, long labsOnSite)` | One field's dial card as it stands (the core), the labs being the ones standing and on site. |
| 796 | 3 | `public static Funding funding(Game game, Field f)` | One field's dial card as it stands in the city. |
| 806 | 8 | `static AtGrant atGrant(Pieces p, Field f, double grant)` | What a grant in a field would do (the core): the posts Research.posts() says it and the field's pot buy at the wages now within the room, those the grant alone buys, the researchers the filled ones give at the fill th... |
| 816 | 3 | `public static AtGrant atGrant(Game game, Field f, double grant)` | What a staged grant in a field would do in the city. |

### the preview (lines 820-893)

| line | len | member | says |
|---:|---:|---|---|
| 827 | 1 | **type** `record Dials(double[] researchers, double[] prize, double[] labFill, double[] wages)` | The dials a preview is made at, field by field (by Field.ordinal()): the researchers held fixed (the faculty and the filled posts of the grant), the prize and the share of a lab's posts filled; and the wages. |
| 829 | 16 | `static Dials dialsOf(Pieces p, Staged staged)` |  |
| 855 | 5 | `private static List<Double> boxed(double[]...arrays)` |  |
| 866 | 17 | `static ResearchPreview.Result preview(Research research, int month, Dials dials, int draws, long seed)` | The preview at some dials (the core): ResearchPreview.preview() of the research at the month, CACHED. |
| 889 | 4 | `public static ResearchPreview.Result preview(Game game, Staged staged)` | What the city's research would find here over the next ResearchPreview.YEARS years at the dials as they are, or with some staged (null or none: as they are): ResearchPreview.DRAWS draws from ResearchPreview.SEED, cached. |

### the preview's words (lines 894-916)

| line | len | member | says |
|---:|---:|---|---|
| 900 | 3 | `public static boolean asChance(double before, double after)` | Whether a card shows its preview as a chance and not a count: when the larger of the expected finds before and after the staged dials is under 1, a chance of at least one is what is worth saying ("0.2% to 25%"); from ... |
| 908 | 8 | `public static String previewWords(double finds, double chance, boolean asChance)` | The preview in words: the chance of at least one find as a whole percent when `asChance`, else the expected finds to two places. |

### WHAT THE SCREENS' CARDS ARE DRAWN FROM (R3b, slice B: "no arithmetic on model figures in ui/") (lines 917-1087)

| line | len | member | says |
|---:|---:|---|---|
| 925 | 12 | `public static String reachByFieldWords(List<FieldReach> fields)` | "engineering 6 · medicine 2 · business and law none": the problems in reach by field, the fields with none named together at the end. |
| 939 | 1 | **type** `public record Dial(double min, double max, double step)` | A dial's range: from `min` to `max` in steps of `step`. |
| 951 | 8 | `public static Dial grantDial(Funding f)` | The grant dial of a field: from nothing in steps of a round figure (1, 2 or 5 times a power of ten) not above a post's cost, to a little past the grant that fills the room (GRANT_REACH) - and past the grant in force, ... |
| 961 | 6 | `static double niceFloor(double x)` | The greatest of 1, 2 and 5 times a power of ten that is not above `x` (0 for x not above nothing). |
| 978 | 6 | `static double niceCeil(double x)` | The least of 1, 2 and 5 times a power of ten that is not below `x` (0 for x not above nothing). |
| 989 | 7 | `public static Dial prizeDial(Funding f)` | The prize dial of a field: from nothing to a round number (1, 2 or 5 times a power of ten) that reaches PRIZE_DIAL_YEARS researcher-years of a post's cost, twice the least prize that brings a lab (PRIZE_REACH) and the... |
| 998 | 3 | `public static long labPosts(long labs)` | The researchers that `labs` Research Labs give once they are staffed: Research.LAB_POSTS posts each. |
| 1003 | 5 | **type** `public record Budget(double before, double after)` | THE BUDGET a month before and after what is staged: the balance as the accounts booked it and the same less what the staged grants' posts would add to the research bill. |
| 1004 | 3 | `public double change()` _(in ResearchView.Budget)_ |  |
| 1013 | 12 | `public static Budget budget(Game game, Staged staged)` | The balance with the staged grants in place of the ones in force: a staged grant buys the posts atGrant() says, and their bill replaces the bill of the posts struck. |
| 1033 | 35 | **type** `public record Card(Field field, FieldReach reach, Funding funding, AtGrant at, boolean researching, double ...` | One field's dial card as the Funding page draws it, at the dials shown on it (the grant and prize in force, or staged ones): the researchers the field has now and with them, in grant posts and in labs, what the posts ... |
| 1039 | 3 | `public boolean moved()` _(in ResearchView.Card)_ | Whether something is staged on the card. |
| 1044 | 3 | `public boolean asChance()` _(in ResearchView.Card)_ | Whether the card shows its preview as a chance and not a count (asChance() of the two figures). |
| 1049 | 3 | `public String findsNowWords()` _(in ResearchView.Card)_ | "none", "0.2%", "1.10": the preview at the dials in force. |
| 1054 | 3 | `public String findsAfterWords()` _(in ResearchView.Card)_ | ...and at the dials shown. |
| 1059 | 3 | `public boolean roomFull()` _(in ResearchView.Card)_ | Whether the shown grant buys every post the room has: more money buys nothing. |
| 1064 | 3 | `public boolean unspends()` _(in ResearchView.Card)_ | Whether some of the shown grant goes unspent: at least half a dollar of it (the money is counted in thousands). |
| 1070 | 17 | `public static Card card(Game game, Field f, Staged staged, FieldReach reach, ResearchPreview.Result nowResult, ResearchPreview....` | The field's card at the dials `staged` names (a dial it does not name is as it is in force): see Card. |

### the schools (lines 1088-1154)

| line | len | member | says |
|---:|---:|---|---|
| 1091 | 1 | **type** `public record SchoolPosts(Field field, double posts, double filled, double room)` | The posts of a field that a kind of school carries: the posts struck in the field by this kind's share of its room, the researchers they give, and the room this kind has. |
| 1094 | 3 | `public static boolean hostsResearch(EducationType kind)` | Whether a kind of school hosts research in any field (the University, and the Institute of technology, Medical, Business and Law schools). |
| 1099 | 14 | `static EnumMap<Field, Double> staffOfKind(Game game, EducationType kind)` | The nominal university-band staff a kind of school stands up in each field, as Game.researchStaff(null) sums them over every kind. |
| 1119 | 15 | `public static List<SchoolPosts> schoolPosts(Game game, EducationType kind)` | What a kind of school carries of the city's research posts: posts are struck by field and not by school, so a kind has its share of each field it hosts - the posts struck there and the researchers they give, by the ki... |
| 1136 | 1 | **type** `public record SchoolRow(EducationType kind, String name, long count, List<SchoolPosts> posts)` | A hosting kind of school standing in the city, as the Overview lists it: its name, how many stand and what it carries. |
| 1139 | 15 | `public static List<SchoolRow> schoolRows(Game game)` | The kinds of school that host research and stand in the city, in the education kinds' order. |

