# The code map

Generated 2026-10-07 by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).

**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.

**The tree:** 272 files, 257,180 lines, 10,913 methods, 1,978 constants. `GameVersion.VERSION` is "0.7.64", `SAVE_FORMAT` 31.

## model (136 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgeBand.java](AgeBand.md) | 181 | 12 | The six ages of a resident. | 35 |
| [BalanceSheet.java](BalanceSheet.md) | 201 | 30 | A simple balance sheet for one business in the city. | 6 |
| [Bank.java](Bank.md) | 6,500 | 443 | The city's commercial bank: every loan in it, and every default. | 48 |
| [BondMarket.java](BondMarket.md) | 2,024 | 165 | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participa... | 18 |
| [BuildAdvice.java](BuildAdvice.md) | 1,146 | 61 | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure befor... | 19 |
| [BuildCard.java](BuildCard.md) | 917 | 50 | One build card's figures, for every one of the 76 buildings (0.7.25): what it gives the city and in what unit, its price per unit of that, the scar... | 8 |
| [BuildLog.java](BuildLog.md) | 149 | 9 | What the city has gained, and when. | 4 |
| [BuildingCatalog.java](BuildingCatalog.md) | 380 | 13 | Reads the building definitions out of buildings.json. | 3 |
| [BuildingInstance.java](BuildingInstance.md) | 40 | 4 |  | 1 |
| [BuildingManager.java](BuildingManager.md) | 5,533 | 174 | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed the... | 63 |
| [BuildingType.java](BuildingType.md) | 225 | 0 |  | 46 |
| [BuildingVisual.java](BuildingVisual.md) | 290 | 16 | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which ... | 8 |
| [BuildingsStacks.java](BuildingsStacks.md) | 466 | 42 | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. | 13 |
| [BuildingsTemplate.java](BuildingsTemplate.md) | 792 | 79 | One kind of building, and what it costs to put up. | 99 |
| [BusinessDebt.java](BusinessDebt.md) | 93 | 12 | Base class for private-sector borrowing. | 9 |
| [BusinessDebtManager.java](BusinessDebtManager.md) | 3,422 | 222 | Private-sector credit. | 23 |
| [BusinessInvestment.java](BusinessInvestment.md) | 1,145 | 53 | Capacity planning for the private sector. | 36 |
| [BusinessLoan.java](BusinessLoan.md) | 72 | 7 | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. | 7 |
| [CapitalFlows.java](CapitalFlows.md) | 609 | 37 | Hot money: what comes in chasing a spread, and what happens when it leaves. | 10 |
| [CareType.java](CareType.md) | 133 | 6 | What a healthcare building actually does. | 26 |
| [CentralBank.java](CentralBank.md) | 882 | 90 | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. | 19 |
| [ChartModel.java](ChartModel.md) | 715 | 49 | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks o... | 15 |
| [CityBasket.java](CityBasket.md) | 120 | 2 | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. | 1 |
| [CityCalendar.java](CityCalendar.md) | 146 | 13 | Turns the month counter into a date a person can hold in their head. | 23 |
| [CityLand.java](CityLand.md) | 741 | 57 | The city's land on the world: the centre it was founded or converted with, the ten lanes on each of its four sides and every purchase made along th... | 20 |
| [CityMap.java](CityMap.md) | 1,947 | 102 | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves,... | 6 |
| [CityNeeds.java](CityNeeds.md) | 983 | 46 | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, t... | 20 |
| [ConstructionControl.java](ConstructionControl.md) | 665 | 53 | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it... | 9 |
| [Consumption.java](Consumption.md) | 539 | 24 | What a household eats, and what changes it. | 5 |
| [CorporateBond.java](CorporateBond.md) | 232 | 28 | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. | 13 |
| [Crime.java](Crime.md) | 525 | 61 | Crime, the police who deter and catch it, and the prisons that hold who they catch. | 16 |
| [Currency.java](Currency.md) | 263 | 21 | What the city's money is called, and how it is written. | 22 |
| [DataSave.java](DataSave.md) | 1,992 | 321 | sections: land and ore, the shedding warning... | 4 |
| [Debt.java](Debt.md) | 517 | 47 | One piece of city paper. | 22 |
| [DebtManager.java](DebtManager.md) | 2,009 | 150 | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of m... | 30 |
| [DebtQuote.java](DebtQuote.md) | 209 | 9 | What a loan would cost, worked out BEFORE the player agrees to it. | 12 |
| [DecisionLog.java](DecisionLog.md) | 206 | 24 | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). | 27 |
| [DemolitionLog.java](DemolitionLog.md) | 142 | 10 | What the city has lost, and when. | 7 |
| [Denomination.java](Denomination.md) | 199 | 12 | The currency's unit, and the power to lop zeros off it. | 3 |
| [Deposit.java](Deposit.md) | 135 | 10 | One field of a resource in the world's ground: which resource, the world cell it was drawn in and its place in that cell's list, its centre as a pl... | 10 |
| [EconomyManager.java](EconomyManager.md) | 2,160 | 226 | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. | 42 |
| [Education.java](Education.md) | 1,093 | 59 | Who the city teaches, what it costs, and why anybody bothers. | 12 |
| [EducationType.java](EducationType.md) | 191 | 11 | What a school actually teaches. | 26 |
| [Equity.java](Equity.md) | 1,086 | 91 | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. | 36 |
| [Exchange.java](Exchange.md) | 1,841 | 149 | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the last trade. | 20 |
| [Expectations.java](Expectations.md) | 259 | 15 | What the city expects prices to do, and how far it believes the central bank. | 8 |
| [FamilyModel.java](FamilyModel.md) | 2,211 | 102 | How the city's people are arranged into households, and what each earns. | 20 |
| [FamilyStructure.java](FamilyStructure.md) | 119 | 7 | The shapes a household comes in. | 30 |
| [ForeignAccounts.java](ForeignAccounts.md) | 1,986 | 107 | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. | 28 |
| [Formats.java](Formats.md) | 122 | 9 | The few formats a sector needs to describe itself, without the toolkit. | 37 |
| [Founding.java](Founding.md) | 481 | 37 | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. | 54 |
| [FundLedger.java](FundLedger.md) | 766 | 92 | The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in incom... | 10 |
| [FundView.java](FundView.md) | 846 | 38 | The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, it... | 5 |
| [Game.java](Game.md) | 15,143 | 598 | sections: THE FOUNDING RESERVE (2026-09-21), THE FOUNDING RECORD (0.7.10)... | 132 |
| [GameFiles.java](GameFiles.md) | 425 | 37 | Where the game keeps its files, and how it writes them. | 84 |
| [GameLog.java](GameLog.md) | 348 | 20 | Everything the game prints, written somewhere a player can find it. | 9 |
| [GamePrefs.java](GamePrefs.md) | 202 | 17 | How the player likes the window, kept between runs. | 2 |
| [GameVersion.java](GameVersion.md) | 4,022 | 4 | What build this is, and what shape its saves are. | 9 |
| [Good.java](Good.md) | 907 | 18 | A thing that can be made, bought, held, imported and exported. | 82 |
| [GoodsMarket.java](GoodsMarket.md) | 493 | 51 | Where one good clears between whoever makes it and whoever wants it. | 30 |
| [Health.java](Health.md) | 403 | 20 | How much of the workforce is off sick this month. | 17 |
| [Healthcare.java](Healthcare.md) | 950 | 55 | The city's healthcare service: what it costs, what it collects, and what it does with the dead. | 22 |
| [HistoryGrapher.java](HistoryGrapher.md) | 115 | 1 |  | 1 |
| [HistorySave.java](HistorySave.md) | 1,671 | 56 | Every month the city has ever lived, one number at a time. | 25 |
| [Household.java](Household.md) | 1,413 | 122 | Every household of one shape at one pay tier, as one ledger. | 37 |
| [HouseholdAccounts.java](HouseholdAccounts.md) | 1,365 | 102 | The city's residents, treated as one household. | 14 |
| [HouseholdBalance.java](HouseholdBalance.md) | 4,642 | 242 | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. | 41 |
| [Inbox.java](Inbox.md) | 577 | 23 | Everything the city has had to say for itself, newest first. | 3 |
| [InfrastructureManager.java](InfrastructureManager.md) | 1,059 | 82 | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. | 17 |
| [InterimLoan.java](InterimLoan.md) | 35 | 1 | Interim financing: the bank's loan to a sector in the month it defaulted, for what the month's bills left unpaid after its debt was written down, r... | 4 |
| [Investor.java](Investor.md) | 44 | 6 | Whoever is paying for a building. | 2 |
| [JobType.java](JobType.md) | 20 | 0 |  | 55 |
| [LabourMarket.java](LabourMarket.md) | 855 | 45 | What labour costs, and why it costs that. | 15 |
| [LandConversion.java](LandConversion.md) | 159 | 5 | An older city's land put on the world, and a city's land drawn again to hold a new figure: the site found for it, the centre holding exactly the dr... | 5 |
| [LandManager.java](LandManager.md) | 804 | 83 | The city's land: what it owns, what is built on, and what it sells. | 31 |
| [LandMap.java](LandMap.md) | 326 | 25 | The city's land as the map view sees it: the outlines it draws - the centre's square, the frontier lane by lane, an offer's band - what lies under ... | 5 |
| [LandMarket.java](LandMarket.md) | 1,010 | 50 | The land office's window: forty offers standing, ten on each side of the city - the next band of each lane - and what the ground costs. | 15 |
| [LandParcel.java](LandParcel.md) | 298 | 37 | One offer on the market, as the land office lists it: the next band of one lane of one side of the city, with what it holds - its area dry, fresh, ... | 18 |
| [LongTermBond.java](LongTermBond.md) | 137 | 12 | A term loan: a coupon every month on the whole face, and the whole face at the end - issued only at the five maturities in MATURITIES (0.7.1). | 10 |
| [LuxuryCounter.java](LuxuryCounter.md) | 155 | 10 | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. | 1 |
| [MapFrame.java](MapFrame.md) | 231 | 37 | The map view's arithmetic without the toolkit: where the view looks and how close, how a drag and a notch of the wheel move it, which level of deta... | 4 |
| [MapTiles.java](MapTiles.md) | 310 | 22 | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again ... | 2 |
| [Markets.java](Markets.md) | 411 | 19 | Every goods market in the city, and the month they clear in. | 21 |
| [MediumTermBond.java](MediumTermBond.md) | 203 | 14 | A serial bond: the workhorse of municipal finance. | 5 |
| [Migration.java](Migration.md) | 1,312 | 56 | Why people move to this city, and the much narrower question of why they leave. | 16 |
| [MoneyAudit.java](MoneyAudit.md) | 1,145 | 27 | Where the money went this month, and whether it all went somewhere. | 30 |
| [Mortgage.java](Mortgage.md) | 520 | 38 | An insured mortgage on a new residential building: a level payment every month over a forty-year amortization, at a rate fixed for a ten-year term ... | 14 |
| [Motoring.java](Motoring.md) | 331 | 20 | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it - and since 0.7.49 the drivers' fuel a... | 7 |
| [NationalAccounts.java](NationalAccounts.md) | 1,125 | 91 | The city's GDP, measured properly, plus the government's own books. | 22 |
| [Notice.java](Notice.md) | 98 | 15 | One thing the city needs told about, and whether anybody has looked at it. | 7 |
| [Offending.java](Offending.md) | 169 | 5 | Who is at risk of offending, sorted by reason, and the thefts handed to them. | 1 |
| [OrderBook.java](OrderBook.md) | 433 | 50 | A limit-order book for one instrument: buy and sell orders from named participants, each a price and a quantity, matched by price-time priority. | 18 |
| [OrphanHousehold.java](OrphanHousehold.md) | 39 | 10 | Children no family holds, by age band. | 3 |
| [OutwardInvestment.java](OutwardInvestment.md) | 384 | 24 | Outward investment: what the city's businesses do with money the bank will not pay for. | 11 |
| [PayTier.java](PayTier.md) | 125 | 5 | The six pay levels a household can be in. | 47 |
| [PolicyPreview.java](PolicyPreview.md) | 417 | 39 | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and ... | 7 |
| [PopulationCohorts.java](PopulationCohorts.md) | 604 | 35 | The city's age pyramid, and since the switch, the city's POPULATION. | 26 |
| [PopulationManager.java](PopulationManager.md) | 1,242 | 61 | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). | 28 |
| [PriceIndex.java](PriceIndex.md) | 694 | 32 | What a month costs a household, against what it cost at founding. | 18 |
| [PrisonerHousehold.java](PrisonerHousehold.md) | 79 | 17 | Adults serving a sentence, as one ledger: the prisoners' ledger. | 5 |
| [Resource.java](Resource.md) | 121 | 10 | The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields ... | 23 |
| [RetiredHousehold.java](RetiredHousehold.md) | 51 | 6 | A household with nobody of working age in it: a senior or an elder, alone or as a couple. | 3 |
| [Rollover.java](Rollover.md) | 350 | 32 | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). | 14 |
| [SafetyType.java](SafetyType.md) | 68 | 4 | What a safety building does: police, or prison cells. | 12 |
| [SalesTaxLedger.java](SalesTaxLedger.md) | 228 | 22 | The month's sales tax, as tax payable less input tax credits. | 5 |
| [SaveHeader.java](SaveHeader.md) | 71 | 12 | Just enough of a save to label it on the slot list. | 7 |
| [Sector.java](Sector.md) | 2,506 | 195 | One business in the city, and the template every sector extends. | 81 |
| [SectorBooks.java](SectorBooks.md) | 510 | 23 | A month of books for every business in the city, and last month's too. | 19 |
| [SectorFlow.java](SectorFlow.md) | 260 | 17 | One business's month as a flow (0.7.30): what went in, what its plant made of it and what held the plant back, and what came out - each good's unit... | 3 |
| [SectorState.java](SectorState.md) | 230 | 6 | One sector, as a save carries it. | 7 |
| [Sectors.java](Sectors.md) | 382 | 38 | Every sector in the city, in one order, by one name. | 58 |
| [ServicesManager.java](ServicesManager.md) | 355 | 26 | / | 7 |
| [ShadowBasket.java](ShadowBasket.md) | 201 | 1 | What a played city WOULD buy, measured against what it spends today. | 0 |
| [ShortTermTBill.java](ShortTermTBill.md) | 127 | 13 | A short-term anticipation note: borrow now, repay one lump, no coupon. | 7 |
| [Sickness.java](Sickness.md) | 357 | 24 | Who has been sick, and for how long - and the ones it kills. | 10 |
| [SimulationEngine.java](SimulationEngine.md) | 216 | 5 | The order the month runs in: the sites advance, the roads, the posts and the wages, the people, then the economy and the services. | 1 |
| [SocialSecurity.java](SocialSecurity.md) | 150 | 9 | Contributions off every wage, and a pension for everyone too old to work. | 5 |
| [StudentHousehold.java](StudentHousehold.md) | 57 | 10 | Full-time students, as one ledger. | 6 |
| [SupplierCredit.java](SupplierCredit.md) | 255 | 21 | What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time. | 8 |
| [TaxPolicy.java](TaxPolicy.md) | 1,641 | 103 | The city's tax rates - the revenue half of what the player actually decides. | 43 |
| [TilePainter.java](TilePainter.md) | 1,044 | 40 | Paints one tile of the city map: from the world's seed, the tile's ground, what the city owns of it, the model's buildings and road plots dealt to ... | 8 |
| [TileRaster.java](TileRaster.md) | 398 | 13 | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixe... | 6 |
| [TimeSkipReport.java](TimeSkipReport.md) | 538 | 63 | What happened while you were not watching. | 4 |
| [Trade.java](Trade.md) | 40 | 4 | One fill: somebody sold somebody some units of a good at a price. | 12 |
| [Traffic.java](Traffic.md) | 60 | 3 | The three things that move, which used to be one number. | 15 |
| [TreasuryFund.java](TreasuryFund.md) | 1,063 | 147 | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand,... | 23 |
| [TreasuryJournal.java](TreasuryJournal.md) | 255 | 16 | The treasury's journal: every movement of the city's cash that is neither a budget line nor paper raised or repaid, recorded by name as it happens,... | 10 |
| [TreasuryLine.java](TreasuryLine.md) | 160 | 2 | Every kind of payment the treasury makes, and whether it is a promise. | 7 |
| [UnemployedHousehold.java](UnemployedHousehold.md) | 88 | 14 | Adults who are out of work, as one ledger per situation. | 7 |
| [Unemployment.java](Unemployment.md) | 607 | 40 | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. | 11 |
| [UtilitiesHandler.java](UtilitiesHandler.md) | 654 | 62 | sections: WATER SUPPLY, THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7)... | 14 |
| [WageBand.java](WageBand.md) | 170 | 7 | The four education bands the wage tax is set by. | 26 |
| [WorkingHousehold.java](WorkingHousehold.md) | 50 | 7 | A household with an earner in it, at one pay tier. | 5 |
| [World.java](World.md) | 1,129 | 59 | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and ri... | 24 |
| [WorldEconomy.java](WorldEconomy.md) | 422 | 18 | The rest of the world, which has its own inflation and did not use to. | 12 |
| [YearBook.java](YearBook.md) | 1,583 | 77 | The run, one line a year - for READING rather than for drawing. | 15 |

