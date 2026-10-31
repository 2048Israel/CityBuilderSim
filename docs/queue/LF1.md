# LF1 - the short's lending fee on the lenders' books

Queued 2026-10-30 by the orchestrator, from the stranger's review at EM2's
gate (money and saves; verdict "ship + follow-up": no money created or
destroyed, the new state round-trips). Small: a Sonnet-whole fix batch
(`docs/batch.md`, 3), unless the edge cases below turn out larger.

## The fault

EM2's world pays a lending fee to whoever lent it the shares it is short, and
neither receiver records it:

- **The households** (`MoneyAudit` about 789-790, `HouseholdBalance` about
  4078): the cells' fee goes from the world into household savings with no
  audit line. The pool identity holds, because households are outside the
  pools, but households' foreign interest has its INCOME/DOMESTIC pair
  (about 750/1146) and this fee has none. So the current account and
  ForeignAccounts' rate never see it. (Main's cell-to-world share trades have
  no line either; say whether that is right.)
- **The fund** (`TreasuryFund.receiveLendingFee`, about 566): the fee lands in
  the fund's cash and touches no counter and no `FundLedger` row, so
  `FundView`'s month income (dividends and coupons) and its per-kind returns
  leave it out. The fund's cash rises with no line on its statements.

Today both are zero, since no short opens in a real run (EM2's notes): a
fixture has to open one.

## The edge cases the review raised

1. `Exchange` about 1150: between a lender selling or emigrating and the next
   step's recall, the short S can be more than the lenders hold, yet the fee
   is charged on all of S and paid pro rata. So lentCells and lentFund exceed
   holdings, and lenders are paid for a month on shares they no longer hold.
   Recall at the sale, or cap the fee at what is held.
2. PLAUSIBLE, `Equity.followEmigrants` about 715: it caps foreignShares at
   l.shares. With a short open the holdings rightly add up to shares + S, so
   if those left behind hold less than S, emigrating shares are dropped and
   the register is not whole (ShortSellingCheck 3's identity).
3. PLAUSIBLE, `Exchange` about 790: when shares in issue reach 0 the short is
   zeroed, but the buyers keep the S shares; a later `listIfUnlisted`
   re-lists with them diluting the founders.
4. PLAUSIBLE, `Exchange.postShorts` about 1199: a company with fair <= minFair
   is skipped, so a short open there is never margin-called, recalled or
   covered.
5. SaveFileCheck's EM2 section: its fund lends nothing (lentByFund 0 == 0).
   Make the fixture's fund lend.

Each case is caused by a fixture first. Fix those that are real, and say of
each which it was.
