# UP7 - the speed reading as the reached speed alone; the drawer's bar and the construction tab kept ({next}, 2026-11-06)

**WHERE IT STANDS** (2026-11-06): ready, see the ship file. The code in and reviewed (b1f1470d, 7babacf3). Built on
`origin/batch/UP6` at its ship file (2c8040a9, RL1); UP6 landed and main (c7ad4d9a) merged in (9be237da); `--pick`
on the merge exit 3, main's recorded MapCheck and KeptCheck reds only; `git merge-tree` with origin/main clean.

The batch's record. `QUEUE.md`'s UP7 row and [UP7.md](UP7.md): Jerus's call on UP2's ★1 (2026-10-23, "Reached
speed alone, amber"); UP2's ★2-★4 stay as built. Lane 2 (screens). Opus wrote the brief, reviewed the diff and
committed; one Sonnet 5.5 slice at xhigh wrote the code (`target/deepseek/brief-UP7.md`). No model change, no saved
field, no format bump; the default playtest never draws the window, so its trace cannot move: no ensemble (EN1).

## 1. The brief, item by item

| item | how |
|---|---|
| 1. the reached speed alone, in amber, at one width | `ClockPace.reached(rungLabel, rung)`: the reached text ("39×", "0.4×") or null (keeping up, a window not yet full, the clock stopped); `reading()` is reached or the rung. The header's label (`clockBlock()`, `paintSpeed()`) takes its words and its colour from that one call: the reached text in `Palette.WARN`, the rung in `TEXT_HEAD`. Its min, pref and max width are `speedWidth()`, measured once in the label's font over every rung's words and every text `ClockPace.reachedTexts()` lists (the formatter `reached()` uses), never under `SPEED_MIN_WIDTH` (34, the floor it had). Measured: the widest is the rung "0.25×", 33.0 px; the widest reached text 26.4 px; so the width is 34, as before, and at pace the label is laid out exactly as it was. The speed's tooltip gains a sentence: amber is the speed the city is reaching when it cannot keep up (the slice's addition, kept). |
| 2. a stall counted clamped; no "2× · 2×" | `frame()` adds the frame's clamped share (`share`, the FRAME_CLAMP'd time the months were advanced by) to the window, not its raw dt: a half-second stall no longer reads short at 1x for two seconds. A reached text equal to the rung's words is no shortfall (a rung of 2 reached at 1.75 reads "2×", white). Found in review and mended: a speed just under 1 that rounds up at one decimal was written "1.0×"; it is "1×" now, as the ladder writes 1x. |
| 3. the drawer's bar and the construction tab kept | Boxes made once (`drawerBarBox()`, `constructionTabBox()`, as `constructionPanelBox()`: their style, widths and alignment there, not at every draw), drawn through `Kept` from `barPool` and `tabPool` (forgotten in `forgetChrome()`): `Kept.label(text, icon)`, `Kept.group()`/`Kept.children` for the rotated words, `Kept.tip` for the three tooltips (the same `Tooltip` objects for ever, their words set), `Kept.set` for the children. The tab's look is chosen from `open || isHover()` at the draw (KP2's rule). Click handlers set every draw. |

## 2. The threading, for the gate's review

Nothing runs off the FX thread. `ClockPace` is called by the AnimationTimer's `handle()` and the redraw, both on
the FX thread, as before; `speedWidth()` measures on the first header draw (FX thread) and caches. The pin's
click still saves the prefs synchronously on the FX thread, as it did (`prefs.save`, unchanged). `UserInterface.pace`
is package-private and not final now, for KeptBench to swap in a pace on a hand-moved clock; nothing in the window
reassigns it.

## 3. The slice and the review

One slice (ClockPace +53/-15, ClockCheck +152/-24, UserInterface +155/-62, KeptBench +232/-2, KeptCheck +493/-2,
counted on the branch against UP6's tip). Reviewed as a stranger's: the header's draw and `paintSpeed()` build the
style from one method (`speedStyle`), so a kept draw sets nothing; `KeptBench.redrawSets` is generalised to a root
and a draw (`redrawSets(Page...)` calls it), not copied; KeptCheck 95's write checks cannot pass on a label made new
(the header draw that finds the pace short must write once on the watched label). The slice set KeptCheck 94's
sites with `BuildingsStacks.setUnderConstruction` (the loader's setter) and put them back, rather than playing
buildings to the end. Its clicks were fired: the bench's city is `GameFiles.scratch("keptcheck")`, so `Prefs.save`
writes into the scratch folder.

## 4. What checks it

- **ClockCheck 4** (rewritten) and **6** (new): the reached alone, `reached()` null exactly when the reading is the
  rung; a 0.5 s stall and one under FRAME_CLAMP at 1x and 50x leave the rung at every frame after (red on the old
  accounting), and a city really short stays short across one; the equal pair at a rung of 2 (1.75 reads "2×"), 1.4
  reads "1×", 50 run at 44.5 reads "45×"; 0.97 reads "1×", 0.92 "0.9×"; every text seen is in `reachedTexts()`.
- **KeptCheck 94**: the bar and the tab over 24 states (pinned or not, the panel open or folded, the city or a game
  menu, 0, 1 and 3 sites): every node handed back, none made; signature equal to one built new; the same pixels at
  three states; a spoiled count seen and set right; a second draw writes nothing (and one after a pin flip does);
  the hover look kept under a still pointer; the same tooltips with their words following; the clicks pin, unpin,
  open and fold.
- **KeptCheck 95**: at every rung, at pace, short and stopped: the words and the colour, the one width, never
  clipped, at pace laid out exactly as the old label (so the same pixels at pace), the clock block and the tiles
  at the same x short and at pace, the kept header equal to one built new, and a flip sets words and style once.
- **The slice's runs** (`./batch-check.sh ClockCheck KeptCheck`, twice): ClockCheck green; KeptCheck main's recorded
  red only ("a month's redraw after each choice hands back nine in ten...", `docs/notes/gate.txt`, not this
  batch's). `KEPT_ALL_CHROME_MS` read 9.0 ms (soft) on one run and 7.8 on the other; that section does not draw the
  bar or the tab.
- **`--pick`** on the merge with main c7ad4d9a: 7 of 181 harnesses (BuildMenuCheck, ClockCheck, KeptCheck,
  LandCheck, MapCheck, TimingCheck, ZoningViewCheck), exit 3: known red only, MapCheck and KeptCheck as
  `docs/notes/gate.txt` records them (not this batch's code: MapCheck's yard and its timing, KeptCheck's
  nine-in-ten on a page this batch does not draw); two timing assertions soft. Stale 0 firm.

## 5. For Jerus

No ★ calls. To look at, only if something seems off: at 50x on a heavy page the reading turns amber and shows one
figure, and nothing beside it moves.
