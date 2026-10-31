# SG1 - the student grant on a surplus share is paid on cash (0.8.84, 2026-10-31)

*The batch's record. The brief is `docs/queue/SG1.md` (Jerus, 2026-10-31, settling CD7-G: "Cash only (Recommended)").
A small fix, written whole by one Sonnet 5.5 session at xhigh, no subagents; built on main at 0.8.83 (CD7).*

## 1. The fault and the fix

- **The fault** (found by the stranger's review at CD7's gate): the grant on its surplus-share basis paid
  `amount * max(0, treasurySurplus)`, and `treasurySurplus` is the budget's balance, which holds CD7's exchange gain on
  the dollar debt and CD3's gain on paper bought back below its book. Neither is cash; the treasury bridge takes both
  out again. A D$ that rose against a large dollar debt paid its paper gain out the next month as cash grants, and a
  loss that took the balance below nothing paid no grant on a cash surplus. In `DebtExchangeCheck`'s new city the 10%
  rise books D$7,467k on a cash surplus of $2,683k, and a grant on the balance would have paid $5,074k against $1,343k.
- **The fix**: `Game.getTreasuryCashSurplus()` = `treasurySurplus + treasuryNotCash`, the bridge's own identity (surplus
  + not cash + raised - repaid + the rest = the cash's change), read by `studentGrantBillUnder()` (the bill, the
  save's re-strike, the Schools page's preview) and `grantAmountAs()` (the dial a picked basis starts at). One sum, the
  bridge's, so a non-cash line added to `treasuryNotCash` later is out of the grant too. Nothing is saved or changed
  in format: both figures were already in the government month.
- **The grant card** (`PolicyScreen.grantInfo()`) says "(X of it cash, which a surplus share is a share of)" after
  "last month's surplus was", only when the two differ by more than $50.
- `TaxPolicy`'s javadoc names the cash surplus (the `lastSurplus` parameter, the basis, the enum constant).

## 2. Calls made where the brief left one open

- **★1 The cash surplus adds back the discount amortised and the uplift not paid**, because the bridge's not-cash row
  is `interestNotCash + upliftNotCash - retiredGain - debtExchangeGain` and the brief said to use that sum. The budget
  charges the discount and the treasury never pays it, so it is not cash either way; the effect on a city with
  discount paper and no gain is a grant a few tenths of a percent larger (3,197.9 against 3,195.1 in a quiet month of
  the new city). Narrower alternative: take only the two gains off. Recommended: keep.
- The history's `surplus` series, the rollover's netting and the fund's rules are not touched (CD7 gave them their
  own subtraction of the exchange line).

## 3. Checks

- `DebtExchangeCheck` sections 11 and 12 (117 assertions in all). A city with homes and the schools to fill
  (`EducationCheck.city()` and `schools()`), every tax at its ceiling so it runs a cash surplus, a US$60M bond abroad
  and the D$ pinned: a quiet month, a 10% rise, a 10% fall, a month held; and a D$100M domestic bond bought back
  below its book. The premises are asserted (students, a cash surplus, a gain or loss that tells the two grants
  apart). With the fix reverted to `treasurySurplus` the section fails 14 assertions.
- `EducationCheck` (its surplus-share section: no non-cash line in its fixture, so unchanged) passes; `--pick` below.
- **The ensemble is skipped**: the default grant is FIXED, which reads no surplus, and `studentGrantBillUnder()`
  evaluates the new getter without side effects, so the default playtest's trace cannot move.
- Not looked at in the running game: the grant card's new clause.
