# HouseholdBalance.java - 6,555 lines · 388 methods · 54 constants · model

`ham/citybuildersim/HouseholdBalance.java` - generated 2026-11-25 by CodeMap; line numbers are as of that run.

> The households' balance sheet: what they have saved, what they owe, and what
> happens in the month they cannot cover the shop.
> 
> ==================== WHY THIS EXISTS ====================
> 
> Retail demand was `min(storeCoverage, population)` - a headcount, with no
> reference whatever to what anybody earned. So the shops sold the same basket
> to an unskilled single adult on $552 and to an Elite household on $18,673,
> and the household screen carried a five-line paragraph admitting it:
> 
>     "Nothing in the model ties spending to income yet - rent is the same per
>      head for everyone and so is the weekly shop - so a poor household is
>      charged what a rich one is. Those deficits are a missing budget
>      constraint, not a result."
> 
> A deficit that nothing funds is money from nowhere. This is the constraint.
> 
> ==================== THE WATERFALL ====================
> 
> Jerus's design, and it is the right shape: a household short of money does
> not simply eat less. It spends its savings, then it borrows, and only when
> both are gone does it go without - at which point going without is a health
> problem rather than an accounting one.
> 
>     payslip  ->  rent  ->  fees (healthcare, tuition, interest)  ->  food
> 
>     food short?   savings first, then credit, then go hungry
>     food spare?   pay down the debt first, then bank it
> 
>     ...and the clinic's fee is the one bill that gives way before food
>     (2026-09-19): a household that has run through its income, its
>     savings, its shares and its credit and would still eat less skips
>     the care bill first, and that share of its people goes untreated.
>     See Household.affordCare().
> 
> Debt before hunger and repayment before saving are both deliberate: they are
> what a household actually does, and each is the direction that makes the
> failure arrive slowly enough for the player to see it coming. The waterfall
> itself lives in Household.settle(); this class decides what each household
> is handed and adds up what they did.
> 
> ==================== SIXTY-EIGHT CELLS, NOT SEVEN ROWS ====================
> 
> Jerus, 2026-09-10: "I need it per household and pay tier type."
> 
> Until then the stocks were kept per pay tier - seven rows - and the header
> here argued that the shape could not carry a stock because FamilyModel
> rebuilds every household from scratch each month: "a stock attached to
> 'unskilled couple with a teen' would be a stock of nothing, handed to a
> different set of people every month." The tier survived; the shape did not.
> 
> It does now, because the money follows the people. Every cell of the family
> matrix is a Household object with its own savings, debt and credit line, and
> when the monthly rebuild moves households between cells - a child ages into
> a teen, five singles take a flatshare, a worker retires - their money moves
> with them. See followThePeople(). What used to be seven rows of arrays is
> sixty-eight objects (ninety-nine since HS1, a hundred and forty-one since
> HS2's young working cells) this class holds, sums and
> strikes; the row getters the screens read are sums of the cells, and the
> tier is a view rather than the unit.
> ... (24 more lines in the source)

**Uses:** [Household](Household.md) (313), [PayTier](PayTier.md) (52), [FamilyStructure](FamilyStructure.md) (45), [Equity](Equity.md) (43), [AgeBand](AgeBand.md) (41), [AdultStage](AdultStage.md) (33), [WorkingHousehold](WorkingHousehold.md) (13), [UnemployedHousehold](UnemployedHousehold.md) (10), [RetiredHousehold](RetiredHousehold.md) (7), [HouseholdAccounts](HouseholdAccounts.md) (6), [Bank](Bank.md) (5), [TaxPolicy](TaxPolicy.md) (5), [StudentHousehold](StudentHousehold.md) (5), [FamilyModel](FamilyModel.md) (5), [OwnerHousehold](OwnerHousehold.md) (5), [OutwardInvestment](OutwardInvestment.md) (5), [BondMarket](BondMarket.md) (4), [OrphanHousehold](OrphanHousehold.md) (3), [PrisonerHousehold](PrisonerHousehold.md) (3), [JobType](JobType.md) (2), [EstateCustody](EstateCustody.md) (2), [Restaurants](Restaurants.md) (2), [PopulationCohorts](PopulationCohorts.md) (2), [Exchange](Exchange.md) (2), [Debt](Debt.md) (2), [DebtStatistics](DebtStatistics.md) (2), [WageBand](WageBand.md) (1), [FundLedger](FundLedger.md) (1), [HousingMarket](HousingMarket.md) (1), [Retail](Retail.md) (1)... and 2 more

**Used by (77):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BasketYearCheck](BasketYearCheck.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalGainsCheck](CapitalGainsCheck.md), [CarCheck](CarCheck.md), [CardCheck](CardCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CpiCoverageFinding](CpiCoverageFinding.md), [CpiReferenceFinding](CpiReferenceFinding.md), [CrimeCheck](CrimeCheck.md), [DebtStatistics](DebtStatistics.md), [DebtStatisticsCheck](DebtStatisticsCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [EstateCustody](EstateCustody.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FamilyModel](FamilyModel.md), [FinancialOwnerFinding](FinancialOwnerFinding.md), [FloatLinkCheck](FloatLinkCheck.md), [ForestryCheck](ForestryCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdFlowCheck](HouseholdFlowCheck.md), [HouseholdStageCheck](HouseholdStageCheck.md), [HousingMarketCheck](HousingMarketCheck.md), [KeptBench](KeptBench.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [MarketCheck](MarketCheck.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [MoneyWordsCheck](MoneyWordsCheck.md), [Motoring](Motoring.md), [Offending](Offending.md), [OrderDoorCheck](OrderDoorCheck.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyOpenCheck](PolicyOpenCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PopulationCohorts](PopulationCohorts.md), [PrizeCheck](PrizeCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [TradeScreen](TradeScreen.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 104 | · the dials |
| 159 | AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT |
| 224 | ...AND WHAT IT SPENDS EATING OUT (2026-09-18) |
| 250 | AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) |
| 266 | AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) |
| 295 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 369 | BANKRUPTCY |
| 394 | · the cells |
| 790 | THE PRICE AT THE CLINIC DOOR (2026-09-19) |
| 870 | · the student loan's rate (2026-09-21) |
| 884 | · the bank's account fee (0.7.7) |
| 972 | · the month |
| 1063 | EVERY HOUSEHOLD, OR ONE OF THEM |
| 1273 | THE MONTH |
| 1375 | · ...AND A MEAL OUT IS FOOD (2026-09-18) |
| 1777 | THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS |
| 2205 | POSITIVE WEIGHTED HOUSEHOLDS OWN THEIR POSITIONS (RES3) |
| 2323 | MONEY FOLLOWS PEOPLE (HS1, 2026-11-18) |
| 2491 | · the retired's money against the working's (HS1) |
| 3010 | · EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16) |
| 3130 | THE CARS (2026-09-16) |
| 3209 | AND THEY BORROW FOR IT (2026-09-17) |
| 3294 | THE SECOND-HAND MARKET (2026-09-17) |
| 3497 | WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) |
| 3688 | · the second-hand market |
| 3753 | GROCERIES AT A PRICE, AND FOOD ASSISTANCE (0.7.43) |
| 3943 | · who goes without, read for the screens (0.7.45) |
| 3988 | THE LUXURY COUNTER (2026-09-17) |
| 4089 | THE TABLE (2026-09-18) |
| 4390 | THE MARKET |
| 4459 | THE WORLD'S PAPER |
| 4477 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) |
| 4578 | · THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17) |
| 4676 | THE CITY'S PAPER, AT HOME (0.7.1) |
| 4926 | THE BUSINESSES' BONDS, AT HOME (0.7.12) |
| 5192 | · the people outside the families |
| 5267 | · what the bank is owed |
| 5444 | · reading |
| 5547 | THEFT (2026-09-11) |
| 5626 | THE OFFER |
| 5934 | · saving |
| 6194 | · THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 99 | `HouseholdBalance.ROWS` | `Household.ROWS` | One row per pay tier, the retired, and since 2026-09-11 the out of work, the students and the orphans - the same shape as HouseholdAccounts. |
| 102 | `HouseholdBalance.ROWS_BEFORE_OUTSIDE` | `Household.RETIRED_ROW + 1` | The rows before the people outside the families had books: the six tiers and the retired. |
| 116 | `HouseholdBalance.CREDIT_LIMIT_MONTHS` | `6` | How many months of take-home a household can owe before the lender stops. |
| 135 | `HouseholdBalance.RISK_SLOPE` | `.015` | Extra spread per month of income owed. |
| 138 | `HouseholdBalance.MAX_RATE` | `Bank.CARD_RATE_CAP` | Nobody is charged more than this, however deep they are. |
| 157 | `HouseholdBalance.MARGINAL_PROPENSITY` | `.80` | How much of the money above subsistence a household spends. |
| 202 | `HouseholdBalance.WEALTH_SPENT_A_MONTH` | `.0033` | What share of its net worth a household spends in a month, over and above what it spends out of income. |
| 222 | `HouseholdBalance.LUXURY_SHARE_OF_SURPLUS` | `.5` | What share of the income a household does NOT spend on food goes over a luxury counter instead of into the bank. |
| 237 | `HouseholdBalance.MEAL_SHARE_OF_SURPLUS` | `.25` |  |
| 248 | `HouseholdBalance.MEAL_SHARE_OF_WEALTH` | `.5` | ...and the share of a FORTUNE'S monthly spend that goes on a table. |
| 264 | `HouseholdBalance.MOST_MEALS_EATEN_OUT` | `1 / 3.0` |  |
| 339 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provisional - Jerus's num... |
| 342 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 345 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 367 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 389 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 392 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 431 | `HouseholdBalance.TAX_FLOW_SLOTS` | `9` | Fixed tax-book slots: two receipts, two call counts, domestic/foreign gains receipts, assessment, loss, consolidation. |
| 432 | `HouseholdBalance.CG` | `0, DIV = 1, GAINS_CALLS = 2, DIV_CALLS = 3, DOMESTIC_CG = 4, FOREIGN_CG = 5, ...` |  |
| 2222 | `HouseholdBalance.EMPTY_CELL` | `.5` | Minimum cohort for the existing optional eligibility paths; actual positive financial recipients still settle. |
| 2402 | `HouseholdBalance.FLOW_RETIRE` | `0, FLOW_AGE_ON = 1, FLOW_DIE_ADULT = 2, FLOW_DIE_SENIOR = 3, FLOW_DIE_ELDER =...` | Slots of the month's flows, as setFlows() strikes them and flowsToSave() writes them: the shares of a band, and the evicted who left, a share of the out of work off benefits: every slot a share, so a scaled city's sav... |
| 2410 | `HouseholdBalance.RETIRING` | `0, AGEING_ON = 1, PARTNER_DIES = 2, DYING_ALONE = 3, LEAVING = 4, MOVING_UP =...` | The kinds of flow, for the month's record (getFlowHouseholds(), getFlowSavings()); MOVING_UP is the young turning 35 (HS2). |
| 2540 | `HouseholdBalance.OUT_OF_THE_CITY` | `- 1, AN_ESTATE = - 2` | A Move's destination when it leaves the city, or is an estate. |
| 3167 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 3179 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 3207 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 3266 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 3292 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 3351 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 3360 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 3710 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 3792 | `HouseholdBalance.FOOD_ASSISTANCE_MEANS_SHARE` | `.5` | A household is eligible for food assistance when a basket a head would take more than this share of what it has after its fixed bills and its investment income over the year: half. |
| 3806 | `HouseholdBalance.MEANS_INCOME_MONTHS` | `12` | Months the means test's investment income is smoothed over, as an exponential average - a twelfth of the gap a month, the year Expectations smooths inflation over (0.7.45; the UI spec's B16). |
| 4507 | `HouseholdBalance.MIN_MOVE` | `PriceLevel.atFounding(1e-12)` |  |
| 4719 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point. |
| 4722 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and, for a BUSINESS issue, never more than this share of one: half, because a bond market with no bank in it is no longer the bank's market (BondMarket). |
| 5640 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 5643 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 6000 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 6003 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 6006 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 6009 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 6012 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 6022 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 6032 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 6041 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 6053 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim on the households' o... |
| 6067 | `HouseholdBalance.CELL_SLOTS_BEFORE_GROCERIES` | `CELL_SLOTS_BEFORE_BONDS + 1` | ...and the last sale's groceries, appended 0.7.43: the baskets each household got, the baskets it asked for at the price, the baskets it needed, and the food assistance paid on them - written at the bottom of the mont... |
| 6075 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEANS_INCOME` | `CELL_SLOTS_BEFORE_GROCERIES + 4` | ...and the investment income the food assistance means test reads, smoothed over MEANS_INCOME_MONTHS, appended 0.7.45. |
| 6085 | `HouseholdBalance.CELL_SLOTS_BEFORE_AFTER_FIXED` | `CELL_SLOTS_BEFORE_MEANS_INCOME + 1` | ...and before each cell's income after its fixed bills was carried (A4). |
| 6088 | `HouseholdBalance.CELL_SLOTS_BEFORE_BASIS` | `CELL_SLOTS_BEFORE_AFTER_FIXED + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month's meals eaten out, t... |
| 6090 | `HouseholdBalance.CELL_SLOTS_BEFORE_SECURED_TAX` | `CELL_SLOTS_BEFORE_BASIS + Equity.COMPANIES.length + 2` | Basis by company, loss carry and the current month's taxable gains appended by HT1. |
| 6092 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARDS` | `CELL_SLOTS_BEFORE_SECURED_TAX + 2 * Equity.COMPANIES.length` | The tagged units and limited claims, appended after the basis checkpoint. |
| 6094 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_CARDS + 16` | Four card stocks, ten settled figures and two pending departure positions (HC1). |

