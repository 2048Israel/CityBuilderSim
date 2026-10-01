# CentralBank.java - 873 lines · 88 methods · 8 constants · model

`ham/citybuildersim/CentralBank.java` - generated 2026-10-01 by CodeMap; line numbers are as of that run.

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
> ... (18 more lines in the source)

**Uses:** [DecisionLog](DecisionLog.md) (7)

**Used by (17):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CentralBankCheck](CentralBankCheck.md), [CurrencyCheck](CurrencyCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 95 | · the dials |
| 121 | · the balance sheet |
| 195 | · the month |
| 211 | · the city's life |
| 254 | THE WINDOW |
| 311 | THE TREASURY |
| 390 | THE HOLDINGS DIAL (0.7.1) |
| 583 | THE DEFENCE (0.7.2) |
| 644 | THE CEILING |
| 686 | READING |
| 750 | THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 98 | `CentralBank.WINDOW_PENALTY` | `.0025` | What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so borrowing reserves alw... |
| 101 | `CentralBank.DEFAULT_ADVANCES_MONTHS` | `6` | The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past it the arrears rule ... |
| 104 | `CentralBank.MAX_ADVANCES_CEILING` | `36` | The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. |
| 107 | `CentralBank.REVENUE_MONTHS` | `12` | How many months of the treasury's revenue the ceiling is averaged over. |
| 110 | `CentralBank.MAX_QE_SHARE` | `1.0` | The most of the city's term paper the holdings dial may aim at: all of it since 0.7.15, half before - Jerus: "central bank bond holding can ve 100% if one wants", a backstop for when the bank and investors will not ho... |
| 113 | `CentralBank.FULL_COMPRESSION_SHARE` | `.5` | The holding at which the term premium is wholly compressed: half the term paper, where it has been since 0.7.1 - MAX_QE_SHARE's value until the dial went past it (DebtManager.compression()). |
| 116 | `CentralBank.QE_SPEED` | `.25` | The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying toward a new target or se... |
| 119 | `CentralBank.QE_COMPRESSION` | `1.0` | How much of the term premium a holding of FULL_COMPRESSION_SHARE takes away: all of it, at 1. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 86 | `private transient DecisionLog decisions` | Where the player's change to its holdings dial or its advances ceiling is written (DecisionLog, 0.7.23); null for one no city holds. |
| 124 | `private double advancesToBank` | Advances to the commercial bank at the window: what its funding past its deposits owes here. |
| 127 | `private double advancesToTreasury` | Advances to the treasury: its overdraft, funded by money this bank made. |
| 135 | `private double paperHeld` | The city's paper this bank holds, AT FACE - principal, the one convention for every holder; the premium or discount it paid to face is its gain or loss the month it pays it (see THE HOLDINGS DIAL). |
| 144 | `private double targetShare` | The holdings dial (0.7.1): the share of the city's outstanding domestic TERM paper - serials and term loans, not notes - this bank aims to hold, 0 to MAX_QE_SHARE (a half until 0.7.15, all of it since). |
| 152 | `private double previousTarget` | ...and the setting before it: the pace is struck from the larger of the two, so selling a book down to a dial of nothing goes at the pace it was bought at rather than slowing for ever as it shrinks. |
| 161 | `private double redemptionDue, redemptionGainDue` | What the treasury paid for this bank's share of a bond it bought back between two presses, and the gain or loss on it against face, both settled at the top of the next month - the money retired there, inside the audit... |
| 164 | `private double reserves` | What this bank owes the banking system: every dollar it has made and not taken back. |
| 167 | `private double currency` | Cash in circulation outside the bank. |
| 178 | `private double lossCarried` | A LOSS IS NOT REMITTED. |
| 181 | `private double remittanceDue` | Last month's profit, owed to the treasury and paid at the top of this one. |
| 193 | `private double advancesCeilingMonths` | THE CEILING AS A DIAL (0.7.2): the most the treasury may owe here, in months of its trailing revenue, 0 to MAX_ADVANCES_CEILING. |
| 197 | `private double issued, retired` |  |
| 198 | `private double advancedToBank, repaidByBank` |  |
| 199 | `private double advancedToTreasury, repaidByTreasury` |  |
| 200 | `private double interestOnReserves, windowInterest, advancesInterest` |  |
| 201 | `private double remitted` |  |
| 203 | `private double boughtPaper, soldPaper, paperCoupons, paperRedeemed, boughtBack, paperGains` | The holdings' month (0.7.1): paid for paper and received for it, the coupons and principal on it, what a buyback redeemed, and the gains and losses against face. |
| 205 | `private double boughtFromHouseholds` | ...and of what it paid for paper, what it paid the households for theirs (0.7.15): money made and paid straight into their savings. |
| 207 | `private double boughtAtIssue, parAtIssue` | ...and what it paid the treasury at issue, and the par it took, rolling its own maturing paper (0.7.15, round 2): money made into the treasury's cash. |
| 209 | `private double defendedThisMonth` | The defence's month (0.7.2): the local price of the vault's dollars sold at this month's reprice - equity spent, not money destroyed. |
| 214 | `private double printedLifetime` | Advanced to the treasury since founding - "printed since founding". |
| 215 | `private double issuedLifetime, retiredLifetime, remittedLifetime` |  |
| 217 | `private double boughtPaperLifetime, soldPaperLifetime` | Paid for the city's paper and received for it since founding (0.7.1). |
| 219 | `private double boughtFromHouseholdsLifetime` | ...and of what it paid, what it paid the households since founding (0.7.15). |
| 221 | `private double boughtAtIssueLifetime, parAtIssueLifetime` | ...and what it paid at issue for its own maturing paper rolled, and the par, since founding (0.7.15, round 2). |
| 223 | `private double defendedLifetime` | Spent defending the currency since founding (0.7.2): the vault's dollars at the rates they were sold at. |
| 226 | `private final double[] revenue` | The treasury's revenue, the last REVENUE_MONTHS of it, for the ceiling. |
| 227 | `private int revenueFilled, revenueNext` |  |
| 230 | `private final java.util.function.DoubleSupplier vault` | Where the vault is read from. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 791 | **type** `public final class CentralBank` | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. |
| 89 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 91 | 3 | `private void decided(String kind, String label)` |  |

### the dials (lines 95-120)

### the balance sheet (lines 121-194)

### the month (lines 195-210)

### the city's life (lines 211-253)

| line | len | member | says |
|---:|---:|---|---|
| 232 | 1 | `public CentralBank()` |  |
| 234 | 3 | `public CentralBank(java.util.function.DoubleSupplier vault)` |  |
| 239 | 11 | `public void startMonth()` | Clears the month's flows. |
| 251 | 1 | `private void issue(double x)` |  |
| 252 | 1 | `private void retire(double x)` |  |

### THE WINDOW (lines 254-310)

| line | len | member | says |
|---:|---:|---|---|
| 265 | 7 | `public double advanceToBank(double x)` | Money made and lent to the commercial bank at the window. |
| 274 | 8 | `public double repayFromBank(double x)` | ...and repaid, which destroys it. |
| 289 | 5 | `public void settleWindow(double owed)` | Brings the window to what the bank owes past its deposits, once a month at the settle: advances the difference or takes the repayment. |
| 296 | 6 | `public double payInterestOnReserves(double x)` | Interest on reserves: the policy rate on the bank's spare cash, paid in money made for it. |
| 304 | 6 | `public double chargeWindow(double x)` | The window's interest, from the commercial bank: income here, and money destroyed. |

### THE TREASURY (lines 311-389)

| line | len | member | says |
|---:|---:|---|---|
| 314 | 8 | `public double advanceToTreasury(double x)` | The treasury's shortfall, advanced in money made for it. |
| 324 | 8 | `public double repayFromTreasury(double x)` | ...and repaid out of cash above zero, which destroys it. |
| 334 | 3 | `public double advancesInterestDue(double policyAnnual)` | What a month on the advances outstanding costs at this policy rate. |
| 339 | 6 | `public double chargeAdvances(double x)` | The treasury's interest on its advances: income here, and money destroyed. |
| 353 | 9 | `public double remit()` | The profit, to the treasury: last month's, struck at closeMonth() and paid at the top of this one - a revenue line, "Central bank remittance". |
| 374 | 10 | `public void closeMonth()` | Strikes the month's profit and decides what is owed to the treasury. |
| 386 | 3 | `public double monthProfit()` | This month's income less what reserves cost, so far. |

### THE HOLDINGS DIAL (0.7.1) (lines 390-582)

| line | len | member | says |
|---:|---:|---|---|
| 446 | 9 | `public double buyPaper(double price, double face)` | Buys this much face for this price, in money made for it. |
| 465 | 6 | `public double buyPaperFromHouseholds(double price, double face)` | Buys this much face from the households for this price, in money made for them (0.7.15): the paper the bank could not sell it. |
| 482 | 10 | `public double buyAtIssue(double price, double face)` | Its add-on at issue (0.7.15, round 2): this much face of the city's new paper for this price - the issue's own price on each unit of face - paid to the treasury in money made for it. |
| 494 | 9 | `public double sellPaper(double price, double face)` | ...and sells it back, which destroys the price. |
| 505 | 6 | `public double takeCoupon(double x)` | The coupon on its share, from the treasury: income here, and money destroyed. |
| 513 | 7 | `public double takePrincipal(double x)` | Principal repaid on its share: off the book at face, and destroyed. |
| 527 | 5 | `public void paperBoughtBack(double price, double face)` | The treasury buys back a bond this bank holds part of, between two presses: the face comes off the book now, and the price and the gain against face wait for the top of the next month (settleRedemptions()), where the ... |
| 534 | 10 | `public double settleRedemptions()` | ...and settled: the price destroyed, the gain into this month's profit. |
| 546 | 1 | `public double getRedemptionDue()` | What the treasury has paid for this bank's share of a buyback and it has not yet settled: carried in the treasury's pool until it has. |
| 548 | 1 | `public double getTargetShare()` |  |
| 549 | 1 | `public double getPreviousTarget()` |  |
| 552 | 8 | `public void setTargetShare(double share)` | The dial, held to 0..MAX_QE_SHARE. |
| 568 | 3 | `public void restoreTargetShare(double share)` | The dial as a save left it, WITHOUT moving the memory of where it was: the load path's setter. |
| 577 | 5 | `public double stepFor(double termPrincipal)` | The most the book may move this month, at face, against this much term paper outstanding: QE_SPEED of the larger of the dial, the setting before it and the share it actually holds. |

### THE DEFENCE (0.7.2) (lines 583-643)

| line | len | member | says |
|---:|---:|---|---|
| 626 | 5 | `public void dollarsSold(double local)` | The vault's dollars were sold at the reprice for this much local money. |
| 633 | 1 | `public double getDefendedThisMonth()` | Spent defending the currency at this month's reprice, at the rate the dollars were sold at. |
| 635 | 1 | `public double getDefendedLifetime()` | ...and since founding. |
| 642 | 1 | `public double vaultSpent()` | Spent defending the currency since founding: the local price of every dollar the defence has sold. |

### THE CEILING (lines 644-685)

| line | len | member | says |
|---:|---:|---|---|
| 647 | 6 | `public void noteRevenue(double monthRevenue)` | Files a month of the treasury's revenue, for the ceiling. |
| 655 | 6 | `public double trailingRevenue()` | The treasury's revenue a month, averaged over what has been filed. |
| 663 | 1 | `public double ceiling()` | The most the treasury may owe here before the arrears rule decides who is paid: the dial's months of its trailing revenue. |
| 666 | 1 | `public double getAdvancesCeilingMonths()` | The ceiling dial, in months of revenue. |
| 669 | 10 | `public void setAdvancesCeilingMonths(double months)` | Sets the ceiling dial, held to 0..MAX_ADVANCES_CEILING; a number that is not one reads the default. |
| 681 | 1 | `public double headroom()` | What the treasury may still draw for anything that is not a promise. |
| 684 | 1 | `public boolean ceilingBound()` | True once the treasury owes the ceiling or more. |

### READING (lines 686-749)

| line | len | member | says |
|---:|---:|---|---|
| 688 | 1 | `public double getAdvancesToBank()` |  |
| 689 | 1 | `public double getAdvancesToTreasury()` |  |
| 690 | 1 | `public double getPaperHeld()` |  |
| 692 | 1 | `public double getVault()` | The vault, at today's rate, read from ForeignAccounts. |
| 693 | 1 | `public double getReserves()` |  |
| 694 | 1 | `public double getCurrency()` |  |
| 696 | 3 | `public double totalAssets()` |  |
| 701 | 1 | `public double m0()` | M0: everything this bank owes, which is every dollar it has made and not taken back. |
| 703 | 1 | `public double totalLiabilities()` |  |
| 705 | 1 | `public double equity()` |  |
| 707 | 1 | `public double getLossCarried()` |  |
| 708 | 1 | `public double getRemittanceDue()` |  |
| 710 | 1 | `public double getIssued()` |  |
| 711 | 1 | `public double getRetired()` |  |
| 712 | 1 | `public double getAdvancedToBank()` |  |
| 713 | 1 | `public double getRepaidByBank()` |  |
| 715 | 1 | `public double getAdvancedToTreasury()` | Printed this month: what was advanced to the treasury. |
| 716 | 1 | `public double getRepaidByTreasury()` |  |
| 717 | 1 | `public double getInterestOnReserves()` |  |
| 718 | 1 | `public double getWindowInterest()` |  |
| 719 | 1 | `public double getAdvancesInterest()` |  |
| 721 | 1 | `public double getRemitted()` | The remittance paid to the treasury this month. |
| 723 | 1 | `public double getBoughtPaper()` | The holdings' month (0.7.1). |
| 724 | 1 | `public double getSoldPaper()` |  |
| 725 | 1 | `public double getPaperCoupons()` |  |
| 726 | 1 | `public double getPaperRedeemed()` |  |
| 728 | 1 | `public double getBoughtBack()` | What a buyback redeemed of its paper, settled this month. |
| 729 | 1 | `public double getPaperGains()` |  |
| 730 | 1 | `public double getBoughtPaperLifetime()` |  |
| 731 | 1 | `public double getSoldPaperLifetime()` |  |
| 733 | 1 | `public double getBoughtFromHouseholds()` | Of what it paid for paper this month, what it paid the households (0.7.15); the treasury at issue, getBoughtAtIssue() (round 2); the rest it paid the bank. |
| 735 | 1 | `public double getBoughtFromHouseholdsLifetime()` | ...and since founding. |
| 737 | 1 | `public double getBoughtAtIssue()` | What it paid the treasury this month for its add-ons at issue, rolling its own maturing paper (0.7.15, round 2). |
| 739 | 1 | `public double getParAtIssue()` | ...their par: what of its maturing paper it rolled this month. |
| 741 | 1 | `public double getBoughtAtIssueLifetime()` | ...and since founding, what it paid. |
| 743 | 1 | `public double getParAtIssueLifetime()` | ...and the par. |
| 745 | 1 | `public double getPrintedLifetime()` |  |
| 746 | 1 | `public double getIssuedLifetime()` |  |
| 747 | 1 | `public double getRetiredLifetime()` |  |
| 748 | 1 | `public double getRemittedLifetime()` |  |

### THE SAVE (lines 750-873)

| line | len | member | says |
|---:|---:|---|---|
| 767 | 28 | `public double[] toSaveArray()` | Everything above that is state rather than this month's flow, as one array under its own key (DataSave.centralBank). |
| 796 | 32 | `public void restore(double[] saved)` |  |
| 830 | 16 | `public void reset()` | An empty bank: nothing lent, nothing made. |
| 852 | 21 | `public void redenominate(double scale)` | Every figure on this balance sheet, in the new unit. |

