# BB1 - the buyback card shows what the auction would book; offers capped at the call price

Queued 2026-10-31, one small screen batch for two of Jerus's calls:

- **CD6-C ★1**, his words: "show fair, but when click buyback it shows what
  the actual gain or loss would be cause of liquidity or whatever, with option
  to just buyback part of it or something idk."
- **CD4-C ★6**: "Cap offers at 100 (Recommended)".

## What it does

1. **The desk keeps fair.** Finances › The book's green or red stays at fair
   (CD6-notes §3, ★1), which the bank and the world are paid.
2. **Close it now shows what the auction would book.** Households and the
   businesses ask more than fair (CD4's ★1), so a line they hold can read
   green on the desk and book a loss at the auction (CD6's render: +D$5.1M at
   fair, -D$2.1M at the auction, households holding 86%). The buyback card
   leads with the gain or loss the reverse auction would book at the holders'
   asks (`Game.quoteBuyback()`, against carrying value), fair's beside it, and
   a line saying why they differ (each holder's own rule asks more than fair).
   Check first what is there: since CD4 the card's three comparisons hold the
   gain against carrying value and over fair, and since CD6 the line's page
   shows both; this makes the auction's figure the card's first.
3. **Part of a line.** CD4 already retires part (`Debt.retirePart()`; the
   ticket's face box, empty meaning everything on offer). Make it read as a
   choice, quick picks beside the box (say a quarter, a half, all on offer),
   the quote following the face; nothing new in the model.
4. **The cap.** A slice is priced at the line's non-call value (Buyback.java
   about 227), so on a line callable now households or the world can ask over
   100 while the city could call at 100. On such a line every offer is capped
   at the call price. A section in CD4's harness: a callable line past its
   first call with households asking over 100 stops at 100 at most; a line not
   yet callable prices as before.

Item 4 may move the trace if the test player buys back callable lines: the
ensemble then. The screen by renders, `docs/queue/BB1/`.
