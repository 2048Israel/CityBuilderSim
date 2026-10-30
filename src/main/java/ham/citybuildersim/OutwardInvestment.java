package ham.citybuildersim;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Outward investment: what the city's businesses do with money the bank will
 * not pay for.
 *
 * THE MIRROR OF HOT MONEY. CapitalFlows is foreign savings coming here for a
 * spread; this is the city's own savings going abroad for one. A treasury
 * sitting on a billion that the bank pays nothing on, while the world pays two
 * percent, does what every corporate treasurer does with idle cash - it buys
 * the world's paper - and brings it home when the bank pays better again.
 *
 * WHY IT EXISTS (2026-09-10). Every long run in this game was a steel economy
 * and every steel economy cycled on the currency: the city exported ore and
 * steel, imported scrap and a little food, and ran a surplus of 30-50% of GDP
 * that NOTHING recycled - no household imported anything but food, no company
 * paid a dividend, no saver could hold a foreign asset, and the treasury only
 * bought reserves to back hot money it did not have. So the float did the one
 * thing a float can do with a surplus nobody recycles: it appreciated at ten
 * percent a year until the exporters were dead, the bank ate their loans, the
 * city recapitalised it, the price recovered and it went round again. Twenty
 * cycles in 4,000 months; $4.5-7.9bn written off a run. The same eight seeds
 * with the rate pinned produced a city twice the size with the spread across
 * seeds collapsed. See claude/the-surplus-has-nowhere-to-go.md.
 *
 * In the balance of payments this is a financial outflow, which is the thing
 * that offsets a current-account surplus in every real surplus economy - the
 * Norwegians, the Swiss, the Singaporeans all run one. It reaches the currency
 * through ForeignAccounts, which prices the rate on the overall balance now
 * rather than the trade balance alone, and the money that leaves earns the
 * world's rate abroad; that coupon is struck as a line of the business's
 * profit and kept abroad, growing the dollars it holds (HD2, 2026-10-30;
 * rolled where it was earned, in no statement, from 2026-09-10 to HD1, and
 * paid home into the till by HD1). Jerus's call, from four options:
 * "outward investment".
 *
 * WHAT IT IS NOT. Not a dividend - the sector still owns the money, abroad
 * rather than at the counter, and its balance sheet carries it. Not hot money
 * in reverse - hot money is a stranger's money with a panic clock; this is the
 * owner's, and the owner is in no hurry either way. And not the household's
 * savings, yet: the $2bn households hold is real and should follow the same
 * rule, but it lives inside HouseholdBalance's own machinery and is a second
 * change.
 *
 * WHO GOES. A sector with money in the bank and nothing owed to it. A sector
 * carrying a loan at six percent has no business lending abroad at two, and a
 * sector in overdraft brings what it has home first - so the rule is also the
 * rule that keeps a borrower liquid, which is worth having on its own.
 *
 * AND THE CITY'S OWN PAPER (CD2, slice 4, D33). The same treasurer has a third
 * place for idle cash: the paper the city's own treasury sells at auction
 * (Auction.firmsAt() is this class's rule with that paper beside the deposit
 * and the world). The sector buys it at the settle out of its till, is paid
 * its coupon and its principal into the till, and sells it to the bank's desk
 * when the spread has gone, when it borrows, or when it needs the money - by
 * the rule that sends the dollars home. See THE CITY'S PAPER.
 *
 * @author Jerus
 */
public class OutwardInvestment {

    /* =======================================================================
       HOW MUCH WANTS TO GO
       ======================================================================= */

    /**
     * The share of a sector's wealth it holds abroad, per unit of spread.
     *
     * A half-point spread is worth a fifth of the till, a point two fifths, two
     * points four fifths. Steep, on purpose, and the first cut was not: at
     * fifteen per point a bank paying nothing against a 2% world sent thirty
     * percent abroad, the other seventy sat at a counter paying nothing, and
     * the trade surplus was two-thirds unrecycled - the currency still
     * appreciated at eight percent a year. Idle corporate cash is not a
     * household's pension: it goes wherever the paper yields, all of it but
     * the working capital, and a treasurer who left seventy percent of a
     * billion earning nothing would be replaced.
     */
    public static final double APPETITE = 40;

    /** Never more than this. The rest is working capital, wherever the rate is. */
    public static final double MAX_SHARE = .9;