## sectors (17 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [Agriculture.java](Agriculture.md) | 393 | 12 | The fields, and what they cost the city in ground. | 4 |
| [Automotive.java](Automotive.md) | 283 | 7 | The automobile industry. | 2 |
| [BusinessServices.java](BusinessServices.md) | 270 | 8 | Somebody else's work, done here, paid for from outside. | 3 |
| [Construction.java](Construction.md) | 919 | 49 | The builders. | 14 |
| [FoodIndustry.java](FoodIndustry.md) | 98 | 6 | The mills and the plants that feed the shops. | 4 |
| [FoodProcessing.java](FoodProcessing.md) | 567 | 12 | The plants between the farm and the shelf. | 2 |
| [HeavyIndustry.java](HeavyIndustry.md) | 119 | 6 | The mills. | 1 |
| [LuxuryRetail.java](LuxuryRetail.md) | 543 | 22 | The luxury shops. | 5 |
| [Manufacturing.java](Manufacturing.md) | 282 | 11 | What the city makes out of its own steel, and ships. | 2 |
| [Materials.java](Materials.md) | 88 | 4 | The materials plant. | 1 |
| [Mining.java](Mining.md) | 153 | 6 | Iron mines. | 4 |
| [Oil.java](Oil.md) | 143 | 6 | Oil wells. | 3 |
| [Rail.java](Rail.md) | 891 | 37 | The railway. | 9 |
| [RealEstate.java](RealEstate.md) | 945 | 90 | The landlords. | 16 |
| [Refining.java](Refining.md) | 188 | 6 | The refinery. | 3 |
| [Restaurants.java](Restaurants.md) | 517 | 23 | The kitchens. | 7 |
| [Retail.java](Retail.md) | 974 | 74 | The shops. | 27 |

