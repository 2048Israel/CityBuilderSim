# DebtManager.java - 2,009 lines · 150 methods · 33 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-10-06 by CodeMap; line numbers are as of that run.

> The city's borrowing: every bond, note and dollar bond the treasury owes,
> the market that prices the next one, and the policy rate every price of
> money in the city is built on - the player's dial, or the rule's with the
> autopilot on (0.7.0).

**Uses:** [Debt](Debt.md) (51), [DecisionLog](DecisionLog.md) (10), [CityCalendar](CityCalendar.md) (5), [Ladder](Ladder.md) (4), [CentralBank](CentralBank.md) (4), [ShortTermTBill](ShortTermTBill.md) (3), [Expectations](Expectations.md) (2), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [LongTermBond](LongTermBond.md) (1), [Game](Game.md) (1)

**Used by (30):** [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [DataSave](DataSave.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [Expectations](Expectations.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Pieces](Pieces.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SimulationEngine](SimulationEngine.md), [TradeScreen](TradeScreen.md), [UserInterface](UserInterface.md)

## Sections

| line | section |
|---:|---|
| 30 | THE POLICY RATE |
| 83 | · THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). |
| 198 | · HOW STRICT (0.7.52) |
| 591 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 648 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 1164 | · WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) |
| 1447 | THE CURVE (0.7.1) |
| 1640 | · who holds it (0.7.1) |
| 1734 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |

## Enum constants

