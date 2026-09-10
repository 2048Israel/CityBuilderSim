package ham.citybuildersim;

/**
 * The share register: who owns the city's companies, what they paid for them,
 * and what the companies pay them back.
 *
 * ==================== WHY ====================
 *
 * Until this evening nobody owned anything. Six sectors and a bank earned,
 * banked, borrowed and defaulted, and every dollar a company ever cleared was
 * still on its balance sheet - at month 4,000 the sectors held US$1.7-2.4
 * trillion abroad and $150-190bn at home in cities of 20,000 people, because
 * no company paid a dividend, bought back a share, or had an owner to pay.
 * And the other way round: every expansion was cash or a loan, so a city with
 * no bank borrowed at nineteen percent to open its first shop, and the bank's
 * own capital was declared to arrive from "savers down the road" without any
 * saver paying for it.
 *
 * Jerus, 2026-09-10: "businesses first do offerings domestically and if not
 * enough is raised they go foreign... each household type gets the offer and
 * based on their situation and their cash available they accept or decline...
 * each household needs a number of shares owned per company."
 *
 * ==================== THE SEVEN ====================
 *
 * The six sectors and the bank - "specially the bank, they need equity to
 * avoid rough start". Each is one company with one class of share, listed
 * here by name; the households hold their shares per cell (Household.shares),
 * and the rest are held abroad. No exchange yet: a share is bought at an
 * offering, pays its dividend, and is held. Jerus: "when we build the
 * exchange, which we will but not just yet."
 *
 * ==================== WHEN A COMPANY GOES TO THE MARKET ====================
 *
 * Every company lists on day one, and the bank's founding capital is the
 * first thing sold. After that, Jerus's rule:
 *
 *   "at the beginning, and equity before debt, specially in good times...
 *    it will check if it thinks it might expand in the next 2-5 years, and
 *    raise equity for it; they will try not to raise equity in bad times; if
 *    the situation is normal, they'll use debt, or if they have too much
 *    equity relative to assets."
 *
 * So each company reads its own last twelve months and is in one of four
 * states (see regime()):
 *
 *   NEW      fewer than twelve months on the books: every plan is part
 *            equity, at the founding share, because there is no record to
 *            borrow against and the founders are still putting money in
 *   GOOD     profitable in nine months of twelve and not declining: it raises
 *            AHEAD - the equity share of three years of what it has been
 *            building, so the next expansions are funded before they are
 *            wanted, and the till carries the war chest
 *   NORMAL   it borrows, unless it has slipped well under its equity target,
 *            in which case it raises back up to it
 *   BAD      losses: it does not go to the market at all
 *
 * ==================== HOW MUCH EQUITY ====================
 *
 * "They adjust based on their profitability: if the business is stable
 * they'll go for less equity compared to debt, if more risky, then more
 * equity... debt is the leverage aspect."
 *
 * The target share of the balance sheet that is equity rises with how much
 * the last twelve months' income swung: a steady earner runs at thirty
 * percent equity and seventy percent debt, a business whose income swings by
 * its own size runs at fifty, and nothing runs past seventy. See
 * targetEquityShare().
 *
 * ==================== WHAT THE OWNERS GET ====================
 *
 * A fixed share of net income - "net income, not cash, and only if it's
 * positive" - paid every month a company made money, from its till, and
 * from what it holds abroad if the till is short. Nothing is paid on a loss
 * and nothing is borrowed to pay a dividend. The households' part lands in
 * their savings; the rest leaves on the income account.
 *
 * ==================== WHO BUYS ====================
 *
 * The households first, on HouseholdBalance.subscribe()'s rule. Then the
 * world, at the same price, if the yield beats what the world pays plus a
 * premium - a founding offering excepted, which is bought on prospects
 * because there is no record to price. What neither will take is not raised,
 * and the plan is trimmed to what debt will carry, as it always was.
 *
 * ==================== PRICE ====================
 *
 * Book. A share is worth the company's equity divided by the shares in
 * issue, and that is what an offering sells them at; the first offering of a
 * company with no shares sells them at a thousand dollars each. An exchange
 * will have an opinion; the register does not.
 */