    /**
     * How much of the gap to its target moves abroad in a month. Slow, on
     * purpose: the money is the owner's and the owner is in no hurry. Half the
     * gap in about seventeen months.
     */
    public static final double OUT_SPEED = .04;

    /** ...and how much comes home in a month, which is faster, because it is needed. */
    public static final double HOME_SPEED = .10;

    /* =======================================================================
       THE ROUNDING FLOOR, AND WHY IT IS SEEDED (2026-09-13)

       A move smaller than a thousandth of a cent is not a move; it is the
       arithmetic's own dust, and moving it writes a trade into the balance of
       payments for nothing. So it is swept to zero - and for two builds it was
       swept at a FIXED 1e-9, which made it a money constant that a currency
       reform never reseeded. That is the third one of these this project has
       found and it is the same bug every time (see Equity.FOUNDING_PRICE and
       Exchange.MIN_FAIR, whose note describes this exact symptom).

       WHY IT MATTERS, which is not obvious from the size of it. After a
       hundred-to-one reform every amount is a hundred times smaller, so a move
       the plain city makes is swept away in the reformed one - and a swept
       move is a sector that did NOT send its money abroad this month. Its
       dollars abroad then differ for ever after, its wealth differs, next
       month's target differs, and within a year the two cities are visibly
       different places. Found by the ninth sector: Manufacturing generates
       cash with no debt behind it, which is exactly the condition
       targetShare tests, and it put three sectors on the wrong side of the
       sweep within five months of the reform.
       ======================================================================= */
    public static final double MIN_MOVE = 1e-9;

    /** The same floor in today's money. See MIN_MOVE. */
    private double minMove = MIN_MOVE;

    /** Re-seeds the floor at a given unit. See Denomination. */
    public void seedConstants(double unit) {
        minMove = MIN_MOVE / (unit > 0 ? unit : 1);
    }

    /* ----------------------------------- state ----------------------------------- */

    /** Held abroad, per sector, in the dollars it is held in. */
    private final Map<String, Double> usd = new LinkedHashMap<>();

    /** This month's move, per sector, in local money: positive went abroad, negative came home. */
    private final Map<String, Double> moved = new LinkedHashMap<>();

    /** Brought home on demand earlier this month, per sector, in local money. Folded into `moved` at takeMonth(). */
    private final Map<String, Double> recalled = new LinkedHashMap<>();

    /** This month's income from abroad, per sector, in local money: the coupon payCoupons() struck as a line of the profit and kept abroad (HD2). */
    private final Map<String, Double> interest = new LinkedHashMap<>();

    /**
     * Local currency per dollar the stock was last valued at - the rate the
     * month was traded at. What a sector's foreign assets are worth in its own
     * money between one month and the next, and the rate a reform has to
     * divide, because the dollars do not move in a reform and the local value
     * of them does.
     */
    private double lastRate = ForeignAccounts.OPENING_RATE;

    /** The city's paper held, per sector, at face, in local money (CD2, slice 4). DebtManager.firmPrincipal() is the same figure from the paper's side. */
    private final Map<String, Double> paper = new LinkedHashMap<>();

    private double spread;          // what the world pays over the bank, annual
    private double targetShare;     // of wealth, abroad
    private double lifetimeOut, lifetimeHome, lifetimeInterest;   // local money
    private double peakUsd;

    public OutwardInvestment() {
        for (String s : Sectors.KEYS) {
            usd.put(s, 0.0);
            moved.put(s, 0.0);
            interest.put(s, 0.0);
            paper.put(s, 0.0);
        }
    }

    /* --------------------------------- the month --------------------------------- */

