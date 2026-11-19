# HL1-R - Business Services' kept share without a cliff (notes)

Builder: Claude (Sonnet 5.5), branch `batch/HL1-R` from `origin/main` 1041df81b (0.9.154), 2026-11-19. The brief is
`docs/queue/HL1-R.md`, item 1; item 2 (`ScaleCheck`'s month 165) went to FS2 with FS1 and is not here.

## What was found

HL1 (0.9.154) bought the city's upkeep of Business Services: the five services' upkeep and the bank's operating cost are its
other revenue the moment they are paid, and `BusinessServices.UPKEEP_IMPORTED = 0.75` of it is an imported service on its own
books, the whole of it where the sector has no seats (`sellUpkeepOn()`: `bill * (getSeats() > 0 ? UPKEEP_IMPORTED : 1)`).
The review's low: a cliff. With no seats all of the upkeep is imported; one seat keeps a quarter of the whole city's upkeep,
with no payroll or capacity behind it.

## What changed (`sectors/BusinessServices.java`, `BusinessServicesCheck`)

- `upkeepCapacity()`: what a month of its seats could bill at capacity, every kind's seats at `priceOfSeat()` (what the world
  pays for a seat-month through the exchange rate, the figure the screen already shows as "price x seats").
- `upkeepKept()`: what of the upkeep billed since the last strike it has kept, read off the pending ledger it is booked on:
  `upkeepSold` less the two imported lines. Both are saved (`SectorState`), so the month's share of capacity already served is
  no new saved field and no format bump.
- `sellUpkeepOn()` imports `max(bill * UPKEEP_IMPORTED, bill - room)` where `room = upkeepCapacity() - upkeepKept()`: the
  dial's quarter of the bill is kept, no more than the room the seats have left this month. No seats: room 0, all imported, as
  before. Capacity enough: `bill * UPKEEP_IMPORTED`, the same expression as before, so the default path is the same to the bit.
- The sentence of `UPKEEP_IMPORTED` now says it is the import share when the seats can serve all the rest. The banner comment
  and the class header say the rule.

