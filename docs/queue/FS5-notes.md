# FS5 notes: KeptCheck II causes what its pages read

Current status: BLOCKED/Pending after the stopped BOX gate
cbs-gate-FS5-pcPZSnVc on ready2dfd503a4/base da438. Sole new hard assertion:
object heap1711.9MiB > unchanged1536 by175.9 (live1716.5-target4.6).
Gate phase773s/exit4, outer exit1; not published. Marker was restored and
pushed FIRST in own commit1d480d976, verified on origin before records edits.
Root moved FS5 to main Pending in queue-only f2dba242; no merge/check replay.

Native kept7 remains truthful PASS at sourceaa570/.180:
live1521.3-target18.5=objects1502.8MiB, Government release1832->0 and
two equal Healthcare primary pairs. It is NOT a gate-box pass or waiver.
BOX Government release1904->0 also passes, but actual model/workload and
unsealed class/data/JDK/platform facts limit comparison. BOX73 numeric
share counts match native totals; funding labels, papers/lots and quiet
counts differ. No root/source/runtime attribution from209.1MiB object delta.

All four existing FS5 vetoes, KHL1Pending scope, hard laws and old evidence
remain. Source is unchanged fromaa570; only saved/static audit and records
are authorized. No owned job. Waiting for root review/separate GO.

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

Pre-full-run recovery checkpoint (FS5c2, 2026-11-21): source is on main 5790d0506
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


## Completed final-source software run (FS5c2)

The one authorized direct run on 068f3ab57 (base main 5790d0506, 0.9.178)
completed **exit 1**, 2026-11-21 11:08:20–11:31:29 (23 min 9 s). Log:
`target/FS5c2-kept5-sw.log`. Its foreground shell was kept alive until exit.
It used `-Xmx2048m -Dprism.order=sw`, the contract's C:/ Earth/world paths,
and Windows' display. The slot was clear of other long harnesses; Jerus's
game PID 32464 and its Maven launcher stayed open. Timing is soft here.
No full pick, ensemble, dry run or unchanged-source replay was started.

Six of the old seven failures are now hard green. Section 63 settled
thirteen new holdings, including B:3071, through the real book. The hand
bid posted 1.497e11 face; the world's actual available face filled
1.4701473837738159e9 at 0.509944630131. The watched bond had 116 months left,
past the three-month page/review watch. Both previously refused comparisons
and both settlement premises passed. Healthcare's opened comparison also
passed directly, with no KRT1 reading used. Section 89's three Household
money premises all reported zero cells closed after the hold; its All of
them view would have lost two unemployed cells without the complete hold.
Those cell identities and successful holds are printed in the log.

Section 102 alone still fails: **1635.0 MiB live, 33 canvases found, ten
targets on heap, 18.5 MiB detected target payload, 1616.5 MiB reported as
objects against 1536 MiB, an 80.5 MiB miss**. That figure is the current
measurement's result, not proof that the entire residual is UI/model
objects. The old software run's 1761.6/18.5/1743.1 MiB figures remain above;
the difference between these runs is not hidden or waved away as load.
The unchanged `.75 * AllChecks.HEAP_MB` assertion remains hard. No red
exemption was added and no real UI/model fault is claimed without a trace.

The five newly green registry removals below are backed by this run. The
old heap row remains vetoed as FS5's responsibility. With all 21 removals,
exact process-file numstat is now KeptCheck +442/-540, Observation +1/-1,
KeptBench +100/-0, known-reds +0/-21: **Java code +543/-541; registry-inclusive
+543/-562**, generated indexes, notes and ship file excluded. The earlier
+543/-557 table records the six-row checkpoint before this run.

| Newly removed label | Final run evidence |
|---|---|
| ...the rule held again, Portfolio with the holdings its month settled | line 2067: ...the rule held again, Portfolio with the holdings its month settled, every row that folds opened: the redraw and the next equal a page built new OK |
| the page of a holding the let-go month settled, from Portfolio | line 2068: the page of a holding the let-go month settled, from Portfolio: the redraw and the next equal a page built new OK |
| fixture: the month the rule was let go settled holdings the fund did not hold | line 2075: fixture: the month the rule was let go settled holdings the fund did not hold, a settled lot's page and review watched after it (13 settled) OK |
| fixture: the settled lot watched | line 2076: fixture: the settled lot watched, B:3071, runs 116 months, past the 3 months its page is watched OK |
| Spending: Healthcare opened: the redraw and the next equal a page built new | line 2558: Spending: Healthcare opened: the redraw and the next equal a page built new OK |

KRT1 accepted zero nonrepeating layout readings; no content difference was
printed or waived. KBP1 traced seven blocks, none repeating held still.
Five stayed in their multi-month retention sums; the two after a choice
were content readings excluded under the standing law. Every count follows,
including zero-count acting groups. **All refused counts are zero**: every
choice actually ran in this final record. No group lost most of its months
to the held-still rule.

