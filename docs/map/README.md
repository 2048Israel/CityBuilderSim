# The code map

Generated 2026-09-18 by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).

**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.

**The tree:** 205 files, 123,851 lines, 4,816 methods, 698 constants. `GameVersion.VERSION` is "0.6.7", `SAVE_FORMAT` 27.

## model (101 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgeBand.java](AgeBand.md) | 181 | 12 | The six ages of a resident. | 31 |
| [BalanceSheet.java](BalanceSheet.md) | 175 | 26 | A simple balance sheet for one business in the city. | 4 |
| [Bank.java](Bank.md) | 2,208 | 133 | The city's commercial bank: every loan in it, and every default. | 19 |
| [BuildLog.java](BuildLog.md) | 148 | 9 | What the city has gained, and when. | 4 |
| [BuildingCatalog.java](BuildingCatalog.md) | 365 | 12 | Reads the building definitions out of buildings.json. | 2 |
| [BuildingInstance.java](BuildingInstance.md) | 40 | 4 |  | 1 |
| [BuildingManager.java](BuildingManager.md) | 4,286 | 106 | sections: WATER DRAW, per building, in units of 10,000 gallons/month., HEALTHCARE... | 45 |
| [BuildingType.java](BuildingType.md) | 225 | 0 |  | 35 |
| [BuildingsStacks.java](BuildingsStacks.md) | 261 | 25 |  | 3 |
| [BuildingsTemplate.java](BuildingsTemplate.md) | 730 | 75 | One kind of building, and what it costs to put up. | 71 |
| [BusinessDebt.java](BusinessDebt.md) | 87 | 12 | Base class for private-sector borrowing. | 4 |
| [BusinessDebtManager.java](BusinessDebtManager.md) | 1,015 | 62 | Private-sector credit. | 10 |
| [BusinessInvestment.java](BusinessInvestment.md) | 849 | 41 | Capacity planning for the private sector. | 25 |
| [BusinessLoan.java](BusinessLoan.md) | 72 | 7 | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. | 3 |
| [CapitalFlows.java](CapitalFlows.md) | 593 | 38 | Hot money: what comes in chasing a spread, and what happens when it leaves. | 7 |
| [CareType.java](CareType.md) | 133 | 6 | What a healthcare building actually does. | 14 |
| [CityBasket.java](CityBasket.md) | 120 | 2 | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. | 1 |
| [CityCalendar.java](CityCalendar.md) | 145 | 13 | Turns the month counter into a date a person can hold in their head. | 10 |
| [Consumption.java](Consumption.md) | 539 | 24 | What a household eats, and what changes it. | 5 |
| [Crime.java](Crime.md) | 522 | 61 | Crime, the police who deter and catch it, and the prisons that hold who they catch. | 12 |
| [Currency.java](Currency.md) | 58 | 5 | What the city's money is called, and how it is written. | 7 |
| [DataSave.java](DataSave.md) | 1,301 | 206 | sections: land, ore and the retainer, the shedding warning... | 3 |
| [Debt.java](Debt.md) | 351 | 31 | One piece of city paper. | 13 |
| [DebtManager.java](DebtManager.md) | 1,017 | 80 | sections: THE POLICY RATE, THE RATE THE FOREIGN PAPER IS VALUED AT... | 18 |
| [DebtQuote.java](DebtQuote.md) | 162 | 8 | What a loan would cost, worked out BEFORE the player agrees to it. | 5 |
| [DemolitionLog.java](DemolitionLog.md) | 136 | 10 | What the city has lost, and when. | 6 |
| [Denomination.java](Denomination.md) | 194 | 12 | The currency's unit, and the power to lop zeros off it. | 3 |
| [EconomyManager.java](EconomyManager.md) | 1,347 | 178 | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. | 32 |
| [Education.java](Education.md) | 874 | 43 | Who the city teaches, what it costs, and why anybody bothers. | 8 |
| [EducationType.java](EducationType.md) | 191 | 11 | What a school actually teaches. | 15 |
| [Equity.java](Equity.md) | 795 | 72 | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. | 25 |
| [Exchange.java](Exchange.md) | 1,006 | 73 | The stock exchange: where a share changes hands, and at what price. | 9 |
| [FamilyModel.java](FamilyModel.md) | 2,052 | 97 | How the city's people are arranged into households, and what each earns. | 19 |
| [FamilyStructure.java](FamilyStructure.md) | 119 | 7 | The shapes a household comes in. | 24 |
| [ForeignAccounts.java](ForeignAccounts.md) | 1,103 | 68 | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. | 15 |
| [Formats.java](Formats.md) | 56 | 6 | The few formats a sector needs to describe itself, without the toolkit. | 16 |
| [Game.java](Game.md) | 7,792 | 274 | sections: THE CONSTRUCTION SUBSIDY, STANDING POLICY: NEVER LET THIS SECTOR SHRINK... | 88 |
| [GameFiles.java](GameFiles.md) | 401 | 31 | Where the game keeps its files, and how it writes them. | 61 |
| [GameLog.java](GameLog.md) | 220 | 12 | Everything the game prints, written somewhere a player can find it. | 8 |
| [GamePrefs.java](GamePrefs.md) | 152 | 11 | How the player likes the window, kept between runs. | 1 |
| [GameVersion.java](GameVersion.md) | 599 | 4 | What build this is, and what shape its saves are. | 7 |
| [Good.java](Good.md) | 863 | 18 | A thing that can be made, bought, held, imported and exported. | 61 |
| [GoodsMarket.java](GoodsMarket.md) | 448 | 46 | Where one good clears between whoever makes it and whoever wants it. | 26 |
| [Health.java](Health.md) | 397 | 20 | How much of the workforce is off sick this month. | 12 |
| [Healthcare.java](Healthcare.md) | 703 | 41 | The city's healthcare service: what it costs, what it collects, and what it does with the dead. | 16 |
| [HistoryGrapher.java](HistoryGrapher.md) | 115 | 1 |  | 1 |
| [HistorySave.java](HistorySave.md) | 898 | 27 | Every month the city has ever lived, one number at a time. | 11 |
| [Household.java](Household.md) | 907 | 81 | Every household of one shape at one pay tier, as one ledger. | 22 |
| [HouseholdAccounts.java](HouseholdAccounts.md) | 976 | 76 | The city's residents, treated as one household. | 8 |
| [HouseholdBalance.java](HouseholdBalance.md) | 3,095 | 138 | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. | 23 |
| [Inbox.java](Inbox.md) | 392 | 17 | Everything the city has had to say for itself, newest first. | 3 |
| [InfrastructureManager.java](InfrastructureManager.md) | 686 | 43 | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. | 11 |
| [Investor.java](Investor.md) | 43 | 6 | Whoever is paying for a building. | 1 |
| [JobType.java](JobType.md) | 20 | 0 |  | 41 |
| [LabourMarket.java](LabourMarket.md) | 724 | 36 | What labour costs, and why it costs that. | 10 |
| [LandManager.java](LandManager.md) | 444 | 41 | The city's land: what it owns, what is built on, and what it sells. | 11 |
| [LandMarket.java](LandMarket.md) | 680 | 24 | The land office's window: ten plots on offer, and what the next one costs. | 5 |
| [LandParcel.java](LandParcel.md) | 111 | 11 | One plot on the market, as the land office lists it. | 8 |
| [LongTermBond.java](LongTermBond.md) | 107 | 11 |  | 3 |
| [LuxuryCounter.java](LuxuryCounter.md) | 152 | 10 | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. | 1 |
| [Markets.java](Markets.md) | 338 | 17 | Every goods market in the city, and the month they clear in. | 16 |
| [MediumTermBond.java](MediumTermBond.md) | 203 | 14 | A serial bond: the workhorse of municipal finance. | 3 |
| [Migration.java](Migration.md) | 1,108 | 44 | Why people move to this city, and the much narrower question of why they leave. | 13 |
| [MoneyAudit.java](MoneyAudit.md) | 760 | 23 | Where the money went this month, and whether it all went somewhere. | 10 |
| [Motoring.java](Motoring.md) | 193 | 10 | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it. | 1 |
| [NationalAccounts.java](NationalAccounts.md) | 810 | 71 | The city's GDP, measured properly, plus the government's own books. | 12 |
| [Notice.java](Notice.md) | 98 | 15 | One thing the city needs told about, and whether anybody has looked at it. | 4 |
| [Offending.java](Offending.md) | 169 | 5 | Who is at risk of offending, sorted by reason, and the thefts handed to them. | 1 |
| [OrphanHousehold.java](OrphanHousehold.md) | 39 | 10 | Children no family holds, by age band. | 3 |
| [OutwardInvestment.java](OutwardInvestment.md) | 384 | 24 | Outward investment: what the city's businesses do with money the bank will not pay for. | 7 |
| [PayTier.java](PayTier.md) | 125 | 5 | The six pay levels a household can be in. | 39 |
| [PopulationCohorts.java](PopulationCohorts.md) | 561 | 32 | The city's age pyramid, and since the switch, the city's POPULATION. | 21 |
| [PopulationManager.java](PopulationManager.md) | 938 | 51 | sections: WHO THE WORKFORCE ACTUALLY IS, POPULATION OVERVIEW... | 22 |
| [PriceIndex.java](PriceIndex.md) | 287 | 16 | What a month costs a household, against what it cost at founding. | 4 |
| [PrisonerHousehold.java](PrisonerHousehold.md) | 59 | 15 | Adults serving a sentence, as one ledger: the prisoners' ledger. | 4 |
| [RetiredHousehold.java](RetiredHousehold.md) | 51 | 6 | A household with nobody of working age in it: a senior or an elder, alone or as a couple. | 3 |
| [SafetyType.java](SafetyType.md) | 68 | 4 | What a safety building does: police, or prison cells. | 8 |
| [SalesTaxLedger.java](SalesTaxLedger.md) | 215 | 21 | The month's sales tax, as tax payable less input tax credits. | 5 |
| [SaveHeader.java](SaveHeader.md) | 59 | 11 | Just enough of a save to label it on the slot list. | 5 |
| [Sector.java](Sector.md) | 1,763 | 147 | One business in the city, and the template every sector extends. | 50 |
| [SectorBooks.java](SectorBooks.md) | 392 | 21 | A month of books for every business in the city, and last month's too. | 10 |
| [SectorState.java](SectorState.md) | 185 | 6 | One sector, as a save carries it. | 5 |
| [Sectors.java](Sectors.md) | 261 | 31 | Every sector in the city, in one order, by one name. | 36 |
| [ServicesManager.java](ServicesManager.md) | 320 | 25 | / | 6 |
| [ShadowBasket.java](ShadowBasket.md) | 201 | 1 | What a played city WOULD buy, measured against what it spends today. | 0 |
| [ShortTermTBill.java](ShortTermTBill.md) | 125 | 13 | A short-term anticipation note: borrow now, repay one lump, no coupon. | 3 |
| [Sickness.java](Sickness.md) | 345 | 23 | Who has been sick, and for how long - and the ones it kills. | 9 |
| [SimulationEngine.java](SimulationEngine.md) | 191 | 5 |  | 1 |
| [SocialSecurity.java](SocialSecurity.md) | 150 | 9 | Contributions off every wage, and a pension for everyone too old to work. | 4 |
| [StudentHousehold.java](StudentHousehold.md) | 51 | 10 | Full-time students, as one ledger. | 5 |
| [TaxPolicy.java](TaxPolicy.md) | 667 | 58 | The city's tax rates - the revenue half of what the player actually decides. | 18 |
| [TimeSkipReport.java](TimeSkipReport.md) | 463 | 56 | What happened while you were not watching. | 4 |
| [Trade.java](Trade.md) | 40 | 4 | One fill: somebody sold somebody some units of a good at a price. | 10 |
| [Traffic.java](Traffic.md) | 60 | 3 | The three things that move, which used to be one number. | 12 |
| [UnemployedHousehold.java](UnemployedHousehold.md) | 83 | 14 | Adults who are out of work, as one ledger per situation. | 7 |
| [Unemployment.java](Unemployment.md) | 570 | 37 | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. | 9 |
| [UtilitiesHandler.java](UtilitiesHandler.md) | 487 | 44 | sections: WATER SUPPLY, READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is... | 6 |
| [WageBand.java](WageBand.md) | 152 | 7 | The four education bands the wage tax is set by. | 19 |
| [WorkingHousehold.java](WorkingHousehold.md) | 39 | 6 | A household with an earner in it, at one pay tier. | 2 |
| [WorldEconomy.java](WorldEconomy.md) | 394 | 17 | The rest of the world, which has its own inflation and did not use to. | 7 |
| [YearBook.java](YearBook.md) | 821 | 39 | The run, one line a year - for READING rather than for drawing. | 3 |