    /* =======================================================================
       A LINE OF PROFIT, KEPT ABROAD (HD2, 2026-10-30)

       WHAT IT WAS. From 2026-09-10 the coupon on a sector's dollars abroad
       was rolled where it was earned and added to the dollars held, and no
       statement saw it, so no profit tax and no dividend ever did. In Jerus's
       San Francisco city at m2,852 the businesses held $406.8T abroad, 47x
       GDP, and 57% of it was compounded coupon - the largest income in the
       city, untaxed and never paid out. Jerus, 2026-10-28: "interest abroad
       as profit sure". HD1 struck it as profit and paid it home into the
       till, and paid home it inverted MonetaryCheck's rate dial: inflation
       7.92 / 6.25 / 6.76 / 8.17% at policy rates of 3 / 10 / 20 / 40%,
       against 10.43 / 9.68 / 8.99 / 8.82% before it, because at low rates
       the businesses' idle money is abroad and its coupon crossing the
       exchanges lifted the D$ more than the dial restrained. (The first
       version of all had paid it into the till too: a coupon from abroad is
       a current-account credit, and a city holding US$46bn abroad earned
       $12M a month of them against $3M of exports.)

       WHAT IT IS NOW. payCoupons() strikes it with the sectors' statements,
       at the top of the month: the sector's income statement carries it as a
       line of finance income, inside the profit before tax, so the profit
       tax and the dividend see it. The cash stays abroad: the dollars held
       grow by it, Sector.bank() puts none of it in the till, the till pays
       the tax and the dividend struck on it, and dollars come home only when
       the till is short (Game.payDividends() and settleBusinessCredit() call
       recall()). A balance of payments books a foreign holding's reinvested
       earnings the same way: income in, the same amount out as investment
       (MoneyAudit's "+ sectors ForeignInterest" and "- sectors
       ForeignInterestReinvested"). The households' coupon is paid home
       (HouseholdBalance.investAbroad); the sectors' is not.

       RATE: measured in docs/queue/HD2-notes.md
       ======================================================================= */

    /**
     * Strikes the month's coupon on every sector's dollars abroad and hands it
     * to the sector, to be a line of its statement, inside its profit: the
     * dollars held times the world's rate over twelve, at this month's rate.
     * The cash is kept abroad - the dollars held grow by it, here, and
     * Sector.bank() puts none of it in the till. Called at the top of the
     * month, before economyManager.strikeSectors().
     *
     * @param worldRate what the world pays, annual
     * @param rate      local currency per dollar, this month
     * @param economy   whose sectors are told their coupon
     */
    public void payCoupons(double worldRate, double rate, EconomyManager economy) {
        if (economy == null) return;
        for (String sector : Sectors.KEYS) {
            double held = usd.getOrDefault(sector, 0.0);
            double earnedUsd = held * Math.max(0, worldRate) / 12;
            double local = rate > 0 ? earnedUsd * rate : 0;
            interest.put(sector, local);
            lifetimeInterest += local;
            // ...kept where it was earned: the dollars held grow by it, only when there is some (a sector with none is untouched).
            if (rate > 0 && earnedUsd > 0) usd.put(sector, held + earnedUsd);
            Sector s = economy.getSectors().byKey(sector);
            if (s != null) s.setForeignIncome(local);
        }
    }

    /**
     * Moves the month's money, in both directions. It earns nothing: the
     * month's coupon was struck as a line of the profit and kept abroad when
     * the statements were struck (payCoupons(), HD1, HD2).
     *
     * Called BEFORE MoneyAudit strikes the month, so the flows are inside the
     * audited window and the balance of payments reads them in the same
     * Result as everything else - unlike hot money, which moves after the
     * strike and reaches the currency only through its own panic clock.
     *
     * @param depositRate what the bank pays savers, annual
     * @param worldRate   what the world pays, annual
     * @param rate        local currency per dollar, this month
     * @param economy     whose sector tills the money moves through
     */
    public void takeMonth(double depositRate, double worldRate, double rate, EconomyManager economy) {

        if (!(rate > 0) || economy == null) return;
        lastRate = rate;

        spread = Math.max(0, worldRate - Math.max(0, depositRate));
        targetShare = Math.min(MAX_SHARE, spread * APPETITE);

        BusinessDebtManager credit = economy.getBusinessDebtManager();

        for (String sector : Sectors.KEYS) {

            double held = usd.getOrDefault(sector, 0.0);
            double cash = economy.getSectorCash(sector);

            /* ---------------------------- the target ---------------------------- */
            double abroad = held * rate;
            double wealth = cash + abroad;
            boolean borrower = credit != null && credit.getPrincipal(sector) > 0;
            // A till under the floor is an empty one (0.7.130): a recall that
            // brought home exactly what an order lacked leaves zero, or a
            // rounding hair over it that one unit of money has and another
            // has not - DenominationCheck's twins parted on 0 against 1.6e-17.
            double target = (cash > minMove && !borrower && wealth > 0) ? wealth * targetShare : 0;

            /* ----------------------------- the move ----------------------------- */
            double gap = target - abroad;
            double move;
            if (gap > 0) {
                move = Math.min(gap * OUT_SPEED, Math.max(0, cash));
            } else {
                move = Math.max(gap * HOME_SPEED, -abroad);
            }
            if (Math.abs(move) < minMove) move = 0;

            if (move != 0) {
                economy.setSectorCash(sector, cash - move);
                held += move / rate;
                if (held < 0) held = 0;
                usd.put(sector, held);
                if (move > 0) lifetimeOut += move; else lifetimeHome -= move;
            }
            // ...plus what was called home earlier in the month, before the
            // month's own move: a recall is a move home like any other.
            moved.put(sector, move - recalled.getOrDefault(sector, 0.0));
            recalled.put(sector, 0.0);

            // ...and the city's paper, by the same rule, once the dollars have moved.
            sellExcessPaper(sector, depositRate, worldRate, rate, borrower, economy);
        }

        double total = totalUsd();
        if (total > peakUsd) peakUsd = total;
    }

