# ForeignAccounts.java - 1,759 lines · 94 methods · 14 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-09-26 by CodeMap; line numbers are as of that run.

> The city's dealings with the rest of the world: the balance of payments, the
> reserve position, and the exchange rate.
> 
> ==================== WHY THIS IS NEARLY EMPTY ====================
> 
> Almost nothing here is calculated. The balance of payments is read straight
> off MoneyAudit, which has been computing it since the day it was written
> without anybody noticing - it tracks every flow crossing the city's edge and
> reconciles them to the cent every month.
> 
> What was missing was not the arithmetic but the BOUNDARY. "Outside the pools"
> bundled households, who are domestic and simply not modelled as a pool,
> together with the rest of the world. Tag those apart - see MoneyAudit.Scope -
> and the foreign subset of a list that already balances IS the balance of
> payments. This class is the running total of it.
> 
> That is the same move the bank rewrite kept making: find the identity that is
> already there rather than inventing a parallel mechanism that can drift from
> it. There is no second set of books here to disagree with the first.
> 
> ==================== PHASE ONE OF FOUR ====================
> 
> Jerus's plan, in order:
> 
>   1. THIS. The accounting only. Split the boundary, count the reserve, show
>      it on a screen. The exchange rate exists and is pinned at 1.00, and
>      NOTHING BEHAVES DIFFERENTLY - the point is to prove the split is honest
>      against MoneyAudit before anything depends on it.
>   2. The rate moves, on reserve adequacy. Import prices and export revenues
>      start converting through it, and wages get a cost-of-living drift.
>   3. Foreign debt: USD instruments beside the existing ones. This is what
>      closes the circular-capital hole, because the treasury finally has a
>      lender that is not its own bank.
>   4. Capital flows: the carry trade in, sudden stops out, straight into the
>      bank's deposits.
> 
> ALL FOUR ARE BUILT, and the list is kept as the plan it was. The rate
> floats (repriceCurrency(), WHAT MOVES THE RATE); the treasury borrows in
> dollars (WHAT THE CITY OWES ABROAD, and DebtManager's foreign paper); the
> hot money and the carry trade are CapitalFlows.
> 
> See claude/foreign-exchange-design.md.
> 
> ==================== WHAT A RESERVE IS, TODAY ====================
> 
> TWO NUMBERS, which were one number until the split (TWO NUMBERS THAT WERE
> ONE NUMBER, below).
> 
> THE VAULT is a holding: the US dollars the treasury chose to buy and has
> not yet sold. The treasury buying and selling moves it (buyReserves(),
> sellReserves()), since 0.7.2 the central bank selling it to defend the
> currency (A DEFENCE THAT SPENDS, at effectivePressure()), and since 0.7.6
> the land office paid out of it when the player says so
> (spendReservesOnLand()); it cannot go below zero, and it is KEPT IN DOLLARS -
> reservesUsd - so everything local about it, what it is worth, what can be
> sold, the import cover and the net position, is those dollars at today's
> rate. When the currency moves the dollars stay put, and the move in their
> local value is booked beside the vault as a revaluation (revalueVault()),
> in the dollar debt's shape: not cash, and not an audit flow. A new city
> opens with the founders' dollars in it - US$25M on the default founding
> ... (28 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (26):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 95 | · the rate |
| 107 | WHAT MOVES THE RATE |
| 281 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 815 | · the month's account |
| 823 | · the position |
| 825 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1041 | · reading |
| 1043 | WHAT THE CITY OWES ABROAD |
| 1175 | BUYING AND SELLING THE RESERVE |
| 1254 | THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) |
| 1370 | · what the currency did to the vault |
| 1442 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 105 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 127 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 130 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 133 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 143 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 181 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 183 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 261 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 321 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 384 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 387 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 497 | `ForeignAccounts.RATE_PULL` | `4.0` | How far the city's own rate is above the world's - in real terms since 0.7.2 - and what that is worth to the currency. |
| 500 | `ForeignAccounts.MAX_RATE_PRESSURE` | `4.0` | A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 points that was the m... |
| 932 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 198 | `private double minRate` | The same three, in TODAY's money. |
| 199 | `private double maxRate` |  |
| 200 | `private double parityBase` |  |
| 264 | `private double parity` | Save slot 18. |
| 266 | `private double localInflation, worldInflation` |  |
| 323 | `private double rate` |  |
| 324 | `private double lastPressure` |  |
| 325 | `private double lastAbsorption` |  |
| 406 | `private double minTrade` | The same floor, in TODAY's money. |
| 502 | `private double realRateDifferential` |  |
| 697 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 729 | `private boolean pinned` |  |
| 817 | `private double exports` |  |
| 818 | `private double imports` |  |
| 819 | `private double foreignInterest` |  |
| 820 | `private double financialIn` |  |
| 821 | `private double financialOut` |  |
| 877 | `private double reservesUsd` |  |
| 889 | `private double cumulativeBalance` | THE RECORD. |
| 890 | `private double forgiven` |  |
| 893 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 894 | `private double currentTrailing` |  |
| 900 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the pressure can be struck on the OVERALL balance over everything that crossed. |
| 901 | `private double financialGrossTrailing` |  |
| 915 | `private double lifetimeExports` | SINCE FOUNDING. |
| 916 | `private double lifetimeImports` |  |
| 917 | `private double lifetimeInterest` |  |
| 918 | `private double lifetimeFinancial` |  |
| 919 | `private double importsTrailing` |  |
| 920 | `private int monthsOfHistory` |  |
| 981 | `private double openness` | How much of the economy actually crosses the border. |
| 1066 | `private double foreignDebt` | local value of USD paper outstanding |
| 1067 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 1068 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 1069 | `private double lastRevaluation` |  |
| 1070 | `private double lifetimeRevaluation` |  |
| 1170 | `private double repudiated` |  |
| 1189 | `private double boughtThisMonth` |  |
| 1190 | `private double soldThisMonth` |  |
| 1296 | `private double landUsdOpen, landVaultUsdOpen` | The month in progress: dollars paid for land, and the part of them that came out of the vault. |
| 1303 | `private double landLocalOpen, landVaultLocalOpen` | ...and their local price on the day, all of it and the vault's part: the part of the budget's land line bought abroad (the rest of that line is plots bought back from businesses, in local money), and the part of it no... |
| 1305 | `private double landUsd, landVaultUsd, landLocal, landVaultLocal` | The month struck - what the screens show between two presses. |
| 1307 | `private double landUsdLifetime, landVaultUsdLifetime` | ...and since founding (slots 33-34). |
| 1390 | `private double lastVaultRate` |  |
| 1391 | `private double lastVaultRevaluation` |  |
| 1433 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 1440 | `private double lastValuation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 1667 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 95-106)

### WHAT MOVES THE RATE (lines 107-814)

| line | len | member | says |
|---:|---:|---|---|
| 202 | 1 | `public double getMinRate()` |  |
| 203 | 1 | `public double getMaxRate()` |  |
| 206 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 279 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates, and the level ratio parity is struck from. |
| 312 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 315 | 1 | `public double getLocalInflation()` | The two inflations the month was handed - the halves of the real rate differential, for the forces page. |
| 316 | 1 | `public double getWorldInflation()` |  |
| 318 | 1 | `public double getParity()` |  |
| 331 | 44 | `public double pressure()` | The month's depreciation pressure: the current-account deficit as a share of everything the city trades. |
| 422 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 505 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 510 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |
| 513 | 4 | `public double ratePressure()` | Negative when the city pays over the odds in real terms: a real rate advantage is support. |
| 602 | 8 | `public double effectivePressure()` | The pressure that actually reaches the rate this month. |
| 624 | 4 | `public double previewPressure()` | The same push, computed and not recorded - for a screen. |
| 639 | 1 | `public double previewRawPressure()` | ...and the readings it is made of, on the same figures and recorded nowhere (0.7.1): the imbalance and the real rate together, and what the vault would take of it - since 0.7.2 the share of this month's deficit the do... |
| 641 | 1 | `public double previewAbsorption()` |  |
| 643 | 1 | `public double previewDefenceUsd()` |  |
| 651 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 657 | 4 | `private double dollarsFor(double total, double deficitUsd)` | What the central bank sells against a push of this size on this deficit: the vault's capacity times the deficit, at most the vault; nothing against a rise, nothing on a month with no deficit. |
| 663 | 5 | `private double realisedFor(double total, double deficitUsd)` | ...and the share of the deficit it meets, which is what damps the push: the capacity when the vault covers the sale, less when it cannot. |
| 670 | 3 | `private double damped(double total, double absorbed)` | The push that reaches the rate, once the vault and the openness have had their share. |
| 679 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 700 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 702 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 704 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 706 | 1 | `public double getLastPressure()` |  |
| 708 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 724 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 732 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 746 | 56 | `public void repriceCurrency()` | Moves the rate on the month just taken. |
| 804 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 807 | 1 | `public double getRate()` | Local currency per US dollar. |
| 810 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 813 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 815-822)

### the position (lines 823-824)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 825-1040)

| line | len | member | says |
|---:|---:|---|---|
| 943 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 959 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 962 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 963 | 1 | `public double monthlyCurrentAccount()` |  |
| 965 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 983 | 1 | `public double getOpenness()` |  |
| 985 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 999 | 41 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 1041-1042)

### WHAT THE CITY OWES ABROAD (lines 1043-1174)

| line | len | member | says |
|---:|---:|---|---|
| 1076 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 1086 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 1087 | 1 | `public double getForeignDebtUsd()` |  |
| 1090 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 1091 | 1 | `public double getLifetimeRevaluation()` |  |
| 1101 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 1111 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 1114 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 1117 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 1120 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 1123 | 1 | `public double tradeBalance()` | Exports less imports. |
| 1126 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 1128 | 1 | `public double getFinancialIn()` |  |
| 1129 | 1 | `public double getFinancialOut()` |  |
| 1130 | 1 | `public double financialAccount()` |  |
| 1133 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 1139 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 1142 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 1152 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 1155 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 1164 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 1173 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 1175-1253)

| line | len | member | says |
|---:|---:|---|---|
| 1193 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 1211 | 6 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 1241 | 9 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1252 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) (lines 1254-1369)

| line | len | member | says |
|---:|---:|---|---|
| 1317 | 7 | `public double buyAndSpendDollarsForLand(double usd)` | Buys exactly these dollars at today's rate and pays them to the land's seller, in one movement: the vault is untouched, and nothing a reserve purchase would leave behind is left (see THE LAND OFFICE IS PAID IN DOLLARS). |
| 1332 | 13 | `public double spendReservesOnLand(double usd)` | Pays the land's seller out of the vault: at most what it holds, and everything it holds empties it exactly. |
| 1347 | 7 | `public void strikeLandMonth()` | Strikes the land's month where the government's books strike its local cost. |
| 1356 | 1 | `public double getLandUsdThisMonth()` | Dollars paid for land in the month last struck, both ways. |
| 1358 | 1 | `public double getLandUsdFromVaultThisMonth()` | ...of which out of the vault. |
| 1360 | 1 | `public double getLandLocalThisMonth()` | What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. |
| 1362 | 1 | `public double getLandLocalFromVaultThisMonth()` | ...and the vault's part of it - the part of the budget's land line no cash paid. |
| 1364 | 1 | `public double getLandUsdLifetime()` | Dollars paid for land since founding, both ways. |
| 1366 | 1 | `public double getLandUsdFromVaultLifetime()` | ...of which out of the vault. |
| 1368 | 1 | `public double getLandUsdPending()` | Dollars paid for land since the last strike - the month in progress. |

### what the currency did to the vault (lines 1370-1441)

| line | len | member | says |
|---:|---:|---|---|
| 1394 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1401 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1403 | 1 | `public double getBoughtThisMonth()` |  |
| 1404 | 1 | `public double getSoldThisMonth()` |  |
| 1406 | 1 | `public double getLifetimeExports()` |  |
| 1407 | 1 | `public double getLifetimeImports()` |  |
| 1408 | 1 | `public double getLifetimeInterest()` |  |
| 1409 | 1 | `public double getLifetimeFinancial()` |  |
| 1421 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1435 | 1 | `public double getLifetimeIntervention()` |  |
| 1438 | 1 | `public double valuationChange()` | This month's part of that. |

### carrying (lines 1442-1759)

| line | len | member | says |
|---:|---:|---|---|
| 1454 | 115 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 1570 | 75 | `public void restore(double[] saved)` |  |
| 1646 | 43 | `public void reset()` |  |
| 1713 | 45 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