## sectors (15 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [Agriculture.java](Agriculture.md) | 393 | 12 | The fields, and what they cost the city in ground. | 4 |
| [Automotive.java](Automotive.md) | 266 | 6 | The automobile industry. | 1 |
| [BusinessServices.java](BusinessServices.md) | 267 | 8 | Somebody else's work, done here, paid for from outside. | 3 |
| [Construction.java](Construction.md) | 400 | 26 | The builders. | 7 |
| [FoodIndustry.java](FoodIndustry.md) | 98 | 6 | The mills and the plants that feed the shops. | 4 |
| [FoodProcessing.java](FoodProcessing.md) | 539 | 12 | The plants between the farm and the shelf. | 2 |
| [HeavyIndustry.java](HeavyIndustry.md) | 105 | 6 | The mills. | 1 |
| [LuxuryRetail.java](LuxuryRetail.md) | 362 | 15 | The luxury shops. | 2 |
| [Manufacturing.java](Manufacturing.md) | 279 | 11 | What the city makes out of its own steel, and ships. | 2 |
| [Materials.java](Materials.md) | 88 | 4 | The materials plant. | 1 |
| [Mining.java](Mining.md) | 139 | 6 | Iron mines. | 4 |
| [Rail.java](Rail.md) | 800 | 32 | The railway. | 5 |
| [RealEstate.java](RealEstate.md) | 800 | 84 | The landlords. | 12 |
| [Restaurants.java](Restaurants.md) | 445 | 18 | The kitchens. | 5 |
| [Retail.java](Retail.md) | 621 | 46 | The shops. | 17 |

