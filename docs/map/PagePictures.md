# PagePictures.java - 362 lines · 17 methods · 3 constants · interface

`ham/citybuildersim/ui/PagePictures.java` - generated 2026-11-27 by CodeMap; line numbers are as of that run.

> The picture tool (UX1): a copied city's every rail page and sub-page as the real window draws it at
> 1920x1080, one PNG each, for the interface rework's study and every UI batch's before-and-after.
> 
> <p>WHY. Until UX1 a picture of a page meant a player at the screen, or a scratch driver written for one
> screen (the desk chat's CbShot, the central bank's places) and thrown away. The rework changes every page,
> so each batch of it needs the same pages of the same cities drawn before and after, by one walk of the
> rail that a harness can share: {@link #pages} is that walk, and UiSteadyCheck redraws the same list.
> 
> <p>USAGE (Git Bash, from the repository root, after a compile):
> <pre>
>   java -Dprism.order=sw -cp "target/classes;$(cat target/classpath.txt)" \
>        -Dcbs.earth=C:/.../data/earth-2.bin -Dcbs.world=C:/.../data/world-1.bin \
>        ham.citybuildersim.ui.PagePictures FOLDER SLOT MONTHS OUT [PREFIX...]
> </pre>
> FOLDER "playtest" grows the playtest's city to month SLOT instead (openPlaytest), then plays MONTHS more.
> Otherwise FOLDER holds a copy of slot-NN.json, slot-NN-history.json and slot-NN-map.bin (copied there by hand, never
> the saves folder itself); the tool copies them again into a {@link GameFiles#scratch} folder and loads that,
> so neither the player's slots nor FOLDER is written. It plays MONTHS months (0 for the city as saved),
> then writes OUT/NN-page.png for every page of {@link #pages}, and OUT/NN-page-whole.png beside it where the
> page's own scroller holds more than the window shows. PREFIX limits the walk to pages whose names start with
> one of them ("bank", "finances-"). -Dpictures.sector=SimpleName picks the sector whose books are drawn
> (else the one with the most workers). Needs a display, or the gate box's Monocle (FollowBench.startJavaFx).

**Uses:** [PolicyScreen](PolicyScreen.md) (11), [UserInterface](UserInterface.md) (7), [GameFiles](GameFiles.md) (5), [Game](Game.md) (4), [KeptBench](KeptBench.md) (4), [Sector](Sector.md) (4), [MiniMapFrame](MiniMapFrame.md) (2), [FollowBench](FollowBench.md) (2), [LongPlaytest](LongPlaytest.md) (2), [GamePrefs](GamePrefs.md) (2), [InfrastructureScreen](InfrastructureScreen.md) (1), [SectorScreen](SectorScreen.md) (1), [GovernmentScreen](GovernmentScreen.md) (1), [FinancesScreen](FinancesScreen.md) (1), [CentralBankScreen](CentralBankScreen.md) (1), [TradeScreen](TradeScreen.md) (1), [ZoningScreen](ZoningScreen.md) (1), [ResearchScreen](ResearchScreen.md) (1)

**Used by (1):** [SteadyBench](SteadyBench.md)

## Sections

| line | section |
|---:|---|
| 181 | · the run |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 61 | `PagePictures.WIDTH` | `1920, HEIGHT = 1080` | The window's size every picture is drawn at: the full-HD screen most players have (UX1's brief). |
| 64 | `PagePictures.SETTLE_MS` | `400` | How long a page is left after it opens before it is drawn: the layout's pulses, and any fade (ms). |
| 67 | `PagePictures.MAP_SETTLE_MS` | `4000` | ...and a page with the map on it: MapView paints its tiles a few a frame, from the middle out (ms). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 183 | `static Game g` |  |
| 184 | `static UserInterface ui` |  |
| 185 | `static Stage stage` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 56 | 307 | **type** `public final class PagePictures` | The picture tool (UX1): a copied city's every rail page and sub-page as the real window draws it at 1920x1080, one PNG each, for the interface rework's study and every UI batch's before-and-after. |
| 58 | 1 | `private PagePictures()` |  |
| 70 | 1 | **type** `public record Page(String name, Runnable open)` | One page of the walk: its file name and the hand that opens it, run on the FX thread. |
| 77 | 71 | `public static List<Page> pages(UserInterface ui, Game g)` | Every rail page and its sub-pages, in rail order: the city as it lands, the map over the window, then each rail tab's home and the sub-pages its chips open. |
| 150 | 11 | `static String[] policyPages(String area)` | A Policy area's pages, in its chip strip's order. |
| 163 | 12 | `static Sector booksSector(Game g)` | The sector whose books are drawn: -Dpictures.sector's, else the one with the most workers. |
| 177 | 3 | `static String slug(String s)` | A page's words as a file name: "Home & abroad" is "home-abroad". |

### the run (lines 181-362)

| line | len | member | says |
|---:|---:|---|---|
| 187 | 3 | `static<T> T fx(Callable<T> c) throws Exception` |  |
| 192 | 5 | `static void call(UserInterface on, String name) throws Exception` | A private method of the window run, as UiProbe runs anotherCity and openCity. |
| 198 | 5 | `static Scene scene(UserInterface on) throws Exception` |  |
| 208 | 16 | `static void openCopy(Path folder, int slot) throws Exception` | A copy of a slot loaded from a scratch folder and shown in the real window, at 1920x1080, not full-screen: the city and the window every picture and UiSteadyCheck's walk are drawn on. |
| 230 | 14 | `static void openPlaytest(int toMonth) throws Exception` | ...or, with FOLDER "playtest", the playtest's city (LongPlaytest.probeCity, the city UiProbe and the nightly grow) played to a month: a mid or a big city when no slot of today's format holds one (UX1: his slots 2 and ... |
| 246 | 6 | `private static void prefsFor(GameFiles files)` | The window reads its preferences from the city's folder: not full-screen. |
| 254 | 15 | `private static void openWindow() throws Exception` | The game's own window on the city {@link #g}, at 1920x1080. |
| 271 | 14 | `static void shoot(String name, File dir) throws Exception` | The window as it shows now, as a PNG; and, where the page's own scroller holds more, its whole content. |
| 287 | 12 | `static ScrollPane pageScroller(Node root)` | The largest scroller on show: the page's own. |
| 300 | 4 | `static void collect(Node n, List<ScrollPane> out)` |  |
| 305 | 7 | `static void png(WritableImage img, File file) throws Exception` |  |
| 313 | 49 | `public static void main(String[] args) throws Exception` |  |

