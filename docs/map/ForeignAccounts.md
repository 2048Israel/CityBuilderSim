# ForeignAccounts.java - 1,757 lines · 94 methods · 14 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-09-23 by CodeMap; line numbers are as of that run.

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
> opens with the founders' US$1B in it (Game's THE FOUNDING RESERVE), and it
> ... (27 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (25):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 94 | · the rate |
| 106 | WHAT MOVES THE RATE |
| 280 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 814 | · the month's account |
| 822 | · the position |
| 824 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1040 | · reading |
| 1042 | WHAT THE CITY OWES ABROAD |
| 1174 | BUYING AND SELLING THE RESERVE |
| 1253 | THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) |
| 1369 | · what the currency did to the vault |
| 1441 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 104 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 126 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 129 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 132 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 142 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 180 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 182 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 260 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 320 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 383 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 386 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 496 | `ForeignAccounts.RATE_PULL` | `4.0` | How far the city's own rate is above the world's - in real terms since 0.7.2 - and what that is worth to the currency. |
| 499 | `ForeignAccounts.MAX_RATE_PRESSURE` | `4.0` | A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 points that was the m... |
| 931 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 197 | `private double minRate` | The same three, in TODAY's money. |
| 198 | `private double maxRate` |  |
| 199 | `private double parityBase` |  |
| 263 | `private double parity` | Save slot 18. |
| 265 | `private double localInflation, worldInflation` |  |
| 322 | `private double rate` |  |
| 323 | `private double lastPressure` |  |
| 324 | `private double lastAbsorption` |  |
| 405 | `private double minTrade` | The same floor, in TODAY's money. |
| 501 | `private double realRateDifferential` |  |
| 696 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 728 | `private boolean pinned` |  |
| 816 | `private double exports` |  |
| 817 | `private double imports` |  |
| 818 | `private double foreignInterest` |  |
| 819 | `private double financialIn` |  |
| 820 | `private double financialOut` |  |
| 876 | `private double reservesUsd` |  |
| 888 | `private double cumulativeBalance` | THE RECORD. |
| 889 | `private double forgiven` |  |
| 892 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 893 | `private double currentTrailing` |  |
| 899 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the pressure can be struck on the OVERALL balance over everything that crossed. |
| 900 | `private double financialGrossTrailing` |  |
| 914 | `private double lifetimeExports` | SINCE FOUNDING. |
| 915 | `private double lifetimeImports` |  |
| 916 | `private double lifetimeInterest` |  |
| 917 | `private double lifetimeFinancial` |  |
| 918 | `private double importsTrailing` |  |
| 919 | `private int monthsOfHistory` |  |
| 980 | `private double openness` | How much of the economy actually crosses the border. |
| 1065 | `private double foreignDebt` | local value of USD paper outstanding |
| 1066 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 1067 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 1068 | `private double lastRevaluation` |  |
| 1069 | `private double lifetimeRevaluation` |  |
| 1169 | `private double repudiated` |  |
| 1188 | `private double boughtThisMonth` |  |
| 1189 | `private double soldThisMonth` |  |
| 1295 | `private double landUsdOpen, landVaultUsdOpen` | The month in progress: dollars paid for land, and the part of them that came out of the vault. |
| 1302 | `private double landLocalOpen, landVaultLocalOpen` | ...and their local price on the day, all of it and the vault's part: the part of the budget's land line bought abroad (the rest of that line is plots bought back from businesses, in local money), and the part of it no... |
| 1304 | `private double landUsd, landVaultUsd, landLocal, landVaultLocal` | The month struck - what the screens show between two presses. |
| 1306 | `private double landUsdLifetime, landVaultUsdLifetime` | ...and since founding (slots 33-34). |
| 1389 | `private double lastVaultRate` |  |
| 1390 | `private double lastVaultRevaluation` |  |
| 1432 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 1439 | `private double lastValuation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 92 | 1666 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 94-105)

### WHAT MOVES THE RATE (lines 106-813)

| line | len | member | says |
|---:|---:|---|---|
| 201 | 1 | `public double getMinRate()` |  |
| 202 | 1 | `public double getMaxRate()` |  |
| 205 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 278 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates, and the level ratio parity is struck from. |
| 311 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 314 | 1 | `public double getLocalInflation()` | The two inflations the month was handed - the halves of the real rate differential, for the forces page. |
| 315 | 1 | `public double getWorldInflation()` |  |
| 317 | 1 | `public double getParity()` |  |
| 330 | 44 | `public double pressure()` | The month's depreciation pressure: the current-account deficit as a share of everything the city trades. |
| 421 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 504 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 509 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |
| 512 | 4 | `public double ratePressure()` | Negative when the city pays over the odds in real terms: a real rate advantage is support. |
| 601 | 8 | `public double effectivePressure()` | The pressure that actually reaches the rate this month. |
| 623 | 4 | `public double previewPressure()` | The same push, computed and not recorded - for a screen. |
| 638 | 1 | `public double previewRawPressure()` | ...and the readings it is made of, on the same figures and recorded nowhere (0.7.1): the imbalance and the real rate together, and what the vault would take of it - since 0.7.2 the share of this month's deficit the do... |
| 640 | 1 | `public double previewAbsorption()` |  |
| 642 | 1 | `public double previewDefenceUsd()` |  |
| 650 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 656 | 4 | `private double dollarsFor(double total, double deficitUsd)` | What the central bank sells against a push of this size on this deficit: the vault's capacity times the deficit, at most the vault; nothing against a rise, nothing on a month with no deficit. |
| 662 | 5 | `private double realisedFor(double total, double deficitUsd)` | ...and the share of the deficit it meets, which is what damps the push: the capacity when the vault covers the sale, less when it cannot. |
| 669 | 3 | `private double damped(double total, double absorbed)` | The push that reaches the rate, once the vault and the openness have had their share. |
| 678 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 699 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 701 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 703 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 705 | 1 | `public double getLastPressure()` |  |
| 707 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 723 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 731 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 745 | 56 | `public void repriceCurrency()` | Moves the rate on the month just taken. |
| 803 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 806 | 1 | `public double getRate()` | Local currency per US dollar. |
| 809 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 812 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 814-821)

### the position (lines 822-823)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 824-1039)

| line | len | member | says |
|---:|---:|---|---|
| 942 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 958 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 961 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 962 | 1 | `public double monthlyCurrentAccount()` |  |
| 964 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 982 | 1 | `public double getOpenness()` |  |
| 984 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 998 | 41 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 1040-1041)

### WHAT THE CITY OWES ABROAD (lines 1042-1173)

| line | len | member | says |
|---:|---:|---|---|
| 1075 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 1085 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 1086 | 1 | `public double getForeignDebtUsd()` |  |
| 1089 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 1090 | 1 | `public double getLifetimeRevaluation()` |  |
| 1100 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 1110 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 1113 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 1116 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 1119 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 1122 | 1 | `public double tradeBalance()` | Exports less imports. |
| 1125 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 1127 | 1 | `public double getFinancialIn()` |  |
| 1128 | 1 | `public double getFinancialOut()` |  |
| 1129 | 1 | `public double financialAccount()` |  |
| 1132 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 1138 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 1141 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 1151 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 1154 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 1163 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 1172 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 1174-1252)

| line | len | member | says |
|---:|---:|---|---|
| 1192 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 1210 | 6 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 1240 | 9 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1251 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) (lines 1253-1368)

| line | len | member | says |
|---:|---:|---|---|
| 1316 | 7 | `public double buyAndSpendDollarsForLand(double usd)` | Buys exactly these dollars at today's rate and pays them to the land's seller, in one movement: the vault is untouched, and nothing a reserve purchase would leave behind is left (see THE LAND OFFICE IS PAID IN DOLLARS). |
| 1331 | 13 | `public double spendReservesOnLand(double usd)` | Pays the land's seller out of the vault: at most what it holds, and everything it holds empties it exactly. |
| 1346 | 7 | `public void strikeLandMonth()` | Strikes the land's month where the government's books strike its local cost. |
| 1355 | 1 | `public double getLandUsdThisMonth()` | Dollars paid for land in the month last struck, both ways. |
| 1357 | 1 | `public double getLandUsdFromVaultThisMonth()` | ...of which out of the vault. |
| 1359 | 1 | `public double getLandLocalThisMonth()` | What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. |
| 1361 | 1 | `public double getLandLocalFromVaultThisMonth()` | ...and the vault's part of it - the part of the budget's land line no cash paid. |
| 1363 | 1 | `public double getLandUsdLifetime()` | Dollars paid for land since founding, both ways. |
| 1365 | 1 | `public double getLandUsdFromVaultLifetime()` | ...of which out of the vault. |
| 1367 | 1 | `public double getLandUsdPending()` | Dollars paid for land since the last strike - the month in progress. |

### what the currency did to the vault (lines 1369-1440)

| line | len | member | says |
|---:|---:|---|---|
| 1393 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1400 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1402 | 1 | `public double getBoughtThisMonth()` |  |
| 1403 | 1 | `public double getSoldThisMonth()` |  |
| 1405 | 1 | `public double getLifetimeExports()` |  |
| 1406 | 1 | `public double getLifetimeImports()` |  |
| 1407 | 1 | `public double getLifetimeInterest()` |  |
| 1408 | 1 | `public double getLifetimeFinancial()` |  |
| 1420 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1434 | 1 | `public double getLifetimeIntervention()` |  |
| 1437 | 1 | `public double valuationChange()` | This month's part of that. |

### carrying (lines 1441-1757)

| line | len | member | says |
|---:|---:|---|---|
| 1453 | 115 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 1569 | 74 | `public void restore(double[] saved)` |  |
| 1644 | 43 | `public void reset()` |  |
| 1711 | 45 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