## interface (20 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [BankScreen.java](BankScreen.md) | 1,624 | 23 | The bank tab: the gauge, the two limits, another branch, who owes it, where the money comes from, the books, the rescue, and its history. | 1 |
| [BuildScreen.java](BuildScreen.md) | 1,724 | 37 | The build tab: the strip of categories across the top, the constraints bar that says what stops a build, the line that says who builds these, and e... | 2 |
| [CityBuilderSim.java](CityBuilderSim.md) | 55 | 2 | The way in. | 0 |
| [FinancesScreen.java](FinancesScreen.md) | 1,713 | 29 | The Finances tab: the position, the ladder of what the city owes, debt service, home and abroad, your rate taken apart, the book, buying back, and ... | 1 |
| [GovernmentScreen.java](GovernmentScreen.md) | 1,417 | 37 | The government tab: the budget as two rings and a balance, what the treasury actually did against the size of the economy, the two lists - who pays... | 1 |
| [HistoryScreen.java](HistoryScreen.md) | 1,427 | 32 | The Reports tab: the city as a shape over time. | 1 |
| [Icons.java](Icons.md) | 156 | 1 | The rail's icons, as vector outlines. | 1 |
| [LandScreen.java](LandScreen.md) | 403 | 3 | The land office: the city's position across the top, the plots on the market as tiles you can compare - price per square foot against what the offi... | 1 |
| [Levers.java](Levers.md) | 66 | 8 | The pieces a policy lever is drawn with: its head, the would-be rows that show a staged change against today's figure, and the arithmetic of snappi... | 0 |
| [Money.java](Money.md) | 216 | 16 | Every figure the interface prints as money, in one place. | 1 |
| [Palette.java](Palette.md) | 355 | 6 | Every colour, size and spacing this game is allowed to use, in one place. | 16 |
| [PeopleScreen.java](PeopleScreen.md) | 2,518 | 32 | The People tab and the household screen behind it. | 1 |
| [Pieces.java](Pieces.md) | 619 | 26 | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals ba... | 0 |
| [PolicyScreen.java](PolicyScreen.md) | 2,519 | 54 | The policy tab: the four rows of levers - taxes, wages, money, promises - the staged set every dial writes into, the ladder and the batch preview t... | 2 |
| [SectorScreen.java](SectorScreen.md) | 1,299 | 25 | The sector economy: the businesses as a list, and each one's five pages - operations, the income statement with last month beside it, the balance s... | 1 |
| [ServicesScreen.java](ServicesScreen.md) | 2,537 | 49 | The services tab: the systems the city runs and how well each covers - infrastructure (roads, transit, the railway, freight), safety, health, educa... | 1 |
| [Statement.java](Statement.md) | 379 | 17 | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages use. | 15 |
| [SummaryScreen.java](SummaryScreen.md) | 1,608 | 27 | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen... | 1 |
| [TradeScreen.java](TradeScreen.md) | 1,715 | 26 | The Trade & the world tab: the landing with its vitals, the month as a river, the reserves, the currency, what we trade, and the three quiet gauges. | 1 |
| [UserInterface.java](UserInterface.md) | 4,037 | 72 | The window: the stage and its theme, the clock and the speed ladder, the two strips, the rail down the left and the inbox, the left panel and the c... | 14 |

