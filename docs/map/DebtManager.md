# DebtManager.java - 3,521 lines · 281 methods · 43 constants · model

`ham/citybuildersim/DebtManager.java` - generated 2026-11-15 by CodeMap; line numbers are as of that run.

> The city's borrowing: every bond, note and dollar bond the treasury owes,
> the market that prices the next one, and the policy rate every price of
> money in the city is built on - the player's dial, or the rule's with the
> autopilot on (0.7.0).

**Uses:** [Debt](Debt.md) (84), [YieldCurve](YieldCurve.md) (38), [DecisionLog](DecisionLog.md) (14), [CallTree](CallTree.md) (10), [InflationLinkedBond](InflationLinkedBond.md) (8), [LongTermBond](LongTermBond.md) (7), [WorldMoneys](WorldMoneys.md) (7), [FloatingRateNote](FloatingRateNote.md) (6), [ShortTermTBill](ShortTermTBill.md) (5), [CityCalendar](CityCalendar.md) (5), [Auction](Auction.md) (5), [PolicyDates](PolicyDates.md) (4), [ForeignAccounts](ForeignAccounts.md) (4), [AuctionTally](AuctionTally.md) (4), [Ladder](Ladder.md) (4), [CentralBank](CentralBank.md) (4), [Expectations](Expectations.md) (3), [Bank](Bank.md) (2), [MediumTermBond](MediumTermBond.md) (1), [Game](Game.md) (1)

**Used by (54):** [AuctionCheck](AuctionCheck.md), [Bank](Bank.md), [BankCheck](BankCheck.md), [BankScreen](BankScreen.md), [BookDesk](BookDesk.md), [BuybackCheck](BuybackCheck.md), [CallCheck](CallCheck.md), [CapitalFlowCheck](CapitalFlowCheck.md), [CarryTradeCheck](CarryTradeCheck.md), [CentralBankCheck](CentralBankCheck.md), [CityNeeds](CityNeeds.md), [CreditCheck](CreditCheck.md), [CurrencyCheck](CurrencyCheck.md), [CurrencyRegimeCheck](CurrencyRegimeCheck.md), [CurveCheck](CurveCheck.md), [DataSave](DataSave.md), [DebtExchangeCheck](DebtExchangeCheck.md), [DebtMoneysCheck](DebtMoneysCheck.md), [DeskCheck](DeskCheck.md), [EquityCheck](EquityCheck.md), [ExchangeCheck](ExchangeCheck.md), [Expectations](Expectations.md), [ExpectationsCheck](ExpectationsCheck.md), [FinancesScreen](FinancesScreen.md), [FloatLinkCheck](FloatLinkCheck.md), [ForeignDebtCheck](ForeignDebtCheck.md), [FundScreen](FundScreen.md), [Game](Game.md), [GoldStandardCheck](GoldStandardCheck.md), [GovernmentScreen](GovernmentScreen.md), [HoldersCheck](HoldersCheck.md), [HousingCheck](HousingCheck.md), [InterestAbroadCheck](InterestAbroadCheck.md), [KeptCheck](KeptCheck.md), [LongPlaytest](LongPlaytest.md), [MonetaryCheck](MonetaryCheck.md), [MoneyDatesCheck](MoneyDatesCheck.md), [MortgageCheck](MortgageCheck.md), [OwnerCheck](OwnerCheck.md), [PartnersCheck](PartnersCheck.md), [PegAnchorCheck](PegAnchorCheck.md), [Pieces](Pieces.md), [PolicyPreview](PolicyPreview.md), [PolicyPreviewCheck](PolicyPreviewCheck.md), [PolicyScreen](PolicyScreen.md), [ReadPathCheck](ReadPathCheck.md), [RestructureCheck](RestructureCheck.md), [SaveFileCheck](SaveFileCheck.md), [SectorScreen](SectorScreen.md), [SimulationEngine](SimulationEngine.md), [TreasuryCheck](TreasuryCheck.md), [UserInterface](UserInterface.md), [VaultView](VaultView.md), [YieldCurve](YieldCurve.md)

## Sections