## Fields (state)

| line | field | says |
|---:|---|---|
| 406 | `private final Household[] cells` | Every meaningful cell of the family matrix, in one fixed order: the working shapes by declaration, each across the six tiers - the established's eleven, then the young's seven (HS2) - then the four retired shapes, eac... |
| 407 | `private final int[][] index` |  |
| 410 | `private final int[][] youngIndex` | The young's cells by shape and tier (HS2): -1 for a shape the young do not form (AdultStage.forms). |
| 412 | `private final java.util.List<Household> view` |  |
| 413 | `private final int[] unemployedIndex` |  |
| 414 | `private int studentIndex` |  |
| 415 | `private final int[] orphanIndex` |  |
| 416 | `private int prisonerIndex` |  |
| 417 | `private int ownerIndex` |  |
| 425 | `private boolean ownerLine` | True when a listing company's founders' shares go to the owner families (BO1) and not to every household. |
| 427 | `private TaxPolicy investmentPolicy` |  |
| 428 | `private java.util.function.BiConsumer<String, Double> investmentTaxReceipt` |  |
| 429 | `private java.util.function.IntToDoubleFunction investmentPrice` |  |
| 434 | `private final double[] taxFlows` |  |
| 435 | `private boolean taxWindow` |  |
| 436 | `private final double[] governmentClaim` |  |
| 437 | `private final double[] departingTaxShares` |  |
| 438 | `private final double[] departingTaxClaim` |  |
| 439 | `private boolean departuresHandedOff` |  |
| 440 | `private Equity investmentRegister` |  |
| 441 | `private java.util.function.IntToDoubleFunction sharesShort` |  |
| 586 | `private EstateCustody custody` |  |
| 587 | `private BondMarket custodyBonds` |  |
| 592 | `final double[][] security` |  |
| 593 | `double loss, cashTax` |  |
| 636 | `private final int[] ownerFloorMonths` | Per company: -1 when it has no owner family; 0 or more when it has one, and then the number is the months its fair value has sat at the exchange's floor (the family gives up after OwnerHousehold.OWNER_BROKE_MONTHS). |
| 650 | `private ToDoubleFunction<Household> outsideCensus` | How many households are in each cell the family matrix does not hold - the out of work, the students, the orphans. |
| 661 | `private ToDoubleFunction<Household> rentShares` | The share of a door's rent one household of a cell pays: 1 alone in its own home, a fifth sharing, half doubled up, none with no door. |
| 663 | `private ToDoubleFunction<Household> rentBills` | RES3 actual class bill per household; NaN retains the bare-model pooled-rent API. |
| 784 | `private ToDoubleFunction<Household> outsideDependants` | Dependants living in each cell the family matrix does not hold. |
| 825 | `private double[] careBillByRow` | This month's treatment fees by row, as the households were billed them, or null for a caller that does not price care. |
| 828 | `private double[] careBillFullByRow` | ...and the treatment bill the same rows would have faced at full service. |
| 831 | `private double lastCareSkipped` | The care bills the households skipped this month, summed over the city: what they ate instead. |
| 877 | `private double studentLoanRate` |  |
| 895 | `private double accountFee` |  |
| 896 | `private ToDoubleFunction<Household> housedShares` |  |
| 974 | `private double lastWrittenOff` |  |
| 976 | `private double lastSeized` | What the month's bankrupt households' savings repaid the bank before the rest of their debt was written off (HL1; Household.discharge()), in total: part of totalRepaid(). |
| 977 | `private double lastLeaving` |  |
| 978 | `private double lastEvicted` |  |
| 979 | `private double lastStudentDebtTakenAway` |  |
| 980 | `private double lastTakenAway` |  |
| 982 | `private double lastArrivedWith` | What the month's newcomers brought as their opening buffer (OPENING_BUFFER_MONTHS): money from abroad into the savings. |
| 989 | `private double lastUnfunded` | Bills the sellers booked in full and a household could not fund (Household.settle, unfunded), summed over the households: money the shops were paid that no savings held. |
| 991 | `private double lastUnsettledIncome` | Row take-home with no positive recipient weight: the row total less what actual households settled. |
| 993 | `private double lastUnsettledFees` | ...the fees the row was billed for treatment and tuition that no settled cell was charged. |
| 995 | `private double lastUnsettledShopping` | ...the row's shopping that no settled cell paid: the sellers booked the sale, the cell that was to pay it was skipped. |
| 997 | `private double lastUnsettledRent` | Rent booked without a positive household recipient; retained as an explicit reconciliation guard. |
| 999 | `private double lastCensusStranded` | Savings left in a pool nobody is handed: a shrinking cell with no grown-ups releases its savings to a stock group that has none to claim them (moveStock, the savings stock only). |
| 1001 | `private double lastCensusCreated` | Savings a growing cell with no grown-ups keeps per household on more households: money made by the census (moveStock, the savings stock only). |
| 1002 | `private double graduating` |  |
| 1003 | `private double lastGraduated` |  |
| 1004 | `private double lastDepositInterest` |  |
| 1005 | `private double lastDelivered` |  |
| 1006 | `private double plannedSpend` |  |
| 1007 | `private double hungryPeople` |  |
| 1008 | `private double totalPeople` |  |
| 1010 | `private double hungryAtFullShelves` | ...of whom hungry even had the shops handed over all that was planned: the money half (0.7.27). |
| 1818 | `private int[] bondStockIds` | The bonds' ids in the order stocks() last listed them, one stock each from its named slot. |
| 1999 | `final double[] delta` |  |
| 2001 | `final double cityKeep` |  |
| 2225 | `private int lastCellsFolded` | Ownerless cells whose remaining positions were cleared. |
| 2312 | `private double[] spreadHad` | spread()'s reads of the cells' holdings, one array kept rather than one a call. |
| 2407 | `private final double[] flows` | The month's flows, as the demographics struck them, waiting for the next census. |
| 2413 | `private final double[] lastFlowHouseholds` |  |
| 2414 | `private final double[] lastFlowSavings` |  |
| 2416 | `private double lastEstateRepaid` | What the estates repaid the bank out of the dead's savings this month: part of totalRepaid(). |
| 2418 | `private double lastEstateWrittenOff` | ...the debt their savings did not cover, written off with the leavers' (in getWrittenOff()). |
| 2420 | `private double lastEstatePassed` | ...the savings passed in kind to the heirs. |
| 2422 | `private double lastPoolSavings` | Savings the pool moved between cells this month, from its row and city pools: what the flows did not explain. |
| 2527 | `final int kind, from, to, tier, heirs` |  |
| 2528 | `double out, in` |  |
| 2529 | `boolean bankrupt` |  |
| 2544 | `final java.util.List<Move> moves` |  |
| 2545 | `final double[] after` |  |
| 3127 | `private final double[] lastSharesTakenAway` | Shares of each company that left the city with their holders this month. |
| 3363 | `private final double[] carsToReplace` | What died this month, per cell, waiting to be replaced. |
| 3366 | `private double lastCarsTakenAway` | ...and the cars whose owners left the city. |
| 3809 | `private double satiationPrice` | The price a full basket is still wanted at, in today's money: told by Retail at its sale and by Game where the households plan (Retail.getSatiationPrice()). |
| 3812 | `private double foodAssistance` | The food assistance dial, 0 to 1: the share of an eligible household's baskets the treasury's voucher covers. |
| 3815 | `private double lastFoodAssistancePaid, lastFedByAssistance` | What the treasury paid toward the households' groceries at the last sale, and the baskets that bought. |
| 4192 | `private double lastMealsBought, lastMealSpend, lastMealsEaten` |  |
| 4213 | `private double lastLuxuriesBought, lastLuxurySpend` |  |
| 4332 | `private double lastUsedOffered, lastUsedTraded, lastUsedSpend, lastUsedFinanced, lastUsedPrice, lastUsedNew...` |  |
| 4370 | `private double lastUsedLoanFees` |  |
| 4372 | `private double lastCarsBought` |  |
| 4373 | `private double lastCarsFinanced` |  |
| 4387 | `private double lastCarLoanFees` |  |
| 4398 | `private Exchange exchange` |  |
| 4399 | `private Equity register` |  |
| 4400 | `private Bank bank` |  |
| 4401 | `private Household.Liquidity liquidity` |  |
| 4510 | `private double minMove` | The founding-money floor in the current unit. |
| 4544 | `private double localPerUsd` | Local currency per dollar, this month: what the paper abroad is worth here. |
| 4553 | `private double lastAbroadTakenAway` | Dollars the households that left this month took with them. |
| 4734 | `private PaperDesk paperDesk` |  |
| 4749 | `private double paperRatio` | What a dollar of face of the households' paper is worth this month: the households' book at the curve over its face (DebtManager .householdBookRatio()), struck once a month by Game before the households' strike and SA... |
| 4766 | `private double spendFactor` | The month's spend factor (0.7.3): what share of its spending above subsistence every household plans, struck by Game once a month on the real deposit rate (Game.spendFactor()) before the households plan - see the bann... |
| 4941 | `private BondMarket bondMarket` |  |
| 4954 | `private double bondRatio` | What a dollar of face of the households' bonds is worth this month: every cell's holdings at the bonds' value over their face, struck by the market at its step and SAVED, because the plan reads it and the load path mu... |
| 4963 | `private double lastBondsTakenAway` | The face that left the city this month with its holders, every bond together. |
| 5003 | `private java.util.Map<String, Household> cellIndex` |  |
| 6103 | `private java.util.function.DoubleSupplier carAssetPrice` |  |
| 6104 | `private java.util.function.DoubleSupplier assetExchangeRate` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 6463 | **type** `public class HouseholdBalance` | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. |

