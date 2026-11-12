# BS1 — Bond settles to a fixed point, the bid tick and the buyback

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

Checks and final gate identities will be recorded here as they complete.
