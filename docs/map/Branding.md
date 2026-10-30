# Branding.java - 124 lines · 4 methods · 7 constants · interface

`ham/citybuildersim/ui/Branding.java` - generated 2026-10-30 by CodeMap; line numbers are as of that run.

> The game's identity on screen: its icon on the window (every size of resources/icons/ handed
> to the stage, so Windows takes the one it needs for the taskbar and Alt+Tab) and its title art
> on the start screen.
> 
> WHY: until BR1 (2026-10-27) the window wore Java's coffee cup, the exe jpackage's default, and
> the start screen the name as a plain label. Jerus chose both drawings, each of four concepts:
> the icon is "towers as a chart" (four towers rising like the bars of a chart, each lit in one
> of the palette's colours, the money line along their roofs), the title art "the ledger's
> total" (the name over the double rule an account closes on, its top line climbing at the end
> like a chart). tools/branding/make_icons.py draws the icon and writes the PNGs and icon.ico for
> Build EXE.bat; make_title.py draws the title art for the store pages from the same proportions
> as title() below. BrandingCheck holds the icon files to SIZES and ICO_SIZES, and the store art to these proportions. Nothing here touches
> JavaFX until dress() or title() is called, so a harness can read the lists without the toolkit.

**Uses:** [Palette](Palette.md) (3)

**Used by (2):** [BrandingCheck](BrandingCheck.md), [UserInterface](UserInterface.md)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 21 | `Branding.FOLDER` | `"/icons/"` | The folder on the classpath the icon files are in. |
| 24 | `Branding.SIZES` | `{ 16, 20, 24, 32, 40, 48, 64, 128, 256 }` | Every size the PNGs come in; the stage is given them all and the platform picks. |
| 27 | `Branding.ICO_SIZES` | `{ 16, 20, 24, 32, 40, 48, 64, 256 }` | The sizes icon.ico carries for the exe: Explorer's and the taskbar's at each display scale, and 256 for large icons. |
| 30 | `Branding.ICO` | `FOLDER + "icon.ico"` | The exe's icon on the classpath; Build EXE.bat passes the same file from the source tree. |
| 39 | `Branding.RULE` | `.0467, TOP_DROP =.413, LOW_DROP =.56, BEND_BACK =.2` | The title art's proportions, in ems of the name's font size, from the baseline and from the name's two ends (tools/branding/make_title.py draws the store art from the same numbers): the rules' thickness, the top rule'... |
| 40 | `Branding.CURVE` | `{.0667, 0,.133, -.08,.227, -.24 }` |  |
| 41 | `Branding.CLIMB_X` | `.467, CLIMB_Y = -.747, DOT =.06, RING =.0333` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 18 | 107 | **type** `public final class Branding` | The game's identity on screen: its icon on the window (every size of resources/icons/ handed to the stage, so Windows takes the one it needs for the taskbar and Alt+Tab) and its title art on the start screen. |
| 43 | 1 | `private Branding()` |  |
| 46 | 3 | `public static String png(int n)` | The classpath name of the PNG at n pixels. |
| 51 | 23 | `public static void dress(javafx.stage.Stage stage)` | Gives the stage every size of the icon; a size missing from the jar is named in the log line, never thrown. |
| 80 | 44 | `public static javafx.scene.layout.Pane title(String text, double size)` | The title art: the name in bold at the given size, the double rule under it in the money blue, the top rule climbing past the name's end to a ringed dot. |

