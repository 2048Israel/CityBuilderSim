package ham.citybuildersim;

/**
 * The city's central bank: the balance sheet its money is made on, and the one
 * place money is made or destroyed.
 *
 * ==================== WHY THIS EXISTS (0.7.0) ====================
 *
 * The game had a central bank before this, and it was the rest of the world.
 * The commercial bank placed its spare cash ABROAD at the world's 2%
 * (Bank.PLACEMENT_RATE) and funded its shortfalls ABROAD at the city's own
 * paper rate plus two points plus a stretch (FUNDING_SPREAD,
 * FUNDING_STRETCH), so the marginal price of money in the city was not the
 * player's: the policy dial
 * only floored the lending rate on top, which is why it stopped mattering past
 * 17%. And the treasury's overdraft was an emergency note the commercial bank
 * bought - free until 0.6.11, and at 27-36% once the bank really paid for it,
 * which compounded a broke seed to $193 quadrillion (the-bank-that-never-paid.md
 * section 3). A free number had been holding a floor.
 *
 * Jerus: "the feds sheet would show how much debt it holds, like debt to
 * itself aka money printing."
 *
 * So the marginal role comes home. The commercial bank's spare cash sits here
 * earning the policy rate; its shortfalls are borrowed from the WINDOW at the
 * policy rate plus WINDOW_PENALTY; the treasury's overdraft is ADVANCED from
 * here at the policy rate, up to a ceiling in months of its revenue (the
 * player's dial since 0.7.2, DEFAULT_ADVANCES_MONTHS until it is moved); and the
 * profit on all of it goes back to the treasury as the remittance. Nothing
 * lends below what reserves earn, and nothing borrows above what the window
 * charges, so the dial is the price of money.
 *
 * ==================== WHAT IT IS IN THE AUDIT ====================
 *
 * A BOUNDARY PARTICIPANT, like the world and the households - not a pool.
 * Every operation below moves money across the edge of MoneyAudit's pools and
 * counts itself as ISSUED (central bank to a pool: an advance, interest on
 * reserves, the remittance) or RETIRED (a pool to the central bank: a
 * repayment, the window's interest, the advances' interest). The audit
 * declares both under Scope.MONEY, and the month's issued less retired is the
 * change in M0 to the cent (CentralBankCheck). The commercial bank's end of
 * each flow moves in Bank.fundToCover() and its pool; the treasury's in
 * Game.settleTreasury(). Each method here books both sides of THIS balance
 * sheet and returns what the caller moves, so the two ends are one figure.
 *
 * ==================== THE TWO SIDES ====================
 *
 * ASSETS: advances to the commercial bank (the window), advances to the
 * treasury (the overdraft - what "printed" means on the screens), the city's
 * paper it holds (its open-market book since 0.7.1, at face - see THE
 * HOLDINGS DIAL), and the vault. The vault is READ from
 * ForeignAccounts, not moved: foreign reserves are a central bank's asset and
 * the page lists them as this one's, but the treasury still buys and sells
 * them and the field stays where the currency's code can see it.
 *
 * LIABILITIES: reserves and currency, and together they are M0. Currency is
 * zero - nobody in this city holds cash outside the bank - and kept so that
 * the day somebody does, it has a line.
 *
 * RESERVES ARE A LEDGER OF WHAT THIS BANK HAS MADE, not a reading of the
 * commercial bank's till, and that is the one place this balance sheet reads
 * differently from a textbook's. In a city with one bank and no cash in hand
 * every dollar a central bank creates ends up in that bank's settlement
 * account, so the textbook's "reserves" and "what the central bank has made"
 * are the same number. They are not here, because money also reaches this
 * city's pools without passing through a central bank at all - exports,
 * capital from abroad, the founding endowment - and the commercial bank's
 * cash is a NET position that moves with every loan it writes. So the policy
 * rate is paid on the commercial bank's spare cash (Bank.cashReserves(), what
 * it has not lent), and this ledger moves only when this bank acts, which is
 * what "money is issued and retired only here" requires.
 *
 * EQUITY is assets less liabilities. Without the vault it is always the
 * remittance not yet paid less the loss carried (see lossCarried), which the
 * harness asserts: a central bank's profit is remitted, so it keeps none -
 * between two presses, plus the gain on a buyback that the top of the next
 * month will take into profit (see paperBoughtBack()). The vault is in it
 * whole, because the treasury bought the vault with its own cash, not with
 * money made here - so since 0.7.2 a dollar the defence sells takes its local
 * price off equity with it, the vault smaller and M0 where it was, and
 * vaultSpent() is that line: spent defending the currency since founding.
 */
public final class CentralBank {

    /** Where the player's change to its holdings dial or its advances ceiling is written (DecisionLog, 0.7.23); null for one no city holds. Never saved: the city wires it. */
    private transient DecisionLog decisions;

    /** Wires this to its city's decision log (Game.buildWorld()). */
    public void recordTo(DecisionLog log) { decisions = log; }

    private void decided(String kind, String label) {
        if (decisions != null) decisions.record(kind, label);
    }

    /* ------------------------------------------------------------------ the dials */

