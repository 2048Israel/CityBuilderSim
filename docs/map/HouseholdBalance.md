# HouseholdBalance.java - 5,631 lines · 298 methods · 51 constants · model

`ham/citybuildersim/HouseholdBalance.java` - generated 2026-11-20 by CodeMap; line numbers are as of that run.

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
> sixty-eight objects (ninety-nine since HS1) this class holds, sums and
> strikes; the row getters the screens read are sums of the cells, and the
> tier is a view rather than the unit.
> 
> ... (23 more lines in the source)

**Uses:** [Household](Household.md) (238), [PayTier](PayTier.md) (38), [FamilyStructure](FamilyStructure.md) (36), [AgeBand](AgeBand.md) (29), [Equity](Equity.md) (23), [UnemployedHousehold](UnemployedHousehold.md) (10), [WorkingHousehold](WorkingHousehold.md) (9), [RetiredHousehold](RetiredHousehold.md) (6), [OwnerHousehold](OwnerHousehold.md) (5), [OutwardInvestment](OutwardInvestment.md) (5), [Bank](Bank.md) (4), [StudentHousehold](StudentHousehold.md) (3), [OrphanHousehold](OrphanHousehold.md) (3), [PrisonerHousehold](PrisonerHousehold.md) (3), [TaxPolicy](TaxPolicy.md) (2), [Restaurants](Restaurants.md) (2), [Exchange](Exchange.md) (2), [Debt](Debt.md) (2), [BondMarket](BondMarket.md) (2), [PopulationCohorts](PopulationCohorts.md) (1), [Retail](Retail.md) (1), [PriceLevel](PriceLevel.md) (1), [ForeignAccounts](ForeignAccounts.md) (1)

**Used by (62):** [Auction](Auction.md), [AuctionCheck](AuctionCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BondCheck](BondCheck.md), [BondMarket](BondMarket.md), [BusinessServicesCheck](BusinessServicesCheck.md), [Buyback](Buyback.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CarCheck](CarCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityBasket](CityBasket.md), [CoalMiningCheck](CoalMiningCheck.md), [CopperMiningCheck](CopperMiningCheck.md), [CrimeCheck](CrimeCheck.md), [DenominationCheck](DenominationCheck.md), [EducationCheck](EducationCheck.md), [Equity](Equity.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FloatLinkCheck](FloatLinkCheck.md), [ForestryCheck](ForestryCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [GroceryCheck](GroceryCheck.md), [HealthCheck](HealthCheck.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [Household](Household.md), [HouseholdCheck](HouseholdCheck.md), [HouseholdFlowCheck](HouseholdFlowCheck.md), [KeptBench](KeptBench.md), [LongPlaytest](LongPlaytest.md), [LuxuryCounter](LuxuryCounter.md), [LuxuryRetailCheck](LuxuryRetailCheck.md), [MarketCheck](MarketCheck.md), [MaterialsCheck](MaterialsCheck.md), [MoneyAudit](MoneyAudit.md), [Motoring](Motoring.md), [Offending](Offending.md), [OutsideCheck](OutsideCheck.md), [OwnerCheck](OwnerCheck.md), [PeopleScreen](PeopleScreen.md), [Pieces](Pieces.md), [PolicyDatesCheck](PolicyDatesCheck.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [PrizeCheck](PrizeCheck.md), [QuarryingCheck](QuarryingCheck.md), [ReadPathCheck](ReadPathCheck.md), [RestaurantsCheck](RestaurantsCheck.md), [Retail](Retail.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md), [ShadowBasket](ShadowBasket.md), [ShortSellingCheck](ShortSellingCheck.md), [TradeScreen](TradeScreen.md), [WoodProductsCheck](WoodProductsCheck.md)

## Sections

| line | section |
|---:|---|
| 103 | · the dials |
| 158 | AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT |
| 223 | ...AND WHAT IT SPENDS EATING OUT (2026-09-18) |
| 249 | AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) |
| 265 | AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) |
| 294 | AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) |
| 368 | BANKRUPTCY |
| 411 | · the cells |
| 491 | THE PRICE AT THE CLINIC DOOR (2026-09-19) |
| 571 | · the student loan's rate (2026-09-21) |
| 595 | · the bank's account fee (0.7.7) |
| 672 | · the month |
| 754 | EVERY HOUSEHOLD, OR ONE OF THEM |
| 947 | THE MONTH |
| 1047 | · ...AND A MEAL OUT IS FOOD (2026-09-18) |
| 1425 | THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS |
| 1678 | A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) |
| 1789 | MONEY FOLLOWS PEOPLE (HS1, 2026-11-18) |
| 1935 | · the retired's money against the working's (HS1) |
| 2320 | · EVERY SHARE HERE IS A FRACTION, AND IS HELD TO BEING ONE (2026-09-16) |
| 2440 | THE CARS (2026-09-16) |
| 2519 | AND THEY BORROW FOR IT (2026-09-17) |
| 2604 | THE SECOND-HAND MARKET (2026-09-17) |
| 2807 | WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) |
| 2963 | · the second-hand market |
| 3028 | GROCERIES AT A PRICE, AND FOOD ASSISTANCE (0.7.43) |
| 3218 | · who goes without, read for the screens (0.7.45) |
| 3263 | THE LUXURY COUNTER (2026-09-17) |
| 3364 | THE TABLE (2026-09-18) |
| 3665 | THE MARKET |
| 3734 | THE WORLD'S PAPER |
| 3752 | THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) |
| 3842 | · THE COUPON IS PAID HOME, NOT ROLLED (2026-09-17) |
| 3939 | THE CITY'S PAPER, AT HOME (0.7.1) |
| 4187 | THE BUSINESSES' BONDS, AT HOME (0.7.12) |
| 4453 | · the people outside the families |
| 4528 | · what the bank is owed |
| 4660 | · reading |
| 4761 | THEFT (2026-09-11) |
| 4821 | THE OFFER |
| 5092 | · saving |
| 5303 | · THE SLOT COUNT ALONE DOES NOT SAY WHAT THE SLOTS ARE (2026-09-12) |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `HouseholdBalance.ROWS` | `Household.ROWS` | One row per pay tier, the retired, and since 2026-09-11 the out of work, the students and the orphans - the same shape as HouseholdAccounts. |
| 101 | `HouseholdBalance.ROWS_BEFORE_OUTSIDE` | `Household.RETIRED_ROW + 1` | The rows before the people outside the families had books: the six tiers and the retired. |
| 115 | `HouseholdBalance.CREDIT_LIMIT_MONTHS` | `6` | How many months of take-home a household can owe before the lender stops. |
| 134 | `HouseholdBalance.RISK_SLOPE` | `.015` | Extra spread per month of income owed. |
| 137 | `HouseholdBalance.MAX_RATE` | `.36` | Nobody is charged more than this, however deep they are. |
| 156 | `HouseholdBalance.MARGINAL_PROPENSITY` | `.80` | How much of the money above subsistence a household spends. |
| 201 | `HouseholdBalance.WEALTH_SPENT_A_MONTH` | `.0033` | What share of its net worth a household spends in a month, over and above what it spends out of income. |
| 221 | `HouseholdBalance.LUXURY_SHARE_OF_SURPLUS` | `.5` | What share of the income a household does NOT spend on food goes over a luxury counter instead of into the bank. |
| 236 | `HouseholdBalance.MEAL_SHARE_OF_SURPLUS` | `.25` |  |
| 247 | `HouseholdBalance.MEAL_SHARE_OF_WEALTH` | `.5` | ...and the share of a FORTUNE'S monthly spend that goes on a table. |
| 263 | `HouseholdBalance.MOST_MEALS_EATEN_OUT` | `1 / 3.0` |  |
| 338 | `HouseholdBalance.SAVING_RESPONSE` | `1.0` | How hard a household's spending above subsistence answers the real deposit rate: at 1.0 ten points of real return cut it by a tenth and ten points of negative real return raise it by a tenth (provisional - Jerus's num... |
| 341 | `HouseholdBalance.SPEND_FLOOR` | `.5` | The least share of its spending above subsistence a household keeps however well saving pays: half - no real return makes a household spend nothing above a basket a head. |
| 344 | `HouseholdBalance.SPEND_CEILING` | `1.5` | The most it spends however badly saving pays: half as much again - no negative real return makes a household spend without limit. |
| 366 | `HouseholdBalance.OPENING_BUFFER_MONTHS` | `1.5` | A month's savings a founding city's households already have. |
| 400 | `HouseholdBalance.BANKRUPT_AT_MONTHS` | `CREDIT_LIMIT_MONTHS *.98` | Months of income owed at which a household stops being able to carry it. |
| 403 | `HouseholdBalance.BANKRUPT_RATE` | `.04` | Share of a stuck cell that goes under in a month. |
| 406 | `HouseholdBalance.LOCKOUT_MONTHS` | `12` | Months a discharged household cannot borrow. |
| 409 | `HouseholdBalance.LEAVE_ON_BANKRUPTCY` | `.25` | ...and the share of them who give up on the city entirely. |
| 1713 | `HouseholdBalance.EMPTY_CELL` | `.5` | Fewer households than this in a cell and it is empty: it holds nothing and is paid nothing. |
| 1862 | `HouseholdBalance.FLOW_RETIRE` | `0, FLOW_AGE_ON = 1, FLOW_DIE_ADULT = 2, FLOW_DIE_SENIOR = 3, FLOW_DIE_ELDER =...` | Slots of the month's flows, as setFlows() strikes them and flowsToSave() writes them: the shares of a band, and the evicted who left, a share of the out of work off benefits: every slot a share, so a scaled city's sav... |
| 1869 | `HouseholdBalance.RETIRING` | `0, AGEING_ON = 1, PARTNER_DIES = 2, DYING_ALONE = 3, LEAVING = 4, FLOW_KINDS = 5` | The kinds of flow, for the month's record (getFlowHouseholds(), getFlowSavings()). |
| 1979 | `HouseholdBalance.OUT_OF_THE_CITY` | `- 1, AN_ESTATE = - 2` | A Move's destination when it leaves the city, or is an estate. |
| 2477 | `HouseholdBalance.CAR_LIFE_MONTHS` | `180` | How long a car lasts. |
| 2489 | `HouseholdBalance.CAR_ADOPTION` | `.02` | What share of the households who have never owned a car buy one in a month, before they are asked whether they can afford it. |
| 2517 | `HouseholdBalance.TRANSIT_DETERRENT` | `.5` | How much of the wanting a fully-served transit system takes away. |
| 2576 | `HouseholdBalance.CAR_DEPOSIT` | `.20` | The least of a car's price a household must find in cash before a lender will put up the rest. |
| 2602 | `HouseholdBalance.CAR_CREDIT_SHARE` | `.5` | How much of what a household could still borrow a lender will actually advance against a car. |
| 2661 | `HouseholdBalance.USED_CAR_FLOOR` | `.15` | What a scrapper pays, as a share of a new car. |
| 2670 | `HouseholdBalance.USED_CAR_CEILING` | `.70` | ...and what one fetches when buyers are queueing, on the same terms. |
| 2985 | `HouseholdBalance.CAR_SALE_HORIZON_MONTHS` | `CREDIT_LIMIT_MONTHS` | How far ahead a household looks before it decides the car has to go. |
| 3067 | `HouseholdBalance.FOOD_ASSISTANCE_MEANS_SHARE` | `.5` | A household is eligible for food assistance when a basket a head would take more than this share of what it has after its fixed bills and its investment income over the year: half. |
| 3081 | `HouseholdBalance.MEANS_INCOME_MONTHS` | `12` | Months the means test's investment income is smoothed over, as an exponential average - a twelfth of the gap a month, the year Expectations smooths inflation over (0.7.45; the UI spec's B16). |
| 3782 | `HouseholdBalance.MIN_MOVE` | `PriceLevel.atFounding(1e-12)` |  |
| 3982 | `HouseholdBalance.HOUSEHOLD_PAPER_APPETITE` | `20` | The share of an issue the households take, per unit of spread between its yield and the deposit rate: 20 - a fifth of an issue for each point. |
| 3985 | `HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE` | `.5` | ...and, for a BUSINESS issue, never more than this share of one: half, because a bond market with no bank in it is no longer the bank's market (BondMarket). |
| 4835 | `HouseholdBalance.SHARE_CUSHION_MONTHS` | `3` | Months of take-home a household keeps in the bank before it buys a share. |
| 4838 | `HouseholdBalance.SHARE_OF_EXCESS` | `.30` | The share of what is past the cushion it puts into one offering. |
| 5158 | `HouseholdBalance.CELL_SLOTS_BEFORE_SHARES` | `8` | Figures carried per cell before the shares were appended (2026-09-10, evening). |
| 5161 | `HouseholdBalance.CELL_SLOTS_BEFORE_ABROAD` | `CELL_SLOTS_BEFORE_SHARES + Equity.COMPANIES.length` | ...and before the dollars abroad were (2026-09-11). |
| 5164 | `HouseholdBalance.CELL_SLOTS_BEFORE_STUDENT_DEBT` | `CELL_SLOTS_BEFORE_ABROAD + 1` | ...and before the student loans were (2026-09-11, afternoon). |
| 5167 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARS` | `CELL_SLOTS_BEFORE_STUDENT_DEBT + 1` | ...and before the cars were (2026-09-16). |
| 5170 | `HouseholdBalance.CELL_SLOTS_BEFORE_INVESTMENT_INCOME` | `CELL_SLOTS_BEFORE_CARS + 1` | ...and before the month's investment income was (2026-09-17). |
| 5180 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEALS` | `CELL_SLOTS_BEFORE_INVESTMENT_INCOME + 1` | ...and the dinners, appended 2026-09-18. |
| 5190 | `HouseholdBalance.CELL_SLOTS_BEFORE_CARE` | `CELL_SLOTS_BEFORE_MEALS + 1` | ...and the share of the cell's people who paid for care, appended 2026-09-19. |
| 5199 | `HouseholdBalance.CELL_SLOTS_BEFORE_PAPER` | `CELL_SLOTS_BEFORE_CARE + 1` | ...and the city's paper, appended 2026-09-22 (0.7.1). |
| 5211 | `HouseholdBalance.CELL_SLOTS_BEFORE_BONDS` | `CELL_SLOTS_BEFORE_PAPER + 1` | ...and the cell's bonds at face, all together, appended 0.7.12: since round 2 the sum of what it holds bond by bond (saved under its own key, householdBondsByCell), and in a round-1 save its claim on the households' o... |
| 5225 | `HouseholdBalance.CELL_SLOTS_BEFORE_GROCERIES` | `CELL_SLOTS_BEFORE_BONDS + 1` | ...and the last sale's groceries, appended 0.7.43: the baskets each household got, the baskets it asked for at the price, the baskets it needed, and the food assistance paid on them - written at the bottom of the mont... |
| 5233 | `HouseholdBalance.CELL_SLOTS_BEFORE_MEANS_INCOME` | `CELL_SLOTS_BEFORE_GROCERIES + 4` | ...and the investment income the food assistance means test reads, smoothed over MEANS_INCOME_MONTHS, appended 0.7.45. |
| 5243 | `HouseholdBalance.CELL_SLOTS_BEFORE_AFTER_FIXED` | `CELL_SLOTS_BEFORE_MEANS_INCOME + 1` | ...and before each cell's income after its fixed bills was carried (A4). |
| 5246 | `HouseholdBalance.CELL_SLOTS` | `CELL_SLOTS_BEFORE_AFTER_FIXED + 1` | Figures carried per cell, in the order toCellSaveArray() writes them: the eight, a share count per company, the dollars abroad, the student loan, the cars, the month's investment income, the month's meals eaten out, t... |

