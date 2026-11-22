# FC2 - construction traded with the world

Jerus, 2026-11-22 (desk chat), after PW5's 17:19 dry run, where a test town's
builders spent every point on upkeep: "the solution is actually quite simple,
and construction keeps coming back up. international construction is doing
its thing, so make it a imported/exported good, if currency is strong, it
could be cheaper than regular construction, so make it so that the base cost
is still in domestic currency but the excess is in foreign currency, and both
amouonts being converted to froeign, if you get my caclulation, that should
solve the domestic construction being priced out right? mathematically? while
still allowing overflow to be fixed at a higher price".

Its place, his words: "make it next after whatever is getting built, cause i
think its quite vital".

## What is true today

FC1's foreign contractor is only a rescue:

- It takes the orders only when the repairs leave the sites under
  `BuildersShort.FOREIGN_FLOOR` (5%) of the builders' month.
- Its price is `FOREIGN_PRICE` (2.0) x `buildingManager.basePricePerPoint()`
  (`Game.foreignCrewsOffer`), with no exchange rate in it.
- That price is set above the queue premium's ceiling, 1 + `CEILING` (0.8),
  so no city prefers it.
- Its month is `FOREIGN_CAPACITY_SHARE` (0.25) of the builders' month.
- `settleForeignCrews()` books it as the builders' import of services.
- Its harness is `ConstructionTrapCheck`.

## The rule

The contractor is always on offer. Its price for a point, in the city's
money, is **p + E x**:

- p is the city's own base price for a point (`basePricePerPoint`, with no
  premium);
- x is an excess in foreign currency;
- E is the city's money per foreign unit.

The whole of it, p/E + x in foreign money, is the builders' import of
services, booked as FC1 books it today. The current account line is FC1's.

## Why the city's builders are never priced out

An order goes abroad only when p (1 + premium) > p + E x, that is, when the
queue's premium is above E x / p.

- The premium is 0 up to `KNEE_MONTHS`. While the city's own builders have
  room they always win, whatever the currency.
- A strong currency only lowers the threshold, so the overflow goes abroad
  sooner. A weak one raises it.
- The overflow starts past the knee, so the backlog that orders new depots
  (`BusinessInvestment.BACKLOG_MONTHS_BEFORE_EXPANDING`) still stands. Say
  whether an order sent abroad still counts in the backlog that orders a
  depot, and why.

## For the builder to settle, saying why in the notes

Jerus: "whoever does it comes up with the numbers".

1. **x and its index.** Index it to the world's price level, so that domestic
   inflation does not shrink it to nothing. Set its value from evidence on
   foreign contractors' margins over local builders.
2. **The weak currency: settled by Jerus.** Jerus, 2026-11-22 (desk chat):
   "also, it should be such that at a certain premium they do go for it, we
   dont want a case where its available but the premium makes it so that they
   never go for it cause contstruction never reaches that premium if you get
   what i mean". So the threshold is always reachable:
   - E x, the foreign crews' excess in the city's money, is capped at
     p x premium(knee + N months).
   - N is a new dial in `BuildersShort`: the months of backlog past
     `KNEE_MONTHS` at which the overflow goes abroad whatever the currency,
     as a foreign firm trims its markup to win the work. Set it and say why.
   - The cap sits well below `CEILING` (0.8), not near it. The premium only
     approaches the ceiling, so a threshold close to it would need a backlog
     of years.
3. **The contractor's capacity.** Keep `FOREIGN_CAPACITY_SHARE`, or replace it
   with a price that rises with its volume.
4. **The export half Jerus names.** The city's builders sell their spare
   points abroad, symmetrically: the world's base plus the city's excess,
   converted. Sell only points under the knee, so that the city's own queue
   is never starved. The receipts are the builders' export of services,
   booked once.
5. **The screens.** The Construction screen and the build card show both
   offers, through `Kept`, each figure from a public getter.

## Bounds

- A harness causes each case through the model's inputs, on both sides of
  each edge:
  - under the knee, at a strong currency, no order goes abroad;
  - past the knee, an order goes abroad exactly when the premium passes
    E x / p, at two exchange rates;
  - at the weakest exchange rate the game can reach, an order goes abroad
    at knee + N months of backlog and not before; at a strong rate it goes
    sooner, but never before the knee;
  - domestic inflation leaves x's foreign value unchanged;
  - an export sells only spare points under the knee.
- Every import and export is paid by a real payer to a real payee, and booked
  once in the money audit, the national accounts and the current account.
  `ConstructionTrapCheck`'s assertions move only to the new truth, each with
  its reason; none is weakened.
- Money and saves move, so a stranger's money and saves review is owed.
- The trace moves, so the ensemble is owed.
- New state that lives across months goes through `DataSave` and the load
  rebuild. Say under the old-saves rule whether a format bump is owed, and
  bump from the shipped value after merging main.
- FC2 touches `BuildersShort` and `Game.foreignCrewsOffer`, as do PW5 (the
  substations' repairs) and OG1's queued follow-ups (RN3). If PW5 is still in
  flight when FC2 starts, FC2 merges PW5 when it ships. FC2 is not PW5's
  mend: PW5 mends its own test towns.
