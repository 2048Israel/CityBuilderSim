# I12 - the materials pictograms drawn (0.7.143)

## 0. Steps done
- Read CLAUDE.md, the brief (docs/queue/I12.md) and fixI11-notes.md; worktree /home/user/wt/I12, branch batch/I12.
- Finished the previous session's `IndustryPicture.java` (never compiled: two errors), then reshaped its layout where the
  headless render showed collisions; added the card to ui/SectorScreen, IndustryViewCheck 12, 0.7.143, the CLAUDE.md row.
- Every build and JVM through the gate's run.sh (one JVM at a time); a words probe rendered headless under xvfb-run.

## 1. What was built
- **`IndustryPicture`** (new, model package, pure; ★1): each of the nine industries' month laid out to scale from
  `IndustryView.of(Game, Sector)`.
  - `trace(View)`: the month as flows, node to node, in the good's own unit and in the picture's - each product's made split
    over the kinds that made it at their run (`Unit.makes()`), what a works kept for itself to the kinds that use it, the
    rest into its band; each feed's home, imported and old-metal parts over the kinds that use it (the lorries' diesel to
    the lorries); each kind's dig from its field (its site's `dig()`); each band to its takers (each buyer here, into the
    warehouse, lost, abroad), and from the warehouse where the month sold more than it made. Coal's wash, Quarrying's carry
    and quarries on ANY, Forestry's salvage each by its own rule (★3).
  - `picture(Game, View, width, height)`: the columns - sources (a mine's fields as vessels filled to what is left, with the
    bar of what each dug; a works' feeds by origin, imports hatched; the salvage cut; the old steel), the works in ranks
    (`ranks()`: a kind right of the kinds that hand it a captive good, the wash plant right of the underground mines), the
    tank of what left the works under the industry's roof (PIT, CAMP, FURNACE, KILN, MILL) with what left the warehouse
    under it, the takers - one scale, the largest at which every column fits; a flow that skips a rank runs as a band
    through that rank's column (★4); the headings, the note and the power line; words, icons, tips. `HEIGHT` 420.
  - `strip(View)`: THIS MONTH, CELL BY CELL - the fields or the forest and where it settles, the products, the old metal, the
    power, at most six.
  - Words: `headWords`, `subWords`, `scaleWords`, `emptyWords`, `noteWords`, `unitName`/`runLine`/`gateLine`/`unitTip`,
    `bandName`, `fieldName`, `lastsWords`; `widthOf(Words)`, an upper bound on a line's width (★6).
- **`ui/SectorScreen`**, OPERATIONS · THE MATERIALS INDUSTRIES (0.7.143): `operationsPage()` adds `industryCard()` for the
  nine (`IndustryView.covers()`), as Refining's adds its card: the heading, its line, the (i) (`INDUSTRY_INFO`) and the
  scale; `IndustryPane` lays the picture out at its own width; `paintIndustry()` paints it with the refinery's painter
  (`paint()` now shares `shapes()` with it - the same nodes in the same order, ★11) and `iconOf(IndustryPicture.Mark)`;
  `industryStrip()`/`industryCell()` the strip. The generic flow card and the rest of the page follow, unchanged; every
  other sector's page as it was.
- **IndustryViewCheck 12** (new section) and lines in 9 and 10 - section 5 below.
- `GameVersion.VERSION` 0.7.143 with its entry; CLAUDE.md's row for IndustryPicture (and IndustryView's row's last line).
  README.md lists no view classes, so it has no row to add.

## 2. Departures, with reasons
- The words probe reads IndustryViewCheck's three towns (works, copper, wood - they cause all nine industries) at months 3,
  6 and 12, not the LongPlaytest city: a long playtest to the months where its industries stand would take far longer
  than the probe's minute, and the check's towns are the fixtures built to cause each part of the picture.
- `picture()` takes the Game (to name the parties, `IndustryView.partyWords()`), so there is no `View.picture()` delegate.

## 3. Premises changed (before -> after) and new assertions
- None moved. Section 9's "read twice" now reads the pictures and the strip too (`words()`); section 10 adds that a loaded
  twin's picture is empty and says a month must run, and a month on is drawn. Section 12 is new: every ribbon attached;
  each pair's ribbons its flows' quantity times one scale and each node's the view's figures (band, taker, source,
  lorries, the old steel); every label's figure the view's (bands, vessels, sources, kinds and runs, takers, power, the
  note, the cells); nothing off the frame at 1,200 x 420 (the 1,389 x 868 window's card) and every line of words within
  its room at `widthOf()`; a fixture line (ranks, bands through, hatching, vessels); the five roofs; nothing drawn with
  nothing standing.

## 4. Trace movement
- None expected, and the playtests were not run here (the orchestrator's gate runs them): nothing in the month calls
  IndustryPicture (it is called by ui/SectorScreen and IndustryViewCheck only), IndustryView and every other model class
  are untouched, and GameVersion changes only its string.

## 5. Checks
- `run.sh build`: 0 lines (model and interface). Targeted suite `industryview sectorflow refineryview oilview`: 4 of 4
  passed (21 s). IndustryViewCheck 12: 106 ribbons in 9 pictures, 134 node sides, 134 labels, 9 pictures on the frame,
  4 pictures in ranks, 18 bands through a column, 19 hatched ribbons, 7 vessels, the five roofs.
- BuildMenuCheck: 139 of 139. tools.Stale: 0 firm (soft only, after the maps were regenerated).
- The words probe (scratch-i12/probe/: `ProbeTowns`, `ui.ProbeI12`, run by scratch-i12/probe.sh under the JVM lock and
  `xvfb-run`): 25 pictures at months 3, 6 and 12, painted by `SectorScreen.paintIndustry()` at 1,200 x 420 and measured
  with JavaFX's own text bounds - no word off the frame, no word over another word, an icon or a works' box. Plex Sans
  measured .55 of its size a letter on average, .75 at most (a line of capitals): `widthOf()`'s .6 and .74 bound it.
- Every string the cards draw is in `fixI12-words.txt` beside these notes (1,039 lines: heading, picture words in
  painting order, strip cells, and at month 12 every tip). The nine cards rendered headless are in `png/` (a 48-colour
  palette, 205 KB; full colour in /home/user/wt/scratch-i12/final/). Iron & Steel's and Cement & Concrete's are month 6:
  by month 12 the works town had shed them (its towns stop the old steel arising after the check's three months).

## 6. ★ calls
- ★1 A sibling class, `IndustryPicture`, not `IndustryView.picture()`: I11's view is figures (883 lines) and the picture is
  a model of its own - the trace that splits each good over the kinds, and the layout - about 1,550 lines; RefineryView
  holds both in one file, but here keeping them apart leaves I11's view and its check's premises untouched.
- ★2 Ribbons in tonnes of the good, the forest's in cubic metres; copper is drawn in tonnes of the good, not of copper
  contained as the mockups' line says - the model holds no copper content for a good (the concentrate's 30% is a comment).
- ★3 The model has no rows per kind of building, so the picture attributes: a good's month split over the kinds in
  proportion to what each made or used at its run; Coal Mining's coking coal the wash plants', worked back at
  CoalMining's yields; a quarry on ANY digging the stone of the field the packing puts it on; the salvage cut half sawlogs
  (Forestry.SAWLOG_SHARE). The nodes the view has figures for (bands, takers, sources) are held to them; the split between
  kinds is the picture's.
- ★4 A flow that skips a rank runs as a band straight through that rank's column (RefineryView's rule; `passKey()`),
  placed in the column by the height it comes from: the ore to the blast furnace past the coke ovens, the coke to the tank
  past the furnace. Without it a ribbon passed behind a works' box it does not enter.
- ★5 Three lines over a works - its name and count, its run (and those on site), one more in a word ("one more: the
  money", "one more pays 1.43% a month") - where the previous session's two lines ran past the next rank; the plan's
  whole sentence and the last one's earnings are in the box's tip.
- ★6 The ranks stand from 70 px right of the sources' bar to 130 px before the tank, and a rank whose words would run into
  the next rank's words or boxes is moved up or down, whole (`apart()`), measured by `widthOf()`, an upper bound on IBM
  Plex (capital .74 of its size, any other letter .6). A band through a column is no obstacle: words read over it on
  their halo.
- ★7 A part under half a unit (`LEAST_PART`) is not drawn - its figure would read "0 t" - nor named in a source's line, nor
  the old metal's note and cell: I11's ★11, Iron & Steel buying a sliver of its own scrap ("Iron & Steel · 0 t").
- ★8 The note line (the wash, the carry, the crews' cut, the old metal's month) starts at the frame's left edge; the power
  line stands over the tank, moved left as far as its words need to end inside the frame (Iron & Steel's and the pulp
  mill's are long).
- ★9 A field's cell is "<Type> fields" ("Limestone fields"), apart from the product's ("Limestone").
- ★10 The warehouse's taker line is its goods alone ("Into the warehouse" says nobody took them); a source's line names its
  first seller and "+N" more, the tip each seller with what it sold.
- ★11 `SectorScreen.paint()` (Refining's) now builds its shapes through `shapes()`, shared with the materials painter -
  the same nodes in the same order, so the refinery's page is unchanged.
- ★12 The picture's unit, with nothing made yet, is the sector's own goods' (Forestry with a crew on site reads cubic
  metres, not tonnes).
- ★13 `TANK_TO_TAKERS` 60 (the WIP's 110; the mockups' 44 to 60), `FIGURE_ROOM` 56, `FOOT_ROOM` 8 under the tank so its
  foot stays inside the frame when the tank is the tallest column.

## 7. Found on the way / not done
- Not done here: the playtests (the gate's); the picture at the 1,280 window (`LEAST_WIDTH` 1,000 is RefineryView's, the
  layout runs there, but only 1,200 is checked).
- In the check's works town, Iron & Steel and Cement & Concrete stand at months 3 and 6 and have nothing standing at
  month 12 once the old steel stops arising (the probe plays on without the check's `each`); Copper Refining's smelter
  is gone by month 12 and it runs on imported anode. Fixture behaviour, read off the pictures, not investigated.
- The Wood Products picture reads "sells 13,500 kW to the grid · buys 526 kW from it": the pulp mill sells its whole make
  while the other mills buy - the view's per-building power (I11's section 6 holds made - drawn = sold - bought).
- For the docs pass: the maps were regenerated (IndustryPicture's page is new); harnesses.md carries IndustryViewCheck 12.

## 8. For Jerus on the PC, at 1,389 x 868
- Open Sectors, then each of the nine - Iron Mining, Iron & Steel, Coal Mining, Quarrying, Cement & Concrete, Copper
  Mining, Copper Refining, Forestry, Wood Products - on Operations: the picture card is first, the old flow card under it.
- Compare with `png/` beside these notes (the check's small towns, so the figures are small) and the words in
  `fixI12-words.txt`. Look at: a works in two or three ranks (Iron & Steel's coke ovens then the blast furnace; Copper
  Refining's smelter, refinery, rod mill) and the bands through a column; the roof over each tank (pit's headframe,
  Forestry's camp of logs, the furnace's stack and flame, the kiln's cone, the mill's saw-tooth); the hatched imports;
  the fields' vessels; the tips on a works (what the last one earns, one more in the plan's words), on a band and a taker;
  the strip of cells; that nothing runs off the card or over another word with the real fonts at your scaling.
- His calls: ★2 (copper in tonnes of the good) and whether the three lines over a works (★5) read well.