public class Equity {

    /** The companies, in register order: the six sectors, then the bank. */
    public static final String[] COMPANIES;
    public static final int BANK;
    static {
        String[] sectors = BusinessDebtManager.SECTORS;
        COMPANIES = new String[sectors.length + 1];
        System.arraycopy(sectors, 0, COMPANIES, 0, sectors.length);
        COMPANIES[sectors.length] = "Bank";
        BANK = sectors.length;
    }

    public static int indexOf(String company) {
        for (int i = 0; i < COMPANIES.length; i++) if (COMPANIES[i].equals(company)) return i;
        return -1;
    }

    /* ------------------------------- the dials ------------------------------- */

    /** The share of a positive month's net income paid to the owners. */
    public static final double PAYOUT = .40;

    /** A founding share: a thousand dollars, in the game's thousands. */
    public static final double FOUNDING_PRICE = 1.0;

    /** Months on the books before a company has a record to be judged on. */
    public static final int RECORD_MONTHS = 12;

    /** Profitable months of the last twelve that make a good year. */
    public static final int GOOD_MONTHS = 9;

    /** ...and the most a bad year has. */
    public static final int BAD_MONTHS = 6;

    /** What a steady business keeps as equity: the rest is leverage. */
    public static final double BASE_EQUITY_SHARE = .30;

    /** How much the target rises per unit of income swing (std dev over |mean|). */
    public static final double RISK_SLOPE = .20;

    public static final double MAX_EQUITY_SHARE = .70;

    /** A new company's plans are this much equity, whatever its assets say. */
    public static final double NEW_EQUITY_SHARE = .50;

    /** In good times, the years of expansion a company raises for ahead. */
    public static final double HORIZON_YEARS = 3;

    /** Under target by this much before a normal year raises instead of borrows. */
    public static final double UNDER_TARGET = .10;

    /** What the world wants over its own rate to buy a share here, annual. */
    public static final double FOREIGN_PREMIUM = .03;

    public enum Regime { NEW, GOOD, NORMAL, BAD }

    /* ------------------------------- a company ------------------------------- */

    /** One listing. Package-private fields; read through the register. */
    static final class Listing {
        final String name;
        double shares;          // in issue
        double foreignShares;   // of those, held abroad
        double dealerShares;    // ...and held by the bank's trading desk (never its own: those are cancelled)
        double lastPrice = FOUNDING_PRICE;
        final double[] income = new double[RECORD_MONTHS];   // net income, a ring
        final double[] spent  = new double[RECORD_MONTHS];   // on buildings, a ring
        int months;             // recorded so far
        double lifetimeRaisedHome, lifetimeRaisedAbroad;
        double lifetimeDividendsHome, lifetimeDividendsAbroad;
        int offerings;

        // this month
        double offered, raisedHome, raisedAbroad, dividendHome, dividendDesk, dividendAbroad;
        double boughtBackThisMonth, lifetimeBoughtBack;
        Regime regime = Regime.NEW;
        double targetShare = NEW_EQUITY_SHARE;

        Listing(String name) { this.name = name; }

        /** Held by the city's households: what is neither abroad nor on the desk. */
        double domesticShares() { return shares - foreignShares - dealerShares; }
        double raised()   { return raisedHome + raisedAbroad; }
        double dividend() { return dividendHome + dividendAbroad; }

        void clearMonth() {
            offered = 0; raisedHome = 0; raisedAbroad = 0;
            dividendHome = 0; dividendDesk = 0; dividendAbroad = 0;
            boughtBackThisMonth = 0;
        }

        double trailingIncome() {
            double sum = 0;
            int n = Math.min(months, RECORD_MONTHS);
            for (int i = 0; i < n; i++) sum += income[i];
            return sum;
        }

