# The code map

Generated 2026-10-24 by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).

**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.

**The tree:** 407 files, 437,439 lines, 18,541 methods, 4,089 constants. `GameVersion.VERSION` is "0.8.40", `SAVE_FORMAT` 45.

## model (191 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgeBand.java](AgeBand.md) | 181 | 12 | The six ages of a resident. | 36 |
| [Auction.java](Auction.md) | 460 | 28 | The city's paper sold at auction: the bidders, each by its own rule in the game, and the one yield that clears them. | 25 |
| [AuctionTally.java](AuctionTally.md) | 145 | 11 | The city's auctions counted: how many sales the treasury has booked, what they sold and what they asked, how often the stop sat more than a point o... | 4 |
| [AutoBuilder.java](AutoBuilder.md) | 2,151 | 144 | Automatic building (0.7.73): the city's own works ordered for the player once a month - power, water, the roads and their lines, care, schools and ... | 8 |
| [BalanceSheet.java](BalanceSheet.md) | 201 | 30 | A simple balance sheet for one business in the city. | 6 |
| [Bank.java](Bank.md) | 6,555 | 448 | The city's commercial bank: every loan in it, and every default. | 60 |
| [BoatSchedule.java](BoatSchedule.md) | 470 | 32 | The month's ships, as a pure function of time: which calls the city's sea trade makes, when each arrives, and where each boat is at any moment (0.7... | 15 |
| [BondMarket.java](BondMarket.md) | 2,091 | 172 | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participa... | 20 |
| [BookDesk.java](BookDesk.md) | 159 | 17 | The debt book as a desk: every figure the book page and a line's page draw (CD6, spec-city-debt.md section 7, boards 1 and 2), a pure model of what... | 4 |
| [BuildAdvice.java](BuildAdvice.md) | 2,197 | 110 | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure befor... | 25 |
| [BuildCard.java](BuildCard.md) | 1,805 | 87 | One build card's figures, for every one of the buildings in buildings.json (0.7.25; 143 of them since FR3, the Bulk Berth): what it gives the city ... | 25 |
| [BuildLog.java](BuildLog.md) | 162 | 10 | What the city has gained, and when. | 5 |
| [BuildingCatalog.java](BuildingCatalog.md) | 548 | 19 | Reads the building definitions out of buildings.json. | 12 |
| [BuildingInstance.java](BuildingInstance.md) | 40 | 4 |  | 1 |
| [BuildingManager.java](BuildingManager.md) | 7,912 | 206 | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed the... | 101 |
| [BuildingType.java](BuildingType.md) | 255 | 0 |  | 77 |
| [BuildingVisual.java](BuildingVisual.md) | 541 | 24 | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which ... | 23 |
| [BuildingsStacks.java](BuildingsStacks.md) | 466 | 42 | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. | 25 |
| [BuildingsTemplate.java](BuildingsTemplate.md) | 1,223 | 129 | One kind of building, and what it costs to put up. | 154 |
| [BusinessDebt.java](BusinessDebt.md) | 93 | 12 | Base class for private-sector borrowing. | 9 |
| [BusinessDebtManager.java](BusinessDebtManager.md) | 3,626 | 235 | Private-sector credit. | 29 |
| [BusinessInvestment.java](BusinessInvestment.md) | 1,224 | 60 | Capacity planning for the private sector. | 67 |
| [BusinessLoan.java](BusinessLoan.md) | 72 | 7 | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. | 7 |
| [Buyback.java](Buyback.md) | 357 | 22 | The city's paper bought back in a reverse auction: every holder offers what it holds in slices, each at the price where its own buying rule would s... | 8 |
| [CallTree.java](CallTree.md) | 330 | 18 | What a callable bond is worth, and what the right to call it costs: a Ho-Lee short-rate tree on monthly steps, fitted to the city's curve (CD4, doc... | 4 |
| [CapSetting.java](CapSetting.md) | 78 | 8 | A cap on the yield an automatic sale takes, saved and said (D28). | 5 |
| [CapitalFlows.java](CapitalFlows.md) | 748 | 47 | Hot money: what comes in chasing a spread, and what happens when it leaves. | 19 |
| [CareType.java](CareType.md) | 170 | 7 | What a healthcare building actually does. | 29 |
| [CentralBank.java](CentralBank.md) | 954 | 95 | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. | 24 |
| [ChartModel.java](ChartModel.md) | 717 | 49 | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks o... | 16 |
| [CityBasket.java](CityBasket.md) | 120 | 2 | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. | 1 |
| [CityCalendar.java](CityCalendar.md) | 146 | 13 | Turns the month counter into a date a person can hold in their head. | 27 |
| [CityLand.java](CityLand.md) | 1,295 | 93 | The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase sinc... | 42 |
| [CityMap.java](CityMap.md) | 7,178 | 369 | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves,... | 21 |
| [CityNeeds.java](CityNeeds.md) | 1,099 | 48 | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, t... | 28 |
| [CityRuns.java](CityRuns.md) | 4,265 | 214 | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with ... | 17 |
| [CityShore.java](CityShore.md) | 244 | 21 | The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over o... | 6 |
| [ClockPace.java](ClockPace.md) | 259 | 16 | The clock's pace (UP2): how much of each frame the months may take, how often the screen is redrawn and what the redraw cost, and what speed the ci... | 2 |
| [ConstructionControl.java](ConstructionControl.md) | 901 | 66 | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it... | 21 |
| [Consumption.java](Consumption.md) | 539 | 24 | What a household eats, and what changes it. | 5 |
| [CorporateBond.java](CorporateBond.md) | 248 | 29 | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. | 13 |
| [Crime.java](Crime.md) | 525 | 61 | Crime, the police who deter and catch it, and the prisons that hold who they catch. | 16 |
| [Currency.java](Currency.md) | 263 | 21 | What the city's money is called, and how it is written. | 22 |
| [DataSave.java](DataSave.md) | 2,437 | 382 | sections: land and ore, the shedding warning... | 8 |
| [Debt.java](Debt.md) | 1,202 | 100 | One piece of city paper. | 38 |
| [DebtManager.java](DebtManager.md) | 3,141 | 245 | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of m... | 40 |
| [DebtQuote.java](DebtQuote.md) | 308 | 12 | What a loan would cost, worked out BEFORE the player agrees to it. | 21 |
| [DecisionLog.java](DecisionLog.md) | 213 | 24 | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). | 32 |
| [DemolitionLog.java](DemolitionLog.md) | 142 | 10 | What the city has lost, and when. | 7 |
| [Denomination.java](Denomination.md) | 199 | 12 | The currency's unit, and the power to lop zeros off it. | 4 |
| [Deposit.java](Deposit.md) | 263 | 13 | One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a pl... | 53 |
| [DistrictPicture.java](DistrictPicture.md) | 309 | 17 | A district's picture for the far views: 32 x 32 pixels, a pixel for 8 x 8 plots (240 m), each the colour of one of its 64 plots - the plot a fixed ... | 3 |
| [DistrictPlan.java](DistrictPlan.md) | 6,057 | 217 | One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it... | 14 |
| [EarthData.java](EarthData.md) | 962 | 87 | The reader of earth-2.bin (and of earth-1.bin, whose layers it holds), the real Earth's ground as data: its layers' 1-degree tiles inflated into an... | 23 |
| [EarthProj.java](EarthProj.md) | 446 | 34 | The real Earth flattened round a city's spot: Lambert's azimuthal equal-area projection on a sphere, forward and inverse, the world's plots in metr... | 13 |
| [EarthSite.java](EarthSite.md) | 674 | 43 | A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the s... | 12 |
| [EarthTerrain.java](EarthTerrain.md) | 2,738 | 142 | The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the... | 17 |
| [EconomyManager.java](EconomyManager.md) | 2,535 | 253 | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. | 55 |
| [Education.java](Education.md) | 1,093 | 59 | Who the city teaches, what it costs, and why anybody bothers. | 12 |
| [EducationType.java](EducationType.md) | 191 | 11 | What a school actually teaches. | 28 |
| [Equity.java](Equity.md) | 1,170 | 97 | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. | 44 |
| [Exchange.java](Exchange.md) | 1,874 | 150 | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the last trade. | 22 |
| [Expectations.java](Expectations.md) | 287 | 16 | What the city expects prices to do, and how far it believes the central bank. | 11 |
| [FamilyModel.java](FamilyModel.md) | 2,211 | 102 | How the city's people are arranged into households, and what each earns. | 20 |
| [FamilyStructure.java](FamilyStructure.md) | 119 | 7 | The shapes a household comes in. | 30 |
| [FloatingRateNote.java](FloatingRateNote.md) | 218 | 20 | A floating-rate note: a coupon every month on the whole face, the month's policy rate plus a margin fixed at the sale, and the face at the end - at... | 6 |
| [ForeignAccounts.java](ForeignAccounts.md) | 2,563 | 166 | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. | 34 |
| [Formats.java](Formats.md) | 122 | 9 | The few formats a sector needs to describe itself, without the toolkit. | 49 |
| [Founding.java](Founding.md) | 560 | 49 | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. | 94 |
| [FreightView.java](FreightView.md) | 470 | 23 | The Freight sector's divisions as the screens draw them (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this mon... | 5 |
| [FuelSplit.java](FuelSplit.md) | 229 | 8 | FUEL, split into the PETROL and DIESEL it became: a save written before 0.7.76 converted once, on its load, before anything is restored (batch O1; ... | 2 |
| [FundLedger.java](FundLedger.md) | 766 | 92 | The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in incom... | 10 |
| [FundView.java](FundView.md) | 863 | 38 | The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, it... | 5 |
| [Game.java](Game.md) | 18,275 | 765 | sections: THE FOUNDING RESERVE (2026-09-21), THE FOUNDING RECORD (0.7.10)... | 204 |
| [GameFiles.java](GameFiles.md) | 469 | 40 | Where the game keeps its files, and how it writes them. | 126 |
| [GameLog.java](GameLog.md) | 348 | 20 | Everything the game prints, written somewhere a player can find it. | 10 |
| [GamePrefs.java](GamePrefs.md) | 216 | 19 | How the player likes the window, kept between runs. | 4 |
| [GameVersion.java](GameVersion.md) | 8,211 | 6 | What build this is, and what shape its saves are. | 17 |
| [Gateways.java](Gateways.md) | 207 | 11 | The city's two gateways - the highway's and the railway's - as one point on the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, wit... | 2 |
| [Geology.java](Geology.md) | 1,298 | 49 | The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's nam... | 9 |
| [GlobeFrame.java](GlobeFrame.md) | 851 | 75 | The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a dra... | 8 |
| [GlobePicture.java](GlobePicture.md) | 411 | 33 | The planet's picture: a code a texel on a latitude-longitude grid at ARCMIN - a World class and, since ET1, its relief - made from the Earth's data... | 8 |
| [Good.java](Good.md) | 1,416 | 23 | A thing that can be made, bought, held, imported and exported. | 132 |
| [GoodsMarket.java](GoodsMarket.md) | 493 | 51 | Where one good clears between whoever makes it and whoever wants it. | 54 |
| [GridConversion.java](GridConversion.md) | 545 | 43 | A saved city's land put on the block grid: a format-31 save's lanes snapped to whole blocks with its fields deciding, or an older save's one figure... | 5 |
| [GridOffers.java](GridOffers.md) | 339 | 26 | The city's offers on the block grid: on each of its four sides six places, left to right facing out, and in each place one standing offer - a recta... | 8 |
| [Health.java](Health.md) | 403 | 20 | How much of the workforce is off sick this month. | 20 |
| [Healthcare.java](Healthcare.md) | 958 | 55 | The city's healthcare service: what it costs, what it collects, and what it does with the dead. | 24 |
| [HistoryGrapher.java](HistoryGrapher.md) | 115 | 1 |  | 1 |
| [HistorySave.java](HistorySave.md) | 1,776 | 58 | Every month the city has ever lived, one number at a time. | 30 |
| [Household.java](Household.md) | 1,413 | 122 | Every household of one shape at one pay tier, as one ledger. | 37 |
| [HouseholdAccounts.java](HouseholdAccounts.md) | 1,365 | 102 | The city's residents, treated as one household. | 14 |
| [HouseholdBalance.java](HouseholdBalance.md) | 4,670 | 242 | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. | 52 |
| [Inbox.java](Inbox.md) | 714 | 29 | Everything the city has had to say for itself, newest first. | 4 |
| [IndustryPicture.java](IndustryPicture.md) | 1,615 | 85 | Each materials industry's month drawn as a picture, laid out to scale (0.7.143, batch I12; docs/studies/spec-materials.md 2.13, the study's mockups... | 2 |
| [IndustryView.java](IndustryView.md) | 883 | 64 | Each materials industry's month as a picture (0.7.141, batch I11; docs/studies/spec-materials.md 2.13): where what it worked came from - its fields... | 3 |
| [InflationLinkedBond.java](InflationLinkedBond.md) | 311 | 28 | An inflation-linked bond: a REAL coupon every month on a principal that follows the city's price index, and at the end the indexed principal, never... | 6 |
| [InfrastructureManager.java](InfrastructureManager.md) | 1,318 | 101 | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. | 28 |
| [InterchangeGround.java](InterchangeGround.md) | 169 | 9 | The ground a highway interchange keeps: a 19 x 19 plot reserve centred on each highway x highway crossing (a cloverleaf), an 11 x 11 one beside eac... | 5 |
| [InterimLoan.java](InterimLoan.md) | 35 | 1 | Interim financing: the bank's loan to a sector in the month it defaulted, for what the month's bills left unpaid after its debt was written down, r... | 4 |
| [Investor.java](Investor.md) | 44 | 6 | Whoever is paying for a building. | 3 |
| [JobType.java](JobType.md) | 20 | 0 |  | 74 |
| [LabourMarket.java](LabourMarket.md) | 855 | 45 | What labour costs, and why it costs that. | 15 |
| [LandConversion.java](LandConversion.md) | 217 | 7 | A saved city's land put on the block grid, and a city's land drawn again to hold a new figure: a format-31 save's lanes snapped to whole blocks, an... | 7 |
| [LandGrid.java](LandGrid.md) | 392 | 30 | The ground a city owns, as a region quadtree over the world's plots: which holding owns each plot, and how much of any block or rectangle of plots ... | 14 |
| [LandManager.java](LandManager.md) | 2,262 | 185 | The city's land: what it owns, what is built on, and what it sells. | 70 |
| [LandMap.java](LandMap.md) | 1,356 | 75 | The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectang... | 12 |
| [LandMarket.java](LandMarket.md) | 1,092 | 56 | The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's ... | 27 |
| [LandParcel.java](LandParcel.md) | 390 | 52 | One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, w... | 33 |
| [LegacyLand.java](LegacyLand.md) | 514 | 44 | The city's land as spec-land drew it, read-only: a square centre round the founding site and forty lanes of wedges pushed out band by band - the ge... | 8 |
| [LongTermBond.java](LongTermBond.md) | 208 | 21 | A bond: a fixed coupon every month on the whole face, and the face at the end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2). | 18 |
| [LuxuryCounter.java](LuxuryCounter.md) | 155 | 10 | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. | 1 |
| [MapFrame.java](MapFrame.md) | 368 | 47 | The map view's arithmetic without the toolkit: where the view looks and how close, how a drag, a notch of the wheel and a held key (0.7.135) move i... | 16 |
| [MapTiles.java](MapTiles.md) | 1,060 | 49 | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again ... | 11 |
| [Markets.java](Markets.md) | 513 | 26 | Every goods market in the city, and the month they clear in. | 38 |
| [MediumTermBond.java](MediumTermBond.md) | 255 | 18 | The amortizing bond: the workhorse of municipal finance - at 5 to 30 years in fives, one coupon, each sale its own line (CD2). | 11 |
| [Migration.java](Migration.md) | 1,312 | 56 | Why people move to this city, and the much narrower question of why they leave. | 16 |
| [MiniMapFrame.java](MiniMapFrame.md) | 316 | 32 | The minimap's arithmetic without the toolkit: what the stamp at the rail's foot and its hover preview frame - the city's built-up ground, the groun... | 6 |
| [MoneyAudit.java](MoneyAudit.md) | 1,271 | 28 | Where the money went this month, and whether it all went somewhere. | 49 |
| [Mortgage.java](Mortgage.md) | 523 | 38 | An insured mortgage on a new residential building: a level payment every month over a forty-year amortization, at a rate fixed for a ten-year term ... | 16 |
| [Motoring.java](Motoring.md) | 365 | 21 | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it - and since 0.7.49 the drivers' fuel a... | 6 |
| [NationalAccounts.java](NationalAccounts.md) | 1,520 | 117 | The city's GDP, measured properly, plus the government's own books. | 33 |
| [Notice.java](Notice.md) | 119 | 17 | One thing the city needs told about, and whether anybody has looked at it. | 12 |
| [Offending.java](Offending.md) | 169 | 5 | Who is at risk of offending, sorted by reason, and the thefts handed to them. | 1 |
| [OilView.java](OilView.md) | 1,347 | 71 | The oil industry on one page (0.7.96, batch O12; docs/studies/spec-oil.md 2.13): the city's wells by kind with what they would lift over the next t... | 4 |
| [OldMetal.java](OldMetal.md) | 418 | 40 | The city's old steel: what it throws out each month that a Scrap Yard can buy and sort into scrap - its scrapped cars, its demolitions, its fabrica... | 17 |
| [OrderBook.java](OrderBook.md) | 459 | 52 | A limit-order book for one instrument: buy and sell orders from named participants, each a price and a quantity, matched by price-time priority. | 18 |
| [OrphanHousehold.java](OrphanHousehold.md) | 39 | 10 | Children no family holds, by age band. | 3 |
| [OutwardInvestment.java](OutwardInvestment.md) | 706 | 46 | Outward investment: what the city's businesses do with money the bank will not pay for. | 15 |
| [PayTier.java](PayTier.md) | 125 | 5 | The six pay levels a household can be in. | 56 |
| [PolicyPreview.java](PolicyPreview.md) | 583 | 49 | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and ... | 10 |
| [PopulationCohorts.java](PopulationCohorts.md) | 604 | 35 | The city's age pyramid, and since the switch, the city's POPULATION. | 27 |
| [PopulationManager.java](PopulationManager.md) | 1,242 | 61 | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). | 31 |
| [Ports.java](Ports.md) | 678 | 54 | The city's ports: the berths its sea terminals stand, the share of each kind of cargo they take off the lorries and the railway, and the tonnes tha... | 45 |
| [PriceIndex.java](PriceIndex.md) | 778 | 37 | What a month costs a household, against what it cost at founding. | 20 |
| [PrisonerHousehold.java](PrisonerHousehold.md) | 79 | 17 | Adults serving a sentence, as one ledger: the prisoners' ledger. | 5 |
| [RefineryView.java](RefineryView.md) | 1,612 | 82 | The refinery's month as a picture (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): the crude it ran and where that came from, the column's cuts,... | 8 |
| [Resource.java](Resource.md) | 218 | 13 | The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields ... | 69 |
| [RetiredHousehold.java](RetiredHousehold.md) | 51 | 6 | A household with nobody of working age in it: a senior or an elder, alone or as a couple. | 3 |
| [Rollover.java](Rollover.md) | 378 | 34 | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). | 18 |
| [SafetyType.java](SafetyType.md) | 68 | 4 | What a safety building does: police, or prison cells. | 13 |
| [SalesTaxLedger.java](SalesTaxLedger.md) | 228 | 22 | The month's sales tax, as tax payable less input tax credits. | 5 |
| [SaveCopy.java](SaveCopy.md) | 54 | 5 | The small copies a save's snapshot is made of: a list or map copied, with each element copied too where the elements can change. | 9 |
| [SaveHeader.java](SaveHeader.md) | 75 | 12 | Just enough of a save to label it on the slot list. | 7 |
| [SaveWriter.java](SaveWriter.md) | 253 | 17 | The one thread saves are written on, for the whole JVM: it takes a finished snapshot of a city and turns it into the save, the map and the history ... | 3 |
| [SeaRoutes.java](SeaRoutes.md) | 480 | 17 | Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the o... | 4 |
| [Sector.java](Sector.md) | 3,168 | 229 | One business in the city, and the template every sector extends. | 125 |
| [SectorBooks.java](SectorBooks.md) | 866 | 47 | A month of books for every business in the city, and last month's too. | 22 |
| [SectorFlow.java](SectorFlow.md) | 261 | 17 | One business's month as a flow (0.7.30): what went in, what its plant made of it and what held the plant back, and what came out - each good's unit... | 4 |
| [SectorState.java](SectorState.md) | 274 | 6 | One sector, as a save carries it. | 12 |
| [SectorStatements.java](SectorStatements.md) | 1,025 | 64 | A business's month as formal statements (0.7.74): the income statement classified down through gross and operating profit, the balance sheet in cur... | 13 |
| [Sectors.java](Sectors.md) | 469 | 46 | Every sector in the city, in one order, by one name. | 115 |
| [ServicesManager.java](ServicesManager.md) | 378 | 26 | / | 7 |
| [ShadowBasket.java](ShadowBasket.md) | 201 | 1 | What a played city WOULD buy, measured against what it spends today. | 0 |
| [ShipShapes.java](ShipShapes.md) | 165 | 10 | How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, i... | 4 |
| [ShortTermTBill.java](ShortTermTBill.md) | 121 | 14 | A bill: borrow now, repay one lump, no coupon - at 1, 3, 6 or 12 months, each sale its own line (CD2). | 11 |
| [Sickness.java](Sickness.md) | 357 | 24 | Who has been sick, and for how long - and the ones it kills. | 10 |
| [SimulationEngine.java](SimulationEngine.md) | 216 | 5 | The order the month runs in: the sites advance, the roads, the posts and the wages, the people, then the economy and the services. | 1 |
| [SocialSecurity.java](SocialSecurity.md) | 150 | 9 | Contributions off every wage, and a pension for everyone too old to work. | 5 |
| [StrategicReserve.java](StrategicReserve.md) | 238 | 32 | The city's strategic reserve of crude oil: what it holds, what it paid for it, and what the player has told it to fill and release (0.7.85, batch O... | 15 |
| [StreetCurves.java](StreetCurves.md) | 152 | 8 | A street's smooth line from the plots it is laid on, and the plots that line's band reaches: pure - plots in, points in plots out, no plan and no s... | 4 |
| [StudentHousehold.java](StudentHousehold.md) | 57 | 10 | Full-time students, as one ledger. | 6 |
| [SupplierCredit.java](SupplierCredit.md) | 255 | 21 | What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time. | 8 |
| [TaxPolicy.java](TaxPolicy.md) | 1,749 | 109 | The city's tax rates - the revenue half of what the player actually decides. | 53 |
| [TempDirs.java](TempDirs.md) | 141 | 7 | The one place a harness, or a scratch city, gets a temporary folder from: one folder per JVM under the system temp folder, deleted when that JVM ex... | 75 |
| [TilePainter.java](TilePainter.md) | 868 | 42 | Paints one tile of the city map: from the tile's ground, what the city owns of it, its district's street plan through it - each street's kind, widt... | 16 |
| [TileRaster.java](TileRaster.md) | 3,953 | 148 | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixe... | 22 |
| [TimeSkipReport.java](TimeSkipReport.md) | 674 | 68 | What happened while you were not watching. | 6 |
| [Timing.java](Timing.md) | 119 | 9 | The one helper every timing assertion in the suite goes through: a figure on a clock against its budget, hard as the harness wrote it, or soft whil... | 14 |
| [TrackCurves.java](TrackCurves.md) | 346 | 26 | The railway's curves as one geometry, for the drawing and for the trains' routes alike (FX5, 0.8.18): a polyline's corners rounded with arcs of a r... | 9 |
| [Trade.java](Trade.md) | 40 | 4 | One fill: somebody sold somebody some units of a good at a price. | 30 |
| [TradeBand.java](TradeBand.md) | 252 | 21 | The freight band of what the city trades, split into its legs, as the Trade tab draws it (batch FR4; docs/studies/spec-freight-and-infrastructure.m... | 2 |
| [Traffic.java](Traffic.md) | 60 | 3 | The three things that move, which used to be one number. | 34 |
| [TrainSchedule.java](TrainSchedule.md) | 668 | 49 | The month's trains, as a pure function of time: which trains the city's railed tonnes make, when each reaches its yard, and where each one is at an... | 13 |
| [TrainShapes.java](TrainShapes.md) | 92 | 6 | How the map draws a train: each vehicle's body and its load's mark as points in plots about where it is and the way it heads, and their colours by ... | 3 |
| [TreasuryFund.java](TreasuryFund.md) | 1,102 | 150 | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand,... | 30 |
| [TreasuryJournal.java](TreasuryJournal.md) | 283 | 16 | The treasury's journal: every movement of the city's cash that is neither a budget line nor paper raised or repaid, recorded by name as it happens,... | 13 |
| [TreasuryLine.java](TreasuryLine.md) | 170 | 2 | Every kind of payment the treasury makes, and whether it is a promise. | 7 |
| [TruckSchedule.java](TruckSchedule.md) | 401 | 28 | The month's trucks, as a pure function of time: which loads Haulage's trucks carry, when each reaches its gate, and where each one is at any moment... | 5 |
| [TruckShapes.java](TruckShapes.md) | 95 | 4 | How the map draws a truck: its body as points in plots about where it is and the way it heads, and its colour by what it carries - what ui/MapView ... | 1 |
| [UnemployedHousehold.java](UnemployedHousehold.md) | 88 | 14 | Adults who are out of work, as one ledger per situation. | 7 |
| [Unemployment.java](Unemployment.md) | 607 | 40 | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. | 12 |
| [UtilitiesHandler.java](UtilitiesHandler.md) | 705 | 66 | sections: WATER SUPPLY, THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7)... | 23 |
| [WageBand.java](WageBand.md) | 170 | 7 | The four education bands the wage tax is set by. | 27 |
| [WorkingHousehold.java](WorkingHousehold.md) | 50 | 7 | A household with an earner in it, at one pay tier. | 5 |
| [World.java](World.md) | 2,116 | 114 | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and ri... | 75 |
| [WorldEconomy.java](WorldEconomy.md) | 422 | 18 | The rest of the world, which has its own inflation and did not use to. | 13 |
| [YearBook.java](YearBook.md) | 1,651 | 83 | The run, one line a year - for READING rather than for drawing. | 17 |
| [YieldCurve.java](YieldCurve.md) | 278 | 19 | The city's curve: a discount factor for every month to fifty years, built from the dial the market expects, the credit spreads, and the shape the t... | 10 |
| [Zoning.java](Zoning.md) | 280 | 29 | The city's zoning (0.7.107, batch Z1): ground held for one sector's buildings - a share of the city's dry ground or a size in square kilometres, at... | 15 |
| [ZoningView.java](ZoningView.md) | 356 | 20 | Policy › Zoning as figures (0.7.109, batch Z2; the project's spec-zoning.md 4, the zoning study's three boards): the hub's four figures, the city's... | 3 |

