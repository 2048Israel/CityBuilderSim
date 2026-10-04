# The code map

Generated 2026-10-04 by `ham.citybuildersim.tools.CodeMap` - do not edit; regenerate with `Regenerate maps.bat` (or `Maps`).

**How to use it.** Open this file first. Every source file is one row here; open `docs/map/NAME.md` for the one you need and it lists that file's banner sections and every method with its line number, so you can read the forty lines that matter instead of the file. `docs/dials.md` has every constant, `docs/month-order.md` the order the month runs in, `docs/harnesses.md` what every check asserts.

**The tree:** 247 files, 226,841 lines, 9,606 methods, 1,563 constants. `GameVersion.VERSION` is "0.7.41", `SAVE_FORMAT` 30.

## model (122 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgeBand.java](AgeBand.md) | 181 | 12 | The six ages of a resident. | 34 |
| [BalanceSheet.java](BalanceSheet.md) | 175 | 26 | A simple balance sheet for one business in the city. | 4 |
| [Bank.java](Bank.md) | 6,488 | 442 | The city's commercial bank: every loan in it, and every default. | 45 |
| [BondMarket.java](BondMarket.md) | 2,015 | 165 | The corporate bond market: every bond the city's businesses have issued, who holds each, the order book each trades on, and the rule each participa... | 17 |
| [BuildAdvice.java](BuildAdvice.md) | 952 | 50 | What the city should build next, and why: the Build tab's categories and the measures its five city categories open on, each measure's figure befor... | 16 |
| [BuildCard.java](BuildCard.md) | 901 | 48 | One build card's figures, for every one of the 73 buildings (0.7.25): what it gives the city and in what unit, its price per unit of that, the scar... | 6 |
| [BuildLog.java](BuildLog.md) | 149 | 9 | What the city has gained, and when. | 4 |
| [BuildingCatalog.java](BuildingCatalog.md) | 365 | 12 | Reads the building definitions out of buildings.json. | 2 |
| [BuildingInstance.java](BuildingInstance.md) | 40 | 4 |  | 1 |
| [BuildingManager.java](BuildingManager.md) | 5,395 | 170 | The city's buildings: the catalogue of templates, the stacks standing and on site with the contracts the builders are working to and who placed the... | 57 |
| [BuildingType.java](BuildingType.md) | 225 | 0 |  | 39 |
| [BuildingsStacks.java](BuildingsStacks.md) | 466 | 42 | One building type in the city: how many stand, how many are on site, and the progress, material and contract its sites carry. | 13 |
| [BuildingsTemplate.java](BuildingsTemplate.md) | 730 | 75 | One kind of building, and what it costs to put up. | 90 |
| [BusinessDebt.java](BusinessDebt.md) | 93 | 12 | Base class for private-sector borrowing. | 9 |
| [BusinessDebtManager.java](BusinessDebtManager.md) | 3,422 | 222 | Private-sector credit. | 21 |
| [BusinessInvestment.java](BusinessInvestment.md) | 1,008 | 44 | Capacity planning for the private sector. | 28 |
| [BusinessLoan.java](BusinessLoan.md) | 72 | 7 | A fixed-term business loan: interest-only each month, principal repaid in full at maturity. | 7 |
| [CapitalFlows.java](CapitalFlows.md) | 609 | 37 | Hot money: what comes in chasing a spread, and what happens when it leaves. | 10 |
| [CareType.java](CareType.md) | 133 | 6 | What a healthcare building actually does. | 24 |
| [CentralBank.java](CentralBank.md) | 882 | 90 | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. | 18 |
| [ChartModel.java](ChartModel.md) | 533 | 41 | What a time chart shows, as numbers: the window of months it looks at and how a drag, a wheel, a range button and the overview move it; the ticks o... | 14 |
| [CityBasket.java](CityBasket.md) | 120 | 2 | What the city eats: the basket per head, struck from the households' own statements, and the file's reference basket for a city that has none yet. | 1 |
| [CityCalendar.java](CityCalendar.md) | 146 | 13 | Turns the month counter into a date a person can hold in their head. | 22 |
| [CityNeeds.java](CityNeeds.md) | 811 | 38 | NEEDS YOU, measured: everything with a lever, each against its own line, in the order a city is built - the list the left panel's Summary prints, t... | 19 |
| [ConstructionControl.java](ConstructionControl.md) | 665 | 53 | The player's hand on the construction queue (0.7.22): the order the city's own sites are served in, the sites it has put on overtime, the orders it... | 9 |
| [Consumption.java](Consumption.md) | 539 | 24 | What a household eats, and what changes it. | 5 |
| [CorporateBond.java](CorporateBond.md) | 232 | 28 | A corporate bond: a sector's debt to investors, issued at par through bookbuilding, paying a fixed coupon every month and its whole face at the end. | 12 |
| [Crime.java](Crime.md) | 525 | 61 | Crime, the police who deter and catch it, and the prisons that hold who they catch. | 16 |
| [Currency.java](Currency.md) | 263 | 21 | What the city's money is called, and how it is written. | 20 |
| [DataSave.java](DataSave.md) | 1,817 | 292 | sections: land and ore, the shedding warning... | 3 |
| [Debt.java](Debt.md) | 517 | 47 | One piece of city paper. | 22 |
| [DebtManager.java](DebtManager.md) | 1,827 | 135 | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of m... | 26 |
| [DebtQuote.java](DebtQuote.md) | 209 | 9 | What a loan would cost, worked out BEFORE the player agrees to it. | 12 |
| [DecisionLog.java](DecisionLog.md) | 206 | 24 | What the player decided, and when: every change of a policy and every spend at scale, one short line each, at the month it was made (0.7.23). | 22 |
| [DemolitionLog.java](DemolitionLog.md) | 142 | 10 | What the city has lost, and when. | 7 |
| [Denomination.java](Denomination.md) | 199 | 12 | The currency's unit, and the power to lop zeros off it. | 3 |
| [EconomyManager.java](EconomyManager.md) | 2,016 | 221 | The private economy: every sector, every market, the credit desk, the tax policy and the national accounts, and the month they run in. | 39 |
| [Education.java](Education.md) | 1,058 | 56 | Who the city teaches, what it costs, and why anybody bothers. | 11 |
| [EducationType.java](EducationType.md) | 191 | 11 | What a school actually teaches. | 24 |
| [Equity.java](Equity.md) | 1,086 | 91 | The share register: who owns the city's companies, what they paid for them, and what the companies pay them back. | 35 |
| [Exchange.java](Exchange.md) | 1,806 | 147 | The stock exchange: one order book per company, where every share that changes hands meets its buyer, and the price is the last trade. | 18 |
| [FamilyModel.java](FamilyModel.md) | 2,211 | 102 | How the city's people are arranged into households, and what each earns. | 20 |
| [FamilyStructure.java](FamilyStructure.md) | 119 | 7 | The shapes a household comes in. | 28 |
| [ForeignAccounts.java](ForeignAccounts.md) | 1,870 | 103 | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. | 27 |
| [Formats.java](Formats.md) | 106 | 9 | The few formats a sector needs to describe itself, without the toolkit. | 32 |
| [Founding.java](Founding.md) | 418 | 31 | How a city was founded: its name, its money's name, and the treasury and the vault the founders left it. | 44 |
| [FundLedger.java](FundLedger.md) | 766 | 92 | The city's fund's cost basis: what each holding cost, by the average-cost method, what its sales and maturities realized, what it has paid in incom... | 10 |
| [FundView.java](FundView.md) | 839 | 38 | The city's fund as the screens read it (0.7.39): every holding with its average cost and P&L, what the fund has made since it began and by kind, it... | 4 |
| [Game.java](Game.md) | 13,944 | 538 | sections: THE FOUNDING RESERVE (2026-09-21), THE FOUNDING RECORD (0.7.10)... | 120 |
| [GameFiles.java](GameFiles.md) | 412 | 35 | Where the game keeps its files, and how it writes them. | 76 |
| [GameLog.java](GameLog.md) | 220 | 12 | Everything the game prints, written somewhere a player can find it. | 8 |
| [GamePrefs.java](GamePrefs.md) | 202 | 17 | How the player likes the window, kept between runs. | 2 |
| [GameVersion.java](GameVersion.md) | 3,039 | 4 | What build this is, and what shape its saves are. | 9 |
| [Good.java](Good.md) | 863 | 18 | A thing that can be made, bought, held, imported and exported. | 71 |
| [GoodsMarket.java](GoodsMarket.md) | 493 | 51 | Where one good clears between whoever makes it and whoever wants it. | 27 |
| [Health.java](Health.md) | 403 | 20 | How much of the workforce is off sick this month. | 14 |
| [Healthcare.java](Healthcare.md) | 932 | 55 | The city's healthcare service: what it costs, what it collects, and what it does with the dead. | 20 |
| [HistoryGrapher.java](HistoryGrapher.md) | 115 | 1 |  | 1 |
| [HistorySave.java](HistorySave.md) | 1,225 | 36 | Every month the city has ever lived, one number at a time. | 24 |
| [Household.java](Household.md) | 1,339 | 115 | Every household of one shape at one pay tier, as one ledger. | 33 |
| [HouseholdAccounts.java](HouseholdAccounts.md) | 1,226 | 92 | The city's residents, treated as one household. | 13 |
| [HouseholdBalance.java](HouseholdBalance.md) | 4,214 | 213 | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. | 36 |
| [Inbox.java](Inbox.md) | 577 | 23 | Everything the city has had to say for itself, newest first. | 3 |
| [InfrastructureManager.java](InfrastructureManager.md) | 876 | 63 | The road network: what the city's buildings demand of it, what it can carry, and what happens when the first number passes the second. | 16 |
| [InterimLoan.java](InterimLoan.md) | 35 | 1 | Interim financing: the bank's loan to a sector in the month it defaulted, for what the month's bills left unpaid after its debt was written down, r... | 4 |
| [Investor.java](Investor.md) | 44 | 6 | Whoever is paying for a building. | 2 |
| [JobType.java](JobType.md) | 20 | 0 |  | 53 |
| [LabourMarket.java](LabourMarket.md) | 800 | 44 | What labour costs, and why it costs that. | 14 |
| [LandManager.java](LandManager.md) | 530 | 47 | The city's land: what it owns, what is built on, and what it sells. | 17 |
| [LandMarket.java](LandMarket.md) | 842 | 29 | The land office's window: nine plots on offer, and what the next one costs. | 7 |
| [LandParcel.java](LandParcel.md) | 132 | 12 | One plot on the market, as the land office lists it. | 9 |
| [LongTermBond.java](LongTermBond.md) | 137 | 12 | A term loan: a coupon every month on the whole face, and the whole face at the end - issued only at the five maturities in MATURITIES (0.7.1). | 10 |
| [LuxuryCounter.java](LuxuryCounter.md) | 155 | 10 | The households' discretionary spending: the boutiques and the restaurants, each striking its price against the queue. | 1 |
| [Markets.java](Markets.md) | 394 | 18 | Every goods market in the city, and the month they clear in. | 19 |
| [MediumTermBond.java](MediumTermBond.md) | 203 | 14 | A serial bond: the workhorse of municipal finance. | 5 |
| [Migration.java](Migration.md) | 1,312 | 56 | Why people move to this city, and the much narrower question of why they leave. | 16 |
| [MoneyAudit.java](MoneyAudit.md) | 1,017 | 24 | Where the money went this month, and whether it all went somewhere. | 21 |
| [Mortgage.java](Mortgage.md) | 458 | 35 | An insured mortgage on a new residential building: a level payment every month over a forty-year amortization, at a rate fixed for a ten-year term ... | 11 |
| [Motoring.java](Motoring.md) | 198 | 10 | The households' car market: the second-hand pass, then the showroom, with the road told what is parked on it. | 1 |
| [NationalAccounts.java](NationalAccounts.md) | 977 | 84 | The city's GDP, measured properly, plus the government's own books. | 21 |
| [Notice.java](Notice.md) | 98 | 15 | One thing the city needs told about, and whether anybody has looked at it. | 7 |
| [Offending.java](Offending.md) | 169 | 5 | Who is at risk of offending, sorted by reason, and the thefts handed to them. | 1 |
| [OrderBook.java](OrderBook.md) | 433 | 50 | A limit-order book for one instrument: buy and sell orders from named participants, each a price and a quantity, matched by price-time priority. | 17 |
| [OrphanHousehold.java](OrphanHousehold.md) | 39 | 10 | Children no family holds, by age band. | 3 |
| [OutwardInvestment.java](OutwardInvestment.md) | 384 | 24 | Outward investment: what the city's businesses do with money the bank will not pay for. | 11 |
| [PayTier.java](PayTier.md) | 125 | 5 | The six pay levels a household can be in. | 44 |
| [PolicyPreview.java](PolicyPreview.md) | 307 | 32 | What a staged set of the Policy tab's dials would do, by the model's own arithmetic (0.7.36): the tax take under another policy, line by line, and ... | 3 |
| [PopulationCohorts.java](PopulationCohorts.md) | 604 | 35 | The city's age pyramid, and since the switch, the city's POPULATION. | 26 |
| [PopulationManager.java](PopulationManager.md) | 1,242 | 61 | The working population: the workforce by skill band, the city's posts and their wages by job type, and who fills which post (fillByBand()). | 27 |
| [PriceIndex.java](PriceIndex.md) | 313 | 17 | What a month costs a household, against what it cost at founding. | 8 |
| [PrisonerHousehold.java](PrisonerHousehold.md) | 79 | 17 | Adults serving a sentence, as one ledger: the prisoners' ledger. | 5 |
| [RetiredHousehold.java](RetiredHousehold.md) | 51 | 6 | A household with nobody of working age in it: a senior or an elder, alone or as a couple. | 3 |
| [Rollover.java](Rollover.md) | 350 | 32 | What falls due next month, refinanced: the treasury's rollover setting and the ledger of the surplus it has netted (0.7.13). | 14 |
| [SafetyType.java](SafetyType.md) | 68 | 4 | What a safety building does: police, or prison cells. | 12 |
| [SalesTaxLedger.java](SalesTaxLedger.md) | 228 | 22 | The month's sales tax, as tax payable less input tax credits. | 5 |
| [SaveHeader.java](SaveHeader.md) | 71 | 12 | Just enough of a save to label it on the slot list. | 6 |
| [Sector.java](Sector.md) | 2,290 | 184 | One business in the city, and the template every sector extends. | 73 |
| [SectorBooks.java](SectorBooks.md) | 450 | 21 | A month of books for every business in the city, and last month's too. | 16 |
| [SectorFlow.java](SectorFlow.md) | 260 | 17 | One business's month as a flow (0.7.30): what went in, what its plant made of it and what held the plant back, and what came out - each good's unit... | 3 |
| [SectorState.java](SectorState.md) | 204 | 6 | One sector, as a save carries it. | 6 |
| [Sectors.java](Sectors.md) | 355 | 36 | Every sector in the city, in one order, by one name. | 52 |
| [ServicesManager.java](ServicesManager.md) | 328 | 25 | / | 7 |
| [ShadowBasket.java](ShadowBasket.md) | 201 | 1 | What a played city WOULD buy, measured against what it spends today. | 0 |
| [ShortTermTBill.java](ShortTermTBill.md) | 127 | 13 | A short-term anticipation note: borrow now, repay one lump, no coupon. | 7 |
| [Sickness.java](Sickness.md) | 357 | 24 | Who has been sick, and for how long - and the ones it kills. | 10 |
| [SimulationEngine.java](SimulationEngine.md) | 216 | 5 | The order the month runs in: the sites advance, the roads, the posts and the wages, the people, then the economy and the services. | 1 |
| [SocialSecurity.java](SocialSecurity.md) | 150 | 9 | Contributions off every wage, and a pension for everyone too old to work. | 5 |
| [StudentHousehold.java](StudentHousehold.md) | 57 | 10 | Full-time students, as one ledger. | 6 |
| [TaxPolicy.java](TaxPolicy.md) | 1,476 | 93 | The city's tax rates - the revenue half of what the player actually decides. | 37 |
| [TimeSkipReport.java](TimeSkipReport.md) | 538 | 63 | What happened while you were not watching. | 4 |
| [Trade.java](Trade.md) | 40 | 4 | One fill: somebody sold somebody some units of a good at a price. | 10 |
| [Traffic.java](Traffic.md) | 60 | 3 | The three things that move, which used to be one number. | 13 |
| [TreasuryFund.java](TreasuryFund.md) | 962 | 138 | The city's fund: the government's holding of its own city's companies and their bonds, bought on the order book by a rule and by the player's hand,... | 18 |
| [TreasuryJournal.java](TreasuryJournal.md) | 252 | 16 | The treasury's journal: every movement of the city's cash that is neither a budget line nor paper raised or repaid, recorded by name as it happens,... | 10 |
| [TreasuryLine.java](TreasuryLine.md) | 141 | 2 | Every kind of payment the treasury makes, and whether it is a promise. | 6 |
| [UnemployedHousehold.java](UnemployedHousehold.md) | 88 | 14 | Adults who are out of work, as one ledger per situation. | 7 |
| [Unemployment.java](Unemployment.md) | 607 | 40 | The people out of work: how many, who they were, what Employment Insurance pays them, and who has lost their home. | 11 |
| [UtilitiesHandler.java](UtilitiesHandler.md) | 532 | 50 | sections: WATER SUPPLY, READ-ONLY ACCESSORS for the utilities screen. printUtilitiesInfo() is... | 10 |
| [WageBand.java](WageBand.md) | 170 | 7 | The four education bands the wage tax is set by. | 25 |
| [WorkingHousehold.java](WorkingHousehold.md) | 50 | 7 | A household with an earner in it, at one pay tier. | 4 |
| [WorldEconomy.java](WorldEconomy.md) | 422 | 18 | The rest of the world, which has its own inflation and did not use to. | 12 |
| [YearBook.java](YearBook.md) | 1,469 | 74 | The run, one line a year - for READING rather than for drawing. | 12 |

