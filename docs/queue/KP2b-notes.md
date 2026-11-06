# KP2b - The kept screens' review follow-ups ({next}, 2026-11-06)

**WHERE IT STANDS** (2026-11-06, 06:45): the two slices are in and reviewed (B 322ec4fa, A 6f62f1c2 with Opus's forest
law card), KeptCheck on them exit 3, main's recorded red only; pushed with no ship file. Left: KP7 onto main and merged
in, KP7's gate review's items (§1, after the table), `--pick` on that merge, the quiet chrome measure (§5), then the
ship file. A gate's hard timing held the PC from
06:40; what is not done by 07:00 the next day's orchestrator carries.

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

**Taken in from KP7's gate review** (the orchestrator, 2026-11-06, into the row after KP7's merge), **not yet done**; line
numbers on KP7's tip: `ui/RankingsScreen.java:1215`, the hover band lit again from the last `pointerY`, so after a wheel
scroll (no mouse-moved after it) a month landing lights the row the pointer was over before the scroll (cosmetic; whether
JavaFX sends a move on a scroll is to be checked first); and KP7's repeats of this row's kinds: `grey()` building a style
on a style (`:580`), a `new Insets` each draw (`:396`, `measureLine`, the browse and bar margins), `ColumnConstraints`
each draw in People's `affordabilityGrid`, `RowsPicture`'s cursor reset when handed back and set again each draw
(`:1182`). One Sonnet slice, after KP7's merge, with 92's pages widened to Rankings and Household money.

Past the list, the same kind, in the files named: Policy's `shutBox` (A); the forest law's card (`ZoningScreen.lawCard`,
Opus: a door made so, the old `PolicyScreen.doorCard(VBox, go)` gone with its last caller); Zoning's breadcrumb and the
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
own slider, which is not gripped). The fixtures run inside `fx()` but 92's `holdSector` and two premise reads, on the
harness thread as `sectors()` already reads the model.

## 5. What checks it

- KeptCheck 66 (`boxEnter`), 78 (`holdDial`), 91 (`greyedWhileHeld`, `doorsStyledOnce`) and 92 (`kp2bSets`): each
  section's condition caused by its fixture, red before its mend (§2).
- KeptCheck on the two slices together: exit 3, main's recorded red only ("a month's redraw after each choice hands back
  nine in ten..."). With Policy counted in 92 (taken out of `NOT_KP2B`) it is 22 reds: 1 to 4 writes on each of Policy's
  19 pages beyond the row's items, from `PolicyScreen`; `NOT_KP2B` names it again (§6).
- **The chrome measure** (the row: 9.34 ms on KP3's merge against 8.02, `KEPT_ALL_CHROME_MS` 8.0): not run yet. The
  driver is section 13's body alone over the city KeptCheck makes, six rounds, compiled against each tree; 0.9.17 is built
  in a worktree of its own (`.claude/worktrees/kp2b-m17`). It needs a PC with no gate on it: one ran its hard timing
  from 06:40. No budget moves either way.
- `--pick`: not run yet (the top).

## 6. Found in review, left

- **The sweep's edge** (`KeptCheck.NOT_KP2B`, 292 writes over 92's pages, named and not counted):
  `UserInterface.scrolled()`'s `new Insets(0, 0, PAGE_FOOT, 0)` on every page each draw (`UserInterface.java:4433`),
  Services' door cards and frame padding, Infrastructure's, and Policy's 1 to 4 a page. Queued as KP2c.
- `Region.setPrefSize` and `HBox.setMargin` ask the parent for a layout even when nothing changes (a probe: `Pieces.
  makeIcon`'s `setPrefSize(24, 24)` about 1,450 times over 92's pages); 92 does not count them. KP2c.
- `Kept.handBack` makes a greyed control enabled and the page greys it again, each draw (the `:disabled` pseudo-class
  twice). KP2c.
- KP4 §7's `Levers.dialCard` "dial or none" (★C there, "recommended into KP2b") is not in the row: not taken here.
