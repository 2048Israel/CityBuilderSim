# LF1 - notes: the short's lending fee on the lenders' books (0.9.52, 2026-11-08)

The brief: [LF1.md](LF1.md), from the stranger's review at EM2's gate. Run as a Sonnet-whole fix batch (no slices), from `main` after
BK2 (0.9.50). Today a real run opens no short (EM2's notes), so every change below sits behind a short being open and the default
playtest's trace is expected unmoved: it was not rerun through the ensemble.

## 1. The two books

- **The households' fee** is a pair in `MoneyAudit`, the one `ForeignInterest` uses and for the same reason: `+ households LendingFee`
  (INCOME, which `ForeignAccounts.takeMonth` reads as `incomeIn`, so the current account and its rate now see the fee) and
  `- households LendingFeeBanked` (DOMESTIC), both `Exchange.getLendingFeeToCells()`. The pool identity sees nothing, as before:
  the cells and the world are both outside the pools. ShortSellingCheck 3's city (a short open through six months) reads the two
  lines off the audit's detail and they add to the exchange's own figure ($0.14 in that town).
- **The fund's fee** had touched its cash alone. `TreasuryFund.receiveLendingFee(company, amount, month)` now also adds to
  `lendingFees` (life) and `monthLendingFees`, and `FundLedger.lendingFee()` credits the company's market lot's income and files one
  `LENDING_FEE` row a month, as the dividend does. `FundView` counts it: income last month (dividends + coupons + fees), the
  "Company shares" kind's income and gain (so the kinds' gains still add to the whole - FundLedgerCheck 7 now pays a fee to cause it),
  and the record's group (`INCOME`). `FundScreen` shows a "Lending fees" line under WHAT CAME IN LAST MONTH in a month one was paid.
- **Saved**: `TreasuryFund.State.life` gains a 22nd slot and `month` a tenth (new tail slots; an older, shorter array reads none),
  so no format bump. `SaveFileCheck`'s EM2 fixture now has the fund lend (it took a quarter of the biggest holder's shares, so
  `lentByFund > 0` where it was 0 == 0) and a fee stamped through the real receiver; the restored fund's counters and the ledger's
  rows are compared, and the fixture asserts they are not two zeros. The old-save fixture (`month` of 9 slots) reads ten now.

## 2. "Main's cell-to-world share trades have no line either - is that right?"

Right for the pool identity, wrong for the financial account, and not fixed here. The cells buy from and sell to the world
(`Exchange.getHouseholdsBoughtAbroad/SoldAbroad`, per company), both outside the pools, so the identity needs nothing. But
`BondMarket` books the same trade for bonds as a FINANCIAL/DOMESTIC pair (`+ bonds HouseholdsSoldAbroad`, `- bonds
HouseholdsBoughtAbroad`...), so the balance of payments' financial account holds the cells' bonds and leaves out their shares; nothing in
`MoneyAudit` reads the exchange's two getters. A pair would put them in and move `ForeignAccounts`' financial flows, so the rate and
the trace: not a fix-batch change. Queued as LF1b (ship file), size unmeasured.

## 3. The four edges, each caused by a fixture first (ShortSellingCheck 8)

| # | verdict | fixture | what changed |
|---|---|---|---|
| 1 | real | 60 short, the cells sold, the lenders hold 50 (section 5's recall bench): the fee was charged on 60 | `strikeLending()` lends `min(S, lendable)` and charges the fee on it (rate at that utilisation); `restrikeLentBook()` the same. The other way the brief named, a recall at the sale, is not needed: the step's own recall buys the 10 in. The lent book is under S for that month only |
| 2 | real | a town holds the founders' 10,000 and the 3,000 a short sold it, and all of it leaves: the cap at the shares in issue dropped the 3,000 | `Equity.followEmigrants(households, shortOf)` caps at shares + S; Game passes `exchange::shortShares`. The one-argument form (no short known) is unchanged and kept for the fixtures that call it |
| 3 | real | 40 short to the desk, every share retired, a step: the desk kept 40 of a company with nothing in issue and a later `listIfUnlisted` held them on top of the founders' | in the valuation loop's zero-shares branch `Exchange.cancelShortHeld()` empties the cells (`surrenderShares`), the desk, the world and the fund's market book (`Equity.cancelHeld()`), and `FundLedger.sharesGone()` closes the fund's lot as a realized loss (WRITTEN_DOWN "gone"). No money moves: the buyers lose a valuation, the world keeps what it was paid |
| 4 | real | `seedConstants` lifts minFair to $1.50 over a $1 share, a print at $2, a short open: the margin call never fired; with none open the same quote opened no short either way | `postShorts()` skips a company at or under minFair only when no short is open there, and the "short more" branch needs fair > minFair |
| 5 | done | SaveFileCheck's fund lent nothing | the fixture above |

## 4. ★ calls

- ★ LF1b, the cells' share trades with the world as a FINANCIAL/DOMESTIC pair (section 2). Recommend: queue it, a batch of its own,
  run the ensemble. Not done here because it moves the trace.
- ★ Edge 3 wipes the fund's market holdings of a company with no shares in issue (a realized loss in its ledger). The other way is to
  keep the S shares and re-list around them; recommended the wipe, since a share with no company is nothing and the 4 fixtures show the
  register whole after it.

## 5. Review hints (it touches money)

Read: `MoneyAudit` the two new lines and their scopes (INCOME in, DOMESTIC out, same size); `Exchange.strikeLending`, `restrikeLentBook`,
`postShorts`, `cancelShortHeld`; `TreasuryFund` the new counters through State/restore/reset/redenominate (`lendingFees` is a
life counter, `monthLendingFees` cleared in `startMonth`); `FundLedger.lendingFee`/`sharesGone`; `FundView.portfolio`. Not changed: the
fee's rate, the quote it is struck at, who is paid pro rata. The trade-off a reviewer may weigh: the lent book is now under S in the month a
lender sold, where it used to be scaled up to S; nothing but the Sector screen's lending split (`SectorScreen` about 2382, as a share of
`shortShares`) reads it, and that split then adds to under 100% for the month.

## 6. Checks

`./batch-check.sh --pick` on the merge with main (1b80cc5c): **exit 3**, the known reds only (PriceLevelCheck, ScaleCheck, MapCheck), 50 harnesses
and LongPlaytest in place of the Game.java readers. ShortSellingCheck 8 (27 assertions), FundLedgerCheck 7, SaveFileCheck's EM2 section and
ReadPathCheck's fingerprint carry the new state. The edge fixtures cause their conditions and pass; the audit pair is discriminated (the cells' fee
is $0.14 in section 3's town, against a tolerance of half a cent a month).

**Run the pick with the Earth and world files named**, as the gate does (`JDK_JAVA_OPTIONS="-Dcbs.earth=<main>/data/earth-2.bin
-Dcbs.world=<main>/data/world-1.bin"`). A worktree has no `data/`, and without them seven harnesses (MortgageCheck, BuildCardCheck,
HouseholdCheck, MapCheck, BusinessServicesCheck, AuctionCheck, PrizeCheck: 25 lines) are red - word for word the same on a clean checkout of
origin/main, so not this batch's - and with a junction to `data/` instead of naming them, SaveFileCheck's "no world file" fixture is red.
Named, exit 3. (Not a fix for the batch; only so the next session does not chase them.)

Process lines: none changed.
