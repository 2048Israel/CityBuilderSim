# KP6 - Screens kept: Government, Trade, Policy and zoning ({next}, 2026-11-05)

**WHERE IT STANDS** (2026-11-06): the four slices are in and reviewed (B bfcbf25e, A a6f7ddef, C 911af0bc, D 342f0a38,
with Opus's mends d3edf5ff and cad44d6f); ready: main merged in with KP5 0.9.25 and R2 0.9.24 (cc807683), `--pick`
exit 3 on it, main's recorded reds only (§5).

The batch's record. The study's KP6 row (`docs/studies/spec-screens-in-place.md` §7) with §3 (the pattern and its
rules, seven since KP2) and §6 (what checks it). Lane 2 (screens), after KP5, built on `origin/batch/KP5` at its ship
file (3920dffb). Opus wrote the briefs, reviewed each diff and committed; Sonnet 5.5 slices at xhigh wrote the code,
the three screens side by side in worktrees of their own, then the harness. Nothing outside `ui/` and `KeptCheck`
changes, and no view class: the default playtest's trace cannot move, so no ensemble (EN1).

## 1. What it builds

The Government tab, Trade & the World with its partners page, and Policy with zoning's pages are drawn over last
month's nodes through `keepMenu` and each screen's `drawPage()`, as the Bank's (KP5): one code path for the first draw
and every month after, the show method's bookkeeping (the page known, the doors' targets cleared, the month's first
draw told, the levers' register on Policy) in the draw, so the bench's draw is the real one.

- **Government** (slice A): every page - the Overview's rings and balance, the books from EARNED to BANKED, the central
  bank and the economy; the two lists' ranked bars and who pays each line; what the debt costs by its paper; Revenue and
  Spending's three cards; the Output with its kept GDP chart. The whole page is a scope keyed by the page, its strip,
  head and five figures scopes of their own; every card, fold, line, ring block, list row and payer row a scope keyed
  by what it shows; a step's sign its own scope (an emptied label kept the words' height).
- **Trade & the World** (slice B): the Overview's bars, currency card and gauges, the Month's river and holdings, What
  we trade with the band in legs, the Currency, the Reserves and the exchange; `PartnersPage`'s table (a row a nation),
  a nation's page and a good's board. The page scope is keyed by the page and, on Partners, the nation; the river's,
  goods' and exchange's toggles are scopes of their own. Pictures told their figures: `TradeScreen.BandBar`,
  `PartnersPage.CurveChart`, `Pieces.BandTrack`, `MirrorRows` (its growth through `Kept.play`), `DivergingBars`.
