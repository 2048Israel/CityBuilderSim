# CentralBank.java - 1,306 lines · 121 methods · 11 constants · model

`ham/citybuildersim/CentralBank.java` - generated 2026-11-26 by CodeMap; line numbers are as of that run.

> The city's central bank: the balance sheet its money is made on, and the one
> place money is made or destroyed.
> 
> ==================== WHY THIS EXISTS (0.7.0) ====================
> 
> The game had a central bank before this, and it was the rest of the world.
> The commercial bank placed its spare cash ABROAD at the world's 2%
> (Bank.PLACEMENT_RATE) and funded its shortfalls ABROAD at the city's own
> paper rate plus two points plus a stretch (FUNDING_SPREAD,
> FUNDING_STRETCH), so the marginal price of money in the city was not the
> player's: the policy dial
> only floored the lending rate on top, which is why it stopped mattering past
> 17%. And the treasury's overdraft was an emergency note the commercial bank
> bought - free until 0.6.11, and at 27-36% once the bank really paid for it,
> which compounded a broke seed to $193 quadrillion (the-bank-that-never-paid.md
> section 3). A free number had been holding a floor.
> 
> Jerus: "the feds sheet would show how much debt it holds, like debt to
> itself aka money printing."
> 
> So the marginal role comes home. The commercial bank's spare cash sits here
> earning the policy rate; its shortfalls are borrowed from the WINDOW at the
> policy rate plus WINDOW_PENALTY; the treasury's overdraft is ADVANCED from
> here at the policy rate, up to a ceiling in months of its revenue (the
> player's dial since 0.7.2, DEFAULT_ADVANCES_MONTHS until it is moved); and the
> profit on all of it goes back to the treasury as the remittance. Nothing
> lends below what reserves earn, and nothing borrows above what the window
> charges, so the dial is the price of money.
> 
> ==================== WHAT IT IS IN THE AUDIT ====================
> 
> A BOUNDARY PARTICIPANT, like the world and the households - not a pool.
> Every operation below moves money across the edge of MoneyAudit's pools and
> counts itself as ISSUED (central bank to a pool: an advance, interest on
> reserves, the remittance) or RETIRED (a pool to the central bank: a
> repayment, the window's interest, the advances' interest). The audit
> declares both under Scope.MONEY, and the month's issued less retired is the
> change in M0 to the cent (CentralBankCheck). The commercial bank's end of
> each flow moves in Bank.fundToCover() and its pool; the treasury's in
> Game.settleTreasury(). Each method here books both sides of THIS balance
> sheet and returns what the caller moves, so the two ends are one figure.
> 
> ==================== THE TWO SIDES ====================
> 
> ASSETS: advances to the commercial bank (the window), advances to the
> treasury (the overdraft - what "printed" means on the screens), the city's
> paper it holds (its open-market book since 0.7.1, at face - see THE
> HOLDINGS DIAL), and the vault. The vault is READ from
> ForeignAccounts, not moved: foreign reserves are a central bank's asset and
> the page lists them as this one's, but the treasury still buys and sells
> them and the field stays where the currency's code can see it.
> 
> LIABILITIES: reserves and currency, and together they are M0. Currency is
> zero - nobody in this city holds cash outside the bank - and kept so that
> the day somebody does, it has a line.
> 
> RESERVES ARE A LEDGER OF WHAT THIS BANK HAS MADE, not a reading of the
> commercial bank's till, and that is the one place this balance sheet reads
> differently from a textbook's. In a city with one bank and no cash in hand
> every dollar a central bank creates ends up in that bank's settlement
> ... (29 more lines in the source)

**Uses:** [DecisionLog](DecisionLog.md) (7)

**Used by (32):** [AutoBuildCheck](AutoBuildCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CentralBankCheck](CentralBankCheck.md), [CentralBankStatementCheck](CentralBankStatementCheck.md), [CentralBankStatements](CentralBankStatements.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignCheck](ForeignCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GoldMiningCheck](GoldMiningCheck.md), [GoldStandardCheck](GoldStandardCheck.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [MoneyDatesCheck](MoneyDatesCheck.md), [MortgageCheck](MortgageCheck.md), [PolicyOpenCheck](PolicyOpenCheck.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [ScaleCheck](ScaleCheck.md)

## Sections

| line | section |
|---:|---|
| 106 | · the dials |
| 132 | · the balance sheet |
| 206 | · the month |
| 234 | · the city's life |
| 291 | THE WINDOW |
| 348 | THE TREASURY |
| 432 | THE HOLDINGS DIAL (0.7.1) |
| 639 | THE DEFENCE (0.7.2) |
| 810 | THE CEILING |
| 858 | READING |
| 937 | ITS OWN STATEMENTS (CBK1, 2026-11-26) |
| 1135 | THE SAVE |

## Enum constants

| line | constant | says |
|---:|---|---|
| 966 | `CentralBank.Line.WINDOW_INTEREST` |  |
| 966 | `CentralBank.Line.ADVANCES_INTEREST` |  |
| 966 | `CentralBank.Line.PAPER_COUPONS` |  |
| 966 | `CentralBank.Line.PAPER_GAINS` |  |
| 966 | `CentralBank.Line.RESERVES_INTEREST` |  |
| 967 | `CentralBank.Line.PROFIT` |  |
| 967 | `CentralBank.Line.LOSS_RECOVERED` |  |
| 967 | `CentralBank.Line.TO_REMIT` |  |
| 967 | `CentralBank.Line.LOSS_ADDED` |  |
| 967 | `CentralBank.Line.REMITTED` |  |
| 968 | `CentralBank.Line.ADVANCED_TO_BANK` |  |
| 968 | `CentralBank.Line.ADVANCED_TO_TREASURY` |  |
| 968 | `CentralBank.Line.BOUGHT_PAPER` |  |
| 968 | `CentralBank.Line.BOUGHT_FROM_BANK` |  |
| 968 | `CentralBank.Line.BOUGHT_FROM_HOUSEHOLDS` |  |
| 968 | `CentralBank.Line.BOUGHT_FROM_WORLD` |  |
| 969 | `CentralBank.Line.BOUGHT_AT_ISSUE` |  |
| 969 | `CentralBank.Line.PAR_AT_ISSUE` |  |
| 969 | `CentralBank.Line.DOLLARS_BOUGHT` |  |
| 969 | `CentralBank.Line.GOLD_MINTED` |  |
| 970 | `CentralBank.Line.REPAID_BY_BANK` |  |
| 970 | `CentralBank.Line.REPAID_BY_TREASURY` |  |
| 970 | `CentralBank.Line.SOLD_PAPER` |  |
| 970 | `CentralBank.Line.PAPER_REDEEMED` |  |
| 970 | `CentralBank.Line.BOUGHT_BACK` |  |
| 970 | `CentralBank.Line.GOLD_REDEEMED` |  |
| 971 | `CentralBank.Line.DEFENDED` |  |
| 971 | `CentralBank.Line.ISSUED` |  |
| 971 | `CentralBank.Line.RETIRED` |  |
| 971 | `CentralBank.Line.M0_AT_START` |  |
| 976 | `CentralBank.Sheet.WINDOW` |  |
| 976 | `CentralBank.Sheet.TREASURY` |  |
| 976 | `CentralBank.Sheet.PAPER` |  |
| 976 | `CentralBank.Sheet.REDEMPTION_DUE` |  |
| 976 | `CentralBank.Sheet.VAULT` |  |
| 976 | `CentralBank.Sheet.ASSETS` |  |
| 976 | `CentralBank.Sheet.RESERVES` |  |
| 976 | `CentralBank.Sheet.CURRENCY` |  |
| 976 | `CentralBank.Sheet.LIABILITIES` |  |
| 976 | `CentralBank.Sheet.EQUITY` |  |
| 977 | `CentralBank.Sheet.LOSS_CARRIED` |  |
| 977 | `CentralBank.Sheet.REMITTANCE_DUE` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 109 | `CentralBank.WINDOW_PENALTY` | `.0025` | What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so borrowing reserves alw... |
| 112 | `CentralBank.DEFAULT_ADVANCES_MONTHS` | `6` | The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past it the arrears rule ... |
| 115 | `CentralBank.MAX_ADVANCES_CEILING` | `36` | The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. |
| 118 | `CentralBank.REVENUE_MONTHS` | `12` | How many months of the treasury's revenue the ceiling is averaged over. |
| 121 | `CentralBank.MAX_QE_SHARE` | `1.0` | The most of the city's term paper the holdings dial may aim at: all of it since 0.7.15, half before - Jerus: "central bank bond holding can ve 100% if one wants", a backstop for when the bank and investors will not ho... |
| 124 | `CentralBank.FULL_COMPRESSION_SHARE` | `.5` | The holding at which the term premium is wholly compressed: half the term paper, where it has been since 0.7.1 - MAX_QE_SHARE's value until the dial went past it (DebtManager.compression()). |
| 127 | `CentralBank.QE_SPEED` | `.25` | The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying toward a new target or se... |
| 130 | `CentralBank.QE_COMPRESSION` | `1.0` | How much of the term premium a holding of FULL_COMPRESSION_SHARE takes away: all of it, at 1. |
| 980 | `CentralBank.LINES` | `Line.values()` |  |
| 981 | `CentralBank.SHEETS` | `Sheet.values()` |  |
| 984 | `CentralBank.SHEET_MONTHS` | `12` | How many months of its sheet it keeps, for the balance sheet's year-ago column: a year, the commercial bank's. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 97 | `private transient DecisionLog decisions` | Where the player's change to its holdings dial or its advances ceiling is written (DecisionLog, 0.7.23); null for one no city holds. |
| 135 | `private double advancesToBank` | Advances to the commercial bank at the window: what its funding past its deposits owes here. |
| 138 | `private double advancesToTreasury` | Advances to the treasury: its overdraft, funded by money this bank made. |
| 146 | `private double paperHeld` | The city's paper this bank holds, AT FACE - principal, the one convention for every holder; the premium or discount it paid to face is its gain or loss the month it pays it (see THE HOLDINGS DIAL). |
| 155 | `private double targetShare` | The holdings dial (0.7.1): the share of the city's outstanding domestic TERM paper - serials and term loans, not notes - this bank aims to hold, 0 to MAX_QE_SHARE (a half until 0.7.15, all of it since). |
| 163 | `private double previousTarget` | ...and the setting before it: the pace is struck from the larger of the two, so selling a book down to a dial of nothing goes at the pace it was bought at rather than slowing for ever as it shrinks. |
| 172 | `private double redemptionDue, redemptionGainDue` | What the treasury paid for this bank's share of a bond it bought back between two presses, and the gain or loss on it against face, both settled at the top of the next month - the money retired there, inside the audit... |
| 175 | `private double reserves` | What this bank owes the banking system: every dollar it has made and not taken back. |
| 178 | `private double currency` | Cash in circulation outside the bank. |
| 189 | `private double lossCarried` | A LOSS IS NOT REMITTED. |
| 192 | `private double remittanceDue` | Last month's profit, owed to the treasury and paid at the top of this one. |
| 204 | `private double advancesCeilingMonths` | THE CEILING AS A DIAL (0.7.2): the most the treasury may owe here, in months of its trailing revenue, 0 to MAX_ADVANCES_CEILING. |
| 208 | `private double issued, retired` |  |
| 209 | `private double advancedToBank, repaidByBank` |  |
| 210 | `private double advancedToTreasury, repaidByTreasury` |  |
| 211 | `private double interestOnReserves, windowInterest, advancesInterest` |  |
| 212 | `private double remitted` |  |
| 214 | `private double boughtPaper, soldPaper, paperCoupons, paperRedeemed, boughtBack, paperGains` | The holdings' month (0.7.1): paid for paper and received for it, the coupons and principal on it, what a buyback redeemed, and the gains and losses against face. |
| 216 | `private double boughtFromHouseholds` | ...and of what it paid for paper, what it paid the households for theirs (0.7.15): money made and paid straight into their savings. |
| 218 | `private double boughtFromWorld` | ...and what it paid the world for the city's paper it holds (CD2, slice 5): money made and paid abroad. |
| 220 | `private double boughtAtIssue, parAtIssue` | ...and what it paid the treasury at issue, and the par it took, rolling its own maturing paper (0.7.15, round 2): money made into the treasury's cash. |
| 222 | `private double defendedThisMonth` | The defence's month (0.7.2): the local price of the vault's dollars sold at this month's reprice - equity spent, not money destroyed. |
| 224 | `private double dollarsBought` | The mirror of the defence (XR2): the local price of the dollars it bought for the vault this month at the strong edge of a band or a peg, in money made and paid abroad for them. |
| 226 | `private double goldRedeemed` | The gold's own defence (FB14): the local price of the gold the mint paid out at the last reprice under Gold, retired here this month - M0 falls with the gold (THE GOLD'S OWN DEFENCE). |
| 229 | `private double goldMinted` | New D$ paid for domestically mined gold this month (GD4). |
| 232 | `private double goldMintedLifetime` | New D$ paid for domestically mined gold since founding (GD4). |
| 237 | `private double printedLifetime` | Advanced to the treasury since founding - "printed since founding". |
| 238 | `private double issuedLifetime, retiredLifetime, remittedLifetime` |  |
| 240 | `private double boughtPaperLifetime, soldPaperLifetime` | Paid for the city's paper and received for it since founding (0.7.1). |
| 242 | `private double boughtFromHouseholdsLifetime` | ...and of what it paid, what it paid the households since founding (0.7.15). |
| 244 | `private double boughtAtIssueLifetime, parAtIssueLifetime` | ...and what it paid at issue for its own maturing paper rolled, and the par, since founding (0.7.15, round 2). |
| 246 | `private double defendedLifetime` | Spent defending the currency since founding (0.7.2): the vault's dollars at the rates they were sold at. |
| 248 | `private double dollarsBoughtLifetime` | ...and what it paid, in money made, for the dollars it bought for the vault since founding (XR2). |
| 250 | `private double goldRedeemedLifetime` | ...and the gold the mint paid out under Gold since founding, the D$ it took back retired (FB14). |
| 252 | `private double goldRedeemDue` | The local price of the gold the mint paid out at the last reprice, owed here and retired at the top of the next month (FB14; settleGoldRedeemed()). |
| 255 | `private final double[] revenue` | The treasury's revenue, the last REVENUE_MONTHS of it, for the ceiling. |
| 256 | `private int revenueFilled, revenueNext` |  |
| 259 | `private final java.util.function.DoubleSupplier vault` | Where the vault is read from. |
| 987 | `private double struckProfit, lossRecovered, toRemit, lossAdded` | What closeMonth() struck this month: the profit, and of it what made good a loss carried and what is owed the treasury; or the loss it carried. |
| 989 | `private double m0AtStart` | M0 as this month opened, before anything moved (fileMonth()). |
| 991 | `private final double[] lastLines` | Last month's lines, in Line's order, and how many months have been filed since founding (or since an older save was opened). |
| 992 | `private int monthsFiled` |  |
| 994 | `private final double[][] sheetRing` | The sheet at the end of each of the last SHEET_MONTHS months, a ring, and how many have been filed. |
| 995 | `private int sheetsFiled` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 94 | 1213 | **type** `public final class CentralBank` | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. |
| 100 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 102 | 3 | `private void decided(String kind, String label)` |  |

### the dials (lines 106-131)

### the balance sheet (lines 132-205)

### the month (lines 206-233)

### the city's life (lines 234-290)

| line | len | member | says |
|---:|---:|---|---|
| 261 | 1 | `public CentralBank()` |  |
| 263 | 3 | `public CentralBank(java.util.function.DoubleSupplier vault)` |  |
| 271 | 16 | `public void startMonth()` | Clears the month's flows. |
| 288 | 1 | `private void issue(double x)` |  |
| 289 | 1 | `private void retire(double x)` |  |

### THE WINDOW (lines 291-347)

| line | len | member | says |
|---:|---:|---|---|
| 302 | 7 | `public double advanceToBank(double x)` | Money made and lent to the commercial bank at the window. |
| 311 | 8 | `public double repayFromBank(double x)` | ...and repaid, which destroys it. |
| 326 | 5 | `public void settleWindow(double owed)` | Brings the window to what the bank owes past its deposits, once a month at the settle: advances the difference or takes the repayment. |
| 333 | 6 | `public double payInterestOnReserves(double x)` | Interest on reserves: the policy rate on the bank's spare cash, paid in money made for it. |
| 341 | 6 | `public double chargeWindow(double x)` | The window's interest, from the commercial bank: income here, and money destroyed. |

### THE TREASURY (lines 348-431)

| line | len | member | says |
|---:|---:|---|---|
| 351 | 8 | `public double advanceToTreasury(double x)` | The treasury's shortfall, advanced in money made for it. |
| 361 | 8 | `public double repayFromTreasury(double x)` | ...and repaid out of cash above zero, which destroys it. |
| 371 | 3 | `public double advancesInterestDue(double policyAnnual)` | What a month on the advances outstanding costs at this policy rate. |
| 376 | 6 | `public double chargeAdvances(double x)` | The treasury's interest on its advances: income here, and money destroyed. |
| 390 | 9 | `public double remit()` | The profit, to the treasury: last month's, struck at closeMonth() and paid at the top of this one - a revenue line, "Central bank remittance". |
| 411 | 15 | `public void closeMonth()` | Strikes the month's profit and decides what is owed to the treasury. |
| 428 | 3 | `public double monthProfit()` | This month's income less what reserves cost, so far. |

### THE HOLDINGS DIAL (0.7.1) (lines 432-638)

| line | len | member | says |
|---:|---:|---|---|
| 488 | 9 | `public double buyPaper(double price, double face)` | Buys this much face for this price, in money made for it. |
| 507 | 6 | `public double buyPaperFromHouseholds(double price, double face)` | Buys this much face from the households for this price, in money made for them (0.7.15): the paper the bank could not sell it. |
| 522 | 5 | `public double buyPaperFromWorld(double price, double face)` | Buys this much face from the world for this price (CD2, slice 5), in money made for it, the last the dial reaches: the same purchase as buyPaper(), counted apart because the money made leaves the city for the world - ... |
| 538 | 10 | `public double buyAtIssue(double price, double face)` | Its add-on at issue (0.7.15, round 2): this much face of the city's new paper for this price - the issue's own price on each unit of face - paid to the treasury in money made for it. |
| 550 | 9 | `public double sellPaper(double price, double face)` | ...and sells it back, which destroys the price. |
| 561 | 6 | `public double takeCoupon(double x)` | The coupon on its share, from the treasury: income here, and money destroyed. |
| 569 | 7 | `public double takePrincipal(double x)` | Principal repaid on its share: off the book at face, and destroyed. |
| 583 | 5 | `public void paperBoughtBack(double price, double face)` | The treasury buys back a bond this bank holds part of, between two presses: the face comes off the book now, and the price and the gain against face wait for the top of the next month (settleRedemptions()), where the ... |
| 590 | 10 | `public double settleRedemptions()` | ...and settled: the price destroyed, the gain into this month's profit. |
| 602 | 1 | `public double getRedemptionDue()` | What the treasury has paid for this bank's share of a buyback and it has not yet settled: carried in the treasury's pool until it has. |
| 604 | 1 | `public double getTargetShare()` |  |
| 605 | 1 | `public double getPreviousTarget()` |  |
| 608 | 8 | `public void setTargetShare(double share)` | The dial, held to 0..MAX_QE_SHARE. |
| 624 | 3 | `public void restoreTargetShare(double share)` | The dial as a save left it, WITHOUT moving the memory of where it was: the load path's setter. |
| 633 | 5 | `public double stepFor(double termPrincipal)` | The most the book may move this month, at face, against this much term paper outstanding: QE_SPEED of the larger of the dial, the setting before it and the share it actually holds. |

### THE DEFENCE (0.7.2) (lines 639-809)

| line | len | member | says |
|---:|---:|---|---|
| 697 | 5 | `public void dollarsSold(double local)` | The vault's dollars were sold at the reprice for this much local money. |
| 704 | 1 | `public double getDefendedThisMonth()` | Spent defending the currency at this month's reprice, at the rate the dollars were sold at. |
| 706 | 1 | `public double getDefendedLifetime()` | ...and since founding. |
| 713 | 1 | `public double vaultSpent()` | Spent defending the currency since founding: the local price of every dollar the defence has sold. |
| 723 | 7 | `public double buyDollarsForVault(double local)` | Pays this much local money, made for the purpose, for the dollars the vault bought at the strong edge (the mirror of the defence; Game .settleTheEdge()). |
| 754 | 4 | `public void goldPaidOut(double local)` | The gold the mint paid out at this month's reprice under Gold fetched this much local money: owed here, and retired at the top of the next month. |
| 763 | 13 | `public double settleGoldRedeemed()` | Retires what the gold paid out at the last reprice fetched (Game, at the top of the month, beside settleTheEdge()): M0 down by it, up to what this bank has made, and the rest booked as a defence's (dollarsSold()). |
| 778 | 1 | `public double getGoldRedeemed()` | What the gold the mint paid out retired this month, M0 down by it (FB14). |
| 780 | 1 | `public double getGoldRedeemedLifetime()` | ...and since founding. |
| 782 | 1 | `public double getGoldRedeemDue()` | ...and what the last reprice's gold fetched, still to be retired at the top of the next month. |
| 789 | 1 | `public double vaultInNewMoney()` | What the vault holds in money this bank made, net: the dollars it bought at the strong edge in new money less what the gold it paid out retired (FB14). |
| 792 | 6 | `public void mintGold(double local)` | Issues the mint's payment to a domestic miner, thousands of D$ (GD4). |
| 800 | 1 | `public double getGoldMinted()` | The month's domestic mint payment, kept on save for the audit and the books. |
| 803 | 1 | `public double getGoldMintedLifetime()` | The domestic mint's new money since founding. |
| 806 | 1 | `public double getDollarsBought()` | What it paid, in new money, for the dollars it bought for the vault this month. |
| 808 | 1 | `public double getDollarsBoughtLifetime()` | ...and since founding. |

### THE CEILING (lines 810-857)

| line | len | member | says |
|---:|---:|---|---|
| 813 | 6 | `public void noteRevenue(double monthRevenue)` | Files a month of the treasury's revenue, for the ceiling. |
| 821 | 6 | `public double trailingRevenue()` | The treasury's revenue a month, averaged over what has been filed. |
| 829 | 1 | `public double ceiling()` | The most the treasury may owe here before the arrears rule decides who is paid: the dial's months of its trailing revenue. |
| 832 | 4 | `public double ceilingAt(double months)` | ...at a ceiling of the caller's, in months (0.7.36): what the dial would allow at that setting, held to its range - the Policy tab's ceiling preview. |
| 838 | 1 | `public double getAdvancesCeilingMonths()` | The ceiling dial, in months of revenue. |
| 841 | 10 | `public void setAdvancesCeilingMonths(double months)` | Sets the ceiling dial, held to 0..MAX_ADVANCES_CEILING; a number that is not one reads the default. |
| 853 | 1 | `public double headroom()` | What the treasury may still draw for anything that is not a promise. |
| 856 | 1 | `public boolean ceilingBound()` | True once the treasury owes the ceiling or more. |

### READING (lines 858-936)

| line | len | member | says |
|---:|---:|---|---|
| 860 | 1 | `public double getAdvancesToBank()` |  |
| 861 | 1 | `public double getAdvancesToTreasury()` |  |
| 862 | 1 | `public double getPaperHeld()` |  |
| 864 | 1 | `public double getVault()` | The vault, at today's rate, read from ForeignAccounts. |
| 865 | 1 | `public double getReserves()` |  |
| 866 | 1 | `public double getCurrency()` |  |
| 868 | 3 | `public double totalAssets()` |  |
| 873 | 1 | `public double m0()` | M0: everything this bank owes, which is every dollar it has made and not taken back. |
| 880 | 1 | `public double m0AfterGoldDue()` | M0 as the gold's cover reads it (FB14; REV-FB14): m0() less what the gold paid out at the last reprice will retire at the top of the next month (settleGoldRedeemed(), up to the reserves), so the cover shown between th... |
| 883 | 1 | `public double goldRetirable()` | ...and what a further sale of gold could still retire, M0 down by it: the reserves past the due, never under nothing (settleGoldRedeemed()'s cap). |
| 885 | 1 | `public double totalLiabilities()` |  |
| 887 | 1 | `public double equity()` |  |
| 889 | 1 | `public double getLossCarried()` |  |
| 890 | 1 | `public double getRemittanceDue()` |  |
| 892 | 1 | `public double getIssued()` |  |
| 893 | 1 | `public double getRetired()` |  |
| 896 | 1 | `public double getM0Moved()` | What M0 moved by this month: the money made less the money destroyed (0.7.32, the Finances tab's THIS MONTH, which subtracted them itself). |
| 897 | 1 | `public double getAdvancedToBank()` |  |
| 898 | 1 | `public double getRepaidByBank()` |  |
| 900 | 1 | `public double getAdvancedToTreasury()` | Printed this month: what was advanced to the treasury. |
| 901 | 1 | `public double getRepaidByTreasury()` |  |
| 902 | 1 | `public double getInterestOnReserves()` |  |
| 903 | 1 | `public double getWindowInterest()` |  |
| 904 | 1 | `public double getAdvancesInterest()` |  |
| 906 | 1 | `public double getRemitted()` | The remittance paid to the treasury this month. |
| 908 | 1 | `public double getBoughtPaper()` | The holdings' month (0.7.1). |
| 909 | 1 | `public double getSoldPaper()` |  |
| 910 | 1 | `public double getPaperCoupons()` |  |
| 911 | 1 | `public double getPaperRedeemed()` |  |
| 913 | 1 | `public double getBoughtBack()` | What a buyback redeemed of its paper, settled this month. |
| 914 | 1 | `public double getPaperGains()` |  |
| 915 | 1 | `public double getBoughtPaperLifetime()` |  |
| 916 | 1 | `public double getSoldPaperLifetime()` |  |
| 918 | 1 | `public double getBoughtFromHouseholds()` | Of what it paid for paper this month, what it paid the households (0.7.15); the treasury at issue, getBoughtAtIssue() (round 2); the rest it paid the bank. |
| 920 | 1 | `public double getBoughtFromWorld()` | ...and the world (CD2, slice 5). |
| 922 | 1 | `public double getBoughtFromHouseholdsLifetime()` | ...and since founding. |
| 924 | 1 | `public double getBoughtAtIssue()` | What it paid the treasury this month for its add-ons at issue, rolling its own maturing paper (0.7.15, round 2). |
| 926 | 1 | `public double getParAtIssue()` | ...their par: what of its maturing paper it rolled this month. |
| 928 | 1 | `public double getBoughtAtIssueLifetime()` | ...and since founding, what it paid. |
| 930 | 1 | `public double getParAtIssueLifetime()` | ...and the par. |
| 932 | 1 | `public double getPrintedLifetime()` |  |
| 933 | 1 | `public double getIssuedLifetime()` |  |
| 934 | 1 | `public double getRetiredLifetime()` |  |
| 935 | 1 | `public double getRemittedLifetime()` |  |

### ITS OWN STATEMENTS (CBK1, 2026-11-26) (lines 937-1134)

| line | len | member | says |
|---:|---:|---|---|
| 965 | 8 | **type** `public enum Line` | A line of its month, as its own pages read it: what this month booked, and last month's beside it (lastMonth()). |
| 975 | 4 | **type** `public enum Sheet` | ...and a line of its balance sheet, as they read it: this month's and a year ago's (yearAgo()). |
| 998 | 8 | `private void fileMonth()` | Last month's lines and its sheet filed, and M0 noted as this month opens: startMonth()'s first step. |
| 1008 | 5 | `private void forgetFiled()` | Nothing filed: a bank whose last month and year ago are not known (reset()). |
| 1015 | 34 | `public double thisMonth(Line line)` | One line of this month, as booked so far. |
| 1051 | 34 | `private void restoreLine(Line line, double v)` | ...the save's way back in, for every line but the one worked out from the others (BOUGHT_FROM_BANK). |
| 1090 | 1 | `public boolean knowsLastMonth()` | Whether last month is known: two months filed, since the first filing is of a month nobody played here - the one before a city's first, or the month an older save was opened in, whose lines that save did not keep. |
| 1093 | 1 | `public double lastMonth(Line line)` | One line of last month; 0 until it is known. |
| 1096 | 1 | `public double getBoughtFromBank()` | What it paid the commercial bank for paper this month: the rest of getBoughtPaper() once the households, the world and the treasury at issue are out. |
| 1099 | 1 | `public double getStruckProfit()` | What closeMonth() struck as this month's profit (a loss negative): monthProfit() at the strike. |
| 1101 | 1 | `public double getLossRecovered()` | ...of a profit, what made good a loss carried from before. |
| 1103 | 1 | `public double getToRemit()` | ...and the rest, owed to the treasury and paid at the top of the next month (remit()). |
| 1105 | 1 | `public double getLossAdded()` | ...or a loss, carried to be made good from future profit and not remitted. |
| 1107 | 1 | `public double getM0AtStart()` | M0 as this month opened, before anything moved: M0 now less it is getM0Moved() in a month with no reform in it. |
| 1110 | 16 | `public double sheet(Sheet line)` | One line of the sheet now. |
| 1128 | 1 | `public boolean knowsYearAgo()` | Whether a year of sheets is filed, so a year ago is known. |
| 1131 | 3 | `public double yearAgo(Sheet line)` | One line of the sheet as it stood a year ago, at the end of the month twelve before this one; 0 until a year is filed. |

### THE SAVE (lines 1135-1306)

| line | len | member | says |
|---:|---:|---|---|
| 1155 | 42 | `public double[] toSaveArray()` | Everything above that is state rather than this month's flow, as one array under its own key (DataSave.centralBank). |
| 1198 | 52 | `public void restore(double[] saved)` |  |
| 1252 | 19 | `public void reset()` | An empty bank: nothing lent, nothing made. |
| 1277 | 29 | `public void redenominate(double scale)` | Every figure on this balance sheet, in the new unit. |

