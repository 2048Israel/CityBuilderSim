# FS5 notes: KeptCheck II causes what its pages read

Batch FS5, built 2026-11-21 by Claude (Opus, headless) on `batch/FS5`, cut from
origin/main 4f1056f11 (0.9.174) and merged with 70af30dcc (AP1, 0.9.175). The brief
is `docs/studies/flaky-suite.md`, section 6.4. It takes KFP1, KBP1, KRW1, CD9-K,
KP5-N, KP6-N, KP7-N and KRT1's 77 and 78.

The batch is harness-only. The diff touches only harness code and records:
- `KeptCheck.java`;
- `Observation.HARNESSES`, which drops KeptCheck (no `Observation.reached` is
  left in it, and ObservationCheck asserts the list is exactly its users);
- `docs/notes/known-reds.txt`, this file, the ship file and the generated maps.

No model class and no screen is touched, `ui/KeptBench.java` included, and the
trace does not move (`moves_playtest=no`); no ensemble is needed.

## The rows

The FS5 rows of `docs/notes/known-reds.txt`, by their row numbers at 4f1056f11,
and AP1's two (rows 82 and 83 at 70af30dcc; MC2's are the same two lines, in its
ship file):

| row | KeptCheck line (section) | cause | change | run 1 |
|---|---|---|---|---|
| 29 | ...and the page is the same slider, kept, equal to a page built new (78) | KRT1: the held dial's page against a page built new, a place/size difference | `againstNewHeld` for the held dial | OK, 12 of 12 |
| 30 | the Bank's Profit page: each month hands back nine in ten of the nodes the first draw made (55) | KBP1: the share against the first draw (299, then 93 in one month) | the caused law (★1), and the block traced held still (★2) | label retired; the new line OK |
| 31 | the read of the saved cities landed within a minute (87) | KRW1: a native file read stalls under load | the worker's own answer awaited (FS1-C) | label retired; "landed, its worker's own answer waited on" OK |
| 42 | a month's redraw after each choice hands back nine in ten of the page's nodes (57) | KBP1 after a choice | as row 30 | label retired; the new line OK |
| 43 | ...the property rate staged: the redraw and the next equal a page built new (77) | KRT1 | `pageReshapedCaused` compares through `againstNewHeld` | OK |
| 47-50 | 63's settlement: the rule held again, the settled holding's page, the settled-holdings fixture, the settled lot watched | KFP1: nothing settles in the let-go month | the settlement caused (★4) | red, 4 lines; mended after the run |
| 58-61 | the fund holds something to draw; its bond; Portfolio's folds equal and adding (61-62) | KP5-N: the fund held no lot on OP1's city | 61 pays in FUND_PAY_IN_ISSUES minimum issues when the fund is empty | OK |
| 62, 72 | no month makes more than a tenth of the nodes the first draw made (69) | the old law read how eventful the Government Overview's months were | the caused law (★1) | label retired; 69 OK |
| 63 | most months make no node at all (11 of 25) (72) | the same, on the Trade reserves page | the caused law | label retired; 72 OK |
| 73 | Spending: Healthcare opened: the redraw and the next equal a page built new (71) | KRT1: a scroll bar narrowed the page built new | `againstNewHeld` | OK, 3 of 3 |
| 81 | fixture: the Rankings tab ...: every cell the household grid opens before the month it opens after (89) | the month closes a family's cell, and the Rankings pages draw no cell | the fixture asserted on Household money (★6) | OK, 3 of 3 |
| AP1 82 | most months make no node at all (9 of 25) (55) | the old law | the caused law | label retired; 55 OK |
| AP1 83 | ...the held slider is the same node, still held, at the thumb (78) | the payers' ranking flipped inside the month | the dial held is the last payer, found by identity (★5) | OK, 2 of 2 |

## What changed in KeptCheck

