**Resume here (16:45, 11-27):** pushed: the picture tool (slot copies, or "playtest MONTH"), UiSteadyCheck (33 reds on today's pages, three faults, section 3 below) and audit 1 of 5 (docs/studies/ux1/audit-1-chrome-build.md). Next, in order: picture the playtest city at month 600 and 2,400 (the mid and big cities, section 2) on scratch; audits 2-5 by helper (brief: C:/Users/Jerus/AppData/Local/Temp/cbs-UX1-scratch/audit-brief.md, placeholder STAB must not hit "STABILITY"); the audit summary; the mockups over 003-map (HTML via Edge headless, the game's Palette, IBM Plex, Icons.java paths); the rows UX2+; the ship file with known-reds; --fast; pick; mail.

# UX1 - the interface rework's study: notes

Builder: Claude (Opus), batch/UX1, cut from origin/main at ecac4f4bd. A study: unversioned, no tag, no
ensemble, no stranger's review. Brief: docs/queue/UX1.md (Jerus's words, 11-27).

## 1. The picture tool - ui.PagePictures

- `ui/PagePictures.java` loads a copy of a slot from a scratch folder (`GameFiles.scratch`), opens the
  game's own window (`UserInterface.start`) at 1920x1080, plays N months, and writes a PNG of every rail
  page and sub-page (`NNN-page.png`), plus `-whole.png` where the page's scroller holds more than the
  window. It walks `PagePictures.pages(ui, g)`: the city as it lands, the drawer, the inbox, the map
  over the window, Build's Overview and every category, Construction's three tabs, the land office,
  People and Household money, Services' every system and page, Infrastructure's four, Sectors and one
  sector's five books pages, Government's four, Finances' hub and every area's pages, the Bank's choice,
  the commercial bank's seven, the central bank's nine, Trade's six, Policy's hub and every area's pages,
  History and Rankings: 121 pages.
- The hook in ui is two package methods on UserInterface beside `replayGoTab`: `railKeys()` and
  `pressRail(key)`, the rail's own `goHome`. No page changed.
- The map paints its tiles a few a frame (MapView), so a page with the map is left 4 s before it is
  drawn (`MAP_SETTLE_MS`), the rest 0.4 s.
- Run (Git Bash, the worktree root, after a compile; never while `mvn compile` rewrites target/classes:
  two runs died of it here, so copy target/classes aside for a long run):

      java -cp "target/classes;$(cat target/classpath.txt)" \
           -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin \
           -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin \
           ham.citybuildersim.ui.PagePictures C:/Users/Jerus/AppData/Local/Temp/cbs-UX1-scratch/slots 1 1 OUT [prefix...]

## 2. The cities

| city | month | people | format | how it was loaded |
|---|---|---|---|---|
| slot 1, "Halifax" (Rotterdam), 1909 | 114 (+1 played) | 1,888 | 67 | a copy, as saved: the young town |
| the playtest's city, mid | 600 | (at the run) | - | `PagePictures playtest 600 1` |
| the playtest's city, big | 2,400 | (at the run) | - | `PagePictures playtest 2400 1` |

★ His slot 2 (Ashbourne, m241, 8,000 people) and slot 9 (m6,680, 50 M) are format 66; today's floor is 67
(RES3). A scratch copy with its `saveFormat` word set to 67 loads and plays, but draws a broken city:
audit 1 found Ashbourne's header at 0 people and slot 9's at 36,732, with every staffing at 0% (the RES3
fields a 66 save lacks). Pictures of a broken city would mislead the audit, so both were dropped. The
mid and big cities are the playtest's own city (`LongPlaytest.probeCity`, the city UiProbe and the
nightly grow), played to two ages by the tool's `playtest` source. Audit 1 (the chrome and Build) was
read on the broken copies' layouts and slot 1; its figures from slots 2 and 9 are not used, only the
layouts (the Industry page's 74 cards are the catalogue's, not the city's).

His saves folder was only read (`cp` to `C:\Users\Jerus\AppData\Local\Temp\cbs-UX1-scratch\slots`).
Every picture is on scratch: `C:\Users\Jerus\AppData\Local\Temp\cbs-UX1-scratch\pics\<city>`.

## 3. The stability harness - UiSteadyCheck

`UiSteadyCheck` (root package, as AllChecks runs harnesses) with `ui.SteadyBench` (the real window,
measured). It founds the playtest's young town (`LongPlaytest.probeTodayCity`, month 19), opens the
game's window at 1920x1080 on a stage drawn transparent and off screen (FollowBench's way), and walks
the picture tool's 121 pages. Sections, as RestaurantsCheck numbers what it has to prove:

0. the measure sees a fault: on a scene of its own, a still redraw shows nothing; a node replaced, a
   box made taller, the scroller moved and the page grown are each seen (5 assertions, green);
1. an unchanged redraw moves no node (to half a pixel) and no scroller, on every page;
2. an unchanged redraw makes no node new and lets none go (the same node objects after as before);
3. three unchanged redraws grow nothing: the page's content and the window's root;
4. the page's scroller set a third down, a month played and redrawn as the clock does, stays at its pixel.

Its own faults, mended: the first run measured the Sectors books' dial arc and waterfall mid-animation
(arrival animations up to 1.1 s), so a page now waits `ARRIVE_MS` (1.5 s) after it opens; a scroller
whose page a month made shorter than its place is held when it stands at its new end. About 4 minutes
on this PC.

**What it found on today's pages (33 reds, three faults):**

| fault | pages | sections | owner |
|---|---|---|---|
| F1. The page breathes: its height flips by one physical pixel (0.8 px at 125% scaling) on every redraw, with the empty toast corner moving with it | Services' 18 pages, Infrastructure's Roads and Transit | 1 (12 pages), 3 (19) | the frame row (the page's foot and the toast room are the frame's) |
| F2. A line rebuilt: "Capital gains: assessed ..." on Government's Revenue is a new Label at every redraw (Kept's first rule) | Government > Revenue | 2 | the Government page's row |
| F3. The place held as a fraction: a month that grows the page by 1.8 px moves the view 0.6 px on Build > Industry's 12,186 px; a page that grows by 300 px above the view would move it 100 px | Build > Industry | 4 | the build screen's row |

F1 depends on the screen's scale: at 125% one physical pixel is 0.8 px; the gate box (Monocle, scale 1)
may show it as 1 px or not at all. The known-reds rows are scoped by page so they cover either.
