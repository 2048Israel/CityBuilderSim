# FW5 - two reads that miss litres, and one check that counts them twice ({next}, 2026-10-25)

The brief: `QUEUE.md`'s row, from `docs/notes/todo-archive.md`, FOUND ON THE
WAY IN 0.7.93 TO 0.7.98 ("Two reads that miss litres"; fixO12 §2, fixO14
★O14-4 and §7, which are not in the tree). Freight and rail have changed
since (FR-, FX-batches), so each fault was checked against today's code
first. All three were still there.

## 1. The railway's diesel was missing from the refiners' row

**Still there.** `Game.startOfMonthUpdate()` runs `chargeFreight()` and then
`economyManager.strikeSectors()`. The haul draws the railway's diesel
(`Freight.haul()`) and its trucks' (`Haulage`, through
`Freight.drawDiesel()`) with `Markets.draw()`, which books the refiners'
sale on their `Output.soldLocal`. Then the strike's `Sector.bank()` zeroes
every row. So the refiners' "sold at home" never held those litres. Bitumen
ordered before the strike (the auto-builder at the top of `nextMonth()`, a
hand order between the presses) was lost the same way. RefineryView had
worked around it by reading the railway's own records. RefineryViewCheck 4
wrote the fault in as its premise: `want = rowSold + railLocal`.

**Fixed.** `Markets.rowDrawsBeforeStrike()`, called right after
`strikeSectors()`, puts every draw made since the month last closed back on
its seller's `soldLocal`. It moves units only, because the struck statements
already carry the money. **The sellers' rows only, on purpose (★, below).**
The railway bills a month's trade at the top of the next from the buyers'
rows (`unitsBoughtLocal()`, `unitsImported()`, `Haulage.legTonnes()`), and the
saved month's trade is read off them too. A buyer's row refilled here would
be billed as freight nobody moved. No model code reads a seller's
`soldLocal` for a stockable good, only the screens and SectorFlow.

## 2. Every draw was missing from the market's fills

**Still there, and wider than the note said.** `GoodsMarket.startMonth()`,
which runs for each good in the clearing (`finalUpdateEconomy()`, the
month's last phase), cleared the trades. Every draw in a month comes before
it: the railway, trucks, cars, the sites' materials, the forecourts' petrol
(`Motoring.drawFuel()`) and the vans' diesel (`Sector.runFleet()`, step 0 of
the clearing itself). So `IndustryView.parties()` (the industry card's
buyers) listed none of them.

**Fixed.** `Markets.draw()` logs each fill on its `GoodsMarket`. The
clearing keeps the month's draws among the trades, and
`Markets.closeMonth()` forgets them. It is the very last statement of
`Game.nextMonth()`, so a draw after it (a hand order between the presses,
the auto-builder at the next month's top) counts as the next month's, on its
row and among its fills. Nothing is saved: after a load nothing of the
month counts until one runs (SectorFlow's rule). `GoodsMarket.reset()`
clears the log.

## 3. OilCheck 6's "L shipped" counted litres twice

**Still there.** `shipped += o.exportBound + o.exported;`.
`Sector.produceStock()` books the export-bound litres through `bookSale()`
as an export, so `exported` already holds them. **Fixed:**
`shipped += o.exported`. The figure now reads 149,821,560 L over the 24
months. A new assertion holds the reason: every product-month,
`exported >= exportBound` (96 export-bound product-months in the fixture).
This fault was in the harness's own sum, not the model, so that line is
green on main too.

## The harnesses (red on main, green here)

Sections 18 and 19 are measured on OilCheck 6's 24 months. They only read
values: the town is reused by sections 9 to 11. Both were run against
main's own classes, with the new OilCheck compiled beside them:

- **OilCheck 18, the refiners' tanks balance.** Every month, for each
  product: opening stock + made - written off - sold at home - shipped from
  the tanks = closing stock. This is ConservationCheck's bread law, applied
  to the refiners' tanks. Fixture: the railway or its trucks drew diesel off
  the tanks in 24 of 24 months. On main it fails on diesel alone, every
  month, by exactly that month's railway and trucks litres (worst 397,079 L,
  month 23). Petrol and every other product balanced. Here: 0.000 L.
- **OilCheck 19, every draw is a fill.** The refiners' fills of petrol and
  diesel are what their takers' own records drew. So the industry card's
  buyers sum to the row's sold at home. Fixture: the forecourts drew petrol
  off the tanks in 18 months and the vans drew diesel in 24, of 24. On main:
  402,210 L missed (diesel), and the card missed 18,081 L (petrol). Here:
  0.000 L.
- **RefineryViewCheck 4** now expects the row to hold the railway's and
  trucks' litres. With that expectation it is red on main (the row is short
  337k L) and green here.

## The trace, and the checks

The default `LongPlaytest`'s trace is byte-identical to main's except for
its run-time figures (51 s and 60 s, and the layout-watch line's seconds).
**The ensemble was skipped (EN1).** `./batch-check.sh --pick`: see the
report.

## ★ The call

- **The buyers' rows stay as the strike leaves them.** The railway's own
  card still shows none of its haul's diesel as bought at home, and
  RefineryView still splits the railway's and trucks' litres off their own
  records. Refilling the buyers' rows would make the railway bill freight on
  its own fuel next month and move the trace. *Recommended: keep it so.*
