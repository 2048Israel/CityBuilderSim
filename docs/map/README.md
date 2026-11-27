# The code map

Generated 2026-11-27 by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).

**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.

**The tree:** 649 files, 704,036 lines, 30,768 methods, 6,873 constants. `GameVersion.VERSION` is "0.9.215", `SAVE_FORMAT` 66.

## model (282 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AdultStage.java](AdultStage.md) | 88 | 10 | The two stages of an adult life inside AgeBand.ADULT: the young, 18-34, and the established, 35-69. | 22 |
| [AdviceValueOracle.java](AdviceValueOracle.md) | 388 | 15 | Independent discounted arithmetic for BuildAdviceCheck's complete-plan law. | 3 |
| [AgeBand.java](AgeBand.md) | 181 | 12 | The six ages of a resident. | 54 |
| [ArrivalMix.java](ArrivalMix.md) | 189 | 9 | What the world's movers hold by year: the skill mix a month's arrivals are drawn from, in a city founded the 1900 way. | 9 |
| [Auction.java](Auction.md) | 460 | 28 | The city's paper sold at auction: the bidders, each by its own rule in the game, and the one yield that clears them. | 31 |
| [AuctionTally.java](AuctionTally.md) | 145 | 11 | The city's auctions counted: how many sales the treasury has booked, what they sold and what they asked, how often the stop sat more than a point o... | 4 |
| [AutoBuilder.java](AutoBuilder.md) | 2,953 | 177 | Automatic building (0.7.73): the city's own works ordered for the player once a month - power, water, the roads and their lines, care, schools and ... | 28 |
| [BalanceSheet.java](BalanceSheet.md) | 201 | 30 | A simple balance sheet for one business in the city. | 6 |
| [Bank.java](Bank.md) | 6,942 | 480 | The city's commercial bank: every loan in it, and every default. | 77 |
| [BoatSchedule.java](BoatSchedule.md) | 1,068 | 62 | The month's ships, as a pure function of time: which calls the city's sea trade makes, when each arrives, and where each boat is at any moment (0.7... | 21 |
| [BondMarket.java](BondMarket.md) | 2,352 | 195 | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participa... | 32 |
| [BookDesk.java](BookDesk.md) | 184 | 20 | The debt book as a desk: every figure the book page and a line's page draw (CD6, spec-city-debt.md section 7, boards 1 and 2), a pure model of what... | 5 |
| [BridgeGround.java](BridgeGround.md) | 72 | 7 | The ground a bridge is surveyed over, read from the World and the city's land: the Earth's terrain, relief and landmass a tile at a time, the city'... | 1 |
| [BridgeSurvey.java](BridgeSurvey.md) | 438 | 25 | The survey of a long bridge's crossing: the shortest chain of plots from the city's own dry ground on a far landmass to its own buildable ground on... | 10 |
| [BridgeView.java](BridgeView.md) | 98 | 11 | What the build card says of a long bridge's crossing: the refusal of a survey in its own words, and the pieces of the line a surveyed crossing gets. | 10 |
| [BridgeWorks.java](BridgeWorks.md) | 126 | 7 | What a long bridge's span costs, in US dollars a metre by kind and by the depth of the water it crosses, and the construction points those dollars ... | 14 |
| [Bridges.java](Bridges.md) | 321 | 31 | The city's long bridges, built and building: for each, the template it was ordered as, its kind, the two landings and the landmass at each end, its... | 11 |
| [BuildAdvice.java](BuildAdvice.md) | 3,483 | 188 | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure befor... | 60 |
| [BuildCard.java](BuildCard.md) | 1,969 | 100 | One build card's figures, for every one of the buildings in buildings.json (0.7.25; 143 of them since FR3, the Bulk Berth): what it gives the city ... | 43 |
| [BuildLog.java](BuildLog.md) | 162 | 10 | What the city has gained, and when. | 5 |
| [BuildersShort.java](BuildersShort.md) | 371 | 20 | The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a premium that rises with the months... | 13 |
| [BuildingCatalog.java](BuildingCatalog.md) | 894 | 30 | Reads the building definitions out of buildings.json. | 25 |
| [BuildingInstance.java](BuildingInstance.md) | 40 | 4 |  | 1 |
| [BuildingManager.java](BuildingManager.md) | 9,816 | 324 | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed the... | 166 |
| [BuildingType.java](BuildingType.md) | 255 | 0 |  | 110 |
| [BuildingVisual.java](BuildingVisual.md) | 553 | 24 | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which ... | 33 |
| [BuildingsStacks.java](BuildingsStacks.md) | 972 | 88 | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. | 75 |
| [BuildingsTemplate.java](BuildingsTemplate.md) | 1,727 | 191 | One kind of building, and what it costs to put up. | 248 |
| [BusinessDebt.java](BusinessDebt.md) | 100 | 14 | Base class for private-sector borrowing. | 11 |
| [BusinessDebtManager.java](BusinessDebtManager.md) | 3,848 | 253 | Private-sector credit. | 44 |
| [BusinessInvestment.java](BusinessInvestment.md) | 1,846 | 108 | Capacity planning for the private sector. | 102 |
| [BusinessLoan.java](BusinessLoan.md) | 72 | 7 | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. | 8 |
| [Buyback.java](Buyback.md) | 432 | 28 | The city's paper bought back in a reverse auction: every holder offers what it holds in slices, each at the price where its own buying rule would s... | 13 |
| [CallPartners.java](CallPartners.md) | 233 | 16 | Who each ship and train of the month calls on: a nation drawn by the month's tonnes of the call's kind of cargo, and the compass bearing of its nod... | 4 |
| [CallTree.java](CallTree.md) | 330 | 18 | What a callable bond is worth, and what the right to call it costs: a Ho-Lee short-rate tree on monthly steps, fitted to the city's curve (CD4, doc... | 5 |
| [CameraPath.java](CameraPath.md) | 231 | 20 | The replay camera's arithmetic without the toolkit (TR1, 2026-10-31): the glide between two marked views, the fly out to the whole Earth and back, ... | 6 |
| [CapSetting.java](CapSetting.md) | 78 | 8 | A cap on the yield an automatic sale takes, saved and said (D28). | 5 |
| [CapitalFlows.java](CapitalFlows.md) | 911 | 58 | Hot money: what comes in chasing a spread, and what happens when it leaves. | 21 |
| [CareType.java](CareType.md) | 170 | 7 | What a healthcare building actually does. | 40 |
| [CausesOfDeath.java](CausesOfDeath.md) | 728 | 32 | The causes of death of 1900, and how history and the city take them away. | 10 |
| [CellSpread.java](CellSpread.md) | 64 | 7 | Closed forms for the amounts among a cell's holders (HC1). | 6 |
| [CentralBank.java](CentralBank.md) | 1,306 | 121 | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. | 33 |
| [CentralBankStatements.java](CentralBankStatements.md) | 287 | 14 | The central bank's month as formal statements (CBK1): its income statement down to the profit and what the strike did with it, its balance sheet ag... | 2 |
| [ChartModel.java](ChartModel.md) | 735 | 50 | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks o... | 23 |
| [CityBasket.java](CityBasket.md) | 122 | 2 | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. | 1 |
| [CityCalendar.java](CityCalendar.md) | 150 | 13 | Turns the month counter into a date a person can hold in their head. | 81 |
| [CityLand.java](CityLand.md) | 1,424 | 99 | The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase sinc... | 67 |
| [CityMap.java](CityMap.md) | 11,645 | 573 | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves,... | 35 |
| [CityMeasures.java](CityMeasures.md) | 468 | 23 | The city's figure for each of the Rankings' 70 measures (spec-leaderboards 6, "Where the game's side comes from"), worked out of its HistorySave an... | 4 |
| [CityNeeds.java](CityNeeds.md) | 1,139 | 50 | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, t... | 32 |
| [CityRuns.java](CityRuns.md) | 5,374 | 269 | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with ... | 21 |
| [CityShore.java](CityShore.md) | 655 | 42 | The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over o... | 18 |
| [ClockPace.java](ClockPace.md) | 311 | 19 | The clock's pace (UP2): how much of each frame the months may take, how often the screen is redrawn and what the redraw cost, and what speed the ci... | 4 |
| [ConstructionControl.java](ConstructionControl.md) | 1,055 | 77 | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it... | 33 |
| [Consumption.java](Consumption.md) | 763 | 35 | What a household eats, and what changes it. | 9 |
| [CorporateBond.java](CorporateBond.md) | 254 | 30 | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. | 20 |
| [Credits.java](Credits.md) | 367 | 18 | The Credits page's sections: the Earth data's attribution, the nations' attribution, the fonts' and libraries' notices with their licences, the Jav... | 4 |
| [Crime.java](Crime.md) | 525 | 61 | Crime, the police who deter and catch it, and the prisons that hold who they catch. | 21 |
| [Currency.java](Currency.md) | 458 | 37 | What the city's money is called, and how it is written. | 53 |
| [DataSave.java](DataSave.md) | 3,092 | 490 | sections: land and ore, the shedding warning... | 24 |
| [Debt.java](Debt.md) | 1,464 | 120 | One piece of city paper. | 50 |
| [DebtManager.java](DebtManager.md) | 3,598 | 290 | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of m... | 71 |
| [DebtQuote.java](DebtQuote.md) | 308 | 12 | What a loan would cost, worked out BEFORE the player agrees to it. | 31 |
| [DebtStatistics.java](DebtStatistics.md) | 196 | 15 | Household debt as the statistics offices print it, read from the cells (HC2). | 7 |
| [DecisionLog.java](DecisionLog.md) | 213 | 24 | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). | 42 |
| [DemolitionLog.java](DemolitionLog.md) | 142 | 10 | What the city has lost, and when. | 8 |
| [Denomination.java](Denomination.md) | 199 | 12 | The currency's unit, and the power to lop zeros off it. | 5 |
| [Deposit.java](Deposit.md) | 280 | 14 | One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a pl... | 60 |
| [DepositMarks.java](DepositMarks.md) | 164 | 12 | The deposits the land office's map marks when it is zoomed far out: which to find for a view, which of them to draw, and the mark each is drawn wit... | 2 |
| [DistrictPicture.java](DistrictPicture.md) | 309 | 17 | A district's picture for the far views: 32 x 32 pixels, a pixel for 8 x 8 plots (240 m), each the colour of one of its 64 plots - the plot a fixed ... | 3 |
| [DistrictPlan.java](DistrictPlan.md) | 7,910 | 292 | One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it... | 21 |
| [EarthData.java](EarthData.md) | 1,118 | 99 | The reader of earth-2.bin (and of earth-1.bin, whose layers it holds), the real Earth's ground as data: its layers' 1-degree tiles inflated into an... | 44 |
| [EarthProj.java](EarthProj.md) | 446 | 34 | The real Earth flattened round a city's spot: Lambert's azimuthal equal-area projection on a sphere, forward and inverse, the world's plots in metr... | 26 |
| [EarthSite.java](EarthSite.md) | 724 | 44 | A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the s... | 22 |
| [EarthTerrain.java](EarthTerrain.md) | 3,311 | 172 | The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the... | 28 |
| [EconomyManager.java](EconomyManager.md) | 2,795 | 281 | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. | 78 |
| [Education.java](Education.md) | 1,098 | 63 | Who the city teaches, what it costs, and why anybody bothers. | 19 |
| [EducationType.java](EducationType.md) | 191 | 11 | What a school actually teaches. | 43 |
| [Equipment.java](Equipment.md) | 173 | 16 | A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends (if any), what fitting it buys and h... | 33 |
| [EquipmentFit.java](EquipmentFit.md) | 403 | 23 | What a piece of equipment would earn a building and what it would cost, and whether that pays: research-design 2.2's rule, steps 1 to 4, and each p... | 25 |
| [EquipmentForms.java](EquipmentForms.md) | 217 | 10 | Sets R0c's 1900 forms from the buildings still without the equipment that ends them: the steam share of each sector, the farms' horse share and the... | 1 |
| [Equity.java](Equity.md) | 1,370 | 120 | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. | 66 |
| [EstateCustody.java](EstateCustody.md) | 155 | 38 | The treasury's passive owner of assets for which an estate has no heir (HT1). | 7 |
| [Exchange.java](Exchange.md) | 2,790 | 215 | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the quote: the middle of an ... | 30 |
| [Expectations.java](Expectations.md) | 287 | 16 | What the city expects prices to do, and how far it believes the central bank. | 12 |
| [FamilyModel.java](FamilyModel.md) | 2,708 | 129 | How the city's people are arranged into households, and what each earns. | 32 |
| [FamilyStructure.java](FamilyStructure.md) | 119 | 7 | The shapes a household comes in. | 42 |
| [Fitout.java](Fitout.md) | 179 | 19 | One new building's equipment as its order fixes it (R1e): the pieces of its template it comes WITHOUT, chosen when the order is quoted and frozen o... | 7 |
| [Fittings.java](Fittings.md) | 150 | 14 | The fittings on site: pieces of equipment ordered for a standing building and not yet worked, first ordered first worked. | 5 |
| [FloatingRateNote.java](FloatingRateNote.md) | 218 | 20 | A floating-rate note: a coupon every month on the whole face, the month's policy rate plus a margin fixed at the sale, and the face at the end - at... | 7 |
| [FloorView.java](FloorView.md) | 376 | 61 | What the Policy tab's minimum-wage page draws (WS1): the floor against the going rate and the unskilled wage, against the average wage and the worl... | 2 |
| [FollowPlayer.java](FollowPlayer.md) | 174 | 15 | Plays an InputTimeline in real time (TR2, 2026-11-09): the cursor of Follow. | 2 |
| [ForeignAccounts.java](ForeignAccounts.md) | 3,977 | 256 | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. | 70 |
| [ForeignCouponView.java](ForeignCouponView.md) | 49 | 3 | What the Government screen says of the coupons the city pays abroad: the note on its "...of which paid abroad" row, from the moneys the city's fore... | 2 |
| [Formats.java](Formats.md) | 165 | 12 | The few formats a sector needs to describe itself, without the toolkit. | 64 |
| [Founding.java](Founding.md) | 713 | 66 | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. | 154 |
| [FoundingName.java](FoundingName.md) | 73 | 6 | The city's name as the Found-a-city page holds it: blank to begin with, filled by a place searched, and the player's own once they have typed one (... | 2 |
| [FreightView.java](FreightView.md) | 472 | 23 | The Freight sector's divisions as the screens draw them (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this mon... | 7 |
| [FuelSplit.java](FuelSplit.md) | 229 | 8 | FUEL, split into the PETROL and DIESEL it became: a save written before 0.7.76 converted once, on its load, before anything is restored (batch O1; ... | 2 |
| [FundLedger.java](FundLedger.md) | 804 | 97 | The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in incom... | 16 |
| [FundView.java](FundView.md) | 921 | 41 | The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, it... | 8 |
| [Game.java](Game.md) | 23,686 | 1065 | sections: THE CITY FOUNDED THE 1900 WAY, AND WHAT A NEW BUILDING COMES WITH (R1b, 2026-11-05; docs/studies/research-design.md 2.2 step 6), THE FOUN... | 338 |
| [GameFiles.java](GameFiles.md) | 559 | 46 | Where the game keeps its files, and how it writes them. | 220 |
| [GameLog.java](GameLog.md) | 348 | 20 | Everything the game prints, written somewhere a player can find it. | 13 |
| [GamePrefs.java](GamePrefs.md) | 282 | 31 | How the player likes the window, kept between runs. | 8 |
| [GameVersion.java](GameVersion.md) | 8,520 | 6 | What build this is, and what shape its saves are. | 29 |
| [Gateways.java](Gateways.md) | 207 | 11 | The city's two gateways - the highway's and the railway's - as one point on the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, wit... | 2 |
| [Geology.java](Geology.md) | 1,306 | 49 | The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's nam... | 10 |
| [GlobeFrame.java](GlobeFrame.md) | 1,088 | 93 | The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a dra... | 12 |
| [GlobePicture.java](GlobePicture.md) | 411 | 33 | The planet's picture: a code a texel on a latitude-longitude grid at ARCMIN - a World class and, since ET1, its relief - made from the Earth's data... | 8 |
| [GoldPrice.java](GoldPrice.md) | 332 | 22 | Gold's price in US dollars a fine troy ounce, a month at a time (batch GD1; spec-gold-money 4.1): the statute's par to March 1933, a ten-month path... | 12 |
| [Good.java](Good.md) | 1,447 | 26 | A thing that can be made, bought, held, imported and exported. | 198 |
| [GoodsMarket.java](GoodsMarket.md) | 674 | 68 | Where one good clears between whoever makes it and whoever wants it. | 73 |
| [GovernmentPlayer.java](GovernmentPlayer.md) | 73 | 2 | The test player's annual response to the government's wealth, shared by the playtest and its measuring drivers. | 2 |
| [GovernmentView.java](GovernmentView.md) | 242 | 32 | What the government owns net of its paper, in years of its revenue, and the existing levers that can give an excess back (GV1, spec-wealth 3.2). | 6 |
| [GridOffers.java](GridOffers.md) | 356 | 28 | The city's offers on the block grid: on each of its four sides six places, left to right facing out, and in each place one standing offer - a recta... | 9 |
| [HarbourYard.java](HarbourYard.md) | 508 | 31 | The picture of one work on the shore at the near view (HB2, 2026-11-09): its open cargo yard by what it handles, the stock on it at a level, and th... | 4 |
| [Health.java](Health.md) | 450 | 27 | How much of the workforce is off sick this month. | 29 |
| [Healthcare.java](Healthcare.md) | 997 | 57 | The city's healthcare service: what it costs, what it collects, and what it does with the dead. | 28 |
| [HistoryGrapher.java](HistoryGrapher.md) | 115 | 1 |  | 1 |
| [HistorySave.java](HistorySave.md) | 2,565 | 96 | Every month the city has ever lived, one number at a time. | 60 |
| [HomeFuel.java](HomeFuel.md) | 171 | 20 | The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the ho... | 4 |
| [Household.java](Household.md) | 1,594 | 138 | Every household of one shape at one pay tier, as one ledger. | 58 |
| [HouseholdAccounts.java](HouseholdAccounts.md) | 1,458 | 110 | The city's residents, treated as one household. | 20 |
| [HouseholdBalance.java](HouseholdBalance.md) | 6,464 | 376 | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. | 73 |
| [HousingInventory.java](HousingInventory.md) | 288 | 33 | Aggregate housing titles, occupants and listings over the physical stacks. | 5 |
| [HousingRecipe.java](HousingRecipe.md) | 55 | 6 | The fixed dwelling mix and rights delivered by one residential structure. | 6 |
| [HydroSites.java](HydroSites.md) | 440 | 31 | The city's hydro sites: where a river and a drop lie on the ground it owns - the places a Hydro Station can be ordered (HY1, 2026-10-26; docs/queue... | 11 |
| [Inbox.java](Inbox.md) | 949 | 40 | Everything the city has had to say for itself, newest first. | 8 |
| [IndustryPicture.java](IndustryPicture.md) | 1,617 | 85 | Each materials industry's month drawn as a picture, laid out to scale (0.7.143, batch I12; docs/studies/spec-materials.md 2.13, the study's mockups... | 2 |
| [IndustryView.java](IndustryView.md) | 883 | 64 | Each materials industry's month as a picture (0.7.141, batch I11; docs/studies/spec-materials.md 2.13): where what it worked came from - its fields... | 6 |
| [InflationLinkedBond.java](InflationLinkedBond.md) | 311 | 28 | An inflation-linked bond: a REAL coupon every month on a principal that follows the city's price index, and at the end the indexed principal, never... | 8 |
| [InfrastructureManager.java](InfrastructureManager.md) | 1,318 | 101 | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. | 36 |
| [InputLog.java](InputLog.md) | 471 | 39 | What the player did in a sitting, as a file (TR1, 2026-10-31): the mouse, the keys and the markers beside them - the window's size, the screen open... | 8 |
| [InputTimeline.java](InputTimeline.md) | 378 | 32 | A recording's input logs read as one timeline (TR2, 2026-11-09): which sitting holds each recorded month, the segments those make, where Follow can... | 4 |
| [InterchangeGround.java](InterchangeGround.md) | 169 | 9 | The ground a highway interchange keeps: a 19 x 19 plot reserve centred on each highway x highway crossing (a cloverleaf), an 11 x 11 one beside eac... | 5 |
| [InterimLoan.java](InterimLoan.md) | 35 | 1 | Interim financing: the bank's loan to a sector in the month it defaulted, for what the month's bills left unpaid after its debt was written down, r... | 5 |
| [Investor.java](Investor.md) | 44 | 6 | Whoever is paying for a building. | 10 |
| [JobType.java](JobType.md) | 20 | 0 |  | 121 |
| [KnownReds.java](KnownReds.md) | 130 | 8 | The common red-line judge for the suite, batch checks and publisher. | 2 |
| [LabourMarket.java](LabourMarket.md) | 1,113 | 65 | What labour costs, and why it costs that. | 30 |
| [LandClassView.java](LandClassView.md) | 188 | 16 | What the land office and the sector statements say about the CLASSES of ground (ET3, spec-earth2-terrain 5.3), in words, worked out without a node:... | 4 |
| [LandDrawing.java](LandDrawing.md) | 308 | 29 | Counts plotted ground and redraws a city's centre to a requested area. | 10 |
| [LandFactors.java](LandFactors.md) | 235 | 19 | Each nation's land factor: what a hectare of its land costs against a hectare of Canada's (1, D21), at the founding and as its output a head moves ... | 8 |
| [LandGrid.java](LandGrid.md) | 410 | 32 | The ground a city owns, as a region quadtree over the world's plots: which holding owns each plot, and how much of any block or rectangle of plots ... | 22 |
| [LandManager.java](LandManager.md) | 3,278 | 292 | The city's land: what it owns, what is built on, and what it sells. | 114 |
| [LandMap.java](LandMap.md) | 2,722 | 163 | The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectang... | 22 |
| [LandMarket.java](LandMarket.md) | 1,871 | 115 | The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's ... | 35 |
| [LandParcel.java](LandParcel.md) | 517 | 66 | One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, w... | 51 |
| [LandSellers.java](LandSellers.md) | 513 | 35 | The nation that sells each plot of a real-Earth world's ground, and an offer's unowned ground split by its sellers. | 7 |
| [LandSellersView.java](LandSellersView.md) | 117 | 5 | What the land office says of who sells an offer's ground: a pure function of the offer's sellers (LandParcel.getSellers()) and the nations' names (... | 3 |
| [LandmassView.java](LandmassView.md) | 683 | 50 | What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that the words are written in one... | 14 |
| [Landmasses.java](Landmasses.md) | 1,268 | 101 | The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass (each landmass's owned dry gro... | 36 |
| [LicenceFees.java](LicenceFees.md) | 609 | 50 | The patent rent on discoveries imported by a city founded the 1900 way. | 27 |
| [LongTermBond.java](LongTermBond.md) | 208 | 21 | A bond: a fixed coupon every month on the whole face, and the face at the end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2). | 25 |
| [LuxuryCounter.java](LuxuryCounter.md) | 160 | 10 | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. | 1 |
| [MagnitudeSteps.java](MagnitudeSteps.md) | 39 | 4 | The steps of a count, an area or an amount, following its leading digit. | 6 |
| [MapFrame.java](MapFrame.md) | 374 | 47 | The map view's arithmetic without the toolkit: where the view looks and how close, how a drag, a notch of the wheel and a held key (0.7.135) move i... | 27 |
| [MapSiteHistory.java](MapSiteHistory.md) | 417 | 25 | The resource sites' plot classes as they change each month, kept sparsely by district for the plans' historical ground. | 2 |
| [MapTiles.java](MapTiles.md) | 1,368 | 74 | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again ... | 12 |
| [Markets.java](Markets.md) | 681 | 33 | Every goods market in the city, and the month they clear in. | 49 |
| [MedicalRows.java](MedicalRows.md) | 64 | 5 | Which of the research catalogue's medical rows (M, PM) a city knows in a month, and how far ahead of the world it found each (R2f). | 5 |
| [MediumTermBond.java](MediumTermBond.md) | 255 | 18 | The amortizing bond: the workhorse of municipal finance - at 5 to 30 years in fives, one coupon, each sale its own line (CD2). | 13 |
| [Migration.java](Migration.md) | 1,650 | 67 | Why people move to this city, and the much narrower question of why they leave. | 21 |
| [MiniMapFrame.java](MiniMapFrame.md) | 316 | 32 | The minimap's arithmetic without the toolkit: what the stamp at the rail's foot and its hover preview frame - the city's built-up ground, the groun... | 6 |
| [MoneyAudit.java](MoneyAudit.md) | 1,713 | 42 | Where the money went this month, and whether it all went somewhere. | 93 |
| [Mortgage.java](Mortgage.md) | 556 | 40 | An insured mortgage on a new residential building: a level payment every month over a forty-year amortization, at a rate fixed for a ten-year term ... | 23 |
| [Motoring.java](Motoring.md) | 395 | 22 | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it - and since 0.7.49 the drivers' fuel a... | 6 |
| [NationalAccounts.java](NationalAccounts.md) | 1,644 | 138 | The city's GDP, measured properly, plus the government's own books. | 46 |
| [Nations.java](Nations.md) | 841 | 47 | The nations beyond the city, each on the world's path: its year - output, people, prices and its money's rate to the US dollar - from the path (His... | 44 |
| [NationsMap.java](NationsMap.md) | 1,085 | 69 | The nations on the map, without the toolkit: the borders simplified and cut to a view, each nation's dot and the name written on its land, what lie... | 10 |
| [NewUnit.java](NewUnit.md) | 227 | 31 | One new building of a template, as it would run (QN1): the recipe, posts, utility draw, output and input of a building ordered now - the pieces it ... | 39 |
| [Notice.java](Notice.md) | 119 | 17 | One thing the city needs told about, and whether anybody has looked at it. | 18 |
| [Observation.java](Observation.md) | 71 | 5 | The played city's path premises, reported without counting a failure. | 11 |
| [Offending.java](Offending.md) | 182 | 5 | Who is at risk of offending, sorted by reason, and the thefts handed to them. | 1 |
| [OilView.java](OilView.md) | 1,353 | 73 | The oil industry on one page (0.7.96, batch O12; docs/studies/spec-oil.md 2.13): the city's wells by kind with what they would lift over the next t... | 7 |
| [OldMetal.java](OldMetal.md) | 418 | 40 | The city's old steel: what it throws out each month that a Scrap Yard can buy and sort into scrap - its scrapped cars, its demolitions, its fabrica... | 26 |
| [OrderBook.java](OrderBook.md) | 552 | 58 | A limit-order book for one instrument: buy and sell orders from named participants, each a price and a quantity, matched by price-time priority. | 25 |
| [OreView.java](OreView.md) | 71 | 4 | The land office's Ore card as the model reads it (batch I13-L): for each ore the city's ground holds, its deposits, the tonnes still in the ground,... | 2 |
| [OrphanHousehold.java](OrphanHousehold.md) | 42 | 10 | Children no family holds, by age band. | 3 |
| [OutwardInvestment.java](OutwardInvestment.md) | 773 | 50 | Outward investment: what the city's businesses do with money the bank will not pay for. | 16 |
| [OwnerHousehold.java](OwnerHousehold.md) | 117 | 12 | The owner families, as one ledger: one two-adult household per company that has listed and not gone broke. | 6 |
| [PartnerTrade.java](PartnerTrade.md) | 573 | 42 | The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's tab... | 16 |
| [PartnersView.java](PartnersView.md) | 315 | 16 | The Partners board's and a nation's page's figures (WN6; docs/studies/spec-nations.md 8, boards 2 and 3, D29): the table of who the city trades wit... | 3 |
| [PayTier.java](PayTier.md) | 135 | 6 | The six pay levels a household can be in. | 83 |
| [PolicyDates.java](PolicyDates.md) | 188 | 17 | When Canada first had each of the Policy tab's levers, as history: the game month Canada first had each one in a city founded in January 1900, the ... | 24 |
| [PolicyPreview.java](PolicyPreview.md) | 672 | 55 | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and ... | 18 |
| [PolicyRates.java](PolicyRates.md) | 357 | 25 | The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal Reser... | 12 |
| [PopulationCohorts.java](PopulationCohorts.md) | 845 | 54 | The city's age pyramid, and since the switch, the city's POPULATION. | 43 |
| [PopulationManager.java](PopulationManager.md) | 1,394 | 74 | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). | 51 |
| [Ports.java](Ports.md) | 770 | 57 | The city's ports: the berths its sea terminals stand, the share of each kind of cargo they take off the lorries and the railway, and the tonnes tha... | 60 |
| [PowerDispatch.java](PowerDispatch.md) | 484 | 34 | One month's merit order for the city's grid: the rows that make power, the load a month asks of them cut in six half-blocks, and the price each hal... | 24 |
| [PowerView.java](PowerView.md) | 95 | 4 | The power screen's pure projection of a retained dispatch and its struck tariff. | 5 |
| [PriceIndex.java](PriceIndex.md) | 781 | 37 | What a month costs a household, against what it cost at founding. | 28 |
| [PriceLevel.java](PriceLevel.md) | 84 | 5 | The price level a city is founded at, 1900's: the US consumer price index of the calendar's first year over its 2025, and the series it is read fro... | 134 |
| [PrisonerHousehold.java](PrisonerHousehold.md) | 79 | 17 | Adults serving a sentence, as one ledger: the prisoners' ledger. | 5 |
| [QuayCrane.java](QuayCrane.md) | 173 | 11 | The cranes that work a boat at its terminal's quay (HB2, 2026-11-09), as lines and a hook in world plots: where each lies, where its hook is and wh... | 2 |
| [QuayRail.java](QuayRail.md) | 148 | 10 | Which terminals on the harbour's quay the railway reaches (HB2, 2026-11-09), and the plots of track between their boxes: pure, from the shore and t... | 3 |
| [Rankings.java](Rankings.md) | 736 | 39 | The Rankings board as the screen draws it: a measure's table of ranked places, the active city among the real world's 196 nations and 62 city regio... | 6 |
| [RealDollar.java](RealDollar.md) | 137 | 9 | The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and ... | 6 |
| [Recipe.java](Recipe.md) | 198 | 24 | A way a building runs: what it uses, what it makes, who it needs and what it draws, and when it is known and what piece of equipment it needs. | 20 |
| [RecipeChange.java](RecipeChange.md) | 68 | 5 | One discovery's signed undo, relative to a template's immutable modern figures. | 9 |
| [RecipeChanges.java](RecipeChanges.md) | 627 | 47 | Composes independent discovery changes on the selected process recipe. | 13 |
| [Recipes.java](Recipes.md) | 494 | 33 | Chooses the recipe each part of a stack runs, at the city's own prices, and sets the template's figures to what its buildings run: the driver of R1... | 19 |
| [Recording.java](Recording.md) | 402 | 23 | One recorded city on disk (TR1, 2026-10-31): the folder {@code recordings/<id>/} that "Record this city" keeps, and the one place that knows its fi... | 10 |
| [RefineryView.java](RefineryView.md) | 1,618 | 82 | The refinery's month as a picture (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): the crude it ran and where that came from, the column's cuts,... | 10 |
| [ReplayCity.java](ReplayCity.md) | 192 | 20 | The throwaway copy of a recorded city that replay shows (TR1, 2026-10-31): a Game of its own, in a scratch folder, that loads any recorded month an... | 6 |
| [ReplayGraphs.java](ReplayGraphs.md) | 92 | 6 | The faint lines behind the city in a replay (TR1, 2026-10-31), without the toolkit: a recorded series read once from the recording's own history, e... | 2 |
| [Research.java](Research.md) | 1,168 | 98 | The city's research (R3): the grants the player sets, the posts they buy, the researchers who fill them, the work piled on each open problem, the d... | 31 |
| [ResearchCatalog.java](ResearchCatalog.md) | 408 | 27 | The research catalogue (R2): every discovery the city's schools can make, read out of research.json: its code, the field that researches it, what i... | 51 |
| [ResearchPreview.java](ResearchPreview.md) | 450 | 26 | What a set of research dials would find in this city over the next YEARS years, from the DISTRIBUTION of the hidden sizes and never from the city's... | 5 |
| [ResearchView.java](ResearchView.md) | 1,154 | 94 | What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each sect... | 15 |
| [Resource.java](Resource.md) | 226 | 13 | The eight things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields ... | 85 |
| [RetiredHousehold.java](RetiredHousehold.md) | 78 | 11 | A household with nobody of working age in it: a senior or an elder, alone or as a couple, at the pay tier its people retired from. | 3 |
| [Rollover.java](Rollover.md) | 382 | 34 | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). | 21 |
| [RunWorks.java](RunWorks.md) | 135 | 4 | What a highway or a railway line needs built on each class of ground beyond what it costs on the flat - cuts and fills on foothill, a viaduct on st... | 5 |
| [SafetyType.java](SafetyType.md) | 72 | 4 | What a safety building does: police, or prison cells. | 14 |
| [SalesTaxLedger.java](SalesTaxLedger.md) | 228 | 22 | The month's sales tax, as tax payable less input tax credits. | 5 |
| [SaveCopy.java](SaveCopy.md) | 54 | 5 | The small copies a save's snapshot is made of: a list or map copied, with each element copied too where the elements can change. | 9 |
| [SaveHeader.java](SaveHeader.md) | 119 | 20 | Just enough of a save to label it on the slot list. | 11 |
| [SaveWriter.java](SaveWriter.md) | 294 | 20 | The one thread saves are written on, for the whole JVM: it takes a finished snapshot of a city and turns it into the save, the map and the history ... | 9 |
| [SavedCities.java](SavedCities.md) | 327 | 24 | The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read o... | 6 |
| [SchoolingLadder.java](SchoolingLadder.md) | 173 | 15 | What each qualification pays after its course's cost, as a level monthly wage. | 7 |
| [SeaLanes.java](SeaLanes.md) | 210 | 15 | The sea lanes a map draws: every leg of every terminal's route once - a leg that many routes share, once - indexed by the district cells it reaches... | 4 |
| [SeaRoutes.java](SeaRoutes.md) | 974 | 45 | Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the o... | 6 |
| [Sector.java](Sector.md) | 4,426 | 315 | One business in the city, and the template every sector extends. | 191 |
| [SectorBooks.java](SectorBooks.md) | 1,030 | 60 | A month of books for every business in the city, and last month's too. | 30 |
| [SectorFlow.java](SectorFlow.md) | 267 | 17 | One business's month as a flow (0.7.30): what went in, what its plant made of it and what held the plant back, and what came out - each good's unit... | 5 |
| [SectorState.java](SectorState.md) | 294 | 6 | One sector, as a save carries it. | 19 |
| [SectorStatements.java](SectorStatements.md) | 1,084 | 66 | A business's month as formal statements (0.7.74): the income statement classified down through gross and operating profit, the balance sheet in cur... | 22 |
| [Sectors.java](Sectors.md) | 516 | 49 | Every sector in the city, in one order, by one name. | 174 |
| [ServicesManager.java](ServicesManager.md) | 453 | 35 | / | 7 |
| [ShadowBasket.java](ShadowBasket.md) | 202 | 1 | What a played city WOULD buy, measured against what it spends today. | 0 |
| [ShipShapes.java](ShipShapes.md) | 169 | 10 | How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, i... | 8 |
| [ShortTermTBill.java](ShortTermTBill.md) | 121 | 14 | A bill: borrow now, repay one lump, no coupon - at 1, 3, 6 or 12 months, each sale its own line (CD2). | 13 |
| [Sickness.java](Sickness.md) | 550 | 40 | Who has been sick, and for how long - and the ones it kills. | 15 |
| [SimulationEngine.java](SimulationEngine.md) | 264 | 5 | The order the month runs in: the sites advance, the roads, the posts and the wages, the people, then the economy and the services. | 1 |
| [SitePreparation.java](SitePreparation.md) | 74 | 3 | What it takes to prepare a foothill site for a building, as builders' work: the earth a pad is cut and filled in and the wall where it meets the sl... | 8 |
| [SocialSecurity.java](SocialSecurity.md) | 150 | 9 | Contributions off every wage, and a pension for everyone too old to work. | 5 |
| [StrategicReserve.java](StrategicReserve.md) | 238 | 32 | The city's strategic reserve of crude oil: what it holds, what it paid for it, and what the player has told it to fill and release (0.7.85, batch O... | 15 |
| [StreetCurves.java](StreetCurves.md) | 152 | 8 | A street's smooth line from the plots it is laid on, and the plots that line's band reaches: pure - plots in, points in plots out, no plan and no s... | 4 |
| [StudentHousehold.java](StudentHousehold.md) | 61 | 10 | Full-time students, as one ledger. | 6 |
| [Substations.java](Substations.md) | 51 | 6 | The city's wires: one substation for each 100 MW of peak, and their own costs in the power bill. | 18 |
| [SupplierCredit.java](SupplierCredit.md) | 255 | 21 | What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time. | 8 |
| [TaxPolicy.java](TaxPolicy.md) | 1,846 | 117 | The city's tax rates - the revenue half of what the player actually decides. | 74 |
| [TempDirs.java](TempDirs.md) | 141 | 7 | The one place a harness, or a scratch city, gets a temporary folder from: one folder per JVM under the system temp folder, deleted when that JVM ex... | 105 |
| [TilePainter.java](TilePainter.md) | 950 | 45 | Paints one tile of the city map: from the tile's ground, what the city owns of it, its district's street plan through it - each street's kind, widt... | 26 |
| [TileRaster.java](TileRaster.md) | 4,563 | 167 | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixe... | 32 |
| [TimeSkipReport.java](TimeSkipReport.md) | 682 | 68 | What happened while you were not watching. | 8 |
| [Timing.java](Timing.md) | 208 | 16 | The suite's clock verdicts and the soft skip for work whose only verdict is time. | 24 |
| [TrackCurves.java](TrackCurves.md) | 377 | 27 | The railway's curves as one geometry, for the drawing and for the trains' routes alike (FX5, 0.8.18): a polyline's corners rounded with arcs of a r... | 9 |
| [Trade.java](Trade.md) | 40 | 4 | One fill: somebody sold somebody some units of a good at a price. | 41 |
| [TradeBand.java](TradeBand.md) | 253 | 21 | The freight band of what the city trades, split into its legs, as the Trade tab draws it (batch FR4; docs/studies/spec-freight-and-infrastructure.m... | 2 |
| [TradeLanes.java](TradeLanes.md) | 907 | 74 | The city's trade as routes on the Earth's map, without the toolkit: one lane from the city toward each partner, the lanes that go the same way shar... | 11 |
| [Traffic.java](Traffic.md) | 60 | 3 | The three things that move, which used to be one number. | 42 |
| [TrainSchedule.java](TrainSchedule.md) | 710 | 54 | The month's trains, as a pure function of time: which trains the city's railed tonnes make, when each reaches its yard, and where each one is at an... | 15 |
| [TrainShapes.java](TrainShapes.md) | 92 | 6 | How the map draws a train: each vehicle's body and its load's mark as points in plots about where it is and the way it heads, and their colours by ... | 3 |
| [TreasuryFund.java](TreasuryFund.md) | 1,249 | 165 | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand,... | 49 |
| [TreasuryJournal.java](TreasuryJournal.md) | 292 | 18 | The treasury's journal: every movement of the city's cash that is neither a budget line nor paper raised or repaid, recorded by name as it happens,... | 14 |
| [TreasuryLine.java](TreasuryLine.md) | 185 | 2 | Every kind of payment the treasury makes, and whether it is a promise. | 10 |
| [TruckSchedule.java](TruckSchedule.md) | 592 | 38 | The month's trucks, as a pure function of time: which loads Haulage's trucks carry, when each reaches its gate, and where each one is at any moment... | 5 |
| [TruckShapes.java](TruckShapes.md) | 95 | 4 | How the map draws a truck: its body as points in plots about where it is and the way it heads, and its colour by what it carries - what ui/MapView ... | 1 |
| [UnemployedHousehold.java](UnemployedHousehold.md) | 88 | 14 | Adults who are out of work, as one ledger per situation. | 8 |
| [Unemployment.java](Unemployment.md) | 614 | 41 | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. | 14 |
| [UtilitiesHandler.java](UtilitiesHandler.md) | 1,444 | 133 | sections: WATER SUPPLY, THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7)... | 50 |
| [VaultView.java](VaultView.md) | 66 | 2 | The vault in eight, and gold (WN6, GD2; docs/studies/spec-nations.md 8, the Trade screen's The reserves with the eight moneys in a table): what the... | 2 |
| [WageBand.java](WageBand.md) | 170 | 7 | The four education bands the wage tax is set by. | 54 |
| [WageLadder.java](WageLadder.md) | 147 | 10 | The wage ladder a city is founded on: today's (PayTier's) or 1901's (the census's). | 10 |
| [WantedPosts.java](WantedPosts.md) | 231 | 22 | The posts the city's businesses wanted this month and the staffing gate would not let them open, and the part of them the labour market prices as i... | 4 |
| [WorkingHousehold.java](WorkingHousehold.md) | 83 | 13 | A household with an earner in it, at one pay tier. | 12 |
| [WorksValue.java](WorksValue.md) | 698 | 47 | A work's discounted cost and the output its service prevents losing (HW1). | 14 |
| [World.java](World.md) | 2,162 | 120 | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and ri... | 116 |
| [WorldChain.java](WorldChain.md) | 151 | 4 | The world market's chain: for each good made of other goods, which goods and in what shares of its price, and the order the January solves the good... | 2 |
| [WorldData.java](WorldData.md) | 1,192 | 109 | The reader of world-1.bin, the nations' world as data: the 196 nations and the areas no nation holds, their borders (an owner a 15" cell, and the l... | 34 |
| [WorldEconomy.java](WorldEconomy.md) | 550 | 27 | The rest of the world, which has its own inflation and did not use to: the in-game US dollar's price level, which is the US's own path where the ci... | 16 |
| [WorldMarket.java](WorldMarket.md) | 2,445 | 145 | The world market: each traded good's price in each of the 196 nations on a smooth spatial equilibrium (a CES gravity model), solved once a year, an... | 21 |
| [WorldMeasures.java](WorldMeasures.md) | 388 | 46 | The Rankings' real-world figures: world-2025.json's 196 nations and city regions, the 70 measures each is ranked on, every place's latest figure of... | 8 |
| [WorldMoneys.java](WorldMoneys.md) | 800 | 56 | The world's moneys against the US dollar, a month at a time (WN4; spec-exchange-rates 3.7, spec-nations 5.1): each money's rate is its purchasing-p... | 32 |
| [YearBook.java](YearBook.md) | 1,744 | 87 | The run, one line a year - for READING rather than for drawing. | 25 |
| [YieldCurve.java](YieldCurve.md) | 295 | 21 | The city's curve: a discount factor for every month to fifty years, built from the dial the market expects, the credit spreads, and the shape the t... | 12 |
| [Zoning.java](Zoning.md) | 411 | 40 | The city's zoning (0.7.107, batch Z1): ground held for one sector's buildings - a share of the city's dry ground or a size in square kilometres, at... | 24 |
| [ZoningView.java](ZoningView.md) | 421 | 24 | Policy › Zoning as figures (0.7.109, batch Z2; the project's spec-zoning.md 4, the zoning study's three boards): the hub's four figures, the city's... | 5 |

