# RS1 - the city can sell the bank it rescued

Queued 2026-11-20 on Jerus's word ("Yes, queue it", 23:2x, by his desk chat). What he saw, in his own
city (0.9.172, m1207-1208, 434-438k people; probably slot 10's city, which the slot-10 debug session
found at m1204): the city's fund owns 100% of the Bank, all of it in the rescue book. He sold by hand
(Finances > The city's fund > Bank), asking -99% on fair. Each month only 1/120 of the shares filled,
the shares in issue fell by exactly the fill, and the fund stayed at 100%. His words: "trying to sell
yet basically it just decreases total shares, and also, city fund still owns 100%, perhaps its cause
its the rescue book? idk worth noting tho". Screenshots: `C:\Users\Jerus\AppData\Local\Temp\cbs-kept-bank-sale\`
(4 png).

## Read from the code (Exchange.java), not run

0. **Working as written.** The only fill is the bank's own desk buyback, `deskCapacity()` for
   `Equity.BANK`, at fair x (1 - SPREAD/2) (20.99 x .99 = 20.78 and 19.29 x .99 = 19.10, his fill
   prices), capped at BUYBACK_PACE/12 of the shares (3.2B/120 = 26,666,666.67 and
   3,173,333,333.33/120 = 26,444,444.44, his fills exactly), and the bought shares cancelled. So the
   city can never really sell its bank to the market.
1. **A bid that never trades.** The book shows a best bid kr21.20 (fair + 1%, probably the world's:
   `postWorld` caps at fair x (1 + SPREAD/2)) for exactly 10% of the shares (317,333,333.33;
   320,000,000 the month before), resting above his ask kr0.1929 all month. Only the desk's lower bid
   fills. Find who posts it and why it never fills against a crossing ask (barred from the bank or the
   rescue book, or withdrawn at the step), then mend it or stop showing it.
2. **The quote from a crossed book.** PRICE kr10.70 is the midpoint of 21.20 and 0.1929, so his own
   -99% ask halves the bank's quoted price and its market value (kr33.7B against about kr66B at fair).
3. **The order card shows "Last kr0.000259"** (fair kr14.14): a dust trade setting the last price,
   maybe DK1's class; check it against DK1.

## ★ Answered by Jerus

May the world and the households buy the rescue book's shares, as a government sells a rescued bank in
share offerings (RBS, Citi), rather than only the bank's own buyback at a tenth a year? Jerus,
2026-11-20 23:32 (by phone): "Yes they may rs1". So the world and the households may buy them; the
builder designs how (an offering, the book's ask open to them, or both) and says why in the notes.

## Done

Each of 0-3 caused in a harness through the model's inputs (a fund holding the bank in the rescue
book, an ask under the resting bid, the quote read from the book): the bid fills or is not shown, the
quote is not the crossed midpoint, the last price is not a dust trade's, and, by the ★, the city can
sell the rescued bank to whoever may buy it. Money moves (the fund's cash, the bank's shares), so a
review; the ensemble if the default trace moves.

- **Files it expects to change:** `Exchange.java` (the book, the quote, the rescue book's sale), the
  fund's sale path, a harness (ExchangeCheck or a new one).
- **Not beside:** RB3 (`Exchange.postFundOverLimit`) or DK1 (`deskLimits`): the same file.
