# The screens kept, not rebuilt every month: a design study (UP4)

*2026-10-23. Jerus, 2026-10-22, asked whether the screens should be updated in place instead of
torn down and rebuilt each month, which means rewriting the twelve rail screens, and answered
"Study it now". The brief is `docs/queue/UP4.md`; the plan it came from is the merge
"plan/ui-perf" (UP1-UP3). This study measured every rail screen at month 4,000, chose one way to
write a kept screen, wrote it (`ui/Kept.java`), moved the People page onto it as the pilot, and
plans the rest.*

*What was read: `UserInterface`'s clock, `clearMenu()`, `restoreScroll()` and the chrome's refresh
methods; `PeopleScreen` whole (its People page); the `Pieces` helpers and pictures that page
reaches; `TimeChart.setData()`; UP1's probe and its output (`docs/queue/UP1/`); `ui/UiProbe` as
UP1 shipped it (0.8.25). The code was measured on the PC (i7-13650HX, JDK 21.0.4, JavaFX 21) at
main `015a4ed4` (0.8.24) and `d6166c7d` (0.8.25), in LongPlaytest's city grown by its own loop
(`LongPlaytest.probeCity`/`probePlay`, 560,230 people and 24,592 buildings at month 4,005).*

*The probes are beside this note, in `screens-in-place/probe/`, never in `src/`: `InPlaceProbe`
(each screen's redraw, its census, the patch estimate, the build's split and the clock at 50x),
`TreePatch` (the estimate's patch), `ChromeProbe`, `ReaddProbe`, `RenderProbe` and `PilotProbe`.
Their output is in `screens-in-place/out/`; how to run them is at the end (§9).*

---

## 0. In one page

**A redraw is mostly new nodes being made into a page.** Every redraw empties the page and
builds it again, chrome and all. A node new to the scene pays four times: it is made (and given
its style helpers as it is added: CSS selector matching happens *inside* `getChildren().add()`),
styled in the pulse's CSS pass, laid out with its text measured, and given a peer for the
renderer. At month 4,000 a redraw costs 29-68 ms on the FX thread (§1); the chrome - header,
rail, city panel, construction panel, inbox - is 12-27 ms of it on every screen, the page the
rest. Month to month, **90-99% of a page's nodes show exactly what they showed** (§2): People
changes 64 words and 56 other values on 1,182 nodes, Bank 49 on 823, Finances 6 on 380.

**The pattern: the page's own code, run over last month's nodes (§3).** A kept screen draws
through `Kept`: every node its code makes comes through `Kept.label(...)`, `Kept.column(...)`,
`Kept.of(Mirror.class, Mirror::new)`, which hand back the node of that class the same scope made
last time, in order. A box's children are staged during the draw and set in one call at its end,
only where they differ, so a node left in place never leaves the scene and never pays CSS again.
Rows that come and go are a scope each, keyed by what they show. There is one code path - the
first draw and every month after run the same code - so the update cannot drift from the build;
`KeptCheck` holds it by drawing the page kept and new at every month and comparing them node by
node and pixel by pixel (§6). The two alternatives were measured and set aside: a second,
hand-written update method per screen (two paths, 46,000 lines written twice) and a generic patch
of a new page onto the old (its handlers hold the new page's nodes, §3.4).

**The pilot: the People page, kept (§4).** Its renders before and after are the same pixels at
months 1,000 and 4,000, at its first draw and after eight redraws on the clock. Measured in one
JVM at month 4,101, turn and turn about with the same page rebuilt as before: **its redraw went
from 53.0 to 32.1 ms, the page's own part from 34.7 to 12.8** (the rest is the chrome, unchanged
and UP2's); its CSS and settle are gone. On the clock at 50x the FX thread went from 50% busy to
41%, and the 95th-percentile frame from 51 ms to 20. What is left on People at 50x is mostly its
count-up animation, which never stops at that speed: held off, the thread is 30-35% busy (★4).

**The rest (§5, §7): seven batches, KP1 to KP7**, after UP2 takes the chrome onto `Kept`
(UP2's item 4, made unconditional by these figures). Ranked by the time a redraw saves against the
lines to convert: History first, then Land, Services and Infrastructure, Build, Sectors. Four
calls wait for Jerus (§8).

---

## 1. What a redraw costs today

### 1.1 The redraw, part by part

`InPlaceProbe` opened the real window on the playtest's city at month 4,005, showed each rail
screen, and timed eight redraws as the clock makes them (`ui.redraw()` after a month), each
followed by **the pulse's own passes** - `Scene.focusCleanup`, `doCSSPass`, `doLayoutPass`, then
the whole scene pulse for the sync to the renderer - and by the settle `clearMenu()` posts after a
rebuilt page. Then it rebuilt the chrome alone, eight times, the same way. Median ms, the quietest
of three full runs (`screens-in-place/out/screens-m4000-0.8.24.txt`; the other batches' checks
share the machine, and the figures moved by up to a third between runs - the pilot's own figures
in §4 were taken turn and turn about in one JVM for that reason):

| screen | first visit | draw | CSS | layout | sync | settle | **redraw** | chrome alone | **the page** | nodes |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Build | 44 | 23.8 | 29.4 | 7.2 | 6.3 | 1.0 | **67.7** | 27.4 | **40.3** | 580 |
| Land | 39 | 14.1 | 18.6 | 3.7 | 3.4 | 0.7 | **40.5** | 19.2 | **21.3** | 474 |
| People | 47 | 19.7 | 25.4 | 5.6 | 5.3 | 1.2 | **57.2** | 17.0 | **40.2** | 1,182 |
| Services | 28 | 9.6 | 17.7 | 3.2 | 2.9 | 0.8 | **34.2** | 19.8 | **14.4** | 447 |
| Infrastructure | 31 | 9.2 | 18.0 | 3.3 | 2.7 | 1.0 | **34.2** | 16.2 | **18.0** | 394 |
| Sectors | 52 | 20.5 | 21.4 | 5.9 | 4.5 | 2.0 | **54.3** | 18.1 | **36.2** | 889 |
| Government | 30 | 8.7 | 19.7 | 2.9 | 3.4 | 1.2 | **35.9** | 17.5 | **18.4** | 449 |
| Finances | 28 | 8.9 | 20.7 | 3.7 | 2.9 | 1.0 | **37.2** | 16.8 | **20.4** | 380 |
| Bank | 38 | 11.3 | 21.8 | 3.9 | 3.2 | 1.6 | **41.8** | 14.1 | **27.7** | 823 |
| Trade | 24 | 8.7 | 14.3 | 2.7 | 2.1 | 0.8 | **28.6** | 13.7 | **14.9** | 391 |
| Policy | 30 | 7.9 | 14.1 | 3.3 | 2.7 | 1.2 | **29.2** | 14.0 | **15.2** | 397 |
| History | 48 | 18.7 | 22.5 | 5.0 | 5.0 | 1.7 | **52.9** | 11.8 | **41.1** | 642 |

"Draw" is the screen's show method, chrome included; "chrome alone" is its draw, CSS and layout
(14-27 ms: the header, rail, city panel, construction panel and inbox, rebuilt on every redraw of
every screen; Build's is the dearest because the construction panel is open on it); "the page" is
the redraw less the chrome. So:

- **A redraw is 29-68 ms, and the chrome is 12-27 of it on every screen.** The page is the rest,
  14 to 41 ms; History, People, Build and Sectors are the dearest pages.
- **CSS is the largest part, and part of it is in the draw.** A node added to a scene is matched
  against the stylesheets inside `getChildren().add()` (its style helper made at once; this is
  UP1's "Parent$ 18%" and why the draw's own samples are 50-91% JavaFX: `SPLIT` in the outputs),
  then styled in the pulse, laid out with its text measured, and given a peer for the renderer
  (the sync, 2-6 ms). A node kept in place pays none of it.
- **The settle** (`clearMenu()`'s `runLater`: `applyCss()` and `layout()` forced over the whole
  page, to put the scroll back) costs 0.7-2.3 ms a redraw.
- **A frame with no redraw costs nothing to speak of** (the day's label: 0-1.3 ms).
- **On the clock at 50x**, after UP1 took the autosave off it, the FX thread is 32-55% busy and the
  redraw (its draw, its pulse and its settle) is half to three quarters of that (a quarter on
  Land, whose map is the rest: UP3's) (`LIVE` in the outputs). Frames
  were p95 18-67 ms and at most 46-88 ms (0.8.25, `screens-m4000-0.8.25.txt`).

### 1.2 Where UP1's figures differ

UP1's table (People 21.3 ms to build and 33.5 of CSS and layout) timed CSS and layout with
`applyCss()` on the scene's root, which puts every node through a CSS update; the pulse touches
only what is dirty. For a rebuilt page that is nearly the same thing - almost everything is new -
so UP1's ranking stands. For a kept page it is not, which is why this study, and UP2 after it,
time the pulse's own passes (§9).

## 2. What changes from one month to the next

To say what changes, `InPlaceProbe` built each month's page off the scene (the window's column
swapped for a detached box for the length of the build) and walked it beside the page on show,
for eight months at month 4,000, setting on the page on show only what differed (`TreePatch`):
the text, the inline style, the values code set, the handlers, and the children where a list grew
or shrank; a subtree whose classes no longer lined up, a canvas, or a control with state of its
own (a slider, a text field) was swapped whole. Per month, on average (0.8.24's city):

| screen | nodes on the page | words changed | styles changed | other values | swapped whole (nodes) | rows come (nodes) | rows go | nodes untouched* |
|---|---:|---:|---:|---:|---|---:|---:|---:|
| Build | 580 | 14 | 1 | 9 | 2 sliders, 1 text field | 0 | 0 | 403 |
| Land | 474 | 13 | 0 | 8 | 1 (12) | 1 (103) | 0 | 213 |
| People | 1,182 | 64 | 0 | 56 | 0 | 0 | 0 | 819 |
| Services | 447 | 8 | 0 | 6 | 0 | 0 | 0 | 314 |
| Infrastructure | 394 | 5 | 0 | 24 | 0 | 0 | 0 | 244 |
| Sectors | 889 | 39 | 6 | 5 | 24 canvases (the sparks) | 0 | 0 | 645 |
| Government | 449 | 36 | 0 | 39 | 0 | 0 (4) | 0 (5) | 252 |
| Finances | 380 | 6 | 0 | 0 | 1 text field | 0 | 0 | 258 |
| Bank | 823 | 31 | 1 | 17 | 1 bar (7) | 0 | 0 | 573 |
| Trade | 391 | 27 | 0 | 6 | 1 canvas | 0 | 0 | 252 |
| Policy | 397 | 15 | 0 | 5 | 0 | 0 | 0 | 267 |
| History | 642 | 9 | 5 | 14 | 14 (269) | 13 (47) | 4 (24) | 84 |

\* of the nodes the page's code made: a label's skin, counted in the nodes column, is not. "Other
values" are sizes, geometry, visibility and the like.

So, by kind:
- **Figures** change: 5 to 64 labels a month on a page of 400 to 1,200 nodes. Their text is set;
  a label whose text is unchanged is not even laid out again.
- **Bars and pictures** change their geometry, not their nodes: the pyramid's bands, a
  waterfall's columns, a segment bar's parts (People 19 pictures, Bank 22, Finances 9). Their
  styles almost never change (a tone flips from amber to red perhaps once a year).
- **Charts' data** changes: History's charts and Sectors' 24 sparks are redrawn each month; as
  canvases they are cheap to style and the kept page keeps the chart node and its window.
- **Rows come and go** rarely: Land's offers (a row of 103 nodes in one month of eight),
  Government's ranked lines, History's - whose 13 "rows" a month are the patch's positions sliding
  past a line that appeared near the top, which a keyed scope turns into one row coming.
- **Fixed**: the headings, captions, keys, (i) buttons, chip strips, the cards' frames and their
  layout - everything else, 90-99% of the page.

What a page with only those changes set on it then pays is the patch's estimate: **0.0 to 1.5 ms
of CSS and 1.2 to 3.8 ms of layout** a month (History 8.8 ms of CSS, from the slid rows), where a
rebuilt page paid 7 to 17 ms of CSS (the chrome's left out). It is an estimate made on a page not
written to be kept; the pilot (§4) measures the real thing.

## 3. The pattern

### 3.1 One code path: the screen's own drawing, over last month's nodes

A kept screen keeps its drawing code as it is and changes where its nodes come from
(`ui/Kept.java`, whose javadoc is the spec). Inside a draw, `Kept.of(Label.class, Label::new)` -
or its short forms `Kept.label(text)`, `Kept.column(spacing, kids...)`, `Kept.row`, `Kept.stack`,
`Kept.flow`, `Kept.grid`, `Kept.plain`, `Kept.region` - hands back the next node of that class
that the same **scope** made at the last draw, or a new one when it made fewer. Outside a draw
every call is `new`, so a helper shared with screens not yet kept is unchanged for them; the pilot
converted the `Pieces` helpers the People page reaches and every other screen still calls them.

- **What holds the nodes:** the screen's `Kept.Pool`, one per kept screen (`PeopleScreen.pool`).
  Each scope in it remembers the nodes it made by class, in order. A scope's nodes not asked for
  at a draw are let go. The pool is forgotten when another screen is shown or another city opens,
  so only the screen on show is held, as today.
- **The boxes stage their children.** `Kept.Column`, `Row`, `Stack`, `Flow`, `Grid` and `Plain`
  are the JavaFX boxes with one difference: while a draw is on, `getChildren()` is a list of the
  draw's own, so the code adds children exactly as it does now; when the draw ends each box's real
  children are set from that list **in one call, and only where it differs**. That is the whole
  trick, and measured (`ReaddProbe`, People at month 1,000): the same nodes cleared and added back
  in two steps cost 19.5 ms, 5.9 of it CSS; set in one call, 2.1 ms and no CSS; left alone, 0.
- **A node handed back loses what code gives only sometimes:** its click, hover, press and scroll
  handlers, its action, its cursor, visibility, opacity, managed, disable, mouse-transparency and
  tooltip, where code set them (a value an inline style set is the style's and is left alone). So
  a row clickable last month and not this one does not keep last month's click.
- **Rows that come and go** are a scope each, keyed by what they show: `Kept.scope(sector, () ->
  row(sector))`. A row that goes takes its nodes with it; the rows after it keep theirs. A card is
  a scope too, so a chip that appears in one card does not shift every label after it onto a
  neighbour's node. The People page has 16 card scopes, 56 in all, and keys its table rows by
  `FamilyStructure`, `EducationType`, `JobType` and `WageBand`.
- **A picture takes new figures without new nodes.** The game's own pictures (`Mirror`, `Strip`,
  `Pair`, `SegmentBar`, `Waterfall`: a `Pane` with its own `layoutChildren()`) make their parts
  once, in the constructor, and are told each month's figures by `set(...)`, which updates them
  and asks for a layout only when a figure the layout reads moved. Where the number of parts
  depends on the data (segments, steps, household shapes) `set()` keeps them while the count holds
  and builds them afresh when it changes. A chart is the same: `TimeChart` already has `setData()`,
  so a kept chart keeps its window, its zoom and its crosshair across months, which a rebuilt one
  restores from the screen's fields today.
- **A choice that reshapes the page** - a details fold opened, the waterfall's year view, another
  sector picked - changes the screen's state and redraws, as now. The draw runs the same code over
  the kept nodes; what the choice adds is made new in its own scope, what it removes is let go,
  and the rest stays. A choice that changes the page wholesale (another business's five pages)
  keys its scope by the choice, so nothing of the old page is handed to the new one.
- **The model's figures still come through public getters**: the drawing code is the code that
  ran before, line for line, so the convention is untouched.

### 3.2 What the window does for a kept screen

`UserInterface.keepMenu(screen, again, pool, page)` replaces the screen's `clearMenu()` call: on
arrival (another screen before, or the rail's own tab pressed, which goes to the top) it forgets
the pool and clears the page as today; on a redraw of the same screen it refreshes the chrome and
the screen's bookkeeping and runs the page's code as a kept draw, without emptying anything. The
page reaches the window through `setPage()`, which leaves the column alone when its nodes are
already there. So on a kept redraw nothing clamps the scroll: the page's height is not pinned,
the settle (`clearMenu`'s `runLater`, a forced CSS pass and layout over the whole page, 0.7-2.3
ms a redraw) is not run, and the scroll position never moves, because the content was never
replaced. The inner scrollers keep theirs the same way.

### 3.3 The rules for code drawn kept

Five, from `Kept`'s javadoc; each was needed somewhere on the People page:
1. **A node is made through `Kept`, never with `new`**, in drawing code; constructor arguments
   become setters, set every draw. (`ColumnConstraints`, `Insets` and records stay `new`.)
2. **A value set under a condition is set on both sides of it** (`l.setWrapText(open)`, not
   `if (open) l.setWrapText(true)`), or a kept node keeps last month's value. The reset in 3.1
   covers the handlers, cursor, visibility and the rest for free.
3. **A list on a node is set, never added to**: points, path elements, style classes. Children
   are the exception (the boxes stage them); a `Kept.grid()` clears its column constraints when it
   is handed back.
4. **A listener or event handler is added once** (`Kept.isNew(node)`), reading what it needs when
   it fires, never a figure captured at the draw; or it becomes the `setOnXxx` the reset clears.
   `infoButton`'s two `addEventHandler`s became `setOnMouseClicked`/`setOnMousePressed`; the door
   pill's hover and press listeners are added once and read the accent from the node.
5. **An animation on a kept node goes through `Kept.play(owner, animation)`**, which stops the one
   the node was last given: the count-up of LIVING HERE and the waterfall's growth, at 50x, start
   again before the last has finished.

### 3.4 The two ways not taken

**A second, hand-written update method per screen** (build the nodes once, keep them in fields,
set their text on the clock) is what the brief first named. It would rewrite 46,000 lines into
two paths per figure - the one that builds and the one that updates - which drift apart unless a
harness compares them, and it is the comparison, not the second path, that holds a kept page
right. With `Kept` the second path does not exist. What the hand-written way would save beyond
it is the kept draw's own overhead - handing nodes back and setting values that did not change -
which on the People page is about a fifth of a 6.3 ms draw at month 4,000 (§4); the figures
themselves would be read and written either way.

**A generic patch** - build each month's page off the scene, walk it beside the page on show and
copy across only what differs - needs no change to any screen, and the probe does exactly that
(`TreePatch`) to estimate what keeping would save (§2). It cannot be the pattern: a handler made
by the new build holds the new build's nodes (a hover that restyles "this label", a click that
opens a popover anchored on "this cell"), so copied onto the kept node it acts on a node that is
not on screen; and a picture's figures live in its fields, which no patch can see. Its build is
still paid in full, too.

## 4. The pilot

**Why People.** It cost the most a redraw in UP1's table, its page is one of the four dearest in
this study's (§1, with History, Build and Sectors, all within 5 ms), and it has the most nodes
(1,182), the most pictures (19 a month to tell) and the only animations on the clock, so it
exercises every part of the pattern.

**What changed in the code** (0.8.26): `ui/Kept.java` (new, 580 lines with its javadoc);
`UserInterface.keepMenu()`/`setPage()` and `clearMenu()`'s kept branch (60 lines);
`PeopleScreen`: the People page moved into `drawPage(landed)` with each card in a scope, every
node made through `Kept` (115 sites), `Mirror`, `Strip` and `Pair` told their figures by `set()`,
the count-up through `Kept.play`; `Pieces`: the 26 helpers and 2 pictures (`SegmentBar`,
`Waterfall`) the page reaches (83 sites), the (i) button's and door pill's handlers set or added
once. Household money, the popovers and every other screen are drawn as before. No model change.
The conversion itself was a Sonnet slice on a brief of §3.3's rules, reviewed here, and the
review changed nothing in it. What `KeptCheck`'s first runs found was in `Kept` itself: its reset
set values already at their defaults, which changed nothing on screen but marked them as code's
(fixed: it resets only what differs).

**It looks exactly as it did.** `RenderProbe` wrote the People page whole (the page's box, not
the viewport) as a PNG at its first draw and after eight months redrawn on the clock, on 0.8.25
and on the pilot: **the same bytes**, at month 1,000 and at month 4,000
(`screens-in-place/renders/`; the after-eight pictures are taken once the count-up has settled).
`KeptCheck` compares the kept page with a page built new for 25 months and through every fold.

**What it costs**, measured by `PilotProbe` in one JVM on the playtest's city at month 4,101:
the kept page against the same page with its pool forgotten before each redraw (which is what
every redraw did before), 24 redraws each, alternated in three turns so the machine's load falls
on both (median ms):

| People's redraw | draw | CSS | layout | sync | settle | **total** | nodes made |
|---|---:|---:|---:|---:|---:|---:|---:|
| built afresh (as 0.8.25) | 16.8 | 23.6 | 5.7 | 5.4 | 1.3 | **53.0** | 863 |
| kept (0.8.26) | 11.2 | 12.3 | 4.7 | 2.7 | 0 | **32.1** | 0 |
| the chrome alone, in both | 4.9 | 10.5 | 1.5 | 1.2 | - | 18.1 | |
| **the page alone** | afresh 11.9 → kept 6.3 | 13.1 → 1.8 | 4.2 → 3.2 | 4.2 → 1.5 | 1.3 → 0 | **34.7 → 12.8** | |

The page's own redraw is **2.7 times cheaper: 22 ms less, every redraw** (the sums of its parts;
the medians of the totals say 34.9 and 14.0). Its CSS is gone (13.1 ms to 1.8), its sync nearly,
its settle wholly; its layout is little changed, because the figures
that changed are measured again and their cards laid out again. What is left is the draw itself,
6.3 ms: the page's code still runs in full every month - every figure read and formatted, every
value set (unchanged ones are free in JavaFX, but the call and the comparison are not) - plus
Kept's bookkeeping. JFR on the kept redraw (`people-top-m4000.txt`) puts three quarters of the
redraw's draw in the chrome being rebuilt - the header alone 40%, reading five whole histories for
its tiles (UP2's item 5) and matching its new nodes' styles as they are added - and a quarter in
the page's draw, most of that the page's own code (the vitals' trends and the outside tiles'
sparks read from the history, the words formatted) and about a fifth Kept's maps.

On `KeptCheck`'s smaller city (month 605, 3,942 people) the page's month is 5.4-6.0 ms kept
against 20.5-22.4 ms built new.

**On the clock at 50x** (`PilotProbe`, the same JVM, People on show, three turns of 5 s
stretches after a warm-up; "still" is the kept page with the count-up held off - the page told no
month landed while it was watched). The FX thread's CPU, shared out by 5 ms samples:

| way | months/s | frame p50 | p95 | max | the month | the redraw's draw | the pulse | the settle | busy |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| built afresh | 10.0-10.2 | 7-8 ms | 49-61 ms | 65-83 ms | 102-148 ms/s | 27-53 | 261-294 | 23-30 | 47-54% |
| kept | 10.0-10.2 | 6 | 17-29 | 39-53 | 112-153 | 17-22 | 234-261 | 0 | 40-42% |
| kept, no count-up | 10.0-10.4 | 6 | 27-29 | 53-60 | 156-193 | 30-33 | 106-108 | 0 | 30-35% |

The pulse is the largest share in all three, and the stillness says why: **the count-up and the
bars' growth** run 600 ms after every month that lands while People is on show, and at 50x one
lands every 100 ms, so every frame of the 60 a second relays the label and the waterfall (about
140 ms a second of the FX thread). It is the same work kept or rebuilt; the pilot did not change
it, and whether it runs at 50x is Jerus's call (★4).

**Behaviour.** A click, a fold, the scroll and the keys behave as they did. One thing had to be
put back by hand: a chip clicked used to lose the focus when the page was rebuilt under it, and a
kept chip would have kept it, with the theme's focus colour round it; `keepMenu()` now moves the
focus off a kept page at each redraw, where the rebuild used to drop it. A tooltip open over a
kept node keeps its node, and its words follow the month; checked by eye on the PC (UP4-C).

## 5. The order and the size

What each screen would save a redraw, kept: its page today (§1) less its page kept, estimated
as the pilot measured People's - the draw keeps 53% of the page's draw today (People: 6.3 of
11.9 ms), the CSS and layout are the patch's estimate (§2; History's with its slid rows keyed, as
KP1 does), and the sync 1 ms. The chrome is UP2's and is left out. Every screen is weighed alike
(★1); lines and sites are the batch's (§7), shared pieces counted once, with the first batch that
needs them.

| screen | page today | page kept (est.) | saved a redraw | batch | its lines | saved per 1,000 lines |
|---|---:|---:|---:|---|---:|---:|
| People | 40.2 | 12.8 (measured) | 22-27 | the pilot | - | - |
| History | 41.1 | 11.8 | **29.3** | KP1 | 4,689 | **6.2** |
| Infrastructure | 18.0 | 4.7 | 13.3 | KP2 | | |
| Land | 21.3 | 10.9 | 10.4 | KP2 | | |
| Services | 14.4 | 4.7 | 9.7 | KP2 | 7,335 for the three | **4.6** |
| Build | 40.3 | 14.5 | **25.8** | KP3 | 6,586 | **3.9** |
| Sectors | 36.2 | 13.6 | **22.6** | KP4 | 7,364 | **3.1** |
| Bank | 27.7 | 7.3 | 20.4 | KP5 | | |
| Finances | 20.4 | 4.9 | 15.5 | KP5 | 10,950 for the two and the fund | **3.3** |
| Government | 18.4 | 4.4 | 14.0 | KP6 | | |
| Policy | 15.2 | 4.5 | 10.7 | KP6 | | |
| Trade | 14.9 | 4.3 | 10.6 | KP6 | 12,139 for the three and zoning | **2.9** |

So every page saves 10-29 ms a redraw - at 50x, five redraws a second, 50-150 ms a second of the
FX thread on the screen on show - and the work is about 2,000 sites of `new` in 46,000 lines
(§7). History pays best for its size, then the three small pages of KP2, then Build. Sectors
comes before Bank and Finances because it carries the statements (`Statement`, `StatementView`)
they share, which makes KP5 smaller. Government, Trade and Policy are the cheapest pages and
Policy is the largest file, so they come last but one; they also wait for the controls' rule
(sliders and fields, §7) that KP2's Services and KP3's Build settle first.

With the chrome kept as well (UP2), a redraw would be the page kept and little more: People's
would go from 53 ms to about 15, and the FX thread at 50x on People from half busy to about a
third (a quarter with the count-up held, ★4).

## 6. What checks it

**`KeptCheck`** (new, in `AllChecks` and `Timing.HARNESSES`) with **`ui/KeptBench`**, its hands on
the page. It needs the JavaFX toolkit but no window: the bench lays the page out on a `Scene` with
no stage at the menu column's width, so it runs on the PC beside Jerus's game without showing
anything, and where the toolkit cannot start (the cloud has no display) it says so in one line and
exits 0. On a city played to `CITY_MONTHS` (600) months it proves:

1. **the page is kept**: each month's redraw keeps the same page in the column and hands back at
   least nine in ten of the nodes its first draw made; most months make no node at all;
2. **it shows what a page built new shows**: for `MONTHS` (25) months the kept page's signature -
   every node's class, words, inline style, the values code set on it, its handlers, its tooltip's
   words and where layout put it - equals the signature of the page built new at the same month,
   line for line (the first difference is printed with both lines);
3. **and looks it**: at three of those months the two pages are the same picture - no pixel more
   than `SHADE_LEVELS` (64 of 255) apart in a channel and at most one in a thousand apart at all:
   the renderer shades the edge of a small shape a kept picture moved a level or two apart from
   one drawn there new (People's household tiles, reused by shape wherever the sort puts them:
   114 and 110 edge pixels, up to 58 levels, at two months of 25; the third month was the very
   same pixels). Then the comparison is shown to see: one figure on the kept page made wrong by
   hand parts both the signature and the picture, and the next kept draw sets it right;
4. **what reshapes the page keeps it right**: a details fold opened and closed, the waterfall's
   year view on and off - the redraw after each change and the one after that equal a page built new;
5. **it is cheap**: the kept page's month - its draw, then the pulse's CSS and layout - is within
   `KEPT_MONTH_MS` through `Timing`, with the same month built new printed beside it.

Section 2 is the harness the brief asked for, that "the in-place page shows the same figures as a
fresh build of it", made stronger: not the figures alone but everything a kept node could carry
from one month into the next. Each KP batch adds its screen to the bench and a section per page
(its sub-pages and the choices that reshape it), and its own `KEPT_MONTH_MS`.

## 7. The batches

Each batch converts whole screen classes - every page and sub-page they draw - so no screen is
half kept; each moves the `Pieces`, `Statement` and picture code its screens reach, adds its
pages to `KeptBench` and a section per page to `KeptCheck` (the sub-pages and the choices that
reshape them, as §6's section 4 does for People), and gives each page its own `KEPT_MONTH_MS`.
The conversion is mechanical and §3.3's rules are the brief, so each screen is a Sonnet slice;
the review is the harness's to make, with the renders before and after beside the notes. Sites
are the `new` of a node in the class today.

**UP2's chrome** (in UP2, its item 4; ★3). `refreshHeader()` (the clock block, the money block,
`HeadlineRow`'s five `HeadlineTile`s and their `Sparkline`s, the rating and the inbox's envelope),
`refreshTabRail()` (twelve `railButton`s and Menu, the minimap left as it is), `SummaryScreen.
refreshCityPanel()`, `refreshConstructionPanel()` and `refreshInbox()`, each drawn as a Kept draw
from a pool of its own in `UserInterface` (each part is drawn whether or not the page is kept,
so it needs its own `Kept.draw`). The header's `getChildren().clear()` and the rail's
`retainAll` become one settled `setAll`. `HeadlineTile` and `Sparkline` become pictures told
their figures. `KeptBench` lays the chrome out beside the page; `KeptCheck` compares it with a
chrome built new. About 1,100 lines (UserInterface's chrome, 197 sites in the file of which about
60 the chrome's; SummaryScreen 1,503 lines, 30 sites).

| batch | what | lines | sites | saves a redraw |
|---|---|---:|---:|---|
| KP1 | **History**: its charts kept (`TimeChart` given `setData()` on a kept chart, which keeps its window, zoom and crosshair), the rows over them keyed so a line that appears does not slide the rest down onto its neighbours' nodes; `RangeBar` | 4,689 | 97 | 29 ms |
| KP2 | **Land, Services and Infrastructure**: the land office's cards and offers (a scope per offer; the map is UP3's and stays a canvas of its own), the four systems' pages, `FlowCurve`; the first **controls** (Services' staged-lever slider): a kept slider or field keeps the player's hand on it - its value set only where it differs and never while pressed, its listener added once reading the model when it fires | 7,335 | 279 | 10-13 ms each |
| KP3 | **Build and the construction page**: the categories, the market's cards (a scope per building type), the order bar, the receipt and its pulse, the auto-builder's card; its sliders and fields by KP2's rule | 6,586 | 306 | 26 ms |
| KP4 | **Sectors**: the list (a scope per business, keyed by its sector), a business's five pages (a scope keyed by the business and the page), the sparks (a kept `Canvas`, cleared and drawn), `RefineryPicture`, `IndustryPane`, `LegBar`; `Statement` and `StatementView`, which the money screens share | 7,364 | 396 | 23 ms |
| KP5 | **Bank, Finances and the fund**: the ladders, the borrow auction (`AuctionChart`, `CurveChart`), the ask's field, `CapChips`, `FundScreen` | 10,950 | 261 | 16-20 ms each |
| KP6 | **Government, Trade, Policy and zoning**: the bridge, the band tracks, the dial cards (`Levers`, `Ladder`: sliders, by KP2's rule) | 12,139 | 209 | 11-14 ms each |
| KP7 | **The rest, and the rebuild path retired**: People's Household money (`Books`, `cashCell`), the `Pieces` helpers no kept screen reached; once every rail screen is kept, `clearMenu()`'s pinned height, its settle and `restoreScroll()`'s three attempts run only on arrival, and CLAUDE.md's "A screen section... must restore its own scroll position" becomes "draws through Kept" | about 2,500 | about 250 | the settle and the hold, on arrival only |

Order: UP2 first (it is queued, and the chrome is on every screen), then KP1 to KP7 as numbered
(§5's ranking), unless ★1 says otherwise. KP3 and KP6 wait for KP2, which settles the controls'
rule; KP5 is smaller for coming after KP4's statements.

## 8. Calls for Jerus

Each with what the study found; a default is what the batches do if he says nothing.

- **★1 Which screens you sit on while the clock runs.** The ranking in §5 weighs every screen
  alike, because nothing records where a player spends his time (the log is 1 KB and says
  nothing of screens). If you mostly watch one or two screens at 50x, those go first whatever
  their size. *Default: the order in §5 (History, then Land, Services and Infrastructure, Build,
  Sectors, Bank and Finances, then the rest).*
- **★2 Go on with the rewrite, KP1 to KP7?** The pilot is in 0.8.26 (People only; every
  other screen is drawn as before). Converting the rest is about 2,000 sites of `new` across the
  screens and `Pieces`, mechanical, one to three screens a batch, each checked by `KeptCheck`
  against a page built new. *Default: queue them in §7's order after UP2.*
- **★3 The chrome in UP2, with Kept.** UP2's item 4 keeps the header and rail "only where the
  probe shows it still matters", under 5% of the FX thread. It matters: the chrome is 12-27 ms of
  every redraw at month 4,000 (§1), the largest single part of a kept redraw, and on every screen.
  The study plans it (§7, "UP2's chrome") and recommends UP2 does it with `Kept`, unconditionally,
  with the city panel, the construction panel and the inbox as well as the header and rail.
  *Default: UP2 follows §7.*
- **★4 The count-up at 50x.** When a month lands while People is on show, LIVING HERE counts up
  and the month's bars grow, for 600 ms (your "fun: the number moves", 0.7.27). At 50x a month
  lands every 100 ms, so the animation never stops: every frame relays the label and the bars
  (§4, the pulse between redraws). Keep it at every speed, or count up only at 1x-10x and set the
  figure at once above? *Default: kept as it is (the pilot changed nothing you can see).*

## 9. The probes

All of them are in package `ham.citybuildersim.ui` (or use it) for the window's package-private
fields, and read the city through `LongPlaytest.probeCity`/`probePlay` (UP1's) or, before UP1
shipped, `docs/queue/UP1/probe/UiGrowProbe.java`. Compiled beside the build, never into it:

    javac -d <probe classes> -cp "target/classes;<gson>;<javafx base, graphics, controls 21 -win>"
          docs/studies/screens-in-place/probe/*.java docs/queue/UP1/probe/UiGrowProbe.java
    APPDATA=<scratch> java -XX:FlightRecorderOptions:stackdepth=1024 -Dprobe.out=<dir>
          -cp "<probe classes>;target/classes;<the same jars>" ham.citybuildersim.ui.InPlaceProbe 4000 8 all

`APPDATA` set to a scratch folder keeps the window's preferences off the real save folder; the
city is in a `GameFiles.scratch()` folder. Options of `InPlaceProbe`: `-Dprobe.kept=0` skips the
patch estimate (which cannot run on a kept screen: it builds the page with the window's column
swapped out), `-Dprobe.split=1` the JFR split of the build, `-Dprobe.top=1` with it the hottest
frames, `-Dprobe.live=S` the clock at 50x for S seconds a screen. `PilotProbe MONTH REDRAWS TURNS
SECONDS` is the pilot's A/B: the kept People page against the same page with its pool forgotten
before every redraw (the old way), alternated in one JVM so the machine's load falls on both.
`RenderProbe MONTH REDRAWS LABEL` writes the People page as PNGs. The "before" build for the
renders and the per-screen tables was main's sources compiled into a scratch folder.

**A note on UP1's method, for UP2.** UP1's probe and `ui/UiProbe` time CSS and layout as
`scene.getRoot().applyCss()` then `layout()`. `applyCss()` forces every node under the node it is
called on through a CSS update, changed or not, which no pulse does; the frame's own passes
(`Scene.doCSSPass`/`doLayoutPass`, reached by reflection since the toolkit is on the class path)
touch only what is dirty. On a rebuilt page the difference is small, because nearly everything is
dirty; on a kept page it is most of the figure. UP2 should time its chrome through the pulse's
passes (`InPlaceProbe.pulse()`), or `UiProbe` will not see what keeping saves.
