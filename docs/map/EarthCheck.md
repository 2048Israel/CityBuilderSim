# EarthCheck.java - 702 lines · 22 methods · 10 constants · harnesses

`ham/citybuildersim/EarthCheck.java` - generated 2026-10-22 by CodeMap; line numbers are as of that run.

> The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and
> 8): EarthProj's projection, blend and areas held to the spec's measures,
> and EarthData's reading of earth-1.bin held to tools/earth/earthread.py
> figure for figure.
> 
> WHY. E2 lays the real Earth's ground on the world through these two, and a
> wrong figure there is a coast in the wrong place under a city that cannot
> be moved off it. The projection is checked against the sphere itself, not
> against its own formulas; the reader against the Python that wrote the file
> and reads it independently.
> 
> THE DATA. The full earth-1.bin (52.8 MB) is not in the repository; since
> f99d62c it is the asset of the GitHub release earth-1, which
> tools/earth/get-earth.bat fetches into data/ (EarthData.DATA_FILE). This
> runs on a fixture cut from it by
> tools/earth/fixture.py - the same layout holding only the tiles read here,
> the three windows the study drew (Rotterdam, Perth, Paris) and Fiji's four
> on the 180th meridian - a resource beside the classes
> (earth/earth-1-fixture.bin), with what earthread.py read from it and from
> the full file beside it (earth/earth-1-fixture.expect). The full file's
> sections run when it is named, -Dcbs.earth=<path> (EarthData.locate(); in
> AllChecks through JDK_JAVA_OPTIONS, which every harness's JVM inherits),
> and say so in one line when it is not.
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
>   4. THE FILE: the fixture opens; its layer table, indexes, meta and
>      identity are what earthread.py read, and it names EarthData.EARTH_1 as
>      the file it was cut from.
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
>      clock, so run under the JVM lock).
>   9. THE FULL FILE, when named: its identity is EarthData.EARTH_1, and 4 to 6
>      hold on it, with a ring of tiles round each window, nine far places and
>      every far-view band besides.
> 
> ... (2 more lines in the source)

**Uses:** [World](World.md) (63), [EarthData](EarthData.md) (48), [EarthProj](EarthProj.md) (44), [Timing](Timing.md) (2)

**Used by (8):** [EarthDepositsCheck](EarthDepositsCheck.md), [EarthFoundingCheck](EarthFoundingCheck.md), [EarthTerrainCheck](EarthTerrainCheck.md), [GlobeCheck](GlobeCheck.md), [MapCheck](MapCheck.md), [NewGameCheck](NewGameCheck.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md)

## Sections

| line | section |
|---:|---|
| 194 | 1. INVERSE THEN FORWARD |
| 260 | 2. EQUAL AREA |
| 337 | 3. THE BLEND |
| 431 | 4 TO 6. THE READER AGAINST earthread.py |
| 638 | 7. THE LRU |
| 674 | 8. A TILE INFLATES IN UNDER A MILLISECOND |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 85 | `EarthCheck.ROUND_TRIP_M` | `1e-6` | Inverse then forward comes back within this, metres (spec-earth 8, E1; 2.4e-7 measured). |
| 87 | `EarthCheck.ROUND_TRIP_REACH_M` | `1_050_000` | ...for every plane point within this of the spot, metres (spec-earth 8: 1,050 km; a 10B city's half-diagonal is 938). |
| 89 | `EarthCheck.AREA_TOLERANCE` | `1e-9` | A cell's area measured on the sphere is World.CELL_KM2 within this share of it (spec-earth 8: 3,774.87 km2 to 1e-9). |
| 91 | `EarthCheck.AREA_SAMPLES` | `4096` | Points a side the sphere's measure of a cell takes: 15 m apart, so the great circles between them stay within a millionth of a square metre of the edge's curve. |
| 93 | `EarthCheck.BLEND_M` | `0.1` | A blended point lies within this of the exact one, metres (spec-earth 8: 0.1 m; 0.054 measured 10,000 km out). |
| 95 | `EarthCheck.BLEND_REACH_M` | `10_000_000` | How far out the blend is measured, metres of the plane (spec-earth 8: 10,000 km). |
| 97 | `EarthCheck.INFLATE_MS` | `1` | A 15" tile inflates within this, milliseconds (spec-earth 8; 0.13 measured). |
| 99 | `EarthCheck.OFF_PLANET_CELLS` | `12_108` | Rotterdam's square: the cells whose 4 x 4 samples are all off the planet (spec-earth 2: 12,108 of 135,424 measured; the square is the same round any spot). |
| 101 | `EarthCheck.FIXTURE` | `"earth/earth-1-fixture.bin", EXPECT = "earth/earth-1-fixture.expect"` | The fixture and its expected figures, beside the classes. |
| 105 | `EarthCheck.out` | `System.out` | Where the harness prints: the console. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 103 | `static int fails` |  |
| 119 | `int n, bad` |  |
| 120 | `String first` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 82 | 621 | **type** `public class EarthCheck` | The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and 8): EarthProj's projection, blend and areas held to the spec's measures, and EarthData's reading of earth-1.bin held to tools/earth/earthread.py... |
| 107 | 4 | `static void assertTrue(String label, boolean ok)` |  |
| 112 | 4 | `static void report(String label, boolean ok, String detail)` |  |
| 118 | 12 | **type** `static final class Tally` | Figures compared one by one, reported as one line: every one has to agree, and at least one has to have been read. |
| 122 | 7 | `void see(boolean ok, String what)` _(in EarthCheck.Tally)_ |  |
| 131 | 6 | `static void tally(String label, Tally t)` |  |
| 138 | 46 | `public static void main(String[] args) throws Exception` |  |
| 186 | 7 | `static byte[] resource(String name) throws IOException` | A resource beside the classes, or failing that the source tree's copy from the working directory; null if neither. |

