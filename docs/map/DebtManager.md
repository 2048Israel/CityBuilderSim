# DebtManager.java - 1,816 lines · 134 methods · 29 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-10-02 by CodeMap; line numbers are as of that run.

> The city's borrowing: every bond, note and dollar bond the treasury owes,
> the market that prices the next one, and the policy rate every price of
> money in the city is built on - the player's dial, or the rule's with the
> autopilot on (0.7.0).

**Uses:** [Debt](Debt.md) (50), [DecisionLog](DecisionLog.md) (9), [CityCalendar](CityCalendar.md) (5), [Ladder](Ladder.md) (4), [CentralBank](CentralBank.md) (4), [ShortTermTBill](ShortTermTBill.md) (3), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [LongTermBond](LongTermBond.md) (1), [Game](Game.md) (1)

**Used by (26):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Pieces](Pieces.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SimulationEngine](SimulationEngine.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 30 | THE POLICY RATE |
| 83 | · THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). |
| 409 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 466 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 982 | · WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) |
| 1254 | THE CURVE (0.7.1) |
| 1447 | · who holds it (0.7.1) |
| 1541 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 72 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 141 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way. |
| 144 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 147 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 150 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 193 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target. |
| 317 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 352 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 355 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 358 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 495 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 498 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 501 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 508 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 511 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 514 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 517 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 520 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 523 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1039 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1042 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 1297 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1300 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1303 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1306 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1309 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1312 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TER...` | The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. |
| 1797 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private transient DecisionLog decisions` | Where the player's change to the policy rate, the rule or the inflation target is written (DecisionLog, 0.7.23); null for one no city holds. |
| 75 | `private double baseRate` | What the city's central bank charges. |
| 114 | `private boolean autopilot` | THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the player. |
| 163 | `private double inflationTarget` | THE TARGET IS A DIAL (0.7.4). |
| 273 | `private double currentRate` |  |
| 274 | `private double GDP` |  |
| 286 | `private double monthlyTaxRevenue` | The month's tax take. |
| 298 | `private double overdraft` | How far the city is overdrawn. |
| 360 | `private List<Debt> debts` |  |
| 413 | `private double exchangeRate` |  |
| 525 | `private double monthlyExports` |  |
| 526 | `private double importCover` |  |
| 527 | `private double defaultScar` |  |
| 528 | `private int monthsSinceForeignDefault` |  |
| 844 | `private double costOfFunds` | What the bank pays for the money it lends the city. |
| 900 | `private double advances` | What the treasury owes its central bank in advances (0.7.0), pushed in with the overdraft. |
| 1537 | `private double accretedForBank` | This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 1799 | **type** `public class DebtManager` | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of money in the city is built on - the player's dial, or the rule's with t... |
| 24 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 26 | 3 | `private void decided(String kind, String label)` |  |

### THE POLICY RATE (lines 30-82)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1 | `public double getPolicyRate()` |  |
| 79 | 3 | `public void setPolicyRate(double rate)` |  |

### THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). (lines 83-408)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1 | `public boolean isAutopilot()` |  |
| 117 | 7 | `public void setAutopilot(boolean on)` |  |
| 130 | 9 | `public void takeTheDial(double rate)` | The player moves the dial by hand, which takes it back from the rule. |
| 166 | 1 | `public double getInflationTarget()` | What the rule aims inflation at, a fraction a year. |
| 169 | 8 | `public void setInflationTarget(double target)` | Sets the target, held between MIN_INFLATION_TARGET and MAX_INFLATION_TARGET; not a number leaves it where it was. |
| 179 | 5 | `public static String targetWords(double target)` | A target as the screens write it: "2%", or "2.5%" on a half point. |
| 212 | 3 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation, held inside the dial's bounds - ruleRate() is the rule before them. |
| 230 | 3 | `public double ruleRate(double inflation)` | What the rule itself says, before the dial's bounds (2026-09-21). |
| 240 | 3 | `public double ruleRate(double inflation, double target)` | ...and what it would say at a target of the caller's rather than the dial's (0.7.4): the monetary page's sentence, "at 0% it would set 10.5%", struck from the rule rather than restated by the screen. |
| 249 | 24 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 362 | 4 | `public DebtManager()` |  |
| 367 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 371 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 375 | 3 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 379 | 4 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 384 | 3 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 388 | 4 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 403 | 5 | `private Debt book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 409-465)

| line | len | member | says |
|---:|---:|---|---|
| 425 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 431 | 1 | `public double getExchangeRate()` |  |
| 434 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 441 | 5 | `public double getForeignPrincipalUsd()` | ...and in the dollars it is actually owed in, which do not move. |
| 448 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 455 | 5 | `public double getForeignCouponUsd()` | Next month's USD coupon bill, in dollars. |
| 461 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 466-981)

| line | len | member | says |
|---:|---:|---|---|
| 536 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 541 | 1 | `public double getMonthlyExports()` |  |
| 542 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 549 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 561 | 1 | `public double getImportCover()` |  |
| 564 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 577 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 605 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 609 | 6 | `public double serviceStressAt(double service)` |  |
| 617 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 628 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 632 | 6 | `public double countryPremiumAt(double owed)` |  |
| 640 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). |
| 662 | 3 | `public double foreignCurveRate(int months)` | THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar paper of this many months - the foreign rate, plus the SAME term premium table the city's own curve carries (termPremium()). |
| 671 | 4 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 677 | 3 | `public double quoteForeignRate(double extraUsd, int months)` | ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. |
| 701 | 11 | `public double quoteForeignRate(Debt proposed, int months)` | ...and priced with the proposed paper ON THE BOOKS, its own next year of service included (0.7.2): the rate the world's curve will read for this paper, at this maturity, the moment it is booked. |
| 720 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 725 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 748 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 754 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 760 | 1 | `public double getDefaultScar()` |  |
| 761 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 764 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 768 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 774 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 823 | 3 | `public double getRate()` | THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term premium - which is what every screen means by "the city's rate" and what the advisor reads. |
| 845 | 1 | `public void setCostOfFunds(double rate)` |  |
| 846 | 1 | `public double getCostOfFunds()` |  |
| 863 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 872 | 3 | `public void setGDP(double GDP)` | setters |
| 877 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 887 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 891 | 1 | `public double getOverdraft()` |  |
| 902 | 1 | `public void setAdvances(double owed)` |  |
| 903 | 1 | `public double getAdvances()` |  |
| 915 | 3 | `public double[] marketToSave()` | THE MARKET'S LAST STRIKE, for the save (0.7.14): the four inputs it was last handed - a month's output, a month's tax, the overdraft, the advances - and the rate it struck on them. |
| 920 | 8 | `public void restoreMarket(double[] saved)` | ...and back, over the load path's own strike; an older save has none and keeps that strike. |
| 929 | 3 | `public List<Debt> getDebt()` |  |
| 933 | 31 | `public void processAllDebts(Game game)` |  |
| 974 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |

### WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) (lines 982-1253)

| line | len | member | says |
|---:|---:|---|---|
| 998 | 5 | `public double getMonthlyCoupon()` | What the city is charged in coupon a month, every piece together, in local money: the bonds' own getMonthlyInterestExpense(), summed - the Finances tab's COUPON. |
| 1005 | 5 | `public double getPrincipalOf(String type)` | Principal outstanding of one kind of paper, by Debt.getType() - "NOTE", "SERIAL" or "TERM" - at home and abroad, in local money. |
| 1017 | 8 | `public double dueWithin(int months)` | Everything the city's paper asks of it over the next `months` months, coupons and principal together, in local money - the first `months` of every piece's remainingCashFlows(), the ladder's own read: the Debt service ... |
| 1027 | 5 | `public double valuationRate(Debt paper)` | The rate a piece is valued at today: the city's curve for its own paper, the world's for a dollar piece, at the months it has left - marketValue()'s, so a screen quoting its yield or its price of par reads the price a... |
| 1034 | 3 | `public double underFace(Debt paper)` | What buying a piece back today would save against its face: its principal less marketValue() - below nothing when it costs a premium to retire (the Finances tab's book, which subtracted them itself). |
| 1045 | 3 | `public static int ladderKind(String type)` | A kind's place in LADDER_KINDS: a note 0, a serial 1, anything else - a term loan - 2. |
| 1059 | 9 | **type** `public record Rung(int fromYear, int toYear, int fromMonth, int toMonth, double[] byKind, double[] abroadBy...` | One rung of the ladder: a calendar year of payments, from its first month on the ladder to its last - a whole year, but the first, which starts next month, and the one the last payment falls in, which ends with it; bo... |
| 1062 | 1 | `public double owed()` _(in DebtManager.Rung)_ | What the city's paper asks in it. |
| 1064 | 1 | `public double abroad()` _(in DebtManager.Rung)_ | ...of which in dollars. |
| 1066 | 1 | `public double total()` _(in DebtManager.Rung)_ | ...and with the proposed issue's. |
| 1075 | 4 | **type** `public record Ladder(List<Rung> years, Rung later, int heaviest, double owed, double proposed)` | The ladder: LADDER_YEARS calendar years from next month's, and what falls after them as one "later" rung (null when nothing does); which of the years is the heaviest (-1 with nothing owed in them) - never "later", whi... |
| 1077 | 1 | `public double total()` _(in DebtManager.Ladder)_ | What is owed and what the proposed issue would ask, together. |
| 1081 | 1 | `public Ladder ladder(int month)` | The ladder as the city owes it, struck in `month`. |
| 1097 | 50 | `public Ladder ladder(int month, double[] extra)` | WHEN IT FALLS DUE, BY THE CALENDAR YEAR IT IS PAID IN (0.7.32, the Finances spec's D4). |
| 1155 | 3 | `public double getPricedDebt()` | Everything the city owes, including what it is overdrawn and what it owes the central bank in advances. |
| 1170 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 1175 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back today: each at the curve's rate for the months it has left (0.7.1). |
| 1195 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 1231 | 3 | `private double priceAt(double debt)` | The curve itself: a floor, plus up to ten points from each measure. |
| 1246 | 7 | `private double priceAt(double debt, int months)` | ...at a maturity (0.7.1): the same credit judgement, clamped the same way, plus the term premium for that many months less what the central bank's holdings compress of it (and the bank's strain premium outside it all,... |

### THE CURVE (0.7.1) (lines 1254-1446)

| line | len | member | says |
|---:|---:|---|---|
| 1323 | 8 | `public static double termPremium(int months)` | What a lender adds for tying money up this many months, before the central bank compresses any of it. |
| 1345 | 7 | `public double compression(int months)` | What the central bank's holdings take off the premium at this many months: the premium, times the share of the city's term paper it actually holds (not its target) over CentralBank.FULL_COMPRESSION_SHARE, times Centra... |
| 1360 | 5 | `public double compressionAt(double share, int months)` | ...at a share of the term paper of the caller's (0.7.36): what holding `share` would take off the premium at this many months, compression()'s rule with the share given - the Policy tab's holdings dial, whose thirty-y... |
| 1372 | 3 | `public double curveRateAtShare(int months, double share)` | The curve at a maturity with the central bank holding `share` of the term paper (0.7.36): curveRate()'s credit judgement and premium, less compressionAt() that share - the thirty-year rate "once the book has moved" to... |
| 1377 | 4 | `private double termShape(int months)` | The premium less the compression: the curve's shape over the short end. |
| 1387 | 3 | `public double curveRate(int months)` | THE CURVE: what the city's paper of this many months is worth to a lender today - the standing rate at that maturity, on what the city owes now. |
| 1398 | 5 | `public double marketValue(Debt paper)` | What this paper would fetch today: the present value of what it still owes at the curve's rate for the months it has left - the city's curve for its own paper, the world's (foreignCurveRate()) for a dollar bond since ... |
| 1405 | 3 | `public static boolean isTermPaper(Debt paper)` | True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. |
| 1410 | 5 | `public double termPrincipal()` | The city's own term paper outstanding, at face: the base of the holdings dial. |
| 1417 | 9 | `public double centralBankShareOfTerm()` | The share of it the central bank holds - the compression's measure. |
| 1437 | 9 | `public double centralBankShareOfPaper()` | The central bank's share of ALL the city's own paper outstanding, at face, every maturity - the notes it never buys in the denominator - the floor's measure (0.7.15, floorRate()). |

### who holds it (0.7.1) (lines 1447-1540)

| line | len | member | says |
|---:|---:|---|---|
| 1450 | 5 | `public double householdPrincipal()` | What the city's households hold of its own paper, at face. |
| 1457 | 5 | `public double centralBankPrincipal()` | ...the central bank, at face. |
| 1464 | 5 | `public double bankPrincipal()` | ...and the commercial bank: the domestic principal less the other two. |
| 1477 | 5 | `public double bankBook()` | ...of which the paper the bank has PAID for: what its book carries. |
| 1484 | 13 | `public double[] bookValues()` | Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank. |
| 1503 | 4 | `public double householdBookRatio()` | The households' book at market over its face: ONE RATIO A MONTH, which is what their paper counts for in their net worth and what the desk pays them for it. |
| 1509 | 10 | `public double householdBookYield()` | What the households' paper yields at today's curve, weighted by what they hold of each piece; the short rate when they hold none. |
| 1527 | 8 | `public double bankUnearnedDiscount()` | The discount the bank has not yet earned on what it holds: every piece's unaccreted remainder, in the share of its principal the bank holds. |
| 1539 | 1 | `public double getAccretedForBank()` |  |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 1541-1816)

| line | len | member | says |
|---:|---:|---|---|
| 1566 | 6 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 1579 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate, or the bank's cost of funds if that is higher - on the share of the city's paper the bank and the households hold, since 0.7.15; the central bank's share is priced at the pol... |
| 1582 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 1585 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 1588 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 1592 | 3 | `public double revenueStress()` |  |
| 1597 | 1 | `public double overTheWorld()` | What the city's own rate is over the world's (0.7.36): the short end against WORLD_BASE_RATE - the difference hot money follows. |
| 1600 | 1 | `public double overTheWorldAt(double policy)` | ...at a policy rate of the caller's: rateAtPolicy() against the world's. |
| 1603 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 1606 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 1609 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 1612 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 1615 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 1663 | 3 | `public double floorRate()` | What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR IS REAL NOW - but never less than the money costs the bank that lends it. |
| 1674 | 6 | `private double floorAt(double policy)` | ...at a policy rate of the caller's: the one definition floorRate() and rateAtPolicy() both read, so the Policy tab's "what moving the dial does" splits the floor as the market does. |
| 1688 | 3 | `public double bankFloorRate()` | The bank's floor, unsplit: the policy rate, or the bank's cost of funds plus Bank.MIN_MARGIN if that is higher - what the share of the city's paper the central bank does not hold is priced at, and the whole floor unti... |
| 1693 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 1704 | 10 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 1735 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 1744 | 12 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months)` | ...at a maturity (0.7.1): the same fixed point on the curve's rate for that many months. |
| 1758 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 1763 | 3 | `public double quoteRate(double requested, int months)` | ...at a maturity. |
| 1782 | 5 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |
| 1788 | 3 | `public void clearDebts()` |  |
| 1792 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 1799 | 4 | `static { ... }` |  |
| 1805 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

