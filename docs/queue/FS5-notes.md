# FS5 notes: KeptCheck II causes what its pages read

Batch FS5, begun 2026-11-21 by Claude (Opus, headless), recovered by
FS5c2 (Codex, headless) on `batch/FS5`, cut from
origin/main 4f1056f11 (0.9.174) and merged with 70af30dcc (AP1, 0.9.175). The brief
is `docs/studies/flaky-suite.md`, section 6.4. It takes KFP1, KBP1, KRW1, CD9-K,
KP5-N, KP6-N, KP7-N and KRT1's 77 and 78.

The batch is harness-only. The diff touches only harness code and records:
- `KeptCheck.java`;
- `Observation.HARNESSES`, which drops KeptCheck (no `Observation.reached` is
  left in it, and ObservationCheck asserts the list is exactly its users);
- `docs/notes/known-reds.txt`, this file, the ship file and the generated maps.

No model class or screen is touched. `ui/KeptBench.java` is check machinery
and adds the render-target measurement. The default game trace does not move
(`moves_playtest=no`); no ensemble is owed.

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
| 47-50 | 63's settlement: the rule held again, the settled holding's page, the settled-holdings fixture, the settled lot watched | KFP1: nothing settles in the let-go month | the settlement caused (★4) | red in both full runs; settlement mend awaits final-state validation |
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
  - 63, the let-go month: the free bond running longest past SETTLED_WATCH (3),
    with an actual world/bank holder, receives the hand's order. After the
    month posts it, `settleFundBid` withdraws competing bids and the holder
    sells into the real hand bid using `BondMarket.tradeForCheck`. The model
    moves face, cash, the ledger lot and the hand's fill. No bid or no seller
    cannot invent a holding, and zero settlements still fail the choice.
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
  - Six months of bids still settled nothing in the software run. Recovery
    supplies the missing seller through the book's own settlement (above).
    `SETTLE_MONTHS` is removed; the premise no longer waits for that seller.
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
  - 82-84's grid cells, 94's stacks, 92's fund security and nation retain
    their existing fixture assertions; they are not new permissions to skip.
  - 95, 99 and 100 were scratch research towns already; they take the caused
    law alone.

## The checks

Current recovery status (FS5c2, 2026-11-21): source is on main 5790d0506
(TN1, 0.9.178), merged at 0ba94a55e after GB5. The final `--fast` passed,
exit 0: compile 51 s, maps regenerated, zero firm stale findings (193 soft).
Log: `target/FS5c2-fast1.log`. All checks name main's earth-2.bin and
world-1.bin with C:/ paths; the isolated city uses GameFiles.scratch.

`target/FS5c2FixtureProbe.java` ran once against that source in Monocle with
SWPipeline, exit 0 in eight seconds (`target/FS5c2-fixture-proof.log`):

- The real book filled `city:hand` from `world`, face 100 at price
  1.087910948267. Cash fell 1087.910948267 to 979.119853440; world face fell
  10000 to 9900; city face, ledger cost and posted hand filled by 100.
  An earlier rule bid was withdrawn, a missing bid invented no settlement,
  and a fully filled bid bought nothing again. The bond outlasts the watch.
- Seventeen ledger-only cells were set just above the grid's .5 threshold.
  The real month closed all seventeen before the complete hold; after it,
  zero closed. All 71 surviving tooltip nodes kept the same Tooltip; none
  lost one. Each cell's before/after result is in the log.
- Healthcare on this small town compared equal. This does not resolve its
  previous played-state failure. The target walk found two canvases, one
  actual software heap target, 4096 bytes; that proves detection only and
  does not account for the full run's unexplained heap excess.

