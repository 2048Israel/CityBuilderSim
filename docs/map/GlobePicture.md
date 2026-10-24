# GlobePicture.java - 358 lines · 27 methods · 6 constants · model

`ham/citybuildersim/GlobePicture.java` - generated 2026-10-24 by CodeMap; line numbers are as of that run.

> The planet's picture: a World class a texel on a latitude-longitude grid at ARCMIN, made from the Earth's data by the far rule through the grid's camera - ice and desert, the forest dithered, the rivers wide enough for a texel - and a frame of the globe drawn from it, each pixel's orthographic inverse then its texel.
> 
> WHY THIS EXISTS (0.7.132, batch E8; docs/studies/spec-globe.md 3.6,
> Jerus's Q5). The globe's widest views (GlobeFrame's PICTURE tier) show the
> whole planet at 17.6 km a pixel and more; drawing every pixel through the
> far rule would read the data afresh at every turn. The founding globe
> (E4) kept a picture of the planet at 12' of colours blended from the
> shares - no rivers, and a desert in the beach's colour - so both globes
> drew the planet unlike the map. This picture is the map's own ground: the
> far rule (EarthTerrain.farTerrain()) through a latitude-longitude grid's
> camera (EarthTerrain.grid()), a texel the 4' far layers' own cell, its
> class what the far view would draw there - the sea at the far land's
> half-contour, the large lakes, ice and the desert by land cover's bits,
> the forest dithered from the fixed grid (EarthTerrain.globeDither()), and
> the rivers stroked at least FAR_RIVER_SHARE of a texel wide (89 m and
> wider at 4') with the small lakes.
> 
> Made once for each Earth file and never saved (of()): FIRST_ARCMIN first,
> a ninth of the texels, then ARCMIN (★E8). It is a pure function of the
> data: the seed's detail is below a texel (every noise octave at its mean),
> and the globe's dither is the point's, not the city's.
> 
> A FRAME (frame()) is a view's pixels, each through the view's orthographic
> inverse (EarthProj.Ortho) to its texel and the texel's colour, space off
> the disc - in bands of rows, one a core. GlobeCheck holds the picture's
> land per 1-degree cell against the far layers', and a frame's time.
> 
> ON SCREEN (0.7.135, batch E9): every frame of the globe - this picture's,
> and the GROUND tier's composed by GlobeFrame.ground() - is cut into bands
> on one pool of daemon threads, a core each (bands(); E8's frame() made a
> pool a call); the pictures are made on a thread of their own the first
> time a screen asks (prepare(), best()); and the founding screen's globe
> is drawn by view(), the picture and then the far rule through the camera,
> so both screens draw the same planet in the same colours (spec-globe 3.8,
> Jerus's Q7) - MapCheck 13 holds it.

**Uses:** [EarthData](EarthData.md) (5), [World](World.md) (5), [TileRaster](TileRaster.md) (5), [EarthTerrain](EarthTerrain.md) (4), [GlobeFrame](GlobeFrame.md) (4), [EarthProj](EarthProj.md) (2)

**Used by (5):** [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [GlobeFrame](GlobeFrame.md), [MapCheck](MapCheck.md), [MapView](MapView.md)

## Sections

| line | section |
|---:|---|
| 111 | · making it |
| 176 | · a frame |
| 212 | THE FRAMES' POOL (0.7.135, batch E9; E8's notes 7: frame() made a pool |
| 268 | · what both screens draw it with |
| 307 | · made away from the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 51 | `GlobePicture.ARCMIN` | `4` | The picture's texel, in minutes of arc: 4, the far layers' own cell (spec-globe 3.6) - 5,400 x 2,700 texels, a byte each, 14.6 MB. |
| 54 | `GlobePicture.FIRST_ARCMIN` | `12` | The picture made first, in minutes of arc: 12, three far cells a side, a ninth of the texels (spec-globe 3.6's est., ★E8) - the founding globe's own picture's texel until 0.7.135, when it came to draw this one. |
| 57 | `GlobePicture.BAND_DEG` | `10` | The picture is made a band of this many degrees of latitude at a time, one camera each: 10 (★E8) - each band reads the 1-degree tiles' rivers within WATER_PAD_DEG of it once, and the bands go one a core. |
| 160 | `GlobePicture.KEPT` | `new HashMap<>()` | The pictures made, by the data's identity and texel: once for each Earth file, never saved. |
| 278 | `GlobePicture.SPACE` | `TileRaster.VOID` | Space round the globe: the map's own void (TileRaster.VOID), as the plane draws past the planet's rim. |
| 310 | `GlobePicture.MAKING` | `new HashMap<>()` | The identities whose pictures are being made, and who to tell as each is kept. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 62 | `private final int arcmin, cols, rows` |  |
| 63 | `private final double perDeg` |  |
| 64 | `private final byte[] texels` |  |
| 65 | `private final String identity` |  |
| 66 | `private final double madeMs` |  |
| 222 | `private static volatile ExecutorService frames` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 48 | 311 | **type** `public final class GlobePicture` | The planet's picture: a World class a texel on a latitude-longitude grid at ARCMIN, made from the Earth's data by the far rule through the grid's camera - ice and desert, the forest dithered, the rivers wide enough fo... |
| 60 | 1 | `public static double texelKm(int arcmin)` | A texel's height on the ground, km: its arc on the sphere (EarthTerrain.M_A_DEGREE). |
| 68 | 9 | `private GlobePicture(int arcmin, byte[] texels, String identity, double madeMs)` |  |
| 79 | 1 | `public int arcmin()` | The picture's texel, minutes of arc. |
| 81 | 1 | `public int cols()` | Its texels across (from 180 W) and down (from 90 N). |
| 82 | 1 | `public int rows()` |  |
| 84 | 1 | `public String identity()` | The data it was made from, by identity. |
| 86 | 1 | `public double madeMs()` | How long it took to make, ms on the wall clock. |
| 88 | 1 | `public long bytes()` | What it holds, bytes: a byte a texel. |
| 91 | 1 | `public byte texel(int row, int col)` | Texel (row, col)'s class. |
| 94 | 1 | `byte[] texels()` | Its texels, row by row from the north: shared, do not write into it (GlobeCheck's). |
| 97 | 3 | `public byte at(double lat, double lon)` | The class of the texel (lat, lon) lies in. |
| 101 | 4 | `private int row(double lat)` |  |
| 106 | 4 | `private int col(double lon)` |  |

### making it (lines 111-175)

| line | len | member | says |
|---:|---:|---|---|
| 114 | 19 | `public static GlobePicture make(EarthData d, int arcmin, int threads)` | The data's picture at `arcmin` (a divisor of 60), made on `threads` threads a band each: the far rule through the grid's camera (EarthTerrain.grid()). |
| 135 | 23 | `private static void run(List<Runnable> jobs, int threads)` | Jobs run on `threads` threads (on this one alone at one), waited for. |
| 163 | 12 | `public static GlobePicture of(EarthData d, int arcmin)` | The data's picture at `arcmin`, made the first time it is asked for (on as many threads as there are cores) and kept - FIRST_ARCMIN then ARCMIN, as the globe asks. |

### a frame (lines 176-211)

| line | len | member | says |
|---:|---:|---|---|
| 184 | 17 | `public void frame(GlobeFrame f, int[] colours, int space, int[] out, int j0, int j1)` | Rows j0 to j1 - 1 of a frame of the view `f` into out (width x height, a pixel an int): each pixel's centre through the view's orthographic inverse to its texel, the texel's class's colour from `colours` (TileRaster.G... |
| 203 | 8 | `public void frame(GlobeFrame f, int[] colours, int space, int[] out, int threads)` | ...the whole frame, in bands of rows, one a thread (`threads`; this one alone at one) - on the one pool the globe's frames share (FRAMES, since 0.7.135). |

### THE FRAMES' POOL (0.7.135, batch E9; E8's notes 7: frame() made a pool (lines 212-267)

| line | len | member | says |
|---:|---:|---|---|
| 220 | 1 | **type** `public interface Rows` | Rows j0 to j1 - 1 of a frame. |
| 220 | 1 | `void run(int j0, int j1)` _(in GlobePicture.Rows)_ |  |
| 225 | 15 | `static ExecutorService frames()` | The pool: as many threads as there are cores, daemons, made once. |
| 242 | 1 | `public static int cores()` | The threads a frame is cut into bands for: one a core. |
| 245 | 22 | `public static void bands(int rows, int threads, Rows job)` | A frame's `rows` rows cut into `threads` bands, run on the pool and waited for (on this thread alone at one). |

### what both screens draw it with (lines 268-306)

| line | len | member | says |
|---:|---:|---|---|
| 271 | 5 | `public static int[] colours(boolean dimmed)` | The ground's colours as the globe draws them: TileRaster.GROUND, or (`dimmed`) the world off the city's land as the expanded map dims it (TileRaster.UNOWNED_DIM; spec-globe 3.7) - the far nodes' own. |
| 289 | 17 | `public void view(GlobeFrame f, World.Key key, int[] colours, int space, int[] out)` | A view of the globe in its own tier, into out (a pixel an int, the view's width a row), on the calling thread and the pool - the founding screen's globe (0.7.135, spec-globe 3.8): in the PICTURE tier this picture's fr... |

### made away from the screen (lines 307-358)

| line | len | member | says |
|---:|---:|---|---|
| 313 | 4 | `public static EarthData dataOf(World.Key key)` | The Earth's data an Earth world is drawn from (the screen's way to its picture); null for a generated world. |
| 319 | 6 | `public static GlobePicture best(EarthData d)` | The finest picture kept of the data: ARCMIN's once it is made, FIRST_ARCMIN's before, null before either. |
| 327 | 31 | `public static void prepare(EarthData d, Runnable kept)` | The data's pictures made on a thread of their own if they are not kept - FIRST_ARCMIN, then ARCMIN (spec-globe 3.6) - and `kept` run on that thread after each (every caller's, while they are made); the screens draw be... |

