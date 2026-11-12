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

## Verification

Checks and final gate identities will be recorded here as they complete.
