# HT1 — household basis and investment taxes

2026-11-21. HT1codex1, gpt-6.1-sol/xhigh, Codex. New isolated batch/HT1 at launch base f2dba24270a0ba34bd08f3214285d9e98c1bb942; origin/main later advanced only QUEUE to 9d4467e8ae610a777f7ce445702c205f3984042c; orchestrator explicitly said not to merge that records-only update to rerun checks. This is a provisional, incomplete checkpoint, **not ready for publication**.

## Accepted scope and what is implemented

QUEUE HT1 and spec-households sections 3.1-3.6, 4, 5, 7 and 8 remain the design. No alternate capital-gains tax or dividend design was substituted. No stage/cell ordering, sector/company IDs or enums changed. moves_playtest=yes.

Average acquisition cost is one per-household stock per company. FundLedger's existing proportional release was extracted and used by its original sale path and HouseholdBalance. Purchases add actual money paid; founders add the actual founding book/price. Exchange fills and spending-waterfall sales release average basis; a split preserves total cost. Losses offset same-month gains in either fill order and carry unused balances to later gains. Basis and loss carry enter stocks(), including stage moves, survivors, the pool, owner-line count changes and empty-cell folding. Folded basis uses its associated share distribution, rather than distributing by historic costs. Surrendered shares move their remaining basis into loss carry.

Sales collect the accepted 50% inclusion at the holder's banded wage rate; nonworking holders use the wage base. The inclusion dial clamps to 0-100%, is copied/staged/saved, and appears on Policy > Taxes > Wage. Integration leaves dividends untaxed at default wage/profit rates and nets the actual tax from savings/investmentIncome when a wage band is raised. The bank credits the Retail profit-tax rate it actually pays. Gross dividends remain gross on the share register, so the foreign dividend is not inflated by domestic tax.

Game receives actual tax cash immediately; MoneyAudit names both household-to-treasury transfers. NationalAccounts and GovernmentScreen have two named revenue lines. TreasuryJournal saves the actual receipts but excludes these already-budgeted revenues from cash-bridge adjustments. YearBook declares both new history series FLOW; no GDP definition/identity/classification was changed.

## Saves

SAVE_FORMAT and OLDEST_LOADABLE_FORMAT both rise 58 -> 59. A zero missing basis would tax the whole first sale, so the accepted incompatible-save refusal is required. No conversion code was added. CELL_SLOTS_BEFORE_BASIS pins the old end and appends company basis, loss carry and current-month taxable gains. DataSave also carries struck tax transfers and actual call counts. Both Game load routes restore these; HistorySave records/copies/scales the two money flows. The named save proof uses nonzero distinct values, compares every cell slot through both real load routes, preserves nonzero histories, and actually refuses the previous format. It writes only scratch slot 10.

## Blocking design calls - not silently omitted

1. **Estate/departure tax collection is not implemented yet.** Section 3.6 requires tax before heirs or departing savings leave. Section 3.3 requires the estate's rest to pass in kind, explicitly no stock-market moves. An appreciated portfolio can owe more tax than its savings. There is no accepted rule for unpaid tax, tax priority versus revolving debt, a tax liability stock, forced sales, or a cash cap. Forced sales would change that accepted in-kind rule; forgiving/capping tax would change the accepted tax design; negative savings would violate stock clamping/conservation. Sent exact issue to orchestrator at 17:02, clarified 17:06, 17:11, 17:13 and 17:16 Halifax time. At 17:23 orchestrator explicitly HELD this phase for Jerus and moved HT1 Queued -> Pending on main9d4467e8. An explicit owner decision is needed before completing the deemed-disposition/step-up and departure flows and their conservation tests. Survivors/stage/pool transfers already carry basis/loss with shares untaxed. No estate/departure proof is claimed.

2. **R6-G-P's L7 TFSA needs an account design.** research-1996-2024 sections 3/8.2 and R6-G records give Jan 2009, $5,000 annual room, untaxed gains/withdrawals, work 4,210 and a Policy lever. They do not specify eligible holders, account/allocation stocks, contribution allocation, withdrawal/recontribution room, treatment at death/departure, or how a player opens/uses it. No existing shelter/account handle was found. A gains tax exists in this patch, but the shelter mechanic does not. An inert catalogue row or a $5,000 gains deduction would invent the effect. research.json is deliberately unchanged, with this scoped owner item explicitly blocked. Sent to orchestrator at 17:01 and 17:02; decision outstanding.

