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
- **The handover, at the payer's price (REV-FC2's P1).** An order's buildings past the threshold go to the contractor
  as it is billed (`Game.processBuildOrder()` and `buildFor()`, after `Construction.bill()`, ->
  `Construction.handAbroad()`), counted building by building by BA1's rule (`BuildersShort.homeBuildings()`, the one
  `meanShare()` prices by), at the price a point its payer's quote charged for them: the order's own base a point and
  his excess at the month the order itself makes him run (`BuildingManager.abroadOfOrder()`: his book, the work ahead
  of it abroad in the same run, and its own points past the threshold, in the threshold's months, never under the
  largest depot; the threshold struck on the base with the order on site). The book holds money as well as points
  (`foreignBookShare`, `foreignBookPrice`, means by the point) and is drawn in proportion, so what he bills over its
  life is what its payers paid for that work. Each month, before the sites advance, `Game.foreignCrewsOffer()` caps
  the book at what is owed on site (FC1b), counts the points handed since the last offer as the month's
  (`closeHandovers()`) and strikes his month on the book; it hands nothing. Work quoted at home stays at home: an
  order within the threshold; a building that starts within it, however far its points run past; work at home that
  a moving threshold falls under; a restart's shell (`Game.quoteRestart()`); and an order placed with no trade on (the
  founding's first, before any depot's output).
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
- **An order's price** (`orderPriceMultiplier(ahead, abroadAhead, each, n, base)`, `BuildersShort.meanShare()`): BA1's
  buildings, each in the queue at home the ones before it leave; within the threshold at the premium, past it at the
  share `abroadOfOrder()` strikes for the order - the share it is handed at. One building past the threshold pays 1
  and that share; a long order's own points raise its month, so it pays the curve's dearer price for them.
- **The wait an order is read at** (`Game.orderWaitOutput()`): the sites' month and his month as the next offer would
  strike it (`foreignMonthNow()`); where the sites get nothing, never under the largest depot, so the trap's waits stay
  finite as FC1 made them.
- **The screens.** The Construction screen's gauge: PRICE OF A POINT (the premium at home) and FOREIGN CREWS (his
  excess now, the threshold's months; amber while an order goes abroad), the FOREIGN CREWS row (built, paid abroad,
  handed this month, still his) and SOLD ABROAD (spare points sold, receipts, what a spare point fetches). The build
  card's line under the price row gives both offers, the foreign one at the share this order would be handed at
  (`Game.abroadOfOrder()`); its info text is written from the constants. The FOREIGN CREWS row's "+x% a point" is the
  book's share. CityNeeds' BUILDERS
  flag: "overflow abroad: +x% a point", severe only where the sites get nothing. Every figure through a public getter,
  drawn through Kept.
- **The Trade tab** (`Sectors.TradeByGood.servicesSold()`): an export with no good behind it, by the sector that sold
  it, as the imports' `services()` were: a sector's export line less its goods sold abroad. `sold()` counts it, so the
  tab's sold abroad is the balance of payments' exports again (ForeignCheck's footing read 0.39 off with the builders
  selling abroad); `TradeScreen.goodRows()` draws it as "Sold abroad by Construction", the row beside "Bought abroad
  by". Before FC2 no sector exported anything but goods.
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
- **SURE_MONTHS = 12 (N), its own reason (REV-FC2; FC2 had derived it from FOREIGN_EXCESS).** A year past the
  builders' own backlog is time enough for an owner to tender the work abroad and have the winner start. A road
  resurfacing contract takes 270 days in Korea from the bidders' documents to the winner starting work, among the
  quickest; Venezuela's average is 2,062 days and Haiti's and eight others' over three years (Bosio and Djankov, World
  Bank, 2020-02-12). FIDIC's Red Book has the work commence within 42 days of the letter of acceptance (1999,
  sub-clause 8.1). An owner who would wait a year past the knee has had the time to bring a foreign firm in at
  whatever it asks, so the threshold never passes KNEE + 12 = 21 months: the cap, premium(21) = 0.16, a fifth of
  CEILING.
