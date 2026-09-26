package ham.citybuildersim;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The corporate bond market: every bond the city's businesses have issued,
 * who holds each, the order book each trades on, and the rule each
 * participant trades by.
 *
 * ==================== WHY THIS EXISTS ====================
 *
 * With the landlords on insured mortgages (0.7.11) the bank failed 115 times
 * over the eight default seeds, against 42 before, and the cause was
 * concentration: the one bank was every business's only lender, so the
 * sector that broke it - Manufacturing, Automotive, Luxury Retail - owed it
 * five to six times its equity. Jerus's answer (2026-09-24): business
 * borrowing becomes two things, bank loans and bonds sold to investors, and
 * a business takes the cheaper. His answers to the design questions, by
 * label, and where each lives:
 *
 *   "Cheapest, within the bank's limit." - WHO ISSUES, AND WHEN (plan()).
 *   "No limit, just price it."            - Bank, THE BANK PRICES CONCENTRATION.
 *   Who buys: households; the bank, if it wants; companies with idle cash;
 *   the world.                             - THE PARTICIPANTS.
 *   "Bank notes rank first."               - round 1's absolute priority,
 *                                            superseded in round 2 by
 *   "Real averages by type."               - BusinessDebtManager, RECOVERIES
 *                                            BY INSTRUMENT.
 *   "Order book for both."                 - OrderBook; the bonds, and since
 *                                            round 2 the shares (Exchange).
 *
 * ==================== WHAT IT IS NOT ====================
 *
 * Not the landlords' mortgages, which stay insured bank loans (0.7.11): a
 * mortgage is not a bond. Not the city's own paper (DebtManager), which its
 * bank still buys at issue and its desk still buys back. And not a place
 * where anybody must trade: an order waits until somebody meets it.
 *
 * EVERY DIAL HERE IS ANOTHER CLASS'S. The underwriter's charge is the one
 * the city pays (Game.FIXED_ISSUE_COST, UNDERWRITING_SPREAD); each
 * participant bids by the rule it already follows for the nearest thing it
 * holds today - the households by their rule for the city's paper, the
 * companies and the world by the rules that send idle cash and hot money
 * where the return is, the bank by its own loan price. The term is
 * CorporateBond.TERM_MONTHS.
 */
public class BondMarket implements BusinessDebtManager.BondBook, BusinessDebtManager.BondDesk {

    /* =====================================================================
       THE PARTICIPANTS

       Each one's order comes from a rule the model already had, applied to
       a bond's EXPECTED RETURN - its yield less what a bondholder expects to
       lose on it a year, expectedLoss(): the issuer's default rate at its
       leverage (0.7.8's curve) times a bond's loss given default
       (BusinessDebtManager, RECOVERIES BY INSTRUMENT).

         THE HOUSEHOLDS, cell by cell since 0.7.12 round 2 ("Each household
         type trades"): every cell holds its own face of each bond
         (Household.bondFace) and posts its own orders under the name
         CELL + its key. Together they want a share of each issue that rises
         with its expected return over what the bank pays savers - the
         city's paper's rule exactly, HOUSEHOLD_PAPER_APPETITE a unit of
         spread up to MAX_HOUSEHOLD_PAPER_SHARE of an issue - and each cell
         wants its part of that in proportion to its money for bonds (savings
         past the cushion plus what it holds in them), out of its own savings
         past the cushion. It closes the gap at OutwardInvestment.OUT_SPEED a
         month and sells an excess at HOME_SPEED, the pace their dollars
         abroad move at. A cell SHORT OF MONEY sells in the waterfall,
         after the city's paper and its dollars abroad and before its shares
         (Household.settle()), asking what makes the buyer's yield its own
         borrowing rate - it sells when that is cheaper than borrowing - and
         if nobody meets it, it borrows. One cell buying from another is a
         transfer inside the households, which no pool sees.

         THE BANK, if it wants: it bids at the price that makes the bond earn
         what an equal loan to the issuer would - its funds-transfer price for
         the months left, its running costs, the expected loss at a BOND's
         loss given default, the capital charge with the book's
         concentration, and the issuer's record (bankYield()) - on the
         capital it holds over its target, the desk's spare-capital limit
         (0.7.8). Under its target it asks, at the bond's value, for what
         takes it back there.

         COMPANIES WITH IDLE CASH, by the rule that sends it abroad
         (OutwardInvestment): a sector owing nothing puts a share of its
         wealth into other sectors' bonds that rises with their expected
         return over the world's rate, OutwardInvestment.APPETITE a unit of
         spread up to MAX_SHARE, at OUT_SPEED of the gap a month. Never its
         own bonds.

         THE WORLD, by hot money's rule (CapitalFlows): a stock of the city's
         bonds rising with their expected return over the world's rate and
         the country premium - CapitalFlows.APPETITE years of output a unit
         of spread, the spread capped at MAX_SPREAD - arriving at
         ARRIVAL_SPEED of the gap and leaving at DEPARTURE_SPEED, and all of
         it at PANIC_EXIT while the money is running. Its purchases are a
         capital inflow the currency sees, its coupons an income outflow.

       EACH ORDER IS PRICED AT ITS MARGINAL UNIT: a buyer bids the price at
       which the return on the last unit it buys still meets its rule, a
       seller asks the price at which the last unit it sells still does. So
       a buyer short of its target bids over the bond's value, one over it
       asks under it, and they meet.
       ===================================================================== */

    /** The bank's desk, on the book. */
    public static final String BANK = "bank";
    /** The world, on the book. */
    public static final String WORLD = "world";
    /** One household cell on the book - bidding, asking, or selling in the waterfall: this, then its key. */
    public static final String CELL = "household:";

    /**
     * THE LEAST A DEFAULT RATE IS READ AT: the through-the-cycle default rate
     * a sound loan is priced for, Bank.BASE_LOSS_RATE over a loan's loss
     * given default, BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT - 1.6% a year
     * since 0.7.12 round 2 (0.67% at the old uniform 60%: RECOVERIES BY
     * INSTRUMENT) - so a sound issuer's bondholder expects the default rate a
     * sound borrower's lender prices, at a bond's own loss given default:
     * 0.88% a year against the lender's 0.4%, because a bond gives back less.
     * Derived, not a dial.
     */
    public static final double DEFAULT_RATE_FLOOR = Bank.BASE_LOSS_RATE / BusinessDebtManager.LOAN_LOSS_GIVEN_DEFAULT;

    /** The term in years, for spreading an issue's costs over its life. */
    public static final double TERM_YEARS = CorporateBond.TERM_MONTHS / 12.0;

    /** The arithmetic a bond's all-in cost may sit over the loan's and still be "no dearer": a billionth of a point a year - the bisection's own resolution, not a margin (plan()). */
    static final double CHEAPER_BY = 1e-11;

    /* ============================ what it reads ============================ */

    /** The city's readings the market prices on, which Game supplies. */
    public interface Readings {
        int month();
        /** The city's curve at this many months (DebtManager.curveRate()). */
        double curve(int months);
        /** The policy rate, for the bank's own prices. */
        double policyRate();
        /** What the bank pays savers. */
        double depositRate();
        /** What the world pays (DebtManager.WORLD_BASE_RATE). */
        double worldRate();
        /** What the world charges this city for its risk (DebtManager.countryPremium()). */
        double countryPremium();
        /** A month's output. */
        double monthlyGdp();
        /** Local money per dollar. */
        double localPerUsd();
        /** True while hot money is running (CapitalFlows.isStopped()). */
        boolean worldRunning();
        /** The currency's unit (Denomination.getUnit()), for the underwriter's fixed fee. */
        double unit();
    }

    private Readings readings;
    private HouseholdBalance households;
    private Bank bank;
    private EconomyManager economy;
    private OutwardInvestment outward;

    /** Wires the market into a city. Any may be null in a fixture. */
    public void attach(Readings readings, HouseholdBalance households, Bank bank,
                       EconomyManager economy, OutwardInvestment outward) {
        this.readings = readings;
        this.households = households;
        this.bank = bank;
        this.economy = economy;
        this.outward = outward;
    }

    /* ================================ state ================================ */

    private final List<CorporateBond> bonds = new ArrayList<>();
    private final Map<String, OrderBook> books = new LinkedHashMap<>();
    private int nextId = 1;

    /* ---- the month's flows, cleared by startMonth(), saved (the screens read them the month after) ---- */
    private double householdsBought, householdsSold;          // cash, households <-> the pools
    private double householdsBoughtAbroad, householdsSoldAbroad; // cash, households <-> the world
    private double worldBought, worldSold;                    // cash, the world <-> the pools
    private double couponsToHouseholds, couponsToBank, couponsToCompanies, couponsAbroad;
    private double principalToHouseholds, principalToBank, principalToCompanies, principalAbroad;
    private double lossHouseholds, lossBank, lossCompanies, lossWorld;  // face written off, by holder
    private double bankLossCost;                              // what the bank's holdings cost it, written off
    private double issuedFace, issuedCosts;
    private int issues;
    private double emigrantsFace;                             // households' bonds taken abroad by leavers
    private final Map<String, Double> issuedBySector = new LinkedHashMap<>();
    private final Map<String, Double> proceedsBySector = new LinkedHashMap<>();
    private final Map<String, Double> repaidBySector = new LinkedHashMap<>();
    private final Map<String, Double> boughtBySector = new LinkedHashMap<>();   // net cash out on bonds it holds
    private final Map<String, Double> couponsBySector = new LinkedHashMap<>();  // coupons it was paid
    private final Map<String, Double> bankLossBySector = new LinkedHashMap<>();
    /** The last month's order-book figures, all books together, and the last issue - for the screens. */
    private double lastPostedBuy, lastPostedSell, lastFilled, lastSellQuantityWaited;
    private int lastSellsPosted, lastSellsWaited, lastTrades;

    /* ---- over the city's life, saved ---- */
    private double lifeIssued, lifeCosts, lifeCouponsHouseholds, lifeCouponsBank, lifeCouponsCompanies, lifeCouponsAbroad;
    private double lifeLossHouseholds, lifeLossBank, lifeLossCompanies, lifeLossWorld;
    private double lifeVolume, lifePostedSell, lifeFilledSell, lifeWorldBought, lifeWorldSold;
    private int lifeIssues, lifeSellsPosted, lifeSellsWaited;
    /** Cell to cell, this month and over the city's life: cash and trades (round 2). */
    private double betweenHouseholds, lifeBetweenHouseholds;
    private int betweenHouseholdsTrades, lifeBetweenHouseholdsTrades;

    /* ---- the coupons struck at the top of the month, paid at the market's step (not saved: a month's working, settled before any save) ---- */
    private double dueHouseholds, dueBank, dueWorld;
    /** ...the households' part, cell by cell, by the cell's key: what each held at the record date (round 2). */
    private final Map<String, Double> dueCells = new LinkedHashMap<>();
    private final Map<String, Double> dueCompanies = new LinkedHashMap<>();
    private final Map<String, Double> dueByIssuer = new LinkedHashMap<>();

    /* ============================== the bonds ============================== */

