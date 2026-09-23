# The harnesses

Generated 2026-09-23 by `ham.citybuildersim.tools.HarnessMap` - every labelled assertion in every harness, under the section it prints. Do not edit; regenerate with `Regenerate maps.bat`.

**61 harness files, 4,066 labelled assertions.** AllChecks runs 61 of them.

## Which harnesses read which class

A class nobody reads is a class nothing checks. Mentions by name, so a harness that reaches a class only through another is not counted.

| class | harnesses that mention it |
|---|---|
| [AgeBand](map/AgeBand.md) | CrimeCheck, DeathRecordCheck, HealthCheck, HouseholdMemoryCheck, LabourCheck, LongPlaytest, OutsideCheck, PopulationCheck, SicknessCheck |
| [Agriculture](map/Agriculture.md) | AgricultureCheck, LongPlaytest |
| [Automotive](map/Automotive.md) | **none** |
| [BalanceSheet](map/BalanceSheet.md) | BooksCheck, CreditCheck |
| [Bank](map/Bank.md) | BankCheck, CapitalFlowCheck, CentralBankCheck, EquityCheck, ExchangeCheck, HoldersCheck, LongPlaytest, MonetaryCheck |
| [BuildLog](map/BuildLog.md) | CalendarCheck |
| [BuildingCatalog](map/BuildingCatalog.md) | BuildingDataCheck |
| [BuildingInstance](map/BuildingInstance.md) | **none** |
| [BuildingManager](map/BuildingManager.md) | AgricultureCheck, BooksCheck, BuildMenuCheck, BuildingDataCheck, BusinessServicesCheck, CarCheck, ConservationCheck, CreditCheck, CrimeCheck, DeathRecordCheck, FoodProcessingCheck, HealthCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, InvestCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, PopulationCheck, RailCheck, ReadPathCheck, RestructureCheck, SaveFileCheck, SicknessCheck, TradeCostCheck, VanCheck, WaterCheck |
| [BuildingType](map/BuildingType.md) | BooksCheck, BuildMenuCheck, BuildingDataCheck, CrimeCheck, HouseholdCheck, InfrastructureCheck, InvestCheck, ManufacturingCheck, MiningCheck |
| [BuildingsStacks](map/BuildingsStacks.md) | InvestCheck |
| [BuildingsTemplate](map/BuildingsTemplate.md) | AgricultureCheck, BankCheck, BooksCheck, BuildMenuCheck, BuildingDataCheck, BusinessServicesCheck, CalendarCheck, CapitalFlowCheck, CentralBankCheck, CreditCheck, CrimeCheck, DeathRecordCheck, DenominationCheck, EducationCheck, FoodProcessingCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HoldersCheck, HouseholdCheck, HousingCheck, InfrastructureCheck, InvestCheck, LabourCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MonetaryCheck, MoneyCheck, NewGameCheck, OutsideCheck, PolicyCheck, PopulationCheck, RailCheck, ReadPathCheck, RestaurantsCheck, RobustnessCheck, SaveFileCheck, SaveSlotCheck, TradeCostCheck, TreasuryCheck, WaterCheck, YearBookCheck |
| [BusinessDebt](map/BusinessDebt.md) | **none** |
| [BusinessDebtManager](map/BusinessDebtManager.md) | CreditCheck, LongPlaytest, MoneyCheck |
| [BusinessInvestment](map/BusinessInvestment.md) | AgricultureCheck, BankCheck, BusinessServicesCheck, ConservationCheck, FoodProcessingCheck, InvestCheck, ManufacturingCheck, PolicyCheck, RestaurantsCheck |
| [BusinessLoan](map/BusinessLoan.md) | CreditCheck |
| [BusinessServices](map/BusinessServices.md) | BusinessServicesCheck, LongPlaytest |
| [CapitalFlows](map/CapitalFlows.md) | CapitalFlowCheck, CarryTradeCheck, CurrencyCheck, LongPlaytest, MoneyCheck |
| [CareType](map/CareType.md) | BuildMenuCheck, BuildingDataCheck, HealthCheck, LongPlaytest, ReadPathCheck |
| [CentralBank](map/CentralBank.md) | CentralBankCheck, CurrencyCheck, ForeignCheck, HoldersCheck, LongPlaytest |
| [CityBasket](map/CityBasket.md) | **none** |
| [CityCalendar](map/CityCalendar.md) | CalendarCheck, YearBookCheck |
| [Construction](map/Construction.md) | HousingCheck, InvestCheck, NewGameCheck, RobustnessCheck, SaveFileCheck |
| [Consumption](map/Consumption.md) | ConsumptionCheck, ForeignCheck |
| [Crime](map/Crime.md) | CrimeCheck, LongPlaytest |
| [Currency](map/Currency.md) | LongPlaytest |
| [DataSave](map/DataSave.md) | PopulationCheck, SaveFileCheck |
| [Debt](map/Debt.md) | BankCheck, CentralBankCheck, CreditCheck, ForeignDebtCheck, GdpCheck, HoldersCheck, ReadPathCheck, RestructureCheck, TreasuryCheck |
| [DebtManager](map/DebtManager.md) | BankCheck, CapitalFlowCheck, CarryTradeCheck, CentralBankCheck, CreditCheck, CurrencyCheck, EquityCheck, ExchangeCheck, ForeignDebtCheck, HoldersCheck, LongPlaytest, MonetaryCheck, MoneyCheck, ReadPathCheck |
| [DebtQuote](map/DebtQuote.md) | CreditCheck, ForeignDebtCheck |
| [DemolitionLog](map/DemolitionLog.md) | CalendarCheck, HouseholdCheck |
| [Denomination](map/Denomination.md) | DenominationCheck |
| [EconomyManager](map/EconomyManager.md) | AgricultureCheck, CapitalFlowCheck, ConservationCheck, CreditCheck, CrimeCheck, EducationCheck, HealthCheck, HousingCheck, InvestCheck, LongPlaytest, NewGameCheck, OutsideCheck, PopulationCheck, ReadPathCheck, SaveFileCheck |
| [Education](map/Education.md) | EducationCheck, LongPlaytest |
| [EducationType](map/EducationType.md) | BuildingDataCheck, EducationCheck, LongPlaytest, ReadPathCheck |
| [Equity](map/Equity.md) | AgricultureCheck, BankCheck, BusinessServicesCheck, CarCheck, CreditCheck, DenominationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, HistoryCheck, HouseholdCheck, LongPlaytest, ManufacturingCheck, RestaurantsCheck |
| [Exchange](map/Exchange.md) | BankCheck, ExchangeCheck, LongPlaytest |
| [FamilyModel](map/FamilyModel.md) | CrimeCheck, EducationCheck, HealthCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, LongPlaytest, OutsideCheck, PopulationCheck |
| [FamilyStructure](map/FamilyStructure.md) | BankCheck, CarCheck, ConsumptionCheck, CrimeCheck, EducationCheck, EquityCheck, ExchangeCheck, HealthCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, LongPlaytest, OutsideCheck, PopulationCheck, RestaurantsCheck |
| [FoodIndustry](map/FoodIndustry.md) | BooksCheck, CreditCheck, WaterCheck |
| [FoodProcessing](map/FoodProcessing.md) | FoodProcessingCheck |
| [ForeignAccounts](map/ForeignAccounts.md) | CapitalFlowCheck, CarryTradeCheck, CurrencyCheck, ForeignCheck, ForeignDebtCheck, LabourCheck, LandCheck, LongPlaytest, MonetaryCheck, NewGameCheck, SaveFileCheck |
| [Formats](map/Formats.md) | AgricultureCheck, FoodProcessingCheck, TradeCostCheck |
| [Game](map/Game.md) | AgricultureCheck, BankCheck, BuildMenuCheck, BusinessServicesCheck, CalendarCheck, CapitalFlowCheck, CarCheck, CarryTradeCheck, CentralBankCheck, ConservationCheck, CreditCheck, CrimeCheck, CurrencyCheck, DeathRecordCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HistoryCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, InboxCheck, InfrastructureCheck, InvestCheck, LabourCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MonetaryCheck, MoneyCheck, NewGameCheck, OutsideCheck, PolicyCheck, PopulationCheck, RailCheck, ReadPathCheck, RestaurantsCheck, RestructureCheck, RobustnessCheck, SaveFileCheck, SaveSlotCheck, SectorBooksCheck, SicknessCheck, TradeCostCheck, TreasuryCheck, VanCheck, YearBookCheck |
| [GameFiles](map/GameFiles.md) | AgricultureCheck, BankCheck, BuildMenuCheck, BusinessServicesCheck, CalendarCheck, CapitalFlowCheck, CarCheck, CarryTradeCheck, CentralBankCheck, ConservationCheck, CreditCheck, CrimeCheck, CurrencyCheck, DeathRecordCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HistoryCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, InboxCheck, InfrastructureCheck, InvestCheck, LabourCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MonetaryCheck, MoneyCheck, NewGameCheck, OutsideCheck, PolicyCheck, PopulationCheck, RailCheck, ReadPathCheck, RestaurantsCheck, RestructureCheck, RobustnessCheck, SaveFileCheck, SaveSlotCheck, SectorBooksCheck, SicknessCheck, TradeCostCheck, TreasuryCheck, VanCheck, YearBookCheck |
| [GameLog](map/GameLog.md) | RobustnessCheck |
| [GamePrefs](map/GamePrefs.md) | **none** |
| [GameVersion](map/GameVersion.md) | RobustnessCheck, SaveSlotCheck |
| [Good](map/Good.md) | AgricultureCheck, BooksCheck, BusinessServicesCheck, CarCheck, ConservationCheck, ConsumptionCheck, CreditCheck, DenominationCheck, FoodProcessingCheck, ForeignCheck, HealthCheck, InfrastructureCheck, InvestCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, RailCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck, TradeCostCheck, VanCheck |
| [GoodsMarket](map/GoodsMarket.md) | AgricultureCheck, BusinessServicesCheck, FoodProcessingCheck, ForeignCheck, InfrastructureCheck, InvestCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, RailCheck, ReadPathCheck, VanCheck |
| [Health](map/Health.md) | HealthCheck, LongPlaytest, OutsideCheck, SicknessCheck |
| [Healthcare](map/Healthcare.md) | HealthCheck, LongPlaytest, OutsideCheck, PopulationCheck, SicknessCheck |
| [HeavyIndustry](map/HeavyIndustry.md) | **none** |
| [HistoryGrapher](map/HistoryGrapher.md) | **none** |
| [HistorySave](map/HistorySave.md) | DeathRecordCheck, HistoryCheck, HouseholdMemoryCheck, SaveFileCheck, YearBookCheck |
| [Household](map/Household.md) | BusinessServicesCheck, CarCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, HealthCheck, HoldersCheck, HouseholdCheck, LongPlaytest, OutsideCheck, RestaurantsCheck, SaveFileCheck |
| [HouseholdAccounts](map/HouseholdAccounts.md) | HealthCheck, HouseholdCheck, HousingCheck, OutsideCheck |
| [HouseholdBalance](map/HouseholdBalance.md) | BankCheck, BusinessServicesCheck, CarCheck, CentralBankCheck, CrimeCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, HealthCheck, HoldersCheck, HouseholdCheck, LongPlaytest, OutsideCheck, RestaurantsCheck |
| [Inbox](map/Inbox.md) | InboxCheck |
| [InfrastructureManager](map/InfrastructureManager.md) | CarCheck, InfrastructureCheck, LongPlaytest, RailCheck, TradeCostCheck |
| [Investor](map/Investor.md) | **none** |
| [JobType](map/JobType.md) | AgricultureCheck, BuildMenuCheck, BuildingDataCheck, BusinessServicesCheck, CrimeCheck, EducationCheck, FoodProcessingCheck, HealthCheck, HouseholdCheck, LabourCheck, LongPlaytest, ManufacturingCheck, MiningCheck, OutsideCheck, PolicyCheck, PopulationCheck, RestaurantsCheck |
| [LabourMarket](map/LabourMarket.md) | LabourCheck, LongPlaytest |
| [LandManager](map/LandManager.md) | LandCheck, LongPlaytest, MiningCheck, ReadPathCheck |
| [LandMarket](map/LandMarket.md) | LandCheck, LongPlaytest, MoneyCheck |
| [LandParcel](map/LandParcel.md) | LandCheck, LongPlaytest, MiningCheck, MoneyCheck, TreasuryCheck |
| [LongTermBond](map/LongTermBond.md) | CreditCheck, ReadPathCheck, RestructureCheck |
| [LuxuryCounter](map/LuxuryCounter.md) | **none** |
| [LuxuryRetail](map/LuxuryRetail.md) | **none** |
| [Manufacturing](map/Manufacturing.md) | ManufacturingCheck |
| [Markets](map/Markets.md) | BooksCheck, FoodProcessingCheck, InfrastructureCheck, MiningCheck, RailCheck |
| [Materials](map/Materials.md) | **none** |
| [MediumTermBond](map/MediumTermBond.md) | BankCheck, RestructureCheck |
| [Migration](map/Migration.md) | CrimeCheck, EducationCheck, HealthCheck, LabourCheck, LongPlaytest, PopulationCheck |
| [Mining](map/Mining.md) | MiningCheck, ReadPathCheck |
| [MoneyAudit](map/MoneyAudit.md) | BankCheck, CapitalFlowCheck, CarryTradeCheck, CentralBankCheck, CurrencyCheck, ForeignCheck, ForeignDebtCheck, HoldersCheck, LandCheck, LongPlaytest, MoneyCheck, OutsideCheck |
| [Motoring](map/Motoring.md) | **none** |
| [NationalAccounts](map/NationalAccounts.md) | EducationCheck, GdpCheck, HealthCheck, HistoryCheck, HouseholdCheck, LongPlaytest, NewGameCheck, TreasuryCheck |
| [Notice](map/Notice.md) | InboxCheck |
| [Offending](map/Offending.md) | **none** |
| [OrphanHousehold](map/OrphanHousehold.md) | OutsideCheck |
| [OutwardInvestment](map/OutwardInvestment.md) | CapitalFlowCheck, ExchangeCheck, HoldersCheck, LongPlaytest |
| [PayTier](map/PayTier.md) | AgricultureCheck, BankCheck, BusinessServicesCheck, CarCheck, EducationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, HealthCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, LabourCheck, LongPlaytest, ManufacturingCheck, OutsideCheck, PopulationCheck, RestaurantsCheck, TradeCostCheck |
| [PopulationCohorts](map/PopulationCohorts.md) | CrimeCheck, DeathRecordCheck, HealthCheck, HouseholdMemoryCheck, OutsideCheck, PopulationCheck, SicknessCheck |
| [PopulationManager](map/PopulationManager.md) | BusinessServicesCheck, CrimeCheck, EducationCheck, HealthCheck, LabourCheck, LongPlaytest, ManufacturingCheck, NewGameCheck, OutsideCheck, PopulationCheck, SaveFileCheck, YearBookCheck |
| [PriceIndex](map/PriceIndex.md) | CurrencyCheck, LabourCheck, LongPlaytest, MonetaryCheck |
| [PrisonerHousehold](map/PrisonerHousehold.md) | CrimeCheck, EducationCheck |
| [Rail](map/Rail.md) | RailCheck |
| [RealEstate](map/RealEstate.md) | HouseholdCheck, HousingCheck, LabourCheck, LongPlaytest, NewGameCheck, ReadPathCheck |
| [Restaurants](map/Restaurants.md) | LongPlaytest, RestaurantsCheck |
| [Retail](map/Retail.md) | ForeignCheck, HealthCheck, InfrastructureCheck, InvestCheck, LongPlaytest, MonetaryCheck, NewGameCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck, WaterCheck |
| [RetiredHousehold](map/RetiredHousehold.md) | HouseholdCheck |
| [SafetyType](map/SafetyType.md) | BuildingDataCheck, CrimeCheck |
| [SalesTaxLedger](map/SalesTaxLedger.md) | PolicyCheck |
| [SaveHeader](map/SaveHeader.md) | SaveSlotCheck |
| [Sector](map/Sector.md) | AgricultureCheck, BooksCheck, ConservationCheck, CreditCheck, DenominationCheck, ForeignCheck, HistoryCheck, HousingCheck, InfrastructureCheck, InvestCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, PolicyCheck, RailCheck, ReadPathCheck, SaveFileCheck, SectorBooksCheck, VanCheck, WaterCheck |
| [SectorBooks](map/SectorBooks.md) | BusinessServicesCheck, CrimeCheck, HistoryCheck, LongPlaytest, ManufacturingCheck, SectorBooksCheck |
| [SectorState](map/SectorState.md) | VanCheck |
| [Sectors](map/Sectors.md) | AgricultureCheck, BankCheck, BooksCheck, BusinessServicesCheck, CapitalFlowCheck, ConservationCheck, CreditCheck, DenominationCheck, EquityCheck, ExchangeCheck, ForeignCheck, HouseholdCheck, HousingCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MoneyCheck, PolicyCheck, RailCheck, RestaurantsCheck, SaveFileCheck, SectorBooksCheck, TreasuryCheck |
| [ServicesManager](map/ServicesManager.md) | NewGameCheck, ReadPathCheck, WaterCheck |
| [ShadowBasket](map/ShadowBasket.md) | **none** |
| [ShortTermTBill](map/ShortTermTBill.md) | BankCheck, CentralBankCheck, RestructureCheck, TreasuryCheck |
| [Sickness](map/Sickness.md) | LongPlaytest, PopulationCheck, SicknessCheck |
| [SimulationEngine](map/SimulationEngine.md) | **none** |
| [SocialSecurity](map/SocialSecurity.md) | HouseholdCheck |
| [StudentHousehold](map/StudentHousehold.md) | EducationCheck, HouseholdCheck, OutsideCheck |
| [TaxPolicy](map/TaxPolicy.md) | AgricultureCheck, EducationCheck, FoodProcessingCheck, HealthCheck, HouseholdCheck, InfrastructureCheck, LongPlaytest, MoneyCheck, PolicyCheck, ReadPathCheck, SaveFileCheck, TradeCostCheck |
| [TimeSkipReport](map/TimeSkipReport.md) | HealthCheck, SkipReportCheck |
| [Trade](map/Trade.md) | BooksCheck, CreditCheck |
| [Traffic](map/Traffic.md) | CarCheck, InfrastructureCheck, LongPlaytest, RailCheck, TradeCostCheck |
| [TreasuryJournal](map/TreasuryJournal.md) | CentralBankCheck, EducationCheck, LandCheck, SaveFileCheck, TreasuryCheck |
| [TreasuryLine](map/TreasuryLine.md) | CentralBankCheck, LongPlaytest |
| [UnemployedHousehold](map/UnemployedHousehold.md) | HouseholdCheck, LongPlaytest, OutsideCheck |
| [Unemployment](map/Unemployment.md) | DeathRecordCheck, LongPlaytest, OutsideCheck |
| [UtilitiesHandler](map/UtilitiesHandler.md) | ConservationCheck, WaterCheck |
| [WageBand](map/WageBand.md) | EducationCheck, HouseholdCheck, LabourCheck, LongPlaytest, OutsideCheck, PolicyCheck, SaveFileCheck |
| [WorkingHousehold](map/WorkingHousehold.md) | HoldersCheck, HouseholdCheck |
| [WorldEconomy](map/WorldEconomy.md) | LongPlaytest, MonetaryCheck |
| [YearBook](map/YearBook.md) | HistoryCheck, YearBookCheck |

## AgricultureCheck.java - 43 labelled assertions

> The ground under the loaf.
> 
> WHAT THIS HAS TO PROVE. Not that the city got bigger - it did not, and the
> measurement is in claude/farms.md where a number that will drift belongs.
> What is checkable is whether the tenth sector does the four things it was
> built to do:
> 
>   1. the mills BUY their raw material now, and the chain is four links deep
>   2. crops clear in a band with a real import ceiling, so a city with no
>      fields eats anyway and pays for the privilege
>   3. a farm is ground and almost nothing else - and the clock that runs on
>      that ground is what decides fields against glass
> ...

- **L75 1. the good, and the chain it closes**
  - L78 crops can be imported - a city with no fields still eats
  - L80 ...and exported, so a farming city can ship its surplus
  - L82 ...and stored, because a harvest keeps and that is what a silo is
  - L84 crops clear on the band
  - L85 a tonne costs more to bring in than a farm gets shipping one out
  - L87 crops are sold by the tonne
  - L98 the Industrial Bakery buys crops, which it never did before
  - L100 ...and so does the Bakery
  - L111 a tonne of crops comes out as 52.5% of its own mass, baked, at the big oven
  - L114 ...and at the small one
  - L128 the mills declare crops as an input on the sector, not just the template
  - L130 ...and the fields declare them as an output
- **L166 2. the band, and the city with no fields**
  - L171 nobody growing any - the mills pay what the world charges
  - L174 nobody buying any - the fields get what the ship pays
  - L177 ...and a matched pair splits the difference
- **L182 3. a farm is ground and almost nothing else**
  - L236 a field takes more ground a post than any factory in the game
  - L246 ...and far more ground per dollar of harvest than glass does
  - L249 glass pays for its density in power
  - L252 a field's wage bill is small against what it sells
  - L272 dear ground makes a field cost many times what glass costs
  - L274 ...while the glass is still mostly the glass
- **L276 4. the dial, and what reads it**
  - L279 full relief by default, which is what real jurisdictions do
  - L302 six grain farms is 3,000 tonnes of crop a month
  - L304 the fields sold something
  - L305 the mills bought crops
  - L306 ...from the fields, at home
  - L307 ...and the fields' own crop line shows it
  - L319 the fields are standing on real money
  - L331 full relief takes the ground off the roll and leaves the barns
  - L333 no relief puts all of it back
  - L334 and it touches nobody else's assessment
  - L336 the share on the roll is one for everybody else
  - L339 ...and one less the relief for the fields
  - L342 the dial cannot be set past full relief
  - L344 ...nor below none
  - L355 the bill the fields are actually charged follows the dial
  - L363 the investment desk prices a prospective farm at the relieved rate too
- **L367 and nobody breaks ground while somebody sleeps outside**
  - L370 the sector answers with a reason either way
  - L374 Agriculture is the tenth sector
  - L376 ...and the share register is the sectors and the bank
  - L383 a reloaded city has the same fields
  - L386 ...the same cash
  - L389 ...and the same farmland dial, which is a policy and has to survive

## BankCheck.java - 126 labelled assertions

> The commercial bank, and the families it discharges.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
> Before this went in there were three lenders in the game and none of them was
> anybody: a sector borrowed from BusinessDebtManager, the city sold bonds to a
> market, and a family short of the shop borrowed from nothing at all. Money
> arrived from outside the city and interest disappeared out of it.
> 
> So the questions here are not "does the class compile". They are:
> 
>   1. Does the price of credit actually depend on the bank? A premium that no
> ...

- **L89 1. the two limits**
  - L101 a well capitalised bank is limited by what it can gather
  - L107 a bank with no capital can lend nothing, however many branches
  - L111 ...and capital is what lets it lend, at the regulator's ratio
  - L114 ...so the two limits are genuinely different questions
  - L117 a bank that has lost more than it owns is insolvent
- **L120 1b. WHAT TO PAY SAVERS IS A DECISION**
- **L135 what to pay savers is a decision**
  - L147 a bank whose margin is gone pays its savers nothing
  - L159 fixture: the idle capital earned a placement
  - L161 ...and one that earned pays the baseline share of what it earned
  - L163 ...which is a rate on the whole deposit book, derived not set
  - L165 ...and that rate is the payout over the deposits
  - L197 fixture: it really is lent out
  - L198 a bank that is lent out bids above its baseline for deposits
  - L200 ...and never past the interest it earned
  - L227 fixture: capital really is what binds it
  - L229 a bank short of CAPITAL does not bid for deposits it may not lend
  - L250 fixture: its payout over its deposits is more than it charges
  - L252 the first month's reported deposit rate is at most the lending rate
  - L254 ...and the month says the quote was capped
  - L255 ...while what it pays its savers is unchanged: the baseline share of what it earned
  - L257 a bank whose payout is under what it charges quotes the payout itself
- **L261 ...and it does not open counters either**
  - L295 fixture: the month really did close on a loss
  - L297 a bank whose counters do not pay for themselves wants no more
  - L299 ...so it does not ask for one
  - L314 ...where a counter carrying a real book is worth opening
- **L317 2. the price of strain**
  - L332 a bank lending half of what it can charges nothing
  - L336 ...and starts charging the moment it is past EASY_STRAIN
  - L340 a bank lending well past itself charges the cap
  - L354 the premium never falls as the book grows
  - L359 a city with no bank and no debt is charged nothing
  - L362 ...but the moment it borrows, it pays the full premium
- **L365 3. the premium reaches the borrower**
  - L376 the premium lands on the quote, point for point
  - L391 the standing rate is the quote for nothing more
- **L394 4. a real city, and its money**
  - L447 the fixture actually built a bank
  - L448 ...and somebody in it has actually borrowed
  - L462 fixture: somebody abroad has actually borrowed
  - L464 the bank's book is every loan in the city, plus what left it
  - L468 ...and the carry book IS the stock that owns it
  - L471 the sector book IS the business lender's principal
  - L476 the city book IS the treasury's principal at home
  - L478 the household book IS what the families owe
  - L488 the bank is one of the audited pools
  - L490 ...and the month still balances to the cent
- **L493 5. the save carries the bank's cash**
  - L498 the fixture's bank is actually holding something
  - L512 the bank's cash reloads exactly
  - L513 ...so the city's whole money supply does too
  - L543 every tier's savings reload to the cent
  - L545 ...and so does what the bank is charging for money
  - L548 ...and the standing rate the debt screen shows
- **L552 6. a family that cannot carry it**
  - L607 a household that cannot pay its rent does go broke
  - L609 ...and it takes months, not one bad month
  - L611 the write-off is real money, not a rounding
  - L613 ...and the discharged tier is locked out afterwards
  - L615 ...for the full term
  - L617 some of them leave the city
  - L618 ...but not all of them
  - L630 debt is bounded by the ceiling, not compounding past it
  - L663 the fixture did reach a lockout - or this proves nothing
  - L665 a locked-out household is lent nothing at all
- **L667 7. and the city opens its own**
  - L804 a city with no bank at all does something about it
  - L805 ...and says so in words about credit, not about shops
  - L896 a city that can afford a branch opens one, unprompted
  - L898 ...and stops paying the punitive premium once it has
  - L908 a bank with room to spare does not ask for another counter
  - L910 ...and prices one at nothing, so nobody would build it
  - L914 a bank lent out past itself does ask
  - L915 ...and a branch is worth real money to it
- **L918 8. the accounting identities, on a played city**
  - L1027 the fixture's bank is actually running a book
  - L1029 ASSETS = LIABILITIES + EQUITY, every month
  - L1030 ...and equity moves by net income and capital, and nothing else
  - L1063 the city takes exactly its share of what the bank made
  - L1065 ...and the figure the treasury books is the figure the bank paid
  - L1074 a profitable bank hands over its share
  - L1075 ...out of its own cash
  - L1076 ...and reports it
  - L1077 ...which comes off the month's net income
  - L1083 a bank that lost money is not paid a refund
  - L1087 it pays its savers something
  - L1108 ...and never more than it earned
  - L1110 ...and its staff are on its own books, not the shops'
- **L1113 8b. the trading desk's statement foots**
  - L1161 fixture: the desk traded
  - L1162 the re-mark is the inventory at the closing quote, from nothing
  - L1164 ...and the desk's total is what it paid against that re-mark
  - L1166 the opened lines and the re-mark sum to the total, exactly
  - L1172 the re-mark is cleared with the trading result at the top of a month
  - L1184 fixture: the played city's desk actually traded
  - L1185 ...and its opened lines and re-mark summed to its total every month
- **L1188 9. capital is the constraint, and it can run out**
  - L1194 a bank can lend its capital over the regulator's ratio
  - L1200 ...and does
  - L1207 a bank that loses more than it owns is insolvent
  - L1208 ...and can lend nothing at all
  - L1209 ...so every borrower in the city pays the full premium
  - L1211 ...and it says what it would take to fix
  - L1215 recapitalised, it is solvent again
  - L1216 ...and lending again
  - L1217 ...and above the ratio it is required to hold, by at least the exit buffer
  - L1219 ...and holding at least what one branch is capitalised with
  - L1221 ...which is exactly what it was asked for, and not a dollar more
  - L1237 fixture: a bank that lost its whole book has failed
  - L1238 fixture: ...and has no book left to strike a ratio on
  - L1239 a failed bank with no book is still asked for one branch's capital
  - L1242 ...and can lend again once it has it
- **L1305 and the bank pays for the city's paper**
  - L1331 fixture: the city has a working bank and owes nothing yet
  - L1355 fixture: the city issued a note and a serial bond, below par
  - L1357 between the presses the bank has not paid yet
  - L1358 ...and owes the treasury exactly what it received
  - L1371 a city saved between the issue and the settle still owes its bank's payment
  - L1384 fixture: the households held their paper through the settle - the desk bought none
  - L1411 the bank's cash fell by exactly what the treasury received less what the households
  - L1413 ...which is the figure the settle reports
  - L1414 ...and between them the holders paid every dollar the treasury received
  - L1417 ...its book rose by its own face, less what the city repaid it this month
  - L1419 ...and its equity by net income and capital, and nothing else - the discount, not the face
  - L1422 ...and once it has paid it owes nothing for the paper
  - L1426 MoneyAudit still closes, on the month the bank paid
  - L1428 the reloaded city's bank paid the same, at its settle
  - L1430 ...and its households the same
  - L1431 ...and its month closes too
  - L1439 fixture: the note and the serial are on the books
  - L1441 each piece carries its own discount, which together is face less what it raised
  - L1447 the bank's interest in the settle month carries one month of its share of the discount,
  - L1449 ...which is well short of the whole (the old rule booked all of it)
  - L1451 the rest sits against its book, unearned, so its equity did not move for the discount
  - L1454 ...and it is exactly the bank's share less what accreted
  - L1468 over the note's life its accretion adds up to its face less what it raised
  - L1470 ...and nothing of it is left unearned once it is repaid
  - L1471 ...and it is off the books

## BooksCheck.java - 44 labelled assertions

> Verifies a sector's income statement and balance sheet, off the template.
> Not part of the game.
> 
> REWRITTEN FOR THE SECTOR TEMPLATE (2026-09-11). This used to drive an
> IndustrialHandler by hand - setFoodDemand, setFoodPrice, computeMonthlyReport
> - and read its report lines back. There is no handler now: a sector's month
> is a LEDGER of trades struck into a STATEMENT at the top of the next month
> (see Sector.strike and Sector.bank), and the only way revenue gets onto a
> statement is a Trade in the ledger. So the fixture books the trades a month
> of selling would have produced and checks the statement is their sum, the
> bills' sum, and nothing else.

  - L70 fixture: the world pays something for food
- **L77 income statement**
- **L86 income statement**
  - L87 revenue
  - L88 ...of which local
  - L89 ...and exported
  - L90 payroll
  - L91 electricity
  - L92 water
  - L93 total operating expenses
  - L94 operating income
  - L95 tax
  - L96 units sold, in the ledger
  - L111 loss-making month: no revenue
  - L113 loss-making month: tax is zero
  - L114 loss-making month: no phantom credit
  - L117 net income after tax
- **L120 the sales tax lands on the statement**
  - L126 sales tax on the statement
  - L127 pre-tax income is net of it
  - L128 ...and so is the profit tax
  - L129 cash moved by the after-tax figure
  - L131 the ledger is cleared for the new month
- **L133 balance sheet**
- **L139 balance sheet**
  - L140 cash
  - L141 inventory at market
  - L142 total current assets
  - L143 land (placeholder)
  - L144 buildings at cost
  - L145 total non-current assets
  - L146 total assets
  - L147 total liabilities
  - L148 equity (plug)
  - L151 ASSETS == LIABILITIES + EQUITY
- **L154 the sheet must move with the market price**
- **L158 price collapse .09 -> .05**
  - L159 inventory revalued
  - L160 total assets fell
  - L161 still balances
- **L168 ratios must not blow up on an empty business**
- **L171 empty business**
  - L172 total assets
  - L173 equity
  - L174 current ratio (no liabilities)
  - L175 debt to assets
  - L176 return on assets
  - L177 inventory share
- **L179 book value comes off the real templates**
- **L200 book value from templates**
  - L201 industrial book value
  - L203 ...and the same figure by sector
- **L210 the tax is paid once, by the business**
  - L233 business banked the AFTER-tax profit
  - L234 city collected exactly the tax it deducted
  - L235 ...and the statement's after-tax line is what was banked

## BuildMenuCheck.java - 0 labelled assertions

> Verifies that every building in the game can describe itself. Not part of the
> game.
> 
> WHY THIS EXISTS
> 
> The build menu's info card carries no written text at all - every sentence on
> it is derived from the building's own fields, so that no description can ever
> describe a building as it was two rebalances ago. That buys accuracy at the
> cost of a specific new failure: production1 is tonnes of ore in a mine,
> construction points in a depot, kilowatts in a power plant and units of food
> in a mill, and capacity is people in a house, shelf stock in a shop,
> warehouse space in a mill and treatments in a clinic. A card that reads the
> ...