- ★ **For Jerus: a weak currency barely raises the threshold.** The cap is barely over FOREIGN_EXCESS, 0.16 against
  0.15, so a D$ about 6% under par (E a fifteenth over it) already sits at it: from par to the weakest D$ the
  threshold moves only from 20.5 months to 21, and past that a weaker D$ changes neither the threshold nor the
  crews' first share (E x is held at 0.16 of p) - it changes only how much of their price leaves as foreign money.
  A strong D$ works the other way the whole length: it lowers the threshold as far as the knee, 9 months at
  `ForeignAccounts.MIN_RATE` (ForeignCrewsCheck section 1).
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
- Bosio and Djankov, "Timely procurement of public works", World Bank, 2020-02-12: https://blogs.worldbank.org/en/developmenttalk/timely-procurement-public-works
- FIDIC Conditions of Contract for Construction (the Red Book), 1999, sub-clause 8.1, commencement of works: https://www.hcindiabrunei.gov.in/docs/1562048637FIDIC%20Condition%20of%20Contract%20(General%20Conditions).pdf
- Olsen and Porter, demand surge (USGS): https://pubs.usgs.gov/publication/70045146 and https://www.usgs.gov/publications/demand-surge-following-earthquakes
- WCEE 2012, demand surge: https://www.iitk.ac.in/nicee/wcee/article/WCEE2012_2865.pdf
- Construction Economics and Building, 2025: https://epress.lib.uts.edu.au/journals/index.php/AJCEB/article/view/9568/8702

## 3. The calls

- ★ **The backlog settles at the threshold.** An order's buildings past the threshold go to him as it is billed, so a
  240-month order leaves the queue at home at the threshold, over it by no more than the building its last at home
  stands in, and his month is his book in the threshold's months: it is built at the threshold's pace, near KNEE + N
  (at par 20.5, at the weakest D$ 21). The threshold moves with the base once the order is billed, so the backlog
  read at both paces stays within a month of it, either side.
- ★ **An order sent abroad still counts in the backlog that orders a depot.** `Construction.plan()` reads the whole
  queue (`getRemainingConstructionPoints()`), the book in it. The depots that bring the work home are what end the
  import, and the builders know the work is theirs to deliver - they are its general contractor. OG1's sizing is capped
  at 1 + CEILING, so the count cannot run away.
- ★ **A cancel cuts the foreign book first** (`cancelOrder(back, r.points(), r.points())`): the builders, losing a job,
  cut the work they pay a stranger's excess for before their own crews'. A depot served first is no exception: since REV-FC2
  an order's buildings past the threshold are on the book from its billing, a depot's as any.
