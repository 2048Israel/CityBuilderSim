# EM1 - an efficient market: one value for a share, every holder trading on it

Queued 2026-10-28, split out of [HD1](HD1.md) the same day. Jerus: "why hold
something that is 1.5 book value, when it can get something else that is 1.2
book value and has same P/E and forward P/E or something like that
comparable? perhaps make it an actual efficient market", then "Yes, split it".
After HD1, whose coupon-as-profit changes the earnings EM1 values. Figures
are from his San Francisco autosave (m2,852, 0.8.62), screen dollars.

## Why

- **Nobody sells for price.** Households hold about 100% of every company
  from founding (`HouseholdBalance.grantFounders`) and "never sold except
  short of money" (`Exchange.java:936-948`): their buy rule reads the price
  (the best dividend yield over the deposit rate plus `HOUSEHOLD_PREMIUM`),
  their sell rule does not. So a company's buyback, resting at fair x 1.10
  (`Exchange.java:892`), fills $0.58B of $4.4T bid a month, and Retail's book
  holds no ask at all. A bigger premium would find no more sellers.
- **The value is book or dividends, never earnings.** `Equity.priceOf`
  (`Equity.java:541-563`) is max(book, a year's dividend over the world rate
  plus `FOREIGN_PREMIUM`). Book is 95% cash and dollars abroad, so every share
  trades at about 1.1x book with P/E 100-250, dividend yields 0.14-0.41% and
  ROE under 1.1%: boxes of cash, priced as such, earning less than the dollars
  abroad (2%) they could be.
- **The cash stays in the company.** With no seller the buyback spends
  nothing; his 0.7.12 call ("the unspent cash stays in the company",
  `Exchange.java:852-860`) stands, so the way out is a buyback that fills,
  not a special dividend.
- **Short selling is not the fix** (it does not exist, `Exchange.java:770`).
  A short here sells a share backed by real cash into a company bidding 10%
  over it, then has to buy it back from holders who never sell.

## What EM1 does

1. **One value for a share.** Spare cash (over a working cushion, dollars
   abroad included) at face, plus the business on its earnings - smoothed
   trailing net income, and a forward figure if the model can give one
   honestly - capitalised at the required return (the world rate plus a
   premium) less its growth. It replaces `Equity.priceOf`'s max(book,
   dividends) wherever fair value is read, and the screens say what it is.
2. **One yardstick for every use of money.** A share's expected return is its
   earnings plus what its cash earns, over its price. Every holder compares it
   with the other shares and with the bank's deposit rate, the bonds and the
   dollars abroad.
3. **Every holder trades on it.** Households, the fund, the world and the desk
   sell a share whose bid is above its value, or whose return is a band below
   the best alternative, and buy the reverse: a pace a month (the bonds'
   speeds) and a band (about 5-10%, a dial) so a quiet market does not churn.
   A household short of money still sells as today. Sectors may differ on
   growth and risk; the comparison is on expected return, not raw P/B or P/E.
4. **The buyback finds its sellers.** The company's bid is unchanged, and a
   holder whose value is below it takes it; its cash reaches the owners that
   way. Say how much a month, against the $4.4T bid.

## Checks

A harness section for each, its fixture causing the case: two companies alike
but for price (holders move to the cheaper one, and the two prices close); a
cash-box company with a buyback (the buyback fills, its cash falls); a share
earning less than dollars abroad (it is sold); a quiet market (no prices
drift, no churn); a company with real earnings and little cash (valued on its
earnings, not its book). A LongPlaytest before and after, under `## pending`:
businesses' cash and dollars abroad against GDP at m600, m1200 and m2400,
buybacks filled against bid, the spread of P/B and P/E across companies, and
share turnover. Every trace moves: compare it and run the ensemble. Scope:
`Equity`, `Exchange`, the fund's and the world's rules, the screens that show
fair value. A version (`{next}`).
