# FC2 - construction traded with the world: the foreign crews always on offer, at the base price and an excess in foreign money

Jerus, 2026-11-22, after PW5's 17:19 dry run, where a test town's builders spent every point on upkeep a little over
FC1's floor and no contractor came: "make it a imported/exported good, if currency is strong, it could be cheaper than
regular construction, so make it so that the base cost is still in domestic currency but the excess is in foreign
currency". The brief is `docs/queue/FC2.md`; FC2b (the next row) is left its seam and none of it is built.

## 1. What was built

- **The price, p + E x** (`BuildersShort`, THE FOREIGN CREWS, ALWAYS ON OFFER). p is the city's base price for a
  point (`BuildingManager.basePricePerPoint()`, no premium). x is `FOREIGN_EXCESS` (15%) of the world's base price for
  the same work: the founding's price for it (`basePricePerPointAtFounding()`, each template's cash over the expected
  level it is struck at - the D$ opened at par, `ForeignAccounts.OPENING_RATE` 1.00) carried at the world's level. E
  is the rate the goods are struck at (`EconomyManager.getExchangeRate()`, the D$ times the world's level, handed to
  `BuildingManager.setExchangeRate()` with every strike and on a load). So E x = `FOREIGN_EXCESS x
  worldPricePerPoint()`, and the share over the base is r = E x / p (`excessShare()`), capped at `capShare()` =
  premium(KNEE + `SURE_MONTHS`) = 0.16. The whole of it, p/E + x in foreign money, is the builders' import of
  services as FC1 books it (`Construction.payForeignCrews()`, `bookImportedService("Foreign crews")`).
- **The threshold.** An order's work goes abroad where the queue's premium passes r: past `thresholdMonths(r)` =
  KNEE + HALF sqrt(r / (CEILING - r)) months queued at home, premium() turned round. Never under the knee; at most
  KNEE + SURE_MONTHS = 21 months, whatever the currency (the cap). At par, 20.5 months.
- **The queue at home** (`BuildingManager.queueAtHome()`) is the queue less the contractor's book. `monthsQueued()`,
  the premium and the depot's earnings read it; the book is in neither.
- **The handover.** Each month, before the sites advance, `Game.foreignCrewsOffer()` caps the book at what is owed on
  site (FC1b), hands the contractor the queue at home past the threshold (`overflow()`, `Construction.handAbroad()`),
  then strikes his month on his book. The only place the book grows: since FC2 `Construction.bill()` puts nothing on
  it.
- **The contractor's month, a supply curve.** `foreignMonth()`: his book in the threshold's months, never under the
  builders' largest depot (FC1's rule) nor over the book - so the work abroad waits no longer than the work at home at
  the threshold. His excess rises with it (`excessAt()`): r and the rest of the way to CEILING times m / (1 + m), m his
  month over `SUPPLY_HALF` (2) of the builders' month at every post. He brings his own crews: his points join the
  sites' advance (FC1's pooled model) and no city worker is hired for them.
- **The export.** The points the city's own crews had and no site took (`pastOwnMonth` under nothing), while the queue
  at home is under the knee, are sold abroad at the world's base and the city's own excess, converted
  (`exportShare()`, the same rule turned round; `BuildingManager.exportPriceOf()`, nothing past the knee):
  `Construction.sellPointsAbroad()` -> `Sector.bookExportedService()` (new) -> the statement's exports -> the audit's
  "+ construction Exports" (TRADE), the national accounts' exports (EconomyManager sums every statement's) and the
  current account (`ForeignAccounts`: tradeIn). Only idle points: the sites take theirs first, so the city's queue is
  never starved.
- **An order's price** (`orderPriceMultiplier(ahead, each, n)`, `BuildersShort.meanShare()`): BA1's buildings, each
  in the queue at home the ones before it leave; within the threshold at the premium, past it at the contractor's
  excess at his month now. One building past the threshold pays 1 + his excess; 600 the same a home.
- **The wait an order is read at** (`Game.orderWaitOutput()`): the sites' month and his month as the next offer would
  strike it (`foreignMonthNow()`); where the sites get nothing, never under the largest depot, so the trap's waits stay
  finite as FC1 made them.
- **The screens.** The Construction screen's gauge: PRICE OF A POINT (the premium at home) and FOREIGN CREWS (his
  excess now, the threshold's months; amber while an order goes abroad), the FOREIGN CREWS row (built, paid abroad,
  handed this month, still his) and SOLD ABROAD (spare points sold, receipts, what a spare point fetches). The build
  card's line under the price row gives both offers; its info text is written from the constants. CityNeeds' BUILDERS
  flag: "overflow abroad: +x% a point", severe only where the sites get nothing. Every figure through a public getter,
  drawn through Kept.
- **Removed:** FC1's `FOREIGN_FLOOR`, `FOREIGN_PRICE`, `FOREIGN_CAPACITY_SHARE`, `foreignTakesOrders()`,
  `foreignCapacity()`, `BuildingManager.foreignCrewsTakeOrders()`, `Construction.bill(.., t)` and the offer's
  `depotPoints`.

## 2. The numbers, and why each is what it is