    /**
     * Brings money home because it is needed now - to build, or to pay the
     * owners - rather than because the bank pays more.
     *
     * Before this a sector holding ninety percent of its wealth abroad would
     * borrow to build, because the investor reads the till and the till was
     * empty: the war chest defeated itself. Sold at the rate the stock was
     * last valued at, which is this month's rate once takeMonth() has run and
     * last month's before it; the difference is the valuation line's, not a
     * flow's. A financial inflow, declared through getBroughtHomeThisMonth().
     *
     * AND, WHEN THE DOLLARS DO NOT COVER IT, THE CITY'S PAPER (CD2, slice 4):
     * what is still wanted is raised by selling it to the bank's desk at the
     * month's market value. That part is the bank's pool paying the till's,
     * so it is not in getBroughtHomeThisMonth() and nothing declares it.
     *
     * @param local how much is wanted, in the city's money
     * @return how much came, which is less when less is held
     */
    public double recall(String sector, double local, EconomyManager economy) {
        if (!(local > 0) || economy == null) return 0;
        double home = 0;
        double held = usd.getOrDefault(sector, 0.0);
        double available = held * lastRate;
        if (lastRate > 0 && Math.min(local, available) > 0) {
            home = Math.min(local, available);
            usd.put(sector, home >= available ? 0 : held - home / lastRate);
            economy.setSectorCash(sector, economy.getSectorCash(sector) + home);
            recalled.merge(sector, home, Double::sum);
            lifetimeHome += home;
        }
        // ...and what the dollars could not cover, from the city's paper, sold to
        // the bank's desk at the month's market value (CD2, slice 4): the
        // dollars first, because they are the older place and the one the
        // balance of payments feels.
        if (local - home > 0) home += sellPaper(sector, (local - home) / paperRatio, economy);
        return home;
    }

    /* --------------------------------- reading --------------------------------- */

    /** Dollars held abroad by one sector. */
    public double getUsd(String sector) { return usd.getOrDefault(sector, 0.0); }

    /** ...and what they are worth in the city's money, at the rate they were last valued at. */
    public double localValue(String sector) { return getUsd(sector) * lastRate; }

    public double totalUsd() {
        double t = 0;
        for (double v : usd.values()) t += v;
        return t;
    }

    public double totalLocalValue() { return totalUsd() * lastRate; }

    /** This month's move for one sector, local money: positive went abroad. */
    public double getMovedThisMonth(String sector) { return moved.getOrDefault(sector, 0.0); }

    /** This month's income from abroad for one sector, local money - the coupon payCoupons() struck: inside its profit and kept abroad, not in its till (HD2). */
    public double getInterestThisMonth(String sector) { return interest.getOrDefault(sector, 0.0); }

    /** Everything that went abroad this month, all sectors, local money. For MoneyAudit. */
    public double getInvestedAbroadThisMonth() {
        double t = 0;
        for (double v : moved.values()) if (v > 0) t += v;
        return t;
    }

    /** Everything that came home this month, all sectors, local money. For MoneyAudit. */
    public double getBroughtHomeThisMonth() {
        double t = 0;
        for (double v : moved.values()) if (v < 0) t -= v;
        return t;
    }

