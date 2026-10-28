# BrandingCheck.java - 168 lines · 5 methods · 0 constants · harnesses

`ham/citybuildersim/BrandingCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The game's icon is in the jar at every size it is given, the exe's .ico carries the sizes it
> should, and the store's title art is drawn to the start screen's proportions (BR1).
> 
> WHY THIS EXISTS. The window's icon (ui/Branding.dress) is loaded from the classpath and a size
> that is missing is only a log line, never an error, so a regeneration by
> tools/branding/make_icons.py that dropped a size, or a file left out of the jar, would go unseen
> until someone looked at a taskbar. The .ico is read by jpackage alone (Build EXE.bat), which no
> harness runs. And the title art is drawn twice, by the start screen in JavaFX (Branding.title)
> and for the store pages by tools/branding/make_title.py, from proportions each keeps: a change
> to one would leave the store's logo unlike the game's. No JavaFX is needed: files are read as
> bytes and text.
> 
> What it has to prove:
> 
>   1. EVERY PNG IS THERE, AT ITS SIZE. Each of Branding.SIZES is on the classpath as a PNG whose
>      header gives that width and height, with an alpha channel (the tile's corners are round).
>   2. THE .ICO CARRIES ITS SIZES. icon.ico is an icon file listing exactly Branding.ICO_SIZES,
>      each entry a PNG whose own header agrees with the directory, inside the file.
>   3. THE EXE IS BUILT WITH IT, AND THE DRAWING IS KEPT. Build EXE.bat passes --icon with a file
>      that exists, and the two SVG masters are beside the PNGs.
>   4. THE TITLE ART IS THE START SCREEN'S. make_title.py's proportions are Branding's, number for
>      number, and branding/ holds the title SVG and its PNGs. Run from the repository only (it
>      reads files there); elsewhere the section says so and is skipped.

**Uses:** [Branding](Branding.md) (19)

## Fields (state)

| line | field | says |
|---:|---|---|
| 44 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 42 | 127 | **type** `public class BrandingCheck` | The game's icon is in the jar at every size it is given, the exe's .ico carries the sizes it should, and the store's title art is drawn to the start screen's proportions (BR1). |
| 46 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 51 | 7 | `static byte[] resource(String name)` |  |
| 60 | 8 | `static int[] pngHeader(byte[] b, int at)` | {width, height, colour type} from a PNG's header, or null if the bytes are not a PNG. |
| 70 | 5 | `static double[] pythonNumbers(String source, String names)` | The numbers a Python line "A, B = 1, 2" or "C = [1, 2]" assigns, in order, or null if the line is not there. |
| 76 | 92 | `public static void main(String[] args) throws Exception` |  |

