package ham.citybuildersim;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Private-sector credit. The counterpart to DebtManager, which handles the
 * city's own borrowing.
 *
 * One manager holds every business loan in the city, tagged by sector, rather
 * than one manager per sector. That way there is a single list to save, a single
 * place to see total private credit, and adding a fourth sector is a constant in
 * this file instead of another object to wire up. Each sector still gets its own
 * rate - the rate is per sector, the bookkeeping is shared.
 *
 * PRICING (0.7.7; the borrower's part from the curve since 0.7.8)
 *
 *     rate = the bank's prime + this borrower's own expected loss over the
 *            book's + its record
 *
 * PRIME is the bank's: what money, running the bank, the expected loss and the
 * capital a loan ties up cost it, for a sound business (Bank.prime(), WHAT A
 * LOAN COSTS). Until 0.7.7 the base was the city's own rate - with the bank's
 * strain premium in it - floored on the bank's cost of funds plus a point.
 * The spread is THIS borrower's: since 0.7.8 its own expected loss off the
 * curve its firms default on, over the through-the-cycle loss prime already
 * carries, at the leverage the loan leaves it at - see PRICING FROM THE
 * CURVE. A loan keeps the rate it was written at for its LOAN_TERM_MONTHS.
 *
 * ORIGINATION
 *
 * Loans are underwritten automatically when a sector cannot cover its month
 * (the shortfall desk, coverShortfall()), and for a building a sector's
 * investor plans and cannot pay for out of its own cash (the investment desk,
 * canFundProject() and issueProjectLoan(), the building counted as the
 * collateral). Before this existed, a sector's cash simply went negative with
 * no lender, no interest and no liability on its balance sheet - the food
 * industry was $48,011.82 overdrawn at month 170 and paying nothing for the
 * privilege.
 *
 * AND THE LANDLORDS' BUILDINGS ON INSURED MORTGAGES (0.7.11): a residential
 * order is funded by a Mortgage, not a loan - issueMortgage(), tested by
 * canFundMortgage(), priced at the insured rate Game pushes in with prime,
 * paid down every month and renewed at each term's end in processMonth().
 * See THE LANDLORDS' MORTGAGES below.
 *
 * AND BONDS, AND INTERIM FINANCING (0.7.12): where either desk borrows it
 * may sell a bond in part of the loan's place when one is cheaper, never
 * past what the bank would lend (AND ITS BONDS; the bonds themselves are
 * BondMarket's); a sector that cannot pay its month defaults in it (CAN'T
 * PAY MEANS DEFAULT), and what is still unpaid is lent as an InterimLoan
 * ranked ahead of its other debt (INTERIM FINANCING).
 */
public class BusinessDebtManager {

    /**
     * Every set of books that can borrow, by name, in the registry's order.
     *
     * Was a static list of six string constants that half the codebase
     * imported by name; since the sector template (2026-09-11) the names
     * come from Sectors and are handed in by EconomyManager. A harness that
     * builds this class on its own gets the same seven the game has, so
     * nothing it asks about a sector by name comes back empty.
     */
    private String[] SECTORS = Sectors.KEYS.clone();

    /** The registry's names, in its order. Re-seeds every per-sector map for a name it has not seen. */
    public void setSectors(String[] keys) {
        if (keys == null || keys.length == 0) return;
        SECTORS = keys.clone();
        for (String sector : SECTORS) {
            assets.putIfAbsent(sector, 0.0);
            rates.putIfAbsent(sector, 0.0);
            maturedPrincipal.putIfAbsent(sector, 0.0);
        }
    }

    public String[] sectors() { return SECTORS.clone(); }

    /* =======================================================================
       PRICING FROM THE CURVE (0.7.8)

       Jerus, 2026-09-23: "Price risk from the curve". A borrower pays prime
       and, over it, its own expected loss past the book's:

           rate = prime + max(0, LOAN_LOSS_GIVEN_DEFAULT x PD(L) - Bank.BASE_LOSS_RATE)
                        + DEFAULT_SURCHARGE x its record

       PD(L) is the curve its firms default on (defaultProbability()), and L
       is THE LEVERAGE THE LOAN LEAVES IT AT: after a shortfall loan, what it
       will owe over the assets it has (the proceeds cover losses already on
       its books); after a project, what it will owe over its assets and the
       building, counted at the loan's value as canFundProject() counts it -
       both on its last quarter's statements, the deal on top, since round 3
       (THE BANK READS A BORROWER FROM ITS LAST QUARTER, below).
       Prime already carries BASE_LOSS_RATE, the loss of a sound book, so a
       sound borrower pays prime; one at the watch line pays 0.11 points over
       it, one at 1.2 4.25, one at the default point 12.1 - at a loan's own
       loss given default since 0.7.12 round 2 (RECOVERIES BY INSTRUMENT;
       at the old 60% they were 0.83, 10.8 and 29.6).

       WHAT IT REPLACED. The spread was a line in leverage: SPREAD_PER_DEBT_TO_
       ASSETS, six points a unit, from MIN_SPREAD's one point to MAX_SPREAD's
       eight - Jerus's "even worst case, not too bad" cap - re-based on prime
       in 0.7.7 so that it ran from nothing to seven points over it. The cap
       was what stopped a bad month compounding, and the todo's "Not doing"
       has why the spread was never made dearer (the 12% measurement): the
       shortfall borrower paid the extra interest by borrowing it.
       That does not bite here: under the shortfall desk's ceiling
       (MAX_LOAN_TO_ASSETS, 0.9) the curve charges at most 0.11 points over
       prime - less than the old line's 5.4 there - and past it the desk lends
       only the month's interest, and nothing past the default point. What
       the curve charges past the ceiling is the projects', and a project is
       judged at the rate it would be written at (Game.consider()), which is
       the brake: a building that takes a sector to 1.2 has to earn prime and
       four and a quarter points. Until 0.7.8 the
       seven-point cap sat under the curve's own expected loss from about 1.15
       times its assets, and the bank lent there at a price its own curve said
       lost money.
       ======================================================================= */

    /** A borrower's own expected loss over the book's, at this leverage: LOAN_LOSS_GIVEN_DEFAULT x PD(L) less Bank.BASE_LOSS_RATE, never below nothing - the part of its rate that is its risk. */
    public static double expectedLossSpread(double leverage) {
        return expectedLossSpread(leverage, LOAN_LOSS_GIVEN_DEFAULT);
    }

    /**
     * The leverage a price is read at: what is owed over the assets, and the
     * whole curve against no assets at all, owing or not - a business with
     * nothing is not a good credit, it is an empty one. (The first version of
     * the old spread returned its minimum there, so the insolvent food
     * industry borrowed $49,611 at 2%.)
     */
    static double pricingLeverage(double principal, double assets) {
        if (!(assets > 0)) return Double.POSITIVE_INFINITY;
        return Math.max(0, principal) / assets;
    }

    /**
     * ...at a loss given default of its own (0.7.12): the same curve at an
     * instrument's own loss - a loan's is the one-argument form, and the
     * screens quote a bond's beside it (RECOVERIES BY INSTRUMENT).
     */
    public static double expectedLossSpread(double leverage, double lossGivenDefault) {
        return Math.max(0, Math.max(0, lossGivenDefault) * defaultProbability(leverage) - Bank.BASE_LOSS_RATE);
    }

    /* =======================================================================
       INSOLVENCY

       Before this existed a sector could borrow without limit forever. That is
       not generosity, it is a missing mechanic: construction at equilibrium had
       no orders, no revenue, a standing payroll and interest on its debt, so it
       borrowed to pay the interest, which raised the interest. Its debt went
       from $91,753 to $506,045 across two hundred months in which nothing was
       built. Nothing in the model could ever stop it, because a business here
       could not shrink, default, or be refused.

       A lender in that position stops lending and takes a haircut, so that is
       what happens now.

       Since 0.7.8 A SECTOR DEFAULTS A SLICE AT A TIME (see the section of
       that name below): the line is where a FIRM defaults, and a sector - many
       firms - loses the share of its debt whose firms fell through it each
       month. The whole-sector write-down is the backstop, for a sector with
       nothing left at all, and since 0.7.12 round 5 for one nobody will make
       an interim loan to (INTERIM FINANCING). Since round 4 a sector that
       cannot pay its month defaults in it too - the cash-flow test beside
       this balance-sheet one (CAN'T PAY MEANS DEFAULT).
       ======================================================================= */

    /**
     * The default point: a firm owing more than this multiple of its assets
     * is not getting repaid, and both sides know it. Since 0.7.8 it is where
     * a FIRM defaults - T in defaultProbability() - and a sector at it has
     * half its firms default within a year; until then it was where the
     * whole sector was written down, overnight.
     */
    public static final double INSOLVENCY_TRIGGER = 1.5;

    /**
     * The most a lender will advance against a borrower's assets.
     *
     * STRICTLY BELOW THE WRITE-DOWN LINE, and the distance between the two is
     * the whole of the lender's protection. For one day (2026-09-10) this was
     * defined AS INSOLVENCY_TRIGGER: the most the bank would lend and the point
     * at which it declared the borrower insolvent and took a 60% haircut were
     * the same number. Lend $1,500,000 against $1,000,000 of assets and the
     * borrower is not insolvent; let its assets fall by one dollar and it is,
     * and $900,001 is written off. Twenty-four of the thirty-two restructures
     * in a 4,000-month run fired between 1.50 and 1.74 times assets. The
     * underwriter was parking every borrower precisely on the line and the
     * first bad month pushed it over.
     *
     * The earlier measurement that "a tighter ratio makes the bank irrelevant"
     * (1.00 and 0.75 both ended with no loan book) was honest and answered a
     * different question: because the two constants were one, moving the
     * ceiling moved the insolvency line with it, so it never tested a GAP.
     * With the trigger held at 1.5 and only this moved, over 4,000 months:
     *
     *   ceiling 1.50   wrote off $25.80bn (103% of interest), failed 308x,
     *                  the city injected $3.76bn
     *   ceiling 0.90   wrote off  $0.74bn (  7% of interest), failed  14x,
     *                  the city injected $27M - and kept a book
     *
     * 0.9 is not a real-world loan-to-value (65-75% on secured commercial
     * lending); it is the first setting measured that leaves a working bank
     * AND a working city, and it is here so the next person can move it.
     * This is the SHORTFALL desk's ceiling. The investment desk asks a
     * different question of the same line - see canFundProject().
     */
    public static final double MAX_LOAN_TO_ASSETS = 0.9;

    /**
     * What the backstop leaves a restructured sector owing, as a multiple of
     * its assets. In the backstop's own case since 0.7.8, a sector with
     * nothing left (isInsolvent(): assets at or below zero), that is
     * nothing: everything it owes is written off, its loans and its bonds
     * alike (restructure(), Bank.lossIfDefaulted()). Since 0.7.12 round 5 the
     * backstop also takes a sector nobody would make an interim loan to,
     * whatever it owns, and leaves that one owing this share of what it owns
     * (restructure(sector, true), INTERIM FINANCING).
     *
     * NOTHING DERIVES A LOSS FROM IT ANY MORE (0.7.12, round 2). From 0.7.8
     * the loss on every defaulted dollar was 1 - RESTRUCTURE_TARGET /
     * INSOLVENCY_TRIGGER = 60%, the old LOSS_GIVEN_DEFAULT; each instrument
     * recovers its own now - LOAN_RECOVERY and BOND_RECOVERY, see
     * RECOVERIES BY INSTRUMENT. What read the 60% and reads them instead:
     * the slice, the allowance and its floor, a loan's expected-loss price,
     * a bond's value and its bidders' expected loss, the default-rate floor
     * a bond is valued at (BondMarket.DEFAULT_RATE_FLOOR), the insured
     * mortgages' claims, the concentration charge, and the Bank and sector
     * screens that quoted the number.
     */
    public static final double RESTRUCTURE_TARGET = .6;

    /* =======================================================================
       A SECTOR DEFAULTS A SLICE AT A TIME (0.7.8)

       Jerus, 2026-09-23: "go for option C" - a sector stands for many firms,
       so when it is in trouble only its weakest part goes bust, a slice at a
       time, instead of 60% of the whole sector overnight.

       Until then a sector was one borrower. The month its debt passed
       INSOLVENCY_TRIGGER times its assets, all of it was written down to
       RESTRUCTURE_TARGET of them, and the bank lost 37-185% of its weighted
       book in its worst year - a whole sector is one borrower, and no real
       capital level holds a loss that size (batch 2's record: 92 failures on
       the default eight seeds, where 0.7.7 had 4, because it no longer kept
       capital it never paid out).

       THE RULE is Merton's (1974) structural model of default, the basis of
       Moody's KMV "expected default frequency". A firm defaults when its
       assets fall below what it owes; with its assets moving with volatility
       sigma, the chance of that within a year is

           PD(L) = N( ln(L / T) / sigma )

       L is the sector's debt over its assets as the month's check reads them
       (after the balance-sheet refresh), T the default point
       INSOLVENCY_TRIGGER, N the standard normal CDF (normalCdf()), sigma
       ASSET_VOLATILITY. The sector's own books carry the common shock - its
       trade, its prices, its losses; sigma is the spread of fortunes among
       the firms inside it. With many firms that spread averages out, so the
       flow is deterministic: no random numbers, and the playtest stays
       reproducible.

       Each month the share of the sector's debt that defaults is the monthly
       hazard h = 1 - (1 - PD)^(1/12), and each instrument loses its own
       share of it (RECOVERIES BY INSTRUMENT, since 0.7.12 round 2): the
       loans h x LOAN_LOSS_GIVEN_DEFAULT, every loan of the sector written
       down pro rata, the bonds h x BOND_LOSS_GIVEN_DEFAULT (defaultSlice()). The firms that defaulted keep
       their plant - a write-down, not a liquidation, as the restructure
       always was - so the sector's assets stand, its debt falls, its leverage
       falls and next month's hazard with it. A sector that keeps losing money
       keeps rising into the hazard; one that stabilises drains out of it.

       What it gives at ASSET_VOLATILITY = 0.25, printed by BankCheck (14)
       from these functions:

           leverage   default a year   a month   lost a year, of its loans / bonds
             0.30         0.00%          0.00%        0.00%     0.00%
             0.50         0.00%          0.00%        0.00%     0.00%
             0.70         0.11%          0.01%        0.03%     0.06%
             0.90         2.05%          0.17%        0.51%     1.13%
             1.10        10.74%          0.94%        2.68%     5.91%
             1.30        28.35%          2.74%        7.09%    15.59%
             1.50        50.00%          5.61%       12.50%    27.50%
             2.00        87.51%         15.91%       21.88%    48.13%
             3.00        99.72%         38.76%       24.93%    54.85%

       ("lost a year" is each instrument's loss given default x PD: what a
       sector held at that leverage loses on each; one that is not held drains
       down the table. The 60% before round 2 lost 30.00% at the line.)

       THE WHOLE-SECTOR RESTRUCTURE STAYS AS THE BACKSTOP, for the case where
       every firm is under water at once: assets at or below zero with debt or
       an overdraft (isInsolvent()). There, restructure() runs as it always
       did - written down to RESTRUCTURE_TARGET of its assets (nothing), the
       overdraft forgiven, the record counted, the ban. Since 0.7.12 round 5
       it also takes a sector that could not pay its month and that nobody
       would make an interim loan to - still past the default point after the
       write-down, or its bank shut - written down the same way, to
       RESTRUCTURE_TARGET of what it still owns (restructure(sector, true),
       INTERIM FINANCING). Only the backstop counts toward the record, the
       surcharge and the ban: a slice does not, because the price already
       charges a sector in trouble its expected loss (PRICING FROM THE CURVE).
       Since round 4 a month's slice is the larger of the curve's share and
       the part of the sector that could not pay its month (CAN'T PAY MEANS
       DEFAULT).
       ======================================================================= */

    /**
     * The one-year volatility of a firm's assets: sigma in PD(L), the spread
     * of fortunes among the firms inside a sector. The Merton/KMV literature
     * estimates it for non-financial firms at commonly 20-35% a year, a
     * typical industrial about 25%. A number with an argument behind it, not
     * a measurement of this city, and not tuned to any result here: at 0.20
     * the curve is steeper (fewer defaults under the line, more past it), at
     * 0.35 flatter. Jerus's number to settle.
     */
    public static final double ASSET_VOLATILITY = .25;

    /** The horizon a default probability is quoted over, in months: one year, the convention of KMV's EDF and of every rating agency's default rate. A convention, not a dial. */
    public static final int DEFAULT_HORIZON_MONTHS = 12;

    /* =====================================================================
       RECOVERIES BY INSTRUMENT (0.7.12, round 2)

       Jerus, 2026-09-25: "Real averages by type." What a default gives back
       is the instrument's own, from the record: a bank loan LOAN_RECOVERY of
       each dollar that defaults, a bond BOND_RECOVERY - so what a sector's
       creditors recover together depends on its mix of the two.

       IT REPLACED ONE LOSS FOR EVERYTHING, derived rather than sourced: a
       defaulting firm sits at the default point owing INSOLVENCY_TRIGGER
       times its assets, the restructure used to leave it owing
       RESTRUCTURE_TARGET of them, so 1 - 0.6 / 1.5 = 60% of every dollar was
       lost. Round 1 split that 40% between the classes by absolute priority,
       loans first (Jerus, 2026-09-24: "Bank notes rank first"), and the runs
       gave the loans 45-54% and the bonds 2-13% against the record's 70-80%
       and 40-50%: the model's total was the reason, not the rule.

       NO PRIORITY RULE ON TOP. The record's recoveries are what each class
       got back in the courts, under the order the courts paid them in: a
       secured loan recovers more BECAUSE it ranks first and holds the
       collateral. Paying the loans first again, out of recoveries already
       measured that way, would count their seniority twice. So a slice
       writes each instrument down by its own loss whatever the mix. The one
       place the order could still decide something is the backstop, and
       there is nothing there to share (restructure()).

       AND ONE THING IT DOES NOT AGREE WITH. The old loss was consistent with
       the default point by construction; these are not. A firm that owes
       only its bank and defaults at INSOLVENCY_TRIGGER times its assets gives
       the bank back LOAN_RECOVERY x INSOLVENCY_TRIGGER = 1.125 times what it
       owns. The recoveries are the record's and the default point is the
       model's, and the two were measured on different things.

       ONE PAIR OF NUMBERS, EVERY READER: the slice writes the loans down by
       LOAN_LOSS_GIVEN_DEFAULT of their share and the bonds by
       BOND_LOSS_GIVEN_DEFAULT of theirs (defaultSlice()); the bank's
       allowance and a loan's price read the loans' (Bank.sectorAllowance(),
       priceSector(), expectedLossSpread()); a bondholder values a bond and
       bids for one on the bonds' (BondMarket.expectedLoss(), its Demand);
       a landlord's insured mortgage is a first-lien loan, so the insurer's
       claim is the loans' loss on it (writeDownSector()); and the bank's
       concentration charge weighs each class at its own (Game.bankExposures()).
       ===================================================================== */

    /** What a bank loan gives back of each dollar that defaults: 75%, the midpoint of the 70-80% that first-lien senior secured bank loans recovered on average in Moody's Ultimate Recovery Database and S&P's recovery studies, 1987-2024 (summarised at collateralizedloanobligations.com/data/recovery-rates). Jerus, 2026-09-25: "Real averages by type." */
    public static final double LOAN_RECOVERY = .75;

    /** What a bond gives back of each dollar that defaults: 45%, the midpoint of the 40-50% that senior unsecured bonds recovered on average in the same Moody's and S&P data, 1987-2024. */
    public static final double BOND_RECOVERY = .45;

    /** What the bank loses on a dollar of a sector's loans that defaults: 1 - LOAN_RECOVERY, derived. The loss the slice, the allowance, a loan's price, the insurer's claim and the concentration charge read. */
    public static final double LOAN_LOSS_GIVEN_DEFAULT = 1 - LOAN_RECOVERY;

    /** ...and what a bondholder loses on a dollar of its bonds: 1 - BOND_RECOVERY, derived. */
    public static final double BOND_LOSS_GIVEN_DEFAULT = 1 - BOND_RECOVERY;

    /**
     * The standard normal cumulative distribution, N(x): Hart's (1968)
     * double-precision rational approximation as Graeme West gives it
     * ("Better approximations to cumulative normal functions", Wilmott,
     * 2005), with a continued fraction past 7.07 standard deviations.
     * Deterministic, and the one place the game computes it. Measured against
     * the exact erfc on a 0.001 grid over +/-40: absolute error at most
     * 2.3e-16, and relative error in the lower tail under 1e-8 - so even
     * the smallest default rates it gives are right to eight figures.
     * BankCheck (14) asserts it against N(0), N(1.96) and N(-1).
     */
    public static double normalCdf(double x) {
        if (Double.isNaN(x)) return Double.NaN;
        double a = Math.abs(x);
        double tail;
        if (a > 37) {
            tail = 0;
        } else {
            double e = Math.exp(-a * a / 2);
            if (a < 7.07106781186547) {
                double b = 3.52624965998911e-02 * a + 0.700383064443688;
                b = b * a + 6.37396220353165;
                b = b * a + 33.912866078383;
                b = b * a + 112.079291497871;
                b = b * a + 221.213596169931;
                b = b * a + 220.206867912376;
                tail = e * b;
                b = 8.83883476483184e-02 * a + 1.75566716318264;
                b = b * a + 16.064177579207;
                b = b * a + 86.7807322029461;
                b = b * a + 296.564248779674;
                b = b * a + 637.333633378831;
                b = b * a + 793.826512519948;
                b = b * a + 440.413735824752;
                tail = tail / b;
            } else {
                double b = a + 0.65;
                b = a + 4 / b;
                b = a + 3 / b;
                b = a + 2 / b;
                b = a + 1 / b;
                tail = e / b / 2.506628274631;
            }
        }
        return x > 0 ? 1 - tail : tail;
    }

    /**
     * PD(L): the share of a sector's firms - weighted by what they owe - that
     * default within a year at this leverage, N(ln(L / INSOLVENCY_TRIGGER) /
     * ASSET_VOLATILITY). Nothing with no debt; all of it against no assets at
     * all (L infinite). THE ONE CURVE: the month's slice, the bank's
     * allowance and the Bank tab all read it.
     */
    public static double defaultProbability(double leverage) {
        if (!(leverage > 0)) return 0;
        if (Double.isInfinite(leverage)) return 1;
        return normalCdf(Math.log(leverage / INSOLVENCY_TRIGGER) / ASSET_VOLATILITY);
    }

    /** ...over this many months instead of a year: 1 - (1 - PD)^(months / DEFAULT_HORIZON_MONTHS), for a borrower held at this leverage. Written through log1p so the smallest rates keep their figures. */
    public static double defaultProbability(double leverage, double months) {
        if (!(months > 0)) return 0;
        double pd = defaultProbability(leverage);
        if (pd >= 1) return 1;
        return -Math.expm1(Math.log1p(-pd) * months / DEFAULT_HORIZON_MONTHS);
    }

    /** h: the share of a sector's debt that defaults in one month at this leverage - the monthly hazard of PD(L). */
    public static double monthlyDefaultShare(double leverage) {
        return defaultProbability(leverage, 1);
    }

    /** A sector's leverage for the curve: what it owes over its assets, infinite when it owes anything against nothing. */
    static double leverageOf(double principal, double assets) {
        if (!(principal > 0)) return 0;
        return assets > 0 ? principal / assets : Double.POSITIVE_INFINITY;
    }

    /**
     * Months a sector cannot borrow after being restructured.
     *
     * Without this the write-down is a gift: the sector is handed a clean
     * balance sheet and immediately re-levers to pay the same bills it could not
     * pay before, and the spiral restarts one month later with the lender
     * funding it. Being cut off is what forces the real adjustment - selling
     * capacity it is not using - and a firm in default genuinely cannot raise
     * money.
     */
    private static final int BORROWING_BLOCKED_MONTHS = 12;

    /**
     * How long a sector is shut out, given how many times it has done this.
     *
     * A FLAT TWELVE MONTHS WAS THE SAME ANSWER FOR A FIRST DEFAULT AND A
     * TWENTIETH, and that is the whole of why the loop existed. A sector with
     * no viable business would default, sit out its year, come back with a
     * clean balance sheet, re-lever against the same assets, and default again.
     * Measured over four thousand months at a flat twelve: 163 restructures,
     * one somewhere every 25 months, INDUSTRY alone 72 of them.
     *
     * Real credit does not work that way. A first default is survivable and a
     * borrower is back in the market in a year or two; a serial defaulter is
     * not lent to at all. The exclusion is the borrower's record, so it grows
     * with the record: twelve months for the first, twenty-four for the second,
     * thirty-six for the third.
     *
     * Nothing new is counted for this - `restructures` was already tracked and
     * already on the credit screen. It just was not being used for anything.
     *
     * Measured with the same run: 163 restructures -> 35, one every 114 months
     * instead of every 25, INDUSTRY 72 -> 5, MINING 55 -> 9. The city's worst
     * unemployment went 27.7% -> 22.7%, hunger 3% -> 0%, the shelf 60% -> 75%
     * delivered, and the recapitalisation the city has to fund fell from
     * $62.7bn to $3.76bn - seventeen-fold - while the bank kept a $4.4bn book
     * at a 16.2% capital ratio. Strictness about WHO fixed what strictness
     * about HOW MUCH could only fix by closing the bank.
     */
    static int exclusionFor(int defaultsSoFar) {
        return BORROWING_BLOCKED_MONTHS * Math.max(1, defaultsSoFar);
    }

    /**
     * Extra annual interest per prior write-down, on top of the borrower's
     * expected loss, up to DEFAULT_SURCHARGE_MAX_COUNT of them.
     * (Outside the old leverage spread's cap too, until 0.7.8 took the cap.)
     *
     * DEFAULTING USED TO CUT THE RATE. restructure() reprices off the
     * post-write-down leverage and the borrower's record never entered the
     * price at all, so a sector quoted 8.5% at the ceiling was quoted 5.1%
     * the month after it defaulted - and a borrower that had defeated the
     * lender eight times was quoted less than one that had never missed. The
     * record is what a lender prices; here it was only what it banned on.
     */
    public static final double DEFAULT_SURCHARGE = .01;
    /** The most write-downs DEFAULT_SURCHARGE is charged for: a record adds three points at the most. */
    public static final int DEFAULT_SURCHARGE_MAX_COUNT = 3;

    /** How long a business loan runs, interest only, before its principal is due: three years, and it keeps the rate it was written at for all of them. Prime is struck at this term (Bank.PRIME_TERM_MONTHS). A landlord's building is on a Mortgage since 0.7.11. */
    public static final int LOAN_TERM_MONTHS = 36;

    /**
     * Borrow enough to cover the hole plus this many months of the current loss.
     *
     * Without the buffer a chronically loss-making sector writes a fresh loan
     * every single month - the food industry would be carrying about 170 of them
     * by now, and the debt schedule would be unreadable. Overshooting slightly
     * keeps it to a handful of larger loans.
     */
    private static final double BUFFER_MONTHS = 3;

    private List<BusinessDebt> loans = new ArrayList<>();

    /* =====================================================================
       AND ITS BONDS (0.7.12)

       A sector's debt is its bank loans and, since 0.7.12, its bonds
       (CorporateBond, held and traded in BondMarket). This class keeps the
       loans; the bonds are the market's, read through the BondBook:
         getPrincipal()        what a sector owes, loans and bonds: its
                               leverage, its default curve, the ceilings,
                               the default point, the slice, its balance
                               sheet;
         getLoanPrincipal()    what it owes the bank: the bank's book, its
                               allowance and its capital rule;
         getBondPrincipal()    what it owes on its bonds;
         getMonthlyInterest()  its interest bill, both;
         getAllPrincipal()     every sector's bank loans, what the bank lent.
       And where the two desks borrow, they ask the BondDesk first whether a
       bond would be cheaper (coverShortfall(), and Game.consider() for the
       investment desk). A harness that builds this class alone has no bond
       market, and every figure is what it was.
       ===================================================================== */

    /** What the bond market tells the lender about each sector's bonds. */
    public interface BondBook {
        /** The face a sector owes on its bonds. */
        double principal(String sector);
        /** The coupons its bonds ask this month, on the face outstanding. */
        double monthlyCoupon(String sector);
        /** Writes a sector's bonds down to this share of their face, every holder pro rata. @return the face written off */
        double writeDown(String sector, double scale);
    }

    /** ...and where the desks ask whether a bond would be cheaper than the bank (BondMarket, WHO ISSUES, AND WHEN). */
    public interface BondDesk {
        /**
         * How to finance `amount` for this sector: a bond, the bank, or both.
         *
         * @param loanRate    what the bank would write the loan at
         * @param loanRoom    what the bank will lend of it: its ceiling, and for a building its capital
         *                    rule (a short month's line is not rationed since round 5)
         * @param bondRoom    the bond's own ceiling, and the ban: the shortfall desk's ceiling for a short
         *                    month (bondCeilingRoom()), the default point after the deal for a building
         *                    (projectBondRoom()) - the plan holds the bond and the loan together to
         *                    loanRoom as well
         * @param extraAssets what the borrowing adds to the assets a lender counts: the building, for a project
         */
        Plan plan(String sector, double amount, double loanRate, double loanRoom, double bondRoom,
                  double extraAssets, int month);
        /** Issues a plan's bond. @return the proceeds - the face less the issuing costs - for the caller to hand the sector */
        double issue(Plan plan, int month);
    }

    /**
     * How one borrowing is financed (0.7.12): of `amount`, `bondFace` in a
     * bond at `coupon` - `allIn` with its issuing costs spread over its life
     * - and `loan` from the bank at `loanRate`, `loanAllIn` with its fee
     * spread over its term. `clearing` is the coupon the book would have
     * cleared the whole amount at (NaN when nothing would clear it), which
     * is what the advisor says when the bank was the cheaper; `extraAssets`
     * what the borrowing adds to what a lender counts, the building for a
     * project, which the bond is sold on; `dealDebt` what the bond and the
     * loan together add to what the sector owes - the leverage both are
     * priced at, since a lender and a bondholder alike price the balance
     * sheet the whole deal leaves; `offered` the bond as the underwriter
     * announced it, which every bid was an amount of (BondMarket, THE BOOK IS
     * BUILT).
     */
    public record Plan(String sector, double amount, double bondFace, double coupon, double allIn,
                       double loan, double loanRate, double loanAllIn, double costs, double clearing,
                       double extraAssets, double dealDebt, double offered) {
        /** All of it from the bank. */
        public static Plan bankOnly(String sector, double amount, double loanRate, double loanAllIn, double clearing) {
            return new Plan(sector, amount, 0, Double.NaN, Double.NaN, amount, loanRate, loanAllIn, 0, clearing, 0, amount, 0);
        }
        /** True when some of it is a bond. */
        public boolean hasBond() { return bondFace > 0; }
        /** The interest a month the plan costs, both parts: what the project's own test is asked to carry, each at its instrument's rate. */
        public double monthlyInterest() {
            return ((bondFace > 0 ? bondFace * coupon : 0) + (loan > 0 ? loan * loanRate : 0)) / 12;
        }
        /** ...as an annual rate on what it raises. */
        public double blendedRate() { return bondFace + loan > 0 ? monthlyInterest() * 12 / (bondFace + loan) : loanRate; }
        /** True when it raises the whole amount. */
        public boolean covers() { return bondFace + loan >= amount * (1 - 1e-12); }
    }

    private BondBook bondBook;
    private BondDesk bondDesk;

    /** The bond market, wired by Game; null in a harness that builds this class alone. */
    public void setBondMarket(BondBook book, BondDesk desk) {
        this.bondBook = book;
        this.bondDesk = desk;
    }

    public BondDesk getBondDesk() { return bondDesk; }

    /** The proceeds of the bonds the shortfall desk issued this month, per sector, until the credit settle hands them over (takeBondProceeds()). */
    private final Map<String, Double> bondProceeds = new LinkedHashMap<>();

    /** The bonds the shortfall desk issued for a sector this month, net of their costs, handed over once. */
    public double takeBondProceeds(String sector) {
        Double v = bondProceeds.remove(sector);
        return v == null ? 0 : v;
    }

    /** The plan the shortfall desk struck for each sector this month, for the advisor and the playtest. Not saved: the month's. */
    private final Map<String, Plan> shortfallPlans = new LinkedHashMap<>();
    public Plan getShortfallPlan(String sector) { return shortfallPlans.get(sector); }

    /** Prime: the bank's rate for a sound business this month, which every sector's own spread sits on (see PRICING). */
    private double primeRate;

    /** The insured mortgage's rate this month (Bank.insuredMortgageRate()), pushed in by Game beside prime: what a new mortgage is written at and a term renews at. */
    private double insuredMortgageRate;

    /** Written off this month, and over the whole game, per sector. */
    private final Map<String, Double> writtenOffThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> writtenOffTotal = new LinkedHashMap<>();
    private final Map<String, Integer> restructures = new LinkedHashMap<>();

    /** Months of borrowing ban remaining, per sector. */
    private final Map<String, Integer> blockedMonths = new LinkedHashMap<>();

    /**
     * Whether the lender is open for business at all.
     *
     * A failed bank has no capacity - Bank.capacity() returns zero in
     * resolution, and the class doc, the panel and the player were all told it
     * "cannot lend a penny until it has capital again". Nothing here ever
     * asked. This class holds no reference to the bank, so the only
     * consequence of the bank failing was that every borrower's premium went
     * to eighteen points - and the borrower paid it by borrowing more from the
     * same failed bank. Measured over 4,000 months: 326 months began with the
     * bank frozen, and $5.05bn of new business credit was written inside them.
     *
     * Game sets this each month from the bank's own state. Harnesses that
     * build this class on its own get an open lender, which is what they had.
     */
    private boolean lendingOpen = true;

    private final Map<String, Double> assets = new LinkedHashMap<>();
    private final Map<String, Double> rates = new LinkedHashMap<>();

    /**
     * Each sector's cash balance, refreshed with the assets. An overdraft is
     * a debt to nobody in particular - the money was paid out and never
     * borrowed - and the month's defaults close it: lent as interim
     * financing since 0.7.12 round 5, or forgiven by the backstop; see
     * INTERIM FINANCING and restructure().
     */
    private final Map<String, Double> cashBalance = new LinkedHashMap<>();

    /** What this month's restructures forgave, per sector, until Game collects it. */
    private final Map<String, Double> overdraftForgiven = new LinkedHashMap<>();

    /** Principal that fell due this month, per sector, waiting to be settled. */
    private final Map<String, Double> maturedPrincipal = new LinkedHashMap<>();

    /**
     * The lender's side of the month, for MoneyAudit: what it advanced and what
     * it took back. Neither is saved - they describe the month in progress and
     * Game.nextMonth() zeroes them before anything moves.
     */
    private double lentThisMonth;
    private double repaidThisMonth;

    /**
     * THE LOAN FEE (0.7.7): Bank.LOAN_FEE of every loan written, paid out of
     * its proceeds - the borrower is handed the principal less this, and the
     * bank's cash takes it at the month's settle beside the lending
     * (Game.nextMonth()). Both ends are pools, so the audit sees a transfer
     * that cancels. Per sector too, so a sector's cash flow statement can
     * show what it actually received (SectorBooks).
     */
    private double feesThisMonth;
    private final Map<String, Double> feesBySector = new LinkedHashMap<>();

    /** What a loan of this principal pays up front: Bank.LOAN_FEE of it. */
    public static double feeOn(double principal) {
        return Math.max(0, principal) * Bank.LOAN_FEE;
    }

    public double getFeesThisMonth() { return feesThisMonth; }
    public double getFeesThisMonth(String sector) { return feesBySector.getOrDefault(sector, 0.0); }

    /*
     * THE SAME TWO FIGURES, PER SECTOR.
     *
     * Recording only - nothing below reads these back, and no decision in this
     * class or any other depends on them. They exist because a cash flow
     * statement is not a statement without them: a sector's cash moves by its
     * profit, by what it borrowed and by what it repaid, and with only the
     * city-wide totals the last two could not be attributed to the sector whose
     * cash actually moved. See SectorBooks.
     */
    private final Map<String, Double> lentBySector = new LinkedHashMap<>();
    private final Map<String, Double> repaidBySector = new LinkedHashMap<>();

    public double getLentThisMonth()   { return lentThisMonth; }
    public double getRepaidThisMonth() { return repaidThisMonth; }

    public double getLentThisMonth(String sector) {
        return lentBySector.getOrDefault(sector, 0.0);
    }

    public double getRepaidThisMonth(String sector) {
        return repaidBySector.getOrDefault(sector, 0.0);
    }

    public void startAuditMonth() {
        shortfallPlans.clear();
        lentThisMonth = 0;
        repaidThisMonth = 0;
        lentBySector.clear();
        repaidBySector.clear();
        feesThisMonth = 0;
        feesBySector.clear();
        writtenThisMonth.clear();
        // ...and the month's mortgages (0.7.11): the premiums written and
        // the principal the payments took. See THE LANDLORDS' MORTGAGES.
        premiumsThisMonth = 0;
        premiumsBySector.clear();
        mortgageRepaidBySector.clear();
        mortgagesWrittenThisMonth = 0;
        renewedThisMonth = 0;
        fallenDueThisMonth = 0;
    }

    public BusinessDebtManager() {
        for (String sector : SECTORS) {
            assets.put(sector, 0.0);
            rates.put(sector, 0.0);   // unpriced until the month's first pricing
            maturedPrincipal.put(sector, 0.0);
        }
    }

    /**
     * Prime, pushed in by Game each month from Bank.prime() before anything
     * is priced (0.7.7; the city's rate before it). Held rather than reached
     * for: a rate that moved half way through a quote would be a quote
     * nobody was offered. The bank's cost of funds used to be pushed in
     * beside it as a floor, and prime is built on the bank's funds-transfer
     * price now, so there is nothing left for a floor to do.
     */
    public void setPrimeRate(double rate) {
        this.primeRate = rate;
    }

    /** The insured mortgage's rate, pushed in by Game with prime (Bank.insuredMortgageRate()). */
    public void setInsuredMortgageRate(double rate) {
        this.insuredMortgageRate = rate;
    }

    /** What a new insured mortgage is written at this month, and what a term that ends renews at. */
    public double getInsuredMortgageRate() {
        return insuredMortgageRate;
    }

    /** Total assets from that sector's balance sheet - the denominator of leverage. */
    public void setAssets(String sector, double totalAssets) {
        assets.put(sector, totalAssets);
    }

    /**
     * THE CONCENTRATION CHARGE ON EACH SECTOR'S LOANS (0.7.12): what the
     * capital a new dollar lent to it adds for concentration costs a year,
     * pushed in by Game from the bank beside prime (Bank
     * .concentrationCharge(), THE BANK PRICES CONCENTRATION). Negative for
     * a sector that diversifies the book. None in a harness that builds this
     * class alone.
     */
    private final Map<String, Double> concentrationCharges = new LinkedHashMap<>();

    public void setConcentrationCharges(Map<String, Double> charges) {
        concentrationCharges.clear();
        if (charges != null) concentrationCharges.putAll(charges);
    }

    /** What a loan to this sector pays for the book's concentration, a year: part of its rate. */
    public double getConcentrationCharge(String sector) { return concentrationCharges.getOrDefault(sector, 0.0); }

    //pricing
    public void updateRates() {
        for (String sector : SECTORS) {
            rates.put(sector, priceSector(sector));
        }
    }

    /** The quote: the curve at the leverage the sector's last quarter of statements reads (getQuarterLeverage()), and the whole curve against no assets (pricingLeverage()). */
    private double priceSector(String sector) {
        return priceSector(sector, 0, 0);
    }

    /**
     * @param extraPrincipal borrowing about to be taken on, included in the
     *                       ratio. A lender prices the loan it is writing, not
     *                       the balance sheet from before it existed - without
     *                       this a sector's FIRST loan always priced as though
     *                       it had no debt, i.e. at the cheapest rate available,
     *                       however much it was borrowing.
     * @param extraAssets    what the borrowing adds to the assets the lender
     *                       counts: nothing for a shortfall loan, whose proceeds
     *                       cover losses already on the books; the loan's own
     *                       value for a project, whose building is the
     *                       collateral - read the way canFundProject() reads
     *                       it, against what it owns now if that is anything.
     *
     * BOTH ON THE LAST QUARTER'S STATEMENTS (0.7.8, round 3): what it owed and
     * what it owned averaged over the last STATEMENT_MONTHS month-end readings
     * (quarterPrincipal(), quarterAssets()), the deal added on top. Loans
     * written earlier in the same month are in neither until the month's own
     * reading - at most a shortfall loan and one project a month a sector.
     */
    private double priceSector(String sector, double extraPrincipal, double extraAssets) {

        double principal = quarterPrincipal(sector) + extraPrincipal;
        double totalAssets = extraAssets > 0
                ? Math.max(0, quarterAssets(sector)) + extraAssets
                : quarterAssets(sector);

        /*
         * THIS BORROWER'S OWN RISK, and only its own: its expected loss off
         * the curve over the loss prime already carries - see PRICING FROM
         * THE CURVE. Counting BASE_LOSS_RATE again would price the same risk
         * twice.
         */
        /*
         * ...AT A LOAN'S LOSS GIVEN DEFAULT (0.7.12, round 2): what a bank
         * loan loses when it defaults, whatever else the sector owes - see
         * RECOVERIES BY INSTRUMENT. (Round 1 read the loans' side of absolute
         * priority here, which fell as the sector's bonds grew.)
         */
        double spread = expectedLossSpread(pricingLeverage(principal, totalAssets));

        // The record, priced: a bad HISTORY is not a leverage.
        spread += recordSurcharge(sector);

        // ...and the capital the book's concentration asks of the loan (0.7.12).
        spread += getConcentrationCharge(sector);

        /*
         * ...OVER PRIME, which is what the money and the bank cost (0.7.7).
         *
         * A credit spread says what the BORROWER's risk is worth. It says
         * nothing about what the money cost, and until 2026-09-13 nothing else
         * did either: this returned the city's rate plus the spread, while the
         * bank funding the loan paid two points over policy for the dollars.
         * From then until 0.7.7 it was floored on the bank's cost of funds
         * plus Bank.MIN_MARGIN. Prime is built on the bank's funds-transfer
         * price and its costs, so the floor went with the base it propped up.
         */
        return primeRate + spread;
    }

    //getters
    /** What NEW borrowing costs this sector today. Existing loans keep their own rate. */
    public double getRate(String sector) {
        return rates.getOrDefault(sector, primeRate);
    }

    /** What this sector pays over prime: its own expected loss and record. */
    public double getSpread(String sector) {
        return getRate(sector) - primeRate;
    }

    /** ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()) and a loan's loss given default. */
    public double getRiskSpread(String sector) {
        return expectedLossSpread(pricingLeverage(quarterPrincipal(sector), quarterAssets(sector)));
    }

    /** ...and the second: DEFAULT_SURCHARGE a write-down on its record, up to DEFAULT_SURCHARGE_MAX_COUNT of them. */
    public double getRecordSurcharge(String sector) {
        return recordSurcharge(sector);
    }

    private double recordSurcharge(String sector) {
        return DEFAULT_SURCHARGE * Math.min(getRestructureCount(sector), DEFAULT_SURCHARGE_MAX_COUNT);
    }

    /**
     * WHAT A PROJECT LOAN OF THIS SIZE WOULD BE WRITTEN AT (0.7.8): the curve
     * at the leverage it leaves the sector at, the building counted at the
     * loan's value - the rate issueProjectLoan() writes, and the one
     * Game.consider() judges a building by. Not today's quote, which is the
     * curve where the sector stands before it borrows.
     */
    public double projectRate(String sector, double amount) {
        return priceSector(sector, Math.max(0, amount), Math.max(0, amount));
    }

    /**
     * ...and the leverage that loan is priced at: the last quarter's
     * statements with the deal on top, the building counted. With no
     * statements yet it is the leverage canFundProject() reads after the deal.
     */
    public double leverageAfterProject(String sector, double amount) {
        double a = Math.max(0, amount);
        return pricingLeverage(quarterPrincipal(sector) + a, Math.max(0, quarterAssets(sector)) + a);
    }

    /** Prime, as the bank set it this month. */
    public double getPrimeRate() {
        return primeRate;
    }

    public double getLeverage(String sector) {
        double totalAssets = assets.getOrDefault(sector, 0.0);
        return (totalAssets > 0) ? getPrincipal(sector) / totalAssets : 0;
    }

    public void setCash(String sector, double cash) {
        cashBalance.put(sector, cash);
        tillReported.add(sector);
    }

    /**
     * The sectors whose sheet the economy has reported (setCash(), from
     * EconomyManager.refreshCreditAssets(), every month before anything is
     * lent): the default point reads only those (pastDefaultPoint()). A
     * harness that builds this class alone and never says where a sector's
     * till is has no sheet to read, and lends as it did - the rule this class
     * keeps for the capital rule and the bond market too.
     */
    private final java.util.Set<String> tillReported = new java.util.HashSet<>();

    public double getCash(String sector) {
        return cashBalance.getOrDefault(sector, 0.0);
    }

    private double getOverdraftForgivenPending(String sector) {
        return overdraftForgiven.getOrDefault(sector, 0.0);
    }

    /** The overdraft a restructure forgave this month, handed over once. */
    public double takeOverdraftForgiven(String sector) {
        double forgiven = overdraftForgiven.getOrDefault(sector, 0.0);
        overdraftForgiven.put(sector, 0.0);
        return forgiven;
    }

    public double getAssets(String sector) {
        return assets.getOrDefault(sector, 0.0);
    }

    /** Everything every sector owes the bank. What the bank has lent the businesses; their bonds are their bondholders' (0.7.12). */
    public double getAllPrincipal() {
        double total = 0;
        for (String s : SECTORS) total += getLoanPrincipal(s);
        return total;
    }

    /** What a sector owes: its bank loans and, since 0.7.12, its bonds - see AND ITS BONDS. */
    public double getPrincipal(String sector) {
        return getLoanPrincipal(sector) + getBondPrincipal(sector);
    }

    /** What a sector owes the bank: its loans and its mortgages. */
    public double getLoanPrincipal(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                total += loan.getOutstandingPrincipal();
            }
        }
        return total;
    }

    /** ...and what it owes on its bonds (0.7.12): nothing without a bond market. */
    public double getBondPrincipal(String sector) {
        return bondBook == null ? 0 : Math.max(0, bondBook.principal(sector));
    }

    /** The bank loans' share of what a sector owes: 1 with no bonds, and with no debt at all. Round 1's loans-first split of a default read it; since round 2 each instrument recovers its own (RECOVERIES BY INSTRUMENT), and only ReadPathCheck reads it. */
    public double getLoanShare(String sector) {
        double loansOwed = getLoanPrincipal(sector);
        double total = loansOwed + getBondPrincipal(sector);
        return total > 0 ? loansOwed / total : 1;
    }

    public double getTotalPrincipal() {
        double total = 0;
        for (BusinessDebt loan : loans) {
            total += loan.getOutstandingPrincipal();
        }
        return total;
    }

    /** This month's interest cost for a sector - the income statement's expense line: its loans' interest and, since 0.7.12, its bonds' coupons. */
    public double getMonthlyInterest(String sector) {
        return getLoanInterest(sector) + (bondBook == null ? 0 : Math.max(0, bondBook.monthlyCoupon(sector)));
    }

    /** ...the part the bank is paid on its loans. */
    public double getLoanInterest(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                total += loan.getMonthlyInterestExpense();
            }
        }
        return total;
    }

    /** Every sector's interest bill, loans and bonds. */
    public double getTotalMonthlyInterest() {
        double total = 0;
        for (String s : SECTORS) total += getMonthlyInterest(s);
        return total;
    }

    /**
     * Blended annual rate actually being paid on existing debt, as opposed to
     * getRate() which is what the next loan would cost. The two diverge when a
     * sector borrowed cheaply and then deteriorated.
     */
    public double getEffectiveRate(String sector) {
        double principal = getPrincipal(sector);
        return (principal > 0) ? (getMonthlyInterest(sector) * 12) / principal : 0;
    }

    public int getLoanCount(String sector) {
        int count = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                count++;
            }
        }
        return count;
    }

    public List<BusinessDebt> getLoans() {
        return loans;
    }

    public List<BusinessDebt> getLoans(String sector) {
        List<BusinessDebt> result = new ArrayList<>();
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                result.add(loan);
            }
        }
        return result;
    }

    //monthly cycle
    /**
     * Advances every loan and retires the ones that mature.
     *
     * Matured principal is parked per sector rather than paid here, because this
     * class has no access to anyone's cash. EconomyManager collects it with
     * takeMaturedPrincipal() and settles it against the right books.
     */
    public void processMonth() {

        // The month's refusals at the default point start here, with the settle,
        // and so does what the shortfall desk lends (round 2).
        refusedAtDefaultPoint.clear();
        notRenewedAtDefaultPoint.clear();
        shortfallLent.clear();
        // ...and why it left a sector short (round 4, CAN'T PAY MEANS DEFAULT).
        shortReason.clear();
        monthObligations.clear();
        // ...what the open line lent while the bank was rationing, and the
        // interim loans that fell due (round 5).
        lineLentRationed.clear();
        lineLentPastOldRule.clear();
        interimMaturedThisMonth.clear();

        Iterator<BusinessDebt> iterator = loans.iterator();

        while (iterator.hasNext()) {
            BusinessDebt loan = iterator.next();
            loan.processMonth();

            /*
             * A MORTGAGE PAYS DOWN EVERY MONTH (0.7.11): the principal its
             * payment took is due now, parked with what matured and settled
             * against the landlord's till the same way. Paid off, it closes.
             * At the end of its term it renews at the day's insured rate -
             * routine, the loan being insured - unless the lender cannot
             * write it: a failed bank lends nothing (lendingOpen), and then
             * the balance falls due whole, which is what a maturing loan
             * does today when the shortfall desk cannot roll it.
             */
            if (loan instanceof Mortgage m) {
                String sector = m.getSector();
                double paid = m.takePrincipalPaid();
                if (paid > 0) {
                    maturedPrincipal.merge(sector, paid, Double::sum);
                    mortgageRepaidBySector.merge(sector, paid, Double::sum);
                }
                if (m.isPaidOff()) {
                    double left = m.close();
                    if (left > 0) {
                        maturedPrincipal.merge(sector, left, Double::sum);
                        mortgageRepaidBySector.merge(sector, left, Double::sum);
                    }
                    iterator.remove();
                } else if (m.isTermEnded()) {
                    // ...and never past the default point (0.7.12, round 2):
                    // a renewal is a refinancing, so a landlord past the line
                    // has its balance fall due whole - see NOTHING PAST THE
                    // DEFAULT POINT. Neutral on its sheet, so read as it stands.
                    boolean past = pastDefaultPoint(sector, getCash(sector));
                    if (past) notRenewedAtDefaultPoint.merge(sector, m.getOutstandingPrincipal(), Double::sum);
                    if (lendingOpen && !past) {
                        m.renew(insuredMortgageRate);
                        renewedThisMonth++;
                        renewedLifetime++;
                    } else {
                        double due = m.close();
                        maturedPrincipal.merge(sector, due, Double::sum);
                        mortgageRepaidBySector.merge(sector, due, Double::sum);
                        fallenDueThisMonth++;
                        fallenDueLifetime++;
                        iterator.remove();
                    }
                }
                continue;
            }

            if (loan.isMatured()) {
                String sector = loan.getSector();
                maturedPrincipal.put(sector,
                        maturedPrincipal.getOrDefault(sector, 0.0) + loan.getOutstandingPrincipal());
                // An interim loan repaid at its term (round 5): counted, then
                // paid or rolled like any loan that falls due.
                if (loan instanceof InterimLoan) interimMaturedThisMonth.merge(sector, loan.getOutstandingPrincipal(), Double::sum);
                iterator.remove();
            }
        }
    }

    /** Reads and clears the principal that fell due this month for one sector. */
    public double takeMaturedPrincipal(String sector) {
        double due = maturedPrincipal.getOrDefault(sector, 0.0);
        maturedPrincipal.put(sector, 0.0);
        repaidThisMonth += due;
        repaidBySector.merge(sector, due, Double::sum);
        return due;
    }

    /**
     * Underwrites a loan if the sector is short, and returns the proceeds -
     * and since 0.7.12 sells a bond in part of its place where one is
     * cheaper, whose proceeds wait for the settle (takeBondProceeds()).
     *
     * @param cash        the sector's cash after the month has settled
     * @param monthlyLoss this month's loss, if any - used to size the buffer
     * @param month       the current month, for the maturity schedule
     * @return the amount lent, which the caller must add to that sector's cash
     */
    public double coverShortfall(String sector, double cash, double monthlyLoss, int month) {

        // The month the defaults write an interim loan in (round 5): every
        // sector passes here once a month, at the credit settle.
        monthNow = month;

        if (cash >= 0) {
            return 0;
        }

        // A sector in default cannot raise money. Its cash stays negative, which
        // is the honest picture and the pressure that makes it shed capacity -
        // until the month's defaults, since round 4 (CAN'T PAY MEANS DEFAULT).
        if (isBorrowingBlocked(sector)) {
            shortReason.put(sector, ShortReason.BANNED);
            return 0;
        }

        double buffer = Math.max(monthlyLoss, 0) * BUFFER_MONTHS;
        /*
         * ...GROSSED UP FOR THE LOAN'S FEE (0.7.7), which comes out of what
         * the sector is handed: it borrows enough to be handed the hole and
         * the buffer. Without it a loan that exactly fills a hole leaves the
         * fee as a new one, and a sector with nothing coming in borrows the
         * fee on the fee every month after.
         */
        double amount = (-cash + buffer) / (1 - Bank.LOAN_FEE);

        /* =====================================================================
           ...AND NOBODY LENDS PAST THE CEILING

           Until 2026-09-10 there was no test here of any kind. A negative
           balance got a loan: any size, at any leverage, every month, for ever.
           The only gate was whether the sector was already inside its
           twelve-month exclusion - which is a test of what happened LAST year,
           not of whether this loan can be repaid.

           Which left this class enforcing one half of its own rule.
           restructure() called a sector insolvent the moment its principal
           passed assets x INSOLVENCY_TRIGGER and wrote it down to
           RESTRUCTURE_TARGET (the slice's default point since 0.7.8);
           underwriting knew nothing about either number and
           would cheerfully lend straight through the line, so the cycle was:
           lend past insolvency -> write off 60% of it -> block for twelve
           months -> lend past insolvency again.

           Measured over 1,202 months: fifty restructures, one somewhere every
           two years, and INDUSTRY alone went bankrupt twenty-seven times - once
           every forty-five months, which is not a default rate, it is a firm
           being refinanced on a loop. It had lost money in 1,063 of its 1,063
           months of existence. The bank ate all of it: write-offs came to 125%
           of every dollar of interest it earned in a century, and the write-off
           was the second-largest reason it kept failing.

           The first version of this test stopped AT the insolvency line, which
           turned out to be the same mistake one notch up - see the note on
           MAX_LOAN_TO_ASSETS. The ceiling now sits below the line, and it is
           read through borrowingRoom() so the investment desk reads the same
           one.

           WHAT HAPPENS INSTEAD is what the comment above already promised: the
           cash stays negative, and that is "the pressure that makes it shed
           capacity". A sector that cannot cover its losses with debt has to
           stop making them. That path exists (Game.runRetirement); unlimited
           credit was short-circuiting it. Since 0.7.12 round 4 the cash stays
           negative only until the month's defaults: what no lender covers
           defaults that month (CAN'T PAY MEANS DEFAULT).
           ===================================================================== */
        double room = borrowingRoom(sector);
        // What the 0.7.8 capital rule would have let it have, for the
        // playtest's count of what the open line lent past it (round 5).
        double oldRoom = Math.min(room, capitalRoom(sector));
        // Why, if it is left short (round 4): the bank shut, or the desk's own
        // ceiling on the sector - since round 5 the bank's capital rule does
        // not reach a working-capital line (CREDIT LINES STAY OPEN). Read only
        // if it is still short once the loan and any bond are in its till.
        shortReason.put(sector, !lendingOpen ? ShortReason.BANK_SHUT : ShortReason.CEILING);
        // ...and a sector already past the default point is that first,
        // whatever else would have stopped the loan.
        if (pastDefaultPoint(sector, cash)) shortReason.put(sector, ShortReason.PAST_DEFAULT_POINT);

        /*
         * ...WITH AN INTEREST RESERVE ABOVE THE CEILING. The investment desk
         * funds a building against the building itself, up to the line - so a
         * sector that has just built its first plant on credit sits ABOVE the
         * shortfall ceiling, and for the year or two before the plant earns
         * its keep it cannot borrow the interest on the loan that built it.
         * Measured on the playtest: Real Estate at -$142M by month 266 with
         * every door let, and 21 households with nowhere to live for eighty
         * months, because the landlord that had borrowed to house them could
         * not borrow the interest. A construction lender carries that as an
         * interest reserve, and so does this one: up to this month's interest
         * bill, and only up to the write-down line, so the reserve can never
         * become the old spiral. Losses beyond the interest are still refused.
         */
        if (amount > room) {
            double interestDue = Math.max(0, getMonthlyInterest(sector));
            // ...and it keeps a borrower going, so the bank's capital rule
            // does not reach it (0.7.8): a bank under its minimum still funds
            // the interest on the loans it has - see setCapitalRule().
            double reserve = Math.min(interestDue / (1 - Bank.LOAN_FEE),
                    ceilingRoom(sector, INSOLVENCY_TRIGGER));
            room = Math.max(room, Math.min(amount, reserve));
            oldRoom = Math.max(oldRoom, Math.min(amount, reserve));
        }

        /*
         * ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2): not the
         * ceiling's loan, not the reserve, not the rollover of what fell due,
         * and not a bond in their place, to a sector past INSOLVENCY_TRIGGER
         * as it stands or once lent. Its till stays short instead - see the
         * section of that name.
         */
        double most = Math.min(amount, room);
        if (most > 0 && pastDefaultPoint(sector, cash, most, most - feeOn(most))) {
            refusedAtDefaultPoint.merge(sector, most, Double::sum);
            shortReason.put(sector, ShortReason.PAST_DEFAULT_POINT);
            return 0;
        }

        /*
         * ...AND A BOND WHERE ONE IS CHEAPER (0.7.12). The desk asks the bond
         * market how it would finance what the sector needs: a bond, where
         * one clears cheaper than the bank's loan with its costs spread over
         * its life, and the bank for the rest - never more, together, than
         * the bank would have lent ("within the bank's limit"; BondMarket,
         * WHO ISSUES, AND WHEN), and the bond never past the ceiling: the
         * interest reserve above it is the bank's alone. The proceeds wait
         * for the settle (takeBondProceeds()); what this returns is the
         * loan, as it always did.
         */
        double asked = Math.min(amount, room);
        double bondRaised = 0;
        if (bondDesk != null) {
            double want = Math.max(0, (-cash + buffer) / (1 - Bank.LOAN_FEE));
            double bondRoom = bondCeilingRoom(sector, MAX_LOAN_TO_ASSETS);
            double loanRoom = Math.min(want, room);
            double loanRate = priceSector(sector, Math.max(loanRoom, 0), 0);
            Plan plan = bondDesk.plan(sector, want, loanRate, loanRoom, bondRoom, 0, month);
            if (plan != null) {
                shortfallPlans.put(sector, plan);
                if (plan.hasBond()) {
                    double proceeds = bondDesk.issue(plan, month);
                    bondProceeds.merge(sector, proceeds, Double::sum);
                    shortfallLent.merge(sector, plan.bondFace(), Double::sum);
                    bondRaised = plan.bondFace();
                    /*
                     * ...AND THE LOAN BESIDE IT HANDS WHAT THE BOND DID NOT
                     * (0.7.12 round 7; the gross-up decided on round 6's
                     * record). A bond hands the till its face less its
                     * issuing costs, and the plan was struck on the loan's
                     * gross-up alone, so the till used to end the month
                     * short by the costs less the fee the bond saved - a
                     * cash-flow default with a slice, 3,032 of them over
                     * round 6's eight default runs. So the bank lends what
                     * the bond's proceeds left of the shortfall and its
                     * buffer, grossed for the loan's fee, exactly as a
                     * building's loan does since round 5 (Game's
                     * sectorInvestor()): the till is handed what it was
                     * short, and the bond's costs ride in the loan's face,
                     * a small loan beside a bond that raised it all. The
                     * costs are booked where they always were - paid to the
                     * bank as underwriter out of the proceeds
                     * (BondMarket.issue()).
                     */
                    amount = Math.min(Math.max(0, -cash + buffer - proceeds) / (1 - Bank.LOAN_FEE), room);
                }
            }
        }

        amount = Math.min(amount, room);
        countOpenLine(sector, Math.max(0, amount) + bondRaised, Math.min(asked, oldRoom));

        if (amount <= 0) {
            return 0;
        }

        issueLoan(sector, amount, month);
        shortfallLent.merge(sector, amount, Double::sum);

        return amount;
    }

    /*
     * WHAT THE OPEN LINE LENT WHILE THE BANK WAS SHORT OF CAPITAL (round 5),
     * per sector, the month's: all of it - the loan and any bond in its place
     * - while the capital rule was on, and the part of it past what the 0.7.8
     * rule would have lent. For the playtest; not saved.
     */
    private final Map<String, Double> lineLentRationed = new LinkedHashMap<>();
    private final Map<String, Double> lineLentPastOldRule = new LinkedHashMap<>();

    private void countOpenLine(String sector, double lent, double oldRule) {
        if (!(lent > 0) || !capitalRuleOn()) return;
        lineLentRationed.merge(sector, lent, Double::sum);
        if (lent > oldRule) lineLentPastOldRule.merge(sector, lent - Math.max(0, oldRule), Double::sum);
    }

    /** True this month when the bank's capital rule limits new lending: under its target, or under its minimum. */
    public boolean capitalRuleOn() { return keepGoingOnly || !Double.isInfinite(capitalGrowth); }

    /** What the working-capital line lent this sector this month while the capital rule was on (round 5). */
    public double getLineLentRationed(String sector) { return lineLentRationed.getOrDefault(sector, 0.0); }
    /** ...of it, what the 0.7.8 rule would have refused. */
    public double getLineLentPastOldRule(String sector) { return lineLentPastOldRule.getOrDefault(sector, 0.0); }

    /**
     * WHAT THE SHORTFALL DESK LENT EACH SECTOR THIS MONTH, loans and bonds at
     * face (0.7.12 round 2): the new borrowing that rolled what fell due, which
     * a dividend is paid after (Game.payDividends(): net income less the
     * principal repaid NET of this - free cash flow to equity). The month's,
     * from the settle; not saved, read the same month.
     */
    private final Map<String, Double> shortfallLent = new LinkedHashMap<>();

    /** ...one sector's. */
    public double getShortfallLentThisMonth(String sector) { return shortfallLent.getOrDefault(sector, 0.0); }

    /* =====================================================================
       ...AND NOTHING PAST THE DEFAULT POINT (0.7.12, round 2)

       Jerus, 2026-09-25: "Stop at the default point." The bank stops
       covering a firm's cash shortfalls once its debt passes
       INSOLVENCY_TRIGGER times its assets, and past it the firm restructures
       instead of borrowing more: real banks stop lending to a firm that is
       already insolvent. THE LINE IS INSOLVENCY_TRIGGER because it is where
       the model's firms default - T in defaultProbability(), the leverage at
       which half a sector's firms default within a year - and a loan past it
       is a loan to firms the model has already put in default.

       BEFORE AND AFTER THE LOAN, AND IT IS THE "BEFORE" THAT REFUSES. A loan
       lands on both sides of the sheet: the principal, and the cash it hands
       the till. So a sector past the line that borrows enough is back under
       it on paper, and "after the loan" alone would lend it: in round 1's
       seed 4 the investment desk lent Luxury Retail $541M at 1.71 times its
       assets, $387M of it filling its overdraft, and left it at 1.35. And
       under the line, "after" follows from "before" for any loan whose cash
       stays on the sheet - so what refuses is always a sector already past
       the line, which is Jerus's wording. Both are asked.

       ON THE QUARTER SINCE ROUND 3 (Jerus: "Read the quarter"): what the
       sector owed over what it owned, each averaged over its last
       STATEMENT_MONTHS month-end readings - the reading the bank already
       prices a loan and strikes its allowance on (THE BANK READS A BORROWER
       FROM ITS LAST QUARTER). Round 2 read the sheet as it stood when the
       loan was written, and Luxury Retail, whose two-month restock swings a
       sixth of its assets from one month to the next, read 1.3 and 2.3-3.4 in
       turn: past the line at 825 month-ends of seed 7, lent in the next month
       776 times, refused $3.6M in all. A sector that reads under the line in
       its month and over it on its quarter is refused; one over in its month
       and under on its quarter is lent, as the price and the allowance
       already treat it. Nothing behind it - the quarter's assets at or below
       nothing, against debt or an overdraft - is past the line, as the
       backstop reads it.

       A SECTOR JUST RESTRUCTURED has no quarter: the backstop drops its
       readings (0.7.8, "keep quarterly"), and it reads as it stands - its
       month's refresh with its till moved to where it is now (assetsNow()) -
       until the next month-end files the first of a new quarter. The backstop
       bans it for BORROWING_BLOCKED_MONTHS besides, so it asks for nothing
       in those months anyway. A new sector reads the same way until it has
       a reading.

       EVERY DOOR:
         the shortfall desk, its interest reserve and the rollover of a
           loan or a bond that fell due, which are the shortfall desk, and
           the bond it may sell in the loan's place (coverShortfall());
         a landlord's mortgage at the end of its term, which renews only
           short of the line and otherwise falls due whole (processMonth());
         and the investment desk and a landlord's new mortgage, whose loan
           covers the till's overdraft before it buys the building
           (Game.buildFor(): the cost less the cash) - a shortfall loan in
           substance (canFundProject(), projectLoanRoom(), projectBondRoom(),
           canFundMortgage()).

       WHAT THE FIRM DOES INSTEAD is what the model does with a sector nobody
       will lend to: its till goes short, and since round 4 it defaults that
       month (CAN'T PAY MEANS DEFAULT) - the slice writes down the part of it
       that cannot pay, or the curve's share for its leverage where that is
       larger (defaultSlice()); what is still unpaid is lent as interim
       financing if the write-down has brought it back under the line, and
       otherwise the whole sector goes to the backstop, which writes it down,
       closes the overdraft and bans it - or, inside a ban already, only
       closes the overdraft (INTERIM FINANCING, restructure(sector, true)).
       The distress rule sells its plant meanwhile (Game.runRetirement()).
       Round 2 left the till short as an overdraft - a debt to nobody that
       bore no interest - until the backstop forgave it; rounds 4 and 5
       ended that.
       ===================================================================== */

    /** A sector's assets on the sheet as it stands, its till at `cash`: the month's refresh (getAssets()), with the till it read (getCash()) moved to this one. */
    public double assetsNow(String sector, double cash) {
        return getAssets(sector) - getCash(sector) + cash;
    }

    /**
     * Past the default point ON ITS QUARTER (round 3): owing more than
     * INSOLVENCY_TRIGGER times what it owned, averaged over its last
     * STATEMENT_MONTHS month-end readings (quarterPrincipal(), quarterAssets())
     * - or anything, a debt or an overdraft at `cash`, against nothing. A
     * sector with no reading yet - a new one, or one a backstop has just
     * restarted - reads as it stands (assetsNow()). Never for a sector
     * whose sheet nobody has reported (tillReported).
     */
    public boolean pastDefaultPoint(String sector, double cash) {
        if (!tillReported.contains(sector)) return false;
        double[] read = defaultPointReading(sector, cash);
        double owed = read[0], assets = read[1];
        if (!(assets > 0)) return owed > 0 || cash < 0;
        return owed > assets * INSOLVENCY_TRIGGER;
    }

    /** ...or past it once lent `amount`, `handed` of it paid into the till: before the loan or after it, on the same reading. */
    public boolean pastDefaultPoint(String sector, double cash, double amount, double handed) {
        if (pastDefaultPoint(sector, cash)) return true;
        if (!(amount > 0) || !tillReported.contains(sector)) return false;
        double[] read = defaultPointReading(sector, cash);
        double assets = read[1] + Math.max(0, handed);
        if (!(assets > 0)) return true;
        return read[0] + amount > assets * INSOLVENCY_TRIGGER;
    }

    /** {owed, owned} as the default point reads them: the quarter's averages, or with no reading the sheet as it stands, its till at `cash` (round 3). */
    private double[] defaultPointReading(String sector, double cash) {
        if (getStatementCount(sector) > 0) return new double[] { quarterPrincipal(sector), quarterAssets(sector) };
        return new double[] { getPrincipal(sector), assetsNow(sector, cash) };
    }

    /** The leverage the default point reads a sector at now: its quarter's, or as it stands with no reading - for the investor's words and the screens. */
    public double defaultPointLeverage(String sector) {
        double[] read = defaultPointReading(sector, getCash(sector));
        return read[1] > 0 ? read[0] / read[1] : Double.POSITIVE_INFINITY;
    }

    /* What the default point refused this month, per sector: what the shortfall desk would otherwise have lent, the mortgages that fell due whole rather than renew, and the projects the investment desk turned down. The month's, read in the month (the playtest, the investor's words), not saved. */
    private final Map<String, Double> refusedAtDefaultPoint = new LinkedHashMap<>();
    private final Map<String, Double> notRenewedAtDefaultPoint = new LinkedHashMap<>();
    private final java.util.Set<String> refusedProjectAtDefaultPoint = new java.util.HashSet<>();

    /** What the shortfall desk would have lent this sector this month and did not, because it is past the default point (0.7.12, round 2). */
    public double getRefusedAtDefaultPoint(String sector) { return refusedAtDefaultPoint.getOrDefault(sector, 0.0); }

    /** ...every sector's. */
    public double getRefusedAtDefaultPoint() {
        double t = 0;
        for (double v : refusedAtDefaultPoint.values()) t += v;
        return t;
    }

    /** The mortgage balances that fell due whole this month rather than renew, because the landlords were past the default point. */
    public double getNotRenewedAtDefaultPoint(String sector) { return notRenewedAtDefaultPoint.getOrDefault(sector, 0.0); }

    /** True when the investment desk turned this sector's project down this month because it is past the default point. */
    public boolean wasRefusedAtDefaultPoint(String sector) { return refusedProjectAtDefaultPoint.contains(sector); }

    /**
     * THE CEILING A BOND IS HELD TO (0.7.12): the line at this multiple of
     * its assets, and the ban, as for a loan. The bank's own state - a
     * failed bank, one short of capital - is in what the bank would lend,
     * which the bond may not exceed either (BondMarket.plan()).
     */
    public double bondCeilingRoom(String sector, double multiple) {
        if (isBorrowingBlocked(sector)) return 0;
        double ceiling = Math.max(0, getAssets(sector)) * multiple;
        return Math.max(0, ceiling - getPrincipal(sector));
    }

    /**
     * WHAT THE BANK WILL LEND OF A PROJECT (0.7.12): the whole of it if
     * canFundProject() says yes, and otherwise what its capital rule leaves
     * under the default point after the deal - the most a bond and a loan
     * may raise of it together ("within the bank's limit").
     */
    public double projectLoanRoom(String sector, double amount) {
        if (!(amount > 0) || !lendingOpen || isBorrowingBlocked(sector) || keepGoingOnly) return 0;
        if (pastDefaultPoint(sector, getCash(sector))) return 0;   // round 2
        double line = (Math.max(0, getAssets(sector)) + amount) * INSOLVENCY_TRIGGER - getPrincipal(sector);
        return Math.max(0, Math.min(amount, Math.min(line, capitalRoom(sector))));
    }

    /** ...and the bond's own ceiling: the same default point after the deal, and the ban. */
    public double projectBondRoom(String sector, double amount) {
        if (!(amount > 0) || isBorrowingBlocked(sector)) return 0;
        if (pastDefaultPoint(sector, getCash(sector))) return 0;   // round 2
        double line = (Math.max(0, getAssets(sector)) + amount) * INSOLVENCY_TRIGGER - getPrincipal(sector);
        return Math.max(0, Math.min(amount, line));
    }

    /**
     * How much more this sector may borrow today, from either desk.
     *
     * THE ONE DEFINITION OF THE CEILING. The shortfall desk above asks this,
     * and the investment desk (Game's Investor.canBorrow) asks
     * canFundProject(), which holds the same ban and the same shut lender
     * against the line after the deal, so a sector cannot be refused the
     * money to keep the lights on and then lent the money to expand. Until
     * 2026-09-10 the investment path enforced the ban
     * and not the ceiling: measured over 4,000 months, fifteen investment
     * loans were written past it, by $563M in total, the worst at 1.84 times
     * assets against a 1.50 rule.
     *
     * Zero while the sector is barred, and zero while the lender itself is
     * shut - see lendingOpen. From 0.7.8 it was also no more than the bank's
     * capital rule let the sector's debt grow this month (capitalRoom()); since
     * 0.7.12 round 5 it is a WORKING-CAPITAL LINE, which the capital rule does
     * not reach - see CREDIT LINES STAY OPEN. The investment desk's own
     * room is canFundProject()'s and projectLoanRoom()'s, which still read it.
     */
    public double borrowingRoom(String sector) {
        return ceilingRoom(sector, MAX_LOAN_TO_ASSETS);
    }

    /* =====================================================================
       ...AND WHAT THE NEXT SETTLE WOULD DO, ASKED AHEAD (0.7.12, round 6)

       The clearing hands each buyer what it can pay for (Sector, BUY ONLY
       WHAT IT CAN PAY FOR), and part of that is the credit the next settle
       would give it. The two below answer for the settle ahead of time, on
       its own rules and nothing new: what falls due there, and what the
       working-capital line would hand the till.
       ===================================================================== */

    /**
     * The principal that falls due at the next settle, as processMonth() will
     * park it: each loan that matures, whole; each mortgage's next payment's
     * principal, and its whole balance where the payment ends the term and
     * the lender would not renew it (shut, or the sector past the default
     * point). Its bonds' is the bond market's (BondMarket.maturingFace()).
     */
    public double principalDueNextMonth(String sector) {
        double due = 0;
        for (BusinessDebt loan : loans) {
            if (!loan.getSector().equals(sector)) continue;
            if (loan instanceof Mortgage m) {
                double owed = Math.max(0, m.getOutstandingPrincipal());
                int left = m.getAmortizationLeft();
                if (left <= 0 || !(owed > 0)) { due += owed; continue; }
                double pay = Mortgage.payment(owed, m.getAnnualRate(), left);
                double principal = left == 1 ? owed
                        : Math.max(0, Math.min(owed, pay - owed * m.getAnnualRate() / 12));
                due += principal;
                boolean termEnds = m.getRemainingMonths() <= 1 && left > 1 && owed - principal > 0;
                if (termEnds && !(lendingOpen && !pastDefaultPoint(sector, getCash(sector)))) due += owed - principal;
            } else if (loan.getRemainingMonths() <= 1) {
                due += Math.max(0, loan.getOutstandingPrincipal());
            }
        }
        return due;
    }

    /**
     * What the working-capital line would hand this sector's till at the next
     * settle, net of its fee: coverShortfall()'s room on coverShortfall()'s
     * rules - the ceiling at MAX_LOAN_TO_ASSETS on what it will owe once
     * what falls due is paid, or the interest reserve above it where that is
     * larger - and nothing while the lender is shut, the sector barred, or
     * its quarter past the default point before the loan or after it.
     *
     * @param cash the till the settle will read
     * @param due  the principal that falls due there, its loans' and bonds'
     */
    public double workingCapitalLine(String sector, double cash, double due) {
        if (!lendingOpen || isBorrowingBlocked(sector)) return 0;
        if (pastDefaultPoint(sector, cash)) return 0;
        double assets = Math.max(0, getAssets(sector));
        double owed = Math.max(0, getPrincipal(sector) - Math.max(0, due));
        double room = Math.max(0, assets * MAX_LOAN_TO_ASSETS - owed);
        double reserve = Math.min(Math.max(0, getMonthlyInterest(sector)) / (1 - Bank.LOAN_FEE),
                Math.max(0, assets * INSOLVENCY_TRIGGER - owed));
        double most = Math.max(room, reserve);
        if (!(most > 0) || pastDefaultPoint(sector, cash, most, most - feeOn(most))) return 0;
        return most - feeOn(most);
    }

    /**
     * Whether the INVESTMENT desk will fund a project of this size.
     *
     * A different test from borrowingRoom(), and the difference is deliberate
     * and measured. A shortfall loan covers losses the borrower is already
     * making, with nothing behind it but the assets it has - so it stops well
     * short of the line. An investment loan buys a building, and the building
     * is the collateral: it lands on the balance sheet the month it is bought.
     * So the honest test is the one this class already applies, asked of the
     * balance sheet AFTER the deal - principal plus the loan, against assets
     * plus the building - and the building is taken at the loan's own value,
     * which is the conservative end of what it will book at.
     *
     * Holding the investment desk to the shortfall ceiling instead was
     * measured over 4,000 months: the city ended at 15,353 people and 21.2%
     * unemployed, worst 42.4%, against 19,562 and 15.6% before. Every early
     * sector has almost no assets, so the desk that funds every first
     * expansion had been cut to a rescue desk's limit. Capping it at the bare
     * write-down line without counting the building did the same thing one
     * notch later: 17,077 people, worst 42.2%.
     *
     * What neither desk may do is leave the borrower past the line. The
     * investment path enforced the ban and not the line until 2026-09-10:
     * fifteen loans written past it in 4,000 months, the worst at 1.84 times
     * assets. And, like the shortfall desk, it lends nothing while the sector
     * is barred or the bank is shut.
     */
    public boolean canFundProject(String sector, double amount) {
        if (amount <= 0) return false;
        if (!lendingOpen) return false;
        if (isBorrowingBlocked(sector)) return false;
        // Not past the default point as it stands (0.7.12, round 2): the
        // project's loan covers the till's overdraft before the building
        // (Game.buildFor()), a shortfall loan in substance.
        if (pastDefaultPoint(sector, getCash(sector))) {
            refusedProjectAtDefaultPoint.add(sector);
            return false;
        }
        double assetsAfter = Math.max(0, getAssets(sector)) + amount;
        if (getPrincipal(sector) + amount > assetsAfter * INSOLVENCY_TRIGGER) return false;
        // ...and what the bank's capital lets it lend this month (0.7.8): a
        // building is new lending, never keeping a borrower going - GROWTH,
        // which the capital rule still rations (CREDIT LINES STAY OPEN).
        if (keepGoingOnly || amount > capitalRoom(sector)) {
            refusedForCapital.add(sector);
            refusedProjectForCapital.add(sector);
            return false;
        }
        return true;
    }

    /** The ceiling alone: nothing while the lender is shut or the sector barred. */
    private double ceilingRoom(String sector, double multiple) {
        if (!lendingOpen) return 0;
        if (isBorrowingBlocked(sector)) return 0;
        double ceiling = Math.max(0, getAssets(sector)) * multiple;
        return Math.max(0, ceiling - getPrincipal(sector));
    }

    /* =====================================================================
       THE LANDLORDS' MORTGAGES (0.7.11)

       Jerus, 2026-09-24: "Mortgages", on "CMHC (Canada)" terms, "Insured by
       the city". A residential building is bought with a Mortgage: the
       landlord puts at least 15% of the cost from its own funds, the lender
       advances the rest - at most Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the
       cost - with CMHC's premium added to it and paid to the treasury, at
       the insured rate, paid down over Mortgage.MORTGAGE_AMORTIZATION_MONTHS
       and renewed every Mortgage.MORTGAGE_TERM_MONTHS. The lender's test is
       the building's net operating income against the payment
       (Mortgage.MORTGAGE_DEBT_COVERAGE), asked in Game.consider(), which
       also trims the order to what the landlord's own funds can put down.

       THE SAME DESK, THE SAME BOOK, THE SAME FEE. A mortgage is funded as a
       project loan is: written here, the principal counted in the month's
       lending (the bank pays it out at the settle), Bank.LOAN_FEE of the
       principal kept back from what the borrower is handed. What differs is
       where the premium goes - to the treasury, out of the principal, so the
       landlord is handed the loan less the fee (getPremiumsThisMonth(), and
       SectorBooks takes it off what the sector received).

       WHAT THE BANK'S CAPITAL RULE DOES WITH THEM: nothing while the
       risk-based requirement is the one that binds. An insured mortgage
       weighs nothing, so under the risk weights it ties up no capital, and
       the rule that rations a bank short of capital (setCapitalRule(),
       capitalRoom()) does not reach it. Until this batch the rule read a
       sector's WHOLE debt. A landlord's mortgage would then have been
       refused by a bank under its minimum as though it were new risk, and
       would have used up the room its shortfall loans had; every payment
       would have made room for new uninsured lending. The rule reads
       uninsured debt alone (getUninsuredPrincipal()).

       ...AND EVERYTHING WHEN THE LEVERAGE RATIO BINDS (round 2). The
       leverage requirement counts a dollar lent whatever it weighs
       (Bank.LEVERAGE_RATIO_MIN). When it is the larger, a mortgage uses
       the very capital the bank is short of, like any loan. Then the rule
       reads the whole debt, mortgages included, and canFundMortgage() asks
       it (setCapitalRule(), insuredRationed). Rationing everyone else
       while the mortgages that put the bank there went on unrationed would
       put the whole squeeze on the other borrowers.

       What canFundMortgage() asks besides is what canFundProject() asks of
       every desk: the lender open, no ban, and the borrower not left past
       the default point after the deal, the building counted.
       ===================================================================== */

    /** The premiums added to the mortgages written this month, and by sector: the treasury's revenue line. */
    private double premiumsThisMonth;
    private final Map<String, Double> premiumsBySector = new LinkedHashMap<>();

    /**
     * THE PRINCIPAL THE MORTGAGES' PAYMENTS TOOK THIS MONTH, by sector - part
     * of getRepaidThisMonth(), and the figure the Bank tab and the landlords'
     * screen show beside the payment. A flow read the month after it is
     * struck, so it is saved (getMortgageRepaidToSave()).
     */
    private final Map<String, Double> mortgageRepaidBySector = new LinkedHashMap<>();

    /** The month's mortgages written, renewed, and fallen due because the lender could not renew them - and the same over the run, for the playtest (not saved, a count for the run). */
    private int mortgagesWrittenThisMonth, renewedThisMonth, fallenDueThisMonth;
    private int renewedLifetime, fallenDueLifetime;

    /**
     * WHAT THE INSURANCE PAID THIS MONTH, by sector: what the month's
     * write-downs took off insured mortgages. The treasury pays it to the
     * bank (Game.runPrivateInvestment(), TreasuryLine.MORTGAGE_INSURANCE_
     * CLAIMS), so the bank books only the rest of the write-off as its loss.
     * A MORTGAGE IS A FIRST-LIEN LOAN, so since 0.7.12 round 2 a slice takes
     * a loan's loss off it like any other loan (RECOVERIES BY INSTRUMENT):
     * the claim is the slice's share h of the insured balance times
     * LOAN_LOSS_GIVEN_DEFAULT - a quarter of it, where the uniform loss took
     * 60% - and the whole balance in the backstop, which recovers nothing.
     * Struck by restructureInsolventSectors() with writtenOffThisMonth; and
     * the same over the city's life, which is state - saved with the
     * write-off totals, for the reason they are.
     */
    private final Map<String, Double> insuredWrittenOffThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> insuredWrittenOffTotal = new LinkedHashMap<>();

    /** The premiums written over the city's life. State, saved: the insurance book's other side. */
    private double premiumsTotal;

    /** The reason canFundMortgage() last refused, or null. */
    private String mortgageRefusal;

    /** canFundMortgage()'s refusal when the bank behind the lender has failed (lendingOpen). */
    public static final String MORTGAGE_BANK_SHUT = "the bank is shut";
    /** ...when the sector is serving a borrowing ban. */
    public static final String MORTGAGE_BANNED = "borrowing ban";
    /** ...when the loan would be more than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost - the down payment, Mortgage.Decision.DOWN_PAYMENT. */
    public static final String MORTGAGE_DOWN_PAYMENT = Mortgage.Decision.DOWN_PAYMENT;
    /** ...when the deal would leave the borrower owing past INSOLVENCY_TRIGGER times what it owns. */
    public static final String MORTGAGE_PAST_DEFAULT_POINT = "past the default point";
    /** ...when the bank's capital rule has no room for it: only while the leverage requirement binds (round 2; setCapitalRule()). */
    public static final String MORTGAGE_CAPITAL = "the bank's capital";
    /** ...when there is nothing to borrow. */
    public static final String MORTGAGE_NOTHING = "nothing to borrow";

    /**
     * Whether the lender will write a mortgage for this shortfall on a
     * building of this cost: open, the sector not barred, the loan no more
     * than Mortgage.MORTGAGE_MAX_LOAN_TO_COST of the cost, and the borrower
     * not past the default point after the deal, the building counted at the
     * mortgage's principal as canFundProject() counts it. The bank's capital
     * rule only while the leverage requirement binds - see THE LANDLORDS'
     * MORTGAGES.
     *
     * @param shortfall what the order costs past the landlord's till
     * @param cost      what the order costs
     */
    public boolean canFundMortgage(String sector, double shortfall, double cost) {
        mortgageRefusal = null;
        if (!(shortfall > 0)) { mortgageRefusal = MORTGAGE_NOTHING; return false; }
        if (!lendingOpen) { mortgageRefusal = MORTGAGE_BANK_SHUT; return false; }
        if (isBorrowingBlocked(sector)) { mortgageRefusal = MORTGAGE_BANNED; return false; }
        double loan = Mortgage.loanFor(shortfall);
        if (loan > Mortgage.MORTGAGE_MAX_LOAN_TO_COST * cost * (1 + 1e-9)) {
            mortgageRefusal = MORTGAGE_DOWN_PAYMENT;
            return false;
        }
        double principal = loan * (1 + Mortgage.premiumRate());
        double assetsAfter = Math.max(0, getAssets(sector)) + principal;
        // ...as it stands, too (0.7.12, round 2): see canFundProject().
        if (pastDefaultPoint(sector, getCash(sector))
                || getPrincipal(sector) + principal > assetsAfter * INSOLVENCY_TRIGGER) {
            mortgageRefusal = MORTGAGE_PAST_DEFAULT_POINT;
            return false;
        }
        if (insuredRationed && principal > capitalRoom(sector)) {
            mortgageRefusal = MORTGAGE_CAPITAL;
            refusedForCapital.add(sector);
            refusedMortgageForCapital.add(sector);
            return false;
        }
        return true;
    }

    /** Why canFundMortgage() last said no, or null if it said yes. */
    public String getMortgageRefusal() { return mortgageRefusal; }

    /**
     * WRITES A MORTGAGE for this shortfall: the loan that covers it once the
     * fee is paid (Mortgage.loanFor()), the premium added, at the insured
     * rate. The bank lends the principal at the settle; the borrower is owed
     * the loan less the fee - which is the shortfall - and the treasury the
     * premium (getPremiumsThisMonth(); Game moves both).
     */
    public Mortgage issueMortgage(String sector, double shortfall, int month) {
        double loan = Mortgage.loanFor(shortfall);
        Mortgage m = new Mortgage(sector, loan, month, insuredMortgageRate, true);
        double principal = m.getOutstandingPrincipal();
        writtenThisMonth.add(new Written(sector, principal,
                pricingLeverage(quarterPrincipal(sector) + principal,
                        Math.max(0, quarterAssets(sector)) + principal), insuredMortgageRate, true));
        loans.add(m);
        lentThisMonth += principal;
        lentBySector.merge(sector, principal, Double::sum);
        double fee = feeOn(principal);
        feesThisMonth += fee;
        feesBySector.merge(sector, fee, Double::sum);
        premiumsThisMonth += m.getPremium();
        premiumsBySector.merge(sector, m.getPremium(), Double::sum);
        premiumsTotal += m.getPremium();
        mortgagesWrittenThisMonth++;
        rates.put(sector, priceSector(sector));
        return m;
    }

    /** The premiums on the mortgages written this month: the treasury's revenue line. */
    public double getPremiumsThisMonth() { return premiumsThisMonth; }
    /** ...by sector, which its cash flow statement takes off what it was handed. */
    public double getPremiumsThisMonth(String sector) { return premiumsBySector.getOrDefault(sector, 0.0); }
    /** The premiums written over the city's life. */
    public double getPremiumsTotal() { return premiumsTotal; }

    /** The mortgages written this month, renewed this month, and fallen due this month because the lender could not renew them. */
    public int getMortgagesWrittenThisMonth() { return mortgagesWrittenThisMonth; }
    public int getRenewedThisMonth()          { return renewedThisMonth; }
    public int getFallenDueThisMonth()        { return fallenDueThisMonth; }
    /** ...over the run, for the playtest; not saved. */
    public int getRenewedLifetime()           { return renewedLifetime; }
    public int getFallenDueLifetime()         { return fallenDueLifetime; }

    /** Every mortgage one sector owes, in the order written. */
    public List<Mortgage> getMortgages(String sector) {
        List<Mortgage> out = new ArrayList<>();
        for (BusinessDebt loan : loans) {
            if (loan instanceof Mortgage m && loan.getSector().equals(sector)) out.add(m);
        }
        return out;
    }

    /** Every mortgage in the city. */
    public List<Mortgage> getMortgages() {
        List<Mortgage> out = new ArrayList<>();
        for (BusinessDebt loan : loans) if (loan instanceof Mortgage m) out.add(m);
        return out;
    }

    /** How many mortgages one sector owes. */
    public int getMortgageCount(String sector) { return getMortgages(sector).size(); }

    /** What one sector owes on its mortgages. */
    public double getMortgagePrincipal(String sector) {
        double total = 0;
        for (Mortgage m : getMortgages(sector)) total += m.getOutstandingPrincipal();
        return total;
    }

    /** What every sector owes on mortgages: the bank's mortgage book. */
    public double getMortgagePrincipal() {
        double total = 0;
        for (Mortgage m : getMortgages()) total += m.getOutstandingPrincipal();
        return total;
    }

    /** What one sector owes on its insured mortgages - the part of its debt the city insures. */
    public double getInsuredPrincipal(String sector) {
        double total = 0;
        for (Mortgage m : getMortgages(sector)) if (m.isInsured()) total += m.getOutstandingPrincipal();
        return total;
    }

    /** ...every sector's: what the bank's book holds at Bank.RISK_INSURED_MORTGAGE. */
    public double getInsuredPrincipal() {
        double total = 0;
        for (Mortgage m : getMortgages()) if (m.isInsured()) total += m.getOutstandingPrincipal();
        return total;
    }

    /** What one sector owes the bank that nobody insures: its loans less its insured mortgages - what the bank's allowance reads, and its capital rule while the leverage ratio does not bind (rationedPrincipal()). Its bonds are not the bank's (0.7.12). */
    public double getUninsuredPrincipal(String sector) {
        return getLoanPrincipal(sector) - getInsuredPrincipal(sector);
    }

    /** The level payments one sector's mortgages ask next month: interest and principal together. */
    public double getMortgagePayment(String sector) {
        double total = 0;
        for (Mortgage m : getMortgages(sector)) total += m.getMonthlyPayment();
        return total;
    }

    /** ...every sector's. */
    public double getMortgagePayment() {
        double total = 0;
        for (Mortgage m : getMortgages()) total += m.getMonthlyPayment();
        return total;
    }

    /** The rate one sector's mortgages carry, weighted by what is owed on each; 0 with none. */
    public double getMortgageRate(String sector) {
        return weightedRate(getMortgages(sector));
    }

    /** ...every mortgage's. */
    public double getMortgageRate() {
        return weightedRate(getMortgages());
    }

    private static double weightedRate(List<Mortgage> ms) {
        double owed = 0, weighted = 0;
        for (Mortgage m : ms) {
            owed += m.getOutstandingPrincipal();
            weighted += m.getOutstandingPrincipal() * m.getAnnualRate();
        }
        return owed > 0 ? weighted / owed : 0;
    }

    /** The first month one of this sector's mortgages renews, or -1 with none. */
    public int getNextRenewalMonth(String sector) {
        int next = -1;
        for (Mortgage m : getMortgages(sector)) {
            int at = m.getNextRenewalMonth();
            if (next < 0 || at < next) next = at;
        }
        return next;
    }

    /** How many mortgages renew within this many months of this one. */
    public int getMortgagesRenewingWithin(int months) {
        int n = 0;
        for (Mortgage m : getMortgages()) if (m.getRemainingMonths() <= months) n++;
        return n;
    }

    /** True when every mortgage in the city is insured - which every one this build writes is. */
    public boolean allMortgagesInsured() {
        for (Mortgage m : getMortgages()) if (!m.isInsured()) return false;
        return true;
    }

    /** The principal one sector's mortgage payments took this month. */
    public double getMortgageRepaidThisMonth(String sector) { return mortgageRepaidBySector.getOrDefault(sector, 0.0); }

    /** ...every sector's. */
    public double getMortgageRepaidThisMonth() {
        double total = 0;
        for (double v : mortgageRepaidBySector.values()) total += v;
        return total;
    }

    /** The month's principal repaid on mortgages, for the save: a copy, by sector name. */
    public Map<String, Double> getMortgageRepaidToSave() { return new LinkedHashMap<>(mortgageRepaidBySector); }

    /** ...and back on load. An older save has none. */
    public void restoreMortgageRepaid(Map<String, Double> saved) {
        mortgageRepaidBySector.clear();
        if (saved == null) return;
        for (Map.Entry<String, Double> e : saved.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) mortgageRepaidBySector.put(e.getKey(), e.getValue());
        }
    }

    /** What this month's write-downs took off one sector's insured mortgages: the claim the treasury pays the bank. */
    public double getInsuredWrittenOffThisMonth(String sector) { return insuredWrittenOffThisMonth.getOrDefault(sector, 0.0); }

    /** ...every sector's: the month's claims. */
    public double getInsuredWrittenOffThisMonth() {
        double total = 0;
        for (double v : insuredWrittenOffThisMonth.values()) total += v;
        return total;
    }

    /** What write-downs have taken off one sector's insured mortgages over the city's life. */
    public double getInsuredWrittenOffTotal(String sector) { return insuredWrittenOffTotal.getOrDefault(sector, 0.0); }

    /** ...every sector's: the claims over the city's life. */
    public double getInsuredWrittenOffTotal() {
        double total = 0;
        for (double v : insuredWrittenOffTotal.values()) total += v;
        return total;
    }

    /** The insurance book's record, for the save: the premiums over the city's life. */
    public double getPremiumsTotalToSave() { return premiumsTotal; }

    /** The claims over the city's life, by sector, for the save. */
    public Map<String, Double> getInsuredWrittenOffTotals() { return new LinkedHashMap<>(insuredWrittenOffTotal); }

    /** ...and both back on load. An older save has none, which is what that city insured. */
    public void restoreInsuranceRecord(double premiums, Map<String, Double> claims) {
        premiumsTotal = Double.isFinite(premiums) ? Math.max(0, premiums) : 0;
        insuredWrittenOffTotal.clear();
        if (claims == null) return;
        for (Map.Entry<String, Double> e : claims.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) insuredWrittenOffTotal.put(e.getKey(), e.getValue());
        }
    }

    /**
     * Writes every instrument of one sector down to this share, pro rata, and
     * says what came off its insured mortgages - the claim. The one loop both
     * the slice and the backstop write down through.
     */
    private double writeDownSector(String sector, double scale) {
        return writeDownSector(sector, scale, true);
    }

    /** One sector's interim loans alone, to this share of what they were. */
    private void writeDownInterim(String sector, double scale) {
        for (BusinessDebt loan : loans) {
            if (loan instanceof InterimLoan && loan.getSector().equals(sector)) loan.writeDown(scale);
        }
    }

    /** ...its interim loans with the rest, or (false) every loan but them - they rank first (INTERIM FINANCING). */
    private double writeDownSector(String sector, double scale, boolean interimToo) {
        double insured = 0;
        for (BusinessDebt loan : loans) {
            if (!loan.getSector().equals(sector)) continue;
            if (!interimToo && loan instanceof InterimLoan) continue;
            double before = loan.getOutstandingPrincipal();
            loan.writeDown(scale);
            if (loan instanceof Mortgage m && m.isInsured()) insured += before - loan.getOutstandingPrincipal();
        }
        if (insured > 0) {
            insuredWrittenOffThisMonth.merge(sector, insured, Double::sum);
            insuredWrittenOffTotal.merge(sector, insured, Double::sum);
        }
        return insured;
    }

    /* =====================================================================
       ...AND WHAT THE BANK'S CAPITAL LETS IT LEND (0.7.8)

       Until 0.7.8 the only thing between the bank and a loan was whether it
       had failed (lendingOpen): a standing bank lent whatever the ceilings
       allowed however thin its capital, and a failed one lent nothing - a
       cliff, with nothing on the slope. The bank's capital rule is the
       slope (Bank, WHAT IT LENDS): Game hands it here at the top of every
       month, before a loan is written, as the most a borrower's debt may
       grow this month - no limit at or over the bank's target, nothing under
       its minimum, and in between a rate that rises from nothing to no
       limit. Measured against what the sector owed when the rule was set,
       so a loan that matures this month may be refinanced whatever the rule
       says (the book does not grow), and a restructure writes nothing new.
       What keeps a borrower going is outside it - the interest reserve in
       coverShortfall() - and a new building is never that. A harness that
       builds this class on its own gets no limit, which is what it had.

       CREDIT LINES STAY OPEN (0.7.12, round 5). Jerus: "When the bank is
       short of capital, rationing cuts new growth lending only. Covering a
       short month is a working-capital line, and the bank honours it up to
       the usual ceiling (90% of assets), as banks did in 2008 and 2020. A
       firm defaults only on its own bad credit: past the default point,
       banned, or over the ceiling." New loans to large US borrowers fell 47%
       in 2008 Q4 from the quarter before while borrowers drew down the lines
       they already had and banks' C&I loans spiked (Ivashina and
       Scharfstein, "Bank lending during the financial crisis of 2008",
       Journal of Financial Economics 97(3), 2010); in March 2020 firms drew
       heavily on their credit lines and loan commitments, and the banks
       funded them (Li, Strahan and Zhang, "Banks as Lenders of First
       Resort: Evidence from the COVID-19 Crisis", Review of Corporate
       Finance Studies 9(3), 2020). A bank short of capital cuts new lending,
       not the lines it has already committed. Round 4 showed what reading a
       short month as growth does once a refusal is a default: a bank between
       its minimum and its target refused sectors under the line - a sector
       owing nothing has no room at all, the room being a share of what it
       owes - they defaulted, the write-offs kept it under its target, and
       the treasury put $13.5B back in on one seed.

       EVERY DOOR THE RULE READ, AND ITS GROUP:
         WORKING CAPITAL - no longer rationed; still held to the ceiling
         (MAX_LOAN_TO_ASSETS), the default point on the quarter, the ban,
         and a bank that stands (lendingOpen):
           the shortfall desk's cover of a short month (coverShortfall(),
             borrowingRoom());
           the rollover of a loan or a bond that fell due, which is that
             same cover - the principal leaves the till short at the
             settle, and the desk lends it again;
           the bond the desk may sell in the loan's place, "within the
             bank's limit", which is the line's;
           the interest reserve above the ceiling, which the rule never
             reached (0.7.8);
           a landlord's mortgage at the end of its term, which renews at the
             settle and never read the rule (processMonth()): a refinancing.
         GROWTH - rationed as before:
           a building's loan, and a bond with it (canFundProject(),
             projectLoanRoom());
           a new mortgage, while the leverage requirement binds
             (canFundMortgage()).
       What the line lends counts against the rule's room like any debt, so
       a sector that drew on its line has that much less for a building the
       same month: the line first, growth from what is left.

       THE BANK'S OWN STATE. The line is honoured while the bank stands - in
       its band, between its minimum and its target, and under its minimum.
       A bank that has failed and is frozen in resolution lends nothing
       (lendingOpen): not the line, not the reserve, not a renewal. Its
       borrowers' short months then go to the month's defaults (INTERIM
       FINANCING), and with no lender to make the interim loan, to the
       backstop.
       ===================================================================== */

    private double capitalGrowth = Double.POSITIVE_INFINITY;
    private boolean keepGoingOnly;
    private final Map<String, Double> principalAtRule = new LinkedHashMap<>();
    /** Sectors whose project the capital rule refused this month, so the investor can say so. */
    private final java.util.Set<String> refusedForCapital = new java.util.HashSet<>();
    /** ...by door (round 5): a building's loan, and a new mortgage - the two GROWTH doors. For the playtest. */
    private final java.util.Set<String> refusedProjectForCapital = new java.util.HashSet<>();
    private final java.util.Set<String> refusedMortgageForCapital = new java.util.HashSet<>();

    /**
     * The bank's capital rule for the month, from Bank.lendingGrowthLimit()
     * and lendsOnlyToKeepBorrowersGoing(). Records what every sector owes as
     * the month's base - what nobody insures, since 0.7.11: this form never
     * rations the insured mortgages (rationedPrincipal()).
     */
    public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly) {
        setCapitalRule(monthlyGrowth, keepGoingOnly, false);
    }

    /**
     * ...and whether it rations the insured mortgages too: true when the
     * bank's leverage requirement is the larger (Bank.leverageBinds(),
     * 0.7.11 round 2), because a mortgage then uses the capital the bank is
     * short of. Game passes it at the top of every month.
     */
    public void setCapitalRule(double monthlyGrowth, boolean keepGoingOnly, boolean insuredToo) {
        this.capitalGrowth = Double.isNaN(monthlyGrowth) ? 0 : Math.max(0, monthlyGrowth);
        this.keepGoingOnly = keepGoingOnly;
        this.insuredRationed = insuredToo;
        principalAtRule.clear();
        // What nobody insures (0.7.11): an insured mortgage ties up no
        // capital under the risk weights - see THE LANDLORDS' MORTGAGES -
        // and the whole debt when the leverage ratio binds (round 2).
        for (String s : SECTORS) principalAtRule.put(s, rationedPrincipal(s));
        refusedForCapital.clear();
        refusedProjectForCapital.clear();
        refusedMortgageForCapital.clear();
        refusedProjectAtDefaultPoint.clear();
    }

    /** True this month when the capital rule rations the insured mortgages too - the bank's leverage requirement binding. */
    private boolean insuredRationed;

    public boolean isInsuredRationed() { return insuredRationed; }

    /** The debt the capital rule reads: what the sector owes the bank, uninsured, or all of it while insuredRationed. Its bonds are not the bank's (0.7.12). */
    private double rationedPrincipal(String sector) {
        return insuredRationed ? getLoanPrincipal(sector) : getUninsuredPrincipal(sector);
    }

    /**
     * What the bank's capital lets this sector borrow this month, over what
     * it owes now: its debt when the rule was set, grown by the month's
     * limit (none under the minimum), less what it owes - so what matured is
     * room to refinance. Unlimited with no limit.
     */
    public double capitalRoom(String sector) {
        if (!keepGoingOnly && Double.isInfinite(capitalGrowth)) return Double.POSITIVE_INFINITY;
        double base = principalAtRule.getOrDefault(sector, rationedPrincipal(sector));
        double growth = keepGoingOnly ? 0 : capitalGrowth;
        return Math.max(0, base * (1 + growth) - rationedPrincipal(sector));
    }

    /** The month's limit on a borrower's growth, a share a month: infinite with none. */
    public double getCapitalGrowth() { return keepGoingOnly ? 0 : capitalGrowth; }

    /** True when the bank lends only to keep its borrowers going this month. */
    public boolean isKeepGoingOnly() { return keepGoingOnly; }

    /** True when the capital rule refused this sector a project this month. */
    public boolean wasRefusedForCapital(String sector) { return refusedForCapital.contains(sector); }

    /** ...a building's loan, this month (round 5, the growth doors counted apart). */
    public boolean wasProjectRefusedForCapital(String sector) { return refusedProjectForCapital.contains(sector); }
    /** ...a new mortgage, this month. */
    public boolean wasMortgageRefusedForCapital(String sector) { return refusedMortgageForCapital.contains(sector); }

    /* =====================================================================
       THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3)

       Jerus, 2026-09-23: "the bank reads a borrower's debt level from its last
       quarter's average, like real statements, not one month's stock swing."
       A lender rates a borrower off its statements, quarterly, not off one
       month's balance - and a sector that buys two months of stock at once
       swings a sixth of its assets from one month to the next (round 2's
       Luxury Retail, whose allowance flipped $227M to $637M on it).

       So the bank's READINGS of a sector - its allowance and the share in
       stage 2 (Game.provideForLosses()), the price of a new loan
       (priceSector()), and since 0.7.12 round 3 the default point it
       refuses a loan at (NOTHING PAST THE DEFAULT POINT) - use its
       leverage over the last STATEMENT_MONTHS
       month-end readings: the average of what it owed over the average of
       what it owned. THE DEFAULT HAZARD DOES NOT: firms fail on what they
       actually owe against what they actually have, not on a report, so the
       slice and the backstop keep the month's own leverage. Nor do the
       lending ceilings, which are the month's own.

       The readings are state a reload cannot rebuild, so they are saved,
       per sector by name. Until a sector has one, the readings are what it
       owes and owns now - a harness that builds this class alone reads the
       month, as it always did.

       A BACKSTOP RESTARTS THE QUARTER (Jerus, 2026-09-24: "keep quarterly").
       A whole-sector restructure is a credit event, and a lender re-rates
       the borrower on its restructured books from then on: restructure()
       drops the sector's readings, so it reads its month until the next
       month-end files the first of a new quarter. A slice restarts nothing -
       it is continuous and small. Measured, the restart moves no loan and
       no failure: the backstop shuts the sector out for
       BORROWING_BLOCKED_MONTHS or more, by which time its old readings have
       gone of themselves. What it changes is what the bank shows - the
       quote, the stage and the watch list, which read a sector owing
       nothing as past the watch line for two months without it.

       WHAT THE QUARTER DOES PRICE PAST THE DEFAULT POINT. Of the 1,436
       loans the eight default seeds priced past it (round 2, on the month's
       own reading: 81), 1,435 came from the shortfall desk
       (coverShortfall()), which lends on the month's own leverage while the
       price reads the quarter: 73% to Luxury Retail, mostly on the high
       month of its two-month restock, its assets then more than a quarter
       over the quarter's average of them; 20% to an industrial sector a
       large slice had just cut the debt of, its quarter still reading the
       debt; 11 were past the point on the month's own reading too. Measured
       on a probe, 2026-09-24; no rule moved. Since 0.7.12 rounds 2 and 3 the
       shortfall desk lends nothing past the default point on the quarter,
       so those loans are not made any more.
       ===================================================================== */

    /** How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. */
    public static final int STATEMENT_MONTHS = 3;

    /** Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. */
    private final Map<String, double[]> statements = new LinkedHashMap<>();

    /**
     * One month-end reading of a sector: what it owed and what it owned, as
     * the bank read them (Game.sectorPositions()).
     *
     * A MONTH-END HOLDING NOTHING IS NOT A READING (0.7.12 round 7, decided
     * on round 6's record). A sector that has owned nothing and owed nothing
     * - a first restaurant, a first railway - filed readings of nothing every
     * month until its first plant arrived, and the lender then read the
     * average of them: a quarter of nothing against its first bill, past the
     * default point on nothing at all. RestaurantsCheck's kitchens were
     * banned for a year over $8.70 of property tax on their site, and
     * RailCheck's railway over its first payroll. So a month-end at which the
     * sector owned nothing and owed nothing files nothing, its quarter
     * starts with its first month-end holding anything, and until then it
     * reads as it stands (assetsNow()), the rule round 3 set for a sector
     * with no quarter. A reading of anything is filed as it always was: a
     * sector that owned plant and crashed still reads the quarter it had.
     */
    public void recordStatement(String sector, double owed, double owned) {
        if (sector == null || !Double.isFinite(owed) || !Double.isFinite(owned)) return;
        if (!(owed > 0) && !(owned > 0)) return;
        double[] was = statements.get(sector);
        int keep = was == null ? 0 : Math.min(was.length / 2, STATEMENT_MONTHS - 1);
        double[] now = new double[2 * (keep + 1)];
        if (keep > 0) System.arraycopy(was, was.length - 2 * keep, now, 0, 2 * keep);
        now[2 * keep] = owed;
        now[2 * keep + 1] = owned;
        statements.put(sector, now);
    }

    /** What the sector owed, averaged over its last quarter of readings - what it owes now, with none. */
    public double quarterPrincipal(String sector) {
        double[] r = statements.get(sector);
        if (r == null || r.length < 2) return getPrincipal(sector);
        double total = 0;
        for (int i = 0; i < r.length; i += 2) total += r[i];
        return total / (r.length / 2);
    }

    /** ...and what it owned. */
    public double quarterAssets(String sector) {
        double[] r = statements.get(sector);
        if (r == null || r.length < 2) return getAssets(sector);
        double total = 0;
        for (int i = 1; i < r.length; i += 2) total += r[i];
        return total / (r.length / 2);
    }

    /** The leverage the bank reads the sector at: its quarter's average debt over its average assets, 0 with no assets. */
    public double getQuarterLeverage(String sector) {
        double a = quarterAssets(sector);
        return a > 0 ? quarterPrincipal(sector) / a : 0;
    }

    /** The sector's default rate a year at the leverage its last quarter reads (getQuarterLeverage()) - the reading its price is struck on (getRiskSpread()), which the screens print beside that price. */
    public double getQuarterDefaultRate(String sector) {
        return defaultProbability(getQuarterLeverage(sector));
    }

    /** How many readings a sector has, up to STATEMENT_MONTHS. */
    public int getStatementCount(String sector) {
        double[] r = statements.get(sector);
        return r == null ? 0 : r.length / 2;
    }

    /** The readings, for the save: a copy, by sector name. */
    public Map<String, double[]> getStatementsToSave() {
        Map<String, double[]> out = new LinkedHashMap<>();
        for (Map.Entry<String, double[]> e : statements.entrySet()) out.put(e.getKey(), e.getValue().clone());
        return out;
    }

    /** ...and back on load. A save from before has none, and each sector reads its month until it has its own. A malformed entry is dropped whole. */
    public void restoreStatements(Map<String, double[]> saved) {
        statements.clear();
        if (saved == null) return;
        for (Map.Entry<String, double[]> e : saved.entrySet()) {
            double[] v = e.getValue();
            if (e.getKey() == null || v == null || v.length < 2 || v.length % 2 != 0
                    || v.length > 2 * STATEMENT_MONTHS) continue;
            statements.put(e.getKey(), v.clone());
        }
    }

    /** Game tells the lender each month whether the bank behind it is standing. */
    public void setLendingOpen(boolean open) {
        this.lendingOpen = open;
    }

    public boolean isLendingOpen() {
        return lendingOpen;
    }

    /**
     * Writes a loan of this principal - a shortfall loan, priced at what the
     * sector will owe over the assets it has. The borrower is owed the
     * principal LESS its fee - feeOn(faceValue), which the caller keeps back
     * from what it hands the sector and the bank collects at the settle.
     */
    public BusinessLoan issueLoan(String sector, double faceValue, int month) {
        return write(sector, faceValue, month, 0, false);
    }

    /** ...a project's: priced with the building it buys counted in the assets, at the loan's value (projectRate()). */
    public BusinessLoan issueProjectLoan(String sector, double faceValue, int month) {
        return write(sector, faceValue, month, faceValue, true);
    }

    /**
     * One loan written this month: to whom, how much, the leverage it left
     * the borrower at, the rate it was written at, and whether it bought a
     * building (0.7.8, for the playtest's count of loans written past the
     * watch line and the default point). A month's flow, read in the month,
     * and not saved.
     */
    public record Written(String sector, double amount, double leverage, double rate, boolean project) { }

    private final List<Written> writtenThisMonth = new ArrayList<>();

    /** Every loan written this month, in the order written. */
    public List<Written> getWrittenThisMonth() { return java.util.Collections.unmodifiableList(writtenThisMonth); }

    private BusinessLoan write(String sector, double faceValue, int month, double extraAssets, boolean project) {
        double rate = priceSector(sector, faceValue, extraAssets);
        double totalAssets = extraAssets > 0
                ? Math.max(0, quarterAssets(sector)) + extraAssets : quarterAssets(sector);
        writtenThisMonth.add(new Written(sector, faceValue,
                pricingLeverage(quarterPrincipal(sector) + faceValue, totalAssets), rate, project));
        BusinessLoan loan = new BusinessLoan(
                sector, faceValue, LOAN_TERM_MONTHS, month, rate);
        loans.add(loan);
        lentThisMonth += faceValue;
        lentBySector.merge(sector, faceValue, Double::sum);
        double fee = feeOn(faceValue);
        feesThisMonth += fee;
        feesBySector.merge(sector, fee, Double::sum);

        // A new loan changes the sector's leverage, so the next one prices off
        // the new position rather than the one before this loan existed.
        rates.put(sector, priceSector(sector));

        return loan;
    }

    /* ----------------------------- insolvency ----------------------------- */

    public boolean isBorrowingBlocked(String sector) {
        return blockedMonths.getOrDefault(sector, 0) > 0;
    }

    public int getBlockedMonths(String sector) {
        return blockedMonths.getOrDefault(sector, 0);
    }

    public double getWrittenOffThisMonth(String sector) {
        return writtenOffThisMonth.getOrDefault(sector, 0.0);
    }

    public double getWrittenOffTotal(String sector) {
        return writtenOffTotal.getOrDefault(sector, 0.0);
    }

    public int getRestructureCount(String sector) {
        return restructures.getOrDefault(sector, 0);
    }

    /**
     * Puts the write-off history back on load.
     *
     * Per sector, because that is how it is kept and how the credit screen
     * reads it. This is a record of money that was destroyed - a city that
     * forgets it on every load reads as having a cleaner credit history than it
     * has, which is the one direction this figure must never move by accident.
     */
    public void restoreWriteOffs(java.util.Map<String, Double> totals) {
        writtenOffTotal.clear();
        if (totals == null) return;
        for (java.util.Map.Entry<String, Double> e : totals.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) {
                writtenOffTotal.put(e.getKey(), e.getValue());
            }
        }
    }

    public java.util.Map<String, Double> getWriteOffTotals() {
        return new LinkedHashMap<>(writtenOffTotal);
    }

    /*
     * THE BORROWER'S RECORD IS STATE, AND IT WAS NOT CARRIED.
     *
     * Until 2026-09-10 only writtenOffTotal survived a save. The loans
     * themselves were restored; the count of how many times each sector had
     * been written down, and how many months of borrowing ban it still had to
     * serve, were written nowhere and restored nowhere. The debt survived and
     * the record of why it existed did not.
     *
     * Two consequences, both measured against the playtest's own reloads: a
     * sector's DEFAULT RECORD failed to survive the save 49 times (worst, Real
     * Estate: 12 restructures -> 0) and its BORROWING BAN 6 times (worst, Heavy
     * Industry: 80 months left -> 0). A player clears an 80-month ban by saving
     * and loading, and the next default is priced as a first offence -
     * exclusionFor(1), twelve months, instead of 108. Running the whole city
     * at that flat twelve - which is what a reloading player converges on -
     * costs $238bn of write-offs against $25.8bn. The entire seventeen-fold
     * improvement the escalating exclusion bought was available to undo with
     * Ctrl-S.
     *
     * Restored as zero on a save from before this, which is what those cities
     * were already reading.
     */
    public java.util.Map<String, Integer> getRestructureCounts() {
        return new LinkedHashMap<>(restructures);
    }

    public java.util.Map<String, Integer> getBlockedMonthsAll() {
        return new LinkedHashMap<>(blockedMonths);
    }

    public void restoreCreditRecord(java.util.Map<String, Integer> counts,
                                    java.util.Map<String, Integer> blocked) {
        restructures.clear();
        blockedMonths.clear();
        if (counts != null) {
            for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    restructures.put(e.getKey(), e.getValue());
                }
            }
        }
        if (blocked != null) {
            for (java.util.Map.Entry<String, Integer> e : blocked.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) {
                    blockedMonths.put(e.getKey(), e.getValue());
                }
            }
        }
    }

    public double getTotalWrittenOff() {
        double total = 0;
        for (String sector : SECTORS) {
            total += getWrittenOffTotal(sector);
        }
        return total;
    }

    /**
     * Is every firm in this sector under water at once - the backstop's case?
     *
     * ASSETS AT OR BELOW ZERO, against any principal at all or AN OVERDRAFT
     * (since 2026-09-10): the balance sheet's assets already carry the cash,
     * so assets at or below zero with cash below zero is a sector whose
     * unpaid bills exceed its plant, whether or not it ever signed a loan.
     * Before that a sector with no loans could run -$13bn for ever and never
     * be called anything.
     *
     * Until 0.7.8 there was a second way in: the loan book past the line,
     * principal over INSOLVENCY_TRIGGER times positive assets. That is no
     * longer a whole-sector event - it is the high end of the month's slice
     * (defaultSlice(): at 1.5 times its assets 5.6% of a sector's debt
     * defaults a month, at 2.0 about 16%, at 3.0 about 39%).
     */
    public boolean isInsolvent(String sector) {
        if (getAssets(sector) > 0) return false;
        return getPrincipal(sector) > 0 || getCash(sector) < 0;
    }

    /**
     * THE BACKSTOP: writes a sector with nothing left down to what its assets
     * can support - RESTRUCTURE_TARGET of nothing - forgives its overdraft,
     * counts the default on its record and shuts it out for exclusionFor().
     * Only a sector that isInsolvent(); since 0.7.8 a sector past the line
     * with assets still standing loses its defaulted firms' share instead
     * (defaultSlice()) - unless, since 0.7.12 round 5, nobody will make it an
     * interim loan: the forced form below.
     *
     * Call once a month, AFTER the balance sheets have been refreshed - the
     * whole judgement is principal against assets, so acting on last month's
     * assets would restructure the wrong sector.
     *
     * @return the amount written off, or 0 if the sector was solvent enough
     */
    public double restructure(String sector) {
        return restructure(sector, false);
    }

    /**
     * ...or, `forced`, a sector nobody would make an interim loan to - still
     * past the default point after the month's write-down, or its bank shut
     * - whatever its assets read (INTERIM FINANCING, round 5): the whole
     * sector to the same full write-off, down to RESTRUCTURE_TARGET of what
     * it owns, its overdraft closed, its record and its ban. A sector already
     * inside its ban is in the episode it started: its overdraft is closed,
     * as the backstop closes one, and nothing new is written off or counted.
     */
    public double restructure(String sector, boolean forced) {

        if (!forced && !isInsolvent(sector)) {
            return 0;
        }

        /*
         * ONE DEFAULT IS ONE EPISODE. A sector inside its ban is already in
         * resolution: it cannot borrow, and the distress rule is selling its
         * plant month by month. Judging it insolvent again every month while
         * that happens - which it is, its assets are falling - counted each
         * month as a fresh default, and the escalating exclusion did what it
         * says: 46 write-downs on Retail in one run, a ban of 525 months, and
         * a city that could not build a shop for forty years. The record
         * grows when the ban ENDS and the sector is still under water, which
         * is what "defaulted again" means.
         */
        if (isBorrowingBlocked(sector)) {
            if (forced) {
                double open = Math.max(0, -getCash(sector));
                if (open > 0) {
                    overdraftForgiven.put(sector, getOverdraftForgivenPending(sector) + open);
                    cashBalance.put(sector, getCash(sector) + open);
                    backstopInBanThisMonth.merge(sector, open, Double::sum);
                }
            }
            return 0;
        }

        double principal = getPrincipal(sector);
        double target = Math.max(getAssets(sector) * RESTRUCTURE_TARGET, 0);
        double writeOff = Math.max(0, principal - target);

        /*
         * ...AND THE OVERDRAFT GOES WITH THE LOAN. The cash a sector paid out
         * and never had is a debt to nobody in particular, and a restructure
         * that wrote the loan down and left the overdraft standing left the
         * sector as insolvent as it found it - assets still at or below zero,
         * so insolvent again the month the ban lifted, for ever. Jerus's call
         * (2026-09-10): the hole is written off, declared, and the record
         * shows it. Game collects the figure through takeOverdraftForgiven()
         * and puts the money back, from outside the city's pools, the same
         * way the bank's creditors absorb a failed bank.
         */
        double overdraft = Math.max(0, -getCash(sector));
        if (writeOff <= 0 && overdraft <= 0) {
            return 0;
        }

        /*
         * ...AND NOTHING TO RECOVER, SO THE RECOVERIES DO NOT APPLY (0.7.12,
         * round 2). The backstop's own case is a sector whose assets are at or
         * below nothing, so the target is RESTRUCTURE_TARGET of nothing and
         * every instrument is written off whole - the loans and the bonds
         * alike. Reading LOAN_RECOVERY and BOND_RECOVERY here would hand the
         * creditors 75 and 45 cents on the dollar out of a sector with nothing
         * left, which is money from nowhere; the record's recoveries are what
         * creditors got back from firms that still had something. The split
         * below is round 1's loans-first order of the target, kept because it
         * is the right order if the target is ever something - which since
         * round 5 it is, for a sector nobody would make an interim loan to
         * that still owns something (forced): the interim loan is kept first,
         * then the loans, then the bonds.
         */
        // ...the interim loan first of all (round 5, INTERIM FINANCING).
        double interimOwed = getInterimPrincipal(sector);
        double interimKept = Math.min(interimOwed, target);
        double loansOwed = getLoanPrincipal(sector) - interimOwed;
        double bondsOwed = principal - loansOwed - interimOwed;
        double loansKept = Math.min(loansOwed, target - interimKept);
        double loanWriteOff = Math.max(0, loansOwed - loansKept) + Math.max(0, interimOwed - interimKept);
        if (writeOff > 0) {
            loansDefaultedThisMonth.merge(sector, loansOwed + interimOwed, Double::sum);
            bondsDefaultedThisMonth.merge(sector, bondsOwed, Double::sum);
        }
        if (writeOff > 0) {
            // Every loan pro rata; what came off its insured mortgages
            // is the treasury's to pay the bank (0.7.11). The interim loans
            // on their own scale, ahead of the rest.
            if (interimOwed > 0) {
                writeDownInterim(sector, interimKept / interimOwed);
                if (interimOwed > interimKept) interimWrittenOffThisMonth.merge(sector, interimOwed - interimKept, Double::sum);
            }
            if (loansOwed > 0) writeDownSector(sector, loansKept / loansOwed, false);
            if (bondsOwed > 0 && bondBook != null) {
                double bondsKept = Math.max(0, target - interimKept - loansKept);
                recordBondWriteOff(sector, bondBook.writeDown(sector, bondsKept / bondsOwed));
            }
        }

        overdraftForgiven.put(sector, getOverdraftForgivenPending(sector) + overdraft);
        cashBalance.put(sector, getCash(sector) + overdraft);

        writtenOffThisMonth.put(sector, getWrittenOffThisMonth(sector) + loanWriteOff);
        writtenOffTotal.put(sector, getWrittenOffTotal(sector) + loanWriteOff);
        restructures.put(sector, getRestructureCount(sector) + 1);
        blockedMonths.put(sector, exclusionFor(getRestructureCount(sector)));
        restructuredThisMonth.add(sector);
        /*
         * ...AND ITS QUARTER RESTARTS (0.7.8, round 4). A lender re-rates a
         * borrower after a credit event on its restructured books; the months
         * before stop counting. Left in, the quarter read the debt just
         * written off for two more months - in the sector's quote, its stage
         * and the watch list; not in a loan, since the ban above outlasts
         * the quarter. A slice does not restart it: it is continuous and
         * small. See THE BANK READS A BORROWER FROM ITS LAST QUARTER.
         */
        statements.remove(sector);

        // Less debt against the same assets is better credit on the leverage
        // leg - and a fresh default is worse credit on the record leg, which
        // priceSector() now charges for. Net, the month after a write-down
        // the borrower is quoted MORE than it was, not less.
        rates.put(sector, priceSector(sector));

        // The bank's loss (0.7.12): its loans'. The bondholders' is recorded
        // beside it (getBondWrittenOffThisMonth()).
        return loanWriteOff;
    }

    /* =====================================================================
       CAN'T PAY MEANS DEFAULT (0.7.12, round 4)

       Jerus: "Bankruptcy law's cash-flow test. When no one will lend to an
       industry and its cash runs out, the firms that can't pay default that
       month instead of running an overdraft for years."

       THE SOURCE: Canada's Bankruptcy and Insolvency Act, s. 2, "insolvent
       person" - one (a) "who is for any reason unable to meet his
       obligations as they generally become due", (b) "who has ceased paying
       his current obligations in the ordinary course of business as they
       generally become due", or (c) the aggregate of whose property is not,
       at a fair valuation, sufficient to pay all his obligations. The model
       had (c), the balance-sheet test: INSOLVENCY_TRIGGER, the slice and the
       backstop. This is (a) and (b), the cash-flow test.

       THE RULE: no company's till carries a negative balance past a
       month-end unless a lender stands behind it. In a short month the
       company first sells what it holds by the households' waterfall rule -
       its dollars abroad, then the other sectors' bonds at the price that
       yields its own borrowing rate (EconomyManager.settleBusinessCredit(),
       BondMarket.sellForCompany()); it holds no shares - then borrows where a
       lender will lend (coverShortfall()), and what it still cannot pay is a
       default that month, struck with the month's other defaults below,
       after the distress rule has sold what plant it would.

       HOW IT DEFAULTS, on the model's own restructure, the smaller of the
       two paths that ends the overdraft: THE SLICE, sized to the part of the
       sector that cannot pay - the share of what the month asked it to pay
       that it could not (cannotPayShare(): its shortfall over its costs,
       interest, taxes and the principal that fell due, setMonthObligations())
       - its debt written down by instrument at LOAN_RECOVERY and
       BOND_RECOVERY as any slice is. One slice a month: where the
       balance-sheet slice (the curve at its leverage) reads a larger share,
       that one is taken, never the two added - they are two readings of which
       firms fell. A sector with nothing left is the backstop's, as before,
       which closes its overdraft itself. No ban and no record: a slice is not
       a restructure.

       WHAT IS STILL UNPAID. Round 4 closed it the way the backstop closes an
       overdraft: forgiven, money created to pay bills the sector never had
       the money for. Since round 5 it is LENT, as interim financing ranked
       ahead of everything else the sector owes, and nothing is forgiven -
       see INTERIM FINANCING, below; only a sector no lender will take even
       then goes to the backstop, whose forgiveness MoneyAudit declares as it
       always has ("+ sectors OverdraftForgiven", a valuation inflow).

       WHY THE LENDER SAID NO is kept (getShortReason(), getCannotPayReason()):
       the sector past the default point, banned after a restructure, the
       bank shut (failed, in resolution), or the shortfall desk's own ceiling
       (MAX_LOAN_TO_ASSETS: over it and under the default point it lends the
       interest reserve and no more) - and a till that went short after the
       credit settle, when nobody was asked. Round 4 had the bank's capital
       rule among them, rationing a short month as growth; since round 5 it
       is not (CREDIT LINES STAY OPEN).
       ===================================================================== */

    /** Why the shortfall desk left a sector short (round 4). The bank's capital rule is not a reason since round 5 (CREDIT LINES STAY OPEN). */
    public enum ShortReason { PAST_DEFAULT_POINT, BANNED, BANK_SHUT, CEILING, AFTER_SETTLE }

    /* Why the shortfall desk left each sector short this month, and what the month asked each to pay - both from the credit settle, read at the month's defaults; the month's, not saved. */
    private final Map<String, ShortReason> shortReason = new LinkedHashMap<>();
    private final Map<String, Double> monthObligations = new LinkedHashMap<>();

    /** Why the shortfall desk left this sector short this month - the tighter of its limits, or its reason to refuse - or null if it was not asked. Read only while the sector is still short. */
    public ShortReason getShortReason(String sector) { return shortReason.get(sector); }

    /** What the month asked this sector to pay: its costs, interest and taxes on this month's statement and the principal that fell due (EconomyManager.settleBusinessCredit()). */
    public void setMonthObligations(String sector, double amount) {
        if (sector != null && Double.isFinite(amount)) monthObligations.put(sector, Math.max(0, amount));
    }
    public double getMonthObligations(String sector) { return monthObligations.getOrDefault(sector, 0.0); }

    /* The month's cash-flow defaults, per sector: what was still unpaid, the share of the sector that could not pay, and why nobody lent. Struck by restructureInsolventSectors() and read in the month; not saved. */
    private final Map<String, Double> cannotPayShort = new LinkedHashMap<>();
    private final Map<String, Double> cannotPayShareThisMonth = new LinkedHashMap<>();
    private final Map<String, ShortReason> cannotPayReason = new LinkedHashMap<>();

    /** What the month's bills still left unpaid in one sector when the cash-flow test struck (round 4) - lent as interim financing since round 5, or the backstop's; 0 if it did not default for want of cash. */
    public double getCannotPayShort(String sector) { return cannotPayShort.getOrDefault(sector, 0.0); }
    /** ...the share of it that could not pay, h. */
    public double getCannotPayShare(String sector) { return cannotPayShareThisMonth.getOrDefault(sector, 0.0); }
    /** ...and why no lender stood behind it, or null. */
    public ShortReason getCannotPayReason(String sector) { return cannotPayReason.get(sector); }

    /**
     * THE CASH-FLOW TEST'S SHARE: the part of a sector whose till is short
     * that cannot pay - its shortfall over what the month asked it to pay,
     * all of it when that is more than the month's bills. 0 when its till is
     * not short.
     */
    double cannotPayShare(String sector) {
        double cash = getCash(sector);
        if (!(cash < 0)) return 0;
        double owed = monthObligations.getOrDefault(sector, 0.0);
        return owed > -cash ? -cash / owed : 1;
    }

    /* =====================================================================
       INTERIM FINANCING (0.7.12, round 5)

       Jerus: "Interim financing. After the debt is written down, the bank
       lends the unpaid part as a new loan that ranks ahead of all other
       debt, so no money is created. This is what real restructurings do
       (Canada's CCAA s. 11.2; US Chapter 11, 11 U.S.C. s. 364). If nobody
       will lend even then, the whole industry goes to the existing full
       write-off." CCAA s. 11.2 lets the court secure interim financing on a
       charge that ranks ahead of every secured creditor's claim; s. 364 lets
       a debtor in possession borrow with priority over the other claims when
       it could not borrow otherwise.

       THE ORDER, in the month's defaults (restructureInsolventSectors()): the
       cash-flow slice writes down the part of the sector that cannot pay, as
       round 4 built it; then what the month's bills still left unpaid - the
       till's overdraft - is lent as an InterimLoan, grossed up for its fee as
       the shortfall desk's loan is (0.7.7), and the till ends the month at
       nothing. Nothing is forgiven on this path.

       THE LENDER'S TEST (interimRefusal()) is the default point, read after
       the write-down: the quarter the bank reads (defaultPointReading()) with
       its debt restated for what the slice just wrote off, before the loan
       and after it - the same before-and-after reading NOTHING PAST THE
       DEFAULT POINT asks of every loan. It is NOT held to the shortfall
       desk's ceiling (MAX_LOAN_TO_ASSETS), nor to the bank's capital rule,
       nor to the ban a backstop gave the sector: its rank is the reason a
       lender makes it, and a firm in restructuring is what it is made for.
       Jerus's refusals are two, and only two: a sector still past the line
       after the write-down, or a bank that cannot lend (failed, frozen in
       resolution: lendingOpen). Measured the other way first: holding the
       interim lender to the ban sent Luxury Retail, banned, at 0.6 of what
       it owned on its quarter and short $8-10M every other month, to the
       backstop's forgiveness 700 times in two runs, $10.8B of money created
       - the thing the brief exists to end. The ban still binds the
       shortfall desk and the investment desk (Game's sectorInvestor(): "the
       ban is one ban"), which lend to grow. Where nobody lends, the whole
       sector goes to the backstop (restructure(sector, true)), the existing
       full write-off, which closes the overdraft as it always has - the one
       path left that creates money. For a sector already inside its ban -
       ONE DEFAULT IS ONE EPISODE - the backstop closes the overdraft and
       writes nothing new.

       ITS PRICE AND TERM are the bank's own loan rules applied to its rank.
       The term is the shortfall desk's, LOAN_TERM_MONTHS, a bullet repaid or
       rolled when it falls due like any loan. The rate is prime plus the
       curve's expected loss at the leverage ITS RANK sees - nothing ranks
       ahead of it, so only the sector's interim debt, this loan with it,
       over what the sector owns (priceInterim()) - plus the record surcharge
       and the concentration charge every loan pays. The loss given default
       in that expected loss is a loan's, LOAN_LOSS_GIVEN_DEFAULT: real
       debtor-in-possession loans rarely lose money, and I found no sourced
       recovery rate for them, so LOAN_RECOVERY is used and it is the
       conservative end.

       ITS RANK IN A LATER DEFAULT: written down only after the sector's
       other debt. A slice leaves it whole: the defaulted firms' recoveries
       pay the claim that ranks first, and the other instruments lose their
       own shares as they always have (their losses are not raised to pay
       for it - the simplest reading, and a generous one to the juniors). The
       backstop keeps it first out of what it keeps: the loans-first order of
       the target round 2 kept, with the interim loan ahead of the loans. The
       backstop of a sector with nothing left keeps nothing, and the interim
       loan goes with the rest, last in the order.
       ===================================================================== */

    /** Why the interim lender would not lend: the sector still past the default point after the write-down. */
    public static final String INTERIM_PAST_LINE = "past the default point after the write-down";
    /** ...or the bank that would lend it has failed or is frozen in resolution. */
    public static final String INTERIM_BANK_SHUT = "the bank is shut";

    /* The month's interim financing, per sector: lent (at face), handed to the till (the overdraft it closed), refused and why, fallen due, and written off in a backstop. Read in the month; not saved - the loans themselves are. */
    private final Map<String, Double> interimLentThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> interimHanded = new LinkedHashMap<>();
    private final Map<String, String> interimRefused = new LinkedHashMap<>();
    private final Map<String, Double> interimMaturedThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> interimWrittenOffThisMonth = new LinkedHashMap<>();
    /** The month an interim loan is written in: the credit settle's (coverShortfall()). */
    private int monthNow;

    /** What the interim lender lent this sector this month, at face (round 5). */
    public double getInterimLentThisMonth(String sector) { return interimLentThisMonth.getOrDefault(sector, 0.0); }
    /** Why the interim lender would not lend to this sector this month, or null. */
    public String getInterimRefusal(String sector) { return interimRefused.get(sector); }
    /** The interim principal that fell due this month in this sector: repaid, or rolled by the shortfall desk. */
    public double getInterimMaturedThisMonth(String sector) { return interimMaturedThisMonth.getOrDefault(sector, 0.0); }
    /** The interim principal a backstop wrote off this month in this sector. */
    public double getInterimWrittenOffThisMonth(String sector) { return interimWrittenOffThisMonth.getOrDefault(sector, 0.0); }

    /* The overdraft the backstop closed this month in a sector already inside its ban, which nobody would make an interim loan to: forgiven, nothing new written off (restructure(sector, true)). */
    private final Map<String, Double> backstopInBanThisMonth = new LinkedHashMap<>();
    /** ...one sector's. */
    public double getBackstopInBanThisMonth(String sector) { return backstopInBanThisMonth.getOrDefault(sector, 0.0); }

    /** The interim loan's cash this sector's till is owed from the month's defaults, handed over once (EconomyManager.settleInsolvency()). */
    public double takeInterimHanded(String sector) {
        Double v = interimHanded.remove(sector);
        return v == null ? 0 : v;
    }

    /** What one sector owes on interim financing. */
    public double getInterimPrincipal(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) if (loan instanceof InterimLoan && loan.getSector().equals(sector)) total += loan.getOutstandingPrincipal();
        return total;
    }

    /** ...every sector's. */
    public double getInterimPrincipal() {
        double total = 0;
        for (BusinessDebt loan : loans) if (loan instanceof InterimLoan) total += loan.getOutstandingPrincipal();
        return total;
    }

    /** How many interim loans one sector has outstanding. */
    public int getInterimCount(String sector) {
        int n = 0;
        for (BusinessDebt loan : loans) if (loan instanceof InterimLoan && loan.getSector().equals(sector)) n++;
        return n;
    }

    /** ...every sector's. */
    public int getInterimCount() {
        int n = 0;
        for (BusinessDebt loan : loans) if (loan instanceof InterimLoan) n++;
        return n;
    }

    /**
     * THE INTERIM LENDER'S TEST: null to lend, or why not. The bank shut; or
     * the sector past the default point read after the write-down - the
     * quarter's debt less what the slice just wrote off, before the loan and
     * after it, `handed` of it paid into the till. Not the ceiling, not the
     * capital rule, not the ban: see INTERIM FINANCING.
     */
    String interimRefusal(String sector, double cash, double loan, double handed, double writtenDown) {
        if (!lendingOpen) return INTERIM_BANK_SHUT;
        if (!tillReported.contains(sector)) return null;
        double[] read = defaultPointReading(sector, cash);
        double owed = Math.max(0, read[0] - Math.max(0, writtenDown)), assets = read[1];
        if (!(assets > 0)) return INTERIM_PAST_LINE;
        if (owed > assets * INSOLVENCY_TRIGGER) return INTERIM_PAST_LINE;
        if (owed + loan > (assets + Math.max(0, handed)) * INSOLVENCY_TRIGGER) return INTERIM_PAST_LINE;
        return null;
    }

    /** What an interim loan of this face would be written at: prime, the curve at the leverage its rank sees, the record and the concentration (INTERIM FINANCING). */
    double priceInterim(String sector, double faceValue) {
        double assets = Math.max(0, quarterAssets(sector));
        double spread = expectedLossSpread(pricingLeverage(getInterimPrincipal(sector) + faceValue, assets));
        spread += recordSurcharge(sector);
        spread += getConcentrationCharge(sector);
        return primeRate + spread;
    }

    /** Writes an interim loan: counted in the month's lending and fees like any loan, the bank paying it out at the settle. */
    private InterimLoan writeInterim(String sector, double faceValue) {
        double rate = priceInterim(sector, faceValue);
        writtenThisMonth.add(new Written(sector, faceValue,
                pricingLeverage(quarterPrincipal(sector) + faceValue, quarterAssets(sector)), rate, false));
        InterimLoan loan = new InterimLoan(sector, faceValue, LOAN_TERM_MONTHS, monthNow, rate);
        loans.add(loan);
        lentThisMonth += faceValue;
        lentBySector.merge(sector, faceValue, Double::sum);
        double fee = feeOn(faceValue);
        feesThisMonth += fee;
        feesBySector.merge(sector, fee, Double::sum);
        interimLentThisMonth.merge(sector, faceValue, Double::sum);
        rates.put(sector, priceSector(sector));
        return loan;
    }

    /**
     * THE MONTH'S DEFAULTS, every sector: the backstop for a sector with
     * nothing left (restructure()), and for every other the slice of its
     * debt whose firms fell through the default point (defaultSlice()) -
     * or, since round 4, the part that cannot pay if its till is short with
     * no lender behind it, whichever share is larger (CAN'T PAY MEANS
     * DEFAULT); and since round 5 what is still unpaid lent as interim
     * financing, or the whole sector to the backstop if nobody will lend it
     * (INTERIM FINANCING).
     * @return total written off this month.
     */
    public double restructureInsolventSectors() {

        double total = 0;
        for (String sector : SECTORS) {
            writtenOffThisMonth.put(sector, 0.0);
        }
        insuredWrittenOffThisMonth.clear();
        bondWrittenOffThisMonth.clear();
        loansDefaultedThisMonth.clear();
        bondsDefaultedThisMonth.clear();
        defaultedThisMonth.clear();
        defaultShareThisMonth.clear();
        restructuredThisMonth.clear();
        cannotPayShort.clear();
        cannotPayShareThisMonth.clear();
        cannotPayReason.clear();
        interimLentThisMonth.clear();
        interimHanded.clear();
        interimRefused.clear();
        interimWrittenOffThisMonth.clear();
        backstopInBanThisMonth.clear();

        for (String sector : SECTORS) {
            total += restructure(sector);
            // The cash-flow test (round 4): what is still short once the
            // backstop has had its turn.
            double cannotPay = cannotPayShare(sector);
            double owedBefore = getPrincipal(sector);
            total += defaultSlice(sector, cannotPay);
            double overdraft = Math.max(0, -getCash(sector));
            if (overdraft > 0) {
                cannotPayShort.put(sector, overdraft);
                cannotPayShareThisMonth.put(sector, cannotPay);
                ShortReason why = shortReason.get(sector);
                if (why == null) why = isBorrowingBlocked(sector) ? ShortReason.BANNED : ShortReason.AFTER_SETTLE;
                cannotPayReason.put(sector, why);
                // ...and what is still unpaid is lent, ranked first (round 5),
                // grossed up for its fee so the till ends at nothing.
                double writtenDown = Math.max(0, owedBefore - getPrincipal(sector));
                double loan = overdraft / (1 - Bank.LOAN_FEE);
                String no = interimRefusal(sector, getCash(sector), loan, overdraft, writtenDown);
                if (no == null) {
                    writeInterim(sector, loan);
                    cashBalance.put(sector, getCash(sector) + overdraft);
                    interimHanded.merge(sector, overdraft, Double::sum);
                } else {
                    // ...or nobody lends, and the whole sector is the backstop's.
                    interimRefused.put(sector, no);
                    total += restructure(sector, true);
                }
            }
        }
        // What each owes as the rule judged it, for the bank's allowance (0.7.8).
        principalJudged.clear();
        for (String sector : SECTORS) principalJudged.put(sector, getPrincipal(sector));
        return total;
    }

    /**
     * THE SLICE: the share of this sector's debt whose firms fell through the
     * default point this month, monthlyDefaultShare() of it at the leverage
     * the month's check reads (principal over getAssets(), struck after the
     * balance-sheet refresh), each instrument written off at its own loss
     * given default (RECOVERIES BY INSTRUMENT). Every loan of the sector is
     * written down pro rata, as restructure() does, and booked through the
     * same writtenOffThisMonth/writtenOffTotal the bank reads.
     * Its assets are untouched - the defaulted firms keep their plant - so its
     * leverage falls. Not on its record, not surcharged, no ban: see A SECTOR
     * DEFAULTS A SLICE AT A TIME. Nothing for a sector with no debt, and
     * nothing for one with no assets, which is the backstop's.
     *
     * @return the amount written off
     */
    public double defaultSlice(String sector) {
        return defaultSlice(sector, 0);
    }

    /** ...or, if more, this share: the part of it that cannot pay this month (round 4, CAN'T PAY MEANS DEFAULT). */
    public double defaultSlice(String sector, double atLeast) {
        double principal = getPrincipal(sector);
        double totalAssets = getAssets(sector);
        if (!(principal > 0) || !(totalAssets > 0)) return 0;
        double share = Math.min(1, Math.max(monthlyDefaultShare(principal / totalAssets), Math.max(0, atLeast)));
        if (!(share > 0)) return 0;
        /*
         * ...EACH INSTRUMENT AT ITS OWN LOSS (0.7.12, round 2). The slice is
         * the same share of every instrument, and each loses its own part of
         * it: the loans LOAN_LOSS_GIVEN_DEFAULT, the bonds
         * BOND_LOSS_GIVEN_DEFAULT, whatever the mix - no priority rule on top
         * (RECOVERIES BY INSTRUMENT). What this returns, and books against the
         * bank, is the loans' loss; the bondholders' is recorded beside it.
         */
        /*
         * ...AND THE INTERIM LOAN, RANKED FIRST, NOT AT ALL (round 5): the
         * defaulted firms' recoveries pay the claim that ranks ahead of
         * everything before anything else - see INTERIM FINANCING. The slice
         * is the share of the rest.
         */
        double interim = getInterimPrincipal(sector);
        double loansOwed = getLoanPrincipal(sector) - interim;
        double bondsOwed = principal - loansOwed - interim;
        double loanWriteOff = loansOwed * share * LOAN_LOSS_GIVEN_DEFAULT;
        loansDefaultedThisMonth.merge(sector, loansOwed * share, Double::sum);
        bondsDefaultedThisMonth.merge(sector, bondsOwed * share, Double::sum);
        // Every loan pro rata; the insured part is the treasury's claim to
        // pay (0.7.11) - a loan's loss on it, since round 2.
        if (loansOwed > 0) writeDownSector(sector, 1 - share * LOAN_LOSS_GIVEN_DEFAULT, false);
        if (bondsOwed > 0 && bondBook != null) {
            recordBondWriteOff(sector, bondBook.writeDown(sector, 1 - share * BOND_LOSS_GIVEN_DEFAULT));
        }
        defaultedThisMonth.put(sector, (principal - interim) * share);
        defaultShareThisMonth.put(sector, share);
        writtenOffThisMonth.put(sector, getWrittenOffThisMonth(sector) + loanWriteOff);
        writtenOffTotal.put(sector, getWrittenOffTotal(sector) + loanWriteOff);
        // Less debt against the same assets: its price off the curve falls with it.
        rates.put(sector, priceSector(sector));
        return loanWriteOff;
    }

    /* What defaulted this month in each class, per sector (0.7.12): the loans' and the bonds' share of the slice, or all of both in the backstop - before what was recovered, so recovered over defaulted is each class's recovery. The month's, read in the month, not saved. */
    private final Map<String, Double> loansDefaultedThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> bondsDefaultedThisMonth = new LinkedHashMap<>();

    /** The loans and the bonds that defaulted this month in one sector, before recovery (0.7.12). */
    public double getLoansDefaultedThisMonth(String sector) { return loansDefaultedThisMonth.getOrDefault(sector, 0.0); }
    public double getBondsDefaultedThisMonth(String sector) { return bondsDefaultedThisMonth.getOrDefault(sector, 0.0); }

    /* The bondholders' side of the month's defaults (0.7.12), per sector: the face written off its bonds, every holder together. The bank's is writtenOffThisMonth; the two add up to what the slice or the backstop wrote off. */
    private final Map<String, Double> bondWrittenOffThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> bondWrittenOffTotal = new LinkedHashMap<>();

    private void recordBondWriteOff(String sector, double face) {
        if (!(face > 0)) return;
        bondWrittenOffThisMonth.merge(sector, face, Double::sum);
        bondWrittenOffTotal.merge(sector, face, Double::sum);
    }

    /** What this month's defaults took off one sector's bonds, every holder together (0.7.12). */
    public double getBondWrittenOffThisMonth(String sector) { return bondWrittenOffThisMonth.getOrDefault(sector, 0.0); }

    /** ...over the city's life. */
    public double getBondWrittenOffTotal(String sector) { return bondWrittenOffTotal.getOrDefault(sector, 0.0); }

    /** ...every sector's, over the city's life. */
    public double getBondWrittenOffTotal() {
        double t = 0;
        for (double v : bondWrittenOffTotal.values()) t += v;
        return t;
    }

    /** The bondholders' losses over the city's life, by sector, for the save. */
    public Map<String, Double> getBondWrittenOffTotals() { return new LinkedHashMap<>(bondWrittenOffTotal); }

    /** ...and back on load; an older save has none. */
    public void restoreBondWrittenOff(Map<String, Double> totals) {
        bondWrittenOffTotal.clear();
        if (totals == null) return;
        for (Map.Entry<String, Double> e : totals.entrySet()) {
            if (e.getKey() != null && e.getValue() != null) bondWrittenOffTotal.put(e.getKey(), e.getValue());
        }
    }

    /*
     * The month's defaults, per sector, for the notice and the playtest -
     * struck by restructureInsolventSectors() and read in the same month
     * (Inbox.takeMonth()), so not saved: the bank's own record of the
     * month's write-off by sector is (Bank.getWrittenOff(String)).
     */
    private final Map<String, Double> defaultedThisMonth = new LinkedHashMap<>();
    private final Map<String, Double> defaultShareThisMonth = new LinkedHashMap<>();
    private final java.util.Set<String> restructuredThisMonth = new java.util.LinkedHashSet<>();

    /** The debt whose firms defaulted this month in the slice, before what its creditors recover - each instrument's write-off is its own loss given default of its part (getLoansDefaultedThisMonth(), getBondsDefaultedThisMonth()). */
    public double getDefaultedThisMonth(String sector) { return defaultedThisMonth.getOrDefault(sector, 0.0); }

    /** The share of the sector's debt that defaulted this month in the slice, h. */
    public double getDefaultShareThisMonth(String sector) { return defaultShareThisMonth.getOrDefault(sector, 0.0); }

    /** True when the backstop wrote this sector down whole this month. */
    public boolean wasRestructuredThisMonth(String sector) { return restructuredThisMonth.contains(sector); }

    /** The sector's default rate a year at its leverage now, PD(L) - what the Bank tab shows beside its leverage. All of it against no assets. */
    public double getDefaultRate(String sector) {
        return defaultProbability(leverageOf(getPrincipal(sector), getAssets(sector)));
    }

    /**
     * WHETHER THIS MONTH'S DEFAULTS ARE NEWS: the backstop, or a slice at
     * least the share that defaults a month at the default point itself -
     * monthlyDefaultShare(INSOLVENCY_TRIGGER), 5.6% of its debt, where half
     * the sector's firms fail within a year: the sector as a whole is past
     * the line. Derived from Jerus's constant, not a number of its own.
     *
     * The first line tried was a slice costing more in a month than a sound
     * borrower's year of expected loss (Bank.BASE_LOSS_RATE, a leverage of
     * about 1.06). Measured on the default eight seeds it was true in 1,303-
     * 2,749 of 4,001 months and raised the notice 40-858 times a run, which
     * is a log, not news. At the default point: true in 530-717 months,
     * raised 63-188 times (the backstop alone, 22-29). Under the line the
     * slice is the trickle a book past the watch line loses, and the bank
     * has set it aside (Bank.sectorAllowance()).
     */
    public boolean defaultsAreNews(String sector) {
        return wasRestructuredThisMonth(sector)
                || getDefaultShareThisMonth(sector) >= monthlyDefaultShare(INSOLVENCY_TRIGGER);
    }

    /**
     * What each sector owed when the month's insolvency check judged it,
     * against the assets it judged it on (getAssets(), struck at the same
     * check). The bank's month-end reading of a sector - the newest of the
     * quarter its allowance and its price read - is that, with whatever it
     * has borrowed since on both sides (Game.sectorPositions()).
     * Not saved: the load path does not provide, and a sector not judged
     * this month reads what it owes now.
     */
    private final Map<String, Double> principalJudged = new LinkedHashMap<>();

    public double getPrincipalJudged(String sector) {
        Double judged = principalJudged.get(sector);
        return judged != null ? judged : getPrincipal(sector);
    }

    /** Counts down the borrowing bans. Call once a month. */
    public void advanceBlocks() {
        for (String sector : SECTORS) {
            int left = blockedMonths.getOrDefault(sector, 0);
            if (left > 0) {
                blockedMonths.put(sector, left - 1);
            }
        }
    }

    //save / load
    public void setLoans(List<BusinessDebt> loans) {
        this.loans = loans;
    }

    public void clearLoans() {
        loans.clear();
        for (String sector : SECTORS) {
            maturedPrincipal.put(sector, 0.0);
            writtenOffThisMonth.put(sector, 0.0);
            writtenOffTotal.put(sector, 0.0);
            restructures.put(sector, 0);
            blockedMonths.put(sector, 0);
            cashBalance.put(sector, 0.0);
            overdraftForgiven.put(sector, 0.0);
        }
        tillReported.clear();
        defaultedThisMonth.clear();
        defaultShareThisMonth.clear();
        restructuredThisMonth.clear();
        // ...and the quarter's readings (0.7.8).
        statements.clear();
        // ...and the mortgages' flows and the insurance book (0.7.11).
        premiumsThisMonth = 0;
        premiumsBySector.clear();
        mortgageRepaidBySector.clear();
        insuredWrittenOffThisMonth.clear();
        insuredWrittenOffTotal.clear();
        premiumsTotal = 0;
        mortgagesWrittenThisMonth = renewedThisMonth = fallenDueThisMonth = 0;
        renewedLifetime = fallenDueLifetime = 0;
        // ...and the bondholders' losses and the month's bond proceeds (0.7.12).
        bondWrittenOffThisMonth.clear();
        bondWrittenOffTotal.clear();
        bondProceeds.clear();
        shortfallPlans.clear();
        concentrationCharges.clear();
    }

    //printers
    public void printBusinessDebtInfo(int currentMonth) {

        System.out.println("\n=============== PRIVATE SECTOR CREDIT ===============");
        System.out.printf("The bank's prime: %.2f%%%n", primeRate * 100);

        for (String sector : SECTORS) {
            System.out.printf("%n%s%n", sector.toUpperCase());
            System.out.printf("  Outstanding Principal:  $%s%n", formatter.format(getPrincipal(sector)));
            System.out.printf("  Monthly Interest:       $%s%n", formatter.format(getMonthlyInterest(sector)));
            System.out.printf("  Leverage (debt/assets): %.2f%n", getLeverage(sector));
            System.out.printf("  New Borrowing Rate:     %.2f%%  (prime %.2f%% + %.2f%% spread)%n",
                    getRate(sector) * 100, primeRate * 100, getSpread(sector) * 100);
            System.out.printf("  Rate on Existing Debt:  %.2f%%%n", getEffectiveRate(sector) * 100);
            System.out.printf("  Loans Outstanding:      %d%n", getLoanCount(sector));

            for (BusinessDebt loan : getLoans(sector)) {
                System.out.printf("    Month %-4d | $%-12s @ %.2f%%%s%n",
                        loan.getMaturityMonth(),
                        formatter.format(loan.getOutstandingPrincipal()),
                        loan.getAnnualRate() * 100,
                        loan instanceof Mortgage ? "  insured mortgage, renews" : "");
            }
        }

        System.out.println("-----------------------------------------------------");
        System.out.printf("TOTAL PRIVATE CREDIT:     $%s%n", formatter.format(getTotalPrincipal()));
        System.out.printf("TOTAL MONTHLY INTEREST:   $%s%n", formatter.format(getTotalMonthlyInterest()));
        System.out.println("=====================================================\n");
    }

    private static final NumberFormat formatter = NumberFormat.getNumberInstance(Locale.CANADA);

    static {
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);
    }

    /** Every business loan and this month's lending, in the new unit. */
    public void redenominate(double scale) {
        lentThisMonth   *= scale;
        repaidThisMonth *= scale;
        lentBySector.replaceAll((k, v) -> v * scale);
        repaidBySector.replaceAll((k, v) -> v * scale);
        for (BusinessDebt loan : loans) {
            if (loan != null) loan.redenominate(scale);
        }
        maturedPrincipal.replaceAll((sector, due) -> due * scale);
        writtenOffThisMonth.replaceAll((sector, off) -> off * scale);
        writtenOffTotal.replaceAll((sector, off) -> off * scale);
        assets.replaceAll((sector, value) -> value * scale);
        // ...and 0.7.8's two bases: what each owed when the capital rule was
        // set and when the insolvency check judged it. Money, like the rest.
        principalAtRule.replaceAll((sector, owed) -> owed * scale);
        principalJudged.replaceAll((sector, owed) -> owed * scale);
        // ...and the month's defaulted debt; its share is a share.
        defaultedThisMonth.replaceAll((sector, owed) -> owed * scale);
        writtenThisMonth.replaceAll(w -> new Written(w.sector(), w.amount() * scale, w.leverage(), w.rate(), w.project()));
        // ...and the quarter's readings, which are money.
        for (double[] r : statements.values()) for (int i = 0; i < r.length; i++) r[i] *= scale;
        // ...and the mortgages' flows and the insurance book (0.7.11).
        premiumsThisMonth *= scale;
        premiumsBySector.replaceAll((k, v) -> v * scale);
        mortgageRepaidBySector.replaceAll((k, v) -> v * scale);
        insuredWrittenOffThisMonth.replaceAll((k, v) -> v * scale);
        insuredWrittenOffTotal.replaceAll((k, v) -> v * scale);
        premiumsTotal *= scale;
        // ...and the bondholders' losses and the month's bond proceeds (0.7.12).
        bondWrittenOffThisMonth.replaceAll((k, v) -> v * scale);
        bondWrittenOffTotal.replaceAll((k, v) -> v * scale);
        bondProceeds.replaceAll((k, v) -> v * scale);
    }

}