    /** What the window charges over the policy rate: a quarter of a point since 0.7.7 (a point before), the Bank of Canada's own spread - its Bank Rate is the overnight target plus 25 basis points - so borrowing reserves always costs more than holding them earns, and a bank short of money is charged for it without being punished. */
    public static final double WINDOW_PENALTY = .0025;

    /** The most the treasury may owe this bank, in months of its trailing revenue, until the player moves the dial (advancesCeilingMonths): the ceiling a new city opens with and an older save reads; past it the arrears rule decides who is paid. */
    public static final double DEFAULT_ADVANCES_MONTHS = 6;

    /** The highest the player may set that ceiling, in months of revenue - three years: a bound on the dial, not a policy. */
    public static final double MAX_ADVANCES_CEILING = 36;

    /** How many months of the treasury's revenue the ceiling is averaged over. */
    public static final int REVENUE_MONTHS = 12;

    /** The most of the city's term paper the holdings dial may aim at: all of it since 0.7.15, half before - Jerus: "central bank bond holding can ve 100% if one wants", a backstop for when the bank and investors will not hold the city's paper, or the bank is in trouble and should not be made to. */
    public static final double MAX_QE_SHARE = 1.0;

    /** The holding at which the term premium is wholly compressed: half the term paper, where it has been since 0.7.1 - MAX_QE_SHARE's value until the dial went past it (DebtManager.compression()). */
    public static final double FULL_COMPRESSION_SHARE = .5;

    /** The most it moves its book in a month: this share of the larger of the dial and the setting before it, of the term paper outstanding - a quarter, so a move from one setting to another, buying toward a new target or selling an old book, takes four months at most, and exactly four from nothing or to nothing; the Fed does not buy its whole target in a month. */
    public static final double QE_SPEED = .25;

    /** How much of the term premium a holding of FULL_COMPRESSION_SHARE takes away: all of it, at 1. */
    public static final double QE_COMPRESSION = 1.0;

    /* ------------------------------------------------------------------ the balance sheet */

    /** Advances to the commercial bank at the window: what its funding past its deposits owes here. */
    private double advancesToBank;

    /** Advances to the treasury: its overdraft, funded by money this bank made. */
    private double advancesToTreasury;

    /**
     * The city's paper this bank holds, AT FACE - principal, the one
     * convention for every holder; the premium or discount it paid to face is
     * its gain or loss the month it pays it (see THE HOLDINGS DIAL). Zero
     * until 0.7.1 filled it; an old save reads zero.
     */
    private double paperHeld;

    /**
     * The holdings dial (0.7.1): the share of the city's outstanding domestic
     * TERM paper - serials and term loans, not notes - this bank aims to hold,
     * 0 to MAX_QE_SHARE (a half until 0.7.15, all of it since). The player's,
     * on the monetary page; saved under its own key (DataSave.qeTargetShare),
     * and an old save reads 0.
     */
    private double targetShare;

    /**
     * ...and the setting before it: the pace is struck from the larger of
     * the two, so selling a book down to a dial of nothing goes at the pace
     * it was bought at rather than slowing for ever as it shrinks. Appended
     * to the save; an old save reads 0.
     */
    private double previousTarget;

    /**
     * What the treasury paid for this bank's share of a bond it bought back
     * between two presses, and the gain or loss on it against face, both
     * settled at the top of the next month - the money retired there, inside
     * the audit's window, and the gain into that month's profit. Appended to
     * the save; an old save owes nothing.
     */
    private double redemptionDue, redemptionGainDue;

    /** What this bank owes the banking system: every dollar it has made and not taken back. See the header. */
    private double reserves;

    /** Cash in circulation outside the bank. Zero: nobody here holds any. */
    private double currency;

    /**
     * A LOSS IS NOT REMITTED. It reduces this bank's equity, and it is
     * recovered from future profits before anything is remitted again - the
     * rule real central banks run when what they pay on reserves exceeds what
     * they earn (the Federal Reserve books it as a deferred asset). What it
     * pays on reserves with nothing lent out is exactly that loss, month after
     * month, and it is money made to pay interest: it is in M0 for that reason.
     * Zero on an old save, which is a bank that has lost nothing.
     */
    private double lossCarried;

    /** Last month's profit, owed to the treasury and paid at the top of this one. Zero on an old save. */
    private double remittanceDue;

    /**
     * THE CEILING AS A DIAL (0.7.2): the most the treasury may owe here, in
     * months of its trailing revenue, 0 to MAX_ADVANCES_CEILING. Batch B
     * found six months "binds within months of first drawing in a small
     * city", so it is the player's now, on the monetary page beside the rate,
     * the autopilot and the holdings dial. Saved under its own key
     * (DataSave.advancesCeilingMonths); an older save reads
     * DEFAULT_ADVANCES_MONTHS, the constant it was. Months, not money, so a
     * reform does not move it.
     */
    private double advancesCeilingMonths = DEFAULT_ADVANCES_MONTHS;

    /* ------------------------------------------------------------------ the month */

