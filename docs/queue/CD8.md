# CD8

**The city borrows in any of the eight moneys.** Jerus, 2026-10-23 (see
CD7.md for his words): queued right after XR4, which gives the eight central
banks their policy rates.

## What is missing

The city can borrow only in D$ and US$. WN4 gives the world eight moneys and
XR4 their interest rates; the city still could not sell a bill or a bond in
euros, yen or the rest.

## What done looks like

1. **Paper in each of the eight**: bills and bonds only, as dollar paper has
   them (§2.7: no floaters or linkers abroad), sold to the world, each priced
   on its own money's curve built from XR4's policy rate the way
   `foreignCurveRate()` builds the US$'s.
2. **Its books** as dollar paper's since CD3 (kept in its own money, read in
   D$ at the month's rate), its buyback as CD4's reverse auction, its
   revaluation as CD7's exchange line.
3. **Borrow** gets the choice of money, with each money's fair yield beside
   it and the risk said plainly (a falling D$ raises what the debt costs).
4. **The money**: every coupon and principal converted at the month's rate
   through the same paths, closing in MoneyAudit; the vault's reserves and
   the world's holdings consistent with WN4's reserve mix.
5. **Harness** sections that sell a piece in each of two moneys, run them
   through a rate move and a buyback, over a save and load.
6. Nothing sells it unasked, so the default playtest's trace should stay
   byte-identical.

## Before it

WN4, CD7 and XR4.