        double trailingSpent() {
            double sum = 0;
            int n = Math.min(months, RECORD_MONTHS);
            for (int i = 0; i < n; i++) sum += spent[i];
            return sum;
        }
    }

    private final Listing[] listings = new Listing[COMPANIES.length];

    public Equity() {
        for (int i = 0; i < COMPANIES.length; i++) listings[i] = new Listing(COMPANIES[i]);
    }

    /* =====================================================================
       THE RECORD

       Fed once a month with each company's closed figures, so the regime is
       read off twelve real months and not off whatever the screen was showing.
       ===================================================================== */

    /**
     * @param company   register index
     * @param netIncome the month's, after tax
     * @param spentOnBuildings what it put into its own premises this month
     */
    public void recordMonth(int company, double netIncome, double spentOnBuildings) {
        Listing l = listings[company];
        int slot = l.months % RECORD_MONTHS;
        l.income[slot] = netIncome;
        l.spent[slot] = Math.max(0, spentOnBuildings);
        l.months++;
        l.regime = regimeOf(l);
        l.targetShare = targetEquityShareOf(l);
    }

    /** Clears the month's flows. Call at the top of the month, before any offering. */
    public void startMonth() {
        for (Listing l : listings) l.clearMonth();
    }

    private static Regime regimeOf(Listing l) {
        if (l.months < RECORD_MONTHS) return Regime.NEW;
        int profitable = 0;
        double firstHalf = 0, secondHalf = 0;
        // The ring is in order of age modulo twelve; the last twelve entries
        // are all of it, so read the older six and the newer six by slot age.
        for (int age = 0; age < RECORD_MONTHS; age++) {
            int slot = ((l.months - 1 - age) % RECORD_MONTHS + RECORD_MONTHS) % RECORD_MONTHS;
            double v = l.income[slot];
            if (v > 0) profitable++;
            if (age < RECORD_MONTHS / 2) secondHalf += v; else firstHalf += v;
        }
        double total = firstHalf + secondHalf;
        if (profitable <= BAD_MONTHS || total <= 0) return Regime.BAD;
        if (profitable >= GOOD_MONTHS && secondHalf >= firstHalf) return Regime.GOOD;
        return Regime.NORMAL;
    }

    /**
     * The share of the balance sheet a company wants as equity.
     *
     * The swing in its income over its size: a business earning the same
     * every month has none and runs at the base; one whose income swings by
     * as much as it earns adds twenty points; a loss-maker is treated as
     * riskiest of all.
     */
    private static double targetEquityShareOf(Listing l) {
        if (l.months < RECORD_MONTHS) return NEW_EQUITY_SHARE;
        int n = RECORD_MONTHS;
        double mean = 0;
        for (double v : l.income) mean += v;
        mean /= n;
        if (mean <= 0) return MAX_EQUITY_SHARE;
        double var = 0;
        for (double v : l.income) var += (v - mean) * (v - mean);
        double swing = Math.sqrt(var / n) / mean;
        return Math.min(MAX_EQUITY_SHARE, Math.max(BASE_EQUITY_SHARE,
                BASE_EQUITY_SHARE + RISK_SLOPE * swing));
    }

    /* =====================================================================
       HOW MUCH TO RAISE

       Asked by the investor with a plan in hand, before it decides what to
       borrow. The answer is in money; the offering below turns it into shares.
       ===================================================================== */

