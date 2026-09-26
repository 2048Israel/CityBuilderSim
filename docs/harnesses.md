# The harnesses

Generated 2026-09-26 by `ham.citybuildersim.tools.HarnessMap` - every labelled assertion in every harness, under the section it prints. Do not edit; regenerate with `Regenerate maps.bat`.

**64 harness files, 5,229 labelled assertions.** AllChecks runs 64 of them.

## Which harnesses read which class

A class nobody reads is a class nothing checks. Mentions by name, so a harness that reaches a class only through another is not counted.

| class | harnesses that mention it |
|---|---|
| [AgeBand](map/AgeBand.md) | CrimeCheck, DeathRecordCheck, HealthCheck, HouseholdMemoryCheck, LabourCheck, LongPlaytest, OutsideCheck, PopulationCheck, SicknessCheck |
| [Agriculture](map/Agriculture.md) | AgricultureCheck, LongPlaytest |
| [Automotive](map/Automotive.md) | **none** |
| [BalanceSheet](map/BalanceSheet.md) | BooksCheck, CreditCheck |
| [Bank](map/Bank.md) | BankCheck, BondCheck, CapitalFlowCheck, CarCheck, CentralBankCheck, CreditCheck, EquityCheck, ExchangeCheck, HistoryCheck, HoldersCheck, LongPlaytest, MonetaryCheck, MortgageCheck, ReadPathCheck, SaveFileCheck |
| [BondMarket](map/BondMarket.md) | BondCheck, LongPlaytest, ReadPathCheck, SaveFileCheck |
| [BuildLog](map/BuildLog.md) | CalendarCheck |
| [BuildingCatalog](map/BuildingCatalog.md) | BuildingDataCheck |
| [BuildingInstance](map/BuildingInstance.md) | **none** |
| [BuildingManager](map/BuildingManager.md) | AgricultureCheck, BondCheck, BooksCheck, BuildMenuCheck, BuildingDataCheck, BusinessServicesCheck, CarCheck, ConservationCheck, CreditCheck, CrimeCheck, DeathRecordCheck, FoodProcessingCheck, HealthCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, InvestCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, PopulationCheck, RailCheck, ReadPathCheck, RestructureCheck, SaveFileCheck, SicknessCheck, TradeCostCheck, VanCheck, WaterCheck |
| [BuildingType](map/BuildingType.md) | BooksCheck, BuildMenuCheck, BuildingDataCheck, CrimeCheck, HouseholdCheck, InfrastructureCheck, InvestCheck, ManufacturingCheck, MiningCheck |
| [BuildingsStacks](map/BuildingsStacks.md) | InvestCheck |
| [BuildingsTemplate](map/BuildingsTemplate.md) | AgricultureCheck, BankCheck, BooksCheck, BuildMenuCheck, BuildingDataCheck, BusinessServicesCheck, CalendarCheck, CapitalFlowCheck, CentralBankCheck, CreditCheck, CrimeCheck, DeathRecordCheck, DenominationCheck, EducationCheck, FoodProcessingCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HoldersCheck, HouseholdCheck, HousingCheck, InfrastructureCheck, InvestCheck, LabourCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MonetaryCheck, MoneyCheck, MortgageCheck, NewGameCheck, OutsideCheck, PolicyCheck, PopulationCheck, RailCheck, ReadPathCheck, RestaurantsCheck, RobustnessCheck, SaveFileCheck, SaveSlotCheck, TradeCostCheck, TreasuryCheck, WaterCheck, YearBookCheck |
| [BusinessDebt](map/BusinessDebt.md) | BankCheck, CreditCheck, LongPlaytest, MortgageCheck |
| [BusinessDebtManager](map/BusinessDebtManager.md) | BankCheck, BondCheck, CreditCheck, LongPlaytest, MoneyCheck, MortgageCheck, ReadPathCheck, SaveFileCheck |
| [BusinessInvestment](map/BusinessInvestment.md) | AgricultureCheck, BankCheck, BusinessServicesCheck, ConservationCheck, FoodProcessingCheck, InvestCheck, ManufacturingCheck, MortgageCheck, PolicyCheck, RestaurantsCheck |
| [BusinessLoan](map/BusinessLoan.md) | BankCheck, BondCheck, CreditCheck, MortgageCheck |
| [BusinessServices](map/BusinessServices.md) | BusinessServicesCheck, LongPlaytest |
| [CapitalFlows](map/CapitalFlows.md) | BondCheck, CapitalFlowCheck, CarryTradeCheck, CurrencyCheck, LongPlaytest, MoneyCheck |
| [CareType](map/CareType.md) | BuildMenuCheck, BuildingDataCheck, HealthCheck, LongPlaytest, ReadPathCheck |
| [CentralBank](map/CentralBank.md) | BankCheck, CentralBankCheck, CurrencyCheck, ForeignCheck, HoldersCheck, LongPlaytest, MortgageCheck |
| [CityBasket](map/CityBasket.md) | **none** |
| [CityCalendar](map/CityCalendar.md) | CalendarCheck, YearBookCheck |
| [Construction](map/Construction.md) | BankCheck, HousingCheck, InvestCheck, NewGameCheck, RobustnessCheck, SaveFileCheck |
| [Consumption](map/Consumption.md) | ConsumptionCheck, ForeignCheck |
| [CorporateBond](map/CorporateBond.md) | BondCheck, LongPlaytest, ReadPathCheck, SaveFileCheck |
| [Crime](map/Crime.md) | CrimeCheck, LongPlaytest |
| [Currency](map/Currency.md) | DenominationCheck, LongPlaytest, NewGameCheck, ReadPathCheck, SaveFileCheck, YearBookCheck |
| [DataSave](map/DataSave.md) | PopulationCheck, SaveFileCheck |
| [Debt](map/Debt.md) | BankCheck, CentralBankCheck, CreditCheck, ForeignDebtCheck, GdpCheck, HoldersCheck, LongPlaytest, NewGameCheck, ReadPathCheck, RestructureCheck, TreasuryCheck |
| [DebtManager](map/DebtManager.md) | BankCheck, CapitalFlowCheck, CarryTradeCheck, CentralBankCheck, CreditCheck, CurrencyCheck, EquityCheck, ExchangeCheck, ForeignDebtCheck, HoldersCheck, LongPlaytest, MonetaryCheck, MoneyCheck, MortgageCheck, ReadPathCheck |
| [DebtQuote](map/DebtQuote.md) | CreditCheck, ForeignDebtCheck, NewGameCheck |
| [DemolitionLog](map/DemolitionLog.md) | CalendarCheck, HouseholdCheck, MortgageCheck |
| [Denomination](map/Denomination.md) | DenominationCheck |
| [EconomyManager](map/EconomyManager.md) | AgricultureCheck, BankCheck, BondCheck, CapitalFlowCheck, ConservationCheck, CreditCheck, CrimeCheck, EducationCheck, HealthCheck, HousingCheck, InvestCheck, LongPlaytest, MortgageCheck, NewGameCheck, OutsideCheck, PopulationCheck, ReadPathCheck, SaveFileCheck |
| [Education](map/Education.md) | EducationCheck, LongPlaytest |
| [EducationType](map/EducationType.md) | BuildingDataCheck, EducationCheck, LongPlaytest, ReadPathCheck |
| [Equity](map/Equity.md) | AgricultureCheck, BankCheck, BusinessServicesCheck, CarCheck, CreditCheck, DenominationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, HistoryCheck, HouseholdCheck, LongPlaytest, ManufacturingCheck, MortgageCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck |
| [Exchange](map/Exchange.md) | BankCheck, ExchangeCheck, LongPlaytest, ReadPathCheck, SaveFileCheck |
| [FamilyModel](map/FamilyModel.md) | CrimeCheck, EducationCheck, HealthCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, LongPlaytest, OutsideCheck, PopulationCheck |
| [FamilyStructure](map/FamilyStructure.md) | BankCheck, BondCheck, CarCheck, ConsumptionCheck, CrimeCheck, EducationCheck, EquityCheck, ExchangeCheck, HealthCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, LongPlaytest, OutsideCheck, PopulationCheck, RestaurantsCheck |
| [FoodIndustry](map/FoodIndustry.md) | BooksCheck, CreditCheck, WaterCheck |
| [FoodProcessing](map/FoodProcessing.md) | FoodProcessingCheck |
| [ForeignAccounts](map/ForeignAccounts.md) | CapitalFlowCheck, CarryTradeCheck, CurrencyCheck, ForeignCheck, ForeignDebtCheck, LabourCheck, LandCheck, LongPlaytest, MonetaryCheck, NewGameCheck, SaveFileCheck |
| [Formats](map/Formats.md) | AgricultureCheck, FoodProcessingCheck, MortgageCheck, TradeCostCheck |
| [Founding](map/Founding.md) | BankCheck, BondCheck, CapitalFlowCheck, CarCheck, CarryTradeCheck, CentralBankCheck, ConservationCheck, CreditCheck, CrimeCheck, CurrencyCheck, DenominationCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HouseholdCheck, InfrastructureCheck, LongPlaytest, ManufacturingCheck, MonetaryCheck, MoneyCheck, MortgageCheck, NewGameCheck, OutsideCheck, RailCheck, ReadPathCheck, SaveFileCheck, SicknessCheck, VanCheck |
| [Game](map/Game.md) | AgricultureCheck, BankCheck, BondCheck, BuildMenuCheck, BusinessServicesCheck, CalendarCheck, CapitalFlowCheck, CarCheck, CarryTradeCheck, CentralBankCheck, ConservationCheck, CreditCheck, CrimeCheck, CurrencyCheck, DeathRecordCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HistoryCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, InboxCheck, InfrastructureCheck, InvestCheck, LabourCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MonetaryCheck, MoneyCheck, MortgageCheck, NewGameCheck, OutsideCheck, PolicyCheck, PopulationCheck, RailCheck, ReadPathCheck, RestaurantsCheck, RestructureCheck, RobustnessCheck, SaveFileCheck, SaveSlotCheck, SectorBooksCheck, SicknessCheck, TradeCostCheck, TreasuryCheck, VanCheck, YearBookCheck |
| [GameFiles](map/GameFiles.md) | AgricultureCheck, BankCheck, BondCheck, BuildMenuCheck, BusinessServicesCheck, CalendarCheck, CapitalFlowCheck, CarCheck, CarryTradeCheck, CentralBankCheck, ConservationCheck, CreditCheck, CrimeCheck, CurrencyCheck, DeathRecordCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, ForeignCheck, ForeignDebtCheck, GdpCheck, HealthCheck, HistoryCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, HousingCheck, InboxCheck, InfrastructureCheck, InvestCheck, LabourCheck, LandCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MonetaryCheck, MoneyCheck, MortgageCheck, NewGameCheck, OutsideCheck, PolicyCheck, PopulationCheck, RailCheck, ReadPathCheck, RestaurantsCheck, RestructureCheck, RobustnessCheck, SaveFileCheck, SaveSlotCheck, SectorBooksCheck, SicknessCheck, TradeCostCheck, TreasuryCheck, VanCheck, YearBookCheck |
| [GameLog](map/GameLog.md) | RobustnessCheck |
| [GamePrefs](map/GamePrefs.md) | **none** |
| [GameVersion](map/GameVersion.md) | RobustnessCheck, SaveSlotCheck |
| [Good](map/Good.md) | AgricultureCheck, BankCheck, BondCheck, BooksCheck, BusinessServicesCheck, CarCheck, ConservationCheck, ConsumptionCheck, CreditCheck, DenominationCheck, FoodProcessingCheck, ForeignCheck, HealthCheck, InfrastructureCheck, InvestCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, RailCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck, TradeCostCheck, VanCheck |
| [GoodsMarket](map/GoodsMarket.md) | AgricultureCheck, BusinessServicesCheck, FoodProcessingCheck, ForeignCheck, InfrastructureCheck, InvestCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, RailCheck, ReadPathCheck, VanCheck |
| [Health](map/Health.md) | HealthCheck, LongPlaytest, OutsideCheck, SicknessCheck |
| [Healthcare](map/Healthcare.md) | HealthCheck, LongPlaytest, OutsideCheck, PopulationCheck, SicknessCheck |
| [HeavyIndustry](map/HeavyIndustry.md) | **none** |
| [HistoryGrapher](map/HistoryGrapher.md) | **none** |
| [HistorySave](map/HistorySave.md) | DeathRecordCheck, HistoryCheck, HouseholdMemoryCheck, ReadPathCheck, SaveFileCheck, YearBookCheck |
| [Household](map/Household.md) | BankCheck, BondCheck, BusinessServicesCheck, CarCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, HealthCheck, HoldersCheck, HouseholdCheck, LongPlaytest, OutsideCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck |
| [HouseholdAccounts](map/HouseholdAccounts.md) | BankCheck, HealthCheck, HouseholdCheck, HousingCheck, OutsideCheck |
| [HouseholdBalance](map/HouseholdBalance.md) | BankCheck, BondCheck, BusinessServicesCheck, CarCheck, CentralBankCheck, CrimeCheck, DenominationCheck, EducationCheck, EquityCheck, ExchangeCheck, HealthCheck, HoldersCheck, HouseholdCheck, LongPlaytest, OutsideCheck, ReadPathCheck, RestaurantsCheck |
| [Inbox](map/Inbox.md) | InboxCheck |
| [InfrastructureManager](map/InfrastructureManager.md) | CarCheck, InfrastructureCheck, LongPlaytest, RailCheck, TradeCostCheck |
| [InterimLoan](map/InterimLoan.md) | CreditCheck, LongPlaytest |
| [Investor](map/Investor.md) | CreditCheck |
| [JobType](map/JobType.md) | AgricultureCheck, BuildMenuCheck, BuildingDataCheck, BusinessServicesCheck, CrimeCheck, EducationCheck, FoodProcessingCheck, HealthCheck, HouseholdCheck, LabourCheck, LongPlaytest, ManufacturingCheck, MiningCheck, OutsideCheck, PolicyCheck, PopulationCheck, RestaurantsCheck |
| [LabourMarket](map/LabourMarket.md) | LabourCheck, LongPlaytest |
| [LandManager](map/LandManager.md) | LandCheck, LongPlaytest, MiningCheck, ReadPathCheck |
| [LandMarket](map/LandMarket.md) | LandCheck, LongPlaytest, MoneyCheck |
| [LandParcel](map/LandParcel.md) | LandCheck, LongPlaytest, MiningCheck, MoneyCheck, TreasuryCheck |
| [LongTermBond](map/LongTermBond.md) | BankCheck, CreditCheck, LongPlaytest, NewGameCheck, ReadPathCheck, RestructureCheck |
| [LuxuryCounter](map/LuxuryCounter.md) | **none** |
| [LuxuryRetail](map/LuxuryRetail.md) | **none** |
| [Manufacturing](map/Manufacturing.md) | ManufacturingCheck |
| [Markets](map/Markets.md) | BooksCheck, FoodProcessingCheck, InfrastructureCheck, MiningCheck, RailCheck |
| [Materials](map/Materials.md) | **none** |
| [MediumTermBond](map/MediumTermBond.md) | BankCheck, RestructureCheck |
| [Migration](map/Migration.md) | CrimeCheck, EducationCheck, HealthCheck, LabourCheck, LongPlaytest, PopulationCheck |
| [Mining](map/Mining.md) | MiningCheck, ReadPathCheck |
| [MoneyAudit](map/MoneyAudit.md) | BankCheck, BondCheck, CapitalFlowCheck, CarryTradeCheck, CentralBankCheck, CreditCheck, CurrencyCheck, ForeignCheck, ForeignDebtCheck, HoldersCheck, LandCheck, LongPlaytest, MoneyCheck, NewGameCheck, OutsideCheck |
| [Mortgage](map/Mortgage.md) | CreditCheck, LongPlaytest, MortgageCheck, ReadPathCheck |
| [Motoring](map/Motoring.md) | **none** |
| [NationalAccounts](map/NationalAccounts.md) | EducationCheck, GdpCheck, HealthCheck, HistoryCheck, HouseholdCheck, LongPlaytest, MortgageCheck, NewGameCheck, TreasuryCheck |
| [Notice](map/Notice.md) | BankCheck, InboxCheck, LongPlaytest |
| [Offending](map/Offending.md) | **none** |
| [OrderBook](map/OrderBook.md) | BankCheck, BondCheck, ExchangeCheck, LongPlaytest, OrderBookCheck, ReadPathCheck, SaveFileCheck |
| [OrphanHousehold](map/OrphanHousehold.md) | OutsideCheck |
| [OutwardInvestment](map/OutwardInvestment.md) | BondCheck, CapitalFlowCheck, ExchangeCheck, HoldersCheck, LongPlaytest |
| [PayTier](map/PayTier.md) | AgricultureCheck, BankCheck, BondCheck, BusinessServicesCheck, CarCheck, EducationCheck, EquityCheck, ExchangeCheck, FoodProcessingCheck, HealthCheck, HoldersCheck, HouseholdCheck, HouseholdMemoryCheck, LabourCheck, LongPlaytest, ManufacturingCheck, OutsideCheck, PopulationCheck, RestaurantsCheck, TradeCostCheck |
| [PopulationCohorts](map/PopulationCohorts.md) | CrimeCheck, DeathRecordCheck, HealthCheck, HouseholdMemoryCheck, LongPlaytest, OutsideCheck, PopulationCheck, SicknessCheck |
| [PopulationManager](map/PopulationManager.md) | BusinessServicesCheck, CrimeCheck, EducationCheck, HealthCheck, LabourCheck, LongPlaytest, ManufacturingCheck, NewGameCheck, OutsideCheck, PopulationCheck, SaveFileCheck, YearBookCheck |
| [PriceIndex](map/PriceIndex.md) | CurrencyCheck, LabourCheck, LongPlaytest, MonetaryCheck, SaveFileCheck |
| [PrisonerHousehold](map/PrisonerHousehold.md) | CrimeCheck, EducationCheck |
| [Rail](map/Rail.md) | RailCheck |
| [RealEstate](map/RealEstate.md) | HouseholdCheck, HousingCheck, LabourCheck, LongPlaytest, MortgageCheck, NewGameCheck, ReadPathCheck |
| [Restaurants](map/Restaurants.md) | LongPlaytest, RestaurantsCheck |
| [Retail](map/Retail.md) | BondCheck, ForeignCheck, HealthCheck, InfrastructureCheck, InvestCheck, LongPlaytest, MonetaryCheck, NewGameCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck, WaterCheck |
| [RetiredHousehold](map/RetiredHousehold.md) | HouseholdCheck |
| [SafetyType](map/SafetyType.md) | BuildingDataCheck, CrimeCheck |
| [SalesTaxLedger](map/SalesTaxLedger.md) | PolicyCheck |
| [SaveHeader](map/SaveHeader.md) | SaveFileCheck, SaveSlotCheck |
| [Sector](map/Sector.md) | AgricultureCheck, BankCheck, BondCheck, BooksCheck, ConservationCheck, CreditCheck, DenominationCheck, ForeignCheck, HistoryCheck, HousingCheck, InfrastructureCheck, InvestCheck, LongPlaytest, ManufacturingCheck, MiningCheck, NewGameCheck, PolicyCheck, RailCheck, ReadPathCheck, SaveFileCheck, SectorBooksCheck, VanCheck, WaterCheck |
| [SectorBooks](map/SectorBooks.md) | BankCheck, BusinessServicesCheck, CreditCheck, CrimeCheck, HistoryCheck, LongPlaytest, ManufacturingCheck, MortgageCheck, SectorBooksCheck |
| [SectorState](map/SectorState.md) | VanCheck |
| [Sectors](map/Sectors.md) | AgricultureCheck, BankCheck, BondCheck, BooksCheck, BusinessServicesCheck, CapitalFlowCheck, ConservationCheck, CreditCheck, DenominationCheck, EquityCheck, ExchangeCheck, ForeignCheck, HouseholdCheck, HousingCheck, LongPlaytest, ManufacturingCheck, MiningCheck, MoneyCheck, MortgageCheck, PolicyCheck, RailCheck, ReadPathCheck, RestaurantsCheck, SaveFileCheck, SectorBooksCheck, TreasuryCheck |
| [ServicesManager](map/ServicesManager.md) | NewGameCheck, ReadPathCheck, WaterCheck |
| [ShadowBasket](map/ShadowBasket.md) | **none** |
| [ShortTermTBill](map/ShortTermTBill.md) | BankCheck, CentralBankCheck, LongPlaytest, RestructureCheck, TreasuryCheck |
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
| [WorldEconomy](map/WorldEconomy.md) | LongPlaytest, MonetaryCheck, NewGameCheck, ReadPathCheck, SaveFileCheck |
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

## BankCheck.java - 444 labelled assertions

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
>   1. Does the price of credit actually depend on the bank - since 0.7.7 on
> ...

- **L127 1. the two limits**
  - L139 a well capitalised bank is limited by what it can gather
  - L145 a bank with no capital can lend nothing, however many branches
  - L149 ...and capital is what lets it lend, at the regulator's ratio
  - L152 ...so the two limits are genuinely different questions
  - L155 a bank that has lost more than it owns is insolvent
- **L158 1b. WHAT TO PAY SAVERS IS A DECISION**
- **L171 what to pay savers is a decision**
  - L183 a bank whose margin is gone pays its savers nothing
  - L185 ...and says its margin held them under the rate it chose
  - L207 fixture: the rate it chose on all its deposits is more than its margin
  - L210 a bank whose deposits earn it nothing pays its savers their share of what it did earn
  - L212 fixture: the second bank's payroll leaves less than that share
  - L214 ...and never so much that its margin no longer pays its staff and buildings
  - L216 ...so paying its savers never costs it money
  - L227 fixture: the idle capital earned a placement
  - L229 a new bank moves a step toward the share of the dial its funding asks for
  - L231 ...and that rate is what it paid, over the deposits
  - L234 ...with its margin to spare, so nothing held it
  - L252 a bank's first month pays its savers less than it charges
  - L254 ...and no more than the window charges it
- **L263 ...and it does not open counters either**
  - L297 fixture: the month really did close on a loss
  - L299 a bank whose counters do not pay for themselves wants no more
  - L301 ...so it does not ask for one
  - L316 ...where a counter carrying a real book is worth opening
- **L319 2. STRAIN IS NOT A PRICE (0.7.7)**
- **L331 (1) a strained bank quotes what an unstrained one with the same costs does**
  - L355 fixture: one is comfortable and the other is lent out past itself
  - L365 the same prime, household rate and lending rate at every dial from 0 to 20%
  - L386 the city's note is the same off the strained bank as off the comfortable one
  - L388 ...and so is its twenty-year paper
  - L390 ...which is the dial, its floor and its own spreads, and nothing else
- **L393 (2) a new loan's rate is its parts, and is fixed for its term**
  - L418 fixture: part of its money is the window's
  - L419 the funds-transfer price: the dial, the window's penalty on its share, the curve's term premium
  - L421 ...the window's share is what it owes the window over all it borrowed
  - L425 the running costs: a year of payroll and upkeep over the book, or over what its capital carries
  - L434 fixture: this month's write-off runs past the base rate a year
  - L436 the expected loss: the through-the-cycle base, whatever the bank has just written off
  - L438 the owners' return is the one the market prices its shares on
  - L447 the capital charge: the weight, the bank's own target ratio, the owners' return over the ftp
  - L450 ...and a bank with no year on record targets the minimum and the conservation buffer
  - L452 prime is the four added up
  - L455 ...a household's line the same four at its own weight, with no term
  - L458 ...and the capital charge never goes negative, whatever the dial
  - L463 a city with no bank yet: its money is the window's
  - L465 ...and it is quoted the same four parts, not a punitive rate
  - L482 a lightly leveraged business pays prime plus its own expected loss and nothing else
  - L484 ...which at a tenth of its assets is nothing: it pays prime
  - L489 fixture: new borrowing reprices with the dial
  - L491 ...and the loan written before it keeps its rate to the last month of its term
  - L493 ...which is still running
- **L495 3. WHAT IT PAYS, WHAT IT TAKES, WHAT IT KEEPS (0.7.7)**
- **L504 (3) a flush bank passes on less of the dial than one borrowing at the window**
  - L531 fixture: one is flush and the other at the window
  - L534 fixture: neither was held by its margin
  - L536 a bank flush with reserves settles at DEPOSIT_SHARE_FLUSH of the dial
  - L538 ...one borrowing at the window at DEPOSIT_SHARE_AT_WINDOW of it
  - L540 ...so the flush bank passes on less
  - L546 a new dial reaches savers a step a month, not at once
  - L558 a bank paying far over the window comes down to the window's rate, no higher
  - L570 ...and at a dial of nothing it pays nothing, and never less
- **L573 (4) the fees: the households' reach the bank to the cent, a loan's is its share**
  - L599 a month's fee is ACCOUNT_FEE at the price index, in today's money
  - L601 the households charged it end the month poorer by exactly the fee on every household
  - L603 ...which is what the balance says they paid
  - L606 ...and what the books were told they would pay, row by row, before they settled
  - L610 ...on its own line of the household books
  - L615 ...and the bank's cash takes it to the cent
  - L616 ...as income on its Fees line
  - L621 a household with no home has no account to be charged for
  - L646 fixture: the households really did borrow
  - L647 a household's loan fee is LOAN_FEE of what it drew, every month
