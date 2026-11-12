# QuayRailCheck.java - 657 lines · 39 methods · 6 constants · harnesses

`ham/citybuildersim/QuayRailCheck.java` - generated 2026-11-12 by CodeMap; line numbers are as of that run.

> The quay lines of the harbour, the spur the railway lays to each, and the railed terminals QuayRail reads from them (batch HB2, Jerus
> 2026-11-09: "tracks on the quay, with the railway leaning toward a port so a line reaches those tracks").
> 
> WHAT THIS HAS TO PROVE:
> 
>   1. THE QUAY LINES: CityShore.quayLines() groups the berthed works whose strips join into one wall: a bay of berths WALL_JOIN_GAP apart is
>      one line of its works in shore order in each of the four sides, berths farther apart are a line each, the line's span and outer
>      edge are the strip's, and the union of walls() plots is the lines' strips less every box (the walls did not move when they and the lines
>      came from one grouping); the lines are cached with the walls.
> 
>   2. THE ENDS: QuayRail.ends() gives two plots a work, BEHIND its land edge at its first and its last column along the shore, none of them
>      blocked, one past the verge the shore keeps clear; an end a neighbour's verge covers is left out.
> 
>   3. THE SPUR: with a main line within QUAY_REACH of a bay and the ends set, layTo() lays one quay spur, from the main line to one of the
>      line's ends, over ground the shore leaves free, as short as any way from the line to any end; the model's railway is the one laid with
>      no ends (sidings, branches, yards, the surplus), the railway has the spur's plots more, and the spur has no loading shed where a siding
>      still has.
> 
>   4. NO SPUR: none to a bay farther than QUAY_REACH, none behind ground the runs cannot take, and two bays in reach get a spur each that
>      share no plot.
> 
>   5. THE STAMP: laid again to the same ends nothing moves; other ends, or a berth laid, lay it again; ends cleared take it off and the
>      railway is the one laid with none.
> 
>   6. THE SIDECAR: the net with its spurs reads back as it was, its quaySpurs() the same, and laying it again to the same ends moves nothing.
> 
>   7. THE RAILED TERMINALS: QuayRail.of() gives every work of a line a spur ends at the quay track, open to its neighbours, the lead at the
>      box the spur stands behind; the plots between the boxes and behind the lead's box are its cells; a line no spur ends at has none.
> 
> WHY THIS EXISTS: before HB2 no port had rail, and the map draws tracks on a terminal's quay only where the railway reaches it. A fixture
> CAUSES each condition (PortCheck's bay of berths, the ground the map would give it), and every assertion is against the model's own
> constants (WALL_JOIN_GAP, CLEAR, BEHIND, QUAY_REACH), never a bare number.

**Uses:** [CityShore](CityShore.md) (71), [CityRuns](CityRuns.md) (37), [QuayRail](QuayRail.md) (28), [HarbourYard](HarbourYard.md) (25), [PortCheck](PortCheck.md) (8), [TilePainter](TilePainter.md) (3), [World](World.md) (2)

## Sections

| line | section |
|---:|---|
| 92 | · the fixtures |
| 256 | 1. the quay lines |
| 334 | 2. the ends |
| 387 | 3. the spur |
| 429 | 4. no spur |
| 488 | 5. the stamp |
| 528 | 6. the sidecar |
| 555 | 7. the railed terminals |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `QuayRailCheck.SIDES` | `{ "north", "east", "south", "west" }` | The sides' names, by CityShore's number: the side the sea is on. |
| 61 | `QuayRailCheck.JOINED` | `PortCheck.BULK_ALONG + CityShore.WALL_JOIN_GAP` | The pitch of a bay that joins into one wall: the berths' gap is WALL_JOIN_GAP, the widest that still reads as one. |
| 64 | `QuayRailCheck.APART` | `JOINED + 1` | The pitch of a bay that does not: one plot past that. |
| 95 | `QuayRailCheck.SEA_DEEP` | `40, SEA_WIDE = 5` | How far out the fixture's sea reaches from a box's sea edge, in plots, and how far past its ends along the shore. |
| 98 | `QuayRailCheck.X_HUB` | `2900` | The column the hub stands at, before the lattice takes it: the main line runs east from it past the bay. |
| 101 | `QuayRailCheck.TRACK` | `3000` | The model's track in the fixture, in plots: more than the main line to the city's edge and the siding use. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 50 | `static int fails` |  |
| 108 | `final CityShore bay` |  |
| 109 | `final long x0, y0, x1, y1` |  |
| 110 | `long bandFrom` |  |
| 146 | `final BayGround g` |  |
| 147 | `final long hx, hy, track` |  |
| 148 | `final List<int[]> yards` |  |
| 149 | `final int spurs` |  |
| 150 | `final List<long[]> works` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 610 | **type** `public class QuayRailCheck` | The quay lines of the harbour, the spur the railway lays to each, and the railed terminals QuayRail reads from them (batch HB2, Jerus 2026-11-09: "tracks on the quay, with the railway leaning toward a port so a line r... |
| 52 | 4 | `static void check(String label, boolean ok)` |  |
| 67 | 3 | `static CityShore bay(int count, int side, int pitch)` | A bay of `count` berths facing `side`, `pitch` plots apart along the shore, from (3000, 3000). |
| 72 | 5 | `static CityShore shifted(CityShore s, long dx, long dy)` | The same works moved (dx, dy) plots. |
| 78 | 3 | `static long key(long x, long y)` |  |
| 83 | 3 | `static long[] box(long x0, long y0, int w, int h)` | A works' box {x0, y0, x1, y1} (inclusive) from its north-west plot and its sides. |
| 88 | 3 | `static long lattice(long v)` | The row or column of the railway's lattice at or before v: the plots the railway's lines run along. |

### the fixtures (lines 92-255)

| line | len | member | says |
|---:|---:|---|---|
| 107 | 36 | **type** `static final class BayGround implements CityRuns.Ground` | The ground of a bay as the map gives it: owned dry ground in a box, -1 where the shore blocks (CityMap.RunGround.read: no run crosses a terminal) or in a band of rows across the whole box, and the sea seaward of every... |
| 112 | 4 | `BayGround(CityShore bay, long x0, long y0, long x1, long y1)` _(in QuayRailCheck.BayGround)_ |  |
| 117 | 6 | `public int at(long x, long y, int dir)` _(in QuayRailCheck.BayGround)_ |  |
| 125 | 13 | `boolean seaward(long x, long y)` _(in QuayRailCheck.BayGround)_ | Whether plot (x, y) is out to sea from a work: within SEA_DEEP of its sea edge and SEA_WIDE of its span along the shore. |
| 139 | 1 | `public long version()` _(in QuayRailCheck.BayGround)_ |  |
| 141 | 1 | `public long[] box()` _(in QuayRailCheck.BayGround)_ |  |
| 145 | 28 | **type** `static final class Rig` | A railway to lay over a bay: the ground, the hub, the model's track, its yards and Rail Spurs, and the bulk works. |
| 152 | 3 | `Rig(BayGround g, long hx, long hy, long track, List<int[]> yards, int spurs, List<long[]> works)` _(in QuayRailCheck.Rig)_ |  |
| 157 | 1 | `List<long[]> ends()` _(in QuayRailCheck.Rig)_ | The quay ends of the bay as the map hands them over. |
| 160 | 3 | `boolean lay(CityRuns runs)` _(in QuayRailCheck.Rig)_ | Lays `runs` to the rig's counts, as CityMap.layRuns() does; whether anything moved. |
| 165 | 7 | `CityRuns fresh(List<long[]> ends)` _(in QuayRailCheck.Rig)_ | A new railway with a gateway east, the quay ends set (none left unset when null), laid to the rig's counts. |
| 175 | 5 | `static Rig rig(CityShore bay, long row)` | A rig over `bay` with the main line along the lattice row `row`: a yard, a Rail Spur and a bulk works south of the line, a hundred plots from the hub's column on. |
| 182 | 8 | `static long rowBehind(List<long[]> ends, int side, int gap)` | The lattice row to run the main line along, `gap` plots or more behind the ends of a bay on `side`: behind is south of a north-facing bay, north of a south-facing one, and past the last berth for the sides the shore r... |
| 192 | 9 | `static List<long[]> plotsOf(CityRuns.Net n, int ai)` | The plots arm `ai` of net n lays after its start, {x, y} in order. |
| 203 | 6 | `static byte[] marksOf(CityRuns runs, BayGround g)` | The marks a railway lays over the rig's whole ground (CityRuns.fill()). |
| 210 | 4 | `static int markAt(BayGround g, byte[] marks, long x, long y)` |  |
| 215 | 5 | `static int sheds(byte[] marks)` |  |
| 222 | 4 | `static boolean isEnd(List<long[]> ends, long x, long y)` | Whether plot (x, y) is one of the ends {line, x, y}. |
| 228 | 13 | `static long nearestWay(CityRuns plain, List<long[]> ends)` | The shortest way, in plots, from any plot of the plain railway to any of `ends`: scanned over the plots about each end, never by the model's candidates. |
| 242 | 13 | `public static void main(String[] args)` |  |

### 1. the quay lines (lines 256-333)

| line | len | member | says |
|---:|---:|---|---|
| 259 | 5 | `static Set<Long> wallPlots(CityShore s)` | The plots of the walls of `s`. |
| 266 | 16 | `static Set<Long> stripPlots(CityShore s)` | The plots of the lines' strips of `s`, each from the line's span and outer edge, less every work's box. |
| 283 | 50 | `static void quayLines()` |  |

### 2. the ends (lines 334-386)

| line | len | member | says |
|---:|---:|---|---|
| 337 | 15 | `static long[] expectedEnd(CityShore.Work k, boolean high)` | The plot BEHIND work k's land edge at its first (or last) column, worked out from the box's plots: the one in that column farthest from the sea, BEHIND further on. |
| 353 | 33 | `static void theEnds()` |  |

### 3. the spur (lines 387-428)

| line | len | member | says |
|---:|---:|---|---|
| 389 | 39 | `static void theSpur()` |  |

### 4. no spur (lines 429-487)

| line | len | member | says |
|---:|---:|---|---|
| 432 | 7 | `static Rig backToBack()` | Two bays back to back with the main line between them: a north-facing one, and (forty plots south) a south-facing one, so each one's land is the other's. |
| 440 | 47 | `static void noSpur()` |  |

### 5. the stamp (lines 488-527)

| line | len | member | says |
|---:|---:|---|---|
| 490 | 37 | `static void theStamp()` |  |

### 6. the sidecar (lines 528-554)

| line | len | member | says |
|---:|---:|---|---|
| 530 | 24 | `static void theSidecar()` |  |

### 7. the railed terminals (lines 555-657)

| line | len | member | says |
|---:|---:|---|---|
| 558 | 11 | `static long seaRow(CityShore.Work k)` | The sea-most row (or column) of work k's box: the coordinate of its plots nearest the sea, across the shore. |
| 571 | 11 | `static Set<Long> gapPlots(CityShore.QuayLine l)` | The plots of a line's sea-most row that no box of the line holds, from the line's first plot along the shore to its last: where the quay track runs between the boxes. |
| 584 | 6 | `static Set<Long> between(long ax, long ay, long bx, long by)` | The plots strictly between plot a and plot b, which lie on one row or column. |
| 592 | 5 | `static long[] landEdge(CityShore.Work k, boolean high)` | The plot of work k's box at its land edge in its first (or last) column along the shore: the one in that column farthest from the sea. |
| 598 | 59 | `static void railedTerminals()` |  |

