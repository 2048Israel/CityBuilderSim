# BS1 — Bond settles to a fixed point, the bid tick and the buyback

**Approved for shipping by Jerus on 2026-11-12, about 15:50**, relayed in
lanekeeper instruction `20261112-155225-270-lanekeeper-8410`. Lanekeeper
will gate BS1 fresh after DM2.

Built from main `fc3b6be5` on `batch/BS1`. The queue's BS1 row and its linked
BA1 and BD1 findings are the brief. WC1 assigns whole batches to Codex.

## Implementation and calls

- ★ The corporate issue search uses a relative size tolerance of `1e-14`
  and allows 100 iterations. Repeated refinancing amplifies the former
  `1e-9` shortfall beyond the price-level twins' bound. BondCheck measures
  the partial issue's residual against the model's own tolerance.
- ★ Every corporate bond order rounds its price per unit of face to the
  nearest `1e-12`, including orders submitted by the harness entry point.
  Equal computed bids retain their arrival order across nominal units.
- ★ A whole-piece buyback cancels the unpaid sale at its original proceeds.
  The ladder identifies that cancellation, the bank offers
  `bankPrincipalPaid()`, and the treasury's outstanding settlement falls by
  the cancelled proceeds. This reverses the original advance and lets each
  paid holder sell its own face at its own price. Partial unsettled buybacks
  retain the existing refusal. The Close it now page labels the cancellation.
- The existing saved settlement counters carry the cancellation through a
  save and through a denomination change. The action adds no persisted state.
- HoldersCheck 13c now asserts zero paid bank face on a fresh cancelled sale,
  as BY1 requires. Its book-cover fixture and equity-split assertions remain.
- New regressions exercise a partial issue's size residual, competing bids
  that differ by one floating-point step, fresh long/medium/bill cancellations,
  and cancellation of a reopened line with paid bank and household holdings.

## Checks during implementation

- `--fast` passed, compile 49 seconds, zero firm Stale findings.
- The first focused compile found an exhaustive switch needing the new
  cancellation counterparty. The switch was completed.
- The focused run passed BuybackCheck, HoldersCheck, PriceLevelCheck and
  DenominationCheck. Every BS1-owned red in the latter two is green.
- BondCheck's added bid-priority fixture reached settlement with an unattached
  bank seller and threw. It now gives the face to the world seller, whose
  settlement is self-contained. Both equal bids still come from funded cells.
- BuybackCheck proves zero bank cash received for unpaid face, cancellation
  of the original proceeds, closing money audits, and the bank's equity split
  after two months for fresh issues and a reopening.
- CreditCheck's same-month curve fixture expected the sale's proceeds plus
  issuance cost. BY1 cancels that sale at the original proceeds. Its fresh
  30-year sale fixture now asserts that price against `MoneyAudit.CENT`.

## Final checks and remaining red lines

`./batch-check.sh --pick` selected **ALL 201**, compiled in 49 seconds and
completed the suite in 2,772 seconds. Exit **4**, 193 passed and 8 failed.
All BS1-owned PriceLevelCheck and DenominationCheck entries are green.
BondCheck, BuybackCheck and HoldersCheck pass, and the default LongPlaytest
reports no findings. Stale reports zero firm findings. There were 37 soft
timing assertions. The retained local evidence is
`target/checks/BS1-pick.txt`, `BS1-full-suite.txt` and `BS1-full-reds.txt`.

The six covered red harnesses are MonetaryCheck, CentralBankCheck,
HealthCheck, PlanCheck, KeptCheck and DeathRecordCheck. Their assertion lines,
with KeptCheck's repeated redraw label shown once, are

```text
MonetaryCheck | ...and a month's delay in the hand moves them less than the allowance FAIL
CentralBankCheck | ...and in at least STRICT_SHARE of them, 9 of 12 (7 hold) FAIL
HealthCheck | ...its old first, whom a fee on senior care turns away FAIL
PlanCheck | at every 30th month every district's plan keeps every check FAIL
PlanCheck | ...the avenues were laid in the played city (328 plots in 11 open cells over the samples), every check holding with them FAIL
KeptCheck | a month's redraw after each choice hands back nine in ten of the page's nodes, or more FAIL
KeptCheck | most months make no node at all (12 of 25) FAIL
KeptCheck | no month makes more than a tenth of the nodes the first draw made (the most made in one: 39 of 377; 16 of 25 months made none) FAIL
KeptCheck | the kept header's signature equals a header built new, at pace and short, at every rung (18 differ) FAIL
DeathRecordCheck | the orphan section does not fill behind it, in any month of the two years FAIL
```

The full run's two new assertion lines were

```text
CreditCheck | a bond issued at 30 years is bought back the same month for what it raised, bar the issuance cost FAIL
FoodProcessingCheck | ...and the difference is most of what a Meat Works is worth FAIL 1.8x
```

CreditCheck's old expected price was 4,916.26 plus the issuance cost. The
actual cancellation returned 4,916.26, as BY1 requires. After correcting the
assertion, `./batch-check.sh CreditCheck FoodProcessingCheck` compiled in
44 seconds, CreditCheck passed, and FoodProcessingCheck retained its 1.8x
red, exit **4**. Evidence is `target/checks/BS1-credit-food.txt`. The full
suite above precedes this assertion-only correction.