- **One law for every page (★1).** Sections 55 and up now go through FS4's
  `pageKeptCaused` and `pageReshapedCaused` (57 calls). The old helpers that
  kept the pre-FS4 law are gone: `pathCheck`, the 4-, 5- and 6-argument
  `pageKept` and every `pageReshaped` overload (with `observeRetention`), and
  `KP6_HANDED_BACK` (.75, the Policy pages' lower share).
  - A month's redraw keeps the page's top nodes, makes nothing when drawn again
    unchanged, and hands back HANDED_BACK (.9) of what it draws again
    (`Redraws`: the nodes handed back over the smaller of last month's page and
    this one's). So the old lines retire with their labels: "no month makes
    more than a tenth of the nodes the first draw made" and "most months make
    no node at all (N of 25)". Those read how eventful the city's months
    were, not whether the page was kept.
  - 96 (the research equipment) had an Observation on the share; it is the
    same `Redraws` check now.
  - 64's pictures: "no month draws it again with nothing changed in it" is a
    check, not an Observation (`observeUnchanged` is gone).
  - 80: every tooltip kept as the same `Tooltip` is a check on every page, and
    each screen's "the X pages carry tooltips ..." is a check.
- **KBP1 held still (★2).** A month's redraw that made a block (more than a
  tenth of what it drew again) is drawn again at once with the model and the
  clock held still (`blockHeldStill`).
  - If the draw makes nodes again, the block repeated: a kept fault, and the
    section's "no block a month made new repeats drawn again held still" line
    fails.
  - If it makes none, the month's content made the block, and the share's law
    judges it.
  - Either way the first BLOCKS_TRACED (3) of a section, and every repeat,
    print what the block made and what it dropped, in words. The last lines
    print the run's total beside KRT1's.
  - `pageReshapedCaused` traces a month under HANDED_BACK after a choice that
    does not act on the model.
- **KRT1 for the reshaped pages (77, 71, 78).** `pageReshapedCaused` compares
  both redraws through `againstNewHeld` (FS4's ★3). 78's held dial compares
  its slider through it too.
- **78's held slider.** On the payer page the dial held is the last payer
  (`two.get(two.size() - 1)`), with the top one in front.
  - Before, the slider held was the top payer, and the payers' ranking between
    the top two could flip within the month.
  - `heldWithCardInFront` now finds the bar by identity (`indexOf`). The page
    has one dial more, and the new one is in front of the held one.
- **KRW1 (87, 92-93).** The read of the saved cities is waited on to its own
  answer: `rankingsReadAwaited` polls `KeptBench.rankingsReadLanded` every
  200 ms, up to `RANKINGS_READ_GUARD_MS` (ten minutes, a hang's guard, not a
  budget). The minute is gone, so no Timing line is owed.
- **KFP1 and KP5-N (61, 63): the fund's lots, caused.**
  - 61: a fund that holds nothing is paid in the larger of half the cash and
    FUND_PAY_IN_ISSUES (200) minimum issues, with cash topped up when short.
    Each of the eight orders then takes an eighth of the free cash.
  - 63: the bond watched must run more than two months; otherwise a bond in the
    fund's reach that runs the longest is bought and a month played.
  - 63, the let-go month: the free bond running longest past SETTLED_WATCH (3)
    is bought through the model, or else a share the fund does not hold. So a
    holding the fund did not hold settles, and the watched lot runs three
    months.
- **CD9-K (64, 66, 67, 92): the paper, caused.**
  - When the auction refuses one of PAPER's six sales, `booked` puts that kind
    on the books through the DebtManager's own booking (`addShortTermTBill`,
    `addFloatingRateNote`, `addMediumTermBond`, `addLongTermBond`,
    `addCallableBond`, `addLinkedBond`, then `updateInterest`).
  - A fixture line checks that all six are on the books.
  - `piecesOnTheBooks` does the same with a Term when nothing sells.
  - 64's census and its two debt-picture watches use FS4's `paperLasting`
    (sixty months at the least), not the city's longest piece.
- **80's zone, caused (KP6-N).** When no zone stands, one is made through the
  model (`policyZone`, agriculture at 5%), as 75 to 77 make theirs. The
  ground's policyRow hover is then always run and checked.
- **89 (KP7-N, row 81).** The household grid's fixture ("every cell the
  household grid opens before the month it opens after, the families held") is
  asserted on the Household money pages, whose tooltips it is the premise of.
  On the Rankings pages, which draw no cell of it, a cell the month closes is
  printed and asks nothing.

## The calls (★)

- ★1 **One law for 55 and up.** The old-law lines retire with their labels
  rather than being kept beside the new ones, as FS4's ★1 did below 55. Those
  labels are "no month makes more than a tenth of the nodes the first draw
  made", "most months make no node at all", "...hands back nine in ten of the
  nodes the first draw made" and "...of the page's nodes". They read how
  eventful Danzik's months were (rows 30, 42, 62, 63, 72, AP1's 82). The
  identity, the same top and the unchanged draw are unchanged.
