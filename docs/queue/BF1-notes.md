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

## Implementation review

The final sales path keeps the existing stock target: Markets clears local
orders, then Sector.shipUnsoldStock sells the shed's remainder to the world
at its net-bid marginal-cost/dump-line rule. The following production fills
the shed again. This fixes the missing buyer, rather than removing inventories;
WoodProductsCheck's positive-yard premise passes unchanged. The revised mill
fixture explicitly makes a first month's stock and asks for its second-month
export; it checks the same deadlock without assuming zero stock or immediate
sales from newly produced stock.

settleRetirement pays cash against interim claims first, then ordinary bank
claims pro rata; repayments enter the existing cash settlement and debt
roll-forward. If no plant or work on site remains, the refreshed borrower
goes through the existing backstop even if its only assets were sale cash.
A closure inside an existing default episode resolves the remainder without
counting a second episode. A partial retirement carries the unpaid balance.

Bank.projectHeadroom supplies a finite shared growth budget using the existing
headroom, capitalTarget and leverageTarget. Defaults beyond their sector's
allowance reduce it before replacements can be financed. The loan and mortgage
growth doors both read it; working-capital lines retain their existing rule.
No save format change: the closure handoff is consumed within the turn, the
growth allowance is struck at the desk's existing monthly reprice, and the
repayment/save records already carry these flows.

★ Failure frequency is an observation, as the brief requests; a bank taking
real credit losses is not guaranteed survival. An initial exploratory assertion
of universal survival was too strong and is not the model's rule. It is replaced
by the bank's equity roll-forward at MoneyAudit's own tolerance, alongside the
24-seed count and hard causal fixtures. The remaining failures are reported,
not hidden behind a special seed or a rescue.
