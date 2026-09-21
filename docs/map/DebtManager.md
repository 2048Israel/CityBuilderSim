# DebtManager.java - 1,046 lines · 81 methods · 20 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-09-21 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Debt](Debt.md) (23), [ShortTermTBill](ShortTermTBill.md) (2), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [LongTermBond](LongTermBond.md) (1), [Game](Game.md) (1)

**Used by (18):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CreditCheck](CreditCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [PolicyScreen](PolicyScreen.md), [SimulationEngine](SimulationEngine.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 16 | THE POLICY RATE |
| 287 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 344 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 841 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `DebtManager.MIN_POLICY_RATE` | `.0` |  |
| 44 | `DebtManager.MAX_POLICY_RATE` | `.25` |  |
| 75 | `DebtManager.CITY_DISCOUNT` | `.02` | WHAT THE CITY'S OWN PAPER IS QUOTED UNDER THE POLICY RATE - and it is the one number in this file that does not describe anything real. |
| 78 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 81 | `DebtManager.INFLATION_TARGET` | `.02` | What the city is trying to hold inflation at. |
| 91 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 197 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 231 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 234 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 237 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 373 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 376 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 379 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 386 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 389 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 392 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 395 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 398 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 401 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1027 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `private double baseRate` | What the city's central bank charges. |
| 155 | `private double currentRate` |  |
| 156 | `private double GDP` |  |
| 168 | `private double monthlyTaxRevenue` | The month's tax take. |
| 180 | `private double overdraft` | How far the city is overdrawn. |
| 239 | `private List<Debt> debts` |  |
| 291 | `private double exchangeRate` |  |
| 403 | `private double monthlyExports` |  |
| 404 | `private double importCover` |  |
| 405 | `private double defaultScar` |  |
| 406 | `private int monthsSinceForeignDefault` |  |
| 647 | `private double bankPremium` | What the bank's strain is adding to every rate in the city. |
| 659 | `private double costOfFunds` | What the bank pays for the money it lends the city. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 14 | 1033 | **type** `public class DebtManager` |  |

### THE POLICY RATE (lines 16-286)

| line | len | member | says |
|---:|---:|---|---|
| 49 | 1 | `public double getPolicyRate()` |  |
| 51 | 3 | `public void setPolicyRate(double rate)` |  |
| 108 | 3 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation, held inside the dial's bounds - ruleRate() is the rule before them. |
| 123 | 3 | `public double ruleRate(double inflation)` | What the rule itself says, before the dial's bounds (2026-09-21). |
| 132 | 23 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 241 | 4 | `public DebtManager()` |  |
| 246 | 3 | `public void addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 250 | 3 | `public void addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 254 | 3 | `public void addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 258 | 4 | `public void addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 263 | 3 | `public void addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 267 | 4 | `public void addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 282 | 4 | `private void book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 287-343)

| line | len | member | says |
|---:|---:|---|---|
| 303 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 309 | 1 | `public double getExchangeRate()` |  |
| 312 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 319 | 5 | `public double getForeignPrincipalUsd()` | ...and in the dollars it is actually owed in, which do not move. |
| 326 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 333 | 5 | `public double getForeignCouponUsd()` | Next month's USD coupon bill, in dollars. |
| 339 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 344-840)

| line | len | member | says |
|---:|---:|---|---|
| 414 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 419 | 1 | `public double getMonthlyExports()` |  |
| 420 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 427 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 439 | 1 | `public double getImportCover()` |  |
| 442 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 455 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 483 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 487 | 6 | `public double serviceStressAt(double service)` |  |
| 495 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 506 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 510 | 6 | `public double countryPremiumAt(double owed)` |  |
| 518 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond. |
| 527 | 4 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 539 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 544 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 567 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 573 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 579 | 1 | `public double getDefaultScar()` |  |
| 580 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 583 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 587 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 593 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 635 | 3 | `public double getRate()` | getters |
| 648 | 1 | `public void setBankPremium(double premium)` |  |
| 649 | 1 | `public double getBankPremium()` |  |
| 660 | 1 | `public void setCostOfFunds(double rate)` |  |
| 661 | 1 | `public double getCostOfFunds()` |  |
| 678 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 687 | 3 | `public void setGDP(double GDP)` | setters |
| 692 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 702 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 706 | 1 | `public double getOverdraft()` |  |
| 708 | 3 | `public List<Debt> getDebt()` |  |
| 712 | 16 | `public void processAllDebts(Game game)` |  |
| 738 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |
| 752 | 3 | `public double getPricedDebt()` | Everything the city owes, including what it is overdrawn. |
| 767 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 772 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back at today's rate. |
| 792 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 834 | 6 | `private double priceAt(double debt)` | The curve itself: a floor, plus up to ten points from each measure. |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 841-1046)

| line | len | member | says |
|---:|---:|---|---|
| 864 | 7 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 873 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate less the city's own spread. |
| 876 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 879 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 882 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 886 | 3 | `public double revenueStress()` |  |
| 891 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 894 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 897 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 900 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 903 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 931 | 4 | `public double floorRate()` | What a spotless city pays: the policy rate less CITY_DISCOUNT - see its note - but never less than the money costs the bank that lends it. |
| 937 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 948 | 14 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 983 | 12 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 997 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 1013 | 4 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |
| 1018 | 3 | `public void clearDebts()` |  |
| 1022 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 1029 | 4 | `static { ... }` |  |
| 1035 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

