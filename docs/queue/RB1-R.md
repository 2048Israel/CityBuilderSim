# RB1-R - RB1's review follow-ups

RB1 (0.9.93, the fund holds to its 10%) shipped on a stranger's review that said SHIP (Opus, xhigh, 2026-11-11). Its
follow-ups, in order of weight. A small model batch: Sonnet whole is enough for 1, 2, 4 and 5; 3 needs a run.

1. **The idle cash is read before last month's toRaise is paid** (should fix). `Game.fundIdleCash()` (Game.java
   ~5511, on RB1) reads `fund.getCash()` before `TreasuryFund.payTransfer()` (TreasuryFund.java ~625-633) pays last
   month's toRaise out of that same cash. With the dial past the default and under 17 steps, that cash goes out as
   `late` and is charged again at 1/24 as idle, so the due is overstated: what the cash cannot cover becomes next
   month's toRaise and the fund sells more. No money is made (paid = min(due, cash)). `PolicyPreview` (~566) takes
   toRaise off the cash for `fromCash` but not off the idle. Fix: idle on cash minus max(0, toRaise), in the model
   and the preview alike. Done means a FundCheck section with the dial past the default and a toRaise carried in,
   the idle part asserted against cash less toRaise, and the preview equal to the month.
2. **The idle figures count the due, not what was paid** (low). `transferFromIdle` and `transfersFromIdle`
   (TreasuryFund.java ~636-637) add the due struck on idle cash; LongPlaytest (~6035) prints that as "paid" and as a
   share of transfers paid. Equal at the default; past it they can differ (1). Count what was paid, or say "due".
3. **No run in the tree counts the mechanics firing** (should fix). LongPlaytest's cap line is 0 by construction:
   `GovernmentPlayer` runs the fund at the most of the dial, so it never holds a share; only the probe city outside
   the tree counted `postFundOverLimit()` and the idle transfer. Add a real run at the default dial (a LongPlaytest
   section or a FundCheck long city) that counts the months the cap sale and the idle transfer fire (> 0) and asserts
   no company's holding past L s/(s - b) at the fastest buyback the pace allows (the README: count how often a
   mechanic fires in a real run).
4. **Stale prose** (low): `Game.java` ~19254, `settleTreasury`'s comment says idle cash is read "under its caps and
   its mix"; ★b dropped the mix.
5. **Wrong words, right formula** (low): `LongPlaytest.java` ~7130 says the fastest buyback is "filled at the
   desk's ask"; dividing by (1 - SPREAD/2) is a fill at the desk's bid (the emigrants' price, the cheapest).