The read-only picker selects 24/235, not ALL, against 5790d0506:
AutoBuildCheck, BankCheck, BuildAdviceCheck, CapitalFlowCheck, ChartFitCheck,
ConstructionControlCheck, FittingCheck, HealthCheck, HousingCheck, KeptCheck,
KeptTimingCheck, MapCheck, MoneyWordsCheck, NewUnitCheck, ObservationCheck,
PriceLevelCheck, PrizeCheck, ResearchViewCheck, SaveFileCheck, ScaleCheck,
ShortSellingCheck, StepsCheck, TimingCheck and ToastCheck. No selected run
has been started. Review line: not needed; no money, saves or threading in
the diff. The harness fixture changes no model/screen source or default
trace, so no ensemble is owed.

Exact `git diff --numstat origin/main` process files, generated indexes,
notes and ship file excluded:

| File | Added | Removed |
|---|---:|---:|
| src/main/java/ham/citybuildersim/KeptCheck.java | 442 | 540 |
| src/main/java/ham/citybuildersim/Observation.java | 1 | 1 |
| src/main/java/ham/citybuildersim/ui/KeptBench.java | 100 | 0 |
| docs/notes/known-reds.txt | 0 | 16 |
| Total | 543 | 557 |

Java machinery alone is 543 added / 541 removed. The duplicated class-header
assertion catalog is reduced to a section map; complete premises and laws
remain beside their methods and in their assertion labels. No assertion was
deleted to balance this count.

The 11:04 orchestrator mail conditionally authorizes one direct software
KeptCheck after these proofs and after R1gc2's focused checks finish. Slot
clearance has been requested because R1gc2 was still active; no full rerun
has started. The old run's four settlement failures and grid failure have
isolated fixture mends but await final played-state validation. Healthcare
and the 1743.1 MiB objects result remain unresolved and hard, with no new
known-red exemption. No full pick, ensemble or dryrun has a go. The
provisional ship marker remains; no readiness or ship is claimed.

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


## Recovery changes and limits

The inherited draft and source were copied to `target/FS5c2-recovered-notes.md`
and `target/FS5c2-recovered-KeptCheck.java` before further edits; all original
logs and probe scripts remain. The original launcher brief is also preserved
as `C:/Users/Jerus/cbs/sessions/FS5/in-1.txt`, and its `out-1.jsonl` final tools
show the unfinished settlement edit. The three original bus replies and four
FS5 reports were read; their old run permissions do not override the recovery
hold. Merge 71204817c includes main cdd4a2a09, preserving GB5's shared
`FollowBench.startJavaFx` and taking main's generated indexes before regeneration.

Recovery found that a refused choice returned a synthetic share of 1.0 and
was counted as a played month. It now returns NaN for the share, remains a
failed equality assertion, and is reported separately as refused with no
month played. No retention law or identity is relaxed.

The grid premise restored FamilyModel alone, but `householdCells` also reads
`PeopleScreen.belowTheRule`: retired, unemployed, student, orphan, prisoner
and owner counts from HouseholdBalance. Those counts change in the month
independently of FamilyModel. The fixture now restores those cell counts as
well and prints each cell the real month closed, and whether the hold kept
it open. It preserves the month's monetary stocks and all tooltip assertions.

Content differences now receive held-still diagnostic comparisons as well.
They always return the original failure, even if a diagnostic comparison
becomes equal. Only the pre-existing KRT1 rule for unchanged content may
accept a nonrepeating bounds reading. Styles are not stripped and no heap,
retention or pixel tolerance changes.

GB5's shipped facts are retained in the audit: section 55 had 9/25 quiet
months, section 78's held slider failed at thumb 0.005, and section 102 read
1554-1719 MiB on the box against 1536, versus 1379 MiB on Direct3D. GB5 found
43,202 Labels on both renderers and an int[] difference. FS5's new objects
label is not covered by that old heap label: its 1743.1 MiB result remains
hard and unresolved. This is no claim that 18.5 MiB accounts for all renderer
retention. A final full run and causal diagnostics await the specific go.

## Every removed known-red row

These are all sixteen removals against GB5 main cdd4a2a09. Evidence is from
the completed software log at 76ec61a65, with the Direct3D run only as earlier
support. The four fund-settlement rows, Healthcare comparison and old heap
row remain in the registry because their repairs are not validated green.

