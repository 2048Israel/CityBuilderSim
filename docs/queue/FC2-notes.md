# FC2 - construction traded with the world: the foreign crews always on offer, at the base price and an excess in foreign money

Jerus, 2026-11-22, after PW5's 17:19 dry run, where a test town's builders spent every point on upkeep a little over
FC1's floor and no contractor came: "make it a imported/exported good, if currency is strong, it could be cheaper than
regular construction, so make it so that the base cost is still in domestic currency but the excess is in foreign
currency". The brief is `docs/queue/FC2.md`; FC2b (the next row) is left its seam and none of it is built.

## 1. What was built

- **The price, p + E x** (`BuildersShort`, THE FOREIGN CREWS, ALWAYS ON OFFER). p is the city's base price for a
  point (`BuildingManager.basePricePerPoint()`, no premium). x is `FOREIGN_EXCESS` (15%) of the world's base price for
  the same work: the founding's price for it (`basePricePerPointAtFounding()`, each template's cash in founding dollars,
  `getFoundingCashCost()`, since the second review - the D$ opened at par, `ForeignAccounts.OPENING_RATE` 1.00) carried
  at the world's level. E
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
- **The handover, at the payer's price (REV-FC2's P1).** An order's work past the threshold goes to the contractor
  as it is billed (`Game.processBuildOrder()` and `buildFor()`, after `Construction.bill()`, ->
  `Construction.handAbroad()`), counted by the point (`BuildersShort.homeUnits()`, the one `meanShare()` prices by:
  BA1's buildings within the threshold whole, and since the second review the one it falls in split there, its
  points past it abroad), at the price a point its payer's quote charged for them: the order's own base a point and
  his excess at the month the order itself makes him run (`BuildingManager.abroadOfOrder()`: his book, the work ahead
  of it abroad in the same run, and its own points past the threshold, in the threshold's months, never under the
  largest depot). The threshold is struck with the order on site, on the base a point and, since the second review,
  on the world's price a point too (`pricePerPointWith()`), so the threshold an order is quoted on is the one read
  once it stands; in a run (`Game.buildRunInvoice()`) with the orders before it on their own stacks as placing them
  would put them (`abroadOfOrder()`'s six-argument form), so the run's invoice is its orders placed in turn. The book holds money as well as points (`foreignBookShare`, `foreignBookPrice`, means by the point)
  and is drawn in proportion, so what he bills over its life is what its payers paid for that work. Each month,
  before the sites advance, `Game.foreignCrewsOffer()` caps the book at what is owed on site (FC1b), counts the points
  handed since the last offer as the month's (`closeHandovers()`) and strikes his month on the book; it hands
  nothing. Work quoted at home stays at home: an order within the threshold; the part of a building within it; work
  at home that a moving threshold falls under; a restart's shell (`Game.quoteRestart()`), a demolition and the
  paving, priced at home (section 3); and an order placed with no trade on (the founding's first, before any depot's
  output).
- **Each site's claim on his book (REV-FC2, the second review).** The book is one sum, drawn by his month; each stack
  holds a claim on it (`BuildingsStacks.abroadClaim`), raised as its order is handed him in proportion to the book
  (`BuildingManager.noteAbroad()`), so a site's work still on the book is the book times its claim over the claims of
  the stacks still owing (`abroadOwedOf()`), and his month draws every site's in proportion as it draws the book's
  money. A cancel takes the cancelled site's share, struck at the stop (`ConstructionControl.Refund.bookShare()`) and
  cut once his month is off the book (`Game.settleConstructionControl()`, after `settleForeignCrews()`), never more
  than the points the order had left (section 3). Since the third review a site that owes nothing starts its next
  work with no claim (`BuildingsStacks.claimAfresh()`, as an order, a restarted shell or a preparation goes on site):
  a load reads none for it, and an order quoted at home notes none.
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
- **An order's price** (`orderPriceMultiplier(ahead, abroadAhead, each, n, of)`, `BuildersShort.meanShare()`): BA1's
  buildings, each in the queue at home the ones before it leave; within the threshold at the premium, past it at the
  share `abroadOfOrder()` strikes for the order - the share it is handed at; the building the threshold falls in, its
  part within at the premium where it begins and the rest at the share. One building past the threshold pays 1 and
  that share; a long order's own points raise its month, so it pays the curve's dearer price for them. Its prep,
  works, bridge and deep water (`Game.quoteBuild()`) go abroad in its buildings' share of points and are priced at
  their blend, home and abroad (`abroadWork()`), so each part is priced where it is built.
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
- **AT_THRESHOLD = 1e-9 months (REV-FC2, the second review).** A queue split at the threshold stands at it to the
  rounding of the months' sum; within this of it, it reads as at it, so the next order goes abroad whole
  (`ordersGoAbroad()`, `homeUnits()`) and not a sliver of a building at home with the rest abroad.

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

- ★ **The backlog settles at the threshold.** An order's work past the threshold goes to him as it is billed, so a
  240-month order leaves the queue at home at the threshold (since the second review exactly: the building it falls
  in is split there), and his month is his book in the threshold's months: it is built at the threshold's pace, near KNEE + N
  (at par 20.5, at the weakest D$ 21). The threshold moves with the base once the order is billed, so the backlog
  read at both paces stays within a month of it, either side.
- ★ **An order sent abroad still counts in the backlog that orders a depot.** `Construction.plan()` reads the whole
  queue (`getRemainingConstructionPoints()`), the book in it. The depots that bring the work home are what end the
  import, and the builders know the work is theirs to deliver - they are its general contractor. OG1's sizing is capped
  at 1 + CEILING, so the count cannot run away.
- ★ **A cancel cuts his book by the site's own work on it (REV-FC2, the second review; FC2 cut the order's points
  left from the book first).** The builders lose the job and the stranger his part of it: the cancelled site's claim
  on the book, struck at the stop and cut after his month, never more than the points it had left; the rest of its
  points were the city's builders' and leave the queue at home. FC2's form cut other sites' work off the book where
  the cancelled order had little or none on it (ForeignCrewsCheck section 7: a Middle School ordered within the
  threshold, 1,658.6 points left and none on his book, which FC2 would have cut from the plant's work; the plant's
  share 0.983221, his book 108,113.0 after his month and 1,814.1 after both cancels). ConstructionTrapCheck
  section 9's University, 0.681261 of the book, cuts 24,361.90 of its 25,459.29 points left. A depot served first is
  no exception: its work past the threshold is on the book from its billing, a depot's as any.
