# SG1 - the student grant pays from the cash surplus

Queued 2026-10-31, settling CD7-G on Jerus's word: "Cash only (Recommended)".

## The fault

The student grant on its surplus-share basis pays `amount * max(0,
treasurySurplus)` (Game.java about 14103/14127, TaxPolicy.java about 696).
`treasurySurplus` is `getBalance()` (Game about 16607), which holds CD7's
exchange gain on the city's US$ debt and CD3's gain retiring debt below its
book. Neither is cash: the treasury bridge takes both out again. So when the
D$ strengthens against a large dollar debt, the paper gain is paid out the
next month as cash grants; a loss never takes the grant below zero. (Found by
the stranger's review at CD7's gate, 2026-10-31.)

## To do

- The grant reads the cash surplus: the surplus less the exchange gain and
  the retired gain, the same non-cash lines the bridge removes. Read them
  through the getters the bridge uses, so a third non-cash line added later
  is not missed: if the bridge sums them in one place, use that sum.
- A harness section whose fixtures cause it: a city with dollar debt, the D$
  pinned 10% stronger (DebtExchangeCheck's way), and the grant equal to its
  value with no move; the same for a buyback below book. The premise holds:
  the grant is a share of the surplus the city earned in cash.
- Small: Sonnet whole, `--pick`, the gate's suite.
