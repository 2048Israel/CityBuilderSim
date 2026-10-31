# TS1 - the toast stays out of the way

Queued 2026-10-31: Jerus ticked "Toast stays out of the way", PT1-C's way (a).

## The finding (PT1-notes, item 10)

The toast in the bottom-right corner passes over a page's controls: on the
Build overview (1,067 px, scrolling) "Turn automatic building off" sits mid-page
and passes under the 33 px toast for about 50 px of scrolling
(`PT1/build-overview-toast-covers-button.png`). A bottom inset alone only adds
scroll after it.

## What it does

While a toast shows, the page gives up the corner: the page's scroller takes a
bottom padding as tall as the toasts, keeping its pixel offset so nothing
jumps, and gives it back when the last toast goes. A harness holds that a toast
never covers the page's viewport (a page scrolled to its end with a toast up:
its last control clear of the toast's bounds) and that the scroll's pixel
offset is unchanged as a toast comes and goes. The toast still fades in 8 s and
goes when clicked. Screens only; the trace does not move.