## sectors (30 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [Agriculture.java](Agriculture.md) | 415 | 12 | The fields, and what they cost the city in ground. | 5 |
| [Automotive.java](Automotive.md) | 307 | 7 | The automobile industry. | 4 |
| [BusinessServices.java](BusinessServices.md) | 274 | 9 | Somebody else's work, done here, paid for from outside. | 3 |
| [CementConcrete.java](CementConcrete.md) | 73 | 2 | Kilns and cement plants - Cement & Concrete (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.12 and 5's I7 row; the study's 6): stone's pr... | 5 |
| [CoalMining.java](CoalMining.md) | 445 | 23 | Coal mines and the plants that wash their coal - Coal Mining (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.10 and 5's I6 row; the study... | 9 |
| [Construction.java](Construction.md) | 991 | 54 | The builders. | 17 |
| [CopperMining.java](CopperMining.md) | 256 | 12 | Copper mines and the leach plants on oxide fields - Copper Mining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.4 and 5's I8 row; the s... | 6 |
| [CopperRefining.java](CopperRefining.md) | 251 | 12 | Smelters, refineries and rod mills - Copper Refining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12 and 5's I8 row; the stu... | 10 |
| [ExtractionSector.java](ExtractionSector.md) | 598 | 33 | A sector that digs or cuts one resource out of the ground the city owns: the template every extraction industry of the materials chains extends (0.... | 16 |
| [FoodIndustry.java](FoodIndustry.md) | 98 | 6 | The mills and the plants that feed the shops. | 4 |
| [FoodProcessing.java](FoodProcessing.md) | 567 | 12 | The plants between the farm and the shelf. | 2 |
| [Forestry.java](Forestry.md) | 632 | 35 | The crews that cut the city's forest, and the plantations they plant - Forestry (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.11 and 5'... | 12 |
| [Freight.java](Freight.md) | 2,035 | 95 | The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 3.1, D1). | 37 |
| [Haulage.java](Haulage.md) | 1,014 | 62 | Freight's Haulage division: the trucks that carry the city leg of every bulk tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.m... | 23 |
| [HeavyIndustry.java](HeavyIndustry.md) | 278 | 13 | The mills - Iron & Steel since 0.7.120, under the key it was saved by, "Heavy Industry": the arc furnaces, which melt scrap into steel, and since 0... | 30 |
| [LuxuryRetail.java](LuxuryRetail.md) | 547 | 23 | The luxury shops. | 5 |
| [Manufacturing.java](Manufacturing.md) | 366 | 14 | What the city makes out of its own steel, and ships. | 5 |
| [Materials.java](Materials.md) | 88 | 4 | The materials plant. | 1 |
| [Mining.java](Mining.md) | 85 | 4 | Iron mines - Iron Mining since 0.7.120, under the key it was saved by, "Mining" (Jerus's Q2, 2026-10-08: rename, keeping the saved keys). | 27 |
| [Oil.java](Oil.md) | 1,360 | 89 | Oil wells. | 18 |
| [PortDivision.java](PortDivision.md) | 363 | 27 | Freight's Ports division: the crews that work the city's berths, paid by the shippers who use them and paying the city for the quay (FR3, 2026-10-1... | 11 |
| [ProcessingSector.java](ProcessingSector.md) | 522 | 42 | A sector that turns goods into other goods in works it builds by what pays: the template every processing industry of the materials chains extends ... | 17 |
| [Quarrying.java](Quarrying.md) | 400 | 19 | Quarries - Quarrying (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.4, 2.10 and 5's I7 row; the study's 6): the third materials chain's ... | 9 |
| [RealEstate.java](RealEstate.md) | 964 | 91 | The landlords. | 18 |
| [RefineryFlow.java](RefineryFlow.md) | 372 | 29 | The refinery as a campus of units, and a month's flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3): the crude units' run cut into ... | 16 |
| [Refining.java](Refining.md) | 1,918 | 108 | The refinery. | 21 |
| [Restaurants.java](Restaurants.md) | 521 | 24 | The kitchens. | 7 |
| [Retail.java](Retail.md) | 1,352 | 102 | The shops. | 38 |
| [SpreadPlanner.java](SpreadPlanner.md) | 556 | 47 | The spread planner (0.7.82, batch O5; docs/studies/spec-oil.md 2.4, and docs/studies/spec-materials.md 2.6): what a processing sector orders - of t... | 32 |
| [WoodProducts.java](WoodProducts.md) | 107 | 2 | Sawmills, and the panel, pulp, paper and pellet mills - Wood Products (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.7, 2.8, 2.12 and 5... | 6 |

## interface (34 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [BankScreen.java](BankScreen.md) | 4,431 | 169 | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figu... | 3 |
| [BuildScreen.java](BuildScreen.md) | 5,526 | 184 | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.... | 14 |
| [CapChips.java](CapChips.md) | 190 | 8 | The highest yield a sale will take, as three chips and a yield box: fair plus a point (the default), a yield the player names, or Fill it (CD2, D28). | 2 |
| [CityBuilderSim.java](CityBuilderSim.md) | 55 | 2 | The way in. | 0 |
| [ConstructionScreen.java](ConstructionScreen.md) | 1,121 | 39 | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - pri... | 1 |
| [FinancesScreen.java](FinancesScreen.md) | 6,386 | 254 | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece an... | 7 |
| [FoundingScreen.java](FoundingScreen.md) | 1,357 | 60 | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. | 1 |
| [FundScreen.java](FundScreen.md) | 2,441 | 99 | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with it... | 2 |
| [GovernmentScreen.java](GovernmentScreen.md) | 2,457 | 105 | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, w... | 2 |
| [HistoryScreen.java](HistoryScreen.md) | 3,134 | 109 | City History: the city as a shape over time. | 10 |
| [Icons.java](Icons.md) | 541 | 6 | The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one per category, and the few the frame draws (the money block, the "Needs... | 18 |
| [InfrastructureScreen.java](InfrastructureScreen.md) | 1,982 | 73 | The infrastructure tab: the roads, the trams, the freight and the ports - four pages under the five figures the whole tab is about, each page led b... | 1 |
| [Kept.java](Kept.md) | 620 | 63 | A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a screen's code made at its last draw, handed back to the... | 8 |
| [KeptBench.java](KeptBench.md) | 684 | 61 | The People page on a scene of its own - no stage, no window - drawn kept and built new, for KeptCheck (study UP4): the harness's hands on the page,... | 1 |
| [Ladder.java](Ladder.md) | 354 | 26 | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying... | 14 |
| [LandScreen.java](LandScreen.md) | 2,186 | 88 | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.... | 3 |
| [Levers.java](Levers.md) | 145 | 3 | The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36) - the dial on a card with what it does beside it, which every dial on t... | 3 |
| [MapView.java](MapView.md) | 3,041 | 134 | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's ... | 3 |
| [MiniMap.java](MiniMap.md) | 471 | 20 | The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288... | 1 |
| [Money.java](Money.md) | 384 | 30 | Every figure the interface prints as money, in one place. | 11 |
| [Palette.java](Palette.md) | 726 | 27 | Every colour, size and spacing this game is allowed to use, in one place. | 29 |
| [PeopleScreen.java](PeopleScreen.md) | 4,562 | 156 | The People tab and its second page, Household money. | 3 |
| [Pieces.java](Pieces.md) | 5,558 | 241 | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals ba... | 17 |
| [PolicyScreen.java](PolicyScreen.md) | 5,320 | 270 | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it w... | 10 |
| [SectorScreen.java](SectorScreen.md) | 6,295 | 246 | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with la... | 6 |
| [ServicesScreen.java](ServicesScreen.md) | 3,167 | 125 | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one p... | 4 |
| [Statement.java](Statement.md) | 482 | 23 | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank... | 27 |
| [StatementView.java](StatementView.md) | 587 | 36 | A formal statement on the page (0.7.74): SectorStatements' rows set as an accountant sets them - a title block, the columns (note, this month, last... | 2 |
| [SummaryScreen.java](SummaryScreen.md) | 1,545 | 33 | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen... | 10 |
| [TimeChart.java](TimeChart.md) | 1,560 | 85 | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, th... | 9 |
| [TradeScreen.java](TradeScreen.md) | 3,332 | 144 | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what ... | 4 |
| [UiProbe.java](UiProbe.md) | 726 | 23 | What the window costs the JavaFX thread as a city ages, measured: it grows the playtest's city to the months it is given, opens the real window on ... | 0 |
| [UserInterface.java](UserInterface.md) | 6,645 | 177 | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating a... | 26 |
| [ZoningScreen.java](ZoningScreen.md) | 1,087 | 71 | Policy › Zoning (0.7.109, batch Z2): the fifth Policy area - the city's ground and its zones on one page, a sector's zone on its own page of dial c... | 2 |

## harnesses (139 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgricultureCheck.java](AgricultureCheck.md) | 413 | 7 | The ground under the loaf. | 0 |
| [AllChecks.java](AllChecks.md) | 203 | 4 | Runs every harness, one JVM each, and says which failed. | 1 |
| [AuctionCheck.java](AuctionCheck.md) | 1,654 | 42 | The city's paper sold at auction: one clearing yield, and what it costs. | 0 |
| [AutoBuildCheck.java](AutoBuildCheck.md) | 1,570 | 35 | Automatic building (0.7.73, batch N4): AutoBuilder's month held to its rules, in a city it builds for decades and in fixtures that cause each of th... | 0 |
| [BankCheck.java](BankCheck.md) | 4,325 | 22 | The commercial bank, and the families it discharges. | 1 |
| [BondCheck.java](BondCheck.md) | 1,581 | 37 | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for co... | 2 |
| [BooksCheck.java](BooksCheck.md) | 299 | 3 | Verifies a sector's income statement and balance sheet, off the template. | 0 |
| [BuildAdviceCheck.java](BuildAdviceCheck.md) | 1,431 | 47 | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city -... | 2 |
| [BuildCardCheck.java](BuildCardCheck.md) | 2,075 | 38 | The build card (0.7.25): BuildCard's figures for every building in buildings.json (143 since FR3, the Bulk Berth) held to the model's own reads - t... | 0 |
| [BuildMenuCheck.java](BuildMenuCheck.md) | 288 | 2 | Verifies that every building in the game can describe itself. | 0 |
| [BuildersFirstCheck.java](BuildersFirstCheck.md) | 648 | 17 | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the month... | 0 |
| [BuildingDataCheck.java](BuildingDataCheck.md) | 833 | 4 | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. | 0 |
| [BusinessServicesCheck.java](BusinessServicesCheck.md) | 386 | 5 | The sector whose customer is not in the city. | 0 |
| [BuybackCheck.java](BuybackCheck.md) | 845 | 31 | The city's paper bought back in a reverse auction (CD4, spec-city-debt.md section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central ba... | 0 |
| [CalendarCheck.java](CalendarCheck.md) | 268 | 5 | The date on the status bar, and the log of what the city has finished. | 0 |
| [CallCheck.java](CallCheck.md) | 756 | 28 | Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be sold ... | 1 |
| [CapitalFlowCheck.java](CapitalFlowCheck.md) | 489 | 6 | Hot money: does it come for the right reason, and does it leave for one? | 0 |
| [CarCheck.java](CarCheck.md) | 1,060 | 8 | The cars: who buys one, what it costs them, and what it does to the road. | 2 |
| [CarryTradeCheck.java](CarryTradeCheck.md) | 303 | 5 | The carry trade: the other side of hot money, and the bank's first borrower. | 0 |
| [CementConcreteCheck.java](CementConcreteCheck.md) | 354 | 11 | Cement & Concrete, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.12, 4 and 5's I7 row; the study's - materials-chains-rese... | 0 |
| [CentralBankCheck.java](CentralBankCheck.md) | 2,073 | 28 | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its tw... | 0 |
| [ChartCheck.java](ChartCheck.md) | 959 | 28 | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the tick... | 0 |
| [ChildcareCheck.java](ChildcareCheck.md) | 553 | 20 | Childcare resized (0.7.71, batch N2): the three childcare centres, the build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS THE ... | 2 |
| [ClockCheck.java](ClockCheck.md) | 455 | 17 | The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and drops what it could not run, the header's speed sa... | 0 |
| [CoalMiningCheck.java](CoalMiningCheck.md) | 624 | 15 | Coal Mining, checked (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.4, 2.10, 4 and 5's I6 row; the study's - materials-chains-research.m... | 1 |
| [ConservationCheck.java](ConservationCheck.md) | 417 | 6 | Nothing is created and nothing is destroyed. | 0 |
| [ConstructionControlCheck.java](ConstructionControlCheck.md) | 846 | 21 | The player's hand on the construction queue (0.7.22): each of ConstructionControl's five rules held to its own arithmetic, in a played city. | 0 |
| [ConsumptionCheck.java](ConsumptionCheck.md) | 342 | 4 | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. | 0 |
| [ConversionCheck.java](ConversionCheck.md) | 702 | 20 | Saved cities put on the block grid (0.7.66, batch M2): the five saves the design was measured on, each converted - a format-31 save's lanes snapped... | 0 |
| [CopperMiningCheck.java](CopperMiningCheck.md) | 525 | 13 | Copper Mining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 4 and 5's I8 row; the study's - materials-chains-research.... | 2 |
| [CopperRefiningCheck.java](CopperRefiningCheck.md) | 538 | 13 | Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's - materials-chains-r... | 0 |
| [CreditCheck.java](CreditCheck.md) | 2,065 | 18 | Verifies private-sector credit: pricing, origination, rollover, cash conservation. | 0 |
| [CrimeCheck.java](CrimeCheck.md) | 344 | 8 | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. | 0 |
| [CurrencyCheck.java](CurrencyCheck.md) | 939 | 22 | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry ap... | 4 |
| [CurrencyRegimeCheck.java](CurrencyRegimeCheck.md) | 1,590 | 45 | Proves the D$'s four regimes - Float, Lean against falls (the default), Band and Peg to the US$ - hold the currency where they say and cost what th... | 0 |
| [CurveCheck.java](CurveCheck.md) | 736 | 36 | Proves the curve from the dial the market expects (CD1, spec-city-debt.md section 3): the expected dial's path, the zero rates it compounds to, the... | 0 |
| [DeathCareFirstCheck.java](DeathCareFirstCheck.md) | 508 | 17 | Death care first while the dead wait (0.7.129, batch T1-B): the rule that serves the city's own cemeteries and crematoria first out of its share of... | 1 |
| [DeathRecordCheck.java](DeathRecordCheck.md) | 222 | 5 | The running totals of the dead, by age and for the orphans and the unhoused. | 0 |
| [DenominationCheck.java](DenominationCheck.md) | 672 | 17 | A currency reform is a change of units, and this is how we know. | 0 |
| [DeskCheck.java](DeskCheck.md) | 1,224 | 31 | The debt desk's figures (CD6, spec-city-debt.md sections 7 and 8): the figures the debt screens used to work out for themselves, held to the model ... | 1 |
| [EarthCheck.java](EarthCheck.md) | 1,065 | 33 | The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and 8): EarthProj's projection, blend and areas held to the spec's measures, a... | 9 |
| [EarthDepositsCheck.java](EarthDepositsCheck.md) | 789 | 36 | Deposits where the geology says (0.8.12, batch EG1; docs/studies/spec-earth2-terrain.md 8.5): oil by a province's known petroleum, iron on shields ... | 0 |
| [EarthFoundingCheck.java](EarthFoundingCheck.md) | 683 | 18 | Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they a... | 0 |
| [EarthTerrainCheck.java](EarthTerrainCheck.md) | 2,449 | 59 | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three... | 3 |
| [EducationCheck.java](EducationCheck.md) | 1,465 | 11 | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. | 1 |
| [EnsembleCheck.java](EnsembleCheck.md) | 509 | 24 | The inflation ensemble is in the tree and still runs: its driver compiles against this tree, its two halves name the same policies, and the stored ... | 0 |
| [EquityCheck.java](EquityCheck.md) | 379 | 5 | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. | 0 |
| [ExchangeCheck.java](ExchangeCheck.md) | 981 | 22 | Verifies the exchange on the order book (0.7.12 round 2): what the desk posts and what it is not obliged to take, who trades with whom and at what ... | 1 |
| [ExchangeRateCheck.java](ExchangeRateCheck.md) | 1,098 | 36 | Proves the D$ is a float that follows its parity and swings like a real currency (XR1, 2026-10-21): parity is the basket with rent held to a third,... | 1 |
| [ExpectationsCheck.java](ExpectationsCheck.md) | 563 | 21 | Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the ... | 0 |
| [FloatLinkCheck.java](FloatLinkCheck.md) | 1,785 | 58 | The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's polic... | 1 |
| [FoodProcessingCheck.java](FoodProcessingCheck.md) | 442 | 7 | The third of the shelf that arrives already made. | 0 |
| [ForeignCheck.java](ForeignCheck.md) | 1,855 | 16 | The balance of payments, and whether the boundary it is drawn on is honest. | 4 |
| [ForeignDebtCheck.java](ForeignDebtCheck.md) | 895 | 9 | Borrowing in somebody else's money. | 3 |
| [ForestryCheck.java](ForestryCheck.md) | 704 | 21 | Forestry, checked (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.11, 4 and 5's I9 row; the study's - materials-chains-research... | 4 |
| [FoundingYardCheck.java](FoundingYardCheck.md) | 599 | 26 | The founding yard (PT1-M4, 2026-10-12): the works department's free 400 points a month are retired and every city founds with one Builders' Yard st... | 0 |
| [FreightViewCheck.java](FreightViewCheck.md) | 716 | 26 | FreightView, the model behind the Freight screens, held to the study's board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's frei... | 0 |
| [FundCheck.java](FundCheck.md) | 1,333 | 26 | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). | 1 |
| [FundLedgerCheck.java](FundLedgerCheck.md) | 965 | 25 | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, thr... | 0 |
| [GatewayCheck.java](GatewayCheck.md) | 1,056 | 36 | A city's gateway is the way the world is. | 3 |
| [GdpCheck.java](GdpCheck.md) | 959 | 5 | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock... | 0 |
| [GlobeCheck.java](GlobeCheck.md) | 964 | 31 | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, G... | 1 |
| [GridCheck.java](GridCheck.md) | 619 | 22 | The block grid (0.7.65, batch M1): the city's owned ground as a quadtree, every plot's owner exact against a raster, and its offers six a side - ap... | 2 |
| [GroceryCheck.java](GroceryCheck.md) | 459 | 10 | Groceries at a price, the shelf cleared stickily, and food assistance (0.7.43): the households' demand curve, the clearing price, who is handed the... | 0 |
| [HaulageCheck.java](HaulageCheck.md) | 1,243 | 34 | Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is billed once, by Haulage, at a quote the trucks' own books set, and the v... | 7 |
| [HealthCheck.java](HealthCheck.md) | 1,714 | 6 | Sickness: what it moves, and - much more importantly - what it does not. | 0 |
| [HistoryCheck.java](HistoryCheck.md) | 851 | 11 | Verifies the graph history: recording, alignment, and the round trip. | 0 |
| [HoldersCheck.java](HoldersCheck.md) | 1,530 | 20 | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is boug... | 0 |
| [HomeRailCheck.java](HomeRailCheck.md) | 886 | 27 | The railway's carriage of the city's own bulk, and the port yard, held to FR6's brief: a bulk tonne bought at home rides a siding at the city leg's... | 2 |
| [HouseholdCheck.java](HouseholdCheck.md) | 1,696 | 7 | Verifies the residents' books and the demolition log. | 0 |
| [HouseholdMemoryCheck.java](HouseholdMemoryCheck.md) | 278 | 6 | The households remember: the builder keeps what still fits. | 0 |
| [HousingCheck.java](HousingCheck.md) | 636 | 5 | Audits the three subsystems that describe the same housing, every month, and makes them agree. | 0 |
| [InboxCheck.java](InboxCheck.md) | 527 | 8 | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. | 0 |
| [IndustryViewCheck.java](IndustryViewCheck.md) | 1,218 | 32 | The materials pictograms' model, checked (0.7.141, batch I11; docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's I11 row): Ind... | 0 |
| [InfrastructureCheck.java](InfrastructureCheck.md) | 1,178 | 8 | The road network, from the curve up to a city that actually jams. | 0 |
| [InvestCheck.java](InvestCheck.md) | 1,606 | 8 | Verifies the private investment engine: forecasting, the demand tests, and the brake. | 0 |
| [IslandNetCheck.java](IslandNetCheck.md) | 834 | 32 | A highway net never stops while the city has ground, and a net is laid on every piece of ground the water parts (CL5). | 0 |
| [JunctionCheck.java](JunctionCheck.md) | 1,768 | 76 | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). | 3 |
| [KeptCheck.java](KeptCheck.md) | 887 | 15 | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exact... | 0 |
| [LabourCheck.java](LabourCheck.md) | 1,181 | 14 | Verifies the labour market: who can hold a job, and what it costs. | 1 |
| [LandCheck.java](LandCheck.md) | 3,023 | 43 | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. | 5 |
| [LandlordPortCheck.java](LandlordPortCheck.md) | 739 | 29 | The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds and keeps the sea terminals and Freight's Ports division works them. | 0 |
| [LongPlaytest.java](LongPlaytest.md) | 7,558 | 137 | A city played for four thousand months, the way a person plays. | 42 |
| [MainLineCheck.java](MainLineCheck.md) | 1,542 | 47 | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the c... | 1 |
| [ManufacturingCheck.java](ManufacturingCheck.md) | 609 | 8 | The ninth sector: what the city makes out of its own steel, and ships. | 0 |
| [MapCheck.java](MapCheck.md) | 9,523 | 250 | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's... | 8 |
| [MaterialsCheck.java](MaterialsCheck.md) | 1,537 | 31 | The materials chains, checked: the shared machinery every chain stands on (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.5-2.8 and 4), a... | 10 |
| [MiningCheck.java](MiningCheck.md) | 600 | 10 | Ore and scrap, from the bands they clear in to what an arc furnace makes of scrap and a mine of ore. | 9 |
| [MonetaryCheck.java](MonetaryCheck.md) | 917 | 10 | Money: what a basket costs, what the world charges, and what the rate does. | 3 |
| [MoneyCheck.java](MoneyCheck.md) | 570 | 5 | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. | 0 |
| [MortgageCheck.java](MortgageCheck.md) | 1,223 | 26 | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. | 1 |
| [NewGameCheck.java](NewGameCheck.md) | 1,291 | 16 | Does "Start New Game" actually start a new game? | 1 |
| [OilCheck.java](OilCheck.md) | 2,096 | 40 | Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K; the project's spec-land.md 2.7 and section 3's K entry). | 1 |
| [OilViewCheck.java](OilViewCheck.md) | 706 | 20 | The oil industry screen (0.7.96, batch O12; docs/studies/spec-oil.md 2.13): OilView's figures, words, chart and levers, held to the model's own rea... | 0 |
| [OrderBookCheck.java](OrderBookCheck.md) | 297 | 16 | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. | 0 |
| [OrderSearchCheck.java](OrderSearchCheck.md) | 550 | 19 | The three order searches against the countdowns they replaced (0.7.54). | 1 |
| [OutsideCheck.java](OutsideCheck.md) | 699 | 5 | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). | 0 |
| [PassThroughCheck.java](PassThroughCheck.md) | 601 | 19 | Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1, 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUG... | 0 |
| [PlanCheck.java](PlanCheck.md) | 3,859 | 129 | The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway... | 2 |
| [PlaytestFixCheck.java](PlaytestFixCheck.md) | 224 | 9 | Verifies the small fixes of a played city (PT1): words and the log, nothing the month does. | 0 |
| [PolicyCheck.java](PolicyCheck.md) | 475 | 5 | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. | 0 |
| [PolicyPreviewCheck.java](PolicyPreviewCheck.md) | 711 | 18 | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. | 0 |
| [PopulationCheck.java](PopulationCheck.md) | 1,426 | 11 | The demographics: do they hold together, and do they move the city the way they were told to? | 0 |
| [PortCheck.java](PortCheck.md) | 998 | 22 | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). | 3 |
| [QuarryingCheck.java](QuarryingCheck.md) | 638 | 13 | Quarrying, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.10, 4 and 5's I7 row; the study's - materials-chains-researc... | 1 |
| [RailCheck.java](RailCheck.md) | 742 | 8 | The railway: what it charges, who pays it, and what it does to the band. | 1 |
| [RailDepotCheck.java](RailDepotCheck.md) | 519 | 14 | The railway's stations: the Rail Depot, and the rules that put a station first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.m... | 1 |
| [RailDrawCheck.java](RailDrawCheck.md) | 1,637 | 73 | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). | 0 |
| [ReadPathCheck.java](ReadPathCheck.md) | 2,045 | 12 | Reading the city must not change the city. | 1 |
| [RefineryCheck.java](RefineryCheck.md) | 1,508 | 35 | The refinery's units and the flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread... | 0 |
| [RefineryViewCheck.java](RefineryViewCheck.md) | 631 | 23 | The refinery's pictogram (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): RefineryView's figures and its picture, held to the model's own reads ... | 1 |
| [RestaurantsCheck.java](RestaurantsCheck.md) | 575 | 6 | A meal out is food, and it is the same food. | 0 |
| [RestructureCheck.java](RestructureCheck.md) | 540 | 5 | Buying the city's own debt back, at what the paper is actually worth. | 2 |
| [RoadCheck.java](RoadCheck.md) | 1,189 | 35 | The roads over their lives, and a gravel road paved (0.7.70, batch N1): BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to the... | 0 |
| [RobustnessCheck.java](RobustnessCheck.md) | 432 | 5 | What the game does when something is already broken. | 0 |
| [SaveFileCheck.java](SaveFileCheck.md) | 3,249 | 15 | Verifies where saves go and how they are written. | 8 |
| [SaveSlotCheck.java](SaveSlotCheck.md) | 1,395 | 42 | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. | 0 |
| [ScaleCheck.java](ScaleCheck.md) | 1,189 | 31 | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them a... | 1 |
| [SectorBooksCheck.java](SectorBooksCheck.md) | 340 | 5 | Plays a city and audits every sector's statements, every month. | 1 |
| [SectorFlowCheck.java](SectorFlowCheck.md) | 308 | 11 | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held t... | 0 |
| [SectorStatementCheck.java](SectorStatementCheck.md) | 908 | 22 | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every mon... | 0 |
| [SicknessCheck.java](SicknessCheck.md) | 322 | 5 | The long sick: who stays sick, and who it kills. | 0 |
| [SkipReportCheck.java](SkipReportCheck.md) | 323 | 5 | Verifies the fast-forward summary. | 0 |
| [StaleCheck.java](StaleCheck.md) | 331 | 6 | The prose still describes the code: the firm half of `tools.Stale`, asserted, and `tools.Names` proved on a fixture. | 0 |
| [SupplierCreditCheck.java](SupplierCreditCheck.md) | 340 | 10 | The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit when the till cannot pay for it, owed for a month, repaid out of the s... | 0 |
| [TimingCheck.java](TimingCheck.md) | 470 | 24 | The harness for soft timing (batch TM1): a timing assertion fails hard, goes SOFT and uncounted under -Dcbs.timing=soft, and nothing else in the su... | 0 |
| [TradeBandCheck.java](TradeBandCheck.md) | 439 | 18 | TradeBand, the model behind the Trade tab's band in legs, held to the study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructur... | 0 |
| [TradeCostCheck.java](TradeCostCheck.md) | 616 | 6 | The wedge between what the world charges and what it pays, and what it is made of. | 0 |
| [TreasuryCheck.java](TreasuryCheck.md) | 1,603 | 33 | Plays a city and audits what the screens say the treasury did. | 2 |
| [TruckCheck.java](TruckCheck.md) | 441 | 10 | Proves that Haulage's loads are drawn as trucks on the map's roads, as a pure function of time, and that drawing them changes nothing in the game (... | 0 |
| [TruckRouteCheck.java](TruckRouteCheck.md) | 489 | 21 | Proves the trucks' routes (0.8.8, batch FR5, slice B; docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the map lets a truck driv... | 0 |
| [VanCheck.java](VanCheck.md) | 476 | 5 | The vans: what a sector needs, what it costs it, and what happens while it waits for them. | 0 |
| [WaterCheck.java](WaterCheck.md) | 529 | 10 | Sanity harness for water production, demand, throttling and billing - and, since 0.7.59, the fresh water limit and the desalination plant (sections... | 0 |
| [WellCheck.java](WellCheck.md) | 1,123 | 27 | The oil wells' lives (0.7.84, batch O7; docs/studies/spec-oil.md 2.6 and 4's WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spe... | 2 |
| [WoodProductsCheck.java](WoodProductsCheck.md) | 640 | 16 | Wood Products, checked (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.2, 2.3, 2.7, 2.8, 2.12, 4 and 5's I10 row; the study's - material... | 0 |
| [WorldCheck.java](WorldCheck.md) | 782 | 8 | The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its ... | 1 |
| [YearBookCheck.java](YearBookCheck.md) | 1,114 | 44 | Proves the year book folds each series the way that series has to be folded, and that the file says so. | 0 |
| [ZoningCheck.java](ZoningCheck.md) | 595 | 17 | Zoning and the forest law, the model (0.7.107, batch Z1): Zoning, and LandManager's ZONES AND THE FOREST LAW, held to the ledger, the treasury, the... | 0 |
| [ZoningViewCheck.java](ZoningViewCheck.md) | 466 | 18 | Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the b... | 0 |

## tools (13 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [CodeMap.java](CodeMap.md) | 177 | 3 | The code map: docs/map/README.md, one row per file, and docs/map/NAME.md for every file, listing its banner sections and every member with the line... | 1 |
| [Dials.java](Dials.md) | 55 | 2 | Every dial in the game on one page: docs/dials.md lists each static final constant in the tree with its value, its line, and the sentence above it. | 1 |
| [HarnessMap.java](HarnessMap.md) | 133 | 3 | What every harness asserts, in its own words: docs/harnesses.md. | 1 |
| [JavaScan.java](JavaScan.md) | 666 | 39 | A structural read of one Java source file, without a compiler. | 8 |
| [ManualToMarkdown.java](ManualToMarkdown.md) | 1,522 | 82 | The published manual as two files the repository keeps: docs/manual.md, which GitHub renders when it is clicked, and docs/manual.html, the page its... | 0 |
| [Maps.java](Maps.md) | 30 | 1 | Regenerates every generated document in one go: the code map, the dials, the month order and the harness map. | 0 |
| [MonthOrder.java](MonthOrder.md) | 167 | 6 | The month as a numbered list: docs/month-order.md walks the top-level statements of the methods that make up a month, in order, each with its line ... | 1 |
| [Names.java](Names.md) | 436 | 26 | Lists the names a branch's new prose uses that the tree does not declare. | 1 |
| [ReliefRenders.java](ReliefRenders.md) | 152 | 6 | Renders the map's relief for review: six PNG files drawn by the game's own painters, headless. | 0 |
| [SaveDump.java](SaveDump.md) | 133 | 4 | Looks inside a save without loading the game - or reading the file. | 0 |
| [SourceTree.java](SourceTree.md) | 154 | 13 | The whole source tree, scanned once, and the little that every generator shares: where the sources are, where the documents go, what area a file be... | 9 |
| [Stale.java](Stale.md) | 633 | 27 | Finds the comments and documents that have stopped being true, mechanically. | 2 |
| [Where.java](Where.md) | 71 | 1 | Finds a member by name anywhere in the tree and, if asked, prints it. | 0 |

