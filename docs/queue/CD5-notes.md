# CD5 - floating and linked paper: notes

*0.8.35, 2026-10-23. The batch is `docs/studies/spec-city-debt.md` §8's CD5
row and the sections it points to: the floating-rate note on the dial (§2.4),
the inflation-linked bond on the price index with its deflation floor and its
uplift line (§2.5, §5.3, §5.4), and their choices on Borrow (D31). D25: floaters
at 2 and 5 years, linked bonds at 10 and 30, with the floor. Built on main at
bc7ed17c (0.8.33, CD4), merged with main at 758ac9e6 (PT1-M3, 0.8.34).*

## 1. What it does

- **The floating-rate note (`FloatingRateNote`, type FLOAT).** A coupon every
  month on the whole face at the month's policy rate plus a margin fixed at the
  sale, never under nothing; the face at the end. The books follow the KNOWN
  schedule (today's coupon held flat, IFRS 9 B5.4.5): at each month's top
  `DebtManager.processAllDebts()` hands it the dial (`reset()`), and when the
  dial has moved its effective rate is solved again at the carrying value as it
  stands (`Debt.reestimateRate()`), so the expense is the coupon plus the fee
  amortised (§5.3).
- **The inflation-linked bond (`InflationLinkedBond`, type LINKED).** A real
  coupon on the face times the index ratio (the city's `PriceIndex.getIndex()`
  over its level at the sale); at maturity the face through the principal path
  and the uplift, face x (max(1, ratio) - 1), through the coupon path, so every
  holder has it as income: the deflation floor is the max. The principal on the
  books and every holder's share stay the face (five models keep holdings in
  face); the indexation is in the flows. At each month's top `index()` reads the
  index and the carrying value catches up to the known schedule's present value
  at the unchanged real effective rate (IFRS 9 B5.4.6, `Debt.reestimateCarrying()`);
  the change is the month's **inflation uplift**, below nothing in a deflation
  month. The index is read with a month's lag: it takes its month after the debts
  are paid.
- **The market prices them on what it expects** (`DebtManager.pricingFlows()`):
  a floater's coupons on the dial the market expects from the policy rate
  (`expectedPolicyPath()`, CD1's path started at the dial), a linker's ratio on
  the inflation it expects (`expectedInflationGrowth()`, the struck pair over
  `Expectations.ADAPT_MONTHS`). `marketValue()`, `yieldOf()` (the nominal yield
  the households weigh against deposits) and the buyback's offers read them;
  `realYieldOf()` is a linker's real yield. Every other piece keeps its own
  schedule, the same array as before.
- **At auction (`Auction.Kind.FLOATING`, `LINKED`).** Fair is the par yield of
  the term (what the bidders weigh); the ask carries the coupon that prices the
  paper at 100 on the curve - `fairMargin()` by bisection, `fairRealCoupon()`
  closed form with the floor in it - and its schedule per 100. The margin is
  fair plus the tail, unrounded; the real coupon fair plus the tail rounded down
  to an eighth (D2), never under nothing. On the study's calm city the game
  gives policy + 17.9 and + 34.2 bp (study: 18 and 34) and real 1.64% and 2.19%
  (1.61% and 2.17%). Each sale its own line; the rollover's same structure rolls
  a floater into a floater and a linker into a linker, the central bank's par
  with them; none abroad (§2.7); nothing sells either unasked.
- **The books (`NationalAccounts`).** "Inflation uplift on linked bonds" is a
  budget line of its own (`getInflationUplift()`, in the total expenses); the
  uplift paid at maturity (`getUpliftPaidInCash()`) is taken off the interest
  handed to the budget, which stays the coupons and the discount, and off the
  bridge's not-cash row (`getUpliftNotCash()`, in `Game.getTreasuryNotCash()`
  and the EARNED walk). `DebtManager.getPricedDebt()` adds the indexation owed
  past the face, so the credit judgement sees all the city owes.
- **The screens (D31).** Borrow at home: Floating and Linked tiles, the terms as
  columns of today's fair margin and of the fair real coupon with the inflation
  it prices (`breakevenInflation()`), the line note, the auction card's coupon
  row, the quote card and the receipt at today's dial or index; none abroad.
  The book names a floater by its margin ("floating, policy + 18 bp, Jul 2010")
  and a linker by its real coupon ("1⅝% linked Jul 2015") and says the coupon,
  the index since the sale, last month's uplift and what it repays. Debt service,
  the Government tab's spending list and ring, its bridge ("Inflation uplift not
  yet paid"), the debt card's kinds and the interest payer rows carry the uplift.
