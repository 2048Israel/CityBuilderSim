# The code map

Generated 2026-11-09 by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).

**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.

**The tree:** 530 files, 589,961 lines, 25,629 methods, 5,606 constants. `GameVersion.VERSION` is "0.9.61", `SAVE_FORMAT` 51.

## model (247 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgeBand.java](AgeBand.md) | 181 | 12 | The six ages of a resident. | 44 |
| [ArrivalMix.java](ArrivalMix.md) | 169 | 8 | What the world's movers hold by year: the skill mix a month's arrivals are drawn from, in a city founded the 1900 way. | 5 |
| [Auction.java](Auction.md) | 460 | 28 | The city's paper sold at auction: the bidders, each by its own rule in the game, and the one yield that clears them. | 28 |
| [AuctionTally.java](AuctionTally.md) | 145 | 11 | The city's auctions counted: how many sales the treasury has booked, what they sold and what they asked, how often the stop sat more than a point o... | 4 |
| [AutoBuilder.java](AutoBuilder.md) | 2,179 | 145 | Automatic building (0.7.73): the city's own works ordered for the player once a month - power, water, the roads and their lines, care, schools and ... | 15 |
| [BalanceSheet.java](BalanceSheet.md) | 201 | 30 | A simple balance sheet for one business in the city. | 6 |
| [Bank.java](Bank.md) | 6,711 | 455 | The city's commercial bank: every loan in it, and every default. | 65 |
| [BoatSchedule.java](BoatSchedule.md) | 1,060 | 61 | The month's ships, as a pure function of time: which calls the city's sea trade makes, when each arrives, and where each boat is at any moment (0.7... | 19 |
| [BondMarket.java](BondMarket.md) | 2,091 | 172 | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participa... | 21 |
| [BookDesk.java](BookDesk.md) | 184 | 20 | The debt book as a desk: every figure the book page and a line's page draw (CD6, spec-city-debt.md section 7, boards 1 and 2), a pure model of what... | 5 |
| [BridgeGround.java](BridgeGround.md) | 72 | 7 | The ground a bridge is surveyed over, read from the World and the city's land: the Earth's terrain, relief and landmass a tile at a time, the city'... | 1 |
| [BridgeSurvey.java](BridgeSurvey.md) | 438 | 25 | The survey of a long bridge's crossing: the shortest chain of plots from the city's own dry ground on a far landmass to its own buildable ground on... | 8 |
| [BridgeView.java](BridgeView.md) | 98 | 11 | What the build card says of a long bridge's crossing: the refusal of a survey in its own words, and the pieces of the line a surveyed crossing gets. | 5 |
| [BridgeWorks.java](BridgeWorks.md) | 126 | 7 | What a long bridge's span costs, in US dollars a metre by kind and by the depth of the water it crosses, and the construction points those dollars ... | 11 |
| [Bridges.java](Bridges.md) | 315 | 30 | The city's long bridges, built and building: for each, the template it was ordered as, its kind, the two landings and the landmass at each end, its... | 7 |
| [BuildAdvice.java](BuildAdvice.md) | 2,219 | 110 | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure befor... | 31 |
| [BuildCard.java](BuildCard.md) | 1,901 | 91 | One build card's figures, for every one of the buildings in buildings.json (0.7.25; 143 of them since FR3, the Bulk Berth): what it gives the city ... | 31 |
| [BuildLog.java](BuildLog.md) | 162 | 10 | What the city has gained, and when. | 5 |
| [BuildersShort.java](BuildersShort.md) | 145 | 10 | The builders short (FC1): what an order pays a point of work as the builders run out of points for the sites - a premium that rises with the months... | 6 |
| [BuildingCatalog.java](BuildingCatalog.md) | 840 | 28 | Reads the building definitions out of buildings.json. | 22 |
| [BuildingInstance.java](BuildingInstance.md) | 40 | 4 |  | 1 |
| [BuildingManager.java](BuildingManager.md) | 8,458 | 235 | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed the... | 123 |
| [BuildingType.java](BuildingType.md) | 255 | 0 |  | 86 |
| [BuildingVisual.java](BuildingVisual.md) | 553 | 24 | How the painted map draws each building type: its class and colour, its footprint (the model's own land for it), whether it is a road and of which ... | 31 |
| [BuildingsStacks.java](BuildingsStacks.md) | 531 | 48 | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. | 43 |
| [BuildingsTemplate.java](BuildingsTemplate.md) | 1,518 | 155 | One kind of building, and what it costs to put up. | 182 |
| [BusinessDebt.java](BusinessDebt.md) | 93 | 12 | Base class for private-sector borrowing. | 9 |
| [BusinessDebtManager.java](BusinessDebtManager.md) | 3,626 | 235 | Private-sector credit. | 33 |
| [BusinessInvestment.java](BusinessInvestment.md) | 1,513 | 80 | Capacity planning for the private sector. | 80 |
| [BusinessLoan.java](BusinessLoan.md) | 72 | 7 | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. | 7 |
| [Buyback.java](Buyback.md) | 357 | 22 | The city's paper bought back in a reverse auction: every holder offers what it holds in slices, each at the price where its own buying rule would s... | 9 |
| [CallPartners.java](CallPartners.md) | 233 | 16 | Who each ship and train of the month calls on: a nation drawn by the month's tonnes of the call's kind of cargo, and the compass bearing of its nod... | 4 |
| [CallTree.java](CallTree.md) | 330 | 18 | What a callable bond is worth, and what the right to call it costs: a Ho-Lee short-rate tree on monthly steps, fitted to the city's curve (CD4, doc... | 4 |
| [CameraPath.java](CameraPath.md) | 231 | 20 | The replay camera's arithmetic without the toolkit (TR1, 2026-10-31): the glide between two marked views, the fly out to the whole Earth and back, ... | 6 |
| [CapSetting.java](CapSetting.md) | 78 | 8 | A cap on the yield an automatic sale takes, saved and said (D28). | 5 |
| [CapitalFlows.java](CapitalFlows.md) | 871 | 55 | Hot money: what comes in chasing a spread, and what happens when it leaves. | 21 |
| [CareType.java](CareType.md) | 170 | 7 | What a healthcare building actually does. | 30 |
| [CausesOfDeath.java](CausesOfDeath.md) | 606 | 30 | The causes of death of 1900, and how history and the city take them away. | 8 |
| [CentralBank.java](CentralBank.md) | 955 | 95 | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. | 24 |
| [ChartModel.java](ChartModel.md) | 735 | 50 | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks o... | 22 |
| [CityBasket.java](CityBasket.md) | 122 | 2 | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. | 1 |
| [CityCalendar.java](CityCalendar.md) | 150 | 13 | Turns the month counter into a date a person can hold in their head. | 59 |
| [CityLand.java](CityLand.md) | 1,312 | 95 | The city's land on the world: whole blocks of a grid lined up with the world - the centre it was founded or converted with, and every purchase sinc... | 58 |
| [CityMap.java](CityMap.md) | 10,705 | 536 | The city map's data: the city's buildings counted by type in each 7.68 km district of its land, placed month by month so nothing placed ever moves,... | 31 |
| [CityMeasures.java](CityMeasures.md) | 468 | 23 | The city's figure for each of the Rankings' 70 measures (spec-leaderboards 6, "Where the game's side comes from"), worked out of its HistorySave an... | 4 |
| [CityNeeds.java](CityNeeds.md) | 1,112 | 48 | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, t... | 29 |
| [CityRuns.java](CityRuns.md) | 4,689 | 238 | The city's highways and railway as runs on corridors: each laid month by month from its hub's lines out, straight by preference, never moved, with ... | 19 |
| [CityShore.java](CityShore.md) | 636 | 41 | The city's works on its shore as the map draws them: each sea terminal's box on owned dry ground at the water with its quay run straight out over o... | 12 |
| [ClockPace.java](ClockPace.md) | 311 | 19 | The clock's pace (UP2): how much of each frame the months may take, how often the screen is redrawn and what the redraw cost, and what speed the ci... | 4 |
| [ConstructionControl.java](ConstructionControl.md) | 911 | 66 | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it... | 24 |
| [Consumption.java](Consumption.md) | 763 | 35 | What a household eats, and what changes it. | 8 |
| [CorporateBond.java](CorporateBond.md) | 248 | 29 | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. | 15 |
| [Crime.java](Crime.md) | 525 | 61 | Crime, the police who deter and catch it, and the prisons that hold who they catch. | 18 |
| [Currency.java](Currency.md) | 263 | 21 | What the city's money is called, and how it is written. | 27 |
| [DataSave.java](DataSave.md) | 2,887 | 441 | sections: land and ore, the shedding warning... | 12 |
| [Debt.java](Debt.md) | 1,438 | 116 | One piece of city paper. | 44 |
| [DebtManager.java](DebtManager.md) | 3,464 | 277 | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of m... | 51 |
| [DebtQuote.java](DebtQuote.md) | 308 | 12 | What a loan would cost, worked out BEFORE the player agrees to it. | 24 |
| [DecisionLog.java](DecisionLog.md) | 213 | 24 | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). | 40 |
| [DemolitionLog.java](DemolitionLog.md) | 142 | 10 | What the city has lost, and when. | 7 |
| [Denomination.java](Denomination.md) | 199 | 12 | The currency's unit, and the power to lop zeros off it. | 5 |
| [Deposit.java](Deposit.md) | 263 | 13 | One field of a resource in the world's ground: which resource, the world cell it is listed in and its place in that cell's list, its centre as a pl... | 54 |
| [DistrictPicture.java](DistrictPicture.md) | 309 | 17 | A district's picture for the far views: 32 x 32 pixels, a pixel for 8 x 8 plots (240 m), each the colour of one of its 64 plots - the plot a fixed ... | 3 |
| [DistrictPlan.java](DistrictPlan.md) | 7,060 | 257 | One district's street plan: from its ground, the buildings and road plots the city map gives it and the city's highway and railway plots through it... | 20 |
| [EarthData.java](EarthData.md) | 1,118 | 99 | The reader of earth-2.bin (and of earth-1.bin, whose layers it holds), the real Earth's ground as data: its layers' 1-degree tiles inflated into an... | 40 |
| [EarthProj.java](EarthProj.md) | 446 | 34 | The real Earth flattened round a city's spot: Lambert's azimuthal equal-area projection on a sphere, forward and inverse, the world's plots in metr... | 25 |
| [EarthSite.java](EarthSite.md) | 724 | 44 | A spot on the real Earth as the founding screen weighs it: whether the game has the Earth's data, the place names a player searches, the spot the s... | 22 |
| [EarthTerrain.java](EarthTerrain.md) | 3,287 | 171 | The real Earth's ground on the world's plots: a tile, a plot, a line or a region at a time, each world cell's land and forest (the cells pass), the... | 24 |
| [EconomyManager.java](EconomyManager.md) | 2,701 | 269 | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. | 65 |
| [Education.java](Education.md) | 1,098 | 60 | Who the city teaches, what it costs, and why anybody bothers. | 13 |
| [EducationType.java](EducationType.md) | 191 | 11 | What a school actually teaches. | 35 |
| [Equipment.java](Equipment.md) | 152 | 16 | A piece of equipment a building template carries: what it is called, when it is known, which 1900 form it ends (if any), what fitting it buys and h... | 15 |
| [EquipmentFit.java](EquipmentFit.md) | 287 | 16 | What a piece of equipment would earn a building and what it would cost, and whether that pays: research-design 2.2's rule, steps 1 to 4, and each p... | 10 |
| [EquipmentForms.java](EquipmentForms.md) | 124 | 5 | Sets R0c's 1900 forms from the buildings still without the equipment that ends them: the steam share of each sector, the farms' horse share and the... | 1 |
| [Equity.java](Equity.md) | 1,291 | 108 | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. | 52 |
| [Exchange.java](Exchange.md) | 2,531 | 196 | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the quote: the middle of the... | 25 |
| [Expectations.java](Expectations.md) | 287 | 16 | What the city expects prices to do, and how far it believes the central bank. | 11 |
| [FamilyModel.java](FamilyModel.md) | 2,211 | 102 | How the city's people are arranged into households, and what each earns. | 23 |
| [FamilyStructure.java](FamilyStructure.md) | 119 | 7 | The shapes a household comes in. | 33 |
| [Fittings.java](Fittings.md) | 124 | 13 | The fittings on site: pieces of equipment ordered for a standing building and not yet worked, first ordered first worked. | 4 |
| [FloatingRateNote.java](FloatingRateNote.md) | 218 | 20 | A floating-rate note: a coupon every month on the whole face, the month's policy rate plus a margin fixed at the sale, and the face at the end - at... | 6 |
| [ForeignAccounts.java](ForeignAccounts.md) | 3,484 | 225 | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. | 55 |
| [Formats.java](Formats.md) | 122 | 9 | The few formats a sector needs to describe itself, without the toolkit. | 49 |
| [Founding.java](Founding.md) | 646 | 58 | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. | 123 |
| [FoundingName.java](FoundingName.md) | 73 | 6 | The city's name as the Found-a-city page holds it: blank to begin with, filled by a place searched, and the player's own once they have typed one (... | 2 |
| [FreightView.java](FreightView.md) | 470 | 23 | The Freight sector's divisions as the screens draw them (batch FR4; docs/studies/spec-freight-and-infrastructure.md 6): the city's freight this mon... | 6 |
| [FuelSplit.java](FuelSplit.md) | 229 | 8 | FUEL, split into the PETROL and DIESEL it became: a save written before 0.7.76 converted once, on its load, before anything is restored (batch O1; ... | 2 |
| [FundLedger.java](FundLedger.md) | 789 | 94 | The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in incom... | 11 |
| [FundView.java](FundView.md) | 875 | 39 | The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, it... | 6 |
| [Game.java](Game.md) | 22,143 | 980 | sections: THE CITY FOUNDED THE 1900 WAY, AND WHAT A NEW BUILDING COMES WITH (R1b, 2026-11-05; docs/studies/research-design.md 2.2 step 6), THE FITT... | 263 |
| [GameFiles.java](GameFiles.md) | 559 | 46 | Where the game keeps its files, and how it writes them. | 172 |
| [GameLog.java](GameLog.md) | 348 | 20 | Everything the game prints, written somewhere a player can find it. | 12 |
| [GamePrefs.java](GamePrefs.md) | 249 | 27 | How the player likes the window, kept between runs. | 5 |
| [GameVersion.java](GameVersion.md) | 8,376 | 6 | What build this is, and what shape its saves are. | 22 |
| [Gateways.java](Gateways.md) | 207 | 11 | The city's two gateways - the highway's and the railway's - as one point on the city's edge: the bearing, every SeaRoutes.BEARING_STEP degrees, wit... | 2 |
| [Geology.java](Geology.md) | 1,298 | 49 | The Earth's geology as its deposits read it: each USGS province's class - ocean, shield, fold belt, rift, shelf, basin or other - by the data's nam... | 9 |
| [GlobeFrame.java](GlobeFrame.md) | 1,078 | 91 | The Earth's map as a globe, without the toolkit: where the view looks on the planet, how close and which way is up, how a notch of the wheel, a dra... | 12 |
| [GlobePicture.java](GlobePicture.md) | 411 | 33 | The planet's picture: a code a texel on a latitude-longitude grid at ARCMIN - a World class and, since ET1, its relief - made from the Earth's data... | 8 |
| [Good.java](Good.md) | 1,435 | 26 | A thing that can be made, bought, held, imported and exported. | 155 |
| [GoodsMarket.java](GoodsMarket.md) | 641 | 65 | Where one good clears between whoever makes it and whoever wants it. | 63 |
| [GridConversion.java](GridConversion.md) | 545 | 43 | A saved city's land put on the block grid: a format-31 save's lanes snapped to whole blocks with its fields deciding, or an older save's one figure... | 6 |
| [GridOffers.java](GridOffers.md) | 356 | 28 | The city's offers on the block grid: on each of its four sides six places, left to right facing out, and in each place one standing offer - a recta... | 10 |
| [Health.java](Health.md) | 428 | 23 | How much of the workforce is off sick this month. | 22 |
| [Healthcare.java](Healthcare.md) | 968 | 55 | The city's healthcare service: what it costs, what it collects, and what it does with the dead. | 26 |
| [HistoryGrapher.java](HistoryGrapher.md) | 115 | 1 |  | 1 |
| [HistorySave.java](HistorySave.md) | 2,066 | 71 | Every month the city has ever lived, one number at a time. | 49 |
| [HomeFuel.java](HomeFuel.md) | 171 | 20 | The homes' lamp oil and coal: what the households that light with a lamp and heat with a stove ask of the shops each month, as two shares of the ho... | 3 |
| [Household.java](Household.md) | 1,433 | 122 | Every household of one shape at one pay tier, as one ledger. | 42 |
| [HouseholdAccounts.java](HouseholdAccounts.md) | 1,420 | 107 | The city's residents, treated as one household. | 18 |
| [HouseholdBalance.java](HouseholdBalance.md) | 4,722 | 247 | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. | 57 |
| [HydroSites.java](HydroSites.md) | 440 | 31 | The city's hydro sites: where a river and a drop lie on the ground it owns - the places a Hydro Station can be ordered (HY1, 2026-10-26; docs/queue... | 7 |
| [Inbox.java](Inbox.md) | 880 | 39 | Everything the city has had to say for itself, newest first. | 9 |
| [IndustryPicture.java](IndustryPicture.md) | 1,617 | 85 | Each materials industry's month drawn as a picture, laid out to scale (0.7.143, batch I12; docs/studies/spec-materials.md 2.13, the study's mockups... | 2 |
| [IndustryView.java](IndustryView.md) | 883 | 64 | Each materials industry's month as a picture (0.7.141, batch I11; docs/studies/spec-materials.md 2.13): where what it worked came from - its fields... | 5 |
| [InflationLinkedBond.java](InflationLinkedBond.md) | 311 | 28 | An inflation-linked bond: a REAL coupon every month on a principal that follows the city's price index, and at the end the indexed principal, never... | 6 |
| [InfrastructureManager.java](InfrastructureManager.md) | 1,318 | 101 | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. | 29 |
| [InputLog.java](InputLog.md) | 438 | 35 | What the player did in a sitting, as a file (TR1, 2026-10-31): the mouse, the keys and the markers beside them - the window's size, the screen open... | 2 |
| [InterchangeGround.java](InterchangeGround.md) | 169 | 9 | The ground a highway interchange keeps: a 19 x 19 plot reserve centred on each highway x highway crossing (a cloverleaf), an 11 x 11 one beside eac... | 5 |
| [InterimLoan.java](InterimLoan.md) | 35 | 1 | Interim financing: the bank's loan to a sector in the month it defaulted, for what the month's bills left unpaid after its debt was written down, r... | 4 |
| [Investor.java](Investor.md) | 44 | 6 | Whoever is paying for a building. | 3 |
| [JobType.java](JobType.md) | 20 | 0 |  | 90 |
| [LabourMarket.java](LabourMarket.md) | 915 | 53 | What labour costs, and why it costs that. | 22 |
| [LandClassView.java](LandClassView.md) | 187 | 16 | What the land office and the sector statements say about the CLASSES of ground (ET3, spec-earth2-terrain 5.3), in words, worked out without a node:... | 4 |
| [LandConversion.java](LandConversion.md) | 219 | 7 | A saved city's land put on the block grid, and a city's land drawn again to hold a new figure: a format-31 save's lanes snapped to whole blocks, an... | 7 |
| [LandFactors.java](LandFactors.md) | 232 | 18 | Each nation's land factor: what a hectare of its land costs against a hectare of Canada's (1, D21), at the founding and as its output a head moves ... | 7 |
| [LandGrid.java](LandGrid.md) | 392 | 30 | The ground a city owns, as a region quadtree over the world's plots: which holding owns each plot, and how much of any block or rectangle of plots ... | 20 |
| [LandManager.java](LandManager.md) | 3,115 | 276 | The city's land: what it owns, what is built on, and what it sells. | 91 |
| [LandMap.java](LandMap.md) | 2,472 | 146 | The city's land as the map view sees it: the outlines it draws - the centre's blocks, the city's edge as runs along block lines, an offer's rectang... | 20 |
| [LandMarket.java](LandMarket.md) | 1,608 | 98 | The land office's window: twenty-four offers standing, six on each side of the city - in each place a rectangle of whole blocks against the city's ... | 33 |
| [LandParcel.java](LandParcel.md) | 493 | 62 | One offer on the market, as the land office lists it: a rectangle of whole blocks against one side of the city, in one of that side's six places, w... | 42 |
| [LandSellers.java](LandSellers.md) | 511 | 35 | The nation that sells each plot of a real-Earth world's ground, and an offer's unowned ground split by its sellers. | 6 |
| [LandSellersView.java](LandSellersView.md) | 117 | 5 | What the land office says of who sells an offer's ground: a pure function of the offer's sellers (LandParcel.getSellers()) and the nations' names (... | 3 |
| [LandmassView.java](LandmassView.md) | 528 | 38 | What the screens and the investors' words say of the city's landmasses: pure functions of the model's figures, so that the words are written in one... | 12 |
| [Landmasses.java](Landmasses.md) | 1,238 | 99 | The city's ground counted by landmass, and which landmasses the city's buildings may stand on: its books by landmass (each landmass's owned dry gro... | 26 |
| [LegacyLand.java](LegacyLand.md) | 514 | 44 | The city's land as spec-land drew it, read-only: a square centre round the founding site and forty lanes of wedges pushed out band by band - the ge... | 8 |
| [LongTermBond.java](LongTermBond.md) | 208 | 21 | A bond: a fixed coupon every month on the whole face, and the face at the end - at 2, 5, 10, 20, 30 or 50 years, sold in lines that reopen (CD2). | 22 |
| [LuxuryCounter.java](LuxuryCounter.md) | 160 | 10 | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. | 1 |
| [MapFrame.java](MapFrame.md) | 374 | 47 | The map view's arithmetic without the toolkit: where the view looks and how close, how a drag, a notch of the wheel and a held key (0.7.135) move i... | 24 |
| [MapTiles.java](MapTiles.md) | 1,368 | 74 | The map view's tiles without the toolkit: each tile's ground kept so it is read from the world once, its inputs stamped so a tile is painted again ... | 12 |
| [Markets.java](Markets.md) | 608 | 30 | Every goods market in the city, and the month they clear in. | 40 |
| [MediumTermBond.java](MediumTermBond.md) | 255 | 18 | The amortizing bond: the workhorse of municipal finance - at 5 to 30 years in fives, one coupon, each sale its own line (CD2). | 11 |
| [Migration.java](Migration.md) | 1,340 | 58 | Why people move to this city, and the much narrower question of why they leave. | 19 |
| [MiniMapFrame.java](MiniMapFrame.md) | 316 | 32 | The minimap's arithmetic without the toolkit: what the stamp at the rail's foot and its hover preview frame - the city's built-up ground, the groun... | 6 |
| [MoneyAudit.java](MoneyAudit.md) | 1,313 | 28 | Where the money went this month, and whether it all went somewhere. | 62 |
| [Mortgage.java](Mortgage.md) | 556 | 40 | An insured mortgage on a new residential building: a level payment every month over a forty-year amortization, at a rate fixed for a ten-year term ... | 19 |
| [Motoring.java](Motoring.md) | 378 | 21 | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it - and since 0.7.49 the drivers' fuel a... | 6 |
| [NationalAccounts.java](NationalAccounts.md) | 1,571 | 121 | The city's GDP, measured properly, plus the government's own books. | 40 |
| [Nations.java](Nations.md) | 780 | 44 | The nations beyond the city, each on the world's path: its year - output, people, prices and its money's rate to the US dollar - from the path (His... | 39 |
| [NationsMap.java](NationsMap.md) | 1,078 | 68 | The nations on the map, without the toolkit: the borders simplified and cut to a view, each nation's dot and the name written on its land, what lie... | 9 |
| [Notice.java](Notice.md) | 119 | 17 | One thing the city needs told about, and whether anybody has looked at it. | 17 |
| [Offending.java](Offending.md) | 169 | 5 | Who is at risk of offending, sorted by reason, and the thefts handed to them. | 1 |
| [OilView.java](OilView.md) | 1,346 | 72 | The oil industry on one page (0.7.96, batch O12; docs/studies/spec-oil.md 2.13): the city's wells by kind with what they would lift over the next t... | 6 |
| [OldMetal.java](OldMetal.md) | 418 | 40 | The city's old steel: what it throws out each month that a Scrap Yard can buy and sort into scrap - its scrapped cars, its demolitions, its fabrica... | 17 |
| [OrderBook.java](OrderBook.md) | 459 | 52 | A limit-order book for one instrument: buy and sell orders from named participants, each a price and a quantity, matched by price-time priority. | 20 |
| [OreView.java](OreView.md) | 71 | 4 | The land office's Ore card as the model reads it (batch I13-L): for each ore the city's ground holds, its deposits, the tonnes still in the ground,... | 2 |
| [OrphanHousehold.java](OrphanHousehold.md) | 39 | 10 | Children no family holds, by age band. | 3 |
| [OutwardInvestment.java](OutwardInvestment.md) | 753 | 47 | Outward investment: what the city's businesses do with money the bank will not pay for. | 16 |
| [PartnerTrade.java](PartnerTrade.md) | 573 | 42 | The city's trade by partner: what it sold to each nation and bought of each this month and so far this year, by good, and every finished year's tab... | 16 |
| [PartnersView.java](PartnersView.md) | 315 | 16 | The Partners board's and a nation's page's figures (WN6; docs/studies/spec-nations.md 8, boards 2 and 3, D29): the table of who the city trades wit... | 3 |
| [PayTier.java](PayTier.md) | 135 | 6 | The six pay levels a household can be in. | 66 |
| [PolicyDates.java](PolicyDates.md) | 295 | 31 | When Canada first had each of the Policy tab's levers, as pure data: the game month each one opens in a city founded in January 1900, the study's e... | 19 |
| [PolicyPreview.java](PolicyPreview.md) | 592 | 50 | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and ... | 14 |
| [PolicyRates.java](PolicyRates.md) | 357 | 25 | The policy rates of the eight central banks of the moneys the vault holds (XR4; spec-exchange-rates 3.8, Jerus's calls 9 and 11): the Federal Reser... | 12 |
| [PopulationCohorts.java](PopulationCohorts.md) | 652 | 39 | The city's age pyramid, and since the switch, the city's POPULATION. | 30 |
| [PopulationManager.java](PopulationManager.md) | 1,263 | 64 | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). | 33 |
| [Ports.java](Ports.md) | 762 | 56 | The city's ports: the berths its sea terminals stand, the share of each kind of cargo they take off the lorries and the railway, and the tonnes tha... | 53 |
| [PriceIndex.java](PriceIndex.md) | 781 | 37 | What a month costs a household, against what it cost at founding. | 21 |
| [PriceLevel.java](PriceLevel.md) | 81 | 5 | The price level a city is founded at, 1900's: the US consumer price index of the calendar's first year over its 2025, and the series it is read fro... | 104 |
| [PrisonerHousehold.java](PrisonerHousehold.md) | 79 | 17 | Adults serving a sentence, as one ledger: the prisoners' ledger. | 5 |
| [Rankings.java](Rankings.md) | 730 | 38 | The Rankings board as the screen draws it: a measure's table of ranked places, the active city among the real world's 196 nations and 62 city regio... | 6 |
| [RealDollar.java](RealDollar.md) | 137 | 9 | The real US dollar, constant 2015 dollars, and what the city's money is against it (WN4, D29): the D$ per real US$, the city's real GDP in it, and ... | 6 |
| [Recipe.java](Recipe.md) | 192 | 23 | A way a building runs: what it uses, what it makes, who it needs and what it draws, and when it is known and what piece of equipment it needs. | 10 |
| [Recipes.java](Recipes.md) | 249 | 17 | Chooses the recipe each part of a stack runs, at the city's own prices, and sets the template's figures to what its buildings run: the driver of R1... | 5 |
| [Recording.java](Recording.md) | 375 | 22 | One recorded city on disk (TR1, 2026-10-31): the folder {@code recordings/<id>/} that "Record this city" keeps, and the one place that knows its fi... | 8 |
| [RefineryView.java](RefineryView.md) | 1,617 | 82 | The refinery's month as a picture (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): the crude it ran and where that came from, the column's cuts,... | 9 |
| [ReplayCity.java](ReplayCity.md) | 144 | 15 | The throwaway copy of a recorded city that replay shows (TR1, 2026-10-31): a Game of its own, in a scratch folder, that loads any recorded month an... | 3 |
| [ReplayGraphs.java](ReplayGraphs.md) | 92 | 6 | The faint lines behind the city in a replay (TR1, 2026-10-31), without the toolkit: a recorded series read once from the recording's own history, e... | 2 |
| [Research.java](Research.md) | 1,168 | 98 | The city's research (R3): the grants the player sets, the posts they buy, the researchers who fill them, the work piled on each open problem, the d... | 19 |
| [ResearchCatalog.java](ResearchCatalog.md) | 339 | 22 | The research catalogue (R2): every discovery the city's schools can make, read out of research.json: its code, the field that researches it, what i... | 23 |
| [ResearchPreview.java](ResearchPreview.md) | 450 | 26 | What a set of research dials would find in this city over the next YEARS years, from the DISTRIBUTION of the hidden sizes and never from the city's... | 5 |
| [ResearchView.java](ResearchView.md) | 1,050 | 87 | What the research screens draw (R5): every discovery on a timeline by field (found here, or from abroad on history's date), the equipment each sect... | 11 |
| [Resource.java](Resource.md) | 218 | 13 | The seven things that lie in the world's ground: iron ore, oil, stone, coal, copper, uranium and standing timber, each with how thickly its fields ... | 76 |
| [RetiredHousehold.java](RetiredHousehold.md) | 51 | 6 | A household with nobody of working age in it: a senior or an elder, alone or as a couple. | 3 |
| [Rollover.java](Rollover.md) | 382 | 34 | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). | 20 |
| [RunWorks.java](RunWorks.md) | 135 | 4 | What a highway or a railway line needs built on each class of ground beyond what it costs on the flat - cuts and fills on foothill, a viaduct on st... | 5 |
| [SafetyType.java](SafetyType.md) | 72 | 4 | What a safety building does: police, or prison cells. | 13 |
| [SalesTaxLedger.java](SalesTaxLedger.md) | 228 | 22 | The month's sales tax, as tax payable less input tax credits. | 5 |
| [SaveCopy.java](SaveCopy.md) | 54 | 5 | The small copies a save's snapshot is made of: a list or map copied, with each element copied too where the elements can change. | 9 |
| [SaveHeader.java](SaveHeader.md) | 102 | 18 | Just enough of a save to label it on the slot list. | 8 |
| [SaveWriter.java](SaveWriter.md) | 294 | 20 | The one thread saves are written on, for the whole JVM: it takes a finished snapshot of a city and turns it into the save, the map and the history ... | 5 |
| [SavedCities.java](SavedCities.md) | 256 | 19 | The player's other saved cities as the Rankings board reads them (spec-leaderboards 5, LB3): slots 1 to GameFiles.SLOT_COUNT of a GameFiles, read o... | 3 |
| [SeaLanes.java](SeaLanes.md) | 210 | 15 | The sea lanes a map draws: every leg of every terminal's route once - a leg that many routes share, once - indexed by the district cells it reaches... | 4 |
| [SeaRoutes.java](SeaRoutes.md) | 974 | 45 | Each sea terminal's route out to the world: found once on a grid of the sea at 480 m, pulled straight, sailed from the terminal's quay out to the o... | 6 |
| [Sector.java](Sector.md) | 3,790 | 266 | One business in the city, and the template every sector extends. | 150 |
| [SectorBooks.java](SectorBooks.md) | 1,030 | 60 | A month of books for every business in the city, and last month's too. | 29 |
| [SectorFlow.java](SectorFlow.md) | 261 | 17 | One business's month as a flow (0.7.30): what went in, what its plant made of it and what held the plant back, and what came out - each good's unit... | 5 |
| [SectorState.java](SectorState.md) | 278 | 6 | One sector, as a save carries it. | 12 |
| [SectorStatements.java](SectorStatements.md) | 1,084 | 66 | A business's month as formal statements (0.7.74): the income statement classified down through gross and operating profit, the balance sheet in cur... | 19 |
| [Sectors.java](Sectors.md) | 481 | 47 | Every sector in the city, in one order, by one name. | 138 |
| [ServicesManager.java](ServicesManager.md) | 401 | 28 | / | 7 |
| [ShadowBasket.java](ShadowBasket.md) | 201 | 1 | What a played city WOULD buy, measured against what it spends today. | 0 |
| [ShipShapes.java](ShipShapes.md) | 169 | 10 | How the map draws a boat: its length and beam by its class, its outline and deck marks as points in plots about where it is and the way it heads, i... | 7 |
| [ShortTermTBill.java](ShortTermTBill.md) | 121 | 14 | A bill: borrow now, repay one lump, no coupon - at 1, 3, 6 or 12 months, each sale its own line (CD2). | 12 |
| [Sickness.java](Sickness.md) | 357 | 24 | Who has been sick, and for how long - and the ones it kills. | 13 |
| [SimulationEngine.java](SimulationEngine.md) | 245 | 5 | The order the month runs in: the sites advance, the roads, the posts and the wages, the people, then the economy and the services. | 1 |
| [SitePreparation.java](SitePreparation.md) | 74 | 3 | What it takes to prepare a foothill site for a building, as builders' work: the earth a pad is cut and filled in and the wall where it meets the sl... | 8 |
| [SocialSecurity.java](SocialSecurity.md) | 150 | 9 | Contributions off every wage, and a pension for everyone too old to work. | 5 |
| [StrategicReserve.java](StrategicReserve.md) | 238 | 32 | The city's strategic reserve of crude oil: what it holds, what it paid for it, and what the player has told it to fill and release (0.7.85, batch O... | 15 |
| [StreetCurves.java](StreetCurves.md) | 152 | 8 | A street's smooth line from the plots it is laid on, and the plots that line's band reaches: pure - plots in, points in plots out, no plan and no s... | 4 |
| [StudentHousehold.java](StudentHousehold.md) | 60 | 10 | Full-time students, as one ledger. | 7 |
| [SupplierCredit.java](SupplierCredit.md) | 255 | 21 | What a buyer owes its suppliers for stock they let it have on credit (0.7.44): the grocers' trade credit, struck and repaid a month at a time. | 8 |
| [TaxPolicy.java](TaxPolicy.md) | 2,023 | 124 | The city's tax rates - the revenue half of what the player actually decides. | 64 |
| [TempDirs.java](TempDirs.md) | 141 | 7 | The one place a harness, or a scratch city, gets a temporary folder from: one folder per JVM under the system temp folder, deleted when that JVM ex... | 84 |
| [TilePainter.java](TilePainter.md) | 920 | 44 | Paints one tile of the city map: from the tile's ground, what the city owns of it, its district's street plan through it - each street's kind, widt... | 24 |
| [TileRaster.java](TileRaster.md) | 4,347 | 160 | A painted tile as pixels: an int[] of 0xAARRGGBB, a row at a time, at a whole number of pixels a plot - what the map view hands PixelWriter.setPixe... | 28 |
| [TimeSkipReport.java](TimeSkipReport.md) | 682 | 68 | What happened while you were not watching. | 7 |
| [Timing.java](Timing.md) | 215 | 14 | The one helper every timing assertion in the suite goes through: a figure on a clock against its budget, hard as the harness wrote it, or soft whil... | 21 |
| [TrackCurves.java](TrackCurves.md) | 377 | 27 | The railway's curves as one geometry, for the drawing and for the trains' routes alike (FX5, 0.8.18): a polyline's corners rounded with arcs of a r... | 9 |
| [Trade.java](Trade.md) | 40 | 4 | One fill: somebody sold somebody some units of a good at a price. | 32 |
| [TradeBand.java](TradeBand.md) | 253 | 21 | The freight band of what the city trades, split into its legs, as the Trade tab draws it (batch FR4; docs/studies/spec-freight-and-infrastructure.m... | 2 |
| [TradeLanes.java](TradeLanes.md) | 907 | 74 | The city's trade as routes on the Earth's map, without the toolkit: one lane from the city toward each partner, the lanes that go the same way shar... | 10 |
| [Traffic.java](Traffic.md) | 60 | 3 | The three things that move, which used to be one number. | 34 |
| [TrainSchedule.java](TrainSchedule.md) | 710 | 54 | The month's trains, as a pure function of time: which trains the city's railed tonnes make, when each reaches its yard, and where each one is at an... | 15 |
| [TrainShapes.java](TrainShapes.md) | 92 | 6 | How the map draws a train: each vehicle's body and its load's mark as points in plots about where it is and the way it heads, and their colours by ... | 3 |
| [TreasuryFund.java](TreasuryFund.md) | 1,123 | 153 | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand,... | 36 |
| [TreasuryJournal.java](TreasuryJournal.md) | 283 | 16 | The treasury's journal: every movement of the city's cash that is neither a budget line nor paper raised or repaid, recorded by name as it happens,... | 14 |
| [TreasuryLine.java](TreasuryLine.md) | 185 | 2 | Every kind of payment the treasury makes, and whether it is a promise. | 10 |
| [TruckSchedule.java](TruckSchedule.md) | 592 | 38 | The month's trucks, as a pure function of time: which loads Haulage's trucks carry, when each reaches its gate, and where each one is at any moment... | 6 |
| [TruckShapes.java](TruckShapes.md) | 95 | 4 | How the map draws a truck: its body as points in plots about where it is and the way it heads, and its colour by what it carries - what ui/MapView ... | 1 |
| [UnemployedHousehold.java](UnemployedHousehold.md) | 88 | 14 | Adults who are out of work, as one ledger per situation. | 7 |
| [Unemployment.java](Unemployment.md) | 607 | 40 | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. | 12 |
| [UtilitiesHandler.java](UtilitiesHandler.md) | 733 | 70 | sections: WATER SUPPLY, THE FRESH WATER LIMIT (0.7.59, batch J2; spec-land 2.3 and star 7)... | 25 |
| [VaultView.java](VaultView.md) | 64 | 2 | The vault in eight (WN6; docs/studies/spec-nations.md 8, the Trade screen's The reserves with the eight moneys in a table): what the vault holds of... | 2 |
| [WageBand.java](WageBand.md) | 170 | 7 | The four education bands the wage tax is set by. | 36 |
| [WageLadder.java](WageLadder.md) | 145 | 10 | The wage ladder a city is founded on: today's (PayTier's) or 1901's (the census's). | 6 |
| [WantedPosts.java](WantedPosts.md) | 222 | 19 | The posts the city's businesses wanted this month and the staffing gate would not let them open, and the part of them the labour market prices as i... | 11 |
| [WorkingHousehold.java](WorkingHousehold.md) | 50 | 7 | A household with an earner in it, at one pay tier. | 5 |
| [World.java](World.md) | 2,162 | 120 | The world a city is founded on: a flat square the size of the Earth with its sea, lakes, forest and beaches, the founding site with its lake and ri... | 106 |
| [WorldData.java](WorldData.md) | 1,192 | 109 | The reader of world-1.bin, the nations' world as data: the 196 nations and the areas no nation holds, their borders (an owner a 15" cell, and the l... | 32 |
| [WorldEconomy.java](WorldEconomy.md) | 550 | 27 | The rest of the world, which has its own inflation and did not use to: the in-game US dollar's price level, which is the US's own path where the ci... | 14 |
| [WorldMarket.java](WorldMarket.md) | 2,024 | 127 | The world market: each traded good's price in each of the 196 nations on a smooth spatial equilibrium (a CES gravity model), solved once a year, an... | 17 |
| [WorldMeasures.java](WorldMeasures.md) | 388 | 46 | The Rankings' real-world figures: world-2025.json's 196 nations and city regions, the 70 measures each is ranked on, every place's latest figure of... | 5 |
| [WorldMoneys.java](WorldMoneys.md) | 800 | 56 | The world's moneys against the US dollar, a month at a time (WN4; spec-exchange-rates 3.7, spec-nations 5.1): each money's rate is its purchasing-p... | 29 |
| [YearBook.java](YearBook.md) | 1,675 | 85 | The run, one line a year - for READING rather than for drawing. | 23 |
| [YieldCurve.java](YieldCurve.md) | 295 | 21 | The city's curve: a discount factor for every month to fifty years, built from the dial the market expects, the credit spreads, and the shape the t... | 12 |
| [Zoning.java](Zoning.md) | 280 | 29 | The city's zoning (0.7.107, batch Z1): ground held for one sector's buildings - a share of the city's dry ground or a size in square kilometres, at... | 20 |
| [ZoningView.java](ZoningView.md) | 421 | 24 | Policy › Zoning as figures (0.7.109, batch Z2; the project's spec-zoning.md 4, the zoning study's three boards): the hub's four figures, the city's... | 5 |

## sectors (30 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [Agriculture.java](Agriculture.md) | 539 | 22 | The fields, and what they cost the city in ground. | 9 |
| [Automotive.java](Automotive.md) | 312 | 7 | The automobile industry. | 4 |
| [BusinessServices.java](BusinessServices.md) | 295 | 10 | Somebody else's work, done here, paid for from outside. | 5 |
| [CementConcrete.java](CementConcrete.md) | 73 | 2 | Kilns and cement plants - Cement & Concrete (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.12 and 5's I7 row; the study's 6): stone's pr... | 6 |
| [CoalMining.java](CoalMining.md) | 447 | 23 | Coal mines and the plants that wash their coal - Coal Mining (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.10 and 5's I6 row; the study... | 10 |
| [Construction.java](Construction.md) | 1,153 | 65 | The builders. | 24 |
| [CopperMining.java](CopperMining.md) | 256 | 12 | Copper mines and the leach plants on oxide fields - Copper Mining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.4 and 5's I8 row; the s... | 6 |
| [CopperRefining.java](CopperRefining.md) | 261 | 13 | Smelters, refineries and rod mills - Copper Refining (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12 and 5's I8 row; the stu... | 10 |
| [ExtractionSector.java](ExtractionSector.md) | 603 | 33 | A sector that digs or cuts one resource out of the ground the city owns: the template every extraction industry of the materials chains extends (0.... | 18 |
| [FoodIndustry.java](FoodIndustry.md) | 98 | 6 | The mills and the plants that feed the shops. | 4 |
| [FoodProcessing.java](FoodProcessing.md) | 591 | 13 | The plants between the farm and the shelf. | 2 |
| [Forestry.java](Forestry.md) | 630 | 35 | The crews that cut the city's forest, and the plantations they plant - Forestry (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.11 and 5'... | 12 |
| [Freight.java](Freight.md) | 2,166 | 107 | The Freight sector: the city's carrier, in the railway's slot (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.md 3.1, D1). | 44 |
| [Haulage.java](Haulage.md) | 1,437 | 89 | Freight's Haulage division: the trucks that carry the city leg of every bulk tonne (FR2, 2026-10-18; docs/studies/spec-freight-and-infrastructure.m... | 28 |
| [HeavyIndustry.java](HeavyIndustry.md) | 288 | 14 | The mills - Iron & Steel since 0.7.120, under the key it was saved by, "Heavy Industry": the arc furnaces, which melt scrap into steel, and since 0... | 31 |
| [LuxuryRetail.java](LuxuryRetail.md) | 558 | 24 | The luxury shops. | 5 |
| [Manufacturing.java](Manufacturing.md) | 371 | 14 | What the city makes out of its own steel, and ships. | 5 |
| [Materials.java](Materials.md) | 88 | 4 | The materials plant. | 1 |
| [Mining.java](Mining.md) | 85 | 4 | Iron mines - Iron Mining since 0.7.120, under the key it was saved by, "Mining" (Jerus's Q2, 2026-10-08: rename, keeping the saved keys). | 32 |
| [Oil.java](Oil.md) | 1,752 | 117 | Oil wells. | 20 |
| [PortDivision.java](PortDivision.md) | 363 | 27 | Freight's Ports division: the crews that work the city's berths, paid by the shippers who use them and paying the city for the quay (FR3, 2026-10-1... | 11 |
| [ProcessingSector.java](ProcessingSector.md) | 543 | 43 | A sector that turns goods into other goods in works it builds by what pays: the template every processing industry of the materials chains extends ... | 18 |
| [Quarrying.java](Quarrying.md) | 412 | 21 | Quarries - Quarrying (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.4, 2.10 and 5's I7 row; the study's 6): the third materials chain's ... | 9 |
| [RealEstate.java](RealEstate.md) | 978 | 94 | The landlords. | 20 |
| [RefineryFlow.java](RefineryFlow.md) | 372 | 29 | The refinery as a campus of units, and a month's flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3): the crude units' run cut into ... | 16 |
| [Refining.java](Refining.md) | 1,936 | 109 | The refinery. | 21 |
| [Restaurants.java](Restaurants.md) | 532 | 25 | The kitchens. | 8 |
| [Retail.java](Retail.md) | 1,411 | 105 | The shops. | 41 |
| [SpreadPlanner.java](SpreadPlanner.md) | 631 | 52 | The spread planner (0.7.82, batch O5; docs/studies/spec-oil.md 2.4, and docs/studies/spec-materials.md 2.6): what a processing sector orders - of t... | 36 |
| [WoodProducts.java](WoodProducts.md) | 107 | 2 | Sawmills, and the panel, pulp, paper and pellet mills - Wood Products (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.7, 2.8, 2.12 and 5... | 6 |

## interface (46 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [BankScreen.java](BankScreen.md) | 4,655 | 179 | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figu... | 6 |
| [Branding.java](Branding.md) | 124 | 4 | The game's identity on screen: its icon on the window (every size of resources/icons/ handed to the stage, so Windows takes the one it needs for th... | 3 |
| [BuildScreen.java](BuildScreen.md) | 6,547 | 240 | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.... | 16 |
| [CapChips.java](CapChips.md) | 213 | 8 | The highest yield a sale will take, as three chips and a yield box: fair plus a point (the default), a yield the player names, or Fill it (CD2, D28). | 3 |
| [ChartFitBench.java](ChartFitBench.md) | 130 | 3 | The full-screen chart on a scene of its own - no stage, no window - laid out at a window's size with some lines picked, for ChartFitCheck (FW10): t... | 1 |
| [CityBuilderSim.java](CityBuilderSim.md) | 55 | 2 | The way in. | 0 |
| [ConstructionScreen.java](ConstructionScreen.md) | 1,319 | 45 | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - pri... | 2 |
| [FinancesScreen.java](FinancesScreen.md) | 7,069 | 289 | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece an... | 8 |
| [FoundingScreen.java](FoundingScreen.md) | 1,427 | 61 | Found a city: its name, its money, what the founders leave in the treasury and the vault, the ground it stands on and the world it is founded into. | 2 |
| [FundScreen.java](FundScreen.md) | 2,580 | 99 | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with it... | 3 |
| [GovernmentScreen.java](GovernmentScreen.md) | 2,703 | 113 | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, w... | 3 |
| [HistoryScreen.java](HistoryScreen.md) | 3,374 | 124 | City History: the city as a shape over time. | 15 |
| [Icons.java](Icons.md) | 560 | 6 | The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one per category, and the few the frame draws (the money block, the "Needs... | 22 |
| [InfrastructureScreen.java](InfrastructureScreen.md) | 2,215 | 77 | The infrastructure tab: the roads, the trams, the freight and the ports - four pages under the five figures the whole tab is about, each page led b... | 2 |
| [InputRecorder.java](InputRecorder.md) | 205 | 14 | The window's input kept for the replay's ghost (TR1, 2026-10-31): while "Record this city" is on, the mouse and the keys of the sitting - and the m... | 1 |
| [IslandChip.java](IslandChip.md) | 48 | 2 | The chip the map draws on a landmass no port links: its lines of amber mono caption on a dark rounded plate the width of the longer line (MT1, BK1-L). | 2 |
| [Kept.java](Kept.md) | 687 | 66 | A page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): the nodes a screen's code made at its last draw, handed back to the... | 30 |
| [KeptBench.java](KeptBench.md) | 4,894 | 545 | The People page on a scene of its own - no stage, no window - drawn kept and built new, for KeptCheck (study UP4): the harness's hands on the page,... | 1 |
| [Ladder.java](Ladder.md) | 449 | 31 | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying... | 16 |
| [LandScreen.java](LandScreen.md) | 2,576 | 102 | The land office: whether the city has room to grow, and which ground to buy - since 0.7.61 round the city's map (batch J4; the project's spec-land.... | 4 |
| [Levers.java](Levers.md) | 166 | 4 | The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36) - the dial on a card with what it does beside it, which every dial on t... | 3 |
| [MapView.java](MapView.md) | 6,078 | 258 | The city map on screen: one Canvas the land office shows small, 600 x 400, and Expand lays over the window to pan and zoom, drawn from the model's ... | 4 |
| [MiniMap.java](MiniMap.md) | 471 | 20 | The minimap: a stamp of the city at the foot of the rail, above Menu, on screen from every tab; while the pointer is on it a preview beside it, 288... | 1 |
| [Money.java](Money.md) | 385 | 30 | Every figure the interface prints as money, in one place. | 23 |
| [Palette.java](Palette.md) | 734 | 27 | Every colour, size and spacing this game is allowed to use, in one place. | 39 |
| [PartnersPage.java](PartnersPage.md) | 1,173 | 56 | Trade &gt; Partners (WN6; docs/studies/spec-nations.md 8, board 2) and a nation's page behind it: who the city trades with, nation by nation; for a... | 1 |
| [PeopleScreen.java](PeopleScreen.md) | 4,732 | 167 | The People tab and its second page, Household money. | 4 |
| [Pieces.java](Pieces.md) | 6,664 | 302 | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals ba... | 25 |
| [PolicyScreen.java](PolicyScreen.md) | 5,831 | 299 | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it w... | 12 |
| [RankingsScreen.java](RankingsScreen.md) | 1,766 | 81 | The Rankings tab: the active city on a board of the real world's nations and city regions (or of the nations of its own world), measure by measure,... | 2 |
| [ReplayCamera.java](ReplayCamera.md) | 390 | 46 | The replay's camera (TR1, 2026-10-31): the two marked views and the glide between them, the fly out to the whole Earth and back, the load of a move... | 4 |
| [ReplayGraphLayer.java](ReplayGraphLayer.md) | 194 | 8 | The faint GDP and population lines behind the city in a replay (TR1, 2026-10-31): one mouse-transparent canvas laid over the window's content and u... | 1 |
| [ReplayMode.java](ReplayMode.md) | 991 | 47 | Replay mode (TR1, 2026-10-31): a recorded city (Recording) shown on a throwaway copy of it (ReplayCity) in the game's own window, with a time bar a... | 4 |
| [ReplayMoveProbe.java](ReplayMoveProbe.md) | 876 | 32 | What a replay's camera moves show unpainted, measured: it opens the real window full screen on a recorded city in replay mode, runs the seven moves... | 1 |
| [ResearchScreen.java](ResearchScreen.md) | 1,199 | 73 | Policy › Research (R5, slice B1; R3b, slice B): the sixth Policy area, drawn in Policy's own style - three pages: the Overview (who is at research ... | 3 |
| [SectorScreen.java](SectorScreen.md) | 7,221 | 291 | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with la... | 8 |
| [ServicesScreen.java](ServicesScreen.md) | 3,452 | 134 | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one p... | 5 |
| [Statement.java](Statement.md) | 517 | 25 | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank... | 30 |
| [StatementView.java](StatementView.md) | 646 | 39 | A formal statement on the page (0.7.74): SectorStatements' rows set as an accountant sets them - a title block, the columns (note, this month, last... | 3 |
| [SummaryScreen.java](SummaryScreen.md) | 1,547 | 33 | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen... | 10 |
| [TimeChart.java](TimeChart.md) | 1,647 | 91 | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, th... | 13 |
| [TradeScreen.java](TradeScreen.md) | 3,942 | 164 | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what ... | 7 |
| [UiProbe.java](UiProbe.md) | 729 | 23 | What the window costs the JavaFX thread as a city ages, measured: it grows the playtest's city to the months it is given, opens the real window on ... | 0 |
| [UserInterface.java](UserInterface.md) | 7,261 | 235 | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating a... | 39 |
| [VaultTable.java](VaultTable.md) | 235 | 13 | The vault in eight moneys (WN6; docs/studies/spec-nations.md 8, board 3): the first card of Trade &gt; The reserves where the vault holds, or can h... | 1 |
| [ZoningScreen.java](ZoningScreen.md) | 1,135 | 71 | Policy › Zoning (0.7.109, batch Z2): the fifth Policy area - the city's ground and its zones on one page, a sector's zone on its own page of dial c... | 3 |

## harnesses (188 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgricultureCheck.java](AgricultureCheck.md) | 463 | 7 | The ground under the loaf. | 0 |
| [AllChecks.java](AllChecks.md) | 490 | 16 | Runs every harness, one JVM each, and says which failed. | 4 |
| [ArrivalMixCheck.java](ArrivalMixCheck.md) | 375 | 21 | The skill mix of the arrivals by year (R0d slice 2): the year's mix in a city founded the 1900 way, and WageBand's ceilings in every other city. | 0 |
| [AuctionCheck.java](AuctionCheck.md) | 1,675 | 42 | The city's paper sold at auction: one clearing yield, and what it costs. | 0 |
| [AutoBuildCheck.java](AutoBuildCheck.md) | 1,975 | 45 | Automatic building (0.7.73, batch N4): AutoBuilder's month held to its rules, in a city it builds for decades and in fixtures that cause each of th... | 0 |
| [BankCheck.java](BankCheck.md) | 4,534 | 24 | The commercial bank, and the families it discharges. | 1 |
| [BasketYearCheck.java](BasketYearCheck.md) | 775 | 36 | The household's basket by year (R0e slice 1): the year's basket in a city founded the 1900 way, today's in every other. | 0 |
| [BondCheck.java](BondCheck.md) | 1,660 | 40 | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for co... | 2 |
| [BooksCheck.java](BooksCheck.md) | 299 | 3 | Verifies a sector's income statement and balance sheet, off the template. | 0 |
| [BrandingCheck.java](BrandingCheck.md) | 168 | 5 | The game's icon is in the jar at every size it is given, the exe's .ico carries the sizes it should, and the store's title art is drawn to the star... | 0 |
| [BridgeCheck.java](BridgeCheck.md) | 971 | 39 | The long bridges checked (ET6, 2026-10-26; docs/queue/ET6-notes.md): the survey of a crossing on grids of its own making and on the Earth, what a s... | 1 |
| [BridgeMapCheck.java](BridgeMapCheck.md) | 1,015 | 51 | A long bridge that stands is laid by the runs, kept clear by the plans and drawn with its deck (ET6; docs/studies/spec-earth2-terrain.md 6.5 and 6.6). | 0 |
| [BuildAdviceCheck.java](BuildAdviceCheck.md) | 1,484 | 47 | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city -... | 2 |
| [BuildCardCheck.java](BuildCardCheck.md) | 2,282 | 41 | The build card (0.7.25): BuildCard's figures for every building in buildings.json (143 since FR3, the Bulk Berth) held to the model's own reads - t... | 0 |
| [BuildMenuCheck.java](BuildMenuCheck.md) | 288 | 2 | Verifies that every building in the game can describe itself. | 0 |
| [BuildersFirstCheck.java](BuildersFirstCheck.md) | 683 | 17 | The builders' own depots first (0.7.130, batch T1-C): the rule that serves a depot on site the crews' rule would starve - keep there past the month... | 1 |
| [BuildingDataCheck.java](BuildingDataCheck.md) | 928 | 6 | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. | 0 |
| [BusinessServicesCheck.java](BusinessServicesCheck.md) | 386 | 5 | The sector whose customer is not in the city. | 0 |
| [BuybackCheck.java](BuybackCheck.md) | 862 | 31 | The city's paper bought back in a reverse auction (CD4, spec-city-debt.md section 6.2-6.3; D12, D17-D19, D26, D34): every holder but the central ba... | 0 |
| [CalendarCheck.java](CalendarCheck.md) | 282 | 6 | The date on the status bar, and the log of what the city has finished. | 0 |
| [CallCheck.java](CallCheck.md) | 809 | 30 | Callable bonds, the call and "worth calling" (CD4, spec-city-debt.md sections 2.6 and 6.4; D22, D23, D24): a bond of ten years or more may be sold ... | 1 |
| [CapitalFlowCheck.java](CapitalFlowCheck.md) | 787 | 9 | Hot money: does it come for the right reason, and does it leave for one? | 0 |
| [CarCheck.java](CarCheck.md) | 1,075 | 8 | The cars: who buys one, what it costs them, and what it does to the road. | 2 |
| [CarryTradeCheck.java](CarryTradeCheck.md) | 382 | 5 | The carry trade: the other side of hot money, and the bank's first borrower. | 0 |
| [CausesOfDeathCheck.java](CausesOfDeathCheck.md) | 914 | 25 | Proves the causes of death of 1900 (R0b, slice 1): the model on its own, before the month hands its rates to the cohorts. | 0 |
| [CementConcreteCheck.java](CementConcreteCheck.md) | 354 | 11 | Cement & Concrete, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.12, 4 and 5's I7 row; the study's - materials-chains-rese... | 0 |
| [CentralBankCheck.java](CentralBankCheck.md) | 2,516 | 44 | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its tw... | 0 |
| [ChartCheck.java](ChartCheck.md) | 963 | 28 | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the tick... | 0 |
| [ChartFitCheck.java](ChartFitCheck.md) | 156 | 4 | The full-screen chart's foot is on the screen (FW10): with the legend wrapped to two rows or three, the chart still ends inside the window and the ... | 0 |
| [ChildcareCheck.java](ChildcareCheck.md) | 558 | 20 | Childcare resized (0.7.71, batch N2): the three childcare centres, the build advice's size that fits the need (BuildAdvice, THE SIZE THAT FITS THE ... | 3 |
| [ClockCheck.java](ClockCheck.md) | 689 | 25 | The clock's pace (UP2): a redraw is paced by what it cost, a frame spends a budget on months and drops what it could not run, the header's speed sa... | 1 |
| [CoalMiningCheck.java](CoalMiningCheck.md) | 628 | 15 | Coal Mining, checked (0.7.126, batch I6; docs/studies/spec-materials.md 2.1, 2.4, 2.10, 4 and 5's I6 row; the study's - materials-chains-research.m... | 2 |
| [ConservationCheck.java](ConservationCheck.md) | 298 | 6 | Nothing is created and nothing is destroyed. | 0 |
| [ConstructionControlCheck.java](ConstructionControlCheck.md) | 851 | 21 | The player's hand on the construction queue (0.7.22): each of ConstructionControl's five rules held to its own arithmetic, in a played city. | 0 |
| [ConstructionTrapCheck.java](ConstructionTrapCheck.md) | 1,240 | 36 | The builders short (FC1): what an order pays a point of work as the repairs leave the builders fewer points for the sites, and the foreign contract... | 0 |
| [ConsumptionCheck.java](ConsumptionCheck.md) | 343 | 4 | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. | 0 |
| [ConversionCheck.java](ConversionCheck.md) | 702 | 20 | Saved cities put on the block grid (0.7.66, batch M2): the five saves the design was measured on, each converted - a format-31 save's lanes snapped... | 0 |
| [CopperMiningCheck.java](CopperMiningCheck.md) | 525 | 13 | Copper Mining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 4 and 5's I8 row; the study's - materials-chains-research.... | 2 |
| [CopperRefiningCheck.java](CopperRefiningCheck.md) | 1,132 | 27 | Copper Refining, checked (0.7.133, batch I8; docs/studies/spec-materials.md 2.1, 2.7, 2.9, 2.12, 4 and 5's I8 row; the study's - materials-chains-r... | 0 |
| [CreditCheck.java](CreditCheck.md) | 2,108 | 19 | Verifies private-sector credit: pricing, origination, rollover, cash conservation. | 0 |
| [CrimeCheck.java](CrimeCheck.md) | 345 | 8 | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. | 0 |
| [CrossingCheck.java](CrossingCheck.md) | 520 | 27 | Short crossings of bridgeable water (ET6; spec-earth2-terrain 6.2, D12): the shallow sea is water a road or a railway may bridge, exactly as fresh ... | 0 |
| [CurrencyCheck.java](CurrencyCheck.md) | 949 | 22 | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry ap... | 6 |
| [CurrencyRegimeCheck.java](CurrencyRegimeCheck.md) | 2,467 | 74 | Proves the D$'s four regimes - Float, Lean against falls (the default), Band and Peg to the US$ - hold the currency where they say and cost what th... | 3 |
| [CurveCheck.java](CurveCheck.md) | 744 | 36 | Proves the curve from the dial the market expects (CD1, spec-city-debt.md section 3): the expected dial's path, the zero rates it compounds to, the... | 0 |
| [DeathCareFirstCheck.java](DeathCareFirstCheck.md) | 535 | 18 | Death care first while the dead wait (0.7.129, batch T1-B): the rule that serves the city's own cemeteries and crematoria first out of its share of... | 2 |
| [DeathRecordCheck.java](DeathRecordCheck.md) | 251 | 6 | The running totals of the dead, by age and for the orphans and the unhoused. | 0 |
| [DebtExchangeCheck.java](DebtExchangeCheck.md) | 703 | 23 | The exchange gain (loss) on the city's foreign-currency debt (CD7, spec-city-debt.md section 5): each dollar piece carried in US$ and read in D$ at... | 0 |
| [DebtMoneysCheck.java](DebtMoneysCheck.md) | 611 | 17 | The city's paper in any of the eight moneys (CD8, Jerus 2026-10-23): the city sells bills and bonds abroad in the euro, the yen, the pound, the yua... | 1 |
| [DenominationCheck.java](DenominationCheck.md) | 1,214 | 38 | A currency reform is a change of units, and this is how we know. | 0 |
| [DeskCheck.java](DeskCheck.md) | 1,286 | 31 | The debt desk's figures (CD6, spec-city-debt.md sections 7 and 8): the figures the debt screens used to work out for themselves, held to the model ... | 1 |
| [DeterminismCheck.java](DeterminismCheck.md) | 447 | 19 | The month reads nothing of the machine it runs on: the same city, played in JVMs set up as opposite machines, comes out the same to the bit. | 1 |
| [DiscoveryCheck.java](DiscoveryCheck.md) | 1,391 | 44 | The city finds discoveries of its own, and a discovery found here is known here from the month it was found (R3, slice A: Research, and the seam in... | 2 |
| [EarthCheck.java](EarthCheck.md) | 1,065 | 33 | The Earth's reader (0.7.111, batch E1; the project's spec-earth.md 2 and 8): EarthProj's projection, blend and areas held to the spec's measures, a... | 12 |
| [EarthDepositsCheck.java](EarthDepositsCheck.md) | 789 | 36 | Deposits where the geology says (0.8.12, batch EG1; docs/studies/spec-earth2-terrain.md 8.5): oil by a province's known petroleum, iron on shields ... | 0 |
| [EarthFoundingCheck.java](EarthFoundingCheck.md) | 706 | 18 | Founding a city on the real Earth (0.7.121, batch E4; the project's spec-earth.md 2, 6 and 8): the founding card's figures against the plots they a... | 0 |
| [EarthTerrainCheck.java](EarthTerrainCheck.md) | 2,524 | 60 | The Earth's ground (0.7.115, batch E2; the project's spec-earth.md 3 and 8): EarthTerrain's plots held to the data they are drawn from on the three... | 4 |
| [EducationCheck.java](EducationCheck.md) | 1,484 | 11 | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. | 2 |
| [EnsembleCheck.java](EnsembleCheck.md) | 509 | 24 | The inflation ensemble is in the tree and still runs: its driver compiles against this tree, its two halves name the same policies, and the stored ... | 0 |
| [EquipmentCheck.java](EquipmentCheck.md) | 1,161 | 46 | Equipment as data, the count of buildings still without it on each stack, R0c's 1900 forms set from that count, and (slice B) what a piece would ea... | 8 |
| [EquityCheck.java](EquityCheck.md) | 386 | 5 | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. | 0 |
| [ExchangeCheck.java](ExchangeCheck.md) | 1,076 | 27 | Verifies the exchange on the order book (0.7.12 round 2): what the desk posts and what it is not obliged to take, who trades with whom and at what ... | 3 |
| [ExchangeRateCheck.java](ExchangeRateCheck.md) | 1,449 | 48 | Proves the D$ is a float that follows its parity and swings like a real currency (XR1, 2026-10-21): parity is the basket with rent held to a third,... | 2 |
| [ExpectationsCheck.java](ExpectationsCheck.md) | 563 | 21 | Proves the anchor (Expectations, 0.7.42): credibility won on target and lost to a miss nobody leans against, expected inflation and its floor, the ... | 0 |
| [FittingCheck.java](FittingCheck.md) | 1,087 | 33 | Fitting the equipment (R1b): the 1900 forms at rest between the presses, a Game's forms cleared when it founds or loads another city, the city foun... | 7 |
| [FloatLinkCheck.java](FloatLinkCheck.md) | 1,904 | 61 | The floating-rate note and the inflation-linked bond (CD5, spec-city-debt.md sections 2.4, 2.5, 5.3 and 5.4; D25): a floater pays the month's polic... | 5 |
| [FoodProcessingCheck.java](FoodProcessingCheck.md) | 511 | 7 | The third of the shelf that arrives already made. | 0 |
| [ForeignCheck.java](ForeignCheck.md) | 1,909 | 16 | The balance of payments, and whether the boundary it is drawn on is honest. | 7 |
| [ForeignDebtCheck.java](ForeignDebtCheck.md) | 1,051 | 14 | Borrowing in somebody else's money. | 3 |
| [ForestryCheck.java](ForestryCheck.md) | 706 | 21 | Forestry, checked (0.7.138, batch I9; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.11, 4 and 5's I9 row; the study's - materials-chains-research... | 4 |
| [Forms1900Check.java](Forms1900Check.md) | 994 | 38 | The farm's horses, the factory's steam, road freight's horse and wagon and the homes' lamp oil and coal, as forms a share of a sector or of the hom... | 4 |
| [FoundingYardCheck.java](FoundingYardCheck.md) | 630 | 27 | The founding yard (PT1-M4, 2026-10-12): the works department's free 400 points a month are retired and every city founds with one Builders' Yard st... | 0 |
| [FreightViewCheck.java](FreightViewCheck.md) | 716 | 26 | FreightView, the model behind the Freight screens, held to the study's board 2 (docs/studies/spec-freight-and-infrastructure.md 6): the city's frei... | 0 |
| [FundCheck.java](FundCheck.md) | 1,394 | 26 | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). | 1 |
| [FundLedgerCheck.java](FundLedgerCheck.md) | 1,005 | 27 | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, thr... | 0 |
| [GatewayCheck.java](GatewayCheck.md) | 1,204 | 40 | A city's gateway is the way the world is. | 4 |
| [GdpCheck.java](GdpCheck.md) | 959 | 5 | Verifies the national accounts: the identity, growth rates, and the government's books - and, since 0.7.58, that every good a sector holds is stock... | 0 |
| [GlobeCheck.java](GlobeCheck.md) | 3,473 | 110 | The Earth's map as a globe, its model half (0.7.132, batch E8; docs/studies/spec-globe.md 3 and 4's E8 row): EarthProj's orthographic projection, G... | 1 |
| [GridCheck.java](GridCheck.md) | 619 | 22 | The block grid (0.7.65, batch M1): the city's owned ground as a quadtree, every plot's owner exact against a raster, and its offers six a side - ap... | 2 |
| [GroceryCheck.java](GroceryCheck.md) | 459 | 10 | Groceries at a price, the shelf cleared stickily, and food assistance (0.7.43): the households' demand curve, the clearing price, who is handed the... | 0 |
| [HaulageCheck.java](HaulageCheck.md) | 1,635 | 43 | Freight's trucks, held to FR2's brief: the city leg of every bulk tonne is billed once, by Haulage, at a quote the trucks' own books set, and the v... | 10 |
| [HealthCheck.java](HealthCheck.md) | 1,760 | 6 | Sickness: what it moves, and - much more importantly - what it does not. | 0 |
| [HistoryCheck.java](HistoryCheck.md) | 857 | 11 | Verifies the graph history: recording, alignment, and the round trip. | 0 |
| [HoldersCheck.java](HoldersCheck.md) | 2,505 | 51 | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is boug... | 0 |
| [HomeRailCheck.java](HomeRailCheck.md) | 921 | 28 | The railway's carriage of the city's own bulk, and the port yard, held to FR6's brief: a bulk tonne bought at home rides a siding at the city leg's... | 2 |
| [HouseholdCheck.java](HouseholdCheck.md) | 1,700 | 7 | Verifies the residents' books and the demolition log. | 0 |
| [HouseholdMemoryCheck.java](HouseholdMemoryCheck.md) | 292 | 6 | The households remember: the builder keeps what still fits. | 0 |
| [HousingCheck.java](HousingCheck.md) | 791 | 5 | Audits the three subsystems that describe the same housing, every month, and makes them agree. | 0 |
| [HydroCheck.java](HydroCheck.md) | 611 | 17 | The Hydro Station and the river it needs (HY1, 2026-10-26; docs/queue/HY1-notes.md): the physics of a drop against its own constants, the sites on ... | 0 |
| [InboxCheck.java](InboxCheck.md) | 527 | 8 | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. | 0 |
| [IndustryViewCheck.java](IndustryViewCheck.md) | 1,221 | 32 | The materials pictograms' model, checked (0.7.141, batch I11; docs/studies/spec-materials.md 2.13, 4's IndustryViewCheck line and 5's I11 row): Ind... | 0 |
| [InfrastructureCheck.java](InfrastructureCheck.md) | 1,184 | 8 | The road network, from the curve up to a city that actually jams. | 0 |
| [InterestAbroadCheck.java](InterestAbroadCheck.md) | 1,185 | 39 | The central banks of the eight moneys the vault holds and the price of money the city reads (batch XR4, interest abroad; spec-exchange-rates 3.8, J... | 2 |
| [InterventionCheck.java](InterventionCheck.md) | 548 | 14 | Proves a purchase or sale of dollars moves the D$ a BOUNDED amount (XI1, 2026-11-06): linear up to INTERVENTION_LINEAR of a year's GDP, exactly as ... | 0 |
| [InvestCheck.java](InvestCheck.md) | 2,067 | 20 | Verifies the private investment engine: forecasting, the demand tests, and the brake. | 0 |
| [IslandNetCheck.java](IslandNetCheck.md) | 834 | 32 | A highway net never stops while the city has ground, and a net is laid on every piece of ground the water parts (CL5). | 2 |
| [JunctionCheck.java](JunctionCheck.md) | 2,367 | 94 | Proves that the junctions and interchanges of spec-freight 4.6 are drawn by rule from what crosses what, not as buildings (0.8.1, batch FR0; D19). | 5 |
| [KeptCheck.java](KeptCheck.md) | 8,853 | 201 | The People page kept between redraws (study UP4, docs/studies/spec-screens-in-place.md): each month's redraw run over last month's nodes ends exact... | 2 |
| [KnownRedsCheck.java](KnownRedsCheck.md) | 311 | 15 | The harness for AllChecks' known reds (batch FW7): a failed harness is said to be a known red or a NEW one against main's record, line by line, and... | 0 |
| [LabourCheck.java](LabourCheck.md) | 1,216 | 14 | Verifies the labour market: who can hold a job, and what it costs. | 1 |
| [LandCheck.java](LandCheck.md) | 3,521 | 55 | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. | 6 |
| [LandClassCheck.java](LandClassCheck.md) | 1,349 | 51 | A price for each class of ground, checked (ET3, slice A; docs/studies/spec-earth2-terrain.md 5.1 and 5.2): the going price stays the price of flat ... | 3 |
| [LandNationsCheck.java](LandNationsCheck.md) | 1,249 | 47 | Land bought from the nation it lies in, checked (WN5; docs/studies/spec-nations.md 6.2-6.4, D21, D22): the land table, each nation's land factor an... | 1 |
| [LandlordPortCheck.java](LandlordPortCheck.md) | 739 | 29 | The landlord port, held to FR3's brief (docs/queue/FR3.md): the city builds and keeps the sea terminals and Freight's Ports division works them. | 0 |
| [LandmassCheck.java](LandmassCheck.md) | 3,300 | 94 | The city's ground counted by landmass, and the island rule, checked (ET5; the ET5 design): a landmass is read from the Earth's data at every plot, ... | 3 |
| [LongPlaytest.java](LongPlaytest.md) | 8,112 | 151 | A city played for 2,400 months at the gate and 4,000 once a night on main (tools/nightly.sh), the way a person plays. | 57 |
| [MainLineCheck.java](MainLineCheck.md) | 1,743 | 56 | The main line is the railway's run from its gateway to the freight station, the yard nearest the gate arm's end, and it is drawn double where the c... | 2 |
| [ManufacturingCheck.java](ManufacturingCheck.md) | 632 | 8 | The ninth sector: what the city makes out of its own steel, and ships. | 0 |
| [MapCheck.java](MapCheck.md) | 14,464 | 383 | The city map's data and painter: the districts add up to the model's counts every month and nothing placed moves, the painter draws each district's... | 12 |
| [MarketCheck.java](MarketCheck.md) | 684 | 22 | Verifies the efficient market (EM1, 2026-10-29): one value for a share, every holder reading it, and a price that follows it - each claim on a fixt... | 1 |
| [MaterialsCheck.java](MaterialsCheck.md) | 1,541 | 31 | The materials chains, checked: the shared machinery every chain stands on (0.7.112, batch I2; docs/studies/spec-materials.md 2.1, 2.5-2.8 and 4), a... | 16 |
| [MiningCheck.java](MiningCheck.md) | 600 | 10 | Ore and scrap, from the bands they clear in to what an arc furnace makes of scrap and a mine of ore. | 11 |
| [MonetaryCheck.java](MonetaryCheck.md) | 970 | 13 | Money: what a basket costs, what the world charges, and what the rate does. | 3 |
| [MoneyCheck.java](MoneyCheck.md) | 584 | 5 | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. | 0 |
| [MortgageCheck.java](MortgageCheck.md) | 1,245 | 26 | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. | 1 |
| [NationsCheck.java](NationsCheck.md) | 680 | 23 | The nations beyond the city and the path they are on (batch WN1; the project's spec-nations.md 7): Nations' year - output, people, prices and the r... | 0 |
| [NewGameCheck.java](NewGameCheck.md) | 1,412 | 17 | Does "Start New Game" actually start a new game? | 1 |
| [OilCheck.java](OilCheck.md) | 2,351 | 47 | Fuel, from the oil in the ground to the drivers' tanks (0.7.62, batch K; the project's spec-land.md 2.7 and section 3's K entry). | 2 |
| [OilViewCheck.java](OilViewCheck.md) | 706 | 20 | The oil industry screen (0.7.96, batch O12; docs/studies/spec-oil.md 2.13): OilView's figures, words, chart and levers, held to the model's own rea... | 1 |
| [OrderBookCheck.java](OrderBookCheck.md) | 297 | 16 | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. | 0 |
| [OrderSearchCheck.java](OrderSearchCheck.md) | 941 | 23 | The order searches against the countdowns they replaced (0.7.54; the order's size is a formula since BA1). | 3 |
| [OutsideCheck.java](OutsideCheck.md) | 707 | 5 | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). | 0 |
| [PartnerCallsCheck.java](PartnerCallsCheck.md) | 1,320 | 40 | The ships and trains that go to their partners, as a model (batch WN7, 2026-11-01; docs/studies/spec-nations.md 9): PartnerTrade's month tonnes by ... | 0 |
| [PartnersCheck.java](PartnersCheck.md) | 830 | 18 | The model the Trade screen's Partners board, a nation's page and The reserves read (batch WN6, "Nations: the screens"; docs/studies/spec-nations.md... | 0 |
| [PassThroughCheck.java](PassThroughCheck.md) | 755 | 21 | Proves the city's goods are struck at a D$ that follows the D$ slowly (AN1, 2026-10-22): each month the goods' D$ closes EconomyManager.PASS_THROUG... | 0 |
| [PegAnchorCheck.java](PegAnchorCheck.md) | 583 | 21 | The peg's anchor (batch WN4b; ForeignAccounts, THE REGIMES, THE PEG'S ANCHOR): the D$ can be pegged to any of the eight moneys, not only the US$. | 0 |
| [PlanCheck.java](PlanCheck.md) | 4,840 | 170 | The district plan: one street network, + junctions no nearer than eight plots, every building within reach and none on a street or beside a highway... | 4 |
| [PlaytestFixCheck.java](PlaytestFixCheck.md) | 224 | 9 | Verifies the small fixes of a played city (PT1): words and the log, nothing the month does. | 0 |
| [PolicyCheck.java](PolicyCheck.md) | 479 | 5 | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. | 0 |
| [PolicyDatesCheck.java](PolicyDatesCheck.md) | 905 | 10 | Every Policy lever opens on the date Canada first had it, and no lever may open more than the law field's four years early (R0, slice 1: PolicyDate... | 0 |
| [PolicyPreviewCheck.java](PolicyPreviewCheck.md) | 771 | 19 | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. | 0 |
| [PopulationCheck.java](PopulationCheck.md) | 1,426 | 11 | The demographics: do they hold together, and do they move the city the way they were told to? | 0 |
| [PortCheck.java](PortCheck.md) | 1,881 | 43 | The city's ports and their boats (0.7.86, batch O9; docs/studies/spec-oil.md 2.9, 2.10 and 4's PortCheck; runs/research-freight.md 5). | 3 |
| [PriceLevelCheck.java](PriceLevelCheck.md) | 1,093 | 42 | A city founded at 1900's prices is today's real economy with every dollar multiplied by one factor, and this is how we know (EP1, Jerus 2026-10-30:... | 12 |
| [PrizeCheck.java](PrizeCheck.md) | 1,912 | 72 | A prize pays only for a result, a Research Lab goes for the cheapest problem the purse pays for, and the schools' share of a purse buys posts (R4, ... | 1 |
| [QuarryingCheck.java](QuarryingCheck.md) | 638 | 13 | Quarrying, checked (0.7.131, batch I7; docs/studies/spec-materials.md 2.1, 2.3, 2.4, 2.10, 4 and 5's I7 row; the study's - materials-chains-researc... | 1 |
| [RailCheck.java](RailCheck.md) | 753 | 8 | The railway: what it charges, who pays it, and what it does to the band. | 1 |
| [RailDepotCheck.java](RailDepotCheck.md) | 594 | 14 | The railway's stations: the Rail Depot, and the rules that put a station first and track only where it is needed (FR7, 2026-10-18; docs/queue/FR7.m... | 1 |
| [RailDrawCheck.java](RailDrawCheck.md) | 1,909 | 80 | Proves that the railway is drawn as rail, not as a cased road band (0.8.1, batch FR0; docs/studies/spec-freight-and-infrastructure.md 4.3, D15, D18). | 0 |
| [RankingsCheck.java](RankingsCheck.md) | 1,461 | 44 | The Rankings board held to what it promises (batch LB2; the project's spec-leaderboards.md 3, 4, 6 and 9): Rankings' pivot and display, its two boa... | 0 |
| [ReadPathCheck.java](ReadPathCheck.md) | 2,451 | 18 | Reading the city must not change the city. | 1 |
| [RealDollarCheck.java](RealDollarCheck.md) | 707 | 25 | The two US dollars (batch WN4, D29; spec-nations 10 item 29, spec-exchange-rates 3.9): the in-game US$ inflating on the US's path, and the real US$... | 1 |
| [RecipeCheck.java](RecipeCheck.md) | 1,168 | 41 | Recipes (R1c): the ways a building runs, as data on its template, chosen at the city's prices in a city founded the 1900 way, set on the template's... | 0 |
| [RefineryCheck.java](RefineryCheck.md) | 1,539 | 35 | The refinery's units and the flow through them (0.7.80, batch O4; docs/studies/spec-oil.md 2.3, and 4's RefineryCheck), and since 0.7.82 the spread... | 0 |
| [RefineryViewCheck.java](RefineryViewCheck.md) | 632 | 23 | The refinery's pictogram (0.7.95, batch O11; docs/studies/spec-oil.md 2.12): RefineryView's figures and its picture, held to the model's own reads ... | 2 |
| [ReloadCheck.java](ReloadCheck.md) | 552 | 34 | Plays a city, saves it, loads the save into a second game, runs both a month and compares what each would save: a reloaded city has to play its nex... | 2 |
| [ReplayCheck.java](ReplayCheck.md) | 895 | 13 | Replay of a recorded city (batch TR1, 2026-10-31): the recorder, the throwaway copy, the input log, and what recording costs. | 0 |
| [ResearchCheck.java](ResearchCheck.md) | 755 | 20 | The research catalogue is read whole from research.json, every discovery is on history's date with what it needs first, and the reader refuses what... | 0 |
| [ResearchPreviewCheck.java](ResearchPreviewCheck.md) | 726 | 34 | The preview each dial card on Policy > Research shows: what a set of dials would find here over ten years (R3b, slice A: ResearchPreview, and the w... | 1 |
| [ResearchViewCheck.java](ResearchViewCheck.md) | 1,713 | 48 | What the research screens read (R5, slice A: ResearchView, three series in HistorySave, ChartModel.flags(log, more), Recipes.chosen()). | 1 |
| [RestaurantsCheck.java](RestaurantsCheck.md) | 575 | 6 | A meal out is food, and it is the same food. | 0 |
| [RestructureCheck.java](RestructureCheck.md) | 546 | 5 | Buying the city's own debt back, at what the paper is actually worth. | 2 |
| [RoadCheck.java](RoadCheck.md) | 1,210 | 35 | The roads over their lives, and a gravel road paved (0.7.70, batch N1): BuildAdvice's A ROAD OVER ITS LIFE and ConstructionControl's F, held to the... | 0 |
| [RobustnessCheck.java](RobustnessCheck.md) | 432 | 5 | What the game does when something is already broken. | 0 |
| [RunWorksCheck.java](RunWorksCheck.md) | 1,310 | 40 | Highways and rail on the slopes (ET4, slices A and B; docs/studies/spec-earth2-terrain.md 4.3, decision D10): a highway's or a railway line's groun... | 1 |
| [SaveFileCheck.java](SaveFileCheck.md) | 4,056 | 18 | Verifies where saves go and how they are written. | 10 |
| [SaveSlotCheck.java](SaveSlotCheck.md) | 1,830 | 51 | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. | 1 |
| [ScaleCheck.java](ScaleCheck.md) | 2,134 | 57 | A city past 2^31 people (0.7.53): its counts read back whole, its posts, doors and household places add up to the last one, and a save gives them a... | 1 |
| [SectorBooksCheck.java](SectorBooksCheck.md) | 349 | 5 | Plays a city and audits every sector's statements, every month. | 1 |
| [SectorFlowCheck.java](SectorFlowCheck.md) | 308 | 11 | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held t... | 0 |
| [SectorStatementCheck.java](SectorStatementCheck.md) | 939 | 23 | The sector statements (0.7.74, batch S1): SectorStatements' formal statements held to the model's own figures, every sector and the bank, every mon... | 2 |
| [ShortSellingCheck.java](ShortSellingCheck.md) | 1,100 | 46 | Verifies a share's price as its quote, and the short selling that brings a bubble down (EM2, 2026-10-30) - each claim on a fixture that causes it, ... | 0 |
| [SicknessCheck.java](SicknessCheck.md) | 322 | 5 | The long sick: who stays sick, and who it kills. | 0 |
| [SkipReportCheck.java](SkipReportCheck.md) | 323 | 5 | Verifies the fast-forward summary. | 0 |
| [SlopeCheck.java](SlopeCheck.md) | 918 | 32 | Buildable by slope, checked (ET2, slice D; docs/studies/spec-earth2-terrain.md 3.2, 3.4, 4.1, 4.2, 4.4): a building stands on flat and foothill gro... | 1 |
| [StaleCheck.java](StaleCheck.md) | 331 | 6 | The prose still describes the code: the firm half of `tools.Stale`, asserted, and `tools.Names` proved on a fixture. | 0 |
| [SupplierCreditCheck.java](SupplierCreditCheck.md) | 340 | 10 | The grocers' supplier credit (0.7.44): stock bought on the suppliers' credit when the till cannot pay for it, owed for a month, repaid out of the s... | 0 |
| [TimingCheck.java](TimingCheck.md) | 733 | 30 | The harness for soft timing (batch TM1): a timing assertion fails hard, goes SOFT and uncounted under -Dcbs.timing=soft, and nothing else in the su... | 0 |
| [TradeBandCheck.java](TradeBandCheck.md) | 440 | 18 | TradeBand, the model behind the Trade tab's band in legs, held to the study's last bullet of section 6 (docs/studies/spec-freight-and-infrastructur... | 0 |
| [TradeCostCheck.java](TradeCostCheck.md) | 627 | 6 | The wedge between what the world charges and what it pays, and what it is made of. | 0 |
| [TreasuryCheck.java](TreasuryCheck.md) | 1,630 | 35 | Plays a city and audits what the screens say the treasury did. | 2 |
| [TruckCheck.java](TruckCheck.md) | 441 | 10 | Proves that Haulage's loads are drawn as trucks on the map's roads, as a pure function of time, and that drawing them changes nothing in the game (... | 1 |
| [TruckRouteCheck.java](TruckRouteCheck.md) | 1,355 | 50 | Proves the trucks' routes (0.8.8, batch FR5, slice B; docs/studies/spec-freight-and-infrastructure.md 4.7, D21): the ways the map lets a truck driv... | 0 |
| [VanCheck.java](VanCheck.md) | 476 | 5 | The vans: what a sector needs, what it costs it, and what happens while it waits for them. | 0 |
| [VaultMoneysCheck.java](VaultMoneysCheck.md) | 753 | 18 | The vault in eight moneys (batch WN4, slice C; spec-nations 5.2-5.4): ForeignAccounts keeps a balance in each of the eight the IMF's COFER names, t... | 0 |
| [WageLadderCheck.java](WageLadderCheck.md) | 419 | 14 | The wage ladder a city is founded on (R0d): today's PayTier ladder in every city but one founded the 1900 way, which pays 1901's, the census's. | 0 |
| [WantedPostsCheck.java](WantedPostsCheck.md) | 1,007 | 30 | The posts the staffing gate refused bid their band's wage up (LD1; WantedPosts). | 0 |
| [WaterCheck.java](WaterCheck.md) | 529 | 10 | Sanity harness for water production, demand, throttling and billing - and, since 0.7.59, the fresh water limit and the desalination plant (sections... | 0 |
| [WellCheck.java](WellCheck.md) | 1,645 | 31 | The oil wells' lives (0.7.84, batch O7; docs/studies/spec-oil.md 2.6 and 4's WellCheck, the land half) - and since 0.7.91 the sea's (batch O10; spe... | 3 |
| [WoodProductsCheck.java](WoodProductsCheck.md) | 642 | 16 | Wood Products, checked (0.7.140, batch I10; docs/studies/spec-materials.md 2.1, 2.2, 2.3, 2.7, 2.8, 2.12, 4 and 5's I10 row; the study's - material... | 0 |
| [WorldCheck.java](WorldCheck.md) | 782 | 8 | The world a city is founded on: the same seed makes the same world, and the world is the one the design describes - its sea, lakes and forest, its ... | 1 |
| [WorldDataCheck.java](WorldDataCheck.md) | 1,384 | 45 | The nations' world file's reader (batch WN0; the project's spec-nations.md 3.3): WorldData's reading of world-1.bin held to tools/world/worldread.p... | 11 |
| [WorldMarketCheck.java](WorldMarketCheck.md) | 1,019 | 35 | The world market (batch WN3; the project's spec-nations.md 4): the nations' January solve held to the equations it solves, the calibration held to ... | 0 |
| [WorldMeasuresCheck.java](WorldMeasuresCheck.md) | 663 | 25 | The Rankings' real-world file held to what it promises (batch LB1; the project's spec-leaderboards.md 6-8): WorldMeasures' reading of world-2025.js... | 1 |
| [WorldMoneysCheck.java](WorldMoneysCheck.md) | 1,215 | 48 | The world's moneys (batch WN4; spec-exchange-rates 3.7, spec-nations 5.1): WorldMoneys' monthly rate of each money to the US dollar - its parity pa... | 1 |
| [YearBookCheck.java](YearBookCheck.md) | 1,114 | 44 | Proves the year book folds each series the way that series has to be folded, and that the file says so. | 0 |
| [ZoningCheck.java](ZoningCheck.md) | 616 | 17 | Zoning and the forest law, the model (0.7.107, batch Z1): Zoning, and LandManager's ZONES AND THE FOREST LAW, held to the ledger, the treasury, the... | 0 |
| [ZoningViewCheck.java](ZoningViewCheck.md) | 747 | 26 | Policy › Zoning's figures (0.7.109, batch Z2): ZoningView, and the words zoning puts on the screens it does not own - NEEDS YOU's GROUND row, the b... | 0 |

## tools (19 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [ChipRender.java](ChipRender.md) | 101 | 3 | Renders the island chip for review (MT1, BK1-L): the chip as it was (one line, a 3 px dark stroke) and as it is (two lines on a dark rounded plate,... | 0 |
| [CodeMap.java](CodeMap.md) | 177 | 3 | The code map: docs/map/README.md, one row per file, and docs/map/NAME.md for every file, listing its banner sections and every member with the line... | 1 |
| [Dials.java](Dials.md) | 55 | 2 | Every dial in the game on one page: docs/dials.md lists each static final constant in the tree with its value, its line, and the sentence above it. | 3 |
| [HarbourRenders.java](HarbourRenders.md) | 983 | 53 | Renders a Bergen city's harbour for review, headless: HB1's placement before and after, and three ways to draw a terminal's box under the quay wall... | 0 |
| [HarnessMap.java](HarnessMap.md) | 135 | 3 | What every harness asserts, in its own words: docs/harnesses.md. | 1 |
| [JavaScan.java](JavaScan.md) | 666 | 39 | A structural read of one Java source file, without a compiler. | 8 |
| [LandClassRenders.java](LandClassRenders.md) | 175 | 6 | Renders what ET3 says about the classes of ground, for review: the land office's prices and offers by class and note 11 of the sector statements, i... | 0 |
| [LandTable.java](LandTable.md) | 362 | 13 | Makes src/main/resources/world/land-2024.json, the table LandFactors reads: every nation's land a hectare, in US dollars of 2024, from the publishe... | 1 |
| [ManualToMarkdown.java](ManualToMarkdown.md) | 1,522 | 82 | The published manual as two files the repository keeps: docs/manual.md, which GitHub renders when it is clicked, and docs/manual.html, the page its... | 0 |
| [Maps.java](Maps.md) | 30 | 1 | Regenerates every generated document in one go: the code map, the dials, the month order and the harness map. | 0 |
| [MonthOrder.java](MonthOrder.md) | 167 | 6 | The month as a numbered list: docs/month-order.md walks the top-level statements of the methods that make up a month, in order, each with its line ... | 1 |
| [Names.java](Names.md) | 436 | 26 | Lists the names a branch's new prose uses that the tree does not declare. | 1 |
| [ReliefRenders.java](ReliefRenders.md) | 152 | 6 | Renders the map's relief for review: six PNG files drawn by the game's own painters, headless. | 0 |
| [RunRenders.java](RunRenders.md) | 206 | 7 | Renders what ET4 slice C did to the map, for review: where a city's highways and railway run across steep ground, a viaduct, and across very steep ... | 0 |
| [SaveDump.java](SaveDump.md) | 133 | 4 | Looks inside a save without loading the game - or reading the file. | 0 |
| [SlopeRenders.java](SlopeRenders.md) | 275 | 10 | Renders what ET2 did to the map and the founding card, for review: a city played on the real Earth and drawn by the game's own painters, headless, ... | 0 |
| [SourceTree.java](SourceTree.md) | 154 | 13 | The whole source tree, scanned once, and the little that every generator shares: where the sources are, where the documents go, what area a file be... | 9 |
| [Stale.java](Stale.md) | 633 | 27 | Finds the comments and documents that have stopped being true, mechanically. | 2 |
| [Where.java](Where.md) | 71 | 1 | Finds a member by name anywhere in the tree and, if asked, prints it. | 1 |

