# RB2: the fund's withdrawal struck on a value that has already paid last month's toRaise

Found in FB4 (2026-11-17), while fixing RB1-R's item 1.

**The fault.** `TreasuryFund.payTransfer(value, idle, year)` pays last month's `toRaise` first, out of the fund's
cash (`late`). It then strikes this month's due, `dueOn(value, idle)`, on the `value` the caller passed:
`Game.fundValue()`, read before `late` left the cash. RB1-R fixed the same order of reads for the idle cash
(`Game.fundIdleCash()` now reads the cash less `max(0, toRaise)`), not for the value.

- The dial's share of the late money is charged once more in the same month.
- That share is `steps x WITHDRAWAL_STEP` of `late`: 0.25% at the default, 10% at the most of the dial (40 steps).
- It is the same mistake RB1-R fixed one line away.
- The preview (`PolicyPreview`, the due through `Game.fundTransferDue()`) reads the same value. It agrees with the
  month and shares the slip.

**Recommended.** Strike the due on `value - max(0, toRaise)`, in the model and the preview alike, and let a FundCheck
section with a toRaise carried in assert both against `dueAt()` on that value (FundCheck 9d's fixture is that city).

**It moves the playtest.** `GovernmentPlayer` runs the fund at the most of the dial, and a dial that sells to pay
carries a toRaise. The ensemble's rules apply.