    /**
     * What a company with this plan would raise from its owners first.
     *
     * @param company  register index
     * @param assets   its balance sheet, total assets
     * @param equity   ...and the owners' part of it
     * @param planCost what it wants to build this month
     * @return money to offer, zero when it would rather borrow
     */
    public double raiseFor(int company, double assets, double equity, double planCost) {
        Listing l = listings[company];
        if (planCost <= 0) return 0;
        double target = l.targetShare;
        switch (l.regime) {
            case NEW:
                // Equity before debt at the beginning: the founders' share of
                // every plan, whatever the balance sheet says yet.
                return planCost * NEW_EQUITY_SHARE;
            case GOOD: {
                // Raise ahead. What it expects to build over the horizon is
                // what it has been building, three years of it, and at least
                // this plan; the equity share of that, less what it already
                // has past the target, capped at the expansion itself.
                double expected = Math.max(planCost, l.trailingSpent() * HORIZON_YEARS);
                double need = target * (assets + expected) - equity;
                return Math.max(0, Math.min(expected, need));
            }
            case NORMAL: {
                // Debt, unless it has slipped well under target.
                double after = assets + planCost;
                if (after <= 0) return 0;
                double shareAfter = equity / after;
                if (shareAfter < target - UNDER_TARGET) {
                    return Math.max(0, Math.min(planCost, target * after - equity));
                }
                return 0;
            }
            case BAD:
            default:
                return 0;
        }
    }

    /* =====================================================================
       THE OFFERING
       ===================================================================== */

    /**
     * Sells shares: to the households first, to the world for what is left.
     *
     * @param company    register index
     * @param amount     money asked for
     * @param bookEquity the company's equity before the offering, for the price
     * @param households who is offered it first
     * @param worldRate  what the world pays, annual, for the foreign buyer's test
     * @return money raised, home and abroad together
     */
    public double offer(int company, double amount, double bookEquity,
                        HouseholdBalance households, double worldRate) {
        return offer(company, amount, bookEquity, households, worldRate, 0);
    }

    /**
     * @param marketPrice what the exchange quotes a share at, or zero when
     *                    there is no market: a listed company sells new shares
     *                    at the market, not at the register's reckoning
     */
    public double offer(int company, double amount, double bookEquity,
                        HouseholdBalance households, double worldRate, double marketPrice) {
        Listing l = listings[company];
        if (amount <= 0) return 0;

        /*
         * THE FOUNDERS FIRST. A company's first offering can find equity
         * already on its books and owned by nobody - Retail founds with $9.5M
         * of stores the endowment built. Sold at the founding price against
         * no shares, that book would be handed to the first buyer for free.
         * So the founding book is issued to the city's households before the
         * new money is priced: the founders own what they founded, and the
         * new shares buy exactly what they pay for.
         */
        if (l.shares <= 0 && bookEquity > 0 && households != null) {
            double founders = bookEquity / FOUNDING_PRICE;
            households.grantFounders(company, founders);
            l.shares += founders;
        }

        double price = marketPrice > 0 && l.shares > 0 ? marketPrice : priceOf(l, bookEquity, worldRate);
        l.offered += amount;
        l.offerings++;

        double home = households == null ? 0 : households.subscribe(company, amount, price);
        double left = Math.max(0, amount - home);

        /*
         * THE WORLD'S TEST. Its money comes at the same price, and only if
         * the record says the dividend will beat the world's own rate by the
         * premium - the payout on the last twelve months, over the equity
         * after the offering. A founding offering has no record and is
         * bought on prospects; a company with twelve months of losses is not.
         */
        double abroad = 0;
        if (left > 1e-9) {
            boolean founding = l.months < RECORD_MONTHS;
            double equityAfter = Math.max(0, bookEquity) + home + left;
            double yield = equityAfter > 0 ? PAYOUT * Math.max(0, l.trailingIncome()) / equityAfter : 0;
            if (founding || yield >= Math.max(0, worldRate) + FOREIGN_PREMIUM) {
                abroad = left;
            }
        }

        l.shares += (home + abroad) / price;
        l.foreignShares += abroad / price;
        l.lastPrice = price;
        l.raisedHome += home;
        l.raisedAbroad += abroad;
        l.lifetimeRaisedHome += home;
        l.lifetimeRaisedAbroad += abroad;
        return home + abroad;
    }