### the dials (lines 104-158)

### AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT (lines 159-223)

### ...AND WHAT IT SPENDS EATING OUT (2026-09-18) (lines 224-249)

### AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) (lines 250-265)

### AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) (lines 266-294)

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 295-368)

| line | len | member | says |
|---:|---:|---|---|
| 353 | 5 | `public static double spendFactor(double realDepositRate)` | The share of its spending above subsistence a household keeps at this real deposit rate (annual, a fraction): 1 at zero, less when saving pays, more when it costs, between SPEND_FLOOR and SPEND_CEILING. |

### BANKRUPTCY (lines 369-393)

### the cells (lines 394-789)

| line | len | member | says |
|---:|---:|---|---|
| 444 | 4 | `public void setInvestmentTaxes(TaxPolicy policy, java.util.function.IntToDoubleFunction prices, java.util.function.BiConsumer<S...` | Wires HT1 to the city's policy, live quotes and actual treasury receipts. |
| 448 | 4 | `public void attachInvestmentRegister(Equity register, java.util.function.IntToDoubleFunction shorts)` |  |
| 452 | 1 | `private void taxFlow(int slot, double amount)` |  |
| 453 | 5 | `public void beginTaxMonth()` |  |
| 458 | 1 | `public void closeTaxMonth()` |  |
| 459 | 5 | `private void resetTaxBook()` |  |
| 464 | 7 | `private void startDepartures()` |  |
| 471 | 1 | `boolean departuresHandedOff()` |  |
| 472 | 1 | `void departuresHandedOff(boolean done)` |  |
| 473 | 1 | `double departingTaxShares(int c)` |  |
| 474 | 1 | `double departingTaxClaim(int c)` |  |
| 477 | 5 | `double investmentWageRate(Household c)` | Working households use their job band's rate; every other holder uses the wage base. |
| 482 | 1 | `private void receiveInvestmentTax(boolean gain, double amount)` |  |
| 483 | 5 | `private void receiveInvestmentTax(boolean gain, double amount, boolean foreign)` |  |
| 489 | 3 | `void collectForeignTax(int company, double amount)` | Actual collection of an existing claim, not another assessment. |
| 492 | 1 | `void extinguishForeignTax(int company, double amount)` |  |
| 493 | 4 | `private void loseClaim(int company, double amount)` |  |
| 497 | 7 | `private double collectClaim(Household c, int company, double available)` |  |
| 504 | 4 | `private void extinguishClaim(Household c, int company)` |  |
| 510 | 12 | `double realizeGain(Household c, double gain)` | A month's losses offset gains in either fill order; only unused losses survive. |
| 522 | 15 | `double sellShares(Household c, int company, double quantity, double price)` |  |
| 537 | 1 | `public double getCapitalGainsTax()` |  |
| 538 | 1 | `public double getDividendTax()` |  |
| 539 | 1 | `public double getDomesticCapitalGainsTax()` |  |
| 540 | 1 | `public double getForeignCapitalGainsTax()` |  |
| 541 | 1 | `public double getCapitalTaxAssessed()` |  |
| 542 | 1 | `public double getCapitalTaxWrittenOff()` |  |
| 543 | 1 | `public double getCapitalTaxConsolidated()` |  |
| 544 | 1 | `public long getGainAssessments()` |  |
| 545 | 1 | `public long getDividendAssessments()` |  |
| 546 | 1 | `public double getCapitalTaxOutstanding()` |  |
| 547 | 6 | `public double securedClaimCounterpart()` |  |
| 553 | 6 | `public double[] investmentTaxesToSave()` |  |
| 559 | 11 | `public void restoreInvestmentTaxes(double[] state)` |  |
| 570 | 6 | `public double[] securedTaxesToSave()` |  |
| 576 | 9 | `public void restoreSecuredTaxes(double[] state)` |  |
| 588 | 1 | `public void attachCustody(EstateCustody owner, BondMarket market)` |  |
| 591 | 38 | **type** `private final class Deemed` | One transferred fraction, assessed before pooling; no person/estate history is retained. |
| 594 | 1 | `Deemed(Household from, double count, double cash)` _(in HouseholdBalance.Deemed)_ |  |
| 595 | 24 | `Deemed(Household from, double count, double cash, boolean collectCash)` _(in HouseholdBalance.Deemed)_ |  |
| 619 | 4 | `double stock(Stock st, Household from, double count)` _(in HouseholdBalance.Deemed)_ |  |
| 623 | 5 | `void leave()` _(in HouseholdBalance.Deemed)_ |  |
| 638 | 5 | `private static int[] newOwnerLine()` |  |
| 652 | 3 | `public void setOutsideCensus(ToDoubleFunction<Household> census)` |  |
| 664 | 1 | `public void setRentBills(ToDoubleFunction<Household> bills)` |  |
| 666 | 1 | `public void setMoneyUnitSource(java.util.function.DoubleSupplier source)` | The student's large finite treasury allowance is money too, and follows the city's unit. |
| 667 | 4 | `private double rentBill(Household c, double pooled)` |  |
| 671 | 1 | **type** `public record HousingResources(double count, double budget)` |  |
| 673 | 3 | `public double incomingHousingBudget(Household c, double netWage, HouseholdAccounts accounts, double foodPrice)` | An incoming worker starts without the city's existing household debts; all recurring fees and food still have a payer. |
| 676 | 5 | `public double incomingHousingBudget(Household c, double netWage, HouseholdAccounts accounts, double foodPrice, double plannedFa...` |  |
| 682 | 5 | `private static double housingBid(double resources, double committedRent, double factor)` | Saving changes an additional housing bid, while a funded current lease remains committed and cash is the ceiling. |
| 688 | 4 | `public java.util.Map<String, HousingResources> strikeHousingBudgets(FamilyModel families, HouseholdAccounts accounts, double fo...` | Quotes the staged census against paid source income and recurring fees without moving live stocks. |
| 693 | 4 | `public java.util.Map<String, HousingResources> strikeHousingBudgets(FamilyModel families, HouseholdAccounts accounts, double fo...` | Current scheduled wages/pensions precede the later cash strike; old row totals describe last month's people. |
| 698 | 4 | `public java.util.Map<String, HousingResources> strikeHousingBudgets(FamilyModel families, HouseholdAccounts accounts, double fo...` | Live housing reserves the opening card balance's contractual bill at the bank's current price. |
| 703 | 43 | `public java.util.Map<String, HousingResources> strikeHousingBudgets(FamilyModel families, HouseholdAccounts accounts, double fo...` | Housing bids read this month's ex-ante saving response before the later household cash strike. |
| 748 | 22 | `Household[] housingCensusCells(FamilyModel families, double[] fresh)` | A derived census quote: route actual saved stocks on a detached ledger, never arrivals' future cash. |
| 771 | 3 | `public void setRentShares(ToDoubleFunction<Household> shares)` |  |
| 786 | 3 | `public void setOutsideDependants(ToDoubleFunction<Household> kin)` |  |