- **L649 (5) the window charges the policy rate plus a quarter of a point**
  - L658 fixture: it really is borrowing at the window
  - L659 the window's rate is the dial plus its penalty
  - L661 ...charged on what the bank owes the window, a month of it
  - L663 ...and the penalty is a quarter of a point: the Bank of Canada's Bank Rate is its target plus 25 basis points
- **L666 (6) the bank's owners are paid on what the tax leaves, and on all of it**
  - L670 the profit the owners are paid a share of is after the tax
  - L672 ...the same tax the city takes
  - L675 ...and a loss is a loss, with no tax to take off it
  - L693 what lands after the close is counted
  - L694 ...while the month's own statement shows it where it happened
  - L699 ...and next month's taxed profit carries it
  - L700 ...once
  - L703 ...and not again
- **L707 4. a real city, and its money**
  - L764 the fixture actually built a bank
  - L765 ...and somebody in it has actually borrowed
  - L779 fixture: somebody abroad has actually borrowed
  - L781 the bank's book is every loan in the city, plus what left it, and the businesses' bonds it holds
  - L785 ...and the carry book IS the stock that owns it
  - L788 the sector book IS the business lender's principal
  - L793 the city book IS the treasury's principal at home
  - L795 the household book IS what the families owe
  - L805 the bank is one of the audited pools
  - L807 ...and the month still balances to the cent
- **L810 5. the save carries the bank's cash**
  - L815 the fixture's bank is actually holding something
  - L829 the bank's cash reloads exactly
  - L830 ...so the city's whole money supply does too
  - L862 every tier's savings reload to the cent
  - L864 ...and so does what the bank is charging for money
  - L867 ...and the standing rate the debt screen shows
- **L871 6. a family that cannot carry it**
  - L926 a household that cannot pay its rent does go broke
  - L928 ...and it takes months, not one bad month
  - L930 the write-off is real money, not a rounding
  - L932 ...and the discharged tier is locked out afterwards
  - L934 ...for the full term
  - L936 some of them leave the city
  - L937 ...but not all of them
  - L949 debt is bounded by the ceiling, not compounding past it
  - L982 the fixture did reach a lockout - or this proves nothing
  - L984 a locked-out household is lent nothing at all
- **L986 7. and the city opens its own**
  - L1129 a city with no bank at all does something about it
  - L1130 ...and says so in words about credit, not about shops
  - L1143 ...and a city with no bank is told its credit is the window's, not punitive
  - L1241 a city that can afford a branch opens one, unprompted
  - L1246 ...and once it has, its money is its own and not all the window's
  - L1256 a bank with room to spare does not ask for another counter
  - L1258 ...and prices one at nothing, so nobody would build it
  - L1262 a bank lent out past itself does ask
  - L1263 ...and a branch is worth real money to it
- **L1266 8. the accounting identities, on a played city**
  - L1456 the fixture's bank is actually running a book
  - L1458 ASSETS = LIABILITIES + EQUITY, every month
  - L1459 ...and equity moves by net income and capital, and nothing else
  - L1465 fixture: the played city's households paid account fees
  - L1466 (4) what the households' books show they paid in account fees is what the bank took, to the cent
  - L1468 fixture: the played city's businesses borrowed
  - L1469 ...a business loan's fee is LOAN_FEE of what was lent, every month it lent
  - L1471 ...and the money audit closes with the fees crossing it
  - L1472 fixture: the bank paid its owners
  - L1473 (6) the bank's dividend is its payout rule on its profit after tax, every month it paid
  - L1475 ...and what lands after a close reaches the next month's taxed profit
  - L1508 the city takes exactly its share of what the bank made
  - L1510 ...and the figure the treasury books is the figure the bank paid
  - L1519 a profitable bank hands over its share
  - L1520 ...out of its own cash
  - L1521 ...and reports it
  - L1522 ...which comes off the month's net income
  - L1528 a bank that lost money is not paid a refund
  - L1532 it pays its savers something
  - L1553 ...and never more than it earned
  - L1555 ...and its staff are on its own books, not the shops'
- **L1558 8b. the trading desk's statement foots**
  - L1607 fixture: the desk traded
  - L1608 fixture: ...at its bid, which is the last trade and so the mark
  - L1609 the re-mark is the inventory at the closing mark, from nothing
  - L1611 ...and the desk's total is what it paid against that re-mark
  - L1613 the opened lines and the re-mark sum to the total, exactly
  - L1619 the re-mark is cleared with the trading result at the top of a month
  - L1631 fixture: the played city's desk actually traded
  - L1632 ...and its opened lines and re-mark summed to its total every month
- **L1635 12. a reload reads the same month (0.7.8)**
- **L1648 (12) a reload between two months reads the same income lines, allowance and target**
  - L1669 fixture: Retail owes the bank alone, no bond, a sound book
  - L1694 fixture: the played bank had set something aside
  - L1695 the allowance reloads to the cent
  - L1696 ...the capital target it chose
  - L1697 ...the month's provision
  - L1698 ...its interest, its fees and its profit, as the Profit page reads them
  - L1701 ...what it kept, and what it paid its owners
  - L1708 ...every statement line and the target's record, exactly
  - L1709 ...and the allowance, book by book
  - L1713 a save from before 0.7.8 loads with an allowance set up from its borrowers
  - L1762 fixture: Materials, held out of building, is levered no further than it was when the premise was written (0.17)
  - L1764 fixture: Luxury Retail, held out of building since round 5, owes nothing
  - L1766 fixture: so every book reads alike on a month and on a quarter, within a hundredth of the allowance
  - L1798 ...by the same rule on the same borrowers, as the load reads them
  - L1800 fixture: some of its books are sound ones, held at BASE_LOSS_RATE
  - L1801 ...which, wherever the curve does not read the borrower's assets, is the saved bank's to the cent
  - L1803 ...booked as no month's provision
  - L1804 ...so its equity is lower by what it set aside, and by nothing else
  - L1826 fixture: Materials, held out through the next month too, borrowed nothing in it
  - L1834 ...and its next month articulates like any other
  - L1835 ...with the provision the saved city made, within a hundredth of the allowance - not the allowance again
  - L1837 ...and a money audit that closes
- **L1839 9. capital is the constraint, and it can run out**
  - L1845 a bank can lend its capital over the regulator's ratio
  - L1851 ...and does
  - L1858 a bank that loses more than it owns is insolvent
  - L1859 ...and can lend nothing at all
  - L1860 ...and it says what it would take to fix
  - L1875 a failed bank prices off the window: its money is all the window's
  - L1877 ...and its prime is the four parts on it, not a premium on every rate
  - L1883 recapitalised, it is solvent again
  - L1884 ...and lending again
  - L1885 ...and above the ratio it is required to hold, by at least the exit buffer
  - L1887 ...and holding at least what one branch is capitalised with
  - L1889 ...which is exactly what it was asked for, and not a dollar more
  - L1905 fixture: a bank that lost its whole book has failed
  - L1906 fixture: ...and has no book left to strike a ratio on
  - L1907 a failed bank with no book is still asked for one branch's capital
  - L1910 ...and can lend again once it has it
- **L1983 (14) the normal curve, and what it gives**
  - L1986 N(0) is a half
  - L1987 N(1.96) is 0.975
  - L1988 N(-1) is 0.1587
  - L1989 N(-3) is 0.00135, to the tail's own figures
  - L1996 ...and N(x) + N(-x) is one
  - L1997 a loan's loss given default is what it does not recover: 1 - LOAN_RECOVERY (0.7.12, round 2)
  - L1999 ...and a bond's 1 - BOND_RECOVERY
  - L2001 at the default point half its firms default in a year
- **L2013 (14) a sector held at a leverage loses its defaulted firms' slice, pro rata**
  - L2018 fixture: the lender's hazard is the brief's, 1 - (1 - PD)^(1/12)
  - L2023 a sector at leverage L writes off principal x h(L) x (1 - LOAN_RECOVERY) in the month
  - L2025 ...which is what the month's sweep returns
  - L2026 ...and the debt that defaulted is h of it
  - L2028 ...every loan falls pro rata: the first
  - L2030 ...and the second
  - L2031 ...its assets are untouched - the firms keep their plant
  - L2032 ...it is not restructured, not on its record and not shut out
  - L2040 ...and owing less against the same assets, its next loan is quoted less
  - L2049 held a year at a leverage, the share of its debt that defaulted is PD(L)
- **L2052 (14) more defaults the deeper in debt, and nothing past dust under the watch line**
  - L2067 the default rate and the monthly slice rise with leverage, and never fall
  - L2068 ...from nothing with no debt to all of it against no assets
  - L2071 under the watch line no month's slice costs more than a sound book's year, BASE_LOSS_RATE
  - L2077 a sector at 1.2 times its assets loses a slice and is not news; one at the default point is
  - L2082 ...and at 0.3 a month's slice of $30M of debt is under a cent
- **L2085 (14) past the default point with its plant standing, it is not written down whole**
  - L2089 fixture:
  - L2092 at
  - L2094 ...not down to RESTRUCTURE_TARGET of its assets in one month
  - L2096 ...and not banned, and not on its record
- **L2101 (14) the backstop: a sector with nothing left is written down whole, and banned**
  - L2105 fixture: owing against assets below nothing is insolvent
  - L2106 the whole of its debt is written off - RESTRUCTURE_TARGET of nothing
  - L2108 ...its overdraft is forgiven
  - L2109 ...it is restructured, not sliced
  - L2111 ...one default on its record
  - L2112 ...and shut out for exclusionFor() of it
  - L2114 ...which is news
- **L2116 (14) the allowance reads the same curve, staged firm by firm: a year on its sound firms, a loan's term on those past the watch line**
  - L2126 far under the watch line its firms are sound: a year's BASE_LOSS_RATE, never less
  - L2128 fixture: at 0.5 even a loan's term of the curve is under BASE_LOSS_RATE
  - L2142 fixture: the curve's year passes BASE_LOSS_RATE under the watch line
  - L2144 fixture: at
  - L2146 ...nearer it, a year on its sound firms and a loan's term on the share past the line
  - L2148 ...the share past it being N(ln(L / SECTOR_WATCH_LEVERAGE) / ASSET_VOLATILITY)
  - L2151 ...half of them at the watch line itself
  - L2152 ...so there, half a year's loss and half a lifetime's
  - L2157 past it, most of the lifetime loss over a loan's term, staged
  - L2159 ...between a year's loss and the whole lifetime's
  - L2162 ...and a sector with nothing left holds all it owes, which the backstop writes off
  - L2179 the allowance rises with leverage and never falls
  - L2180 ...and has no cliff at the watch line: the step across it shrinks with the step, as a continuous one does
  - L2219 moving past the watch line provides the rise in its staged loss, less the year it held
  - L2221 ...which is the staged loss at its new leverage
  - L2222 ...which is more than the year of slices that follow
  - L2223 every slice after is drawn from the allowance
  - L2224 ...and none of it reaches the month past it
  - L2225 ...so the year's provisions are less than its write-offs: the loss was taken when it crossed
  - L2227 ...and every month its equity moved by its net income and nothing else
- **L2229 (14) a played city through months of slices: the money audit closes**
  - L2266 fixture: the city has a standing bank, and retail has plant to lend against
  - L2301 fixture: retail's firms really did default, a slice a month, for months
  - L2302 every month of it closes to the cent
  - L2303 ...and within 0.01% of what moved
  - L2304 ...and the bank wrote off what the lender did, every month
  - L2306 fixture: months went by with retail's firms defaulting and nothing past the line
  - L2308 the defaults notice is live exactly in the months that were news, and in no other
  - L2321 fixture: retail's month was past the default point
  - L2322 ...and the inbox says so, naming it
  - L2324 ...and the month still closes
- **L2343 (15) a failing sector's plant is sold to the builders, as its materials**
  - L2408 fixture: retail in distress retired plant, and the rule that did it was the distress rule
  - L2414 the material sold is the template's own, times the buildings retired
  - L2416 ...a shop's material, the shops the city lost
  - L2417 fixture: the builders could pay for all of it
  - L2418 the builders paid the units at the day's price for material
  - L2419 ...the materials market's own price, as it stood that day
  - L2420 their stock rose by the units, less what the month's sites drew from it
  - L2422 ...and it is on their balance sheet at the day's price
  - L2424 the builders' cash moved out by what they paid
  - L2425 ...and the seller's in by the same
  - L2428 ...both on their cash-flow statements
  - L2429 ...which close for the seller, but for the overdraft the fixture handed it
  - L2431 ...and for the builders
  - L2432 and the month's money audit closes, to the cent
  - L2465 fixture: the city ordered work its yard does not cover
  - L2466 fixture: the builders built with it in a later month
  - L2469 what they built with from it is on their income statement, at what they paid for it
  - L2471 ...as a named input
  - L2474 ...and no cash: the cash flow adds it back
  - L2475 ...and closes
  - L2476 ...and that month's money audit closes, to the cent
  - L2503 fixture: retail retired plant again, with the builders overdrawn
  - L2505 builders who cannot pay buy nothing, and pay nothing
  - L2507 ...so the seller is paid nothing for it
- **L2527 (16) the bank reads a borrower from its last quarter**
  - L2533 with no statements yet, the bank reads a borrower's month
  - L2542 the price reads the last quarter's statements: their average debt over their average assets
  - L2544 ...not the month's own
  - L2545 ...and so does the allowance: its staged loss at the quarter's leverage
  - L2551 the quarter is saved and restored by the sector's name
  - L2554 a fourth reading drops the oldest: STATEMENT_MONTHS of them
  - L2556 ...and the count stops there
  - L2559 the hazard stays on the month's own leverage: firms fail on what they owe against what they have
  - L2561 ...and a slice, continuous and small, does not restart the quarter
  - L2582 fixture: the sector with nothing left was written down whole
  - L2583 after the backstop its quarter restarts: no reading from before it counts
  - L2588 ...so its next loan's price reads the restructured books, what it owes now over what it owns now
  - L2591 ...which is far under what the quarter before the backstop would have charged
- **L2616 (17) the desk bids for the bank's own shares only with what it holds over its target**
  - L2627 fixture: a bank a little over its target
  - L2628 fixture: ...which by its own rule buys its shares back
  - L2632 fixture: its shares have a record neither new nor bad
  - L2641 its desk bids half a spread under fair value
  - L2642 ...for what its spare capital pays for at the bid, under the pace
  - L2648 fixture: the household offers more than the pace, and the pace more than the spare
  - L2657 a bank a little over its target, offered more than its spare capital, buys back exactly the spare
  - L2659 ...and ends at its target
  - L2660 ...not under it
  - L2661 ...so the household raised what the desk took, and no more - the rest of its need goes on to credit
  - L2663 ...and the rest of what it offered waits on the book: the desk is not obliged
  - L2666 a second offer the same month, at its target, finds no bid
- **L2668 (17) ...and other companies' shares only on the capital it has to spare, at the desk's weight**
  - L2682 its desk bids for what its spare capital carries at RISK_EQUITY, marked at the price it pays
  - L2686 fixture: the household offers more than that
  - L2692 a bank a little over its target buys only what its spare capital carries at the weight
  - L2695 ...the weight the weighted book already gives the desk, RISK_EQUITY, at the price it paid
  - L2697 ...and marked, it holds its target
  - L2698 ...its ratio at or over the target
  - L2699 ...the household raised what the desk paid
  - L2700 ...and the rest of its offer waits on the book
  - L2705 its selling is unchanged: at its target it still asks for what it holds
  - L2707 ...and bids for no more than rounding
  - L2716 fixture: a bank under its target, its desk still posting
  - L2717 ...bids for nothing
  - L2720 a bank under its target buys none: the household raises nothing from its shares
  - L2721 ...and the desk holds none
  - L2734 fixture: twenty households leave with their shares
  - L2741 fixture: the desk was posting
  - L2742 the leavers' shares a bank under its target will not bid for stay abroad with them, as with no market
  - L2744 ...resting on the book, unpaid
- **L2764 (13) the ladder's four parts are its prime, at every dial**
  - L2782 the funds-transfer price, running costs, expected loss and capital charge add up to prime, at six dials
  - L2784 ...and every other rung is the bank's own rate for it
  - L2785 fixture: at 3% every part is priced
  - L2787 ...and past the owners' return the capital part is nothing
  - L2788 the step from the policy rate is the window's penalty on its share and the term premium
  - L2791 ...and the step to prime is the three costs over it
- **L2794 (13) the weight table foots to the weighted book, term and desk included**
  - L2815 the weight table's weighed column foots to the weighted book capacity reads
  - L2817 fixture: the desk's shares are on it, at RISK_EQUITY
  - L2818 ...a book halfway through its loans' terms shows the share its term counts for
  - L2820 ...and every row is its face, term and risk weight multiplied
  - L2821 ...where face times risk, with no term and no desk, did not foot
- **L2824 (13) a branch's reach is in today's money after a reform**
  - L2828 three branches reach three times DEPOSITS_PER_BRANCH
  - L2831 ...and after a hundred-for-one reform, a hundredth of it
  - L2833 ...so what they reach of the city's savings is too - not the founding figure the screen used
  - L2835 ...and their owners' capital for a branch
  - L2837 ...and reached and beyond reach add up to the city's own savings
- **L2840 (13) the bank's state in a sentence, with the figure that decides it**
  - L2843 with no branch: there is no bank
  - L2850 fixture: the four banks are healthy, rebuilding, under the minimum and failed
  - L2855 a bank at or over its target is healthy and lending freely, at its ratio and its target
  - L2859 ...one under its target is rebuilding, at the growth its rule allows
  - L2862 ...one under the minimum lends only to keep its borrowers going, at its ratio
  - L2865 ...and a failed one says what it must hold again
- **L2870 (13) on a played city: the interest by who paid it, last month, the year, and the equity's movement**
  - L2878 the city's coupons, the businesses', the families' and the discount each on its own line
  - L2882 ...and together they are its interest
  - L2980 fixture: the played city's first branch opened in it
  - L2981 fixture: the bank paid its owners in it
  - L2982 fixture: the treasury bought paper back from it, at a gain or a loss
  - L2983 fixture: the city put capital into it
  - L2984 its equity's movement left nothing unexplained, every month
  - L2985 ...nor between two presses, after a buyback and after a rescue
  - L2986 fixture: the city and the businesses paid it interest
  - L2987 its interest by who paid it is the whole of its interest, every month
  - L2988 fixture: last month and the year were read in most months
  - L2989 last month's column is what that month read, whole
  - L2990 ...and the last twelve months add up to the last twelve read
  - L2991 the played bank's weight table foots every month
- **L2993 (13) the rescue's guard is the model's**
  - L2994 fixture: the bank stands and needs nothing
  - L2995 a bank that needs nothing is not offered a rescue
  - L2999 fixture: the bank is under water
  - L3000 ...a treasury holding half of what it needs cannot rescue it
  - L3002 ...one holding all of it can
  - L3004 ...and doing it stands the bank back up
- **L3038 (7) a loan is met by its year's allowance, a weakening borrower by its lifetime loss, a write-off by the allowance first**
  - L3051 a new loan to a sound borrower is met by its year's expected loss, BASE_LOSS_RATE of it
  - L3053 ...a stage-1 book
  - L3054 ...and that is the month's provision
  - L3072 a borrower past SECTOR_WATCH_LEVERAGE moves to stage 2
  - L3073 ...and its allowance rises toward its lifetime loss: a loan's term of defaults on the share of its firms past the line
  - L3075 ...which is more than a year's loss
  - L3076 ...and the rise is the month's provision, before anything is written off
  - L3092 fixture: the month's slice is written off, and the sector is not restructured
  - L3099 a write-off is drawn against the allowance its book held first
  - L3101 ...and only what that did not cover reaches the month
  - L3103 ...so the provision is the allowance's move plus the write-off
  - L3105 ...and the month's hit to equity is less than the write-off, because most was taken already
  - L3108 ...and a borrower still past the watch line stays in stage 2, its staged loss struck on what it owes now
  - L3120 a repaid book holds nothing, and its allowance comes back as a release
  - L3125 over the episode the provisions add up to the write-offs, to the cent
  - L3126 ...and every month its equity moved by its net income and nothing else
  - L3129 a family owing under the watch line holds a year's loss
  - L3131 ...halfway from there to the discharge line, half its debt
  - L3134 ...and at the line, all of it - a discharge writes the whole debt off
  - L3154 fixture: a family borrowing to eat does pass the watch line
  - L3155 ...and its book then holds more than a year's loss
  - L3157 ...which is the rule cell by cell
- **L3159 (8) a bank that has lived through a bad year chooses more capital than a young one**
  - L3189 a bank whose worst year was under the conservation buffer targets the minimum and the buffer
  - L3191 fixture: the scarred bank's worst year was past the conservation buffer
  - L3193 ...one that has lived through a worse year holds a buffer that would take it and leave the minimum
  - L3195 ...which is more than the young one holds
  - L3196 ...its worst year is its year's provisions over its weighted book
  - L3198 ...and never more than the whole Basel stack, MAX_BUFFER, however bad the year
  - L3201 ...and a loss on a book smaller than its founding capital carries is read over that capacity
  - L3207 no bank ever targets less than the minimum and the conservation buffer
  - L3208 ...and it prices a loan's capital at the target it chose
- **L3213 (9) under its target it keeps its profit and lends slower, and under its minimum only to keep borrowers going**
  - L3218 fixture: halfway between its minimum and its target
  - L3220 it keeps every dollar of a profit
  - L3221 ...and says so
  - L3222 ...and a borrower's debt may grow RATIONED_GROWTH a month halfway there
  - L3234 the limit is RATIONED_GROWTH x x/(1-x) from the minimum to the target
  - L3235 ...rising smoothly, never falling, as the ratio rises
  - L3236 ...from nothing at the minimum
  - L3237 ...to no limit at all at the target
  - L3248 a sector owing 100,000 may borrow that share of it more this month
  - L3249 ...a project inside it is funded
  - L3250 ...and one past it is not
  - L3251 ...and the investor can say it was the bank's capital
  - L3256 ...and its short month is lent whole, its fee on top: a working-capital line the rule does not ration
  - L3260 under its minimum it lends only to keep its borrowers going
  - L3268 ...no project, however small
  - L3270 ...but a borrower short of cash is still lent its short month: the line is honoured under the minimum too
  - L3281 fixture: its loan fell due
  - L3282 ...and what fell due may be lent again under the minimum
  - L3287 ...and a borrower past the line still has its defaulted firms written off
  - L3290 ...and one with nothing left is still restructured whole
  - L3307 fixture: free, it borrows more than its interest
  - L3308 a family's debt may grow the month's share and its interest, and no more
  - L3311 ...and under the minimum it may draw its interest and nothing else
- **L3314 (10) inside its band it pays a share of its profit, over the top it returns the excess**
  - L3318 fixture: inside its band, with no reserves at all
  - L3320 inside its band it pays PAYOUT_IN_BAND of its profit after tax
  - L3324 ...and pays it though it holds no reserves - out of what it borrows, like any bank
  - L3326 ...nothing on a loss
  - L3328 ...and never so much that it would fall under its target
  - L3332 fixture: far over the top of its band
  - L3333 over the top it pays its share and a twelfth of the excess
  - L3342 ...so a year returns all but (11/12)^12 of it, on no profit at all
  - L3344 ...and it counts what it paid over the year
  - L3348 under its target, under its minimum, failed or with no bank it pays nothing
- **L3352 (11) while the bank stands, there is always something it will lend**
  - L3368 a standing bank at any capital has capacity
  - L3369 ...lends more than nothing a month whenever it is over its minimum
  - L3370 ...and at any capital lends a borrower short of cash its interest
  - L3374 ...where a failed one, in resolution, has none - resolution is as it was
- **L3432 and the bank pays for the city's paper**
  - L3462 fixture: the city has a working bank and owes nothing yet
  - L3491 fixture: the city issued a note and a serial bond, below par
  - L3493 between the presses the bank has not paid yet
  - L3494 ...and owes the treasury exactly what it received
  - L3507 a city saved between the issue and the settle still owes its bank's payment
  - L3520 fixture: the households held their paper through the settle - the desk bought none
  - L3547 the bank's cash fell by exactly what the treasury received less what the households
  - L3549 ...which is the figure the settle reports
  - L3550 ...and between them the holders paid every dollar the treasury received
  - L3553 ...its book rose by its own face, less what the city repaid it this month
  - L3555 ...and its equity by net income and capital, and nothing else - the discount, not the face
  - L3558 ...and once it has paid it owes nothing for the paper
  - L3562 MoneyAudit still closes, on the month the bank paid
  - L3564 the reloaded city's bank paid the same, at its settle
  - L3566 ...and its households the same
  - L3567 ...and its month closes too
  - L3575 fixture: the note and the serial are on the books
  - L3577 each piece carries its own discount, which together is face less what it raised
  - L3583 the bank's interest in the settle month carries one month of its share of the discount,
  - L3585 ...which is well short of the whole (the old rule booked all of it)
  - L3587 the rest sits against its book, unearned, so its equity did not move for the discount
  - L3590 ...and it is exactly the bank's share less what accreted
  - L3604 over the note's life its accretion adds up to its face less what it raised
  - L3606 ...and nothing of it is left unearned once it is repaid
  - L3607 ...and it is off the books