    /** Every bond outstanding, oldest first. */
    public List<CorporateBond> getBonds() { return java.util.Collections.unmodifiableList(bonds); }

    /** One sector's bonds. */
    public List<CorporateBond> getBonds(String issuer) {
        List<CorporateBond> out = new ArrayList<>();
        for (CorporateBond b : bonds) if (b.issuer.equals(issuer)) out.add(b);
        return out;
    }

    /** The face of one issuer's bonds that fall due at `month`'s settle (redeemMaturing()): asked ahead by what a buyer can pay for (0.7.12 round 6). */
    public double maturingFace(String issuer, int month) {
        double due = 0;
        for (CorporateBond b : bonds) if (b.issuer.equals(issuer) && b.isMatured(month)) due += b.face;
        return due;
    }

    /** The bond with this id, or null. */
    public CorporateBond bond(int id) {
        for (CorporateBond b : bonds) if (b.id == id) return b;
        return null;
    }

    /** The order book a bond trades on - a reader: an empty one, not kept, for a bond with none. */
    public OrderBook bookOf(CorporateBond b) {
        OrderBook book = books.get(b.instrument());
        return book != null ? book : new OrderBook(b.instrument());
    }

    /** ...and the one the market posts to, opened if it has none - at the market's dust (see dust). */
    private OrderBook openBook(CorporateBond b) {
        OrderBook book = books.get(b.instrument());
        if (book == null) {
            book = new OrderBook(b.instrument());
            book.setDust(dust);
            books.put(b.instrument(), book);
        }
        return book;
    }

    /* =====================================================================
       THE BOND BOOK'S DUST, AND WHY IT IS SEEDED (0.7.12 round 6)

       A bond book counts FACE, which is money, and every book read
       OrderBook.DUST - a billionth - against it as an absolute amount. After
       a hundred-to-one reform every face is a hundred times smaller, so a sell
       of a few billionths of each bond that posted in the plain city fell
       under the dust in its reformed twin: round 5 found DenominationCheck's
       two cities parting there, a year after the reform. A money constant
       that a reform never reseeded, like Equity.FOUNDING_PRICE,
       Exchange.MIN_FAIR, OutwardInvestment.MIN_MOVE and
       HouseholdBalance.MIN_MOVE before it, and fixed the way they were: in
       founding money, divided by the unit on load (seedConstants()), and
       scaled with the face when a reform scales it (redenominate(), and each
       book's own). Jerus, 2026-09-25: "Make both scale with the currency, the
       way the tree's other money thresholds do."

       THE SHARE BOOK IS NOT THIS BUG: its quantities are shares, which a
       reform does not move, so OrderBook.DUST stays a literal there.
       ===================================================================== */

    /** OrderBook.DUST of face, in today's money. See above. */
    private double dust = OrderBook.DUST;

    /** Re-seeds the dust at a given unit, and every open book's with it. See Denomination. */
    public void seedConstants(double unit) {
        dust = OrderBook.DUST / (unit > 0 ? unit : 1);
        for (OrderBook book : books.values()) book.setDust(dust);
    }

    /** Every bond's face outstanding. */
    public double totalFace() {
        double t = 0;
        for (CorporateBond b : bonds) t += b.face;
        return t;
    }

    /** ...held by each class. */
    public double faceHeldByHouseholds() { double t = 0; for (CorporateBond b : bonds) t += b.households; return t; }
    public double faceHeldByBank()       { double t = 0; for (CorporateBond b : bonds) t += b.bank; return t; }
    public double faceHeldByWorld()      { double t = 0; for (CorporateBond b : bonds) t += b.world; return t; }
    public double faceHeldByCompanies()  { double t = 0; for (CorporateBond b : bonds) t += b.companiesTotal(); return t; }
    /** ...by one company. */
    public double faceHeldBy(String sector) { double t = 0; for (CorporateBond b : bonds) t += b.company(sector); return t; }
    /** What the bank's bonds cost it: its carrying value. */
    public double bankCost() { double t = 0; for (CorporateBond b : bonds) t += b.bankCost; return t; }
    /** ...of one issuer's bonds. */
    public double bankCost(String issuer) {
        double t = 0;
        for (CorporateBond b : bonds) if (b.issuer.equals(issuer)) t += b.bankCost;
        return t;
    }

    /* ====================== the lender's view (BondBook) ====================== */

    @Override public double principal(String sector) {
        double t = 0;
        for (CorporateBond b : bonds) if (b.issuer.equals(sector)) t += b.face;
        return t;
    }

    @Override public double monthlyCoupon(String sector) {
        double t = 0;
        for (CorporateBond b : bonds) if (b.issuer.equals(sector)) t += b.monthlyCoupon();
        return t;
    }

    /**
     * A DEFAULT, ON THE BONDHOLDERS (0.7.12): every bond of the sector
     * written down to this share of its face, every holder by the same share
     * - the households' face and every cell's own face of it (round 2), the
     * bank's face and what it cost, each company's, the world's. The bank's part of the
     * loss, at what it paid, waits for Game to book against its allowance
     * (takeBankLoss()); a company's lowers its assets; the world's is
     * declared across the border (getWorldWrittenOff()). No cash moves.
     */
    @Override public double writeDown(String sector, double scale) {
        double keep = Math.max(0, Math.min(1, scale));
        double gone = 0;
        List<Integer> ids = new ArrayList<>();
        for (CorporateBond b : bonds) {
            if (!b.issuer.equals(sector)) continue;
            double hh = b.households, bk = b.bank, cost = b.bankCost, w = b.world, co = b.companiesTotal();
            gone += b.writeDown(keep);
            double hhLost = hh - b.households;
            ids.add(b.id);
            lossHouseholds += hhLost;
            lossBank += bk - b.bank;
            double costLost = cost - b.bankCost;
            if (costLost > 0) {
                bankLossCost += costLost;
                bankLossBySector.merge(sector, costLost, Double::sum);
                if (bank != null) bank.bondsWrittenDown(costLost);
            }
            lossWorld += w - b.world;
            lossCompanies += co - b.companiesTotal();
        }
        // ...every cell's own holding of them by the same share (round 2),
        // the issuer's bonds together.
        if (households != null && !ids.isEmpty()) households.writeDownBonds(ids, keep);
        return gone;
    }

    /** What the month's defaults took off the bank's bonds of this sector, at what it paid - read once by Game, which books it against the bank's allowance with the sector's loans. */
    public double takeBankLoss(String sector) {
        Double v = bankLossBySector.remove(sector);
        if (v != null) bankLossTaken.merge(sector, v, Double::sum);
        return v == null ? 0 : v;
    }

    /** What takeBankLoss() handed Game this month, by sector, for reading after (the Bank tab's write-off by sector is the loans' and this). The month's, cleared by startMonth(), not saved. */
    private final Map<String, Double> bankLossTaken = new LinkedHashMap<>();

    /** ...one sector's: the bank's loss on its bonds this month, at what it paid (0.7.12, round 2). */
    public double getBankLossThisMonth(String sector) { return bankLossTaken.getOrDefault(sector, 0.0); }

    /* ============================ the valuation ============================ */

    /** The lender, for the issuer's readings. */
    private BusinessDebtManager credit() { return economy == null ? null : economy.getBusinessDebtManager(); }

    /**
     * THE ISSUER'S DEFAULT RATE, a year, for a bondholder: 0.7.8's curve at
     * the leverage its last quarter reads, with a deal on top - `extraDebt`
     * owed and `extraAssets` owned - never under DEFAULT_RATE_FLOOR. All of
     * it against no assets at all.
     */
    public double defaultRate(String issuer, double extraDebt, double extraAssets) {
        BusinessDebtManager c = credit();
        if (c == null) return DEFAULT_RATE_FLOOR;
        double owed = c.quarterPrincipal(issuer) + Math.max(0, extraDebt);
        double owned = extraAssets > 0 ? Math.max(0, c.quarterAssets(issuer)) + extraAssets : c.quarterAssets(issuer);
        double pd = BusinessDebtManager.defaultProbability(BusinessDebtManager.pricingLeverage(owed, owned));
        return Math.max(DEFAULT_RATE_FLOOR, pd);
    }

    /**
     * WHAT A BONDHOLDER EXPECTS TO LOSE a year on this issuer's bonds: its
     * default rate times a bond's loss given default,
     * BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT - whatever else the issuer
     * owes, since 0.7.12 round 2 (RECOVERIES BY INSTRUMENT; round 1 read what
     * absolute priority left the bonds at the issuer's mix, which was all of
     * it once its loans took the recovery).
     */
    public double expectedLoss(String issuer) {
        return defaultRate(issuer, 0, 0) * BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT;
    }

    /** WHAT A BOND IS WORTH TO THE MARKET: the city's curve for the months it has left plus what a holder expects to lose on it - the yield the participants bid and ask around. */
    public double modelYield(CorporateBond b, int month) {
        int n = Math.max(1, b.remainingMonths(month));
        return (readings == null ? 0 : readings.curve(n)) + expectedLoss(b.issuer);
    }

    /** ...as a price a unit of face. */
    public double modelPrice(CorporateBond b, int month) {
        return b.priceAt(modelYield(b, month), month);
    }

    /** The last price it traded at on its book, or NaN before its first trade. */
    public double lastPrice(CorporateBond b) {
        OrderBook book = books.get(b.instrument());
        return book == null ? Double.NaN : book.lastPrice();
    }

    /** The yield at its last traded price, or at its value before its first trade. */
    public double lastYield(CorporateBond b, int month) {
        double p = lastPrice(b);
        if (!(p > 0)) return modelYield(b, month);
        return CorporateBond.yieldAtPrice(b.coupon, b.remainingMonths(month), p);
    }

    /**
     * WHAT THE BANK WOULD EARN LENDING THE ISSUER THE SAME MONEY: an equal
     * loan's four parts at the bond's term (Bank.loanRate() at
     * RISK_BUSINESS), the book's concentration on the issuer
     * (Bank.concentrationCharge()), the issuer's own expected loss over the
     * book's at a BOND's loss given default, and its record - the yield at
     * which a bond earns the bank what the loan would.
     *
     * AT THE LEVERAGE THE WHOLE DEAL LEAVES: `extraBonds` of this bond and
     * `dealDebt` in all, the bond and the bank's loan beside it. The loan's
     * price reads the sector after the whole borrowing
     * (BusinessDebtManager.projectRate()), so the bond's must too - priced on
     * the bond alone against the whole building, a small bond beside a large
     * loan read a sound balance sheet the deal did not leave, and cleared a
     * bond two and a half points under the loan it was financed beside (the
     * first measured run, month 3).
     */
    public double bankYield(String issuer, int months, double extraBonds, double dealDebt, double extraAssets) {
        if (bank == null || readings == null) return Double.POSITIVE_INFINITY;
        double policy = readings.policyRate();
        BusinessDebtManager c = credit();
        double deal = Math.max(Math.max(0, extraBonds), dealDebt);
        double owed = c == null ? 0 : c.quarterPrincipal(issuer) + deal;
        double owned = c == null ? 0 : extraAssets > 0 ? Math.max(0, c.quarterAssets(issuer)) + extraAssets : c.quarterAssets(issuer);
        double lev = BusinessDebtManager.pricingLeverage(owed, owned);
        return bank.loanRate(policy, months, Bank.RISK_BUSINESS)
                + bank.concentrationCharge(policy, months, issuer)
                + BusinessDebtManager.expectedLossSpread(lev, BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT)
                + (c == null ? 0 : c.getRecordSurcharge(issuer));
    }

