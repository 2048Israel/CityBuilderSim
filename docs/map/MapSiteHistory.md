# MapSiteHistory.java - 276 lines · 20 methods · 0 constants · model

`ham/citybuildersim/MapSiteHistory.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The resource sites' plot classes as they change each month, kept sparsely by district for the plans' historical ground.
> 
> WHY (DM2): a plan replayed its old buildings around today's mined sites, so a mine first worked at month 100 barred its
> ground before month 100 too. Counts alone cannot date that ground. The map captures the site claims before their first
> mutation, compares the finished month's masks, and keeps only actual plot transitions; a canonical city's unchanged sites
> need no records. Nothing here plans a district or depends on when the player first looks at it.

**Uses:** [CityMap](CityMap.md) (35), [DistrictPlan](DistrictPlan.md) (7), [World](World.md) (2)

**Used by (1):** [CityMap](CityMap.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 30 | `private final Map<CityMap.District, Journal> journals` |  |
| 31 | `private final Map<CityMap.District, int[][]> before` |  |
| 32 | `private boolean recording` |  |
| 231 | `int[] month, rec` |  |
| 232 | `int n` |  |
| 233 | `long hash` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 22 | 255 | **type** `final class MapSiteHistory` | The resource sites' plot classes as they change each month, kept sparsely by district for the plans' historical ground. |
| 26 | 3 | **type** `interface MaskReader` | Reads a district's own 256 by 256 mask; an override replaces its site runs, and a null override means absent before. |
| 27 | 1 | `byte[] mask(CityMap.District district, Map<CityMap.District, int[][]> overrides)` _(in MapSiteHistory.MaskReader)_ |  |
| 35 | 5 | `void begin()` | Starts the month's source capture; canonical construction outside this interval has no history to record. |
| 42 | 1 | `boolean isRecording()` | Whether site claims are being captured for a reconciled month. |
| 45 | 6 | `void changed(CityMap.District d)` | A district's claims before its first mutation this month, including mutations that are later undone. |
| 53 | 3 | `void added(CityMap.District d)` | A new district had no mask before this month; subsequent claim writes keep that fact. |
| 58 | 27 | `void end(BiFunction<Integer, Integer, CityMap.District> lookup, MaskReader reader, int month)` | Compares finished masks only near changed claims, recording the month's net transitions in plot order. |
| 87 | 6 | `private static void checkMask(byte[] mask)` | Rejects a renderer's malformed mask before it becomes saved history. |
| 95 | 4 | `long hash(CityMap.District d)` | The history's stamp, zero for a district without any transitions. |
| 101 | 33 | `void frameInto(CityMap.District d, DistrictPlan.Input in, BiFunction<Integer, Integer, CityMap.District> lookup)` | The own district and its east/south seams, transformed to the plan's 257 by 257 frame, in month and plot order. |
| 136 | 9 | `private static int framePlot(int plot, int part)` | Own plots, east's first column, south's first row and the south-east's corner alone belong to this frame. |
| 147 | 5 | `private static CityMap.District neighbour(CityMap.District d, int x, int y, BiFunction<Integer, Integer, CityMap.District> lookup)` | A neighbour without wrapping the district coordinates at the integer limits. |
| 154 | 11 | `void writeDistrict(DataOutputStream out, CityMap.District d) throws IOException` | The sidecar's count, month deltas and packed plot/class transitions for one district. |
| 167 | 28 | `void readDistrict(DataInputStream in, CityMap.District d, int savedMonth, int remainingBytes) throws IOException` | Reads bounded, ordered transitions; a plot's next before-class must agree with its preceding after-class. |
| 197 | 7 | `boolean same(CityMap.District a, MapSiteHistory other, CityMap.District b)` | Whether two districts have exactly the same source history, independently of array capacity. |
| 206 | 17 | `boolean matchesCurrent(MaskReader reader)` | A loaded journal must end on the actual claims; only districts with transitions need a mask read. |
| 225 | 3 | `private static boolean kind(int kind)` | The only source classes a transition may name. |
| 230 | 46 | **type** `private static final class Journal` | One district's ordered plot transitions, with capacity kept between months. |
| 235 | 1 | `Journal()` _(in MapSiteHistory.Journal)_ |  |
| 237 | 4 | `Journal(int capacity)` _(in MapSiteHistory.Journal)_ |  |
| 242 | 10 | `void append(int at, int record)` _(in MapSiteHistory.Journal)_ |  |
| 254 | 21 | `void record(int at, byte[] was, byte[] now)` _(in MapSiteHistory.Journal)_ | A repeated reconciliation in the same month merges into that month's original before-classes. |