### 1. INVERSE THEN FORWARD (lines 194-259)

| line | len | member | says |
|---:|---:|---|---|
| 199 | 13 | `static List<double[]> spots(Map<String, String[]> windows)` | The spots: the windows' (Rotterdam, Perth, Paris, Fiji) and both poles. |
| 213 | 46 | `static void projection(Map<String, String[]> windows)` |  |

### 2. EQUAL AREA (lines 260-336)

| line | len | member | says |
|---:|---:|---|---|
| 264 | 59 | `static void areas()` |  |
| 325 | 11 | `static double columns(double x0, double x1, double y0, double y1)` | The area of the rectangle within the reach, integrated along x by Simpson's rule on the exact chord: the same figure as areaOnPlanetM2 by another road. |

### 3. THE BLEND (lines 337-430)

| line | len | member | says |
|---:|---:|---|---|
| 341 | 74 | `static void blend(Map<String, String[]> windows)` |  |
| 417 | 13 | `static double blendError(EarthProj p, EarthProj.Blend b, double tx, double ty)` | The farthest a tile's blended point lies from the exact one, metres, over its plots' centres and the ring of plots round it. |

### 4 TO 6. THE READER AGAINST earthread.py (lines 431-637)

| line | len | member | says |
|---:|---:|---|---|
| 435 | 3 | `static String sha(byte[] b) throws Exception` |  |
| 440 | 9 | `static String riverInts(List<EarthData.Run> runs)` | A tile's runs as the integers they were stored as, written as tools/earth/fixture.py writes them. |
| 450 | 5 | `static String lakeInts(EarthData.SmallLakes l)` |  |
| 456 | 5 | `static int segmentsOf(List<EarthData.Run> runs)` |  |
| 462 | 5 | `static int[] tilesOf(String[] f, int from)` |  |
| 469 | 130 | `static void agree(EarthData d, String scope, List<String[]> expect, Map<String, String[]> windows) throws Exception` | Every expected figure of scope "both" and of this scope, read through EarthData and compared. |
| 600 | 7 | `static Tally merge(Tally a, Tally b)` |  |
| 609 | 28 | `static void duplicates(EarthData d, Map<String, String[]> windows)` | A segment crossing a tile's edge: in both tiles' runs, once in the read across them. |

### 7. THE LRU (lines 638-673)

| line | len | member | says |
|---:|---:|---|---|
| 642 | 31 | `static void lru(byte[] fixtureBytes) throws IOException` |  |

### 8. A TILE INFLATES IN UNDER A MILLISECOND (lines 674-702)

| line | len | member | says |
|---:|---:|---|---|
| 678 | 24 | `static void inflateTime(EarthData d, byte[] fixtureBytes) throws Exception` |  |

