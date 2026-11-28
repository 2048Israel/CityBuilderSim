**Resume here (11-27, 21:00):** the study is final and pushed; --fast green, UiSteadyCheck known reds only (exit 3), the pick ALL; the pictures' mail sent (20:48). Waiting for the orchestrator's go on "UX1 dry run?". On the go: merge origin/main if it moved; `sh tools/gate.sh UX1 --dry-run --no-tag > target/UX1-dry.txt 2>&1` in the background (timeout 7200000), `sh tools/wait.sh --line target/UX1-dry.txt 'gate +exit [0-9]|^exit [0-9]' --for 9` until it ends; then the gate-run marker, ready.

# UX1 - the interface rework's study: notes

Builder: Claude (Opus), batch/UX1, cut from origin/main at ecac4f4bd. A study: unversioned, no tag, no
ensemble, no stranger's review. Brief: docs/queue/UX1.md (Jerus's words, 11-27). Calls are marked ★;
section 9 gathers them. The study's folder is `docs/studies/ux1/` (its README shows the mockups and picks).

## 1. The picture tool - ui.PagePictures

- `ui/PagePictures.java` opens a city in the game's own window (`UserInterface.start`) at exactly
  1920x1080 (an undecorated stage: a decorated one gave 1906x1043), plays N months, and writes a PNG of
  every rail page and sub-page (`NNN-page.png`), plus `-whole.png` where the page's scroller holds more
  than the window. Two sources: a folder of copied slots (`GameFiles.scratch`, his saves only read), or
  `playtest MONTH`, the playtest's own city (`LongPlaytest.probeCity` + `probePlay`) grown to that month
  on the main thread before the window exists.
