# ForeignAccounts.java - 1,606 lines · 84 methods · 14 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

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
> sellReserves()), and since 0.7.2 the central bank selling it to defend the
> currency (A DEFENCE THAT SPENDS, at effectivePressure()); it cannot go
> below zero, and it is KEPT IN DOLLARS -
> reservesUsd - so everything local about it, what it is worth, what can be
> sold, the import cover and the net position, is those dollars at today's
> rate. When the currency moves the dollars stay put, and the move in their
> local value is booked beside the vault as a revaluation (revalueVault()),
> in the dollar debt's shape: not cash, and not an audit flow. A new city
> opens with the founders' US$1B in it (Game's THE FOUNDING RESERVE), and it
> is spent only when the push would weaken the currency (A RESERVE DEFENDS A
> ... (26 more lines in the source)

**Uses:** [MoneyAudit](MoneyAudit.md) (2)

**Used by (20):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LabourCheck](LabourCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 93 | · the rate |
| 105 | WHAT MOVES THE RATE |
| 279 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 813 | · the month's account |
| 821 | · the position |
| 823 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1038 | · reading |
| 1040 | WHAT THE CITY OWES ABROAD |
| 1172 | BUYING AND SELLING THE RESERVE |
| 1251 | · what the currency did to the vault |
| 1323 | · carrying |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 103 | `ForeignAccounts.OPENING_RATE` | `1.00` | Local currency per US dollar. |
| 125 | `ForeignAccounts.DEAD_BAND` | `.05` | Deficits smaller than this share of trade are noise, and are ignored. |
| 128 | `ForeignAccounts.COMFORTABLE_COVER` | `6` | Cover at which reserves fully absorb the month's pressure. |
| 131 | `ForeignAccounts.MAX_ABSORPTION` | `.85` | The most of the pressure deep reserves can absorb. |
| 141 | `ForeignAccounts.DRIFT_SPEED` | `.02` | How much of a month's pressure passes into the rate. |
| 179 | `ForeignAccounts.MIN_RATE` | `1e-9` | The least a US dollar can cost in local money: a numerical guard against a rate run to nothing, never a price the game expects to see (.01 until 0.7.3). |
| 181 | `ForeignAccounts.MAX_RATE` | `1e9` | ...and the most: a numerical guard against a rate run to infinity, never a price the game expects to see (100 until 0.7.3) - a city whose currency reaches a thousand reforms it. |
| 259 | `ForeignAccounts.OPENING_PARITY` | `1.00` | The rate at which a basket costs the same at home and abroad. |
| 319 | `ForeignAccounts.REVERSION` | `.004` | How hard. |
| 382 | `ForeignAccounts.SETTLING_MONTHS` | `24` | Months before the rate is allowed to move at all. |
| 385 | `ForeignAccounts.MIN_TRADE` | `50` | Below this much trade a month, the exchange rate is not a real price. |
| 495 | `ForeignAccounts.RATE_PULL` | `4.0` | How far the city's own rate is above the world's - in real terms since 0.7.2 - and what that is worth to the currency. |
| 498 | `ForeignAccounts.MAX_RATE_PRESSURE` | `4.0` | A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 points that was the m... |
| 929 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |

## Fields (state)

| line | field | says |
|---:|---|---|
| 196 | `private double minRate` | The same three, in TODAY's money. |
| 197 | `private double maxRate` |  |
| 198 | `private double parityBase` |  |
| 262 | `private double parity` | Save slot 18. |
| 264 | `private double localInflation, worldInflation` |  |
| 321 | `private double rate` |  |
| 322 | `private double lastPressure` |  |
| 323 | `private double lastAbsorption` |  |
| 404 | `private double minTrade` | The same floor, in TODAY's money. |
| 500 | `private double realRateDifferential` |  |
| 695 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 727 | `private boolean pinned` |  |
| 815 | `private double exports` |  |
| 816 | `private double imports` |  |
| 817 | `private double foreignInterest` |  |
| 818 | `private double financialIn` |  |
| 819 | `private double financialOut` |  |
| 874 | `private double reservesUsd` |  |
| 886 | `private double cumulativeBalance` | THE RECORD. |
| 887 | `private double forgiven` |  |
| 890 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 891 | `private double currentTrailing` |  |
| 897 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the pressure can be struck on the OVERALL balance over everything that crossed. |
| 898 | `private double financialGrossTrailing` |  |
| 912 | `private double lifetimeExports` | SINCE FOUNDING. |
| 913 | `private double lifetimeImports` |  |
| 914 | `private double lifetimeInterest` |  |
| 915 | `private double lifetimeFinancial` |  |
| 916 | `private double importsTrailing` |  |
| 917 | `private int monthsOfHistory` |  |
| 978 | `private double openness` | How much of the economy actually crosses the border. |
| 1063 | `private double foreignDebt` | local value of USD paper outstanding |
| 1064 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 1065 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 1066 | `private double lastRevaluation` |  |
| 1067 | `private double lifetimeRevaluation` |  |
| 1167 | `private double repudiated` |  |
| 1186 | `private double boughtThisMonth` |  |
| 1187 | `private double soldThisMonth` |  |
| 1271 | `private double lastVaultRate` |  |
| 1272 | `private double lastVaultRevaluation` |  |
| 1314 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 1321 | `private double lastValuation` |  |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 91 | 1516 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 93-104)