    /** True while the bank stands, has a branch, and is over its capital target: the only bank that buys. */
    private boolean bankBuys() {
        return bank != null && bank.getBranches() > 0 && !bank.isInsolvent()
                && bank.equity() >= bank.targetEquity();
    }

    /* =====================================================================
       WHAT AN ISSUE COSTS

       The city's own shape, reused (Game, WHAT IT COSTS TO GO TO MARKET AT
       ALL): a fixed part - counsel, the rating, the listing - plus the
       underwriter's spread on the face, 0.75%, inside the 0.5-1% gross
       spread investment-grade corporate issues pay (Melnik & Nissim, 2003,
       "Debt issue costs and issue characteristics in the market for US
       dollar denominated international bonds"). The bank is the
       underwriter, as it is for the city: it is paid the costs out of the
       proceeds. Spread over the bond's TERM_YEARS, they are what makes a
       small bond dear - so the size at which a bond beats the bank is not
       set anywhere; it is where these costs and the two prices cross
       (crossover()).
       ===================================================================== */

    /** What an issue of this face costs to bring: Game.FIXED_ISSUE_COST, in today's money, and Game.UNDERWRITING_SPREAD of the face. */
    public double issueCost(double face) {
        if (!(face > 0)) return 0;
        double unit = readings == null ? 1 : readings.unit();
        return Game.FIXED_ISSUE_COST / (unit > 0 ? unit : 1) + face * Game.UNDERWRITING_SPREAD;
    }

    /** A bond's all-in cost a year: its coupon and its issuing costs spread over its life. */
    public double allIn(double face, double coupon) {
        return face > 0 ? coupon + issueCost(face) / face / TERM_YEARS : Double.POSITIVE_INFINITY;
    }

    /** A bank loan's all-in cost a year: its rate and its fee (Bank.LOAN_FEE) spread over its term, BusinessDebtManager.LOAN_TERM_MONTHS - the loan's own issuing cost, counted as the bond's are. */
    public static double loanAllIn(double loanRate) {
        return loanRate + Bank.LOAN_FEE * 12.0 / BusinessDebtManager.LOAN_TERM_MONTHS;
    }

    /**
     * THE SIZE AT WHICH A BOND BEGINS TO BEAT THE BANK: the face at which
     * the coupon and the costs spread over the life equal the loan's all-in
     * cost -
     *
     *     face = fixed / (TERM_YEARS x (loan all-in - coupon - spread / TERM_YEARS))
     *
     * with the fixed part in today's money. Infinite when the coupon alone,
     * with the spread, is no cheaper than the loan. Printed by BondCheck (3);
     * nothing in the model reads it - the choice is made on the two all-in
     * costs of the deal in hand.
     */
    public double crossover(double loanRate, double coupon) {
        double unit = readings == null ? 1 : readings.unit();
        double fixed = Game.FIXED_ISSUE_COST / (unit > 0 ? unit : 1);
        double margin = loanAllIn(loanRate) - coupon - Game.UNDERWRITING_SPREAD / TERM_YEARS;
        return margin > 0 ? fixed / (TERM_YEARS * margin) : Double.POSITIVE_INFINITY;
    }

    /* =====================================================================
       THE BOOK IS BUILT

       A new issue is sold the way an underwriter sells one: it collects
       every participant's bid - how much it would take at each coupon - and
       sets the coupon at the lowest yield that fills the issue, so the bond
       is worth exactly par the day it is sold (CorporateBond.priceAtYield()
       at its own coupon). The bids are the participants' own rules read at
       the new bond (Demand): the households' share of an issue, the world's
       and the companies' first month of closing the gap to their target in
       it, and the bank's bid at its loan-equivalent yield on its spare
       capital. They are PROSPECTIVE bids, from the same rules the resting
       orders come from, because a bond that does not exist yet has no book.

       BIDS ARE AMOUNTS, AGAINST THE DEAL AS OFFERED. The underwriter
       announces the most the bond may raise (plan()'s bond room) and every
       bidder says how much it would take of THAT at each yield; the issue is
       then sized to what the book will take, and the bids stand. A bid is not
       re-read against the smaller deal: read that way, a rule that takes a
       share of an issue shrinks with it, the book can never fill anything
       without the bank, and a partial bond - "the rest is a bank loan" -
       could not exist (the second measured run: four bonds in 4,000 months).
       Each size is still priced at the leverage of the whole deal.
       ===================================================================== */

    /** What every bidder brings to a new issue by this issuer, read once: the rates, the households' spare savings, each company's till and wealth, the bank's room. */
    final class Bidders {
        final String issuer;
        final double deposit, world, premium, yearOfOutput, faceOutstanding, householdSpare, bankRoom;
        final boolean running, bankBuys;
        final String[] companies = Sectors.KEYS;
        final double[] cash, wealth, othersFace;

        Bidders(String issuer) {
            this.issuer = issuer;
            this.deposit = readings == null ? 0 : Math.max(0, readings.depositRate());
            this.world = readings == null ? 0 : Math.max(0, readings.worldRate());
            this.premium = readings == null ? 0 : Math.max(0, readings.countryPremium());
            this.yearOfOutput = readings == null ? 0 : Math.max(0, readings.monthlyGdp()) * 12;
            this.running = readings != null && readings.worldRunning();
            this.faceOutstanding = totalFace();
            this.householdSpare = households == null ? 0 : households.bondSpare();
            cash = new double[companies.length];
            wealth = new double[companies.length];
            othersFace = new double[companies.length];
            BusinessDebtManager c = credit();
            for (int i = 0; i < companies.length; i++) {
                String k = companies[i];
                if (k.equals(issuer) || economy == null) continue;
                double till = economy.getSectorCash(k);
                if (!(till > 0) || (c != null && c.getPrincipal(k) > 0)) continue;
                cash[i] = till;
                wealth[i] = till + (outward == null ? 0 : outward.localValue(k)) + faceHeldBy(k);
                othersFace[i] = faceOutstanding - principal(k);
            }
            this.bankBuys = bankBuys();
            double room = 0;
            if (bankBuys) {
                double perDollar = bank.capitalPerDollar(Bank.RISK_BUSINESS, issuer);
                room = perDollar > 0 ? Math.max(0, bank.spareCapital()) / perDollar : 0;
            }
            this.bankRoom = room;
        }
    }

    /** Every participant's bid for one new issue of this size, offered at this size, as a function of its coupon. */
    final class Demand {
        final Bidders who;
        final String issuer;
        final double size, offered, el, deposit, world, premium, yearOfOutput, faceOutstanding, householdSpare;
        final String[] companies;
        final double[] cash, wealth, othersFace;
        final double bankYield, bankRoom;
        final boolean running;

        Demand(String issuer, double size, double offered, double dealDebt, double extraAssets) {
            this(new Bidders(issuer), size, offered, dealDebt, extraAssets);
        }

        /**
         * @param size     the bond this prices
         * @param offered  the deal as the underwriter announced it, which every
         *                 bid is an amount of (THE BOOK IS BUILT)
         * @param dealDebt what the bond and the bank's loan beside it add to
         *                 the sector's debt together - the leverage every
         *                 bidder reads, as the loan's price does (bankYield())
         */
        Demand(Bidders who, double size, double offered, double dealDebt, double extraAssets) {
            this.who = who;
            this.issuer = who.issuer;
            this.size = size;
            this.offered = Math.max(size, offered);
            double deal = Math.max(size, dealDebt);
            double pd = defaultRate(issuer, deal, extraAssets);
            // ...at a bond's loss given default (0.7.12, round 2).
            this.el = pd * BusinessDebtManager.BOND_LOSS_GIVEN_DEFAULT;
            this.deposit = who.deposit;
            this.world = who.world;
            this.premium = who.premium;
            this.yearOfOutput = who.yearOfOutput;
            this.running = who.running;
            this.faceOutstanding = who.faceOutstanding;
            this.householdSpare = who.householdSpare;
            this.companies = who.companies;
            this.cash = who.cash;
            this.wealth = who.wealth;
            this.othersFace = who.othersFace;
            this.bankYield = who.bankBuys ? bankYield(issuer, CorporateBond.TERM_MONTHS, size, deal, extraAssets)
                    : Double.POSITIVE_INFINITY;
            this.bankRoom = who.bankRoom;
        }

        /** The households: the city's paper's share of the issue offered at this expected return, out of savings past the cushion. */
        double households(double y) {
            double share = Math.min(HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE,
                    HouseholdBalance.HOUSEHOLD_PAPER_APPETITE * Math.max(0, y - el - deposit));
            return Math.min(householdSpare, share * offered);
        }

        /** The world: a month of closing the gap to hot money's target, the offered issue's share of it. */
        double world(double y) {
            if (running) return 0;
            double spread = Math.min(CapitalFlows.MAX_SPREAD, Math.max(0, y - el - world - premium));
            double target = spread * CapitalFlows.APPETITE * yearOfOutput * offered / (faceOutstanding + offered);
            return Math.min(offered, CapitalFlows.ARRIVAL_SPEED * Math.min(offered, target));
        }

        /** One company: a month of closing the gap to OutwardInvestment's share of its wealth, the offered issue's part of it. */
        double company(int i, double y) {
            if (!(wealth[i] > 0)) return 0;
            double share = Math.min(OutwardInvestment.MAX_SHARE,
                    OutwardInvestment.APPETITE * Math.max(0, y - el - world));
            double target = wealth[i] * share * offered / (othersFace[i] + offered);
            return Math.min(cash[i], OutwardInvestment.OUT_SPEED * Math.min(offered, target));
        }

        /** The bank: all its room at its loan-equivalent yield or over, nothing under. */
        double bank(double y) { return y >= bankYield ? Math.min(offered, bankRoom) : 0; }

        double total(double y) {
            double t = households(y) + world(y) + bank(y);
            for (int i = 0; i < companies.length; i++) t += company(i, y);
            return t;
        }

        /** The yield past which nobody's bid grows: the book's ceiling. */
        double ceiling() {
            double c = el + deposit + HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE / HouseholdBalance.HOUSEHOLD_PAPER_APPETITE;
            c = Math.max(c, el + world + premium + CapitalFlows.MAX_SPREAD);
            c = Math.max(c, el + world + OutwardInvestment.MAX_SHARE / OutwardInvestment.APPETITE);
            if (Double.isFinite(bankYield)) c = Math.max(c, bankYield);
            return c;
        }
    }

    /** The lowest coupon at which the bids fill `face`, or NaN when nothing up to the book's ceiling does. */
    double clearingYield(Demand dm, double face) {
        double hi = dm.ceiling();
        if (!(face > 0) || dm.total(hi) < face * (1 - 1e-12)) return Double.NaN;
        double lo = -1;
        for (int i = 0; i < 100 && hi - lo > 1e-12; i++) {
            double mid = (lo + hi) / 2;
            if (dm.total(mid) >= face * (1 - 1e-12)) hi = mid; else lo = mid;
        }
        return hi;
    }

