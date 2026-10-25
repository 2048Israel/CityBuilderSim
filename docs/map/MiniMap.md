# MiniMap.java - 471 lines · 20 methods · 5 constants · interface

`ham/citybuildersim/ui/MiniMap.java` - generated 2026-10-25 by CodeMap; line numbers are as of that run.

> The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288 x 192; a click opens the land office's map over the window on what it shows - drawn by the model's MiniMapFrame from the district pictures, at the month's end.
> 
> WHY THIS EXISTS (0.7.134, batch MM2; docs/studies/spec-globe.md 8.3, and
> its minimap/ mockup). Jerus, 2026-10-11: "i think a small map always
> visible on the ui would be nice or no?". The header is full (the money
> block and five tiles to a 1,280-pixel window) and anything floating over
> the page hides what is under it - the income dome once hid the Memorial
> Cemetery's price - so the stamp sits in the rail's own foot, which covers
> nothing (the study's Q10, his to change). What it frames, its stride, its
> pixels and what a click opens on are MiniMapFrame's, which MapCheck 12
> holds; this is the toolkit's half, checked by eye on the PC.
> 
> WHEN IT IS DRAWN: when the map's version has moved (MapTiles.version():
> a month's end, a purchase) and at least STAMP_EVERY_MS of the clock has
> passed since its picture was last begun, and again when a district it drew
> without its picture gets one. Each far node is drawn from its kept layers
> (MapTiles.KeptNode): the districts that moved repainted, the runs and the
> land only when they moved - a few hundred microseconds a month at Jerus's
> size, on the screen's thread, a node at a time (MiniMapFrame.node()), the
> stamp's one node in one frame, the preview's six over as many frames as
> FRAME_MS allows, and only while it shows. A month that is not drawn costs
> nothing: the frame (MiniMapFrame.frame(), two passes over every district)
> is found again only when a picture is about to be begun. Nothing slow is
> done here: the city's map drawn aside the first time (a draft, as the land
> office's map does, and never both at once: MapView.drafting), the nodes'
> ground and the pictures it lacks (CityMap.pictureJobs()) on MapView.WORKER,
> the map's one worker. No boats (Q12). On the real Earth it is the city's
> plane, never the globe.

**Uses:** [MiniMapFrame](MiniMapFrame.md) (16), [CityMap](CityMap.md) (12), [MapView](MapView.md) (10), [Palette](Palette.md) (8), [MapTiles](MapTiles.md) (7), [Game](Game.md) (7), [UserInterface](UserInterface.md) (2), [MapFrame](MapFrame.md) (2), [TileRaster](TileRaster.md) (2), [World](World.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 151 | · what it draws from |
| 238 | A FRAME OF THE CLOCK |
| 449 | THE PREVIEW, ON HOVER |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 58 | `MiniMap.FRAME_MS` | `MapView.FRAME_MS` | The most a frame spends drawing the stamp and the preview, in ms: MapView.FRAME_MS - a node at least, so each finishes. |
| 61 | `MiniMap.STAMP_EVERY_MS` | `250` | The least a drawing waits between beginning one picture and the next, in ms: the stamp drawn at most four times a second, against ten at 50x (a month every 100 ms), so a highway laid shows within a quarter of a second... |
| 64 | `MiniMap.PREVIEW_GAP` | `6` | The gap between the rail's edge and the preview: 6 px (the mockup's). |
| 67 | `MiniMap.PREVIEW_WORDS` | `"Click for the full map"` | The preview's line under the picture (the mockup's). |
| 70 | `MiniMap.STAMP_WORD` | `"Map"` | The stamp's word, under it as each rail button's is. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 72 | `private final UserInterface ui` |  |
| 73 | `private final VBox node` |  |
| 74 | `private final StackPane stampBox` |  |
| 75 | `private final Popup popup` |  |
| 76 | `private final VBox previewBox` |  |
| 80 | `final int w, h` |  |
| 81 | `final int[] px, buf` |  |
| 82 | `final WritableImage image` |  |
| 83 | `final ImageView view` |  |
| 84 | `MiniMapFrame.View at` |  |
| 85 | `byte[][] ground` |  |
| 86 | `boolean groundAsked` |  |
| 88 | `MapTiles.KeptNode[] keeps` | One kept node per far node of its view, row by row: what each was last drawn from (MapTiles.KeptNode), made when it is aimed at a new view. |
| 90 | `long drawnFor` | The map's version and pictures kept that the image shows, whether it was drawn short of a picture; and the pass under way: its next node, what it is of. |
| 91 | `boolean partial` |  |
| 92 | `int next` |  |
| 93 | `long passFor, passPictures` |  |
| 94 | `boolean passPartial` |  |
| 96 | `long startedAt` | When its last pass was begun (System.nanoTime()), and whether any has been since it was aimed at a view: the pace is kept from there. |
| 97 | `boolean started` |  |
| 99 | `final Set<CityMap.District> wanted` | The built districts its last pass drew without the picture they would sample: asked of WORKER (askPictures()). |
| 148 | `private final Drawing stamp` |  |
| 153 | `private CityMap shown` |  |
| 154 | `private MapTiles tiles` |  |
| 155 | `private long version` |  |
| 156 | `private MiniMapFrame.Box frame` |  |
| 157 | `private Game.MapDraft draft` |  |
| 158 | `private int draftsFailed` |  |
| 160 | `private final Set<CityMap.District> asked` | The districts whose pictures' jobs are on WORKER. |
| 162 | `private final AnimationTimer pump` |  |
| 165 | `private boolean running` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 55 | 417 | **type** `final class MiniMap` | The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288 x 192; a click opens the land office's map over the window on what it... |
| 79 | 68 | **type** `private final class Drawing` | One of its two pictures: its view, its far nodes' ground (asked of WORKER), its pixels and image, and how far its pass has come. |
| 101 | 9 | `Drawing(int w, int h)` _(in MiniMap.Drawing)_ |  |
| 112 | 12 | `void aim(MiniMapFrame.View v)` _(in MiniMap.Drawing)_ | Aimed at a view: the same view keeps its ground and its kept nodes; another asks for its own ground, makes new kept nodes and is not held to the pace. |
| 125 | 11 | `void forget()` _(in MiniMap.Drawing)_ |  |
| 138 | 3 | `boolean paceOpen(long now)` _(in MiniMap.Drawing)_ | Whether the pace lets it begin a picture at `now` (System.nanoTime()): none begun since it was aimed at its view, or STAMP_EVERY_MS passed since the last was. |
| 142 | 4 | `void blank()` _(in MiniMap.Drawing)_ |  |

