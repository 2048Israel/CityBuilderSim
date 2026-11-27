# UX1 - the interface rework's study: the map as the ground of every screen

Jerus, 2026-11-27, through the desk chat. Next week (Nov 29 - Dec 5) he spends
a spare Claude week on a UI rework, run beside the other batches. UX1 is the
study that makes the rows for it, so it ships before then.

## His words (11-27)

- "next week we use that free reset to do a ui rework and expansion of ui, since ui can go along side the other batches"
- "there are no queue for UI"
- "before we had no map, and the ui was designed for a no map lifestyle, but now there is a map, and the ui is sitll designed to be played as if there is no map, so basically i was thinking of having the map as the main focus and then just clicking stuff to go deeper while the map still visible in the background, for inspiration you can use HoiV like for layout, doesnt hav eto be the same, infact do it differently but what i mean is that in herats of iron 4 the map is nearly always visible in the background. and also, during the ui rework its not just smaller ui that opens up with the map present, no no no, its a different ui that takes into account the map is still on the screen, sure you can click to open up more stuff you see how to do that, and basically it still shows the current ui info, but more and with more submenus. additionally have it so that also the ui work watches out for scrolling issues, and issues were the screen flickers or expands without stopping. also gotta improve the build screen, currently if you go to industry its a mess, too many buildings to make a selection. so basically also take a look at how city skylines handles its ui if you want more ideas. and im thinking, perhaps goinf for more icons, for individual buildings, and like on the build screen not the map, and with different colours, but somehow clearly shown whats yours to build. you can also rework the top to include or remove if you think nescessary and other stuff are relevant, also the needs you idk, perhaps instead its a sidebar? with just tiny circles and text or soemthing idk, but yes basically a full ui rework, that has the map as the main focus (although you dont actually rework the map itself) while still being able to easily nagivate the UI and it shows cleaner and can go more deep than the current UI, cause also alot of stuff has been added hasnt it."
- On the alerts, about 10:45: "the notifcations are hearts of iron style, aka appear on the top and you can click to open or right click to make disappear"

## What is there now (main, 0.9.217)

- **The interface.** 54 files, about 110,000 lines. The largest are
  UserInterface 7.5k, SectorScreen 7.2k, FinancesScreen 7.2k, BuildScreen
  6.7k, PolicyScreen 6.6k, MapView 6.4k, PeopleScreen 4.9k and BankScreen
  4.8k (`docs/map/README.md`).
- **The window is THE RAIL** (UserInterface, 0.7.21).
  - Twelve buttons run down the left edge, each an outline icon over its name,
    coloured by area (`areaOf()`): Build, Land office, People, Services,
    Infrastructure, Sectors, Government, Finances, Bank, Trade, Policy and
    History. Menu sits at the foot.
  - Each button opens a full page, which covers the map.
- **The map** is reached through the minimap stamp above Menu (MM2, 0.7.134),
  which opens the land office's map.
- **The buildings.** `buildings.json` has 316. `Icons.java` draws outline
  icons on purpose: the Tab record's comment says why there is no emoji.
- **The rules any rework keeps.**
  - Everything is drawn through `Kept`: the redraw sets the month, and the
    scroll never moves.
  - Every figure comes through a public getter, and the screen recomputes
    nothing.

## What the study delivers

1. **A picture tool in `tools/`.**
   - It is a headless JavaFX run: it loads a copied slot, plays N months, and
     saves a PNG of every page and sub-page at 1920x1080.
   - The desk chat's scratch driver already does this; copy its approach. It
     uses `GameFiles.scratch`, `Game.loadGame`, `UserInterface.start`,
     `Scene.snapshot` and `-Dprism.order=sw`, and needs no one at the screen.
     It is at
     `C:\Users\Jerus\AppData\Local\Temp\claude\b3caf16bc6c0fc69\6d1675b7-5759-4631-8e1c-fdaa808c8090\scratchpad\game\src2\ham\citybuildersim\ui\CbShot.java`.
   - Every UI batch afterwards uses it for before-and-after pictures.