    /** The coupon a new issue of this face would clear at today, or NaN - for the screens and the harness. The bond alone: no loan beside it. */
    public double quoteCoupon(String issuer, double face, double extraAssets) {
        return clearingYield(new Demand(issuer, face, face, face, extraAssets), face);
    }

    /* =====================================================================
       WHO ISSUES, AND WHEN

       Jerus: "Cheapest, within the bank's limit." Where a sector borrows -
       the investment desk (Game.consider()) and the shortfall desk
       (BusinessDebtManager.coverShortfall(), which also refinances a bond
       or a loan that fell due) - it asks plan() how to finance the amount:

         THE LARGEST BOND THAT IS CHEAPER: of the amount, the most a bond
         can raise whose coupon, with its costs spread over its life, is
         under the bank's loan all-in - the fixed point of "the most the
         bids fill at the coupon that would still be cheaper at that size",
         found from the top. Small amounts go to the bank because the fixed
         costs make small bonds dear; how small is not set anywhere.

         THE BANK FOR THE REST.

         AND NEVER MORE THAN THE BANK WOULD LEND. "Within the bank's limit":
         the bond replaces what the bank would lend - its ceiling, its
         capital rule, a failed bank's nothing - and never adds to it. The
         first version read the limit as the ceiling alone, and let a bond
         raise what a bank short of capital would not lend: on the first
         measured run 1,435 of 2,102 issues, $53B of $71B, were bonds dearer
         than the loan the bank had refused, at coupons up to 16%, and the
         bank failed 16 times against 10. That was a new place to borrow past
         the bank's rules, which the brief did not ask for.

       What the book would have cleared the whole amount at goes with the
       plan, for the advisor's line when the bank was the cheaper.
       ===================================================================== */

    @Override
    public BusinessDebtManager.Plan plan(String sector, double amount, double loanRate, double loanRoom,
                                         double bondRoom, double extraAssets, int month) {
        if (!(amount > 0)) return null;
        double loanAllIn = loanAllIn(loanRate);
        loanRoom = Math.max(0, Math.min(amount, loanRoom));
        /*
         * WITHIN THE BANK'S LIMIT: the bond may raise only what the bank
         * would lend - and never past its own ceiling, the shortfall desk's
         * for a short month and the default point after the deal for a
         * building (the bank's room may reach past the shortfall ceiling by
         * the interest reserve, which is the bank's alone). The two together
         * raise what the bank alone would have.
         */
        bondRoom = Math.max(0, Math.min(loanRoom, bondRoom));
        if (readings == null || !(bondRoom > 0)) {
            return BusinessDebtManager.Plan.bankOnly(sector, amount, loanRate, loanAllIn, Double.NaN);
        }
        Bidders who = new Bidders(sector);
        // What the bond and the loan will add to the debt together: every size is priced at it.
        double deal = loanRoom;
        Demand whole = new Demand(who, bondRoom, bondRoom, deal, extraAssets);
        double clearing = clearingYield(whole, bondRoom);

        // The largest bond cheaper than the bank: from the top, the most the
        // bids fill at the coupon that would still be cheaper at that size.
        double s = bondRoom;
        double bond = 0, threshold = Double.NaN;
        Demand at = null;
        for (int i = 0; i < 40 && s > 0; i++) {
            Demand dm = i == 0 ? whole : new Demand(who, s, bondRoom, deal, extraAssets);
            double t = loanAllIn - issueCost(s) / s / TERM_YEARS;
            double filled = Math.min(bondRoom, dm.total(t));
            if (filled >= s * (1 - 1e-9)) { bond = s; threshold = t; at = dm; break; }
            if (!(filled > 0)) break;
            s = filled;
        }
        double coupon = Double.NaN;
        if (bond > 0) {
            /*
             * The coupon is the lowest yield that fills it, which is at most
             * the threshold it filled at. THE LARGEST CHEAPER BOND IS THE
             * LIMIT OF THE CHEAPER ONES: where the last bidder it needs is
             * still coming in at the threshold, its last unit costs what the
             * loan does and the whole issue exactly what the loan would -
             * NO DEARER, and taken, a tie going to the bond (a billionth of
             * a point's arithmetic allowed). Measured on BondCheck's city:
             * read strictly, "cheaper" refused every such bond on a rounding
             * error, and with it every issue whose marginal buyer was the world.
             */
            coupon = Math.min(clearingYield(at, bond), threshold);
            // A bond that raises less than it costs is not a bond anybody brings,
            // nor one dearer than the loan.
            if (Double.isNaN(coupon) || issueCost(bond) >= bond || allIn(bond, coupon) > loanAllIn + CHEAPER_BY) bond = 0;
        }
        if (!(bond > 0)) return BusinessDebtManager.Plan.bankOnly(sector, amount, loanRate, loanAllIn, clearing);
        double loan = Math.min(amount, loanRoom) - bond;
        return new BusinessDebtManager.Plan(sector, amount, bond, coupon, allIn(bond, coupon),
                Math.max(0, loan), loanRate, loanAllIn, issueCost(bond), clearing, extraAssets, deal, bondRoom);
    }

    /**
     * SELLS A PLAN'S BOND, at its coupon, at par: every participant takes
     * its bid at that coupon, pro rata if the bids more than fill it, and
     * pays; the bank, as underwriter, is paid the costs out of the proceeds.
     * What is left is the issuer's, which the caller hands it.
     *
     * @return the proceeds: the face sold less its costs
     */
    @Override
    public double issue(BusinessDebtManager.Plan plan, int month) {
        if (plan == null || !plan.hasBond()) return 0;
        double face = plan.bondFace();
        double coupon = plan.coupon();
        Demand dm = new Demand(plan.sector(), face, plan.offered(), plan.dealDebt(), plan.extraAssets());
        double[] take = new double[dm.companies.length];
        double hh = dm.households(coupon), w = dm.world(coupon), bk = dm.bank(coupon), total = hh + w + bk;
        for (int i = 0; i < take.length; i++) { take[i] = dm.company(i, coupon); total += take[i]; }
        if (!(total > 0)) return 0;
        double scale = Math.min(1, face / total);
        double sold = Math.min(face, total);
        CorporateBond b = new CorporateBond(nextId++, plan.sector(), sold, coupon, month, CorporateBond.TERM_MONTHS);
        // Each takes its part, and pays for it at par.
        double hhTake = hh * scale;
        if (hhTake > 0 && households != null) {
            // ...each eligible cell its own part, out of its own savings (round 2).
            double paid = households.payForBonds(b.id, hhTake, hhTake);
            b.households += paid;
            householdsBought += paid;
        }
        double wTake = w * scale;
        if (wTake > 0) {
            b.world += wTake;
            worldBought += wTake;
            lifeWorldBought += wTake;
        }
        double bkTake = bk * scale;
        if (bkTake > 0 && bank != null) {
            bank.buyBond(bkTake, bkTake * Bank.RISK_BUSINESS * Bank.maturityWeight(CorporateBond.TERM_MONTHS));
            b.bank += bkTake;
            b.bankCost += bkTake;
        }
        for (int i = 0; i < take.length; i++) {
            double c = take[i] * scale;
            if (!(c > 0) || economy == null) continue;
            String k = dm.companies[i];
            economy.setSectorCash(k, economy.getSectorCash(k) - c);
            b.companies.merge(k, c, Double::sum);
            boughtBySector.merge(k, c, Double::sum);
        }
        // What was actually sold: the four parts, to the dust.
        b.face = b.households + b.bank + b.world + b.companiesTotal();
        b.issued = b.face;
        if (!(b.face > 0)) return 0;
        bonds.add(b);
        openBook(b);
        double costs = issueCost(b.face);
        if (bank != null) bank.takeUnderwriting(costs);
        issuedFace += b.face;
        issuedCosts += costs;
        issues++;
        lifeIssued += b.face;
        lifeCosts += costs;
        lifeIssues++;
        issuedBySector.merge(b.issuer, b.face, Double::sum);
        proceedsBySector.merge(b.issuer, b.face - costs, Double::sum);
        lastIssue = b.issuer;
        lastIssueFace = b.face;
        lastIssueCoupon = coupon;
        lastIssueLoanRate = plan.loanRate();
        lastIssueMonth = month;
        return b.face - costs;
    }

    /** The last issue, for the Bonds page: who, how much, at what, against the bank's rate, when. */
    private String lastIssue;
    private double lastIssueFace, lastIssueCoupon, lastIssueLoanRate;
    private int lastIssueMonth = -1;

    public String getLastIssuer()       { return lastIssue; }
    public double getLastIssueFace()    { return lastIssueFace; }
    public double getLastIssueCoupon()  { return lastIssueCoupon; }
    public double getLastIssueLoanRate(){ return lastIssueLoanRate; }
    public int getLastIssueMonth()      { return lastIssueMonth; }

    /* =====================================================================
       THE COUPONS AND THE PRINCIPAL

       STRUCK AT THE TOP OF THE MONTH, with the interest bill every issuer's
       statement pays (strikeCoupons(), beside EconomyManager
       .updateBusinessCredit(), on the same face): a twelfth of each coupon,
       split by who holds the bond then - the record date. The bank's part
       reaches it with its loans' interest at the settle (Game); the rest is
       paid at the market's step (takeMonth()): into each cell's savings and
       its month's investment income, on what the cell held at the record
       date (round 2); into each company's till; and abroad, an income
       outflow.

       THE FACE AT MATURITY is paid from the issuer's till to the holders, at
       the credit settle (redeemMaturing()), before the shortfall desk looks
       - so what the issuer could not pay is a short month: it sells what it
       holds, the desk lends or a new bond raises the rest, and what nobody
       lends defaults that month (BusinessDebtManager, CAN'T PAY MEANS
       DEFAULT, since round 4).
       ===================================================================== */

    /** Strikes this month's coupons by who holds each bond now: the record date. */
    public void strikeCoupons() {
        dueHouseholds = dueBank = dueWorld = 0;
        dueCompanies.clear();
        dueByIssuer.clear();
        dueCells.clear();
        // The cells' part at the record date, cell by cell (round 2): what
        // each holds of each bond now.
        if (households != null) {
            Map<Integer, CorporateBond> byId = new java.util.HashMap<>();
            for (CorporateBond b : bonds) byId.put(b.id, b);
            for (Household cell : households.cellsForMarket()) {
                if (cell.bondFace.isEmpty() || cell.households() <= 0) continue;
                double due = 0;
                for (Map.Entry<Integer, Double> e : cell.bondFace.entrySet()) {
                    CorporateBond b = byId.get(e.getKey());
                    if (b == null || !(b.face > 0)) continue;
                    due += e.getValue() * cell.households() * b.coupon / CorporateBond.COUPONS_A_YEAR;
                }
                if (due > 0) dueCells.merge(cell.key(), due, Double::sum);
            }
        }
        for (CorporateBond b : bonds) {
            double c = b.coupon / CorporateBond.COUPONS_A_YEAR;
            if (!(b.face > 0) || !(c > 0)) continue;
            dueHouseholds += b.households * c;
            dueBank += b.bank * c;
            dueWorld += b.world * c;
            for (Map.Entry<String, Double> e : b.companies.entrySet()) {
                if (e.getValue() > 0) dueCompanies.merge(e.getKey(), e.getValue() * c, Double::sum);
            }
            dueByIssuer.merge(b.issuer, b.face * c, Double::sum);
        }
    }