| line | section |
|---:|---|
| 30 | THE POLICY RATE |
| 93 | · THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). |
| 136 | · WHAT THE AUTOPILOT AIMS AT, AND THE DIAL UNDER A PEG (XR2, 2026-10-22) |
| 339 | · HOW STRICT (0.7.52) |
| 799 | THE RATE THE FOREIGN PAPER IS VALUED AT |
| 1032 | WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING |
| 1629 | · THE CITY'S BOOKS, IN TOTAL (CD3, spec-city-debt.md section 5) |
| 1735 | · WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) |
| 2049 | THE CURVE (0.7.1) |
| 2197 | · the expectations the curve is built from (CD1) |
| 2481 | · the price index, and the paths a floater and a linker are priced on (CD5) |
| 2598 | · the call (CD4, spec-city-debt.md section 6.4) |
| 2731 | · what the month's close keeps for the debt screens (CD6, spec-city-debt.md section 7) |
| 2883 | · who holds it (0.7.1) |
| 3027 | THE RATE, TAKEN APART - for the Finances screen and nothing else. |
| 3330 | · WHAT THE AUCTION STARTS FROM, AND WHERE A SALE GOES (CD2) |
| 3361 | · FAIR AT THE SALE, FOR A FLOATER AND A LINKER (CD5, spec-city-debt.md |

## Enum constants

| line | constant | says |
|---:|---|---|
| 179 | `DebtManager.Aim.INFLATION` |  |
| 179 | `DebtManager.Aim.CURRENCY` |  |
| 179 | `DebtManager.Aim.MIX` |  |
| 179 | `DebtManager.Aim.GOLD` |  |
| 401 | `DebtManager.Strictness.VERY_LOOSE` |  |
| 402 | `DebtManager.Strictness.LOOSE` |  |
| 403 | `DebtManager.Strictness.STANDARD` |  |
| 404 | `DebtManager.Strictness.STRICT` |  |
| 405 | `DebtManager.Strictness.VERY_STRICT` |  |

## Constants

| line | constant | value | says |
|---:|---|---|---|
| 70 | `DebtManager.MIN_POLICY_RATE` | `.0` | The floor of the dial: no central bank sets a negative nominal rate by typing one. |
| 72 | `DebtManager.MAX_POLICY_RATE` | `1.00` | The top of the dial, 100% a year since 0.7.2 (25% before): a bound that never binds in play, kept so a typo cannot set 2,500%. |
| 185 | `DebtManager.CURRENCY_WEIGHT` | `.25` | A quarter point of rate for each 1% the D$ sits past its target: the open-economy Taylor rule's weight on the exchange rate (the study's star 13, R39). |
| 188 | `DebtManager.GOLD_DEFENCE` | `.10` | The most the dial stands over the US's rate to defend the gold, a fraction a year (10 points; est.: inside the probe's K 10 and K 25, spec-gold-money 3.2). |
| 191 | `DebtManager.GOLD_DEFENCE_FROM` | `2 * ForeignAccounts.COVER_RATIO` | The gold cover the defence starts from: twice the legal floor, where the spread is nothing; it reaches GOLD_DEFENCE at the floor itself. |
| 281 | `DebtManager.NEUTRAL_RATE` | `.03` | Where the rate sits when nobody is leaning on it either way, at the default 2% target: a one-point real rate (ruleRate() adds the target's distance from the default since 0.7.42 - its aim's, at a strict setting, since... |
| 284 | `DebtManager.DEFAULT_INFLATION_TARGET` | `.02` | Where the inflation target opens, and what a save from before the dial reads: two per cent, the constant it was until 0.7.4. |
| 287 | `DebtManager.MAX_INFLATION_TARGET` | `.20` | The top of the target's dial: 20% a year, Jerus's number (0.7.15: "inflation target can be higher tgan 10%, up yo 20%"); it was 10% from 0.7.4. |
| 290 | `DebtManager.MIN_INFLATION_TARGET` | `0` | The bottom of the target's dial: stable prices to the letter - no central bank aims at falling ones. |
| 337 | `DebtManager.TAYLOR_WEIGHT` | `1.5` | How hard the advised rate reacts to inflation missing its target, at Standard - since 0.7.52 the dial beside the target sets another, never as little as one (HOW STRICT, below). |
| 388 | `DebtManager.STRICTEST_AIM` | `Expectations.TOLERANCE` | How far under the target the very strict bank aims, a fraction a year: Expectations.TOLERANCE, the furthest under it inflation can sit and still count as on target - so hitting the aim costs no trust. |
| 391 | `DebtManager.LOOSEST_BAND` | `2 * Expectations.TOLERANCE` | The band over the target the very loose bank lets be, a fraction a year (either side of it until 0.7.81; under it, it cuts as Standard does): twice Expectations.TOLERANCE, so its top is a miss trust counts and the ban... |
| 394 | `DebtManager.STRICTEST_WEIGHT` | `2.0` | The very strict bank's weight on the gap from its aim: TAYLOR_WEIGHT's margin over one, doubled. |
| 397 | `DebtManager.LOOSEST_WEIGHT` | `1.25` | The very loose bank's weight on the gap past its band: TAYLOR_WEIGHT's margin over one, halved - still over one (the Taylor principle). |
| 664 | `DebtManager.MAX_SPREAD_PER_MEASURE` | `0.05` | The most either measure alone can add to the rate. |
| 699 | `DebtManager.FULL_STRESS_MULTIPLE` | `150` | Debt, as a multiple of a year of the thing, at which a measure maxes out. |
| 702 | `DebtManager.MIN_RATE` | `0.005` | The cheapest money the market will ever offer, whatever the books say (public since CD1: YieldCurve clamps every forward at it). |
| 705 | `DebtManager.MIN_FOREIGN_COUPON` | `0` | The lowest coupon a foreign bond is struck at (CD9, 2026-11-08): 0%, as the zero-coupon issues of 2015-22 were; a yield under it is paid in the price, the bond selling over par. |
| 708 | `DebtManager.QUOTE_ITERATIONS` | `6` | How many times to walk the face-value/rate fixed point. |
| 1066 | `DebtManager.WORLD_BASE_RATE` | `.02` | The world's price of money where the city has no world file (and where its world is quieted for the D$): 2%, deliberately below the city's own floor. |
| 1078 | `DebtManager.MAX_COUNTRY_PREMIUM` | `.16` | What the world adds on top of that, at the city's very worst. |
| 1081 | `DebtManager.FULL_STRESS_EXPORT_YEARS` | `8` | USD debt at this many years of exports, and the solvency term maxes out. |
| 1088 | `DebtManager.FULL_STRESS_SERVICE_SHARE` | `.25` | A year's USD bill at this share of a year's exports, and the service term maxes out. |
| 1091 | `DebtManager.SOLVENCY_WEIGHT` | `.60, SERVICE_WEIGHT =.40` | How the two halves of country risk are weighted. |
| 1094 | `DebtManager.WINDOW_SHUT_EXPORT_YEARS` | `14` | Above this many years of exports the window shuts outright. |
| 1097 | `DebtManager.WINDOW_SHUT_SERVICE_SHARE` | `.45` | ...and above this share of exports going out in service, likewise. |
| 1100 | `DebtManager.DEFAULT_SCAR` | `.10` | What a default abroad adds to the premium the day it happens. |
| 1103 | `DebtManager.SCAR_DECAY` | `.9885` | ...and how much of the scar is left after each month. |
| 1674 | `DebtManager.BOOK_KINDS` | `{ "NOTE", "SERIAL", "TERM", "FLOAT", "LINKED" }` | The kinds of paper the interest is booked by, as Debt.getType() calls them: the notes, the serial bonds, the bonds, the floaters, the linkers. |
| 1834 | `DebtManager.LADDER_YEARS` | `12` | How many calendar years the ladder draws before it totals the rest as "later". |
| 1837 | `DebtManager.LADDER_KINDS` | `{ "NOTE", "SERIAL", "TERM" }` | The ladder's instruments, in its order: what Debt.getType() calls each, short to long. |
| 2112 | `DebtManager.TERM_PREMIUM_10Y` | `.0050` | The premium on ten-year money, in points of annual rate: Jerus's numbers to settle, roughly half a point at ten years. |
| 2115 | `DebtManager.TERM_PREMIUM_20Y` | `.0090` | ...on twenty-year money. |
| 2118 | `DebtManager.TERM_PREMIUM_30Y` | `.0115` | ...on thirty-year money. |
| 2121 | `DebtManager.TERM_PREMIUM_40Y` | `.0135` | ...on forty-year money. |
| 2124 | `DebtManager.TERM_PREMIUM_50Y` | `.0150` | ...on fifty-year money, and on anything longer: the long end, a point and a half over the dial. |
| 2127 | `DebtManager.TERM_PREMIUM` | `{ TERM_PREMIUM_10Y, TERM_PREMIUM_20Y, TERM_PREMIUM_30Y, TERM_PREMIUM_40Y, TER...` | The table, at 10, 20, 30, 40 and 50 years: the curve's points (40 is no longer a term a bond is sold at, CD2, but it still shapes the line between 30 and 50). |
| 2695 | `DebtManager.A_POINT` | `0.01` | A point on the curve (CD6): the rise in every rate that valueOfAPoint() prices a piece at, 1% a year. |
| 2742 | `DebtManager.CURVE_TENORS` | `{ 3, 6, 12, 24, 60, 120, 240, 360, 480, 600 }` | The maturities, in months, the curve is drawn and kept at: three months to fifty years. |
| 2745 | `DebtManager.CURVE_KEPT` | `25` | The months of the curve kept: today, a year ago and two years ago. |
| 2748 | `DebtManager.CURVE_ROW` | `2 + 2 * CURVE_TENORS.length` | A curve-book row: the month, the dial, the city's zero rate at each of CURVE_TENORS, the world's at each. |
| 3389 | `DebtManager.FAIR_COUPON_STEPS` | `100` | The halvings of the bisection above: far past what a double keeps of a bracket of one unit of yield. |
| 3502 | `DebtManager.formatter` | `NumberFormat.getNumberInstance(Locale.CANADA)` |  |

## Fields (state)

| line | field | says |
|---:|---|---|
| 21 | `private transient DecisionLog decisions` | Where the player's change to the policy rate, the rule, the inflation target or how strictly the rule holds it is written (DecisionLog, 0.7.23); null for one no city holds. |
| 75 | `private double baseRate` | What the city's central bank charges. |
| 78 | `private boolean rateOpen` | Calendar permissions, derived again after a load; standalone pricing fixtures keep their dials. |
| 124 | `private boolean autopilot` | THE AUTOPILOT (0.7.0): whether the rule holds the dial rather than the player. |
| 180 | `public final String words` |  |
| 193 | `private Aim aim` |  |
| 206 | `private double pegSpread` | Under a peg, what the dial stands over its anchor's rate (the US's for a peg to the US$; a fraction a year, negative under it): the player's to set, to defend the peg. |
| 304 | `private double inflationTarget` | THE TARGET IS A DIAL (0.7.4). |
| 408 | `public final String words` | Its name on the dial. |
| 410 | `public final double slack` | Where the rule starts to act, against the target, a fraction a year: under nothing a strict bank's aim under it, over nothing a loose bank's band over it (either side until 0.7.81). |
| 412 | `public final double weight` | The rule's weight on the gap it answers. |
| 442 | `private Strictness strictness` | How strict the rule is: STANDARD until the player moves it. |
| 620 | `private double currentRate` |  |
| 621 | `private double GDP` |  |
| 633 | `private double monthlyTaxRevenue` | The month's tax take. |
| 645 | `private double overdraft` | How far the city is overdrawn. |
| 710 | `private List<Debt> debts` |  |
| 804 | `private double exchangeRate` | Local money per US$: the rate the D$ stands at, which US$ paper is valued at as it always was. |
| 815 | `private final double[] moneyUsd` | US$ per unit of each of the eight as Game last handed them, EIGHT's order: the US$'s is 1, a money never handed is NaN. |
| 818 | `private final double[] moneyRate` | Each money's world rate (its central bank's policy rate), a fraction a year, EIGHT's order; WORLD_BASE_RATE until handed. |
| 821 | `private final boolean[] offered` | Whether each of the eight has a rate THIS month, so a new piece can be sold in it; the US$'s always does. |
| 1069 | `private double worldRate` | The world's price of money now: WORLD_BASE_RATE until Game hands the US's policy rate (setWorldRate). |
| 1105 | `private double monthlyExports` |  |
| 1106 | `private double importCover` |  |
| 1107 | `private double defaultScar` |  |
| 1108 | `private int monthsSinceForeignDefault` |  |
| 1450 | `private double costOfFunds` | What the bank pays for the money it lends the city. |
| 1506 | `private double advances` | What the treasury owes its central bank in advances (0.7.0), pushed in with the overdraft. |
| 1517 | `private final AuctionTally auctionTally` | THE CITY'S AUCTIONS, COUNTED (CD2, D33): every domestic sale that booked, its tail and its cover, and the last one's figures. |
| 1646 | `private double interestBooked` | This month's interest expense on the city's paper: every piece's carrying value at its effective rate, a month. |
| 1649 | `private double amortisedBooked` | ...and the part of it that is not coupon: the discount amortised, below nothing for a premium. |
| 1656 | `private double upliftBooked` | The month's inflation uplift on the linked bonds (CD5): the indexation each booked as its carrying value caught up with the price index (InflationLinkedBond.index()), summed. |
| 1659 | `private double upliftPaidBooked` | ...and the uplift the month PAID in cash: a linker's indexed principal over its face, in its last month. |
| 1671 | `private final double[] interestByKind` | THE INTEREST BOOKED, BY KIND (CD6): the month's interest expense on each kind of paper, in BOOK_KINDS' order, struck in processAllDebts() beside interestBooked from the same figures, so the five add up to getInterestB... |
| 2200 | `private double struckInflation` | The inflation the rule read when the month's expectations were struck (strikeExpectations()); NaN until something strikes them. |
| 2203 | `private double struckExpected` | ...and the inflation the city expected then (Expectations.getExpectedInflation()); NaN until struck. |
| 2277 | `private transient java.util.function.BooleanSupplier tiedSource` |  |
| 2284 | `private transient java.util.function.Supplier<String> tieMoneySource` |  |
| 2307 | `final double floor, inflation, expected, target, tie` |  |
| 2308 | `final Strictness strictness` |  |
| 2309 | `final double[] path` |  |
| 2317 | `final double[] path` |  |
| 2318 | `final double gdpSpread, revenueSpread, ceiling, share` |  |
| 2319 | `final YieldCurve curve` |  |
| 2326 | `private transient volatile PathEntry pathCache` |  |
| 2327 | `private transient volatile CurveEntry curveCache` |  |
| 2439 | `final YieldCurve curve` |  |
| 2440 | `final double sigma` |  |
| 2441 | `final CallTree tree` |  |
| 2445 | `private transient volatile TreeEntry treeCache` |  |
| 2479 | `private transient java.util.function.DoubleSupplier rateVolatility` |  |
| 2498 | `private transient java.util.function.DoubleSupplier priceIndexSource` |  |
| 2500 | `private transient volatile PathEntry policyPathCache` |  |
| 2534 | `final double inflation, expected, target` |  |
| 2535 | `final double[] growth` |  |
| 2541 | `private transient volatile GrowthEntry growthCache` |  |
| 2623 | `final double rate` |  |
| 2624 | `final double floor` |  |
| 2625 | `final YieldCurve curve` |  |
| 2629 | `private transient volatile WorldEntry worldCache` |  |
| 2750 | `private final java.util.ArrayList<double[]> curveBook` |  |
| 2753 | `private boolean recording` | True: the month's close records the book. |
| 3023 | `private double accretedForBank` | This month's accretion on the bank's share, struck in processAllDebts() before the month's payments; handed to the bank at the settle. |

