# ResearchCheck.java - 755 lines · 20 methods · 6 constants · harnesses

`ham/citybuildersim/ResearchCheck.java` - generated 2026-11-07 by CodeMap; line numbers are as of that run.

> The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses
> what is not a discovery (R2, slice A: ResearchCatalog and research.json; docs/studies/research-design.md 4, research-1900.md 8).
> 
> WHY. research.json is 89 entries typed from two studies' tables, and a wrong figure in it is no error anywhere: a discovery a year late is an
> oxygen converter that arrives in 1953, and nobody sees it until the ensemble moves. So the dates, the needs, the work and the fees are held to
> the studies' tables here, with spot values typed out below as the specification and not read back from the class under test; the law rows are
> held to PolicyDates, whose dates the Policy gate already reads, so the catalogue and the gate cannot disagree about when a lever opens.
> 
> What it has to prove:
> 
>   1. THE FILE READS. The packaged file gives 89 entries and a fresh reading of its text reports nothing (no entry skipped); the codes are
>      unique and in the studies' order; the counts by prefix and by field are the studies'; every entry has a name, a world, a touches and an
>      effect; the kinds, the kind notes and the names of a few are the tables'.
>   2. NEEDS. Every need is a code in the catalogue, no entry needs itself, history dates each need no later than its entry; the tables' 26
>      needs of 23 entries are there; M1 needs PM6 (research-1900 8.6), E18 needs E13 and E9, PE17 needs PE14.
>   3. HISTORY'S DATES. Every entry's world month is PolicyDates.monthOf() of its year and month; values typed from the tables (a dated month, and
>      the study's model's month where the table names none); every PolicyDates lever with an entry key is a LAW entry on the lever's own year,
>      month and monthKnown; PL1 and PL11, which have no lever, are in their year's January; nothing is known at the founding.
>   4. THE WORK. Every entry's work is the rule's, rounded, to within 1; values typed from the tables; the size of the work is the table's (a
>      Walk-in Clinic is a recipe's work); the rule's constants are the study's and a discovery costs more every year.
>   5. KNOWN FROM. A year is known from its January, a code from its entry's month, a blank "since" or a code the catalogue lacks never; every
>      code buildings.json uses is in the catalogue; the catalogue is one list, unmodifiable, and the same for every thread.
>   6. THE READER'S REFUSALS, AND THE FEES. A duplicate code keeps the first and reports; an unknown field, size or kind, a month 13, a year
>      before 1900, a negative work and a blank code are skipped and reported; a need that is not in the catalogue and one dated after its entry
>      are reported and the entry stays; a text that is not JSON gives an empty catalogue. And the fees: no law entry carries one but L4 (the DNA lab, equipment), and the tables'
>      yes and no of a few engineering, medicine and business entries are as typed.
>   7. BUILDINGS.JSON'S CODES (slice B). Every "since" that is not a year, on a template, a recipe or a piece, is a code in the catalogue: a
>      template's one that unlocks a building, a piece's one that unlocks equipment, a recipe's one that is not a building alone. The
>      code-dated templates are exactly research-1900.md 1.2's list typed out by id (29), the Oxygen Steel Shop is dated 1900, the Panel Mill 1980
>      and the Wood Pellet Mill 1970, every other row undated or 1900; every building entry opens a template but PE12 and B8; a template's year
>      is its entry's. A building file read from text keeps a code as written, reports one the catalogue lacks and never knows it.
>   8. KNOWN FROM HISTORY'S MONTH. The tractor's piece and recipe are known from PE14's month and not the month before, the oxygen converter's
>      recipe from E1's (November 1952), the Container Terminal from E5's, the Electric Arc Furnace from PE1's December 1900, the Pulp Mill's kraft
>      recipe from January 1907; an undated row, and one dated 1900, from the first month; and asking by the year (knownIn) agrees with asking
>      by the month of that year's January, 1900 to 1960, and nothing dated 1900 is known the year before the start.
>   9. THE VERDICT AND THE CHOICE ON HISTORY'S MONTH. EquipmentFit.judge() for the tractor with figures that pay is NOT_KNOWN the month before
>      PE14's and PAYS at it (and figures a thousandth short of paying are NOT_YET at it, so the rule runs), and Recipes.canRun() for the
>      oxygen converter's recipe is false the month before E1's and true at it, for the part that may run it (the one without the open hearth).

**Uses:** [ResearchCatalog](ResearchCatalog.md) (51), [PolicyDates](PolicyDates.md) (17), [BuildingsTemplate](BuildingsTemplate.md) (14), [EquipmentFit](EquipmentFit.md) (12), [Recipes](Recipes.md) (11), [CityCalendar](CityCalendar.md) (6), [Equipment](Equipment.md) (5), [BuildingCatalog](BuildingCatalog.md) (3), [Recipe](Recipe.md) (2), [BuildingType](BuildingType.md) (1), [BusinessInvestment](BusinessInvestment.md) (1)

