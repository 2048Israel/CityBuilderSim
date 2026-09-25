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

           rate = prime + max(0, LOSS_GIVEN_DEFAULT x PD(L) - Bank.BASE_LOSS_RATE)
                        + DEFAULT_SURCHARGE x its record

       PD(L) is the curve its firms default on (defaultProbability()), and L
       is THE LEVERAGE THE LOAN LEAVES IT AT: after a shortfall loan, what it
       will owe over the assets it has (the proceeds cover losses already on
       its books); after a project, what it will owe over its assets and the
       building, counted at the loan's value as canFundProject() counts it -
       both on its last quarter's statements, the deal on top, since round 3
       (THE BANK READS A BORROWER FROM ITS LAST QUARTER, below).
       Prime already carries BASE_LOSS_RATE, the loss of a sound book, so a
       sound borrower pays prime; one at the watch line pays 0.83 points over
       it, one at 1.2 10.8, one at the default point 29.6.

       WHAT IT REPLACED. The spread was a line in leverage: SPREAD_PER_DEBT_TO_
       ASSETS, six points a unit, from MIN_SPREAD's one point to MAX_SPREAD's
       eight - Jerus's "even worst case, not too bad" cap - re-based on prime
       in 0.7.7 so that it ran from nothing to seven points over it. The cap
       was what stopped a bad month compounding, and the todo's "Not doing"
       has why the spread was never made dearer (the 12% measurement): the
       shortfall borrower paid the extra interest by borrowing it.
       That does not bite here: under the shortfall desk's ceiling
       (MAX_LOAN_TO_ASSETS, 0.9) the curve charges at most 0.83 points over
       prime - less than the old line's 5.4 there - and past it the desk lends
       only the month's interest. What the curve charges past the ceiling is
       the projects', and a project is judged at the rate it would be written
       at (Game.consider()), which is the brake: a building that takes a
       sector to 1.2 has to earn prime and eleven points. Until 0.7.8 the
       seven-point cap sat under the curve's own expected loss from about 1.15
       times its assets, and the bank lent there at a price its own curve said
       lost money.
       ======================================================================= */

    /** A borrower's own expected loss over the book's, at this leverage: LOSS_GIVEN_DEFAULT x PD(L) less Bank.BASE_LOSS_RATE, never below nothing - the part of its rate that is its risk. */
    public static double expectedLossSpread(double leverage) {
        return Math.max(0, LOSS_GIVEN_DEFAULT * defaultProbability(leverage) - Bank.BASE_LOSS_RATE);
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
       nothing left at all.
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

    /** What a restructured borrower is left owing, as a multiple of its assets. Public since 0.7.8: what a default costs the bank is read off this - LOSS_GIVEN_DEFAULT for a firm, and the backstop's Bank.lossIfDefaulted() for a sector with nothing left. */
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
       hazard h = 1 - (1 - PD)^(1/12), and the bank loses LOSS_GIVEN_DEFAULT
       of it: principal x h x LOSS_GIVEN_DEFAULT, every loan of the sector
       written down pro rata (defaultSlice()). The firms that defaulted keep
       their plant - a write-down, not a liquidation, as the restructure
       always was - so the sector's assets stand, its debt falls, its leverage
       falls and next month's hazard with it. A sector that keeps losing money
       keeps rising into the hazard; one that stabilises drains out of it.

       What it gives at ASSET_VOLATILITY = 0.25, printed by BankCheck (14)
       from these functions:

           leverage   default a year   a month   lost a year, of its debt
             0.30         0.00%          0.00%        0.00%
             0.50         0.00%          0.00%        0.00%
             0.70         0.11%          0.01%        0.07%
             0.90         2.05%          0.17%        1.23%
             1.10        10.74%          0.94%        6.44%
             1.30        28.35%          2.74%       17.01%
             1.50        50.00%          5.61%       30.00%
             2.00        87.51%         15.91%       52.50%
             3.00        99.72%         38.76%       59.83%

       ("lost a year" is LOSS_GIVEN_DEFAULT x PD: what a sector held at that
       leverage loses; one that is not held drains down the table.)

       THE WHOLE-SECTOR RESTRUCTURE STAYS AS THE BACKSTOP, for the case where
       every firm is under water at once: assets at or below zero with debt or
       an overdraft (isInsolvent()). There, restructure() runs as it always
       did - written down to RESTRUCTURE_TARGET of its assets (nothing), the
       overdraft forgiven, the record counted, the ban. Only that counts
       toward the record, the surcharge and the ban: a slice does not, because
       the price already charges a sector in trouble its expected loss (PRICING
       FROM THE CURVE).
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

    /**
     * What the bank loses on a dollar that defaults: a defaulting firm sits
     * at the default point, owing INSOLVENCY_TRIGGER times its assets, and
     * the restructure rule leaves it owing RESTRUCTURE_TARGET of them - so
     * 1 - 0.6 / 1.5 = 60%. Derived from those two, not a number of its own.
     */
    public static final double LOSS_GIVEN_DEFAULT = 1 - RESTRUCTURE_TARGET / INSOLVENCY_TRIGGER;

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
     * borrowed - and it is what a restructure forgives; see restructure().
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
        double spread = expectedLossSpread(pricingLeverage(principal, totalAssets));

        // The record, priced: a bad HISTORY is not a leverage.
        spread += recordSurcharge(sector);

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

    /** ...the first part of it: its own expected loss over the book's, at the leverage its last quarter reads (expectedLossSpread(), quarterPrincipal() over quarterAssets()). */
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
    }

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

    /** Everything every sector owes. What the bank has lent the businesses. */
    public double getAllPrincipal() {
        double total = 0;
        for (String s : SECTORS) total += getPrincipal(s);
        return total;
    }

    public double getPrincipal(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                total += loan.getOutstandingPrincipal();
            }
        }
        return total;
    }

    public double getTotalPrincipal() {
        double total = 0;
        for (BusinessDebt loan : loans) {
            total += loan.getOutstandingPrincipal();
        }
        return total;
    }

    /** This month's interest cost for a sector - the income statement's expense line. */
    public double getMonthlyInterest(String sector) {
        double total = 0;
        for (BusinessDebt loan : loans) {
            if (loan.getSector().equals(sector)) {
                total += loan.getMonthlyInterestExpense();
            }
        }
        return total;
    }

    public double getTotalMonthlyInterest() {
        double total = 0;
        for (BusinessDebt loan : loans) {
            total += loan.getMonthlyInterestExpense();
        }
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
                    if (lendingOpen) {
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
     * Underwrites a loan if the sector is short, and returns the proceeds.
     *
     * @param cash        the sector's cash after the month has settled
     * @param monthlyLoss this month's loss, if any - used to size the buffer
     * @param month       the current month, for the maturity schedule
     * @return the amount lent, which the caller must add to that sector's cash
     */
    public double coverShortfall(String sector, double cash, double monthlyLoss, int month) {

        if (cash >= 0) {
            return 0;
        }

        // A sector in default cannot raise money. Its cash stays negative, which
        // is the honest picture and the pressure that makes it shed capacity.
        if (isBorrowingBlocked(sector)) {
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
           credit was short-circuiting it.
           ===================================================================== */
        double room = borrowingRoom(sector);

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
        }

        amount = Math.min(amount, room);

        if (amount <= 0) {
            return 0;
        }

        issueLoan(sector, amount, month);

        return amount;
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
     * shut - see lendingOpen. And since 0.7.8 no more than the bank's capital
     * rule lets the sector's debt grow this month (capitalRoom()).
     */
    public double borrowingRoom(String sector) {
        return roomUnder(sector, MAX_LOAN_TO_ASSETS);
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
        double assetsAfter = Math.max(0, getAssets(sector)) + amount;
        if (getPrincipal(sector) + amount > assetsAfter * INSOLVENCY_TRIGGER) return false;
        // ...and what the bank's capital lets it lend this month (0.7.8): a
        // building is new lending, never keeping a borrower going.
        if (keepGoingOnly || amount > capitalRoom(sector)) {
            refusedForCapital.add(sector);
            return false;
        }
        return true;
    }

    /** The ceiling at this multiple of assets, and the bank's capital rule on top of it. */
    private double roomUnder(String sector, double multiple) {
        return Math.min(ceilingRoom(sector, multiple), capitalRoom(sector));
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
        if (getPrincipal(sector) + principal > assetsAfter * INSOLVENCY_TRIGGER) {
            mortgageRefusal = MORTGAGE_PAST_DEFAULT_POINT;
            return false;
        }
        if (insuredRationed && principal > capitalRoom(sector)) {
            mortgageRefusal = MORTGAGE_CAPITAL;
            refusedForCapital.add(sector);
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

    /** What one sector owes that nobody insures: its debt less its insured mortgages - what the bank's allowance reads, and its capital rule while the leverage ratio does not bind (rationedPrincipal()). */
    public double getUninsuredPrincipal(String sector) {
        return getPrincipal(sector) - getInsuredPrincipal(sector);
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
        double insured = 0;
        for (BusinessDebt loan : loans) {
            if (!loan.getSector().equals(sector)) continue;
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
       ===================================================================== */

    private double capitalGrowth = Double.POSITIVE_INFINITY;
    private boolean keepGoingOnly;
    private final Map<String, Double> principalAtRule = new LinkedHashMap<>();
    /** Sectors whose project the capital rule refused this month, so the investor can say so. */
    private final java.util.Set<String> refusedForCapital = new java.util.HashSet<>();

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
    }

    /** True this month when the capital rule rations the insured mortgages too - the bank's leverage requirement binding. */
    private boolean insuredRationed;

    public boolean isInsuredRationed() { return insuredRationed; }

    /** The debt the capital rule reads: the uninsured, or all of it while insuredRationed. */
    private double rationedPrincipal(String sector) {
        return insuredRationed ? getPrincipal(sector) : getUninsuredPrincipal(sector);
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

    /* =====================================================================
       THE BANK READS A BORROWER FROM ITS LAST QUARTER (0.7.8, round 3)

       Jerus, 2026-09-23: "the bank reads a borrower's debt level from its last
       quarter's average, like real statements, not one month's stock swing."
       A lender rates a borrower off its statements, quarterly, not off one
       month's balance - and a sector that buys two months of stock at once
       swings a sixth of its assets from one month to the next (round 2's
       Luxury Retail, whose allowance flipped $227M to $637M on it).

       So the bank's READINGS of a sector - its allowance and the share in
       stage 2 (Game.provideForLosses()), and the price of a new loan
       (priceSector()) - use its leverage over the last STATEMENT_MONTHS
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
       on a probe, 2026-09-24; no rule moved.
       ===================================================================== */

    /** How many month-end readings the bank averages a borrower over: a quarter, as a real lender reads its statements. */
    public static final int STATEMENT_MONTHS = 3;

    /** Each sector's last month-end readings, oldest first: {owed, owned, owed, owned, ...}, at most STATEMENT_MONTHS pairs. */
    private final Map<String, double[]> statements = new LinkedHashMap<>();

    /** One month-end reading of a sector: what it owed and what it owned, as the bank read them (Game.sectorPositions()). */
    public void recordStatement(String sector, double owed, double owned) {
        if (sector == null || !Double.isFinite(owed) || !Double.isFinite(owned)) return;
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
     * (defaultSlice()).
     *
     * Call once a month, AFTER the balance sheets have been refreshed - the
     * whole judgement is principal against assets, so acting on last month's
     * assets would restructure the wrong sector.
     *
     * @return the amount written off, or 0 if the sector was solvent enough
     */
    public double restructure(String sector) {

        if (!isInsolvent(sector)) {
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

        if (writeOff > 0) {
            // Every instrument pro rata; what came off its insured mortgages
            // is the treasury's to pay the bank (0.7.11).
            writeDownSector(sector, target / principal);
        }

        overdraftForgiven.put(sector, getOverdraftForgivenPending(sector) + overdraft);
        cashBalance.put(sector, getCash(sector) + overdraft);

        writtenOffThisMonth.put(sector, getWrittenOffThisMonth(sector) + writeOff);
        writtenOffTotal.put(sector, getWrittenOffTotal(sector) + writeOff);
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

        return writeOff;
    }

    /**
     * THE MONTH'S DEFAULTS, every sector: the backstop for a sector with
     * nothing left (restructure()), and for every other the slice of its
     * debt whose firms fell through the default point (defaultSlice()).
     * @return total written off this month.
     */
    public double restructureInsolventSectors() {

        double total = 0;
        for (String sector : SECTORS) {
            writtenOffThisMonth.put(sector, 0.0);
        }
        insuredWrittenOffThisMonth.clear();
        defaultedThisMonth.clear();
        defaultShareThisMonth.clear();
        restructuredThisMonth.clear();

        for (String sector : SECTORS) {
            total += restructure(sector);
            total += defaultSlice(sector);
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
     * balance-sheet refresh), written off at LOSS_GIVEN_DEFAULT. Every loan of
     * the sector is written down pro rata, as restructure() does, and booked
     * through the same writtenOffThisMonth/writtenOffTotal the bank reads.
     * Its assets are untouched - the defaulted firms keep their plant - so its
     * leverage falls. Not on its record, not surcharged, no ban: see A SECTOR
     * DEFAULTS A SLICE AT A TIME. Nothing for a sector with no debt, and
     * nothing for one with no assets, which is the backstop's.
     *
     * @return the amount written off
     */
    public double defaultSlice(String sector) {
        double principal = getPrincipal(sector);
        double totalAssets = getAssets(sector);
        if (!(principal > 0) || !(totalAssets > 0)) return 0;
        double share = monthlyDefaultShare(principal / totalAssets);
        double writeOff = principal * share * LOSS_GIVEN_DEFAULT;
        if (!(writeOff > 0)) return 0;
        // Every instrument pro rata; the insured part is the treasury's
        // claim to pay (0.7.11).
        writeDownSector(sector, 1 - share * LOSS_GIVEN_DEFAULT);
        defaultedThisMonth.put(sector, principal * share);
        defaultShareThisMonth.put(sector, share);
        writtenOffThisMonth.put(sector, getWrittenOffThisMonth(sector) + writeOff);
        writtenOffTotal.put(sector, getWrittenOffTotal(sector) + writeOff);
        // Less debt against the same assets: its price off the curve falls with it.
        rates.put(sector, priceSector(sector));
        return writeOff;
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

    /** The debt whose firms defaulted this month in the slice, before what the bank recovers - the write-off is LOSS_GIVEN_DEFAULT of it. */
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
    }

}