## interface (27 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [BankScreen.java](BankScreen.md) | 4,276 | 161 | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figu... | 3 |
| [BuildScreen.java](BuildScreen.md) | 4,096 | 140 | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.... | 14 |
| [CityBuilderSim.java](CityBuilderSim.md) | 55 | 2 | The way in. | 0 |
| [ConstructionScreen.java](ConstructionScreen.md) | 1,064 | 39 | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - pri... | 1 |
| [FinancesScreen.java](FinancesScreen.md) | 3,415 | 142 | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece an... | 5 |
| [FoundingScreen.java](FoundingScreen.md) | 486 | 18 | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. | 1 |
| [FundScreen.java](FundScreen.md) | 2,434 | 99 | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with it... | 1 |
| [GovernmentScreen.java](GovernmentScreen.md) | 2,232 | 103 | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, w... | 2 |
| [HistoryScreen.java](HistoryScreen.md) | 3,121 | 109 | City History: the city as a shape over time. | 10 |
| [Icons.java](Icons.md) | 498 | 6 | The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one per category, and the few the frame draws (the money block, the "Needs... | 17 |
| [InfrastructureScreen.java](InfrastructureScreen.md) | 1,633 | 60 | The infrastructure tab: the roads, the trams, the railway and the freight - four pages under the five figures the whole tab is about, each page led... | 1 |
| [Ladder.java](Ladder.md) | 354 | 26 | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying... | 11 |
| [LandScreen.java](LandScreen.md) | 1,772 | 73 | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.... | 2 |
| [Levers.java](Levers.md) | 145 | 3 | The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36) - the dial on a card with what it does beside it, which every dial on t... | 2 |
| [MapView.java](MapView.md) | 964 | 43 | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's ... | 1 |
| [Money.java](Money.md) | 374 | 29 | Every figure the interface prints as money, in one place. | 9 |
| [Palette.java](Palette.md) | 715 | 25 | Every colour, size and spacing this game is allowed to use, in one place. | 23 |
| [PeopleScreen.java](PeopleScreen.md) | 4,361 | 148 | The People tab and its second page, Household money. | 3 |
| [Pieces.java](Pieces.md) | 5,307 | 232 | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals ba... | 12 |
| [PolicyScreen.java](PolicyScreen.md) | 4,340 | 212 | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it w... | 8 |
| [SectorScreen.java](SectorScreen.md) | 3,521 | 121 | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with la... | 4 |
| [ServicesScreen.java](ServicesScreen.md) | 3,150 | 124 | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one p... | 4 |
| [Statement.java](Statement.md) | 482 | 23 | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank... | 21 |
| [SummaryScreen.java](SummaryScreen.md) | 1,494 | 30 | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen... | 9 |
| [TimeChart.java](TimeChart.md) | 1,554 | 85 | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, th... | 9 |
| [TradeScreen.java](TradeScreen.md) | 2,720 | 117 | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what ... | 4 |
| [UserInterface.java](UserInterface.md) | 6,134 | 139 | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating a... | 21 |

