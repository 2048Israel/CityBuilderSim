# GlobePicture.java - 411 lines · 33 methods · 9 constants · model

`ham/citybuildersim/GlobePicture.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The planet's picture: a code a texel on a latitude-longitude grid at ARCMIN - a World class and, since ET1, its relief - made from the Earth's data by the far rule through the grid's camera - ice and desert, the forest dithered, the rivers wide enough for a texel - and a frame of the globe drawn from it, each pixel's orthographic inverse then its texel.
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
> 
> THE RELIEF (ET1 slice D; spec-earth2-terrain 9.3): the picture's byte is a
> CODE, the texel's World class in its low CLASS_BITS bits and its relief
> (World.FLAT to VERY_STEEP, the far rule's: the shares of the 4' blocks
> under the texel against the camera's dither, so a range is grey in as many
> texels as the data's shares say) above them: relief x World.CLASSES +
> class, 0 to CODES - 1, the order TileRaster's colour table is in
> (TileRaster.codeColours()), so a frame colours a texel by one lookup and
> mountains read grey from orbit while snow stays white (ice has no relief:
> its code is its class). WHY NOT MORE CLASSES: World's classes are what
> every reader that counts them knows; a code is this picture's own, and
> texel(), at() and the class counts read the class alone, as they did. An
> earth-1 file or a generated world has all FLAT: its codes are its classes.

**Uses:** [World](World.md) (8), [EarthData](EarthData.md) (5), [TileRaster](TileRaster.md) (5), [EarthTerrain](EarthTerrain.md) (4), [GlobeFrame](GlobeFrame.md) (4), [EarthProj](EarthProj.md) (2)

**Used by (8):** [EarthTerrainCheck](EarthTerrainCheck.md), [FoundingScreen](FoundingScreen.md), [GlobeCheck](GlobeCheck.md), [GlobeFrame](GlobeFrame.md), [LandMap](LandMap.md), [MapCheck](MapCheck.md), [MapView](MapView.md), [ReliefRenders](ReliefRenders.md)

## Sections

| line | section |
|---:|---|
| 159 | · making it |
| 225 | · a frame |
| 265 | THE FRAMES' POOL (0.7.135, batch E9; E8's notes 7: frame() made a pool |
| 321 | · what both screens draw it with |
| 360 | · made away from the screen |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 64 | `GlobePicture.ARCMIN` | `4` | The picture's texel, in minutes of arc: 4, the far layers' own cell (spec-globe 3.6) - 5,400 x 2,700 texels, a byte each, 14.6 MB. |
| 67 | `GlobePicture.FIRST_ARCMIN` | `12` | The picture made first, in minutes of arc: 12, three far cells a side, a ninth of the texels (spec-globe 3.6's est., ★E8) - the founding globe's own picture's texel until 0.7.135, when it came to draw this one. |
| 70 | `GlobePicture.BAND_DEG` | `10` | The picture is made a band of this many degrees of latitude at a time, one camera each: 10 (★E8) - each band reads the 1-degree tiles' rivers within WATER_PAD_DEG of it once, and the bands go one a core. |
| 76 | `GlobePicture.CLASS_BITS` | `3` | The bits of a texel's code that hold its World class: 3 (World.CLASSES is 8, 1 << CLASS_BITS - checked below); its relief is above them. |
| 79 | `GlobePicture.CLASS_MASK` | `(1<<CLASS_BITS) - 1` | A code's class: its low CLASS_BITS bits. |
| 82 | `GlobePicture.CODES` | `World.RELIEFS<<CLASS_BITS` | How many codes there are: World.RELIEFS x World.CLASSES, 32 - the length of a table of colours a texel indexes (TileRaster.codeColours()). |
| 209 | `GlobePicture.KEPT` | `new HashMap<>()` | The pictures made, by the data's identity and texel: once for each Earth file, never saved. |
| 331 | `GlobePicture.SPACE` | `TileRaster.VOID` | Space round the globe: the map's own void (TileRaster.VOID), as the plane draws past the planet's rim. |
| 363 | `GlobePicture.MAKING` | `new HashMap<>()` | The identities whose pictures are being made, and who to tell as each is kept. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `private final int arcmin, cols, rows` |  |
| 98 | `private final double perDeg` |  |
| 99 | `private final byte[] texels` |  |
| 100 | `private final String identity` |  |
| 101 | `private final double madeMs` |  |
| 275 | `private static volatile ExecutorService frames` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 61 | 351 | **type** `public final class GlobePicture` | The planet's picture: a code a texel on a latitude-longitude grid at ARCMIN - a World class and, since ET1, its relief - made from the Earth's data by the far rule through the grid's camera - ice and desert, the fores... |
| 73 | 1 | `public static double texelKm(int arcmin)` | A texel's height on the ground, km: its arc on the sphere (EarthTerrain.M_A_DEGREE). |
| 84 | 3 | `static { ... }` |  |
| 89 | 1 | `public static byte code(int cover, int relief)` | The code of a texel of World class `cover` and relief `relief`: relief x World.CLASSES + class (the class alone at FLAT). |
| 92 | 1 | `public static int classOf(int code)` | A code's World class. |
| 95 | 1 | `public static int reliefOf(int code)` | A code's relief (World.FLAT to VERY_STEEP). |
| 103 | 9 | `private GlobePicture(int arcmin, byte[] texels, String identity, double madeMs)` |  |
| 114 | 1 | `public int arcmin()` | The picture's texel, minutes of arc. |
| 116 | 1 | `public int cols()` | Its texels across (from 180 W) and down (from 90 N). |
| 117 | 1 | `public int rows()` |  |
| 119 | 1 | `public String identity()` | The data it was made from, by identity. |
| 121 | 1 | `public double madeMs()` | How long it took to make, ms on the wall clock. |
| 123 | 1 | `public long bytes()` | What it holds, bytes: a byte a texel. |
| 126 | 1 | `public byte texel(int row, int col)` | Texel (row, col)'s World class (its code without the relief). |
| 129 | 1 | `public byte texelCode(int row, int col)` | Texel (row, col)'s code: its class and relief (code()). |
| 132 | 1 | `byte[] texels()` | Its texels' codes, row by row from the north: shared, do not write into it (GlobeCheck's); classOf() reads a code's class. |
| 135 | 3 | `public byte at(double lat, double lon)` | The World class of the texel (lat, lon) lies in. |
| 140 | 3 | `public byte codeAt(double lat, double lon)` | The code of the texel (lat, lon) lies in: its class and its relief. |
| 145 | 3 | `public int reliefAt(double lat, double lon)` | The relief of the texel (lat, lon) lies in: World.FLAT to VERY_STEEP. |
| 149 | 4 | `private int row(double lat)` |  |
| 154 | 4 | `private int col(double lon)` |  |

### making it (lines 159-224)

| line | len | member | says |
|---:|---:|---|---|
| 162 | 20 | `public static GlobePicture make(EarthData d, int arcmin, int threads)` | The data's picture at `arcmin` (a divisor of 60), made on `threads` threads a band each: the far rule through the grid's camera (EarthTerrain.grid()). |
| 184 | 23 | `private static void run(List<Runnable> jobs, int threads)` | Jobs run on `threads` threads (on this one alone at one), waited for. |
| 212 | 12 | `public static GlobePicture of(EarthData d, int arcmin)` | The data's picture at `arcmin`, made the first time it is asked for (on as many threads as there are cores) and kept - FIRST_ARCMIN then ARCMIN, as the globe asks. |

### a frame (lines 225-264)

| line | len | member | says |
|---:|---:|---|---|
| 236 | 18 | `public void frame(GlobeFrame f, int[] colours, int space, int[] out, int j0, int j1)` | Rows j0 to j1 - 1 of a frame of the view `f` into out (width x height, a pixel an int): each pixel's centre through the view's orthographic inverse to its texel, the texel's colour from `colours` - by its code from a ... |
| 256 | 8 | `public void frame(GlobeFrame f, int[] colours, int space, int[] out, int threads)` | ...the whole frame, in bands of rows, one a thread (`threads`; this one alone at one) - on the one pool the globe's frames share (FRAMES, since 0.7.135). |

### THE FRAMES' POOL (0.7.135, batch E9; E8's notes 7: frame() made a pool (lines 265-320)

| line | len | member | says |
|---:|---:|---|---|
| 273 | 1 | **type** `public interface Rows` | Rows j0 to j1 - 1 of a frame. |
| 273 | 1 | `void run(int j0, int j1)` _(in GlobePicture.Rows)_ |  |
| 278 | 15 | `static ExecutorService frames()` | The pool: as many threads as there are cores, daemons, made once. |
| 295 | 1 | `public static int cores()` | The threads a frame is cut into bands for: one a core. |
| 298 | 22 | `public static void bands(int rows, int threads, Rows job)` | A frame's `rows` rows cut into `threads` bands, run on the pool and waited for (on this thread alone at one). |

### what both screens draw it with (lines 321-359)

| line | len | member | says |
|---:|---:|---|---|
| 324 | 5 | `public static int[] colours(boolean dimmed)` | The ground's colours as the globe draws them, by code (CODES of them: a class and its relief, TileRaster.codeColours(), whose FLAT row is TileRaster.GROUND), or (`dimmed`) the world off the city's land as the expanded... |
| 342 | 17 | `public void view(GlobeFrame f, World.Key key, int[] colours, int space, int[] out)` | A view of the globe in its own tier, into out (a pixel an int, the view's width a row), on the calling thread and the pool - the founding screen's globe (0.7.135, spec-globe 3.8): in the PICTURE tier this picture's fr... |

### made away from the screen (lines 360-411)

| line | len | member | says |
|---:|---:|---|---|
| 366 | 4 | `public static EarthData dataOf(World.Key key)` | The Earth's data an Earth world is drawn from (the screen's way to its picture); null for a generated world. |
| 372 | 6 | `public static GlobePicture best(EarthData d)` | The finest picture kept of the data: ARCMIN's once it is made, FIRST_ARCMIN's before, null before either. |
| 380 | 31 | `public static void prepare(EarthData d, Runnable kept)` | The data's pictures made on a thread of their own if they are not kept - FIRST_ARCMIN, then ARCMIN (spec-globe 3.6) - and `kept` run on that thread after each (every caller's, while they are made); the screens draw be... |