    /** Every coupon struck this month, all holders: what the issuers' statements paid. */
    public double getCouponsStruck() {
        double t = 0;
        for (double v : dueByIssuer.values()) t += v;
        return t;
    }

    /** The bank's part of this month's coupons, paid it at the settle with its loans' interest. */
    public double getCouponsDueToBank() { return dueBank; }

    /**
     * PAYS EVERY BOND THAT FALLS DUE THIS MONTH: its face out of the issuer's
     * till - which may go short, for the credit settle to cover (see THE
     * COUPONS AND THE PRINCIPAL) - to each holder: every cell its own face,
     * into its savings (round 2), the bank (its carrying value off its book,
     * the difference a gain or a loss), each company's till, and abroad.
     * Then the bond and its book go.
     */
    public void redeemMaturing(int month) {
        java.util.Iterator<CorporateBond> it = bonds.iterator();
        while (it.hasNext()) {
            CorporateBond b = it.next();
            if (!b.isMatured(month)) continue;
            if (economy != null) economy.setSectorCash(b.issuer, economy.getSectorCash(b.issuer) - b.face);
            repaidBySector.merge(b.issuer, b.face, Double::sum);
            if (b.households > 0) {
                // Each cell its own face (round 2).
                if (households != null) households.creditBondPrincipal(b.id);
                principalToHouseholds += b.households;
            }
            if (b.bank > 0) {
                if (bank != null) bank.redeemBond(b.bank, b.bankCost);
                principalToBank += b.bank;
            }
            for (Map.Entry<String, Double> e : b.companies.entrySet()) {
                double v = e.getValue();
                if (!(v > 0)) continue;
                if (economy != null) economy.setSectorCash(e.getKey(), economy.getSectorCash(e.getKey()) + v);
                boughtBySector.merge(e.getKey(), -v, Double::sum);
                principalToCompanies += v;
            }
            if (b.world > 0) principalAbroad += b.world;
            OrderBook book = books.remove(b.instrument());
            if (book != null) { book.withdrawAll(); noteBook(book); }
            it.remove();
        }
    }

    /** Pays the month's coupons struck at the top to everybody but the bank. */
    private void payCoupons() {
        if (dueHouseholds > 0) {
            // To the cells that held them at the record date (round 2).
            if (households != null) households.creditBondCoupons(dueCells);
            couponsToHouseholds += dueHouseholds;
            lifeCouponsHouseholds += dueHouseholds;
        }
        dueCells.clear();
        for (Map.Entry<String, Double> e : dueCompanies.entrySet()) {
            double v = e.getValue();
            if (!(v > 0) || economy == null) continue;
            economy.setSectorCash(e.getKey(), economy.getSectorCash(e.getKey()) + v);
            couponsBySector.merge(e.getKey(), v, Double::sum);
            couponsToCompanies += v;
            lifeCouponsCompanies += v;
        }
        if (dueWorld > 0) {
            couponsAbroad += dueWorld;
            lifeCouponsAbroad += dueWorld;
        }
        couponsToBank += dueBank;
        lifeCouponsBank += dueBank;
        dueHouseholds = dueBank = dueWorld = 0;
        dueCompanies.clear();
    }

    /* =====================================================================
       THE ORDERS ARE GOOD FOR A MONTH

       At the market's step every order still resting is withdrawn - those
       that waited are counted - and every participant posts again from its
       view of the month: the curve, each issuer's leverage and mix, its own
       cash and its own holdings. A limit that stood for longer would be a
       price struck on a curve and a leverage that have since moved, which is
       a free option to whoever sees it first - the reason a real
       participant cancels and replaces as the market moves. Between two
       steps the book stands, and a household short of money in the
       waterfall sells into what rests there.

       THE ORDER OF POSTING is the bank, the world, the companies, the
       households - each sweep over every bond, oldest first - so an order
       that crosses what an earlier participant posted trades at that
       resting price. Fixed, so a run reproduces.
       ===================================================================== */

    /** Opens the month's flows. The books and the bonds stand. */
    public void startMonth() {
        householdsBought = householdsSold = householdsBoughtAbroad = householdsSoldAbroad = 0;
        worldBought = worldSold = 0;
        couponsToHouseholds = couponsToBank = couponsToCompanies = couponsAbroad = 0;
        principalToHouseholds = principalToBank = principalToCompanies = principalAbroad = 0;
        lifeLossHouseholds += lossHouseholds;
        lifeLossBank += lossBank;
        lifeLossCompanies += lossCompanies;
        lifeLossWorld += lossWorld;
        lossHouseholds = lossBank = lossCompanies = lossWorld = 0;
        bankLossCost = 0;
        issuedFace = issuedCosts = 0;
        issues = 0;
        emigrantsFace = 0;
        betweenHouseholds = 0;
        betweenHouseholdsTrades = 0;
        issuedBySector.clear();
        proceedsBySector.clear();
        repaidBySector.clear();
        boughtBySector.clear();
        couponsBySector.clear();
        bankLossBySector.clear();
        bankLossTaken.clear();
        companiesSoldShort = 0;
    }

    /**
     * THE MARKET'S MONTH, after the shares have traded and before the
     * sectors move their money abroad: the coupons paid, last month's orders
     * withdrawn, every bond valued, and every participant's orders posted.
     */
    public void takeMonth(int month) {
        payCoupons();
        lastPostedBuy = lastPostedSell = lastFilled = lastSellQuantityWaited = 0;
        lastSellsPosted = lastSellsWaited = lastTrades = 0;
        for (OrderBook book : books.values()) {
            book.withdrawAll();
            noteBook(book);
            book.startMonth();
        }
        // Gone whole: a bond a backstop wrote to nothing.
        bonds.removeIf(b -> {
            if (b.face > dust) return false;
            OrderBook book = books.remove(b.instrument());
            if (book != null) book.clear();
            return true;
        });
        if (readings == null) return;
        Map<String, Double> el = new LinkedHashMap<>();
        double[] value = new double[bonds.size()];
        double[] yield = new double[bonds.size()];
        for (int i = 0; i < bonds.size(); i++) {
            CorporateBond b = bonds.get(i);
            double loss = el.computeIfAbsent(b.issuer, this::expectedLoss);
            yield[i] = readings.curve(Math.max(1, b.remainingMonths(month))) + loss;
            value[i] = b.priceAt(yield[i], month);
        }
        if (households != null) {
            double face = 0, worth = 0;
            for (int i = 0; i < bonds.size(); i++) { face += bonds.get(i).households; worth += bonds.get(i).households * value[i]; }
            households.setBondRatio(face > 0 ? worth / face : 1);
        }
        postBank(month, el, value);
        postWorld(month, el);
        postCompanies(month, el);
        postHouseholds(month, el);
    }

    /** Adds a book's month, as it closed, to the market's record. */
    private void noteBook(OrderBook book) {
        lastPostedBuy += book.postedBuy();
        lastPostedSell += book.postedSell();
        lastFilled += book.filledSell();
        lastSellsPosted += book.sellsPosted();
        lastSellsWaited += book.sellsWaited();
        lastSellQuantityWaited += book.sellQuantityWaited();
        lastTrades += book.trades();
        lifeVolume += book.volume();
        lifePostedSell += book.postedSell();
        lifeFilledSell += book.filledSell();
        lifeSellsPosted += book.sellsPosted();
        lifeSellsWaited += book.sellsWaited();
    }

    /** The bank: over its target, a bid at its loan-equivalent price on its spare capital; under it, an ask at the bond's value for what takes it back. */
    private void postBank(int month, Map<String, Double> el, double[] value) {
        if (bank == null || bank.getBranches() <= 0) return;
        if (bankBuys()) {
            double spare = Math.max(0, bank.spareCapital());
            for (CorporateBond b : bonds) {
                if (!(spare > 0)) break;
                int n = b.remainingMonths(month);
                if (n <= 0) continue;
                double perDollar = bank.capitalPerDollar(Bank.RISK_BUSINESS, b.issuer);
                if (!(perDollar > 0)) continue;
                double p = b.priceAt(bankYield(b.issuer, n, 0, 0, 0), month);
                double q = Math.min(b.face - b.bank, spare / perDollar);
                if (!(q > dust) || !(p > 0)) continue;
                submit(b, BANK, OrderBook.Side.BUY, p, q, month);
                spare -= q * perDollar;
            }
        } else if (bank.equity() < bank.targetEquity()) {
            double short_ = bank.targetEquity() - Math.max(0, bank.equity());
            double perDollar = bank.capitalTarget() * Bank.RISK_BUSINESS;
            double faceToSell = perDollar > 0 ? short_ / perDollar : 0;
            double held = faceHeldByBank();
            if (!(held > 0) || !(faceToSell > 0)) return;
            double share = Math.min(1, faceToSell / held);
            for (int i = 0; i < bonds.size(); i++) {
                CorporateBond b = bonds.get(i);
                if (b.bank > dust) submit(b, BANK, OrderBook.Side.SELL, value[i], b.bank * share, month);
            }
        }
    }

    /** The world: hot money's target in each bond, its share of the stock the spread calls for; a bid for a month of the gap, an ask for the excess. */
    private void postWorld(int month, Map<String, Double> el) {
        double year = Math.max(0, readings.monthlyGdp()) * 12;
        double all = totalFace();
        if (!(year > 0) || !(all > 0)) return;
        double hurdle = Math.max(0, readings.worldRate()) + Math.max(0, readings.countryPremium());
        boolean running = readings.worldRunning();
        for (CorporateBond b : bonds) {
            int n = b.remainingMonths(month);
            if (n <= 0 || !(b.face > 0)) continue;
            double loss = el.get(b.issuer);
            double y = readings.curve(Math.max(1, n)) + loss;
            double per = CapitalFlows.APPETITE * year * b.face / all;   // holding a unit of spread buys
            double spread = Math.min(CapitalFlows.MAX_SPREAD, Math.max(0, y - loss - hurdle));
            double target = running ? 0 : Math.min(b.face, spread * per);
            double held = b.world;
            if (target > held) {
                double q = CapitalFlows.ARRIVAL_SPEED * (target - held);
                double after = Math.min(CapitalFlows.MAX_SPREAD, (held + q) / per);
                submit(b, WORLD, OrderBook.Side.BUY, b.priceAt(hurdle + loss + after, month), q, month);
            } else if (held > target) {
                double q = running ? held * CapitalFlows.PANIC_EXIT : CapitalFlows.DEPARTURE_SPEED * (held - target);
                double after = Math.min(CapitalFlows.MAX_SPREAD, Math.max(0, held - q) / per);
                submit(b, WORLD, OrderBook.Side.SELL, b.priceAt(hurdle + loss + after, month), q, month);
            }
        }
    }