    private double issued, retired;
    private double advancedToBank, repaidByBank;
    private double advancedToTreasury, repaidByTreasury;
    private double interestOnReserves, windowInterest, advancesInterest;
    private double remitted;
    /** The holdings' month (0.7.1): paid for paper and received for it, the coupons and principal on it, what a buyback redeemed, and the gains and losses against face. */
    private double boughtPaper, soldPaper, paperCoupons, paperRedeemed, boughtBack, paperGains;
    /** ...and of what it paid for paper, what it paid the households for theirs (0.7.15): money made and paid straight into their savings. */
    private double boughtFromHouseholds;
    /** ...and what it paid the treasury at issue, and the par it took, rolling its own maturing paper (0.7.15, round 2): money made into the treasury's cash. */
    private double boughtAtIssue, parAtIssue;
    /** The defence's month (0.7.2): the local price of the vault's dollars sold at this month's reprice - equity spent, not money destroyed. */
    private double defendedThisMonth;

    /* ------------------------------------------------------------------ the city's life */

    /** Advanced to the treasury since founding - "printed since founding". Zero on an old save. */
    private double printedLifetime;
    private double issuedLifetime, retiredLifetime, remittedLifetime;
    /** Paid for the city's paper and received for it since founding (0.7.1). Appended to the save; zero on an old one. */
    private double boughtPaperLifetime, soldPaperLifetime;
    /** ...and of what it paid, what it paid the households since founding (0.7.15). Appended to the save; zero on an old one. */
    private double boughtFromHouseholdsLifetime;
    /** ...and what it paid at issue for its own maturing paper rolled, and the par, since founding (0.7.15, round 2). Appended to the save; zero on an old one. */
    private double boughtAtIssueLifetime, parAtIssueLifetime;
    /** Spent defending the currency since founding (0.7.2): the vault's dollars at the rates they were sold at. Appended to the save; zero on an old one. */
    private double defendedLifetime;

    /** The treasury's revenue, the last REVENUE_MONTHS of it, for the ceiling. Empty on an old save, which has no ceiling until a month is struck. */
    private final double[] revenue = new double[REVENUE_MONTHS];
    private int revenueFilled, revenueNext;

    /** Where the vault is read from. Game hands it ForeignAccounts; a harness's bare bank reads none. */
    private final java.util.function.DoubleSupplier vault;

    public CentralBank() { this(() -> 0); }

    public CentralBank(java.util.function.DoubleSupplier vault) {
        this.vault = vault == null ? () -> 0 : vault;
    }

    /** Clears the month's flows. Called at the top of a month, before anything moves. */
    public void startMonth() {
        issued = retired = 0;
        advancedToBank = repaidByBank = 0;
        advancedToTreasury = repaidByTreasury = 0;
        interestOnReserves = windowInterest = advancesInterest = 0;
        remitted = 0;
        boughtPaper = soldPaper = paperCoupons = paperRedeemed = boughtBack = paperGains = 0;
        boughtFromHouseholds = 0;
        boughtAtIssue = parAtIssue = 0;
        defendedThisMonth = 0;
    }

    private void issue(double x)  { reserves += x; issued += x;  issuedLifetime += x; }
    private void retire(double x) { reserves -= x; retired += x; retiredLifetime += x; }

    /* ============================ THE WINDOW ============================
     *
     * The commercial bank's funding past its deposits, borrowed here instead
     * of abroad. Its cash position already carries the loan - a negative cash
     * balance IS borrowing, see Bank's FUNDING SIDE - so what moves at the
     * settle is this ledger and the bank's POOL in the audit, which is its
     * cash plus what it owes the window (MoneyAudit.pools()). An advance is
     * money made; a repayment is money destroyed.
     * ==================================================================== */

    /** Money made and lent to the commercial bank at the window. */
    public double advanceToBank(double x) {
        if (!(x > 0)) return 0;
        advancesToBank += x;
        advancedToBank += x;
        issue(x);
        return x;
    }

    /** ...and repaid, which destroys it. */
    public double repayFromBank(double x) {
        double paid = Math.min(Math.max(0, x), advancesToBank);
        if (paid <= 0) return 0;
        advancesToBank -= paid;
        repaidByBank += paid;
        retire(paid);
        return paid;
    }

    /**
     * Brings the window to what the bank owes past its deposits, once a month
     * at the settle: advances the difference or takes the repayment. The
     * repayment comes first out of anything spare, in the order the funding
     * side has always shed its wholesale tranche before its deposit one.
     */
    public void settleWindow(double owed) {
        double target = Math.max(0, owed);
        if (target > advancesToBank) advanceToBank(target - advancesToBank);
        else if (target < advancesToBank) repayFromBank(advancesToBank - target);
    }

    /** Interest on reserves: the policy rate on the bank's spare cash, paid in money made for it. */
    public double payInterestOnReserves(double x) {
        if (!(x > 0)) return 0;
        interestOnReserves += x;
        issue(x);
        return x;
    }

    /** The window's interest, from the commercial bank: income here, and money destroyed. */
    public double chargeWindow(double x) {
        if (!(x > 0)) return 0;
        windowInterest += x;
        retire(x);
        return x;
    }

    /* ============================ THE TREASURY ============================ */