    /**
     * What one share sells for: the company's value over its shares.
     *
     * BOOK, OR WHAT THE EARNINGS ARE WORTH, WHICHEVER IS MORE. Book alone
     * was tried and it did what book alone does to a leveraged company: Real
     * Estate, borrowing ninety cents on the dollar of its buildings, had a
     * book near zero and sold 1.37 million shares for $270k - the new money
     * bought the whole company for nothing and the founders were diluted to
     * a rounding error. A landlord with no equity still has the rents, so a
     * share is worth at least the dividend it will pay capitalised at what
     * the world asks of it: the last year's income over the world's rate plus
     * the premium. A company with neither book nor earnings sells at the last
     * price it sold at; a company with no shares at all, at the founding one.
     */
    private static double priceOf(Listing l, double bookEquity, double worldRate) {
        if (l.shares <= 0) return FOUNDING_PRICE;
        double earningsValue = PAYOUT * Math.max(0, l.trailingIncome())
                / (Math.max(0, worldRate) + FOREIGN_PREMIUM);
        double value = Math.max(bookEquity, earningsValue);
        return value > 0 ? value / l.shares : l.lastPrice;
    }

    /**
     * Lists a company that has equity and no owners: the founders' shares
     * are issued against its book, as the first offering would have done.
     *
     * "Every company lists on day one" - and a company that never went to
     * the market never did. Construction ran for four thousand months with
     * $20M in the till, no shares, no dividend and no buyback, because its
     * plans were always cash; Retail in the playtest city the same. Called
     * every month, it lists them the month they first have a book.
     *
     * @return true if it was listed now
     */
    public boolean listIfUnlisted(int company, double bookEquity, HouseholdBalance households) {
        Listing l = listings[company];
        if (l.shares > 0 || !(bookEquity > 0) || households == null) return false;
        double founders = bookEquity / FOUNDING_PRICE;
        households.grantFounders(company, founders);
        l.shares += founders;
        l.lastPrice = FOUNDING_PRICE;
        return true;
    }

    /** What one share is worth on the books today. */
    public double bookPerShare(int company, double bookEquity) {
        Listing l = listings[company];
        return l.shares > 0 ? Math.max(0, bookEquity) / l.shares : 0;
    }

    /* =====================================================================
       THE DIVIDEND
       ===================================================================== */

    /**
     * What a company owes its owners on a month's result.
     *
     * @param netIncome the closed month's, after tax
     * @return the dividend, before anybody asks whether the till can pay it
     */
    public double dividendDue(int company, double netIncome) {
        Listing l = listings[company];
        if (l.shares <= 0 || netIncome <= 0) return 0;
        return netIncome * PAYOUT;
    }

    /**
     * Shares that left the city with their holders this month are held
     * abroad from now on.
     *
     * The register's count of domestic shares and the households' own count
     * differ by exactly what emigrants took with them (see
     * HouseholdBalance.getSharesTakenAway()), and the two have to agree
     * before a dividend is split - so this runs every month for every
     * company, whether or not it pays.
     */
    public void followEmigrants(HouseholdBalance households) {
        if (households == null) return;
        for (int c = 0; c < listings.length; c++) {
            double gone = households.getSharesTakenAway(c);
            if (gone > 0) {
                Listing l = listings[c];
                l.foreignShares = Math.min(l.shares, l.foreignShares + gone);
            }
        }
    }