## harnesses (59 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgricultureCheck.java](AgricultureCheck.md) | 413 | 7 | The ground under the loaf. | 0 |
| [AllChecks.java](AllChecks.md) | 81 | 1 | Runs every harness, one JVM each, and says which failed. | 0 |
| [BankCheck.java](BankCheck.md) | 1,095 | 4 | The commercial bank, and the families it discharges. | 0 |
| [BooksCheck.java](BooksCheck.md) | 241 | 3 | Verifies a sector's income statement and balance sheet, off the template. | 0 |
| [BuildMenuCheck.java](BuildMenuCheck.md) | 211 | 2 | Verifies that every building in the game can describe itself. | 0 |
| [BuildingDataCheck.java](BuildingDataCheck.md) | 237 | 3 | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. | 0 |
| [BusinessServicesCheck.java](BusinessServicesCheck.md) | 377 | 5 | The sector whose customer is not in the city. | 0 |
| [CalendarCheck.java](CalendarCheck.md) | 242 | 5 | The date on the status bar, and the log of what the city has finished. | 0 |
| [CapitalFlowCheck.java](CapitalFlowCheck.md) | 483 | 6 | Hot money: does it come for the right reason, and does it leave for one? | 0 |
| [CarCheck.java](CarCheck.md) | 722 | 6 | The cars: who buys one, what it costs them, and what it does to the road. | 0 |
| [CarryTradeCheck.java](CarryTradeCheck.md) | 297 | 5 | The carry trade: the other side of hot money, and the bank's first borrower. | 0 |
| [ConservationCheck.java](ConservationCheck.md) | 403 | 6 | Nothing is created and nothing is destroyed. | 0 |
| [ConsumptionCheck.java](ConsumptionCheck.md) | 342 | 4 | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. | 0 |
| [CreditCheck.java](CreditCheck.md) | 1,022 | 9 | Verifies private-sector credit: pricing, origination, rollover, cash conservation. | 0 |
| [CrimeCheck.java](CrimeCheck.md) | 333 | 8 | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. | 0 |
| [DeathRecordCheck.java](DeathRecordCheck.md) | 206 | 5 | The running totals of the dead, by age and for the orphans and the unhoused. | 0 |
| [DenominationCheck.java](DenominationCheck.md) | 523 | 14 | A currency reform is a change of units, and this is how we know. | 0 |
| [EducationCheck.java](EducationCheck.md) | 685 | 10 | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. | 0 |
| [EquityCheck.java](EquityCheck.md) | 348 | 5 | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. | 0 |
| [ExchangeCheck.java](ExchangeCheck.md) | 686 | 15 | Verifies the exchange: what the desk quotes, who trades with it and why, what a company does with its surplus, and that a city with a market in it ... | 0 |
| [FoodProcessingCheck.java](FoodProcessingCheck.md) | 427 | 7 | The third of the shelf that arrives already made. | 0 |
| [ForeignCheck.java](ForeignCheck.md) | 990 | 9 | The balance of payments, and whether the boundary it is drawn on is honest. | 0 |
| [ForeignDebtCheck.java](ForeignDebtCheck.md) | 589 | 5 | Borrowing in somebody else's money. | 0 |
| [GdpCheck.java](GdpCheck.md) | 405 | 3 | Verifies the national accounts: the identity, growth rates, and the government's books. | 0 |
| [HealthCheck.java](HealthCheck.md) | 927 | 5 | Sickness: what it moves, and - much more importantly - what it does not. | 0 |
| [HistoryCheck.java](HistoryCheck.md) | 301 | 6 | Verifies the graph history: recording, alignment, and the round trip. | 0 |
| [HouseholdCheck.java](HouseholdCheck.md) | 1,299 | 3 | Verifies the residents' books and the demolition log. | 0 |
| [HouseholdMemoryCheck.java](HouseholdMemoryCheck.md) | 278 | 6 | The households remember: the builder keeps what still fits. | 0 |
| [HousingCheck.java](HousingCheck.md) | 636 | 5 | Audits the three subsystems that describe the same housing, every month, and makes them agree. | 0 |
| [InboxCheck.java](InboxCheck.md) | 364 | 5 | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. | 0 |
| [InfrastructureCheck.java](InfrastructureCheck.md) | 802 | 8 | The road network, from the curve up to a city that actually jams. | 0 |
| [InvestCheck.java](InvestCheck.md) | 546 | 6 | Verifies the private investment engine: forecasting, the demand tests, and the brake. | 0 |
| [LabourCheck.java](LabourCheck.md) | 680 | 8 | Verifies the labour market: who can hold a job, and what it costs. | 0 |
| [LandCheck.java](LandCheck.md) | 602 | 3 | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. | 0 |
| [LongPlaytest.java](LongPlaytest.md) | 2,512 | 28 | A city played for four thousand months, the way a person plays. | 4 |
| [ManufacturingCheck.java](ManufacturingCheck.md) | 522 | 7 | The ninth sector: what the city makes out of its own steel, and ships. | 0 |
| [MiningCheck.java](MiningCheck.md) | 609 | 7 | Ore, from the band it clears in to whether it makes steel worth building. | 0 |
| [MonetaryCheck.java](MonetaryCheck.md) | 303 | 4 | Money: what a basket costs, what the world charges, and what the rate does. | 0 |
| [MoneyCheck.java](MoneyCheck.md) | 219 | 5 | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. | 0 |
| [NewGameCheck.java](NewGameCheck.md) | 314 | 5 | Does "Start New Game" actually start a new game? | 1 |
| [OutsideCheck.java](OutsideCheck.md) | 631 | 5 | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). | 0 |
| [PolicyCheck.java](PolicyCheck.md) | 353 | 5 | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. | 0 |
| [PopulationCheck.java](PopulationCheck.md) | 1,338 | 10 | The demographics: do they hold together, and do they move the city the way they were told to? | 0 |
| [RailCheck.java](RailCheck.md) | 499 | 8 | The railway: what it charges, who pays it, and what it does to the band. | 0 |
| [ReadPathCheck.java](ReadPathCheck.md) | 439 | 5 | Reading the city must not change the city. | 0 |
| [RestaurantsCheck.java](RestaurantsCheck.md) | 513 | 6 | A meal out is food, and it is the same food. | 0 |
| [RestructureCheck.java](RestructureCheck.md) | 482 | 5 | Buying the city's own debt back, at what the paper is actually worth. | 0 |
| [RobustnessCheck.java](RobustnessCheck.md) | 376 | 5 | What the game does when something is already broken. | 0 |
| [SaveFileCheck.java](SaveFileCheck.md) | 1,094 | 7 | Verifies where saves go and how they are written. | 0 |
| [SaveSlotCheck.java](SaveSlotCheck.md) | 264 | 5 | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. | 0 |
| [SectorBooksCheck.java](SectorBooksCheck.md) | 256 | 4 | Plays a city and audits every sector's statements, every month. | 0 |
| [SicknessCheck.java](SicknessCheck.md) | 284 | 5 | The long sick: who stays sick, and who it kills. | 0 |
| [SkipReportCheck.java](SkipReportCheck.md) | 229 | 4 | Verifies the fast-forward summary. | 0 |
| [StaleCheck.java](StaleCheck.md) | 204 | 5 | The prose still describes the code: the firm half of `tools.Stale`, asserted. | 0 |
| [TradeCostCheck.java](TradeCostCheck.md) | 534 | 6 | The wedge between what the world charges and what it pays, and what it is made of. | 0 |
| [TreasuryCheck.java](TreasuryCheck.md) | 227 | 2 | Plays a city and audits what the screens say the treasury did. | 0 |
| [VanCheck.java](VanCheck.md) | 305 | 5 | The vans: what a sector needs, what it costs it, and what happens while it waits for them. | 0 |
| [WaterCheck.java](WaterCheck.md) | 180 | 3 | Sanity harness for water production, demand, throttling and billing. | 0 |
| [YearBookCheck.java](YearBookCheck.md) | 563 | 27 | Proves the year book folds each series the way that series has to be folded, and that the file says so. | 0 |