    /** The treasury's shortfall, advanced in money made for it. What the screens call printing. */
    public double advanceToTreasury(double x) {
        if (!(x > 0)) return 0;
        advancesToTreasury += x;
        advancedToTreasury += x;
        printedLifetime += x;
        issue(x);
        return x;
    }

    /** ...and repaid out of cash above zero, which destroys it. */
    public double repayFromTreasury(double x) {
        double paid = Math.min(Math.max(0, x), advancesToTreasury);
        if (paid <= 0) return 0;
        advancesToTreasury -= paid;
        repaidByTreasury += paid;
        retire(paid);
        return paid;
    }

    /** What a month on the advances outstanding costs at this policy rate. */
    public double advancesInterestDue(double policyAnnual) {
        return advancesToTreasury * Math.max(0, policyAnnual) / 12;
    }

    /** The treasury's interest on its advances: income here, and money destroyed. */
    public double chargeAdvances(double x) {
        if (!(x > 0)) return 0;
        advancesInterest += x;
        retire(x);
        return x;
    }

    /**
     * The profit, to the treasury: last month's, struck at closeMonth() and
     * paid at the top of this one - a revenue line, "Central bank remittance".
     * Money made, because a central bank pays out of nothing but its own books.
     *
     * @return what the treasury receives
     */
    public double remit() {
        double x = remittanceDue;
        remittanceDue = 0;
        if (!(x > 0)) return 0;
        remitted += x;
        remittedLifetime += x;
        issue(x);
        return x;
    }

    /**
     * Strikes the month's profit and decides what is owed to the treasury.
     *
     * Interest on the window and on the advances, and since 0.7.1 the coupons
     * on the paper held and the gains and losses against face on what it
     * bought, sold and was bought back from it; less interest paid on
     * reserves. A profit first makes good
     * any loss still carried and the rest is remitted next month; a loss is
     * carried. Called once, at the bottom of the month, after the commercial
     * bank has settled.
     */
    public void closeMonth() {
        double profit = monthProfit();
        if (profit > 0) {
            double recovered = Math.min(lossCarried, profit);
            lossCarried -= recovered;
            remittanceDue += profit - recovered;
        } else if (profit < 0) {
            lossCarried += -profit;
        }
    }

    /** This month's income less what reserves cost, so far. */
    public double monthProfit() {
        return windowInterest + advancesInterest + paperCoupons + paperGains - interestOnReserves;
    }

    /* ======================== THE HOLDINGS DIAL (0.7.1) ========================
     *
     * Jerus: "the central bank would buy gbonds or sell gbonds from thin air
     * basically (the feds sheet would show how much debt it holds, like debt
     * to itself aka money printing, like QE and QT, just like USA does ... to
     * manipulate the rate accordingly?"
     *
     * QUANTITY, beside the price. The dial is a share of the city's term
     * paper to hold (targetShare); once a month, after the treasury has
     * settled with this bank and before the market is priced, Game moves the
     * holding toward it by at most stepFor() (Game's THE HOLDINGS DIAL, AT THE
     * TOP OF THE MONTH). BUYING is from the commercial bank,
     * at the curve's market value, in money made for it - reserves up, M0 up,
     * the commercial bank's book down by the face - and since 0.7.15, once the
     * bank has no more to sell, from the households, at the same value, the
     * money paid into their savings (buyPaperFromHouseholds()), so the dial's
     * whole range, MAX_QE_SHARE, can be reached. SELLING is to the commercial
     * bank only, the reverse, and destroys the money.
     *
     * AT ISSUE ONLY TO ROLL ITS OWN (0.7.15, round 2). Until 0.7.15 this read
     * "Never at issue: a central bank buying from its own treasury is
     * monetisation, which is the advances' line, not this one." It still never
     * lends the treasury new money at issue. But what it holds of a piece
     * falling due is replaced at issue, par for par, as the Federal Reserve
     * rolls its own - the Federal Reserve Bank of New York, "FAQs: Treasury
     * Rollovers": "the Desk rolls over the SOMA's maturing Treasury security
     * holdings by replacing maturing holdings with securities issued at
     * Treasury auctions ... by placing non-competitive bids at Treasury
     * auctions equal in par amount to the value of holdings maturing on the
     * issue date of the securities being auctioned, allocated proportionally
     * across those securities by announced offering amount"; the bids "are
     * treated as add-ons to announced auction sizes", and "Noncompetitive
     * bidders receive the stop-out rate, yield or discount margin determined
     * by the competitive auction process." Until then the treasury repaid its
     * maturing par, the rollover sold the replacement to the commercial bank,
     * and this bank bought it back from the bank over the months after - and
     * a bank whose paper it had bought held nothing else and funded the whole
     * issue at the window, and failed on it (0.7.15's round 1 ensembles). So
     * buyAtIssue(): the par of its maturing holding, at the issue's own price,
     * in money made for it, on top of what the market is sold - while its
     * dial is at or above what it holds; over its dial, as the Fed's QT let
     * it, the part it holds past the dial runs off; and less what the
     * treasury's surplus pays off once the market's part is paid
     * (Jerus: "Surplus pays everyone"; Game, THE CENTRAL BANK ROLLS ITS OWN,
     * AT ISSUE).
     *
     * AT FACE, and the difference is profit. The paper is carried at its
     * principal, like every holder's; what it paid under face is a gain the
     * month it paid it, and over face a loss, taken into the month's profit
     * beside the interest - so the remittance carries it to the treasury, and
     * a central bank that keeps nothing keeps nothing here either. The
     * coupons on its share are paid it by the treasury and destroyed, and
     * come back as the remittance the month after: "debt to itself".
     * ==================================================================== */

