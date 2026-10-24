# CD7

**The city's foreign debt revalued.** Jerus, 2026-10-23, asked whether a
batch handles foreign-currency debt; told of the two gaps, he answered "both
as you said, but queud right with the WN batches, you choose where". CD7 goes
right after WN4, whose revaluation of the vault in eight moneys it builds on;
CD8 (borrowing in the eight) goes after XR4.

## What is missing

The city borrows in US$ ("dollar paper", `docs/studies/spec-city-debt.md`
§2.7, D26). Since CD3 a dollar line keeps its books in US$ and is read in D$
at the month's actual rate (`debt.exchangeRate`, `foreign.getRate()`). When
the D$ moves, the D$ value of that debt moves with it, and nothing books the
change: CD3's review found that the FX move on a carrying value "is booked
nowhere", feeding only a display (`getNetPositionAtCarrying`).

## What done looks like

1. **The exchange gain or loss** on each foreign-currency line, month by
   month: its carrying value in its own money times the change in the rate,
   as a not-cash line in the budget (an "Exchange loss on foreign debt", or
   gain) and a step in the treasury bridge, so the bridge still foots. The
   same rule WN4 uses to revalue the vault, so the two read alike.
2. **What it does not move:** no cash. Coupons and principal stay paid at the
   month's rate as today. Say what the surplus that the rollover nets and the
   fund pays in from (CD3-C) does with a not-cash exchange line.
3. **The book shows it** beside the line's gain or loss on retiring (CD6's
   green and red, Jerus's "make it juicy"), in D$ and as a %.
4. **Harness:** a fixture with dollar paper outstanding and the D$ falling
   10%, then rising: the exchange loss and gain equal the carrying value
   times the move, the bridge foots, MoneyAudit is untouched, over a save and
   load.
5. If the default playtest holds no foreign paper the trace stays
   byte-identical; otherwise run the ensemble.

## Before it

WN4 (the eight moneys and revaluation) and CD6 (the book's gain and loss).
