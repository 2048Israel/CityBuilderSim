# ResearchCheck.java - 1,069 lines · 26 methods · 10 constants · harnesses

`ham/citybuildersim/ResearchCheck.java` - generated 2026-11-21 by CodeMap; line numbers are as of that run.

> The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses
> what is not a discovery (R2, slice A: ResearchCatalog and research.json; docs/studies/research-design.md 4, research-1900.md 8).
> 
> WHY. research.json is 96 entries, 91 typed from two studies' tables, the near future's three (R6b) and the speculative tier's two (R6d), and a wrong figure in it is no error anywhere: a discovery a year late is an
> oxygen converter that arrives in 1953, and nobody sees it until the ensemble moves. So the dates, the needs, the work and the fees are held to
> the studies' tables here, with spot values typed out below as the specification and not read back from the class under test; the law rows are
> held to PolicyDates, whose dates the Policy gate already reads, so the catalogue and the gate cannot disagree about when a lever opens.
> 
> What it has to prove:
> 
>   1. THE FILE READS. The packaged file gives 97 entries and a fresh reading of its text reports nothing (no entry skipped); the codes are
>      unique and in the studies' order, the near future's FE1, FE2 and FE4 and the speculative tier's FE8 and FE10 after them; the counts by prefix and by field are the studies'; the
>      field follows the code (the second letter of an F code); every entry has a name, a world, a touches and an effect; the kinds, the kind
>      notes and the names of a few are the tables'.
>   2. NEEDS. Every need is a code in the catalogue, no entry needs itself, history dates each need no later than its entry; the tables' 26
>      needs of 23 entries and the near future's 3 of 2 are there; M1 needs PM6 (research-1900 8.6), E18 needs E13 and E9, PE17 needs PE14.
>   3. HISTORY'S DATES. Every entry's world month is PolicyDates.monthOf() of its year and month; every original non-F row is 1900 to 1995, E21 is 1996, every F row
>      NEAR_FUTURE's year to 2060, and every row from NEAR_FUTURE on is coded F; values typed from the tables (a dated month, and
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
>      code-dated templates are exactly research-1900.md 1.2's list typed out by id (31) and the near future's two (33), the Oxygen Steel Shop is dated
>      1900, the Panel Mill E19 1981 and the Wood Pellet Mill E20 1970, every other row undated or 1900; every building entry opens a template but PE12 and B8; a template's year
>      is its entry's. A building file read from text keeps a code as written, reports one the catalogue lacks and never knows it.
>   8. KNOWN FROM HISTORY'S MONTH. The tractor's piece and recipe are known from PE14's month and not the month before, the oxygen converter's
>      recipe from E1's (November 1952), the Container Terminal from E5's, the Electric Arc Furnace from PE1's December 1900, the Pulp Mill's kraft
>      recipe from January 1907; an undated row, and one dated 1900, from the first month; and asking by the year (knownIn) agrees with asking
>      by the month of that year's January, 1900 to 1960, and nothing dated 1900 is known the year before the start.
>   9. THE VERDICT AND THE CHOICE ON HISTORY'S MONTH. EquipmentFit.judge() for the tractor with figures that pay is NOT_KNOWN the month before
>      PE14's and PAYS at it (and figures a thousandth short of paying are NOT_YET at it, so the rule runs), and Recipes.canRun() for the
>      oxygen converter's recipe is false the month before E1's and true at it, for the part that may run it (the one without the open hearth).
>  10. BUILDINGS ON THEIR DATES (R2b). All sector and category candidates exclude unknown templates in a 1900 city, opening on their month
>      or a local discovery. A paying spread-planner choice, a city's power shortage and funded direct city/private orders cause the refusals;
>      every candidate and direct order remains available in a default founding but the near future's.
>  11. THE MARK (R6b). A row is projected exactly when its world month is after HISTORY_ENDS, a projected row has a window of years that holds its
>      year and a history row none; FE4 is projected (2027-2030), FE8 (2030-2036) and FE10 (2031-2037) too, FE1 and FE2 are not; mark() and dateWords() say so, and nothing else.
>  12. THE NEAR FUTURE (R6b, call 1). nearFuture(code) is true exactly for the rows dated from NEAR_FUTURE on, and those are exactly the F codes.
>  13. ITS BUILDINGS. A city founded the default way never knows a near-future building (a candidate nor an order) and knows the oxide leach plant; one
>      founded the 1900 way knows the Sulphide Leach & SX-EW Plant from FE2's month and the Hydrogen Steel Works from FE4's, and not the month before.

