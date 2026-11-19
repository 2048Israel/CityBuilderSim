# Steps and lots that scale (SZ1)

Jerus found that ordering ten thousand buildings or moving a fund measured
in billions took hundreds of presses, while a bond's fixed hundred-million
lot made a young 1900 city unable to ask for paper it could carry.

Magnitude buttons now follow the amount in hand: one unit of its leading
digit and a tenth of that, never below the control's least step. An empty
control starts at that least. Count labels stay compact, and the same rule
serves building, paving, land, borrowing, fund tickets, transfers and the
reserves exchange. Percentages and rates keep their own steps.

Each instrument has one reference-price lot in BondMarket. The lot follows
the month's price level, is capped by the city's smallest issue, and is
rounded down to 1, 2 or 5 times a power of ten. The smallest issue can
therefore be requested at founding, at reference prices or after very high
inflation. Foreign paper uses its own currency's minimum and cross rate.
Rollover, automatic construction and the playtest's funding driver use the
exact priced-and-capped lot before that final rounding. This preserves
EP1's rule that every model dollar scales with the founding price level;
rounding to nominal 1-2-5 values is for the player's pages and presses.

The changed played city exposed a save bug: care offered and the share who
could afford it were lost on loading, so the builder skipped a needed
clinic. Those last-month access figures now cross a save in their own
optional key, without changing the existing state array or save format.
