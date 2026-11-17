<!-- readiness: provisional -->
## changelog
### {next} — LX1, Luxury Retail's sales per post — 2026-11-17, ready, see docs/queue/LX1-notes.md
A Boutique covered 320 pieces of a $22,000 good a month with seven posts, so Luxury Retail's payroll was 0.5-1.7% of
its margin on main after CU2, where BEA's other retail pays 41.7% (LS1). A Boutique now covers 7 pieces with 8 posts
and the Department Store 80 with its 91 (ids 69 and 70), about a piece a post a month; the buildings, their floor and
cost and the piece's price are held. `LuxuryRetail.LABOUR_SHARE` (BEA's 41.7%) and a factor of two either way are the
band `LuxuryRetailCheck` holds the catalogue and a town's year of Boutiques to (red on main: 0.6% and 1.3%). On LS1's
probe the share reads 36-41% (median from m240; in the band 313 of seed 0's 331 years, none on main), and the shops
take a quarter of the old city's business workers; the default city is 912k at m2399 against main's 413k (551k
against 633k before OX1 and AQ1). The Department Store is never the planner's choice, as on main (a call, LQ1's).
A road load's last stream is now the remainder of the other two's sum (`TradeCostCheck`). A 4,000-month run on this
balance has the city map's save refused at months 3589 and 3952 (5.5M people), FB8's to mend. No save field, no
format bump.
## queued
| LQ1 | The luxury queue's size, a study: Luxury Retail's margin is 10-30% of the business sectors' value added on main (BEA's other retail is a few per cent of GDP), the households' luxury budget out of the wealth term and the surplus; at BEA's share of it the shops employ a quarter or more of the old city's workers (LX1), so what the budget should be is the next question ({next}) | [LX1's notes](docs/queue/LX1-notes.md) |
## known-reds
KeptCheck | the played city's market causes at least three Search pages | FB6 | 2026-11-17 | LX1's 600-month played city has 61 securities in the fund's reach; the fixture reads the city rather than causing the market
KeptCheck | Search Show more reaches page 3 | FB6 | 2026-11-17 | Follows the line above: on LX1's played city the Search list does not reach a third page
KeptCheck | the played city's record causes multiple older year folds | FB6 | 2026-11-17 | On LX1's played city the fund's last ACTIVITY_ROWS rows held one older year in one run of two; the fixture reads the record rather than causing it
LongPlaytest | the city map across a save | FB8 | 2026-11-17 | LX1's balance, seed 0 to 4,000 months: the map read back from its sidecar is not the same at m3589 and m3952 (slot 9, a 5.5M city; main's 1.9M run is clean); the sidecar refused whole on a reload, FB8's DM2-B
LongPlaytest | the played city sidecar was refused or lost history | FB8 | 2026-11-17 | The same two reloads read directly: CityMap.readSidecar refuses the sidecar (or loses its history) at m3589 and m3952 of LX1's seed-0 4,000-month run, beyond the gate's 2,400