### THE PRICE AT THE CLINIC DOOR (2026-09-19) (lines 790-869)

| line | len | member | says |
|---:|---:|---|---|
| 839 | 4 | `public void setCareBills(double[] treatmentBilled, double[] treatmentAtFullService)` | Hands the strike the month's treatment bills, by row: what the households were billed (HouseholdAccounts.getRowCareBilled) and what the same rows would have been billed at full service (getRowCareFull). |
| 849 | 10 | `public double carePaidShare(int row)` | Of a row's people, the share who paid for care at the last strike - people-weighted over its cells, 1 for a row with nobody in it. |
| 861 | 5 | `public double[] carePaidShares()` | The same, for every row at once, in row order. |
| 868 | 1 | `public double getCareSkipped()` | What the city's households skipped of their care bills this month, in money - and ate instead. |

### the student loan's rate (2026-09-21) (lines 870-883)

| line | len | member | says |
|---:|---:|---|---|
| 880 | 3 | `public void setStudentLoanRate(double annual)` | Sets the annual rate the graduates are charged this month. |

### the bank's account fee (0.7.7) (lines 884-971)

| line | len | member | says |
|---:|---:|---|---|
| 898 | 1 | `public void setAccountFee(double perHousehold)` |  |
| 899 | 1 | `public double getAccountFee()` |  |
| 911 | 7 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | THE BANK'S CAPITAL RULE FOR THE MONTH (0.7.8), from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(), set by Game at the top of the month before any family borrows: each cell may owe at most what it owes ... |
| 926 | 3 | `void setCardLenderOpenForHarness(boolean open)` | Harness-only lender input for a native window with no card offers or draws. |
| 931 | 1 | `public double lossAllowance()` | What the bank sets aside against the families' debt (0.7.8), summed cell by cell - see Household.lossAllowance(). |
| 934 | 1 | `public double debtInTrouble()` | ...and how much of their debt is in cells that are in trouble. |
| 936 | 3 | `public void setHousedShares(ToDoubleFunction<Household> shares)` |  |
| 941 | 4 | `private double accountFeeFor(Household c, double households)` | The fee one household is charged: every positive weighted household pays for its housed share. |
| 953 | 9 | `public double[] accountFeesByRow(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | What each row's households will be charged in account fees this month, on this census: the figure the household books show on their own line (HouseholdAccounts.setAccountFees()), struck BEFORE the cells settle - the b... |
| 964 | 1 | `public double totalAccountFees()` | What the households paid the bank in account fees this month: the sum the bank takes. |
| 967 | 1 | `public double totalLoanFees()` | ...and the loan fees the bank added to what they owe on the credit they drew this month. |
| 970 | 1 | `public double getStudentLoanRate()` | The annual rate the cells were last told. |

### the month (lines 972-1062)

| line | len | member | says |
|---:|---:|---|---|
| 1012 | 50 | `public HouseholdBalance()` |  |

### EVERY HOUSEHOLD, OR ONE OF THEM (lines 1063-1272)

| line | len | member | says |
|---:|---:|---|---|
| 1071 | 3 | `public Household cell(FamilyStructure shape, PayTier tier)` | The cell for this shape at this tier: for a retired shape, the tier its people retired from (HS1); for a working shape, the established's (HS2: cell(shape, tier, stage) for either). |
| 1076 | 5 | `public Household cell(FamilyStructure shape, PayTier tier, AdultStage stage)` | A working shape's cell at a tier for one adult stage (HS2), or null where the stage forms no such shape; a retired shape's is its cell(shape, tier) whatever the stage. |
| 1083 | 8 | `public java.util.List<Household> stagesOf(FamilyStructure shape, PayTier tier)` | Every working cell of a shape at a tier, the established's first (HS2): what a screen's one cell of the matrix is the sum of. |
| 1097 | 8 | `public java.util.List<Household> cells(FamilyStructure shape)` | A retired shape's cells, one for each tier its people retired from, in tier order (HS1: there was one cell a shape, cell(shape), until the retired kept their former tier). |
| 1107 | 5 | `public double retiredHouseholds(FamilyStructure shape)` | Households of a retired shape, every former tier together. |
| 1119 | 5 | `public double tierShareOf(Household c)` | A retired cell's share of its shape's households, every former tier together, as the last census left them: a sixth each when the shape is empty, so the shares always add to one. |
| 1126 | 3 | `public UnemployedHousehold unemployed(UnemployedHousehold.Status status)` | The out-of-work cell in this situation. |
| 1131 | 1 | `public StudentHousehold students()` | The students' cell. |
| 1134 | 4 | `public OrphanHousehold orphans(AgeBand band)` | The orphans of a child band, or null for a band that has none. |
| 1140 | 1 | `public PrisonerHousehold prisoners()` | The prisoners' ledger. |
| 1143 | 1 | `public OwnerHousehold owners()` | The owner families' ledger: one two-adult household per company with a family. |
| 1146 | 1 | `public void setOwnerLine(boolean on)` | Switches the owner line on or off (BO1); see ownerLine. |
| 1148 | 1 | `public boolean isOwnerLine()` |  |
| 1151 | 5 | `public int ownerFamilies()` | How many companies have an owner family. |
| 1158 | 1 | `public boolean hasOwnerFamily(int company)` | True when this company (Equity.COMPANIES index) has an owner family. |
| 1161 | 7 | `public java.util.Map<String, Integer> ownerLineForSave()` | The line's state for the save: company key to months at the floor, only the companies with a family, in company order. |
| 1170 | 13 | `public void restoreOwnerLine(java.util.Map<String, Integer> line)` | Restores the line from a save; null or a missing key means no family, an unknown key is skipped. |
| 1200 | 22 | `public void tallyOwnersAtFloor(java.util.function.IntPredicate atFloor, java.util.function.IntPredicate listed)` | A YEAR AT THE FLOOR SENDS A FAMILY BACK (BO1; Jerus, 2026-11-02: "if sector goes broke he just joins the regular pop pool"). |
| 1228 | 5 | `public void ownerCompanyFailed(int company)` | A company whose equity was taken (a failed bank's resolution) has nothing left to own: its family leaves the line at once, for the working population (BO1). |
| 1235 | 1 | `public java.util.List<Household> cells()` | Every cell, in the fixed order. |
| 1237 | 1 | `public int cellCount()` |  |
| 1239 | 3 | `public void forEach(Consumer<Household> action)` |  |
| 1244 | 5 | `public double sum(ToDoubleFunction<Household> figure)` | Adds a figure up across every cell. |
| 1251 | 5 | `public double sumRow(int row, ToDoubleFunction<Household> figure)` | ...or across one row: a tier's cells, or the retired. |
| 1258 | 3 | `public double rowHouseholds(int row)` | Households in the row: the denominator of every per-household row figure. |
| 1263 | 9 | `private double perHousehold(int row, ToDoubleFunction<Household> perCell)` | A row total, per household of the row. |

### THE MONTH (lines 1273-1776)

| line | len | member | says |
|---:|---:|---|---|
| 1306 | 303 | `public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, ...` | months owed sit on (Bank.householdRate() since 0.7.7; the city's own borrowing rate before it) |
| 1616 | 5 | `private static double[] padRows(double[] rows)` | A seven-row array - the tiers and the retired, from a caller written before the people outside the families had rows - padded with empty rows (or a ten-row one from before the prisons, an eleven-row one from before th... |
| 1623 | 5 | `private static double sumOfRows(double[] rows)` | The sum of a row array, nothing for no array (HL1: what the rows were handed, against what the cells took). |
| 1638 | 9 | **type** `public interface Census extends ToDoubleBiFunction<FamilyStructure, PayTier>` | A census by adult stage (HS2): the working households of a shape at a tier in one stage. |
| 1639 | 1 | `double households(FamilyStructure shape, PayTier tier, AdultStage stage)` _(in HouseholdBalance.Census)_ |  |
| 1641 | 5 | `default double applyAsDouble(FamilyStructure shape, PayTier tier)` _(in HouseholdBalance.Census)_ |  |
| 1649 | 4 | `static Census byStage(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | A census read by stage: itself when it is one, and otherwise its counts all established. |
| 1655 | 3 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Who lives in each cell now, in cell order: the month's flows planned and not used up, for a reader before the month (the account fees). |
| 1670 | 33 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] after)` | Who lives in each cell now, in cell order. |
| 1710 | 26 | `private double[] retiredMix(double[] after, double[] fresh)` | The tiers a retired shape with nobody in it yet is split by: every retired household's after the flows, by people; in a city with none (a founding), the working census's grown-ups this month, so the first retirees ret... |
| 1747 | 13 | `private double[] splitIncome(double[] fresh, double[] rowTotal)` | A row's take-home, per household of each of its cells. |
| 1762 | 14 | `private double[] splitByPeople(double[] fresh, double[] rowTotal)` | A row's fees, per household of each of its cells, by the people in them. |

### THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS (lines 1777-2204)

| line | len | member | says |
|---:|---:|---|---|
| 1821 | 16 | **type** `private interface Stock` | One stock a household carries, for followThePeople() to move. |
| 1822 | 1 | `double get(Household c)` _(in HouseholdBalance.Stock)_ |  |
| 1823 | 1 | `void set(Household c, double perHousehold)` _(in HouseholdBalance.Stock)_ |  |
| 1825 | 1 | `default boolean notAChildsToClaim()` _(in HouseholdBalance.Stock)_ | A stock an orphan never claims from the pool (HS1's review): a debt, a loan or a car - no credit, no licence. |
| 1827 | 1 | `default int securityCompany()` _(in HouseholdBalance.Stock)_ | The share holding whose distribution this basis follows, or -1 for another stock. |
| 1828 | 1 | `default int securityPart()` _(in HouseholdBalance.Stock)_ |  |
| 1829 | 1 | `default boolean lossStock()` _(in HouseholdBalance.Stock)_ |  |
| 1831 | 1 | `default boolean borrowerStatus()` _(in HouseholdBalance.Stock)_ | Borrower status ends with the borrower; an estate settles the debt separately. |
| 1833 | 1 | `default double bankruptPosition(Household c)` _(in HouseholdBalance.Stock)_ | A charged-off household leaves with no card debt or active card status. |
| 1834 | 1 | `default int custodyKind()` _(in HouseholdBalance.Stock)_ |  |
| 1835 | 1 | `default int bondId()` _(in HouseholdBalance.Stock)_ | 1 USD, 2 cars, 3 city paper |
| 1845 | 151 | `private java.util.List<Stock> stocks(int[] named)` | Every stock a household carries, in the order followThePeople() moves them and foldEmptyCells() folds them. |
| 1998 | 49 | **type** `private final class SecurityRoute` | One existing row/city routing calculation shared by every component of each security. |
| 2002 | 25 | `SecurityRoute(double[] fresh, double[] before, Stock stock)` _(in HouseholdBalance.SecurityRoute)_ |  |
| 2027 | 19 | `void move(double[] fresh, double[] before)` _(in HouseholdBalance.SecurityRoute)_ |  |
| 2047 | 1 | `private static double[] security(Household h, int c)` |  |
| 2048 | 3 | `private static void security(Household h, int c, double[] v)` |  |
| 2051 | 18 | `private boolean spreadSecurity(int company, double[] amount, int group)` |  |
| 2081 | 3 | `private void followThePeople(double[] fresh, double[] buffer, Plan plan)` | Moves every stock with the people, then sets every cell's count. |
| 2085 | 119 | `private void followThePeople(double[] fresh, double[] buffer, Plan plan, boolean quote)` |  |

### POSITIVE WEIGHTED HOUSEHOLDS OWN THEIR POSITIONS (RES3) (lines 2205-2322)

| line | len | member | says |
|---:|---:|---|---|
| 2233 | 1 | `int foldEmptyCells()` | Clears ownerless positions while preserving every positive weighted owner. |
| 2235 | 31 | `private int foldEmptyCells(java.util.List<Stock> stocks)` |  |
| 2274 | 36 | `private boolean spread(Stock s, double amount, int group)` | One stock's amount onto the cells that are not empty, in one stock group (or the whole city, for -1): pro rata to what each holds of it, or by households when none holds any. |
| 2315 | 4 | `boolean holdsAnything(Household c)` | True when the cell holds any stock at all, including a positive fractional owner. |
| 2321 | 1 | `public int getCellsFolded()` | Ownerless cells whose remaining weighted positions were folded this month. |

### MONEY FOLLOWS PEOPLE (HS1, 2026-11-18) (lines 2323-2490)

| line | len | member | says |
|---:|---:|---|---|
| 2432 | 8 | `public void setFlows(PopulationCohorts cohorts)` | Hands the month's flows to the next census (HS1), from the pyramid's own month: the shares of the adults who retired and of the seniors who aged on, of each band who died, the share of every band the month's net migra... |
| 2442 | 4 | `void setFlows(double retire, double ageOn, double dieAdult, double dieSenior, double dieElder, double leave, double evictedLeft)` | The same, by hand: for a harness that causes one flow at a time. |
| 2452 | 15 | `void setFlows(double retire, double ageOn, double dieAdult, double dieSenior, double dieElder, double leave, double evictedLeft...` | ...and with the stages' own (HS2): the share of the established who retire, and of the young who turn 35 and move up to the established cell of their shape and tier. |
| 2468 | 1 | `private static double clampShare(double s)` |  |
| 2471 | 1 | `public double[] flowsToSave()` | The month's flows waiting for the next census, in FLOW_ slot order: for the save. |
| 2474 | 5 | `public void restoreFlows(double[] saved)` | ...and back; null or short (a save from before HS1, or HS2's nine slots) leaves none waiting: the first census back moves money by the pool alone. |
| 2481 | 1 | `public double getFlowHouseholds(int kind)` | Households, by the flow's kind (RETIRING and the rest), that the last census's flows carried out of the cells they left. |
| 2483 | 1 | `public double getFlowSavings(int kind)` | ...and the savings they carried with them. |
| 2485 | 1 | `public double getEstateRepaid()` | The last census's estates: what they repaid the bank, what was written off, and the savings the heirs were handed. |
| 2486 | 1 | `public double getEstateWrittenOff()` |  |
| 2487 | 1 | `public double getEstatePassed()` |  |
| 2489 | 1 | `public double getPoolSavings()` | Savings the census's pool moved between cells at the last census: what no flow explained. |

### the retired's money against the working's (HS1) (lines 2491-3129)

| line | len | member | says |
|---:|---:|---|---|
| 2499 | 1 | `public double getRetiredToWorkingNetWorth()` | Every retired household's net worth, per household, over a working household's. |
| 2501 | 3 | `public double getSeniorsAloneToWorkingNetWorth()` | The seniors living alone, the same way: the cell the study found poorest. |
| 2505 | 3 | `public double getElderCouplesToWorkingNetWorth()` | The elder couples, the same way. |
| 2509 | 15 | `private double netWorthOver(java.util.function.Predicate<Household> which)` |  |
| 2526 | 12 | **type** `private static final class Move` | One flow out of a cell: households out of `from`, into `to` (a cell), or out of the city, or an estate to `tier` - to the heirs' adult stage there first, `heirs` (HS2; -1 for either). |
| 2530 | 3 | `Move(int kind, int from, int to, int tier, double out, double in)` _(in HouseholdBalance.Move)_ |  |
| 2533 | 4 | `Move(int kind, int from, int to, int tier, int heirs, double out, double in)` _(in HouseholdBalance.Move)_ |  |
| 2543 | 5 | **type** `private static final class Plan` | The month's flows, planned off the households coming in: the moves, and every cell's households after them. |
| 2546 | 1 | `Plan(double[] after)` _(in HouseholdBalance.Plan)_ |  |
| 2557 | 85 | `private Plan planFlows(ToDoubleBiFunction<FamilyStructure, PayTier> census, boolean useUp)` | The month's flows as moves between cells, struck off each cell's households coming into the month and the flows setFlows() handed on - used up only for the month itself (useUp), so the account fees struck before it re... |
| 2650 | 11 | `private void dying(Plan plan, int i, double h, double share, PayTier tier)` | The band's dead in one cell: in a couple, one of them, and the household goes whole to the alone cell of its row and tier - the survivor keeps everything; otherwise an estate, their share of the household, to the form... |
| 2669 | 7 | `private static int heirsOf(Household c)` | The adult stage an estate goes to first, in the dead's own tier (HS2): a retiree's to the established, their children's age; an established adult's to the young, theirs; a young adult's to the established, their paren... |
| 2678 | 9 | `private void into(Plan plan, int kind, int i, double households, int grownUps, double[] shares, PayTier tier)` | People going on to the shapes of a band at the tier they left: `households` of cell i, each `grownUps` people, split by the shapes' shares. |
| 2689 | 13 | `private int aloneOf(Household c)` | The alone cell a couple's survivor goes to, in its row and tier: the senior or elder alone, a single adult, or a single parent where there were children. |
| 2709 | 28 | `private double[] shapeShares(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] b0, AgeBand band)` | The shares of a band's people that go to each retired shape holding the band: by the census's own growth in the shape this month, by its grown-ups where nothing grew, by the census where it holds nobody yet, and to th... |
| 2747 | 95 | `private double[] carryTheFlows(Plan plan, java.util.List<Stock> stocks, double[] left, int loanSlot, boolean quote)` | Carries the plan's money (see the banner above): every outflow at its cell's average coming in, then every inflow, then the estates. |
| 2849 | 46 | `private void passOn(double[] e, PayTier tier, AdultStage heirs, double[] b, java.util.List<Stock> stocks, double[] left, boolea...` | An estate, in kind, to the working households of the tier by grown-ups - those of the heirs' adult stage first (HS2), then the tier's either - with none, the city's; with none at all (no heir after the flows), out wit... |
| 2897 | 8 | `private boolean hasHeir(PayTier tier, AdultStage heirs, double[] counts)` | Whether a cell inherits in a pass of passOn(): 0 the tier's heirs' stage, 1 the tier's, 2 the city's working households. |
| 2905 | 6 | `private static boolean heirOf(Household c, double households, int pass, PayTier tier, AdultStage heirs)` |  |
| 2926 | 32 | `private double[] carryGraduatesLoans(double[] before)` | The graduates' student loans, handed to the working families before the census moves anything. |
| 2972 | 3 | `private static int poolWeight(Household c, Stock stock)` | What a household of a cell weighs in the pool: its grown-ups, and a cell with none (the orphans) by its people (OH1). |
| 2988 | 137 | `private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival)` | One stock through the pool: released by the cells that shrank at their own average, claimed by the cells that grew - within the row first, then across the city - and what nobody claimed returned. |
| 3128 | 1 | `public double getSharesTakenAway(int company)` |  |

### THE CARS (2026-09-16) (lines 3130-3208)

### AND THEY BORROW FOR IT (2026-09-17) (lines 3209-3293)

### THE SECOND-HAND MARKET (2026-09-17) (lines 3294-3496)

| line | len | member | says |
|---:|---:|---|---|
| 3368 | 1 | `public double getCarsTakenAway()` |  |
| 3371 | 1 | `public double totalCars()` | Every car in the city. |
| 3384 | 7 | `public double[] commuteWorkersByRow()` | The working cells' commuters by row: households times grown-ups. |
| 3393 | 9 | `public double[] commuteCarsByRow()` | ...and the cars they hold by row, at most one per grown-up: households times the lesser of the two. |
| 3404 | 6 | `public double captiveShare()` | The share of those commuters with no car of their own; 1 with no commuters. |
| 3412 | 4 | `public double carsPerHousehold()` | Cars per household, 0 to 1 - what the road reads. |
| 3427 | 12 | `public double wearOutCars()` | Fifteen years on, every car in the city is scrapped. |
| 3450 | 18 | `public double creditScrappedCars(double total)` | What the Scrap Yards paid for the steel in the cars scrapped this month (0.7.123, batch I4; OldMetal): into savings, each cell its share by the cars it scrapped at the month's top (wearOutCars()) - by households where... |
| 3490 | 6 | `public double carsWanted(double price, double ceiling)` | What the households would buy this month at this price, before the market says how many there are. |

### WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) (lines 3497-3687)

| line | len | member | says |
|---:|---:|---|---|
| 3529 | 49 | `private double wantOf(int i, double price, double ceiling)` | What one cell would buy this month, in whole cars. |
| 3592 | 3 | `public double takeCars(double units, double price, double ceiling)` | Hands out the cars the market actually had, pro rata over who wanted them, and takes the money out of savings. |
| 3625 | 62 | `public double takeCars(double units, double price, double wantPrice, double ceiling)` | Hands out the cars a draw sized at one price brought in at another: each household's want read at {@code wantPrice}, the price the draw was sized at, and its bill at {@code price}, what the draw cost. |

### the second-hand market (lines 3688-3752)

| line | len | member | says |
|---:|---:|---|---|
| 3739 | 13 | `private double offerOf(int i, double floorPrice)` | What one cell would put up for sale this month, in whole cars. |

### GROCERIES AT A PRICE, AND FOOD ASSISTANCE (0.7.43) (lines 3753-3942)

| line | len | member | says |
|---:|---:|---|---|
| 3817 | 1 | `public void setSatiationPrice(double price)` |  |
| 3818 | 1 | `public double getSatiationPrice()` |  |
| 3820 | 3 | `public void setFoodAssistance(double share)` |  |
| 3823 | 1 | `public double getFoodAssistance()` |  |
| 3826 | 1 | `public double getFoodAssistancePaid()` | What the treasury paid toward the groceries at the last sale: every voucher, up to the baskets got at the price charged. |
| 3829 | 1 | `public double getFedByAssistance()` | ...and the baskets that bought, at the price charged. |
| 3836 | 8 | `public double groceryDemandOf(Household c, double price)` | What one of these households would buy at this price, in baskets: the curve in the banner. |
| 3846 | 5 | `public double groceriesWanted(double price)` | What the city's households would buy at this price, in baskets: every cell's demand times its households. |
| 3853 | 5 | `public double groceriesNeeded()` | The baskets the city's households need, one a head: what the demand is a share of. |
| 3874 | 18 | `public void allocateGroceries(double sold, double pAlloc, double pCharged)` | Hands out the baskets the shops sold, and pays the vouchers on them. |
| 3899 | 3 | `public double foodAssistanceFor(Household c, double price)` | The voucher one of these households holds at this shelf price: the dial's share of its baskets at the price, when they would take more than FOOD_ASSISTANCE_MEANS_SHARE of what it has after its fixed bills and its inve... |
| 3904 | 8 | `double foodAssistanceFor(Household c, double price, double share)` | ...at a dial of the caller's: the Policy tab's preview (PolicyPreview.foodAssistanceAt()). |
| 3920 | 8 | `public double foodAssistanceAt(double share, double price)` | What the vouchers would have paid at the last sale at another dial: each eligible household's voucher at that share, up to the baskets it got at the price - the same rule allocateGroceries() pays by, against the baske... |
| 3930 | 5 | `public double householdsEligibleForFoodAssistance(double price)` | Households whose baskets would take more than FOOD_ASSISTANCE_MEANS_SHARE of their means at this price: who a voucher would go to. |
| 3937 | 5 | `public double[] foodAssistanceByRow()` | What the treasury paid toward each row's groceries at the last sale, row by row: the cells that were paid, summed. |

### who goes without, read for the screens (0.7.45) (lines 3943-3987)

| line | len | member | says |
|---:|---:|---|---|
| 3949 | 7 | **type** `public record GroceryRow(int row, double need, double asked, double got, double households, double eligible...` | The baskets one row of households needed at the last sale, asked for at the price, and got; its households, those a voucher would go to at a price, and those a voucher was paid to. |
| 3952 | 1 | `public double gotShare()` _(in HouseholdBalance.GroceryRow)_ | Baskets got per basket needed: 1 is fed. |
| 3954 | 1 | `public double askedShare()` _(in HouseholdBalance.GroceryRow)_ | Baskets asked for at the price per basket needed: under 1 is priced out. |
| 3958 | 17 | `public GroceryRow[] groceriesByRow(double price)` | Every row's groceries at the last sale, with who a voucher would go to at this price (ROWS order). |
| 3977 | 5 | `public double getHouseholdsAssisted()` | The households a voucher was paid to at the last sale. |
| 3984 | 3 | `public double foodAssistanceBasketsAt(double share, double price)` | The baskets the vouchers would have paid for at the last sale at another dial: foodAssistanceAt() at the price, in baskets. |

### THE LUXURY COUNTER (2026-09-17) (lines 3988-4088)

| line | len | member | says |
|---:|---:|---|---|
| 4015 | 4 | `public double luxuriesWanted(double price)` | What the city's households would buy at this price, in whole pieces. |
| 4021 | 5 | `private double luxuryBudget()` | What the city's households would put over a counter, in money. |
| 4037 | 7 | `private double luxuryBudgetOf(Household c)` | What one cell would buy, in whole pieces. |
| 4058 | 30 | `public double takeLuxuries(double units, double price)` | Hands out the pieces the shops actually had, pro rata over who wanted them, and takes the money out of savings. |

### THE TABLE (2026-09-18) (lines 4089-4389)

| line | len | member | says |
|---:|---:|---|---|
| 4107 | 16 | `public double mealsWanted(double price)` | What the city's households would eat out at this price, in whole meals. |
| 4125 | 5 | `private double mealBudget()` | What the city's households would put on a table, in money. |
| 4144 | 7 | `private double mealBudgetOf(Household c)` | What one cell would spend eating out. |
| 4163 | 28 | `public double takeMeals(double meals, double price)` | Hands out the meals the kitchens actually served, pro rata over who wanted them, takes the money out of savings and REMEMBERS THE MEALS. |
| 4199 | 4 | `public void clearMealsBought()` | The month starts with no meals bought (FW6, 2026-10-25). |
| 4205 | 1 | `public double getMealsEaten()` | Meals the city ate out in the month the hunger measure just read. |
| 4208 | 1 | `public double getMealsBought()` | Meals the households ate out this month. |
| 4211 | 1 | `public double getMealSpend()` | ...and what they paid for them. |
| 4216 | 4 | `public void clearLuxuriesBought()` | ...and none of the pieces (FW6, 2026-10-25): see clearMealsBought(), and the counter's shop(). |
| 4222 | 1 | `public double getLuxuriesBought()` | Pieces the households took this month. |
| 4225 | 1 | `public double getLuxurySpend()` | ...and what they paid for them. |
| 4228 | 1 | `public double getLuxuryWant()` | What every household in the city would like to put over a counter this month. |
| 4231 | 5 | `public double carsOffered(double floorPrice)` | What the city's households would put up for sale at this floor. |
| 4259 | 72 | `public double clearUsedCars(double newPrice, double ceiling)` | Clears the month's second-hand market: strikes a price, moves the cars that find a buyer, and pays the households that sold them. |
| 4336 | 1 | `public double getUsedCarsOffered()` | Cars put up for sale this month, whether or not anybody took them. |
| 4339 | 1 | `public double getUsedCarsTraded()` | ...and the ones that found a buyer. |
| 4342 | 1 | `public double getUsedCarPrice()` | What they went for. |
| 4355 | 3 | `public double getUsedCarShare()` | ...as a share of what a NEW one cost the month it was struck, which is the only honest denominator and is between USED_CAR_FLOOR and USED_CAR_CEILING by construction. |
| 4360 | 1 | `public double getUsedCarSpend()` | What the buyers paid, all in. |
| 4366 | 1 | `public double getUsedCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month. |
| 4369 | 1 | `public double getUsedCarLoanFees()` | ...and the bank's fee on that, added to what the buyers owe (0.7.7). |
| 4376 | 1 | `public double getCarsBought()` | Cars the households actually took this month. |
| 4383 | 1 | `public double getCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month or the money audit sees a pool fall for no reason. |
| 4386 | 1 | `public double getCarLoanFees()` | ...and the bank's fee on it, added to what the buyers owe rather than paid (0.7.7). |

### THE MARKET (lines 4390-4458)

| line | len | member | says |
|---:|---:|---|---|
| 4403 | 6 | `public void setMarket(Exchange exchange, Equity register, Bank bank)` |  |
| 4415 | 15 | `private void rebuildLiquidity()` | The waterfall's two sales, in one object: the city's paper to the bank's desk first (0.7.1), then shares on the exchange. |
| 4437 | 1 | `Household[] cellsForMarket()` | The cells, for the exchange and the bond market to post their orders from (0.7.12 round 2: each household type is its own participant). |
| 4440 | 5 | `public void splitShares(int company, double k)` | A split or consolidation: every household's count of the company by the factor. |
| 4447 | 4 | `public double marketValueOfShares()` | What the households' shares are worth at the exchange's price - the last trade on each book, or fair value before one. |
| 4453 | 5 | `public double totalSold()` | What the households raised this month selling shares to cover the shop. |

### THE WORLD'S PAPER (lines 4459-4476)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) (lines 4477-4675)

| line | len | member | says |
|---:|---:|---|---|
| 4513 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |
| 4533 | 1 | `boolean owes(Household c)` | WHETHER A CELL OWES ANYTHING, READ AT THE SAME FLOOR (0.7.12 round 6). |
| 4536 | 6 | `public double debtFreeShare(Household c)` | Share whose cash may buy investments: cards/exclusion belong to their holders, not every neighbour (HC1). |
| 4546 | 3 | `public void setExchangeRate(double localPerUsd)` |  |
| 4550 | 1 | `public double getExchangeRate()` |  |
| 4556 | 1 | `static double foreignCouponUsd(double dollars, double worldRate)` | The existing foreign coupon is paid home; the USD principal stays abroad. |
| 4570 | 71 | `public void investAbroad(double depositRate, double worldRate, double rate)` | Every household decides where to keep its idle money: what is past the cushion goes abroad towards the target share, a little a month; what is abroad comes home when the target falls. |
| 4643 | 5 | `public double totalAbroadUsd()` | Dollars every household holds abroad. |
| 4650 | 1 | `public double totalAbroadValue()` | ...worth this much at home, at the month's rate. |
| 4653 | 5 | `public double getSentAbroad()` | Sent abroad this month, all households, local money. |
| 4660 | 5 | `public double getBroughtHome()` | Brought home this month - to keep to the target or to eat - all households, local money. |
| 4667 | 5 | `public double getForeignInterest()` | What the world paid the households this month, rolled abroad. |
| 4674 | 1 | `public double getAbroadTakenAway()` | Dollars that left with the households that left this month. |

### THE CITY'S PAPER, AT HOME (0.7.1) (lines 4676-4925)

| line | len | member | says |
|---:|---:|---|---|
| 4729 | 4 | **type** `public interface PaperDesk` | Where the households' paper is sold, for what, and what it does to the paper on the other side. |
| 4731 | 1 | `void buy(double face, double cash, double heldBefore)` _(in HouseholdBalance.PaperDesk)_ | The desk buys face / heldBefore of the opening household book for this much cash. |
| 4736 | 4 | `public void setPaperDesk(PaperDesk desk)` |  |
| 4751 | 3 | `public void setPaperRatio(double ratio)` |  |
| 4755 | 1 | `public double getPaperRatio()` |  |
| 4768 | 3 | `public void setSpendFactor(double factor)` |  |
| 4773 | 1 | `public double getSpendFactor()` | The factor the last plan was struck at. |
| 4776 | 1 | `public double totalPaper()` | Every cell's paper, at face - DebtManager.householdPrincipal() from the other side. |
| 4779 | 1 | `public double marketValueOfPaper()` | ...at this month's market value. |
| 4782 | 5 | `public double totalPaperSold()` | Sold to the desk this month, all households, in cash. |
| 4789 | 5 | `public double totalPaperIncome()` | Coupons and principal the city paid the households on their paper this month. |
| 4795 | 3 | `private boolean mayBuyPaper(Household c)` |  |
| 4799 | 3 | `private double spareFor(Household c)` |  |
| 4804 | 5 | `public double spareForPaper()` | What every eligible household could put into paper: savings past the cushion, in total. |
| 4816 | 17 | `public double buyAtIssue(double cash, double facePerCash)` | The households buy at issue: this much cash in total, from each eligible cell pro rata to what is past its cushion, for this much face per dollar. |
| 4835 | 3 | `private double paperWeight(Household c, double total)` | Pro rata to the paper held: what a dollar of face in the households' book is spread as, cell by cell. |
| 4840 | 13 | `public void creditPaperCoupon(double total)` | A coupon on the households' share, into savings and the month's investment income, pro rata to the paper. |
| 4855 | 13 | `public void creditPaperPrincipal(double face)` | Principal on their share, into savings with the paper down by the same, pro rata. |
| 4876 | 10 | `public void creditPaperBuyback(double face, double price)` | A bond bought back: this much of their face off the paper, this much of the price into savings, pro rata. |
| 4888 | 10 | `private double sellPaperToDesk(Household c, double needPer)` | One cell sells to the desk for up to needPer a household. |
| 4908 | 17 | `public double sellPaperForSpread(double bookYield, double depositRate)` | THE SPREAD HAS GONE: when the households' paper yields no more than the bank pays savers, the target for it is nothing, and they sell a little a month - HOME_SPEED of it, the pace the dollars abroad come home at - int... |

### THE BUSINESSES' BONDS, AT HOME (0.7.12) (lines 4926-5191)

| line | len | member | says |
|---:|---:|---|---|
| 4943 | 4 | `public void setBondMarket(BondMarket market)` |  |
| 4956 | 3 | `public void setBondRatio(double ratio)` |  |
| 4960 | 1 | `public double getBondRatio()` |  |
| 4964 | 1 | `public double getBondsTakenAway()` |  |
| 4967 | 1 | `public double totalBonds()` | Every cell's bonds, at face: the households' face in every bond. |
| 4970 | 1 | `public double marketValueOfBonds()` | ...at this month's value. |
| 4973 | 5 | `public double totalBondsSold()` | Sold this month, in cash, and the coupons and principal received. |
| 4979 | 5 | `public double totalBondIncome()` |  |
| 4986 | 1 | `public double getBonds(int row)` | One row's bonds, per household of the row. |
| 4989 | 5 | `public double bondFaceHeld(int id)` | Every cell's face in one bond, together. |
| 4996 | 7 | `public Household cellByKey(String key)` | The cell with this key, or null - from an index built once, because the markets ask on every fill. |
| 5006 | 1 | `public double bondSpare()` | What every eligible household could put into bonds: savings past the cushion - the city's paper's rule. |
| 5009 | 1 | `public double bondSpare(Household c)` | ...one cell's, every household of it: nothing for a cell that may not buy (the city's paper's eligibility). |
| 5018 | 19 | `public double payForBonds(int id, double cash, double face)` | A new issue's households' part, at issue: this much cash out of every eligible cell's savings past its cushion, pro rata, for this much face of the bond, which each cell holds in the same proportion. |
| 5049 | 30 | `public double creditBondCoupons(java.util.Map<String, Double> dueByCell)` | THE MONTH'S COUPONS, to the cells that held the bonds at the record date (BondMarket.strikeCoupons()): into savings and the month's investment income. |
| 5081 | 13 | `public double creditBondPrincipal(int id)` | One bond's principal at maturity: every cell paid its own face of it, into savings, and the bond gone from it. |
| 5101 | 14 | `public void writeDownBonds(java.util.Collection<Integer> ids, double keep)` | A default on one issuer's bonds together: every cell's face of each down to this share, and its total recounted once - a slice writes an issuer's bonds down every month it is past the curve's floor, and a recount per ... |
| 5117 | 12 | `public double writeDownBonds(int id, double keep)` | A default on one bond: every cell's face of it down to this share. |
| 5133 | 10 | `public java.util.Map<String, java.util.Map<String, Double>> bondsByCellToSave()` | Every cell's bonds, per household, by the cell's key and the bond's id: the save's (0.7.12 round 2). |
| 5149 | 16 | `public void restoreBondsByCell(java.util.Map<String, java.util.Map<String, Double>> saved)` | ...back, by name: a cell this build does not have is dropped, as the cell arrays' rule is, and a bond id that does not parse is skipped. |
| 5176 | 15 | `public void claimPooledBonds(java.util.Map<Integer, Double> poolById)` | A SAVE FROM ROUND 1 held the households' bonds as one pool, each cell with a claim on it at face (Household.bonds): the pool is handed to the cells by those claims - every bond's households' face split across the cell... |

### the people outside the families (lines 5192-5266)

| line | len | member | says |
|---:|---:|---|---|
| 5195 | 1 | `public double getEvicted()` | Out-of-work households that lost their home this month. |
| 5198 | 1 | `public double totalStudentBorrowed()` | Student loans drawn this month, all students - the treasury's money out. |
| 5201 | 1 | `public double totalStudentRepaid()` | ...and repaid by graduates, the treasury's money back: principal, which is what the journal's line carries. |
| 5204 | 1 | `public double totalStudentInterest()` | ...and the interest the graduates paid on top of it this month: the treasury's revenue. |
| 5207 | 3 | `public double studentInterestAt(double annualRate)` | What a month's interest would come to at an annual rate, on the balances that are charged it - for a screen previewing a rate. |
| 5212 | 1 | `public double totalStudentDebt()` | What every household owes the treasury in student loans. |
| 5215 | 3 | `public double totalGraduateDebt()` | ...and the part of it that is in repayment: the graduates' balances, which the rate is charged on. |
| 5220 | 1 | `public double getStudentDebtTakenAway()` | Student loans that left the city with graduates who left. |
| 5241 | 1 | `public void setGraduates(double people)` | THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS. |
| 5244 | 1 | `public double getLastGraduated()` | Students who left the student body with what they carried, at the last month's census. |
| 5247 | 1 | `public double getGraduating()` | The students who finished and wait for the next census to carry their loans (setGraduates()). |
| 5258 | 1 | `public double[] graduatesToSave()` | ...AND BOTH ACROSS A SAVE (0.7.63). |
| 5261 | 5 | `public void restoreGraduates(double[] saved)` | ...and back; null, a save from before 0.7.63, leaves both at nothing, as every load until then did. |

### what the bank is owed (lines 5267-5443)

| line | len | member | says |
|---:|---:|---|---|
| 5270 | 1 | `public double getWrittenOff()` | Written off this month, which is the bank's loss. |
| 5273 | 1 | `public double getSeizedAtBankruptcy()` | What the month's bankrupt households' savings repaid the bank with, before the rest was written off (HL1): already in totalRepaid(). |
| 5276 | 1 | `public double getTakenAway()` | Savings that left the city with the households that left. |
| 5279 | 1 | `public double getArrivedWith()` | What the month's newcomers brought as their opening buffer (OPENING_BUFFER_MONTHS): money from abroad into the savings. |
| 5282 | 1 | `public double getUnfunded()` | Bills the sellers booked in full and the households could not fund, this month, summed over the households (HL1: an open fault, named for HL2). |
| 5285 | 1 | `public double getUnsettledIncome()` | Income the month's rows were handed with no positive recipient weight (HL1 reconciliation). |
| 5288 | 1 | `public double getUnsettledFees()` | Fees the month's rows were billed that no settled cell was charged (HL1: an open fault, named for HL2). |
| 5291 | 1 | `public double getUnsettledShopping()` | Shopping the month's rows were billed for that no settled cell paid (HL1: an open fault, named for HL2). |
| 5294 | 1 | `public double getUnsettledRent()` | Rent booked without a positive household recipient (HL1 reconciliation). |
| 5297 | 1 | `public double getCensusStranded()` | Savings the census left in a pool nobody is handed (HL1: an open fault, named for HL2). |
| 5300 | 1 | `public double getCensusCreated()` | Savings the census made, a growing cell with no grown-ups keeping its per-household savings on more households (HL1: an open fault, named for HL2). |
| 5303 | 1 | `public double getBankrupt(int row)` | Households discharged this month, and the share of them leaving the city. |
| 5304 | 1 | `public double getLeavingCity()` |  |
| 5307 | 3 | **type** `public record BankruptcyDepartures(double[] ages, double[] stages)` | The actual source families, rather than a city-wide average household size. |
| 5308 | 1 | `public double people()` _(in HouseholdBalance.BankruptcyDepartures)_ |  |
| 5312 | 28 | `public BankruptcyDepartures prepareBankruptcyDepartures(PopulationCohorts cohorts)` | Bound one source-preserving departure plan to the people still alive this month. |
| 5342 | 9 | `public BankruptcyDepartures limitBankruptcyDepartures(BankruptcyDepartures plan, double admitted)` | Migration may cap total departures; apply that same cap to every source and its cash. |
| 5353 | 1 | `public boolean isLockedOut(int row)` | True when any cell of the row the bank has stopped lending to is still locked out. |
| 5356 | 7 | `public int getLockout(int row)` | Legacy row indicator: a shut-out share exists; the actual position is Household.shutOut(). |
| 5365 | 1 | `public double bookOwed()` | Everything the households owe the bank. |
| 5374 | 21 | `void censusOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | The census alone (HS1): the month's flows used up, the pool, the fold - no month settled and nobody arriving with a buffer, so a harness reads what the people carried to the cent (HouseholdFlowCheck). |
| 5412 | 31 | `public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, doub...` | The plan alone, without settling a month. |

### reading (lines 5444-5546)

| line | len | member | says |
|---:|---:|---|---|
| 5447 | 1 | `public double getSpendingCapacity()` | What the shops can sell next month, in money. |
| 5450 | 1 | `public double getWantedSpend()` | What they would spend if money were no object - the demand behind the cap. |
| 5453 | 1 | `public double getSubsistence(int row)` | One basket a head, per household of the row: what going short is measured against. |
| 5466 | 12 | `public double[] plannedShare()` | The share of each row's groceries, for splitting what retail actually sold back across the rows. |
| 5480 | 3 | `public double getHungerRate()` | Share of the city going short of food, 0-1. |
| 5484 | 1 | `public double getHungryPeople()` |  |
| 5492 | 3 | `public double getHungerPricedOut()` | Of getHungerRate(), the share of the city short of a basket that could not afford one at the price - the money half, at full shelves - and the share the shelves left short (0.7.45): the two add to the rate. |
| 5497 | 1 | `public double getHungerShortOfStock()` | ...and the rest of it: the shelves ran short. |
| 5508 | 1 | `public double getHungryAtFullShelves()` | Of getHungryPeople(), the ones who would have gone hungry even with the shelves full: the baskets they asked for at the price (0.7.43; their own plan until then), every one handed over, came up short of a basket - the... |
| 5520 | 1 | `public double getDeliveredShare()` | The share of what households asked for at the price (0.7.43; what they planned to buy until then) that the shops could hand over - and therefore which of the two hungers is biting. |
| 5533 | 13 | `public void creditDepositInterest(double total)` | Interest the bank paid on what these households have saved. |

### THEFT (2026-09-11) (lines 5547-5625)

| line | len | member | says |
|---:|---:|---|---|
| 5566 | 16 | `public double takeFromSavings(double total)` | Takes up to `total` from the households' savings, each cell in proportion to what it has saved. |
| 5596 | 8 | `public double creditableWeight(double[] weight)` | Of a weight per cell, in the cells' order, the part creditByWeight would pay: the cells with households in them (HS2, 2026-11-19). |
| 5611 | 11 | `public double creditByWeight(double total, double[] weight)` | Credits `total` to the cells in proportion to a weight per cell, in the cells' order - what the offenders' households took home. |
| 5624 | 1 | `public double getDepositInterest()` | What the bank paid the city's savers this month. |

### THE OFFER (lines 5626-5933)

| line | len | member | says |
|---:|---:|---|---|
| 5658 | 24 | `public double subscribe(int company, double offered, double price)` | Puts an offering to every household, and takes up what they will buy. |
| 5688 | 5 | `public double offeringMeans()` | What the households would put into an offering today, in all (REV-FC2b): subscribe()'s own weighing, cell by cell, of a share (SHARE_OF_EXCESS) of what a cell that may buy shares holds past SHARE_CUSHION_MONTHS of its... |
| 5695 | 9 | `private double offeringWant(Household c)` | One cell's want for an offering, as subscribe() weighs it: nothing for a cell that may not, or will not, buy. |
| 5727 | 3 | `public boolean grantFounders(int company, double shares)` | Hands out a company's founding shares to the people who founded it. |
| 5731 | 20 | `public boolean grantFounders(int company, double shares, double price)` |  |
| 5757 | 19 | `public double creditDividend(int company, double perShare)` | Pays every household its dividend, straight into its savings. |
| 5788 | 13 | `public double creditLendingFee(int company, double perShare)` | Pays every household the lending fee on the shares of one company it lends, per share it holds, straight into its savings (EM2): the world pays it for the shares it sold short, which the cells lend pro rata to what ea... |
| 5812 | 16 | `public double surrenderShares(int company)` | EVERY CELL GIVES UP ITS SHARES OF ONE COMPANY, for nothing: a failed bank's resolution (0.7.14; Equity.takeAllForCity() - the old owners are wiped out, CDIC). |
| 5830 | 5 | `public double sharesHeld(int company)` | Shares of this company the city's households hold between them. |
| 5837 | 5 | `public double totalDividends()` | What the households were paid in dividends this month. |
| 5845 | 1 | `public double getSavings(int row)` | ---- the row, per household of it: what the screens and the fixtures read ---- |
| 5848 | 1 | `public double getHouseholds(int row)` | Households this row was struck for - the multiplier on every per-row figure. |
| 5849 | 1 | `public double getDebt(int row)` |  |
| 5850 | 1 | `public double getAfterFixed(int row)` |  |
| 5852 | 1 | `public double getDisposable(int row)` | What one household of the row had to spend this month, from its own ledger (0.7.27: Policy's pension lines foot on it). |
| 5854 | 1 | `public double getFixedCosts(int row)` | ...and its rent, fees and interest: what came off that before the shop - the ledger's own, so the three foot. |
| 5855 | 1 | `public double getInterest(int row)` |  |
| 5871 | 14 | `public double disposableWithRowMoved(int row, double rowChange)` | What one household of a row would have had to spend with the row's income moved by `rowChange` (0.7.36, the Policy spec's M11): the move split across the row's cells by advanceMonth()'s own rule - splitIncome(), by ea... |
| 5885 | 1 | `public double getDrawn(int row)` |  |
| 5888 | 1 | `public double getUnfunded(int row)` | What this row wanted, could not fund, and did not get. |
| 5891 | 4 | `public boolean isCutOff(int row)` | True when the bank has stopped lending to any cell of this row - ceiling or lockout. |
| 5895 | 1 | `public double getBorrowed(int row)` |  |
| 5896 | 1 | `public double getRepaid(int row)` |  |
| 5897 | 1 | `public double getBanked(int row)` |  |
| 5898 | 1 | `public double getWant(int row)` |  |
| 5899 | 1 | `public double getPlanned(int row)` |  |
| 5900 | 1 | `public double getRate(int row)` |  |
| 5903 | 4 | `public boolean isGoingShort(int row)` | True when this row is buying less food than it wants. |
| 5909 | 1 | `public double totalSavings()` | City totals, for the headline lines on the screen. |
| 5910 | 1 | `public double totalDebt()` |  |
| 5911 | 1 | `public double totalInterest()` |  |
| 5920 | 4 | `public double averageRate()` | What the families' credit lines cost them this month on average: each cell's rate weighted by what it owes - the bank's household rate plus RISK_SLOPE for every month of income owed, capped at MAX_RATE (Household.sett... |
| 5926 | 1 | `public double totalBorrowed()` | New lending to families this month - the bank's money out the door. |
| 5929 | 1 | `public double totalRepaid()` | ...and what came back: the cells' repayments and the estates' (HS1, the dead's debt settled from their savings). |
| 5932 | 1 | `public double totalNetWorth()` | What every household in the city has, less what it owes. |

### saving (lines 5934-6555)

| line | len | member | says |
|---:|---:|---|---|
| 5954 | 44 | `public double[] toSaveArray()` | The row array an older build reads: ROWS*8+5 since 0.7.27 (ROWS*8+3 before), per household of the row. |
| 6097 | 3 | `public double totalEmergencyBills()` | The name of every cell, in the order toCellSaveArray() writes them. |
| 6101 | 1 | `public DebtStatistics.Snapshot debtStatistics()` |  |
| 6107 | 1 | `public void setCarAssetPrice(java.util.function.DoubleSupplier price)` | The city's current used-car mark, injected like its rent/census readers. |
| 6110 | 1 | `public void setAssetExchangeRate(java.util.function.DoubleSupplier rate)` | Closing exchange rate for valuing assets, distinct from the settlement's opening rate. |
| 6113 | 3 | `public double assets(Household c)` | The assets this model has: financial holdings and cars; owner homes arrive with HO1. |
| 6118 | 8 | `public double financialAssets(Household c)` | A cell's financial assets at this month's marks, per household (HC2). |
| 6127 | 5 | `public String[] cellKeys()` |  |
| 6134 | 46 | `public double[] toCellSaveArray()` | CELL_SLOTS per cell, in cellKeys() order, then the three city figures. |
| 6192 | 173 | `public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies)` | Puts the cells back, by name. |
| 6384 | 58 | `public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Puts a ROW array back, seeding every cell of the row with the row's position: the save from a build that kept the stocks per tier. |
| 6451 | 8 | `private void seedGroceries(Household c)` | The last sale's baskets for a household from a save before 0.7.43, which carries the money measure its hunger was struck on: its plan times the share the shops handed over, against subsistence - turned into baskets by... |
| 6461 | 1 | `public void restore(double[] saved)` | The row array alone, with no census: the cells wait for the plan to count them. |
| 6463 | 49 | `public void reset()` |  |
| 6519 | 35 | `public void redenominate(double scale)` | The households' stocks and this month's working, in the new unit. |