- **Policy and zoning** (slice C): the hub's area cards and what is biting, Taxes, Wages, Money (the policy rate, how
  strict, the anchor and the basket, the currency's pieces under every regime, the reform), Promises, zoning's ground,
  each zone's page and the forest law. The page scope is keyed by the area and the page. **The controls' rule**
  (KP2's sixth and seventh) holds on every dial: each dial card is a scope keyed by its lever, so a card that comes or
  goes in front of a held dial hands no other card's slider to the wrong lever; the ladders outside a card (the rule's
  target, how strict, zoning's multi-dial card, which now fills its effects at a held thumb) keyed the same way; the
  staged tray's chips a scope a lever; a shut lever's greyed dial set on both sides.
- `Pieces` helpers through `Kept`: `splitRing`, `ringKey`, `rankBars` (`rankRow`), `payerRow`, the bridge's pieces
  (A); `BandTrack`, `gaugeCard`, `MirrorRows`, `DivergingBars`, `openPopover`'s own nodes (B); `stagedChip`,
  `stagedTray`, `whoGoesWithout` (C) - People's WHO GOES SHORT card shares the last, and Policy's Food page the first
  two. A `chip(text, colour, graphic)` overload keeps the printed chip's icon.
- The month's animations (Trade's strip count-up, the mirror rows' growth) only on the month's first draw and only at
  1x-10x (`ui.countsUp()`), as on every kept screen since KP1's ★A; Government and Policy have none.
- `UserInterface.anotherCity()` forgets the three screens' pools.

## 2. The slices

| slice | what | commit | its smoke run (scratch city, outside the repository) |
|---|---|---|---|
| B | Trade & the World and its partners page | bfcbf25e | 69 choices, 138 comparisons with a page built new, 0 differing; the fewest handed back 91.4% |
| Opus | VaultTable's fold, B's leftover (§6) | d3edf5ff | - |
| A | the Government tab | a6f7ddef | 309 comparisons over three cities (as it is; a forced deficit with printing and arrears; every kind of paper sold), 0 differing; fewest 94.7% |
| C | Policy with zoning's pages | 911af0bc | 182 choices in an old city and a young one, each equal to a page built new; the dials held across a month on a tax page, the policy rate and a zone's ground; fewest 98.0% and 90.2% |
| D | KeptCheck 69-81, the bench's hands | 342f0a38 | the harness (§5) |
| Opus | the bench's signature reads a path's elements (§6) | cad44d6f | - |

A, B and C ran at once in three worktrees on three sets of files; each owned the `Pieces` helpers its pages reached
first. Their diffs met only at `KeptBench`'s last lines (three new sections side by side).

## 3. The ★ calls, taken the recommended way

- **★A. Trade's month animations at 1x-10x only**, as People, History, Sectors and the money screens (KP1 ★A, KP4,
  KP5 ★A): at 50x a month lands every 100 ms and a count started on each never ends. *Recommended and taken.*

No other call: the study's KP6 row names the four screens, and zoning's pages are drawn inside Policy's frame.

## 4. The threading, for the gate's review

**One thread**, as KP1-KP5: the clock is an `AnimationTimer`; the month, every redraw, every handler and listener
below, every animation and every repaint of a picture run on the FX thread. **Nothing in this batch runs off it**: no
thread, executor or timer is added. What a reviewer weighs is what runs inside a draw, what runs a pulse later and what
a kept node carries from one draw to the next.

- **The pools and the arrivals.** One `Kept.Pool` per screen (Government, Trade with Partners, Policy with zoning);
  `anotherCity()` forgets the three. A move between pages is a kept redraw under the same screen name that changes the
  page's scope key, so the old page's nodes, its scroller among them, are let go and the new page opens at its top.
- **The frame over the scroller**, each screen's `frameOver`: the scroller's height and the page's width bound once
  per scroller (a marker in its properties), as KP4's and KP5's. Policy's tray: its height is rebound only when the
  tray comes or goes (`TRAY_NODE`), never each draw; Policy's split cards bind their width once per box and share
  (`SPLIT_OF`).
- **What runs a pulse later**: the scroll to a door's target on Government and Trade, and on Trade the popover of a good
  picked from the river (`Platform.runLater`, one-shot, scheduled only when a door left a target; unchanged from before
  the batch, reading the targets the draw just noted). Nothing else.
- **Added once** (`Kept.isNew`, a marker, or a picture's constructor): the `VaultTable` fold's width listener (it sizes
  the latest draw's charts, read from the box's properties when it fires); `MirrorRows`' growth listener, in its
  constructor; the sliders' listeners, through `Ladder` (KP2), reading the latest build's ladder when they fire. Every
  click is a `setOnXxx` set each draw; every tooltip on a node a draw makes is `Kept.tip` (the river's and the band
  key's had `Tooltip.install`); hover looks are chosen from `isHover()` where the draw sets them.
- **Animations**: Trade's count-up and the mirror rows' growth through `Kept.play`, which stops the one the node was
  last given; past 10x the running one is stopped and the node left at rest (★A).
- **The model** is read through the same getters and written only where the player's controls wrote it before (the
  staged tray and Apply, a zone's dials, the exchange's ask).

## 5. What checks it

`KeptCheck` sections 69-81, after KP5's 55-68, on its city as it stands when it reaches them (month 2,687; 906
assertions and 6 timed lines; 1,999 in all, all pass; 445 s alone on the shared PC).

