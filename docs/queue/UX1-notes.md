**Resume here (16:10, 11-27):** the picture tool works (three cities pictured on scratch); UiSteadyCheck written and first run on today's pages; next: the audit (helpers, page by page), the mockups over the real map, the rows UX2 onward, the ship file.

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

| slot | city | month | people | format | how it was loaded |
|---|---|---|---|---|---|
| 1 | "Halifax" (Rotterdam), 1909 | 114 | 1,888 | 67 | as saved: the young town |
| 2 | Ashbourne | 241 | 7,998 | 66 | patched to 67 on the scratch copy, pictures only |
| 9 | "Claude Optimization" (dojha) | 6,680 | 50.2 M | 66 | patched to 67 on the scratch copy, pictures only |

★ Slots 2 and 9 are format 66, and today's floor is 67 (RES3: typed leases and held monetary inputs a
66 city lacks). The study needs a town, a mid city and a big one, and these are his; so the scratch copy's
`saveFormat` word was set to 67 and the copy loaded, played one month and pictured. The figures on their
pages may differ from what a true 67 city would show (slot 2's load logs one caught
`Unknown housing class Key[recipe=0, dwelling=legacy]`); the layout, which is what the study reads, does
not. Nothing of this is in the tree. His saves folder was only read (cp to scratch).

The full set of pictures: `C:\Users\Jerus\AppData\Local\Temp\cbs-UX1-scratch\pics\slot1`, `slot2`, `slot9`.
