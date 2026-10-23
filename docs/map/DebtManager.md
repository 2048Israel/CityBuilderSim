# DebtManager.java - 2,901 lines · 224 methods · 35 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-10-23 by CodeMap; line numbers are as of that run.

> The city's borrowing: every bond, note and dollar bond the treasury owes,
> the market that prices the next one, and the policy rate every price of
> money in the city is built on - the player's dial, or the rule's with the
> autopilot on (0.7.0).

**Uses:** [Debt](Debt.md) (70), [YieldCurve](YieldCurve.md) (34), [DecisionLog](DecisionLog.md) (14), [CallTree](CallTree.md) (10), [InflationLinkedBond](InflationLinkedBond.md) (8), [LongTermBond](LongTermBond.md) (5), [FloatingRateNote](FloatingRateNote.md) (5), [CityCalendar](CityCalendar.md) (5), [Auction](Auction.md) (5), [AuctionTally](AuctionTally.md) (4), [Ladder](Ladder.md) (4), [CentralBank](CentralBank.md) (4), [Expectations](Expectations.md) (3), [ShortTermTBill](ShortTermTBill.md) (3), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [Game](Game.md) (1)

**Used by (37):** [AuctionCheck](AuctionCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [Expectations](Expectations.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [Game](Game.md), [HoldersCheck](HoldersCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyCheck](MoneyCheck.md), [MortgageCheck](MortgageCheck.md), [Pieces](Pieces.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [SimulationEngine](SimulationEngine.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [YieldCurve](YieldCurve.md)

## Sections

| line | section |
|---:|---|
| 30 | THE POLICY RATE |
| 83 | · THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). |
| 125 | · WHAT THE AUTOPILOT AIMS AT, AND THE DIAL UNDER A PEG (XR2, 2026-10-22) |
| 275 | · HOW STRICT (0.7.52) |
| 714 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 771 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 1331 | · THE CITY'S BOOKS, IN TOTAL (CD3, spec-city-debt.md section 5) |
| 1402 | · WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) |
| 1692 | THE CURVE (0.7.1) |
| 1840 | · the expectations the curve is built from (CD1) |
| 2049 | · the price index, and the paths a floater and a linker are priced on (CD5) |
| 2162 | · the call (CD4, spec-city-debt.md section 6.4) |
| 2279 | · who holds it (0.7.1) |
| 2423 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |
| 2721 | · WHAT THE AUCTION STARTS FROM, AND WHERE A SALE GOES (CD2) |
| 2752 | · FAIR AT THE SALE, FOR A FLOATER AND A LINKER (CD5, spec-city-debt.md |

## Enum constants

| line | constant | says |
|---:|---|---|
| 150 | `DebtManager.Aim.INFLATION` |  |
| 150 | `DebtManager.Aim.CURRENCY` |  |
| 150 | `DebtManager.Aim.MIX` |  |
| 337 | `DebtManager.Strictness.VERY_LOOSE` |  |
| 338 | `DebtManager.Strictness.LOOSE` |  |
| 339 | `DebtManager.Strictness.STANDARD` |  |
| 340 | `DebtManager.Strictness.STRICT` |  |
| 341 | `DebtManager.Strictness.VERY_STRICT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 72 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 156 | `DebtManager.CURRENCY_WEIGHT` | `.25` | A quarter point of rate for each 1% the D$ sits past its target: the open-economy Taylor rule's weight on the exchange rate (the study's star 13, R39). |
| 218 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way, at the default 2% target: a one-point real rate (ruleRate() adds the target's distance from the default since 0.7.42 - its aim's, at a strict setting, since... |
| 221 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 224 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 227 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 273 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target, at Standard - since 0.7.52 the dial beside the target sets another, never as little as one (HOW STRICT, below). |
| 324 | `DebtManager.STRICTEST_AIM` | `Expectations.TOLERANCE` | How far under the target the very strict bank aims, a fraction a year: Expectations.TOLERANCE, the furthest under it inflation can sit and still count as on target - so hitting the aim costs no trust. |
| 327 | `DebtManager.LOOSEST_BAND` | `2 * Expectations.TOLERANCE` | The band over the target the very loose bank lets be, a fraction a year (either side of it until 0.7.81; under it, it cuts as Standard does): twice Expectations.TOLERANCE, so its top is a miss trust counts and the ban... |
| 330 | `DebtManager.STRICTEST_WEIGHT` | `2.0` | The very strict bank's weight on the gap from its aim: TAYLOR_WEIGHT's margin over one, doubled. |
| 333 | `DebtManager.LOOSEST_WEIGHT` | `1.25` | The very loose bank's weight on the gap past its band: TAYLOR_WEIGHT's margin over one, halved - still over one (the Taylor principle). |
| 599 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 634 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 637 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say (public since CD1: YieldCurve clamps every forward at it). |
| 640 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 800 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money. |
| 803 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 806 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 813 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 816 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 819 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 822 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 825 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 828 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1477 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1480 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 1755 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 1758 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 1761 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 1764 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 1767 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 1770 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TER...` | The table, at 10, 20, 30, 40 and 50 years: the curve's points (40 is no longer a term a bond is sold at, CD2, but it still shapes the line between 30 and 50). |
| 2780 | `DebtManager.FAIR_COUPON_STEPS` | `100` | The halvings of the bisection above: far past what a double keeps of a bracket of one unit of yield. |
| 2882 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private transient DecisionLog decisions` | Where the player's change to the policy rate, the rule, the inflation target or how strictly the rule holds it is written (DecisionLog, 0.7.23); null for one no city holds. |
| 75 | `private double baseRate` | What the city's central bank charges. |
| 114 | `private boolean autopilot` | THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the player. |
| 151 | `public final String words` |  |
| 158 | `private Aim aim` |  |
| 170 | `private double pegSpread` | Under a peg, what the dial stands over the US's rate (a fraction a year, negative under it): the player's to set, to defend the peg. |
| 241 | `private double inflationTarget` | THE TARGET IS A DIAL (0.7.4). |
| 344 | `public final String words` | Its name on the dial. |
| 346 | `public final double slack` | Where the rule starts to act, against the target, a fraction a year: under nothing a strict bank's aim under it, over nothing a loose bank's band over it (either side until 0.7.81). |
| 348 | `public final double weight` | The rule's weight on the gap it answers. |
| 378 | `private Strictness strictness` | How strict the rule is: STANDARD until the player moves it. |
| 555 | `private double currentRate` |  |
| 556 | `private double GDP` |  |
| 568 | `private double monthlyTaxRevenue` | The month's tax take. |
| 580 | `private double overdraft` | How far the city is overdrawn. |
| 642 | `private List<Debt> debts` |  |
| 718 | `private double exchangeRate` |  |
| 830 | `private double monthlyExports` |  |
| 831 | `private double importCover` |  |
| 832 | `private double defaultScar` |  |
| 833 | `private int monthsSinceForeignDefault` |  |
| 1149 | `private double costOfFunds` | What the bank pays for the money it lends the city. |
| 1205 | `private double advances` | What the treasury owes its central bank in advances (0.7.0), pushed in with the overdraft. |
| 1216 | `private final AuctionTally auctionTally` | THE CITY'S AUCTIONS, COUNTED (CD2, D33): every domestic sale that booked, its tail and its cover, and the last one's figures. |
| 1343 | `private double interestBooked` | This month's interest expense on the city's paper: every piece's carrying value at its effective rate, a month. |
| 1346 | `private double amortisedBooked` | ...and the part of it that is not coupon: the discount amortised, below nothing for a premium. |
| 1353 | `private double upliftBooked` | The month's inflation uplift on the linked bonds (CD5): the indexation each booked as its carrying value caught up with the price index (InflationLinkedBond.index()), summed. |
| 1356 | `private double upliftPaidBooked` | ...and the uplift the month PAID in cash: a linker's indexed principal over its face, in its last month. |
| 1843 | `private double struckInflation` | The inflation the rule read when the month's expectations were struck (strikeExpectations()); NaN until something strikes them. |
| 1846 | `private double struckExpected` | ...and the inflation the city expected then (Expectations.getExpectedInflation()); NaN until struck. |
| 1886 | `final double floor, inflation, expected, target` |  |
| 1887 | `final Strictness strictness` |  |
| 1888 | `final double[] path` |  |
| 1896 | `final double[] path` |  |
| 1897 | `final double gdpSpread, revenueSpread, ceiling, share` |  |
| 1898 | `final YieldCurve curve` |  |
| 1905 | `private transient volatile PathEntry pathCache` |  |
| 1906 | `private transient volatile CurveEntry curveCache` |  |
| 2007 | `final YieldCurve curve` |  |
| 2008 | `final double sigma` |  |
| 2009 | `final CallTree tree` |  |
| 2013 | `private transient volatile TreeEntry treeCache` |  |
| 2047 | `private transient java.util.function.DoubleSupplier rateVolatility` |  |
| 2066 | `private transient java.util.function.DoubleSupplier priceIndexSource` |  |
| 2068 | `private transient volatile PathEntry policyPathCache` |  |
| 2098 | `final double inflation, expected, target` |  |
| 2099 | `final double[] growth` |  |
| 2105 | `private transient volatile GrowthEntry growthCache` |  |
| 2187 | `final double rate` |  |
| 2188 | `final YieldCurve curve` |  |
| 2192 | `private transient volatile WorldEntry worldCache` |  |
| 2419 | `private double accretedForBank` | This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 2884 | **type** `public class DebtManager` | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of money in the city is built on - the player's dial, or the rule's with t... |
| 24 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 26 | 3 | `private void decided(String kind, String label)` |  |

### THE POLICY RATE (lines 30-82)

| line | len | member | says |
|---:|---:|---|---|
| 77 | 1 | `public double getPolicyRate()` |  |
| 79 | 3 | `public void setPolicyRate(double rate)` |  |

### THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). (lines 83-124)

| line | len | member | says |
|---:|---:|---|---|
| 116 | 1 | `public boolean isAutopilot()` |  |
| 117 | 7 | `public void setAutopilot(boolean on)` |  |

### WHAT THE AUTOPILOT AIMS AT, AND THE DIAL UNDER A PEG (XR2, 2026-10-22) (lines 125-274)

| line | len | member | says |
|---:|---:|---|---|
| 149 | 5 | **type** `public enum Aim` | What the autopilot sets the dial to hold (XR2; the study's star 13). |
| 152 | 1 | `Aim(String words)` _(in DebtManager.Aim)_ |  |
| 161 | 1 | `public Aim getAim()` | What the autopilot aims at: Inflation unless the player says otherwise. |
| 163 | 5 | `public void setAim(Aim a)` |  |
| 172 | 1 | `public double getPegSpread()` |  |
| 175 | 9 | `public void setPegSpread(double spread)` | Sets the spread, inside what keeps the tied rate on the dial (MIN_POLICY_RATE to MAX_POLICY_RATE over WORLD_BASE_RATE). |
| 186 | 3 | `public double currencyRate(double gap)` | The dial that holds the currency alone: the US's rate plus CURRENCY_WEIGHT for each unit of gap (the D$ past its target, a share, positive weaker), inside the dial's bounds. |
| 191 | 4 | `public double mixRate(double inflation, double gap)` | The inflation rule's dial plus CURRENCY_WEIGHT for each unit of gap: the mix of the two aims, inside the dial's bounds. |
| 197 | 4 | `public double tiedRate(double gap)` | The dial under a peg (star 5): the US's rate plus the spread - the player's, or with the autopilot aiming at the currency (or a mix) the one the gap asks - inside the dial's bounds. |
| 207 | 9 | `public void takeTheDial(double rate)` | The player moves the dial by hand, which takes it back from the rule. |
| 244 | 1 | `public double getInflationTarget()` | What the city holds inflation to, a fraction a year: the rule aims at it, or under it at a strict setting (Strictness.aim(), 0.7.52). |
| 247 | 8 | `public void setInflationTarget(double target)` | Sets the target, held between MIN_INFLATION_TARGET and MAX_INFLATION_TARGET; not a number leaves it where it was. |
| 257 | 5 | `public static String targetWords(double target)` | A target as the screens write it: "2%", or "2.5%" on a half point. |

### HOW STRICT (0.7.52) (lines 275-713)

| line | len | member | says |
|---:|---:|---|---|
| 336 | 40 | **type** `public enum Strictness` | The five steps of the dial, loosest first; Strict and Loose sit halfway between Standard and their ends, in both numbers. |
| 350 | 5 | `Strictness(String words, double slack, double weight)` _(in DebtManager.Strictness)_ |  |
| 357 | 3 | `public double aim(double target)` _(in DebtManager.Strictness)_ | What it aims inflation at under a target: the target, or under it by the slack - never under MIN_INFLATION_TARGET, because no central bank aims at falling prices. |
| 362 | 1 | `public double band()` _(in DebtManager.Strictness)_ | How far over its aim it lets inflation be before it acts: nothing, or the slack. |
| 365 | 4 | `public static Strictness at(int step)` _(in DebtManager.Strictness)_ | The step at a place on the dial, 0 the loosest, held inside the ends. |
| 371 | 4 | `public static Strictness named(String name)` _(in DebtManager.Strictness)_ | By its saved name: STANDARD for null or a name this build does not know. |
| 380 | 1 | `public Strictness getStrictness()` |  |
| 383 | 5 | `public void setStrictness(Strictness s)` | Sets how strict the rule is, applied at once like the target; null leaves it where it was. |
| 390 | 7 | `public static String aimWords(double target, Strictness s)` | What a bank this strict aims at under a target, in words: "aims at 2.0%", "aims under 2.0%, at 1.0%", "acts only past 3.0% (or under 2.0%)" - under the target itself since 0.7.81, as Standard does; its band's foot, "(... |
| 399 | 1 | `public String aimWords()` | ...at the city's target and strictness. |
| 402 | 4 | `private static String pct1(double rate)` | A rate to one place, with a true minus: "2.0%", "−1.0%". |
| 424 | 3 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation, held inside the dial's bounds - ruleRate() is the rule before them. |
| 429 | 3 | `public double advisedPolicyRate(double inflation, Strictness s)` | ...at a strictness of the caller's, at the city's target (0.7.52): the Policy tab's preview of the dial (PolicyPreview.ruleAt()) and holdingRate(). |
| 448 | 3 | `public double ruleRate(double inflation)` | What the rule itself says, before the dial's bounds (2026-09-21). |
| 453 | 3 | `public double holdingRate(double inflation)` | ...and what the Standard rule would set at it, held inside the dial's bounds: what holding the target takes, whatever the strictness - the advice Expectations measures the lean against (0.7.52, HOW STRICT). |
| 475 | 3 | `public double ruleRate(double inflation, double target)` | ...and what it would say at a target of the caller's rather than the dial's (0.7.4): the monetary page's sentence, "at 0% it would set 10.5%", struck from the rule rather than restated by the screen. |
| 489 | 8 | `public static double ruleRate(double inflation, double target, Strictness s)` | ...and at a strictness of the caller's (0.7.52): struck at the aim, as the rule is struck at a target, on the gap past the band, at the strictness's weight. |
| 499 | 1 | `public double neutralRate()` | The neutral nominal rate at the city's target: the neutral real rate plus the target - what the Standard rule sets on target, at any strictness (0.7.45, for the screens; Expectations' lean is measured from it). |
| 507 | 25 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 534 | 21 | `private String strictReason(double inflation, double advised, String target)` | adviceReason() off Standard (0.7.52): what this strictness aims at, and what the rule says against it - a loose bank's band over its aim only (0.7.81), Standard's rule under it. |
| 644 | 4 | `public DebtManager()` |  |
| 649 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 653 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 657 | 3 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 661 | 4 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 666 | 3 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 670 | 4 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 676 | 3 | `public Debt addCallableBond(double faceValue, int months, int monthStarted, double rate, int firstCallMonth)` | A bond sold callable (CD4): the right to call it whole at 100 from the game month firstCallMonth on, its coupon the caller's, option included. |
| 685 | 3 | `public Debt addFloatingRateNote(double faceValue, int months, int monthStarted, double margin, double index)` | A floating-rate note (CD5, spec-city-debt.md section 2.4): a coupon at the policy rate plus {@code margin}, the face at the end. |
| 694 | 3 | `public Debt addLinkedBond(double faceValue, int months, int monthStarted, double realCoupon, double baseIndex)` | An inflation-linked bond (CD5, section 2.5): a real coupon on a principal that follows the price index, never under the face at the end. |
| 708 | 5 | `private Debt book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 714-770)

| line | len | member | says |
|---:|---:|---|---|
| 730 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 736 | 1 | `public double getExchangeRate()` |  |
| 739 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 746 | 5 | `public double getForeignPrincipalUsd()` | ...and in the dollars it is actually owed in, which do not move. |
| 753 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 760 | 5 | `public double getForeignCouponUsd()` | Next month's USD coupon bill, in dollars. |
| 766 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 771-1330)

| line | len | member | says |
|---:|---:|---|---|
| 841 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 846 | 1 | `public double getMonthlyExports()` |  |
| 847 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 854 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 866 | 1 | `public double getImportCover()` |  |
| 869 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 882 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 910 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 914 | 6 | `public double serviceStressAt(double service)` |  |
| 922 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 933 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 937 | 6 | `public double countryPremiumAt(double owed)` |  |
| 945 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). |
| 967 | 3 | `public double foreignCurveRate(int months)` | THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar paper of this many months - the foreign rate, plus the SAME term premium table the city's own curve carries (termPremium()). |
| 976 | 4 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 982 | 3 | `public double quoteForeignRate(double extraUsd, int months)` | ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. |
| 1006 | 11 | `public double quoteForeignRate(Debt proposed, int months)` | ...and priced with the proposed paper ON THE BOOKS, its own next year of service included (0.7.2): the rate the world's curve will read for this paper, at this maturity, the moment it is booked. |
| 1025 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 1030 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 1053 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 1059 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 1065 | 1 | `public double getDefaultScar()` |  |
| 1066 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 1069 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 1073 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 1079 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 1128 | 3 | `public double getRate()` | THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term premium - which is what every screen means by "the city's rate" and what the advisor reads. |
| 1150 | 1 | `public void setCostOfFunds(double rate)` |  |
| 1151 | 1 | `public double getCostOfFunds()` |  |
| 1168 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 1177 | 3 | `public void setGDP(double GDP)` | setters |
| 1182 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 1192 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 1196 | 1 | `public double getOverdraft()` |  |
| 1207 | 1 | `public void setAdvances(double owed)` |  |
| 1208 | 1 | `public double getAdvances()` |  |
| 1218 | 1 | `public AuctionTally getAuctionTally()` |  |
| 1221 | 1 | `public int auctionSales()` | Sales the treasury has booked at auction. |
| 1223 | 1 | `public int auctionTailsOverPoint()` | ...of them, the ones that cleared more than a point over fair (Auction.MAX_TAIL). |
| 1225 | 1 | `public int auctionShortSales()` | ...and the ones whose bids did not reach the issue at the city's cap. |
| 1227 | 1 | `public double largestTail()` | The largest tail any of them cleared at, in yield. |
| 1229 | 1 | `public AuctionTally.Last lastAuction()` | The sale last booked, or null before the first. |
| 1247 | 4 | `public double[] marketToSave()` | THE MARKET'S LAST STRIKE, for the save (0.7.14): the four inputs it was last handed - a month's output, a month's tax, the overdraft, the advances - and the rate it struck on them. |
| 1253 | 9 | `public void restoreMarket(double[] saved)` | ...and back, over the load path's own strike; an older save has none and keeps that strike. |
| 1263 | 3 | `public List<Debt> getDebt()` |  |
| 1267 | 63 | `public void processAllDebts(Game game)` |  |