- ★ **A finished site claims nothing (REV-FC2, the third review, P2-1).** `noteAbroad()` notes only work handed
  abroad, so a site that finished kept the claim its orders had made: inert while it owed nothing (`liveClaims()` reads
  the sites with points owed), woken by the next order on it quoted at home - and the save keeps only the sites with
  points owed, so its loaded twin woke with none, and the two parted at the next cancel. The claim now goes when the
  site starts new work owing nothing, which is what the load reads. ForeignCrewsCheck section 7: the Houses' site
  finishes 5 months after a second plant is split, his book 90,030.1 all the plant's; 162 Houses quoted at home on it
  then claim none of it in the city and in its loaded twin, the plant's 90,030.1 in both, and the cancels take the
  same shares (the Houses' 0, the plant's 1) and leave the same book. On d35563166 the Houses' site claimed 1,421.3
  (the twin 0), the plant's 88,608.7 against 90,030.1, and the Houses' cancel 0.015787 of his book (the twin 0): 3 red.
- **The parts handed abroad are owed on their site (the third review, P2-2: not reproduced).** The review read the
  queue (`getRemainingConstructionPoints()`) as the buildings' points alone, so that the preparation, works, bridge
  and deep water `Game.abroadWork()` hands abroad with an order would be missing from `queueAtHome()` and the cut of his
  book to what the sites owe (`Construction.capForeignBook()`). They are owed on the order's stack, as progress below
  nothing (`BuildingsStacks.addPreparation()`, each placed by both order paths), and the queue reads every stack as its
  points on site less its progress. ForeignCrewsCheck section 7, the same on d35563166: a Water Treatment Plant on
  foothill, 44,000 points and 9,119.7 of its pad's preparation, split with 36,398.0 of its points past the threshold,
  quotes 43,942.0 abroad (its preparation in its building's share); what the sites owe rises by 53,119.67, both; his
  book by 43,942.04; a month on his book is what it held less what he built, 41,850.04, nothing cut. (A business's
  order puts no bridge on its site and bills none; no business orders a bridge.)
- ★ **The payer pays (REV-FC2's P1; FC2 as first reviewed had the builders bear the gap).** That form quoted an
  order's abroad part at his excess before the order and billed it at the curve's excess at the month the whole
  overflow at home made him run, so the builders paid the difference: 1,389.92 in the first month of
  ForeignCrewsCheck's 240-month backlog, and in ForeignCheck's founded town 46,753 billed in three years where its
  orders were quoted 54,170 points abroad for 7,958, the crews handed 208,555. Jerus's "a huge backlog costs more
  abroad" means the payer pays it: an order is quoted at the curve's month it makes and handed him at that price as
  it is billed, so the bill is the quote (4,370.0443 and 4,370.0443 in that first month; the town to 1.25e-16).
- ★ **The giant building: a defect of the rule's spirit, mended by splitting it at the threshold (REV-FC2, the
  second review).** FC2 counted an order by whole buildings, BA1's rule: a building that began within the threshold
  stayed at home however far its points ran past it. The reviewer's lead is PW5's Substation, 132,000 points (its
  template in `BuildingManager`), years of a middling city's sites: ordered with the queue at home under the
  threshold, it stood whole in that queue, and every order after it went abroad until the city's crews had worked
  it down. In ForeignCrewsCheck section 7's city a Coal Power Plant of 120,000 points is 338.03 months of the sites'
  355 a month; whole, it would have left 351.13 months queued at home, 330.13 past the threshold, every later order
  abroad for as long. In ForeignCheck's founded town the coal plant left 70.40 months against a threshold of 20.53.
  That is not Jerus's rule working: "at a certain premium they do go for it" makes the crews reachable once the
  queue is long, and it does not hand them the city's builders' every later order for as long as one building sits
  in the queue, with no way for the city's crews to win any back until it is built. Now the threshold splits the
  building it falls in: its part within is the city's, the rest goes abroad at the share, the queue at home stands
  at the threshold after it, and the next order within it is built at home. Section 7: the plant's 117,195 points
  past it abroad at +0.7123 (the queue at home 13.0986 months before it, the threshold 21), a month on 20.6444
  months and a House ordered then wholly at home; the town's coal plant leaves exactly 20.5292 months against
  20.5292. Work quoted at home still stays at home when the threshold later falls under it: its payer was quoted
  that.
- ★ **Home work is priced at home (REV-FC2, the second review).** A restart, a demolition and the paving are built by
  the city's crews and never handed abroad, so they are priced at the premium at home
  (`BuildingManager.homePriceMultiplier()`, 1 + `scarcityPremium()`), not the order's multiple, which past the
  threshold is the crews' share: past it a restart costs what the city's own month charges for it. An order's prep,
  works, bridge and deep water follow its buildings, abroad in their share of points and priced at the blend.
- ★ **The export covers idle points only.** Hiring crews for work abroad is not built: the spare points are the city's
  crews' own month that no site took.
- ★ **The threshold is struck at the curve's first point** (r), the brief's "an order goes abroad exactly when the
  premium passes E x / p"; the price paid is the curve's at his month, so a quote past the threshold is over the
  premium at the threshold - though not always over the premium at home a road past it, which keeps rising: there
  one House's share is +0.1510 against the queue's 0.1519 (ConstructionTrapCheck section 3). It goes abroad because
  the premium passed his first share, not because he is the dearer.
- ★ **The trap's fixture stays FC1's depth.** ConstructionTrapCheck causes the trap to 5% of the builders' month
  (`SLIVER`, FC1's floor), though the model reads no floor now, so every section starts where FC1's did; its Row's
  trap is that depth. Since the second review the queue at home stands at the threshold, the building it fell in
  split there: in section 4 the depot's month works it down to 0.1387 months, and orders stay at home after it.

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
(`priceUnpricedBook()`); `handedSinceOffer` loads 0, nothing having been handed since. ★ Corrected in the second
review: that is not the price the book would have been billed at under the form it was saved in. FC1 billed its book
at `FOREIGN_PRICE`, 2.0 times the base a point; those points now bill at the base times one and the share (under 1 +
CEILING, 1.8), so the builders pay the crews less for that work than FC1 would have and keep the difference. Money
still goes from the builders to the crews, only less of it, once; no save reads wrongly, so still no bump.

The second review adds one key, `DataSave.abroadOwedById`: each site's work still on his book, in points by template
id (its claim, `BuildingsStacks.abroadClaim`, saved as the points it reads; the stacks are restored by id). A save
before it has none, a null map: every site's claim is then struck at the points it still owes
(`BuildingManager.restoreAbroadOwed()`), so his book is shared out by the work owed on site, which is what an FC2
save's claims would have read wherever every site was handed him in the same share. Only a cancel reads the claims,
and in such a city it takes that share. No save reads wrongly, so no bump (ForeignCrewsCheck section 7 reads a save
both ways: the plant 112,936.4 of its 120,000 owed, the Houses 4,282.2 of their 4,550, read as a save from before).
The third review's claim reset (section 3) is the live city reading what the load reads: no key, no bump.

A currency reform (`Construction.redenominateExtras()`) now scales the book's price a point with the rest of the
builders' money (the second review's P1; FC2 left it in the old D$): ForeignCrewsCheck section 8 reforms a thousand
to one with his book on, 58,014 points at 1.174893464 a point after it 0.001174893464, and the next offer's price is
the same. The book's share and its points are not money and stay.

★ Two more of the reform's family, found by DenominationCheck on the second review's tree (its reformed twin parted
from its twin the month after the reform: its people, its rent, its price level, the shops' till), both in FC2 since
its first form. The builders' copy of the rate (`BuildingManager.exchangeRate`, kept for the world's price of a point)
was not scaled by a reform: EconomyManager scales its own and hands it on at the next strike only. And the world's
price a point was struck on each template's cash over the expected level, which is in today's unit, so E, in today's
unit too, times it moved with the square of a reform: once the month struck the rate, a reform cut the
crews' first share by its own factor and the threshold to the knee. Now `BuildingManager.redenominate()` scales the rate
with the materials' price struck from it, and the founding's price a point is the catalogue's cash in founding
dollars (`getFoundingCashCost()`), which neither the level nor a reform moves - before any reform the same figure, to
the rounding. DenominationCheck holds its twins to 1e-9 again, a month, a year and a decade on. No save is touched: the
rate is handed on with every strike and on a load.

REV-FC2b (the fourth review's P1): the names of the exports with no good behind them, `Sector.Ledger.servicesSold`
and `Statement.servicesSold` (the builders' work sold abroad), were never saved. The month's sale is in the pending
ledger at every save, so a load struck its receipts into revenue with no line naming them, and "revenue by good"
missed by the sale. Both are saved now (`SectorState.LedgerState` and `StatementState`, a name-to-amount map each).
No format bump is owed under the old-saves rule: a save before FC2 sold no work abroad, so the empty map an older save
reads is what it had pending and struck, and no FC2 save is in any player's hands. SectorStatementCheck 6a saves
ConstructionTrapCheck's calm town in the month it sold 19.0 points abroad for 3.418447, loads it through the game's
own load (pending 3.418447 after it), plays both a month (both statements name 3.418447; the loaded one's revenue
26.549287 is its other revenue 23.130841 and the work sold abroad, and the saved town's to the bit), and saves and loads the
struck statement again.

## 5. Checks

The scope moves below (BuildersFirstCheck, SaveFileCheck's brake town, GapOrderCheck's section 6, ForeignCheck's JVM)
were measured with the crews on under FC2 as first reviewed, which handed the overflow at home on each month. Since
REV-FC2 an order hands only its own buildings past the threshold, so with the crews on those cities would send less
abroad; each section's claim is the city's own builders', and it is still run with the crews off, as written.

- **ForeignCrewsCheck** (new; a Sonnet slice, reviewed; 165 assertions of its own since the third review, green). Each bound is caused
  through the model's inputs (the rate, the queue, the level, standing roads) on both sides of its edge:
  1. The strongest D$ (`ForeignAccounts.MIN_RATE`): threshold 9.000326 against the knee's 9; 8.854 months queued
     not abroad, 9.075 abroad.
  2. r = 0.05 (E 0.3393): threshold 15.1968, crossed between 15.18 and 15.84 months. r = 0.10 (E 0.6786): 18.0711,
     crossed between 17.23 and 18.09. The share is FOREIGN_EXCESS E x / p computed in the harness, to 1e-12.
  3. The weakest (`MAX_RATE`): the share is the cap, 0.16, the threshold exactly 21; crossed between 19.92 and 21.07.
  4. The level doubled (1.0217 to 2.0434): p 0.7008 to 1.3918; x 0.103275 and E x 0.070084 unchanged; r 0.10 to
     0.05035; the threshold 18.07 to 15.22.
  5. The export, after 8 months with the queue at home at 0.088 months: the sites' month 23, 23.0 points sold for
     4.2612 and none built (no site starved). The month after, the statement, the audit's "+ construction Exports",
     the trade in, the national and the foreign accounts carry it. A point fetches 0.9198 at 8.85 months queued and
     nothing at 9.07; a month played at 10.85 sold nothing.
  6. 240 months (REV-FC2): the sites' month 355; the order's 77,745 points past the threshold quoted at +0.6764
     (+0.1600 his share without it), 1.174893 a point on the House's base 0.700838, and on his book at exactly that
     as it was billed; the queue at home at the threshold, 21.0000 months (to 1e-9 since the second review, the House
     it fell in split); his month 3,702 at that price. 120 months: 1,674 at +0.5785. The first month he built 3,702
     points and billed 4,349.4556, the payer's price for them, the gap in the builders' cash 0 where the form first
     reviewed left 1,339.8321. For months 2 to 6 the backlog at both paces ran -0.2560 to 0.1111 months from the
     threshold (at most 21.0000), nothing more was handed him, and the book's price stayed the order's to the bit;
     the save round-trip carries the book's share and price. A twin with `buildersShortOn` false for its first
     month: every sector's workers and every tier's posts the same, his 3,702 points built in one and none in the
     other. ★ Not "the jobs filled the same" (513 against 511): the fill rate is the households' side, which the
     month moves through what it built.
  7. The building the threshold falls in (the second review): a Coal Power Plant of 120,000 points, 338.03 months
     of the sites' 355, ordered with the queue at home at 13.0986 months against a threshold of 21: 117,195 points
     past it abroad at +0.7123 (the multiple 1.696194), the rest at home, where whole buildings would have left
     351.13 months queued (330.13 past it). A month on, the queue at home 20.6444 months and a House quoted wholly at
     home. The claims: his book 113,519.0, the plant's 111,614.2 and the Houses' 1,904.8, carried through a save; read
     as a save from before the key, the plant's struck at its 112,936.4 owed of 120,000. Cancelled after his month: a
     Middle School with none on the book cuts none of it (FC2 cut its 1,658.6 left), the plant its share 0.983221 of
     his book after his month, 108,113.0 to 1,814.1. The third review: a finished site's later order at home claims
     none, in the city and its loaded twin, through a cancel; a split works' preparation abroad is owed on its site
     (section 3's two calls).
  8. A currency reform, a thousand to one, with his book on: 58,014 points at 1.174893464 a point, after it
     0.001174893464; the month after he built 6,199 points and billed 7.283165, at the new unit's price.
  9. 51 months audited here, the worst residual 3.13e-13.
- **ConstructionTrapCheck** (moved to the new truth, 324 OK): FC2's pure shape. Since the second review the
  founding's Penitentiary (62,000 points) is split at the threshold and its work past it went on his book as it was
  billed, 7,716 points of it still there before the trap's sites; they were ordered from under the threshold (the
  queue they joined 7,368 to 37,968) and went on his book as billed, all but the threshold's room, 768 points (struck
  unclamped: his book stood over the queue at home by 348). The queue at home stands at the threshold, 21.000000000000
  months, and the city's own premium is the crews' first share, 0.16, far under its ceiling, where FC2's whole
  Penitentiary left it within a hair of it. Sold abroad in 4 of 24 months, 77.11; every month audited, the worst
  residual 1.71e-13. The second review's moves, each with its reason in a comment: section 1's straddle priced by
  the split; section 2's queue at the threshold, its premium and the book before the trap; section 4's depot leaving
  the queue at home at 0.1387 months, orders at home after it; section 9's cancel cutting the University's share of
  his book (0.681261: 24,361.90 of its 25,459.29 points left), the restart built at home, and the landlords' Houses
  all abroad in the cancelled twin (400 points) and 170 in the kept; the identity's split building (15 of 24 queues
  and sizes past the threshold or split by it). REV-FC2's first moves stand: the depot on his book as billed at its
  quote's share and price (section 4), the offer handing nothing, the plan ordering the Builders' Yard or a depot
  (section 5), his month as the month struck it within a point of the offer read before it (sections 4 and 10: the
  book is fixed and the threshold restruck at the month's top), and section 3's ordering (the call above).
- **DeathCareFirstCheck** (1 red, moved): FC2 as first reviewed had moved "the Penitentiary still owing most of its
  points" to "still owing points", the founding handing its work past the threshold abroad; REV-FC2 put main's back
  when whole buildings kept the Penitentiary at home; since the second review the threshold splits it, its work past
  the threshold goes abroad again, and the move is FC2's first one, with its reason: 56,905 of its 62,000 points
  built at the section's month, on site still, which keeps every post offered (the fixture after it). Green.
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
- **LongPlaytest** (green, every month audited; rerun on the second review's tree, 2026-11-23, and judged against main
  0.9.192 at f937d2c22, PW5's merge, run the same day on the same PC). ★ The played city's path: the second review's
  grows faster than main's from the founding (1.5 times main's at m254, 2.6 times at m597, 5.1 times at m812), then
  dips and stalls from about m800 to m1,300 (31,974 to 27,242 people, cash $39.2M to $3.1M) while main's grows through
  it (6,265 to 60,979), lags most at m1,503 (a fifth of main's), passes it again by m1,750 (1.19 times) and ends 5%
  under it (1,755,763 at m2,329 against 1,848,741 at m2,353); REV-FC2 as first reviewed (whole buildings) ended 16%
  under it. Population at the checkpoints (each run strikes its own months; the nearest are set side by side):

  | month | the second review | REV-FC2 as first reviewed | main 0.9.192 (f937d2c22) |
  |---|---|---|---|
  | ~255 | 2,679 (m254) | 2,817 | 1,828 (m257) |
  | ~600 | 14,059 (m597) | 9,883 | 5,372 (m597) |
  | ~810 | 31,974 (m812; cash $39.2M, GDP $70.6M a month) | 25,686 (m751) | 6,265 (m810) |
  | ~1,050 | 27,242 (m1044; cash $11.7M, GDP $69.0M) | 28,572 | 19,146 (m1050) |
  | ~1,290 | 27,912 (m1276; cash $3.1M, GDP $90.2M) | 36,093 | 60,979 (m1297) |
  | ~1,510 | 76,015 (m1503) | 54,390 | 415,928 (m1513) |
  | ~1,750 | 1,074,735 (m1750) | | 903,213 (m1754) |
  | ~2,050 | 1,539,505 (m2082) | | 1,322,540 (m2001) |
  | ~2,340 | 1,755,763 (m2329) | 1,555,403 | 1,848,741 (m2353) |

  The judgment against main: FC2 changes when the played city grows, not whether - ahead of main for its first 1,050
  months, behind it from about m1,100 to m1,700, ahead again at m1,750 and m2,082, and within 5% at the end. The stall
  between m800 and m1,300 (the cash $36M down over it) is FC2's economy, not a check's: what drives it is the
  ensemble's to read, and its verdict goes to the orchestrator before any ship.
- **The ensemble** (80 of 80 runs on the gate box, 22.0 min, the final model 348fdae18 merged with main 0.9.193,
  committed as `docs/ensemble/FC2.md`, scored against `docs/ensemble/0.9.192.md`, the newest; PW1-W's trace was
  unchanged): batch verdict PASS, the hard criteria 10/10. M1 0 in every policy. F1 0/16 in every policy, where the
  baseline had 1/16 ('households with no home') in AUTO2, ZERO_SLACK, SHOCK and HIGH: four FAIL -> PASS. 25 of 26
  criteria passed (H1 retired); the one FAIL is AUTO2's A2 (info), the longest spell of yoy over 10% from m240, 12
  months [8 to 31] against the baseline's own FAIL of 14 [6 to 37]. The played city grows sooner and larger: T1 16/16
  AUTO2 seeds reach 40,000 people at m678 [607 to 871] (baseline m747 [624 to 1,300]); final population (T2) AUTO2
  5,627,582 (baseline 4,649,724), ZERO_SLACK 3,801,960 (2,883,284), ZERO_HOT 9,827 (7,632), SHOCK 94,822 (67,498; its
  T2 FAIL -> PASS), HIGH 32,514 (32,356). Inflation unmoved: A1 2.69% [2.32% to 2.93%], 16/16 in band (2.67%,
  14/16); S1 2.01% (2.14%); H2 -1.75% (-1.47%), 13/16 below; K1 15.14% (16.49%).
- **KeptCheck**: the played city in its dip at m1043 cannot pay for the land office's best offer (section 31, before
  FS5's range): the fixture gives it the gap first (`KeptCheck.buyTheBestOffer()`), so the receipt's page is caused.
  From section 55 on, the reds are FS5's played-city classes (rows 39, 42-45, 54-56 and 104 of
  `docs/notes/known-reds.txt` pass them by name); one is new, the ship file's known-reds row: section 55's Bank
  Overview made no node in 23 of 25 months, but its lists shrank from the first draw's 721 nodes (590 handed back a
  month), KBP1's class - the measure is the first draw, not the page drawn again. ★ On REV-FC2's merged city (after
  PW5, 2026-11-23) the played path moved again and the reds with it: five, three passed by name (section 63's redraw
  and its rule held again, Household money's grid) and two new: section 50's Wood Products picture, drawn at
  section 47 and gone by 50 at m1442, and section 79's Month page counts past m3472, twelve months with neither trade
  figure's words moving after the two pages before it found a move at every rung. Both are fixtures that read the
  played city and do not cause what they need; FC2 touches neither the pictures nor the count-up.
  ★ The second review: section 50 is outside FS5's sweep (from section 55), so it is mended here as section 31 was -
  the section causes its own works month where it reads the picture (`ensureWorks()`, which section 47 already calls:
  a sawmill stood if none stands, the sector held through a month played, only where the view is not drawn), and its
  ship row is gone. Section 79's row and section 55's and 63's stay FS5's. On the second review's tree KeptCheck ran
  green, every section, its played city with a wood products month to draw at section 50 (the fixture had nothing to
  do there that time). After the reform's and the run's mends (which move the played path by the rounding of the
  founding's price) it ran 7 red lines: section 63's five, the ship row and the four `docs/notes/known-reds.txt`
  passes by name, and two new, each now a ship row with FS5 its owner by class - section 49's Freight Cash & debt
  statement, the blank-figure row kept at 836x24 against 836x16 built new, the very route of known-reds' Construction
  line that FS5 takes with FS5H; and section 69's Government Overview at m2366, its lists shrunk from the first draw's
  357 nodes (316 handed back a month, 23 of 25 months making none), KBP1's class as section 55's row is. Section 50
  green in both runs; FC2 touches no Kept, statement or Government code.
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
  alone): section 2's town ordered whole on day one, played 36 months. Since the second review every order is placed
  (the fixture tops up the cash an order needs, and asserts the threshold it was quoted on is the one read after
  it), and the buildings the threshold falls in are split: 139,931 points stood past the threshold at their orders
  (180 of 500 Houses, 1,797 points at +0.5111; the 8 convenience stores, 3 grocers and 4 depots; the coal plant's
  87,174 points at +0.5146; the water works' 44,000 at +0.5748; both bakeries), quoted abroad for 37,152.30; the first
  offer counted exactly those handed; for the 6 months before a later order handed him work, what he billed and his
  book still held at its price came to the quote within 1.96e-16; the coal plant left the queue at home at 20.5292
  months against a threshold of 20.5292 (before the second review, whole at home, 70.4 against 20.53); the 500 Houses
  stood whole at month 13 with 1,253 people (month 32 with 1,822 before the second review; month 10, ahead of its
  people, as FC2 was first reviewed). Its first year the crews billed 15,725.65.
- **FitoutCheck** (2 red) **and WellCheck** (1 red), moved by REV-FC2: their reference multiple read no base a point
  (`orderPriceMultiplier(ahead, each, n)`), while the quote, `dearOf()` and `getCostOfAt()` strike the foreign crews'
  threshold and share on the order's own base a point (`basePerPointOf()`, the threshold with the order on site); in
  a city past the threshold the two differ (FitoutCheck's default town's two farms: 97.72 against 99.04). Each
  reference reads the order's own base now; each claim (the template's points; the wells at the multiple of an order
  of that many) is as it was. Green.
- **FitoutCheck** (1 red) **and BuildCardCheck** (1 red), section 9 of each, found on the second review's tree and
  mended in the model: a run's invoice is its orders placed in turn (FitoutCheck near, BuildCardCheck to the bit), and
  the run struck each order's threshold with its own points on site where the run's orders before it should have stood
  (FitoutCheck's depot after six farms was struck on seven depots' mix: 223.3426 against 223.3099; BuildCardCheck's
  run of three orders, 4,632.4954 against 4,632.5052). The run hands each order the points
  the orders before it put on site, by template, and the threshold folds each into its own stack as `addStack()` would
  (one stack a template; a new one at the end), so the sum runs in the placed city's order. Both green, BuildCardCheck
  to the bit again.
- **ConstructionControlCheck** (1 more red, moved): section 6's demolition price referenced the order's multiple
  (`orderPriceMultiplier()`, FC1); a demolition is home work since the second review, so it references
  `homePriceMultiplier()`, the premium at home (53.1108 where the order's multiple, the crews' share past the
  threshold in this city, read 56.8944). Green.
- **DenominationCheck** (6 red, mended in the model): the reform's family above. Green.
- Green on the third review's tree (c2d0c8bd5), run one at a time: ForeignCrewsCheck, ConstructionTrapCheck,
  ForeignCheck, ConstructionControlCheck, DenominationCheck, SaveFileCheck; `./batch-check.sh --fast` green.
- Green on the second review's tree (26fe8b1d0 and its notes), run one at a time: ForeignCrewsCheck, ForeignCheck,
  ConstructionTrapCheck, DenominationCheck, FitoutCheck, BuildCardCheck, ConstructionControlCheck, DeathCareFirstCheck,
  SaveFileCheck, WellCheck, GapOrderCheck, BuildMenuCheck, SubstationCheck, BuildersFirstCheck, AuctionCheck,
  MonetaryCheck, FittingCheck, RunWorksCheck, PowerPriceCheck, LongPlaytest; HoldersCheck its known red only;
  KeptCheck its rows above.
- Green on REV-FC2 merged with PW5, run one at a time: ForeignCrewsCheck, ConstructionTrapCheck, ForeignCheck,
  DeathCareFirstCheck, SubstationCheck, GapOrderCheck, ConstructionControlCheck, BuildersFirstCheck, SaveFileCheck,
  AuctionCheck, FitoutCheck, FittingCheck, RunWorksCheck, WellCheck, MonetaryCheck, BuildCardCheck, BuildMenuCheck,
  PowerPriceCheck, LongPlaytest; HoldersCheck its known red only; KeptCheck its rows above.
- Green after FC2, run one at a time: ManufacturingCheck, FitoutCheck, InvestCheck, WellCheck, RunWorksCheck,
  MortgageCheck, FittingCheck, AuctionCheck; HoldersCheck its known red only. `./batch-check.sh --fast` green.

## 6. The dry run's mends (2026-11-23)

The first dry run (e90eb0b93, gate box run cbs-gate-FC2-0uEcU2DO) was red in 27 harnesses, 168 NEW lines. Fourteen of them were red on
RES3's dry run too; by the orchestrator's call (08:28) FC2 owns the mends to those fixtures, and RES3 verifies them after merging.
The rule: a fixture causes its condition on any played path, a broken law is mended in the model, and a premise traced and not
causable in FC2's scope passes by name to its owner, with its trace. Two laws were restated to the model's own figures, neither
loosened: CurrencyCheck's vault (read as the lean sold on it, since-founding as every sale since founding) and Forms1900Check 11's
allowance (now to the cent: feed, hire and the carters' pay the haul priced). FundCheck 10's law gained the households' taxes on
the shares the fund's buying moved, read from the model (below). Each mend's comment in its harness carries the figures.

Model (for the delta review):
- SectorStatementCheck: Construction's work sold abroad is a revenue line of its own, `Sector.Ledger/Statement.servicesSold` and
  `bookExportedService()` (struck, restored and scaled with the rest), named `Construction.WORK_SOLD_ABROAD` in SectorFlow and the
  sector screen's revenue detail (d241790cc). Before, the export's money reached the till without a line, and the statement did not add up.
- MoneyWordsCheck: the builders-short line names the city's money by `Currency.name()` (BuildScreen, ConstructionScreen; f7043a5c7).

REV-FC2b's (the fourth review, NOT SHIP, 4 P1), model and interface, for the delta review:
- `Ledger/Statement.servicesSold` saved (section 4), with SectorStatementCheck 6a.
- `BuildScreen.buildersShortInfo()` takes the money's name as a `%s` argument of its format: joined into the template, a typed
  "100% Crown" threw where the build card's tooltip was drawn. MoneyWordsCheck 4 types it. The method and its class are public
  so the harness, in the model's package, can call it.
- `HouseholdBalance.offeringMeans()`: subscribe()'s own weighing summed, from one private `offeringWant(cell)` that subscribe()
  now calls (the same arithmetic in the same order, so no offering moves by a bit). OrderDoorCheck's fixture reads it, and its
  lift raises every cell's savings by the same sum a household until the model's figure covers OWNERS_COVER times the down
  payment; until the review the harness kept its own copy of the weighing and asserted the copy.

Fixtures, each caused between months by a lever the model has:
- ScaleCheck: the builders' saved fill (`workFill`) is a share, left unscaled; the K-times copy worked K times its posts and sold the
  phantom crews' month abroad.
- FundLedgerCheck, FundCheck: the founding hands the bank's quote where the town's orders left the cash short. FundCheck 9d lifts
  the rescue book until the month's due is BOND_ROOM_MARGIN times the bond room. Section 10 (REV-FC2b): the twins play at the
  default dial, the player's case, and the law is the budget moving by the fund's transfer and by the households' taxes on the
  shares its buying moved, both read from the model (`NationalAccounts.getCapitalGainsTax()`, `getDividendTax()`): balances
  39.711545 and 0.614910, transfers 39.130746 and 0, the gains tax 0.082604 and 0.116715 (a's fund bought 5,016 Industry shares),
  the dividend tax 0 in both. The twins one step over the default, where the fund trades with nobody, keep the bare law beside it.
- FloatLinkCheck, DeskCheck: every played order is funded at its quote, and the town is still building at the sale, so the tax
  covers the sale's fee.
- CallCheck: the bank's capital is kept over its share desk's book between months.
- GoldStandardCheck 9: a pinned month makes its quiet top, and M0 is brought to M0_FLOOR by an advance.
- CurrencyCheck: the vault is read as the lean sold on it, and since-founding as every sale since founding.
- ShortSellingCheck: the desk is given the means for the short (its position and book limits).
- BankCheck 14: retail's till is lifted to SLICE_TILL_MONTHS of its bills, so only the slice defaults.
- CentralBankCheck 19: an heirless estate of the households' paper goes to custody before the press. The two par-alone and add-on
  plans read nothing netted within 1e-9: the central bank's whole par is one ulp over the principal, against the exact `== 0` the
  fixture had.
- OrderDoorCheck 2: the households' means for the offering, the model's (`HouseholdBalance.offeringMeans()`, REV-FC2b), are lifted
  to OWNERS_COVER times the down payment at the door's price.
- CarCheck OE1: the edge's cells hold EDGE_HOUSEHOLDS = 1024, so the take's count is exact. At 333.54 households, one car read an
  ulp under. The `==` is unchanged.
- EssentialWorksCheck: the later priority work is one Container Terminal. The foreign crews had built the 10,000 roads, and their
  repairs took the builders' output to 0.
- RailCheck: every sector is held from the track's laying, so the freight stays what the track was sized to.
- CapitalFlowCheck: the builders hold no dollars or bonds, and owe the bank, in both twins.
- ForeignDebtCheck: twenty-year paper is sold until "later" is the heaviest rung.
- PegAnchorCheck 9: the euro town holds a vault of PEG_VAULT_MONTHS of imports.
- DebtMoneysCheck: the window abroad is held open at the euro bill's roll, and at the save's quotes.
- Forms1900Check: the carters' pay is read as the haul priced it (the month before), and the homes beside the bill; with it the
  allowance covers the feed, the teams' hire and the pay to the cent (ALLOWANCE_CENT), the old 0.9 gone (REV-FC2b). The lamp-lit
  cells get half a car a grown-up.
- GdpCheck: the railway is handed the cash for its sets.
- InvestCheck: the House is priced out by its materials, and the crews are thinned to 95% of the best home, none of it past the
  threshold.
- ER1Check: the restored cash is sized from the offer the cash brings.
- CreditCheck: the banned retailer keeps a till of two shops for a statement's months, and Freight's founding is one month.

Passed by name to OX2: DeathRecordCheck, `the orphan section does not fill behind it`, a law and not a premise, so not FC2's to
cause; its owner is OX2's queued row (the arrivals' children), beside its sibling row. The row and its trace are in the ship
file's known-reds.

Found, not FC2's (queued FC2-N): `Game.Slices.passes()` returns before `judged()` for a banned sector that borrows, so
`consider()` prints "at NaN%" in the refusal (CreditCheck's banned retailer); identical on main at b34277adc. A message only; the
fix is to set firstRate before the early return.

FC2's consequences, owned (REV-FC2b's P1 4), each with its queued row in the ship file:
- FC2-G, Gold: the foreign crews' bills, an import of services, are paid in gold at the points and drain M0. GoldStandardCheck's
  1933 town held M0 at 0.00 from world month 48, its cover reading Infinity from 120 to 398 while the regime stood (main's rule:
  no break at an M0 of nothing); section 9's fixture brings M0 to M0_FLOOR by an advance. Whether a regime with no money breaks,
  or the crews' bill waits on the reserves, is the queued row's to decide.
- FC2-R, repairs: what the foreign crews build, the home builders repair. EssentialWorksCheck's 10,000 roads, 505 built abroad by
  m38, took the repairs from 347 to 12,893 points by m41 and the builders' site output to 0, so the auto-builder held its power
  order ("no site output"); the fixture's later priority work is one Container Terminal. Whether repairs past the builders' month
  go abroad as new work does is the queued row's to decide.
- FC2-C, the build-time caps (found on the merge of 0.9.194): his month is his book over the threshold's months, and PW2 caps what a
  site takes in a month at its build time (`BuildingManager.siteCap()`), so where the sites holding his book are capped, the
  month's points past their caps land on the other sites, those quoted at home among them, and his book falls by what he built
  there. ForeignCrewsCheck 7's Coal Power Plant, held to 1,449.7 points a month, took that of a month of 4,125 while the Houses'
  site took 2,447 and the Middle School, quoted at home, 229; later, his book only the plant's, 118 Houses quoted at home met his
  3,729 and finished in the month. It is not a money error (REV-FC2c): the builders alone pay the world, the points he built at
  his book's price, and the swap sits in their margin, the home price their payers paid for the work he built and his price for
  the capped sites' work they build later. Whether his month is what the sites holding his book can take is the queued row's.

The merge of main at 0.9.194 (PW1-W, PW2; 6c487106e). Every harness the merge touched on either side was rerun alone, and
`--fast`; three were red, each a premise the merged path moved, and each is now caused:
- DeathCareFirstCheck 2: the trap's town was sicker on the merged path, 29.0% against main's 20.9% (the baseline 7.5%, 3.7% more
  for the unhoused, the capped dead's 15%), its sick month 398 points against 443, so a year of the cemetery's points left 80 of
  it and the repairs took 80.0%, at the bar and not over it (main's town 82.4%). The roads now leave the builders no more than
  1 - TRAP_REPAIRS of their sick month less a road's repairs (`sites(g, TRAP_REPAIRS)`): the repairs take 81.6%.
- CurrencyRegimeCheck 12: money arrives in the month saved only while that month's target, drawn on the month's own GDP, is over
  the stock. The save month's GDP fell from 816.1 to 277.6 (its construction investment 970.5, then 247.3), its target to 2,499
  under the stock of 3,826, and nothing arrived (main's town took 244.6k, FC2's before the merge 267.6k). The save month's target
  is read from a twin played the month ahead from a save, and the stock set to HOT_SHARE of it (`InterestAbroadCheck.setHotStock()`,
  the bank moved with it): 100.0k arrived, 23.0k left on doubt, and the save's checks read them to the bit.
- ForeignCrewsCheck 7: the claims a month on read the queue at home in months of the sites a month after the split left it at the
  threshold, 22.69 months against 21. The calm town's crews had been struck to the core crew on its last month, of repairs and
  nothing on site (work ordered at the top of a month is crewed the month after, THE CREWS THE WORK NEEDS), so the month on worked
  19 points of the queue at home; and the sites' month at every post fell from 351 to 324 as the town's sickness rose from 0.2084
  to 0.2541 (its out-of-work cell, past its savings' cliff, paid no care: the coverage 1.0, then 0.6965). PW2's cap sent his month
  onto the Houses' site, which finished in the cancels' month and took its claim on his book with it (FC2-C). Now care is free at
  the point of use (the fee scale 0, as HealthCheck's insured city has it), a month is played with the landlords' Houses on site
  so the crews are struck at every post (posts offered 1.0), and the Houses are as many as outlast HOUSE_MONTHS of everything the
  city's crews and his could build (1,600): the month on worked 355 points, the sites' month 355 then 342, the queue at home 20.76
  months. The Houses' claim line reads the claim itself, as the save carries it (`getAbroadOwedById()`, no cut to the points owed;
  REV-FC2c), against their share: the Houses' site took his month past the plant's cap and owed 14,654.9 against its share of
  15,238.1, printed as FC2-C's trace. `claimOfAFinishedSite()` plays the city and its twin
  on alike until half the room under the threshold owes more than a month of every crew (37 months; 158 Houses against 1,577
  points), so the Houses quoted at home are on site at the cancel.

The merge of main at 0.9.195 (LP-G1; 975886be7). Every harness the merge touched was rerun alone, and `--fast`; one was red,
GroundSkipCheck (LP-G1's own), in two lines, both passed by name to LP-G2 with their traces (the ship file's known-reds
and queued row; the orchestrator, 2026-11-23: a real gap in LP-G1's player, not a fixture premise, not mended in FC2):
- Its town is the playtest's probe town played to m245 (probePlay()), and its premises are found in main's path: on main the old
  skip holds the landlords for land 17 of 60 months, first with no home on site at m283, and the watched skip never. FC2 moves
  the path (section 5): its town at m245 is 1,285 people, 0.9274 built on against the line 0.9295, growth 1.024620 over HORIZON
  (main's 1.039056), and Manufacturing and Agriculture are held for land from the skip's first month.
- Section 1: with the industry held, jobs do not grow; the landlords decline (rent cover 1.10-1.19x against the lender's 1.20x)
  or hold "housing ahead of jobs", and are held for land only at m291-m292, with homes for 252 on site.
- Section 2: Manufacturing's order is a 480,000 sq ft Fabrication Shop, about 10% of the ground, more than the look's reserve
  (HORIZON months of growth with the slack). The m269 look bought the town to 0.8946 built on; the Fabrication Shop took it at
  m270 (0.9923), and the landlords were held for land m271-m275, no home on site, until the m275 look; never after (Fabrication
  Shops again at m281, m283 and m289 took the later looks' ground too). keepGroundAhead() sizes a look by the used share alone,
  not by what the sectors held for land asked for (`Game.getLandBlockedNeeds()`): LP-G2, the look counting every sector's
  blocked needs. FC2 changes no ground, look or landlord rule.

The dry run on 0a1b64a0b (cbs-gate-FC2-jDTdtFcP, 2026-11-24) stopped on three harnesses main's gate passes, each traced by a
helper (contract 4) and each checked against the world first (Jerus, 2026-11-23). No model change; harnesses only:
- PegAnchorCheck 9 (section 9's town, InterestAbroadCheck.town()): a real effect. The crews' bill is an import of services, and a
  peg's reserves are judged in months of imports of goods and services (the IMF's import cover; the rule behind VAULT_FLOOR,
  Krugman 1979). The town's month of imports stood at 3,770 (the crews' bill 1,996 of it) against a vault of 653, the floor
  (VAULT_FLOOR months of the trailing 1,885) over the vault, so the hold's sale took it under and the peg broke by the vault in
  month 1 (0.9976, then 1.2712 and 1.2416 against the held 0.9959). The FC2 top-up read the trailing imports before month 1, when
  the town had no trade history and they read 0. With the crews off, the run matched main's to the digit. Now the town plays to its
  first month of imports before the peg (a fixture line says so). Two lines assert the real law: the month's imports the floor
  reads include the crews' bill (1,996 of 3,770), and the floor is VAULT_FLOOR months of the trailing imports, which rose with
  them (1,885 from 0). The top-up then acts from month 1 (21,968), and the peg holds three months (1.0081, 0.9758, 1.0134).
- ConstructionTrapCheck 1 (section 9, red since the merge of 0.9.194: the gate had not rerun it since): the fixture found its
  room. The room the Houses meet in the twin that never cancelled is T x (S' - S) + S, where S and S' are the sites' month at the
  order and a month on. Before PW2 it was 230 points; after it nothing, S = T = 21 and S' = 20, because PW2 dated the Wind Farm to
  1980 and the founding's first plant became Oil-Fired. So every House went abroad, and no split was shown. This is a contractor's
  real rule (a book at its limit sends the order on), not a defect. Both twins now stand a depot while the room is under
  ROOM_LEAST of the Houses' points, then roads while it is at or over ROOM_MOST: on the merged town 1 depot and 47 roads, the room
  294.0 of 400. A fixture line asserts it caused the room, and a new line asserts the split exactly: handed 106.0 against the
  Houses' 400 less the room.
- LongPlaytest 2 (MoneyFixtures, the orphaned coupon): the fixture found its bond. On FC2's path the founding city's landlords'
  order went to the crews from m2 (their bills about 15,350 to m12), and Industry was short of cash from m7. Its first bond, for a
  shortfall of $20.82, was $0.80 (the record 0.0731), and every cell halved left D 0.0366 with its coupon 0.000033, under a cent.
  The orphaned line was exact and the books closed. The bond is now chosen by its coupon: the soonest whose coupon on half its
  record clears twice the visible line (a fixture line). That is Retail's, due m129 (the record 5.41, 1 of 59 cells halved), and
  a second fixture line asserts the coupon on D is visible.

The merge of main at 0.9.196 (R6f; 69402a412) and its dry run (cbs-gate-FC2-IWtpy02T, 2026-11-24) stopped on one red,
CopperRefiningCheck's "seed 1's 2,400 months stand at most ROD_MILLS_STANDING (1) Rod Mill": 181 Rod Mills, 40 smelters, 46
refineries and 60 scrap refineries (main 0.9.196 on the box: 0). It is the box's path only. On the PC the same commit, arguments
and data stand 0 (33 min on 0a1b64a0b, 40 on 0e1c26bf6), the JVMs' arithmetic parting the 2,400 months (box OpenJDK 21.0.13,
PC Oracle 21.0.4). There, Copper Refining's borrowing ban (BusinessDebtManager.java:3172) holds the Rod Mill, the planner's best
candidate for 1,064 months (it would replace 7,640 t of imported rod a month). A hooked run on the box (one JVM under the box
lock, held shared, in a scratch copy since removed; the orchestrator's leave) reproduced it exactly:
- No ban was ever on. The chain starts at m858 with a Scrap Copper Refinery at a 5% margin over its hurdle; the first Rod Mill
  follows at m889, the first refinery at m1035 and the first smelter at m1091, one works a month after that
  (ProcessingSector.plan(), ProcessingSector.java:491-499), paid by cash and borrowing, none shed.
- From about m1200 the cathode and rod tallies repeat every three months: cathode exports of 494k t, then imports of 82k t with
  992k t of rod made, then an idle month. The planner prices a works' output at the import price up to the month's imports and its
  feed at the export price up to the month's exports (SpreadPlanner.java:166-190), and reads the peak month as lasting. A Rod Mill
  earns +54k, -83k and -33k over the cycle, a mean of -20k against a hurdle of about 45k, and still passes (earns over hurdle 1.08
  to 14.95). Each stage's flush month is the next stage's import to replace, so the stages feed one another.
- CopperRefining declares no feedRefusal (HeavyIndustry.feedRefusal(), HeavyIndustry.java:332-339, has one), so standing
  capacity never refuses an order: 180 mills stand for 1.44 Mt a month against 0.69 Mt of cathode made, at about 25% of
  nameplate. The idle-shed count resets in each working month (SpreadPlanner.noteMonth()), so none is retired.
- FC2 changes none of ProcessingSector, SpreadPlanner, CopperRefining or the goods market. The foreign crews took points in 19 of
  the 355 orders, and the queue premium was 1.000 on most orders after m1089 (1.14 at the first Rod Mill, against a margin of
  4.9 times).
A runaway, not a real outcome (the orchestrator's reality check): main's planner, which FC2's path exposes. It is filed to CY1
(the planner over the cycle), whose remedy must read the margin over the cycle (a trailing year), not only the tally. Beside it
stand a feed gate on the standing works and the idle shed.

The third dry run (2026-11-24): 6665d8aa6 merged with origin/main (b1ccd1c27, two QUEUE-only commits past 0.9.196) on the box,
/home/jerus/cbs/runs/cbs-gate-FC2-qmeDdMqj: gate exit 0, known red only (CopperRefiningCheck, CurrencyCheck, DeathRecordCheck,
DenominationCheck, GroundSkipCheck, HoldersCheck, InvestCheck, KeptCheck, PlanCheck, PopulationCheck, RailDrawCheck, RoadCheck),
unnamed.txt empty. REV-FC2d (Codex) said SHIP on the three harness mends; the ensemble FC2-m195 stands (R6f's default trace is
0.9.195's to the byte, the orchestrator).
