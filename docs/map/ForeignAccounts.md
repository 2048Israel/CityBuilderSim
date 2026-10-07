# ForeignAccounts.java - 1,986 lines · 107 methods · 18 constants · model

`ham/citybuildersim/ForeignAccounts.java` - generated 2026-10-05 by CodeMap; line numbers are as of that run.

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

**Used by (28):** [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CityBasket](CityBasket.md), [CurrencyCheck](CurrencyCheck.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignCheck](ForeignCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Founding](Founding.md), [Game](Game.md), [GovernmentScreen](GovernmentScreen.md), [HistorySave](HistorySave.md), [HouseholdBalance](HouseholdBalance.md), [LabourCheck](LabourCheck.md), [LandCheck](LandCheck.md), [LandManager](LandManager.md), [LandMarket](LandMarket.md), [LandScreen](LandScreen.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [NewGameCheck](NewGameCheck.md), [OutwardInvestment](OutwardInvestment.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SaveFileCheck](SaveFileCheck.md), [SummaryScreen](SummaryScreen.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 95 | · the rate |
| 107 | WHAT MOVES THE RATE |
| 281 | · · PARITY IS A RATE, NOT A RATIO, AND IT NEEDS AN ANCHOR IN MONEY. |
| 892 | · the month's account |
| 900 | · the position |
| 902 | TWO NUMBERS THAT WERE ONE NUMBER |
| 1121 | · reading |
| 1123 | WHAT THE CITY OWES ABROAD |
| 1255 | BUYING AND SELLING THE RESERVE |
| 1334 | THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) |
| 1450 | · what the currency did to the vault |
| 1522 | WHAT THE TRADE TAB READS (0.7.35) |
| 1643 | · carrying |

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
| 500 | `ForeignAccounts.RATE_PULL` | `4.0` | How far the city's own rate is above the world's - in real terms since 0.7.2 - and what that is worth to the currency. |
| 503 | `ForeignAccounts.MAX_RATE_PRESSURE` | `4.0` | A numerical guard, not a mechanic: it binds only at a real gap of MAX_RATE_PRESSURE / RATE_PULL - a hundred points, a currency in collapse rather than a policy (it was .8 until 0.7.2, a cap at 13 points that was the m... |
| 539 | `ForeignAccounts.EXPECTED_REVERSION` | `.15` | How fast investors expect a currency away from parity to come back, a fraction of the log gap a year: the reversion that sets the UIP level at e^(-gap / this). |
| 1009 | `ForeignAccounts.COVER_WINDOW` | `12` | How many months the trailing figures are averaged over: the import bill the cover is measured against, and the balances the pressure reads. |
| 1565 | `ForeignAccounts.PARITY_WATCH` | `.25` | How far from parity, either side, the rate reads as a watch - amber on the Trade tab, the drawer's THE CURRENCY row and the header's rate line, which until 0.7.35 judged one rate three ways (the spec's B9): the drawer... |
| 1568 | `ForeignAccounts.PARITY_FAR` | `.50` | ...and how far reads as far: red on all four. |
| 1583 | `ForeignAccounts.THIN_COVER` | `3` | Months of import cover under which the world prices a currency for a crisis rather than on its trade balance - the Trade tab's red line and its alert, three months by the usual rule of thumb. |

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
| 505 | `private double realRateDifferential` |  |
| 578 | `private double expectedDrift` | ================ THE ANCHORED DRIFT (0.7.42) ================ |
| 767 | `private double defenceUsd, defenceLocal, defenceUsdLifetime` | THE MONTH'S DEFENCE: the dollars the central bank sold at the last reprice and the local money they fetched, at that month's rate, and the dollars sold since founding. |
| 799 | `private boolean pinned` |  |
| 894 | `private double exports` |  |
| 895 | `private double imports` |  |
| 896 | `private double foreignInterest` |  |
| 897 | `private double financialIn` |  |
| 898 | `private double financialOut` |  |
| 954 | `private double reservesUsd` |  |
| 966 | `private double cumulativeBalance` | THE RECORD. |
| 967 | `private double forgiven` |  |
| 970 | `private double exportsTrailing` | Trailing means, for anything the rate is decided on. |
| 971 | `private double currentTrailing` |  |
| 977 | `private double financialTrailing` | The financial account, trailing, and the gross of it - what crossed in either direction - so the pressure can be struck on the OVERALL balance over everything that crossed. |
| 978 | `private double financialGrossTrailing` |  |
| 992 | `private double lifetimeExports` | SINCE FOUNDING. |
| 993 | `private double lifetimeImports` |  |
| 994 | `private double lifetimeInterest` |  |
| 995 | `private double lifetimeFinancial` |  |
| 996 | `private double importsTrailing` |  |
| 997 | `private int monthsOfHistory` |  |
| 1058 | `private double openness` | How much of the economy actually crosses the border. |
| 1146 | `private double foreignDebt` | local value of USD paper outstanding |
| 1147 | `private double foreignDebtUsd` | local value of USD paper outstanding |
| 1148 | `private double lastDebtRate` | ...and in the dollars it is owed in |
| 1149 | `private double lastRevaluation` |  |
| 1150 | `private double lifetimeRevaluation` |  |
| 1250 | `private double repudiated` |  |
| 1269 | `private double boughtThisMonth` |  |
| 1270 | `private double soldThisMonth` |  |
| 1376 | `private double landUsdOpen, landVaultUsdOpen` | The month in progress: dollars paid for land, and the part of them that came out of the vault. |
| 1383 | `private double landLocalOpen, landVaultLocalOpen` | ...and their local price on the day, all of it and the vault's part: the part of the budget's land line bought abroad (the rest of that line is plots bought back from businesses, in local money), and the part of it no... |
| 1385 | `private double landUsd, landVaultUsd, landLocal, landVaultLocal` | The month struck - what the screens show between two presses. |
| 1387 | `private double landUsdLifetime, landVaultUsdLifetime` | ...and since founding (slots 33-34). |
| 1470 | `private double lastVaultRate` |  |
| 1471 | `private double lastVaultRevaluation` |  |
| 1513 | `private double lifetimeIntervention` | What the treasury has bought and sold on its own account, since founding. |
| 1520 | `private double lastValuation` |  |
| 1550 | `private boolean monthCounted` | Whether the month's flows are the month's: false only after a founding or a load of an older save. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 93 | 1894 | **type** `public class ForeignAccounts` | The city's dealings with the rest of the world: the balance of payments, the reserve position, and the exchange rate. |

### the rate (lines 95-106)

### WHAT MOVES THE RATE (lines 107-891)

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
| 508 | 3 | `public void setRealRateDifferential(double differential)` |  |
| 513 | 1 | `public double getRealRateDifferential()` | The city's real rate less the world's, as the month was handed it. |
| 542 | 5 | `public double ratePressure()` | Negative when the city pays over the odds in real terms: a real rate advantage is support - less what a strong currency is expected to give back (INVESTORS EXPECT A CURRENCY TO COME BACK). |
| 549 | 3 | `public double uipLevel()` | Where the rate's own pressure is zero against parity: rate / parity = e^(-real gap / EXPECTED_REVERSION). |
| 581 | 3 | `public void setExpectedDrift(double drift)` |  |
| 586 | 1 | `public double getExpectedDrift()` | The anchored drift the month was handed, a fraction a year. |
| 672 | 8 | `public double effectivePressure()` | The pressure that actually reaches the rate this month. |
| 694 | 4 | `public double previewPressure()` | The same push, computed and not recorded - for a screen. |
| 709 | 1 | `public double previewRawPressure()` | ...and the readings it is made of, on the same figures and recorded nowhere (0.7.1): the imbalance and the real rate together, and what the vault would take of it - since 0.7.2 the share of this month's deficit the do... |
| 711 | 1 | `public double previewAbsorption()` |  |
| 713 | 1 | `public double previewDefenceUsd()` |  |
| 721 | 4 | `public double monthDeficitUsd()` | The month's own net outflow, in dollars at the month's rate: its current plus financial account when that is negative, and nothing when it is not. |
| 727 | 4 | `private double dollarsFor(double total, double deficitUsd)` | What the central bank sells against a push of this size on this deficit: the vault's capacity times the deficit, at most the vault; nothing against a rise, nothing on a month with no deficit. |
| 733 | 5 | `private double realisedFor(double total, double deficitUsd)` | ...and the share of the deficit it meets, which is what damps the push: the capacity when the vault covers the sale, less when it cannot. |
| 740 | 3 | `private double damped(double total, double absorbed)` | The push that reaches the rate, once the vault and the openness have had their share. |
| 749 | 8 | `private void sellToDefend(double usd)` | The sale itself, at the month's rate - before the reprice moves it, so the dollars sold are never revalued (revalueVault() strikes the move on what is left). |
| 770 | 1 | `public double getDefenceUsd()` | Dollars sold defending the currency at the last reprice. |
| 772 | 1 | `public double getDefenceLocal()` | ...and the local money they fetched, which is what the central bank's equity fell by (CentralBank.dollarsSold()). |
| 774 | 1 | `public double getDefenceUsdLifetime()` | Dollars sold defending the currency since founding. |
| 776 | 1 | `public double getLastPressure()` |  |
| 778 | 1 | `public double getLastAbsorption()` | The share of the month's deficit the vault met - the realised absorption, 0 on a month nothing was sold. |
| 794 | 4 | `public void pinRate(double fixed)` | Holds the exchange rate still. |
| 802 | 1 | `public boolean isPinned()` | True while the rate is being held for a measurement. |
| 816 | 63 | `public void repriceCurrency()` | Moves the rate on the month just taken. |
| 881 | 1 | `public double deviationFromParity()` | How far the currency sits from parity. |
| 884 | 1 | `public double getRate()` | Local currency per US dollar. |
| 887 | 1 | `public double toLocal(double usd)` | What a USD amount is worth in the city's own money. |
| 890 | 1 | `public double toUsd(double local)` | ...and the other way. |

### the month's account (lines 892-899)

### the position (lines 900-901)

### TWO NUMBERS THAT WERE ONE NUMBER (lines 902-1120)

| line | len | member | says |
|---:|---:|---|---|
| 1020 | 14 | `public double importCover()` | Months of imports the reserve would cover. |
| 1036 | 1 | `public double monthlyImports()` | The trailing monthly import bill the cover is measured against. |
| 1039 | 1 | `public double monthlyExports()` | ...and the trailing figures the rate is decided on. |
| 1040 | 1 | `public double monthlyCurrentAccount()` |  |
| 1042 | 1 | `public double monthlyFinancialAccount()` | The financial account, trailing: positive is money coming in. |
| 1060 | 1 | `public double getOpenness()` |  |
| 1062 | 3 | `public void takeMonth(MoneyAudit.Result month)` |  |
| 1076 | 44 | `public void takeMonth(MoneyAudit.Result month, double monthlyGdp)` | Takes the month off the audit that has just been struck. |

### reading (lines 1121-1122)

### WHAT THE CITY OWES ABROAD (lines 1123-1254)

| line | len | member | says |
|---:|---:|---|---|
| 1156 | 8 | `public void takeForeignDebt(double usdOutstanding, double rateNow)` |  |
| 1166 | 1 | `public double getForeignDebt()` | What the city owes abroad, in local money at today's rate. |
| 1167 | 1 | `public double getForeignDebtUsd()` |  |
| 1170 | 1 | `public double getLastRevaluation()` | What the currency did to that debt this month. |
| 1171 | 1 | `public double getLifetimeRevaluation()` |  |
| 1181 | 1 | `public double getReservesUsd()` | The reserve position in dollars - the stock itself. |
| 1191 | 1 | `public double netForeignPosition()` | Claims abroad less what is owed abroad: the city's net position. |
| 1194 | 1 | `public double getExports()` | Goods and services sold abroad. |
| 1197 | 1 | `public double tradeImports()` | Goods bought abroad, interest excluded. |
| 1200 | 1 | `public double getForeignInterest()` | Interest paid to foreign creditors. |
| 1203 | 1 | `public double tradeBalance()` | Exports less imports. |
| 1206 | 1 | `public double currentAccount()` | ...less what was paid to foreign lenders. |
| 1208 | 1 | `public double getFinancialIn()` |  |
| 1209 | 1 | `public double getFinancialOut()` |  |
| 1210 | 1 | `public double financialAccount()` |  |
| 1213 | 1 | `public double balance()` | The month's change in the city's foreign position. |
| 1219 | 1 | `public double getReserves()` | What the treasury holds in foreign money, and could spend today - in local money, at today's rate. |
| 1222 | 1 | `public double getCumulativeBalance()` | Every month's balance of payments since founding, added up. |
| 1232 | 1 | `public boolean inDeficit()` | Whether the city owes the world more than it holds there. |
| 1235 | 1 | `public double getForgiven()` | Claims the world has written off, cumulatively. |
| 1244 | 5 | `public void forgiveDebt(double localAmount)` | Debt the city refused to pay, and its creditors will not see again. |
| 1253 | 1 | `public double getRepudiated()` | What the city has walked away from, since founding. |

### BUYING AND SELLING THE RESERVE (lines 1255-1333)

| line | len | member | says |
|---:|---:|---|---|
| 1273 | 4 | `public void startMonth()` | Clears the month's intervention. |
| 1291 | 6 | `public void buyReserves(double localAmount)` | Local currency spent buying foreign money. |
| 1321 | 9 | `public double sellReserves(double localAmount)` | Foreign money sold for local currency. |
| 1332 | 1 | `public double sellableReserves()` | The most the treasury could sell right now, in local money at today's rate. |

### THE LAND OFFICE IS PAID IN DOLLARS (0.7.6) (lines 1334-1449)

| line | len | member | says |
|---:|---:|---|---|
| 1397 | 7 | `public double buyAndSpendDollarsForLand(double usd)` | Buys exactly these dollars at today's rate and pays them to the land's seller, in one movement: the vault is untouched, and nothing a reserve purchase would leave behind is left (see THE LAND OFFICE IS PAID IN DOLLARS). |
| 1412 | 13 | `public double spendReservesOnLand(double usd)` | Pays the land's seller out of the vault: at most what it holds, and everything it holds empties it exactly. |
| 1427 | 7 | `public void strikeLandMonth()` | Strikes the land's month where the government's books strike its local cost. |
| 1436 | 1 | `public double getLandUsdThisMonth()` | Dollars paid for land in the month last struck, both ways. |
| 1438 | 1 | `public double getLandUsdFromVaultThisMonth()` | ...of which out of the vault. |
| 1440 | 1 | `public double getLandLocalThisMonth()` | What the month's land from abroad cost in local money on the day, both ways - the budget's land line less the buybacks. |
| 1442 | 1 | `public double getLandLocalFromVaultThisMonth()` | ...and the vault's part of it - the part of the budget's land line no cash paid. |
| 1444 | 1 | `public double getLandUsdLifetime()` | Dollars paid for land since founding, both ways. |
| 1446 | 1 | `public double getLandUsdFromVaultLifetime()` | ...of which out of the vault. |
| 1448 | 1 | `public double getLandUsdPending()` | Dollars paid for land since the last strike - the month in progress. |

### what the currency did to the vault (lines 1450-1521)

| line | len | member | says |
|---:|---:|---|---|
| 1474 | 5 | `public void revalueVault()` | Values the vault at today's rate. |
| 1481 | 1 | `public double getLastVaultRevaluation()` | What the currency did to the vault this month, in local money. |
| 1483 | 1 | `public double getBoughtThisMonth()` |  |
| 1484 | 1 | `public double getSoldThisMonth()` |  |
| 1486 | 1 | `public double getLifetimeExports()` |  |
| 1487 | 1 | `public double getLifetimeImports()` |  |
| 1488 | 1 | `public double getLifetimeInterest()` |  |
| 1489 | 1 | `public double getLifetimeFinancial()` |  |
| 1501 | 3 | `public double balanceFromFlows()` | The cumulative balance, rebuilt from the flows that made it. |
| 1515 | 1 | `public double getLifetimeIntervention()` |  |
| 1518 | 1 | `public double valuationChange()` | This month's part of that. |

### WHAT THE TRADE TAB READS (0.7.35) (lines 1522-1642)

| line | len | member | says |
|---:|---:|---|---|
| 1553 | 1 | `public boolean isMonthCounted()` | True once the month's flows are the month's - a month taken since the founding, or a load of a save that carried them - and not nothing. |
| 1556 | 1 | `public double getLifetimeTradeBalance()` | Exports less imports since founding: the record on trade alone, without the income or the capital. |
| 1571 | 5 | `public static int parityLevel(double deviation)` | The verdict on a deviation from parity (deviationFromParity()), either side: 0 near, 1 at PARITY_WATCH or more, 2 at PARITY_FAR or more. |
| 1586 | 4 | `public static int coverLevel(double months)` | The verdict on months of cover: 2 under THIN_COVER, 1 under COMFORTABLE_COVER, 0 at or over it. |
| 1597 | 6 | `public double coverWith(double localChange)` | The cover the vault would give with this much local money's worth of dollars bought into it at today's rate (negative: sold out of it) - the Exchange card's "what it would do". |
| 1605 | 3 | `public double toCover(double months)` | What it would cost in local money, at today's rate, to bring the vault up to this many months of the trailing import bill; nothing when it is there already. |
| 1623 | 3 | `public double previewPush()` | The next reprice, in its three parts, as fractions of today's rate: the month's push - previewPressure() at DRIFT_SPEED - the anchored drift on the rate the push leaves, and the pull back to parity, struck on the rate... |
| 1628 | 4 | `public double previewDrift()` | ...the anchored drift, on the rate the push leaves (0.7.45). |
| 1634 | 5 | `public double previewPull()` | ...the pull back to parity, on the rate the push and the drift leave. |
| 1641 | 1 | `public double previewMove()` | ...and the three together: the next month's move, a fraction of today's rate. |

### carrying (lines 1643-1986)

| line | len | member | says |
|---:|---:|---|---|
| 1655 | 128 | `public double[] toSaveArray()` | A STOCK, so it is saved. |
| 1784 | 87 | `public void restore(double[] saved)` |  |
| 1872 | 44 | `public void reset()` |  |
| 1940 | 45 | `public void redenominate(double scale)` | Every figure in the city's foreign accounts, in the new unit. |