- **69-71 Government** (347): every page kept (top nodes stay; nine in ten handed back over 25 months, most months none;
  the Overview's lists show a line only in the months it took in or paid out something, so there no month may make more
  than a tenth); each page equal to a page built new for `MONTHS` months, the same pixels at three, the spoil proof;
  every fold, line and door opened and closed, in the city as it is, owing paper of every kind, and in a deficit made by
  hand with the central bank printing for the treasury and arrears owed (the dials put back after).
- **72-74 Trade** (176): the same, on its six pages and a nation's page: every sub-page, fold, toggle and line, a good's
  board, three nations' pages and back to the table, the exchange's ask and buying and selling, each of the eight
  moneys, the vault's mix; the eight over the years' two charts as wide as their card, kept and a month after.
- **75-77 Policy** (273): the hub, Taxes' Profit page, the policy rate, Schools, Food, zoning's ground and a zone's page
  for every sector, zoned or not, the forest law, every fold and line, a change staged and dropped on every page that
  takes one, the currency and reform pages under every regime; People's WHO GOES SHORT with 92.6% short of food.
- **78 the controls** (49): on a tax page, the policy rate and a zone's ground, through the real show method, a slider
  pressed and dragged two steps and a month landed is the same node, still held, the thumb where the hand left it, the
  card's effects at the thumb; released, staged at the thumb and equal to a page built new; dropped, equal again; a dial
  card that comes in front of a held dial (a course's own price on Schools, a payer's own move on Profit) leaves it the
  same node at its thumb.
- **79 the count-up** (14) at each rung of the speed ladder and paused; **80 the hover and the tooltips** (47).
- **81 the cost**, through `Timing` (soft beside the other lane, hard at the gate):

| dial | value | page | kept, five runs (median) | built new |
|---|---|---|---|---|
| `KEPT_GOVERNMENT_MS` | 7 | the Overview | 3.1-4.9 ms (3.4) | 12.0-20.6 ms |
| `KEPT_TRADE_MS` | 10 | the Overview; the reserves | 4.3-9.0 (4.7); 2.7-4.3 (3.0) | 8.3-14.8; 10.6-13.8 |
| `KEPT_POLICY_MS` | 10 | the hub; Profit; the policy rate | 4.3-5.2 (4.6); 2.0-3.3 (2.1); 4.2-7.2 (4.5) | 9.7-14.9; 6.9-8.9; 15.9-26.5 |

