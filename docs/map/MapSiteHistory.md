# MapSiteHistory.java - 406 lines · 25 methods · 0 constants · model

`ham/citybuildersim/MapSiteHistory.java` - generated 2026-11-14 by CodeMap; line numbers are as of that run.

> The resource sites' plot classes as they change each month, kept sparsely by district for the plans' historical ground.
> 
> WHY (DM2): a plan replayed its old buildings around today's mined sites, so a mine first worked at month 100 barred its
> ground before month 100 too. Counts alone cannot date that ground. The map captures the site claims before their first
> mutation, compares the finished month's masks, and keeps only actual plot transitions; a canonical city's unchanged sites
> need no records. Nothing here plans a district or depends on when the player first looks at it.

**Uses:** [CityMap](CityMap.md) (64), [DistrictPlan](DistrictPlan.md) (13), [World](World.md) (3)

**Used by (1):** [CityMap](CityMap.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 33 | `private final Map<CityMap.District, Journal> journals` |  |
| 35 | `private final Map<CityMap.District, MinedBirth> pendingBirths` | Implicit masks expand only after the reader has restored all of the map's claim state. |
| 36 | `private final Map<CityMap.District, int[][]> before` |  |
| 37 | `private boolean recording` |  |
| 224 | `final int month, plot, width, kinds` |  |
| 225 | `int height` |  |
| 347 | `int[] month, rec` |  |
| 348 | `int n` |  |
| 349 | `long hash` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 25 | 382 | **type** `final class MapSiteHistory` | The resource sites' plot classes as they change each month, kept sparsely by district for the plans' historical ground. |
| 29 | 3 | **type** `interface MaskReader` | Reads a district's own 256 by 256 mask; an override replaces its site runs, and a null override means absent before. |
| 30 | 1 | `byte[] mask(CityMap.District district, Map<CityMap.District, int[][]> overrides)` _(in MapSiteHistory.MaskReader)_ |  |
| 39 | 1 | **type** `private record MinedBirth(int month, int count, long hash)` |  |
| 42 | 5 | `void begin()` | Starts the month's source capture; canonical construction outside this interval has no history to record. |
| 49 | 1 | `boolean isRecording()` | Whether site claims are being captured for a reconciled month. |
| 52 | 6 | `void changed(CityMap.District d)` | A district's claims before its first mutation this month, including mutations that are later undone. |
| 60 | 3 | `void added(CityMap.District d)` | A new district had no mask before this month; subsequent claim writes keep that fact. |
| 65 | 27 | `void end(BiFunction<Integer, Integer, CityMap.District> lookup, MaskReader reader, int month)` | Compares finished masks only near changed claims, recording the month's net transitions in plot order. |
| 94 | 6 | `private static void checkMask(byte[] mask)` | Rejects a renderer's malformed mask before it becomes saved history. |
| 102 | 4 | `long hash(CityMap.District d)` | The history's stamp, zero for a district without any transitions. |
| 108 | 33 | `void frameInto(CityMap.District d, DistrictPlan.Input in, BiFunction<Integer, Integer, CityMap.District> lookup)` | The own district and its east/south seams, transformed to the plan's 257 by 257 frame, in month and plot order. |
| 143 | 9 | `private static int framePlot(int plot, int part)` | Own plots, east's first column, south's first row and the south-east's corner alone belong to this frame. |
| 154 | 5 | `private static CityMap.District neighbour(CityMap.District d, int x, int y, BiFunction<Integer, Integer, CityMap.District> lookup)` | A neighbour without wrapping the district coordinates at the integer limits. |
| 161 | 24 | `void writeDistrict(DataOutputStream out, CityMap.District d, MaskReader reader) throws IOException` | No history, explicit rectangles, or one exact FIELD-to-MINED birth reconstructed from the saved current claims. |
| 187 | 12 | `private static boolean currentMinedBirth(Journal j, CityMap.District d, MaskReader reader)` | A compact predictor is used only after an exact plot-for-plot comparison, never from a count or hash alone. |
| 201 | 21 | `private static List<SiteRectangle> rectangles(Journal j)` | Equal row spans on consecutive rows become one rectangle, without changing any expanded plot transition. |
| 223 | 8 | **type** `private static final class SiteRectangle` |  |
| 227 | 3 | `SiteRectangle(int month, int plot, int width, int kinds)` _(in MapSiteHistory.SiteRectangle)_ |  |
| 233 | 6 | `private static int spanEnd(Journal j, int from)` | Consecutive plots of the same transition and month, stopped at the row's edge. |
| 241 | 56 | `void readDistrict(DataInputStream in, CityMap.District d, int savedMonth, int remainingBytes) throws IOException` | Reads bounded, ordered transitions; a plot's next before-class must agree with its preceding after-class. |
| 299 | 7 | `boolean same(CityMap.District a, MapSiteHistory other, CityMap.District b)` | Whether two districts have exactly the same source history, independently of array capacity. |
| 308 | 31 | `boolean matchesCurrent(MaskReader reader)` | A loaded journal must end on the actual claims; only districts with transitions need a mask read. |
| 341 | 3 | `private static boolean kind(int kind)` | The only source classes a transition may name. |
| 346 | 60 | **type** `private static final class Journal` | One district's ordered plot transitions, with capacity kept between months. |
| 351 | 1 | `Journal()` _(in MapSiteHistory.Journal)_ |  |
| 353 | 4 | `Journal(int capacity)` _(in MapSiteHistory.Journal)_ |  |
| 358 | 10 | `void append(int at, int record)` _(in MapSiteHistory.Journal)_ |  |
| 370 | 12 | `void finishMonth(int from, byte[] last, long prefixHash) throws IOException` _(in MapSiteHistory.Journal)_ | Rectangle expansions interleave across rows; restore plot order and validate each transition exactly once. |
| 384 | 21 | `void record(int at, byte[] was, byte[] now)` _(in MapSiteHistory.Journal)_ | A repeated reconciliation in the same month merges into that month's original before-classes. |