_(this harness does not label its checks through a helper - it prints its findings; read its header and its sections)_

- **L51 WHAT THE CARD SAYS**
- **L99 EVERY JOB TYPE HAS A NAME**
- **L108 JOB LABELS**
- **L128 THE TWO PRICES AGREE WITH THE TILL**
- **L138 THE PRICE COLUMN**
- **L163 THE RECEIPT SERIAL COUNTS**
- **L172 THE RECEIPT SERIAL**

## BuildingDataCheck.java - 11 labelled assertions

> The migration's safety net: buildings.json must produce exactly the templates
> the hardcoded definitions did.
> 
> Run against the real Gson, not a stub, because the whole risk of moving data
> out of code is that the two quietly disagree - a field that silently reads
> zero, an id that lands on the wrong building, a job tier that never loads.
> Field-by-field equality against the built-ins is the only check that catches
> that.

- **L34 what the code says**
- **L39 what the file says**
- **L43 source**
  - L44 buildings.json loaded at all
- **L51 coverage**
  - L52 same number of buildings (
- **L54 every field of every building**
- **L135 care types line up with the category**
- **L142 care types**
  - L153 healthcare declares a care type, nothing else does
  - L167 education declares what it teaches, nothing else does
  - L184 safety declares police or prison, nothing else does
  - L186 two police buildings and two prisons
- **L188 and every profession has exactly one school**
  - L205 every gated profession has exactly one school
- **L207 ids are unique, which the saves depend on**
  - L218 every id is unique
- **L220 the manager actually uses the file**
  - L224 initializeTemplates() produced the same count
  - L226 lookup by id still works
  - L229 lookup by name still works

## BusinessServicesCheck.java - 39 labelled assertions

> The sector whose customer is not in the city.
> 
> WHAT THIS HAS TO PROVE, and why it is mechanism rather than size: the crime
> batch established that the long-run population of a city moves ten percent on
> perturbations of one part in a million, so an eight-seed median cannot resolve
> a ten percent effect and "the city got bigger" is not evidence of anything.
> What is checkable is whether the thing WORKS:
> 
>   1. the three goods are export-only and clear at the world's floor
>   2. the templates are the arithmetic the design doc claims
>   3. the licence gate refuses, and stops refusing when the engineers exist
>   4. the wage bill is the brake - a city that pays more stops expanding
> ...

- **L67 1. the goods: export only, and the floor is the price**
  - L81 the rungs pay more per seat the higher they are
  - L89 nobody here bidding - a seat-month fetches the export floor
  - L91 ...which at founding is the world's own price
  - L94 a currency twice as strong halves what the work fetches
  - L97 ...and a weak one doubles it
- **L100 2. the templates are the design's arithmetic**
- **L142 the trap this batch actually fell into, now a standing check**
- **L173 3. the licence gate**
  - L177 the engineering office declares its licence
  - L179 ...and seventy-eight of its posts need it
  - L181 half of them is the bar
  - L182 ...so thirty-nine engineers open one
  - L190 a city with no engineers cannot open one
  - L192 ...and is refused, rather than being offered a bond
  - L200 thirty-eight engineers is still one short
  - L203 thirty-nine opens the doors
  - L205 ...and the office goes up
  - L207 an engineer already at work is not spare
  - L210 a contact centre needs no licence at all
- **L213 4/5. the wage bill and the currency are the brake**
  - L233 two centres is six hundred seats
  - L234 it billed somebody
  - L235 ...and all of it left the city
  - L236 everything it sold, it exported
  - L238 it bought no materials to do it
  - L241 payroll is most of the cost and not all of it
  - L253 a strong currency cuts what the work fetches
  - L254 ...and a weak one lifts it
  - L266 the expansion test sees the difference
  - L267 a strong enough currency makes another centre a loss
  - L281 tripling the minimum wage makes another centre worth less
- **L284 6. the books, and a reload**
  - L292 every sector's statement still foots
  - L293 and the money identity holds with a new sector in it
  - L305 Business Services is the eighth sector, wherever the list ends
  - L307 the share register is the sectors and the bank
  - L314 a reloaded city has the same seats
  - L315 ...the same cash
  - L317 ...and the same engineering licences
  - L330 a cell carries eight slots, a holding per company, the dollars abroad,
  - L346 an older save restores against the company list it was written with
  - L349 ...and owns none of the company that save had never heard of
  - L351 ...while its first holding is still its first holding
  - L361 ...and a city from before cars existed owns none

## CalendarCheck.java - 62 labelled assertions

> The date on the status bar, and the log of what the city has finished.
> 
> Both are new and both are the kind of thing that looks obviously right and is
> quietly off by one. The calendar in particular has two independent chances to
> be wrong - the epoch and the modulo - and they cancel out at exactly the point
> anyone would eyeball it (month 1), so it is checked at the boundaries of every
> year it touches rather than at a couple of convenient months.

- **L42 1. the epoch**
  - L45 month 1 -> year
  - L46 month 1 -> month of year
  - L47 month 1 formatted
  - L62 a new game starts at month 1
- **L64 2. every boundary in the first year**
  - L67 month 12
  - L68 month 13
  - L69 month 12 is still 2000
  - L70 month 13 rolls over
- **L77 the decade Jerus specified**
  - L78 month 121, ten years on
  - L79 ...and 120 months IS ten years
  - L80 month 120, the month before
- **L82 3. the far end and the bad end**
  - L86 month 4002 (the playtest's last)
  - L87 4002 lands in 2333
- **L89 days, which only the clock uses**
  - L97 January has 31
  - L98 April has 30
  - L99 December has 31
  - L103 February 2001 has 28
  - L106 2000 was a leap year
  - L107 2100 is not
  - L108 2400 is
  - L109 2001 is not
  - L110 ...so month 2 has 29 days
  - L111 February 2100 has 28
  - L118 a month opens on the first
  - L119 ...and negative is still the first
  - L120 half way through January
  - L121 the end of January is the 31st
  - L122 ...and past the end is still the 31st
  - L123 the end of February is the 29th in 2000
  - L131 ten years of days, none of them outside their month
  - L132 the date the clock prints
  - L139 month 0 floors at the epoch
  - L140 a negative month does too
- **L142 4. "in N months"**
  - L145 same month
  - L146 one ahead
  - L147 under two years
  - L148 exactly two years
  - L149 two and a bit
  - L150 already passed
- **L152 5. the build log**
  - L157 one entry
  - L166 same building, same month, still one entry
  - L167 ...and the quantities added
  - L170 a different building is its own entry
  - L173 the same building next month is a new entry
  - L176 newest first
  - L179 the window matches demolitions
  - L181 at exactly 24 months it is still shown
  - L183 one month later the month-10 rows are gone
  - L185 ...but nothing was actually deleted
  - L187 the wording
  - L188 ...last month
  - L189 ...older
  - L195 restoring null empties it rather than throwing
  - L198 a real restore keeps every entry
  - L199 ...and the merge did not run again on the way back in
- **L202 6. through a real month**
  - L214 the city logged what it finished
  - L220 ...and the houses are in it (
  - L227 every entry is stamped inside the game's own timeline
- **L229 7. one press is one month (0.7.1)**
- **L238 one press is one month**
  - L242 one press of nextMonth() advances the month by exactly one
  - L246 ...five months of a skip, by exactly five
  - L250 ...and SimulationEngine.simulateMonth(), the spine it runs through, by none
  - L252 ...so the date on the status bar did not move with it

## CapitalFlowCheck.java - 49 labelled assertions

> Hot money: does it come for the right reason, and does it leave for one?
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the SPREAD pull it - the excess return net of what the risk costs -
>      rather than the headline rate? A city paying 9% while the world charges
>      it 7% for its own risk is not offering anything, and money that came for
>      that is money that came for a number rather than a reason.
> 
>   2. Is fragility distinct from crisis? Thin reserves must make a shock into
>      a run without themselves being one - the first version got this backwards
>      and produced 110 sudden stops in 333 years, each of them a city fleeing
> ...

- **L63 1. it comes for the spread**
  - L68 a city paying the world rate attracts nothing
  - L83 ...nor does one whose whole spread is its own risk premium
  - L91 a real spread pulls money in
  - L92 ...and it settles near what the spread justifies
  - L94 ...on the spread NET of the premium
- **L96 2. and it leaves when it closes**
  - L102 a closing spread takes the money with it
  - L103 ...without anybody panicking
- **L105 3. fragility is not a crisis**
  - L120 a city with no reserves still attracts money
  - L121 ...and is not in crisis for it
  - L130 a shock on top of fragility is a run
  - L131 ...and the money starts leaving at once
  - L132 ...and is told why in words, both halves
- **L136 4. reserves are the defence**
  - L148 the same shock on a backed position is not a run
  - L149 ...and the money stays
  - L169 the defence works exactly where the constant says it does
  - L179 a default runs the money however deep the reserves
- **L182 5. painful, never fatal**
  - L198 the money does not vanish in a single month
  - L199 ...but it is gone within a couple of years
  - L205 a city that survives a stop can be lent to again
  - L207 ...and is not still marked as stopped
- **L209 6. it reaches the bank**
  - L221 hot money is funding the branch network could not gather
  - L223 ...so it lifts what the bank can lend
  - L225 ...and the bank knows how much of its funding can walk
  - L246 money arriving does not make the bank richer
  - L251 ...and money leaving does not make it poorer either
  - L267 a position CAN get big enough to be fragile
- **L270 7. in a real city, and across a reload**
  - L321 fixture: the city actually offered a carry over the world
  - L330 hot money actually arrives in a real city that offers a carry
  - L332 ...in real money, not a rounding error
  - L334 ...and the books balance every month it does
  - L349 the position reloads
  - L350 ...and the record of what has moved
  - L352 ...and the bank is funded by it on the first tick, not the second
  - L365 the fixture is genuinely mid-stop
  - L370 a city loaded mid-stop is still in it
  - L371 ...with the same months left to run
- **L374 8. and the city's own money goes the other way**
  - L397 fixture: the borrower really owes something
  - L406 idle money goes abroad for the world's rate
  - L407 ...most of it, at this spread
  - L409 ...and not all of it - working capital stays
  - L412 the coupon rolls where it is earned, so the wealth grew
  - L414 ...and nothing of it landed in the till
  - L416 a sector with a loan keeps its money home
  - L423 it comes home when the bank pays better
  - L424 ...to nothing, in the end
  - L439 fixture: the surplus alone would strengthen the currency
  - L441 a surplus sent abroad pushes the currency nowhere
  - L461 a live city sends money abroad on its own
  - L462 ...and every month of it is conserved

## CarCheck.java - 52 labelled assertions

> The cars: who buys one, what it costs them, and what it does to the road.
> 
> WHY THIS HARNESS EXISTS. Transport steps 1 to 4 split the road's demand into
> streams and gave the city three ways to serve it - highways, transit, rail -
> and every one of them was a RELIEF on a baseline that never moved. A car is
> the first thing in this game that makes the road worse, so for the first
> time the promise "an existing city computes exactly what it computed
> yesterday" is carried by a number that can be non-zero, and it needs holding
> down in its own file.
> 
> SIX CLAIMS.
> 
> ...

- **L85 1. A CITY WITH NO CARS**
- **L89 a city with nobody driving is the city it was**
  - L97 the factor is one, and it is one exactly
  - L99 ...and so is the willingness to ride
  - L103 setting the ownership to zero changes nothing, to the bit
  - L117 ...and a road that was never jammed is remembered as clear
- **L121 2. THE PENALTY**
- **L125 what a car costs the street**
  - L129 a fully motorised city asks CAR_LOAD_AT_SATURATION of the road
  - L134 ...and half way there is half way up the line
  - L141 a city where everybody drives is a different city
  - L169 the penalty lands on the commuters still driving and no others
  - L182 ...so a rider taken off a jammed motorised street is worth more than one off a walking street
- **L186 3. THE LOOP**
- **L190 people drive until the road is full, then take the tram**
  - L197 a city where everybody owns a car and the road is clear rides nothing
  - L215 ...and rides more of it the worse the commute gets, all the way down
  - L216 ...but never past the share any city's transit can carry
  - L229 at a standstill, three quarters of the car owners are on the tram
  - L240 one good month does not empty the trams
- **L244 4, 5, 6. IN A CITY**
- **L248 and now a city that buys them**
  - L270 a city with money in the bank motorises
  - L274 ...and the road is told about them
  - L297 a month of car sales is a month of real money
  - L301 ...and every car sold was paid for at the same price
  - L306 ...and the savings really left
  - L316 no household owns less than none of a car, or more than one
  - L317 ...and the fleet is the cells' own count
- **L323 and a fleet is a stock, not a flow**
  - L332 a fleet wears out at one part in CAR_LIFE_MONTHS a month
  - L337 ...and a household whose car died replaces it at once, not at the diffusion rate
- **L358 WHY THE BAR IS 95% AND NOT 100%, and why it is not the plateau this**
  - L377 a city that can afford cars ends up with one per household, bar the grain of a whole car
  - L399 ...and a city that cannot loses its fleet over the life of a car
- **L403 THE DEPOSIT AND THE LOAN (2026-09-17)**
- **L407 and they borrow for it**
  - L426 a household short of the cash still buys, on credit
  - L437 ...and the seller is paid in full
  - L440 ...of which the household paid every penny it had spare
  - L443 ...and a lender found the difference, and said so
  - L446 ...and the two halves are the whole price
  - L466 a household that can pay cash borrows nothing
  - L480 a household with nothing down and no room left buys nothing
  - L495 ...but a household that owes a little still buys one
- **L500 AND A FAMILY IN TROUBLE SELLS IT (2026-09-17)**
- **L511 and a family in trouble sells the car**
  - L542 a household that cannot feed itself puts the car up
  - L552 ...and somebody buys it
  - L554 ...for less than a new one, and more than scrap
  - L564 the fleet did not shrink - it changed hands
  - L567 ...off the family that could not keep it
  - L570 ...and onto the one that could
  - L583 the seller was paid what the car went for
  - L586 ...and every dollar of it came out of a buyer or a lender
  - L605 a household that can ride the gap out keeps it
  - L630 a city where everybody is selling gets the floor and nothing else
  - L635 ...so the cars stay where they were, and so does the hunger
  - L643 a city whose transit could carry everybody halves what it will own
  - L649 ...and a city with no transit deters nobody, exactly
  - L670 a metro built after the cars unmotorises the city to its ceiling
- **L677 and it all survives a save**
  - L686 a reloaded city has the same fleet
  - L689 ...the same ownership rate on the road
  - L692 ...and the same memory of the commute
  - L708 a save from before cars existed still loads
  - L710 ...and the city it loads owns none

## CarryTradeCheck.java - 27 labelled assertions

> The carry trade: the other side of hot money, and the bank's first borrower.
> 
> Jerus's framing, and it is the right one: "foreign borrow from the bank and
> convert to usd to do stuff with it, aka effectively having outflow of
> currency... its basically the opposite of hot money". Hot money comes here
> because the city pays more than the world. The carry goes the other way: when
> the city pays LESS than the world, the trade is to owe the cheap currency and
> hold the dear one, and every dollar of it is local currency SOLD.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Is the direction right? Hot money's condition is cityRate > worldRate;
> ...

- **L76 1. it is the opposite sign**
  - L87 a city paying MORE than the world is never borrowed from
  - L91 ...and one paying less is
  - L92 ...on the gap between the two rates
  - L102 ...net of what it costs to be short this currency
  - L105 ...and a small premium only narrows it
  - L107 ...leaving a smaller book, not none
- **L110 2. the bank's book is the bound**
  - L117 twice the headroom is twice the trade
  - L119 ...and never more of it than the cap allows
  - L122 fixture: the cap actually leaves the bank something
  - L131 a bank with no room lends none of it
- **L134 3. it builds and unwinds gradually**
  - L143 no one arrives all at once
  - L144 ...but at the speed the constant says
  - L151 ...and it does get there in the end
  - L159 a closed spread is repaid to nothing
  - L160 ...and repaying is the faster of the two
  - L165 the coupon is the book at the rate they took it at
- **L168 4. it survives a save**
  - L175 the book comes back
  - L176 ...and what it borrowed over its life
  - L178 ...and what it paid for it
  - L180 ...and the high-water mark
  - L191 a save from before it existed has none of it
- **L194 5. and it reaches the border**
  - L255 fixture: a cheap city really is borrowed from
  - L256 what the bank lends crosses the border
  - L257 ...and every month of it still balances
  - L258 ...with the bank's book holding exactly the stock
  - L280 fixture: the surplus alone would strengthen the currency
  - L282 money borrowed and taken out offsets a surplus

## CentralBankCheck.java - 174 labelled assertions

> Proves the central bank's books: that money is made and destroyed on them
> and nowhere else, every price 0.7.0 hangs off the policy rate, and its two
> dials - the holdings (0.7.1) and the advances ceiling (0.7.2). Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "the feds sheet would show how much debt it holds,
> like debt to itself aka money printing." Until 0.7.0 the city's central bank
> was the rest of the world - the bank placed its spare cash abroad and funded
> its shortfalls abroad - and the treasury's overdraft was an emergency note
> with no limit, which compounded a broke seed to $193 quadrillion once the
> bank really paid for it. The central bank brings all of it home, and a
> balance sheet that makes money is exactly the kind of thing that must be
> ...

- **L171 2. reserves earn the policy rate**
  - L179 its spare cash is paid the policy rate, a month of it
  - L186 ...at 10% as at 3%
  - L187 savers are paid their share of what reserves earn
  - L189 ...so the deposit rate rises with the dial
  - L196 ...paid in money the central bank made
- **L198 3. the window**
  - L207 past its deposits, what it borrows is the window's
  - L210 the window charges the dial plus the penalty
  - L212 ...on the window's tranche, and nothing on the deposits
  - L214 ...and a bank that is borrowing has no reserves to be paid on
  - L220 the advance is money made
  - L221 ...and the interest is the central bank's, and destroyed
  - L223 ...so M0 is what it lent less what it was paid
  - L226 a smaller shortfall is a repayment
  - L237 fixture: a bank past its deposits whose equity went under has failed
  - L240 a failed bank is charged nothing at the window: its equity is zero by the resolution,
  - L245 ...while the central bank goes on advancing what it owes
  - L247 ...interest-free
- **L249 4. the city's paper**
  - L260 the floor is the policy rate itself
  - L261 ...so a debt-free city is quoted the dial plus the bank's premium
  - L265 ...and one that owes, the dial plus both spreads plus the premium
  - L268 ...where the spreads are real, not zero
  - L270 the ceiling sits on the dial too, not under it
  - L272 what a different dial would be quoted is the same sum
  - L276 ...and nothing lends the city below what the money costs the bank
- **L279 the city**
  - L305 fixture: the city has a bank
  - L306 fixture: and a year of revenue for the ceiling
- **L308 1 and 5. every kind of flow**
  - L315 fixture: the bank has spare cash to be paid on
  - L317 the central bank paid exactly what the bank booked on its reserves
  - L319 ...and it paid something
  - L331 fixture: the treasury sold the bank a bond
  - L333 the bank bought it at the window
  - L334 ...which charges the dial plus the penalty
  - L336 ...and its interest is the central bank's, to the cent
- **L340 5. a broke treasury draws advances, and repays them first**
  - L352 the shortfall is advanced whole, net of the remittance that arrived first
  - L354 ...and owed
  - L355 ...and journalled as printing on the bridge
  - L366 the advances' interest is the policy rate on what was owed
  - L368 the remittance is last month's profit, as struck
  - L369 cash above zero repays the advances before anything else
  - L373 ...the first thing the month's journal records
  - L376 the month's profit is the interest it took less what reserves cost
  - L378 ...and it is owed back to the treasury once any loss is made good
  - L380 ...or carried, if it was a loss
  - L384 fixture: a profit was struck to remit
  - L398 a buyback: the bank's share of the price goes to its cash
  - L400 ...its book drops by its share of the principal
  - L402 ...and the difference against what it carried the paper at is its gain or loss
  - L404 ...and was the price quoted
  - L407 with the bond sold back, the bank repays the window
  - L408 ...and the month's profit reaches the treasury the month after
  - L410 ...as a revenue line on its budget
- **L416 1, closed: every kind of flow, and every month**
  - L418 the run had
  - L428 ...and a month with five kinds at once, which is all a month can hold
- **L433 6. the ceiling and the arrears**
  - L440 fixture: the advances are past the ceiling
  - L441 fixture: the city owns something that needs repairing
  - L443 its repairs were refused, cash being nothing
  - L446 ...and owed to the builders as arrears
  - L448 ...who were paid the rest of the bill and not that part
  - L456 a promise is paid whatever the treasury holds
  - L458 ...overdrawing it
  - L460 a discretionary line is refused
  - L463 ...and the refusal is owed
  - L464 a purchase is refused and nothing is owed
  - L470 ...a building the treasury cannot pay for is not ordered
  - L472 ...nor capital put into the bank
  - L473 ...nor reserves bought
  - L476 the promise's overdraft is advanced past the ceiling
- **L479 ...and the arrears are paid down first when cash returns**
  - L482 fixture: the treasury owes arrears
  - L486 the central bank was repaid in full
  - L487 ...and then the arrears
  - L488 ...every dollar of them
  - L495 ...in that order
- **L499 7. the autopilot**
  - L504 fixture: there is a year of prices to read
  - L507 fixture: the rule wants something else
  - L509 with the rule's hand on it, the month opens with the dial where the rule says
  - L512 the player's hand takes it back
  - L514 ...and the rule leaves it where the player put it
  - L523 the toggle survives a save
  - L530 ...off, too
  - L531 ...and a dial at 0% reloads at 0%, not the 3% default
- **L535 9. the save**
  - L543 fixture: advances and arrears to carry
  - L554 the central bank's whole balance sheet reloads exactly
  - L555 ...M0
  - L556 ...what the treasury owes it
  - L558 ...the ceiling
  - L559 ...and the arrears, line by line
  - L561 ...and M2
  - L563 ...and a year of M0 behind it
- **L566 8. a currency reform**
  - L574 fixture: the reform happened
  - L575 M0 is a hundredth
  - L576 ...the treasury's advances
  - L577 ...the bank's at the window
  - L578 ...the loss carried
  - L579 ...printed since founding
  - L580 ...the ceiling
  - L581 ...and the arrears
  - L582 but the policy rate is the policy rate
  - L583 ...the advances against the ceiling are where they were
  - L585 ...and M0 against M2
  - L588 ...and the first month in the new money closes
- **L590 10. an old save**
  - L596 fixture: the city carries a note
  - L602 fixture: the save carried
  - L611 it loads
  - L613 with an empty central bank: nothing made
  - L614 ...nothing advanced
  - L615 ...nothing printed
  - L616 ...nothing owed in arrears
  - L617 ...and the player's hand on the dial
  - L620 and it runs, closing the audit and the M0 identity every month
  - L624 ...and runs its note off
  - L627 a new game after a load founds a fresh central bank
  - L629 ...with nothing owed
- **L646 11. the holdings dial buys the bank's term paper with money it makes**
  - L675 fixture: the treasury sold a twenty-year bond
  - L678 fixture: ...and the bank holds all of it
  - L680 fixture: ...and the central bank none
  - L681 with nothing held, the long end is the table's premium over the note
  - L694 after one month the central bank holds QE_SPEED x 30% of it, at face
  - L696 ...which is what the paper says it holds
  - L697 it paid the market value at the curve
  - L698 ...and the bank was paid exactly that
  - L699 ...money it made: the audit's issue carries the price
  - L702 ...and M0 moved by exactly what it made less what it destroyed
  - L704 the bank's book fell by the face
  - L705 ...and its book on the bank's own sheet with it
  - L706 the bank booked its gain against what it carried the paper at
  - L708 ...and the central bank its own against face, into the month's profit
  - L711 compression(240) is the twenty-year premium times the share held over the most it may hold
  - L714 the twenty-year rate sits exactly compression(240) under the table
  - L716 ...and the note carries none of it
  - L717 ...so the short end is where it was: the dial, the spreads and the premium
- **L722 12. the coupon on its share is the central bank's, destroyed, and remitted**
  - L727 the coupon on its share arrived at the central bank
  - L728 ...and was destroyed with the rest of what it took back
  - L732 the month's profit carries it
  - L734 ...owed back to the treasury once any loss is made good
  - L737 ...and remitted the month after
- **L740 13. the dial to nothing sells it back, and the curve returns to the table**
  - L742 fixture: the central bank holds some of the bond
  - L753 the dial to 0 sells it all back within the speed's months
  - L754 ...the paper agrees
  - L755 ...a step at a time, not in one month
  - L756 money retired equals what the bank paid
  - L757 ...and the curve returns to the table
- **L760 14. the holdings survive a save**
  - L764 fixture: holdings to carry
  - L770 fixture: the dial was moved, so the setting before it is not the dial
  - L780 the paper it holds
  - L781 ...the dial
  - L782 ...and the setting before it, which sets the pace
  - L784 ...so the reloaded city steps at the same pace
  - L786 ...what the paper says it holds
  - L788 ...and the long end of the curve
- **L791 15. a reform scales the holdings and not the dial**
  - L796 fixture: the reform happened
  - L797 the paper it holds is a hundredth
  - L798 ...on the paper too
  - L799 the dial does not move
  - L800 ...nor the compression it buys
  - L803 ...and the first month in the new money closes
- **L815 16. the ceiling is the player's dial, up to three years of revenue**
  - L829 a city opens at the default
  - L831 fixture: a year of revenue to set it on, nothing owed, nothing in arrears
  - L840 ...which is DEFAULT_ADVANCES_MONTHS of trailing revenue
  - L843 fixture: overdrawn by the whole ceiling, the treasury has no room
  - L845 ...so a purchase is refused
  - L848 set to twelve months, the ceiling doubles
  - L849 ...and the room is the six months it added
  - L850 ...so the same purchase is paid, overdrawing further
  - L856 a treasury that was at the old ceiling draws past it
  - L858 ...and is still inside the new one
  - L860 the dial stops at MAX_ADVANCES_CEILING
  - L863 ...and at nothing below
  - L872 the setting survives a save
  - L873 ...and so the ceiling
  - L877 fixture: the save carried the dial under its own key
  - L884 a save from before the dial reads the default: six months, the constant it was
  - L890 fixture: the reform happened
  - L891 a reform does not move the dial: months are not money
  - L892 ...while the ceiling, which is money, is a hundredth

## ConservationCheck.java - 18 labelled assertions

> Nothing is created and nothing is destroyed.
> 
> ==================== WHY THIS FILE EXISTS ====================
> 
> A deliberate audit in September 2026 turned up three bugs that all twenty-two
> existing harnesses and four thousand months of `LongPlaytest` had missed:
> 
>   1. Loading a save into a running game DOUBLED every building. loadGameSave()
>      called only initialize(), which is guarded and therefore a no-op once a
>      game is running, and the reset inside loadGame() was commented out.
>      Measured: house capacity 2,720 -> 2,720 -> 5,340.
> 
> ...

- **L111 1. FOOD**
  - L182 every month, food in equals food out plus the change in stock
  - L199 ...and the warehouse is not simply frozen
  - L200 ...and the stock level actually moves
- **L202 2. ELECTRICITY**
  - L212 the utility's revenue is exactly what the sectors were charged
  - L222 there is unbilled draw, and it is not booked as revenue
- **L225 3. WATER**
  - L231 the water utility's revenue is what the sectors were charged
- **L234 4. A CITY IS THE SAME CITY AFTER YOU LOAD IT**
  - L266 
  - L272 ...and matches the city that was saved
  - L298 
  - L301 ...and it is back at month one
- **L303 5. WHAT A RELOAD MUST NOT FORGET**
  - L342 the mine has a payroll at all after a load
  - L343 ...and it is the payroll it was working
  - L347 utility income survives a load
  - L349 the residents' statement is not blank after a load
  - L351 ...and the government's revenue block is not either
  - L359 ...and rebuilding it does not book the month twice
  - L367 the population trend survives, so forecasts are not flat
  - L375 every sector's losing streak survives a load

## ConsumptionCheck.java - 48 labelled assertions

> Verifies the consumption model against the two laws it is shaped to obey,
> and guards the data file against the Java.
> 
> WHY A HARNESS FOR SOMETHING NOTHING EATS YET. Every number in here will be
> load-bearing the month households start buying these goods, and hunger runs
> into sickness and sickness runs into mortality - so a wrong curve is not a
> wrong figure on a screen, it is a body count. The model is proved first and
> connected second, which is the only order that lets the ensemble say
> anything when it IS connected.
> 
> THREE OF THESE ASSERTIONS EXIST BECAUSE THE PROBE FOUND THE BUG FIRST, and
> each is written so the bug cannot come back: calories that fell as a
> ...

  - L43 the file loads
  - L44 ...and has goods in it
- **L49 1. the file itself**
  - L64 every good has a delivered price and a gate price
  - L65 ...and the gate is under the door, as it is for every other good
  - L66 every good carries calories, so subsistence can be met out of it
  - L67 every elasticity is inside a band a demand system can mean
  - L68 ...and quality and convenience are shares of one
  - L69 every good says what a person eats of it at the reference
  - L73 a balanced diet's shares are a whole diet
  - L76 ...and every category a good declares has a target
  - L86 something in the file is an inferior good, or Bennett cannot happen
- **L88 2. Engel, both halves**
  - L104 the food SHARE never rises with income
  - L105 ...while the AMOUNT spent never falls with it
  - L106 a household at bare subsistence spends all of it on food
  - L108 ...and a household at fifty times subsistence spends a fraction
  - L120 NOBODY EATS LESS FOR BEING RICHER, at any rung of the ladder
  - L121 ...and no household anywhere comes back with an empty basket
- **L123 3. Bennett**
  - L137 the calorie share from staples FALLS as a city gets richer
  - L138 ...and the share from protein rises
  - L139 ...and from produce too
  - L141 a poor household takes most of its calories from starch
- **L143 4. satiation**
  - L151 no household eats past what satiation allows, however rich
  - L152 ...and a rich one eats exactly that
  - L154 a household at subsistence is hungry, which is the point of the floor
- **L157 5. the time axis**
  - L166 a household with no time takes more of its calories ready-made
  - L168 ...and it costs them the same money, because this axis is not money
  - L170 ...but it costs them diet quality
  - L179 a working single parent carries one dependant an adult
  - L181 a large family, two adults and four others, carries two
  - L183 a couple with nobody to feed carries none
  - L185 and a retired household has all the time in the world
- **L188 6. diet quality is balance**
  - L203 a diet of mostly starch scores badly, however wholesome the starch
  - L205 ...and so does one with a third of its calories as treats
  - L206 the middle of a city eats best
- **L208 7. a currency reform changes nothing**
  - L224 fixture: the same file at a hundred times the prices
  - L234 every household buys exactly what it bought, to the gram
  - L235 ...and the bill is a hundred times what it was, and nothing else
  - L237 ...subsistence costing a hundred times as much too
  - L239 ...and the diet is the same diet
- **L241 8. the OTHER currency**
- **L256 the city's money is not the world's money**
  - L263 the same household is the same household at twice the rate
  - L266 ...and the same wage at twice the rate is half the household
  - L276 leaving the rate out overstates the city by exactly the rate
  - L278 fixture: which is a doubling, not a rounding
- **L281 9. the file and the enum**
- **L295 the file and the enum are the same numbers**
  - L300 \"
  - L303 
  - L305 ...and sells to it for what the file says
  - L307 ...and is importable, because nothing here is made at home
  - L310 every good in the file has a good in the market

## CreditCheck.java - 130 labelled assertions

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.

- **L35 1. pricing**
  - L44 no debt -> min spread
  - L45 no debt -> rate
  - L51 leverage 0.5
  - L52 leverage 0.5 -> spread
  - L53 leverage 0.5 -> rate
  - L59 leverage now 2.0
  - L60 spread capped
  - L61 rate capped at govt + 8
  - L66 govt 20% -> business 28%
  - L72 negative assets -> spread at ceiling
  - L81 no debt but insolvent -> ceiling
  - L82 ...and the loan is written at that rate
  - L92 quoted rate before borrowing
  - L94 but a big loan prices itself in
- **L97 2. rate is fixed at issue**
  - L110 new borrowing got dearer
  - L111 but the old loan's rate is unchanged
  - L112 interest still priced off the old rate
- **L115 3. origination**
  - L123 solvent sector borrows nothing
  - L124 ...and has no debt
  - L128 borrowed hole + buffer
  - L129 principal on the books
  - L130 one loan, not many
- **L132 ...and it stops at the borrower's own insolvency line**
- **L154 ...and the ceiling is BELOW the line, by a real gap**
  - L165 fixture: the ceiling is strictly below the write-down line
  - L170 a loan is capped at the ceiling, not at what was asked
  - L171 ...so the principal sits exactly on it
  - L172 ...and the borrower is solvent there
  - L176 a borrower at the ceiling survives a one-third fall in assets
  - L190 fixture: the borrower at the ceiling owes interest this month
  - L191 at the ceiling, the desk advances this month's interest and no more
  - L193 ...so the books moved by exactly that
  - L194 ...and there is nothing left to lend for losses
  - L199 the room the shortfall desk has grows with the assets
  - L202 a borrower whose assets grew can borrow again
- **L205 ...and a shut lender lends nothing, from either desk**
  - L216 a frozen bank lends nothing to a short sector
  - L217 ...and offers the investment desk no room
  - L219 ...until it is standing again
  - L226 a sector with no assets at all cannot borrow a penny
- **L229 ...and a repeat defaulter is shut out for longer**
  - L254 fixture: attempt
  - L263 a first default costs a year
  - L264 ...a second costs longer than the first
  - L266 ...and a third longer than the second
- **L269 ...and the record is priced, not only banned on**
  - L287 fixture: the two borrowers really are at the same leverage
  - L289 a serial defaulter is quoted more than a spotless borrower at the same leverage
- **L292 ...and a bankruptcy forgives the overdraft, once**
  - L308 fixture: overdrawn past everything it owns is insolvent
  - L310 the loan is written off in full against nothing
  - L311 ...and the overdraft with it
  - L312 ...once
  - L313 one default on the record
  - L314 ...and a ban
  - L317 fixture: it is insolvent again inside its ban
  - L318 ...but a sector inside its ban is not a new default
  - L319 ...so the record does not grow
  - L321 fixture: the ban has lifted
  - L323 still under water when the ban lifts IS a new default
  - L324 ...and the overdraft it ran up meanwhile is forgiven too
  - L330 a sector with no loans and an overdraft past its plant is insolvent
  - L332 ...and one whose plant still outweighs the overdraft is not
- **L334 4. maturity and rollover**
  - L346 still outstanding at month 35
  - L347 nothing matured yet
  - L350 loan retired
  - L352 principal fell due
  - L353 and is only handed over once
  - L358 balloon took cash negative
  - L360 refinanced back to zero
  - L361 new loan on the books
  - L362 matures 36 months later
- **L364 5. cash conservation**
  - L377 there is interest to pay
  - L393 operating income excludes interest
  - L394 interest expensed
  - L395 pre-tax income is net of interest
  - L396 cash moved by exactly that
  - L401 processMonth moved no cash
- **L403 6. balance sheet integration**
  - L409 loans payable
  - L410 still balances
  - L411 equity is now assets less debt
- **L413 7. the spiral guard**
  - L435 cash never left negative
  - L436 loan count stayed readable (<20, not 60)
  - L437 rate stayed inside the cap
- **L439 the CITY's debt market, repriced**
  - L462 a debt-free city with no overdraft prices at the floor
  - L471 being overdrawn costs more than being clean
  - L472 ...and a positive balance is not credit
  - L483 a big loan is quoted dearer than a small one, on the same books
  - L495 ...and ten million is well up the band, not near the floor
  - L497 the standing rate is unmoved by merely asking
  - L509 more borrowing never gets cheaper
  - L510 the quote never leaves the band
  - L524 the quoted rate is a fixed point of its own face value
  - L526 ...and a discounted bill costs more than its cash value implies
  - L538 proceeds that close the overdraft are not counted twice
  - L546 a city that cannot tax its economy is the worse credit
- **L551 7. the quote IS the deal**
- **L566 the quote is the deal**
  - L605 three hundred quotes book no debt
  - L607 three hundred quotes move no cash
  - L609 three hundred quotes leave the standing rate alone
  - L622 asking for eight times as much is priced dearer
  - L624 ...and the small one is a real quote, not the cap
  - L626 the quote reports the rate it is moving from
- **L643 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR**
- **L665 a note raises what it was asked to raise**
  - L685 every note covers the cash it was quoted for
  - L696 a serial bond's face IS the request
  - L698 ...and the city receives par less fees, by design
- **L701 10. THE STORY THAT BROKE, END TO END**
- **L708 borrow for a road, and get the road**
  - L718 the Paved Road template exists
  - L761 the fixture is actually short of the money - or this proves nothing
  - L763 ...and the city is told so rather than refused outright
  - L765 nothing was built by the refusal
  - L776 the note actually covered the gap
  - L781 THE ROADS ARE BUILT
  - L782 ...all forty of them
  - L785 ...and the cash was actually spent, not left sitting
  - L787 ...and there is a receipt for it
- **L789 10. THE BAN IS ONE BAN**
- **L801 a borrowing ban also stops the investment advisor**
- **L805 MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY.**
  - L876 fixture: retail is under a borrowing ban
  - L933 a banned sector takes no new loan to expand
  - L936 ...and either its owners paid for the shops, or the refusal says why in the ban's own words
  - L940 ...and nothing was built on credit it did not have
- **L964 11. the curve: the dial at the short end, a premium by maturity**
  - L978 debt-free, the note quotes exactly the dial
  - L979 ...and getRate() is the note's rate, the short end
  - L980 the 10-year term loan: the dial plus TERM_PREMIUM_10Y
  - L982 the 20-year: the dial plus TERM_PREMIUM_20Y
  - L984 the 50-year: the dial plus TERM_PREMIUM_50Y
  - L986 ...and past fifty years the curve holds
  - L987 a 5-year serial: the dial plus the 10-year entry, 48 of the 108 months from a year to ten
  - L989 ...a 35-year point halfway between 30 and 40
  - L991 ...and a year-long note carries none
  - L999 a hike of two points moves every row by two: the whole curve in parallel
  - L1005 fixture: the debt adds a real credit spread
  - L1010 debt moves every row by the same credit spread
  - L1016 a 25-year request is refused, in words
  - L1017 ...quoted nothing
  - L1018 ...and books nothing
  - L1024 ...while each of the five maturities is quoted
  - L1033 a bond issued at 20 years is bought back the same month for what it raised,
  - L1037 ...and the buyback takes it off the books at that price

## CrimeCheck.java - 79 labelled assertions

> Crime, the police and the prisons: every claim in
> claude/crime-has-reasons.md, each with its own cause.
> 
> Jerus, 2026-09-11: "crime is a function of unemployment, and tight or under
> households, we need police, and also prison... alot of police drastically
> reduces it but never eliminates it... if there is a reason for crime there
> is no way to actually remove it without changing the underlying reason."

- **L65 1. K is Canada**
  - L68 a Canada-like city at Canada's half coverage makes Canada's 5,585 a year per 100,000
  - L70 K is about 0.0075
  - L71 ...and kills Canada's 1.61 a year per 100,000
  - L73 a quarter of it is violent
  - L74 the injured: half a month off for each violent crime, over the city
  - L76 a theft takes a quarter of a month's unskilled wage
- **L79 2. the police**
  - L81 no police leave all of it
  - L82 Canada's half coverage takes about a third off
  - L83 full coverage takes 90% off
  - L84 full coverage is twice Canada's 180 officers per 100,000
  - L111 more police never means more crime
  - L112 ...and no amount of police makes it zero
  - L113 officers past full coverage buy nothing
  - L119 at full coverage, work for the tenth past EI cuts crime by more than half
  - L121 the crime of a reason is its share of the pressure
  - L126 ...and the reasons add up to the crime
  - L127 the what-if at this month's coverage is this month
  - L129 coverage is officers over full coverage
  - L130 an empty city is covered
- **L132 3. who is caught, and who is held**
  - L134 nobody is caught with no police
  - L136 9% of crimes at full coverage
  - L137 4.5% at Canada's
  - L138 ...and with no cells, every one of them caught and not held
  - L143 Canada's police, six months each: about Canada's 127 inside per 100,000
  - L145 ...admitted, 4.5% of crimes
  - L150 fixture: somebody went in
  - L157 they serve months two to six
  - L159 ...and the seventh month they are out
  - L160 ...and nobody is inside
  - L163 prisons full: ten cells hold ten
  - L164 ...and the rest are caught but not held
  - L166 ...next month the cells are still full, so nobody goes in
  - L168 a prison that lost its guards lets out who it cannot hold
  - L169 ...leaving what its staffed cells hold
  - L175 prisoners die and turn seventy at the adults' rate
  - L179 the state restores
  - L180 ...every figure
  - L181 a malformed state is refused whole
- **L183 4. migration**
  - L185 at or below Canada's rate nothing moves
  - L186 at twice Canada's, a tenth off the size the city supports
- **L188 5. the buildings**
  - L198 ...as
  - L199 ...holding
  - L200 ...for what the real thing costs
  - L203 the founding constabulary: full coverage for 1,200 people
  - L205 ...and no founding cells
  - L210 a police station with half its staff patrols with half its officers
  - L213 ...and the buildings hold what they hold
- **L216 6. a played city**
  - L259 with only the founding constabulary, coverage is 4.32 officers over the city
  - L261 ...and crime is above Canada's
  - L262 a police station brings it well under
  - L263 ...but not to nothing
  - L264 every month passed the money audit, the thefts from the tills declared
  - L265 every business's cash flow explained its till, the thefts a line of their own
  - L266 fixture: something was stolen from the businesses
  - L269 ...and the tills' lines add up to it
  - L270 violence killed somebody
  - L271 the graph holds this month's killed
  - L274 injuries are on the sick rate
  - L275 the crime notice is up exactly when crime is at 1.5x Canada's or worse
  - L278 migration read last month's crime
  - L284 a theft takes from the households' savings what it says it took
  - L289 ...and handing it to the offenders puts every dollar back
  - L293 fixture: somebody is in prison
  - L294 the prisoners are out of the labour force
  - L296 ...the whole of them
  - L297 the pool is still the labour market's
  - L301 the families hold the adults outside nothing: not the pool, the students or the prisoners
  - L305 the prisoners' ledger holds the prisoners, as the month opened
  - L307 ...pays no rent
  - L308 ...buys no food
  - L309 ...and pays no interest
  - L310 ...and none of them is caught but not held with cells to spare
  - L313 the treasury pays the police and the prisons
  - L314 ...on the government's books
  - L315 ...and it costs something
- **L318 a save**
  - L322 the crime comes back, the prisoners in their months
  - L324 ...and out of the labour force on the load path
  - L326 ...and the bill
  - L327 ...and the ledger

## CurrencyCheck.java - 107 labelled assertions

> Proves the currency under a central bank (0.7.2): the rate answers to the
> real rate, the vault is spent defending it, and the dial and the carry
> appetite have lost their stops. Not part of the game.
> 
> WHY THIS EXISTS. Jerus: "i think we need to uncap the rate... but if we
> do... what happens to everyone?" Until 0.7.2 repriceCurrency() moved the
> rate by the whole inflation differential every month, unbounded, while the
> support a high rate gave it saturated at 13 points over the world (the
> old MAX_RATE_PRESSURE .8 over the old ForeignAccounts.RATE_PULL 6), the
> hot money's appetite at six (CapitalFlows.MAX_SPREAD) and the dial at 25%
> (DebtManager.MAX_POLICY_RATE). So a city whose prices rose 40% a year saw
> its currency fall 40% a year by rule, whatever its central bank did - the
> ...

- **L149 1. the real rate moves the currency, not the inflation differential**
  - L168 fixture: past settling, trade in balance, the vault empty
  - L170 fixture: ...and the whole economy trades, so openness is one
  - L181 paying over the world in real terms strengthens the rate
  - L182 ...by exactly -(real gap x RATE_PULL) x DRIFT_SPEED, and the parity pull
  - L185 paying under it weakens the rate
  - L186 ...by exactly the mirror
  - L188 at the world's real rate the rate moves by the parity pull alone
  - L190 ...so the inflation differential no longer moves the rate directly
- **L199 2. a spiral needs an outflow**
  - L222 fixture: ten months with no push and no deficit
  - L223 with the real gap at nothing and no outflow, the rate moves by the parity pull alone
  - L225 ...where the inflation drift would have carried it a third weaker
- **L251 3. the defence sells the vault's dollars, and what it sells is what it damps**
  - L269 fixture: the trailing deficit pushes the currency weaker
  - L270 fixture: the month's own accounts show a US$2M deficit
  - L271 fixture: the vault is at six months' cover
  - L272 fixture: ...so its capacity is the most there is
  - L273 fixture: the treasury bought nothing this month
  - L280 the central bank sells absorption() x the deficit, to the cent
  - L281 ...and the vault falls by exactly that
  - L282 ...at the month's rate, which is what it fetches
  - L283 ...and comes off the treasury's intervention record, as a sale does
  - L285 the realised absorption is what the sale met of the deficit
  - L287 the push that reached the rate is total x (1 - realised) x openness
  - L289 ...and cover fell
  - L290 ...to what is left, at the new rate
  - L292 the dollars sold since founding count it
  - L299 an empty vault sells nothing
  - L300 ...damps nothing
  - L301 ...and the whole push reaches the rate
  - L308 fixture: a month in surplus, pushed stronger
  - L311 a surplus month sells nothing
  - L312 ...and buys nothing: the vault is what it was
  - L313 ...not by the treasury either
  - L314 ...and the rise reaches the rate in full
  - L316 ...which rose
  - L327 fixture: the vault's capacity would sell more than it holds
  - L332 a US$500k vault against a US$2M deficit sells all of it
  - L333 ...and is empty
  - L334 ...and damps 500k / 2M
  - L335 ...not absorption()
  - L336 ...and the push that reached the rate is the rest of it
- **L342 4. and what the dollars fetch is the central bank's capital spent, not money destroyed**
  - L364 fixture: the bank has made money and owes a remittance
  - L373 fixture: the defence sold
  - L374 M0 did not move
  - L375 ...nothing issued
  - L376 ...and nothing retired
  - L377 equity fell by exactly what the dollars fetched, at the rate they were sold at
  - L379 ...which is exactly what the vault fell by, at that rate
  - L381 the remittance is untouched
  - L382 ...the loss carried
  - L383 ...and the month's profit: it is capital spent, not a loss to recover
  - L385 the balance sheet's line says why: spent defending the currency
  - L387 ...this month
  - L435 every month, Game handed the currency the real rate differential
  - L437 fixture: the currency was defended in a real city
  - L445 fixture: the treasury neither bought nor sold
  - L446 the sale is the vault's capacity times the month's own deficit, at most the vault
  - L448 ...the vault fell by exactly it
  - L449 ...and it fetched the dollars at the month's rate
  - L450 the central bank booked it the month it was sold
  - L451 ...as spent defending the currency since founding
  - L452 equity fell by it: its move, less the rest of the sheet's, less the revaluation
  - L474 a save keeps the month's sale
  - L475 ...what it fetched
  - L476 ...the dollars sold since founding
  - L477 ...the vault
  - L478 ...what the central bank spent on it
  - L479 ...its equity
  - L480 ...and the real rate the reprice was handed
  - L506 the defence fires in a real city more than once
  - L507 ...and the equity line is every month's sale at its own month's rate
  - L509 in every one of those months: M0 moved by the money made alone
  - L510 ...nothing retired but what the named operations took back
  - L511 ...the audit closed, and declared nothing for the defence
  - L512 ...the remittance was the month's profit, the sale not in it
  - L513 ...and the central bank kept nothing: its equity less the vault is what it owes
  - L554 M0 moved by the money made and nothing else,
  - L555 ...nothing retired but what the named operations took back
  - L556 ...the audit closed, and declared nothing for the defence
  - L557 ...the remittance is the month's profit, the sale not in it
  - L558 ...and equity less the vault is what the bank owes: nothing kept
- **L564 5. the dial, the rule and the appetite are uncapped**
  - L568 the dial takes 60%
  - L570 ...and stops at MAX_POLICY_RATE, where a typo would have taken it
  - L572 the rule is advised unclamped past the old stop
  - L598 fixture: the city reads a year of 45% inflation
  - L605 the autopilot sets the rule's rate
  - L607 ...past the old stop of the dial
  - L617 the appetite at twenty points is larger than at six: it no longer stops there
  - L619 ...it is twenty points' worth
  - L620 ...and it stops at MAX_SPREAD
  - L623 a month at twenty points is pulled by all twenty
  - L624 ...toward the stock twenty points want
- **L652 6. the rate goes where the push takes it: the guards are a billion either way**
  - L671 fixture: past settling, trade in balance, the vault empty, the whole economy trading
  - L677 a month's push past the old guard of 100 takes the rate to 150, not 100
  - L679 ...and the dollar debt is worth the dollars at it
  - L680 ...the month's revaluation the whole of the move
  - L695 a rate under the old guard crosses it in a month
  - L696 ...and every month is the push and the pull, never the guard
  - L708 a month's push past the old floor of .01 takes the rate to 1/150, not .01
  - L710 ...and the dollar debt is worth the dollars at it
  - L711 ...the month's revaluation the whole of the move, a gain
  - L715 a rate over the old floor crosses it in a month, by the push and the pull alone
  - L717 ...to under .01
  - L727 three zeros off: the rate a thousandth
  - L728 ...the guard above it a thousandth of MAX_RATE
  - L730 ...and the one below a thousandth of MIN_RATE
  - L736 ...and the push moves it as it moves the unreformed one, a thousandth the size
  - L740 a city founded in the new unit carries the guards in it

## DeathRecordCheck.java - 22 labelled assertions

> The running totals of the dead, by age and for the orphans and the unhoused.
> 
> Jerus, 2026-09-11: "in history/graphs track how many of each category have
> died cumulative over time" - by age band, and the orphans and the unhoused;
> graphs only. The months are recorded, the totals derived. Every claim sets
> its own cause.

- **L39 1. who among the dead**
  - L53 a tenth of the babies orphaned, at forty times the rate: their share of the dead
  - L55 a tenth of the adults with no home, at 3.7 times: their share
  - L60 the sick die alike across the band, so an orphan takes a tenth of the babies' illness deaths
  - L62 ...and the unhoused a tenth of the adults'
  - L66 a city with no orphans and nobody unhoused attributes none of its dead to them
- **L69 2. the running total**
  - L73 months before a series was recorded stay unrecorded
  - L75 ...then it sums
  - L76 ...carries across a gap
  - L77 ...and goes on
- **L79 3. a city whose employer closes**
  - L105 the graph holds this month's dead:
  - L109 ...and the bands add up to the month's dead
  - L113 the running total is every month's dead since the founding
  - L114 fixture: the city has buried somebody
- **L116 CLOSING THE MILL USED TO ORPHAN CHILDREN, AND IT NO LONGER DOES.**
  - L170 fixture: the closing put adults out of work
  - L185 ...and big enough that the old rule would have at least doubled the orphans
  - L187 the orphan section does not fill behind it, in any month of the two years
  - L189 ...because the children went with their parents
  - L191 fixture: the city still has orphans for the record to count
  - L193 ...and some of them died
  - L194 the graph holds this month's orphans who died
  - L196 ...and this month's dead with no home
  - L199 ...and its running total grew with them

## DenominationCheck.java - 42 labelled assertions

> A currency reform is a change of units, and this is how we know.
> 
> WHAT IS ACTUALLY BEING ASKED. Jerus wanted a button that divides the money by
> ten or a hundred so that three centuries of inflation do not end with bread
> priced at 300M and the arithmetic losing digits at the bottom. That is a
> currency reform, and the entire risk in one is that it is not ONLY a change
> of units: miss one balance and the button quietly creates or destroys money;
> miss one compile-time constant and a House is a hundred times dearer the next
> morning.
> 
> So the test is not a list of fields. It is TWO CITIES:
> 
> ...

  - L54 the same people live there, to the person
  - L57 the same output
  - L59 ...and the same year of it
  - L61 the same rent
  - L62 the same price level
  - L64 the same currency
  - L66 the same treasury
  - L67 the same shops' till
  - L71 the same city debt
  - L73 the same bank
- **L156 1. THE UNIT**
  - L160 a founding city is in founding money
  - L161 ...and cannot reform, because nothing has inflated
  - L163 ...nor at nine times
  - L164 ...and can at ten
  - L167 lopping two zeros makes a dollar a hundred old ones
  - L168 ...so a founding price reads as a hundredth
  - L169 ...and the money gets a new name
  - L171 ...and again
  - L172 the unit compounds
  - L176 it stops before a double runs out of digits
- **L179 2. IT IS THE SAME CITY**
- **L185 a reformed city is the same city**
  - L190 fixture: the two cities really are identical to start with
  - L202 the reform goes through
  - L204 cash divided
  - L205 the exchange rate divided
  - L208 the shelf price divided
  - L210 rent divided
  - L211 the minimum wage divided
  - L214 a House costs a hundredth as many dollars
- **L217 ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT**
  - L249 the desk's room in
  - L314 fixture: there was a per-good breakdown to divide (
  - L332 the price index did not move
  - L334 nor the rent burden
  - L337 nor the cost of living
  - L340 nor the population
- **L343 3. AND IT STAYS THE SAME CITY**
- **L394 ...and the month after the reform is the same month**
- **L402 ...and the same city a year later**
- **L406 ...and the same city a decade later**
- **L448 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING**
- **L461 ...and no cell under half a household holds anything, reformed or not**
  - L462 right after the reform, in either city
  - L465 fixture: the census left cells under half a household
  - L466 ...and after no month did one hold anything
- **L468 4. AND NO MONEY WAS MADE OR LOST**
- **L473 and the books still balance**
  - L481 a reformed city conserves money like any other
- **L483 5. AND IT SURVIVES A SAVE**
  - L506 the unit came back
  - L507 ...and the money with it
  - L508 ...and the rent it was charging
  - L509 ...and a House still costs what it cost

## EducationCheck.java - 145 labelled assertions

> Verifies the schools: who gets taught, who is allowed to practise, and what
> it costs. Not part of the game.
> 
> WHY THIS EXISTS
> 
> Education is the longest feedback loop in the game - a medical school built
> today is doctors in the 2040s - and a loop that long is one nobody can debug
> by playing. Every failure mode here looks like patience:
> 
> 1. A SCHOOL THAT TEACHES NOBODY looks exactly like a school whose graduates
>    have not arrived yet. The first version of this feature had a medical
>    school that enrolled zero students for three hundred and sixty months
> ...

- **L138 1. THE PROFESSION IS GATED**
- **L145 without a medical school, a city cannot make a doctor**
  - L177 the fixture built hospitals, or this proves nothing
  - L178 there ARE graduates - they simply cannot practise medicine
  - L180 so doctor posts stand empty
  - L187 ...but some doctors moved in anyway
  - L188 ...and never more than the graduates who contain them
- **L191 2. AND WITH ONE, IT CAN**
  - L207 the school produced doctors
  - L208 ...enough to staff the hospitals
- **L210 3. NO SCHOOL TEACHES NOBODY**
- **L216 every school actually enrols somebody**
  - L225 
- **L229 4. AND DOES NOT EMPTY THE BAND IT DRAWS FROM**
- **L236 and does not drain the band beneath it**
  - L242 the diploma band survives having colleges above it
  - L244 ...and the city is genuinely better educated than the bare one
- **L249 5. A DEGREE IS A MOVE, NOT AN APPEARANCE**
- **L256 a graduate stops being a diploma-holder**
  - L262 the month's schooling moves people rather than making them
  - L273 the bands plus the students still add up to the workforce
  - L275 fixture: somebody is actually studying
- **L277 6. LICENCES CANNOT OUTNUMBER GRADUATES**
- **L284 and a licence belongs to a person**
  - L294 no more
- **L298 7. THE PIPELINE IS ITS NARROWEST STAGE**
  - L326 plenty of primary places
  - L327 ...and no high school at all
  - L328 the pipeline reports the SMALLEST, not the average
  - L330 ...and names the building to fix
  - L332 so the city produces no diplomas of its own at all
- **L335 8. THE SUBSIDY IS A REAL DIAL**
- **L342 and tuition decides who can actually go**
  - L366 paying for it yourself keeps people out
  - L367 ...and free education costs the city more
- **L370 9. IT SURVIVES A SAVE**
- **L375 through a save**
  - L392 the city saved
  - L413 the fixture actually licensed somebody
  - L415 every licence came back
  - L416 ...and the running totals with them
  - L417 ...and the tuition policy the player chose
  - L430 fixture: somebody is part way through a course
  - L431 ...and every student came back
  - L432 ...and they are out of the supply on both sides
- **L437 9b. THE WAIT IS REAL**
- **L444 the wait is real**
  - L471 fixture: people enrolled
  - L472 nobody graduates before the course is over
  - L474 ...and somebody graduates once it is
  - L476 ...while the students were out of the supply
- **L479 10. AND THE TREASURY PAYS FOR IT**
  - L489 the schools cost something
  - L490 ...charged to the treasury
  - L492 ...and on the national accounts' expenditure
  - L495 it never pays for itself, which is the point
  - L509 the bill is wages and buildings, and nothing else
  - L511 ...so net cost is that, less the fees households paid
  - L527 a generous dial collects less at the door
  - L529 ...and the subsidy it forgave is the larger one
- **L532 11. THE UNSKILLED BAND IS A REPORT CARD**
- **L545 the unskilled band is a report card on the schools**
  - L567 fixture: the unschooled city really has no basic coverage
  - L569 fixture: the schooled one really has some
  - L597 a city with no schools makes its own unskilled adults
  - L599 ...and against a city with them it is a factor, not a margin
  - L601 ...and schools are what stop it
  - L603 nobody arrived unskilled - not one, in either city
- **L607 12. THE QUEUE FOR A JOB INCLUDES THE OVERQUALIFIED**
- **L621 the queue for a job includes the overqualified**
  - L633 fixture: graduates really have come down into diploma work
- **L636 THIS PREMISE USED TO READ `open[dip] > ownHeads[dip]` - "on its own**
  - L662 fixture: the two readings of this market really do differ
  - L666 ...and it is not: the queue is what counts
  - L677 the opportunity read is the queue's, not the band's own
  - L679 nobody is ever written off entirely - the floor holds
- **L682 13. THE GRANT IS A MENU**
- **L692 the grant, on four bases**
  - L705 fixture: somebody is studying, and the wage is a wage
  - L707 a new city grants a share of the unskilled wage, the founding rule
  - L711 ...and at that basis and share the bill is bit for bit the founding expression
  - L713 ...which is the bill the month struck and the treasury carries
  - L715 ...and what the students' row was handed, the same month (0.7.1: it was the
  - L723 a fixed grant pays the amount per student
  - L727 ...and it reaches the students as their income
  - L736 ...and a currency reform scales it, because it is money
  - L738 ...while the loan rate and the tuition scale, being ratios, stay
  - L743 ...and a share of tuition does not move either
  - L745 the fixed ceiling is an unskilled wage
  - L757 fixture: last month ran a surplus, on the bridge
  - L763 a surplus share pays a tenth of the surplus the bridge showed, as one pool
  - L765 ...split evenly over this month's students
  - L768 a deficit month pays nothing
  - L770 ...and so does a surplus with nobody to split it over
  - L778 a tuition share is half of what the student body is charged
  - L784 ...each student at their own course's fee
  - L791 ...and it follows the tuition scale: at x2 the body's tuition is double
  - L796 ...so the bill the month struck was half of the scaled tuition
  - L800 the wage share puts the founding basis back
- **L804 14. THE LOAN'S RATE**
- **L814 the loan, at a rate**
  - L842 fixture: the students borrowed, and were charged nothing for it while they studied - at any rate
  - L844 a balance never told a rate is interest free, as it always was
  - L847 at 0% the repayment, the balance and the month are bit for bit what they were
  - L853 at 5% a graduate is charged the month's interest on the balance
  - L855 ...on top of the same principal as before
  - L857 ...so the balance falls exactly as it did
  - L859 ...and the month is poorer by exactly the interest
  - L861 the city's interest is every graduate's, summed
  - L864 ...and the principal repaid is what it was
  - L866 a screen asking what 5% would bring in gets the graduates' balances at the rate over twelve
  - L883 fixture: five graduates went to prison and their loans went with them
  - L885 a prisoner's student loan is frozen: nothing comes off it inside
  - L887 ...and nothing is charged on it, whatever the rate
  - L891 ...so a month later they owe exactly what they came in with
  - L893 ...while the graduates still outside kept paying, interest and all
  - L910 fixture: at a $50 grant the students borrowed, and the graduates owe the treasury
  - L920 the graduates paid interest this month
  - L921 ...which is the treasury's, to the penny
  - L923 ...as its own revenue line on the national accounts
  - L924 ...counted in the revenue total
  - L929 ...while the journal's line is principal, net of what was lent
  - L933 ...and the month passed the audit with the interest in it
  - L935 ...and the ledger was told the city's rate
- **L937 15. THE PRICE OF A PLACE**
- **L947 the price of a place**
  - L958 at x3 every fee is three times the founding table, and the table itself has not moved
  - L959 ...and so is what a household pays out of pocket
  - L960 at x1 the fee is the founding fee, bit for bit
  - L963 the schools clamp the scale to the policy's ceiling
  - L1003 the schools charge at the city's scale
  - L1004 fixture: the price is what decides here - x3 cuts the willing share, not the seats
  - L1006 a poor city at x3 enrols fewer than at x1: the trap is back
  - L1011 at x0 everybody who would go can afford to
  - L1012 ...so everybody the cap lets go, goes
  - L1014 ...and more than at x3
  - L1015 a free place bills nothing and forgoes nothing
  - L1016 the treasury's fee revenue is the scaled fees households paid
  - L1028 ...and it collects more at the door at x3, from the students it kept
- **L1031 16. ALL THREE SURVIVE A SAVE**
  - L1044 fixture: a tuition-share grant was struck and interest was charged
  - L1046 the city saved
  - L1049 ...and loaded
  - L1051 the grant's basis and amount came back
  - L1053 ...and the loan rate
  - L1054 ...and the tuition scale
  - L1055 ...and the schools charge at it from the first read
  - L1057 ...and the ledger knows the rate
  - L1058 fixture: the rule re-struck from the reloaded city would not reproduce it - the body moved on
  - L1060 the bill the month struck came back as the save struck it, not re-derived
  - L1062 ...and so did the interest line
  - L1066 a policy array from before the dials is still read
  - L1067 ...as the founding basis at the share its own slot carried, no interest, the founding price
  - L1077 ...and an old save's own wage share is the grant it had
  - L1079 a wrong shape is still refused whole
- **L1082 17. A PRICE PER SCHOOL (0.7.6)**
- **L1091 a price per school**
  - L1104 raising the university's price moves its fee and no other kind's
  - L1105 ...so the nine have parted
  - L1106 ...and the every-school reading is the first kind's, unmoved
  - L1110 one kind is held to the policy's ceiling
  - L1117 the every-school setter moves all nine
  - L1125 the array carries the nine on the end
  - L1131 ...and each kind's scale comes back as it went
  - L1134 an array of the old length is still read
  - L1140 ...as nine equal scales, the one its slot carried
  - L1156 fixture: the schools cost something and collected something
  - L1157 the kinds' costs add up to the staff and buildings the page shows, to the cent
  - L1159 ...and their fees to the tuition households paid, to the cent
  - L1161 a kind with no school costs nothing and collects nothing
  - L1163 ...and the university, standing, costs something
  - L1165 the month re-struck at today's scales is what was billed
  - L1177 the month charges the university at its own price and the college at the city's
  - L1181 the city saved again
  - L1184 ...and loaded
  - L1186 the nine came back parted
  - L1189 ...and the load path told the schools the nine, not the one
  - L1194 ...and the month by kind came back with the save, not zero

## EquityCheck.java - 64 labelled assertions

> Verifies the share register: who buys, at what price, what they are paid,
> and that a month with owners in it still adds up.
> 
> Every claim is CAUSED. A register is handed a record and asked what it
> would do; households are given savings and offered shares; a company is
> given a month's income and its owners are paid. The one live city at the
> end is there to show the mechanism runs inside the audited month, not to
> hope that somebody happens to buy something.

- **L71 1. the households first, then the world**
  - L78 the fixture's households have money past the cushion
  - L83 each household puts in the fraction of what is past its cushion
  - L85 ...and holds shares worth exactly what it paid, at the founding price
  - L87 the households' part is what a hundred of them paid
  - L89 ...and the world took the rest of a founding offering
  - L91 so the whole offering was raised
  - L92 the register agrees with the households about what they hold
  - L100 a household that owes the bank buys nothing
  - L104 ...and one with only its cushion buys nothing
  - L110 a small offering is taken up, not oversubscribed
  - L111 ...and nothing goes abroad when the households took it all
  - L112 ...raised is what was offered
- **L114 2. the world's test**
  - L121 twelve months of losses: the world buys nothing
  - L127 a record that yields past the world's rate plus the premium: it buys
  - L134 ...and one that yields less than that gets nothing from abroad
- **L137 3. the regimes**
  - L141 three months on the books is NEW
  - L142 steady profit for a year is GOOD
  - L143 a year of losses is BAD
  - L145 profitable but declining is NORMAL, not GOOD
  - L148 six loss months of twelve is BAD, whatever the total
  - L151 a business whose income swings wants more equity than a steady one
  - L153 ...a steady one runs at the base
  - L155 ...and a loss-maker at the ceiling
- **L158 4. how much it raises**
  - L161 a new company sells the founding share of every plan
  - L163 a company in a bad year sells nothing
  - L168 the fixture's company is in a good year
  - L172 in a good year it raises AHEAD: the target share of three years' building, less what it has past target
  - L174 ...which is far more than this month's plan
  - L175 ...and never more than the expansion itself
  - L177 a good year with equity already past the target raises nothing
  - L180 a normal year at target borrows
  - L184 ...but well under target it raises back up to it
- **L188 5. the price**
  - L195 a company's first shares sell at the founding price
  - L197 the next sell at book per share
  - L203 ...and a company with no book but earnings sells at the earnings' value, not for nothing
- **L206 6. the founders**
  - L212 a first offering against a book already there issues the book to the households first
  - L214 ...so the new money buys only what it paid for
  - L216 ...and the founders keep the company
- **L218 7. the dividend**
  - L221 nothing is due on a loss
  - L222 ...and the payout share of a profit
  - L224 a company with no shares owes nobody
  - L230 each household is paid on its shares, into its savings
  - L232 ...the households' part is theirs
  - L234 ...and the rest went abroad
  - L235 ...every share paid once
  - L249 twenty households leaving take twenty households' shares
  - L251 ...and the ones who stayed hold what they held
  - L256 ...and are paid abroad from then on
  - L258 ...with the register still agreeing with the households
- **L261 8. the save**
  - L265 it restores
  - L266 ...the shares
  - L267 ...held abroad
  - L268 ...and the lifetime dividends
  - L269 ...and the regime is re-read from the record
  - L270 an array of the wrong length is refused whole
  - L274 the households' shares ride in the cell save
- **L277 9. a live city**
  - L292 the founding bank's capital was sold as shares
  - L294 ...and every dollar of it arrived as capital, home or abroad
  - L311 retail is listed once it has a book
  - L313 ...and every share of it is held, at home or abroad
  - L332 the city's companies went to the market on their own
  - L333 ...and paid their owners
  - L334 ...and every month of it is conserved
  - L343 ...with the register and the households agreeing on every company

## ExchangeCheck.java - 147 labelled assertions

> Verifies the exchange: what the desk quotes, who trades with it and why,
> what a company does with its surplus, and that a city with a market in it
> still adds up and survives a save.
> 
> Every claim is CAUSED. A register is handed shares and a book and the
> desk is asked what it quotes; a family leaves and its shares are bought;
> the world is shown a yield and sells or buys; households are given money
> and a choice of two companies; a household is left short and sells before
> it borrows; a company is handed a surplus and a market that is cheap, then
> one that is dear. The live city at the end shows the mechanism runs inside
> the audited month and comes back from a save.

- **L101 1. the quote**
  - L111 a bank with capital makes a market
  - L112 with nothing on the desk the quote is fair value
  - L113 ...the ask half a spread over
  - L114 ...the bid half a spread under
  - L115 the position limit is the bank's capital's share, in shares
  - L121 long, it quotes under fair value by the pressure of its position
  - L122 ...and carries what it holds at that quote
  - L123 ...so the securities line is the inventory at the mark
  - L127 however long, the quote never goes under the floor
  - L131 a bank with no capital makes no market
  - L132 ...and can carry no position
- **L134 2. the emigrants**
  - L140 the fixture's households own the company
  - L147 twenty households leave with two thousand shares
  - L149 ...held abroad from the moment they go
  - L159 the desk buys them at the bid, and the cash leaves with the leavers
  - L161 ...out of the bank's cash
  - L162 ...onto the desk
  - L163 ...and nothing is held abroad any more
  - L164 the register still agrees with the households
  - L166 the closing quote reads the desk's new position
  - L167 ...and the bank carries the inventory at it
  - L168 the month's trading result is the cash paid against the mark
  - L170 ...and the bank's equity moved by exactly that
  - L183 with a dead bank there is no market
  - L184 ...so the leavers' shares stay abroad
  - L185 ...and the desk holds nothing
- **L187 3. the world**
  - L193 the fixture's company is wholly foreign-owned
  - L201 the fixture's yield at the bid is under the world's hurdle
  - L205 the world sells a share of its holding in proportion to the shortfall
  - L207 ...to the desk
  - L208 ...at the bid
  - L209 ...and nothing was sold to it
  - L218 the fixture's desk is quoting under fair value
  - L220 ...so the yield at the ask beats the hurdle
  - L225 the world buys in proportion to the excess yield
  - L226 ...from the desk
  - L227 ...at the ask
  - L228 ...and the bank has the cash
  - L236 a company with no record is not traded by the world: not sold
  - L237 ...and not bought
- **L239 4. the households**
  - L257 the fixture's Industry yields more than its Retail
  - L258 ...and both clear the deposit rate plus the premium
  - L261 the fixture's households want a month's share of what is past their cushion
  - L266 they go for the best yield first
  - L267 ...and take everything the desk has of it
  - L268 ...and put the rest into the next best
  - L269 ...so the whole month's money was spent
  - L270 ...out of their savings
  - L271 ...into shares of both
  - L272 ...the register agreeing on each
  - L273 the desk sold nothing it did not have
  - L274 what the best could not fill is remembered as demand
  - L276 ...and lifts its closing quote over fair value
  - L277 ...but not the mark: the desk does not book a gain on its own quote
  - L280 the demand fades by half a month
  - L286 with the deposit rate above every yield, nobody buys
  - L287 ...and nobody's savings moved
- **L290 4b. and when the model prices two of them the same**
- **L308 two companies the buyers cannot tell apart share the unmet demand**
  - L328 fixture: the valuation rule prices Industry to the discount rate
  - L330 fixture: ...and Materials to the same one, on a third of the shares
  - L332 fixture: ...so the two yields tie inside the dead band
  - L334 fixture: ...and the companies are not the same size
  - L340 fixture: the desk holds less than the month's money can buy
  - L346 the unmet demand is shared in proportion to the shares on issue, not given to one
  - L349 ...and the smaller company keeps its own
  - L352 ...so both quotes lift, not one
  - L354 the deepest of the tied names is the one the screen calls best
  - L377 a company's income moved by one ulp does not move who the demand goes to
  - L379 ...nor how much of it either of them carries
- **L383 5. the distress sale**
  - L403 each household sold shares for exactly what it was short
  - L404 ...at the bid
  - L405 ...and borrowed nothing
  - L406 the desk bought them
  - L407 ...for cash out of the bank
  - L408 ...declared as bought from the households
  - L409 ...and the households' total says the same
  - L418 a household with paper abroad sells that first
  - L419 ...keeping the rest there
  - L420 ...and its shares
  - L421 ...a financial inflow of what came home
- **L423 5b. the world's paper**
  - L431 a household sends a step towards its target abroad
  - L432 ...out of its savings
  - L433 ...declared as a financial outflow
  - L452 the next month it earns the world's rate
  - L456 when the bank pays more, a tenth comes home a month
  - L461 a household in debt sends nothing abroad
  - L465 twenty households leaving take twenty households' dollars, and nothing crosses the border
- **L468 6. the buyback, and the special dividend**
  - L474 the fixture's company is in a normal year
  - L481 the fixture: 8,000 at home
  - L482 ...1,000 on the desk
  - L483 ...1,000 abroad
  - L495 the fixture's market has the shares near fair value
  - L498 the fixture's world held still
  - L499 it retires a month of the pace, in shares
  - L500 ...paid from its till at the ask
  - L501 ...pro rata from the households
  - L502 ...from the desk
  - L503 ...and from abroad
  - L504 the households have the cash
  - L505 ...the bank has the desk's
  - L506 ...the abroad line has the world's
  - L507 ...and the shares are gone
  - L508 no special dividend was paid
  - L523 the fixture's buyers were left unfilled
  - L525 ...and the company retired shares while they were still cheap
  - L531 the next month's quote is past fair value by more than the tolerance
  - L534 so the same money goes out as a special dividend
  - L535 ...from the till
  - L536 ...to every holder
  - L537 ...and not one share is retired
  - L538 ...nor was one bought back this month
  - L548 a company in a bad year buys nothing back
  - L549 ...and pays no special dividend
  - L559 a company at its target buys nothing back
- **L561 7. the split**
  - L574 a share quoted at five hundred is split a hundred for one
  - L575 ...the register's count by the factor
  - L576 ...every household's count by the factor
  - L577 ...the quote by its inverse
  - L578 ...and what the households hold is worth what it was
  - L579 ...the last sale price with it
  - L580 ...the split factor remembers it
  - L581 ...so the price per FOUNDING share is what it was: the history has no cliff
  - L591 a share quoted at a ten-thousandth is consolidated ten thousand for one
  - L592 ...to a hundred shares
  - L593 ...at a dollar
  - L594 ...the households still agreeing with the register
- **L596 8. the save**
  - L600 it restores
  - L601 ...the quote
  - L602 ...fair value
  - L603 ...and the demand the quote carries
  - L606 ...and the split factor
  - L609 the exchange's first-night save, three a company, still restores
  - L610 ...with one share then being one share now
  - L611 ...closed until the bank says otherwise
  - L613 ...open once a bank with capital is put back
  - L615 ...and not for a bank without
  - L616 an array of the wrong length is refused whole
- **L618 9. a live city**
  - L654 the city's shares traded on their own
  - L655 ...and every month of it is conserved
  - L656 ...with the bank carrying the desk at the exchange's mark all along
  - L663 ...the register and the households agreeing on every company
  - L664 ...and the desk never short of anything
  - L666 the city saves
  - L671 ...and loads
  - L678 every quote and the demand it carries come back
  - L679 ...and the desk's inventory
  - L680 ...at the same mark
  - L681 ...and the households' dollars abroad

## FoodProcessingCheck.java - 21 labelled assertions

> The third of the shelf that arrives already made.
> 
> WHAT THIS HAS TO PROVE. Not that the eleventh sector makes money - whether it
> does depends on the city, which is the point of it, and the run that shows
> one is in claude/. What is checkable is the four things that cost this batch
> its four calibration passes, each of which was a real fault that a green
> suite did not catch:
> 
>   1. ALL THREE PLANTS CAN BE BUILT. The generic planner sizes a sector by one
>      good and skips every template that does not make it, so two of the three
>      were invisible: not refused, not scored badly, never considered. The
>      test is that the sector's own planner reaches each of them.
> ...

- **L96 1. the five goods it exists for**
  - L115 the sector is registered under its saved name
  - L118 ...and claims
  - L128 ...and does not buy back its own
- **L132 2. three plants, and all three reachable**
  - L138 the catalogue has all three
  - L155 the generic planner would only ever have seen one of them
- **L160 3. what a plant earns per person who runs it**
  - L185 ...and its payroll is a maker's share of what it sells
  - L199 the Meat Works is meat: the input bill is most of what it sells
  - L204 the Bottling Plant is wages and water: its crop bill is a rounding error
  - L206 ...and its water bill is not - it is the heaviest per dollar in the game
- **L210 4. the estimate: the price it leaves behind, and the tax**
  - L277 the fixture is a city that has been lived in
  - L287 ...and the sector still owns nothing in it, which is what these two rules are about
  - L303 a first
  - L325 twenty points on the sales tax moves what a plant is thought to earn
  - L329 ...and putting the rate back puts the estimate back
- **L333 5. the meat price is the bet, and it is real**
  - L352 meat at a farm's floor is worth more to a Meat Works than meat off a ship
  - L366 ...and the difference is most of what a Meat Works is worth
  - L379 nobody finances a Meat Works on meat nothing could pay for
  - L389 and every decision comes with a reason
- **L394 6. it survives a save, by name**
  - L408 the save loaded at all
  - L410 the sector's till survives the round trip
  - L413 ...and it is still the eleventh name in the equity register

## ForeignCheck.java - 133 labelled assertions

> The balance of payments, and whether the boundary it is drawn on is honest.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
> MoneyAudit has always tracked every flow across the city's edge. What it could
> not do was tell a HOUSEHOLD from a FOREIGNER - both sat outside the audited
> pools, for entirely different reasons - and the balance of payments is exactly
> the foreign half of that list.
> 
> So the question this file exists for is not "do the numbers look plausible".
> It is:
> 
> ...

- **L80 1. the rate is pinned**
  - L84 the exchange rate opens at parity
  - L85 ...so converting to local money changes nothing
  - L86 ...and back again likewise
  - L87 ...and a city with no history owes the world nothing
- **L91 2. a city that trades**
  - L181 a city that never intervened holds its founders' dollars, less the defence's
  - L183 ...and its record is the day-one purchase, less what the defence fetched
  - L186 ...however much it has traded
  - L188 ...so it has cover from the start, as they meant
  - L191 the fixture actually traded with anybody at all
  - L193 ...and imported, which every city does
  - L196 every dollar crossing the edge is domestic OR foreign, never both
  - L198 ...and the reserve is exactly the flows that built it
  - L212 household flows are counted, but not as trade
  - L233 ...and large enough that counting them as trade would swamp the balance
- **L236 3. across a reload**
  - L258 the fixture's position is actually worth carrying
  - L260 the cumulative balance reloads exactly
  - L262 ...and the vault, which is a different number
  - L264 ...in the dollars it is held in, exactly
  - L266 ...and they really are different
  - L268 ...and the trade record with it
  - L269 ...and the import bill the cover is measured against
  - L271 ...and the exchange rate
  - L285 the fixture has an openness worth carrying
  - L286 openness survives the reload
  - L287 ...and the pressure reading with it
  - L289 ...and the absorption
- **L292 4. nothing behaves differently**
  - L321 two runs of the same city agree on its population
  - L322 ...and on its treasury, to the cent
- **L324 5. every unit that leaves the shelf is paid for**
  - L418 the fixture is genuinely short of something
  - L420 ...and the shops actually traded
  - L422 every unit that leaves the shelf is paid for
  - L433 ...so the shops are not importing to replace goods nobody bought
- **L436 5b. reserves you sell are reserves you no longer have**
  - L467 buying reserves actually buys reserves
  - L472 ...and selling them spends them
  - L473 ...for exactly what was asked
  - L492 a reserve stock cannot be sold twice
  - L493 ...and what is left is nothing
  - L494 ...and it ran out when it should have
  - L500 asking for more than the city holds sells what it holds
  - L502 ...and never lends the difference into existence
  - L504 ...and a city with nothing sells nothing
  - L506 ...and the stock never goes negative through selling
  - L526 a treasury that never bought reserves absorbs nothing
  - L528 ...however long it has been trading
  - L532 ...and buying a comfortable buffer is what earns the damping
- **L535 6. the rate is bounded, and moves the right way**
  - L559 a sustained deficit weakens the currency
  - L569 ...and the pressure that did it reads as depreciation pressure
  - L579 a sustained surplus strengthens it
  - L580 ...and reads as appreciation pressure
  - L600 the rate stays a number
  - L601 ...and inside its bounds, every month of the way
  - L610 a pinned rate says it is pinned
  - L611 ...and does not move, however bad the deficit
- **L613 7. and it comes home**
  - L637 the fixture actually moved the rate somewhere
  - L645 balanced trade brings the rate back toward parity
  - L647 ...and most of the way home
- **L650 8. a devaluation improves the current account**
  - L707 the fixture trades enough for the question to mean anything
  - L796 the fixture sells food abroad at all
  - L799 the same programme costs the same in the world's money
  - L812 the fixture has a world price to move at all
  - L813 a 40% devaluation is a 40% rise in what an import costs
- **L838 9. the vault is held in dollars**
  - L867 rX of local money at rate r buys US$X, and US$X is held
  - L869 ...worth rX at home the day it is bought
  - L871 ...X over the dollar import bill, in months of cover
  - L876 the currency halves and the vault still holds US$X
  - L877 ...now worth 2rX at home
  - L878 ...and sellable for 2rX
  - L879 ...and the move is booked as the vault's revaluation: rX
  - L881 ...positive: a falling currency is a dollar vault's gain
  - L883 ...not a flow: the cumulative balance did not move
  - L885 ...nor the flows it is rebuilt from
  - L897 the bill struck at 2r, the cover is what it was at r
  - L902 sold at 2r, the whole vault raises 2rX of cash
  - L903 ...and leaves nothing, not even a division's rounding
  - L905 ...so the treasury is rX ahead: the revaluation it saw
  - L907 ...as the record says: bought for rX, sold for 2rX
  - L909 and the cumulative balance is still the sum of its flows
- **L912 10. and the move is not money anybody moved**
  - L941 fixture: the treasury bought dollars and the currency fell
  - L943 a month the currency falls in leaves the dollars alone
  - L945 ...and books dollars times the move as its revaluation
  - L947 ...and none of it appeared in the audit as money moving
- **L950 11. a reform does not reach the dollars**
  - L970 lopping two zeros leaves the vault's dollars alone
  - L972 ...and divides their worth at home by the same hundred
  - L974 ...so the cover does not move
  - L976 ...and the next valuation finds no move to book
- **L979 12. an older save**
  - L997 slot 19 holds the local value, as an older build reads it
  - L999 ...and slot 22 its dollars
  - L1003 a save of this shape reloads the dollars exactly
  - L1005 ...and what the currency did to them that month
  - L1008 ...and the first valuation after it books no phantom move
  - L1014 a 0.6.9 save's vault comes back at its saved rate
  - L1016 ...which is the same dollars it held that day
  - L1017 ...worth what the old build said they were worth
  - L1028 a save older than the vault's slot loads it empty
  - L1033 ...and so does a save with no foreign accounts at all
  - L1035 ...with no purchase on its record either
- **L1063 13. the vault defends, it does not hold down**
  - L1079 fixture: the deficit city is pushed weaker
  - L1080 fixture: the surplus city is pushed stronger
  - L1081 fixture: both trade a fraction of their output
  - L1083 fixture: both vaults are deep enough for the most damping
  - L1090 a push weaker is damped by the vault's cover
  - L1093 ...and the absorption it records is what was applied
  - L1099 the same vault passes a push stronger in full
  - L1101 ...and records that it absorbed nothing
  - L1102 ...though its cover could have absorbed the most there is
  - L1113 fixture: the support is half the trade term, unclipped
  - L1116 the rate's support comes off before the vault sees it
  - L1118 ...and the vault damps what is left
  - L1125 fixture: support larger than the trade term turns the push
  - L1127 support outweighing the deficit reaches the rate in full
  - L1144 fixture: the page's push is not the one the month recorded
  - L1147 previewing the push leaves the month's pressure as recorded
  - L1149 ...and its absorption
  - L1150 ...and previews exactly what the month will apply
  - L1152 ...which is the month's own call, and records what it applied
- **L1170 and land bought by converting pushes the rate as reserves bought would**
  - L1184 fixture: the city is pushed weaker
  - L1192 converting pays the parcel's dollars at today's rate
  - L1193 converting for land pushes the rate as buying the dollars for the vault does
  - L1195 ...which is what buying nothing pushes: a treasury's dollars are the financing item
  - L1197 ...and it leaves the vault where it began
  - L1198 ...where the reserve purchase leaves it the parcel's dollars fuller
  - L1200 ...and nets to nothing in the intervention record
  - L1202 ...and books no reserve purchase for the month
  - L1204 ...while the seller was paid the parcel's dollars
  - L1210 ...so what the vault would absorb is the control's, not the reserve buyer's
  - L1215 paid from the vault, the dollars leave it
  - L1216 ...and the record falls by their local price, as a sale's would
  - L1218 ...and a vault asked for more than it holds pays what it holds
  - L1220 ...and is empty
  - L1222 the land's month, struck: every dollar paid for it
  - L1224 ...of which out of the vault
  - L1228 ...and the struck month and the lifetime survive a save
  - L1234 ...and a reform does not reach them: they are dollars
- **L1350 ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN**

## ForeignDebtCheck.java - 66 labelled assertions

> Borrowing in somebody else's money.
> 
> ORIGINAL SIN, which is the name the literature gives the thing this file is
> here to prove works: a city that cannot borrow abroad in its own currency owes
> dollars and earns local money, so a devaluation makes the debt dearer without
> anybody having borrowed another cent. Domestic debt does the opposite -
> inflation and devaluation quietly shrink it.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the instrument speak two currencies HONESTLY? The contract is in
>      dollars and does not move; the city's books are in local money and must.
> ...

- **L92 1. the instrument speaks two currencies**
  - L99 at parity the two are worth the same
  - L104 the dollars owed do not move
  - L105 ...but what they cost at home does
  - L107 ...and the local bond has not noticed a thing
  - L124 the coupon revalues too
  - L133 ...and every payment still to come
  - L137 a nonsense rate is refused rather than applied
- **L140 2. and the world is cheaper, to begin with**
  - L214 a city with no foreign debt owes no risk premium
  - L216 ...so the world's money starts cheaper than the city's own
  - L218 ...and the window is open
- **L220 3. the books balance with dollars on them**
  - L233 the bond was actually issued
  - L234 ...and the city now owes dollars
  - L247 the bank did not buy a cent of it
  - L271 every month of servicing a dollar bond reconciles
  - L280 ...and the fixture really did service it
- **L283 4. original sin**
  - L291 the fixture has a debt worth revaluing
  - L299 not one dollar more is owed
  - L300 ...and half again as much at home
  - L302 ...which is exactly what the revaluation says it is
  - L330 the fixture is not already against its cap
  - L332 a weaker currency makes the NEXT bond dearer too
  - L344 ...and none of it appeared in the audit as money moving
- **L347 4b. and where the dollars actually went**
  - L394 converting puts the whole proceeds in the treasury
  - L395 ...and leaves the reserve position alone
  - L397 holding puts the whole proceeds in reserves
  - L398 ...and the treasury does not see a cent of it
  - L418 parking borrowed dollars does not read as getting richer
- **L421 5. the window shuts**
  - L426 a city that owes nothing can always borrow
  - L432 ...and one drowning in dollars cannot
  - L433 ...and is told why in words
  - L437 earning its way out reopens the window
  - L442 owing dollars and selling nothing shuts it too
- **L445 6. and the price of walking away**
  - L459 everything owed abroad is written off
  - L460 ...so the city owes nothing abroad
  - L461 ...and the position improves by the whole of it
  - L463 ...recorded as what it is: money nobody was paid
  - L466 the window is shut
  - L467 ...for five years
  - L469 ...and the price carries a scar
  - L481 debt-to-exports now says the city is spotless
  - L483 ...and it still cannot borrow a dollar
  - L494 ...and says so when asked
- **L496 7. across a reload**
  - L540 the fixture has foreign paper worth carrying
  - L542 the dollars owed reload exactly
  - L556 ...valued at the rate the city actually has, immediately
  - L559 ...and the exchange rate reached the instruments
  - L562 the scar reloads
  - L564 ...and it is a scar worth reloading
  - L569 fixture: the live city's window is shut BY THE DEFAULT CLOCK
  - L572 ...so the window is still shut on the reloaded city
  - L574 ...and shut for the same reason, not a different one
  - L577 the lifetime revaluation reloads
  - L579 ...and what the city walked away from
  - L598 a reloaded city does not book its whole history as one month
- **L610 and the world's paper is priced on the world's curve**
  - L620 fixture: the world will lend
  - L631 fixture: a dollar twenty-year on the books
  - L639 the world's curve is the foreign rate plus the city's own term premium table
  - L641 ...one shape for both currencies, fifty years over ten as on the city's curve
  - L644 a dollar twenty-year is valued on the world's curve
  - L646 ...not at the city's own rate, which is a different figure
  - L648 ...and it was issued at the rate it is valued at: the round trip is neutral by construction
  - L655 fixture: the dial moved the city's own rate
  - L656 the dial moving does not move a dollar bond's value
  - L663 fixture: a city earning a quarter as much abroad is charged more for it
  - L665 ...and the world charging the city more moves it: down
  - L668 ...and back when the city's standing is

## GdpCheck.java - 69 labelled assertions

> Verifies the national accounts: the identity, growth rates, and the government's books.

- **L24 1. the identity**
  - L31 consumption
  - L32 investment (200 built + 1000 stock built up)
  - L33 government
  - L34 net exports
  - L35 GDP
  - L36 identity holds
- **L41 2. stock is a CHANGE**
  - L46 unchanged stock adds nothing
  - L47 GDP without the one-off stock build
  - L51 stock drawn down subtracts
- **L53 3. THE BUG THIS REPLACES**
  - L60 GDP is positive despite sector losses
  - L66 a city living on imports does read negative
- **L68 3c. the two ways it went negative anyway**
- **L74 a bulk order is not negative output**
  - L84 the quiet month is positive
  - L97 ordering is not output, and not an import either
  - L105 a month of building reads the value the builders added
  - L107 ...which is positive
- **L109 the materials plant's shed is stock; the city's yard is not**
  - L129 a plant filling its shed is production
  - L130 ...and GDP rises by exactly that
  - L132 ...while a price move on the same stock is nothing
  - L135 ...and selling it into a contract runs the term the other way
  - L152 a shop stocking its first stockroom books it as production
  - L154 ...so a month that is nothing but the import of it is not negative output
  - L164 ...and a month of selling and restocking scores the margin alone
  - L166 ...which is what a retailer adds
  - L173 ...and running it down runs the term the other way
- **L176 a price move is not production**
  - L190 an unchanged warehouse contributes nothing when the price moves
  - L192 ...and output stays positive
  - L196 100,000 more units at $0.75 is $75,000 of production
- **L199 a legacy save sets the baseline, it does not spend it**
  - L209 the first month after a legacy load books no inventory swing
  - L214 ...and the next month measures against it
  - L221 a new city's first stock IS production
- **L224 3b. exports, the first the city has ever had**
  - L232 exports
  - L233 raw material imported
  - L234 net exports is the difference
  - L235 ...and that is the whole of GDP here
  - L236 a city that exports more than it imports reads positive
  - L243 selling below the cost of the input subtracts
  - L249 every import counted
  - L250 ...and none of them is output
- **L252 4. annual and per capita**
  - L259 twelve months of 100
  - L260 trend equals the month when flat
  - L261 per capita over 100 people
  - L262 no population -> no divide by zero
  - L268 annual uses the last twelve only
- **L270 5. growth**
  - L274 no history -> no growth
  - L277 one month -> still nothing to compare
  - L281 2%/mo annualised
  - L282 ...which is more than 12x the monthly rate
  - L287 a halved month is negative growth
  - L291 year on year needs 13 months
  - L293 13th month against the 1st
- **L295 6. government books**
  - L304 total revenue
  - L305 land sales are revenue
  - L306 property tax is its own line
  - L307 total expenditure
  - L308 land bought is an expense
  - L309 deficit
  - L310 a deficit is negative
  - L315 a land boom leaves GDP alone
  - L319 revenue to GDP
  - L320 debt to GDP
  - L321 no debt -> zero
  - L324 no output -> no ratio blow-up
  - L325 ...nor for revenue
- **L327 4. THE INTEREST LINE IS REAL**
- **L341 the city's interest reaches its own accounts**
  - L375 fixture: the city really did issue a coupon bond
  - L376 THE INTEREST REACHES THE ACCOUNTS AT ALL
  - L378 ...and it is the coupon, not some other number
  - L380 ...so it survives budgetPie(), which drops a zero slice
  - L391 fixture: saved
  - L392 and a reloaded city reports the same interest

## HealthCheck.java - 230 labelled assertions

> Sickness: what it moves, and - much more importantly - what it does not.
> 
> The specification was one sentence: "it modifies the fillrate, but doesnt
> reduce workforce." Almost every assertion here is a way of saying the second
> half of that, because the second half is what a plausible implementation gets
> wrong. Cutting the workforce would have been a smaller change and would have
> looked identical on the output line - and would have quietly cut the wage
> bill, the wage tax, the households' income and the rent they can afford, none
> of which anybody asked for.
> 
> NUMBERS ARE COMPARED AGAINST THE MODEL'S OWN CONSTANTS, not against literals.
> That rule has been earned five separate times in this codebase: an assertion
> ...

- **L63 1. the buildings know what they treat**
  - L74 a hospital is general care
  - L75 a daycare is childcare
  - L76 a nursing home is senior care
  - L77 a house treats nobody
  - L83 \"Home Daycare\" and \"Nursing Home\" are not the same care
  - L92 something in the catalogue does
- **L97 care capacity**
  - L106 a new city has the doctor it was founded with
  - L109 ...and a churchyard
  - L112 ...but nobody founds a city with a crematorium
  - L121 the childcare endowment is the pyramid's own share of the founding city
  - L137 ...and senior care's, weighted across both retired bands
  - L140 an elder needs a whole place and a senior a fraction of one
  - L144 ...both less than general care, which serves everybody
  - L162 two hospitals, on top of it
  - L165 ...and the daycares are not counted with them
  - L171 a hospital under construction treats nobody
- **L174 2. coverage sets the baseline**
  - L179 no beds at all
  - L183 beds for everybody
  - L187 half covered is halfway between
  - L193 surplus beds do not go below the floor
  - L199 a city with nobody in it is fully covered
  - L200 ...and is not mid-plague
- **L202 3. outbreaks actually happen**
- **L208 outbreaks, over three hundred years**
  - L225 outbreaks happen at all
  - L229 ...about as often as the chance says (
  - L231 ...and each one lasts more than a month
  - L233 ...but none of them is permanent
  - L234 no month ever loses more than the cap
- **L238 an outbreak ends**
  - L246 found an outbreak to follow
  - L263 it ended
  - L264 ...after more than one month
  - L265 and the city is back to its baseline
- **L269 what coverage buys**
  - L281 coverage takes the edge off an outbreak
  - L282 ...but does not prevent one
  - L283 ...by exactly the mitigation it claims
- **L286 4. the same month rolls the same way**
- **L293 not save-scummable**
  - L302 two cities living the same months get the same illness
  - L309 saving mid-outbreak
  - L312 the save was accepted
  - L313 the outbreak came back
  - L315 ...and so did the rate the month was throttled by
  - L321 a malformed array is refused
  - L322 ...and nothing was half-read
  - L323 a save from before sickness is refused too, not read at an offset
- **L326 5. THE POINT: output falls, nobody does**
  - L361 a city with no clinics is already ill
  - L394 fixture: the shops sold something at full health
  - L404 the workforce is unchanged
  - L405 the population is unchanged
  - L406 the wage bill is unchanged
  - L407 the employer still pays the full payroll
  - L423 ...and the shops hand over fewer baskets by exactly the sick rate
  - L427 the mills run slower by the same share
  - L443 the sites were not moved by the economy's ratio
  - L455 beds for everybody speeds the sites up
  - L459 ...by exactly the difference in the sick rate
- **L463 a city, played and reloaded**
  - L481 clinics gave the city some coverage
  - L482 ...so it is healthier than an untreated one
  - L484 ...but not perfectly healthy
  - L491 the sick rate came back
  - L493 ...and the outbreak with it
  - L495 ...and the coverage the month was priced at
  - L504 and the sectors were told about it on the load path
  - L511 ...including the statement it struck under it
- **L515 6. an unstaffed hospital treats nobody**
  - L530 fully staffed, a hospital treats its whole capacity
  - L537 with nobody at all, only the founding doctor is left
  - L548 ...and losing only its doctors costs it exactly their share of the posts
- **L555 7. what care does to mortality**
  - L565 half-covered is exactly today's rate:
  - L569 no childcare at all
  - L571 childcare for everybody
  - L580 no general care at all leaves the adults' rate alone
  - L582 ...and so does general care for everybody
  - L584 ...and the teenagers' the same
  - L586 no senior care at all
  - L593 children are the drastic ones
  - L595 ...and seniors are the gentlest
  - L599 general care does not also treat babies
  - L601 ...nor seniors
  - L607 
  - L612 no childcare, no bonus
  - L613 childcare for everybody doubles it
  - L634 ...and the pyramid gets that many more babies
  - L655 a city with childcare loses far fewer infants
  - L657 ...and has far more of them
  - L665 a cared-for city keeps more of its babies
  - L667 ...and more of its seniors
- **L670 8. death care**
  - L678 with savings and plots, everybody is buried
  - L679 ...and nobody is cremated
  - L680 ...and the plots are gone for good
  - L681 ...and the city collected the burial fee
  - L687 with no savings, everybody is cremated
  - L688 ...and the ground is untouched
  - L689 ...which is the cheaper funeral
  - L695 a full cemetery sends the rest to the oven
  - L696 ...which takes them
  - L697 ...and nobody is left waiting
  - L701 a busy crematorium sends the rest to the ground
  - L703 ...even though nobody could afford a plot
  - L704 ...and nobody is left waiting
  - L709 with neither, they all wait
  - L710 ...and nothing was collected
  - L714 a new cemetery clears the backlog and the month together
  - L716 ...leaving nobody
  - L721 a city that never builds one stops counting after two years
- **L725 and it makes people ill**
  - L732 leaving them where they fell costs output
  - L733 ...by the weight it claims
  - L738 however many there are, it is capped
- **L741 9. senior care draws people in**
  - L744 no senior care, no bonus
  - L745 full coverage, the full draw
  - L758 senior care raises the target by exactly the pull
- **L762 10. and somebody pays for all of it**
  - L804 the service costs something
  - L805 ...most of which is wages
  - L821 ...and it is a NET DEFICIT business, per the spec
  - L823 ...with fees nowhere near funding it
  - L826 the treasury is billed for it
  - L828 ...and it is on the city's expenditure list
  - L830 ...and the fees are on its revenue list
  - L838 and the households paid exactly what the city collected a month ago
  - L840 ...which is not the same as this month's, so the test means something
  - L847 ...and the seven tiers add back up to it
  - L851 healthcare is counted as government output
  - L862 a bigger bill is a smaller surplus, penny for penny
  - L874 the graves came back
  - L876 ...and the backlog
  - L878 ...and the bill the city was paying
  - L881 a save from before healthcare had books is refused whole
- **L884 11. a skip cannot hide an epidemic**
- **L892 a skip reports what it lived through**
  - L908 the skip noticed the epidemics
  - L909 ...and counted every month of them
  - L911 ...and kept the worst month, which the endpoints cannot show
  - L913 an untreated city is below full every single month
  - L918 a healthy month reports no outbreak
  - L919 ...and nothing left unburied
- **L921 12. the fee has a dial, and the funerals do not**
- **L930 the price at the door: the fee scale**
  - L934 the scale multiplies general care's fee
  - L936 ...and childcare's
  - L938 ...and senior care's
  - L940 ...and NOT the burial fee
  - L941 ...nor the cremation fee
  - L942 the founding fee is still the founding fee, unscaled
  - L945 the dial stops at its ceiling
  - L947 ...and at nothing
  - L949 a new city charges the founding fee
  - L951 ...and no premium
  - L953 the policy clamps the scale to the same ceiling
  - L956 ...and the premium to its own
  - L970 at 0 nobody pays for treatment
  - L971 ...and everybody is still treated
  - L972 ...and the funerals still charge
  - L973 ...so the fees are the funerals alone
  - L974 ...and nobody was priced out by a fee of nothing
  - L986 the break-even scale is the gross cost over the fees at 1x
  - L987 ...and this ward's is inside the dial
  - L990 at the break-even scale the fees meet the gross cost
  - L992 ...and the net cost is nothing
  - L995 below it the service loses money
  - L996 ...half the fees, at half the scale
  - L999 above it, it is a business
  - L1000 ...and the recovery rate says so
  - L1009 fixture: the city's break-even is inside the dial
  - L1014 the policy's scale reached the service
  - L1015 at the city's break-even the fees at full service are within a month's drift of the cost
  - L1020 ...at half of it the service loses money
  - L1021 ...by about half the cost
- **L1031 13. who can afford the clinic**
- **L1044 who can afford the clinic**
  - L1056 a household with room pays its whole care bill
  - L1058 ...and skips nothing
  - L1062 ...and so does one whose room is exactly the bill
  - L1070 a household short of a basket pays only what fits after eating
  - L1072 ...which is the share of its people the clinic will see
  - L1073 ...and the rest of the bill is what it eats instead
  - L1075 ...so the bill it does pay leaves the basket whole
  - L1081 a household with nothing pays nothing
  - L1082 ...and none of its people are served
  - L1083 ...whatever the fee
  - L1085 ...and with a fee of nothing it is served in full
  - L1094 a household that skipped last month's bill is judged on the full one
  - L1099 ...and settles where it pays what it can, month after month
  - L1102 ...rather than swinging between served and starving
  - L1194 fixture: the poor city has somebody at the eat-less step
  - L1196 fixture: at the dial's top the fee priced somebody out
  - L1198 at a high fee a poor city serves a smaller share of its people than at the founding fee
  - L1207 ...and its baseline sick rate, which coverage sets, is higher for it
  - L1229 ...and it buries more of its people over the run
  - L1230 ...its old first, whom a fee on senior care turns away
  - L1231 with the fee at nothing the same city is served in full, every month
  - L1233 ...every kind of care
  - L1236 ...and the households who skipped a bill ate with it: the dear city is no hungrier than the free one by more than the price of care
  - L1242 
  - L1244 ...and the served are the offered times the share who could pay
  - L1252 the treatment fees are charged on the people treated
  - L1253 ...which is less than the same beds would raise at full service
  - L1261 the buildings' upkeep is the same at a dear fee as at the founding fee
  - L1263 ...and the service still costs money to run
- **L1266 14. the unchanged case, at zero tolerance**
- **L1278 the unchanged case, at zero tolerance**
  - L1291 in a city that can pay, every household paid its whole care bill
  - L1293 
  - L1295 ...and the served are the offered, exactly
  - L1298 ...and the fees charged are the fees at full service, exactly
  - L1300 ...and the households were billed exactly what the city collected a month ago, as before
  - L1302 ...and nobody was priced out
  - L1303 ...and no bill was skipped
- **L1305 15. the premium**
- **L1313 the premium**
  - L1321 the premium raises exactly the rate times the wage bill
  - L1323 fixture: which is money
  - L1324 ...on the same base as the EI premium
  - L1326 ...and it is on the government's revenue list
  - L1329 ...inside the revenue total
  - L1333 ...and reaches the treasury's cash, penny for penny
  - L1341 the households' statement shows the premium the city collected a month ago
  - L1347 ...and the rows add back up to it
  - L1348 ...off the wages, so the retired pay none
  - L1350 ...and it comes off take-home, like the EI premium
  - L1356 the money audit saw it as a household-to-treasury flow
  - L1383 
  - L1386 the insured city charged no treatment fee
  - L1387 ...and the fee-funded one did
  - L1388 the insured city's wage earners paid a premium
  - L1390 ...and the fee-funded city's paid none
  - L1391 the gross cost is the same either way: a ward is paid for whether or not its patients are
- **L1394 16. both dials survive a save, and a reform**
  - L1404 the fee scale came back
  - L1405 ...and the premium
  - L1406 ...and the service charges at the reloaded scale
  - L1407 ...and the full-service bill the next strike reads came back
  - L1415 a save from before the dials is still read
  - L1416 ...at the founding fee
  - L1417 ...with no premium
  - L1420 ...and so is the service's state from before the full-service bill
  - L1422 ...which reads the bill it charged as the bill at full service
  - L1424 ...and a coverage of 1 until a month strikes it
  - L1429 ...and the state from before the coverages were kept
  - L1430 ...which reads the full bill at 1x it carried
  - L1431 the reloaded service kept the coverage the month read
  - L1433 ...which is the figure the sick rate read, not the beds
  - L1440 a currency reform leaves the fee scale alone
  - L1441 ...and the premium
  - L1445 ...and the service's scale
  - L1446 ...while its fees move with the money

## HistoryCheck.java - 36 labelled assertions

> Verifies the graph history: recording, alignment, and the round trip. Not
> part of the game.
> 
> WHY THIS EXISTS
> 
> The history went from eight series to twenty-three, and every one of the
> three ways that can go wrong is silent.
> 
> 1. A SERIES THAT IS NEVER RECORDED looks exactly like one that is, until you
>    open the graph and it is empty - which is a long way from where the
>    mistake was made.
> 2. A SERIES MISSING FROM restoreFrom() records perfectly all session and
> ...

- **L66 1. every series is recorded, every month**
  - L76 the axis has a point per month lived
  - L88 there are more than the original eight
  - L97 
  - L101 
  - L104 the companies' share prices are among them
  - L108 the bank, listed at the founding, has a price in the last month
  - L124 
- **L127 2. it survives a save and a reload**
  - L130 the city saved
  - L136 the axis came back whole
  - L160 
- **L165 2b. every sector's two series (0.7.4)**
- **L174 every sector's net income and workers, every month**
  - L184 
  - L190 ...its last month's net income is SectorBooks' for that month
  - L192 ...and its last month's workers are the sector's posts filled
  - L198 ...and both came back from the save
  - L203 every sector in the city has its two series
  - L204 ...and some sector earned or lost something, so the income is read
  - L205 ...and some sector employs somebody, so the workers are read
- **L207 2c. GDP's four parts (0.7.6)**
- **L217 GDP's four parts, every month, adding up**
  - L220 the city's own accounts: C+I+G+NX is this month's GDP
  - L226 
  - L241 C+I+G+NX is the month's kept GDP to the cent, every month
  - L242 ...and the households bought something, so C is read
  - L244 the last month's parts are the accounts' own, to the cent
  - L254 a rolling year of the four in founding money is the year of real GDP
  - L257 ...and no year before twelve months of them
- **L260 3. A SHORT SERIES LINES UP WITH THE END**
- **L275 a series added late lines up with the END**
  - L283 the fixture actually removed the series
  - L294 the axis is untouched by the missing series
  - L296 ...and the missing series is empty, not absent
  - L301 aligned() still returns a full-length array
  - L312 ...reading as NaN - not-recorded is not the same as zero
  - L323 only the new months have the series
  - L326 aligned() covers the whole axis
  - L329 month
  - L336 ...and the LAST four months carry the data
- **L338 4. the derived series do not divide by zero**
- **L345 derived series on a city with nobody in it**
  - L358 month
- **L362 5. a new game forgets it**
  - L366 the axis is empty
  - L369 

## HoldersCheck.java - 79 labelled assertions

> Proves who holds the city's own paper (0.7.1): that the households buy it at
> the settle, are paid on it, sell it back, and are paid when it is bought
> back - every crossing declared, and every holding exactly where the paper
> says it is. Not part of the game.
> 
> WHY THIS EXISTS. Jerus: "yes households should be able to hold." Until
> 0.7.1 the commercial bank held every dollar of the city's paper, so a bond
> was a loan from the city's own bank with extra steps. Now a piece of paper
> carries what the households and the central bank hold of it, and the
> households carry their paper as a fourth asset. Two books that must agree
> to the dollar, and a set of crossings - households are outside the money
> audit's pools - each of which is a dollar from nowhere if it is not
> ...

- **L125 1. at the settle**
  - L131 fixture: households with savings past the cushion
  - L139 fixture: the treasury sold a twenty-year bond between the presses
  - L146 fixture: its yield is well above the deposit rate
  - L148 fixture: and the households can pay for their share
  - L158 the households took the share the dials give an issue this far over the deposit rate
  - L160 ...which is the most they take, MAX_HOUSEHOLD_PAPER_SHARE, or the spread times
  - L163 their savings fell by exactly what they paid
  - L164 ...for the face the issue's price buys: face times cash over received
  - L166 the bank paid exactly the rest
  - L167 ...and holds exactly the rest of the face
  - L169 the paper's household share is the sum of every cell's paper
  - L171 ...and the treasury is owed nothing more for it
  - L172 the audit closed on the settle month
- **L174 2. the coupon**
  - L180 the households were paid their share of the coupon
  - L182 ...into their savings, as investment income
  - L183 the bank's share is on the month's interest bill, which it takes at the next settle
  - L185 (fixture: the paper income was cleared and re-paid, not carried)
  - L186 the two books still agree
- **L188 3. the waterfall**
  - L211 short by less than its paper is worth, it sells paper and nothing else
  - L213 ...for exactly what it was short
  - L216 short by more, it sells all of its paper first and its shares after
  - L218 ...the shares covering what the paper could not
- **L221 4. selling after the curve rose**
  - L228 the month's ratio - their book at the curve over its face - is under face, the rate
  - L238 fixture: they sold some
  - L239 ...a little of it: HOME_SPEED of what the households who decide hold (a prisoner's stays put)
  - L241 ...for less than face: its face at the month's ratio
  - L242 ...into their savings
  - L243 the bank paid it
  - L244 ...its book rose by the face
  - L245 ...and it booked the gain against what it carries the paper at
  - L247 ...a gain
  - L248 the two books still agree
- **L251 5. a buyback**
  - L255 fixture: three holders - the households, the central bank and the bank
  - L265 fixture: the buyback was at the quoted price
  - L266 the households' share of the price went to their savings
  - L268 ...and their paper is gone with the bond
  - L269 the bank's share to its cash
  - L270 ...its gain against what it carried its face at
  - L272 the central bank's face came off its book
  - L273 ...and its share of the price waits for the month to destroy it
  - L275 ...held in the treasury's pool until then, with the households'
  - L278 the next month declares what the households were paid
  - L280 ...and the central bank destroys its share
  - L281 ...so nothing is carried any more
  - L282 the audit closes on it
  - L283 ...and M0 moved by exactly the money made, the redemption among it
  - L285 the central bank kept nothing: its gain against face is in the month's profit
- **L288 6. the save**
  - L294 fixture: paper held by all three again
  - L307 every cell's paper, and everything else in the cell, came back exactly
  - L316 every piece of paper's holders, what it is owed for and its discount came back
  - L318 ...the households' paper ratio
  - L319 ...the central bank's book
  - L320 ...and the bank's unearned discount, re-derived from the paper
  - L322 ...and the two books agree in the reloaded city
  - L324 a month on, both settle the paper still owed for the same way
  - L326 ...and pay the households the same coupons
  - L328 ...and both months close
- **L330 7. an old save**
  - L338 fixture: the save carries the cells
  - L340 fixture: at today's width
  - L365 it loads
  - L366 the households hold none of the city's paper
  - L367 ...nor the central bank
  - L368 ...so the bank holds everything
  - L370 ...and its book says so
  - L371 the dial reads nothing
  - L374 ...and it runs, the audit closing every month
  - L375 ...with the two books agreeing
- **L377 8. a dollar bond bought back**
  - L382 fixture: the treasury sold a twenty-year dollar bond
  - L384 fixture: ...still owed a month on
  - L390 fixture: bought back at the quoted price
  - L391 fixture: ...which cost something
  - L392 the treasury paid it
  - L393 ...and owes the world nothing on it
  - L394 none of the price went to the bank
  - L395 ...nor to the households
  - L396 the treasury's pool carries it until the month declares it
  - L399 the next month declares the whole price leaving the country
  - L401 ...so nothing is carried any more
  - L402 the audit closes on it

## HouseholdCheck.java - 269 labelled assertions

> Verifies the residents' books and the demolition log.
> 
> The household statement is the last missing side of this economy's ledger, so
> what matters most here is that it is the OTHER SIDE of figures that already
> exist rather than a second, differently-computed version of them. If the
> people can be shown paying a different rent from the one landlords are shown
> receiving, the statement is worse than useless.

- **L37 1. the statement**
  - L45 wages
  - L46 wage tax
  - L47 take-home
  - L48 rent
  - L49 shopping
  - L50 total spending
  - L51 saved
  - L55 saving rate is on take-home
  - L56 effective tax rate
  - L57 rent burden
- **L59 2. spending more than they earn**
  - L68 spending over take-home is flagged
  - L69 ...as a negative
  - L70 ...and a negative saving rate
  - L71 rent over a third of income
  - L78 nothing left
  - L79 ...but not a shortfall
- **L81 3. accumulating**
  - L88 a year of saving 150
  - L89 the month itself is still just one month
  - L96 two years of losing 250
  - L97 cumulative can go negative
- **L99 4. per head**
  - L102 income per resident
  - L103 spending per resident
  - L104 average filled job pays
  - L105 people per worker
  - L110 no people -> no income per head
  - L111 no workers -> no average wage
  - L112 no dependency ratio either
  - L113 no income -> no saving rate
  - L114 ...nor a rent burden
  - L115 no wages -> no tax rate
  - L120 reset clears the running total
  - L121 ...and the month
- **L123 5. it is the other side of consumption**
  - L136 what the people paid in rent
  - L137 what they spent in shops
  - L138 household spending IS consumption
- **L141 6. the demolition log**
  - L145 nothing lost yet
  - L146 ...and nothing to show
  - L149 recorded
  - L152 quantity
  - L153 building
  - L154 sector
  - L155 the city paid for the plot
  - L157 this month
  - L158 last month
  - L159 three months ago
  - L160 months ago
  - L163 never negative
  - L167 abandoned plot
- **L169 7. it fades out**
  - L172 still visible after a year
  - L173 still there at the limit
  - L175 gone a month later
  - L177 ...but not forgotten by all()
  - L180 newest first
  - L187 nothing nonsensical stored
  - L194 the log is capped
  - L195 and keeps the newest
  - L199 cleared
- **L201 8. the same books, per tier**
  - L240 the tiers' wages add up to the city's
  - L241 ...and their tax
  - L242 ...and their rent
  - L243 ...and their shopping
  - L244 everybody is in exactly one row
  - L262 rent follows front doors
  - L264 the shop follows heads
  - L266 ...and the two are not the same split
  - L280 rent per door is the city's rent over its households
  - L282 the shop per head is its shopping over its people
  - L292 pensioners earn nothing
  - L294 ...so their row is a deficit
  - L296 ...and it is labelled as theirs
  - L301 a malformed split is refused
  - L324 the tier split IS the wage tax
  - L330 ...and a flat rate on the total is NOT the same number
- **L333 9. rent is per home, not per head**
  - L363 a House is at least the home the rent target is struck against
  - L365 ...and it is the biggest home in the game
  - L368 ...so it costs more than the reference home does
  - L370 ...in a single dwelling
  - L378 a home is charged for its size, not its occupants
  - L380 the average home here holds four
  - L386 a working couple pays the burden the price was set for
  - L388 ...and a family of six pays exactly the same, not three times it
  - L400 half let, half the rent
  - L403 nobody home, no rent
  - L408 a crowded city still only has 400 rents to pay
- **L411 10. pensions**
  - L421 the pension follows the wage table, not a typed-in figure
  - L424 contributions are a slice of the wage bill
  - L427 ...and it really is only a tad
  - L441 an ageing city covers less of its own pension bill
  - L443 a city with no pensioners owes nothing
  - L445 ...and is fully covered by definition
  - L447 the shortfall is what contributions do not reach
  - L476 fixture: a hundred student households
  - L477 one of them is an adult and their dependants
  - L478 ...so the cell is a hundred and sixty people
  - L479 a student with children is still ONE wallet
  - L480 ...so the row's income is split by wallets and they take home a whole one
  - L486 a student with nobody is one person, as they always were
  - L512 fixture: two out-of-work cells, one of them with a child in it
  - L514 the claimant with a child is billed for two heads
  - L516 ...and the one without a child for one
  - L518 ...so the row's bill is still all of it and no more
  - L530 the benefit goes to the claimants, and a child does not draw one
  - L532 ...so a household whose EI has run out draws nothing, child or no child
  - L556 every one of a
  - L558 ...and the row split weighs them all
  - L580 a senior living alone draws one pension
  - L581 ...and an elder living alone draws the same one
- **L585 THE BUDGET CONSTRAINT**
- **L597 a household short of money spends its savings, then borrows**
  - L631 a founding household is not destitute on day one
  - L632 ...by about the buffer the dial names
  - L638 SAVINGS GO FIRST
  - L639 ...and nothing is borrowed while there are savings
  - L647 the savings run out
  - L648 ...THEN THEY BORROW
  - L649 ...at a rate over the risk-free one
  - L651 ...which climbs with what they already owe
  - L653 ...and never past the cap
  - L679 the debt stops at the ceiling, it does not run away
  - L685 ...AND THEN THEY EAT LESS
  - L686 ...in most months, not just on average
  - L687 ...which the health service can see
- **L689 and a household with money to spare pays it down, then banks it**
  - L698 a household with a surplus banks it
  - L699 ...and owes nothing
  - L700 ...and is not hungry
  - L708 a rich household WANTS more than a basket
  - L710 ...but a poor one wants exactly a basket, not less
- **L713 the supply side starves people who had the money**
  - L721 empty shelves are hunger even in a rich city
- **L724 SIXTY-EIGHT CELLS, AND THE MONEY FOLLOWS THE PEOPLE**
- **L736 every cell has its own books, and they sum to the rows**
  - L767 seventy-eight cells: eleven working shapes by six tiers, four retired, eight outside the families
  - L773 a single adult takes home one wage
  - L774 a couple takes home two
  - L775 a large family, two earners, takes home two
  - L776 a pensioner draws the pension
  - L777 a working cell is a WorkingHousehold
  - L778 a retired cell is a RetiredHousehold, with no tier
  - L780 the cells' take-home sums to the row's
  - L783 the buffer is per household of the CELL, not of the tier
  - L785 ...so a single adult opens with half a couple's
  - L787 the row's per-household figure is the cells' weighted average
  - L790 sum() adds up every cell
  - L792 ...and the households in every cell are the city's
  - L797 forEach touches every cell and changes nothing at scale one
- **L801 the money follows the people**
  - L821 fifty new families arrived with the buffer
  - L829 the child's cell is empty
  - L830 ...the teen's cell holds them
  - L831 ...WITH THEIR MONEY: the teen's family opened with what the child's had
  - L833 nothing was written off for a birthday
  - L834 ...and nothing left the city
  - L849 five singles sharing carry FIVE wallets into the flatshare
  - L851 ...and the singles who stayed have what they had
  - L853 nothing was written off for a lease
  - L854 ...and nothing left the city
  - L869 a couple promoted to skilled brings its savings up the ladder
  - L871 ...and nothing left the city
  - L895 the fixture saved something - or the next line proves nothing
  - L900 twenty households leaving take twenty households' savings with them
  - L902 ...and nobody wrote anything off, because they owed nothing
  - L904 ...and the ones who stayed opened the month with what they had
  - L914 the fixture reached a debt - or the next line proves nothing
  - L920 twenty households leaving in debt write it off against the bank
  - L922 ...and the ones who stayed still owe what they owed, plus the month
  - L939 fifty arrivals owe nothing: the debt is the old total, less what the month repaid
  - L941 ...and the per-household debt is diluted by them
  - L943 ...and they brought the buffer: the old savings plus fifty buffers
  - L976 the large family, short every month, discharges
  - L977 ...and is locked out when it does
  - L978 the single adult next door never borrowed
  - L979 ...and was never locked out
  - L980 the row reads the lockout because ONE cell has it
  - L1002 the shares move with the people, like the savings
  - L1004 ...and none left the city
  - L1005 ...nor were taken away
  - L1006 the dollars abroad move with them too
  - L1008 ...all of them
  - L1009 ...and none left with anybody
  - L1012 the cells restore by name
  - L1023 ...every cell, to the cent, shares and dollars included
  - L1032 a save from before the dollars abroad restores
  - L1033 ...with the shares
  - L1034 ...and no dollars
  - L1043 a save from before the shares restores
  - L1044 ...with the savings
  - L1045 ...and no shares
  - L1046 ...and the city's stock with them
  - L1047 a save with a key this build does not know is refused whole
  - L1052 a row-only save seeds the row's cells with the row's position
  - L1055 ...and the row totals survive the seeding
- **L1057 A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2)**
- **L1069 a cell under half a household is empty, and what it held stays**
  - L1088 fixture: a cell of .3 households holds a position
  - L1091 the cell under half a household holds nothing
  - L1092 ...and its count stands: the census's, not ours to round
  - L1093 one cell had something to fold
  - L1095 ...and the households hold all their
  - L1098 into its own row first: the unskilled couples hold the single's savings
  - L1100 ...and the skilled couples, a row away, none of it
  - L1101 a stock nobody in the row held goes by the row's households
  - L1103 ...and none of it across the row's edge
  - L1114 a cell alone in its row is emptied too
  - L1115 ...into the city, every saver's savings up by 1 in 703
  - L1117 ...the unskilled couples' too
  - L1118 ...and the households hold all their savings
  - L1145 a cell the census leaves at .3 of a household holds nothing after the month
  - L1147 ...and the month counted it
  - L1148 the shares: the households' now, plus what left with the 99.7
  - L1150 ...and the .3's are the couples'
  - L1151 the dollars abroad the same way
  - L1166 the proceeds are all credited
  - L1167 ...all of them to the households who are there
  - L1169 ...and none to a cell of 1e-15 households
- **L1171 AND WHEN THEY CANNOT AFFORD A HOME, THEY SHARE**
- **L1177 a tier priced out of living alone shares instead**
  - L1182 nobody shares when everybody can afford a home
  - L1188 ...and a model with no households still forms none
  - L1191 somebody always holds out, however dear the rent
  - L1213 no household model, no pressure
- **L1267 THIS CITY IS NO LONGER POOR, AND THAT IS NOT A FAILURE (2026-09-09).**
  - L1298 ...so some of them are sharing
  - L1300 A FLATSHARE IS BETTER OFF THAN LIVING ALONE
  - L1302 ...because five of them pay one rent, not five
  - L1304 ...and still five baskets
- **L1307 A HOME IS A SIZE, AND A HOUSEHOLD HAS TO FIT**
- **L1317 a home is a size, and a household has to fit**
  - L1329 a studio flat takes two
  - L1330 a house takes four
  - L1331 a low-rise flat takes three
  - L1332 ...and only the studio refuses children
  - L1342 an empty city needs no homes
  - L1351 a two-person flat bills two
  - L1352 a six-person house bills six
  - L1353 ...so a bigger home is a dearer one
  - L1357 and a flat nobody in the city could live in bills nothing
- **L1360 and the two pension dials are the player's**
  - L1362 the contribution starts at the real CPP rate
  - L1365 ...and moves
  - L1367 ...but not past the ceiling
  - L1370 a richer pension is a bigger cheque
  - L1374 the dials survive a save
  - L1375 ...the contribution
  - L1376 ...and the pension
  - L1377 and never negative when contributions overshoot
  - L1384 contributions come off take-home
  - L1386 ...and the pension goes on
  - L1406 the workers pay all the contributions
  - L1408 ...and the pensioners pay none
  - L1410 the pension goes entirely to the retired
  - L1412 ...and nowhere else
  - L1414 the retired row now has an income at all
- **L1445 what a household spends above subsistence answers the real deposit rate**
  - L1479 fixture: income past the basket, a net worth, and a surplus left over
  - L1483 at no real return the factor is one
  - L1485 ...and the plan is what it was: the grocer's
  - L1486 ...the counter's
  - L1487 ...and the table's
  - L1494 ...the part above subsistence is exactly that share of it
  - L1496 ...and subsistence does not move
  - L1497 ...the counter moves with the surplus, by the same share
  - L1498 ...and so does the table
  - L1501 fixture: eighty points either way is past the floor and the ceiling
  - L1504 at +80 points the floor binds
  - L1506 ...and the plan keeps SPEND_FLOOR of what is above subsistence
  - L1508 at -80 points the ceiling binds
  - L1510 ...and the plan asks SPEND_CEILING of it
  - L1512 a rate that is not a number is no return at all
  - L1527 fixture: short of a basket after the rent, with nothing saved
  - L1532 a household at subsistence plans the same at any rate
  - L1533 ...a basket and no more
  - L1534 ...and can fund the same
  - L1549 fixture: two balances in lockstep
  - L1554 a balance nobody hands a factor plans at one
  - L1555 the one handed a factor plans at it
  - L1556 ...every cell asking that share of what is above subsistence
  - L1558 ...so the shops are told of less to sell
  - L1562 ...and the load path's re-strike plans at the same factor
  - L1587 a temporary directory for the city:
  - L1597 the page's real deposit rate is the deposit rate less the year's inflation
  - L1599 ...and its factor is the rule's on it
  - L1600 fixture: saving pays, so the factor is under one
  - L1608 ...and it is the factor the next month's plan was struck at
  - L1610 every month of it passed the money audit

## HouseholdMemoryCheck.java - 26 labelled assertions

> The households remember: the builder keeps what still fits.
> 
> Jerus, 2026-09-11: a record of the households of each type, "so when the
> model rebuilds it has a reference to try and keep but still allow change".
> Keep what still fits, 1% a month re-forming on its own, every cell, in the
> save and on the graphs. Every claim sets its own cause. See
> claude/the-households-remember.md.

- **L60 1. a bare model does not remember**
  - L71 a harness's FamilyModel rebuilt twice is the fresh build of the second month
  - L72 ...and it does not keep a record
- **L74 2. the same city a month later**
  - L79 the first month has no record to keep from, so it is the fresh build
  - L81 ...and after it there is one
  - L84 the second month keeps all but the 1% that re-forms
  - L86 ...the 1%
  - L87 ...and nothing stopped fitting, because nobody changed
- **L89 3. kept, and the rest built from the people left over**
  - L118 every shape is last month's, less 1%, plus the builder's answer for the people left over
  - L128 nobody kept was redrawn:
- **L132 4. a child grows up**
  - L151 the households that needed a baby shrink by exactly the babies who are gone
  - L153 ...a couple with a baby is last month's, less 1%, times what fits - no new ones form
  - L155 ...and the grown children found households of their own among the new ones
- **L158 5. the tiers follow the jobs**
  - L181 every tier holds the jobs' share of the working households
  - L184 ...and no shape gained or lost a household for it
- **L186 6. a save**
  - L198 the record comes back
  - L199 ...with the month's kept households
  - L206 the month after a load keeps exactly what the unloaded city keeps
  - L222 a save from before the record still loads its households
  - L223 ...and has no record
  - L227 ...so its first month is the fresh build
- **L229 7. a city**
  - L245 the game's families remember
  - L248 ...and keep most of last month's
  - L253 the graph holds this month's
  - L257 fixture: the city saved
  - L266 a played city's record survives a save
  - L272 ...and the month after, both cities hold the same households

## HousingCheck.java - 41 labelled assertions

> Audits the three subsystems that describe the same housing, every month, and
> makes them agree. Not part of the game.
> 
> WHY THIS EXISTS. Three classes hold a piece of one fact - how many doors this
> city has and who is behind them - and nothing made them say the same thing:
> 
>   BuildingManager  built the doors, and knows their SIZES
>   FamilyModel      puts households behind them, and knows who did not fit
>   CommercialHandler owns them as a business, and bills whoever is in one
> 
> On 2026-09-08 a UI redo drew two of those figures next to each other for the
> first time and reported an apparent contradiction: 15,181 homes standing
> ...

- **L97 1. every door is accounted for**
  - L100 let plus empty is owned
  - L101 ...and the landlords own what was built
  - L107 ...and the sizes add up to the same stock
  - L118 nobody lets more homes than exist
- **L121 2. every household is accounted for**
  - L126 housed plus doubled up is every household
  - L128 ...and nobody is doubled up who does not exist
  - L145 ...nor refused by a studio without being somewhere
- **L148 3. rent paid is rent received**
  - L154 the households paid what the landlords billed
  - L163 ...and that is the weight times the price
  - L167 ...and the two segment weights are the whole weight
- **L172 3b. the two segments partition everything**
  - L180 studio doors plus family doors is every door
  - L182 ...and studio seekers plus family seekers is every household
  - L184 a studio never bills more than its own doors hold
- **L188 3c. rent never falls through the floor**
  - L219 the blended rent target never goes under break-even
- **L223 3d. repairs are a flow, not a number**
  - L251 what the city paid for repairs is what the builders were paid
  - L258 and the landlords' share is the residential charge
  - L286 ...and the per-household figure is that over the payers
- **L329 4b. A CITY THAT REALLY DOES HOLD BOTH AT ONCE.**
- **L341 a city that built nothing but studios**
- **L373 WHAT THIS SECTION CAN AND CANNOT CLAIM, REWRITTEN 2026-09-09.**
  - L448 a studio city either builds family doors or can say who it refused
  - L451 either way it owns doors a child is allowed in, or refuses openly
  - L453 ...and every refusal is either crammed in or outside
- **L457 5. AND ALL OF IT SURVIVES A SAVE.**
  - L490 fixture: this city really is collecting rent
  - L491 fixture: ...at two prices that are really being charged
  - L493 fixture: ...on doors that are really let
  - L494 saved
  - L499 households doubled up
  - L501 ...the ones a studio turned away
  - L503 ...the ones in something too small
  - L505 the rent the households paid
  - L507 the homes the landlords let
  - L509 ...and the studio market's own price
  - L512 ...and the family one's
  - L519 the month after a reload still balances
- **L522 4b. A SMALL HOUSEHOLD TAKES A BIG DOOR WHEN THE SMALL ONES RUN OUT.**
- **L542 a single adult takes a four-person flat when that is all there is**
  - L559 fixture: the city really does have households to place
  - L561 every household is placed when only big doors exist
  - L563 ...and each one bills its DOOR's size, not its own
  - L565 ...and none of it is billed as studio
  - L581 ...and the only crowding is households too big for the flat
  - L602 fixture: some households really do have a child
  - L603 a studio-only city turns away exactly the households with a child
  - L605 ...and those are exactly the ones it could not place
  - L607 nothing is billed as a family let when only studios exist

## InboxCheck.java - 31 labelled assertions

> Verifies the inbox: raising, refreshing, resolving, culling and the round
> trip. Not part of the game.
> 
> WHY THIS EXISTS
> 
> The four things in here were banners, and a banner is checkable by looking at
> it: it is on the screen or it is not. A notice is not. It is raised by the
> city in a month nobody was watching, kept after it is read, and kept after it
> is fixed - so every way it can go wrong is quiet.
> 
> 1. RAISED TWICE. A condition that is true for forty months must produce ONE
>    notice that keeps its wording current, not forty notices saying the same
> ...

- **L65 1. a new city has an empty inbox**
  - L71 the inbox starts empty
  - L72 and nothing is urgent
  - L73 and the envelope shows no count
- **L75 2. living raises what is true**
  - L120 something got raised
  - L131 no condition has two live notices at once
  - L134 
  - L135 
  - L137 
- **L141 3. unread, urgent, and then read**
  - L160 a resolved notice never becomes urgent
  - L168 it counts toward the envelope
  - L171 reading it clears its urgency
  - L172 and takes it off the count
  - L173 but it is still in the inbox
  - L174 and it still says what it said
  - L182 reading it did not resolve it
- **L185 4. a live notice is refreshed, never re-raised**
  - L232 the run produced a standing problem to test
  - L233 a live notice is never raised a second time
  - L246 and keeps the month it was first raised
- **L253 5. it survives a save and a reload**
  - L256 the city saved
  - L266 every notice came back
  - L267 and so did what had been read
  - L283 key, title, body and all three months round-trip
- **L285 6. resolving and culling**
  - L299 a fresh notice is urgent
  - L301 a resolved notice is not urgent any more
  - L302 but it is still there to read
  - L303 and it still counts as unread
  - L304 it knows how long ago it settled
  - L306 just inside two years, it stays
  - L320 a stale notice is loaded
  - L334 and the next month drops it
- **L336 7. a new game forgets it**
  - L340 nothing carried over from the old city

## InfrastructureCheck.java - 67 labelled assertions

> The road network, from the curve up to a city that actually jams.
> 
> Three things have to hold and none of them is obvious from the code:
> 
>   1. The response curve is right - flat until it isn't, and floored.
>   2. A NEW city is never congested. The base network exists precisely so
>      that the opening hour is not a wall, which is the mistake the land pass
>      made when the starting allocation could not fit a power plant.
>   3. Building a road actually fixes it, and the fix survives a save.
> 
> The last one is the point of the whole feature. A mechanic the player cannot
> see themselves solve is just a tax.

- **L72 1. the curve, on its own**
  - L77 an empty network is not congested
  - L78 ...and carries everything
  - L79 a new city starts with a network at all
  - L83 half full, still free-flowing
  - L84 ...and says so
  - L89 exactly at free flow, still nothing lost
  - L90 ...but there is no headroom left
  - L93 just past it, throughput starts falling
  - L95 ...but only just
  - L96 ...and it is congested, not merely busy
  - L100 hopelessly overloaded, throughput hits the floor
  - L102 ...a gridlocked city still moves
  - L108 the floor is well clear of zero
- **L111 2. monotonic, with no cliff**
  - L124 more traffic never means more throughput
  - L125 no cliff edge - one building never costs 5%
- **L127 3. capacity is what you paid for**
  - L131 built capacity adds to the base
  - L133 ...and the player's share is reported separately
  - L137 a network at exactly capacity is congested
  - L140 reset puts the base network back
  - L142 ...with nothing on it
- **L144 4. a real city**
  - L153 a brand new city is not congested
  - L155 ...its roads carry everything
  - L188 a starter city still has road to spare
  - L190 ...and loses nothing to traffic
- **L192 5. growth jams it**
  - L213 a grown city outruns the network it was given
  - L215 ...and the load really is above free flow
  - L217 ...it says Congested
  - L240 the builders are slowed by it too
  - L242 ...by exactly the throughput ratio
  - L246 the shops feel it
- **L249 6. building a road fixes it**
  - L254 roads are a building the city can order
  - L256 ...that costs materials, so construction earns from it
  - L258 ...and takes months, so it cannot be a panic button
  - L260 ...and adds capacity when it is done
  - L262 a road generates no traffic of its own
  - L264 ...and employs nobody to run it
  - L276 the congested city saved
  - L315 the order goes through
  - L319 ordering one changes nothing yet
  - L334 the finished roads added capacity
  - L336 ...and cleared the jam
  - L337 ...while the city that built nothing is still stuck
- **L381 AND THE CLEANER SIGNAL UNDERNEATH IT, WHICH IS NOT CONSUMPTION.**
  - L416 ...and its shops can actually be supplied
- **L418 AND THERE IS NO SECOND OUTCOME ASSERTION HERE, ON PURPOSE.**
- **L456 7. across a save**
  - L486 the test city is still growing, which is the hard case
  - L488 the test city is genuinely congested
  - L497 saved
  - L502 it loaded
  - L503 the network came back
  - L505 ...at the same capacity
  - L508 ...and the same throughput
  - L515 the ratio the sectors were handed came back
  - L517 ...so retail revenue is unchanged
  - L520 ...and so is the month in progress
  - L534 ...and so is next month's income
  - L536 ...and the industrial statement, which was the last to drift
  - L540 the basis is NOT just the current ratio, or this proved nothing
- **L543 7b. THREE ROADS, AND ALL THREE USEFUL**
- **L561 three roads, and each of them wins somewhere**
  - L601 the gravel road is the cheapest to build per trip
  - L603 ...and the hungriest for ground per trip
  - L605 the elevated highway draws the most power per trip
  - L608 ...and the gravel road the least
  - L656 all three roads win a band, not two of them
- **L659 8. a new game forgets the traffic**
  - L663 capacity is back to the base
  - L666 nothing is on the roads
  - L667 ...and throughput is whole again
- **L669 9. EVERY FIGURE THE INFRASTRUCTURE TAB READS**
- **L689 every figure the Infrastructure tab reads is a number**
  - L756 ...in
  - L775 every good's wedge is a share of its own price
  - L785 every sector's fleet reading is a number

## InvestCheck.java - 80 labelled assertions

> Verifies the private investment engine: forecasting, the demand tests, and
> the brake. Not part of the game.
> 
> REWRITTEN FOR THE SECTOR TEMPLATE (2026-09-11). The planners used to be
> four methods on BusinessInvestment that took the city's figures as
> arguments - planRealEstate(jobs, homes, burden, output, orders) - so the
> fixture fed them numbers. Each sector plans for itself now, off its own
> buildings and the city it is attached to (Sector.plan), and the numbers
> come from a real Game. So the fixture is a city put into a stated shape
> with instant builds, asked what it would do. The arithmetic the old
> fixture pinned - the trend, the lead time, the order size, the brake, the
> costing, the land cap - is still asked of BusinessInvestment directly.

- **L83 1. the trend**
  - L85 no history -> no growth
  - L90 100/month over 5 readings
  - L97 capped population reads as no growth
- **L99 2. lead time**
  - L112 house at 100 pts/mo
  - L114 food plant at 100 pts/mo
  - L115 food plant at 1300 pts/mo
  - L116 no construction capacity -> unbuildable
- **L119 3. real estate reads JOBS**
  - L144 housing ahead of jobs -> hold
  - L152 fixture: the jobs now outrun the beds
  - L155 jobs ahead of housing -> build
  - L156 ...picked a residential building
- **L160 4. retail**
  - L170 customers ahead of coverage -> build
  - L171 ...picked a commercial building
  - L178 coverage ahead of demand -> hold
- **L181 5. industry**
  - L221 price below cost -> hold
  - L226 demand ahead of output -> build
  - L227 ...picked an industrial building
- **L231 6. THE BRAKE**
  - L235 cash purchase always passes
  - L239 profit well over interest passes
  - L241 profit under interest is declined
  - L245 merely breaking even is declined
  - L247 1.25x clears it
  - L251 same project at 2% passes
  - L253 ...but not at 9%
- **L256 7. costing matches the build path**
  - L263 house, empty materials yard
- **L265 8. order sizing**
  - L286 ordered more than one
  - L287 capped at twelve months of output
  - L293 more builders, bigger order
  - L298 small gap -> small order
  - L299 ...and well under the cap
  - L303 almost no builders -> still orders one
- **L305 9. construction expands itself**
  - L318 a short queue -> hold
  - L323 a long queue -> build
  - L324 ...picked a construction building
  - L326 ...one that actually adds output
  - L335 construction refuses without land
  - L336 ...which is the trap: no land, no builders
  - L341 already expanding -> hold
- **L343 10. construction earns as it builds**
  - L354 nothing earned on the order month
  - L355 all of it unearned
  - L358 a quarter delivered, a quarter earned
  - L359 three quarters still owed
  - L360 fully utilised
  - L365 job finished, all earned
  - L366 nothing left unearned
  - L367 backlog cleared
  - L372 half a month of work -> half utilised
  - L376 idle
- **L384 material is drawn in step with the work**
  - L390 two houses owe two houses' material
  - L392 a quarter of the work draws a quarter of the material
  - L393 ...and the rest is still owed
  - L395 the first house finished: half drawn in all
  - L398 the site empties: everything left is drawn
  - L399 nothing owed on a finished building
  - L400 two houses standing
  - L402 an empty site draws nothing
- **L404 11. idle payroll is floored, not full**
  - L418 busy: full payroll
  - L424 idle: floored at 25%
  - L426 idle costs less than busy
- **L428 12. land is the one thing that can say no**
  - L441 land short of the gap -> still builds
  - L442 ...but only what there are plots for
  - L447 under one plot -> refuses
  - L448 ...and says land is why
  - L453 no land at all -> refuses
  - L458 retail refuses without land
  - L459 ...saying so
  - L464 industry refuses without land
  - L469 land no longer binding -> the order is back
  - L474 already on site -> hold
- **L476 13. land is part of what a building costs**
  - L484 house plus its plot at $3
  - L485 dearer land makes a house dearer
  - L487 four houses, four plots
- **L492 14. distress**
  - L517 a solvent sector is left alone however long it has lost
  - L522 ...and an overdrawn one gets its two years first
  - L526 after two years overdrawn it sheds, with nothing spare at all
  - L527 ...its biggest holding
  - L528 ...at the gradual rate, not all at once
  - L530 ...and not while it is still building
  - L540 a distressed retailer sells shops
  - L541 ...and never the city's bank

## LabourCheck.java - 73 labelled assertions

> Verifies the labour market: who can hold a job, and what it costs. Not part
> of the game.
> 
> WHY THIS EXISTS
> 
> Two separate things were wrong and both were silent.
> 
> PopulationManager.getJobVacancy() walked the job array from the top index
> down, handing an undifferentiated pool of adults to the most-skilled posts
> first. A measured city of 9,016 people staffed 220 doctor posts at 100% with
> no school, college or university anywhere in the game, while a third of its
> workers sat idle. The eleven job types were real on the demand side and
> ...

- **L64 1. THE LADDER IS NEUTRAL AT ITS DEFAULT**
- **L72 at the default minimum wage, nothing has changed**
  - L75 the dial starts on the unskilled tier
  - L79 
- **L83 2. THE DIAL MOVES EVERYTHING**
  - L93 the doctor's base doubled too
  - L95 the ladder kept its shape
  - L100 and the dial is bounded
- **L104 3. SCARCITY RAISES IT, SURPLUS LOWERS IT**
- **L109 a shortage raises the wage, a glut lowers it**
  - L126 a shortage pays over the odds
  - L128 ...but not without limit
  - L130 a glut pays under them
  - L132 ...and stops at the bottom of the range
  - L142 no wage falls below the minimum
  - L144 ...so an unskilled glut is pinned, by construction
- **L147 4. IT IS LAGGED, WHICH IS THE POINT**
- **L155 and it gets there slowly**
  - L179 one month does not close the gap
  - L180 ...it closes ADJUST_RATE of it
  - L188 a shortage takes a year or more to price in
  - L189 ...and does get there eventually
- **L191 5. NOBODY IS A DOCTOR WHO IS NOT A DOCTOR**
- **L196 and a city cannot staff what it has not attracted**
  - L239 no more
  - L251 graduates with no graduate work cascade downward
  - L254 ...and nothing cascades up
  - L264 skilled workers exist at all - migration supplied them
- **L267 6. AND IT SURVIVES A SAVE**
- **L274 through a save**
  - L297 the city saved
  - L306 
  - L310 the
  - L327 ...and a recomputed market would NOT have matched
- **L329 RENT NO LONGER FOLLOWS THE UNSKILLED WAGE (2026-09-07).**
- **L352 rent is a market, not a wage formula**
  - L355 a reloaded city charges the rent it was charging
  - L358 fixture: the city has a housing cost to price against
  - L376 fixture: doubling the floor moved the unskilled wage
  - L385 doubling the minimum wage does not double rent
  - L387 ...and rent is not the wage formula any more
  - L398 the rent TARGET is not a wage formula either
- **L401 ARRIVING CHILDREN ARE NOT GRADUATES (2026-09-06).**
- **L412 arriving children are not graduates**
- **L428 HUNTED FOR, NOT ASSUMED.**
  - L475 fixture: found a month with skilled arrivals in it
  - L476 fixture: the city is not all adults
  - L484 the skilled counts gained the ADULT share of the skilled arrivals
  - L498 fixture: the two explanations are far enough apart to tell apart
  - L500 ...and not the whole mix
- **L503 A DOCTOR SHORTAGE IS PRICED ON DOCTORS (2026-09-06).**
- **L512 a doctor shortage is priced on doctors**
  - L557 fixture: more doctor posts than licence holders
  - L559 the market reports a licence shortage
  - L561 doctors are paid over the band
  - L563 ...and never over the ceiling
  - L565 an ungated graduate job carries no licence premium
  - L567 licence holders arrive in response to the price
- **L570 WHO MOVES IN, AND WHY (2026-09-07).**
- **L583 who moves in, and why**
  - L596 fixture: somebody moved in at all
  - L597 NOBODY ARRIVES WITHOUT A DIPLOMA
  - L599 ...so the unskilled band is only ever home-grown
  - L614 fixture: the graduate band is NOT bid up - only doctors are
  - L616 a doctor shortage brings doctors on its own
  - L627 a licence holder is one of the university arrivals, not an extra head
- **L639 the pull curve**
  - L640 at the going rate a graduate has no reason to come
  - L642 ...nor below it
  - L643 at twice the going rate, a third of the ceiling
  - L645 at the wage ceiling, all of it
  - L647 ...and never more, however far a premium is pushed
  - L649 a diploma is the base and is not competed for
  - L651 the graduate ceilings stay under it - the world has few to spare
- **L735 wages against the index they chase**
  - L754 fixture: the basket is based, so there is an index to chase
  - L775 fixture: the crawl really did inflate the basket, steadily
  - L777 every month wages chased the index the city published
  - L779 ...and moved DRIFT_PER_MONTH of the way to it - one indexation, not two
  - L781 so the wage index sits inside the lag's window, every month
  - L783 ...which under a rising index is BELOW it, not a third above
  - L791 fixture: the currency was reformed
  - L792 a reform leaves the wage index where it was
  - L793 ...and the level it is walking toward
  - L794 ...and the price index it walks toward
  - L802 ...and two years on, wages still chase the index the city published
  - L804 ...a DRIFT_PER_MONTH at a time
  - L805 ...inside the lag's window
  - L810 fixture: the reformed city saved
  - L813 the wage index reloads
  - L815 ...and the level it is walking toward
  - L817 ...and the price index it is handed
  - L831 and a reloaded city's wages chase the index it publishes
  - L833 ...a DRIFT_PER_MONTH at a time
  - L834 ...inside the lag's window

## LandCheck.java - 175 labelled assertions

> Verifies the land ledger: what the city owns, what it can allocate, what it
> charges, and that the three numbers never drift apart.
> 
> The one thing worth being paranoid about here is that allocated land can only
> ever go up by exactly what was built. A leak in either direction is invisible
> for a hundred months and then the city is either mysteriously full or
> mysteriously infinite.

- **L36 1. what the city starts with**
  - L41 owned
  - L42 allocated
  - L43 available
  - L44 thirty blocks
  - L45 nothing built on -> 0% used
  - L46 no blocks bought yet
- **L48 2. allocating**
  - L51 room for a 600,000 sq ft plant
  - L52 allocation succeeds
  - L53 allocated
  - L54 available
  - L55 20% used
  - L58 exactly what is left fits
  - L59 one sq ft more does not
  - L61 an oversized allocation is refused
  - L62 ...and took nothing when it refused
  - L67 available unchanged after a refusal
- **L69 3. filling up**
  - L72 the last of it fits
  - L73 nothing left
  - L74 100% used
  - L75 even one sq ft is refused now
  - L79 zero always fits
- **L81 4. releasing**
  - L85 freed
  - L87 over-releasing floors at zero
  - L88 ...and cannot invent land
- **L90 5. the listing**
  - L97 nine plots are listed
  - L98 the ground still costs $0.70/sq ft
  - L99 ...which is US$0.70 in the money it is asked in
  - L109 every plot has a size and a price
  - L112 the plots are different sizes
  - L129 the same city always sees the same plots
- **L131 5b. buying one**
  - L139 paid exactly what was listed
  - L141 owned grew by the plot's size
  - L143 recorded as a purchase this month
  - L144 the window refilled
  - L145 ...and that plot is gone from it
  - L156 the other offers did not move
  - L158 buying an unlisted plot does nothing
- **L161 5c. a bigger city pays more**
  - L171 more land and more people means dearer land
  - L178 ...but only slightly
- **L181 5d. supply and demand inside the city**
  - L192 a full city sells land dearer than an empty one
  - L209 a city with nothing built sells BELOW what it paid
  - L211 ...and a built-out one sells well above
  - L237 buying land makes land cheaper for investors
  - L249 the scarcity curve never goes backwards
  - L250 nothing built is the cheapest it gets
  - L252 completely full is the dearest
  - L254 ...and bad data cannot price below that
- **L257 5e. iron in the ground**
  - L264 some plot on offer has iron under it
  - L270 a deposit costs more than the ground it sits on
  - L275 no deposits to start with
  - L282 buying it gives the city its sites
  - L283 ...and its tonnage
  - L285 the sites support that many mines
  - L286 ...and not one more
  - L289 mining takes ore out of the ground
  - L290 ...and the reserve falls
  - L296 a deposit can be worked out
  - L297 ...and then yields nothing
  - L298 ...and supports no more mines
- **L301 5f. parcels are blocks, and they grow**
- **L307 no more slivers**
  - L319 nothing on offer is smaller than a block
  - L320 a new city is offered blocks of one
- **L324 and the floor rises with the city**
  - L334 a big city is not offered scraps
  - L344 ...and every plot it IS offered respects that floor
  - L345 its smallest plot dwarfs a new city's
  - L352 the floor stops climbing eventually
- **L357 a tract can hold a mining district**
  - L383 some parcels carry more than one deposit
  - L384 ...but most still carry one
  - L385 every site has room for a mine
- **L387 the listing survives a save, old format included**
  - L395 a listing restores
  - L406 ...every field of every parcel, deposits included
  - L433 a pre-deposit save still loads
  - L434 ...read four fields wide, then trimmed to the shelf
  - L436 ...their ids intact
  - L437 ...to the last one kept
  - L440 ...their sizes not read as prices
  - L442 ...and its one ore parcel counts as a single site
  - L444 ...while bare ground counts as none
  - L453 an older listing's prices wait as written for the loading rate
  - L455 ...and settle as dollars at it, every parcel of them
  - L457 ...so a $400 plot is a US$200 one at 2.00
  - L459 ...and costs what the save said on the day it is loaded
  - L461 ...and settles once: a second call converts nothing
  - L463 a dollar listing is not converted at all
  - L465 ...its prices exactly as written
  - L468 a length that is neither shape is refused
- **L471 6. not affording it**
  - L479 cannot afford it -> pays nothing
  - L481 ...and gets nothing
  - L482 ...and is not recorded
  - L483 ...and it is still on offer
  - L485 exactly enough does buy it
- **L488 7. selling**
  - L493 opening price is $1/sq ft
  - L494 a 8,000 sq ft house plot
  - L495 margin at the opening price
  - L498 sale recorded
  - L499 sq ft recorded
  - L502 sales accumulate over the month
  - L505 cleared for the next month
  - L506 ...sq ft too
  - L507 ...and purchases
  - L510 owned survives the clear
- **L512 8. the player's price**
  - L516 price set
  - L517 the same plot now costs more
  - L518 fatter margin
  - L523 below cost reads as a negative margin
  - L526 free land is allowed
  - L527 ...and costs the buyer nothing
  - L530 a negative price floors at zero
- **L532 9. reset**
  - L540 owned back to the start
  - L541 nothing allocated
  - L542 no blocks bought
  - L543 price back to default
  - L544 block cost back to the first
  - L545 no flows
- **L547 10. every building fits on a starting city**
  - L568 every building has a footprint
  - L575 a power plant fits on the starting land
  - L579 the starting land holds this many houses
  - L588 both utilities fit
  - L589 ...with almost nothing to spare
  - L593 ...and then a materials plant does not fit
- **L599 11. the price is a density policy**
  - L616 at the default price, sprawl is cheaper
  - L626 at $20/sq ft, density is cheaper
- **L648 land is priced in dollars; what it costs here is the day's rate**
  - L660 a listed parcel's dollar price does not move when the rate does
  - L662 ...nor the office's ground price in dollars
  - L664 ...while what it quotes here is that at today's rate
  - L666 ...and what businesses pay does not read the rate at all
  - L668 ...so the margin carries the currency
  - L673 what a parcel costs is exactly its dollars times the rate
  - L674 ...and that is what the month's land purchases carry
  - L677 ...and a parcel it cannot pay that for is refused
  - L690 a currency reform leaves the listing's dollar prices alone
  - L691 ...and the office's dollar ground price
  - L693 ...while what businesses pay is reformed with every local price
- **L722 converting: the treasury buys the dollars and pays them over**
  - L726 fixture: a new city converts by default
  - L732 fixture: both cities list the same plot at the same dollars
  - L740 fixture: the plot is bought
  - L743 converting: the treasury pays usd x rate
  - L744 ...the vault ends where it began
  - L746 ...the seller is paid the parcel's dollars
  - L748 ...and nothing was bought for the vault
  - L752 ...the city's pool fell by exactly that - money across the edge
  - L753 ...and no other pool took it
  - L754 ...and the receipt names the conversion
- **L757 from the vault: the dollars leave it, and no money moves**
  - L763 fixture: the vault can pay for the plot
  - L764 fixture: the plot is bought
  - L767 from the vault: the treasury's cash does not move
  - L768 ...the vault falls by the parcel's dollars
  - L770 ...its record by their local price, as a sale's would
  - L774 ...and no pool moved at all
  - L779 ...the journal names it
  - L780 ...at usd x rate, the other way up from the budget's land line
  - L782 ...and the receipt says it came out of the vault
- **L785 and each month closes**
  - L791 the month after land bought
  - L793 ...nothing moved after it struck
  - L794 ...the budget carries the land at usd x rate
  - L796 ...and the month's dollars paid for it
  - L798 ...which cost here what the budget's line carries
  - L801 the vault's part of them, converting
  - L803 ...and from the vault
  - L805 the bridge leaves the same over either way: the journal carries the vault's
- **L808 a short vault pays what it holds and converts the rest**
  - L817 fixture: the vault holds about half the parcel
  - L820 a purchase never fails for the toggle's sake
  - L821 the vault paid what it held
  - L822 ...and the rest was converted from cash
  - L823 ...and the receipt says so
- **L827 the toggle survives a save, and an older listing reads as dollars**
  - L839 fixture: it loads
  - L840 the toggle survives a save
  - L845 ...and a dollar listing comes back to the cent
  - L846 ...and the office's dollar quote with it
  - L858 fixture: the save carried the toggle
  - L873 fixture: the older save loads
  - L875 fixture: at the rate it was saved at
  - L876 an older save converts
  - L881 an older listing's local prices read as dollars at the loading rate
  - L882 ...so the first costs what the save said, on the day it is loaded
  - L884 ...and the office's local quote reads the same way

## LongPlaytest.java - 0 labelled assertions

> A city played for four thousand months, the way a person plays.
> 
> WHY NOT JUST simulateMonths(4000)
> 
> A single long skip is one input. It exercises the month loop and almost
> nothing else: the player never intervenes, so the city settles into whatever
> equilibrium it finds in the first fifty months and then repeats it. Every bug
> that lives in the interaction between DECIDING and SIMULATING - the ones that
> need something to be built, taxed, borrowed against, saved or reloaded partway
> through - is invisible to it.
> 
> So this alternates. Short hands-on stretches where an advisor looks at the
> ...

_(this harness does not label its checks through a helper - it prints its findings; read its header and its sections)_

- **L338 AND NOTHING MOVED AFTER THE AUDIT STRUCK.**
- **L1209 THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free.**
- **L1264 AND EVERYTHING THAT IS A PURCHASE.**
- **L1553 AND THE BEST OF THEM WINS.**
- **L2258 founding: a few months at a time, by hand**
- **L2348 then the real rhythm**
- **L2493 the report**
- **L2501 ==**
- **L2587 BUSINESS SERVICES - and the point of printing it is the MECHANISM,**
- **L3168 what the advisor tried, and what happened**
- **L3174 findings**

## ManufacturingCheck.java - 57 labelled assertions

> The ninth sector: what the city makes out of its own steel, and ships.
> 
> WHAT THIS HAS TO PROVE, and why none of it is "the city got bigger". The
> crime batch established that the long-run population of a city moves ten
> percent on perturbations of one part in a million, so no ensemble this
> harness could afford is evidence about a sector. Sixteen seeds of 333 years
> say the effect is real and enormous - 6/16 cities past forty thousand
> becoming 16/16, median 16,834 becoming 89,700 - and that measurement lives
> in claude/manufacturing.md where a number that will drift belongs. What is
> CHECKABLE is whether the thing works:
> 
>   1. the two goods are export-only, flow not stock, and clear at the floor
> ...

- **L79 a line's costs are split between its outputs, not charged twice**
  - L84 
  - L94 
  - L96 ...with every one a real share, not nothing and not all of it
- **L107 1. two goods the city makes**
- **L134 two goods that leave**
  - L144 a beam still cannot be bought from the world, at any price
  - L146 ...but a machine can, now that something here buys one
  - L148 ...and the world charges a premium for it over what it pays
  - L151 shaped steel is worth more than the steel in it
  - L153 ...and a machine is worth more than the beam
  - L158 nobody here bidding - a tonne fetches the export floor
  - L160 ...which at founding is the world's own price
  - L163 a weak currency lifts what the work fetches
- **L166 2. and steel has a ceiling now**
  - L169 steel is importable - something here buys it
  - L170 ...at the US hot-rolled band
  - L171 ...which is above what a mill gets shipping it out
  - L176 a city with mills and no fabricator: steel sits on the floor, as it always did
  - L179 a city with a fabricator and no mill pays the world's price
  - L182 ...and a city with both splits the difference
- **L187 3. the templates are the design's arithmetic**
  - L232 fabrication loses five percent of every tonne it cuts
  - L235 ...and the works loses the same, being the same trade
  - L238 machining takes a tonne and a bit under a half
  - L295 fabrication lives and dies on the steel price
  - L296 ...and the machine works on the wage bill
  - L297 neither is a business at all if both bite
  - L307 a fabrication yard takes more ground a post than a steel mill
  - L310 ...and more than anything else an investor builds
  - L313 the machine works does not - machining happens indoors
  - L315 ...and pays for its density in power instead
- **L319 4. the traps**
  - L332 no plant here needs a licence at all - none of them practise anything
  - L339 the staffing floor is eighty percent
  - L350 a village cannot staff the biggest plant in the game
  - L353 ...so the sector does not ask for one
  - L354 ...and says why, in people rather than money
- **L358 5. the two brakes, each caused**
  - L377 two shops is 2,400 tonnes of fabricating capacity
  - L379 ...and one machine works is 180 of machinery
  - L381 it sold something
  - L382 ...and every tonne of it left the city
  - L383 everything it sold, it exported
  - L385 it bought steel to do it
  - L388 the two together are under one, or it would be shedding
  - L408 dear steel makes another shop worth less
  - L409 ...far less: it is most of what a shop spends
  - L410 ...while the machine works is still worth building
  - L411 ...which is the whole point of having both
  - L428 quadrupling the wage floor makes another machine works worth less
  - L430 ...and it is the machine works that gives way first
- **L434 6. the books, the audit, and a reload**
  - L457 every sector's statement still foots
  - L458 and the money identity holds with a ninth sector in it
  - L468 the mills sold steel at home for the first time in this game's history
  - L470 ...and got more than the ship would have paid
  - L472 ...and less than the fabricator would have paid the world
  - L474 the fabricators bought from them
  - L476 Manufacturing is the ninth sector
  - L478 ...and the share register is the sectors and the bank
  - L490 a reloaded city has the same fabricating capacity
  - L493 ...the same machinery capacity
  - L495 ...the same cash
  - L498 ...and the same steel price, restored rather than recomputed

## MiningCheck.java - 30 labelled assertions

> Ore, from the band it clears in to whether it makes steel worth building.
> 
> The point of this feature is one number: a Steel Foundry earns about $9,650 a
> month on a $3.6M asset, which is a quarter of a percent and the reason nobody
> builds one. If local ore does not move that number substantially, everything
> else here is decoration.
> 
> So the last section measures it directly - the same foundry, the same city,
> with and without a mine - rather than asserting that the parts are wired
> together and hoping.

- **L69 1. the band**
  - L78 the mills' scrap price is the ceiling
  - L79 the mines' export price is the floor
  - L81 ...and they are the world's two prices for ore
  - L89 no mines - price sits at the scrap ceiling
  - L94 no mills - price sits at the export floor
  - L101 supply meets demand - the middle of the band
  - L106 three times the ore that is wanted - down near the floor
  - L110 three times the demand - up near the ceiling
  - L121 more ore never makes ore dearer
  - L133 the price never leaves the band
  - L137 a warehouse counts as a sixth of itself a month
- **L140 2. a mine needs ground with ore in it**
  - L164 the floor IS the world's price for the city's ore
  - L166 the ceiling IS what the mills pay for scrap
  - L168 ...and the mine's own template no longer carries a price of its own
  - L171 the Iron Mine is its own category
- **L173 THE MINE IS NOT THE BIGGEST EMPLOYER IN THE GAME ANY MORE, AND IT**
  - L199 ...and is still a serious employer
  - L210 a city with no deposit cannot build one
  - L212 ...and it is refused for the RIGHT reason, not for money
  - L219 buying a parcel with iron gives the city a deposit
  - L225 ...and now the mine can be ordered
  - L231 but one deposit only supports one mine
- **L234 3. does it actually pay?**
- **L259 "BARELY BREAKS EVEN" WAS A CONSEQUENCE OF A FAKE STEEL PRICE.**
  - L283 a foundry makes an electric-arc mill's margin on scrap
  - L285 ...and it is paying the scrap ceiling to do it
  - L297 local ore takes steel past half its revenue
  - L299 ...which is a different business, not a better month
- **L302 4. and is the mine worth sinking?**
  - L358 fixture: every tonne left at the floor
  - L367 a mine lifts ore for less than the export floor
  - L369 ...so exporting alone is profitable, with no mill anywhere
  - L371 ...comfortably, not marginally
  - L385 a mill next door is worth more to a mine than exporting

## MonetaryCheck.java - 64 labelled assertions

> Money: what a basket costs, what the world charges, and what the rate does.
> 
> WHAT IS ACTUALLY BEING ASKED
> 
>   1. Does the price index measure what households BUY, on a basket fixed at a
>      base period? A CPI that re-weights as spending shifts shows no inflation
>      for a family that switched to cheaper food while eating worse.
> 
>   2. Do prices RATION? A shop that can meet a fifth of demand and charges
>      cost-plus is not a shop, it is a queue - and a model with no demand-pull
>      channel gives a policy rate nothing to cool.
> 
> ...

- **L64 1. the basket**
  - L70 a young city has no basket yet
  - L72 ...and a settled one does
  - L73 the basket is what they actually spent
  - L74 ...and it starts at one
  - L84 doubling food moves the index by the food weight
  - L86 ...and the weights did not move to hide it
- **L88 the high and low water marks**
  - L96 the peak is the dearest month, not the last one
  - L97 ...and it remembers which month that was
  - L98 the trough opens where the basket was based
  - L99 ...on the month it was based
  - L103 cheaper food moves the index
  - L104 ...and the peak does not come down with it
  - L105 ...while the trough follows it down
  - L106 ...and stamps the new month
  - L107 the swing is peak over trough
  - L116 the peak reloads
  - L117 ...with its month
  - L118 the trough reloads
  - L119 ...with its month
  - L129 an older save opens both marks on where it is now
  - L131 ...both of them
  - L137 a rate is not quoted before there is a year of readings
- **L140 2. prices ration**
  - L145 shelves that meet demand charge cost-plus
  - L151 a total shortage charges the ceiling
  - L156 ...and half a shortage is between the two
- **L160 3. the world is a real place**
  - L172 it stays inside its band
  - L175 ...and it actually moves around in it
  - L176 ...and the price level stays a usable number
  - L185 two worlds from the same seed agree exactly
  - L191 a pinned world does not move
- **L193 4. and the rate does something**
  - L197 it opens at neutral
  - L200 the dial moves it
  - L202 ...and will not go below zero
  - L205 ...nor past the guard that stops a typo setting 9,900%
  - L218 on target it advises neutral
  - L219 above target it advises more than one for one
  - L221 ...and below target, less
  - L222 ...and says why in words about inflation
- **L232 the target is a dial**
  - L234 it opens at the target it always had
  - L236 ...where the rule is the rule it always was
  - L244 the same inflation aimed at 0% and at 5% differs by TAYLOR_WEIGHT x 5 points
  - L246 ...and on its target the rule advises neutral, whatever the target is
  - L248 ...the dial's rule being the rule at that target
  - L250 ...and the advice it clamps to the dial reads it too
  - L253 ...and the reason names the target it aims at, half point and all
  - L256 the dial stops at MAX_INFLATION_TARGET
  - L259 ...and at MIN_INFLATION_TARGET below
  - L278 paying under the world in real terms weakens the currency
  - L279 ...and paying over it supports the currency
  - L280 ...by RATE_PULL for every point of the real gap
  - L284 ...and past any gap a city reaches, only the numerical guard
- **L287 5. in a city, and across a reload**
  - L317 the city based its basket
  - L318 ...and the world's prices moved
  - L320 ...and the currency is not against a bound
  - L339 the policy rate reloads
  - L341 ...and the world's price level
  - L343 ...and the price index
  - L345 ...and the basket it is measured on
  - L354 ...and the year of history the inflation rate is struck from
  - L361 ...and the inflation target, the player's dial
  - L366 fixture: the save carried the target under its own key
  - L378 a save from before the dial reads the default: 2%, the constant it was
- **L539 inflation falls with the rate**
  - L600 the runs differ by the dial and by nothing else
  - L602 ...and a month's delay in the hand moves them less than the allowance

## MoneyCheck.java - 12 labelled assertions

> Money is conserved: every dollar that leaves a pool arrives in another, or
> crosses the city's boundary in a way the audit can name.
> 
> The 29th harness, and the one the other twenty-eight were missing. Every
> money bug this codebase has had was a flow with one side - a charge with no
> payee, a tax with no payer - and each was found by hand, months after it
> started. MoneyAudit strikes the identity every month; this plays a city with
> all six sectors trading, borrowing, building and being taxed, and demands
> the residual stay at rounding.
> 
> Two cities, because a fixture that only ever grows can hide a leak that
> only opens under stress: one is left to prosper, the other is bankrupted
> ...

- **L72 1. a city that prospers**
  - L90 a founding city conserves money to within 0.01% of what moved
- **L109 AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16).**
  - L130 an industrial city conserves money to within 0.01% of what moved
  - L132 ...and it really did carry passengers, so the fare was really charged
- **L136 AND LAND PAID FOR OUT OF THE VAULT (0.7.6).**
- **L146 land bought out of the vault: nothing moves the audit cannot see**
  - L156 fixture: the vault paid for a parcel and the treasury's cash did not move
  - L160 a city that paid for land out of the vault conserves money
  - L162 ...and nothing moved a pool after any month's audit
- **L165 2. a city under stress**
  - L188 a stressed city conserves money to within 0.01% of what moved
  - L199 a restructure moves no cash the audit cannot see
- **L202 AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK.**
- **L220 a city paying over the world: the money that arrives is audited**
  - L237 fixture: the rate actually brought money in
  - L239 ...and the audit saw it cross the border
  - L242 a city conserves money with hot money flowing
  - L245 ...and nothing moved after the audit struck

## NewGameCheck.java - 18 labelled assertions

> Does "Start New Game" actually start a new game?
> 
> Plays a city hard, calls newGame(), and compares the result field by field
> against a Game that has never been played at all. Anything that differs is the
> previous city's fingerprints on a fresh one.
> 
> WHY THIS IS A LIST OF FIELDS AND NOT A LIST OF ASSERTIONS
> 
> The bug this exists for was not that someone reset the wrong thing. It was
> that resetGame() cleared the fields somebody had remembered to add to it, and
> the list had fallen twenty-three fields behind - a new city inherited $81,777k
> of construction cash, $15,402k of business debt, 1,868 units of the previous
> ...

- **L191 1. what a city that never existed looks like**
  - L199 it has no people
  - L200 it has its starting cash
  - L201 ...and the founders' dollars in the vault
  - L203 ...bought on day one, at the opening rate
  - L206 it is at month 1
- **L208 2. live in one, hard**
  - L250 the used city really is used
  - L253 ...and its vault is not the founders' any more
  - L255 ...and it paid for land out of it, and pays that way still
- **L258 3. start a new one**
- **L279 4. and it is actually playable**
  - L283 the building catalogue is loaded
  - L287 houses can be ordered
  - L293 and so can shops
  - L298 months pass
  - L299 and people move in
  - L302 its history starts from this city, not the last one
- **L305 5. a new game after a LOAD, too**
  - L315 saved
  - L319 loaded
  - L324 ...with the vault it was saved with, not a new city's
  - L347 starting a new game does not delete the save it left

## OutsideCheck.java - 103 labelled assertions

> The people outside the families: the out of work, the students, the
> unhoused and the orphans (2026-09-11).
> 
> Jerus: "we are to add a new household structure called unemployed... these
> will just sum up by age the unemployed, or unhoused... and they will have
> their own cashflow and stuff." Twenty questions answered; this is every one
> of the answers that can be caused and measured, each with the cause set
> by the fixture rather than stood next to. See
> claude/the-people-the-books-left-out.md.

- **L50 1. a student is not unemployed**
  - L63 the labour force is the workforce less the students
  - L64 ...and the unemployed are the labour force less the filled posts
  - L66 ...at a rate over the labour force, not the workforce
- **L70 2. the families are the people who work**
  - L90 the families hold every adult but the ones outside them
  - L92 ...and the builder records who it left out
  - L99 the orphans are exactly the babies no family took
  - L101 ...and the teens
  - L102 in an equilibrium city at full employment, there are some (Jerus: \"they're the orphans\")
  - L104 no adult is ever an orphan
- **L107 3. the out of work share doors with their own kind**
  - L137 fixture: two hundred could not be housed alone
  - L140 the working singles share only for their own unplaced (a common valve would leave 150)
  - L143 ...and the out of work share only for theirs
  - L147 one of them pays less than a whole door
  - L148 every household that wants a door is housed, doubled up, or outside
- **L153 3b. the children go where their parent goes**
- **L176 every child is in a household, or in the orphan section**
  - L192 fixture: four hundred adults left work and none went away
  - L194 ...so a fifth of the children went with them
  - L201 the
  - L205 one outside adult is one household, so this is what each of them holds
  - L217 fixture: the same four hundred adults, in prison instead
  - L219 a prisoner takes nobody with them
  - L220 ...so their children are orphans, which is the honest answer for them
  - L227 ...and the count still holds for
  - L235 a city with nobody outside the families holds nobody's children either
  - L237 ...and asks for none per household
- **L240 4. EI, on the inflow**
  - L254 a founding city with nobody out of work draws nothing
  - L261 the posts that disappeared are the jobs lost
  - L262 ...and every one of them is on EI
  - L263 ...at 55% of the wage they lost
  - L271 a post left empty by somebody leaving the city loses nobody a job
  - L278 eleven months on, the claim is still being paid
  - L281 ...and in the thirteenth month it drops off
  - L282 ...into the off-EI group, whole
  - L283 nobody past the twelfth month is paid
  - L294 fifty openings go half to the pool...
  - L295 ...and half to the arrivals
  - L296 the arrivals who did not get one are on EI (Jerus: \"same as locals\")
  - L298 ...at the unskilled rate
  - L300 ...and nobody entered the pool by a door the flows do not name
  - L308 an elite claim is capped at the maximum insurable earnings
  - L315 fixture: the pool is off EI
  - L318 a quarter of the evicted leave the city
  - L319 ...and the rest have no home
  - L320 ...and are no longer looking for a door
- **L323 5. the books**
  - L349 the EI bill goes to those on EI
  - L350 ...and nobody past it shares it
  - L351 an off-EI household that cannot pay its rent is evicted by the share unpaid
  - L358 a student is paid the grant
  - L359 ...cannot run out: nothing unfunded (Jerus: \"can't run out, for now\")
  - L360 ...because the student loan covered what the savings could not
  - L362 ...and it is a student loan, not the bank's credit
  - L363 the treasury lent it
  - L366 an orphan has no income
  - L367 ...and goes without
  - L378 fixture: the students owe something
  - L386 the graduates repay a 114th of what they brought
  - L387 ...and owe the rest
  - L397 fixture: the students owe something
  - L399 fixture: the workers owe nothing
  - L402 five graduates leave a student body that did not change size
  - L403 ...with their loans, to the working households
  - L405 ...who start repaying them
  - L406 the freshers in their places owe nothing yet: the students' loan is halved, plus this month's
  - L408 nothing written off: a graduate is not a leaver
  - L410 ...and it happens once
- **L413 6. health**
  - L421 a tenth of the city unhoused adds their extra sickness over the baseline
  - L430 a tenth of the adults unhoused die 3.7 times as fast
  - L432 a fifth of the babies orphaned die at the rate of a baby nobody cares for
  - L434 ...which is faster than any cared-for baby
  - L435 a band with nobody outside is untouched
- **L438 7. a real city: the treasury and the books agree**
  - L462 the treasury pays the EI the ring struck on the pool the month opened with
  - L464 the premium is the dial times the staffed wage bill
  - L467 the pool is the labour market's
  - L472 the families hold the adults who are not outside them
  - L502 fixture: the city has more workers than posts
  - L511 fixture: the closing cost filled posts
  - L513 ...and put people on EI
  - L526 the closing month pays the EI of the pool it opened with
  - L528 ...and the out of work are credited that figure, the same month
  - L531 fixture: the closing moved the bill, so a month's lag would show
  - L533 fixture: there is EI to pay
  - L536 the month after pays the bill the closing struck
  - L538 ...and the out of work are credited it in the month it is paid
  - L541 and both months pass the money audit
- **L545 8. a city with a college: the students' money, and a save**
  - L623 fixture: the college has students
  - L627 the grant is the dial times the unskilled wage, for every student
  - L630 the students pay the tuition (Jerus: \"students pay it\")
  - L632 ...and no family does
  - L633 the treasury lent student loans
  - L634 every month, the students the census saw finish left with their loans
  - L636 fixture: people graduated
  - L637 ...so the working families carry student loans
  - L638 ...and graduates have repaid some of them
  - L639 a student is never cut off
  - L640 every month passed the money audit
  - L646 the city saved
  - L649 the student loans came back
  - L650 ...and the pool
  - L651 ...and who was on EI
  - L652 ...and the EI dial
  - L653 ...and the orphans
  - L654 ...and who finished a course, which the families read next
  - L656 fixture: somebody finished that month
  - L658 a temporary directory for the save:

## PolicyCheck.java - 80 labelled assertions

> The Policy tab: banded wage tax, per-sector offsets, the VAT, and subsidies.
> 
> THE ONE THING THIS HARNESS IS REALLY FOR
> 
> Every rate in the game moved from "one number" to "a city rate plus an
> offset", and every one of those changes is invisible when the offsets are
> zero - which is how they ship. A plumbing change that reproduces the old
> behaviour exactly is indistinguishable from a plumbing change that quietly
> broke and reproduced the old behaviour by accident, unless something moves the
> dials and checks the result. Section 1 pins the zero case; everything after it
> moves a dial.

- **L46 1. zero offsets reproduce the single rate**
  - L62 every band and sector resolves to the city rate
- **L64 2. offsets, and their clamps**
  - L70 university pays five points more
  - L71 the unskilled pay eight less
  - L72 and the middle is untouched
  - L77 an enormous discount is capped, not honoured
  - L79 ...and the rate it resolves to never goes below zero
  - L88 a city-wide rise carries the offsets with it
- **L91 3. the wage tax is banded, not averaged**
  - L112 flat: 4000 of wages at 20%
  - L116 banded: 1000 at 10% plus 3000 at 30%
  - L119 ...which taxing the total at the city rate would have got wrong
  - L126 an unfilled post pays no wage tax
- **L129 4. the VAT taxes value added, once**
  - L144 the mine remits on its ore
  - L145 the mill remits on its margin only
  - L146 so the city collects 10% of the FINAL value, not of both stages
  - L151 ...which is less than taxing every stage's turnover
- **L154 5. exports are zero-rated, and can refund**
  - L162 nothing is charged on what leaves the city
  - L164 ...but the credits behind it still stand
  - L166 so a pure exporter is owed money
  - L167 ...and the ledger says so rather than flooring at zero
  - L169 the zero-rated sales are still recorded
- **L172 6. imports carry the tax in, and out again**
  - L190 an importer reselling at cost remits the tax on its sale
  - L198 ...which is what the local chain remits on the same goods
  - L200 ...so importing carries no advantage over buying locally
- **L203 7. the subsidy floors a sector and stops the count**
- **L215 a protected sector never reaches six losses**
  - L260 an unprotected sector runs the counter up
  - L261 ...past the six that trigger a sale
  - L262 a protected sector's counter stays at zero
  - L263 ...and the city paid exactly the losses
- **L265 8. the money goes somewhere**
  - L279 the city paid the whole loss
  - L280 ...out of its own cash
  - L281 ...and into the sector's
- **L283 9. it all survives a save**
  - L308 saved
  - L319 city income rate
  - L320 ...the profit base, which it is once they part
  - L321 ...the sales base, parted from it
  - L322 ...and the wage base, parted too
  - L323 city property rate
  - L324 a wage offset
  - L325 a profit offset
  - L326 a sales offset
  - L327 a property offset
  - L328 a protected sector is still protected
  - L330 ...and so is the other one
  - L332 ...and an unprotected one is still unprotected
  - L339 a policy array of the wrong shape is refused
  - L341 ...and nothing was changed by the attempt
  - L343 a null policy array is refused too
  - L355 fixture: the save carried this build's policy array
  - L369 a save from before the split reads its one rate as profit
  - L370 ...as sales
  - L371 ...and as wage
  - L372 ...three equal bases, the city it was
  - L373 ...its offsets kept, riding their own bases
- **L375 10. three bases, one per tax (0.7.4)**
- **L382 each income tax moves off its own base**
  - L389 fixture: one rate, the three bases equal
  - L392 the sales base moves on its own
  - L393 ...every sector's sales rate follows it
  - L394 ...an offset riding it
  - L395 ...while profit stays where it was
  - L396 ...and so does wage
  - L397 ...and the three have parted
  - L400 the wage base moves on its own
  - L401 ...a band's offset riding it
  - L402 ...and profit is still its own
  - L405 the profit base moves on its own
  - L406 ...and sales did not follow it
  - L407 ...which getIncomeTaxRate() reads once they part
  - L410 a base stops at MAX_INCOME_TAX
  - L412 ...and at nothing below
  - L415 every tax at once sets the profit base
  - L416 ...and the sales base
  - L417 ...and the wage base
  - L418 ...so they are one rate again
  - L419 ...the offsets still riding their own bases
  - L425 the policy array is read back
  - L426 ...the profit base with it
  - L427 ...the sales base
  - L428 ...and the wage base
  - L431 an array of the length before the split is still read
  - L433 ...and reads the one rate in slot 0 as all three: profit
  - L434 ...sales
  - L435 ...and wage

## PopulationCheck.java - 130 labelled assertions

> The demographics: do they hold together, and do they move the city the way
> they were told to?
> 
> THIS FILE USED TO ASK THE OPPOSITE QUESTION. For two batches the cohorts were
> a placeholder, and section 4 played two identical cities - one with
> demographics running, one suppressed - and required every live figure to match
> exactly. That assertion existed because `BuildingManager.instances` had rotted
> in place as an unwatched placeholder, and the note here promised that the day
> the cohorts became load-bearing, the section would fail.
> 
> It has, and this is what replaced it. The claims are now about BEHAVIOUR, and
> they are the four things Jerus actually asked for:
> ...

- **L65 1. the bands**
  - L68 babies span six years
  - L69 children seven
  - L70 teens five
  - L71 adults fifty-two
  - L72 seniors fifteen
  - L73 elders thirty-five
  - L77 and together, one hundred and twenty years
  - L85 the bands meet exactly - nobody falls between them
  - L86 only adults work
- **L89 2. ageing conserves people**
  - L103 the first arrivals seed the pyramid
  - L117 a new city is mostly of working age
  - L119 ...and is not founded by pensioners
  - L121 ...but has some, since people do retire
  - L141 twenty years of ageing conserves every person
  - L143 babies are born
  - L144 ...and seniors die
  - L145 every band is populated
  - L164 the smear is the expected ~37%, not something worse
- **L167 2b. mortality, and the trap in the arithmetic**
  - L183 
  - L194 a catastrophic rate is capped, not obeyed
  - L196 ...and so is a nonsensical one
  - L198 a negative rate kills nobody
  - L201 children are the safest band
  - L204 seniors die far more than adults
  - L206 ...but not absurdly - a senior is not doomed
  - L232 a seventy-year-old has a realistic time left
  - L252 most of the city is of working age
  - L254 seniors are a realistic share, not half the city
  - L256 the dependency ratio is plausible
- **L259 3. pay tiers and families**
  - L264 eleven job types, six distinct wages
  - L266 every job maps to a tier
  - L278 PopulationManager pays exactly what PayTier says
  - L288 households were formed
  - L289 seniors got their own households
  - L292 families with dependants exist
  - L299 most households are unskilled, as most jobs are
  - L301 no household sits in a tier with no jobs
  - L307 almost nobody is left unhoused
  - L324 a real share of adults live alone
  - L326 ...but living alone is not the whole city
  - L336 households are a plausible size
  - L343 rebuilding is deterministic
- **L345 3b. homes, and the squeeze**
  - L369 a house is one home
  - L370 a studio block is many small flats
  - L372 a low-rise flat is bigger than a studio and smaller than a house
  - L374 only the studio refuses a child
  - L376 every block houses what its flats add up to
  - L392 a studio is the cheapest home in the game
  - L413 with room to spare, nobody shares
  - L415 ...and nobody doubles up
  - L422 singles start sharing when homes run short
  - L431 families double up as the last resort
  - L433 and nobody is homeless even then
  - L437 a crowded city reports fewer homes needed than households
  - L443 no homes at all does not throw or invent shares
- **L446 4. AND NOW IT DRIVES THE CITY**
  - L479 at a 50% workforce this is still exactly Jerus's 2.25
  - L481 a city with more adults needs fewer residents per job
  - L483 ...and an ageing one needs more
  - L485 an absurd age structure cannot demand an absurd city
  - L502 a city at target has the slack it was designed for
  - L512 with a home each, nothing damps arrivals
  - L514 at the crowding floor, arrivals stop dead
  - L516 ...but just above it they have not
  - L518 half way down, they are damped but not stopped
- **L522 a door a family cannot enter is not room**
  - L562 the same doors as studios are a HIGHER floor, not the same one
  - L576 a city of studios is past its floor there and takes nobody
  - L578 ...where the same households behind family doors still have room
  - L595 fixture: too few doors, and studios at that, really does leave
  - L598 a city that left somebody nowhere last month takes nobody now
  - L613 a full city with jobs still attracts people
  - L625 jobs pull harder than housing
  - L626 but housing still pulls on its own
- **L629 what pushes people out**
  - L639 a city with too few jobs sheds nobody while wages hold
  - L641 ...because no tier is in decline
  - L651 eleven months of decline is not yet enough
  - L661 a full year of decline finally moves people out
  - L662 and they leave slower than they arrived
- **L665 DECLINING IN WHAT IT BUYS, NOT IN WHAT IT SAYS (2026-09-08)**
- **L686 a tier declines when its pay buys less, not when it counts less**
  - L698 two years of falling PRICES is not two years of decline
  - L700 ...so nobody is pushed out of a city that is merely cheaper
  - L716 fixture: the cash bill really did rise the whole way
  - L718 ...and a tier losing its real pay is in decline anyway
  - L720 ...so the people in it are entitled to go
  - L727 a tier that stops paying entirely counts as declining
  - L743 a growing city still loses people to a trade that is dying
  - L745 ...while gaining more than it loses
  - L769 a trade that merely slipped costs the city almost nobody
- **L778 a city, played**
  - L818 a city does not fill in a single month
  - L820 ...but it does fill, given a decade
  - L874 ...and does not run away entirely
  - L878 ...because a city past its target stops pulling people in
  - L900 the workforce is the city's adults, within a month's growth
  - L931 ...and the share is the pyramid's, not a constant
- **L935 5. and it survives a save**
  - L956 the city has a pyramid
  - L962 the pyramid came back
  - L963 ...and the households with it
  - L966 
  - L988 a declining streak survives a save
  - L993 ...and a malformed one is refused, not half-read
  - L1003 a malformed pyramid is refused, not half-read
- **L1069 the pyramid survives a band being added**
  - L1077 the names travel beside the pyramid
  - L1083 restored by name:
  - L1085 and the births beside it
  - L1086 and the deaths
  - L1105 order does not matter:
  - L1125 an unknown band shifts nothing:
  - L1127 ...and the births are still the births
  - L1128 ...and the stranger is not in the pyramid
  - L1138 the legacy format is five bands, in writing
  - L1144 a nameless save puts babies in BABY
  - L1145 ...and seniors in SENIOR
  - L1146 ...and reads births after the fifth
  - L1147 ...and deaths after that
  - L1148 ...and nobody else is in the city
  - L1163 a save from before the names carries none
  - L1167 a real old save: its adults are its adults
  - L1169 ...its seniors are its seniors
  - L1171 ...last month's births are births, not a band
  - L1173 ...and the city is the size it was saved at
  - L1181 a malformed named pyramid is refused too
- **L1210 the families and the sick ring survive it too**
- **L1219 families**
  - L1221 the fixture actually has families to save
  - L1226 the families round-trip by name
  - L1236 a today-save read as five bands and thirteen shapes is refused whole
  - L1260 ...and a band from the future shifts nothing behind it
  - L1265 a malformed family array is refused whole
- **L1267 the ring**
  - L1270 the ring restores by name
  - L1271 ...and round-trips
  - L1280 a today-ring read as five bands is refused whole
  - L1292 a five-band ring from before the names still loads
  - L1293 ...and it put somebody in the ring
  - L1296 a ring is refused whole when the width does not match its names

## RailCheck.java - 33 labelled assertions

> The railway: what it charges, who pays it, and what it does to the band.
> 
> WHY THIS HARNESS EXISTS. TradeCostCheck proved the decomposition - that every
> delivered price is still the literal it was, and that three quarters of the
> wedge is freight. This one is about the business that now charges for that
> freight, and it has four claims to hold that the earlier one cannot:
> 
>   1. A CITY WITH NO RAILWAY IS THE CITY IT WAS, to the bit, on all four
>      prices - the band and the net pair both. Everything below only matters
>      if this holds, because it is the promise that a mechanic this large did
>      not quietly move every existing save.
> 
> ...

- **L69 1. A CITY WITH NO RAILWAY**
- **L73 a city with no railway quotes what it always quoted**
  - L89 every good's band and net price is the literal, to the bit
  - L106 ...and at an exchange rate that is not one, still to the bit
- **L108 2. THE TWO HALVES ADD UP**
- **L112 what leaves the band plus what the railway bills**
  - L152 the shipper pays the blended rate, both directions, 25 pairs
  - L160 a railway carrying everything at the lorry rate changes nothing
  - L169 ...and at its floor the wedge is the world's margin plus a real freight
- **L174 3. THE LAND, THE ROAD AND THE CATALOGUE**
- **L178 a terminal is trucks, not an office**
  - L195 the catalogue has rail in it
  - L196 ...whose three loads still add to the one, to the bit
  - L197 ...and whose road load is mostly the drayage, not the staff
  - L210 the ladder is cheaper per tonne the further up it you go
- **L215 and the road it takes the ore off**
  - L226 a fully railed city still carries its freight's last mile
  - L230 ...and its commuters are not on the train at all
  - L233 ...so the network is relieved, and nowhere near halved
  - L239 ...and a city with no railway is back where it started, to the bit
- **L244 4. THE SECTOR, IN A CITY**
- **L248 and the business, in a city that trades**
  - L277 a city that trades steel has freight to move
  - L281 ...and the band agrees with what the railway says it is carrying
  - L309 track laid and no trains bought yet carries nothing
  - L314 ...and the month after it has the trains, it carries
  - L327 every dollar the railway billed is on a shipper's cost line
  - L331 ...and the shippers' opened cost line still adds to the closed one
  - L346 the railway's fuel and its locomotives are both imports
  - L352 ...and it bought the fleet its track needs
  - L357 it is carrying what it built to carry
  - L363 ...and the band moved for it
- **L389 and the quote prices the track**
  - L397 a railway sized to its city bills about what the rule allows it
  - L401 ...and is inside its own bounds while it does
  - L410 four times the track it needs, and it cannot charge for it
  - L416 ...so it pins at the lorries' price, which is its ceiling
  - L419 ...and earns LESS on four times the capital
  - L424 ...but never quotes below its floor, because freight needs profit
- **L429 5. AND IT SURVIVES A RELOAD**
- **L433 and what it was charging survives a reload**
  - L441 the quote came back
  - L448 ...and so did what it was carrying, stream by stream
  - L449 ...and its locomotives came back with it
  - L452 ...and the band the reloaded city quotes is the one it was saved with

## ReadPathCheck.java - 10 labelled assertions

> Reading the city must not change the city.
> 
> WHY THIS EXISTS
> 
> Three of the worst bugs this project has had were the same bug:
> 
>   printCommercialInfo()      banked a month of net income every time it ran,
>                              so opening the sector screen twice paid the
>                              shops twice
>   getIndustrialTaxIncome()   recomputed industry's month from live fields and
>                              rewrote two report figures doing it, so a
>                              reloaded city collected $0 where the live one
> ...

- **L280 a city with money moving in every sector**
  - L317 every sector is actually trading
- **L323 the FIRST read, which is the hard one**
  - L375 one pass over the screens moved nothing
  - L392 the live sale figure IS the one in the ledger
- **L396 read it, and read it again**
  - L421 reading the city fifty times changed nothing
- **L423 and the specific one item 7 was about**
  - L479 every one of the thirteen pantries fell by what sold and rose by what arrived
  - L486 ...and the statement never sold more than was in stock
  - L488 ...and the shelf never goes negative
- **L491 the tax the city takes is the tax it shows**
  - L499 business tax collected == business tax printed
  - L503 ...and it is the companies taxed separately, not netted
- **L508 a rate change reaches the treasury at once**
  - L529 doubling the rate moves the very next month's commercial tax

## RestaurantsCheck.java - 32 labelled assertions

> A meal out is food, and it is the same food.
> 
> WHAT THIS HAS TO PROVE, and none of it is "the sector makes money" - whether
> it does depends on the city, which is the point of it:
> 
>   1. A MEAL IS A NINETIETH OF A PERSON-MONTH, in both directions, and the
>      kitchens buy the reference basket in exactly that proportion. If the
>      two halves of that conversion ever disagree, a Diner either feeds
>      thirty times the people it can or starves them, and nothing else in the
>      game would notice.
> 
>   2. THE MARGIN IS STRUCK AGAINST THE TABLES, on GoodsMarket.strike()'s own
> ...

- **L86 1. a meal is a ninetieth of a person-month**
  - L89 three a day, thirty days
  - L90 ...and the same fact the other way up
  - L107 the catalogue has both kitchens
  - L110 ...and a shop to measure the basket against
- **L128 2. the margin, struck against the tables**
  - L132 the sector is registered under its saved name
  - L160 the food in a meal costs something
  - L169 a city with no kitchens has no tables
  - L170 ...so the margin sits at its floor
  - L179 a Diner is a Diner's worth of tables
  - L189 empty tables charge the floor
  - L190 ...a kitchen as full as it is big charges the middle
  - L192 ...and a queue round the block charges near the ceiling
  - L195 ...monotone between them
  - L233 ...and clears them at half its tables, mid-band, after the sales tax
- **L239 3. a meal eaten is food, at the grocer's price**
  - L288 a household that ate nothing is wholly short
  - L289 ...ninety dinners feeds one of the two of them
  - L290 ...and a hundred and eighty feeds the household
  - L298 ...and a dinner at a hundred times the price feeds exactly the same
  - L328 a dinner is eaten once: the month after, the household is short again
- **L331 3b. appetite, not money, is the ceiling**
  - L354 a city that could buy anything still only eats a third of its meals out
  - L362 ...and a household that cannot afford a third of them eats fewer
- **L366 4. the kitchens eat the city's own food**
  - L378 the kitchens restocked themselves without ever having served
  - L383 ...and they serve what the tables and the larder allow
  - L392 ...off every one of the thirteen, in the basket's proportions
- **L398 5. the bootstrap**
  - L430 a city with a queue and no kitchens has sold nothing at all
  - L434 ...and with nobody at the door it does not want one either
  - L441 ...but a queue at a door that is not there is a reason to build one
  - L452 ...and the sector values its own kitchen, because nothing else can
- **L455 and the save**
  - L474 the cell array carries a slot for them
  - L482 ...and the meals are in it
  - L502 a save from before the kitchens is still read
  - L505 ...and that city ate in

## RestructureCheck.java - 61 labelled assertions

> Buying the city's own debt back, at what the paper is actually worth.
> 
> The arithmetic is textbook and the risk is not in the arithmetic. It is that
> a buyback priced off the same market the city moves by borrowing might be a
> MONEY PUMP: issue, buy back, pocket the difference, repeat. Section 5 is the
> reason this file exists, and it is written to try to break the feature rather
> than to demonstrate it.
> 
> The rest is the property that makes the button worth having at all: market
> value is not face value, and which side of face it lands on says something
> true about the city's credit.

- **L46 1. the present value, against hand arithmetic**
  - L63 discounted at its own coupon, a bond is worth par
  - L71 rate above the coupon -> below par
  - L72 ...which is a discount
  - L74 rate below the coupon -> above par
  - L77 ...which is a premium
  - L85 a T-Bill is a pure discount instrument
  - L87 ...worth less than its face while it has time to run
- **L90 2. the edges**
  - L94 a bond due now costs its face to clear
  - L96 a zero rate discounts nothing
  - L104 the same rate move hurts long paper more
- **L108 3. a real city buying real paper back**
  - L117 the city has a bond
  - L124 it is quoted a price
  - L130 it paid exactly what it was quoted
  - L131 ...and the cash came out
  - L132 the bond is off the books
  - L133 ...and the principal went with it
  - L137 buying the same bond twice does nothing
  - L147 a city that cannot afford it is refused
  - L149 ...and was not charged anyway
  - L150 ...and still owes it
- **L152 4. the credit story**
  - L170 at its own coupon it is worth par
  - L171 after the city's credit worsens, it is cheap to retire
  - L173 ...so the city books a real gain
- **L176 5. THE ONE THAT MATTERS: is it a money pump?**
  - L225 fixture: the dollar city earns abroad, and the world lends it for less than it lends itself
  - L265 a
  - L267 ...and eight of them do not either
  - L269 nothing is left outstanding after a round trip
- **L273 5b. the serial bond, on its own terms**
  - L278 ten annual slices
  - L279 ...of a tenth each
  - L280 coupon starts on the whole balance
  - L284 one payment a month for ten years
  - L297 a serial pays materially less interest than a bullet
  - L299 ...but not zero
  - L302 at its own coupon it is worth par
  - L326 principal actually fell over the first year
  - L342 ...and the coupon fell with it
- **L345 5c. the note knows its own term**
  - L348 three months discounts a quarter of the annual rate
  - L350 twelve months discounts the whole of it
  - L352 twenty-four months, twice
  - L355 a short note costs less than a long one for the same cash
  - L360 ...which it did NOT before, when every term priced alike
  - L364 the discount is capped rather than going negative
- **L367 5d. yield to maturity**
  - L372 bought at par, the yield IS the coupon
  - L374 bought at a discount, the yield is higher
  - L376 bought at a premium, the yield is lower
  - L388 and it round-trips for an amortising schedule too
  - L391 par is quoted as 100
  - L392 a discount quotes below 100
- **L395 6. and it survives a save**
  - L414 two bonds
  - L416 one bought back
  - L424 saved
  - L430 the retired bond did not come back
  - L432 ...and the principal matches
  - L434 ...and so does the cash
- **L437 all three instruments round-trip**
  - L463 three instruments issued
  - L470 all three came back
  - L471 ...owing the same
  - L474 
  - L476 
  - L484 the serial kept its slices
  - L485 ...and its slice size

## RobustnessCheck.java - 57 labelled assertions

> What the game does when something is already broken.
> 
> Every other harness checks that the game works. This one checks that it fails
> legibly - which matters more in a shipped build than in development, because
> the developer has a console and the player has a window that either explains
> itself or does not.
> 
> THE BUG THIS WAS WRITTEN FOR
> 
> A truncated save threw JsonSyntaxException out of loadGame(). That is a
> RuntimeException, so it went straight through the catch (IOException) sitting
> right there. readHeader() failed safely and the menu labelled the slot
> ...

  - L63 a good save to work from
- **L67 1. a save cut in half**
  - L73 the file is there
  - L74 ...and is NOT reported as empty
  - L75 ...it is reported as unreadable
  - L76 ...and therefore not loadable
  - L88 loading it does not throw
  - L89 ...it reports a failure instead
  - L90 ...and nothing was applied
- **L92 2. a file that is not JSON at all**
  - L97 also unreadable
  - L98 also not loadable
  - L102 reports a failure
  - L103 nothing applied
- **L105 3. an empty file, and an empty slot**
  - L109 a zero-byte file is unreadable, not empty
  - L112 a slot with no file IS empty
  - L113 ...and is not 'unreadable'
  - L114 ...and is not loadable either
  - L118 loading an empty slot changes nothing
- **L120 4. the good save still loads**
  - L123 slot 1 is loadable
  - L127 it loaded
  - L128 with the city in it
  - L129 ...and its people
  - L134 the damaged files were not deleted
- **L137 5. a save from a newer build**
  - L144 it reads as a file
  - L145 ...its header parses fine
  - L146 ...but it is not loadable
  - L150 and the loader says why
  - L151 nothing applied
- **L153 5b. a save from an OLDER build**
  - L186 a save from before the sectors reads as a file
  - L187 ...its header parses fine
  - L188 ...and it says so
  - L189 ...but it is not loadable
  - L192 and the loader refuses it with a reason
  - L194 nothing applied
  - L208 an older save is loadable
  - L213 it loads without complaint
  - L214 with its city
  - L215 ...and its people
  - L218 a missing workforce is recomputed, not left at zero
  - L220 ...to half the population, which is the documented fallback
  - L227 its income statement is rebuilt, not blank
  - L230 and it keeps running
- **L232 5c. a city whose numbers have overflowed**
  - L254 a healthy city saves fine
  - L267 saving it does not throw
  - L268 ...it reports a failure instead
  - L270 ...and names the field, so the log is a bug report
  - L277 the game is still alive afterwards
  - L278 ...and the earlier good save is untouched
  - L282 ...and still loads
- **L284 5d. a parameter that used to do nothing**
  - L311 an instant build is standing the same month
  - L314 ...and nothing was put in the construction queue
  - L316 ...and the crews were not paid for work they did not do
  - L318 ...and it cost the cash price and no materials
  - L326 an ordinary order is NOT standing yet
  - L328 ...and it did go into the queue
  - L333 an instant build still needs somewhere to stand
- **L336 6. the log**
  - L347 the log lives beside the saves
  - L350 the previous run's log has its own name

## SaveFileCheck.java - 160 labelled assertions

> Verifies where saves go and how they are written.
> 
> This is the only part of the game where a bug destroys something the player
> cannot get back. Everything else can be re-simulated; a save written over
> badly is a city that no longer exists. So the tests here are deliberately
> mean: they fill the target with junk, point the writer at a path it cannot
> write to, and check that in every case the file that was already on disk is
> still exactly what it was.
> 
> Path resolution is tested through the pure resolveDirectory() rather than the
> real environment, because a chooser that reads System.getenv can only be
> tested on the machine it is running on - which is precisely the machine where
> ...

- **L61 1. where it goes**
  - L66 Windows follows %APPDATA%
  - L73 ...and falls back when it is missing
  - L78 ...blank counts as missing, not as a folder named nothing
  - L83 macOS uses Application Support
  - L88 Linux uses the XDG default
  - L93 ...and honours XDG_DATA_HOME when set
  - L99 an unrecognised platform still resolves
  - L103 no home directory still resolves rather than throwing
  - L106 it is never the old folder
- **L109 2. writing safely**
  - L118 save file
  - L119 history file
  - L122 folder does not exist yet
  - L125 first write succeeds
  - L126 ...and says where
  - L127 ...and the contents are right
  - L130 no .tmp left behind
  - L132 nothing to back up on the first write
  - L136 second write succeeds
  - L137 ...the new city is on disk
  - L139 ...and the previous one survives as .bak
- **L142 3. a write that cannot possibly work**
  - L153 a failed write is reported as failed
  - L154 ...with a reason attached
  - L156 ...and the message says so plainly
  - L158 ...and does not claim to have saved
  - L161 no .tmp litter after a failure
  - L166 the existing save is untouched by a later failure
- **L169 4. the old folder**
  - L183 both files came over, both hops
  - L184 the save arrived in Slot 1
  - L186 the history arrived with it
  - L188 ...and the intermediate flat save is left behind too
  - L193 the originals are still there
  - L196 ...and unchanged
  - L200 running it again copies nothing
- **L202 5. it never overwrites a newer save**
  - L212 the save being played is untouched
  - L214 ...and the old history still came over
  - L216 ...without the old save overwriting Slot 1
  - L222 no old folder, nothing to do
  - L230 no file copied onto itself
  - L232 ...but the flat save was promoted to Slot 1
  - L234 ...and the original is intact
- **L237 6. a real round trip**
  - L255 DataSave wrote itself
  - L260 cash survived
  - L261 month survived
  - L262 population survived
  - L263 buildings survived
  - L264 household savings survived
  - L265 land survived
  - L266 tax rates survived
  - L271 no file paths leaked into the save
  - L283 two months recorded
  - L284 HistorySave wrote itself
  - L285 ...to its own file, not over the save
- **L288 7. construction survives a save**
  - L329 something is genuinely mid-build
  - L355 saved
  - L364 depots still under construction
  - L366 stores still under construction
  - L368 and the part-finished work came back
  - L375 land committed to unfinished sites is still committed
- **L378 7b. the warning survives a save**
  - L421 an unprotected city with a fresh shed IS warned
  - L431 the month construction last shed came back
  - L433 ...and the capacity it has sold since
  - L435 ...and it is still unprotected
  - L437 ...so the player is still being warned
  - L442 protecting construction takes the warning down
  - L453 a dismissed warning stays dismissed across a reload
- **L456 8. a save with nothing else built**
  - L469 nothing is finished yet
  - L471 but one is being built
  - L473 saved
  - L477 it did not vanish
- **L480 9. the headline income does not move**
  - L487 property tax survived the round trip
  - L502 the city actually charges retail something
  - L504 retail's property tax expense came back
  - L507 real estate's did too
  - L510 ...and the line on the statement with it
  - L517 the sector charges are part of the city's total
- **L520 10. the whole figure, to the cent**
  - L535 the city has people at all
  - L536 population is not invented by loading
  - L578 goods reached the shops, which the mills had to make first
  - L580 ...so the figures compared below are not all zero
  - L584 business tax
  - L586 wage tax
  - L588 retail cost of goods
  - L591 ...and the month in progress
  - L607 next-month income is identical across a save
  - L610 sales tax
  - L612 monthly GDP
  - L618 construction backlog
  - L620 construction unearned revenue
  - L623 construction cash
  - L635 the city did lose something
  - L636 the demolition log came back
  - L638 ...with its entries intact
  - L641 the write-off record came back
  - L656 the city did finish something
  - L657 the build log came back
  - L659 ...with its entries intact
  - L662 ...and the merged quantities unchanged
- **L666 11. a city that is still MOVING**
  - L723 the city really is still growing
  - L725 ...and its statement describes a smaller month than the one in progress
  - L738 saved mid-growth
  - L746 the workforce that worked the month came back
  - L748 wage tax
  - L750 retail gross revenue
  - L753 ...and the month the shops are in
  - L756 industrial gross revenue
  - L759 sales tax
  - L761 monthly GDP
  - L763 and next month's income, to the cent
- **L766 12. a city that OWES money**
  - L797 the city really does owe something
  - L799 ...and has interest on the books waiting to be charged
  - L805 saved in debt
  - L814 the debt came back
  - L824 fixture: the households hold some of it
  - L826 ...and still do after the reload, cell by cell
  - L829 ...which is what the paper says they hold
  - L832 ...and the discount still to accrete on it came back
  - L835 ...and so did the interest it had already accrued
  - L838 ...so next month's income still shows the deficit
  - L850 and a month later both cities have paid the same bill
- **L854 12b. ...AND ONE THAT OWES ABROAD (2026-09-21)**
- **L868 and a city with dollars owed abroad**
  - L890 fixture: the city really does owe dollars
  - L892 fixture: ...and the rate really did move them this month
  - L895 saved owing abroad
  - L898 what the currency did to the debt this month reloads
  - L900 ...beside the rest of the foreign accounts, which always did
- **L904 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY**
- **L936 and a freshly loaded city reads what the live one reads**
  - L1034 fixture: the bank opened and started paying its savers
  - L1086 saved a city with one of everything in it
  - L1093 fixture: savers really were being paid something
  - L1095 fixture: the schools really were running
  - L1097 fixture: the dial really did pay out
  - L1099 fixture: somebody really was hungry
  - L1101 fixture: the tiers really were shopping
  - L1112 fixture: the city really was paying for repairs
  - L1114 what the city paid to keep its buildings up
  - L1117 what savers are paid
  - L1119 what the dial paid out
  - L1121 the schools' payroll
  - L1123 ...their upkeep
  - L1125 ...the fees they waived
  - L1127 ...and the fees they collected
  - L1129 the hunger inside the sick rate
  - L1132 fixture: the health premium really was collected
  - L1134 the fee scale
  - L1136 ...and the health premium
  - L1138 ...the scale the service charges at
  - L1140 ...what the premium raised
  - L1142 ...what the households paid of it
  - L1144 ...the treatment bill at full service
  - L1147 row
  - L1149 row
  - L1158 households doubled up
  - L1160 ...and the ones a studio turned away
  - L1164 tier
  - L1166 tier
  - L1169 the government's surplus
  - L1172 ...and the business tax inside it
  - L1185 fixture: the treasury's journal had lines in it
  - L1186 the treasury's journal has as many lines as it had
  - L1188 ...line
  - L1190 ...and still says
  - L1193 ...and what the journal left unexplained

## SaveSlotCheck.java - 51 labelled assertions

> Verifies the slot system: ten saves plus an autosave, the version stamp, and
> the labels the menu is drawn from.
> 
> The thing this is really guarding is INDEPENDENCE. A save menu that shows ten
> slots and quietly writes them all to the same file, or draws slot 3's label
> from slot 7's city, is worse than a single save - it invites a player to
> spread a hundred hours across ten slots that were never really there.

- **L47 1. eleven distinct files**
  - L55 22 distinct paths, no two slots sharing
  - L57 the autosave is named for what it is
  - L62 slot 2 sorts before slot 10
  - L66 slot 0 is valid
  - L67 slot 10 is valid
  - L68 slot 11 is not
  - L69 slot -1 is not
- **L71 2. empty means empty**
  - L81 and an empty slot has no header
- **L83 3. three cities that do not touch**
  - L91 saved to slot 1
  - L99 saved to slot 7
  - L104 saved to slot 10
  - L106 slot 1 exists
  - L107 slot 7 exists
  - L108 slot 10 exists
  - L109 slot 4 is still empty
  - L110 slot 2 is too, for now
  - L117 slot 1 still holds the month it was saved at
  - L119 slot 7 holds its own
  - L120 and slot 10 is later still
  - L122 slot 1's population is its own
- **L124 4. names**
  - L127 the name was kept
  - L128 ...and reported as present
  - L129 an unnamed slot says so
  - L141 re-saving keeps the existing name
- **L144 5. the version stamp**
  - L147 the build that wrote it
  - L148 the save format
  - L149 and when
  - L150 this build is not from the future
  - L153 a save from a newer build is recognised
  - L155 an older one is not - those still load
  - L164 the header sees it
  - L168 and the load path refuses it
  - L169 with nothing loaded
- **L171 6. each slot round-trips into its own city**
  - L176 slot 1 loads the early city
  - L177 ...with its own population
  - L179 ...and no failure
  - L183 slot 10 loads a later city
  - L186 loading one slot does not touch another
- **L189 7. the autosave**
  - L200 nothing autosaved yet
  - L201 twelve months to go
  - L208 still nothing at month
  - L210 one month to go
  - L213 the twelfth month writes it
  - L215 and the counter starts again
  - L219 it says it is an autosave
  - L221 ...and holds the month it fired at
  - L228 a multi-month skip autosaves on the way in
- **L239 8. histories are per slot**
  - L242 slot 1 has a history
  - L243 slot 10 has its own
  - L244 ...and they are different files
  - L246 ...with different contents

## SectorBooksCheck.java - 13 labelled assertions

> Plays a city and audits every sector's statements, every month. Not part of
> the game.
> 
> WHAT THIS IS FOR. BooksCheck already proves the food industry's statement is
> right against hand arithmetic on one fixture. This proves something different
> and, for a screen, more important: that the SAME three statements hold for
> all six sectors, on a city that is actually running, for a hundred months
> together - because a reporting layer that is correct in isolation and wrong
> on a live city is a reporting layer that lies to the player.
> 
> The three things it will not let past:
> 
> ...

- **L120 the sheet**
  - L121 balance sheet
- **L124 the income statement**
  - L125 operating
  - L128 pre-tax
  - L132 net
- **L135 the cash flow**
  - L136 cash flow
  - L162 revenue by good
  - L163 cost of sales by good
  - L176 exports by good
  - L177 imports by good
  - L192 other revenue named
- **L211 AND IT HAS TO SURVIVE A SAVE.**
  - L229 survives a save
  - L231 ...and its cash
- **L243 and the month after**
  - L246 cash flow across a reload

## SicknessCheck.java - 51 labelled assertions

> The long sick: who stays sick, and who it kills.
> 
> Jerus, 2026-09-11: sick people who stay sick start dying; the base death
> rate halved for every band but babies and seniors; general care saves lives
> by curing people, not by scaling a death rate. Every claim below sets its
> own cause. See claude/the-long-sick.md.

- **L55 1. the base rates**
  - L57 children: half the life table's 0.015%
  - L58 teens: half of 0.04%
  - L59 adults: half of 0.45%
  - L60 babies untouched
  - L75 seniors, 70 to 85, from the life table
  - L76 elders, 85 to 120, from the life table
  - L77 and the split brackets the flat rate it replaced
  - L81 general care no longer scales
  - L83 ...or everybody covered
- **L86 2. how much of each band is sick**
  - L93 babies with no childcare: twice the city rate
  - L94 ...with half of it, one and a half times
  - L95 ...with childcare for everybody, the city rate
  - L96 seniors with no senior care: twice
  - L97 ...with it, the city rate
  - L98 no band is ever sicker than the ceiling
- **L101 3. the ring: recovery, and nobody dies at once**
  - L105 fixture: a well city's ring is empty
  - L107 the month they fall ill, they are all in the first slot
  - L108 ...and nobody can die of it yet
  - L110 with no general care, half of them are better a month later
  - L112 ...the city rate is met by new cases
  - L113 ...and two months sick is still not enough to die of it
  - L117 past two months, what is left of them can die
  - L118 ...at the adults' 2% a month
  - L125 with general care for everybody, nine in ten are better in a month
  - L130 the dead leave the ring: a slot three months on keeps (1-h)(1-r)
- **L134 4. each age's chance, and the steady state**
  - L136 babies 4% a month
  - L137 children 1%
  - L138 teens 1%
  - L139 adults 2%
  - L140 seniors 4%
  - L152 steady state,
  - L160 ...and a month at the same rate leaves it where it was
  - L166 when the rate halves, the adults sick are the new rate
  - L169 ...and nobody's slot is favoured
  - L183 a city with no care loses far more
  - L194 fixture: the hungry city is sicker
  - L195 ...so more of it dies of illness
- **L198 5. the pyramid**
  - L209 the adults who died of illness are the opening band times the ring's rate
  - L211 ...on top of everybody else's deaths
  - L214 a pyramid with no ring has no illness deaths
- **L216 6. a city**
  - L247 fixture: the hospitals gave the second city more coverage
  - L249 the adults' share of the ring is the city's sick rate - output is untouched
  - L251 the city with no hospitals lost people to illness
  - L254 ...many times more, per head, than the city with them
  - L257 the ring's dead are the pyramid's dead
  - L258 every month passed the money audit
- **L260 7. a save, and a save from before**
  - L263 fixture: the ring has something in it
  - L270 the whole ring comes back
  - L271 a malformed ring is refused whole
  - L275 fixture: the ring is gone
  - L277 a city with no ring seeds one and loses its long sick the same month

## SkipReportCheck.java - 62 labelled assertions

> Verifies the fast-forward summary.
> 
> The reason this needs testing at all is that half of it cannot be derived
> from the endpoints. A city that ran out of power for forty months and then
> built a second station looks, at both ends, exactly like one that never had a
> problem - so the episode counters have to be sampled, and a sampling bug
> would silently report a smooth century on a city that spent it starving.

- **L42 1. a hundred good months**
  - L65 complete
  - L66 months
  - L67 did not stop early
  - L68 from month
  - L69 to month
  - L71 population change
  - L72 cash change
  - L73 cash per month
  - L74 jobs
  - L75 monthly GDP
  - L76 business debt fell
  - L77 blocks bought
  - L80 annualised growth
- **L85 2. what got built**
  - L89 three types moved
  - L92 largest change first
  - L93 ...by 118
  - L95 gained
  - L96 nothing lost
  - L100 unchanged types are omitted (
- **L104 3. a century that went badly**
  - L128 months short of power
  - L129 months congested
  - L130 worst throughput
  - L131 worst brownout
  - L132 months with no land
  - L133 months households were short
  - L134 months nothing was built
  - L135 half the time idle
  - L137 eight depots gone
  - L138 nothing gained
  - L139 written off during the skip
  - L143 peak
  - L144 ended below its peak
- **L146 4. the headlines**
  - L150 it has something to say
  - L165 mentions the land
  - L166 mentions the power
  - L167 mentions the demolitions
  - L168 mentions the write-offs
  - L169 mentions the households
  - L170 mentions the decline
  - L174 one line
  - L175 ...and it is the reassuring one
- **L178 5. stopping early**
  - L190 only twelve ran
  - L191 flagged as short
  - L192 and said so first
- **L195 5b. the central bank's advances (0.7.1)**
  - L206 six months on advances
  - L207 ...three of them at the ceiling
  - L212 and the report says it, where it once promised emergency debt
- **L214 6. nothing to report**
  - L218 not complete
  - L219 no change to report
  - L220 ...nor cash
  - L221 no growth rate
  - L222 no buildings
  - L223 and no headlines
  - L230 no population, no growth rate
  - L231 no months sampled, no cash rate
  - L232 ...nor an idle share
- **L234 7. beginSkip clears the last one**
  - L238 power counter cleared
  - L239 congestion counter cleared
  - L240 land counter cleared
  - L241 months cleared
  - L242 worst energy back to full
  - L243 and it is no longer complete

## StaleCheck.java - 11 labelled assertions

> The prose still describes the code: the firm half of `tools.Stale`, asserted.
> 
> WHAT THIS HAS TO PROVE, and none of it is about what the game does - a
> finding here is a sentence that has stopped being true, not a mechanic that
> has stopped working:
> 
>   1. THE TOOL SEES A DEFECT WHEN THERE IS ONE. A fixture tree is written
>      with three planted lies in it - a javadoc with no member under it, a
>      comment naming a source file that is not in the tree, and a class
>      header that miscounts its own banners - and each one has to come back.
>      A checker that cannot fail is the failure mode this harness itself is
>      most exposed to, because the tree it reads is usually clean.
> ...

- **L84 no source tree**
- **L90 1. the tool sees a defect**
  - L104 the fixture is two files
  - L105 a javadoc with no member under it is found
  - L107 a comment naming a source file that is not in the tree is found
  - L109 a header that miscounts its own banners is found
- **L112 2. ...and nothing when there is none**
  - L115 no orphaned javadoc in the sound file
  - L117 ...no missing file
  - L118 ...and no stated count, because its header counts right
- **L123 3. the tree itself**
  - L128 the scan read the whole tree, not an empty one
  - L131 no javadoc is left documenting nothing
  - L132 nothing names a source file or a docs/ page that is not there
  - L133 every stated count is the count

## TradeCostCheck.java - 30 labelled assertions

> The wedge between what the world charges and what it pays, and what it is
> made of.
> 
> WHY THIS HARNESS EXISTS. Until today the two world prices were two constants
> and nothing tested them, because there was nothing to test: a constant is
> either right or it is a balance decision, and neither is a harness's
> business. They are arithmetic now - a world margin the player can never move
> and a freight cost that will one day be a business's price - and arithmetic
> has invariants.
> 
> WHAT IT HAS TO PROVE, and the first one is the whole of step one:
> 
> ...

- **L110 not one delivered price moved**
  - L120 ...and every good the world trades is in that table
- **L123 and the wedge adds up**
  - L143 ...and freight is three quarters of the half-wedge
- **L148 and nothing unshippable is charged for shipping**
- **L171 the traffic split (2026-09-16)**
  - L198 every building's three loads add to its road load, to the bit
  - L200 ...and there were buildings to check
  - L222 a shop's freight is GOODS, not BULK - a van at a back door
- **L233 and the network's total did not move**
  - L254 the network's total is the sweep it has always been, to the bit
  - L259 ...and the breakdown accounts for all of it
  - L262 ...and a real city is mostly people, not ore
- **L269 the modes (2026-09-16)**
  - L305 ...and costs more per unit moved than the rung below
- **L312 and the road can be halved and never deleted**
  - L339 a city with neither is asking for exactly what it always asked for
  - L343 fixture: and it really is jammed
  - L348 no more than
  - L354 ...so the road still carries every crate and every tonne
  - L363 a city that builds transit and not streets gets less of it
  - L369 ...and a city with no streets at all gets less again
  - L378 ore on a grade-separated road costs the network less
  - L381 ...and a commuter costs what a commuter always cost
  - L384 ...so a highway helps bulk more than it helps goods
  - L393 with the best of everything the road still carries a third and more
- **L402 and two businesses on the same road do not have the same problem**
  - L410 a jammed city with good transit keeps its offices working
  - L424 ...but a lorry in a jam is in a jam, whatever the city built
  - L427 ...so the mill beats the road only by as much as its payroll rides
  - L433 ...and a sector with nothing standing reads the city, not a clean road
- **L437 the fare (2026-09-16)**
  - L445 free transit carries everyone the ceiling allows
  - L452 ...and at the default fare almost all of them still ride
  - L458 ...and at the ceiling fare nobody does
  - L475 a dearer fare takes more money and puts more cars on the road
  - L501 a month of riding costs a commuter a share of a wage somebody would recognise
  - L509 a currency reform divides the fare like every other price
  - L513 ...and nobody changes their mind about the bus because of it

## TreasuryCheck.java - 39 labelled assertions

> Plays a city and audits what the screens say the treasury did. Not part of
> the game.
> 
> WHY THIS EXISTS. Jerus: "show how much was the actual month change, like in
> the next month button it shows 3k but sometimes cause of land buybacks or
> sales it was actually more or less." The dome and the Government Overview now
> both print a measured cash movement, and a measured figure that is measured
> wrongly is worse than the estimate it replaced - it looks authoritative.
> 
> The six things it will not let past:
> 
>   1. THE WINDOW CLOSES. Each month's opening balance is the previous month's
> ...

- **L123 1. no gap between windows**
  - L125 window opens where it closed
- **L129 2. the closing IS the cash**
  - L130 closing balance is the cash
- **L133 3. the bridge foots**
  - L134 the bridge foots
- **L141 5. and on a hands-off city there is nothing in it**
  - L143 nothing the budget cannot explain
- **L152 AND AGAIN WITH THE SUBSIDY DIAL ON.**
  - L178 subsidised: window opens where it closed
  - L181 subsidised: the bridge foots
  - L187 subsidised: nothing the budget cannot explain
- **L199 AND IT HAS TO SURVIVE A SAVE.**
  - L219 survives a save
  - L221 ...and its closing balance
  - L233 ...and the budget behind it
  - L236 ...including what the city spent on buildings
  - L239 ...and what it paid in interest
- **L243 and the first month back still has no gap**
  - L245 window survives a reload
- **L248 AND THE ROW OPENS.**
- **L262 and the row that says \"everything else\" opens into lines**
  - L273 fixture: the land office listed a plot the city can afford
  - L278 fixture: the city paid for ten houses
  - L284 fixture: capital went into the bank and reserves were bought twice and sold once
  - L296 fixture: the city issued a note and a serial bond
  - L301 fixture: and bought the note straight back
  - L313 the land is on the budget's own line
  - L314 ...so the journal does not name it a second time
  - L316 the houses are on the budget's own line
  - L317 ...so the journal does not name them a second time
  - L321 the journal carries the capital put into the bank
  - L323 ...the reserves bought, two purchases folded into one line
  - L325 ...the reserves sold, on a line of their own
  - L327 ...and the bond bought back, for what it cost
  - L329 ...and nothing the treasury did not do
  - L333 the paper raised is on the bridge's own row, not in the journal
  - L339 the bridge foots through the journal
  - L343 the journal explained more than it left over
  - L362 ...and what it left over is the first coupon's timing, to the cent: the bank's share of it
  - L371 a city that did nothing has an empty journal, and the row stays a row
  - L392 last month's journal survives a save, line for line, in order
  - L393 ...and so does the residual under it
  - L395 ...and the paper raised on the row above
  - L397 ...and the month in progress, which the next strike will count
  - L400 ...so the reserves bought before the save are on the next month's line
  - L402 ...and the note issued before the save is on its raised row
  - L407 a save from before the journal loads with an empty journal, not a broken one
- **L411 THE REPORT.**

## VanCheck.java - 21 labelled assertions

> The vans: what a sector needs, what it costs it, and what happens while it
> waits for them.
> 
> THE FIFTH THROTTLE, and the first one a business buys. Energy, water, road
> and health arrive from outside - the city builds the plants, lays the streets
> and staffs the clinics - and a sector takes whatever it is given. A lorry is
> the sector's own capital: it decides how many it needs, it pays for them, it
> replaces them when they wear out, and if it cannot get them its output falls.
> 
> Jerus asked for exactly that: "a constraint - a sector with too few vans can't
> move what it makes; its operating rate falls."
> 
> ...

- **L99 1. WHAT A FLEET IS FOR**
- **L103 a sector's fleet is sized by what its plant moves**
  - L108 a sector that moves nothing needs no vehicles, and its ratio is one exactly
  - L114 ...and one that moves something needs vehicles in proportion
  - L120 ...and a sector with vehicles enough multiplies its rate by one, to the bit
  - L137 what goes in is a lorry movement as much as what comes out
- **L141 2. THE FLOOR AND THE CEILING OF THE RATIO**
- **L145 and what being short of them does**
  - L153 a sector with no lorries at all is slowed to the floor, not stopped
  - L158 ...and half a fleet is half way up from the floor
  - L164 ...and a sector with more than it needs gets no bonus for them
  - L167 fixture: the mills move something, or this proves nothing
- **L171 3. YOU CANNOT PUT A FLEET ON THE ROAD IN A MONTH**
- **L175 a fleet is built up, not bought**
  - L178 a sector with nothing asks for what it can take delivery of, not for everything
  - L183 ...and one that is nearly there asks only for the gap
  - L188 ...and one with enough asks for nothing
- **L192 4. IT WEARS OUT**
- **L196 and a fleet is a stock, not a flow**
  - L201 a fleet wears out at one part in VAN_LIFE_MONTHS a month
  - L210 ...and ten years of it with nothing bought is a fleet gone
  - L220 ...which leaves the sector down at its floor
- **L227 5. A SAVE FROM BEFORE VANS HAD VANS**
- **L231 and every city that exists already owns a fleet**
  - L246 a save from before vans reads as a sector that has never been asked
  - L248 ...and is not stopped by it, because an unasked sector runs at one
  - L252 ...while a save from this build carries the fleet it had
- **L257 6. IN A CITY THAT RUNS**
- **L261 and the city really buys them**
  - L273 a city with industry in it runs a fleet
  - L275 ...and pays for it, every month, on somebody's cost line
  - L279 ...at a price the market struck, inside its band
  - L292 the city's own industry is the market for a van plant

## WaterCheck.java - 34 labelled assertions

> Sanity harness for water production, demand, throttling and billing. Not part of the game.

- **L37 1. template draws**
  - L39 House
  - L40 Small Grocery Store
  - L41 Industrial Bakery
  - L42 Coal Power Plant
  - L43 Water Treatment Plant
- **L45 2. demand = buildings + people**
- **L60 demand**
  - L61 building draw
  - L62 resident draw
  - L63 total draw
  - L64 supply (base wells only)
  - L65 ratio - base still covers it
- **L67 3. the ratio bites**
- **L77 outgrowing the wells**
  - L78 total draw
  - L79 supply
  - L80 ratio is rationing
- **L83 4. a plant fixes it**
- **L90 one water plant**
  - L91 supply
  - L92 draw grew by the plant's own use
  - L93 ratio back to 1
- **L95 5. split books**
- **L99 split books**
  - L100 electricity payroll
  - L101 water payroll
  - L102 combined payroll
  - L106 billed draw
  - L107 unbilled draw
  - L110 water revenue (billed only)
  - L111 water income
  - L112 consolidated = electric + water
  - L114 consolidated revenue = sum
- **L125 both plants staffed**
  - L126 electricity payroll
  - L127 water payroll unchanged
  - L128 combined payroll
- **L130 6. the ratio throttles output**
  - L140 industrial output at full water
  - L142 industrial output at half water
- **L144 7. billing is symmetric with power**
  - L151 commercial water bill
  - L156 industrial water bill
  - L166 ratio
- **L169 8. household affordability**
- **L172 coherence**
  - L173 household water bill (thousands)

## YearBookCheck.java - 82 labelled assertions

> Proves the year book folds each series the way that series has to be folded,
> and that the file says so. Not part of the game.
> 
> WHY THIS IS WORTH A HARNESS
> 
> The year book's whole value is that somebody who has never seen this game can
> read a run off it. That makes a wrong fold worse than no file at all: a
> population summed over twelve months reads as a city twelve times its size,
> and reads as a FACT, with no way for the reader to catch it. Every assertion
> here is on the text the reader actually gets, not on an internal the reader
> never sees.
> 
> ...

  - L89 the fixture actually produced crime-by-cause series
  - L90 the fixture actually produced households-by-shape series
  - L91 the fixture actually produced a share register
  - L104 kind is declared for
  - L105 a note is declared for
  - L117 year 1 gdp is its twelve months added
  - L118 year 2 gdp is its twelve months added
  - L119 gdp is marked as a flow
  - L129 year 1 population is December's
  - L130 year 2 population is December's
  - L131 population is marked as a level
  - L147 year 1 sick rate is the mean of its months
  - L148 the year's worst month is kept
  - L149 and its best
  - L150 sickRate is marked as a rate
  - L169 a flow row missing months is blank
  - L170 and the complete row is the sum
  - L171 the file says what a blank means
  - L182 a full year covers twelve months
  - L183 the stub year says it is one month
  - L184 and its flow is that one month
  - L194 a full decade covers ten years of months
  - L196 the stub decade carries the remainder
  - L197 the first decade's flow is its months added
  - L198 the decade book calls its rows decades
  - L215 the episode list names the failure
  - L216 and says which month it was
  - L217 the year's own row hides it, which is why the list exists
  - L232 a decimal point survives a French locale
  - L233 ...and so does a small one
  - L234 ...and a big one
  - L235 a thousand is written without a separator
  - L236 nothing is recorded is not zero
  - L237 and zero is zero
  - L250 no comma in a data row:
  - L252 and there were data rows to look at
  - L263 the year book sits in the game's own folder
  - L265 so does the decade book
  - L267 and they are not the same file
  - L295 the history records the pool the People screen shows
  - L302 with the pool recorded, the pool over the labour force
  - L304 the column says what it is
  - L308 without the pool, the labour force less the posts, over the labour force
- **L363 the premise**
  - L364 the fixture has somebody studying, or the two denominators are the same number
  - L366 the fixture offers posts it has not filled, or jobs and filled posts agree by luck
  - L373 ...so the formula this replaced actually disagrees with the model here
- **L376 and the assertions**
  - L396 the labour force is the model's labour force
  - L398 the filled posts are the model's filled posts
  - L401 unemployment is the model's own rate
  - L412 the average wage divides the recorded wage bill by the filled posts
  - L417 ...and the recorded wage bill is the model's, to the cent
  - L464 the fxRate note says Danzik dollars per US dollar
  - L465 ...and that higher is a fallen currency
  - L466 ...and never the other way round
  - L502 the fixture names two episodes and no others
  - L505 the first is the bank's
  - L506 ...named for the year it began
  - L508 ...from the month equity went under
  - L509 ...to the last month it was under
  - L510 ...and its worst is the equity it reached
  - L512 the second is the recession
  - L513 ...named for the year it began
  - L515 ...from the month the year fell short of the one before
  - L516 ...for exactly EPISODE_MIN_MONTHS months
  - L518 ...and its worst is a fall
  - L522 the chart shades the same recession and nothing else
  - L524 ...from the month it began
  - L525 ...to the month it ended
  - L529 the book lists the crisis, one line with its months
  - L531 ...and the recession
- **L534 the edges**
  - L537 a two-month dip under water is not a crisis
  - L542 a loss that leaves one month below the year before is not a recession
  - L544 ...and is not shaded
  - L550 two failures a year apart are two episodes
  - L552 ...with two names
  - L553 ...the first for its year
  - L554 ...the second for its own
  - L561 two failures in one year, EPISODE_JOIN_MONTHS apart, are two episodes
  - L563 ...and the second is the first's name, again
  - L572 two failures with less relief than EPISODE_JOIN_MONTHS are one episode
  - L574 ...from the first month of the first
  - L575 ...to the last month of the second