FoodProcessingCheck passes on retained main classes, exit 0, with 969.49
homegrown profit against 379.79 imported, 2.6x. Its first-plant fixture reaches
month 120, 7,942 people and unskilled wage 124.58. BS1 reaches month 152,
13,252 people and wage 124.09, and reports 1,255.01 against 679.17, 1.8x.
The fixture waits for the planner's first build and then tests its meat-price
claim in that lived-in city. Both profit cases cause their meat price. The
new path exposes the assertion that homegrown profit exceeds twice imported
profit. Jerus accepted this line by name as path noise, with fixture repair
queued as FPK1. The main comparison is `target/checks/BS1-food-on-main.txt`.

## Verification

- Rendered Close it now through the real FinancesScreen and KeptBench on
  scratch cities. Fresh cancellation and a reopened line both display the
  cancelled sale, its proceeds, the paid holders and the total without
  overlap. Images are `target/checks/BS1-fresh.png` and
  `target/checks/BS1-reopened.png`. The helper and images remain under target.
- The default playtest path moves. The prior main evidence is the retained
  GR2 launcher run `C:/Users/Jerus/AppData/Local/Temp/cbs-gate-GR2-20261112-01`,
  main `420c65dcebd0a0371d4d785145ef904af87a7883`. Its model equals BS1's
  base `fc3b6be5`. The intervening Java changes are AllChecks, KnownReds and
  KnownRedsCheck. Its first stock write-off is month 192, BS1's month 260.
  The required ensemble ran once on final model `41551904`, label `BS1`,
  against stored `docs/ensemble/0.9.93.md`.
- Ensemble completed all 80 runs in 20.7 minutes, exit 0. The committed
  baseline is `docs/ensemble/BS1.md`, scored **23/26**, alongside stored
  0.9.93's **23/26**. Every policy reports zero money-audit failures.
  A1 misses the breadth requirement, 10/16 seeds in band with median 2.71%
  (stored baseline 13/16 and 2.75%). R3 passes, having failed in the stored
  baseline. A2 and A3 fail in both. The stored baseline predates main's LM1
  changes to Founding, CityLand and AutoBuilder, so the comparison covers
  those changes as well as BS1. The baseline is used as recorded under EN1.

## Why the path moved

Temporary BondMarket overrides isolate the old arithmetic, the size correction
alone and the tick alone. The old-arithmetic default report equals the retained
main report after removing its run-time line. Both corrections independently
move bank stance at month 142 from REBUILDING to PAYING. With the size correction
alone the monthly CSV changes population and cash at month 143. With the tick
alone the monthly CSV first prints a fund-value difference at month 253, and
the corporate-bond CSV prints a loan difference at month 260. The buyback's
entry point has no caller in the default simulation or playtest.

The corrections affect issued face and trade prices and allocation. Their
liquidity and capital effects provide a route through credit and investment
to the inflation path. The changed calculations set numerical convergence
and trading priority. The default-run isolations establish both sources of path
movement. A1's 80-run attribution between the two remains unresolved.
Evidence is `target/BS1-isolation/{old,issue,tick}/`, including each CSV and
report, and `comparison.txt`. The reference and two isolated runs were already
started when lanekeeper's two-run cap arrived, and all three completed.

## Jerus's approval and fresh gate

Asked "Ship BS1, passing A1 and FoodProcessingCheck as noise?", Jerus replied
"yes ship bs1" on 2026-11-12, about 15:50. On floating point he said
"just do your best, but mostly its an issue that cant be solved". On
FoodProcessingCheck he said "its just noise, no?". Lanekeeper relayed these
words in `20261112-155225-270-lanekeeper-8410`, lifting the earlier A1 hold.

A1's 10/16 seeds in band against the baseline's 13/16 is accepted as path
noise from the bank's stance flipping at month 142. FoodProcessingCheck's
"...and the difference is most of what a Meat Works is worth" is passed by
name, with FPK1 queued. The ship file records that approval. It also names
OX1 for DeathRecordCheck's already recorded orphan line. The other covered
reds already have scoped entries and queued fixes in the repository,
MonetaryCheck MQ1, CentralBankCheck CB2, HealthCheck HX1, PlanCheck PX1 and
KeptCheck KS1.

Lanekeeper directs the fresh gate after DM2 because main will move, and
explicitly waives the builder's dry run for this readiness handoff. The
branch stays based on `fc3b6be5`. The final push adds the ship file last.

The saved post-CreditCheck-correction suite was rejudged with the ship file's
known-red entries on 2026-11-12. Exit **3**, the FoodProcessingCheck line
accepted as known. Evidence is `target/checks/BS1-approval-judge.txt`, with
the combined approval record in `BS1-approved-known-reds.txt`. This rejudges
the completed checks, and the fresh merged-tree gate belongs to lanekeeper.

## FPK1

FoodProcessingCheck 5's meat bet sets its own farm-floor and ship prices,
so it tests the Meat Works' profit sum independently of the city's path.
The current case reads the city's own meat prices and moves with the path,
1.8x on BS1 and 2.6x on main. This is the queued fixture repair authorized
with the shipping approval above.