    /** Buys this much face for this price, in money made for it. The commercial bank is paid the price. */
    public double buyPaper(double price, double face) {
        if (!(price > 0) || !(face > 0)) return 0;
        issue(price);
        paperHeld += face;
        boughtPaper += price;
        boughtPaperLifetime += price;
        paperGains += face - price;
        return price;
    }

    /**
     * Buys this much face from the households for this price, in money made
     * for them (0.7.15): the paper the bank could not sell it. The same
     * purchase as buyPaper() - the money made, the face on the book at face,
     * the gain against face into the month's profit - and counted apart, so
     * MoneyAudit can declare the money made arriving (MONEY, with the rest of
     * boughtPaper) and leaving the pools for the households' savings
     * (DOMESTIC), as a sale of theirs to the desk leaves them.
     */
    public double buyPaperFromHouseholds(double price, double face) {
        double paid = buyPaper(price, face);
        boughtFromHouseholds += paid;
        boughtFromHouseholdsLifetime += paid;
        return paid;
    }

    /**
     * Its add-on at issue (0.7.15, round 2): this much face of the city's new
     * paper for this price - the issue's own price on each unit of face -
     * paid to the treasury in money made for it. The same purchase as
     * buyPaper(): the money made, the face on the book at face, the gain
     * against face into the month's profit and so back to the treasury as
     * the remittance. Counted apart, for the playtest and the screens.
     * MoneyAudit's "+ centralbank BoughtPaper" carries it with the rest: the
     * money arrives in the treasury's pool, and nothing else is declared.
     */
    public double buyAtIssue(double price, double face) {
        double paid = buyPaper(price, face);
        if (paid > 0) {
            boughtAtIssue += paid;
            parAtIssue += face;
            boughtAtIssueLifetime += paid;
            parAtIssueLifetime += face;
        }
        return paid;
    }

    /** ...and sells it back, which destroys the price. */
    public double sellPaper(double price, double face) {
        if (!(price > 0) || !(face > 0)) return 0;
        retire(price);
        paperHeld = Math.max(0, paperHeld - face);
        soldPaper += price;
        soldPaperLifetime += price;
        paperGains += price - face;
        return price;
    }

    /** The coupon on its share, from the treasury: income here, and money destroyed. */
    public double takeCoupon(double x) {
        if (!(x > 0)) return 0;
        paperCoupons += x;
        retire(x);
        return x;
    }

    /** Principal repaid on its share: off the book at face, and destroyed. */
    public double takePrincipal(double x) {
        if (!(x > 0)) return 0;
        paperHeld = Math.max(0, paperHeld - x);
        paperRedeemed += x;
        retire(x);
        return x;
    }

    /**
     * The treasury buys back a bond this bank holds part of, between two
     * presses: the face comes off the book now, and the price and the gain
     * against face wait for the top of the next month (settleRedemptions()),
     * where the audit can see the money destroyed.
     */
    public void paperBoughtBack(double price, double face) {
        if (face > 0) paperHeld = Math.max(0, paperHeld - face);
        if (price > 0) redemptionDue += price;
        redemptionGainDue += Math.max(0, price) - Math.max(0, face);
    }

    /** ...and settled: the price destroyed, the gain into this month's profit. Called at the top of the month, inside the audit's window. */
    public double settleRedemptions() {
        double x = redemptionDue;
        redemptionDue = 0;
        paperGains += redemptionGainDue;
        redemptionGainDue = 0;
        if (!(x > 0)) return 0;
        boughtBack += x;
        retire(x);
        return x;
    }

    /** What the treasury has paid for this bank's share of a buyback and it has not yet settled: carried in the treasury's pool until it has. */
    public double getRedemptionDue() { return redemptionDue; }

    public double getTargetShare()   { return targetShare; }
    public double getPreviousTarget() { return previousTarget; }

    /** The dial, held to 0..MAX_QE_SHARE. Moving it remembers where it was, for the pace. */
    public void setTargetShare(double share) {
        double to = Double.isFinite(share) ? Math.max(0, Math.min(MAX_QE_SHARE, share)) : 0;
        if (to != targetShare) {
            previousTarget = targetShare;
            decided(DecisionLog.CENTRAL_BANK, "Central bank to hold " + DecisionLog.pct(to) + " of the city's paper");
        }
        targetShare = to;
    }

    /**
     * The dial as a save left it, WITHOUT moving the memory of where it was:
     * the load path's setter. Going through setTargetShare() from the empty
     * bank restore() founds would record "it was 0 before" for every reloaded
     * city, and a move saved half way through would resume at the slower
     * pace of its new setting alone (found by the 0.7.1 docs pass).
     */
    public void restoreTargetShare(double share) {
        targetShare = Double.isFinite(share) ? Math.max(0, Math.min(MAX_QE_SHARE, share)) : 0;
    }

