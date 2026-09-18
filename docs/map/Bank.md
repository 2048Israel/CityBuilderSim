# Bank.java - 2,208 lines · 133 methods · 23 constants · model

`ham/citybuildersim/Bank.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> The city's commercial bank: every loan in it, and every default.
> 
> ==================== WHY THIS EXISTS ====================
> 
> There were three lenders in this game and none of them was anybody. A sector
> short of cash borrowed from `BusinessDebtManager`, the city sold bonds to a
> market, and - since the household balance sheet went in - a family short of
> the shop borrowed from nobody at all. Money arrived from outside the city and
> interest disappeared out of it, and when a sector was written down in a
> restructure the loss was written off against nothing.
> 
> Jerus: "we need to add a commercial bank, which will be the one everyone,
> everyone lends money from, the one earning all the interest, and the one
> getting billed the horrible bankruptcies."
> 
> So there is one now, and it is a building. What changes is not the arithmetic
> of any single loan - the rates and schedules are the same - but that the
> money has somewhere to come from and somewhere to go, and that somewhere can
> run out and can be bankrupted.
> 
> ==================== WHAT SETS THE PRICE ====================
> 
> Two limits, and the tighter one binds - Jerus's answer, and it is the right
> one because the two fail differently:
> 
>   DEPOSITS. A bank lends a multiple of what the city has banked with it -
>   households' savings and the sectors' cash. A city with no savings is a city
>   with nothing to lend, whatever it has built.
> 
>   BRANCHES. One building can only carry so much of a loan book. A city with
>   deep savings and one branch is a city queueing at one counter.
> 
> Past the tighter of the two the bank does not refuse - it funds the rest
> abroad and charges for it, which is what a real bank does and what makes the
> no-bank case fall out of the same formula rather than needing a rule: a city
> with no branches has no capacity, is therefore infinitely strained, and pays
> the maximum premium on everything it borrows. Jerus asked for "credit at a
> punitive rate" with no bank, and this is that, without a special case.
> 
> ==================== WHAT IS AND IS NOT INSIDE ====================
> 
> The bank holds cash and it is one of MoneyAudit's pools, so lending to a
> SECTOR or to the CITY is now an internal transfer that cancels - the money
> moves from the bank's account to theirs and back with interest, and none of
> it enters or leaves the city any more. That is a strictly better identity
> than the one it replaces, where both ends were the outside world.
> 
> HOUSEHOLDS ARE STILL OUTSIDE IT, as they have always been - wages leave the
> audited system and rent and shopping come back into it - so lending to a
> family is a real outflow and its repayments are real inflows. Their savings
> are counted as deposits for the capacity above WITHOUT being held as the
> bank's cash, which is the one deliberate fudge here and is stated rather than
> hidden: the alternative is to make every household a pool, which is a
> different and much larger model.
> 
> A write-off costs the bank its BOOK, not its cash. Writing off a loan is the
> loss of a receivable; the money went out the door months ago.

**Uses:** [DebtManager](DebtManager.md) (1)

**Used by (19):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [DebtManager](DebtManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [Motoring](Motoring.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md)

## Sections

| line | section |
|---:|---|
| 64 | · the dials |
| 142 | · what a dollar of book WEIGHS |
| 239 | · the position |
| 259 | · the month's working |
| 297 | · the month |
| 352 | · carry |
| 526 | · the arithmetic |
| 553 | THE TRADING DESK |
| 1168 | · the flows |
| 1244 | THE FUNDING SIDE |
| 1458 | · · SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY. |
| 1508 | WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. |
| 1666 | THE THREE STATEMENTS |
| 1754 | · tax |
| 1824 | · LAST MONTH, KEPT ON PURPOSE |
| 1918 | · reading |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 73 | `Bank.LEVERAGE` | `6` | How many times its deposits the bank will lend. |
| 101 | `Bank.CAPITAL_RATIO` | `.08` | Equity a bank must hold against its risk-weighted book. |
| 114 | `Bank.DEPOSITS_PER_BRANCH` | `250_000` | How much of a city's savings one branch can gather. |
| 137 | `Bank.PAID_IN_PER_BRANCH` | `32_000` | What the shareholders put up when a branch opens. |
| 165 | `Bank.RISK_CITY` | `.20` | The city cannot default on its own paper. |
| 168 | `Bank.RISK_BUSINESS` | `1.00` | A business can be restructured, and in this game regularly is. |
| 171 | `Bank.RISK_HOUSEHOLD` | `1.00` | ...and a family can be discharged. |
| 187 | `Bank.RISK_CARRY` | `1.00` | A foreign carry borrower, against the risk weights above. |
| 190 | `Bank.SHORTEST_WEIGHT` | `.40` | What a loan repaying tomorrow weighs against one repaying never. |
| 193 | `Bank.LONG_TERM_MONTHS` | `60` | Months of remaining term at which a loan weighs its full amount. |
| 208 | `Bank.EASY_STRAIN` | `.80` | Where the premium starts biting, as a share of capacity lent out. |
| 224 | `Bank.BUILD_AT_STRAIN` | `.70` | Where the private sector starts building, which is BEFORE it bites. |
| 227 | `Bank.HARD_STRAIN` | `1.50` | ...and where it is fully bitten. |
| 237 | `Bank.MAX_STRAIN_PREMIUM` | `.18` | The most the strain can add to any borrower's annual rate. |
| 294 | `Bank.PLACEMENT_RATE` | `DebtManager.WORLD_BASE_RATE` |  |
| 425 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 567 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 807 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 937 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 1327 | `Bank.DEPOSIT_PASS_THROUGH` | `.45` | What share of its INTEREST INCOME the bank passes on to its depositors. |
| 1361 | `Bank.FUNDING_SPREAD` | `.02` | Over the risk-free rate, for being a bank rather than a treasury. |
| 1364 | `Bank.FUNDING_STRETCH` | `.06` | ...and more, the further past its deposits it has reached. |
| 1561 | `Bank.RATE_STEPS` | `12` | How many candidate rates the bank considers between nothing and its ceiling. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 117 | `private double depositsPerBranch` | The same, in today's money - reformed with every other figure. |
| 140 | `private double paidInPerBranch` | The same, in today's money. |
| 241 | `private double cash` |  |
| 242 | `private double branches` |  |
| 243 | `private double deposits` |  |
| 246 | `private double householdDeposits` | The two halves of that, because they are paid separately. |
| 247 | `private double sectorDeposits` |  |
| 250 | `private double sectorBook` | What is lent out, by whom it is owed. |
| 251 | `private double cityBook` |  |
| 252 | `private double householdBook` |  |
| 255 | `private double sectorWeighted` | The same three, weighted for risk and remaining term. |
| 256 | `private double cityWeighted` |  |
| 257 | `private double householdWeighted` |  |
| 261 | `private double interestEarned` |  |
| 262 | `private double writeOffs` |  |
| 263 | `private double payroll` |  |
| 264 | `private double upkeep` |  |
| 265 | `private double lentToHouseholds` |  |
| 266 | `private double repaidByHouseholds` |  |
| 267 | `private double fundingCost` |  |
| 293 | `private double placementIncome` | WHAT THE VAULT EARNS WHILE NOBODY IS BORROWING (2026-09-11). |
| 295 | `private double openingEquity` |  |
| 309 | `private double carryBook` | The fourth book: what foreigners have borrowed to take abroad. |
| 310 | `private double carryLent, carryRepaid, carryInterest` |  |
| 468 | `private double lastCostOfFunds` | What money cost this bank over the month that just closed. |
| 570 | `private double securities` | The desk's inventory, at the exchange's mark. |
| 578 | `private double tradingIncome` | The trading result so far this month: cash from what it sold less cash for what it bought, plus the change in what it holds at the mark, plus the dividends its inventory was paid. |
| 658 | `private double foreignDeposits` |  |
| 696 | `private double hotMoneyIn, hotMoneyOut` |  |
| 772 | `private boolean inResolution` | Failed, and not yet put back on its feet. |
| 773 | `private int failures` |  |
| 785 | `private double resolutionLossThisMonth` | ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. |
| 786 | `private double resolutionLossLifetime` |  |
| 949 | `private double domesticCapitalScale` | The same, in today's money. |
| 1026 | `private double dividendsPaid` |  |
| 1068 | `private double capitalInjected` |  |
| 1069 | `private double capitalFromHome` |  |
| 1070 | `private double bailoutReceived` |  |
| 1071 | `private double foundingSettlement` |  |
| 1142 | `private double branchesCapitalised` |  |
| 1181 | `private double internalInterest` |  |
| 1329 | `private double depositRate` |  |
| 1330 | `private double depositInterestToHouseholds` |  |
| 1331 | `private double depositInterestToSectors` |  |
| 1341 | `private double depositInterestToForeign` | ...and what the hot money is paid for parking here. |
| 1366 | `private double fundingRate` |  |
| 1564 | `private DepositMarket depositMarket` | Set by Game each month so the bank can price deposits without knowing what a carry trade is. |
| 1656 | `private int monthsBidUp` | HOW OFTEN THE BANK ACTUALLY BID FOR MONEY. |
| 1657 | `private double lastBidUpGain` |  |
| 1781 | `private double taxPaid` | Jerus: "quick question banks are taxed right?" |
| 1782 | `private double profitLastMonth` |  |
| 1843 | `private double lastPayroll, lastUpkeep, lastInterest, lastBook` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 2147 | **type** `public class Bank` | The city's commercial bank: every loan in it, and every default. |

### the dials (lines 64-141)

### what a dollar of book WEIGHS (lines 142-238)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 4 | `public static double maturityWeight(double remainingMonths)` | How much of its face a loan with this long left to run counts for. |

### the position (lines 239-258)

### the month's working (lines 259-296)

### the month (lines 297-351)

| line | len | member | says |
|---:|---:|---|---|
| 319 | 18 | `public void refresh(double branches, double householdSavings, double sectorCash, double sectorBook, double cityBook, double hou...` | Re-reads the city and re-prices credit. |
| 346 | 5 | `public void setWeightedBook(double sector, double city, double household)` | The same book, weighed by what each loan actually is. |

### carry (lines 352-525)

| line | len | member | says |
|---:|---:|---|---|
| 363 | 6 | `public void lendCarry(double amount)` | Lend to a foreigner who is about to take it abroad. |
| 371 | 7 | `public void repayCarry(double amount)` | ...and they bring it home. |
| 387 | 6 | `public void takeCarryInterest(double amount)` | The coupon, which arrives from abroad. |
| 403 | 1 | `public void setCarryBook(double amount)` | The carry book, set from the stock that owns it. |
| 405 | 1 | `public double getCarryBook()` |  |
| 406 | 1 | `public double getCarryLent()` |  |
| 407 | 1 | `public double getCarryRepaid()` |  |
| 408 | 1 | `public double getCarryInterest()` |  |
| 411 | 4 | `public double lendingRate(double riskFreeAnnual)` | What a good credit pays to borrow here. |
| 465 | 1 | `public double marginalCostOfFunds()` | WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND. |
| 487 | 8 | `private double blendedCostOfFunds()` | The two tranches, blended at the weights the bank actually used them. |
| 497 | 28 | `public void startMonth()` | Clears the month's flows. |

### the arithmetic (lines 526-552)

| line | len | member | says |
|---:|---:|---|---|
| 538 | 1 | `public double getBook()` | Everything lent, carry included. |
| 547 | 5 | `public double getWeightedBook()` | The book as CAPACITY sees it: risk- and maturity-weighted. |

### THE TRADING DESK (lines 553-1167)

| line | len | member | says |
|---:|---:|---|---|
| 581 | 4 | `public void markSecurities(double value)` | The exchange re-marks the inventory. |
| 587 | 5 | `public void deskPays(double cash)` | The desk paid cash for shares. |
| 594 | 5 | `public void deskReceives(double cash)` | ...and was paid for shares it sold. |
| 601 | 5 | `public void receiveDividend(double amount)` | Dividends on the inventory: income, and cash. |
| 608 | 1 | `public void restoreSecurities(double value)` | The load path puts the mark back without calling it income. |
| 610 | 1 | `public double getSecurities()` |  |
| 611 | 1 | `public double getTradingIncome()` |  |
| 614 | 4 | `public double weightingRelief()` | How much lighter the weighting makes the book. |
| 627 | 12 | `public double capacity()` | The most the bank can lend before it is funding itself abroad. |
| 652 | 5 | `public double depositsGathered()` | What the branches let it gather, out of what the city has to bank - PLUS whatever the world has parked here, which needed no branch at all. |
| 661 | 1 | `public double getForeignDeposits()` | Hot money currently funding this bank, in local money. |
| 664 | 4 | `public void setForeignDeposits(double amount)` | Told by Game each month, from CapitalFlows. |
| 676 | 5 | `public void receiveHotMoney(double amount)` | Hot money arriving, as cash. |
| 690 | 5 | `public void returnHotMoney(double amount)` | ...and leaving, which is the whole danger. |
| 698 | 1 | `public double getHotMoneyIn()` |  |
| 699 | 1 | `public double getHotMoneyOut()` |  |
| 727 | 3 | `public double fundingCostAbroad()` | The bank's wholesale funding cost, split by whose money it is. |
| 732 | 3 | `public double fundingCostAtHome()` | ...and the part paid to lenders down the road. |
| 737 | 4 | `public double hotFundingShare()` | The share of the bank's funding that could leave at any time. |
| 743 | 3 | `public double capitalLimit()` | What its capital supports, on the risk-weighted book. |
| 748 | 3 | `public boolean capitalBound()` | True when it is the capital that binds rather than the funding. |
| 758 | 4 | `public double capitalRatio()` | Equity against the risk-weighted book - the ratio a regulator reads. |
| 788 | 1 | `public boolean isInsolvent()` |  |
| 790 | 1 | `public double getResolutionLoss()` | Over the city's life. |
| 792 | 1 | `public double getResolutionLossThisMonth()` | This month only. |
| 793 | 1 | `public int getFailures()` |  |
| 830 | 3 | `public double[] solvencyToSave()` | The solvency record, for the save. |
| 834 | 6 | `public void restoreSolvency(double[] state)` |  |
| 857 | 24 | `public void resolveIfFailed()` | The bank fails, and its creditors take the loss. |
| 891 | 22 | `public double resolutionExitEquity()` | The equity at which the freeze lifts, however the bank gets there. |
| 920 | 6 | `public double recapitalisationNeeded()` | What it would take to put the bank back on its feet, with a buffer. |
| 952 | 5 | `public double domesticCapitalShare()` | The share of new paid-in capital the city's own savers can find. |
| 981 | 13 | `public void injectCapital(double amount)` | Shareholders' money, put in as capital - and WHOSE shareholders. |
| 1005 | 7 | `public void injectCapital(double fromHome, double fromAbroad)` | Shareholders' money, put in as capital, and whose: the households' part came out of their savings through the register, the rest from the world. |
| 1020 | 5 | `public void payDividend(double amount)` | Pays the owners. |
| 1029 | 1 | `public double getDividendsPaid()` | What it paid its shareholders this month. |
| 1060 | 7 | `public void receiveBailout(double amount)` | The same money, from the TREASURY rather than from shareholders. |
| 1077 | 1 | `public double getFoundingSettlement()` | The one-off jump in the bank's cash when the city's first branch opens and it takes over the standing loan book. |
| 1080 | 1 | `public double getCapitalInjected()` | Shareholders' money from OUTSIDE the city. |
| 1089 | 1 | `public double getCapitalFromHome()` | ...and from the city's own savers. |
| 1092 | 1 | `public double getBailoutReceived()` | The treasury's money, from inside it. |
| 1106 | 35 | `public double openBranches(double nowStanding)` | Opens whatever branches have been built since last month, and says what capital they need. |
| 1143 | 1 | `public double getBranchesCapitalised()` |  |
| 1144 | 1 | `public void setBranchesCapitalised(double n)` |  |
| 1147 | 5 | `public double strain()` | Book over capacity. |
| 1160 | 7 | `public double ratePremium()` | What the strain adds to every borrower's annual rate. |

### the flows (lines 1168-1243)

| line | len | member | says |
|---:|---:|---|---|
| 1174 | 6 | `public void takeInterest(double amount)` | Interest arriving from a SECTOR or the CITY - both inside the audit, so this is a transfer between pools and declares nothing. |
| 1184 | 1 | `public double getInternalInterest()` | The part of the month's interest that came from inside the city's pools. |
| 1193 | 5 | `public void takeDiscount(double amount)` | Paper bought below par: income the bank earns without any cash moving. |
| 1200 | 4 | `public void takeRepayment(double amount)` | Principal coming back. |
| 1206 | 4 | `public void lend(double amount)` | Money out the door. |
| 1212 | 5 | `public void lendToHouseholds(double amount)` | Lending to a family, which crosses the audit's boundary and is counted. |
| 1219 | 4 | `public void takeFromHouseholds(double principal, double interest)` | ...and from a household, which is outside, so both halves are declared. |
| 1232 | 4 | `public void writeOff(double amount)` | A loan that will not be repaid. |
| 1238 | 5 | `public void payRunning(double payroll, double upkeep)` | Wages and running costs, which are real money leaving. |

### THE FUNDING SIDE (lines 1244-1507)

| line | len | member | says |
|---:|---:|---|---|
| 1344 | 1 | `public double depositRate()` | What savers are being paid. |
| 1346 | 1 | `public double getDepositInterestToHouseholds()` |  |
| 1347 | 1 | `public double getDepositInterestToSectors()` |  |
| 1348 | 1 | `public double getDepositInterestToForeign()` |  |
| 1349 | 4 | `public double depositInterest()` |  |
| 1355 | 4 | `public double netInterestMargin()` | What it charges borrowers, less what it pays savers. |
| 1369 | 1 | `public double borrowings()` | Everything it owes: the mirror of a negative cash position. |
| 1379 | 1 | `public double depositFunding()` | The cheap tranche - the city's own money, lent back out. |
| 1382 | 1 | `public double wholesaleFunding()` | ...and the part it had to go to the market for. |
| 1391 | 1 | `public double fundingRate()` | What the bank is paying for the money it did not have. |
| 1404 | 103 | `public void fundToCover(double riskFreeAnnual)` | Settles the funding for the month: charge for what was borrowed, then borrow what is short or repay what is spare. |

### WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. (lines 1508-1665)

| line | len | member | says |
|---:|---:|---|---|
| 1567 | 3 | **type** `public interface DepositMarket` | What the world would park here at a given deposit rate. |
| 1568 | 1 | `double arrivalsAt(double depositRate)` _(in Bank.DepositMarket)_ |  |
| 1571 | 1 | `public void setDepositMarket(DepositMarket market)` |  |
| 1578 | 68 | `private double chooseDepositRate(double riskFreeAnnual)` | The month's deposit interest, in money. |
| 1659 | 1 | `public int getMonthsBidUp()` |  |
| 1660 | 1 | `public double getLastBidUpGain()` |  |
| 1662 | 1 | `public double getFundingCost()` |  |
| 1664 | 1 | `public double getPlacementIncome()` | What the idle reserves earned abroad this month. |

### THE THREE STATEMENTS (lines 1666-1753)

| line | len | member | says |
|---:|---:|---|---|
| 1689 | 1 | `public double cashReserves()` | Cash it is actually sitting on. |
| 1692 | 1 | `public double totalAssets()` | Total assets: the loan book, whatever cash it has not lent, and what the desk holds. |
| 1695 | 1 | `public double shortSecurities()` | ...and a desk that is short owes the shares: a liability at the mark. |
| 1717 | 1 | `public double totalLiabilities()` | What the bank owes: its market funding, and the hot money. |
| 1727 | 1 | `public double equity()` | The residual - and, once the two above are written out, simply the book plus the cash position. |
| 1730 | 1 | `public double getOpeningEquity()` | Equity as it stood at the top of the month. |
| 1735 | 1 | `public double interestIncome()` | What every borrower paid it this month. |
| 1738 | 3 | `public double netInterestIncome()` | ...less what it paid savers and what it paid the market. |
| 1743 | 1 | `public double afterLosses()` | ...less the loans that will not come back. |
| 1746 | 1 | `public double operatingExpenses()` | ...less the tellers and the lights. |
| 1749 | 1 | `public double afterTrading()` | ...plus what the desk made or lost. |
| 1752 | 1 | `public double profitBeforeTax()` | What it made before the city took its share. |

### tax (lines 1754-1823)

| line | len | member | says |
|---:|---:|---|---|
| 1795 | 1 | `public double getProfitLastMonth()` | Profit the tax is charged on: the month that has just finished. |
| 1798 | 25 | `public void closeMonth()` | Called at the end of the month, once the profit is final. |

### LAST MONTH, KEPT ON PURPOSE (lines 1824-1917)

| line | len | member | says |
|---:|---:|---|---|
| 1845 | 4 | `public double[] lastMonthToSave()` |  |
| 1872 | 10 | `public void restoreLastMonth(double[] state)` | ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the cost-of-funds floor. |
| 1884 | 1 | `public void setProfitLastMonth(double value)` | Restored from the save, so next month taxes the right figure. |
| 1896 | 1 | `public void setDepositRate(double rate)` | What savers were paid in the month this save was taken in. |
| 1899 | 1 | `public double getTaxPaid()` | What the city took this month. |
| 1912 | 5 | `public double chargeTax(double annualProfitRate)` | Hands the city its share of last month's profit. |

### reading (lines 1918-2208)

| line | len | member | says |
|---:|---:|---|---|
| 1920 | 1 | `public double getCash()` |  |
| 1921 | 1 | `public double getDeposits()` |  |
| 1922 | 1 | `public double getBranches()` |  |
| 1923 | 1 | `public double getSectorBook()` |  |
| 1924 | 1 | `public double getCityBook()` |  |
| 1925 | 1 | `public double getHouseholdBook()` |  |
| 1926 | 1 | `public double getInterestEarned()` |  |
| 1927 | 1 | `public double getWriteOffs()` |  |
| 1928 | 1 | `public double getPayroll()` |  |
| 1929 | 1 | `public double getUpkeep()` |  |
| 1930 | 1 | `public double getLentToHouseholds()` |  |
| 1931 | 1 | `public double getRepaidByHouseholds()` |  |
| 1941 | 3 | `public double getNetIncome()` | The month's profit: interest earned, less the cost of the money, less the loans that died, less the cost of running the place. |
| 1946 | 1 | `public boolean isStrained()` | True when the bank is lending past what it holds and charging for it. |
| 1956 | 31 | `public boolean wantsBranch()` | True when the city should be opening another counter. |
| 2016 | 37 | `public boolean branchWouldPayForItself()` | Whether the counter earns more than it costs to keep open. |
| 2068 | 7 | `public double bookAnotherBranchWouldCarry()` | The loan book one more branch would take onto the bank's own account. |
| 2087 | 8 | `public double capacityWith(double branchCount)` | Capacity this bank would have with a given number of branches. |
| 2097 | 3 | `public double headroom()` | How much more it could lend before the premium starts. |
| 2101 | 1 | `public void setCash(double value)` |  |
| 2103 | 23 | `public void reset()` |  |
| 2146 | 53 | `public void redenominate(double scale)` | Every figure on the bank's balance sheet, in the new unit. |
| 2202 | 5 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