## Fields (state)

| line | field | says |
|---:|---|---|
| 421 | `private final Household[] cells` | Every meaningful cell of the family matrix, in one fixed order: the working shapes by declaration, each across the six tiers, then the two retired shapes, each across the six tiers its households retired from (HS1), t... |
| 422 | `private final int[][] index` |  |
| 424 | `private final java.util.List<Household> view` |  |
| 425 | `private final int[] unemployedIndex` |  |
| 426 | `private int studentIndex` |  |
| 427 | `private final int[] orphanIndex` |  |
| 428 | `private int prisonerIndex` |  |
| 429 | `private int ownerIndex` |  |
| 437 | `private boolean ownerLine` | True when a listing company's founders' shares go to the owner families (BO1) and not to every household. |
| 445 | `private final int[] ownerFloorMonths` | Per company: -1 when it has no owner family; 0 or more when it has one, and then the number is the months its fair value has sat at the exchange's floor (the family gives up after OwnerHousehold.OWNER_BROKE_MONTHS). |
| 459 | `private ToDoubleFunction<Household> outsideCensus` | How many households are in each cell the family matrix does not hold - the out of work, the students, the orphans. |
| 470 | `private ToDoubleFunction<Household> rentShares` | The share of a door's rent one household of a cell pays: 1 alone in its own home, a fifth sharing, half doubled up, none with no door. |
| 485 | `private ToDoubleFunction<Household> outsideDependants` | Dependants living in each cell the family matrix does not hold. |
| 526 | `private double[] careBillByRow` | This month's treatment fees by row, as the households were billed them, or null for a caller that does not price care. |
| 529 | `private double[] careBillFullByRow` | ...and the treatment bill the same rows would have faced at full service. |
| 532 | `private double lastCareSkipped` | The care bills the households skipped this month, summed over the city: what they ate instead. |
| 578 | `private double studentLoanRate` |  |
| 586 | `private boolean studentLoansOpen` | Whether the treasury lends to students this month (R0): false before the student-aid lever opens, and then no new loan is made. |
| 606 | `private double accountFee` |  |
| 607 | `private ToDoubleFunction<Household> housedShares` |  |
| 674 | `private double lastWrittenOff` |  |
| 676 | `private double lastSeized` | What the month's bankrupt households' savings repaid the bank before the rest of their debt was written off (HL1; Household.discharge()), in total: part of totalRepaid(). |
| 677 | `private double lastLeaving` |  |
| 678 | `private double lastEvicted` |  |
| 679 | `private double lastStudentDebtTakenAway` |  |
| 680 | `private double lastTakenAway` |  |
| 682 | `private double lastArrivedWith` | What the month's newcomers brought as their opening buffer (OPENING_BUFFER_MONTHS): money from abroad into the savings. |
| 689 | `private double lastUnfunded` | Bills the sellers booked in full and a household could not fund (Household.settle, unfunded), summed over the households: money the shops were paid that no savings held. |
| 691 | `private double lastUnsettledIncome` | Income the rows were handed that went to a cell too small to settle (isEmpty()): the row's take-home less what the settled cells were given. |
| 693 | `private double lastUnsettledFees` | ...the fees the row was billed for treatment and tuition that no settled cell was charged. |
| 695 | `private double lastUnsettledShopping` | ...the row's shopping that no settled cell paid: the sellers booked the sale, the cell that was to pay it was skipped. |
| 697 | `private double lastUnsettledRent` | ...the rent of the cells too small to settle: the landlords booked it, no household was charged it. |
| 699 | `private double lastCensusStranded` | Savings left in a pool nobody is handed: a shrinking cell with no grown-ups releases its savings to a stock group that has none to claim them (moveStock, the savings stock only). |
| 701 | `private double lastCensusCreated` | Savings a growing cell with no grown-ups keeps per household on more households: money made by the census (moveStock, the savings stock only). |
| 702 | `private double graduating` |  |
| 703 | `private double lastGraduated` |  |
| 704 | `private double lastDepositInterest` |  |
| 705 | `private double lastDelivered` |  |
| 706 | `private double plannedSpend` |  |
| 707 | `private double hungryPeople` |  |
| 708 | `private double totalPeople` |  |
| 710 | `private double hungryAtFullShelves` | ...of whom hungry even had the shops handed over all that was planned: the money half (0.7.27). |
| 1466 | `private int[] bondStockIds` | The bonds' ids in the order stocks() last listed them, one stock each from its named slot. |
| 1716 | `private int lastCellsFolded` | Cells the census left under EMPTY_CELL this month that still held something, and were emptied. |
| 1866 | `private final double[] flows` | The month's flows, as the demographics struck them, waiting for the next census. |
| 1871 | `private final double[] lastFlowHouseholds` |  |
| 1872 | `private final double[] lastFlowSavings` |  |
| 1874 | `private double lastEstateRepaid` | What the estates repaid the bank out of the dead's savings this month: part of totalRepaid(). |
| 1876 | `private double lastEstateWrittenOff` | ...the debt their savings did not cover, written off with the leavers' (in getWrittenOff()). |
| 1878 | `private double lastEstatePassed` | ...the savings passed in kind to the heirs. |
| 1880 | `private double lastPoolSavings` | Savings the pool moved between cells this month, from its row and city pools: what the flows did not explain. |
| 1971 | `final int kind, from, to, tier` |  |
| 1972 | `double out, in` |  |
| 1983 | `final java.util.List<Move> moves` |  |
| 1984 | `final double[] after` |  |
| 2437 | `private final double[] lastSharesTakenAway` | Shares of each company that left the city with their holders this month. |
| 2673 | `private final double[] carsToReplace` | What died this month, per cell, waiting to be replaced. |
| 2676 | `private double lastCarsTakenAway` | ...and the cars whose owners left the city. |
| 3084 | `private double satiationPrice` | The price a full basket is still wanted at, in today's money: told by Retail at its sale and by Game where the households plan (Retail.getSatiationPrice()). |
| 3087 | `private double foodAssistance` | The food assistance dial, 0 to 1: the share of an eligible household's baskets the treasury's voucher covers. |
| 3090 | `private double lastFoodAssistancePaid, lastFedByAssistance` | What the treasury paid toward the households' groceries at the last sale, and the baskets that bought. |
| 3467 | `private double lastMealsBought, lastMealSpend, lastMealsEaten` |  |
| 3488 | `private double lastLuxuriesBought, lastLuxurySpend` |  |
| 3607 | `private double lastUsedOffered, lastUsedTraded, lastUsedSpend, lastUsedFinanced, lastUsedPrice, lastUsedNew...` |  |
| 3645 | `private double lastUsedLoanFees` |  |
| 3647 | `private double lastCarsBought` |  |
| 3648 | `private double lastCarsFinanced` |  |
| 3662 | `private double lastCarLoanFees` |  |
| 3673 | `private Exchange exchange` |  |
| 3674 | `private Equity register` |  |
| 3675 | `private Bank bank` |  |
| 3676 | `private Household.Liquidity liquidity` |  |
| 3785 | `private double minMove` | The founding-money floor in the current unit. |
| 3811 | `private double localPerUsd` | Local currency per dollar, this month: what the paper abroad is worth here. |
| 3820 | `private double lastAbroadTakenAway` | Dollars the households that left this month took with them. |
| 3997 | `private PaperDesk paperDesk` |  |
| 4012 | `private double paperRatio` | What a dollar of face of the households' paper is worth this month: the households' book at the curve over its face (DebtManager .householdBookRatio()), struck once a month by Game before the households' strike and SA... |
| 4029 | `private double spendFactor` | The month's spend factor (0.7.3): what share of its spending above subsistence every household plans, struck by Game once a month on the real deposit rate (Game.spendFactor()) before the households plan - see the bann... |
| 4202 | `private BondMarket bondMarket` |  |
| 4215 | `private double bondRatio` | What a dollar of face of the households' bonds is worth this month: every cell's holdings at the bonds' value over their face, struck by the market at its step and SAVED, because the plan reads it and the load path mu... |
| 4224 | `private double lastBondsTakenAway` | The face that left the city this month with its holders, every bond together. |
| 4264 | `private java.util.Map<String, Household> cellIndex` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 5540 | **type** `public class HouseholdBalance` | The households' balance sheet: what they have saved, what they owe, and what happens in the month they cannot cover the shop. |

