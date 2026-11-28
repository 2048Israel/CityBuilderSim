# UX2 - the frame: the map as the ground, the top strip, the panel and the wide sheet

From UX1, the interface rework's study (`docs/queue/UX1-notes.md`, the mockups in
`docs/studies/ux1/mockups/`). Jerus, 2026-11-27: "having the map as the main focus and then just clicking
stuff to go deeper while the map still visible in the background ... its a different ui that takes into
account the map is still on the screen ... it still shows the current ui info, but more and with more
submenus".

## What it builds

The window's frame, and nothing inside the pages. Mockups 1 to 3 are the picture of it.

1. **The map is the ground.** The window's centre is always the map (MapView on the city's CityMap, as
   `openMapOn(MiniMapFrame.frame(...))` draws it today), and the pages lie over it. The Land office's Map
   sub-page and the rail's mini-map go; the map's legend folds to a button by the zoom (bottom right).
2. **The top strip** (mockup 1, item 1). Today's 84 px header of cards becomes one 52 px band: the clock and
   speed, the six figures (treasury, population, GDP, inflation, out of work, the rate), each a label in its
   area's colour, a figure, a short line, and its colour where it is bad; then building, rating, inbox, menu.
   Every figure keeps its click (it opens the page it opens today) and its tooltip.
3. **The panel** (P1 A, recommended). A rail button opens its page in a panel 560 px wide beside the rail,
   over the map; the map beside it keeps its place and zoom. The panel's top is the **path line**: a back
   arrow, the crumbs ("Sectors > Manufacturing > Balance sheet"), each a link to its level, then Wide and
   close. Back and Esc step up a level; the page under a crumb keeps its scroll when you come back
   (mockup 2). The same rail button again closes the panel.
4. **The wide sheet** (mockup 3). Wide lays the page over the dimmed map from the rail to the needs' edge;
   "Back to the panel" and Esc fold it. **Every page opens in the wide sheet unchanged in this batch:** today's
   page node, laid in the sheet at today's width, so no figure or control is lost or moved. The page rows
   (UX9 to UX13) then give each page its narrow form for the panel, page by page. Until a page has one, its
   rail button opens it wide.
5. **Keys:** the number keys 1 to 9 and 0 for the first ten rail buttons, Esc closes the sheet, then the
   panel, and Space pauses (the desk chat's list).
6. **What stays where it is:** the toasts' corner and the "Needs you" chip until UX3; the inbox until UX3; the
   Construction strip on the right becomes the strip's "building" chip.

## Files

`ui/UserInterface.java` (the window: its stage, header and rail, `goHome`, `redraw`), a new
`ui/Frame.java` for the panel, its path line and the wide sheet (so UserInterface does not grow), `ui/Kept.java`
only if a rule must name the panel, `ui/MapView.java` and `ui/MiniMapFrame.java` only to host the map as the
ground (no change to the map's own drawing), `ui/PagePictures.java` and `ui/SteadyBench.java` if the walk's
openers change. Read `docs/map/UserInterface.md` for the banners; never open a large screen whole.

## Size

Large: one Opus session, about 1,500 to 2,500 lines changed, most of it in UserInterface and the new Frame.

## The guard

- **UiSteadyCheck** on every page in the frame: no page moves, rebuilds or grows on an unchanged redraw,
  and a month keeps the scroller's pixel. Its known reds on main stay owned by their rows (UX7, UX9, UX10);
  a new red on any page is this batch's to mend. KeptCheck, ToastCheck, TimingCheck and every harness in
  AllChecks.DISPLAY stay green.
- **Pictures before and after**: `java -cp "target/classes;<the jars>" ham.citybuildersim.ui.PagePictures playtest
  2400 1 <scratch folder>` on main and on the branch (and a copied slot, as the tool's usage says); the notes
  keep three pairs (the city, a panel page, a wide page). The walk must still reach every page it reaches
  today: a page the walk loses is a red.
- The harness's walk is the definition of "every page": if the frame changes how a page opens, change
  `PagePictures.pages` with it, not the harness.

## Order and fit

First of the UX rows; it waits on nothing. CBK3 and ASH1 are in flight on BankScreen, CentralBankScreen,
ServicesScreen and BuildScreen; this batch does not touch those files' pages, only lays them in the sheet,
so it merges main as each ships. REU1's Real Estate page, if it lands after this, opens in the sheet like
every page and takes its narrow form by the page rows' rule. UX3 to UX8 start after it; the
page rows after it.

## The bottom console's centre (Jerus, 2026-11-27, 22:00-22:05, through the desk chat)

- **His picks.** He took all four of UX1's picks, every one A: P1, P2, P3 and P4. The rows as written stand.
- **His new piece:** "something could and should be in the bottom no? like something semi round occupying 60% of the bottom", then "perhaps the build, for quick access, or the debt?". Its row is UX14.
- **What moves into this batch.** The clock moves out of item 2's top strip, because where the clock sits is the frame's call. This is the ★. He asked "but like top bar still right?": yes, the strip stays. His word on the clock is still to come through the desk chat: build to the ★ unless he says top.
  - The console is a low, semi-round band at the bottom centre, over the map: about 60% of the width and 90-110 px tall. A click folds it down to its centre.
  - Its centre is the clock: the date, play and pause, the speed, and a ring for the month's progress.
  - The top strip keeps the six figures, building, the rating, the inbox and the menu. The alert row (UX3) sits under it.
- **What this batch builds.** The console's shell and its centre only. The wings are UX14's, after UX5 and UX7.
- **The guard.** UiSteadyCheck covers the console:
  - it never covers a panel's foot or the zoom buttons;
  - folding and unfolding it keeps the map's place and zoom.

## Not this batch's

The alerts (UX3), the needs (UX4), the map's card and tints (UX5), the icons (UX6), the build screen (UX7),
the figures' format (UX8), any page's own layout (UX9 to UX13), the map's own drawing, and the manual.
