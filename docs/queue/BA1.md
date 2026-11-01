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

1. **The landlords' rule for every sector.** A sector may add orders while
   the points its own sites owe stay inside a number of months of the
   builders' site output, each order sized to stay inside it, so a growing
   sector does not wait for one plant to finish before the next is begun.
2. **A longer horizon where the build is long.** A sector plans its demand
   growth over its lead time plus a horizon that grows with how long its
   buildings take, so a works that takes years is ordered years ahead of the
   demand it meets; the demand forecast and its trend window say how far they
   can be trusted, and the horizon stops there.
3. **The screens:** each sector's planner line says how many orders it holds
   and how far ahead it is building; the advisor reads the same.

## Calls (each recommended; settled that way unless Jerus says otherwise)

- ★ One cap for all sectors, the landlords' months of the builders' work, with
  each sector's share of it (so the landlords' 12 months do not become every
  sector's 12 months at once): the builders' queue stays bounded.
- ★ FC1 first: with the scarcity price a far-ahead order pays for the strain
  it puts on the builders, so the market rations itself.
- ★ `MAX_CONCURRENT_ORDERS` goes, replaced by the months rule; the refiners'
  crude units keep their own count.

## The checks

A fixture whose sector's demand grows faster than one order at a time can
meet: it holds two orders on site, the second begun before the first is done,
and its capacity keeps up. A fixture where every sector wants to build: the
builders' queue stays inside the cap. The 16-seed ensemble before and after
(the young city's growth, and the overbuild busts ST2 saw after a fast boom:
count them).