**Uses:** [ResearchCatalog](ResearchCatalog.md) (84), [BuildingsTemplate](BuildingsTemplate.md) (28), [PolicyDates](PolicyDates.md) (19), [Game](Game.md) (17), [CityCalendar](CityCalendar.md) (13), [EquipmentFit](EquipmentFit.md) (12), [Recipes](Recipes.md) (11), [BuildAdvice](BuildAdvice.md) (8), [Refining](Refining.md) (7), [Equipment](Equipment.md) (5), [Sectors](Sectors.md) (5), [ResearchView](ResearchView.md) (5), [BuildingManager](BuildingManager.md) (4), [SpreadPlanner](SpreadPlanner.md) (4), [Ports](Ports.md) (4), [BuildingCatalog](BuildingCatalog.md) (3), [Founding](Founding.md) (3), [Good](Good.md) (3), [Recipe](Recipe.md) (2), [LongPlaytest](LongPlaytest.md) (2), [BuildingType](BuildingType.md) (1), [BusinessInvestment](BusinessInvestment.md) (1), [GameFiles](GameFiles.md) (1), [RefineryFlow](RefineryFlow.md) (1), [Investor](Investor.md) (1), [MaterialsCheck](MaterialsCheck.md) (1), [UtilitiesHandler](UtilitiesHandler.md) (1)

**Used by (9):** [DriverlessTruckCheck](DriverlessTruckCheck.md), [ElectrolyserCheck](ElectrolyserCheck.md), [FB13Check](FB13Check.md), [FieldOperatorsCheck](FieldOperatorsCheck.md), [HistoricalLicenceCheck](HistoricalLicenceCheck.md), [HydrogenSteelCheck](HydrogenSteelCheck.md), [LicenceFeesCheck](LicenceFeesCheck.md), [PublicLicenceCheck](PublicLicenceCheck.md), [SulphideLeachCheck](SulphideLeachCheck.md)

## Sections