Each twice the median, rounded up (KP1's way), measured at month 3,505 in five runs of the KP6 sections alone; in the
full run at month 2,687 the kept months came to 3.2-6.7 ms. Broken by hand to see them caught, one at a time and put
back: both layers of a dial card's scope made constant (`Levers.dialCard`'s key and `PolicyScreen.addPayer`'s; either
alone is covered by the other; red in 78's new fixture as "the held slider is the same node"), a `Kept.tip` turned back
into `Tooltip.install` on the river's bar (74, 80), `isHover()` ignored on `rankRow` (80), Trade's count-up gate removed
(79, six assertions).

**For the gate's review, the harness's choices.** The reshape sections (71, 74, 77) ask three in four of a page handed
back after each choice (`KP6_HANDED_BACK`), not nine in ten: these pages rank what the model did, and at month 3,505 one
month made 80 of the Partners page's 350 nodes (its eleven partners re-ranked) and 44 of Spending's 397 with every line
open; a page remade by a redraw hands back next to nothing, and 69, 72 and 75 still hold nine in ten. Partners' table
and the deficit run as `acting` (KP5's way for the fund's Activity: compared with a page built new, the share printed,
not asserted). The deficit's arrears are written into `Game`'s map by reflection (`arrears`, `arrearsKey`): the
model's own way, cash past the central bank's ceiling and a line refused (CentralBankCheck 6), would leave the city at
the ceiling for every section after; a rename fails the harness loudly.

On the merge with KP5's mended branch (183aa5d5, main at 0.9.23 with R1b and R1c inside), with the world files:
`--pick` exit 3, 9 harnesses, 7 passed (TimingCheck, ClockCheck, ZoningViewCheck, BuildMenuCheck, BridgeMapCheck,
LandCheck, IslandNetCheck); the two reds main's recorded: MapCheck (2d, the yard's boxes) and KeptCheck's one line,
section 49's Construction income page at 89.8% on R1c's city (KS1, queued) - every KP6 line passes on that city, 2,000
OK in all. KP6's six timed lines, soft beside the other lane's full suite: Government's Overview 6.2 ms of 7, Trade's
Overview 7.3 of 10 and the reserves 2.8, Policy's hub 4.4, Profit 2.1 and the policy rate 7.1 of 10. **For the gate:**
`KEPT_GOVERNMENT_MS` is the tight one (89% of it under that load, as KP5's `KEPT_BANK_MS`, 10.6 of 12 in the same run);
being new, a red on it would have no main to compare with. No full suite: the diff is `ui/` and `KeptCheck`, and the
nine picked are the harnesses that read it; nothing outside `ui/` changed.

On the merge with main (cc807683: KP5 0.9.25, R2 0.9.24's research catalogue), with the world files: `--pick` exit 3,
the same nine, the same two reds, main's recorded (MapCheck 2d; KeptCheck 49 at 89.8%, gate.txt's line); every KP6
line passes, 2,000 OK. Timed alone this time: Government's Overview 3.8 ms of 7, Trade's Overview 5.8 of 10 and the
reserves 2.8, Policy's hub 4.1, Profit 2.0, the policy rate 4.4 of 10.

## 6. Found in review

- **By the slices:** an emptied label keeps the height of the words it had (Government's step signs: scopes of their
  own, as KP2, KP4 and KP5 found); a `row()`'s max height stayed on the kept node of the currency's deal card (keyed by
  its arrangement now); Policy's tray height rebound every draw; a ladder outside a dial card filled its effects at the
  slider's value, not the held thumb (zoning's multi-dial card).
- **By Opus:** every slice's diff read for `new` left in a draw, `Tooltip.install`, `setTooltip`, `getStyle() +`,
  `addListener` and `runLater`: none but records, `Insets`, path elements, `TimeChart.Line` and a picture's own parts;
  every added listener added once (§4). `VaultTable`'s fold, the Reserves page's "the eight over the years", which B
  left: a kept box hears no width change, so its charts would have stayed at 794 px; now its width listener is added
  once and sizes the latest draw's charts, and a kept box's charts are sized at the width the box has (74 asserts it).
  The bench's signature read no `Path`'s elements, so a kept ribbon carrying last month's geometry would have passed:
  it reads them now (Trade's ribbons, a Sector's curve, both set whole each draw).
- **By the harness (slice D's mend):** `Pieces.ScaleRows` left a rule that went off the scale with its line and name
  where the month it was last on laid them (the policy rate page against a page built new, seen only at month 3,505):
  now reset when hidden.

## 7. Left

- **Not reached by a fixture** (KP6-N): the DEFICIT card in KeptCheck's city (its fixture leaves a surplus by month
  2,687 - the central bank's remittance and the fund's marks - though the central bank and arrears cards are reached;
  slice A's smoke reached it at month 600); WHO GOES WITHOUT's door to the shops (drawn only when the short half is the
  larger); the reform page's unlocked branch. Each is drawn by the same code as the kept pages around it.
- **Cost only, for KP2b's sweep** (as KP5's review queued there): a style set whole and then built on `getStyle()` in
  the same draw (`TradeScreen.java:703` and `PolicyScreen.java:961`, a door card's hand; `ZoningScreen.java:827`, the
  picked step's chip), so CSS is applied twice a draw. Correct, since the first set is this draw's.
