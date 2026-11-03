# BD1 - the bank's equity parts part from its equity after it buys the city's paper ({next}, 2026-11-02)

**WHERE IT STANDS (paused 2026-11-02 ~20:32 on the orchestrator's word; resume when DZ1's ship file is on origin):**
1. WIP is local branch `bd1-wip` (from origin/batch/FC1 8c772b41), unpushed: FC1's switch out of SectorStatementCheck,
   `Bank.restrikeCarriedDiscount()`, `Game.restrikeBankDiscountCarried()` at the six sites (section 2). Both drafted by Sonnet, not yet reviewed.
2. Checked green before section 13: HoldersCheck, BankCheck, CentralBankCheck (`./batch-check.sh`, 3 of 3).
3. HoldersCheck section 13 (13a premium reopening, 13b desk on an unsettled line) is half-written by the stopped slice: finish it, then review the whole diff.
4. Then: `git checkout -b batch/BD1 origin/batch/DZ1` (or origin/main if DZ1 went to Pending), cherry-pick bd1-wip's WIP commit, `--pick`,
   SectorStatementCheck with `-Dcbs.world`/`-Dcbs.earth` set to the main checkout's data/ (the gate's path), the trace against the base, the ship file last.

## 1. The release, named

Reproduced on FC1's tip with FC1 on. The city's path depends on `data/world-1.bin`, which a worktree lacks, so a
worktree's run is not the gate's. With the main checkout's files named, the gate's path shows the same fault in months
134-135 at a smaller scale: 0.0287, against 7.06 on FC1's own path. The debt book's figure for the bank's unearned discount
(`DebtManager.bankUnearnedDiscount()`, re-read at every `Game.refreshBank()`) moves twice with no cause booked:

- **A line reopened at a premium.** `Game.issueLongBond()` -> `Debt.reopen()`: the premium eats the line's
  `discountLeft`, the bank's settled share with it (66.18 face for 67.81: 0.6915 -> 0, the bank's 0.028709 gone),
  after the month's last refresh. The next month's refresh released it with no cause. BK1 measures only across the settle.
- **The desk buying paper off a line with a sale still unsettled.** The desk hands the bank `Debt.unaccretedOn()` (the
  whole line pro rata), while the book counts the settled part over the settled face. The book's figure rose 0.0248 more
  than the bank carried. BK1's settle then re-blended it back and booked +0.0248 that the equity never saw.

## 2. The mend

A re-strike carried at once (`Bank.restrikeCarriedDiscount()`): it books `discountRestruck` and moves the carried
discount by the same, so the equity moves where the release happens, as a treasury buyback does between the presses.
It is measured as what the bank's trade moved its carried discount by, less what the book's figure moved by
(`Game.restrikeBankDiscountCarried()`). Sites: `issueLongBond()`'s reopen, the desk's three purchases, the treasury's
buyback, the call. The central bank's dial is left alone: it trades only pieces with nothing owed at the settle, where
the two figures agree. BK1's settle re-strike is unchanged: the same month's refresh realizes it with the settle's paper.