### WHAT MOVES THE RATE (lines 105-812)

| line | len | member | says |
|---:|---:|---|---|
| 200 | 1 | `public double getMinRate()` |  |
| 201 | 1 | `public double getMaxRate()` |  |
| 204 | 6 | `public void seedConstants(double unit)` | Re-seeds the money CONSTANTS at a given unit. |
| 277 | 31 | `public void setParity(double localLevel, double worldLevel, double localRate, double worldRate)` | The two inflation rates, and the level ratio parity is struck from. |
| 310 | 1 | `public double inflationGap()` | How far the city's inflation is running above the world's. |
| 313 | 1 | `public double getLocalInflation()` | The two inflations the month was handed - the halves of the real rate differential, for the forces page. |
| 314 | 1 | `public double getWorldInflation()` |  |
| 316 | 1 | `public double getParity()` |  |
| 329 | 44 | `public double pressure()` | The month's depreciation pressure: the current-account deficit as a share of everything the city trades. |
| 420 | 6 | `public double absorption()` | How much of it the reserve absorbs. |
| 503 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 508 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |
| 511 | 4 | `public double ratePressure()` | Negative when the city pays over the odds in real terms: a real rate advantage is support. |
| 600 | 8 | `public double effectivePressure()` | The pressure that actually reaches the rate this month. |
| 622 | 4 | `public double previewPressure()` | The same push, computed and not recorded - for a screen. |
| 637 | 1 | `public double previewRawPressure()` | ...and the readings it is made of, on the same figures and recorded nowhere (0.7.1): the imbalance and the real rate together, and what the vault would take of it - since 0.7.2 the share of this month's deficit the do... |
| 639 | 1 | `public double previewAbsorption()` |  |
| 641 | 1 | `public double previewDefenceUsd()` |  |
| 649 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 655 | 4 | `private double dollarsFor(double total, double deficitUsd)` | What the central bank sells against a push of this size on this deficit: the vault's capacity times the deficit, at most the vault; nothing against a rise, nothing on a month with no deficit. |
| 661 | 5 | `private double realisedFor(double total, double deficitUsd)` | ...and the share of the deficit it meets, which is what damps the push: the capacity when the vault covers the sale, less when it cannot. |
| 668 | 3 | `private double damped(double total, double absorbed)` | The push that reaches the rate, once the vault and the openness have had their share. |
| 677 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 698 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 700 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 702 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 704 | 1 | `public double getLastPressure()` |  |
| 706 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 722 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 730 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 744 | 56 | `public void repriceCurrency()` | Moves the rate on the month just taken. |
| 802 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 805 | 1 | `public double getRate()` | Local currency per US dollar. |
| 808 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 811 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 813-820)

### the position (lines 821-822)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 823-1037)

| line | len | member | says |
|---:|---:|---|---|
| 940 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 956 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 959 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 960 | 1 | `public double monthlyCurrentAccount()` |  |
| 962 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 980 | 1 | `public double getOpenness()` |  |
| 982 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 996 | 41 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 1038-1039)

### WHAT THE CITY OWES ABROAD (lines 1040-1171)

| line | len | member | says |
|---:|---:|---|---|
| 1073 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 1083 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 1084 | 1 | `public double getForeignDebtUsd()` |  |
| 1087 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 1088 | 1 | `public double getLifetimeRevaluation()` |  |
| 1098 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 1108 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 1111 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 1114 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 1117 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 1120 | 1 | `public double tradeBalance()` | Exports less imports. |
| 1123 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 1125 | 1 | `public double getFinancialIn()` |  |
| 1126 | 1 | `public double getFinancialOut()` |  |
| 1127 | 1 | `public double financialAccount()` |  |
| 1130 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 1136 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 1139 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 1149 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 1152 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 1161 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 1170 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 1172-1250)

| line | len | member | says |
|---:|---:|---|---|
| 1190 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 1208 | 6 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 1238 | 9 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1249 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### what the currency did to the vault (lines 1251-1322)

| line | len | member | says |
|---:|---:|---|---|
| 1275 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1282 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1284 | 1 | `public double getBoughtThisMonth()` |  |
| 1285 | 1 | `public double getSoldThisMonth()` |  |
| 1287 | 1 | `public double getLifetimeExports()` |  |
| 1288 | 1 | `public double getLifetimeImports()` |  |
| 1289 | 1 | `public double getLifetimeInterest()` |  |
| 1290 | 1 | `public double getLifetimeFinancial()` |  |
| 1302 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1316 | 1 | `public double getLifetimeIntervention()` |  |
| 1319 | 1 | `public double valuationChange()` | This month's part of that. |

### carrying (lines 1323-1606)

| line | len | member | says |
|---:|---:|---|---|
| 1335 | 100 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 1436 | 64 | `public void restore(double[] saved)` |  |
| 1501 | 40 | `public void reset()` |  |
| 1564 | 41 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