- It walks `PagePictures.pages(ui, g)`, 122 pages: the city as it lands, the drawer, the inbox, the map,
  Build's Overview and every category, Construction's three tabs, the land office, People and Household
  money, Services' every system and page, Infrastructure's four, Sectors and one sector's five books,
  Government's four, Finances' hub and every area's pages, the Bank's choice, the commercial bank's seven,
  the central bank's nine, Trade's six, Policy's hub and every area's pages, History, Rankings, and
  `map-ground` (the map alone over the whole window, `replayCinematic(true)`: the mockups' ground).
- The hook in ui is two package methods on UserInterface beside `replayGoTab`: `railKeys()` and
  `pressRail(key)`, the rail's own `goHome`. No page changed.
- The map paints its tiles a few a frame, so a page with the map waits 4 s (`MAP_SETTLE_MS`), the rest 0.4 s.
- Run (Git Bash, the worktree root, after a compile; never while `mvn compile` rewrites target/classes:
  two runs died of it, so copy target/classes aside for a long run):

      java -cp "target/classes;$(cat target/classpath.txt)" \
           -Dcbs.earth=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/earth-2.bin \
           -Dcbs.world=C:/Users/Jerus/Documents/NetBeansProjects/CityBuilderSim/data/world-1.bin \
           ham.citybuildersim.ui.PagePictures C:/Users/Jerus/AppData/Local/Temp/cbs-UX1-scratch/slots 1 1 OUT [prefix...]
      ... ham.citybuildersim.ui.PagePictures playtest 2400 1 OUT      # the big city; about 15 minutes

## 2. The cities

| city | month | people | how it was loaded |
|---|---|---|---|
| slot 1, his young town (Rotterdam), July 1909 | 115 | 1,888 | a copy, as saved (format 67) |
| the playtest's city, mid | 606 | 3,900 | `PagePictures playtest 600 1` |
| the playtest's city, big | 2,406 | 174,226 | `PagePictures playtest 2400 1` |

★ His slot 2 (Ashbourne, m241, 8,000 people) and slot 9 (m6,680) are format 66; today's floor is 67
(RES3). A scratch copy with its format word set to 67 loads and plays but draws a broken city (0 people
in Ashbourne's header, 36,732 in slot 9's, every staffing 0%: the fields a 66 save lacks), so both were
dropped for the playtest's own city at two ages. Audit 1 was first read on the broken copies and then
read again on the three cities above; nothing of slots 2 and 9 is used. The playtest city built no school
at 600 or at 2,400 months (174,226 people): the audit takes it as it is, a city that needs one.

Every picture is on scratch, 122 a city: `C:\Users\Jerus\AppData\Local\Temp\cbs-UX1-scratch\pics\`
`slot1`, `playtest600`, `playtest2400` (about 26 MB each; `slot2`, `slot9` are the broken ones, unused).

## 3. The stability harness - UiSteadyCheck

`UiSteadyCheck` (root package, as AllChecks runs harnesses; in HARNESSES and DISPLAY) with
`ui.SteadyBench` (the real window, measured). It founds the playtest's young town
(`LongPlaytest.probeTodayCity`, month 19), opens the game's window at 1920x1080 on a stage drawn
transparent and off screen (FollowBench's way), and walks the picture tool's 122 pages. Its sections:

0. the measure sees a fault: on a scene of its own, a still redraw shows nothing; a node replaced, a
   box made taller, the scroller moved and the page grown are each seen (5 assertions, green);
1. an unchanged redraw moves no node (to half a pixel) and no scroller, on every page;
2. an unchanged redraw makes no node new and lets none go (the same node objects after as before);
3. three unchanged redraws grow nothing: the page's content and the window's root;
4. the page's scroller set a third down, a month played and redrawn as the clock does, stays at its pixel.

Its own faults, mended: arrival animations (the sector books' dial arc and waterfall, up to 1.1 s) were
measured mid-flight, so a page waits `ARRIVE_MS` (1.5 s) after it opens; a scroller whose page a month made
shorter than its place is held when it stands at its new end; its tally line says "N FAILED", the form
KnownReds counts. About 4 minutes on this PC; three runs gave the same 33 reds.

**What it found on today's pages (33 reds, three faults):**

| fault | pages | sections | owner |
|---|---|---|---|
| F1. The page breathes: its height flips by one physical pixel (0.8 px at 125% scaling) on every redraw, the empty toast corner moving with it | Services' 18 pages; Infrastructure's Roads and Transit (Freight on a fourth run) | 1 (12 pages), 3 (19) | UX9 ★ |
| F2. A line rebuilt: "Capital gains: assessed ..." on Government's Revenue is a new Label at every redraw (Kept's first rule), and it is a developer's check line shown to the player (audit 3) | Government > Revenue | 2 | UX10 |
| F3. The place held as a fraction: a month that grows Build > Industry's 12,186 px by 1.8 px moves the view 0.6 px; a page that grew 300 px above the view would move it 100 px | Build > Industry | 4 | UX7 |

★ F1's owner is the page row, not the frame: every page sits in the same frame and only these breathe,
so the cause is in their content (a fractional height snapped two ways). At the gate box's scale 1 it may
show as 1 px or not at all; the known-reds rows are scoped by page, so they cover either. The rows are in
the ship file: `-services-` and `-infrastructure-` (UX9), `-government-revenue` (UX10),
`-build-industry` (UX7). Infrastructure's row is the screen's, not two pages': three runs showed F1 on
Roads and Transit, a fourth (the batch check's) on Freight as well, the same fault on another of its pages.

## 4. The audit

Five areas, each read by a helper from the pictures alone (no source), the big city first, the young
town for the thin case, the mid city where they disagree; each page's Shows, Wrong and For the map, then
the area's five worst problems and its pictures: `docs/studies/ux1/audit-1-chrome-build.md`,
`audit-2-people-services.md`, `audit-3-sectors-government-finances.md`, `audit-4-bank-trade.md`,
`audit-5-policy-history.md`. Their 36 pictures are in `docs/studies/ux1/pictures/` as `NNN-page--city.png`.

What runs through all five:

- **Alarms buried or repeated, never in one place.** Alerts are in six places and none is whole (the
  "Needs you" chip's 7 matches nothing in its drawer; the toasts' [!] cards; the inbox's 12 with settled
  items over open ones; the bank's "under the minimum" card on all 16 Bank pages; Trade's four cards on
  every Trade page in the young town); nothing can be dismissed. Meanwhile the big city's real crises
  (no school for 174,226 people, 87.9% ate less than a basket, no pension for 23,669 seniors) sit in
  9 px pills, tab dots or 1,470 px down a page.
- **Walls and empty pages.** Build's Industry is about 75 open cards over 13,606 px with no filter or
  sort; Services' books are one chart behind "details"; half of Services and most of the central bank are
  nearly empty (Holdings is 28 rows of $0 in every city); the young town gets full-size pages of zeros.
- **One thing drawn many ways.** Four header widths, eight build-button labels, three drawings of "share
  served", dials drawn as chips, slider and stepper at once, navigation tripled (buttons, cards and strip
  links to the same six Policy areas, up to three rows of tabs).
- **Figures that disagree or cannot be read.** "362% served" over 96,033 MWh unserved; Spending's
  headline against its rows; "$ millions" tables cut off; axes at "$1000000B"; "$" against "D$"/"R$" for
  the same money; codes in player text ("(E)", "Q11"); the ladder drawing no bars; the pension waterfall's
  negatives drawn upright; a debug line on Revenue.
- **Build never tells the city's own from the market's** ("you have N" counts the investors'), and a
  "best buy" chip sits on a card that loses $576.9M a month.
- **For the map**, every audit sorted its pages three ways: what fits a 480-560 px panel (most pages'
  figures and cards), what needs the wide view (statements, the bond book, Freight, the "who pays"
  tables, Trade's partners), and what belongs on the map (a sector's works, a building's card, a need's
  short districts, Lending's "shut" and "defaulting" tags, the ports' berths).

## 5. The mockups and the picks

Ten mockups at 1920x1080, drawn as HTML in the game's own look (`ui/Palette.java`'s colours, IBM Plex,
the outline icons read from `ui/Icons.java` by `mockups/lib/icons2js.py`) over the real map
(`121-map-ground` of the young town and of the big city), rendered by Edge headless:
`docs/studies/ux1/mockups/mock-NN-*.png`, each beside its `.html`. Nothing is taken from Hearts of
Iron IV or Cities: Skylines but the ideas (the map always behind; a build menu by groups). White labels
are the mockup's, not the interface; the yellow one names the mockup and its pick. Mockups 1 and 5-6
are on the young town, the rest on the big city; most figures are the pictures', the invented ones are
the new things (an alert's trend, the build tiles' counts) and agree with the page's totals.

| pick | recommended (★) | alternatives | the trade-off |
|---|---|---|---|
| P1, where pages open | A: one panel, 560 px beside the rail, a path line and Wide (mockups 1, 2, 3) | B: columns, a level each (4); C: a bottom drawer (5) | A keeps the map two thirds of the window and one page in view; B shows every level but leaves the map a third; C fits wide tables but not long pages |
| P2, the needs | A: a sidebar of small circles and words at the right edge (1) | B: the circles in the top strip (6) | B frees the map's edge but fills the strip, its words 9 px, no room for a figure |
| P3, the alerts | A: a mark per kind under the strip, eight then "+N" (7) | B: four marks by area with counts (8) | B never crowds but each alert is a click further and the kind is not seen at a glance |
| P4, the build screen | A: in the panel, categories as icons, families, a tile per building, one card at the foot, placing by a ghost on the map (9) | B: the catalogue wide over the dimmed map (10), which A keeps as its Wide | B compares every building at once but hides the map while choosing |

**The alerts' rules** (P3 A, drawn on mockup 7, built by UX3): one mark per kind of alert, bad before
warnings, newest first within each; eight show, the rest fold into "+N", which opens the list in the
panel. Hover tells (title, one line, the figure); left click opens it in the panel (what, the figure and
its trend against the steps, what to do as buttons to the page that mends it, Dismiss); right click
dismisses with an Undo toast. ★ A dismissed alert returns when it worsens a step (its figure crosses the
next threshold) or after 12 months if it still holds, and leaves the row by itself when its condition
clears; the alternative, gone until it clears and recurs, would hide a crisis that deepens. The inbox
keeps letters and every alert's history; a letter never becomes a mark.

**Today's alerts, where they go** (UX3 builds it):
- the "Needs you" chip and drawer: Schools, Childcare and the Bank become marks, Roads an amber Streets
  mark; the drawer's thirteen rows become UX4's needs;
- the toasts: "The city is not looking after its people" splits into its parts' marks (Schools,
  Childcare, Food); "Businesses are going bust" a mark; "Government wealth: hoarding" an amber mark;
- the inbox: the bank's letters stay letters; every alert's history goes there, settled or not;
- the bank's "It is under the minimum" (a card on 16 pages): one mark, its page keeps the gauge; "Some
  of its funding is foreign" at 1% stays a line on Funding;
- Trade's cards: "Under 3 months of import cover" a mark; "money leaving" and "hot money" (one page) one
  mark; "dollar debt" a mark; the cards leave the pages;
- the strip's red figures (population falling, GDP −7.0%, deflation and trust 26%, 12% out of work) keep
  their colour, and their conditions become amber marks;
- on their pages: Build's rings, Lending's "shut" and "defaulting" tags (on the map's buildings, UX5),
  the central bank's equity (a red figure in its top row, UX12).

## 6. The rows UX2 to UX13

Each has its brief in `docs/queue/UX<n>.md` (what it builds, its files, size, order and fit, the guard:
UiSteadyCheck and before-and-after pictures by `ui.PagePictures`). They are in the ship file's
`## queued`, in this order:

| row | what | size | after |
|---|---|---|---|
| UX2 | the frame: the map as the ground, the top strip, the panel, the path line, the wide sheet, keys | large | nothing |
| UX3 | the alert row and its model (`Alerts`), the dismissed set saved | medium-large | UX2 |
| UX4 | the needs sidebar, one reading of "served" | medium | UX2, ASH1 |
| UX5 | the map's hooks: a card on a building, a page lighting its buildings, a need's districts | medium | UX2 |
| UX6 | the icon set: an icon and a colour per building | medium | UX2 |
| UX7 | the build screen (mockup 9), its Wide catalogue (10); owns F3 | large | UX2, UX5, UX6, BMD1 |
| UX8 | the figures read one way: money, its sign and unit, the axes | medium | UX2 |
| UX9 | People, Services, Infrastructure, the Land office; owns F1 | large | UX2, UX8, ASH1 |
| UX10 | Sectors and Government; owns F2 | large | UX2, UX8 |
| UX11 | Finances | large | UX2, UX8, ASH3 |
| UX12 | the Bank, the central bank, Trade | large | UX2, UX3, UX8, CBK3, CBK2 |
| UX13 | Policy, History, Rankings | large | UX2, UX3, UX8, HO4 |

- ★ **The frame is split in three.** The brief expected one frame batch with the strip, panels, needs
  and alert row; UX2 keeps the frame and the strip (both UserInterface's window) and the alerts and needs
  are rows of their own, each with its own model, harness and (for the alerts) a saved field. UX2 is
  already the largest row.
- ★ **Every page opens in the wide sheet first** (UX2), today's page node unchanged, so the frame breaks
  no page and no figure moves; each page row then gives its pages their narrow form. Pages can therefore
  go in parallel, each its own file, as the brief expected.
- ★ **A format row (UX8)** the brief did not name: money's format crosses every money page, so it is done
  once before the page rows.
- **How the queued batches fit.** ASH1 (the Overview's POWER row, the small-town grid step) before UX4 and
  UX9, which draw its reading; ASH3 (the buyback card) before UX11; CBK3 and CBK2 (the central bank's
  dials) before UX12, which lays out the dials they leave; HO4 (the credit levers on Policy) before UX13;
  BMD1 (Build's lab descriptions, BuildMenuCheck's exit) before UX7; REU1's Real Estate page opens in the
  sheet like every page if it lands after UX2, and takes its narrow form by the page rows' rule. None of
  them waits on a UX row.

## 7. The pictures

`docs/studies/ux1/` holds about 12 MB: `pictures/` (36 of the audit's, 8.6 MB, PNG, unchanged so every
figure stays legible), `mockups/` (ten PNGs and their HTML, the frame's `lib/` with the two grounds, 3.7 MB),
the five audits and a README. The full set, 122 pages a city, stays on scratch (section 2).

## 8. The checks

- UiSteadyCheck alone: 33 reds on three runs, all F1-F3 (section 3), each covered by a ship-file row.
- `./batch-check.sh --fast`: green (after the interface's count in CLAUDE.md went to 56 and the helpers'
  mockups to LF); `sh tools/known-reds.sh UX1 docs/queue/UX1-ship.md`: exit 0.
- `./batch-check.sh UiSteadyCheck`: exit 3, known reds only (32 FAILED: pages that move 12, rebuild 1,
  grow 18, lose their place 1, of 122). The run before it showed F1 on Infrastructure > Freight as well,
  so Infrastructure's row is the screen's (section 3).
- `sh tools/pick.sh origin/main HEAD`: ALL (AllChecks changed). Its review line says "needed - threading:
  ui/UserInterface.java (UI-wide)": the change there is two package accessors beside `replayGoTab`
  (`railKeys()`, `pressRail(key)`, the rail's own `goHome`), no thread and no timer; the brief says no
  stranger's review, so none was run, and the dry-run mail says so.
- The dry run: (to fill.)

## 9. The calls

1. ★ Slots 2 and 9 dropped (format 66 draws a broken city); the playtest's city at two ages instead (section 2).
2. ★ F1 is the page rows', UX9, not the frame's (section 3).
3. ★ The frame split in three; every page first in the wide sheet; a format row (section 6).
4. ★ The alerts: a mark per kind, eight then "+N", a dismissed alert back a step worse or after 12 months (section 5).
5. ★ The sign: the city's sign as FM1 chooses it on every page; US$ only where two currencies meet (UX8).
6. ★ Industry's 18 groups in five families: Food, Metals, Making, Materials, Energy (the desk chat's list
   had chemicals; the game has none, and its refining is energy's).
7. ★ The build filters are the desk chat's, "Needed now", "Can staff", "All", plus "The city's own";
   mockup 9 draws the first as "Worth building".
8. ★ P4's alternative is a wide catalogue, not a bottom category bar with a tray: that layout is Cities:
   Skylines', and the brief asks for none of its look.

## 10. For the manual

Nothing a player sees changes in UX1 (a tool and a harness). When the rows ship, the manual's edition
needs: the frame (the map always behind; a rail button opens its page in the panel; the path line, Back
and Esc; Wide and the dimmed map; the number keys and Space); the top strip's six figures; the alert row
(left click opens, right click dismisses, when a dismissed alert returns, "+N"); the needs sidebar (what
a circle's ring means, a click opens its page); a building's card on the map; the build screen (the two
sets of categories, the families, the tiles' pink and violet counts, the filters, placing by the ghost);
and money's one format and sign. Not this batch's: `docs/manual.*` is untouched.

## 11. For Jerus: the picks

His part is the four picks, P1 to P4 (section 5; the pictures in `docs/studies/ux1/`). The rows are
written to the recommendations (P1 A, P2 A, P3 A, P4 A); a pick of an alternative changes UX2 (P1), UX4
(P2), UX3 (P3) or UX7 (P4), and nothing waits on him.