| line | constant | says |
|---:|---|---|
| 243 | `DebtManager.Strictness.VERY_LOOSE` |  |
| 244 | `DebtManager.Strictness.LOOSE` |  |
| 245 | `DebtManager.Strictness.STANDARD` |  |
| 246 | `DebtManager.Strictness.STRICT` |  |
| 247 | `DebtManager.Strictness.VERY_STRICT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 72 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 141 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way, at the default 2% target: a one-point real rate (ruleRate() adds the target's distance from the default since 0.7.42 - its aim's, at a strict setting, since... |
| 144 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 147 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 150 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 196 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target, at Standard - since 0.7.52 the dial beside the target sets another, never as little as one (HOW STRICT, below). |
| 230 | `DebtManager.STRICTEST_AIM` | `Expectations.TOLERANCE` | How far under the target the very strict bank aims, a fraction a year: Expectations.TOLERANCE, the furthest under it inflation can sit and still count as on target - so hitting the aim costs no trust. |
| 233 | `DebtManager.LOOSEST_BAND` | `2 * Expectations.TOLERANCE` | The band either side of the target the very loose bank lets be, a fraction a year: twice Expectations.TOLERANCE, so its outer point is a miss trust counts and the bank does not answer. |
| 236 | `DebtManager.STRICTEST_WEIGHT` | `2.0` | The very strict bank's weight on the gap from its aim: TAYLOR_WEIGHT's margin over one, doubled. |
| 239 | `DebtManager.LOOSEST_WEIGHT` | `1.25` | The very loose bank's weight on the gap past its band: TAYLOR_WEIGHT's margin over one, halved - still over one (the Taylor principle). |
| 499 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 534 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 537 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say. |
| 540 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 677 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 680 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 683 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 690 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 693 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 696 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 699 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 702 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 705 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1232 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1235 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 1490 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1493 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1496 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1499 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1502 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1505 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TER...` | The table, at 10, 20, 30, 40 and 50 years - LongTermBond.MATURITIES. |
| 1990 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private transient DecisionLog decisions` | Where the player's change to the policy rate, the rule, the inflation target or how strictly the rule holds it is written (DecisionLog, 0.7.23); null for one no city holds. |
| 75 | `private double baseRate` | What the city's central bank charges. |
| 114 | `private boolean autopilot` | THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the player. |
| 164 | `private double inflationTarget` | THE TARGET IS A DIAL (0.7.4). |
| 250 | `public final String words` | Its name on the dial. |
| 252 | `public final double slack` | Where the rule starts to act, against the target, a fraction a year: under nothing a strict bank's aim under it, over nothing a loose bank's band either side of it. |
| 254 | `public final double weight` | The rule's weight on the gap it answers. |
| 284 | `private Strictness strictness` | How strict the rule is: STANDARD until the player moves it. |
| 455 | `private double currentRate` |  |
| 456 | `private double GDP` |  |
| 468 | `private double monthlyTaxRevenue` | The month's tax take. |
| 480 | `private double overdraft` | How far the city is overdrawn. |
| 542 | `private List<Debt> debts` |  |
| 595 | `private double exchangeRate` |  |
| 707 | `private double monthlyExports` |  |
| 708 | `private double importCover` |  |
| 709 | `private double defaultScar` |  |
| 710 | `private int monthsSinceForeignDefault` |  |
| 1026 | `private double costOfFunds` | What the bank pays for the money it lends the city. |
| 1082 | `private double advances` | What the treasury owes its central bank in advances (0.7.0), pushed in with the overdraft. |
| 1730 | `private double accretedForBank` | This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 1992 | **type** `public class DebtManager` | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of money in the city is built on - the player's dial, or the rule's with t... |
| 24 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 26 | 3 | `private void decided(String kind, String label)` |  |

### THE POLICY RATE (lines 30-82)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1 | `public double getPolicyRate()` |  |
| 79 | 3 | `public void setPolicyRate(double rate)` |  |

### THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). (lines 83-197)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1 | `public boolean isAutopilot()` |  |
| 117 | 7 | `public void setAutopilot(boolean on)` |  |
| 130 | 9 | `public void takeTheDial(double rate)` | The player moves the dial by hand, which takes it back from the rule. |
| 167 | 1 | `public double getInflationTarget()` | What the city holds inflation to, a fraction a year: the rule aims at it, or under it at a strict setting (Strictness.aim(), 0.7.52). |
| 170 | 8 | `public void setInflationTarget(double target)` | Sets the target, held between MIN_INFLATION_TARGET and MAX_INFLATION_TARGET; not a number leaves it where it was. |
| 180 | 5 | `public static String targetWords(double target)` | A target as the screens write it: "2%", or "2.5%" on a half point. |

### HOW STRICT (0.7.52) (lines 198-590)

| line | len | member | says |
|---:|---:|---|---|
| 242 | 40 | **type** `public enum Strictness` | The five steps of the dial, loosest first; Strict and Loose sit halfway between Standard and their ends, in both numbers. |
| 256 | 5 | `Strictness(String words, double slack, double weight)` _(in DebtManager.Strictness)_ |  |
| 263 | 3 | `public double aim(double target)` _(in DebtManager.Strictness)_ | What it aims inflation at under a target: the target, or under it by the slack - never under MIN_INFLATION_TARGET, because no central bank aims at falling prices. |
| 268 | 1 | `public double band()` _(in DebtManager.Strictness)_ | How far either side of its aim it lets inflation be before it acts: nothing, or the slack. |
| 271 | 4 | `public static Strictness at(int step)` _(in DebtManager.Strictness)_ | The step at a place on the dial, 0 the loosest, held inside the ends. |
| 277 | 4 | `public static Strictness named(String name)` _(in DebtManager.Strictness)_ | By its saved name: STANDARD for null or a name this build does not know. |
| 286 | 1 | `public Strictness getStrictness()` |  |
| 289 | 5 | `public void setStrictness(Strictness s)` | Sets how strict the rule is, applied at once like the target; null leaves it where it was. |
| 296 | 7 | `public static String aimWords(double target, Strictness s)` | What a bank this strict aims at under a target, in words: "aims at 2.0%", "aims under 2.0%, at 1.0%", "acts only past 3.0% (or under 1.0%)". |
| 305 | 1 | `public String aimWords()` | ...at the city's target and strictness. |
| 308 | 4 | `private static String pct1(double rate)` | A rate to one place, with a true minus: "2.0%", "−1.0%". |
| 330 | 3 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation, held inside the dial's bounds - ruleRate() is the rule before them. |
| 335 | 3 | `public double advisedPolicyRate(double inflation, Strictness s)` | ...at a strictness of the caller's, at the city's target (0.7.52): the Policy tab's preview of the dial (PolicyPreview.ruleAt()) and holdingRate(). |
| 354 | 3 | `public double ruleRate(double inflation)` | What the rule itself says, before the dial's bounds (2026-09-21). |
| 359 | 3 | `public double holdingRate(double inflation)` | ...and what the Standard rule would set at it, held inside the dial's bounds: what holding the target takes, whatever the strictness - the advice Expectations measures the lean against (0.7.52, HOW STRICT). |
| 381 | 3 | `public double ruleRate(double inflation, double target)` | ...and what it would say at a target of the caller's rather than the dial's (0.7.4): the monetary page's sentence, "at 0% it would set 10.5%", struck from the rule rather than restated by the screen. |
| 393 | 7 | `public static double ruleRate(double inflation, double target, Strictness s)` | ...and at a strictness of the caller's (0.7.52): struck at the aim, as the rule is struck at a target, on the gap past the band, at the strictness's weight. |
| 402 | 1 | `public double neutralRate()` | The neutral nominal rate at the city's target: the neutral real rate plus the target - what the Standard rule sets on target, at any strictness (0.7.45, for the screens; Expectations' lean is measured from it). |
| 410 | 25 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 437 | 18 | `private String strictReason(double inflation, double advised, String target)` | adviceReason() off Standard (0.7.52): what this strictness aims at, and what the rule says against it. |
| 544 | 4 | `public DebtManager()` |  |
| 549 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 553 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 557 | 3 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 561 | 4 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 566 | 3 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 570 | 4 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 585 | 5 | `private Debt book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 591-647)

