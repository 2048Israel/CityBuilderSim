# FS5 notes: KeptCheck II causes what its pages read

Latest full-run result: frozen source 0446cec5e on MC2 main .179 completed
its one authorized final software run with two hard failures: recurring
Healthcare layout and objects heap 1589.6 MiB against 1536. The full-model
Government release regression passes (1832 fresh nodes, zero survivors).
The subsequent parent-edge-only accounting mend at 4e65bd86b passed --fast
and one bounded production-helper proof. No subsequent full measurement
is authorized; the 53.6 MiB residual remains unattributed. No readiness
is claimed, and bounds and vetoes remain unchanged.

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

Provisional queued trace/fix, not a named-pass exemption. `tools/batch-id.sh
KHL1` passed on .179. The defect is the repeating kept/fresh law violation
above, also present on HS2's earlier path and FS5's earlier software run.
The next work must identify whether the retaining/layout owner is actual UI
or the comparison machinery before moving a fix outside FS5. Relevant
methods are GovernmentScreen.frameOver, UserInterface.scrolled and
KeptBench.settle/showFresh/restoreKeptPage. Trace the actual scroller viewport,
bar visibility, bound page width and layout invalidation across the Healthcare
open; cause the threshold in a small scratch fixture and compare both sides.
Preserve the original style, geometry and equality assertions. Do not strip
preferred width from signatures, waive the repeated difference as load, or
use a redraw to conceal it. If the cause is FS5's fixture/measurement, mend it
here; a demonstrated actual UI defect gets its own fix. FS5 keeps the hard
veto until that causal disposition is complete. The 53.6 MiB heap miss remains
a separate, unclassified responsibility and is not passed by KHL1 or FS1.

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
