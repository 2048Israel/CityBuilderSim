## changelog
### {next} — TS1, the toast stays out of the way — 2026-11-09, ready, see docs/queue/TS1-notes.md
While a toast shows, the page gives up the corner it stands in: the page's foot takes a bottom padding as tall as the
toasts and the stack's margin under them, so a page scrolled to its end has its last control clear of the toast (the
Build overview's "Turn automatic building off" went under the 33 px toast for about 50 px of scrolling; PT1, item 10,
Jerus ticked "Toast stays out of the way", 2026-10-31). The scroll keeps its pixel offset as a toast comes and goes (the
ScrollPane does it itself; a first version that moved it by hand put the page 15 px out, and ToastCheck caught it), and
the room goes back when the last toast does, when the stack is hidden on a game menu and when cinematic mode takes it
out of the window. The toast still fades in 8 s and goes when clicked. New `ui/ToastRoom` and `ToastCheck` (JavaFX,
the window's nodes on a scene of its own: the last control under the toast without the room and clear with it at one
toast, three and a wrapped one; the offset held; the room given back; a rebuild with a toast). Screens only, so the
default trace cannot move: no ensemble.
