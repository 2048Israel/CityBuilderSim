# BA1 - Every business may build ahead: more than one order on site, planned years out

Queued 2026-11-01 from Jerus: "i noticed can only build one thing at a time?
right? what about letting the market build more than that, even years ahead?
yes after 0.9.0". It runs after FC1, whose scarcity price on a point is what
keeps one sector from filling the builders' queue for years.

## What the code does now

- Every sector but the landlords may have one order on site at a time
  (`BusinessInvestment.MAX_CONCURRENT_ORDERS` = 1, "already building"), and
  plans `PLANNING_HORIZON` (6) months of demand growth past the lead time.
- The landlords hold work by the month since 0.7.17 (Jerus: "Landlords may
  hold orders worth a number of months of building work, instead of one order
  at a time"): they keep ordering while the points their sites owe are at most
  `MAX_ORDER_MONTHS` (12) of the builders' site output after the repairs
  (`withinMonthsOfWork()`). One order at a time had held their next order
  "already building" for fifty months in his year-149 city.

## What to build

1. **No cap: the price rations the queue** (Jerus, 2026-11-01: "why bounded?
   why not let contracts flow? market after is efficient, too much and
   contracts get expensive thus business stop trying to place orders until
   contracts arent so expensive"). `MAX_CONCURRENT_ORDERS` and the landlords'
   `MAX_ORDER_MONTHS` both go: a sector orders whenever an order clears its
   profit test at the price of a point it would pay, FC1's scarcity price, which
   rises with ALL the work queued. So a growing sector does not wait for one
   plant to finish before the next is begun, and a flood of orders makes the
   next one dearer until the orders stop.
   - Each order is priced on the queue as it stands with the orders before it
     that month (in the planners' order), never at the month-old price, so a
     month's orders cannot all take the cheap price before it moves.
   - The profit test counts the money tied up: an order pays up front for a
     building delivered when the queue reaches it, so the interest over that
     wait is part of its cost, and an order years out must earn it.
2. **A longer horizon where the build is long.** A sector plans its demand
   growth over its lead time plus a horizon that grows with how long its
   buildings take, so a works that takes years is ordered years ahead of the
   demand it meets; the demand forecast and its trend window say how far they
   can be trusted, and the horizon stops there.
3. **The screens:** each sector's planner line says how many orders it holds
   and how far ahead it is building; the advisor reads the same.

## Calls (each recommended; settled that way unless Jerus says otherwise)

- Jerus's: no cap; the price and the interest on the wait ration the queue.
- ★ FC1 first: its scarcity price reads the whole queue (FC1, call 1).
- ★ `MAX_CONCURRENT_ORDERS` and `MAX_ORDER_MONTHS` go; the refiners' crude
  units keep their own count (a unit count, not a cap on orders).

## The checks

A fixture whose sector's demand grows faster than one order at a time can
meet: it holds two orders on site, the second begun before the first is done,
and its capacity keeps up. A fixture where every sector wants to build: the
price of a point rises month by month until the orders stop, and the queue
settles where the last order just clears its test (count the months it takes,
and that no single month's orders all took the old price). The 16-seed ensemble before and after
(the young city's growth, and the overbuild busts ST2 saw after a fast boom:
count them).