    /**
     * The most the book may move this month, at face, against this much term
     * paper outstanding: QE_SPEED of the larger of the dial, the setting
     * before it and the share it actually holds.
     */
    public double stepFor(double termPrincipal) {
        if (!(termPrincipal > 0)) return 0;
        double held = paperHeld / termPrincipal;
        return QE_SPEED * Math.max(Math.max(targetShare, previousTarget), held) * termPrincipal;
    }

    /* ========================= THE DEFENCE (0.7.2) =========================
     *
     * the-central-bank.md section 8: "a defence that spends: the central bank
     * sells dollars from the vault for local money, which destroys M0 - the
     * classic, and what the founding US$1B is for." Jerus had already decided
     * which way it runs: the vault is spent only when the push would weaken
     * the currency (ForeignAccounts, A RESERVE DEFENDS A CURRENCY).
     *
     * A CAPITAL TRANSACTION AGAINST THE WORLD, NOT A RETIREMENT, and this is
     * where the model reads differently from the textbook it was briefed
     * from, the way RESERVES ARE A LEDGER above does. In a textbook the
     * importer buys the dollars from its bank, which buys them here: the
     * importer's deposit falls, the bank's reserves fall, M2 and M0 shrink
     * together. In this city the deficit's local money has ALREADY left the
     * pools - an import is paid in local money and the audit sees it cross
     * the edge to the world (Scope.TRADE), so the deposit half of the
     * textbook happened the day the goods were bought. What is left is the
     * world holding that money and selling it, which is the push, and the
     * defence is this bank buying the world's claim with the vault's dollars.
     * None of what it buys is money this bank made, and M0 is a ledger of
     * exactly that. The first build of this (2026-09-22) retired the price
     * anyway and took M0 below nothing - seed 0 ended at -$255M, a bank that
     * had destroyed more than it ever made. So: the vault falls by the
     * dollars, this bank's equity falls by their local price, and M0 does not
     * move. Nothing goes through issued or retired, and nothing through the
     * month's profit - it is capital spent, not a loss for the remittance to
     * recover (lossCarried).
     *
     * NO POOL MOVES, so the audit declares nothing, and nothing waits to
     * settle: it is booked where it happens, at the reprice, after the audit
     * (NOTHING AFTER THE AUDIT MAY MOVE A POOL, Game.nextMonth() - the vault
     * is not a pool). Charging the commercial bank's cash instead, as the
     * design first read, would have charged the city a second time for a
     * deficit it had already paid, and since deposits are not the commercial
     * bank's liabilities here (Bank, THE THREE STATEMENTS) it would have come
     * straight out of the bank's equity.
     * ======================================================================== */

    /**
     * The vault's dollars were sold at the reprice for this much local money.
     * The equity line and nothing more: the vault itself is ForeignAccounts',
     * and equity is lower by the sale already because it reads the vault.
     */
    public void dollarsSold(double local) {
        if (!(local > 0) || !Double.isFinite(local)) return;
        defendedThisMonth += local;
        defendedLifetime += local;
    }

    /** Spent defending the currency at this month's reprice, at the rate the dollars were sold at. */
    public double getDefendedThisMonth()  { return defendedThisMonth; }
    /** ...and since founding. */
    public double getDefendedLifetime()   { return defendedLifetime; }

    /**
     * Spent defending the currency since founding: the local price of every
     * dollar the defence has sold. The line under equity that says why equity
     * is lower - it is lower already, through the vault it reads.
     */
    public double vaultSpent() { return defendedLifetime; }

    /* ============================ THE CEILING ============================ */

    /** Files a month of the treasury's revenue, for the ceiling. Called where the government's books are struck. */
    public void noteRevenue(double monthRevenue) {
        if (!Double.isFinite(monthRevenue)) return;
        revenue[revenueNext] = Math.max(0, monthRevenue);
        revenueNext = (revenueNext + 1) % REVENUE_MONTHS;
        if (revenueFilled < REVENUE_MONTHS) revenueFilled++;
    }

    /** The treasury's revenue a month, averaged over what has been filed. */
    public double trailingRevenue() {
        if (revenueFilled == 0) return 0;
        double total = 0;
        for (int i = 0; i < revenueFilled; i++) total += revenue[i];
        return total / revenueFilled;
    }

    /** The most the treasury may owe here before the arrears rule decides who is paid: the dial's months of its trailing revenue. */
    public double ceiling() { return advancesCeilingMonths * trailingRevenue(); }

    /** The ceiling dial, in months of revenue. */
    public double getAdvancesCeilingMonths() { return advancesCeilingMonths; }

    /** Sets the ceiling dial, held to 0..MAX_ADVANCES_CEILING; a number that is not one reads the default. */
    public void setAdvancesCeilingMonths(double months) {
        double was = advancesCeilingMonths;
        advancesCeilingMonths = Double.isFinite(months)
                ? Math.max(0, Math.min(MAX_ADVANCES_CEILING, months)) : DEFAULT_ADVANCES_MONTHS;
        if (DecisionLog.moved(was, advancesCeilingMonths)) {
            decided(DecisionLog.CENTRAL_BANK, String.format(java.util.Locale.ROOT,
                    "Advances to the treasury capped at %s months of revenue",
                    DecisionLog.times(advancesCeilingMonths).substring(1)));
        }
    }