    /**
     * Pays a dividend: the households' part into their savings, the rest to
     * the shareholders abroad. Every share is paid exactly once, which needs
     * followEmigrants() to have run for the month first.
     *
     * @param paid the money actually paid, decided by the caller from the till
     * @return the part paid abroad
     */
    public double payDividend(int company, double paid, HouseholdBalance households) {
        Listing l = listings[company];
        if (paid <= 0 || l.shares <= 0) return 0;

        double perShare = paid / l.shares;
        double home = households == null ? 0 : households.creditDividend(company, perShare);
        // The desk is paid on what it holds - and PAYS on what it is short,
        // like any short seller: the holders of the shares it sold and does
        // not have are paid in full, and the difference is the desk's.
        double desk = l.dealerShares * perShare;
        double abroad = Math.max(0, paid - home - desk);
        l.dividendHome += home;
        l.dividendDesk += desk;
        l.dividendAbroad += abroad;
        l.lifetimeDividendsHome += home;
        l.lifetimeDividendsAbroad += abroad;
        return abroad;
    }

    /** What the bank's trading desk was paid on its inventory this month. */
    public double getDividendDeskThisMonth(int company) { return listings[company].dividendDesk; }
    public double getDividendDeskThisMonth() { double s = 0; for (Listing l : listings) s += l.dividendDesk; return s; }

    /* =====================================================================
       THE DESK

       The exchange moves shares between the three holders through the bank's
       trading desk, and the register keeps the count so the identity
       "in issue = households + desk + abroad" lives in one place. See
       Exchange. The bank's OWN shares never sit on the desk: bought, they
       are cancelled; sold, they are issued - which is what treasury stock is.
       ===================================================================== */

    /** Shares the desk holds; negative when it has sold what it did not have. */
    public double getDealerShares(int company) { return listings[company].dealerShares; }

    /** Shares in the owners' hands: in issue less what the desk is long. */
    public double getOutstanding(int company) {
        Listing l = listings[company];
        return l.shares - Math.max(0, l.dealerShares);
    }

    /** The desk buys from the city's households (whose cells the caller has already debited). */
    void deskBuysFromHouseholds(int company, double n) {
        if (n <= 0) return;
        Listing l = listings[company];
        if (company == BANK) { l.shares -= n; return; }
        l.dealerShares += n;
    }

    /** ...and sells to them. */
    void deskSellsToHouseholds(int company, double n) {
        if (n <= 0) return;
        Listing l = listings[company];
        if (company == BANK) { l.shares += n; return; }
        l.dealerShares -= n;
    }

    void deskBuysFromAbroad(int company, double n) {
        if (n <= 0) return;
        Listing l = listings[company];
        l.foreignShares = Math.max(0, l.foreignShares - n);
        if (company == BANK) { l.shares -= n; return; }
        l.dealerShares += n;
    }

    void deskSellsAbroad(int company, double n) {
        if (n <= 0) return;
        Listing l = listings[company];
        l.foreignShares += n;
        if (company == BANK) { l.shares += n; return; }
        l.dealerShares -= n;
    }

    /**
     * A company buys back and cancels shares from every holder pro rata - a
     * tender at one price. The caller has already paid each holder.
     *
     * @param fromHouseholds shares the households tendered (their cells already debited)
     * @param fromDesk       shares the desk tendered
     * @param fromAbroad     shares the world tendered
     */
    void cancel(int company, double fromHouseholds, double fromDesk, double fromAbroad) {
        Listing l = listings[company];
        double n = Math.max(0, fromHouseholds) + Math.max(0, fromDesk) + Math.max(0, fromAbroad);
        if (n <= 0) return;
        l.foreignShares = Math.max(0, l.foreignShares - Math.max(0, fromAbroad));
        l.dealerShares -= Math.max(0, fromDesk);
        l.shares = Math.max(0, l.shares - n);
        l.boughtBackThisMonth += n;
        l.lifetimeBoughtBack += n;
    }

    /**
     * A split (k > 1) or a consolidation (k < 1): every count by k, the
     * last price by its inverse. The households' and the desk's counts are
     * the exchange's to move alongside (see Exchange.splitWhatNeedsIt).
     */
    void split(int company, double k) {
        if (!(k > 0) || k == 1) return;
        Listing l = listings[company];
        l.shares *= k;
        l.foreignShares *= k;
        l.dealerShares *= k;
        l.lastPrice /= k;
        l.boughtBackThisMonth *= k;
        l.lifetimeBoughtBack *= k;
    }

