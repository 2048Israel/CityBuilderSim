# ET3 - A price for each class of ground, and the land line by class (0.8.44, 2026-10-25)

The brief: `QUEUE.md`'s ET3 row and `docs/studies/spec-earth2-terrain.md` §10's ET3 row, with §5.1-5.3;
Jerus's decisions of 2026-10-12 ("all rec": D8). Three slices, each written by a Sonnet subagent and reviewed
and committed here: A the prices, B the books, C the screens and the renders.

## The prices (slice A, §5.1, §5.2)

- `LandMarket.reliefPrice(relief, going, base, prep)`, one rule for every price: FLAT the going price; FOOTHILL
  `min(going, max(base, going - prep))`; STEEP and VERY_STEEP `min(going, base)`. No class is dearer than flat;
  where the going price is under the base (scarcity's floor), every class is the going price.
- The office (US$): an offer's dry ground by relief at those prices, base `BASE_PRICE_PER_SQ_FT` at the world's
  price level, the preparation `SitePreparation.USD_PER_M2` ($110.2 a m²) at that level (`prepUsdPerSqFt()`);
  fresh water and sea at their shares of the flat price; ice 0; ore as before. Getters `getFlatUsdPerSqFt()`,
  `getFoothillUsdPerSqFt()`, `getSteepUsdPerSqFt()`.
- What a business pays (local): `getPricePerSqFt()` stays flat's; `getFoothillPricePerSqFt()`, `getSteepPricePerSqFt()`
  on the local base, the preparation the quote's own figure a sq ft (`Game.preparationCost(preparationPoints(1))`,
  wired by `setPreparationPerSqFt()`), so the price and the quote read one number. A plot is priced class by class
  where `allocate()` would put it (`plotByRelief()`, the split after less before; a buyback by the reverse,
  `releasedByRelief()`), the zone's share on every class. `priceFor(sqFt, sector, works)` and
  `buybackPriceFor(..., works)` take `Game.standsAnywhere(t)` at every caller with a template.
- The pickers rank by buildable ground (flat and foothill) a dollar, the foothill's preparation added to the price
  (`buildableKm2PerUsd()`, `priceWithPrepUsd()`): `bestValue()`, `Game.bestOffer()` (room and shortfall) and
  `AutoBuilder.shortcutOffer()`. With no buildable ground in any offer, the old dry rule. The deposit pickers and
  `cheapest()` are unchanged.
- **A flat city is priced by the old expressions, to the bit**: every new path is behind an all-flat test (the
  offer's relief, `LandManager.flatOnly()`), as LandClassCheck 5 proves on a flat fixture.

## The books (slice B, §5.3)

- Nothing records which sector's plot lies on which class, so a sector's land is attributed **pro rata** to the
  split: its buildings' ground at the city's buildings' mix (flat, foothill), its works' at the works' mix (steep,
  foothill, flat) (`EconomyManager.landByReliefOf()`, `LandManager.reliefUse()`). Summed over every sector, the
  city's categories and the ground no sector holds (the founding's bank branches), each class is the split's own.
- `landValueOf()` (sectors and categories), `assessedLandValueOf()` (the zone's share on every class) and
  `groundPriceFor(template)` (the investors' read, now `priceFor()` over the plot: the number `buildFor()` charges)
  by class. Game pushes the class prices with the land's price each month and on a load (`pushLandByRelief()`) and
  the use again at the close (`pushLandUse()`), before the sheets.
- `SectorBooks.SectorMonth.landByRelief`: {flat, foothill, slope sq ft; their three prices}, saved with the books;
  null for a flat city and in an older save, which reads the one price. No format bump: nothing loads wrongly.
- `SectorStatements`: the Land line carries **note 11** (`LAND_NOTE`, `landNote()`: a row a class with its km², its
  price a m² and its value, adding to the line); the equity's land row is the sum over classes of last month's area
  times the price change ("at today's prices"); the old one-price figure and words in a flat city.

## The screens and the renders (slice C)

- `LandClassView` (model, no JavaFX): `officeLines()`, `offerLine()`, `landNoteLines()`; it holds a copy of
  `ui.Money`'s money words, as the model may not import `ui`. The land office draws the office's lines under ET2's
  relief lines; an offer's size words carry its class line (the row's tooltip and the map card's single offer; a
  card of several summed offers does not); `onTop()` prices the House's and Bakery's plots where the next plot
  falls; the sector sheet draws note 11 with one sentence. No screen was looked at in a running game.
- Renders in `docs/queue/ET3/` (`tools.LandClassRenders`, about 40 s, earth-2 named): `et3-a-land-office.txt`,
  Medellín at months 2,000 and 120 ("Flat: D$11,370 a m² to a business · the office asks US$234.48 a m²",
  "Foothill: D$6,611 a m² (flat less its preparation, D$4,760 a m²)", "Steep (only mines, quarries and wells):
  D$324.39 a m²"; at month 120 foothill sits at the base); `et3-b-note-11.txt`, note 11 for the run's 14 sectors
  and, since the run builds no works, the harness's hilly fixture for a mining sector's steep row;
  `et3-c-rotterdam.txt`, no lines at all (flat).

## Checks and traces

- LandClassCheck (new, `landclass`), seven sections: the rule against its three cases; the office buys each
  class at its price (an offer made all flat, all foothill and all steep from the real shelf, bought, the money at
  the rate, the class areas grown by its own); the pickers (valley floor over mountain, foothill losing by its
  preparation, a mountain city still has a best, Game's and the auto-builder's); a business's plot on each class,
  zone shares; a flat city to the bit; the books (sums, office and books one price a class, note 11 adds up, the
  revaluation by class, a save round trip, redenomination); the screens' words rebuilt from the getters.
- `./batch-check.sh` with earth-2 named: see the report; MapCheck 8 is main's known red (FW11).
- `LongPlaytest --earth rotterdam`: byte-identical but the run-time lines (Rotterdam is flat).
- `LongPlaytest --earth medellin`, before (main, 0.8.43) and after: 257,761 people at month 4,005 and **432,191**;
  hunger 50% short and 0%; the ground at the end flat 39.3 / foothill 27.8 / steep 5.4 km² and 35.2 / 67.1 / 37.7;
  ground kept ahead 137 offers, 52.0 km², US$20.1B and 104 offers, 107.5 km², US$7.5B; the going ground price
  $37,803 a sq ft and $20,761. Audit and every reload clean in both. Its trace is a baseline of its own.

## Calls for Jerus (ET3-C, under Pending)

1. **Pro rata.** A sector's land is valued at the city's mix, not at the plots it bought. Keep (rec.), or per-sector
   class records as a batch of its own.
2. **Steep rides along.** Offers on the slopes hold foothill and steep together; at the base price the steep is
   nearly free, so Medellín's buyer ends with 37.7 km² of idle steep (ET2: 5.4). And crowding is people over ALL owned
   ground, so idle steep lowers every class's price ($20,761 a sq ft against $37,803). Crowding per buildable km²
   (rec., a small batch), or as is.
3. **Two preparation figures.** The office's foothill price takes SitePreparation's $110 a m² at the world's price
   level; a business's, the quote's own figure at today's builders' price. Keep both (rec.).
4. **The words.** The foothill line has three wordings (flat less its preparation / the base price / the flat price);
   prices under D$1 a m² print to four places (`Money.unitPrice`).

## Not done

- The build advice prices an order's ground at the best offer's price a DRY square foot
  (`LandManager.getOfficePricePerSqFt()`), not a buildable one.
- Works on ice (ET2-C ★4) stays with ET2-C.