### the dials (lines 103-157)

### AND A HOUSEHOLD SPENDS OUT OF WHAT IT HAS, NOT ONLY OUT OF WHAT IT (lines 158-222)

### ...AND WHAT IT SPENDS EATING OUT (2026-09-18) (lines 223-248)

### AND THE CEILING, WHICH IS IN MEALS AND NOT IN MONEY (2026-09-18) (lines 249-264)

### AND IT IS NOT ENOUGH, WHICH IS THE REAL ANSWER (2026-09-17) (lines 265-293)

### AND WHAT IT SPENDS ANSWERS THE REAL RATE (0.7.3) (lines 294-367)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 5 | `public static double spendFactor(double realDepositRate)` | The share of its spending above subsistence a household keeps at this real deposit rate (annual, a fraction): 1 at zero, less when saving pays, more when it costs, between SPEND_FLOOR and SPEND_CEILING. |

### BANKRUPTCY (lines 368-410)

### the cells (lines 411-490)

| line | len | member | says |
|---:|---:|---|---|
| 447 | 5 | `private static int[] newOwnerLine()` |  |
| 461 | 3 | `public void setOutsideCensus(ToDoubleFunction<Household> census)` |  |
| 472 | 3 | `public void setRentShares(ToDoubleFunction<Household> shares)` |  |
| 487 | 3 | `public void setOutsideDependants(ToDoubleFunction<Household> kin)` |  |

### THE PRICE AT THE CLINIC DOOR (2026-09-19) (lines 491-570)

| line | len | member | says |
|---:|---:|---|---|
| 540 | 4 | `public void setCareBills(double[] treatmentBilled, double[] treatmentAtFullService)` | Hands the strike the month's treatment bills, by row: what the households were billed (HouseholdAccounts.getRowCareBilled) and what the same rows would have been billed at full service (getRowCareFull). |
| 550 | 10 | `public double carePaidShare(int row)` | Of a row's people, the share who paid for care at the last strike - people-weighted over its cells, 1 for a row with nobody in it. |
| 562 | 5 | `public double[] carePaidShares()` | The same, for every row at once, in row order. |
| 569 | 1 | `public double getCareSkipped()` | What the city's households skipped of their care bills this month, in money - and ate instead. |

