# MiniMap.java - 440 lines · 19 methods · 4 constants · interface

`ham/citybuildersim/ui/MiniMap.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

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
> WHEN IT IS DRAWN: whenever the map's version moves (MapTiles.version():
> a month's end, a purchase), and again when a district it drew without its
> picture gets one - so a few milliseconds a month, on the screen's thread,
> a far node at a time (MiniMapFrame.node()), the stamp's one node in one
> frame, the preview's six over as many frames as FRAME_MS allows, and only
> while it shows. Nothing slow is done here: the city's map drawn aside
> the first time (a draft, as the land office's map does, and never both at
> once: MapView.drafting), the nodes' ground and the pictures it lacks
> (CityMap.pictureJobs()) on MapView.WORKER, the map's one worker. No boats
> (Q12). On the real Earth it is the city's plane, never the globe.

**Uses:** [MiniMapFrame](MiniMapFrame.md) (16), [CityMap](CityMap.md) (12), [MapView](MapView.md) (10), [Palette](Palette.md) (8), [Game](Game.md) (7), [MapTiles](MapTiles.md) (3), [UserInterface](UserInterface.md) (2), [MapFrame](MapFrame.md) (2), [TileRaster](TileRaster.md) (2), [World](World.md) (1)

**Used by (1):** [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 127 | · what it draws from |
| 214 | A FRAME OF THE CLOCK |
| 418 | THE PREVIEW, ON HOVER |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 52 | `MiniMap.FRAME_MS` | `MapView.FRAME_MS` | The most a frame spends drawing the stamp and the preview, in ms: MapView.FRAME_MS - a node at least, so each finishes. |
| 55 | `MiniMap.PREVIEW_GAP` | `6` | The gap between the rail's edge and the preview: 6 px (the mockup's). |
| 58 | `MiniMap.PREVIEW_WORDS` | `"Click for the full map"` | The preview's line under the picture (the mockup's). |
| 61 | `MiniMap.STAMP_WORD` | `"Map"` | The stamp's word, under it as each rail button's is. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `private final UserInterface ui` |  |
| 64 | `private final VBox node` |  |
| 65 | `private final StackPane stampBox` |  |
| 66 | `private final Popup popup` |  |
| 67 | `private final VBox previewBox` |  |
| 71 | `final int w, h` |  |
| 72 | `final int[] px, buf` |  |
| 73 | `final WritableImage image` |  |
| 74 | `final ImageView view` |  |
| 75 | `MiniMapFrame.View at` |  |
| 76 | `byte[][] ground` |  |
| 77 | `boolean groundAsked` |  |
| 79 | `long drawnFor` | The map's version and pictures kept that the image shows, whether it was drawn short of a picture; and the pass under way: its next node, what it is of. |
| 80 | `boolean partial` |  |
| 81 | `int next` |  |
| 82 | `long passFor, passPictures` |  |
| 83 | `boolean passPartial` |  |
| 85 | `final Set<CityMap.District> wanted` | The built districts its last pass drew without the picture they would sample: asked of WORKER (askPictures()). |
| 124 | `private final Drawing stamp` |  |
| 129 | `private CityMap shown` |  |
| 130 | `private MapTiles tiles` |  |
| 131 | `private long version` |  |
| 132 | `private MiniMapFrame.Box frame` |  |
| 133 | `private Game.MapDraft draft` |  |
| 134 | `private int draftsFailed` |  |
| 136 | `private final Set<CityMap.District> asked` | The districts whose pictures' jobs are on WORKER. |
| 138 | `private final AnimationTimer pump` |  |
| 141 | `private boolean running` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 49 | 392 | **type** `final class MiniMap` | The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288 x 192; a click opens the land office's map over the window on what it... |
| 70 | 53 | **type** `private final class Drawing` | One of its two pictures: its view, its far nodes' ground (asked of WORKER), its pixels and image, and how far its pass has come. |
| 87 | 9 | `Drawing(int w, int h)` _(in MiniMap.Drawing)_ |  |
| 98 | 9 | `void aim(MiniMapFrame.View v)` _(in MiniMap.Drawing)_ | Aimed at a view: the same view keeps its ground; another asks for its own. |
| 108 | 9 | `void forget()` _(in MiniMap.Drawing)_ |  |
| 118 | 4 | `void blank()` _(in MiniMap.Drawing)_ |  |

### what it draws from (lines 127-213)

| line | len | member | says |
|---:|---:|---|---|
| 143 | 39 | `MiniMap(UserInterface ui)` |  |
| 184 | 4 | `private static String stampStyle(boolean hover)` | The stamp's frame: a pixel of the accent round it (the mockup's), the lighter accent under the pointer. |
| 190 | 1 | `Region node()` | The stamp and its word: what the rail lays above Menu. |
| 193 | 13 | `void forget()` | A new city or a load: everything drawn is another city's. |
| 207 | 6 | `private void start()` |  |

### A FRAME OF THE CLOCK (lines 214-417)

| line | len | member | says |
|---:|---:|---|---|
| 218 | 24 | `private void tick()` |  |
| 244 | 4 | `private static boolean inView(Node n)` | Whether a node and every parent of it are visible: the rail is hidden on the game's menus. |
| 257 | 40 | `private boolean keepUp(Game g)` | The minimap caught up with the city: its map drawn aside the first time - a draft on WORKER, unless the land office's map has one there already (MapView.drafting), when it waits for that - a new map (a load) thrown ou... |
| 299 | 8 | `private void kept(Game.MapDraft d)` | A draft drawn on WORKER, back on the screen's thread: kept as the city's map when it still is the city's. |
| 308 | 9 | `private void forgetDrawn()` |  |
| 326 | 32 | `private void draw(Drawing dr, long deadline)` | A picture drawn, or a node of it, until the deadline - one node at least: when the version moved, or a district it drew without its picture has one now (the map's count of pictures kept moved), its nodes again from th... |
| 360 | 14 | `private void askGround(Drawing dr)` | A view's far nodes' ground read on WORKER (the world is safe there), kept on the screen's thread while the view is still the one aimed at. |
| 384 | 33 | `private void askPictures(Drawing dr)` | The district pictures a drawing's last pass lacked: their plans' jobs (CityMap.pictureJobs()) handed to WORKER as one task, in order - as the expanded map's far nodes ask (MapView.askPictures()) - while its view's dis... |

### THE PREVIEW, ON HOVER (lines 418-440)

| line | len | member | says |
|---:|---:|---|---|
| 423 | 13 | `private void showPreview()` | The preview beside the rail, its foot level with the stamp's, while the pointer is on the stamp: shown, then moved by what its box measures on the screen (its shadow is the popup's too). |
| 437 | 3 | `private void hidePreview()` |  |

