# BK1 - the bank's equity split, and its interest margin

Queued 2026-10-28 by the orchestrator, from issue #5
(https://github.com/2048Israel/CityBuilderSim/issues/5, filed by a Claude
playtest session at Jerus's request); Jerus placed it: "Tomorrow", first after
the night line of 10-28/29.

## What the issue saw

Civic Ledger 0.8.62, Jerus's San Francisco city (slot 8), month 2,852, paused,
Bank > Overview:

1. **$212.5M of the bank's equity is neither paid in nor retained.**
   `Bank.equitySplitResidual()` (`paidInCapital() + retainedEarnings() +
   preferredOutstanding() - equity()`) is $212.5M, and the red card at
   `ui/BankScreen.java` about line 663 says so. Either something moves equity
   outside paid-in capital and retained earnings, or the load path
   (`Bank.restoreEquitySplit`) restores the two counters out of step with
   equity. The game's promise is that every dollar is accounted for: this one
   first.
2. **The interest margin reads 9,972.71%.** `Bank.netInterestMarginOverYear()`
   divides a year of `Line.NET_INTEREST` by the average `Line.BOOK`, the loan
   book alone ($84.2M here), while net interest includes what the bank earns
   on bonds and reserves (deposits $118.8T). `Bank.netInterestMargin()` has the
   same denominator. Screens read it at `BankScreen.java` about lines 1023,
   1619 and 1897.

## What BK1 does

1. **Find where the equity split breaks, first.** Copy the San Francisco save
   (slot 8) to a `GameFiles.scratch` folder, **never writing slot 8 or any slot
   1-9**. Load the copy and find the month the residual first opens: a flow
   that moves equity around paid-in and retained, or the load. Fix it at its
   cause, so the residual is zero every month, and add a harness section whose
   fixture causes the case. A save that is already out of step: say what a
   load of it does (CLAUDE.md's old-saves rule; no conversion code).
2. **The margin on earning assets.** Net interest over the bank's average
   earning assets (loans, bonds and reserves that earn), monthly and over 12
   months, wherever the screens read it. The card's words say what it is. A
   harness section asserts it against the model's own lines.
3. **Look, and report only:**
   - the money that piles up over 237 years while 22% are out of work
     (treasury $38.5T, deposits $118.8T, GDP $8.4T a year): intended or not,
     with figures;
   - the land office map's island label, hard to read over busy ground.
   Each goes under `## pending` with a recommendation; neither is fixed here.

## Scope and checks

`Bank`, `ui/BankScreen`, and whatever moves the equity. The trace may move if
a flow is fixed: compare it, and run the ensemble if it does. A version
(`{next}`). Close issue #5 from the ship file's notes once BK1 is on main (the
orchestrator does).