- **FOREIGN_EXCESS = 0.15.** What a foreign contractor's point costs over a local builder's for the same work, at par.
  The evidence brackets it: flying the workers in adds up to 10% of a whole job's cost (RLB, Hawaii 2014-15), about a
  sixth of the work beside its materials; an expatriate costs two to five times a local post (Mercer; SHRM), and a
  foreign firm brings a few of them over a local workforce; a mobilisation bid past a tenth of a contract is held back
  (MoDOT 618); and the World Bank's margin of preference for domestic contractors on works, the gap a foreign bid
  must clear to lose, is 7.5% (WB Appendix 2; CGD). 15% is twice the preference margin and inside the travel and
  expatriate figures.
- **SURE_MONTHS = 12 (N).** The cap: a weak D$ never pushes the threshold past KNEE + 12 = 21 months, premium 0.16 - a
  fifth of CEILING (0.8), "well below" it, and the first whole year past the knee whose premium is over
  FOREIGN_EXCESS, so the cap does not bind at par. A foreign firm working in a weak currency's market trims its markup
  to the one it asks at par rather than price itself out (est.; pricing-to-market).
- **SUPPLY_HALF = 2.** His excess is half way from r to CEILING when his month is twice the city's own trade. Demand
  surge after disasters, when the work is a fraction to a few times the local trade, raised reconstruction costs
  10-40% after Katrina and 20-50% after earthquakes (Olsen and Porter, USGS 2011; WCEE 2012; CEB 2025). From r = 0.15
  the curve gives +22% at a quarter of the city's month, +37% at one and +54% at three, never CEILING.

Sources:

- World Bank, domestic preference for works, Appendix 2: https://de.slideshare.net/InfoProcurementAsia/wb-appendix-2-domestic-preference
- Center for Global Development, the MDBs' local content rules: https://www.cgdev.org/sites/default/files/role-mdb-local-content-rules-industrial-strategy.pdf
- RLB, Perspective 2022 vol. 2, "Project managers: know your workforce" (Hawaii, 2014-15): https://www.rlb.com/europe/insight/perspective-2022-vol-2/project-managers-know-your-workforce/
- Mercer, expatriate cost projections: https://mobilityexchange.mercer.com/insights/article/cost-projections-the-answer-to-budget-and-management-mandates
- SHRM, the cost of expatriates: https://www.shrm.org/hr-today/news/hr-magazine/Pages/0305krell.aspx
- MoDOT Engineering Policy Guide 618, mobilization: https://epg.modot.org/index.php/Category:618_mobilization
- Olsen and Porter, demand surge (USGS): https://pubs.usgs.gov/publication/70045146 and https://www.usgs.gov/publications/demand-surge-following-earthquakes
- WCEE 2012, demand surge: https://www.iitk.ac.in/nicee/wcee/article/WCEE2012_2865.pdf
- Construction Economics and Building, 2025: https://epress.lib.uts.edu.au/journals/index.php/AJCEB/article/view/9568/8702

## 3. The calls

- ★ **The backlog settles at the threshold.** His month is his book in the threshold's months, and each month hands him
  the queue at home past it, so a long backlog read at both paces is the threshold's months after the first offer: a
  240-month backlog settles near KNEE + N (at par 20.5, at the weakest D$ 21).
- ★ **An order sent abroad still counts in the backlog that orders a depot.** `Construction.plan()` reads the whole
  queue (`getRemainingConstructionPoints()`), the book in it. The depots that bring the work home are what end the
  import, and the builders know the work is theirs to deliver - they are its general contractor. OG1's sizing is capped
  at 1 + CEILING, so the count cannot run away.
- ★ **A cancel cuts the foreign book first** (`cancelOrder(back, r.points(), r.points())`): the builders, losing a job,
  cut the work they pay a stranger's excess for before their own crews'. A depot served first is no exception now: the
  book holds the queue's points, not orders.
- ★ **The builders bear the gap** between what an order was billed and what the contractor is paid for the work
  handed him, as FC1's builders paid for their depots: the order's abroad part is priced at his excess now
  (`foreignTerms().share()`), and his month's excess is struck when he builds it.
- ★ **The export covers idle points only.** Hiring crews for work abroad is not built: the spare points are the city's
  crews' own month that no site took.
- ★ **The threshold is struck at the curve's first point** (r), the brief's "an order goes abroad exactly when the
  premium passes E x / p"; the price paid is the curve's at his month, so a quote past the threshold is over the
  premium at the threshold.
- ★ **The trap's fixture stays FC1's depth.** ConstructionTrapCheck causes the trap to 5% of the builders' month
  (`SLIVER`, FC1's floor), though the model reads no floor now, so every section starts where FC1's did; its Row's
  trap is that depth, since the month's handover leaves the queue at home at the threshold.

## 4. Saves

No format bump is owed. Every figure the model reads is derived on a load: the book was saved since FC1, the
exchange rate reaches the builders from the load path (`economyManager.setExchangeRate()` at the traded rate), and
the threshold, the overflow and the price are worked out from them. The three new fields
(`handedAbroadThisMonth`, `exportPointsThisMonth`, `exportReceiptsThisMonth`, in Construction's extras) are flows the
screens read for the month last played; a save before FC2 lacks them, and nothing was handed or sold abroad in it, so
0 is its true value.

## 5. Checks

(to come)
