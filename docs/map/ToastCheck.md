# ToastCheck.java - 388 lines · 16 methods · 7 constants · harnesses

`ham/citybuildersim/ToastCheck.java` - generated 2026-11-11 by CodeMap; line numbers are as of that run.

> A toast stays out of the page's way (TS1): while a toast shows, the page's foot takes a bottom padding as
> tall as the toasts, the scroll's pixel offset is kept as the toast comes and goes, and the last control of a
> page scrolled to its end is clear of the toast.
> 
> WHY THIS EXISTS. The toasts float over the page in the stage's bottom-right corner, and on the Build
> overview (1,067 px, scrolling) "Turn automatic building off" sits mid-page and went under the 33 px toast
> for about 50 px of scrolling (PT1, item 10). A bottom inset that is always there only adds scroll after the
> page, so the page gives the corner up only while a toast stands in it (ToastRoom); and a ScrollPane keeps
> the FRACTION of the way down, so a page that grows by a padding while the player is part way down it would
> jump unless the scroll is moved to the pixel it was at. The window cannot be used for it (it opens
> maximised, and the suite runs beside the player's game), so this lays the same nodes out on a scene of its
> own, as the window builds them (a scroller over a stack, the page's minimum height bound to the viewport's,
> the toasts a VBox in the bottom right with the window's margin), and reads where they land. Nothing in the
> model is involved. The FX toolkit must be up: it is skipped, with a note, where it cannot start.
> 
> What it has to prove:
> 
>   1. THE FIXTURE CAUSES THE CONDITION. With no room made (the page as it was), a page scrolled to its end
>      with a toast up has its last control under the toast's bounds, at one toast and at three.
>   2. THE LAST CONTROL IS CLEAR. With the room made, the same page scrolled to its end has its last control
>      clear of the toasts' bounds and inside the viewport, at one toast, at three, and at three with the
>      first one wrapped to three lines; the padding is the stack's height and its corner's margin.
>   3. THE PIXEL OFFSET IS KEPT. A page scrolled to a third and to two thirds of the way down, and to the top,
>      is at the same pixel offset (within a pixel) after a toast comes, after a second and a third come, and
>      after each of them goes; and the window is told the fraction the page ends at, which is what it
>      restores the player's place from after a rebuild.
>   4. IT IS GIVEN BACK. When the last toast goes the page's padding is its own again and the page is as
>      tall as it was; with two of three gone the padding is that of the one left.
>   5. A STACK THAT IS NOT ON SHOW GIVES NOTHING. Hidden (a game menu) or taken out of the window (cinematic
>      mode), the stack with its toasts in it takes no room; shown or put back, it does.
>   6. A PAGE THAT FITS IS NOT MOVED. A page shorter than the viewport is at the top with a toast up, and does
>      not grow past the viewport.
>   7. A TOAST THAT ARRIVES WITH THE REBUILD is fitted to the rebuilt page: the page emptied and drawn again
>      in one pulse with a toast added keeps the offset it was at, and its end is clear of the toast.
>   8. THE WINDOW BUILDS IT. UserInterface makes a ToastRoom of its own scroller, its page and its stack of
>      toasts, and keeps what it is told in the field the rebuild restores the player's place from.

**Uses:** [KeptCheck](KeptCheck.md) (19), [ToastRoom](ToastRoom.md) (2)

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `ToastCheck.WIDTH` | `1280, HEIGHT = 720` | The window the rig is laid out in: a laptop's. |
| 76 | `ToastCheck.TALL_ROWS` | `20` | The rows above the last control that make the page 1,067 px or so tall, like the Build overview. |
| 79 | `ToastCheck.PAGE_PADDING` | `new Insets(8, 0, 14, 0)` | The window's page padding (UserInterface.rootMenu) and toast margin, which the rig copies. |
| 80 | `ToastCheck.CORNER` | `new Insets(0, 24, 16, 0)` |  |
| 88 | `ToastCheck.WITHIN` | `2.0` | How far the pixel offset may stray as toasts come and go. |
| 213 | `ToastCheck.SHORT` | `"[!] Crime is running above Canada's"` |  |
| 214 | `ToastCheck.LONG` | `"[!] The harbour's grain stores are running below a month of what the city ea...` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 63 | `private static int fails` |  |
| 92 | `final ScrollPane scroller` |  |
| 93 | `final VBox page` |  |
| 94 | `final VBox stack` |  |
| 95 | `final StackPane root` |  |
| 96 | `final Scene scene` |  |
| 97 | `final List<Button> rows` |  |
| 98 | `final Button last` |  |
| 99 | `final ToastRoom room` |  |
| 101 | `double wrote` | The fraction of the way down the window was last told the page would be at (the field a rebuild restores it from). |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 61 | 328 | **type** `public class ToastCheck` | A toast stays out of the page's way (TS1): while a toast shows, the page's foot takes a bottom padding as tall as the toasts, the scroll's pixel offset is kept as the toast comes and goes, and the last control of a pa... |
| 65 | 4 | `static void check(String label, boolean ok, String detail)` |  |
| 70 | 1 | `static String px(double v)` |  |
| 91 | 88 | **type** `static final class Rig` | The window's page, scroller and toast stack on a scene of their own. |
| 103 | 19 | `Rig(boolean withRoom, int rowCount)` _(in ToastCheck.Rig)_ |  |
| 124 | 17 | `void fill(int rowCount)` _(in ToastCheck.Rig)_ | The page's rows, a full-width last control after them (the finding's button sat where a toast passed). |
| 143 | 6 | `void relayout()` _(in ToastCheck.Rig)_ | What the toolkit would do in a pulse: styles and layout, twice, since the scroll bar changes the width. |
| 151 | 7 | `Label toast(String text)` _(in ToastCheck.Rig)_ | A toast like the window's: bold 12.5 px text wrapped at the window's width, padded 7 10 7 10. |
| 160 | 4 | `double offset()` _(in ToastCheck.Rig)_ | The pixels the page is scrolled down by: the first row's distance above the viewport's top. |
| 166 | 6 | `boolean covered()` _(in ToastCheck.Rig)_ | Whether the last control and the toasts' stack overlap on the screen. |
| 173 | 1 | `double footOfLast()` _(in ToastCheck.Rig)_ |  |
| 175 | 1 | `double topOfStack()` _(in ToastCheck.Rig)_ |  |
| 177 | 1 | `double padding()` _(in ToastCheck.Rig)_ |  |
| 181 | 6 | `static void settle(Rig rig) throws Exception` | Everything the queue of the FX thread holds has run, and the scene is laid out: what a pulse does after a toast. |
| 189 | 7 | `static void scrollTo(Rig rig, double v) throws Exception` | Scrolled to a fraction of the way down, laid out. |
| 198 | 9 | `static Label arrive(Rig rig, String text) throws Exception` | A toast arrives: added to the stack on the FX thread, as the window does, then settled. |
| 208 | 4 | `static void leave(Rig rig, Label t) throws Exception` |  |
| 216 | 172 | `public static void main(String[] args) throws Exception` |  |