    /** The companies: OutwardInvestment's share of each owing-nothing company's wealth, spread over the other sectors' bonds by face; a bid for OUT_SPEED of the gap, an ask for HOME_SPEED of the excess. */
    private void postCompanies(int month, Map<String, Double> el) {
        if (economy == null) return;
        BusinessDebtManager c = credit();
        double all = totalFace();
        double w = Math.max(0, readings.worldRate());
        for (String k : Sectors.KEYS) {
            double till = economy.getSectorCash(k);
            boolean lends = till > 0 && (c == null || !(c.getPrincipal(k) > 0));
            double held = faceHeldBy(k);
            if (!lends && !(held > 0)) continue;
            double wealth = Math.max(0, till) + (outward == null ? 0 : outward.localValue(k)) + held;
            double others = all - principal(k);
            double[] q = new double[bonds.size()];
            double[] p = new double[bonds.size()];
            double spend = 0;
            for (int i = 0; i < bonds.size(); i++) {
                CorporateBond b = bonds.get(i);
                int n = b.remainingMonths(month);
                if (n <= 0 || b.issuer.equals(k) || !(b.face > 0) || !(others > 0)) continue;
                double loss = el.get(b.issuer);
                double y = readings.curve(Math.max(1, n)) + loss;
                double per = OutwardInvestment.APPETITE * wealth * b.face / others;
                double share = Math.min(OutwardInvestment.MAX_SHARE, OutwardInvestment.APPETITE * Math.max(0, y - loss - w));
                double target = lends ? Math.min(b.face, wealth * share * b.face / others) : 0;
                double mine = b.company(k);
                if (target > mine && per > 0) {
                    q[i] = OutwardInvestment.OUT_SPEED * (target - mine);
                    double after = Math.min(OutwardInvestment.MAX_SHARE / OutwardInvestment.APPETITE, (mine + q[i]) / per);
                    p[i] = b.priceAt(w + loss + after, month);
                    spend += q[i] * p[i];
                } else if (mine > target) {
                    double sell = OutwardInvestment.HOME_SPEED * (mine - target);
                    double after = per > 0 ? Math.min(OutwardInvestment.MAX_SHARE / OutwardInvestment.APPETITE, Math.max(0, mine - sell) / per) : 0;
                    submit(b, k, OrderBook.Side.SELL, b.priceAt(w + loss + after, month), sell, month);
                }
            }
            // Never more than the till: every bid scaled to it together.
            double scale = spend > till ? Math.max(0, till) / spend : 1;
            for (int i = 0; i < bonds.size(); i++) {
                if (q[i] > 0 && scale > 0) submit(bonds.get(i), k, OrderBook.Side.BUY, p[i], q[i] * scale, month);
            }
        }
    }

    /**
     * THE HOUSEHOLDS, CELL BY CELL (0.7.12 round 2): round 1's rule for the
     * pool, applied to each cell's own savings and holdings. The households
     * want the city's paper's share of each issue at its expected return over
     * the deposit rate; each cell wants its part of that, in proportion to
     * what it has to put into bonds - its savings past the cushion and what
     * it already holds in them - against every cell's. It bids for
     * OUT_SPEED of its gap, at the price its own slice of the demand curve
     * puts on it, out of its own savings past the cushion, and asks for
     * HOME_SPEED of its excess. So a cell whose savings have grown bids, and
     * one whose savings have gone asks - and when one rests and the other
     * arrives, they trade with each other. No number of its own: every dial
     * is round 1's, and the weight is the cell's money.
     */
    private void postHouseholds(int month, Map<String, Double> el) {
        if (households == null) return;
        double d = Math.max(0, readings.depositRate());
        Household[] cells = households.cellsForMarket();
        double[] weight = new double[cells.length];
        double all = 0;
        for (int i = 0; i < cells.length; i++) {
            Household c = cells[i];
            if (c.isEmpty()) continue;
            weight[i] = households.bondSpare(c) + Math.max(0, c.bonds) * c.households();
            all += weight[i];
        }
        if (!(all > 0)) return;
        int nb = bonds.size();
        double[] target = new double[nb], per = new double[nb], floor = new double[nb];
        for (int j = 0; j < nb; j++) {
            CorporateBond b = bonds.get(j);
            int n = b.remainingMonths(month);
            if (n <= 0 || !(b.face > 0)) continue;
            double loss = el.get(b.issuer);
            double y = readings.curve(Math.max(1, n)) + loss;
            per[j] = HouseholdBalance.HOUSEHOLD_PAPER_APPETITE * b.face;
            double share = Math.min(HouseholdBalance.MAX_HOUSEHOLD_PAPER_SHARE,
                    HouseholdBalance.HOUSEHOLD_PAPER_APPETITE * Math.max(0, y - loss - d));
            target[j] = share * b.face;
            floor[j] = d + loss;
        }
        double[] q = new double[nb], p = new double[nb];
        for (int i = 0; i < cells.length; i++) {
            double w = weight[i] / all;
            if (!(w > 0)) continue;
            Household c = cells[i];
            String who = CELL + c.key();
            double spare = households.bondSpare(c);
            double spend = 0;
            java.util.Arrays.fill(q, 0);
            for (int j = 0; j < nb; j++) {
                if (!(per[j] > 0)) continue;
                CorporateBond b = bonds.get(j);
                double perC = per[j] * w;
                double mine = c.bondFace(b.id) * c.households();
                double tgt = target[j] * w;
                if (tgt > mine && spare > 0) {
                    q[j] = OutwardInvestment.OUT_SPEED * (tgt - mine);
                    double after = (mine + q[j]) / perC;
                    p[j] = b.priceAt(floor[j] + after, month);
                    spend += q[j] * p[j];
                } else if (mine > tgt) {
                    double sell = OutwardInvestment.HOME_SPEED * (mine - tgt);
                    double after = Math.max(0, mine - sell) / perC;
                    submit(b, who, OrderBook.Side.SELL, b.priceAt(floor[j] + after, month), sell, month);
                }
            }
            double scale = spend > spare ? Math.max(0, spare) / spend : 1;
            if (!(scale > 0)) continue;
            posting = c;
            postingBudget = spare;
            try {
                for (int j = 0; j < nb; j++) {
                    if (q[j] > 0) submit(bonds.get(j), who, OrderBook.Side.BUY, p[j], q[j] * scale, month);
                }
            } finally {
                posting = null;
            }
        }
    }

    /** The cell posting its bids at the step right now, and what it may still spend this month. */
    private Household posting;
    private double postingBudget;

    /* =====================================================================
       A HOUSEHOLD SHORT OF MONEY SELLS

       In the waterfall (Household.settle()), after the city's paper and the
       dollars abroad, before the shares: the cell asks, in each bond it
       holds, the price at which the buyer's yield would be its own
       borrowing rate - selling is worth it only while it is cheaper than
       the credit it would otherwise draw - for no more than it is short.
       What rests at that price or better fills now, at the resting price;
       the rest waits on the book until the market's step withdraws it, and
       the household borrows. A seller with no buyer waits.
       ===================================================================== */

    /** @return cash raised now, per household of the cell */
    double sellForCell(Household cell, double needPer, double borrowingRate) {
        if (households == null || cell == null || !(needPer > 0) || !(cell.bonds > 0) || cell.households() <= 0) return 0;
        int month = readings == null ? 0 : readings.month();
        String who = CELL + cell.key();
        double need = needPer * cell.households();
        selling = cell;
        raised = 0;
        try {
            for (CorporateBond b : new ArrayList<>(bonds)) {
                if (need - raised <= 0) break;
                int n = b.remainingMonths(month);
                if (n <= 0) continue;
                // Its own face of it (round 2): no claim on a pool to read.
                double mine = cell.bondFace(b.id) * cell.households();
                double ask = b.priceAt(Math.max(0, borrowingRate), month);
                if (!(ask > 0) || !(mine > dust)) continue;
                double q = Math.min(mine, (need - raised) / ask);
                submit(b, who, OrderBook.Side.SELL, ask, q, month);
            }
        } finally {
            selling = null;
        }
        /*
         * Sized at its own ask, filled at the bids' prices, which are higher:
         * what it raised past what it was short is banked, not spent - the
         * waterfall takes what it needs and no more.
         */
        if (raised > need) {
            cell.savings += (raised - need) / cell.households();
            raised = need;
        }
        return raised / cell.households();
    }

    /** The cell selling in the waterfall right now, whose proceeds go back to its waterfall rather than into its savings. */
    private Household selling;
    private double raised;

    /**
     * A COMPANY SHORT OF MONEY SELLS (0.7.12 round 4, CAN'T PAY MEANS DEFAULT -
     * see BusinessDebtManager), by the households' rule above: in each other
     * sector's bond it holds, it asks the price at which the buyer's yield
     * would be its own borrowing rate, for no more than it is short; what
     * rests at that price or better fills now, into its till, and the rest
     * waits on the book until the market's step withdraws it. Companies had
     * no such sale until this round - they asked HOME_SPEED of what they held
     * over their target at the step, and borrowed or ran short meanwhile.
     *
     * @param need          what it is short, in money
     * @param borrowingRate what new borrowing costs it (BusinessDebtManager.getRate())
     * @return cash raised now
     */
    public double sellForCompany(String sector, double need, double borrowingRate) {
        if (economy == null || sector == null || !(need > 0)) return 0;
        int month = readings == null ? 0 : readings.month();
        double before = economy.getSectorCash(sector);
        for (CorporateBond b : new ArrayList<>(bonds)) {
            double got = economy.getSectorCash(sector) - before;
            if (need - got <= 0) break;
            int n = b.remainingMonths(month);
            if (n <= 0) continue;
            double mine = b.company(sector);
            double ask = b.priceAt(Math.max(0, borrowingRate), month);
            if (!(ask > 0) || !(mine > dust)) continue;
            submit(b, sector, OrderBook.Side.SELL, ask, Math.min(mine, (need - got) / ask), month);
        }
        double got = Math.max(0, economy.getSectorCash(sector) - before);
        companiesSoldShort += got;
        return got;
    }

    /** What companies short of money raised selling their bonds this month, from the credit settle (round 4); a month's figure, cleared with the market's month. */
    private double companiesSoldShort;
    public double getCompaniesSoldShort()     { return companiesSoldShort; }

    /* =========================== one order, settled =========================== */

    private void submit(CorporateBond b, String who, OrderBook.Side side, double price, double quantity, int month) {
        if (!(quantity > dust) || !(price > 0) || !Double.isFinite(price)) return;
        openBook(b).submit(who, side, price, quantity, month, new Settle(b));
    }

    /** Where each participant's money and holding is, for one bond's book. */
    private final class Settle implements OrderBook.Clearing {
        final CorporateBond b;
        Settle(CorporateBond b) { this.b = b; }

