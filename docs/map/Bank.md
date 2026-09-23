# Bank.java - 2,441 lines · 146 methods · 20 constants · model

`ham/citybuildersim/Bank.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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
> (at the central bank's window since 0.7.0, abroad before it) and charges
> for it, which is what a real bank does and what makes the no-bank case fall out of the same formula rather than needing a rule: a city
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

**Uses:** [CentralBank](CentralBank.md) (1)

**Used by (23):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [BusinessDebtManager](BusinessDebtManager.md), [BusinessInvestment](BusinessInvestment.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CentralBankCheck](CentralBankCheck.md), [DebtManager](DebtManager.md), [EquityCheck](EquityCheck.md), [Exchange](Exchange.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [Game](Game.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [HouseholdBalance](HouseholdBalance.md), [Inbox](Inbox.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [Motoring](Motoring.md), [PolicyScreen](PolicyScreen.md), [SummaryScreen](SummaryScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 64 | · the dials |
| 142 | · what a dollar of book WEIGHS |
| 239 | · the position |
| 259 | · the month's working |
| 311 | · the month |
| 366 | · carry |
| 547 | · the arithmetic |
| 574 | THE TRADING DESK |
| 1190 | · the flows |
| 1263 | THE CITY'S PAPER CHANGES HANDS (0.7.1) |
| 1398 | THE FUNDING SIDE |
| 1670 | · · SPLIT THREE WAYS, NOT TWO, AND IT USED TO LOSE MONEY. |
| 1723 | WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. |
| 1883 | THE THREE STATEMENTS |
| 1978 | · tax |
| 2048 | · LAST MONTH, KEPT ON PURPOSE |
| 2142 | · reading |

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
| 440 | `Bank.MIN_MARGIN` | `.01` | The least a lender takes for writing the loan at all. |
| 588 | `Bank.RISK_EQUITY` | `1.50` | What a dollar of shares on the desk weighs against capital. |
| 821 | `Bank.RESOLUTION_EXIT_BUFFER` | `1.5` | How far above the required ratio a rescued bank comes out. |
| 959 | `Bank.DOMESTIC_CAPITAL_SCALE` | `400_000` | Deposits at which half of new bank capital is found at home. |
| 1494 | `Bank.DEPOSIT_PASS_THROUGH` | `.45` | What share of its INTEREST INCOME the bank passes on to its depositors. |
| 1777 | `Bank.RATE_STEPS` | `12` | How many candidate rates the bank considers between nothing and its ceiling. |

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
| 308 | `private double placementIncome` | WHAT ITS SPARE CASH EARNS: THE POLICY RATE, AT THE CENTRAL BANK (0.7.0). |
| 309 | `private double openingEquity` |  |
| 323 | `private double carryBook` | The fourth book: what foreigners have borrowed to take abroad. |
| 324 | `private double carryLent, carryRepaid, carryInterest` |  |
| 485 | `private double lastCostOfFunds` | What money cost this bank over the month that just closed. |
| 591 | `private double securities` | The desk's inventory, at the exchange's mark. |
| 599 | `private double tradingIncome` | The trading result so far this month: cash from what it sold less cash for what it bought, plus the change in what it holds at the mark, plus the dividends its inventory was paid. |
| 611 | `private double markChange` | The part of the trading result that is the re-mark: every change in what the inventory is carried at this month, summed. |
| 696 | `private double foreignDeposits` |  |
| 734 | `private double hotMoneyIn, hotMoneyOut` |  |
| 786 | `private boolean inResolution` | Failed, and not yet put back on its feet. |
| 787 | `private int failures` |  |
| 799 | `private double resolutionLossThisMonth` | ONE FIELD WAS BEING ASKED TO BE A FLOW AND A STOCK. |
| 800 | `private double resolutionLossLifetime` |  |
| 971 | `private double domesticCapitalScale` | The same, in today's money. |
| 1048 | `private double dividendsPaid` |  |
| 1090 | `private double capitalInjected` |  |
| 1091 | `private double capitalFromHome` |  |
| 1092 | `private double bailoutReceived` |  |
| 1093 | `private double foundingSettlement` |  |
| 1164 | `private double branchesCapitalised` |  |
| 1203 | `private double internalInterest` |  |
| 1278 | `private double paperGains` | The gain or loss on the city's paper that changed hands this month. |
| 1281 | `private double paperBoughtFromHouseholds` | What the desk paid households for their paper this month: money leaving the pools for a household, which MoneyAudit declares. |
| 1284 | `private double paperSoldToCentralBank, paperBoughtFromCentralBank` | What the central bank paid it for paper this month, and what it paid the central bank. |
| 1298 | `private double unearnedDiscount` | THE UNEARNED DISCOUNT (0.7.1): the part of what the bank paid under face for the city's paper it has not yet accreted into income, carried as a liability against the book - so equity does not jump by the discount the ... |
| 1350 | `private double buybackGains` | Gains less losses on paper the treasury bought back, over the city's life. |
| 1496 | `private double depositRate` |  |
| 1497 | `private double depositInterestToHouseholds` |  |
| 1498 | `private double depositInterestToSectors` |  |
| 1508 | `private double depositInterestToForeign` | ...and what the hot money is paid for parking here. |
| 1516 | `private boolean depositRateCapped` |  |
| 1543 | `private double fundingRate` | THE PRICE OF WHOLESALE MONEY WAS TWO DIALS until 0.7.0: FUNDING_SPREAD, two points over the risk-free rate "for being a bank rather than a treasury", and FUNDING_STRETCH, six points more at a reach of one deposit book... |
| 1780 | `private DepositMarket depositMarket` | Set by Game each month so the bank can price deposits without knowing what a carry trade is. |
| 1872 | `private int monthsBidUp` | HOW OFTEN THE BANK ACTUALLY BID FOR MONEY. |
| 1873 | `private double lastBidUpGain` |  |
| 2005 | `private double taxPaid` | Jerus: "quick question banks are taxed right?" |
| 2006 | `private double profitLastMonth` |  |
| 2067 | `private double lastPayroll, lastUpkeep, lastInterest, lastBook` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 62 | 2380 | **type** `public class Bank` | The city's commercial bank: every loan in it, and every default. |

### the dials (lines 64-141)

### what a dollar of book WEIGHS (lines 142-238)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 4 | `public static double maturityWeight(double remainingMonths)` | How much of its face a loan with this long left to run counts for. |

### the position (lines 239-258)

### the month's working (lines 259-310)

### the month (lines 311-365)

| line | len | member | says |
|---:|---:|---|---|
| 333 | 18 | `public void refresh(double branches, double householdSavings, double sectorCash, double sectorBook, double cityBook, double hou...` | Re-reads the city and re-prices credit. |
| 360 | 5 | `public void setWeightedBook(double sector, double city, double household)` | The same book, weighed by what each loan actually is. |

### carry (lines 366-546)

| line | len | member | says |
|---:|---:|---|---|
| 377 | 6 | `public void lendCarry(double amount)` | Lend to a foreigner who is about to take it abroad. |
| 385 | 7 | `public void repayCarry(double amount)` | ...and they bring it home. |
| 402 | 6 | `public void takeCarryInterest(double amount)` | The coupon, which arrives from abroad. |
| 418 | 1 | `public void setCarryBook(double amount)` | The carry book, set from the stock that owns it. |
| 420 | 1 | `public double getCarryBook()` |  |
| 421 | 1 | `public double getCarryLent()` |  |
| 422 | 1 | `public double getCarryRepaid()` |  |
| 423 | 1 | `public double getCarryInterest()` |  |
| 426 | 4 | `public double lendingRate(double riskFreeAnnual)` | What a good credit pays to borrow here. |
| 482 | 1 | `public double marginalCostOfFunds()` | WHAT THE NEXT DOLLAR OF LENDING COSTS THIS BANK TO FUND. |
| 504 | 8 | `private double blendedCostOfFunds()` | The two tranches, blended at the weights the bank actually used them. |
| 514 | 32 | `public void startMonth()` | Clears the month's flows. |

### the arithmetic (lines 547-573)

| line | len | member | says |
|---:|---:|---|---|
| 559 | 1 | `public double getBook()` | Everything lent, carry included. |
| 568 | 5 | `public double getWeightedBook()` | The book as CAPACITY sees it: risk- and maturity-weighted. |

### THE TRADING DESK (lines 574-1189)

| line | len | member | says |
|---:|---:|---|---|
| 614 | 5 | `public void markSecurities(double value)` | The exchange re-marks the inventory. |
| 621 | 5 | `public void deskPays(double cash)` | The desk paid cash for shares. |
| 628 | 5 | `public void deskReceives(double cash)` | ...and was paid for shares it sold. |
| 635 | 5 | `public void receiveDividend(double amount)` | Dividends on the inventory: income, and cash. |
| 642 | 1 | `public void restoreSecurities(double value)` | The load path puts the mark back without calling it income. |
| 644 | 1 | `public double getSecurities()` |  |
| 645 | 1 | `public double getTradingIncome()` |  |
| 648 | 1 | `public double getMarkChange()` | What re-marking the inventory did to this month's trading result. |
| 651 | 4 | `public double weightingRelief()` | How much lighter the weighting makes the book. |
| 665 | 12 | `public double capacity()` | The most the bank can lend before it is funding itself at the central bank's window (abroad, until 0.7.0). |
| 690 | 5 | `public double depositsGathered()` | What the branches let it gather, out of what the city has to bank - PLUS whatever the world has parked here, which needed no branch at all. |
| 699 | 1 | `public double getForeignDeposits()` | Hot money currently funding this bank, in local money. |
| 702 | 4 | `public void setForeignDeposits(double amount)` | Told by Game each month, from CapitalFlows. |
| 714 | 5 | `public void receiveHotMoney(double amount)` | Hot money arriving, as cash. |
| 728 | 5 | `public void returnHotMoney(double amount)` | ...and leaving, which is the whole danger. |
| 736 | 1 | `public double getHotMoneyIn()` |  |
| 737 | 1 | `public double getHotMoneyOut()` |  |
| 751 | 4 | `public double hotFundingShare()` | The share of the bank's funding that could leave at any time. |
| 757 | 3 | `public double capitalLimit()` | What its capital supports, on the risk-weighted book. |
| 762 | 3 | `public boolean capitalBound()` | True when it is the capital that binds rather than the funding. |
| 772 | 4 | `public double capitalRatio()` | Equity against the risk-weighted book - the ratio a regulator reads. |
| 802 | 1 | `public boolean isInsolvent()` |  |
| 804 | 1 | `public double getResolutionLoss()` | Over the city's life. |
| 806 | 1 | `public double getResolutionLossThisMonth()` | This month only. |
| 807 | 1 | `public int getFailures()` |  |
| 844 | 3 | `public double[] solvencyToSave()` | The solvency record, for the save. |
| 848 | 6 | `public void restoreSolvency(double[] state)` |  |
| 879 | 24 | `public void resolveIfFailed()` | The bank fails, and its creditors take the loss. |
| 913 | 22 | `public double resolutionExitEquity()` | The equity at which the freeze lifts, however the bank gets there. |
| 942 | 6 | `public double recapitalisationNeeded()` | What it would take to put the bank back on its feet, with a buffer. |
| 974 | 5 | `public double domesticCapitalShare()` | The share of new paid-in capital the city's own savers can find. |
| 1003 | 13 | `public void injectCapital(double amount)` | Shareholders' money, put in as capital - and WHOSE shareholders. |
| 1027 | 7 | `public void injectCapital(double fromHome, double fromAbroad)` | Shareholders' money, put in as capital, and whose: the households' part came out of their savings through the register, the rest from the world. |
| 1042 | 5 | `public void payDividend(double amount)` | Pays the owners. |
| 1051 | 1 | `public double getDividendsPaid()` | What it paid its shareholders this month. |
| 1082 | 7 | `public void receiveBailout(double amount)` | The same money, from the TREASURY rather than from shareholders. |
| 1099 | 1 | `public double getFoundingSettlement()` | The one-off jump in the bank's cash when the city's first branch opens and it takes over the standing loan book. |
| 1102 | 1 | `public double getCapitalInjected()` | Shareholders' money from OUTSIDE the city. |
| 1111 | 1 | `public double getCapitalFromHome()` | ...and from the city's own savers. |
| 1114 | 1 | `public double getBailoutReceived()` | The treasury's money, from inside it. |
| 1128 | 35 | `public double openBranches(double nowStanding)` | Opens whatever branches have been built since last month, and says what capital they need. |
| 1165 | 1 | `public double getBranchesCapitalised()` |  |
| 1166 | 1 | `public void setBranchesCapitalised(double n)` |  |
| 1169 | 5 | `public double strain()` | Book over capacity. |
| 1182 | 7 | `public double ratePremium()` | What the strain adds to every borrower's annual rate. |

### the flows (lines 1190-1262)

| line | len | member | says |
|---:|---:|---|---|
| 1196 | 6 | `public void takeInterest(double amount)` | Interest arriving from a SECTOR or the CITY - both inside the audit, so this is a transfer between pools and declares nothing. |
| 1206 | 1 | `public double getInternalInterest()` | The part of the month's interest that came from inside the city's pools. |
| 1215 | 5 | `public void takeDiscount(double amount)` | Paper bought below par: income the bank earns without any cash moving. |
| 1239 | 3 | `public double sellPaperBack(double price, double principal)` | THE TREASURY BUYS ITS PAPER BACK FROM THE BANK (0.7.0), which is who holds it: the price arrives as cash, the book drops by the principal at once rather than at the next refresh, and the difference is the bank's gain ... |
| 1250 | 12 | `public double sellPaperBack(double price, double principal, double unearned)` | ...and since 0.7.1 at amortised cost: the unearned discount riding on the face that leaves goes with it, so the gain is the price less what the book carried the paper at - face less the discount not yet earned. |

### THE CITY'S PAPER CHANGES HANDS (0.7.1) (lines 1263-1397)

| line | len | member | says |
|---:|---:|---|---|
| 1286 | 1 | `public double getPaperSoldToCentralBank()` |  |
| 1287 | 1 | `public double getPaperBoughtFromCentralBank()` |  |
| 1300 | 1 | `public void setUnearnedDiscount(double amount)` |  |
| 1301 | 1 | `public double getUnearnedDiscount()` |  |
| 1302 | 1 | `public double getPaperGains()` |  |
| 1303 | 1 | `public double getPaperBoughtFromHouseholds()` |  |
| 1305 | 6 | `private void addToCityBook(double face)` |  |
| 1313 | 10 | `public double buyPaperFromHouseholds(double price, double face, double unearned)` | A household sells this face to the desk for this price. |
| 1325 | 11 | `public double sellPaperToCentralBank(double price, double face, double unearned)` | The central bank buys this face from the bank's book, in money it made. |
| 1338 | 10 | `public double buyPaperFromCentralBank(double price, double face, double unearned)` | ...and sells it back: the bank pays, and the face returns to its book. |
| 1351 | 1 | `public double getBuybackGains()` |  |
| 1354 | 4 | `public void takeRepayment(double amount)` | Principal coming back. |
| 1360 | 4 | `public void lend(double amount)` | Money out the door. |
| 1366 | 5 | `public void lendToHouseholds(double amount)` | Lending to a family, which crosses the audit's boundary and is counted. |
| 1373 | 4 | `public void takeFromHouseholds(double principal, double interest)` | ...and from a household, which is outside, so both halves are declared. |
| 1386 | 4 | `public void writeOff(double amount)` | A loan that will not be repaid. |
| 1392 | 5 | `public void payRunning(double payroll, double upkeep)` | Wages and running costs, which are real money leaving. |

### THE FUNDING SIDE (lines 1398-1722)

| line | len | member | says |
|---:|---:|---|---|
| 1511 | 1 | `public double depositRate()` | The rate savers are being paid, a year: the month's payout over the deposits, reported no higher than the lending rate since 0.7.3 - in a month isDepositRateCapped() the payout is more than this. |
| 1514 | 1 | `public boolean isDepositRateCapped()` | True when this month's payout over the deposits came to more than the bank charges, and the rate reported is its lending rate instead (0.7.3) - see fundToCover(). |
| 1518 | 1 | `public double getDepositInterestToHouseholds()` |  |
| 1519 | 1 | `public double getDepositInterestToSectors()` |  |
| 1520 | 1 | `public double getDepositInterestToForeign()` |  |
| 1521 | 4 | `public double depositInterest()` |  |
| 1527 | 4 | `public double netInterestMargin()` | What it charges borrowers, less what it pays savers. |
| 1546 | 1 | `public double borrowings()` | Everything it owes: the mirror of a negative cash position. |
| 1556 | 1 | `public double depositFunding()` | The cheap tranche - the city's own money, lent back out. |
| 1559 | 1 | `public double wholesaleFunding()` | ...and the part it had to go to the window for. |
| 1566 | 1 | `public double fundingRate()` | What the bank is paying for the money it did not have: the window's rate, policy plus CentralBank.WINDOW_PENALTY. |
| 1579 | 143 | `public void fundToCover(double policyAnnual)` | Settles the funding for the month: charge for what was borrowed, then borrow what is short or repay what is spare. |

### WHAT TO PAY SAVERS: A DECISION, NOT A CONSTANT. (lines 1723-1882)

| line | len | member | says |
|---:|---:|---|---|
| 1783 | 3 | **type** `public interface DepositMarket` | What the world would park here at a given deposit rate. |
| 1784 | 1 | `double arrivalsAt(double depositRate)` _(in Bank.DepositMarket)_ |  |
| 1787 | 1 | `public void setDepositMarket(DepositMarket market)` |  |
| 1794 | 68 | `private double chooseDepositRate(double riskFreeAnnual)` | The month's deposit interest, in money. |
| 1875 | 1 | `public int getMonthsBidUp()` |  |
| 1876 | 1 | `public double getLastBidUpGain()` |  |
| 1879 | 1 | `public double getFundingCost()` | What the window charged this month, paid to the central bank. |
| 1881 | 1 | `public double getPlacementIncome()` | What its reserves earned at the central bank this month, at the policy rate. |

### THE THREE STATEMENTS (lines 1883-1977)

| line | len | member | says |
|---:|---:|---|---|
| 1906 | 1 | `public double cashReserves()` | Cash it is actually sitting on. |
| 1909 | 1 | `public double totalAssets()` | Total assets: the loan book, whatever cash it has not lent, and what the desk holds. |
| 1912 | 1 | `public double shortSecurities()` | ...and a desk that is short owes the shares: a liability at the mark. |
| 1939 | 3 | `public double totalLiabilities()` | What the bank owes: what it borrowed to fund its book (past its deposits, at the central bank's window since 0.7.0), and the hot money. |
| 1951 | 1 | `public double equity()` | The residual - and, once the two above are written out, simply the book plus the cash position. |
| 1954 | 1 | `public double getOpeningEquity()` | Equity as it stood at the top of the month. |
| 1959 | 1 | `public double interestIncome()` | What every borrower paid it this month. |
| 1962 | 3 | `public double netInterestIncome()` | ...less what it paid savers and what it paid the window. |
| 1967 | 1 | `public double afterLosses()` | ...less the loans that will not come back. |
| 1970 | 1 | `public double operatingExpenses()` | ...less the tellers and the lights. |
| 1973 | 1 | `public double afterTrading()` | ...plus what the desk made or lost. |
| 1976 | 1 | `public double profitBeforeTax()` | What it made before the city took its share. |

### tax (lines 1978-2047)

| line | len | member | says |
|---:|---:|---|---|
| 2019 | 1 | `public double getProfitLastMonth()` | Profit the tax is charged on: the month that has just finished. |
| 2022 | 25 | `public void closeMonth()` | Called at the end of the month, once the profit is final. |

### LAST MONTH, KEPT ON PURPOSE (lines 2048-2141)

| line | len | member | says |
|---:|---:|---|---|
| 2069 | 4 | `public double[] lastMonthToSave()` |  |
| 2096 | 10 | `public void restoreLastMonth(double[] state)` | ...AND THE TWO PRICES THE MONTH CLOSED AT, added 2026-09-13 with the cost-of-funds floor. |
| 2108 | 1 | `public void setProfitLastMonth(double value)` | Restored from the save, so next month taxes the right figure. |
| 2120 | 1 | `public void setDepositRate(double rate)` | What savers were paid in the month this save was taken in. |
| 2123 | 1 | `public double getTaxPaid()` | What the city took this month. |
| 2136 | 5 | `public double chargeTax(double annualProfitRate)` | Hands the city its share of last month's profit. |

### reading (lines 2142-2441)

| line | len | member | says |
|---:|---:|---|---|
| 2144 | 1 | `public double getCash()` |  |
| 2145 | 1 | `public double getDeposits()` |  |
| 2146 | 1 | `public double getBranches()` |  |
| 2147 | 1 | `public double getSectorBook()` |  |
| 2148 | 1 | `public double getCityBook()` |  |
| 2149 | 1 | `public double getHouseholdBook()` |  |
| 2150 | 1 | `public double getInterestEarned()` |  |
| 2151 | 1 | `public double getWriteOffs()` |  |
| 2152 | 1 | `public double getPayroll()` |  |
| 2153 | 1 | `public double getUpkeep()` |  |
| 2154 | 1 | `public double getLentToHouseholds()` |  |
| 2155 | 1 | `public double getRepaidByHouseholds()` |  |
| 2165 | 3 | `public double getNetIncome()` | The month's profit: interest earned, less the cost of the money, less the loans that died, less the cost of running the place. |
| 2170 | 1 | `public boolean isStrained()` | True when the bank is lending past what it holds and charging for it. |
| 2180 | 31 | `public boolean wantsBranch()` | True when the city should be opening another counter. |
| 2240 | 37 | `public boolean branchWouldPayForItself()` | Whether the counter earns more than it costs to keep open. |
| 2293 | 7 | `public double bookAnotherBranchWouldCarry()` | The loan book one more branch would take onto the bank's own account. |
| 2312 | 8 | `public double capacityWith(double branchCount)` | Capacity this bank would have with a given number of branches. |
| 2322 | 3 | `public double headroom()` | How much more it could lend before the premium starts. |
| 2326 | 1 | `public void setCash(double value)` |  |
| 2328 | 25 | `public void reset()` |  |
| 2373 | 59 | `public void redenominate(double scale)` | Every figure on the bank's balance sheet, in the new unit. |
| 2435 | 5 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |

