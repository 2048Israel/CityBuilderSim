## changelog
### {next} — TP1, the map's lane floor through a currency reform — 2026-11-08, ready, see docs/queue/TP1-notes.md
The brief's first two faults (trade by partner not rescaled by a reform) were already mended by DZ1 (0.9.4); this closes the
third. The map draws a partner only over a floor, `TradeLanes.MIN_LANE` (300, the board's D$0.3M), which was absolute city
money: a reform dividing every trade by 100 left it and hid every lane and arrow, and inflation drifted it. The floor is now
`TradeLanes.minLane(priceIndex, unit)`: the board's figure at the founding's prices (times `PriceLevel.FACTOR`, as EP1 prices
every anchor), times the price index, over the currency's unit, struck into each partner snapshot and carried to the map by
the feed (`MapView` rebuilds lanes and arrows when it moves). A 1900 town's map therefore shows a lane from about D$7.8
thousand a month of trade instead of D$300 thousand. DenominationCheck's section 7 gains the floor's arithmetic and, on its two
cities either side of the reform and with prices moved, the nation card, the month's figures, the floor, the lanes and the
arrows. Display only: no save change, no pool moves, the trace unchanged.