## sectors (15 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [Agriculture.java](Agriculture.md) | 393 | 12 | The fields, and what they cost the city in ground. | 4 |
| [Automotive.java](Automotive.md) | 272 | 7 | The automobile industry. | 1 |
| [BusinessServices.java](BusinessServices.md) | 270 | 8 | Somebody else's work, done here, paid for from outside. | 3 |
| [Construction.java](Construction.md) | 859 | 45 | The builders. | 13 |
| [FoodIndustry.java](FoodIndustry.md) | 98 | 6 | The mills and the plants that feed the shops. | 4 |
| [FoodProcessing.java](FoodProcessing.md) | 567 | 12 | The plants between the farm and the shelf. | 2 |
| [HeavyIndustry.java](HeavyIndustry.md) | 119 | 6 | The mills. | 1 |
| [LuxuryRetail.java](LuxuryRetail.md) | 493 | 18 | The luxury shops. | 3 |
| [Manufacturing.java](Manufacturing.md) | 282 | 11 | What the city makes out of its own steel, and ships. | 2 |
| [Materials.java](Materials.md) | 88 | 4 | The materials plant. | 1 |
| [Mining.java](Mining.md) | 153 | 6 | Iron mines. | 4 |
| [Rail.java](Rail.md) | 861 | 36 | The railway. | 6 |
| [RealEstate.java](RealEstate.md) | 929 | 89 | The landlords. | 16 |
| [Restaurants.java](Restaurants.md) | 465 | 19 | The kitchens. | 5 |
| [Retail.java](Retail.md) | 654 | 47 | The shops. | 18 |

