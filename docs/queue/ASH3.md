# ASH3 - four small mends from Ashbourne

Jerus, desk chat, 2026-11-27, from his Ashbourne findings (see ASH1's head for
his words). The findings are in `docs/queue/ASH-findings.md`; this batch takes
13, 15, 10 and 6. Ashbourne is his save: copies on scratch only, and each
condition caused on a fixture of the batch's own.

## 1. The buyback shows the bank's loss (finding 13)

- **What happens now.**
  - The bank carries city paper at amortised cost (`Bank.sellPaperBack`,
    0.7.1).
  - A buyback near fair books the whole discount on the bank's capital at
    once, unannounced.
  - At m211: about Cr 15.5M carried, fair 65-85, and about Cr 4.9M unbooked,
    near the bank's whole equity.
- **What changes.**
  - The buyback card shows the commercial bank's loss on the slices it would
    take, and its capital ratio after.
  - The holdings dial does the same, since its trades with the bank book the
    same loss.
- **What stays.** Held-to-maturity accounting stays: it is realistic (SVB
  2023). This is the warning, not a new accounting.

## 2. A licence wait counts as demand for its school (finding 15)

- **What happens now.**
  - Business Services said "Holding: 39 more engineering licences" every month
    from m193 to m217.
  - `BuildAdvice.school()` and `schoolNeed()` count only posts, so the
    Institute of Technology quoted a need of 0.0.
- **What changes.** The wait counts as demand for the school that grants the
  licence. If that cannot be made exact, the need list names the sector
  waiting on the school instead.

## 3. The rollover nets maturities against the operating balance (finding 10)

- **What happens now.**
  - The rollover leaves city capital spending in. `cityCapitalSpending` is in
    the save.
  - The Cr 22.1M prepaid substation made the year's surplus about -Cr 22M.
  - With an operating surplus of Cr 170-360k a month, the rollover still
    re-borrowed Cr 448k as a new 5-year serial at 5.256%. It would also have
    rolled the Cr 1.2M floater at about 12.1%.
- **What changes.** The rollover leaves capital spending out of the balance it
  nets against. This keeps Jerus's "net of last year's surplus" and reads the
  surplus as governments do.

## 4. Retail weighs every format it could staff (finding 6)

- **What happens now.**
  - At m195-196, hunger went from 0 to 5.7% to 3.9%. Baskets asked were 6,031
    and baskets handed 5,724, while `hungerPricedOut` stayed 0 and
    `shelfPrice` equalled `shelfFloor` throughout.
  - Retail held back a Small Grocery Store it could staff at only 61% (2
    college posts at an 11% fill). It never looked at the Convenience Store,
    which has no college post.
  - Three city-built Convenience Stores ended the hunger within two months.
- **What changes.**
  - Retail weighs every format it could staff.
  - A short shelf raises the price above its floor before anyone goes hungry.
  - Check reality for how fast a real grocer reprices a shortage.

## The checks

- Each mend has one section, with a fixture that causes the condition.
- The money audit is exact to the cent.

## Order

- **When it starts.** After CBK3, which changes the bank's books and the
  window; the buyback's loss is read on CBK3's bank.
- **The manual is not this batch's.** An edition comes only at a milestone or
  at Jerus's request (docs/orchestrator.md). The notes carry a "For the
  manual" section, and `docs/manual.*` stays untouched.

Versioned. Default behaviour moves on purpose (the rollover, Retail's choice,
the price, the school advice), and money moves: an 80-run ensemble against the
newest baseline, its verdict in the notes, and a stranger's review at
"mends at".
