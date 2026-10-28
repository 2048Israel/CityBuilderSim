# EarthCheck.java - 1,065 lines · 33 methods · 17 constants · harnesses

`ham/citybuildersim/EarthCheck.java` - generated 2026-10-28 by CodeMap; line numbers are as of that run.

> The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and
> 8): EarthProj's projection, blend and areas held to the spec's measures,
> and EarthData's reading of earth-2.bin (batch ET1; earth-1.bin before) held
> to tools/earth/earthread.py figure for figure, the layers earth-2 added
> with the rest, and earth-1's ten layers held to earth-1.bin byte for byte.
> 
> WHY. E2 lays the real Earth's ground on the world through these two, and a
> wrong figure there is a coast in the wrong place under a city that cannot
> be moved off it. The projection is checked against the sphere itself, not
> against its own formulas; the reader against the Python that wrote the file
> and reads it independently.
> 
> THE DATA. The full earth-2.bin (281.8 MB) is not in the repository; it is
> the asset of the GitHub release earth-2, which tools/earth/get-earth.bat
> fetches into data/ (EarthData.DATA_FILE). This runs on a fixture cut from
> it by tools/earth/fixture.py - the same layout holding only the tiles read
> here, the three windows the study drew (Rotterdam, Perth, Paris), Fiji's
> four on the 180th meridian and Halifax's Narrows, every layer's blobs
> (earth-1's and earth-2's) - a resource beside the classes
> (earth/earth-2-fixture.bin), with what earthread.py read from it and from
> the full file beside it (earth/earth-2-fixture.expect). ET1 replaced the
> earth-1 fixture by it, since the layers of earth-1 are in it byte for byte.
> The full file's sections run when it is named, -Dcbs.earth=<path>
> (EarthData.locate(); in AllChecks through JDK_JAVA_OPTIONS, which every
> harness's JVM inherits), and say so in one line when it is not; the
> comparison with earth-1.bin runs when that is found too, -Dcbs.earth1=<path>
> or earth-1.bin beside the named file.
> 
> What this has to prove:
>   1. INVERSE THEN FORWARD COMES BACK: from spots at the three windows,
>      Fiji and both poles, every plane point within ROUND_TRIP_REACH_M goes
>      to the sphere and back within ROUND_TRIP_M (spec: 2.4e-7 m measured);
>      the spot is the origin, and a point due north or south lands at
>      2R sin(c/2) on its axis, the projection's own radial law.
>   2. EQUAL AREA: a world cell's area measured on the sphere - its edge
>      carried there point by point - is World.CELL_KM2 within
>      AREA_TOLERANCE, at the spot and 10,000 km out; the closed form the
>      game uses agrees, cell by cell, with the same area integrated along
>      its columns where the antipode's circle crosses it; and Rotterdam's
>      square has the spec's OFF_PLANET_CELLS cells whose sixteen samples are
>      all off the planet.
>   3. THE BLEND: a tile's points blended from its four corners lie within
>      BLEND_M of the exact ones at the spot and out to BLEND_REACH_M (spec:
>      0.054 m measured), across the date line too; two tiles agree along the
>      edge they share; above EarthProj.BLEND_MAX_LAT a tile projects exactly.
>   4. THE FILE: the fixture opens; its layer table (seventeen layers),
>      indexes, meta and identity ("earth-2:" and the SHA-256) are what
>      earthread.py read, and it names EarthData.EARTH_2 as the file it was
>      cut from.
>   5. THE READER IS earthread.py'S: every fixture tile's bytes, cells of every
>      value and the spots' cells found by position, every river run and
>      small lake decoded, the lake areas, the geology at the spots and the
>      provinces it names, and the places.
>   6. A RIVER STORED TWICE IS READ ONCE: across a window's tiles each segment
>      comes back once, as many as earthread.py counts, and a segment that
>      crosses a tile's edge is in both tiles' runs but once in the read.
>   7. THE LRU: a tile read again is not inflated again, the least recently
>      read goes first, the bytes kept stay within the budget, and the
>      default budget holds the spec's 64 tiles of sea and lake.
>   8. A TILE INFLATES IN UNDER INFLATE_MS (spec: 0.13 ms measured; wall
> ... (24 more lines in the source)

**Uses:** [EarthData](EarthData.md) (120), [World](World.md) (63), [EarthProj](EarthProj.md) (44), [Timing](Timing.md) (2)

**Used by (11):** [BridgeCheck](BridgeCheck.md), [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GlobeCheck](GlobeCheck.md), [LandCheck](LandCheck.md), [LandmassCheck](LandmassCheck.md), [MapCheck](MapCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 256 | 1. INVERSE THEN FORWARD |
| 322 | 2. EQUAL AREA |
| 399 | 3. THE BLEND |
| 493 | 4 TO 6. THE READER AGAINST earthread.py |
| 701 | 7. THE LRU |
| 737 | 8. A TILE INFLATES IN UNDER A MILLISECOND |
| 766 | 10 TO 12. EARTH-2'S LAYERS (ET1) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 110 | `EarthCheck.ROUND_TRIP_M` | `1e-6` | Inverse then forward comes back within this, metres (spec-earth 8, E1; 2.4e-7 measured). |
| 112 | `EarthCheck.ROUND_TRIP_REACH_M` | `1_050_000` | ...for every plane point within this of the spot, metres (spec-earth 8: 1,050 km; a 10B city's half-diagonal is 938). |
| 114 | `EarthCheck.AREA_TOLERANCE` | `1e-9` | A cell's area measured on the sphere is World.CELL_KM2 within this share of it (spec-earth 8: 3,774.87 km2 to 1e-9). |
| 116 | `EarthCheck.AREA_SAMPLES` | `4096` | Points a side the sphere's measure of a cell takes: 15 m apart, so the great circles between them stay within a millionth of a square metre of the edge's curve. |
| 118 | `EarthCheck.BLEND_M` | `0.1` | A blended point lies within this of the exact one, metres (spec-earth 8: 0.1 m; 0.054 measured 10,000 km out). |
| 120 | `EarthCheck.BLEND_REACH_M` | `10_000_000` | How far out the blend is measured, metres of the plane (spec-earth 8: 10,000 km). |
| 122 | `EarthCheck.INFLATE_MS` | `1` | A 15" tile inflates within this, milliseconds (spec-earth 8; 0.13 measured). |
| 124 | `EarthCheck.OFF_PLANET_CELLS` | `12_108` | Rotterdam's square: the cells whose 4 x 4 samples are all off the planet (spec-earth 2: 12,108 of 135,424 measured; the square is the same round any spot). |
| 126 | `EarthCheck.FIXTURE` | `"earth/earth-2-fixture.bin", EXPECT = "earth/earth-2-fixture.expect"` | The fixture and its expected figures, beside the classes. |
| 128 | `EarthCheck.EARTH1_PROPERTY` | `"cbs.earth1"` | The system property that names earth-1.bin for section 11, when it is not beside the named file. |
| 130 | `EarthCheck.ROOMY` | `Long.MAX_VALUE / 4` | An LRU budget no harness fills: a reader with room for every tile it is asked for, to weigh them. |
| 132 | `EarthCheck.CITY_TILES` | `6` | A city's 50 km reads this many 1-degree tiles at most (the study's 2.9: 4 to 6). |
| 134 | `EarthCheck.MIN_FULL_TILES` | `4 * CITY_TILES` | The default LRU has to hold at least this many tiles with every layer inflated (section 12's loose bound): four cities' tiles. |
| 136 | `EarthCheck.EARTH1_TILE_LAYERS` | `{ "sea", "lake", "landcover", "lakes.small", "rivers" }, EARTH2_TILE_LAYERS =...` | The 1-degree tile layers of earth-1 and of earth-2 section 12 inflates: the five a tile of earth-1 has, the four earth-2 adds. |
| 139 | `EarthCheck.EARTH1_LAYERS` | `{ "sea", "lake", "lakes.small", "rivers", "landcover", "far.land", "far.lake"...` | The ten layers earth-2 holds of earth-1's byte for byte (meta is earth-2's own). |
| 143 | `EarthCheck.out` | `System.out` | Where the harness prints: the console. |
| 771 | `EarthCheck.RELIEF_KINDS` | `Set.of("slopeb", "shallowb", "depthb", "depthcode", "slopeest", "landmasstile...` | The expected figures' kinds relief() reads (agree() leaves them to it). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 141 | `static int fails` |  |
| 157 | `int n, bad` |  |
| 158 | `String first` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 107 | 959 | **type** `public class EarthCheck` | The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and 8): EarthProj's projection, blend and areas held to the spec's measures, and EarthData's reading of earth-2.bin (batch ET1; earth-1.bin before) ... |
| 145 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 150 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 156 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 160 | 7 | `void see(boolean ok, String what)` _(in EarthCheck.Tally)_ |  |
| 169 | 6 | `static void tally(String label, Tally t)` |  |
| 176 | 70 | `public static void main(String[] args) throws Exception` |  |
| 248 | 7 | `static byte[] resource(String name) throws IOException` | A resource beside the classes, or failing that the source tree's copy from the working directory; null if neither. |

### 1. INVERSE THEN FORWARD (lines 256-321)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 13 | `static List<double[]> spots(Map<String, String[]> windows)` | The spots: the windows' (Rotterdam, Perth, Paris, Fiji) and both poles. |
| 275 | 46 | `static void projection(Map<String, String[]> windows)` |  |

### 2. EQUAL AREA (lines 322-398)

| line | len | member | says |
|---:|---:|---|---|
| 326 | 59 | `static void areas()` |  |
| 387 | 11 | `static double columns(double x0, double x1, double y0, double y1)` | The area of the rectangle within the reach, integrated along x by Simpson's rule on the exact chord: the same figure as areaOnPlanetM2 by another road. |

### 3. THE BLEND (lines 399-492)

| line | len | member | says |
|---:|---:|---|---|
| 403 | 74 | `static void blend(Map<String, String[]> windows)` |  |
| 479 | 13 | `static double blendError(EarthProj p, EarthProj.Blend b, double tx, double ty)` | The farthest a tile's blended point lies from the exact one, metres, over its plots' centres and the ring of plots round it. |

### 4 TO 6. THE READER AGAINST earthread.py (lines 493-700)

| line | len | member | says |
|---:|---:|---|---|
| 497 | 3 | `static String sha(byte[] b) throws Exception` |  |
| 502 | 9 | `static String riverInts(List<EarthData.Run> runs)` | A tile's runs as the integers they were stored as, written as tools/earth/fixture.py writes them. |
| 512 | 5 | `static String lakeInts(EarthData.SmallLakes l)` |  |
| 518 | 5 | `static int segmentsOf(List<EarthData.Run> runs)` |  |
| 524 | 5 | `static int[] tilesOf(String[] f, int from)` |  |
| 531 | 131 | `static void agree(EarthData d, String scope, List<String[]> expect, Map<String, String[]> windows) throws Exception` | Every expected figure of scope "both" and of this scope, read through EarthData and compared. |
| 663 | 7 | `static Tally merge(Tally a, Tally b)` |  |
| 672 | 28 | `static void duplicates(EarthData d, Map<String, String[]> windows)` | A segment crossing a tile's edge: in both tiles' runs, once in the read across them. |

### 7. THE LRU (lines 701-736)

| line | len | member | says |
|---:|---:|---|---|
| 705 | 31 | `static void lru(byte[] fixtureBytes) throws IOException` |  |

### 8. A TILE INFLATES IN UNDER A MILLISECOND (lines 737-765)

| line | len | member | says |
|---:|---:|---|---|
| 741 | 24 | `static void inflateTime(EarthData d, byte[] fixtureBytes) throws Exception` |  |

### 10 TO 12. EARTH-2'S LAYERS (ET1) (lines 766-1065)

| line | len | member | says |
|---:|---:|---|---|
| 775 | 3 | `static double[] centre(int tile, int cpd, int r, int c)` | The point at the centre of cell (r, c) of a 1-degree tile read at cpd cells a degree. |
| 780 | 3 | `static int[] tilesWith(List<String[]> expect, String kind, String scope)` | The tile numbers of an expected figure's lines of this kind that are true of this scope (both, or the scope's own). |
| 785 | 8 | `static boolean refuses(Runnable call)` | Whether a call is refused with an IllegalArgumentException. |
| 795 | 79 | `static void relief(EarthData d, String scope, List<String[]> expect) throws Exception` | Every expected figure of earth-2's layers of this scope (both, or the scope's own), read through EarthData's readers and compared. |
| 876 | 56 | `static void decoders()` | The unpacking of the slope byte and the depth code by hand, and the landmass palette's short-index form on a tile built for it. |
| 934 | 7 | `static void varint(java.io.ByteArrayOutputStream o, long v)` | A value as a LEB128 varint, as the file's lists write them. |
| 943 | 18 | `static void landmassLayer(EarthData d)` | The landmass layer is no byte grid, and the landmasses come largest first. |
| 963 | 3 | **type** `interface Opener` | A reader to be opened with room in its LRU for every tile weigh() reads. |
| 964 | 1 | `EarthData open() throws IOException` _(in EarthCheck.Opener)_ |  |
| 968 | 40 | `static void weigh(String what, Opener opener, int[] tiles) throws IOException` | What a tile with every layer inflated holds in the LRU, from a reader with room for all of them: printed, with a loose bound that a city's tiles fit (the study's 2.9). |
| 1010 | 44 | `static void sameAsEarth1(EarthData d2, Path full) throws IOException` | Section 11: the ten layers earth-2 holds of earth-1's, compared with earth-1.bin index entry by index entry and stored blob by stored blob. |
| 1056 | 9 | `static boolean sameStored(FileChannel c1, long posA, FileChannel c2, long posB, int len, ByteBuffer a, ByteBuffer b) throws IOE...` | Whether len bytes at posA of one file are the same as those at posB of the other. |

