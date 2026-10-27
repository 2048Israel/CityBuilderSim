# IconCheck.java - 121 lines · 4 methods · 0 constants · harnesses

`ham/citybuildersim/IconCheck.java` - generated 2026-10-27 by CodeMap; line numbers are as of that run.

> The game's icon is in the jar at every size it is given, and the exe's .ico carries the sizes it
> should (BR1).
> 
> WHY THIS EXISTS. The window's icon (ui/AppIcon) is loaded from the classpath and a size that is
> missing is only a log line, never an error, so a regeneration by tools/icon/make_icons.py that
> dropped a size, or a file left out of the jar, would go unseen until someone looked at a taskbar.
> The .ico is read by jpackage alone (Build EXE.bat), which no harness runs. No JavaFX is needed:
> the files are read as bytes.
> 
> What it has to prove:
> 
>   1. EVERY PNG IS THERE, AT ITS SIZE. Each of AppIcon.SIZES is on the classpath as a PNG whose
>      header gives that width and height, with an alpha channel (the tile's corners are round).
>   2. THE .ICO CARRIES ITS SIZES. icon.ico is an icon file listing exactly AppIcon.ICO_SIZES, each
>      entry a PNG whose own header agrees with the directory, inside the file.
>   3. THE EXE IS BUILT WITH IT, AND THE DRAWING IS KEPT. Build EXE.bat passes --icon with a file
>      that exists, and the two SVG masters are beside the PNGs.

**Uses:** [AppIcon](AppIcon.md) (10)

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private static int fails` |  |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 88 | **type** `public class IconCheck` | The game's icon is in the jar at every size it is given, and the exe's .ico carries the sizes it should (BR1). |
| 38 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 43 | 7 | `static byte[] resource(String name)` |  |
| 52 | 8 | `static int[] pngHeader(byte[] b, int at)` | {width, height, colour type} from a PNG's header, or null if the bytes are not a PNG. |
| 61 | 60 | `public static void main(String[] args) throws Exception` |  |

