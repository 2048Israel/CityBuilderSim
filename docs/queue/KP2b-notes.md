# KP2b - The kept screens' review follow-ups (0.9.31, 2026-11-06)

**WHERE IT STANDS** (2026-11-06, 09:40): ready, see the ship file. The three slices are in and reviewed (B 322ec4fa,
A 6f62f1c2 with Opus's forest law card, C 01516d88 with Opus's matrix columns and section 92's letting go); KP7 0.9.29
and R3 0.9.28 merged in (2b3bcf74); `--pick` on the merge exit 3, main's recorded KeptCheck red only (§5); the chrome
measured quiet, ABBA against 0.9.17 (§5).

The batch's record. `QUEUE.md`'s KP2b row: the stranger's threading reviews at the gates of KP2, KP3, KP4, KP5 and KP6
(and KP6's notes §7, the sweep it left for this row). Lane 2 (screens), after KP7, built on `origin/batch/KP7` at its
ship file (d9043e16). Opus wrote the briefs, reviewed each diff and committed; two Sonnet 5.5 slices at xhigh wrote the
code, A in a worktree of its own (`kp2b-a`, the sliders and Policy) and B in this one (the cost sweep). Nothing outside
`ui/` and `KeptCheck` changes: the default playtest's trace cannot move, so no ensemble (EN1).

## 1. The row, item by item

| item (the row's file and line) | on this tree | how |
|---|---|---|
| `Pieces` funnel re-binds its fill's width every kept draw | mended (B) | bound once, when the fill is new, to the holder's width times a share kept in the fill's properties; a draw sets the share |
| `Ladder` sets a reading's text twice a draw | mended (A) | `said()` works out the words and style first; `Kept.label(text)` and one `setStyle` |
| `Kept`: a slider greyed mid-press keeps `held` | mended (A) | `grip()` adds a `disabledProperty` listener once, beside the scene's: greyed, the node lets go (and a slider's `valueChanging` ends) |
| `CapChips:202`: the yield box keeps "4.5" and the caret after Enter | **already gone**: KP5's slice D (`CapChips.java:114-120`, a focused box that differs from what the page shows is set, caret at the end) | KeptCheck 66 `boxEnter` fires Enter in Borrow's and the rollover's yield boxes: the same node, "4.500" (`CapChips.yieldText`), the caret at the end, the focus kept |
| KeptCheck's whole kept chrome's month, quiet, ABBA against 0.9.17 | **not yet** (§5) | |
| set twice a draw: `StatementView:363-369`, `Statement:269/272/386`, `SectorScreen:1266/3585/4203` | mended (B) | the note cell, `bookLine`, `statementDisclosure` and the mark; the ratio and rule chips, the `so` caption, the decision card, the KEPT tone (`limitCell(..., noteTone)`) |
| `SectorScreen:6376` `berthRow`'s empty caption keeps its old wrap width | mended (B) | `berthScope()`: a berth kind and none are scopes of their own (`SectorScreen.java:6408`), as KP4's `billRow` |
| `LandScreen:1225`'s comment on the oil helpers | mended (B) | the comment says what they do since KP4. **No landmass fixture**: `Landmasses.rows()` comes from plots owned on another landmass, and no scratch city has ground on one |
| KP5's: `BankScreen` `doorCard` and the sheet key, `FundScreen` `handStyle`, `FinancesScreen` the rescue card and the window abroad shut, `Pieces.pageHead`; a `new Insets` each draw (`frameOver` in Finances and Bank, `Pieces`' rate ladder, the desk's `GridPane.setMargin`) | mended (B) | `doorCard(go, rows...)` sets the style once; `handStyle` is gone (`pageHeadTo` makes the fund's name a way back); the insets static finals, set only where they differ (`Pieces.pad`, `gridMargin`, `hMargin`, `vMargin`) |
| KP6's: `PolicyScreen:3139-3143` `strictPart`'s line at the city's setting | mended (A) | the dial built first, the line from `strictAt(dial.shown())`: a held thumb's words |
| KP6's: KeptCheck 78 holds only the first slider | mended (A) | `holdDial` also holds the policy rate's target ladder, HOW STRICT and a zone's ITS TAXES' second dial (the first sector whose card moves: Retail at month 2,674; Agriculture pays no sales tax there) |
| KP6's: `ZoningScreen:387-392` clears each picker chip's icon every draw | mended (B) | the icon handed to `stepChip` where the chip is made |
| KP6's notes §7: `TradeScreen:703`, `PolicyScreen:961`, `ZoningScreen:827` built on `getStyle()` | mended (A: Policy's; B: Trade's and Zoning's) | set once, whole (`PolicyScreen.doorCard(go, rows...)`, `TradeScreen.doorCard(go, rows...)`, `stepChip(..., picked)`) |

