# BS1 — Bond settles to a fixed point, the bid tick and the buyback

**Held for Jerus's A1 review**, under lanekeeper instruction
`20261112-092258-011-lanekeeper-e492`.

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

## Ship-file order and hold

Lanekeeper clarified the versioned launcher's dependency in bus instruction
`20261112-085949-898-lanekeeper-9dec`. The launcher requires the ship file
on origin before its dry run, while batch.md says to push it afterwards.
Lanekeeper initially authorized a provisional ship file for the dry run.
His later A1 hold supersedes that instruction. The ensemble baseline, notes
and completed pick evidence are to be pushed. The launcher gate and readiness
marker await Jerus's A1 decision.