- ★2 **KBP1: a block that does not repeat held still is a reading.** This is
  the brief's rule, and FS4's for KRT1. The two helpers apply it differently:
  - `pageReshapedCaused` judges one month after each choice. A block there that
    does not repeat is printed, counted and left out of the share; one that
    repeats fails both lines.
  - `pageKeptCaused` sums twenty-five months, so a month of new content cannot
    sink it. Its blocks stay in the sum: a page keyed by its figures would sink
    the sum month after month.
  - The trace says what came and went, in words. Run 1's three single-month
    reds are of this kind: 71's Government Overview, "Advanced by the central
    bank (printed)" and "Inflation uplift on linked bonds" in, "Raised by issuing
    paper", "Principal repaid" and "Arrears paid down" out, with 0 made drawn
    again; and 77 twice.
  - No block repeated in the run. KBP1 is a reading, not a kept fault, and
    nothing is queued for it.
  - Recommended: keep it. A page that keys a row by what it says ("Bought land
    with D$12.4M") would make the block again every month, and the 25-month sum
    still catches it.
- ★3 **KRW1: the worker's own answer, no minute.**
  `RANKINGS_READ_GUARD_MS` (10 minutes) is a hang's guard, not a budget, so no
  `Timing` line is owed to KeptTimingCheck.
- ★4 **KFP1: the settlement caused through the model, not a holding written
  in.** A lot put straight into `CorporateBond.city` would skip what a fill
  does: the fund's cash (`noteBought`), the ledger's lot (`boughtBond`) and the
  hand's fill (`handFilled`). The ticket's own buys do all three.
  - A hand bid rests on the book until a seller comes (BondCheck's fixture),
    and on run 1 one month's bid settled nothing.
  - So the hand orders a bond and a share the fund does not hold each month,
    up to SETTLE_MONTHS (6).
  - Its resting orders are good for a month and may fill after the rule is held
    again. The held-rule fixture judges the rule's buys, so it leaves those out.
- ★5 **78: the held dial is the last payer.** The top two payers' ranking can
  flip within the month (AP1's and MC2's row). The page is found by identity,
  the new dial in front of the held one.
- ★6 **89: the grid's fixture where the grid is.** The household grid's cells
  are the premise of Household money's tooltips. The Rankings pages draw no
  cell of it, so there a closed cell is printed and asks nothing.
- ★7 **The floor pin stays.** Without it the city reached month 605 with 7,066
  people, against 2,075 pinned (run 0, on main 4f1056f11). KeptTimingCheck's
  budgets are set on the pinned city's pages, and since FS4 and FS5 no section's
  premise is the city's. The pin's comment now says so. (Run 0's timeout was
  the PC's load, not the size: the pinned run timed out at the same section.)
- ★8 **102 weighs the objects, on either renderer** (the orchestrator's ask,
  from GB5's ★5). The share is now taken on the live heap less the render
  targets a software renderer keeps on it.
  - `KeptBench.canvasTargets` walks what the window, its scenes and the
    interface's objects hold (fields, statics, lists, maps, node properties).
    It reads each canvas's targets on its NGCanvas peer.
  - Direct3D's are not on the heap (0 bytes), so on the PC the line reads as it
    did.
  - A fixture line asserts the walk found canvases holding a target.
  - The label is new, so the box judges it fresh, not under GB5's row.
  - The dial does not move.
- ★9 **CD9-K: the paper put on the books, as a sale books it.** When the
  auction refuses, `booked` uses the DebtManager's own booking, the coupon a
  point over the policy rate. On run 1 all six sold.
- ★10 **Left as found:**
  - 57's foreign money in the bank: printed by design, a hidden empty label of
    the bar. Run 1: "apart (line 175 ...)", a month later the same.
  - Premises any played city holds: 82-84's grid cells, 94's stacks, 92's fund
    security and nation.
  - 95, 99 and 100 were scratch research towns already; they take the caused
    law alone.

## The checks

Recovery audit (FS5c2, Codex, 2026-11-21): this draft was recovered with the
uncommitted book-settlement mend. Earlier "run 1" results above refer only to
the Direct3D run on 69d19e0a4; they are not final validation. The software
run on 76ec61a65 completed with exit 7 (`target/FS5-kept4-sw.log`). Its seven
failures are four section-63 consequences of zero settlements, section 71's
Healthcare comparison, section 89's Household money premise (one cell
closed despite restoring families), and section 102's objects bound.

Healthcare differed at content dimensions 1454x689 kept and 1446x705 fresh;
the column's preferred-width style also differed, so `againstNewHeld`
returned the content difference without applying its bounds-only retry.
This is unresolved, not a nonrepeating KRT1 reading. The grid reported 85
tooltips before, 83 surviving and the same Tooltip, zero lost on kept nodes;
the separate fixture still failed, and remains hard. The heap read 1761.6
MiB live, 53 canvases, ten targets, 18.5 MiB on heap, 1743.1 MiB objects
against 1536 MiB. Subtracting those targets did not resolve the red.

The software run traced four KBP1 blocks, none repeating held still, two
after a choice excluded as content readings. The complete per-section
choice/month counts are being recovered from the preserved log. The
settlement mend, final main merge, fast check and small isolated proofs
are pending at this recovery checkpoint. No full rerun, pick run, ensemble
or dry run is authorized until the orchestrator gives the specific go.