| line | section |
|---:|---|
| 91 | · the specification, typed from the studies' tables |
| 171 | · fixtures for the reader |
| 230 | · 1. the file reads |
| 294 | · 2. needs |
| 327 | · 3. history's dates |
| 376 | · 4. the work |
| 419 | · 5. known from |
| 463 | · 6. the reader's refusals, and the fees |
| 565 | 7. buildings.json's codes (R2, slice B) |
| 678 | 8. known from history's month, 9. the verdict and the choice on it (R2, slice B) |
| 766 | 10. the building candidates and orders (R2b) |
| 918 | 11. the mark against HISTORY_ENDS (R6b) |
| 960 | 12. the near future (R6b, call 1) |
| 988 | 13. a near-future building, by the way the city was founded (R6b, call 1) |
| 1028 | · helpers |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 97 | `ResearchCheck.DATES` | `List.of(new Spot("E1", 1952, 11, true), new Spot("E5", 1956, 4, true), new Sp...` | research-design.md 4 and research-1900.md 8: the World column's month where it names one, the prototype's where it does not. |
| 143 | `ResearchCheck.MONTH_KNOWN` | `32` | The entries whose World column names a month: the tables' 30 and the near future's FE1 and FE2 (FE4 is a projection, a year's window). |
| 146 | `ResearchCheck.PREFIXES` | `{ "E", "M", "B", "L", "PE", "PM", "PB", "PL" }` | The prefixes in the studies' order, and how many entries each has; the counts add to the studies' 92 rows (HISTORY_ROWS). |
| 147 | `ResearchCheck.PREFIX_COUNTS` | `{ 21, 10, 8, 5, 27, 6, 3, 12 }` |  |
| 148 | `ResearchCheck.HISTORY_ROWS` | `92` |  |
| 151 | `ResearchCheck.NEAR_PREFIXES` | `{ "FE", "FM", "FB", "FL" }` | The near future's prefixes (the first letter F, the second the field, research-near-future's call 7), and how many rows each has: FE1, FE2, FE4, FE8 and FE10 (the near future's study has no FE3; FE8 and FE10 are the s... |
| 152 | `ResearchCheck.NEAR_COUNTS` | `{ 5, 0, 0, 0 }` |  |
| 155 | `ResearchCheck.NEAR_CODES` | `List.of("FE1", "FE2", "FE4", "FE8", "FE10")` | The near future's rows, in the file's order, after the studies' 92. |
| 158 | `ResearchCheck.WORK` | `{ { "E1", 444 }, { "E5", 545 }, { "E12", 1013 }, { "E18", 2998 }, { "E21", 21...` | Work typed from the tables (researcher-years at the date); PL1 and PL12 are the 8.4 text's, L5 the 8.6 rule's. |
| 568 | `ResearchCheck.CODED` | `new TreeMap<>(Map.ofEntries(Map.entry(11, "PE1"), Map.entry(119, "PE4"), Map....` | research-1900.md 1.2 and the R2 brief: the templates dated by a code, id to the entry that opens each; typed here as the specification. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 77 | `private static int fails` |  |
| 78 | `private static int passes` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 75 | 995 | **type** `public class ResearchCheck` | The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses what is not a discovery (R2, slice A: ResearchCatalog and research.json; d... |
| 80 | 4 | `static void check(String label, boolean ok)` |  |
| 86 | 4 | `static void check(String label, boolean ok, String found)` | The same, with what was found printed under a failure. |

### the specification, typed from the studies' tables (lines 91-170)

| line | len | member | says |
|---:|---:|---|---|
| 94 | 1 | **type** `record Spot(String code, int year, int month, boolean monthKnown)` | One date typed from a table: the year, the month and whether the table's World column names the month (else the study's model's month). |
| 163 | 3 | `static int monthOf(int year, int monthOfYear)` | The game month of a real calendar month, worked out here and not by PolicyDates. |
| 167 | 3 | `static String prefixOf(String code)` |  |

### fixtures for the reader (lines 171-564)

| line | len | member | says |
|---:|---:|---|---|
| 173 | 5 | `private static String quoted(String...items)` |  |
| 180 | 4 | `static String entryJson(String code, String field, String size, String kind, int year, int month, int work, String...needs)` | One entry's JSON, with the fields the reader refuses on; the rest it reads as absent. |
| 185 | 3 | `static String file(String...entries)` |  |
| 190 | 3 | `static String good(String code, String...needs)` | A good entry: an engineering building in 1960. |
| 195 | 1 | **type** `record Read(ResearchCatalog catalogue, String log)` | What a reading wrote and what it made. |
| 198 | 12 | `static Read read(String json)` | Reads a text through the class's parse() with System.out captured, so that what it reports can be asserted. |
| 211 | 5 | `static String text(String resource) throws Exception` |  |
| 217 | 3 | `static Entry of(String code)` |  |
| 221 | 3 | `static boolean has(Entry e, List<String> kinds)` |  |
| 225 | 339 | `public static void main(String[] args) throws Exception` |  |

### 7. buildings.json's codes (R2, slice B) (lines 565-677)

| line | len | member | says |
|---:|---:|---|---|
| 579 | 4 | `static BuildingsTemplate byId(List<BuildingsTemplate> templates, int id)` |  |
| 584 | 93 | `static void sectionSeven()` |  |

### 8. known from history's month, 9. the verdict and the choice on it (R2, slice B) (lines 678-765)

| line | len | member | says |
|---:|---:|---|---|
| 680 | 85 | `static void sectionEightAndNine()` |  |

### 10. the building candidates and orders (R2b) (lines 766-917)

| line | len | member | says |
|---:|---:|---|---|
| 769 | 11 | `static Game datedCity(String label, boolean historical)` | A scratch city with ample land and cash; the date alone must refuse its orders. |
| 782 | 5 | `static void atMonth(Game g, int month) throws Exception` | Set the clock at an exact boundary without playing decades of unrelated economics. |
| 788 | 129 | `static void buildingsOnDates() throws Exception` |  |

### 11. the mark against HISTORY_ENDS (R6b) (lines 918-959)

| line | len | member | says |
|---:|---:|---|---|
| 920 | 39 | `static void theMark()` |  |

### 12. the near future (R6b, call 1) (lines 960-987)

| line | len | member | says |
|---:|---:|---|---|
| 962 | 25 | `static void theNearFuture()` |  |

### 13. a near-future building, by the way the city was founded (R6b, call 1) (lines 988-1027)

| line | len | member | says |
|---:|---:|---|---|
| 990 | 37 | `static void nearFutureBuildings() throws Exception` |  |

### helpers (lines 1028-1069)

| line | len | member | says |
|---:|---:|---|---|
| 1030 | 5 | `static List<String> codes(ResearchCatalog c)` |  |
| 1037 | 13 | `static List<String> orderOf()` | The studies' order: E, M, B, L1-L4, PE, PM, PB, PL, then L5, E19-E21, then the near future's rows. |
| 1052 | 9 | `static boolean allLaw(ResearchCatalog c, String prefix, String note)` | Whether every entry whose code starts with a prefix and a digit is a recipe with that kind note. |
| 1062 | 7 | `static boolean drugs(ResearchCatalog c)` |  |