    /** Everything the world paid the city's businesses this month, local money, inside their profits and kept abroad (HD2). For MoneyAudit, twice. */
    public double getInterestThisMonth() {
        double t = 0;
        for (double v : interest.values()) t += v;
        return t;
    }

    public double getSpread()           { return spread; }
    public double getTargetShare()      { return targetShare; }
    public double getLastRate()         { return lastRate; }
    public double getLifetimeOut()      { return lifetimeOut; }
    public double getLifetimeHome()     { return lifetimeHome; }
    public double getLifetimeInterest() { return lifetimeInterest; }
    public double getPeakUsd()          { return peakUsd; }

    /* =======================================================================
       THE CITY'S PAPER (CD2, slice 4, D33)

       The businesses are the fourth holder of the city's own paper (Debt, WHO
       HOLDS IT). The auction's bid for them is Auction.firmsAt(); this is
       what they do with it afterwards, line by line the households' path
       (HouseholdBalance, THE CITY'S PAPER).

         - BOUGHT AT THE SETTLE, out of the sector's till, never past it: each
           sector in proportion to its own bid at the sale's yield (bidOf(),
           allotmentWeights(), Game.firmsTakeTheirShare()). What a till
           cannot pay the bank takes, as it takes the households' shortfall.
         - PAID its coupon and its principal into the till, in proportion to
           the paper it holds (creditPaperCoupon(), creditPaperPrincipal()),
           and its share of a buyback (creditPaperBuyback()).
         - SOLD to the bank's desk, at the month's market value and from every
           piece's businesses' share pro rata (Game.desksBuysFirmPaper()):
           when the spread has gone or the sector borrows, a tenth of the
           excess a month - the pace the dollars come home at (takeMonth());
           and on demand, after the dollars, for what a sector still wants
           (recall()).

       THE SECTOR'S BOOKS carry it as they carry the other businesses' bonds
       (EconomyManager.getBondAssets(), SectorBooks.record()): the paper an
       asset at face, what the sector spent on it net a cash flow of investing,
       its coupons a coupon. Without that its till moves by money its
       statement cannot name.

       POOL TO POOL. A till and the bank are both inside MoneyAudit's pools,
       so none of these moves declares a line: what leaves one pool arrives
       in the other, as a bond changing hands between two companies does
       (MoneyAudit, the businesses' bonds). The audit holds the tills' end to
       the bank's and the treasury's by closing.

       THE SUM OF EVERY SECTOR'S PAPER IS DebtManager.firmPrincipal(),
       exactly: every move here is a move on the paper by the same face, and
       HoldersCheck asserts it.
       ======================================================================= */

    /** Where the businesses' paper is sold: Game's desk books the bank's end and takes the face off every piece's businesses' share pro rata. */
    public interface PaperDesk {
        /** The desk buys this much face of the businesses' paper for this much cash. */
        void buy(double face, double cash);
    }

    private PaperDesk paperDesk;

    public void setPaperDesk(PaperDesk desk) { this.paperDesk = desk; }

    /**
     * What a dollar of face of the businesses' paper is worth this month (the
     * businesses' book at the curve over its face, DebtManager.firmBookRatio())
     * and what that paper yields at today's curve (firmBookYield()): struck
     * once a month by Game, SAVED like the households' ratio so a loaded city
     * sells at the price the live one would have.
     */
    private double paperRatio = 1;
    private double paperYield;

    public void setPaperBook(double ratio, double bookYield) {
        paperRatio = Double.isFinite(ratio) && ratio > 0 ? ratio : 1;
        paperYield = Double.isFinite(bookYield) ? bookYield : 0;
    }

    public double getPaperRatio() { return paperRatio; }
    public double getPaperYield() { return paperYield; }

    /** Bought, and sold to the desk, over the city's life, in cash. Counts for the playtest. */
    private double lifetimePaperBought, lifetimePaperSold;

    /**
     * THE MONTH'S CASH ON THE PAPER, per sector, for the sector's books: what
     * it spent on it net of what came back (bought at the settle, less what
     * it sold the desk, was repaid and was paid in a buyback) and the coupons
     * it was paid. The books carry the city's paper beside the other
     * businesses' bonds - an asset at face, a cash flow of investing and a
     * coupon - so a sector's cash is explained to the cent (SectorBooks,
     * unexplained()). Cleared by startMonth(); not saved, because the books
     * that read them are.
     */
    private final Map<String, Double> paperSpent = new LinkedHashMap<>();
    private final Map<String, Double> paperCoupons = new LinkedHashMap<>();