### the student loan's rate (2026-09-21) (lines 571-594)

| line | len | member | says |
|---:|---:|---|---|
| 581 | 3 | `public void setStudentLoanRate(double annual)` | Sets the annual rate the graduates are charged this month. |
| 589 | 3 | `public void setStudentLoansOpen(boolean open)` | Told with the rate, by Game, every month and after a load. |
| 593 | 1 | `public boolean isStudentLoansOpen()` |  |

### the bank's account fee (0.7.7) (lines 595-671)

| line | len | member | says |
|---:|---:|---|---|
| 609 | 1 | `public void setAccountFee(double perHousehold)` |  |
| 610 | 1 | `public double getAccountFee()` |  |
| 622 | 7 | `public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly)` | THE BANK'S CAPITAL RULE FOR THE MONTH (0.7.8), from Bank.lendingGrowthLimit() and lendsOnlyToKeepBorrowersGoing(), set by Game at the top of the month before any family borrows: each cell may owe at most what it owes ... |
| 631 | 1 | `public double lossAllowance()` | What the bank sets aside against the families' debt (0.7.8), summed cell by cell - see Household.lossAllowance(). |
| 634 | 1 | `public double debtInTrouble()` | ...and how much of their debt is in cells that are in trouble. |
| 636 | 3 | `public void setHousedShares(ToDoubleFunction<Household> shares)` |  |
| 641 | 4 | `private double accountFeeFor(Household c, double households)` | The fee one household of this cell is charged, when the cell holds this many: nothing in a cell too empty to strike, which is paid nothing and pays nothing. |
| 653 | 9 | `public double[] accountFeesByRow(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | What each row's households will be charged in account fees this month, on this census: the figure the household books show on their own line (HouseholdAccounts.setAccountFees()), struck BEFORE the cells settle - the b... |
| 664 | 1 | `public double totalAccountFees()` | What the households paid the bank in account fees this month: the sum the bank takes. |
| 667 | 1 | `public double totalLoanFees()` | ...and the loan fees the bank added to what they owe on the credit they drew this month. |
| 670 | 1 | `public double getStudentLoanRate()` | The annual rate the cells were last told. |

### the month (lines 672-753)

| line | len | member | says |
|---:|---:|---|---|
| 712 | 41 | `public HouseholdBalance()` |  |

### EVERY HOUSEHOLD, OR ONE OF THEM (lines 754-946)

| line | len | member | says |
|---:|---:|---|---|
| 762 | 3 | `public Household cell(FamilyStructure shape, PayTier tier)` | The cell for this shape at this tier: for a retired shape, the tier its people retired from (HS1). |
| 771 | 8 | `public java.util.List<Household> cells(FamilyStructure shape)` | A retired shape's cells, one for each tier its people retired from, in tier order (HS1: there was one cell a shape, cell(shape), until the retired kept their former tier). |
| 781 | 5 | `public double retiredHouseholds(FamilyStructure shape)` | Households of a retired shape, every former tier together. |
| 793 | 5 | `public double tierShareOf(Household c)` | A retired cell's share of its shape's households, every former tier together, as the last census left them: a sixth each when the shape is empty, so the shares always add to one. |
| 800 | 3 | `public UnemployedHousehold unemployed(UnemployedHousehold.Status status)` | The out-of-work cell in this situation. |
| 805 | 1 | `public StudentHousehold students()` | The students' cell. |
| 808 | 4 | `public OrphanHousehold orphans(AgeBand band)` | The orphans of a child band, or null for a band that has none. |
| 814 | 1 | `public PrisonerHousehold prisoners()` | The prisoners' ledger. |
| 817 | 1 | `public OwnerHousehold owners()` | The owner families' ledger: one two-adult household per company with a family. |
| 820 | 1 | `public void setOwnerLine(boolean on)` | Switches the owner line on or off (BO1); see ownerLine. |
| 822 | 1 | `public boolean isOwnerLine()` |  |
| 825 | 5 | `public int ownerFamilies()` | How many companies have an owner family. |
| 832 | 1 | `public boolean hasOwnerFamily(int company)` | True when this company (Equity.COMPANIES index) has an owner family. |
| 835 | 7 | `public java.util.Map<String, Integer> ownerLineForSave()` | The line's state for the save: company key to months at the floor, only the companies with a family, in company order. |
| 844 | 13 | `public void restoreOwnerLine(java.util.Map<String, Integer> line)` | Restores the line from a save; null or a missing key means no family, an unknown key is skipped. |
| 874 | 22 | `public void tallyOwnersAtFloor(java.util.function.IntPredicate atFloor, java.util.function.IntPredicate listed)` | A YEAR AT THE FLOOR SENDS A FAMILY BACK (BO1; Jerus, 2026-11-02: "if sector goes broke he just joins the regular pop pool"). |
| 902 | 5 | `public void ownerCompanyFailed(int company)` | A company whose equity was taken (a failed bank's resolution) has nothing left to own: its family leaves the line at once, for the working population (BO1). |
| 909 | 1 | `public java.util.List<Household> cells()` | Every cell, in the fixed order. |
| 911 | 1 | `public int cellCount()` |  |
| 913 | 3 | `public void forEach(Consumer<Household> action)` |  |
| 918 | 5 | `public double sum(ToDoubleFunction<Household> figure)` | Adds a figure up across every cell. |
| 925 | 5 | `public double sumRow(int row, ToDoubleFunction<Household> figure)` | ...or across one row: a tier's cells, or the retired. |
| 932 | 3 | `public double rowHouseholds(int row)` | Households in the row: the denominator of every per-household row figure. |
| 937 | 9 | `private double perHousehold(int row, ToDoubleFunction<Household> perCell)` | A row total, per household of the row. |

### THE MONTH (lines 947-1424)

| line | len | member | says |
|---:|---:|---|---|
| 980 | 304 | `public void advanceMonth(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, ...` | months owed sit on (Bank.householdRate() since 0.7.7; the city's own borrowing rate before it) |
| 1291 | 5 | `private static double[] padRows(double[] rows)` | A seven-row array - the tiers and the retired, from a caller written before the people outside the families had rows - padded with empty rows (or a ten-row one from before the prisons, an eleven-row one from before th... |
| 1298 | 5 | `private static double sumOfRows(double[] rows)` | The sum of a row array, nothing for no array (HL1: what the rows were handed, against what the cells took). |
| 1305 | 3 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Who lives in each cell now, in cell order: the month's flows planned and not used up, for a reader before the month (the account fees). |
| 1320 | 31 | `private double[] takeCensus(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] after)` | Who lives in each cell now, in cell order. |
| 1358 | 26 | `private double[] retiredMix(double[] after, double[] fresh)` | The tiers a retired shape with nobody in it yet is split by: every retired household's after the flows, by people; in a city with none (a founding), the working census's grown-ups this month, so the first retirees ret... |
| 1395 | 13 | `private double[] splitIncome(double[] fresh, double[] rowTotal)` | A row's take-home, per household of each of its cells. |
| 1410 | 14 | `private double[] splitByPeople(double[] fresh, double[] rowTotal)` | A row's fees, per household of each of its cells, by the people in them. |

### THE STOCK BELONGS TO THE PEOPLE, NOT TO THE CELLS (lines 1425-1677)

| line | len | member | says |
|---:|---:|---|---|
| 1469 | 6 | **type** `private interface Stock` | One stock a household carries, for followThePeople() to move. |
| 1470 | 1 | `double get(Household c)` _(in HouseholdBalance.Stock)_ |  |
| 1471 | 1 | `void set(Household c, double perHousehold)` _(in HouseholdBalance.Stock)_ |  |
| 1473 | 1 | `default boolean notAChildsToClaim()` _(in HouseholdBalance.Stock)_ | A stock an orphan never claims from the pool (HS1's review): a debt, a loan or a car - no credit, no licence. |
| 1483 | 83 | `private java.util.List<Stock> stocks(int[] named)` | Every stock a household carries, in the order followThePeople() moves them and foldEmptyCells() folds them. |
| 1578 | 99 | `private void followThePeople(double[] fresh, double[] buffer, Plan plan)` | Moves every stock with the people, then sets every cell's count. |

### A CELL UNDER HALF A HOUSEHOLD IS EMPTY (0.7.2) (lines 1678-1788)

| line | len | member | says |
|---:|---:|---|---|
| 1724 | 1 | `int foldEmptyCells()` | Empties every cell under EMPTY_CELL into the cells that are not. |
| 1726 | 22 | `private int foldEmptyCells(java.util.List<Stock> stocks)` |  |
| 1756 | 23 | `private boolean spread(Stock s, double amount, int group)` | One stock's amount onto the cells that are not empty, in one stock group (or the whole city, for -1): pro rata to what each holds of it, or by households when none holds any. |
| 1781 | 4 | `boolean holdsAnything(Household c)` | True when the cell holds any stock at all, which an empty cell must not. |
| 1787 | 1 | `public int getCellsFolded()` | Cells emptied this month that still held something when the census left them under EMPTY_CELL. |

### MONEY FOLLOWS PEOPLE (HS1, 2026-11-18) (lines 1789-1934)

| line | len | member | says |
|---:|---:|---|---|
| 1890 | 6 | `public void setFlows(PopulationCohorts cohorts)` | Hands the month's flows to the next census (HS1), from the pyramid's own month: the shares of the adults who retired and of the seniors who aged on, of each band who died, the share of every band the month's net migra... |
| 1898 | 13 | `void setFlows(double retire, double ageOn, double dieAdult, double dieSenior, double dieElder, double leave, double evictedLeft)` | The same, by hand: for a harness that causes one flow at a time. |
| 1912 | 1 | `private static double clampShare(double s)` |  |
| 1915 | 1 | `public double[] flowsToSave()` | The month's flows waiting for the next census, in FLOW_ slot order: for the save. |
| 1918 | 5 | `public void restoreFlows(double[] saved)` | ...and back; null or short (a save from before HS1) leaves none waiting: the first census back moves money by the pool alone. |
| 1925 | 1 | `public double getFlowHouseholds(int kind)` | Households, by the flow's kind (RETIRING and the rest), that the last census's flows carried out of the cells they left. |
| 1927 | 1 | `public double getFlowSavings(int kind)` | ...and the savings they carried with them. |
| 1929 | 1 | `public double getEstateRepaid()` | The last census's estates: what they repaid the bank, what was written off, and the savings the heirs were handed. |
| 1930 | 1 | `public double getEstateWrittenOff()` |  |
| 1931 | 1 | `public double getEstatePassed()` |  |
| 1933 | 1 | `public double getPoolSavings()` | Savings the census's pool moved between cells at the last census: what no flow explained. |

### the retired's money against the working's (HS1) (lines 1935-2439)

| line | len | member | says |
|---:|---:|---|---|
| 1943 | 1 | `public double getRetiredToWorkingNetWorth()` | Every retired household's net worth, per household, over a working household's. |
| 1945 | 3 | `public double getSeniorsAloneToWorkingNetWorth()` | The seniors living alone, the same way: the cell the study found poorest. |
| 1949 | 3 | `public double getElderCouplesToWorkingNetWorth()` | The elder couples, the same way. |
| 1953 | 15 | `private double netWorthOver(java.util.function.Predicate<Household> which)` |  |
| 1970 | 7 | **type** `private static final class Move` | One flow out of a cell: households out of `from`, into `to` (a cell), or out of the city, or an estate to `tier`. |
| 1973 | 3 | `Move(int kind, int from, int to, int tier, double out, double in)` _(in HouseholdBalance.Move)_ |  |
| 1982 | 5 | **type** `private static final class Plan` | The month's flows, planned off the households coming in: the moves, and every cell's households after them. |
| 1985 | 1 | `Plan(double[] after)` _(in HouseholdBalance.Plan)_ |  |
| 1996 | 57 | `private Plan planFlows(ToDoubleBiFunction<FamilyStructure, PayTier> census, boolean useUp)` | The month's flows as moves between cells, struck off each cell's households coming into the month and the flows setFlows() handed on - used up only for the month itself (useUp), so the account fees struck before it re... |
| 2061 | 11 | `private void dying(Plan plan, int i, double h, double share, PayTier tier)` | The band's dead in one cell: in a couple, one of them, and the household goes whole to the alone cell of its row and tier - the survivor keeps everything; otherwise an estate, their share of the household, to the form... |
| 2074 | 9 | `private void into(Plan plan, int kind, int i, double households, int grownUps, double[] shares, PayTier tier)` | People going on to the shapes of a band at the tier they left: `households` of cell i, each `grownUps` people, split by the shapes' shares. |
| 2085 | 11 | `private int aloneOf(Household c)` | The alone cell a couple's survivor goes to, in its row and tier: the senior or elder alone, a single adult, or a single parent where there were children. |
| 2103 | 28 | `private double[] shapeShares(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] b0, AgeBand band)` | The shares of a band's people that go to each retired shape holding the band: by the census's own growth in the shape this month, by its grown-ups where nothing grew, by the census where it holds nobody yet, and to th... |
| 2141 | 52 | `private double[] carryTheFlows(Plan plan, java.util.List<Stock> stocks, double[] left, int loanSlot)` | Carries the plan's money (see the banner above): every outflow at its cell's average coming in, then every inflow, then the estates. |
| 2195 | 26 | `private void passOn(double[] e, PayTier tier, double[] b, java.util.List<Stock> stocks, double[] left)` | An estate, in kind, to the working households of the tier by grown-ups; with none, the city's; with none at all (no heir after the flows), out with the leavers. |
| 2236 | 32 | `private double[] carryGraduatesLoans(double[] before)` | The graduates' student loans, handed to the working families before the census moves anything. |
| 2282 | 3 | `private static int poolWeight(Household c, Stock stock)` | What a household of a cell weighs in the pool: its grown-ups, and a cell with none (the orphans) by its people (OH1). |
| 2298 | 137 | `private double moveStock(Stock stock, double[] fresh, double[] before, double[] arrival)` | One stock through the pool: released by the cells that shrank at their own average, claimed by the cells that grew - within the row first, then across the city - and what nobody claimed returned. |
| 2438 | 1 | `public double getSharesTakenAway(int company)` |  |

### THE CARS (2026-09-16) (lines 2440-2518)

### AND THEY BORROW FOR IT (2026-09-17) (lines 2519-2603)

### THE SECOND-HAND MARKET (2026-09-17) (lines 2604-2806)

| line | len | member | says |
|---:|---:|---|---|
| 2678 | 1 | `public double getCarsTakenAway()` |  |
| 2681 | 1 | `public double totalCars()` | Every car in the city. |
| 2694 | 7 | `public double[] commuteWorkersByRow()` | The working cells' commuters by row: households times grown-ups. |
| 2703 | 9 | `public double[] commuteCarsByRow()` | ...and the cars they hold by row, at most one per grown-up: households times the lesser of the two. |
| 2714 | 6 | `public double captiveShare()` | The share of those commuters with no car of their own; 1 with no commuters. |
| 2722 | 4 | `public double carsPerHousehold()` | Cars per household, 0 to 1 - what the road reads. |
| 2737 | 12 | `public double wearOutCars()` | Fifteen years on, every car in the city is scrapped. |
| 2760 | 18 | `public double creditScrappedCars(double total)` | What the Scrap Yards paid for the steel in the cars scrapped this month (0.7.123, batch I4; OldMetal): into savings, each cell its share by the cars it scrapped at the month's top (wearOutCars()) - by households where... |
| 2800 | 6 | `public double carsWanted(double price, double ceiling)` | What the households would buy this month at this price, before the market says how many there are. |

### WHOLE CARS, AND WHY THE FLOOR IS LOAD-BEARING (2026-09-16) (lines 2807-2962)

| line | len | member | says |
|---:|---:|---|---|
| 2839 | 49 | `private double wantOf(int i, double price, double ceiling)` | What one cell would buy this month, in whole cars. |
| 2902 | 60 | `public double takeCars(double units, double price, double ceiling)` | Hands out the cars the market actually had, pro rata over who wanted them, and takes the money out of savings. |

### the second-hand market (lines 2963-3027)

| line | len | member | says |
|---:|---:|---|---|
| 3014 | 13 | `private double offerOf(int i, double floorPrice)` | What one cell would put up for sale this month, in whole cars. |

### GROCERIES AT A PRICE, AND FOOD ASSISTANCE (0.7.43) (lines 3028-3217)

| line | len | member | says |
|---:|---:|---|---|
| 3092 | 1 | `public void setSatiationPrice(double price)` |  |
| 3093 | 1 | `public double getSatiationPrice()` |  |
| 3095 | 3 | `public void setFoodAssistance(double share)` |  |
| 3098 | 1 | `public double getFoodAssistance()` |  |
| 3101 | 1 | `public double getFoodAssistancePaid()` | What the treasury paid toward the groceries at the last sale: every voucher, up to the baskets got at the price charged. |
| 3104 | 1 | `public double getFedByAssistance()` | ...and the baskets that bought, at the price charged. |
| 3111 | 8 | `public double groceryDemandOf(Household c, double price)` | What one of these households would buy at this price, in baskets: the curve in the banner. |
| 3121 | 5 | `public double groceriesWanted(double price)` | What the city's households would buy at this price, in baskets: every cell's demand times its households. |
| 3128 | 5 | `public double groceriesNeeded()` | The baskets the city's households need, one a head: what the demand is a share of. |
| 3149 | 18 | `public void allocateGroceries(double sold, double pAlloc, double pCharged)` | Hands out the baskets the shops sold, and pays the vouchers on them. |
| 3174 | 3 | `public double foodAssistanceFor(Household c, double price)` | The voucher one of these households holds at this shelf price: the dial's share of its baskets at the price, when they would take more than FOOD_ASSISTANCE_MEANS_SHARE of what it has after its fixed bills and its inve... |
| 3179 | 8 | `double foodAssistanceFor(Household c, double price, double share)` | ...at a dial of the caller's: the Policy tab's preview (PolicyPreview.foodAssistanceAt()). |
| 3195 | 8 | `public double foodAssistanceAt(double share, double price)` | What the vouchers would have paid at the last sale at another dial: each eligible household's voucher at that share, up to the baskets it got at the price - the same rule allocateGroceries() pays by, against the baske... |
| 3205 | 5 | `public double householdsEligibleForFoodAssistance(double price)` | Households whose baskets would take more than FOOD_ASSISTANCE_MEANS_SHARE of their means at this price: who a voucher would go to. |
| 3212 | 5 | `public double[] foodAssistanceByRow()` | What the treasury paid toward each row's groceries at the last sale, row by row: the cells that were paid, summed. |

### who goes without, read for the screens (0.7.45) (lines 3218-3262)

| line | len | member | says |
|---:|---:|---|---|
| 3224 | 7 | **type** `public record GroceryRow(int row, double need, double asked, double got, double households, double eligible...` | The baskets one row of households needed at the last sale, asked for at the price, and got; its households, those a voucher would go to at a price, and those a voucher was paid to. |
| 3227 | 1 | `public double gotShare()` _(in HouseholdBalance.GroceryRow)_ | Baskets got per basket needed: 1 is fed. |
| 3229 | 1 | `public double askedShare()` _(in HouseholdBalance.GroceryRow)_ | Baskets asked for at the price per basket needed: under 1 is priced out. |
| 3233 | 17 | `public GroceryRow[] groceriesByRow(double price)` | Every row's groceries at the last sale, with who a voucher would go to at this price (ROWS order). |
| 3252 | 5 | `public double getHouseholdsAssisted()` | The households a voucher was paid to at the last sale. |
| 3259 | 3 | `public double foodAssistanceBasketsAt(double share, double price)` | The baskets the vouchers would have paid for at the last sale at another dial: foodAssistanceAt() at the price, in baskets. |

### THE LUXURY COUNTER (2026-09-17) (lines 3263-3363)

| line | len | member | says |
|---:|---:|---|---|
| 3290 | 4 | `public double luxuriesWanted(double price)` | What the city's households would buy at this price, in whole pieces. |
| 3296 | 5 | `private double luxuryBudget()` | What the city's households would put over a counter, in money. |
| 3312 | 7 | `private double luxuryBudgetOf(Household c)` | What one cell would buy, in whole pieces. |
| 3333 | 30 | `public double takeLuxuries(double units, double price)` | Hands out the pieces the shops actually had, pro rata over who wanted them, and takes the money out of savings. |

### THE TABLE (2026-09-18) (lines 3364-3664)

| line | len | member | says |
|---:|---:|---|---|
| 3382 | 16 | `public double mealsWanted(double price)` | What the city's households would eat out at this price, in whole meals. |
| 3400 | 5 | `private double mealBudget()` | What the city's households would put on a table, in money. |
| 3419 | 7 | `private double mealBudgetOf(Household c)` | What one cell would spend eating out. |
| 3438 | 28 | `public double takeMeals(double meals, double price)` | Hands out the meals the kitchens actually served, pro rata over who wanted them, takes the money out of savings and REMEMBERS THE MEALS. |
| 3474 | 4 | `public void clearMealsBought()` | The month starts with no meals bought (FW6, 2026-10-25). |
| 3480 | 1 | `public double getMealsEaten()` | Meals the city ate out in the month the hunger measure just read. |
| 3483 | 1 | `public double getMealsBought()` | Meals the households ate out this month. |
| 3486 | 1 | `public double getMealSpend()` | ...and what they paid for them. |
| 3491 | 4 | `public void clearLuxuriesBought()` | ...and none of the pieces (FW6, 2026-10-25): see clearMealsBought(), and the counter's shop(). |
| 3497 | 1 | `public double getLuxuriesBought()` | Pieces the households took this month. |
| 3500 | 1 | `public double getLuxurySpend()` | ...and what they paid for them. |
| 3503 | 1 | `public double getLuxuryWant()` | What every household in the city would like to put over a counter this month. |
| 3506 | 5 | `public double carsOffered(double floorPrice)` | What the city's households would put up for sale at this floor. |
| 3534 | 72 | `public double clearUsedCars(double newPrice, double ceiling)` | Clears the month's second-hand market: strikes a price, moves the cars that find a buyer, and pays the households that sold them. |
| 3611 | 1 | `public double getUsedCarsOffered()` | Cars put up for sale this month, whether or not anybody took them. |
| 3614 | 1 | `public double getUsedCarsTraded()` | ...and the ones that found a buyer. |
| 3617 | 1 | `public double getUsedCarPrice()` | What they went for. |
| 3630 | 3 | `public double getUsedCarShare()` | ...as a share of what a NEW one cost the month it was struck, which is the only honest denominator and is between USED_CAR_FLOOR and USED_CAR_CEILING by construction. |
| 3635 | 1 | `public double getUsedCarSpend()` | What the buyers paid, all in. |
| 3641 | 1 | `public double getUsedCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month. |
| 3644 | 1 | `public double getUsedCarLoanFees()` | ...and the bank's fee on that, added to what the buyers owe (0.7.7). |
| 3651 | 1 | `public double getCarsBought()` | Cars the households actually took this month. |
| 3658 | 1 | `public double getCarsFinanced()` | ...and what of that a lender advanced, which the bank has to be told about in the same month or the money audit sees a pool fall for no reason. |
| 3661 | 1 | `public double getCarLoanFees()` | ...and the bank's fee on it, added to what the buyers owe rather than paid (0.7.7). |

### THE MARKET (lines 3665-3733)

| line | len | member | says |
|---:|---:|---|---|
| 3678 | 6 | `public void setMarket(Exchange exchange, Equity register, Bank bank)` |  |
| 3690 | 15 | `private void rebuildLiquidity()` | The waterfall's two sales, in one object: the city's paper to the bank's desk first (0.7.1), then shares on the exchange. |
| 3712 | 1 | `Household[] cellsForMarket()` | The cells, for the exchange and the bond market to post their orders from (0.7.12 round 2: each household type is its own participant). |
| 3715 | 5 | `public void splitShares(int company, double k)` | A split or consolidation: every household's count of the company by the factor. |
| 3722 | 4 | `public double marketValueOfShares()` | What the households' shares are worth at the exchange's price - the last trade on each book, or fair value before one. |
| 3728 | 5 | `public double totalSold()` | What the households raised this month selling shares to cover the shop. |

### THE WORLD'S PAPER (lines 3734-3751)

### THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-16) (lines 3752-3938)

| line | len | member | says |
|---:|---:|---|---|
| 3788 | 3 | `public void seedConstants(double unit)` | Re-seeds the floor at a given unit. |
| 3808 | 1 | `boolean owes(Household c)` | WHETHER A CELL OWES ANYTHING, READ AT THE SAME FLOOR (0.7.12 round 6). |
| 3813 | 3 | `public void setExchangeRate(double localPerUsd)` |  |
| 3817 | 1 | `public double getExchangeRate()` |  |
| 3834 | 70 | `public void investAbroad(double depositRate, double worldRate, double rate)` | Every household decides where to keep its idle money: what is past the cushion goes abroad towards the target share, a little a month; what is abroad comes home when the target falls. |
| 3906 | 5 | `public double totalAbroadUsd()` | Dollars every household holds abroad. |
| 3913 | 1 | `public double totalAbroadValue()` | ...worth this much at home, at the month's rate. |
| 3916 | 5 | `public double getSentAbroad()` | Sent abroad this month, all households, local money. |
| 3923 | 5 | `public double getBroughtHome()` | Brought home this month - to keep to the target or to eat - all households, local money. |
| 3930 | 5 | `public double getForeignInterest()` | What the world paid the households this month, rolled abroad. |
| 3937 | 1 | `public double getAbroadTakenAway()` | Dollars that left with the households that left this month. |

### THE CITY'S PAPER, AT HOME (0.7.1) (lines 3939-4186)

| line | len | member | says |
|---:|---:|---|---|
| 3992 | 4 | **type** `public interface PaperDesk` | Where the households' paper is sold, for what, and what it does to the paper on the other side. |
| 3994 | 1 | `void buy(double face, double cash)` _(in HouseholdBalance.PaperDesk)_ | The desk buys this much face of the households' paper for this much cash. |
| 3999 | 4 | `public void setPaperDesk(PaperDesk desk)` |  |
| 4014 | 3 | `public void setPaperRatio(double ratio)` |  |
| 4018 | 1 | `public double getPaperRatio()` |  |
| 4031 | 3 | `public void setSpendFactor(double factor)` |  |
| 4036 | 1 | `public double getSpendFactor()` | The factor the last plan was struck at. |
| 4039 | 1 | `public double totalPaper()` | Every cell's paper, at face - DebtManager.householdPrincipal() from the other side. |
| 4042 | 1 | `public double marketValueOfPaper()` | ...at this month's market value. |
| 4045 | 5 | `public double totalPaperSold()` | Sold to the desk this month, all households, in cash. |
| 4052 | 5 | `public double totalPaperIncome()` | Coupons and principal the city paid the households on their paper this month. |
| 4058 | 3 | `private boolean mayBuyPaper(Household c)` |  |
| 4062 | 3 | `private double spareFor(Household c)` |  |
| 4067 | 5 | `public double spareForPaper()` | What every eligible household could put into paper: savings past the cushion, in total. |
| 4079 | 17 | `public double buyAtIssue(double cash, double facePerCash)` | The households buy at issue: this much cash in total, from each eligible cell pro rata to what is past its cushion, for this much face per dollar. |
| 4098 | 3 | `private double paperWeight(Household c, double total)` | Pro rata to the paper held: what a dollar of face in the households' book is spread as, cell by cell. |
| 4103 | 13 | `public void creditPaperCoupon(double total)` | A coupon on the households' share, into savings and the month's investment income, pro rata to the paper. |
| 4118 | 13 | `public void creditPaperPrincipal(double face)` | Principal on their share, into savings with the paper down by the same, pro rata. |
| 4139 | 10 | `public void creditPaperBuyback(double face, double price)` | A bond bought back: this much of their face off the paper, this much of the price into savings, pro rata. |
| 4151 | 9 | `private double sellPaperToDesk(Household c, double needPer)` | One cell sells to the desk for up to needPer a household. |
| 4170 | 16 | `public double sellPaperForSpread(double bookYield, double depositRate)` | THE SPREAD HAS GONE: when the households' paper yields no more than the bank pays savers, the target for it is nothing, and they sell a little a month - HOME_SPEED of it, the pace the dollars abroad come home at - int... |

### THE BUSINESSES' BONDS, AT HOME (0.7.12) (lines 4187-4452)

| line | len | member | says |
|---:|---:|---|---|
| 4204 | 4 | `public void setBondMarket(BondMarket market)` |  |
| 4217 | 3 | `public void setBondRatio(double ratio)` |  |
| 4221 | 1 | `public double getBondRatio()` |  |
| 4225 | 1 | `public double getBondsTakenAway()` |  |
| 4228 | 1 | `public double totalBonds()` | Every cell's bonds, at face: the households' face in every bond. |
| 4231 | 1 | `public double marketValueOfBonds()` | ...at this month's value. |
| 4234 | 5 | `public double totalBondsSold()` | Sold this month, in cash, and the coupons and principal received. |
| 4240 | 5 | `public double totalBondIncome()` |  |
| 4247 | 1 | `public double getBonds(int row)` | One row's bonds, per household of the row. |
| 4250 | 5 | `public double bondFaceHeld(int id)` | Every cell's face in one bond, together. |
| 4257 | 7 | `public Household cellByKey(String key)` | The cell with this key, or null - from an index built once, because the markets ask on every fill. |
| 4267 | 1 | `public double bondSpare()` | What every eligible household could put into bonds: savings past the cushion - the city's paper's rule. |
| 4270 | 1 | `public double bondSpare(Household c)` | ...one cell's, every household of it: nothing for a cell that may not buy (the city's paper's eligibility). |
| 4279 | 19 | `public double payForBonds(int id, double cash, double face)` | A new issue's households' part, at issue: this much cash out of every eligible cell's savings past its cushion, pro rata, for this much face of the bond, which each cell holds in the same proportion. |
| 4310 | 30 | `public double creditBondCoupons(java.util.Map<String, Double> dueByCell)` | THE MONTH'S COUPONS, to the cells that held the bonds at the record date (BondMarket.strikeCoupons()): into savings and the month's investment income. |
| 4342 | 13 | `public double creditBondPrincipal(int id)` | One bond's principal at maturity: every cell paid its own face of it, into savings, and the bond gone from it. |
| 4362 | 14 | `public void writeDownBonds(java.util.Collection<Integer> ids, double keep)` | A default on one issuer's bonds together: every cell's face of each down to this share, and its total recounted once - a slice writes an issuer's bonds down every month it is past the curve's floor, and a recount per ... |
| 4378 | 12 | `public double writeDownBonds(int id, double keep)` | A default on one bond: every cell's face of it down to this share. |
| 4394 | 10 | `public java.util.Map<String, java.util.Map<String, Double>> bondsByCellToSave()` | Every cell's bonds, per household, by the cell's key and the bond's id: the save's (0.7.12 round 2). |
| 4410 | 16 | `public void restoreBondsByCell(java.util.Map<String, java.util.Map<String, Double>> saved)` | ...back, by name: a cell this build does not have is dropped, as the cell arrays' rule is, and a bond id that does not parse is skipped. |
| 4437 | 15 | `public void claimPooledBonds(java.util.Map<Integer, Double> poolById)` | A SAVE FROM ROUND 1 held the households' bonds as one pool, each cell with a claim on it at face (Household.bonds): the pool is handed to the cells by those claims - every bond's households' face split across the cell... |

### the people outside the families (lines 4453-4527)

| line | len | member | says |
|---:|---:|---|---|
| 4456 | 1 | `public double getEvicted()` | Out-of-work households that lost their home this month. |
| 4459 | 1 | `public double totalStudentBorrowed()` | Student loans drawn this month, all students - the treasury's money out. |
| 4462 | 1 | `public double totalStudentRepaid()` | ...and repaid by graduates, the treasury's money back: principal, which is what the journal's line carries. |
| 4465 | 1 | `public double totalStudentInterest()` | ...and the interest the graduates paid on top of it this month: the treasury's revenue. |
| 4468 | 3 | `public double studentInterestAt(double annualRate)` | What a month's interest would come to at an annual rate, on the balances that are charged it - for a screen previewing a rate. |
| 4473 | 1 | `public double totalStudentDebt()` | What every household owes the treasury in student loans. |
| 4476 | 3 | `public double totalGraduateDebt()` | ...and the part of it that is in repayment: the graduates' balances, which the rate is charged on. |
| 4481 | 1 | `public double getStudentDebtTakenAway()` | Student loans that left the city with graduates who left. |
| 4502 | 1 | `public void setGraduates(double people)` | THE GRADUATES LEAVE THE STUDENT BODY WITH THEIR LOANS. |
| 4505 | 1 | `public double getLastGraduated()` | Students who left the student body with what they carried, at the last month's census. |
| 4508 | 1 | `public double getGraduating()` | The students who finished and wait for the next census to carry their loans (setGraduates()). |
| 4519 | 1 | `public double[] graduatesToSave()` | ...AND BOTH ACROSS A SAVE (0.7.63). |
| 4522 | 5 | `public void restoreGraduates(double[] saved)` | ...and back; null, a save from before 0.7.63, leaves both at nothing, as every load until then did. |

### what the bank is owed (lines 4528-4659)

| line | len | member | says |
|---:|---:|---|---|
| 4531 | 1 | `public double getWrittenOff()` | Written off this month, which is the bank's loss. |
| 4534 | 1 | `public double getSeizedAtBankruptcy()` | What the month's bankrupt households' savings repaid the bank with, before the rest was written off (HL1): already in totalRepaid(). |
| 4537 | 1 | `public double getTakenAway()` | Savings that left the city with the households that left. |
| 4540 | 1 | `public double getArrivedWith()` | What the month's newcomers brought as their opening buffer (OPENING_BUFFER_MONTHS): money from abroad into the savings. |
| 4543 | 1 | `public double getUnfunded()` | Bills the sellers booked in full and the households could not fund, this month, summed over the households (HL1: an open fault, named for HL2). |
| 4546 | 1 | `public double getUnsettledIncome()` | Income the month's rows were handed that went to cells too small to settle (HL1: an open fault, named for HL2). |
| 4549 | 1 | `public double getUnsettledFees()` | Fees the month's rows were billed that no settled cell was charged (HL1: an open fault, named for HL2). |
| 4552 | 1 | `public double getUnsettledShopping()` | Shopping the month's rows were billed for that no settled cell paid (HL1: an open fault, named for HL2). |
| 4555 | 1 | `public double getUnsettledRent()` | Rent of the cells too small to settle, which the landlords booked and no household was charged (HL1: an open fault, named for HL2). |
| 4558 | 1 | `public double getCensusStranded()` | Savings the census left in a pool nobody is handed (HL1: an open fault, named for HL2). |
| 4561 | 1 | `public double getCensusCreated()` | Savings the census made, a growing cell with no grown-ups keeping its per-household savings on more households (HL1: an open fault, named for HL2). |
| 4564 | 1 | `public double getBankrupt(int row)` | Households discharged this month, and the share of them leaving the city. |
| 4565 | 1 | `public double getLeavingCity()` |  |
| 4568 | 1 | `public boolean isLockedOut(int row)` | True when any cell of the row the bank has stopped lending to is still locked out. |
| 4571 | 7 | `public int getLockout(int row)` | The longest lockout standing in the row. |
| 4580 | 1 | `public double bookOwed()` | Everything the households owe the bank. |
| 4589 | 19 | `void censusOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census)` | The census alone (HS1): the month's flows used up, the pool, the fold - no month settled and nobody arriving with a buffer, so a harness reads what the people carried to the cent (HouseholdFlowCheck). |
| 4625 | 34 | `public void planOnly(ToDoubleBiFunction<FamilyStructure, PayTier> census, double[] rowDisposable, double rentPerHousehold, doub...` | The plan alone, without settling a month. |

### reading (lines 4660-4760)

| line | len | member | says |
|---:|---:|---|---|
| 4663 | 1 | `public double getSpendingCapacity()` | What the shops can sell next month, in money. |
| 4666 | 1 | `public double getWantedSpend()` | What they would spend if money were no object - the demand behind the cap. |
| 4669 | 1 | `public double getSubsistence(int row)` | One basket a head, per household of the row: what going short is measured against. |
| 4682 | 12 | `public double[] plannedShare()` | The share of each row's groceries, for splitting what retail actually sold back across the rows. |
| 4696 | 3 | `public double getHungerRate()` | Share of the city going short of food, 0-1. |
| 4700 | 1 | `public double getHungryPeople()` |  |
| 4708 | 3 | `public double getHungerPricedOut()` | Of getHungerRate(), the share of the city short of a basket that could not afford one at the price - the money half, at full shelves - and the share the shelves left short (0.7.45): the two add to the rate. |
| 4713 | 1 | `public double getHungerShortOfStock()` | ...and the rest of it: the shelves ran short. |
| 4724 | 1 | `public double getHungryAtFullShelves()` | Of getHungryPeople(), the ones who would have gone hungry even with the shelves full: the baskets they asked for at the price (0.7.43; their own plan until then), every one handed over, came up short of a basket - the... |
| 4736 | 1 | `public double getDeliveredShare()` | The share of what households asked for at the price (0.7.43; what they planned to buy until then) that the shops could hand over - and therefore which of the two hungers is biting. |
| 4749 | 11 | `public void creditDepositInterest(double total)` | Interest the bank paid on what these households have saved. |

### THEFT (2026-09-11) (lines 4761-4820)

| line | len | member | says |
|---:|---:|---|---|
| 4780 | 16 | `public double takeFromSavings(double total)` | Takes up to `total` from the households' savings, each cell in proportion to what it has saved. |
| 4803 | 14 | `public double creditByWeight(double total, double[] weight)` | Credits `total` to the cells in proportion to a weight per cell, in the cells' order - what the offenders' households took home. |
| 4819 | 1 | `public double getDepositInterest()` | What the bank paid the city's savers this month. |

### THE OFFER (lines 4821-5091)

| line | len | member | says |
|---:|---:|---|---|
| 4853 | 30 | `public double subscribe(int company, double offered, double price)` | Puts an offering to every household, and takes up what they will buy. |
| 4906 | 18 | `public boolean grantFounders(int company, double shares)` | Hands out a company's founding shares to the people who founded it. |
| 4930 | 15 | `public double creditDividend(int company, double perShare)` | Pays every household its dividend, straight into its savings. |
| 4957 | 12 | `public double creditLendingFee(int company, double perShare)` | Pays every household the lending fee on the shares of one company it lends, per share it holds, straight into its savings (EM2): the world pays it for the shares it sold short, which the cells lend pro rata to what ea... |
| 4980 | 6 | `public double surrenderShares(int company)` | EVERY CELL GIVES UP ITS SHARES OF ONE COMPANY, for nothing: a failed bank's resolution (0.7.14; Equity.takeAllForCity() - the old owners are wiped out, CDIC). |
| 4988 | 5 | `public double sharesHeld(int company)` | Shares of this company the city's households hold between them. |
| 4995 | 5 | `public double totalDividends()` | What the households were paid in dividends this month. |
| 5003 | 1 | `public double getSavings(int row)` | ---- the row, per household of it: what the screens and the fixtures read ---- |
| 5006 | 1 | `public double getHouseholds(int row)` | Households this row was struck for - the multiplier on every per-row figure. |
| 5007 | 1 | `public double getDebt(int row)` |  |
| 5008 | 1 | `public double getAfterFixed(int row)` |  |
| 5010 | 1 | `public double getDisposable(int row)` | What one household of the row had to spend this month, from its own ledger (0.7.27: Policy's pension lines foot on it). |
| 5012 | 1 | `public double getFixedCosts(int row)` | ...and its rent, fees and interest: what came off that before the shop - the ledger's own, so the three foot. |
| 5013 | 1 | `public double getInterest(int row)` |  |
| 5029 | 14 | `public double disposableWithRowMoved(int row, double rowChange)` | What one household of a row would have had to spend with the row's income moved by `rowChange` (0.7.36, the Policy spec's M11): the move split across the row's cells by advanceMonth()'s own rule - splitIncome(), by ea... |
| 5043 | 1 | `public double getDrawn(int row)` |  |
| 5046 | 1 | `public double getUnfunded(int row)` | What this row wanted, could not fund, and did not get. |
| 5049 | 4 | `public boolean isCutOff(int row)` | True when the bank has stopped lending to any cell of this row - ceiling or lockout. |
| 5053 | 1 | `public double getBorrowed(int row)` |  |
| 5054 | 1 | `public double getRepaid(int row)` |  |
| 5055 | 1 | `public double getBanked(int row)` |  |
| 5056 | 1 | `public double getWant(int row)` |  |
| 5057 | 1 | `public double getPlanned(int row)` |  |
| 5058 | 1 | `public double getRate(int row)` |  |
| 5061 | 4 | `public boolean isGoingShort(int row)` | True when this row is buying less food than it wants. |
| 5067 | 1 | `public double totalSavings()` | City totals, for the headline lines on the screen. |
| 5068 | 1 | `public double totalDebt()` |  |
| 5069 | 1 | `public double totalInterest()` |  |
| 5078 | 4 | `public double averageRate()` | What the families' credit lines cost them this month on average: each cell's rate weighted by what it owes - the bank's household rate plus RISK_SLOPE for every month of income owed, capped at MAX_RATE (Household.sett... |
| 5084 | 1 | `public double totalBorrowed()` | New lending to families this month - the bank's money out the door. |
| 5087 | 1 | `public double totalRepaid()` | ...and what came back: the cells' repayments and the estates' (HS1, the dead's debt settled from their savings). |
| 5090 | 1 | `public double totalNetWorth()` | What every household in the city has, less what it owes. |

### saving (lines 5092-5631)

| line | len | member | says |
|---:|---:|---|---|
| 5112 | 44 | `public double[] toSaveArray()` | The row array an older build reads: ROWS*8+5 since 0.7.27 (ROWS*8+3 before), per household of the row. |
| 5249 | 5 | `public String[] cellKeys()` | The name of every cell, in the order toCellSaveArray() writes them. |
| 5256 | 33 | `public double[] toCellSaveArray()` | CELL_SLOTS per cell, in cellKeys() order, then the three city figures. |
| 5301 | 145 | `public boolean restoreCells(String[] keys, double[] saved, String[] savedCompanies)` | Puts the cells back, by name. |
| 5465 | 58 | `public void restore(double[] saved, ToDoubleBiFunction<FamilyStructure, PayTier> census)` | Puts a ROW array back, seeding every cell of the row with the row's position: the save from a build that kept the stocks per tier. |
| 5532 | 8 | `private void seedGroceries(Household c)` | The last sale's baskets for a household from a save before 0.7.43, which carries the money measure its hunger was struck on: its plan times the share the shops handed over, against subsistence - turned into baskets by... |
| 5542 | 1 | `public void restore(double[] saved)` | The row array alone, with no census: the cells wait for the plan to count them. |
| 5544 | 48 | `public void reset()` |  |
| 5599 | 31 | `public void redenominate(double scale)` | The households' stocks and this month's working, in the new unit. |