    /** What the treasury may still draw for anything that is not a promise. */
    public double headroom() { return Math.max(0, ceiling() - advancesToTreasury); }

    /** True once the treasury owes the ceiling or more. */
    public boolean ceilingBound() { return advancesToTreasury >= ceiling() - 1e-9 && advancesToTreasury > 0; }

    /* ============================ READING ============================ */

    public double getAdvancesToBank()     { return advancesToBank; }
    public double getAdvancesToTreasury() { return advancesToTreasury; }
    public double getPaperHeld()          { return paperHeld; }
    /** The vault, at today's rate, read from ForeignAccounts. */
    public double getVault()              { return vault.getAsDouble(); }
    public double getReserves()           { return reserves; }
    public double getCurrency()           { return currency; }

    public double totalAssets() {
        return advancesToBank + advancesToTreasury + paperHeld + redemptionDue + getVault();
    }

    /** M0: everything this bank owes, which is every dollar it has made and not taken back. */
    public double m0() { return reserves + currency; }

    public double totalLiabilities() { return m0(); }

    public double equity() { return totalAssets() - totalLiabilities(); }

    public double getLossCarried()     { return lossCarried; }
    public double getRemittanceDue()   { return remittanceDue; }

    public double getIssued()          { return issued; }
    public double getRetired()         { return retired; }
    public double getAdvancedToBank()  { return advancedToBank; }
    public double getRepaidByBank()    { return repaidByBank; }
    /** Printed this month: what was advanced to the treasury. */
    public double getAdvancedToTreasury() { return advancedToTreasury; }
    public double getRepaidByTreasury()   { return repaidByTreasury; }
    public double getInterestOnReserves() { return interestOnReserves; }
    public double getWindowInterest()     { return windowInterest; }
    public double getAdvancesInterest()   { return advancesInterest; }
    /** The remittance paid to the treasury this month. */
    public double getRemitted()           { return remitted; }
    /** The holdings' month (0.7.1). */
    public double getBoughtPaper()        { return boughtPaper; }
    public double getSoldPaper()          { return soldPaper; }
    public double getPaperCoupons()       { return paperCoupons; }
    public double getPaperRedeemed()      { return paperRedeemed; }
    /** What a buyback redeemed of its paper, settled this month. */
    public double getBoughtBack()         { return boughtBack; }
    public double getPaperGains()         { return paperGains; }
    public double getBoughtPaperLifetime() { return boughtPaperLifetime; }
    public double getSoldPaperLifetime()   { return soldPaperLifetime; }
    /** Of what it paid for paper this month, what it paid the households (0.7.15); the treasury at issue, getBoughtAtIssue() (round 2); the rest it paid the bank. */
    public double getBoughtFromHouseholds()         { return boughtFromHouseholds; }
    /** ...and since founding. */
    public double getBoughtFromHouseholdsLifetime() { return boughtFromHouseholdsLifetime; }
    /** What it paid the treasury this month for its add-ons at issue, rolling its own maturing paper (0.7.15, round 2). */
    public double getBoughtAtIssue()                { return boughtAtIssue; }
    /** ...their par: what of its maturing paper it rolled this month. */
    public double getParAtIssue()                   { return parAtIssue; }
    /** ...and since founding, what it paid. */
    public double getBoughtAtIssueLifetime()        { return boughtAtIssueLifetime; }
    /** ...and the par. */
    public double getParAtIssueLifetime()           { return parAtIssueLifetime; }

    public double getPrintedLifetime()    { return printedLifetime; }
    public double getIssuedLifetime()     { return issuedLifetime; }
    public double getRetiredLifetime()    { return retiredLifetime; }
    public double getRemittedLifetime()   { return remittedLifetime; }

    /* ============================ THE SAVE ============================ */

    /**
     * Everything above that is state rather than this month's flow, as one
     * array under its own key (DataSave.centralBank). Slots, in order: the
     * two advances, the paper held, reserves, currency, the loss carried, the
     * remittance due, printed / issued / retired / remitted since founding,
     * the revenue ring's position and fill, then the ring itself; and since
     * 0.7.1, appended after the ring, the redemption and its gain still due,
     * paper bought and sold since founding, and the dial's previous setting;
     * since 0.7.2 what the defence has spent since founding; and since
     * 0.7.15 what it has paid the households for their paper, and (round 2)
     * what it has paid at issue rolling its own paper, and that paper's par. A
     * slot appended later reads zero from an older save, which is a bank that
     * has not done that yet. A save without the key founds an empty bank. The
     * two dials, the holdings and the ceiling, are under keys of their own.
     */
    public double[] toSaveArray() {
        double[] out = new double[13 + REVENUE_MONTHS + 9];
        int i = 0;
        out[i++] = advancesToBank;
        out[i++] = advancesToTreasury;
        out[i++] = paperHeld;
        out[i++] = reserves;
        out[i++] = currency;
        out[i++] = lossCarried;
        out[i++] = remittanceDue;
        out[i++] = printedLifetime;
        out[i++] = issuedLifetime;
        out[i++] = retiredLifetime;
        out[i++] = remittedLifetime;
        out[i++] = revenueNext;
        out[i++] = revenueFilled;
        for (double r : revenue) out[i++] = r;
        out[i++] = redemptionDue;
        out[i++] = redemptionGainDue;
        out[i++] = boughtPaperLifetime;
        out[i++] = soldPaperLifetime;
        out[i++] = previousTarget;
        out[i++] = defendedLifetime;
        out[i++] = boughtFromHouseholdsLifetime;
        out[i++] = boughtAtIssueLifetime;
        out[i++] = parAtIssueLifetime;
        return out;
    }