## sectors (31 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [Agriculture.java](Agriculture.md) | 607 | 27 | The fields, and what they cost the city in ground. | 15 |
| [Automotive.java](Automotive.md) | 315 | 8 | The automobile industry. | 4 |
| [BusinessServices.java](BusinessServices.md) | 415 | 17 | Somebody else's work, done here, paid for from outside. | 9 |
| [CementConcrete.java](CementConcrete.md) | 73 | 2 | Kilns and cement plants - Cement & Concrete (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.12 and 5's I7 row; the study's 6): stone's pr... | 6 |
| [CoalMining.java](CoalMining.md) | 503 | 27 | Coal mines and the plants that wash their coal - Coal Mining (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.10 and 5's I6 row; the study... | 11 |
| [Construction.java](Construction.md) | 1,384 | 81 | The builders. | 30 |
| [CopperMining.java](CopperMining.md) | 305 | 13 | Copper mines and the leach plants on oxide fields - Copper Mining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.4 and 5's I8 row; the s... | 11 |
| [CopperRefining.java](CopperRefining.md) | 299 | 17 | Smelters, refineries and rod mills - Copper Refining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12 and 5's I8 row; the stu... | 15 |
| [ExtractionSector.java](ExtractionSector.md) | 668 | 42 | A sector that digs or cuts one resource out of the ground the city owns: the template every extraction industry of the materials chains extends (0.... | 20 |
| [FoodIndustry.java](FoodIndustry.md) | 105 | 7 | The mills and the plants that feed the shops. | 5 |
| [FoodProcessing.java](FoodProcessing.md) | 610 | 15 | The plants between the farm and the shelf. | 3 |
| [Forestry.java](Forestry.md) | 655 | 39 | The crews that cut the city's forest, and the plantations they plant - Forestry (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.11 and 5'... | 12 |
| [Freight.java](Freight.md) | 2,239 | 112 | The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 3.1, D1). | 47 |
| [GoldMining.java](GoldMining.md) | 171 | 16 | Gold mines and their mills recover fine ounces at a geology-dependent grade. | 4 |
| [Haulage.java](Haulage.md) | 1,561 | 99 | Freight's Haulage division: the trucks that carry the city leg of every bulk tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.m... | 30 |
| [HeavyIndustry.java](HeavyIndustry.md) | 370 | 20 | The mills - Iron & Steel since 0.7.120, under the key it was saved by, "Heavy Industry": the arc furnaces, which melt scrap into steel, and since 0... | 35 |
| [LuxuryRetail.java](LuxuryRetail.md) | 590 | 24 | The luxury shops. | 6 |
| [Manufacturing.java](Manufacturing.md) | 440 | 22 | What the city makes out of its own steel, and ships. | 7 |
| [Materials.java](Materials.md) | 88 | 4 | The materials plant. | 1 |
| [Mining.java](Mining.md) | 85 | 4 | Iron mines - Iron Mining since 0.7.120, under the key it was saved by, "Mining" (Jerus's Q2, 2026-10-08: rename, keeping the saved keys). | 32 |
| [Oil.java](Oil.md) | 1,759 | 117 | Oil wells. | 20 |
| [PortDivision.java](PortDivision.md) | 363 | 27 | Freight's Ports division: the crews that work the city's berths, paid by the shippers who use them and paying the city for the quay (FR3, 2026-10-1... | 11 |
| [ProcessingSector.java](ProcessingSector.md) | 777 | 63 | A sector that turns goods into other goods in works it builds by what pays: the template every processing industry of the materials chains extends ... | 22 |
| [Quarrying.java](Quarrying.md) | 436 | 24 | Quarries - Quarrying (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.4, 2.10 and 5's I7 row; the study's 6): the third materials chain's ... | 9 |
| [RealEstate.java](RealEstate.md) | 1,067 | 98 | The landlords. | 24 |
| [RefineryFlow.java](RefineryFlow.md) | 431 | 38 | The refinery as a campus of units, and a month's flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3): the crude units' run cut into ... | 23 |
| [Refining.java](Refining.md) | 2,024 | 118 | The refinery. | 27 |
| [Restaurants.java](Restaurants.md) | 532 | 25 | The kitchens. | 8 |
| [Retail.java](Retail.md) | 1,409 | 105 | The shops. | 44 |
| [SpreadPlanner.java](SpreadPlanner.md) | 858 | 72 | The spread planner (0.7.82, batch O5; docs/studies/spec-oil.md 2.4, and docs/studies/spec-materials.md 2.6): what a processing sector orders - of t... | 45 |
| [WoodProducts.java](WoodProducts.md) | 107 | 2 | Sawmills, and the panel, pulp, paper and pellet mills - Wood Products (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.7, 2.8, 2.12 and 5... | 7 |

## interface (55 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [BankScreen.java](BankScreen.md) | 4,808 | 184 | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figu... | 7 |
| [Branding.java](Branding.md) | 124 | 4 | The game's identity on screen: its icon on the window (every size of resources/icons/ handed to the stage, so Windows takes the one it needs for th... | 3 |
| [BuildScreen.java](BuildScreen.md) | 6,655 | 248 | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.... | 17 |
| [CapChips.java](CapChips.md) | 213 | 8 | The highest yield a sale will take, as three chips and a yield box: fair plus a point (the default), a yield the player names, or Fill it (CD2, D28). | 3 |
| [CentralBankScreen.java](CentralBankScreen.md) | 923 | 51 | The central bank's pages in the Bank tab: its frame - the head "Bank › Central bank", the strip of nine chips - and the nine pages behind them: the... | 5 |
| [ChartFitBench.java](ChartFitBench.md) | 130 | 3 | The full-screen chart on a scene of its own - no stage, no window - laid out at a window's size with some lines picked, for ChartFitCheck (FW10): t... | 1 |
| [CityBuilderSim.java](CityBuilderSim.md) | 55 | 2 | The way in. | 0 |
| [ConstructionScreen.java](ConstructionScreen.md) | 1,422 | 51 | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - pri... | 2 |
| [FinancesScreen.java](FinancesScreen.md) | 7,204 | 302 | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece an... | 9 |
| [FollowBench.java](FollowBench.md) | 918 | 33 | The Reenactor on the real window (TR2, 2026-11-09): a city's window made as the game makes it (UserInterface.start on an undecorated stage that is ... | 6 |
| [FollowRules.java](FollowRules.md) | 106 | 3 | What Follow re-enacts of a logged sitting and what it drops (TR2, 2026-11-09): the verdict on one logged input event, from the screen the player ha... | 4 |
| [FoundingScreen.java](FoundingScreen.md) | 1,583 | 69 | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. | 2 |
| [FundScreen.java](FundScreen.md) | 2,689 | 108 | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with it... | 4 |
| [GhostLayer.java](GhostLayer.md) | 141 | 11 | The ghost cursor (TR2, 2026-11-09): a see-through pointer arrow drawn at a scene point over the replay's window, in {@link Palette#GHOST}, with a r... | 1 |
| [GovernmentScreen.java](GovernmentScreen.md) | 2,809 | 120 | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, w... | 3 |
| [HistoryScreen.java](HistoryScreen.md) | 3,386 | 124 | City History: the city as a shape over time. | 15 |
| [HouseholdDebtPanel.java](HouseholdDebtPanel.md) | 132 | 6 | The debt survey on Household money, after HS3's stage page (HC1/HC2). | 1 |
| [Icons.java](Icons.md) | 560 | 6 | The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one per category, and the few the frame draws (the money block, the "Needs... | 24 |
| [InfrastructureScreen.java](InfrastructureScreen.md) | 2,218 | 77 | The infrastructure tab: the roads, the trams, the freight and the ports - four pages under the five figures the whole tab is about, each page led b... | 2 |
| [InputRecorder.java](InputRecorder.md) | 207 | 14 | The window's input kept for the replay's ghost (TR1, 2026-10-31): while "Record this city" is on, the mouse and the keys of the sitting - and the m... | 1 |
| [IslandChip.java](IslandChip.md) | 48 | 2 | The chip the map draws on a landmass no port links: its lines of amber mono caption on a dark rounded plate the width of the longer line (MT1, BK1-L). | 2 |
| [Kept.java](Kept.md) | 913 | 91 | A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a screen's code made at its last draw, handed back to the... | 33 |
| [KeptBench.java](KeptBench.md) | 6,233 | 636 | The game's kept screens on scenes of their own, without a stage, for KeptCheck and KeptTimingCheck. | 6 |
| [Ladder.java](Ladder.md) | 449 | 31 | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying... | 16 |
| [LandScreen.java](LandScreen.md) | 2,586 | 105 | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.... | 4 |
| [Levers.java](Levers.md) | 166 | 4 | The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36) - the dial on a card with what it does beside it, which every dial on t... | 3 |
| [MapView.java](MapView.md) | 6,436 | 274 | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's ... | 6 |
| [MiniMap.java](MiniMap.md) | 480 | 22 | The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288... | 2 |
| [Money.java](Money.md) | 411 | 36 | Every figure the interface prints as money, in one place. | 35 |
| [Palette.java](Palette.md) | 741 | 27 | Every colour, size and spacing this game is allowed to use, in one place. | 43 |
| [PartnersPage.java](PartnersPage.md) | 1,184 | 59 | Trade &gt; Partners (WN6; docs/studies/spec-nations.md 8, board 2) and a nation's page behind it: who the city trades with, nation by nation; for a... | 2 |
| [PeopleScreen.java](PeopleScreen.md) | 4,897 | 170 | The People tab and its second page, Household money. | 5 |
| [Pieces.java](Pieces.md) | 6,751 | 305 | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals ba... | 32 |
| [PolicyScreen.java](PolicyScreen.md) | 6,585 | 333 | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it w... | 15 |
| [RankingsScreen.java](RankingsScreen.md) | 1,778 | 81 | The Rankings tab: the active city on a board of the real world's nations and city regions (or of the nations of its own world), measure by measure,... | 2 |
| [Reenactor.java](Reenactor.md) | 463 | 33 | Re-enacts one logged input event in the window (TR2, 2026-11-09): the mouse moved over the copy's real nodes, pressed, dragged, released and clicke... | 2 |
| [ReplayCamera.java](ReplayCamera.md) | 396 | 47 | The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back, the load of a move... | 4 |
| [ReplayGraphLayer.java](ReplayGraphLayer.md) | 197 | 9 | The faint GDP and population lines behind the city in a replay (TR1, 2026-10-31): one mouse-transparent canvas laid over the window's content and u... | 1 |
| [ReplayMode.java](ReplayMode.md) | 1,641 | 86 | Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own window, with a time bar a... | 5 |
| [ReplayMoveProbe.java](ReplayMoveProbe.md) | 1,165 | 39 | What a replay's camera moves show unpainted, measured: it opens the real window full screen on a recorded city in replay mode, runs the seven moves... | 1 |
| [ResearchScreen.java](ResearchScreen.md) | 1,197 | 73 | Policy › Research (R5, slice B1; R3b, slice B): the sixth Policy area, drawn in Policy's own style - three pages: the Overview (who is at research ... | 3 |
| [SchoolingProbe.java](SchoolingProbe.md) | 59 | 2 | Reads the qualification page's actual drawing inputs without starting a JavaFX window. | 1 |
| [SectorScreen.java](SectorScreen.md) | 7,230 | 291 | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with la... | 9 |
| [ServicesScreen.java](ServicesScreen.md) | 3,618 | 141 | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one p... | 5 |
| [SignFont.java](SignFont.md) | 71 | 4 | Which signs for the city's money the interface's own faces can draw. | 2 |
| [Statement.java](Statement.md) | 517 | 25 | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank... | 42 |
| [StatementView.java](StatementView.md) | 646 | 39 | A formal statement on the page (0.7.74): SectorStatements' rows set as an accountant sets them - a title block, the columns (note, this month, last... | 4 |
| [SummaryScreen.java](SummaryScreen.md) | 1,547 | 33 | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen... | 10 |
| [TimeChart.java](TimeChart.md) | 1,647 | 91 | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, th... | 13 |
| [ToastRoom.java](ToastRoom.md) | 113 | 5 | The corner a toast stands in, given up by the page (TS1): while a toast shows, the page's foot takes a bottom padding as tall as the toasts and the... | 2 |
| [TradeScreen.java](TradeScreen.md) | 4,040 | 172 | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what ... | 7 |
| [UiProbe.java](UiProbe.md) | 791 | 25 | What the window costs the JavaFX thread as a city ages, measured: it grows the playtest's city to the months it is given, opens the real window on ... | 0 |
| [UserInterface.java](UserInterface.md) | 7,483 | 252 | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating a... | 43 |
| [VaultTable.java](VaultTable.md) | 238 | 13 | The vault in eight moneys and gold (WN6, GD2; docs/studies/spec-nations.md 8, board 3): the first card of Trade &gt; The reserves where the vault h... | 1 |
| [ZoningScreen.java](ZoningScreen.md) | 1,149 | 72 | Policy › Zoning (0.7.109, batch Z2): the fifth Policy area - the city's ground and its zones on one page, a sector's zone on its own page of dial c... | 3 |

## harnesses (260 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgricultureCheck.java](AgricultureCheck.md) | 463 | 7 | The ground under the loaf. | 0 |
| [AllChecks.java](AllChecks.md) | 600 | 29 | Runs every harness, one JVM each, and says which failed. | 7 |
| [ArrivalMixCheck.java](ArrivalMixCheck.md) | 398 | 21 | The skill mix of the arrivals by year (R0d slice 2): the year's mix in a city founded the 1900 way, and WageBand's ceilings in every other city. | 0 |
| [AskProceedsCheck.java](AskProceedsCheck.md) | 562 | 29 | The bond proceeds an open ask has raised pay its works, as a fenced fund pays them: the treasury holds them outside the operating reserve, the orde... | 0 |
| [AuctionCheck.java](AuctionCheck.md) | 1,860 | 45 | The city's paper sold at auction: one clearing yield, and what it costs. | 2 |
| [AutoBuildCheck.java](AutoBuildCheck.md) | 2,191 | 50 | Automatic building (0.7.73, batch N4): AutoBuilder's month held to its rules, in a city it builds for decades and in fixtures that cause each of th... | 2 |
| [BankCheck.java](BankCheck.md) | 5,155 | 36 | The commercial bank, and the families it discharges. | 3 |
| [BankFailureCheck.java](BankFailureCheck.md) | 734 | 23 | The hands-off bank's failures, caused separately and counted across Random cities. | 0 |
| [BasketYearCheck.java](BasketYearCheck.md) | 807 | 37 | The household's basket by year (R0e slice 1): the year's basket in a city founded the 1900 way, today's in every other. | 0 |
| [BondCheck.java](BondCheck.md) | 2,096 | 57 | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for co... | 2 |
| [BooksCheck.java](BooksCheck.md) | 299 | 3 | Verifies a sector's income statement and balance sheet, off the template. | 0 |
| [BrandingCheck.java](BrandingCheck.md) | 219 | 5 | The game's icon is in the jar at every size it is given, the exe's .ico carries the sizes it should, the store's title art is drawn to the start sc... | 0 |
| [BridgeCheck.java](BridgeCheck.md) | 977 | 39 | The long bridges checked (ET6, 2026-10-26; docs/queue/ET6-notes.md): the survey of a crossing on grids of its own making and on the Earth, what a s... | 3 |
| [BridgeMapCheck.java](BridgeMapCheck.md) | 1,015 | 51 | A long bridge that stands is laid by the runs, kept clear by the plans and drawn with its deck (ET6; docs/studies/spec-earth2-terrain.md 6.5 and 6.6). | 1 |
| [BuildAdviceCheck.java](BuildAdviceCheck.md) | 2,222 | 67 | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city -... | 3 |
| [BuildCardCheck.java](BuildCardCheck.md) | 2,361 | 42 | The build card (0.7.25): BuildCard's figures for every building in buildings.json (143 since FR3, the Bulk Berth) held to the model's own reads - t... | 1 |
| [BuildMenuCheck.java](BuildMenuCheck.md) | 288 | 2 | Verifies that every building in the game can describe itself. | 0 |
| [BuildersFirstCheck.java](BuildersFirstCheck.md) | 691 | 17 | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the month... | 3 |
| [BuildingDataCheck.java](BuildingDataCheck.md) | 975 | 6 | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. | 0 |
| [BuildingStackCheck.java](BuildingStackCheck.md) | 680 | 14 | A building stack that holds more than 2^31 - 1 buildings reads back whole: through an order, through the save and the load, through ScaleCheck's sc... | 0 |
| [BusinessServicesCheck.java](BusinessServicesCheck.md) | 528 | 6 | The sector whose customer is not in the city. | 0 |
| [BuybackCheck.java](BuybackCheck.md) | 1,579 | 48 | The city's paper bought back in a reverse auction (CD4, spec-city-debt.md section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central ba... | 0 |
| [CalendarCheck.java](CalendarCheck.md) | 313 | 7 | The date on the status bar, and the log of what the city has finished. | 0 |
| [CallCheck.java](CallCheck.md) | 870 | 32 | Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be sold ... | 1 |
| [CapitalFlowCheck.java](CapitalFlowCheck.md) | 883 | 10 | Hot money: does it come for the right reason, and does it leave for one? | 0 |
| [CapitalGainsCheck.java](CapitalGainsCheck.md) | 1,025 | 33 | Household average-cost stocks and investment taxes, through the real fills, census and city (HT1). | 2 |
| [CarCheck.java](CarCheck.md) | 1,423 | 12 | The cars: who buys one, what it costs them, and what it does to the road. | 2 |
| [CardCheck.java](CardCheck.md) | 434 | 15 | Cards inside a household cell: shapes, shocks, minimums, arrears, and the bank (HC1). | 1 |
| [CarryTradeCheck.java](CarryTradeCheck.md) | 430 | 5 | The carry trade: the other side of hot money, and the bank's first borrower. | 0 |
| [CausesOfDeathCheck.java](CausesOfDeathCheck.md) | 1,023 | 30 | Proves the causes of death of 1900 (R0b, slice 1): the model on its own, before the month hands its rates to the cohorts. | 1 |
| [CementConcreteCheck.java](CementConcreteCheck.md) | 368 | 11 | Cement & Concrete, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.12, 4 and 5's I7 row; the study's - materials-chains-rese... | 0 |
| [CentralBankCheck.java](CentralBankCheck.md) | 2,812 | 55 | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its tw... | 0 |
| [CentralBankStatementCheck.java](CentralBankStatementCheck.md) | 537 | 20 | The central bank's own statements (CBK1): CentralBankStatements' income statement, balance sheet and money made and destroyed held to the central b... | 1 |
| [ChartCheck.java](ChartCheck.md) | 962 | 28 | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the tick... | 0 |
| [ChartFitCheck.java](ChartFitCheck.md) | 156 | 4 | The full-screen chart's foot is on the screen (FW10): with the legend wrapped to two rows or three, the chart still ends inside the window and the ... | 0 |
| [ChildcareCheck.java](ChildcareCheck.md) | 641 | 22 | Childcare resized (0.7.71, batch N2): the three childcare centres, the build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS THE ... | 7 |
| [ClockCheck.java](ClockCheck.md) | 690 | 25 | The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and drops what it could not run, the header's speed sa... | 1 |
| [CoalMiningCheck.java](CoalMiningCheck.md) | 675 | 16 | Coal Mining, checked (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.4, 2.10, 4 and 5's I6 row; the study's - materials-chains-research.m... | 3 |
| [ConstructionControlCheck.java](ConstructionControlCheck.md) | 872 | 21 | The player's hand on the construction queue (0.7.22): each of ConstructionControl's five rules held to its own arithmetic, in a played city. | 0 |
| [ConstructionTrapCheck.java](ConstructionTrapCheck.md) | 1,653 | 44 | The builders short (FC1) and the foreign crews always on offer beside them (FC2): what an order pays a point of work as the repairs leave the build... | 3 |
| [ConsumptionCheck.java](ConsumptionCheck.md) | 343 | 4 | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. | 0 |
| [CopperBufferCheck.java](CopperBufferCheck.md) | 250 | 10 | A stock buffer must not buy feed worth more than its undemanded output (CPB1). | 0 |
| [CopperMiningCheck.java](CopperMiningCheck.md) | 526 | 13 | Copper Mining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 4 and 5's I8 row; the study's - materials-chains-research.... | 5 |
| [CopperRefiningCheck.java](CopperRefiningCheck.md) | 1,485 | 31 | Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's - materials-chains-r... | 1 |
| [CreditCheck.java](CreditCheck.md) | 2,245 | 19 | Verifies private-sector credit: pricing, origination, rollover, cash conservation. | 0 |
| [CreditCrunchCheck.java](CreditCrunchCheck.md) | 711 | 19 | A credit crunch and the city's homes (MC1, 2026-11-20): a bank whose leverage ratio binds, standing in its band between its leverage minimum and it... | 0 |
| [CreditsCheck.java](CreditsCheck.md) | 462 | 10 | The Credits page shows every attribution file the exe ships, in full, and the fonts' notice and licence, the libraries' licences and notices, the J... | 0 |
| [CrimeCheck.java](CrimeCheck.md) | 370 | 8 | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. | 0 |
| [CrossingCheck.java](CrossingCheck.md) | 520 | 27 | Short crossings of bridgeable water (ET6; spec-earth2-terrain 6.2, D12): the shallow sea is water a road or a railway may bridge, exactly as fresh ... | 0 |
| [CurrencyCheck.java](CurrencyCheck.md) | 1,091 | 27 | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry ap... | 6 |
| [CurrencyRegimeCheck.java](CurrencyRegimeCheck.md) | 2,529 | 77 | Proves the D$'s four regimes - Float, Lean against falls (the default), Band and Peg to the US$ - hold the currency where they say and cost what th... | 6 |
| [CurveCheck.java](CurveCheck.md) | 897 | 42 | Proves the curve from the dial the market expects (CD1, spec-city-debt.md section 3): the expected dial's path, the zero rates it compounds to, the... | 0 |
| [DeathCareFirstCheck.java](DeathCareFirstCheck.md) | 556 | 19 | Death care first while the dead wait (0.7.129, batch T1-B): the rule that serves the city's own cemeteries and crematoria first out of its share of... | 6 |
| [DeathRecordCheck.java](DeathRecordCheck.md) | 432 | 10 | The running totals of the dead, by age and for the orphans and the unhoused. | 0 |
| [DebtExchangeCheck.java](DebtExchangeCheck.md) | 770 | 23 | The exchange gain (loss) on the city's foreign-currency debt (CD7, spec-city-debt.md section 5): each dollar piece carried in US$ and read in D$ at... | 0 |
| [DebtMoneysCheck.java](DebtMoneysCheck.md) | 637 | 19 | The city's paper in any of the eight moneys (CD8, Jerus 2026-10-23): the city sells bills and bonds abroad in the euro, the yen, the pound, the yua... | 2 |
| [DebtStatisticsCheck.java](DebtStatisticsCheck.md) | 120 | 6 | The statistics office's debt figures against distributions and their component sums (HC2). | 0 |
| [DenominationCheck.java](DenominationCheck.md) | 1,258 | 39 | A currency reform is a change of units, and this is how we know. | 0 |
| [DeskCheck.java](DeskCheck.md) | 1,365 | 35 | The debt desk's figures (CD6, spec-city-debt.md sections 7 and 8): the figures the debt screens used to work out for themselves, held to the model ... | 1 |
| [DeterminismCheck.java](DeterminismCheck.md) | 446 | 19 | The month reads nothing of the machine it runs on: the same city, played in JVMs set up as opposite machines, comes out the same to the bit. | 2 |
| [DiscoveryCheck.java](DiscoveryCheck.md) | 1,380 | 44 | The city finds discoveries of its own, and a discovery found here is known here from the month it was found (R3, slice A: Research, and the seam in... | 6 |
| [DistressBleedCheck.java](DistressBleedCheck.md) | 598 | 18 | The distress rule for a price taker whose operating losses the bank's shortfall desk is still lending: it sells its plant over BusinessInvestment.B... | 0 |
| [DriverlessTruckCheck.java](DriverlessTruckCheck.md) | 419 | 23 | The driverless truck (R6b, FE1): the Truck Depot's second piece of equipment, the kit that ends the driver's cab, and what a depot fitted with it d... | 0 |
| [ER1Check.java](ER1Check.md) | 332 | 12 | The growth hold when a full city's treasury cannot buy a whole land offer, and its repair: the offer funded on the land office's bond while a secto... | 0 |
| [EarthCheck.java](EarthCheck.md) | 1,066 | 33 | The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and 8): EarthProj's projection, blend and areas held to the spec's measures, a... | 16 |
| [EarthDepositsCheck.java](EarthDepositsCheck.md) | 789 | 36 | Deposits where the geology says (0.8.12, batch EG1; docs/studies/spec-earth2-terrain.md 8.5): oil by a province's known petroleum, iron on shields ... | 0 |
| [EarthFoundingCheck.java](EarthFoundingCheck.md) | 706 | 18 | Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they a... | 0 |
| [EarthTerrainCheck.java](EarthTerrainCheck.md) | 2,673 | 65 | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three... | 4 |
| [EducationCheck.java](EducationCheck.md) | 1,971 | 19 | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. | 4 |
| [ElectrolyserCheck.java](ElectrolyserCheck.md) | 477 | 24 | Electrolysis at 46 kWh a kilogram (R6d, FE10: the US energy department's target for a PEM system, 55 today; projected 2034, window 2031-2037): the ... | 0 |
| [EnsembleCheck.java](EnsembleCheck.md) | 470 | 25 | Checks the ensemble runner, driver compilation, policy names and stored baseline. | 0 |
| [EquipmentCheck.java](EquipmentCheck.md) | 1,200 | 46 | Equipment as data, the count of buildings still without it on each stack, R0c's 1900 forms set from that count, and (slice B) what a piece would ea... | 20 |
| [EquityCheck.java](EquityCheck.md) | 390 | 5 | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. | 0 |
| [EssentialWorksCheck.java](EssentialWorksCheck.md) | 1,433 | 38 | Essential works ordered for their unmet service, including their ground (HW1). | 7 |
| [ExchangeCheck.java](ExchangeCheck.md) | 1,609 | 40 | Verifies the exchange on the order book (0.7.12 round 2): what the desk posts and what it is not obliged to take, who trades with whom and at what ... | 7 |
| [ExchangeRateCheck.java](ExchangeRateCheck.md) | 1,709 | 59 | Proves the D$ is a float that follows its parity and swings like a real currency (XR1, 2026-10-21): parity is the basket with rent held to a third,... | 2 |
| [ExpectationsCheck.java](ExpectationsCheck.md) | 563 | 21 | Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the ... | 0 |
| [FB13Check.java](FB13Check.md) | 600 | 13 | FB13's three texts, each asked of the model that says it: the island's words (AL1-W), the Government screen's coupons abroad (CD9-G) and the build ... | 0 |
| [FarmlandReserveCheck.java](FarmlandReserveCheck.md) | 419 | 17 | The farmland reserve, the model (RG1, 2026-11-25; Jerus, 2026-11-24: "Policy, that also lets farms crops as well be cheaper"): the fields' zone at ... | 0 |
| [FieldOperatorsCheck.java](FieldOperatorsCheck.md) | 474 | 22 | Driverless field work (R6d, FE8: Deere's goal of a fully autonomous corn and soybean season by 2030, projected 2033, window 2030-2036): the autonom... | 0 |
| [FitoutCheck.java](FitoutCheck.md) | 757 | 32 | A new building's equipment is chosen and priced at its order, kept on its site, saved, and delivered at its finish (R1e): Fitout, BuildingsStacks' ... | 0 |
| [FittingCheck.java](FittingCheck.md) | 1,414 | 43 | Fitting the equipment (R1b): the 1900 forms at rest between the presses, a Game's forms cleared when it founds or loads another city, the city foun... | 20 |
| [FloatLinkCheck.java](FloatLinkCheck.md) | 2,092 | 71 | The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's polic... | 4 |
| [FoodIndustryCheck.java](FoodIndustryCheck.md) | 67 | 2 | The mills counted in the heads their retirement measure serves (RT2). | 0 |
| [FoodProcessingCheck.java](FoodProcessingCheck.md) | 639 | 7 | The third of the shelf that arrives already made. | 0 |
| [ForeignCheck.java](ForeignCheck.md) | 2,206 | 18 | The balance of payments, and whether the boundary it is drawn on is honest. | 7 |
| [ForeignChoiceCheck.java](ForeignChoiceCheck.md) | 1,180 | 37 | The choices that use the foreign crews (FC2b): a payer - a business by its own sum, the city from its card - hands its share of one site's remainin... | 0 |
| [ForeignCrewsCheck.java](ForeignCrewsCheck.md) | 1,041 | 31 | The foreign crews always on offer (FC2): the threshold past which the city's orders go abroad, read at the exchange rate the city's money stands at... | 1 |
| [ForeignDebtCheck.java](ForeignDebtCheck.md) | 1,087 | 15 | Borrowing in somebody else's money. | 3 |
| [ForestryCheck.java](ForestryCheck.md) | 707 | 21 | Forestry, checked (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.11, 4 and 5's I9 row; the study's - materials-chains-research... | 4 |
| [FormatsCheck.java](FormatsCheck.md) | 49 | 2 | Proves the shared readable percentage rule (SX1), without the toolkit. | 0 |
| [Forms1900Check.java](Forms1900Check.md) | 1,033 | 39 | The farm's horses, the factory's steam, road freight's horse and wagon and the homes' lamp oil and coal, as forms a share of a sector or of the hom... | 14 |
| [FoundingYardCheck.java](FoundingYardCheck.md) | 635 | 27 | The founding yard (PT1-M4, 2026-10-12): the works department's free 400 points a month are retired and every city founds with one Builders' Yard st... | 0 |
| [FreightViewCheck.java](FreightViewCheck.md) | 716 | 26 | FreightView, the model behind the Freight screens, held to the study's board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's frei... | 0 |
| [FundCheck.java](FundCheck.md) | 1,978 | 37 | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). | 1 |
| [FundLedgerCheck.java](FundLedgerCheck.md) | 1,186 | 28 | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, thr... | 2 |
| [GapOrderCheck.java](GapOrderCheck.md) | 414 | 14 | A planner orders to the gap, not one building a month (OG1, 2026-11-22). | 0 |
| [GatewayCheck.java](GatewayCheck.md) | 1,498 | 49 | A city's gateway is the way the world is. | 5 |
| [GdpCheck.java](GdpCheck.md) | 1,297 | 12 | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock... | 0 |
| [GlobeCheck.java](GlobeCheck.md) | 3,499 | 110 | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, G... | 1 |
| [GoldMiningCheck.java](GoldMiningCheck.md) | 268 | 12 | Gold Mining from geological fields through the mill to exports and the mint. | 1 |
| [GoldPriceCheck.java](GoldPriceCheck.md) | 491 | 23 | Gold's price a month (batch GD1; spec-gold-money 4.1): GoldPrice's statutes, its table, its refusal of a table that is not whole, and the rule afte... | 0 |
| [GoldStandardCheck.java](GoldStandardCheck.md) | 1,119 | 33 | The gold standard (batch GD3; spec-gold-money 4.3): the D$ on Gold, a mint price that keeps its value when it is entered, the rate held inside the ... | 2 |
| [GovernmentPlayerCheck.java](GovernmentPlayerCheck.md) | 241 | 15 | The test player's annual wealth policy: both directions, boundaries, caps, the first eligible January and the actual skip/reload path. | 0 |
| [GovernmentViewCheck.java](GovernmentViewCheck.md) | 403 | 9 | The government's wealth bands and advice, measured without moving its books (GV1). | 0 |
| [GridCheck.java](GridCheck.md) | 621 | 22 | The block grid (0.7.65, batch M1): the city's owned ground as a quadtree, every plot's owner exact against a raster, and its offers six a side - ap... | 1 |
| [GroceryCheck.java](GroceryCheck.md) | 463 | 10 | Groceries at a price, the shelf cleared stickily, and food assistance (0.7.43): the households' demand curve, the clearing price, who is handed the... | 0 |
| [GroundInputCheck.java](GroundInputCheck.md) | 371 | 17 | A works the ground limits buys the inputs of what its ground gives it, not of its plant (NG1). | 0 |
| [GroundSkipCheck.java](GroundSkipCheck.md) | 338 | 12 | The test player's ground across its long skips (LP-G1): a growing town whose ground holds the look's BuildAdvice.HORIZON months but not the skip af... | 0 |
| [HaulageCheck.java](HaulageCheck.md) | 1,673 | 44 | Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is billed once, by Haulage, at a quote the trucks' own books set, and the v... | 9 |
| [HealthCheck.java](HealthCheck.md) | 1,921 | 9 | Sickness: what it moves, and - much more importantly - what it does not. | 0 |
| [HistoricalLicenceCheck.java](HistoricalLicenceCheck.md) | 602 | 22 | R2d's independent discoveries meet R2c's captured patent service. | 11 |
| [HistoricalRecipeCheck.java](HistoricalRecipeCheck.md) | 743 | 31 | Each sourced 1900 undo runs before its entry, knowledge alone does not fit equipment, independent pieces compose, and the default city stays modern. | 7 |
| [HistoryCheck.java](HistoryCheck.md) | 1,243 | 21 | Verifies the graph history: recording, alignment, and the round trip. | 0 |
| [HoldersCheck.java](HoldersCheck.md) | 3,046 | 66 | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is boug... | 0 |
| [HomeRailCheck.java](HomeRailCheck.md) | 961 | 30 | The railway's carriage of the city's own bulk, and the port yard, held to FR6's brief: a bulk tonne bought at home rides a siding at the city leg's... | 2 |
| [HouseholdCheck.java](HouseholdCheck.md) | 1,802 | 13 | Verifies the residents' books and the demolition log. | 0 |
| [HouseholdFlowCheck.java](HouseholdFlowCheck.md) | 718 | 22 | Money follows people: the households' balance carries each month's own flows - the retiring, the ageing on, the dead and the leavers - with the sto... | 3 |
| [HouseholdMemoryCheck.java](HouseholdMemoryCheck.md) | 416 | 11 | The households remember: the builder keeps what still fits. | 4 |
| [HouseholdStageCheck.java](HouseholdStageCheck.md) | 564 | 11 | The two adult stages: the young, 18-34, and the established, 35-69, each with a pyramid compartment, households and books of its own (HS2). | 0 |
| [HousingCheck.java](HousingCheck.md) | 1,133 | 11 | Audits the three subsystems that describe the same housing, every month, and makes them agree. | 0 |
| [HousingDoorCheck.java](HousingDoorCheck.md) | 393 | 6 | The landlords' order at the lender's door (HN1, 2026-11-18): a played city's households held from the homes that fit them while the plan passed tho... | 0 |
| [HousingInventoryCheck.java](HousingInventoryCheck.md) | 288 | 11 | Aggregate housing inventory, caused through recipes, construction and transfers. | 0 |
| [HydroCheck.java](HydroCheck.md) | 628 | 17 | The Hydro Station and the river it needs (HY1, 2026-10-26; docs/queue/HY1-notes.md): the physics of a drop against its own constants, the sites on ... | 0 |
| [HydrogenSteelCheck.java](HydrogenSteelCheck.md) | 510 | 22 | The Hydrogen Steel Works, checked (R6b, FE4: hydrogen-reduced iron, Boden, Sweden, projected June 2028): the near-future row that makes steel from ... | 1 |
| [InboxCheck.java](InboxCheck.md) | 727 | 14 | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. | 0 |
| [IndustryViewCheck.java](IndustryViewCheck.md) | 1,239 | 34 | The materials pictograms' model, checked (0.7.141, batch I11; docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's I11 row): Ind... | 1 |
| [InfrastructureCheck.java](InfrastructureCheck.md) | 1,184 | 8 | The road network, from the curve up to a city that actually jams. | 0 |
| [InterestAbroadCheck.java](InterestAbroadCheck.md) | 1,230 | 41 | The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8, J... | 3 |
| [InterventionCheck.java](InterventionCheck.md) | 607 | 15 | Proves a purchase or sale of dollars moves the D$ a BOUNDED amount (XI1, 2026-11-06): linear up to INTERVENTION_LINEAR of a year's GDP, exactly as ... | 0 |
| [InvestCheck.java](InvestCheck.md) | 2,142 | 20 | Verifies the private investment engine: forecasting, the demand tests, and the brake. | 0 |
| [IslandLinkCheck.java](IslandLinkCheck.md) | 814 | 26 | The island link checked (AL1, 2026-11-09; docs/queue/AL1.md): the rule that says what the city should order to link an island it owns ground on, th... | 0 |
| [IslandNetCheck.java](IslandNetCheck.md) | 834 | 32 | A highway net never stops while the city has ground, and a net is laid on every piece of ground the water parts (CL5). | 3 |
| [IslandRailCheck.java](IslandRailCheck.md) | 1,017 | 47 | The railway has a piece of its own on each piece of the city's ground the water parts, its track dealt by built weight and its yards on the islands... | 1 |
| [JunctionCheck.java](JunctionCheck.md) | 2,549 | 98 | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). | 5 |
| [KeptCheck.java](KeptCheck.md) | 10,169 | 261 | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exact... | 8 |
| [KeptTimingCheck.java](KeptTimingCheck.md) | 682 | 20 | What the kept pages cost: KeptCheck's timed sections, each kept month on a page against the same month built new, within its budget through Timing. | 0 |
| [KnownRedsCheck.java](KnownRedsCheck.md) | 351 | 12 | The harness for AllChecks' known reds, time limit and resource admission. | 0 |
| [LabourCheck.java](LabourCheck.md) | 2,573 | 31 | Verifies the labour market: who can hold a job, and what it costs. | 5 |
| [LandCheck.java](LandCheck.md) | 3,900 | 64 | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. | 9 |
| [LandClassCheck.java](LandClassCheck.md) | 1,349 | 51 | A price for each class of ground, checked (ET3, slice A; docs/studies/spec-earth2-terrain.md 5.1 and 5.2): the going price stays the price of flat ... | 4 |
| [LandNationsCheck.java](LandNationsCheck.md) | 1,842 | 67 | Land bought from the nation it lies in, checked (WN5; docs/studies/spec-nations.md 6.2-6.4, D21, D22): the land table, each nation's land factor an... | 1 |
| [LandlordPortCheck.java](LandlordPortCheck.md) | 822 | 33 | The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds and keeps the sea terminals and Freight's Ports division works them. | 1 |
| [LandmassCheck.java](LandmassCheck.md) | 3,487 | 101 | The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the Earth's data at every plot, ... | 7 |
| [LicenceFeesCheck.java](LicenceFeesCheck.md) | 546 | 15 | Licence fees leave a running firm's money and enter this month's current account. | 4 |
| [LongPlaytest.java](LongPlaytest.md) | 10,166 | 194 | A city played for 2,400 months at the gate and 4,000 once a night on main (tools/nightly.sh), the way a person plays. | 82 |
| [LuxuryRetailCheck.java](LuxuryRetailCheck.md) | 249 | 10 | What a luxury shop's post sells: its payroll over its margin at BEA's mark (LX1, 2026-11-17). | 0 |
| [MainLineCheck.java](MainLineCheck.md) | 2,032 | 67 | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the c... | 2 |
| [ManufacturingCheck.java](ManufacturingCheck.md) | 779 | 11 | The ninth sector: what the city makes out of its own steel, and ships. | 0 |
| [MapCheck.java](MapCheck.md) | 16,807 | 460 | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's... | 14 |
| [MarketCheck.java](MarketCheck.md) | 684 | 22 | Verifies the efficient market (EM1, 2026-10-29): one value for a share, every holder reading it, and a price that follows it - each claim on a fixt... | 1 |
| [MaterialsCheck.java](MaterialsCheck.md) | 2,290 | 45 | The materials chains, checked: the shared machinery every chain stands on (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.5-2.8 and 4), a... | 30 |
| [MedicineCheck.java](MedicineCheck.md) | 619 | 35 | Proves the research catalogue's medical rows (R2f) move the constants they map to: a medical row a 1900 city finds ahead of the world brings its st... | 0 |
| [MiningCheck.java](MiningCheck.md) | 600 | 10 | Ore and scrap, from the bands they clear in to what an arc furnace makes of scrap and a mine of ore. | 12 |
| [ModernEngineeringCheck.java](ModernEngineeringCheck.md) | 594 | 28 | E21's crop-only historical undo, its actual farm production and patent bill, and the saved physical state on either side of the discovery. | 2 |
| [ModernRowsCheck.java](ModernRowsCheck.md) | 868 | 24 | R6f's three research rows and R6e-rest's one, each undone in a city founded the 1900 way until it is known: robot-assisted surgery's share of gener... | 0 |
| [MonetaryCheck.java](MonetaryCheck.md) | 1,127 | 15 | Money: what a basket costs, what the world charges, and what the rate does. | 3 |
| [MoneyDatesCheck.java](MoneyDatesCheck.md) | 268 | 10 | Proves Canada's dated gold in a city founded the 1900 way: both episodes, Money the player's from the first month, and a reserve exhaustion that su... | 2 |
| [MoneySignCheck.java](MoneySignCheck.md) | 397 | 9 | The money's sign is the city's to choose and place, and with the default it writes what it always wrote (FM1). | 0 |
| [MoneyWordsCheck.java](MoneyWordsCheck.md) | 544 | 20 | The words of every page, read off the nodes, in a city whose money is written after the figure (FM1). | 0 |
| [MonthPostsCheck.java](MonthPostsCheck.md) | 23 | 2 | Proves the labour figures held for a pass and across a month survive every writer, with the original walks compared as raw bits (AP5/AP6). | 0 |
| [MortgageCheck.java](MortgageCheck.md) | 1,320 | 30 | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. | 1 |
| [NationsCheck.java](NationsCheck.md) | 680 | 23 | The nations beyond the city and the path they are on (batch WN1; the project's spec-nations.md 7): Nations' year - output, people, prices and the r... | 0 |
| [NewGameCheck.java](NewGameCheck.md) | 1,477 | 18 | Does "Start New Game" actually start a new game? | 4 |
| [NewUnitCheck.java](NewUnitCheck.md) | 1,203 | 35 | A new building is read as it would run, not as the fleet standing runs: NewUnit beside the template, read by every future-building figure. | 0 |
| [ObservationCheck.java](ObservationCheck.md) | 126 | 4 | Holds the path-observation contract with a premise deliberately held off. | 0 |
| [OilCheck.java](OilCheck.md) | 2,421 | 48 | Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K; the project's spec-land.md 2.7 and section 3's K entry). | 2 |
| [OilViewCheck.java](OilViewCheck.md) | 706 | 20 | The oil industry screen (0.7.96, batch O12; docs/studies/spec-oil.md 2.13): OilView's figures, words, chart and levers, held to the model's own rea... | 1 |
| [OrderBookCheck.java](OrderBookCheck.md) | 633 | 33 | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. | 1 |
| [OrderDoorCheck.java](OrderDoorCheck.md) | 579 | 23 | An order at the door (FB15, 2026-11-20): a landlord's order sized, and its owners asked, at the price buildFor() charges; a project's order the len... | 0 |
| [OrderSearchCheck.java](OrderSearchCheck.md) | 923 | 23 | The order searches against the countdowns they replaced (0.7.54; the order's size is a formula since BA1). | 2 |
| [OutsideCheck.java](OutsideCheck.md) | 783 | 5 | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). | 0 |
| [OwnerCheck.java](OwnerCheck.md) | 995 | 28 | The owner families (BO1): one two-adult household per company that has listed and not gone broke, holding the company's founding stake alone. | 1 |
| [PackagingCheck.java](PackagingCheck.md) | 381 | 9 | The built image carries Temurin's runtime with its notices, Microsoft's terms for the C runtime files, the libraries' texts and the complete corres... | 0 |
| [PartnerCallsCheck.java](PartnerCallsCheck.md) | 1,322 | 40 | The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by ... | 0 |
| [PartnersCheck.java](PartnersCheck.md) | 838 | 18 | The model the Trade screen's Partners board, a nation's page and The reserves read (batch WN6, "Nations: the screens"; docs/studies/spec-nations.md... | 0 |
| [PassThroughCheck.java](PassThroughCheck.md) | 911 | 23 | Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1, 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUG... | 0 |
| [PegAnchorCheck.java](PegAnchorCheck.md) | 686 | 22 | The peg's anchor (batch WN4b; ForeignAccounts, THE REGIMES, THE PEG'S ANCHOR): the D$ can be pegged to any of the eight moneys, not only the US$. | 1 |
| [PlanCheck.java](PlanCheck.md) | 5,566 | 188 | The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway... | 5 |
| [PlaytestFixCheck.java](PlaytestFixCheck.md) | 224 | 9 | Verifies the small fixes of a played city (PT1): words and the log, nothing the month does. | 0 |
| [PolicyCheck.java](PolicyCheck.md) | 479 | 5 | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. | 0 |
| [PolicyOpenCheck.java](PolicyOpenCheck.md) | 425 | 15 | Every Policy lever is open from the city's first month, in every city, and nothing opens one later (OP1, 2026-11-19). | 1 |
| [PolicyPreviewCheck.java](PolicyPreviewCheck.md) | 1,406 | 28 | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. | 1 |
| [PopulationCheck.java](PopulationCheck.md) | 1,685 | 13 | The demographics: do they hold together, and do they move the city the way they were told to? | 0 |
| [PortCheck.java](PortCheck.md) | 2,416 | 73 | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). | 6 |
| [PostwarRecipeCheck.java](PostwarRecipeCheck.md) | 934 | 21 | The postwar engineering and business discoveries run their sourced earlier figures, compose in either order, retain saved decisions and leave defau... | 0 |
| [PowerDispatchCheck.java](PowerDispatchCheck.md) | 413 | 24 | The month's merit order (PW1, 2026-11-11; PowerDispatch, UtilitiesHandler's struck book): the load cut in six half-blocks, the works first, the win... | 0 |
| [PowerPlanCheck.java](PowerPlanCheck.md) | 1,720 | 53 | The power planner (PW3): wind and solar by place and season, the reserve test and the spread, checked on caused cities. | 2 |
| [PowerPlantsCheck.java](PowerPlantsCheck.md) | 672 | 23 | The power plants and their fuels (PW2, 2026-11-23; spec-electricity 2.3 and 2.5): the Oil-Fired Plant, the Diesel Station, the Oil-Fired Turbine, t... | 0 |
| [PowerPriceCheck.java](PowerPriceCheck.md) | 747 | 31 | The month's power at the market's price, through the game's own month (PW1, 2026-11-11; Jerus's Q1: "Market price"): the struck book's prices, what... | 6 |
| [PowerViewCheck.java](PowerViewCheck.md) | 372 | 14 | Power observations against native dispatch, including shortage, curtailment, fuel order, phase timing and saves. | 1 |
| [PriceLevelCheck.java](PriceLevelCheck.md) | 1,683 | 64 | A city founded at 1900's prices is today's real economy with every dollar multiplied by one factor, and this is how we know (EP1, Jerus 2026-10-30:... | 13 |
| [PrizeCheck.java](PrizeCheck.md) | 2,024 | 76 | A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts (R4, ... | 1 |
| [PublicLicenceCheck.java](PublicLicenceCheck.md) | 787 | 34 | The city's own patented works pay R2c's patent rent too: a utility whose upgrade came from abroad is billed the patent share of the service it deli... | 3 |
| [QuarryingCheck.java](QuarryingCheck.md) | 639 | 13 | Quarrying, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.10, 4 and 5's I7 row; the study's - materials-chains-researc... | 1 |
| [QuayCraneCheck.java](QuayCraneCheck.md) | 409 | 17 | The quay crane (HB2, 2026-11-09; QuayCrane, BoatSchedule.atQuay()): a boat at its terminal's quay is worked by a light line from over the ship to t... | 0 |
| [QuayRailCheck.java](QuayRailCheck.md) | 657 | 39 | The quay lines of the harbour, the spur the railway lays to each, and the railed terminals QuayRail reads from them (batch HB2, Jerus 2026-11-09: "... | 0 |
| [RailCheck.java](RailCheck.md) | 816 | 9 | The railway: what it charges, who pays it, and what it does to the band. | 2 |
| [RailDepotCheck.java](RailDepotCheck.md) | 734 | 16 | The railway's stations: the Rail Depot, and the rules that put a station first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.m... | 1 |
| [RailDrawCheck.java](RailDrawCheck.md) | 1,983 | 82 | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). | 0 |
| [RankingsCheck.java](RankingsCheck.md) | 1,804 | 51 | The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9): Rankings' pivot and display, its two boa... | 0 |
| [ReadPathCheck.java](ReadPathCheck.md) | 2,563 | 19 | Reading the city must not change the city. | 4 |
| [RealDollarCheck.java](RealDollarCheck.md) | 707 | 25 | The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$... | 1 |
| [RecipeCheck.java](RecipeCheck.md) | 1,197 | 43 | Recipes (R1c): the ways a building runs, as data on its template, chosen at the city's prices in a city founded the 1900 way, set on the template's... | 9 |
| [RefineryCheck.java](RefineryCheck.md) | 1,552 | 36 | The refinery's units and the flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread... | 1 |
| [RefineryViewCheck.java](RefineryViewCheck.md) | 632 | 23 | The refinery's pictogram (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): RefineryView's figures and its picture, held to the model's own reads ... | 2 |
| [ReloadCheck.java](ReloadCheck.md) | 1,074 | 51 | Plays a city, saves it, loads the save into a second game, runs both a month and compares what each would save: a reloaded city has to play its nex... | 2 |
| [ReplayCheck.java](ReplayCheck.md) | 1,842 | 38 | Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs; and Follow (batch TR2... | 1 |
| [ResearchCheck.java](ResearchCheck.md) | 1,094 | 26 | The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses what... | 10 |
| [ResearchPreviewCheck.java](ResearchPreviewCheck.md) | 731 | 34 | The preview each dial card on Policy > Research shows: what a set of dials would find here over ten years (R3b, slice A: ResearchPreview, and the w... | 1 |
| [ResearchViewCheck.java](ResearchViewCheck.md) | 1,757 | 48 | What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()). | 5 |
| [RestaurantsCheck.java](RestaurantsCheck.md) | 575 | 6 | A meal out is food, and it is the same food. | 0 |
| [RestructureCheck.java](RestructureCheck.md) | 418 | 5 | Buying the city's own debt back, at what the paper is actually worth. | 2 |
| [RoadCheck.java](RoadCheck.md) | 1,209 | 35 | The roads over their lives, and a gravel road paved (0.7.70, batch N1): BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to the... | 1 |
| [RobustnessCheck.java](RobustnessCheck.md) | 432 | 5 | What the game does when something is already broken. | 0 |
| [RunWorksCheck.java](RunWorksCheck.md) | 1,310 | 40 | Highways and rail on the slopes (ET4, slices A and B; docs/studies/spec-earth2-terrain.md 4.3, decision D10): a highway's or a railway line's groun... | 2 |
| [SaveFileCheck.java](SaveFileCheck.md) | 5,163 | 42 | Verifies where saves go and how they are written. | 13 |
| [SaveSlotCheck.java](SaveSlotCheck.md) | 2,001 | 53 | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. | 1 |
| [ScaleCheck.java](ScaleCheck.md) | 2,313 | 68 | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them a... | 2 |
| [SchoolingLadderCheck.java](SchoolingLadderCheck.md) | 229 | 7 | Hand-summed monthly cash ledgers for every after-cost qualification (WG3). | 2 |
| [SchoolingPageCheck.java](SchoolingPageCheck.md) | 48 | 1 | Compares Wages' actual qualification bars with the pure model (WG3/WS2). | 0 |
| [SectorFlowCheck.java](SectorFlowCheck.md) | 308 | 11 | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held t... | 0 |
| [SectorStatementCheck.java](SectorStatementCheck.md) | 1,367 | 30 | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every mon... | 3 |
| [ShortSellingCheck.java](ShortSellingCheck.md) | 1,502 | 53 | Verifies a share's price as its quote, and the short selling that brings a bubble down (EM2, 2026-10-30) - each claim on a fixture that causes it, ... | 4 |
| [SicknessCheck.java](SicknessCheck.md) | 426 | 7 | The long sick: who stays sick, and who it kills. | 0 |
| [SkipReportCheck.java](SkipReportCheck.md) | 323 | 5 | Verifies the fast-forward summary. | 0 |
| [SlopeCheck.java](SlopeCheck.md) | 990 | 34 | Buildable by slope, checked (ET2, slice D; docs/studies/spec-earth2-terrain.md 3.2, 3.4, 4.1, 4.2, 4.4): a building stands on flat and foothill gro... | 1 |
| [StaffingDrawCheck.java](StaffingDrawCheck.md) | 1,124 | 52 | The harness for GW1's staffing gate that counts the draw: a business orders a building only when the city could staff Sector.MIN_STAFFABLE_TO_ORDER... | 1 |
| [StaffingTieCheck.java](StaffingTieCheck.md) | 522 | 29 | The harness for TQ1's tie rule at the staffing gate: a planner whose buildings the gate refused hands WantedPosts one hold (LD1), and where two of ... | 0 |
| [StaleCheck.java](StaleCheck.md) | 331 | 6 | The prose still describes the code: the firm half of `tools.Stale`, asserted, and `tools.Names` proved on a fixture. | 0 |
| [StepsCheck.java](StepsCheck.md) | 212 | 7 | Magnitude controls follow the figure and bond lots follow the month's money. | 0 |
| [SubstationCheck.java](SubstationCheck.md) | 375 | 24 | Substations at both sides of their carry, the order that keeps the peak covered, and their costs in the bills (PW5). | 16 |
| [SulphideLeachCheck.java](SulphideLeachCheck.md) | 404 | 18 | The Sulphide Leach & SX-EW Plant, checked (R6b, FE2: sulphide heap leaching, Johnson Camp, Arizona, Nov 2025): the near-future row that lets a sulp... | 0 |
| [SupplierCreditCheck.java](SupplierCreditCheck.md) | 402 | 10 | The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit when the till cannot pay for it, owed for a month, repaid out of the s... | 0 |
| [TimingCheck.java](TimingCheck.md) | 787 | 35 | Checks hard and soft timing, JVM forwarding, suite coverage and paired medians. | 0 |
| [ToastCheck.java](ToastCheck.md) | 388 | 16 | A toast stays out of the page's way (TS1): while a toast shows, the page's foot takes a bottom padding as tall as the toasts, the scroll's pixel of... | 0 |
| [TradeBandCheck.java](TradeBandCheck.md) | 534 | 19 | TradeBand, the model behind the Trade tab's band in legs, held to the study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructur... | 0 |
| [TradeCostCheck.java](TradeCostCheck.md) | 634 | 6 | The wedge between what the world charges and what it pays, and what it is made of. | 0 |
| [TreasuryCheck.java](TreasuryCheck.md) | 1,874 | 45 | Plays a city and audits what the screens say the treasury did. | 2 |
| [TruckRouteCheck.java](TruckRouteCheck.md) | 1,997 | 65 | Proves the trucks' routes (0.8.8, batch FR5, slice B; docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the map lets a truck driv... | 0 |
| [UndemandedFeedCheck.java](UndemandedFeedCheck.md) | 383 | 15 | No feed is bought for undemanded output that cannot cover it (CPB2). | 0 |
| [VanCheck.java](VanCheck.md) | 537 | 6 | The vans: what a sector needs, what it costs it, and what happens while it waits for them. | 0 |
| [VaultMoneysCheck.java](VaultMoneysCheck.md) | 1,080 | 26 | The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's COFER names, t... | 0 |
| [Village1900Check.java](Village1900Check.md) | 584 | 19 | Why the 1900 city stalls (R1g's finding), and what R1g's two switches change: the playtest's village founded the 1900 way, played the playtest's wa... | 0 |
| [WageLadderCheck.java](WageLadderCheck.md) | 458 | 14 | The wage ladder a city is founded on (R0d): today's PayTier ladder in every city but one founded the 1900 way, which pays 1901's, the census's. | 0 |
| [WantedPostsCheck.java](WantedPostsCheck.md) | 1,057 | 31 | The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). | 0 |
| [WaterCheck.java](WaterCheck.md) | 530 | 10 | Sanity harness for water production, demand, throttling and billing - and, since 0.7.59, the fresh water limit and the desalination plant (sections... | 0 |
| [WaterPowerCheck.java](WaterPowerCheck.md) | 435 | 18 | The water priced from its cost, as PW1 prices power (PW1-W, 2026-11-23; Jerus's Q1 for power: "Market price"), with the licence's power read from t... | 0 |
| [WellCheck.java](WellCheck.md) | 1,647 | 31 | The oil wells' lives (0.7.84, batch O7; docs/studies/spec-oil.md 2.6 and 4's WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spe... | 3 |
| [WoodProductsCheck.java](WoodProductsCheck.md) | 651 | 16 | Wood Products, checked (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.2, 2.3, 2.7, 2.8, 2.12, 4 and 5's I10 row; the study's - material... | 0 |
| [WorksFinanceCheck.java](WorksFinanceCheck.md) | 1,104 | 28 | Essential funding reservations, actual parcel cash and quoted bond terms (HW1). | 2 |
| [WorldCheck.java](WorldCheck.md) | 782 | 8 | The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its ... | 1 |
| [WorldDataCheck.java](WorldDataCheck.md) | 1,384 | 45 | The nations' world file's reader (batch WN0; the project's spec-nations.md 3.3): WorldData's reading of world-1.bin held to tools/world/worldread.p... | 12 |
| [WorldMarketCheck.java](WorldMarketCheck.md) | 1,685 | 52 | The world market (batch WN3; the project's spec-nations.md 4): the nations' January solve held to the equations it solves, the calibration held to ... | 0 |
| [WorldMeasuresCheck.java](WorldMeasuresCheck.md) | 663 | 25 | The Rankings' real-world file held to what it promises (batch LB1; the project's spec-leaderboards.md 6-8): WorldMeasures' reading of world-2025.js... | 1 |
| [WorldMoneysCheck.java](WorldMoneysCheck.md) | 1,282 | 49 | The world's moneys (batch WN4; spec-exchange-rates 3.7, spec-nations 5.1): WorldMoneys' monthly rate of each money to the US dollar - its parity pa... | 1 |
| [YearBookCheck.java](YearBookCheck.md) | 1,240 | 46 | Proves the year book folds each series the way that series has to be folded, and that the file says so. | 0 |
| [YoungBankCheck.java](YoungBankCheck.md) | 423 | 14 | A young town's bank against the lone plant it lends for, and the first plant that plant is weighed as: the harness of DQ1, "young towns borrow ahea... | 0 |
| [ZoningCheck.java](ZoningCheck.md) | 615 | 17 | Zoning and the forest law, the model (0.7.107, batch Z1): Zoning, and LandManager's ZONES AND THE FOREST LAW, held to the ledger, the treasury, the... | 0 |
| [ZoningViewCheck.java](ZoningViewCheck.md) | 744 | 26 | Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the b... | 0 |

## tools (21 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [ChipRender.java](ChipRender.md) | 101 | 3 | Renders the island chip for review (MT1, BK1-L): the chip as it was (one line, a 3 px dark stroke) and as it is (two lines on a dark rounded plate,... | 0 |
| [CodeMap.java](CodeMap.md) | 177 | 3 | The code map: docs/map/README.md, one row per file, and docs/map/NAME.md for every file, listing its banner sections and every member with the line... | 1 |
| [Dials.java](Dials.md) | 55 | 2 | Every dial in the game on one page: docs/dials.md lists each static final constant in the tree with its value, its line, and the sentence above it. | 3 |
| [HarbourRenders.java](HarbourRenders.md) | 983 | 53 | Renders a Bergen city's harbour for review, headless: HB1's placement before and after, and three ways to draw a terminal's box under the quay wall... | 0 |
| [HarnessMap.java](HarnessMap.md) | 135 | 3 | What every harness asserts, in its own words: docs/harnesses.md. | 1 |
| [IslandRailRenders.java](IslandRailRenders.md) | 123 | 7 | Renders what RI1 did for an island the city's port links, for review: the island's own railway, a rail hub nearest the terminal with its track and ... | 0 |
| [JavaScan.java](JavaScan.md) | 666 | 39 | A structural read of one Java source file, without a compiler. | 8 |
| [LandClassRenders.java](LandClassRenders.md) | 175 | 6 | Renders what ET3 says about the classes of ground, for review: the land office's prices and offers by class and note 11 of the sector statements, i... | 0 |
| [LandTable.java](LandTable.md) | 362 | 13 | Makes src/main/resources/world/land-2024.json, the table LandFactors reads: every nation's land a hectare, in US dollars of 2024, from the publishe... | 1 |
| [ManualToMarkdown.java](ManualToMarkdown.md) | 1,522 | 82 | The published manual as two files the repository keeps: docs/manual.md, which GitHub renders when it is clicked, and docs/manual.html, the page its... | 0 |
| [Maps.java](Maps.md) | 30 | 1 | Regenerates every generated document in one go: the code map, the dials, the month order and the harness map. | 0 |
| [MonthOrder.java](MonthOrder.md) | 167 | 6 | The month as a numbered list: docs/month-order.md walks the top-level statements of the methods that make up a month, in order, each with its line ... | 1 |
| [Names.java](Names.md) | 436 | 26 | Lists the names a branch's new prose uses that the tree does not declare. | 1 |
| [Packaging.java](Packaging.md) | 454 | 21 | Build EXE.bat's source archives and its check of the built image (CR1-J): the packaging JDK checked against the pins, the complete corresponding so... | 2 |
| [ReliefRenders.java](ReliefRenders.md) | 152 | 6 | Renders the map's relief for review: six PNG files drawn by the game's own painters, headless. | 0 |
| [RunRenders.java](RunRenders.md) | 206 | 7 | Renders what ET4 slice C did to the map, for review: where a city's highways and railway run across steep ground, a viaduct, and across very steep ... | 0 |
| [SaveDump.java](SaveDump.md) | 133 | 4 | Looks inside a save without loading the game - or reading the file. | 0 |
| [SlopeRenders.java](SlopeRenders.md) | 275 | 10 | Renders what ET2 did to the map and the founding card, for review: a city played on the real Earth and drawn by the game's own painters, headless, ... | 0 |
| [SourceTree.java](SourceTree.md) | 154 | 13 | The whole source tree, scanned once, and the little that every generator shares: where the sources are, where the documents go, what area a file be... | 9 |
| [Stale.java](Stale.md) | 633 | 27 | Finds the comments and documents that have stopped being true, mechanically. | 2 |
| [Where.java](Where.md) | 71 | 1 | Finds a member by name anywhere in the tree and, if asked, prints it. | 1 |

