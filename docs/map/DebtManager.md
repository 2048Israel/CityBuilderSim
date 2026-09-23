# DebtManager.java - 1,471 lines · 110 methods · 25 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-09-22 by CodeMap; line numbers are as of that run.

> The city's borrowing: every bond, note and dollar bond the treasury owes,
> the market that prices the next one, and the policy rate every price of
> money in the city is built on - the player's dial, or the rule's with the
> autopilot on (0.7.0).

**Uses:** [Debt](Debt.md) (42), [ShortTermTBill](ShortTermTBill.md) (3), [CentralBank](CentralBank.md) (2), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [LongTermBond](LongTermBond.md) (1), [Game](Game.md) (1)

**Used by (22):** [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SimulationEngine](SimulationEngine.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 20 | THE POLICY RATE |
| 72 | · THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). |
| 336 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 393 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 1009 | THE CURVE (0.7.1) |
| 1152 | · who holds it (0.7.1) |
| 1246 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 59 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 61 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 119 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 122 | `DebtManager.INFLATION_TARGET` | `.02` | What the city is trying to hold inflation at. |
| 132 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 244 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 279 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 282 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 285 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 422 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 425 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 428 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 435 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 438 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 441 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 444 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 447 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 450 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1051 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1054 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1057 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1060 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1063 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1066 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TER...` | The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. |
| 1452 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 64 | `private double baseRate` | What the city's central bank charges. |
| 103 | `private boolean autopilot` | THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the player. |
| 200 | `private double currentRate` |  |
| 201 | `private double GDP` |  |
| 213 | `private double monthlyTaxRevenue` | The month's tax take. |
| 225 | `private double overdraft` | How far the city is overdrawn. |
| 287 | `private List<Debt> debts` |  |
| 340 | `private double exchangeRate` |  |
| 452 | `private double monthlyExports` |  |
| 453 | `private double importCover` |  |
| 454 | `private double defaultScar` |  |
| 455 | `private int monthsSinceForeignDefault` |  |
| 761 | `private double bankPremium` | What the bank's strain is adding to every rate in the city. |
| 784 | `private double costOfFunds` | What the bank pays for the money it lends the city. |
| 840 | `private double advances` | What the treasury owes its central bank in advances (0.7.0), pushed in with the overdraft. |
| 1242 | `private double accretedForBank` | This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 1454 | **type** `public class DebtManager` | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of money in the city is built on - the player's dial, or the rule's with t... |

### THE POLICY RATE (lines 20-71)

| line | len | member | says |
|---:|---:|---|---|
| 66 | 1 | `public double getPolicyRate()` |  |
| 68 | 3 | `public void setPolicyRate(double rate)` |  |

### THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). (lines 72-335)

| line | len | member | says |
|---:|---:|---|---|
| 105 | 1 | `public boolean isAutopilot()` |  |
| 106 | 1 | `public void setAutopilot(boolean on)` |  |
| 113 | 4 | `public void takeTheDial(double rate)` | The player moves the dial by hand, which takes it back from the rule. |
| 151 | 3 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation, held inside the dial's bounds - ruleRate() is the rule before them. |
| 168 | 3 | `public double ruleRate(double inflation)` | What the rule itself says, before the dial's bounds (2026-09-21). |
| 177 | 23 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 289 | 4 | `public DebtManager()` |  |
| 294 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 298 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 302 | 3 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 306 | 4 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 311 | 3 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 315 | 4 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 330 | 5 | `private Debt book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 336-392)

| line | len | member | says |
|---:|---:|---|---|
| 352 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 358 | 1 | `public double getExchangeRate()` |  |
| 361 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 368 | 5 | `public double getForeignPrincipalUsd()` | ...and in the dollars it is actually owed in, which do not move. |
| 375 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 382 | 5 | `public double getForeignCouponUsd()` | Next month's USD coupon bill, in dollars. |
| 388 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 393-1008)