    /** Starts the month's figures over: Game calls it as the month opens. */
    public void startMonth() {
        paperSpent.clear();
        paperCoupons.clear();
    }

    /** Cash a sector spent on the city's paper this month, net: positive bought, negative sold or repaid. */
    public double getPaperSpentThisMonth(String sector) { return paperSpent.getOrDefault(sector, 0.0); }

    /** Coupons the city paid a sector on its paper this month. */
    public double getPaperCouponsThisMonth(String sector) { return paperCoupons.getOrDefault(sector, 0.0); }

    public double getLifetimePaperBought() { return lifetimePaperBought; }
    public double getLifetimePaperSold()   { return lifetimePaperSold; }

    /** The city's paper one sector holds, at face. */
    public double paperOf(String sector) { return paper.getOrDefault(sector, 0.0); }

    /** ...and every sector's, at face: DebtManager.firmPrincipal() from the other side. */
    public double totalPaper() {
        double t = 0;
        for (double v : paper.values()) t += v;
        return t;
    }

    /** True when a sector may hold more of it: the rule that sends dollars abroad - money in the till and nothing owed (WHO GOES). */
    private boolean mayHold(String sector, double cash, EconomyManager economy) {
        BusinessDebtManager credit = economy.getBusinessDebtManager();
        boolean borrower = credit != null && credit.getPrincipal(sector) > 0;
        return cash > minMove && !borrower;
    }

    /**
     * The businesses' idle money, for the auction (Auction.Bidders.firmsIdle):
     * the tills and the dollars abroad, at the rate they were last valued at,
     * of every sector that may hold the city's paper.
     */
    public double idleWealth(EconomyManager economy) {
        if (economy == null) return 0;
        double total = 0;
        for (String sector : Sectors.KEYS) {
            double cash = economy.getSectorCash(sector);
            if (!mayHold(sector, cash, economy)) continue;
            total += Math.max(0, cash + usd.getOrDefault(sector, 0.0) * lastRate);
        }
        return total;
    }

    /**
     * What one sector would bid for the city's paper at this yield:
     * Auction.firmsAt() for the one sector - a share of its idle money,
     * APPETITE per unit of what the paper pays over the better of the deposit
     * rate and the world's, never past MAX_SHARE, less what it holds, at
     * OUT_SPEED of the gap. Nothing from a sector that may not hold it.
     */
    public double bidOf(String sector, double yield, double depositRate, double worldRate, EconomyManager economy) {
        if (economy == null) return 0;
        double cash = economy.getSectorCash(sector);
        if (!mayHold(sector, cash, economy)) return 0;
        double wealth = cash + usd.getOrDefault(sector, 0.0) * lastRate;
        double over = Math.max(0, yield - Math.max(depositRate, worldRate));
        double target = Math.max(0, wealth) * Math.min(MAX_SHARE, APPETITE * over);
        return Math.max(0, target - paperOf(sector)) * OUT_SPEED;
    }

    /**
     * How a sale at this yield is shared among the sectors, in Sectors.KEYS
     * order, summing to one: each sector's bid over all of them. When no
     * sector bids at that yield (it has moved since the sale, or they hold
     * what they want) the sectors that may hold it share by their tills; when
     * none may, every weight is nothing and the bank takes the allotment.
     */
    public double[] allotmentWeights(double yield, double depositRate, double worldRate, EconomyManager economy) {
        String[] keys = Sectors.KEYS;
        double[] w = new double[keys.length];
        if (economy == null) return w;
        double total = 0;
        for (int i = 0; i < keys.length; i++) {
            w[i] = bidOf(keys[i], yield, depositRate, worldRate, economy);
            total += w[i];
        }
        if (!(total > 0)) {
            for (int i = 0; i < keys.length; i++) {
                double cash = economy.getSectorCash(keys[i]);
                w[i] = mayHold(keys[i], cash, economy) ? cash : 0;
                total += w[i];
            }
        }
        if (!(total > 0)) return new double[keys.length];
        for (int i = 0; i < w.length; i++) w[i] /= total;
        return w;
    }

