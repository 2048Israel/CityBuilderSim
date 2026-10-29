# LandClassRenders.java - 175 lines · 6 methods · 0 constants · tools

`ham/citybuildersim/tools/LandClassRenders.java` - generated 2026-10-29 by CodeMap; line numbers are as of that run.

> Renders what ET3 says about the classes of ground, for review: the land office's prices and offers by class and note 11
> of the sector statements, in the words the screens use (LandClassView), for cities played on the real Earth.
> 
>     java -Dcbs.earth=<earth-2.bin> -cp "target/classes;<gson.jar>" ham.citybuildersim.tools.LandClassRenders [earth-2.bin [outDir [name...]]]
> 
> The file is the argument, else -Dcbs.earth, else data/earth-2.bin (EarthData.locate()); outDir is docs/queue/ET3
> unless named; names (render-a, render-b, render-c) pick which to write, all three otherwise. -Dlandclass.months=N plays
> Medellin and Rotterdam N months (2,000 unless named, SlopeRenders' length, at which the land office's offers have
> brought a city in the hills steep ground) and -Dlandclass.early=N plays the early Medellin render A also shows
> (120, when the going price is low and foothill sits at the base). A city is founded as the playtest founds one
> (LongPlaytest.earthCity(): a GameFiles.scratch() folder deleted when the JVM exits - never the real saves). It writes:
> 
>   et3-a-land-office.txt   LandClassView.officeLines() and every offer on the shelf's offerLine(), Medellin at the later
>                           month and at the early one
>   et3-b-note-11.txt       note 11 (LandClassView.landNoteLines()) for each sector of Medellin that holds any land by class,
>                           with the sheet's Land line it adds up to, and says whether the run has a works sector among them
>                           (the playtest builds no mine on the steep ground); then the same for LandClassCheck.hillyCity(), a
>                           fixture of houses on flat and foothill and iron mines on steep ground, which has one
>   et3-c-rotterdam.txt     the same lines for Rotterdam's city, which has no foothill and no steep ground: no class line
>                           in the office, no note 11
> 
> WHY (ET3 slice C): the screens are checked by eye, and the words are written in one place so that they can be read
> here without JavaFX. Not a harness: nothing here asserts (LandClassCheck 7 does), and nothing under docs/ but its
> outDir is written.

**Uses:** [LandManager](LandManager.md) (12), [LandClassView](LandClassView.md) (9), [Game](Game.md) (7), [SectorStatements](SectorStatements.md) (4), [EarthData](EarthData.md) (3), [LandParcel](LandParcel.md) (2), [SectorBooks](SectorBooks.md) (2), [LandClassCheck](LandClassCheck.md) (1), [LongPlaytest](LongPlaytest.md) (1), [GameFiles](GameFiles.md) (1), [LandMarket](LandMarket.md) (1), [BuildingManager](BuildingManager.md) (1), [Sector](Sector.md) (1)

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 52 | 124 | **type** `public final class LandClassRenders` | Renders what ET3 says about the classes of ground, for review: the land office's prices and offers by class and note 11 of the sector statements, in the words the screens use (LandClassView), for cities played on the ... |
| 54 | 1 | `private LandClassRenders()` |  |
| 56 | 49 | `public static void main(String[] args) throws IOException` |  |
| 107 | 3 | `private static void writeLf(Path file, List<String> text) throws IOException` | Writes a render's lines with LF endings, as the tree stores text (Files.write() would use the PC's CRLF). |
| 111 | 9 | `private static Game play(String spot, int months, PrintStream silent)` |  |
| 122 | 24 | `private static void office(List<String> text, Game g, String title)` | A city's ground, the office's prices by class and every offer on the shelf with its ground. |
| 148 | 27 | `private static void notes(List<String> text, Game g, String title)` | Note 11 for every sector that holds any land by class, services and works, with the sheet's Land line it adds up to. |