## Checks and evidence

- --fast: compile/maps/Stale passed, 0 firm findings.
- CapitalGainsCheck: first completed run passed; actual two-price fills, partial/full releases, gain-before-loss/refund, carry, founders, offerings, actual split trigger, integration and stage/pool conservation. In 120 real months: 8,233 gain assessments, 17,451 dividend assessments, 64.20752381053605 actual gain-tax transfers; every monthly pool/household/treasury identity held. Final expanded run also passed (0 failures): spending-waterfall net cash conservation, losses before gains, bank integration at Retail's effective profit rate, inclusion endpoints/copy, all appended save fields/history through both loaders, and actual format-58 refusal.
- FundLedgerCheck: passed, proving the existing fund average-cost behavior after extraction.
- HouseholdFlowCheck: passed; EquityCheck: passed.
- SaveFileCheck: passed, including nonzero HT1 fields/history through both load routes and actual incompatible-format refusal.
- ReadPathCheck: first attempt failed in the new assertion because it preceded the harness's out initialization. This was HT1's own harness placement error, mended by moving it after initialization; rerun passed. The HT1 saved-field assertions themselves had passed before that failure.
- The initial CapitalGains wrapper selected zero harnesses because its registry append followed that javac snapshot; recorded incomplete, never called green. The subsequent registered run above passed.

Each named wrapper supplies compile/maps/Stale; no immediate unchanged fast replay. Full pick suite, LongPlaytest, ensemble and dry run have not run. All worktree JVMs use main's C:/ earth-2.bin/world-1.bin. No user slots 1-9 writes. No remote job or retained gate evidence touched. Individual output remains under target/checks/NAME.log.

## Review/publication floor and next work

Money and saves require a fresh stranger review; this builder does not self-certify it. Pick/review output will be appended after the checkpoint is committed. Final diff inspection restored 12 untouched original Unicode comment lines in Game after a shell encoding read had altered them; indexes were regenerated after that comment-only correction, with no model change or unchanged suite replay. All six required named wrappers exited 0; the final wrapper regenerated maps and found 0 firm Stale findings. The earlier ReadPath placement failure was mended, with no known-red row or waiver. The default trace moves, so the **final** model owes the gate-box-first ensemble on the newest committed baseline, and explicit orchestrator GO/heavy ordering; the model is not final while the above calls remain unresolved. No ensemble, dry-run, publication or marker removal is authorized by this checkpoint.

Actual sources: Household, HouseholdBalance, FundLedger, Exchange, Equity, TaxPolicy, Game, DataSave, HistorySave, GameVersion (save constants only), MoneyAudit, TreasuryJournal, NationalAccounts, YearBook, ui/GovernmentScreen, ui/PolicyScreen, CapitalGainsCheck, AllChecks registry, SaveFileCheck and ReadPathCheck, plus generated indexes. Orchestrator accepted NationalAccounts/GovernmentScreen on 17:05; YearBook's two flow declarations were explained before editing at 17:17. No other lane's dated-home, apprentice, ladder or housing fixture sections were edited. Publisher VERSION/QUEUE/changelog/gate records unchanged. No process/test machinery changed; no one-in/one-out removal is invented.

Eye check once the mechanic is complete: Policy > Taxes > Wage inclusion endpoints/default and staged apply; Government revenues show the two receipts and their histories; the budget-to-cash bridge counts each receipt once.

## Checkpoint pick (classification only; no full suite launched)

Source checkpoint 5c29f8dcd. `sh tools/pick.sh origin/main HEAD` against records-only main9d4467e8 printed:

```
ALL
pick      ALL 238 harnesses - src/main/java/ham/citybuildersim/AllChecks.java runs the suite (59 files changed since f2dba242)
review    needed - money: FundLedger.java, TreasuryJournal.java; saves: DataSave.java, HistorySave.java, +SAVE_FORMAT, +OLDEST_LOADABLE_FORMAT
```

This is a blocked checkpoint, not complete mends, a readiness claim, or a review/ship request. The provisional marker stays. No owned job remains running. Independent checks are green, but the held tax-collection phase, L7 account design, fresh review, final ensemble and any ordered full gate remain unfulfilled.
