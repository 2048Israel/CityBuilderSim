# CentralBank.java - 854 lines · 86 methods · 8 constants · model

`ham/citybuildersim/CentralBank.java` - generated 2026-09-30 by CodeMap; line numbers are as of that run.

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

**Used by (17):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CentralBankCheck](CentralBankCheck.md), [CurrencyCheck](CurrencyCheck.md), [DebtManager](DebtManager.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [FundCheck](FundCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MoneyAudit](MoneyAudit.md), [MortgageCheck](MortgageCheck.md), [PolicyScreen](PolicyScreen.md)

## Sections

| line | section |
|---:|---|
| 85 | · the dials |
| 111 | · the balance sheet |
| 185 | · the month |
| 201 | · the city's life |
| 244 | THE WINDOW |
| 301 | THE TREASURY |
| 380 | THE HOLDINGS DIAL (0.7.1) |
| 570 | THE DEFENCE (0.7.2) |
| 631 | THE CEILING |
| 667 | READING |
| 731 | THE SAVE |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 88 | `CentralBank.WINDOW_PENALTY` | `.0025` | What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so borrowing reserves alw... |
| 91 | `CentralBank.DEFAULT_ADVANCES_MONTHS` | `6` | The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past it the arrears rule ... |
| 94 | `CentralBank.MAX_ADVANCES_CEILING` | `36` | The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. |
| 97 | `CentralBank.REVENUE_MONTHS` | `12` | How many months of the treasury's revenue the ceiling is averaged over. |
| 100 | `CentralBank.MAX_QE_SHARE` | `1.0` | The most of the city's term paper the holdings dial may aim at: all of it since 0.7.15, half before - Jerus: "central bank bond holding can ve 100% if one wants", a backstop for when the bank and investors will not ho... |
| 103 | `CentralBank.FULL_COMPRESSION_SHARE` | `.5` | The holding at which the term premium is wholly compressed: half the term paper, where it has been since 0.7.1 - MAX_QE_SHARE's value until the dial went past it (DebtManager.compression()). |
| 106 | `CentralBank.QE_SPEED` | `.25` | The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying toward a new target or se... |
| 109 | `CentralBank.QE_COMPRESSION` | `1.0` | How much of the term premium a holding of FULL_COMPRESSION_SHARE takes away: all of it, at 1. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 114 | `private double advancesToBank` | Advances to the commercial bank at the window: what its funding past its deposits owes here. |
| 117 | `private double advancesToTreasury` | Advances to the treasury: its overdraft, funded by money this bank made. |
| 125 | `private double paperHeld` | The city's paper this bank holds, AT FACE - principal, the one convention for every holder; the premium or discount it paid to face is its gain or loss the month it pays it (see THE HOLDINGS DIAL). |
| 134 | `private double targetShare` | The holdings dial (0.7.1): the share of the city's outstanding domestic TERM paper - serials and term loans, not notes - this bank aims to hold, 0 to MAX_QE_SHARE (a half until 0.7.15, all of it since). |
| 142 | `private double previousTarget` | ...and the setting before it: the pace is struck from the larger of the two, so selling a book down to a dial of nothing goes at the pace it was bought at rather than slowing for ever as it shrinks. |
| 151 | `private double redemptionDue, redemptionGainDue` | What the treasury paid for this bank's share of a bond it bought back between two presses, and the gain or loss on it against face, both settled at the top of the next month - the money retired there, inside the audit... |
| 154 | `private double reserves` | What this bank owes the banking system: every dollar it has made and not taken back. |
| 157 | `private double currency` | Cash in circulation outside the bank. |
| 168 | `private double lossCarried` | A LOSS IS NOT REMITTED. |
| 171 | `private double remittanceDue` | Last month's profit, owed to the treasury and paid at the top of this one. |
| 183 | `private double advancesCeilingMonths` | THE CEILING AS A DIAL (0.7.2): the most the treasury may owe here, in months of its trailing revenue, 0 to MAX_ADVANCES_CEILING. |
| 187 | `private double issued, retired` |  |
| 188 | `private double advancedToBank, repaidByBank` |  |
| 189 | `private double advancedToTreasury, repaidByTreasury` |  |
| 190 | `private double interestOnReserves, windowInterest, advancesInterest` |  |
| 191 | `private double remitted` |  |
| 193 | `private double boughtPaper, soldPaper, paperCoupons, paperRedeemed, boughtBack, paperGains` | The holdings' month (0.7.1): paid for paper and received for it, the coupons and principal on it, what a buyback redeemed, and the gains and losses against face. |
| 195 | `private double boughtFromHouseholds` | ...and of what it paid for paper, what it paid the households for theirs (0.7.15): money made and paid straight into their savings. |
| 197 | `private double boughtAtIssue, parAtIssue` | ...and what it paid the treasury at issue, and the par it took, rolling its own maturing paper (0.7.15, round 2): money made into the treasury's cash. |
| 199 | `private double defendedThisMonth` | The defence's month (0.7.2): the local price of the vault's dollars sold at this month's reprice - equity spent, not money destroyed. |
| 204 | `private double printedLifetime` | Advanced to the treasury since founding - "printed since founding". |
| 205 | `private double issuedLifetime, retiredLifetime, remittedLifetime` |  |
| 207 | `private double boughtPaperLifetime, soldPaperLifetime` | Paid for the city's paper and received for it since founding (0.7.1). |
| 209 | `private double boughtFromHouseholdsLifetime` | ...and of what it paid, what it paid the households since founding (0.7.15). |
| 211 | `private double boughtAtIssueLifetime, parAtIssueLifetime` | ...and what it paid at issue for its own maturing paper rolled, and the par, since founding (0.7.15, round 2). |
| 213 | `private double defendedLifetime` | Spent defending the currency since founding (0.7.2): the vault's dollars at the rates they were sold at. |
| 216 | `private final double[] revenue` | The treasury's revenue, the last REVENUE_MONTHS of it, for the ceiling. |
| 217 | `private int revenueFilled, revenueNext` |  |
| 220 | `private final java.util.function.DoubleSupplier vault` | Where the vault is read from. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 83 | 772 | **type** `public final class CentralBank` | The city's central bank: the balance sheet its money is made on, and the one place money is made or destroyed. |

### the dials (lines 85-110)

### the balance sheet (lines 111-184)

### the month (lines 185-200)

### the city's life (lines 201-243)

| line | len | member | says |
|---:|---:|---|---|
| 222 | 1 | `public CentralBank()` |  |
| 224 | 3 | `public CentralBank(java.util.function.DoubleSupplier vault)` |  |
| 229 | 11 | `public void startMonth()` | Clears the month's flows. |
| 241 | 1 | `private void issue(double x)` |  |
| 242 | 1 | `private void retire(double x)` |  |

### THE WINDOW (lines 244-300)

| line | len | member | says |
|---:|---:|---|---|
| 255 | 7 | `public double advanceToBank(double x)` | Money made and lent to the commercial bank at the window. |
| 264 | 8 | `public double repayFromBank(double x)` | ...and repaid, which destroys it. |
| 279 | 5 | `public void settleWindow(double owed)` | Brings the window to what the bank owes past its deposits, once a month at the settle: advances the difference or takes the repayment. |
| 286 | 6 | `public double payInterestOnReserves(double x)` | Interest on reserves: the policy rate on the bank's spare cash, paid in money made for it. |
| 294 | 6 | `public double chargeWindow(double x)` | The window's interest, from the commercial bank: income here, and money destroyed. |

### THE TREASURY (lines 301-379)

| line | len | member | says |
|---:|---:|---|---|
| 304 | 8 | `public double advanceToTreasury(double x)` | The treasury's shortfall, advanced in money made for it. |
| 314 | 8 | `public double repayFromTreasury(double x)` | ...and repaid out of cash above zero, which destroys it. |
| 324 | 3 | `public double advancesInterestDue(double policyAnnual)` | What a month on the advances outstanding costs at this policy rate. |
| 329 | 6 | `public double chargeAdvances(double x)` | The treasury's interest on its advances: income here, and money destroyed. |
| 343 | 9 | `public double remit()` | The profit, to the treasury: last month's, struck at closeMonth() and paid at the top of this one - a revenue line, "Central bank remittance". |
| 364 | 10 | `public void closeMonth()` | Strikes the month's profit and decides what is owed to the treasury. |
| 376 | 3 | `public double monthProfit()` | This month's income less what reserves cost, so far. |

### THE HOLDINGS DIAL (0.7.1) (lines 380-569)

| line | len | member | says |
|---:|---:|---|---|
| 436 | 9 | `public double buyPaper(double price, double face)` | Buys this much face for this price, in money made for it. |
| 455 | 6 | `public double buyPaperFromHouseholds(double price, double face)` | Buys this much face from the households for this price, in money made for them (0.7.15): the paper the bank could not sell it. |
| 472 | 10 | `public double buyAtIssue(double price, double face)` | Its add-on at issue (0.7.15, round 2): this much face of the city's new paper for this price - the issue's own price on each unit of face - paid to the treasury in money made for it. |
| 484 | 9 | `public double sellPaper(double price, double face)` | ...and sells it back, which destroys the price. |
| 495 | 6 | `public double takeCoupon(double x)` | The coupon on its share, from the treasury: income here, and money destroyed. |
| 503 | 7 | `public double takePrincipal(double x)` | Principal repaid on its share: off the book at face, and destroyed. |
| 517 | 5 | `public void paperBoughtBack(double price, double face)` | The treasury buys back a bond this bank holds part of, between two presses: the face comes off the book now, and the price and the gain against face wait for the top of the next month (settleRedemptions()), where the ... |
| 524 | 10 | `public double settleRedemptions()` | ...and settled: the price destroyed, the gain into this month's profit. |
| 536 | 1 | `public double getRedemptionDue()` | What the treasury has paid for this bank's share of a buyback and it has not yet settled: carried in the treasury's pool until it has. |
| 538 | 1 | `public double getTargetShare()` |  |
| 539 | 1 | `public double getPreviousTarget()` |  |
| 542 | 5 | `public void setTargetShare(double share)` | The dial, held to 0..MAX_QE_SHARE. |
| 555 | 3 | `public void restoreTargetShare(double share)` | The dial as a save left it, WITHOUT moving the memory of where it was: the load path's setter. |
| 564 | 5 | `public double stepFor(double termPrincipal)` | The most the book may move this month, at face, against this much term paper outstanding: QE_SPEED of the larger of the dial, the setting before it and the share it actually holds. |

### THE DEFENCE (0.7.2) (lines 570-630)

| line | len | member | says |
|---:|---:|---|---|
| 613 | 5 | `public void dollarsSold(double local)` | The vault's dollars were sold at the reprice for this much local money. |
| 620 | 1 | `public double getDefendedThisMonth()` | Spent defending the currency at this month's reprice, at the rate the dollars were sold at. |
| 622 | 1 | `public double getDefendedLifetime()` | ...and since founding. |
| 629 | 1 | `public double vaultSpent()` | Spent defending the currency since founding: the local price of every dollar the defence has sold. |

### THE CEILING (lines 631-666)

| line | len | member | says |
|---:|---:|---|---|
| 634 | 6 | `public void noteRevenue(double monthRevenue)` | Files a month of the treasury's revenue, for the ceiling. |
| 642 | 6 | `public double trailingRevenue()` | The treasury's revenue a month, averaged over what has been filed. |
| 650 | 1 | `public double ceiling()` | The most the treasury may owe here before the arrears rule decides who is paid: the dial's months of its trailing revenue. |
| 653 | 1 | `public double getAdvancesCeilingMonths()` | The ceiling dial, in months of revenue. |
| 656 | 4 | `public void setAdvancesCeilingMonths(double months)` | Sets the ceiling dial, held to 0..MAX_ADVANCES_CEILING; a number that is not one reads the default. |
| 662 | 1 | `public double headroom()` | What the treasury may still draw for anything that is not a promise. |
| 665 | 1 | `public boolean ceilingBound()` | True once the treasury owes the ceiling or more. |

### READING (lines 667-730)

| line | len | member | says |
|---:|---:|---|---|
| 669 | 1 | `public double getAdvancesToBank()` |  |
| 670 | 1 | `public double getAdvancesToTreasury()` |  |
| 671 | 1 | `public double getPaperHeld()` |  |
| 673 | 1 | `public double getVault()` | The vault, at today's rate, read from ForeignAccounts. |
| 674 | 1 | `public double getReserves()` |  |
| 675 | 1 | `public double getCurrency()` |  |
| 677 | 3 | `public double totalAssets()` |  |
| 682 | 1 | `public double m0()` | M0: everything this bank owes, which is every dollar it has made and not taken back. |
| 684 | 1 | `public double totalLiabilities()` |  |
| 686 | 1 | `public double equity()` |  |
| 688 | 1 | `public double getLossCarried()` |  |
| 689 | 1 | `public double getRemittanceDue()` |  |
| 691 | 1 | `public double getIssued()` |  |
| 692 | 1 | `public double getRetired()` |  |
| 693 | 1 | `public double getAdvancedToBank()` |  |
| 694 | 1 | `public double getRepaidByBank()` |  |
| 696 | 1 | `public double getAdvancedToTreasury()` | Printed this month: what was advanced to the treasury. |
| 697 | 1 | `public double getRepaidByTreasury()` |  |
| 698 | 1 | `public double getInterestOnReserves()` |  |
| 699 | 1 | `public double getWindowInterest()` |  |
| 700 | 1 | `public double getAdvancesInterest()` |  |
| 702 | 1 | `public double getRemitted()` | The remittance paid to the treasury this month. |
| 704 | 1 | `public double getBoughtPaper()` | The holdings' month (0.7.1). |
| 705 | 1 | `public double getSoldPaper()` |  |
| 706 | 1 | `public double getPaperCoupons()` |  |
| 707 | 1 | `public double getPaperRedeemed()` |  |
| 709 | 1 | `public double getBoughtBack()` | What a buyback redeemed of its paper, settled this month. |
| 710 | 1 | `public double getPaperGains()` |  |
| 711 | 1 | `public double getBoughtPaperLifetime()` |  |
| 712 | 1 | `public double getSoldPaperLifetime()` |  |
| 714 | 1 | `public double getBoughtFromHouseholds()` | Of what it paid for paper this month, what it paid the households (0.7.15); the treasury at issue, getBoughtAtIssue() (round 2); the rest it paid the bank. |
| 716 | 1 | `public double getBoughtFromHouseholdsLifetime()` | ...and since founding. |
| 718 | 1 | `public double getBoughtAtIssue()` | What it paid the treasury this month for its add-ons at issue, rolling its own maturing paper (0.7.15, round 2). |
| 720 | 1 | `public double getParAtIssue()` | ...their par: what of its maturing paper it rolled this month. |
| 722 | 1 | `public double getBoughtAtIssueLifetime()` | ...and since founding, what it paid. |
| 724 | 1 | `public double getParAtIssueLifetime()` | ...and the par. |
| 726 | 1 | `public double getPrintedLifetime()` |  |
| 727 | 1 | `public double getIssuedLifetime()` |  |
| 728 | 1 | `public double getRetiredLifetime()` |  |
| 729 | 1 | `public double getRemittedLifetime()` |  |

### THE SAVE (lines 731-854)

| line | len | member | says |
|---:|---:|---|---|
| 748 | 28 | `public double[] toSaveArray()` | Everything above that is state rather than this month's flow, as one array under its own key (DataSave.centralBank). |
| 777 | 32 | `public void restore(double[] saved)` |  |
| 811 | 16 | `public void reset()` | An empty bank: nothing lent, nothing made. |
| 833 | 21 | `public void redenominate(double scale)` | Every figure on this balance sheet, in the new unit. |

