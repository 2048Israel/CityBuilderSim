# KC1 - KeptCheck's fixtures cause their conditions

Queued 2026-11-08 by BK2 ([notes](BK2-notes.md), 5 and ★5). A small check batch; no model change.

## What BK2 found

KeptCheck's bench is `LongPlaytest.probeCity` played `CITY_MONTHS` (600), and many of its sections read whatever that city
happens to hold then. BK2 moved the trace (each holder's own discount); the bench city parts from main's between m205 and m305
and at m600 holds one piece of paper where main's holds none. KeptCheck then went red on 11 lines that read the city's state,
not the screens' code (BK2 touches no screen), and is green on main:

- 66: "the city has a piece of paper to close" and 66's "a bookRow was found on a money page of this city" - `longestPiece()`
  finds none by then;
- 65: "no month in forty-eight paid a holding and moved the fund's worth" - the fund's count-up has nothing to count;
- a sector page's redraw handing back 89.3% of its nodes against nine in ten ("Retail, Cash & debt as a statement");
  each month's redraw 238 of 302; note 1's page; "no month makes more than a tenth of the nodes" (41 of 335);
- 89: one Household money tooltip on a node kept without one;
- the kept header's signature against a header built new (18 rungs differ: a hidden label's layout, 11,44 kept against 0,0 new).

## What KC1 does

1. **Each section that needs a thing makes it** (README, *The checks*: a fixture must cause the condition): 66 borrows a piece
   (its own `financesBorrow` hand, already in the section) when the city has none; 65 gives the fund a holding that pays in the
   window; the node-ratio sections pick their page by what it holds, or say what premise they read.
2. **The two that may be the screens'** (the tooltip lost from a kept node; the hidden label's layout in the kept header):
   reproduce on BK2's bench city (its m600 save) and mend in the screen if they are real, or show they are the fixture's.
3. KeptCheck green on main and on a moved trace: play the bench a few seeds or months either side and show the sections hold.

Scope: `KeptCheck.java`, `ui/KeptBench.java`, and a screen only for item 2.