2. **A stability harness**, the guard for every UI batch after (his "scrolling
   issues", "flickers", "expands without stopping"). It asserts:
   - every page redrawn with nothing changed keeps every node's bounds and its
     scroll position;
   - a month's redraw keeps the scroll position;
   - nothing grows from one redraw to the next;
   - no node is rebuilt when nothing changed.

   Run it on today's pages first, and put the faults it finds in the audit.
3. **The audit.**
   - It covers every page on two or three cities: copies of his slots, a young
     town among them.
   - For each page: what it shows and what is wrong on it, meaning crowding,
     inconsistency, what is hard to find, what is missing, and the stability
     faults.
4. **The new interface, drawn as mockups over a real picture of the map.**
   - The map is the ground of every screen, and the interface is designed for
     the map being there. It is not today's pages shrunk.
   - It shows everything today's pages show, with more depth and submenus.
   - It covers:
     - the top strip: what it keeps, adds or drops;
     - the needs as a sidebar of small circles and words (his idea);
     - how a player goes deeper and comes back while the map stays in view;
     - where the wide tables and statements go;
     - **the alerts, settled by him:**
       - a row of small alert marks at the top of the screen, over the map;
       - a left click opens the alert's page or detail;
       - a right click dismisses it;
       - the study decides whether a dismissed alert returns when its
         condition worsens or comes back, how many show before they group,
         and which of today's alerts move there (the needs, the bank's
         capital requests, a sector's holds);
     - **the build screen:**
       - Industry broken into groups a player can choose from;
       - an icon and a colour per building;
       - plainly which buildings are the city's to build and which the
         market builds.
5. **The batch plan:** rows UX2 and onward for the UI section, in order, each
   with its size.
   - **Expected, for the study to confirm or replace:**
     - a frame batch first: the map as the ground, the top strip, the panels,
       the needs sidebar and the alert row;
     - then one batch per page, run in parallel, since each page is its own
       file;
     - the build screen and the icon set as rows of their own.
   - **How the queued batches that touch screens fit:** ASH1 (the Overview's
     POWER row), ASH3 (the buyback card), CBK2 and CBK3 (the Central bank
     page's dials), HO4 (the credit dials on Policy), REU1 (the Real Estate
     page) and BMD1 (the Build menu).

## Inspiration and scope

- **Inspiration.** Hearts of Iron IV, where the map is nearly always on screen
  behind everything, and Cities: Skylines, for the build menu.
  - Take their ideas only, never their look or layout.
  - He says "do it differently": the design is the game's own.
- **Out of scope: the map's own drawing** ("you dont actually rework the map
  itself"). If the design needs hooks into the map, they are named as rows of
  their own; for example, clicking a building for its card, or tinting the
  districts short of a need.

## The desk chat's starting ideas, for the study to weigh (none is a decision)

- **The rail stays** and opens a panel over part of the map.
  - A path line inside the panel ("Finances > Debt > the 30-year") shows how
    deep you are.
  - A "wide" button opens big statements over a dimmed map, and one click
    comes back.
- **The needs sidebar:** a dot per need, green, amber or red, with its word.
  - Hover shows the figure.
  - A click opens its detail and tints the districts where the need is short.
- **A map card:** clicking a building or district opens a small card beside
  it, with a way into its sector's pages.
- **The build screen:**
  - a category bar, then groups (Industry by chain: food, materials, metals,
    chemicals, energy, manufacturing), then cards;
  - filters: "needed now", "can staff" and "all";
  - each card shows its icon in its area's colour, its cost, months and jobs,
    and the advice's star;
  - the city's own buildings carry the city's mark, and the market's are
    drawn as the market's.
- **The icons:** one per building in today's outline style, a base glyph per
  group plus a mark per building, coloured by area.
- **Keys:** number keys for the rail, Esc to close a panel, Space to pause.

(The desk chat first proposed a corner feed for the alerts. His 10:45 words
replace it with the row at the top.)

## For Jerus

- **The pictures** and the mockups go in `docs/studies/ux1/`. The desk chat is
  mailed the folder and sends them to his phone.
- **His part is the picks, and only the picks.** He picks from the study, and
  nothing else in it is his call.
  - For each place where a real choice exists, the study draws its
    recommendation and one or two alternatives.
  - The rows are written to the recommendation, and his picks, when they come,
    adjust them.
  - Nothing waits on him.

## Order and lane

- **Lane 3** (Claude, design-heavy).
- **It waits on nothing in flight.** It changes no page, so the batches that
  touch screens go on beside it. Its UX rows say how they fit.
- **The manual is not this batch's.** An edition comes only at a milestone or
  at Jerus's request (docs/orchestrator.md). The notes carry a "For the
  manual" section, and `docs/manual.*` stays untouched.

Unversioned, a study (like RES1): the tool and the harness change no game
behaviour. No ensemble, no stranger's review.
