# AppIcon.java - 59 lines · 3 methods · 4 constants · interface

`ham/citybuildersim/ui/AppIcon.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The game's icon on its window: the PNGs in resources/icons/, every size handed to the stage, so
> Windows takes the one it needs for the title bar, the taskbar and Alt+Tab.
> 
> WHY: until BR1 (2026-10-27) the window wore Java's coffee cup and the exe jpackage's default.
> The drawing is Jerus's pick of four, "towers as a chart": four towers rising like the bars of a
> chart, each lit in one of the palette's colours, the money line along their roofs.
> tools/icon/make_icons.py draws it, renders it at SIZES and packs ICO_SIZES of them into icon.ico
> for Build EXE.bat; IconCheck holds the files to these lists. Nothing here touches JavaFX until
> dress() is called, so a harness can read the lists without the toolkit.

**Used by (2):** [IconCheck](IconCheck.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 17 | `AppIcon.FOLDER` | `"/icons/"` | The folder on the classpath the icon files are in. |
| 20 | `AppIcon.SIZES` | `{ 16, 20, 24, 32, 40, 48, 64, 128, 256 }` | Every size the PNGs come in; the stage is given them all and the platform picks. |
| 23 | `AppIcon.ICO_SIZES` | `{ 16, 20, 24, 32, 40, 48, 64, 256 }` | The sizes icon.ico carries for the exe: Explorer's and the taskbar's at each display scale, and 256 for large icons. |
| 26 | `AppIcon.ICO` | `FOLDER + "icon.ico"` | The exe's icon on the classpath; Build EXE.bat passes the same file from the source tree. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 14 | 46 | **type** `public final class AppIcon` | The game's icon on its window: the PNGs in resources/icons/, every size handed to the stage, so Windows takes the one it needs for the title bar, the taskbar and Alt+Tab. |
| 28 | 1 | `private AppIcon()` |  |
| 31 | 3 | `public static String png(int n)` | The classpath name of the PNG at n pixels. |
| 36 | 23 | `public static void dress(javafx.stage.Stage stage)` | Gives the stage every size of the icon; a size missing from the jar is named in the log line, never thrown. |