| line | len | member | says |
|---:|---:|---|---|
| 463 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 468 | 1 | `public double getMonthlyExports()` |  |
| 469 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 476 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 488 | 1 | `public double getImportCover()` |  |
| 491 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 504 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 532 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 536 | 6 | `public double serviceStressAt(double service)` |  |
| 544 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 555 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 559 | 6 | `public double countryPremiumAt(double owed)` |  |
| 567 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). |
| 589 | 3 | `public double foreignCurveRate(int months)` | THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar paper of this many months - the foreign rate, plus the SAME term premium table the city's own curve carries (termPremium()). |
| 598 | 4 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 604 | 3 | `public double quoteForeignRate(double extraUsd, int months)` | ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. |
| 628 | 11 | `public double quoteForeignRate(Debt proposed, int months)` | ...and priced with the proposed paper ON THE BOOKS, its own next year of service included (0.7.2): the rate the world's curve will read for this paper, at this maturity, the moment it is booked. |
| 647 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 652 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 675 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 681 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 687 | 1 | `public double getDefaultScar()` |  |
| 688 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 691 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 695 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 701 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 749 | 3 | `public double getRate()` | THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term premium - which is what every screen means by "the city's rate", what the bank's strain and the advisor read, and what the households' lender pric... |
| 762 | 1 | `public void setBankPremium(double premium)` |  |
| 763 | 1 | `public double getBankPremium()` |  |
| 774 | 1 | `public double getRateBeforeStrain()` | The city's rate without the bank's strain in it (0.7.2): what the hot money compares with the world's (CapitalFlows, MAX_SPREAD). |
| 785 | 1 | `public void setCostOfFunds(double rate)` |  |
| 786 | 1 | `public double getCostOfFunds()` |  |
| 803 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 812 | 3 | `public void setGDP(double GDP)` | setters |
| 817 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 827 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 831 | 1 | `public double getOverdraft()` |  |
| 842 | 1 | `public void setAdvances(double owed)` |  |
| 843 | 1 | `public double getAdvances()` |  |
| 845 | 3 | `public List<Debt> getDebt()` |  |
| 849 | 31 | `public void processAllDebts(Game game)` |  |
| 890 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |
| 905 | 3 | `public double getPricedDebt()` | Everything the city owes, including what it is overdrawn and what it owes the central bank in advances. |
| 920 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 925 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back today: each at the curve's rate for the months it has left (0.7.1). |
| 945 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 987 | 3 | `private double priceAt(double debt)` | The curve itself: a floor, plus up to ten points from each measure. |
| 1001 | 7 | `private double priceAt(double debt, int months)` | ...at a maturity (0.7.1): the same credit judgement, clamped the same way, plus the term premium for that many months less what the central bank's holdings compress of it, and the bank's premium outside it all. |

### THE CURVE (0.7.1) (lines 1009-1151)

| line | len | member | says |
|---:|---:|---|---|
| 1077 | 8 | `public static double termPremium(int months)` | What a lender adds for tying money up this many months, before the central bank compresses any of it. |
| 1093 | 7 | `public double compression(int months)` | What the central bank's holdings take off the premium at this many months: the premium, times the share of the city's term paper it actually holds (not its target) over CentralBank.MAX_QE_SHARE, times CentralBank.QE_C... |
| 1102 | 4 | `private double termShape(int months)` | The premium less the compression: the curve's shape over the short end. |
| 1112 | 3 | `public double curveRate(int months)` | THE CURVE: what the city's paper of this many months is worth to a lender today - the standing rate at that maturity, on what the city owes now. |
| 1123 | 5 | `public double marketValue(Debt paper)` | What this paper would fetch today: the present value of what it still owes at the curve's rate for the months it has left - the city's curve for its own paper, the world's (foreignCurveRate()) for a dollar bond since ... |
| 1130 | 3 | `public static boolean isTermPaper(Debt paper)` | True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. |
| 1135 | 5 | `public double termPrincipal()` | The city's own term paper outstanding, at face: the base of the holdings dial. |
| 1142 | 9 | `public double centralBankShareOfTerm()` | The share of it the central bank holds - the compression's measure. |

### who holds it (0.7.1) (lines 1152-1245)

| line | len | member | says |
|---:|---:|---|---|
| 1155 | 5 | `public double householdPrincipal()` | What the city's households hold of its own paper, at face. |
| 1162 | 5 | `public double centralBankPrincipal()` | ...the central bank, at face. |
| 1169 | 5 | `public double bankPrincipal()` | ...and the commercial bank: the domestic principal less the other two. |
| 1182 | 5 | `public double bankBook()` | ...of which the paper the bank has PAID for: what its book carries. |
| 1189 | 13 | `public double[] bookValues()` | Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank. |
| 1208 | 4 | `public double householdBookRatio()` | The households' book at market over its face: ONE RATIO A MONTH, which is what their paper counts for in their net worth and what the desk pays them for it. |
| 1214 | 10 | `public double householdBookYield()` | What the households' paper yields at today's curve, weighted by what they hold of each piece; the short rate when they hold none. |
| 1232 | 8 | `public double bankUnearnedDiscount()` | The discount the bank has not yet earned on what it holds: every piece's unaccreted remainder, in the share of its principal the bank holds. |
| 1244 | 1 | `public double getAccretedForBank()` |  |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 1246-1471)

| line | len | member | says |
|---:|---:|---|---|
| 1269 | 7 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 1278 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate, or the bank's cost of funds if that is higher. |
| 1281 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 1284 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 1287 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 1291 | 3 | `public double revenueStress()` |  |
| 1296 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 1299 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 1302 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 1305 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 1308 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 1337 | 4 | `public double floorRate()` | What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR IS REAL NOW - but never less than the money costs the bank that lends it. |
| 1343 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 1354 | 15 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 1390 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 1399 | 12 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months)` | ...at a maturity (0.7.1): the same fixed point on the curve's rate for that many months. |
| 1413 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 1418 | 3 | `public double quoteRate(double requested, int months)` | ...at a maturity. |
| 1437 | 5 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |
| 1443 | 3 | `public void clearDebts()` |  |
| 1447 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 1454 | 4 | `static { ... }` |  |
| 1460 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