**KP7's gate review's items** (taken into the row after KP7's merge; slice C, 01516d88):

| item (on KP7's tip) | how |
|---|---|
| `RankingsScreen.java:1215`: the hover band lit again from the last `pointerY`, so after a wheel scroll a month lights the row the pointer was over before it | **what JavaFX sends**, seen in a scratch driver on a shown stage: a `ScrollEvent` on the canvas bubbles to the scroller, which moves the page; no `MOUSE_MOVED` follows; while the same picture stays under the pointer nothing is sent at all, and when another node comes under it the pulse sends EXITED and ENTERED, never MOVED. Mended so the band follows the pointer, as a list's hover in a browser: `RowsPicture` keeps the pointer's scene y, and a listener on its scene transform (added once, in the constructor) lights the row under it; `onMouseEntered` lights it when rows slide under a pointer from elsewhere. KeptCheck 93 |
| `grey()` sets a style on top of chipStrip's (`:580`) | `strip()` takes a spent test and draws through `Pieces.chipStripSaying`; a spent chip that is the one chosen keeps its raised ground and accent edge, as the look set on top did |
| a `new Insets` each draw (`:396`, the pin, the browse margin) | `Pieces.FRAME_ROOM`, `PIN_ROOM`, `BROWSE_ROOM` |
| `ColumnConstraints` each draw in People's `affordabilityGrid` | made once in each grid's life and kept in its properties (Opus: slice C made one static list for every grid, and a constraint holds each grid it is added to); `Kept.Grid.stage` still clears and re-adds them each draw, a `Kept.java` mend for KP2c |
| `RowsPicture`'s cursor reset by `Kept.handBack` and set again (`:1182`) | the hand in its style, set once in the constructor (`handBack` resets only a code-set cursor) |

Slice C also took Policy and `Ladder` out of `KeptCheck.NOT_KP2B`: their 32 writes over 21 pages (the frame padding,
the "paid nothing" fold, two opened cards' margins, the marks lane's padding) are 0. 92 now draws Household money (12
pages: three family cells and three rows below the rule, each way; 0 before and after) and Rankings (about 37 pages,
17 to 24 writes each and 83 with Browse all open before; 0 after).

Past the list, the same kind, in the files named: Policy's `shutBox` (A); the forest law's card (`ZoningScreen.lawCard`,
Opus: a door made so, the old `PolicyScreen.doorCard(VBox, go)` gone with its last caller; 92's Zoning ground page
counts it, 0 since Policy is counted); Zoning's breadcrumb and the
multi-dial card's title, Finances' quote and expect cards and `gainLine`, the land office's title and ore card, the
books table's heads and piece names, the button padding at `Pieces.java:1400` (B). `PartnersPage` changed by one line,
a caller of `TradeScreen.doorCard`.

**One look moves**, an old quirk mended: the last cell of a vitals strip that is a door (People's GOING SHORT) took its
dividing rule back with the hover (the 0.7.27 comment says so) and kept it after the pointer left; it is drawn as a last
cell now, at rest and under the pointer (`Pieces.cellLook`).

## 2. The slices

- **A** (6f62f1c2): `Kept`, `Ladder`, `PolicyScreen`, KeptCheck 66, 78 and 91, five bench helpers. Each fixture run red
  against the tree before its mend (seen): 91's held slider stayed held; 78's reading heard two text and two style sets a
  draw; HOW STRICT said the city's step under a held thumb; the door cards 10 and 8 style sets on the hub and Taxes.
  `boxEnter` is green on the tree as it was, as it should be: it is KP5's mend's fixture.
- **C** (01516d88): KP7's items and Policy's last writes (above); 93's band red before its mend ("the band stayed on the
  row the pointer was over before the scroll"), its cursor 2 writes a draw before. Its first whole KeptCheck died of an
  `OutOfMemoryError` in 92's 51st household page: 92 now takes three cells and every tenth measure, and lets each tab go
  before the next (`letGo`: the tab forgotten and `KeptBench.release()`, Opus). KeptCheck's live set is about 1.95 GB of
  its 2 GB by section 92 on this tree (§6).
- **B** (322ec4fa): the sweep, `KeptBench.redrawSets` and KeptCheck 92. Before the mends every one of 105 pages wrote
  15 to 102 times on a second draw of the same month; after, none on 111 (the premises added: a line's page, Close it
  now, a security, a nation, the land office, Borrow abroad with the window shut).

## 3. The ★ calls

None.

## 4. The threading, for the gate's review

**One thread**, as KP2's §4. Nothing new runs off the FX thread. Added once in a node's life: `grip()`'s disabled
listener (inside the block its filters and scene listener are added in, under its own marker); the funnel's binding
(`Kept.isNew`). `strictPart`'s line is reached by the dial's drag through a one-cell array the latest build fills: the
Ladder's listener calls the latest build's `track` (KP2 §4), so a drag sets the kept line of this draw. A greyed
slider's `setValueChanging(false)` in the listener: no screen listens to `valueChanging` (`ReplayMode` reads it on its
own slider, which is not gripped). `RowsPicture`'s scene-transform listener runs on the FX thread with the layout that
moves it, and only lights its own band. The fixtures run inside `fx()` but 92's `holdSector` and two premise reads, on
the harness thread as `sectors()` already reads the model, and 93's wait for the saved cities' read (87's pattern).

## 5. What checks it

- KeptCheck 66 (`boxEnter`), 78 (`holdDial`), 91 (`greyedWhileHeld`, `doorsStyledOnce`) and 92 (`kp2bSets`): each
  section's condition caused by its fixture, red before its mend (§2).
- KeptCheck on A and B together: exit 3, main's recorded red only ("a month's redraw after each choice hands back nine
  in ten..."); with Policy counted in 92 it was 22 reds, Policy's last writes, which slice C mended. On C: exit 3, the
  same red alone.
- **The chrome measure** (the row: 9.34 ms on KP3's merge against main's 8.02, `KEPT_ALL_CHROME_MS` 8.0): KeptCheck
  section 13's body alone over the city KeptCheck makes (month 605), 20 rounds a run, the median of rounds 11-20 (the
  JIT still settles to about round 10), compiled against each tree, ABBA, 09:29-09:31 with no gate on the PC (one other
  batch's suite running; slice C's hung KeptCheck, below, stopped first):

  | run | kept, median ms (min-max) | built new, median ms |
  |---|---|---|
  | A 0.9.17 | 2.46 (2.05-3.71) | 12.73 |
  | B KP2b | 3.03 (2.55-4.58) | 13.28 |
  | B KP2b | 3.02 (2.62-5.72) | 13.41 |
  | A 0.9.17 | 3.48 (2.49-4.72) | 16.20 |

  About 3 ms both ways (A 2.97, B 3.03 on average, the A runs either side of B's): no cost from KP3 to here. The 9.34
  and 8.02 were the gate's load and section 13's place in the whole KeptCheck, where it runs warm but beside 12
  sections' garbage. No budget moved.
- `--pick` on the merge with main (KP7 0.9.29, R3 0.9.28): 20 harnesses, exit 3, main's recorded KeptCheck red alone.
  It asks a money review (`ui/BankScreen.java`, `ui/FundScreen.java`): their change is look only - door cards made with
  their style set once, a key swatch's ghost look made where it is made, constant paddings, the fund's name a way back
  through `pageHeadTo` - no figure, no model call.
- **A hung run, mine:** slice C's first KeptCheck (07:51), out of memory in 92, never exited: its JVM held 2.9 GB and
  this worktree's log until 09:26, beside two gates. Stopped by hand (Opus).

## 6. Found in review, left

- **The sweep's edge** (`KeptCheck.NOT_KP2B`, 209 writes over 92's pages, named and not counted):
  `UserInterface.scrolled()`'s `new Insets(0, 0, PAGE_FOOT, 0)` on every page each draw (`UserInterface.java:4433`),
  Services' door cards and frame padding, Infrastructure's. Queued as KP2c.
- `Kept.Grid.stage` clears a kept grid's column constraints and the page adds them again, each draw (People's matrix and
  about 13 grids in Bank, Finances, Sectors, Trade, `Pieces` and `MapView`); 92 does not see constraint lists. KP2c.
- **KeptCheck's heap**: its live set is about 1.95 GB of `AllChecks.HEAP_MB` (2 GB) by section 92 and 1.97 GB at its
  end, on this tree; section 92 lets each tab go, but the sections before it hold most. A margin to watch at the gate;
  KP2c to look at what the earlier sections keep.
- `Region.setPrefSize` and `HBox.setMargin` ask the parent for a layout even when nothing changes (a probe: `Pieces.
  makeIcon`'s `setPrefSize(24, 24)` about 1,450 times over 92's pages); 92 does not count them. KP2c.
- `Kept.handBack` makes a greyed control enabled and the page greys it again, each draw (the `:disabled` pseudo-class
  twice). KP2c.
- KP4 §7's `Levers.dialCard` "dial or none" (★C there, "recommended into KP2b") is not in the row: not taken here.