| line | len | member | says |
|---:|---:|---|---|
| 607 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 613 | 1 | `public double getExchangeRate()` |  |
| 616 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 623 | 5 | `public double getForeignPrincipalUsd()` | ...and in the dollars it is actually owed in, which do not move. |
| 630 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 637 | 5 | `public double getForeignCouponUsd()` | Next month's USD coupon bill, in dollars. |
| 643 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 648-1163)

| line | len | member | says |
|---:|---:|---|---|
| 718 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 723 | 1 | `public double getMonthlyExports()` |  |
| 724 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 731 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 743 | 1 | `public double getImportCover()` |  |
| 746 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 759 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 787 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 791 | 6 | `public double serviceStressAt(double service)` |  |
| 799 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 810 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 814 | 6 | `public double countryPremiumAt(double owed)` |  |
| 822 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). |
| 844 | 3 | `public double foreignCurveRate(int months)` | THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar paper of this many months - the foreign rate, plus the SAME term premium table the city's own curve carries (termPremium()). |
| 853 | 4 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 859 | 3 | `public double quoteForeignRate(double extraUsd, int months)` | ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. |
| 883 | 11 | `public double quoteForeignRate(Debt proposed, int months)` | ...and priced with the proposed paper ON THE BOOKS, its own next year of service included (0.7.2): the rate the world's curve will read for this paper, at this maturity, the moment it is booked. |
| 902 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 907 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 930 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 936 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 942 | 1 | `public double getDefaultScar()` |  |
| 943 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 946 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 950 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 956 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 1005 | 3 | `public double getRate()` | THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term premium - which is what every screen means by "the city's rate" and what the advisor reads. |
| 1027 | 1 | `public void setCostOfFunds(double rate)` |  |
| 1028 | 1 | `public double getCostOfFunds()` |  |
| 1045 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 1054 | 3 | `public void setGDP(double GDP)` | setters |
| 1059 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 1069 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 1073 | 1 | `public double getOverdraft()` |  |
| 1084 | 1 | `public void setAdvances(double owed)` |  |
| 1085 | 1 | `public double getAdvances()` |  |
| 1097 | 3 | `public double[] marketToSave()` | THE MARKET'S LAST STRIKE, for the save (0.7.14): the four inputs it was last handed - a month's output, a month's tax, the overdraft, the advances - and the rate it struck on them. |
| 1102 | 8 | `public void restoreMarket(double[] saved)` | ...and back, over the load path's own strike; an older save has none and keeps that strike. |
| 1111 | 3 | `public List<Debt> getDebt()` |  |
| 1115 | 31 | `public void processAllDebts(Game game)` |  |
| 1156 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |

### WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) (lines 1164-1446)