    public double getBoughtBackThisMonth(int company) { return listings[company].boughtBackThisMonth; }
    public double getLifetimeBoughtBack(int company)  { return listings[company].lifetimeBoughtBack; }

    /**
     * What a share is worth on the register's own reckoning: book or
     * capitalised earnings, whichever is more, over the shares in issue. The
     * exchange quotes around this; an offering with no market sells at it.
     */
    public double fairValue(int company, double bookEquity, double worldRate) {
        return priceOf(listings[company], bookEquity, worldRate);
    }

    /** Dividend a share would pay over a year on the last twelve months' record. */
    public double dividendPerShareAnnual(int company) {
        Listing l = listings[company];
        return l.shares > 0 ? PAYOUT * Math.max(0, l.trailingIncome()) / l.shares : 0;
    }

    /* ------------------------------- reading ------------------------------- */

    public double getShares(int company)         { return listings[company].shares; }
    public double getForeignShares(int company)  { return listings[company].foreignShares; }
    public double getDomesticShares(int company) { return listings[company].domesticShares(); }
    public double getLastPrice(int company)      { return listings[company].lastPrice; }
    public Regime getRegime(int company)         { return listings[company].regime; }
    public double getTargetEquityShare(int company) { return listings[company].targetShare; }
    public int getMonthsRecorded(int company)    { return listings[company].months; }
    public int getOfferings(int company)         { return listings[company].offerings; }

    /** Share of the company held abroad, 0-1. */
    public double foreignShare(int company) {
        Listing l = listings[company];
        return l.shares > 0 ? l.foreignShares / l.shares : 0;
    }

    public double getOfferedThisMonth(int company)      { return listings[company].offered; }
    public double getRaisedHomeThisMonth(int company)   { return listings[company].raisedHome; }
    public double getRaisedAbroadThisMonth(int company) { return listings[company].raisedAbroad; }
    public double getDividendHomeThisMonth(int company)   { return listings[company].dividendHome; }
    public double getDividendAbroadThisMonth(int company) { return listings[company].dividendAbroad; }
    public double getRaisedThisMonth(int company)   { return listings[company].raised(); }
    public double getDividendThisMonth(int company) { return listings[company].dividend(); }

    public double getLifetimeRaisedHome(int company)      { return listings[company].lifetimeRaisedHome; }
    public double getLifetimeRaisedAbroad(int company)    { return listings[company].lifetimeRaisedAbroad; }
    public double getLifetimeDividendsHome(int company)   { return listings[company].lifetimeDividendsHome; }
    public double getLifetimeDividendsAbroad(int company) { return listings[company].lifetimeDividendsAbroad; }

    /* ---- the city, for MoneyAudit and the summary ---- */

    public double getRaisedHomeThisMonth()   { double s = 0; for (Listing l : listings) s += l.raisedHome; return s; }
    public double getRaisedAbroadThisMonth() { double s = 0; for (Listing l : listings) s += l.raisedAbroad; return s; }
    public double getDividendHomeThisMonth()   { double s = 0; for (Listing l : listings) s += l.dividendHome; return s; }
    public double getDividendAbroadThisMonth() { double s = 0; for (Listing l : listings) s += l.dividendAbroad; return s; }
    public double getLifetimeRaisedHome()      { double s = 0; for (Listing l : listings) s += l.lifetimeRaisedHome; return s; }
    public double getLifetimeRaisedAbroad()    { double s = 0; for (Listing l : listings) s += l.lifetimeRaisedAbroad; return s; }
    public double getLifetimeDividendsHome()   { double s = 0; for (Listing l : listings) s += l.lifetimeDividendsHome; return s; }
    public double getLifetimeDividendsAbroad() { double s = 0; for (Listing l : listings) s += l.lifetimeDividendsAbroad; return s; }
    public int getOfferings() { int s = 0; for (Listing l : listings) s += l.offerings; return s; }

