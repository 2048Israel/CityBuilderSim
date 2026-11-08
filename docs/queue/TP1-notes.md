# TP1 - The lanes' floor through a currency reform ({next}, 2026-11-08)

The brief (`docs/queue/TP1.md`, queued 2026-10-30 from WN3's review) named three faults. **Two were already mended on main**
by DZ1 (0.9.4, 2026-11-02, after this row was written): `PartnerTrade.redenominate(scale)` scales the month, the year to
date, `yearCells`, the no-partner figures and every finished year (`Table.scaled`), `Game.reformCurrency()` calls it beside
the others, and `HistorySave.redenominate()` scales `tradeByPartner`'s values and not its units. DenominationCheck's section 7
holds the state, the history and the save's copy in two cities on the world fixture. Read, not rewritten; section 7 is green.
What was left is the third fault and the check the brief asks for.

## The floor in real terms

`TradeLanes.MIN_LANE` (300) was a floor in absolute city money: inflation drifted it, and a reform dividing every trade by
100 left it where it was and hid every lane and arrow.

- `TradeLanes.minLane(priceIndex, unit)` = `MIN_LANE * PriceLevel.FACTOR * index / unit`. `MIN_LANE` stays the dial as the
  board wrote it (D$0.3M at 2025's prices); GlobeCheck's explicit-figure fixtures keep it through the old `of()` and
  `Arrows.of()` overloads, which pass it.
- `PartnerTrade` reads the price index live (`setPriceIndex`, wired in `Game.ensureWorldMarket()` beside `setMoneyUnit`) and
  strikes `minLane()` into every snapshot it publishes, at the tally and at the reform, so the floor and the figures it is held
  against are always in one money. `TradeLanes.Feed.minLane()` (default `MIN_LANE`) carries it to the map.
- `TradeLanes.of(...)`, `Arrows.of(...)` and both `matches(...)` take the floor (new overloads; a floor that moves rebuilds
  the lanes); `MapView.lanesNow()` and `arrowsNow()` pass the feed's. The route chip's tip said "none under D$0.3M a month",
  now "...at 2025's prices (the same trade in the money of the day)".
- No save format change; nothing is saved (the snapshot is rebuilt at load, after the price index and the denomination are
  restored). No pool moves; the model reads none of it.

## Calls (★), taken the recommended way

- **★ Real terms by the price index and the money's unit, not by a share of the month's trade.** Recommended and taken: it
  keeps the board's meaning (a floor in money under which a trade is too small to draw) and the same lanes for the same trade;
  a share would draw the largest partners of a trade of D$50 and change what GlobeCheck's fixtures mean.
- **★ Priced at the founding's level (times `PriceLevel.FACTOR`, as EP1 prices every nominal anchor).** The board's D$0.3M
  predates EP1 and is 2025's money; left at 300 a 1900 town would draw no lane for decades (the fixture town's whole trade, D$15
  thousand a month in its second year, is under it). Now the floor is D$7.8 thousand at the founding and rises with the index
  (about D$300 thousand when prices reach 2025's). This changes what a 1900 player's map shows (lanes and arrows from a small
  trade). To keep 300 at the founding, drop `* PriceLevel.FACTOR` in `minLane()` and the first line of `lanesFloor()`.

## The check

DenominationCheck, section 7's third part (the harness is the finding; the mutation below was run):
- `lanesFloor()` on bare lanes: the floor's arithmetic (index, unit, both, bad input), and at the floor of units 1, 100, 1000
  and 1e6 a partner exactly at it has a lane and an arrow, one a hair under none, `matches()` true for that floor and false for
  another; the board's floor left as written hides both.
- `cardAndLanes()` on the section's two fixture cities (the plain one and the one reformed by 100 in July), before the reform,
  right after, a year on, and with prices moved (index 1.0134): the nation card's year to date (money divided, tonnes not),
  the month's figures, the floor (the board's in each city's index and unit), the lanes and the arrows built from the
  snapshot's own floor: the reformed city draws the same partners as the plain one; and the floor a reform left undivided
  would hide every lane of the reformed city (the fault, as a fixture assertion).
- The fixture's only partner is the nation the city stands in, which needs no lane at its own node, so the lanes are drawn
  from Halifax (an assertion says every partner stands apart from it); the floor and the figures are under test, not geometry.
- Mutation: with the unit left out of `minLaneNow()` the section goes red (floor 7.77 against 0.0777, lanes "0 of 1", arrows
  missing) in all three reformed checkpoints.

## Trace and gate

The trace cannot move: the model reads neither `Snapshot.minLane()` nor the lanes (only `MapView` does), and
`PriceIndex.getIndex()` is only read. Not played; the ensemble is skipped (EN1). `MapView` is outside the brief's file list
by three lines (the two builders and the tip), because the screen is where the floor is used.
