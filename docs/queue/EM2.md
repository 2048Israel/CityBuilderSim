# EM2 - a share quoted at the middle of its book, and short selling

Queued 2026-10-29 by the orchestrator, after EM1 (0.8.74). Jerus,
2026-10-29: "share price should reflect mid price, and if no ask or bid then
last traded. also, we need to implement short selling cause ive seen bubbles
form in game, are that last traded is 448k but current bids are 4k for
example."

## 1. The quote at the middle of the book

- **Today** `Exchange.price(c)` is the last trade, or fair value before the
  first (`Exchange.java`, THE PRICES). One print far above the book stays the
  quote for as long as nobody trades: his 448k last against 4k bids.
- **To build:** the quote is the midpoint of `bestBid(c)` and `bestAsk(c)`
  when both stand. With either side empty it is the last trade, and before
  the first trade it is fair value.
- **Who reads `price()`:** trading, the households, the world, the bank's
  strike, and `cityMark()` (the fund's mark). The recommendation: every
  reader of the quote takes the new one, and only what is about trades
  (`hasTraded`, the age of the last trade) keeps reading the book's last
  trade.
  - ★1, recommended: a quote with both sides standing is current, so
    STALE_MARK_MONTHS applies only when the quote falls back to an old
    last trade.
- **`Equity`'s own `lastPrice`** (the no-value fallback, `Equity.java` ~575
  and ~982) follows the same rule if it mirrors the exchange's last trade.
- ★2, recommended no: the companies' bonds (`BondMarket.lastPrice`) are left
  out, since he named shares. Put them in pending as a follow-up.
- **The screens:** wherever a share's price is shown, show the quote, and
  bid, ask and last beside it where there is room. These are checked by eye.

## 2. Short selling

- **Find the bubble first.** Look on main after EM1, whose value-anchored
  holders may already damp it. In a real run, count the months in which a
  share's last trade stands more than 10x over its best bid, and for how
  long, per company. If main still shows it, that run is the fixture. If not,
  a fixture causes it: one high print over a thin book.
- **Who shorts:** the holders EM1 trades for (households, the fund, the
  world) and the desk, which can already be short (it pays the dividend on
  what it is short, `Equity.java` ~741).
  - A holder whose expected return on a share is below the others' past
    EM1's band borrows shares, sells them, and buys them back (covers) as the
    price comes back to value, or when the loan is recalled.
- **The lending:** long holders lend from what they hold, for a fee. The
  general rate is about 0.3-1% a year; a hard-to-borrow share costs more.
  The short pays the lender every dividend, and the lent shares stay the
  lender's.
- **The limits are real rules, each a dial:**
  - The initial margin is 150% of the sale (Regulation T: the proceeds plus
    50%).
  - The maintenance margin is 30% (FINRA 4210 for shorts). Under it, the
    short covers: a squeeze.
  - The short interest is capped at a share of the float.
- **The register stays whole:** shares held plus shares lent out equal the
  shares outstanding plus the shares sold short. MoneyAudit balances the
  proceeds, the collateral, the fee and the dividend paid on.
- **Saved:** each holder's short position and the lending book, through
  `DataSave`, the load path and `SaveFileCheck` (`HistorySave` too if a
  series). These are new keys only, so an old save reads no shorts and needs
  no format bump.

## The harness

A `ShortSellingCheck`, modelled on `RestaurantsCheck`:
1. the quote is the midpoint; it is the last trade with one side empty, and
   fair value before the first trade;
2. the fixture bubble falls toward value within a stated number of months,
   with shorts and not without;
3. the register is whole and the money audit balances;
4. the lender is paid the dividend and the fee;
5. a margin call makes the short cover;
6. a save and a load in the middle of a short;
7. how often shorts fire in a real run.

The full suite runs before the ship file, and the trace moves, so the
ensemble runs too.

Money and saves: an Opus session with Sonnet slices.
