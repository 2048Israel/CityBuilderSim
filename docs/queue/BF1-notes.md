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

- ★ Exportable stock left after local buyers should also reach the world's
  buyers, using the existing net-bid/marginal-cost and dump-line rules.
  The planner's export-profit premise must agree with actual sales.
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

The final low-fill mill fixture includes a wage bill and imported feed. With
Sector and Markets replaced by the unchanged e5642145 versions (all other
fixes left on), all its premises remain green and three sales/invoice laws
fail; the BF1 versions pass. Logs: target/checks/bf1-stock-before.log and
bf1-final-fixtures.log. Closing and capital refusal were independently red
before their mends in bf1-fixtures-before.log.

The first full pick's default 2,400-month LongPlaytest is green. Its economic
trace differs from the stored SX1 gate's LongPlaytest.log beyond runtime
(first served-works reading and the market/stock series differ), so the final
model must run the ensemble against docs/ensemble/0.9.101.md.

The first full pick exposed a missing ordering constraint: warehouse exports
could sell stock after the warehouse had been demolished, ahead of the old
end-of-market cap. GdpCheck's unchanged write-off fixtures caught it. Stock
past the surviving room is now written off before the market or the closing
borrower's credit reading. Production adds any further write-off to the same
existing flow. The initial BF1 ensemble snapshot was stopped by its own run
folder/PIDs and is superseded; the final model will use a new label.

The changed path also exposes KeptCheck 55's Bank Overview tag churn: 9 months
allocate one label and 6 allocate two, most saying "defaulting". Its unchanged
25-month prefix reproduces 8 quiet months. Pieces.rungRow now retains its status
label hidden and unmanaged when absent. This is a UI reuse fix; it does not
change what the player sees or weaken the assertion. Temporary instrumentation
is isolated under target/bf1-kept-classes and is not in the source.

## Corrections from the first full pick

The generic warehouse exporter reached food and refinery policies which were
not the processing planner's contract. The final exporter is ProcessingSector's:
Wood Products, Copper Refining, Cement & Concrete and Heavy Industry. Sector's
hook defaults to no shipment, preserving other sectors' sales policies.
Consequently GdpCheck, SaveFileCheck, RefineryCheck, GoldMiningCheck and SlopeCheck
pass, including the original food-conservation law. No HW1 source or fixture
is changed. The temporary GDP restocking change was removed.

The narrower model's first price-level twin diverges physically at month 39.
A probe finds the earlier difference at month 16: a near-zero Real Estate
cash subtraction draws a working-capital loss buffer in one denomination and
none in the other. ★ A repayment short only by MoneyAudit's existing floating
point allowance (floor zero) is roundoff, not a new shortfall. EconomyManager
normalizes that tail before asset sales and credit; a caused one-ULP fixture
and a real-shortfall control retain the loss-buffer rule against BUFFER_MONTHS.

The first full pick also exposed three fixture assumptions. BankCheck's 200x
capital injection is topped up if subsequent lending means its reserves no
longer dwarf the live book; the 100x premise is unchanged. CurrencyRegimeCheck
keeps searching for its actual gain-branch window, stopping at the first hold,
with a 600-month cap (the probe reaches its first full window at month 173);
the gain/dent laws and inflation tolerance are unchanged. DebtExchangeCheck
compares the fall month's residual with a no-move counterfactual in the same
month, whose opening month, cash and debt must match, rather than assuming
ordinary residuals equal in successive months. Both financial controls passed
in isolated runs without relaxing a tolerance.
