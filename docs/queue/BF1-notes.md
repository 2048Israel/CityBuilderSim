# BF1 — The hands-off bank fails on the sawmills

Built from main e5642145 (SX1, 0.9.101), on batch/BF1. HW1's named
files are kept out of this batch.

## Evidence before the mends

- LD2's historical count was 8/24; the current SX1 tree is 5/24 (seeds
  7, 10, 14, 18, 23). The original Ld2Bank probe is retained in the tree;
  BF1 also audits all 120 months, including months after a bank fails.
- BankFailureCheck --fixtures on unchanged model: five failed laws,
  all fixture premises green. The mill stocks its entire low-rate output
  under DUMP_THRESHOLD with no local demand; exports get only the spare
  past that speculative stock target. Closing sells land/material but
  leaves loans; the default slice is not a whole-borrower resolution.
  New growth above the bank's current target has an infinite allowance,
  and the allowance struck before defaults is reused after losses.
- Historical source 31f675b6 was compiled under target/bf1-historic, using
  scratch GameFiles only. Seed 4 m48→49: 14,444.25931 shares become
  14.44426, Exchange.getSplit=.001. The world's sales transfer to the
  desk before the consolidation. Its fair value/mark falls at the same
  time. This disproves LD2's inference that shares disappeared without
  a split. SX1 moves when this happens; its split settlement is preserved.

## Calls

- ★ Exportable output with zero domestic demand should be made for its
  world buyers, rather than reserving an empty domestic market's dump-line
  buffer. The planner's export-profit premise must agree with production.
- ★ Sale proceeds repay the closing borrower's bank loans in rank order;
  an emptied borrower enters the existing resolution and exclusion rules.
  Remaining productive assets or work on site permit carrying the loans.
- ★ Growth commitments must fit the bank's own capital target after losses
  and consume one shared allowance; working-capital promises keep their
  existing treatment. No rule equates total lending with paid-in capital.
- The apparent share-count loss is tested in split-adjusted units. A fair
  valuation loss is not repaired by handing investors their book equity.

## Checks and records

Work in progress. Pre-mend logs are target/checks/bf1-fixtures-before.log,
bf1-count-before.log, bf1-before-seeds.log and bf1-ld2-shares.log.