| line | len | member | says |
|---:|---:|---|---|
| 1180 | 5 | `public double getMonthlyCoupon()` | What the city is charged in coupon a month, every piece together, in local money: the bonds' own getMonthlyInterestExpense(), summed - the Finances tab's COUPON. |
| 1187 | 5 | `public double getPrincipalOf(String type)` | Principal outstanding of one kind of paper, by Debt.getType() - "NOTE", "SERIAL" or "TERM" - at home and abroad, in local money. |
| 1199 | 8 | `public double dueWithin(int months)` | Everything the city's paper asks of it over the next `months` months, coupons and principal together, in local money - the first `months` of every piece's remainingCashFlows(), the ladder's own read: the Debt service ... |
| 1209 | 9 | `public double dueAbroadWithinUsd(int months)` | ...of the dollar paper alone, in the dollars it is owed in (0.7.40): the first `months` of each foreign piece's own schedule - Finances' ask abroad. |
| 1220 | 5 | `public double valuationRate(Debt paper)` | The rate a piece is valued at today: the city's curve for its own paper, the world's for a dollar piece, at the months it has left - marketValue()'s, so a screen quoting its yield or its price of par reads the price a... |
| 1227 | 3 | `public double underFace(Debt paper)` | What buying a piece back today would save against its face: its principal less marketValue() - below nothing when it costs a premium to retire (the Finances tab's book, which subtracted them itself). |
| 1238 | 3 | `public static int ladderKind(String type)` | A kind's place in LADDER_KINDS: a note 0, a serial 1, anything else - a term loan - 2. |
| 1252 | 9 | **type** `public record Rung(int fromYear, int toYear, int fromMonth, int toMonth, double[] byKind, double[] abroadBy...` | One rung of the ladder: a calendar year of payments, from its first month on the ladder to its last - a whole year, but the first, which starts next month, and the one the last payment falls in, which ends with it; bo... |
| 1255 | 1 | `public double owed()` _(in DebtManager.Rung)_ | What the city's paper asks in it. |
| 1257 | 1 | `public double abroad()` _(in DebtManager.Rung)_ | ...of which in dollars. |
| 1259 | 1 | `public double total()` _(in DebtManager.Rung)_ | ...and with the proposed issue's. |
| 1268 | 4 | **type** `public record Ladder(List<Rung> years, Rung later, int heaviest, double owed, double proposed)` | The ladder: LADDER_YEARS calendar years from next month's, and what falls after them as one "later" rung (null when nothing does); which of the years is the heaviest (-1 with nothing owed in them) - never "later", whi... |
| 1270 | 1 | `public double total()` _(in DebtManager.Ladder)_ | What is owed and what the proposed issue would ask, together. |
| 1274 | 1 | `public Ladder ladder(int month)` | The ladder as the city owes it, struck in `month`. |
| 1290 | 50 | `public Ladder ladder(int month, double[] extra)` | WHEN IT FALLS DUE, BY THE CALENDAR YEAR IT IS PAID IN (0.7.32, the Finances spec's D4). |
| 1348 | 3 | `public double getPricedDebt()` | Everything the city owes, including what it is overdrawn and what it owes the central bank in advances. |
| 1363 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 1368 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back today: each at the curve's rate for the months it has left (0.7.1). |
| 1388 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 1424 | 3 | `private double priceAt(double debt)` | The curve itself: a floor, plus up to ten points from each measure. |
| 1439 | 7 | `private double priceAt(double debt, int months)` | ...at a maturity (0.7.1): the same credit judgement, clamped the same way, plus the term premium for that many months less what the central bank's holdings compress of it (and the bank's strain premium outside it all,... |

### THE CURVE (0.7.1) (lines 1447-1639)

| line | len | member | says |
|---:|---:|---|---|
| 1516 | 8 | `public static double termPremium(int months)` | What a lender adds for tying money up this many months, before the central bank compresses any of it. |
| 1538 | 7 | `public double compression(int months)` | What the central bank's holdings take off the premium at this many months: the premium, times the share of the city's term paper it actually holds (not its target) over CentralBank.FULL_COMPRESSION_SHARE, times Centra... |
| 1553 | 5 | `public double compressionAt(double share, int months)` | ...at a share of the term paper of the caller's (0.7.36): what holding `share` would take off the premium at this many months, compression()'s rule with the share given - the Policy tab's holdings dial, whose thirty-y... |
| 1565 | 3 | `public double curveRateAtShare(int months, double share)` | The curve at a maturity with the central bank holding `share` of the term paper (0.7.36): curveRate()'s credit judgement and premium, less compressionAt() that share - the thirty-year rate "once the book has moved" to... |
| 1570 | 4 | `private double termShape(int months)` | The premium less the compression: the curve's shape over the short end. |
| 1580 | 3 | `public double curveRate(int months)` | THE CURVE: what the city's paper of this many months is worth to a lender today - the standing rate at that maturity, on what the city owes now. |
| 1591 | 5 | `public double marketValue(Debt paper)` | What this paper would fetch today: the present value of what it still owes at the curve's rate for the months it has left - the city's curve for its own paper, the world's (foreignCurveRate()) for a dollar bond since ... |
| 1598 | 3 | `public static boolean isTermPaper(Debt paper)` | True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. |
| 1603 | 5 | `public double termPrincipal()` | The city's own term paper outstanding, at face: the base of the holdings dial. |
| 1610 | 9 | `public double centralBankShareOfTerm()` | The share of it the central bank holds - the compression's measure. |
| 1630 | 9 | `public double centralBankShareOfPaper()` | The central bank's share of ALL the city's own paper outstanding, at face, every maturity - the notes it never buys in the denominator - the floor's measure (0.7.15, floorRate()). |

### who holds it (0.7.1) (lines 1640-1733)

| line | len | member | says |
|---:|---:|---|---|
| 1643 | 5 | `public double householdPrincipal()` | What the city's households hold of its own paper, at face. |
| 1650 | 5 | `public double centralBankPrincipal()` | ...the central bank, at face. |
| 1657 | 5 | `public double bankPrincipal()` | ...and the commercial bank: the domestic principal less the other two. |
| 1670 | 5 | `public double bankBook()` | ...of which the paper the bank has PAID for: what its book carries. |
| 1677 | 13 | `public double[] bookValues()` | Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank. |
| 1696 | 4 | `public double householdBookRatio()` | The households' book at market over its face: ONE RATIO A MONTH, which is what their paper counts for in their net worth and what the desk pays them for it. |
| 1702 | 10 | `public double householdBookYield()` | What the households' paper yields at today's curve, weighted by what they hold of each piece; the short rate when they hold none. |
| 1720 | 8 | `public double bankUnearnedDiscount()` | The discount the bank has not yet earned on what it holds: every piece's unaccreted remainder, in the share of its principal the bank holds. |
| 1732 | 1 | `public double getAccretedForBank()` |  |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 1734-2009)

| line | len | member | says |
|---:|---:|---|---|
| 1759 | 6 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 1772 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate, or the bank's cost of funds if that is higher - on the share of the city's paper the bank and the households hold, since 0.7.15; the central bank's share is priced at the pol... |
| 1775 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 1778 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 1781 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 1785 | 3 | `public double revenueStress()` |  |
| 1790 | 1 | `public double overTheWorld()` | What the city's own rate is over the world's (0.7.36): the short end against WORLD_BASE_RATE - the difference hot money follows. |
| 1793 | 1 | `public double overTheWorldAt(double policy)` | ...at a policy rate of the caller's: rateAtPolicy() against the world's. |
| 1796 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 1799 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 1802 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 1805 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 1808 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 1856 | 3 | `public double floorRate()` | What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR IS REAL NOW - but never less than the money costs the bank that lends it. |
| 1867 | 6 | `private double floorAt(double policy)` | ...at a policy rate of the caller's: the one definition floorRate() and rateAtPolicy() both read, so the Policy tab's "what moving the dial does" splits the floor as the market does. |
| 1881 | 3 | `public double bankFloorRate()` | The bank's floor, unsplit: the policy rate, or the bank's cost of funds plus Bank.MIN_MARGIN if that is higher - what the share of the city's paper the central bank does not hold is priced at, and the whole floor unti... |
| 1886 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 1897 | 10 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 1928 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 1937 | 12 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months)` | ...at a maturity (0.7.1): the same fixed point on the curve's rate for that many months. |
| 1951 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 1956 | 3 | `public double quoteRate(double requested, int months)` | ...at a maturity. |
| 1975 | 5 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |
| 1981 | 3 | `public void clearDebts()` |  |
| 1985 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 1992 | 4 | `static { ... }` |  |
| 1998 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