## Methods, in file order, under their sections

| line | len | member | says |
|---:|---:|---|---|
| 18 | 3504 | **type** `public class DebtManager` | The city's borrowing: every bond, note and dollar bond the treasury owes, the market that prices the next one, and the policy rate every price of money in the city is built on - the player's dial, or the rule's with t... |
| 24 | 1 | `public void recordTo(DecisionLog log)` | Wires this to its city's decision log (Game.buildWorld()). |
| 26 | 3 | `private void decided(String kind, String label)` |  |

### THE POLICY RATE (lines 30-92)

| line | len | member | says |
|---:|---:|---|---|
| 81 | 5 | `public void hearMoneyMonth(int month, boolean onGold)` | Canada had an Advance Rate from 1914; its gold years inherit the world's interest instead. |
| 87 | 1 | `public double getPolicyRate()` |  |
| 89 | 3 | `public void setPolicyRate(double rate)` |  |

### THE FLOOR IS REAL NOW (0.7.0), AND THE CURVE SITS ON IT (0.7.1). (lines 93-135)

| line | len | member | says |
|---:|---:|---|---|
| 126 | 1 | `public boolean isAutopilot()` |  |
| 127 | 8 | `public void setAutopilot(boolean on)` |  |

### WHAT THE AUTOPILOT AIMS AT, AND THE DIAL UNDER A PEG (XR2, 2026-10-22) (lines 136-338)

