# ToastRoom.java - 110 lines · 5 methods · 0 constants · interface

`ham/citybuildersim/ui/ToastRoom.java` - generated 2026-11-09 by CodeMap; line numbers are as of that run.

> The corner a toast stands in, given up by the page (TS1): while a toast shows, the page's foot takes a
> bottom padding as tall as the toasts and the corner they are set in, its pixel offset kept, and gives it
> back when the last toast goes.
> 
> WHY THIS EXISTS. The toasts float over the page in the stage's bottom right (UserInterface, TOASTS), and a
> control can pass under one: on the Build overview (1,067 px, scrolling) "Turn automatic building off" sits
> mid-page and went under the 33 px toast for about 50 px of scrolling (PT1, item 10,
> PT1/build-overview-toast-covers-button.png). A padding that is always there only adds scroll after the
> page, so it is there only while a toast is; and a page that grows by a padding while the player is part
> way down it must not jump, so the scroll is moved to the pixel it was at (a ScrollPane keeps the
> FRACTION of the way down, and a longer page puts that fraction further on). Screens only: nothing in the
> model reads this.
> 
> <p>What it watches is the stack itself: its toasts coming and going, its being hidden (a game menu) and
> its being taken out of the window (cinematic mode). Any of the three asks for a fit, and the fit is made
> once, after what asked has finished - a toast arrives inside clearMenu(), while the page is emptied, and
> measured then the page would be nothing. The fit asks the page what it WANTS to be, as the wheel does
> (UserInterface.pageSpan: prefHeight needs no layout), before and after the padding changes, so it is right
> on the first frame and does not wait for a layout pass.

**Used by (2):** [ToastCheck](ToastCheck.md), [UserInterface](UserInterface.md)

## Fields (state)

| line | field | says |
|---:|---|---|
| 36 | `private final ScrollPane scroller` |  |
| 37 | `private final Region page` |  |
| 38 | `private final Pane stack` |  |
| 39 | `private final DoubleConsumer moveTo` |  |
| 40 | `private final Insets base` |  |
| 43 | `private double room` | What the page's foot gives now, in pixels. |
| 46 | `private boolean asked` | True from a toast coming or going until the fit it asked for has run. |

## Methods, in file order

| line | len | member | says |
|---:|---:|---|---|
| 34 | 77 | **type** `public final class ToastRoom` | The corner a toast stands in, given up by the page (TS1): while a toast shows, the page's foot takes a bottom padding as tall as the toasts and the corner they are set in, its pixel offset kept, and gives it back when... |
| 55 | 11 | `public ToastRoom(ScrollPane scroller, Region page, Pane stack, DoubleConsumer moveTo)` | (the window keeps that number as the player's place, so it must hear this one) |
| 68 | 3 | `public double room()` | What the page's foot gives now: the toasts' height and the corner's, or nothing while no toast is on show. |
| 73 | 7 | `double wanted()` | What it should give: the stack's height (asked, not measured) and the margin under it, or nothing. |
| 82 | 5 | `private void ask()` | A toast came or went, or the stack was shown, hidden or moved: fit once, when what is in flight has finished. |
| 92 | 18 | `public void fit()` | Give the page the room the toasts want, and put the scroll back to the pixel it was at. |