    /* ------------------------------- saving -------------------------------
     *
     * Named company by company, like the household cells: a sector added to
     * the list cannot read another's register. SLOTS a company, in the order
     * below; the rings first, then the position, then the lifetime figures.
     */

    /** Slots a company before the desk (2026-09-10, night). */
    public static final int SLOTS_BEFORE_DESK = RECORD_MONTHS * 2 + 10;

    public static final int SLOTS = SLOTS_BEFORE_DESK + 2;

    public String[] keys() { return COMPANIES.clone(); }

    public double[] toSaveArray() {
        double[] out = new double[listings.length * SLOTS];
        int i = 0;
        for (Listing l : listings) {
            for (double v : l.income) out[i++] = v;
            for (double v : l.spent) out[i++] = v;
            out[i++] = l.months;
            out[i++] = l.shares;
            out[i++] = l.foreignShares;
            out[i++] = l.lastPrice;
            out[i++] = l.offerings;
            out[i++] = l.lifetimeRaisedHome;
            out[i++] = l.lifetimeRaisedAbroad;
            out[i++] = l.lifetimeDividendsHome;
            out[i++] = l.lifetimeDividendsAbroad;
            out[i++] = l.targetShare;
            out[i++] = l.dealerShares;
            out[i++] = l.lifetimeBoughtBack;
        }
        return out;
    }

    /** @return false if nothing was restored: a null, or an array not the keys' length */
    public boolean restore(String[] keys, double[] saved) {
        if (keys == null || saved == null || keys.length == 0) return false;
        int slots = saved.length / keys.length;
        if (saved.length != keys.length * slots || (slots != SLOTS && slots != SLOTS_BEFORE_DESK)) return false;
        int i = 0;
        for (String key : keys) {
            int c = indexOf(key);
            if (c < 0) { i += slots; continue; }
            Listing l = listings[c];
            for (int k = 0; k < RECORD_MONTHS; k++) l.income[k] = saved[i++];
            for (int k = 0; k < RECORD_MONTHS; k++) l.spent[k] = saved[i++];
            l.months        = (int) Math.round(saved[i++]);
            l.shares        = saved[i++];
            l.foreignShares = saved[i++];
            l.lastPrice     = saved[i++];
            l.offerings     = (int) Math.round(saved[i++]);
            l.lifetimeRaisedHome      = saved[i++];
            l.lifetimeRaisedAbroad    = saved[i++];
            l.lifetimeDividendsHome   = saved[i++];
            l.lifetimeDividendsAbroad = saved[i++];
            l.targetShare   = saved[i++];
            l.dealerShares = 0;
            l.lifetimeBoughtBack = 0;
            if (slots == SLOTS) {
                l.dealerShares      = saved[i++];
                l.lifetimeBoughtBack = saved[i++];
            }
            l.regime = regimeOf(l);
        }
        return true;
    }

    public void reset() {
        for (int i = 0; i < listings.length; i++) listings[i] = new Listing(COMPANIES[i]);
    }

    /**
     * Everything in money, in the new unit. Share counts do not move; the
     * price of one, the incomes on the record and the lifetime flows do.
     */
    public void redenominate(double scale) {
        for (Listing l : listings) {
            for (int k = 0; k < RECORD_MONTHS; k++) { l.income[k] *= scale; l.spent[k] *= scale; }
            l.lastPrice *= scale;
            l.lifetimeRaisedHome *= scale;      l.lifetimeRaisedAbroad *= scale;
            l.lifetimeDividendsHome *= scale;   l.lifetimeDividendsAbroad *= scale;
            l.offered *= scale;  l.raisedHome *= scale;  l.raisedAbroad *= scale;
            l.dividendHome *= scale;  l.dividendDesk *= scale;  l.dividendAbroad *= scale;
        }
    }
}