    /** A sector pays this cash out of its till at the settle for this face of the city's paper (Game.firmsTakeTheirShare(), which has capped it at the till). */
    public void takeAllotment(String sector, double cash, double face, EconomyManager economy) {
        if (economy == null || !(face > 0) || !(cash >= 0)) return;
        economy.setSectorCash(sector, economy.getSectorCash(sector) - cash);
        paper.merge(sector, face, Double::sum);
        paperSpent.merge(sector, cash, Double::sum);
        lifetimePaperBought += cash;
    }

    /** A coupon on the businesses' share, into the tills pro rata to the paper each sector holds. */
    public void creditPaperCoupon(double total, EconomyManager economy) {
        double held = totalPaper();
        if (economy == null || !(total > 0) || !(held > 0)) return;
        for (String sector : Sectors.KEYS) {
            double mine = paperOf(sector);
            if (mine <= 0) continue;
            double share = total * mine / held;
            economy.setSectorCash(sector, economy.getSectorCash(sector) + share);
            paperCoupons.merge(sector, share, Double::sum);
        }
    }

    /** Principal on their share, into the tills with the paper down by the same, pro rata. */
    public void creditPaperPrincipal(double face, EconomyManager economy) {
        creditPaperBuyback(face, face, economy);
    }

    /**
     * A bond bought back: this much of the businesses' face off the paper,
     * this much of the price into the tills, each sector's in proportion to
     * the paper it holds.
     */
    public void creditPaperBuyback(double face, double price, EconomyManager economy) {
        double held = totalPaper();
        if (economy == null || !(face > 0) || !(held > 0)) return;
        for (String sector : Sectors.KEYS) {
            double mine = paperOf(sector);
            if (mine <= 0) continue;
            double w = mine / held;
            paper.put(sector, Math.max(0, mine - face * w));
            economy.setSectorCash(sector, economy.getSectorCash(sector) + Math.max(0, price) * w);
            paperSpent.merge(sector, -Math.max(0, price) * w, Double::sum);
        }
    }

    /**
     * A sector sells up to this much face of the city's paper to the desk, at
     * the month's market value, into its till.
     *
     * @return the cash raised
     */
    private double sellPaper(String sector, double face, EconomyManager economy) {
        double held = paperOf(sector);
        if (paperDesk == null || economy == null || !(face > 0) || !(held > 0)) return 0;
        face = Math.min(face, held);
        double cash = face * paperRatio;
        if (cash < minMove) return 0;
        paper.put(sector, held - face);
        economy.setSectorCash(sector, economy.getSectorCash(sector) + cash);
        paperSpent.merge(sector, -cash, Double::sum);
        lifetimePaperSold += cash;
        paperDesk.buy(face, cash);
        return cash;
    }

    /**
     * THE SPREAD GONE, and the borrower: a sector's target for the city's
     * paper is its rule at the paper's book yield - a share of its idle money
     * for each unit the paper pays over the better of the deposit rate and
     * the world's, nothing for a sector that has no money in its till or owes
     * (WHO GOES) - and what it holds past that goes home to the bank's desk,
     * a tenth of the excess a month (HOME_SPEED). A borrower's target is
     * nothing, so it sells all it holds at that pace.
     */
    private void sellExcessPaper(String sector, double depositRate, double worldRate, double rate,
                                 boolean borrower, EconomyManager economy) {
        double held = paperOf(sector);
        if (!(held > 0) || paperDesk == null) return;
        double cash = economy.getSectorCash(sector);
        double wealth = cash + usd.getOrDefault(sector, 0.0) * rate;
        double over = Math.max(0, paperYield - Math.max(depositRate, worldRate));
        double target = (cash > minMove && !borrower && wealth > 0) ? wealth * Math.min(MAX_SHARE, APPETITE * over) : 0;
        double excess = held - target;
        if (excess > 0) sellPaper(sector, excess * HOME_SPEED, economy);
    }

    /* ------------------------------ save and restore ------------------------------ */

