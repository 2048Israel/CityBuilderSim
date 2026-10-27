# CrossingCheck.java - 520 lines · 27 methods · 2 constants · harnesses

`ham/citybuildersim/CrossingCheck.java` - generated 2026-10-26 by CodeMap; line numbers are as of that run.

> Short crossings of bridgeable water (ET6; spec-earth2-terrain 6.2, D12): the shallow sea is water a road or a railway
> may bridge, exactly as fresh water is, and a highway or a railway reaches 34 plots (1,020 m) across it.
> 
> WHAT THIS HAS TO PROVE (the sections below are numbered to match):
> 
>   1. A HIGHWAY AND A RAILWAY SPAN THE SHALLOW SEA UP TO THEIR REACH. A strait of SHALLOW plots exactly as wide as the
>      net bridges (TilePainter.MAX_BRIDGE[HIGHWAY], TilePainter.RAIL_BRIDGE), the city's dry ground beyond it: CityRuns
>      clear() takes the whole crossing, along a row and along a diagonal; one plot wider it stops at the sea, and the
>      same strait of fresh water is judged alike. A strait with no owned ground beyond it is not crossed (all or
>      nothing: the stop is the city's edge). A highway net grown on a strait of its reach lays the plots on the water
>      and the ground beyond; on one plot wider it lays nothing beyond; and the painter draws a run's plot on
>      shallow water as a bridge (and a dry plot's not), a street's plot too, and the raster its deck.
> 
>   2. THE SPAN IS BRIDGEABLE WATER ALONE. A span of fresh and shallow plots in any mix is one crossing under the same
>      reach (and one plot wider stops); a single plot of deep sea (SALT) anywhere in the span - the first plot, the
>      middle, the last, or the landing - stops it, the premise being the same span with the plot shallow. On a
>      45-degree step a deep corner in the span stops the run and a shallow one does not; onto dry ground a shallow
>      corner stops it, as it always did (a highway never shaves a shallow coast), and a fresh one does not.
> 
>   3. A STREET AND AN ARTERIAL KEEP THEIR OWN REACH. Over a district plan whose ground is a band of shallow water across
>      it: a street is laid across STREET_BRIDGE plots of it and not one plot more, the plots on the water bridges; an
>      arterial crosses ARTERIAL_BRIDGE plots and not one more - ARTERIAL_BRIDGE is no longer a highway's - and a
>      band of deep sea is crossed by neither. An avenue (the diagonal arterial) bridges shallow water as it does fresh,
>      AVENUE_BRIDGE_STEPS and not one step more, and its counts follow its plots where a stray piece is taken up. The
>      plan over shallow water is the plan over fresh water, plot for plot, and a tile of it is drawn as the fresh one
>      is (the water's colour apart); the sea has no deck.
> 
>   4. THE DIALS. A highway and a railway span 34 plots, an arterial 14, a street 6 - the decision (D12) pinned by name,
>      the one place the numbers are typed.
> 
> WHY THIS EXISTS. A run stopped at every sea (World.salt()), so a Halifax city that owned both shores of the Narrows
> laid no highway across the Macdonald's 820 m or the MacKay's 805 m, and the 14-plot reach (420 m) refused them even
> across fresh water. ET1 made the shallow sea a class of its own and left every rule that asks "is this the sea?"
> alone; this is the rule that asks "can a bridge cross it?", and a fixture CAUSES each case: a strait of a given width
> and kind, a plan with a band of water across it. The map alone is changed (the model reads nothing of it), so the
> default playtest's trace cannot move.

**Uses:** [World](World.md) (76), [TilePainter](TilePainter.md) (58), [DistrictPlan](DistrictPlan.md) (37), [CityRuns](CityRuns.md) (30), [BuildingVisual](BuildingVisual.md) (15), [TileRaster](TileRaster.md) (6), [PlanCheck](PlanCheck.md) (4), [CityMap](CityMap.md) (1), [IslandNetCheck](IslandNetCheck.md) (1), [BuildingCatalog](BuildingCatalog.md) (1)

## Sections

| line | section |
|---:|---|
| 290 | · 1. a highway and a railway span the shallow sea up to their reach |
| 379 | · 2. the span is bridgeable water alone |
| 433 | · 3. a street and an arterial keep their own reach |
| 508 | · 4. the dials |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 161 | `CrossingCheck.TOP` | `160` | The plan's frame row (or column) at which a band of water starts across it (PlanCheck 10 (e)'s: south of the hub, with district to spare either side). |
| 164 | `CrossingCheck.ARTERIAL_LINES` | `(DistrictPlan.FRAME - 1) / DistrictPlan.CELL + 1` | How many lines of the plan's frame are an arterial's: the multiples of CELL (a district's lines), the frame's first and last among them. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 48 | `static int fails` |  |
| 57 | `final long x0, y0, x1, y1` |  |
| 58 | `final List<long[]> bands` |  |
| 59 | `final Map<Long, Integer> over` |  |
| 174 | `final DistrictPlan.Input in` |  |
| 175 | `final DistrictPlan plan` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 46 | 475 | **type** `public class CrossingCheck` | Short crossings of bridgeable water (ET6; spec-earth2-terrain 6.2, D12): the shallow sea is water a road or a railway may bridge, exactly as fresh water is, and a highway or a railway reaches 34 plots (1,020 m) across... |
| 50 | 4 | `static void check(String label, boolean ok)` |  |
| 56 | 33 | **type** `static final class Cells implements CityRuns.Ground` | A strip of ground the runs read: the owned box (x0..x1 by y0..y1), grass by default, with bands of water across it and single plots set apart. |
| 61 | 3 | `Cells(long x0, long y0, long x1, long y1)` _(in CrossingCheck.Cells)_ |  |
| 66 | 4 | `Cells band(long xa, long xb, int cls)` _(in CrossingCheck.Cells)_ | Columns xa..xb of every row of the box are class cls. |
| 72 | 4 | `Cells set(long x, long y, int cls)` _(in CrossingCheck.Cells)_ | The one plot (x, y) is class cls, whatever the bands say. |
| 77 | 7 | `public int at(long x, long y, int dir)` _(in CrossingCheck.Cells)_ |  |
| 85 | 1 | `public long version()` _(in CrossingCheck.Cells)_ |  |
| 87 | 1 | `public long[] box()` _(in CrossingCheck.Cells)_ |  |
| 91 | 3 | `static Cells strait(int width, int cls)` | A strait running north to south across a box, `width` plots of class cls from column 1, grass on both banks (column 0 and below; column width + 1 and above), owned from column -30 to width + 60. |
| 96 | 8 | `static int[] go(CityRuns.Net n, Cells g, int d)` | What clear() makes of the way from (0, 0) along heading d, looking up to the strait's width and twenty plots more: {the steps it takes, what stops it}. |
| 106 | 4 | `static boolean crosses(CityRuns.Net n, Cells g, int d)` | Whether the way crosses the whole strait and goes on to the look's end, nothing stopping it. |
| 112 | 4 | `static boolean stopsAtShore(CityRuns.Net n, Cells g, int d, int why)` | Whether the way stops at the shore, before the first plot of water, the sea (or the city's edge) being what stops it. |
| 118 | 10 | `static TilePainter.Input rowTile(int y, int xa, byte cls, byte run)` | One tile of the painter's input: dry grass owned all over, the row y of it a run of kind `run` from the first column to the last, its columns xa to the end class cls. |
| 130 | 6 | `static TilePainter.Input streetTile(int y, int xa, byte cls)` | ...with a paved street along the row instead of a run, its code carrying no bridge bit. |
| 137 | 5 | `static TilePainter.Painted paint(TilePainter.Input in)` |  |
| 144 | 5 | `static int bridged(TilePainter.Painted p, int y, int xa, int xb)` | How many plots of the tile's row y, columns xa to xb, are drawn as bridge plots. |
| 151 | 8 | `static int decks(TilePainter.Input in, int px)` | How many pixels of the tile drawn at px a plot are the bridge deck's colour (TileRaster.DECK). |
| 167 | 4 | `static boolean inBand(int x, int y, int width, boolean columns)` | Whether frame plot (x, y) lies in the band of water, `width` rows (or, `columns`, columns) from TOP. |
| 173 | 9 | **type** `static final class Planned` | A plan and the input it was made from (its ground is what a tile is drawn on). |
| 177 | 4 | `Planned(DistrictPlan.Input in, DistrictPlan plan)` _(in CrossingCheck.Planned)_ |  |
| 184 | 3 | `static DistrictPlan planOver(BuildingVisual.Type[] types, int width, byte cls, boolean columns)` | A flat district at his fill (PlanCheck's) with a band of class cls `width` rows (or columns) wide across the whole frame, and its plan. |
| 189 | 10 | `static DistrictPlan planOver(BuildingVisual.Type[] types, int width, byte cls, byte rest, boolean columns)` | ...the band's first half of class cls and the rest of class rest (a span of two kinds of water). |
| 201 | 9 | `static Planned planRows(BuildingVisual.Type[] types, int top, java.util.function.IntPredicate wet, byte cls)` | A flat district at his fill with rows of class cls where `wet` says so (by the row's distance from `top`), and its plan. |
| 212 | 24 | `static TilePainter.Input tileOf(Planned pl, int tx, int ty)` | The tile (tx, ty) of a plan's frame as the map hands it to the painter: the plan's ground, its streets (and the ring of them about the tile) and its avenue marks, the origin the avenue's arms run out from; the runs an... |
| 238 | 6 | `static int[] imageOf(TilePainter.Input in, int px)` | The tile drawn at px pixels a plot. |
| 246 | 6 | `static int differ(int[] shallow, int[] fresh)` | How many pixels of two tiles differ once the shallow sea's colour in the first is read as fresh water's. |
| 254 | 5 | `static int deckPixels(int[] img)` | How many pixels of an image are the bridge deck's colour. |
| 265 | 17 | `static int[] onWater(DistrictPlan p, int width, boolean columns)` | What a plan lays on the band: {street plots, of them bridges, of them on an arterial's line (a multiple of CELL across the band), on another line and not an avenue's, on an avenue}. |
| 283 | 237 | `public static void main(String[] args)` |  |

