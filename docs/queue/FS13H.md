# FS13H — cause room for the fund's hand-limit ticket

Companion to the shipped FS13 sweep, queued under the orchestrator's HT1
end-to-end direction of 2026-11-22. Do not reuse the merged FS13 batch.

HT1's versioned dry run `cbs-gate-HT1-AnHRdJzp` first stopped earlier in
FundCheck at a genuinely extinct corporate-bond position still held by a
household. HT1 mends that owner defect. Once that run can finish, section10
reports `a limit posts at its limit`: Retail is already at its cap.

The original warmed book and the original hand fixture were replayed on the
dry run's production classes, without clearing any asset or changing the
fund's rule. Native month76 holds 453.0628287024921 of 4530.628287024921
Retail shares, exactly `TreasuryFund.OWNERSHIP_LIMIT` (0.1). Its ledger
records the real 111.11111111111116-share limit order as LAPSED, `no room
under the 10% cap`, at its actual limit 0.02329511745868057. No hand order
is posted. The observed cumulative split factor is1 before and after;
splits do not explain the failure. Exchange.postFund's cap/drop route and
the ordinary TreasuryFund controller are unchanged from main.

Cause a real listed share's available room before the hand's fair-value,
limit and cancellation phases. Preserve the cap, first-come hand priority,
actual cash/holdings, exact limit and cancellation assertions, and a
separate no-room rejection control. Do not disable the rule or silently
discard inherited/tax-bearing holdings. Existing FS13 money/ledger laws stay.

Evidence: HT1 notes, `target/ht1-dry-mends-20261122-0220/` (the native
FundCheck first completed run and immutable original-class hand observer).