## tools (10 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [CodeMap.java](CodeMap.md) | 177 | 3 | The code map: docs/map/README.md, one row per file, and docs/map/NAME.md for every file, listing its banner sections and every member with the line... | 1 |
| [Dials.java](Dials.md) | 55 | 2 | Every dial in the game on one page: docs/dials.md lists each static final constant in the tree with its value, its line, and the sentence above it. | 1 |
| [HarnessMap.java](HarnessMap.md) | 133 | 3 | What every harness asserts, in its own words: docs/harnesses.md. | 1 |
| [JavaScan.java](JavaScan.md) | 666 | 39 | A structural read of one Java source file, without a compiler. | 7 |
| [Maps.java](Maps.md) | 30 | 1 | Regenerates every generated document in one go: the code map, the dials, the month order and the harness map. | 0 |
| [MonthOrder.java](MonthOrder.md) | 167 | 6 | The month as a numbered list: docs/month-order.md walks the top-level statements of the methods that make up a month, in order, each with its line ... | 1 |
| [SaveDump.java](SaveDump.md) | 133 | 4 | Looks inside a save without loading the game - or reading the file. | 0 |
| [SourceTree.java](SourceTree.md) | 154 | 13 | The whole source tree, scanned once, and the little that every generator shares: where the sources are, where the documents go, what area a file be... | 8 |
| [Stale.java](Stale.md) | 627 | 26 | Finds the comments and documents that have stopped being true, mechanically. | 1 |
| [Where.java](Where.md) | 71 | 1 | Finds a member by name anywhere in the tree and, if asked, prints it. | 0 |