## harnesses (81 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgricultureCheck.java](AgricultureCheck.md) | 413 | 7 | The ground under the loaf. | 0 |
| [AllChecks.java](AllChecks.md) | 82 | 1 | Runs every harness, one JVM each, and says which failed. | 0 |
| [BankCheck.java](BankCheck.md) | 4,301 | 22 | The commercial bank, and the families it discharges. | 1 |
| [BondCheck.java](BondCheck.md) | 1,554 | 37 | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for co... | 2 |
| [BooksCheck.java](BooksCheck.md) | 241 | 3 | Verifies a sector's income statement and balance sheet, off the template. | 0 |
| [BuildAdviceCheck.java](BuildAdviceCheck.md) | 1,100 | 41 | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city -... | 0 |
| [BuildCardCheck.java](BuildCardCheck.md) | 998 | 21 | The build card (0.7.25): BuildCard's figures for all 76 buildings held to the model's own reads - the quote, the land, the staffing tests, the mark... | 0 |
| [BuildMenuCheck.java](BuildMenuCheck.md) | 269 | 2 | Verifies that every building in the game can describe itself. | 0 |
| [BuildingDataCheck.java](BuildingDataCheck.md) | 297 | 3 | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. | 0 |
| [BusinessServicesCheck.java](BusinessServicesCheck.md) | 387 | 5 | The sector whose customer is not in the city. | 0 |
| [CalendarCheck.java](CalendarCheck.md) | 268 | 5 | The date on the status bar, and the log of what the city has finished. | 0 |
| [CapitalFlowCheck.java](CapitalFlowCheck.md) | 487 | 6 | Hot money: does it come for the right reason, and does it leave for one? | 0 |
| [CarCheck.java](CarCheck.md) | 891 | 6 | The cars: who buys one, what it costs them, and what it does to the road. | 0 |
| [CarryTradeCheck.java](CarryTradeCheck.md) | 301 | 5 | The carry trade: the other side of hot money, and the bank's first borrower. | 0 |
| [CentralBankCheck.java](CentralBankCheck.md) | 1,756 | 25 | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its tw... | 0 |
| [ChartCheck.java](ChartCheck.md) | 899 | 27 | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the tick... | 0 |
| [ConservationCheck.java](ConservationCheck.md) | 415 | 6 | Nothing is created and nothing is destroyed. | 0 |
| [ConstructionControlCheck.java](ConstructionControlCheck.md) | 826 | 21 | The player's hand on the construction queue (0.7.22): each of ConstructionControl's five rules held to its own arithmetic, in a played city. | 0 |
| [ConsumptionCheck.java](ConsumptionCheck.md) | 342 | 4 | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. | 0 |
| [CreditCheck.java](CreditCheck.md) | 2,022 | 18 | Verifies private-sector credit: pricing, origination, rollover, cash conservation. | 0 |
| [CrimeCheck.java](CrimeCheck.md) | 340 | 8 | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. | 0 |
| [CurrencyCheck.java](CurrencyCheck.md) | 779 | 20 | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry ap... | 1 |
| [DeathRecordCheck.java](DeathRecordCheck.md) | 222 | 5 | The running totals of the dead, by age and for the orphans and the unhoused. | 0 |
| [DenominationCheck.java](DenominationCheck.md) | 656 | 17 | A currency reform is a change of units, and this is how we know. | 0 |
| [EducationCheck.java](EducationCheck.md) | 1,465 | 11 | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. | 1 |
| [EquityCheck.java](EquityCheck.md) | 380 | 5 | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. | 0 |
| [ExchangeCheck.java](ExchangeCheck.md) | 982 | 22 | Verifies the exchange on the order book (0.7.12 round 2): what the desk posts and what it is not obliged to take, who trades with whom and at what ... | 1 |
| [ExpectationsCheck.java](ExpectationsCheck.md) | 570 | 21 | Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the ... | 0 |
| [FoodProcessingCheck.java](FoodProcessingCheck.md) | 443 | 7 | The third of the shelf that arrives already made. | 0 |
| [ForeignCheck.java](ForeignCheck.md) | 1,698 | 13 | The balance of payments, and whether the boundary it is drawn on is honest. | 1 |
| [ForeignDebtCheck.java](ForeignDebtCheck.md) | 871 | 8 | Borrowing in somebody else's money. | 1 |
| [FundCheck.java](FundCheck.md) | 1,307 | 26 | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). | 1 |
| [FundLedgerCheck.java](FundLedgerCheck.md) | 933 | 23 | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, thr... | 0 |
| [GdpCheck.java](GdpCheck.md) | 608 | 4 | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock... | 0 |
| [GroceryCheck.java](GroceryCheck.md) | 460 | 10 | Groceries at a price, the shelf cleared stickily, and food assistance (0.7.43): the households' demand curve, the clearing price, who is handed the... | 0 |
| [HealthCheck.java](HealthCheck.md) | 1,587 | 5 | Sickness: what it moves, and - much more importantly - what it does not. | 0 |
| [HistoryCheck.java](HistoryCheck.md) | 842 | 11 | Verifies the graph history: recording, alignment, and the round trip. | 0 |
| [HoldersCheck.java](HoldersCheck.md) | 590 | 10 | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is boug... | 0 |
| [HouseholdCheck.java](HouseholdCheck.md) | 1,696 | 7 | Verifies the residents' books and the demolition log. | 0 |
| [HouseholdMemoryCheck.java](HouseholdMemoryCheck.md) | 278 | 6 | The households remember: the builder keeps what still fits. | 0 |
| [HousingCheck.java](HousingCheck.md) | 636 | 5 | Audits the three subsystems that describe the same housing, every month, and makes them agree. | 0 |
| [InboxCheck.java](InboxCheck.md) | 364 | 5 | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. | 0 |
| [InfrastructureCheck.java](InfrastructureCheck.md) | 1,134 | 8 | The road network, from the curve up to a city that actually jams. | 0 |
| [InvestCheck.java](InvestCheck.md) | 1,458 | 7 | Verifies the private investment engine: forecasting, the demand tests, and the brake. | 0 |
| [LabourCheck.java](LabourCheck.md) | 1,179 | 14 | Verifies the labour market: who can hold a job, and what it costs. | 0 |
| [LandCheck.java](LandCheck.md) | 2,079 | 24 | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. | 0 |
| [LongPlaytest.java](LongPlaytest.md) | 5,720 | 85 | A city played for four thousand months, the way a person plays. | 19 |
| [ManufacturingCheck.java](ManufacturingCheck.md) | 529 | 7 | The ninth sector: what the city makes out of its own steel, and ships. | 0 |
| [MapCheck.java](MapCheck.md) | 1,525 | 35 | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter keeps the mockup's ru... | 0 |
| [MiningCheck.java](MiningCheck.md) | 659 | 8 | Ore, from the band it clears in to whether it makes steel worth building. | 1 |
| [MonetaryCheck.java](MonetaryCheck.md) | 899 | 10 | Money: what a basket costs, what the world charges, and what the rate does. | 2 |
| [MoneyCheck.java](MoneyCheck.md) | 289 | 5 | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. | 0 |
| [MortgageCheck.java](MortgageCheck.md) | 1,199 | 25 | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. | 0 |
| [NewGameCheck.java](NewGameCheck.md) | 1,116 | 14 | Does "Start New Game" actually start a new game? | 1 |
| [OilCheck.java](OilCheck.md) | 592 | 19 | Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K; the project's spec-land.md 2.7 and section 3's K entry). | 0 |
| [OrderBookCheck.java](OrderBookCheck.md) | 297 | 16 | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. | 0 |
| [OrderSearchCheck.java](OrderSearchCheck.md) | 497 | 19 | The three order searches against the countdowns they replaced (0.7.54). | 1 |
| [OutsideCheck.java](OutsideCheck.md) | 699 | 5 | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). | 0 |
| [PolicyCheck.java](PolicyCheck.md) | 475 | 5 | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. | 0 |
| [PolicyPreviewCheck.java](PolicyPreviewCheck.md) | 519 | 15 | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. | 0 |
| [PopulationCheck.java](PopulationCheck.md) | 1,422 | 11 | The demographics: do they hold together, and do they move the city the way they were told to? | 0 |
| [RailCheck.java](RailCheck.md) | 627 | 8 | The railway: what it charges, who pays it, and what it does to the band. | 1 |
| [ReadPathCheck.java](ReadPathCheck.md) | 1,609 | 9 | Reading the city must not change the city. | 0 |
| [RestaurantsCheck.java](RestaurantsCheck.md) | 576 | 6 | A meal out is food, and it is the same food. | 0 |
| [RestructureCheck.java](RestructureCheck.md) | 523 | 5 | Buying the city's own debt back, at what the paper is actually worth. | 0 |
| [RobustnessCheck.java](RobustnessCheck.md) | 425 | 5 | What the game does when something is already broken. | 0 |
| [SaveFileCheck.java](SaveFileCheck.md) | 2,334 | 7 | Verifies where saves go and how they are written. | 0 |
| [SaveSlotCheck.java](SaveSlotCheck.md) | 315 | 7 | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. | 0 |
| [ScaleCheck.java](ScaleCheck.md) | 1,017 | 30 | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them a... | 1 |
| [SectorBooksCheck.java](SectorBooksCheck.md) | 332 | 5 | Plays a city and audits every sector's statements, every month. | 1 |
| [SectorFlowCheck.java](SectorFlowCheck.md) | 307 | 11 | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held t... | 0 |
| [SicknessCheck.java](SicknessCheck.md) | 323 | 5 | The long sick: who stays sick, and who it kills. | 0 |
| [SkipReportCheck.java](SkipReportCheck.md) | 323 | 5 | Verifies the fast-forward summary. | 0 |
| [StaleCheck.java](StaleCheck.md) | 204 | 5 | The prose still describes the code: the firm half of `tools.Stale`, asserted. | 0 |
| [SupplierCreditCheck.java](SupplierCreditCheck.md) | 341 | 10 | The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit when the till cannot pay for it, owed for a month, repaid out of the s... | 0 |
| [TradeCostCheck.java](TradeCostCheck.md) | 551 | 6 | The wedge between what the world charges and what it pays, and what it is made of. | 0 |
| [TreasuryCheck.java](TreasuryCheck.md) | 965 | 17 | Plays a city and audits what the screens say the treasury did. | 1 |
| [VanCheck.java](VanCheck.md) | 309 | 5 | The vans: what a sector needs, what it costs it, and what happens while it waits for them. | 0 |
| [WaterCheck.java](WaterCheck.md) | 512 | 10 | Sanity harness for water production, demand, throttling and billing - and, since 0.7.59, the fresh water limit and the desalination plant (sections... | 0 |
| [WorldCheck.java](WorldCheck.md) | 332 | 4 | The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its ... | 0 |
| [YearBookCheck.java](YearBookCheck.md) | 1,114 | 44 | Proves the year book folds each series the way that series has to be folded, and that the file says so. | 0 |

## tools (11 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [CodeMap.java](CodeMap.md) | 177 | 3 | The code map: docs/map/README.md, one row per file, and docs/map/NAME.md for every file, listing its banner sections and every member with the line... | 1 |
| [Dials.java](Dials.md) | 55 | 2 | Every dial in the game on one page: docs/dials.md lists each static final constant in the tree with its value, its line, and the sentence above it. | 1 |
| [HarnessMap.java](HarnessMap.md) | 133 | 3 | What every harness asserts, in its own words: docs/harnesses.md. | 1 |
| [JavaScan.java](JavaScan.md) | 666 | 39 | A structural read of one Java source file, without a compiler. | 7 |
| [ManualToMarkdown.java](ManualToMarkdown.md) | 1,522 | 82 | The published manual as two files the repository keeps: docs/manual.md, which GitHub renders when it is clicked, and docs/manual.html, the page its... | 0 |
| [Maps.java](Maps.md) | 30 | 1 | Regenerates every generated document in one go: the code map, the dials, the month order and the harness map. | 0 |
| [MonthOrder.java](MonthOrder.md) | 167 | 6 | The month as a numbered list: docs/month-order.md walks the top-level statements of the methods that make up a month, in order, each with its line ... | 1 |
| [SaveDump.java](SaveDump.md) | 133 | 4 | Looks inside a save without loading the game - or reading the file. | 0 |
| [SourceTree.java](SourceTree.md) | 154 | 13 | The whole source tree, scanned once, and the little that every generator shares: where the sources are, where the documents go, what area a file be... | 8 |
| [Stale.java](Stale.md) | 627 | 26 | Finds the comments and documents that have stopped being true, mechanically. | 1 |
| [Where.java](Where.md) | 71 | 1 | Finds a member by name anywhere in the tree and, if asked, prints it. | 0 |

