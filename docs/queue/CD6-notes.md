# CD6 - the rest of the debt's screens: notes

*0.8.36, 2026-10-23. The batch is `docs/studies/spec-city-debt.md` §8's CD6 row and the §7 it points to (boards 1, 2
and 5: the book as a desk, a line's page, Your rate), the manual (D31), CD3's ★4 (Jerus, 2026-10-23: "★4 to CD6"), the
screen notes CD4-C and CD5-C handed on, and Jerus's request of the same evening: "for the book, aka the gain/loss of
bonds and all, have it so that it shows green or red, and a % value and dollar value, cause right now it hard to
determine, like make it juicy to buyback". Built on main at 894648b5 (0.8.35), merged with 2bf138f1.*

## 1. What it does

- **Finances › The book, as a desk** (board 1). Six figures: owed at face, carried, worth today, if closed now, the
  coupon a month in cash, and what a point on the curve moves. WHERE EACH LINE SITS ON THE CURVE: today's zero curve
  with a dot a line at its months left and its yield (a floater and a linker off the chart and said so, dollar paper
  hollow). AT FACE, CARRIED, AND WORTH TODAY: three bars by kind. EVERY LINE: a row a line by when it falls due - its
  name and sale, **if closed now** (carried less worth, in D$ and as a share of carried, green a gain and red a loss,
  with *Close it now* beside it), face, coupon, issue yield, price, yield, worth, carried, vs face, duration, holders -
  and the book's foot row. A click on a row opens its page. The figures are `BookDesk`'s, struck once by
  `Game.bookDesk()`. The old piece cards and the fold's table went.