- ★ **The payer pays (REV-FC2's P1; FC2 as first reviewed had the builders bear the gap).** That form quoted an
  order's abroad part at his excess before the order and billed it at the curve's excess at the month the whole
  overflow at home made him run, so the builders paid the difference: 1,389.92 in the first month of
  ForeignCrewsCheck's 240-month backlog, and in ForeignCheck's founded town 46,753 billed in three years where its
  orders were quoted 54,170 points abroad for 7,958, the crews handed 208,555. Jerus's "a huge backlog costs more
  abroad" means the payer pays it: an order is quoted at the curve's month it makes and handed him at that price as
  it is billed, so the bill is the quote (4,370.0443 and 4,370.0443 in that first month; the town to 1.25e-16).
- ★ **Work quoted at home stays at home.** The work abroad is only what an order's quote put there. Work its payers
  were quoted at home stays with the city's builders when the threshold later falls under it, or when a building
  starts within it and runs far past (ForeignCheck: the coal plant, 70.4 months queued at home after it against a
  threshold of 20.53), so orders after it go abroad until the city's builders work it down; FC2 as first reviewed
  handed it on at the curve's price, which is where the builders' gap came from.
- ★ **The export covers idle points only.** Hiring crews for work abroad is not built: the spare points are the city's
  crews' own month that no site took.
- ★ **The threshold is struck at the curve's first point** (r), the brief's "an order goes abroad exactly when the
  premium passes E x / p"; the price paid is the curve's at his month, so a quote past the threshold is over the
  premium at the threshold - though not always over the premium at home a road past it, which keeps rising: there
  one House's share is +0.1510 against the queue's 0.1519 (ConstructionTrapCheck section 3). It goes abroad because
  the premium passed his first share, not because he is the dearer.
- ★ **The trap's fixture stays FC1's depth.** ConstructionTrapCheck causes the trap to 5% of the builders' month
  (`SLIVER`, FC1's floor), though the model reads no floor now, so every section starts where FC1's did; its Row's
  trap is that depth. Since REV-FC2 the queue at home may stand past the threshold, holding the work quoted at home:
  in section 4 the depot lifts the city's own sites over the sliver in its month while orders still go abroad.

## 4. Saves

No format bump is owed. Every figure the model reads is derived on a load: the book was saved since FC1, the
exchange rate reaches the builders from the load path (`economyManager.setExchangeRate()` at the traded rate), and
the threshold, the overflow and the price are worked out from them. The three new fields
(`handedAbroadThisMonth`, `exportPointsThisMonth`, `exportReceiptsThisMonth`, in Construction's extras) are flows the
screens read for the month last played; a save before FC2 lacks them, and nothing was handed or sold abroad in it, so
0 is its true value.

REV-FC2 adds three more of Construction's extras: `foreignBookShare` and `foreignBookPrice`, the book's share and
price a point, means by the point, and `handedSinceOffer`, the points the orders billed since the last offer handed
him. A save before REV-FC2 (FC1's, whose book carried no price) loads its book with both means NaN
(`foreignBookUnpriced()`), and the first offer strikes them at the curve's share at his month on it and the base then
(`priceUnpricedBook()`), the price that book would have been billed at under the form it was saved in;
`handedSinceOffer` loads 0, nothing having been handed since. No save reads wrongly, so still no bump.

## 5. Checks

The scope moves below (BuildersFirstCheck, SaveFileCheck's brake town, GapOrderCheck's section 6, ForeignCheck's JVM)
were measured with the crews on under FC2 as first reviewed, which handed the overflow at home on each month. Since
REV-FC2 an order hands only its own buildings past the threshold, so with the crews on those cities would send less
abroad; each section's claim is the city's own builders', and it is still run with the crews off, as written.

- **ForeignCrewsCheck** (new; a Sonnet slice, reviewed; 109 assertions of its own since REV-FC2, green). Each bound is caused
  through the model's inputs (the rate, the queue, the level, standing roads) on both sides of its edge:
  1. The strongest D$ (`ForeignAccounts.MIN_RATE`): threshold 9.000326 against the knee's 9; 8.854 months queued
     not abroad, 9.075 abroad.
  2. r = 0.05 (E 0.3393): threshold 15.1968, crossed between 15.18 and 15.84 months. r = 0.10 (E 0.6786): 18.0711,
     crossed between 17.23 and 18.09. The share is FOREIGN_EXCESS E x / p computed in the harness, to 1e-12.
  3. The weakest (`MAX_RATE`): the share is the cap, 0.16, the threshold exactly 21; crossed between 19.92 and 21.07.
  4. The level doubled (1.0217 to 2.0434): p 0.7008 to 1.3918; x 0.103275 and E x 0.070084 unchanged; r 0.10 to
     0.05035; the threshold 18.07 to 15.22.
  5. The export, after 8 months with the queue at 0.047 months: the sites' month 23, 6.0 points sold for 5.6884 and
     17.0 built (no site starved). The month after, the statement, the audit's "+ construction Exports", the trade in,
     the national and the foreign accounts carry it. A point fetches 0.9191 at 8.85 months queued and nothing at 9.07;
     a month played at 10.85 sold nothing.
  6. 240 months (REV-FC2): the sites' month 343; the order's 75,380 points past the threshold quoted at +0.6767 (+0.1434
     his share without it), 1.173166 a point on the House's base 0.699684, and on his book at exactly that as it was
     billed; the queue at home 20.23 months against a threshold of 20.2170; his month 3,729 at that price. 120 months:
     1,693 at +0.5785. The first month he built 3,725 points and billed 4,370.0443, the payer's price for them, the
     gap in the builders' cash 0 where the form first reviewed left 1,389.9198. For months 2 to 6 the backlog at both
     paces ran -0.2277 to 0.2862 months from the threshold (at most 20.4095), nothing more was handed him, and the
     book's price stayed the order's to the bit; the save round-trip carries the book's share and price. A twin with
     `buildersShortOn` false for its first month: every sector's workers and every tier's posts the same, his 3,725
     points built in one and none in the other. ★ Not "the jobs filled the same" (516 against 513): the fill rate is
     the households' side, which the month moves through what it built.
  7. 29 months audited here, the worst residual 1.15e-13.
- **ConstructionTrapCheck** (moved to the new truth, 319 OK): FC2's pure shape; since REV-FC2 his book before the
  trap's sites is empty (the founding's Penitentiary was quoted at home and its work stays there) and the trap's
  sites, ordered past the threshold, go on it whole as they are billed (30,600 points); sold abroad in 3 of 35
  months, 21.87; the worst residual over its 59 months 1.15e-13. REV-FC2's moves, each with its reason in a comment: the depot on his book
  as billed at its quote's share and price (section 4), the offer handing nothing, the city's own sites over the
  sliver while orders still go abroad, the plan ordering the Builders' Yard or a depot (section 5), a restart's
  points staying at home (section 9), his month as the month struck it within a point of the offer read before it
  (sections 4 and 10: the book is fixed and the threshold restruck at the month's top), and section 3's ordering
  (the call above).
- **DeathCareFirstCheck**: main's, unchanged. FC2 as first reviewed had moved "the Penitentiary still owing most of
  its points" to "still owing points", the founding handing its work past the threshold abroad; since REV-FC2 the
  Penitentiary is quoted at home and its work stays there, so main's assertion holds again and is back.
- **BuildersFirstCheck** (22 red with the foreign crews on): they finish the stall's depot in its first month with
  BF1's rule or without it (section 4: 1,120 of nameplate without the rule, not the 560 it held). The rule is shown
  where it is the only way out, the city's crews alone: `buildersShortOn` false for its JVM, as HoldersCheck,
  MonetaryCheck and RunWorksCheck have it. Green.
- **SaveFileCheck** (4 red), two moved:
  - The brake town (WG2-R) is caught in the builders' trap on main (23.8% out of work, 0.1 arrivals) and FC2 frees
    it (647 people, 612 posts, none out of work). That section reads Migration's months recorded through a save, so
    the town is played as written, the switch off for that section alone. Green (29.9% out of work).
  - REV-HC1 P5's continuation: FC2's populated city consolidates Real Estate to 1e-28 of its founding shares (main's
    run, about 1e-7), and the load put it back at 1e-12 (`Exchange`, since 0.7.12). ★ The load now carries every
    positive factor (`Exchange.carriedSplitFactor()`; 1e-12 only for none, a negative or a NaN), asserted directly.
    No save reads differently unless its factor is under 1e-12, which was wrong before. ★ Real Estate's deeper slide
    in that city (its cash -234 at month 153) is FC2's economy, not its save: the ensemble reads it.
- **LongPlaytest** (green, every month audited; rerun on REV-FC2 merged with PW5, 2026-11-23). ★ The played city's
  path moved: REV-FC2's grows faster from the founding (twice main's at m255), lags it from about m1,000 to m1,800 (a
  fifth of main's at m1,506), and ends 13% smaller at m2,344. FC2 as first reviewed ended 57% larger. Population at
  its checkpoints against main's (AP2's tree, before PW5):

  | month | REV-FC2 | FC2 as first reviewed | main |
  |---|---|---|---|
  | ~255 | 2,817 | 2,826 | 1,413 |
  | ~600 | 9,883 | 14,831 | 7,027 |
  | ~1,050 | 28,572 (cash $963k, GDP $19.1M a month) | 28,684 (cash $694k, GDP $36.9M) | 35,771 (cash $255.8M, GDP $89.6M) |
  | ~1,300 | 36,093 | 47,577 | 159,044 |
  | ~1,510 | 54,390 | 268,294 | 269,905 |
  | ~2,350 | 1,555,403 | 2,819,538 | 1,796,799 |

  The dip is the ensemble's to judge; every harness that reads the played city at a month reads a different city.
- **KeptCheck**: the played city in its dip at m1043 cannot pay for the land office's best offer (section 31, before
  FS5's range): the fixture gives it the gap first (`KeptCheck.buyTheBestOffer()`), so the receipt's page is caused.
  From section 55 on, the reds are FS5's played-city classes (rows 39, 42-45, 54-56 and 104 of
  `docs/notes/known-reds.txt` pass them by name); one is new, the ship file's known-reds row: section 55's Bank
  Overview made no node in 23 of 25 months, but its lists shrank from the first draw's 721 nodes (590 handed back a
  month), KBP1's class - the measure is the first draw, not the page drawn again. ★ On REV-FC2's merged city (after
  PW5, 2026-11-23) the played path moved again and the reds with it: five, three passed by name (section 63's redraw
  and its rule held again, Household money's grid) and two new, the ship file's rows - section 50's Wood Products
  picture, drawn at section 47 and gone by 50 at m1442, and section 79's Month page counts past m3472, twelve months
  with neither trade figure's words moving after the two pages before it found a move at every rung. Both are
  fixtures that read the played city and do not cause what they need; FC2 touches neither the pictures nor the
  count-up.
- **ConstructionControlCheck** (8 red, moved): its rush city's queue is past the threshold, so the foreign crews'
  points join the month the plan shares out (since REV-FC2 400 of 596-1,744 a month, his month on his book at the
  largest depot's points; 419-423 of 619-1,744 as first reviewed). The rush's work and premium move from
  the share to the city's own crews' part of it, ConstructionTrapCheck section 10's truth (FC1b's rule: only the
  city's crews work overtime): own = share x (month - his points) / month, asserted to 1e-12 each month; the work
  (share - own) + own x 50/40 x the factor; the premium 0.375 of the own part's bill. With nothing of his in the month
  own is the share and each line is the one it was.
- **GapOrderCheck** (2 red in section 6, moved by scope): the stalled city's year of orders is built abroad with the
  crews on (41,486 owed at its end against 109,675 off), and the city it leaves can staff 0 of 2 depots (sites 0 and
  275 a depot, against 69 and 400 off), the plan holding on staff. The section's claim is the depot plan's cut
  (`Construction.depotsForTheQueue()`, which reads `BuildersShort.premium()` itself, so the switch leaves it be): run
  with the switch off for that section and on after, SaveFileCheck's brake-town shape; 2 of 2 staffable, one ordered.
- **ForeignCheck** (5 red; one mend in the model, the rest by the switch for its JVM):
  - "the goods the businesses sold abroad are the month's exports" read 0.39: the builders' export was in the balance
    of payments and on no row of the Trade tab. Mended in the model (the Trade tab above); 0.00e+00 after it, and
    ForeignCrewsCheck section 5 asserts it with the crews on (5.6884 on the builders' row; 68.9176 sold against
    68.9176 of exports).
  - Every city here is ordered its town and programme whole on day one, years of its four depots' work, which the
    crews now build abroad within the year. Measured with them on: section 2's town finished at month 10 with nobody
    in it, the first two years' payroll $677k and $366k against trade deficits of $20,859k and $12,689k (the crews
    billed $35,333k and $9,805k) - a town being bought, not the swamp law's town paying its people - and its bakery
    stood two months with nothing sold abroad (3 of 3 years and 74 of 76 months); section 6's twins bought part of the
    programme at p/E + x a point, whose p/E falls and whose threshold rises with a weaker D$ (Construction's imports
    $99,022k at parity, $78,545k at 1.40): the premise read 0.8040 and not every order of the 1.40 city's was placed. That is
    the hinge Manufacturing and Cement already sit out of the twins for. The switch is off for ForeignCheck's JVM, the
    precedent's form (HoldersCheck, MonetaryCheck), and `BuildingManager.buildersShortOn`'s javadoc now lists every
    harness and section that turns it off. Green: 11 of 11 years swamped, 140 of 180 months sold, the premise 0.9708.
- **ForeignCheck's founded town with the crews on** (REV-FC2's third item, a section of its own, the switch on for it
  alone): section 2's town ordered whole on day one, played 36 months. 52,410 of its points stood past the threshold
  at their orders (321 of 500 Houses at +0.4481, 0.9970 a point; 2 of 3 grocers; the water works, 44,000 points at
  +0.4145; both bakeries; none of the coal plant), quoted abroad for 7,260.50; the first offer counted exactly those
  handed; for the 7 months before a later order handed him work, what he billed and his book still held at its
  price came to the quote within 1.25e-16; the coal plant built at home though it left 70.4 months queued there
  against a threshold of 20.53; the 500 Houses stood whole at month 32 with 1,822 people (before REV-FC2: month 10,
  ahead of its people). Its first year the crews billed 3,143.25.
- **FitoutCheck** (2 red) **and WellCheck** (1 red), moved by REV-FC2: their reference multiple read no base a point
  (`orderPriceMultiplier(ahead, each, n)`), while the quote, `dearOf()` and `getCostOfAt()` strike the foreign crews'
  threshold and share on the order's own base a point (`basePerPointOf()`, the threshold with the order on site); in
  a city past the threshold the two differ (FitoutCheck's default town's two farms: 97.72 against 99.04). Each
  reference reads the order's own base now; each claim (the template's points; the wells at the multiple of an order
  of that many) is as it was. Green.
- Green on REV-FC2 merged with PW5, run one at a time: ForeignCrewsCheck, ConstructionTrapCheck, ForeignCheck,
  DeathCareFirstCheck, SubstationCheck, GapOrderCheck, ConstructionControlCheck, BuildersFirstCheck, SaveFileCheck,
  AuctionCheck, FitoutCheck, FittingCheck, RunWorksCheck, WellCheck, MonetaryCheck, BuildCardCheck, BuildMenuCheck,
  PowerPriceCheck, LongPlaytest; HoldersCheck its known red only; KeptCheck its rows above.
- Green after FC2, run one at a time: ManufacturingCheck, FitoutCheck, InvestCheck, WellCheck, RunWorksCheck,
  MortgageCheck, FittingCheck, AuctionCheck; HoldersCheck its known red only. `./batch-check.sh --fast` green.
