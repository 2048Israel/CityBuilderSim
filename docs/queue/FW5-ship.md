## changelog
### {next} — FW5, two reads that miss litres and one check that counts them twice — 2026-10-25, shipped, see docs/queue/FW5-notes.md
The month's draws are kept now. The railway's and its trucks' diesel, and
bitumen ordered before the strike, are drawn before the strike clears the
rows; `Markets.rowDrawsBeforeStrike()` puts them back on their sellers'
"sold at home" (the sellers' rows only, units only: the railway bills from
the buyers'). Every draw (the forecourts' petrol, the vans' diesel, the
railway's, cars, materials) now stays among the market's fills past the
clearing until `Markets.closeMonth()`, the last statement of the month. The
industry card's buyers sum to the refinery's sold at home. OilCheck 6's "L
shipped" no longer adds exportBound to exported. OilCheck 18 and 19 and
RefineryViewCheck 4 are red on main and green here. The playtest's trace is
byte-identical, so no ensemble was run.