### THE CITY'S BOOKS, IN TOTAL (CD3, spec-city-debt.md section 5) (lines 1331-1401)

| line | len | member | says |
|---:|---:|---|---|
| 1358 | 1 | `public double getInterestBooked()` |  |
| 1359 | 1 | `public double getAmortisedBooked()` |  |
| 1362 | 1 | `public double getUpliftBooked()` | The month's inflation uplift on linked bonds, booked (CD5): an expense, not cash. |
| 1365 | 1 | `public double getUpliftPaid()` | ...and the part of it the month paid in cash (a linker's last month): in the cash coupons the holders were paid, and the uplift line's, not the interest line's. |
| 1373 | 1 | `public double getCouponBooked()` | The coupons the month's books paid: its interest expense less what was amortised. |
| 1376 | 5 | `public double getCarryingValue()` | What the city's paper is carried at, every piece together, in local money: what the balance sheet shows (face less the unamortised discount, plus the premium). |
| 1383 | 1 | `public double getFairValue()` | What the city's paper is worth on the market today, every piece together: the note a government discloses and does not book (getTotalMarketValue()). |
| 1394 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |

### WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) (lines 1402-1691)

| line | len | member | says |
|---:|---:|---|---|
| 1418 | 5 | `public double getMonthlyCoupon()` | What the city is charged in coupon a month, every piece together, in local money: the bonds' own getMonthlyInterestExpense(), summed - the Finances tab's COUPON. |
| 1425 | 5 | `public double getPrincipalOf(String type)` | Principal outstanding of one kind of paper, by Debt.getType() - "NOTE", "SERIAL" or "TERM" - at home and abroad, in local money. |
| 1437 | 8 | `public double dueWithin(int months)` | Everything the city's paper asks of it over the next `months` months, coupons and principal together, in local money - the first `months` of every piece's remainingCashFlows(), the ladder's own read: the Debt service ... |
| 1447 | 9 | `public double dueAbroadWithinUsd(int months)` | ...of the dollar paper alone, in the dollars it is owed in (0.7.40): the first `months` of each foreign piece's own schedule - Finances' ask abroad. |
| 1466 | 4 | `public double valuationRate(Debt paper)` | The one flat rate at which a piece's payments price to marketValue(): its yield to maturity on the price a buyback pays (CD1; until then the curve read at the months it has left, which was the rate marketValue() had d... |
| 1472 | 3 | `public double underFace(Debt paper)` | What buying a piece back today would save against its face: its principal less marketValue() - below nothing when it costs a premium to retire (the Finances tab's book, which subtracted them itself). |
| 1483 | 3 | `public static int ladderKind(String type)` | A kind's place in LADDER_KINDS: a note 0, a serial 1, anything else - a term loan - 2. |
| 1497 | 9 | **type** `public record Rung(int fromYear, int toYear, int fromMonth, int toMonth, double[] byKind, double[] abroadBy...` | One rung of the ladder: a calendar year of payments, from its first month on the ladder to its last - a whole year, but the first, which starts next month, and the one the last payment falls in, which ends with it; bo... |
| 1500 | 1 | `public double owed()` _(in DebtManager.Rung)_ | What the city's paper asks in it. |
| 1502 | 1 | `public double abroad()` _(in DebtManager.Rung)_ | ...of which in dollars. |
| 1504 | 1 | `public double total()` _(in DebtManager.Rung)_ | ...and with the proposed issue's. |
| 1513 | 4 | **type** `public record Ladder(List<Rung> years, Rung later, int heaviest, double owed, double proposed)` | The ladder: LADDER_YEARS calendar years from next month's, and what falls after them as one "later" rung (null when nothing does); which of the years is the heaviest (-1 with nothing owed in them) - never "later", whi... |
| 1515 | 1 | `public double total()` _(in DebtManager.Ladder)_ | What is owed and what the proposed issue would ask, together. |
| 1519 | 1 | `public Ladder ladder(int month)` | The ladder as the city owes it, struck in `month`. |
| 1535 | 50 | `public Ladder ladder(int month, double[] extra)` | WHEN IT FALLS DUE, BY THE CALENDAR YEAR IT IS PAID IN (0.7.32, the Finances spec's D4). |
| 1593 | 7 | `public double getPricedDebt()` | Everything the city owes, including what it is overdrawn and what it owes the central bank in advances. |
| 1607 | 7 | `public double getIndexationOwed()` | What the linked bonds owe past their face today (CD5): each one's face times how far its ratio is over 1 - nothing for a ratio at or under 1, because the deflation floor means the face is owed whatever the index did. |
| 1626 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 1631 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back today: marketValue() of each, every payment at its own month's factor on the curve. |
| 1651 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 1687 | 4 | `private double priceAt(double debt)` | The curve itself: a floor, plus up to ten points from each measure. |

### THE CURVE (0.7.1) (lines 1692-1839)

| line | len | member | says |
|---:|---:|---|---|
| 1781 | 8 | `public static double termPremium(int months)` | What a lender adds for tying money up this many months, before the central bank compresses any of it. |
| 1803 | 7 | `public double compression(int months)` | What the central bank's holdings take off the premium at this many months: the premium, times the share of the city's term paper it actually holds (not its target) over CentralBank.FULL_COMPRESSION_SHARE, times Centra... |
| 1818 | 5 | `public double compressionAt(double share, int months)` | ...at a share of the term paper of the caller's (0.7.36): what holding `share` would take off the premium at this many months, compression()'s rule with the share given - the Policy tab's holdings dial, whose thirty-y... |
| 1830 | 3 | `public double curveRateAtShare(int months, double share)` | The curve at a maturity with the central bank holding `share` of the term paper (0.7.36): curveRate()'s credit judgement and premium, less compressionAt() that share - the thirty-year rate "once the book has moved" to... |
| 1835 | 4 | `private double termShapeAt(double share, int months)` | The premium less the compression at a central-bank share of the term paper: the curve's shape over the short end. |

### the expectations the curve is built from (CD1) (lines 1840-2048)

| line | len | member | says |
|---:|---:|---|---|
| 1860 | 4 | `public void strikeExpectations(double inflationNow, double expectedInflation)` | Strikes the two figures the curve's expected dial is built from: the inflation the rule reads (this month's) and the inflation the city expects (where it settles). |
| 1866 | 3 | `public boolean isStruck()` | True once the curve's expectations have been struck and are numbers. |
| 1871 | 1 | `public double getStruckInflation()` | The inflation the rule read when the month struck the curve's expectations, a fraction a year; NaN unstruck. |
| 1874 | 1 | `public double getStruckExpected()` | ...and the inflation the city expected then; NaN unstruck. |
| 1885 | 9 | **type** `private static final class PathEntry` | The expected dial's path and the curve built on it are CACHED, because marketValue() runs for every piece several times a month and quoteRate() walks a fixed point that builds a curve in each pass. |
| 1889 | 4 | `PathEntry(double floor, double inflation, double expected, double target, Strictness strictness, double[] path)` _(in DebtManager.PathEntry)_ |  |
| 1895 | 9 | **type** `private static final class CurveEntry` |  |
| 1899 | 4 | `CurveEntry(double[] path, double gdpSpread, double revenueSpread, double ceiling, double share, YieldCurve curve)` _(in DebtManager.CurveEntry)_ |  |
| 1908 | 3 | `private static boolean same(double a, double b)` |  |
| 1913 | 18 | `private double[] pathFor(double floor)` | The dial the market expects from this floor: the rule's path from the struck expectations, or the floor held flat when nothing is struck. |
| 1939 | 16 | `private YieldCurve curveAt(double debt)` | The curve this debt would be priced on: the expected dial plus the credit judgement at this debt (spreadFor() on a year of output and a year of tax, clamped at the ceiling), the shape the term premium and the central ... |
| 1957 | 3 | `public YieldCurve curve()` | THE CURVE (CD1): the city's own, on what it owes now - a discount factor for every month to fifty years. |
| 1967 | 3 | `public double curveRate(int months)` | THE CURVE: what the city's paper of this many months is worth to a lender today - the zero rate at that maturity, on what the city owes now: the compounded forward rates of the dial the market expects, plus the shape. |
| 1981 | 11 | `public double marketValue(Debt paper)` | What this paper would fetch today: the present value of what it still owes, EACH PAYMENT AT ITS OWN MONTH'S FACTOR (CD1) - the city's curve() for its own paper, the world's (worldCurveAt()) for a dollar bond since 0.7... |
| 2001 | 4 | `private double callableValue(LongTermBond bond, YieldCurve curve, double[] flows)` | A callable bond's value (CD4): what the tree prices its remaining payments at on today's curve, with the city's right to call it at its principal from the first call month on (LongTermBond.monthsToFirstCall()), at the... |
| 2006 | 6 | **type** `private static final class TreeEntry` |  |
| 2010 | 1 | `TreeEntry(YieldCurve curve, double sigma, CallTree tree)` _(in DebtManager.TreeEntry)_ |  |
| 2022 | 7 | `private CallTree treeOn(YieldCurve curve, double sigma, int months)` | The tree fitted to this curve at this volatility, long enough for a schedule of this many months: built once and kept while the curve (one object while its inputs stand, curveAt()) and the volatility stand, and refitt... |
| 2036 | 4 | `public double rateVolatility()` | The volatility of the city's short rate, a year, that a callable piece is valued at: Game's measure of its own history (Game.rateVolatility(), handed in with setRateVolatility()), the floor (CallTree.RATE_VOL_FLOOR) f... |
| 2042 | 4 | `public void setRateVolatility(java.util.function.DoubleSupplier source)` | Hands the manager the city's volatility, so it can value a callable piece without knowing the city's history. |

### the price index, and the paths a floater and a linker are priced on (CD5) (lines 2049-2161)

| line | len | member | says |
|---:|---:|---|---|
| 2056 | 4 | `public double priceIndexLevel()` | The city's price index now (PriceIndex.getIndex()), as a linked bond is indexed on it: what Game hands in with setPriceIndexSource(), read afresh each time so a reloaded index is the one read. |
| 2062 | 3 | `public void setPriceIndexSource(java.util.function.DoubleSupplier source)` | Hands the manager the price index, so it can index a linked bond without knowing the city's prices. |
| 2077 | 19 | `public double[] expectedPolicyPath()` | THE DIAL THE MARKET EXPECTS FROM THE POLICY RATE, t = 0 the dial now (CD5): what a floating-rate note's coupons are priced on - the same expected path the curve is built on (YieldCurve.expectedPath()), started from th... |
| 2097 | 7 | **type** `private static final class GrowthEntry` |  |
| 2100 | 3 | `GrowthEntry(double inflation, double expected, double target, double[] growth)` _(in DebtManager.GrowthEntry)_ |  |
| 2115 | 19 | `public double[] expectedInflationGrowth()` | THE PRICE INDEX THE MARKET EXPECTS, month by month (CD5): entry j is the growth of the index in the month j months ahead, (1 + pi(j))^(1/12), with pi(j) = expected + (now - expected) x e^(-j / Expectations.ADAPT_MONTH... |
| 2141 | 7 | `public double[] pricingFlows(Debt paper)` | The payments the market prices a piece on (CD5): a floating-rate note's coupons at the dial it expects (expectedPolicyPath()), a linked bond's on the index it expects (expectedInflationGrowth(), the next payment on th... |
| 2157 | 4 | `public double realYieldOf(InflationLinkedBond paper)` | The real yield of a linked bond at its market value (CD5): the known schedule - the real coupon on the principal as the index stands now, held flat, and the indexed principal at the end - solved for the yield that mak... |

### the call (CD4, spec-city-debt.md section 6.4) (lines 2162-2278)

| line | len | member | says |
|---:|---:|---|---|
| 2171 | 4 | `public static double callSavingOn(YieldCurve curve, Debt paper, double fee)` | The present value a call would save, per the study: the line's remaining payments at this curve, as a bond that cannot be called, less what the call pays (its principal) and less what selling the paper again costs (th... |
| 2177 | 3 | `public double callSaving(Debt paper, double fee)` | callSavingOn() the city's curve. |
| 2182 | 3 | `public static boolean worthCalling(double saving, double principal)` | True when a saving passes CallTree.WORTH_CALLING of the principal called (D24). |
| 2186 | 5 | **type** `private static final class WorldEntry` |  |
| 2189 | 1 | `WorldEntry(double rate, YieldCurve curve)` _(in DebtManager.WorldEntry)_ |  |
| 2206 | 7 | `public YieldCurve worldCurveAt(double shortRate)` | THE WORLD'S CURVE as a YieldCurve (CD1): the world's short rate held flat, and the term premium table (termPremium()) on top - the one definition of what a dollar payment this many months away is discounted by, for th... |
| 2215 | 3 | `public double foreignFactor(int months)` | What a dollar payment this many months away is discounted by: (1 + foreignCurveRate(months)/12)^(-months), on worldCurveAt(foreignRate()). |
| 2220 | 9 | `public double yieldOf(Debt paper)` | The yield to maturity of a piece on the price a buyback pays: a RESULT of marketValue(), not the rate it was priced at (CD1). |
| 2231 | 4 | `public double priceOfPar(Debt paper)` | Where a piece trades against par, as a percentage of what is still owed on it: marketValue() over its outstanding principal x 100; 0 when nothing is owed. |
| 2237 | 3 | `public static boolean isTermPaper(Debt paper)` | True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. |
| 2242 | 5 | `public double termPrincipal()` | The city's own term paper outstanding, at face: the base of the holdings dial. |
| 2249 | 9 | `public double centralBankShareOfTerm()` | The share of it the central bank holds - the compression's measure. |
| 2269 | 9 | `public double centralBankShareOfPaper()` | The central bank's share of ALL the city's own paper outstanding, at face, every maturity - the notes it never buys in the denominator - the floor's measure (0.7.15, floorRate()). |

### who holds it (0.7.1) (lines 2279-2422)

| line | len | member | says |
|---:|---:|---|---|
| 2282 | 5 | `public double householdPrincipal()` | What the city's households hold of its own paper, at face. |
| 2289 | 5 | `public double centralBankPrincipal()` | ...the central bank, at face. |
| 2296 | 5 | `public double firmPrincipal()` | ...the businesses, at face (CD2, slice 4). |
| 2303 | 5 | `public double worldPrincipal()` | ...the world, at face (CD2, slice 5). |
| 2310 | 5 | `public double bankPrincipal()` | ...and the commercial bank: the domestic principal less the other four. |
| 2323 | 5 | `public double bankBook()` | ...of which the paper the bank has PAID for: what its book carries. |
| 2330 | 15 | `public double[] bookValues()` | Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank, 3 the businesses, 4 the world. |
| 2351 | 4 | `public double householdBookRatio()` | The households' book at market over its face: ONE RATIO A MONTH, which is what their paper counts for in their net worth and what the desk pays them for it. |
| 2362 | 4 | `public double firmBookRatio()` | The businesses' book at market over its face (CD2, slice 4): the households' twin, ONE RATIO A MONTH - what a dollar of face of their paper is worth, what the desk pays them for it. |
| 2368 | 10 | `public double firmBookYield()` | What the businesses' paper yields at today's curve: the households' twin, weighted by what the businesses hold of each piece; the short rate when they hold none. |
| 2385 | 4 | `public double worldBookRatio()` | The world's book at market over its face (CD2, slice 5): the households' twin, one ratio a month - what a dollar of face of the paper it holds is worth at the curve, which is what the auction counts of it beside its h... |
| 2391 | 10 | `public double householdBookYield()` | What the households' paper yields at today's curve (each piece's yieldOf(), its yield to maturity on the price a buyback pays), weighted by what they hold of each piece; the short rate when they hold none. |
| 2409 | 8 | `public double bankUnearnedDiscount()` | The discount the bank has not yet earned on what it holds: every piece's unaccreted remainder, in the share of its principal the bank holds. |
| 2421 | 1 | `public double getAccretedForBank()` |  |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 2423-2720)

| line | len | member | says |
|---:|---:|---|---|
| 2449 | 5 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 2461 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate, or the bank's cost of funds if that is higher - on the share of the city's paper the bank and the households hold, since 0.7.15; the central bank's share is priced at the pol... |
| 2464 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 2467 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 2470 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 2474 | 3 | `public double revenueStress()` |  |
| 2479 | 1 | `public double overTheWorld()` | What the city's own rate is over the world's (0.7.36): the short end against WORLD_BASE_RATE - the difference hot money follows. |
| 2482 | 1 | `public double overTheWorldAt(double policy)` | ...at a policy rate of the caller's: rateAtPolicy() against the world's. |
| 2485 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 2488 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 2491 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 2494 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 2497 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 2512 | 3 | `public double expectedDialOver(int months)` | The dial the market expects, averaged over this many months, and nothing else in the rate: the compounded forwards less the credit spread. |
| 2517 | 3 | `public double creditSpread()` | What the credit judgement adds to the forward, as the curve carries it: the two spreads, or the room under the ceiling if less - the same in every month, the middle term of curveRate(m) == expectedDialOver(m) + credit... |
| 2522 | 3 | `public double expectedDialIn(int monthsAhead)` | The dial the market expects this many months ahead (0 this month: the floor). |
| 2570 | 3 | `public double floorRate()` | What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR IS REAL NOW - but never less than the money costs the bank that lends it. |
| 2581 | 6 | `private double floorAt(double policy)` | ...at a policy rate of the caller's: the one definition floorRate() and rateAtPolicy() both read, so the Policy tab's "what moving the dial does" splits the floor as the market does. |
| 2595 | 3 | `public double bankFloorRate()` | The bank's floor, unsplit: the policy rate, or the bank's cost of funds plus Bank.MIN_MARGIN if that is higher - what the share of the city's paper the central bank does not hold is priced at, and the whole floor unti... |
| 2600 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 2611 | 10 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 2642 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 2654 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months)` | ...at a maturity (0.7.1): the same fixed point, read as the curve's zero rate for that many months (none: the short rate). |
| 2659 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 2664 | 3 | `public double quoteRate(double requested, int months)` | ...at a maturity. |
| 2683 | 11 | `public YieldCurve quoteCurve(double requested, java.util.function.ToDoubleFunction<YieldCurve> faceOf)` | THE CURVE A NEW LOAN IS PRICED ON, with itself included (CD1): the fixed point quoteRate() walks, returned as the whole curve instead of one rate read off it. |
| 2696 | 3 | `public YieldCurve quoteCurve(double faceIsRequest)` | ...for an instrument whose face value IS the request: one pass, the loan priced in. |
| 2715 | 5 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |

### WHAT THE AUCTION STARTS FROM, AND WHERE A SALE GOES (CD2) (lines 2721-2751)

| line | len | member | says |
|---:|---:|---|---|
| 2737 | 3 | `public double fairYield(Auction.Kind kind, int months, double extraFace)` | The yield an auction of this paper starts from - fair (spec-city-debt.md section 4): what the curve says the paper is worth with `extraFace` of new debt on the books (quoteCurve(): the issue is priced into the credit ... |
| 2742 | 9 | `public static double fairYieldOn(YieldCurve curve, Auction.Kind kind, int months)` | ...read off a curve already in hand: the one the sale is priced on (Game.runAuction() asks quoteCurve() once and reads both from it). |

### FAIR AT THE SALE, FOR A FLOATER AND A LINKER (CD5, spec-city-debt.md (lines 2752-2901)

| line | len | member | says |
|---:|---:|---|---|
| 2770 | 8 | `public static double fairMargin(YieldCurve curve, double[] policyPath, int months)` | The margin over the policy rate that prices a floating-rate note of these months at 100 on this curve: each coupon at the dial the path expects when it is paid (FloatingRateNote.flowsFor()), the face at the end, found... |
| 2792 | 8 | `public static double fairRealCoupon(YieldCurve curve, double[] growth, int months)` | The real coupon that prices a NEW inflation-linked bond of these months at 100 on this curve: the first payment on a ratio of 1, each later month's grown by the growth the market expects (InflationLinkedBond.ratiosFor... |
| 2802 | 3 | `public double fairMargin(int months, double extraFace)` | fairMargin() on the curve a sale of `extraFace` is priced on and the dial the market expects now: the Borrow page's term column. |
| 2807 | 3 | `public double fairRealCoupon(int months, double extraFace)` | fairRealCoupon() on the curve a sale of `extraFace` is priced on and the index the market expects now. |
| 2817 | 4 | `public double breakevenInflation(int months, double extraFace)` | The inflation a linked line prices in (CD5): the nominal par yield of this term less the real coupon that prices a linked bond at 100 - the breakeven the market's two lines disagree by. |
| 2833 | 6 | `public static double amortizingParYield(YieldCurve curve, int years)` | The one coupon that prices an amortizing bond of these years at 100 on a curve: every payment of its schedule at its own month's factor. |
| 2849 | 3 | `public Debt benchmarkLine(int years)` | The bond line a sale at this term goes into (spec-city-debt.md section 2.2, D6, D14): the city's own LongTermBond - not dollar paper, and not a callable one (CD4: a sale that is not callable never reopens a line that ... |
| 2859 | 13 | `public Debt benchmarkLine(int years, boolean callable)` | ...of a callable sale (CD4): a callable sale reopens only a callable line of the same term whose first call sits as far from its start as the sale's would (CallTree.firstCallYears()), and a sale that is not callable n... |
| 2873 | 3 | `public void clearDebts()` |  |
| 2877 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 2884 | 4 | `static { ... }` |  |
| 2890 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