- **The save.** The two pieces' fields ride with them (Gson); the load path's
  cases FLOAT and LINKED; the government block's slots 35 and 36. No format bump:
  an old save holds neither kind and reads no uplift, which is right.

## 2. How the money closes

No new cash path: a floater's coupon, a linker's coupon and its uplift go
through `Game.payDomesticCoupon()` and the face through `payDomesticPrincipal()`,
split by the holders' shares struck before the payment, so MoneyAudit sees them
as it sees any coupon. The uplift accrued is booked and is not cash; the uplift
paid is cash on the interest line and comes off the not-cash row. Over a
linker's life, interest expense plus uplift booked equals the cash coupons
(uplift paid included) plus the face less what the sale raised, to the cent, and
its carrying value closes to nothing - under a rising index and under a falling
one, where the floor repays the face; over a floater's, interest expense equals
the coupons plus the face less what was raised. FloatLinkCheck holds the audit
closed every month of those lives, with the households, the bank, the world and
the central bank holding parts, and the cash coupons to the holders equal to
what the treasury paid. The bridge's only residual is main's bank coupon timing
(★8).

## 3. ★ calls for Jerus

1. **★1 No reopening.** Each floater and linker sale is its own line. The US
   Treasury reopens both; a linker reopened mid-life needs its index ratio in the
   price of the new face. Keep, or reopen them as bonds are (D6).
2. **★2 The face stays the face.** A linker's principal on the books, the
   holders' shares and "owed at face" are the original face; the indexation is in
   the coupons, the carrying value and what it repays. The credit spread's priced
   debt adds the indexation owed, so a heavily indexed linker raises the city's
   own rate.
3. **★3 The uplift is paid as interest** at maturity (income to every holder,
   taxed or not as a coupon is), not as principal. The rollover refinances the
   face; the uplift is paid from cash.
4. **★4 The floater's coupon reads the dial of the month it is paid.** The study
   priced it on the dial a month before. So a floater is not quite deaf to the
   dial: +150 bp moves it -0.12 per 100 against -0.76 for a fixed 2-year bond.
5. **★5 Rounding.** The real coupon is rounded down to an eighth like a bond's,
   so a 10-year linker sells about a point under par; the floater's margin is not
   rounded (the US's high discount margin). Both coupons are floored at nothing.
6. **★6 The floor is priced at its intrinsic value only** (the expected index
   path, no volatility), so with inflation expected its option is worth nothing
   in the price.
7. **★7 The households offer a linker dear in a buyback.** CD4's ★1 sharper:
   their rule discounts the expected nominal flows, which grow with inflation, at
   the deposit rate plus their spread. In a rendered city a whole linker's
   buyback booked a loss of D$6.2M on D$15.0M carried.
8. **★8 The bridge's bank timing.** The bank's share of a linker's uplift paid at
   maturity is booked that month and paid the next, so the treasury bridge shows
   it as "Not accounted for" that month and the reverse next month - main's bank
   coupon timing (CD4-C), large here because the uplift is a lump.
9. **★9 The bank's books.** The bank keeps its straight-line discount on these
   too, and never carries a linker's uplift until it is paid (spec §10).

## 4. The checks, the trace and the ensemble

- **FloatLinkCheck** (new, 441 assertion lines, about 5 s): fair prices each at
  100 on a calm, a hiking and a flat curve, a basis point either way moving it the
  right way, and the floor binding on a falling index; a sale's margin and real
  coupon from the auction's tail, caused by a capped sale; refusals; none sold
  unasked in a default run; the floater's coupon following the dial and its rate
  resetting; the linker's uplift as the carrying value's catch-up, on the
  statements and in the bridge, and the EARNED walk leaving nothing on bank-held
  paper; both to maturity under inflation and deflation, the books closing over
  each life; the audit every month; half of each bought back; the rollover in the
  same structure; the save and a change of unit. Mutating the reset, the
  catch-up, the floor, the expense line or the pricing flows each turns it red.
- **The trace:** the default playtest never sells either, and its log on each
  slice's model is byte-identical to main's but for the run-time lines, so the
  ensemble was not run (EN1).
- **The suite:** the baseline on main was 135 of 135. The final run is §6.

## 5. Who wrote what

Sonnet 5.5 at xhigh wrote the three slices: the model, the harness and the
screens (rendered offscreen to PNG and looked at). Claude (Opus 5.5) wrote the
design and the briefs, reviewed each diff and the renders, ran the trace and
wrote the record.

## 6. The final checks

See the batch's report: `./batch-check.sh` on the final model merged with main.