    public void restore(double[] saved) {
        reset();
        if (saved == null || saved.length < 13) return;
        int i = 0;
        advancesToBank     = saved[i++];
        advancesToTreasury = saved[i++];
        paperHeld          = saved[i++];
        reserves           = saved[i++];
        currency           = saved[i++];
        lossCarried        = saved[i++];
        remittanceDue      = saved[i++];
        printedLifetime    = saved[i++];
        issuedLifetime     = saved[i++];
        retiredLifetime    = saved[i++];
        remittedLifetime   = saved[i++];
        revenueNext   = Math.floorMod((int) saved[i++], REVENUE_MONTHS);
        revenueFilled = Math.max(0, Math.min(REVENUE_MONTHS, (int) saved[i++]));
        for (int r = 0; r < REVENUE_MONTHS && i < saved.length; r++) revenue[r] = saved[i++];
        // 0.7.1's five, after the ring: an older save has none, and owes nothing.
        if (i < saved.length) redemptionDue       = saved[i++];
        if (i < saved.length) redemptionGainDue   = saved[i++];
        if (i < saved.length) boughtPaperLifetime = saved[i++];
        if (i < saved.length) soldPaperLifetime   = saved[i++];
        if (i < saved.length) previousTarget      = saved[i++];
        // 0.7.2's: an older save has spent nothing in a defence.
        if (i < saved.length) defendedLifetime    = Math.max(0, saved[i++]);
        // 0.7.15's: an older save has bought nothing from the households.
        if (i < saved.length) boughtFromHouseholdsLifetime = Math.max(0, saved[i++]);
        // ...and round 2's: an older save has rolled nothing at issue.
        if (i < saved.length) boughtAtIssueLifetime = Math.max(0, saved[i++]);
        if (i < saved.length) parAtIssueLifetime    = Math.max(0, saved[i++]);
    }

    /** An empty bank: nothing lent, nothing made. */
    public void reset() {
        advancesToBank = advancesToTreasury = paperHeld = 0;
        reserves = currency = 0;
        lossCarried = remittanceDue = 0;
        printedLifetime = issuedLifetime = retiredLifetime = remittedLifetime = 0;
        redemptionDue = redemptionGainDue = 0;
        boughtPaperLifetime = soldPaperLifetime = 0;
        boughtFromHouseholdsLifetime = 0;
        boughtAtIssueLifetime = parAtIssueLifetime = 0;
        defendedLifetime = 0;
        targetShare = previousTarget = 0;
        advancesCeilingMonths = DEFAULT_ADVANCES_MONTHS;
        java.util.Arrays.fill(revenue, 0);
        revenueFilled = revenueNext = 0;
        startMonth();
    }

    /**
     * Every figure on this balance sheet, in the new unit. Money is scaled;
     * the two dials are not - a share and a number of months. The vault is
     * ForeignAccounts' and reforms with it.
     */
    public void redenominate(double scale) {
        advancesToBank *= scale;  advancesToTreasury *= scale;  paperHeld *= scale;
        reserves *= scale;  currency *= scale;
        lossCarried *= scale;  remittanceDue *= scale;
        printedLifetime *= scale;  issuedLifetime *= scale;
        retiredLifetime *= scale;  remittedLifetime *= scale;
        issued *= scale;  retired *= scale;
        advancedToBank *= scale;  repaidByBank *= scale;
        advancedToTreasury *= scale;  repaidByTreasury *= scale;
        interestOnReserves *= scale;  windowInterest *= scale;  advancesInterest *= scale;
        remitted *= scale;
        redemptionDue *= scale;  redemptionGainDue *= scale;
        boughtPaperLifetime *= scale;  soldPaperLifetime *= scale;
        boughtPaper *= scale;  soldPaper *= scale;  paperCoupons *= scale;
        boughtFromHouseholds *= scale;  boughtFromHouseholdsLifetime *= scale;
        boughtAtIssue *= scale;  parAtIssue *= scale;
        boughtAtIssueLifetime *= scale;  parAtIssueLifetime *= scale;
        paperRedeemed *= scale;  boughtBack *= scale;  paperGains *= scale;
        defendedThisMonth *= scale;  defendedLifetime *= scale;
        for (int r = 0; r < REVENUE_MONTHS; r++) revenue[r] *= scale;
    }
}