        @Override public double capacity(String who, OrderBook.Side side, double price) {
            if (side == OrderBook.Side.BUY) {
                if (WORLD.equals(who)) return Double.POSITIVE_INFINITY;
                if (BANK.equals(who)) return bankBuys() ? Double.POSITIVE_INFINITY : 0;
                if (who.startsWith(CELL)) {
                    Household c = cellOf(who);
                    if (c == null) return 0;
                    double cash = households.bondSpare(c);
                    if (c == posting) cash = Math.min(cash, postingBudget);
                    return Math.max(0, cash) / price;
                }
                return economy == null ? 0 : Math.max(0, economy.getSectorCash(who)) / price;
            }
            if (WORLD.equals(who)) return b.world;
            if (BANK.equals(who)) return b.bank;
            if (who.startsWith(CELL)) {
                Household c = cellOf(who);
                return c == null ? 0 : Math.min(b.households, c.bondFace(b.id) * c.households());
            }
            return b.company(who);
        }

        @Override public void settle(String buyer, String seller, double q, double price) {
            double cash = q * price;
            boolean hhBuys = buyer.startsWith(CELL);
            boolean hhSells = seller.startsWith(CELL);
            boolean worldBuys = WORLD.equals(buyer), worldSells = WORLD.equals(seller);

            /* ---- the seller gives up the face and is paid ---- */
            if (hhSells) {
                Household c = cellOf(seller);
                b.households -= q;
                c.setBondFace(b.id, Math.max(0, c.bondFace(b.id) - q / c.households()));
                if (c == selling) {
                    raised += cash;
                    c.bondsSold += cash / c.households();
                } else {
                    c.savings += cash / c.households();
                    c.bondsSold += cash / c.households();
                }
            } else if (worldSells) {
                b.world -= q;
            } else if (BANK.equals(seller)) {
                double basis = b.bank > 0 ? b.bankCost * Math.min(1, q / b.bank) : 0;
                b.bank -= q;
                b.bankCost -= basis;
                bank.sellBond(cash, basis);
            } else {
                b.companies.merge(seller, -q, Double::sum);
                economy.setSectorCash(seller, economy.getSectorCash(seller) + cash);
                boughtBySector.merge(seller, -cash, Double::sum);
            }

            /* ---- the buyer pays and takes it ---- */
            if (hhBuys) {
                Household c = cellOf(buyer);
                b.households += q;
                c.setBondFace(b.id, c.bondFace(b.id) + q / c.households());
                c.savings -= cash / c.households();
                if (c == posting) postingBudget -= cash;
            } else if (worldBuys) {
                b.world += q;
            } else if (BANK.equals(buyer)) {
                b.bank += q;
                b.bankCost += cash;
                int left = b.remainingMonths(readings == null ? b.issueMonth : readings.month());
                bank.buyBond(cash, cash * Bank.RISK_BUSINESS * Bank.maturityWeight(left));
            } else {
                b.companies.merge(buyer, q, Double::sum);
                economy.setSectorCash(buyer, economy.getSectorCash(buyer) - cash);
                boughtBySector.merge(buyer, cash, Double::sum);
            }

            /* ---- and what crossed the pools' edge, for the audit ---- */
            boolean poolBuys = !hhBuys && !worldBuys;
            boolean poolSells = !hhSells && !worldSells;
            if (hhSells && poolBuys) householdsSold += cash;
            if (hhSells && worldBuys) { householdsSoldAbroad += cash; lifeWorldBought += cash; }
            if (poolSells && hhBuys) householdsBought += cash;
            if (worldSells && hhBuys) { householdsBoughtAbroad += cash; lifeWorldSold += cash; }
            if (poolSells && worldBuys) { worldBought += cash; lifeWorldBought += cash; }
            if (worldSells && poolBuys) { worldSold += cash; lifeWorldSold += cash; }
            // ...and a cell selling to a cell, a transfer inside the households
            // that no pool sees (round 2).
            if (hhSells && hhBuys) {
                betweenHouseholds += cash;
                betweenHouseholdsTrades++;
                lifeBetweenHouseholds += cash;
                lifeBetweenHouseholdsTrades++;
            }
        }
    }

    /** The cell a participant's name is, or null. */
    private Household cellOf(String who) {
        return households == null ? null : households.cellByKey(who.substring(CELL.length()));
    }

    /* =========================== the people who leave =========================== */

    /**
     * WHAT A HOUSEHOLD THAT LEAVES TAKES WITH IT: its bonds, which it holds
     * from abroad from then on - as a leaver's shares are held abroad
     * (Equity.followEmigrants()) - taken off the households' face of the bond
     * and put on the world's, bond by bond (round 2: the census moves each
     * bond as a stock, HouseholdBalance.stocks()). No cash moves.
     */
    public void householdLeft(int id, double face) {
        CorporateBond b = bond(id);
        if (b == null || !(face > 0)) return;
        double moved = Math.min(face, Math.max(0, b.households));
        b.households -= moved;
        b.world += moved;
        emigrantsFace += moved;
    }

    /**
     * Every bond's households' face, checked against the cells that hold it
     * after a load, which restores the two separately (round 2). The cells
     * are what the households own and the bond's figure is their sum; within
     * the sum's rounding the saved figure stands, so a reloaded city replays
     * the one that saved. Past it - a cell this build does not have, whose
     * figures the load drops as it drops every cell array's - the bond's
     * households' face becomes the cells' and its face its holders', and
     * what the dropped cell held goes with it, off the issuer's debt.
     */
    public void recountHouseholds() {
        if (households == null) return;
        for (CorporateBond b : bonds) {
            double held = households.bondFaceHeld(b.id);
            if (Math.abs(held - b.households) <= 1e-9 * Math.max(1, Math.abs(b.households))) continue;
            b.households = held;
            b.face = b.households + b.bank + b.world + b.companiesTotal();
        }
    }

    /** The households' face in each bond, by its id: what an old pooled save hands the cells (HouseholdBalance.claimPooledBonds()). */
    public Map<Integer, Double> householdsFaceById() {
        Map<Integer, Double> out = new LinkedHashMap<>();
        for (CorporateBond b : bonds) if (b.households > 0) out.put(b.id, b.households);
        return out;
    }

    /* ================================ reading ================================ */

    /** The households' cash into the pools for bonds this month: at issue and from the bank and the companies. For MoneyAudit. */
    public double getHouseholdsBought()        { return householdsBought; }
    /** ...and out of the pools to them, for bonds they sold to the bank and the companies. */
    public double getHouseholdsSold()          { return householdsSold; }
    /** What the households paid the world for bonds, and the world paid them: both outside the pools, declared as pairs. */
    public double getHouseholdsBoughtAbroad()  { return householdsBoughtAbroad; }
    public double getHouseholdsSoldAbroad()    { return householdsSoldAbroad; }
    /** The world's money into the pools for bonds - at issue and from the bank and the companies - and out of them for bonds it sold them. */
    public double getWorldBought()             { return worldBought; }
    public double getWorldSold()               { return worldSold; }
    /** Every bond purchase the world made this month, whoever sold, and every sale. */
    public double getWorldPurchases()          { return worldBought + householdsSoldAbroad; }
    public double getWorldSales()              { return worldSold + householdsBoughtAbroad; }
    public double getCouponsToHouseholds()     { return couponsToHouseholds; }
    public double getCouponsToBank()           { return couponsToBank; }
    public double getCouponsToCompanies()      { return couponsToCompanies; }
    public double getCouponsAbroad()           { return couponsAbroad; }
    public double getPrincipalToHouseholds()   { return principalToHouseholds; }
    public double getPrincipalToBank()         { return principalToBank; }
    public double getPrincipalToCompanies()    { return principalToCompanies; }
    public double getPrincipalAbroad()         { return principalAbroad; }
    /** Face written off this month's defaults, by holder. The world's is declared across the border. */
    public double getLossHouseholds()          { return lossHouseholds; }
    public double getLossBank()                { return lossBank; }
    public double getLossCompanies()           { return lossCompanies; }
    public double getWorldWrittenOff()         { return lossWorld; }
    public double getIssuedFace()              { return issuedFace; }
    public double getIssuedCosts()             { return issuedCosts; }
    public int getIssues()                     { return issues; }
    public double getEmigrantsFace()           { return emigrantsFace; }

    /** One sector's month: the face it issued, what that handed it, the principal it repaid, what it spent on other sectors' bonds (net of sales and principal back), and the coupons it was paid. */
    public double getIssued(String sector)     { return issuedBySector.getOrDefault(sector, 0.0); }
    public double getProceeds(String sector)   { return proceedsBySector.getOrDefault(sector, 0.0); }
    public double getRepaid(String sector)     { return repaidBySector.getOrDefault(sector, 0.0); }
    public double getBoughtNet(String sector)  { return boughtBySector.getOrDefault(sector, 0.0); }
    public double getCouponsTo(String sector)  { return couponsBySector.getOrDefault(sector, 0.0); }

    /** Last month's order book, every bond together: posted to buy and to sell, filled, sell orders posted and those that waited unfilled, and trades. */
    public double getLastPostedBuy()   { return lastPostedBuy; }
    public double getLastPostedSell()  { return lastPostedSell; }
    public double getLastFilled()      { return lastFilled; }
    public int getLastSellsPosted()    { return lastSellsPosted; }
    public int getLastSellsWaited()    { return lastSellsWaited; }
    public double getLastSellQuantityWaited() { return lastSellQuantityWaited; }
    public int getLastTrades()         { return lastTrades; }

    /** Over the city's life. */
    public double getLifeIssued()      { return lifeIssued; }
    public double getLifeCosts()       { return lifeCosts; }
    public int getLifeIssues()         { return lifeIssues; }
    public double getLifeCouponsHouseholds() { return lifeCouponsHouseholds; }
    public double getLifeCouponsBank() { return lifeCouponsBank; }
    public double getLifeCouponsCompanies() { return lifeCouponsCompanies; }
    public double getLifeCouponsAbroad() { return lifeCouponsAbroad; }
    public double getLifeLossHouseholds() { return lifeLossHouseholds + lossHouseholds; }
    public double getLifeLossBank()    { return lifeLossBank + lossBank; }
    public double getLifeLossCompanies() { return lifeLossCompanies + lossCompanies; }
    public double getLifeLossWorld()   { return lifeLossWorld + lossWorld; }
    public double getLifeVolume()      { return lifeVolume; }
    public double getLifePostedSell()  { return lifePostedSell; }
    public double getLifeFilledSell()  { return lifeFilledSell; }
    public int getLifeSellsPosted()    { return lifeSellsPosted; }
    public int getLifeSellsWaited()    { return lifeSellsWaited; }
    public double getLifeWorldBought() { return lifeWorldBought; }
    public double getLifeWorldSold()   { return lifeWorldSold; }
    /** One cell buying from another (round 2): this month's cash and trades, and the city's life's - a transfer inside the households. */
    public double getBetweenHouseholds()          { return betweenHouseholds; }
    public int getBetweenHouseholdsTrades()       { return betweenHouseholdsTrades; }
    public double getLifeBetweenHouseholds()      { return lifeBetweenHouseholds; }
    public int getLifeBetweenHouseholdsTrades()   { return lifeBetweenHouseholdsTrades; }