| Removed label | Disposition and evidence |
|---|---|
| ...and the page is the same slider, kept, equal to a page built new | Section 78 equality and identity at thumb 0.005 are green twice (4174, 4186); no KRT1 reading was needed. Includes AP1 and GB5 wording variants. |
| the Bank's Profit page: each month hands back nine in ten of the nodes the first draw made, or more | Old first-draw/quiet-month law retired; the caused HANDED_BACK law remains hard. Bank Profit returns 4667/4674 across 25 redraws (1413), KBP1 repeats zero (1414); every reshape exclusion is listed below. |
| the read of the saved cities landed within a minute | Section 87: retired the minute deadline; the worker-awaited assertion is green with one saved city on the board (log line 4518). |
| a month's redraw after each choice hands back nine in ten of the page's nodes, or more | Old first-draw/quiet-month law retired; the caused HANDED_BACK law remains hard. Bank Profit returns 4667/4674 across 25 redraws (1413), KBP1 repeats zero (1414); every reshape exclusion is listed below. |
| ...the property rate staged: the redraw and the next equal a page built new | Section 77 equality remains green (3912); no KRT1 reading was needed in this run. |
| the fund holds something to draw | Sections 61-63: holdings, a lasting held bond and the Portfolio fold equality/addition lines are green; these are distinct from the four still-red new-settlement rows. |
| fixture: the fund holds a bond, null, that runs 0 months | Sections 61-63: holdings, a lasting held bond and the Portfolio fold equality/addition lines are green; these are distinct from the four still-red new-settlement rows. |
| every row that folds opened on Portfolio: the redraw and the next equal a page built new | Sections 61-63: holdings, a lasting held bond and the Portfolio fold equality/addition lines are green; these are distinct from the four still-red new-settlement rows. |
| every row that folds opened on Portfolio adds to the page | Sections 61-63: holdings, a lasting held bond and the Portfolio fold equality/addition lines are green; these are distinct from the four still-red new-settlement rows. |
| no month makes more than a tenth of the nodes the first draw made (the most made in one: 39 of 373 | Old first-draw/quiet-month law retired; the caused HANDED_BACK law remains hard. Bank Profit returns 4667/4674 across 25 redraws (1413), KBP1 repeats zero (1414); every reshape exclusion is listed below. |
| most months make no node at all (11 of 25) | Old first-draw/quiet-month law retired; the caused HANDED_BACK law remains hard. Bank Profit returns 4667/4674 across 25 redraws (1413), KBP1 repeats zero (1414); every reshape exclusion is listed below. |
| no month makes more than a tenth of the nodes the first draw made | Old first-draw/quiet-month law retired; the caused HANDED_BACK law remains hard. Bank Profit returns 4667/4674 across 25 redraws (1413), KBP1 repeats zero (1414); every reshape exclusion is listed below. |
| fixture: the Rankings tab with every measure open: every cell the household grid opens before the month it opens after | Section 89: Rankings draws no household grid. Assertion relocated to Household money; its new one-cell closure is still hard and is mended as the incomplete count-hold fixture above. |
| ...the held slider is the same node, still held, at the thumb | Section 78 equality and identity at thumb 0.005 are green twice (4174, 4186); no KRT1 reading was needed. Includes AP1 and GB5 wording variants. |
| most months make no node at all (9 of 25) | Old first-draw/quiet-month law retired; the caused HANDED_BACK law remains hard. Bank Profit returns 4667/4674 across 25 redraws (1413), KBP1 repeats zero (1414); every reshape exclusion is listed below. |
| ...the held slider is the same node, still held, at the thumb (0.005) | Section 78 equality and identity at thumb 0.005 are green twice (4174, 4186); no KRT1 reading was needed. Includes AP1 and GB5 wording variants. |

## Software run: each reshape share's excluded months

Order below is log order, including helpers shared with FS4; repeated section
numbers identify separate calls by their ordinal. `readings` are the months
excluded by the held-still content rule; `acted` are predeclared model-action
choices. No other 25-month `pageKeptCaused` draw is excluded. Only two
reading months were excluded: Policy 77 calls 8 and 9 each count 10/11.
All other calls excluded zero months by that rule. Three groups already
declare all their choices acting (Fund call 4, Government call 6, Trade call
4): those groups prove equality, not a retention share.

This table records the old run verbatim numerically, not a new run of these
mends. Fund call 6's 4 counted months include two refused choices: only two
of those four actually played a month. Recovery fixes that reporting bug;
the final run will report refused choices separately. Both refused
equalities failed in the old run, so they never supplied green identity proof.

| Section/call | Counted | Choices | Readings excluded | Acted excluded |
|---|---:|---:|---:|---:|
| 23: what reshapes Infrastructure / 1 | 14 | 14 | 0 | 0 |
| 27: what reshapes Services / 1 | 54 | 54 | 0 | 0 |
| 31: what reshapes the land office / 1 | 20 | 20 | 0 | 0 |
| 33: what reshapes the fell-short page (a bond) / 1 | 1 | 1 | 0 | 0 |
| 33: what reshapes the fell-short page (the vault's dollars) / 2 | 1 | 1 | 0 | 0 |
| 38: what reshapes the Build screen / 1 | 40 | 42 | 0 | 2 |
| 42: what reshapes the construction page / 1 | 10 | 10 | 0 | 0 |
| 44: what reshapes the no-deposit page (1 x Iron Mine) / 1 | 1 | 1 | 0 | 0 |
| 44: what reshapes the no-coast page (1 x Desalination Plant) / 2 | 1 | 1 | 0 | 0 |
| 44: what reshapes the no-river page (1 x Hydro Station) / 3 | 1 | 1 | 0 | 0 |
| 44: what reshapes the no-bridge page (1 x Highway Bridge) / 4 | 1 | 1 | 0 | 0 |
| 44: what reshapes the no-licence page (1 x Engineering Services Office) / 5 | 1 | 1 | 0 | 0 |
| 44: what reshapes the no-land page (10000 x Studio Apartments) / 6 | 1 | 1 | 0 | 0 |
| 44: what reshapes the build funding page (1000000 x Crude Pipeline) / 7 | 1 | 1 | 0 | 0 |
| 44: what reshapes the build fell-short page (2 x Gravel Road built, 1000000 x Crude Pipeline left) / 8 | 1 | 1 | 0 | 0 |
| 44: what reshapes the build fell-short page (none built, 1000000 x Crude Pipeline left) / 9 | 1 | 1 | 0 | 0 |
| 44: what reshapes the build funding page (488 x Metro Line) / 10 | 1 | 1 | 0 | 0 |
| 49: what reshapes the Sectors list / 1 | 2 | 2 | 0 | 0 |
| 49: what reshapes the Sectors tab / 2 | 25 | 25 | 0 | 0 |
| 49: what reshapes the Sectors tab / 3 | 45 | 45 | 0 | 0 |
| 49: what reshapes the Sectors tab / 4 | 5 | 5 | 0 | 0 |
| 49: what reshapes the Sectors tab / 5 | 18 | 18 | 0 | 0 |
| 96: what reshapes Agriculture's Investors page / 1 | 3 | 3 | 0 | 0 |
| 57: what reshapes the Bank Overview / 1 | 10 | 10 | 0 | 0 |
| 57: what reshapes the Bank Overview / 2 | 22 | 22 | 0 | 0 |
| 57: what reshapes the Bank Overview / 3 | 10 | 10 | 0 | 0 |
| 60: what reshapes the Finances hub / 1 | 11 | 11 | 0 | 0 |
| 60: what reshapes the Finances hub / 2 | 9 | 9 | 0 | 0 |
| 60: what reshapes the Finances hub / 3 | 33 | 33 | 0 | 0 |
| 60: what reshapes the Finances hub / 4 | 36 | 36 | 0 | 0 |
| 60: what reshapes the foreign default menu / 5 | 1 | 1 | 0 | 0 |
| 60: what reshapes the foreign default menu / 6 | 1 | 1 | 0 | 0 |
| 60: what reshapes the debt result menu / 7 | 1 | 1 | 0 | 0 |
| 60: what reshapes the debt result menu / 8 | 1 | 1 | 0 | 0 |
| 60: what reshapes the debt result menu / 9 | 1 | 1 | 0 | 0 |
| 60: what reshapes the debt result menu / 10 | 1 | 1 | 0 | 0 |
| 60: what reshapes the debt result menu / 11 | 1 | 1 | 0 | 0 |
| 60: what reshapes the debt result menu / 12 | 1 | 1 | 0 | 0 |
| 63: what reshapes the city's fund / 1 | 14 | 17 | 0 | 3 |
| 63: what reshapes the city's fund / 2 | 6 | 13 | 0 | 7 |
| 63: what reshapes the city's fund / 3 | 8 | 8 | 0 | 0 |
| 63: what reshapes the city's fund / 4 | 0 | 3 | 0 | 3 |
| 63: what reshapes the city's fund / 5 | 18 | 28 | 0 | 10 |
| 63: what reshapes the city's fund / 6 | 4 | 5 | 0 | 1 |
| 71: what reshapes the Government Overview / 1 | 5 | 5 | 0 | 0 |
| 71: what reshapes the Government Overview / 2 | 86 | 86 | 0 | 0 |
| 71: what reshapes the Government Overview / 3 | 11 | 11 | 0 | 0 |
| 71: what reshapes the Government Overview / 4 | 86 | 86 | 0 | 0 |
| 71: what reshapes the Government Overview / 5 | 19 | 19 | 0 | 0 |
| 71: what reshapes the Government Overview / 6 | 0 | 86 | 0 | 86 |
| 71: what reshapes the Government Overview / 7 | 4 | 10 | 0 | 6 |
| 74: what reshapes the Trade Overview / 1 | 7 | 7 | 0 | 0 |
| 74: what reshapes the Trade Overview / 2 | 21 | 26 | 0 | 5 |
| 74: what reshapes the Trade Overview / 3 | 10 | 10 | 0 | 0 |
| 74: what reshapes the Trade Overview / 4 | 0 | 25 | 0 | 25 |
| 74: what reshapes the Trade Overview / 5 | 27 | 27 | 0 | 0 |
| 74: what reshapes the Trade Overview / 6 | 9 | 9 | 0 | 0 |
| 77: what reshapes the Policy hub / 1 | 24 | 24 | 0 | 0 |
| 77: what reshapes the Policy hub / 2 | 39 | 39 | 0 | 0 |
| 77: what reshapes the Policy hub / 3 | 44 | 44 | 0 | 0 |
| 77: what reshapes the Policy hub / 4 | 55 | 55 | 0 | 0 |
| 77: what reshapes the Policy hub / 5 | 11 | 11 | 0 | 0 |
| 77: what reshapes the Policy hub / 6 | 11 | 11 | 0 | 0 |
| 77: what reshapes the Policy hub / 7 | 11 | 11 | 0 | 0 |
| 77: what reshapes the Policy hub / 8 | 10 | 11 | 1 | 0 |
| 77: what reshapes the Policy hub / 9 | 10 | 11 | 1 | 0 |
| 84: what reshapes Household money / 1 | 65 | 65 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 1 | 44 | 44 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 2 | 15 | 15 | 0 | 0 |
| 95: what reshapes Policy, Research's Found page / 1 | 4 | 4 | 0 | 0 |
| 99: what reshapes Policy, Research's Overview / 1 | 5 | 5 | 0 | 0 |
| 100: what reshapes Policy, Research's Funding / 1 | 9 | 9 | 0 | 0 |