### what it draws from (lines 151-237)

| line | len | member | says |
|---:|---:|---|---|
| 167 | 39 | `MiniMap(UserInterface ui)` |  |
| 208 | 4 | `private static String stampStyle(boolean hover)` | The stamp's frame: a pixel of the accent round it (the mockup's), the lighter accent under the pointer. |
| 214 | 1 | `Region node()` | The stamp and its word: what the rail lays above Menu. |
| 217 | 13 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 231 | 6 | `private void start()` |  |

### A FRAME OF THE CLOCK (lines 238-448)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 24 | `private void tick()` |  |
| 268 | 4 | `private static boolean inView(Node n)` | Whether a node and every parent of it are visible: the rail is hidden on the game's menus. |
| 282 | 40 | `private boolean keepUp(Game g)` | The minimap caught up with the city: its map drawn aside the first time - a draft on WORKER, unless the land office's map has one there already (MapView.drafting), when it waits for that - a new map (a load) thrown ou... |
| 324 | 8 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the screen's thread: kept as the city's map when it still is the city's. |
| 333 | 9 | `private void forgetDrawn()` |  |
| 353 | 36 | `private void draw(Drawing dr, long deadline)` | A picture drawn, or a node of it, until the deadline - one node at least: when the version moved, or a district it drew without its picture has one now (the map's count of pictures kept moved), and at least STAMP_EVER... |
| 391 | 14 | `private void askGround(Drawing dr)` | A view's far nodes' ground read on WORKER (the world is safe there), kept on the screen's thread while the view is still the one aimed at. |
| 415 | 33 | `private void askPictures(Drawing dr)` | The district pictures a drawing's last pass lacked: their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order - as the expanded map's far nodes ask (MapView.askPictures()) - while its view's dis... |

### THE PREVIEW, ON HOVER (lines 449-471)

| line | len | member | says |
|---:|---:|---|---|
| 454 | 13 | `private void showPreview()` | The preview beside the rail, its foot level with the stamp's, while the pointer is on the stamp: shown, then moved by what its box measures on the screen (its shadow is the popup's too). |
| 468 | 3 | `private void hidePreview()` |  |