    /**
     * Seven headline figures, then three per sector in SECTORS order - and
     * since CD2 (slice 4) the city's paper: one per sector, then the ratio and
     * the yield it was struck at and what the businesses have bought and sold
     * over the city's life.
     * Absent-safe: a save from before this existed restores nothing, which is
     * a city that has never invested abroad, which is what every such city was;
     * one from before the paper restores the rest and no paper.
     */
    public double[] toSaveArray() {
        String[] sectors = Sectors.KEYS;
        double[] out = new double[7 + 3 * sectors.length + sectors.length + 4];
        out[0] = lastRate;
        out[1] = lifetimeOut;
        out[2] = lifetimeHome;
        out[3] = lifetimeInterest;
        out[4] = peakUsd;
        out[5] = spread;
        out[6] = targetShare;
        for (int i = 0; i < sectors.length; i++) {
            out[7 + 3 * i]     = getUsd(sectors[i]);
            out[7 + 3 * i + 1] = getMovedThisMonth(sectors[i]);
            out[7 + 3 * i + 2] = getInterestThisMonth(sectors[i]);
        }
        int at = 7 + 3 * sectors.length;
        for (int i = 0; i < sectors.length; i++) out[at + i] = paperOf(sectors[i]);
        out[at + sectors.length]     = paperRatio;
        out[at + sectors.length + 1] = paperYield;
        out[at + sectors.length + 2] = lifetimePaperBought;
        out[at + sectors.length + 3] = lifetimePaperSold;
        return out;
    }

    public void restore(double[] saved) {
        String[] sectors = Sectors.KEYS;
        if (saved == null || saved.length < 7 + 3 * sectors.length) return;   // refused whole
        lastRate = saved[0] > 0 ? saved[0] : ForeignAccounts.OPENING_RATE;
        lifetimeOut = saved[1];
        lifetimeHome = saved[2];
        lifetimeInterest = saved[3];
        peakUsd = saved[4];
        spread = saved[5];
        targetShare = saved[6];
        for (int i = 0; i < sectors.length; i++) {
            usd.put(sectors[i], Math.max(0, saved[7 + 3 * i]));
            moved.put(sectors[i], saved[7 + 3 * i + 1]);
            interest.put(sectors[i], saved[7 + 3 * i + 2]);
        }
        // The city's paper (CD2, slice 4), when the save carries it: nothing otherwise.
        int at = 7 + 3 * sectors.length;
        boolean carriesPaper = saved.length >= at + sectors.length + 4;
        for (int i = 0; i < sectors.length; i++) paper.put(sectors[i], carriesPaper ? Math.max(0, saved[at + i]) : 0.0);
        paperRatio = carriesPaper && saved[at + sectors.length] > 0 ? saved[at + sectors.length] : 1;
        paperYield = carriesPaper ? saved[at + sectors.length + 1] : 0;
        lifetimePaperBought = carriesPaper ? saved[at + sectors.length + 2] : 0;
        lifetimePaperSold = carriesPaper ? saved[at + sectors.length + 3] : 0;
    }

    public void reset() {
        for (String s : Sectors.KEYS) {
            usd.put(s, 0.0);
            moved.put(s, 0.0);
            interest.put(s, 0.0);
            paper.put(s, 0.0);
        }
        lastRate = ForeignAccounts.OPENING_RATE;
        spread = targetShare = 0;
        lifetimeOut = lifetimeHome = lifetimeInterest = peakUsd = 0;
        paperRatio = 1;
        paperYield = 0;
        lifetimePaperBought = lifetimePaperSold = 0;
        startMonth();
    }

    /**
     * A reform divides the local money and leaves the dollars alone. The rate
     * the dollars were last valued at is local money per dollar, so it moves;
     * the dollars do not.
     */
    public void redenominate(double scale) {
        minMove *= scale;
        lastRate *= scale;
        lifetimeOut *= scale;
        lifetimeHome *= scale;
        lifetimeInterest *= scale;
        // The city's paper is face, in local money; the ratio and the yield are not.
        lifetimePaperBought *= scale;
        lifetimePaperSold *= scale;
        paperSpent.replaceAll((k, v) -> v * scale);
        paperCoupons.replaceAll((k, v) -> v * scale);
        for (String s : Sectors.KEYS) {
            moved.put(s, moved.getOrDefault(s, 0.0) * scale);
            interest.put(s, interest.getOrDefault(s, 0.0) * scale);
            paper.put(s, paper.getOrDefault(s, 0.0) * scale);
        }
    }
}