The audit is untouched: the whole bill is still the seller's other revenue once and the buyer's spending once; only the
imported line (the world's share of the same dollars) is larger when the seats serve less. Nothing was added to the month.

## Calls taken (★ = a design choice)

- ★ **The reading: nameplate seats at the world's price.** "What the seats can deliver" is capacity (the brief says "at their
  capacity"), valued the way the screen values a seat: `getCapacity(g) * priceOfSeat(g)` summed over the three kinds. Chosen
  over **last month's delivered work** (`statement().revenue` less other revenue: staffed seats' output, payroll behind it)
  because that is zero in the first month a centre stands, lags a month, and swings the kept share with the labour market on
  top of the seat count; and over **seat count alone** (no price) because a strong currency that leaves the work no price
  should leave it no upkeep to serve (`priceOfSeat()` is 0 then and all of it is imported). If Jerus wants payroll behind the
  kept share too (`payrollShare()`'s own comment warns that unstaffed seats counted as earning flatter), it is one method:
  `upkeepCapacity()` reads the staffed seats' billing instead.
- ★ **The bank's bill and the services' bill share the seats.** They are two calls in one month (the services' in
  `finalUpdateEconomy()` first, the bank's after, `docs/month-order.md` 67 and 92) onto one pending ledger. Capacity per call
  would keep up to twice the seats; so the room is what the seats can serve less what the pending ledger has kept already. The
  services' bill therefore has first claim on the room, the bank's the rest: the bill that is larger, in play, gets it (the
  bank's is usually the smaller). Not a tie broken by loop order: the order is the month's.
- ★ **No second dial.** `UPKEEP_IMPORTED` stays the dial ("up to the dial's quarter"); its sentence is reworded. The capacity
  is the model's own figure (seats times the world's price); a multiplier of 1 on it would be a dial with nothing behind it.
- ★ **The harness is `BusinessServicesCheck` section 7**, not a new class: a new class is a line in `AllChecks`, whose pick is ALL
  and its dry run owed; the sector's own check borrows `LongPlaytest.MoneyFixtures.upkeepCity()` (HL1's fixture) for the played
  month. HL1's own section (MoneyFixtures section 12) and `GdpCheck`'s bank section stay as they were: their fixtures have
  seats to spare (600 seats against a bill of 32.46 a month; the quarter binds), so their "the dial's share" assertions hold,
  and would fail loudly, not pass, if a fixture ever drifted into the partial region.

## The checks

`BusinessServicesCheck` section 7 causes each side: a city with no seats (a bill sold to the sector directly: nothing kept,
all imported); one seat (the Contact Centre template made to hold one, one standing): a bill of a hundred seat-months keeps
one seat's worth, not the quarter, and a bill of one seat-month keeps the quarter; the bank's and services' bills together keep
no more than the seats can serve (the second's room is what the first left); seat by seat (1, 2, 5, 10, 40, 100 and a whole
centre) the kept share is `min(quarter, capacity)`, never falls, and no step adds more than the seats added can serve, from
nothing to one seat included; with seats enough the quarter and no more; and a played month (`upkeepCity`, three bank branches,
one seat) in which the whole bill is sold, the one seat's worth kept, the money audit and the households' identity close in the
paid month and the struck one. Bills are set from the model's own capacity and the dial, never a literal.

**With the cliff put back** (the old line restored, nothing else) nine assertions of the section FAIL: the one seat keeps
3.559 against 0.142 it can serve, the rest imported 10.677 against 14.094, the bank's room, the seat-by-seat scan, the
step from none to one seat, the played month's kept share (8.114 against 0.142) and its struck statement. The old line was
put back for the one run and reverted; the committed code is the new rule.

Picked by `sh tools/pick.sh origin/main HEAD` (2 files): `BusinessServicesCheck` OK; `GdpCheck` OK (the bank's books, the
accounts' imports and the identity, with and without an office); `LongPlaytest` OK (MoneyFixtures section 12: the audit, the
households' identity and the reload with the upkeep in transit, and the 2,400-month audit); `DistressBleedCheck` and
`PrizeCheck` exit 3, main's known reds only (`docs/notes/known-reds.txt`: FS9 and R3b-R; no new line); `TimingCheck` OK.
`./batch-check.sh --fast` OK. No stranger's review is owed (the review line: no money, saves or threading in the diff; the
audit's lines are untouched).

## The default trace

`LongPlaytest` with `-Dcbs.parallel=1 -Dplaytest.trace=...` on this branch against main's fresh trace
(`cbs-trace-main-6b0aa202`, made on 6b0aa202e): all eight files (`month pop house bank bonds build labour borrow`) are
**byte-identical**, 2,405 lines each (37 for `borrow`). The played city has 130,200 seats by month 2,400 (434 centres) and the
quarter binds from the first centre in it, so the capacity never caps the dial there: the partial region is not entered in
2,400 months. No ensemble is owed (contract 4: an unchanged trace is said in the notes).

## What moves for the player

A city whose Business Services seats serve less than a quarter of its upkeep keeps less of it and imports more: GDP falls by
the added import (the five services' upkeep is in G), the sector's other revenue is the same whole bill. In the test city a
Contact Centre (300 seats) serves 42.7 a month at the world's price, a town of 400 homes pays 32.5 of upkeep: the quarter is
8.1, covered by a third of one centre; a city whose upkeep is more than about five such towns' per centre keeps less than the
quarter, and one with a strong currency (a seat's price falls) keeps less sooner. Nothing is spent differently; the books of
the sector read a smaller margin on upkeep work. No eye check.

## What is left

Nothing of this brief. The brief's item 2 (`ScaleCheck`'s month 165) is FS2's. No dry run was owed (the pick is not ALL) and none
was run, so there is no kept dry-run log. If Jerus wants the seats' payroll behind the kept share, the first ★ above names the
one method.