| Section/call | Counted | Choices | Content readings excluded | Acted excluded | Refused (no month) |
|---|---:|---:|---:|---:|---:|
| 23: what reshapes Infrastructure / 1 | 14 | 14 | 0 | 0 | 0 |
| 27: what reshapes Services / 1 | 54 | 54 | 0 | 0 | 0 |
| 31: what reshapes the land office / 1 | 20 | 20 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (a bond) / 1 | 1 | 1 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (the vault's dollars) / 2 | 1 | 1 | 0 | 0 | 0 |
| 38: what reshapes the Build screen / 1 | 40 | 42 | 0 | 2 | 0 |
| 42: what reshapes the construction page / 1 | 10 | 10 | 0 | 0 | 0 |
| 44: what reshapes the no-deposit page (1 x Iron Mine) / 1 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-coast page (1 x Desalination Plant) / 2 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-river page (1 x Hydro Station) / 3 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-bridge page (1 x Highway Bridge) / 4 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-licence page (1 x Engineering Services Office) / 5 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-land page (10000 x House) / 6 | 1 | 1 | 0 | 0 | 0 |
| 49: what reshapes the Sectors list / 1 | 2 | 2 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 2 | 25 | 25 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 3 | 45 | 45 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 4 | 5 | 5 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 5 | 18 | 18 | 0 | 0 | 0 |
| 96: what reshapes Agriculture's Investors page / 1 | 3 | 3 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 1 | 10 | 10 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 2 | 22 | 22 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 1 | 11 | 11 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 2 | 9 | 9 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 3 | 33 | 33 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 4 | 36 | 36 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 5 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 6 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 7 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 8 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 9 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 10 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 11 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 12 | 1 | 1 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 1 | 14 | 17 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 2 | 6 | 13 | 0 | 7 | 0 |
| 63: what reshapes the city's fund / 3 | 8 | 8 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 4 | 0 | 3 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 5 | 18 | 28 | 0 | 10 | 0 |
| 63: what reshapes the city's fund / 6 | 4 | 5 | 0 | 1 | 0 |
| 71: what reshapes the Government Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 2 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 3 | 11 | 11 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 4 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 5 | 19 | 19 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 6 | 0 | 86 | 0 | 86 | 0 |
| 71: what reshapes the Government Overview / 7 | 4 | 10 | 0 | 6 | 0 |
| 74: what reshapes the Trade Overview / 1 | 7 | 7 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 2 | 21 | 26 | 0 | 5 | 0 |
| 74: what reshapes the Trade Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 4 | 0 | 25 | 0 | 25 | 0 |
| 74: what reshapes the Trade Overview / 5 | 27 | 27 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 6 | 9 | 9 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 1 | 24 | 24 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 2 | 39 | 39 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 3 | 44 | 44 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 4 | 55 | 55 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 5 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 6 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 7 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 8 | 10 | 11 | 1 | 0 | 0 |
| 77: what reshapes the Policy hub / 9 | 10 | 11 | 1 | 0 | 0 |
| 84: what reshapes Household money / 1 | 69 | 69 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 1 | 44 | 44 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 2 | 15 | 15 | 0 | 0 | 0 |
| 95: what reshapes Policy, Research's Found page / 1 | 4 | 4 | 0 | 0 | 0 |
| 99: what reshapes Policy, Research's Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 100: what reshapes Policy, Research's Funding / 1 | 9 | 9 | 0 | 0 | 0 |
| Total | 1079 | 1229 | 2 | 148 | 0 |

The orchestrator released the long slot after this result and asked for a
bounded heap diagnostic before another substantial JVM. A read-only parse
of the installed OpenJFX 21 class files (`target/fs5c2-class-fields.py`)
shows `NGCanvas.RenderBuf.savedPixelData` contains an IntBuffer, alongside
its `tex` field, and SWResourceFactory has three static texture caches.
The current walker reads texture fields only. This is a candidate accounting
gap, not its demonstrated cause. `target/FS5c2HeapProbe.java` and
`target/fs5c2-heap-probe.sh` are prepared for one -Xmx768m Monocle/SW JVM,
expected 10–20 s with a 45-second halt guard: eight 512x512 canvases, explicit
renderer save, deduplicated texture/saved-pixel arrays, discarded-root weak
references, then eight comparisons on a six-month scratch town, UI versus
harness-owned canvas buffers and software texture-cache payloads. The scope
was sent for check GO. It has not run. No source, model, screen or bound is
changed for that diagnostic; readiness remains held and the ship marker stays.

## Bounded heap diagnostic 1: negative evidence preserved

On the 11:37 explicit GO, `target/fs5c2-heap-probe.sh` ran one Monocle/SW
JVM at -Xmx768m, exit 0 in about eight seconds; its 45-second halt guard
was not reached. Log `target/FS5c2-heap-probe.log`, original source recovered
as `target/FS5c2HeapProbe-v1.java`, compiled classes preserved separately.
It played only the scratch fixture's six months and eight comparisons.

- Eight 512x512 synthetic canvases held 8,388,608 unique texture-array bytes.
  The explicit renderer save added zero distinct saved-pixel bytes after
  identity deduplication. All eight canvases were collected after the
  diagnostic discarded its roots.
- The small town's current FS5 walker reported [2, 1, 1, 4096]. The extended
  accounting found the same 4096 texture bytes and zero additional saved
  pixels from both UI-only and UI-plus-harness roots. All three static
  software texture caches held zero entries/bytes.
- Sixteen pageTop weak references remained alive after release/GC, while
  `comparisonRoots()` returned zero. This does not establish a leak: the
  diagnostic did not record whether the top nodes were distinct from the
  intentionally kept graph, and omitted `finishPageAnimations`, unlike the
  real `againstNewHeld` comparison.

Thus **zero bytes of the full run's residual 80.5 MiB are explained** by
this probe. The saved-pixel/cache hypothesis is not used for any exclusion.
Static GovernmentScreen inspection creates frame and scroller inside the
supplied pool, so shared-wrapper identity has not been confirmed. The
orchestrator's subsequent conditional shared-wrapper GO was not consumed.
A corrected scope was sent for explicit GO: same limits, but finish
animations as the real helper does, collect fresh-minus-kept identity sets,
print shared top identities, and observe those fresh-only nodes after
release/GC. Prepared `target/fs5c2-heap-probe2.sh` has not run. Model/screens,
source measurement, bound and provisional marker remain unchanged.

## Retaining-root diagnostics 2–4

All are preserved under `target/FS5c2-heap-probeN.log`, with source and
compiled classes in the correspondingly numbered target folders. Each ran
one Monocle/SW JVM, -Xmx768m with a 45-second guard, exit 0 in about eight
seconds. Each used only the same six-month scratch city and eight fresh
Government comparisons. No production source, screen, dial or exclusion
changed, and no full run replayed.

Probe 2 finished animations exactly as `againstNewHeld` does, and collected
only fresh-minus-kept node identities. Every comparison made 145 distinct
fresh-only nodes and shared zero top nodes with the kept graph. All 1160
nodes survived release/GC, while `comparisonRoots()` was zero. This disproves
the shared-wrapper explanation but did not itself identify a root.

Probe 3, the first of two further bounded root JVMs explicitly authorized
at 11:47, found actual paths with weak referents excluded:

```
MonocleApplication.runnableProcessor.queue.queue[11].arg$1.arg$1 -> ScrollPane
KeptBench.ui.governmentScreen.body -> ScrollPane
```

`UserInterface.restoreScroll` queues a callback for every new scroller.
The eight comparisons run within one FX task, leaving their callbacks queued.
A FIFO FX barrier before GC reduced fresh survivors from 1160 to 145.
A kept redraw plus barrier reduced them to zero, but this was a diagnostic
only: a redraw could heal state and is not accepted as the housekeeping mend.

Probe 4 used the second/final root JVM from that authorization. It restored
only the pre-comparison `body` reference, with no redraw. The kept signature
and draw counters remained identical, but 145 fresh nodes still survived.
Thus body restoration alone **failed** the conditional mend premise; no
source edit was made under that conditional GO. Static audit found another
specific owner: `GovernmentScreen.targets` is cleared and filled by each
draw with rank-row Nodes (lines 184 and 1696–1700). Restoring the body does
not restore those map entries. A Node also retains its parent graph, which
the current canvas walker does not follow; that is a second measurement
candidate, not a proven byte exclusion.

The same negative buffer accounting holds in all three probes: eight
synthetic canvases carry 8,388,608 unique texture bytes and no additional
saved-pixel bytes; all eight collect after their roots are discarded.
The small-town UI-only and UI-plus-harness walks each account for 4096 bytes,
and the three software texture caches are empty. The full run's 80.5 MiB
residual is still not quantitatively attributed.

Prepared, not run: `target/fs5c2-heap-probe5.sh`, same size/heap/guard, to
trace the remaining owner after body restoration, compare parent-inclusive
canvas traversal, and restore the captured `targets` contents as well as
`body`. It must show zero fresh survivors with unchanged kept signature and
counters before a corresponding housekeeping mend is justified. Explicit
GO was requested after the two-probe budget was exhausted.

MC2 coordination: gate eaV6vYID stopped on main's section-92 paper premise,
`piecesOnTheBooks` returning zero after its three auction attempts. Existing
FS5 CD9-K code at 8511–8527 keeps those attempts and, when still empty, calls
`booked(g, PAPER[3], ask)`. PAPER[3] is Term 10: DebtManager books 120 months
at the current month, then updates interest; `financesPieces` copies its
debt list, with no month/maturity step between. The identical hard premise
at 8642 passed with one piece in this branch's full run (log line 5115).
That run did not exercise all-auctions-refused fallback, so this is static
coordination, not a claimed causal proof on MC2's state. FS5 owns that helper;
MC2 was not asked to edit it or to waive the premise.

## Probe 5 and the precise Government housekeeping mend

Explicitly authorized probe 5 completed exit 0 in about eight seconds
(`target/FS5c2-heap-probe5.log`). After body-only restoration it found the
remaining actual path:

```
KeptBench.ui.governmentScreen.targets.value[0] -> Row
```

The same 1160 fresh-only nodes fell to 145 after the FIFO barrier, remained
145 after body-only restoration, then fell to zero after restoring the
captured targets-map contents too and draining callbacks. Both pure restores
left the kept signature and draw counters identical; there was no redraw.
Parent-inclusive canvas traversal found two canvases where the current walk
found one, but both measured the same 4096 unique texture bytes. No additional
saved-pixel/cache payload appeared. The full run's 80.5 MiB is still not
quantitatively attributed, and no buffer exclusion was added from this result.

The 12:02 explicit MEND GO authorized only those proven owners and callback
housekeeping. Source commit 5215aa357:

- `KeptBench.showFresh` captures the existing Government `body` reference and
  a copy of its existing final `targets` map contents. Restore puts exactly
  those owners back, without drawing, forgetting a pool, clearing unrelated
  fields, or modifying a node's displayed value. Saved references clear in
  finally; a failed Government draw also restores before propagating its
  exception, preserving any cleanup error as suppressed evidence.
- `comparisonRoots` includes the new saved owners, so forgotten captures
  cannot pass its zero-root assertion.
- A hard regression runs while the Government Spending page is already
  active: seven normal fresh comparisons and one deliberately aborted draw.
  It records only distinct fresh-minus-kept nodes and verifies unchanged
  signatures, counters and node identities plus cleared captures. The non-FX
  driver drains queued callbacks and asserts every fresh node collects.
- Before section 102's original GC/heap check, the non-FX driver also uses a
  FIFO FX barrier. It never waits for itself on the FX thread. All original
  heap, pixel, identity and retention bounds remain intact.

The duplicated method/header descriptions were shortened to pay for this
machinery; each complete premise remains at its method and assertion. No
assertion was removed to meet one-in-one-out. No model/screen source changed,
and the default trace still does not move; no ensemble is owed.

MC2 shipped during the mend. Merge 22a789e8e includes main 2b049188b (.179),
takes its generated indexes before regeneration and preserves both incoming
FS5 known-red rows (12/25 quiet Bank months and section 92's no-paper premise).
They are not silently retired on the earlier .178 run. Main's published Bank
and BusinessDebtManager are accepted only through that merge.

Final merged `--fast`: exit 0, compile 51 s, maps regenerated, zero firm stale
findings (193 soft), `target/FS5c2-fast1.log`. One authorized isolated mend
proof then ran on the same merged source, exit 0 in about eight seconds,
-Xmx768m, 45-second guard not reached. Log:
`target/FS5c2-government-mend-proof.log`; fixture source:
`target/FS5c2GovernmentMendProof.java`. It starts an already active Spending
page on a six-month scratch city and calls the actual new hard regression:
**1160 distinct fresh nodes, zero alive; signature, counters, kept-node
identities and cleared captures all true; aborted draw restored true**.
All four assertions passed. The preserved old-source probes 2/4 retained
1160/145 nodes respectively; the complete owner+barrier operation in probe 5
and the actual source mend collect them without a healing draw.

The current read-only pick remains 24/235 (the same named list above), not
ALL. Review: not needed, no money, saves or threading in the diff. No full
pick, ensemble, dry run, new full-software replay or readiness was authorized.
The marker remains. The actual fresh gate will judge flakes; no dry run is
owed while this pick and publisher scope remain unchanged.

Exact final process numstat against MC2 main, generated indexes, notes and
ship file excluded:

| File | Added | Removed |
|---|---:|---:|
| src/main/java/ham/citybuildersim/KeptCheck.java | 447 | 559 |
| src/main/java/ham/citybuildersim/Observation.java | 1 | 1 |
| src/main/java/ham/citybuildersim/ui/KeptBench.java | 204 | 47 |
| docs/notes/known-reds.txt | 0 | 21 |
| Java code | 652 | 607 |
| Registry-inclusive total | 652 | 628 |

The 21 historical removals still have their individual green/retired-law
rows above. Three FS5 registry responsibilities remain hard/vetoed after the
MC2 merge: heap 102 and the two incoming rows. The previous full run is
evidence for its recorded commits only; it does not establish the new
merged model's heap or claim the entire 80.5 MiB is explained. Awaiting
explicit full-software GO after this provisional mends report.


## Final frozen software run 6: two hard failures remain

On the 12:20 explicit GO, one direct run used frozen source
0446cec5e3d43e35b0f4a7a84be03e12aee3e1fe and main
2b049188bcc8ba1b693beef9303409826936487e (.179). It ran 2026-11-21
12:23:04–12:44:38 (21 min 34 s), **KeptCheck exit 2**, and the foreground
runner was owned through exit. The outer PowerShell execution reported 1
for the failed native command; the direct log records exit 2 and two FAILs.
Log `target/FS5c2-kept6-sw.log` is preserved raw (Windows cp1252 output);
parsed evidence: `target/FS5c2-kept6-audit.json` and
`target/FS5c2-kept6-exclusions.md`.

Configuration exactly matched the preceding direct software run:
`-Xmx2048m -Dprism.order=sw`, C:/ main earth-2.bin/world-1.bin paths, scratch
game files, no Glass/Monocle override. A read-only native jcmd query of owned
JVM 27100 confirmed loaded `com.sun.glass.ui.win.WinApplication`; evidence
`target/FS5c2-kept6-display.txt`. The heap line confirmed SWPipeline. This
was native Windows Glass, not the isolated probes' Monocle. The orchestrator
cleared the laptop slot (13.3 GiB free, no test JVM); Jerus's game stayed
open, PW1's ensemble was on the separate box, and R1g's fast run had ended.
Source/marker were clean and frozen through exit. No other check, replay,
full pick, ensemble, dry run or source edit was started.

The full played-city Government regression passed all four hard assertions:
**1832 distinct fresh nodes, zero alive; signature, counters, kept-node
identity, cleared captures and aborted-draw restore all true**. Fund 63
settled nine new lots through the real book, watching B:2645 for 116 months
against three. Its world-to-hand fill was 4.1935250918588996e7 face at
0.373341462955 (the bid posted 2.428e11); no holding was written in. All
three grid premises passed with zero cells closed, and section 92's identical
paper premise passed with one piece. This run did not print the all-sales-
refused fallback message, so it does not claim that fallback branch ran.

| Hard failure | Actual evidence and disposition |
|---|---|
| Section 71, Spending: Healthcare opened, log line 2586 | Kept content 1454x684; fresh 1446x700. Inner preferred width is 1454 vs 1446; inner height 660 vs 676. Both comparisons around the choice's month printed two held-still diagnostic looks each, all repeating the difference. This is not a nonrepeating KRT1 reading. The original failure is retained. |
| Section 102 objects heap, log line 5273 | Live 1608.1 MiB; 32 canvases, ten heap render targets; measured target payload 18.5 MiB; reported objects 1589.6 MiB, **53.6 MiB over the unchanged 1536 MiB bound**. It remains hard and unexplained. |

The prior run reported 1635.0/18.5/1616.5 MiB; the current run reports
1608.1/18.5/1589.6. The 26.9 MiB arithmetic delta is **not** wholly attributed
to the Government cleanup. The model base changed from .178 to .179 and
the played state demonstrably differs (nine settlements/B:2645 rather than
thirteen/B:3071; one fewer household choice). The lifetime regression proves
release and unchanged state, not a byte allocation for the entire former
80.5 MiB miss. No guessed buffer exclusion or dial change is justified.

Some intermediate milestone-only tails missed the Healthcare failure after
it scrolled out of the selected last lines. That status was corrected before
the final report. The complete scan and four hard held-still traces above
govern this result; no difference is hidden by the zero accepted KRT1 count.

KBP1 traced two blocks, none recurring held still. Both were choice-month
content readings, excluded under the standing law. KRT1 accepted zero layout
readings; Healthcare's four repeated content/width looks stayed hard. Every
one of the **68** reshape calls follows. Totals: **1078 counted of 1228
choices; two content readings, 148 acting choices, zero refused**. No line
lost most of its months to the held-still rule.

| Section/call | Counted | Choices | Readings excluded | Acted excluded | Refused |
|---|---:|---:|---:|---:|---:|
| 23: what reshapes Infrastructure / 1 | 14 | 14 | 0 | 0 | 0 |
| 27: what reshapes Services / 1 | 54 | 54 | 0 | 0 | 0 |
| 31: what reshapes the land office / 1 | 20 | 20 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (a bond) / 1 | 1 | 1 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (the vault's dollars) / 2 | 1 | 1 | 0 | 0 | 0 |
| 38: what reshapes the Build screen / 1 | 40 | 42 | 0 | 2 | 0 |
| 42: what reshapes the construction page / 1 | 10 | 10 | 0 | 0 | 0 |
| 44: what reshapes the no-deposit page (1 x Iron Mine) / 1 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-coast page (1 x Desalination Plant) / 2 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-river page (1 x Hydro Station) / 3 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-bridge page (1 x Highway Bridge) / 4 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-licence page (1 x Engineering Services Office) / 5 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-land page (1 x House) / 6 | 1 | 1 | 0 | 0 | 0 |
| 49: what reshapes the Sectors list / 1 | 2 | 2 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 2 | 25 | 25 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 3 | 45 | 45 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 4 | 5 | 5 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 5 | 18 | 18 | 0 | 0 | 0 |
| 96: what reshapes Agriculture's Investors page / 1 | 3 | 3 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 1 | 10 | 10 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 2 | 22 | 22 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 1 | 11 | 11 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 2 | 9 | 9 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 3 | 33 | 33 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 4 | 36 | 36 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 5 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 6 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 7 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 8 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 9 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 10 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 11 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 12 | 1 | 1 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 1 | 14 | 17 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 2 | 6 | 13 | 0 | 7 | 0 |
| 63: what reshapes the city's fund / 3 | 8 | 8 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 4 | 0 | 3 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 5 | 18 | 28 | 0 | 10 | 0 |
| 63: what reshapes the city's fund / 6 | 4 | 5 | 0 | 1 | 0 |
| 71: what reshapes the Government Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 2 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 3 | 11 | 11 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 4 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 5 | 19 | 19 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 6 | 0 | 86 | 0 | 86 | 0 |
| 71: what reshapes the Government Overview / 7 | 4 | 10 | 0 | 6 | 0 |
| 74: what reshapes the Trade Overview / 1 | 7 | 7 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 2 | 21 | 26 | 0 | 5 | 0 |
| 74: what reshapes the Trade Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 4 | 0 | 25 | 0 | 25 | 0 |
| 74: what reshapes the Trade Overview / 5 | 27 | 27 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 6 | 9 | 9 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 1 | 24 | 24 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 2 | 39 | 39 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 3 | 44 | 44 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 4 | 55 | 55 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 5 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 6 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 7 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 8 | 10 | 11 | 1 | 0 | 0 |
| 77: what reshapes the Policy hub / 9 | 10 | 11 | 1 | 0 | 0 |
| 84: what reshapes Household money / 1 | 68 | 68 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 1 | 44 | 44 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 2 | 15 | 15 | 0 | 0 | 0 |
| 95: what reshapes Policy, Research's Found page / 1 | 4 | 4 | 0 | 0 | 0 |
| 99: what reshapes Policy, Research's Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 100: what reshapes Policy, Research's Funding / 1 | 9 | 9 | 0 | 0 | 0 |
| Total | 1078 | 1228 | 2 | 148 | 0 |

The Healthcare registry row removed after run 5's green result is restored
verbatim from main as FS5's own veto because it recurred in this final run.
The earlier green result remains historical evidence, not a current waiver.
All three existing own vetoes also remain: heap, MC2's quiet-month label and
its section-92 paper premise. **Four own vetoes, no new exemption**. Current
registry removals are therefore 20, not the earlier checkpoint's 21.

Exact process counts, generated indexes/notes/ship excluded, are now:

| File | Added | Removed |
|---|---:|---:|
| src/main/java/ham/citybuildersim/KeptCheck.java | 447 | 559 |
| src/main/java/ham/citybuildersim/Observation.java | 1 | 1 |
| src/main/java/ham/citybuildersim/ui/KeptBench.java | 204 | 47 |
| docs/notes/known-reds.txt | 0 | 20 |
| Java code | 652 | 607 |
| Registry-inclusive total | 652 | 627 |

The read-only pick remains 24/235 with the named list already recorded;
review not needed (no money, saves or threading in the diff). The only
post-run edits are records. No source mend, substantial diagnostic or replay
is assumed authorized. The provisional marker remains.

## KHL1: repeating Healthcare kept-layout fault

Unresolved historical ownership/capture question for Jerus review, placed
under pending by root GO2413. The earlier provisional ship row was under
queued, but KHL1 has NEVER been adopted as a queued fix or named exemption.
Its existing ID/link are retained; no new work order or ID is created.

Full6's original Healthcare choice printed four repeating hard held looks;
kept7's same choice has equal primary pairs on new source/model-state
provenance, with no held look needed. The old failure's leaf and UI-versus-
comparison owner remain unproved. Relevant historical owner candidates are
GovernmentScreen.frameOver, UserInterface.scrolled and
KeptBench.settle/showFresh/restoreKeptPage; they are not a decided mend.
Preserve the original style, geometry, identity, hard equality and heap laws.

Bounded capture may observe a recurrence only during the next independently
required, separately authorized native software run. There is no automatic
standalone historical replay, generic calibration or UI/model work order.
The old53.6MiB excess remains unattributed despite kept7's current green heap.
No known red is retargeted to KHL1; all four existing FS5 fix-ID vetoes remain
for the publisher's normal handling, with no waiver or early reclassification.

## Read-only ownership trace after run 6

On the 12:56 records/read-only GO, no further JVM, probe execution or source
edit ran. The concrete proposed proofs are in `target/FS5c2-next-trace.md`.
Both are separately gated proposals, each at most one native Windows/SW
JVM, Xmx768m, expected at most 20 s with a 45-second guard; neither includes
the 600-month builder or a replay.

Healthcare has a source-level feedback candidate: GovernmentScreen.frameOver
binds page width to scroller viewport width (minimum 320), while its height
follows window space less the frame. UserInterface.scrolled fits the outer
content to width but makes the inner page prefer its bound width. A bar can
narrow text, wrapping can add height, and height controls the bar. Two stable
width/wrapping states are therefore plausible. KeptBench.settle's two quiet
hashes may accept either; its extra scroller kick requires a content-height
change over 400. None of that proves UI versus bench ownership.

The proposed positive causes the wrap/overflow boundary from actual measured
font, viewport and scrollbar dimensions, comparing kept closed-to-open with
fresh construction. Taller space and a wide-seeded fresh initialization
with the same final AS_NEEDED policy are negative controls. A real native
stage/pulse control would need explicit permission for one transparent,
non-maximized stage; without it, the ordinary-UI attribution remains limited.
Anticipated source is KHL1's Government frame initialization only if an actual
UI cause is demonstrated; a bench-only layout/pulse cause remains FS5's.
No stripping style width, tolerance change or redraw-to-heal is proposed.

Heap accounting has a proved traversal-completeness gap (parent ownership
finds an extra canvas in probe 5) but zero explained bytes there. Read-only
installed OpenJFX-21 fields also show NGNode cache/effect buffers outside
NGCanvas: CacheFilter.cachedImageData/tempTexture and NodeEffectInput's cached
ImageData can reach PrTexture/PSWDrawable render textures and pixel buffers.
The UI uses DropShadow/effect styles, but field existence and style use do
not prove any such payload survived at the full heap line. Static evidence
is preserved in `target/FS5c2-render-cache-static-fields.txt` and
`target/FS5c2-effect-buffer-static-fields.txt`.

The proposed heap positive renders a bounded detached canvas graph retained
only through a leaf map entry, comparing current versus parent-inclusive
traversal of actual backing arrays. Direct-canvas ownership is its negative
control. At most one cache/effect node adds an independent actual-peer payload
check, with cache/effect disabled and snapshot copies separated as negatives.
Anticipated source, only after bytes and a separate mend GO, is
KeptBench.canvasTargets traversal/accounting. No proposed subtraction is
chosen to close 53.6 MiB, and that residual remains unexplained.

## Parent-edge completeness diagnostic and mend

The 13:13 GO (`20261121-131353-165-orchestrator-801c`) authorized one bounded
native Windows/SW diagnostic. It exited 0 in 4.397 s (Java monotonic time;
wrapper 13:23:07--13:23:13, second precision), Xmx768m, without a Stage or
the full builder. Four rendered 512x512 canvases were held only through
leaf labels in Government's target map: label.parent reaches the Group
whose children include the canvas. Current forward traversal found zero;
parent-inclusive traversal found four distinct actual texture arrays,
4,194,304 bytes each, totaling 16,777,216 bytes. Direct map references with
duplicate references to each canvas found the identical four arrays and
bytes. The existing production physical-size reader agreed in that control.
Four separate snapshot copies were 1,048,576 bytes each; all copies died
while the map-held canvas graphs stayed alive. After map restoration,
release, FIFO and GC, canvases, labels and renderer arrays were all zero.

The same diagnostic's single controlled cached/effected Rectangle exposed
1,183,744 actual bytes at
NGNode.cacheFilter.cachedImageData.image.rtt.data. Its disabled cache/effect
peer had zero payload. After map release the node died, but that enabled
renderer array was still alive at the last observation. Its remaining root
and lifetime are unknown. Snapshot payloads were kept separate; no cache or
effect exclusion follows. The diagnostic does not attribute any part of the
full run's 53.6 MiB residual. Source, classes, logs and the exact report are
preserved under `target/FS5c2-heap-completeness*` and
`target/FS5c2HeapCompletenessProbe.java`. This original negative was not replayed.

On the parent-only MEND GO (`20261121-132750-572-orchestrator-a245`), commit
4e65bd86b added exactly `push.accept(n.getParent())` inside canvasTargets'
existing identity-visited walk. The target reader, physical-size accounting,
deduplication, model, screens, dials, heap bound and all four own vetoes are
unchanged. No cache/effect subtraction was added. Main stayed .179 throughout
the proof; no merge occurred mid-proof. The generated maps were regenerated.

`target/FS5c2-parent-mend-fast.log`: exit 0, 13:31:11--13:32:11; compile 41 s,
maps regenerated, Stale 0 firm / 193 soft. One changed-source fixture JVM,
`target/FS5c2-parent-mend-proof.log`, exited 0 in 4.769 s, wrapper
13:32:25--13:32:31. Actual Glass was com.sun.glass.ui.win.WinApplication and
renderer SWPipeline; Xmx768m, no Stage, six-month scratch, absolute main data
paths, only four rendered 512x512 canvases and a 45-second guard not reached.
It reused only the old diagnostic's bounded fixture and array readers;
the old main/negative test and cache fixture did not run again.

Both the amended PRODUCTION helper's leaf-held result and its direct plus
duplicate-node control were `[4,4,4,16777216]`, from baseline `[0,0,0,0]`.
Both counts and bytes matched four actual arrays, and the controls compared
the same backing references by identity. Each snapshot copy was proved
separate. After restoring the exact original map, release, FIFO and GC,
weak survivors were canvases 0, labels 0, renderer arrays 0, snapshot images
0 and snapshot arrays 0; all were hard assertions. The control proves
node/map alias deduplication, not two texture objects sharing an array.

Exact process numstat against .179, excluding generated maps, these notes
and the ship file:

| File | Added | Removed |
|---|---:|---:|
| src/main/java/ham/citybuildersim/KeptCheck.java | 447 | 559 |
| src/main/java/ham/citybuildersim/Observation.java | 1 | 1 |
| src/main/java/ham/citybuildersim/ui/KeptBench.java | 205 | 47 |
| docs/notes/known-reds.txt | 0 | 20 |
| Java code | 653 | 607 |
| Registry-inclusive total | 653 | 627 |

The read-only `tools/pick.sh origin/main HEAD` at 4e65bd86b on base
2b049188b (.179) reports 24/235, not ALL: AutoBuildCheck BankCheck
BuildAdviceCheck CapitalFlowCheck ChartFitCheck ConstructionControlCheck
FittingCheck HealthCheck HousingCheck KeptCheck KeptTimingCheck MapCheck
MoneyWordsCheck NewUnitCheck ObservationCheck PriceLevelCheck PrizeCheck
ResearchViewCheck SaveFileCheck ScaleCheck ShortSellingCheck StepsCheck
TimingCheck ToastCheck. Review: not needed, no money, saves or threading in
the diff. No selected suite was executed on this GO.

The latest full-run figures remain live 1608.1 MiB, measured targets 18.5 MiB,
objects 1589.6 MiB, 53.6 MiB over the 1536 bound, with recurring Healthcare
and its four hard held-still looks. The 68-call exclusion table is unchanged.
No amended full-model heap figure is inferred from the small fixture.
KHL1 remains a provisional ownership proposal with no UI-versus-bench cause
or named exemption adopted. No Healthcare, Stage, full replay, ensemble,
dryrun or readiness check ran. The provisional marker stays; next execution
requires the orchestrator's scoped GO.

## PW1 .180 integration and final fast

On GO `20261121-133658-504-orchestrator-2d50`, the completed parent proof and
its records were already checkpointed and pushed at 150c9e02d. Merged
verified main d1d70f0ae (.180) into the same batch/FS5 branch as 6554d848b.
No semantic overlap touched KeptCheck, Observation or KeptBench. Three
generated-index conflicts (dials, harnesses and map README) took main's
version before regeneration. All 13 incoming registry rows and all four
existing FS5 vetoes were retained; the four veto rows compare byte-for-byte
with the checkpoint. No publisher record was manually changed.

The entire KeptBench source blob is identical before and after integration:
995a8099eb4ed11f6df223e9adb78b820335f9c5. The one-line parent mend, actual
texture reader and identity walk are unchanged. The valid 4.769 s bounded
proof was not replayed solely for upstream power changes. This correspondence
preserves its measurement-helper evidence, not a claim about .180's played
city or full-run heap. The old full result remains historical on .179.

`target/FS5c2-main180-fast.log`: final --fast exit 0, 13:40:07--13:41:07,
compile 40 s, maps regenerated, Stale 0 firm / 193 soft. Prior target/checks
artifacts were copied to target/fs5c2-before-main180-checks before this run.
The committed read-only pick is the same named 24 harnesses, now 24/237
because main added two checks. Review remains not needed: no money, saves
or threading in the FS5 diff. Exact process counts remain Java +653/-607,
registry inclusive +653/-627, generated/notes/ship excluded. No suite or
bounded parent replay ran. The marker stays provisional and the four vetoes,
unattributed 53.6 MiB residual and unknown cache-array root stay unchanged.

## Fixture-age correction and bounded Healthcare diagnostic

Source audit after the Healthcare run found a real scope/reporting error:
LongPlaytest.probeCity calls probeRun(3), (4), (5), (6), and probeRun counts
months rather than ending at the argument's month. That is 18 advancing
months and the city was observed at month 19, not a six-month fixture.
The earlier six-month descriptions of the parent proof, completeness
diagnostic and any other artifact using this helper are incorrect. Their
raw source, logs and measured array facts remain preserved, but six-month
scope compliance is not claimed. This was reported to the orchestrator;
no corrected run was presumed authorized or attempted. The parent helper
source remains byte-identical across .180 and was not replayed for the merge.

The static audit explicitly covers recovery FixtureProbe, heap/root probes
1--5 including preserved v1/v2 sources, GovernmentMendProof,
HeapCompletenessProbe, ParentMendProof and HealthcareAccess/Diagnostic:
ACTUAL 18 advances/endmonth19 for each helper-based fixture, not six.
probeCity and probeRun hashes are identical at all five inspected source
checkpoints 76ec61a65, 068f3ab57, 0446cec5e, 4e65bd86b and 5bfa3783b.
target/FS5c2-fixture-age-audit.md and its JSON enumerate source artifacts
and hashes. Historical recovered notes and raw evidence are retained;
current reports carry the correction. A helper call in the recovered
KeptCheck source snapshot does not reclassify its full played-city builder.
The orchestrator acknowledged this in 20261121-135459-668-orchestrator-77cd:
the month-independent controlled parent evidence stands with its fixture
limit; no repeat just to fix wording and no retroactive scope compliance.

GO `20261121-133923-158-orchestrator-4a09` authorized one Healthcare causal
diagnostic after .180 integration/final fast. The target-only diagnostic
ran on frozen 5bfa3783b, actual WinApplication/SW, Xmx768m. It completed all
eight fresh comparisons and exited 1 in 7.018 s monotonic, wrapper
13:50:17--13:50:25. The 45-second total guard did not fire. Exactly one
transparent native Stage, opacity 0, nonmaximized and not always on top,
was reused for two native controls with no requestFocus call and closed
in finally. No full builder or replay ran; no owned JVM remains. Month 19
and the city's cash were hard-asserted unchanged during all comparisons.

Artifacts are `target/FS5c2HealthcareDiagnostic.java`, the target-only
root-package access bridge, `target/fs5c2-healthcare-diagnostic.sh`, compile
evidence and `target/FS5c2-healthcare-diagnostic.log`. All sixteen complete
unmodified signatures are in target/FS5c2-healthcare-signatures. The read-only
audit JSON holds differing rows, endpoints and SHA256 hashes. Its report
is target/FS5c2-healthcare-result.md. Compilation access errors were corrected
before the one diagnostic JVM; those compile logs remain. No source change
or second diagnostic followed the result.

| Comparison | Full signature | Exact disposition |
|---|---|---|
| Natural Healthcare unstaged | equal | Content 1454x374, viewport 1454x696, no bar, both sides |
| Natural held comparison | equal | Clock/model unchanged and no intervening redraw |
| Controlled ordinary wrap positive | equal | Both narrow: content 1446x850, viewport 1446x841, bar 8 |
| Controlled held comparison | equal | Both narrow states remain stable |
| Taller negative | equal | Both wide: content 1454x833, viewport 1454x891, no bar |
| Fresh wide-seeded, final AS_NEEDED | HARD FAIL, 4 rows | Kept narrow 1446x850/bar 8; fresh wide 1454x833/no bar; both held stable |
| Controlled native pulses | equal | Both content 1446.4x850.4, viewport 1446.4x840.8, bar 8 |
| Natural Healthcare native pulses | HARD FAIL, 4 rows | Both content 1454.4x375.2 and viewport 1454.4x694.4/no bar; four icon Stack geometries differ |

The natural premise was not caused. Its only Healthcare text is in the
"nothing this month" list, and there is no "by kind of care" detail in its
signature. Government.listPage leaves Money.nothing amounts out of the
expandable rows. Setting the open-state key therefore did not cause the
actual Healthcare detail expansion that fails on the full city. The natural
equalities cannot be offered as a passing check of that premise.

The controlled frame used the real Government.frameOver and UI.scrolled
paths. Its threshold came from actual 1454/1446 page widths, 8-pixel bar,
IBM Plex Sans Regular 12 font, 275 characters, measured label preferred
heights 17/34, viewport height 841, wrapper padding 24 and filler 791.5.
The taller increment 49.6 was derived from those font/heights. No full-run
684/700 constants were used. The ordinary anticipated positive ended narrow
on both sides. Fresh-only wide seeding instead caused a different stable
geometry under the same final AS_NEEDED policy; it remains a hard failed
negative, not a passing negative or authority for a fresh-only source mend.

The native natural failure is separate from the full run's scrollbar fault:
one Stack kept 13x13 at 6,6 versus fresh 13.6x13.6 at 5.6,5.6; three kept
15x15 at 7,7 versus fresh 15.2x15.2 at 6.4,6.4. Both whole pages agree in
geometry. The kept graph was first constructed unstaged and then migrated
to the Stage; fresh construction happened under that Stage. This history
limits actual-UI attribution. Pixel snapping/scale initialization is only
a hypothesis; native output scale was not measured. No cause or exemption
is adopted from those four differences.

There were 54 hard checks, 2 failures and 377 geometry events. Every held
public-pass initial-signature assertion and every exact restore assertion
passed. Production settle ran unmodified, with geometry events and before/
after endpoints recorded; subsequent held public passes were numbered.
Native pre/post layout pulses were recorded. Quiet internal settle passes
that changed no observed property were not individually numbered, so event
counts are not production-pass counts. NeedsLayout often stayed true even
when signatures stayed equal; this observation alone establishes no owner.

Recommendation: no source MEND on this result. Keep KHL1 provisional, four
own vetoes, the full run's four Healthcare held-still looks and its 53.6 MiB
residual; no cache-root/lifetime attribution. A proposed next causing proof
is target/FS5c2-healthcare-next-scope.md: explicitly counted six monthly
advances, real standing care and positive actual spending, hard actual
Healthcare expansion premise, native attachment before first CSS/layout,
actual-page threshold and taller negative. It needs a new scoped GO and
has not executed. No preferred-width stripping, tolerance change, redraw
to heal, model/UI source mend, full SW, ensemble, dryrun or readiness check
followed. Process counts remain +653/-607 Java and +653/-627 with registry,
generated/notes/ship excluded. The provisional marker remains.

Static refinement of that proposal: General Hospital is the actual template
used by HealthCheck; Game.buildStack(template,quantity,true) is the standing
building API. Six Game.simulateMonths(1) calls, the API used by LongPlaytest.run,
would each require delta1 and total delta6; a refused month stops as LIMIT
without fallback or extra month. Game.nextMonth is private. No proposed
direct NationalAccounts write or fake expansion is allowed. The native
scene would precede first kept CSS/layout, with output scale recorded to
remove the migration confound. This is a proposal only. The laptop slot
was released to WG3 by the orchestrator; FS5 requires a new slot GO for any
future JVM. No source mend is recommended from the current evidence.

## Corrected Healthcare attempt: hospital funding premise absent

On GO `20261121-141248-461-orchestrator-7aa3`, exactly one corrected target-only
diagnostic JVM ran on frozen 4e2769e61/main .180. Actual WinApplication/SW,
Xmx768m, absolute main data paths, fresh scratch Game and played(null)'s
standard founding, no probeCity or full builder. Compilation passed. The
outer process exited 1; Java finally measured 1.297 s and the wrapper ran
14:20:34--14:20:37. The 45-second guard did not fire; no owned JVM remains.

The actual General Hospital template existed, but the required standing
build failed its first premise. At month 1 cash was 2588.3463842978417 and
the actual scaled hospital cost was 3879.1547261471756, a shortfall of
1290.8083418493339 from those printed values. The single real
Game.buildStack(hospital,1,true) returned NEEDS_FUNDING. General care capacity
stayed 1200 before and after. The run immediately stopped as a premise LIMIT.
No funding retry, changed founding, fake cash/ledger, direct capacity write
or substituted building followed. There were ZERO simulateMonths calls,
fresh comparisons and Stage creations. Six monthly deltas, payroll/upkeep,
NA health spending, visible Healthcare row, actual expansion, font/geometry
calibration and ownership controls were unreached, not passing results.
No six-month compliance is claimed for this aborted fixture.

The runner had a separate reporting defect: KeptCheck.fx wrapped the target
Limit in ExecutionException, bypassing its direct-Limit catch. The finally
block therefore printed its default inner exit=0 before the uncaught wrapped
exception caused the authoritative outer exit1. That inner line is incorrect
and is explicitly not a pass. The complete raw log and stack trace remain;
the audit JSON separates the outer exit from the wrong inner field. Any
future target runner must unwrap the FX cause or initialize/report a nonzero
outcome before execution; no corrective repeat or production mend ran.

Artifacts: target/FS5c2HealthcareSixAccess.java,
target/FS5c2HealthcareSixDiagnostic.java, the preparatory source and script,
compiled classes, compile logs, target/FS5c2-healthcare-six.log,
target/FS5c2-healthcare-six-audit.json and target/FS5c2-healthcare-six-result.md.
All previous evidence remains. This attempt gives no Healthcare UI/layout
owner evidence; the diagnostic setup lacked the required building funds.
It establishes no game fault or source-mend authority. The four vetoes,
full-run four hard Healthcare looks and 53.6 MiB unattributed residual remain
unchanged, as do cache-root uncertainty, source, model, dials and bound.
Process counts remain Java +653/-607 and registry-inclusive +653/-627;
the previous 24/237 pick and review line are unchanged. The provisional marker
stays. The result was reported before any further proposal, as instructed;
any funded replacement fixture or further JVM needs a new scoped/slot GO.

## Read-only affordable fixture proposal and target reporting fix

GO `20261121-142357-899-orchestrator-ee79` authorized records/read-only
preparation, not execution. The raw hospital LIMIT, wrong innerexit0,
authoritative outerexit1, source and receipts remain unchanged: all five
artifact hashes from its audit were checked again. New target-only candidate
files fix reporting without overwriting that evidence. CentreAccess unwraps
ExecutionException's Exception/Error cause so Limit reaches its handler;
CentreDiagnostic initializes exit1 and sets success only after normal
completion, with cleanup in finally. This fix is source-reviewed, not compiled
or runtime-verified. No JVM, monthly call or Stage ran on this GO.

One proposed fixture, still HELD: the same standard played(null) scratch
founding, ONE actual Community Health Centre plus forty standing Houses
through ordinary Game.buildStack(...,true), then exactly six counted monthly
calls. Catalogue id19 is HEALTHCARE/GENERAL, capacity12000, cash9569,
upkeep190, land90000 and69 jobs (12 no diploma,20 diploma,26 college health,
3 college business,8 doctors). House id0 has capacity6/dwellings1, cash266,
land8000. This reaches the same GENERAL care-accounting and Government
detail branch as the hospital, with no template, cash, capacity or ledger
write. Every actual build result/increment would remain required SUCCESS.

The static opening bill uses PriceLevel.FACTOR=8.333/321.943 from source:
Centre247.67886551346046 plus40 Houses275.40005528929034 equals
523.0789208027508, against standard cash2588.3463842978417, leaving
2065.267463495091. The Centre's opening upkeep is4.917858130165899.
BuildingCatalog reads the definitions, BuildingsTemplate prices cash/upkeep
at that factor, Game.FOUNDING_CASH uses the same factor, and the standing
build path bills actual quantity*cashCost. These are static figures, not a
new runtime quote; ordinary build checks and later monthly repricing remain.

The Houses address a separate staffing premise: founding hundred household
places are aggregate capacity, while getTotalHomes/homesBySize count actual
residential doors. Migration caps arrivals at FamilyModel.roomLeft. Real
doors plus the Centre's jobs let normal migration/cohort/labour paths staff
its twelve no-diploma posts without manufactured residents, skills or fill
rates. Positive payroll by month6 is still a required observation, not a
static guarantee. Any absent premise stops as LIMIT without fallback.

The ordinary monthly category payroll/upkeep reaches Healthcare.advanceMonth,
then EconomyManager.setHealthcare and NationalAccounts health spending.
Government.healthSpendDetail reads the same care payroll/upkeep by GENERAL
type and builds the real "by kind of care" detail. All positive payroll,
upkeep, NA, visible row, actual expansion and changed-signature premises stay
hard, as do six delta1 calls and delta6 total. No source mend is established.

The exact proposal, source/static bill and causal trace are in
target/FS5c2-healthcare-affordable-proposal.md; its static audit JSON records
catalogue inputs, candidate hashes and unproved payroll. Candidate bridge,
diagnostic and runner are prepared under unique Centre names and output
paths; none has been compiled or executed. A future run would still need
a NEW scoped/slot GO explicitly covering the Centre and forty Houses,
native Stage before first CSS/layout, separate actual-page calibration,
at most four hard fresh comparisons, taller negative and held checks,
nativeWin/SW Xmx768m expected20s/hard45s TOTAL. The laptop is unallocated.
Four vetoes, full four Healthcare looks, 53.6 MiB residual, cache-root
uncertainty and provisional marker remain unchanged. Production and process
counts are unchanged; this is records/preparation only.

## Valid six-month care fixture; calibration LIMIT before comparisons

GO `20261121-143852-464-orchestrator-2620` authorized the exact Centre+40 Houses
fixture and one diagnostic. Target-only compilation passed. The one foreground
JVM used frozen c4a642bb4/main .180, actual WinApplication/SW, Xmx768m and main
data paths. Java and Bash exited 2 LIMIT, agreeing with the corrected inner
exit2; PowerShell surfaced a generic failed-native-command exit1, recorded
separately. Monotonic elapsed 4.105 s, wrapper 14:41:25--14:41:30; the 45-second
guard did not fire. No owned JVM remains. No duplicate run followed.

Actual initial cash2588.3463842978417, Centre247.6788655134605 and
House6.885001382232259 made the actual standing bill523.0789208027509.
Both builds succeeded, GENERAL capacity increment12000 and doors0->40 passed.
No changed founding or injected cash, capacity, ledger, residents, skills or
fill rates. Six and only six simulateMonths(1) calls, every delta1/returned1,
advanced month1->7, passing the corrected age premise:

| Call | Before | After | Returned | Population | Workforce |
|---|---:|---:|---:|---:|---:|
| 1 | 1 | 2 | 1 | 38 | 0 |
| 2 | 2 | 3 | 1 | 74 | 22 |
| 3 | 3 | 4 | 1 | 76 | 44 |
| 4 | 4 | 5 | 1 | 94 | 45 |
| 5 | 5 | 6 | 1 | 110 | 56 |
| 6 | 6 | 7 | 1 | 130 | 66 |

Actual care payroll6.091950603069947, upkeep4.917858130165899 and NA health
spending11.009808733235847 passed all positive guards. Visible Healthcare,
real visible "by kind of care" detail and closed/open full-signature change
also passed. The sole opacity0/nonmax/non-always-on-top native Stage preceded
first kept draw/CSS/layout, had output/render scale1.25 on both axes, never
requested focus and closed in finally. The FX cause/default-exit fix handled
the actual later Limit correctly; the old wrong inner0/raw receipt and all
five hashes remain unchanged.

The separate calibration had viewport1454.4x694.4 and content1454.4x502.4
under NEVER; under ALWAYS viewport1446.4x694.4 and content1446.4x502.4.
The8px bar was measured, but content heights were equal: no wrap threshold
was caused. It then stopped LIMIT at its selector for the supposedly visible
"discounted by how many" paragraph. Source Government.payers/notes (1771--1778)
and noteLine (592) put that prose behind the information icon. The saved
opened signature has actual care detail but no "discounted" text. This was
a wrong target-calibrator assumption, not a missing care premise or UI fault.
There were 65 geometry events and ZERO fresh comparisons. Positive, taller
negative, held equality/restore and the final post-comparison model-held
assertion were unreached; none is claimed passing. The scratch calibration
policy was still ALWAYS at the stop, before the comparison's AS_NEEDED
reset; the Stage and process ended. No persistent player UI was touched.

Raw source, compile, classes, log and two full calibration signatures remain.
target/FS5c2-healthcare-centre-result.md reports actuals; its audit JSON has
all six calls, exact figures and hashes, plus the separate PowerShell receipt.
Log decoding is cp1252, signatures UTF-8, with raw bytes unchanged.

On follow-up records/read-only GO `20261121-144530-648-orchestrator-5127`,
the exact actual visible wrapping candidate was identified from saved
signature row133 and source Government.listPage (1705): the Label beginning
"nothing this month: Pensions ...", actual347-character model-produced text,
font IBM Plex Sans11, wrapText true, minWidth0, width1417.6/height32 at
18.4,243.2 in actual page1454.4. Its exact entire text/signature is saved in
target/FS5c2-healthcare-visible-node.json. It is the direct child of the
wide page in retained scope Spending/page/nothing, inside UI.scrolled's
content; frameOver binds that page's preferred width to the body's viewport.

The corrected READ-ONLY design in target/FS5c2-healthcare-calibration-next.md
uses that existing node's prefHeight over actual widths, with no replacement
text, new Text/Label, tooltip opening or synthetic node. It would require
actual responsive label widths and actual total-content height delta before
constructing a midpoint viewport positive and a measured taller negative.
No delta means STOP LIMIT. It also uses the captured no-bar chrome1.6 rather
than the live ALWAYS width's9.6, which includes the8px bar; this corrects an
unreached candidate-width expression, not an observed full-fault cause.
No target Java source was changed or rerun in this static follow-up.

Relevance remains bounded: this is an actual visible Spending node governed
by the same page/scroller binding as the original full Healthcare-open red.
Opening the detail can change the content-height relation without changing
zero-lines text. But full6's four held looks (2551--2578) and failing report
(2579--2586) identify only signature rows59/60, the containing Columns,
1454x684/1446x700 and inner1454x660/1446x676. They do not identify the leaf
that grew. "The next60" is a signature-row index, not sixty printed leaves.
This candidate's presence/text in that failed full city is unproved from
those reports. A16px change is compatible with wrapping but establishes no
owner. A corrected small proof could show an actual-page mechanism only;
it would not alone attribute the retained four-look failure or justify a
production mend. No new JVM is requested on an original-owner claim.

No new rows, production/source mend, full SW, heap/cache measure, ensemble,
dryrun or readiness action followed. Four vetoes, full four hard Healthcare
looks, 1536MiB bound,53.6MiB residual and cache-root uncertainty stay hard.
Process counts +653/-607 Java, +653/-627 registry-inclusive, pick24/237 and
review line are unchanged. The provisional marker stays; awaiting the
orchestrator's decision and any NEW scoped/slot GO.

## Actual-page mechanism attempt: UNCAUSED at actual width response

GO `20261121-145602-061-orchestrator-db9d` authorized only the corrected
target calibrator and one mechanism-only diagnostic. Target-only compile
passed. ONE native WinApplication/SW Xmx768m foreground JVM ran on frozen
a81bcdee5/main .180. Java/Bash exit2 LIMIT and inner2 agree; the PowerShell
generic native-command failure1 is retained separately. Monotonic4.722s,
wrapper15:03:27--15:03:33. Total45s guard did not fire. The sole opacity0,
nonmax/non-always-on-top Stage preceded first kept CSS/layout, scale1.25,
made no focus request and closed in finally. No owned JVM or repeat remained.

All unchanged real fixture premises passed again: actual Centre+40 Houses
affordable/successful, capacity+12000, doors0->40; exactly six monthly calls
returned1/delta1, month1->7, same population/workforce rows as the2620 table
above, ending130/66. Payroll6.091950603069947, upkeep4.917858130165899 and
NAhealth11.009808733235847 positive, visible Healthcare, actual by-kind
detail and closed/open full-signature change passed. No model state injection,
changed founding, synthetic text/node, tooltip, fallback or extra month.

The same existing347-character Label retained actual text/font/parent and
passed initial actual viewport-width response. Existing Label.prefHeight only,
16 bounded bisection steps, predicted32/48 at widths835.80388184/827.80388184.
Captured NEVER chrome1.6 plus actual page offset36.8 produced candidate
stage874.20388184, actually snapped874.4. Actual calibration then was:

| Sample | Viewport | Page width | Label | Content height |
|---|---|---:|---|---:|
| Initial NEVER | 1454.4x694.4 | 1454.4 | 1417.6x32 | 502.4 |
| Initial ALWAYS | 1446.4x694.4 | 1446.4 | 1409.6x32 | 502.4 |
| Derived NEVER | 873.6x686.4 | 1028 | 991.2x32 | 502.4 |
| Derived ALWAYS | 865.6x686.4 | 1028 | 991.2x32 | 502.4 |

The page's preferred width followed the viewport, but actual page width
remained1028 and the actual Label remained991.2 at both derived widths.
It never reached the predicted wrapping transition. This observes overflow
at those widths, not a proved absolute minimum or its individual owner.
The actual-label-narrows guard stopped LIMIT; no constraint, preferred width
or signature was stripped to force it through. Label/content height delta
was absent. Font prefHeight prediction is not an actual layout cause.

Mechanism UNCAUSED, ZERO fresh comparisons. Midpoint positive, taller negative,
held/restored equalities and final post-comparison model-held check were
unreached, not passing. There were106 geometry events. The six complete
premise/calibration signatures, source, script, classes, compile and raw log
remain under target/FS5c2-healthcare-mechanism*. The actual result and audit
JSON preserve samples/hashes plus all six monthly rows; earlier2620 and all
five original raw hashes were checked unchanged. No duplicate execution.

No original full Healthcare leaf/owner attribution, production/source MEND,
new row or exemption, full SW, heap/cache measurement, ensemble, dryrun or
readiness follows. Four vetoes, full four Healthcare hard looks,1536MiB bound,
53.6MiB unattributed residual and cache-root uncertainty remain unchanged.
Process counts/pick/review are unchanged. The provisional marker stays.
Awaiting the orchestrator's next scoped decision; no further JVM is owned.

## Static 1028 owner and original-leaf capture feasibility

GO `20261121-150833-721-orchestrator-2879`: static saved-artifact/source review
only, no compile/JVM/month/Stage/calibration/source mend. The production
minimum path is now identified: spendingPage's three percentage columns
contain servicesCard, whose serviceLine care coverage bar is fixed300 by
Pieces.bar. Card padding14 plus1px border snap15.2 per side at the observed
1.25 scale, requiring330.4. JavaFX21's percentage-column minimum bound
requires330.4*3=991.2, exactly the saved grid width. widePage's18.4 snapped
side insets give991.2+36.8=1028, exactly the observed page. This path is
sufficient; no ablation or claim of uniqueness against every co-limiter.

The prefWidth binding was working. UI.scrolled's fitToWidth and maxWidth
USE_PREF_SIZE do not cancel the content's computed minimum. OpenJFX21
ScrollPaneSkin bounds the requested width by min/max, with minimum winning
a conflict. That explains preferred873.6/865.6 and actual1028, hence label991.2.
No constraint stripping is recommended. Both original widths1454/1446 are
above this bound, so it does not explain their16px full-fault delta. The
version21 graphics/controls source artifacts were read as text only, with
URL/SHA256 provenance under target/fs5c2-jfx21-static. Exact source path,
formula and saved-row references are in target/FS5c2-1028-owner-static.md.

Original capture provenance is source0446cec5e on main.179, nativeWin/SW,
2048MiB, Government's unstaged1456x900 scene. Current.180 contains PW1 model
changes and the later parent walk; a current rebuild is not exact history.
playedCity first uses probeCity18/end19, then pins minimum wage and calls
probePlay(g,600). Static nuance:600 is a toMonth TARGET, not600 additional
advances; advisor/post-look months can overshoot. Sealed full6 began at605/
3365 people and entered Government at2364 after the preceding mainSections
mutations. The full original main/Government source prefixes are archived
from that commit under target, with blob/tree hashes in the static audit.

Government's own pre-choice prefix has four25-month69 runs, four25-month70
runs, five page-pick months, line-add/release UI controls, then Overview13,
Revenue31 and Spending11 choices before Healthcare opens. That is260 month
calls after2364 before the target hand;2624/2625 are a source-derived candidate
only if every call advances once, not observed fault-month records. The old
clipped four-look report exposes only containing rows59/60, not a leaf.

The old PID27100 scratch root/autosave are absent, consistent with TempDirs
shutdown cleanup; no target hprof was found. Logs/source constrain intent
but cannot restore full model/pool/scrollbar/callback state; a model-only
save would not reproduce live JavaFX history. There is no demonstrated cheap
retained-state shortcut. The original data paths are recorded, but their
then-byte hashes and JDK patch were not sealed; those limits stay explicit.

target/FS5c2-original-leaf-capture-feasibility.md defines the smallest payload:
retain the EXISTING full kept/fresh signature arrays at both primary and
held capture sites, emit indexed JSON after existing restores, and identify
the actual changed leaf offline. Tag the first false-debt Healthcare choice
and both before/after-month phases; preserve all existing hard looks without
extra layout, redraw or retry. A prefix diagnostic would stop nonzero only
after that choice's existing hard verdict; absent reproduction is LIMIT,
never a pass or later-label substitution. It is not a completed heap/full check.

Prefix duration was not separately measured; the only corresponding full
cost is1294s/21m34s. A faithful attempt requires the city plus full earlier
model/UI prefix and69/70 warmup, native Windows/SW/2048MiB and an explicit
long laptop slot; BOX Monocle is not the original display provenance.
No new/historical replay is requested merely to find one assertion. Recommend
a small read-only observer piggybacked on a future independently-required,
specifically authorized final-source check; any recurrence has that new
source/model provenance, not automatic attribution of an identical old leaf.

All prior raw hashes were rechecked unchanged. Four vetoes, full53.6MiB
residual/1536MiB bound, cache-root uncertainty, source and marker stay hard.
No new row, waiver, readiness or execution request. Waiting for next explicit GO.

## FS5c2 GO2c73: optional original-choice observer, preparation only

Authorized scope: implementation in KeptCheck and required --fast only,
piggybacking a future independently required final-source software/heap
measurement. This is not a historical replay or execution GO. The branch
retains the parent walk and .180 model merge; neither has a later full
software result. No model, screen, dial, publisher or registry changed here.

Enable only when separately authorized, with
`-Dkeptcheck.capture.healthcare=UNIQUE_TARGET_DIRECTORY`. Only the first
`governmentChoices(false)` section71 call opts into the observer, and only
the exact `Spending: Healthcare opened` choice consumes it. That selection
is consumed even if its premise refuses; other choices and the later
same-label debt list cannot retry capture. Default operation writes nothing.

The existing primaryA/primaryB calls and their existing held looks are
unchanged. After each existing restoreKeptPage, the observer writes the
already-created full kept/fresh List<String> arrays as UTF-8 JSON; zero-based
array indices are the original logical rows and embedded newlines stay
inside their unmodified strings. Files are primaryA-primary.json,
primaryA-held1.json, primaryA-held2.json and the corresponding primaryB
names, only when those comparisons actually occur. CREATE_NEW refuses
overwriting evidence. It adds no signature, comparison, retry, draw,
layout, settle, pulse, snapshot, month, Stage or asynchronous work.

Metadata is narrowly allowlisted: configured source/base and earth/world
hash receipts (`keptcheck.capture.source`, `.base`, `.earth.sha256`,
`.world.sha256`), actual compiled KeptCheck/KeptBench class SHA256, version,
Java/OS identities, prism/Glass/Monocle flags, configured absolute data
paths with actual file sizes/modified times, loaded Glass/pipeline classes,
and actual month/population. Source/base/input hashes are explicitly
labelled configured receipts, not independently verified source/data
fingerprints. A future launcher must seal those against its frozen inputs.
Restored-kept metadata includes govPage/sorted open keys, scene/root/window
dimensions, output scale if there is a window, body/content/viewport bounds,
scrollbar policies/values and actual bar visibility/geometry. Fresh geometry
remains in the full original fresh signature, not inferred from restored
state. Reflection/lookup only reads; existing unstaged display stays unchanged.

Data lifetime argument (static review, NOT a runtime weak-release proof):
the sole active static reference holds only directory/phase Strings,
month/look integers, population long and disabled boolean. It is armed only
around one original againstNewHeld chain, and cleared in finally after
primaryA and again after primaryB, including exceptions. It is inactive
between those calls and after the exact choice. Signature arrays, JSON map,
copied string metadata, class bytes, reflection receivers and writer/Gson
are local to one synchronous capture call. No fresh Node or Game is stored
in a capture field or payload; each writer closes before return. Nothing
archives signature history for heap102. Class/library initialization may
have its ordinary runtime footprint; that has not been measured or subtracted.
An observer Exception disables further writes for this choice and prints
the error while the original hard verdict/held looks/restoration/full
continuation/exit remain unchanged. There is no prefix stop or heap skip.

One-in/one-out: 32 repeated section narrations were condensed, removing
172 net comment lines; target/FS5c2-observer-comment-audit.json preserves
their old/new text. All original WHY lines, prints and assertion tokens
remain. target/fs5c2-observer-static-audit.py reverses only the allowlisted
observer edits and verifies the remaining Java token stream equals 0c94a0b
exactly after comments; it also rejects extra UI/month/thread operations
inside the observer and confirms scalar/String context fields. This is
static correspondence, not an executed observer/JSON/reflection fixture.
The actual incremental KeptCheck diff is +171/-207; it does not claim
fabricated code removals. Against origin/main da43868f (same .180 source
base d1d70f0a), process counts excluding generated maps/notes/ship are:

| file | added | removed |
|---|---:|---:|
| KeptCheck.java | 613 | 761 |
| Observation.java | 1 | 1 |
| ui/KeptBench.java | 205 | 47 |
| Java machinery total | 819 | 809 |
| docs/notes/known-reds.txt | 0 | 20 |
| Including registry | 819 | 829 |

Validation: target/FS5c2-observer-fast.log, foreground-owned through exit0,
2026-11-21 15:49:30-15:50:29 Atlantic; compile41s, regenerated maps,
Stale0 firm/193 soft. Prior target/checks copied first to
target/fs5c2-before-observer-checks. git diff --check is clean. No UI probe,
full KeptCheck, heap test, ensemble, dry run or readiness check was run.
All26 hash receipts across the hospital/2620/db9d audit files still match,
including the old inner-exit0 defect and outer receipt. The registry is
byte-identical to 0c94 after newline normalization, with all four vetoes kept.

Pick remains24/237: AutoBuildCheck BankCheck BuildAdviceCheck
CapitalFlowCheck ChartFitCheck ConstructionControlCheck FittingCheck
HealthCheck HousingCheck KeptCheck KeptTimingCheck MapCheck MoneyWordsCheck
NewUnitCheck ObservationCheck PriceLevelCheck PrizeCheck ResearchViewCheck
SaveFileCheck ScaleCheck ShortSellingCheck StepsCheck TimingCheck ToastCheck.
Review line: not needed - no money, saves or threading in the diff. The
orchestrator's explicit observer review is still required before execution.

No newly captured pair or new green is claimed. Historical full6 still has
Healthcare's four hard held looks and objects1589.6MiB >1536 by53.6MiB;
the1832->0 Government hard release regression and68 reshape-call exclusions
remain its actual results. Parent/cache/original-leaf causality limits and
all raw probes/history stay intact. Provisional checkpoint for review;
waiting for a separate explicit full-software slot/execution GO.


## FS5c2 GOed03: final-source software run 7 green, still provisional

Root accepted observer aa570ab455ec902dfbb3594fd77ab78722236e55, then
explicitly authorized ONE final-source native Windows software run after
parent-edge4e65 and the .180 model merge. This was that independently owed
measurement, not a replay to find the historical Healthcare leaf. No other
compile, fast, diagnostic JVM, probe, pick, baseline, ensemble, dry run,
source mend or readiness check ran. No source/records edit or push occurred
while the job was alive. The ship marker and all four vetoes remain unchanged.

Actual direct KeptCheck result: exit0, ALL OK, 2026-11-21
16:03:46-16:19:34 Atlantic, monotonic947.758s (15m47.758s). Owned JVM16348
exited normally; the2400s resource/hang guard did not fire. The foreground
Python runner completed its post-exit sealing at16:19:39 and also exited0.
No owned process remains. The full5308-line cp1252 log was scanned, not
only its tail: zero FAILs, observer-disable errors or uncaught-exception
lines;4128 OK-terminated lines. Existing continuation reached102 and103.

### Frozen inputs and actual host

The prelaunch slot receipt records zero Java processes and13,153,228KiB
free physical memory. Configuration: direct native JDK21.0.4+8-LTS-274,
`-Xmx2048m -Dprism.order=sw`, original unstaged bench, scratch GameFiles,
absolute C:/ main earth-2.bin/world-1.bin paths, no Glass/Monocle override,
added Stage, viewport/constraint adjustment or fixture/prefix alteration.
Only the accepted observer's output/source/base/data-hash properties were
added. The same frozen command and JDK_JAVA_OPTIONS are in the pre receipt.

Source aa570ab455ec902dfbb3594fd77ab78722236e55;
tree85593ac35c36946a93088db7dee646938402fae2;
origin/main da43868fb86115c2ce41ca669915609f79704170;
merge-base d1d70f0aee8312bb86c888f8cf9831c71322754b (.180).
Before launch, target/FS5c2-kept7-pre.json sealed all2894 tracked files,
2438 class/resource files,11 classpath files, five JDK/launcher files,
the JDK release text and both data files. After exit,
target/FS5c2-kept7-post.json independently recomputed these manifests:
HEAD/tree/base/merge-base/status/tracked/classes/classpath/JDK/release/data
and prior-raw categories ALL unchanged. All26 prior hash receipts
(25 distinct files, including raw2620/db9d/old5 and wrong-inner-exit0 receipt)
still match. No source/class/data correspondence is inferred from names alone.

| Actual input | SHA256 |
|---|---|
| earth-2.bin,281761695 bytes | c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b |
| world-1.bin,8712947 bytes | fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1 |
| KeptCheck.class | a6db535c87ded57be62c44591a7584710a1265099f3552b98076c1b816fd49ed |
| KeptBench.class | bb9cd132a67f2aea696ea250c8132432312113dc1e9ad5ac03e42455dcfb1922 |
| KeptCheck$HealthcareCapture.class | b475a55715c829f0fc25a789eb242025381304e3ed0c54247e76a51cc74c45e5 |

Read-only native module inspection of the owned process confirmed the
expected jvm.dll/java.exe21.0.4, glass.dll and prism_sw.dll, with no D3D
module in the selected native inventory. The accepted observer then
recorded actual com.sun.glass.ui.win.WinApplication and
com.sun.prism.sw.SWPipeline with window=null in both captured pairs.
No inspection/model JVM or attach diagnostic was launched.

### Full-run premises and heap

The watched city actually began at month605/population4903, unlike full6's
3365. Government entered at month2369 (full6:2364); the exact target primary
captures were at2629/2630, population35973/35946. These are current measured
states, not reconstructed historical fault months.

Government's hard release regression:1832 distinct fresh nodes, zero alive;
signature, counters, kept-node identity, roots cleared and aborted restore
all true, all four assertions green. Fund63 settled ONE actual new holding
through its real book; B:3102 ran116 months beyond the three watched,
and the rule was restored with17->18 lots. All three Household grid holds
passed with0 closed; the unheld month would also have closed0 in this run,
so this does not newly exercise a would-close negative control.

Bank55 Profit printed25/25 quiet redraws, maximum0 new nodes. The old
`most months make no node at all (12 of25)` label was already retired by
the common caused law; no new assertion with that name is invented.
Section92 started without paper, then sold Term10 through the real model
(LongTermBond due month4458). Its unchanged hard premise found ONE paper
and passed. The all-sales-refused booked fallback was NOT exercised.

| Full current heap reading | MiB/count |
|---|---:|
| Live after release | 1521.3 MiB |
| Canvases found | 45 |
| Actual render targets / on heap | 10 / 10 |
| Measured heap render-target payload | 18.5 MiB |
| Objects, live minus measured targets | 1502.8 MiB |
| Unchanged hard bound | 1536 MiB |
| Current margin | 33.2 MiB |

The heap assertion passed strictly, without cache/effect/snapshot exclusions
or a bound/dial change. Full6's live1608.1/targets18.5/objects1589.6 and
historical53.6MiB excess remain their actual recorded result. The current
live/object arithmetic is86.8MiB lower, with the same18.5MiB target payload.
This is not a controlled attribution to cleanup or parent traversal: model
base, played population, reachable funding states, choices and retained
graph differ. Parent traversal's earlier backing-array proof remains valid;
it does not assign these86.8MiB or the old53.6MiB to a root. Cache payload
root/lifetime and historical Healthcare ownership remain unproved.

### Original-choice capture: current equality, no historical leaf cause

The observer executed successfully at the EXACT first
governmentChoices(false)/section71/Spending: Healthcare opened choice.
It wrote only its two existing primary pairs, after restore:

| Pair file under target/FS5c2-kept7-capture | Month | Population | Kept/fresh rows | Differing rows |
|---|---:|---:|---:|---:|
| primaryA-primary.json | 2629 | 35973 | 252/252 | 0 |
| primaryB-primary.json | 2630 | 35946 | 252/252 | 0 |

Each array preserves eight rows containing embedded newlines, along with
every other original string and logical index. No held pair is missing:
both existing primary comparisons were equal, so the original code made
NO held look. There was no added comparison or later-label capture retry.
All configured source/base/data hashes, actual class hashes, actual data
sizes, display metadata and untouched arrays match the sealed receipts.
Both captures' restored-kept metadata reports govPageSpending, body1456x698, content1454x682,
viewport1454x696, with both scrollbars invisible; window=null. Open keys,
full original signatures and other allowlisted metadata remain in raw JSON.

target/FS5c2-kept7-row-diffs.json records empty differing-row/path lists for
both pairs. There is NO differing leaf to name on this current provenance.
The old full6 four-look fault/1454x684-vs1446x700 container report remains
historically valid and its leaf remains unavailable. Current equality does
not assign its cause, prove a UI-versus-bench owner or justify a waiver.
Metadata/reflection/JSON writes now have execution evidence; a byte footprint
or separate weak-release proof for observer payloads was not measured.

### Readings, actual reshape calls and every excluded month count

KRT1 accepted0 layout readings and printed0 held content looks. KBP1 traced
FOUR blocks, all making0 nodes on the held redraw, with0 recurring faults:
Build Overview twice72/414 (78 new/80 dropped, then80 new/78 dropped), and
Policy currency twice22/201 (22 new/134 dropped, then22 new/114 dropped).
Both Build blocks remain in the full25-month kept watch. Only the two
Policy Money > The currency choice-month blocks are excluded as permitted
content readings (89.1% handed back; section77 calls8 and9 each count10/11).

Actual share summaries:73 calls, not full6's68. One additional Land33
funding call and four Build44 funding/fell-short calls were exercised by
this played state, each with one choice/month. Household84 also had72
choices versus68 in full6. These account for all nine additional reported
choices:1237 versus1228. No harness call or model month was inserted for
the observer. The extra page labels and all totals are recorded in
target/FS5c2-kept7-audit.json. Initial parser's assumed68 failed; its only
correction was to count/check ALL raw summary lines, not change software.

Totals:1087 counted of1237 choices, two content readings excluded,
148 acting choices excluded,0 refused/no-month choices. Every call follows;
there is no new exclusion, hidden held-still difference or repeated-fault pass.

| Section/call | Counted | Choices | Readings excluded | Acted excluded | Refused |
|---|---:|---:|---:|---:|---:|
| 23: what reshapes Infrastructure / 1 | 14 | 14 | 0 | 0 | 0 |
| 27: what reshapes Services / 1 | 54 | 54 | 0 | 0 | 0 |
| 31: what reshapes the land office / 1 | 20 | 20 | 0 | 0 | 0 |
| 33: what reshapes the funding page / 1 | 1 | 1 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (a bond) / 2 | 1 | 1 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (the vault's dollars) / 3 | 1 | 1 | 0 | 0 | 0 |
| 38: what reshapes the Build screen / 1 | 40 | 42 | 0 | 2 | 0 |
| 42: what reshapes the construction page / 1 | 10 | 10 | 0 | 0 | 0 |
| 44: what reshapes the no-deposit page (1 x Iron Mine) / 1 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-coast page (1 x Desalination Plant) / 2 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-river page (1 x Hydro Station) / 3 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-bridge page (1 x Highway Bridge) / 4 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-licence page (1 x Engineering Services Office) / 5 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-land page (10000 x House) / 6 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build funding page (1000 x Metro Line) / 7 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build fell-short page (2 x Gravel Road built, 1000 x Metro Line left) / 8 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build fell-short page (none built, 1000 x Metro Line left) / 9 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build funding page (532 x Studio Apartments) / 10 | 1 | 1 | 0 | 0 | 0 |
| 49: what reshapes the Sectors list / 1 | 2 | 2 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 2 | 25 | 25 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 3 | 45 | 45 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 4 | 5 | 5 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 5 | 18 | 18 | 0 | 0 | 0 |
| 96: what reshapes Agriculture's Investors page / 1 | 3 | 3 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 1 | 10 | 10 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 2 | 22 | 22 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 1 | 11 | 11 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 2 | 9 | 9 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 3 | 33 | 33 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 4 | 36 | 36 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 5 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 6 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 7 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 8 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 9 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 10 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 11 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 12 | 1 | 1 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 1 | 14 | 17 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 2 | 6 | 13 | 0 | 7 | 0 |
| 63: what reshapes the city's fund / 3 | 8 | 8 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 4 | 0 | 3 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 5 | 18 | 28 | 0 | 10 | 0 |
| 63: what reshapes the city's fund / 6 | 4 | 5 | 0 | 1 | 0 |
| 71: what reshapes the Government Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 2 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 3 | 11 | 11 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 4 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 5 | 19 | 19 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 6 | 0 | 86 | 0 | 86 | 0 |
| 71: what reshapes the Government Overview / 7 | 4 | 10 | 0 | 6 | 0 |
| 74: what reshapes the Trade Overview / 1 | 7 | 7 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 2 | 21 | 26 | 0 | 5 | 0 |
| 74: what reshapes the Trade Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 4 | 0 | 25 | 0 | 25 | 0 |
| 74: what reshapes the Trade Overview / 5 | 27 | 27 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 6 | 9 | 9 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 1 | 24 | 24 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 2 | 39 | 39 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 3 | 44 | 44 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 4 | 55 | 55 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 5 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 6 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 7 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 8 | 10 | 11 | 1 | 0 | 0 |
| 77: what reshapes the Policy hub / 9 | 10 | 11 | 1 | 0 | 0 |
| 84: what reshapes Household money / 1 | 72 | 72 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 1 | 44 | 44 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 2 | 15 | 15 | 0 | 0 | 0 |
| 95: what reshapes Policy, Research's Found page / 1 | 4 | 4 | 0 | 0 | 0 |
| 99: what reshapes Policy, Research's Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 100: what reshapes Policy, Research's Funding / 1 | 9 | 9 | 0 | 0 | 0 |
| Total | 1087 | 1237 | 2 | 148 | 0 |

Accepted process counts remain Java+819/-809, including registry+819/-829,
generated maps/notes/ship excluded. The accepted aa570 pick24/237 and review
"not needed - no money, saves or threading in the diff" are carried forward;
neither was rerun, per GO. Only these notes change after terminal.

Preserved receipts: target/FS5c2-kept7-{slot,pre,pid,native,post,audit}.json,
FS5c2-kept7-sw.log, FS5c2-kept7-row-diffs.json,
FS5c2-kept7-exclusions.md and the two unmodified capture JSON files.
Raw log SHA256:
84c7dcd566794d8a9d1b9ed46db3c103d75cacc20c3b94a61bfd72a16d982e80.
Pre receipt SHA256:
8d122a7d167001f2906933959809c67d0049cf3815d164ac830822f511724f85.
Post receipt SHA256:
3249c1d760638d616a21a8ba4242a85a6eab84e1642cec0e3c22f0fc5fe63c04.
Capture A SHA256:
3567455a79227c372549e8fac0961b2d0b605e28883b3d456f5e898051708a79.
Capture B SHA256:
f458ad1499f01778826fcd72cdfd971ae6dd4c8963938b4295c0db0755fdbdee.
All other per-input/receipt hashes are in the sealed manifests/audit.

No registry removal/reclassification, new waiver, source mend, readiness
or publication follows this green current run. KHL1 remains provisional
ownership work. All old raw evidence/history and four vetoes are retained.
Notes-only provisional checkpoint; no owned job remains. Waiting for the
orchestrator's next explicit GO/decision.


## Root GO2413: KHL1 pending records disposition

Root independently accepted kept7's complete byte-preserving log scan,
frozen receipts, current green heap/release and two equal primary captures.
The final exclusion/paper/quiet audit and all73 per-call rows are recorded
above at notes-only checkpoint596d97c60. They are carried forward without
replaying a passed check, parser-derived model fixture or JVM.

The sole publication semantics change here moves the EXISTING KHL1 row
from queued to pending, as an unresolved historical ownership/capture
question for Jerus review. KHL1 was never adopted as a queued fix or named
exemption. Its ID and history anchor stay the same; no new row/ID, decided
UI/model mend, standalone historical replay or fresh work order is added.
Full6's four repeating hard Healthcare looks remain historical evidence;
kept7's current equal pairs have different source/model-state provenance
and do not prove the old leaf or its owner. Further bounded capture is
limited to a recurrence during the next independently required native
software measurement, separately authorized. No automatic replay follows.

Actual final audit remains73 share calls/1237 choices:1087 counted,
two Policy-currency content readings excluded (calls77/8 and77/9),148
acting choices excluded, zero refused/no-month choices. KRT1 readings0;
KBP1 blocks4/nonrecurring4/recurring0. Section92's actual Term10 sale gave
one paper and passed the hard premise; all-sales-refused fallback remains
UNEXERCISED. Bank55 Profit quiet diagnostic25/25(max0) accompanies the
current caused law; it is not a newly invented pass for the retired quiet
label. Separate target-only copy/detection probes are not evidence of
original full-run fault/negative-control coverage. No untested branch is
represented as covered by the green current full run.

Exactly FOUR current registry rows retain fix-ID FS5, byte-for-byte, with
no KHL1 retargeting, waiver, deletion or early reclassification:

```text
KeptCheck | Spending: Healthcare opened: the redraw and the next equal a page built new | FS5 | 2026-11-19 | KRT1's class on HS2's played city: the kept content is 1454x684 against a page built new 1446x700, the fresh one narrowed by a scroll bar; HS2 touches no Spending code (HS2-notes.md, The checks)
KeptCheck | the full kept check leaves a quarter of AllChecks.HEAP_MB free | FS5 | 2026-11-21 | Section 102 on the gate box's software renderer (Monocle, prism.order=sw, GB5): a canvas once drawn keeps its render target on the Java heap while it lives, where the PC's Direct3D keeps it in video memory. The pages hold the same objects on both (43,202 Labels each, int[] 226 MB on the box against about 40); 1,554-1,719 MiB against 1,536 (KEPT_HEAP_SHARE .75 of 2,048), the PC 1,379, the PC in software 1,594-1,780 (GB5-notes.md, ★5)
KeptCheck | most months make no node at all (12 of 25) | FS5 | 2026-11-21 | Section 55's Bank's Profit page (KBP1's, FS5's range), on the PC under load: MC2's merged tree gave 12 of 25 quiet months, and the same tree with main's Bank.java and BusinessDebtManager.java put back gave 9 of 25, a FAIL too (MC2-notes, REV-MC2 and the mends). KeptCheck's own note at playedCity gives 6 of 25 loaded and 17 quiet; main's kept gate runs give 14 to 25, and FS4's gate on the box passed it. Load, not MC2's code.
KeptCheck | the city's books hold a piece of paper (the fixture's premise for a line's page and Close it now: 0) | FS5 | 2026-11-21 | Section 92 at month 4327 in kept gate cbs-gate-MC2-eaV6vYID: piecesOnTheBooks found no city paper; its Term 10, Note 12 and Serial 10 real auction offers all returned Nothing issued. The unchanged issuance/bidder path permits no sale and never calls MC2's projectHeadroom, projectRoom or bandShare; the helper assumes demand instead of causing the paper needed by its line/Close-it-now page watches (CD9-K). The log does not distinguish zero bids from a tiny fill. FS5 d5f9cf8c6 adds the exact helper's booked-Term fallback and reread, retaining the hard premise; its full software run sold a Term and did not exercise this fallback. Traced in MC2-notes, Stopped gate and section 92 trace.
```

The publisher's normal fix-ID==FS5 removal remains the publisher's action.
Java machinery remains+819/-809 and registry-inclusive+819/-829, generated
maps/notes/ship excluded. FS5 stays UNTAGGED, with no model/playtrace change
or ensemble owed. Root GO474f settles the order: readiness now, then root's
normal --no-tag publication gate. No builder dry run or gate-run is owed.

Records-only validation: extracted the actual ship.sh sect/shiprows parser
functions without invoking ship.sh, plus tools/known-reds.sh FS5 on the
updated ship file. target/FS5c2-2413-record-parse.log exit0: allowed sections,
UTF-8 two-em-dash untagged heading, first-line provisional marker, one
four-cell pending KHL1 row, zero queued rows, valid existing notes/history
anchor, exactly four FS5 vetoes, no KHL1 exemption. git diff --check is
clean. This parses publication records only; it is not the root gate,
readiness check or a substitute for any original software coverage.

Only FS5-notes.md and FS5-ship.md change in GO2413. Source, dials, class/data
files, registry, observer scope and old raw evidence stay unchanged. No
compile/JVM/fast/pick/ensemble/dry/readiness check ran. Provisional records
checkpoint; no owned job. Root's subsequent readiness GO474f is recorded below.

## Root GO474f: readiness before the publisher's normal gate

Root accepted a58b8f1e2, the complete73-call/exclusion/paper/quiet audit,
kept7's ALL OK result and unchanged post-run seals for sourceaa570ab45.
KHL1 stays Pending for Jerus's historical ownership/capture review, with
no adopted fix/exemption. All four FS5 registry veto rows remain unchanged
for the publisher's normal fix-ID removal. All actual limits, raw evidence,
source/model/class/data bytes, hard laws and1536MiB bound are retained.

The final records-only provisional commit replaces the changelog's stale
waiting/marker narration with the factual current software pass at the
unchanged bound and historical Healthcare ownership pending review. It is
pushed and its marker verified on origin BEFORE a separate last commit
removes only that marker. This is the root's readiness GO, not publication.
The publisher's normal --no-tag gate follows FS5 ready. No prior gate is
claimed, no dry run or gate-run is owed, no model/playtrace change or ensemble
is owed, and the accepted pick24/237/review-not-needed are not replayed.
If the publisher sends FS5 back, restore and push the marker FIRST.

Records/parser validation uses the same actual publisher sect/shiprows and
normal known-reds parser as GO2413, with a new474f receipt; this validates
only records. Java machinery+819/-809 and registry-inclusive+819/-829 remain
unchanged, generated maps/notes/ship excluded. The final semantic source
paths are src/main/java/ham/citybuildersim/KeptCheck.java,
src/main/java/ham/citybuildersim/Observation.java and
src/main/java/ham/citybuildersim/ui/KeptBench.java, all byte-identical to
accepted sourceaa570ab45. No compile/JVM/fast/pick/probe/rejudge/ensemble/
dry/readiness execution, builder publication or main push is performed.
No owned job remains. Readiness is authorized now; root publication follows.


## Root GO658b: stopped BOX gate, static/saved evidence only

FS5 was NOT published. Before any other tracked edit, marker-only commit
1d480d9766f5aaf88595a8e489005a1aef4b9c70 restored the provisional marker,
pushed and verified on origin. Bus state is blocked, with gate base
da43868fb86115c2ce41ca669915609f79704170 and the three unchanged source
paths. Root alone moved FS5 to main Pending in queue-only
f2dba24270a0ba34bd08f3214285d9e98c1bb942; its sole diff from da438 is QUEUE.md.
No merge/recheck is needed for that records-only main change. Return to
Queued remains Jerus's decision. No passing gate-run comment is added.

### Sealed stopped result

Original copied run: C:/Users/Jerus/AppData/Local/Temp/cbs-gate-FS5-pcPZSnVc.
Root's immutable receipt records checks.complete, exit1, not cancelled,
inactive and all original controller/ship/guardian PIDs gone. This worker
did no remote access or process poll. Root verified ALL859 manifest hashes.
This audit matched that manifest digest and its859 entries, verified the
selected manifest entries, and made byte-preserving copies of30 relevant
files under target/fs5c2-stopped-gate-658b; original gate files were not edited.

| Artifact | SHA256 |
|---|---|
| checks.sha256 | e96ef0ffff2ec997c7c791a1b877cacf335b9f1a31890ded89fdbe63425a65e8 |
| checks/suite.txt | e26236563ec5ecc5f88607bde42dcf0ab60c615401d89de933d0936f3893d345 |
| checks/KeptCheck.log | 600dcfc884ff7f4f0813ed906741f9d782e88c965bdaa00abf86dff0f4f5227a |

Sealed branch2dfd503a4f67a217e6e8e9da979e10d0c0406018/base da438;
normal --no-tag gate, no publication. Saved selection says full/nightly
KeptCheck, PrizeCheck, ShortSellingCheck, version0.9.180. The recorded pick
was24/237, review not needed; actual suite ran225 harnesses in756s,
212 passed/13 failed,12 known-red harnesses and only KeptCheck NEW.
Whole check phase773s/exit4; KeptCheck311.2s/exit1; outer gate exit1.
The three unnamed lines are exit1, the ONE object-heap assertion FAIL,
and1 FAILED. Full UTF-8 KeptCheck log scan finds that sole failed assertion
at line5356 in section102; it continued through103 and its final summaries.
No absent fault is inferred from a last-N tail.

| Actual reading | Native kept7 | BOX gate |
|---|---:|---:|
| Live after release, MiB | 1521.3 | 1716.5 |
| Render-target payload, MiB | 18.5 | 4.6 |
| Objects, MiB | 1502.8 | 1711.9 |
| Unchanged hard bound, MiB | 1536 | 1536 |
| Margin / excess, MiB | 33.2 below | 175.9 above |
| Found canvases / target-bearing / heap-bearing | 45 / 10 / 10 | 45 / 10 / 10 |

The BOX live reading is195.2MiB higher, target reading13.9MiB lower, and
object reading209.1MiB higher. Arithmetic is not source/runtime/root
attribution. The native pass is NOT a BOX pass or waiver. Neither the old
full6's53.6MiB excess nor this BOX175.9MiB excess has a proven byte owner.

### Proved provenance and limits

Git src subtree c518908bbc58374df1b680830be6990f7067aa63 is identical at
aa570ab45, ready2dfd503a4 and this restored branch. Ready full tree is
79ecf07f914d92eadec074f95120508b87e5ed54; aa570's tree is
85593ac35c36946a93088db7dee646938402fae2 (notes/ship account for differences).
Only KeptCheck.java, Observation.java and ui/KeptBench.java differ under src
between gate base and branch; the sealed pair plus successful8s gate compile
prove the declared source/build provenance. The gate's uncommitted publisher
merge/tree was not separately archived as a committed tree digest. No gate
class-file digests exist in the859-file manifest, so binary class identity
to native kept7 is NOT proved by source equality or compile success.

Native kept7 has pre/post source/class/classpath/JDK/data hashes, actual
Oracle21.0.4+8 JDK/native modules and WinApplication/SWPipeline receipts.
BOX logs name cbs.display=monocle, glass.platform=Monocle,
monocle.platform=Headless, prism.order=sw, and actual measured SWPipeline;
the display report says all nine display harnesses started, none skipped.
No direct loaded Glass-class receipt, JDK patch/binary/jar hashes, selected
GC/collection-completion telemetry or matching data-byte hashes were sealed
in this gate. Gatebox docs name its installed Temurin, not this run's exact
binary. Linux /home/jerus/cbs/main/data/{earth-2.bin,world-1.bin} paths/version
names alone do not prove equality to the hashed C:/ native data files.

AllChecks.command adds -Xmx2048m/-Xms64m, observation harness forwarding,
timing forwarding and UTF-8 output options. Native direct kept7 used
-Xmx2048m/-Dprism.order=sw with no explicit -Xms and only its accepted
capture properties. Gate runs under the multi-process suite, native alone.
These are configuration/workload differences, not a demonstrated cause.
BOX observer writes0; native observer wrote2 equal primary pairs. No BOX
raw signature/viewport/texture physical-dimension census was captured.
Same source WIDTH1456/RAIL_HEIGHT900 constrains intended geometry, not
identical actual layouts. Native's captured restored body1456x698,
content1454x682/viewport1454x696 cannot be assigned to the BOX graph.

### Actual workload, premises and exclusions

Both watched cities start month605/population4903. Government entry is
native2369 versus BOX2370. Build44 differs: native no-land10000 Houses
versus BOX100 Houses; native1000 Metro Line and532 Studio Apartments
funding cases versus BOX10000/1547 Crude Pipeline cases. Numeric per-call
counts match for all73 calls, but five call labels/caused workloads differ.
No complete model-state identity is proved by shared month/population.

Government release: BOX1904 distinct fresh nodes/0alive, versus native1832/0;
signature/counters/kept-nodes/roots/abort restored all true, all four hard
assertions pass on both. This proves that specific exercised lifetime
regression, not release of every prior comparison or an attribution of
the entire residual. BOX's three Healthcare-open assertions all pass;
no held content look or accepted KRT1 reading occurs. No original full6
leaf, historical UI-versus-bench owner or new KHL1 exemption follows.

BOX Fund63 settles1 actual holding/B:3181 lasting114 months versus3 watched,
and the held-rule assertion reports36->32 lots; native is1 settled/B:3102
lasting116,17->18 lots. These printed totals differ and are retained as-is.
BOX section92 finds2 papers already held; it exercises neither a fresh
Term sale nor refused-sale fallback there. Native needed a real Term10
sale and found1 paper; its fallback was also unexercised. BOX Bank Profit
quiet24/25(max1) versus native25/25(max0); retired quiet label is not renamed
passing. Three BOX grid holds report0closed/unheld0, as native does.

BOX73 calls total1087 counted/1237 choices, two content readings excluded,
148 acting choices excluded,0 refusals. All numeric rows match native,
not all page labels. KRT1 readings0/held-content-looks0. BOX KBP1 traces2
blocks, both Policy Money > The currency22/201, repeated redraw0, retained
89.1% and excluded only under the standing content law (77 calls8 and9,
each10/11). BOX has0 recurring blocks. Native had two additional Build
Overview blocks within its25-month watch; those were not excluded. The
complete BOX per-call table follows, with its actual funding labels.

### Static lifetime/walker/measurement owners

KeptCheck.kp2cMends (section102, source9396 onward) holds Game/KeptBench on
the driver stack. It runs retiredPicture/retiredChrome, bench.release,
a non-FX-to-FX FIFO barrier, System.gc(), hard weak-release assertions,
then MemoryMXBean heap-used BEFORE canvasTargets/renderer readback.
There is no sealed GC-completion telemetry; no runtime conclusion about
collection policy follows. Release snapshots the eight existing scene roots
into the one existing1x1 image to drain dirty lists; it does not discard
Game, UI, screen pools or those scenes' graphs.

KeptBench fields64ff retain UI, scenes, kept-node lists/pools and histories.
UserInterface owns each screen and Game. Kept.Pool/Scope holds made/inner/
parked keyed scopes (parked bound48). These are existing root candidates,
not proven excessive owners. Government comparison body/targets restoration
and finally clearing are at1567-1620; their sampled regression passes.
The Healthcare observer active context is null outside its exact chain and
BOX never enabled writes; no observer root or blanket exoneration is inferred.

canvasTargets1692ff identity-visits objects from UI plus eight roots,
Node.parent/children/graphics/clips/properties/ScrollPane.content,
collections/maps/Object[] and fields/statics of ham.citybuildersim.ui classes.
It does not reflect arbitrary JavaFX/Prism internal static roots or all Game
fields. Reader follows found Canvas.peer fields and NGCanvas$ wrapper fields
to Texture, counting SW4*physicalWidth*physicalHeight. The visited walk
deduplicates Node/object identity; no separately proved shared backing-array
alias dedup is claimed. Both45/10 counts do not prove matching graph or
array identities across processes. Cache/effect/readback copies and unknown
arrays remain objects under the hard bound, never presumed subtractable.

Saved official JavaFX21 source (archiveSHA599fa2b1c7dd343ab48bebb7b3da7f490d2420dc82f448e517345e182b94cc76)
shows NGCanvas.highestPixelScale = ceil(max recommended screen output scale),
logical dimensions scaled before allocation, and SW physical dimension
fields. This is a possible mechanism consistent with the roughly4x render
payload ratio, NOT measured scale/dimension evidence from either full run,
and it assigns none of the209.1MiB object delta or remaining175.9MiB.

### ONE proposed next evidence scope, not authorized implementation/execution

No hprof/JFR/model-save checkpoint exists in the sealed copied gate; the
original JVM is gone. Logs cannot reconstruct its retained heap or complete
model/UI/pool state. A cheap generic fixture cannot honestly recreate it.
Recommend ONE opt-in failure-time heap evidence bundle on the NEXT
independently required, separately authorized BOX KeptCheck measurement:
after printing and recording the unchanged section102 hard verdict,
capture a uniquely named HPROF with dumpHeap(path,false) plus a small
allowlisted receipt of the existing used/target readings and actual
source/class/data/JDK/GC/host identities. No extra model month, signature,
draw/layout/snapshot/settle, pool clearing, repeated comparison, altered
bound or target exclusion. Continue normal103/exit, retaining FAIL even
if capture fails. The dump/receipt is ONE evidence bundle, not another sweep.

Local JDK21 HotSpotDiagnosticMXBean docs verify the API: live=true means
only reachable objects. false permits all objects; it is not a promise
of zero VM safepoint/collection side effects. Capture is a subsequent
snapshot, not retrospective proof of exact bytes at the earlier sample.
Offline root reachability/dominators can distinguish Game/UI/scene/pool/
Prism owners and unreachable payloads; any proposed causal release/byte
assignment would still need separate review/proof. No extra GC is requested
by this proposal; internal effects and observer footprint remain limits.
Budget disk for about the2GiB heap plus dump overhead; time/IO cost is
unmeasured and no20s/45s guarantee is made. Root must separately approve
implementation, process one-in/one-out, host/disk/wall budget and any run.
No unchanged full replay, current execution GO, source mend or waiver is
requested here. Await root review of this single evidence proposal.

| Section/call | Counted | Choices | Content readings excluded | Acting excluded | Refused |
|---|---:|---:|---:|---:|---:|
| 23: what reshapes Infrastructure / 1 | 14 | 14 | 0 | 0 | 0 |
| 27: what reshapes Services / 1 | 54 | 54 | 0 | 0 | 0 |
| 31: what reshapes the land office / 1 | 20 | 20 | 0 | 0 | 0 |
| 33: what reshapes the funding page / 1 | 1 | 1 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (a bond) / 2 | 1 | 1 | 0 | 0 | 0 |
| 33: what reshapes the fell-short page (the vault's dollars) / 3 | 1 | 1 | 0 | 0 | 0 |
| 38: what reshapes the Build screen / 1 | 40 | 42 | 0 | 2 | 0 |
| 42: what reshapes the construction page / 1 | 10 | 10 | 0 | 0 | 0 |
| 44: what reshapes the no-deposit page (1 x Iron Mine) / 1 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-coast page (1 x Desalination Plant) / 2 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-river page (1 x Hydro Station) / 3 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-bridge page (1 x Highway Bridge) / 4 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-licence page (1 x Engineering Services Office) / 5 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the no-land page (100 x House) / 6 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build funding page (10000 x Crude Pipeline) / 7 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build fell-short page (2 x Gravel Road built, 10000 x Crude Pipeline left) / 8 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build fell-short page (none built, 10000 x Crude Pipeline left) / 9 | 1 | 1 | 0 | 0 | 0 |
| 44: what reshapes the build funding page (1547 x Crude Pipeline) / 10 | 1 | 1 | 0 | 0 | 0 |
| 49: what reshapes the Sectors list / 1 | 2 | 2 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 2 | 25 | 25 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 3 | 45 | 45 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 4 | 5 | 5 | 0 | 0 | 0 |
| 49: what reshapes the Sectors tab / 5 | 18 | 18 | 0 | 0 | 0 |
| 96: what reshapes Agriculture's Investors page / 1 | 3 | 3 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 1 | 10 | 10 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 2 | 22 | 22 | 0 | 0 | 0 |
| 57: what reshapes the Bank Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 1 | 11 | 11 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 2 | 9 | 9 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 3 | 33 | 33 | 0 | 0 | 0 |
| 60: what reshapes the Finances hub / 4 | 36 | 36 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 5 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the foreign default menu / 6 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 7 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 8 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 9 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 10 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 11 | 1 | 1 | 0 | 0 | 0 |
| 60: what reshapes the debt result menu / 12 | 1 | 1 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 1 | 14 | 17 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 2 | 6 | 13 | 0 | 7 | 0 |
| 63: what reshapes the city's fund / 3 | 8 | 8 | 0 | 0 | 0 |
| 63: what reshapes the city's fund / 4 | 0 | 3 | 0 | 3 | 0 |
| 63: what reshapes the city's fund / 5 | 18 | 28 | 0 | 10 | 0 |
| 63: what reshapes the city's fund / 6 | 4 | 5 | 0 | 1 | 0 |
| 71: what reshapes the Government Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 2 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 3 | 11 | 11 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 4 | 86 | 86 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 5 | 19 | 19 | 0 | 0 | 0 |
| 71: what reshapes the Government Overview / 6 | 0 | 86 | 0 | 86 | 0 |
| 71: what reshapes the Government Overview / 7 | 4 | 10 | 0 | 6 | 0 |
| 74: what reshapes the Trade Overview / 1 | 7 | 7 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 2 | 21 | 26 | 0 | 5 | 0 |
| 74: what reshapes the Trade Overview / 3 | 10 | 10 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 4 | 0 | 25 | 0 | 25 | 0 |
| 74: what reshapes the Trade Overview / 5 | 27 | 27 | 0 | 0 | 0 |
| 74: what reshapes the Trade Overview / 6 | 9 | 9 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 1 | 24 | 24 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 2 | 39 | 39 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 3 | 44 | 44 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 4 | 55 | 55 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 5 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 6 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 7 | 11 | 11 | 0 | 0 | 0 |
| 77: what reshapes the Policy hub / 8 | 10 | 11 | 1 | 0 | 0 |
| 77: what reshapes the Policy hub / 9 | 10 | 11 | 1 | 0 | 0 |
| 84: what reshapes Household money / 1 | 72 | 72 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 1 | 44 | 44 | 0 | 0 | 0 |
| 87: what reshapes the Rankings tab, to the pixel / 2 | 15 | 15 | 0 | 0 | 0 |
| 95: what reshapes Policy, Research's Found page / 1 | 4 | 4 | 0 | 0 | 0 |
| 99: what reshapes Policy, Research's Overview / 1 | 5 | 5 | 0 | 0 | 0 |
| 100: what reshapes Policy, Research's Funding / 1 | 9 | 9 | 0 | 0 | 0 |
| Total | 1087 | 1237 | 2 | 148 | 0 |

The full audit and30 copied artifact hashes are in
target/fs5c2-stopped-gate-658b/audit.json; library-source/label comparison
is static-owner-context.json. No original gate folder/manifest/suite was
modified; selected original hashes still match the immutable copied set.
Java machinery+819/-809 and registry-inclusive+819/-829 remain unchanged,
generated maps/notes/ship excluded. The accepted pick/review are not rerun.

Only notes and provisional changelog wording change after marker restoration:
both the native pass and hard BOX failure are explicit. No registry removal,
retarget, red waiver, passing gate-run, new row/ID, code/runtime/dial/model
mend, readiness, merge, publication or main push. FS5 blocked/Pending;
no owned process. Waiting for separate root review/GO of the ONE payload.


## Root GO71c5: ignored post-hard-verdict HPROF prototype preparation

STATIC TARGET-ONLY, UNCOMPILED and NOT RUN. Root accepted the stopped gate
records4e87837e1 and saved audit9cd81d84a5ab9c851a8ae6f24b2b670cbb89e5d7e21163f11fb119f046289f59.
FS5 stays blocked/Pending; marker/four vetoes/KHL1 shipPending and all prior
raw/source/class/data/registry proofs remain untouched. No live original
heap/profile/checkpoint exists. No source/harness/model mend or invented
line removal; process counts remain Java819/809 and registry819/829.

Actual ignored prototype directory: target/fs5c2-71c5-heap-capture-v2.
It contains the exact aa570 base KeptCheck copy, modified target-only copy,
separate FS5PostHeapCapture helper, minimal14-line insertion diff, inverse
restored base, primary local JDK source/release copies, provenance-pins.json,
static-overlay-audit.json and PREPARATION.md. Base src tree is
c518908bbc58374df1b680830be6990f7067aa63; original source blob is
f3c5c600c1e4239817f9b920a5843ad195fe90ca. Inverse overlay is byte AND token
exact; the prefix through the unchanged original printed heap assertion is
byte exact. No compiler/typecheck/driver/JVM success is claimed.

Base KeptCheck SHA256:
b5b5014db575c590e5d170baa25056c4606ff7f35ba43d304e8d38e6d5647aa3.
Target overlay KeptCheck SHA256:
a1fd6e15334e6a4d842f3dcf0be9c4106cd1bdaef4e49fd852ce4258677af5cb.
Target helper SHA256:
8d901ec8c78d10cbd1f2b7ae016aa2a2a0142bde64f16383700ab655c680f22e.
Minimal overlay patch SHA256:
f4df7d396da783095200bd60b29c344916fce7f46ae4bb445f45680b193aed10.

The hook is strictly AFTER the existing102 reading AND unchanged evaluated/
printed hard assertion. It repeats only pure predicate arithmetic from
cached readings/constants, then requires explicit opt-in. Only there does
Class.forName load the helper. Primitive/String arguments only; no earlier
payload/Node/Game/signature/context root, extra model month/UI/draw/layout/
settle/snapshot/GC/thread or altered threshold/tally/exit. Modified class
constants/JIT/VM footprint are not asserted zero; this is new provenance.
All metadata allocations are after verdict. Opt-in absent or hard pass
does not capture. Reflection/helper/dump errors leave original FAIL and
normal103/exit intact, subject to fatal VM/OS limits. Partial files remain.

Helper requests native HotSpotDiagnosticMXBean.dumpHeap(fixedLeaf,false).
Actual local Oracle21.0.4+8 primary API/implementation are copied and hashed:
live=true means reachable-only; implementation checks suffix/security and
calls native dumpHeap0. false includes unreachable objects; safepoints,
VM collection/initialization/native pauses and helper debug objects are
not promised absent. Offline roots/dominators, aliases, collector state,
unreachable payload and object-layout/dump overhead must be distinguished.
No retrospective byte/root assignment to pcPZSnVc follows a snapshot.

Destination is ONE pre-existing private0700 launcher-owned directory
/home/jerus/cbs/evidence/FS5-heap-71c5-<32hex nonce>, outside TempDirs;
strict absolute normalized/real paths, fixed unused .hprof and before/after
JSON leaves, matching bounded ownership/source pin receipt, no symlink/
overwrite/arbitrary-save path or destructive cleanup. Private launcher lock
and umask077 are required; helper alone does not prove live lock ownership
or eliminate trusted-same-user path races. Proposed free space >=12GiB at
both output/copy ends; ~2GiB heap plus substantial HPROF/copy overhead and
latency are unknown. No45s promise/deadline is finalized or authorized.

PREPARATION.md gives an isolated -sourcepath EMPTY/-implicit:none/-proc:none
compile plan for ONLY overlay KeptCheck + helper, with frozen compiled-base
capsule and dependency classpath. No model compilation/fallback or old gate
clone mutation. Native class/data/JDK context is not substituted for BOX
pins; future source/overlay/base classes/deps/data/JDK and exact-copy hashes
must be sealed before/after. Missing capsule/pins means STOP, not rebuild.

ONE future proposal only: separately reviewed/authorized purpose-specific
BOX KeptCheck replay of the accepted workload solely to collect missing
heap evidence on NEW provenance, not yet an independently required
production measurement, not a225suite/gate replay or historical Healthcare
replay. Foreground supervisor owns only its PID/group/lock, with root-set
total/dump deadlines, real-terminal confirmation before lock release/copy
completion, no retry/extension, partial preservation and verified homecopy.
Hard pass means NOT_TRIGGERED/no retry/fishing/waiver; hard failure remains
FAIL even with capture. Any forced stop is incomplete/nonzero; no success
is inferred. Implementation/compile/execution/disk/time still need separate GO.

The final review artifact is the v2 directory above:14 inserted lines place
the opt-in property read inside the same post-verdict Throwable guard.
The initial13-line ignored variant is retained at
target/fs5c2-71c5-heap-capture; it is superseded for review, not executed.
Final v2 artifact-manifest.json SHA256:
fc825bfa7b455685076e6d51ad4c4223da0bd53fe9a4c33f48fc52341670ff10.

Only these notes change in the versioned tree. No compile/JVM/BOXaccess/
poll/measurement/dump/run/fast/pick/rejudge/ensemble/dry/readiness/publication
or registry edit, no other process stopped. Notes-only provisional
checkpoint; no owned job. Await root review/separate GO.


## Root GO89ae: ONE isolated BOX compile, no model/runtime GO

Root accepted ignored v2 sources/pins for this compile stage only. FS5
remains blocked/Pending with provisional marker, four unchanged vetoes and
KHL1Pending. Versioned KeptCheck/Observation/KeptBench/source tree remains
aa570/c518908bbc58374df1b680830be6990f7067aa63. No model/harness mend or
shipped machinery change. Different overlay bytecode/JIT/VM and NEW
diagnostic provenance remain explicit; no stopped-heap byte attribution.

ONE compile job label: FS5c2-heap-compile-89ae-0a4e20cf7164642c.
New BOX run /home/jerus/cbs/runs/FS5c2-heap-compile-89ae-0a4e20cf7164642c;
verified homecopy at target/FS5c2-heap-compile-89ae-0a4e20cf7164642c.
No original gate clone/run was touched. gate.env was sourced before java/
javac resolution; selected BOTH from /home/jerus/cbs/jdk-21.0.13+9/bin,
actual release Eclipse Adoptium/Temurin-21.0.13+9. Exact gate.env file bytes
were not sealed; selected paths/release and five JDK file hashes were.
No java/version-probe/local javac/Maven fallback occurred; local dispatch
allows SSH only and explicitly refuses local Java/javac/Maven fallback.

The accepted2438-file native-built base class/resource capsule was verified
before packing, with exact relative paths and SHA256, then all2438 files
verified on BOX after copy and after compile, and original local capsule
verified again after homecopy. No replacement base/model/source/Maven
compile fallback. Source/src tree and reviewed overlay/helper/patch pins
match. Input archive15902650 bytes, SHA256:
c3419ae296b93f68cc617d81fc3232a4d3581dd7da7ce17fb284069af327628a.

Remote preflight checked gate/nightly priority once and took box.lock
exclusive/nonblocking; no competing owner polling/wait. One javac phase
only, exit0: --release21 --add-modules jdk.management -proc:none
-implicit:none, explicit EMPTY sourcepath/NEW empty output, encodingUTF-8,
explicit reviewed overlay KeptCheck.java and FS5PostHeapCapture.java ONLY.
Base classes plus ten current Linux JavaFX21/Monocle21.0.2/Gson2.10.1 jars
formed the pinned classpath. No processor, model compilation or helper/path
smoke test. No Java mainSections/model month/display/GC/dump run occurred;
the only JVM was the authorized javac compiler JVM.

Linux dependencies, five selected JDK files/release and two actual data
files match byte-for-byte before/after compile. Current Linux data SHA256:
earth c897b2421d0b742899c034eeba84a4668f6534cae85e9c5500fa9a26172ae33b;
world fbd11a6a3b880ffeeae4e5c07e7a9d950b3d28f17f7c77656aea7a1071a6fcc1.
These match kept7's hashed data but do NOT recover historical pcPZSnVc
input bytes or class/JDK identity. The current compiled base is explicitly
the accepted native-built capsule, not recovered old BOX binaries.

Remote javac PID/group2478403 was waited/reaped and group gone; remote
owner2478399 actually exited with SSH0, own box lock released, all29 copied
artifacts verified. Total elapsed7.088s includes local capsule verification/
packing, preflight/upload/compile/homecopy/postverification; remote3.312s.
This is within the300s total/no extension, not a model performance result.
Compiler log empty, no source change/retry. Homecopy archive SHA256:
7f198b2c18608a6e1d1cb34d75b1c3ff3ba6d463d38c68d338313d84a2fa2893.
No owned process/lock remains. Only after real terminal/copy verification
were these notes edited. Exactly19 allowed output classes follow; complete
manifests/argv/JDK/dependency/data hashes and before/after receipts are under
the homecopy result directory and compile-stage-audit.json.

| Emitted class under ham/citybuildersim | SHA256 |
|---|---|
| FS5PostHeapCapture.class | be366a3cf2e7e23ab4af610cfd5b236d16c44c21abb4b2b9995d19304e66fee8 |
| KeptCheck$1.class | a20fce6b28ce0e346ff5fc276ad7f8ad2620e0f8310bb7c282fa4cdbd331b05f |
| KeptCheck$1Quiet.class | 065072734b6a07cc29dba9053c0a7f6eae95b2abe47b835f1d4cbd7b72c00c8d |
| KeptCheck$1Where.class | e5510b95fb78a9e3bd2bddc4eea57029840bc983fc88a5d3bf0faa0482ccd95a |
| KeptCheck$2.class | 9a748394558c4e36e74c62026cf27c94346f3445178f551a022089fabda15472 |
| KeptCheck$2Where.class | b489ec31b950c0abd258087a8c802ef71e13a6da006c4c8db158d1c1e1fbf5e9 |
| KeptCheck$AtOnce.class | ec7861afb0ffc474521f39a50b1efe7c4b03fe8f450da431619ac719f230f504 |
| KeptCheck$Choice.class | ee625b1503bb1e5d98e0344f98ca72cf5def96107af6c73d0325c8692b0e26a8 |
| KeptCheck$Greyed.class | 8afa87dcf94acd33e00ec6cab9313c32955cbc0a477eee18079f90a48b7fb9dd |
| KeptCheck$Hand.class | a02917082c32f454eb9a0ba9838f6dbb745fb226d66bd6ce28305c2203bfec95 |
| KeptCheck$HealthcareCapture.class | 10ad956edcd1a1f428594f658e482efdba51679c94a14cdc1c7c2ae8330ffaaf |
| KeptCheck$Held.class | f289edb6d9c44bbe47494a2d932a324b5c7038ef5112d96b22d7ab0baca27ace |
| KeptCheck$MonthHand.class | 434a618c2e60bc1ef8100a9559aaf560768a38ca326de26e7aeeacb0e8aa7182 |
| KeptCheck$RailScreen.class | 45bd7471d2ffc124ad06c836f58e44f6357fb76382e83866f34ef15398042081 |
| KeptCheck$Redraws.class | 79c1ae971fdfd9bd9107dbad3bee3e32f2102828796c1e735f94baa80799f1f9 |
| KeptCheck$SectorKinds.class | 75ff9d796028810cdfb007054e66a0a795325b0c53cfc7827fc77ac882d8b5b8 |
| KeptCheck$Spot.class | a2ea64e8e89ecdbfbf91059fd29d9cfe72e1d52b763e51357f9045898a121648 |
| KeptCheck$Verify.class | 0e736c12e1ac0e02c85bbe202a2e02336672f8790585fe8edc1e5cb1c0ffc0ac |
| KeptCheck.class | bd5205cb494098c7bfc0446b55d5c59d7711a862127f32b9b7547e44fc1666ea |

Future preparation ONLY is under future-runtime-PROPOSAL/: runtime-plan.json,
future-supervisor.DRAFT.py, launch.properties.template, JDK/deps/data hash
manifests, proposal-manifest.json and REVIEW.md. No runtime source was
executed or syntax/typechecked, no capture parent or dump was created and
runtime disk checks have NOT run. Draft supervisor has no executable entry
point. Proposed nonce/absolute parent, fixed leaves,0700/umask077 ownership,
reviewed argv/pins, before/after/terminal receipt and both-end>=12GiB
requirements are explicit. Proposed limits:1800s total INCLUDING homecopy,
180s after dump-begin, copy up to600s WITHIN total. None is accepted or
finalized without separate root runtime/disk/deadline review/GO.

Runtime plan SHA256:
a7cf7631aa0408058dd2a96f54010c70aa8acce52037ecb09259cf382265ee68.
The local foreground runtime/copy driver remains a REVIEW ITEM: it must
seal relocated plan/template/pins, source gate.env, enforce both-end space
and exact selected JDK, stream multi-GiB homecopy instead of in-memory tar,
verify all output hashes/sizes, and preserve originals/partials on failure.
Remote draft alone does not implement or prove cross-machine copy. Its
template path is local now and must be relocated/pinned before runtime.
No end-to-end lifecycle test/ready-to-launch claim is made.

Dump/copy cost is unknown:~2GiB heap plus potentially substantial HPROF
object-ID/record overhead. This15.9MB/7.09s compile transfer does not predict
multi-GiB dump/copy speed and is no45s promise. Foreground ownership must
continue through actual owned JVM/group/supervisor exit and verified copy,
with own-only PID/start-tick recovery on SSH loss, terminal lock release,
partial preservation, no retries/extension/competing-owner stop. A deadline
stop is INCOMPLETE nonzero. Normal PASS=>NOT_TRIGGERED/no retry/fishing/
waiver/old-fault attribution; FAIL remains FAIL even if a dump succeeds.

Reviewed ignored source pins remain overlaya1fd6e15334e6a4d842f3dcf0be9c4106cd1bdaef4e49fd852ce4258677af5cb,
helper8d901ec8c78d10cbd1f2b7ae016aa2a2a0142bde64f16383700ab655c680f22e,
manifestfc825bfa7b455685076e6d51ad4c4223da0bd53fe9a4c33f48fc52341670ff10.
Versioned process counts819/809 and registry819/829 remain unchanged.
Only notes change; source/classes/registry/ship/old evidence remain intact.
No model/helper JVM, dump, version probe, wrapper/fast/pick/rejudge/ensemble/
dry/gate/readiness/waiver/publication run. Notes-only provisional checkpoint;
no owned job. Await separate root runtime/disk/deadline review/GO.