| line | len | member | says |
|---:|---:|---|---|
| 178 | 5 | **type** `public enum Aim` | What the autopilot sets the dial to hold (XR2; the study's star 13; Defend the gold, GD3, last: saved by name). |
| 181 | 1 | `Aim(String words)` _(in DebtManager.Aim)_ |  |
| 196 | 1 | `public Aim getAim()` | What the autopilot aims at: Inflation unless the player says otherwise. |
| 198 | 6 | `public void setAim(Aim a)` |  |
| 208 | 1 | `public double getPegSpread()` |  |
| 211 | 14 | `public void setPegSpread(double spread)` | Sets the spread, inside what keeps the tied rate on the dial (MIN_POLICY_RATE to MAX_POLICY_RATE over tieRate(): the peg's anchor's rate, the US's for the US$). |
| 227 | 3 | `public double currencyRate(double gap)` | The dial that holds the currency alone: the US's rate plus CURRENCY_WEIGHT for each unit of gap (the D$ past its target, a share, positive weaker), inside the dial's bounds. |
| 232 | 4 | `public double mixRate(double inflation, double gap)` | The inflation rule's dial plus CURRENCY_WEIGHT for each unit of gap: the mix of the two aims, inside the dial's bounds. |
| 238 | 4 | `public double tiedRate(double gap)` | The dial under a peg (star 5): the anchor's rate (tieRate(): the US's for a peg to the US$) plus the spread - the player's, or with the autopilot aiming at the currency (or a mix) the one the gap asks - inside the dia... |
| 244 | 3 | `public static double goldSpread(double cover)` | The spread over the US's rate that defends the gold at this cover (the vault's gold at the mint price over M0): GOLD_DEFENCE x how far the cover is below GOLD_DEFENCE_FROM, as a share of the way down to the legal floo... |
| 254 | 4 | `public double goldRate(double gap, double cover)` | The dial under Gold: the US's rate (tieRate(): the tie money is "USD" whatever the peg's anchor) plus the spread - with the autopilot aiming at the gold, goldSpread(cover); aiming at the currency or a mix, CURRENCY_WE... |
| 260 | 3 | `public void dropGoldAim()` | A break took the city off gold: an autopilot aim at the gold becomes Inflation, with no decision line (a break is the city's fate, not a decision). |
| 269 | 10 | `public void takeTheDial(double rate)` | The player moves the dial by hand, which takes it back from the rule. |
| 307 | 1 | `public double getInflationTarget()` | What the city holds inflation to, a fraction a year: the rule aims at it, or under it at a strict setting (Strictness.aim(), 0.7.52). |
| 310 | 9 | `public void setInflationTarget(double target)` | Sets the target, held between MIN_INFLATION_TARGET and MAX_INFLATION_TARGET; not a number leaves it where it was. |
| 321 | 5 | `public static String targetWords(double target)` | A target as the screens write it: "2%", or "2.5%" on a half point. |

### HOW STRICT (0.7.52) (lines 339-798)

| line | len | member | says |
|---:|---:|---|---|
| 400 | 40 | **type** `public enum Strictness` | The five steps of the dial, loosest first; Strict and Loose sit halfway between Standard and their ends, in both numbers. |
| 414 | 5 | `Strictness(String words, double slack, double weight)` _(in DebtManager.Strictness)_ |  |
| 421 | 3 | `public double aim(double target)` _(in DebtManager.Strictness)_ | What it aims inflation at under a target: the target, or under it by the slack - never under MIN_INFLATION_TARGET, because no central bank aims at falling prices. |
| 426 | 1 | `public double band()` _(in DebtManager.Strictness)_ | How far over its aim it lets inflation be before it acts: nothing, or the slack. |
| 429 | 4 | `public static Strictness at(int step)` _(in DebtManager.Strictness)_ | The step at a place on the dial, 0 the loosest, held inside the ends. |
| 435 | 4 | `public static Strictness named(String name)` _(in DebtManager.Strictness)_ | By its saved name: STANDARD for null or a name this build does not know. |
| 444 | 1 | `public Strictness getStrictness()` |  |
| 447 | 6 | `public void setStrictness(Strictness s)` | Sets how strict the rule is, applied at once like the target; null leaves it where it was. |
| 455 | 7 | `public static String aimWords(double target, Strictness s)` | What a bank this strict aims at under a target, in words: "aims at 2.0%", "aims under 2.0%, at 1.0%", "acts only past 3.0% (or under 2.0%)" - under the target itself since 0.7.81, as Standard does; its band's foot, "(... |
| 464 | 1 | `public String aimWords()` | ...at the city's target and strictness. |
| 467 | 4 | `private static String pct1(double rate)` | A rate to one place, with a true minus: "2.0%", "−1.0%". |
| 489 | 3 | `public double advisedPolicyRate(double inflation)` | What a rule would set, given this month's inflation, held inside the dial's bounds - ruleRate() is the rule before them. |
| 494 | 3 | `public double advisedPolicyRate(double inflation, Strictness s)` | ...at a strictness of the caller's, at the city's target (0.7.52): the Policy tab's preview of the dial (PolicyPreview.ruleAt()) and holdingRate(). |
| 513 | 3 | `public double ruleRate(double inflation)` | What the rule itself says, before the dial's bounds (2026-09-21). |
| 518 | 3 | `public double holdingRate(double inflation)` | ...and what the Standard rule would set at it, held inside the dial's bounds: what holding the target takes, whatever the strictness - the advice Expectations measures the lean against (0.7.52, HOW STRICT). |
| 540 | 3 | `public double ruleRate(double inflation, double target)` | ...and what it would say at a target of the caller's rather than the dial's (0.7.4): the monetary page's sentence, "at 0% it would set 10.5%", struck from the rule rather than restated by the screen. |
| 554 | 8 | `public static double ruleRate(double inflation, double target, Strictness s)` | ...and at a strictness of the caller's (0.7.52): struck at the aim, as the rule is struck at a target, on the gap past the band, at the strictness's weight. |
| 564 | 1 | `public double neutralRate()` | The neutral nominal rate at the city's target: the neutral real rate plus the target - what the Standard rule sets on target, at any strictness (0.7.45, for the screens; Expectations' lean is measured from it). |
| 572 | 25 | `public String adviceReason(double inflation)` | ...and why, in words, for the screen. |
| 599 | 21 | `private String strictReason(double inflation, double advised, String target)` | adviceReason() off Standard (0.7.52): what this strictness aims at, and what the rule says against it - a loose bank's band over its aim only (0.7.81), Standard's rule under it. |
| 712 | 4 | `public DebtManager()` |  |
| 717 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted)` |  |
| 721 | 3 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted, boolean foreign)` |  |
| 725 | 3 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 729 | 4 | `public Debt addMediumTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 734 | 3 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate)` |  |
| 738 | 4 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate, boolean foreign)` |  |
| 747 | 5 | `public Debt addShortTermTBill(double faceValue, int months, int monthStarted, String money)` | A bill written in a money of the eight (CD8): {@code money} null is paper at home, "USD" the dollar bill the boolean form books, another code the same bill in that money. |
| 754 | 5 | `public Debt addLongTermBond(double faceValue, int months, int monthStarted, double rate, String money)` | ...and a bond written in a money of the eight (CD8), as addShortTermTBill(face, months, start, money) books a bill. |
| 761 | 3 | `public Debt addCallableBond(double faceValue, int months, int monthStarted, double rate, int firstCallMonth)` | A bond sold callable (CD4): the right to call it whole at 100 from the game month firstCallMonth on, its coupon the caller's, option included. |
| 770 | 3 | `public Debt addFloatingRateNote(double faceValue, int months, int monthStarted, double margin, double index)` | A floating-rate note (CD5, spec-city-debt.md section 2.4): a coupon at the policy rate plus {@code margin}, the face at the end. |
| 779 | 3 | `public Debt addLinkedBond(double faceValue, int months, int monthStarted, double realCoupon, double baseIndex)` | An inflation-linked bond (CD5, section 2.5): a real coupon on a principal that follows the price index, never under the face at the end. |
| 793 | 5 | `private Debt book(Debt paper)` | The one place paper joins the list, so the one place it can be told the rate. |

### THE RATE THE FOREIGN PAPER IS VALUED AT (lines 799-1031)

| line | len | member | says |
|---:|---:|---|---|
| 823 | 6 | `private static double[] newMoneyUsd()` |  |
| 830 | 5 | `private static double[] newMoneyRates()` |  |
| 836 | 5 | `private static boolean[] newOffered()` |  |
| 843 | 3 | `private static int moneyAt(String code)` | The index of a code in WorldMoneys.EIGHT; 0 for the US$ and for null (paper at home reads as the D$'s own US$ figures), -1 for a code that is none of the eight. |
| 854 | 12 | `public void setMoneyUsd(double[] usdPerUnit)` | Hands the eight's US$ per unit, in WorldMoneys.EIGHT's order (Game does, each month right after the moneys step and at a load): for a money other than the US$, a finite rate above zero is taken and the money is offere... |
| 868 | 6 | `public void setMoneyRates(double[] annual)` | Hands each of the eight's world rate - its central bank's policy rate - as a fraction a year, from index 1 (the US$'s stays on setWorldRate()); a rate that is no number reads WORLD_BASE_RATE. |
| 876 | 4 | `public boolean offers(String code)` | Whether a new piece can be sold in this money now: the US$ always, another of the eight when it has a rate this month, anything else never. |
| 882 | 5 | `public double worldRate(String code)` | The world's price of money in this money: the US$'s (worldRate()) for the US$ and for null, the money's central bank's policy rate (handed) for another; WORLD_BASE_RATE for a code that is none of the eight. |
| 889 | 4 | `public double usdPer(String code)` | US$ per unit of this money as last handed: 1 for the US$ and for null, NaN for a money never handed or a code that is none of the eight. |
| 895 | 5 | `public double rateOf(String code)` | Local money per unit of this money: the D$'s rate to the US$ (getExchangeRate()) for the US$, exactly, and that times its US$ per unit for another; NaN for a money never handed. |
| 902 | 1 | `private double rateFor(Debt d)` | The rate a piece is valued at: its own money's (rateOf()) for foreign paper, the D$'s for paper at home. |
| 909 | 7 | `private double rateFor(Debt d, double usdRate)` | ...the same with the D$'s rate to the US$ given (revalueForeign() asks for the rate a move would leave it at): the D$'s rate itself for a dollar piece and for paper at home, to the bit; that times the money's US$ per ... |
| 918 | 7 | `private double usdPerPiece(Debt d)` | US$ per unit of a foreign piece's money (1 for a dollar piece); a money with no rate handed reads from the piece's own last rate. |
| 936 | 5 | `public void setExchangeRate(double rate)` | Pushed down to every instrument, from the month tick AND from the load path. |
| 942 | 1 | `public double getExchangeRate()` |  |
| 966 | 9 | `public double revalueForeign(double rate)` | THE MONTH'S EXCHANGE GAIN ON THE CITY'S FOREIGN-CURRENCY PAPER (CD7): tells every piece the rate (setExchangeRate()) and returns what the move did to the local value of what the city carries in foreign money, a gain p... |
| 977 | 5 | `public double getForeignPrincipal()` | What the city owes abroad, in local money at today's rate. |
| 987 | 5 | `public double getForeignPrincipalUsd()` | ...and in US$, at the month's cross rates: a dollar piece's principal in the dollars it is actually owed in, which do not move, and a piece in another money its own-money principal times that money's US$ per unit (CD8... |
| 994 | 5 | `public double getForeignPrincipalIn(String code)` | What the city owes in one money of the eight (CD8), in that money's own units: the principal of every foreign piece written in it. |
| 1001 | 5 | `public double getForeignPrincipalLocalIn(String code)` | ...and the same pieces in local money, each at the rate it is valued at (getOustandingPrincipal()): what the books carry them at, for a screen that names one money (CD8). |
| 1008 | 4 | `public boolean hasForeignDebtIn(String code)` | Whether any foreign piece on the books is written in this money (CD8). |
| 1014 | 5 | `public double getDomesticPrincipal()` | What it owes at home. |
| 1021 | 5 | `public double getForeignCouponUsd()` | Next month's coupon bill on the foreign paper, in US$ at the month's cross rates (a dollar piece's in dollars as it is owed; CD8: another money's coupon times its US$ per unit). |
| 1027 | 4 | `public boolean hasForeignDebt()` |  |

### WHAT THE WORLD CHARGES, AND WHEN IT STOPS ANSWERING (lines 1032-1628)

| line | len | member | says |
|---:|---:|---|---|
| 1072 | 1 | `public double worldRate()` | The world's price of money: the US's policy rate where the city has the world file (PolicyRates, XR4), else WORLD_BASE_RATE. |
| 1075 | 1 | `public void setWorldRate(double rate)` | Hands the world's price of money (Game does, each month and at a load): the US's policy rate; WORLD_BASE_RATE for a rate that is no number. |
| 1116 | 4 | `public void setTrade(double monthlyExportsLocal, double cover)` | The two figures the world prices the city on, handed down each month. |
| 1121 | 1 | `public double getMonthlyExports()` |  |
| 1122 | 1 | `public double getMonthlyTaxRevenue()` |  |
| 1129 | 12 | `public double repudiateForeignDebt()` | Tears every piece of foreign paper off the books. |
| 1141 | 1 | `public double getImportCover()` |  |
| 1144 | 3 | `public double solvencyStress()` | Can it pay at all: USD debt against a year of exports. |
| 1157 | 6 | `public double solvencyStressAt(double owed)` | ...priced with a proposed bond already on the books. |
| 1185 | 3 | `public double serviceStress()` | Can it pay NOW: next year's USD bill against next year's export earnings. |
| 1189 | 6 | `public double serviceStressAt(double service)` |  |
| 1197 | 9 | `public double nextYearService()` | Everything foreign paper demands over the next twelve months, in local money. |
| 1208 | 3 | `public double countryPremium()` | What the world adds to its base rate for lending to THIS city. |
| 1212 | 6 | `public double countryPremiumAt(double owed)` |  |
| 1220 | 3 | `public double foreignRate()` | The all-in annual rate on a new USD bond: the short end of the world's curve (foreignCurveRate()). |
| 1228 | 3 | `public double foreignRate(String code)` | ...in a money of the eight (CD8): that money's world rate (its central bank's policy rate, worldRate(code)) plus the city's country premium, which stays ONE figure for the city - its local foreign principal and servic... |
| 1250 | 3 | `public double foreignCurveRate(int months)` | THE WORLD'S CURVE (0.7.2): what the world charges this city for dollar paper of this many months - the foreign rate, plus the SAME term premium table the city's own curve carries (termPremium()). |
| 1255 | 3 | `public double foreignCurveRate(String code, int months)` | ...in a money of the eight (CD8): its foreignRate(code) plus the same term premium table - the one shape for every money. |
| 1264 | 3 | `public double quoteForeignRate(double extraUsd)` | ...quoted with the proposed bond priced in. |
| 1269 | 3 | `public double quoteForeignRate(double extraUsd, int months)` | ...and at a maturity, on the world's curve (0.7.2): the quote plus the term premium for those months. |
| 1277 | 4 | `public double quoteForeignRate(String code, double extraOwn)` | ...in a money of the eight (CD8), the proposed face in that money's own units, priced into the premium in local money at its rate (rateOf()). |
| 1283 | 3 | `public double quoteForeignRate(String code, double extraOwn, int months)` | ...and at a maturity, on that money's curve: the quote plus the term premium for those months. |
| 1307 | 11 | `public double quoteForeignRate(Debt proposed, int months)` | ...and priced with the proposed paper ON THE BOOKS, its own next year of service included (0.7.2): the rate the world's curve will read for this paper, at this maturity, the moment it is booked. |
| 1326 | 3 | `public boolean foreignWindowOpen()` | Whether anybody abroad is still willing to lend. |
| 1331 | 21 | `public String foreignWindowReason()` | Why it is shut, in words, or null if it is open. |
| 1354 | 4 | `public void markForeignDefault()` | Called the month the city fails to pay abroad. |
| 1360 | 5 | `public void ageForeignStanding()` | The scar fades, slowly, and the window reopens before the price does. |
| 1366 | 1 | `public double getDefaultScar()` |  |
| 1367 | 1 | `public int getMonthsSinceForeignDefault()` |  |
| 1370 | 3 | `public double[] foreignStandingToSave()` | Carried, because a scar that heals on reload is not a scar. |
| 1374 | 5 | `public void restoreForeignStanding(double[] saved)` |  |
| 1380 | 40 | `public void printDebtInfo(int currentMonth)` |  |
| 1429 | 3 | `public double getRate()` | THE CITY'S RATE: the SHORT END of the curve - the note's rate, no term premium - which is what every screen means by "the city's rate" and what the advisor reads. |
| 1451 | 1 | `public void setCostOfFunds(double rate)` |  |
| 1452 | 1 | `public double getCostOfFunds()` |  |
| 1469 | 7 | `public double getAllPrincipal()` | Total outstanding principal across every live debt. |
| 1478 | 3 | `public void setGDP(double GDP)` | setters |
| 1483 | 3 | `public void setTaxRevenue(double monthlyTaxRevenue)` | The month's tax take - the other half of what the market prices against. |
| 1493 | 3 | `public void setCashPosition(double cash)` | How far the city is overdrawn, pushed in from Game each month. |
| 1497 | 1 | `public double getOverdraft()` |  |
| 1508 | 1 | `public void setAdvances(double owed)` |  |
| 1509 | 1 | `public double getAdvances()` |  |
| 1519 | 1 | `public AuctionTally getAuctionTally()` |  |
| 1522 | 1 | `public int auctionSales()` | Sales the treasury has booked at auction. |
| 1524 | 1 | `public int auctionTailsOverPoint()` | ...of them, the ones that cleared more than a point over fair (Auction.MAX_TAIL). |
| 1526 | 1 | `public int auctionShortSales()` | ...and the ones whose bids did not reach the issue at the city's cap. |
| 1528 | 1 | `public double largestTail()` | The largest tail any of them cleared at, in yield. |
| 1530 | 1 | `public AuctionTally.Last lastAuction()` | The sale last booked, or null before the first. |
| 1548 | 4 | `public double[] marketToSave()` | THE MARKET'S LAST STRIKE, for the save (0.7.14): the four inputs it was last handed - a month's output, a month's tax, the overdraft, the advances - and the rate it struck on them. |
| 1554 | 9 | `public void restoreMarket(double[] saved)` | ...and back, over the load path's own strike; an older save has none and keeps that strike. |
| 1564 | 3 | `public List<Debt> getDebt()` |  |
| 1568 | 60 | `public void processAllDebts(Game game)` |  |

### THE CITY'S BOOKS, IN TOTAL (CD3, spec-city-debt.md section 5) (lines 1629-1734)

| line | len | member | says |
|---:|---:|---|---|
| 1661 | 1 | `public double getInterestBooked()` |  |
| 1662 | 1 | `public double getAmortisedBooked()` |  |
| 1677 | 4 | `static int bookKind(String type)` | A type's place in BOOK_KINDS; a type that is none of them is a term loan's, as ladderKind() reads it. |
| 1683 | 1 | `public double getInterestBookedOf(String type)` | The month's interest expense on one kind of paper (a BOOK_KINDS entry), in local money: the Government tab's row of the budget's interest line. |
| 1686 | 1 | `double[] interestByKindToSave()` | The interest by kind as a save carries it: a copy, in BOOK_KINDS' order. |
| 1689 | 4 | `void restoreInterestByKind(double[] saved)` | ...and put back by a load; null or the wrong length (an older save) reads as zeros. |
| 1695 | 1 | `public double getUpliftBooked()` | The month's inflation uplift on linked bonds, booked (CD5): an expense, not cash. |
| 1698 | 1 | `public double getUpliftPaid()` | ...and the part of it the month paid in cash (a linker's last month): in the cash coupons the holders were paid, and the uplift line's, not the interest line's. |
| 1706 | 1 | `public double getCouponBooked()` | The coupons the month's books paid: its interest expense less what was amortised. |
| 1709 | 5 | `public double getCarryingValue()` | What the city's paper is carried at, every piece together, in local money: what the balance sheet shows (face less the unamortised discount, plus the premium). |
| 1716 | 1 | `public double getFairValue()` | What the city's paper is worth on the market today, every piece together: the note a government discloses and does not book (getTotalMarketValue()). |
| 1727 | 7 | `public double getNotePrincipal()` | Face value of the discount notes outstanding. |

### WHAT THE FINANCES TAB DRAWS THE DEBT FROM (0.7.32) (lines 1735-2048)

| line | len | member | says |
|---:|---:|---|---|
| 1755 | 5 | `public double getMonthlyCoupon()` | What the paper pays in coupon next month, in cash, every piece together, in local money: the pieces' own getMonthlyCoupon(), summed - the Finances tab's COUPON. |
| 1762 | 5 | `public double getUpliftDueNextMonth()` | The inflation uplift the linked bonds pay next month, with their last coupon, every piece together, in local money (CD6): cash beside getMonthlyCoupon(), and the uplift line's in the budget. |
| 1769 | 5 | `public double getPrincipalOf(String type)` | Principal outstanding of one kind of paper, by Debt.getType() - "NOTE", "SERIAL" or "TERM" - at home and abroad, in local money. |
| 1781 | 5 | `public double getPrincipalOfLadderKind(int kind)` | Principal outstanding of one kind as the ladder draws it (CD5; LADDER_KINDS' index, 0 the notes, 1 the serial bonds, 2 the bonds): a floating-rate note's face and an inflation-linked bond's are the bonds', as ladderKi... |
| 1793 | 8 | `public double dueWithin(int months)` | Everything the city's paper asks of it over the next `months` months, coupons and principal together, in local money - the first `months` of every piece's remainingCashFlows(), the ladder's own read: the Debt service ... |
| 1803 | 10 | `public double dueAbroadWithinUsd(int months)` | ...of the foreign paper alone, in US$ at the month's cross rates (0.7.40; a dollar piece's in the dollars it is owed in, another money's in its own units times its US$ per unit, CD8): the first `months` of each foreig... |
| 1823 | 4 | `public double valuationRate(Debt paper)` | The one flat rate at which a piece's payments price to marketValue(): its yield to maturity on the price a buyback pays (CD1; until then the curve read at the months it has left, which was the rate marketValue() had d... |
| 1829 | 3 | `public double underFace(Debt paper)` | What buying a piece back today would save against its face: its principal less marketValue() - below nothing when it costs a premium to retire (the Finances tab's book, which subtracted them itself). |
| 1840 | 3 | `public static int ladderKind(String type)` | A kind's place in LADDER_KINDS: a note 0, a serial 1, anything else - a term loan - 2. |
| 1854 | 9 | **type** `public record Rung(int fromYear, int toYear, int fromMonth, int toMonth, double[] byKind, double[] abroadBy...` | One rung of the ladder: a calendar year of payments, from its first month on the ladder to its last - a whole year, but the first, which starts next month, and the one the last payment falls in, which ends with it; bo... |
| 1857 | 1 | `public double owed()` _(in DebtManager.Rung)_ | What the city's paper asks in it. |
| 1859 | 1 | `public double abroad()` _(in DebtManager.Rung)_ | ...of which in dollars. |
| 1861 | 1 | `public double total()` _(in DebtManager.Rung)_ | ...and with the proposed issue's. |
| 1870 | 4 | **type** `public record Ladder(List<Rung> years, Rung later, int heaviest, double owed, double proposed)` | The ladder: LADDER_YEARS calendar years from next month's, and what falls after them as one "later" rung (null when nothing does); which of the years is the heaviest (-1 with nothing owed in them) - never "later", whi... |
| 1872 | 1 | `public double total()` _(in DebtManager.Ladder)_ | What is owed and what the proposed issue would ask, together. |
| 1876 | 1 | `public Ladder ladder(int month)` | The ladder as the city owes it, struck in `month`. |
| 1892 | 50 | `public Ladder ladder(int month, double[] extra)` | WHEN IT FALLS DUE, BY THE CALENDAR YEAR IT IS PAID IN (0.7.32, the Finances spec's D4). |
| 1950 | 7 | `public double getPricedDebt()` | Everything the city owes, including what it is overdrawn and what it owes the central bank in advances. |
| 1964 | 7 | `public double getIndexationOwed()` | What the linked bonds owe past their face today (CD5): each one's face times how far its ratio is over 1 - nothing for a ratio at or under 1, because the deflation floor means the face is owed whatever the index did. |
| 1983 | 3 | `public boolean retire(Debt debt)` | Takes one bond off the books. |
| 1988 | 7 | `public double getTotalMarketValue()` | What every outstanding bond would cost to buy back today: marketValue() of each, every payment at its own month's factor on the curve. |
| 2008 | 12 | `private double spreadFor(double debt, double annualCapacity)` | What one measure adds to the rate: a linear ramp, then flat. |
| 2044 | 4 | `private double priceAt(double debt)` | The curve itself: a floor, plus up to ten points from each measure. |

### THE CURVE (0.7.1) (lines 2049-2196)

| line | len | member | says |
|---:|---:|---|---|
| 2138 | 8 | `public static double termPremium(int months)` | What a lender adds for tying money up this many months, before the central bank compresses any of it. |
| 2160 | 7 | `public double compression(int months)` | What the central bank's holdings take off the premium at this many months: the premium, times the share of the city's term paper it actually holds (not its target) over CentralBank.FULL_COMPRESSION_SHARE, times Centra... |
| 2175 | 5 | `public double compressionAt(double share, int months)` | ...at a share of the term paper of the caller's (0.7.36): what holding `share` would take off the premium at this many months, compression()'s rule with the share given - the Policy tab's holdings dial, whose thirty-y... |
| 2187 | 3 | `public double curveRateAtShare(int months, double share)` | The curve at a maturity with the central bank holding `share` of the term paper (0.7.36): curveRate()'s credit judgement and premium, less compressionAt() that share - the thirty-year rate "once the book has moved" to... |
| 2192 | 4 | `private double termShapeAt(double share, int months)` | The premium less the compression at a central-bank share of the term paper: the curve's shape over the short end. |

### the expectations the curve is built from (CD1) (lines 2197-2480)

| line | len | member | says |
|---:|---:|---|---|
| 2217 | 4 | `public void strikeExpectations(double inflationNow, double expectedInflation)` | Strikes the two figures the curve's expected dial is built from: the inflation the rule reads (this month's) and the inflation the city expects (where it settles). |
| 2223 | 3 | `public boolean isStruck()` | True once the curve's expectations have been struck and are numbers. |
| 2228 | 1 | `public double getStruckInflation()` | The inflation the rule read when the month struck the curve's expectations, a fraction a year; NaN unstruck. |
| 2231 | 1 | `public double getStruckExpected()` | ...and the inflation the city expected then; NaN unstruck. |
| 2244 | 3 | `public double ruleNow()` | WHAT THE RULE WOULD SET THE DIAL TO AT TODAY'S INFLATION (CD6): the rule the market expects the dial to close on (YieldCurve.expectedPath()'s R(0)), advisedPolicyRate() at the inflation the month struck; the dial itse... |
| 2254 | 3 | `public double settledDial()` | ...and where the market expects the dial to settle (CD6): the rule at the inflation the city expects it to settle at, which the path approaches with the gap closed (expectedPath()'s limit); the dial when nothing is st... |
| 2267 | 4 | `public boolean isTied()` | IS THE DIAL TIED TO THE ANCHOR'S RATE (XR5, PRICE ON THE TIED PATH)? |
| 2273 | 3 | `public void setTiedSource(java.util.function.BooleanSupplier source)` | Hands the manager whether the city's dial is tied to its anchor's rate (Game: the peg is the regime in force), so it can price the tied path without knowing the currency. |
| 2280 | 3 | `public void setTieMoneySource(java.util.function.Supplier<String> source)` | Hands the manager the code of the money the peg is kept in (Game: ForeignAccounts.getPegAnchorCode(), WN4b), so the tied dial reads that money's central bank's rate. |
| 2287 | 4 | `public String tieMoney()` | The money the dial is tied to (WN4b): the peg's anchor's code while the dial is tied (isTied()); null when it is not, or nobody handed a source. |
| 2293 | 3 | `public double tieRate()` | The rate the tied dial stands over (WN4b): the world's rate in the anchor's money, worldRate(tieMoney()) - the US's for a peg to the US$ and for a dial not tied, to the bit. |
| 2306 | 9 | **type** `private static final class PathEntry` | The expected dial's path and the curve built on it are CACHED, because marketValue() runs for every piece several times a month and quoteRate() walks a fixed point that builds a curve in each pass. |
| 2310 | 4 | `PathEntry(double floor, double inflation, double expected, double target, double tie, Strictness strictness, double[] path)` _(in DebtManager.PathEntry)_ |  |
| 2316 | 9 | **type** `private static final class CurveEntry` |  |
| 2320 | 4 | `CurveEntry(double[] path, double gdpSpread, double revenueSpread, double ceiling, double share, YieldCurve curve)` _(in DebtManager.CurveEntry)_ |  |
| 2329 | 3 | `private static boolean same(double a, double b)` |  |
| 2342 | 21 | `private double[] pathFor(double floor)` | The dial the market expects from this floor: the rule's path from the struck expectations, or the floor held flat when nothing is struck - and since XR5, with the dial tied to its anchor's rate (isTied(), a peg), the ... |
| 2371 | 16 | `private YieldCurve curveAt(double debt)` | The curve this debt would be priced on: the expected dial plus the credit judgement at this debt (spreadFor() on a year of output and a year of tax, clamped at the ceiling), the shape the term premium and the central ... |
| 2389 | 3 | `public YieldCurve curve()` | THE CURVE (CD1): the city's own, on what it owes now - a discount factor for every month to fifty years. |
| 2399 | 3 | `public double curveRate(int months)` | THE CURVE: what the city's paper of this many months is worth to a lender today - the zero rate at that maturity, on what the city owes now: the compounded forward rates of the dial the market expects, plus the shape. |
| 2413 | 11 | `public double marketValue(Debt paper)` | What this paper would fetch today: the present value of what it still owes, EACH PAYMENT AT ITS OWN MONTH'S FACTOR (CD1) - the city's curve() for its own paper, the world's (worldCurveAt()) for a dollar bond since 0.7... |
| 2433 | 4 | `private double callableValue(LongTermBond bond, YieldCurve curve, double[] flows)` | A callable bond's value (CD4): what the tree prices its remaining payments at on today's curve, with the city's right to call it at its principal from the first call month on (LongTermBond.monthsToFirstCall()), at the... |
| 2438 | 6 | **type** `private static final class TreeEntry` |  |
| 2442 | 1 | `TreeEntry(YieldCurve curve, double sigma, CallTree tree)` _(in DebtManager.TreeEntry)_ |  |
| 2454 | 7 | `private CallTree treeOn(YieldCurve curve, double sigma, int months)` | The tree fitted to this curve at this volatility, long enough for a schedule of this many months: built once and kept while the curve (one object while its inputs stand, curveAt()) and the volatility stand, and refitt... |
| 2468 | 4 | `public double rateVolatility()` | The volatility of the city's short rate, a year, that a callable piece is valued at: Game's measure of its own history (Game.rateVolatility(), handed in with setRateVolatility()), the floor (CallTree.RATE_VOL_FLOOR) f... |
| 2474 | 4 | `public void setRateVolatility(java.util.function.DoubleSupplier source)` | Hands the manager the city's volatility, so it can value a callable piece without knowing the city's history. |

### the price index, and the paths a floater and a linker are priced on (CD5) (lines 2481-2597)

| line | len | member | says |
|---:|---:|---|---|
| 2488 | 4 | `public double priceIndexLevel()` | The city's price index now (PriceIndex.getIndex()), as a linked bond is indexed on it: what Game hands in with setPriceIndexSource(), read afresh each time so a reloaded index is the one read. |
| 2494 | 3 | `public void setPriceIndexSource(java.util.function.DoubleSupplier source)` | Hands the manager the price index, so it can index a linked bond without knowing the city's prices. |
| 2511 | 21 | `public double[] expectedPolicyPath()` | THE DIAL THE MARKET EXPECTS FROM THE POLICY RATE, t = 0 the dial now (CD5): what a floating-rate note's coupons are priced on - the same expected path the curve is built on (YieldCurve.expectedPath()), started from th... |
| 2533 | 7 | **type** `private static final class GrowthEntry` |  |
| 2536 | 3 | `GrowthEntry(double inflation, double expected, double target, double[] growth)` _(in DebtManager.GrowthEntry)_ |  |
| 2551 | 19 | `public double[] expectedInflationGrowth()` | THE PRICE INDEX THE MARKET EXPECTS, month by month (CD5): entry j is the growth of the index in the month j months ahead, (1 + pi(j))^(1/12), with pi(j) = expected + (now - expected) x e^(-j / Expectations.ADAPT_MONTH... |
| 2577 | 7 | `public double[] pricingFlows(Debt paper)` | The payments the market prices a piece on (CD5): a floating-rate note's coupons at the dial it expects (expectedPolicyPath()), a linked bond's on the index it expects (expectedInflationGrowth(), the next payment on th... |
| 2593 | 4 | `public double realYieldOf(InflationLinkedBond paper)` | The real yield of a linked bond at its market value (CD5): the known schedule - the real coupon on the principal as the index stands now, held flat, and the indexed principal at the end - solved for the yield that mak... |

### the call (CD4, spec-city-debt.md section 6.4) (lines 2598-2730)

| line | len | member | says |
|---:|---:|---|---|
| 2607 | 4 | `public static double callSavingOn(YieldCurve curve, Debt paper, double fee)` | The present value a call would save, per the study: the line's remaining payments at this curve, as a bond that cannot be called, less what the call pays (its principal) and less what selling the paper again costs (th... |
| 2613 | 3 | `public double callSaving(Debt paper, double fee)` | callSavingOn() the city's curve. |
| 2618 | 3 | `public static boolean worthCalling(double saving, double principal)` | True when a saving passes CallTree.WORTH_CALLING of the principal called (D24). |
| 2622 | 6 | **type** `private static final class WorldEntry` |  |
| 2626 | 1 | `WorldEntry(double rate, double floor, YieldCurve curve)` _(in DebtManager.WorldEntry)_ |  |
| 2645 | 3 | `public YieldCurve worldCurveAt(double shortRate)` | THE WORLD'S CURVE as a YieldCurve (CD1): the world's short rate held flat, and the term premium table (termPremium()) on top - the one definition of what a dollar payment this many months away is discounted by, for th... |
| 2658 | 8 | `public YieldCurve worldCurveAt(String code, double shortRate)` | ...in a money of the eight (CD9, 2026-11-08), floored at THAT money's own policy rate (worldRate(code)), not at the city's MIN_RATE: the city's floor is the cheapest it will lend at home, and a franc sold abroad at 0.... |
| 2668 | 3 | `public double foreignFactor(int months)` | What a dollar payment this many months away is discounted by: (1 + foreignCurveRate(months)/12)^(-months), on worldCurveAt(foreignRate()). |
| 2673 | 3 | `public double foreignFactor(String code, int months)` | ...in a money of the eight (CD8): on worldCurveAt(code, foreignRate(code)), that money's level. |
| 2678 | 9 | `public double yieldOf(Debt paper)` | The yield to maturity of a piece on the price a buyback pays: a RESULT of marketValue(), not the rate it was priced at (CD1). |
| 2689 | 4 | `public double priceOfPar(Debt paper)` | Where a piece trades against par, as a percentage of what is still owed on it: marketValue() over its outstanding principal x 100; 0 when nothing is owed. |
| 2708 | 12 | `public double valueOfAPoint(Debt paper)` | WHAT A POINT MOVES (CD6): what the piece's worth falls by if every rate on its curve (the world's for dollar paper, as marketValue() uses) rises a point, A_POINT - its pricing flows (pricingFlows()) worth at the curve... |
| 2726 | 4 | `public double durationOf(Debt paper)` | ...and the piece's duration, in years (CD6): valueOfAPoint() over its worth and the point, V / (marketValue x A_POINT) - the finite difference, the one definition. |

### what the month's close keeps for the debt screens (CD6, spec-city-debt.md section 7) (lines 2731-2882)

| line | len | member | says |
|---:|---:|---|---|
| 2755 | 1 | `void setRecordingForTest(boolean on)` |  |
| 2762 | 9 | `public void recordTheBook(int month)` | Records the month's close: every piece's price against par and its carrying value per 100 of what is owed (Debt.recordPath()), then the curve (the zero rates at CURVE_TENORS, the dial and the world's rates) in the cur... |
| 2772 | 17 | `private void recordCurve(int month)` |  |
| 2791 | 6 | `private double[] curveRowAgo(int months)` | The row recorded this many months before the newest, or null when none was: the book is read by the month, not by place. |
| 2799 | 4 | `public double[] curveMonthsAgo(int months)` | The city's zero rates at CURVE_TENORS as the curve stood this many months before the newest month recorded (0 the newest); null when that month was not kept - none recorded, or more than CURVE_KEPT - 1 months back. |
| 2805 | 4 | `public double[] worldCurveMonthsAgo(int months)` | ...and the world's, at the same tenors (foreignCurveRate()); null likewise. |
| 2811 | 4 | `public double policyRateMonthsAgo(int months)` | ...and the dial that month; NaN when that month was not kept. |
| 2817 | 1 | `public int curveBookSize()` | How many months the curve book holds. |
| 2820 | 5 | `double[][] curveBookToSave()` | The curve book as a save carries it: a copy, one row a month, oldest first - [month, dial, the city's rates, the world's]. |
| 2827 | 12 | `void restoreCurveBook(double[][] saved)` | ...and put back by a load; null (an older save) or a row of the wrong length (a changed tenor list) is not kept, and the book is built afresh from the next close. |
| 2841 | 3 | `public static boolean isTermPaper(Debt paper)` | True for the city's own term paper - serial and term, not the notes: what the central bank's dial holds. |
| 2846 | 5 | `public double termPrincipal()` | The city's own term paper outstanding, at face: the base of the holdings dial. |
| 2853 | 9 | `public double centralBankShareOfTerm()` | The share of it the central bank holds - the compression's measure. |
| 2873 | 9 | `public double centralBankShareOfPaper()` | The central bank's share of ALL the city's own paper outstanding, at face, every maturity - the notes it never buys in the denominator - the floor's measure (0.7.15, floorRate()). |

### who holds it (0.7.1) (lines 2883-3026)

| line | len | member | says |
|---:|---:|---|---|
| 2886 | 5 | `public double householdPrincipal()` | What the city's households hold of its own paper, at face. |
| 2893 | 5 | `public double centralBankPrincipal()` | ...the central bank, at face. |
| 2900 | 5 | `public double firmPrincipal()` | ...the businesses, at face (CD2, slice 4). |
| 2907 | 5 | `public double worldPrincipal()` | ...the world, at face (CD2, slice 5). |
| 2914 | 5 | `public double bankPrincipal()` | ...and the commercial bank: the domestic principal less the other four. |
| 2927 | 5 | `public double bankBook()` | ...of which the paper the bank has PAID for: what its book carries. |
| 2934 | 15 | `public double[] bookValues()` | Each holder's book at the curve: 0 the households, 1 the central bank, 2 the bank, 3 the businesses, 4 the world. |
| 2955 | 4 | `public double householdBookRatio()` | The households' book at market over its face: ONE RATIO A MONTH, which is what their paper counts for in their net worth and what the desk pays them for it. |
| 2966 | 4 | `public double firmBookRatio()` | The businesses' book at market over its face (CD2, slice 4): the households' twin, ONE RATIO A MONTH - what a dollar of face of their paper is worth, what the desk pays them for it. |
| 2972 | 10 | `public double firmBookYield()` | What the businesses' paper yields at today's curve: the households' twin, weighted by what the businesses hold of each piece; the short rate when they hold none. |
| 2989 | 4 | `public double worldBookRatio()` | The world's book at market over its face (CD2, slice 5): the households' twin, one ratio a month - what a dollar of face of the paper it holds is worth at the curve, which is what the auction counts of it beside its h... |
| 2995 | 10 | `public double householdBookYield()` | What the households' paper yields at today's curve (each piece's yieldOf(), its yield to maturity on the price a buyback pays), weighted by what they hold of each piece; the short rate when they hold none. |
| 3013 | 8 | `public double bankUnearnedDiscount()` | The discount the bank has not yet earned on what it holds: every piece's unaccreted remainder, in the share of its principal the bank holds. |
| 3025 | 1 | `public double getAccretedForBank()` |  |

### THE RATE, TAKEN APART - for the Finances screen and nothing else. (lines 3027-3329)

| line | len | member | says |
|---:|---:|---|---|
| 3053 | 5 | `public double rateAtPolicy(double policy)` | What the city would be quoted if the policy rate were this instead. |
| 3065 | 1 | `public double baseComponent()` | The floor everybody pays: the policy rate, or the bank's cost of funds if that is higher - on the share of the city's paper the bank and the households hold, since 0.7.15; the central bank's share is priced at the pol... |
| 3068 | 1 | `public double gdpSpread()` | What the debt costs against the size of the economy. |
| 3071 | 1 | `public double revenueSpread()` | ...and against what the city can actually collect. |
| 3074 | 3 | `public double gdpStress()` | How much of the worst case each measure has used up, 0 to 1. |
| 3078 | 3 | `public double revenueStress()` |  |
| 3083 | 1 | `public double overTheWorld()` | What the city's own rate is over the world's (0.7.36): the short end against worldRate() - the difference hot money follows. |
| 3086 | 1 | `public double overTheWorldAt(double policy)` | ...at a policy rate of the caller's: rateAtPolicy() against the world's. |
| 3089 | 1 | `public static double maxSpreadPerMeasure()` | The most either measure can add on its own. |
| 3092 | 1 | `public static double fullStressMultiple()` | Years of GDP, or of revenue, at which a measure has said all it can. |
| 3095 | 1 | `public double annualCapacityGdp()` | A year of output, as the market is pricing it. |
| 3098 | 1 | `public double annualCapacityRevenue()` | ...and a year of tax, likewise. |
| 3101 | 3 | `public boolean atCeiling()` | True when the quoted rate is pinned at the top of the curve. |
| 3116 | 3 | `public double expectedDialOver(int months)` | The dial the market expects, averaged over this many months, and nothing else in the rate: the compounded forwards less the credit spread. |
| 3121 | 3 | `public double creditSpread()` | What the credit judgement adds to the forward, as the curve carries it: the two spreads, or the room under the ceiling if less - the same in every month, the middle term of curveRate(m) == expectedDialOver(m) + credit... |
| 3126 | 3 | `public double expectedDialIn(int monthsAhead)` | The dial the market expects this many months ahead (0 this month: the floor). |
| 3174 | 3 | `public double floorRate()` | What a spotless city pays: the policy rate - since 0.7.0, see THE FLOOR IS REAL NOW - but never less than the money costs the bank that lends it. |
| 3185 | 6 | `private double floorAt(double policy)` | ...at a policy rate of the caller's: the one definition floorRate() and rateAtPolicy() both read, so the Policy tab's "what moving the dial does" splits the floor as the market does. |
| 3199 | 3 | `public double bankFloorRate()` | The bank's floor, unsplit: the policy rate, or the bank's cost of funds plus Bank.MIN_MARGIN if that is higher - what the share of the city's paper the central bank does not hold is priced at, and the whole floor unti... |
| 3204 | 3 | `public double ceilingRate()` | What a hopeless one pays - both measures maxed out. |
| 3215 | 10 | `public void updateInterest()` | Re-prices the standing rate off what the city owes right now. |
| 3246 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf)` | What a NEW loan of this size would cost - priced with itself included. |
| 3258 | 3 | `public double quoteRate(double requested, java.util.function.DoubleUnaryOperator faceOf, int months)` | ...at a maturity (0.7.1): the same fixed point, read as the curve's zero rate for that many months (none: the short rate). |
| 3263 | 3 | `public double quoteRate(double requested)` | Straight-line version for instruments whose face value IS the request. |
| 3268 | 3 | `public double quoteRate(double requested, int months)` | ...at a maturity. |
| 3287 | 11 | `public YieldCurve quoteCurve(double requested, java.util.function.ToDoubleFunction<YieldCurve> faceOf)` | THE CURVE A NEW LOAN IS PRICED ON, with itself included (CD1): the fixed point quoteRate() walks, returned as the whole curve instead of one rate read off it. |
| 3300 | 3 | `public YieldCurve quoteCurve(double faceIsRequest)` | ...for an instrument whose face value IS the request: one pass, the loan priced in. |
| 3319 | 10 | `private double debtAfterProceedsOf(double received)` | The debt the loan lands ON TOP OF - which is not simply what is owed now. |

### WHAT THE AUCTION STARTS FROM, AND WHERE A SALE GOES (CD2) (lines 3330-3360)

| line | len | member | says |
|---:|---:|---|---|
| 3346 | 3 | `public double fairYield(Auction.Kind kind, int months, double extraFace)` | The yield an auction of this paper starts from - fair (spec-city-debt.md section 4): what the curve says the paper is worth with `extraFace` of new debt on the books (quoteCurve(): the issue is priced into the credit ... |
| 3351 | 9 | `public static double fairYieldOn(YieldCurve curve, Auction.Kind kind, int months)` | ...read off a curve already in hand: the one the sale is priced on (Game.runAuction() asks quoteCurve() once and reads both from it). |

### FAIR AT THE SALE, FOR A FLOATER AND A LINKER (CD5, spec-city-debt.md (lines 3361-3521)

| line | len | member | says |
|---:|---:|---|---|
| 3379 | 8 | `public static double fairMargin(YieldCurve curve, double[] policyPath, int months)` | The margin over the policy rate that prices a floating-rate note of these months at 100 on this curve: each coupon at the dial the path expects when it is paid (FloatingRateNote.flowsFor()), the face at the end, found... |
| 3401 | 8 | `public static double fairRealCoupon(YieldCurve curve, double[] growth, int months)` | The real coupon that prices a NEW inflation-linked bond of these months at 100 on this curve: the first payment on a ratio of 1, each later month's grown by the growth the market expects (InflationLinkedBond.ratiosFor... |
| 3411 | 3 | `public double fairMargin(int months, double extraFace)` | fairMargin() on the curve a sale of `extraFace` is priced on and the dial the market expects now: the Borrow page's term column. |
| 3416 | 3 | `public double fairRealCoupon(int months, double extraFace)` | fairRealCoupon() on the curve a sale of `extraFace` is priced on and the index the market expects now. |
| 3426 | 4 | `public double breakevenInflation(int months, double extraFace)` | The inflation a linked line prices in (CD5): the nominal par yield of this term less the real coupon that prices a linked bond at 100 - the breakeven the market's two lines disagree by. |
| 3438 | 3 | `public double parYield(int months, double extraFace)` | The nominal coupon a new bullet bond of these months sells at par for (CD6): the par yield of the curve a sale of `extraFace` is priced on, YieldCurve.parYield() - the figure breakevenInflation() takes fairRealCoupon(... |
| 3453 | 6 | `public static double amortizingParYield(YieldCurve curve, int years)` | The one coupon that prices an amortizing bond of these years at 100 on a curve: every payment of its schedule at its own month's factor. |
| 3469 | 3 | `public Debt benchmarkLine(int years)` | The bond line a sale at this term goes into (spec-city-debt.md section 2.2, D6, D14): the city's own LongTermBond - not dollar paper, and not a callable one (CD4: a sale that is not callable never reopens a line that ... |
| 3479 | 13 | `public Debt benchmarkLine(int years, boolean callable)` | ...of a callable sale (CD4): a callable sale reopens only a callable line of the same term whose first call sits as far from its start as the sale's would (CallTree.firstCallYears()), and a sale that is not callable n... |
| 3493 | 3 | `public void clearDebts()` |  |
| 3497 | 4 | `public void setDebt(List<Debt> debts)` |  |
| 3504 | 4 | `static { ... }` |  |
| 3510 | 10 | `public void redenominate(double scale)` | The city's debt book, in the new unit. |

