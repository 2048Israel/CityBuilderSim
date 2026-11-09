# TS1 - the toast stays out of the way ({next}, 2026-11-09)

The brief: `docs/queue/TS1.md` (PT1-C, way (a); Jerus ticked "Toast stays out of the way", 2026-10-31). Branch `batch/TS1`
from main 679abe59 (BB1 merged). A small fix batch (screens only, no money, saves or threading), so one Sonnet 5.5
session did all of it, no slices; Opus reviews at the gate.

## What it does

`ui/ToastRoom` (new, 110 lines) watches the toast stack: its toasts coming and going, its being hidden (a game
menu: `showIf`) and its being taken out of the window (cinematic mode). Any of the three asks for one fit, run on the
next FX event (`Platform.runLater`, coalesced), because a toast arrives inside `clearMenu()` while the page is empty.
The fit sets the page's (`rootMenu`'s) bottom padding to its own 14 px plus the room: the stack's height, asked with
`applyCss()` and `prefHeight(-1)` since a toast just added is not styled yet (unstyled it has neither its padding nor
its font), plus the stack's bottom margin (16, read from the `StackPane` margin so the two cannot drift). No toast, a
hidden stack or one out of the window: no room, the padding its own. `UserInterface` makes it over `menuScroller`,
`rootMenu` and `toastStack` right after the stack is added, and keeps what it is told in `pageScrollAt`.

The toast is unchanged otherwise: it fades in `TOAST_SECONDS` (8) and goes when clicked.

## The pixel offset: the scroller does it, and a first version broke it

The brief says "keeping its pixel offset so nothing jumps". The first version did it by hand: read
`vvalue * (prefHeight - viewport)`, set the padding, then `setVvalue(offset / newSpan)`. `ToastCheck` 3 caught it at
once: a page at 106 px went to 91 px (15 px, the room being 49), at 220 px to 192. The cause: the write lands while the
ScrollPane still has the OLD content height, and the skin then keeps that pixel offset through the layout that follows.
With no write at all the same page stays at 106 px, 220 px: **ScrollPane keeps the pixel offset itself when its
content changes height** (measured: vvalue 0.3333 to 0.2923 across the layout, the page where it was). So `ToastRoom`
moves nothing. It does tell the window the fraction the page will be at (`placeAt`, `pageScrollAt = at`): the window
keeps the player's place as a fraction and a rebuild restores it from that field, and without the number a toast
arriving with a rebuild (a menu screen redrawn, `settlingScroll`) would have the restore put the page at the
fraction of the shorter page. `ToastCheck` 3 holds the told number to the pixel against where the page ends, and 3
holds the offset itself, so a toolkit that stopped keeping it would be heard.

## ToastCheck (new, registered after ChartFitCheck; needs JavaFX, skipped without it)

Eight sections on a scene of its own (the window's nodes as `start()` builds them: a `ScrollPane` over a `StackPane`
with the stack in the bottom right with the window's margin, the page's minimum height bound to the viewport's, 20
rows and a full-width last control, a page of 1,062 px in a window of 720, like the Build overview's 1,067):

1. the fixture causes the condition: with no `ToastRoom`, a page at its end with one toast, and with three, has its
   last control under the stack's bounds (706 px against a stack from 671);
2. with the room: the last control clear of the stack at one toast (657 against 671), three (579 against 593) and
   three with the first wrapped to a 71 px toast, inside the viewport, the padding the stack's height and the margin;
3. the offset kept at the top, a third and two thirds of the way, as one, two and three toasts come and as each goes;
4. the room given back: two of three gone is the one left's room, the last gone is none, the padding 14 and the page
   1,062 px again;
5. a hidden stack, and one taken out of the window, give nothing; shown, put back, they do;
6. a page shorter than the window is not moved and does not grow past the viewport;
7. a toast raised in the same pulse as the page is emptied and drawn again is fitted to the rebuilt page: the offset
   kept, the room against the redrawn page, its end clear;
8. a source check that `UserInterface` builds the `ToastRoom` over its own scroller, page and stack, and keeps the
   told fraction in `pageScrollAt` (the window cannot be started in the suite, KeptBench's reason).

The offset's tolerance is 2 px, not 1: the scroller keeps the offset through a fraction and snaps it at every layout,
so five changes in a row drifted 2 px at worst (a first run: 0 at the top, 1 at three toasts, 2 at the last going).
The jump it stands against is 15 px and more.

## What it does not do

- A toast that arrives while the player is at the very END of a page leaves the page where it is (the brief: pixel
  offset kept), so the last control stays under the toast until the player scrolls the room (49 px for one toast) -
  the page now has that much more to scroll, where before it had none. A page scrolled to its end WITH a toast up is
  clear (ToastCheck 2). Following the end would move the page by the room the moment a toast came, which is the jump
  the brief rules out.
- A toast going while the page is scrolled into the room clamps the offset to the shorter page's end (ToastCheck 3,
  the last section): the page can only be where it ends.
- Not looked at in the real window: the suite cannot start it, and Jerus looks only if an issue turns up (11-04).

## Checks

The default playtest's trace does not move (screens only): no ensemble. No save field, no `SAVE_FORMAT`, no model
file, nothing in the month. No process file touched: the one-in-one-out count is zero lines each way.