- **A line's page** (board 2): its five figures and the paper's own lines (a floater's dial and margin, a linker's
  index, the books, the call); ITS PRICE SINCE IT WAS SOLD against what it is carried at, per 100 (each piece records
  its price and carried per 100 at every month's close, `Debt.recordPath()`, by reads alone); IF YOU CLOSED IT TODAY,
  at fair and at the reverse auction, each green or red with its per cent, against face, and the honest line, with
  *Close it now…* and *Call it…*; THE NEXT SIX MONTHS ON THE BOOKS (`Debt.booksAhead()`, `amortise()` projected on
  each kind's coupon schedule); WHO HOLDS IT; ITS SALES (each sale into the line, recorded at the sale), its duration
  and the value of a point (`DebtManager.valueOfAPoint()`, `durationOf()`). NEXT DUE's rows open it.
- **Your rate, as the curve** (board 5): the city's curve today, a year ago and two years ago (the curve book, 25
  months of zero rates at ten tenors, recorded at the close and saved), the world's and the dial the market expects;
  what the market expects the dial to do (today, the rule at today's inflation `ruleNow()`, a year, five years, where
  it settles `settledDial()`); the 10-year rate taken apart; real rates and the inflation priced from the linked bonds
  (`fairRealCoupon()`, `breakevenInflation()`, `parYield()`), each linked line a dot. The short end's two cards follow.
- **CD3's ★4.** The hub's COUPON, the Debt service gauge and the book say the coupon is cash, next month, and put the
  budget's interest beside it (★2 below). The Government tab's debt grid read each live piece's last expense, so it
  fell short of the budget's line in a month a piece matured and moved with the D$ on dollar paper; it now reads
  `DebtManager.getInterestBookedOf()`, struck by kind beside the budget's line and saved (`DataSave.interestBookedByKind`),
  with a foot row that is the line itself.
- **CD4-C's and CD5-C's screen notes.** The journal's premium or discount (`Buyback.Result.unamortisedTaken()`), the
  honest line (`Game.honestLine()`, `Buyback.HonestLine`) and the call's gain (`Game.callGain()`) are the model's; the
  Government card reads `getAllPrincipal()`; a linker's last month no longer counts its uplift as "coupon a month"
  (`Debt.getMonthlyCoupon()` and `getUpliftDue()`; `getMonthlyInterestExpense()` stays the cash the coupon path pays);
  a linker's effective rate says "real".
- **Green or red** (Jerus, above). The 0.7.32 spec's B6 kept a gain uncoloured; his newer word wins for the book, a
  line's page, Close it now's comparison and the call card (`BookDesk.Line.ifClosedNowShare()`,
  `BookDesk.ifClosedNowShare()`, `Buyback.Result.gainOnCarryingShare()`).
- **The manual**: `docs/queue/CD6-manual.md`, §13 rewritten for CD1-CD6 and the edits elsewhere, each part marked by
  where it goes. Nothing was published.

## 2. The month and the save

Nothing the month computes moved. The recorder (`DebtManager.recordTheBook()`, from `Game.recordMonth()`) reads only
pure getters - never `fairValueOf()` or anything that strikes the market - and DeskCheck's twin cities, one recording and
one not, are alike to the bit for 26 months. The three new histories ride the save: the interest by kind and the curve
book as DataSave fields (ReadPathCheck and SaveFileCheck see both), each piece's path and sales with the piece through
Gson. No format bump: an older save reads them as empty.

## 3. ★ calls for Jerus

1. **★1 The book's green or red is at fair.** The bank offers at fair (D34) and the world sells dollar paper at fair
   (D26), so on their paper the desk's figure is what a buyback books, to the cent (DeskCheck 8). The households and
   the businesses ask more (CD4's ★1), so a line they hold can read green on the desk and book a loss at the auction:
   in the render the ten-year line, 86% household-held, reads +D$5.1M (+33.9%) at fair and −D$2.1M (−13.8%) at the
   auction. Its page shows both. Show the auction's own figure on the desk instead, or beside it?
2. **★2 The hub and Debt service stay cash.** The gauge asks whether the take can carry what the paper asks, which is
   cash, so the coupon stays the cash next month, labelled, and the budget's interest is beside it rather than in it.
3. **★3 Duration on a callable and a floater.** A callable line's duration and value of a point are on its schedule
   as a bond that cannot be called (an upper bound); a floater's run to its next reset, about a month.
4. **★4 Your rate's world line** is the board's ore, close to the dial's amber; only its dash tells them apart.

## 4. The checks, the trace and the ensemble

**DeskCheck** (new, 280 assertions, about 5 s): 1 a linker's last month, the coupon and the uplift apart; 2 the interest
by kind is the budget's line in the month a bill matured and a month the D$ moved, and through a save; 3 a buyback at a
gain, its journal and honest line against what it then does, and the call's gain against what the call books; 4 the
desk is the ledger's figures, the value of a point what a curve a point higher takes off; 5 a line's next six months
are the months it then books; 6 a line's price path and sales through a reopening, a part bought back, a change of unit
and a save; 7 the curve's past, the rule and the settled dial, the par yield and the ten-year parts, and the trace
guard; 8 a bank-held line at a gain and one at a loss, and a dollar line, each bought back whole for exactly the desk's
figure. SaveFileCheck and ReadPathCheck cover the new fields and getters.

**The trace**: the default playtest on each slice's model, and on the final one, is byte-identical to main's but for
its run-time lines, so the ensemble was not run (EN1). The recorder first cost the run 8 s of 63 (the world's short
rate struck ten times a month); striking it once brought it back.

**The screens** were checked by eye in headless renders (`docs/queue/CD6/`, `RenderCD6.java`): the book at 1,200 and
900, the page of a reopened ten-year line, a linker and a callable line, Your rate in a played city at both widths and
in a young one. The render city is FloatLinkCheck's, whose curve ends near 13%; a calmer one needs a fixture of its own.
The clicks (a row to its page, Close it now from a row and back, NEXT DUE) were read, not driven.

## 5. Who wrote what

Sonnet 5.5 at xhigh wrote the six slices: the getters and ★4 (A), the model the screens draw (B), the book and a line's
page (C), Your rate (D), the manual's text (M) and the green or red (E). Claude (Opus 5.5) wrote the briefs, reviewed
each diff and render, struck the world's rate once in the recorder, kept a kind whose last piece fell due on the
Government lists, gave the line's page its own strike (`Game.bookLine()`), coloured Close it now's and the call's gain,
added DeskCheck 7's last two assertions, ran the traces and wrote the record.

## 6. The final checks

`./batch-check.sh` on the final model merged with main (2bf138f1), beside another batch's JVM and Jerus's game, timing
soft: **137 of 137 passed** in 1,685 s (main's baseline was 136 of 136; seven timing assertions said SOFT and were not
counted: EarthTerrainCheck 1, MapCheck 4, KeptCheck 2). Stale reports 0 firm findings; the maps are regenerated and
committed. The default playtest on that model is byte-identical to main's but for its two run-time lines. `--timing`
was left for the ship gate, alone and hard.
