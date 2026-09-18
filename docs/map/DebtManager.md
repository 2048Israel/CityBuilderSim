# DebtManager.java - 1,018 lines · 80 methods · 20 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-09-18 by CodeMap; line numbers are as of that run.

> (no class header - the file explains itself in its section banners)

**Uses:** [Debt](Debt.md) (23), [ShortTermTBill](ShortTermTBill.md) (2), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [LongTermBond](LongTermBond.md) (1), [Game](Game.md) (1)

**Used by (18):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuildScreen](BuildScreen.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CreditCheck](CreditCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [PolicyScreen](PolicyScreen.md), [SimulationEngine](SimulationEngine.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 16 | THE POLICY RATE |
| 259 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 316 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 813 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 43 | `DebtManager.MIN_POLICY_RATE` | `.0` |  |
| 44 | `DebtManager.MAX_POLICY_RATE` | `.25` |  |
| 75 | `DebtManager.CITY_DISCOUNT` | `.02` | WHAT THE CITY'S OWN PAPER IS QUOTED UNDER THE POLICY RATE - and it is the one number in this file that does not describe anything real. |
| 78 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 81 | `DebtManager.INFLATION_TARGET` | `.02` | What the city is trying to hold inflation at. |
| 91 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 169 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 203 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 206 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 209 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 345 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 348 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 351 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 358 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 361 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 364 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 367 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 370 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 373 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 999 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 47 | `private double baseRate` | What the city's central bank charges. |
| 127 | `private double currentRate` |  |
| 128 | `private double GDP` |  |
| 140 | `private double monthlyTaxRevenue` | The month's tax take. |
| 152 | `private double overdraft` | How far the city is overdrawn. |
| 211 | `private List<Debt> debts` |  |
| 263 | `private double exchangeRate` |  |
| 375 | `private double monthlyExports` |  |
| 376 | `private double importCover` |  |
| 377 | `private double defaultScar` |  |
| 378 | `private int monthsSinceForeignDefault` |  |
| 618 | `private double bankPremium` | What the bank's strain is adding to every rate in the city. |
| 630 | `private double costOfFunds` | What the bank pays for the money it lends the city. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 14 | 1005 | **type** `public class DebtManager` |  |

### THE POLICY RATE (lines 16-258)

| line | len | member | says |
|---:|---:|---|---|
| 49 | 1 | `public double getPolicyRate()` |  |
| 51 | 3 | `public void setPolicyRate(double rate)` |  |
| 107 | 4 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation. |
| 113 | 14 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 213 | 4 | `public DebtManager()` |  |
| 218 | 3 | `public void addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 222 | 3 | `public void addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 226 | 3 | `public void addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 230 | 4 | `public void addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 235 | 3 | `public void addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 239 | 4 | `public void addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 254 | 4 | `private void book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 259-315)

| line | len | member | says |
|---:|---:|---|---|
| 275 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 281 | 1 | `public double getExchangeRate()` |  |
| 284 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 291 | 5 | `public double getForeignPrincipalUsd()` | ...and in the dollars it is actually owed in, which do not move. |
| 298 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 305 | 5 | `public double getForeignCouponUsd()` | Next month's USD coupon bill, in dollars. |
| 311 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 316-812)

| line | len | member | says |
|---:|---:|---|---|
| 386 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 391 | 1 | `public double getMonthlyExports()` |  |
| 392 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 399 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 411 | 1 | `public double getImportCover()` |  |
| 414 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 427 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 454 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 458 | 6 | `public double serviceStressAt(double service)` |  |
| 466 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 477 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 481 | 6 | `public double countryPremiumAt(double owed)` |  |
| 489 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond. |
| 498 | 4 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 510 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 515 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 538 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 544 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 550 | 1 | `public double getDefaultScar()` |  |
| 551 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 554 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 558 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 564 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 606 | 3 | `public double getRate()` | getters |
| 619 | 1 | `public void setBankPremium(double premium)` |  |
| 620 | 1 | `public double getBankPremium()` |  |
| 631 | 1 | `public void setCostOfFunds(double rate)` |  |
| 632 | 1 | `public double getCostOfFunds()` |  |
| 649 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 658 | 3 | `public void setGDP(double GDP)` | setters |
| 663 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 673 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 677 | 1 | `public double getOverdraft()` |  |
| 679 | 3 | `public List<Debt> getDebt()` |  |
| 683 | 16 | `public void processAllDebts(Game game)` |  |
| 715 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |
| 723 | 3 | `public double getPricedDebt()` |  |
| 738 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 743 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back at today's rate. |
| 763 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 806 | 6 | `private double priceAt(double debt)` | funds - and the second half has to be here rather than at the one call site that sets the standing rate. |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 813-1018)

| line | len | member | says |
|---:|---:|---|---|
| 836 | 7 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 845 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate less the city's own spread. |
| 848 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 851 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 854 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 858 | 3 | `public double revenueStress()` |  |
| 863 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 866 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 869 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 872 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 875 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 903 | 4 | `public double floorRate()` | What a spotless city pays: the policy rate less CITY_DISCOUNT - see its note - but never less than the money costs the bank that lends it. |
| 909 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 920 | 14 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 955 | 12 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 969 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 985 | 4 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |
| 990 | 3 | `public void clearDebts()` |  |
| 994 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 1001 | 4 | `static { ... }` |  |
| 1007 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