## Sections

| line | section |
|---:|---|
| 81 | · the specification, typed from the studies' tables |
| 152 | · fixtures for the reader |
| 211 | · 1. the file reads |
| 267 | · 2. needs |
| 297 | · 3. history's dates |
| 339 | · 4. the work |
| 382 | · 5. known from |
| 426 | · 6. the reader's refusals, and the fees |
| 519 | 7. buildings.json's codes (R2, slice B) |
| 630 | 8. known from history's month, 9. the verdict and the choice on it (R2, slice B) |
| 718 | · helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 87 | `ResearchCheck.DATES` | `List.of(new Spot("E1", 1952, 11, true), new Spot("E5", 1956, 4, true), new Sp...` | research-design.md 4 and research-1900.md 8: the World column's month where it names one, the prototype's where it does not. |
| 132 | `ResearchCheck.MONTH_KNOWN` | `30` | The entries whose World column names a month: the table's count. |
| 135 | `ResearchCheck.PREFIXES` | `{ "E", "M", "B", "L", "PE", "PM", "PB", "PL" }` | The prefixes in the studies' order, and how many entries each has. |
| 136 | `ResearchCheck.PREFIX_COUNTS` | `{ 18, 10, 8, 5, 27, 6, 3, 12 }` |  |
| 139 | `ResearchCheck.WORK` | `{ { "E1", 444 }, { "E5", 545 }, { "E12", 1013 }, { "E18", 2998 }, { "M3", 594...` | Work typed from the tables (researcher-years at the date); PL1 and PL12 are the 8.4 text's, L5 the 8.6 rule's. |
| 522 | `ResearchCheck.CODED` | `new TreeMap<>(Map.ofEntries(Map.entry(11, "PE1"), Map.entry(119, "PE4"), Map....` | research-1900.md 1.2 and the R2 brief: the templates dated by a code, id to the entry that opens each; typed here as the specification. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 67 | `private static int fails` |  |
| 68 | `private static int passes` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 65 | 691 | **type** `public class ResearchCheck` | The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses what is not a discovery (R2, slice A: ResearchCatalog and research.json; d... |
| 70 | 4 | `static void check(String label, boolean ok)` |  |
| 76 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |

### the specification, typed from the studies' tables (lines 81-151)

| line | len | member | says |
|---:|---:|---|---|
| 84 | 1 | **type** `record Spot(String code, int year, int month, boolean monthKnown)` | One date typed from a table: the year, the month and whether the table's World column names the month (else the study's model's month). |
| 144 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month, worked out here and not by PolicyDates. |
| 148 | 3 | `static String prefixOf(String code)` |  |

### fixtures for the reader (lines 152-518)

| line | len | member | says |
|---:|---:|---|---|
| 154 | 5 | `private static String quoted(String...items)` |  |
| 161 | 4 | `static String entryJson(String code, String field, String size, String kind, int year, int month, int work, String...needs)` | One entry's JSON, with the fields the reader refuses on; the rest it reads as absent. |
| 166 | 3 | `static String file(String...entries)` |  |
| 171 | 3 | `static String good(String code, String...needs)` | A good entry: an engineering building in 1960. |
| 176 | 1 | **type** `record Read(ResearchCatalog catalogue, String log)` | What a reading wrote and what it made. |
| 179 | 12 | `static Read read(String json)` | Reads a text through the class's parse() with System.out captured, so that what it reports can be asserted. |
| 192 | 5 | `static String text(String resource) throws Exception` |  |
| 198 | 3 | `static Entry of(String code)` |  |
| 202 | 3 | `static boolean has(Entry e, List<String> kinds)` |  |
| 206 | 312 | `public static void main(String[] args) throws Exception` |  |

### 7. buildings.json's codes (R2, slice B) (lines 519-629)

| line | len | member | says |
|---:|---:|---|---|
| 531 | 4 | `static BuildingsTemplate byId(List<BuildingsTemplate> templates, int id)` |  |
| 536 | 93 | `static void sectionSeven()` |  |

### 8. known from history's month, 9. the verdict and the choice on it (R2, slice B) (lines 630-717)

| line | len | member | says |
|---:|---:|---|---|
| 632 | 85 | `static void sectionEightAndNine()` |  |

### helpers (lines 718-755)

| line | len | member | says |
|---:|---:|---|---|
| 720 | 5 | `static List<String> codes(ResearchCatalog c)` |  |
| 727 | 9 | `static List<String> orderOf()` | The studies' order: E, M, B, L1-L4, PE, PM, PB, PL, then L5. |
| 738 | 9 | `static boolean allLaw(ResearchCatalog c, String prefix, String note)` | Whether every entry whose code starts with a prefix and a digit is a recipe with that kind note. |
| 748 | 7 | `static boolean drugs(ResearchCatalog c)` |  |