## interface (26 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [BankScreen.java](BankScreen.md) | 4,265 | 161 | The bank tab: whether the city's bank is healthy and what it charges, on an Overview - its state in a sentence, its capital in its band, eight figu... | 3 |
| [BuildScreen.java](BuildScreen.md) | 3,817 | 128 | The build tab: the Overview it opens on and the city's five categories opened on their needs (both 0.7.24), the market's nine in their groups (0.7.... | 14 |
| [CityBuilderSim.java](CityBuilderSim.md) | 55 | 2 | The way in. | 0 |
| [ConstructionScreen.java](ConstructionScreen.md) | 1,064 | 39 | The construction page (0.7.22): the builders' gauge, every site with its order, its crews, its time and its money and the player's hand on it - pri... | 1 |
| [FinancesScreen.java](FinancesScreen.md) | 3,414 | 142 | The Finances tab: what the city owes and when it falls due, what its paper costs and who holds it, why its money costs what it does, every piece an... | 5 |
| [FoundingScreen.java](FoundingScreen.md) | 444 | 17 | Found a city: its name, its money, what the founders leave in the treasury and the vault, and the world it is founded into. | 1 |
| [FundScreen.java](FundScreen.md) | 2,324 | 94 | The city's fund on the Finances tab, as a brokerage (0.7.39): its Portfolio - what it is worth, what it has made and on what, every holding with it... | 1 |
| [GovernmentScreen.java](GovernmentScreen.md) | 2,128 | 99 | The government tab: where the city's money came from and went, the road from what it EARNED through the budget's SURPLUS to what the cash BANKED, w... | 2 |
| [HistoryScreen.java](HistoryScreen.md) | 3,050 | 108 | City History: the city as a shape over time. | 8 |
| [Icons.java](Icons.md) | 483 | 6 | The rail's icons, as vector outlines - and since 0.7.24 the Build tab's, one per category, and the few the frame draws (the money block, the "Needs... | 15 |
| [InfrastructureScreen.java](InfrastructureScreen.md) | 1,528 | 56 | The infrastructure tab: the roads, the trams, the railway and the freight - four pages under the five figures the whole tab is about, each page led... | 1 |
| [Ladder.java](Ladder.md) | 354 | 26 | One dial, drawn the one way: a "−" worth one step, a slider that snaps to the step, a "+" worth one step, the reading, and a line under them saying... | 10 |
| [LandScreen.java](LandScreen.md) | 1,579 | 59 | The land office: whether the city has room to grow, and which ground to buy. | 2 |
| [Levers.java](Levers.md) | 144 | 3 | The pieces a policy lever is drawn with: the dial card (dialCard(), 0.7.36) - the dial on a card with what it does beside it, which every dial on t... | 1 |
| [Money.java](Money.md) | 356 | 27 | Every figure the interface prints as money, in one place. | 9 |
| [Palette.java](Palette.md) | 715 | 25 | Every colour, size and spacing this game is allowed to use, in one place. | 22 |
| [PeopleScreen.java](PeopleScreen.md) | 4,285 | 146 | The People tab and its second page, Household money. | 2 |
| [Pieces.java](Pieces.md) | 5,240 | 229 | The small pieces of text and layout every screen is made from: a sentence, an alert, a sub-heading, a grid and its cells, a chip strip, a vitals ba... | 12 |
| [PolicyScreen.java](PolicyScreen.md) | 3,817 | 186 | The Policy tab: every number the city sets for itself - the taxes, the wage floor, the price of money and the promises - each a dial with what it w... | 5 |
| [SectorScreen.java](SectorScreen.md) | 3,221 | 109 | The sector economy: every business in the city as a card, and each one's five pages - what goes in and what comes out, the income statement with la... | 4 |
| [ServicesScreen.java](ServicesScreen.md) | 3,123 | 124 | The services tab: the four systems the city runs for its people - health, education, utilities and safety - each opening on an Overview whose one p... | 4 |
| [Statement.java](Statement.md) | 482 | 23 | The rows a statement is built from: a head, a line, a note, a total, a disclosure that opens, and the two-column book the sector pages and the bank... | 20 |
| [SummaryScreen.java](SummaryScreen.md) | 1,480 | 30 | The left panel's content: the summary and the dashboard - the vitals, the alert block, the six lines that are always worth a glance or the thirteen... | 9 |
| [TimeChart.java](TimeChart.md) | 1,395 | 70 | A chart of months (0.7.23): lines over a window the player drags, wheels and ranges through, years on its axis, recessions named on their bands, th... | 9 |
| [TradeScreen.java](TradeScreen.md) | 2,700 | 117 | The Trade & the world tab: how the city stands against the world this month - what it sells and buys abroad, the month's balance of payments, what ... | 3 |
| [UserInterface.java](UserInterface.md) | 6,020 | 134 | The window: the stage and its theme, the header - the clock and the speed, the money block, five headline tiles, the "Needs you" chip, the rating a... | 20 |

## harnesses (73 files)

| file | lines | methods | what it is | used by |
|---|---:|---:|---|---:|
| [AgricultureCheck.java](AgricultureCheck.md) | 413 | 7 | The ground under the loaf. | 0 |
| [AllChecks.java](AllChecks.md) | 82 | 1 | Runs every harness, one JVM each, and says which failed. | 0 |
| [BankCheck.java](BankCheck.md) | 4,284 | 22 | The commercial bank, and the families it discharges. | 1 |
| [BondCheck.java](BondCheck.md) | 1,463 | 37 | Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it over the bank, who loses what in a default, what the bank charges for co... | 0 |
| [BooksCheck.java](BooksCheck.md) | 241 | 3 | Verifies a sector's income statement and balance sheet, off the template. | 0 |
| [BuildAdviceCheck.java](BuildAdviceCheck.md) | 664 | 28 | The Build overview's advice (0.7.24): BuildAdvice's rule held to NEEDS YOU's list, to the quote, and to the model's own figures, in a played city -... | 0 |
| [BuildCardCheck.java](BuildCardCheck.md) | 989 | 21 | The build card (0.7.25): BuildCard's figures for all 73 buildings held to the model's own reads - the quote, the land, the staffing tests, the mark... | 0 |
| [BuildMenuCheck.java](BuildMenuCheck.md) | 269 | 2 | Verifies that every building in the game can describe itself. | 0 |
| [BuildingDataCheck.java](BuildingDataCheck.md) | 237 | 3 | The migration's safety net: buildings.json must produce exactly the templates the hardcoded definitions did. | 0 |
| [BusinessServicesCheck.java](BusinessServicesCheck.md) | 380 | 5 | The sector whose customer is not in the city. | 0 |
| [CalendarCheck.java](CalendarCheck.md) | 268 | 5 | The date on the status bar, and the log of what the city has finished. | 0 |
| [CapitalFlowCheck.java](CapitalFlowCheck.md) | 487 | 6 | Hot money: does it come for the right reason, and does it leave for one? | 0 |
| [CarCheck.java](CarCheck.md) | 733 | 6 | The cars: who buys one, what it costs them, and what it does to the road. | 0 |
| [CarryTradeCheck.java](CarryTradeCheck.md) | 301 | 5 | The carry trade: the other side of hot money, and the bank's first borrower. | 0 |
| [CentralBankCheck.java](CentralBankCheck.md) | 1,435 | 19 | Proves the central bank's books: that money is made and destroyed on them and nowhere else, every price 0.7.0 hangs off the policy rate, and its tw... | 0 |
| [ChartCheck.java](ChartCheck.md) | 722 | 22 | The charts (0.7.23): the player's decisions as the model records them, and the arithmetic City History's charts are drawn by - the window, the tick... | 0 |
| [ConservationCheck.java](ConservationCheck.md) | 415 | 6 | Nothing is created and nothing is destroyed. | 0 |
| [ConstructionControlCheck.java](ConstructionControlCheck.md) | 826 | 21 | The player's hand on the construction queue (0.7.22): each of ConstructionControl's five rules held to its own arithmetic, in a played city. | 0 |
| [ConsumptionCheck.java](ConsumptionCheck.md) | 342 | 4 | Verifies the consumption model against the two laws it is shaped to obey, and guards the data file against the Java. | 0 |
| [CreditCheck.java](CreditCheck.md) | 2,020 | 18 | Verifies private-sector credit: pricing, origination, rollover, cash conservation. | 0 |
| [CrimeCheck.java](CrimeCheck.md) | 340 | 8 | Crime, the police and the prisons: every claim in claude/crime-has-reasons.md, each with its own cause. | 0 |
| [CurrencyCheck.java](CurrencyCheck.md) | 756 | 19 | Proves the currency under a central bank (0.7.2): the rate answers to the real rate, the vault is spent defending it, and the dial and the carry ap... | 0 |
| [DeathRecordCheck.java](DeathRecordCheck.md) | 206 | 5 | The running totals of the dead, by age and for the orphans and the unhoused. | 0 |
| [DenominationCheck.java](DenominationCheck.md) | 582 | 16 | A currency reform is a change of units, and this is how we know. | 0 |
| [EducationCheck.java](EducationCheck.md) | 1,417 | 11 | Verifies the schools: who gets taught, who is allowed to practise, and what it costs. | 0 |
| [EquityCheck.java](EquityCheck.md) | 380 | 5 | Verifies the share register: who buys, at what price, what they are paid, and that a month with owners in it still adds up. | 0 |
| [ExchangeCheck.java](ExchangeCheck.md) | 982 | 22 | Verifies the exchange on the order book (0.7.12 round 2): what the desk posts and what it is not obliged to take, who trades with whom and at what ... | 1 |
| [FoodProcessingCheck.java](FoodProcessingCheck.md) | 427 | 7 | The third of the shelf that arrives already made. | 0 |
| [ForeignCheck.java](ForeignCheck.md) | 1,647 | 13 | The balance of payments, and whether the boundary it is drawn on is honest. | 1 |
| [ForeignDebtCheck.java](ForeignDebtCheck.md) | 871 | 8 | Borrowing in somebody else's money. | 1 |
| [FundCheck.java](FundCheck.md) | 1,087 | 22 | Proves the city's fund, the bank's rescue for its shares, the preferred a standing bank asks for, and the Insane founding (0.7.14). | 1 |
| [FundLedgerCheck.java](FundLedgerCheck.md) | 924 | 23 | Proves the city's fund's cost basis (FundLedger, 0.7.39): average cost, what sales, maturities, write-downs and a rescue realize, income apart, thr... | 0 |
| [GdpCheck.java](GdpCheck.md) | 409 | 3 | Verifies the national accounts: the identity, growth rates, and the government's books. | 0 |
| [HealthCheck.java](HealthCheck.md) | 1,552 | 5 | Sickness: what it moves, and - much more importantly - what it does not. | 0 |
| [HistoryCheck.java](HistoryCheck.md) | 527 | 6 | Verifies the graph history: recording, alignment, and the round trip. | 0 |
| [HoldersCheck.java](HoldersCheck.md) | 583 | 10 | Proves who holds the city's own paper (0.7.1): that the households buy it at the settle, are paid on it, sell it back, and are paid when it is boug... | 0 |
| [HouseholdCheck.java](HouseholdCheck.md) | 1,624 | 5 | Verifies the residents' books and the demolition log. | 0 |
| [HouseholdMemoryCheck.java](HouseholdMemoryCheck.md) | 278 | 6 | The households remember: the builder keeps what still fits. | 0 |
| [HousingCheck.java](HousingCheck.md) | 636 | 5 | Audits the three subsystems that describe the same housing, every month, and makes them agree. | 0 |
| [InboxCheck.java](InboxCheck.md) | 364 | 5 | Verifies the inbox: raising, refreshing, resolving, culling and the round trip. | 0 |
| [InfrastructureCheck.java](InfrastructureCheck.md) | 998 | 8 | The road network, from the curve up to a city that actually jams. | 0 |
| [InvestCheck.java](InvestCheck.md) | 1,386 | 7 | Verifies the private investment engine: forecasting, the demand tests, and the brake. | 0 |
| [LabourCheck.java](LabourCheck.md) | 1,120 | 14 | Verifies the labour market: who can hold a job, and what it costs. | 0 |
| [LandCheck.java](LandCheck.md) | 1,282 | 13 | Verifies the land ledger: what the city owns, what it can allocate, what it charges, and that the three numbers never drift apart. | 0 |
| [LongPlaytest.java](LongPlaytest.md) | 5,332 | 77 | A city played for four thousand months, the way a person plays. | 11 |
| [ManufacturingCheck.java](ManufacturingCheck.md) | 529 | 7 | The ninth sector: what the city makes out of its own steel, and ships. | 0 |
| [MiningCheck.java](MiningCheck.md) | 609 | 7 | Ore, from the band it clears in to whether it makes steel worth building. | 0 |
| [MonetaryCheck.java](MonetaryCheck.md) | 705 | 10 | Money: what a basket costs, what the world charges, and what the rate does. | 1 |
| [MoneyCheck.java](MoneyCheck.md) | 277 | 5 | Money is conserved: every dollar that leaves a pool arrives in another, or crosses the city's boundary in a way the audit can name. | 0 |
| [MortgageCheck.java](MortgageCheck.md) | 1,194 | 25 | The landlords' insured mortgages (0.7.11): the instrument, the lender's tests, the city's insurance and the bank's book. | 0 |
| [NewGameCheck.java](NewGameCheck.md) | 991 | 13 | Does "Start New Game" actually start a new game? | 1 |
| [OrderBookCheck.java](OrderBookCheck.md) | 297 | 16 | The limit-order book (0.7.12), on its own: the rules any instrument trades by, proved on a book that knows nothing about what it trades. | 0 |
| [OutsideCheck.java](OutsideCheck.md) | 697 | 5 | The people outside the families: the out of work, the students, the unhoused and the orphans (2026-09-11). | 0 |
| [PolicyCheck.java](PolicyCheck.md) | 452 | 5 | The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies. | 0 |
| [PolicyPreviewCheck.java](PolicyPreviewCheck.md) | 470 | 14 | The Policy tab's previews (0.7.36): PolicyPreview and the reads it is made of, held to the month's own books in a played city. | 0 |
| [PopulationCheck.java](PopulationCheck.md) | 1,422 | 11 | The demographics: do they hold together, and do they move the city the way they were told to? | 0 |
| [RailCheck.java](RailCheck.md) | 581 | 8 | The railway: what it charges, who pays it, and what it does to the band. | 0 |
| [ReadPathCheck.java](ReadPathCheck.md) | 1,421 | 7 | Reading the city must not change the city. | 0 |
| [RestaurantsCheck.java](RestaurantsCheck.md) | 550 | 6 | A meal out is food, and it is the same food. | 0 |
| [RestructureCheck.java](RestructureCheck.md) | 523 | 5 | Buying the city's own debt back, at what the paper is actually worth. | 0 |
| [RobustnessCheck.java](RobustnessCheck.md) | 376 | 5 | What the game does when something is already broken. | 0 |
| [SaveFileCheck.java](SaveFileCheck.md) | 1,865 | 7 | Verifies where saves go and how they are written. | 0 |
| [SaveSlotCheck.java](SaveSlotCheck.md) | 264 | 5 | Verifies the slot system: ten saves plus an autosave, the version stamp, and the labels the menu is drawn from. | 0 |
| [SectorBooksCheck.java](SectorBooksCheck.md) | 256 | 4 | Plays a city and audits every sector's statements, every month. | 0 |
| [SectorFlowCheck.java](SectorFlowCheck.md) | 300 | 11 | The flow (0.7.30): SectorFlow's figures - what went into each business, what its plant made of it and what held it back, and what came out - held t... | 0 |
| [SicknessCheck.java](SicknessCheck.md) | 323 | 5 | The long sick: who stays sick, and who it kills. | 0 |
| [SkipReportCheck.java](SkipReportCheck.md) | 323 | 5 | Verifies the fast-forward summary. | 0 |
| [StaleCheck.java](StaleCheck.md) | 204 | 5 | The prose still describes the code: the firm half of `tools.Stale`, asserted. | 0 |
| [TradeCostCheck.java](TradeCostCheck.md) | 534 | 6 | The wedge between what the world charges and what it pays, and what it is made of. | 0 |
| [TreasuryCheck.java](TreasuryCheck.md) | 872 | 15 | Plays a city and audits what the screens say the treasury did. | 0 |
| [VanCheck.java](VanCheck.md) | 309 | 5 | The vans: what a sector needs, what it costs it, and what happens while it waits for them. | 0 |
| [WaterCheck.java](WaterCheck.md) | 186 | 3 | Sanity harness for water production, demand, throttling and billing. | 0 |
| [YearBookCheck.java](YearBookCheck.md) | 1,074 | 43 | Proves the year book folds each series the way that series has to be folded, and that the file says so. | 0 |

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