    /** One sector's bonds' coupon, weighted by face: what its bond debt costs a year. 0 with none. */
    public double averageCoupon(String sector) {
        double face = 0, paid = 0;
        for (CorporateBond b : bonds) if (b.issuer.equals(sector)) { face += b.face; paid += b.face * b.coupon; }
        return face > 0 ? paid / face : 0;
    }

    /** ...and every bond's. */
    public double averageCoupon() {
        double face = 0, paid = 0;
        for (CorporateBond b : bonds) { face += b.face; paid += b.face * b.coupon; }
        return face > 0 ? paid / face : 0;
    }

    /** What a holder class's bonds are worth at the bonds' value this month. */
    public double valueHeld(java.util.function.ToDoubleFunction<CorporateBond> holding, int month) {
        double t = 0;
        for (CorporateBond b : bonds) {
            double h = holding.applyAsDouble(b);
            if (h > 0) t += h * modelPrice(b, month);
        }
        return t;
    }

    /* ============================== save and load ============================== */

    /** Everything the market carries from one month to the next, as the save writes it. */
    public static final class State {
        List<CorporateBond> bonds;
        List<OrderBook> books;
        int nextId;
        double[] month;
        double[] life;
        String lastIssue;
        double[] lastIssueFigures;
        Map<String, Double> issuedBySector, proceedsBySector, repaidBySector, boughtBySector, couponsBySector;
    }

    public State toState() {
        State s = new State();
        s.bonds = new ArrayList<>(bonds);
        s.books = new ArrayList<>(books.values());
        s.nextId = nextId;
        s.month = new double[] {
                householdsBought, householdsSold, householdsBoughtAbroad, householdsSoldAbroad,
                worldBought, worldSold,
                couponsToHouseholds, couponsToBank, couponsToCompanies, couponsAbroad,
                principalToHouseholds, principalToBank, principalToCompanies, principalAbroad,
                lossHouseholds, lossBank, lossCompanies, lossWorld,
                issuedFace, issuedCosts, issues, emigrantsFace,
                lastPostedBuy, lastPostedSell, lastFilled, lastSellQuantityWaited,
                lastSellsPosted, lastSellsWaited, lastTrades, bankLossCost,
                betweenHouseholds, betweenHouseholdsTrades };
        s.life = new double[] {
                lifeIssued, lifeCosts, lifeCouponsHouseholds, lifeCouponsBank, lifeCouponsCompanies, lifeCouponsAbroad,
                lifeLossHouseholds, lifeLossBank, lifeLossCompanies, lifeLossWorld,
                lifeVolume, lifePostedSell, lifeFilledSell, lifeWorldBought, lifeWorldSold,
                lifeIssues, lifeSellsPosted, lifeSellsWaited,
                lifeBetweenHouseholds, lifeBetweenHouseholdsTrades };
        s.lastIssue = lastIssue;
        s.lastIssueFigures = new double[] { lastIssueFace, finite(lastIssueCoupon), finite(lastIssueLoanRate), lastIssueMonth };
        s.issuedBySector = new LinkedHashMap<>(issuedBySector);
        s.proceedsBySector = new LinkedHashMap<>(proceedsBySector);
        s.repaidBySector = new LinkedHashMap<>(repaidBySector);
        s.boughtBySector = new LinkedHashMap<>(boughtBySector);
        s.couponsBySector = new LinkedHashMap<>(couponsBySector);
        return s;
    }

    /** Puts a saved market back. A save from before 0.7.12 has none: a city with no bonds, which is what it was. */
    public void restore(State s) {
        reset();
        if (s == null) return;
        if (s.bonds != null) for (CorporateBond b : s.bonds) if (b != null && b.issuer != null) {
            if (b.companies == null) b.companies = new LinkedHashMap<>();
            bonds.add(b);
        }
        if (s.books != null) for (OrderBook book : s.books) if (book != null && book.instrument() != null) {
            // ...at the market's dust, which the load has already seeded (round 6).
            book.setDust(dust);
            books.put(book.instrument(), book);
        }
        nextId = Math.max(1, s.nextId);
        double[] m = s.month;
        // 30 from round 1, 32 since round 2 (the cells' trades with each other).
        if (m != null && (m.length == 30 || m.length == 32)) {
            int i = 0;
            householdsBought = m[i++]; householdsSold = m[i++]; householdsBoughtAbroad = m[i++]; householdsSoldAbroad = m[i++];
            worldBought = m[i++]; worldSold = m[i++];
            couponsToHouseholds = m[i++]; couponsToBank = m[i++]; couponsToCompanies = m[i++]; couponsAbroad = m[i++];
            principalToHouseholds = m[i++]; principalToBank = m[i++]; principalToCompanies = m[i++]; principalAbroad = m[i++];
            lossHouseholds = m[i++]; lossBank = m[i++]; lossCompanies = m[i++]; lossWorld = m[i++];
            issuedFace = m[i++]; issuedCosts = m[i++]; issues = (int) Math.round(m[i++]); emigrantsFace = m[i++];
            lastPostedBuy = m[i++]; lastPostedSell = m[i++]; lastFilled = m[i++]; lastSellQuantityWaited = m[i++];
            lastSellsPosted = (int) Math.round(m[i++]); lastSellsWaited = (int) Math.round(m[i++]);
            lastTrades = (int) Math.round(m[i++]); bankLossCost = m[i++];
            if (m.length == 32) { betweenHouseholds = m[i++]; betweenHouseholdsTrades = (int) Math.round(m[i]); }
        }
        double[] l = s.life;
        if (l != null && (l.length == 18 || l.length == 20)) {
            int i = 0;
            lifeIssued = l[i++]; lifeCosts = l[i++]; lifeCouponsHouseholds = l[i++]; lifeCouponsBank = l[i++];
            lifeCouponsCompanies = l[i++]; lifeCouponsAbroad = l[i++];
            lifeLossHouseholds = l[i++]; lifeLossBank = l[i++]; lifeLossCompanies = l[i++]; lifeLossWorld = l[i++];
            lifeVolume = l[i++]; lifePostedSell = l[i++]; lifeFilledSell = l[i++]; lifeWorldBought = l[i++]; lifeWorldSold = l[i++];
            lifeIssues = (int) Math.round(l[i++]); lifeSellsPosted = (int) Math.round(l[i++]); lifeSellsWaited = (int) Math.round(l[i++]);
            if (l.length == 20) { lifeBetweenHouseholds = l[i++]; lifeBetweenHouseholdsTrades = (int) Math.round(l[i]); }
        }
        lastIssue = s.lastIssue;
        if (s.lastIssueFigures != null && s.lastIssueFigures.length == 4) {
            lastIssueFace = s.lastIssueFigures[0];
            lastIssueCoupon = s.lastIssueFigures[1];
            lastIssueLoanRate = s.lastIssueFigures[2];
            lastIssueMonth = (int) Math.round(s.lastIssueFigures[3]);
        }
        putAll(issuedBySector, s.issuedBySector);
        putAll(proceedsBySector, s.proceedsBySector);
        putAll(repaidBySector, s.repaidBySector);
        putAll(boughtBySector, s.boughtBySector);
        putAll(couponsBySector, s.couponsBySector);
    }

    /** A save carries no NaN: nothing where there was none. */
    private static double finite(double v) { return Double.isFinite(v) ? v : 0; }

    private static void putAll(Map<String, Double> into, Map<String, Double> from) {
        into.clear();
        if (from == null) return;
        for (Map.Entry<String, Double> e : from.entrySet()) if (e.getKey() != null && e.getValue() != null) into.put(e.getKey(), e.getValue());
    }

    /** No bonds, no books, nothing over the city's life: a new city. */
    public void reset() {
        bonds.clear();
        books.clear();
        nextId = 1;
        startMonth();
        lifeIssued = lifeCosts = lifeCouponsHouseholds = lifeCouponsBank = lifeCouponsCompanies = lifeCouponsAbroad = 0;
        lifeLossHouseholds = lifeLossBank = lifeLossCompanies = lifeLossWorld = 0;
        lossHouseholds = lossBank = lossCompanies = lossWorld = 0;
        lifeVolume = lifePostedSell = lifeFilledSell = lifeWorldBought = lifeWorldSold = 0;
        lifeIssues = lifeSellsPosted = lifeSellsWaited = 0;
        lifeBetweenHouseholds = 0;
        lifeBetweenHouseholdsTrades = 0;
        dueCells.clear();
        lastPostedBuy = lastPostedSell = lastFilled = lastSellQuantityWaited = 0;
        lastSellsPosted = lastSellsWaited = lastTrades = 0;
        dueHouseholds = dueBank = dueWorld = 0;
        dueCompanies.clear();
        dueByIssuer.clear();
        lastIssue = null;
        lastIssueFace = lastIssueCoupon = lastIssueLoanRate = 0;
        lastIssueMonth = -1;
    }

    /** Every money figure in the new unit: the bonds' faces and holdings, the orders' quantities, the month's flows. Prices a unit of face and coupons do not move. */
    public void redenominate(double scale) {
        for (CorporateBond b : bonds) b.redenominate(scale);
        for (OrderBook book : books.values()) book.redenominate(scale, true);
        dust *= scale;
        householdsBought *= scale; householdsSold *= scale; householdsBoughtAbroad *= scale; householdsSoldAbroad *= scale;
        worldBought *= scale; worldSold *= scale;
        couponsToHouseholds *= scale; couponsToBank *= scale; couponsToCompanies *= scale; couponsAbroad *= scale;
        principalToHouseholds *= scale; principalToBank *= scale; principalToCompanies *= scale; principalAbroad *= scale;
        lossHouseholds *= scale; lossBank *= scale; lossCompanies *= scale; lossWorld *= scale;
        bankLossCost *= scale; issuedFace *= scale; issuedCosts *= scale; emigrantsFace *= scale;
        lastPostedBuy *= scale; lastPostedSell *= scale; lastFilled *= scale; lastSellQuantityWaited *= scale;
        lifeIssued *= scale; lifeCosts *= scale; lifeCouponsHouseholds *= scale; lifeCouponsBank *= scale;
        lifeCouponsCompanies *= scale; lifeCouponsAbroad *= scale;
        lifeLossHouseholds *= scale; lifeLossBank *= scale; lifeLossCompanies *= scale; lifeLossWorld *= scale;
        lifeVolume *= scale; lifePostedSell *= scale; lifeFilledSell *= scale; lifeWorldBought *= scale; lifeWorldSold *= scale;
        betweenHouseholds *= scale; lifeBetweenHouseholds *= scale;
        companiesSoldShort *= scale;
        bankLossTaken.replaceAll((k, v) -> v * scale);
        lastIssueFace *= scale;
        dueHouseholds *= scale; dueBank *= scale; dueWorld *= scale;
        dueCompanies.replaceAll((k, v) -> v * scale);
        dueCells.replaceAll((k, v) -> v * scale);
        dueByIssuer.replaceAll((k, v) -> v * scale);
        for (Map<String, Double> m : List.of(issuedBySector, proceedsBySector, repaidBySector, boughtBySector,
                couponsBySector, bankLossBySector)) m.replaceAll((k, v) -> v * scale);
    }
}