## BondCheck.java - 215 labelled assertions

> Corporate bonds (0.7.12): the bond, how it is sold, when a sector takes it
> over the bank, who loses what in a default, what the bank charges for
> concentration, who buys and sells, and the save.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Cheapest, within the bank's
> limit", "No limit, just price it", "Bank notes rank first", "Order book for
> both"):
> 
>   1. THE BOND'S ARITHMETIC: priced at its own coupon it is worth its face;
>      its price falls as the yield rises; the coupons a month are a twelfth
>      of the coupon on the face each holder had, exactly; at maturity every
>      holder is paid its face.
> ...

  - L196 every month this harness played closed the audit (
- **L204 1. the bond's arithmetic: par at its own coupon, and a price that falls as the yield rises**
  - L211 priced at its own coupon a bond is worth exactly its face, whatever is left of it
  - L214 a point more on the yield and it is worth less
  - L215 ...a point less and it is worth more
  - L223 ...and it falls all the way, the yield from 0% to 30%
  - L225 the coupons and the face, each discounted a month at a time
  - L227 the yield a price implies is the one that gives it
  - L229 with nothing left it is worth its face
  - L231 a month's coupon is a twelfth of the coupon on the face
  - L235 written down to a quarter, three quarters of it is gone
  - L236 ...off every holder by the same share, the bank's cost with its face
  - L238 ...so the holders still add up to the face
- **L244 2. bookbuilding: the coupon is the lowest yield that fills the issue, and the rest is a bank loan**
  - L253 fixture: at the bank's own price the book takes a bond of it
  - L255 the bids at its coupon fill it
  - L256 ...and a hundredth of a point under it they do not: its coupon is the lowest yield that does
  - L258 ...no dearer than the loan, its costs spread over its ten years
  - L260 fixture: the bank would take it only at its loan-equivalent yield, over the loan's price
  - L262 fixture: so the book does not take all of it at a price no dearer than the loan
  - L263 ...and the rest is a bank loan
  - L264 ...the two raising it all
  - L265 ...at the leverage the whole deal leaves, which every bid read
  - L268 at a loan rate under every bid the book fills nothing: all of it is a bank loan
  - L270 ...and the plan says what the book would have cleared the whole at
  - L274 where the bank would lend nothing, no bond either: \"within the bank's limit\"
  - L276 ...and where it would lend a quarter, the bond and the loan raise no more than that quarter
- **L283 3. the choice: a small amount goes to the bank, a large one to a bond, and where they cross**
  - L301 borrowing $10k it takes the bank: the fixed cost of an issue would be more than a tenth of it
  - L303 borrowing $20M it takes a bond
  - L312 ...and nothing smaller than that took a bond
  - L316 the crossover is the face at which the coupon and its costs cost what the loan does
  - L319 ...fixed / (TERM_YEARS x (loan all-in - coupon - spread / TERM_YEARS)), from nothing else
  - L322 ...half that size and the bond would be dearer than the bank
  - L323 ...twice it, cheaper
  - L324 a bank loan's all-in is its rate and its fee over its term
- **L331 7a. a sector short of cash borrows in the month: the shortfall desk asks the book, and the world's purchase crosses the border**
  - L341 fixture: short of $5M,
  - L348 its holders add up to its face
  - L349 the issuer was handed its face less its costs
  - L350 ...which the bank, its underwriter, was paid
  - L352 fixture: the world bought part of it
  - L353 the world's purchase is on the month's audit as money arriving from abroad
  - L354 ...in the financial account
  - L355 ...which is what the currency read for the month
  - L356 ...and the month closes
- **L363 1b. the coupons: a twelfth of each coupon on what each holder had at the month's top, every class**
  - L375 the issuer's month's interest is its loans' and its bonds' coupons
  - L378 the households were paid theirs
  - L379 ...the bank its
  - L380 ...which it took as interest
  - L381 ...the companies theirs
  - L382 ...and the world its, abroad
  - L383 fixture: every class held some of the city's bonds at the month's top
  - L384 the world's coupons leave on the audit, as income paid abroad
  - L385 ...in the income account the currency reads
  - L386 the households' coupons are on it too
- **L392 4. the order book in play: no book crossed after the step, the sellers who waited counted, the audit closed**
  - L415 fixture: participants posted orders on the books
  - L416 after every step every book is uncrossed: what could meet already traded
  - L417 ...and no sector bids for its own bonds
  - L418 the sellers who waited are counted, and are no more than the sellers
  - L419 the market's life volume is the sum of its books'
- **L425 7. each participant by its own rule**
  - L433 households bid nothing where a bond's expected return is no more than the deposit rate
  - L435 ...a point over it, the paper's rule: HOUSEHOLD_PAPER_APPETITE a unit of spread of the issue
  - L438 ...and never more than MAX_HOUSEHOLD_PAPER_SHARE of it
  - L442 fixture: the bank is over its capital target, so it buys
  - L447 its yield is an equal loan's four parts at the bond's term, the concentration charge, the loss at (1 - BOND_RECOVERY) and the record
  - L452 under it the bank bids nothing
  - L453 ...at it, the issue up to what its spare capital carries
  - L459 its bonds are in its weighted book at a loan's weight, for the months they have left
  - L465 the world bids a month of closing the gap to hot money's target: ARRIVAL_SPEED of it
- **L477 7c. a company with idle cash bids for other sectors' bonds, never its own, and only while it owes nothing**
  - L481 fixture:
  - L493 ...a point over the world's rate, a positive bid
  - L494 ...and for its own bond nothing, at any yield
  - L499 ...and no company that owes anything bids at all
  - L516 fixture: a bank between its minimum and its target
  - L519 under its target the bank bids for no bond at any yield
  - L526 ...and asks to sell what takes it back there, at a loan's weight on its target - or all it holds
  - L535 while hot money is running the world bids for nothing, at any yield
  - L559 fixture: a household cell holds a claim on the bonds
  - L578 fixture: bids rest on the books of the bonds it has a claim on, after the market's step
  - L584 a household whose credit is cheaper than every bid's yield sells nothing
  - L590 ...its asks wait on the books, over the bids
  - L595 a household whose credit costs more than the bids' yield sells into them
  - L596 ...raising what it was short, and banking what the higher bids paid past it
  - L597 ...the rest in its savings
  - L609 ...each at a price a bid was resting at, not its own ask
  - L666 fixture: the month the bank books the claims closes
  - L692 fixture:
  - L694 fixture:
  - L697 fixture:
  - L700 fixture: the bank is between its minimum and its target, and rations growth
  - L709 fixture: the month ran on the bank's capital rule, rationing
  - L711 a rationing bank covers a healthy sector's short month: a working-capital line
  - L713 ...past what the capital rule would have let its debt grow
  - L714 ...and refuses it a building its room does not cover: growth
  - L716 a short sector sells what it holds first: its bonds, into the bids resting
  - L718 ...then asks the lender, who will not lend past the line
  - L720 ...and what it still could not pay defaults that month
  - L721 ...its loans losing (1 - LOAN_RECOVERY) of what of them defaulted
  - L723 ...its bonds (1 - BOND_RECOVERY) of theirs
  - L725 fixture: after the write-down the lender reads it under the line, and lends
  - L726 ...the unpaid rest lent as an interim loan, ranked first, its fee on top
  - L728 ...its till ends the month at or above nothing
  - L729 a sector over the ceiling is refused all but its interest reserve, and defaults on its own credit
  - L731 ...lent the unpaid rest in the interim: the ceiling is not the interim lender's
  - L733 nothing is forgiven on either: the audit's OverdraftForgiven is the backstop's alone
  - L735 ...and it is the month's forgiven overdraft, as the backstop's always was
  - L737 ...and the month closes
  - L772 fixture: the shops owe under the shortfall desk's ceiling
  - L774 the month closes
  - L778 fixture: it ordered stock, and could pay for all of it
  - L780 a sector under its ceiling buys as before: every order placed whole
  - L782 ...and filled whole
  - L788 fixture: the month the bank books the claim closes
  - L795 fixture: the shops owe past the shortfall desk's ceiling and under the line on their quarter
  - L798 the month closes
  - L803 fixture: it could pay for some of its order and not all of it
  - L804 a sector over its ceiling and short buys only what its cash and the credit it can get cover
  - L806 ...and does not order the rest
  - L810 ...so it does not default on the stock it did not buy
  - L811 the month closes
  - L823 fixture: under the line on its quarter, still
  - L824 the month closes
  - L828 a sector whose unavoidable bills outrun its cash and credit can pay for no stock
  - L830 ...and buys none
  - L834 the same sector still defaults on a bill it cannot avoid: principal that fell due
  - L836 ...on no more than the principal: nothing it bought is in what it could not pay
  - L838 the month closes
- **L865 5. recoveries by instrument: a slice takes a loan's loss off the loans and a bond's off the bonds**
  - L882 fixture: $900k of loans and $600k of bonds, 1.5 times its assets
  - L889 the loans lose h x (1 - LOAN_RECOVERY) of theirs
  - L891 ...the bonds h x (1 - BOND_RECOVERY) of theirs
  - L893 ...so the loans recover LOAN_RECOVERY of what defaulted
  - L895 ...and the bonds BOND_RECOVERY
  - L896 what defaulted is on the lender's record by class
  - L915 at
  - L917 ...and the bonds h x (1 - BOND_RECOVERY)
  - L923 both inside the sources' ranges
  - L943 the backstop on a sector with nothing left writes its loans off whole
  - L944 ...and its bonds
  - L945 ...leaving it owing nothing on either
- **L949 5b. a slice in play: the bondholders' loss reaches every class, and the world's crosses the border**
  - L959 fixture:
  - L962 the loans lost (1 - LOAN_RECOVERY) of what of them defaulted
  - L964 ...and the bonds (1 - BOND_RECOVERY) of theirs
  - L968 ...the loans recovering more than the bonds
  - L974 the bondholders lost it between them: households, the bank, the companies and the world
  - L976 fixture: every class held some of what defaulted
  - L978 the world's loss is declared across the border, a valuation with no cash in it
  - L979 ...on the valuation line the foreign position reads
  - L980 ...and the month closes
  - L983 a bondholder's expected loss is the default rate times (1 - BOND_RECOVERY)
  - L995 fixture: the bank read the sector at its provision
  - L996 ...what it owes the bank as it stands at the month's end
  - L997 ...the bank's allowance on the sector its loans at (1 - LOAN_RECOVERY) and its bonds at (1 - BOND_RECOVERY)
  - L1001 ...and a loan's own risk in its price the curve at (1 - LOAN_RECOVERY)
  - L1013 written down by a tenth, every holder of the bond loses a tenth: households, the bank at its cost, the world, the companies
  - L1018 ...the households' claims falling by exactly their loss
  - L1019 ...the world's on the border's line
  - L1020 ...and it was a tenth of the sector's bonds
- **L1027 6. concentration: Basel's capital, a charge that rises with a sector's share and falls as the book spreads**
  - L1036 the IRB capital gives Basel's own table of corporate risk weights, to its two decimals
  - L1037 the correlation at the soundest firms is 24%
  - L1038 ...and a book in one industry R x SECTOR_CORRELATION_MULTIPLIER
  - L1057 a dollar lent to an industry carries more capital the more of the book it already is
  - L1058 ...and a small one's carries less than none: it diversifies the book
  - L1072 ...and falls as the book spreads over more industries, its Herfindahl index with it
  - L1080 by the Euler rule the sectors' shares add back up to the book's add-on
  - L1081 ...which is weight on the book at the minimum ratio
  - L1094 fixture: the played bank's book has a largest industry
  - L1096 a loan's capital per dollar is the target on its weight plus the sector's add-on, carried at the target
  - L1099 ...and its charge is that capital at the owners' return over the money - the one formula
  - L1107 pushed to the lender, it is the part of the sector's rate over prime, its own risk and its record
  - L1115 the add-on is in the bank's requirement: its own row of the weight table
  - L1116 ...and the table still foots to the weighted book the requirement reads (BankCheck 13)
  - L1117 ...its minimum the ratio on all of it
- **L1134 8. save and load: the bonds, who holds them and the resting orders; and a save from before them**
  - L1146 it loads
  - L1148 fixture: the city has bonds outstanding and orders resting
  - L1149 the same bonds come back
  - L1153 fixture: an interim loan is outstanding (5c)
  - L1154 the interim loans come back, by their own type: what a sector owes on them
  - L1156 ...as many of them, ranked first, over every sector
  - L1158 ...with the same orders resting on their books, in the same order
  - L1159 ...the households' bonds on them
  - L1168 fixture:
  - L1169 ...and each comes back by the cell's name, bond by bond, to the bit
  - L1174 ...kept in the save under the cells' names
  - L1175 ...their value a unit of face
  - L1176 ...the bank's bonds on its book
  - L1177 ...and its concentration add-on
  - L1178 ...the lender's bond write-offs on the record
  - L1181 ...and last month's book for the screens
  - L1191 a month on, both the city and its reload play and close
  - L1205 a round-1 save, its households' bonds one pool, loads
  - L1224 ...each cell holding the share of every bond its claim was of the pool, its total its claim
  - L1225 ...every bond's households' face the pool it was, and the cells' sum
  - L1228 ...and it plays, the audit closing
  - L1239 fixture: the save's cells are today's width
  - L1250 a save from before the bonds loads
  - L1251 ...with no bonds, no books and nobody holding any
  - L1253 ...the households' paper where it was
  - L1256 ...and it plays, the audit closing every month
- **L1354 9. each household type trades: a rich cell bids, a cell over its money asks, and they meet**
  - L1356 fixture: the bond's households' face is the cells' own, summed
  - L1365 the rich cell bid and the cell over its money asked, and they traded with each other
  - L1367 ...the face the one gave up is the face the other took
  - L1368 ...the cash the one paid is the cash the other was paid, the transfer counted
  - L1370 ...and the same the other way up
  - L1371 ...so the bond's households' face did not move
  - L1372 ...and is still the cells' own, summed
  - L1373 no pool line saw it: nothing bought from or sold to the pools
  - L1383 fixture: the rich cell's bid rests after the step, and nothing traded
  - L1388 a cell short of money sells into it, raising what it was short
  - L1389 ...cell to cell, the rich cell taking the face
  - L1391 ...the face the one gave up the other took
  - L1392 ...and no pool line
- **L1398 1c. at maturity the issuer pays every holder its face, and the bond and its book go**
  - L1402 fixture: a bond outstanding
  - L1409 it falls due and is gone
  - L1410 the issuer repaid its face
  - L1411 ...to its holders, every class together
  - L1413 the world's principal leaves on the audit, in the financial account
  - L1414 ...the households' arrives in their savings
  - L1415 ...and the month closes

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
  - L331 a cell carries eight slots, a holding per company, the dollars abroad,
  - L347 an older save restores against the company list it was written with
  - L350 ...and owns none of the company that save had never heard of
  - L352 ...while its first holding is still its first holding
  - L362 ...and a city from before cars existed owns none

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
  - L325 fixture: the city actually offered a carry over the world
  - L334 hot money actually arrives in a real city that offers a carry
  - L336 ...in real money, not a rounding error
  - L338 ...and the books balance every month it does
  - L353 the position reloads
  - L354 ...and the record of what has moved
  - L356 ...and the bank is funded by it on the first tick, not the second
  - L369 the fixture is genuinely mid-stop
  - L374 a city loaded mid-stop is still in it
  - L375 ...with the same months left to run
- **L378 8. and the city's own money goes the other way**
  - L401 fixture: the borrower really owes something
  - L410 idle money goes abroad for the world's rate
  - L411 ...most of it, at this spread
  - L413 ...and not all of it - working capital stays
  - L416 the coupon rolls where it is earned, so the wealth grew
  - L418 ...and nothing of it landed in the till
  - L420 a sector with a loan keeps its money home
  - L427 it comes home when the bank pays better
  - L428 ...to nothing, in the end
  - L443 fixture: the surplus alone would strengthen the currency
  - L445 a surplus sent abroad pushes the currency nowhere
  - L465 a live city sends money abroad on its own
  - L466 ...and every month of it is conserved

## CarCheck.java - 53 labelled assertions

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
  - L274 a city with money in the bank motorises
  - L278 ...and the road is told about them
  - L301 a month of car sales is a month of real money
  - L305 ...and every car sold was paid for at the same price
  - L310 ...and the savings really left
  - L320 no household owns less than none of a car, or more than one
  - L321 ...and the fleet is the cells' own count
- **L327 and a fleet is a stock, not a flow**
  - L336 a fleet wears out at one part in CAR_LIFE_MONTHS a month
  - L341 ...and a household whose car died replaces it at once, not at the diffusion rate
- **L362 WHY THE BAR IS 95% AND NOT 100%, and why it is not the plateau this**
  - L381 a city that can afford cars ends up with one per household, bar the grain of a whole car
  - L403 ...and a city that cannot loses its fleet over the life of a car
- **L407 THE DEPOSIT AND THE LOAN (2026-09-17)**
- **L411 and they borrow for it**
  - L430 a household short of the cash still buys, on credit
  - L444 ...and the seller is paid in full
  - L447 ...of which the household paid every penny it had spare
  - L450 ...and a lender found the difference, and said so
  - L453 ...and the two halves are the whole price
  - L455 ...and the bank's fee on the loan is added to what they owe, a share of it
  - L477 a household that can pay cash borrows nothing
  - L491 a household with nothing down and no room left buys nothing
  - L506 ...but a household that owes a little still buys one
- **L511 AND A FAMILY IN TROUBLE SELLS IT (2026-09-17)**
- **L522 and a family in trouble sells the car**
  - L553 a household that cannot feed itself puts the car up
  - L563 ...and somebody buys it
  - L565 ...for less than a new one, and more than scrap
  - L575 the fleet did not shrink - it changed hands
  - L578 ...off the family that could not keep it
  - L581 ...and onto the one that could
  - L594 the seller was paid what the car went for
  - L597 ...and every dollar of it came out of a buyer or a lender
  - L616 a household that can ride the gap out keeps it
  - L641 a city where everybody is selling gets the floor and nothing else
  - L646 ...so the cars stay where they were, and so does the hunger
  - L654 a city whose transit could carry everybody halves what it will own
  - L660 ...and a city with no transit deters nobody, exactly
  - L681 a metro built after the cars unmotorises the city to its ceiling
- **L688 and it all survives a save**
  - L697 a reloaded city has the same fleet
  - L700 ...the same ownership rate on the road
  - L703 ...and the same memory of the commute
  - L719 a save from before cars existed still loads
  - L721 ...and the city it loads owns none

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
  - L259 fixture: a cheap city really is borrowed from
  - L260 what the bank lends crosses the border
  - L261 ...and every month of it still balances
  - L262 ...with the bank's book holding exactly the stock
  - L284 fixture: the surplus alone would strengthen the currency
  - L286 money borrowed and taken out offsets a surplus

## CentralBankCheck.java - 175 labelled assertions

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
  - L191 savers are paid the share of the dial its funding asks for, a step of the way there
  - L193 ...out of what its reserves earn
  - L195 ...so the deposit rate rises with the dial
  - L202 ...paid in money the central bank made
- **L204 3. the window**
  - L213 past its deposits, what it borrows is the window's
  - L216 the window charges the dial plus the penalty
  - L218 ...on the window's tranche, and nothing on the deposits
  - L220 ...and a bank that is borrowing has no reserves to be paid on
  - L226 the advance is money made
  - L227 ...and the interest is the central bank's, and destroyed
  - L229 ...so M0 is what it lent less what it was paid
  - L232 a smaller shortfall is a repayment
  - L243 fixture: a bank past its deposits whose equity went under has failed
  - L246 a failed bank is charged nothing at the window: its equity is zero by the resolution,
  - L251 ...while the central bank goes on advancing what it owes
  - L253 ...interest-free
- **L255 4. the city's paper**
  - L265 the floor is the policy rate itself
  - L266 ...so a debt-free city is quoted the dial
  - L270 ...and one that owes, the dial plus both spreads
  - L273 ...where the spreads are real, not zero
  - L275 the ceiling sits on the dial too, not under it
  - L277 what a different dial would be quoted is the same sum
  - L281 ...and nothing lends the city below what the money costs the bank
- **L284 the city**
  - L310 fixture: the city has a bank
  - L311 fixture: and a year of revenue for the ceiling
- **L313 1 and 5. every kind of flow**
  - L320 fixture: the bank has spare cash to be paid on
  - L322 the central bank paid exactly what the bank booked on its reserves
  - L324 ...and it paid something
  - L336 fixture: the treasury sold the bank a bond
  - L338 the bank bought it at the window
  - L339 ...which charges the dial plus the penalty
  - L341 ...and its interest is the central bank's, to the cent
- **L345 5. a broke treasury draws advances, and repays them first**
  - L357 the shortfall is advanced whole, net of the remittance that arrived first
  - L359 ...and owed
  - L360 ...and journalled as printing on the bridge
  - L371 the advances' interest is the policy rate on what was owed
  - L373 the remittance is last month's profit, as struck
  - L374 cash above zero repays the advances before anything else
  - L378 ...the first thing the month's journal records
  - L381 the month's profit is the interest it took less what reserves cost
  - L383 ...and it is owed back to the treasury once any loss is made good
  - L385 ...or carried, if it was a loss
  - L389 fixture: a profit was struck to remit
  - L403 a buyback: the bank's share of the price goes to its cash
  - L405 ...its book drops by its share of the principal
  - L407 ...and the difference against what it carried the paper at is its gain or loss
  - L409 ...and was the price quoted
  - L412 with the bond sold back, the bank repays the window
  - L413 ...and the month's profit reaches the treasury the month after
  - L415 ...as a revenue line on its budget
- **L421 1, closed: every kind of flow, and every month**
  - L423 the run had
  - L433 ...and a month with five kinds at once, which is all a month can hold
- **L438 6. the ceiling and the arrears**
  - L445 fixture: the advances are past the ceiling
  - L446 fixture: the city owns something that needs repairing
  - L448 its repairs were refused, cash being nothing
  - L451 ...and owed to the builders as arrears
  - L453 ...who were paid the rest of the bill and not that part
  - L461 a promise is paid whatever the treasury holds
  - L463 ...overdrawing it
  - L465 a discretionary line is refused
  - L468 ...and the refusal is owed
  - L469 a purchase is refused and nothing is owed
  - L475 ...a building the treasury cannot pay for is not ordered
  - L477 ...nor capital put into the bank
  - L478 ...nor reserves bought
  - L481 the promise's overdraft is advanced past the ceiling
- **L484 ...and the arrears are paid down first when cash returns**
  - L487 fixture: the treasury owes arrears
  - L491 the central bank was repaid in full
  - L492 ...and then the arrears
  - L493 ...every dollar of them
  - L500 ...in that order
- **L504 7. the autopilot**
  - L509 fixture: there is a year of prices to read
  - L512 fixture: the rule wants something else
  - L514 with the rule's hand on it, the month opens with the dial where the rule says
  - L517 the player's hand takes it back
  - L519 ...and the rule leaves it where the player put it
  - L528 the toggle survives a save
  - L535 ...off, too
  - L536 ...and a dial at 0% reloads at 0%, not the 3% default
- **L540 9. the save**
  - L548 fixture: advances and arrears to carry
  - L559 the central bank's whole balance sheet reloads exactly
  - L560 ...M0
  - L561 ...what the treasury owes it
  - L563 ...the ceiling
  - L564 ...and the arrears, line by line
  - L566 ...and M2
  - L568 ...and a year of M0 behind it
- **L571 8. a currency reform**
  - L579 fixture: the reform happened
  - L580 M0 is a hundredth
  - L581 ...the treasury's advances
  - L582 ...the bank's at the window
  - L583 ...the loss carried
  - L584 ...printed since founding
  - L585 ...the ceiling
  - L586 ...and the arrears
  - L587 but the policy rate is the policy rate
  - L588 ...the advances against the ceiling are where they were
  - L590 ...and M0 against M2
  - L593 ...and the first month in the new money closes
- **L595 10. an old save**
  - L601 fixture: the city carries a note
  - L607 fixture: the save carried
  - L616 it loads
  - L618 with an empty central bank: nothing made
  - L619 ...nothing advanced
  - L620 ...nothing printed
  - L621 ...nothing owed in arrears
  - L622 ...and the player's hand on the dial
  - L625 and it runs, closing the audit and the M0 identity every month
  - L629 ...and runs its note off
  - L632 a new game after a load founds a fresh central bank
  - L634 ...with nothing owed
- **L651 11. the holdings dial buys the bank's term paper with money it makes**
  - L680 fixture: the treasury sold a twenty-year bond
  - L683 fixture: ...and the bank holds all of it
  - L685 fixture: ...and the central bank none
  - L686 with nothing held, the long end is the table's premium over the note
  - L699 after one month the central bank holds QE_SPEED x 30% of it, at face
  - L701 ...which is what the paper says it holds
  - L702 it paid the market value at the curve
  - L703 ...and the bank was paid exactly that
  - L704 ...money it made: the audit's issue carries the price
  - L707 ...and M0 moved by exactly what it made less what it destroyed
  - L709 the bank's book fell by the face
  - L710 ...and its book on the bank's own sheet with it
  - L711 the bank booked its gain against what it carried the paper at
  - L713 ...and the central bank its own against face, into the month's profit
  - L716 compression(240) is the twenty-year premium times the share held over the most it may hold
  - L719 the twenty-year rate sits exactly compression(240) under the table
  - L721 ...and the note carries none of it
  - L722 ...so the short end is where it was: the dial and the spreads
- **L727 12. the coupon on its share is the central bank's, destroyed, and remitted**
  - L732 the coupon on its share arrived at the central bank
  - L733 ...and was destroyed with the rest of what it took back
  - L737 the month's profit carries it
  - L739 ...owed back to the treasury once any loss is made good
  - L742 ...and remitted the month after
- **L745 13. the dial to nothing sells it back, and the curve returns to the table**
  - L747 fixture: the central bank holds some of the bond
  - L758 the dial to 0 sells it all back within the speed's months
  - L759 ...the paper agrees
  - L760 ...a step at a time, not in one month
  - L761 money retired equals what the bank paid
  - L762 ...and the curve returns to the table
- **L765 14. the holdings survive a save**
  - L769 fixture: holdings to carry
  - L775 fixture: the dial was moved, so the setting before it is not the dial
  - L785 the paper it holds
  - L786 ...the dial
  - L787 ...and the setting before it, which sets the pace
  - L789 ...so the reloaded city steps at the same pace
  - L791 ...what the paper says it holds
  - L793 ...and the long end of the curve
- **L796 15. a reform scales the holdings and not the dial**
  - L801 fixture: the reform happened
  - L802 the paper it holds is a hundredth
  - L803 ...on the paper too
  - L804 the dial does not move
  - L805 ...nor the compression it buys
  - L808 ...and the first month in the new money closes
- **L820 16. the ceiling is the player's dial, up to three years of revenue**
  - L838 a city opens at the default
  - L840 fixture: a year of revenue to set it on, nothing owed, nothing in arrears
  - L849 ...which is DEFAULT_ADVANCES_MONTHS of trailing revenue
  - L852 fixture: overdrawn by the whole ceiling, the treasury has no room
  - L854 ...so a purchase is refused
  - L857 set to twelve months, the ceiling doubles
  - L858 ...and the room is the six months it added
  - L859 ...so the same purchase is paid, overdrawing further
  - L865 a treasury that was at the old ceiling draws past it
  - L867 ...and is still inside the new one
  - L869 the dial stops at MAX_ADVANCES_CEILING
  - L872 ...and at nothing below
  - L881 the setting survives a save
  - L882 ...and so the ceiling
  - L886 fixture: the save carried the dial under its own key
  - L893 a save from before the dial reads the default: six months, the constant it was
  - L899 fixture: the reform happened
  - L900 a reform does not move the dial: months are not money
  - L901 ...while the ceiling, which is money, is a hundredth

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

- **L123 1. FOOD**
  - L194 every month, food in equals food out plus the change in stock
  - L211 ...and the warehouse is not simply frozen
  - L212 ...and the stock level actually moves
- **L214 2. ELECTRICITY**
  - L224 the utility's revenue is exactly what the sectors were charged
  - L234 there is unbilled draw, and it is not booked as revenue
- **L237 3. WATER**
  - L243 the water utility's revenue is what the sectors were charged
- **L246 4. A CITY IS THE SAME CITY AFTER YOU LOAD IT**
  - L278 
  - L284 ...and matches the city that was saved
  - L310 
  - L313 ...and it is back at month one
- **L315 5. WHAT A RELOAD MUST NOT FORGET**
  - L354 the mine has a payroll at all after a load
  - L355 ...and it is the payroll it was working
  - L359 utility income survives a load
  - L361 the residents' statement is not blank after a load
  - L363 ...and the government's revenue block is not either
  - L371 ...and rebuilding it does not book the month twice
  - L379 the population trend survives, so forecasts are not flat
  - L387 every sector's losing streak survives a load

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

## CreditCheck.java - 263 labelled assertions

> Verifies private-sector credit: pricing, origination, rollover, cash conservation.

- **L35 1. pricing**
- **L54 pricing: prime + the borrower's own expected loss, off the curve**
  - L63 no debt -> no spread over prime
  - L64 no debt -> rate is prime
  - L71 leverage 0.5
  - L72 leverage 0.5 -> spread, its expected loss over the book's
  - L74 ...which at half its assets is nothing
  - L75 leverage 0.5 -> rate
  - L81 leverage now 2.0
  - L83 at 2.0 the spread is LGD x PD(2.0) less BASE_LOSS_RATE, uncapped
  - L84 rate is prime + that
  - L90 prime 20% -> business 20% + the same spread
  - L96 negative assets -> the whole curve, LGD less BASE_LOSS_RATE
  - L105 no debt but insolvent -> the whole curve, not prime
  - L106 ...and the loan is written at that rate
  - L116 quoted rate before borrowing
  - L118 but a big loan prices itself in
  - L139 a project's loan is priced at the leverage it leaves the sector at, its building counted
  - L142 ...which is the leverage canFundProject() reads after the deal
  - L144 ...dearer than today's quote when it takes the sector up the curve
  - L147 ...and the loan is written at exactly the rate it was judged at
  - L152 a shortfall loan counts no building: what it will owe over what it owns
- **L156 ...and it pays its fee out of the proceeds (0.7.7)**
  - L157 a loan's fee is Bank.LOAN_FEE of its principal
  - L159 ...counted for the month, for the bank to collect at the settle
  - L161 ...and against the sector that borrowed
  - L164 ...and cleared with the month
- **L166 2. rate is fixed at issue**
  - L179 new borrowing got dearer
  - L180 but the old loan's rate is unchanged
  - L181 interest still priced off the old rate
- **L184 3. origination**
  - L192 solvent sector borrows nothing
  - L193 ...and has no debt
  - L198 handed hole + buffer, the fee kept back
  - L199 principal on the books: that, grossed up for the fee
  - L201 one loan, not many
- **L203 ...and it stops at the borrower's own insolvency line**
- **L225 ...and the ceiling is BELOW the line, by a real gap**
  - L236 fixture: the ceiling is strictly below the write-down line
  - L241 a loan is capped at the ceiling, not at what was asked
  - L242 ...so the principal sits exactly on it
  - L251 ...and the borrower is short of the default point there
  - L257 a borrower at the ceiling survives a one-third fall in assets, short of the default point
  - L272 fixture: the borrower at the ceiling owes interest this month
  - L274 at the ceiling, the desk hands over this month's interest and no more, its fee on top
  - L276 ...so the books moved by exactly that
  - L277 ...and there is nothing left to lend for losses
  - L282 the room the shortfall desk has grows with the assets
  - L285 a borrower whose assets grew can borrow again
- **L288 ...and a shut lender lends nothing, from either desk**
  - L299 a frozen bank lends nothing to a short sector
  - L300 ...and offers the investment desk no room
  - L302 ...until it is standing again
  - L309 a sector with no assets at all cannot borrow a penny
- **L312 ...and a repeat defaulter is shut out for longer**
  - L340 fixture: attempt
  - L349 a first default costs a year
  - L350 ...a second costs longer than the first
  - L352 ...and a third longer than the second
- **L355 ...and the record is priced, not only banned on**
  - L378 fixture: the two borrowers really are at the same leverage
  - L380 a serial defaulter is quoted more than a spotless borrower at the same leverage
- **L383 ...and a bankruptcy forgives the overdraft, once**
  - L399 fixture: overdrawn past everything it owns is insolvent
  - L401 the loan is written off in full against nothing
  - L402 ...and the overdraft with it
  - L403 ...once
  - L404 one default on the record
  - L405 ...and a ban
  - L408 fixture: it is insolvent again inside its ban
  - L409 ...but a sector inside its ban is not a new default
  - L410 ...so the record does not grow
  - L412 fixture: the ban has lifted
  - L414 still under water when the ban lifts IS a new default
  - L415 ...and the overdraft it ran up meanwhile is forgiven too
  - L421 a sector with no loans and an overdraft past its plant is insolvent
  - L423 ...and one whose plant still outweighs the overdraft is not
- **L425 4. maturity and rollover**
  - L437 still outstanding at month 35
  - L438 nothing matured yet
  - L441 loan retired
  - L443 principal fell due
  - L444 and is only handed over once
  - L449 balloon took cash negative
  - L454 refinanced back to zero
  - L455 new loan on the books, grossed up for its fee
  - L457 matures 36 months later
- **L459 5. cash conservation**
  - L472 there is interest to pay
  - L488 operating income excludes interest
  - L489 interest expensed
  - L490 pre-tax income is net of interest
  - L491 cash moved by exactly that
  - L496 processMonth moved no cash
- **L498 6. balance sheet integration**
  - L504 loans payable
  - L505 still balances
  - L506 equity is now assets less debt
- **L508 7. the spiral guard**
  - L530 cash never left negative
  - L531 loan count stayed readable (<20, not 60)
  - L534 its rate is the curve's at its leverage
  - L536 ...under what the curve charges at the shortfall ceiling
- **L540 the CITY's debt market, repriced**
  - L563 a debt-free city with no overdraft prices at the floor
  - L572 being overdrawn costs more than being clean
  - L573 ...and a positive balance is not credit
  - L584 a big loan is quoted dearer than a small one, on the same books
  - L596 ...and ten million is well up the band, not near the floor
  - L598 the standing rate is unmoved by merely asking
  - L610 more borrowing never gets cheaper
  - L611 the quote never leaves the band
  - L625 the quoted rate is a fixed point of its own face value
  - L627 ...and a discounted bill costs more than its cash value implies
  - L639 proceeds that close the overdraft are not counted twice
  - L647 a city that cannot tax its economy is the worse credit
- **L652 7. the quote IS the deal**
- **L667 the quote is the deal**
  - L710 three hundred quotes book no debt
  - L712 three hundred quotes move no cash
  - L714 three hundred quotes leave the standing rate alone
  - L727 asking for eight times as much is priced dearer
  - L729 ...and the small one is a real quote, not the cap
  - L731 the quote reports the rate it is moving from
- **L748 9. A NOTE DELIVERS WHAT IT WAS ASKED FOR**
- **L770 a note raises what it was asked to raise**
  - L790 every note covers the cash it was quoted for
  - L807 a serial bond's face IS the request
  - L809 ...and the city receives par less fees, by design
- **L812 10. THE STORY THAT BROKE, END TO END**
- **L819 borrow for a road, and get the road**
  - L829 the Paved Road template exists
  - L872 the fixture is actually short of the money - or this proves nothing
  - L874 ...and the city is told so rather than refused outright
  - L876 nothing was built by the refusal
  - L887 the note actually covered the gap
  - L892 THE ROADS ARE BUILT
  - L893 ...all forty of them
  - L896 ...and the cash was actually spent, not left sitting
  - L898 ...and there is a receipt for it
- **L900 10. THE BAN IS ONE BAN**
- **L912 a borrowing ban also stops the investment advisor**
- **L916 MORE PEOPLE THAN THE SHOPS CAN COVER, AND IT HAS TO STAY THAT WAY.**
  - L987 fixture: retail is under a borrowing ban
  - L1051 a banned sector takes no new loan to expand
  - L1054 ...and either its owners paid for the shops, or the refusal says why in the ban's own words
  - L1058 ...and nothing was built on credit it did not have
- **L1101 13. can't pay means default, and what is still unpaid is lent as interim financing, ranked first**
  - L1124 fixture: the sector is past the line, and the desk lends it nothing
  - L1125 fixture: the part that cannot pay is more than the curve's share at its leverage
  - L1126 fixture: once that part of its debt is written off it reads under the line on its quarter, the loan counted
  - L1129 a sector refused credit whose till is short defaults that month, $100 of its bills unpaid
  - L1131 ...the part that cannot pay: what it was short over what the month asked of it
  - L1133 ...and its debt is sliced at that share, the larger reading
  - L1134 recoveries by instrument: the loans lose a loan's loss on the share
  - L1136 ...the bonds a bond's
  - L1137 the unpaid rest is lent as an interim loan, grossed up for its fee
  - L1138 ...an interim loan, which a save will carry by its own type
  - L1139 ...owed beside what is left of the rest
  - L1140 ...its till ends the month at nothing, not short
  - L1141 ...handed exactly what was unpaid
  - L1142 ...and nothing is forgiven
  - L1145 ...priced by the bank's own rule at its rank: cheaper than the sector's loans, nothing ranking ahead of it
  - L1147 ...counted as the default point's refusal
  - L1148 ...and a slice is not a restructure: no ban, no record
  - L1159 fixture: a later month's slice
  - L1160 in a later slice the interim loan loses nothing: it ranks ahead of all the sector's other debt
  - L1162 ...while the loans lose their own share
  - L1179 a sector a dollar short is sliced at the curve's share, the larger: one slice, never the two added
  - L1181 fixture: after the curve's slice it still reads past the line on its quarter
  - L1183 a sector still past the line after the write-down is lent nothing more
  - L1185 ...and goes to the backstop, the whole sector: banned, and on its record
  - L1187 ...written down to RESTRUCTURE_TARGET of what it owns
  - L1189 ...its overdraft closed as the backstop closes one: forgiven
  - L1190 ...its till at nothing
  - L1206 fixture: it owes an interim loan, and still past the line after the month's slice it goes to the backstop
  - L1209 in the backstop the interim loan is kept first, whole: written down only after the sector's other debt
  - L1211 ...the loans keep what is left of the target
  - L1212 ...and the backstop of a sector with nothing left keeps nothing: the interim loan goes last, with the rest
  - L1228 a sector that can borrow is lent what it is short, and more
  - L1229 ...and does not default for want of cash
  - L1230 ...nor is lent anything in the interim
  - L1231 ...its slice only the curve's at its leverage
  - L1250 fixture: banned
  - L1251 a banned sector short of cash defaults too
  - L1252 ...counted as the ban's
  - L1253 ...owing nothing, nothing is sliced
  - L1254 ...and the unpaid $50 is lent in the interim: the ban binds the desks that lend to grow, not the interim lender
  - L1256 ...nothing forgiven
  - L1257 ...nothing new on its record, still banned
  - L1273 a banned sector refused the interim loan has its overdraft closed by the backstop
  - L1274 ...inside its episode: nothing new on its record
  - L1289 a sector whose bank is shut is refused, counted as the bank's
  - L1292 ...and nobody lends in the interim either: the whole sector to the backstop
  - L1294 ...which closes its overdraft
- **L1316 15. a new sector's first reading: month-ends of nothing are not a quarter**
  - L1327 month-ends at which a sector owned and owed nothing file no reading
  - L1331 ...so with its first plant it reads as it stands, under the line
  - L1336 a sector whose first plant arrived after months of nothing borrows its first bill
  - L1337 ...and does not default on it
  - L1338 ...nor is it banned
  - L1340 its first month-end holding plant is the first reading of its quarter
  - L1352 a sector that owned plant and crashed still reads its real quarter
  - L1353 ...past the line on it
  - L1355 ...and a month-end of nothing after it leaves that quarter as it was
  - L1356 ...its readings unchanged
  - L1384 fixture:
  - L1395 fixture:
  - L1397 a sector whose first plant was handed over between months borrows its first bill (
  - L1399 ...with no default on it (
  - L1401 ...and with no interim loan (
  - L1402 ...nor a ban (
- **L1431 16. the shortfall desk's bond, grossed up: the till is handed what it was short**
  - L1445 fixture (
  - L1446 a shortfall covered by
  - L1447 ...the bond's costs carried in the loan's face
- **L1465 14. credit lines stay open: a rationing bank covers a short month and refuses growth**
  - L1483 fixture: the bank is short of capital - between its minimum and its target, rationing
  - L1485 fixture: the rule would let the sector owing $100,000 borrow $100 more
  - L1486 ...and the sector owing nothing, nothing
  - L1488 a rationing bank covers a healthy sector's short month whole, its fee on top: a working-capital line
  - L1490 ...counted as the line lent while the bank rationed
  - L1491 ...all but $100 of it past what the 0.7.8 rule would have lent
  - L1493 ...a sector that owes nothing too, whose room under the rule is none
  - L1494 ...and refuses its building's loan: that is growth
  - L1495 ...counted at that door
  - L1498 fixture: a sector over the ceiling, under the line
  - L1500 a sector over the ceiling is refused all but its interest reserve
  - L1502 ...and one past the line is refused everything
  - L1510 the lines the bank honoured leave nobody to default: the sector that owed
  - L1511 ...and the one that owed nothing
  - L1512 the sector over the ceiling defaults for want of cash, on its own credit
  - L1514 ...and so does the one past the line
  - L1526 a bank under its minimum honours the line too
  - L1527 ...and funds no building, however small
- **L1545 12. nothing past the default point: the desks lend nothing past INSOLVENCY_TRIGGER**
  - L1565 fixture (
  - L1578 fixture: paying what fell due takes it past INSOLVENCY_TRIGGER
  - L1580 ...where the old ceilings, reading the refresh, had room: the interest reserve
  - L1582 a sector past the line is refused the shortfall loan
  - L1583 ...so nothing is written
  - L1592 ...though filling the whole hole would leave it under the line on paper
  - L1598 the investment desk refuses it too, whatever the building would add
  - L1601 ...where the test after the deal alone would have funded it
  - L1604 fixture: one whose maturity leaves it under the line
  - L1605 a sector under the line is lent
  - L1606 ...nothing refused
  - L1608 ...and the loan leaves it under the line
  - L1627 a landlord at 1.2 times what it owns renews its mortgage at the term's end
  - L1630 ...one at 1.6 does not: the balance falls due whole with the term's last payment
  - L1632 ...and the mortgage is gone, counted against the default point
- **L1645 12. ...on the quarter: the month under and the quarter over is refused, the other way round is lent**
  - L1665 fixture: paying what fell due leaves it under the line as it stands, and its quarter reads it over
  - L1667 a sector whose month reads under the line but whose quarter reads it over is refused
  - L1668 ...counted against the default point
  - L1670 fixture: paying what fell due takes it past the line as it stands, and its quarter reads it under
  - L1672 a sector whose month reads over the line but whose quarter reads it under is lent, as its price reads it
- **L1678 12. ...what a sector past the line does instead, in a played city**
  - L1697 fixture: the city's builders have plant and a till, and a standing bank
  - L1712 fixture: the bank's quarter reads the builders past the line
  - L1720 the shortfall desk refused the builders once paying what fell due took them past the line
  - L1722 ...so their till went short: the overdraft
  - L1723 ...and the month's slice wrote their debt down: at the curve's rate for its leverage, or since round 4 the part that could not pay, whichever is larger
  - L1725 ...what it could not pay defaulted that month, and the till ends it at nothing (round 4)
  - L1731 ...and still past the line after it, nobody lent them the rest: the whole sector went to the backstop (round 5)
  - L1734 ...and the month's money audit closes
  - L1752 fixture: a sector with plant, outside any ban:
  - L1755 fixture: the month's defaults read its assets at or below nothing
  - L1760 with its overdraft past its plant the backstop writes it down whole
  - L1762 ...forgives the overdraft and bans it
  - L1764 ...and that month's audit closes too
  - L1783 fixture:
  - L1786 fixture: the bank lent it all, no bond: what it owes more is what the bank wrote
  - L1788 a project loan hands its full purpose to the till
  - L1789 ...its fee carried in the principal: the loan is the purpose grossed up for LOAN_FEE
  - L1791 ...and the fee the bank keeps is LOAN_FEE of that principal
- **L1810 11. the curve: the dial at the short end, a premium by maturity**
  - L1823 debt-free, the note quotes exactly the dial
  - L1824 ...and getRate() is the note's rate, the short end
  - L1825 the 10-year term loan: the dial plus TERM_PREMIUM_10Y
  - L1827 the 20-year: the dial plus TERM_PREMIUM_20Y
  - L1829 the 50-year: the dial plus TERM_PREMIUM_50Y
  - L1831 ...and past fifty years the curve holds
  - L1832 a 5-year serial: the dial plus the 10-year entry, 48 of the 108 months from a year to ten
  - L1834 ...a 35-year point halfway between 30 and 40
  - L1836 ...and a year-long note carries none
  - L1844 a hike of two points moves every row by two: the whole curve in parallel
  - L1850 fixture: the debt adds a real credit spread
  - L1855 debt moves every row by the same credit spread
  - L1861 a 25-year request is refused, in words
  - L1862 ...quoted nothing
  - L1863 ...and books nothing
  - L1869 ...while each of the five maturities is quoted
  - L1878 a bond issued at 20 years is bought back the same month for what it raised,
  - L1882 ...and the buyback takes it off the books at that price

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
  - L266 with only the founding constabulary, coverage is 4.32 officers over the city
  - L268 ...and crime is above Canada's
  - L269 a police station brings it well under
  - L270 ...but not to nothing
  - L271 every month passed the money audit, the thefts from the tills declared
  - L272 every business's cash flow explained its till, the thefts a line of their own
  - L273 fixture: something was stolen from the businesses
  - L276 ...and the tills' lines add up to it
  - L277 violence killed somebody
  - L278 the graph holds this month's killed
  - L281 injuries are on the sick rate
  - L282 the crime notice is up exactly when crime is at 1.5x Canada's or worse
  - L285 migration read last month's crime
  - L291 a theft takes from the households' savings what it says it took
  - L296 ...and handing it to the offenders puts every dollar back
  - L300 fixture: somebody is in prison
  - L301 the prisoners are out of the labour force
  - L303 ...the whole of them
  - L304 the pool is still the labour market's
  - L308 the families hold the adults outside nothing: not the pool, the students or the prisoners
  - L312 the prisoners' ledger holds the prisoners, as the month opened
  - L314 ...pays no rent
  - L315 ...buys no food
  - L316 ...and pays no interest
  - L317 ...and none of them is caught but not held with cells to spare
  - L320 the treasury pays the police and the prisons
  - L321 ...on the government's books
  - L322 ...and it costs something
- **L325 a save**
  - L329 the crime comes back, the prisoners in their months
  - L331 ...and out of the labour force on the load path
  - L333 ...and the bill
  - L334 ...and the ledger

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
  - L439 every month, Game handed the currency the real rate differential
  - L441 fixture: the currency was defended in a real city
  - L449 fixture: the treasury neither bought nor sold
  - L450 the sale is the vault's capacity times the month's own deficit, at most the vault
  - L452 ...the vault fell by exactly it
  - L453 ...and it fetched the dollars at the month's rate
  - L454 the central bank booked it the month it was sold
  - L455 ...as spent defending the currency since founding
  - L456 equity fell by it: its move, less the rest of the sheet's, less the revaluation
  - L478 a save keeps the month's sale
  - L479 ...what it fetched
  - L480 ...the dollars sold since founding
  - L481 ...the vault
  - L482 ...what the central bank spent on it
  - L483 ...its equity
  - L484 ...and the real rate the reprice was handed
  - L510 the defence fires in a real city more than once
  - L511 ...and the equity line is every month's sale at its own month's rate
  - L513 in every one of those months: M0 moved by the money made alone
  - L514 ...nothing retired but what the named operations took back
  - L515 ...the audit closed, and declared nothing for the defence
  - L516 ...the remittance was the month's profit, the sale not in it
  - L517 ...and the central bank kept nothing: its equity less the vault is what it owes
  - L558 M0 moved by the money made and nothing else,
  - L559 ...nothing retired but what the named operations took back
  - L560 ...the audit closed, and declared nothing for the defence
  - L561 ...the remittance is the month's profit, the sale not in it
  - L562 ...and equity less the vault is what the bank owes: nothing kept
- **L568 5. the dial, the rule and the appetite are uncapped**
  - L572 the dial takes 60%
  - L574 ...and stops at MAX_POLICY_RATE, where a typo would have taken it
  - L576 the rule is advised unclamped past the old stop
  - L602 fixture: the city reads a year of 45% inflation
  - L609 the autopilot sets the rule's rate
  - L611 ...past the old stop of the dial
  - L621 the appetite at twenty points is larger than at six: it no longer stops there
  - L623 ...it is twenty points' worth
  - L624 ...and it stops at MAX_SPREAD
  - L627 a month at twenty points is pulled by all twenty
  - L628 ...toward the stock twenty points want
- **L656 6. the rate goes where the push takes it: the guards are a billion either way**
  - L675 fixture: past settling, trade in balance, the vault empty, the whole economy trading
  - L681 a month's push past the old guard of 100 takes the rate to 150, not 100
  - L683 ...and the dollar debt is worth the dollars at it
  - L684 ...the month's revaluation the whole of the move
  - L699 a rate under the old guard crosses it in a month
  - L700 ...and every month is the push and the pull, never the guard
  - L712 a month's push past the old floor of .01 takes the rate to 1/150, not .01
  - L714 ...and the dollar debt is worth the dollars at it
  - L715 ...the month's revaluation the whole of the move, a gain
  - L719 a rate over the old floor crosses it in a month, by the push and the pull alone
  - L721 ...to under .01
  - L731 three zeros off: the rate a thousandth
  - L732 ...the guard above it a thousandth of MAX_RATE
  - L734 ...and the one below a thousandth of MIN_RATE
  - L740 ...and the push moves it as it moves the unreformed one, a thousandth the size
  - L744 a city founded in the new unit carries the guards in it

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

## DenominationCheck.java - 43 labelled assertions

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
- **L160 1. THE UNIT**
  - L164 a founding city is in founding money
  - L165 ...and cannot reform, because nothing has inflated
  - L167 ...nor at nine times
  - L168 ...and can at ten
  - L171 lopping two zeros makes a dollar a hundred old ones
  - L172 ...so a founding price reads as a hundredth
  - L175 ...and the money gets a new name
  - L176 ...the city's own money's, renamed
  - L178 ...and again
  - L179 the unit compounds
  - L183 it stops before a double runs out of digits
- **L186 2. IT IS THE SAME CITY**
- **L192 a reformed city is the same city**
  - L197 fixture: the two cities really are identical to start with
  - L209 the reform goes through
  - L211 cash divided
  - L212 the exchange rate divided
  - L215 the shelf price divided
  - L217 rent divided
  - L218 the minimum wage divided
  - L221 a House costs a hundredth as many dollars
- **L224 ...AND THE SHARE DESK'S ROOM DOES NOT DIVIDE, WHICH IS THE POINT**
  - L256 the desk's room in
  - L321 fixture: there was a per-good breakdown to divide (
  - L339 the price index did not move
  - L341 nor the rent burden
  - L344 nor the cost of living
  - L347 nor the population
- **L350 3. AND IT STAYS THE SAME CITY**
- **L401 ...and the month after the reform is the same month**
- **L409 ...and the same city a year later**
- **L413 ...and the same city a decade later**
- **L455 3a. AND NO CELL UNDER HALF A HOUSEHOLD HOLDS ANYTHING**
- **L468 ...and no cell under half a household holds anything, reformed or not**
  - L469 right after the reform, in either city
  - L472 fixture: the census left cells under half a household
  - L473 ...and after no month did one hold anything
- **L475 4. AND NO MONEY WAS MADE OR LOST**
- **L480 and the books still balance**
  - L488 a reformed city conserves money like any other
- **L490 5. AND IT SURVIVES A SAVE**
  - L513 the unit came back
  - L514 ...and the money with it
  - L515 ...and the rent it was charging
  - L516 ...and a House still costs what it cost

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
  - L933 ...while the journal's line is principal, net of what was lent
  - L937 ...and the month passed the audit with the interest in it
  - L939 ...and the ledger was told the city's rate
- **L941 15. THE PRICE OF A PLACE**
- **L951 the price of a place**
  - L962 at x3 every fee is three times the founding table, and the table itself has not moved
  - L963 ...and so is what a household pays out of pocket
  - L964 at x1 the fee is the founding fee, bit for bit
  - L967 the schools clamp the scale to the policy's ceiling
  - L1007 the schools charge at the city's scale
  - L1008 fixture: the price is what decides here - x3 cuts the willing share, not the seats
  - L1010 a poor city at x3 enrols fewer than at x1: the trap is back
  - L1015 at x0 everybody who would go can afford to
  - L1016 ...so everybody the cap lets go, goes
  - L1018 ...and more than at x3
  - L1019 a free place bills nothing and forgoes nothing
  - L1020 the treasury's fee revenue is the scaled fees households paid
  - L1032 ...and it collects more at the door at x3, from the students it kept
- **L1035 16. ALL THREE SURVIVE A SAVE**
  - L1048 fixture: a tuition-share grant was struck and interest was charged
  - L1050 the city saved
  - L1053 ...and loaded
  - L1055 the grant's basis and amount came back
  - L1057 ...and the loan rate
  - L1058 ...and the tuition scale
  - L1059 ...and the schools charge at it from the first read
  - L1061 ...and the ledger knows the rate
  - L1062 fixture: the rule re-struck from the reloaded city would not reproduce it - the body moved on
  - L1064 the bill the month struck came back as the save struck it, not re-derived
  - L1066 ...and so did the interest line
  - L1070 a policy array from before the dials is still read
  - L1071 ...as the founding basis at the share its own slot carried, no interest, the founding price
  - L1081 ...and an old save's own wage share is the grant it had
  - L1083 a wrong shape is still refused whole
- **L1086 17. A PRICE PER SCHOOL (0.7.6)**
- **L1095 a price per school**
  - L1108 raising the university's price moves its fee and no other kind's
  - L1109 ...so the nine have parted
  - L1110 ...and the every-school reading is the first kind's, unmoved
  - L1114 one kind is held to the policy's ceiling
  - L1121 the every-school setter moves all nine
  - L1129 the array carries the nine on the end
  - L1135 ...and each kind's scale comes back as it went
  - L1138 an array of the old length is still read
  - L1144 ...as nine equal scales, the one its slot carried
  - L1160 fixture: the schools cost something and collected something
  - L1161 the kinds' costs add up to the staff and buildings the page shows, to the cent
  - L1163 ...and their fees to the tuition households paid, to the cent
  - L1165 a kind with no school costs nothing and collects nothing
  - L1167 ...and the university, standing, costs something
  - L1169 the month re-struck at today's scales is what was billed
  - L1181 the month charges the university at its own price and the college at the city's
  - L1185 the city saved again
  - L1188 ...and loaded
  - L1190 the nine came back parted
  - L1193 ...and the load path told the schools the nine, not the one
  - L1198 ...and the month by kind came back with the save, not zero

## EquityCheck.java - 65 labelled assertions

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
  - L215 ...and a company with no book but earnings sells at the dividend's value, not for nothing
  - L221 ...while the same earnings with no dividend paid are worth only the book (round 2)
- **L224 6. the founders**
  - L230 a first offering against a book already there issues the book to the households first
  - L232 ...so the new money buys only what it paid for
  - L234 ...and the founders keep the company
- **L236 7. the dividend**
  - L239 nothing is due on a loss
  - L240 ...and the payout share of a profit
  - L242 a company with no shares owes nobody
  - L248 each household is paid on its shares, into its savings
  - L250 ...the households' part is theirs
  - L252 ...and the rest went abroad
  - L253 ...every share paid once
  - L267 twenty households leaving take twenty households' shares
  - L269 ...and the ones who stayed hold what they held
  - L274 ...and are paid abroad from then on
  - L276 ...with the register still agreeing with the households
- **L279 8. the save**
  - L283 it restores
  - L284 ...the shares
  - L285 ...held abroad
  - L286 ...and the lifetime dividends
  - L287 ...and the regime is re-read from the record
  - L288 an array of the wrong length is refused whole
  - L292 the households' shares ride in the cell save
- **L295 9. a live city**
  - L310 the founding bank's capital was sold as shares
  - L312 ...and every dollar of it arrived as capital, home or abroad
  - L329 retail is listed once it has a book
  - L331 ...and every share of it is held, at home or abroad
  - L350 the city's companies went to the market on their own
  - L351 ...and paid their owners
  - L352 ...and every month of it is conserved
  - L361 ...with the register and the households agreeing on every company

## ExchangeCheck.java - 193 labelled assertions

> Verifies the exchange on the order book (0.7.12 round 2): what the desk
> posts and what it is not obliged to take, who trades with whom and at what
> price, what a company does with its surplus, a split, and that a city with
> a market in it still adds up and survives a save.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Order book for both"; the
> round-2 brief: the dealer's quote is replaced by the book, the desk is one
> participant, the price is the last trade):
> 
>   1. THE PRICE IS THE LAST TRADE: before its first a company is priced at
>      fair value; the desk bids and asks half a SPREAD round fair value,
>      within its capital, and never asks what it does not hold; a trade
> ...

- **L199 1. the price is the last trade**
  - L210 a bank with capital posts on the book
  - L211 fair value is the register's reckoning: book over shares
  - L212 ...and before any trade the price is it
  - L213 the desk bids half a spread under fair value
  - L214 ...for the tightest of its limits: its capital (marked at the price it pays), its caps, the float
  - L216 ...and asks nothing: it holds nothing
  - L221 a household short of money sells into the desk's bid for what it is short
  - L222 ...at the bid: the shares it gave up
  - L223 THE PRICE READ IS THE LAST TRADE, the desk's bid
  - L224 ...the book's own last price
  - L225 ...with fair value beside it, unmoved
  - L226 the desk carries its inventory at the lower of the two
  - L227 ...and holds what it bought
  - L228 the register still agrees with the households
  - L231 a month on, with nothing traded, the price is still the last trade
  - L232 ...the desk asks half a spread over fair value
  - L233 ...for exactly what it holds
- **L235 2. nobody is obliged**
  - L245 a bank with no capital posts nothing
  - L256 a household short of money with nobody bidding sells nothing
  - L257 ...keeps its shares
  - L258 ...its ask waits on the book
  - L259 ...and it borrows what it was short
  - L260 the price did not move: nothing traded
  - L264 the step withdraws it and counts the seller who waited (what rests now is the step's own)
  - L274 fixture: a small bank's desk bids for the tightest of its limits
  - L277 fixture: ...which is less than a hundred households short of $90k each offer
  - L281 the desk takes what its limits allow and not a share more
  - L282 ...the household raising what it paid
  - L283 ...and the rest of its offer waits on the book, unfilled: AN UNFILLED SALE RESTS
- **L286 2b. the desk's caps (round 3)**
  - L298 fixture: its capital would carry more than a quarter of its equity in the company
  - L300 the desk bids for POSITION_LIMIT of the bank's equity in one company, at fair value
  - L302 ...counted as the cap that bound
  - L305 a household offering $5,000k sells only what the cap leaves
  - L306 ...the desk holding a quarter of the equity it posted on, at fair value, no more
  - L308 ...and the rest of the sale rests on the book
  - L326 fixture: what the book has left is under the single-name cap
  - L327 with $4,000k of Industry held, the desk bids for Retail only what BOOK_LIMIT leaves
  - L329 ...counted as the cap that bound
  - L330 ...its Industry bid none: it is at the single-name cap there
- **L333 2c. what is over the caps, offered at fair value**
  - L349 fixture: the desk holds more than POSITION_LIMIT of the bank's equity in one company, under BOOK_LIMIT in all
  - L358 a desk over its position cap offers exactly the excess, at fair value
  - L359 ...counted as the month's excess
  - L360 ...and the rest of what it holds at fair value plus half the spread
  - L361 nothing is asked under fair value
  - L362 with no bid to meet it, the excess rests, on offer at fair value
  - L363 ...and nothing of it sold
  - L381 fixture: the company's month of buybacks is less than the desk's excess
  - L382 the excess sells when a bid meets it: the company's buyback took it
  - L383 ...at fair value, not under it
  - L384 ...out of the desk's holding
  - L385 ...into the bank's cash
  - L386 ...and the rest of the excess still rests at fair value
  - L397 a desk under both caps offers no excess
  - L398 ...and asks what it holds at fair value plus half the spread
  - L419 fixture: the book is over BOOK_LIMIT, and only Retail over POSITION_LIMIT
  - L421 the book's excess is spread pro rata by value at fair value: Industry its sixth
  - L423 ...Materials the same
  - L424 a holding over both caps offers the larger of its two excesses, not the two added
- **L427 3. the leavers**
  - L439 fixture: twenty households leave with two thousand shares
  - L441 ...held abroad from the moment they go
  - L446 the leavers sell into the desk's bid, and the cash leaves with them
  - L448 ...out of the bank's cash
  - L449 ...onto the desk
  - L450 ...and nothing is held abroad any more
  - L451 the register still agrees with the households
  - L460 with nobody bidding, an emigrant's shares rest unfilled on the book
  - L462 ...nobody paid them
  - L465 the next step withdraws their ask and counts it waited
  - L467 ...and their shares stay abroad with them
- **L469 4. the world**
  - L474 fixture: the company is wholly foreign-owned
  - L481 fixture: the yield at the desk's bid is under the world's hurdle
  - L483 the world sells a share of its holding in proportion to the shortfall
  - L485 ...to the desk
  - L486 ...at the desk's bid, the resting price
  - L502 fixture: fair value is the dividend capitalised at the hurdle
  - L505 the world bids for a share of the float in proportion to the excess yield
  - L507 ...resting at the market, the last trade - not at its reservation
  - L512 a household short of money sells into the world's bid
  - L513 ...declared abroad, at the world's price
  - L514 ...raising what it was short and banking the rest
  - L515 ...the shares abroad again
  - L521 a company with no record is not traded by the world: not sold
  - L522 ...and not bid for
- **L524 5. the household cells**
  - L542 fixture: Industry yields more at the desk's ask than Retail, both over the deposit rate plus the premium
  - L544 the cell takes everything asked of the best yield first
  - L545 ...then the next best with the rest
  - L546 ...its month's money, out of its own savings
  - L547 ...into shares of both
  - L548 ...the register agreeing on each
  - L549 the desk sold nothing it did not have
  - L560 with nothing else asked, what is left rests as a bid on the best yield
  - L562 ...AT THE MARKET, its last trade - not at the price that yields the floor
  - L564 ...which is five times higher
  - L569 with the deposit rate above every yield, nobody buys
  - L570 ...and nobody's savings moved
  - L589 fixture: both are priced to the discount rate
  - L590 fixture: ...on different share counts
  - L592 tied names: the bid rests on the deepest
  - L600 a dividend moved by one ulp does not move where the money goes
- **L603 6. cell to cell**
  - L625 fixture: the rich cell's bid rests on the book; the short cell has nothing to bid with
  - L627 fixture: ...at the market, fair value
  - L636 the short cell sold into the rich cell's bid
  - L637 ...the rich cell holds what it sold
  - L638 ...at the rich cell's resting price, the last trade
  - L639 ...sized at its own ask, the price its borrowing rate puts on the dividend
  - L641 ...the cash between them the transfer counted
  - L642 ...the short cell raising what it was short
  - L643 ...and borrowing nothing
  - L644 no pool line moved: nothing sold to or bought from the households by anybody else
  - L647 the register still agrees with the cells
- **L649 6b. the cells rebalance by the bond rule (round 3)**
  - L662 fixture: the cell under its cushion holds more than its shortfall in shares
  - L668 the cell under its cushion offers HOME_SPEED of the shortfall, at the market
  - L670 ...the cell past its cushion takes all of it
  - L671 ...cell to cell, the transfer counted
  - L672 ...at the market: the price is where it was
  - L673 ...the seller's savings up by what it was paid
  - L674 ...and the buyer bids for the rest of OUT_SPEED of its excess
  - L677 no pool line moved
- **L680 7. the buyback, and the money that stays**
  - L686 fixture: the company is in a normal year
  - L701 it retires a month of the pace, in shares, at the desk's ask
  - L703 ...paid from its till
  - L704 ...to the desk, which was asking
  - L705 ...into the bank's cash
  - L706 ...and the shares are gone
  - L707 ...and nothing of its bid is left to rest: it spent the month's money
  - L718 fixture: six months of its costs with the payments are more than its till, without them far less
  - L722 a company whose loan payments take its cushion past its till buys nothing back
  - L724 ...bids for nothing either
  - L725 ...and its till is untouched
  - L744 fixture: the only ask rests past fair value plus the tolerance
  - L748 so the money stays in the till
  - L749 ...and the bid rests at its limit, fair value plus the tolerance
  - L751 ...for what the month's money buys there
  - L753 ...no holder is paid a dividend for it
  - L754 ...not one share retired
  - L771 fixture: nobody offers the company's shares at all
  - L773 with nobody offering its shares, the money stays in the till
  - L774 ...and the bid rests at its limit
  - L775 ...for what the month's money buys there
  - L777 ...not one share retired yet
  - L781 fixture: a household short of money sells in the waterfall and the bid meets it
  - L782 a household short of money sells into the resting bid, at the bid's price
  - L784 ...paid from the company's till
  - L785 ...the shares retired
  - L786 ...counted as a buyback from the households
  - L787 ...and the rest of the bid still rests
  - L797 a company in a bad year buys nothing back
  - L798 ...and bids for nothing
  - L807 a company at its target buys nothing back
- **L809 8. the split**
  - L821 a share last traded at five hundred and twenty is split a hundred for one
  - L822 ...the register's count by the factor
  - L823 ...every household's count by the factor
  - L824 ...the last trade by its inverse: the price
  - L825 ...fair value with it
  - L826 ...and what the households hold is worth what it was
  - L827 the desk's resting bid moves too: its price by the inverse
  - L828 ...its quantity by the factor: still the ten shares' worth it was
  - L829 the split factor remembers it
  - L830 ...so the price per FOUNDING share is what it was: the history has no cliff
  - L838 a share priced at a ten-thousandth is consolidated ten thousand for one
  - L839 ...to a hundred shares
  - L840 ...at a dollar
  - L841 ...the households still agreeing with the register
- **L843 9. the save**
  - L849 it restores
  - L850 ...the last trade, so the price
  - L851 ...fair value
  - L852 ...the desk's resting ask
  - L854 ...and its bid
  - L858 ...and the split factor
  - L859 ...closed until the bank says otherwise
  - L861 ...open once a bank with capital is put back
  - L863 ...and not for a bank without
  - L870 the dealer's save, four a company, still restores
  - L871 ...its last quote the book's last price, so where the market opens
  - L872 ...fair value and the split factor as they were
  - L873 ...and the demand it carried is no order
  - L876 the exchange's first-night save, three a company, still restores
  - L877 ...with one share then being one share now
  - L878 an array of the wrong length is refused whole
- **L880 10. a live city**
  - L930 the city's shares traded on the book
  - L931 ...and every month of it is conserved
  - L934 fixture: the desk's excess over its caps sold in some month
  - L935 ...and the audit closed through every month it did
  - L936 ...with the bank carrying the desk at the exchange's mark all along
  - L937 ...and every price read, on a book that has traded, is its last trade (
  - L945 ...the register and the cells agreeing on every company
  - L946 ...and the desk never short of anything
  - L948 the city saves
  - L953 ...and loads
  - L966 every last trade and fair value comes back
  - L967 ...every book with the orders resting on it
  - L968 ...and the desk's inventory
  - L969 ...at the same mark

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

- **L83 1. the rate is pinned**
  - L87 the exchange rate opens at parity
  - L88 ...so converting to local money changes nothing
  - L89 ...and back again likewise
  - L90 ...and a city with no history owes the world nothing
- **L94 2. a city that trades**
  - L194 a city that never intervened holds its founders' dollars, less the defence's
  - L196 ...and its record is the day-one purchase, less what the defence fetched
  - L199 ...however much it has traded
  - L201 ...so it has cover from the start, as they meant
  - L204 the fixture actually traded with anybody at all
  - L206 ...and imported, which every city does
  - L209 every dollar crossing the edge is domestic OR foreign, never both
  - L211 ...and the reserve is exactly the flows that built it
  - L225 household flows are counted, but not as trade
  - L256 ...and large enough that counting them as trade would swamp the balance
- **L259 3. across a reload**
  - L281 the fixture's position is actually worth carrying
  - L283 the cumulative balance reloads exactly
  - L285 ...and the vault, which is a different number
  - L287 ...in the dollars it is held in, exactly
  - L289 ...and they really are different
  - L291 ...and the trade record with it
  - L292 ...and the import bill the cover is measured against
  - L294 ...and the exchange rate
  - L308 the fixture has an openness worth carrying
  - L309 openness survives the reload
  - L310 ...and the pressure reading with it
  - L312 ...and the absorption
- **L315 4. nothing behaves differently**
  - L344 two runs of the same city agree on its population
  - L345 ...and on its treasury, to the cent
- **L347 5. every unit that leaves the shelf is paid for**
  - L445 the fixture is genuinely short of something
  - L447 ...and the shops actually traded
  - L449 every unit that leaves the shelf is paid for
  - L460 ...so the shops are not importing to replace goods nobody bought
- **L463 5b. reserves you sell are reserves you no longer have**
  - L494 buying reserves actually buys reserves
  - L499 ...and selling them spends them
  - L500 ...for exactly what was asked
  - L519 a reserve stock cannot be sold twice
  - L520 ...and what is left is nothing
  - L521 ...and it ran out when it should have
  - L527 asking for more than the city holds sells what it holds
  - L529 ...and never lends the difference into existence
  - L531 ...and a city with nothing sells nothing
  - L533 ...and the stock never goes negative through selling
  - L553 a treasury that never bought reserves absorbs nothing
  - L555 ...however long it has been trading
  - L559 ...and buying a comfortable buffer is what earns the damping
- **L562 6. the rate is bounded, and moves the right way**
  - L586 a sustained deficit weakens the currency
  - L596 ...and the pressure that did it reads as depreciation pressure
  - L606 a sustained surplus strengthens it
  - L607 ...and reads as appreciation pressure
  - L627 the rate stays a number
  - L628 ...and inside its bounds, every month of the way
  - L637 a pinned rate says it is pinned
  - L638 ...and does not move, however bad the deficit
- **L640 7. and it comes home**
  - L664 the fixture actually moved the rate somewhere
  - L672 balanced trade brings the rate back toward parity
  - L674 ...and most of the way home
- **L677 8. a devaluation improves the current account**
  - L737 the fixture trades enough for the question to mean anything
  - L842 the fixture sells food abroad at all
  - L845 the same programme costs the same in the world's money
  - L858 the fixture has a world price to move at all
  - L859 a 40% devaluation is a 40% rise in what an import costs
- **L884 9. the vault is held in dollars**
  - L913 rX of local money at rate r buys US$X, and US$X is held
  - L915 ...worth rX at home the day it is bought
  - L917 ...X over the dollar import bill, in months of cover
  - L922 the currency halves and the vault still holds US$X
  - L923 ...now worth 2rX at home
  - L924 ...and sellable for 2rX
  - L925 ...and the move is booked as the vault's revaluation: rX
  - L927 ...positive: a falling currency is a dollar vault's gain
  - L929 ...not a flow: the cumulative balance did not move
  - L931 ...nor the flows it is rebuilt from
  - L943 the bill struck at 2r, the cover is what it was at r
  - L948 sold at 2r, the whole vault raises 2rX of cash
  - L949 ...and leaves nothing, not even a division's rounding
  - L951 ...so the treasury is rX ahead: the revaluation it saw
  - L953 ...as the record says: bought for rX, sold for 2rX
  - L955 and the cumulative balance is still the sum of its flows
- **L958 10. and the move is not money anybody moved**
  - L987 fixture: the treasury bought dollars and the currency fell
  - L989 a month the currency falls in leaves the dollars alone
  - L991 ...and books dollars times the move as its revaluation
  - L993 ...and none of it appeared in the audit as money moving
- **L996 11. a reform does not reach the dollars**
  - L1016 lopping two zeros leaves the vault's dollars alone
  - L1018 ...and divides their worth at home by the same hundred
  - L1020 ...so the cover does not move
  - L1022 ...and the next valuation finds no move to book
- **L1025 12. an older save**
  - L1043 slot 19 holds the local value, as an older build reads it
  - L1045 ...and slot 22 its dollars
  - L1049 a save of this shape reloads the dollars exactly
  - L1051 ...and what the currency did to them that month
  - L1054 ...and the first valuation after it books no phantom move
  - L1060 a 0.6.9 save's vault comes back at its saved rate
  - L1062 ...which is the same dollars it held that day
  - L1063 ...worth what the old build said they were worth
  - L1074 a save older than the vault's slot loads it empty
  - L1079 ...and so does a save with no foreign accounts at all
  - L1081 ...with no purchase on its record either
- **L1109 13. the vault defends, it does not hold down**
  - L1125 fixture: the deficit city is pushed weaker
  - L1126 fixture: the surplus city is pushed stronger
  - L1127 fixture: both trade a fraction of their output
  - L1129 fixture: both vaults are deep enough for the most damping
  - L1136 a push weaker is damped by the vault's cover
  - L1139 ...and the absorption it records is what was applied
  - L1145 the same vault passes a push stronger in full
  - L1147 ...and records that it absorbed nothing
  - L1148 ...though its cover could have absorbed the most there is
  - L1159 fixture: the support is half the trade term, unclipped
  - L1162 the rate's support comes off before the vault sees it
  - L1164 ...and the vault damps what is left
  - L1171 fixture: support larger than the trade term turns the push
  - L1173 support outweighing the deficit reaches the rate in full
  - L1190 fixture: the page's push is not the one the month recorded
  - L1193 previewing the push leaves the month's pressure as recorded
  - L1195 ...and its absorption
  - L1196 ...and previews exactly what the month will apply
  - L1198 ...which is the month's own call, and records what it applied
- **L1216 and land bought by converting pushes the rate as reserves bought would**
  - L1230 fixture: the city is pushed weaker
  - L1238 converting pays the parcel's dollars at today's rate
  - L1239 converting for land pushes the rate as buying the dollars for the vault does
  - L1241 ...which is what buying nothing pushes: a treasury's dollars are the financing item
  - L1243 ...and it leaves the vault where it began
  - L1244 ...where the reserve purchase leaves it the parcel's dollars fuller
  - L1246 ...and nets to nothing in the intervention record
  - L1248 ...and books no reserve purchase for the month
  - L1250 ...while the seller was paid the parcel's dollars
  - L1256 ...so what the vault would absorb is the control's, not the reserve buyer's
  - L1261 paid from the vault, the dollars leave it
  - L1262 ...and the record falls by their local price, as a sale's would
  - L1264 ...and a vault asked for more than it holds pays what it holds
  - L1266 ...and is empty
  - L1268 the land's month, struck: every dollar paid for it
  - L1270 ...of which out of the vault
  - L1274 ...and the struck month and the lifetime survive a save
  - L1280 ...and a reform does not reach them: they are dollars
- **L1404 ...AND THE SHOPS AND THE KITCHENS, HELD OUT FOR REAL ESTATE'S OWN**

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

- **L96 1. the instrument speaks two currencies**
  - L103 at parity the two are worth the same
  - L108 the dollars owed do not move
  - L109 ...but what they cost at home does
  - L111 ...and the local bond has not noticed a thing
  - L128 the coupon revalues too
  - L137 ...and every payment still to come
  - L141 a nonsense rate is refused rather than applied
- **L144 2. and the world is cheaper, to begin with**
  - L222 a city with no foreign debt owes no risk premium
  - L224 ...so the world's money starts cheaper than the city's own
  - L226 ...and the window is open
- **L228 3. the books balance with dollars on them**
  - L241 the bond was actually issued
  - L242 ...and the city now owes dollars
  - L255 the bank did not buy a cent of it
  - L279 every month of servicing a dollar bond reconciles
  - L288 ...and the fixture really did service it
- **L291 4. original sin**
  - L299 the fixture has a debt worth revaluing
  - L307 not one dollar more is owed
  - L308 ...and half again as much at home
  - L310 ...which is exactly what the revaluation says it is
  - L338 the fixture is not already against its cap
  - L340 a weaker currency makes the NEXT bond dearer too
  - L352 ...and none of it appeared in the audit as money moving
- **L355 4b. and where the dollars actually went**
  - L402 converting puts the whole proceeds in the treasury
  - L403 ...and leaves the reserve position alone
  - L405 holding puts the whole proceeds in reserves
  - L406 ...and the treasury does not see a cent of it
  - L426 parking borrowed dollars does not read as getting richer
- **L429 5. the window shuts**
  - L434 a city that owes nothing can always borrow
  - L440 ...and one drowning in dollars cannot
  - L441 ...and is told why in words
  - L445 earning its way out reopens the window
  - L450 owing dollars and selling nothing shuts it too
- **L453 6. and the price of walking away**
  - L467 everything owed abroad is written off
  - L468 ...so the city owes nothing abroad
  - L469 ...and the position improves by the whole of it
  - L471 ...recorded as what it is: money nobody was paid
  - L474 the window is shut
  - L475 ...for five years
  - L477 ...and the price carries a scar
  - L489 debt-to-exports now says the city is spotless
  - L491 ...and it still cannot borrow a dollar
  - L502 ...and says so when asked
- **L504 7. across a reload**
  - L548 the fixture has foreign paper worth carrying
  - L550 the dollars owed reload exactly
  - L564 ...valued at the rate the city actually has, immediately
  - L567 ...and the exchange rate reached the instruments
  - L570 the scar reloads
  - L572 ...and it is a scar worth reloading
  - L577 fixture: the live city's window is shut BY THE DEFAULT CLOCK
  - L580 ...so the window is still shut on the reloaded city
  - L582 ...and shut for the same reason, not a different one
  - L585 the lifetime revaluation reloads
  - L587 ...and what the city walked away from
  - L606 a reloaded city does not book its whole history as one month
- **L618 and the world's paper is priced on the world's curve**
  - L628 fixture: the world will lend
  - L639 fixture: a dollar twenty-year on the books
  - L647 the world's curve is the foreign rate plus the city's own term premium table
  - L649 ...one shape for both currencies, fifty years over ten as on the city's curve
  - L652 a dollar twenty-year is valued on the world's curve
  - L654 ...not at the city's own rate, which is a different figure
  - L656 ...and it was issued at the rate it is valued at: the round trip is neutral by construction
  - L663 fixture: the dial moved the city's own rate
  - L664 the dial moving does not move a dollar bond's value
  - L671 fixture: a city earning a quarter as much abroad is charged more for it
  - L673 ...and the world charging the city more moves it: down
  - L676 ...and back when the city's standing is

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
  - L379 fixture: the city really did issue a coupon bond
  - L380 THE INTEREST REACHES THE ACCOUNTS AT ALL
  - L382 ...and it is the coupon, not some other number
  - L384 ...so it survives budgetPie(), which drops a zero slice
  - L395 fixture: saved
  - L396 and a reloaded city reports the same interest

## HealthCheck.java - 233 labelled assertions

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
  - L487 clinics gave the city some coverage
  - L488 ...so it is healthier than an untreated one
  - L490 ...but not perfectly healthy
  - L497 the sick rate came back
  - L499 ...and the outbreak with it
  - L501 ...and the coverage the month was priced at
  - L510 and the sectors were told about it on the load path
  - L517 ...including the statement it struck under it
- **L521 6. an unstaffed hospital treats nobody**
  - L536 fully staffed, a hospital treats its whole capacity
  - L543 with nobody at all, only the founding doctor is left
  - L554 ...and losing only its doctors costs it exactly their share of the posts
- **L561 7. what care does to mortality**
  - L571 half-covered is exactly today's rate:
  - L575 no childcare at all
  - L577 childcare for everybody
  - L586 no general care at all leaves the adults' rate alone
  - L588 ...and so does general care for everybody
  - L590 ...and the teenagers' the same
  - L592 no senior care at all
  - L599 children are the drastic ones
  - L601 ...and seniors are the gentlest
  - L605 general care does not also treat babies
  - L607 ...nor seniors
  - L613 
  - L618 no childcare, no bonus
  - L619 childcare for everybody doubles it
  - L640 ...and the pyramid gets that many more babies
  - L661 a city with childcare loses far fewer infants
  - L663 ...and has far more of them
  - L671 a cared-for city keeps more of its babies
  - L673 ...and more of its seniors
- **L676 8. death care**
  - L684 with savings and plots, everybody is buried
  - L685 ...and nobody is cremated
  - L686 ...and the plots are gone for good
  - L687 ...and the city collected the burial fee
  - L693 with no savings, everybody is cremated
  - L694 ...and the ground is untouched
  - L695 ...which is the cheaper funeral
  - L701 a full cemetery sends the rest to the oven
  - L702 ...which takes them
  - L703 ...and nobody is left waiting
  - L707 a busy crematorium sends the rest to the ground
  - L709 ...even though nobody could afford a plot
  - L710 ...and nobody is left waiting
  - L715 with neither, they all wait
  - L716 ...and nothing was collected
  - L720 a new cemetery clears the backlog and the month together
  - L722 ...leaving nobody
  - L727 a city that never builds one stops counting after two years
- **L731 and it makes people ill**
  - L738 leaving them where they fell costs output
  - L739 ...by the weight it claims
  - L744 however many there are, it is capped
- **L747 9. senior care draws people in**
  - L750 no senior care, no bonus
  - L751 full coverage, the full draw
  - L764 senior care raises the target by exactly the pull
- **L768 10. and somebody pays for all of it**
  - L810 the service costs something
  - L811 ...most of which is wages
  - L827 ...and it is a NET DEFICIT business, per the spec
  - L829 ...with fees nowhere near funding it
  - L832 the treasury is billed for it
  - L834 ...and it is on the city's expenditure list
  - L836 ...and the fees are on its revenue list
  - L844 and the households paid exactly what the city collected a month ago
  - L846 ...which is not the same as this month's, so the test means something
  - L853 ...and the seven tiers add back up to it
  - L857 healthcare is counted as government output
  - L868 a bigger bill is a smaller surplus, penny for penny
  - L880 the graves came back
  - L882 ...and the backlog
  - L884 ...and the bill the city was paying
  - L887 a save from before healthcare had books is refused whole
- **L890 11. a skip cannot hide an epidemic**
- **L898 a skip reports what it lived through**
  - L914 the skip noticed the epidemics
  - L915 ...and counted every month of them
  - L917 ...and kept the worst month, which the endpoints cannot show
  - L919 an untreated city is below full every single month
  - L924 a healthy month reports no outbreak
  - L925 ...and nothing left unburied
- **L927 12. the fee has a dial, and the funerals do not**
- **L936 the price at the door: the fee scale**
  - L940 the scale multiplies general care's fee
  - L942 ...and childcare's
  - L944 ...and senior care's
  - L946 ...and NOT the burial fee
  - L947 ...nor the cremation fee
  - L948 the founding fee is still the founding fee, unscaled
  - L951 the dial stops at its ceiling
  - L953 ...and at nothing
  - L955 a new city charges the founding fee
  - L957 ...and no premium
  - L959 the policy clamps the scale to the same ceiling
  - L962 ...and the premium to its own
  - L976 at 0 nobody pays for treatment
  - L977 ...and everybody is still treated
  - L978 ...and the funerals still charge
  - L979 ...so the fees are the funerals alone
  - L980 ...and nobody was priced out by a fee of nothing
  - L992 the break-even scale is the gross cost over the fees at 1x
  - L993 ...and this ward's is inside the dial
  - L996 at the break-even scale the fees meet the gross cost
  - L998 ...and the net cost is nothing
  - L1001 below it the service loses money
  - L1002 ...half the fees, at half the scale
  - L1005 above it, it is a business
  - L1006 ...and the recovery rate says so
  - L1015 fixture: the city's break-even is inside the dial
  - L1020 the policy's scale reached the service
  - L1021 at the city's break-even the fees at full service are within a month's drift of the cost
  - L1026 ...at half of it the service loses money
  - L1027 ...by about half the cost
- **L1037 13. who can afford the clinic**
- **L1050 who can afford the clinic**
  - L1062 a household with room pays its whole care bill
  - L1064 ...and skips nothing
  - L1068 ...and so does one whose room is exactly the bill
  - L1076 a household short of a basket pays only what fits after eating
  - L1078 ...which is the share of its people the clinic will see
  - L1079 ...and the rest of the bill is what it eats instead
  - L1081 ...so the bill it does pay leaves the basket whole
  - L1087 a household with nothing pays nothing
  - L1088 ...and none of its people are served
  - L1089 ...whatever the fee
  - L1091 ...and with a fee of nothing it is served in full
  - L1100 a household that skipped last month's bill is judged on the full one
  - L1105 ...and settles where it pays what it can, month after month
  - L1108 ...rather than swinging between served and starving
  - L1247 fixture: the poor city has somebody at the eat-less step
  - L1249 fixture: at the dial's top the fee priced somebody out
  - L1251 at a high fee a poor city serves a smaller share of its people than at the founding fee
  - L1277 fixture: the same clinics in every twin, a place for more people than any of them holds
  - L1279 fixture: so the beds are not what differs: at the dial's top they take no larger a share by more than the fee turns away
  - L1281 ...and the coverage the month reads, what the fee leaves of the beds, is lower at the dial's top
  - L1283 ...and its baseline sick rate, which coverage sets, is higher for it
  - L1305 ...and it buries more of its people over the run
  - L1306 ...its old first, whom a fee on senior care turns away
  - L1307 with the fee at nothing the same city is served in full, every month
  - L1309 ...every kind of care
  - L1312 ...and the households who skipped a bill ate with it: over the last year the dear city is no hungrier than the free one by more than the price of care
  - L1318 
  - L1320 ...and the served are the offered times the share who could pay
  - L1328 the treatment fees are charged on the people treated
  - L1329 ...which is less than the same beds would raise at full service
  - L1337 the buildings' upkeep is the same at a dear fee as at the founding fee
  - L1339 ...and the service still costs money to run
- **L1342 14. the unchanged case, at zero tolerance**
- **L1354 the unchanged case, at zero tolerance**
  - L1367 in a city that can pay, every household paid its whole care bill
  - L1369 
  - L1371 ...and the served are the offered, exactly
  - L1374 ...and the fees charged are the fees at full service, exactly
  - L1376 ...and the households were billed exactly what the city collected a month ago, as before
  - L1378 ...and nobody was priced out
  - L1379 ...and no bill was skipped
- **L1381 15. the premium**
- **L1389 the premium**
  - L1397 the premium raises exactly the rate times the wage bill
  - L1399 fixture: which is money
  - L1400 ...on the same base as the EI premium
  - L1402 ...and it is on the government's revenue list
  - L1405 ...inside the revenue total
  - L1409 ...and reaches the treasury's cash, penny for penny
  - L1417 the households' statement shows the premium the city collected a month ago
  - L1423 ...and the rows add back up to it
  - L1424 ...off the wages, so the retired pay none
  - L1426 ...and it comes off take-home, like the EI premium
  - L1432 the money audit saw it as a household-to-treasury flow
  - L1459 
  - L1462 the insured city charged no treatment fee
  - L1463 ...and the fee-funded one did
  - L1464 the insured city's wage earners paid a premium
  - L1466 ...and the fee-funded city's paid none
  - L1467 the gross cost is the same either way: a ward is paid for whether or not its patients are
- **L1470 16. both dials survive a save, and a reform**
  - L1480 the fee scale came back
  - L1481 ...and the premium
  - L1482 ...and the service charges at the reloaded scale
  - L1483 ...and the full-service bill the next strike reads came back
  - L1491 a save from before the dials is still read
  - L1492 ...at the founding fee
  - L1493 ...with no premium
  - L1496 ...and so is the service's state from before the full-service bill
  - L1498 ...which reads the bill it charged as the bill at full service
  - L1500 ...and a coverage of 1 until a month strikes it
  - L1505 ...and the state from before the coverages were kept
  - L1506 ...which reads the full bill at 1x it carried
  - L1507 the reloaded service kept the coverage the month read
  - L1509 ...which is the figure the sick rate read, not the beds
  - L1516 a currency reform leaves the fee scale alone
  - L1517 ...and the premium
  - L1521 ...and the service's scale
  - L1522 ...while its fees move with the money

## HistoryCheck.java - 56 labelled assertions

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
- **L260 2d. the dial and the bank's three prices (0.7.7)**
- **L270 the dial and the bank's three prices**
  - L277 the last month's policyRate is the dial
  - L279 ...its bankPrime is Bank.prime() at that dial
  - L281 ...its bankDepositRate is what savers were paid
  - L283 ...and its bankFees is the month's fee income, to the cent
  - L293 prime is never under the dial, in any month
  - L294 ...and the founding bank charged a fee in some month
- **L296 2e. the bank's capital (0.7.8)**
- **L306 the bank's capital**
  - L313 the last month's bankCapitalRatio is Bank.capitalRatio(), clamped at ten
  - L316 ...its bankCapitalTarget is the target the bank chose
  - L318 ...its bankAllowance is what it has set aside, to the cent
  - L320 ...its bankProvisions is the month's provision, to the cent
  - L322 ...its bankDividends is what it paid its owners, to the cent
  - L324 ...and its bankReturnOnEquity is the month's return, a year, clamped at ten
  - L333 fixture: the founding bank has lived through no bad year
  - L334 ...so it targets the minimum and the conservation buffer in every month
- **L336 3. A SHORT SERIES LINES UP WITH THE END**
- **L351 a series added late lines up with the END**
  - L359 the fixture actually removed the series
  - L370 the axis is untouched by the missing series
  - L372 ...and the missing series is empty, not absent
  - L377 aligned() still returns a full-length array
  - L388 ...reading as NaN - not-recorded is not the same as zero
  - L399 only the new months have the series
  - L402 aligned() covers the whole axis
  - L405 month
  - L412 ...and the LAST four months carry the data
- **L414 3b. a 0.7.6 history still loads (0.7.7)**
- **L422 a history from before the premium went**
  - L436 the fixture has the old series and none of the four new ones
  - L444 the old history loads with its axis whole
  - L445 ...the premium it carried is not a series any more
  - L447 ...and the bank's prices are empty, not absent
  - L451 ...and fill from the end once the city plays on
  - L455 ...and so do the bank's capital series (0.7.8), empty until then
- **L462 4. the derived series do not divide by zero**
- **L469 derived series on a city with nobody in it**
  - L482 month
- **L486 5. a new game forgets it**
  - L490 the axis is empty
  - L493 

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
  - L339 fixture: the save carries the cells
  - L341 fixture: at today's width
  - L366 it loads
  - L367 the households hold none of the city's paper
  - L368 ...nor the central bank
  - L369 ...so the bank holds everything
  - L371 ...and its book says so
  - L372 the dial reads nothing
  - L375 ...and it runs, the audit closing every month
  - L376 ...with the two books agreeing
- **L378 8. a dollar bond bought back**
  - L383 fixture: the treasury sold a twenty-year dollar bond
  - L385 fixture: ...still owed a month on
  - L391 fixture: bought back at the quoted price
  - L392 fixture: ...which cost something
  - L393 the treasury paid it
  - L394 ...and owes the world nothing on it
  - L395 none of the price went to the bank
  - L396 ...nor to the households
  - L397 the treasury's pool carries it until the month declares it
  - L400 the next month declares the whole price leaving the country
  - L402 ...so nothing is carried any more
  - L403 the audit closes on it

## HouseholdCheck.java - 270 labelled assertions

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
  - L645 ...and with nothing owed its line is priced at the bank's household rate, no more
  - L653 the savings run out
  - L654 ...THEN THEY BORROW
  - L655 ...at a rate over the risk-free one
  - L657 ...which climbs with what they already owe
  - L659 ...and never past the cap
  - L685 the debt stops at the ceiling, it does not run away
  - L691 ...AND THEN THEY EAT LESS
  - L692 ...in most months, not just on average
  - L693 ...which the health service can see
- **L695 and a household with money to spare pays it down, then banks it**
  - L704 a household with a surplus banks it
  - L705 ...and owes nothing
  - L706 ...and is not hungry
  - L714 a rich household WANTS more than a basket
  - L716 ...but a poor one wants exactly a basket, not less
- **L719 the supply side starves people who had the money**
  - L727 empty shelves are hunger even in a rich city
- **L730 SIXTY-EIGHT CELLS, AND THE MONEY FOLLOWS THE PEOPLE**
- **L742 every cell has its own books, and they sum to the rows**
  - L773 seventy-eight cells: eleven working shapes by six tiers, four retired, eight outside the families
  - L779 a single adult takes home one wage
  - L780 a couple takes home two
  - L781 a large family, two earners, takes home two
  - L782 a pensioner draws the pension
  - L783 a working cell is a WorkingHousehold
  - L784 a retired cell is a RetiredHousehold, with no tier
  - L786 the cells' take-home sums to the row's
  - L789 the buffer is per household of the CELL, not of the tier
  - L791 ...so a single adult opens with half a couple's
  - L793 the row's per-household figure is the cells' weighted average
  - L796 sum() adds up every cell
  - L798 ...and the households in every cell are the city's
  - L803 forEach touches every cell and changes nothing at scale one
- **L807 the money follows the people**
  - L827 fifty new families arrived with the buffer
  - L835 the child's cell is empty
  - L836 ...the teen's cell holds them
  - L837 ...WITH THEIR MONEY: the teen's family opened with what the child's had
  - L839 nothing was written off for a birthday
  - L840 ...and nothing left the city
  - L855 five singles sharing carry FIVE wallets into the flatshare
  - L857 ...and the singles who stayed have what they had
  - L859 nothing was written off for a lease
  - L860 ...and nothing left the city
  - L875 a couple promoted to skilled brings its savings up the ladder
  - L877 ...and nothing left the city
  - L901 the fixture saved something - or the next line proves nothing
  - L906 twenty households leaving take twenty households' savings with them
  - L908 ...and nobody wrote anything off, because they owed nothing
  - L910 ...and the ones who stayed opened the month with what they had
  - L920 the fixture reached a debt - or the next line proves nothing
  - L926 twenty households leaving in debt write it off against the bank
  - L930 ...and the ones who stayed still owe what they owed, plus the month
  - L947 fifty arrivals owe nothing: the debt is the old total, less what the month repaid
  - L949 ...and the per-household debt is diluted by them
  - L951 ...and they brought the buffer: the old savings plus fifty buffers
  - L984 the large family, short every month, discharges
  - L985 ...and is locked out when it does
  - L986 the single adult next door never borrowed
  - L987 ...and was never locked out
  - L988 the row reads the lockout because ONE cell has it
  - L1010 the shares move with the people, like the savings
  - L1012 ...and none left the city
  - L1013 ...nor were taken away
  - L1014 the dollars abroad move with them too
  - L1016 ...all of them
  - L1017 ...and none left with anybody
  - L1020 the cells restore by name
  - L1031 ...every cell, to the cent, shares and dollars included
  - L1040 a save from before the dollars abroad restores
  - L1041 ...with the shares
  - L1042 ...and no dollars
  - L1051 a save from before the shares restores
  - L1052 ...with the savings
  - L1053 ...and no shares
  - L1054 ...and the city's stock with them
  - L1055 a save with a key this build does not know is refused whole
  - L1060 a row-only save seeds the row's cells with the row's position
  - L1063 ...and the row totals survive the seeding
- **L1065 A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2)**
- **L1077 a cell under half a household is empty, and what it held stays**
  - L1096 fixture: a cell of .3 households holds a position
  - L1099 the cell under half a household holds nothing
  - L1100 ...and its count stands: the census's, not ours to round
  - L1101 one cell had something to fold
  - L1103 ...and the households hold all their
  - L1106 into its own row first: the unskilled couples hold the single's savings
  - L1108 ...and the skilled couples, a row away, none of it
  - L1109 a stock nobody in the row held goes by the row's households
  - L1111 ...and none of it across the row's edge
  - L1122 a cell alone in its row is emptied too
  - L1123 ...into the city, every saver's savings up by 1 in 703
  - L1125 ...the unskilled couples' too
  - L1126 ...and the households hold all their savings
  - L1153 a cell the census leaves at .3 of a household holds nothing after the month
  - L1155 ...and the month counted it
  - L1156 the shares: the households' now, plus what left with the 99.7
  - L1158 ...and the .3's are the couples'
  - L1159 the dollars abroad the same way
  - L1174 the proceeds are all credited
  - L1175 ...all of them to the households who are there
  - L1177 ...and none to a cell of 1e-15 households
- **L1179 AND WHEN THEY CANNOT AFFORD A HOME, THEY SHARE**
- **L1185 a tier priced out of living alone shares instead**
  - L1190 nobody shares when everybody can afford a home
  - L1196 ...and a model with no households still forms none
  - L1199 somebody always holds out, however dear the rent
  - L1221 no household model, no pressure
- **L1279 THIS CITY IS NO LONGER POOR, AND THAT IS NOT A FAILURE (2026-09-09).**
  - L1310 ...so some of them are sharing
  - L1312 A FLATSHARE IS BETTER OFF THAN LIVING ALONE
  - L1314 ...because five of them pay one rent, not five
  - L1316 ...and still five baskets
- **L1319 A HOME IS A SIZE, AND A HOUSEHOLD HAS TO FIT**
- **L1329 a home is a size, and a household has to fit**
  - L1341 a studio flat takes two
  - L1342 a house takes four
  - L1343 a low-rise flat takes three
  - L1344 ...and only the studio refuses children
  - L1354 an empty city needs no homes
  - L1363 a two-person flat bills two
  - L1364 a six-person house bills six
  - L1365 ...so a bigger home is a dearer one
  - L1369 and a flat nobody in the city could live in bills nothing
- **L1372 and the two pension dials are the player's**
  - L1374 the contribution starts at the real CPP rate
  - L1377 ...and moves
  - L1379 ...but not past the ceiling
  - L1382 a richer pension is a bigger cheque
  - L1386 the dials survive a save
  - L1387 ...the contribution
  - L1388 ...and the pension
  - L1389 and never negative when contributions overshoot
  - L1396 contributions come off take-home
  - L1398 ...and the pension goes on
  - L1418 the workers pay all the contributions
  - L1420 ...and the pensioners pay none
  - L1422 the pension goes entirely to the retired
  - L1424 ...and nowhere else
  - L1426 the retired row now has an income at all
- **L1457 what a household spends above subsistence answers the real deposit rate**
  - L1491 fixture: income past the basket, a net worth, and a surplus left over
  - L1495 at no real return the factor is one
  - L1497 ...and the plan is what it was: the grocer's
  - L1498 ...the counter's
  - L1499 ...and the table's
  - L1506 ...the part above subsistence is exactly that share of it
  - L1508 ...and subsistence does not move
  - L1509 ...the counter moves with the surplus, by the same share
  - L1510 ...and so does the table
  - L1513 fixture: eighty points either way is past the floor and the ceiling
  - L1516 at +80 points the floor binds
  - L1518 ...and the plan keeps SPEND_FLOOR of what is above subsistence
  - L1520 at -80 points the ceiling binds
  - L1522 ...and the plan asks SPEND_CEILING of it
  - L1524 a rate that is not a number is no return at all
  - L1539 fixture: short of a basket after the rent, with nothing saved
  - L1544 a household at subsistence plans the same at any rate
  - L1545 ...a basket and no more
  - L1546 ...and can fund the same
  - L1561 fixture: two balances in lockstep
  - L1566 a balance nobody hands a factor plans at one
  - L1567 the one handed a factor plans at it
  - L1568 ...every cell asking that share of what is above subsistence
  - L1570 ...so the shops are told of less to sell
  - L1574 ...and the load path's re-strike plans at the same factor
  - L1599 a temporary directory for the city:
  - L1609 the page's real deposit rate is the deposit rate less the year's inflation
  - L1611 ...and its factor is the rule's on it
  - L1612 fixture: saving pays, so the factor is under one
  - L1620 ...and it is the factor the next month's plan was struck at
  - L1622 every month of it passed the money audit

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
  - L157 a brand new city is not congested
  - L159 ...its roads carry everything
  - L192 a starter city still has road to spare
  - L194 ...and loses nothing to traffic
- **L196 5. growth jams it**
  - L217 a grown city outruns the network it was given
  - L219 ...and the load really is above free flow
  - L221 ...it says Congested
  - L244 the builders are slowed by it too
  - L246 ...by exactly the throughput ratio
  - L250 the shops feel it
- **L253 6. building a road fixes it**
  - L258 roads are a building the city can order
  - L260 ...that costs materials, so construction earns from it
  - L262 ...and takes months, so it cannot be a panic button
  - L264 ...and adds capacity when it is done
  - L266 a road generates no traffic of its own
  - L268 ...and employs nobody to run it
  - L280 the congested city saved
  - L319 the order goes through
  - L323 ordering one changes nothing yet
  - L338 the finished roads added capacity
  - L340 ...and cleared the jam
  - L341 ...while the city that built nothing is still stuck
- **L385 AND THE CLEANER SIGNAL UNDERNEATH IT, WHICH IS NOT CONSUMPTION.**
  - L420 ...and its shops can actually be supplied
- **L422 AND THERE IS NO SECOND OUTCOME ASSERTION HERE, ON PURPOSE.**
- **L460 7. across a save**
  - L494 the test city is still growing, which is the hard case
  - L496 the test city is genuinely congested
  - L505 saved
  - L510 it loaded
  - L511 the network came back
  - L513 ...at the same capacity
  - L516 ...and the same throughput
  - L523 the ratio the sectors were handed came back
  - L525 ...so retail revenue is unchanged
  - L528 ...and so is the month in progress
  - L542 ...and so is next month's income
  - L544 ...and the industrial statement, which was the last to drift
  - L548 the basis is NOT just the current ratio, or this proved nothing
- **L551 7b. THREE ROADS, AND ALL THREE USEFUL**
- **L569 three roads, and each of them wins somewhere**
  - L609 the gravel road is the cheapest to build per trip
  - L611 ...and the hungriest for ground per trip
  - L613 the elevated highway draws the most power per trip
  - L616 ...and the gravel road the least
  - L664 all three roads win a band, not two of them
- **L667 8. a new game forgets the traffic**
  - L671 capacity is back to the base
  - L674 nothing is on the roads
  - L675 ...and throughput is whole again
- **L677 9. EVERY FIGURE THE INFRASTRUCTURE TAB READS**
- **L697 every figure the Infrastructure tab reads is a number**
  - L764 ...in
  - L783 every good's wedge is a share of its own price
  - L793 every sector's fleet reading is a number

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

- **L1110 AND NOTHING MOVED AFTER THE AUDIT STRUCK.**
- **L2217 THE TWO THINGS THAT ARE NOT PURCHASES, done first and for free.**
- **L2272 AND EVERYTHING THAT IS A PURCHASE.**
- **L2561 AND THE BEST OF THEM WINS.**
- **L3302 founding: a few months at a time, by hand**
- **L3392 then the real rhythm**
- **L3539 the report**
- **L3547 ==**
- **L3633 BUSINESS SERVICES - and the point of printing it is the MECHANISM,**
- **L4503 what the advisor tried, and what happened**
- **L4509 findings**

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
  - L384 two shops is 2,400 tonnes of fabricating capacity
  - L386 ...and one machine works is 180 of machinery
  - L388 it sold something
  - L389 ...and every tonne of it left the city
  - L390 everything it sold, it exported
  - L392 it bought steel to do it
  - L395 the two together are under one, or it would be shedding
  - L415 dear steel makes another shop worth less
  - L416 ...far less: it is most of what a shop spends
  - L417 ...while the machine works is still worth building
  - L418 ...which is the whole point of having both
  - L435 quadrupling the wage floor makes another machine works worth less
  - L437 ...and it is the machine works that gives way first
- **L441 6. the books, the audit, and a reload**
  - L464 every sector's statement still foots
  - L465 and the money identity holds with a ninth sector in it
  - L475 the mills sold steel at home for the first time in this game's history
  - L477 ...and got more than the ship would have paid
  - L479 ...and less than the fabricator would have paid the world
  - L481 the fabricators bought from them
  - L483 Manufacturing is the ninth sector
  - L485 ...and the share register is the sectors and the bank
  - L497 a reloaded city has the same fabricating capacity
  - L500 ...the same machinery capacity
  - L502 ...the same cash
  - L505 ...and the same steel price, restored rather than recomputed

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
  - L321 the city based its basket
  - L322 ...and the world's prices moved
  - L324 ...and the currency is not against a bound
  - L343 the policy rate reloads
  - L345 ...and the world's price level
  - L347 ...and the price index
  - L349 ...and the basket it is measured on
  - L358 ...and the year of history the inflation rate is struck from
  - L365 ...and the inflation target, the player's dial
  - L370 fixture: the save carried the target under its own key
  - L382 a save from before the dial reads the default: 2%, the constant it was
- **L557 inflation falls with the rate**
  - L618 the runs differ by the dial and by nothing else
  - L620 ...and a month's delay in the hand moves them less than the allowance

## MoneyCheck.java - 13 labelled assertions

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
  - L94 a founding city conserves money to within 0.01% of what moved
- **L113 AND A BUS, WHICH IS THE WHOLE REASON THIS LINE EXISTS (2026-09-16).**
  - L134 an industrial city conserves money to within 0.01% of what moved
  - L136 ...and it really did carry passengers, so the fare was really charged
- **L140 AND LAND PAID FOR OUT OF THE VAULT (0.7.6).**
- **L150 land bought out of the vault: nothing moves the audit cannot see**
  - L160 fixture: the vault paid for a parcel and the treasury's cash did not move
  - L164 a city that paid for land out of the vault conserves money
  - L166 ...and nothing moved a pool after any month's audit
- **L169 2. a city under stress**
  - L196 a stressed city conserves money to within 0.01% of what moved
  - L220 fixture: retail went under whole, and was restructured
  - L222 a restructure moves no cash the audit cannot see
- **L225 AND NOTHING MOVES AFTER THE AUDIT HAS STRUCK.**
- **L243 a city paying over the world: the money that arrives is audited**
  - L264 fixture: the rate actually brought money in
  - L266 ...and the audit saw it cross the border
  - L269 a city conserves money with hot money flowing
  - L272 ...and nothing moved after the audit struck

## MortgageCheck.java - 186 labelled assertions

> The landlords' insured mortgages (0.7.11): the instrument, the lender's
> tests, the city's insurance and the bank's book.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Mortgages", "CMHC (Canada)",
> "Keep it", "Insured by the city"):
> 
>   1. THE ANNUITY: the payment is the level annuity; the balance after k
>      payments is the closed form; interest and principal add up to the
>      payment every month; nothing is owed after
>      Mortgage.MORTGAGE_AMORTIZATION_MONTHS.
>   2. ORIGINATION: the loan is the cost less what the landlord put in, and it
>      put in at least 1 - MORTGAGE_MAX_LOAN_TO_COST; the premium is CMHC's
> ...

- **L174 1. the annuity: a level payment, the closed form, and nothing left at the end**
  - L181 the payment is B r / (1 - (1 + r)^-n), r a twelfth of the rate
  - L182 ...which Mortgage.payment() gives for any balance, rate and term
  - L203 ...interest is the balance at a twelfth of the rate, month
  - L225 interest plus principal is the payment, every month
  - L226 ...and the payment is level, a renewal at the same rate included
  - L227 the balance after k payments is the closed form, B(1+r)^k - A((1+r)^k - 1)/r
  - L228 ...which Mortgage.balanceAfter() gives
  - L230 after MORTGAGE_AMORTIZATION_MONTHS payments nothing is owed
  - L231 ...every dollar of principal came back through the settle
  - L232 ...and the mortgage is closed and off the books
  - L233 ...the payments were the annuity n times over
- **L239 2. origination: the loan is the cost less the landlord's funds, the premium on top**
  - L248 the lender writes it with the least till the down payment allows
  - L255 the landlord is handed the loan less its fee - the shortfall, to the cent
  - L256 ...so its till pays the building and the fee and ends at nothing
  - L257 the loan is the cost less what the landlord put in
  - L258 ...and it put in 1 - MORTGAGE_MAX_LOAN_TO_COST of the cost, the least it may
  - L260 the premium is the schedule's rate: 5.00% and 0.25% for each five years past 25
  - L262 ...which is PREMIUM_RATE and three surcharges at forty years
  - L264 ...of the loan
  - L265 ...added to the principal
  - L266 the bank lends the principal
  - L267 ...keeps Bank.LOAN_FEE of it
  - L268 ...and the premium is the month's, for the treasury
  - L269 it is written at the insured rate
  - L270 ...insured, for MORTGAGE_TERM_MONTHS, over MORTGAGE_AMORTIZATION_MONTHS
  - L273 a cent less of its own and the lender refuses it: the down payment
- **L277 2. ...and a played month that writes one: the treasury takes the premium, and it closes**
  - L285 fixture: the landlords wrote a mortgage in a played month
  - L296 it was written at the month's insured rate, the bank's pushed in with prime
  - L300 the treasury's revenue line is the premiums written that month
  - L302 ...and the budget's revenue carries it
  - L305 the loan was no more than MORTGAGE_MAX_LOAN_TO_COST of what the building cost
  - L307 the month's money audit closes
  - L308 ...and the landlords' cash-flow statement, handed the loan less its fee and not the premium
- **L315 3. the down payment: short of it on one, the landlord holds, and says so**
  - L321 a till a cent short of the down payment on one buys none
  - L322 ...and says what it needs
  - L327 a till in overdraft covers nothing
  - L329 a till that puts the down payment on three of five orders three
  - L331 ...and says why it was trimmed
  - L335 a till that covers the order buys it outright, with no lender to ask
- **L337 3. ...and in a played city, a landlord short of it holds, in those words**
  - L362 ...no mortgage is written in a month it holds for the down payment, month
  - L367 fixture: the landlords did hold for the down payment
  - L368 ...and the advisor said what it needed for the down payment
- **L377 4. the lender's test at its line: MORTGAGE_DEBT_COVERAGE passes, a hair under does not**
  - L384 the payment the test reads is on the principal with the premium, over the amortization
  - L389 a building whose income covers the payment MORTGAGE_DEBT_COVERAGE times is financed
  - L390 ...and one a hair under is declined, not for its down payment
  - L393 ...in the lender's words
  - L397 the test is on the order: five that fail it are trimmed until they pass, or dropped
- **L404 5. fixed for the term: the dial moves nothing until the term ends**
  - L413 the dial went up eight points and the payment did not move
  - L414 ...nor the rate it pays
  - L416 ...to the last month of the term
  - L419 at MORTGAGE_TERM_MONTHS it renews at the day's rate
  - L420 ...for another term, once
  - L422 ...with what is left of the amortization, 360 months
  - L424 ...and the payment recomputed on the balance over those months
  - L426 ...which is dearer
- **L428 5. ...and a renewal the lender cannot write falls due, as a loan would**
  - L437 a failed bank renews nothing: what it owed falls due with the term's last payment
  - L439 ...and the mortgage is gone
- **L441 5. ...and the rent floor reads a mortgage's interest, and not its principal**
  - L449 a landlord's interest line is its mortgage's on the balance and its loan's on the face
  - L452 ...which leaves out the principal each payment repays
  - L460 fixture: the landlords owe a mortgage and are paying it down
  - L463 the break-even is maintenance, property tax and the interest line over what it owns
  - L465 ...and the principal it repaid this month is not in it
- **L472 6. insurance: a landlord's slice - its debt falls, the city pays the bank the insured part**
  - L480 fixture: the landlords owe insured mortgages
  - L499 fixture: its firms defaulted a slice, insured mortgages among them
  - L502 every bank loan fell pro rata: the insured share of the write-off is their share of the loans a slice takes
  - L507 the insurer's claim is h x the insured balance x (1 - LOAN_RECOVERY), a first-lien loan's loss
  - L509 the bank books only what nobody insured as its loss
  - L515 ...so its month's write-offs carry none of the insured part
  - L516 the treasury paid the bank exactly the written-down insured balance
  - L517 ...on its claims line
  - L518 the bank's equity moved by its income, its named causes and the fixture's own loan, and nothing else
  - L520 the month's money audit closes
  - L521 a slice is not on the landlord's record
- **L523 6. ...and the backstop: a landlord with nothing left is written down whole**
  - L531 fixture: the landlord went under whole
  - L532 its debt is gone
  - L533 ...every insured balance claimed - what was left after the month's payments
  - L535 the bank booked what nobody insured
  - L536 the treasury paid the bank the insured balance, on its claims line
  - L538 ...cash the bank received
  - L539 the default is still the landlord's: on its record, and banned
  - L541 the bank's equity moved by its income and its named causes
  - L542 the month's money audit closes
  - L543 over the city's life the claims grew by what the two write-downs took off insured mortgages
- **L550 7. the bank: an insured mortgage weighs nothing and carries no allowance**
  - L564 the weight table has the insured mortgages as their own row, at face
  - L565 ...at RISK_INSURED_MORTGAGE
  - L566 ...which is the sovereign's nothing (Basel III, CRE20)
  - L567 ...so they weigh nothing
  - L568 ...the businesses' row is the rest of what they owe
  - L569 ...and the table still foots to the weighted book
  - L572 an all-insured book holds no allowance while sound
  - L574 ...nor in stage 2, past the watch line
  - L575 fixture: that borrower really is in stage 2
  - L576 ...and a mixed book holds the uninsured part's loss, at the leverage of the whole
- **L589 7. ...priced with no loss and with the capital its leverage requirement ties up, the ladder's rung**
  - L594 a dollar of it ties up the leverage minimum scaled by the bank's own buffer
  - L597 ...and a business loan what it always did: the target on its whole weight
  - L605 ...and the rate is the ten-year funds-transfer price, running the bank and that
  - L608 ...the ladder's rung is it
  - L609 ...its money is the policy rate, the window's share and the ten-year term premium
  - L612 ...the capital its own part of the rung
  - L613 ...and over its money the running costs and the capital, no loss
  - L616 fixture: at the dials under the owners' return the part is a few tenths of a point
  - L619 ...and past it nothing: the owners' money would be the cheaper money
  - L621 the played bank carries the insured mortgages as its mortgage book
  - L625 ...and its weight table foots
  - L627 the landlords' allowance is struck on what nobody insures, at the curve of all they owe
- **L634 7. ...and the capital rule rations it only when the leverage ratio binds**
  - L641 fixture: a bank under its minimum refuses a project loan
  - L642 ...and writes the insured mortgage
  - L645 ...which leaves the room its uninsured borrowing has where it was
  - L649 a bank under its leverage minimum refuses the insured mortgage too
  - L651 ...for its capital
  - L653 ...and short of its target it lets the whole debt grow at the rule's rate, the mortgages counted
  - L656 ...where on the risk weights it counts only what nobody insures
  - L664 fixture: that borrower's own risk costs it points over prime
  - L665 ...and its insured mortgage is written at the insured rate all the same
- **L672 8. save and load in the middle of a term: every field round-trips**
  - L678 fixture: the landlords owe mortgages, mid-term
  - L688 saved
  - L707 every mortgage, every field, to the bit
  - L708 ...the principal the month's payments took
  - L710 ...the premiums over the city's life
  - L711 ...the claims over it
  - L712 ...the budget's premium line
  - L714 ...and its claims line
  - L716 the next payment is the same payment
  - L717 the insured rate is struck again on load from the bank's saved prices, as prime is
  - L720 ...and a month on, the two cities write at the same rate
  - L722 ...and owe the same on their mortgages
- **L724 8. ...and a save from before mortgages loads its loans unchanged**
  - L752 fixture: the city owes business loans beside its mortgages
  - L753 its loans load as they were, field by field
  - L754 ...with no mortgage and an empty insurance book
- **L771 9. where the old rule said no: a new landlord's own risk refused a House the lender's test passes**
  - L776 fixture: the landlords have nothing on site and want to build
  - L792 fixture: the landlords own nothing and owe nothing yet
  - L830 the old rule refused it at 0.7.11's loss: gross rent under 1.25 times the interest on its whole cost at its own risk
  - L832 ...borrowing all of it leaves the landlord at 1.0 times what it owns
  - L834 ...where the curve charges its own expected loss over prime, at a loan's loss today
  - L836 ...which was points of it at 0.7.11's loss
  - L837 the lender's test passes: its income covers the payment on 85% MORTGAGE_DEBT_COVERAGE times
  - L839 ...so the landlord builds it
  - L850 ...and in a played month it does, on an insured mortgage
- **L872 10. the leverage ratio: a book of insured mortgages holds capital against its face**
  - L875 fixture: the bank's equity is what the fixture set
  - L876 ...its exposure is everything it has lent, the mortgages at face
  - L877 fixture: on its risk-weighted book it is far past its target
  - L879 ...but the leverage requirement is the larger
  - L880 so its minimum is LEVERAGE_RATIO_MIN of the exposure
  - L882 ...its leverage target LEVERAGE_RATIO_MIN scaled by the buffer it chose on the risk side
  - L884 ...and its target equity that on the exposure
  - L886 fixture: 3.5% of what it has lent is between the minimum and that target
  - L888 so it is rebuilding, whatever its risk-weighted ratio says
  - L890 ...pays its owners nothing
  - L891 ...buys none of its shares back, and issues them
  - L893 ...lets the desk carry nothing new
  - L896 ...and lets a borrower's debt grow by the rule, on the leverage ratio
  - L898 ...nothing asked of the city while it is over the minimum
  - L907 fixture: at 2.5% of what it has lent it is under its minimum
  - L909 ...and the city is asked for what takes it back to its target
  - L911 fixture: every other part of the branch test says open one - the book spills over what
  - L915 ...but a bank under its minimum opens no branch for the capital it would bring
  - L919 fixture: at 4.5% it is over its leverage target and under the top of its band
  - L922 its payout is its share of a profit
  - L923 ...never past what it holds over its leverage target
  - L924 ...its buybacks too
  - L925 ...which is less than the risk weights alone would have let it spend
  - L927 ...and its desk carries only what leaves it at the leverage target
  - L930 paid out, it holds its leverage target
  - L931 ...which is at least LEVERAGE_RATIO_MIN of everything it has lent
  - L935 its weight table still foots
  - L936 ...and the Bank tab's words read the leverage ratio
- **L951 11. a branch that does not pay is closed: after the fuse, and never the last**
  - L956 fixture: its book has not kept its three branches' staff for a month short of the fuse
  - L958 ...and it closes nothing yet
  - L960 at BRANCH_CLOSE_MONTHS it closes one
  - L961 ...the fuse being the city's distress fuse
  - L963 a month the book keeps its staff resets the count and closes nothing
  - L969 the last branch stays, however long its book has not kept it
  - L1011 fixture: the played bank closed a branch
  - L1012 ...not before its book had failed its staff for BRANCH_CLOSE_MONTHS
  - L1014 ...one branch
  - L1015 ...sold as a retired building, by its owner - on the city's list of what came down
  - L1017 ...its founding capital left where it was
  - L1018 the bank's equity moved by its income and its named causes that month
  - L1019 ...and the month's money audit closes through the closure
  - L1023 fixture: the streak is running when the city is saved
  - L1029 a reloaded bank has run the same months of its streak
  - L1031 ...and stands the same branches
- **L1037 12. payouts after principal: a share of what the month leaves once the lender is paid**
  - L1100 fixture: a month whose income beat the principal its mortgages took, and one it did not
  - L1104 a landlord with a mortgage pays PAYOUT of its income less the principal it repaid
  - L1109 no month paid the landlords past what their income left after the principal, net of what the desk rolled
  - L1110 with nothing repaid the rule is the old one

## NewGameCheck.java - 89 labelled assertions

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

- **L215 1. what a city that never existed looks like**
  - L223 it has no people
  - L224 it has its starting cash
  - L225 ...and the founders' dollars in the vault
  - L227 ...bought on day one, at the opening rate
  - L230 it is at month 1
- **L232 2. live in one, hard**
  - L278 the used city really is used
  - L281 ...and its vault is not the founders' any more
  - L283 ...and it paid for land out of it, and pays that way still
  - L287 ...and its landlords owe insured mortgages, and have paid the city premiums on them
- **L291 3. start a new one**
- **L312 4. and it is actually playable**
  - L316 the building catalogue is loaded
  - L320 houses can be ordered
  - L326 and so can shops
  - L331 months pass
  - L332 and people move in
  - L335 its history starts from this city, not the last one
- **L338 5. a new game after a LOAD, too**
  - L348 saved
  - L352 loaded
  - L357 ...with the vault it was saved with, not a new city's
  - L380 starting a new game does not delete the save it left
- **L383 6-11. FOUNDING A CITY (0.7.10)**
- **L421 6**
  - L425 the defaults are the two constants
  - L427 ...in the default world, Danzik, its money named after it
  - L431 the Standard preset is the constants
  - L435 ...and Lean and Wealthy are their own constants
  - L442 a city founded on the defaults opens with exactly them
  - L447 ...named Danzik, in money named after it
  - L462 fixture:
  - L478 each preset and a custom founding opens with exactly its treasury and vault
  - L479 ...the vault bought at the opening rate, booked as the purchase it is
  - L480 ...and lives its first month with the audit closed
  - L497 a city at the
  - L499 ...and runs two years, every month audited
  - L503 newGame() with a founding - the menu's door - founds exactly it
  - L514 an empty treasury is not a city: refused at the door
  - L515 ...as is one under the floor or over the ceiling
  - L518 ...and a vault below nothing or over its ceiling
  - L522 ...and a city with no name, or a name too long for the title
- **L527 7**
  - L540 fixture: Arden, in its crown, founded and lived in
  - L542 saved
  - L544 the city's name comes back
  - L545 ...and its money, whole: name, plural, code and both symbols
  - L547 ...and the treasury and vault it was founded with, not what it has now
  - L550 ...and the world it was founded into, which the world's own save carries
  - L552 the slot list names the city
  - L563 fixture: the save carried all eight of the founding's keys
  - L567 fixture: the stripped save loads
  - L568 a save from before 0.7.10 loads as Danzik
  - L569 ...in the Danzik dollar, every city's before: Danzik dollars, DZD, $ and D$
  - L573 ...founded with D$2.5B and US$1B, the Wealthy preset's
  - L575 ...and otherwise the city it was: its own cash and vault
  - L578 ...and the slot list says Danzik too
- **L580 8**
  - L584 Arden gives the Arden dollar, the Arden dollars
  - L586 ...A$ beside a US dollar and $ alone, ARD
  - L605 the edges: accents, other scripts, stops, short names and the world's code
  - L610 the world's code is never derived
  - L613 typed by hand: its name, an s for the plural, and the code upper-cased
  - L615 ...written $ alone and its initial and $ beside a US dollar
  - L618 ...a name that ends in s is its own plural
  - L619 the world's code is never accepted, in any case
  - L622 ...nor a code that is not exactly three letters A to Z
  - L626 ...nor a name with no letter in it
  - L628 a founding in the world's money is no founding
  - L631 the foreign money stays the US dollar, US$, USD
- **L635 9**
  - L640 a city founded on the defaults beside Arden is Danzik, in its own money
  - L642 ...and Arden is still Arden, in its crown, with its own founding
  - L646 Start New Game on Arden's own object founds Danzik on the defaults, nothing of Arden's
  - L649 ...and after loading Arden: its name, its money and its world all left behind
- **L652 10**
  - L663 founding at each of the screen's worlds sets the world's mean
  - L664 ...and back-casts its first year at that mean
  - L665 ...and the default is among them
  - L674 a new city after a
- **L678 11**
  - L698 fixture: a new city places the founding village
  - L699 ...and is charged for it exactly what whatItBuys() says
  - L704 ...and quoted for each first work exactly what it says, the yard spent
  - L709 (a) the Standard treasury pays for the village and at least one of the first works
  - L726 (b) ...and not the others as well: after any one of them, the rest need a bond
  - L741 (c) fixture: a year on, the water plant is more than its treasury holds
  - L744 (c) the page's gap is the plant's invoice less the treasury
  - L746 (c) the page's bond is at one of the five maturities
  - L753 (c) the page's note is still quoted: six months, covering the gap
  - L767 (c) a Standard city short of the water plant is quoted the bond
  - L770 ...whose cash covers the gap
  - L771 ...by no more than one issue granule plus the fees
  - L782 ...and so at every maturity and size, not this one alone
  - L787 (c) a new city borrowing for it gets it: the bond issues
  - L789 ...and lands exactly the cash it was quoted
  - L791 ...on the books as a term bond of BUILD_BOND_YEARS, at the face quoted
  - L794 ...and the plant is ordered
  - L803 ...and built
  - L804 ...and every month of it the audit closed

## OrderBookCheck.java - 58 labelled assertions

> The limit-order book (0.7.12), on its own: the rules any instrument trades
> by, proved on a book that knows nothing about what it trades.
> 
> WHAT THIS HAS TO PROVE (Jerus, 2026-09-24: "Order book for both" - "its
> availability to get filled both buy and sell"):
> 
>   1. PRICE-TIME PRIORITY: the best price first, and at one price the
>      order that came first.
>   2. A TRADE IS AT THE RESTING ORDER'S PRICE, whichever side arrives.
>   3. NOBODY IS OBLIGED TO TRADE: a sell with no buyer rests unfilled, a
>      buy with no seller rests, and the close counts who waited.
>   4. PARTIAL FILLS: an order fills as far as the other side reaches and
> ...

- **L105 1. price-time priority: the best price first, and at one price the first to come**
  - L113 fixture: four bids resting, nobody selling
  - L114 the bids stand best first: 1.02 (B), 1.02 (C), 1.01 (D), 1.00 (A)
  - L118 a sell of 12 at 0.95 takes B's 5, then C's 5, then 2 of D's - in that order
  - L121 ...B's first, whole
  - L122 ...D's last, as far as the sell reached
  - L123 ...and A's bid at 1.00, the worst, is untouched
  - L130 the other way up: a buy takes the lowest ask first, and at one price the earlier (B, then C)
- **L137 2. a trade is at the resting order's price, whichever side arrives**
  - L142 a buy at 1.20 meeting a resting ask at 1.05 trades at 1.05
  - L143 ...and the buyer paid 1.05 a unit, not its limit
  - L147 a sell at 0.90 meeting a resting bid at 0.98 trades at 0.98
  - L148 the last price is that trade's
- **L154 3. nobody is obliged to trade: an order with nobody across rests, and the close counts who waited**
  - L158 a sell with no buyer makes no trade
  - L159 ...and rests, unfilled
  - L161 a buy under the ask makes no trade either
  - L162 ...and rests beside it: the book does not close the gap
  - L163 ...the spread stands
  - L164 before any trade there is no last price
  - L167 the close withdraws every order
  - L168 ...and counts both sellers as having waited
  - L169 ...with what they still had to sell
  - L170 ...and the buyer too
- **L176 4. partial fills**
  - L182 a buy of 4 against a resting 10 fills 4
  - L183 ...and the 6 left of it stay
  - L184 ...at the head of the book, where it was
  - L186 a buy of 15 against what is left, 11, fills 11
  - L187 ...and rests the other 4 at its own limit
  - L188 ...on a book with no asks left
  - L189 the month filled 15 of the 15 posted to sell
  - L191 no seller waited: both filled whole
- **L197 5. no trade with oneself**
  - L203 A's buy passes over A's own ask and takes B's
  - L204 ...at B's price
  - L205 ...and A's ask keeps its place at the head
- **L212 6. what the clearing can settle: a buyer short of cash, a seller short of the thing**
  - L217 a buyer with $3 for 8 at $1 fills 3
  - L218 ...and nothing of it rests: it cannot pay for more
  - L219 ...the seller's 7 still rest
  - L223 a resting seller that holds 2 of the 6 it asked for delivers 2
  - L224 ...and the buyer's other 4 rest at its limit
  - L225 ...where the seller's ask, trimmed to nothing, is gone
  - L226 every trade the book reported settled
  - L227 ...and no participant holds less than nothing
- **L233 7. the record: posted, filled, volume, turnover, the last price, the depth**
  - L240 posted to buy: 4 + 6 + 5
  - L241 posted to sell: 7
  - L242 filled: 7, all at 0.99
  - L243 ...its turnover 7 x 0.99
  - L244 two trades, in the month they were made
  - L246 the depth by level: 3 left at 0.99 (one order), 5 at 0.97
  - L249 ...and the depth at or better than 0.97 is all of it
  - L251 a new month opens a new record, and the orders and the last price stand
- **L258 8. a currency reform: quantities for an instrument counted in money, prices for one that is not**
  - L264 a bond's bid: its face a hundredth
  - L265 ...and its price a unit of face unchanged
  - L269 a share's bid: its price a hundredth
  - L270 ...and still three shares
- **L276 9. save and load: the resting orders, their order and the last price**
  - L285 it comes back under its own instrument
  - L286 ...with the same bids, best first
  - L288 ...the same ask
  - L289 ...and the same last price
  - L291 an order posted after the reload queues behind the saved ones at its price
  - L294 a book saved with nothing on it loads empty, with no last price

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
  - L466 the treasury pays the EI the ring struck on the pool the month opened with
  - L468 the premium is the dial times the staffed wage bill
  - L471 the pool is the labour market's
  - L476 the families hold the adults who are not outside them
  - L515 fixture: the city has more workers than posts
  - L524 fixture: the closing cost filled posts
  - L526 ...and put people on EI
  - L539 the closing month pays the EI of the pool it opened with
  - L541 ...and the out of work are credited that figure, the same month
  - L544 fixture: the closing moved the bill, so a month's lag would show
  - L546 fixture: there is EI to pay
  - L549 the month after pays the bill the closing struck
  - L551 ...and the out of work are credited it in the month it is paid
  - L554 and both months pass the money audit
- **L558 8. a city with a college: the students' money, and a save**
  - L636 fixture: the college has students
  - L640 the grant is the dial times the unskilled wage, for every student
  - L643 the students pay the tuition (Jerus: \"students pay it\")
  - L645 ...and no family does
  - L646 the treasury lent student loans
  - L647 every month, the students the census saw finish left with their loans
  - L649 fixture: people graduated
  - L650 ...so the working families carry student loans
  - L651 ...and graduates have repaid some of them
  - L652 a student is never cut off
  - L653 every month passed the money audit
  - L659 the city saved
  - L662 the student loans came back
  - L663 ...and the pool
  - L664 ...and who was on EI
  - L665 ...and the EI dial
  - L666 ...and the orphans
  - L667 ...and who finished a course, which the families read next
  - L669 fixture: somebody finished that month
  - L671 a temporary directory for the save:

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
  - L281 a city that trades steel has freight to move
  - L285 ...and the band agrees with what the railway says it is carrying
  - L313 track laid and no trains bought yet carries nothing
  - L318 ...and the month after it has the trains, it carries
  - L331 every dollar the railway billed is on a shipper's cost line
  - L335 ...and the shippers' opened cost line still adds to the closed one
  - L350 the railway's fuel and its locomotives are both imports
  - L356 ...and it bought the fleet its track needs
  - L361 it is carrying what it built to carry
  - L367 ...and the band moved for it
- **L393 and the quote prices the track**
  - L401 a railway sized to its city bills about what the rule allows it
  - L405 ...and is inside its own bounds while it does
  - L414 four times the track it needs, and it cannot charge for it
  - L420 ...so it pins at the lorries' price, which is its ceiling
  - L423 ...and earns LESS on four times the capital
  - L428 ...but never quotes below its floor, because freight needs profit
- **L433 5. AND IT SURVIVES A RELOAD**
- **L437 and what it was charging survives a reload**
  - L445 the quote came back
  - L452 ...and so did what it was carrying, stream by stream
  - L453 ...and its locomotives came back with it
  - L456 ...and the band the reloaded city quotes is the one it was saved with

## ReadPathCheck.java - 11 labelled assertions

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

- **L714 a city with money moving in every sector**
  - L759 fixture: the businesses owe bonds, and orders rest on their books
  - L767 every sector is actually trading
- **L773 the FIRST read, which is the hard one**
  - L829 one pass over the screens moved nothing
  - L846 the live sale figure IS the one in the ledger
- **L850 read it, and read it again**
  - L877 reading the city fifty times changed nothing
- **L879 and the specific one item 7 was about**
  - L935 every one of the thirteen pantries fell by what sold and rose by what arrived
  - L942 ...and the statement never sold more than was in stock
  - L944 ...and the shelf never goes negative
- **L947 the tax the city takes is the tax it shows**
  - L955 business tax collected == business tax printed
  - L959 ...and it is the companies taxed separately, not netted
- **L964 a rate change reaches the treasury at once**
  - L985 doubling the rate moves the very next month's commercial tax

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

## SaveFileCheck.java - 256 labelled assertions

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
  - L260 DataSave wrote itself
  - L265 cash survived
  - L266 month survived
  - L267 population survived
  - L268 buildings survived
  - L269 household savings survived
  - L270 land survived
  - L271 tax rates survived
  - L273 the city's name survived
  - L274 ...its money, all five of its names
  - L275 ...the treasury it was founded with
  - L276 ...and the vault
  - L277 ...and the world's mean is handed back, not saved twice
  - L279 the slot list reads the same name off the same file
  - L283 a save with none of the eight reads as the founding every city had before 0.7.10
  - L288 ...and its header says Danzik
  - L294 no file paths leaked into the save
  - L306 two months recorded
  - L307 HistorySave wrote itself
  - L308 ...to its own file, not over the save
- **L311 7. construction survives a save**
  - L352 something is genuinely mid-build
  - L378 saved
  - L387 depots still under construction
  - L389 stores still under construction
  - L391 and the part-finished work came back
  - L398 land committed to unfinished sites is still committed
- **L401 7b. the warning survives a save**
  - L444 an unprotected city with a fresh shed IS warned
  - L454 the month construction last shed came back
  - L456 ...and the capacity it has sold since
  - L458 ...and it is still unprotected
  - L460 ...so the player is still being warned
  - L465 protecting construction takes the warning down
  - L476 a dismissed warning stays dismissed across a reload
- **L479 8. a save with nothing else built**
  - L492 nothing is finished yet
  - L494 but one is being built
  - L496 saved
  - L500 it did not vanish
- **L503 9. the headline income does not move**
  - L510 property tax survived the round trip
  - L525 the city actually charges retail something
  - L527 retail's property tax expense came back
  - L530 real estate's did too
  - L533 ...and the line on the statement with it
  - L540 the sector charges are part of the city's total
- **L543 10. the whole figure, to the cent**
  - L558 the city has people at all
  - L559 population is not invented by loading
  - L601 goods reached the shops, which the mills had to make first
  - L603 ...so the figures compared below are not all zero
  - L607 business tax
  - L609 wage tax
  - L611 retail cost of goods
  - L614 ...and the month in progress
  - L630 next-month income is identical across a save
  - L633 sales tax
  - L635 monthly GDP
  - L641 construction backlog
  - L643 construction unearned revenue
  - L646 construction cash
  - L658 the city did lose something
  - L659 the demolition log came back
  - L661 ...with its entries intact
  - L664 the write-off record came back
  - L679 the city did finish something
  - L680 the build log came back
  - L682 ...with its entries intact
  - L685 ...and the merged quantities unchanged
- **L689 11. a city that is still MOVING**
  - L750 the city really is still growing
  - L752 ...and its statement describes a smaller month than the one in progress
  - L765 saved mid-growth
  - L773 the workforce that worked the month came back
  - L775 wage tax
  - L777 retail gross revenue
  - L780 ...and the month the shops are in
  - L783 industrial gross revenue
  - L786 sales tax
  - L788 monthly GDP
  - L790 and next month's income, to the cent
- **L793 12. a city that OWES money**
  - L824 the city really does owe something
  - L826 ...and has interest on the books waiting to be charged
  - L832 saved in debt
  - L841 the debt came back
  - L851 fixture: the households hold some of it
  - L853 ...and still do after the reload, cell by cell
  - L856 ...which is what the paper says they hold
  - L859 ...and the discount still to accrete on it came back
  - L862 ...and so did the interest it had already accrued
  - L865 ...so next month's income still shows the deficit
  - L877 and a month later both cities have paid the same bill
- **L881 12b. ...AND ONE THAT OWES ABROAD (2026-09-21)**
- **L895 and a city with dollars owed abroad**
  - L917 fixture: the city really does owe dollars
  - L919 fixture: ...and the rate really did move them this month
  - L922 saved owing abroad
  - L925 what the currency did to the debt this month reloads
  - L927 ...beside the rest of the foreign accounts, which always did
- **L931 13. AND NOTHING READS ZERO ON A FRESHLY LOADED CITY**
- **L963 and a freshly loaded city reads what the live one reads**
  - L1066 fixture: the bank opened and started paying its savers
  - L1118 saved a city with one of everything in it
  - L1125 fixture: savers really were being paid something
  - L1127 fixture: the schools really were running
  - L1129 fixture: the dial really did pay out
  - L1131 fixture: somebody really was hungry
  - L1133 fixture: the tiers really were shopping
  - L1144 fixture: the city really was paying for repairs
  - L1146 what the city paid to keep its buildings up
  - L1149 what savers are paid
  - L1153 the founding record reads the same: the name
  - L1154 ...the money
  - L1155 ...the treasury it was founded with
  - L1156 ...the vault
  - L1157 ...and the world it was founded into
  - L1171 fixture: the bank really had a price with every part in it
  - L1174 the bank's prime
  - L1175 ...what a household pays it
  - L1177 ...what the carry trade is lent at
  - L1179 ...its running costs per dollar lent
  - L1181 ...what it expects to lose
  - L1183 ...and how much of its money came from the window
  - L1191 its year of costs came back whole
  - L1192 the profit it booked after its close
  - L1202 fixture: the bank really had set something aside
  - L1203 what the bank has set aside against its loans
  - L1214 
  - L1215 ...and its assets
  - L1216 ...and the risk its next loan is priced at, over prime
  - L1227 fixture: the city's landlords owe insured mortgages, and paid them down this month
  - L1229 what the landlords owe on their mortgages
  - L1231 ...at the rate each was written at for its term
  - L1233 ...their next payment
  - L1234 ...their next renewal
  - L1235 ...the principal the month's payments took
  - L1237 ...the premiums the insurance has taken over the city's life
  - L1239 ...and the claims it has paid
  - L1240 ...the budget's premium line
  - L1242 ...and its claims line
  - L1244 ...and the bank's book of them
  - L1255 fixture: the city's businesses owe bonds, and orders rest on their books
  - L1257 the bonds outstanding
  - L1258 ...their face
  - L1259 ...the households' of it
  - L1260 ...the bank's
  - L1261 ...the companies'
  - L1262 ...the world's
  - L1263 ...their coupon, weighted
  - L1270 ...the orders resting on their books
  - L1271 the households' bonds
  - L1280 ...each cell's own, bond by bond, by its name (
  - L1290 the shares' last trades and fair values
  - L1291 ...and the orders resting on their books
  - L1292 ...and what a unit of them is worth this month
  - L1294 the bank's bonds, at what they cost it
  - L1295 ...weighed as loans
  - L1296 ...and the capital its book's concentration adds
  - L1298 the month's issues, for the Bonds page
  - L1299 ...the coupons paid abroad, for the Trade tab
  - L1300 ...the world's purchases
  - L1301 ...last month's book
  - L1302 ...and over the city's life, the coupons the households were paid
  - L1305 
  - L1306 ...what bondholders have lost on them
  - L1307 ...its month's bond lines on the sector screen
  - L1313 ...how long the bank's book has not kept its branches' staff
  - L1315 ...and its capital against everything it has lent
  - L1317 the builders' salvage at what they paid for it
  - L1319 ...the month's provision
  - L1320 ...the capital target it chose
  - L1321 ...its interest income, on the reloaded Profit page
  - L1322 ...its fees
  - L1323 ...its profit before tax
  - L1324 ...what it kept
  - L1325 ...what it paid its owners, this month and over the year
  - L1327 ...and the equity it opened the month with
  - L1333 its month's
  - L1341 ...and its allowance, book by book
  - L1348 fixture: the bank had last month on file
  - L1349 last month's profit, beside this month's, on the reloaded Bank tab
  - L1351 ...the last twelve months' interest
  - L1353 ...this month's interest from the businesses
  - L1355 ...and its year of statements came back whole
  - L1358 fixture: the households really paid account fees
  - L1360 what the households paid in account fees
  - L1363 tier
  - L1366 what the dial paid out
  - L1368 the schools' payroll
  - L1370 ...their upkeep
  - L1372 ...the fees they waived
  - L1374 ...and the fees they collected
  - L1376 the hunger inside the sick rate
  - L1379 fixture: the health premium really was collected
  - L1381 the fee scale
  - L1383 ...and the health premium
  - L1385 ...the scale the service charges at
  - L1387 ...what the premium raised
  - L1389 ...what the households paid of it
  - L1391 ...the treatment bill at full service
  - L1394 row
  - L1396 row
  - L1405 households doubled up
  - L1407 ...and the ones a studio turned away
  - L1411 tier
  - L1413 tier
  - L1416 the government's surplus
  - L1419 ...and the business tax inside it
  - L1432 fixture: the treasury's journal had lines in it
  - L1433 the treasury's journal has as many lines as it had
  - L1435 ...line
  - L1437 ...and still says
  - L1440 ...and what the journal left unexplained
- **L1443 14. a reloaded city PLAYS ON as the one it was saved from**
  - L1480 fixture: the shops handed over less than the households asked for
  - L1482 fixture: the price index has not been based yet
  - L1484 saved a city in its first two years
  - L1488 the share the shops handed over came back
  - L1494 fixture: the index was based in those six months, in the city saved
  - L1496 ...and in its reload
  - L1497 six months on: the price index
  - L1499 ...the sick rate
  - L1500 ...the treasury
  - L1501 ...the households' savings
  - L1503 ...the bank's equity
  - L1505 

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
  - L254 fixture: the hospitals gave the second city more coverage
  - L256 the adults' share of the ring is the city's sick rate - output is untouched
  - L258 the city with no hospitals lost people to illness
  - L261 ...many times more, per head, than the city with them
  - L264 the ring's dead are the pyramid's dead
  - L265 every month passed the money audit
- **L267 7. a save, and a save from before**
  - L270 fixture: the ring has something in it
  - L277 the whole ring comes back
  - L278 a malformed ring is refused whole
  - L282 fixture: the ring is gone
  - L284 a city with no ring seeds one and loses its long sick the same month

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

- **L103 1. WHAT A FLEET IS FOR**
- **L107 a sector's fleet is sized by what its plant moves**
  - L112 a sector that moves nothing needs no vehicles, and its ratio is one exactly
  - L118 ...and one that moves something needs vehicles in proportion
  - L124 ...and a sector with vehicles enough multiplies its rate by one, to the bit
  - L141 what goes in is a lorry movement as much as what comes out
- **L145 2. THE FLOOR AND THE CEILING OF THE RATIO**
- **L149 and what being short of them does**
  - L157 a sector with no lorries at all is slowed to the floor, not stopped
  - L162 ...and half a fleet is half way up from the floor
  - L168 ...and a sector with more than it needs gets no bonus for them
  - L171 fixture: the mills move something, or this proves nothing
- **L175 3. YOU CANNOT PUT A FLEET ON THE ROAD IN A MONTH**
- **L179 a fleet is built up, not bought**
  - L182 a sector with nothing asks for what it can take delivery of, not for everything
  - L187 ...and one that is nearly there asks only for the gap
  - L192 ...and one with enough asks for nothing
- **L196 4. IT WEARS OUT**
- **L200 and a fleet is a stock, not a flow**
  - L205 a fleet wears out at one part in VAN_LIFE_MONTHS a month
  - L214 ...and ten years of it with nothing bought is a fleet gone
  - L224 ...which leaves the sector down at its floor
- **L231 5. A SAVE FROM BEFORE VANS HAD VANS**
- **L235 and every city that exists already owns a fleet**
  - L250 a save from before vans reads as a sector that has never been asked
  - L252 ...and is not stopped by it, because an unasked sector runs at one
  - L256 ...while a save from this build carries the fleet it had
- **L261 6. IN A CITY THAT RUNS**
- **L265 and the city really buys them**
  - L277 a city with industry in it runs a fleet
  - L279 ...and pays for it, every month, on somebody's cost line
  - L283 ...at a price the market struck, inside its band
  - L296 the city's own industry is the market for a van plant

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

## YearBookCheck.java - 92 labelled assertions

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

  - L103 the fixture actually produced crime-by-cause series
  - L104 the fixture actually produced households-by-shape series
  - L105 the fixture actually produced a share register
  - L118 kind is declared for
  - L119 a note is declared for
  - L131 year 1 gdp is its twelve months added
  - L132 year 2 gdp is its twelve months added
  - L133 gdp is marked as a flow
  - L143 year 1 population is December's
  - L144 year 2 population is December's
  - L145 population is marked as a level
  - L161 year 1 sick rate is the mean of its months
  - L162 the year's worst month is kept
  - L163 and its best
  - L164 sickRate is marked as a rate
  - L181 year 1
  - L186 year 2 bankFees is its twelve months added
  - L187 bankFees is marked as a flow
  - L205 year 1
  - L211 year 2
  - L215 year 2 bankAllowance is December's
  - L216 bankAllowance is marked as a level
  - L235 a flow row missing months is blank
  - L236 and the complete row is the sum
  - L237 the file says what a blank means
  - L248 a full year covers twelve months
  - L249 the stub year says it is one month
  - L250 and its flow is that one month
  - L260 a full decade covers ten years of months
  - L262 the stub decade carries the remainder
  - L263 the first decade's flow is its months added
  - L264 the decade book calls its rows decades
  - L281 the episode list names the failure
  - L282 and says which month it was
  - L283 the year's own row hides it, which is why the list exists
  - L298 a decimal point survives a French locale
  - L299 ...and so does a small one
  - L300 ...and a big one
  - L301 a thousand is written without a separator
  - L302 nothing is recorded is not zero
  - L303 and zero is zero
  - L316 no comma in a data row:
  - L318 and there were data rows to look at
  - L329 the year book sits in the game's own folder
  - L331 so does the decade book
  - L333 and they are not the same file
  - L361 the history records the pool the People screen shows
  - L368 with the pool recorded, the pool over the labour force
  - L370 the column says what it is
  - L374 without the pool, the labour force less the posts, over the labour force
- **L429 the premise**
  - L430 the fixture has somebody studying, or the two denominators are the same number
  - L432 the fixture offers posts it has not filled, or jobs and filled posts agree by luck
  - L439 ...so the formula this replaced actually disagrees with the model here
- **L442 and the assertions**
  - L462 the labour force is the model's labour force
  - L464 the filled posts are the model's filled posts
  - L467 unemployment is the model's own rate
  - L478 the average wage divides the recorded wage bill by the filled posts
  - L483 ...and the recorded wage bill is the model's, to the cent
  - L537 the fxRate note says the city's dollars per US dollar
  - L538 ...in the preamble and in the column's own note
  - L540 ...and that higher is a fallen currency
  - L541 ...and never the other way round
  - L542 ...and never another city's money: no Danzik in Arden's book
  - L543 ...nor the rule's placeholder, which the book writes out
  - L579 the fixture names two episodes and no others
  - L582 the first is the bank's
  - L583 ...named for the year it began
  - L585 ...from the month equity went under
  - L586 ...to the last month it was under
  - L587 ...and its worst is the equity it reached
  - L589 the second is the recession
  - L590 ...named for the year it began
  - L592 ...from the month the year fell short of the one before
  - L593 ...for exactly EPISODE_MIN_MONTHS months
  - L595 ...and its worst is a fall
  - L599 the chart shades the same recession and nothing else
  - L601 ...from the month it began
  - L602 ...to the month it ended
  - L606 the book lists the crisis, one line with its months
  - L608 ...and the recession
- **L611 the edges**
  - L614 a two-month dip under water is not a crisis
  - L619 a loss that leaves one month below the year before is not a recession
  - L621 ...and is not shaded
  - L627 two failures a year apart are two episodes
  - L629 ...with two names
  - L630 ...the first for its year
  - L631 ...the second for its own
  - L638 two failures in one year, EPISODE_JOIN_MONTHS apart, are two episodes
  - L640 ...and the second is the first's name, again
  - L649 two failures with less relief than EPISODE_JOIN_MONTHS are one episode
  - L651 ...from the first month of the first
  - L652 ...to the last month of the second

