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
 * world's rate abroad and comes back as income. Jerus's call, from four
 * options: "outward investment".
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

    /* ----------------------------------- state ----------------------------------- */

    /** Held abroad, per sector, in the dollars it is held in. */
    private final Map<String, Double> usd = new LinkedHashMap<>();

    /** This month's move, per sector, in local money: positive went abroad, negative came home. */
    private final Map<String, Double> moved = new LinkedHashMap<>();

    /** Brought home on demand earlier this month, per sector, in local money. Folded into `moved` at takeMonth(). */
    private final Map<String, Double> recalled = new LinkedHashMap<>();

    /** This month's income from abroad, per sector, in local money, rolled where it was earned. */
    private final Map<String, Double> interest = new LinkedHashMap<>();

    /**
     * Local currency per dollar the stock was last valued at - the rate the
     * month was traded at. What a sector's foreign assets are worth in its own
     * money between one month and the next, and the rate a reform has to
     * divide, because the dollars do not move in a reform and the local value
     * of them does.
     */
    private double lastRate = ForeignAccounts.OPENING_RATE;

    private double spread;          // what the world pays over the bank, annual
    private double targetShare;     // of wealth, abroad
    private double lifetimeOut, lifetimeHome, lifetimeInterest;   // local money
    private double peakUsd;

    public OutwardInvestment() {
        for (String s : BusinessDebtManager.SECTORS) {
            usd.put(s, 0.0);
            moved.put(s, 0.0);
            interest.put(s, 0.0);
        }
    }

    /* --------------------------------- the month --------------------------------- */

    /**
     * Moves the month's money, in both directions, and rolls the month's income.
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

        for (String sector : BusinessDebtManager.SECTORS) {

            double held = usd.getOrDefault(sector, 0.0);
            double cash = economy.getSectorCash(sector);

            /* ------------------------ the income, first ------------------------ */
            /*
             * REINVESTED WHERE IT IS EARNED, not paid home. The first version
             * paid it into the till, and the till is where it did its damage:
             * a coupon from abroad is a current-account credit, and a city
             * holding US$46bn abroad was earning $12M a month of them against
             * $3M of exports - the surplus this class exists to recycle had
             * been replaced by a rentier's, and the currency appreciated on
             * it exactly as before. Reinvested, the coupon is an income credit
             * matched by a financial debit, which is what a fund that rolls its
             * coupons looks like in any balance of payments, and the rate sees
             * nothing until the owner wants the money home - at which point
             * the inflow is real and so is the appreciation. Both lines are
             * declared to MoneyAudit; no cash moves, so they cancel there.
             */
            double earnedUsd = held * Math.max(0, worldRate) / 12;
            held += earnedUsd;
            interest.put(sector, earnedUsd * rate);

            /* ---------------------------- the target ---------------------------- */
            double abroad = held * rate;
            double wealth = cash + abroad;
            boolean borrower = credit != null && credit.getPrincipal(sector) > 0;
            double target = (cash > 0 && !borrower && wealth > 0) ? wealth * targetShare : 0;

            /* ----------------------------- the move ----------------------------- */
            double gap = target - abroad;
            double move;
            if (gap > 0) {
                move = Math.min(gap * OUT_SPEED, Math.max(0, cash));
            } else {
                move = Math.max(gap * HOME_SPEED, -abroad);
            }
            if (Math.abs(move) < 1e-9) move = 0;

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
            lifetimeInterest += earnedUsd * rate;
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
     * @param local how much is wanted, in the city's money
     * @return how much came, which is less when less is held
     */
    public double recall(String sector, double local, EconomyManager economy) {
        if (!(local > 0) || economy == null || !(lastRate > 0)) return 0;
        double held = usd.getOrDefault(sector, 0.0);
        double available = held * lastRate;
        double home = Math.min(local, available);
        if (home <= 0) return 0;
        usd.put(sector, held - home / lastRate);
        economy.setSectorCash(sector, economy.getSectorCash(sector) + home);
        recalled.merge(sector, home, Double::sum);
        lifetimeHome += home;
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

    /** This month's income from abroad for one sector, local money - earned and reinvested there, not in the till. */
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

    /** Everything the world paid the city's businesses this month, local money, reinvested where it was paid. For MoneyAudit, twice. */
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

    /* ------------------------------ save and restore ------------------------------ */

    /**
     * Seven headline figures, then three per sector in SECTORS order.
     * Absent-safe: a save from before this existed restores nothing, which is
     * a city that has never invested abroad, which is what every such city was.
     */
    public double[] toSaveArray() {
        String[] sectors = BusinessDebtManager.SECTORS;
        double[] out = new double[7 + 3 * sectors.length];
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
        return out;
    }

    public void restore(double[] saved) {
        String[] sectors = BusinessDebtManager.SECTORS;
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
    }

    public void reset() {
        for (String s : BusinessDebtManager.SECTORS) {
            usd.put(s, 0.0);
            moved.put(s, 0.0);
            interest.put(s, 0.0);
        }
        lastRate = ForeignAccounts.OPENING_RATE;
        spread = targetShare = 0;
        lifetimeOut = lifetimeHome = lifetimeInterest = peakUsd = 0;
    }

    /**
     * A reform divides the local money and leaves the dollars alone. The rate
     * the dollars were last valued at is local money per dollar, so it moves;
     * the dollars do not.
     */
    public void redenominate(double scale) {
        lastRate *= scale;
        lifetimeOut *= scale;
        lifetimeHome *= scale;
        lifetimeInterest *= scale;
        for (String s : BusinessDebtManager.SECTORS) {
            moved.put(s, moved.getOrDefault(s, 0.0) * scale);
            interest.put(s, interest.getOrDefault(s, 0.0) * scale);
        }
    }
}
